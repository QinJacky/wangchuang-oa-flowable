package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.workflow.domain.TemplateMessageNotice;
import com.wangchuan.workflow.mapper.TemplateMessageNoticeMapper;
import com.wangchuan.workflow.service.ITemplateMessageNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息通知模板Service业务层处理
 * 
 * @Author  wangchuan
 */
@Slf4j
@Service
public class TemplateMessageNoticeServiceImpl implements ITemplateMessageNoticeService {
    @Autowired
    private TemplateMessageNoticeMapper templateMessageNoticeMapper;

    /**
     * 新增消息通知模板
     * 
     * @param templateMessageNotice 消息通知模板
     * @return 结果
     */
    @Override
    public int saveTemplateMessageNotice(TemplateMessageNotice templateMessageNotice) {
        templateMessageNotice.setCreateTime(DateUtils.getNowDate());
        return templateMessageNoticeMapper.insertTemplateMessageNotice(templateMessageNotice);
    }

    /**
     * 根据模板id查询消息通知模板
     *
     * @param templateId
     * @return 结果
     */
    @Override
    public TemplateMessageNotice getByTemplateId(String templateId) {
        return templateMessageNoticeMapper.selectByTemplateId(templateId);
    }
}
