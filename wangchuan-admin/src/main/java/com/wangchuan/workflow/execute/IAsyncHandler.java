package com.wangchuan.workflow.execute;

import com.wangchuan.workflow.domain.AsyncLog;

/**
 * <p> 异步处理器 </p>
 *
 * @Author  wangchuan
 */
public interface IAsyncHandler {

    /**
     * 执行异步方法
     *
     * @param asyncLog 异步日志
     */
    void doAsync(AsyncLog asyncLog);
}
