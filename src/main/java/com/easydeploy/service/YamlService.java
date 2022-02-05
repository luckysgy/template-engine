package com.easydeploy.service;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.context.ApplicationContext;
import com.easydeploy.entity.EnableValues;
import com.easydeploy.properties.App;
import com.easydeploy.properties.EasyDeployProperties;
import com.easydeploy.properties.OutProperties;
import com.easydeploy.utils.FileUtils;
import com.easydeploy.directive.RootPath;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shenguangyang
 * @date 2021/11/11 11:14
 */
@SuppressWarnings("unchecked")
public class YamlService {
    // 也可以将值转换为Map
    private static final Map<String, Object> YAML_DATA = new HashMap<>();

    // 存放自定义对象
    private static final Map<String, Object> CUSTOM_OBJECT = new HashMap<>();

    // 正则表达式用于提取 字符串中所有${}
    private static final Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");

    /**
     * 加载yaml
     */
    public static void loadYaml() {
        Yaml yaml = new Yaml();
        InputStream in = null;
        try {
            // 首先获取 easy-deploy.yaml 中的数据
            String easyDeployYamlPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.SYSTEM_YAML_FILE_NAME;
            in = new FileInputStream(easyDeployYamlPath);
            Map<String, Object> easyDeployYamlData = yaml.loadAs(in, Map.class);
            if (easyDeployYamlData == null) {
                throw new RuntimeException(SystemConstant.SYSTEM_YAML_FILE_NAME + " 内容不能为空");
            }
            YAML_DATA.putAll(easyDeployYamlData);

            EnableValues enableValues = new EnableValues(YAML_DATA);

            List<String> allFile = FileUtils.getAllFile(ApplicationContext.targetProjectValuePath, false, null);
            allFile = FileUtils.winToLinuxForPath(allFile);
            for (String yamlFilePath : allFile) {
                if (!enableValues.isEnable(yamlFilePath)) {
                    continue;
                }
                in = new FileInputStream(yamlFilePath);
                Map<String, Object> yamlData = yaml.loadAs(in, Map.class);
                if (yamlData == null) {
                    System.out.println("value file is empty: " + yamlFilePath);
                    continue;
                }
                YAML_DATA.putAll(yamlData);
            }

            initInternalVariable();
            loadCustomObject();

            // 从easy-deploy中获取app信息
            App app = EasyDeployProperties.app;
            Map<String, String> appInfo = (Map<String, String>) YAML_DATA.get(SystemConstant.PRO_APP);
            if (appInfo == null) {
                System.err.println("easy-deploy.yaml === app is null");
                return;
            }
            app.setName(appInfo.get(SystemConstant.PRO_APP_NAME));
            app.setEnv(appInfo.get(SystemConstant.PRO_APP_ENV));
            app.setVersion(appInfo.get(SystemConstant.PRO_APP_VERSION));

            // 从easy-deploy中获取模板信息
            Map<String, Object> outConfigInfo = (Map<String, Object>) YAML_DATA.get(SystemConstant.PRO_OUT);
            if (outConfigInfo == null) {
                System.err.println("easy-deploy.yaml === out is null");
                return;
            }
            OutProperties outProperties = EasyDeployProperties.outProperties;
            outProperties.setPath(String.valueOf(outConfigInfo.get(SystemConstant.PRO_OUT_PATH)));
            Boolean isOnlyRead = Boolean.valueOf(String.valueOf(outConfigInfo.get(SystemConstant.PRO_OUT_IS_ONLY_READ)));
            outProperties.setOnlyRead(isOnlyRead);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化内部变量
     */
    private static void initInternalVariable() {
        YAML_DATA.put(SystemConstant.INTERNAL_VAR_DATA_PATH, ApplicationContext.targetProjectDataPath);
        YAML_DATA.put(SystemConstant.INTERNAL_VAR_ROOT_PATH, ApplicationContext.targetProjectRootPath);
    }

    /**
     * 处理yaml中的值
     */
    public static void processYamlValue() {
        Map<String, Object> yamlDataParseResult = new HashMap<>();
        recursionYamlData("", YAML_DATA, yamlDataParseResult);
        Map<String,String> envMap = System.getenv();

        Map<String, Object> forResult = new HashMap<>(yamlDataParseResult);
        for (Map.Entry<String, Object> entry : forResult.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                processYamlValueByRecursion(key, String.valueOf(value), yamlDataParseResult);
            }
        }

        // 查找环境变量
        Map<String, Object> forResultFromEnv = new HashMap<>(yamlDataParseResult);
        for (Map.Entry<String, Object> entry : forResultFromEnv.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                String valueString = String.valueOf(value);
                Matcher matcher = pattern.matcher(valueString);
                while (matcher.find()) {
                    String findKey = matcher.group(1);
                    // 查找环境变量
                    value = envMap.get(findKey);
                    if (value == null) {
                        continue;
                    }
                    valueString = valueString.replace("${" + findKey + "}", value + "");
                    setYamlQuote(key, valueString);
                    yamlDataParseResult.put(key, valueString);
                }
            }
        }
    }

