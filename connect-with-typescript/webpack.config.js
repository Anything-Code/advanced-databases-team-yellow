const path = require('path');
// const nodeExternals = require('webpack-node-externals');

module.exports =  {
    entry: './src/index.ts',
    target: 'node',
    node: {
        __filename: false,
        __dirname: false
    },
    module: {
        rules: [
            {
                loader: 'ts-loader',
                test: /\.ts?$/
            },
            {
                test: /\.node$/,
                loader: 'node-loader',
            }
        ]
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'build.js',
        libraryTarget: 'commonjs'
    },
    resolve: {
        extensions: ['.ts', '.js', '.node']
    },
    // externals: [nodeExternals()]
}