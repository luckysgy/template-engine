package com.easydeploy;

import org.apache.commons.cli.*;

/**
 * 命令行解析
 * @author shenguangyang
 * @date 2022-02-06 8:59
 */
public class CommandParser {
    private final Options options = new Options();


    public CommandLine exec(String[] args) throws ParseException {
        // 根据命令行参数定义Option对象，第1/2/3/4个参数分别是指命令行参数名缩写、参数名全称、是否有参数值、参数描述
        Option optCurrentDir = new Option("cd","currentDir",true,"the directory where the command is executed");
        optCurrentDir.setRequired(true);

        // 是否解析模板
        Option optParseTemplate = new Option("pt","parse template",true,"true | false");
        optCurrentDir.setRequired(false);

        Option optShellPid = new Option("sp","shell pid",true,"passed in by the shell");
        optShellPid.setRequired(true);

        Option optExecUserCommand = new Option("exec","exec command",true,"exec user command");
        optExecUserCommand.setRequired(false);

        Options options = new Options();
        options.addOption(optCurrentDir);
        options.addOption(optParseTemplate);
        options.addOption(optShellPid);
        options.addOption(optExecUserCommand);

        CommandLineParser cliParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        CommandLine cli;
        try {
            cli = cliParser.parse(options, args);
            if (cli == null) {
                throw new RuntimeException("command parse fail");
            }
            return cli;
        } catch (ParseException e) {
            // 解析失败是用 HelpFormatter 打印 帮助信息
            helpFormatter.printHelp(" cli options", options);
            throw e;
        }
    }
}
