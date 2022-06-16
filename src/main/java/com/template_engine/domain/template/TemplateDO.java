package com.template_engine.domain.template;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.template_engine.constant.SystemConstant;
import com.template_engine.domain.ApplicationContext;
import com.template_engine.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenguangyang
 * @date 2022-06-16 20:33
 */
public class TemplateDO {
    /**
     * 历史的模板文件名集合, 存在项目的根目录下 {@link SystemConstant#CACHE_DIR_NAME}
     */
    private static final String HISTORY_TEMPLATE_FILE_NAMES= "template_file_names.json";
    /**
     * 存放模板文件路径list
     */
    private final List<String> templateFilePathList = new ArrayList<>(16);

    public synchronized List<String> templateFilePathList() throws Exception {
        // 如果不为空说明已经被解析过, 直接返回结果
        if (!templateFilePathList.isEmpty()) {
            return templateFilePathList;
        }
        if (StringUtils.isEmpty(ApplicationContext.targetProjectTemplatePath)) {
            throw new RuntimeException("template path is null");
        }

        // 从缓存中获取旧的数据, 目前没啥用
        String cachePath = ApplicationContext.targetProjectRootPath + "/" + SystemConstant.CACHE_DIR_NAME + "/" + HISTORY_TEMPLATE_FILE_NAMES;
        File file = new File(cachePath);
        List<String> cacheTemplatePathLists = new ArrayList<>();
        if (file.exists()) {
            cacheTemplatePathLists = new Gson().fromJson(new FileReader(file), new TypeToken<List<String>>() {}.getType());
        }

        // 获取一遍模板list集合
        List<String> newData = FileUtils.getAllFile(ApplicationContext.targetProjectTemplatePath, false, null);
        for (String item : newData) {
            // 筛选出模板文件
            if (StringUtils.isNotEmpty(item) &&
                    ( item.endsWith(SystemConstant.TEMPLATE_SUFFIX__VM) || item.endsWith(SystemConstant.TEMPLATE_SUFFIX__TE_VM) )) {
                templateFilePathList.add(item);
                System.out.println(item);
            }
        }
        FileUtils.saveAsFileWriter(cachePath, new Gson().toJson(templateFilePathList));

        return templateFilePathList;
    }
}
