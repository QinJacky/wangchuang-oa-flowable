import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import 'normalize.css/normalize.css'
import '@/assets/styles/index.scss'

import App from './App'
import store from './store'
import router from './router'
import './permission'

import '@/assets/icons' // 注册 svg-icon 组件

import './assets/styles/ruoyi.scss'
import Ruoyi from '@/components/Ruoyi' // 全局组件挂载

// ===== 移植源码页面所需的全局基础设施 =====
import directive from './directive'
import plugins from './plugins'
import { download } from '@/utils/request'
import { getDicts } from "@/api/system/dict/data"
import { getConfigKey } from "@/api/system/config"
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/ruoyi"
import DictData from '@/components/DictData'
import ImagePreview from '@/components/ImagePreview'
import modelerStore from '@/components/Process/common/global'
import * as socketApi from '@/utils/socket/wssocket'
import uploader from 'vue-simple-uploader'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
// 全局流程设计器状态（流程设计/模型器页面依赖 this.modelerStore）
Vue.prototype.modelerStore = modelerStore
// 全局 WebSocket 相关原型（源码 login.vue / store 通过 connectWs 使用）
Vue.prototype.$wsApi = socketApi
Vue.prototype.$eventBus = new Vue()

Vue.use(directive)
Vue.use(plugins)
DictData.install()
Vue.component('ImagePreview', ImagePreview)
Vue.use(Element, {
  size: Cookies.get('size') || 'medium'
})
Vue.use(Ruoyi)
// 全局注册 vue-simple-uploader（附件分片上传组件 <uploader> 等）
Vue.use(uploader)

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
