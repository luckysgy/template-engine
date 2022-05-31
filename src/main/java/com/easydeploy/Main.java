package com.easydeploy;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.context.ApplicationContext;
import com.easydeploy.domain.user_command.UserCommand;
import com.easydeploy.properties.EasyDeployProperties;
import com.easydeploy.service.YamlService;
import com.easydeploy.utils.FileUtils;
import com.easydeploy.utils.VelocityUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
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
    public static void main(String[] args) throws IOException {
        String currentDir = "";
        String shellPid = "0";
        try {
            CommandLine cli = commandParser.exec(args);
            if (cli.hasOption("sp")) {
                shellPid = String.valueOf(cli.getOptionValue("sp","0"));
            }

            if (cli.hasOption("cd")) {
                // 获取参数“cd”对应的参数值，如果为空则返回1（默认值）
                currentDir = String.valueOf(cli.getOptionValue("cd",""));
                ApplicationContext.init(currentDir, shellPid);
                ApplicationContext.writeTargetProjectRootPathToFile();
            }

            // 判断是否解析模板
            if (cli.hasOption("pt")){
                boolean isParseTemplate = Boolean.parseBoolean(cli.getOptionValue("pt","false"));
                if (!isParseTemplate) {
                    return;
                }
                YamlService.loadYaml();
                YamlService.processYamlValue();

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
                            .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__ED_VM, "")
                            .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__VM, "");
                    FileUtils.saveAsFileWriter(outPath, sw.toString());
                    if (EasyDeployProperties.outProperties.getOnlyRead()) {
                        if (!new File(outPath).setReadOnly()) {
                            throw new RuntimeException("set [ " + outPath + " ] read only fail");
                        }
                    }
                }
                // 执行用户命令
                UserCommand userCommand = new UserCommand();
                userCommand.init();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationContext.writeTargetProjectRootPathToFile();
        }
    }
}
