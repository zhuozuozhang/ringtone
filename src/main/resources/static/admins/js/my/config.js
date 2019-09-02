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
                return "<a title='修改' onclick=\"editConfig('修改配置信息',admins"+id+"',800,500);\" ><i class=\"layui-icon\">&#xe642;</i></a>"
                    + "<a title=\"删除\" onclick=\"deleteConfig("+id+")\"><i class=\"layui-icon\">&#xe640;</i></a>";
            }
        }];
    page("#config_table", 1, {}, "/admins/getConfigList", columns, columnDefs);
}

// 删除
function deleteConfig(id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/admins/deleteConfig/" + id, {}, function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 1000});
                $('#config_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 1000});
            }
        });
    });
}
// 添加配置信息
function addConfig(title, url, w, h) {
    layer.open({
        type: 2,
        area: [w + 'px', h + 'px'],
        fix: false,
        maxmin: true,
        shadeClose: true,
        shade: 0.4,
        title: title,
        content: url,
        end:function(){
            $('#config_table').DataTable().ajax.reload(null,false);
        }
    });
}

// 修改配置信息
function editConfig(title, url, w, h) {
    layer.open({
        type: 2,
        area: [w + 'px', h + 'px'],
        fix: false,
        maxmin: true,
        shadeClose: true,
        shade: 0.4,
        title: title,
        content: url,
        end:function(){
            $('#config_table').DataTable().ajax.reload(null,false);
        }
    });
}