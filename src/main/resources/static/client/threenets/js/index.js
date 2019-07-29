$(function () {
    //一进页面公告弹窗
    layer.open({
        type: 2,
        title: '公告',
        area: ['850px', '500px'],
        content: '/threenets/threeNetsAnnunciate'
    });
    $(".index1").addClass("active");
});

function showTable() {
    var params = {
        "isMonthly" : $('#memberStatus option:selected').val(),
        "timeType" : $('#timeType option:selected').val(),
    }
    var columns = [
        {"data": "id"},
        {"data": "linkmanTel"},
        {"data": "linkman"},
        {"data": "companyLinkman"},
        {"data": "province"},
        {"data": "createDate"},
        {"data": "ringName"},
        {"data": "isMonthly"},
        {"data": "isVideoUser"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[2],
        render:function (data,type,row,meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets: [3],
        render: function (data, type, row, meta) {
            var id = row.parentOrderId;
            var name = row.companyLinkman;
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'><a href='/threenets/toMerchantsPhonePage/" + id + "'>"+name+"</a></div>";
        }
    },{
        targets:[6],
        render:function (data,type,row,meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets:[7],
        render:function (data,type,row,meta) {
            if (data == 1){
                return '未包月'
            } else if(data == 2){
                return '已包月'
            }else{
                return '已退订'
            }
        }
    },{
        targets:[8],
        render:function (data,type,row,meta) {
            return data?'是':'否';
        }
    },{
        targets:[9],
        render:function (data,type,row,meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets: [10],
        render: function(data, type, row, meta) {
            var id = row.parentOrderId;
            return "<a href='/threenets/toMerchantsPhonePage/" + id + "'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
        }
    }]
    page("#set",10,params,"/threenets/getThreeNetsTaskList",columns,columnDefs);
}
//公告详情
function openDetails(notice){
    var date = notice.noticeTime.toString().substring(0,10);
    layer.open({
        type: 1,
        title: '公告详情',
        area: ['860px', '500px'],
        content: '<div class="content clearfix" style="width: 800px; line-height: 25px; margin-left: 20px;"><div class="articleTitle font04">'+notice.noticeTitle+'</div><div class="articleTitle_details">'+date+'</div><div>'+notice.noticeContent+'</div></div>'
    });
}