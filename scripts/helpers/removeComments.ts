const PLAIN = Symbol('plain');
const IN_DQSTRING = Symbol('in dqstring');
const IN_SQSTRING = Symbol('in sqstring');
const IN_MLCOMMENT = Symbol('in multiline comment');
const IN_SLCOMMENT = Symbol('in singleline comment');

export function removeComments(contents: string): string {
  // This is a big ol' state-machine-based parser to remove
  // comments. See the "CommentStateMachine.excalidraw" file
  // for a basic understanding of the state-machine being run.
  // I assume it's not perfect, but it works well enough for now...
  let result = '';
  let state = PLAIN;
  let justSawSlash = false;
  let justSawBackslash = false;
  let justSawStar = false;
  for (const char of contents) {
    switch (state) {
      case IN_SLCOMMENT:
        if (char === '\n') {
          state = PLAIN;
          result += '\n';
        }
        continue;
      case IN_MLCOMMENT:
        if (justSawStar && char === '/') {
          state = PLAIN;
          justSawStar = false;
          continue;
        }
        justSawStar = char === '*';
        continue;
      case IN_DQSTRING:
        if (char === '"') {
          state = justSawBackslash ? IN_DQSTRING : PLAIN;
        }
        break;
      case IN_SQSTRING:
        if (char === '"') {
          state = justSawBackslash ? IN_SQSTRING : PLAIN;
        }
        break;
      case PLAIN:
        if (justSawSlash) {
          if (char === '/') {
            justSawSlash = false;
            state = IN_SLCOMMENT;
            continue;
          }
          if (char === '*') {
            justSawSlash = false;
            state = IN_MLCOMMENT;
            continue;
          }
          // Output the slash that we saw in the previous iteration,
          // since it wasn't part of a comment.
          result += '/';
        }
        if (char === '"') {
          state = IN_DQSTRING;
        } else if (char === "'") {
          state = IN_SQSTRING;
        }
        break;
    }
    justSawSlash = char === '/';
    justSawBackslash = char === '\\';
    if (!justSawSlash && char !== '\r') {
      // For a front-slash not in a string, don't output it.
      // It might be the beginning of a comment. We handle it above...
      // Also: Strip out any carriage returns.
      result += char;
    }
  }
  // Deal with any trailing states
  if (justSawSlash && state === PLAIN) {
    result += '/';
  }
  return result;
}
