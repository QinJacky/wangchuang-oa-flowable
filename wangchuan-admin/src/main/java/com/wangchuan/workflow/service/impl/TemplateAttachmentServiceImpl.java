package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.domain.TemplateAttachment;
import com.wangchuan.workflow.mapper.TemplateAttachmentMapper;
import com.wangchuan.workflow.service.ITemplateAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 附件配置Service业务层处理
 * 
 * @Author  wangchuan
 */
@Slf4j
@Service
public class TemplateAttachmentServiceImpl implements ITemplateAttachmentService {
    @Autowired
    private TemplateAttachmentMapper templateAttachmentMapper;

    /**
     * 根据模板ID查询附件配置
     *
     * @param templateId 模板ID
     * @return
     */
    @Override
    public TemplateAttachment getTemplateAttachmentByTemplateId(String templateId) {
        return templateAttachmentMapper.selectTemplateAttachmentByTemplateId(templateId);
    }

    /**
     * 新增附件配置
     * 
     * @param templateAttachment 附件配置
     * @return 结果
     */
    @Override
    public int saveTemplateAttachment(TemplateAttachment templateAttachment) {
        templateAttachment.setId(IdUtils.fastSimpleUUID());
        templateAttachment.setCreateTime(DateUtils.getNowDate());
        return templateAttachmentMapper.insertTemplateAttachment(templateAttachment);
    }
}
