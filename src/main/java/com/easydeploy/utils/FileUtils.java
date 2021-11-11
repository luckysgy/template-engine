package com.easydeploy.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件处理工具类
 * 
 * @author shenguangyang
 */
public class FileUtils {
    /** 字符常量：斜杠 {@code '/'} */
    public static final char SLASH = '/';

    /** 字符常量：反斜杠 {@code '\\'} */
    public static final char BACKSLASH = '\\';

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 创建文件夹
     * @param dirPath 文件夹目录
     */
    public static void mkdirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("dir mkdirs fail: " + dirPath);
            }
        }
    }

    /**
     * 获取路径下的所有文件/文件夹
     * @param directoryPath 需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @param excludeFilePathRegs 排除的文件路径正则表达式  eg:  /abc/** ===> /abc/[^/]*
     * @return
     */
    public static List<String> getAllFile(String directoryPath, boolean isAddDirectory, List<String> excludeFilePathRegs) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        assert files != null;
        for (File file : files) {
            String path = file.getPath();
            if (checkExclude(path,excludeFilePathRegs)) {
                continue;
            }
            if (file.isDirectory()) {
                if(isAddDirectory){
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllFile(file.getAbsolutePath(), isAddDirectory, excludeFilePathRegs));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

    /**
     * 将win路径转成linux路径格式
     * eg: E:\mnt\test ===> /mnt/test
     * @param winPath win路径
     * @return linux路径格式
     */
    public static String winToLinuxForPath(String winPath) {
        if (winPath.contains(":")) {
            return winPath.substring(2).replace("\\", "/");
        }
        return winPath;
    }

    public static List<String> winToLinuxForPath(List<String> winPaths) {
        List<String> linuxPaths = new ArrayList<>();
        for (String winPath : winPaths) {
            linuxPaths.add(winToLinuxForPath(winPath));
        }
        return linuxPaths;
    }

    /**
     * 校验是否排除
     * @param targetPath 目标路径  /abc/34/4353
     * @param excludeFilePathRegs 正则表达式集合 比如 /abc/[^/]* , /abc/.* , /[^/]*.do ...
     * @return true 排除  false 不排除
     */
    private static boolean checkExclude(String targetPath, List<String> excludeFilePathRegs) {
        if (excludeFilePathRegs == null || excludeFilePathRegs.size() == 0 ) {
            return false;
        }
        for (String excludeFilePathReg : excludeFilePathRegs) {
            if (Pattern.compile(excludeFilePathReg).matcher(targetPath).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 输出指定文件的byte数组
     * 
     * @param filePath 文件路径
     * @param os 输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 将文本保存成文件
     * @param filePath 文件路径
     * @param content 内容
     */
    public static void saveAsFileWriter(String filePath, String content) {
        FileWriter fwriter = null;
        try {
            content = content.replace("\r\n", "\n");
            String path = filePath.substring(0, filePath.lastIndexOf("/"));
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filePath);
            fwriter.write(content);

            File fileData = new File(filePath);
            fileData.setReadable(true, false);
            fileData.setExecutable(true, false);
            fileData.setWritable(true, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert fwriter != null;
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     * 
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }
}
