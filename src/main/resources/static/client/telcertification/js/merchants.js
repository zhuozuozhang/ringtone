$(document).ready(function () {
    showTelCerTable();
});

//tab切换
//我的订单

function telCerTable() {
    showTelCerTable();
    $('#myorder').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "block";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}
//即将到期号码
function fallDueTable() {
    showFallDueTable();
    $('#myorder2').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "block";
    document.getElementsByClassName('net_middle2')[0].style.display = "none";
}

//到期号码
function dueTable() {
    showDueTable();
    $('#myorder3').addClass('active').siblings().removeClass('active');
    document.getElementsByClassName('myorder')[0].style.display = "none";
    document.getElementsByClassName('net_middle')[0].style.display = "none";
    document.getElementsByClassName('net_middle2')[0].style.display = "block";
}
// 获得订单列表-->我的订单
function showTelCerTable() {
    var param = {
        "id": $("#id").val(),
        "rangetime" : $("#rangetime").val(),
        "telCompanyName": $("#companyName").val().trim(),
        "telLinkPhone" : $("#tel").val().trim(),
        "phoneNum" : $("#membernum").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telCompanyName"},
        {"data": "telContent"},
        {"data": "telLinkName"},
        {"data": "telLinkPhone"},
        {"data": "memberNum"},
        // {"data": "statusName"},
        {"data": "unitPrice"},
        {"data": "telOrderTime"},
        {"data": "productName"},
        {"data": "remark"}
    ];
    var columnDefs = [{
        targets:[8],
        render: function (data, type, row, meta) {
            var productName = row.productName;
            var product = $.parseJSON(productName);
            var service = product.service;
            var str = "";
            for (var i = 0; i < service.length; i++) {
                str += "<tr>";
                str += "<td>" + service[i].name + ",</td>";
                str += "<td>" + service[i].periodOfValidity + ",</td>";
                str += "<td>" + service[i].cost + "元</td></br>";
                str += "</tr>";
            }
            return str;
        }
    }, {
        targets:[10],
        render: function (data, type, row, meta) {
            var id = row.id;
            return "<i class='layui-icon layui-icon-edit' title='编辑' onclick='editTelCerOrder("+id+");'></i>"
                + "<i class='layui-icon layui-icon-log' title='详情' onclick='ckeckDetails("+id+");'></i>"
                + "<a href='/telcertify/toTelMembersPage/"+id+"'><i class='layui-icon layui-icon-username' title='成员管理'></i></a>";
        }
    }];
    page("#merchants", 10, param, "/telcertify/getTelCerOrderList", columns, columnDefs);
}
// 获取订单列表-->即将到期列表
function showFallDueTable() {
    var param = {
        "phoneNum": $("#number").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        {"data": "years"},
        {"data": "price"},
        {"data": "statusName"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderExpireTime"}
    ];
    var columnDefs = [{
        targets: [5],
        render: function (data, type, row, meta) {
            if (data != null && data != "") {
                return data;
            } else {
                return "<div>暂无<div/>";
            }
        }
    },{
        targets:[8],
        render: function (data, type, row, meta) {
            var phoneNumber = row.telChildOrderPhone;
            var id = row.id;
            return "<p><i class='layui-icon layui-icon-log' title='查看详情' onclick='ckeckDetailsOne("+id+");'></i></p>";
        }
    }];
    page("#fall_due_table", 10, param, "/telcertify/getFallDueList", columns, columnDefs);
}

//获取订单列表-->已经到期列表
function showDueTable() {
    var param = {
        "phoneNum": $("#number2").val().trim()
    }
    var columns = [
        {"data": null},
        {"data": "telChildOrderPhone"},
        {"data": "years"},
        {"data": "price"},
        {"data": "statusName"},
        {"data": "businessFeedback"},
        {"data": "telChildOrderCtime"},
        {"data": "telChildOrderExpireTime"}
        // {"data": "remark"}
    ];
    var columnDefs = [{
        targets: [5],
        render: function (data, type, row, meta) {
            if (data != null && data != "") {
                return data;
            } else {
                return "<span>暂无</span>";
            }
        }
    },{
        targets:[8],
        render: function (data, type, row, meta) {
            var phoneNumber = row.telChildOrderPhone;
            var id = row.id;
            return "<p><i class='layui-icon layui-icon-log' title='查看详情' onclick='ckeckDetailsOne("+id+");'></i></p>";
        }
    }];
    page("#due_table", 10, param, "/telcertify/getDueList", columns, columnDefs);
}

//日期时间范围
layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#rangetime',
        range: true
    });
})

//我的订单查看详情
function ckeckDetails(id) {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetailsPage/'+id,
        area: ['500px', '680px']
    });
}

//即将到期和到期号码查看详情
function ckeckDetailsOne(id) {
    layer.open({
        type: 2,
        title: '详情',
        content: '/telcertify/toTeldetailsOnePage/'+id,
        area: ['500px', '680px']
    });
}
//添加商户
function addMerchant() {
    layer.open({
        type:2,
        title:'添加商户',
        content:'/telcertify/toTelAddPage',
        area:['650px','650px']
    });
}
// ---------------------------------------------------修改弹窗--回显-----------------------------------------------------
//打开修改订单弹窗--回显
function editTelCerOrder(id) {
    layer.open({
        type: 2,
        title: '更改商户信息',
        content: '/telcertify/toTelEditPage/'+id,
        area: ['30%', '60%']
    });
}




