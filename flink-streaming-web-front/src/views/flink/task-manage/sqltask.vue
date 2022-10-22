<template>
  <div v-loading="loading" :class="'fl-sqltask-container fl-task-edit'+(isReadOnly?' fl-task-edit__isRead':'')">
    <el-tooltip class="item" effect="dark" content="返回" placement="right">
      <i ref="backbutton" class="el-icon-d-arrow-left fl-back" @click="handleBack()" />
    </el-tooltip>
    <el-form ref="taskform" :model="form" :rules="rules" :disabled="isReadOnly" label-width="80px" size="small">
      <el-row v-if="params.flag==='history'">
        <el-col :span="10">
          <el-form-item label="版本号" prop="version">
            <el-input v-model="form.version" />
          </el-form-item>
        </el-col>
        <el-col :span="14">
          <el-form-item label="备份时间" prop="createTime" label-width="100px">
            <el-input :value="formatDateTime(form.createTime)" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="任务编号" prop="id">
            <el-input v-model="form.id" placeholder="任务编号" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="22">
          <el-form-item label="运行配置" prop="flinkRunConfig">
            <el-input v-model="form.flinkRunConfig" placeholder="请输入任务提交所需的资源参数如： -p 2  -Dtaskmanager.numberOfTaskSlots=2 -Dyarn.application.queue=default" />
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item label="参数说明" label-width="100px">
            <span slot="label"><a href="https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/deployment/config/" style="color: blue" target="_blank">参数说明</a>
              <el-popover placement="right" trigger="hover">
                <template v-if="form.deployModeEnum==='LOCAL'">
                  在LOCAL模式下无需配置
                </template>
                <template v-else-if="form.deployModeEnum==='YARN_PER'">
                  参数如 ： -p 2 -Dyarn.application.queue=default -Dtaskmanager.numberOfTaskSlots=2 -Djobmanager.memory.process.size=1024m  -Dtaskmanager.memory.process.size=2048m
                </template>
                <template v-else-if="form.deployModeEnum==='YARN_APPLICATION'">
                  参数如 ： -p 2 -Dyarn.application.queue=default -Dtaskmanager.numberOfTaskSlots=2 -Djobmanager.memory.process.size=1024m  -Dtaskmanager.memory.process.size=2048m
                </template>
                <template v-else-if="form.deployModeEnum==='STANDALONE'">
                  -d,--detached 如果存在，则以分离模式运行作业<br>
                  -p,--parallelism &lt;parallelism&gt; 运行程序的并行性。用于覆盖配置中指定的默认值的可选标志<br>
                  -s,--fromSavepoint &lt;savepointPath&gt; 从savepointPath中恢复任务(如：-s hdfs:///flink/savepoint-1537)<br>
                  其他运行参数可通过 flink -h查看
                </template>
                <template v-else>
                  请选择运行模式
                </template>
                <i slot="reference" class="el-icon-info" />
              </el-popover>
            </span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="任务名称" prop="jobName">
            <el-input v-model="form.jobName" placeholder="请输入任务名称" />
          </el-form-item>
        </el-col>
        <el-col :span="14">
          <el-form-item label="状态保存" prop="flinkCheckpointConfig" label-width="100px">
            <span slot="label">状态保存
              <el-popover placement="bottom-start" trigger="hover">
                不填默认不开启checkpoint机制，支持如下参数：<br>
                checkpointInterval：整数，默认每60s保存一次checkpoint，单位毫秒<br>
                checkpointingMode：EXACTLY_ONCE 或者 AT_LEAST_ONCE，一致性模式 默认EXACTLY_ONCE，单位字符<br>
                checkpointTimeout：6000，默认超时10 minutes，单位毫秒<br>
                checkpointDir：保存地址 如 hdfs://hcluster/flink/checkpoints/ 注意目录权限<br>
                tolerableCheckpointFailureNumber：1，设置失败次数 默认一次<br>
                asynchronousSnapshots：true 或者 false，是否异步<br>
                externalizedCheckpointCleanup：DELETE_ON_CANCELLATION或者RETAIN_ON_CANCELLATION，作业取消后检查点是否删除（可不填）<br>
                stateBackendType：0 或者 1 或者 2，默认1 后端状态 0:MemoryStateBackend 1: FsStateBackend 2:RocksDBStateBackend<br>
                enableIncremental：true 或者 false，是否采用增量 只有在 stateBackendType 2模式下才有效果 即RocksDBStateBackend<br>
                如： -stateBackendType 2 -enableIncremental true -checkpointInterval 900000 -checkpointDir hdfs:///flink/checkpoints/hearbeat_to_hive
                <i slot="reference" class="el-icon-info" />
              </el-popover>
            </span>
            <el-input v-model="form.flinkCheckpointConfig" placeholder="Checkpoint配置，如：-checkpointDir hdfs:///flink/checkpoint/" :disabled="!(this.form.jobType===0)" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-row>
            <el-col :span="24">
              <el-form-item label="任务描述" prop="jobDesc">
                <el-input v-model="form.jobDesc" placeholder="请输入任务描述" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="运行模式" prop="deployModeEnum">
                <el-select v-model="form.deployModeEnum" placeholder="请选择运行模式" class="fl-form-item">
                  <el-option label="Local Cluster" value="LOCAL" />
                  <el-option label="Standalone Cluster" value="STANDALONE" />
                  <el-option label="YARN PER" value="YARN_PER" />
                  <el-option label="YARN APPLICATION" value="YARN_APPLICATION" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="14">
          <el-row>
            <el-col :span="24">
              <el-form-item label="三方JAR" prop="extJarPath" label-width="100px">
                <span slot="label"> <a href="#/flink/jarManage/index" style="color: red" target="_blank">点击jar管理</a>
                  三方JAR
                  <el-popover placement="right" trigger="hover">
                    自定义udf、连接器等jar地址，多个请使用换行(如 http://xxxx.com/udf.jar 或者 xxx.jar 两者都支持) 目前只支持http 可在三方jar管理
                    <i slot="reference" class="el-icon-info" />
                  </el-popover>
                </span>
                <el-input v-model="form.extJarPath" type="textarea" resize="none" placeholder="自定义udf、连接器等jar地址，多个请使用换行(如 http://xxxx.com/udf.jar 或者 xxx.jar 两者都支持) 目前只支持http 可在三方jar管理" class="fl-jar-input" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row class="fl-alarm-row">
        <el-col :xs="16" :sm="16" :md="14" :lg="12">
          <el-form-item label="告警配置" prop="alarmTypes">
            <el-checkbox-group v-model="form.alarmTypes">
              <el-checkbox :label="1">钉钉告警</el-checkbox>
              <el-checkbox :label="2">Http回调告警</el-checkbox>
              <el-checkbox :label="3">任务退出自动拉起</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
        <el-col :xs="4" :sm="4" :md="5" :lg="6">
          <el-form-item label="开启状态">
            <el-switch v-model="form.isOpen" :active-value="1" :inactive-value="0" active-color="#13ce66" disabled />
          </el-form-item>
        </el-col>
        <el-col :xs="4" :sm="4" :md="5" :lg="6">
          <el-form-item label="运行状态">
            <el-tag v-if="form.status===-2||form.status==='UNKNOWN'" type="info" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else-if="form.status===-1||form.status==='FAIL'" type="danger" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else-if="form.status===0||form.status==='STOP'" type="warning" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else-if="form.status===1||form.status==='RUN'" type="success" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else-if="form.status===2||form.status==='STARTING'" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else-if="form.status===3||form.status==='SUCCESS'" type="success" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
            <el-tag v-else type="info" size="mini">{{ getStatusDesc(form.status) }}</el-tag>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :class="getTaskClass()">
        <el-col :span="24">
          <el-form-item label="SQL脚本" prop="flinkSql">
            <codemirror ref="cm" :value="form.flinkSql" :options="cmOptions" class="fl-codemirror" @changes="doSqlChange" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isReadOnly===false" class="fl-button-row">
        <el-col :span="24">
          <el-form-item label="">
            <el-button type="primary" @click="submitTask()">提 交</el-button>
            <el-button type="primary" plain @click="formatSQL()">格式化代码</el-button>
            <el-button type="primary" plain @click="checkSQL()">SQL预校验</el-button>
            <el-popover placement="top-start" trigger="hover">
              SQL预校验<br>
              备注：只能校验单个sql语法正确与否, 不能校验上下文之间关系，如：这张表是否存在、数据类型是否正确等无法校验<br>
              总之不能完全保证运行的时候sql没有异常，只是能校验出一些语法错误
              <i slot="reference" class="el-icon-info" />
            </el-popover>
            <el-button type="text" style="color:#13ce66">{{ form.checkSQLResult }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { CodeMirror, codemirror } from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/show-hint'
import 'codemirror/theme/mbo.css'
import 'codemirror/mode/sql/sql.js'
import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/hint/sql-hint.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/selection/active-line'
import 'codemirror/addon/selection/selection-pointer'

import { checkfSql, addConfig, editConfig } from '@/api/task'

export default {
  name: 'SQLTask',
  components: {
    codemirror
  },
  data() {
    var checkFlinkRunConfig = (rule, value, callback) => {
      if (this.form.deployModeEnum !== 'YARN_PER') {
        return callback()
      }
      if (!value || value.trim() === '') {
        return callback(new Error('请选择运行参数'))
      }
      return callback()
    }
    return {
      loading: false,
      params: {
        flag: '', // create,update,view
        data: {},
        context: '' // 父页面传递过来的参加，返回时带给父页面恢复上下文
      },
      isReadOnly: false,
      form: {
        id: '',
        jobName: '',
        jobDesc: '',
        jobType: 0,
        deployModeEnum: '',
        flinkRunConfig: '',
        flinkCheckpointConfig: '',
        flinkSql: '',
        alarmTypes: [],
        extJarPath: '',
        isOpen: '',
        status: '',
        checkSQLResult: '',
        version: '',
        createTime: ''
      },
      rules: {
        jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        jobDesc: [{ required: true, message: '请输入任务描述', trigger: 'blur' }],
        deployModeEnum: [{ required: true, message: '请选择运行模式', trigger: 'blur' }],
        flinkSql: [{ required: true, message: '请输入SQL脚本', trigger: 'blur' }],
        flinkRunConfig: [{ validator: checkFlinkRunConfig, trigger: 'blur' }]
      },
      cmOptions: {
        value: '',
        mode: 'text/x-mysql', // flink/x-fsql, text/x-mysql, text/x-sh
        theme: 'mbo', // solarized light,base16-light,cobalt,default,mbo,cobalt
        readOnly: false,
        tabSize: 4,
        line: true,
        lineNumbers: true,
        indentUnit: 4, // 缩进块用多少个空格表示 默认是2
        matchBrackets: true, // 括号匹配（这个需要导入codemirror的matchbrackets.js文件）
        extraKeys: { 'Ctrl': 'autocomplete' }, // 自定义快捷键
        hintOptions: { // 自定义提示选项
          completeSingle: false, // 当匹配只有一项的时候是否自动补全
          tables: {
            'table.dynamic-table-options.enabled': [],
            'table.sql-dialect': [],
            'table.local-time-zone': [],
            'table.generated-code.max-length': [],
            'table.exec': ['state.ttl', 'source.idle-timeout',
              'source.cdc-events-duplicate', 'window-agg.buffer-size-limit', 'source.cdc-events-duplicate',
              'mini-batch.enabled', 'mini-batch.allow-latency', 'mini-batch.enabled', 'mini-batch.allow-latency',
              'mini-batch.size', 'sink.not-null-enforcer', 'resource.default-parallelism', 'async-lookup.buffer-capacity',
              'async-lookup.timeout'],
            'table.optimizer': ['distinct-agg.split.enabled',
              'distinct-agg.split.bucket-num',
              'agg-phase-strategy',
              'reuse-sub-plan-enabled',
              'reuse-source-enabled',
              'source.predicate-pushdown-enabled',
              'join-reorder-enabled']
          }
        }
      }
    }
  },
  mounted() {
    const params = this.$route.params
    this.params.flag = params.flag
    this.params.context = params.context
    this.params.data = params.data
    this.isReadOnly = !(params.flag === 'create' || params.flag === 'update')
    this.cmOptions.readOnly = this.isReadOnly

    const task = params.data
    this.form.id = task.id ? task.id : ''
    this.form.jobName = task.jobName ? task.jobName : ''
    this.form.jobDesc = task.jobDesc ? task.jobDesc : ''
    this.form.jobType = task.jobTypeEnum ? this.getJobType(task.jobTypeEnum) : 0
    this.form.deployModeEnum = task.deployModeEnum ? task.deployModeEnum : ''
    this.form.flinkRunConfig = task.flinkRunConfig ? task.flinkRunConfig : ''
    this.form.flinkCheckpointConfig = task.flinkCheckpointConfig ? task.flinkCheckpointConfig : ''
    this.form.flinkSql = task.flinkSql ? task.flinkSql : ''
    this.form.alarmTypes = task.alarmTypes ? task.alarmTypes : []
    this.form.extJarPath = task.extJarPath ? task.extJarPath : ''
    this.form.isOpen = task.isOpen ? task.isOpen : ''
    this.form.status = task.status ? task.status : ''
    this.form.version = task.version ? task.version : ''
    this.form.createTime = task.createTime ? task.createTime : ''
    //
    this.$refs.cm.codemirror.on('keypress', (cm) => {
      cm.showHint()
    })
  },
  methods: {
    handleBack() { // 返回
      const routerName = this.params.flag === 'history' ? 'HistoryTask' : 'FlinkTaskManage'
      this.$router.replace({ name: routerName, params: this.params.context })
    },
    submitTask() { // 提交修改、新建表单
      this.form.flinkSql = this.$refs.cm.codemirror.getValue() // codemirror 双向绑定有问题
      this.$refs.taskform.validate((valid) => {
        if (valid) {
          const jobName = this.form.jobName
          const alarmTypes = this.form.alarmTypes.join(',')
          const data = {
            id: this.form.id,
            jobName: jobName,
            jobDesc: this.form.jobDesc,
            deployMode: this.form.deployModeEnum,
            flinkRunConfig: this.form.flinkRunConfig,
            flinkCheckpointConfig: this.form.flinkCheckpointConfig,
            flinkSql: this.form.flinkSql,
            jobType: this.form.jobType,
            alarmTypes: alarmTypes,
            extJarPath: this.form.extJarPath
          }
          if (!data.id && this.params.flag === 'create') {
            addConfig(data).then(response => {
              this.loading = false
              const { code, data, success, message } = response
              if (code !== '200' || !success) {
                this.$message({ type: 'error', message: (message || '请求数据异常！') })
                return
              }
              this.$message({ type: 'success', message: `新增任务[${jobName}]成功！` })
              this.$refs.backbutton.click()
            }).catch(error => {
              this.loading = false
              this.$message({ type: 'error', message: '请求异常！' })
              console.log(error)
            })
          } else if (data.id && this.params.flag === 'update') {
            editConfig(data).then(response => {
              this.loading = false
              const { code, data, success, message } = response
              if (code !== '200' || !success) {
                this.$message({ type: 'error', message: (message || '请求数据异常！') })
                return
              }
              this.$message({ type: 'success', message: `修改任务[${jobName}]成功！` })
              this.$refs.backbutton.click()
            }).catch(error => {
              this.loading = false
              this.$message({ type: 'error', message: '请求异常！' })
              console.log(error)
            })
          }
        } else {
          return false
        }
      })
    },
    formatSQL() { // 格式化SQL
      var editor = this.$refs.cm.codemirror
      CodeMirror.commands['selectAll'](editor)
      var range = { from: editor.getCursor(true), to: editor.getCursor(false) }
      editor.autoFormatRange(range.from, range.to)
    },
    checkSQL() { // 预校验SQL
      this.loading = true
      const sql = this.$refs.cm.codemirror.getValue()
      checkfSql(sql).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.form.checkSQLResult = '校验SQL通过!'
        this.$message({ type: 'success', message: `校验SQL通过！` })
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    doSqlChange(cm) {
      this.form.checkSQLResult = ''
    },
    getTaskClass() {
      if (this.params.flag === 'history') {
        return 'fl-task-edit__history'
      } else {
        return this.isReadOnly ? 'fl-cm-row__isRead' : ''
      }
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
        default: return status + '　'
      }
    },
    getJobType(jobTypeEnum) {
      switch (jobTypeEnum) {
        case 'SQL_STREAMING': return 0
        case 'SQL_BATCH': return 2
        case 'JAR': return 1
        default: return jobTypeEnum
      }
    }
  }
}
</script>

<style scoped>
  .fl-sqltask-container {
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
  .fl-task-edit {
    box-sizing: border-box;
    background: #fff;
    min-height: calc(100% - 40px);
    /*max-width: 1160px;*/
  }
  .fl-task-edit >>> label {
    font-weight: 500;
  }
  .fl-task-edit >>> .el-form-item {
    margin-bottom: 15px!important;
  }
  .fl-alarm-row >>> .el-form-item {
    margin-top: -10px!important;
    margin-bottom: 0px!important;
  }
  .fl-cm-row__isRead >>> .el-form-item {
    margin-bottom: 0px!important;
  }
  .fl-button-row >>> .el-form-item {
    margin-bottom: 0px!important;
  }
 .fl-task-edit >>> .CodeMirror {
    height: calc(100vh - 345px);
    line-height : 150%;
    font-family: monospace,Helvetica Neue,Helvetica,Arial,sans-serif;
    font-size: 13px;
  }
 .fl-task-edit__isRead >>> .CodeMirror {
    height: calc(100vh - 300px);
  }
 .fl-task-edit__history >>> .CodeMirror {
    height: calc(100vh - 354px);
  }
  .fl-codemirror {
    border: 1px solid#C0C4CC;
  }
  .fl-jar-input >>> textarea {
    min-height: 83px !important;
    line-height : 110% !important;
  }
  .fl-form-item {
    width: 100%;
  }
</style>
