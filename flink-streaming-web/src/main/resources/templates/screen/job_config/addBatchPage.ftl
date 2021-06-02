<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>新增批任务配置</title>
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
                    <li class="active">新增批任务</li>
                </ul>
            </div>


            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">
                        <input type="hidden" name="jobType" id="jobType" value="2">
                        <div class="panel-body">
                            <div class="form-group">
                                <label for="inputfile">相关配置说明详见：</label>
                                <a href="https://github.com/zhp8341/flink-streaming-platform-web/blob/master/docs/manual-batch.md"
                                   target="_blank">点击查看</a>
                            </div>
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
                                <label for="inputfile" >*flink运行配置(如yarn模式 -yjm 1024m -ytm 1024m -p 1 -yqu streaming)：</label>
                                <input class="form-control " type="text" name="flinkRunConfig" id="flinkRunConfig">
                            </div>
                            <div class="form-group">
                                <label for="inputfile" >三方jar地址 (自定义udf、连接器等jar地址
                                    多个用换行(如 http://xxxx.com/udf.jar) 目前只支持http )</label>
                                <textarea class="form-control"  name="extJarPath" id="extJarPath" rows="5" ></textarea>
                            </div>

                            <div class="form-group">
                                <label for="inputfile">*sql语句：</label>
                                <textarea name="flinkSql" id="flinkSql"> </textarea>
                            </div>

                            <div class="form-group">
                                <a class="btn btn-info btn-sm" onclick="addConfig()" href="#errorMessage">提交保存</a>
                                <a class="btn btn-success btn-sm " style="margin-left: 60px"
                                   onclick="autoFormatSelection()"> 格式化代码</a>

                            </div>

<#--                            <div class="form-group">-->
<#--                                <h5  style="color: #FFB752"> sql预校验 备注：只能校验单个sql语法正确与否,-->
<#--                                    不能校验上下文之间关系，如：这张表是否存在-->
<#--                                    数据类型是否正确等无法校验,总之不能完全保证运行的时候sql没有异常，只是能校验出一些语法错误-->
<#--                                </h5>-->
<#--                            </div>-->

                            <div class="form-group">
                                <label name="errorMessage" id="errorMessage"></label>
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

    editor.setSize('auto', '750px');

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


    $(function () {
        $("[data-toggle='tooltip']").tooltip();
    });


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


    function addConfig() {
        flinkSqlVal = editor.getValue();
        var chk_value =[];//定义一个数组
        $('input[name="alarmType"]:checked').each(function(){
            chk_value.push($(this).val());
        });
        $.post("../api/addConfig", {
                jobName: $('#jobName').val(),
                deployMode: $('#deployMode').val(),
                flinkRunConfig: $('#flinkRunConfig').val(),
                flinkSql: flinkSqlVal,
                jobType: $('#jobType').val(),
                alarmTypes:   chk_value.toString(),
                extJarPath: $('#extJarPath').val()
            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data != null && data.success) {
                    skipUrl("/admin/batchListPage")
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

</script>
</body>
</html>
