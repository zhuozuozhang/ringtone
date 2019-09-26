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