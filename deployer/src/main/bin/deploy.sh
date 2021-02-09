#!/usr/bin/env bash


source /etc/profile
set -e

export SQL_HOME="$(cd "`dirname "$0"`"/..; pwd)"

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

#具体执行哪个步骤
ACTION=$1

##变量设置##
app_name=flink-streaming-web-1.2.0.RELEASE.jar
env=prod
project=../lib/$app_name
time=$(date "+%Y%m%d-%H%M%S")

##JAVA_OPTS设置
JAVA_OPTS="-Xmx1888M -Xms1888M -Xmn1536M -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseConcMarkSweepGC -Xdebug -Xrunjdwp:transport=dt_socket,address=9901,server=y,suspend=n  -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Dcom.sun.management.jmxremote.port=8999 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:ErrorFile=../logs/hs_err_pid%p.log  -XX:HeapDumpPath=../logs -XX:+HeapDumpOnOutOfMemoryError"

start(){
     echo "开始启动服务 app_name=$app_name "
         pid=$(ps x | grep $app_name  | grep -v grep | awk '{print $1}')
         echo $pid
     if [ -z $pid ]
     then
         echo "开始启动进程 $app_name "
          java $JAVA_OPTS   -jar $project --spring.profiles.active=$env --spring.config.additional-location=../conf/application.properties      >/dev/null 2>&1  &
          sleep 10
          pid=$(ps x | grep $app_name  | grep -v grep | awk '{print $1}')
     else

      echo " $app_name 进程已经存 pid=" $pid
     fi

    echo "Start java end pid=" $pid
}

stop()
{
pid=$(ps x | grep $app_name  | grep -v grep | awk '{print $1}')
echo $pid

echo "------>Check pid of $app_name"

if [ -z "$pid" ]
then
    echo "------>APP_NAME lication [$app_name] is already stopped"
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
