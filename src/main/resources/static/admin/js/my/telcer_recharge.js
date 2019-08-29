$(document).ready(function () {
   showRechargeLog();
});

function showRechargeLog() {
    var param = {
        // "parentId": $("#id").val(),
    }
    var columns = [
        {"data": null},
        {"data": "userName"},
        {"data": "userTel"},
        {"data": "price"},
        {"data": "rechargeTime"},
        {"data": "operatorId"},
        {"data": "remark"},
    ];
    var columnDefs = [

    ];
    var url = "/admin/getTelCerRechargeLogList";
    page("#recharge", 2, param, url, columns, columnDefs);
}