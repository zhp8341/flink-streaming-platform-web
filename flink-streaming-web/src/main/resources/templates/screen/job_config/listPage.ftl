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
                <form action="/admin/listPage" name="search" method="post">
                    <div class="panel-body">

                        <input type="hidden" name="pageNum" id="pageNum" value="${jobConfigParam.pageNum}">
                        <input type="hidden" name="pageSize" id="pageSize"  value="${jobConfigParam.pageSize}">
                        <div class="col-sm-3">
                            <input type="text" class="form-control" placeholder="任务名称(模糊查询)"
                                   name="jobName" <#if (jobConfigParam??)> value="${jobConfigParam.jobName!""}" </#if> />
                        </div>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" placeholder="任务id"
                                   name="jobId"  <#if (jobConfigParam??) >  value="${jobConfigParam.jobId!""}" </#if> />
                        </div>
                        <div class="col-sm-2">
                            <select class="form-control" name="stauts">
                                <option value=""> 运行状态</option>
                                <option value="1" <#if (jobConfigParam??) &&(jobConfigParam.stauts??) && jobConfigParam.stauts==1> selected</#if> >
                                    运行中
                                </option>
                                <option value="0" <#if (jobConfigParam??) &&(jobConfigParam.stauts??) && jobConfigParam.stauts==0> selected</#if> >
                                    停止中
                                </option>
                                <option value="-1" <#if (jobConfigParam??)&&(jobConfigParam.stauts??) && jobConfigParam.stauts==-1> selected</#if> >
                                    运行失败
                                </option>
                            </select>
                        </div>
                        <div class="col-sm-2">
                            <select class="form-control" name="open">
                                <option value=""> 配置状态</option>
                                <option value="1" <#if (jobConfigParam??) &&(jobConfigParam.open??) && jobConfigParam.open==1> selected</#if> >
                                   开启
                                </option>
                                <option value="0" <#if (jobConfigParam??) &&(jobConfigParam.open??) && jobConfigParam.open==0> selected</#if> >
                                   关闭
                                </option>
                            </select>
                        </div>
                        <div class="col-sm-1">
                            <button type="button" class="btn btn-primary" onclick="searchForm(1)">搜索</button>
                        </div>
                        <div class="col-sm-1">
                            <a class="btn btn-primary" href="/admin/addPage">新增</a>
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
                    <th>运行模式</th>
                    <th>是否开启</th>
                    <th>运行状态</th>
                    <th>任务id</th>
                    <th>创建时间</th>
                    <th>savePoint</th>
                    <th>操作</th>
                    <th>日志</th>
                </tr>
                </thead>
                <tbody>

                <#if jobConfigList?size == 0>
                    <tr>
                        <td colspan="10" align="center">
                            没有数据
                        </td>
                    </tr>
                <#else>

                    <#list jobConfigList as jobConfigVO>
                        <tr>
                            <td>${jobConfigVO.id!""}</td>
                            <td>${jobConfigVO.jobName!""}</td>
                            <td>${jobConfigVO.deployMode!""}</td>
                            <td>${jobConfigVO.isOpenStr!""}
                                <#if jobConfigVO.isOpen==1>
                                    <a  href="#" onclick="closeConfig(${jobConfigVO.id})">关闭配置</a>
                                <#else>
                                    <a href="#"  onclick="openConfig(${jobConfigVO.id})">开启配置</a>
                                </#if>
                            </td>
                            <td>${jobConfigVO.stautsStr!""} </td>
                            <#if (jobConfigVO.jobId)??>
                                <td> <a href="${jobConfigVO.flinkRunUrl!""}" target="_blank"> ${jobConfigVO.jobId!""}</a>  </td>
                            <#else>
                                <td></td>
                            </#if>


                            <td>${jobConfigVO.createTime!""}</td>
                            <td> <a href="/admin/savepointList?jobConfigId=${jobConfigVO.id!""}"  target="_blank">历史备份</a> </td>
                            <td>
                                <#if jobConfigVO.isOpen==1>

                                    <#if jobConfigVO.stauts==1>
                                        <a href="#" onclick="stop(${jobConfigVO.id})">停止任务</a>
                                    <#else>
                                        <a href="#" onclick="start(${jobConfigVO.id})">提交任务</a>
                                    </#if>
                                <#else>
                                    <a href="#" onclick="deleteConfig(${jobConfigVO.id})">删除</a>
                                </#if>
                                <a href="/admin/editPage?id=${jobConfigVO.id}"  target="_blank">修改</a>
                                <a href="/admin/detailPage?id=${jobConfigVO.id}" target="_blank">详情</a>


                            </td>
                            <td>
                             <#if jobConfigVO.lastRunLogId??>
                                 <a href="/admin/detailLog?id=${jobConfigVO.lastRunLogId!""}"  target="_blank">日志详情 </a>
                                 <a href="/admin/logList?jobConfigId=${jobConfigVO.id!""}"  target="_blank">历史日志 </a>
                              </#if>

                            </td>
                        </tr>
                    </#list>

                </#if>
                </tbody>
            </table>
        </div>


        <#--分页-->
        <#if jobConfigList?size != 0>
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

<#include "../../layout/bottom.ftl">

<script src="/static/js/customer/list_job_config.js"></script>
</body>
</html>
