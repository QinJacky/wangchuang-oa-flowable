package com.wangchuan.workflow.controller;

import com.wangchuan.common.core.controller.BaseController;
import com.wangchuan.common.core.domain.AjaxResult;
import com.wangchuan.workflow.module.MainStampParam;
import com.wangchuan.workflow.module.MainTextParam;
import com.wangchuan.workflow.service.IWorkflowMainTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 正文Controller
 *
 * @Author  wangchuan
 */
@RestController
@RequestMapping("/workfile/main")
public class WorkflowMainTextController extends BaseController {
    @Autowired
    private IWorkflowMainTextService workflowMainTextService;

    /**
     * 获取正文信息
     */
    @GetMapping(value = "/getMainText")
    public AjaxResult getMainInfo(MainTextParam param) {
        return success(workflowMainTextService.getMainInfo(param));
    }

    /**
     * 上传正文
     * @param mainTextParam
     * @return
     */
    @PostMapping(value = "/uploadMainText")
    public AjaxResult uploadMainText(@RequestBody MainTextParam mainTextParam) {
        workflowMainTextService.uploadMainText(mainTextParam);
        return AjaxResult.success();
    }

    /**
     * 删除正文（用户上传方式的正文）
     * @param businessId
     * @return
     */
    @DeleteMapping("/remove/{businessId}")
    public AjaxResult remove(@PathVariable String businessId) {
        return toAjax(workflowMainTextService.removeMainText(businessId));
    }

    /**
     * 正文盖章
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/stamp")
    public AjaxResult stamp(@RequestBody MainStampParam param) {
        return AjaxResult.success(workflowMainTextService.stamp(param));
    }

    /**
     * 还原印章
     *
     * @param businessId
     * @return
     */
    @PutMapping(value = "/restoreSeal/{businessId}")
    public AjaxResult restoreSeal(@PathVariable("businessId") String businessId) {
        return AjaxResult.success("操作成功", workflowMainTextService.restoreSeal(businessId));
    }

}
