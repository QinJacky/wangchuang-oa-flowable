package com.wangchuan.workflow.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.wangchuan.common.annotation.Excel;
import com.wangchuan.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 业务表对象 t_biz_table
 * 
 * @author oa-generator
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizTable extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 物理表名，如 oa_leave */
    @Excel(name = "表名")
    private String tableName;

    /** 中文说明 */
    @Excel(name = "中文说明")
    private String comment;

    /** 状态（0停用 1启用） */
    private String status;

    /** 删除标识（0未删 1已删） */
    private String delFlag;

    /** 创建人ID */
    private String createId;

    /** 更新人ID */
    private String updateId;

    /** 字段列表（非表字段，仅用于前后端交互） */
    private List<BizTableColumn> columns;

    /** 字段数量（列表展示用） */
    private Integer columnCount;
}
