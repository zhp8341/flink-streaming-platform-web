## demo5 滑动窗口


source kafka json 数据格式  

topic  flink_test_9  

{"username":"zhp","click_url":"http://xxx/","ts":1602295200000}
{"username":"zhp","click_url":"http://xxx/","ts":1602295210000}
{"username":"zhp","click_url":"http://xxx/","ts":1602295270000}




sink mysql 创建语句

```sql

CREATE TABLE `sync_test_hop_output` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `window_start` datetime DEFAULT NULL,
  `window_end` datetime DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `clicks` bigint(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uk` (`window_start`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

```

配置语句

```sql

     -- -- 开启 mini-batch （相关配置说明 https://ci.apache.org/projects/flink/flink-docs-release-1.10/zh/dev/table/config.html）
SET table.exec.mini-batch.enabled=true;
-- -- mini-batch的时间间隔，即作业需要额外忍受的延迟
SET table.exec.mini-batch.allow-latency=60s;
-- -- 一个 mini-batch 中允许最多缓存的数据
SET table.exec.mini-batch.size=1000;

create table user_clicks ( 
  username varchar,
  click_url varchar,
  ts BIGINT,
  ts2 AS TO_TIMESTAMP(FROM_UNIXTIME(ts / 1000, 'yyyy-MM-dd HH:mm:ss')),
  WATERMARK FOR ts2 AS ts2 - INTERVAL '5' SECOND 

)
 with ( 
 'connector.properties.zookeeper.connect'='hadoop001:2181',
  'connector.version'='universal',
  'connector.topic'='flink_test_9',
  'connector.startup-mode'='earliest-offset',
  'format.derive-schema'='true',
  'connector.type'='kafka',
  'update-mode'='append',
  'connector.properties.bootstrap.servers'='hadoop003:9092',
  'connector.properties.group.id'='flink_gp_test1',
  'format.type'='json'
 );


CREATE TABLE sync_test_hop_output (
				  window_start TIMESTAMP(3),
				  window_end TIMESTAMP(3),
				  username VARCHAR,
				  clicks BIGINT
 ) WITH (
   'connector.type' = 'jdbc',
   'connector.url' = 'jdbc:mysql://127.0.0.1:3306/flink_web?characterEncoding=UTF-8',
   'connector.table' = 'sync_test_hop_output',
   'connector.username' = 'flink_web_test',
   'connector.password' = 'flink_web_test_123'
 );
 
 
 INSERT INTO sync_test_hop_output
 SELECT
  HOP_START (ts2, INTERVAL '30' SECOND, INTERVAL '1' MINUTE) as window_start,
  HOP_END (ts2, INTERVAL '30' SECOND, INTERVAL '1' MINUTE) as window_end,
 username,
 COUNT(click_url)
 FROM user_clicks
 GROUP BY HOP (ts2, INTERVAL '30' SECOND, INTERVAL '1' MINUTE), username; 


``` 


