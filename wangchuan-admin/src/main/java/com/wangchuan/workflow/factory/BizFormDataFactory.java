package com.wangchuan.workflow.factory;

import com.wangchuan.workflow.service.IBizFormDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 业务表单数据工厂 </p>
 *
 * @Author  wangchuan
 */
@Component
public class BizFormDataFactory {

    private final Map<String, IBizFormDataService> bizFormDataMap = new ConcurrentHashMap<>();

    @Autowired
    private Map<String, IBizFormDataService> bizFormDataServiceMap;

    @PostConstruct
    public void init() {
        //从Spring容器获取所有的IBizFormDataService实现类
        bizFormDataServiceMap.values().forEach(iBizFormDataService ->
            bizFormDataMap.put(iBizFormDataService.getBizType(), iBizFormDataService));
    }

    public IBizFormDataService getBizFormDataImplByType(String bizType) {
        return bizFormDataMap.get(bizType);
    }
}