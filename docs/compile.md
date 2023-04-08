目前web客户端支持的flink版本是1.15.3,如果需要调整flink版本可下载源码
然后修改pom里面的版本号 https://github.com/zhp8341/flink-streaming-platform-web/blob/master/pom.xml 
~~~~
<flink.version>1.12.0</flink.version> <!--flink版本-->
<scala.binary.version>2.11</scala.binary.version> <!--scala版本-->
~~~~
可能调整后导致flink引用的上下不兼容 需要你手动解决下

保存后打包

~~~~
mvn clean package  -Dmaven.test.skip=true
~~~~

最后打好的包在 {你的目录}/flink-streaming-platform-web/deployer/target

包名是：flink-streaming-platform-web.tar.gz
