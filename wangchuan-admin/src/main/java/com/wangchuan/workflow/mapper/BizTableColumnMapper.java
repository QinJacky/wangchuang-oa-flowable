package com.wangchuan.workflow.mapper;

import java.util.List;
import com.wangchuan.workflow.domain.BizTableColumn;

/**
 * 业务表字段Mapper接口
 * 
 * @author oa-generator
 */
public interface BizTableColumnMapper {
    /**
     * 按业务表查询字段
     */
    public List<BizTableColumn> selectBizTableColumnByTableId(String tableId);

    /**
     * 新增字段
     */
    public int insertBizTableColumn(BizTableColumn bizTableColumn);

    /**
     * 删除某业务表全部字段
     */
    public int deleteBizTableColumnByTableId(String tableId);

    /**
     * 批量删除多个业务表的字段
     */
    public int deleteBizTableColumnByTableIds(String[] tableIds);
}
