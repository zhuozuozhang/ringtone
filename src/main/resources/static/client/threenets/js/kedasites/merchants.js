//分页
$(document).ready(function () {
    var f  = sessionStorage.getItem("showKedaAnn")
    if(f == null || f == "true"){
        // layer.open({type: 2, title: '公告', area: ['850px', '500px'], content: '/threenets/threeNetsAnnunciate?type=keda'});
        // sessionStorage.setItem("showKedaAnn","false")
        // layer.open({type: 2, title: '公告', area: ['850px', '500px'], content: '/threenets/threeNotice'});
        window.location.href = "/threenets/threeNotice/0/1/5/1?noticeTitle=";
    }
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
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='"+data+"'><a href='/threenets/clcy/toNumberList/" + id + "/"+data+"' style='color: #0569FD'>"+data+"</a></div>";
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
                + "<i class='layui-icon layui-icon-delete' title='删除' onclick='delMerchants(" + id + ");'></i>";
        }
    }];
    page("#set", 10, param, "/threenets/clcy/getKeDaOrderList", columns, columnDefs);
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
function delMerchants(id) {
    layer.confirm("你确定要删除该行记录吗?", {
        btn: ["确定", "取消"] //按钮
    }, function () {
        AjaxDelete("/threenets/clcy/deleteKedaOrder",{
            id:id
        },function (res) {
            if (res.code == 200){
                layer.msg(res.msg, {icon: 6, time: 3000});
                $('#set').DataTable().ajax.reload(null,false);
            } else {
                layer.msg("删除失败！", {icon: 5, time: 3000});
            }
        });
        layer.closeAll('dialog');//关闭弹层
    }, function () {
    });
}

//添加商户
function AddUser(a, c, b) {
    layer.open({
        type: 2,
        title: '添加商户',
        area: ['650px', '350px'],
        content: '/threenets/clcy/toAddmerchants',
        end: function () {
            $('#set').DataTable().ajax.reload(null,false);//弹出层结束后，刷新主页面
        }
    });
}