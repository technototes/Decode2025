import { MakeStack } from '@freik/containers';
import {
  chkAllOf,
  chkFieldType,
  hasFieldType,
  isArray,
  isNonNullable,
  isNumber,
} from '@freik/typechk';
import {
  ArgumentListCtx,
  BaseJavaCstVisitorWithDefaults,
  BinaryExpressionCtx,
  ClassBodyDeclarationCtx,
  ClassDeclarationCtx,
  ClassMemberDeclarationCtx,
  ConditionalExpressionCtx,
  CstNode,
  ExpressionCtx,
  FieldDeclarationCtx,
  FqnOrRefTypeCtx,
  FqnOrRefTypePartCommonCtx,
  FqnOrRefTypePartFirstCtx,
  ImportDeclarationCtx,
  LambdaBodyCtx,
  LambdaExpressionCtx,
  NewExpressionCtx,
  NormalClassDeclarationCtx,
  PackageDeclarationCtx,
  parse,
  PrimaryCtx,
  PrimaryPrefixCtx,
  PrimarySuffixCtx,
  TypeIdentifierCtx,
  UnannClassOrInterfaceTypeCtx,
  UnannReferenceTypeCtx,
  UnannTypeCtx,
  UnaryExpressionCtx,
  UnqualifiedClassInstanceCreationExpressionCtx,
  VariableDeclaratorCtx,
  VariableDeclaratorIdCtx,
  VariableDeclaratorListCtx,
  VariableInitializerCtx,
} from 'java-parser';

/*** BEGIN CONFIGURATION STUFF ***/
// These are types the parser is transforming:
const typeMap = new Map([
  ['ConfigurablePose', 'Pose2d'],
  ['ConfigurablePoseD', 'Pose2d'],
  [
    'Function<Function<Pose2d,TrajectorySequenceBuilder>,TrajectorySequence>',
    'Supplier<Trajectory>',
  ],
]);
// This is a map of old import names to new ones for the generated code.
const importMap = new Map([
  [
    'com.technototes.path.geometry.ConfigurablePoseD',
    'com.acmerobotics.roadrunner.geometry.Pose2d',
  ],
  [
    'com.technototes.path.geometry.ConfigurablePose',
    'com.acmerobotics.roadrunner.geometry.Pose2d',
  ],
  [
    'com.technototes.path.trajectorysequence.TrajectorySequence',
    'com.acmerobotics.roadrunner.trajectory.Trajectory',
  ],
  [
    'com.technototes.path.trajectorysequence.TrajectorySequenceBuilder',
    'com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder',
  ],
]);
// This is a set of imports that we want to remove from the generated code.
const removeImports = new Set([
  'com.acmerobotics.dashboard.config.Config',
  'com.technototes.library.command.Command',
  'com.technototes.library.command.SequentialCommandGroup',
  'com.technototes.path.command.TrajectorySequenceCommand',
]);
const extraImports = [
  'static java.lang.Math.toRadians',
  'java.util.function.Supplier',
];
/*** END CONFIGURATION STUFF ***/

// Context-free Helper functions:

function noWhitespace(input: string): string {
  return input.replace(/\s/g, '');
}

function unsupported(name: string, obj: object): void {
  if (obj && obj[name]) {
    throw new Error(`${name} is unsupported by the MeepMeep synchronizer`);
  }
}

function required(obj: unknown, message?: string): obj is NonNullable<unknown> {
  if (!obj) {
    throw new Error(message ?? 'Required type not found :(');
  }
  return true;
}

function assert(obj: unknown, message: string): obj is NonNullable<unknown> {
  if (!obj) {
    throw new Error(message);
  }
  return true;
}

enum TokenKind {
  NewType,
  LambdaParam,
  DeclType,
}
type Token = { kind: TokenKind; value: string };
function MakeToken(kind: TokenKind, value: string) {
  return { kind, value };
}

export type AutoConstantsTransformer = {
  transformFile: (fileContents: string) => void;
  collectImports: () => string;
  getTransformedCode: () => string[];
};

