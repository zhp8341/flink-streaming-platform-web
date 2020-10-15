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
    <link href="/static/css/dashboard/dashboard.css" rel="stylesheet">
</head>

<body>
<#include "../../layout/top.ftl">

<div class="container-fluid">
    <div class="row clearfix">
        <#include "../../layout/menu.ftl" >
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel">
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
                            <button type="button" class="btn btn-primary" onclick="searchForm(1)">搜索</button>
                        </div>
                </form>
            </div>
        </div>

        <div class="col-md-12 column">
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
        </div>


        <#--分页-->
        <#if alartLogVOList?size != 0>
         <div class="col-md-12 column">
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
                        <li class="page-item active "><a class="page-link" href="#" onclick="searchForm(${index})" >${index}</a>
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
                        <a class="page-link" onclick="searchForm(${pageVO.pageNum+1})" >下一页</a>
                    </li>
                </#if>
            </ul>
         </div>
        </#if>
    </div>
</div>


<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
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
                    alert("查询失败："+data.message)

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
