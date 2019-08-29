
// 获取号码认证信息
$(document).ready(function () {
    showTelcertification_table();
});
function showTelcertification_table() {
    var param = {
        "id": $("#id").val(),
        "rangetime" : $("#rangetime").val(),
        "companyName": $("#companyName").val(),
        "tel" : $("#tel").val(),
        "phoneNum" : $("#membernum").val()
    }
    var columns = [
        {"data": "id"},
        {"data": "id"},
        {"data": "telCompanyName"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "memberNum"},
        {"data": "statusName"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "productName"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[1],
        render: function (data, type, row, meta) {
            var id = row.id;
            // return "<div class='layui-unselect layui-form-checkbox' lay-skin='primary' data-id='"+id+"'><i class='layui-icon'>&#xe605;</i></div>"
            return "<div class='layui-unselect layui-form-checkbox' lay-skin='primary' data-id='"+id+"'><i class='layui-icon layui-icon-ok'>&#xe605;</div>"
            // return "<div class='layui-unselect header layui-form-checkbox' lay-skin='primary'><i class='layui-icon'>&#xe605;</i></div>"
        }
    }, {
        targets:[9],
        render: function (data, type, row, meta) {
            var productName = row.productName;
            var product = $.parseJSON(productName);
            var service = product.service;
            var obj = JSON.stringify(service);
            var serviceJson = $.parseJSON(obj);

            var totalCost = product.totalCost;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].period0fValidity + ",</td>";
                str += "<td>" + service[i].cost + "元</td></br>";
                str += "</tr>";
            }
            return str;
        }
    }, {
        targets:[11],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<a title='查看' onclick='x_admin_show(\"详细信息\",\"/admin/telcertificationDetail/"+id+"\",\"\",\"\")' href='javascript:;'><i class='layui-icon layui-icon-survey'>&emsp;</i></a>" +
                "<a title='成员管理' onclick='x_admin_show(\"成员管理\",\"/admin/telcertificationChildList/"+id+"\",\"\",\"\")' href='javascript:;'><i class='layui-icon layui-icon-user'>&emsp;</i></a>" +
                "<a title='删除' onclick='telCertification_del(\""+id+"\")' href='javascript:;'><i class='layui-icon layui-icon-delete'></i></a>";
         }
    }];

    if(param.phoneNum != "" && param.phoneNum != null){
        if(!isTel(param.phoneNum.trim())){
            layer.msg("请输入正确的手机号码！",{icon: 0, time: 3000});
            return;
        }
    }
    page("#telcertification_table", 10, param, "/admin/getTelCerOrderList", columns, columnDefs);
}


//全选框
$('#checkAll').on('click', function () {
    if (this.checked) {
        $(this).attr('checked','checked')
        $("input[name='ckb-jobid']").each(function () {
            this.checked = true;
        });
    } else {
        $(this).removeAttr('checked')
        $("input[name='ckb-jobid']").each(function () {
            this.checked = false;
        });
    }
});

function childclick(){
    if ($(this).is(":checked") == false) {
        $("#checkAll").prop("checked", false);
    }
}


/*用户-删除*/
function telCertification_del(id){
    layer.confirm('确认要删除吗？',function(index){
        //发异步删除数据
        AjaxDelete("/admin/deleteTelCer/"+id,{},function (res) {
            if (res.code == 200 && res.data) {
                layer.msg(res.msg, {icon: 6, time: 2000});
                $('#telcertification_table').DataTable().ajax.reload();
            } else {
                layer.msg(res.msg, {icon: 5, time: 2000});
            }
        });
        // $(obj).parents("tr").remove();
        // layer.msg('已删除!',{icon:1,time:1000});
    });
}
// 批量删除号码认证订单
function delAll () {
    var data = tableCheck.getData();
    if (isNotEmpty(data)) {
        layer.confirm('确认要删除吗？'+data,function(index){
            //捉到所有被选中的，发异步进行删除
            layer.msg('删除成功', {icon: 1});
            $(".layui-form-checked").not('.header').parents('tr').remove();
        });
    }else{
        layer.msg('至少选择一条数据', {icon: 7});
    }
}