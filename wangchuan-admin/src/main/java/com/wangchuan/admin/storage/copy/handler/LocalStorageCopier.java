package com.wangchuan.admin.storage.copy.handler;

import com.wangchuan.common.exception.base.BaseException;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.admin.storage.copy.Copier;
import com.wangchuan.admin.storage.copy.domain.CopyFile;
import com.wangchuan.admin.storage.util.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class LocalStorageCopier extends Copier {
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = IdUtils.fastSimpleUUID();
        String fileUrl = FileStorageUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        File saveFile = new File(FileStorageUtils.getDataPath() + fileUrl);
        try {
            FileUtils.copyInputStreamToFile(inputStream, saveFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BaseException("创建文件出现异常");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return fileUrl;
    }
}
