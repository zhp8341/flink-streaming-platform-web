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
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row ">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel">
                <h3 style="text-align:center">只显示最近10次(每个一小时自动备份或者停止任务的时候备份) </h3>
                <h5 style="text-align:center;color: red">备注：如果sql语句发生变更或者业务逻辑发生变化，此时从savepoint恢复可能导致数据结果错误 </h5>
            </div>

            <div class="col-md-12 column">
                <table class="table table-striped table-bordered">
                    <thead>
                    <tr>
                        <th colspan="4"> <span style="text-align:center"> <button type="button" id="btn_add" class="btn btn-primary"> 手动添加</button></span></th>

                    </tr>
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
                                   </#if>
                                </td>
                            </tr>
                        </#list>

                    </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

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
                <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>保存</button>
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
                    alert("执行成功 请稍后刷新");
                    window.location.href="../admin/listPage";
                }else{
                    alert("执行失败："+data.message)

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
                     ("执行成功");
                    window.location.reload();
                }else{
                    alert("执行失败："+data.message)

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
