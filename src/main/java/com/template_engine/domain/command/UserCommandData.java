package com.template_engine.domain.command;

import java.util.List;

/**
 * 用户命令数据
 * @author shenguangyang
 * @date 2022-05-31 21:16
 */
public class UserCommandData {
    // 服务名称
    private String name;
    private List<CommandsData> commands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CommandsData> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandsData> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "UserCommandData{" +
                "name='" + name + '\'' +
                ", commands=" + commands +
                '}';
    }

    public static class CommandsData {
        // 可以指定多个命令
        private String name;
        private List<String> scripts;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getScripts() {
            return scripts;
        }

        public void setScripts(List<String> scripts) {
            this.scripts = scripts;
        }

        @Override
        public String toString() {
            return "CommandsData{" +
                    "name='" + name + '\'' +
                    ", scripts=" + scripts +
                    '}';
        }
    }
}
