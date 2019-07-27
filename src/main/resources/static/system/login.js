
/**
 * Author:zcy
 * Date:2019-07-03 11:33
 * Description:登录控制器
 */

$(function(){
    // 点击执行登录
    $("#login").click(function(){
        check_login();
        return false;
    });
    $('.imgcode').click(function() {
        var url = ctx + "imageCode?type=char&s=" + Math.random();
        $(".imgcode").attr("src", url);
    });
});

// 检查用户名密码是否为空
function check_login(){
    var name=$("#user_name").val();
    var pass=$("#password").val();
    alert(name);
    if (!isNotEmpty(name)) {
        layer.msg('登录名不能为空!',{icon: 5,time:1000});
        $("#login_form").removeClass('shake_effect');
        setTimeout(function(){
            $("#login_form").addClass('shake_effect')
        },1);
        return;
    }
    if (!isNotEmpty(pass)){
        layer.msg('密码不能为空!',{icon: 5,time:1000});
        $("#login_form").removeClass('shake_effect');
        setTimeout(function(){
            $("#login_form").addClass('shake_effect')
        },1);
        return;
    }
    $(".login-form").submit();
}