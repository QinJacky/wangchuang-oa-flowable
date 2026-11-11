package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.dto.FlowTaskDto;
import com.wangchuan.workflow.domain.vo.VariableInfoVo;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 转换实体
 *
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface FlowableSourceTargetMapper {
    FlowableSourceTargetMapper INSTANCE = Mappers.getMapper(FlowableSourceTargetMapper.class);

    /**
     * 变量转换
     *
     * @param variableInstance
     * @return
     */
    VariableInfoVo variableInstance2Info(HistoricVariableInstance variableInstance);

    FlowTaskDto copyFlowTaskDto(FlowTaskDto flowTaskDto);
}
