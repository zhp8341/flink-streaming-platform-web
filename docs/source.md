## 整体设计

### 1、设计图

![图片](http://img.ccblog.cn/flink/flink-streaming-platform-web.jpg)

### 2、代码模块简介

1. deploy:
    1. 工程打包方式
    2. 程序运维脚本:启动、停止、重启
    3. web端配置文件 application.properties
   
2. docs: 
   1. 项目运行数据库初始化sql
   2. sql样例
   3. 使用文档等
   
3. flink-streaming-common: flink流计算相关公共类

4. flink-streaming-core: flink流计算核心模块
   1. 流计算任务提交、配置等.

5. flink-streaming-valication: sql校验模块

6. flink-streaming-web: web平台模块
   1. 任务管理
   2. 用户管理
   3. 日志管理
   4. 系统配置等.
   
7. flink-streaming-web-alarm: web平台报警接口

8. flink-streaming-web-common: web平台模块公共类
   1. 各种bean类
   2. 各种工具类
   3. 各种枚举等.
   
9. flink-streaming-web-config: web平台配置类
   1. 报警、任务、savepoint、等待 各个队列的配置

### 3、各代码模块详细设计思路及流程

