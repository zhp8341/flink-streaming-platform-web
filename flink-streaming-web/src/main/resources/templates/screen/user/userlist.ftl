<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>用户管理</title>
    <#include "../../control/public_css_js.ftl">

</head>

<body class="no-skin">
<!-- start top-->
<div id="navbar" class="navbar navbar-default          ace-save-state">
    <#include "../../layout/top.ftl">
</div>
<!-- end top-->
<div class="main-container ace-save-state" id="main-container">
    <script type="text/javascript">
        try {
            ace.settings.loadState('main-container')
        } catch (e) {
        }
    </script>

    <#include "../../layout/menu.ftl">


    <div class="main-content">
        <div class="main-content-inner">

            <div class="breadcrumbs ace-save-state" id="breadcrumbs">
                <ul class="breadcrumb">
                    <li>
                        <a href="#">用户管理</a>
                    </li>
                    <li class="active">用户列表</li>
                </ul>
            </div>

            <div class="page-content">


                <div class="row">
                    <div class="col-xs-12">
                        <div class="form-group " name="errorMessage" id="errorMessage"></div>
                        <form class="form-horizontal" role="form">
                            <div class="form-group">
                                <label class="col-sm-3 control-label no-padding-right" for="form-field-1">
                                    *账号 </label>
                                <div class="col-sm-9">
                                    <input type="text" id="name" class="col-xs-10 col-sm-5"/>
                                    <span class="help-inline col-xs-12 col-sm-7">
                                      <span class="middle"> 必须是英文且长度不能少于4位</span>
								   </span>
                                </div>
                            </div>

                            <div class="space-4"></div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label no-padding-right" for="form-field-2">
                                    *输入密码</label>
                                <div class="col-sm-9">
                                    <input type="password" id="pwd1" class="col-xs-10 col-sm-5"/>
                                    <span class="help-inline col-xs-12 col-sm-7">
                                      <span class="middle"> 长度不能少于6位</span>
								   </span>
                                </div>

                            </div>
                            <div class="space-4"></div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label no-padding-right" for="form-field-2">
                                    *确认密码</label>
                                <div class="col-sm-9">
                                    <input type="password" id="pwd2" class="col-xs-10 col-sm-5"/>
                                    <span class="help-inline col-xs-12 col-sm-7">
                                      <span class="middle"> 长度不能少于6位</span>
								   </span>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-offset-3 col-md-9">
                                    <button type="button" class="btn  btn-purple btn-sm " onclick="addUser()">
                                        保存
                                    </button>
                                    <button type="reset" class="btn btn-sm ">重置</button>
                                </div>
                            </div>
                        </form>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <div class="row">
                    <div class="col-xs-12">
                        <table class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th>账号</th>
                                <th>状态</th>
                                <th>创建时间</th>
                                <th>修改时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#if userList?size == 0>
                                <tr>
                                    <td colspan="5" align="center">
                                        <h4>没有数据</h4>
                                    </td>
                                </tr>
                            <#else>
                                <#list userList as userVO>
                                    <tr>
                                        <td>${userVO.username!""}</td>
                                        <td>${userVO.stautsDesc!""}</td>
                                        <td>${userVO.createTimeStr!""}</td>
                                        <td>${userVO.editTimeStr!""}</td>
                                        <td>
                                            <#if userVO.stauts==1>
                                                <a  href="#" onclick="closeUser('${userVO.username}')">停用</a>
                                            <#else>
                                                <a  href="#"  onclick="openUser('${userVO.username}')">启用</a>
                                            </#if>
                                            <a href="/admin/editUser?name=${userVO.username}" >修改密码</a>
                                        </td>
                                    </tr>
                                </#list>
                            </#if>
                            </tbody>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->
    <#include "../../layout/bottom.ftl">
</div><!-- /.main-container -->


</body>
<script>
    function addUser() {
        $.post("../api/addUser", {
                name: $('#name').val(),
                pwd1: $('#pwd1').val(),
                pwd2: $('#pwd2').val()
            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data != null && data.success) {
                    window.location.reload();
                } else {
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);
                }
            }
        );
    }

    function openUser(name) {
        $.post("../api/stopOrOpen", {
                name: name,
                code:1
            },
            function (data, status) {
                if (data!=null && data.success){
                    $.gritter.add({
                        title: 'Success!',
                        text: '成功，请稍后刷新',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
                            window.location.reload();
                        }
                    });
                }else{
                    $.gritter.add({
                        title: 'Fail!',
                        text: '执行失败：' + data.message,
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 3000,
                        // class_name: (!$('#gritter-light').get(0).checked ? 'gritter-light' : ''),
                        after_close: function(e) {
                        }
                    });
                }

            }
        );
    }

    function closeUser(name) {
        $.post("../api/stopOrOpen", {
                name: name,
                code:0
            },
            function (data, status) {
                if (data!=null && data.success){
                    $.gritter.add({
                        title: 'Success!',
                        text: '成功，请稍后刷新',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
                            window.location.reload();
                        }
                    });
                }else{
                    $.gritter.add({
                        title: 'Fail!',
                        text: '执行失败：' + data.message,
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 3000,
                        // class_name: (!$('#gritter-light').get(0).checked ? 'gritter-light' : ''),
                        after_close: function(e) {
                        }
                    });
                }
            }
        );
    }

</script>
</html>
