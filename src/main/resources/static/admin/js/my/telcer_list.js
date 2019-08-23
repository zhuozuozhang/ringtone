function showTelcertification_table() {
    var param = {
        "id": $("#id").val(),
        "rangetime" : $("#rangetime").val(),
        "companyName": $("#companyName").val(),
        "tel" : $("#tel").val(),
        "phoneNum" : $("#membernum").val()
    }
    var columns = [
        {"data": null},
        {"data": "id"},
        {"data": "telCompanyName"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "memberNum"},
        {"data": "statusName"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "productName"},
        {"data": "remark"}
    ];
    var columnDefs = [
    //     {
    //     targets:[10],
    //     render: function (data, type, row, meta) {
    //         var id = row.id;
    //         return "<div class='layui-unselect layui-form-checkbox' lay-skin='primary' data-id='"+id+"'><i class='layui-icon layui-icon-ok'></i></div>"
    //         // return "<div class='layui-unselect header layui-form-checkbox' lay-skin='primary'><i class='layui-icon'>&#xe605;</i></div>"
    //     }
    // },
        {
        targets:[9],
        render: function (data, type, row, meta) {
            var productName = row.productName;
            var product = $.parseJSON(productName);
            var service = product.service;
            var obj = JSON.stringify(service);
            var serviceJson = $.parseJSON(obj);

            var totalCost = product.totalCost;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].period0fValidity + ",</td>";
                str += "<td>" + service[i].cost + "元</td></br>";
                str += "</tr>";
            }
            return str;
        }
    }, {
        targets:[11],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<a title='查看' onclick='x_admin_show(\"详细信息\",\"/admin/telcertification_detail/"+id+"\",\"\",\"\")' href='javascript:;'><i class='layui-icon layui-icon-survey'>&emsp;</i></a>" +
                "<a title='成员管理' onclick='x_admin_show(\"成员管理\",\"/admin/telcertification_child_list/"+id+"\",\"\",\"\")' href='javascript:void();'><i class='layui-icon layui-icon-user'>&emsp;</i></a>" +
                "<a title='删除' onclick='telCertification_del(this,\""+id+"\")' href='javascript:;'><i class='layui-icon layui-icon-delete'></i></a>";
            //
            // return "<i class='layui-icon layui-icon-log' title='查看详细信息' onclick='ckeckDetails("+id+");'></i>"
            //     + "<a href='/telcertify/toTelMembersPage/"+id+"'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>"
            //     + "<a title='删除' onclick='telCertification_del(this,\"要删除的id\")' href='javascript:;'><i class='layui-icon layui-icon-delete'></i></a>";
        }
    }];

    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum.trim())){
            layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
            return;
        }
    }
    page("#telcertification_table", 10, param, "/admin/getTelCerOrderList", columns, columnDefs);
}

// $(document).ready(function(){
// 	//  分页
// 	page("#telcertification_table",9);
// });

/*用户-删除*/
function telCertification_del(obj,id){
    layer.confirm('确认要删除吗？',function(index){
        //发异步删除数据
        $(obj).parents("tr").remove();
        layer.msg('已删除!',{icon:1,time:1000});
    });
}
// 批量删除号码认证订单
function delAll () {
    var data = tableCheck.getData();
    if (isNotEmpty(data)) {
        layer.confirm('确认要删除吗？'+data,function(index){
            //捉到所有被选中的，发异步进行删除
            layer.msg('删除成功', {icon: 1});
            $(".layui-form-checked").not('.header').parents('tr').remove();
        });
    }else{
        layer.msg('至少选择一条数据', {icon: 7});
    }
}