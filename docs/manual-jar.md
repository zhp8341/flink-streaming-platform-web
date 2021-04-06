 
## 效果
 
 
 ![图片](http://img.ccblog.cn/flink/1-3.png)
 ![图片](http://img.ccblog.cn/flink/1-2.png)
 
 
 
## 说明
 
 
 **重要：自研的jar流计算任务必须先放到http服务上（自己搭建一个http服务器）**
 

### 主类名*
 
 
 如：org.apache.flink.streaming.examples.socket.SocketWindowWordCount
 
 
### 主类jar的http地址*
 
 
 如：http://192.168.1.100/jar/SocketWindowWordCount.jar
 
 
 需要提前将jar放到内部的http服务器上
 
 
### 自定义参数主类参数：
 
 
 主类里面main方法所需的参数 有用户自定义 可以不填写 取决于用户开发的主类
 
  --port 9999 --hostname 192.168.1.100
 
 
 
 
 
