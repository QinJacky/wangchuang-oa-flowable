package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.TemplateAttachment;

/**
 * 附件配置Service接口
 * 
 * @Author  wangchuan
 */
public interface ITemplateAttachmentService {

    /**
     * 查询附件配置
     * @param templateId
     * @return
     */
    public TemplateAttachment getTemplateAttachmentByTemplateId(String templateId);

    /**
     * 新增附件配置
     * 
     * @param templateAttachment 附件配置
     * @return 结果
     */
    public int saveTemplateAttachment(TemplateAttachment templateAttachment);
}
