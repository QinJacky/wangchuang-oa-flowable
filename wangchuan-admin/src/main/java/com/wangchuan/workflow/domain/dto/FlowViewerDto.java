package com.wangchuan.workflow.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程查看
 *
 * @Author  wangchuan
 */
@Data
public class FlowViewerDto implements Serializable {

    /**
     * 流程key
     */
    private String key;

    /**
     * 是否完成(已经审批)
     */
    private boolean completed;
}
