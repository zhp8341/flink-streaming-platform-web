##demo1 单流kafka写入mysqld  参考


source kafka json 数据格式  {"day_time": "20201009","id": 7,"amnount":20}

sink mysql 创建语句

```sql
CREATE TABLE sync_test_1 (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `day_time` varchar(64) DEFAULT NULL,
  `total_gmv` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx` (`day_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

```

配置语句

```sql

create table flink_test_1 ( 
  id BIGINT,
  day_time VARCHAR,
  amnount BIGINT,
  proctime AS PROCTIME ()
)
 with ( 
 'connector.properties.zookeeper.connect'='hadoop001:2181',
  'connector.version'='universal',
  'connector.topic'='flink_test_4',
  'connector.startup-mode'='earliest-offset',
  'format.derive-schema'='true',
  'connector.type'='kafka',
  'update-mode'='append',
  'connector.properties.bootstrap.servers'='hadoop003:9092',
  'connector.properties.group.id'='flink_gp_test1',
  'format.type'='json'
 );

CREATE TABLE sync_test_1 (
                   day_time string,
                   total_gmv bigint
 ) WITH (
   'connector.type' = 'jdbc',
   'connector.url' = 'jdbc:mysql://127.0.0.1:3306/flink_web?characterEncoding=UTF-8',
   'connector.table' = 'sync_test_1',
   'connector.username' = 'root',
   'connector.password' = 'root'

 );

INSERT INTO sync_test_1 
SELECT day_time,SUM(amnount) AS total_gmv
FROM flink_test_1
GROUP BY day_time;

``` 

