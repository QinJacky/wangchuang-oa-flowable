package com.wangchuan.workflow.controller;


import com.wangchuan.common.core.controller.BaseController;
import com.wangchuan.common.core.domain.AjaxResult;
import com.wangchuan.workflow.service.IFlowInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>工作流流程实例管理<p>
 *
 * @Author  wangchuan
 */
@Slf4j
@RestController
@RequestMapping("/flowable/instance")
public class FlowInstanceController extends BaseController {

    @Autowired
    private IFlowInstanceService flowInstanceService;

    /**
     * 根据流程定义id启动流程实例
     * @param procDefId
     * @param variables
     * @return
     */
    @PostMapping("/startBy/{procDefId}")
    public AjaxResult startById(@PathVariable(value = "procDefId") String procDefId, @RequestBody Map<String, Object> variables) {
        flowInstanceService.startProcessInstanceById(procDefId, variables);
        return AjaxResult.success();
    }

    /**
     * 激活或挂起流程实例
     * @param state
     * @param instanceId
     * @return
     */
    @PostMapping(value = "/updateState")
    public AjaxResult updateState(@RequestParam Integer state, @RequestParam String instanceId) {
        flowInstanceService.updateState(state, instanceId);
        return AjaxResult.success();
    }

    /**
     * 删除流程实例
     * @param instanceIds
     * @param deleteReason
     * @return
     */
    @DeleteMapping(value = "/delete/{instanceIds}")
    public AjaxResult delete(@PathVariable String[] instanceIds, @RequestParam(required = false) String deleteReason) {
        for (String instanceId : instanceIds) {
            flowInstanceService.delete(instanceId, deleteReason);
        }
        return AjaxResult.success();
    }
}