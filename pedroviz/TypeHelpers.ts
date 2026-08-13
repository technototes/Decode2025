// This is a Typescript mechanism to allow strings to be constrained a bit more.
// I'm not sure if it's worth the trouble or not...
declare const brand: unique symbol;
export type Nominal<T, Brand extends string> = T & { readonly [brand]: Brand };
