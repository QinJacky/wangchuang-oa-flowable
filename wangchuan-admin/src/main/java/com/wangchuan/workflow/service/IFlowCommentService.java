package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.dto.FlowCommentDto;
import com.wangchuan.workflow.domain.qo.FlowCommentQo;

import java.util.List;

/**
 * <p> 流程审批意见接口 </p>
 *
 * @Author  wangchuan
 */
public interface IFlowCommentService {

    /**
     * 流程审批意见列表
     *
     * @param qo 查询参数
     * @return List<FlowCommentDto> 流程审批意见列表
     */
    List<FlowCommentDto> getComments(FlowCommentQo qo);
}
