//获取号码认证子订单列表
$(document).ready(function(){
    showTelCerMemberTable();
});
function showTelCerMemberTable() {
    var param = {
        "parentId": $("#id").val(),
        "phoneNum": $("#phoneNum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        // {"data": "product"},
        {"data": "years"},
        {"data": "price"},
        {"data": "telChildOrderStatus"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderOpenTime"},
        {"data": "telChildOrderExpireTime"}
    ];
    var columnDefs = [{
        targets:[4],
        render: function(data, type, row, meta){
            var status = row.telChildOrderStatus;
            //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if(status == 1){
                return "<span>开通中</span>";
            }else if(status == 2){
                return "<span>开通成功</span>";
            }else if(status == 3){
                return "<span>开通失败</span>";
            } else if(status == 4){
                return "<span>续费中</span>";
            }else if(status == 5){
                return "<span>续费成功</span>";
            }else if(status == 6){
                return "<span>续费失败</span>";
            }else{
                return "<span>未知</span>";
            }
        }
    }, {
        targets:[6],
        render: function(data, type, row, meta){
            if(data != null && data != ""){
                return data;
            }else{
                return "<div>暂无</div>";
            }
        }
    }, {
        targets:[9],
        render: function (data, type, row, meta) {
            var id = row.id;
            var phoneNum = row.telChildOrderPhone;
            return "<a href='javascript:;'><i class='layui-icon layui-icon-rmb' title='续费' onclick='Renewal("+phoneNum+");'></i></a>"
                + "<a href='/telcertify/toTelCostPage/"+phoneNum+"'><i class='layui-icon layui-icon-form' title='费用支出记录'></i></a>"
        }
    }];

    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum.trim())){
            if(!isPhone(param.phoneNum.trim())){
                if(!is_Phone(param.phoneNum).trim()){
                    layer.msg("请输入正确的手机号或者座机号，座机号请务必添加区号！",{icon: 5, time: 3000});
                    return;
                }
            }
        }
    }
    var url = "/telcertify/getTelCerMembersList";
    page("#members", 15, param, url, columns, columnDefs);
}

//打开添加号码弹窗
function Edits(){
    var panel = $('#ids');
    var back = $('#cover');
    panel.css('display',"block");
    back.css('display',"block");
    $("body").css("overflow","hidden");
    if($(document.body).height() >900){
        $("#cover").css("height",$(document.body).height());
    }
}
//关闭添加号码弹窗
function closePanel(){
    var panel = $('#ids');
    var back = $('#cover');
    panel.css('display',"none");
    back.css('display',"none");
    $("body").css("overflow","auto");
}
//续费
function Renewal(phoneNum){
    layer.open({
        type: 2,
        title: '续费',
        area: ['540px', '320px'],
        content: '/telcertify/toRenewPage/'+phoneNum
    });
}
// 确认续费
function confirmRenew() {
    var phoneNum = $("#phoneNum").val();

    AjaxPost("/telcertify/addRenewConsumeLog",{
        userTel:phoneNum
    },function (res) {
        if (res.code == 200 && res.data){
            layer.confirm(res.msg,{
                btn:'确定'
            },function () {
                window.parent.location.reload();
            });
        }else{
            layer.msg(res.msg,{icon: 5, time: 3000});
        }
    });

}

//验证成员号码
function checkNum(obj) {

    //所输入的号码集合
    var checkData = [];
    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value);
            var phones = checkData[i];
            var phoneregex = /^[1][3,4,5,7,8,9][0-9]{9}$/; //手机号码
            var tel_regex = /^(\d{3,4}\-)?\d{7,8}$/i;   //座机格式是 010-98909899 010-86551122
            var telregex = /^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/; //没有中间那段 -的 座机格式是 01098909899
            if (!phoneregex.test(phones)) {
                if (!tel_regex.test(phones)) {
                    if(!telregex.test(phones)){
                        layer.msg('号码"' + phones + '"不正确!');
                        break;
                    }
                }
            }
        }
    }
    if(checkData.length == 0){
        $("#allPrice").html(0);
    }else{
        $("#allPrice").html(checkData.length*$("#sendPrice").val());
    }
}
//添加号码
function addLandline() {
    //所输入的号码集合
    var checkData = [];
    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
        if (document.getElementsByClassName('numlists')[i].value.length == 0) {
            layui.use('layer', function () {
                layer.msg("新增号码不能为空！");
            });
            return;
        }
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value)
        }
    }
    $("#num").append(
        '<div class="layui-form-item" style="position: relative;"><div class="layui-input-block" style="margin-left: 156px;"><input type="text" style="width:81%;" name="content" class="layui-input numlists" value="" placeholder="如果是座机号码务必加上区号" autocomplete="off" onblur="checkNum(this)"></div><img src="../../client/telcertification/images/del.png" alt="" class="add_phone" onclick="delLandline(this);" width="28px"></div>'
    );
}
//删除号码
function delLandline(obj) {
    $(obj).parent().remove();
    checkNum(obj);
}
//添加多个号码
function addPhoneNumList(){
    var checkData = [];
    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
        if (document.getElementsByClassName('numlists')[i].value.length == 0) {
            layui.use('layer', function () {
                layer.msg("请输入号码！");
            });
            return;
        }
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value)
        }
    }

    AjaxPost("/telcertify/insertTelCerChild",{
        phoneNumberArray : JSON.stringify(checkData),
        parentOrderId : $("#id").val()
    },function (res) {
        if (res.code == 200 && res.data){
            layer.confirm(res.msg,{
                btn:'确定'
            },function () {
                window.parent.location.reload();//刷新父页面
            });
        }else{
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}
