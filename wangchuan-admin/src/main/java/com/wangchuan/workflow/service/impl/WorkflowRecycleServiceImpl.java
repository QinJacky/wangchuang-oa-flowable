package com.wangchuan.workflow.service.impl;

import java.util.List;
import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.wangchuan.workflow.mapper.WorkflowRecycleMapper;
import com.wangchuan.workflow.domain.WorkflowRecycle;
import com.wangchuan.workflow.service.IWorkflowRecycleService;

/**
 * 回收站Service业务层处理
 * 
 * @Author  wangchuan
 * @date 2025-04-03
 */
@Slf4j
@Service
public class WorkflowRecycleServiceImpl implements IWorkflowRecycleService {
    @Autowired
    private WorkflowRecycleMapper workflowRecycleMapper;

    /**
     * 查询回收站
     * 
     * @param id 回收站主键
     * @return 回收站
     */
    @Override
    public WorkflowRecycle getWorkflowRecycleById(String id) {
        return workflowRecycleMapper.selectWorkflowRecycleById(id);
    }

    /**
     * 查询回收站列表
     * 
     * @param workflowRecycle 回收站
     * @return 回收站
     */
    @Override
    public List<WorkflowRecycle> listWorkflowRecycle(WorkflowRecycle workflowRecycle) {
        workflowRecycle.setCreateId(SecurityUtils.getUserId());
        return workflowRecycleMapper.selectWorkflowRecycleList(workflowRecycle);
    }

    /**
     * 新增回收站
     * 
     * @param workflowRecycle 回收站
     * @return 结果
     */
    @Override
    public int saveWorkflowRecycle(WorkflowRecycle workflowRecycle) {
        workflowRecycle.setId(IdUtils.fastSimpleUUID());
        workflowRecycle.setCreateId(SecurityUtils.getUserId());
        workflowRecycle.setCreateTime(DateUtils.getNowDate());
        return workflowRecycleMapper.insertWorkflowRecycle(workflowRecycle);
    }

    /**
     * 修改回收站
     * 
     * @param workflowRecycle 回收站
     * @return 结果
     */
    @Override
    public int updateWorkflowRecycle(WorkflowRecycle workflowRecycle) {
        return workflowRecycleMapper.updateWorkflowRecycle(workflowRecycle);
    }

    /**
     * 批量删除回收站
     * 
     * @param ids 需要删除的回收站主键
     * @return 结果
     */
    @Override
    public int deleteWorkflowRecycleByIds(String[] ids) {
        return workflowRecycleMapper.deleteWorkflowRecycleByIds(ids);
    }

    /**
     * 删除回收站信息
     * 
     * @param id 回收站主键
     * @return 结果
     */
    @Override
    public int deleteWorkflowRecycleById(String id) {
        return workflowRecycleMapper.deleteWorkflowRecycleById(id);
    }
}
