log="this is a log string"

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

function coloredlog()
{
    echo -e "\033[$1;$2m $3\033[0m"
}
function debug()
{
    echo -e "\033[37m$1\033[0m"
}
function info()
{
    echo -e "\033[32m$1\033[0m"
}
function warn()
{
    echo -e "\033[33m$1\033[0m"
}
function error()
{
    echo -e "\033[31m$1\033[0m"
}

#coloredlog $BG_GREEN $FG_RED "$log"
#debug "$log"
#info "$log"
# warn "$log"
#error "$log"
