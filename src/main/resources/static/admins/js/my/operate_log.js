function showTable() {
    var columns = [
        {"data": null},
        {"data": "operateLogTitle"},
        {"data": "operateLogUser"},
        {"data": "ipAddress"},
        {"data": "operateLogLocation"},
        {"data": "operateLogType"},
        {"data": "operateLogClassify"},
        {"data": "operateLogTime","width":200},
        {"data": "operateLogStatus"},
        {"data": "operateLogUrl","width":250}
    ];
    var columnDefs = [{
        targets:[5],
        render:function (data, type, row, meta) {
            switch (data){
                case 0:
                    return "其他";
                    break;
                case 1:
                    return "新增";
                    break;
                case 2:
                    return "修改";
                    break;
                case 3:
                    return "删除";
                    break;
                default:
                    return "其他";
            }
        }
    },{
        targets:[6],
        render:function (data, type, row, meta) {
            //0.其它/1.三网/2号码认证/3.科大网站/4.公众号/5.管理端
            switch (data){
                case 0:
                    return "其他";
                    break;
                case 1:
                    return "三网";
                    break;
                case 2:
                    return "号码认证";
                    break;
                case 3:
                    return "科大网站";
                    break;
                case 4:
                    return "公众号";
                    break;
                case 5:
                    return "管理端";
                    break;
                default:
                    return "其他";
            }
        }
    },{
        targets:[8],
        render:function (data, type, row, meta) {
            switch (data){
                case 0:
                    return "正常";
                    break;
                case 1:
                    var errorMessage = row.errorMsg;
                    return "<span style='color: red;' title='"+errorMessage+"'>异常</span>";
                    break;
                default:
                    return "正常";
            }
        }
    },{
        targets:[10],
        render:function (data, type, row, meta) {
            var id = row.operateLogId;
            return " <a title=\"查看\" onclick=\"x_admin_show('详细信息','/admin/toOperateLogDetailPage/"+id+"',800,700)\" href=\"javascript:;\">"
                + "<i class=\"layui-icon\">&#xe63c;</i></a>";
        }
    }];
    page("#example", 16, {}, "/admin/findAllOperateLog", columns, columnDefs);
}