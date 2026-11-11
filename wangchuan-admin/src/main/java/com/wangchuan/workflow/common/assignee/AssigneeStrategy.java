package com.wangchuan.workflow.common.assignee;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.UserTask;

/**
 * 审批人确定策略
 * <p>
 * 将"审批人如何确定"抽象为策略接口，引擎在运行时根据流程节点配置
 * （userType / dataType）动态选择策略，计算下一环节候选人。
 * 现有策略：固定审批人、按角色、候选人（发起人/上一环节指定）等。
 * </p>
 */
public interface AssigneeStrategy {

    /**
     * 是否支持该节点配置
     *
     * @param userType 处理人类型（assignee / candidateUsers）
     * @param dataType 数据类型（roles / dynamic / 空）
     * @return 是否支持
     */
    boolean supports(String userType, String dataType);

    /**
     * 计算候选人用户ID集合
     *
     * @param userTask  用户任务
     * @param variables 流程变量
     * @return 候选人用户ID集合
     */
    List<String> resolveUserIds(UserTask userTask, Map<String, Object> variables);
}
