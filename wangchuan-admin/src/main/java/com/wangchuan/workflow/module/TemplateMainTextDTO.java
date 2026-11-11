package com.wangchuan.workflow.module;

import com.wangchuan.workflow.domain.TemplateMainText;
import lombok.Data;

/**
 * @Author  wangchuan
 */
@Data
public class TemplateMainTextDTO extends TemplateMainText {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String extendName;
}
