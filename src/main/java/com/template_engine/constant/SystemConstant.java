package com.template_engine.constant;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:35
 */
public class SystemConstant {
    public static final String VERSION = "v1.1.0";
    public static final String RESOURCES_DIR_INIT = "init";
    public static final String PRINT_SEPARATOR_PREFIX = "\n===========================>>>";
    /**
     * 将模板文件转成输出文件时候, 程序会自动去掉 .vm 后缀 以及 ED.vm
     *
     * 为什么会_te.vm后缀?
     * 有时候原始文件没有后缀,那么您也许需要采用ED.vm作为后缀
     * 比如你的文件是Dockerfile, 即使你加上vm = Dockerfile.vm, vscode依旧会将其当成
     * Dockerfile文件, 这时候你需要采用_te.vm后缀, Dockerfile_te.vm, vscode这时会将其
     * 当成一个模板文件
     */
    public static final String TEMPLATE_SUFFIX__VM = ".vm";
    public static final String TEMPLATE_SUFFIX__TE_VM = "_te.vm";
    /**
     * 提示用户是否初始化项目基本目录结构标识
     */
    public static final String SCANNER_INIT_DIR_FLAG = "Y/y";
    /**
     * 存放模板文件的文件夹名称
     * 存放在项目根目录下
     */
    public static final String TEMPLATE_DIR_NAME = "templates";
    public static final String TEMPLATE_MODULES_DIR_NAME = "modules";

    /**
     * 存放值配置文件的文件夹名称
     * 存放在项目根目录下
     */
    public static final String VALUE_DIR_NAME = "values";

    /**
     * 数据目录文件名
     * 存放在项目根目录下
     */
    public static final String DATA_DIR_NAME = "data";

    /**
     * 项目必须加载的yaml文件
     */
    public static final String TEMPLATE_ENGINE_FILE_NAME = "config.yaml";

    /**
     * 模板中key前缀, template-engine 首字母
     */
    public static final String TEMPLATE_KEY_PRE = "te";
    /**
     * 解析模板输出路径, 在工程的out目录下
     * out 目录变成 template
     * @since v1.1.0
     */
    public static final String TEMPLATE_OUT_DIR_NAME = "templates";

    /**
     * 模板自定义对象的前置
     */
    public static final String TEMPLATE_CUSTOM_OBJECT_KEY_PRE = "utils";

    /**
     * 系统配置文件中的key
     */
    public static String PRO_APP = "app";
    public static String PRO_APP_NAME = "name";
    public static String PRO_APP_VERSION = "version";
    public static String PRO_APP_ENV = "env";

    public static String PRO_OUT = "out";
    public static String PRO_OUT_IS_ONLY_READ = "isOnlyRead";
    public static String PRO_ENABLE_VALUES = "enableValues";

    /**
     * 缓存目录名称
     * 存在项目的根目录下
     */
    public static String CACHE_DIR_NAME = ".cache";
}
