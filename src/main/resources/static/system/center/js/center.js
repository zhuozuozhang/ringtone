$(function () {
    //一层弹窗
    $('#cause').on('click', function () { //点击按钮显示弹窗
        $("#pop").removeClass("display")
    })
    $("#cancel").on('click', function () { //点击取消隐藏弹窗
        $("#pop").addClass("display")
    })
    //表格切换
    $('#czjl').on('click', function () {
        $(".recharge_record").removeClass("bxs")
        $("#czjl").addClass("active")
        $(".spending_record").addClass("bxs")
        $("#zcjl").removeClass("active")
    })
    $('#zcjl').on('click', function () {
        $(".recharge_record").addClass("bxs")
        $("#czjl").removeClass("active")
        $(".spending_record").removeClass("bxs")
        $("#zcjl").addClass("active")
    })
});

//获取验证码
var colock = '';
var num = 60;
function sendMessage() {
    var phone = $("#phone").val();
    if (!isNotEmpty(phone)){
        layer.msg('验证手机号不能为空!', {icon: 5, time: 1000});
        return;
    }
    $("#tel_code").attr("disabled", "true");
    $("#tel_code").val(num + "秒后重发");
    colock = setInterval(doLoop, 1000);//一秒一次
    AjaxPost("/system/sendMessage/1",{
        phone:phone
    },function (res) {
        if (res.code == 200 && res.data){
            layer.msg(res.msg, {icon: 6, time: 3000});
        }else {
            layer.msg(res.msg, {icon: 5, time: 3000});
            clearInterval(colock);
            $("#tel_code").removeAttr("disabled");
            $("#tel_code").val("获取验证码");
            num = 60;
        }
    });
}
function doLoop() {
    num--;
    if (num > 0) {
        $("#tel_code").val(num + "秒后重发");
    } else {
        clearInterval(colock);//
        $("#tel_code").removeAttr("disabled");
        $("#tel_code").val("获取验证码");
        num = 60;
    }
}

// 执行修改密码操作
function changepwd(){
    var oldword = $("#oldword").val();
    var newpwd = $('#newpwd').val();
    var againpwd= $('#againpwd').val();
    // var phone= $('#phone').val();
    // var code = $('#code').val();

    // 判断密码
    if (!isNotEmpty(newpwd)) {
        layer.msg('新密码不能为空!', {icon: 5, time: 1000});
        return;
    }
    // 判断确认密码
    if (!isNotEmpty(againpwd)) {
        layer.msg('确认新密码不能为空!', {icon: 5, time: 1000});
        return;
    }
    // 判断确认密码
    if (againpwd != newpwd) {
        layer.msg('密码不一致，请确认密码!', {icon: 5, time: 1000});
        return;
    }

    AjaxPut("/threenets/updatePasswor",{
        oldPassword:oldword,
        newPassword:newpwd
    },function (res) {
        if (res.code == 200 && res.data){
            layer.msg("修改成功,下次请使用新密码登录",{icon: 1,time:2500});
            $("#pop").addClass("display")
        }else{
            layer.msg(res.msg,{icon: 1,time:1000});
        }
    })


    // 判断手机号
    // if (!isNotEmpty(phone)) {
    //     layer.msg('手机号不能为空!', {icon: 5, time: 1000});
    //     return;
    // } else {
    //     if (!isTel(phone)) {
    //         layer.msg('手机号格式不正确!', {icon: 5, time: 1000});
    //         return;
    //     }
    // }
    // // 判断验证码
    // if (!isNotEmpty(code)) {
    //     layer.msg('验证码不能为空!', {icon: 5, time: 1000});
    //     return;
    // }
    // AjaxPut("/system/updatePassword",{
    //     userPassword:newpwd,
    //     code :code
    // },function (res) {
    //     if (res.code = 200 && res.data) {
    //         layer.msg(res.msg, {icon: 6, time: 3000});
    //         $("#pop").addClass("display")
    //     }else{
    //         layer.msg(res.msg, {icon: 5, time: 3000});
    //     }
    // });
}