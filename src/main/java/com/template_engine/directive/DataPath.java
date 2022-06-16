package com.template_engine.directive;

import com.template_engine.constant.DirectiveConstant;
import com.template_engine.constant.SystemConstant;
import com.template_engine.domain.ApplicationContext;
import com.template_engine.utils.FileUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * 仅供参考
 * @deprecated
 * @author shenguangyang
 * @date 2021-11-13 7:34
 */
public class DataPath extends Directive {
    /**
     * 相对根路径
     * @param absolutePath 传入绝对路径
     * @return 相对于根路径的相对路径
     */
    public String dataPath(String absolutePath) {
        return FileUtils.returnRootPath(ApplicationContext.targetProjectRootPath, absolutePath) + "/" + SystemConstant.DATA_DIR_NAME;
    }

    @Override
    public String getName() {
        //指令名称，也就是在模板中使用的指令名字
        return DirectiveConstant.DATA_PATH_NAME;
    }

    /**
     * getType:当前有LINE,BLOCK两个值，line行指令，不要end结束符，block块指令，需要end结束符
     * @return
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String templatePath = ApplicationContext.targetProjectRootPath + "/" + context.getCurrentTemplateName();
        //将结果写入到writer中，相当于把结果输出
        writer.write(dataPath(templatePath));
        return true;
    }
}
