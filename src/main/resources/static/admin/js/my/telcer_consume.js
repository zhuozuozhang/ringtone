/**
 * 获取管理端消费记录
 */
function showTelcertification_consume_log() {
    var param = {
        "phoneNum": $("#phoneNum").val()
    }
    var columns = [
        {"data": "id"},
        {"data": "userName"},
        {"data": "userTel"},
        {"data": "telConsumeLogType"},
        {"data": "telConsumeLogStatus"},
        {"data": "telconsumeLogPrice"},
        {"data": "telConsumeLogCtime"},
        {"data": "telConsumeLogOpenTime"},
        {"data": "telConsumeLogExpireTime"}
    ];
    var columnDefs = [{
        targets:[4],
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
        targets:[5],
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
    page("#telcertification_consume_log", 15, param, "/admin/getTelcertification_consume_log", columns, columnDefs);
}