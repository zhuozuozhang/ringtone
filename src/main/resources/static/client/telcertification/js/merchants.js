$(document).ready(function () {
    showTelCerTable();
});

//tab切换
//我的订单

function telCerTable() {
    showTelCerTable();
    $('#myorder').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "block";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}
//即将到期号码
function fallDueTable() {
    showFallDueTable();
    $('#myorder2').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "block";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}

//到期号码
function dueTable() {
    showDueTable();
    $('#myorder3').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "block";
}
// 获得订单列表-->我的订单
function showTelCerTable() {
    var param = {
        "id": $("#id").val(),
        "rangetime" : $("#rangetime").val(),
        "telCompanyName": $("#companyName").val().trim(),
        "telLinkPhone" : $("#tel").val().trim(),
        "phoneNum" : $("#membernum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telCompanyName"},
        {"data": "telContent"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "memberNum"},
        // {"data": "statusName"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "productName"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[8],
        render: function (data, type, row, meta) {
            var productName = row.productName;
            var product = $.parseJSON(productName);
            var service = product.service;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].periodOfValidity + ",</td>";
                str += "<td>" + service[i].cost + "元</td></br>";
                str += "</tr>";
            }
            return str;
        }
    }, {
        targets:[10],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<i class='layui-icon layui-icon-edit' title='编辑' onclick='editTelCerOrder("+id+");'></i>"
                + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetails("+id+");'></i>"
                + "<a href='/telcertify/toTelMembersPage/"+id+"'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>";
        }
    }];
    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum)){
            if(!isPhone(param.phoneNum)){
                if(!is_Phone(param.phoneNum)){
                    layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    if(param.telLinkPhone != "" && param.telLinkPhone != null){
        if(!isTel(param.telLinkPhone)){
            if(!isPhone(param.telLinkPhone)){
                if(!is_Phone(param.telLinkPhone)){
                    layer.msg("请输入正确的商户联系电话！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    page("#merchants", 10, param, "/telcertify/getTelCerOrderList", columns, columnDefs);
}
// 获取订单列表-->即将到期列表
function showFallDueTable() {
    var param = {
        "phoneNum": $("#number").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        {"data": "years"},
        {"data": "price"},
        {"data": "statusName"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderExpireTime"}
    ];
    var columnDefs = [{
        targets: [5],
        render: function (data, type, row, meta) {
            if (data != null && data != "") {
                return data;
            } else {
                return "<div>暂无<div/>";
            }
        }
    },{
        targets:[8],
        render: function (data, type, row, meta) {
            var phoneNumber = row.telChildOrderPhone;
            var id = row.id;
            return "<p><i class='layui-icon layui-icon-log' title='查看详情' onclick='ckeckDetailsOne("+id+");'></i></p>";
        }
    }];
    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum)){
            if(!isPhone(param.phoneNum)){
                if(!is_Phone(param.phoneNum)){
                    layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    page("#fall_due_table", 10, param, "/telcertify/getFallDueList", columns, columnDefs);
}

//获取订单列表-->已经到期列表
function showDueTable() {
    var param = {
        "phoneNum": $("#number2").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        {"data": "years"},
        {"data": "price"},
        {"data": "statusName"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderExpireTime"}
        // {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [5],
        render: function (data, type, row, meta) {
            if (data != null && data != "") {
                return data;
            } else {
                return "<span>暂无</span>";
            }
        }
    },{
        targets:[8],
        render: function (data, type, row, meta) {
            var phoneNumber = row.telChildOrderPhone;
            var id = row.id;
            return "<p><i class='layui-icon layui-icon-log' title='查看详情' onclick='ckeckDetailsOne("+id+");'></i></p>";
        }
    }];
    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum)){
            if(!isPhone(param.phoneNum)){
                if(!is_Phone(param.phoneNum)){
                    $("#membernum").val(null).focus();
                    layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    page("#due_table", 10, param, "/telcertify/getDueList", columns, columnDefs);
}

//验证集团名称，不能为重复的
function verificationName() {
    var name = $("#telCompanyNameAdd").val();
    if (name == '' || name == null) {
        $("#telCompanyNameAdd").focus();
        layer.msg("集团名称不能为空！");
        return
    }
    AjaxPost("/telcertify/verificationName", {
        "telCompanyName": name,
    }, function (result) {
        if (result.code == 500) {
            $("#telCompanyNameAdd").focus();
            layer.msg(result.msg);
        } else {
            layer.msg(result.msg);
        }
    });
}
//修改订单中的验证集团号码
function verificationNameEdit() {
    var name = $("#telCompanyName").val();
    if (name == '' || name == null) {
        $("#telCompanyName").focus();
        layer.msg("集团名称不能为空！");
        return
    }
    AjaxPost("/telcertify/verificationName", {
        "telCompanyName": name,
    }, function (result) {
        if (result.code == 500) {
            $("#telCompanyName").focus();
            layer.msg(result.msg);
        } else {
            layer.msg(result.msg);
        }
    });
}

//验证联系人电话
function vertifyTelLinkPhone() {
    var phones = $("#telLinkPhoneAdd").val();
    var phoneregex = /^[1][3,4,5,7,8,9][0-9]{9}$/; //手机号码
    var tel_regex = /^(\d{3,4}\-)?\d{7,8}$/i;   //座机格式是 010-98909899 010-86551122
    var telregex = /^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/; //没有中间那段 -的 座机格式是 01098909899

    if (!phoneregex.test(phones)) {
        if (!tel_regex.test(phones)) {
            if(!telregex.test(phones)){
                $("#telLinkPhoneAdd").focus();
                layer.msg('联系人号码"' + phones + '"不正确!');
            }
        }
    }
}

function vertifyTelLinkPhoneEdit() {
    var phones = $("#telLinkPhone").val();
    var phoneregex = /^[1][3,4,5,7,8,9][0-9]{9}$/; //手机号码
    var tel_regex = /^(\d{3,4}\-)?\d{7,8}$/i;   //座机格式是 010-98909899 010-86551122
    var telregex = /^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/; //没有中间那段 -的 座机格式是 01098909899

    if (!phoneregex.test(phones)) {
        if (!tel_regex.test(phones)) {
            if(!telregex.test(phones)){
                $("#telLinkPhone").focus();
                layer.msg('联系人号码"' + phones + '"不正确!');
            }
        }
    }
}
var price = 0;

//日期时间范围
layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#rangetime',
        range: true
    });
})

//我的订单查看详情
function ckeckDetails(id) {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetailsPage/'+id,
        area: ['500px', '680px']
    });
}

//即将到期和到期号码查看详情
function ckeckDetailsOne(id) {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetailsOnePage/'+id,
        area: ['500px', '680px']
    });
}

// ---------------------------------------------------修改弹窗--回显-----------------------------------------------------
var sendId = null;
//打开修改订单弹窗--回显
function editTelCerOrder(id) {
    sendId = id;
    AjaxPost("/telcertify/toTelEditPage",{
        id:id
    },function (res) {
        if (res.code = 200 && res.data) {
            var telCerOrder = res.data;
            $("#telCompanyName").val(telCerOrder.telCompanyName);
            $("#telLinkName").val(telCerOrder.telLinkName);
            $("#telLinkPhone").val(telCerOrder.telLinkPhone);
            $("#telContent").val(telCerOrder.telContent);
            // $("#businessLicense").val(telCerOrder.businessLicense);
            // $("#legalPersonCardZhen").val(telCerOrder.legalPersonCardZhen);
            // $("#").val(telCerOrder.);
            // $("#").val(telCerOrder.);
            // $("#").val(telCerOrder.);
            // $("#").val(telCerOrder.);
            $("#remark").val(telCerOrder.remark);
        }else{
            layer.msg("弹出修改弹窗失败！", {icon: 5, time: 3000});
        }
    });
    $("#editor").removeClass("display");
}


// --------------------------------------------------------计算---------------------------------------------------------
var teddyPriceOut = 0;

$(function () {
    //多选
    $("	.btn-calculate").click(function () {
        if ($(this).hasClass("active")) {
            $(this).removeClass("active");
        } else {
            $(this).addClass("active");
        }
    });
    //年份单选
    $(".year .btn-calculate").click(function () {
        if($(".year .btn-calculate").hasClass("active")){
            if($(this).val() == 1){
                $(".dhbyear").find('button[value^="1"]').addClass("active");
                $(".dhbyear").find('button[value^="2"]').removeClass("active");
                $(".dhbyear").find('button[value^="3"]').removeClass("active");
                $(".dhbyear").find('button[value^="4"]').removeClass("active");
                $(".dhbyear").find('button[value^="5"]').removeClass("active");
            }
            if($(this).val() == 2){
                $(".dhbyear").find('button[value^="2"]').addClass("active");
                $(".dhbyear").find('button[value^="1"]').removeClass("active");
                $(".dhbyear").find('button[value^="3"]').removeClass("active");
                $(".dhbyear").find('button[value^="4"]').removeClass("active");
                $(".dhbyear").find('button[value^="5"]').removeClass("active");
            }
            if($(this).val() == 3){
                $(".dhbyear").find('button[value^="3"]').addClass("active");
                $(".dhbyear").find('button[value^="2"]').removeClass("active");
                $(".dhbyear").find('button[value^="1"]').removeClass("active");
                $(".dhbyear").find('button[value^="4"]').removeClass("active");
                $(".dhbyear").find('button[value^="5"]').removeClass("active");
            }
            if($(this).val() == 4){
                $(".dhbyear").find('button[value^="4"]').addClass("active");
                $(".dhbyear").find('button[value^="2"]').removeClass("active");
                $(".dhbyear").find('button[value^="3"]').removeClass("active");
                $(".dhbyear").find('button[value^="1"]').removeClass("active");
                $(".dhbyear").find('button[value^="5"]').removeClass("active");
            }
            if($(this).val() == 5){
                $(".dhbyear").find('button[value^="5"]').addClass("active");
                $(".dhbyear").find('button[value^="2"]').removeClass("active");
                $(".dhbyear").find('button[value^="3"]').removeClass("active");
                $(".dhbyear").find('button[value^="4"]').removeClass("active");
                $(".dhbyear").find('button[value^="1"]').removeClass("active");
            }
        }
        $(".year .btn-calculate").removeClass("active");
        $(this).addClass("active");
    });

    //电话邦年份单选
    $(".dhbyear	.btn-calculate").click(function () {
        if($(".year .btn-calculate").hasClass("active")) {
            if ($(this).val() == 1) {
                $(".year").find('button[value^="1"]').addClass("active");
                $(".year").find('button[value^="2"]').removeClass("active");
                $(".year").find('button[value^="3"]').removeClass("active");
                $(".year").find('button[value^="4"]').removeClass("active");
                $(".year").find('button[value^="5"]').removeClass("active");
            }
            if ($(this).val() == 2) {
                $(".year").find('button[value^="2"]').addClass("active");
                $(".year").find('button[value^="1"]').removeClass("active");
                $(".year").find('button[value^="3"]').removeClass("active");
                $(".year").find('button[value^="4"]').removeClass("active");
                $(".year").find('button[value^="5"]').removeClass("active");
            }
            if ($(this).val() == 3) {
                $(".year").find('button[value^="3"]').addClass("active");
                $(".year").find('button[value^="2"]').removeClass("active");
                $(".year").find('button[value^="1"]').removeClass("active");
                $(".year").find('button[value^="4"]').removeClass("active");
                $(".year").find('button[value^="5"]').removeClass("active");
            }
            if ($(this).val() == 4) {
                $(".year").find('button[value^="4"]').addClass("active");
                $(".year").find('button[value^="2"]').removeClass("active");
                $(".year").find('button[value^="3"]').removeClass("active");
                $(".year").find('button[value^="1"]').removeClass("active");
                $(".year").find('button[value^="5"]').removeClass("active");
            }
            if ($(this).val() == 5) {
                $(".year").find('button[value^="5"]').addClass("active");
                $(".year").find('button[value^="2"]').removeClass("active");
                $(".year").find('button[value^="3"]').removeClass("active");
                $(".year").find('button[value^="4"]').removeClass("active");
                $(".year").find('button[value^="1"]').removeClass("active");
            }
        }
        $(".dhbyear .btn-calculate").removeClass("active");
        $(this).addClass("active");
    });
    //短信单选
    $(".note    .btn-calculate").click(function () {
        $(".note .btn-calculate").removeClass("active");
        $(this).addClass("active");
    });
    //彩印单选
    $(".permanent	.btn-calculate").click(function () {
        $(".permanent .btn-calculate").removeClass("active");
        $(this).addClass("active");
    });
    //泰迪熊选项显隐
    $("#taidix").click(function () {
        if ($("#taidix").hasClass("active")) {
            $("#tdxcon").removeClass("bxs");
        } else {
            $("#tdxcon").addClass("bxs");
        }
        ;
    });
    //电话邦选项显隐
    $("#dhb").click(function () {
        if ($("#dhb").hasClass("active")) {
            $("#dhbcon").removeClass("bxs");
        } else {
            $("#dhbcon").addClass("bxs");
        }
        ;
    });
    //彩印选项显隐
    $("#caiyin").click(function () {
        if ($("#caiyin").hasClass("active")) {
            $("#cycon").removeClass("bxs");
        } else {
            $("#cycon").addClass("bxs");
        }
    });
    //短信选项显隐
    $("#gjdx").click(function () {
        if ($("#gjdx").hasClass("active")) {
            $("#gjdxcon").removeClass("bxs");
        } else {
            $("#gjdxcon").addClass("bxs");
        }
    });
    //判断输入整百数
    $("#dxsrk").blur(function () {
        //判断输入是否为整百数
        var num = $(this).val();
        if (!/^\d+$/.test(num / 100)) {
            alert("输入金额必须为整百数！");
            $(this).val('');
            $("#dxnumber").html(0);
            return false;
        }
        $("#dxnumber").html(num / 0.1);
        setTotal();
    });
    //计算应付金额
    $(".btn-calculate").click(function () {
        setTotal();
    });

    function setTotal() {
        var total = 0;
        var year = $(".year	.btn-calculate.active").val();
        var dhbyear = $(".dhbyear	.btn-calculate.active").val();
        var teddyPrice = 0;
        var telBondPrice = 0;
        AjaxPost("/telcertify/getAllConfig",{},function (res) {
            if(res.code==200 && res.data){
                var config = res.data;
                var configJson = JSON.stringify(config);
                for (let i = 0; i < config.length; i++) {
                    if(config[i].type == 1){
                        teddyPrice = config[i].price;
                        teddyPriceOut = teddyPrice;
                    }
                    if(config[i].type == 2){
                        telBondPrice = config[i].price;
                    }
                }
                //计算泰迪熊金额
                if ($("#taidix").hasClass("active")) {
                    total += Number(year) * teddyPrice;
                }
                //计算电话邦金额
                if ($("#dhb").hasClass("active")) {
                    total += Number(dhbyear) * telBondPrice;
                }
                //计算挂机短信金额
                if ($("#gjdx").hasClass("active")) {
                    total += Number($("#dxsrk").val());
                }
                price = total;
                $("#yfje").html(total);
            }else {
                layer.msg("未获取到", {icon: 5, time: 3000});
            }
        });
        jiSuan();
    }
});

function jiSuan() {
    var num = 1;
    $(".opennum").each(function () {
        if ($(this).val() != "") {
            num++;
        }
    });
    if (num > 1) {
        $("#yfje").html(price * (num - 1));
    }
}

//填写信息弹窗
$('#cause').on('click', function () { //点击按钮显示弹窗
    $("#pop").removeClass("display")
    $("#orderamount").html(price);
})
$("#cancel").on('click', function () { //点击取消隐藏弹窗
    $("#pop").addClass("display")
})
//修改信息
$('.openeditor').on('click', function () { //点击按钮显示弹窗
    $("#editor").removeClass("display")
})
$("#gbxg").on('click', function () { //点击取消隐藏弹窗
    $("#editor").addClass("display")
})
//查看实例弹窗
$('.opencase').on('click', function () { //点击按钮显示弹窗
    $("#case").removeClass("display")
})
$("#closecase").on('click', function () { //点击取消隐藏弹窗
    $("#case").addClass("display")
})
//查看订购协议
$('.openagreement').on('click', function () { //点击按钮显示弹窗
    $("#agreement").removeClass("display")
})
$("#closeagreement").on('click', function () { //点击取消隐藏弹窗
    $("#agreement").addClass("display")
})

//----------------------------------------------成员号码的验证和批量添加---------------------------------------------------
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
        }
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
        '<div class="approvebk"><span><i>*</i></span>\n' +
        '<input type="text" name="phoneNum" class="layui-input numlists" value=""' +
        'placeholder="如果是座机号码务必加上区号" autocomplete="off" onblur="checkNum(this)">\n' +
        '<img style="display:block;float:left;width:27px;height:27px;margin-top:8px;margin-left:19px;" ' +
        'src="../../client/telcertification/images/del.png" alt="" onclick="delLandline(this);">\n' +
        '</div>'
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
}

