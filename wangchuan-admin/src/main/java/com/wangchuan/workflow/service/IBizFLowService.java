package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.CommonFlowSubmit;

/**
 * <p> 业务流程接口 </p>
 *
 * @Author  wangchuan
 */
public interface IBizFLowService {

    void submit(CommonFlowSubmit submit);
}
