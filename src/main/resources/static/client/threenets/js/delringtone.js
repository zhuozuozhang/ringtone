//搜索
function findRingInfoByMsisdn() {
    var msisdn = $("#ringMsisdn").val().trim();
    if(isTel(msisdn)){

        var url = "/threenets/findRingInfoByMsisdn/" + msisdn;
        AjaxPut(url, {
            msisdn:msisdn
        }, function (res) {
            if (res.code == 200 && res.data) {
                var data = res.data;
                // layer.msg('该手机号的铃音信息已查到！', {icon: 6, time: 3000});
                // $("#set").DataTable().ajax.reload(msisdn, true);
                if(data != null){
                    var ringSettingListByMsisdn = $.parseJSON(data.ringSettingListByMsisdn);
                    var ringListByMsisdn = $.parseJSON(data.ringListByMsisdn);
                    if(!ringSettingListByMsisdn.success && !ringListByMsisdn.success){
                        layer.msg(ringSettingListByMsisdn.msg,{icon: 5,time: 3000});
                        return;
                    }
                    var ringSettingListByMsisdnRow = ringSettingListByMsisdn.rows;
                    var ringListByMsisdnRow = ringListByMsisdn.rows;
                    if(ringSettingListByMsisdnRow.length == 0 && ringListByMsisdnRow.length == 0){
                        $(".ringSettingListByMsisdnTbody").html("");
                        $(".ringListByMsisdnTbody").html("");
                        layer.msg("没有该用户的数据",{icon: 2,time: 3000});
                        return;
                    }
                    //个人铃音设置列表
                    if(ringSettingListByMsisdn.success){
                        if(ringSettingListByMsisdnRow.length == 0){
                            layer.msg("个人铃音设置列表无数据",{icon: 2,time: 3000});
                        }
                        var str = "";
                        for(var i = 0; i < ringSettingListByMsisdnRow.length; i++){
                            str += "<tr>";
                            str += "<td><input type='checkbox' name='check' value='{\"toneID\":\""+ringSettingListByMsisdnRow[i].toneID+"\",\"type\":\""+ringSettingListByMsisdnRow[i].type+"\",\"settingID\":\""+ringSettingListByMsisdnRow[i].settingID+"\"}'></td>";
                            str += "<td>"+ringSettingListByMsisdnRow[i].settingID+"</td>";
                            str += "<td>"+ringSettingListByMsisdnRow[i].toneID+"</td>";
                            str += "<td>"+ringSettingListByMsisdnRow[i].type+"</td>";

                            if(ringSettingListByMsisdnRow[i].timeType == 1){
                                str += "<td>每天时间段</td>";
                            }else{
                                str += "<td>全天播放</td>";
                            }

                            if(ringSettingListByMsisdnRow[i].loopType == 0){
                                str += "<td>顺序</td>";
                            }else{
                                str += "<td>随机</td>";
                            }

                            if(ringSettingListByMsisdnRow[i].startTime != null){
                                str += "<td>"+ringSettingListByMsisdnRow[i].startTime+"</td>";
                            }else{
                                str += "<td>-</td>";
                            }

                            if(ringSettingListByMsisdnRow[i].endTime != null){
                                str += "<td>"+ringSettingListByMsisdnRow[i].endTime+"</td>";
                            }else{
                                str += "<td>-</td>";
                            }
                            // str += "<td><a style='color: #000;' data-msg='12' href='javascript:singleDeleteRingSetting(\""+ringSettingListByMsisdnRow[i].settingID+"\",\""+ringSettingListByMsisdnRow[i].toneID+"\",\""+ringSettingListByMsisdnRow[i].type+"\");'>&nbsp;删除</a></td>";
                            str += "<td><a class='layui-icon layui-icon-delete' title='删除' href='javascript:singleDeleteRingSetting(\""+ringSettingListByMsisdnRow[i].settingID+"\",\""+ringSettingListByMsisdnRow[i].toneID+"\",\""+ringSettingListByMsisdnRow[i].type+"\");'></a></td>";
                            str += "</tr>";
                        }
                        $(".ringSettingListByMsisdnTbody").html(str);
                    }else{
                        layer.msg("获取个人铃音设置列表失败",{icon: 2, time: 3000});
                    }
                    //个人铃音库列表
                    if(ringListByMsisdn.success){
                        if(ringListByMsisdn.length == 0){
                            layer.msg("个人铃音库列表无数据",{icon: 2, time: 3000});
                        }
                        var str = "";
                        for(var i = 0; i < ringListByMsisdnRow.length; i++){
                            str += "<tr>";
                            str += "<td><input type='checkbox' name='percheck' value='"+ringListByMsisdnRow[i].id+"|"+ringListByMsisdnRow[i].type+"'></td>";
                            // str += "<td><input type='checkbox' name='perCheck' value='{\"id\":\""+ringListByMsisdnRow[i].id+"\",\"type\":\""+ringListByMsisdnRow[i].type+"\"}'></td>";
                            str += "<td>"+ringListByMsisdnRow[i].id+"</td>";
                            str += "<td>"+ringListByMsisdnRow[i].name+"</td>";
                            str += "<td>"+ringListByMsisdnRow[i].type+"</td>";
                            str += "<td>"+ringListByMsisdnRow[i].toneValidDay+"</td>";
                            if(ringListByMsisdnRow[i].status == 1){
                                str += "<td>正常</td>";
                            }else if(ringListByMsisdnRow[i].status == 2){
                                str += "<td>隐藏</td>";
                            }else{
                                str += "<td>-</td>";
                            }
                            str += "<td><a class='layui-icon layui-icon-delete' title='删除' href='javascript:singleDeleteRing(\""+ringListByMsisdnRow[i].id+"\",\""+ringListByMsisdnRow[i].type+"\");'></a></td>";
                            str += "</tr>";
                        }
                        $(".ringListByMsisdnTbody").html(str);
                    }else{
                        layer.msg("获取个人铃音库列表失败",{icom: 2, time: 3000});
                    }
                }else{
                    layer.msg("获取失败",{icom: 2, time: 3000});
                }
            } else {
                layer.msg(res.msg, {icon: 5, time: 3000});
            }
        });
    }else{
        layer.msg("请输入正确的移动手机号码！",{icon: 0, time: 3000});
        return;
    }

}

