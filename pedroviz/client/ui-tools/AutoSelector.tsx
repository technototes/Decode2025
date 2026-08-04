import { ReactElement, useCallback, useEffect } from 'react';

import {
  Button,
  Menu,
  MenuItem,
  MenuList,
  MenuPopover,
  MenuTrigger,
} from '@fluentui/react-components';
import { ChevronDown16Regular } from '@fluentui/react-icons';
import { isString } from '@freik/typechk';

// Show a selection, unless there are no items, then disable the selector entirely
export function AutoSelector({
  id,
  prompt,
  items,
  selected,
  setSelected,
  default: defItem,
}: {
  id?: string;
  prompt: string;
  items: string[];
  selected: string;
  setSelected: (item: string) => void;
  default?: string;
}): ReactElement {
  let selectedItem = selected.length === 0 ? prompt : selected;

  // This may trigger a re-render, so it has to happen in an effect
  useEffect(() => {
    if (items.length === 1 && selected !== items[0]) {
      // If we only have 1 item go ahead & select it.
      setSelected(items[0]!);
    } else if (selected === '' && isString(defItem) && defItem.length > 0) {
      setSelected(defItem);
    }
  }, [items, selected, defItem, setSelected]);
  return (
    <Menu>
      <MenuTrigger>
        <Button disabled={items.length === 0} id={id}>
          {selectedItem}
          <ChevronDown16Regular style={{ marginLeft: 10 }} />
        </Button>
      </MenuTrigger>
      <MenuPopover>
        <MenuList>
          {items.map((val) => (
            <MenuItem
              key={val}
              /* Needs a delay to prevent shenanigans with React & even timing */
              onClick={() => setTimeout(() => setSelected(val), 0)}>
              {val}
            </MenuItem>
          ))}
        </MenuList>
      </MenuPopover>
    </Menu>
  );
}
