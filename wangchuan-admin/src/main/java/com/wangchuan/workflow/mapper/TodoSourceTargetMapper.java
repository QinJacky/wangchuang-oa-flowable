package com.wangchuan.workflow.mapper;

import com.wangchuan.workflow.domain.Done;
import com.wangchuan.workflow.domain.Todo;
import com.wangchuan.workflow.domain.vo.TodoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 转换实体
 *
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface TodoSourceTargetMapper {
    TodoSourceTargetMapper INSTANCE = Mappers.getMapper(TodoSourceTargetMapper.class);

    /**
     * 转换待办
     *
     * @param todo
     * @return
     */
    Done todo2Done(Todo todo);

    Todo todoVo2Todo(TodoVO todoVO);
}
