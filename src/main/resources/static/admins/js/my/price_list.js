$(function() {
    showTable()
});
function showTable() {
    var param = {
        "name": $(".userName").val()
    };
    var columns = [{
        "data": null
    },
        {
            "data": "categoryAlias"
        },
        {
            "data": "category"
        },
        {
            "data": "costPrice"
        },
        {
            "data": "raisePrice"
        },
        {
            "data": "guidePrice"
        },
        {
            "data": "createTime"
        }];
    var columnDefs = [{
        targets: [7],
        render: function(data, type, row, meta) {
            var id = row.id;
            return "<a title='编辑' onclick='eidt(\"编辑\",\"/admin/toUpdatePricePage/" + id + "\", 800, 570)' href='javascript:;'><i class=\"layui-icon\"></i></a>"
        }
    }];
    page("#price_table", 10, param, "/admin/getNumPriceList", columns, columnDefs)
}
function eidt(title, url, w, h) {
    layer.open({
        type: 2,
        area: [w + 'px', h + 'px'],
        fix: false,
        maxmin: true,
        shadeClose: true,
        shade: 0.4,
        title: title,
        content: url,
        end: function() {
            $('#price_table').DataTable().ajax.reload(null, false)
        }
    })
}