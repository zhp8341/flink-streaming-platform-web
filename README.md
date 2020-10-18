## 一、简介 
  flink-streaming-platform-web系统是基于flink封装的一个可视化的web系统，用户只需在web界面进行sql配置就能完成流计算任务，
  主要功能包含任务配置、启/停任务、告警、日志等功能。目的是减少开发，完全实现flink-sql 流计算任务
  效果图
 

  
## 二、环境以及安装


### 1、环境

操作系统：linux

hadoop版本 2+ 

flink 版本 1.10.0  官方地址: https://ci.apache.org/projects/flink/flink-docs-release-1.10/

jdk版本 jdk1.8

scala版本 2.11  

kafka版本 1.0+

mysql版本 5.6+


### 2、应用安装

#### 1、flink客户端安装

下载对应版本 
https://archive.apache.org/dist/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz 然后解压


a: /flink-1.10.0/conf  文件下面放入hadoop客户端配置文件

`
core-site.xml 
yarn-site.xml 
hdfs-site.xml
`

`
修改flink-conf.yaml 开启 classloader.resolve-order 并且设置  classloader.resolve-order: parent-first
`

b: /flink-1.10.0/lib  hadoop集成

~~~~
下载 flink-shaded-hadoop-2-uber-${xxx}.jar 到lib 
地址  https://repo.maven.apache.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.7.5-10.0/flink-shaded-hadoop-2-uber-2.7.5-10.0.jar
完毕后执行  export HADOOP_CLASSPATH=`hadoop classpath`
~~~~

#### 2、flink-streaming-platform-web安装

a:下载解压 //TODO 下载地址

~~~~
 tar -xvf   flink-streaming-platform-web.tar.gz

~~~~


b:执行mysql语句

~~~~

mysql 版本5.6+以上

 创建数据库 数据库名：flink_web
 
 执行表语句
 语句地址 https://github.com/zhp8341/flink-streaming-platform-web/blob/master/docs/sql/flink_web.sql

~~~~

c:修改数据库连接配置

~~~~
/flink-streaming-platform-web/conf/application.properties  
改成上面建好的mysql地址
~~~~


d:启动web

~~~~
cd  /XXXX/flink-streaming-platform-web/bin 


启动 : sh deploy.sh  start

停止 :  sh deploy.sh  stop

日志目录地址： /XXXX/flink-streaming-platform-web/logs/

~~~~


e:登录

~~~~
http://${ip或者hostname}:9084/  如 : http://hadoop003:9084/


登录号：admin  密码 123456

~~~~


f:登录

集群部署模式网络图



## 三、功能介绍

### 1、新增任务配置说明

a: 任务名称（*必选）
~~~~
任务名称不能超过50个字符 并且 任务名称仅能含数字,字母和下划线
 ~~~~
 
b: 运行模式（*必选）暂时支持YARN_PER


c: flink运行配置（*必选） 
~~~~
参数（和官方保持一致）但是只支持 -p -yjm -yn -ytm -ys -yqu(必选)  
 -ys slot个数。
 -yn task manager 数量。
 -yjm job manager 的堆内存大小。
 -ytm task manager 的堆内存大小。
 -yqu yarn队列明
 -p 并行度
 详见官方文档
如： -yqu flink   -yjm 1024m -ytm 2048m  -p 1  -ys 1
~~~~

d: Checkpoint信息
~~~~
不填默认不开启checkpoint机制 参数只支持 -checkpointInterval -checkpointingMode -checkpointTimeout -checkpointDir -tolerableCheckpointFailureNumber -asynchronousSnapshots 
如：  -asynchronousSnapshots true  -checkpointDir   hdfs://hcluster/flink/checkpoints/

~~~~

| 参数        | 值   |  说明  |
| --------   | -----:  | :----:  |
| checkpointInterval      |  整数 （如 1000）  |   默认每60s保存一次checkpoint  单位毫秒   |
| checkpointingMode      |  EXACTLY_ONCE  或者 AT_LEAST_ONCE  |     一致性模式 默认EXACTLY_ONCE  单位字符  |
| checkpointTimeout      |   6000|      默认超时10 minutes 单位毫秒|
| checkpointDir      |    |   保存地址 如  hdfs://hcluster/flink/checkpoints/ 注意目录权限   | 
| tolerableCheckpointFailureNumber      |  1  |    设置失败次数 默认一次    |
| asynchronousSnapshots      |  true 或者 false  |     是否异步  |



e: udf地址
~~~~
udf地址 只支持http并且填写一个 
 如： http://xxx.xxx.com/flink-streaming-udf.jar 
~~~~
f: udf注册名称
~~~~
 多个可用;分隔 
 utc2local代表udf函数名称 com.streaming.flink.udf.UTC2Local代表类名 中间用|分隔

 如多个写法:
  utc2local|com.streaming.flink.udf.UTC2Local;
  utc2local2|com.streaming.flink.udf.UTC2Local2;
  utc2local3|com.streaming.flink.udf.UTC2Local3;
~~~~



### 2、系统设置

~~~~

    系统设置有三个必选项
    1、flink-streaming-platform-web应用安装的目录（必选）  -- 这个问应用的安装目录 如 /root/flink-streaming-platform-web/
    2、flink安装目录（必选）  --flink客户端的目录 如： /usr/local/flink-1.10.0/
    3、yarn的rm Http地址（必选） --hadoop yarn的rm Http地址  http://hadoop003:8088/
    
~~~~

### 3、报警设置

~~~~
    报警设置用于: 当运行的任务挂掉的时候会告警
    
    
    安全设置 关键词必须填写：告警
    
    
    资料：钉钉报警设置官方文档：https://help.aliyun.com/knowledge_detail/106247.html
    
~~~~
效果图
