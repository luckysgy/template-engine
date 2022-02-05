package com.easydeploy.constant;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:35
 */
public class SystemConstant {
    public static final String RESOURCES_DIR_INIT = "init";
    public static final String RESOURCES_DIR_SHELL = "shell";
    /**
     * 将模板文件转成输出文件时候, 程序会自动去掉 .vm 后缀 以及 ED.vm
     *
     * 为什么会有ED.vm后缀?
     * 有时候原始文件没有后缀,那么您也许需要采用ED.vm作为后缀
     * 比如你的文件是Dockerfile, 即使你加上vm = Dockerfile.vm, vscode依旧会将其当成
     * Dockerfile文件, 这时候你需要采用ED.vm后缀, DockerfileED.vm, vscode这时会将其
     * 当成一个模板文件
     */
    public static final String DELETE_TEMPLATE_SUFFIX__VM = ".vm";
    public static final String DELETE_TEMPLATE_SUFFIX__ED_VM = "ED.vm";
    /**
     * 提示用户是否初始化项目基本目录结构标识
     */
    public static final String SCANNER_INIT_DIR_FLAG = "Y/y";
    /**
     * 存放模板文件的文件夹名称
     * 存放在项目根目录下
     */
    public static final String TEMPLATE_DIR_NAME = "template";

    /**
     * 存放值配置文件的文件夹名称
     * 存放在项目根目录下
     */
    public static final String VALUE_DIR_NAME = "value";

    /**
     * 数据目录文件名
     * 存放在项目根目录下
     */
    public static final String DATA_DIR_NAME = "data";

    /**
     * shell脚本存放的文件夹名称
     * 存在于输出目录的根目录下
     */
    public static final String SHELL_DIR_NAME = "shell";

    /**
     * 项目必须加载的yaml文件
     */
    public static final String SYSTEM_YAML_FILE_NAME = "easy-deploy.yaml";

    /**
     * 模板中key前缀, easy-deploy 首字母
     */
    public static final String TEMPLATE_KEY_PRE = "ed";
    /**
     * 模板自定义对象的前置
     */
    public static final String TEMPLATE_CUSTOM_OBJECT_KEY_PRE = "utils";

    public static String PRO_APP = "app";
    public static String PRO_APP_NAME = "name";
    public static String PRO_APP_VERSION = "version";
    public static String PRO_APP_ENV = "env";

    public static String PRO_OUT = "out";
    public static String PRO_OUT_PATH = "path";
    public static String PRO_OUT_IS_ONLY_READ = "isOnlyRead";
    public static String PRO_ENABLE_VALUES = "enableValues";

    /**
     * 内部变量名
     */
    // 存放数据的路径
    public static String INTERNAL_VAR_DATA_PATH = "dataPath";
    // 项目根路径
    public static String INTERNAL_VAR_ROOT_PATH = "rootPath";
}
