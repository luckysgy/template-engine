package com.easydeploy.context;

import com.easydeploy.utils.FileUtils;

/**
 * @author shenguangyang
 * @date 2021-11-11 19:50
 */
public class ApplicationContext {
    /**
     * 目标工程的根目录
     */
    private String targetProjectRootPath;

    private String templateOutPut;

    public ApplicationContext(String targetProjectRootPath) {
        this.targetProjectRootPath = FileUtils.winToLinuxForPath(targetProjectRootPath);
        if (this.targetProjectRootPath.endsWith("/")) {
            this.targetProjectRootPath = this.targetProjectRootPath.substring(0, this.targetProjectRootPath.lastIndexOf("/"));
        }
    }

    public String getTemplateOutPut() {
        return templateOutPut;
    }

    public String getTargetProjectRootPath() {
        return targetProjectRootPath;
    }

    public String createParseTemplateOutPath(String templateOutPut) {
        this.templateOutPut = templateOutPut;
        if (templateOutPut == null) {
            this.templateOutPut = "out";
        }

        if (!this.templateOutPut.startsWith("/")) {
            this.templateOutPut = this.targetProjectRootPath + "/" + this.templateOutPut;
        }
        return this.templateOutPut;
    }
}
