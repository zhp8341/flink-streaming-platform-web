<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>savepoint历史备份</title>
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
                        <a href="#">savepoint管理</a>
                    </li>
                    <li class="active">savepoint</li>
                </ul>
            </div>

            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <h3 style="text-align:center">只显示最近10次(每个一小时自动备份或者停止任务的时候备份) </h3>
                        <h5 style="text-align:center;color: red">
                            备注1：如果sql语句发生变更或者业务逻辑发生变化，此时从savepoint恢复可能导致数据结果错误
                        </h5>
                        <h5 style="text-align:center;color: red">
                            备注2：yarn模式下和集群模式下统一目录是(必须绑定hdfs)： hdfs:///flink/savepoint/flink-streaming-platform-web/
                        </h5>
                        <h5 style="text-align:center;color: red">
                            备注3：LOCAL模式本地模式下保存在flink客户端的根目录下
                        </h5>
                        <h5 style="text-align:center;color: red">
                            备注4： hdfs:///flink/savepoint/flink-streaming-platform-web/ 建议提前创建好
                        </h5>
                    </div><!-- /.col -->
                </div>
            </div><!-- /.page-content -->

            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <button type="button" id="btn_add" class="btn btn-info btn-sm "> 手动添加</button>
                    </div><!-- /.col -->
                </div>
            </div><!-- /.page-content -->

            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <table class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <th>地址</th>
                                <th>备份时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>

                            <#if savepointList?size == 0>
                                <tr>
                                    <td colspan="4" align="center">
                                        <h4>没有数据</h4>
                                    </td>
                                </tr>
                            <#else>

                                <#list savepointList as savepointBackupVO>
                                    <tr>
                                        <td>${savepointBackupVO_index+1}</td>
                                        <td>${savepointBackupVO.savepointPath!""}</td>
                                        <td>${savepointBackupVO.backupTime!""}</td>
                                        <td>
                                            <#if startButton>
                                                <a href="#" onclick="start(${jobConfigId},${savepointBackupVO.id})">点击恢复任务</a>
                                            <#else>
                                                任务运行、关闭配置状态下不能使用
                                            </#if>
                                        </td>
                                    </tr>
                                </#list>

                            </#if>
                            </tbody>
                        </table>
                    </div><!-- /.col -->
                </div>
            </div><!-- /.page-content -->

        </div>
    </div><!-- /.main-content -->

    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">新增</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="txt_departmentname">savepoint地址</label>
                    <input type="text" name="savepointPath" class="form-control" id="savepointPath" placeholder="手动savepoint地址" />
                    <input type="hidden" name="jobConfigId" class="form-control"  id="jobConfigId" value="${jobConfigId}" />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>关闭</button>
                <button type="button" id="btn_submit" class="btn btn-info btn-sm " data-dismiss="modal"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>保存</button>
            </div>
        </div>
    </div>
</div>

<script>
    function start(id,savepointId) {
        $.post("../api/start", {
                id: id,
                savepointId:savepointId

            },
            function (data, status) {
                if (data!=null && data.success){
                    $.gritter.add({
                        title: 'Success!',
                        text: '成功，请稍后刷新',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
                            window.location.href="../admin/listPage";
                        }
                    });
                } else{
                    $.gritter.add({
                        title: 'Fail!',
                        text: '执行失败：' + data.message,
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 3000,
                        // class_name: (!$('#gritter-light').get(0).checked ? 'gritter-light' : ''),
                        after_close: function(e) {
                        }
                    });
                }

            }
        );
    }

    $("#btn_submit").click(function () {
        $.post("../api/addSavepoint", {
                jobConfigId:  $("#jobConfigId").val(),
                savepointPath:$("#savepointPath").val()
            },
            function (data, status) {
                if (data!=null && data.success){
                    $.gritter.add({
                        title: 'Success!',
                        text: '成功，请稍后刷新',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
                            window.location.reload();
                        }
                    });
                }else{
                    $.gritter.add({
                        title: 'Fail!',
                        text: '执行失败：' + data.message,
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 3000,
                        // class_name: (!$('#gritter-light').get(0).checked ? 'gritter-light' : ''),
                        after_close: function(e) {
                        }
                    });
                }

            }
        );
    });


    $("#btn_add").click(function () {
        $("#myModalLabel").text("新增");
        $('#myModal').modal();
    });
</script>
</body>
</html>
