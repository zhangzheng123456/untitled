var oc = new ObjectControl();
var page=1;
var param={};//定义传值对象
var cls="";//标签搜索下class名
var txt="";//标签搜索下标签名
var val="";//贴上input值
var cart_num = 0;//购物车页码
var has_next_list = false;//购物车下一页标志
var back_num = 1;//回访页码
var next_page = false;//回访下一页标志
var swip_image = [];//图片切换播放
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
var chart_data;//图表数据
var chart={
    "myChart":"",//type
    "myChart1":"",//weeks
    "myChart2":"",//price
    "myChart3":"",//Season
    "myChart4":"",//month
    "myChart5":"",//series
    "myChart6":"",//areas
    "chartYear":"",//Year
    "chartMId":"", //中类偏好
    "chartSm":"",//小类偏好
    "chartSeries":""//系列偏好
};
//取消下拉框
$(document).on('click', function (e) {
    if(!($(e.target).is(".chart_year_input"))){$("#chart_year_select").hide();}
    if(!($(e.target).is(".callback_input"))){$(".callback_select").hide()}
});
function getConsumCount(){//获取会员信息
    // whir.loading.add("",0.5);//加载等待框
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param["vip_id"]=id;
    $("#center_module").show();
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var memorandum=JSON.parse(Data.Memo);
       var HTML="";
       var Ablum_all_html="";
       var LABEL="";
       var LABELALL="";
        $("#upAlbum").parent().parent().siblings().remove();
        if(album.length!==0){
            for(var i=0;i<album.length;i++){
                swip_image.push(album[i].image_url);
                var date=album[i].time;
                date=date.substring(0,11);
                if(i<16){
                    HTML+="<span><img src="+album[i].image_url+" /></span>";
                }
                Ablum_all_html="<li>"
                    +"<img src='"+album[i].image_url+"'>"
                    +"<div class='cancel_img' data-time='"+album[i].time+"'></div>"
                    +"<span class='album_date'>"+date+"</span>"
                    +"</li>";
                $("#upAlbum").parent().parent().before(Ablum_all_html);
            }
        }else {
            HTML+="<p>暂无图片</p>";
        }
        $("#images").html(HTML);
        if(label.length!==0){
            for(var i=0;i<label.length;i++){
                LABEL+="<span>"+label[i].label_name+"</span>";
                LABELALL+="<span class='label_u' data-rid="+label[i].id+">"+label[i].label_name+"<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i></span>"
            }
        }else{
            LABEL+="<p>暂无标签</p>";
        }
        if(memorandum.length!==0){
            $("#memorandum_table tbody").empty();
            for(var i=0;i<memorandum.length;i++){
                $("#memorandum_table tbody").append("<tr id='"+memorandum[i].memoid+"'><td>"+memorandum[i].time+"</td><td><span title='"+memorandum[i].content+"'>"+memorandum[i].content+"</span></td><td class='icon-ishop_6-12'></td></tr>");
            }
        }else {
            var td="";
            for(var i=0;i<10;i++){
                if(i==4){
                    td+="<tr><td colspan='3' style='text-align: center'>暂无内容</td></tr>";
                }else {
                    td+="<tr><td colspan='3'></td></tr>"
                }
            }
            $("#memorandum_table tbody").html(td);
        }
        //统计已有标签
        $(".span_total").html(label.length);
        $("#labels").html(LABEL);
        $("#label_box").html(LABELALL);
        img_hover();
        $("#center_module").hide();
        // whir.loading.remove();
    })
}
function compare(a, b) {
   return a-b ;
}
function chartShow(order) {
    if (order !== "" && order !== undefined) {
        var id = $("#chart_analyze").attr("data-id");
        var param = {
            "corp_code": "C10000",
            "function_code": funcCode,
            "type": "order",
            "id": id,
            "order": order
        };
    } else {
        var param = {
            "corp_code": "C10000",
            "function_code": funcCode,
            "type": "show"
        };
    }
    oc.postRequire("post", "/privilege/vip/chartOrder", "0", param, function (data) {
        if (data.code == 0) {
            if (data.message == "success") {
            } else {
                var message = JSON.parse(data.message);
                var list=message.list;
                if(message.can_add=="N"){
                    $("#add_chart").hide();
                }else{
                    $("#add_chart").show();
                }
                if (list.length == 0) {
                    return;
                }
                var l = list.length - 1;
                var id = list[l].id;
                if (id !== undefined) {
                    $("#chart_analyze").attr("data-id", id);
                    var column = JSON.parse(list[l].column_name);
                    for (var i = 0; i < column.length; i++) {
                        var a = column[i];
                        $(".chart_module").each(function () {
                            if (a == $(this).attr("data-id")) {
                                $(this).show();
                                $("#chart_analyze").append($(this));
                                var type=$(this).find(".chart_analyze_condition span").html();
                                switch (type){
                                    case "按金额分析":
                                        type="amt";
                                        break;
                                    case "按件数分析":
                                        type="num";
                                        break;
                                    case "按折扣分析":
                                        type="discount";
                                        break;
                                    case "按客单价分析":
                                        type="trade_price";
                                        break;
                                    case "按连带率分析":
                                        type="relate_rate";
                                        break;
                                    case "按笔数分析":
                                        type="times";
                                        break;
                                }
                                var ID = $(this).attr("data-id");
                                init_chart(ID,type);
                            }
                        });
                    }
                }
            }
        } else if (data.code == -1) {
            console.log(data.message);
        }
    });
}
function init_chart(id,type) {
    if (id == "type") {
        var data = chart_data.Type;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#type").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#type").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#type").next().html(head+value);
        $("#type").next().find("li").css("width",($("#type").next().width()-10)/$(head).find("li").length+"px")
        var option_pie = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart = echarts.init(document.getElementById('type'));
        chart.myChart.setOption(option_pie);
        reSize(chart.myChart);
    }
    if (id == "series") {
        var data = chart_data.Series;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#series").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#series").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#series").next().html(head+value);
        $("#series").next().find("li").css("width",($("#series").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart5 = echarts.init(document.getElementById('series'));
        chart.myChart5.setOption(option_pie_series);
        reSize(chart.myChart5);
    }
    if (id == "weeks") {
        var data = chart_data.Week;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#weeks").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#weeks").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr=[];
        var head="";
        var value="";
        var newArr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
            newArr.push(arr[i].value);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#weeks").next().html(head+value);
        $("#weeks").next().find("li").css("width",($("#weeks").next().width()-10)/$(head).find("li").length+"px");
        var option_week_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis',
                confine:true
            },
            polar: [
                {
                    indicator: [
                        {text: '周一', max: 100},
                        {text: '周二', max: 100},
                        {text: '周三', max: 100},
                        {text: '周四', max: 100},
                        {text: '周五', max: 100},
                        {text: '周六', max: 100},
                        {text: '周日', max: 100}
                    ],
                    radius: 100,
                    splitNumber: 7,
                    startAngle: 68,
                    splitArea: {
                        show: true,
                        areaStyle: {
                            color: '#fff'
                        }
                    }
                }
            ],
            series: [
                {
                    symbol: 'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize: '0',
                    data: [
                        {
                            value: Arr,
                            name: '星期偏好'
                        }
                    ]
                }
            ]
        };
        newArr.sort(compare);
        for(var j=0;j<option_week_radar.polar[0].indicator.length;j++){
            option_week_radar.polar[0].indicator[j].max=newArr[newArr.length-1]*1.1==0?"10":newArr[newArr.length-1]*1.1;
        }
        chart.myChart1 = echarts.init(document.getElementById('weeks'));
        chart.myChart1.setOption(option_week_radar);
        reSize(chart.myChart1);
    }
    if (id == "price") {
        var data=chart_data.Price;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#price").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#price").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var ArrName=[];
        var ArrValue=[];
        var dataZoom=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        if(ArrName.length>8){
            dataZoom=[
                {
                    type: 'slider',
                    show: true,
                    handleSize: 8,
                    width:15,
                    yAxisIndex: [0],
                    right:"20",
                    start:100,
                    end: 50
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start:0,
                    end: 100
                }
            ]
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#price").next().html(head+value);
        $("#price").next().find("li").css("width",($("#price").next().width()-10)/$(head).find("li").length+"px");
        chart.myChart2 = echarts.init(document.getElementById('price'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
            },
            xAxis: [

                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01]

                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    },
                    type: 'category',
                    data: ArrName
                }
            ],
            dataZoom:dataZoom,
            series: [
                {
                    itemStyle: {
                        normal: {
                            barBorderRadius: 0
                        }
                    },
                    name: '价格偏好',
                    type: 'bar',
                    barWidth: 10,
                    data: ArrValue
                }
            ]
        };
        chart.myChart2.setOption(option_bar);
        reSize(chart.myChart2);
    }
    if (id == "year") {
        var data=chart_data.Year;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#Year").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#Year").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var ArrName=[];
        var ArrValue=[];
        var dataZoom=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        if(ArrName.length>8){
            dataZoom=[
                {
                    type: 'slider',
                    show: true,
                    handleSize: 8,
                    width:15,
                    yAxisIndex: [0],
                    right:"20",
                    start:100,
                    end: 50
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start:0,
                    end: 100
                }
            ]
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#Year").next().html(head+value);
        $("#Year").next().find("li").css("width",($("#price").next().width()-10)/$(head).find("li").length+"px");
        chart.chartYear = echarts.init(document.getElementById('Year'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
            },
            xAxis: [

                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01]

                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    },
                    type: 'category',
                    data: ArrName
                }
            ],
            dataZoom:dataZoom,
            series: [
                {
                    itemStyle: {
                        normal: {
                            barBorderRadius: 0
                        }
                    },
                    name: '年份偏好',
                    type: 'bar',
                    barWidth: 10,
                    data: ArrValue
                }
            ]
        };
        chart.chartYear.setOption(option_bar);
        reSize(chart.chartYear);
    }
    if (id == "month") {
        var data=chart_data.Month;
        var arr=new Array();
        switch (type){
            case "amt":
                arr=data.trade_amt;
                break;
            case "num":
                arr=data.trade_num;
                break;
            case "discount":
                arr=data.discount;
                break;
            case "relate_rate":
                arr=data.relate_rate;
                break;
            case "trade_price":
                arr=data.trade_price;
                break;
            case "times":
                arr=data.stroke_num;
                break;
        }
        if(arr.length==0){
            $("#month").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#month").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr=[];
        var newArr=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
            newArr.push(arr[i].value);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        switch (type){
            case "amt":
                value="<ul><li>金额</li>"+value+"</ul>";
                break;
            case "num":
                value="<ul><li>件数</li>"+value+"</ul>";
                break;
            case "discount":
                value="<ul><li>金额</li>"+value+"</ul>";
                break;
            case "relate_rate":
                value="<ul><li>连带率</li>"+value+"</ul>";
                break;
            case "trade_price":
                value="<ul><li>客单价</li>"+value+"</ul>";
                break;
            case "times":
                value="<ul><li>次数</li>"+value+"</ul>";
                break;
        }
        $("#month").next().html(head+value);
        $("#month").next().find("li").css("width",($("#month").next().width()-10)/$(head).find("li").length+"px");
        var option_month_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis',
                confine:true
            },
            polar: [
                {
                    indicator: [
                        {text: '一月', max: 100},
                        {text: '二月', max: 100},
                        {text: '三月', max: 100},
                        {text: '四月', max: 100},
                        {text: '五月', max: 100},
                        {text: '六月', max: 100},
                        {text: '七月', max: 100},
                        {text: '八月', max: 100},
                        {text: '九月', max: 100},
                        {text: '十月', max: 100},
                        {text: '十一月', max: 100},
                        {text: '十二月', max: 100}
                    ],
                    radius: 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea: {
                        show: true,
                        areaStyle: {
                            color: '#fff'
                        }
                    }
                }
            ],
            series: [
                {
                    symbol: 'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize: '0',
                    data: [
                        {
                            value: Arr,
                            name: '月份偏好'
                        }
                    ]
                }
            ]
        };
        newArr.sort(compare);
        for(var j=0;j<option_month_radar.polar[0].indicator.length;j++){
            option_month_radar.polar[0].indicator[j].max=newArr[newArr.length-1]*1.1==0?"10":newArr[newArr.length-1]*1.1;
        }
        chart.myChart4 = echarts.init(document.getElementById('month'));
        chart.myChart4.setOption(option_month_radar);
        reSize(chart.myChart4);
    }
    if (id == "season") {
        var data = chart_data.Season;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#season").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#season").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var a=0;a<arr.length;a++){
            Arr.push(arr[a].name);
            head+='<li>'+arr[a].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[a].value+'">'+arr[a].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#season").next().html(head+value);
        $("#season").next().find("li").css("width",($("#season").next().width()-10)/$(head).find("li").length+"px")
        var option_pie_season = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '季节分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart3 = echarts.init(document.getElementById('season'));
        chart.myChart3.setOption(option_pie_season);
        reSize(chart.myChart3);
    }
    if (id == "areas") {
        var data=chart_data.Area;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#areas").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#areas").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        chart.myChart6 = echarts.init(document.getElementById('areas'));
        var valArr=[];
       for(var i=0;i<arr.length;i++){
          arr[i].name=arr[i].name.replace(/省|市/,"");
           if(arr[i].name=="宁夏回族自治区"){
               arr[i].name="宁夏"
           }
           if(arr[i].name=="内蒙古自治区"){
               arr[i].name="内蒙古"
           }
           if(arr[i].name=="新疆维吾尔自治区"){
               arr[i].name="新疆"
           }
           if(arr[i].name=="西藏自治区"){
               arr[i].name="西藏"
           }
           if(arr[i].name=="广西壮族自治区"){
               arr[i].name="广西"
           }
           valArr.push(arr[i].value)
       }
        valArr.sort(function(a,b){return a-b});
        var option_map = {
            tooltip: {
                trigger: 'item'
            },
            dataRange: {
                itemWidth: 5,
                itemGap: 0.2,
                color: ['#3C95A2', '#A7CFD5'],
                splitNumber: '20',
                orient: 'horizontal',
                min: 0,
                max: Number(valArr[valArr.length-1]<10?10:valArr[valArr.length-1]),
                x: 'left',
                y: 'top',
                text: ['高','低']      // 文本，默认为数值文本
            },
            series: [
                {
                    name: '地域偏好',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    label: {
                        normal: {
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    itemStyle: {
                        normal: {
                            label: {
                                show: true, textStyle: {
                                    color: "#434960",
                                    fontSize: 10
                                }
                            }
                        },
                        emphasis: {label: {show: true}}
                    },
                    data:arr
                }
            ]
        };
        chart.myChart6.setOption(option_map);
        reSize(chart.myChart6);
    }
    if (id == "typeMid") {
        var data = chart_data.TypeMid;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeMid").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeMid").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeMid").next().html(head+value);
        $("#typeMid").next().find("li").css("width",($("#typeMid").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartMId = echarts.init(document.getElementById('typeMid'));
        chart.chartMId.setOption(option_pie_series);
        reSize(chart.chartMId);
    }
    if (id == "typeSm") {
        var data = chart_data.TypeSm;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeSm").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSm").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeSm").next().html(head+value);
        $("#typeSm").next().find("li").css("width",($("#typeSm").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartSm = echarts.init(document.getElementById('typeSm'));
        chart.chartSm.setOption(option_pie_series);
        reSize(chart.chartSm);
    }
    if (id == "typeSeries") {
        var data = chart_data.TypeSeries;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeSeries").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSeries").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeSeries").next().html(head+value);
        $("#typeSeries").next().find("li").css("width",($("#typeSeries").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartSeries = echarts.init(document.getElementById('typeSeries'));
        chart.chartSeries.setOption(option_pie_series);
        reSize(chart.chartSeries);
    }
    if (id == "view_source") {
        var data = chart_data.source;
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#view_source").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#view_source").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li>'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#view_source").next().html(head+value);
        $("#view_source").next().find("li").css("width",($("#view_source").next().width()-10)/$(head).find("li").length+"px");
        var option_pie = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '渠道偏好',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart_source = echarts.init(document.getElementById('view_source'));
        chart.myChart_source.setOption(option_pie);
        reSize(chart.myChart_source);
    }
}
function reSize(chart) {
    window.addEventListener("resize", function () {
        chart.resize();
    });
}
//图表数据
function chartData(id) {
    var param={};
    param["type"] = funcCode == 'F0044' ? "allCard" : "vipInfo";
    param['corp_code'] = sessionStorage.getItem('corp_code');
    param["vip_id"] = funcCode == "F0044" ? id : sessionStorage.getItem("id");
    param['year'] = $("#chart_year_input").val()=='全部'?'':$("#chart_year_input").val();
    funcCode == "F0044" ? param.vip_phone = sessionStorage.getItem("vip_phone") : "";
    $('.right_shadow').show();
    oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            chart_data= msg.message;
            chartShow();
            $('.right_shadow').hide();
        }else if(data.code == -1){
            $('.right_shadow').hide();
        }
    });
}
//图表年份筛选
$(".chart_year_input").click(function () {
    $("#chart_year_select").toggle();
});
$("#chart_year_select").on("click","li",function () {
    $("#chart_year_input").val($(this).text());
    chartData();
});
//图表数据导出
$(".chart_head .icon-ishopwebicon_6-24").click(function(){
    var arr=whir.loading.getPageSize();
    var self=$(this);
    var id=self.parents(".chart_module").attr("data-id");
    var data="";
    switch (id){
        case "type":
            data=chart_data.Type;
            break;
        case "weeks":
            data=chart_data.Week;
            break;
        case "price":
            data=chart_data.Price;
            break;
        case "month":
            data=chart_data.Month;
            break;
        case "season":
            data=chart_data.Season;
            break;
        case "series":
            data=chart_data.Series;
            break;
        case "typeMid":
            data=chart_data.TypeMid;
            break;
        case "typeSm":
            data=chart_data.TypeSm;
            break;
        case "TypeSeries":
            data=chart_data.TypeSeries;
            break;
        case "view_source":
            data=chart_data.source;
            break;
    }
    if(data.trade_amt["length"]=="0" && data.trade_num["length"]=="0" &&data.discount["length"]=="0"){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            zIndex:10011,
            content: "暂无数据"
        });
        return
    }
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#tk_export").show();
    $("#enter a").attr("data-src","");
    $("#enter a").removeAttr("href");
    $("#enter a").html("确认");
    $("#X_export p").html("是否导出该图表数据?");
    $("#enter").unbind("click").bind("click",function(){
        if($("#enter a").attr("data-src")!=""){
            $("#tk_export").hide();
            $("#p").hide();
        }else{
            whir.loading.add("","0.5");
            oc.postRequire("post","/vipAnalysis/exportExecl2View","0",data,function (reg) {
                whir.loading.remove();
                if(reg.code=="0"){
                    var src=JSON.parse(reg.message);
                    var path=src.path;
                    path=path.substring(1,path.length-1);
                    $("#X_export p").html("是否下载文件?");
                    $("#enter a").attr("href","/"+path);
                    $("#enter a").attr("data-src",src.path);
                    $("#enter a").html("下载文件");
                }else if(reg.code=="-1"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        zIndex:10011,
                        content: reg.message
                    });
                }
            })
        }
    });
    $("#X_export,#cancel_export").click(function(){
        $("#tk_export").hide();
        $("#enter a").attr("data-src","");
        $("#enter a").removeAttr("href");
        $("#enter a").html("确认");
        $("#X_export p").html("是否导出该图表数据?");
        $("#p").hide();
    })
});
//图标新增弹窗
$("#add_chart").click(function () {
    var arr = whir.loading.getPageSize();
    var w = arr[0];
    var h = arr[1];
    var left = (arr[0] - $("#chart_add_wrap").width()) / 2;
    var tp = (arr[3] - $("#chart_add_wrap").height()) / 2;
    var corp_code = sessionStorage.getItem("corp_code");
    corp_code == 'C10055' ? $("#year_chart").show() : "";
    $("#chart_add_wrap").css({"left": left, "top": tp, "position": "fixed"});
    $("#p").css({"width": w, "height": h});
    $("#chart_add_wrap").show();
    $("#p").show();
    $("#chart_analyze .chart_module").each(function () {
        var val = $(this).find(".drag_area").children("span").html();
        if ($(this).css("display") == "block") {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_name").html() == val) {
                    $(this).find(".chart_icon").html("-");
                    $(this).find(".chart_icon").removeClass("chart_icon").addClass("chart_icon_remove");
                }
            });
        }else if($(this).css("display") == "none"){
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_name").html() == val) {
                    $(this).find(".chart_icon_remove").html("+");
                    $(this).find(".chart_icon_remove").removeClass("chart_icon_remove").addClass("chart_icon");
                }
            });
        }
    });
});
$(".chart_close").click(function () {
    $("#chart_add_wrap").hide();
    $("#p").hide();
});
$("#chart_add_enter").click(function () {
    $("#chart_add_details .chart_icon_remove").each(function () {
        var val = $(this).prev().find(".chart_name").html();
        $("#chart_analyze .chart_module").each(function (i,e) {
            if ($(e).css("display") == "none" && val == $(e).find(".drag_area span").html()) {
                $("#chart_analyze").append($(e));
                $(e).show();
                var type=$(e).find(".chart_analyze_condition span").html();
                type=="按金额分析"?type="amt":type=="按件数分析"?type="num":"";
                var ID = $(e).attr("data-id");
                init_chart(ID,type);
            } else {
                return;
            }
        });
    });
    $("#chart_add_details .chart_icon").each(function () {
        var val = $(this).prev().find(".chart_name").html();
        $("#chart_analyze .chart_module").each(function (i,e) {
            if ($(e).css("display") !== "none" && val == $(e).find(".drag_area span").html()) {
                $(e).hide();
            } else {
                return;
            }
        });
    });
    var order = [];
    $(".chart_module").each(function () {
        if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
            order.push($(this).attr("data-id"));
        }
    });
    $("#chart_add_wrap").hide();
    $("#p").hide();
    chartShow(order);
});
$("#chart_add_details").on("click",".chart_icon",function () {
    $(this).removeClass().addClass("chart_icon_remove");
    $(this).html("-");
});
$("#chart_add_details").on("click",".chart_icon_remove",function () {
    $(this).removeClass().addClass("chart_icon");
    $(this).html("+");
});
//图表分析下拉
$(".chart_analyze_condition").click(function () {
    $(this).children("ul").toggle();
});
$('.chart_analyze_condition ul li').click(function () {
    $(this).parent().prevAll("span").html($(this).html());
    var ID=$(this).parents(".chart_module").attr("data-id");
    var type=$(this).html();
    switch (type){
        case "按金额分析":
            type="amt";
            break;
        case "按件数分析":
            type="num";
            break;
        case "按折扣分析":
            type="discount";
            break;
        case "按客单价分析":
            type="trade_price";
            break;
        case "按连带率分析":
            type="relate_rate";
            break;
        case "按笔数分析":
            type="times";
            break;
    }
    init_chart(ID,type);
});
//图表删除
$(".chart_close_icon").click(function () {
    $(this).parents(".chart_module").hide();
    var order = [];
    $(".chart_module").each(function () {
        if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
            order.push($(this).attr("data-id"));
        }
    });
    chartShow(order);
});
//列表图表切换
$(".chart_list_icon").click(function () {
    var li = $(this).parent(".chart_head").nextAll(".data_table");
    var id = $(this).parent(".chart_head").next('li').attr("id");
    if($(li).css("display") == "block"){
        $(li).hide();
        $(li).prev("li").show();
        switch (id){
            case "type" :chart.myChart!==""?chart.myChart.resize():"";break;
            case "series" :chart.myChart5!==""?chart.myChart5.resize():"";break;
            case "weeks" :chart.myChart1!==""?chart.myChart1.resize():"";break;
            case "month" :chart.myChart4!==""?chart.myChart4.resize():"";break;
            case "season" :chart.myChart3!==""?chart.myChart3.resize():"";break;
            case "areas" :chart.myChart6!==""?chart.myChart6.resize():"";break;
            case "price" :chart.myChart2!==""?chart.myChart2.resize():"";break;
            case "typeMid" :chart.chartMId!==""?chart.chartMId.resize():"";break;
            case "typeSm" :chart.chartSm!==""?chart.chartSm.resize():"";break;
            case "typeSeries" :chart.chartSeries!==""?chart.chartSeries.resize():"";break;
            case "view_source" :chart.myChart_source!==""?chart.myChart_source.resize():"";break;
        }
    }else if($(li).css("display") == "none"){
        $(li).show();
        $(li).prev("li").hide();
    }
});
$("#chart_analyze").dad({
    placeholder:"拖放到这里",
    draggable: '.drag_area',
    callback: function (data) {
        var index = $(data).index();
        var order = [];
        $(".chart_module").each(function () {
            if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                order.push($(this).attr("data-id"));
            }
        });
        order.pop();
        chartShow(order);
    }
});
//点击图片放大
$("#images").on("click","span",function(){
        var src=$(this).children().attr("src");
        whir.loading.add("",0.8,src);//显示图片
    });
