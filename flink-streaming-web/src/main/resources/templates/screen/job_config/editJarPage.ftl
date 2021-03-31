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
    <link rel="stylesheet" type="text/css" href="/static/codemirror/css/codemirror.css?version=20210123"/>
    <link rel="stylesheet" type="text/css" href="/static/codemirror/theme/mbo.css?version=20210123"/>
    <link rel="stylesheet" type="text/css" href="/static/codemirror/hint/show-hint.css?version=20210123"/>
    <script type="text/javascript" src="/static/codemirror/js/codemirror.js?version=20210123"></script>
    <script type="text/javascript" src="/static/codemirror/js/css.js?version=20210123"></script>
    <script type="text/javascript" src="/static/codemirror/js/sql.js?version=20210123"></script>
    <script type="text/javascript" src="/static/codemirror/hint/show-hint.js?version=20210123"></script>
    <script type="text/javascript" src="/static/codemirror/hint/sql-hint.js?version=20210123"></script>
    <script type="text/javascript" src="/static/codemirror/hint/formatting.js?version=20210123"></script>

    <style>
        label{font-weight: 800}
    </style>
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
                        <input type="hidden" name="jobType" id="jobType" value="1">
                        <div class="panel-body">
                            <input type="hidden"  name="id"  id="id"  value="${jobConfig.id}" >
                            <div class="form-group">
                                <label for="inputfile">相关配置说明详见：</label>
                                <a href="https://github.com/zhp8341/flink-streaming-platform-web#%E4%B8%89%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D"
                                   target="_blank">点击查看</a>
                            </div>
                            <div class="form-group">
                                <label for="inputfile">告警辅助配置：</label>

                                <label class="checkbox-inline">
                                    <input type="checkbox" name="alarmType" value="1"  <#if jobConfig.types??&&
                                    jobConfig.types?seq_contains(1) > checked </#if>  />
                                    钉钉告警
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="alarmType" value="2"  <#if jobConfig.types??&&
                                    jobConfig.types?seq_contains(2) > checked </#if> />
                                    http回调告警
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="alarmType" value="3"
                                            <#if jobConfig.types??&&
                                            jobConfig.types?seq_contains(3) > checked </#if> />
                                    任务退出自动拉起
                                </label>
                            </div>
                            <div class="form-group ">
                                <label for="inputfile">任务状态：</label>
                                <pre>${jobConfig.stautsStr!""}</pre>
                            </div>
                            <div class="form-group">
                                <label for="inputfile">配置是否开启：</label>
                                <pre>${jobConfig.openStr!""}</pre>
                            </div>
                            <div class="form-group">
                                <label for="inputfile">*任务名称：</label>
                                <input class="form-control " type="text" placeholder="任务名称" name="jobName"  value="${jobConfig.jobName!""}"   id="jobName" >
                            </div>

                            <div class="form-group">
                                <label for="inputfile">*运行模式：</label>
                                <select class="form-control " id="deployMode">
                                    <option value="YARN_PER"  <#if jobConfig.deployMode??&& jobConfig.deployMode=="YARN_PER" > selected </#if> >YARN_PER</option>
                                    <option value="LOCAL"     <#if jobConfig.deployMode??&& jobConfig.deployMode=="LOCAL" > selected </#if> >Local Cluster</option>
                                    <option value="STANDALONE"     <#if jobConfig.deployMode??&& jobConfig.deployMode=="STANDALONE" > selected </#if> >Standalone Cluste</option>
                                </select>
                            </div>

                            <div class="form-group" id="configDiv">
                                <label for="inputfile" >*flink运行配置(如yarn模式 -yjm 1024m -ytm 1024m -p 1 -yqu streaming)
                                    ：</label>
                                <input class="form-control " type="text"  name="flinkRunConfig" value="${jobConfig.flinkRunConfig!""}"   id="flinkRunConfig" >
                            </div>


                            <div class="form-group">
                                <label for="inputfile"  data-toggle="tooltip"   data-placement="bottom" title="不填默认不开启checkpoint机制 参数只支持 -checkpointInterval -checkpointingMode -checkpointTimeout -checkpointDir -tolerableCheckpointFailureNumber -asynchronousSnapshots 如  -asynchronousSnapshots true  -checkpointDir  hdfs//XXX/flink/checkpoint/ ">Checkpoint信息：</label>
                                <input class="form-control " type="text" placeholder="Checkpoint信息" name="flinkCheckpointConfig"  value="${jobConfig.flinkCheckpointConfig!""}"  id="flinkCheckpointConfig" >
                            </div>

                            <div class="form-group">
                                <label for="inputfile">主类名*：</label>
                                <input class="form-control " type="text"
                                       name="customMainClass" id="customMainClass"  value="${jobConfig.customMainClass!""}" >
                            </div>

                            <div class="form-group">
                                <label for="inputfile">主类jar的http地址*：</label>
                                <input class="form-control " type="text"
                                       placeholder="主类jar的http地址 http://ccblog.cn/xx.jar"
                                       name="customJarUrl" id="customJarUrl"  value="${jobConfig.customJarUrl!""}" >
                            </div>


                            <div class="form-group">
                                <label for="inputfile">自定义参数主类参数：</label>
                                <input class="form-control " type="text" placeholder="启动jar可能需要使用的自定义参数"
                                       name="customArgs" id="customArgs" value="${jobConfig.customArgs!""}">
                            </div>

                            <div class="form-group">
                                <a class="btn btn-info btn-sm " onclick="editConfig()" href="#errorMessage">提交保存</a>
                            </div>


                            <div class="form-group">
                                <label   name="errorMessage" id="errorMessage"></label>
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


    function editConfig() {

        var chk_value =[];//定义一个数组
        $('input[name="alarmType"]:checked').each(function(){
            chk_value.push($(this).val());
        });

        $.post("../api/editConfig", {
                id: $('#id').val(),
                jobName: $('#jobName').val(),
                deployMode: $('#deployMode').val(),
                flinkRunConfig:  $('#flinkRunConfig').val(),
                flinkCheckpointConfig: $('#flinkCheckpointConfig').val(),
                jobType: $('#jobType').val(),
                customArgs: $('#customArgs').val(),
                customMainClass: $('#customMainClass').val(),
                customJarUrl: $('#customJarUrl').val()

            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data!=null && data.success){
                    $("#errorMessage").addClass("form-group alert alert-success")
                    $("#errorMessage").html("修改成功");
                }else{
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);

                }

            }
        );
    }


    $(document).ready(function(){
        initDeployMode()
        $('#deployMode').change(function() {
            initDeployMode()
        })
    });
    function  initDeployMode(){
        if ("LOCAL" == $('#deployMode').val()){
            $("#configDiv").hide();
        } else {
            $("#configDiv").show();
        }
    }
</script>
</body>
</html>
