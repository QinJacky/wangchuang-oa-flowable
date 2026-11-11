package com.wangchuan.workflow.consume;

import com.alibaba.fastjson2.JSONObject;
import com.wangchuan.common.core.redis.RedisCache;
import com.wangchuan.common.utils.StringUtils;
import com.wangchuan.workflow.execute.IAsyncHandler;
import com.wangchuan.workflow.domain.AsyncLog;
import com.wangchuan.workflow.enums.AsyncStatusEnum;
import com.wangchuan.workflow.service.IAsyncLogService;
import com.wangchuan.admin.lock.RedisLock;
import com.wangchuan.common.bean.ApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p> 消费服务（本地线程池驱动，轻量化替代 RabbitMQ） </p>
 *
 * @Author  wangchuan
 */
@Slf4j
@Component
public class AsyncTaskConsumeService {

    @Resource
    private RedisLock redisLock;
    @Resource
    private RedisCache redisCache;
    @Autowired
    private IAsyncLogService asyncLogService;

    private static final String ASYNC_LOCK_KEY = "async:lock:";
    private static final String ASYNC_LOG_KEY = "async:log:";

    /**
     * 执行Spring容器bean的目标方法
     *
     * @param asyncLog
     */
    public void consume(AsyncLog asyncLog) {
        if (StringUtils.isBlank(asyncLog.getBeanName())) {
            log.error("未指定执行异步任务的bean!");
        } else {
            redisLock.doLock(ASYNC_LOCK_KEY + asyncLog.getId(), () -> {
                IAsyncHandler handler = null;
                try {
                    handler = ApplicationContextHelper.getBean(asyncLog.getBeanName(), IAsyncHandler.class);
                } catch (Exception ex) {
                    log.error("异步消息处理异常，无法获取注册的bean：{}", ex);
                }

                if (null != handler && redisCache.getCacheObject(ASYNC_LOG_KEY + asyncLog.getId()) == null) {
                    // 幂等校验：已成功消费过的消息直接跳过；查不到说明推送事务尚未提交，正常执行
                    AsyncLog asyncLogById = asyncLogService.getAsyncLogById(asyncLog.getId());
                    if (asyncLogById != null && AsyncStatusEnum.SUCCESS.getCode().equals(asyncLogById.getStatus())) {
                        return;
                    }
                    try {
                        handler.doAsync(asyncLog);
                        asyncLog.setStatus(AsyncStatusEnum.SUCCESS.getCode());
                        asyncLogService.updateAsyncLog(asyncLog);
                        redisCache.setCacheObject(ASYNC_LOG_KEY + asyncLog.getId(), asyncLog, 5, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        asyncLog.setStatus(AsyncStatusEnum.FAIL.getCode());
                        if (e.getMessage() != null) {
                            if (e.getMessage().length() > 500) {
                                asyncLog.setFailReason(e.getMessage().substring(0, 500));
                            } else {
                                asyncLog.setFailReason(e.getMessage());
                            }
                        }
                        asyncLogService.updateAsyncLog(asyncLog);
                        log.error("消费异步消息异常：msg={}, exception={}", JSONObject.toJSONString(asyncLog), e);
                    }
                }
            });
        }
    }
}
