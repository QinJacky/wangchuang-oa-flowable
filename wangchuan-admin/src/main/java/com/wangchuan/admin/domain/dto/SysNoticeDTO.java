package com.wangchuan.admin.domain.dto;

import com.wangchuan.admin.domain.SysNotice;
import lombok.Data;

/**
 * @Author  wangchuan
 */
@Data
public class SysNoticeDTO extends SysNotice {
    private static final long serialVersionUID = 1L;

    /**
     * 阅读状态（0未读 1已读）
     */
    private String readFlag;
}
