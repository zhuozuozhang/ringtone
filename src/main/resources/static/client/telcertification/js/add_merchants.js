

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
            // layer.msg(result.msg);
        }
    });
}


//验证联系人电话
function vertifyTelLinkPhone() {
    var phones = $("#telLinkPhoneAdd").val();
    var phoneregex = /^[1][3,4,5,7,8,9][0-9]{9}$/; //手机号码
    var tel_regex = /^(\d{3,4}\-)?\d{7,8}$/i;   //座机格式是 010-98909899 010-86551122
    var telregex = /^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/; //没有中间那段 -的 座机格式是 01098909899


    AjaxPost("/telcertify/verificationTelLinkPhone", {
        "telLinkPhone": phones,
    }, function (result) {
        if (result.code == 500) {
            $("#telLinkPhoneAdd").focus();
            layer.msg(result.msg);
        } else {
            if (!phoneregex.test(phones)) {
                if (!tel_regex.test(phones)) {
                    if(!telregex.test(phones)){
                        $("#telLinkPhoneAdd").focus();
                        layer.msg('联系人号码"' + phones + '"不正确!');
                    }
                }
            }
        }
    });
}

var price = 0;
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
            // alert("输入金额必须为整百数！");
            layer.msg("输入金额必须为整百数！")
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
//验证成员号码是否正确，不能重复
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
        if($("#dxsrk").val() == ""){
            $("#dxsrk").focus();
            layer.msg("请填写挂机短信的金额");
            return;
        }
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
        layer.msg("请填写联系人姓名");
        return;
    }
    if($("#telLinkPhoneAdd").html() > 0 || $("#telLinkPhoneAdd").val() == ""){
        $("#telLinkPhoneAdd").focus();
        layer.msg("请填写联系人电话");
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


<!--添加----上传图片文件 -->
$("body").on('click', '#businessLicenseClass', function () {
    valid($("#telCompanyNameAdd"),"businessLicense",$("#businessLicenseAdd"),"businessLicenseAdd",$(this));
});
$("body").on('click', '#legalPersonCardZhenClass', function () {
    valid($("#telCompanyNameAdd"),"legalPersonCardZhen",$("#legalPersonCardZhenAdd"),"legalPersonCardZhenAdd",$(this));
});
$("body").on('click', '#legalPersonCardFanClass', function () {
    valid($("#telCompanyNameAdd"),"legalPersonCardFan",$("#legalPersonCardFanAdd"),"legalPersonCardFanAdd",$(this));
});
$("body").on('click', '#logoClass', function () {
    valid($("#telCompanyNameAdd"),"logo",$("#logoAdd"), "logoAdd",$(this));
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
        }
    });
    // alert("upload");
    $.ajaxFileUpload({
            url: '/system/upload/'+url, //用于文件上传的服务器端请求地址
            type: 'post',
            data: {
                folderName:folderName
            },
            secureuri: false, //是否需要安全协议，一般设置为false
            fileElementId: fileId, //文件上传域的ID
            dataType:"text/html",
            success: function (data, status){
                var pre = data.split(">")[1];
                var aft = pre.split("<")[0];
                var json = $.parseJSON(aft);
                data = json.data;
                if (url === "businessLicense") {
                    $("#businessLicenseHidden").val(data)
                } else if (url === "legalPersonCardZhen") {
                    $("#legalPersonCardZhenHidden").val(data)
                } else if (url === "legalPersonCardFan") {
                    $("#legalPersonCardFanHidden").val(data)
                } else if (url === "logo") {
                    $("#logoHidden").val(data)
                } else if (url === "authorization") {
                    $("#authorizationHidden").val(data)
                } else if (url === "numberProve") {
                    $("#numberProveHidden").val(data)
                }
                layer.close(layuiLoding);
                layer.msg("上传成功！", {icon: 1, time: 3000});
            },
            error: function (data, status, e){
                layer.msg("上传失败！", {icon: 2, time: 3000});
            }
        }
    );
    return false;
}



function valid(telCompanyNameId,url,pictureId,fileId,ts){
    var telCompanyNameVal = telCompanyNameId.val();
    if(telCompanyNameId.val() == "" || telCompanyNameId.val() == null){
        telCompanyNameId.focus();
        layer.msg("请先输入集团名称！");
    }else{
        ts.next().click(); //隐藏了input:file样式后，点击头像就可以本地上传
        ts.next().on("change", function () {
            var pictureVal = pictureId.val();
            if(pictureVal != "" && validate_img2(pictureVal,this.files)){
                if (this.files[0] != null && this.files[0] != "" && this.files[0] != undefined) {
                    var objUrl = getObjectURL(this.files[0]); //获取图片的路径，该路径不是图片在本地的路径
                    if (objUrl) {
                        ts.children().attr("src", objUrl); //将图片路径存入src中，显示出图片
                    }
                } else {
                    $(this).attr("src", "../image/photo_icon1.png");
                    $(this).next().val("");
                }
                uploadFile(url,fileId,telCompanyNameVal);
            }
        });
    }
}

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
function validate_img2(idVal,file) {
    var ext = idVal.substring(idVal.lastIndexOf('.')+1,idVal.length).toLowerCase();
    if(ext == 'jpg' || ext == 'jpeg' || ext == 'png' || ext == 'bmp' || ext == 'gif'){
        if(((file[0].size).toFixed(2)) <= (4 * 1024 * 1024)){
            return true;
        }else{
            layer.msg("请上传小于4M的图片!");
            return false;
        }
    } else{
        layer.msg('图片类型必须是jpeg,jpg,png,bmp,gif中的一种!');
        return false;
    }
}


