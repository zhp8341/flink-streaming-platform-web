<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>查询列表</title>
    <meta name="description" content="overview &amp; stats" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <#include "../../control/public_css_js.ftl">
</head>
<style>
    .ant-tag-green{
        color: #52c41a;
        background: #f6ffed;
        border-color: #52c41b;
        border: 1px solid #52c41a ;
        padding:0 4px;
        border-radius:6px;
    }

    .ant-tag-magenta{
        color: #eb2f96;
        background: #fff0f6;
        border-color: #ffadd2;
        border: 1px solid #eb2f96 ;
        padding:0 4px;
        border-radius:6px;
    }

    .ant-tag-b1{
        border: 3px solid #D15B47;
        padding: 0 1px;
        border-radius: 3px;
    }

    .ant-tag-b2{
        border: 3px solid #87B87F;
        padding: 0 1px;
        border-radius: 3px;
    }

</style>
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
                    <li class="active">自定义Jar任务列表</li>
                </ul>
            </div>


            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <div class="panel-body">
                            <form action="/admin/jarListPage" name="search" method="post">
                            <input type="hidden" name="pageNum" id="pageNum" value="${jobConfigParam.pageNum}">
                            <input type="hidden" name="pageSize" id="pageSize"  value="${jobConfigParam.pageSize}">
                            <div class="col-sm-3">
                                <input type="text" class="form-control" placeholder="任务名称(模糊查询)"
                                       name="jobName" <#if (jobConfigParam??)> value="${jobConfigParam.jobName!""}" </#if> />
                            </div>
                            <div class="col-sm-2">
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
                                <button type="button" class="btn btn-purple btn-sm" onclick="searchForm(1)">搜索</button>
                            </div>
                             <div class="col-sm-1">
                                 <button type="button" class="btn btn-danger btn-sm" onclick="refreshForm()">刷新</button>
                             </div>
                            <div class="col-sm-1">
                                <a class="btn btn-info btn-sm" href="/admin/addJarPage">新增</a>
                            </div>
                            </form>
                        </div>

                        <div class="panel-body">
                            <table class="table table-striped table-bordered"  style="text-align: center;">
                                <thead>
                                <tr>
                                    <th>配置ID</th>
                                    <th>任务名称</th>
                                    <th width="90px" >是否开用</th>
                                    <th>运行模式</th>
                                    <th>运行状态</th>
                                    <th>任务id</th>
                                    <th>创建时间</th>
<#--                                    <th>savePoint</th>-->
                                    <th>操作</th>
                                    <th>辅助</th>
                                    <th>日志</th>
                                </tr>
                                </thead>
                                <tbody>

                                <#if jobConfigList?size == 0>
                                    <tr>
                                        <td colspan="9" align="center">
                                            没有数据
                                        </td>
                                    </tr>
                                <#else>

                                    <#list jobConfigList as jobConfigVO>
                                        <tr>
                                            <td>${jobConfigVO.id!""}</td>
                                            <td>${jobConfigVO.jobName!""}</td>
                                            <td align="center" height="34px">
                                                <#if jobConfigVO.isOpen==1>
                                                <a href="#" class="btn-success ant-tag-b2"
                                                            onclick="closeConfig(${jobConfigVO.id})">关闭配置</a>
                                                <#else>
                                                    <a href="#" class="btn-danger ant-tag-b1 "
                                                            onclick="openConfig(${jobConfigVO.id})">开启配置</a>
                                                </#if>
                                            </td>
                                            <td>${jobConfigVO.deployMode!""}</td>
                                            <td>
                                                <#if jobConfigVO.stauts==1>
                                                    <span class="ant-tag-green">
                                                     ${jobConfigVO.stautsStr!""}
                                                 </span>
                                                <#else>
                                                    <#if jobConfigVO.stauts==-1>
                                                        <span  class="ant-tag-magenta">
                                                     ${jobConfigVO.stautsStr!""}
                                                    </span>
                                                    <#else>
                                                        ${jobConfigVO.stautsStr!""}
                                                    </#if>
                                                </#if>
                                            </td>
                                            <#if (jobConfigVO.jobId)??>
                                                <td> <a href="${jobConfigVO.flinkRunUrl!""}" target="_blank"> ${jobConfigVO.jobId!""}</a>  </td>
                                            <#else>
                                                <td></td>
                                            </#if>

                                            <td>${jobConfigVO.createTime!""}</td>
<#--                                            <td>-->
<#--                                                <#if jobConfigVO.deployMode=="YARN_PER">-->
<#--                                                    <a href="/admin/savepointList?jobConfigId=${jobConfigVO.id!""}"  target="_blank">历史备份</a>-->
<#--                                                </#if>-->
<#--                                                <#if jobConfigVO.deployMode=="LOCAL">-->
<#--                                                    本地模式不启用-->
<#--                                                </#if>-->
<#--                                                <#if jobConfigVO.deployMode=="STANDALONE">-->
<#--                                                    待开发中...-->
<#--                                                </#if>-->
<#--                                            </td>-->
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
                                                <a href="/admin/editJarPage?id=${jobConfigVO.id}"  target="_blank">修改</a>
<#--                                                <a href="/admin/detailPage?id=${jobConfigVO.id}" target="_blank">详情</a>-->


                                            </td>
                                            <td>${jobConfigVO.alarmStrs!""}</td>
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

                        <#if jobConfigList?size != 0>
                            <div class="panel-body">
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
                                        <li class="disabled "><a class="page-link" href="#" onclick="">下一页</a></li>
                                    <#else>
                                        <li>
                                            <a class="page-link" onclick="searchForm(${pageVO.pageNum+1})"  href="#" >下一页</a>
                                        </li>
                                    </#if>
                                </ul>
                            </div>
                        </#if>



                        <!-- PAGE CONTENT ENDS -->
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->


    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->


<script src="/static/js/customer/list_job_config.js?version=20210123"></script>
</body>
</html>
