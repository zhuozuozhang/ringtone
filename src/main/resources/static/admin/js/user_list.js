//获取代理商列表
function showRingTable() {
    var param={
        "name" : $(".userName").val()
    }
    var columns = [
        {"data": null},
        {"data": "userName"},
        {"data": "userTel"},
        {"data": "userEmail"},
        {"data": "userRole"},
        {"data": "userStatus"},
        {"data": "openNum"},
        {"data": "telcertificationAccount"},
        {"data": "parentUserName"},
        {"data": "userTime"}
    ];
    var columnDefs = [
        {
            targets: [4],
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
            targets: [5],
            render:function (data, type, row, meta) {
                var userStatus = row.userStatus;
                var id = row.id;
                if(userStatus == 1){
                    return "<span class=\"layui-btn layui-btn-normal layui-btn-sm\" onclick=\"user_status(this,"+id+");\" title=\"启用\">已启用</span>"
                }else{
                    return "<span class=\"layui-btn layui-btn-normal layui-btn-sm layui-btn-danger\" onclick=\"user_status(this,"+id+");\" title=\"禁用\">已禁用</span>";
                }
            }
        }, {
            targets: [10],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title=\"充值\" onclick=\"toChargePage('充值','/admin/toChargePage/"+id+"',800,300);\"><i class=\"layui-icon\">&#xe65e;</i></a>"
                    + "<a title=\"查看\" onclick=\"x_admin_show('详细信息','/admin/toUserDetailPage/"+id+"','','');\"><i class=\"layui-icon\">&#xe63c;</i></a>"
                    + "<a onclick=\"resetPassword("+id+");\" title=\"重置密码\"><i class=\"layui-icon\">&#xe631;</i></a>";
            }
        }];
    page("#user_table", 10, param, "/admin/getUserList", columns, columnDefs);
}
// 状态设置
function user_status(obj, id) {
    if ($(obj).attr('title') == '启用') {
        layer.confirm('确认要禁用吗？', function (index) {
            AjaxPut("/admin/updateUserStatus", {
                "id": id,
                "userStatus": false
            }, function (res) {
                if (res.data) {
                    layer.msg('已禁用!', {icon: 5, time: 1000});
                    $('#user_table').DataTable().ajax.reload(null,false);
                } else {
                    layer.msg(res.msg, {icon: 6, time: 1000});
                }
            });
        });
    } else {
        layer.confirm('确认要启用吗？', function (index) {
            AjaxPut("/admin/updateUserStatus", {
                "id": id,
                "userStatus": true
            }, function (res) {
                if (res.data) {
                    layer.msg('已启用!', {icon: 6, time: 1000});
                    $('#user_table').DataTable().ajax.reload(null,false);
                } else {
                    layer.msg(res.msg, {icon: 6, time: 1000});
                }
            });
        });
    }
}
// 重置密码
function resetPassword(id) {
    layer.confirm('确认要重置密码为‘sg123456’吗？', function (index) {
        AjaxPut("/admin/updateUserPassword/" + id, {}, function (res) {
            if (res.data) {
                layer.msg('重置密码成功!', {icon: 1, time: 1000});
            } else {
                layer.msg(res.msg, {icon: 6, time: 1000});
            }
        });
    });
}
// 充值
function toChargePage(title, url, w, h) {
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
/*用户-删除*/
function member_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        //发异步删除数据
        $(obj).parents("tr").remove();
        layer.msg('已删除!', {icon: 1, time: 1000});
    });
}