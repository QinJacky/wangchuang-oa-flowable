package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.WorkflowEntrust;
import com.wangchuan.workflow.domain.WorkflowSecretary;
import com.wangchuan.workflow.domain.vo.WorkflowEntrustVO;
import com.wangchuan.workflow.domain.vo.WorkflowSecretaryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 转换实体
 *
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface WorkSettingSourceTargetMapper {
    WorkSettingSourceTargetMapper INSTANCE = Mappers.getMapper(WorkSettingSourceTargetMapper.class);

    WorkflowEntrust entrustVo2entrust(WorkflowEntrustVO workflowEntrustVo);

    WorkflowEntrustVO entrust2entrustVo(WorkflowEntrust entrust);

    WorkflowSecretaryVO secretary2secretaryVO(WorkflowSecretary secretary);

}
