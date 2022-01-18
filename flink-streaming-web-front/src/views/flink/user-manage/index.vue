<template>
  <div v-loading="loading" class="fl-container">
    <el-form :inline="true">
      <el-form-item>
        <el-button type="primary" class="el-icon-search" @click="handleQuery()">查询</el-button>
        <el-button type="primary" class="el-icon-plus" @click="openAddUserDialog()">新增</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" :header-cell-style="{background:'#f4f4f5','text-align':'center'}" class="wl-table" border>
      <el-table-column prop="id" :show-overflow-tooltip="true" label="编号" min-width="60" width="80" align="center" />
      <el-table-column prop="username" :show-overflow-tooltip="true" label="帐号" min-width="80" align="center" />
      <el-table-column prop="name" :show-overflow-tooltip="true" label="姓名" min-width="80" align="center" />
      <el-table-column prop="statusDesc" :show-overflow-tooltip="true" label="状态" min-width="100" align="center" />
      <el-table-column prop="createTimeStr" :show-overflow-tooltip="true" label="创建时间" min-width="100" align="center" />
      <el-table-column prop="editTimeStr" :show-overflow-tooltip="true" label="修改时间" min-width="100" align="center" />
      <el-table-column prop="operate" label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <el-link v-if="scope.row.status===0" type="primary" icon="el-icon-switch-button" @click.native="stopOrOpen(scope.row.username, 1)">启用</el-link>
          <el-link v-if="scope.row.status===1" type="danger" icon="el-icon-switch-button" @click.native="stopOrOpen(scope.row.username, 0)">停用</el-link>
          <el-link type="primary" icon="el-icon-edit" @click.native="openUserDialog(scope.row)">修改</el-link>
          <el-link type="primary" icon="el-icon-key" @click.native="openPwdDialog(scope.row)">修改密码</el-link>
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
    <!-- 新增对话框 -->
    <el-dialog title="新增用户" :visible.sync="addDialogVisible" :close-on-click-modal="false" :modal-append-to-body="false" width="40%">
      <el-form ref="addform" :model="addform" :rules="addrules" label-width="80px">
        <el-form-item label="用户帐号" prop="username">
          <el-input v-model="addform.username" placeholder="请输入用户帐号，必须是英文且长度不能少于4位" size="medium" />
        </el-form-item>
        <el-form-item label="用户姓名" prop="name">
          <el-input v-model="addform.name" placeholder="请输入用户姓名" size="medium" />
        </el-form-item>
        <el-form-item label="输入密码" prop="password">
          <el-input v-model="addform.password" type="password" placeholder="请输入密码，长度不能少于6位" size="medium" />
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
          <el-input v-model="addform.password2" type="password" placeholder="请输入确认密码，长度不能少于6位" size="medium" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible=false">取 消</el-button>
        <el-button type="primary" @click="doAddUser()">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 修改密码对话框 -->
    <el-dialog title="修改用户密码" :visible.sync="pwdDialogVisible" :close-on-click-modal="false" :modal-append-to-body="false" width="40%">
      <el-form ref="pwdform" :model="pwdform" :rules="pwdrules" label-width="100px">
        <el-form-item label="用户帐号" prop="username">
          <el-input v-model="pwdform.username" placeholder="请输入用户帐号，必须是英文且长度不能少于4位" size="medium" disabled />
        </el-form-item>
        <el-form-item label="输入旧密码" prop="oldpwd">
          <el-input v-model="pwdform.oldpwd" type="password" placeholder="请输入旧密码" size="medium" />
        </el-form-item>
        <el-form-item label="输入新密码" prop="password">
          <el-input v-model="pwdform.password" type="password" placeholder="请输入新密码，长度不能少于6位" size="medium" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="password2">
          <el-input v-model="pwdform.password2" type="password" placeholder="请输入确认新密码，长度不能少于6位" size="medium" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pwdDialogVisible=false">取 消</el-button>
        <el-button type="primary" @click="doPwd()">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 修改用户信息对话框 -->
    <el-dialog title="修改用户" :visible.sync="editDialogVisible" :close-on-click-modal="false" :modal-append-to-body="false" width="40%">
      <el-form ref="editform" :model="editform" :rules="editrules" label-width="100px">
        <el-form-item label="用户编号" prop="userid">
          <el-input v-model="editform.userid" size="medium" disabled />
        </el-form-item>
        <el-form-item label="用户帐号" prop="username">
          <el-input v-model="editform.username" size="medium" disabled />
        </el-form-item>
        <el-form-item label="用户姓名" prop="name">
          <el-input v-model="editform.name" placeholder="请输入用户姓名" size="medium" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible=false">取 消</el-button>
        <el-button type="primary" @click="doEditUser()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { userList, addUser, stopOrOpen, updatePassword, updateUserInfo } from '@/api/user'

