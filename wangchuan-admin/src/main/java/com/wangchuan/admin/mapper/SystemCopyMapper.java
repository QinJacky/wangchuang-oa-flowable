package com.wangchuan.admin.mapper;

import com.wangchuan.admin.domain.SysNotice;
import com.wangchuan.admin.domain.dto.SysNoticeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 转换实体
 *
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface SystemCopyMapper {
    SystemCopyMapper INSTANCE = Mappers.getMapper(SystemCopyMapper.class);

    SysNoticeDTO toSysNoticeDTO(SysNotice sysNotice);
    List<SysNoticeDTO> listSysNoticeDTO(List<SysNotice> list);
}
