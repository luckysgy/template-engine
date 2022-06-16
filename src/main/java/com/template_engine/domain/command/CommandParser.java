package com.template_engine.domain.command;

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
            Option currentDirOption = new Option("cd","current_dir",true,"the directory where the command is executed");
            currentDirOption.setRequired(true);

            Option versionOption = new Option("v","version",false,"current version");
            versionOption.setRequired(false);

            Option helpOption = new Option("h","help",false,"help command");
            helpOption.setRequired(false);

            Option lsValuesOption = new Option("lv","ls_values",false,"list values");
            lsValuesOption.setRequired(false);

            options.addOption(currentDirOption);
            options.addOption(helpOption);
            options.addOption(lsValuesOption);
            options.addOption(versionOption);

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