export function MakeAutoConstantsTransformer(): AutoConstantsTransformer {
  // Closure state:

  const imports = new Set<string>(extraImports);
  const theCode: string[] = [];
  let curFile = '';
  let prev = '';
  let classHelpersEmitted = false;

  // A little "context" stack
  const tokenStack = MakeStack<Token>();

  // A really dumb symbol table (That I'm only filling, but never using...)
  const symbolTypes = new Map<string, string>();

  // Closure-scoped functions:

  function codeSpit(...args: string[]): void {
    if (prev.length > 0) {
      args = [prev, ...args];
      prev = '';
    }
    if (args.length > 1) {
      theCode.push(args.join(''));
    } else {
      theCode.push(args[0]);
    }
  }

  function codeAdd(...args: string[]): void {
    prev += args.join('');
  }

  function codeReset() {
    prev = '';
  }

  function emitClassHelpers() {
    // TODO: Read the attributes from the Drivebase file!
    if (classHelpersEmitted) {
      return;
    }
    classHelpersEmitted = true;
    // public static MinVelocityConstraint MIN_VEL = new MinVelocityConstraint(Arrays.asList(
    //    new AngularVelocityConstraint(60 /*MAX_ANG_VEL*/),
    //    new MecanumVelocityConstraint(60 /*MAX_VEL*/, 14 /*TRACK_WIDTH*/)));
    // public static ProfileAccelerationConstraint PROF_ACCEL = new ProfileAccelerationConstraint(20/*MAX_ACCEL*/);
    codeSpit('public static Function<Pose2d, TrajectoryBuilder> fwdFunc;'); // = pose -> new TrajectoryBuilder(pose, MIN_VEL, PROF_ACCEL);")`);
    codeSpit('public static Function<Pose2d, TrajectoryBuilder> revFunc;'); // = pose -> new TrajectoryBuilder(pose, MIN_VEL, PROF_ACCEL);")`);
  }

  // Produces a single, unique set of import statements from the group of files.
  function collectImports(): string {
    return [...imports]
      .filter((val) => !removeImports.has(val))
      .sort()
      .map((i) => `import ${i};`)
      .join('\n');
  }

  /* Begin non-parser visitor helpers */
  function readCode(start: number, end: number): string {
    return curFile.substring(start, end + 1);
  }

  function getItemContent(f: unknown): string {
    if (
      hasFieldType(
        f,
        'location',
        chkAllOf(
          chkFieldType('startOffset', isNumber),
          chkFieldType('endOffset', isNumber),
        ),
      )
    ) {
      return readCode(f.location.startOffset, f.location.endOffset);
    }
    return '';
  }

  function getContent(field: unknown, sep?: string): string {
    return isArray(field)
      ? field.map(getItemContent).join(sep ?? ' ')
      : getItemContent(field);
  }
  /* End non-parser visitor helpers */

  class AutoConstTransformer extends BaseJavaCstVisitorWithDefaults {
    constructor() {
      super();
      this.validateVisitor();
    }

    transformFile(fileContents: string) {
      curFile = fileContents;
      this.visit(parse(fileContents));
    }

    maybeVisit(field: unknown): void {
      if (!isNonNullable(field)) return;
      this.visit(field as CstNode);
    }

    mustVisit(obj: unknown): void {
      if (isNonNullable(obj)) {
        this.maybeVisit(obj as CstNode);
      } else {
        throw new Error(`Missing required child element`);
      }
    }

    // Rewire the package:
    packageDeclaration(ctx: PackageDeclarationCtx, param?: unknown) {
      codeSpit(
        '// Original package: ',
        ctx.Identifier.map((token) => token.image).join('.'),
      );
      // codeSpit('package ', packageDir.join('.'), ';');
    }

    // Copy, reroute, or remove imports:
    importDeclaration(ctx: ImportDeclarationCtx, param?: unknown) {
      const stat = ctx.Static ? 'static ' : '';
      const star = ctx.Star ? '.*' : '';
      const imprt = ctx.packageOrTypeName
        .map((cst) => cst.children.Identifier.map((tok) => tok.image).join('.'))
        .join('.');
      const actual = importMap.has(imprt) ? importMap.get(imprt) : imprt;
      const key = `${stat}${actual}${star}`;
      imports.add(key);
    }

    classDeclaration(ctx: ClassDeclarationCtx, param?: any) {
      // Class declarations are smashed to just be public static:
      codeAdd('public static class ');
      classHelpersEmitted = false;
      this.mustVisit(ctx.normalClassDeclaration);
    }
    normalClassDeclaration(ctx: NormalClassDeclarationCtx, param?: any) {
      // No extends/implements are carried over
      this.mustVisit(ctx.typeIdentifier);
      codeSpit(' {');
      this.mustVisit(ctx.classBody);
      codeSpit('}');
    }
    // spit out the type identifier
    typeIdentifier(ctx: TypeIdentifierCtx, param?: any) {
      codeAdd(ctx.Identifier.map((tok) => tok.image).join('.'));
    }
    // We're picky about the type of class bodies supported
    classBodyDeclaration(ctx: ClassBodyDeclarationCtx, param?: any) {
      // No constructor!
      unsupported('constructorDeclaration', ctx);
      // No instance initializers!
      unsupported('instanceInitializer', ctx);
      // No static initializers!
      unsupported('staticInitializer', ctx);
      this.mustVisit(ctx.classMemberDeclaration);
    }
    classMemberDeclaration(ctx: ClassMemberDeclarationCtx, param?: any) {
      // We don't support methods!
      unsupported('methodDeclaration', ctx);
      // We don't support interfaces, either.
      unsupported('interfaceDeclaration', ctx);
      this.maybeVisit(ctx.fieldDeclaration);
      this.maybeVisit(ctx.classDeclaration);
    }
    fieldDeclaration(ctx: FieldDeclarationCtx, param?: any) {
      emitClassHelpers();
      // Add the field modifiers
      codeAdd(getContent(ctx.fieldModifier), ' ');
      this.mustVisit(ctx.unannType);
      this.mustVisit(ctx.variableDeclaratorList);
      if (tokenStack.peek()?.kind === TokenKind.DeclType) {
        tokenStack.pop();
      } else {
        // No constants, random variables, whatever. Delete 'em!
        codeReset();
      }
    }
    unannType(ctx: UnannTypeCtx, param?: any) {
      this.maybeVisit(ctx.unannReferenceType);
      if (ctx.unannPrimitiveTypeWithOptionalDimsSuffix) {
        codeAdd(getContent(ctx.unannPrimitiveTypeWithOptionalDimsSuffix));
      }
    }
    unannReferenceType(ctx: UnannReferenceTypeCtx, param?: any) {
      this.mustVisit(ctx.unannClassOrInterfaceType);
    }
    unannClassOrInterfaceType(ctx: UnannClassOrInterfaceTypeCtx, param?: any) {
      const typeName = noWhitespace(getContent(ctx.unannClassType));
      tokenStack.push(MakeToken(TokenKind.DeclType, typeName));
      codeAdd(typeMap.get(typeName) || typeName);
    }
    variableDeclaratorList(ctx: VariableDeclaratorListCtx, param?: any) {
      this.mustVisit(ctx.variableDeclarator);
    }
    variableDeclarator(ctx: VariableDeclaratorCtx, param?: any) {
      this.mustVisit(ctx.variableDeclaratorId);
      this.mustVisit(ctx.variableInitializer);
    }
    variableDeclaratorId(ctx: VariableDeclaratorIdCtx, param?: any) {
      if (required(ctx.Identifier, 'Unsupported Identifier-less varDeclID')) {
        const varName = ctx.Identifier[0].image;
        codeAdd(' ', varName, ' = ');
        // This is setting us up to keep track of variable name types,
        // so we can yoink ".toPose()" modifiers later in the file...
        const declType = tokenStack.peek();
        if (declType?.kind === TokenKind.DeclType) {
          symbolTypes.set(varName, declType.value);
        }
      }
    }
    variableInitializer(ctx: VariableInitializerCtx, param?: any) {
      this.mustVisit(ctx.expression);
    }
    expression(ctx: ExpressionCtx, param?: any) {
      this.maybeVisit(ctx.lambdaExpression);
      this.maybeVisit(ctx.conditionalExpression);
    }
    lambdaExpression(ctx: LambdaExpressionCtx, param?: any) {
      const params = getContent(ctx.lambdaParameters, ', ');
      // We need to transform b -> b.apply(...) to () -> func.apply(...)
      // Push the parameter(s) to the lambda for the lambdaBody to process
      tokenStack.push(MakeToken(TokenKind.LambdaParam, params));
      this.mustVisit(ctx.lambdaBody);
    }
    lambdaBody(ctx: LambdaBodyCtx, param?: any) {
      const expr = noWhitespace(getContent(ctx.expression));
      // console.log('Expr:  ', expr);
      unsupported('block', ctx);
      const top = tokenStack.pop();
      if (
        top &&
        top.kind === TokenKind.LambdaParam &&
        expr.startsWith(`${top.value}.apply(`)
      ) {
        const cleanupExpr = expr
          .substring(top.value.length)
          .replaceAll('.toPose()', '')
          .replaceAll('.toVec()', '.vec()');
        // This is a hack to deal with the fact that it looks like
        // MeepMeep doesn't allow us to use the .setReversed method.
        // It's not perfect, but it lets MeepMeep drive the bot backwards.
        const setRev = '.setReversed(true)';
        const thisFunc = cleanupExpr.includes(setRev) ? 'revFunc' : 'fwdFunc';
        codeSpit('() -> ', thisFunc, cleanupExpr.replace(setRev, ''), ';');
      } else if (top) {
        codeSpit(top.value, ' -> ', expr);
      } else {
        throw new Error('Unexpected Lambda body stack');
      }
    }
    // Conditional expression is the container for all non-lambdas
    // which is definitely a little weird, but whatever...
    conditionalExpression(ctx: ConditionalExpressionCtx, param?: any) {
      unsupported('QuestionMark', ctx);
      unsupported('Colon', ctx);
      this.mustVisit(ctx.binaryExpression);
    }
    binaryExpression(ctx: BinaryExpressionCtx, param?: any) {
      assert(
        !(
          ctx.AssignmentOperator ||
          ctx.BinaryOperator ||
          ctx.Instanceof ||
          ctx.pattern ||
          ctx.referenceType
        ),
        'unsupported child of binary expression',
      );
      this.maybeVisit(ctx.expression);
      this.mustVisit(ctx.unaryExpression);
    }
    unaryExpression(ctx: UnaryExpressionCtx, param?: any) {
      this.mustVisit(ctx.primary);
    }
    primary(ctx: PrimaryCtx, param?: any) {
      // This is a list...
      this.mustVisit(ctx.primaryPrefix);
      this.maybeVisit(ctx.primarySuffix);
    }
    primaryPrefix(ctx: PrimaryPrefixCtx, param?: any) {
      this.maybeVisit(ctx.newExpression);
      this.maybeVisit(ctx.fqnOrRefType);
      this.maybeVisit(ctx.literal);
    }
    primarySuffix(ctx: PrimarySuffixCtx, param?: any) {
      // console.log('primarySuffix', ctx);
      codeAdd(getContent(ctx));
    }
    fqnOrRefType(ctx: FqnOrRefTypeCtx, param?: any) {
      this.mustVisit(ctx.fqnOrRefTypePartFirst);
      codeAdd(getContent(ctx.fqnOrRefTypePartRest));
    }
    fqnOrRefTypePartFirst(ctx: FqnOrRefTypePartFirstCtx, param?: any) {
      this.mustVisit(ctx.fqnOrRefTypePartCommon);
    }
    fqnOrRefTypePartCommon(ctx: FqnOrRefTypePartCommonCtx, param?: any) {
      const lambdaArg = tokenStack.peek();
      if (lambdaArg && lambdaArg.kind === TokenKind.LambdaParam) {
        codeAdd('func');
      } else {
        codeAdd(getContent(ctx));
      }
    }
    newExpression(ctx: NewExpressionCtx, param?: any) {
      // console.log('new', ctx);
      codeAdd('new');
      this.maybeVisit(ctx.unqualifiedClassInstanceCreationExpression);
    }
    unqualifiedClassInstanceCreationExpression(
      ctx: UnqualifiedClassInstanceCreationExpressionCtx,
      param?: any,
    ) {
      const typeName = getContent(ctx.classOrInterfaceTypeToInstantiate);
      codeAdd(' ', typeMap.get(typeName) || typeName);
      tokenStack.push(MakeToken(TokenKind.NewType, typeName));
      this.maybeVisit(ctx.argumentList);
      tokenStack.pop();
    }
    argumentList(ctx: ArgumentListCtx, param?: any) {
      codeAdd('(');
      const top = tokenStack.peek();
      ctx.expression.forEach((node, idx) => {
        const prefix = idx === 0 ? '' : ', ';
        const code = getContent(node);
        if (
          idx === 2 &&
          top.kind === TokenKind.NewType &&
          top.value === 'ConfigurablePoseD'
        ) {
          codeAdd(prefix, 'toRadians(', code, ')');
        } else {
          codeAdd(prefix, code);
        }
      });
      codeSpit(');');
    }
  }
  const transformer = new AutoConstTransformer();
  return {
    transformFile: (fileContents: string) => {
      transformer.transformFile(fileContents);
    },
    collectImports,
    getTransformedCode: () => theCode,
  };
}
