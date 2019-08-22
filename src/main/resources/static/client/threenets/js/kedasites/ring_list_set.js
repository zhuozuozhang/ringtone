$(function () {
    $(".index2").addClass("active");
    //全选框
    $("#allCheck").click(function () {
        $("input[name='check']").prop("checked", this.checked);
    })
    //单选框
    $("input[name='check']").change(function () {
        if ($("input[name='check']").not("input:checked").size() <= 0) {
            $("#allCheck").prop("checked", true);
        } else {
            $("#allCheck").prop("checked", false);
        }
    })

    showTable();

    // 设置铃音
    $("#sear").click(function () {
        var checkArr = checkBoxIsSelected("check").toString();
        if (checkArr.length <= 0) return layer.msg('请至少选中一条!', {icon: 5, time: 1000});
        var orderId = $("#orderId").val();
        var ringId = $("#ringId").val();
        AjaxPut("/threenets/clcy/setRing", {
            phones: checkArr,
            orderId: orderId,
            id: ringId
        }, function (result) {
            if (result.code == 200) {
                layer.msg("设置成功！", {icon: 6, time: 3000});
                setTimeout(function () {
                    window.location.href = "/threenets/clcy/toRingList/" + orderId + "/"+ $("#name").html();
                }, 1000);
            } else {
                layer.alert(result.msg);
            }
        });
    });
});

function showTable() {
    var param = {
        orderId: $("#orderId").val()
    }
    var columns = [
        {"data": "id"},
        {"data": null},
        {"data": "linkMan"},
        {"data": "linkTel"},
        {"data": "province"},
        {"data": "ringName"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            var linkTel = row.linkTel;
            return "<input type='checkbox' name='check' data='" + linkTel + "'>";
        }
    }];
    page("#set", 10, param, "/threenets/clcy/getKedaChildSettingList", columns, columnDefs);
}
