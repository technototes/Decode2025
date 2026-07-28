import * as child_proc from 'child_process';

type BufStr = string | Buffer;

export function OpenBrowser(url: string): void {
  let command = '';
  const plat = process.platform;
  if (/^darwin/.test(plat)) {
    command = 'open';
  } else if (/^win/.test(plat)) {
    command = 'start';
  } else if (/^linux/.test(plat)) {
    command = 'xdg-open';
  } else {
    console.log(`open a brower to ${url} to launch the application`);
    return;
  }
  child_proc.exec(
    command + ' ' + url,
    (err, stdout: BufStr, stderr: BufStr) => {
      console.log('stdout: ' + stdout.toString());
      console.log('stderr: ' + stderr.toString());
      if (err !== null && err !== undefined) {
        console.log(`exec error: ${JSON.stringify(err)}`);
      }
    },
  );
}
