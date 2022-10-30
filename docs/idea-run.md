# 关于使用IDEA直接运行(免安装)
## 使用IDEA直接运行的好处

方便看在控制台直接看日志，方便本地Debug可以快速阅读底层，方便参与贡献

## 步骤

```
1、使用git clone 到本地后，使用IDEA打开项目

2、修改数据库连接 flink-streaming-web/src/main/resources/application.properties

3、在本地数据库中创建 flink_web 库，并执行 docs/sql/flink_web.sql 构建数据库表

4、执行  mvn clean package -DskipTests

5、在 flink-streaming-platform-web 主目录下创建 lib 文件夹

6、将 flink-streaming-core/target/flink-streaming-core.jar 移动到 刚创建的lib 中

7、将 flink-streaming-core.jar 改名为 flink-streaming-core-1.5.0.RELEASE.jar

8、flink-streaming-web 模块是REST服务模块，运行启动类是：com.flink.streaming.web.StartApplication

9、启动完成后，访问本地: localhost:8080   用户名: admin  密码: 123456

10、系统设置：flink_streaming_platform_web_home=你自己的路径/flink-streaming-platform-web/

11、如果你开发环境有 YARN 就配置 yarn_rm_http_address  (根据你自己的确定)

12、如果是standalone模式，需要本地运行Flink 集群，并配置 flink_rest_http_address
```

## 原理

```
1、平台提交任务，是使用的 Process pcs = Runtime.getRuntime().exec(command); 来执行

2、command 类似于，下面这个命令是 平台生成的 使用 yarn 提交 批任务

/Users/gump/dreamware/flink-1.13.1/bin/flink run -yjm 1024m -ytm 1024m -p 1 -yqu default  -ynm flink@my_batch_job  -yd -m yarn-cluster  -c com.flink.streaming.core.JobApplication /Users/gump/study/source/github/flink-streaming-platform-web/lib/flink-streaming-core-1.5.0.RELEASE.jar -sql /Users/gump/study/source/github/flink-streaming-platform-web/sql/job_sql_3.sql  -type 2 

3、大家可以去分析这个命令，其实就是要知道 Flink 的客户端在哪个目录 ，提交的jar 就是导入到lib下的 flink-streaming-core-1.5.0.RELEASE.jar 

4、yarn 的url 貌似只是 日志那里有使用

5、com.flink.streaming.core.JobApplication 这个就是真正运行的JOB,在IDEA 里面也是可以执行的，配置一下参数就可以了，相信大家可以举一反三的
   我debug时用的参数参考： -sql /Users/gump/study/source/github/flink-streaming-platform-web/sql/job_sql_1.sql  -type 0
   这里有个细节：core pom中的Flink 依赖包是 provide 的，本地要debug job时可以 注释这个刷新Maven

6、我们在任务中配置的SQL，会生成在项目的 /sql 目录下 ，也就是上面命令的 -sql 后的路径 -type 是告诉任务是流任务还是 批任务