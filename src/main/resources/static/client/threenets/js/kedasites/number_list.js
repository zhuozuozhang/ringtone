$(document).ready(function () {
    //分页
    $(".index2").addClass("active");
    showTable();
});

function showTable() {
    var params = {
        "orderId": $('#orderId').val(),
        "isMonthly": $("#isMonthly").val(),
        "telLinkPhone": $("#telLinkPhone").val()
    }
    var columns = [
        {"data": "id"},
        {"data": "linkMan"},
        {"data": "linkTel"},
        {"data": "province"},
        {"data": "createTime"},
        {"data": "status"},
        {"data": "ringName"},
        {"data": "isRingtoneUser"},
        {"data": "isMonthly"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [6],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [7],
        render: function (data, type, row, meta) {
            var id = row.id;
            var status = "是";
            if (data == 1) {
                status = "否";
            } else if (data == 2) {
                status = "是";
            } else {
                status = "开通失败"
            }
            //return status + "<i onclick='getPhoneInfo(" + id + ")' class='layui-icon' title='刷新' data-rowindex='" + meta.row + "'><img src='../../../../client/threenets/images/refresh.png'></i>";
            return status;
        }
    }, {
        targets: [8],
        render: function (data, type, row, meta) {
            // 0.未开通/1.开通成功/2.开通失败/3.已退订/4.开通中/5.删除中/6.删除失败
            var status = '';
            if (data == 0) {
                status = '未包月';
            } else if (data == 1) {
                status = '已包月';
            } else if (data == 2) {
                status = '包月失败';
            } else if (data == 3) {
                status = '已退订';
            } else if (data == 4) {
                status = '包月中';
            } else if (data == 5) {
                status = '删除中';
            } else if (data == 6) {
                status = '删除失败';
            } else {
                status = '已回短信';
            }
            var id = row.id;
            //return status + "<i onclick='getPhoneInfo(" + id + ")' class='layui-icon' title='刷新' data-rowindex='" + meta.row + "'><img src='../../../../client/threenets/images/refresh.png'></i>";
            return status;
        }
    }, {
        targets: [9],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            return "<div style='width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [10],
        render: function (data, type, row, meta) {
            var id = row.id;
            var isMonthly = row.isMonthly;
            var linkMan = row.linkMan;
            var linkTel = row.linkTel;
            var employeeId = row.employeeId;
            var operate = row.operate;
            var note = "<i onclick='sendMessage("+row.linkTel+");' class='layui-icon layui-icon-reply-fill' title='下发短信'></i>";
            var setRing = "<a href='javascript:;' onclick='ringSet(\"" + linkMan + "\",\"" + linkTel + "\"," + employeeId + "," + id + ");'><i class='layui-icon layui-icon-set' title='设置铃音'></i></a>";
            var refresh = "<i onclick='getPhoneInfo(" + id + ")' class='layui-icon layui-icon-refresh-3' title='刷新'></i>";
            var del = "<i class='layui-icon layui-icon-delete' title='删除' onclick='deleteTel(" + id + ")'></i>";
            return  (isMonthly != 2 ? note : '') + refresh + setRing + del;
        }
    }];
    page("#set", 10, params, "/threenets/clcy/getKedaChidList", columns, columnDefs);
}

//添加账号
function AddUser() {
    layer.open({
        type: 2,
        title: '添加号码',
        area: ['650px', '400px'],
        content: '/threenets/clcy/addnumber/' + $('#orderId').val(),
        end: function () {
            $('#set').DataTable().ajax.reload(null, false);//弹出层结束后，刷新主页面
        }
    });
}

