<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">
    <title>告警</title>
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
                        <a href="#">配置管理</a>
                    </li>
                    <li class="active">编辑配置</li>
                </ul>
            </div>
            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">


                            <div class="form-group " name="errorMessage" id="errorMessage"></div>

                            <div class="form-group">
                                <select class="form-control " name="key" id="key">
                                    <#list sysConfigVOList as val>
                                        <option value="${val.getKey()}">${val.getDesc()}</option>
                                    </#list>
                                </select>
                            </div>
                            <div class="form-group">
                                <input class="form-control " type="text" placeholder="变量值" name="val" id="val">
                            </div>
                            <div class="form-group">
                                <button class="btn btn-info btn-sm " type="button" onclick="upsertSynConfig()">保存</button>
                            </div>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.page-content -->


            <div class="page-content">

                <div class="row">
                    <div class="col-xs-12">

                        <table class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th colspan="4" >
                                    <h5 style="text-align:center;color: red">备注：目前暂时只支持钉钉群告警且只能添加一个 （当运行的任务挂掉的时候会告警）</h5>
                                    <h5 style="text-align:center;color: fuchsia">部署的机器需要支持外网否则无法支持钉钉发送</h5>
                                </th>
                            </tr>
                            <tr>
                                <th>名称</th>
                                <th>健值</th>
                                <th>描述</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>

                            <#if systemConfigVOList?size == 0>
                                <tr>
                                    <td colspan="6" align="center">
                                        你还没有配置报警
                                    </td>
                                </tr>
                            <#else>

                                <#list systemConfigVOList as systemConfigVO>
                                    <tr>
                                        <td>${systemConfigVO.key}</td>
                                        <td>${systemConfigVO.val}</td>
                                        <td>${systemConfigVO.desc}</td>
                                        <td><a href="#" id="dingding_test">测试一下</a> <a href="#" onclick="deleteConfig('${systemConfigVO.key}')">删除</a></td>
                                    </tr>
                                </#list>

                            </#if>
                            </tbody>
                        </table>


                    </div><!-- /.col -->
                </div><!-- /.row -->





            </div><!-- /.page-content -->
        </div>
    </div><!-- /.main-content -->

    <#include "../../layout/bottom.ftl">

</div><!-- /.main-container -->

<script>
    $("#dingding_test").click(function () {
        $.post("../api/testDingdingAlert", {},
            function (data, status) {
                if (data!=null && data.success){
                    alert("测试通过成功 ");
                }else{
                    alert("执行失败："+data.message)
                }

            }
        );
    });
</script>
<script src="/static/js/customer/config.js?version=20210123"></script>
</body>
</html>
