// A little control that expands or collapses the children

import { Button, Text, TextProps } from '@fluentui/react-components';
import { ChevronDownRegular, ChevronRightRegular } from '@fluentui/react-icons';
import { isString } from '@freik/typechk';
import { ReactElement, useState } from 'react';

export type ExpandoProps = TextProps & {
  children: ReactElement | ReactElement[];
  label: string | React.JSX.Element;
  defaultShow?: boolean;
  separator?: boolean;
  indent?: number;
};

// with the header provided
export function Expando({
  children,
  label,
  defaultShow,
  separator,
  indent,
  ...props
}: ExpandoProps): ReactElement {
  const indentSize = indent || 0;
  const [hidden, setHidden] = useState(!defaultShow);
  const button = (
    <Button
      appearance="transparent"
      icon={hidden ? <ChevronRightRegular /> : <ChevronDownRegular />}
      onClick={() => setHidden(!hidden)}
    />
  );
  const theHeader = (
    <span style={{ marginTop: 10 }}>
      {button}
      {isString(label) ? <Text {...props}>{label}</Text> : label}
    </span>
  );
  const padding = indentSize ? { paddingLeft: indentSize } : {};
  const display = hidden ? { display: 'none' } : {};
  return (
    <div>
      {theHeader}
      <div style={{ ...padding, ...display }}>{children}</div>
    </div>
  );
}
