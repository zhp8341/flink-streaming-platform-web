<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>告警</title>
    <#include "../../control/public_css_js.ftl">
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <form role="form" action="/admin/upsertAlartConfig" method="post">
                <#if message??>
                <div class="form-group alert alert-danger"
                ">
                ${message}
        </div>
        </#if>

        <div class="form-group">
            <select class="form-control " name="key">
                <#list sysConfigVOList as val>
                    <option value="${val.getKey()}">${val.getDesc()}</option>
                </#list>
            </select>
        </div>
        <div class="form-group">
            <input class="form-control " type="text" placeholder="变量值" name="val">
        </div>
        <div class="form-group">
            <button class="btn  btn-primary " type="submit">保存</button>
        </div>

        </form>

        <div class="col-md-12 column">
            <table class="table table-striped table-bordered">
                <thead>
                <tr>
                    <th colspan="4" >
                        <h5 style="text-align:center;color: red">备注：目前暂时只支持钉钉群告警且只能添加一个 （当运行的任务挂掉的时候会告警）</h5>
                        <h5 style="text-align:center;color: fuchsia">部署的机器需要支持外网否则无法支持钉钉发送</h5>
                    </th>
                </tr>
                <tr>
                    <th>名称</th>
                    <th>健值</th>
                    <th>描述</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>

                <#if systemConfigVOList?size == 0>
                    <tr>
                        <td colspan="6" align="center">
                            你还没有配置报警
                        </td>
                    </tr>
                <#else>

                    <#list systemConfigVOList as systemConfigVO>
                        <tr>
                            <td>${systemConfigVO.key}</td>
                            <td>${systemConfigVO.val}</td>
                            <td>${systemConfigVO.desc}</td>
                            <td><a href="#" id="dingding_test">测试一下</a></td>
                        </tr>
                    </#list>

                </#if>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
    $("#dingding_test").click(function () {
        $.post("../api/testDingdingAlert", {},
            function (data, status) {
                if (data!=null && data.success){
                    alert("测试通过成功 ");
                }else{
                    alert("执行失败："+data.message)
                }

            }
        );
    });
</script>
</body>
</html>
