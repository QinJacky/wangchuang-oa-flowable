import request from '@/utils/request'

// 业务表管理
export function listBizTable(query) {
  return request({ url: '/biz/table/list', method: 'get', params: query })
}
export function getBizTable(id) {
  return request({ url: '/biz/table/' + id, method: 'get' })
}
export function addBizTable(data) {
  return request({ url: '/biz/table', method: 'post', data: data })
}
export function updateBizTable(data) {
  return request({ url: '/biz/table', method: 'put', data: data })
}
export function delBizTable(ids) {
  return request({ url: '/biz/table/' + ids, method: 'delete' })
}
export function optionSelectBizTable() {
  return request({ url: '/biz/table/optionSelect', method: 'get' })
}
export function generateDdl(id) {
  return request({ url: '/biz/table/generateDdl/' + id, method: 'get' })
}
