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
                        <input type="hidden" name="jobType" id="jobType" value="0">
                        <div class="panel-body">
                            <input type="hidden"  name="id"  id="id"  value="${jobConfig.id}" >
                            <div class="form-group">
                                <label for="inputfile">相关配置说明详见：</label>
                                <a href="https://github.com/zhp8341/flink-streaming-platform-web/blob/master/docs/manual-sql.md"
                                   target="_blank">点击查看</a>
                            </div>
                            <div class="form-group">
                                <label for="inputfile">历史修改版本查看：</label>
                                <a href="/admin/jobConfigHistoryPage?jobConfigId=${jobConfig.id}" target="_blank">历史版本 </a>
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
                                <label for="inputfile" >三方jar地址 (自定义udf、连接器等jar地址 多个用换行
                                    (如 http://xxxx.com/udf.jar) 目前只支持http )：</label>
                             <textarea class="form-control"  name="extJarPath" id="extJarPath" rows="5" >${jobConfig.extJarPath!""}</textarea>
                            </div>


                            <div class="form-group">
                                <label for="inputfile">*sql语句：</label>
                                <textarea  name="flinkSql" id="flinkSql">${jobConfig.flinkSql!""}</textarea>
                            </div>


                            <div class="form-group">
                                <a class="btn btn-info btn-sm " onclick="editConfig()" href="#errorMessage">提交保存</a>
                                <a class="btn btn-success btn-sm" style="margin-left: 60px" onclick="autoFormatSelection()"> 格式化代码</a>
                                <a class="btn btn-warning btn-sm" style="margin-left: 60px" onclick="checkSql()"> sql预校验</a>
                            </div>
<#--                            <div class="form-group">-->
<#--                                <h5  style="color: #87B87F"> 代码格式化 备注： 需要选中对应的代码再点击"格式化代码" 按钮 才有效果-->
<#--                                    tips: win系统 CTRL+A 全选     mac系统 command+A  全选-->
<#--                                </h5>-->
<#--                            </div>-->
                            <div class="form-group">
                                <h5  style="color: #FFB752"> sql预校验 备注：只能校验单个sql语法正确与否,
                                    不能校验上下文之间关系，如：这张表是否存在
                                    数据类型是否正确等无法校验,总之不能完全保证运行的时候sql没有异常，只是能校验出一些语法错误
                                </h5>
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
    var flinkSqlVal;
    myTextarea = document.getElementById("flinkSql");
    var editor = CodeMirror.fromTextArea(myTextarea, {
        mode: "flink/x-fsql",
        lineNumbers: true,//显示行数
        matchBrackets: true,  // 括号匹配（这个需要导入codemirror的matchbrackets.js文件）
        indentUnit: 4,//缩进块用多少个空格表示 默认是2
        theme: "mbo",
        extraKeys: {'Ctrl': 'autocomplete'},//自定义快捷键
        hintOptions: {//自定义提示选项
            completeSingle: false, // 当匹配只有一项的时候是否自动补全
            tables: {
                'table.dynamic-table-options.enabled': [],
                'table.sql-dialect': [],
                'table.local-time-zone': [],
                'table.generated-code.max-length': [],
                'table.exec': ['state.ttl', 'source.idle-timeout',
                    'source.cdc-events-duplicate', 'window-agg.buffer-size-limit', 'source.cdc-events-duplicate',
                    'mini-batch.enabled', 'mini-batch.allow-latency', 'mini-batch.enabled', 'mini-batch.allow-latency',
                    'mini-batch.size', 'sink.not-null-enforcer', 'resource.default-parallelism', 'async-lookup.buffer-capacity',
                    'async-lookup.timeout'],
                'table.optimizer': ['distinct-agg.split.enabled',
                    'distinct-agg.split.bucket-num',
                    'agg-phase-strategy',
                    'reuse-sub-plan-enabled',
                    'reuse-source-enabled',
                    'source.predicate-pushdown-enabled',
                    'join-reorder-enabled'],
            }

        }
    });
    editor.setSize('auto','750px');

    //代码自动提示功能，记住使用cursorActivity事件不要使用change事件，这是一个坑，那样页面直接会卡死
    editor.on('keypress', function () {
        editor.showHint()
    });


    function getSelectedRange() {
        return { from: editor.getCursor(true), to: editor.getCursor(false) };
    }

    function autoFormatSelection() {
        CodeMirror.commands["selectAll"](editor);
        var range = getSelectedRange();
        editor.autoFormatRange(range.from, range.to);
    }

    function commentSelection(isComment) {
        var range = getSelectedRange();
        editor.commentRange(isComment, range.from, range.to);
    }



    $(function () { $("[data-toggle='tooltip']").tooltip(); });


    function checkSql(){
        flinkSqlVal=editor.getValue();
        $.post("../api/checkfSql", {
                flinkSql:   flinkSqlVal
            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data!=null && data.success){
                    $("#errorMessage").addClass("form-group alert alert-success")
                    $("#errorMessage").html("校验Sql通过");
                }else{
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);

                }

            }
        );
    }

    function editConfig() {
        flinkSqlVal=editor.getValue();

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
                flinkSql:   flinkSqlVal,
                jobType: $('#jobType').val(),
                alarmTypes:   chk_value.toString(),
                extJarPath:  $('#extJarPath').val()
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

    $(window).bind('beforeunload',function(){
        return '确定要离开当前页面吗';
    });
</script>
</body>
</html>
