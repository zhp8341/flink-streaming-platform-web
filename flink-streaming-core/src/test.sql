
SET 'table.local-time-zone' = 'Asia/Shanghai';

SET pipeline.name= my_Flink_job;

SHOW MODULES;

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

CREATE TABLE print_table2 (
                              f0 INT,
                              f1 INT,
                              f2 STRING
) WITH (
      'connector' = 'print'
      );



desc print_table2;
insert into print_table select f0,f1,f2 from source_table;

insert into print_table2 select f0,f1,f2 from source_table;
