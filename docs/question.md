
1、

```java
Setting HADOOP_CONF_DIR=/etc/hadoop/conf because no HADOOP_CONF_DIR was set.
Could not build the program from JAR file.

Use the help option (-h or --help) to get help on the command.


解决
   export HADOOP_HOME=/etc/hadoop
   export HADOOP_CONF_DIR=/etc/hadoop/conf
   export HADOOP_CLASSPATH=`hadoop classpath`

   source /etc/profile

  最好配置成全局变量
```

2

```java

2020-10-02 14:48:22,060 ERROR com.flink.streaming.core.JobApplication                       - 任务执行失败：
java.lang.IllegalStateException: Unable to instantiate java compiler
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.compile(JaninoRelMetadataProvider.java:434)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.load3(JaninoRelMetadataProvider.java:375)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.lambda$static$0(JaninoRelMetadataProvider.java:109)
        at org.apache.flink.calcite.shaded.com.google.common.cache.CacheLoader$FunctionToCacheLoader.load(CacheLoader.java:149)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache$LoadingValueReference.loadFuture(LocalCache.java:3542)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache$Segment.loadSync(LocalCache.java:2323)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache$Segment.lockedGetOrLoad(LocalCache.java:2286)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache$Segment.get(LocalCache.java:2201)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache.get(LocalCache.java:3953)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache.getOrLoad(LocalCache.java:3957)
        at org.apache.flink.calcite.shaded.com.google.common.cache.LocalCache$LocalLoadingCache.get(LocalCache.java:4875)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.create(JaninoRelMetadataProvider.java:475)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.revise(JaninoRelMetadataProvider.java:488)
        at org.apache.calcite.rel.metadata.RelMetadataQuery.revise(RelMetadataQuery.java:193)
        at org.apache.calcite.rel.metadata.RelMetadataQuery.getPulledUpPredicates(RelMetadataQuery.java:797)
        at org.apache.calcite.rel.rules.ReduceExpressionsRule$ProjectReduceExpressionsRule.onMatch(ReduceExpressionsRule.java:298)
        at org.apache.calcite.plan.AbstractRelOptPlanner.fireRule(AbstractRelOptPlanner.java:319)
        at org.apache.calcite.plan.hep.HepPlanner.applyRule(HepPlanner.java:560)
        at org.apache.calcite.plan.hep.HepPlanner.applyRules(HepPlanner.java:419)
        at org.apache.calcite.plan.hep.HepPlanner.executeInstruction(HepPlanner.java:256)
        at org.apache.calcite.plan.hep.HepInstruction$RuleInstance.execute(HepInstruction.java:127)
        at org.apache.calcite.plan.hep.HepPlanner.executeProgram(HepPlanner.java:215)
        at org.apache.calcite.plan.hep.HepPlanner.findBestExp(HepPlanner.java:202)
        at org.apache.flink.table.planner.plan.optimize.program.FlinkHepProgram.optimize(FlinkHepProgram.scala:69)
        at org.apache.flink.table.planner.plan.optimize.program.FlinkHepRuleSetProgram.optimize(FlinkHepRuleSetProgram.scala:87)
        at org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram$$anonfun$optimize$1.apply(FlinkChainedProgram.scala:62)
        at org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram$$anonfun$optimize$1.apply(FlinkChainedProgram.scala:58)
        at scala.collection.TraversableOnce$$anonfun$foldLeft$1.apply(TraversableOnce.scala:157)
        at scala.collection.TraversableOnce$$anonfun$foldLeft$1.apply(TraversableOnce.scala:157)
        at scala.collection.Iterator$class.foreach(Iterator.scala:891)
        at scala.collection.AbstractIterator.foreach(Iterator.scala:1334)
        at scala.collection.IterableLike$class.foreach(IterableLike.scala:72)
        at scala.collection.AbstractIterable.foreach(Iterable.scala:54)
        at scala.collection.TraversableOnce$class.foldLeft(TraversableOnce.scala:157)
        at scala.collection.AbstractTraversable.foldLeft(Traversable.scala:104)
        at org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram.optimize(FlinkChainedProgram.scala:57)
        at org.apache.flink.table.planner.plan.optimize.StreamCommonSubGraphBasedOptimizer.optimizeTree(StreamCommonSubGraphBasedOptimizer.scala:170)
        at org.apache.flink.table.planner.plan.optimize.StreamCommonSubGraphBasedOptimizer.doOptimize(StreamCommonSubGraphBasedOptimizer.scala:90)
        at org.apache.flink.table.planner.plan.optimize.CommonSubGraphBasedOptimizer.optimize(CommonSubGraphBasedOptimizer.scala:77)
        at org.apache.flink.table.planner.delegation.PlannerBase.optimize(PlannerBase.scala:248)
        at org.apache.flink.table.planner.delegation.PlannerBase.translate(PlannerBase.scala:151)
        at org.apache.flink.table.api.internal.TableEnvironmentImpl.translate(TableEnvironmentImpl.java:682)
        at org.apache.flink.table.api.internal.TableEnvironmentImpl.sqlUpdate(TableEnvironmentImpl.java:495)
        at com.flink.streaming.core.JobApplication.callDml(JobApplication.java:138)
        at com.flink.streaming.core.JobApplication.main(JobApplication.java:85)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:321)
        at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:205)
        at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:138)
        at org.apache.flink.client.cli.CliFrontend.executeProgram(CliFrontend.java:664)
        at org.apache.flink.client.cli.CliFrontend.run(CliFrontend.java:213)
        at org.apache.flink.client.cli.CliFrontend.parseParameters(CliFrontend.java:895)
        at org.apache.flink.client.cli.CliFrontend.lambda$main$10(CliFrontend.java:968)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.Subject.doAs(Subject.java:422)
        at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1754)
        at org.apache.flink.runtime.security.HadoopSecurityContext.runSecured(HadoopSecurityContext.java:41)
        at org.apache.flink.client.cli.CliFrontend.main(CliFrontend.java:968)
Caused by: java.lang.ClassCastException: org.codehaus.janino.CompilerFactory cannot be cast to org.codehaus.commons.compiler.ICompilerFactory
        at org.codehaus.commons.compiler.CompilerFactoryFactory.getCompilerFactory(CompilerFactoryFactory.java:129)
        at org.codehaus.commons.compiler.CompilerFactoryFactory.getDefaultCompilerFactory(CompilerFactoryFactory.java:79)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.compile(JaninoRelMetadataProvider.java:432)
        ... 60 more

conf/flink-conf.yaml 

```
**配置里面 设置  classloader.resolve-order: parent-first**




**主要日志目录**

1、web系统日志

/{安装目录}/flink-streaming-platform-web/logs/

2 、flink客户端命令

${FLINK_HOME}/log/flink-${USER}-client-.log
