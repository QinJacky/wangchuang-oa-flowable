package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.WorkflowEntrustTemplate;
import com.wangchuan.workflow.mapper.WorkflowEntrustTemplateMapper;
import com.wangchuan.workflow.service.IWorkflowEntrustTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 委托关联模板Service业务层处理
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class WorkflowEntrustTemplateServiceImpl implements IWorkflowEntrustTemplateService {
    @Autowired
    private WorkflowEntrustTemplateMapper workflowEntrustTemplateMapper;

    /**
     * 查询委托关系模板列表
     *
     * @param entrustIds 委托关系ID集合
     * @return 结果
     */
    @Override
    public List<WorkflowEntrustTemplate> listByEntrustIds(List<String> entrustIds) {
        return workflowEntrustTemplateMapper.selectListByEntrustIds(entrustIds);
    }

    /**
     * 批量插入委托关系模板
     *
     * @param templateList 模板列表
     */
    @Override
    public void saveBatch(List<WorkflowEntrustTemplate> templateList) {
        workflowEntrustTemplateMapper.batchInsert(templateList);
    }

    /**
     * 根据委托关系ID批量更新删除标记
     *
     * @param entrustIds 需要更新的委托关系ID
     * @return
     */
    @Override
    public int updateDelFlagByEntrustIds(String[] entrustIds) {
        return workflowEntrustTemplateMapper.updateDelFlagByEntrustIds(entrustIds);
    }
}
