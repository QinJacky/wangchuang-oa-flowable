package com.wangchuan.workflow.domain;

import com.wangchuan.workflow.domain.vo.FlowTaskVo;
import com.wangchuan.workflow.domain.Template;
import lombok.Data;

/**
 * <p> 流程提交实体 </p>
 *
 * @Author  wangchuan
 */
@Data
public class CommonFlowSubmit {

    /**
     * 操作类型，提交、驳回、转交、退回等
     */
    private String operateType;

    /**
     * 流程数据
     */
    private FlowTaskVo flowTask;

    /**
     * 模板信息
     */
    private Template template;
}
