import { CSSProperties } from 'react';

import { RowData } from './types';

// Generate the grid row start/end for a span starting at a *zero* based row index
// "start" and a row count height of "count".
export function rowSpan(offset: number, rd: RowData): CSSProperties {
  return {
    gridRowStart: rd.offset + offset,
    gridRowEnd: rd.offset + rd.size + offset,
    alignSelf: 'center',
  };
}
