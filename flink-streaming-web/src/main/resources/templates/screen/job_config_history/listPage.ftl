<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>历史版本查询列表</title>
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
                        <a href="#">历史版本</a>
                    </li>
                    <li class="active">Flink-SQL历史版本列表</li>
                </ul>
            </div>
            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <h4 style="text-align:center;color: red" >
                            只显示最近50次变更的记录(每次新增、修改都会记录)，另外如果想查看更多版本可以直接查库
                        </h4>
                    </div><!-- /.col -->
                </div>
            </div><!-- /.page-content -->

            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <div class="panel-body">
                            <table class="table table-striped table-bordered" style="text-align: center;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>配置ID</th>
                                    <th>任务名称</th>
                                    <th>运行模式</th>
                                    <th>版本号</th>
                                    <th>创建时间</th>
                                    <th>详情</th>
                                </tr>
                                </thead>
                                <tbody>

                                <#if jobConfigHistoryList?size == 0>
                                    <tr>
                                        <td colspan="7" align="center">
                                            没有数据
                                        </td>
                                    </tr>
                                <#else>

                                    <#list jobConfigHistoryList as jobConfigVO>
                                        <tr>
                                            <td>${jobConfigVO_index+1}</td>
                                            <td>${jobConfigVO.jobConfigId!""}</td>
                                            <td>${jobConfigVO.jobName!""}</td>
                                            <td>${jobConfigVO.deployMode!""}</td>
                                            <td>${jobConfigVO.version!""}</td>
                                            <td>${jobConfigVO.createTime!""}</td>
                                            <td>
                                                <a href="/admin/jobConfigHistoryDetailPage?id=${jobConfigVO.id!""}"  target="_blank">详情 </a>
                                            </td>
                                        </tr>
                                    </#list>

                                </#if>
                                </tbody>
                            </table>
                        </div>
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
