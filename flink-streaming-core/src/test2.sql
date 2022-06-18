-- SET 'table.local-time-zone' = 'Asia/Shanghai';
SET pipline.name= my_Flink_job;

CREATE TABLE MyTable1 (`count` bigint, word VARCHAR(256)) WITH ('connector' = 'datagen');


CREATE TABLE MyTable2 (`count` bigint, word VARCHAR(256)) WITH ('connector' = 'datagen');

EXPLAIN PLAN FOR SELECT `count`, word FROM MyTable1 WHERE word LIKE 'F%' UNION ALL

                 SELECT `count`, word FROM MyTable2;


EXPLAIN ESTIMATED_COST, CHANGELOG_MODE, JSON_EXECUTION_PLAN SELECT `count`, word FROM MyTable1
     WHERE word LIKE 'F%'
          UNION ALL
                                                             SELECT `count`, word FROM MyTable2;