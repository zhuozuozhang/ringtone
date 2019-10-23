
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

// --------------------------------------------------------修改---------------------------------------------------------
<!--修改-----上传图片文件 -->
$("body").on('click', '#businessLicenseClassEdit', function () {
    valid($("#telCompanyName"),"businessLicense",$("#businessLicense"),"businessLicense", $(this));
});
$("body").on('click', '#legalPersonCardZhenClassEdit', function () {
    valid($("#telCompanyName"),"legalPersonCardZhen",$("#legalPersonCardZhen"),"legalPersonCardZhen",$(this));
});
$("body").on('click', '#legalPersonCardFanClassEdit', function () {
    valid($("#telCompanyName"),"legalPersonCardFan", $("#legalPersonCardFan"),"legalPersonCardFan",$(this));
});
$("body").on('click', '#logoClassEdit', function () {
    valid($("#telCompanyName"),"logo",$("#logo"),"logo",$(this));
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