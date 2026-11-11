package com.wangchuan.admin.storage.delete;

import com.wangchuan.admin.storage.delete.domain.DeleteFile;
import com.wangchuan.admin.storage.util.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 文件删除抽象类
 */
@Slf4j
public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);

    protected void deleteCacheFile(DeleteFile deleteFile) {
        File cacheFile = FileStorageUtils.getCacheFile(deleteFile.getFileUrl());
        if (cacheFile.exists()) {
            boolean result = cacheFile.delete();
            if (!result) {
                log.error("删除本地缓存文件失败！");
            }
        }
    }
}
