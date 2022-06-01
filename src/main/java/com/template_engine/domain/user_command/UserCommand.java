package com.template_engine.domain.user_command;

import com.template_engine.constant.SystemConstant;
import com.template_engine.context.ApplicationContext;
import com.template_engine.utils.CommandExecutor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户命令
 * @author shenguangyang
 * @date 2022-05-31 21:01
 */
@SuppressWarnings("unchecked")
public class UserCommand {

    private static final String YAML_USER_COMMAND_SERVER_KEY = "server";
    private Map<String, List<UserCommandData>> userCommandYamlData;
    /**
     * 命令缓存
     * key: 命令名称 服务名称, eg: build minio
     * value: 执行的脚本路径
     */
    private final Map<String, List<String>> commandCache;

    public UserCommand() {
        commandCache = new HashMap<>();
    }

    public void init() throws Exception {
        loadYaml();
    }

    private void loadYaml() throws Exception {
//        long t1 = System.currentTimeMillis();
        Yaml yaml = new Yaml();
        FileReader in = null;
        try {
            // 首先获取user_command.yaml 中的数据
            String easyDeployYamlPath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.USER_COMMAND_FILE_NAME;
            in = new FileReader(easyDeployYamlPath);
            Object data = yaml.loadAs(in, Object.class);
            if (data == null) {
                userCommandYamlData = new HashMap<>();
            } else {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<UserCommandData>>>() {}.getType();
                userCommandYamlData = gson.fromJson(gson.toJson(data), type);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
//        long t2 = System.currentTimeMillis();
//        System.out.println("init time: " + (t2 - t1) + " ms");
    }

    /**
     * 根据用户的输入搜索相似命令
     * @param userInput 用户输入的命令, 比如用户输入 ed exec build minio, 则这里就是入参就是build minio
     * @return 相似命令
     */
    public List<String> searchCommand(String userInput) {
        List<UserCommandData> userCommandData = userCommandYamlData.get(YAML_USER_COMMAND_SERVER_KEY);
        if (userCommandData == null || userInput == null || "".equals(userInput)) {
            return new ArrayList<>();
        }
        // userInput 第一个参数是命令名称, 之后的是服务名称 (可以有多个)
        String[] userInputArr = userInput.split(" ");
        if (userInputArr.length == 1) {
            System.out.println("error: illegal input userCommand [ " + userInput + " ]");
            System.exit(0);
        }
        String userInputCommandName = userInputArr[0];
        Map<String, String> userInputServerNameMap = new HashMap<>();
        for (int i = 1; i < userInputArr.length; i++) {
            userInputServerNameMap.put(userInputArr[i], "");
        }
        List<String> result = new ArrayList<>();
        for (UserCommandData item : userCommandData) {
            String serverName = item.getName();
            // 判断输入字符串中是否包含服务名称
            if (userInputServerNameMap.containsKey(serverName)) {
                List<UserCommandData.CommandsData> commands = item.getCommands();
                for (UserCommandData.CommandsData command : commands) {
                    String commandName = command.getName();
                    // 判断输入字符串中是否等于命令名称
                    if (userInputCommandName.contains(commandName)) {
                        List<String> scripts = command.getScripts();
                        if (scripts == null || scripts.isEmpty()) {
                            continue;
                        }

                        String name = commandName + " " + serverName;
                        List<String> targetScripts = new ArrayList<>();
                        for (String script : scripts) {
                            if (script == null || "".equals(script)) {
                                continue;
                            }
                            targetScripts.add(ApplicationContext.templateOutPutPath  + "/" + script.replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__VM, ""));
                        }
                        commandCache.put(name, targetScripts);
                        // 添加可选择的命令
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 执行 {@link #searchCommand(String)} 返回的其中一个命令
     * @param command eg: build minio
     */
    public void execCommand(String command) throws Exception {
        List<String> list = commandCache.get(command);
        if (list == null) {
            throw new RuntimeException(command + " not exist");
        }
        for (String script : list) {
            System.out.println("exec ===> " + command + " " + script);
            CommandExecutor commandExecutor = new CommandExecutor();
            String[] cmd = {"/bin/bash", "-c", "bash " + script};
            String workDir = script.substring(0, script.lastIndexOf("/"));
            commandExecutor.executeCommand(cmd, new File(workDir), System.getenv());
        }
    }

    /**
     * 获取全部可用命令
     * @return build minio  / start redis  / build redis 等等用户定义命令
     */
    public List<String> getAllCommand() {
        List<UserCommandData> userCommandData = userCommandYamlData.get(YAML_USER_COMMAND_SERVER_KEY);
        List<String> result = new ArrayList<>();
        for (UserCommandData item : userCommandData) {
            String serverName = item.getName();
            List<UserCommandData.CommandsData> commands = item.getCommands();
            for (UserCommandData.CommandsData command : commands) {
                String commandName = command.getName();
                String name = commandName + " " + serverName;
                result.add(name);
            }
        }
        return result;
    }
}
