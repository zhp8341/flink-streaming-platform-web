<template>
  <div class="dashboard-editor-container">
    <!--    <div class=" clearfix">-->
    <!--      <pan-thumb :image="avatar" style="float: left">-->
    <!--        角色：<span v-for="item in roles" :key="item" class="pan-info-roles">{{ item }}</span>-->
    <!--        <el-link type="primary" href="/admin/listPage" target="_blank">跳转老UI界面</el-link>-->
    <!--      </pan-thumb>-->
    <!--      &lt;!&ndash; <github-corner style="position: absolute; top: 0px; border: 0; right: 0;" /> &ndash;&gt;-->
    <!--      <div class="info-container">-->
    <!--        <span class="display_name">{{ name }}</span>-->
    <!--        &lt;!&ndash; <span style="font-size:20px;padding-top:20px;display:inline-block;">Editor's Dashboard</span> &ndash;&gt;-->
    <!--      </div>-->
    <!--    </div>-->
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PanThumb from '@/components/PanThumb'
import GithubCorner from '@/components/GithubCorner'

export default {
  name: 'DashboardEditor',
  components: { PanThumb, GithubCorner },
  data() {
    return {
      // emptyGif: 'https://wpimg.wallstcn.com/0e03b7da-db9e-4819-ba10-9016ddfdaed3'
      python: '',
      server: '',
      version: ''
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles'
    ])
  },
  mounted: function() {
    //
  }
}
</script>

<style lang="scss" scoped>
  .emptyGif {
    display: block;
    width: 45%;
    margin: 0 auto;
  }

  .dashboard-editor-container {
    // background-color: #e3e3e3;
    background: url('~@/assets/welcom.jpg') no-repeat fixed center;
    background-size: cover;
    background-color: #e3e3e3;
    min-height: 100vh;
    padding: 50px 60px 0px;
    .pan-info-roles {
      font-size: 14px;
      font-weight: 700;
      color: #333;
      display: block;
      padding-bottom: 10px;
    }
    .info-container {
      position: relative;
      margin-left: 190px;
      height: 150px;
      line-height: 200px;
      .display_name {
        font-size: 48px;
        line-height: 48px;
        color: #212121;
        position: absolute;
        top: 50px;
      }
    }
  }
</style>
