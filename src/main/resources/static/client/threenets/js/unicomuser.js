//搜索
function getUnicomUserInfoByPhoneNumber() {
    var phoneNumber = $("#phoneNum").val().trim();
    if(isTel(phoneNumber)){
        var url = "/threenets/getUnicomUserInfoByPhoneNumber/" + phoneNumber;
        AjaxPut(url,{
            phoneNumber:phoneNumber
        },function (res) {
            if (res.code == 200 && res.data) {
                var data = res.data;
                if(data != null){
                    var silentMemberByMsisdn = $.parseJSON(data.silentMemberByMsisdn);
                    var systemLogListByMsisdn = $.parseJSON(data.systemLogListByMsisdn);
                    if(!silentMemberByMsisdn.success || !systemLogListByMsisdn.success){
                        layer.msg("都不成功",{icon: 5,time: 3000});
                        return;
                    }
                    var silentMemberByMsisdnData = silentMemberByMsisdn.data.data;
                    var systemLogListByMsisdnData = systemLogListByMsisdn.data.data;

                    if(silentMemberByMsisdnData != null && silentMemberByMsisdnData.length > 0){
                        for(var i = 0; i < silentMemberByMsisdnData.length; i++ ){
                            var str = "";
                            str += "<tr>";
                            str += "<td>"+silentMemberByMsisdnData[i].msisdn+"</td>";
                            str += "<td>"+silentMemberByMsisdnData[i].provinceName+"</td>";
                            str += "<td>"+formatUnixtimestamp(silentMemberByMsisdnData[i].ctime)+"</td>";
                            if(silentMemberByMsisdnData[i].monthStatus == 0){
                                str += "<td>已包月</td>";
                            }else{
                                str += "<td>未包月</td>";
                            }
                            if(silentMemberByMsisdnData[i].closeTime != null){
                                str += "<td>"+silentMemberByMsisdnData[i].closeTime+"</td>";
                            }else{
                                str += "<td>-</td>";
                            }
                            if(silentMemberByMsisdnData[i].isSilent){
                                str += "<td><a class='layui-icon layui-icon-delete' title='删除' href='javascript:deleteSilentMemberByMsisdn(\""+silentMemberByMsisdnData[i].msisdn+"\");'></a></td>";
                            }else{
                                str += "<td>-</td>";
                            }
                            str += "</tr>";
                        }
                        $(".userInfoTable").html(str);
                    }else{
                        if(silentMemberByMsisdn.recode == '500'){
                            layer.msg("内部错误",{icon:2, time:3000})
                        }else if(silentMemberByMsisdn.recode == '7777777'){
                            layer.msg(silentMemberByMsisdnData.message,{icon:2, time:3000})
                        }else{
                            layer.msg("抱歉,系统没有查询到用户当前信息",{icon:5, time:3000})
                        }
                    }

                    if(systemLogListByMsisdnData != null && systemLogListByMsisdnData.length > 0){
                        var str = "";
                        for(var i = 0; i < systemLogListByMsisdnData.length; i++ ){
                            str += "<tr>";
                            if(systemLogListByMsisdnData[i].managerName != null){
                                str += "<td>"+systemLogListByMsisdnData[i].managerName+"</td>";
                            }else{
                                str += "<td>-</td>";
                            }
                            str += "<td>"+formatUnixtimestamp(systemLogListByMsisdnData[i].ctime)+"</td>";
                            str += "<td>"+systemLogListByMsisdnData[i].msisdn+"</td>";
                            str += "<td>"+systemLogListByMsisdnData[i].resultDesc+"</td>";
                            str += "</tr>";
                        }
                        $(".tbody").html(str);
                    }else{
                        if(systemLogListByMsisdn.recode == '500'){
                            layer.msg("内部错误",{icon:2, time:3000})
                        }else if(systemLogListByMsisdn.recode == '7777777'){
                            layer.msg(systemLogListByMsisdn.message,{icon:2, time:3000})
                        }else{
                            layer.msg("抱歉,系统没有查询到用户操作记录",{icon:5, time:3000})
                        }
                    }
                }else {
                    layer.msg("data为null",{icon: 5,time: 3000});
                }
            }else{
                layer.msg("服务器异常",{icon: 5,time: 3000})
                // res.code==200&&res.data
            }
        });
    }else {
        layer.msg("请输入联通的手机号码！",{icon: 0, time: 3000});
        return;
    }
}

//重置搜索框
function resetPhoneNumberText() {
    $("#phoneNum").val("");
}

//删除联通用户
function deleteSilentMemberByMsisdn(msisdn) {
    var msisdn = $("#phoneNum").val().trim();
    if(isTel(msisdn)){
        var url = "/threenets/deleteSilentMemberByMsisdn/"+msisdn;
        AjaxPut(url,{
            msisdn:msisdn
        },function (res) {
            if (res.code == 200 && res.data) {
                getUnicomUserInfoByPhoneNumber(msisdn);
            }else{
                layer.msg(res.msg, {icon: 5, time: 3000});
            }
        });
    }else {
        layer.msg("请输入联通手机号",{icon: 0, time: 3000});
        return;
    }
}

//时间戳转换为yyyy-MM-dd HH:mm:ss格式的时间
function formatUnixtimestamp(unixtimestamp) {
    var date = new Date(unixtimestamp);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}