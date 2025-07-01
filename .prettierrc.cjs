/** @type {import("prettier").Options} */
module.exports = {
  printWidth: 80,
  trailingComma: 'all',
  singleQuote: true,
  proseWrap: 'always',
  tabWidth: 2,
  useTabs: false,
  plugins: [require.resolve('prettier-plugin-java')],
  overrides: [
    {
      files: '*.json',
      options: { parser: 'json' },
    },
    {
      files: '*.java',
      options: { tabWidth: 4, printWidth: 100 },
    },
  ],
};
