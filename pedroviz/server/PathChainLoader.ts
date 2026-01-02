import {
  hasField,
  isArray,
  isDefined,
  isString,
  isUndefined,
} from '@freik/typechk';
import {
  BaseJavaCstVisitorWithDefaults,
  BlockStatementCstNode,
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
import { promises as fsp } from 'node:fs';
import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  HeadingRef,
  HeadingType,
  isAnonymousValue,
  isRadiansRef,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  PathChainFile,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from './types';

class PathChainLoader extends BaseJavaCstVisitorWithDefaults {
  content: string = '';
  parsed: ReturnType<typeof parse> | null = null;
  pathChainFields: string[] = [];
  info: PathChainFile = {
    name: '',
    values: [], // NamedValue[];
    poses: [], // NamedPose[];
    beziers: [], // Bezier[];
    pathChains: [], // PathChain[];
  };

  constructor() {
    super();
    this.validateVisitor();
  }

  async loadFile(filename: string): Promise<string | true> {
    // Read the contents fo the file and parse it:
    this.info.name = filename;
    try {
      const content = await fsp.readFile(filename, 'utf-8');
      return this.parseContent(content);
    } catch (e) {
      return `Could not read file: ${filename} - ${e}`;
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

  fieldDeclaration(ctx: FieldDeclarationCtx) {
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
      this.pathChainFields.push(maybePathChainField);
    }
    return super.fieldDeclaration(ctx);
  }

  constructorDeclaration(ctx: ConstructorDeclarationCtx) {
    this.info.pathChains.push(...getPathChainFactories(ctx));
    return super.constructorDeclaration(ctx);
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

function nameOf(ctx: IToken[] | undefined): string | undefined {
  return descend(ctx)?.image;
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
  return (
    ctx.fieldModifier &&
    ctx.fieldModifier.length === 1 &&
    isDefined(ctx.fieldModifier[0].children.Public)
  );
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
  return nameOf(
    child(child(child(fqn)?.fqnOrRefTypePartFirst)?.fqnOrRefTypePartCommon)
      ?.Identifier,
  );
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
  expr: ExpressionCstNode,
  getOr: (expr: ExpressionCstNode) => T | undefined,
): T | Str | undefined {
  const ref = getRef(expr);
  return isString(ref) ? (ref as Str) : getOr(expr);
}

function getMethodInvoke(primary: PrimaryCtx): [string, string] | undefined {
  const methodInvoke = child(primary.primaryPrefix)?.fqnOrRefType;
  const objName = getRefTypeName(methodInvoke);
  if (isUndefined(objName)) {
    return;
  }
  let methodName = nameOf(
    child(
      child(child(methodInvoke)?.fqnOrRefTypePartRest)?.fqnOrRefTypePartCommon,
    )?.Identifier,
  );
  return isDefined(methodName) ? [objName, methodName] : undefined;
}

function getToRadians(
  expr: ExpressionCstNode,
): RadiansRef | AnonymousValue | undefined {
  const maybeMethod = child(
    child(
      child(child(expr.children.conditionalExpression)?.binaryExpression)
        ?.unaryExpression,
    ).primary,
  );
  const maybeMathToRad = getMethodInvoke(maybeMethod);
  if (
    isUndefined(maybeMathToRad) ||
    maybeMathToRad[0] !== 'Math' ||
    maybeMathToRad[1] !== 'toRadians'
  ) {
    return;
  }
  const argList = getArgList(
    child(
      child(
        child(child(expr.children.conditionalExpression)?.binaryExpression)
          ?.unaryExpression,
      )?.primary,
    )?.primarySuffix[0],
  );
  if (argList.length !== 1) {
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
    expr = decl;
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
  return [type || dataType, child(newExpr?.argumentList)?.expression];
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
  const heading = getHeadingRef(ctorArgs[2]);
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
  const [foundType, ctorArgs] = getCtorArgs(firstExpr, checkType);
  if (isUndefined(ctorArgs)) {
    return;
  }
  const points = ctorArgs.map(getPoseRef);
  if (!points.every(isDefined)) {
    return;
  }
  return { type: getBType(foundType), points };
}

function tryMatchingBeziers(ctx: FieldDeclarationCtx): NamedBezier | undefined {
  if (!isPublicStaticField(ctx)) {
    return;
  }
  const classType = getClassTypeName(ctx.unannType);
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
): ExpressionCstNode[] {
  return child(child(cstNode.children.methodInvocationSuffix)?.argumentList)
    ?.expression;
}

function getHeadingRef(
  arg: ExpressionCstNode,
  poseAllowed: boolean = false,
): HeadingRef | undefined {
  if (poseAllowed) {
    // TODO:
    // Check for a <ref>.getHeading() expression
    // As it currently stands, this just gets the name of the ref, which is the end result
    // we're looking for, but it doesn't do any validation that it's also a "foo.getHeading()"
    // for names refer to poses.
  }
  return getValueRef(arg);
}

function getBezierRef(arg: ExpressionCstNode): BezierRef | undefined {
  return getRefOr(arg, getAnonymousBezier);
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
  if (isUndefined(stmt.AssignmentOperator)) {
    return;
  }
  const fieldName = getRefTypeName(
    child(child(child(stmt.unaryExpression)?.primary)?.primaryPrefix)
      .fqnOrRefType,
  );
  // TODO: make sure the field name is in the list of fields
  const builder = child(
    child(
      child(
        child(child(stmt.expression)?.conditionalExpression)?.binaryExpression,
      )?.unaryExpression,
    )?.primary,
  );
  const objInvoke = getMethodInvoke(builder);
  if (
    isUndefined(objInvoke) ||
    objInvoke[0] !== 'follower' ||
    objInvoke[1] !== 'pathBuilder'
  ) {
    return;
  }
  const methods = builder.primarySuffix;
  if (methods.length < 5) {
    return;
  }
  // Okay, remove the '.pathBuilder()' prefix, and the
  // '.build();' suffix.
  let chain: BezierRef[] = [];
  let heading: HeadingType | null = null;
  let lastMethodName = 'pathBuilder';
  for (let index = 0; index < methods.length; index++) {
    const method = methods[index];
    if (index % 2 === 1) {
      // This should be a dot
      if (isUndefined(method.children.Dot)) {
        return;
      }
      lastMethodName = nameOf(method.children.Identifier);
      switch (lastMethodName) {
        case 'pathBuilder':
        case 'addPath':
        case 'setTangentHeadingInterpolation':
        case 'setLinearHeadingInterpolation':
        case 'setConstantHeadingInterpolation':
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
          heading = { type: 'tangent' };
          continue;
        case 'setLinearHeadingInterpolation':
          const linearArgs = getArgList(method);
          if (linearArgs.length !== 2) {
            return;
          }
          const startHeading = getHeadingRef(linearArgs[0], true);
          const endHeading = getHeadingRef(linearArgs[1], true);
          if (isUndefined(startHeading) || isUndefined(endHeading)) {
            return;
          }
          heading = {
            type: 'interpolated',
            headings: [startHeading, endHeading],
          };
          continue;
        case 'setConstantHeadingInterpolation':
          const constantArgs = getArgList(method);
          if (constantArgs.length !== 1) {
            return;
          }
          const headingRef = getHeadingRef(constantArgs[0], true);
          if (isUndefined(headingRef)) {
            return;
          }
          heading = { type: 'constant', heading: headingRef };
          continue;
        case 'addPath':
          const pathArgs = getArgList(method);
          if (pathArgs.length !== 1) {
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
  return { name: fieldName as PathChainName, paths: chain, heading };
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
  const pathChains = statements.map(getPathChain);
  return pathChains.filter(isDefined);
}

export async function MakePathChainFile(
  filename: string,
): Promise<PathChainFile | string> {
  const loader = new PathChainLoader();
  const res = await loader.loadFile(filename);
  return isString(res) ? res : loader.info;
}
