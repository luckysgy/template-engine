#! /bin/sh
# 老版本的getopt问题较多，增强版getopt比较好用，执行命令getopt -T; echo $?，如果输出4，则代表是增强版的

cur_exec_dir=$(pwd)

#用法提示
usage() {
    echo "Usage:"
    echo "  ed [-j JUMP] [-h HELP]"
    echo "Description:"
    echo "    JUMP: 跳转到工作目录 [v (value) | t (template) | d (data) | o (out)]"
    echo "    HELP: 查看帮助信息"
}


#-o或--options选项后面是可接受的短选项，如ab:c::，表示可接受的短选项为-a -b -c，
#其中-a选项不接参数，-b选项后必须接参数，-c选项的参数为可选的
#-l或--long选项后面是可接受的长选项，用逗号分开，冒号的意义同短选项。
#-n选项后接选项解析错误时提示的脚本名字
ARGS=`getopt -o j:h:: --long jump:,help -n "$0" -- "$@"`

if [ $? != 0 ]; then
  echo "Terminating..."
  return
fi

#将规范化后的命令行参数分配至位置参数（$1,$2,...)
eval set -- "${ARGS}"

isParseTemplate="false"
while true; do
    case $1 in
        -h|--help)
          usage
          return 1
          ;;
        -j|--jump)
          JUMP_WORK_DIR=$2;
          shift 2
          break
          ;;
        --)
          isParseTemplate="true"
          break
          ;;
        *)
          usage
          return 1
          ;;
    esac
done

easyDeployRootDir=$(java -Dfile.encoding=utf-8 -jar /usr/bin/easy-deploy-jar-with-dependencies.jar -cd $cur_exec_dir -pt ${isParseTemplate} | tail -1)

# 跳转目录
if [ "$JUMP_WORK_DIR" = "t" ]; then
  cd ${easyDeployRootDir}/template
  echo "jump to template"
elif [ "$JUMP_WORK_DIR" = "v" ]; then
  cd ${easyDeployRootDir}/value
  echo "jump to value"
elif [ "$JUMP_WORK_DIR" = "o" ]; then
  cd ${easyDeployRootDir}/out
  echo "jump to out"
elif [ "$JUMP_WORK_DIR" = "d" ]; then
  cd ${easyDeployRootDir}/data
  echo "jump to data"
fi

echo "easy-deploy root dir is "$easyDeployRootDir