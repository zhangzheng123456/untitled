var oc = new ObjectControl();
var page=1;
var param={};//定义传值对象
var cls="";//标签搜索下class名
var txt="";//标签搜索下标签名
var val="";//贴上input值
var swip_image = [];//图片切换播放
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
var chart_data;//图表数据
var chart={
    "myChart":"",
    "myChart1":"",
    "myChart2":"",
    "myChart3":"",
    "myChart4":"",
    "myChart5":"",
    "myChart6":""
};
function getConsumCount(){//获取会员信息
    //whir.loading.add("",0.5);//加载等待框
    whir.loading.add("",0.5);//加载等待框
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var HTML="";
       var Ablum_all_html="";
       var LABEL="";
       var LABELALL="";
        if(album.length!==0){
            $("#upAlbum").parent().parent().siblings().remove();
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
                $("#upAlbum").parent().parent().before(Ablum_all_html)
            }
        }else {
            HTML+="<p>暂无图片</p>";
        }
        $("#images").html(HTML);
        //$("#Ablum-all").html(Ablum_all_html);
        if(label.length!==0){
            for(var i=0;i<label.length;i++){
                LABEL+="<span>"+label[i].label_name+"</span>";
                LABELALL+="<span class='label_u' data-rid="+label[i].rid+">"+label[i].label_name+"<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i></span>"
            }
        }else{
            LABEL+="<p>暂无标签</p>";
        }
        //统计已有标签
        $(".span_total").html(label.length);
        $("#labels").html(LABEL);
        $("#label_box").html(LABELALL);
        img_hover();
        whir.loading.remove();
    })
}
function compare(value1, value2) {
    if (value1 < value2) {
        return -1;
    } else if (value1 > value2) {
        return 1;
    } else {
        return 0;
    }

}
function chartShow(order) {
    var key_val = sessionStorage.getItem("key_val");//取页面的function_code
    key_val = JSON.parse(key_val);
    var funcCode = key_val.func_code;
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
                console.log("成功");
            } else {
                var list = JSON.parse(data.message);
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
                                var ID = $(this).attr("data-id");
                                init_chart(ID);
                                $("#chart_analyze").append($(this));
                                //$("#add_chart").before($(this));
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
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
        }
        var option_pie = {
            color: ['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2', '#5C778F', '#4D5F7C', '#444960', '#2C3244'],
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
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
        }
        var option_pie_series = {
            color: ['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2', '#5C778F', '#4D5F7C', '#444960', '#2C3244'],
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
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr=[];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
        }
        var option_week_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis'
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
                            name: '周变'
                        }
                    ]
                }
            ]
        };
        Arr.sort(compare);
        for(var j=0;j<option_week_radar.polar[0].indicator.length;j++){
            option_week_radar.polar[0].indicator[j].max=Arr[Arr.length-1]*1.1;
        }
        chart.myChart1 = echarts.init(document.getElementById('weeks'));
        chart.myChart1.setOption(option_week_radar);
        reSize(chart.myChart1);
    }
    if (id == "price") {
        var data=chart_data.Price;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var ArrName=[];
        var ArrValue=[];
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
        }
        chart.myChart2 = echarts.init(document.getElementById('price'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    return params.seriesName + ' :' + params.value;
                }
            },
            grid: {
                borderWidth: 0,
                x: '100',
                y: '20',
                x2: '0',
                y2: '20'
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
    if (id == "times") {
        var data=chart_data.Time;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var ArrName=[];
        var ArrValue=[];
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
        }
        chart.myChart3 = echarts.init(document.getElementById('times'));
        var option_line = {
            color: ['#6DADC8'],
            tooltip: {
                trigger: 'item'
            },
            grid: {
                borderWidth: 0,
                x: '50',
                y: '20',
                x2: '20',
                y2: '50'
            },
            xAxis: [
                {
                    axisLine: {
                        lineStyle: {color: '#58A0C0'}
                    },
                    splitLine: {
                        show: false
                    },
                    axisLabel: {
                        rotate: 45
                    },
                    axisTick: {
                        show: false
                    },
                    type: 'category',
                    boundaryGap: false,
                    data: ArrName
                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    splitArea: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#999',
                            type: 'dashed'
                        }
                    },
                    type: 'value'
                }
            ],
            series: [
                {
                    itemStyle: {
                        symbolSize: '0',
                        normal: {
                            borderRadius: 0,
                            nodeStyle: {
                                borderRadius: 0
                            }
                        }
                    },
                    name: '购买时段',
                    type: 'line',
                    stack: '总量',
                    symbolSize: 0,
                    smooth: false,
                    data: ArrValue
                }
            ]
        };
        chart.myChart3.setOption(option_line);
        reSize(chart.myChart3);
    }
    if (id == "month") {
        var data=chart_data.Month;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr=[];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
        }
        var option_month_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis'
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
                            name: '月变'
                        }
                    ]
                }
            ]
        };
        Arr.sort(compare);
        for(var j=0;j<option_month_radar.polar[0].indicator.length;j++){
            option_month_radar.polar[0].indicator[j].max=Arr[Arr.length-1]*1.1;
        }
        chart.myChart4 = echarts.init(document.getElementById('month'));
        chart.myChart4.setOption(option_month_radar);
        reSize(chart.myChart4);
    }
    if (id == "areas") {
        var data=chart_data.Area;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        chart.myChart6 = echarts.init(document.getElementById('areas'));
        var option_map = {
            tooltip: {
                trigger: 'item'
            },
            dataRange: {
                itemWidth: 5,
                itemGap: 0.2,
                color: ['#A7CFD5', '#3C95A2'],
                splitNumber: '20',
                orient: 'horizontal',
                min: 0,
                max: 2500,
                x: 'left',
                y: 'top',
                text: ['低','高']      // 文本，默认为数值文本
            },
            series: [
                {
                    name: 'iphone3',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
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
    function reSize(chart) {
        window.addEventListener("resize", function () {
            chart.resize();
        });
    }
}
//图表数据
function chartData() {
    var param={};
    param["type"]="vipInfo";
    param['corp_code'] = localStorage.getItem('corp_code');
    param["vip_id"] = sessionStorage.getItem("id");
    oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            chart_data= msg;
            chartShow();
        }else if(data.code == -1){
            console.log(data.message);
        }
    });
}
//图标新增弹窗
$("#add_chart").click(function () {
    var arr = whir.loading.getPageSize();
    var w = arr[0];
    var h = arr[1];
    var left = (arr[0] - $("#chart_add_wrap").width()) / 2;
    var tp = (arr[3] - $("#chart_add_wrap").height()) / 2;
    $("#chart_add_wrap").css({"left": left, "top": tp, "position": "fixed"});
    $("#p").css({"width": w, "height": h});
    $("#chart_add_wrap").show();
    $("#p").show();
    $("#chart_analyze .chart_module").each(function (i, e) {
        var val = $(this).find(".drag_area").children("span").html();
        if ($(this).css("display") == "block") {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_checkbox span").html() == val) {
                    $(this).find(".check")[0].checked = true;
                }
            });
        } else {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_checkbox span").html() == val) {
                    $(this).find(".check")[0].checked = false;
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
    $("#chart_add_details input[type='checkbox']:checked").each(function () {
        var val = $(this).nextAll("span").html();
        $("#chart_analyze .chart_module").each(function () {
            if ($(this).css("display") == "none" && val == $(this).find(".drag_area span").html()) {
                $(this).show();
                var ID = $(this).attr("data-id");
                init_chart(ID);
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
    chartShow(order);
    $("#chart_add_wrap").hide();
    $("#p").hide();
});
//图表分析下拉
$(".chart_analyze_condition").click(function () {
    $(this).children("ul").toggle();
});
$('.chart_analyze_condition ul li').click(function () {
    $(this).parent().prevAll("span").html($(this).html());
    var ID=$(this).parents(".chart_module").attr("data-id");
    var type=$(this).html();
    type=="按金额分析"?type="amt":"";
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
    whir.loading.add("mask",0.5);
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
    $("#label_user").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $("#label_org").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $(".search_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $("#label_box").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
});
//回到会员列表
$("#VIP_LIST_info").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});

//回到会员列表
$("#VIP_LIST").click(function(){
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
        getVipPoints($("#corp_code").html(),'2')
    }
    if($(this).attr('data-name')=='label'){
        gethotVIPlabel();
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
    param["rid"]=rid;
    oc.postRequire("post","/VIP/label/delRelViplabel","",param,function(data){
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
        $('.frame').html('亲，最多添加50个标签');
        return ;
    }
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['vip_code']=id;
    param['label_id']="";
    param['store_code']=store_id;
    oc.postRequire("post","/VIP/label/addRelViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var rid=JSON.parse(msg.list);
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
function allowDrop(ev)
{
    ev.preventDefault();
}
//拖拽事件
function drop(ev)
{
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
    })
}

function upLoadAlbum(){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('upAlbum').addEventListener('change', function (e) {
        whir.loading.add("上传中,请稍后...",0.5);
        var file = e.target.files[0];
        var time=getNowFormatDate();
        var corp_code=sessionStorage.getItem("corp_code");
        var vip_id=sessionStorage.getItem("id");
        var storeAs='Album/Vip/iShow/'+corp_code+'_'+vip_id+'_'+time+'.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
            $("#upAlbum").val("");
            addVipAlbum(url)
        }).catch(function (err) {
             //console.log(err);
        });
    });
}
function addVipAlbum(url){//上传照片到相册
     var param_addAblum={};
    param_addAblum["vip_id"]=sessionStorage.getItem("id");
    param_addAblum["vip_name"]=$("#vip_name").html();
    param_addAblum["phone"]=$("#vip_phone").html();
    param_addAblum["card_no"]=$("#vip_card_no").html();
    param_addAblum["image_url"]=url;
    param_addAblum["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/vipSaveInfo","",param_addAblum,function(data){
        var AlbumData=data;
        if(data.code=="0"){
            swip_image.push(url);
            frame();
            $('.frame').html('添加成功');
            $("#upAlbum").parent().parent().before("<li>"
                +"<img src='"+url+"'>"
                +"<div class='cancel_img' data-time='"+AlbumData.message+"'></div>"
                    +"<span class='album_date'>"+AlbumData.message.substring(0,11)+"</span>"
                +"</li>")
        }else{
            frame();
            $('.frame').html('添加失败');
        }
        whir.loading.remove();
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
           }
        }
    })
}
//会员资料基本信息更改
$('#change_save').click(function () {
    var param={}
    param.vip_id=$('#vip_name_edit').attr('data_vip_id');
    param.vip_name=$('#vip_name_edit').val();
    param.corp_code=sessionStorage.getItem('corp_code');
    param.phone=$('#vip_phone_edit').val();
    param.birthday=$('#vip_birthday_edit').val().split('-').join('');
    param.card_no=$('#vip_card_no_edit').val();
    oc.postRequire("post","/vip/updateVip","",param,function(data){
        if(data.code==0){
            art.dialog({
                zIndex:10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "保存成功"
            });
            return;
        }else if(data.code==-1){
            art.dialog({
                zIndex:10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "保存失败"
            });
            return;
        }
    });
});
$(function(){
    getConsumCount();
    upLoadAlbum();
    moreSearch();
    qjia();
    chartData();
    chartShow();
    GET();
});


