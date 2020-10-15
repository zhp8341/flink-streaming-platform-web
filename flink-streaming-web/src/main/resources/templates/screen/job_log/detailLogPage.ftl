<!DOCTYPE html>
<html lang="zh-CN" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>日志详情</title>
    <#include "../../control/public_css_js.ftl">
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">


            <#if jobRunLogDetail??>
                <div class="form-group ">
                    <h4>启动状态：</h4>
                    <pre>${jobRunLogDetail.jobStatus!""}</pre>
                </div>
                <div class="form-group ">
                    <h4>运行模式：</h4>
                    <pre>${jobRunLogDetail.deployMode!""}</pre>
                </div>

                <#if jobRunLogDetail.jobId??>
                    <div class="form-group ">
                        <h4>远程日志：</h4>
                        <pre> <a href="${jobRunLogDetail.remoteLogUrl!""}" target="_blank">${jobRunLogDetail.remoteLogUrl!""}</a>  </pre>
                    </div>
                </#if>


                <div class="form-group ">
                    <h4>日志详情：</h4>
                    <pre>${jobRunLogDetail.localLog!""}</pre>
                </div>
            <#else>
                <div  id="message"> 没有找到改记录日志</div>
            </#if>



        </div>
    </div>
</div>

<!--sd -->

</body>
</html>
