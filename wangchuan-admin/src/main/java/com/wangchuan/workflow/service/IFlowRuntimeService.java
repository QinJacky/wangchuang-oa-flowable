package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.dto.FlowProcessInstanceDto;
import com.wangchuan.workflow.domain.qo.FlowProcessInstanceQo;

import java.util.List;

/**
 * <p> 流程运行接口 </p>
 *
 * @Author  wangchuan
 */
public interface IFlowRuntimeService {

    /**
     * 获取流程实例列表
     *
     * @param qo
     * @return List<FlowProcessInstanceDto>
     */
    public List<FlowProcessInstanceDto> getProcessInstances(FlowProcessInstanceQo qo);
}
