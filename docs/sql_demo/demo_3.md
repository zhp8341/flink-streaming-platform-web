##demo3 kafka和mysql维表实时关联写入mysql 参考


source kafka json 数据格式  

topic  flink_test_6  {"day_time": "20201011","id": 8,"amnount":211}

dim    test_dim

```sql
CREATE TABLE `test_dim` (
  `id` bigint(11) NOT NULL,
  `coupon_amnount` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_dim
-- ----------------------------
BEGIN;
INSERT INTO `test_dim` VALUES (1, 1);
INSERT INTO `test_dim` VALUES (3, 1);
INSERT INTO `test_dim` VALUES (8, 1);
COMMIT;

```


sink mysql 创建语句

```sql

CREATE TABLE `flink_test_6` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `day_time` varchar(64) DEFAULT NULL,
  `total_gmv` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx` (`day_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

```

配置语句

```sql

     create table flink_test_6 ( 
  id BIGINT,
  day_time VARCHAR,
  amnount BIGINT,
  proctime AS PROCTIME ()
)
 with ( 
 'connector.properties.zookeeper.connect'='hadoop001:2181',
  'connector.version'='universal',
  'connector.topic'='flink_test_6',
  'connector.startup-mode'='earliest-offset',
  'format.derive-schema'='true',
  'connector.type'='kafka',
  'update-mode'='append',
  'connector.properties.bootstrap.servers'='hadoop003:9092',
  'connector.properties.group.id'='flink_gp_test1',
  'format.type'='json'
 );


create table flink_test_6_dim ( 
  id BIGINT,
  coupon_amnount BIGINT
)
 with ( 
   'connector.type' = 'jdbc',
   'connector.url' = 'jdbc:mysql://127.0.0.1:3306/flink_web?characterEncoding=UTF-8',
   'connector.table' = 'test_dim',
   'connector.username' = 'flink_web_test',
   'connector.password' = 'flink_web_test_123',
   'connector.lookup.max-retries' = '3'
 );


CREATE TABLE sync_test_3 (
                   day_time string,
                   total_gmv bigint
 ) WITH (
   'connector.type' = 'jdbc',
   'connector.url' = 'jdbc:mysql://127.0.0.1:3306/flink_web?characterEncoding=UTF-8',
   'connector.table' = 'sync_test_3',
   'connector.username' = 'flink_web_test',
   'connector.password' = 'flink_web_test_123'

 );


INSERT INTO sync_test_3 
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
      flink_test_6 as a 
      LEFT JOIN flink_test_6_dim  FOR SYSTEM_TIME AS OF  a.proctime  as b
     ON b.id = a.id
  ) 
GROUP BY day_time;


``` 


