package com.wangchuan.admin.business.mapper;

import com.wangchuan.admin.business.domain.FileStorage;
import com.wangchuan.admin.storage.upload.domain.UploadFileResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 对象转换
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface FileSourceTargetMapper {
    FileSourceTargetMapper INSTANCE = Mappers.getMapper(FileSourceTargetMapper.class);

    FileStorage convertFileStorage(UploadFileResult uploadFileResult);

    UploadFileResult convertUploadFileResult(FileStorage fileStorage);
}
