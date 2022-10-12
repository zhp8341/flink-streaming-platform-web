<template>
  <div v-loading="loading" class="fl-container">
    <div style="margin-bottom:2px;color:red">
      <span class="wl-title">1、不支持集群部署的时候使用此功能,另外jar全部存在本地服务器上 ./flink-streaming-platform-web/upload_jars</span>
    </div>
    <div style="margin-bottom:2px;color:red">
      <span class="wl-title">2、该功能主要是提供了jar管理:如：连接器jar、udf的jar 等 方便创建SQL流任务需要的jar</span>
    </div>

    <el-form ref="queryform" :model="queryform" :inline="true">
      <el-form-item>
        <el-form-item>
          <el-input v-model="queryform.fileName" placeholder="文件名(模糊查询)" class="wl-input" @input="handleQuery()">
            <el-button slot="append" type="primary" icon="el-icon-search" class="wl-search" @click="handleQuery()" />
          </el-input>
        </el-form-item>
        <uploader
          ref="uploader"
          :options="options"
          :file-status-text="fileStatusText"
          class="uploader-example"
          @file-complete="fileComplete"
          @complete="complete"
        >
          <uploader-unsupport />
          <uploader-drop>
            <p>请上传jar包</p>
            <uploader-btn :attrs="attrs">上传jar文件</uploader-btn>
            <uploader-btn :directory="true">选择文件夹</uploader-btn>
          </uploader-drop>
          <uploader-list />
        </uploader>
      </el-form-item>
    </el-form>
    <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" border>
      <el-table-column prop="id" :show-overflow-tooltip="true" label="编号" min-width="50" width="80" align="center" />
      <el-table-column prop="fileName" :show-overflow-tooltip="true" label="文件名" align="center" />
      <el-table-column prop="downloadJarHttp" :show-overflow-tooltip="true" label="http地址" min-width="40" align="center" />
      <el-table-column prop="createTimeStr" :show-overflow-tooltip="true" label="上传时间" min-width="25" align="center" />
      <el-table-column prop="operate" label="操作" width="180" fixed="right" align="center">
        <template slot-scope="scope">
          <el-link type="primary" icon="el-icon-delete" @click.native="deleteFile(scope.row)">删除</el-link>
          <el-link type="primary" @click.native="doCopy(scope.row)">复制URL</el-link>
          <el-link type="primary" @click.native="doCopyFileName(scope.row)">复制文件名</el-link>
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
</template>

<script>
import { queryUploadFile, deleteFile } from '@/api/upload'

export default {
  name: 'UploadManage',
  data() {
    return {
      options: {
        target: '/api/upload',
        chunkSize: 1024 * 1024 * 1024,
        testChunks: false
      },
      attrs: {
        // 接受的文件类型 根据实际需要
        accept: ['.JAR']
      },
      fileStatusText(status, response) {
        const statusTextMap = {
          success: '成功了',
          error: '出错了',
          uploading: '上传中',
          paused: '暂停中',
          waiting: '等待中'
        }
        if (status === 'success' || status === 'error') {
          if (response.code == 200) {
            return statusTextMap[status]
          } else {
            alert(response.message)
            return statusTextMap['error']
          }
        } else {
          return statusTextMap[status]
        }
      },
      loading: true,
      queryform: {
        fileName: ''
      },
      list: [],
      count: 0,
      pageSize: 10,
      currentPage: 1,
      pageshow: true
    }
  },
  mounted() {
    this.handleQuery()
    this.$nextTick(() => {
      window.uploader = this.$refs.uploader.uploader
    })
  },
  methods: {
    handleQuery(event) { // 查询
      this.pageshow = false
      this.queryUserList()
      this.$nextTick(() => { this.pageshow = true }) // 解决界面页码不更新问题
    },
    handleSizeChange(pageSize) { // 设置分页大小事件
      this.pageSize = pageSize
      this.handleQuery()
    },
    complete() {
      console.log('complete', arguments)
      this.queryUserList()
    },
    fileComplete() {
      this.queryUserList()
      console.log('file complete', arguments)
    },
    handleCurrentChange(pageno) { // 处理分页事件
      this.currentPage = pageno
      this.handleQuery()
    },
    queryUserList() {
      this.loading = true
      const fileName = this.queryform.fileName ? this.queryform.fileName.trim() : ''
      queryUploadFile(this.currentPage, this.pageSize, fileName).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.count = data.total
        this.list = data.data
        if (this.count > 0 && this.list.length === 0) { // 调整PageNo
          this.currentPage = Math.ceil(this.count / this.pageSize)
          this.queryUserList()
        }
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    doCopy(row) {
      const { id, fileName, downloadJarHttp } = row
      this.$copyText(downloadJarHttp).then(function(e) {
        alert('复制jar地址成功:' + downloadJarHttp)
      }, function(e) {
        alert('Can not copy')
        console.log(e)
      })
    },
    doCopyFileName(row) {
      const { id, fileName, downloadJarHttp } = row
      this.$copyText(fileName).then(function(e) {
        alert('复制jar文件名字成功:' + fileName)
      }, function(e) {
        alert('Can not copy')
        console.log(e)
      })
    },
    deleteFile(row) { // 删除
      const { id, fileName } = row
      this.$confirm(`确定要删除[${fileName}]吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteFile(id).then(response => {
          this.loading = false
          const { code, success, message, data } = response
          if (code !== '200' || !success) {
            this.$message({ type: 'error', message: (message || '请求数据异常！') })
            return
          }
          this.handleQuery()
          this.$message({ type: 'success', message: `删除[${fileName}]成功！` })
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
.fl-container {
  margin: 20px;
}
.wl-pagination {
  margin-top: 5px;
}
.fl-container >>> .el-form-item {
  margin-bottom: 25px!important;
}
.wl-table >>> .el-link [class*=el-icon-] + span {
  margin-left: 1px;
}
.wl-table >>> .el-link {
  margin-right: 2px;
  margin-left: 2px;
}
.wl-title {
  font-size: 16px;
  font-weight: 600;
  cursor: default;
  padding-right: 2px;
}
</style>
