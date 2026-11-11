package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.Form;

/**
 * 流程表单Service接口
 * 
 * @Author  wangchuan
 */
public interface IFormService {
    /**
     * 查询流程表单
     * 
     * @param id 流程表单主键
     * @return 流程表单
     */
    public Form getFormById(String id);

    /**
     * 新增流程表单
     * 
     * @param form 流程表单
     * @return 结果
     */
    public String saveForm(Form form);

    /**
     * 修改流程表单
     * 
     * @param form 流程表单
     * @return 结果
     */
    public int updateForm(Form form);

    /**
     * 回写流程实例ID与状态（提交启动流程后调用）
     *
     * @param id            表单主键
     * @param procInstId    流程实例ID
     * @param processStatus 流程状态
     */
    void updateProcessInfo(String id, String procInstId, String processStatus);

    /**
     * 查询动态表单
     *
     * @param templateId 流程模板ID
     * @return String
     */
    String dynamicFormData(String templateId);
}