//设置铃音
function ringSet(linkMan, linkTel, employeeId, id) {
    var str = '';
    str += '<div class="layui-col-md12" id="search">';
    str += '<div class="setting_title">';
    str += '成员姓名：<span id="settingRing_apersonnelName" style="color:#17a9ff;">' + linkMan + '</span>';
    str += '| 成员电话：<span id="settingRing_apersonnelPhone" style="color:#17a9ff;">' + linkTel + '</span>';
    str += '</div>';
    str += '<table class="layui-table" id="setRing" style="width:700px;margin: 0 auto;">';
    str += '<thead>';
    str += '<tr>';
    str += '<th>#</th>';
    str += '<th></th>';
    str += '<th>铃音名称</th>';
    str += '<th>创建时间</th>';
    str += '<th>在线试听</th>';
    str += '</tr>';
    str += '</thead>';
    str += '<tbody>';
    str += '</tbody>';
    str += '</table>';
    str += '</div>';
    layer.open({
        title: '设置铃音',
        area: ['750px', '500px'],
        content: str,
        btn: ['确定'],
        yes: function (index, layero) {
            // 判断是否选择铃音,获取铃音编号
            var ringId = $('input[name="check"]:checked').val();
            if (!isNotEmpty(ringId)) return layer.msg("请选中一条铃音！", {icon: 5, time: 3000});
            AjaxPut("/threenets/clcy/setKedaChidOrder", {
                ringId: ringId,
                linkTel: linkTel,
                employeeId: employeeId,
                childOrderId: id
            }, function (res) {
                if (res.code == 200) {
                    layer.msg(res.msg, {icon: 6, time: 3000});
                    $('#set').DataTable().ajax.reload(null, false);
                    setTimeout(function () {
                        layer.close(index);
                    }, 3000);
                } else {
                    layer.msg(res.msg, {icon: 5, time: 3000});
                }
            });
        }
    });
    AjaxPost("/threenets/clcy/getKedaRingSetting/" + $('#orderId').val(), {}, function (res) {
        if (res.code == 200 && isNotEmpty(res.data)) {
            var list = res.data;
            var data = '';
            for (var i = 0; i < list.length; i++) {
                data += '<tr><td>' + (i + 1) + '</td><td><input type="radio" name="check" value="' + list[i].id + '"></td><td>' + list[i].ringName + '</td><td>' + list[i].createTime + '</td><td><span onclick="openPlayer(\'' + list[i].ringUrl + '\');">试听</span></td></tr>';
            }
            $("#setRing tbody").html(data);
        }
    });
}

//在线试听
function openPlayer(ringUrl) {
    layer.open({
        type: 1,
        title: '在线试听',
        area: ['800px', '560px'],
        content: '<video id="ovideo" autoplay loop src="' + ringUrl + '" controls="controls " allowfullscreen="true" quality="high" width="800px" height="514px" align="middle" allowscriptaccess="always" flashvars="isAutoPlay=true" type="application/x-shockwave-flash"></video>'
    });
}

// 刷新子订单信息
function getPhoneInfo(id) {
    AjaxPost("/threenets/clcy/getPhoneInfo/" + id, {}, function (res) {
        if (res.code == 200) {
            layer.msg("刷新成功！", {icon: 6, time: 3000});
            $('#set').DataTable().ajax.reload(null, false);
        } else {
            layer.msg("刷新失败！", {icon: 5, time: 3000});
        }
    });
}

// 删除子订单
function deleteTel(id) {
    layer.confirm("你确定要删除该行记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxDelete("/threenets/clcy/deleteKedaChildOrder/" + id, {}, function (res) {
            if (res.code == 200) {
                layer.msg(res.msg, {icon: 6, time: 3000});
                $('#set').DataTable().ajax.reload(null, false);
            } else {
                layer.msg("删除失败！", {icon: 5, time: 3000});
            }
        });
        layer.closeAll('dialog');//关闭弹层
    }, function () {
    });
}

function sendMessage(phone) {
    // layer.open({
    //     type: 2,
    //     title: '用户信息',
    //     content: '/threenets/clcy/sendMessages/'+phone,
    //     area: ['950px', '600px'],
    //     maxmin: true
    // });
    window.open("http://t.cn/R1BRf4e?phoneNo=15150013617");
}

// 刷新子订单信息
function listPhoneInfo() {
    var id = $("#orderId").val();
    AjaxPost("/threenets/clcy/listPhoneInfo/" + id, {}, function (res) {
        if (res.code == 200) {
            layer.msg("刷新成功！", {icon: 6, time: 3000});
            $('#set').DataTable().ajax.reload(null, false);
        } else {
            layer.msg("刷新失败！", {icon: 5, time: 3000});
        }
    });
}