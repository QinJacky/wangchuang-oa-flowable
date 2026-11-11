package com.wangchuan.workflow.domain;

import lombok.Data;

import java.util.Map;

/**
 * 流程表单扩展对象
 *
 * @Author  wangchuan
 */
@Data
public class FormExtend extends Form {

    /**
     * 字段值
     */
    private Map<String, Object> valData;
}
