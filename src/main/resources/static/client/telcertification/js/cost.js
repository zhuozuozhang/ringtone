function showTelCerCostTable() {
    var param = {
        "telConsumeLogId": $("#telConsumeLogId").val()
    }
    var columns = [
        {"data": null},
        {"data": "userTel"},
        {"data": "telConsumeLogType"},
        {"data": "telConsumeLogStatus"},
        {"data": "telconsumeLogPrice"},
        {"data": "telConsumeLogTime"},
        {"data": "telConsumeLogOpenTime"}
    ];
    var columnDefs = [{

    }];
    page("#cost", 15, param, "/telcertify/getTelCerCostLogList", columns, columnDefs);
}