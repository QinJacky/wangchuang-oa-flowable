package com.wangchuan.workflow.service;

import com.wangchuan.workflow.domain.BizButton;
import com.wangchuan.workflow.domain.CommonButton;

import java.util.List;

/**
 * <p> 业务按钮接口 </p>
 *
 * @Author  wangchuan
 */
public interface IBizButtonService {

    List<BizButton> getButtons(CommonButton button);
}
