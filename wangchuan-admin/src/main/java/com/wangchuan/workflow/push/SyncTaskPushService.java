package com.wangchuan.workflow.push;

import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.api.ISyncPush;
import com.wangchuan.workflow.consume.AsyncTaskConsumeService;
import com.wangchuan.workflow.domain.AsyncLog;
import com.wangchuan.workflow.enums.AsyncStatusEnum;
import com.wangchuan.workflow.exception.AsyncLogParamCheckException;
import com.wangchuan.workflow.service.IAsyncLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p> 同步任务推送接口实现（本地线程池异步，轻量化替代 RabbitMQ） </p>
 *
 * @Author  wangchuan
 */
@Component
public class SyncTaskPushService implements ISyncPush {

    private static final Logger log = LoggerFactory.getLogger(SyncTaskPushService.class);

    @Autowired
    private IAsyncLogService asyncLogService;
    @Autowired
    private AsyncTaskConsumeService asyncTaskConsumeService;

    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(4, 16, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                r -> new Thread(r, "async-task-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void push(String consumerBeanName, String jsonContent, String queue) {
        push(consumerBeanName, jsonContent, queue, SecurityUtils.getUserId());
    }

    @Override
    public void push(String consumerBeanName, String jsonContent, String queue, String creatorId) {
        checkSubmitParams(consumerBeanName, jsonContent);
        AsyncLog asyncLog = new AsyncLog();
        asyncLog.setId(IdUtils.fastSimpleUUID());
        asyncLog.setBeanName(consumerBeanName);
        asyncLog.setMessageContent(jsonContent);
        asyncLog.setExchangeKey(queue);
        asyncLog.setCreateId(creatorId);
        asyncLog.setStatus(AsyncStatusEnum.WAITING.getCode());
        asyncLogService.saveAsyncLog(asyncLog);
        Runnable consumeTask = () -> {
            try {
                asyncTaskConsumeService.consume(asyncLog);
            } catch (Exception e) {
                log.error("异步任务消费失败, id={}", asyncLog.getId(), e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 事务内推送：等事务提交后再消费，避免消费线程读不到未提交数据
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executor.submit(consumeTask);
                }
            });
        } else {
            executor.submit(consumeTask);
        }
    }

    private void checkSubmitParams(String consumerBeanName, String jsonContent) {
        if (StringUtils.isBlank(consumerBeanName)) {
            throw new AsyncLogParamCheckException("consumerBeanName为空！");
        }
        if (StringUtils.isBlank(jsonContent)) {
            throw new AsyncLogParamCheckException("jsonContent为空！");
        }
    }
}
