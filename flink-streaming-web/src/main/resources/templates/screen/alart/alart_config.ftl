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
                                <input class="form-control " type="text" placeholder="请输入URl地址" name="val" id="val">
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
                                    <h5 style="text-align:center;color: red">1、目前暂时钉钉群告警且只能添加一个
                                        （当运行的任务挂掉的时候会告警）</h5>
                                    <h5 style="text-align:center;color: fuchsia">部署的机器需要支持外网否则无法支持钉钉发送</h5>

                                    <h5 style="text-align:center;color: red">2、回调url用于支持用户自定义告警，当任务出现告警时，会通过回调url通知 如
                                        http://127.0.0.1/alarmCallback
                                    </h5>
                                    <h5 style="text-align:center;color:
                                    fuchsia">回调url支持post、get请求，请求参数是appId、jobName、deployMode
                                    在自定义开发的时候必须要有这三个请求参数，且URN必须是alarmCallback

                                    </h5>
                                    <h5 style="text-align:center;color:
                                    fuchsia">
                                    <a href="https://github.com/zhp8341/flink-streaming-platform-web#3%E6%8A%A5%E8%AD%A6%E8%AE%BE%E7%BD%AE"  target="_blank">详见告警配置说明</a>
                                    </h5>


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
                                        <td>

                                            <#if systemConfigVO.key?? && systemConfigVO.key=="dingding_alart_url">
                                                <a href="#" id="dingding_test">测试一下</a>
                                            </#if>
                                            <#if systemConfigVO.key?? && systemConfigVO.key=="callback_alart_url">
                                                <a href="#" id="callback_test">测试一下</a>
                                            </#if>

                                            <a href="#" onclick="deleteConfig('${systemConfigVO.key}')">删除</a>

                                            <a href="/admin/alartLogList" target="_blank" >操作日志查看</a>

                                        </td>
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
                    $.gritter.add({
                        title: 'Success!',
                        text: '测试通过',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
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

    $("#callback_test").click(function () {
        $.post("../api/testHttpAlert", {},
            function (data, status) {
                if (data!=null && data.success){
                    $.gritter.add({
                        title: 'Success!',
                        text: '测试通过',
                        // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                        sticky: false,
                        time: 2000,
                        class_name: 'gritter-light,gritter-fontsize',
                        after_close: function(e) {
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
</script>
<script src="/static/js/customer/config.js?version=20210123"></script>
</body>
</html>
