function showTelCerTable() {
    var param = {
        "id": $("#id").val(),
        "companyName": $("#companyName").val()
        // "name": $("#enterPriseName").val()
    }
    var columns = [
        {"data": null},
        {"data": "telCompanyName"},
        {"data": "telContent"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "productName"},
        {"data": null},
        {"data": "telOrderStatus"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[11],
        render: function (data, type, row, meta) {
            var id = row.id;
            var name = row.telCompanyName;
            return "<i class='layui-icon layui-icon-edit' title='编辑' onclick='edit();'></i>"
                + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetailsOne();'></i>"
                + "<a href='/telcertify/toTelMembersPage'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>";
            // return "<a href='/threenets/toMerchantsPhonePage/" + id + "'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
            //     + "<i class='layui-icon layui-icon-edit' title='编辑' onclick='EditMerchants(" + id + ",\"" + name + "\")'></i>"
            //     + "<i class='layui-icon layui-icon-delete' title='删除' onclick='DelMerchants(" + id + ");'></i>";
            //
            //     <i class="layui-icon layui-icon-edit" title="编辑" onclick="Edit();"></i>
            //     <i class="layui-icon layui-icon-log" title="详情" onclick="ckeckDetailsOne();"></i>
            //     <a href="/telcertify/toTelMembersPage"><i class="layui-icon layui-icon-username" title="成员管理"></i></a>
        }
    }];
    page("#merchants", 15, param, "/telcertify/getTelCerOrderList", columns, columnDefs);
}
function determine() {
    var url = "/telcertify/insertTelCertifyOrder";
    alert("提交");
    // AjaxPost(url,{
    //
    // },function (res) {
    //    if(res.code==200 && res.data){
    //        alert(1);
    //    }else {
    //        layer.msg(res.msg, {icon: 5, time: 3000});
    //    }
    // });
}

//tab切换
//我的订单
function myorder() {
    $('#myorder').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "block";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
}

//即将到期号码
function surplusOrder() {
    $('#myorder2').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "block";
}

//到期号码
function overOrder() {
    $('#myorder3').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "block";
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
function ckeckDetails() {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetails_onePage',
        area: ['500px', '680px']
    });
}

//即将到期号码查看详情
function ckeckDetailsOne() {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetails_onePage',
        area: ['500px', '680px']
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

//打开修改订单弹窗
function edit() {
    $("#editor").removeClass("display")
}


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
    $(".year	.btn-calculate").click(function () {
        $(".year .btn-calculate").removeClass("active");
        $(this).addClass("active");
    });
    //电话邦年份单选
    $(".dhbyear	.btn-calculate").click(function () {
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
        //计算泰迪熊金额
        if ($("#taidix").hasClass("active")) {
            total += Number(year) * 80;

        }
        //计算电话邦金额
        if ($("#dhb").hasClass("active")) {
            total += Number(dhbyear) * 80;
        }
        //计算挂机短信金额
        if ($("#gjdx").hasClass("active")) {
            total += Number($("#dxsrk").val());
        }
        price = total;
        $("#yfje").html(total);
        jiSuan();
    }


});

//号码添加按钮
function addtel(obj) {
    var err = 0;
    //判断座机格式的
    var partten = /^(\d{3,4}\-)?\d{7,8}$/i;   //座机格式是 010-98909899
    //var partten = /^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/; 没有中间那段 -的 座机格式是 01098909899
    var zuoji = partten.test($(obj).val());
    //判断手机格式可以用
    var re = /^1[35]\d{9}$/i;
    var shouji = re.test($(obj).val());
    $(".opennum").each(function () {
        if ($(obj).val() == "") {
            alert("请输入开通号码！");
            err++;
            //return false;
        }
        if ($(obj).val() != zuoji || shouji) {
            alert("你输入的电话号码有误！")
            $(obj).focus();
            return;
        }
    });
    if (err > 0) {
        return false;
    }
    //循环添加开通号码
    var str = '<div class="approvebk"><input class="opennum" type="tel" placeholder="如果是座机号码务必加上区号" style="margin-left:180px;" onblur="jiSuan()"><em onClick="removephone(this)"><img th:src="@{/client/telcertification/images/del.png}"></em></div>';
    $("#before").before(str);
}

//移除号码
function removephone(e) {
    $(e).parent().remove();
    var total = Number($("#yfje").html());
    var num = 0;
    $(".opennum").each(function () {
        if ($(this).val() != "") {
            num++;
        }
    });
    $("#yfje").html(price * num);
};
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
