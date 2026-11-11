import request from '@/utils/request'

// 启动流程（带表单）
export function startFlow(data) {
  return request({ url: '/workflow/handle/startFlow', method: 'post', data: data })
}

// 取回
export function revokeProcess(data) {
  return request({ url: '/workflow/handle/revokeProcess', method: 'post', data: data })
}

// 驳回
export function taskReject(data) {
  return request({ url: '/workflow/handle/reject', method: 'post', data: data })
}

// 终止（发起人）
export function finishProcess(data) {
  return request({ url: '/workflow/handle/finishProcess', method: 'post', data: data })
}

// 退回
export function taskReturn(data) {
  return request({ url: '/workflow/handle/return', method: 'post', data: data })
}

// 可退回节点
export function returnList(data) {
  return request({ url: '/workflow/handle/returnList', method: 'post', data: data })
}

// 认领
export function claimTask(data) {
  return request({ url: '/workflow/handle/claim', method: 'post', data: data })
}

// 取消认领
export function unClaimTask(data) {
  return request({ url: '/workflow/handle/unClaim', method: 'post', data: data })
}

// 委派
export function delegateTask(data) {
  return request({ url: '/workflow/handle/delegate', method: 'post', data: data })
}

// 转办
export function assignTask(data) {
  return request({ url: '/workflow/handle/assign', method: 'post', data: data })
}

// 抄送
export function copyTask(data) {
  return request({ url: '/workflow/handle/copyTask', method: 'post', data: data })
}

// 下一节点
export function nextFlowNode(data) {
  return request({ url: '/workflow/handle/nextFlowNode', method: 'post', data: data })
}

// 流程变量
export function processVariables(taskId) {
  return request({ url: '/workflow/handle/processVariables/' + taskId, method: 'get' })
}

// 流程图
export function diagram(processId) {
  return request({ url: '/workflow/handle/diagram/' + processId, method: 'get' })
}

// 流程xml与节点
export function flowXmlAndNode(params) {
  return request({ url: '/workflow/handle/flowXmlAndNode', method: 'get', params: params })
}

// 任务详情
export function getFlowTask(taskId) {
  return request({ url: '/workflow/handle/info?taskId=' + taskId, method: 'get' })
}

// 历史任务详情
export function getHistoryFlowTask(taskId) {
  return request({ url: '/workflow/handle/history/info?taskId=' + taskId, method: 'get' })
}

// 审批意见
export function flowCmts(data) {
  return request({ url: '/workflow/handle/flowCmts', method: 'post', data: data })
}

// 流转记录
export function flowRecord(data) {
  return request({ url: '/workflow/handle/flowRecord', method: 'post', data: data })
}

// 催办
export function urge(data) {
  return request({ url: '/workflow/handle/urge', method: 'post', data: data })
}

// 校验提交
export function checkComplete(data) {
  return request({ url: '/workflow/handle/checkComplete', method: 'post', data: data })
}

// 校验退回
export function checkReturn(data) {
  return request({ url: '/workflow/handle/checkReturn', method: 'post', data: data })
}

// 校验驳回
export function checkReject(data) {
  return request({ url: '/workflow/handle/checkReject', method: 'post', data: data })
}
