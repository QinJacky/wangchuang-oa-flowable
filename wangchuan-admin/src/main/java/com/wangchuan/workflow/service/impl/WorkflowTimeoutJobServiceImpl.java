package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.workflow.domain.WorkflowTimeoutJob;
import com.wangchuan.workflow.mapper.WorkflowTimeoutJobMapper;
import com.wangchuan.workflow.service.IWorkflowTimeoutJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 流程任务超时记录Service业务层处理
 * 
 * @Author  wangchuan
 */
@Slf4j
@Service
public class WorkflowTimeoutJobServiceImpl implements IWorkflowTimeoutJobService {
    @Autowired
    private WorkflowTimeoutJobMapper workflowTimeoutJobMapper;

    /**
     * 新增流程任务超时记录
     * 
     * @param workflowTimeoutJob 流程任务超时记录
     * @return 结果
     */
    @Override
    public int saveWorkflowTimeoutJob(WorkflowTimeoutJob workflowTimeoutJob) {
        workflowTimeoutJob.setCreateTime(DateUtils.getNowDate());
        return workflowTimeoutJobMapper.insertWorkflowTimeoutJob(workflowTimeoutJob);
    }

    /**
     * 删除流程任务超时记录信息
     * 
     * @param id 流程任务超时记录主键
     * @return 结果
     */
    @Override
    public int deleteWorkflowTimeoutJobById(String id) {
        return workflowTimeoutJobMapper.deleteWorkflowTimeoutJobById(id);
    }

    /**
     * 查询当前时间之前的超时任务
     *
     * @param currentTime 当前时间
     * @return 结果
     */
    @Override
    public List<WorkflowTimeoutJob> listWorkflowTimeoutJobByCurrentTime(Date currentTime) {
        return workflowTimeoutJobMapper.selectWorkflowTimeoutJobListByCurrentTime(currentTime);
    }
}
