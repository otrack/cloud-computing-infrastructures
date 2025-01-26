const path = require('path')

module.exports = {
  mode: 'development',
  devtool: 'source-map',
  entry: {
    bachelor : './bachelor.js'
  },
  output: {
    globalObject: 'self',
    path: path.resolve(__dirname, './dist/'),
    filename: '[name].bundle.js',
    publicPath: '/bachelor/dist/'
  },
  devServer: {
    static: path.join(__dirname),
    compress: true
  }
}
