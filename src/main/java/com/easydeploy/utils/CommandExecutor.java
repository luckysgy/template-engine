package com.easydeploy.utils;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for ProcessBuilder that can be overridden easily for frameworks like Gradle that don't support it well.
 *
 * @author shenguangyang
 */
public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    /**
     * Executes a command with {@link ProcessBuilder}, but also logs the call
     * and redirects its input and output to our process.
     *
     * 解决乱码问题:
     * win10: java -jar -Dfile.encoding=UTF-8
     * linux: java -jar -Dfile.encoding=gbk
     *
     * @param cmd to have {@link ProcessBuilder} execute
     *        linux: String[] cmd = {"/bin/bash", "-c", "sh build.sh && cd ../cpp-base && sh build.sh"};
     *        win10: String[] cmd = {"cmd.exe", "/c", "ping www.baidu.com"};
     *               cmd /c dir 是执行完dir命令后关闭命令窗口。
     *               cmd /k dir 是执行完dir命令后不关闭命令窗口。
     *               cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
     *               cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
     * @param workingDirectory to pass to {@link ProcessBuilder#directory()}
     *        可以理解为: 进入到系统的指定目录下执行cmd命令
     * @param environmentVariables to put in {@link ProcessBuilder#environment()}
     * @return the exit value of the command
     */
    public boolean executeCommand(String[] cmd, File workingDirectory,
                              Map<String,String> environmentVariables) throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        boolean windows = OSInfo.isWindows();
        initCommand(windows, command);
        printfCommand(windows, command);

        Process process = null;
        try {
            ProcessBuilder pb = createProcessBuilder(command, workingDirectory, environmentVariables);
            process = pb.inheritIO().start();
            return process.waitFor() == 0;
        } finally {
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }
    }


    /**
     * Executes a command with {@link ProcessBuilder}, but also logs the call
     * and redirects its input and output to our process, and return result
     *
     * 等待执行命令执行完成之后, 将结果返回
     *
     * 解决乱码问题:
     * win10: java -jar -Dfile.encoding=UTF-8
     * linux: java -jar -Dfile.encoding=gbk
     * <code>
     *     String[] cmd = {"/bin/bash", "-c", "sh build.sh && cd ../cpp-base && sh build.sh"};
     *     CommandExecutor commandExecutor = new CommandExecutor();
     *     Map<String, String> environmentVariables = System.getenv();
     *     CommandExecutor.Result result = commandExecutor.executeCommandAndReturnResult(cmd, new File("/mnt/project/javacpp-native/cpp-project"), environmentVariables);
     *     if (result.isSuccess()) {
     *         log.info("exec success: \n{}", result.getSuccessResult());
     *     } else {
     *         log.error("exec fail: \n{}", result.getErrorResult());
     *
     *     if (result.hasWarn()){
     *         log.warn("exec warn: \n{}", result.getWarnResult());
     *     }
     * </code>
     * @param cmd to have {@link ProcessBuilder} execute
     *        linux: String[] cmd = {"/bin/bash", "-c", "sh build.sh && cd ../cpp-base && sh build.sh"};
     *        win10: String[] cmd = {"cmd.exe", "/c", "ping www.baidu.com"};
     *               cmd /c dir 是执行完dir命令后关闭命令窗口。
     *               cmd /k dir 是执行完dir命令后不关闭命令窗口。
     *               cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
     *               cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
     * @param workingDirectory to pass to {@link ProcessBuilder#directory()}
     *        可以理解为: 进入到系统的指定目录下执行cmd命令
     * @param environmentVariables to put in {@link ProcessBuilder#environment()}
     * @return the exit value of the command
     */
    public Result executeCommandAndReturnResult(String[] cmd, File workingDirectory,
                                  Map<String,String> environmentVariables) throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        boolean windows = OSInfo.isWindows();
        initCommand(windows, command);
        printfCommand(windows, command);

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        StringBuilder successCommandExecResult = new StringBuilder();
        StringBuilder errorCommandExecResult = new StringBuilder();
        try {
            ProcessBuilder pb = createProcessBuilder(command, workingDirectory, environmentVariables);
            process = pb.redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE).start();

            String charset;
            if (windows) {
                charset = "gbk";
            } else {
                charset = "utf-8";
            }

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));

            boolean isSuccess = process.waitFor() == 0;

            // 读取输出
            String line;
            while ((line = bufrIn.readLine()) != null) {
                successCommandExecResult.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                errorCommandExecResult.append(line).append('\n');
            }

            return new Result(
                    isSuccess,
                    successCommandExecResult.substring(0, successCommandExecResult.lastIndexOf("\n")),
                    errorCommandExecResult.substring(0, errorCommandExecResult.lastIndexOf("\n")));
        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static class Result {
        private final Boolean success;
        private final String successResult;
        private String errorResult;
        private String warnResult;

        public Result(Boolean success, String successResult, String errorResult) {
            this.success = success;
            this.successResult = successResult;

            // 如果执行成功, 那不会有错误结果, 但= new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
            // 很有可能返回警告消息, 而非错误消息
            if (!this.success) {
                this.errorResult = errorResult;
            } else {
                this.warnResult = errorResult;
            }
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean hasWarn() {
            return StringUtils.isNotEmpty(warnResult);
        }

        public Boolean getSuccess() {
            return success;
        }

        public String getSuccessResult() {
            return successResult;
        }

        public String getErrorResult() {
            return errorResult;
        }

        public void setErrorResult(String errorResult) {
            this.errorResult = errorResult;
        }

        public String getWarnResult() {
            return warnResult;
        }

        public void setWarnResult(String warnResult) {
            this.warnResult = warnResult;
        }
    }


    public void initCommand(boolean windows, List<String> command) {
        for (int i = 0; i < command.size(); i++) {
            String arg = command.get(i);
            if (arg == null) {
                arg = "";
            }
            if (arg.trim().isEmpty() && windows) {
                // seems to be the only way to pass empty arguments on Windows?
                arg = "\"\"";
            }
            command.set(i, arg);
        }
    }

    public void printfCommand(boolean windows, List<String> command) {
        StringBuilder text = new StringBuilder();
        for (String s : command) {
            boolean hasSpaces = s.indexOf(" ") > 0 || s.isEmpty();
            if (hasSpaces) {
                text.append(windows ? "\"" : "'");
            }
            text.append(s);
            if (hasSpaces) {
                text.append(windows ? "\"" : "'");
            }
            text.append(" ");
        }
        log.info(text.toString());
    }

    public ProcessBuilder createProcessBuilder(List<String> command, File workingDirectory,
                                               Map<String,String> environmentVariables) {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        if (environmentVariables != null) {
            for (Map.Entry<String,String> e : environmentVariables.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    pb.environment().put(e.getKey(), e.getValue());
                }
            }
        }
        return pb;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }
}