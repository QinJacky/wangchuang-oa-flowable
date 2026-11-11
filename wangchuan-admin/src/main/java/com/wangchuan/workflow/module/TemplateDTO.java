package com.wangchuan.workflow.module;

import com.wangchuan.workflow.domain.Template;
import com.wangchuan.workflow.domain.TemplateAttachment;
import com.wangchuan.workflow.domain.TemplateDynamicForm;
import lombok.Data;


/**
 * @Author  wangchuan
 */
@Data
public class TemplateDTO extends Template {

    /**
     * 附件信息
     */
    private TemplateAttachment attachment;

    /**
     * 动态表单信息
     */
    private TemplateDynamicForm dynamicForm;

    /**
     * 正文信息
     */
    private TemplateMainTextDTO mainText;

    /**
     * 消息通知
     */
    private TemplateMessageNoticeDTO messageNotice;
}
