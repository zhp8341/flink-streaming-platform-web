<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>编辑配置</title>
    <#include "../../control/public_css_js.ftl">
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">



                        <input type="hidden"  name="id"  id="id"  value="${jobConfig.id}" >
                        <div class="form-group ">
                            <h4>任务状态：</h4>
                            <pre>${jobConfig.stautsStr!""}</pre>
                        </div>
                        <div class="form-group">
                            <h4>配置是否开启：</h4>
                            <pre>${jobConfig.openStr!""}</pre>
                        </div>
                        <div class="form-group">
                            <h4>*任务名称：</h4>
                            <input class="form-control input-lg" type="text" placeholder="任务名称" name="jobName"  value="${jobConfig.jobName!""}"   id="jobName" >
                        </div>

                        <div class="form-group">
                            <h4>*运行模式：</h4>
                            <select class="form-control input-lg" id="deployMode">
                                <option value="${jobConfig.deployMode!""}">${jobConfig.deployMode!""}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <h4 >*flink运行配置：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="参数只支持 -p -yjm -yn -ytm -ys -yqu(必选)  如： -yqu flink   -yjm 1024m -ytm 2048m  -p 1  -ys 1 "/></h4>
                            <input class="form-control input-lg" type="text" placeholder="flink运行配置" name="flinkRunConfig" value="${jobConfig.flinkRunConfig!""}"   id="flinkRunConfig" >
                        </div>


                        <div class="form-group">
                            <h4>Checkpoint信息：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="不填默认不开启checkpoint机制 参数只支持 -checkpointInterval -checkpointingMode -checkpointTimeout -checkpointDir -tolerableCheckpointFailureNumber -asynchronousSnapshots 如  -asynchronousSnapshots true  -checkpointDir  hdfs//XXX/flink/checkpoint/"/></h4>
                            <input class="form-control input-lg" type="text" placeholder="Checkpoint信息" name="flinkCheckpointConfig"  value="${jobConfig.flinkCheckpointConfig!""}"  id="flinkCheckpointConfig" >
                        </div>

                        <div class="form-group">
                            <h4 >udf地址：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="udf地址 &#10;如 http://xxx.xxx.com/flink-streaming-udf.jar"/></h4>
                            <input class="form-control input-lg" type="text" placeholder="udf地址 如：http://xxx.xxx.com/flink-streaming-udf.jar" value="${jobConfig.udfJarPath!""}"  name="udfJarPath"  id="udfJarPath" >
                        </div>

                        <div class="form-group">
                            <h4 >udf注册名称：<span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom"title="如 utc2local|com.streaming.flink.udf.UTC2Local&#10; 多个可用;分隔 &#10;  utc2local代表组册的名称 &#10; com.streaming.flink.udf.UTC2Local代表类名"/></h4>
                            <textarea  rows="8" class="form-control input-lg" type="text" placeholder="udf地址 如 utc2local|com.streaming.flink.udf.UTC2Local  utc2local代表注册的名称  com.streaming.flink.udf.UTC2Local代表类名" name="udfRegisterName"  id="udfRegisterName" >${jobConfig.udfRegisterName!""}</textarea>
                        </div>

                        <div class="form-group">
                            <h4>*sql语句：</h4>
                            <textarea class="form-control" rows="20" placeholder="sql语句" name="flinkSql" id="flinkSql">  ${jobConfig.flinkSql!""}  </textarea>
                        </div>
                         <div class="form-group">
                                 <div  id="message"/>
                         </div>

                          <div class="form-group">
                               <button class="btn btn-lg btn-primary " onclick="editConfig()" type="submit">提交</button>
                          </div>

        </div>
    </div>
</div>
<#include "../../layout/bottom.ftl">
<script>

    $(function () { $("[data-toggle='tooltip']").tooltip(); });

    function editConfig() {
        $.post("../api/editConfig", {
                id: $('#id').val(),
                jobName: $('#jobName').val(),
                deployMode: $('#deployMode').val(),
                flinkRunConfig:  $('#flinkRunConfig').val(),
                flinkCheckpointConfig: $('#flinkCheckpointConfig').val(),
                flinkSql:  $('#flinkSql').val(),
                udfJarPath:  $('#udfJarPath').val(),
                udfRegisterName:  $('#udfRegisterName').val()
            },
            function (data, status) {
                $("#message").removeClass();
                if (data!=null && data.success){
                    $("#message").addClass("form-group alert alert-success")
                    $("#message").html("修改成功");
                }else{
                    $("#message").addClass("form-group alert alert-danger")
                    $("#message").html(data.message);

                }

            }
        );
    }

</script>
</body>
</html>
