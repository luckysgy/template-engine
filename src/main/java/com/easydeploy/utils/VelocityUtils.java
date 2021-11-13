package com.easydeploy.utils;

import com.easydeploy.context.ApplicationContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.reflections.Reflections;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * VelocityEngine工厂
 * 
 * @author shenguangyang
 */
public class VelocityUtils {
    /**
     * 初始化vm方法
     */
    public static void initVelocity() {
        Properties p = new Properties();

        try {
            // 加载classpath目录下的vm文件
            //p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, ApplicationContext.targetProjectRootPath);
            p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            loadCustomDirective("com.easydeploy.directive", p);
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadCustomDirective(String packageName, Properties properties) {
        // 指定扫描的包名
        Reflections reflections = new Reflections(packageName);
        //component是个接口，获取在指定包扫描的目录所有的实现类
        String classNames = "";
        Set<Class<? extends Directive>> classes = reflections.getSubTypesOf(Directive.class);
        for (Class<? extends Directive> aClass : classes) {
            //遍历执行
            try {
                classNames = classNames + aClass.getName() + ",";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        classNames = classNames.substring(0, classNames.lastIndexOf(","));
        //这一步很重要，要把自己写的类的全名加到velocity的配置中去。
        properties.setProperty("userdirective", classNames);
    }
}
