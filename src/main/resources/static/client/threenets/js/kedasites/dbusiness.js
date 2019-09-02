
$(function () {
    $(".index6").addClass("active");
    //年月选择器
    layui.use('laydate', function () {
        var laydate = layui.laydate;
        laydate.render({
            elem: '#month',
            type: 'month',
            value: new Date()
        });
    });
    loading();
});
//选择列表
function sel(obj) {
    $(".lists_li").removeClass("selecteds");
    $(obj).addClass("selecteds");
    var id = obj.children[0].innerHTML
    $("#auserid").val(id);
    loading();
}

// 代理商搜索
function searchEnterprise() {
    var enterprisename = $("#enterprisename").val();
    AjaxPost("/admins/findUserLikeName/",{"name":enterprisename},function (result) {
        var data =  result.data;
        let html = '<li class="lists_li" onclick="Sel(this);"><span style="display: none">0</span><span >全部汇总</span></li>'
        for (var i = 0;i<data.length;i++){
            var ele = data[i];
            html = html + '<li class="lists_li" onclick="Sel(this)"><span style="display: none">'+ele.id+'</span><span>'+ele.userName+'</span></li>'
        }
        $(".lists").html(html);
    });
}

//展示图表
function showEcharts(data){
    var myChart = echarts.init(document.getElementById('main'));
    var option = {
        title: {
            text: '每日发展用户情况'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend:{
            data:['每日发展用户情况']
        },
        xAxis: {
            type: 'category',
            data:data.map(x=>[x.dateTimes])
        },
            yAxis: {
                type: 'value'
            },
            series: [{
                data:data.map(x=>[x.dateTimes,x.addUser]),
            type: 'line',
                symbol: 'circle',//折线点设置为实心点
                symbolSize: 4,   //折线点的大小
                itemStyle: {
                normal: {
                    color: "#17a9ff",//折线点的颜色
                        lineStyle: {
                        color: "#17a9ff"//折线的颜色
                    }
                }
            },
        }]
    };
    myChart.setOption(option);
}


//加载数据
function loading() {
    let params = {
        "userId":$("#auserid").val(),
        "month":$("#month").val()
    }
    AjaxPost("/threenets/clcy/getBusinessData/1",params,function (result) {
        var data =  result.data;
        showTable(data)
        showEcharts(data)
    });
}

//展示表格
function showTable(data) {
    let html = ''
    for (var i = 0;i<data.length;i++){
        var ele = data[i];
        html = html + "<tr><td>"+(i+1)+"</td><td>"+ele.dateTimes+"</td><td>"+ele.addUser+"</td><td>"+ele.unsubscribeUser+"</td><td>"+ele.cumulativeUser+"</td></tr>"
    }
    $("#set tbody").html(html);
}