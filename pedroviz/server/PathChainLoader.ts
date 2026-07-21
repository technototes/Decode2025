import { promises as fsp } from 'node:fs';

import {
  BaseJavaCstVisitorWithDefaults,
  BlockStatementCstNode,
  ClassDeclarationCtx,
  ConstructorDeclarationCtx,
  ExpressionCstNode,
  FieldDeclarationCtx,
  FqnOrRefTypeCstNode,
  IToken,
  parse,
  PrimaryCtx,
  PrimarySuffixCstNode,
  UnannTypeCstNode,
  UnaryExpressionCtx,
  VariableDeclaratorCtx,
} from 'java-parser';
import {
  hasField,
  isArray,
  isDefined,
  isString,
  isUndefined,
} from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousFacing,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  EmptyPathChainClass,
  ErrorOr,
  HeadingRef,
  isAnonymousValue,
  isRadiansRef,
  isRef,
  makeError,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  PathChainClass,
  PathChainHelper,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from './types';

type PCCContext = { pathChainFields: string[] };
type PCCInfo = PCCContext & PathChainClass;
type OptPCCInfo = Partial<PCCContext> & PathChainClass;

class PathChainLoader extends BaseJavaCstVisitorWithDefaults {
  content: string = '';
  parsed: ReturnType<typeof parse> | null = null;

  contextStack: PCCInfo[] = [];

  info: PCCInfo = {
    ...structuredClone(EmptyPathChainClass),
    pathChainFields: [],
  };

  constructor() {
    super();
    this.validateVisitor();
  }

  async loadFile(fileName: string): Promise<string | true> {
    // Read the contents fo the file and parse it:
    this.info.container = { fileName };
    try {
      const content = await fsp.readFile(fileName, 'utf-8');
      return this.parseContent(content);
    } catch (e) {
      return `Could not read file: ${fileName} - ${e}`;
    }
  }

  parseContent(content: string): string | true {
    try {
      this.content = content;
      this.parsed = parse(this.content);
    } catch (e) {
      return `Could not parse content - ${e}`;
    }
    // Now visit the parsed CST, filling in all the data structures:
    try {
      this.visit(this.parsed);
    } catch (e) {
      return `Could not visit parsed CST for file: ${this.info.name} - ${e}`;
    }
    return true;
  }

  // Okay, now we need to implement the visitor methods to extract the data we want.
  // All the static fields we care about:
  // double's, int's, pose's, BezierCurve's, BezierLine's.
  // PathChains shouldn't be static

  override fieldDeclaration(ctx: FieldDeclarationCtx) {
    // We're looking for public static double/int name = value;
    const maybeNamedValue = tryMatchingNamedValues(ctx);
    if (isDefined(maybeNamedValue)) {
      this.info.values.push(maybeNamedValue);
      return super.fieldDeclaration(ctx);
    }
    const maybeNamedPoses = tryMatchingNamedPoses(ctx);
    if (isDefined(maybeNamedPoses)) {
      this.info.poses.push(maybeNamedPoses);
      return super.fieldDeclaration(ctx);
    }
    const maybeNamedBeziers = tryMatchingBeziers(ctx);
    if (isDefined(maybeNamedBeziers)) {
      this.info.beziers.push(maybeNamedBeziers);
      return super.fieldDeclaration(ctx);
    }
    const maybePathChainField = tryMatchingPathChainFields(ctx);
    if (isDefined(maybePathChainField)) {
      this.info.pathChainFields.push(maybePathChainField);
    }
    return super.fieldDeclaration(ctx);
  }

  override constructorDeclaration(ctx: ConstructorDeclarationCtx) {
    this.info.pathChainHelpers.push(...getPathChainHelpers(ctx));
    this.info.pathChains.push(...getPathChainFactories(ctx));
    return super.constructorDeclaration(ctx);
  }

  // I need to handle nested classes.
  // I should probably make sure they're static if they're nested
  override classDeclaration(ctx: ClassDeclarationCtx, param?: any) {
    const theClassName = nameOf(
      child(child(ctx?.normalClassDeclaration)?.typeIdentifier)?.Identifier,
    );
    if (isUndefined(theClassName)) {
      return;
    }
    // If the stack is empty, we just push the current PCC.
    // If the stack *isn't* empty, we need to create a new PathChainClass (and push it).
    if (this.contextStack.length !== 0) {
      const parent = this.info;
      this.info = {
        ...structuredClone(EmptyPathChainClass),
        container: { className: parent.name },
        pathChainFields: [],
      };
      parent.children[theClassName] = this.info;
    }
    this.info.name = theClassName;
    this.contextStack.push(this.info);

    const res = super.classDeclaration(ctx, param);

    // Let's make the name the fully qualified name, now
    this.info.name = this.contextStack.map((pcc) => pcc.name).join('.');
    this.contextStack.pop();
    if (this.contextStack.length > 0) {
      this.info = this.contextStack[this.contextStack.length - 1]!;
    }
    return res;
  }
}

