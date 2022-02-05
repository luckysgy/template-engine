package com.easydeploy.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2021-11-11 20:28
 */
public class EasyDeployProperties {
    public static App app = new App();
    public static OutProperties outProperties = new OutProperties();
    /**
     * 指定使能value目录下的哪些yaml文件, 默认不指定(为空)就是全部使能
     */
    public static List<String> enableValues = new ArrayList<>();
}
