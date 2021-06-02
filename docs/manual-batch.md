
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





d: 三方地址
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


e: sql语句
####  提前先在hive test的库下创建好test的表
~~~~
create table  test(
id int,
name string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';
~~~~


~~~~sql
CREATE CATALOG testmyhive WITH (
    'type' = 'hive',
    'default-database' = 'test',
    'hive-conf-dir' = '/alidata/server/zhp/catalog/config'
);
USE CATALOG testmyhive;

insert into test.test values(4,'n2');

~~~~


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









