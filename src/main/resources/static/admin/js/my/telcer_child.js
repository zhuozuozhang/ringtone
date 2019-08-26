// 管理端号码认证成员信息
$(document).ready(function(){
    showTelcertification_child_table();
});
function showTelcertification_child_table() {
    var param = {
        "parentId": $("#id").val(),
        "phoneNum": $("#phoneNum").val()
    }
    var columns = [
        {"data": "id"},
        {"data": "id"},
        {"data": "telChildOrderPhone"},
        {"data": "years"},
        {"data": "price"},
        {"data": "telChildOrderStatus"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderOpenTime"},
        {"data": "telChildOrderExpireTime"}
    ];
    var columnDefs = [{
        targets:[1],
        render: function (data, type, row, meta) {
            var id = row.id;
            // return "<div class='layui-unselect layui-form-checkbox' lay-skin='primary' data-id='"+id+"'><i class='layui-icon'>&#xe605;</i></div>"
            return "<div class=\"layui-unselect layui-form-checkbox\" lay-skin=\"primary\" data-id='1'><i class=\"layui-icon\">&#xe605;</i></div>"
            // return "<div class='layui-unselect header layui-form-checkbox' lay-skin='primary'><i class='layui-icon'>&#xe605;</i></div>"
        }
    }, {
        targets: [5],
        render: function (data, type, row, meta) {
            var status = row.telChildOrderStatus;
            // var str = "<select id='selector' onchange='updateTelCertificationStatus(this);'>" +
            //     "<option id='1' value='1'>开通中</option>" +
            //     "<option id='2' value='2'>开通成功</option>" +
            //     "<option id='3' value='3'>开通失败</option>" +
            //     "<option id='4' value='4'>续费中</option>" +
            //     "<option id='5' value='5'>续费成功</option>" +
            //     "<option id='6' value='6'>续费失败</option>" +
            //     "</select>"
            // display(status);
            var str = "<select id='selector' name='selector' onchange='updateTelCertificationStatus(this);'>" +
                "<option value='1'>开通中</option>" +
                "<option value='2'>开通成功</option>" +
                "<option value='3'>开通失败</option>" +
                "<option value='4'>续费中</option>" +
                "<option value='5'>续费成功</option>" +
                "<option value='6'>续费失败</option>" +
                "</select>"
            var selector = $("#selector").option;
            // alert(selector);
            // initSelectedValue(selector,status);
            return str;
        }
    }, {
        targets: [6],
        render: function (data, type, row, meta) {
            if (data != null && data != "") {
                return data;
            } else {
                return "<input onchange='editFeedBackWhenMyKeyUp("+row.id+",this.value)' type=\"text\" id=\"businessFeedback\" name=\"businessFeedback\" required lay-verify=\"businessFeedback\" autocomplete=\"off\" class=\"layui-input\">";
            }
        }
    }, {
        targets: [10],
        render: function (data, type, row, meta) {
            var id = row.id;
            var phoneNum = row.telChildOrderPhone;
            return "<a title='费用支出记录' onclick='x_admin_show(\"费用支出记录\",\"/admin/getTheTelcerConsumeLog/" + phoneNum + "\",\"\",\"\")' href='javascript:;\'><i class='layui-icon layui-icon-survey'>&emsp;</i></a>" +
                "<a title='删除' onclick='telCertification_del(\""+id+"\")' href='javascript:;\'><i class='layui-icon'>&#xe640;</i></a>";
        }
    }];

    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum.trim())){
            layer.msg("请输入正确的成员手机号码！",{icon: 0, time: 3000});
            return;
        }
    }
    var url = "/admin/getTelcertificationChildList";
    page("#telcertification_child_table", 10, param, url, columns, columnDefs);
}
function initSelectedValue(selObj, val) {
    var i;
    for (i = 0; i < selObj.options.length; i++) {
        if (selObj.options[i].value == val) {    // 选项值与变量相同
            selObj.selectedIndex = i;            // 设置下拉选择框的选中索引号
            break;
        }
    }
}
//修改业务反馈
function editFeedBackWhenMyKeyUp(id,feedBack){
    layer.confirm('确认要修改吗？', {
        btn: ['确定', '取消']
    }, function () {
        AjaxPost("/admin/editFeedBackById",{
            id:id,
            businessFeedback:feedBack
        },function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#telcertification_child_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
    }, function () {

    });
}

var a = $("#selector option:selected").val();

function updateTelCertificationStatus(obj) {
    layer.confirm('确认要修改吗？', {
        btn: ['确定', '取消']
    }, function () {
        var status = $(obj).val();
        // AjaxPost("/admin/editChildStatus",{
        //
        // })
        alert($(obj).val());
        layer.msg('修改成功!', {icon: 1, time: 1000});
    }, function () {
        $(obj).val(a);
    });
}

/*用户-删除*/
function telCertification_del(id) {
    layer.confirm('确认要删除吗？', function (index) {
        //发异步删除数据
        AjaxDelete("/admin/deleteTelCerChild/"+id,{},function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#telcertification_child_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
    });
}

// 批量删除号码认证订单
function delAll() {
    var data = tableCheck.getData();
    if (isNotEmpty(data)) {
        layer.confirm('确认要删除吗？' + data, function (index) {
            //捉到所有被选中的，发异步进行删除
            layer.msg('删除成功', {icon: 1});
            $(".layui-form-checked").not('.header').parents('tr').remove();
        });
    } else {
        layer.msg('至少选择一条数据', {icon: 7});
    }
}