
//搜索
/* 工具箱--》用户信息获取 */
function getUserInfoByRingMsisdn() {
    var ringMsisdn = $("#ringMsisdn").val().trim();

    if(isTel(ringMsisdn)){
        var url = "/threenets/getUserInfoByRingMsisdn/" + ringMsisdn;
        AjaxPut(url,{
            ringMsisdn : ringMsisdn
        },function (res) {
            if(res.code == 200 && res.data){
                var data = res.data;
                if(data.total == 0){
                    // $(".userInfoTable").html("<td colspan='8' style='color:#F00;'>抱歉,系统没有查询到数据</td>");
                    // $(".tbody").html("<tr><td colspan='3' style='color:#F00;'>抱歉,系统没有查询到数据</td></tr>");
                    layer.msg("抱歉,系统没有查询到数据",{icon:5,time:3000});
                    return;
                }
                var userInfo = $.parseJSON(data.userInfo);
                var userLog = userInfo.userLog;
                var userCurrentInfo = userInfo.userCurrentInfo;
                if(userLog.length == 0 && userCurrentInfo.length == 0){
                    $(".userInfoTable").html("");
                    $(".tbody").html("");
                    layer.msg("无数据",{icon: 2,time: 3000});
                    return;
                }
                if(userCurrentInfo.length == 0){
                    layer.msg("用户当前信息无数据",{icon: 2,time: 3000})
                }else{
                    var str = "";
                    str += "<tr>"
                    str += "<td>" + userCurrentInfo.group + "</td>";
                    str += "<td>" + userCurrentInfo.sa + "</td>";
                    str += "<td>" + userCurrentInfo.ctime + "</td>";
                    if(userCurrentInfo.closeTime == null){
                        str += "<td>未知</td>";
                    }else{
                        str += "<td>" + userCurrentInfo.closeTime + "</td>";
                    }
                    if (userCurrentInfo.payType == "GE") {
                        str += "<td>" + userCurrentInfo.price + "元个付</td>";
                    }
                    if (userCurrentInfo.applyForSmsNotification == 0) {
                        str += "<td>否</td>";
                    }
                    str += "<td>" + userCurrentInfo.userRingName + "</td>";
                    if (userCurrentInfo.freezeStatus == 0) {
                        str += "<td>正常</td>";
                    } else {
                        str += "<td>欠费</td>";
                    }
                    str += "</tr>";

                    $(".userInfoTable").html(str);
                }
                //用户操作记录
                if (userInfo.length == 0) {
                    layer.msg("用户操作记录列表无数据", {icon: 2, time: 3000});
                }else{
                    var str = "";
                    for (var i = 0; i < userLog.length; i++) {
                        str += "<tr>";
                        str += "<td>" + userLog[i].msisdn + "</td>";
                        str += "<td>" + userLog[i].content + "</td>";
                        str += "<td>" + userLog[i].ctime + "</td>";
                        str += "</tr>";
                    }
                    $(".tbody").html(str);
                }
            }else {
                layer(res.msg,{icon: 5,time: 3000});
            }
        });
    }else{
        layer.msg("请输入正确的移动手机号码！",{icon: 0, time: 3000});
        return;
    }
}

function resetRingMsisdnText() {
    $("#ringMsisdn").val("");
}
