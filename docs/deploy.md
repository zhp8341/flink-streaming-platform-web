
### 1、环境

操作系统：linux  **(暂时不支持win系统)**

hadoop版本 2+ 

**flink 版本 1.12.0**  官方地址: https://ci.apache.org/projects/flink/flink-docs-release-1.12/

jdk版本 jdk1.8

scala版本 2.11  

kafka版本 1.0+

mysql版本 5.6+



**如果有flink版本需要可以自己编译 详见下面源码编译**

### 2、应用安装

#### 1、flink客户端安装

下载对应版本 
https://www.apache.org/dyn/closer.lua/flink/flink-1.15.3/flink-1.15.3-bin-scala_2.11.tgz 然后解压


a: /flink-1.15.3/conf  

**1、YARN_PER模式** 

文件下面放入hadoop客户端配置文件
配置hadoop客户端环境 （HADOOP_CLASSPATH 环境变量）


~~~~
core-site.xml 
yarn-site.xml 
hdfs-site.xml
~~~~

**2、LOCAL模式** 


无


**3、STANDALONE模式**

无

**3、yarn-Application模式**

无



以上三种模式都需要修改  **flink-conf.yaml**   开启 classloader.resolve-order 并且设置   

**classloader.resolve-order: parent-first**



b: /flink-1.12.0/lib  hadoop集成



~~~~
下载 flink-shaded-hadoop-2-uber-${xxx}.jar 到lib 
地址  https://repo.maven.apache.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.7.5-10.0/flink-shaded-hadoop-2-uber-2.7.5-10.0.jar
~~~~


**完毕后执行  export HADOOP_CLASSPATH


~~~~
 export HADOOP_CLASSPATH=`hadoop classpath`

~~~~

#### 2、flink-streaming-platform-web安装



  ##### a:**下载最新版本** 并且解压 https://github.com/zhp8341/flink-streaming-platform-web/releases/

~~~~
 tar -xvf   flink-streaming-platform-web.tar.gz

~~~~


 ##### b:执行mysql语句

~~~~

mysql 版本5.6+以上

 创建数据库 数据库名：flink_web
 
 执行表语句
 语句地址 https://github.com/zhp8341/flink-streaming-platform-web/blob/master/docs/sql/flink_web.sql

~~~~

  ##### c:修改数据库连接配置

~~~~
/flink-streaming-platform-web/conf/application.properties  
改成上面建好的mysql地址
~~~~

**关于数据库连接配置 需要看清楚你 useSSL=true 你的mysql是否支持 如果不支持可以直接 useSSL=false**


  ##### d:启动web

~~~~
cd  /XXXX/flink-streaming-platform-web/bin 



启动 : sh deploy.sh  start

停止 :  sh deploy.sh  stop

日志目录地址： /XXXX/flink-streaming-platform-web/logs/

~~~~


**一定 一定 一定 要到bin目录下再执行deploy.sh  否则无法启动**


  ##### e:登录

~~~~
http://${ip或者hostname}:9084/  如 : http://hadoop003:9084/admin/index


登录号：admin  密码 123456

~~~~


  ##### f:集群

如果需要集群部署模式 简单参考图

![图片](https://img-blog.csdnimg.cn/20201018111339635.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pocDgzNDE=,size_16,color_FFFFFF,t_70#pic_center)




 **备注：flink客户端必须和flink-streaming-platform-web应用部署在同一服务器**


  ##### g:端口/内存修改

 web端口修改 在conf下面的 application.properties 

 **server.port参数 默认是9084**

 jmx端口 在启动脚本 deploy.sh 里面

 **默认是8999**

 **debug端口 9901**


jvm内存修改 都在deploy.sh

**默认是按2G物理内存在分配jvm的  -Xmx1888M -Xms1888M -Xmn1536M -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M**



