//获取列表
function showTable() {
    var id = $("#id").val();
    // alert("js中的"+id); //获取到了33
    var param = {
        "id": id
    }
    var columns = [
        {"data": "type"},
        {"data": "status"},
        {"data": "remark"},
        {"data": "ctime"}
    ];
    var columnDefs = [{
        targets:[4],
        render: function (data, type, row, meta) {
            var id = row.id;
            // var name = row.companyName;
            return "<a href='/threenets/toMerchantsPhonePage/" + id + "'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
        }
    }];
    var url = "/threenets/findCricleMsgList/"+id;
    page("#set", 15, param, url , columns, columnDefs);
    // page("#set", 15, param, "/threenets/getThressNetsOrderList", columns, columnDefs);
}
