$(document).ready(function () {
    showDistributorTable();
});

// 获得订购统计-->渠道商信息
function showDistributorTable() {
    var param = {
        "id": $("#id").val(),
        "distributorName" : $("#distributorName").val().trim(),
        "phoneNum" : $("#phoneNum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "distributorName"},
        {"data": "stage"},
        {"data": "total"},
        {"data": "lastMonthTotal"},
        {"data": "theMonthTotal"},
        {"data": "yesterdayTotal"},
    ];
    var columnDefs = [{
        // targets:[8],
        // render: function (data, type, row, meta) {
        //     var productName = row.productName;
        //     var product = $.parseJSON(productName);
        //     var service = product.service;
        //     var str = "";
        //     for (var i = 0; i < service.length; i++) {
        //         str += "<tr>";
        //         str += "<td>" + service[i].name + ",</td>";
        //         str += "<td>" + service[i].periodOfValidity + ",</td>";
        //         str += "<td>" + service[i].cost + "元</td></br>";
        //         str += "</tr>";
        //     }
        //     return str;
        // }
    }, {
        // targets:[10],
        // render: function (data, type, row, meta) {
        //     var id = row.id;
        //     return "<i class='layui-icon layui-icon-edit' title='编辑' onclick='editTelCerOrder("+id+");'></i>"
        //         + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetails("+id+");'></i>"
        //         + "<a href='/telcertify/toTelMembersPage/"+id+"'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>";
        // }
    }];
    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum)){
            if(!isPhone(param.phoneNum)){
                if(!is_Phone(param.phoneNum)){
                    layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
                    return;
                }
            }
        }
    }
    page("#distributor", 10, param, "/telcertify/getTelCerDistributor", columns, columnDefs);
}