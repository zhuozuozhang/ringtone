function showNoticeTable() {
    var columns = [
        {"data": null},
        {"data": "noticeTitle"},
        {"data": "noticeTime","width":140},
        {"data": "noticeAuthor"},
        {"data": "noticeWeight"},
        {"data": "noticeModule","width":150},
        {"data": "noticeStatus"}
    ];
    var columnDefs = [{
        targets:[1],
        render:function (data, type, row, meta) {
            return "<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:150px;' title='" + data + "'>" + data + "</div>";
        }
    },{
        targets:[5],
        render:function (data, type, row, meta) {
            if (data == 0){
                return "企业彩铃/视频彩铃";
            } else if (data == 1){
                return "电话认证/挂机短信/彩印";
            } else if (data == 2){
                return "400电话";
            } else if (data == 3){
                return "企业秀";
            } else if (data == 4){
                return "网站建设/万词霸屏/快排优化";
            } else if (data == 5){
                return "微信/百度/抖音小程序";
            } else if (data == 6){
                return "铃音录制/视频制作";
            } else if (data == 7){
                return "流量卡/号卡";
            } else {
                return "疑难杂单";
            }
        }
    },{
        targets:[6],
        render:function (data, type, row, meta) {
            var noticeId = row.noticeId;
            if (data){
                return "<span class='layui-btn layui-btn layui-btn-sm' onclick='notice_status(this,"+noticeId+");' title='是'>是</span>";
            }else{
                return "<span class='layui-btn layui-btn layui-btn-sm layui-btn-danger' onclick='notice_status(this,"+noticeId+");' title='否'>否</span>";
            }
        }
    },{
        targets: [7],
        render: function (data, type, row, meta) {
            var noticeId = row.noticeId;
            return "<a title='查看' onclick='x_admin_show(\"详细信息\",admins"+noticeId+"",800,570);' href='javascript:;'><i class='layui-icon'>&#xe63c;</i></a>"
                +"<a title='编辑' onclick='changeNotice(\"编辑\"admins"+noticeId+"\",800,570)' href='javascript:;'><i class=\"layui-icon\">&#xe642;</i></a>"
                +"<a title='删除' onclick='notice_del(this,"+noticeId+");' href='javascript:;'><i class='layui-icon'>&#xe640;</i></a>";
        }
    }];
    page("#notice_table", 10, {}, "/admins/getNoticeList", columns, columnDefs);
}
// 状态设置
function notice_status(obj, id) {
    if ($(obj).attr('title') == '是') {
        layer.confirm('确认要禁用吗？', function (index) {
            AjaxPut("/admins/updataNotiecStatus/"+id,{
                noticeStatus:false
            },function (res) {
                if (res.code == 200 && res.data){
                    layer.msg('已禁用!', {icon: 5, time: 1000});
                    $('#notice_table').DataTable().ajax.reload(null,false);
                }else{
                    layer.msg('执行修改公告状态出错!', {icon: 5, time: 1000});
                }
            });
        });
    } else {
        layer.confirm('确认要启用吗？', function (index) {
            AjaxPut("/admins/updataNotiecStatus/"+id,{
                noticeStatus:true
            },function (res) {
                if (res.code == 200 && res.data){
                    layer.msg('已启用!', {icon: 6, time: 1000});
                    $('#notice_table').DataTable().ajax.reload(null,false);
                }else{
                    layer.msg('执行修改公告状态出错!', {icon: 5, time: 1000});
                }
            });
        });
    }
}
// 修改公告信息
function changeNotice(title, url, w, h) {
    layer.open({
        type: 2,
        area: [w + 'px', h + 'px'],
        fix: false,
        maxmin: true,
        shadeClose: true,
        shade: 0.4,
        title: title,
        content: url,
        end:function(){
            $('#notice_table').DataTable().ajax.reload(null,false);
        }
    });
}
/*公告-删除*/
function notice_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        AjaxDelete("/admins/deleteNotice/"+id,'',function (res) {
            if (res.code = 200 && res.data){
                //发异步删除数据
                $(obj).parents("tr").remove();
                layer.msg('删除成功!', {icon: 1, time: 1000});
            }else{
                layer.msg('删除出错!', {icon: 5, time: 1000});
            }
        });
    });
}