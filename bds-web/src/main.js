import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

/** 缓解缩放、表格列宽计算等场景下 Chrome 对 ResizeObserver 的误报（控制台 / dev overlay） */
function patchResizeObserver() {
  const RO = window.ResizeObserver
  if (!RO || RO.__bdsPatched) return
  window.ResizeObserver = class extends RO {
    constructor(callback) {
      super((entries, observer) => {
        window.requestAnimationFrame(() => {
          callback(entries, observer)
        })
      })
    }
  }
  window.ResizeObserver.__bdsPatched = true
}
patchResizeObserver()

const app = createApp(App)
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