$(".album").on("click","li img",function(){
        var src=$(this).attr("src");
        whir.loading.add("",0.8,src);
    });
function img_hover(){
    //相册关闭按钮显示
    $(".album img").mouseover(function () {
        $(this).next(".cancel_img").show();
    }).mouseout(function () {
        $(this).next(".cancel_img").hide();
    });

    $(".cancel_img").mouseover(function () {
        $(this).show();
    }).mouseout(function () {
        $(this).hide();
    })
}
$("#Ablum-all").on("click",".cancel_img",function(){
    whir.loading.add("mask",0.1);
    var time=$(this).attr("data-time");
    $("#tk").show();
    $("#delete").attr("data-time",time);
});
$("#X").click(function(){
    $("#tk").hide();
    whir.loading.remove("mask");
});
$("#Ablum-all").on("mouseover","img",function(){
   $(this).next().show()
});$("#Ablum-all").on("mouseover","div",function(){
   $(this).show()
});
$("#Ablum-all").on("mouseout","img",function(){
   $(this).next().hide()
});
$("#Ablum-all").on("mouseout","div",function(){
    $(this).hide()
});
$(".message-class ul li a").click(function(){
    $(this).addClass("active");
    $(this).parent().siblings().children().removeClass("active");
    var nowIndex=$(this).parent().index();
    $(".tabs-parent").children().eq(nowIndex).show();
    $(".tabs-parent").children().eq(nowIndex).siblings().hide()
});
function toall(){
    $('html,body').animate({
        'scrollTop': 0
    },0);
    userlist();
    var nowdataName=$(".message-class ul li .active").attr("data-name");
    $("#VIP_Message").hide();
    $("#VIP_edit").show();
    $("#nav_bar").find("li").each(function(index){
        if($(this).attr("data-name")==nowdataName){
            var len=$(this).width();
            $(this).addClass("active1");
            $(this).siblings().removeClass("active1");
            $(".all_list").children().eq(index).show();
            $(".all_list").children().eq(index).siblings().hide();
            $("#remark").animate({left:len*index},0.1);
        }
    });
    gethotVIPlabel();
}
$("#fenLei").click(function(){//点击查看更多跳到编辑资料
    toall();
});
$("#consum tbody").click(function(){
    toall();
});
$("#points tbody").click(function(){
    toall();
});
$("#VIP_message_back").click(function(){//回到会员信息
   $("#VIP_Message").show();
   $("#VIP_edit").hide();
    for(var key in chart){
        chart[key] && chart[key].resize()
    }
   text_first="";
   text_second="";
   text_third="";
   time_start="";
   time_end="";
    getConsumCount();
    getVipInfo();
});
//热门标签
function gethotVIPlabel() {
    //热门标签
    $("#hotlabel").empty();
    var param={};
    param['vip_id']=sessionStorage.getItem("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
            var html="";
            var classname="";
            for(var i=0;i<msg.length;i++){
                if(msg[i].label_type=="user"){
                    if(msg[i].label_sign=="Y"){
                        classname="label_u_active";
                    }else {
                        classname="label_u";
                    }
                }else{
                    if(msg[i].label_sign=="Y"){
                        classname="label_g_active";
                    }else {
                        classname="label_g";
                    }
                }
                html+="<span  draggable='true' data-id="+msg[i].label_id+" class="+classname+" id="+i+">"+msg[i].label_name+"</span>"
            }
            $("#hotlabel").append(html);
        }
        //绑定拖拽事件
        $('#hotlabel span').on('dragstart',function (event) {
            var ev=event;
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
        //右侧标签点击事件
        clickLabeladd();
    })
}
$("#hot_label").click(function () {
    gethotVIPlabel();
});
//官方用户标签
function getOtherlabel() {
    param['vip_id']=sessionStorage.getItem("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['pageNumber']=page;
    param['searchValue']="";
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list)
            var hasNextPage=list.hasNextPage;
                list=list.list;
            var html="";
            var classname="";
            if(hasNextPage==false){
                $("#more_label_g").hide();
                $("#more_label_u").hide();
            }else {
                $("#more_label_g").show();
                $("#more_label_u").show();
            }
            if(list[0].label_type=="org"){
                for(var i=0;i<list.length;i++){
                    if(list[i].label_sign=="Y"){
                        classname="label_g_active";
                    }else {
                        classname="label_g";
                    }
                    html+="<span  draggable='true' data-id="+list[i].id+" class="+classname+" id='"+i+"g'>"+list[i].label_name+"</span>"
                }
                $("#label_org").append(html);
            }else if(list[0].label_type=="user"){
                for(var j=0;j<list.length;j++){
                    if(list[j].label_sign=="Y"){
                        classname="label_u_active";
                    }else {
                        classname="label_u";
                    }
                    html+="<span  draggable='true' data-id="+list[j].id+" class="+classname+" id='"+j+"u'>"+list[j].label_name+"</span>"
                }
                $("#label_user").append(html);
            }

        }
        //绑定拖拽事件
        $('#label_org span').on('dragstart',function (event) {
            var ev=event;
                ev=ev.originalEvent;
                ev.dataTransfer.setData("Text",ev.target.id);
        });
        //绑定拖拽事件
        $('#label_user span').on('dragstart',function (event) {
            var ev=event;
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
        //右侧标签点击事件
        clickLabeladd();
    })
}
$("#label_li_org").click(function () {
    page=1;
    $("#label_org").empty();
    param['type']="2";
    getOtherlabel();
})
$("#label_li_user").click(function () {
    page=1;
    $("#label_user").empty();
    param['type']="3";
    getOtherlabel();
})

