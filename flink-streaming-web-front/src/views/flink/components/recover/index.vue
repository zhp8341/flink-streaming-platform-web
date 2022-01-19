<!-- 恢复SavePoint -->
<template>
  <el-dialog
    v-loading.fullscreen.lock="loading"
    :title="title"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :modal-append-to-body="false"
    class="wl-dialog"
    width="800px"
    @close="doClose()"
  >
    <span class="wl-title">只显示最近10次备份(每一小时自动备份或者停止任务的时候备份)</span>
    <el-popover placement="top-start" trigger="hover">
      <div class="wl-popover">
        <p>备注1：如果sql语句发生变更或者业务逻辑发生变化，此时从savepoint恢复可能导致数据结果错误</p>
        <p>备注2：yarn模式下和集群模式下统一目录是(必须绑定hdfs)：hdfs:///flink/savepoint/flink-streaming-platform-web/</p>
        <p>备注3：LOCAL模式本地模式下保存在flink客户端的根目录下</p>
        <p>备注4：hdfs:///flink/savepoint/flink-streaming-platform-web/ 建议提前创建好</p>
      </div>
      <span slot="reference">
        <i class="el-icon-info" />
      </span>
    </el-popover>
    <el-form ref="form" :model="form" :inline="true" :rules="rules" style="padding-top: 10px;">
      <el-form-item prop="savepoint">
        <el-input ref="inputSavePoint" v-model="form.savepoint" placeholder="手动增加SavePoint地址" class="wl-input" @blur="blurSavePoint()" />
      </el-form-item>
      <el-form-item>
        <el-button @click="addSavepoint()">手动添加</el-button>
      </el-form-item>
    </el-form>
    <!-- 列表 -->
    <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" highlight-current-row border @current-change="handleCurrentChange">
      <el-table-column width="50">
        <template slot-scope="scope">
          <el-radio v-model="radioIndex" :label="scope.$index" @change.native="getCurrentRow(scope.$index)" />
        </template>
      </el-table-column>
      <el-table-column prop="id" :show-overflow-tooltip="true" label="备份编号" width="80" align="center" />
      <el-table-column prop="savepointPath" :show-overflow-tooltip="true" label="备份地址" />
      <el-table-column prop="backupTime" :show-overflow-tooltip="true" label="备份时间" width="135" />
    </el-table>
    <span slot="footer" class="dialog-footer">
      <el-button @click="doCancel()">取 消</el-button>
      <el-button type="primary" @click="doConfirm()">恢复任务</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { querySavePointList10, addSavepoint } from '@/api/savepoint'
import { startSavepoint } from '@/api/task'

export default {
  name: 'RecoverSavePoint',
  data() {
    return {
      loading: false,
      visible: false,
      title: '恢复任务运行',
      form: { // 基本设置表单
        taskid: '',
        savepoint: ''
      },
      rules: {
        savepoint: [{ required: true, message: '请输入savepoint地址', trigger: 'change' }]
      },
      task: {},
      list: [],
      currentRow: null,
      radioIndex: false
    }
  },
  methods: {
    openDialog(task) {
      this.visible = true
      this.list = []
      this.form.savepoint = ''
      this.form.taskid = task.id
      this.task = task
      this.title = `恢复任务运行[${task.jobName}]`
      this.currentRow = null
      this.radioIndex = false
      this.querySavePointList(task.id)
    },
    querySavePointList(id) { // 查询SavePoint历史列表（最近10条）
      this.loading = true
      this.radioIndex = false
      querySavePointList10(id).then(response => {
        this.loading = false
        this.$refs.form.clearValidate()
        const { code, data, success, message } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.list = (data && data.data) ? data.data : []
        this.loading = false
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    addSavepoint() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          const jobConfigId = this.form.taskid
          const savepointPath = this.form.savepoint
          addSavepoint(jobConfigId, savepointPath).then(response => {
            this.loading = false
            const { code, data, success, message } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.list = (data && data.data) ? data.data : []
            this.querySavePointList(jobConfigId)
          }).catch(error => {
            this.loading = false
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        } else {
          this.$refs.inputSavePoint.focus()
        }
      })
    },
    blurSavePoint() { //
      this.$refs.form.clearValidate()
    },
    handleCurrentChange(row) { // 选中行
      if (row) {
        this.currentRow = row
        const index = this.list.findIndex(item => item.id == this.currentRow.id)
        if (index > -1) {
          this.radioIndex = index
        }
      }
    },
    getCurrentRow(index) { // 设置单选框索引
      this.radioIndex = index
    },
    doConfirm() {
      if (!this.currentRow) {
        this.$message({ type: 'error', message: '请选择备份记录！' })
        return
      }
      const id = this.form.taskid
      const savepointId = this.currentRow.id
      const savepointPath = this.currentRow.savepointPath
      this.$confirm(`是否恢复任务[备份：${savepointId}]！`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        startSavepoint(id, savepointId).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.$message({ type: 'info', message: `正在恢复任务[备份：${savepointId}]，稍后请刷新！` })
          this.$parent.getTasks()
          this.visible = false
        }).catch(error => {
          this.loading = false
          this.$message({ type: 'error', message: '请求异常！' })
          console.log(error)
        })
      })
    },
    doCancel() {
      this.visible = false
    },
    doClose() {
      this.list = []
      this.form.savepoint = ''
      this.form.taskid = ''
      this.task = null
      this.title = ''
      this.currentRow = null
      this.radioIndex = false
    }
  }
}
</script>

<style scoped>
  .wl-input {
    width: 660px;
  }
  .wl-title {
    font-size: 16px;
    font-weight: 600;
    cursor: default;
    padding-right: 2px;
  }
  .wl-popover {
    line-height: 8px;
    color: red;
  }
  .wl-popover p {
    line-height: 8px;
  }
  .wl-dialog >>> .el-dialog__body {
    padding: 5px 20px !important;
  }
  .wl-dialog >>> .el-radio {
    margin-left: 6px;
  }
  .wl-dialog >>> .el-radio .el-radio__label {
    display: none;
  }
</style>
