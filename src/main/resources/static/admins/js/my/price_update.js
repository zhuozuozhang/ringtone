layui.use(['form', 'layer'], function () {
    $ = layui.jquery;
    var form = layui.form, layer = layui.layer;
    form.on('submit(add)', function (data) {
        AjaxPut("/admin/updateNumcertificationPrice", data.field, function (res) {
            if (res.code == 200 && res.data) {
                layer.alert("修改成功", {icon: 6}, function () {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index)
                })
            } else {
                layer.msg("修改出错！", {icon: 5, time: 1000})
            }
        });
        return false
    })
});