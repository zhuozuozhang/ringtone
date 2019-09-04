$(document).ready(function () {
    showTelCerTable();
});

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
            var obj = JSON.stringify(service);
            var serviceJson = $.parseJSON(obj);

            var totalCost = product.totalCost;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].period0fValidity + ",</td>";
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
        if(!isTel(param.phoneNum.trim())){
            layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
            return;
        }
    }
    if(param.tel != "" && param.tel != null){
        if(!isTel(param.tel.trim())){
            layer.msg("请输入正确的商户联系电话！",{icon: 0, time: 3000});
            return;
        }
    }
    page("#merchants", 2, param, "/telcertify/getTelCerOrderList", columns, columnDefs);
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
    page("#fall_due_table", 10, param, "/telcertify/getFallDueList", columns, columnDefs);
}

//获取订单列表-->已经到期列表
function showDueTable() {
    var param = {
        "phoneNum": $("#number2").val()
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
    page("#due_table", 10, param, "/telcertify/getDueList", columns, columnDefs);
}
//
// function addTelCerMerchant() {
//     layer.open({
//         type: 2,
//         title: '业务订购',
//         area: ['790px', '850px'],
//         content: '/telcertify/toAddTelCerMerchantPage'
//     });
//修改商户信息
function determine() {
    var url = "/telcertify/editTelCerOrderById";
    alert(sendId);
    AjaxPost(url,{
        id:sendId,
        telCompanyName : $("#telCompanyName").val(),
        telLinkName : $("#telLinkName").val(),
        telLinkPhone : $("#telLinkPhone").val(),
        telContent : $("#telContent").val(),
    // businessLicense:$("#businessLicense").val(),
    // legalPersonCardZhen:
    // legalPersonCardFan:
    // logo:
    // authorization:
    // numberProve:
    // unitPrice:
    // userId:
    // telOrderStatus:
    // telOrderTime:
    // productName:
        remark:$("#remark").val()
    },function (res) {
       if(res.code==200 && res.data){
           alert(res.data);
       }else {
           layer.msg(res.msg, {icon: 5, time: 3000});
       }
    });
}

//tab切换
//我的订单

function myorder() {
    showTelCerTable();
    $('#myorder').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "block";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}
//即将到期号码
function surplusOrder() {
    showFallDueTable();
    $('#myorder2').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "block";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}

//到期号码
function overOrder() {
    showDueTable();
    $('#myorder3').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "block";
}

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

function addTelCerMerchants(){
    // $(".btn-calculate").click(function () {
    //    if($(this).hasClass("active")){
    //
    //    }
    // });
    if ($("#taidix").hasClass("active")) {
        var teddy = $("#taidix").val();
        alert(teddy);
    }
    if($(".year .btn-calculate").hasClass("active")){
        var year = $(".year .btn-calculate").val();
        alert(year);
    }
    if ($("#dhb").hasClass("active")) {
        var dhb = $("#dhb").val();
    }
    if($(".dhbyear .btn-calculate").hasClass("active")){
        var dhbyear = $(".dhbyear .btn-calculate").val();
        alert(dhbyear);
    }
    if ($("#caiyin").hasClass("active")) {
        var caiyin = $("#caiyin").val();
    }
    if ($("#gjdx").hasClass("active")) {
        var gjdx = $("#gjdx").val();
    }
    if($(".note .btn-calculate").hasClass("active")){
        var note = $(".note .btn-calculate").val();
        alert("note  "+note);
    }
    var dxsrk = $("#dxsrk").val();
    var businessLicense = $("businessLicenseAdd").val();
    alert(businessLicense);


        AjaxPost("/telcertify/addTelCertifyOrder",{
        telCompanyName: $("#telCompanyNameAdd").val(),
        telLinkName : $("#telLinkNameAdd").val(),
        telLinkPhone : $("#telLinkPhoneAdd").val(),
        telContent : $("#telContentAdd").val(),
        businessLicense : $("businessLicenseAdd").val(),
        remark : $("#remarkAdd").val(),

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
        unitPrice : price
    },function (res) {
        if(res.code==200 && res.data){
            alert(res.data);
        }else {
            layer.msg("失败", {icon: 5, time: 3000});
        }
    });
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
    if (checkData.length == 0) {
        $("#allPrice").html(80);
    } else {
        $("#allPrice").html(checkData.length * 80);
    }
}

var sendId = null;
//打开修改订单弹窗
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



var teddyPriceOut = 0;
var price = 80;
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
    $(".note	.btn-calculate").click(function () {
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
// }
//计算业务订购中支付金额
//$("body").on('blur','.opennum',function(){
//var num=0;
//$(".opennum").each(function() {
// if($(this).val()!=""){
//num++;
//}
//});
//console.log(num);
//console.log(price);
//$("#yfje").html(price*num);
//})
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

<!--上传图片 -->
$("body").on('click', '.tpimgbka', function () {
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
    // var fileName = document.getElementById("businessLicenseAdd").value;
    //     // var fileType = fileName.slice(fileName.lastIndexOf(".")+1).toLowerCase();
    //     // if(fileType != "jpg" && fileType != "jpeg" && fileType != "png" && fileType != "JPG" && fileType != "bmp"){
    //     //     layer.msg("图片类型必须是jpeg,jpg,png,bmp中的一种!");
    //     //     return ;
    //     // }
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

//上传营业执照
$("body").on('change', '#businessLicenseAdd', function (e) {
    uploadFile("businessLicense")
});
//上传法人身份证正面
//上传法人身份证反面
//上传LOGO
//上传授权书
//上传号码证明

//上传文件
function uploadFile(url) {
    alert("url "+url);
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
    // AjaxPost("/system/upload/"+url,{},function (res) {
    //     if (res.code == 200 && res.data){
    //         if (url === "businessLicense") {
    //             alert("查看营业执照的上传地址");
    //         }
    //     }else{
    //         layer.msg("上传文件失败！", {icon: 5, time: 3000});
    //     }
    // });
    $.ajax({
        url: '/system/upload/' + url,
        type: 'POST',
        cache: false,
        data: {businessLicense: $('#businessLicenseAdd').val()},
        processData: false,
        contentType: false
    }).done(function (res) {
        layer.close(layuiLoding);
        layer.msg("上传成功", {icon: 1, time: 6000});

    }).fail(function (res) {
        layer.msg(res.msg, {icon: 2, time: 6000});
    });
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
