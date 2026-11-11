package com.wangchuan.admin.storage.context;

import com.wangchuan.admin.storage.config.FileConfig;
import com.wangchuan.admin.storage.copy.Copier;
import com.wangchuan.admin.storage.delete.Deleter;
import com.wangchuan.admin.storage.download.Downloader;
import com.wangchuan.admin.storage.factory.FileServerFactory;
import com.wangchuan.admin.storage.preview.Previewer;
import com.wangchuan.admin.storage.read.Reader;
import com.wangchuan.admin.storage.upload.Uploader;
import com.wangchuan.admin.storage.write.Writer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件服务上下文
 * @Author  wangchuan
 */
@Component
public class FileServerContext {
    @Autowired
    private FileConfig config;

    /**
     * 获取文件上传实现
     * @return
     */
    public Uploader getUploader() {
        return FileServerFactory.getInstance().getUploader(config.getStorageType());
    }

    /**
     * 获取文件下载实现
     * @return
     */
    public Downloader getDownloader(String storageType) {
        return FileServerFactory.getInstance().getDownloader(storageType);
    }

    /**
     * 获取文件读取实现
     * @return
     */
    public Reader getReader(String storageType) {
        return FileServerFactory.getInstance().getReader(storageType);
    }

    /**
     * 获取文件写入实现
     * @return
     */
    public Writer getWriter(String storageType) {
        return FileServerFactory.getInstance().getWriter(storageType);
    }

    /**
     * 获取文件复制实现
     * @return
     */
    public Copier getCopier(String storageType) {
        return FileServerFactory.getInstance().getCopier(storageType);
    }

    /**
     * 获取文件删除实现
     * @return
     */
    public Deleter getDeleter(String storageType) {
        return FileServerFactory.getInstance().getDeleter(storageType);
    }

    /**
     * 获取文件预览实现
     * @param storageType
     * @return
     */
    public Previewer getPreviewer(String storageType) {
        return FileServerFactory.getInstance().getPreviewer(storageType);
    }
}
