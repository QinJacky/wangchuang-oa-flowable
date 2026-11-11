package com.wangchuan.workflow.domain;

import lombok.Data;

/**
 * <p> 表单下拉项 </p>
 *
 * @Author  wangchuan
 */
@Data
public class FormOption {

    /**
     * 表单ID
     */
    private String formId;

    /**
     * 表单名称
     */
    private String formName;

}
