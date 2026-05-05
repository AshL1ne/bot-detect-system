const { defineConfig } = require('@vue/cli-service')

/** Element Plus 表格/分页等在测量布局时会触发浏览器 ResizeObserver 的一次性告警，不应遮住页面 */
function shouldShowRuntimeOverlay(error) {
  const msg = error && error.message ? String(error.message) : String(error)
  if (/ResizeObserver loop/i.test(msg)) {
    return false
  }
  return true
}

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack(config) {
    config.plugin('html').tap((args) => {
      args[0].title = '恶意行为检测与可视分析 - Bot Detect System'
      return args
    })
  },
  devServer: {
    client: {
      overlay: {
        runtimeErrors: shouldShowRuntimeOverlay
      }
    }
  }
})
