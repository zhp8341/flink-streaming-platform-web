<#--<div class="col-sm-3 col-md-2 sidebar ">-->

<#--    <ul class="nav nav-list">-->
<#--        <li class="nav-header" class="active">配置管理</li>-->
<#--        <li <#if active??&& active=="list" > class="active" </#if> ><a href="/admin/listPage">任务列表  </a></li>-->
<#--        <li <#if active??&&active=="addpage" > class="active" </#if> ><a href="/admin/addPage">新增配置</a></li>-->
<#--        <li class="nav-header" >日志管理</li>-->
<#--        <li <#if active??&&active=="log" > class="active" </#if> ><a href="/admin/logList">运行日志</a></li>-->
<#--        <li class="nav-header">系统管理</li>-->
<#--        <li <#if active??&&active=="synconfig" > class="active" </#if> ><a href="/admin/sysConfig">系统设置</a></li>-->
<#--        <li class="nav-header">报警管理</li>-->
<#--        <li <#if active??&&active=="alartConfig" > class="active" </#if> ><a href="/admin/alartConfig">报警设置</a></li>-->
<#--        <li <#if active??&&active=="alartLogList" > class="active" </#if> ><a href="/admin/alartLogList">报警日志</a></li>-->
<#--    </ul>-->


<#--</div>-->
<div id="sidebar" class="sidebar                  responsive                    ace-save-state">
    <script type="text/javascript">
        try{ace.settings.loadState('sidebar')}catch(e){}
    </script>

    <ul class="nav nav-list">

        <li <#if open??&& open=="config" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-list"></i>
                <span class="menu-text"> 配置管理 </span>

                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li <#if active??&& active=="list" > class="active" </#if> >
                    <a href="/admin/listPage">
                        <i class="menu-icon fa fa-caret-right"></i>
                        SQL任务列表
                    </a>
                    <b class="arrow"></b>
                </li>
                <li <#if active??&& active=="jarlist" > class="active" </#if>>
                    <a href="/admin/jarListPage">
                        <i class="menu-icon fa fa-caret-right"></i>
                        JAR任务列表
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>

        <li <#if open??&& open=="log" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-file-o"></i>
                <span class="menu-text">日志管理</span>

                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li  <#if active??&& active=="logList" > class="active" </#if> >
                    <a href="/admin/logList">
                        <i class="menu-icon fa fa-caret-right"></i>
                        运行日志
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>

        <li <#if open??&& open=="system" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-desktop"></i>
                <span class="menu-text">系统管理</span>

                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li <#if active??&& active=="synconfig" > class="active" </#if>>
                    <a href="/admin/sysConfig">
                        <i class="menu-icon fa fa-caret-right"></i>
                        系统设置
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>

        <li <#if open??&& open=="alart" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-list-alt"></i>
                <span class="menu-text">报警管理</span>

                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li <#if active??&& active=="alartConfig" > class="active" </#if>>
                    <a href="/admin/alartConfig">
                        <i class="menu-icon fa fa-caret-right"></i>
                        报警设置
                    </a>
                    <b class="arrow"></b>
                </li>
                <li <#if active??&& active=="alartLog" > class="active" </#if>>
                    <a href="/admin/alartLogList">
                        <i class="menu-icon fa fa-caret-right"></i>
                        报警日志
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>
        <li <#if open??&& open=="user" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-user" style="color: #444"></i>
                <span class="menu-text">用户管理</span>

                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li <#if active??&& active=="userlist" > class="active" </#if>>
                    <a href="/admin/userList">
                        <i class="menu-icon fa fa-caret-right"></i>
                        用户列表
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>
        <li <#if open??&& open=="qrcode" > class="open" </#if>>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon  fa fa-search" ></i>
                <span class="menu-text">联系方式</span>
                <b class="arrow fa fa-angle-down"></b>
            </a>
            <b class="arrow"></b>
            <ul class="submenu">
                <li <#if active??&& active=="qrcode"  > class="active" </#if>>
                    <a href="/admin/qrcode" target="_blank">
                        <i class="menu-icon fa fa-caret-right"></i>
                        联系方式
                    </a>
                    <b class="arrow"></b>
                </li>
            </ul>
        </li>


    </ul>
</div>