//删除某一行个人铃音设置
function singleDeleteRingSetting(settingID,toneID,type){
    if(confirm("删除该铃音设置会将相同设置ID的铃音设置一起删掉，确认删除？")){
        var msisdn = $("#ringMsisdn").val().trim();
        if(isTel(msisdn)){
            var url = "/threenets/singleDeleteRingSet/"+msisdn;
            AjaxPut(url,{
                msisdn : msisdn,
                settingID : settingID,
                toneID : toneID,
                type : type
            },function (res) {
                if (res.code == 200 && res.data) {
                    findRingInfoByMsisdn(msisdn);
                }else{
                    layer.msg(res.msg, {icon: 5, time: 3000});
                }
            });
            // formDialog("正在删除...");
            // $.post(url, {
            //     "msisdn" : msisdn,
            //     "settingID" : settingID,
            //     "toneID" : toneID,
            //     "type" : type
            // }, function(data, textstatus) {
            //     art.dialog({
            //         id : 'formDialog'
            //     }).close();
            //     if(data){
            //         deleteRingSeting();
            //     }else{
            //         tipDialog("删除失败！");
            //     }
            // }, "json");
        }
    }else {
        layer.msg("请输入正确的移动手机号",{icon: 0, time: 3000});
        return;
    }
}
//删除某一行个人铃音库
function singleDeleteRing(toneIds,type){
    if(confirm("确认删除该铃音？")){
        var msisdn = $("#ringMsisdn").val().trim();
        if(isTel(msisdn)){
            var url = "/threenets/singleDeleteRing/"+msisdn;
            AjaxPut(url,{
                msisdn:msisdn,
                toneIds : toneIds,
                type : type
            }, function (res) {
                if (res.code == 200 && res.data) {
                    findRingInfoByMsisdn(msisdn);
                }else{
                    layer.msg(res.msg, {icon: 5, time: 3000});
                }
            });
        }else {
            layer.msg("请输入正确的移动手机号",{icon: 0, time: 3000});
            return;
        }
    }
}
//批量删除个人铃音设置
function delRingSettingListByMsisdn(){
    if (confirm("删除选中的铃音设置会将相同设置ID的铃音设置一起删掉，确认删除？")) {
        var msisdn = $("#ringMsisdn").val().trim();
        if(isTel(msisdn)){
            var valArr = new Array;
            $(".ringSettingListByMsisdnTbody :checkbox[checked]").each(function (i) {
                valArr[i] = $(this).val();
            });
            var vals = valArr.join(',');
            if (vals == null || vals.length == 0) {
                layer.msg("请选择需要删除的铃音设置", {icon: 0, time: 3000});
                return;
            }
            var url = "/threenets/batchDeleteRingSet";
            AjaxPut(url, {
                msisdn: msisdn,
                vals: vals
            }, function (res) {
                if (res.code == 200 && res.data) {
                    findRingInfoByMsisdn(msisdn);
                } else {
                    layer.msg(res.msg, {icon: 5, time: 3000});
                }
            });
        }else {
            layer.msg("请输入正确的移动手机号", {icon: 0, time: 3000});
            return;
        }
    }
}
//批量删除个人铃音库列表
function delRingListByMsisdn(){
    if(confirm("确认删除选中的铃音?")){
        var msisdn = $("#ringMsisdn").val().trim();
        if(isTel(msisdn)){
            var valArr = new Array;
            $(".ringListByMsisdnTbody :checkbox[checked]").each(function(i){
                valArr[i] = $(this).val();
            });
            var vals = valArr.join(',');
            if(vals == null || vals.length == 0){
                layer.msg("请选择需要删除的铃音库",{icon: 0, time: 3000});
                return;
            }
            var url = "/threenets/batchDeleteRing";
            AjaxPut(url,{
                msisdn:msisdn,
                vals:vals
            },function (res) {
                if (res.code == 200 && res.data) {
                    findRingInfoByMsisdn(msisdn);
                }else{
                    layer.msg(res.msg, {icon: 5, time: 3000});
                }
            });
        }else {
            layer.msg("请输入正确的手机号",{icon: 0, time: 3000});
            return;
        }
    }
}

//个人铃音设置列表
$("#setCheck").click(function(){
    $("input[name='check']").prop("checked",this.checked);
})
$("input[name='check']").change(function(){
    if($("input[name='check']").not("input:checked").size() <= 0){
        $("#setCheck").prop("checked",true);
    }else{
        $("#setCheck").prop("checked",false);
    }
})
//个人铃音库列表
$("#perCheck").click(function(){
    $("input[name='percheck']").prop("checked",this.checked);
})
$("input[name='percheck']").change(function(){
    if($("input[name='percheck']").not("input:checked").size() <= 0){
        $("#perCheck").prop("checked",true);
    }else{
        $("#perCheck").prop("checked",false);
    }
})

function resetRingMsisdnText() {
    $("#ringMsisdn").val("");
}