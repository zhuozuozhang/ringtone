function showTelCerTable() {
    var param = {
        "id": $("#id").val(),
        "companyName": $("#companyName").val()
        // "name": $("#enterPriseName").val()
    }
    var columns = [
        {"data": null},
        {"data": "telCompanyName"},
        {"data": "telContent"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "productName"},
        {"data": null},
        {"data": "telOrderStatus"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[11],
        render: function (data, type, row, meta) {
            var id = row.id;
            var name = row.telCompanyName;
            return "<i class='layui-icon layui-icon-edit' title='编辑' onclick='edit();'></i>"
                + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetailsOne();'></i>"
                + "<a href='/telcertify/toTelMembersPage'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>";
        }
    }];
    page("#merchants", 15, param, "/telcertify/getTelCerOrderList", columns, columnDefs);
}