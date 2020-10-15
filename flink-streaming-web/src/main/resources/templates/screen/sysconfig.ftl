<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>系统配置</title>
    <#include "../control/public_css_js.ftl">
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../layout/menu.ftl" >


        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <form role="form" action="/admin/upsertSynConfig" method="post">
                <#if message??>
                <div class="form-group alert alert-danger"
                ">
                ${message}
        </div>
        </#if>

        <div class="form-group">
            <select class="form-control input-lg" name="key">
                <option value="">选择配置项 </option>
                <#list sysConfigVOList as val>
                    <option value="${val.getKey()}">${val.getDesc()}</option>
                </#list>
            </select>
        </div>
        <div class="form-group">
            <input class="form-control input-lg" type="text" placeholder="变量值" name="val">
        </div>
        <div class="form-group">
            <button class="btn btn-lg btn-primary " type="submit">提交</button>
            <span>备注：如果想修改直接保存就可以覆盖</span>
        </div>

        </form>

        <div class="col-md-12 column">
            <table class="table table-striped table-bordered">
                <thead>
                <tr>
                    <th>编号</th>
                    <th>名称</th>
                    <th>健值</th>
                    <th>描述</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>

                <#if systemConfigVOList?size == 0>
                    <tr>
                        <td colspan="7" align="center">
                            你还没有配置系统变量
                        </td>
                    </tr>
                <#else>

                    <#list systemConfigVOList as systemConfigVO>
                        <tr>
                            <td>${systemConfigVO.id}</td>
                            <td>${systemConfigVO.key}</td>
                            <td>${systemConfigVO.val}</td>
                            <td>${systemConfigVO.desc}</td>
                            <td> </td>
                        </tr>
                    </#list>

                </#if>
                </tbody>
            </table>
        </div>
    </div>

</div>


</body>

<#--<script>-->
    <#--function deleteConfig(id) {-->
        <#--if(confirm('确定要删除吗')==true){-->
            <#--$.post("../api/xx", {-->
                    <#--id: id-->
                <#--},-->
                <#--function (data, status) {-->
                    <#--if (data!=null && data.success){-->
                        <#--window.location.reload();-->
                    <#--}else{-->
                        <#--alert("执行失败："+data.message)-->
                    <#--}-->
                <#--}-->
            <#--);-->

        <#--}else{-->
            <#--return false;-->

        <#--}-->

    <#--}-->
<#--</script>-->
</html>
