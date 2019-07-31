
$(function () {
    // 设置铃音
    $("#sear").click(function () {
        var checkArr = checkBoxIsSelected("check").toString();
        if (checkArr.length <= 0) return layer.msg('请至少选中一条!', {icon: 5, time: 1000});
        var orderId = $("#orderId").val();
        var operate = $("#operate").val();
        var ringId = $("#ringId").val();
        AjaxPut("/threenets/setRing",{
            phones:checkArr,
            orderId:orderId,
            operate:operate,
            id:ringId
        },function (result) {
            if (result.code == 200){
                layer.msg("设置成功！", {icon: 6, time: 3000});
                setTimeout(function () {
                    window.location.href = "/threenets/toMerchantsRingPage/"+orderId;
                },1000);
            }else{
                layer.msg(result.msg, {icon: 5, time: 3000});
            }
        });
    });
});

function showTable() {
    var param = {
        orderId:$("#orderId").val(),
        operate:$("#operate").val()
    }
    var columns = [
        {"data": "id"},
        {"data": null},
        {"data": "linkman"},
        {"data": "linkmanTel"},
        {"data": "province"},
        {"data": "ringName"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            var linkmanTel = row.linkmanTel;
            return "<input type='checkbox' name='check' data-phone='"+linkmanTel+"'>";
        }
    }];
    page("#set", 10, param, "/threenets/getThreeNetsChidOrderSetingList", columns, columnDefs);
}

//全选框
$("#allCheck").click(function(){
    $("input[name='check']").prop("checked",this.checked);
});
//单选框
$("input[name='check']").change(function(){
    if($("input[name='check']").not("input:checked").size() <= 0){
        $("#allCheck").prop("checked",true);
    }else{
        $("#allCheck").prop("checked",false);
    }
});