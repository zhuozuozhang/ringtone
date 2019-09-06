//获取号码认证子订单列表
$(document).ready(function(){
    showTelCerMemberTable();
});

var sendPrice = null;
var parentId = null;
function showTelCerMemberTable() {
    var param = {
        "parentId": $("#id").val(),
        "phoneNum": $("#phoneNum").val()
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
    var columnDefs = [
    //     {
    //     targets:[2],
    //     render: function(data, type, row, meta){
    //         var serviceStr = $.parseJSON(data);
    //         var service = serviceStr.service;
    //         var str = "";
    //         for (var i = 0; i < service.length; i++) {
    //             str += "<tr>";
    //             str += "<td>" + service[i].name + ",</td>";
    //             str += "<td>" + service[i].period0fValidity + ",</td>";
    //             str += "<td>" + service[i].cost + "元</td></br>";
    //             str += "</tr>";
    //         }
    //         return str;
    //     }
    // },
        {
        targets:[3],
        render: function(data, type, row, meta){
            sendPrice = data;
            parentId = row.parentOrderId;
            return "<div>"+data+"</div>"
        }
    },{
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

    var phoneNum = param.phoneNum.trim();
    if(phoneNum != null && phoneNum != ""){
        if(!isTel(phoneNum)){
            if(!isPhone(phoneNum)){
                if(!is_Phone(phoneNum)){
                    layer.msg("请输入正确的手机号或者座机号，座机号请务必添加区号！",{icon: 5, time: 3000});
                }
            }
        }
    }

    var url = "/telcertify/getTelCerMembersList";
    page("#members", 15, param, url, columns, columnDefs);
}

function addMember(){

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
            //发异步，把数据提交给后台
            layer.alert("续费成功", {icon: 6},function () {
                // 获得frame索引
                var index = parent.layer.getFrameIndex(window.name);
                //关闭当前frame
                parent.layer.close(index);
            });
        }else{
            layer.msg(res.msg,{icon: 5, time: 3000});
            // layer.msg("续费失败！", {icon: 5, time: 1000});
        }
    });

}

var falseNum = 0;
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

            alert("phones "+phones);
            if (!phoneregex.test(phones)) {
                if (!tel_regex.test(phones)) {
                    if(!telregex.test(phones)){
                        layer.msg('号码"' + phones + '"不正确!');
                        falseNum++;
                        break;
                    }
                }
            }
        }
    }
    if(checkData.length == 0){
        $("#allPrice").html(0);
    }else{
        $("#allPrice").html(checkData.length*sendPrice);
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
            return;
        }
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value)
        }
    }
    // if(falseNum == 0){
    //     $("#num").append(
    //         '<div class="layui-form-item" style="position: relative;"><div class="layui-input-block" style="margin-left: 156px;"><input type="text" style="width:81%;" name="content" class="layui-input numlists" value="" placeholder="如果是座机号码务必加上区号" autocomplete="off" onblur="checkNum(this)"></div><img src="../../client/telcertification/images/del.png" alt="" class="add_phone" onclick="delLandline(this);" width="28px"></div>'
    //     );
    // }else{
    //     layer.msg("请修改错误号码");
    // }

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
    alert(checkData);

    AjaxPost("/telcertify/insertTelCerChild",{
        phoneNumberArray:JSON.stringify(checkData),
        parentOrderId:parentId
    },function (res) {
        if (res.code == 200 && res.data){
            //发异步，把数据提交给后台
            layer.alert("批量添加号码成功", {icon: 6,time:3000},
                // 获得frame索引
                // var index = parent.layer.getFrameIndex(window.name);
                //关闭当前frame
                // parent.layer.close(index);
            );
            let index = parent.layer.getFrameIndex(window.name); //获取窗口索引
            parent.layer.close(index);
            window.parent.location.reload();//刷新父页面
            return false;
        }else{
            layer.msg("批量添加号码失败！", {icon: 5, time: 3000});
        }
    });

}


// layui.use('form',function () {
//     var form = layui.form;
//     //添加号码
//     form.on('submit(formDemo)',function () {
//         var checkData = [];
//         for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
//             if (document.getElementsByClassName('numlists')[i].value.length == 0) {
//                 layui.use('layer', function () {
//                     layer.msg("请输入号码！");
//                 });
//                 return;
//             }
//             if (document.getElementsByClassName('numlists')[i].value.length != 0) {
//                 checkData.push(document.getElementsByClassName('numlists')[i].value)
//             }
//         }
//         alert("lala");
//         alert("checkData");
//         $("#Form").submit();
//         // let index = parent.layer.getFrameIndex(window.name); //获取窗口索引
//         // parent.layer.close(index);
//         // window.parent.location.reload();//刷新父页面
//         // return false;
//     });
//
// });
