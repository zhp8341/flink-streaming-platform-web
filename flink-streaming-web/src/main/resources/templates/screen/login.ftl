<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>帐号登陆</title>
    <#include "../control/public_css_js.ftl">
    <link rel="stylesheet" href="/static/css/signin/signin.css">
</head>

<body>


<div class="container">

    <form class="form-signin" action="/admin/login" method="post">
        <h2 class="form-signin-heading text-center">请登录帐号</h2>
        <input type="text" id="name" name="name" class="form-control" placeholder="帐号" required autofocus>
        <input type="password" name="password" id="inputPassword" class="form-control" placeholder="密码" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">提交</button>

        <#if  message?? && message != "" >
        <p class="text-center text-primary"> ${message}</p>
        </#if>
    </form>

</div>

</body>
</html>
