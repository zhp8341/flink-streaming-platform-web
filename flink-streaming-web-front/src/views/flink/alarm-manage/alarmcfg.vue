<template>
  <div v-loading="loading" class="tab-container" size="mini">
    <el-form ref="form" :model="form" label-width="100px">
      <el-form-item inline="true">
        <span slot="label">钉钉告警
          <el-popover placement="right" trigger="hover">
            <p>钉钉告警所需的url（如果不填写将无法告警）</p>
            <p>1、目前暂时钉钉群告警且只能添加一个 （当运行的任务挂掉的时候会告警）</p>
            <p>2、部署的机器需要支持外网否则无法支持钉钉发送</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.dingding_alart_url" placeholder="钉钉告警所需的url" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('dingding_alart_url', form.dingding_alart_url)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('dingding_alart_url')">删除</el-button>
        <el-button type="success" @click="testalarm('/testDingdingAlert')">测试一下</el-button>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">自定义回调
          <el-popover placement="right" trigger="hover" width="800">
            <p>自定义http回调告警</p>
            <p>1、回调url用于支持用户自定义告警，当任务出现告警时，会通过回调url通知 如 http://127.0.0.1/alarmCallback</p>
            <p>2、回调url支持post、get请求，请求参数是appId、jobName、deployMode 在自定义开发的时候必须要有这三个请求参数，且URN必须是alarmCallback</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.callback_alart_url" placeholder="自定义http回调告警" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('callback_alart_url', form.callback_alart_url)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('callback_alart_url')">删除</el-button>
        <el-button type="success" @click="testalarm('/testHttpAlert')">测试一下</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { alartConfig, upsertSynConfig, deleteConfig,test_alarm } from '@/api/config'

export default {
  name: 'AlarmCfg',
  data() {
    return {
      loading: false,
      form: {
        dingding_alart_url: '',
        callback_alart_url: ''
      }
    }
  },
  mounted() {
    this.queryConfig()
  },
  methods: {
    queryConfig() { // 查询配置列表
      this.loading = true
      alartConfig().then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.form.dingding_alart_url = ''
        this.form.callback_alart_url = ''
        data.forEach((item) => {
          if (item.key === 'dingding_alart_url') {
            this.form.dingding_alart_url = item.val
          } else if (item.key === 'callback_alart_url') {
            this.form.callback_alart_url = item.val
          }
        })
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    updateConfig(key, value) { // 更新配置
      this.loading = true
      upsertSynConfig(key, value).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.$message({ type: 'success', message: `更新成功！` })
        this.queryConfig()
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },

    testalarm(url) { // 测试一下
      console.log(url)
      test_alarm(url).then(response => {
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '测试数据异常！') })
          return
        }
        this.$message({ type: 'success', message: `测试成功！` })
        this.queryConfig()
      }).catch(error => {
        this.$message({ type: 'error', message: '测试异常！' })
        console.log(error)
      })
    },

    deleteConfig(key) { // 删除配置
      var keyname = ''
      if (key === 'dingding_alart_url') {
        keyname = '钉钉告警通知配置'
      } else if (key === 'callback_alart_url') {
        keyname = '自定义告警通知配置'
      }
      this.$confirm(`是否删除${keyname}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteConfig(key).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.$message({ type: 'success', message: `删除成功！` })
          this.queryConfig()
        }).catch(error => {
          this.loading = false
          this.$message({ type: 'error', message: '请求异常！' })
          console.log(error)
        })
      })
    }
  }
}
</script>

<style scoped>
  .tab-container {
    margin: 20px;
  }
  .fl-form-item {
    width: 500px;
    margin-right: 5px;
  }
</style>
