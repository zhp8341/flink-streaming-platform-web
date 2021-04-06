
**国内文章备用地址（因为github上面 图片效果可能看不到，原因你懂的 哈哈）**

https://xie.infoq.cn/article/1af0cb75be056fea788e6c86b 



**github地址**  https://github.com/zhp8341/flink-streaming-platform-web


**国内gitee 地址** https://gitee.com/zhuhuipei/flink-streaming-platform-web


## 一、简介 
  flink-streaming-platform-web系统是基于[Apache Flink](https://flink.apache.org) 封装的一个可视化的、轻量级的flink web客户端系统，用户只需在web
  界面进行sql配置就能完成流计算任务。
  
  **主要功能**:包含任务配置、启/停任务、告警、日志等功能，支持sql语法提示，格式化、sql语句校验。 
   
  **目的**:减少开发、降低成本 完全实现sql化 流计算任务。

### 1、主要功能

   * **[1] 任务支持单流 、双流、 单流与维表等。**
   * **[2] 支持本地模式、yarn-per模式、STANDALONE模式。**
   * **[3] 支持catalog、hive。**
   * **[4] 支持自定义udf、连接器等,完全兼容官方连接器。**
   * **[5] 支持sql的在线开发，语法提示，格式化。**
   * **[6] 支持钉钉告警、自定义回调告警、自动拉起任务。**
   * **[7] 支持自定义Jar提交任务。**
   * **[8] 支持多版本flink版本（需要用户编译对应flink版本）。**
   * **[9] 支持自动、手动savepoint备份，并且从savepoint恢复任务。**

  **目前flink版本已经升级到1.12**


  **如果您觉得还不错请在右上角点一下star 谢谢 🙏 大家的支持是开源最大动力**
  
    
### 2、效果及源码文档

1、 [点击查看WEB页面功能显示效果](/docs/img.md)

2、 [源码讲解文档](/docs/source.md)



## 二、环境搭建及安装

1、 [Flink 和 flink-streaming-platform-web 安装部署](/docs/deploy.md)




## 三、功能介绍

### 3.1 配置操作

1、[sql任务配置使用手册](/docs/manual-sql.md)

2、[jar任务配置使用手册](/docs/manual-jar.md)


### 3.2 sql配置demo


1、[demo1 单流kafka写入mysqld 参考 ](/docs/sql_demo/demo_1.md)

2、[demo2 双流kafka写入mysql 参考](/docs/sql_demo/demo_2.md)

3、[demo3 kafka和mysql维表实时关联写入mysql 参考](/docs/sql_demo/demo_3.md)

4、[demo4 滚动窗口 ](/docs/sql_demo/demo_4.md)

5、[demo5 滑动窗口](/docs/sql_demo/demo_5.md)

6、[demo6 JDBC CDC的使用示例](/docs/sql_demo/demo_6.md)

7、[demo7 datagen简介](/docs/sql_demo/demo_datagen.md)

8、[catalog 使用示例](/docs/catalog.md)


###  3.2 hello-word demo


**请使用下面的sql进行环境测试 用于新用户跑一个hello word 对平台有个感知的认识**

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


**官方相关连接器下载** 

请移步 https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/ 






##  四、支持flink sql官方语法


| 支持的sql语法        | 
| --------   | 
| INSERT   INTO         |     
| INSERT   OVERWRITE          |   
|  CREATE   TABLE   |      
|  CREATE    FUNCTION |      
| CREATE    VIEW  |      
| USE   CATALOG   |      
|  DROP     |      
|  ALTER     |      
|  SHOW CATALOGS     |      
|  SHOW DATABASES     |      
|  SHOW TABLES     |      
|  SHOW FUNCTIONS     |      
| CREATE CATALOG      |      
|   SET   |      
|   SELECT (不支持)    | 






##  五、其他
1、由于hadoop集群环境不一样可能导致部署出现困难,整个搭建比较耗时.

2、由于es 、hbase等版本不一样可能需要下载源码重新选择对应版本 源码地址 [https://github.com/zhp8341/flink-streaming-platform-web](https://github.com/zhp8341/flink-streaming-platform-web)

交流和解答



钉钉  http://img.ccblog.cn/flink/dd2.png 

微信二维码 http://img.ccblog.cn/flink/wx2.png


 完全按照flink1.12的连接器相关的配置详见

https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/ 



如果需要使用到连接器请去官方下载
如：kafka 连接器 https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/kafka.html

**第一种下载连接器后直接放到 flink/lib/目录下就可以使用了**

    1、该方案存在jar冲突可能，特别是连接器多了以后
    2、在非yarn模式下每次新增jar需要重启flink集群服务器


**第二种放到http的服务下填写到三方地址**

    公司内部建议放到内网的某个http服务
    http://ccblog.cn/jars/flink-connector-jdbc_2.11-1.12.0.jar
    http://ccblog.cn/jars/flink-sql-connector-kafka_2.11-1.12.0.jar
    http://ccblog.cn/jars/flink-streaming-udf.jar
    http://ccblog.cn/jars/mysql-connector-java-5.1.25.jar


 ![图片](http://img.ccblog.cn/flink/9.png)

 多个url使用换行

**自定义连接器打包的时候需要打成shade 并且解决jar的冲突**


**个人建议使用第二种方式，每个任务之间jar独立，如果把所有连接器放到lib 可能会和其他任务的jar冲突**
**公用的可以放到flink/lib目录里面  如：mysql驱动 kafka连接器等**


##  六、问题

1、 [可能遇到的问题和解决方案](/docs/question.md)



##  七、RoadMap


1、 支持除官方以外的连接器  如：阿里云的sls

2、 任务告警自动拉起 (完成)

3、 支持Application模式

4、 完善文档 （持续过程）

5、 支持sql预校验，编写sql的时候语法提示等友好的用户体验(完成)

6、 checkpoint支持rocksDB (完成)

7、 支持jar模式提交任务 (完成)



##  八、源码编译

[根据自己的flink版本重新编译web客户端](/docs/compile.md)


 ##  九、联系方式 
 

钉钉 
 [钉钉二维码](http://img.ccblog.cn/flink/dd2.png)

http://img.ccblog.cn/flink/dd2.png 


微信二维码 http://img.ccblog.cn/flink/wx2.png


 [微信二维码](http://img.ccblog.cn/flink/wx2.png)

 
 
 ##  十、捐赠

 
[点击打开支付宝捐赠](http://img.ccblog.cn/jz/zfb.jpg)
 
[点击打开微信捐赠](http://img.ccblog.cn/jz/wxzf.jpg)
 
 
 


