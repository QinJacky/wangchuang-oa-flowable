package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.WorkflowMainText;
import com.wangchuan.workflow.module.MainInfoResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 对象转换
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface WorkFileSourceTargetMapper {
    WorkFileSourceTargetMapper INSTANCE = Mappers.getMapper(WorkFileSourceTargetMapper.class);

    MainInfoResult toMainInfoResult(WorkflowMainText workflowMainText);
}
