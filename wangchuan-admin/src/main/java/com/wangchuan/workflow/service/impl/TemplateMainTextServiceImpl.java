package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.StringUtils;
import com.wangchuan.workflow.domain.TemplateMainText;
import com.wangchuan.workflow.mapper.TemplateMainTextMapper;
import com.wangchuan.workflow.service.ITemplateMainTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 正文配置Service业务层处理
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class TemplateMainTextServiceImpl implements ITemplateMainTextService {
    @Autowired
    private TemplateMainTextMapper templateMainTextMapper;

    /**
     * 根据模板id查询正文配置
     *
     * @param templateId
     * @return
     */
    @Override
    public TemplateMainText getByTemplateId(String templateId) {
        if (StringUtils.isBlank(templateId)) {
            return null;
        }
        return templateMainTextMapper.selectByTemplateId(templateId);
    }

    /**
     * 新增正文配置
     *
     * @param templateMainText 正文配置
     * @return 结果
     */
    @Override
    public int saveTemplateMainText(TemplateMainText templateMainText) {
        templateMainText.setCreateTime(DateUtils.getNowDate());
        return templateMainTextMapper.insertTemplateMainText(templateMainText);
    }
}
