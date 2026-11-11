package com.wangchuan.workflow.mapper;

import java.util.List;
import com.wangchuan.workflow.domain.BizTable;

/**
 * 业务表Mapper接口
 * 
 * @author oa-generator
 */
public interface BizTableMapper {
    /**
     * 查询业务表列表
     */
    public List<BizTable> selectBizTableList(BizTable bizTable);

    /**
     * 查询业务表详细
     */
    public BizTable selectBizTableById(String id);

    /**
     * 新增业务表
     */
    public int insertBizTable(BizTable bizTable);

    /**
     * 修改业务表
     */
    public int updateBizTable(BizTable bizTable);

    /**
     * 删除业务表（逻辑）
     */
    public int deleteBizTableById(String id);

    /**
     * 批量删除业务表（逻辑）
     */
    public int deleteBizTableByIds(String[] ids);

    /**
     * 下拉选项（表单设计器读取）
     */
    public List<BizTable> selectBizTableOptionList();
}
