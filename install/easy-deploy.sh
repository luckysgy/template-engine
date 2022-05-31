#! /bin/sh

cur_exec_dir=$(pwd)

java -Dfile.encoding=utf-8 -jar /usr/bin/easy-deploy-jar-with-dependencies.jar -cd $cur_exec_dir -pt 1 -sp $$ $*