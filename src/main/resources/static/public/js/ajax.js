

function AjaxGet(url,callback) {
    $.ajax({
        url: url,
        type: "GET",
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
        success: function (ret) {
            if (callback) {
                callback(ret);
            }
        },
        complete:function(){
            layer.close(layuiLoding);
        },
        error: function (err) {
            if (callback) {
                callback(err);
            }
        }
    });
}

function AjaxPost(url, data, callback) {
    $.ajax({
        url: url,
        data: data,
        type: "POST",
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
        success: function(ret) {
            if (callback) {
                callback(ret);
            }
        },
        complete:function(){
            layer.close(layuiLoding);
        },
        error: function(err) {
            if (callback) {
                callback(err);
            }
        }
    });
}

function AjaxDelete(url,data,callback) {
    data._method = "DELETE";
    $.ajax({
        url: url,
        data:data,
        type: "DELETE",
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
        success: function(ret) {
            if (callback) {
                callback(ret);
            }
        },
        complete:function(){
            layer.close(layuiLoding);
        },
        error: function(err) {
            if (callback) {
                callback(err);
            }
        }
    });
}

function AjaxPut(url,data,callback) {
    data._method = "PUT";
    $.ajax({
        url: url,
        type: "PUT",
        data:data,
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
        success: function(ret) {
            if (callback) {
                callback(ret);
            }
        },
        complete:function(){
            layer.close(layuiLoding);
        },
        error: function(err) {
            if (callback) {
                callback(err);
            }
        }
    });
}