package com.wangchuan.workflow.service;


import com.wangchuan.workflow.domain.WorkflowAttachment;
import com.wangchuan.workflow.module.BizAttachmentDTO;

import java.util.List;

/**
 * 流程业务附件Service接口
 *
 * @Author  wangchuan
 */
public interface IWorkflowAttachmentService {
    /**
     * 新增流程业务附件
     *
     * @param workflowAttachment 流程业务附件
     * @return 结果
     */
    public int saveWorkflowAttachment(WorkflowAttachment workflowAttachment);

    /**
     * 删除附件
     *
     * @param fileId 文件ID
     * @return
     */
    public int remove(String fileId);

    /**
     * 查询流程业务附件列表
     *
     * @param businessId 业务ID
     * @return
     */
    public List<BizAttachmentDTO> listAttachment(String businessId);
}
