function showTelcertification_child_table() {
    var param = {
        "parentId": $("#id").val(),
    }
    var columns = [
        {"data": null},
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
        targets:[5],
        render: function(data, type, row, meta){
            var status = row.telChildOrderStatus;
            var str = "<select id='selector' onchange='updateTelCertificationStatus(this);'>" +
                "<option value='1'>开通中</option>" +
                "<option value='2' selected>开通成功</option>" +
                "<option value='3'>开通失败</option>" +
                "<option value='4'>续费中</option>" +
                "<option value='5'>续费成功</option>" +
                "<option value='6'>续费失败</option>" +
                "</select>"
            return str;
        }
    },{
        targets:[6],
        render: function(data, type, row, meta){
            if(data != null && data != ""){
                return data;
            }else{
                return "<span>暂无</span>";
            }
        }
    }, {
        targets:[10],
        render: function (data, type, row, meta) {
            var id = row.id;
            var phoneNum = row.telChildOrderPhone;
            return "<a title='费用支出记录' onclick='x_admin_show(\"费用支出记录\",\"/admin/getTheTelcerConsumeLog/"+phoneNum+"\",\"\",\"\")' href='javascript:;\'><i class='layui-icon'>&#xe60e;</i></a>" +
                "<a title='删除' onclick='telCertification_del(this,\""+phoneNum+"\")' href='javascript:;\'><i class='layui-icon'>&#xe640;</i></a>";
        }
    }];
    var url = "/admin/getTelcertification_child_list";
    page("#telcertification_child_table", 10, param, url, columns, columnDefs);
}


var a = $("#selector option:selected").val();
function updateTelCertificationStatus(obj){
    layer.confirm('确认要修改吗？', {
        btn: ['确定', '取消']
    },function(){
        alert($(obj).val());
        layer.msg('修改成功!',{icon:1,time:1000});
    },function(){
        $(obj).val(a);
    });
}

/*用户-删除*/
function telCertification_del(obj,id){
    layer.confirm('确认要删除吗？',function(index){
        //发异步删除数据
        $(obj).parents("tr").remove();
        layer.msg('已删除!',{icon:1,time:1000});
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