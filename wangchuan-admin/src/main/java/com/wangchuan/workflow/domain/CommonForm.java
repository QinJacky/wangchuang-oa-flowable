package com.wangchuan.workflow.domain;

import lombok.Data;

import java.util.Map;

/**
 * <p> 公共表单数据 </p>
 *
 * @Author  wangchuan
 */
@Data
public class CommonForm {

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 表单数据
     */
    private Object formData;

    /**
     * 模板信息
     */
    private Template template;

    /**
     * 字段值
     */
    private Map<String, Object> valData;
}
