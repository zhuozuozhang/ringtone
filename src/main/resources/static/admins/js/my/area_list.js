function showAreaTable() {

    var param = {
        "name": $("#name").val()
    }

    var columns = [
        {"data": null},
        {"data": "name"},
        {"data": "pname"},
        {"data": "type"},
    ];
    var columnDefs = [{
        targets:[1],
        render:function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets:[3],
        render:function (data, type, row, meta) {
            if(data == 'province'){
                return "省";
            }else{
                return "市";
            }
        }
    }];
    page("#Area_table", 10, {}, "/area/queryAreaList", columns, columnDefs);
}

// 修改公告信息
function changeArea(title, url, w, h) {
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
            $('#Area_table').DataTable().ajax.reload(null,false);
        }
    });
}
/*公告-删除*/
function Area_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/Area/deleteArea/"+id,'',function (res) {
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