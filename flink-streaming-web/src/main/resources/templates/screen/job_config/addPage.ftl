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
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="/static/codemirror/css/codemirror.css"/>
    <link rel="stylesheet" type="text/css" href="/static/codemirror/theme/mbo.css"/>
    <script type="text/javascript" src="/static/codemirror/js/codemirror.js"></script>
    <script type="text/javascript" src="/static/codemirror/js/css.js"></script>
    <script type="text/javascript" src="/static/codemirror/js/sql.js"></script>
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

                <div class="form-group">
                    <h4>*任务名称：</h4>
                    <input class="form-control input-lg" type="text" placeholder="任务名称" name="jobName" id="jobName" >
                </div>
                <div class="form-group">
                    <h4>*运行模式(暂时支持YARN_PER)：</h4>
                    <select class="form-control input-lg" id="deployMode">
                        <#--<option value="">运行模式</option>-->
                        <option value="YARN_PER">YARN_PER</option>
                        <#--<option value="STANDALONE">STANDALONE</option>-->
                    </select>
                </div>
                <div class="form-group">
                    <h4 >*flink运行配置：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="参数只支持 -p -yjm -yn -ytm -ys -yqu(必选)  如： -yqu flink   -yjm 1024m -ytm 2048m  -p 1  -ys 1 "/></h4>
                    <input class="form-control input-lg" type="text" placeholder="flink运行配置  如：  -yjm 1024m -ytm 2048m  -p 1  -ys 1 -yqu flink  " name="flinkRunConfig"  id="flinkRunConfig" >
                </div>
                <div class="form-group">
                    <h4>Checkpoint信息：
                        <span class="glyphicon glyphicon-question-sign"  data-toggle="tooltip" data-placement="bottom"
                                           title="不填默认不开启checkpoint机制 参数只支持 -checkpointInterval -checkpointingMode -checkpointTimeout -checkpointDir -tolerableCheckpointFailureNumber -asynchronousSnapshots 如  -asynchronousSnapshots true  -checkpointDir  hdfs//XXX/flink/checkpoint/"/>
                    </h4>
                    <input class="form-control input-lg" type="text" placeholder="Checkpoint信息 如   -checkpointDir  hdfs//XXX/flink/checkpoint/" name="flinkCheckpointConfig" id="flinkCheckpointConfig" >
                </div>

                <div class="form-group">
                    <h4 >udf地址：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="udf地址 &#10;如 http://xxx.xxx.com/flink-streaming-udf.jar"/></h4>
                    <input class="form-control input-lg" type="text" placeholder="udf地址 如：http://xxx.xxx.com/flink-streaming-udf.jar" name="udfJarPath"  id="udfJarPath" >
                </div>


                <div class="form-group">
                    <h4>*sql语句：</h4>
                    <textarea   placeholder="sql语句" name="flinkSql" id="flinkSql">  </textarea>
                </div>

                <div class="form-group">
                    <a class="btn btn-lg btn-primary " onclick="addConfig()" href="#"> 提交</a>
                </div>
                <div  id="errorMessage"/>

            </div>

        </div>
    </div>
</div>
<#include "../../layout/bottom.ftl">
<script>

    flinkSqlVal=editor.getValue();
    myTextarea = document.getElementById("flinkSql");
    var editor = CodeMirror.fromTextArea(myTextarea, {
        mode: "text/x-sql",
        lineNumbers: false,//显示行数
        matchBrackets: true,  // 括号匹配（这个需要导入codemirror的matchbrackets.js文件）
        indentUnit: 4,//缩进块用多少个空格表示 默认是2
        theme: "mbo"
    });

    editor.setSize('auto','500px');



    $(function () { $("[data-toggle='tooltip']").tooltip(); });
    function addConfig() {
        flinkSqlVal=editor.getValue();
        $.post("../api/addConfig", {
            jobName: $('#jobName').val(),
            deployMode: $('#deployMode').val(),
            flinkRunConfig:  $('#flinkRunConfig').val(),
            flinkCheckpointConfig: $('#flinkCheckpointConfig').val(),
            flinkSql:  flinkSqlVal,
            udfJarPath:  $('#udfJarPath').val()
            },
            function (data, status) {
                $("#message").removeClass();
                if (data!=null && data.success){
                    skipUrl("/admin/listPage")
                }else{
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);

                }

            }
        );
    }


</script>
</body>
</html>
