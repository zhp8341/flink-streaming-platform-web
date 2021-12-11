
### docker 本地体验版本


#### 备注： docker 这里不介绍了



#### 一、镜像拉取

docker pull registry.cn-hangzhou.aliyuncs.com/flink-streaming-platform-web/flink-web:flink-1.13.2



#### 二、数据库

在本地或者可以访问的局域网内创建数据库

然后初始化 [sql预计点击这里](./sql/flink_web_docker.sql)



#### 三、创建数据库配置文件

在本地机器上创建 application-docker.properties 文件（任意目录）

内容是

```properties
spring.datasource.url=jdbc:mysql://${局域网的ip}:3306/flink_web_docker?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
```

如：
```properties
spring.datasource.url=jdbc:mysql://192.168.1.100:3306/flink_web_docker?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
```
  **ip不能是localhost 或者  127.0.0.1** 


#### 四、启动docker
```shell

docker run -it  \
 -v  ${本地application-docker.properties文件所在的目录}:/data/projects/flink-streaming-platform-web/conf/  \
 -v  ${本地日志目录挂载地址}:/data/projects/flink-streaming-platform-web/logs  \
 -p 9084:9084 \
 -p 8081:8081 \
  -d --name="flink-streaming-platform-web-demo" registry.cn-hangzhou.aliyuncs.com/flink-streaming-platform-web/flink-web:flink-1.13.2 
  
  
 
```
如：

```shell
docker run -it  -v  /Users/huipeizhu/git/flink-streaming-platform-web/deployer/src/main/conf:/data/projects/flink-streaming-platform-web/conf/   -v  /Users/huipeizhu/git/docker_file:/data/projects/flink-streaming-platform-web/logs  -p 9084:9084 -p 8081:8081 -d --name="flink-streaming-platform-web-demo" registry.cn-hangzhou.aliyuncs.com/flink-streaming-platform-web/flink-web:flink-1.13.2
```

**注意：9084 和8081端口不要被占用. 8081映射端口不要修改 否则会导致任务无法提交**


#### 五、查看


http://127.0.0.1:9084/admin/index   账号/密码 ： admin / 123456

登录后直接 提交任务 **test_datagen_simple**   就可以查看效果

另外任务可以在这里看 http://127.0.0.1:8081/#/overview 


**注意：通过这个demo  让新人对flink-streaming-platform-web应用有个直观的认识  目前docker只是本地模式 没办法上线使用，线上使用还需要自己安装**




