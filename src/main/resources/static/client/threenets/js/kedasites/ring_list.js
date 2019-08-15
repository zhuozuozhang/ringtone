//分页
$(document).ready(function () {
    $(".index2").addClass("active");
    showTable();
});
function showTable() {
    var params = {
        "orderId": $('#orderId').val(),
    }
    var columns = [
        {"data": "id"},
        {"data": "ringName"},
        {"data": "ringStatus"},
        {"data": "ringUrl"},
        {"data": "createTime"}
    ];
    var columnDefs = [{
        targets: [1],
        render: function (data, type, row, meta) {
            data = isNotEmpty(data)?data:"";
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets:[2],
        render:function (data, type, row, meta) {
            // 铃音状态（1.待审核/2.激活中/3.激活成功/4.激活失败）
            if (data ==1){
                return "待审核";
            } else if(data ==2){
                return "激活中";
            } else if(data ==3){
                return "激活成功";
            } else {
                return "激活失败";
            }
        }
    },{
        targets:[3],
        render:function (data, type, row, meta) {
            return "<span onclick='openPlayer(\""+data+"\");'>试听</span>";
        }
    },{
        targets: [5],
        render: function (data, type, row, meta) {
            var id = row.id;
            var ringStatus = row.ringStatus;
            var setRing = "";
            if(ringStatus == 3 ){
                setRing = "<a href='/threenets/toSetingRing/'><i class='layui-icon layui-icon-set' title='设置铃音'></i></a>";
            }
            return "<i class='layui-icon' title='查看广告词' onclick='advertising(\"" + row.ringContent + "\");'><img src='/client/threenets/images/see.png'></i>"
                + setRing + '<i class="layui-icon layui-icon-delete" title="删除" onclick="deleteRing(' + id + ')"></i>';
        }
    }];
    page("#set", 1, params, "/threenets/clcy/getKedaRingList", columns, columnDefs);
}

//上传铃音
function AddUser() {
    layer.open({
        type: 2,
        title: '上传铃音',
        area: ['650px', '550px'],
        content: 'Addring.html'
    });
}

//在线试听
function openPlayer(ringUrl) {
    layer.open({
        type: 1,
        title: '在线试听',
        area: ['800px', '560px'],
        content: '<video id="ovideo" autoplay loop src="'+ringUrl+'" controls="controls " allowfullscreen="true" quality="high" width="800px" height="514px" align="middle" allowscriptaccess="always" flashvars="isAutoPlay=true" type="application/x-shockwave-flash"></video>'
    });
}

//查看广告词
function advertising(ringContent) {
    layer.open({
        title: '广告词',
        content: ringContent
    });
}