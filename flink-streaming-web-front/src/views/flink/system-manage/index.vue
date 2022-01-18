<template>
  <div v-loading="loading" class="tab-container" size="mini">
    <el-form ref="form" :model="form" label-width="180px">
      <el-form-item inline="true">
        <span slot="label">Flink客户端目录
          <el-popover placement="right" trigger="hover">
            <p>Flink客户端目录（必选）</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.flink_home" placeholder="Flink客户端目录（必选）" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('flink_home', form.flink_home)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('flink_home')">删除</el-button>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">Flink管理平台目录
          <el-popover placement="right" trigger="hover">
            <p>Flink管理平台应用安装的目录（必选）</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.flink_streaming_platform_web_home" placeholder="Flink管理平台应用安装的目录（必选）" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('flink_streaming_platform_web_home', form.flink_streaming_platform_web_home)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('flink_streaming_platform_web_home')">删除</el-button>
      </el-form-item>

      <el-form-item inline="true">
        <span slot="label">自动开启savepoint
          <el-popover placement="right" trigger="hover">
            <p>全局配置 默认开启 变量值 true 或 false</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.auto_savepoint" placeholder="全局配置 默认开启 变量值 true 或 false" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('auto_savepoint', form.auto_savepoint)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('auto_savepoint')">删除</el-button>
      </el-form-item>

      <el-form-item inline="true">
        <span slot="label">Yarn RM Http地址
          <el-popover placement="right" trigger="hover">
            <p>Yarn的RM Http地址（yarn per模式必选）</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.yarn_rm_http_address" placeholder="Yarn的RM Http地址（yarn per模式必选）" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('yarn_rm_http_address', form.yarn_rm_http_address)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('yarn_rm_http_address')">删除</el-button>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">Flink服务Web地址
          <el-popover placement="right" trigger="hover">
            <p>Flink Rest & web frontend 地址(Local Cluster模式)</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.flink_rest_http_address" placeholder="Flink Rest & web frontend 地址(Local Cluster模式)" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('flink_rest_http_address', form.flink_rest_http_address)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('flink_rest_http_address')">删除</el-button>
      </el-form-item>
      <el-form-item inline="true">
        <span slot="label">Flink HA服务Web地址
          <el-popover placement="right" trigger="hover">
            <p>Flink Rest & web frontend HA 地址(Standalone Cluster模式支持HA，可以填写多个地址用;分隔)</p>
            <i slot="reference" class="el-icon-info" />
          </el-popover>
        </span>
        <el-input v-model="form.flink_rest_ha_http_address" placeholder="Flink Rest & web frontend HA 地址(Standalone Cluster模式支持HA，可以填写多个地址用;分隔)" class="fl-form-item" />
        <el-button type="primary" @click="updateConfig('flink_rest_ha_http_address', form.flink_rest_ha_http_address)">提交</el-button>
        <el-button type="danger" @click="deleteConfig('flink_rest_ha_http_address')">删除</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { sysConfig, upsertSynConfig, deleteConfig } from '@/api/config'

export default {
  name: 'SystemCfg',
  data() {
    return {
      loading: false,
      form: {
        flink_home: '',
        flink_streaming_platform_web_home: '',
        auto_savepoint: '',
        yarn_rm_http_address: '',
        flink_rest_http_address: '',
        flink_rest_ha_http_address: ''
      }
    }
  },
  mounted() {
    this.queryConfig()
  },
  methods: {
    queryConfig() { // 查询配置列表
      this.loading = true
      sysConfig().then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.form.flink_home = ''
        this.form.flink_streaming_platform_web_home = ''
        this.form.auto_savepoint = ''
        this.form.yarn_rm_http_address = ''
        this.form.flink_rest_http_address = ''
        this.form.flink_rest_ha_http_address = ''
        data.forEach((item) => {
          if (item.key === 'flink_home') {
            this.form.flink_home = item.val
          } else if (item.key === 'flink_streaming_platform_web_home') {
            this.form.flink_streaming_platform_web_home = item.val
          } else if (item.key === 'yarn_rm_http_address') {
            this.form.yarn_rm_http_address = item.val
          } else if (item.key === 'flink_rest_http_address') {
            this.form.flink_rest_http_address = item.val
          } else if (item.key === 'flink_rest_ha_http_address') {
            this.form.flink_rest_ha_http_address = item.val
          } else if (item.key === 'auto_savepoint') {
            this.form.auto_savepoint = item.val
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
    deleteConfig(key) { // 删除配置
      var keyname = ''
      if (key === 'flink_home') {
        keyname = 'Flink主目录地址配置'
      } else if (key === 'flink_streaming_platform_web_home') {
        keyname = 'Flink管理平台Web地址配置'
      } else if (key === 'yarn_rm_http_address') {
        keyname = 'Yarn RM Http地址配置'
      } else if (key === 'flink_rest_http_address') {
        keyname = 'Flink Web地址'
      } else if (key === 'flink_rest_ha_http_address') {
        keyname = 'Flink HA Web地址'
      } else if (key === 'auto_savepoint') {
        keyname = 'Flink auto_savepoint'
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
