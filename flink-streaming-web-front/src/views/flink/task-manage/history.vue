<template>
  <div v-loading="loading">
    <div v-if="subPageFlag==false" :class="backFlag?'fl-container2':'fl-container'">
      <el-tooltip v-if="backFlag==true" class="item" effect="dark" content="返回" placement="right">
        <i ref="backbutton" class="el-icon-d-arrow-left fl-back" @click="handleBack()" />
      </el-tooltip>
      <!-- 查询 -->
      <el-form ref="queryform" :model="queryform" :inline="true">
        <el-form-item>
          <el-input v-model="queryform.jobConfigId" placeholder="任务编号" class="wl-input" @input="handleQuery()" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryform.jobName" placeholder="任务名称(模糊查询)" class="wl-input" @input="handleQuery()">
            <el-button slot="append" type="primary" icon="el-icon-search" class="wl-search" @click="handleQuery()" />
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery()">查询</el-button>
        </el-form-item>
      </el-form>
      <!-- 列表 -->
      <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" border>
        <el-table-column prop="id" :show-overflow-tooltip="true" label="编号" min-width="60" width="80" align="center" fixed />
        <el-table-column prop="jobConfigId" :show-overflow-tooltip="true" label="任务编号" min-width="60" width="80" align="center" fixed>
          <template slot-scope="scope">
            <span style="margin-right:5px;">{{ scope.row.jobConfigId }}</span>
            <el-tooltip class="item" effect="dark" :content="getTaskTypeName(scope.row.jobTypeEnum)" placement="right">
              <i v-if="scope.row.jobTypeEnum==='SQL_STREAMING'" class="iconfont my-icon-jiediansql" style="font-size:16px;" />
              <i v-if="scope.row.jobTypeEnum==='SQL_BATCH'" class="iconfont my-icon-file-SQL" style="font-size:16px;" />
              <i v-if="scope.row.jobTypeEnum==='JAR'" class="iconfont my-icon-suffix-jar" style="font-size:16px;" />
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="version" :show-overflow-tooltip="true" label="版本号" min-width="60" width="80" align="center" fixed />
        <el-table-column prop="jobName" :show-overflow-tooltip="true" label="任务名称" min-width="100" align="center" fixed />
        <el-table-column prop="jobDesc" :show-overflow-tooltip="true" label="版本说明" min-width="150" align="center" fixed />
        <el-table-column prop="deployMode" :show-overflow-tooltip="true" label="运行模式" width="105" align="center" />
        <el-table-column prop="createTime" :show-overflow-tooltip="true" label="创建时间" min-width="100" width="135" align="center">
          <template slot-scope="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="editor" :show-overflow-tooltip="true" label="提交人员" min-width="70" width="100" align="center" />
        <el-table-column label="详情" width="115" align="center">
          <template slot-scope="scope">
            <router-link :to="{name: getRouteTaskName(scope.row.jobTypeEnum), params:{flag:'history', context:queryContent(), data:scope.row}}">
              <el-link type="info" icon="el-icon-message">详情</el-link>
            </router-link>
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
    </div>
    <!-- 子菜单路由 -->
    <router-view />
  </div>
</template>

<script>
import { getTaskHistory } from '@/api/task'

export default {
  name: 'HistoryTask',
  data() {
    return {
      loading: false,
      subPageFlag: false, // 当打开子菜单时，当前页内屏蔽
      backFlag: false,
      params: {
        flag: '', // tasklist
        data: {},
        context: '' // 父页面传递过来的参加，返回时带给父页面恢复上下文
      },
      queryform: {
        jobConfigId: '',
        jobName: ''
      },
      list: [],
      count: 0,
      pageSize: 15,
      currentPage: 1,
      pageshow: true
    }
  },
  mounted() {
    if (this.$route.name === 'HistoryTask') {
      this.subPageFlag = false
      const params = this.$route.params
      if (params) {
        this.params.flag = params.flag
        this.queryform.jobConfigId = (params.jobConfigId) ? params.jobConfigId : ''
        this.queryform.jobName = (params.jobName) ? params.jobName : ''
        if (params.currentPage) { // 恢复分页状态
          this.count = params.count
          this.currentPage = params.currentPage
          this.pageSize = params.pageSize
        }
        if (params.flag === 'tasklist') { // 保存由任务列表跳转过来的状态
          this.backFlag = true
          this.params.flag = params.flag
          this.params.data = params.data
          this.params.context = params.context
        }
        if (params.parentContent) { // 详情回退后，保存继续回退到任务列表的状态
          this.backFlag = true
          this.params.context = params.parentContent
        }
      }
      this.handleQuery()
    } else {
      this.subPageFlag = true
    }
  },
  methods: {
    handleBack() { // 返回
      this.$router.replace({ name: 'FlinkTaskManage', params: this.params.context })
    },
    queryContent() {
      return {
        count: this.count,
        currentPage: this.currentPage,
        pageSize: this.pageSize,
        jobConfigId: this.queryform.jobConfigId,
        jobName: this.queryform.jobName,
        parentContent: this.params.context
      }
    },
    handleSizeChange(pageSize) { // 设置分页大小事件
      this.pageSize = pageSize
      this.handleQuery()
    },
    handleCurrentChange(pageno) { // 处理分页事件
      this.currentPage = pageno
      this.handleQuery()
    },
    handleQuery() { // 查询任务历史版本列表
      this.pageshow = false
      this.getTaskHistory()
      this.$nextTick(() => { this.pageshow = true }) // 解决界面页码不更新问题
    },
    getTaskHistory() {
      this.loading = true
      const data = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        jobConfigId: this.queryform.jobConfigId,
        jobName: this.queryform.jobName
      }
      getTaskHistory(data).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.count = data.total
        this.list = data.data
        this.list.forEach((row) => {
          row.deployModeEnum = row.deployMode
        })
        if (this.count > 0 && this.list.length == 0) { // 调整PageNo
          this.currentPage = Math.ceil(this.count / this.pageSize)
          this.getTaskHistory(data)
        }
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    formatDateTime(date) {
      return this.dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    getTaskTypeName(tasktype) {
      switch (tasktype) {
        case 'SQL_STREAMING': return 'SQL流任务'
        case 'SQL_BATCH': return 'SQL批任务'
        case 'JAR': return 'JAR包'
        default: return tasktype
      }
    },
    getRouteTaskName(jobType) {
      switch (jobType) {
        case 'SQL_STREAMING': return 'ViewHistorySqlStreamingTask'
        case 'SQL_BATCH': return 'ViewHistorySqlBatchTask'
        case 'JAR': return 'ViewHistoryJarTask'
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
  .fl-container2 {
    margin: 0px 20px 20px 20px;
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
  .fl-container >>> .el-form-item {
    margin-bottom: 5px!important;
  }
  .wl-pagination {
    margin-top: 5px;
  }
</style>
