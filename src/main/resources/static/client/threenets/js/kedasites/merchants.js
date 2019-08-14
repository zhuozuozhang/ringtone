//分页
$(document).ready(function () {
    $(".index2").addClass("active");
    showTable();
});
function showTable() {
    var param = {
        "tel": $("#linkPhone").val(),
        "name": $("#enterPriseName").val()
    }
    var columns = [
        {"data":null},
        {"data": "companyName"},
        {"data": "userName"},
        {"data": "linkMan"},
        {"data": "linkTel"},
        {"data": "peopleNum"},
        {"data": "province"},
        {"data": "status"},
        {"data": "cerateTime"},
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
        targets:[9],
        render: function (data, type, row, meta) {
            var id = row.id;
            var name = row.companyName;
            return "<a href='/threenets/clcy/toNumberList/" + id + "/"+name+"'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
                + "<i class='layui-icon layui-icon-edit' title='编辑' onclick='editMerchants(" + id + ",\"" + name + "\")'></i>"
                + "<i class='layui-icon layui-icon-delete' title='删除' onclick='DelMerchants(" + id + ");'></i>";
        }
    }];
    page("#set", 1, param, "/threenets/clcy/getKeDaOrderList", columns, columnDefs);
}
//修改商户名称
function editMerchants(id,name) {
    layer.open({
        type: 1,
        title: '修改商户名称',
        area: ['700px', '250px'],
        content: '<div style="width: 600px;margin: 20px auto;"><div class="layui-form-item"><label class="layui-form-label">商户名称：</label><div class="layui-input-block"><input type="text" name="EnterPriseName" id="EnterPriseName" autocomplete="off" class="layui-input" style="width:450px;" value="'+name+'"></div></div><div class="layui-input-block"><button class="layui-btn" lay-submit lay-filter="formDemo" style="margin: 10px 144px;" onclick="ChangeName('+id+');">立即提交</button></div></div>'
    });
}

//提交修改商户名称
function ChangeName(id) {
    var enterPriseName = $("#EnterPriseName").val();
    if (!isNotEmpty(enterPriseName)) {
        layer.msg("商户名称不能为空！", {icon: 5, time: 3000});
        return;
    }
    AjaxPut("/threenets/clcy/updateCompanyName",{
        id:id,
        companyName:enterPriseName
    },function (res) {
        if (res.code == 200){
            layer.msg(res.msg, {icon: 6, time: 3000});
            $('#set').DataTable().ajax.reload(null,false);
            setTimeout(function () {
                layer.closeAll();//关闭弹层
            },2000);
        } else {
            layer.msg(res.msg, {icon: 5, time: 3000});
        }
    });
}

//删除
function DelMerchants() {
    layer.confirm("你确定要删除该行记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        alert(123);//确定
        layer.closeAll('dialog');//关闭弹层
    }, function () {
        alert(456);//取消
    });
}

//添加商户
function AddUser(a, c, b) {
    layer.open({
        type: 2,
        title: '添加商户',
        area: ['650px', '350px'],
        content: 'Addmerchants.html'
    });
}