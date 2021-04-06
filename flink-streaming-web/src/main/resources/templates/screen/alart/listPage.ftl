<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>查询列表</title>
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
                        <a href="#">报警管理</a>
                    </li>
                    <li class="active">报警日志</li>
                </ul>
            </div>

            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <form action="/admin/alartLogList" name="search" method="post">
                            <div class="panel-body">

                                <input type="hidden" name="pageNum" id="pageNum" value="${alartLogParam.pageNum}">
                                <input type="hidden" name="pageSize" id="pageSize"  value="${alartLogParam.pageSize}">


                                <div class="col-sm-2">
                                    <input type="text" class="form-control" placeholder="配置id"
                                           name="jobConfigId"  <#if (alartLogParam??) >  value="${alartLogParam.jobConfigId!""}" </#if> />
                                </div>

                                <div class="col-sm-2">
                                    <select class="form-control" name="status">
                                        <option value="">选择状态</option>
                                        <option  <#if  alartLogParam.status?? &&  alartLogParam.status==1 > selected </#if>   value="1">成功</option>
                                        <option  <#if  alartLogParam.status?? &&  alartLogParam.status==0 > selected </#if>   value="0">失败</option>
                                    </select>
                                </div>

                                <div class="col-sm-1">
                                    <button type="button" class="btn btn-info btn-sm" onclick="searchForm(1)">搜索</button>
                                </div>
                                <div class="col-sm-2">
                                    <h5  style="color: red">(只显示最近30天)</h5>
                                </div>
                            </div>
                        </form>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->

            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">
                        <table class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th>配置ID</th>
                                <th>任务名称</th>
                                <th>告警类型</th>
                                <th>消息内容</th>
                                <th>状态</th>
                                <th>告警时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>

                            <#if alartLogVOList?size == 0>
                                <tr>
                                    <td colspan="7" align="center">
                                        没有数据
                                    </td>
                                </tr>
                            <#else>

                                <#list alartLogVOList as alartLogVO>
                                    <tr>
                                        <td>${alartLogVO.jobConfigId!""}</td>
                                        <td>${alartLogVO.jobName!""}</td>
                                        <td>${alartLogVO.typeDesc!""}</td>
                                        <td>${alartLogVO.message!""} </td>
                                        <td>${alartLogVO.statusDesc!""}</td>
                                        <td>${alartLogVO.createTime!""}</td>
                                        <td>
                                            <#if alartLogVO.status==0>
                                                <a href="#" onclick="detailError(${alartLogVO.id})"  data-toggle="modal" data-target="#myModal">查看错误日志</a>
                                            </#if>
                                        </td>
                                    </tr>
                                </#list>

                            </#if>
                            </tbody>
                        </table>
                    </div><!-- /.col -->
                    <div class="col-xs-12">
                        <#if alartLogVOList?size != 0>
                            <ul class="pagination">

                                <#if pageVO.pages lte 1>
                                    <li class="disabled "><a class="page-link" href="#">上一页</a></li>
                                <#else>
                                    <li>
                                        <a class="page-link" href="#" onclick="searchForm(${pageVO.pages -1})" >上一页</a>
                                    </li>
                                </#if>

                                <#list 1..pageVO.pages as index>
                                    <#if pageVO.pageNum == index>
                                        <li class="page-item active ">
                                            <a class="page-link" href="#" onclick="searchForm(${index})" >${index}</a>
                                        </li>
                                    <#else>
                                        <li>
                                            <a class="page-link" href="#" onclick="searchForm(${index})" >${index}</a>
                                        </li>
                                    </#if>
                                </#list>

                                <#if pageVO.pageNum gte pageVO.pages>
                                    <li class="disabled "><a class="page-link" href="#">下一页</a></li>
                                <#else>
                                    <li>
                                        <a class="page-link" onclick="searchForm(${pageVO.pageNum+1})"  href="#">下一页</a>
                                    </li>
                                </#if>
                            </ul>
                        </#if>
                    </div><!-- /.col -->
                </div><!-- /.row -->

            </div><!-- /.page-content -->




        </div>
    </div><!-- /.main-content -->

    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->


<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width:76%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    错误信息
                </h4>
            </div>
            <div class="modal-body" id="errorInfo">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                </button>

            </div>
        </div>
    </div>
</div>

<script>
    function searchForm(pageNum) {
        $("#pageNum").attr("value", pageNum);
        $("form[name='search']").submit();
    }


    function detailError(id) {
        $.post("../api/logErrorInfo", {
                id: id
            },
            function (data, status) {
                if (data!=null && data.success){
                    $("#errorInfo").html(data.data)
                }else{
                    $.gritter.add({
                        title: 'Fail!',
                        text: '查询失败：' + data.message,
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

    $(function () { $('.popover-show').popover('show');});
    $(function () { $('.popover-hide').popover('hide');});
    $(function () { $('.popover-destroy').popover('destroy');});
    $(function () { $('.popover-toggle').popover('toggle');});
    $(function () { $(".popover-options a").popover({html : true });});
</script>
</body>
</html>