function descend<T>(ctx: T[] | undefined): T | undefined {
  if (!isArray(ctx) || ctx.length !== 1) {
    return;
  }
  return ctx[0];
}

function child<T extends { children: any }>(
  ctx: T[] | undefined,
): T['children'] | undefined {
  return descend(ctx)?.children;
}

function nameOf(ctx: IToken[] | IToken | undefined): string | undefined {
  if (isArray(ctx)) return ctx.map((tok) => tok.image).join('.');
  else return ctx?.image;
}

function getBType(className: string): 'line' | 'curve' | undefined {
  switch (className) {
    case 'BezierLine':
      return 'line';
    case 'BezierCurve':
      return 'curve';
  }
}

function isPublicStaticField(ctx: FieldDeclarationCtx): boolean {
  if (!ctx.fieldModifier || ctx.fieldModifier.length !== 2) {
    return false;
  }
  if (
    !ctx.fieldModifier.every(
      (mod) => mod.children.Public || mod.children.Static,
    )
  ) {
    return false;
  }
  return true;
}

function isPublicField(ctx: FieldDeclarationCtx): boolean {
  if (!ctx.fieldModifier || ctx.fieldModifier.length !== 1) {
    return false;
  }
  return isDefined(ctx.fieldModifier[0]!.children.Public);
}

// This matches the 'public static int/double name = value;' pattern
function tryMatchingNamedValues(
  ctx: FieldDeclarationCtx,
): NamedValue | undefined {
  if (!isPublicStaticField(ctx)) {
    return;
  }
  const numType = child(
    child(
      child(child(ctx.unannType)?.unannPrimitiveTypeWithOptionalDimsSuffix)
        ?.unannPrimitiveType,
    )?.numericType,
  );
  if (!numType) {
    return;
  }
  let type = 'double';
  if (numType.floatingPointType) {
    if (!child(numType.floatingPointType)?.Double) {
      return;
    }
  } else if (numType.integralType) {
    if (!child(numType.integralType)?.Int) {
      return;
    }
    type = 'int';
  }
  // Okay, found the type. Need the name and the initialized value.
  if (ctx.variableDeclaratorList.length !== 1) {
    return;
  }
  const varDecl = child(child(ctx.variableDeclaratorList)?.variableDeclarator);
  if (!varDecl) {
    return;
  }
  const maybeName = nameOf(child(varDecl.variableDeclaratorId)?.Identifier);
  if (!maybeName) {
    return;
  }
  const name: ValueName = maybeName as ValueName;
  // TODO: Support initializers of "Math.toRadians(K)"
  const expr = descend(child(varDecl.variableInitializer)?.expression);
  if (isUndefined(expr)) {
    return;
  }
  const valRef = getHeadingRef(expr);
  if (isString(valRef)) {
    return { name, value: valRef as ValueName };
  }
  if (isAnonymousValue(valRef) || isRadiansRef(valRef)) {
    return { name, value: valRef };
  }
}

function getNumericConstant(
  expr: ExpressionCstNode,
): AnonymousValue | undefined {
  const unary: UnaryExpressionCtx | undefined = child(
    child(child(expr.children.conditionalExpression)?.binaryExpression)
      ?.unaryExpression,
  );
  const negative = '-' === nameOf(unary?.UnaryPrefixOperator) ? -1 : 1;
  const whichLit = child(child(child(unary?.primary)?.primaryPrefix)?.literal);
  if (isDefined(whichLit?.integerLiteral)) {
    const value = nameOf(child(whichLit.integerLiteral)?.DecimalLiteral);
    if (isDefined(value)) {
      return { int: parseInt(value) * negative };
    }
  } else if (isDefined(whichLit?.floatingPointLiteral)) {
    const value = nameOf(child(whichLit.floatingPointLiteral)?.FloatLiteral);
    if (isDefined(value)) {
      return { double: parseFloat(value) * negative };
    }
  }
  return;
}

