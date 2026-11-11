package com.wangchuan.workflow.buttons;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.enums.BizButtonEnum;
import com.wangchuan.workflow.enums.PageTypeEnum;
import com.wangchuan.workflow.service.abs.AbstractBizButtonData;
import org.springframework.stereotype.Component;

/**
 * <p> 保存按钮 </p>
 *
 * @Author  wangchuan
 */
@Component
public class SaveButtonDataService extends AbstractBizButtonData {
    @Override
    public String getCode() {
        return BizButtonEnum.SAVE.getCode();
    }

    @Override
    public String getName() {
        return BizButtonEnum.SAVE.getName();
    }

    @Override
    public Integer getSort() {
        return 999;
    }

    @Override
    public Boolean isShow(CommonButton button) {
        if (PageTypeEnum.READ.getCode().equals(button.getPageType())) {
            return false;
        }
        return isShowButton(button);
    }
}
