package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.domain.Template;
import com.wangchuan.workflow.domain.TemplateDynamicForm;
import com.wangchuan.workflow.service.ITemplateDynamicFormService;
import com.wangchuan.workflow.service.ITemplateService;
import com.wangchuan.workflow.domain.Form;
import com.wangchuan.workflow.enums.ProcessStatusEnum;
import com.wangchuan.workflow.mapper.FormMapper;
import com.wangchuan.workflow.service.IFormService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 流程表单Service业务层处理
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class FormServiceImpl implements IFormService {
    @Autowired
    private FormMapper formMapper;
    @Autowired
    private ITemplateService templateService;
    @Autowired
    private ITemplateDynamicFormService dynamicFormService;

    /**
     * 查询流程表单
     *
     * @param id 流程表单主键
     * @return 流程表单
     */
    @Override
    public Form getFormById(String id) {
        return formMapper.selectFormById(id);
    }


    /**
     * 新增流程表单
     *
     * @param form 流程表单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveForm(Form form) {
        String id = IdUtils.fastSimpleUUID();
        form.setId(id);
        form.setCreateId(SecurityUtils.getUserId());
        form.setCreateTime(DateUtils.getNowDate());
        // 草稿：未关联流程实例标记 DRAFT；已关联标记 RUNNING
        form.setProcessStatus(StringUtils.isBlank(form.getProcInstId())
                ? ProcessStatusEnum.DRAFT.getCode() : ProcessStatusEnum.RUNNING.getCode());
        formMapper.insertForm(form);
        return id;
    }

    /**
     * 提交启动流程后回写流程实例ID与状态
     *
     * @param id            表单主键
     * @param procInstId    流程实例ID
     * @param processStatus 流程状态
     */
    @Override
    public void updateProcessInfo(String id, String procInstId, String processStatus) {
        Form form = new Form();
        form.setId(id);
        form.setProcInstId(procInstId);
        form.setProcessStatus(processStatus);
        form.setUpdateId(SecurityUtils.getUserId());
        form.setUpdateTime(DateUtils.getNowDate());
        formMapper.updateProcessInfo(form);
    }

    /**
     * 修改流程表单
     *
     * @param form 流程表单
     * @return 结果
     */
    @Override
    public int updateForm(Form form) {
        form.setUpdateId(SecurityUtils.getLoginUser().getUserId());
        form.setUpdateTime(DateUtils.getNowDate());
        return formMapper.updateForm(form);
    }

    /**
     * 流程初始化表单
     *
     * @param templateId 模板ID
     * @return JSONObject 表单JSON对象
     */
    @Override
    public String dynamicFormData(String templateId) {
        // 第一次申请获取初始化表单
        if (StringUtils.isNotBlank(templateId)) {
            Template template = templateService.getTemplateById(templateId);
            if (Objects.isNull(template)) {
                throw new RuntimeException("模板为空!");
            }
            TemplateDynamicForm dynamicForm = dynamicFormService.getTemplateDynamicFormById(template.getFormId());
            return dynamicForm.getContent();
        } else {
            throw new RuntimeException("参数错误!");
        }
    }
}
