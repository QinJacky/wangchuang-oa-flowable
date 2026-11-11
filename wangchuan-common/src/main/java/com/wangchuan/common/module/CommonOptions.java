package com.wangchuan.common.module;

import lombok.Data;

import java.util.List;

/**
 * 通用的下拉选项
 * @Author  wangchuan
 */
@Data
public class CommonOptions {
    /**
     * 值
     */
    private String value;

    /**
     * 标签
     */
    private String label;

    /**
     * 子选项
     */
    private List<OptionsVO> children;

}
