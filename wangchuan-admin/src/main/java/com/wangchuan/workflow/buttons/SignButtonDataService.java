package com.wangchuan.workflow.buttons;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.enums.BizButtonEnum;
import com.wangchuan.workflow.enums.PageTypeEnum;
import com.wangchuan.workflow.service.abs.AbstractBizButtonData;
import com.wangchuan.common.constant.Constants;
import com.wangchuan.workflow.domain.dto.FlowTaskDto;
import com.wangchuan.workflow.service.IFlowTaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * <p> 签收按钮 </p>
 *
 * @Author  wangchuan
 */
@Component
public class SignButtonDataService extends AbstractBizButtonData {

    @Autowired
    private IFlowTaskService flowTaskService;

    @Override
    public String getCode() {
        return BizButtonEnum.SIGN.getCode();
    }

    @Override
    public String getName() {
        return BizButtonEnum.SIGN.getName();
    }

    @Override
    public Integer getSort() {
        return 888;
    }

    @Override
    public Boolean isShow(CommonButton button) {
        if (PageTypeEnum.READ.getCode().equals(button.getPageType())) {
            return false;
        }
        if (!isShowButton(button)) {
            return false;
        }
        //多人审批情况
        List<FlowTaskDto> currentTaskList = flowTaskService.getTaskList(button.getProcInsId(), Collections.singletonList(button.getTaskId()));
        if (CollectionUtils.isEmpty(currentTaskList) || currentTaskList.size() > 1) {
            return false;
        }
        FlowTaskDto flowTaskDto = currentTaskList.get(0);
        if (StringUtils.isNotBlank(flowTaskDto.getAssignee())) {
            return flowTaskDto.getAssignee().split(Constants.COMMA).length > 1;
        }
        if (StringUtils.isNotBlank(flowTaskDto.getCandidate())) {
            return flowTaskDto.getCandidate().split(Constants.COMMA).length > 1;
        }
        return false;
    }
}
