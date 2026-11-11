package com.wangchuan.workflow.async;

import com.alibaba.fastjson2.JSONObject;
import com.wangchuan.common.exception.base.BaseException;
import com.wangchuan.workflow.domain.AsyncLog;
import com.wangchuan.workflow.execute.IAsyncHandler;
import com.wangchuan.workflow.domain.Todo;
import com.wangchuan.workflow.domain.vo.TodoVO;
import com.wangchuan.workflow.mapper.TodoSourceTargetMapper;
import com.wangchuan.workflow.service.ITodoService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * <p> 新启待办异步处理服务 </p>
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class TodoAsyncService implements IAsyncHandler {

    @Autowired
    private ITodoService todoService;
    @Autowired
    private RuntimeService runtimeService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAsync(AsyncLog asyncLog) {
        log.info("收到消息，消息内容：{}", asyncLog);
        String msg = asyncLog.getMessageContent();
        Assert.hasText(msg, "消息内容为空");
        try {
            TodoVO todoVO = JSONObject.parseObject(msg, TodoVO.class);
            Todo todo = TodoSourceTargetMapper.INSTANCE.todoVo2Todo(todoVO);
            todoService.saveAndUpdateTodo(todo);
            runtimeService.updateBusinessKey(todo.getProcInstId(), todo.getBusinessId());
        } catch (Exception e) {
            log.error("异步处理待办失败，原因：", e);
            throw new BaseException("异步处理待办失败:" + e.getMessage());
        }
    }
}
