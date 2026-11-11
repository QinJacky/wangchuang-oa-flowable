package com.wangchuan.workflow.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.wangchuan.common.annotation.Excel;
import com.wangchuan.common.core.domain.BaseEntity;

/**
 * 业务表字段对象 t_biz_table_column
 * 
 * @author oa-generator
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BizTableColumn extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 所属业务表主键 */
    private String tableId;

    /** 列名，如 apply_user */
    @Excel(name = "列名")
    private String columnName;

    /** 中文名 */
    @Excel(name = "中文名")
    private String comment;

    /** 数据库类型 VARCHAR/BIGINT/DECIMAL/DATETIME... */
    private String dbType;

    /** 长度/精度，如 50 或 18,2 */
    private String length;

    /** 表单控件类型 input/select/date/sys_user... */
    private String formType;

    /** 选项（下拉/单选/多选），逗号分隔 */
    private String columnOptions;

    /** 是否主键（0否 1是） */
    private String isPk;

    /** 是否索引（0否 1是） */
    private String isIndex;

    /** 是否可空（0否 1是） */
    private String isNullable;

    /** 排序 */
    private Integer sort;

    /** 删除标识 */
    private String delFlag;
}
