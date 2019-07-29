//获取列表
function showTable() {
    var param = {
        "id": $("#id").val(),
        "tel": $("#linkPhone").val(),
        "name": $("#enterPriseName").val()
    }
    var columns = [
        {"data":null},
        {"data": "companyName"},
        {"data": "userName"},
        {"data": "companyLinkman"},
        {"data": "linkmanTel"},
        {"data": "peopleNum"},
        {"data": "province"},
        {"data": "status"},
        {"data": "createTime"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='"+data+"'><a href='/threenets/toMerchantsPhonePage/" + id + "'>"+data+"</a></div>";
        }
    },{
        targets:[3],
        render:function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='"+data+"'>"+data+"</div>";
        }
    },{
        targets: [9],
        render: function (data, type, row, meta) {
            var id = row.id;
            var name = row.companyName;
            return "<a href='/threenets/toMerchantsPhonePage/" + id + "'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
                + "<i class='layui-icon layui-icon-edit' title='编辑' onclick='EditMerchants(" + id + ",\"" + name + "\")'></i>"
                + "<i class='layui-icon layui-icon-delete' title='删除' onclick='DelMerchants(" + id + ");'></i>";
        }
    }];
    page("#set", 15, param, "/threenets/getThressNetsOrderList", columns, columnDefs);
}

//修改商户名称
function EditMerchants(id, name) {
    console.log(name)
    layer.open({
        type: 1,
        title: '修改商户名称',
        area: ['700px', '250px'],
        content: '<div style="width: 600px;margin: 20px auto;"><div class="layui-form-item"><input type="hidden" id="EnterPriseId" value="' + id + '"><label class="layui-form-label">输入框</label><div class="layui-input-block"><input type="text" name="EnterPriseName" id="EnterPriseName" required  lay-verify="required" autocompvare="off" class="layui-input" style="width:450px;" value="' + name + '"></div></div><div class="layui-input-block"><button class="layui-btn" lay-submit lay-filter="formDemo" style="margin: 10px 144px;" onclick="ChangeName();">立即提交</button></div></div>'
    });
}

//提交修改商户名称
function ChangeName() {
    var id = $("#EnterPriseId").val();
    var name = $("#EnterPriseName").val();
    AjaxPost("/threenets/updateThreeNetsOrder", {"id": id, "companyName": name}, function (result) {
        layer.closeAll();//关闭弹层
        layer.msg(result.msg + "!", {icon: result.code == 500 ? 2 : 1, time: 1000});
        $('#set').DataTable().ajax.reload(null,false);
    })
}

//删除
function DelMerchants(id) {
    layer.confirm("你确定要删除该行记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxDevare("/threenets/devareThreeNetsOrder", {"id": id}, function (result) {
            layer.closeAll('dialog');//关闭弹层
            layer.msg(result.msg + "!", {icon: result.code == 500 ? 2 : 1, time: 1000});
            $('#set').DataTable().ajax.reload(null,false);
        })
    }, function () {});
}

//添加商户
function AddUser(a, c, b) {
    layer.open({
        type: 2,
        title: '添加商户',
        area: ['650px', '650px'],
        content: '/threenets/toAddMerchantsPage'
    });
}