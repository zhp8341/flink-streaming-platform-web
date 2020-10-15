## 一、简介 
  flink-streaming-platform-web系统是基于flink封装的一个web系统，用户只需在web界面进行sql配置就能完成流计算任务，
  主要功能包含任务配置、启/停任务、告警、日志等功能。目的是减少开发，完全实现flink-sql 流计算任务
  
## 二、环境以及安装


### 1、环境
flink 版本 1.10.0  官方地址: https://ci.apache.org/projects/flink/flink-docs-release-1.10/

java 版本 jdk1.8

scala版本 2.11  

kafka版本 1.0+


### 2、应用安装





## 三、功能介绍

### 1、新增配置说明

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
| checkpointDir      |    |   保存地址 如  hdfs://hcluster/flink/checkpoints/   | 
| tolerableCheckpointFailureNumber      |  1  |    设置失败次数 默认一次    |
| asynchronousSnapshots      |  true 或者 false  |     是否异步  |



e: udf地址
~~~~
udf地址 只支持http并且填写一个 
 如： http://xxx.xxx.com/flink-streaming-udf.jar 具体参考//TODO 地址
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



