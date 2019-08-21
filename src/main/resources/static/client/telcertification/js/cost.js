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
        targets:[2],
        render: function (data, type, row, meta) {
            var status = row.telConsumeLogType;
            //号码认证消费记录类型（1.首次/2.续费）
            if(status == 1){
                return "<span>首次</span>";
            }else if(status == 2){
                return "<span>续费</span>";
            }else{
                return "<span>未知</span>";
            }
        }

    },{
        targets:[3],
        render: function(data, type, row, meta){
            var status = row.telConsumeLogStatus;
            //号码认证消费记录状态（1.开通中/2.开通成功/3.开通失败/4.续费中/5.续费成功/6.续费失败）
            if(status == 1){
                return "<span>开通中</span>";
            }else if(status == 2){
                return "<span>开通成功</span>";
            }else if(status == 3){
                return "<span>开通失败</span>";
            } else if(status == 4){
                return "<span>续费中</span>";
            }else if(status == 5){
                return "<span>续费成功</span>";
            }else if(status == 6){
                return "<span>续费失败</span>";
            }else{
                return "<span>未知</span>";
            }
        }
    }];
    page("#cost", 15, param, "/telcertify/getTelCerCostLogList", columns, columnDefs);
}