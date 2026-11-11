package com.wangchuan.workflow.service.abs;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.flow.ProcessVarService;
import com.wangchuan.workflow.service.IBizButtonDataService;
import com.wangchuan.common.enums.WhetherStatus;
import com.wangchuan.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p> 业务按钮数据抽象类 </p>
 *
 * @Author  wangchuan
 */
public abstract class AbstractBizButtonData implements IBizButtonDataService {

    @Autowired
    private ProcessVarService processVarService;

    public Boolean isShowButton(CommonButton button) {
        if (button.getVariables() == null) {
            button.setVariables(processVarService.getTaskVariables(button.getTaskId()));
        }
        if (!button.getVariables().containsKey(getCode())) {
            return false;
        }
        String value = String.valueOf(button.getVariables().get(getCode()));
        return StringUtils.equals(WhetherStatus.YES.getCode(), value);
    }
}
