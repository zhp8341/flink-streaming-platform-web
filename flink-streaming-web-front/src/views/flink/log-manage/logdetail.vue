<template>
  <div v-loading="loading" class="fl-logdetail-container">
    <el-tooltip class="item" effect="dark" content="返回" placement="right">
      <i ref="backbutton" class="el-icon-d-arrow-left fl-back" @click="handleBack()" />
    </el-tooltip>
    <el-form ref="form" :model="form" :disabled="false" label-width="70px" size="small" class="fl-log">
      <el-row>
        <el-col :span="10">
          <el-form-item label="运行状态" prop="jobStatus">
            <el-input v-model="form.jobStatus" placeholder="运行状态" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="14">
          <el-form-item label="Flink客户端日志" prop="clinetJobUrl" label-width="120px">
            <el-link type="primary" target="_blank" :href="form.clinetJobUrl">{{ form.clinetJobUrl }}</el-link>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="运行模式" prop="deployMode">
            <el-input v-model="form.deployMode" placeholder="运行模式" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="14">
          <el-form-item label="Flink集群日志" prop="remoteLogUrl" label-width="120px">
            <el-link type="primary" target="_blank" :href="form.remoteLogUrl">{{ form.remoteLogUrl }}</el-link>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="日志内容" prop="localLog">
            <codemirror ref="cm" :value="form.localLog" :options="cmOptions" class="fl-codemirror" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="fl-button-row">
        <el-col :span="24">
          <el-form-item label="">
            <el-button type="primary" @click="getLogDetail()">刷新</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { CodeMirror, codemirror } from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/shell/shell.js'

import { logDetail } from '@/api/log'

export default {
  name: 'LogDetail',
  components: {
    codemirror
  },
  data() {
    return {
      loading: false,
      params: {
        flag: '',
        data: {},
        context: '' // 父页面传递过来的参加，返回时带给父页面恢复上下文
      },
      logid: '',
      form: {},
      cmOptions: {
        value: '',
        mode: 'text/x-sh', // flink/x-fsql, text/x-mysql, text/x-sh
        theme: 'default', // solarized light,base16-light,cobalt,default,mbo,cobalt
        readOnly: true,
        tabSize: 4,
        line: true,
        lineNumbers: true,
        extraKeys: { 'Ctrl': 'autocomplete' } // 自定义快捷键
      }
    }
  },
  mounted() {
    const params = this.$route.params
    this.params.flag = params.flag
    this.params.context = params.context
    this.params.data = params.data
    this.logid = params.data.id
    this.getLogDetail()
  },
  methods: {
    handleBack() { // 返回
      if (this.params.flag === 'loglist') {
        this.$router.replace({ name: 'FlinkLogManage', params: this.params.context })
      } else if (this.params.flag === 'tasklist') {
        this.$router.replace({ name: 'FlinkTaskManage', params: this.params.context })
      }
    },
    getLogDetail() { // 查询日志详情
      this.loading = true
      logDetail(this.logid).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.form = data || {}
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
  .fl-logdetail-container {
    margin: 0px 20px;
  }
  .fl-back {
    color: #303133;
    font-size: 14px;
    margin-left: -20px;
    cursor: pointer;
  }
  .fl-back:hover {
    color: #a2a6af;
  }
  .fl-codemirror {
    border: 1px solid#C0C4CC;
  }
  .fl-log >>> .el-form-item {
    margin-bottom: 10px!important;
  }
  .fl-button-row >>> .el-form-item {
    margin-bottom: 0px!important;
  }
 .fl-log >>> .CodeMirror {
    height: calc(100vh - 205px);
    line-height : 150%;
    font-family: monospace,Helvetica Neue,Helvetica,Arial,sans-serif;
    font-size: 13px;
    background: #f5f7fa;
  }
</style>
