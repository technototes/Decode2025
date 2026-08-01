import {
  chkAnyOf,
  chkArrayOf,
  chkObjectOfExactType,
  chkTupleOf,
  isNumber,
  typecheck,
} from '@freik/typechk';

import {
  ConcreteConstantHeading,
  ConcreteHeadingType,
  ConcreteLinearHeading,
  ConcretePiecewiseHeading,
  ConcretePointHeading,
  ConcreteReversedHeading,
  ConcreteReversibleHeading,
  ConcreteSimpleHeading,
  ConcreteTangentHeading,
} from './ConcreteEvalTypes';

export const chkPoint = chkObjectOfExactType({ x: isNumber, y: isNumber });
export const chkConcreteTangentHeading =
  chkObjectOfExactType<ConcreteTangentHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Tangent =>
      t === ConcreteHeadingType.Tangent,
  });
export const chkConcreteConstantHeading =
  chkObjectOfExactType<ConcreteConstantHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Constant =>
      t === ConcreteHeadingType.Constant,
    heading: isNumber,
  });
export const chkConcreteLinearHeading =
  chkObjectOfExactType<ConcreteLinearHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Linear =>
      t === ConcreteHeadingType.Linear,
    headings: chkTupleOf(isNumber, isNumber),
  });
export const chkConcretePointHeading =
  chkObjectOfExactType<ConcretePointHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Point =>
      t === ConcreteHeadingType.Point,
    heading: chkPoint,
  });
export const chkConcreteReversibleHeading: typecheck<ConcreteReversibleHeading> =
  chkAnyOf(
    chkConcreteTangentHeading,
    chkConcreteConstantHeading,
    chkConcreteLinearHeading,
    chkConcretePointHeading,
  );
export const chkConcreteReversedHeading =
  chkObjectOfExactType<ConcreteReversedHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Reverse =>
      t === ConcreteHeadingType.Reverse,
    heading: chkConcreteReversibleHeading,
  });
export const chkConcreteSimpleHeading: typecheck<ConcreteSimpleHeading> =
  chkAnyOf(chkConcreteReversibleHeading, chkConcreteReversedHeading);
export const chkConcretePiece = chkObjectOfExactType({
  start: isNumber,
  end: isNumber,
  heading: chkConcreteSimpleHeading,
});
export const chkConcretePieceWiseHeading =
  chkObjectOfExactType<ConcretePiecewiseHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Piecewise =>
      t === ConcreteHeadingType.Piecewise,
    pieces: chkArrayOf(chkConcretePiece),
  });
export const chkConcreteHeading = chkAnyOf(
  chkConcretePieceWiseHeading,
  chkConcreteSimpleHeading,
);
