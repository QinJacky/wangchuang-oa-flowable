package com.wangchuan.workflow.service;

import java.util.List;
import com.wangchuan.workflow.domain.BizTable;

/**
 * 业务表Service接口
 * 
 * @author oa-generator
 */
public interface IBizTableService {
    /**
     * 查询业务表列表
     */
    public List<BizTable> selectBizTableList(BizTable bizTable);

    /**
     * 查询业务表详细（含字段）
     */
    public BizTable selectBizTableById(String id);

    /**
     * 新增业务表（含字段）
     */
    public int insertBizTable(BizTable bizTable);

    /**
     * 修改业务表（含字段，字段整体替换）
     */
    public int updateBizTable(BizTable bizTable);

    /**
     * 删除业务表（含字段）
     */
    public int deleteBizTableByIds(String[] ids);

    /**
     * 下拉选项
     */
    public List<BizTable> selectBizTableOptionList();

    /**
     * 生成物理表 DDL（预览，不执行）
     */
    public String generateDdl(String id);
}
