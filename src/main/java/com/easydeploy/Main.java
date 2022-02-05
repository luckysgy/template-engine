package com.easydeploy;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.context.ApplicationContext;
import com.easydeploy.properties.EasyDeployProperties;
import com.easydeploy.service.YamlService;
import com.easydeploy.utils.FileUtils;
import com.easydeploy.utils.VelocityUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:30
 */
public class Main {
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
        ApplicationContext.init(args[0]);

        YamlService.loadYaml();
        YamlService.processYamlValue();

        ApplicationContext.createParseTemplateOutPath(EasyDeployProperties.template.getOutPath());
        String parseTemplateOutPath = ApplicationContext.templateOutPutPath;
        FileUtils.deleteDir(parseTemplateOutPath);

        VelocityUtils.initVelocity();
        VelocityContext context = new VelocityContext();

        context.put(SystemConstant.TEMPLATE_KEY_PRE, YamlService.getYamlValueData());
        context.put(SystemConstant.TEMPLATE_CUSTOM_OBJECT_KEY_PRE, YamlService.getCustomObject());

        // 渲染模板
        // 获取模板列表
        List<String> templates = getVmList();
        for (String template : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            String outPath = parseTemplateOutPath + "/" + template.replace(SystemConstant.TEMPLATE_DIR_NAME + "/", "")
                    .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__ED_VM, "")
                    .replace(SystemConstant.DELETE_TEMPLATE_SUFFIX__VM, "");
            FileUtils.saveAsFileWriter(outPath, sw.toString());
            // System.out.println("done parse template: " + template);
        }
    }
}
