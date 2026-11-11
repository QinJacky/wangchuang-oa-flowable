package com.wangchuan.workflow.factory;

import com.wangchuan.workflow.service.IBizButtonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 业务按钮数据工厂 </p>
 *
 * @Author  wangchuan
 */
@Component
public class BizButtonDataFactory {

    private final Map<String, IBizButtonDataService> bizButtonDataMap = new ConcurrentHashMap<>();

    @Autowired
    private Map<String, IBizButtonDataService> bizButtonDataServiceMap;

    @PostConstruct
    public void init() {
        //从Spring容器获取所有的IBizButtonDataService实现类
        bizButtonDataServiceMap.values().forEach(iBizButtonDataService ->
            bizButtonDataMap.put(iBizButtonDataService.getCode(), iBizButtonDataService));
    }

    public Map<String, IBizButtonDataService> getBizButtonDataMap() {
        return bizButtonDataMap;
    }
}