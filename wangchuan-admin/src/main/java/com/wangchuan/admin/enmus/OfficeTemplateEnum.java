package com.wangchuan.admin.enmus;

/**
 * office模板枚举
 * @Author  wangchuan
 */
public enum OfficeTemplateEnum {
    WORD("docx", "mapper/template/Word.docx"),
    EXCEL("xlsx", "mapper/template/Excel.xlsx"),
    PPT("pptx", "mapper/template/PowerPoint.pptx");
    private String extendName;
    private String templatePath;
    OfficeTemplateEnum(String extendName, String templatePath) {
        this.extendName = extendName;
        this.templatePath = templatePath;
    }
    public String getExtendName() {
        return extendName;
    }
    public String getTemplatePath() {
        return templatePath;
    }
    public static OfficeTemplateEnum getEnum(String extendName) {
        for (OfficeTemplateEnum officeTemplateEnum : values()) {
            if (officeTemplateEnum.getExtendName().equals(extendName)) {
                return officeTemplateEnum;
            }
        }
        return null;
    }
}
