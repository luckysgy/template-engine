# 让部署变得简单

# 都有哪些功能

- 解析yaml文件将值传入到vm模板文件中
- 在value目录下的yaml文件可以通过 ${xxx.yyy} 的格式取当前yaml或其他yaml文件数据, 如果找不到会找系统环境变量
- 项目启动自动提示并创建部署项目的目录基本结构
- 内置相对路径指令以及绝对路径参数 (data / value / shell等项目根目录)
- 内置系统自带的方法， 比如时间方法, 字符串转整型, 遍历，更多信息请参考 (velocity官网或者百度找教程, 语法很简单)
- 可以调用java中的方法
- 内置一些脚本, 可以很方便的模板文件vm中指定具体想要调用哪个脚本, 解析之后会将脚本放在根目录下的shell目录中, 并且解析之后的文件是以相对路径找到脚本文件
- 新增可以使能指定value目录下的yaml文件生效
- 模板文件后缀应为`.vm` 或者 `ED.vm` 

# 关于模板文件的后缀问题

将模板文件转成输出文件时候, 程序会自动去掉 .vm 后缀 以及 ED.vm

为什么会有ED.vm后缀?
有时候原始文件没有后缀,那么您也许需要采用ED.vm作为后缀， 比如你的文件是Dockerfile, 即使你加上vm = Dockerfile.vm, vscode依旧会将其当成
Dockerfile文件, 这时候你需要采用ED.vm后缀, DockerfileED.vm, vscode这时会将其当成一个模板文件

# 安装

进入到install目录下, 执行

```sh
sh install.sh
```

如果你使用的是win10, 则使用git命令行执行`sh install.sh`

# 注意事项

在模板中从value中取的所有变量都是以ed开头

> 对于注释最好使用英文, 如果是中文会发生乱码以及缩进问题

# 要部署项目的目录结构

```text
data: 存放你部署时候需要的数据
outProperties: 存放模板数据, 每个文件必须以.vm结尾
shell: 存放本项目自带的脚本
value: 存放模板文件中所有的变量值
easy-deploy.yaml: 系统配置文件, 必须有
```

# 使用方法

当安装完本工程之后, 你需要创建一个需要将部署文件放入其中的文件夹, 此时文件夹是空的 (如果是win10, 需要安装git, 使用git命令行执行如下命令)

docker-rabbitmq 目录下是空的

```shell
PS D:\Git\mnt\data_disk_5000\deploy> cd d:\Git\mnt\data_disk_5000\deploy\docker-rabbitmq
PS D:\Git\mnt\data_disk_5000\deploy\docker-rabbitmq> bash 

sheng@DESKTOP-G30JSO0 MINGW64 /mnt/data_disk_5000/deploy/docker-rabbitmq (master)
$ ls

sheng@DESKTOP-G30JSO0 MINGW64 /mnt/data_disk_5000/deploy/docker-rabbitmq (master)
$ easy-deploy 
current exec dir: /mnt/data_disk_5000/deploy/docker-rabbitmq
current directory structure is incomplete, do you need to initialize(y/n): y

sheng@DESKTOP-G30JSO0 MINGW64 /mnt/data_disk_5000/deploy/docker-rabbitmq (master)
$ ls
data  easy-deploy.yaml  shell  outProperties  value
```

> 可以看到如果当前文件夹有缺失的文件或者文件夹会自动提示初始化完整的目录结构



接下来你需要在value文件夹中编写你的yaml文件, 在template文件夹中编写你的模板文件



easy-deploy.yaml

```yaml
app:
  name: project
  version: v202107
  env: dev

# 指定使能value目录下的哪些yaml文件, 默认不指定就是全部使能
#enableValues:
#  - demo.yaml

outProperties:
  outPath: out
```



# 内置变量

## 路径

ed.dataPath: 数据路径, 存在与项目的根目录下且文件夹名为data
ed.rootPath: 项目根路径
```text
$ed.dataPath
$ed.rootPath
```

以上路径变量输出的都是绝对路径: 但是有时我们为了方便将生成的目标文件移植到线上环境部署, 那就需要使用相对路径

> 请看内置自定义指令#路径指令

# 内置自定义指令

## 路径指令

```velocity
项目根路径
#rootPath 
项目根路径下的数据路径
#dataPath

```

## 脚本指令

```velocity
项目根路径下的脚本路径, 参数需要填入你想要使用的脚本名称
#shell("color-log.sh")
```

## 安全删除文件指令

新增安全删除文件命令

```vbscript
#rm("out目录下文件相对out路径")
```

仅限于删除out目录下的文件, 解析之后是删除的是文件全路径, 目的防止从配置文件中动态传入删除的文件路径, 如果用户不小心传入了 /, /usr, 那会造成毁灭性的灾难, 比如 rm -rf ${ed.name}, 如果name = /usr, 则会变成 rm -rf /usr



## 如何二次开发新增指令

只需要继承: `Directive` 类, 然后实现如下方法即可, 具体看代码实现

```java
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

/**
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
        return "dataPath";
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
```



> 项目中将所有自定义的指令都放在了 `com.easydeploy.directive` 包下



# 内置的脚本

## 内置的脚本如何使用

使用内置自定义的路径指令调用即可

```velocity
项目根路径下的脚本路径
source #shell("color-log.sh")

## 解析结果
source ../shell/color-log.sh
```

当对模板解析之后, 会将所有需要的脚本文件放入到项目的根路径下的shell文件夹中

## color-log.sh

作用: 输出不同颜色的打印日志

模板文件中调用:

```velocity
source #shell("color-log.sh")
```

当解析成功之后, 不能使用sh命令执行脚本, 需要使用bash命令执行脚本

```shell
bash start.sh
```





变量:

```shell
BOLD="01"             #加粗
UNDERLINE="04"        #下划线
BLINK="05"            #闪烁

#foreground color
FG_GREY="30"             #灰色
FG_RED="31"
FG_GREEN="32"
FG_YELLOW="33"
FG_BLUE="34"          #前景色蓝色
FG_VIOLET="35"        #紫色
FG_SKY_BLUE="36"
FG_WHITE="37"

#background color
BG_RED="41"
BG_GREEN="42"
BG_YELLOW="43"
BG_BLUE="44"
BG_VIOLET="45"
BG_SKYBLUE="46"
BG_WHITE="47"
```



函数:

```shell
coloredlog $BG_GREEN $FG_RED "$log"
debug "$log"
info "$log"
warn "$log"
error "$log"
```
