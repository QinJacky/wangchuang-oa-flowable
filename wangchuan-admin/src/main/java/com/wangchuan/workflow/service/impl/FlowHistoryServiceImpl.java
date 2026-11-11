package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.dto.FlowHistoricActivityInstanceDto;
import com.wangchuan.workflow.domain.dto.FlowHistoricProcessInstanceDto;
import com.wangchuan.workflow.domain.dto.FlowIdentityLinkDto;
import com.wangchuan.workflow.domain.qo.FlowHistoricActivityInstanceQo;
import com.wangchuan.workflow.domain.qo.FlowHistoricProcessInstanceQo;
import com.wangchuan.workflow.domain.qo.FlowIdentityLinkQo;
import com.wangchuan.workflow.mapper.FlowHistoryMapper;
import com.wangchuan.workflow.service.IFlowHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p> 流程历史接口实现 </p>
 *
 * @Author  wangchuan
 */
@Service
public class FlowHistoryServiceImpl implements IFlowHistoryService {

    @Autowired
    private FlowHistoryMapper flowHistoryMapper;

    @Override
    public List<FlowIdentityLinkDto> getIdentityLinks(FlowIdentityLinkQo qo) {
        return flowHistoryMapper.selectIdentityLinks(qo);
    }

    @Override
    public List<FlowHistoricActivityInstanceDto> getHistoricActivityInstances(FlowHistoricActivityInstanceQo qo) {
        return flowHistoryMapper.selectHistoricActivityInstances(qo);
    }

    @Override
    public List<FlowHistoricProcessInstanceDto> getHistoricProcessInstances(FlowHistoricProcessInstanceQo qo) {
        return flowHistoryMapper.selectHistoricProcessInstances(qo);
    }
}
