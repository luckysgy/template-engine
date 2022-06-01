package com.template_engine.directive;

import com.template_engine.constant.DirectiveConstant;
import com.template_engine.constant.SystemConstant;
import com.template_engine.context.ApplicationContext;
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

                // 去掉首尾空格
                String regStartSpace = "^[　 ]*";
                String regEndSpace = "[　 ]*$";

                // 第一个是去掉前端的空格， 第二个是去掉后端的空格
                deleteFilePath = deleteFilePath.replaceAll(regStartSpace, "").replaceAll(regEndSpace, "");
                if (deleteFilePath.contains(" ")) {
                    for (String filePath : deleteFilePath.split(" ")) {
                        //将结果写入到writer中，相当于把结果输出
                        writer.write("rm -rf " + outDir + "/" + filePath  + "\n");
                    }
                } else {
                    //将结果写入到writer中，相当于把结果输出
                    writer.write("rm -rf " + outDir + "/" +  deleteFilePath + "\n");
                }

            }
        }
        return true;
    }
}
