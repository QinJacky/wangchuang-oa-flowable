package com.wangchuan.workflow.enums;

/**
 * 正文类型枚举
 * @Author  wangchuan
 */
public enum MainTextTypeEnum {

    UPLOAD("0", "上传"),
    BOOKMARK("1", "书签替换");

    private final String code;
    private final String name;

    MainTextTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
