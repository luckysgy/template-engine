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
    public static List<String> getVmList(ApplicationContext applicationContext) {
        List<String> templates = new ArrayList<>();
        List<String> allVmFilePaths = FileUtils.getAllFile(applicationContext.getTargetProjectRootPath() + "/" + SystemConstant.TEMPLATE_DIR_NAME, false, null);
        allVmFilePaths = FileUtils.winToLinuxForPath(allVmFilePaths);
        for (String vmFilePath : allVmFilePaths) {
            templates.add(vmFilePath.replace(applicationContext.getTargetProjectRootPath() + "/", ""));
        }
        return templates;
    }

    /**
     * java -jar xxx.jar /mnt/targetProject (跟上目标工程的根路径)
     * @param args args[0] = /mnt/targetProject
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(args[0]);
        YamlService.loadYaml(applicationContext);
        YamlService.processYamlValue();

        VelocityUtils.initVelocity(applicationContext);
        VelocityContext context = new VelocityContext();

        context.put(SystemConstant.TEMPLATE_KEY_PRE, YamlService.getYamlValueData());

        String parseTemplateOutPath = applicationContext.createParseTemplateOutPath(EasyDeployProperties.template.getOutPath());
        // 渲染模板
        // 获取模板列表
        List<String> templates = getVmList(applicationContext);
        for (String template : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "utf-8");
            tpl.merge(context, sw);
            String outPath = parseTemplateOutPath + "/" + template.replace(SystemConstant.TEMPLATE_DIR_NAME + "/", "").replace(".vm", "");
            FileUtils.saveAsFileWriter(outPath, sw.toString());
        }
    }
}
