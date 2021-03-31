#!/usr/bin/env bash


#具体执行哪个步骤
ACTION=$1

echo "开始执行脚本 启动参数 $1"

source /etc/profile
set -e

# Find the java binary
if [ -n "${JAVA_HOME}" ]; then
  JAVA_RUN="${JAVA_HOME}/bin/java"
else
  if [ `command -v java` ]; then
    JAVA_RUN="java"
  else
    echo "JAVA_HOME is not set" >&2
    exit 1
  fi
fi


echo "JAVA_HOME= ${JAVA_HOME}"

##变量设置##
env=prod
project=$(find "../lib" -regex ".*flink-streaming-web.*.jar")
echo "project= $project"
time=$(date "+%Y%m%d-%H%M%S")

##JAVA_OPTS设置
JAVA_OPTS="-Xmx1888M -Xms1888M -Xmn1536M -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseConcMarkSweepGC -Xdebug -Xrunjdwp:transport=dt_socket,address=9901,server=y,suspend=n  -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Dcom.sun.management.jmxremote.port=8999 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:ErrorFile=../logs/hs_err_pid%p.log  -XX:HeapDumpPath=../logs -XX:+HeapDumpOnOutOfMemoryError"

start(){
     echo "开始启动服务 app_name=$project "
         pid=$(ps x | grep $project  | grep -v grep | awk '{print $1}')
         echo $pid
     if [ -z $pid ]
     then

         echo "开始启动进程执行命令  java $JAVA_OPTS   -jar $project --spring.profiles.active=$env --spring.config.additional-location=../conf/application.properties  "
        
          java $JAVA_OPTS   -jar $project --spring.profiles.active=$env --spring.config.additional-location=../conf/application.properties      >/dev/null 2>&1  &
          sleep 20
          pid=$(ps x | grep $project  | grep -v grep | awk '{print $1}')

           if [ -z $pid ]
           then
              echo "启动应用进程失败 请手动执行一下  java  -jar $project --spring.profiles.active=$env --spring.config.additional-location=../conf/application.properties  "
           else
              echo "启动成功 pid=" $pid
           fi

           echo "可通过命令  tail -fn 300  ../logs/info.log  查看web日志"

     else
      echo " $project 进程已经存 pid=" $pid
     fi


}

stop()
{
pid=$(ps x | grep $project  | grep -v grep | awk '{print $1}')
echo "进程 $pid"

echo "------>Check pid of $project"

if [ -z "$pid" ]
then
    echo "------>APP_NAME process [$project] is already stopped"
else
    for pid in ${pid[*]}
    do
      echo "------>Kill process which pid=$pid"
      /bin/kill $pid
    done
        sleep  30

fi



}

restart()
{
  stop;
  start;
}

case "$ACTION" in

    restart)
    cp $project  $project$time
       restart
    ;;
    start)
       start
    ;;

    stop)
        stop
    ;;
esac
