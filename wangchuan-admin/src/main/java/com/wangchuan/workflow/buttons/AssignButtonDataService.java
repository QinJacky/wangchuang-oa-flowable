package com.wangchuan.workflow.buttons;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.enums.BizButtonEnum;
import com.wangchuan.workflow.enums.PageTypeEnum;
import com.wangchuan.workflow.service.abs.AbstractBizButtonData;
import org.springframework.stereotype.Component;

/**
 * <p> 转办按钮 </p>
 *
 * @Author  wangchuan
 */
@Component
public class AssignButtonDataService extends AbstractBizButtonData {
    @Override
    public String getCode() {
        return BizButtonEnum.ASSIGN.getCode();
    }

    @Override
    public String getName() {
        return BizButtonEnum.ASSIGN.getName();
    }

    @Override
    public Integer getSort() {
        return 3;
    }

    @Override
    public Boolean isShow(CommonButton button) {
        if (PageTypeEnum.READ.getCode().equals(button.getPageType())) {
            return false;
        }
        return isShowButton(button);
    }
}