export default {
  name: 'UserManage',
  data() {
    var checkPwd = (rule, value, callback) => { // 密码验证规则
      const regpwd = /^(\w){6,20}$/ // 6-20个字母、数字、下划线
      if (regpwd.test(value)) {
        return callback()
      }
      return callback(new Error('至少6个字符，至少1个大写字母，1个小写字母和1个数字'))
    }
    var addCheckPwd2 = (rule, value, callback) => { // 密码验证规则
      return (value === this.addform.password) ? callback() : callback(new Error('再次输入的密码不一致'))
    }
    var editCheckPwd2 = (rule, value, callback) => { // 密码验证规则
      return (value === this.pwdform.password) ? callback() : callback(new Error('再次输入的密码不一致'))
    }
    return {
      loading: false,
      addDialogVisible: false,
      editDialogVisible: false,
      pwdDialogVisible: false,
      addform: {
        username: '',
        name: '',
        password: '',
        password2: ''
      },
      pwdform: {
        username: '',
        name: '',
        oldpwd: '',
        password: '',
        password2: ''
      },
      editform: {
        userid: '',
        username: '',
        name: ''
      },
      addrules: {
        username: [{ required: true, message: '请输入用户帐号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入用户姓名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: checkPwd, trigger: 'blur' }
        ],
        password2: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: addCheckPwd2, trigger: 'blur' }
        ]
      },
      pwdrules: {
        oldpwd: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { validator: checkPwd, trigger: 'blur' }
        ],
        password2: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: editCheckPwd2, trigger: 'blur' }
        ]
      },
      editrules: {
        username: [{ required: true, message: '请输入用户帐号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入用户姓名', trigger: 'blur' }]
      },
      list: [],
      count: 0,
      pageSize: 15,
      currentPage: 1,
      pageshow: true
    }
  },
  mounted() {
    this.handleQuery()
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
    handleCurrentChange(pageno) { // 处理分页事件
      this.currentPage = pageno
      this.handleQuery()
    },
    queryUserList() {
      this.loading = true
      userList(this.currentPage, this.pageSize).then(response => {
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
          this.queryUserList()
        }
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    stopOrOpen(name, oper_code) {
      this.loading = true
      const o_code_str = (oper_code === 0 ? '停用' : '启用')
      stopOrOpen(name, oper_code).then(response => {
        this.loading = false
        const { code, success, message, data } = response
        if (code !== '200' || !success) {
          this.$message({ type: 'error', message: (message || '请求数据异常！') })
          return
        }
        this.$message({ type: 'success', message: `${o_code_str}[${name}]成功！` })
        this.handleQuery()
      }).catch(error => {
        this.loading = false
        this.$message({ type: 'error', message: '请求异常！' })
        console.log(error)
      })
    },
    openAddUserDialog() {
      this.addform.username = ''
      this.addform.name = ''
      this.addform.pwd1 = ''
      this.addform.pwd2 = ''
      this.addDialogVisible = true
    },
    doAddUser() {
      const user = {
        name: this.addform.username,
        fullname: this.addform.name,
        pwd1: this.addform.password,
        pwd2: this.addform.password2
      }
      this.$refs.addform.validate((valid) => {
        if (valid) {
          addUser(user).then(response => {
            this.loading = false
            const { code, success, message, data } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.addDialogVisible = false
            this.$message({ type: 'success', message: `新增[${user.username}]成功！` })
            this.handleQuery()
          }).catch(error => {
            this.loading = false
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        } else {
          return false
        }
      })
    },
    openPwdDialog(user) {
      this.pwdform.username = user.username
      this.pwdform.name = user.name
      this.pwdform.oldpwd = ''
      this.pwdform.pwd1 = ''
      this.pwdform.pwd2 = ''
      this.pwdDialogVisible = true
    },
    doPwd() {
      this.$refs.pwdform.validate((valid) => {
        if (valid) {
          const user = {
            name: this.pwdform.username,
            fullname: this.pwdform.name,
            oldPwd: this.pwdform.oldpwd,
            pwd1: this.pwdform.password,
            pwd2: this.pwdform.password2
          }
          updatePassword(user).then(response => {
            this.loading = false
            const { code, success, message, data } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.pwdDialogVisible = false
            this.$message({ type: 'success', message: `修改[${user.username}]密码成功！` })
            this.handleQuery()
          }).catch(error => {
            this.loading = false
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        } else {
          return false
        }
      })
    },
    openUserDialog(user) {
      this.editform.userid = user.id
      this.editform.username = user.username
      this.editform.name = user.name
      this.editDialogVisible = true
    },
    doEditUser() {
      this.$refs.editform.validate((valid) => {
        if (valid) {
          const user = {
            userid: this.editform.userid,
            fullname: this.editform.name
          }
          updateUserInfo(user).then(response => {
            this.loading = false
            const { code, success, message, data } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.editDialogVisible = false
            this.$message({ type: 'success', message: `修改[${user.username}]成功！` })
            this.handleQuery()
          }).catch(error => {
            this.loading = false
            this.$message({ type: 'error', message: '请求异常！' })
            console.log(error)
          })
        } else {
          return false
        }
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
</style>
