package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.dto.FlowHistoricActivityInstanceDto;
import com.wangchuan.workflow.domain.dto.FlowHistoricProcessInstanceDto;
import com.wangchuan.workflow.domain.dto.FlowIdentityLinkDto;
import com.wangchuan.workflow.domain.qo.FlowHistoricActivityInstanceQo;
import com.wangchuan.workflow.domain.qo.FlowHistoricProcessInstanceQo;
import com.wangchuan.workflow.domain.qo.FlowIdentityLinkQo;

import java.util.List;

/**
 * <p> 流程历史接口 </p>
 *
 * @Author  wangchuan
 */
public interface IFlowHistoryService {

    /**
     * 流程审批意见列表
     *
     * @param qo 查询参数
     * @return List<Comment>
     */
    List<FlowIdentityLinkDto> getIdentityLinks(FlowIdentityLinkQo qo);

    /**
     * 历史环节实例列表
     *
     * @param qo 查询参数
     * @return List<FlowHistoricActivityInstanceDto>
     */
    List<FlowHistoricActivityInstanceDto> getHistoricActivityInstances(FlowHistoricActivityInstanceQo qo);

    /**
     * 历史流程实例列表
     *
     * @param qo 查询参数
     * @return List<FlowHistoricProcessInstanceDto>
     */
    List<FlowHistoricProcessInstanceDto> getHistoricProcessInstances(FlowHistoricProcessInstanceQo qo);
}
