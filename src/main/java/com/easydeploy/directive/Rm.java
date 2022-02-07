package com.easydeploy.directive;

import com.easydeploy.constant.DirectiveConstant;
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


/**
 * 安全删除当前目录文件
 * @author shenguangyang
 * @date 2022-02-06 8:22
 */
public class Rm extends Directive {

    @Override
    public String getName() {
        return DirectiveConstant.RM;
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String outDirRelativePath = context.getCurrentTemplateName().replaceFirst(SystemConstant.TEMPLATE_DIR_NAME, SystemConstant.TEMPLATE_OUT_PATH);
        outDirRelativePath = outDirRelativePath.substring(0, outDirRelativePath.lastIndexOf("/"));
        String outDir = ApplicationContext.targetProjectRootPath + "/" + outDirRelativePath;
        Node node1 = node.jjtGetChild(0);
        String deleteFilePath = "";
        if (node1 != null) {
            deleteFilePath = node1.literal();
            if (deleteFilePath != null && !deleteFilePath.equals("")) {
                deleteFilePath = deleteFilePath.replace("\"", "");
                //将结果写入到writer中，相当于把结果输出
                writer.write("rm -rf " + outDir + "/" + deleteFilePath  + "\n");
            }
        }
        return true;
    }
}
