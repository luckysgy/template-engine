package com.easydeploy.directive;

import com.easydeploy.constant.SystemConstant;
import com.easydeploy.context.ApplicationContext;
import com.easydeploy.utils.FileUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

import static com.easydeploy.constant.DirectiveConstant.SHELL_NAME;

/**
 * @author shenguangyang
 * @date 2021-11-13 7:34
 */
public class Shell extends Directive {


    /**
     * 相对根路径
     * @param absolutePath 传入绝对路径
     * @return 相对于根路径的相对路径
     */
    public String shellPath(String absolutePath) {
        return FileUtils.returnRootPath(ApplicationContext.targetProjectRootPath, absolutePath) + "/" + SystemConstant.SHELL_DIR_NAME;
    }

    @Override
    public String getName() {
        //指令名称，也就是在模板中使用的指令名字
        return SHELL_NAME;
    }

    /**
     * getType:当前有LINE,BLOCK两个值，line行指令，不要end结束符，block块指令，需要end结束符
     */
    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String templatePath = ApplicationContext.targetProjectRootPath + "/" + context.getCurrentTemplateName();
        Node node1 = node.jjtGetChild(0);
        String shellName = "";
        if (node1 != null) {
            shellName = node1.literal();
            if (shellName != null && !shellName.equals("")) {
                shellName = shellName.replace("\"", "");
                //将结果写入到writer中，相当于把结果输出
                writer.write(shellPath(templatePath) + "/" + shellName  + "\n");
                FileUtils.copyFileFromJar(SystemConstant.RESOURCES_DIR_SHELL + "/" + shellName, ApplicationContext.targetProjectShellPath + "/" + shellName);
            }
        }

        return true;
    }
}
