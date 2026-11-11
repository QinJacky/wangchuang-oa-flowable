package com.wangchuan.workflow.exception;

/**
 * <p> 异步日志参数校验异常 </p>
 *
 * @Author  wangchuan
 */
public class AsyncLogParamCheckException extends RuntimeException{
    public AsyncLogParamCheckException(String message) {
        super(message);
    }
}
