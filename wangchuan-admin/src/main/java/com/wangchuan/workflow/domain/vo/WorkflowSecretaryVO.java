package com.wangchuan.workflow.domain.vo;

import com.wangchuan.common.core.domain.entity.SysUser;
import com.wangchuan.workflow.domain.WorkflowSecretary;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程办理委托对象
 *
 * @Author  wangchuan
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkflowSecretaryVO extends WorkflowSecretary {
    private static final long serialVersionUID = 1L;

    /**
     * 领导
     */
    private SysUser leader;

    /**
     * 秘书
     */
    private SysUser secretary;
}
