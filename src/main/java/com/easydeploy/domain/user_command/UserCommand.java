package com.easydeploy.domain.user_command;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.context.ApplicationContext;
import com.easydeploy.utils.CommandExecutor;
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
    private static final String USER_COMMAND_FILE_NAME = "user-command.yaml";
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
        List<String> list = searchCommand("build minio redis");
        for (String s : list) {
            execCommand(s);
        }
    }

    private void loadYaml() throws Exception {
        long t1 = System.currentTimeMillis();
        Yaml yaml = new Yaml();
        FileReader in = null;
        try {
            // 首先获取user_command.yaml 中的数据
            String easyDeployYamlPath = ApplicationContext.targetProjectRootPath + "/" + USER_COMMAND_FILE_NAME;
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
        long t2 = System.currentTimeMillis();
        System.out.println("init time: " + (t2 - t1) + " ms");
    }

    /**
     * 根据用户的输入搜索相似命令
     * @param userInput 用户输入的命令, 比如用户输入 ed exec build minio, 则这里就是入参就是build minio
     * @return 相似命令
     */
    public List<String> searchCommand(String userInput) {
        List<UserCommandData> userCommandData = userCommandYamlData.get(YAML_USER_COMMAND_SERVER_KEY);
        if (userCommandData == null || userInput == null) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (UserCommandData item : userCommandData) {
            String serverName = item.getName();
            // 判断输入字符串中是否包含服务名称
            if (userInput.contains(serverName)) {
                List<UserCommandData.CommandsData> commands = item.getCommands();
                for (UserCommandData.CommandsData command : commands) {
                    String commandName = command.getName();
                    // 判断输入字符串中是否包含命令名称
                    if (userInput.contains(commandName)) {
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
            commandExecutor.executeCommand(cmd, new File(ApplicationContext.templateOutPutPath), System.getenv());
        }
    }
}
