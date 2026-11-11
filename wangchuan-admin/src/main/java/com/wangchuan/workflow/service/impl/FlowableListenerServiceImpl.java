package com.wangchuan.workflow.service.impl;

import com.wangchuan.common.utils.DateUtils;
import com.wangchuan.common.utils.SecurityUtils;
import com.wangchuan.common.utils.uuid.IdUtils;
import com.wangchuan.workflow.domain.FlowableListener;
import com.wangchuan.workflow.mapper.FlowableListenerMapper;
import com.wangchuan.workflow.service.IFlowableListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程监听Service业务层处理
 *
 * @Author  wangchuan
 */
@Service
public class FlowableListenerServiceImpl implements IFlowableListenerService {
    @Autowired
    private FlowableListenerMapper flowableListenerMapper;

    /**
     * 查询流程监听
     *
     * @param id 流程监听主键
     * @return 流程监听
     */
    @Override
    public FlowableListener getFlowableListenerById(String id) {
        return flowableListenerMapper.selectFlowableListenerById(id);
    }

    /**
     * 查询流程监听列表
     *
     * @param flowableListener 流程监听
     * @return 流程监听
     */
    @Override
    public List<FlowableListener> listFlowableListener(FlowableListener flowableListener) {
        return flowableListenerMapper.selectFlowableListenerList(flowableListener);
    }

    /**
     * 新增流程监听
     *
     * @param flowableListener 流程监听
     * @return 结果
     */
    @Override
    public int saveFlowableListener(FlowableListener flowableListener) {
        flowableListener.setId(IdUtils.fastSimpleUUID());
        flowableListener.setCreateId(SecurityUtils.getUserId());
        flowableListener.setCreateTime(DateUtils.getNowDate());
        return flowableListenerMapper.insertFlowableListener(flowableListener);
    }

    /**
     * 修改流程监听
     *
     * @param flowableListener 流程监听
     * @return 结果
     */
    @Override
    public int updateFlowableListener(FlowableListener flowableListener) {
        flowableListener.setUpdateId(SecurityUtils.getUserId());
        flowableListener.setUpdateTime(DateUtils.getNowDate());
        return flowableListenerMapper.updateFlowableListener(flowableListener);
    }

    /**
     * 批量删除流程监听
     *
     * @param ids 需要删除的流程监听主键
     * @return 结果
     */
    @Override
    public int deleteFlowableListenerByIds(String[] ids) {
        return flowableListenerMapper.deleteFlowableListenerByIds(ids);
    }
}
