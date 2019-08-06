function showTable() {
    var columns = [
        {"data": null},
        {"data": "operateLogTitle"},
        {"data": "operateLogUser"},
        {"data": "ipAddress"},
        {"data": "operateLogLocation"},
        {"data": "operateLogType"},
        {"data": "operateLogClassify"},
        {"data": "operateLogTime"},
        {"data": "operateLogStatus"},
        {"data": "operateLogUrl","width":250}
    ];
    var columnDefs = [{
        targets:[10],
        render:function (data, type, row, meta) {
            var id = row.operateLogId;
            return "<a title='查看' onclick='x_admin_show('详细信息','/admin/toOperateLogDetailPage/"+id+"',800,700)'>"
                + "<i class='layui-icon'>&#xe63c;</i></a>"
        }
    }];
    page("#example", 10, {}, "/admin/findAllOperateLog", columns, columnDefs);
}