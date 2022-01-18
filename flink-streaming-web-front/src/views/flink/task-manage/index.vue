<template>
  <div v-loading="loading">
    <div v-if="subPageFlag==false" class="fl-container">
      <!-- 查询与新增 -->
      <el-form ref="queryform" :model="queryform" :inline="true">
        <el-form-item>
          <el-input v-model="queryform.jobId" placeholder="Flink任务Id" class="wl-input" @input="handleQuery()" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryform.jobType" placeholder="任务类型" clearable @change="handleQuery()">
            <el-option label="SQL流任务" value="0" />
            <el-option label="SQL批任务" value="2" />
            <el-option label="JAR包" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryform.open" placeholder="开启状态" clearable @change="handleQuery()">
            <el-option label="关闭" value="0" />
            <el-option label="开启" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryform.jobName" placeholder="任务名称(模糊查询)" class="wl-input" @input="handleQuery()">
            <el-button slot="append" type="primary" icon="el-icon-search" class="wl-search" @click="handleQuery()" />
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-dropdown>
            <el-button type="primary" icon="el-icon-plus">新建任务<i class="el-icon-arrow-down el-icon--right" /></el-button>
            <el-dropdown-menu slot="dropdown">
              <router-link :to="{name:getRouteTaskName('create','SQL_STREAMING'), params:{flag:'create', context: queryContent(), data:{jobTypeEnum:'SQL_STREAMING'}}}" class="wl-append">
                <el-dropdown-item icon="iconfont my-icon-jiediansql">SQL流任务</el-dropdown-item>
              </router-link>
              <router-link :to="{name:getRouteTaskName('create','SQL_BATCH'), params:{flag:'create', context: queryContent(), data:{jobTypeEnum:'SQL_BATCH'}}}" class="wl-append">
                <el-dropdown-item icon="iconfont my-icon-file-SQL">SQL批任务</el-dropdown-item>
              </router-link>
              <router-link :to="{name:getRouteTaskName('create','JAR'), params:{flag:'create', context: queryContent(), data:{jobTypeEnum:'JAR'}}}" class="wl-append">
                <el-dropdown-item icon="iconfont my-icon-suffix-jar">JAR任务</el-dropdown-item>
              </router-link>
            </el-dropdown-menu>
          </el-dropdown>
        </el-form-item>
      </el-form>
      <el-form ref="queryform2" :model="queryform" :inline="true">
        <el-form-item>
          <el-radio-group v-model="queryform.status" class="wl-radio-group" size="small" @change="handleQuery()">
            <el-radio-button label="">所有</el-radio-button>
            <el-radio-button label="0">停止</el-radio-button>
            <el-radio-button label="1">运行中</el-radio-button>
            <el-radio-button label="2">启动中</el-radio-button>
            <el-radio-button label="3">提交成功</el-radio-button>
            <el-radio-button label="-1">失败</el-radio-button>
            <el-radio-button label="-2">其他</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <!-- 列表 -->
      <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" border>
        <el-table-column prop="id" :show-overflow-tooltip="true" label="ID" min-width="60" width="80" align="center" fixed>
          <template slot-scope="scope">
            <span style="margin-right:5px;">{{ scope.row.id }}</span>
            <el-tooltip class="item" effect="dark" :content="getTaskTypeName(scope.row.jobTypeEnum)" placement="right">
              <i v-if="scope.row.jobTypeEnum==='SQL_STREAMING'" class="iconfont my-icon-jiediansql" style="font-size:16px;" />
              <i v-if="scope.row.jobTypeEnum==='SQL_BATCH'" class="iconfont my-icon-file-SQL" style="font-size:16px;" />
              <i v-if="scope.row.jobTypeEnum==='JAR'" class="iconfont my-icon-suffix-jar" style="font-size:16px;" />
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="jobName" :show-overflow-tooltip="true" label="任务名称" min-width="100" align="center" fixed />
        <el-table-column prop="isOpen" :show-overflow-tooltip="true" label="开启" width="65" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.isOpen" :active-value="1" :inactive-value="0" active-color="#13ce66" @change="changeIsOpen($event, scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status===-2||scope.row.status==='UNKNOWN'" type="info" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else-if="scope.row.status===-1||scope.row.status==='FAIL'" type="danger" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else-if="scope.row.status===0||scope.row.status==='STOP'" type="warning" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else-if="scope.row.status===1||scope.row.status==='RUN'" type="success" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else-if="scope.row.status===2||scope.row.status==='STARTING'" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else-if="scope.row.status===3||scope.row.status==='SUCCESS'" type="success" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
            <el-tag v-else type="info" size="mini">{{ getStatusDesc(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deployModeEnum" :show-overflow-tooltip="true" label="运行模式" width="105" align="center" />
        <el-table-column prop="jobId" :show-overflow-tooltip="true" label="Flink任务Id" min-width="100" align="center">
          <template slot-scope="scope">
            <el-link v-if="scope.row.jobId" :href="scope.row.flinkRunUrl" target="_blank">{{ scope.row.jobId }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="editTime" :show-overflow-tooltip="true" label="最后提交时间" min-width="100" width="135" align="center">
          <template slot-scope="scope">
            <span>{{ formatDateTime(scope.row.editTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="editor" :show-overflow-tooltip="true" label="提交人员" min-width="70" width="100" align="center" />
        <el-table-column :show-overflow-tooltip="true" label="版本信息" min-width="80" align="center">
          <template slot-scope="scope">
            <router-link :to="{name:'HistoryTask', params:{flag:'tasklist', context:queryContent(), jobConfigId:scope.row.id}}">
              <el-link type="info" class="fl-version-text"><span class="fl-version-span">[{{ scope.row.version }}]</span>{{ scope.row.jobDesc }}</el-link>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="其他" :show-overflow-tooltip="true" align="center">
          <template slot-scope="scope">
            <el-link v-if="scope.row.alarmStrs">{{ scope.row.alarmStrs }}</el-link><!-- 辅助 -->
          </template>
        </el-table-column>
        <el-table-column prop="savepoint" label="保存状态" width="115" fixed="right">
          <template slot-scope="scope">
            <el-link v-if="scope.row.jobTypeEnum==='SQL_STREAMING'" type="primary" icon="el-icon-sell" @click.native="doRecoverSavePoint(scope.row)">恢复</el-link>
            <el-link v-if="scope.row.jobTypeEnum==='SQL_STREAMING'" type="success" icon="el-icon-sold-out" @click.native="savePoint(scope.row)">备份</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="operate" label="操作" width="210" fixed="right">
          <template slot-scope="scope">
            <el-link v-if="scope.row.isOpen===1&&scope.row.status!=='RUN'" type="success" icon="el-icon-video-play" @click.native="startTask(scope.row)">运行</el-link>
            <el-link v-if="scope.row.isOpen===1&&scope.row.status==='RUN'" type="warning" icon="el-icon-switch-button" @click.native="stopTask(scope.row)">停止</el-link>
            <router-link :to="{name:getRouteTaskName('view',scope.row.jobTypeEnum), params:{flag:'view', context:queryContent(), data:scope.row}}">
              <el-link type="info" icon="el-icon-view">查看</el-link>
            </router-link>
            <router-link v-if="scope.row.isOpen===0" :to="{name:getRouteTaskName('update',scope.row.jobTypeEnum), params:{flag:'update', context:queryContent(), data:scope.row}}">
              <el-link type="primary" icon="el-icon-edit-outline">修改</el-link>
            </router-link>
            <el-link type="primary" icon="el-icon-document-copy" @click.native="copyConfig(scope.row)">复制</el-link>
            <el-link v-if="scope.row.isOpen===0" type="danger" icon="el-icon-delete" @click.native="deleteTask(scope.row)">删除</el-link>
          </template>
        </el-table-column>
        <el-table-column label="日志" width="115" align="center">
          <template slot-scope="scope">
            <router-link v-if="scope.row.lastRunLogId!==null" :to="{name:'ViewTaskLogDetail', params:{flag:'tasklist', context:queryContent(), data:{id:scope.row.lastRunLogId}}}">
              <el-link type="info" icon="el-icon-message">详情</el-link>
            </router-link>
            <router-link :to="{name:'FlinkLogManage', params:{flag:'tasklist', context:queryContent(), jobConfigId:scope.row.id}}">
              <el-link type="info" icon="el-icon-chat-line-square">历史</el-link>
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
    <!-- 恢复任务弹出窗口-->
    <recover-save-point ref="recoverSavePoint" />
    <!-- 子菜单路由 -->
    <router-view />
  </div>
</template>

<script>
import { getTasks, openTask, startTask, stopTask, closeTask, savePoint, copyConfig, deleteTask } from '@/api/task'
import RecoverSavePoint from '@/views/flink/components/recover'

export default {
  name: 'TaskManage',
  components: {
    RecoverSavePoint
  },
  data() {
    return {
      loading: false,
      subPageFlag: false, // 当打开子菜单时，当前页内屏蔽
      queryform: {
        jobId: '',
        jobName: '',
        jobType: '',
        open: '',
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
    if (this.$route.name === 'FlinkTaskManage') {
      this.subPageFlag = false
      const params = this.$route.params
      if (params && params.currentPage) {
        this.queryform.jobId = params.jobId
        this.queryform.jobName = params.jobName
        this.queryform.jobType = params.jobType
        this.queryform.open = params.open
        this.queryform.status = params.status
        if (params.currentPage) { // 恢复分页状态
          this.count = params.count
          this.currentPage = params.currentPage
          this.pageSize = params.pageSize
        }
      }
      this.handleQuery()
    } else {
      this.subPageFlag = true
    }
  },
  methods: {
    queryContent() {
      return {
        count: this.count,
        currentPage: this.currentPage,
        pageSize: this.pageSize,
        jobId: this.queryform.jobId,
        jobName: this.queryform.jobName,
        jobType: this.queryform.jobType,
        open: this.queryform.open,
        status: this.queryform.status
      }
    },
    handleQuery(event) { // 查询
      this.pageshow = false
      this.getTasks()
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
    getTasks() { // 查询任务列表
      this.loading = true
      const jobName = this.queryform.jobName.trim()
      const { jobId, jobType, status, open } = this.queryform
      getTasks(this.currentPage, this.pageSize, jobName, jobId, jobType, status, open).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.list = data.data
        this.count = data.total
        if (this.count > 0 && this.list.length == 0) { // 调整PageNo
          this.currentPage = Math.ceil(this.count / this.pageSize)
          this.getTasks()
        }
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    changeIsOpen(callback, row) { // 开启或关闭任务
      const text = (callback === 1) ? '开启' : '关闭'
      const { id, jobName } = row
      this.$confirm(`是否${text}[${jobName}]`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (callback === 1) {
          this.loading = true
          openTask(id).then(response => {
            this.loading = false
            const { code, success, message, data } = response
            if (code !== '200' || !success) {
              row.isOpen = (callback === 1) ? 0 : 1
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.$message({ type: 'success', message: `开启[${jobName}]成功！` })
          }).catch(error => {
            this.loading = false
            row.isOpen = (callback === 1) ? 0 : 1
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        } else if (callback === 0) {
          this.loading = true
          closeTask(id).then(response => {
            this.loading = false
            const { code, success, message, data } = response
            if (code !== '200' || !success) {
              row.isOpen = (callback === 1) ? 0 : 1
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.$message({ type: 'success', message: `关闭[${jobName}]成功！` })
          }).catch(error => {
            this.loading = false
            row.isOpen = (callback === 1) ? 0 : 1
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        }
      }).catch(action => {
        row.isOpen = (callback === 1) ? 0 : 1
        // this.$message({ type: 'warning', message: '取消' })
      })
    },
    startTask(row) { // 启动任务
      this.loading = true
      const { id, jobName } = row
      startTask(id).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.handleQuery()
        this.$message({ type: 'info', message: `正在启动[${jobName}]，稍后请刷新！` })
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    stopTask(row) { // 停止任务
      const { id, jobName } = row
      this.$confirm(`确定要停止任务【${jobName}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        stopTask(id).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.handleQuery()
          this.$message({ type: 'success', message: `正在停止任务【${jobName}】，稍后请刷新！` })
        }).catch(error => {
          this.loading = false
          this.$message({ type: 'error', message: '请求异常！' })
          console.log(error)
        })
      })
    },
    savePoint(row) { // 备份任务状态（savePoint）
      const { id, jobName } = row
      this.$confirm(`确定要手动执行[${jobName}]savePoint吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.loading = true
        savePoint(id).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.$message({ type: 'success', message: `手动执行[${jobName}]savePoint成功！` })
        }).catch(error => {
          this.loading = false
          this.$message({ type: 'error', message: '请求异常！' })
          console.log(error)
        })
      })
    },
    copyConfig(row) { // 复制
      const { id, jobName } = row
      this.loading = true
      copyConfig(id).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.handleQuery()
        this.$message({ type: 'success', message: `复制[${jobName}]成功！` })
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    deleteTask(row) { // 删除
      const { id, jobName } = row
      this.$confirm(`确定要删除[${jobName}]吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteTask(id).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.handleQuery()
          this.$message({ type: 'success', message: `删除[${jobName}]成功！` })
        }).catch(error => {
          this.loading = false
          this.$message({ type: 'error', message: '请求异常！' })
          console.log(error)
        })
      })
    },
    doRecoverSavePoint(row) { // 从SavePoint中恢复任务
      this.$refs.recoverSavePoint.openDialog(row)
    },
    formatDateTime(date) {
      return this.dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    getStatusDesc(status) { // 任务状态
      switch (status) {
        case -2: return '未知'
        case -1: return '失败'
        case 0: return '停止'
        case 1: return '运行中'
        case 2: return '启动中'
        case 3: return '提交成功'
        case 'UNKNOWN': return '未知'
        case 'FAIL': return '失败'
        case 'STOP': return '停止'
        case 'RUN': return '运行中'
        case 'STARTING': return '启动中'
        case 'SUCCESS': return '提交成功'
        default: return ''
      }
    },
    getTaskTypeName(tasktype) {
      switch (tasktype) {
        case 'SQL_STREAMING': return 'SQL流任务'
        case 'SQL_BATCH': return 'SQL批任务'
        case 'JAR': return 'JAR包'
        default: return tasktype
      }
    },
    getRouteTaskName(flag, jobTypeEnum) {
      switch (flag) {
        case 'create':
          switch (jobTypeEnum) {
            case 'SQL_STREAMING': return 'CreateSqlStreamingTask'
            case 'SQL_BATCH': return 'CreateSqlBatchTask'
            case 'JAR': return 'CreateJarTask'
            default: return ''
          }
        case 'update':
          switch (jobTypeEnum) {
            case 'SQL_STREAMING': return 'UpdateSqlStreamingTask'
            case 'SQL_BATCH': return 'UpdateSqlBatchTask'
            case 'JAR': return 'UpdateJarTask'
            default: return ''
          }
        case 'view':
          switch (jobTypeEnum) {
            case 'SQL_STREAMING': return 'ViewSqlStreamingTask'
            case 'SQL_BATCH': return 'ViewSqlBatchTask'
            case 'JAR': return 'ViewJarTask'
            default: return ''
          }
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
  .wl-table {
    width:100%;
    margin-top: 0px;
  }
  .wl-icon {
    margin-right: 2px;
  }
  .wl-operate {
    overflow: hidden;
  }
  .wl-input {
    width: 200px;
  }
  .wl-form-input {
    width: 360px;
  }
  .wl-search {
    background: #f5f7fa;
  }
  .fl-version-text {
    /*display: inline;
    white-space: nowrap;
    text-overflow: ellipsis;*/
  }
  .fl-version-span {
    color: #337ab7;
    text-decoration: underline;
  }
  .wl-search >>> .el-button {
    background: #f00;
  }
  .wl-deploy-edit {
    padding-left: 10px;
  }
  .wl-deploy-to {
    padding-left: 8px;
  }
  .wl-delete {
    color: #f56c6c;
  }
  .wl-pagination {
    margin-top: 5px;
  }
  .wl-myicon {
    font-size: 14px;
  }
  .wl-table-icon >>> span {
    margin-left: 2px;
  }
  .wl-radio-group >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background-color: #f5f7fa;
    border-color: #DCDFE6;
    -webkit-box-shadow: -1px 0 0 0 #DCDFE6;
    box-shadow: -1px 0 0 0 #DCDFE6;
  }
  .wl-radio-group >>> .el-radio-button__inner {
    color: #606266;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
    font-size: 12px;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  .wl-table >>> .el-link [class*=el-icon-] + span {
    margin-left: 1px;
  }
  .wl-table >>> .el-link {
    margin-right: 2px;
    margin-left: 2px;
  }
</style>
