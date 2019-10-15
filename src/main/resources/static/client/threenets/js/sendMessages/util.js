;
var util = (function (global) {
    /**
     * 动态加载js文件
     * @param {String} url js文件的url
     */
    function loadJs(url,successCb) {
        var scriptElem = document.createElement("script");
        scriptElem.src = url;

        if(successCb){
            scriptElem.onload=function () {
                successCb();
            }
        }

        document.getElementsByTagName("body")[0].appendChild(scriptElem);

    }

    /**
     * 判断客户端系统类型
     * @return {Number} 1-Android 2-iOS -1-非Android非iOS，目前不关心Android和iOS之外的App类型
     *
     */
    function getAppSysType() {
        var ua = navigator.userAgent.toLowerCase();
        if (/iphone|ipad|ipod/.test(ua)) {
            return 2;
        }
        if (/android/.test(ua)) {
            return 1;
        }
        return -1;
    }

    /**
     * 向指定对象添加方法，支持命名空间
     * @param obj   被添加方法的对象
     * @param namespace 命名空间，格式为xx.xx.xx
     * @param fun   方法体
     */
    function registerFun(obj, namespace, fun) {
        if (!obj) {
            console.error('obj is undefined in function registerFun');
            return;
        }
        var nsArray = namespace.split('.');
        var funObj = obj;
        var ns = null;
        while (ns = nsArray.shift()) {
            if (nsArray.length) {
                if (funObj[ns] === undefined) {
                    funObj[ns] = {};
                }
                funObj = funObj[ns];
            } else {
                if (funObj[ns] === undefined) {
                    try {
                        funObj[ns] = fun;
                    } catch (ex) {
                        console.error('register fun fail');
                    }
                }
            }
        }
    }

    /**
     * 从url中查询指定name的参数
     * @param name
     * @return {String} url中匹配name的值部分
     */
    function getQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURI(r[2]);
        }
        return null;
    }
    
    function browseFromMobile() {
        var sUserAgent = navigator.userAgent.toLowerCase();
        var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
        var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
        var bIsMidp = sUserAgent.match(/midp/i) == "midp";
        var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
        var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
        var bIsAndroid = sUserAgent.match(/android/i) == "android";
        var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
        var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";

        return bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM;
    }

    /**
     * 手机号码格式验证
     * @param  {String} phone 手机号码
     * @return {Boolean}       验证结果
     */
    function validatePhoneFormat(phone) {
        if (!phone || phone == '') {
            return false;
        }
        var _bizParam = (typeof bizParam == 'undefined') ? {} : bizParam;
        var reg = new RegExp((_bizParam.phoneReg ||  '^1(3[0-9]|4[1245679]|5[0-9]|7[0135678]|8[0-9])[0-9]{8}$'), 'gi');
        if (reg.test(phone)) {
            return true;
        }
        return false;
    }

    /**
     * 验证短信码验证
     * @param  {String} code 短信验证码
     * @return {Boolean}      验证结果
     */
    function validateSmsFormat(code) {
        if (!code || code == '') {
            return false;
        }

        var reg = new RegExp('^\\d{4,6}$', 'gi');
        if (reg.test(code)) {
            return true;
        }
        return false;
    }

    return {
        loadJs: loadJs,
        getAppSysType: getAppSysType,
        registerFun: registerFun,
        getQueryString:getQueryString,
        browseFromMobile:browseFromMobile,
        validatePhoneFormat: validatePhoneFormat,
        validateSmsFormat: validateSmsFormat
    }


})(window)
