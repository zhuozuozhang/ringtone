
// 获取号码认证信息
$(document).ready(function () {
    showTelcertification_table();
});
function showTelcertification_table() {
    var param = {
        "id": $("#id").val(),
        "rangetime" : $("#rangetime").val(),
        "telCompanyName": $("#companyName").val().trim(),
        "telLinkPhone" : $("#tel").val().trim(),
        "phoneNum" : $("#membernum").val().trim()
    }
    var columns = [
        {"data": "id"},
        {"data": "telCompanyName"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "memberNum"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "productName"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[7],
        render: function (data, type, row, meta) {
            var productName = row.productName;
            var product = $.parseJSON(productName);
            var service = product.service;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].periodOfValidity + ",</td>";
                str += "<td>" + service[i].cost + "元</td></br>";
                str += "</tr>";
            }
            return str;
        }
    }, {
        targets:[9],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<a title='查看' onclick='x_admin_show(\"详细信息\",\"/admin/telcertificationDetail/"+id+"\",\"\",\"\")' href='javascript:;'><i class='layui-icon layui-icon-survey'></i></a>" +
                "<a title='成员管理' onclick='x_admin_show(\"成员管理\",\"/admin/telcertificationChildList/"+id+"\",\"\",\"\")' href='javascript:;'><i class='layui-icon layui-icon-user'></i></a>" +
                "<a title='删除' onclick='telCertification_del(\""+id+"\")' href='javascript:;'><i class='layui-icon layui-icon-delete'></i></a>";
         }
    }];

    if(param.telLinkPhone != "" && param.telLinkPhone != null){
        if(!isTel(param.telLinkPhone)){
            if(!isPhone(param.telLinkPhone)){
                if(!is_Phone(param.telLinkPhone)){
                    layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    page("#telcertification_table", 10, param, "/admin/getTelCerOrderList", columns, columnDefs);
}

/*用户-删除*/
function telCertification_del(id){
    layer.confirm('确认要删除吗？',function(index){
        //发异步删除数据
        AjaxDelete("/admin/deleteTelCer/"+id,{},function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#telcertification_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
    });
}
