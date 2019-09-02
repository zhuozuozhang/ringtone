$(document).ready(function () {
   showRechargeLog();
});

function showRechargeLog() {
    var param = {
    }
    var columns = [
        {"data": null},
        {"data": "userId"},
        {"data": "userName"},
        {"data": "rechargePrice"},
        {"data": "rechargeMoney"},
        {"data": "rechargeTime"},
        {"data": "rechargeTypeName"},
        {"data": "rechargeOperator"},
        {"data": "rechargeRemark"}
    ];
    var columnDefs = [

    ];
    var url = "/admin/getRechargeLogList";
    page("#recharge", 2, param, url, columns, columnDefs);
}