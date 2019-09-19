$(document).ready(function () {
    showTelCerConfigTable();
});
function showTelCerConfigTable(){
    var columns = [
        {"data": null},
        {"data": "type"},
        {"data": "typeName"},
        {"data": "price"}
    ];
    var columnDefs = [
        {
            targets: [4],
            render: function (data, type, row, meta) {
                var id = row.id;
                return "<a title='修改' onclick=\"editConfig('修改配置信息','/admin/toEditTelCerConfig/"+id+"',800,500);\" ><i class=\"layui-icon\">&#xe642;</i></a>" +
                    "<a title='删除' onclick='config_del(\""+id+"\")' href='javascript:;'><i class='layui-icon layui-icon-delete'></i></a>";
            }
        }];
    page("#config_table", 15, {}, "/admin/getAllConfig", columns, columnDefs);
}

/*配置-删除*/
function config_del(id){
    layer.confirm('确认要删除吗？',function(index){
        //发异步删除数据
        AjaxDelete("/admin/delConfig/"+id,{},function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#config_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
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
        AjaxPut("/admin/editTelCerConfig",{
            typeName:$("#typeName").val(),
            price: $("#price").val(),
            id:$("#configId").val(),
        },function (res) {
            if (res.code == 200){
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
function abc() {
    alert("lala");
}
//添加号码认证业务
function addTelCerService(title, url, w, h){
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

layui.use(['form','layer'], function(){
    $ = layui.jquery;
    var form = layui.form,layer = layui.layer;
    //监听提交
    form.on('submit(addConfig)', function(data){
        AjaxPut("/admin/addTelCerService",{
            typeName : $("#typeName").val(),
            price: $("#price").val(),
        },function (res) {
            if (res.code == 200){
                //发异步，把数据提交给后台
                layer.alert("添加成功", {icon: 6},function () {
                    // 获得frame索引
                    var index = parent.layer.getFrameIndex(window.name);
                    //关闭当前frame
                    parent.layer.close(index);
                });
            }else{
                layer.msg("添加业务出错！", {icon: 5, time: 1000});
            }
        });
        return false;
    });
});
