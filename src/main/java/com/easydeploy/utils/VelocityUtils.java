package com.easydeploy.utils;

import com.easydeploy.context.ApplicationContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.util.Properties;

/**
 * VelocityEngine工厂
 * 
 * @author shenguangyang
 */
public class VelocityUtils {
    /**
     * 初始化vm方法
     * @param applicationContext 应用上下文
     */
    public static void initVelocity(ApplicationContext applicationContext) {
        Properties p = new Properties();

        try {
            // 加载classpath目录下的vm文件
            //p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, applicationContext.getTargetProjectRootPath());
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
