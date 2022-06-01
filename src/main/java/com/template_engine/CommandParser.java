package com.template_engine;

import org.apache.commons.cli.*;

/**
 * 命令行解析
 * @author shenguangyang
 * @date 2022-02-06 8:59
 */
public class CommandParser {
    private final Options options = new Options();
    private final HelpFormatter helpFormatter = new HelpFormatter();

    public CommandLine exec(String[] args)  {
        try {
            // 根据命令行参数定义Option对象，第1/2/3/4个参数分别是指命令行参数名缩写、参数名全称、是否有参数值、参数描述
            Option optCurrentDir = new Option("cd","current-dir",true,"the directory where the command is executed");
            optCurrentDir.setRequired(true);

            Option optExecUserCommand = new Option("euc","exec-user-command",true,"exec user command");
            optExecUserCommand.setRequired(false);

            // 用户命令操作
            Option optUcCommand = new Option("uc","user-command",true,"user command, arg: \n\t1.ls: List all available user commands");
            optUcCommand.setRequired(false);

            Option optHelpCommand = new Option("h","help",false,"help command");
            optHelpCommand.setRequired(false);

            options.addOption(optCurrentDir);
            options.addOption(optExecUserCommand);
            options.addOption(optUcCommand);
            options.addOption(optHelpCommand);

            CommandLineParser cliParser = new DefaultParser();

            CommandLine cli = cliParser.parse(options, args);
            if (cli == null) {
                throw new RuntimeException("command parse fail");
            }

            // 打印帮助信息
            if (cli.hasOption("h")) {
                helpFormatter.printHelp(" cli options", options);
                System.exit(0);
            }
            return cli;
        } catch (Exception e) {
            // 解析失败是用 HelpFormatter 打印 帮助信息
            helpFormatter.printHelp(" cli options", options);
            System.err.println("error: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }
}
