package com.wangchuan.workflow.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wangchuan.common.annotation.Log;
import com.wangchuan.common.core.controller.BaseController;
import com.wangchuan.common.core.domain.AjaxResult;
import com.wangchuan.common.enums.BusinessType;
import com.wangchuan.workflow.domain.TemplateType;
import com.wangchuan.workflow.service.ITemplateTypeService;
import com.wangchuan.common.utils.poi.ExcelUtil;
import com.wangchuan.common.core.page.TableDataInfo;

/**
 * 模板分类Controller
 * 
 * @Author  wangchuan
 * @date 2025-08-07
 */
@RestController
@RequestMapping("/template/type")
public class TemplateTypeController extends BaseController {
    @Autowired
    private ITemplateTypeService templateTypeService;

    /**
     * 查询模板分类列表
     */
    @PreAuthorize("@ss.hasPermi('template:type:list')")
    @GetMapping("/list")
    public TableDataInfo list(TemplateType templateType) {
        startPage();
        List<TemplateType> list = templateTypeService.listTemplateType(templateType);
        return getDataTable(list);
    }

    /**
     * 查询所有启用的模板分类
     * @return
     */
    @GetMapping("/listEnable")
    public AjaxResult list() {
        return AjaxResult.success(templateTypeService.listAllEnabledTemplateType());
    }

    /**
     * 导出模板分类列表
     */
    @PreAuthorize("@ss.hasPermi('template:type:export')")
    @Log(title = "模板分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TemplateType templateType) {
        List<TemplateType> list = templateTypeService.listTemplateType(templateType);
        ExcelUtil<TemplateType> util = new ExcelUtil<TemplateType>(TemplateType.class);
        util.exportExcel(response, list, "模板分类数据");
    }

    /**
     * 获取模板分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('template:type:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(templateTypeService.getTemplateTypeById(id));
    }

    /**
     * 新增模板分类
     */
    @PreAuthorize("@ss.hasPermi('template:type:add')")
    @Log(title = "模板分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TemplateType templateType) {
        return toAjax(templateTypeService.saveTemplateType(templateType));
    }

    /**
     * 修改模板分类
     */
    @PreAuthorize("@ss.hasPermi('template:type:edit')")
    @Log(title = "模板分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TemplateType templateType) {
        return toAjax(templateTypeService.updateTemplateType(templateType));
    }

    /**
     * 删除模板分类
     */
    @PreAuthorize("@ss.hasPermi('template:type:remove')")
    @Log(title = "模板分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(templateTypeService.deleteTemplateTypeByIds(ids));
    }

    /**
     * 启用禁用
     * @param templateType
     * @return
     */
    @PutMapping("/changeEnableFlag")
    public AjaxResult changeEnableFlag(@RequestBody TemplateType templateType) {
        return toAjax(templateTypeService.changeEnableFlag(templateType));
    }
}
