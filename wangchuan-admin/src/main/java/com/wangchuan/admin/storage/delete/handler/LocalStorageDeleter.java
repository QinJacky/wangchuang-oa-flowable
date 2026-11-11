package com.wangchuan.admin.storage.delete.handler;

import com.wangchuan.common.exception.base.BaseException;
import com.wangchuan.admin.storage.delete.Deleter;
import com.wangchuan.admin.storage.delete.domain.DeleteFile;
import com.wangchuan.admin.storage.util.FileStorageUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 本地文件删除器
 */
@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = FileStorageUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            boolean result = localSaveFile.delete();
            if (!result) {
                throw new BaseException("删除本地文件失败");
            }
        }
        deleteCacheFile(deleteFile);
    }
}
