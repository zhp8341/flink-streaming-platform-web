<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>新增配置</title>
    <#include "../../control/public_css_js.ftl">

    <link rel="stylesheet" type="text/css" href="/static/codemirror/css/codemirror.css"/>
    <link rel="stylesheet" type="text/css" href="/static/codemirror/theme/mbo.css"/>
    <script type="text/javascript" src="/static/codemirror/js/codemirror.js"></script>
    <script type="text/javascript" src="/static/codemirror/js/css.js"></script>
    <script type="text/javascript" src="/static/codemirror/js/sql.js"></script>
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
                    <li class="active">新增任务</li>
                </ul>
            </div>


            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <div class="panel-body">
                            <div class="form-group">
                                <label for="inputfile">*任务名称：</label>
                                <input class="form-control " type="text" placeholder="任务名称" name="jobName" id="jobName">
                            </div>
                            <div class="form-group">
                                <label for="inputfile">*运行模式：</label>
                                <select class="form-control " id="deployMode">
                                    <option value="">请选择</option>
                                    <option value="YARN_PER">YARN_PER</option>
                                    <option value="LOCAL">Local Cluster</option>
                                    <option value="STANDALONE">Standalone Cluster</option>
                                </select>
                            </div>
                            <div class="form-group" id="configDiv">
                                <label for="inputfile">*flink运行配置：</label>
                                <input class="form-control " type="text" name="flinkRunConfig" id="flinkRunConfig">
                            </div>
                            <div class="form-group">
                                <label for="inputfile" data-toggle="tooltip" data-placement="bottom"
                                       title="不填默认不开启checkpoint机制 参数只支持 -checkpointInterval -checkpointingMode -checkpointTimeout -checkpointDir -tolerableCheckpointFailureNumber -asynchronousSnapshots 如  -asynchronousSnapshots true  -checkpointDir  hdfs//XXX/flink/checkpoint/ ">Checkpoint信息：</label>
                                <input class="form-control " type="text"
                                       placeholder="Checkpoint信息 如   -checkpointDir  hdfs//XXX/flink/checkpoint/"
                                       name="flinkCheckpointConfig" id="flinkCheckpointConfig">
                            </div>

                            <div class="form-group">
                                <label for="inputfile" data-toggle="tooltip" data-placement="bottom"
                                       title="udf地址 &#10;如 http://xxx.xxx.com/flink-streaming-udf.jar">udf地址：</label>
                                <input class="form-control " type="text"
                                       placeholder="udf地址 如：http://xxx.xxx.com/flink-streaming-udf.jar" name="udfJarPath"
                                       id="udfJarPath">
                            </div>

                            <div class="form-group">
                                <label for="inputfile">*sql语句：</label>
                                <textarea name="flinkSql" id="flinkSql"> </textarea>
                            </div>

                            <div class="form-group">
                                <label name="errorMessage" id="errorMessage"></label>
                            </div>

                            <div class="form-group">
                                <a class="btn btn-info btn-sm" onclick="addConfig()" href="#errorMessage">保存</a>
                            </div>
                        </div>

                        <!-- PAGE CONTENT ENDS -->
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->


    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->

<script>
    var flinkSqlVal;
    myTextarea = document.getElementById("flinkSql");
    var editor = CodeMirror.fromTextArea(myTextarea, {
        mode: "text/x-sql",
        lineNumbers: false,//显示行数
        matchBrackets: true,  // 括号匹配（这个需要导入codemirror的matchbrackets.js文件）
        indentUnit: 4,//缩进块用多少个空格表示 默认是2
        theme: "mbo"
    });

    editor.setSize('auto', '800px');

    $(function () {
        $("[data-toggle='tooltip']").tooltip();
    });

    function addConfig() {
        flinkSqlVal = editor.getValue();
        $.post("../api/addConfig", {
                jobName: $('#jobName').val(),
                deployMode: $('#deployMode').val(),
                flinkRunConfig: $('#flinkRunConfig').val(),
                flinkCheckpointConfig: $('#flinkCheckpointConfig').val(),
                flinkSql: flinkSqlVal,
                udfJarPath: $('#udfJarPath').val()
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

    $('#deployMode').change(function () {
        if ("LOCAL" == $(this).val()) {
            $("#configDiv").hide();
        } else {
            $("#configDiv").show();
        }
    })

    $(window).bind('beforeunload',function(){
        return '确定要离开当前页面吗';
    });
</script>
</body>
</html>