function getRefTypeName(fqn: FqnOrRefTypeCstNode[]): string | undefined {
  let name = nameOf(
    child(child(child(fqn)?.fqnOrRefTypePartFirst)?.fqnOrRefTypePartCommon)
      ?.Identifier,
  );
  if (isUndefined(name)) {
    return;
  }
  const rest = child(fqn)?.fqnOrRefTypePartRest;
  if (isDefined(rest)) {
    for (const item of rest.map(
      (fqnRest) => fqnRest.children.fqnOrRefTypePartCommon,
    )) {
      const next = nameOf(descend(item)?.children.Identifier);
      if (isDefined(next)) {
        name = name + '.' + next;
      }
    }
  }
  return name;
}

function getRef(expr: ExpressionCstNode): string | undefined {
  const unary: UnaryExpressionCtx | undefined = child(
    child(child(expr.children.conditionalExpression)?.binaryExpression)
      ?.unaryExpression,
  );
  const val = child(child(unary?.primary)?.primaryPrefix);
  if (isDefined(val?.fqnOrRefType)) {
    return getRefTypeName(val.fqnOrRefType);
  }
}

function getRefOr<Str, T>(
  expr: ExpressionCstNode | undefined,
  getOr: (expr: ExpressionCstNode) => T | undefined,
): T | Str | undefined {
  if (isUndefined(expr)) {
    return undefined;
  }
  const ref = getRef(expr);
  return isString(ref) ? (ref as Str) : getOr(expr);
}

function getMethodInvoke(primary: PrimaryCtx): string | undefined {
  const methodInvoke = child(primary.primaryPrefix)?.fqnOrRefType;
  if (isUndefined(methodInvoke)) {
    return;
  }
  return getRefTypeName(methodInvoke);
}

function getToRadians(
  expr: ExpressionCstNode,
): RadiansRef | AnonymousValue | undefined {
  const maybeMethod = child(
    child(
      child(child(expr.children.conditionalExpression)?.binaryExpression)
        ?.unaryExpression,
    )?.primary,
  );
  if (isUndefined(maybeMethod)) {
    return;
  }
  const maybeMathToRad = getMethodInvoke(maybeMethod);
  if (
    isUndefined(maybeMathToRad) ||
    maybeMathToRad !== 'Math.toRadians' ||
    isUndefined(expr.children.conditionalExpression)
  ) {
    return;
  }
  const maybeArgList = child(
    child(
      child(child(expr.children.conditionalExpression)?.binaryExpression)
        ?.unaryExpression,
    )?.primary,
  )?.primarySuffix;
  if (isUndefined(maybeArgList) || maybeArgList.length === 0) {
    return;
  }
  const argList = getArgList(maybeArgList[0]);
  if (isUndefined(argList) || argList.length !== 1) {
    return;
  }
  const numRef = getOnlyValueRef(argList[0]);
  if (isString(numRef)) {
    return { radians: numRef };
  } else if (isDefined(numRef)) {
    return { radians: numRef };
  }
}

function getOnlyValueRef(
  expr: ExpressionCstNode | undefined,
): ValueRef | undefined {
  if (isUndefined(expr)) {
    return;
  }
  return getRefOr(expr, getNumericConstant);
}

function getValueRef(
  expr: ExpressionCstNode | undefined,
): ValueRef | RadiansRef | undefined {
  if (isUndefined(expr)) {
    return;
  }
  // Check for Math.toRadians(...)
  const radian = getToRadians(expr);
  return isDefined(radian) ? radian : getRefOr(expr, getNumericConstant);
}

function getClassTypeName(
  unannType: UnannTypeCstNode[] | undefined,
): string | undefined {
  return nameOf(
    child(
      child(
        child(child(unannType)?.unannReferenceType)?.unannClassOrInterfaceType,
      )?.unannClassType,
    )?.Identifier,
  );
}

function getLValueName(decl: VariableDeclaratorCtx): string | undefined {
  return nameOf(child(decl?.variableDeclaratorId)?.Identifier);
}

function getVariableDeclarator(
  ctx: FieldDeclarationCtx,
): VariableDeclaratorCtx | undefined {
  return child(child(ctx.variableDeclaratorList)?.variableDeclarator);
}

