$(document).ready(function () {
    //分页
    $(".index2").addClass("active");
    showTable();
});

function showTable() {
    var params = {
        "orderId": $('#orderId').val(),
        "isMonthly": $("#isMonthly").val()
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
        {
            "data": "isMonthly", render: function (data, type, row, meta) {
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
                } else {
                    status = '删除失败';
                }
                return status;
            }
        },
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
            if (data == 1) {
                return "否";
            } else if (data == 2) {
                return "是";
            } else {
                return "开通失败"
            }
        }
    }, {
        targets: [9],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [10],
        render: function (data, type, row, meta) {
            var id = row.id;
            var isMonthly = row.isMonthly;
            var setRing = "<a href=''><i class='layui-icon layui-icon-set' title='设置铃音'></i></a>";
            var refresh = "<i onclick='getPhoneInfo(" + id + ")' class='layui-icon layui-icon-refresh-3' title='刷新'></i>";
            var del = "<i class='layui-icon layui-icon-delete' title='删除' onclick='deleteTel(" + id + ")'></i>";
            return refresh + (isMonthly == 2 ? setRing : '') + del;
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
        content: '/threenets/clcy/addnumber'
    });
}

//设置铃音
function RingSet() {
    var str = '';
    str += '<div class="layui-col-md12" id="search">';
    str += '<div class="setting_title">';
    str += '成员姓名：<span id="settingRing_apersonnelName">18315065540</span> ';
    str += '| 成员电话：<span id="settingRing_apersonnelPhone">18315065540</span>';
    str += '</div>';
    str += '<table class="layui-table" id="set" style="width:700px;margin: 0 auto;">';
    str += '<thead>';
    str += '<tr>';
    str += '<th>#</th>';
    str += '<th><input type="checkbox" id="allCheck"></th>';
    str += '<th>铃音名称</th>';
    str += '<th>创建时间</th>';
    str += '<th>在线试听</th>';
    str += '</tr>';
    str += '</thead>';
    str += '<tbody>';
    str += '<tr>';
    str += '<td>1</td>';
    str += '<td><input type="checkbox" name="check"></td>';
    str += '<td>3d环保墙体.mp3</td>';
    str += '<td>2019-06-26 13:40:10</td>';
    str += '<td>';
    str += '<span onclick="openPlayer();">试听</span>';
    str += '</td>';
    str += '</tr>';
    str += '<tr>';
    str += '<td>2</td>';
    str += '<td><input type="checkbox" name="check"></td>';
    str += '<td>3d环保墙体.mp3</td>';
    str += '<td>2019-06-26 13:40:10</td>';
    str += '<td>';
    str += '<span onclick="openPlayer();">试听</span>';
    str += '</td>';
    str += '</tr>';
    str += '</tbody>';
    str += '</table>';
    str += '</div>';
    layer.open({
        title: '设置铃音',
        area: ['750px', '500px'],
        content: str
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