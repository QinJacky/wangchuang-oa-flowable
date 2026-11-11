package com.wangchuan.workflow.mapper;
import com.wangchuan.workflow.domain.Template;
import com.wangchuan.workflow.domain.TemplateDynamicForm;
import com.wangchuan.workflow.domain.TemplateMainText;
import com.wangchuan.workflow.domain.TemplateMessageNotice;
import com.wangchuan.workflow.module.TemplateDTO;
import com.wangchuan.workflow.module.TemplateMainTextDTO;
import com.wangchuan.workflow.module.TemplateMessageNoticeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 对象转换
 * @Author : wangchuan.com
 */
@Mapper(componentModel = "spring")
public interface TemplateSourceTargetMapper {
    TemplateSourceTargetMapper INSTANCE = Mappers.getMapper(TemplateSourceTargetMapper.class);

    Template convertTemplate(TemplateDTO templateDTO);

    TemplateDTO convertTemplateDTO(Template template);

    TemplateDynamicForm copyDynamicForm(TemplateDynamicForm templateDynamicForm);

    Template copyTemplate(Template template);

    TemplateMainTextDTO copyTemplateMainTextDTO(TemplateMainText templateMainText);

    TemplateMessageNoticeDTO copyTemplateMessageNoticeDTO(TemplateMessageNotice messageNotice);
}
