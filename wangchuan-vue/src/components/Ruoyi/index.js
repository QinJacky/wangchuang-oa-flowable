import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import DictTag from '@/components/DictTag'
import TreeSelect from '@/components/TreeSelect'

export default {
  install(Vue) {
    Vue.component('Pagination', Pagination)
    Vue.component('RightToolbar', RightToolbar)
    Vue.component('DictTag', DictTag)
    Vue.component('TreeSelect', TreeSelect)
  }
}
