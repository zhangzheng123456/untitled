/**
 * Created by Administrator on 2017/9/20.
 */
var color=["#7ba442","#3697b5","#7a4db0","#ce815b","#ae605d"];
var allMyChart={};
var option={
    color:color,
    tooltip: {
        trigger: 'item',
        //formatter: "{a} <br/>{b}: {c} ({d}%)"
        formatter: function (params) {
            return params.seriesName+"<br>"+params.name+":"+(params.value==0?0:formatCurrency(params.value))+"("+params.percent+"%)";
        },
        textStyle:{
            fontSize:12
        }
    },
    series: {
        name:'渠道会员占比',
        type:'pie',
        radius: ['35%', '40%'],
        startAngle:'0',
        label: {
            normal: {
                formatter: '{b|{b}}',
                rich: {
                    b:{
                        color: '#fff'
                    }
                }
            }
        },
        labelLine: {
            normal: {
//                            show: false
            }
        },
        data:[]
    }
};
var option_1={
    color:["#1a1e28","#41b8db"],
    tooltip: {
        show:false
    },
    series: {
        name:'活跃会员占比',
        type:'pie',
        silent  :true,
        radius: ['35%', '40%'],
        avoidLabelOverlap: false,
        label: {
            normal: {
                show: false

            },
            emphasis: {
                show: false

            }
        },
        labelLine: {
            normal: {
                show: false
            }
        },
        data:[
            {value:0, name:'非活跃会员占比'},
            {value:0, name:'活跃会员占比',label: {
                normal: {
                    show: true,
                    formatter:'{d}%',
                    position: 'center',
                    textStyle: {
                        fontSize: '12',
                        fontWeight: 'bold'
                    },
                    emphasis: {
                        show: false,
                        textStyle: {
                            fontSize: '12',
                            fontWeight: 'bold'
                        }
                    }
                }}
            }
        ]
    }
};
var oc = oc || new ObjectControl();
function initChartData(type,Data,selector,chart_name){
    if(Data.length==0){
        noDataView("#"+selector);
    }else{
        var data=[];
        allMyChart[chart_name]=echarts.init(document.getElementById(selector));
        for(var i=0;i<Data.length;i++){
            if(type=="trade"){
                data.push({
                    name:Data[i].name,
                    value:Data[i].amt
                })
            }else{
                data.push({
                    name:Data[i].name,
                    value:Data[i].size
                })
            }
        }
        option.series.data=data;
        option.series.name=$("#"+selector).prev(".model_title").html();
        allMyChart[chart_name].setOption(option);
    }
    $("#"+selector).parent().next(".spinner_warp").hide();
}
function noData(text){
    return '<div class="notData" >'+text+'</div>';
}
function formatCurrency(num) {
    if(num)
    {
        //将num中的$,去掉，将num变成一个纯粹的数据格式字符串
        num = num.toString().replace(/\$|\,/g,'');
        //如果num不是数字，则将num置0，并返回
        if(''==num || isNaN(num)){return 'Not a Number ! ';}
        //如果num是负数，则获取她的符号
        var sign = num.indexOf("-")> 0 ? '-' : '';
        //如果存在小数点，则获取数字的小数部分
        var cents = num.indexOf(".")> 0 ? num.substr(num.indexOf(".")) : '';
        cents = cents.length>1 ? cents : '' ;//注意：这里如果是使用change方法不断的调用，小数是输入不了的
        //获取数字的整数数部分
        num = num.indexOf(".")>0 ? num.substring(0,(num.indexOf("."))) : num ;
        //如果没有小数点，整数部分不能以0开头
        if('' == cents){ if(num.length>1 && '0' == num.substr(0,1)){return 'Not a Number ! ';}}
        //如果有小数点，且整数的部分的长度大于1，则整数部分不能以0开头
        else{if(num.length>1 && '0' == num.substr(0,1)){return 'Not a Number ! ';}}
        //针对整数部分进行格式化处理，这是此方法的核心，也是稍难理解的一个地方，逆向的来思考或者采用简单的事例来实现就容易多了
        /*
         也可以这样想象，现在有一串数字字符串在你面前，如果让你给他家千分位的逗号的话，你是怎么来思考和操作的?
         字符串长度为0/1/2/3时都不用添加
         字符串长度大于3的时候，从右往左数，有三位字符就加一个逗号，然后继续往前数，直到不到往前数少于三位字符为止
         */
        for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
        {
            num = num.substring(0,num.length-(4*i+3))+','+num.substring(num.length-(4*i+3));
        }
        //将数据（符号、整数部分、小数部分）整体组合返回
        return (sign + num + cents);
    }
}
function noDataView(selector,text){
    $(selector).children().not(".notData").hide();
    if($(selector).children(".notData").length==0){
        $(selector).append(noData(text));
    }else{
        $(selector).children(".notData").html(text).show();
    }
}
function showData(data,type){
    var msg=JSON.parse(data.message);
    if(type=="vip"){
        if(data.code==0){
            var actvip=msg.actvip;
            var cardType=msg.cardType;
            var newold=msg.newold;
            var channel_vip=msg.channel_vip;
            var brand=msg.brand;
            var cycle=msg.cycle;
            $('#allVipNum').html(formatCurrency(msg.vipAllSize));
            initChartData(type,channel_vip,"div1","myChart_1");
            initChartData(type,brand,"div2","myChart_2");
            initChartData(type,cardType,"div4","myChart_3");
            initChartData(type,newold,"div3","myChart_4");
            initChartData(type,cycle,"div5","myChart_5");
            allMyChart["myChart_7"]=echarts.init(document.getElementById("div7"));
            option_1.series.data[0].value=msg.vipAllSize-actvip[1].size;
            option_1.series.data[1].value=actvip[1].size;
            allMyChart["myChart_7"].setOption(option_1);

            allMyChart["myChart_6"]=echarts.init(document.getElementById("div6"));
            option_1.series.data[0].value=msg.vipAllSize-actvip[0].size;
            option_1.series.data[1].value=actvip[0].size;
            allMyChart["myChart_6"].setOption(option_1);

            $("#div6,#div7").parent().next(".spinner_warp").hide();
        }else{
            noDataView("#div1","获取数据失败");
            noDataView("#div2","获取数据失败");
            noDataView("#div3","获取数据失败");
            noDataView("#div4","获取数据失败");
            noDataView("#div5","获取数据失败");
            noDataView("#div6","获取数据失败");
            noDataView("#div7","获取数据失败");
            $("#vip_content .spinner_warp").hide();
            $('#allVipNum').html(0);
        }
    }
    if(type=="trade"){
        if(data.code==0){
            var list=msg.list;
            var li="";
            initChartData(type,list,"div8","myChart_8");
            allMyChart["myChart_9"]=echarts.init(document.getElementById("div9"));
            for(var i=0;i<list.length;i++){
                li+='<li>'+
                    '<span class="list_num"><i>'+(i+1)+'</i></span>'+
                    '<span>'+list[i].name+'</span>'+
                    '<span class="ranking_data">'+formatCurrency(list[i].amt)+'</span>'+
                    '<span class="ranking_data">'+list[i].rate+'</span>'+
                    '</li>'
            }
            $("#channelRank").html(li);
            $("#channel_content_warp").next(".spinner_warp").hide();
            //线上线下业绩占比
            option.series.name=$("#div9").prev(".model_title").html();
            option.series.data=[{
                name:"线上",
                value:msg.up_amt
            },{
                name:"线下",
                value:msg.down_amt
            }];
            $("#div9").parent().next(".spinner_warp").hide();
            allMyChart["myChart_9"].setOption(option);
        }else{
            noDataView("#div8","获取数据失败");
            noDataView("#div9","获取数据失败");
            allMyChart.myChart_9.dispose();
            allMyChart.myChart_8.dispose();
            $("#channelRank").html(noData("获取数据失败"));
            $("#trade_content .spinner_warp").hide();
        }
    }
}
function getData(type){
    var param={
        type:type,
        start_time:$('#search').attr("data-start"),
        end_time:$('#search').attr("data-end")
    };
    oc.postRequire("post", "/board/vipAnaly", "", param, function (data) {
        showData(data,type)
    },function(){
        if(type=="vip"){
            $("#vip_content .spinner_warp").hide();
            $('#allVipNum').html(0);
            $("#vip_content .data_view_block div:first-child>div").map(function(){
                noDataView("#"+$(this).attr("id"),"获取数据失败");
            })
        }
        if(type=="trade"){
            $("#trade_content .spinner_warp").hide();
            noDataView("#div8","获取数据失败");
            noDataView("#div9","获取数据失败");
            $("#channelRank").html(noData("获取数据失败"));
        }
    })
}
$('#sure_select').click(function(){
    getData("trade");
    $("#div8").parent().next(".spinner_warp").show();
    $("#div9").parent().next(".spinner_warp").show();
    $("#channel_content_warp").next(".spinner_warp").show();
});
$(function(){
    var thirtyDate=thirtyDays();
    $("#search").attr("data-start",thirtyDate.t2);
    $("#search").attr("data-end",thirtyDate.t1);
    laydate.render({
        elem: '#search'
        ,range: true,
        value:thirtyDate.t2+" - "+thirtyDate.t1,
        done: function(value, date, endDate){
            if(value==""){
                $("#search").attr("data-start","");
                $("#search").attr("data-end","");
            }else{
                value=value.split(" - ");
                $("#search").attr("data-start",value[0]);
                $("#search").attr("data-end",value[1]);
            }

        }
    });
    window.addEventListener("resize", function () {
        allMyChart.myChart_1!=undefined && (allMyChart.myChart_1.isDisposed()==undefined) && allMyChart.myChart_1.resize();
        allMyChart.myChart_2!=undefined && (allMyChart.myChart_2.isDisposed()==undefined) && allMyChart.myChart_2.resize();
        allMyChart.myChart_3!=undefined && (allMyChart.myChart_3.isDisposed()==undefined) && allMyChart.myChart_3.resize();
        allMyChart.myChart_4!=undefined && (allMyChart.myChart_4.isDisposed()==undefined) && allMyChart.myChart_4.resize();
        allMyChart.myChart_5!=undefined && (allMyChart.myChart_5.isDisposed()==undefined) && allMyChart.myChart_5.resize();
        allMyChart.myChart_6!=undefined && (allMyChart.myChart_6.isDisposed()==undefined) && allMyChart.myChart_6.resize();
        allMyChart.myChart_7!=undefined && (allMyChart.myChart_7.isDisposed()==undefined) && allMyChart.myChart_7.resize();
        allMyChart.myChart_8!=undefined && (allMyChart.myChart_8.isDisposed()==undefined) && allMyChart.myChart_8.resize();
        allMyChart.myChart_9!=undefined && (allMyChart.myChart_9.isDisposed()==undefined) && allMyChart.myChart_9.resize();

    });
    $(".ranking_content ").niceScroll({
        cursorborder: "0 none",
        boxzoom: false,
        cursorcolor: " rgba(255,255,255,0.5)",
        background: " rgba(0,0,0,0.2)",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 30,
        autohidemode:"leave"
    });
    getData("vip");
    getData("trade");
});
function timeForMat (count) {
    // 拼接时间
    var  time1 = new Date();
    time1.setTime(time1.getTime());
    var Y1 = time1.getFullYear();
    var M1 = ((time1.getMonth() + 1) > 10 ? (time1.getMonth() + 1) : '0' + (time1.getMonth() + 1));
    var D1 = (time1.getDate() > 10 ? time1.getDate() : '0' + time1.getDate());
    var timer1 = Y1 + '-' + M1 + '-' + D1; // 当前时间
    var time2 = new Date();
    time2.setTime(time2.getTime() - (24 * 60 * 60 * 1000 * count));
    var Y2 = time2.getFullYear();
    var M2 = ((time2.getMonth() + 1) > 10 ? (time2.getMonth() + 1) : '0' + (time2.getMonth() + 1));
    var D2 = (time2.getDate() > 10 ? time2.getDate() : '0' + time2.getDate());
    var timer2 = Y2 + '-' + M2 + '-' + D2; // 之前的7天或者30天
    return {
        t1: timer1,
        t2: timer2
    }
}
function thirtyDays () {
    // 获取最近30天
    var timer = timeForMat(30);
    return timer
}
var pendingRequests = {};
$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
    var key = options.url;
    var name = JSON.parse(originalOptions.data.param).message.type;
    if(key == "/board/vipAnaly" && !pendingRequests[name]){
        pendingRequests[name] = jqXHR;
    }else if(!pendingRequests[key]){
        pendingRequests[key] = jqXHR;
    }else {
        // jqXHR.abort();    //放弃后触发的提交
        console.log(pendingRequests[name]);
        pendingRequests[name]?pendingRequests[name].abort(): pendingRequests[key].abort();// 放弃先触发的提交
        pendingRequests[name]=jqXHR
    }
    var complete = options.complete;
    options.complete = function(jqXHR, textStatus) {
        //pendingRequests[key] = null;
        //pendingRequests[name] = null;
        if ($.isFunction(complete)) {
            complete.apply(this, arguments);
        }
    };
});