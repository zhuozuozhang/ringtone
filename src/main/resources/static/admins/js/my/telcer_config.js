$(document).ready(function () {
    showTelCerConfigTable();
});
function showTelCerConfigTable(){
    var columns = [
        {"data": null},
        {"data": "name"},
        {"data": "price"}
    ];
    var columnDefs = [
        {
            targets: [3],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title='修改' onclick=\"editConfig('修改配置信息','/admin/toEditTelCerConfig/"+id+"',800,500);\" ><i class=\"layui-icon\">&#xe642;</i></a>";
            }
        }];
    page("#config_table", 15, {}, "/admin/getAllConfig", columns, columnDefs);
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
        end: function () {
            $('#config_table').DataTable().ajax.reload(null, false);
        }
    });
}


layui.use(['form','layer'], function(){
    $ = layui.jquery;
    var form = layui.form,layer = layui.layer;
    //监听提交
    form.on('submit(add)', function(data){
        var type = $("#type").val();
        var price = $("#price").val();
        AjaxPut("/admin/editTelCerConfig",{
            type:type,
            price:price,
            id:$("#configId").val()
        },function (res) {
            if (res.code == 200 && res.data){
                //发异步，把数据提交给后台
                layer.alert("修改成功", {icon: 6},function () {
                    // 获得frame索引
                    var index = parent.layer.getFrameIndex(window.name);
                    //关闭当前frame
                    parent.layer.close(index);
                });
            }else{
                layer.msg("修改配置出错！", {icon: 5, time: 1000});
            }
        });
        return false;
    });
});