package com.wangchuan.workflow.factory;

import com.wangchuan.workflow.service.IBizFLowSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 业务按钮工厂 </p>
 *
 * @Author  wangchuan
 */
@Component
public class BizFlowSubmitFactory {

    private final Map<String, IBizFLowSubmitService> bizFlowSubmitMap = new ConcurrentHashMap<>();

    @Autowired
    private Map<String, IBizFLowSubmitService> bizFLowSubmitServiceMap;

    @PostConstruct
    public void init() {
        //从Spring容器获取所有的IBizFLowSubmitService实现类
        bizFLowSubmitServiceMap.values().forEach(iBizFLowSubmitService ->
            bizFlowSubmitMap.put(iBizFLowSubmitService.getBizType(), iBizFLowSubmitService));
    }

    public IBizFLowSubmitService getBizFLowSubmitImplByType(String bizType) {
        return bizFlowSubmitMap.get(bizType);
    }
}