// --------------------------------------------------------添加---------------------------------------------------------
//添加商户订单
function addTelCerMerchants(){
    if ($("#taidix").hasClass("active")) {
        var teddy = $("#taidix").val();
    }
    var year = $(".year .active").val();
    if ($("#dhb").hasClass("active")) {
        var dhb = $("#dhb").val();
    }
    var dhbyear = $(".dhbyear .active").val();
    if ($("#caiyin").hasClass("active")) {
        var caiyin = $("#caiyin").val();
    }
    if ($("#gjdx").hasClass("active")) {
        var gjdx = $("#gjdx").val();
    }
    var note = $(".note .active").val();
    var checkData = [];
    for (var i = 0; i < document.getElementsByClassName('numlists').length; i++) {
        if (document.getElementsByClassName('numlists')[i].value.length != 0) {
            checkData.push(document.getElementsByClassName('numlists')[i].value)
        }
    }
    if($("#telCompanyNameAdd").html() > 0 || $("#telCompanyNameAdd").val() == ""){
        $("#telCompanyNameAdd").focus();
        layer.msg("请填写集团名称");
        return;
    }
    if($("#telLinkNameAdd").html() > 0 || $("#telLinkNameAdd").val() == ""){
        $("#telLinkNameAdd").focus();
        layer.msg("请填写联系人姓名")
        return;
    }
    if($("#telLinkPhoneAdd").html() > 0 || $("#telLinkPhoneAdd").val() == ""){
        $("#telLinkPhoneAdd").focus();
        layer.msg("请填写联系人电话")
        return;
    }
    if($("#telContentAdd").html() > 0 || $("#telContentAdd").val() == ""){
        $("#telContentAdd").focus();
        layer.msg("请填写认证展示内容");
        return;
    }
    if($("#businessLicenseAdd").html() > 0 || $("#businessLicenseHidden").val() == ""){
        layer.msg("您需要上传营业执照")
        return;
    }
    if($("#legalPersonCardZhenAdd").html() > 0 || $("#legalPersonCardZhenHidden").val() == ""){
        layer.msg("您需要上传法人身份证正面");
        return;
    }
    if($("#legalPersonCardFanAdd").html() > 0 || $("#legalPersonCardFanHidden").val() == ""){
        layer.msg("您需要上传法人身份证反面");
        return;
    }
    if($("#logoAdd").html() > 0 || $("#logoHidden").val() == ""){
        layer.msg("您需要上传LOGO");
        return;
    }
    if($("#authorizationAdd").html() > 0 || $("#authorizationHidden").val() == ""){
        layer.msg("请上传授权书");
        return;
    }
    if($("#numberProveAdd").html() > 0 || $("#numberProveHidden").val() == ""){
        layer.msg("请上传号码证明");
        return;
    }
    AjaxPost("/telcertify/addTelCertifyOrder",{
        telCompanyName: $("#telCompanyNameAdd").val(),
        telLinkName : $("#telLinkNameAdd").val(),
        telLinkPhone : $("#telLinkPhoneAdd").val(),
        telContent : $("#telContentAdd").val(),
        remark : $("#remarkAdd").val(),

        businessLicense : $("#businessLicenseHidden").val(),
        legalPersonCardZhen : $("#legalPersonCardZhenHidden").val(),
        legalPersonCardFan : $("#legalPersonCardFanHidden").val(),
        logo : $("#logoHidden").val(),
        authorization : $("#authorizationHidden").val(),
        numberProve : $("#numberProveHidden").val(),

        teddy : teddy,
        year : year,
        telBond : dhb,
        telBondYear : dhbyear,
        colorPrint : caiyin,
        hangUpMessage : gjdx,
        //挂机短信条数
        itemPerMonth : note,
        //挂机短信费用
        hangUpMessagePrice : $("#dxsrk").val(),
        unitPrice : price,

        phoneNumberArray:JSON.stringify(checkData),
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
//上传文件
function uploadFile(url,fileId,folderName) {
    var layuiLoding = layer.load(0, { //icon支持传入0-2
        time:false,
        shade: [0.5, '#9c9c9c'], //0.5透明度的灰色背景
        content: '文件上传中...',
        success: function (layero) {
            layero.find('.layui-layer-content').css({
                'padding': '39px 10px',
                'width': '100px'
            });
        },
        error: function (data, status, e){
            layer.msg("上传失败！", {icon: 2, time: 3000});
        }
    });
    var businessLicense = $("#businessLicenseAdd").val();
    alert(businessLicense);
    var ext = businessLicense.substring(businessLicense.lastIndexOf('.')+1,businessLicense.length).toLowerCase();
    alert(ext);
    if(!(ext == 'jpg' || ext == 'jpeg' || ext == 'png' || ext == 'bmp' || ext == 'gif')){
        layer.msg('请上传图片文件');
        return;
    }

    // $.ajaxFileUpload({
    //         url: '/system/upload/'+url, //用于文件上传的服务器端请求地址
    //         type: 'post',
    //         data: {
    //             folderName:folderName
    //         },
    //         secureuri: false, //是否需要安全协议，一般设置为false
    //         fileElementId: fileId, //文件上传域的ID
    //         dataType:"text/html",
    //         success: function (data, status){
    //             var pre = data.split(">")[1];
    //             var aft = pre.split("<")[0];
    //             var json = $.parseJSON(aft);
    //             data = json.data;
    //             if (url === "businessLicense") {
    //                 $("#businessLicenseHidden").val(data)
    //             } else if (url === "legalPersonCardZhen") {
    //                 $("#legalPersonCardZhenHidden").val(data)
    //             } else if (url === "legalPersonCardFan") {
    //                 $("#legalPersonCardFanHidden").val(data)
    //             } else if (url === "logo") {
    //                 $("#logoHidden").val(data)
    //             } else if (url === "authorization") {
    //                 $("#authorizationHidden").val(data)
    //             } else if (url === "numberProve") {
    //                 $("#numberProveHidden").val(data)
    //             }
    //             layer.close(layuiLoding);
    //             layer.msg("上传成功！", {icon: 1, time: 3000});
    //         },
    //         error: function (data, status, e){
    //             layer.msg("上传失败！", {icon: 2, time: 3000});
    //         }
    //     }
    // );
    // return false;
}

//上传营业执照
$("body").on('change', '#businessLicenseAdd', function (e) {
    var telCompanyName = $("#telCompanyNameAdd").val();
    uploadFile("businessLicense","businessLicenseAdd",telCompanyName);
});
//上传法人身份证正面
$("body").on('change', '#legalPersonCardZhenAdd', function (e) {
    var telCompanyName = $("#telCompanyNameAdd").val();
    uploadFile("legalPersonCardZhen","legalPersonCardZhenAdd",telCompanyName)
});
//上传法人身份证反面
$("body").on('change', '#legalPersonCardFanAdd', function (e) {
    var telCompanyName = $("#telCompanyNameAdd").val();
    uploadFile("legalPersonCardFan","legalPersonCardFanAdd",telCompanyName)
});
//上传LOGO
$("body").on('change', '#logoAdd', function (e) {
    var telCompanyName = $("#telCompanyNameAdd").val();
    uploadFile("logo","logoAdd",telCompanyName)
});
//上传授权书
$('#authorizationAdd').on('change', function (e) {
    //判断有没有填写用户名
    //没有，给出提示信息 ，清空文件选择   给用户输入框聚焦 return false
    var telCompanyName = $("#telCompanyNameAdd").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyNameAdd").focus();
        $("#authorizationAdd").val("");
        layer.msg("请先输入集团名称！");
        return
    }
    uploadFile("authorization","authorizationAdd",$("#telCompanyNameAdd").val())
});
//上传号码证明
$("#numberProveAdd").on('change', function (e) {
    var telCompanyName = $("#telCompanyNameAdd").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyNameAdd").focus();
        $("#numberProveAdd").val("");
        layer.msg("请先输入集团名称！");
        return
    }
    uploadFile("numberProve","numberProveAdd",$("#telCompanyNameAdd").val())
});

<!--添加----上传图片文件 -->
$("body").on('click', '.tpimgbka', function () {
    var telCompanyName = $("#telCompanyNameAdd").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyNameAdd").focus();
        layer.msg("请先输入集团名称！");
    }else{
        var ts = $(this);
        $(this).next().click(); //隐藏了input:file样式后，点击头像就可以本地上传
        $(this).next().on("change", function () {
            if (this.files[0] != null && this.files[0] != "" && this.files[0] != undefined) {
                var objUrl = getObjectURL(this.files[0]); //获取图片的路径，该路径不是图片在本地的路径
                if (objUrl) {
                    ts.children().attr("src", objUrl); //将图片路径存入src中，显示出图片
                }
            } else {
                $(this).attr("src", "../image/photo_icon1.png");
                $(this).next().val("");
            }
        });
    }
});

