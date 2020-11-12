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
                    <li class="active">编辑用户</li>
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
                                    <input type="hidden" id="name" value="${username}" />
                                    <input type="text"  value="${username}" class="col-xs-10 col-sm-5" disabled/>
                                    <span class="help-inline col-xs-12 col-sm-7">
                                      <span class="middle"> 必须是英文且长度不能少于4位</span>
								   </span>
                                </div>
                            </div>

                            <div class="space-4"></div>

                            <div class="form-group">
                                <label class="col-sm-3 control-label no-padding-right" for="form-field-2">
                                    *输入旧密码</label>
                                <div class="col-sm-9">
                                    <input type="password" id="oldPwd" class="col-xs-10 col-sm-5"/>
                                </div>
                            </div>

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
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->
    <#include "../../layout/bottom.ftl">
</div><!-- /.main-container -->


</body>
<script>
    function addUser() {
        $.post("../api/updatePassword", {
                name: $('#name').val(),
                oldPwd: $('#oldPwd').val(),
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



</script>
</html>
