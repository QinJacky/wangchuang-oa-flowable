package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.CommonFlowSubmit;

/**
 * <p> 业务流程接口 </p>
 *
 * @Author  wangchuan
 */
public interface IBizFLowSubmitService{

    String getBizType();

    void buildBizFlowData(CommonFlowSubmit submit);

    void submit(CommonFlowSubmit commFlowSubmit);

    void beforeSubmit(CommonFlowSubmit commFlowSubmit);

    void afterSubmit(CommonFlowSubmit commFlowSubmit);
}
