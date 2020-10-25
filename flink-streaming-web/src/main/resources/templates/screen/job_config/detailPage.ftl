<!DOCTYPE html>
<html lang="zh-CN" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>配置详情</title>
    <#include "../../control/public_css_js.ftl">
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

                <#if message??>
                    <div class="form-group alert alert-danger"">
                        ${message}
                    </div>
                <#else >
                    <div class="form-group ">
                        <h4>配置是否开启：</h4>
                       <pre>${jobConfig.openStr!""}</pre>
                    </div>
                    <div class="form-group ">
                        <h4>运行模式：</h4>
                       <pre>${jobConfig.deployMode!""}</pre>
                    </div>
                    <div class="form-group">
                        <h4>任务状态：</h4>
                        <pre>${jobConfig.stautsStr!""}</pre>
                    </div>
                    <div class="form-group">
                        <h4>flink运行配置：</h4>
                        <pre>${jobConfig.flinkRunConfig!""}</pre>
                    </div>
                    <div class="form-group">
                        <h4>Checkpoint信息：</h4>
                        <pre>${jobConfig.flinkCheckpointConfig!"无"}</pre>
                    </div>
                    <div class="form-group">
                        <h4>udf地址：</h4>
                        <pre>${jobConfig.udfJarPath!"无"}</pre>
                    </div>
                    <div class="form-group">
                        <h4>sql语句：</h4>
                        <pre>${jobConfig.flinkSql!""}</pre>

                    </div>

                    <div class="form-group">
                        <a  class="btn btn-lg btn-primary" href="/admin/editPage?id=${jobConfig.id!""}" >修改</a>
                    </div>
                </#if>

        </div>
    </div>
</div>

<!--sd -->
<#include "../../layout/bottom.ftl">
</body>
</html>
