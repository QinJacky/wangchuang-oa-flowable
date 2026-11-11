package com.wangchuan.admin.storage.factory;

import com.wangchuan.common.exception.base.BaseException;
import com.wangchuan.admin.storage.copy.Copier;
import com.wangchuan.admin.storage.copy.handler.LocalStorageCopier;
import com.wangchuan.admin.storage.delete.Deleter;
import com.wangchuan.admin.storage.delete.handler.LocalStorageDeleter;
import com.wangchuan.admin.storage.download.Downloader;
import com.wangchuan.admin.storage.download.handler.LocalStorageDownloader;
import com.wangchuan.admin.storage.enums.StorageTypeEnum;
import com.wangchuan.admin.storage.preview.Previewer;
import com.wangchuan.admin.storage.preview.product.LocalStoragePreviewer;
import com.wangchuan.admin.storage.read.Reader;
import com.wangchuan.admin.storage.read.handler.LocalStorageReader;
import com.wangchuan.admin.storage.upload.Uploader;
import com.wangchuan.admin.storage.upload.handler.LocalStorageUploader;
import com.wangchuan.admin.storage.write.Writer;
import com.wangchuan.admin.storage.write.handler.LocalStorageWriter;
import com.wangchuan.common.bean.ApplicationContextHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件服务工厂
 * @Author  wangchuan
 */
public class FileServerFactory {

    private Map<String, Class<? extends Writer>> writerStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Reader>> readerStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Uploader>> uploaderStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Downloader>> downloaderStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Deleter>> deleterStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Copier>> copyerStrategyMap = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Previewer>> previewerStrategyMap = new ConcurrentHashMap<>();

    /**
     * 扩展实现
     */
    private FileServerFactory() {
        writerStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageWriter.class);
        readerStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageReader.class);
        uploaderStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageUploader.class);
        downloaderStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageDownloader.class);
        copyerStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageCopier.class);
        deleterStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStorageDeleter.class);
        previewerStrategyMap.put(StorageTypeEnum.LOCAL.getCode(), LocalStoragePreviewer.class);
    }


    private static class InnerFileServerFactory {
        private final static FileServerFactory fileServerFactory = new FileServerFactory();
    }

    public static FileServerFactory getInstance() {
        return InnerFileServerFactory.fileServerFactory;
    }

    public Uploader getUploader(String type) throws BaseException {
        Class<? extends Uploader> clazz = uploaderStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }

    public Downloader getDownloader(String type) throws BaseException {
        Class<? extends Downloader> clazz = downloaderStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }


    public Reader getReader(String type) throws BaseException {
        Class<? extends Reader> clazz = readerStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }

    public Writer getWriter(String type) throws BaseException {
        Class<? extends Writer> clazz = writerStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }

    public Copier getCopier(String type) throws BaseException {
        Class<? extends Copier> clazz = copyerStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }

    public Deleter getDeleter(String type) throws BaseException {
        Class<? extends Deleter> clazz = deleterStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }

    public Previewer getPreviewer(String type) throws BaseException {
        Class<? extends Previewer> clazz = previewerStrategyMap.get(type);
        if(clazz == null) {
            throw new BaseException("类型不支持");
        }
        return ApplicationContextHelper.getBean(clazz);
    }
}
