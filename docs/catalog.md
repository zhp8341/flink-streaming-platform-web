**cataLog操作说明**

## 依懒jar参考官方文档 

https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/connectors/hive/

不同hive所需jar不一样

官方catalog连接配置说明：

https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/connectors/hive/#%E8%BF%9E%E6%8E%A5%E5%88%B0hive


`可以把jar放到 lib 下 也可以放到http服务器上 然后在使用的时候添加http服务。`

## demo1
~~~~ sql
 CREATE CATALOG testmyhive WITH (
    'type' = 'hive',
    'default-database' = 'zhp',
    'hive-conf-dir' = '/Users/huipeizhu/hive-conf'
);

USE CATALOG testmyhive;

 CREATE TABLE  source_table_01 (
 f0 INT,
 f1 INT,
 f2 STRING
) WITH (
 'connector' = 'datagen',
 'rows-per-second'='5'
);
 
 
CREATE TABLE   print_table_01 (
 f0 INT,
 f1 INT,
 f2 STRING
) WITH (
 'connector' = 'print'
);

insert into print_table_01 select f0,f1,f2 from source_table_01;
 
SHOW TABLES;

SHOW FUNCTIONS;

SHOW CATALOGS;

SHOW DATABASES;
 ~~~~ 


## demo2
如果已经使用过了可以直接
~~~~ sql
 CREATE CATALOG testmyhive WITH (
    'type' = 'hive',
    'default-database' = 'zhp',
    'hive-conf-dir' = '/Users/huipeizhu/hive-conf'
);

USE CATALOG testmyhive;

insert into print_table_01 select f0,f1,f2 from source_table_01;
 ~~~~ 


## demo3

https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/connectors/hive/hive_read_write.html

流数据结果sink到hive 

**注意写到hive必须要开启checkpoint**

~~~~ sql


 CREATE CATALOG testmyhive WITH (
    'type' = 'hive',
    'default-database' = 'zhp',
    'hive-conf-dir' = '/Users/huipeizhu/hive-conf'
);

USE CATALOG testmyhive;

drop table IF EXISTS item_test;

drop table IF EXISTS hive_flink_table;

create table item_test ( 
     itemId BIGINT,
     price BIGINT,
     proctime AS PROCTIME ()
 )with ( 
     'connector' = 'kafka',
     'topic' = 'flink-catalog-v1',  
     'properties.bootstrap.servers'='127.0.0.1:9092',
     'properties.group.id'='test-1',
     'format'='json',
     'scan.startup.mode' = 'earliest-offset'
 );


SET table.sql-dialect=hive;

CREATE TABLE hive_flink_table (
 itemId BIGINT, 
 price BIGINT, 
 ups string
) TBLPROPERTIES (
  'sink.rolling-policy.rollover-interval'='1min',
  'sink.partition-commit.trigger'='process-time',
  'sink.partition-commit.policy.kind'='metastore,success-file'
);

SET table.sql-dialect=default;

insert into hive_flink_table select itemId,price, 'XXXXaaa' as ups from item_test;
 ~~~~ 

## kafka 生产者 数据demo 用于测试用

~~~java
public class KafkaSend {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        Map<String, Long> map = new HashMap<>();

        for (long i = 0; i <10000 ; i++) {
            map.put("itemId", i);
            map.put("price", i+1);
            producer.send(new ProducerRecord<String, String>("flink-catalog-v1", null, JSON.toJSONString(map)));
            producer.flush();
            Thread.sleep(1000L);
        }

        producer.close();
    }
}
~~~
