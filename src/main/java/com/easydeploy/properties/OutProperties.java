package com.easydeploy.properties;

/**
 * @author shenguangyang
 * @date 2021-11-11 20:47
 */
public class OutProperties {
    private String path;
    /**
     * 输出的文件是否只读
     */
    private Boolean isOnlyRead = true;

    public Boolean getOnlyRead() {
        return isOnlyRead;
    }

    public void setOnlyRead(Boolean onlyRead) {
        isOnlyRead = onlyRead;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
