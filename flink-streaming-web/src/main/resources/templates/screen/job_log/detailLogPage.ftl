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

</head>

<body class="no-skin">
<!-- start top-->
<div id="navbar" class="navbar navbar-default          ace-save-state">
    <#include "../../layout/top.ftl">
</div>
<!-- end top-->
<div class="main-container ace-save-state" id="main-container">
    <script type="text/javascript">
        try{ace.settings.loadState('main-container')}catch(e){}
    </script>

    <#include "../../layout/menu.ftl">


    <div class="main-content">
        <div class="main-content-inner">

            <div class="breadcrumbs ace-save-state" id="breadcrumbs">
                <ul class="breadcrumb">
                    <li>
                        <a href="#">日志管理</a>
                    </li>
                    <li class="active">日志详情</li>
                </ul>
            </div>


            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">


                        <#if jobRunLogDetail??>
                            <div class="form-group ">
                                <label for="inputfile">启动状态：</label>
                                <pre>${jobRunLogDetail.jobStatus!""}</pre>
                            </div>
                            <div class="form-group ">
                                <label for="inputfile">运行模式：</label>
                                <pre>${jobRunLogDetail.deployMode!""}</pre>
                            </div>

                            <div class="form-group ">
                                <label for="inputfile">日志内容：</label>
                                <pre>${jobRunLogDetail.localLog!""}</pre>
                            </div>
                        <#else>
                            <div  id="message"> 没有找到该记录日志</div>
                        </#if>

                        <#if jobRunLogDetail.clinetJobUrl??>
                            <div class="form-group ">
                            <label for="inputfile">
                                Flink客户端日志（只能看到最新运行的cli日志，建议不要同时提交多个任务,如果当时主机发生变化将无法查询）：
                            </label>
                            <pre><a href="${jobRunLogDetail.clinetJobUrl!""}" target="_blank">${jobRunLogDetail.clinetJobUrl!""}</a>   (连接跳转后 如果要看最新日志 需要手动刷新页面)</pre>
                            </div>
                        </#if>

                        <#if jobRunLogDetail.remoteLogUrl??>
                            <div class="form-group ">
                                <label for="inputfile">查看Flink集群日志：</label>
                                <pre><a href="${jobRunLogDetail.remoteLogUrl!""}" target="_blank">${jobRunLogDetail.remoteLogUrl!""}</a></pre>
                            </div>
                        </#if>

                        <div class="form-group ">
                            <button class="btn btn-info btn-sm "  onclick="window.location.reload()" >刷新日志</button>
                        </div>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->

    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->
</body>

</html>
