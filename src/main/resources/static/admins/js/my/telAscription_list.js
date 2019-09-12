function showTelAscriptionTable() {
    var columns = [
        {"data": null},
        {"data": "province"},
        {"data": "city"},
        {"data": "areaCode"},
    ];
    var columnDefs = [{
        targets:[1],
        render:function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets: [4],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<a title='编辑' onclick='changetelAscription(\"编辑\",\"/admin/getTelAscriptionById/"+id+"\",800,570)' href='javascript:;'><i class=\"layui-icon\">&#xe642;</i></a>"
                +"<a title='删除' onclick='telAscription_del(this,"+id+");' href='javascript:;'><i class='layui-icon'>&#xe640;</i></a>";
        }
    }];
    page("#telAscription_table", 10, {}, "/telAscription/queryTelAscriptionList", columns, columnDefs);
}

// 修改公告信息
function changetelAscription(title, url, w, h) {
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
            $('#telAscription_table').DataTable().ajax.reload(null,false);
        }
    });
}
/*公告-删除*/
function telAscription_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/admin/deleteTelAscription/"+id,'',function (res) {
            if (res.code = 200 && res.data){
                //发异步删除数据
                $(obj).parents("tr").remove();
                layer.msg('删除成功!', {icon: 1, time: 1000});
            }else{
                layer.msg('删除出错!', {icon: 5, time: 1000});
            }
        });
    });
}