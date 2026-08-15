export type Point = { x: number; y: number; h?: number };

export const ConcreteHeadingType = Object.freeze({
  Tangent: 'T',
  Constant: 'C',
  Linear: 'I',
  Point: 'P',
  Reverse: 'R',
  Piecewise: 'L',
} as const);
export type ConcreteHeadingType =
  (typeof ConcreteHeadingType)[keyof typeof ConcreteHeadingType];

export type ConcreteTangentHeading = {
  type: typeof ConcreteHeadingType.Tangent;
};
export type ConcreteConstantHeading = {
  type: typeof ConcreteHeadingType.Constant;
  heading: number;
};
export type ConcreteLinearHeading = {
  type: typeof ConcreteHeadingType.Linear;
  headings: [number, number];
};
export type ConcretePointHeading = {
  type: typeof ConcreteHeadingType.Point;
  heading: Point;
};
export type ConcreteReversedHeading = {
  type: typeof ConcreteHeadingType.Reverse;
  heading: ConcreteReversibleHeading;
};
export type ConcretePiece = {
  start: number;
  end: number;
  heading: ConcreteSimpleHeading;
};
export type ConcretePiecewiseHeading = {
  type: typeof ConcreteHeadingType.Piecewise;
  pieces: ConcretePiece[];
};
export type ConcreteReversibleHeading =
  | ConcreteTangentHeading
  | ConcreteConstantHeading
  | ConcreteLinearHeading
  | ConcretePointHeading;
export type ConcreteSimpleHeading =
  ConcreteReversibleHeading | ConcreteReversedHeading;
export type ConcreteHeading = ConcreteSimpleHeading | ConcretePiecewiseHeading;
