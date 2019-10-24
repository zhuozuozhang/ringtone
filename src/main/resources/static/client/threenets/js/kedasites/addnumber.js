//表单提交
function sub() {
    var member_phone = $("#member_phone").val();
    var phones = member_phone.split(/[\r\n]/g);
    if (!isNotEmpty(member_phone)) {
        layer.msg('员工号码不能为空');
        return;
    }
    // 执行添加子订单操作
    AjaxPost("/threenets/clcy/batchInsertKedaChildOrder", {
        linkTel: member_phone,
        orderId: $("#orderId").val()
    }, function (res) {
        if (res.code == 200) {
            layer.msg(res.msg, {icon: 6, time: 2500});
            setTimeout(function () {
                // 获得frame索引
                var index = parent.layer.getFrameIndex(window.name);
                //关闭当前frame
                parent.layer.close(index);
            }, 3000);
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}
//表单提交
function subNew() {
    var tels = $("#tels").val();
    if (!isNotEmpty(tels)) {
        layer.msg('员工号码不能为空');
        return;
    }
    // 执行添加子订单操作
    AjaxPost("/threenets/clcy/batchInsertKedaChildOrder", {
        tels: $("#tels").val(),
        orderId: $("#orderId").val()
    }, function (res) {
        if (res.code == 200) {
            layer.msg(res.msg, {icon: 6, time: 2500});
            setTimeout(function () {
                // 获得frame索引
                var index = parent.layer.getFrameIndex(window.name);
                //关闭当前frame
                parent.layer.close(index);
            }, 3000);
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}
//上传客户确认函
$("form").on("change", "#clientFile", function (e) {
    $(".queren").html(this.value);
    uploadFile()
})
//上传文件
function uploadFile() {
    var layuiLoding = layer.load(0, { //icon支持传入0-2
        time:false,
        shade: [0.5, '#9c9c9c'], //0.5透明度的灰色背景
        content: '文件上传中...',
        success: function (layero) {
            layero.find('.layui-layer-content').css({
                'padding': '39px 10px',
                'width': '100px'
            });
        }
    });
    $.ajax({
        url: '/system/upload/clientFile',
        type: 'POST',
        cache: false,
        data: new FormData($('#Form')[0]),
        processData: false,
        contentType: false
    }).done(function (res) {
        layer.close(layuiLoding);
        layer.msg(res.msg, {icon: 1, time: 1000});
        $("#protocol").val(res.data)
    }).fail(function (res) {
        layer.msg(res.msg, {icon: 2, time: 1000});
    });
}

function verificationTels() {
    var member_phone = $("#memberTels").val();
    if ($("#memberTels").val() == '' || $("#memberTels").val() == null) {
        $("#memberTels").focus();
        layer.msg("成员号码不能为空！");
        return
    }
    var phones = member_phone.split(/[\r\n]/g);
    var tels = "";
    for (i = 0; i < phones.length; i++) {
        tels = phones[i] + "," + tels;
    }
    $("#tels").val(tels);
    AjaxPost("/threenets/clcy/formatPhoneNumber", {
        "tels": tels,
        "orderId":$("#orderId").val()
    }, function (result) {
        if (result.code == 500) {
            layer.msg(result.msg);
            $("#memberTels").focus();
        } else {
            if (result.data > 0) {
                $("#protocolTelecom10Div").show();
                $("#protocolTelecom20Div").show();
            }else{
                $("#protocolTelecom10Div").hide();
                $("#protocolTelecom20Div").hide();
            }
        }
    })
}