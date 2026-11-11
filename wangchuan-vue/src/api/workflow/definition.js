import request from '@/utils/request'

// 流程定义列表
export function definitionList(query) {
  return request({ url: '/flowable/definition/list', method: 'get', params: query })
}

// 导入流程文件
export function definitionImport(data) {
  return request({ url: '/flowable/definition/import', method: 'post', data: data })
}

// 读取xml
export function readXml(deployId) {
  return request({ url: '/flowable/definition/readXml/' + deployId, method: 'get' })
}

// 读取图片
export function readImage(deployId) {
  return request({ url: '/flowable/definition/readImage/' + deployId, method: 'get' })
}

// 保存流程设计器xml
export function saveXml(data) {
  return request({ url: '/flowable/definition/save', method: 'post', data: data })
}

// 启动流程（按定义id）
export function startProcess(procDefId, data) {
  return request({ url: '/flowable/definition/start/' + procDefId, method: 'post', data: data })
}

// 激活/挂起
export function updateState(state, deployId) {
  return request({ url: '/flowable/definition/updateState', method: 'put', params: { state, deployId } })
}

// 删除流程
export function delDefinition(procDefKey) {
  return request({ url: '/flowable/definition/' + procDefKey, method: 'delete' })
}

// 已发布流程（发起用）
export function optionSelect() {
  return request({ url: '/flowable/definition/optionSelect', method: 'get' })
}

// 指定办理人列表
export function userList(params) {
  return request({ url: '/flowable/definition/userList', method: 'get', params: params })
}

// 指定办理组列表
export function roleList(params) {
  return request({ url: '/flowable/definition/roleList', method: 'get', params: params })
}

// 表达式列表
export function expList(params) {
  return request({ url: '/flowable/definition/expList', method: 'get', params: params })
}
