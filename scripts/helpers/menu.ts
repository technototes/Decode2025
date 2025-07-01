import readline from 'readline';

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

export const Ask = (query: string): Promise<string> =>
  new Promise((resolve) => rl.question(query, resolve));

export type MenuItem = [string, () => Promise<boolean>];

// Display a menu with a header, and run the function for the menu item selected
export async function Menu(header: string, menu: MenuItem[]): Promise<void> {
  let done = false;
  while (!done) {
    const bar = '*'.repeat(header.length + 4);
    console.log();
    console.log(bar);
    console.log('*', header, '*');
    console.log(bar);
    console.log();
    menu.forEach((val: [string, () => Promise<boolean>], i: number) =>
      console.log(`${i + 1}: ${val[0]}`),
    );
    console.log();
    const res = await Ask('Please select an option: ');
    const cleaned = res.trim();
    const opt = Number.parseInt(cleaned, 10);
    if (isNaN(opt) || opt < 1 || opt > menu.length) {
      // Let's check for unique first letters, just for fun...
      if (isNaN(opt) && cleaned.length === 1) {
        const itemsMatched = menu.filter(
          (mnuItem: MenuItem) =>
            mnuItem[0][0].toLowerCase() === cleaned.toLowerCase(),
        );
        // If we found *only one* menu item with that first character,
        // proceed with that selection
        if (itemsMatched.length === 1) {
          done = await itemsMatched[0][1]();
          continue;
        }
      }
      console.error(`Please enter a number from 1 to ${menu.length}`);
    } else {
      done = await menu[opt - 1][1]();
    }
  }
}

export function Sleep(ms: number): Promise<void> {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}

export async function Error(message: string): Promise<false> {
  console.error('>>>> ');
  console.error('>>>>', message);
  console.error('>>>> ');
  await Sleep(3000);
  return false;
}
