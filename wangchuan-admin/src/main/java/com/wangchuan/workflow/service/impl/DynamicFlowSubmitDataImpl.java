package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.CommonFlowSubmit;
import com.wangchuan.workflow.service.abs.AbstractBizFlowSubmitData;
import com.wangchuan.workflow.api.ISyncPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p> 动态表单数据相关业务实现 </p>
 *
 * @Author  wangchuan
 */
@Slf4j
@Service
public class DynamicFlowSubmitDataImpl extends AbstractBizFlowSubmitData {

    @Autowired
    private ISyncPush syncPush;

    @Override
    public String getBizType() {
        return "dynamic";
    }

    @Override
    public void buildBizFlowData(CommonFlowSubmit submit) {
        log.info("动态表单提交");
    }

    @Override
    public void beforeSubmit(CommonFlowSubmit commFlowSubmit) {
        log.info("动态表单提交前处理");
    }

    @Override
    public void afterSubmit(CommonFlowSubmit commFlowSubmit) {
        log.info("动态表单提交后处理");
    }
}
