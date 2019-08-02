$(".index5").addClass("active");
var columns = [
    {"data": "id"},
    {"data": "userName"},
    {"data": "parentUserName"},
    {"data": "userEmail"},
    {"data": "userTel"},
    {"data": "province"},
    {"data": "userTime"},
    {"data": "id"},
];
var columnDefs = [{
    targets: [8],
    render: function(data, type, row, meta) {
        var id = row.id;
        console.log(id);
        return "<i class='layui-icon layui-icon-home' title='进入' onclick='toIndexPage("+id+");'></i>"
            +"<i class='layui-icon layui-icon-edit' title='重置密码' onclick='editMerchants("+id+");'></i>";
    }
}];

//子账号切换
function openLower(obj) {
    if (obj == 1) {
        $("#Alower").removeClass("seled");
        $("#lower").addClass("seled");
    } else if (obj == 2) {
        $("#lower").removeClass("seled");
        $("#Alower").addClass("seled");
    }
    page("#set",15,{
        type:obj
    },"/threenets/getChildAccountList",columns,columnDefs);
}
//添加子账号
function AddUser() {
    layer.open({
        type: 2,
        title: '添加子账号',
        content: '/threenets/toChildAccountAddPage',
        area: ['600px', '680px']
    });
}
//重置密码
function editMerchants(id) {
    layer.confirm('确认要重置密码为‘sg123456’吗？',function(index){
        AjaxPut("/admin/updateUserPassword/"+id,{},function (res) {
            if (res.data) {
                layer.msg('重置密码成功!',{icon:1,time:1000});
            }else{
                layer.msg(res.msg,{icon: 6,time:1000});
            }
        });
    });
}
// 进入子账号
function toIndexPage(id) {
    window.location.href="/threenets/enterSubUser/"+id;
}