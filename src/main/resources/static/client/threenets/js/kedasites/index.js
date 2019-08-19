//分页
$(document).ready(function () {
    $(".index1").addClass("active");
    showTable();
});
function showTable() {
    var params = {
        "isMonthly": $('#isMonthly').val(),
        "timeType": $('#timeType').val(),
    }
    var columns = [
        {"data": "id"},
        {"data": "linkTel"},
        {"data": "linkMan"},
        {"data": "companyName"},
        {"data": "province"},
        {"data": "createTime"},
        {"data": "ringName"},
        {"data": "isMonthly"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [2],
        render: function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [3],
        render: function (data, type, row, meta) {
            var id = row.orderId;
            var name = row.companyName;
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'><a href='/threenets/clcy/toNumberList/" + id + "/"+name+"'>" + name + "</a></div>";
        }
    }, {
        targets: [6],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data) ? data : "";
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    }, {
        targets: [7],
        render: function (data, type, row, meta) {
            // 0.未开通/1.开通成功/2.开通失败/3.已退订/4.开通中/5.删除中/6.删除失败
            var status = '';
            if (data == 0) {
                status = '未包月';
            } else if (data == 1) {
                status = '已包月';
            } else if (data == 2) {
                status = '包月失败';
            } else if (data == 3) {
                status = '已退订';
            } else if (data == 4) {
                status = '包月中';
            } else if (data == 5) {
                status = '删除中';
            } else {
                status = '删除失败';
            }
            return status;
        }
    },  {
        targets: [8],
        render: function (data, type, row, meta) {
            if (isNotEmpty(data)) {
                return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
            } else {
                return "";
            }
        }
    }, {
        targets: [9],
        render: function (data, type, row, meta) {
            var id = row.orderId;
            var name = row.companyName;
            return "<a href='/threenets/clcy/toNumberList/" + id + "/"+name+"'><i class='layui-icon layui-icon-user' title='进入商户'></i></a>"
        }
    }]
    page("#set", 10, params, "/threenets/clcy/getKeDaChildOrderBacklogList", columns, columnDefs);
}
//公告详情
function openDetails(notice) {
    var date = notice.noticeTime.toString().substring(0, 10);
    layer.open({
        type: 1,
        title: '公告详情',
        area: ['860px', '500px'],
        content: '<div class="content clearfix" style="width: 800px; line-height: 25px; margin-left: 20px;"><div class="articleTitle font04">' + notice.noticeTitle + '</div><div class="articleTitle_details">' + date + '</div><div>' + notice.noticeContent + '</div></div>'
    });
}