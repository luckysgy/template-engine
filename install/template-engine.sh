#! /bin/sh

cur_exec_dir=$(pwd)

java -Dfile.encoding=utf-8 -jar /usr/bin/template-engine-jar-with-dependencies.jar -cd $cur_exec_dir $*