//右侧加载更多标签
$("#more_label_g").click(function () {
    page=page+1;
    param['type']="2";
    getOtherlabel();
})
$("#more_label_u").click(function () {
    page=page+1;
    param['type']="3";
    getOtherlabel();
})

//搜索热门标签
function searchHotlabel() {
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['pageNumber']=page;
    param['vip_id']=sessionStorage.getItem("id");
    param['searchValue']=$('#search_input').val().replace(/\s+/g,"");
    param['type']="1";
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list)
            var hasNextPage=JSON.parse(msg.hasNextPage);
            list=msg.list;
            var html="";
            if(list.length!==0){
                $(".search_box").show();
            }else {
                $(".search_box").hide();
            }
            if(hasNextPage==true){
                for(var i=0;i<list.length;i++){
                    if(list[i].label_type=="user"){
                        html+="<li class='label_u' data-id="+list[i].id+">"+list[i].label_name+"</li>"
                    }else {
                        html+="<li class='label_g' data-id="+list[i].id+">"+list[i].label_name+"</li>"
                    }
                }
                $("#more_search").show();
                $(".search_list").append(html);
            }else {
                for(var j=0;j<list.length;j++){
                    if(list[j].label_type=="user"){
                        html+="<li class='label_u' data-id="+list[j].id+">"+list[j].label_name+"</li>"
                    }else {
                        html+="<li class='label_g' data-id="+list[j].id+">"+list[j].label_name+"</li>"
                    }
                }
                $("#more_search").hide();
                $(".search_list").append(html);
            }
        }
        //搜索下拉点击事件
        $(".search_list li").click(function () {
            var a="li";
            cls=$(this).attr("class");
            txt=$(this).html();
            param['label_name']=txt;
            $("#search_input").val("");
            addViplabel(a);
        });
    })
}
//加载更多
function moreSearch() {
    $("#more_search").click(function () {
        page=page+1;
       searchHotlabel();
    })
}
$(document).click(function(e){
    if($(e.target).is("#more_search")){
        return;
    }else{
        $(".search_box").hide();
    }
    if(!($(e.target).is("#send_code i") || $(e.target).is("#send_code input"))){
        $("#send_code ul").hide();
    }
    if(!($(e.target).is("#USER_SEX") || $(e.target).is("#vip_sex_select"))){
        $("#vip_sex_select").hide();
    }
    if(!($(e.target).is("#user_name_edit") || $(e.target).is("#user_name_select"))){
        $("#user_name_select").hide();
    }
    if(!($(e.target).is("#vip_card_type_edit") || $(e.target).is("#vip_card_type_select"))){
        $("#vip_card_type_select").hide();
    }
    if(!($(e.target).is("#STORE_address")||$(e.target).is("#address .down_icon_vip")||$(e.target).is(".address_nav a")||$(e.target).is(".dl_box a"))){
        $("#address_container").hide();
    }
});
//input输入框里面
$('#search_input').bind('input propertychange', function() {
    var thatFun=arguments.callee;
    var that=this;
    $(this).unbind("input propertychange",thatFun);
    $(".search_list").empty();
    page=1;
    searchHotlabel();
    setTimeout(function(){$(that).bind("input propertychange",thatFun)},0);
});
//隐藏下拉框滚动条
$(function(){
    $("#label_user").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    $("#label_org").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    $(".search_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    $("#label_box").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
});
//回到会员列表
$("#VIP_LIST_info").click(function(){
    sessionStorage.getItem("vip_phone") != undefined ? sessionStorage.removeItem("vip_phone") : '';
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});

//回到会员列表
$("#VIP_LIST,#VIP_LIST_S").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});
//导航点击切换窗口
$("#nav_bar li").click(function () {
    var index=$(this).index();
    $(this).addClass("active1");
    $(this).siblings().removeClass("active1");
    $(".all_list").children().eq(index).show();
    $(".all_list").children().eq(index).siblings().hide();
    if($(this).attr('data-name')=='jifen'){
        getVipPoints($("#corp_code").html(),'1')
    }
    if($(this).attr('data-name')=='consume'){
        row_num = 0;
        getVipPoints($("#corp_code").html(),'2')
    }
    if($(this).attr('data-name')=='label'){
        gethotVIPlabel();
    }
    if($(this).attr('data-name')=='coupon'){
        $('.coupon_nav li').eq(0).click();//优惠券默认
    }
    if($(this).attr('data-name')=='cart'){
        getShoppingCart();
    }
    if($(this).attr('data-name')=='callback'){
        getCallBack();
    }
}).mouseover(function(){
    var index=$(this).index();
    var len=$(this).width();
    $("#remark").animate({left:len*index},200);
}).mouseout(function(){
    $("#remark").stop(true,true);
});
$(".nav_bar").mouseleave(function() {
    $("#remark").stop();
    var _this = $(".active1").index();
    $(this).children().eq(_this).addClass("active1");
    var len = $(this).children().eq(_this).width();
    $("#remark").animate({left: len * _this}, 200);
});
//标签导航切换窗口
$(".label_nav li").click(function () {
    var index=$(this).index()+1;
    $(this).addClass("label_li_active");
    $(this).siblings().removeClass("label_li_active");
    $(".label_box").eq(index).show();
    $(".label_box").eq(index).siblings("div").hide();
});
//添加，删除标签
function labelDelete(obj) {
    var param={};
    var span=$(obj);
    var rid=$(obj).parent("span").attr("data-rid");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['vip_id']=sessionStorage.getItem("id")
    param["label_id"]=rid;
    oc.postRequire("post","/VIP/label/delRelViplabelToHbase","",param,function(data){
        if(data.code=="0"){
            span.parent("span").remove();
            var total=parseInt($(".span_total").html())-1;
            $(".span_total").html(total);
        }
    });
    var that=span.parent("span").text();
    var len=$("#hotlabel span").length;
    var len_o=$("#label_org span").length;
    var len_u=$("#label_user span").length;
    for(var i=0;i<len;i++){
        if($($("#hotlabel span")[i]).html()==that){
           var classname=$($("#hotlabel span")[i]).attr("class");
            if(classname=="label_u_active"){
                $($("#hotlabel span")[i]).removeClass().addClass("label_u");
            }
            if(classname=="label_g_active"){
                $($("#hotlabel span")[i]).removeClass().addClass("label_g");
            }
        }
    }
    for(var i=0;i<len_o;i++){
        if($($("#label_org span")[i]).html()==that){
            $($("#label_org span")[i]).removeClass().addClass("label_g")
        }
    }
    for(var i=0;i<len_u;i++){
        if($($("#label_user span")[i]).html()==that){
            $($("#label_user span")[i]).removeClass().addClass("label_u")
        }
    }
}
function addViplabel(obj,ev) {
    var id=sessionStorage.getItem("id");
    var store_id=sessionStorage.getItem("store_id");
    var that="";//获取贴上标签的名字
    var total=parseInt($(".span_total").html())+1;
    if(total>50){
        frame();
        $('.frame').html('最多添加50个标签');
        return ;
    }
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['vip_code']=id;
    param['label_id']="";
    param['store_code']=store_id;
    whir.loading.add("",0.5);
    oc.postRequire("post","/VIP/label/addRelViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var rid=JSON.parse(msg.list).label_id;
            var html="";
            if(cls==""||cls==undefined){
                html='<span class="label_u" data-rid="'+rid+'">'+val+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }else{
                html='<span class='+cls+' data-rid="'+rid+'">'+txt+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }
            $(".span_total").html(total);
            $("#label_box").append(html);
            if(obj=="btn"){
                that = val;
            }else if(obj=="li"){
                that = txt;
            }
            var len=$("#hotlabel span").length;
            var len_o=$("#label_org span").length;
            var len_u=$("#label_user span").length;
            for(var i=0;i<len;i++){
                if($($("#hotlabel span")[i]).html()==that){
                    var classname=$($("#hotlabel span")[i]).attr("class");
                    if(classname=="label_u"){
                        $($("#hotlabel span")[i]).removeClass().addClass("label_u_active");
                    }
                    if(classname=="label_g"){
                        $($("#hotlabel span")[i]).removeClass().addClass("label_g_active");
                    }
                }
            }
            for(var i=0;i<len_o;i++){
                if($($("#label_org span")[i]).html()==that){
                    $($("#label_org span")[i]).removeClass().addClass("label_g_active")
                }
            }
            for(var i=0;i<len_u;i++){
                if($($("#label_user span")[i]).html()==that){
                    $($("#label_user span")[i]).removeClass().addClass("label_u_active")
                }
            }
            if($(ev).attr("class")=="label_u"){
                $(ev).addClass("label_u_active").removeClass("label_u");
            }else if($(ev).attr("class")=="label_g") {
                $(ev).addClass("label_g_active").removeClass("label_g");
            }
        }else if(data.code=="-1"){
            frame();
            $('.frame').html('请勿重复添加');
        }
        whir.loading.remove();
    })
}
$("#labeladd_btn").click(function () {
    var a="btn";
    cls="";
    val=$("#search_input").val().replace(/\s+/g,"");
    // val=val.substring(0,8);
    if(val==""){
        return;
    }
    param['label_name']=val;
    addViplabel(a);
    $("#search_input").val("");
});
//右侧点击添加标签
function clickLabeladd() {
    $("#hotlabel span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
    });
    $("#label_org span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
    });
    $("#label_user span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
});
}
//拖拽
//阻止拖拽默认事件
function allowDrop(ev){
    ev.preventDefault();
}
//拖拽事件
function drop(ev) {
    var param={};
    ev.preventDefault();
    var data=ev.dataTransfer.getData("Text");
    var span=$(document.getElementById(data));
    var clone= $(document.getElementById(data)).clone();
    var label_id=clone.attr("data-id");
    var val=$(clone).text();
    //调用借口
    var id = sessionStorage.getItem("id");
    var store_id = sessionStorage.getItem("store_id");
    var total = parseInt($(".span_total").html()) + 1;
    if(total>50){
        frame();
        $('.frame').html('亲，最多添加50个标签');
        return ;
    }
    param["corp_code"] = sessionStorage.getItem("corp_code");
    param['label_name'] = val;
    param['vip_code'] = id;
    param['label_id'] = "";
    param['store_code'] = store_id;
    whir.loading.add("",0.5);
    oc.postRequire("post", "/VIP/label/addRelViplabel", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var rid = JSON.parse(msg.list);
            var html = "<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i>";
            clone = $(clone).append(html);
            $(clone).attr("data-rid", rid);
            $("#label_box").append(clone);
            $(".span_total").html(total);
            if(span.attr("class")=="label_u"){
                $(span).addClass("label_u_active").removeClass("label_u");
            }else {
                $(span).addClass("label_g_active").removeClass("label_g");
            }
        }else if(data.code =="-1"){
            frame();
            $('.frame').html('请勿重复添加');
        }
        whir.loading.remove();
    })
}

