//表单提交
function sub() {
    var apersonnelName = $("#apersonnelName").val();
    var apersonnelPhone = $("#apersonnelPhone").val();
    if (!isNotEmpty(apersonnelName)) {
        layer.msg('员工姓名不能为空');
        return;
    }
    if (!isNotEmpty(apersonnelPhone)) {
        layer.msg('员工号码不能为空');
        return;
    } else {
        var myreg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
        var areg = /^0\d{2,3}-?\d{7,8}$/;
        if (!myreg.test(apersonnelPhone)) {
            if (!areg.test(apersonnelPhone)) {
                layer.msg("号码格式不正确");
                return;
            }
        }
    }
    // 执行添加子订单操作
    AjaxPost("/threenets/clcy/insertKedaChildOrder", {
        linkMan: apersonnelName,
        linkTel: apersonnelPhone,
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