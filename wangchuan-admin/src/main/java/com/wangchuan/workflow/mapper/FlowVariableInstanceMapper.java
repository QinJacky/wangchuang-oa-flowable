package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.dto.FlowVariableInstanceDto;
import com.wangchuan.workflow.domain.qo.FlowVariableInstanceQo;

import java.util.List;

/**
 * <p> 流程变量实例 </p>
 *
 * @Author  wangchuan
 */
public interface FlowVariableInstanceMapper {

    /**
     * 流程变量实例列表
     *
     * @param qo 查询参数
     * @return List<FlowVariableInstanceDto>
     */
    List<FlowVariableInstanceDto> selectVariableInstances(FlowVariableInstanceQo qo);
}