function addVipAlbum(url){//上传照片到相册
     var param_addAblum={};
    param_addAblum["vip_id"]=sessionStorage.getItem("id");
    param_addAblum["vip_name"]=$("#vip_name").html();
    param_addAblum["phone"]=$("#vip_phone").html();
    param_addAblum["card_no"]=$("#vip_card_no").html()?$("#vip_card_no").html():$("#card_wrap_s .vip_card_border").attr("data-cardNo");
    param_addAblum["image_url"]=url;
    param_addAblum["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/vipSaveInfo","",param_addAblum,function(data){
        var AlbumData=JSON.parse(data.message);
        whir.loading.remove("上传中,请稍后...",0.1);
        whir.loading.add("mask",0.1);
        if(data.code=="0"){
            swip_image.push(url);
            $("#editTk").show();
            $("#msg").html("添加成功");
            $("#upAlbum").parent().parent().before("<li>"
                +"<img src='"+AlbumData.oss_url+"'>"
                +"<div class='cancel_img' data-time='"+AlbumData.date+"'></div>"
                    +"<span class='album_date'>"+AlbumData.date.substring(0,11)+"</span>"
                +"</li>")
        }else{
            frame();
            $('.frame').html('添加失败');
        }
    })
}
function getNowFormatDate() {//获取当前日期
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var H=date.getHours();
    var M=date.getMinutes();
    var S=date.getSeconds();
    var m=date.getMilliseconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = ""+year+month+strDate+H+M+S+m;
    return currentdate
}
//页面加载调权限接口
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        for(var i=0;i<actions.length;i++){
           if(actions[i].act_name=="changeVipType"){
                $("#grade_change").show();
            }else if(actions[i].act_name=="edit"){
               $("#change_save").show();
           }else if(actions[i].act_name=="sendSMS"){
               $("#send_code").css("display","inline-block");
           }else if(actions[i].act_name=="recharge"){
               $("#toTopUp").show();
               $("#toRefund").show();
               $("#toRecord").show();
           }
        }
    })
}
function userlist(){
    var _param={};
    _param.store_code=$("#store_name").attr("data-store_code");
    _param.corp_code=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/shop/staff","", _param, function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var html='';
            if(msg.length==0){
                // html='<option value="">无</option>'
            }else {
                for( var i=0;i<msg.length;i++){
                    html+='<li data-code="'+msg[i].user_code+'">'+msg[i].user_name+'</li>';
                }
            }
            $("#user_name_select").append(html);
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    })
}
function vipcardType() {
    var _param={};
    _param.store_code=sessionStorage.getItem("store_code");
    _param.corp_code=sessionStorage.getItem("corp_code");
    $("#vip_card_type_select").empty();
    oc.postRequire("post","/vipCardType/getCardTypesByRole","", _param, function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list);
            var html='';
            if(list.length==0){
                // html='<option value="">无</option>'
            }else {
                for( var i=0;i<list.length;i++){
                    html+='<li data-code="'+list[i].vip_card_type_code+'">'+list[i].vip_card_type_name+'</li>';
                }
            }
            $("#vip_card_type_select").append(html);
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    })
}
function getProvince() {
    var param={};
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        if(data.code == "0"){
            $(".dl_box dl dd").empty();
            var msg = JSON.parse(data.message);
            for(var i=0;i<msg.length;i++){
                $("#province dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" data-name="'+msg[i].location_name+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#province a").click(function () {
                var val=$(this).html();
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(2)").trigger("click");
                $("#county dl dd").empty();
                getCity();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name",$(this).attr("data-name"));
            })
        }else {
            console.log(data.message);
        }
    })
}//获取省份
function getCity() {
    var param={};
    param['higher_level_code'] = $("#province .current").attr("data-code");
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        var msg = JSON.parse(data.message);
        if(data.code == "0"){
            $("#city dl dd").empty();
            for(var i=0;i<msg.length;i++){
                $("#city dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" data-name="'+msg[i].location_name+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#city a").click(function () {
                var val=$("#province .current").html();
                val+='/'+$(this).html();
                var data_name = $("#province .current").attr("data-name")+'/'+$(this).attr("data-name");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(3)").trigger("click");
                getCounty();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name",data_name);
            })
        }else {
            console.log(data.message);
        }
    })
}
function getCounty() {
    var param={};
    param['higher_level_code'] = $("#city .current").attr("data-code");
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        var msg = JSON.parse(data.message);
        if(data.code == "0"){
            $("#county dl dd").empty();
            for(var i=0;i<msg.length;i++){
                $("#county dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" data-name="'+msg[i].location_name+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#county a").click(function () {
                var val=$("#province .current").html()+'/'+$("#city .current").html()+'/'+$(this).html();
                var data_name = $("#province .current").attr("data-name")+'/'+$("#city .current").attr("data-name")+'/'+$(this).attr("data-name");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name",data_name);
                $("#address_container").hide();
            });
        }else {
            console.log(data.message);
        }
    })
}
//获取购物车数据
function getShoppingCart() {
    var param = {};
    param.corp_code = sessionStorage.getItem("corp_code");
    param.vip_id = sessionStorage.getItem("id");
    param.page_num = cart_num;
    param.page_size = 50;
    oc.postRequire("post","/vip/shoppingCarts","",param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            var tr = '';
            if(msg.list){
                var list = msg.list;
                var Num = cart_num || 1;
                var pages = msg.totalpages;
                has_next_list = Num < pages ? true : false;
                for(var i=0;i<list.length;i++){
                    var n = (Num-1)*50+1+i;
                    tr += '<tr>'
                                +'<td width="50px" style="text-align: left;">'
                                +       '<div class="checkbox"><input type="checkbox" name="test" title="全选/取消" class="check"  id="cart${i}"/><label for="cart${i}"></label></div></td>'
                                +'<td width="5%">'+n+'</td>'
                                +'<td width="7%"><img style="width: 40px;height: 40px" src="'+list[i].wx_product_id_image_url+'"></td>'
                                +'<td width="15%"><span title="'+list[i].wx_productitem_id_code+'">'+list[i].wx_productitem_id_code+'</span></td>'
                                +'<td width="15%"><span title="'+list[i].wx_product_id_name+'">'+list[i].wx_product_id_name+'</span></td>'
                                +'<td width="10%">'+list[i].wx_productitem_id_dp_price+'</td>'
                                +'<td width="10%">'+list[i].wx_productitem_id_sc_price+'</td>'
                                +'<td width="10%">'+list[i].count+'</td>'
                                +'<td width="10%">'+list[i].wx_productitem_id_sc_price+'</td>'
                                +'<td width="15%"><span title="'+list[i].createdate+'">'+list[i].createdate+'</span></td>'
                           +'</tr>'
                }
            }else {
                for(var j=0;j<10;j++){
                    j == 4 ? tr += '<tr><td colspan="10">暂无数据</td></tr>' :tr += '<tr><td colspan="10"></td></tr>';
                }
            }
            cart_num == 0 ? $("#shoppingCart tbody").html(tr):$("#shoppingCart tbody").append(tr);
        }else {
            var tr = '';
            for(var j=0;j<10;j++){
                j == 4 ? tr += '<tr><td colspan="10">暂无数据</td></tr>' :tr += '<tr><td colspan="10"></td></tr>';
            }
            $("#shoppingCart tbody").html(tr)
        }
    });
}
$("#address_nav a").click(function () {//选择地址
    var index=$(this).index();
    $(this).addClass("address_liActive");
    $(this).siblings().removeClass("address_liActive");
    $(".address_content").children('.dl_box').eq(index).show();
    $(".address_content").children('.dl_box').eq(index).siblings().hide();
});
$('#change_save').click(function () {
    if(name=="无"){
        name="";
    }
    var address = $("#STORE_address").val().split("/");
    if(address.length < 3 && $("#STORE_address").val() != ""){
        $("#editTk").show();
        $("#msg").html("省市区请填写完整");
        return ;
    }
    var param={};
    param.vip_id=$('#vip_name_edit').attr('data_vip_id');
    param.vip_name=$('#vip_name_edit').val();
    param.corp_code=sessionStorage.getItem('corp_code');
    param.phone=$('#vip_phone_edit').val();
    param.birthday=$('#vip_birthday_edit').val().split('-').join('');
    param.card_no=$('#vip_card_no_edit').val();
    param.sex=$("#USER_SEX").val();
    param.user_code=$("#user_name_edit").attr("data-code");
    param.user_name=$("#user_name_edit").val();
    param.cardno=$("#vip_card_no_edit").val();
    param.store_code=$("#store_name_edit").attr("data-code");
    param.vip_card_type=$("#vip_card_type_edit").val();
    param.address=$("#STORE_address").attr("data-name");
    param.street=$("#address_details_input").val().trim();
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/updateVip","",param,function(data){
        whir.loading.remove("",0.5);
        whir.loading.add("mask");
        if(data.code==0){
            $("#editTk").show();
            $("#msg").html("修改成功");
        }else if(data.code==-1){
           $("#editTk").show();
            $("#msg").html(data.message==""?"修改失败":data.message);
        }
    });
});
//获取回访反馈
function getCallBack() {
    var param = {};
    var action = $("#vip_call_back_filter input").eq(0).val() =='全部' ? "" : $("#vip_call_back_filter input").eq(0).val();
    var initiator = $("#vip_call_back_filter input").eq(1).val() =='全部' ? "" : $("#vip_call_back_filter input").eq(1).val();
    var feedback_status = $("#vip_call_back_filter input").eq(3).val() =='全部' ? "" : $("#vip_call_back_filter input").eq(3).val();
    var type = $("#vip_call_back_filter input").eq(4).val() =='全部' ? "" : $("#vip_call_back_filter input").eq(4).val();
    param.corp_code = sessionStorage.getItem("corp_code");
    param.vip_id = sessionStorage.getItem("id");
    param.page_num = back_num;
    param.page_size = 50;
    param.list = [
        {"screen_key":"action","screen_value":action},
        {"screen_key":"initiator","screen_value":initiator},
        {"screen_key":"user_name","screen_value":$("#vip_call_back_filter input").eq(2).val()},
        {"screen_key":"feedback_status","screen_value":feedback_status},
        {"screen_key":"type","screen_value":type},
        {"screen_key":"created_date","screen_value":{"start":$("#vip_call_back_filter input").eq(5).val(),"end":$("#vip_call_back_filter input").eq(6).val()}},
        ];
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/visitAndFeedback","",param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            var tr = '';
            var list = msg.list;
            var pages = msg.totalpages;
            next_page = back_num < pages ? true : false;
            if(list.length != 0){
                for(var i=0;i<list.length;i++){
                    var n = (back_num-1)*50+i+1;
                    tr += '<tr>'
                        +'<td width="5%">'+n+'</td>'
                        +'<td width="10%">'+list[i].action+'</td>'
                        +'<td width="10%">'+list[i].initiator+'</td>'
                        +'<td width="10%">'+list[i].user_name+'</span></td>'
                        +"<td width='20%'><span title='"+list[i].message_content+"'>"+list[i].message_content+"</span></td>"
                        +'<td width="10%">'+list[i].feedback_status+'</td>'
                        +'<td width="10%">'+list[i].feedback_result+'</td>'
                        +'<td width="10%">'+list[i].type+'</td>'
                        +'<td width="15%">'+list[i].created_date+'</span></td>'
                        +'</tr>'
                }
            }else {
                for(var j=0;j<10;j++){
                    j == 4 ? tr += '<tr><td colspan="10" style="text-align: center">暂无数据</td></tr>' :tr += '<tr><td colspan="10"></td></tr>';
                }
            }
            back_num == 1 ? $("#callback_table tbody").html(tr):$("#callback_table tbody").append(tr);
            whir.loading.remove();
        }else {
            var tr = '';
            for(var j=0;j<10;j++){
                j == 4 ? tr += '<tr><td colspan="10" style="text-align: center">暂无数据</td></tr>' :tr += '<tr><td colspan="10"></td></tr>';
            }
            $("#callback_table tbody").html(tr);
            whir.loading.remove();
        }
    });
}
//获取图标筛选的年份
function geyCurrentYear() {
    var date = new Date(),
        Year = date.getFullYear(),
        temp = '<li>全部</li>',
        n = 10;
    while(n > 0){
        temp += '<li>'+Year+'</li>';
        $("#chart_year_select").html(temp);
        n--;
        Year--;
    }
}
$(function(){
    var key_val=sessionStorage.getItem("key_val");//取页面的function_code
    key_val=JSON.parse(key_val);
    var funcCode=key_val.func_code;
    $.get("/detail?funcCode="+funcCode+"", function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.columns;
            if(list.length>0){
                $("#change_save").show();
                for(var i=0;i<list.length;i++){
                    if(list[i].column_name=="sex"){
                        $("#USER_SEX").css({"background":"#ffffff url('../img/btn_dropmenu.png') no-repeat 100% 50%","background-size":"30px"});
                        $("#USER_SEX").addClass("input_select");
                    }
                    if(list[i].column_name=="vip_name"){
                        $("#vip_name_edit").removeAttr("readonly");
                        $("#vip_name_edit").css("background","#FFFFFF");
                    }
                    if(list[i].column_name=="cardno"){
                        $("#vip_card_no_edit").removeAttr("readonly");
                        $("#vip_card_no_edit").css("background","#FFFFFF");
                    }
                    if(list[i].column_name=="vip_card_type"){
                        $("#vip_card_type_edit").click(function () {
                            if($("#vip_card_type_select").css("display")=="none"){
                                vipcardType();
                            }
                           $("#vip_card_type_select").toggle();
                        });
                        $("#vip_card_type_select").on("click","li",function () {
                            $("#vip_card_type_edit").val($(this).html());
                            $("#vip_card_type_edit").attr("data-code",$(this).attr("data-code"));
                        });
                        $("#vip_card_type_edit").css({"background":"#ffffff url('../img/btn_dropmenu.png') no-repeat 100% 50%","background-size":"30px"});
                    }
                    if(list[i].column_name=="vip_phone"){
                        $("#vip_phone_edit").removeAttr("readonly");
                        $("#vip_phone_edit").css("background","#FFFFFF");
                    }
                    if(list[i].column_name=="user_name"){
                        $("#user_name_edit").addClass("input_select");
                        $("#user_name_select").on("click","li",function () {
                           $("#user_name_edit").val($(this).html());
                           $("#user_name_edit").attr("data-code",$(this).attr("data-code"));
                        });
                        $("#user_name_edit").css({"background":"#ffffff url('../img/btn_dropmenu.png') no-repeat 100% 50%","background-size":"30px"});
                    }
                    if(list[i].column_name=="vip_birthday"){
                        $("#vip_birthday_edit").css("background","");
                        $("#vip_birthday_edit").addClass("laydate-icon");
                        $("#vip_birthday_edit").attr("onclick","laydate({elem: '#vip_birthday_edit',istime: false, format: 'YYYY-MM-DD'})");
                    }
                    if(list[i].column_name=="area"){
                        $("#STORE_address").addClass("input_select");
                        $("#STORE_address").css("background","url(../img/btn_dropmenu.png) 100% 50% / 30px no-repeat rgb(255, 255, 255)");
                    }
                    if(list[i].column_name=="address"){
                        $("#address_details_input").css("background","#fff");
                        $("#address_details_input").removeAttr("readonly");
                    }
                }
            }else if(list.length==0){
                $("#change_save").hide();
            }
        }
    });
    if(funcCode != 'F0044'){
        getVipInfo();
        getConsumCount();
    }
    moreSearch();
    qjia();
    chartData("");
    getProvince();
    geyCurrentYear();
});
//滚动分页
$("#shoppingCart").unbind("scroll").bind("scroll",function(){
    var offsetHeight = $(this).height(),
        scrollHeight = $(this)[0].scrollHeight,
        scrollTop = $(this)[0].scrollTop;
    if(scrollTop + offsetHeight >= scrollHeight && scrollTop != 0){
        if(has_next_list){
            cart_num++;
        }else{
            return
        }
        getShoppingCart();
    }
});
$("#callback_table").unbind("scroll").bind("scroll",function(){
    var offsetHeight = $(this).height(),
        scrollHeight = $(this)[0].scrollHeight,
        scrollTop = $(this)[0].scrollTop;
    if(scrollTop + offsetHeight >= scrollHeight && scrollTop != 0){
        if(next_page){
            back_num++;
        }else{
            return
        }
        getCallBack();
    }
});