    /**
     * 递归处理yaml文件
     * @param key 值带有 ${...} 符号的key
     * @param valueString 值中含有 ${...}
     * @return 返回的是值
     */
    private static String processYamlValueByRecursion(String key, String valueString, Map<String, Object> yamlDataParseResult) {
        Matcher matcher = pattern.matcher(valueString);
        while (matcher.find()) {
            Object value;
            String findKey = matcher.group(1);
            value = yamlDataParseResult.get(findKey);
            if (value == null) {
                continue;
            }
            if (value instanceof String || value instanceof Double || value instanceof Integer || value instanceof Float
                    || value instanceof Long || value instanceof Short) {
                value = processYamlValueByRecursion(findKey, String.valueOf(value), yamlDataParseResult);
                valueString = valueString.replace("${" + findKey + "}", value + "");
            }
        }
        setYamlQuote(key, valueString);
        yamlDataParseResult.put(key, valueString);
        return valueString;
    }

    private static void setYamlQuote(String keyStr, Object value) {
        String[] keys = keyStr.split("\\.");
        Map<String, Object> findDataPre = new HashMap<>();
        Object findData = null;
        for (String key : keys) {
            if (findData == null) {
                findData = YAML_DATA.get(key);
            } else {
                findData = findDataPre.get(key);
            }

            if (findData instanceof Map) {
                findDataPre = (Map<String, Object>) findData;
            } else {
                findDataPre.put(key, value);
            }
        }
    }

    /**
     * 加载自定义对象, 在模板文件中可以直接调用方法
     */
    private static void loadCustomObject() {
        CUSTOM_OBJECT.put("path", new RootPath());
    }

    /**
     * 将map中的key, 以点连接在一起
     *
     * 比如yaml插件解析的数据为Map
     *  key1:
     *      key2:
     *          key3-1: we4r23
     *          key3-2: 234234
     *      key2-1: 23412
     *
     *  可以看到如果你的yaml文件有多层, 则对应map就有多层
     *
     *  而本方法的作用从根节点出发,连接所有的key, 比如key1.key2.key3-1 = we4r23
     * @param keyData
     * @param valueData
     * @param result
     */
    public static void recursionYamlData(String keyData, Map<String, Object> valueData, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : valueData.entrySet()) {
            keyData = keyData + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                recursionYamlData(keyData, (Map<String, Object>) entry.getValue(), result);
            } else {
                result.put(keyData.substring(1), entry.getValue());
            }
            keyData = keyData.substring(0, keyData.lastIndexOf("."));

        }
    }

    public static Map<String, Object> getYamlValueData() {
        return YAML_DATA;
    }

    public static Map<String, Object> getCustomObject() {
        return CUSTOM_OBJECT;
    }
}
