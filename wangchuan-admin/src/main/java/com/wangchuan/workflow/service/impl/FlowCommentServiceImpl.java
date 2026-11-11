package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.dto.FlowCommentDto;
import com.wangchuan.workflow.domain.qo.FlowCommentQo;
import com.wangchuan.workflow.mapper.FlowCommentMapper;
import com.wangchuan.workflow.service.IFlowCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p> 流程审批意见接口实现 </p>
 *
 * @Author  wangchuan
 */
@Service
public class FlowCommentServiceImpl implements IFlowCommentService {

    @Autowired
    private FlowCommentMapper flowCommentMapper;

    @Override
    public List<FlowCommentDto> getComments(FlowCommentQo qo) {
        return flowCommentMapper.selectCommentList(qo);
    }
}
