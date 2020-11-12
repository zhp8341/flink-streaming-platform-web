

function upsertSynConfig() {
    $.post("../api/upsertSynConfig", {
            key: $('#key').val(),
            val: $('#val').val()
        },
        function (data, status) {
            $("#errorMessage").removeClass();
            if (data != null && data.success) {
                window.location.reload();
            } else {
                $("#errorMessage").addClass("form-group alert alert-danger")
                $("#errorMessage").html(data.message);

            }
        }
    );
}


function deleteConfig(key) {
    if(confirm('确定要删除吗')==true) {
        $.post("../api/deleteConfig", {
                key: key
            },
            function (data, status) {
                $("#errorMessage").removeClass();
                if (data != null && data.success) {
                    window.location.reload();
                } else {
                    $("#errorMessage").addClass("form-group alert alert-danger")
                    $("#errorMessage").html(data.message);

                }
            }
        );
    }else{
        return false;
    }

}

