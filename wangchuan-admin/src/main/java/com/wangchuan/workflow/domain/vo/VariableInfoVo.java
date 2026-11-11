package com.wangchuan.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 流程变量
 *
 * @Author  wangchuan
 */
@Data
public class VariableInfoVo {

    String variableName;

    String variableTypeName;

    Object value;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date lastUpdatedTime;
}
