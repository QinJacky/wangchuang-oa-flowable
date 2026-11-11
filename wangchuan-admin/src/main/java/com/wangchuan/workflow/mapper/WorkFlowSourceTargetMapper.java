package com.wangchuan.workflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 对象转换
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface WorkFlowSourceTargetMapper {
    WorkFlowSourceTargetMapper INSTANCE = Mappers.getMapper(WorkFlowSourceTargetMapper.class);
}
