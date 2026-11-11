package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.qo.FlowVariableInstanceQo;

import java.util.Map;

/**
 * <p> 流程变量接口 </p>
 *
 * @Author  wangchuan
 */
public interface IFlowVariableInstanceService {

    /**
     * 流程变量列表
     *
     * @param qo 查询参数
     * @return Map<String, Object>
     */
    Map<String, Object> getVariableInstanceMap(FlowVariableInstanceQo qo);
}
