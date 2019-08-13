//获取列表
function showTable() {
    var id = $("#id").val();
    var url = "/threenets/findCricleMsgList/"+id;
    var index = parent.layer.getFrameIndex(window.name);
    AjaxPost(url,{
        id:id
    },function (res) {
        if(res.code == 200 && res.data){
            var data = res.data;
            if(data != null){
                var cricle_msg_list = $.parseJSON(data.cricle_msg_list);
                var rows = cricle_msg_list.rows;
                if(rows != null && rows.length > 0){
                    var str = "";
                    for(var i = 0; i < rows.length; i++ ){
                        str += "<tr>";
                        if(rows[i].type == 1){
                            str += "<td>铃音分发成功</td>";
                        }else if(rows[i].type == 2){
                            str += "<td>用户回复确认短信</td>";
                        }else if(rows[i].type == 4){
                            str += "<td>开通业务失败</td>";
                        }else{
                            str += "<td>未知</td>";
                        }
                        if(rows[i].status == 1){
                            str += "<td>已处理</td>";
                        }else if(rows[i].status == 0){
                            str += "<td>未处理</td>";
                        }else{
                            str += "<td>未知</td>";
                        }
                        str += "<td>"+rows[i].remark+"</td>>";
                        str += "<td>"+rows[i].ctime+"</td>";
                        if(rows[i].type == 1){
                            str += "<td ><a target='_top' class='layui-icon layui-icon-notice' title='查看' href='/threenets/toMerchantsRingPage/"+id+"'></a></td>"; //加引号的参数
                        }else if(rows[i].type == 2 || rows[i].type == 4){
                            str += "<td ><a target='_top' class='layui-icon layui-icon-notice' title='查看' href='/threenets/toMerchantsPhonePage/"+id+"'></a></td>"; //加引号的参数
                        }else{
                            str += "<td>未知类型</td>";
                            // str += "<td ><a target='_top' class='layui-icon layui-icon-notice' title='查看' href='#'></a></td>"; //加引号的参数\
                        }
                        str += "</tr>";
                    }
                    $(".msg_list").html(str);
                }else{
                    if(cricle_msg_list.recode == '500'){
                        layer.msg("内部错误",{icon:2, time:3000})
                    }else if(cricle_msg_list.recode == '7777777'){
                        layer.msg(rows.message,{icon:2, time:3000})
                    }else{
                        layer.msg("抱歉,系统没有查询到商户当前的消息",{icon:5, time:3000})
                    }
                    // layer.msg("rows == null || rows.length <= 0");
                }
            }else{
                layer(res.msg,{icon:5,time:3000});
                // layer.msg("data==null");
            }
        }else{
            $(".msg_list").html("<td colspan='5' style='color:#F00;'>当前商户可能不是移动用户或者当前商户没有需要处理的信息</td>");

            // layer.msg("当前商户可能不是移动用户或者当前商户没有需要处理的信息",{icon:2, time:3000});
        }

    });
}