CREATE TABLE test (
                      id INT NOT NULL,
                      name STRING,
                      age INT
) WITH (
      'connector' = 'mysql-cdc',
      'hostname' = '192.168.79.128',
      'port' = '3306',
      'username' = 'root',
      'password' = '123456',
      'database-name' = 'mydb',
      'table-name' = 'test',
      'scan.incremental.snapshot.enabled'='false'
      );
CREATE TABLE test_sink (
                           id INT NOT NULL,
                           name STRING,
                           age INT,
                           PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'jdbc',
      'url' = 'jdbc:mysql://192.168.79.128:3306/mydb?characterEncoding=UTF-8',
      'table-name' = 'test_sink',
      'username' = 'root',
      'password' = '123456'
      );

select * from test;

insert into test_sink select * from test;