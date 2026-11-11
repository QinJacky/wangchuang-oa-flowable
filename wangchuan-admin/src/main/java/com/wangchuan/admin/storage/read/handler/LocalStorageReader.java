package com.wangchuan.admin.storage.read.handler;

import com.wangchuan.common.exception.base.BaseException;
import com.wangchuan.common.utils.StringUtils;
import com.wangchuan.admin.storage.read.Reader;
import com.wangchuan.admin.storage.read.domain.ReadFile;
import com.wangchuan.admin.storage.util.ReadFileUtils;
import com.wangchuan.admin.storage.util.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地文件读取
 * @Author  wangchuan
 */
@Slf4j
@Component
public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {
        String fileContent;
        FileInputStream fileInputStream = null;
        try {
            String extendName = FilenameUtils.getExtension(readFile.getFileUrl());
            fileInputStream = new FileInputStream(FileStorageUtils.getDataPath() + readFile.getFileUrl());
            fileContent = ReadFileUtils.getContentByInputStream(extendName, fileInputStream);
        } catch (IOException e) {
            throw new BaseException("文件读取出现异常");
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        return fileContent;
    }

    @Override
    public byte[] readBytes(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        if (StringUtils.isBlank(fileUrl)) {
            return new byte[0];
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(FileStorageUtils.getDataPath() + fileUrl);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return new byte[0];
    }
}
