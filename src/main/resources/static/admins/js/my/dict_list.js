function showDictTable() {
    console.log("----------");
    var columns = [
        {"data": null},
        {"data": "code"},
        {"data": "name"},
        {"data": "type"},
        {"data": "remarks"},
    ];
    var columnDefs = [{
        targets:[1],
        render:function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets: [5],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<a title='编辑' onclick='changeDict(\"编辑\",\"/dict/getDictById/"+id+"\",800,570)' href='javascript:;'><i class=\"layui-icon\">&#xe642;</i></a>"
                +"<a title='删除' onclick='dict_del(this,"+id+");' href='javascript:;'><i class='layui-icon'>&#xe640;</i></a>";
        }
    }];
    page("#dict_table", 10, {}, "/dict/queryDictList", columns, columnDefs);
}

// 修改公告信息
function changeDict(title, url, w, h) {
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
            $('#dict_table').DataTable().ajax.reload(null,false);
        }
    });
}
/*公告-删除*/
function dict_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/dict/deleteDict/"+id,'',function (res) {
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