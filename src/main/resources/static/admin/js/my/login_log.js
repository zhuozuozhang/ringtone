function showTable() {
    var columns = [
        {"data": null},
        {"data": "loginLogUsername"},
        {"data": "ipAdress"},
        {"data": "loginLocation"},
        {"data": "loginLogTime"},
        {"data": "loginLogStatus"}
    ];
    var columnDefs = [];
    page("#example", 16, {}, "/admin/findAllLoginLog", columns, columnDefs);
}