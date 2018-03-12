/**
 * Created by Administrator on 2017/9/20.
 */
$(function(){
    $(".ranking_content,.select_list,.time_details").niceScroll({
        cursorborder: "0 none",
        boxzoom: false,
        cursorcolor: " rgba(255,255,255,0.5)",
        background: " rgba(0,0,0,0.2)",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 30,
        autohidemode:"leave"
    });
    select_time.init();
    $(".ranking_content").getNiceScroll().resize();
    toggleSelect();
});
var color=["#7ba442","#3697b5","#7a4db0","#ce815b","#ae605d"];
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
    //if(num==0){
    //    return 0
    //}else if(num)
    //{
    //    //将num中的$,去掉，将num变成一个纯粹的数据格式字符串
    //    num = num.toString().replace(/\$|\,/g,'');
    //    //如果num不是数字，则将num置0，并返回
    //    if(''==num || isNaN(num)){return 'Not a Number ! ';}
    //    //如果num是负数，则获取她的符号
    //    var sign = num.indexOf("-")> 0 ? '-' : '';
    //    //如果存在小数点，则获取数字的小数部分
    //    var cents = num.indexOf(".")> 0 ? num.substr(num.indexOf(".")) : '';
    //    cents = cents.length>1 ? cents : '' ;//注意：这里如果是使用change方法不断的调用，小数是输入不了的
    //    //获取数字的整数数部分
    //    num = num.indexOf(".")>0 ? num.substring(0,(num.indexOf("."))) : num ;
    //    //如果没有小数点，整数部分不能以0开头
    //    if('' == cents){ if(num.length>1 && '0' == num.substr(0,1)){return 'Not a Number ! ';}}
    //    //如果有小数点，且整数的部分的长度大于1，则整数部分不能以0开头
    //    else{if(num.length>1 && '0' == num.substr(0,1)){return 'Not a Number ! ';}}
    //    //针对整数部分进行格式化处理，这是此方法的核心，也是稍难理解的一个地方，逆向的来思考或者采用简单的事例来实现就容易多了
    //    /*
    //     也可以这样想象，现在有一串数字字符串在你面前，如果让你给他家千分位的逗号的话，你是怎么来思考和操作的?
    //     字符串长度为0/1/2/3时都不用添加
    //     字符串长度大于3的时候，从右往左数，有三位字符就加一个逗号，然后继续往前数，直到不到往前数少于三位字符为止
    //     */
    //    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
    //    {
    //        num = num.substring(0,num.length-(4*i+3))+','+num.substring(num.length-(4*i+3));
    //    }
    //    //将数据（符号、整数部分、小数部分）整体组合返回
    //    return (sign + num + cents);
    //}
}
var option={
    color:color,
    tooltip: {
        trigger: 'item',
        //formatter: "{a} <br/>{b}: {c} ({d}%)",
        formatter: function (params) {
            return params.seriesName+"<br>"+params.name+":"+(params.value==0?0:formatCurrency(params.value))+"("+params.percent+"%)";
        },
        textStyle:{
            fontSize:12
        }
    },
    series:{
        name:'生命周期占比',
        type:'pie',
        radius: "55%",
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
            }
        },
        data:[]
    }
};
var option_1={
    title : {
        text: '56%',
        subtext: '90天活跃人数',
        subtextStyle:{
            fontSize:"12px",
            color:"#fff",
            fontWeight:"normal"
        },
        x:'center',
        bottom:35,
        textStyle:{
            fontSize:"10px",
            color:"#41c7db",
            fontWeight:"normal"
        }
    },
    color:["#41b8db","#1a1e28"],
    tooltip: {
        show:false
    },
    series: {
        name:'活跃会员占比',
        type:'pie',
        silent  :true,
        radius: ['35%', '40%'],
        center:["50%","35%"],
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
            {value:0, name:'活跃会员占比',label: {
                normal: {
                    show: true,
                    formatter:'{d}%',
                    position: 'center',
                    textStyle: {
                        fontSize: '12'
                    },
                    emphasis: {
                        show: false,
                        textStyle: {
                            fontSize: '12'
                        }
                    }
                }}
            },
            {value:0, name:'非活跃会员占比'}
        ]
    }
};
var select_time={
    data:{
        chart_year:[],
        timeType:""
    },
    init:function(){
        this.timeSwitchEvent();
        this.getYear();
    },
    timeSwitchEvent:function () {
        $("#time_nav_switch li").click(function (e) {
            e.stopPropagation();
            var time_type = $(this).text();
            $(this).addClass("time_active");
            $(this).siblings("li").removeClass("time_active");
            $('#years').text(select_time.data.year_choosed);
            select_time.getTime(time_type);
            time_type == "年" ? $("#year_change_wrap").hide() : $("#year_change_wrap").show();
        });
        $("#time_details").on("click","li",function () {
            var nav = $("#time_nav_switch .time_active").text();
            $(this).addClass("choosedTime");
            $(this).siblings("li").removeClass("choosedTime");
            $("#chartTimeInput").attr("data-time",$(this).attr("data-time"));
            if(nav == "周"){
                $("#chartTimeInput").html($(this).children().eq(0).text());
                select_time.data.timeType="weekly";
            }else {
                if(nav == "年"){
                    select_time.data.timeType="yearly";
                    select_time.data.year_choosed = $(this).text().replace(/年/,"");
                }else{
                    select_time.data.timeType="monthly";
                }
                $("#chartTimeInput").html($(this).text());
            }
            //select_time.getChartData();
            //select_time.getSale();//获取业绩
            $("#time_choose_wrap").hide();
        });//选中具体周月年点击事件
        $("#arrow_right").click(function (e) {
            e.stopPropagation();
            var nav = $("#time_nav_switch .time_active").text();
            if(select_time.data.year_choosed == select_time.data.chart_year[0]){
                return ;
            }else {
                select_time.data.year_choosed++
            }
            $('#years').text(select_time.data.year_choosed);
            select_time.getTime(nav);
        });//左切换年
        $("#arrow_left").click(function (e) {
            e.stopPropagation();
            var nav = $("#time_nav_switch .time_active").text();
            var len = select_time.data.chart_year.length-1;
            if(select_time.data.year_choosed == select_time.data.chart_year[len]){
                return ;
            }else {
                select_time.data.year_choosed--;
            }
            $('#years').text(select_time.data.year_choosed);
            select_time.getTime(nav);
        });//右切换年
    },
    getYear:function () {//加载是获取当前年月
        var date = new Date();
        var Y = date.getFullYear();
        var M = date.getMonth()+1;
        var D = date.getDate();
        for(var i = Y;i >=2000;i--){
            select_time.data.chart_year.push(i);
        }
        $("#years").text(Y);
        select_time.data.date_choosed = D;
        select_time.data.year_choosed = Y;
        select_time.data.month_choosed = M;
        select_time.getTime("月").then(function () {
            $("#time_details .choosedTime").trigger("click");
            getData("brand");
            getData("newVip");
            getData("storeRank");
            getData("skuRank");
            getData("sourceRank");
            getData("vipCard");
            getViewData("actvip");
            getViewData("cycle");
        });
        //select_time.getTime("周").then(function () {
        //    $("#time_details .choosedTime").trigger("click");
        //    getData("brand");
        //    getData("newVip");
        //    getData("storeRank");
        //    getData("skuRank");
        //    getData("sourceRank");
        //    getData("vipCard");
        //});
    },
    getTime:function (type) {//周月年切换页面细节渲染
        var def=$.Deferred();
        var details = "";
        var year = select_time.data.year_choosed;
        if(type == "年"){
            var year_arr = select_time.data.chart_year;
            for(var i = 0;i < year_arr.length;i++){
                var li = year_arr[i] == year ? "<li data-time='"+year_arr[i]+"-01-01' class='choosedTime'>"+year_arr[i]+"年</li>":"<li data-time='"+year_arr[i]+"-01-01'>"+year_arr[i]+"年</li>";
                details += li;
            }
            $("#time_details").html(details);
        }
        if(type == "月"){
            for(var k = 1;k < 13;k++){
                var mm = k <10 ? "0"+k : k;
                var className = k == select_time.data.month_choosed ? "class='choosedTime'" : "";
                details += "<li data-time='"+year+"-"+mm+"-01' "+className+">"+year+"年"+k+"月</li>"
            }
            $("#time_details").html(details);
            def.resolve();
        }
        if(type == "周"){
            var d = new Date(year,0,1);
            var w1 = d.getDay() || 7;
            if(w1>1&&w1<4){//1月1号若为周二周三，则从上一年开始算第一周
                var n = w1 - 1 ;
                d.setDate(d.getDate()-n);
            }else if(w1>3){
                var n = 7 - w1 + 1;
                d.setDate(d.getDate()+n);
            }
            var week = 1;
            var timer=setInterval(function(){
                week>1?d.setDate(d.getDate()+1):"";
                var Y=d.getFullYear();
                var M=d.getMonth()+1;
                var D=d.getDate();
                d.setDate(d.getDate()+6);
                var y=d.getFullYear();
                var m=d.getMonth()+1;
                var dd=d.getDate();
                var range = Y+"-"+M+"-"+D+" 至 "+y+"-"+m+"-"+dd;
                var className =  "";
                var mm_format = M < 10 ? "0"+M : M;
                var dd_format = D <10 ? "0"+D : D;
                if(M == m && select_time.data.month_choosed == M && select_time.data.date_choosed >= D && select_time.data.date_choosed <= dd){
                    className = "choosedTime";
                }else if(M != m &&((select_time.data.month_choosed == M && select_time.data.date_choosed >= D)||(select_time.data.month_choosed == m && select_time.data.date_choosed <= dd))){
                    className = "choosedTime";
                }
                details += "<li data-time='"+Y+"-"+mm_format+"-"+dd_format+"' class='weekContent "+className+"'><div>"+year+"年"+week+"周</div><div class='color_grey'>"+range+"</div></li>";
                $("#time_details").html(details);
                // console.log("第"+week+"周"+Y+"年"+M+"月"+D+"日到"+y+"年"+m+"月"+dd+"日");
                week ++;
                if(y!=year||m==12&&(dd==29||dd==30||dd==31)){//最后一周的周日为29，30，31则为最后一周
                    clearInterval(timer);
                }
            },0);
        }
        return def;
    }
};
var oc = oc || new ObjectControl();
function getSelect(){
    $(".spinner_warp").show();
    getData("brand");
    getData("newVip");
    getData("storeRank");
    getData("skuRank");
    getData("sourceRank");
    getData("vipCard");
    getViewData("actvip");
    getViewData("cycle");
}
$("#sure_select").bind("click",getSelect);
var allMyChart={};
window.addEventListener("resize", function () {
    allMyChart.myChart!=undefined && (allMyChart.myChart.isDisposed()==undefined) && allMyChart.myChart.resize();
    allMyChart.myChart_2!=undefined && (allMyChart.myChart_2.isDisposed()==undefined) && allMyChart.myChart_2.resize();
    allMyChart.myChart_3!=undefined && (allMyChart.myChart_3.isDisposed()==undefined) && allMyChart.myChart_3.resize();
    $(".ranking_content").getNiceScroll().resize();
});
function getBrandList (){
    var _param = {};
    _param["searchValue"] ="";
    _param["corp_code"] ="C10000";
    oc.postRequire("post", "/shop/brand", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.brands;
            var li="<li data-code='' class='selected'>全部品牌</li>";
            for(var i=0;i<list.length;i++){
                li+="<li data-code='"+list[i].brand_code+"'>"+list[i].brand_name+"</li>"
            }
            $("#brand_list").html(li);
        } else if (data.code == "-1") {
            $("#brand_list div").html("加载失败")
        }
    })
}
function getViewData(queryType){
    var param={};
    param.type="vip";
    param.start_time="";
    param.end_time="";
    param.query_type=queryType;
    param.brand_code=$("#select_brand").attr("data-code");
    param.source=$("#select_channel").attr("data-type");
    oc.postRequire("post", "/board/vipAnaly", "", param, function (data) {
        if(queryType=="actvip"){
            $(".actvip_chart").next(".spinner_warp").hide();
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var actvip=msg.actvip;
                var allVip=msg.all_size==0?"1":msg.all_size;
                option_1.title.text=actvip[0].size;
                option_1.title.subtext="90天活跃人数";
                option_1.series.data[0].value=actvip[0].size;
                option_1.series.data[1].value=allVip-actvip[0].size;
                option_1.color=["#41b8db","#1a1e28"];
                option_1.title.textStyle.color="#41b8db";
                allMyChart.myChart_2=echarts.init(document.getElementById("div2"));
                allMyChart.myChart_2.setOption(option_1);

                option_1.title.text=actvip[1].size;
                option_1.title.subtext="180天活跃人数";
                option_1.title.textStyle.color="#7a4db0";
                option_1.series.data[0].value=actvip[1].size;
                option_1.series.data[1].value=allVip-actvip[1].size;
                option_1.color=["#7a4db0","#1a1e28"];
                allMyChart.myChart_3=echarts.init(document.getElementById("div3"));
                allMyChart.myChart_3.setOption(option_1);
            }else{
                dataError("actvip");
            }
        }else if(queryType=="cycle"){
            $(".cycle_chart").next(".spinner_warp").hide();
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var cycle=msg.cycle;
                var data=[];
                for(var i=0;i<cycle.length;i++){
                    data.push({
                        name:cycle[i].name,
                        value:cycle[i].size
                    })
                }
                option.series.data=data;
                allMyChart.myChart=echarts.init(document.getElementById("div1"));
                allMyChart.myChart.setOption(option);
            }else{
                dataError("cycle");
            }
        }
    })
}
function toggleSelect(){
    $("#select_brand").parent().click(function(e){
        e.stopPropagation();
        $(this).siblings().find(".select_parent").hide();
        if($(this).find(".select_parent").is(":hidden")){
            $(this).find(".select_parent").show();
            if($("#brand_list li").length==0){
                getBrandList();
            }
            $(".select_list").getNiceScroll().resize();
        }else{
            $(this).find(".select_parent").hide();
        }
    });
    $("#select_channel,#chartTimeInput").click(function(e){
        e.stopPropagation();
        $(this).parent().siblings().find(".select_parent").hide();
        $(this).parent().find(".select_parent").toggle();
        $(".select_list,.time_details").getNiceScroll().resize();
    });
    $(".select_parent").on("click",'#brand_list li',function(e){
        e.stopPropagation();
        $("#select_brand").html($(this).html());
        $("#select_brand").attr("data-code",$(this).attr("data-code"));
        $(this).parents(".select_parent").hide();
        $(this).addClass("selected").siblings().removeClass("selected")
    });
    $(".select_parent").on("click",'#channel_list li',function(e){
        e.stopPropagation();
        $("#select_channel").html($(this).html());
        $("#select_channel").attr("data-type",$(this).attr("data-type"));
        $(this).parents(".select_parent").hide();
        $(this).addClass("selected").siblings().removeClass("selected")
    })
}
function noData(data){
    return '<div class="notData" >'+data+'</div>';
}
function noDataView(selector){
    $(selector).children().not(".notData").hide();
    if($(selector).children(".notData").length==0){
        $(selector).html(noData("暂无数据"));
    }else{
        $(selector).children(".notData").show();
    }
}
function errorViewTip(selector){
    $(selector).children().not(".notData").hide();
    if($(selector).children(".notData").length==0){
        $(selector).append(noData("获取数据失败"));
    }else{
        $(selector).children(".notData").show();
    }
}
function forMatNumRate(num,position){
    if(position=="left"){
        if(num*10000>=1){
            return (num*100).toFixed(2)+"%"
        }else if(num*100000>=1){
            return (num*1000).toFixed(2)+"‰"
        }else{
            return (num*100).toFixed(2)+"%"
        }
    }else if(position=="right"){
        if(num*10000>=1){
            return  (100-num*100).toFixed(2)+"%"
        }else if(num*100000>=1){
            return  (1000-num*1000).toFixed(2)+"‰"
        }else{
            return  (100-num*100).toFixed(2)+"%"
        }
    }else if(position=="width"){
        if(num*10000>=1){
            return (num*100).toFixed(2)+"%"
        }else if(num*100000>=1){
            return ((num*1000).toFixed(2))/10+"%"
        }else{
            return (num*100).toFixed(2)+"%"
        }
    }
}
function textALingRight(selector){
    //var maxNum="";
    //selector.map(function(){
    //    maxNum=maxNum>$(this).width()?maxNum:$(this).width()
    //});
    //selector.css("width",maxNum+"px");
}
function initViewData(type,data){
    switch (type){
        case "brand":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var data_brand_ranking=msg.brandRank;
                var brand_management=msg.brandAchv;
                if(data_brand_ranking.length==0){
                    $("#brand_content").html(noData("暂无数据"));
                    allMyChart.myChart.dispose();
                    //noDataView('#div1')
                }else {
                    var li="";
                    var data=[];
                    for(var b=0;b<data_brand_ranking.length;b++){
                        var val=Number(data_brand_ranking[b].amt_trade);
                        li+='<li>'+
                            '<span class="list_num list_num_5"><i>'+(b+1)+'</i></span>'+
                            '<span title="'+data_brand_ranking[b].brand_name+'">'+data_brand_ranking[b].brand_name+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(val)+'">'+formatCurrency(val)+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(data_brand_ranking[b].num_sales)+'">'+formatCurrency(data_brand_ranking[b].num_sales)+'</span>'+
                            '<span class="ranking_data" title="'+data_brand_ranking[b].last_year_amt_trade_rate+'">'+data_brand_ranking[b].last_year_amt_trade_rate+'</span>'+
                            '<span class="ranking_data" title="'+data_brand_ranking[b].last_time_amt_trade_rate+'">'+data_brand_ranking[b].last_time_amt_trade_rate+'</span>'+
                            '</li>';
                        data.push({
                            name:data_brand_ranking[b].brand_name,
                            value:val
                        })
                    }
                    $("#brand_content").html(li);
                }
                $("#brand_amt_trade").html(formatCurrency(brand_management.amt_trade));
                $("#num_trade").html(formatCurrency(brand_management.num_trade));
                $("#num_sales").html(formatCurrency(brand_management.num_sales));
                $("#relate_rate").html(brand_management.relate_rate);
                $("#discount").html(brand_management.discount);
                $("#trade_price").html(formatCurrency(brand_management.trade_price));
                $("#sale_price").html(formatCurrency(brand_management.sale_price));
                $('.brand_ranking').next(".spinner_warp").hide();
                $('.brand_management').next(".spinner_warp").hide();
                $(".brand_management .model_title").find("span").remove();
            }else{
                $("#brand_content").html(noData("获取数据失败"));
                $("#brand_amt_trade").html(0);
                $("#num_trade").html(0);
                $("#num_sales").html(0);
                $("#relate_rate").html(0);
                $("#discount").html(0);
                $("#trade_price").html(0);
                $("#sale_price").html(0);
                $(".brand_management .model_title").find("span").remove();
                $(".brand_management .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
            }
            break;
        case "newVip":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var newVip=msg.newVip;
                var vipAchv=msg.vipAchv;


                $("#num_trade_rate").parent().nextAll(".line_center").find(".bar_data_left").css("width",forMatNumRate(newVip.num_trade_rate,"width"));
                $("#vip_count_rate").parent().nextAll(".line_center").find(".bar_data_left").css("width",forMatNumRate(newVip.vip_count_rate,"width"));
                $("#amt_trade_rate").parent().nextAll(".line_center").find(".bar_data_left").css("width",forMatNumRate(newVip.amt_trade_rate,"width"));

                $("#num_trade_rate").html(forMatNumRate(newVip.num_trade_rate,"left"));
                $("#vip_count_rate").html(forMatNumRate(newVip.vip_count_rate,"left"));
                $("#amt_trade_rate").html(forMatNumRate(newVip.amt_trade_rate,"left"));

                $("#num_trade_rate").parent().nextAll(".line_right").children().eq(1).html(forMatNumRate(newVip.num_trade_rate,"right"));
                $("#vip_count_rate").parent().nextAll(".line_right").children().eq(1).html(forMatNumRate(newVip.vip_count_rate,"right"));
                $("#amt_trade_rate").parent().nextAll(".line_right").children().eq(1).html(forMatNumRate(newVip.amt_trade_rate,"right"));

                $("#vip_amt_trade").html(formatCurrency(vipAchv.amt_trade));
                $("#vip_num_trade").html(formatCurrency(vipAchv.num_trade));
                $("#vip_num_sales").html(formatCurrency(vipAchv.num_sales));
                $("#vip_relate_rate").html(vipAchv.relate_rate);
                $("#vip_discount").html(vipAchv.discount);
                $("#vip_trade_price").html(formatCurrency(vipAchv.trade_price));
                $("#vip_sale_price").html(formatCurrency(vipAchv.sale_price));

                $(".band_ability .model_title").find("span").remove();
                $(".vip_pay .model_title").find("span").remove();
            }else{
                $("#vip_amt_trade").html(0);
                $("#vip_num_trade").html(0);
                $("#vip_num_sales").html(0);
                $("#vip_relate_rate").html(0);
                $("#vip_discount").html(0);
                $("#vip_trade_price").html(0);
                $("#vip_sale_price").html(0);
                $(".band_ability .model_title").find("span").remove();
                $(".vip_pay .model_title").find("span").remove();
                $(".band_ability .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
                $(".vip_pay .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
            }
            $('.band_ability').next(".spinner_warp").hide();
            $('.vip_pay').next(".spinner_warp").hide();
            break;
        case "storeRank":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var storeRank=msg.storeRank;
                if(storeRank.length>0){
                    var li="";
                    for(var s= 0,len=storeRank.length;s<len;s++){
                        var val=Number(storeRank[s].amt_trade);
                        li+='<li>'+
                            '<span class="list_num list_num_4"><i>'+(s+1)+'</i></span>'+
                            '<span title="'+storeRank[s].store_name+'" style="width: 30%">'+storeRank[s].store_name+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(val)+'">'+formatCurrency(val)+'</span>'+
                            '<span class="ranking_data" title="'+storeRank[s].last_year_amt_trade_rate+'">'+storeRank[s].last_year_amt_trade_rate+'</span>'+
                            '<span class="ranking_data" title="'+storeRank[s].last_time_amt_trade_rate+'">'+storeRank[s].last_time_amt_trade_rate+'</span>'+
                            '</li>'
                    }
                    $("#storeRank").html(li);
                }else {
                    $("#storeRank").html(noData("暂无数据"))
                }
            }else {
                $("#storeRank").html(noData("获取数据失败"))
            }
            $('.store_ranking').next(".spinner_warp").hide();
            break;
        case "skuRank":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var skuRank=msg.skuRank;
                if(skuRank.length>0){
                    var li="";
                    for(var k= 0,len=skuRank.length;k<len;k++){
                        var val=Number(skuRank[k].amt_trade);
                        li+='<li>'+
                            '<span class="list_num list_num_3"><i>'+(k+1)+'</i></span>'+
                            '<span title="'+skuRank[k].product_code+'">'+skuRank[k].product_code+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(val)+'">'+formatCurrency(val)+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(skuRank[k].num_sales)+'">'+formatCurrency(skuRank[k].num_sales)+'</span>'+
                            '</li>'
                    }
                    $("#skuRank").html(li);
                }else {
                    $("#skuRank").html(noData("暂无数据"))
                }
            }else {
                $("#skuRank").html(noData("获取数据失败"))
            }
            $('.sku_ranking').next(".spinner_warp").hide();
            break;
        case "sourceRank":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var sourceRank=msg.sourceRank;
                if(sourceRank.length>0){
                    var li="";
                    for(var c= 0,len=sourceRank.length;c<len;c++){
                        var val=Number(sourceRank[c].amt_trade);
                        li+= '<li><span class="list_num list_num_5"><i>'+(c+1)+'</i></span>'+
                            '<span title="'+sourceRank[c].name+'">'+sourceRank[c].name+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(val)+'">'+formatCurrency(val)+'</span>'+
                            '<span class="ranking_data" title="'+sourceRank[c].rate+'">'+sourceRank[c].rate+'</span>'+
                            '<span class="ranking_data" title="'+sourceRank[c].last_year_amt_trade_rate+'">'+sourceRank[c].last_year_amt_trade_rate+'</span>'+
                            '<span class="ranking_data" title="'+sourceRank[c].last_time_amt_trade_rate+'">'+sourceRank[c].last_time_amt_trade_rate+'</span>'+
                            '</li>'
                    }
                    $("#sourceRank").html(li);
                }else {
                    $("#sourceRank").html(noData("暂无数据"))
                }
            }else {
                $("#sourceRank").html(noData("获取数据失败"))
            }
            $('.source_ranking').next(".spinner_warp").hide();
            break;
        case "vipCard":
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var vipCard=msg.vipCard;
                if(vipCard.length>0){
                    var li="";
                    for(var c= 0,len=vipCard.length;c<len;c++){
                        var val=Number(vipCard[c].vip_num);
                        li+= '<li><span class="list_num list_num_3"><i>'+(c+1)+'</i></span>'+
                            '<span title="'+vipCard[c].card_type+'">'+vipCard[c].card_type+'</span>'+
                            '<span class="ranking_data" title="'+formatCurrency(val)+'">'+formatCurrency(val)+'</span>'+
                            '<span class="ranking_data" title="'+vipCard[c].vip_rate+'">'+vipCard[c].vip_rate+'</span>'+
                            '</li>'
                    }
                    $("#vipCard").html(li);
                }else {
                    $("#vipCard").html(noData("暂无数据"))
                }
            }else {
                $("#vipCard").html(noData("获取数据失败"))
            }
            $('.vip_card_ranking').next(".spinner_warp").hide();
            break;
    }
}
function dataError(type){
    switch (type){
        case "brand":
            $('.brand_ranking').next(".spinner_warp").hide();
            $('.brand_management').next(".spinner_warp").hide();
                $("#brand_content").append(noData("获取数据失败"));
                $("#num_trade").html(0);
                $("#num_sales").html(0);
                $("#relate_rate").html(0);
                $("#discount").html(0);
                $("#trade_price").html(0);
                $("#sale_price").html(0);
                $(".brand_management .model_title").find("span").remove();
                $(".brand_management .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
            break;
        case "newVip":
            $('.band_ability').next(".spinner_warp").hide();
            $('.type_data_all').next(".spinner_warp").hide();
                $("#vip_amt_trade").html(0);
                $("#vip_num_trade").html(0);
                $("#vip_num_sales").html(0);
                $("#vip_relate_rate").html(0);
                $("#vip_discount").html(0);
                $("#vip_trade_price").html(0);
                $("#vip_sale_price").html(0);
                $(".vip_pay .model_title").find("span").remove();
                $(".band_ability .model_title").find("span").remove();
                $(".band_ability .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
                $(".vip_pay .model_title").append("<span style='color:#c26555;margin-left: 10px;float: right'>*获取数据失败</span>");
            break;
        case "storeRank":
            $('.store_ranking').next(".spinner_warp").hide();
            $("#storeRank").html(noData("获取数据失败"));
            break;
        case "skuRank":
            $('.sku_ranking').next(".spinner_warp").hide();
            $("#skuRank").html(noData("获取数据失败"));
            break;
        case "sourceRank":
            $('.source_ranking').next(".spinner_warp").hide();
            $("#sourceRank").html(noData("获取数据失败"));
            break;
        case "vipCard":
            $('.vip_card_ranking').next(".spinner_warp").hide();
            $("#vipCard").html(noData("获取数据失败"));
            break;
        case "actvip":
            noDataView("#div2");
            noDataView("#div3");
            break;
        case "cycle":
            noDataView("#div1");
            break;
    }
}
function getData(type){  //brand，newVip，storeRank，skuRank
    var param={};
    param.time_type=select_time.data.timeType;
    param.time_value=$("#chartTimeInput").attr("data-time");
    param.type=type;
    param.brand_code=$("#select_brand").attr("data-code");
    param.source=$("#select_channel").attr("data-type");
    $(".ranking_content").map(function(){
        $(this).getNiceScroll(0).doScrollTop(0,0); // Scroll Y Axis
    });
    oc.postRequire("post", "/board/brandAnaly", "", param, function (data) {
        initViewData(type,data);
    },function(){
        dataError(type)
    })
}
$(document).click(function (e) {
    var selector=$(".select_parent");
    if(!selector.is(e.target) && selector.has(e.target).length === 0){
        selector.hide();
    }
});
//防止ajax重复请求
var pendingRequests = {};
$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
    var key = options.url;
    var name = JSON.parse(originalOptions.data.param).message.type;
    var queryName = JSON.parse(originalOptions.data.param).message.query_type;
        if(key == "/board/brandAnaly" && !pendingRequests[name]){
            pendingRequests[name] = jqXHR;
        }else if(key == "/board/vipAnaly" && !pendingRequests[queryName]){
            pendingRequests[queryName] = jqXHR;
        }else if(!pendingRequests[key] && key != "/board/brandAnaly" && key != "/board/vipAnaly"){
            pendingRequests[key] = jqXHR;
        }else {
            // jqXHR.abort();    //放弃后触发的提交
            if(pendingRequests[name]){
                pendingRequests[name].abort();
                pendingRequests[name]=jqXHR;
            }else if(pendingRequests[queryName]){
                pendingRequests[queryName].abort();
                pendingRequests[queryName]=jqXHR
            }else{
                pendingRequests[key].abort()
            }
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