/*上传图片预览*/
//建立一個可存取到該file的url
function getObjectURL(file) {
    if (file != null && file != "" && file != undefined) {
        var url = null;
        if (window.createObjectURL != undefined) { // basic
            url = window.createObjectURL(file);
        } else if (window.URL != undefined) { // mozilla(firefox)
            url = window.URL.createObjectURL(file);
        } else if (window.webkitURL != undefined) { // webkit or chrome
            url = window.webkitURL.createObjectURL(file);
        }
        return url;
    }
}

//限制上传文件的类型和大小
function validate_img(ele) {
    // 返回 KB，保留小数点后两位
    var file = ele.value;
        // var fileType = fileName.slice(fileName.lastIndexOf(".")+1).toLowerCase();
        // if(fileType != "jpg" && fileType != "jpeg" && fileType != "png" && fileType != "JPG" && fileType != "bmp"){
        //     layer.msg("图片类型必须是jpeg,jpg,png,bmp中的一种!");
        //     return ;
        // }
    //在弹出上传图片的框之间就会运行，所以不行
    if (file != null && file != '' && file != undefined) {
        if (!/.(jpg|jpeg|png|JPG|bmp)$/.test(file)) {
            api.toast({
                msg: '图片类型必须是jpeg,jpg,png,bmp中的一种!',
                duration: 3000,
                location: 'top'
            });
            return false;
        } else if (((ele.files[0].size).toFixed(2)) >= (4 * 1024 * 1024)) {
            //返回Byte(B),保留小数点后两位
            api.toast({
                msg: '请上传小于4M的图片!',
                duration: 5000,
                location: 'top'
            });
            return false;
        }
    }
}


