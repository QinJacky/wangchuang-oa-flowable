package com.wangchuan.workflow.config;

import org.flowable.common.engine.impl.persistence.deploy.DefaultDeploymentCache;
import org.flowable.engine.impl.db.DbIdGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.job.service.SpringAsyncExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展流程配置
 *
 * @Author  wangchuan
 */
@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
        engineConfiguration.setIdGenerator(new DbIdGenerator());

        //启动二级缓存
        engineConfiguration.setProcessDefinitionCache(new DefaultDeploymentCache(200));
        engineConfiguration.setProcessDefinitionInfoCache(new DefaultDeploymentCache(500));

        //关闭表单字段校验
        engineConfiguration.setFormFieldValidationEnabled(false);

        //开启异步任务执行
        engineConfiguration.setAsyncExecutor(springAsyncExecutor());
        engineConfiguration.setAsyncExecutorActivate(false);

        // 启用表自动创建/更新（首次启动自动建 act_* 流程表）
        engineConfiguration.setDatabaseSchemaUpdate("true");
    }

    @Bean
    public SpringAsyncExecutor springAsyncExecutor() {
        return new SpringAsyncExecutor();
    }
}
