package com.wangchuan.workflow.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.UserTask;

/**
 * BPMN 扩展元素读取工具
 * <p>
 * 流程设计器把自定义属性（userType/dataType/expId）写入 UserTask 的
 * &lt;bpmn:extensionElements&gt; 下（如 &lt;flowable:userType&gt;），
 * Flowable 部署重排 XML 时会原样保留这些扩展元素，因此运行时需从此处读取，
 * 而非从 flowable: 命名空间属性读取（自定义属性会被 Flowable 丢弃）。
 * </p>
 */
public class ExtensionUtil {

    /**
     * 读取 UserTask 扩展元素内指定名称的子元素文本
     *
     * @param userTask 用户任务
     * @param name     扩展元素 localName，如 userType / dataType / expId
     * @return 元素文本；不存在返回 null
     */
    public static String getExtValue(UserTask userTask, String name) {
        if (userTask == null) {
            return null;
        }
        Map<String, List<ExtensionElement>> exts = userTask.getExtensionElements();
        if (exts != null && exts.containsKey(name)) {
            List<ExtensionElement> list = exts.get(name);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0).getElementText();
            }
        }
        return null;
    }
}
