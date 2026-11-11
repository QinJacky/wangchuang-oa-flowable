package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.WorkflowSecretaryRecord;
import com.wangchuan.workflow.mapper.WorkflowSecretaryRecordMapper;
import com.wangchuan.workflow.service.IWorkflowSecretaryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程办理秘书记录Service业务层处理
 * 
 * @Author  wangchuan
 */
@Slf4j
@Service
public class WorkflowSecretaryRecordServiceImpl implements IWorkflowSecretaryRecordService {
    @Autowired
    private WorkflowSecretaryRecordMapper workflowSecretaryRecordMapper;

    /**
     * 查询流程办理秘书记录列表
     * 
     * @param workflowSecretaryRecord 流程办理秘书记录
     * @return 流程办理秘书记录
     */
    @Override
    public List<WorkflowSecretaryRecord> listWorkflowSecretaryRecord(WorkflowSecretaryRecord workflowSecretaryRecord) {
        return workflowSecretaryRecordMapper.selectWorkflowSecretaryRecordList(workflowSecretaryRecord);
    }

    /**
     * 批量插入秘书记录
     *
     * @param secretaryRecords 秘书记录
     */
    @Override
    public void saveBatchSecretaryRecordList(List<WorkflowSecretaryRecord> secretaryRecords) {
        workflowSecretaryRecordMapper.batchInsertWorkflowSecretaryRecord(secretaryRecords);
    }
}
