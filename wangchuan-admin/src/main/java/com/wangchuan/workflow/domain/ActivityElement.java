package com.wangchuan.workflow.domain;

import lombok.Data;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

import java.util.List;

/**
 * <p> 环节元素 </p>
 *
 * @Author  wangchuan
 */
@Data
public class ActivityElement {

    /**
     * 入口连线
     */
    private List<SequenceFlow> incomingFlows;

    /**
     * 出口连线
     */
    private List<SequenceFlow> outgoingFlows;

    /**
     * 节点（倒序递归）
     */
    private UserTask userTask;

    /**
     * 上一个环节元素
     */
    private ActivityElement lastActivityElement;
}
