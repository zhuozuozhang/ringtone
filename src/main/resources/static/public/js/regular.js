/*
 *  js工具类
 *  @Author: 周传禹
 *  @QQ:1659055323
 *  @Copy：未经本人同意，禁止用于商业盈利，禁止转载
 *  @Date：2019-03-01
 */

/**
 * 验证checkBox 是否有选中项
 * @param name input checkBox元素name
 * @returns {boolean}
 */
function checkBoxIsSelected(name){
    var names = document.getElementsByName(name);
    var checkArr = new Array();
    for (var i = 0; i < names.length; i++){
        if (names[i].checked){
            checkArr.push($(names[i]).attr("data-phone"))
        }
    }
    return checkArr;
}


/**
 * 验证是否是正整数
 */
function isPositiveInteger(integer) {
    var reg = /^[1-9]\d*$/;
    return reg.test(integer);
}

/**
 * 验证电话号码格式
 */
function isTel(tel) {
    var reg = /^((0\d{2,3}-\d{7,8})|(1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}))$/;
    ///^(13[0-9]|14[0-9]|15[0-9]|16[0-9]|17[0-9]|18[0-9]|19[0-9])[0-9]{8}$/
    return reg.test(tel);
}
/**
 * 验证邮箱格式
 */
function isEmail(email) {
    var mailReg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
    return mailReg.test(email);
}

/**
 * 验证身份证号码格式
 */
function isIdentityNum(identityNum) {
    var reg = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
    return reg.test(identityNum);
}

/**
 * 验证社会组织机构代码格式
 */
function isSocialNum(socialNum) {
    var reg = /^[a-zA-Z0-9]{17}-[a-zA-Z0-9]$/;
    return reg.test(socialNum);
}

/**
 * 是否含有中文（也包含日文和韩文）
 */
function isChineseChar(str) {
    var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;
    return reg.test(str);
}

/**
 * 判读是否非空，非空返回true
 */
function isNotEmpty(obj) {
    if (obj != null && obj != "" && obj != undefined && obj != "null") {
        return true;
    }
    return false;
}

/**
 * 判读是否是整数，是整数返回true
 */
function isNotInteger(obj) {
    if (!isNaN(obj) && obj % 1 === 0) {
        return true;
    } else {
        return false;
    }
}

// 正浮点数
function isNotFloat(num) {
    var reg = /^(0|[1-9]+[0-9]*)(\.[0-9]{1,2})?$/;
    return reg.test(num);
}
/**
 * 判读是否超出所规定的字数限制
 */
function checknum(id, max) {
    var textDom = document.getElementById(id);
    var len = textDom.value.length;
    if (len > max) {
        textDom.value = textDom.value.substring(0, max);
        api.toast({
            msg: '最多可以输入' + max + "个字",
            duration: 2000,
            location: 'bottom'
        });
        return;
    }
    // document.getElementById("in").value="你还可以输入"+(nMax-len)+"个字";
}

// 图片显示破图时 设置默认图片路径
function onerrors(obj, item_default) {
    $api.attr(obj, 'src', $api.getStorage("URL") + item_default);
}

// 图文详情更换图片链接
function displayHtmlWithImageStream(bodyHtml) {
    var imgReg = /<img.*?(?:>|\/>)/gi;
    var arr = bodyHtml.match(imgReg);
    if (arr != null) {
        for (var i = 0; i < arr.length; i++) {
            var images = arr[i];
            var srcReg = /src=[\'\"]?([^\'\"]*)[\'\"]?/i;
            var src = images.match(srcReg);
            var url = src[1];
            bodyHtml = bodyHtml.replace(url, $api.getStorage("URL") + url)
        }
    }
    return bodyHtml;
}

// 判断图片类型
function checkFileExt(ext) {
    if (!ext.match(/.jpg|.jpeg|.png/i)) {
        return false;
    }
    return true;
}


