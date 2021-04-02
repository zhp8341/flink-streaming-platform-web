function stop(id) {
    $.post("../api/stop", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                $.gritter.add({
                    title: 'Success!',
                    text: '提交成功，请稍后刷新',
                    // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                    sticky: false,
                    time: 2000,
                    class_name: 'gritter-light,gritter-fontsize',
                    after_close: function(e) {
                        window.location.reload();
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
                        window.location.reload();
                    }
                });
            }

        }
    );
}
function start(id) {
    $.post("../api/start", {
            id: id
        },
        function (data, status) {
            if (data !== null && data.success) {
                $.gritter.add({
                    title: 'Success!',
                    text: '提交成功，请稍后刷新',
                    // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                    sticky: false,
                    time: 2000,
                    class_name: 'gritter-light,gritter-fontsize',
                    after_close: function(e) {
                        window.location.reload();
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
                        window.location.reload();
                    }
                });
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
                $.gritter.add({
                    title: 'Success!',
                    text: '成功，请稍后刷新',
                    // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                    sticky: false,
                    time: 2000,
                    class_name: 'gritter-light,gritter-fontsize',
                    after_close: function(e) {
                        window.location.reload();
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
}


function closeConfig(id) {
    $.post("../api/close", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                $.gritter.add({
                    title: 'Success!',
                    text: '成功，请稍后刷新',
                    // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                    sticky: false,
                    time: 2000,
                    class_name: 'gritter-light,gritter-fontsize',
                    after_close: function(e) {
                        window.location.reload();
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
}

function  savePoint(id){
    if(confirm('确定要手执行savePoint吗？')==true){
    $.post("../api/savepoint", {
            id: id
        },
        function (data, status) {
            if (data!=null && data.success){
                $.gritter.add({
                    title: 'Success!',
                    text: '执行成功，请稍后刷新',
                    // image: 'assets/images/avatars/avatar1.png', //in Ace demo ./dist will be replaced by correct assets path
                    sticky: false,
                    time: 2000,
                    class_name: 'gritter-light,gritter-fontsize',
                    after_close: function(e) {
                        window.location.reload();
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




