package com.wangchuan.workflow.buttons;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.enums.BizButtonEnum;
import com.wangchuan.workflow.enums.PageTypeEnum;
import com.wangchuan.workflow.service.abs.AbstractBizButtonData;
import org.springframework.stereotype.Component;

/**
 * <p> 减签按钮 </p>
 *
 * @Author  wangchuan
 */
@Component
public class DeleteMultiButtonDataService extends AbstractBizButtonData {
    @Override
    public String getCode() {
        return BizButtonEnum.DELETE_MULTI.getCode();
    }

    @Override
    public String getName() {
        return BizButtonEnum.DELETE_MULTI.getName();
    }

    @Override
    public Integer getSort() {
        return 5;
    }

    @Override
    public Boolean isShow(CommonButton button) {
        if (PageTypeEnum.READ.getCode().equals(button.getPageType())) {
            return false;
        }
        return isShowButton(button);
    }
}
