
BEGIN STATEMENT SET;

SET 'table.local-time-zone' = 'Asia/Shanghai';

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


SET 'table.sql-dialect'='hive';

CREATE TABLE hive_flink_table (
                                  itemId BIGINT,
                                  price BIGINT,
                                  ups string
) TBLPROPERTIES (
  'sink.rolling-policy.rollover-interval'='1min',
  'sink.partition-commit.trigger'='process-time',
  'sink.partition-commit.policy.kind'='metastore,success-file'
);

SET 'table.sql-dialect'=default;

insert into hive_flink_table select itemId,price, 'XXXXaaa' as ups from item_test;