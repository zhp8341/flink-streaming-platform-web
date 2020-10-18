<div class="col-sm-3 col-md-2 sidebar ">

    <ul class="nav nav-list">
        <li class="nav-header" class="active">配置管理</li>
        <li <#if active??&& active=="list" > class="active" </#if> ><a href="/admin/listPage">任务列表  </a></li>
        <li <#if active??&&active=="addpage" > class="active" </#if> ><a href="/admin/addPage">新增配置</a></li>
        <li class="nav-header" >日志管理</li>
        <li <#if active??&&active=="log" > class="active" </#if> ><a href="/admin/logList">运行日志</a></li>
        <li class="nav-header">系统管理</li>
        <li <#if active??&&active=="synconfig" > class="active" </#if> ><a href="/admin/sysConfig">系统设置</a></li>
        <li class="nav-header">报警管理</li>
        <li <#if active??&&active=="alartConfig" > class="active" </#if> ><a href="/admin/alartConfig">报警设置</a></li>
        <li <#if active??&&active=="alartLogList" > class="active" </#if> ><a href="/admin/alartLogList">报警日志</a></li>
    </ul>


</div>
