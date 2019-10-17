// 显示数据
function showTable() {
    var params = {
        "isMonthly": 0,
        "timeType": 0,
        "id": $('#parentOrderId').val(),
        "telLinkPhone": $('#telLinkPhone').val()
    }
    var columns = [
        {"data": "id"},
        {"data": "linkman"},
        {"data": "linkmanTel"},
        {"data": "operator"},
        {"data": "price"},
        {"data": "province"},
        {"data": "createDate"},
        {"data": "ringName"},
        {"data": "isRingtoneUser"},
        {"data": "isMonthly"},
        {"data": "isVideoUser"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [3],
        render: function (data, type, row, meta) {
            if (data == 1) {
                return '移动'
            } else if (data == 2) {
                return '电信'
            } else {
                return row.isExemptSms == 1 ? '联通（免短）' : "联通";
            }
        }
    }, {
        targets: [7],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            // return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
            return data
        }
    }, {
        targets: [8],
        render: function (data, type, row, meta) {
            var id = row.id;
            var status = data ? '是' : '否';
            // return status + "<i onclick='refreshRingStatus(" + id + ")' class='layui-icon' title='刷新' data-rowindex='" + meta.row + "'><img src='../../client/threenets/images/refresh.png'></i>";
            return status
        }
    }, {
        targets: [9],
        render: function (data, type, row, meta) {
            var status = '';
            if (data == 1) {
                status = '未包月';
            } else if (data == 2) {
                status = '已包月';
            } else {
                status = '已退订';
            }
            var id = row.id;
            //return status + "<i onclick='refreshMonthlyStatus(" + id + ")' class='layui-icon' title='刷新'><img src='../../client/threenets/images/refresh.png'></i>";
            return status;
        }
    }, {
        targets: [10],
        render: function (data, type, row, meta) {
            var operator = row.operator;
            var id = row.id;
            var status = data ? '是' : '否'
            //return status + (operator == 1 ? "<i onclick='refreshVideoRingStatus(" + id + ")' class='layui-icon' title='刷新'><img src='../../client/threenets/images/refresh.png'></i>" : '');
            return status;
        }
    }, {
        targets: [11],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            return "<div style='width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [12],
        render: function (data, type, row, meta) {
            var id = row.id;
            var operator = row.operator;
            var isMonthly = row.isMonthly;
            var setRing = "<a href='/threenets/toUserSetingRing/" + id + "/" + operator + "/" + $("#companyName").val() + "/" + $("#parentOrderId").val() + "'><i class='layui-icon layui-icon-set' title='设置铃音'></i></a>";
            var refresh = "<i onclick='refreshStatus(" + id + ")' class='layui-icon layui-icon-refresh-3' title='刷新'></i>";
            var note = "<i onclick='sendMessage(2,1," + id + ");' class='layui-icon' title='下发短信'><img src='../../client/threenets/images/message.png'></i>";
            var linkNote = "<i onclick='sendMessage(2,2," + id + ");' class='layui-icon' title='下发链接短信'><img src='../../client/threenets/images/link.png'></i>";
            var del = "<i class='layui-icon layui-icon-delete' title='删除' onclick='deleteTel(" + id + ")'></i>";
            if (row.isExemptSms) {
                var open = "<i onclick='openingBusiness(" + id + ");' class='layui-icon layui-icon-auz' title='开通业务'></i>"
                return refresh + open + (isMonthly == 2 ? setRing : '') + del;
            }
            if (operator == 2) {
                return refresh + setRing + del;
            }
            return refresh + (isMonthly != 2 ? note : '') + (isMonthly == 2 ? setRing : '') + (operator == 3 && isMonthly == 1 ? linkNote : '') + del;
        }
    }];
    page("#set", 15, params, "/threenets/getThreeNetsTaskList", columns, columnDefs);
}

function refreshRingStatus(id) {
    refresh("/threenets/refreshUserStatus/ring", id);
}

function refreshMonthlyStatus(id) {
    refresh("/threenets/refreshUserStatus/monthly", id);
}

function refreshVideoRingStatus(id) {
    refresh("/threenets/refreshUserStatus/videoRing", id);
}

function refreshStatus(id) {
    refresh("/threenets/refreshUserStatus/alone", id);
}

function batchRefresh() {
    bRefresh("/threenets/refreshUserStatus/all", $('#parentOrderId').val());
}

function openingBusiness(id) {
    AjaxPut("/threenets/openingBusiness/" + id, {}, function (res) {
        if (res.code == 200 && res.data) {
            layer.msg('更新成功！', {icon: 6, time: 3000});
            $("#set").DataTable().ajax.reload(null, false);
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}

function refresh(url, id) {
    AjaxPut(url, {id: id}, function (res) {
        if (res.code == 200) {
            layer.msg('更新成功！', {icon: 6, time: 3000});
            $("#set").DataTable().ajax.reload(null, false);
        } else if (res.code == 501) {
            layer.msg(res.msg, {icon: 5, time: 3000});
            setTimeout(function () {
                showTable();
            }, 3000)
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}

function bRefresh(url, id) {
    AjaxPut(url, {id: id}, function (res) {
        if (res.code == 200) {
            layer.msg('更新成功！', {icon: 6, time: 3000});
            setTimeout(function () {
                showTable();
            }, 3000)
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}

//添加账号
function addUser() {
    var orderId = $("#parentOrderId").val();
    layer.open({
        type: 2,
        title: '添加号码',
        area: ['650px', '650px'],
        content: '/threenets/toAddMerchantsPhonePage/' + orderId
    });
}

// 显示批量下发短信弹窗
function batch() {
    layer.open({
        type: 1,
        title: '批量下发短信',
        area: ['500px', '300px'],
        content: "<div class='sendMes'>" +
            "<input type='button' class='btn btn-primary' value='给所有未包月用户下发短信' id='SMS' onclick='sendMessage(1,1,);'>" +
            "<span>(运营商下的所有未包月用户)</span></br></br></br></br>" +
            "<input type='button' class='btn btn-primary' value='给所有未包月用户下发链接短信' id='links' onclick='sendMessage(1,2,);'>" +
            "<span>(仅联通下的所有未包月用户)</span>" +
            "</div>"
    });
}

// 发送短信 包含批量发送以及联通发送链接短信
// type 标识是否是批量操作 1、批量操作/2、单个操作
// flag 标识是否是下发链接短信 1、普通短信/2、链接短信
// data 数据 type为1时，data为父级订单ID；type为2时，data为子订单ID
function sendMessage(type, flag, data) {
    if (type == 1) {
        data = $("#parentOrderId").val()
    }
    AjaxPut("/threenets/sendMessage", {
        type: type,
        flag: flag,
        data: data
    }, function (res) {
        if (res.code == 200 && res.data) {
            layer.msg(res.msg, {icon: 6, time: 3000});
            $("#set").DataTable().ajax.reload(null, false);
        } else {
            layer.confirm(res.msg, {
                btn: ["确定"]
            }, function () {
                layer.closeAll('dialog');//关闭弹层
            });
        }
    });
}

//删除
function deleteTel(id) {
    layer.confirm("你确定要删除此行记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxDelete("/threenets/deleteThreeNetsChildOrder", {"id": id}, function (result) {
            layer.closeAll('dialog');//关闭弹层
            layer.msg(result.msg + "!", {icon: result.code == 500 ? 2 : 1, time: 1000});
            $('#set').DataTable().ajax.reload(null, false);
        })
    }, function () {
    });
}