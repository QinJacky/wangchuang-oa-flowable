package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.dto.FlowProcessInstanceDto;
import com.wangchuan.workflow.domain.qo.FlowProcessInstanceQo;
import com.wangchuan.workflow.mapper.FlowProcessInstanceMapper;
import com.wangchuan.workflow.service.IFlowRuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p> 流程实例接口实现 </p>
 *
 * @Author  wangchuan
 */
@Service
public class FlowRuntimeServiceImpl implements IFlowRuntimeService {

    @Autowired
    private FlowProcessInstanceMapper flowProcessInstanceMapper;

    /**
     * 获取流程实例列表
     *
     * @param qo
     * @return
     */
    @Override
    public List<FlowProcessInstanceDto> getProcessInstances(FlowProcessInstanceQo qo) {
        return flowProcessInstanceMapper.selectProcessInstances(qo);
    }
}
