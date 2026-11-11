package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.dto.FlowProcDefDto;
import com.wangchuan.workflow.domain.qo.FlowProcDefQo;

import java.util.List;

/**
 * 流程定义查询
 *
 * @Author  wangchuan
 **/
public interface FlowDeployMapper {

    /**
     * 流程定义列表
     *
     * @param qo 流程名称
     * @return List<FlowProcDefDto> 流程定义列表
     */
    List<FlowProcDefDto> selectDeployList(FlowProcDefQo qo);
}
