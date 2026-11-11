package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.dto.FlowProcessInstanceDto;
import com.wangchuan.workflow.domain.qo.FlowProcessInstanceQo;

import java.util.List;

/**
 * <p> 流程实例映射 </p>
 *
 * @Author  wangchuan
 */
public interface FlowProcessInstanceMapper {

    /**
     * 获取流程实例列表
     *
     * @param qo
     * @return List<FlowProcessInstanceDto>
     */
    List<FlowProcessInstanceDto> selectProcessInstances(FlowProcessInstanceQo qo);
}
