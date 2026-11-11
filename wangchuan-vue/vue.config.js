'use strict'
const path = require('path')
const defaultSettings = require('./src/settings.js')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const name = defaultSettings.title || '轻量OA工作流' // 页面标题

// 端口
const port = process.env.port || process.env.npm_config_port || 80

// vue.config.js 配置说明
// 官方文档 https://cli.vuejs.org/zh/config/
module.exports = {
  // 部署生产环境时的基本 URL
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  // 构建输出目录
  outputDir: 'dist',
  // 静态资源目录
  assetsDir: 'static',
  // eslint-loader 是否在保存的时候检查（无 eslint 配置时关闭，避免构建报错）
  lintOnSave: false,
  // 生产环境是否生成 sourceMap 文件
  productionSourceMap: false,
  // 关闭文件名哈希
  filenameHashing: true,
  configureWebpack: {
    name: name,
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  },
  // webpack-dev-server 相关配置
  css: {
    loaderOptions: {
      sass: {
        additionalData: ``
      }
    }
  },
  devServer: {
    host: '0.0.0.0',
    port: port,
    open: true,
    // 开发模式 chunk 不带 hash，强制浏览器每次刷新都重新校验，避免旧代码被缓存
    headers: {
      'Cache-Control': 'no-cache'
    },
    proxy: {
      // 本地开发环境代理，将 /dev-api 转发到后端 8080
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:8080`,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      }
    },
    disableHostCheck: true
  },
  chainWebpack(config) {
    config.plugins.delete('preload')
    config.plugins.delete('prefetch')
    // 设置名称为 index.html 的 htmlWebpackPlugin
    config.plugin('html').tap(args => {
      args[0].title = name
      return args
    })
    // src/assets/icons 下的 svg 用 sprite-loader 注入为 symbol（供 svg-icon 的 use 引用），其余 svg 保持默认处理
    config.module
      .rule('svg')
      .exclude.add(resolve('src/assets/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({ symbolId: 'icon-[name]' })
      .end()
  }
}
