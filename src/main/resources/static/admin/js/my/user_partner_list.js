
function serach() {
    var userTel = $(".userTel").val();
    // 查询上级用户
    showlow("/admin/getUpUser","#up",{
        userTel:userTel
    },columns,columnDefs);
    $('#up').DataTable().ajax.reload(null,false);
    // 查询下级用户
    showlow("/admin/getLowUser","#low",{
        userTel:userTel
    },columns,columnDefs);
    $('#low').DataTable().ajax.reload(null,false);
}

// 查询下级用户
function showlow(url,obj,dataJsonStr,columns) {
    var  labelItemListDataTable =$(obj).dataTable();
    labelItemListDataTable.fnClearTable(false);
    labelItemListDataTable.fnDestroy();
    $(obj).DataTable({
        language: {
            "processing":"正在加载数据...",
            "zeroRecords":  "没有匹配的记录",
            "infoPostFix":  "",
            "search":       "搜索:",
            "url":          "",
            "decimal": ",",
            "thousands": ".",
            "emptyTable":"未找到符合条件的数据",
        },
        retrieve: true,
        ordering: false,// 禁止排序
        info: false, // 是否展示分页记录
        autoWidth: false,
        bDestory: true,
        paging: false,
        bFilter: false, //去掉搜索框方法
        bLengthChange: false,//也就是页面上确认是否可以进行选择一页展示多少条
        serverSide: true,// 服务器端分页
        ajax: function (data, callback, settings) {
            //封装相应的请求参数，这里获取页大小和当前页码
            var layuiLoding;
            $.ajax({
                type: "POST",
                url: url,
                cache : false,  //禁用缓存
                data: dataJsonStr,   //传入已封装的参数
                beforeSend:function(){
                    layuiLoding = layer.load(0, { //icon支持传入0-2
                        time:false,
                        shade: [0.5, '#9c9c9c'], //0.5透明度的灰色背景
                        content: '正在读取数据...',
                        success: function (layero) {
                            layero.find('.layui-layer-content').css({
                                'padding': '39px 10px',
                                'width': '100px'
                            });
                        }
                    });
                },
                success: function(data) {
                    if (data.code == 200){
                        var arr = "";
                        if ('object' == typeof data) {
                            arr = data;
                        } else {
                            arr = $.parseJSON(data);//将json字符串转化为了一个Object对象
                        }
                        var returnData = {};
                        returnData.data = arr.data;//返回数据列表
                        callback(returnData);
                    }else {
                        layer.msg(data.msg, {icon: 5, time: 1000});
                    }
                },
                complete:function(){
                    layer.close(layuiLoding);
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    layer.msg("查询失败!", {icon: 5, time: 1000});
                }
            });
        },
        columns: columns,
        columnDefs : columnDefs,
        fnDrawCallback: function( oSettings ) {
            this.api().column(0).nodes().each(function(cell, i) {
                //i 从0开始，所以这里先加1
                i = i + 1;
                //服务器模式下获取分页信息，使用 DT 提供的 API 直接获取分页信息
                //行号等于 页数*每页数据长度+行号
                var columnIndex = i;
                cell.innerHTML = columnIndex;
            });
            $(obj +" thead tr th:first").removeClass("sorting_asc");
        }
    });
}