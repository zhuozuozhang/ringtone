//分页
$(document).ready(function () {
    $(".index1").addClass("active");
    //发布日期
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        //日期范围
        laydate.render({
            elem: '#start'
        });
        //日期范围
        laydate.render({
            elem: '#end'
        });
    });
    showTable();
});

function showTable() {
    var params = {
        noticeTitle: $("#noticeTitle").val(),
        start: $("#start").val(),
        end: $("#end").val(),
        noticeModule: 8,
        noticeStatus:true
    };
    var columns = [
        {"data": "noticeId"},
        {"data": "noticeTitle"},
        {"data": "noticeTime"}];

    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            var param = {
                noticeTitle:row.noticeTitle,
                noticeTime:row.noticeTime,
                noticeContent:row.noticeContent
            };
            return "<div onclick='openDetails(" + JSON.stringify(param) + ");' style='cursor: pointer;'>" + data + "</div>";
        }
    },{
        targets:[2],
        render:function (data, type, row, meta) {
            var date =data.toString().substring(0, 10);
            return date;
        }
    }];
    page("#set", 10, params, "/threenets/clcy/getNoticeList", columns, columnDefs);
}


//公告详情
function openDetails(notice) {
    var date = notice.noticeTime.toString().substring(0, 10);
    layer.open({
        type: 1,
        title: '公告详情',
        area: ['850px', '400px'],
        content: '<div class="content clearfix" style="width: 800px; line-height: 25px; margin-left: 20px;"><div class="articleTitle font04">'+notice.noticeTitle+'</div><div class="articleTitle_details">'+date+'</div><div>'+notice.noticeContent+'</div></div>'
    });
}