package com.wangchuan.workflow.enums;

/**
 * 流程业务表单状态
 * <p>
 * 对应文档：草稿(DRAFT)仅保存表单不启动流程；提交(RUNNING)后流程停在第一个审批节点。
 *
 * @Author  wangchuan
 */
public enum ProcessStatusEnum {
    DRAFT("DRAFT", "草稿"),
    RUNNING("RUNNING", "运行中"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已驳回"),
    CANCELLED("CANCELLED", "已撤销");

    private final String code;
    private final String name;

    ProcessStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
