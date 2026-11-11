package com.wangchuan.workflow.controller;

import com.wangchuan.common.annotation.Log;
import com.wangchuan.common.core.controller.BaseController;
import com.wangchuan.common.core.domain.AjaxResult;
import com.wangchuan.common.core.page.TableDataInfo;
import com.wangchuan.common.enums.BusinessType;
import com.wangchuan.common.enums.WhetherStatus;
import com.wangchuan.workflow.domain.BizTable;
import com.wangchuan.workflow.service.IBizTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业务表管理Controller
 * 
 * @author oa-generator
 */
@RestController
@RequestMapping("/biz/table")
public class BizTableController extends BaseController {
    @Autowired
    private IBizTableService bizTableService;

    /**
     * 查询业务表列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizTable bizTable) {
        startPage();
        bizTable.setDelFlag(WhetherStatus.NO.getCode());
        List<BizTable> list = bizTableService.selectBizTableList(bizTable);
        return getDataTable(list);
    }

    /**
     * 获取业务表详细
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(bizTableService.selectBizTableById(id));
    }

    /**
     * 新增业务表
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:add')")
    @Log(title = "业务表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizTable bizTable) {
        return toAjax(bizTableService.insertBizTable(bizTable));
    }

    /**
     * 修改业务表
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:edit')")
    @Log(title = "业务表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizTable bizTable) {
        return toAjax(bizTableService.updateBizTable(bizTable));
    }

    /**
     * 删除业务表
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:remove')")
    @Log(title = "业务表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(bizTableService.deleteBizTableByIds(ids));
    }

    /**
     * 下拉选项（供表单设计器读取）
     */
    @GetMapping("/optionSelect")
    public AjaxResult optionSelect() {
        return AjaxResult.success(bizTableService.selectBizTableOptionList());
    }

    /**
     * 生成物理表 DDL（预览，不执行）
     */
    @PreAuthorize("@ss.hasPermi('workflow:bizTable:query')")
    @GetMapping("/generateDdl/{id}")
    public AjaxResult generateDdl(@PathVariable("id") String id) {
        return AjaxResult.success(bizTableService.generateDdl(id));
    }
}
