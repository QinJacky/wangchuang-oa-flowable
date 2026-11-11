package com.wangchuan.workflow.service.impl;

import com.wangchuan.workflow.domain.dto.FlowVariableInstanceDto;
import com.wangchuan.workflow.domain.qo.FlowVariableInstanceQo;
import com.wangchuan.workflow.common.enums.VariableTypeEnum;
import com.wangchuan.workflow.mapper.FlowVariableInstanceMapper;
import com.wangchuan.workflow.service.IFlowVariableInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 流程变量实例接口实现 </p>
 *
 * @Author  wangchuan
 */
@Service
public class FlowVariableInstanceServiceImpl implements IFlowVariableInstanceService {

    @Autowired
    private FlowVariableInstanceMapper flowVariableInstanceMapper;

    @Override
    public Map<String, Object> getVariableInstanceMap(FlowVariableInstanceQo qo) {
        Map<String, Object> requestedVariables = new HashMap<>();
        List<FlowVariableInstanceDto> flowVariableInstanceDtos = flowVariableInstanceMapper.selectVariableInstances(qo);
        for (FlowVariableInstanceDto flowVariableInstanceDto : flowVariableInstanceDtos) {
            VariableTypeEnum variableTypeEnum = VariableTypeEnum.fromCode(flowVariableInstanceDto.getType());
            if (variableTypeEnum == null) {
                continue;
            }
            switch (variableTypeEnum) {
                case INTEGER:
                    requestedVariables.put(flowVariableInstanceDto.getName(), flowVariableInstanceDto.getLongValue());
                    break;
                case DOUBLE:
                    requestedVariables.put(flowVariableInstanceDto.getName(), flowVariableInstanceDto.getDoubleValue());
                    break;
                case BOOLEAN:
                    requestedVariables.put(flowVariableInstanceDto.getName(), flowVariableInstanceDto.getLongValue() == 1);
                    break;
                default:
                    requestedVariables.put(flowVariableInstanceDto.getName(), flowVariableInstanceDto.getTextValue());
                    break;
            }
        }
        return requestedVariables;
    }
}
