package com.wangchuan.workflow.domain.qo;

import lombok.Data;

import java.util.List;

/**
 * 委托关系查询对象
 *
 * @Author  wangchuan
 */
@Data
public class WorkflowEntrustRelationQo {

    /**
     * 委托人ID
     */
    private List<String> entrustIds;

    /**
     * 被委托人ID
     */
    private List<String> beEntrustIds;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 委托方式，0-全部， 1-部分
     */
    private String type;

    /**
     * 模板ID集合
     */
    private List<String> templateIds;
}
