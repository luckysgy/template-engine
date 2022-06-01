package com.template_engine.context;

import com.template_engine.constant.SystemConstant;
import com.template_engine.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    public static String targetProjectRootPath = ".";

    public static String templateOutPutPath;

    /**
     * 目标工程的数据目录
     */
    public static String targetProjectDataPath;
    /**
     * 目标工程存放 modules 的文件夹
     */
    public static String targetProjectModulesPath;

    /**
     * 工程模板路径
     */
    public static String targetProjectTemplatePath;

    /**
     * 工程模板路径
     */
    public static String targetProjectValuePath;

    /**
     * 执行easy-deploy所在目录
     */
    public static String CURRENT_DIR = "";

//    public static final String SAVE_TARGET_PROJECT_ROOT_PATH_TEMP_FILE_DIR = "/tmp/easy-deploy";
//    public static final String SAVE_TARGET_PROJECT_ROOT_PATH_TEMP_FILE_PRE = "path-";

    private static final Scanner scanner = new Scanner(System.in);

    public static void init(String currentDir) throws IOException {
        ApplicationContext.targetProjectRootPath = findTemplateEngineRootDir(currentDir);
        ApplicationContext.targetProjectRootPath = FileUtils.winToLinuxForPath(targetProjectRootPath);
        if (ApplicationContext.targetProjectRootPath.endsWith("/")) {
            ApplicationContext.targetProjectRootPath = targetProjectRootPath.substring(0, targetProjectRootPath.lastIndexOf("/"));
        }
        ApplicationContext.targetProjectDataPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.DATA_DIR_NAME;
        ApplicationContext.targetProjectTemplatePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.TEMPLATE_DIR_NAME;
        ApplicationContext.targetProjectValuePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.VALUE_DIR_NAME;
        ApplicationContext.targetProjectModulesPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.TEMPLATE_MODULES_DIR_NAME;
        initDir();
    }

    /**
     * 查找当前目录以及父级目录中存在easy-deploy.yaml所在根目录
     * @param currentDir 执行easy-deploy命令所在的目录
     */
    public static String findTemplateEngineRootDir(String currentDir) {
        if (currentDir == null) {
            throw new RuntimeException("current dir is null");
        }
        CURRENT_DIR = currentDir;

        if (currentDir.contains(":")) {
            currentDir = currentDir.substring(currentDir.indexOf(":") + 1);
        }
        String findDir = currentDir.replace("\\", "/");
        if (findDir.endsWith("/")) {
            findDir = findDir.substring(0, findDir.length() - 1);
        }

        while (!"".equals(findDir) && !"/".equals(findDir)) {
            File file = new File(findDir + "/" + SystemConstant.TEMPLATE_ENGINE_FILE_NAME);
            if (file.exists()) {
                return findDir;
            } else {
                findDir = findDir.substring(0, findDir.lastIndexOf("/"));
            }
        }
        System.out.println("not find template engine root dir! (not find " + SystemConstant.TEMPLATE_ENGINE_FILE_NAME + " file) currentDir is " + currentDir);
        return currentDir;
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
        isInitDir = createDir(ApplicationContext.targetProjectValuePath, isInitDir);
        isInitDir = createDir(ApplicationContext.targetProjectModulesPath, isInitDir);

        String filePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.TEMPLATE_ENGINE_FILE_NAME;
        File file = new File(filePath);
        if (!file.exists()) {
            if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.TEMPLATE_ENGINE_FILE_NAME, file.getPath());
            } else {
                System.out.print("current directory structure is incomplete, do you need to initialize(y/n): ");
                isInitDir = scanner.next();
                if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                    FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.TEMPLATE_ENGINE_FILE_NAME, filePath);
                } else {
                    System.exit(0);
                }
            }
        }

        String userCommandFilePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.USER_COMMAND_FILE_NAME;
        File userCommandFile = new File(userCommandFilePath);
        if (!userCommandFile.exists()) {
            if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.USER_COMMAND_FILE_NAME, userCommandFile.getPath());
            } else {
                System.out.print("current directory structure is incomplete, do you need to initialize(y/n): ");
                isInitDir = scanner.next();
                if (SystemConstant.SCANNER_INIT_DIR_FLAG.contains(isInitDir)) {
                    FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_INIT + "/" + SystemConstant.USER_COMMAND_FILE_NAME, userCommandFilePath);
                } else {
                    System.exit(0);
                }
            }
        }
    }

    private static String createDir(String path, String isInitDir) throws IOException {
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
            // writeTargetProjectRootPathToFile();
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

    /**
     * 删除输出目录中的所有文件, 不删除目录, 只删除文件
     */
    public static void deleteTemplateOutFiles() {
        List<String> outAllFile = FileUtils.getAllFile(templateOutPutPath, false, null);
        for (String outFilePath : outAllFile) {
            File file = new File(outFilePath);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException(String.format("delete out file [%s] fail%n", outFilePath));
                }
            }
        }
    }

//    public static void writeTargetProjectRootPathToFile() throws IOException {
//        FileUtils.mkdirs(SAVE_TARGET_PROJECT_ROOT_PATH_TEMP_FILE_DIR);
//        InputStream inputStream = new ByteArrayInputStream(targetProjectRootPath.getBytes(StandardCharsets.UTF_8));
//        FileUtils.writeToFile(inputStream, SAVE_TARGET_PROJECT_ROOT_PATH_TEMP_FILE_DIR + "/" +
//                SAVE_TARGET_PROJECT_ROOT_PATH_TEMP_FILE_PRE + shellPid);
//    }
}
