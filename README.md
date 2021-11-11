# 让部署变得简单

# 内置变量

ed.dataPath: 数据路径, 存在与项目的根目录下且文件夹名为data
ed.rootPath: 项目根路径
```text
$ed.dataPath
$ed.rootPath
```

# 要部署项目的目录结构

```text
data: 存放你部署时候需要的数据
template: 存放模板数据, 每个文件必须以.vm结尾
value: 存放模板文件中所有的变量值
easy-deploy.yaml: 系统配置文件, 必须有
```