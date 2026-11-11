package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.dto.FlowCommentDto;
import com.wangchuan.workflow.domain.qo.FlowCommentQo;

import java.util.List;

/**
 * 流程审批意见查询
 *
 * @Author  wangchuan
 **/
public interface FlowCommentMapper {

    /**
     * 流程审批意见列表
     *
     * @param qo 流程名称
     * @return List<Comment> 流程定义列表
     */
    List<FlowCommentDto> selectCommentList(FlowCommentQo qo);
}
