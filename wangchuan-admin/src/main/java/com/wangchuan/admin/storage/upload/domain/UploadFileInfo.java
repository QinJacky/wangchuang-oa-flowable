package com.wangchuan.admin.storage.upload.domain;

import lombok.Data;

@Data
public class UploadFileInfo {
    private String bucketName;
    private String key;
    private String uploadId;
}