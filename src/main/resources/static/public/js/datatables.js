/**
 * 获取数据，含分页封装
 * @param obj 要操作的table元素
 * @param pagesize 分页大小
 * @param dataJsonStr 参数json字符串
 * @param url 获取数据的URL
 * @param columns table 对应行数据
 * @param columnDefs table 数据对应操作
 */
var table;
function page(obj,pagesize,dataJsonStr,url,columns,columnDefs){
    // 每次执行请求前，清空还原Table
    var  labelItemListDataTable =$(obj).dataTable();
    labelItemListDataTable.fnClearTable(false);
    labelItemListDataTable.fnDestroy();
    table = $(obj).DataTable({
        // bProcessing : true,// 加载数据
        language: {
            "processing":"正在加载数据...",
            "lengthMenu":   "_MENU_ 记录/页",
            "zeroRecords":  "没有匹配的记录",
            "info":         "第 _START_ 至 _END_ 项记录，共 _TOTAL_ 项",
            "infoEmpty":    "第 0 至 0 项记录，共 0 项",
            "infoFiltered": "(由 _MAX_ 项记录过滤)",
            "infoPostFix":  "",
            "search":       "搜索:",
            "url":          "",
            "decimal": ",",
            "thousands": ".",
            "emptyTable":"未找到符合条件的数据",
            "paginate": {
                "first":    "首页",
                "previous": "上页",
                "next":     "下页",
                "last":     "末页"
            }
        },
        // aLengthMenu: [15, 30, 50],// 分页大小，在bLengthChange为true的情况下才有效
        retrieve: true,
        paging: true,
        ordering: false,// 禁止排序
        info: true,
        autoWidth: false,
        bDestory: true,
        pageLength:pagesize,//每页显示10条数据
        pagingType: "full_numbers", //分页样式：simple,simple_numbers,full,full_numbers，
        bFilter: false, //去掉搜索框方法
        bLengthChange: false,//也就是页面上确认是否可以进行选择一页展示多少条
        serverSide: true,// 服务器端分页
        ajax: function (data, callback, settings) {
            //封装相应的请求参数，这里获取页大小和当前页码
            var start = data.start;//开始的记录序号
            dataJsonStr.page = (data.start) / data.length + 1;//当前页码
            dataJsonStr.pagesize = data.length;//页面显示记录条数，在页面显示每页显示多少项的时候,页大小
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
                        if (arr != null){
                            returnData.recordsTotal = arr.totalCount;//totalCount指的是总记录数
                            returnData.recordsFiltered = arr.totalCount;//后台不实现过滤功能,全部的记录数都需输出到前端，记录数为总数
                            returnData.data = arr.data;//返回数据列表
                        }
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
                var page = table.page.info();
                //当前第几页，从0开始
                var pageno = page.page;
                //每页数据
                var length = page.length;
                //行号等于 页数*每页数据长度+行号
                var columnIndex = (i + pageno * length);
                cell.innerHTML = columnIndex;
            });
            $(obj +" thead tr th:first").removeClass("sorting_asc");
        }
    });
}
