const { defineConfig } = require('@vue/cli-service')

/** 缩放页面 / Element Plus 表格测量布局时，浏览器可能上报 ResizeObserver 相关告警，多为无害时序问题 */
function runtimeErrorText(error) {
  if (!error) return ''
  if (typeof error === 'string') return error
  const parts = [
    error.message,
    error.stack,
    error.reason && error.reason.message,
    error.reason && error.reason.stack,
    error.error && error.error.message
  ]
  return parts.filter(Boolean).join('\n')
}

function shouldShowRuntimeOverlay(error) {
  const msg = runtimeErrorText(error)
  if (/ResizeObserver/i.test(msg)) {
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
