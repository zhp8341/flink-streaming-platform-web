##demo2 双流kafka写入mysql 参考


source kafka json 数据格式  

topic  flink_test_5_1  {"day_time": "20201011","id": 8,"amnount":211}
topic  flink_test_5_2  {"id": 8,"coupon_amnount":100}

sink mysql 创建语句

```sql

CREATE TABLE `sync_test_2` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `day_time` varchar(64) DEFAULT NULL,
  `total_gmv` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx` (`day_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

```

配置语句

```sql

create table flink_test_5_1 ( 
  id BIGINT,
  day_time VARCHAR,
  amnount BIGINT,
  proctime AS PROCTIME ()
)
 with ( 
 'connector.properties.zookeeper.connect'='hadoop001:2181',
  'connector.version'='universal',
  'connector.topic'='flink_test_5_1',
  'connector.startup-mode'='earliest-offset',
  'format.derive-schema'='true',
  'connector.type'='kafka',
  'update-mode'='append',
  'connector.properties.bootstrap.servers'='hadoop003:9092',
  'connector.properties.group.id'='flink_gp_test1',
  'format.type'='json'
 );


  create table flink_test_5_2 ( 
  id BIGINT,
  coupon_amnount BIGINT,
  proctime AS PROCTIME ()
)
 with ( 
 'connector.properties.zookeeper.connect'='hadoop001:2181',
  'connector.version'='universal',
  'connector.topic'='flink_test_5_2',
  'connector.startup-mode'='earliest-offset',
  'format.derive-schema'='true',
  'connector.type'='kafka',
  'update-mode'='append',
  'connector.properties.bootstrap.servers'='hadoop003:9092',
  'connector.properties.group.id'='flink_gp_test1',
  'format.type'='json'
 );


CREATE TABLE sync_test_2 (
                   day_time string,
                   total_gmv bigint
 ) WITH (
  'connector.type' = 'jdbc',
   'connector.url' = 'jdbc:mysql://127.0.0.1:3306/flink_web?characterEncoding=UTF-8',
   'connector.table' = 'sync_test_1',
   'connector.username' = 'root',
   'connector.password' = 'root'

 );

INSERT INTO sync_test_2 
SELECT 
  day_time, 
  SUM(amnount - coupon_amnount) AS total_gmv 
FROM 
  (
    SELECT 
      a.day_time as day_time, 
      a.amnount as amnount, 
      b.coupon_amnount as coupon_amnount 
    FROM 
      flink_test_5_1 as a 
      LEFT JOIN flink_test_5_2 b on b.id = a.id
  ) 
GROUP BY 
  day_time;

``` 


