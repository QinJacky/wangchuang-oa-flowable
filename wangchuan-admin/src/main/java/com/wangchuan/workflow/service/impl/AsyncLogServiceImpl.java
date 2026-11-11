package com.wangchuan.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.StringUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.consume.AsyncTaskConsumeService;
import com.wangchuan.workflow.domain.AsyncLog;
import com.wangchuan.workflow.enums.AsyncStatusEnum;
import com.wangchuan.workflow.mapper.AsyncLogMapper;
import com.wangchuan.workflow.service.IAsyncLogService;
import com.wangchuan.admin.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 异步任务日志记录Service业务层处理
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class AsyncLogServiceImpl implements IAsyncLogService {

    @Autowired
    private AsyncLogMapper asyncLogMapper;
    @Autowired
    private AsyncTaskConsumeService asyncTaskConsumeService;
    @Autowired
    private RedisLock redisLock;

    @Value("${server.servlet.context-path}")
    private String apiPrefix;

    @Override
    public AsyncLog getAsyncLogById(String id) {
        return asyncLogMapper.selectAsyncLogById(id);
    }

    @Override
    public List<AsyncLog> listAsyncLog(AsyncLog asyncLog) {
        return asyncLogMapper.selectAsyncLogList(asyncLog);
    }

    @Override
    public int saveAsyncLog(AsyncLog asyncLog) {
        asyncLog.setCreateTime(DateUtils.getNowDate());
        return asyncLogMapper.insertAsyncLog(asyncLog);
    }

    @Override
    public int updateAsyncLog(AsyncLog asyncLog) {
        asyncLog.setUpdateTime(DateUtils.getNowDate());
        return asyncLogMapper.updateAsyncLog(asyncLog);
    }

    @Override
    public int deleteAsyncLogByIds(String[] ids) {
        return asyncLogMapper.deleteAsyncLogByIds(ids);
    }

    @Override
    public int saveBatchAsyncLog(List<AsyncLog> asyncLogs) {
        return asyncLogMapper.batchInsertAsyncLog(asyncLogs);
    }

    @Override
    public void retry(String id) {
        AsyncLog asyncLog = asyncLogMapper.selectAsyncLogById(id);
        if (asyncLog == null) {
            throw new RuntimeException("异步日志不存在");
        }
        try {
            redisLock.doLock(asyncLog.getId(), () -> {
                asyncTaskConsumeService.consume(asyncLog);
                asyncLogMapper.updateAsyncLogRetryTimeById(asyncLog.getId());
            });
        } catch (Exception e) {
            log.error("异步任务重试失败", e);
            throw new RuntimeException("异步任务重试失败");
        }
    }

    public String addLogMessage(String exchangeName, String routingKey, Object message) {
        AsyncLog asyncLog = new AsyncLog();
        String logId = IdUtils.fastSimpleUUID();
        asyncLog.setId(logId);
        if (StringUtils.isNotBlank(asyncLog.getApiPrefix())) {
            asyncLog.setApiPrefix(apiPrefix.replace("/", ""));
        }
        asyncLog.setExchangeKey(exchangeName);
        asyncLog.setRoutingKey(routingKey);
        asyncLog.setMessageContent(JSON.toJSONString(message));
        asyncLog.setStatus(AsyncStatusEnum.WAITING.getCode());
        asyncLog.setCreateId(SecurityUtils.getUserId());
        asyncLog.setCreateTime(DateUtils.getNowDate());
        asyncLogMapper.insertAsyncLog(asyncLog);
        return logId;
    }
}
