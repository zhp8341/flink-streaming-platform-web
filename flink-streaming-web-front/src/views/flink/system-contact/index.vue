<template>
  <div v-loading="loading" class="tab-container" size="mini">

    <el-form ref="form" :model="form" label-width="180px">
      <el-form-item inline="true">
        <span slot="label">作者：朱慧培 </span>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">源码： <a href="https://github.com/zhp8341/flink-streaming-platform-web" target="_blank">点击查看源码</a> </span>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">钉钉群：34315096 </span>
        <img src="https://flink-sql.oss-cn-hangzhou.aliyuncs.com/dd2.png" style="width: 800px; height: 1028px;">
      </el-form-item>

    </el-form>
  </div>
</template>