// --------------------------------------------------------修改---------------------------------------------------------
<!--修改-----上传图片文件 -->
$("body").on('click', '.tpimgbkaedit', function () {
    var telCompanyName = $("#telCompanyName").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyName").focus();
        layer.msg("请先输入集团名称！");
    }else{
        var ts = $(this);
        $(this).next().click(); //隐藏了input:file样式后，点击头像就可以本地上传
        $(this).next().on("change", function () {
            if (this.files[0] != null && this.files[0] != "" && this.files[0] != undefined) {
                var objUrl = getObjectURL(this.files[0]); //获取图片的路径，该路径不是图片在本地的路径
                if (objUrl) {
                    ts.children().attr("src", objUrl); //将图片路径存入src中，显示出图片
                }
            } else {
                $(this).attr("src", "../image/photo_icon1.png");
                $(this).next().val("");
            }
        });
    }
});

//上传营业执照
$("body").on('change', '#businessLicense', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    uploadFile("businessLicense","businessLicense",telCompanyName);
});
//上传法人身份证正面
$("body").on('change', '#legalPersonCardZhen', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    uploadFile("legalPersonCardZhen","legalPersonCardZhen",telCompanyName)
});
//上传法人身份证反面
$("body").on('change', '#legalPersonCardFan', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    uploadFile("legalPersonCardFan","legalPersonCardFan",telCompanyName)
});
//上传LOGO
$("body").on('change', '#logo', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    uploadFile("logo","logo",telCompanyName)
});
//上传授权书
$('#authorization').on('change', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyName").focus();
        $("#authorization").val("");
        layer.msg("请先输入集团名称！");
        return
    }
    uploadFile("authorization","authorization",$("#telCompanyName").val())
});
//上传号码证明
$("#numberProve").on('change', function (e) {
    var telCompanyName = $("#telCompanyName").val();
    if(telCompanyName == "" || telCompanyName == null){
        $("#telCompanyName").focus();
        $("#numberProve").val("");
        layer.msg("请先输入集团名称！");
        return
    }
    uploadFile("numberProve","numberProve",$("#telCompanyName").val())
});

