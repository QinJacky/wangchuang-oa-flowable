package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.core.domain.entity.SysUser;
import com.wangchuan.common.enums.WhetherStatus;
import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.domain.TemplateDynamicForm;
import com.wangchuan.workflow.mapper.TemplateDynamicFormMapper;
import com.wangchuan.workflow.mapper.TemplateSourceTargetMapper;
import com.wangchuan.workflow.module.FormOption;
import com.wangchuan.workflow.service.ITemplateDynamicFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 动态单Service业务层处理
 * 
 * @author wucorr.com
 */
@Slf4j
@Service
public class TemplateDynamicFormServiceImpl implements ITemplateDynamicFormService {
    @Autowired
    private TemplateDynamicFormMapper templateDynamicFormMapper;

    /**
     * 查询动态单
     * 
     * @param id 动态单主键
     * @return 动态单
     */
    @Override
    public TemplateDynamicForm getTemplateDynamicFormById(String id) {
        return templateDynamicFormMapper.selectTemplateDynamicFormById(id);
    }

    /**
     * 查询动态单列表
     * 
     * @param templateDynamicForm 动态单
     * @return 动态单
     */
    @Override
    public List<TemplateDynamicForm> listTemplateDynamicForm(TemplateDynamicForm templateDynamicForm) {
        templateDynamicForm.setEnableFlag(WhetherStatus.YES.getCode());
        templateDynamicForm.setDelFlag(WhetherStatus.NO.getCode());
        return templateDynamicFormMapper.selectTemplateDynamicFormList(templateDynamicForm);
    }

    /**
     * 新增动态单
     * 
     * @param templateDynamicForm 动态单
     * @return 结果
     */
    @Override
    public int saveTemplateDynamicForm(TemplateDynamicForm templateDynamicForm) {
        templateDynamicForm.setId(IdUtils.fastSimpleUUID());
        templateDynamicForm.setFormKey(IdUtils.fastSimpleUUID());
        String userId = SecurityUtils.getLoginUser().getUserId();
        templateDynamicForm.setCreateId(userId);
        templateDynamicForm.setCreateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        templateDynamicForm.setCreateTime(DateUtils.getNowDate());
        return templateDynamicFormMapper.insertTemplateDynamicForm(templateDynamicForm);
    }

    /**
     * 修改动态单
     * 
     * @param templateDynamicForm 动态单
     * @return 结果
     */
    @Override
    public int updateTemplateDynamicForm(TemplateDynamicForm templateDynamicForm) {
        TemplateDynamicForm newDynamicForm = TemplateSourceTargetMapper.INSTANCE.copyDynamicForm(templateDynamicForm);
        String userId = SecurityUtils.getUserId();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Date now = DateUtils.getNowDate();
        //设置停用状态
        templateDynamicForm.setEnableFlag(WhetherStatus.NO.getCode());
        templateDynamicForm.setUpdateTime(now);
        templateDynamicForm.setUpdateId(userId);
        templateDynamicForm.setUpdateBy(user.getNickName());
        templateDynamicFormMapper.updateTemplateDynamicForm(templateDynamicForm);
        //新增表单
        newDynamicForm.setId(IdUtils.fastSimpleUUID());
        newDynamicForm.setCreateId(userId);
        newDynamicForm.setCreateBy(user.getNickName());
        newDynamicForm.setCreateTime(now);
        return templateDynamicFormMapper.insertTemplateDynamicForm(newDynamicForm);
    }

    /**
     * 批量删除动态单
     * 
     * @param ids 需要删除的动态单主键
     * @return 结果
     */
    @Override
    public int deleteTemplateDynamicFormByIds(String[] ids) {
        return templateDynamicFormMapper.deleteTemplateDynamicFormByIds(ids);
    }

    @Override
    public List<FormOption> getOptionSelect() {
        return templateDynamicFormMapper.selectFormOptionList();
    }
}
