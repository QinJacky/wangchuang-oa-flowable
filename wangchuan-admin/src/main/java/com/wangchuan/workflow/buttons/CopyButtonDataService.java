package com.wangchuan.workflow.buttons;

import com.wangchuan.workflow.domain.CommonButton;
import com.wangchuan.workflow.enums.BizButtonEnum;
import com.wangchuan.workflow.enums.PageTypeEnum;
import com.wangchuan.workflow.service.abs.AbstractBizButtonData;
import org.springframework.stereotype.Component;

/**
 * <p> 抄送按钮 </p>
 *
 * @Author  wangchuan
 */
@Component
public class CopyButtonDataService extends AbstractBizButtonData {
    @Override
    public String getCode() {
        return BizButtonEnum.COPY.getCode();
    }

    @Override
    public String getName() {
        return BizButtonEnum.COPY.getName();
    }

    @Override
    public Integer getSort() {
        return 1;
    }

    @Override
    public Boolean isShow(CommonButton button) {
        if (PageTypeEnum.READ.getCode().equals(button.getPageType())) {
            return false;
        }
        return isShowButton(button);
    }
}