function getCtorArgs(
  decl: VariableDeclaratorCtx | ExpressionCstNode,
  type?: string,
): [string, ExpressionCstNode[] | undefined] {
  let expr: ExpressionCstNode;
  if (!hasField(decl, 'name')) {
    const theExpr = descend(child(decl.variableInitializer)?.expression);
    if (isUndefined(theExpr)) {
      return ['', undefined];
    }
    expr = theExpr;
  } else {
    expr = decl as unknown as ExpressionCstNode;
  }
  const newExpr = child(
    child(
      child(
        child(
          child(
            child(child(expr?.children.conditionalExpression)?.binaryExpression)
              ?.unaryExpression,
          )?.primary,
        )?.primaryPrefix,
      )?.newExpression,
    )?.unqualifiedClassInstanceCreationExpression,
  );
  const dataType = nameOf(
    child(newExpr?.classOrInterfaceTypeToInstantiate)?.Identifier,
  );
  if (isDefined(type) && dataType !== type) {
    return ['', undefined];
  }
  return [type || dataType || '', child(newExpr?.argumentList)?.expression];
}

function tryMatchingNamedPoses(
  ctx: FieldDeclarationCtx,
): NamedPose | undefined {
  if (!isPublicStaticField(ctx) && !isPublicField(ctx)) {
    return;
  }
  const classType = getClassTypeName(ctx.unannType);
  if (classType !== 'Pose') {
    return;
  }
  const decl = getVariableDeclarator(ctx);
  if (isUndefined(decl)) {
    return;
  }
  const name = getLValueName(decl);
  if (isUndefined(name)) {
    return;
  }
  const pose = getAnonymousPose(decl);
  return isDefined(pose) ? { name: name as PoseName, pose } : undefined;
}

function getAnonymousPose(
  expr: ExpressionCstNode | VariableDeclaratorCtx,
): AnonymousPose | undefined {
  const [, ctorArgs] = getCtorArgs(expr, 'Pose');
  if (
    isUndefined(ctorArgs) ||
    (ctorArgs.length !== 3 && ctorArgs.length !== 2)
  ) {
    return;
  }
  const x = getOnlyValueRef(ctorArgs[0]);
  const y = getOnlyValueRef(ctorArgs[1]);
  if (isUndefined(x) || isUndefined(y)) {
    return;
  }
  const heading =
    ctorArgs.length === 3 ? getHeadingRef(ctorArgs[2]!) : undefined;
  return isUndefined(heading) ? { x, y } : { x, y, heading };
}

function getPoseRef(expr: ExpressionCstNode): PoseRef | undefined {
  return getRefOr(expr, getAnonymousPose);
}

function getAnonymousBezier(
  expr: ExpressionCstNode[] | ExpressionCstNode | undefined,
  checkType?: string,
): AnonymousBezier | undefined {
  const firstExpr = isArray(expr) ? expr[0] : expr;
  if (isUndefined(firstExpr)) {
    return;
  }
  const [foundType, ctorArgs] = getCtorArgs(firstExpr, checkType);
  if (isUndefined(ctorArgs)) {
    return;
  }
  const type = getBType(foundType);
  if (isUndefined(type)) {
    return;
  }
  const points: PoseRef[] = ctorArgs.map(getPoseRef) as unknown as PoseRef[];
  if (!points.every(isDefined)) {
    return;
  }
  return { type, points };
}

function tryMatchingBeziers(ctx: FieldDeclarationCtx): NamedBezier | undefined {
  if (!isPublicStaticField(ctx)) {
    return;
  }
  const classType = getClassTypeName(ctx.unannType);
  if (isUndefined(classType)) {
    return;
  }
  const type = getBType(classType);
  const decl = getVariableDeclarator(ctx);
  if (isUndefined(decl) || isUndefined(type)) {
    return;
  }
  const maybeName = getLValueName(decl);
  if (isUndefined(maybeName)) {
    return;
  }
  const name: BezierName = maybeName as BezierName;
  const points = getAnonymousBezier(
    child(decl.variableInitializer)?.expression,
  );
  if (isDefined(points)) {
    return { name, points };
  }
}

function tryMatchingPathChainFields(
  ctx: FieldDeclarationCtx,
): string | undefined {
  if (!isPublicField(ctx)) {
    return;
  }
  if ('PathChain' !== getClassTypeName(ctx.unannType)) {
    return;
  }
  const decl = getVariableDeclarator(ctx);
  if (isUndefined(decl)) {
    return;
  }
  return getLValueName(decl);
}

