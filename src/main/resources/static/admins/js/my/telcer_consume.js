/**
 * 获取管理端消费记录
 */
$(document).ready(function() {
    showTelcertification_consume_log();
});
function showTelcertification_consume_log() {
    var param = {
        "phoneNum": $("#phoneNum").val()
    }
    var columns = [
        {"data": "id"},
        {"data": "consumePrice"},
        {"data": "consumeMoney"},
        {"data": "consumeTime"},
        {"data": "consumeTypeName"},
        {"data": "consumeOperator"},
        {"data": "userName"},
        {"data": "userTel"},
        {"data": "userId"},
        {"data": "consumeRemark"}
    ];
    var columnDefs = [];
    page("#telcertification_consume_log", 15, param, "/admin/getConsumeLogList", columns, columnDefs);
}