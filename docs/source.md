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
   1. 
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

1. 任务提交启动流程代码片段.

```java
/**
 * Controller 层.
 * 加载任务详情
 * 加载任务报警配置
 * 确定任务运行模式,使用对应的实现类启动任务.接口类:JobServerAO
 */
JobConfigApiController.start(Long id,Long savepointId);
```

```java
/**
 * 任务提交主流程
 */
JobYarnServerAOImpl.start(Long id,Long savepointId,String userName){
        //1、检查jobConfigDTO 状态等参数
        jobBaseServiceAO.checkStart(jobConfigDTO);

        //2、将配置的sql 写入本地文件并且返回运行所需参数
        JobRunParamDTO jobRunParamDTO=jobBaseServiceAO.writeSqlToFile(jobConfigDTO);

        //3、插一条运行日志数据
        Long jobRunLogId=jobBaseServiceAO.insertJobRunLog(jobConfigDTO,userName);

        //4、变更任务状态（变更为：启动中） 有乐观锁 防止重复提交
        jobConfigService.updateStatusByStart(jobConfigDTO.getId(),userName,jobRunLogId,jobConfigDTO.getVersion());

        String savepointPath=savepointBackupService.getSavepointPathById(id,savepointId);

        //异步提交任务
        jobBaseServiceAO.aSyncExecJob(jobRunParamDTO,jobConfigDTO,jobRunLogId,savepointPath);
        }
```

```java
/**
 * 异步提交任务
 */
JobBaseServiceAOImpl.aSyncExecJob(JobRunParamDTO jobRunParamDTO,JobConfigDTO jobConfigDTO,Long jobRunLogId,String savepointPath){
        // 以yarn-per-job为例
        case YARN_PER:
        //1、构建执行命令
        command=CommandUtil.buildRunCommandForYarnCluster(jobRunParamDTO,jobConfigDTO,savepointPath);
        //2、提交任务
        appId=this.submitJobForYarn(command,jobConfigDTO,localLog);
        break;
        }
        //提交完成后更新状态.
        this.updateStatusAndLog(jobConfigDTO,jobRunLogId,jobStatus,localLog.toString(),appId);
```
