const path = require('path');

module.exports = {
  entry: './src/index.js',
  target: ["web", "es5"],
  output: {
    filename: 'inject.js',
    path: path.resolve(__dirname, '../src/main/java/delight/nashornsandbox/internal/resources'),
    library: 'injectJs',
    libraryTarget: 'umd',
    globalObject: "typeof self !== 'undefined' ? self : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : this"
  },
  mode: 'production',
  optimization: {
    minimize: true
  },
  module: {
    rules: [ { 
        test: /\.js$/, 
        exclude: /node_modules/, 
        use: { loader: 'babel-loader', }, }, ],
  }
};
