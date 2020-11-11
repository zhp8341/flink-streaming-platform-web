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
                        <a href="#">配置管理</a>
                    </li>
                    <li class="active">编辑配置</li>
                </ul>
            </div>

            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <#if message??>
                            <div class="form-group alert alert-danger">
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
                                <a  class="btn btn-info btn-sm " href="/admin/editPage?id=${jobConfig.id!""}" >编辑</a>
                            </div>
                        </#if>

                        <!-- PAGE CONTENT ENDS -->
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->

    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->
</body>
</html>
