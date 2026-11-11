package com.wangchuan.admin.utils;

import org.quartz.JobExecutionContext;
import com.wangchuan.admin.domain.SysJob;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author wangchuan
 *
 */
public class QuartzJobExecution extends AbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
