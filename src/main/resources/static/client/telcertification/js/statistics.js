
// 获得订购统计-->渠道商信息
function showDistributorTable() {
    var param = {
        "id": $("#id").val(),
        "distributorName" : $("#distributorName").val().trim()
        // "phoneNum" : $("#phoneNum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "distributorName"},
        {"data": "stage"},
        {"data": "total"},
        {"data": "lastMonthTotal"},
        {"data": "theMonthTotal"},
        {"data": "todayTotal"},
    ];
    var columnDefs = [{
        targets:[3],
        render: function (data, type, row, meta) {
            return data;
        }
    }];
    // if(param.phoneNum != "" && param.phoneNum != null){
    //     if(!isTel(param.phoneNum)){
    //         if(!isPhone(param.phoneNum)){
    //             if(!is_Phone(param.phoneNum)){
    //                 layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
    //                 return;
    //             }
    //         }
    //     }
    // }
    page("#distributor", 10, param, "/telcertify/getTelCerDistributor", columns, columnDefs);
}


