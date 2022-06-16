package com.template_engine;

import com.template_engine.constant.SystemConstant;
import com.template_engine.context.ApplicationContext;
import com.template_engine.domain.user_command.UserCommand;
import com.template_engine.properties.EasyDeployProperties;
import com.template_engine.service.YamlService;
import com.template_engine.utils.FileUtils;
import com.template_engine.utils.VelocityUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:30
 */
public class Main {
    private static final CommandParser commandParser = new CommandParser();
    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getVmList() {
        List<String> templates = new ArrayList<>();
        List<String> allVmFilePaths = FileUtils.getAllFile(ApplicationContext.targetProjectTemplatePath, false, null);
        allVmFilePaths = FileUtils.winToLinuxForPath(allVmFilePaths);
        for (String vmFilePath : allVmFilePaths) {
            templates.add(vmFilePath.replace(ApplicationContext.targetProjectRootPath + "/", ""));
        }
        return templates;
    }

    /**
     * java -jar xxx.jar /mnt/targetProject (跟上目标工程的根路径)
     * @param args args[0] = /mnt/targetProject
     */
    public static void main(String[] args)  {
        String currentDir = "";
        try {
            CommandLine cli = commandParser.exec(args);
            // 获取当前目录
            if (cli.hasOption("cd")) {
                // 获取参数“cd”对应的参数值，如果为空则返回1（默认值）
                currentDir = String.valueOf(cli.getOptionValue("cd",""));
                ApplicationContext.init(currentDir);
                // ApplicationContext.writeTargetProjectRootPathToFile();
            } else {
                System.err.println("error: no cd (current dir) command specified");
                return;
            }

            UserCommand userCommand = new UserCommand();
            userCommand.init();

            if (cli.hasOption("uc")) {
                String ucOptionValue = String.valueOf(cli.getOptionValue("uc","ls"));
                // 列出所有可用的用户命令
                if ("ls".equals(ucOptionValue)) {
                    List<String> allUserCommand = userCommand.getAllCommand();
                    System.out.println("all available user commands: ");
                    for (String uc : allUserCommand) {
                        System.out.println("  " + uc);
                    }
                }
                return;
            }

            YamlService.loadYaml();
            YamlService.processYamlValue();
            YamlService.printFlatYamlValue();

            ApplicationContext.createParseTemplateOutPath(SystemConstant.TEMPLATE_OUT_PATH);

            ApplicationContext.deleteTemplateOutFiles();

            VelocityUtils.initVelocity();
            VelocityContext context = new VelocityContext();

            context.put(SystemConstant.TEMPLATE_KEY_PRE, YamlService.getYamlValueData());
            context.put(SystemConstant.TEMPLATE_CUSTOM_OBJECT_KEY_PRE, YamlService.getCustomObject());

            // 渲染模板
            // 获取模板列表
            String parseTemplateOutPath = ApplicationContext.templateOutPutPath;
            List<String> templates = getVmList();
            for (String template : templates) {
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);
                String outPath = parseTemplateOutPath + "/" + template.replace(SystemConstant.TEMPLATE_DIR_NAME + "/", "")
                        .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__TE_VM, "")
                        .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__VM, "");
                FileUtils.saveAsFileWriter(outPath, sw.toString());
                if (EasyDeployProperties.outProperties.getOnlyRead()) {
                    if (!new File(outPath).setReadOnly()) {
                        throw new RuntimeException("set [ " + outPath + " ] read only fail");
                    }
                }
            }

            // 执行用户命令
            List<String> allArgs = cli.getArgList();
            if (!cli.hasOption("euc")) {
                return;
            }
            StringBuilder execCommand = new StringBuilder(String.valueOf(cli.getOptionValue("euc")));
            for (String arg : allArgs) {
                execCommand.append(" ").append(arg);
            }


            List<String> searchCommand = userCommand.searchCommand(execCommand.toString());
            if (searchCommand.isEmpty()) {
                System.out.println("execCommand: " + execCommand + " not exist\n");
                System.out.println("all available user commands: ");
                List<String> allUserCommand = userCommand.getAllCommand();
                for (String uc : allUserCommand) {
                    System.out.println("  " + uc);
                }
                System.exit(0);
            }

            System.out.println("execCommand: " + execCommand);
            // 执行命令
            for (String command : searchCommand) {
                userCommand.execCommand(command);
            }
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            // ApplicationContext.writeTargetProjectRootPathToFile();
        }
    }
}
