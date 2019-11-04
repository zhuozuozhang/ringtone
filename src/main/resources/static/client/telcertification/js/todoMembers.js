//获取号码认证子订单列表
$(document).ready(function(){
    showTelCerMemberTable();
});
function showTelCerMemberTable() {
    var param = {
        "phoneNum": $("#phoneNum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        {"data": "userName"},
        {"data": "years"},
        {"data": "price"},
        {"data": "telChildOrderStatus"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderOpenTime"},
        {"data": "telChildOrderExpireTime"}
    ];
    var columnDefs = [{
        targets:[2],
        render: function(data, type, row, meta){
            var userName = row.userName;
            var ye = row.userPrice;
            var iocn =userName + " (<i class='layui-icon layui-icon-rmb'></i> " +ye +")";
            return iocn;
        }
    },{
        targets:[5],
        render: function(data, type, row, meta){
            var status = row.telChildOrderStatus;
            //号码认证子订单状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if(status == 1){
                return "<span>审核中</span>";
            }else if(status == 2){
                return "<span>开通中</span>";
            }else if(status == 3){
                return "<span>开通成功</span>";
            } else if(status == 4){
                return "<span>续费中</span>";
            }else if(status == 5){
                return "<span>续费成功</span>";
            }else if(status == 6){
                return "<span>续费失败</span>";
            }else if(status == 7){
                return "<span>余额不足,请充值</span>";
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
        targets:[10],
        render: function (data, type, row, meta) {
            var id = row.id;
            var pid = row.parentOrderId;
            var phoneNum = row.telChildOrderPhone;
            var loginName = $("#loginName").val();
            var result = "";
            if(loginName == '江苏中高俊聪'){
                result = "<a href='javascript:;'><i class='layui-icon layui-icon-util' title='开通' onclick='Opening("+id+");'></i></a>";
            }
            result = result + "<a href='javascript:;'><i class='layui-icon layui-icon-rmb' title='续费' onclick='Renewal("+id+");'></i></a>"
                + "<a href='/telcertify/toTelCostPage/"+phoneNum+"'><i class='layui-icon layui-icon-form' title='费用支出记录'></i></a>"
                + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetails("+pid+");'></i>";

            return result;
        }
    }];

    var url = "/telcertify/todoTelMemberList";
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

/**
 * 开通
 * @param id
 * @constructor
 */
function Opening(id){
    AjaxPost("/telcertify/opening",{
        id: id
    },function (res) {
        if(res.code == 200 && res.data){
            layer.confirm(res.msg,{
                btn:'确定'
            },function () {
                window.parent.location.reload();//刷新父页面
            });
        }else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}

//续费
function Renewal(id){
    layer.open({
        type: 2,
        title: '续费',
        area: ['540px', '320px'],
        content: '/telcertify/toRenewPage/'+id
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
            if(!isTel(phones)){
                if(!isPhone(phones)){
                    if(!is_Phone(phones)){
                        $(document.getElementsByClassName("numlists")[i]).focus();
                        layer.msg('号码"' + phones + '"不正确!');
                        break;
                    }
                }
            }
            AjaxPost("/telcertify/verificationChildNum", {
                "phoneNum": phones,
            }, function (result) {
                if (result.code == 500) {
                    layer.msg(result.msg);
                } else {
                    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
                        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
                            var phones = checkData[i];
                            if(!isTel(phones)){
                                if(!isPhone(phones)){
                                    if(!is_Phone(phones)){
                                        $(document.getElementsByClassName("numlists")[i]).focus();
                                        layer.msg('号码"' + phones + '"不正确!');
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // layer.msg(result.msg);
                }
            });
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
//我的订单查看详情
function ckeckDetails(id) {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetailsPage/'+id,
        area: ['500px', '680px']
    });
}