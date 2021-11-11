package com.easydeploy.constant;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:35
 */
public class SystemConstant {
    /**
     * 存放模板文件的文件夹名称
     */
    public static final String TEMPLATE_DIR_NAME = "template";

    /**
     * 存放值配置文件的文件夹名称
     */
    public static final String VALUE_DIR_NAME = "value";

    /**
     * 数据目录文件名
     */
    public static final String DATA_DIR_NAME = "data";

    /**
     * 项目必须加载的yaml文件
     */
    public static final String SYSTEM_YAML_FILE_NAME = "easy-deploy.yaml";

    /**
     * 模板中key前缀, easy-deploy 首字母
     */
    public static final String TEMPLATE_KEY_PRE = "ed";

    public static String PRO_APP = "app";
    public static String PRO_APP_NAME = "name";
    public static String PRO_APP_VERSION = "version";
    public static String PRO_APP_ENV = "env";

    public static String PRO_TEMPLATE = "template";
    public static String PRO_TEMPLATE_OUTPATH = "outPath";

    /**
     * 内部变量名
     */
    // 存放数据的路径
    public static String INTERNAL_VAR_DATA_PATH = "dataPath";
    // 项目根路径
    public static String INTERNAL_VAR_ROOT_PATH = "rootPath";
}
