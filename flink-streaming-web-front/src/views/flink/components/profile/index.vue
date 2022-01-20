<!-- 个人中心 -->
<template>
  <el-dialog v-loading.fullscreen.lock="loading" title="个人设置" :visible.sync="visible" :close-on-click-modal="false" :modal-append-to-body="false" width="600px">
    <el-tabs tab-position="left">
      <el-tab-pane label="基本设置">
        <el-form ref="form" :model="form" :rules="rules" label-position="top">
          <el-form-item label="Avatar" prop="avatar">
            <!-- <el-avatar :size="70" :src="avatar"></el-avatar> -->
            <img src="avatar.gif" class="user-avatar">
          </el-form-item>
          <el-form-item label="用户帐号" prop="name">
            <el-input v-model="form.username" placeholder="请输入内容" :disabled="true" size="medium" class="wl-input" />
          </el-form-item>
          <el-form-item label="用户名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入内容" size="medium" class="wl-input" />
          </el-form-item>
          <el-form-item prop="oprator">
            <el-button type="primary" @click="updateUserInfo()">更新基本信息</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="安全设置">
        <el-form ref="safeform" :model="safeform" :rules="rules" label-position="top">
          <el-form-item label="重置密码" prop="password">
            <el-input v-model="safeform.password" type="password" placeholder="请输入密码" size="medium" class="wl-input" />
          </el-form-item>
          <el-form-item label="再输入密码" prop="password2">
            <el-input v-model="safeform.password2" type="password" placeholder="请再次输入密码" size="medium" class="wl-input" />
          </el-form-item>
          <el-form-item prop="oprator">
            <el-button type="primary" @click="updateUserPassword()">重置密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script>
import { updateUserInfo, updateCurrentUserPassword } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    var checkPwd = (rule, value, callback) => { // 密码验证规则
      const regpwd = /^(\w){6,20}$/ // 6-20个字母、数字、下划线
      // const regpwd2 = /^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\d)[a-zA-Z\d]*$/  // 必须包含大写字母，小写字母，数字，特殊字符: /^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\d)(?=.*?[#@*&.])[a-zA-Z\d#@*&.]*$/
      // if (regpwd.test(value) && regpwd2.test(value)) {
      if (regpwd.test(value)) {
        return callback()
      }
      return callback(new Error('至少6个字符，至少1个大写字母，1个小写字母和1个数字'))
    }
    var checkPwd2 = (rule, value, callback) => { // 密码验证规则
      return (value === this.safeform.password) ? callback() : callback(new Error('再次输入的密码不一致'))
    }
    return {
      loading: false,
      visible: false,
      form: { // 基本设置表单
        userid: '',
        username: '',
        name: '',
        avatar: ''
      },
      safeform: {
        password: '',
        password2: ''
      },
      rules: {
        name: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: checkPwd, trigger: 'blur' }
        ],
        password2: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: checkPwd2, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    avatar: function() {
      return '/static/assets' + this.form.avatar
    }
  },
  mounted() {
    //
  },
  created() {
    // init the default selected tab
  },
  methods: {
    openDialog() {
      const token = this.$store.getters.token
      const user = (token instanceof String) ? JSON.parse(token) : token
      this.form.userid = user.id
      this.form.avatar = user.avatar
      this.form.username = user.username
      this.form.name = decodeURI(user.name)
      this.safeform.password = ''
      this.safeform.password2 = ''
      this.visible = true
    },
    handleAvatarSuccess(res, file) {
      if (res.code !== 0) {
        this.$message({ type: 'error', message: (res.message) })
        return
      }
      const data = res.data
      this.form.avatar = data.avatar
      this.$message({ type: 'success', message: '上传成功!' })
    },
    updateUserInfo() { // 更新用户基本信息
      this.$refs.form.validate((valid) => {
        if (valid) {
          const user = {
            userid: this.form.userid,
            fullname: this.form.name
          }
          updateUserInfo(user).then(response => {
            this.loading = false
            const { code, data, success, message } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.$store.dispatch('user/getInfo')
            this.$message({ type: 'success', message: '修改成功！' })
            this.visible = false
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
    updateUserPassword() { // 重置密码
      this.$refs.safeform.validate((valid) => {
        if (valid) {
          updateCurrentUserPassword(this.form.userid, this.safeform.password).then(response => {
            this.loading = false
            const { code, data, success, message } = response
            if (code !== '200' || !success) {
              this.$message({ type: 'error', message: (message || '请求数据异常！') })
              return
            }
            this.$store.dispatch('user/getInfo')
            this.$message({ type: 'success', message: '重置密码成功！' })
            this.visible = false
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
  .wl-input {
    width: 350px;
  }
  .user-avatar {
    /* cursor: pointer; */
    width: 70px;
    height: 70px;
    border-radius: 35px;
    margin-right: 10px;
  }
</style>
