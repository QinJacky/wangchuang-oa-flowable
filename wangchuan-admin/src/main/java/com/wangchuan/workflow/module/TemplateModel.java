package com.wangchuan.workflow.module;

import com.wangchuan.workflow.domain.Template;
import lombok.Data;

import java.util.List;

/**
 * <p> 新启模板模型 </p>
 *
 * @Author  wangchuan
 */
@Data
public class TemplateModel {

    /**
     * 模板类型
     */
    private String type;

    /**
     * 模板类型名称
     */
    private String typeName;

    /**
     * 模板列表
     */
    private List<Template> templates;
}