function getArgList(
  cstNode: PrimarySuffixCstNode | undefined,
): ExpressionCstNode[] | undefined {
  return child(child(cstNode?.children.methodInvocationSuffix)?.argumentList)
    ?.expression;
}

function getHeadingRef(
  arg: ExpressionCstNode | undefined,
  poseAllowed: boolean = false,
): HeadingRef | undefined {
  const valRef = getValueRef(arg);
  if (isDefined(valRef) && poseAllowed) {
    // Check for a <ref>.getHeading() expression, which turns into a simple PoseName
    if (isRef(valRef) && valRef.endsWith('.getHeading')) {
      return valRef.substring(0, valRef.length - 11) as PoseName;
    }
  }
  return valRef;
}

function getBezierRef(
  arg: ExpressionCstNode | undefined,
): BezierRef | undefined {
  return getRefOr(arg, getAnonymousBezier);
}

function getHeadingInterpolation(
  method: PrimarySuffixCstNode,
): AnonymousFacing | undefined {
  // TODO: Fix this, cuz it's *very* wrong
  return { type: 'piecewise', pieces: [] };
}

function getPathChain(node: BlockStatementCstNode): NamedPathChain | undefined {
  const stmt = child(
    child(
      child(
        child(
          child(
            child(
              child(node.children.statement)
                ?.statementWithoutTrailingSubstatement,
            )?.expressionStatement,
          )?.statementExpression,
        )?.expression,
      )?.conditionalExpression,
    )?.binaryExpression,
  );
  if (isUndefined(stmt) || isUndefined(stmt.AssignmentOperator)) {
    return;
  }
  const maybeFqnOrRef = child(
    child(child(stmt.unaryExpression)?.primary)?.primaryPrefix,
  );
  if (isUndefined(maybeFqnOrRef) || isUndefined(maybeFqnOrRef.fqnOrRefType)) {
    return;
  }
  const fieldName = getRefTypeName(maybeFqnOrRef.fqnOrRefType);
  // TODO: make sure the field name is in the list of fields
  const builder = child(
    child(
      child(
        child(child(stmt.expression)?.conditionalExpression)?.binaryExpression,
      )?.unaryExpression,
    )?.primary,
  );
  if (isUndefined(builder)) {
    return;
  }
  const objInvoke = getMethodInvoke(builder);
  if (isUndefined(objInvoke) || objInvoke !== 'follower.pathBuilder') {
    return;
  }
  const methods = builder.primarySuffix;
  if (isUndefined(methods) || methods.length < 5) {
    return;
  }
  // Okay, remove the '.pathBuilder()' prefix, and the
  // '.build();' suffix.
  let chain: BezierRef[] = [];
  let pathHeading: AnonymousFacing | null = null;
  let lastMethodName: string | undefined = 'pathBuilder';
  for (let index = 0; index < methods.length; index++) {
    const method = methods[index]!;
    if (index % 2 === 1) {
      // This should be a dot
      if (isUndefined(method?.children?.Dot)) {
        return;
      }
      lastMethodName = nameOf(method.children.Identifier);
      switch (lastMethodName) {
        case 'pathBuilder':
        case 'addPath':
        case 'setTangentHeadingInterpolation':
        case 'setLinearHeadingInterpolation':
        case 'setConstantHeadingInterpolation':
        case 'setHeadingInterpolation':
        case 'setReversed':
        case 'build':
          continue;
        default:
          return;
      }
    } else {
      switch (lastMethodName) {
        case 'pathBuilder':
        case 'build':
          if (isDefined(getArgList(method))) {
            return;
          }
          continue;
        case 'setTangentHeadingInterpolation':
          if (isDefined(getArgList(method))) {
            return;
          }
          pathHeading = { type: 'tangent' };
          continue;
        case 'setLinearHeadingInterpolation':
          const linearArgs = getArgList(method);
          if (linearArgs?.length !== 2) {
            return;
          }
          const startHeading = getHeadingRef(linearArgs[0], true);
          const endHeading = getHeadingRef(linearArgs[1], true);
          if (isUndefined(startHeading) || isUndefined(endHeading)) {
            return;
          }
          pathHeading = {
            type: 'linear',
            start: startHeading,
            end: endHeading,
          };
          continue;
        case 'setConstantHeadingInterpolation':
          const constantArgs = getArgList(method);
          if (constantArgs?.length !== 1) {
            return;
          }
          const headingRef = getHeadingRef(constantArgs[0], true);
          if (isUndefined(headingRef)) {
            return;
          }
          pathHeading = { type: 'constant', heading: headingRef };
          continue;
        case 'setReversed':
          if (pathHeading === null) {
            return;
          }
          pathHeading = { type: 'reversed', facing: pathHeading };
          continue;

        case 'setHeadingInterpolation':
          // TODO: This is *very* not working
          const interp = getHeadingInterpolation(method);
          if (isUndefined(interp)) {
            return;
          }

          continue;

        case 'addPath':
          const pathArgs = getArgList(method);
          if (pathArgs?.length !== 1) {
            return;
          }
          const bezierRef = getBezierRef(pathArgs[0]);
          if (isUndefined(bezierRef)) {
            return;
          }
          chain.push(bezierRef);
          continue;
        default:
          return;
      }
    }
  }
  if (pathHeading === null) {
    return;
  }
  return { name: fieldName as PathChainName, paths: chain, pathHeading };
}

