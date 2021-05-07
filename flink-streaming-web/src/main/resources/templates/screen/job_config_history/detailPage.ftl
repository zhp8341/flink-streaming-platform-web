<!DOCTYPE html>
<html lang="zh-CN" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>配置历史详情</title>
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



            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <#if message??>
                            <div class="form-group alert alert-danger">
                                ${message}
                            </div>
                        <#else >
                            <div class="form-group ">
                                <h4>备份时间：</h4>
                                <pre>${jobConfigHistory.createTime!""}</pre>
                            </div>

                            <div class="form-group ">
                                <h4>运行模式：</h4>
                                <pre>${jobConfigHistory.deployMode!""}</pre>
                            </div>

                            <div class="form-group">
                                <h4>flink运行配置：</h4>
                                <pre>${jobConfigHistory.flinkRunConfig!"无"}</pre>
                            </div>
                            <div class="form-group">
                                <h4>Checkpoint信息：</h4>
                                <pre>${jobConfigHistory.flinkCheckpointConfig!"无"}</pre>
                            </div>
                            <div class="form-group">
                                <h4>三方jar地址：</h4>
                                <pre>${jobConfigHistory.extJarPath!"无"}</pre>
                            </div>
                            <div class="form-group">
                                <h4>sql语句：</h4>
                                <pre>${jobConfigHistory.flinkSql!""}</pre>
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
