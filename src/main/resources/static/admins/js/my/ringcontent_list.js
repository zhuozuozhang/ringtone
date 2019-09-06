//获取代理商列表
function showRingTable() {
    var param = {
        "name": $(".userName").val()
    }
    var columns = [
        {"data": null},
        {"data": "ringContent"},
        {"data": "createTime"}
    ];
    var columnDefs = [
        {
            targets: [3],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title='编辑' onclick='changeRingContent(\"编辑\",\"/admin/toRingContentUpdate/" + id + "\",800,570)' href='javascript:;'><i class=\"layui-icon\">&#xe642;</i></a>"
                    + "<a title='删除' onclick='del(this," + id + ");' href='javascript:;'><i class='layui-icon'>&#xe640;</i></a>";
            }
        }];
    page("#user_table", 10, param, "/admin/selectRingContent", columns, columnDefs);
}

// 修改信息
function changeRingContent(title, url, w, h) {
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
            $('#user_table').DataTable().ajax.reload(null, false);
        }
    });
}

/*删除*/
function del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/admin/deleteRingContent/" + id, '', function (res) {
            if (res.code = 200 && res.data) {
                //发异步删除数据
                $(obj).parents("tr").remove();
                layer.msg('删除成功!', {icon: 1, time: 1000});
            } else {
                layer.msg('删除出错!', {icon: 5, time: 1000});
            }
        });
    });
}