function getPathChainHelper(
  node: BlockStatementCstNode,
): PathChainHelper | undefined {
  const varDecl = child(
    child(node.children.localVariableDeclarationStatement)
      ?.localVariableDeclaration,
  );
  if (isUndefined(varDecl)) {
    return;
  }
  const lclType = child(
    child(
      child(
        child(child(varDecl.localVariableType)?.unannType)?.unannReferenceType,
      )?.unannClassOrInterfaceType,
    )?.unannClassType,
  )?.Identifier;
  if (isUndefined(lclType) || lclType.length === 0) {
    return;
  }
  const lclVal = child(
    child(varDecl.variableDeclaratorList)?.variableDeclarator,
  );
  if (isUndefined(lclVal)) {
    return;
  }
  const typeName = nameOf(lclType);
  const varName = getLValueName(lclVal);
  if (isUndefined(varName) || isUndefined(typeName)) {
    return;
  }
  const newExpr = child(
    child(
      child(
        child(
          child(
            child(
              child(
                child(child(lclVal.variableInitializer)?.expression)
                  ?.conditionalExpression,
              )?.binaryExpression,
            )?.unaryExpression,
          )?.primary,
        )?.primaryPrefix,
      )?.newExpression,
    )?.unqualifiedClassInstanceCreationExpression,
  );
  if (isUndefined(newExpr)) {
    return;
  }
  const ctorClass = child(
    newExpr.classOrInterfaceTypeToInstantiate,
  )?.Identifier;
  if (isUndefined(ctorClass)) {
    return;
  }
  const ctorName = nameOf(ctorClass);
  if (ctorName !== typeName) {
    // This is being picky, but tough luck: I'm picky..
    return;
  }
  if (
    isDefined(newExpr.argumentList) ||
    isDefined(newExpr.classBody) ||
    isDefined(newExpr.typeArguments)
  ) {
    return;
  }
  // TODO: Read the file's package and make the names fully qualified
  return { name: varName, staticType: typeName };
}

function getPathChainHelpers(
  ctx: ConstructorDeclarationCtx,
): PathChainHelper[] {
  const statements = child(
    child(ctx.constructorBody)?.blockStatements,
  )?.blockStatement;
  if (isUndefined(statements)) {
    return [];
  }
  return statements
    .map(getPathChainHelper)
    .filter(isDefined) as PathChainHelper[];
}

function getPathChainFactories(
  ctx: ConstructorDeclarationCtx,
): NamedPathChain[] {
  const statements = child(
    child(ctx.constructorBody)?.blockStatements,
  )?.blockStatement;
  if (isUndefined(statements)) {
    return [];
  }
  return statements.map(getPathChain).filter(isDefined) as NamedPathChain[];
}

export async function MakePathChainFile(
  filename: string,
): Promise<ErrorOr<PathChainClass>> {
  const loader = new PathChainLoader();
  const res = await loader.loadFile(filename);
  if (isString(res)) {
    return makeError(res);
  }
  let pcc: OptPCCInfo = { ...loader.info };
  delete pcc.pathChainFields;
  return pcc;
}

/*
if (import.meta.main) {
  MakePathChainFile(
    ['..', 'Sixteen750', 'src', 'main', 'java', 'org', 'firstinspires', 'ftc', 'sixteen750', 'commands', 'auto', 'Poses.java',].join('/'))
    .then((strOrPcf) => console.log(strOrPcf))
    .catch((err) => console.error(err));
}
*/
