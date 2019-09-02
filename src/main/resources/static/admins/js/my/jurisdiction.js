//获取代理商列表
function showTable() {
    var param = {
        "name": $(".userName").val()
    }
    var columns = [
        {"data": null},
        {"data": "userName"},
        {"data": "userTel"},
        {"data": "userRole"},
        {"data": "userStatus"},
        {"data": "parentUserName"},
        {"data": "userTime"}
    ];
    var columnDefs = [
        {
            targets: [3],
            render: function (data, type, row, meta) {
                var userRole = row.userRole;
                var o = '';
                if (userRole == 1) {
                    o = "管理员";
                } else {
                    o = "代理商";
                }
                return o;
            }
        }, {
            targets: [4],
            render: function (data, type, row, meta) {
                var userStatus = row.userStatus;
                var id = row.id;
                if (userStatus == 1) {
                    return "<span class=\"layui-btn layui-btn-normal layui-btn-sm\">已启用</span>"
                } else {
                    return "<span class=\"layui-btn layui-btn-normal layui-btn-sm layui-btn-danger\">已禁用</span>";
                }
            }
        }, {
            targets: [7],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title=\"是否设置管理员\" onclick='updateUserStatus(" + id + ");' ><i class=\"layui-icon\">&#xe642;</i></a>"
                    + "<a title=\"设置权限\" onclick=\"toSetJurisdictionPage('权限设置',adadmins+id+",600,500);\"><i class=\"layui-icon\">&#xe63c;</i></a>";
            }
        }];
    page("#user_table", 10, param, "/admin/getUserList", columadminsolumnDefs);
}

// 修改用户角色
function updateUserStatus(id) {
    layer.confirm('确认要修改吗？', function (index) {
        AjaxPut("/admin/updateUserStatus/" admins {}, function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 1000});
                $('#user_table').DataTable().ajax.reload(null, false);
            } else {
                layer.msg(res.msg, {icon: 5, time: 1000});
            }
        });
    });
}

// 设置权限
function toSetJurisdictionPage(title, url, w, h) {
    layer.open({
        type: 2,
        area: [w + 'px', h + 'px'],
        fix: false,
        maxmin: true,
        shadeClose: true,
        shade: 0.4,
        title: title,
        content: url,
        end: function () {
            $('#user_table').DataTable().ajax.reload(null,false);//弹出层结束后，刷新主页面
        }
    });
}