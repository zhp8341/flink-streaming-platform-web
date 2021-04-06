
### 1、新增任务配置说明

a: 任务名称（*必选）
~~~~
任务名称不能超过50个字符 并且 任务名称仅能含数字,字母和下划线
~~~~

b: 运行模式

   YARN_PER( yarn独立模式 https://ci.apache.org/projects/flink/flink-docs-release-1.11/zh/ops/deployment/yarn_setup.html#run-a-single-flink-job-on-yarn)


   STANDALONE（独立集群 https://ci.apache.org/projects/flink/flink-docs-release-1.11/zh/ops/deployment/cluster_setup.html）


   LOCAL(本地集群 https://ci.apache.org/projects/flink/flink-docs-release-1.11/zh/ops/deployment/local.html )


   <font color=red size=5>LOCAL 需要在本地单机启动flink 服务  ./bin/start-cluster.sh </font>


c: flink运行配置

<font color=red size=5>1、YARN_PER模式 </font>

~~~~

参数（和官方保持一致）但是只支持 -yD -p -yjm -yn -ytm -ys -yqu(必选)  
 -ys slot个数。
 -yn task manager 数量。
 -yjm job manager 的堆内存大小。
 -ytm task manager 的堆内存大小。
 -yqu yarn队列明
 -p 并行度
 -yD 如-yD  taskmanager.heap.mb=518
 详见官方文档
如： -yqu flink   -yjm 1024m -ytm 2048m  -p 1  -ys 1

~~~~

<font color=red size=5>2、LOCAL模式 </font>
~~~~
无需配置
~~~~


<font color=red size=5>3、STANDALONE模式 </font>
~~~~
-d,--detached                        If present, runs the job in detached
                                          mode

-p,--parallelism <parallelism>       The parallelism with which to run the
                                          program. Optional flag to override the
                                          default value specified in the
                                          configuration.

-s,--fromSavepoint <savepointPath>   Path to a savepoint to restore the job
                                          from (for example
                                          hdfs:///flink/savepoint-1537).

其他运行参数可通过 flink -h查看
~~~~




d: Checkpoint信息
~~~~
不填默认不开启checkpoint机制 参数只支持 
-checkpointInterval 
-checkpointingMode 
-checkpointTimeout 
-checkpointDir 
-tolerableCheckpointFailureNumber 
-asynchronousSnapshots
-externalizedCheckpointCleanup
如：  -asynchronousSnapshots true  -checkpointDir   hdfs://hcluster/flink/checkpoints/   
(注意目前权限)

~~~~

| 参数        | 值   |  说明  |
| --------   | -----:  | :----:  |
| checkpointInterval      |  整数 （如 1000）  |   默认每60s保存一次checkpoint  单位毫秒   |
| checkpointingMode      |  EXACTLY_ONCE  或者 AT_LEAST_ONCE  |     一致性模式 默认EXACTLY_ONCE  单位字符  |
| checkpointTimeout      |   6000|      默认超时10 minutes 单位毫秒|
| checkpointDir      |    |   保存地址 如  hdfs://hcluster/flink/checkpoints/ 注意目录权限   |
| tolerableCheckpointFailureNumber      |  1  |    设置失败次数 默认一次    |
| asynchronousSnapshots      |  true 或者 false  |     是否异步  |
| externalizedCheckpointCleanup | DELETE_ON_CANCELLATION或者RETAIN_ON_CANCELLATION | 作业取消后检查点是否删除（可不填） |
| stateBackendType      |  0 或者 1 或者 2 |   默认1  后端状态 0:MemoryStateBackend   1: FsStateBackend  2:RocksDBStateBackend  |
| enableIncremental      |  true 或者 false  |     是否采用增量 只有在 stateBackendType 2模式下才有效果 即RocksDBStateBackend  |


**rocksBD 优化配置参数** 

https://ci.apache.org/projects/flink/flink-docs-release-1.12/deployment/config.html#advanced-rocksdb-state-backends-options

**源码配置项java类  RocksDBConfigurableOptions**

e: 三方地址
~~~~
填写连接器或者udf等jar 
 如： 
http://ccblog.cn/jars/flink-connector-jdbc_2.11-1.12.0.jar
http://ccblog.cn/jars/flink-sql-connector-kafka_2.11-1.12.0.jar
http://ccblog.cn/jars/flink-streaming-udf.jar
http://ccblog.cn/jars/mysql-connector-java-5.1.25.jar
 
 地址填写后 udf可以在sql语句里面直接写
CREATE   FUNCTION jsonHasKey as 'com.xx.udf.JsonHasKeyUDF';
~~~~
![图片](http://img.ccblog.cn/flink/9.png)

 多个url使用换行


udf 开发demo 详见  [https://github.com/zhp8341/flink-streaming-udf](https://github.com/zhp8341/flink-streaming-udf)


f: sql语句

![图片](http://img.ccblog.cn/flink/10.png)

![图片](http://img.ccblog.cn/flink/12.png)


**备注： 需要选中对应的代码再点击"格式化代码" 按钮 才有效果 tips: win系统 CTRL+A 全选 mac系统 command+A 全选**

**备注：只能校验单个sql语法正确与否, 不能校验上下文之间关系 和catalog语法，如：这张表是否存在 数据类型是否正确等无法校验,总之不能完全保证运行的时候sql没有异常，只是能校验出一些语法错误**


[支持catalog](https://github.com/zhp8341/flink-streaming-platform-web/tree/master/docs/catalog.md)



### 2、系统设置

~~~~

    系统设置有三个必选项
    1、flink-streaming-platform-web应用安装的目录（必选） 
     这个是应用的安装目录
      如 /root/flink-streaming-platform-web/

    2、flink安装目录（必选）
      --flink客户端的目录 如： /usr/local/flink-1.12.0/

    3、yarn的rm Http地址
     --hadoop yarn的rm Http地址  http://hadoop003:8088/

    4、flink_rest_http_address
     LOCAL模式使用 flink http的地址

    5、flink_rest_ha_http_address
     STANDALONE模式 支持HA的   可以填写多个地址 ;用分隔

~~~~


![图片](http://img.ccblog.cn/flink/5.png)




### 3、报警设置


#### a:钉钉告警配置

~~~~
    报警设置用于: 当运行的任务挂掉的时候会告警
   
    资料：钉钉报警设置官方文档：https://help.aliyun.com/knowledge_detail/106247.html
 
~~~~

安全设置 关键词必须填写： <font color=red size=5> 告警 </font>


![图片](https://img-blog.csdnimg.cn/20201018110534482.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pocDgzNDE=,size_16,color_FFFFFF,t_70#pic_center)
![图片](https://img-blog.csdnimg.cn/20201018112359232.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pocDgzNDE=,size_16,color_FFFFFF,t_70#pic_center)


效果图
![图片](https://img-blog.csdnimg.cn/20201018111816869.png#pic_center)



#### b:自定义回调告警
 
 
自定义回调告警用于用户可以按照一定的http接口开发自己想要的告警模式 如短信、邮件、微信等

**开发要求**

**url: http://{domain}/alarmCallback    URN必须是alarmCallback**

**支持 post get**


| 请求参数        |  说明  |
| --------   | :----:  |
| appId              |     任务appid  |
| jobName           |   任务名称  |
| deployMode      |    模式   |


具体开发可参考如下代码

https://github.com/zhp8341/flink-streaming-platform-web/blob/master/flink-streaming-web/src/main/java/com/flink/streaming/web/controller/api/ApiController.java

~~~~
  @RequestMapping("/alarmCallback")
    public RestResult alarmCallback(String appId, String jobName, String deployMode) {
        log.info("测试回调 appId={} jobName={} deployMode={}", appId, jobName, deployMode);
        //业务逻辑
        return RestResult.success();
    }
~~~~


#### c:任务自动拉起


如果配置了自动拉起并且检测到集群上的任务挂掉就会再次重启

**配置效果图**

 ![图片](http://img.ccblog.cn/flink/13.png)




### 请使用下面的sql进行环境测试 用于新用户跑一个hello word 对平台有个感知的认识

```sql

  CREATE TABLE source_table (
  f0 INT,
  f1 INT,
  f2 STRING
 ) WITH (
  'connector' = 'datagen',
  'rows-per-second'='5'
 );
  
  
 CREATE TABLE print_table (
  f0 INT,
  f1 INT,
  f2 STRING
 ) WITH (
  'connector' = 'print'
 );
  
  
  insert into print_table select f0,f1,f2 from source_table;
 
```

```diff
+ 备注：如果有开发条件的同学可以将错误日志接入你们的日志报警系统
```


