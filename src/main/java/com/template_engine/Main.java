package com.template_engine;

import com.template_engine.constant.SystemConstant;
import com.template_engine.domain.ApplicationContext;
import com.template_engine.domain.command.CommandParser;
import com.template_engine.properties.SystemProperties;
import com.template_engine.domain.yaml.YamlParseDO;
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
            if (cli.hasOption("v")) {
                System.out.println("current version: " + SystemConstant.VERSION);
                return;
            }

            // 获取当前目录
            if (cli.hasOption("cd")) {
                // 获取参数“cd”对应的参数值，如果为空则返回1（默认值）
                currentDir = String.valueOf(cli.getOptionValue("cd",""));
                ApplicationContext.init(currentDir);
            } else {
                System.err.println("error: no cd (current dir) command specified");
                return;
            }

            YamlParseDO.loadYaml();
            YamlParseDO.processYamlValue();
            if (cli.hasOption("lv")) {
                YamlParseDO.printFlatYamlValue();
            }

            ApplicationContext.createParseTemplateOutPath(SystemConstant.TEMPLATE_OUT_DIR_NAME);

            VelocityUtils.initVelocity();
            VelocityContext context = new VelocityContext();

            context.put(SystemConstant.TEMPLATE_KEY_PRE, YamlParseDO.getYamlValueData());
            context.put(SystemConstant.TEMPLATE_CUSTOM_OBJECT_KEY_PRE, YamlParseDO.getCustomObject());

            // 渲染模板
            // 获取模板列表
            String parseTemplateOutPath = ApplicationContext.templateOutPutPath;
            List<String> templateFilePathList = getVmList();
            for (String templateFilePath : templateFilePathList) {
                // 判断是否需要解析
                if (!templateFilePath.endsWith(SystemConstant.TEMPLATE_SUFFIX__VM) 
                        && !templateFilePath.endsWith(SystemConstant.TEMPLATE_SUFFIX__TE_VM)) {
                    continue;
                }
                
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(templateFilePath, "UTF-8");
                tpl.merge(context, sw);
                String outPath = parseTemplateOutPath + "/" + templateFilePath.replace(SystemConstant.TEMPLATE_DIR_NAME + "/", "")
                        .replace(SystemConstant.TEMPLATE_SUFFIX__TE_VM, "")
                        .replace(SystemConstant.TEMPLATE_SUFFIX__VM, "");
                File outFile = new File(outPath);
                if (outFile.exists() && !outFile.delete()) {
                    System.out.println("delete file fail: " + outPath);
                    continue;
                }

                FileUtils.saveAsFileWriter(outPath, sw.toString());
                if (SystemProperties.outProperties.getOnlyRead()) {
                    if (!new File(outPath).setReadOnly()) {
                        throw new RuntimeException("set [ " + outPath + " ] read only fail");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }
}
