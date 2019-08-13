//获取代理商列表
function showTable() {
    var columns = [
        {"data": null},
        {"data": "type"},
        {"data": "info"}
    ];
    var columnDefs = [
        {
            targets: [3],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title=\"是否设置管理员\" onclick='updateUserStatus(" + id + ");' ><i class=\"layui-icon\">&#xe642;</i></a>"
                    + "<a title=\"设置权限\" onclick=\"toSetJurisdictionPage('权限设置','/admin/toSetJurisdictionPage/"+id+"',600,500);\"><i class=\"layui-icon\">&#xe63c;</i></a>";
            }
        }];
    page("#user_table", 1, {}, "/admin/getConfigList", columns, columnDefs);
}

// 修改用户角色
function updateUserStatus(id) {
    layer.confirm('确认要修改吗？', function (index) {
        AjaxPut("/admin/updateUserStatus/" + id, {}, function (res) {
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