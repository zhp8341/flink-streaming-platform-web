
bootbox.setDefaults("locale","zh_CN");  //弹窗设置中文


function stop(id) {
    $.post("../api/stop", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                bootbox.alert({
                    message: "提交成功 请稍后刷新",
                    onShown: function(e) {
                        /* e is the shown.bs.modal event */
                    },
                    callback: function(e) {
                        window.location.reload();
                    }
                });

            }else{
                alert("执行失败："+data.message)
                window.location.reload();
            }

        }
    );
}
function start(id) {
    $.post("../api/start", {
            id: id

        },
        function (data, status) {
            // bootbox.confirm("投票已结束，是否直接查看投票结果？",function(re){
            //     if(re) {
            //     }
            // });



            if (data!=null && data.success){
                bootbox.alert({
                    message: "提交成功 请稍后刷新",
                    backdrop: false,
                    centerVertical: true,
                    callback: function(e) {
                        window.location.reload();
                    }
                });
            }else{
                alert("执行失败："+data.message)
                window.location.reload();
            }

        }
    );
}


function deleteConfig(id) {
    if(confirm('确定要删除吗')==true){
        $.post("../api/delete", {
                id: id
            },
            function (data, status) {
                if (data!=null && data.success){
                    window.location.reload();
                }else{
                    alert("执行失败："+data.message)
                }
            }
        );

    }else{
        return false;

    }

}

function openConfig(id) {
    $.post("../api/open", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                alert("执行成功");
                window.location.reload();
            }else{
                alert("执行失败："+data.message)
            }

        }
    );
}


function closeConfig(id) {
    $.post("../api/close", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                alert("执行成功");
                window.location.reload();
            }else{
                alert("执行失败："+data.message)
            }

        }
    );
}

function  savePoint(id){
    if(confirm('确定要手执行savePoint吗？')==true){
    $.post("../api/savepoint", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                alert("执行成功");
                window.location.reload();
            }else{
                alert("执行失败："+data.message)
            }
        }
    );
    }else{
        return false;

    }
}

function searchForm(pageNum) {
    $("#pageNum").attr("value", pageNum);
    $("form[name='search']").submit();

}

function refreshForm() {
    $("form[name='search']").submit();

}




