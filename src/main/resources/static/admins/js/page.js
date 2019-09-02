
//  分页插件封装
// 参数：
//  obj:需要操作的dom元素
//  sort:默认降序列
function page(obj,sort){
	$(obj).DataTable({
    	"bProcessing" : true,
        "pagingType":"full_numbers",
    	"ordering":true,
    	"searching": true,
    	"aLengthMenu": [2, 15, 50],
    	"bAutoWidth" : true,
    	"aaSorting": [[sort, "desc" ]],
    	"language":{
            "decimal":",",
            "thousands":".",
            "sProcessing" : "正在获取数据，请稍后...",
            "sSearch": "搜索:   ",
            "lengthMenu": " _MENU_ ",
            "sZeroRecords" : "对不起，没有您要搜索的内容",
            "zeroRecords": "没有找到记录",
            "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
            "infoEmpty": "无记录",
            "infoFiltered": "(从 _MAX_ 条记录过滤)",
            "paginate" :{
                "first" : "<<",
                "sPrevious" : "<",
                "sNext" : ">",
                "sLast" : ">>"
            }
        }
    });
}