//修改商户信息
function determine() {
    if($("#telCompanyName").html() > 0 || $("#telCompanyName").val() == ""){
        $("#telCompanyName").focus();
        layer.msg("请填写集团名称");
        return;
    }
    if($("#telLinkName").html() > 0 || $("#telLinkName").val() == ""){
        $("#telLinkName").focus();
        layer.msg("请填写联系人姓名");
        return;
    }
    if($("#telLinkPhone").html() > 0 || $("#telLinkPhone").val() == ""){
        $("#telLinkPhone").focus();
        layer.msg("请填写联系人电话");
        return;
    }
    if($("#telContent").html() > 0 || $("#telContent").val() == ""){
        $("#telContent").focus();
        layer.msg("请填写认证展示内容");
        return;
    }
    if($("#businessLicense").html() > 0 || $("#businessLicenseHidden").val() == ""){
        layer.msg("您需要上传营业执照");
        return;
    }
    if($("#legalPersonCardZhen").html() > 0 || $("#legalPersonCardZhenHidden").val() == ""){
        layer.msg("您需要上传法人身份证正面");
        return;
    }
    if($("#legalPersonCardFan").html() > 0 || $("#legalPersonCardFanHidden").val() == ""){
        layer.msg("您需要上传法人身份证反面");
        return;
    }
    if($("#logo").html() > 0 || $("#logoHidden").val() == ""){
        layer.msg("您需要上传LOGO");
        return;
    }
    if($("#authorization").html() > 0 || $("#authorizationHidden").val() == ""){
        layer.msg("请上传授权书");
        return;
    }
    if($("#numberProve").html() > 0 || $("#numberProveHidden").val() == ""){
        layer.msg("请上传号码证明");
        return;
    }
    var url = "/telcertify/editTelCerOrderById";
    AjaxPost(url,{
        id : sendId,
        telCompanyName : $("#telCompanyName").val(),
        telLinkName : $("#telLinkName").val(),
        telLinkPhone : $("#telLinkPhone").val(),
        telContent : $("#telContent").val(),

        businessLicense : $("#businessLicenseHidden").val(),
        legalPersonCardZhen : $("#legalPersonCardZhenHidden").val(),
        legalPersonCardFan : $("#legalPersonCardFanHidden").val(),
        logo : $("#logoHidden").val(),
        authorization : $("#authorizationHidden").val(),
        numberProve : $("#numberProveHidden").val(),

        remark : $("#remark").val()
    },function (res) {
        if(res.code==200 && res.data){
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