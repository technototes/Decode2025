import { ReactElement } from 'react';

import {
  Button,
  Menu,
  MenuButton,
  MenuItem,
  MenuItemRadio,
  MenuList,
  MenuPopover,
  MenuProps,
  MenuTrigger,
  useId,
} from '@fluentui/react-components';
import { ChevronDown16Regular } from '@fluentui/react-icons';
import { isString } from '@freik/typechk';

// Show a selection, unless there are no items, then disable the selector entirely
export function AutoSelector({
  prompt,
  items,
  selected,
  setSelected,
  default: defItem,
}: {
  prompt: string;
  items: string[];
  selected: string;
  setSelected: (item: string) => void;
  default?: string;
}): ReactElement {
  const id = useId('ADS');
  /*
  const onChange: MenuProps['onCheckedValueChange'] = (_, { checkedItems }) => {
    if (checkedItems.length >= 1) {
      setSelected(checkedItems[0]!);
    }
  };
  */
  let selectedItem = selected.length === 0 ? prompt : selected;
  if (items.length === 1 && selected !== items[0]) {
    // If we only have 1 item go ahead & select it, but schedule it in the future
    // so we don't screw up the render cycle in an unpredictable manner.
    setTimeout(() => setSelected(items[0]!), 0);
    // This *should* prevent a full visual re-render.
    selectedItem = items[0]!;
  } else if (selected === '' && isString(defItem) && defItem.length > 0) {
    // If we don't have a selection, pick the default one
    setTimeout(() => setSelected(defItem), 0);
    // This *should* prevent a full visual re-render.
    selectedItem = defItem;
  }
  return (
    <Menu>
      <MenuTrigger>
        <Button disabled={items.length === 0}>
          {selectedItem}&nbsp;
          <ChevronDown16Regular />
        </Button>
      </MenuTrigger>
      <MenuPopover>
        <MenuList>
          {/*  onCheckedValueChange={onChange} */}
          {items.map((val) => (
            <MenuItem key={`${id}:${val}`} onClick={() => setSelected(val)}>
              {val}
            </MenuItem>
          ))}
        </MenuList>
      </MenuPopover>
    </Menu>
  );
}
