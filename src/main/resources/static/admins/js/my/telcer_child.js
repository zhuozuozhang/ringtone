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
        targets: [4],
        render: function (data, type, row, meta) {
            var pgmt = "";
            pgmt += '<option value = "1" ';
            if(data == 1 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '开通中</option>';
            pgmt += '<option value = "2" ';
            if(data == 2 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '开通成功</option>';
            pgmt += '<option value = "3" ';
            if(data == 3 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '开通失败</option>';
            pgmt += '<option value = "4" ';
            if(data == 4 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '续费中</option>';
            pgmt += '<option value = "5" ';
            if(data == 5 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '续费成功</option>';
            pgmt += '<option value = "6" ';
            if(data == 6 ){
                pgmt += 'selected = "selected" >';
            }else{
                pgmt += '>';
            }
            pgmt += '续费失败</option>';
            var id = row.id;
            var str = "<select id='selector' name='selector' onchange='updateTelCertificationStatus(this,"+id+");'>"+
                pgmt+
                "</select>";
            return str;
        }
    }, {
        targets: [5],
        render: function (data, type, row, meta) {
            return "<input onchange='editFeedBackWhenMyKeyUp("+row.id+",this.value)' value='"+data+"' type=\"text\" id=\"businessFeedback\" name=\"businessFeedback\" required lay-verify=\"businessFeedback\" autocomplete=\"off\" class=\"layui-input\">";
        }
    }, {
        targets: [9],
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

// 修改状态
function updateTelCertificationStatus(selObj,id) {
    layer.confirm('确认要修改吗？', {
        btn: ['确定', '取消']
    }, function () {
        var status = $(selObj).val();
        AjaxPost("/admin/editChildStatus",{
            id:id,
            telChildOrderStatus:status
        },function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#telcertification_child_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
    }, function () {
        $('#telcertification_child_table').DataTable().ajax.reload();
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