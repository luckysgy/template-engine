package com.easydeploy.context;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:50
 */
@SuppressWarnings("unchecked")
public class ApplicationContext {
    /**
     * 目标工程的根目录
     */
    public static String targetProjectRootPath;

    public static String templateOutPutPath;

    /**
     * 目标工程的数据目录
     */
    public static String targetProjectDataPath;

    /**
     * 目标工程存放shell脚本的目录
     */
    public static String targetProjectShellPath;

    /**
     * 工程模板路径
     */
    public static String targetProjectTemplatePath;

    /**
     * 工程模板路径
     */
    public static String targetProjectValuePath;

    private static final Scanner scanner = new Scanner(System.in);

    public static void init(String targetProjectRootPath) throws IOException {
        ApplicationContext.targetProjectRootPath = FileUtils.winToLinuxForPath(targetProjectRootPath);
        if (ApplicationContext.targetProjectRootPath.endsWith("/")) {
            ApplicationContext.targetProjectRootPath = targetProjectRootPath.substring(0, targetProjectRootPath.lastIndexOf("/"));
        }
        ApplicationContext.targetProjectDataPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.DATA_DIR_NAME;
        ApplicationContext.targetProjectShellPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.SHELL_DIR_NAME;
        ApplicationContext.targetProjectTemplatePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.TEMPLATE_DIR_NAME;
        ApplicationContext.targetProjectValuePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.VALUE_DIR_NAME;
        initDir();
    }

    /**
     * 初始化目录
     * 目前规定的目录结构为
     * data: 存放你部署时候需要的数据
     * template: 存放模板数据, 每个文件必须以.vm结尾
     * shell: 存放本项目自带的脚本
     * value: 存放模板文件中所有的变量值
     * easy-deploy.yaml: 系统配置文件, 必须有
     */
    private static void initDir() throws IOException {
        String isInitDir = "n";
        isInitDir = createDir(ApplicationContext.targetProjectTemplatePath, isInitDir);
        isInitDir = createDir(ApplicationContext.targetProjectDataPath, isInitDir);
        isInitDir = createDir(ApplicationContext.targetProjectShellPath, isInitDir);
        isInitDir = createDir(ApplicationContext.targetProjectValuePath, isInitDir);


        String filePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.SYSTEM_YAML_FILE_NAME;
        File file = new File(filePath);
        if (!file.exists()) {
            if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.SYSTEM_YAML_FILE_NAME, file.getPath());
            } else {
                System.out.print("current directory structure is incomplete, do you need to initialize(y/n): ");
                isInitDir = scanner.next();
                if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                    FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.SYSTEM_YAML_FILE_NAME, filePath);
                } else {
                    System.exit(0);
                }
            }
        }
    }

    private static String createDir(String path, String isInitDir) {
        File file = new File(path);
        boolean exist = file.exists();
        if (exist) {
            return isInitDir;
        }
        if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Directory creation failed: " + path);
            }
            return isInitDir;
        }

        // 当前目录不完整是否需要初始化, 已存在的不会做任何修改, 只创建不存在的目录或者文件
        System.out.print("current directory structure is incomplete, do you need to initialize(y/n): ");
        isInitDir = scanner.next();
        if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir) && !"".equals(isInitDir)) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Directory creation failed: " + path);
            }
        } else {
            System.exit(0);
        }
        return isInitDir;
    }


    public static void createParseTemplateOutPath(String templateOutPut) {
        ApplicationContext.templateOutPutPath = templateOutPut;
        if (templateOutPut == null) {
            ApplicationContext.templateOutPutPath = "out";
        }

        if (!ApplicationContext.templateOutPutPath.startsWith("/")) {
            ApplicationContext.templateOutPutPath = targetProjectRootPath + "/" + ApplicationContext.templateOutPutPath;
        }
    }
}
