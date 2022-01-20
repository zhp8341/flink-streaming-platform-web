<template>
  <div v-loading="loading" class="fl-container">
    <!-- 查询 -->
    <el-form ref="queryform" :model="queryform" :inline="true">
      <el-form-item>
        <el-input v-model="queryform.jobConfigId" placeholder="任务Id" class="wl-input" @input="handleQuery()" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryform.status" placeholder="选择状态" clearable @change="handleQuery()">
          <el-option label="成功" value="1" />
          <el-option label="失败" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery()">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 列表 -->
    <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" border>
      <el-table-column prop="id" :show-overflow-tooltip="true" label="编号" min-width="60" width="80" align="center" fixed />
      <el-table-column :show-overflow-tooltip="true" label="任务ID" min-width="60" width="80" align="center" fixed>
        <template slot-scope="scope">
          <span style="margin-right:5px;">{{ scope.row.jobConfigId }}</span>
          <el-tooltip class="item" effect="dark" :content="getTaskTypeName(scope.row.jobTypeEnum)" placement="right">
            <i v-if="scope.row.jobTypeEnum==='SQL_STREAMING'" class="iconfont my-icon-jiediansql" style="font-size:16px;" />
            <i v-if="scope.row.jobTypeEnum==='SQL_BATCH'" class="iconfont my-icon-file-SQL" style="font-size:16px;" />
            <i v-if="scope.row.jobTypeEnum==='JAR'" class="iconfont my-icon-suffix-jar" style="font-size:16px;" />
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="jobName" :show-overflow-tooltip="true" label="任务名称" min-width="80" width="150" align="center" fixed />
      <el-table-column prop="alarMLogTypeEnum" :show-overflow-tooltip="true" label="告警类型" width="110" align="center">
        <template slot-scope="scope">
          <span>{{ getAlarMLogType(scope.row.alarMLogTypeEnum) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="message" :show-overflow-tooltip="true" label="消息内容" min-width="200" align="center" />
      <el-table-column prop="alarmLogStatusEnum" label="状态" width="65" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.alarmLogStatusEnum==='FAIL'" type="danger" size="mini">{{ getAlarmLogStatus(scope.row.alarmLogStatusEnum) }}</el-tag>
          <el-tag v-else-if="scope.row.alarmLogStatusEnum==='SUCCESS'" type="success" size="mini">{{ getAlarmLogStatus(scope.row.alarmLogStatusEnum) }}</el-tag>
          <el-tag v-else type="info" size="mini">{{ getStatusDesc(scope.row.alarmLogStatusEnum) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :show-overflow-tooltip="true" label="告警时间" min-width="100" width="135" align="center">
        <template slot-scope="scope">
          <span>{{ formatDateTime(scope.row.editTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="operate" label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-link icon="el-icon-message" @click="getLogErrorInfo(scope.row.id)">附加日志</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="pageshow"
      class="wl-pagination"
      background
      layout="total, sizes, prev, pager, next"
      :current-page="currentPage"
      :page-sizes="[10, 15, 20, 50, 100, 150, 200]"
      :page-size="pageSize"
      :total="count"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <el-dialog title="错误信息" :visible.sync="dialogFailLogVisible">
      <p>{{ failLog }}</p>
    </el-dialog>
  </div>
</template>

<script>
import { alartLogList, logErrorInfo } from '@/api/alert'

export default {
  name: 'AlarmLogs',
  data() {
    return {
      loading: false,
      dialogFailLogVisible: false,
      failLog: '',
      queryform: {
        jobConfigId: '',
        status: ''
      },
      list: [],
      count: 0,
      pageSize: 15,
      currentPage: 1,
      pageshow: true
    }
  },
  mounted() {
    const params = this.$route.params
    if (params) {
      this.count = params.count
      this.currentPage = params.currentPage
      this.pageSize = params.pageSize
      this.queryform.jobConfigId = params.jobConfigId
      this.queryform.status = params.status
    }
    this.handleQuery()
  },
  methods: {
    handleQuery(event) { // 查询
      this.pageshow = false
      this.getLogs()
      this.$nextTick(() => { this.pageshow = true }) // 解决界面页码不更新问题
    },
    handleSizeChange(pageSize) { // 设置分页大小事件
      this.pageSize = pageSize
      this.handleQuery()
    },
    handleCurrentChange(pageno) { // 处理分页事件
      this.currentPage = pageno
      this.handleQuery()
    },
    getLogs() { // 查询日志列表
      this.loading = true
      const { jobConfigId, status } = this.queryform
      alartLogList(this.currentPage, this.pageSize, jobConfigId, status).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.count = data.total
        this.list = data.data
        if (this.count > 0 && this.list.length == 0) { // 调整PageNo
          this.currentPage = Math.ceil(this.count / this.pageSize)
          this.getLogs()
        }
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    getLogErrorInfo(id) {
      this.loading = true
      this.failLog = ''
      logErrorInfo(id).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.failLog = data
        this.dialogFailLogVisible = true
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    getTaskTypeName(tasktype) {
      switch (tasktype) {
        case 'SQL_STREAMING': return 'SQL流任务'
        case 'SQL_BATCH': return 'SQL批任务'
        case 'JAR': return 'JAR包'
        default: return tasktype
      }
    },
    formatDateTime(date) {
      return this.dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    getAlarMLogType(type) {
      switch (type) {
        case 'DINGDING': return '钉钉'
        case 'CALLBACK_URL': return '自定义回调http'
        default: return ''
      }
    },
    getAlarmLogStatus(status) { // 任务状态
      switch (status) {
        case 'FAIL': return '失败'
        case 'SUCCESS': return '成功'
        default: return ''
      }
    }
  }
}
</script>

<style scoped>
  .fl-container {
    margin: 20px;
  }
  .fl-container >>> .el-form-item {
    margin-bottom: 5px!important;
  }
  .wl-pagination {
    margin-top: 5px;
  }
</style>
