function showTelCerMemberTable() {
    var param = {
        "parentId": $("#id").val(),
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
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
                return "<span>暂无</span>";
            }
        }
    }, {
        targets:[9],
        render: function (data, type, row, meta) {
            var id = row.id;
            var phoneNum = row.telChildOrderPhone;
            return "<a href='javascript:;'><i class='layui-icon layui-icon-rmb' title='续费' onclick='Renewal();'></i></a>"
                + "<a href='/telcertify/toTelCostPage/"+phoneNum+"'><i class='layui-icon layui-icon-form' title='费用支出记录'></i></a>"
        }
    }];
    var url = "/telcertify/getTelCerMembersList";
    page("#members", 2, param, url, columns, columnDefs);
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
function Renewal(){
    layer.open({
        type: 1,
        title: '续费',
        area: ['540px', '320px'],
        content: '<div><div><h4 class="h4s"><span>小米号码认证-小米（查看下方开通注意事项）</span></h4> <p class="zhifu">共需支付：<span>80</span><span>元</span></p></div> <div class="renew_div"><div><p>订购年份:</p> <div><a class="btnactive">1年</a></div></div><div style="margin-top: 0px;"><button onclick="confirmRenew()" class="btnactive">确认续费</button></div> <p>支付后，制作商会在2小时内主动联系您</p></div></div>'

    });
}
// 确认续费
function confirmRenew() {

}
//支付费用
function checkNum(obj) {
    //所输入的号码集合
    var checkData = [];
    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value);
        }
    }
    if(checkData.length == 0){
        $("#allPrice").html(80);
    }else{
        $("#allPrice").html(checkData.length*80);
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
