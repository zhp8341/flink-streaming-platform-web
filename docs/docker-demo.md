
### docker 本机体验版本


#### 备注： docker 这里不介绍了



#### 一、docker-compose文件

创建docker-compose.yml 文件
内容是
~~~~
version: '3'
services:
  flink-streaming-platform-web: 
    container_name: flink-streaming-platform-web-demo
    image: registry.cn-hangzhou.aliyuncs.com/flink-streaming-platform-web/flink-web:flink-1.13.2-20220327
    ports:
      - 8081:8081
      - 9084:9084
  mysql:
    restart: always
    image: registry.cn-hangzhou.aliyuncs.com/flink-streaming-platform-web/mysql:mysql-5.7.16-20220327
    container_name: mysql-web
    ports:
      - 3307:3306
    environment:
      - "MYSQL_ROOT_PASSWORD=root"
      - "MYSQL_DATABASE=root"
      - "TZ=Asia/Shanghai"

~~~~



按顺序执行下面命令


~~~~

docker-compose up -d mysql

docker-compose up -d flink-streaming-platform-web


docker-compose restart flink-streaming-platform-web

~~~~

注意需要现执行 mysql 再执行 flink-streaming-platform-web

**日志查看 docker-compose  logs -f flink-streaming-platform-web**

#### 二、查看


http://127.0.0.1:9084  账号/密码 ： admin / 123456

登录后直接 提交任务 **test_datagen_simple**   就可以查看效果

另外任务可以在这里看 http://127.0.0.1:8081/#/overview 


**注意：通过这个demo  让新人对flink-streaming-platform-web应用有个直观的认识  目前docker只是本地模式 没办法上线使用，线上使用还需要自己安装**



#### 三、原始镜像制作文件

[Dockerfile](../docker/Dockerfile)