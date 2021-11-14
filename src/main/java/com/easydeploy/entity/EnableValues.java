package com.easydeploy.entity;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shenguangyang
 * @date 2021-11-14 9:35
 */
@SuppressWarnings("unchecked")
public class EnableValues {
    private final Map<String, String> enableValueMap;

    public EnableValues(Map<String, Object> yamlData) {
        // 从easy-deploy中获取使能的values
        List<String> enableValuesData = (List<String>) yamlData.get(SystemConstant.PRO_ENABLE_VALUES);
        List<String> enableValuesList = enableValuesData != null ? enableValuesData : new ArrayList<>();
        this.enableValueMap = enableValuesList.stream().collect(Collectors.toMap(String::valueOf, Function.identity(), (key1, key2) -> key2));
    }

    public boolean isEnable(String yamlFilePath) {
        String name = FileUtils.getName(yamlFilePath);
        return enableValueMap.isEmpty() || enableValueMap.get(name) != null;
    }
}
