
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>帐号登陆</title>
    <#include "../control/public_css_js.ftl">
</head>

<body style="background-color: #e4e6e97d" >
<div class="modal-dialog" style="margin-top: 10%;">
    <div class="modal-content">
        <div class="modal-header">

            <h4 class="modal-title text-center" id="myModalLabel">登录</h4>
        </div>

        <div class="modal-body" id = "model-body">
            <div class="form-group">
                <input type="text" class="form-control"placeholder="用户名" autocomplete="off"  id="name" name="name">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" placeholder="密码" autocomplete="off"  name="password" id="password">
            </div>
        </div>
        <div class="modal-footer">
            <div class="form-group">
                <button type="button" class="btn btn-primary form-control" onclick="login()">登录</button>
            </div>
        </div>
            <div class="modal-footer">
                <div class="form-group " name="errorMessage" id="errorMessage" style="text-align:center"></div>
            </div>

    </div><!-- /.modal-content -->
</div><!-- /.modal -->

</body>
<script>

    function login() {
        $.post("../api/login", {
                name: $('#name').val(),
                password: $('#password').val()
            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data != null && data.success) {
                    skipUrl("/admin/listPage")
                } else {
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);
                }
            }
        );
    }
</script>
</html>
