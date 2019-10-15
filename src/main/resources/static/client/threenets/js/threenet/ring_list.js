$(function () {
    $(".index2").addClass("active");
    // 判断运营商显示状态
    var ringOperate = $("#ringOperate").val();
    if (ringOperate.indexOf("1") != -1) { // 移动
        $(".yidong").addClass("InternetOperators-clicked");
        $(".yidong").css("display", "block");
        $("#operate").val(1);
    }
    if (ringOperate.indexOf("2") != -1) { // 电信
        if (ringOperate.indexOf("1") == -1) {
            $(".dianxin").addClass("InternetOperators-clicked");
            $("#operate").val(2);
        }
        $(".dianxin").css("display", "block");
    }
    if (ringOperate.indexOf("3") != -1) { // 联通
        if (ringOperate.indexOf("1") == -1 && ringOperate.indexOf("2") == -1) {
            $(".liantong").addClass("InternetOperators-clicked");
            $("#operate").val(3);
        }
        $(".liantong").css("display", "block");
    }
});

function showTable() {
    var params = {
        "id": $('#orderId').val(),
        "operator": $('#operate').val(),
    }
    var columns = [
        {"data": "id"},
        {"data": "operate"},
        {"data": "createTime"},
        {"data": "ringName"},
        {"data": "ringType"},
        {"data": "fileUrl"},
        {"data": "ringStatus"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            if (data == 1) {
                return '移动'
            } else if (data == 2) {
                return '电信'
            } else {
                return '联通'
            }
        }
    }, {
        targets: [3],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data)?data:"";
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [5],
        render: function (data, type, row, meta) {
            var result = "<a><span onclick=\"openPlayer('"+data+"');\" style='color:#0569FD'>试听</span></a>";
            return result;
        }
    }, {
        targets: [6],
        render: function (data, type, row, meta) {
            if (data == 1) {
                return '待审核'
            } else if (data == 2) {
                return '激活中'
            } else if (data == 3) {
                return '激活成功'
            } else if (data == 4) {
                return '部分省份激活超时'
            } else if (data == 5) {
                return '部分省份激活成功'
            } else {
                return '激活失败'
            }
        }
    }, {
        targets: [7],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data)?data:"";
            return "<div style='width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [8],
        render: function (data, type, row, meta) {
            var id = row.id;
            var ringStatus = row.ringStatus;
            var operate = row.operate;
            var setRing = "";
            if(ringStatus == 3 || ringStatus == 4 || ringStatus == 5){
                setRing = "<a href='/threenets/toSetingRing/"+id+"/"+operate+"/" + $("#orderId").val() + "/" + $("#companyName").val()+ "'><i class='layui-icon layui-icon-set' title='设置铃音'></i></a>";
            }
            return '<i class="layui-icon" title="复制铃音" onclick="cloneRing(' + id + ')"><img src="../../client/threenets/images/copy.png" alt=""></i>' +
                '<a href="/threenets/downloadRing/' + id + '"><i class="layui-icon layui-icon-download-circle" title="下载铃音"></i></a>' +
                '<i class="layui-icon" title="查看广告词" onclick="advertising(\'' + row.ringContent + '\');"><img src="../../client/threenets/images/see.png" alt=""></i>' +
                setRing + '<i class="layui-icon layui-icon-delete" title="删除" onclick="deleteRing(' + id + ')"></i>';
        }
    }];
    page("#set", 15, params, "/threenets/getThreeNetsRingList", columns, columnDefs);
}

//添加铃音
function addRing() {
    var orderId = $("#orderId").val();
    var companyName = $("#companyName").val();
    layer.open({
        type: 2,
        title: '上传铃音',
        area: ['650px', '550px'],
        content: '/threenets/toAddMerchantsRingPage?orderId=' + orderId+"&companyName="+companyName
    });
}

//在线试听
function openPlayer(url) {
    layer.open({
        type: 1,
        title: '在线试听',
        area: ['800px', '560px'],
        content: '<video id="ovideo" autoplay loop src="' + url + '" controls="controls " allowfullscreen="true" quality="high" width="800px" height="514px" align="middle" allowscriptaccess="always" flashvars="isAutoPlay=true" type="application/x-shockwave-flash"></video>'
    });
}

//查看广告词
function advertising(content) {
    layer.open({
        title: '广告词',
        content: content
    });
}

//运营商切换
function dianxin(obj) {
    $(".more").removeClass("InternetOperators-clicked");
    $(obj).addClass("InternetOperators-clicked");
    var className = obj.className;
    if (className.indexOf('yidong') >= 0) {
        $("#operate").val(1)
    } else if (className.indexOf('liantong') >= 0) {
        $("#operate").val(3)
    } else {
        $("#operate").val(2)
    }
    showTable();
}

//克隆铃音
function cloneRing(id) {
    layer.confirm("你确定要克隆此条记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxPost("/threenets/cloneRing/" + id, {}, function (result) {
            layer.closeAll('dialog');//关闭弹层
            layer.msg(result.msg + "!", {icon: result.code == 500 ? 2 : 1, time: 1000});
            $("#set").DataTable().ajax.reload(null, false);
        })
    }, function () {
    });
}

//删除铃音
function deleteRing(id) {
    layer.confirm("你确定要删除此条记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxDelete("/threenets/deleteThreeNetsRing", {"id": id}, function (result) {
            layer.closeAll('dialog');//关闭弹层
            layer.msg(result.msg + "!", {icon: result.code == 500 ? 2 : 1, time: 1000});
            $("#set").DataTable().ajax.reload(null, false);
        })
    }, function () {
    });
}