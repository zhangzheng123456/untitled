/**
 * Created by Administrator on 2017/11/13.
 */
var oc = new ObjectControl();
var opt={};
var isChangeTabsBrand=false;
var isChangeTabsSource=false;
var currYear = (new Date()).getFullYear();
var theRequest={};
var left=0;
var right=0;
var params={};
function setSessionParam(){
    params.brand={
        source_code:$("#source_list").val(),
        brand_code:$("#brand_value_brand").val(),
        time:$("#select_date_brand").attr("data-value"),
        time_type:$("#time_tabs span a.active").attr("data-type")
    };
    params.source={
        brand_code:$("#brand_value").val(),
        time:$("#select_date").attr("data-value")
    };
    if($(".tabs_head span:last-child").hasClass("active")){
        params.nowActive="brand"
    }else{
        params.nowActive="source"
    }
    sessionStorage.setItem("sessionParam",JSON.stringify(params))
}
function geUrl(){
    var url = decodeURIComponent(location.search); //获取url中"?"符后的字串
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
}
geUrl();
theRequest.corp_code="C10261";
opt.date = {preset : 'date'};
opt.datetime = {preset : 'datetime'};
opt.time = {preset : 'time'};
opt.default = {
    theme: 'android-ics light', //皮肤样式
    display: 'modal', //显示方式
    mode: 'scroller', //日期选择模式
    dateFormat: 'yy-mm-dd',
    dateOrder: 'yymmdd', //面板中日期排列格
    lang: 'zh',
    showNow: true,
    nowText: "今天",
    startYear: currYear - 100, //开始年份
    endYear: currYear + 40, //结束年份
    onSelect:function(valueText){
        var valueTextFormat=valueText.replace(/-/g,"");
        if($(".tabs_head span.active").index()==0){
            $("#select_date").attr("data-value",valueText);
            $("#select_date").html(valueTextFormat);
            source.initData();
        }
        if($(".tabs_head span.active").index()==1){
            $("#select_date_brand").attr("data-value",valueText);
            $("#select_date_brand").html(valueTextFormat);
            brand.initData();
        }
    }
};
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
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
$(".tabs_head span").bind("click",function(){
    var index=$(this).index();
    if($(this).hasClass("active")){
        $(".content").animate({scrollTop: 0}, 500);
    }else{
        if(index==0){
            window.right=$(".content").scrollTop();
        }else{
            window.left=$(".content").scrollTop();
        }
        $(this).addClass("active").siblings().removeClass("active");
        $(".content").children().eq(index).show(0,chartResize).siblings().hide();
        if(index==0){
            $(".content").scrollTop(window.left)
        }else{
            $(".content").scrollTop(window.right);
        }
        if(!isChangeTabsBrand && index==1 && !params){
            brand.initData();
        }
        if(!isChangeTabsSource && index==0 && params && params.nowActive=="brand"){
            source.initData()
        }
        if(!isChangeTabsBrand && index==1 && params && params.nowActive=="source"){
            brand.initData()
        }
        if(isChangeTabsBrand && isChangeTabsSource){
            setSessionParam();
        }
    }
});

$(".dateClassOther").mobiscroll($.extend(opt['date'], opt['default']));
$("#select_date").click(function(){
    $('#dateClassOtherSource').mobiscroll('show');
    $(this).blur();
    return false;
});
$("#select_date_brand").click(function(){
    $('#dateClassOtherBrand').mobiscroll('show');
    $(this).blur();
    return false;
});
var API="/api/external/youshuBoard";
var color=["#7ba442","#3697b5","#7a4db0","#ce815b","#ae605d"];
var source={
    allIsOk:false,
    channelIsOk:false,
    brand_code:$("#brand_value").val(),
    line:echarts.init(document.getElementById('lineChart')),
    bar:echarts.init(document.getElementById('barChart')),
    getBrand:function(custom){
        var _params={
            "searchValue":"",
            corp_code:theRequest.corp_code,
            sign:theRequest.sign,
            method:"brand",
            custom:custom
        };
        var def= $.Deferred();
        oc.postRequire("post", API,"", _params, function(data){
            if(data.code==0){
                var msg=JSON.parse(data.message);
                var list=msg.brands;
                for(var i=0;i<list.length;i++){
                    $("#brand_value").append("<option value='"+list[i].brand_code+"'>"+list[i].brand_name+"</option>");
                    $("#brand_value_brand").append("<option value='"+list[i].brand_code+"'>"+list[i].brand_name+"</option>")
                }
                def.resolve();
            }else{
                def.reject();
            }
        });
        return def;
    },
    getData:function(type,custom){
        var _self=this;
        $(".mask").show();
        var _params={
            type:type,
            query_time:$("#select_date").attr("data-value"),
            brand_code:$("#brand_value").val(),
            corp_code:theRequest.corp_code,
            sign:theRequest.sign,
            method:"allData",
            custom:custom
        };
        if(type == "trade"){
            _self.line.showLoading({maskColor: 'rgba(255, 255, 255, 0.1)',textColor: '#ba2832'});
        }else if(type == 'vip'){
            _self.bar.showLoading({maskColor: 'rgba(255, 255, 255, 0.1)',textColor: '#ba2832'});
        }
        oc.postRequire("post", API,"", _params, function(data){
            if(data.code==0){
                var msg=JSON.parse(data.message);
                switch (type){
                    case "all":
                        var value=$("#ach_today").text()=="加载中..."?"0":$("#ach_today span:first-child").text();
                        var startVal = value.replace(/,/g,'');
                        var endVal = msg.current.replace(/,/g,'');
                        $("#ach_today").html("<span>"+msg.current+"</span><span>元</span>");
                        var demo = new CountUp($("#ach_today span")[0], startVal,endVal , 0, 2);
                        demo.start();
                        $("#data_value").html(msg.date);
                        $("#before").html(msg.before+"元");
                        $("#month").html(msg.month+"元");
                        _self.allIsOk=true;
                        break;
                    case "channel":
                        var list=msg.list;
                        if(list.length==0){
                            $("#sourceListContent").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }else{
                        var html="";
                        for(var s=0;s<list.length;s++){
                            html+='<div class="list_line">'+
                                '<span>'+(s+1)+'</span>'+
                                '<span>'+list[s].name+'</span>'+
                                '<span>'+list[s].amt+'</span>'+
                                '<span>'+list[s].rate+'</span>'+
                                '</div>'
                        }
                        $("#sourceListContent").html(html).parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break;
                    case "area":
                        var areaList=msg.list;
                        if(areaList.length==0){
                            $("#areaListContent").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }else{
                            var areaHtml="";
                            for(var a=0;a<5;a++){
                                areaHtml+='<div class="list_line">'+
                                    '<span>'+(a+1)+'</span>'+
                                    '<span>'+areaList[a].area+'</span>'+
                                    '<span>'+areaList[a].amt+'</span>'+
                                    '<span>'+areaList[a].rate+'%</span>'+
                                    '</div>'
                            }
                            $("#areaListContent").html(areaHtml).parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break;
                    case "brand":
                        var brandList=msg.list;
                        if(brandList.length==0){
                            $("#brandListContent").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }else{
                            var brandHtml="";
                            for(var b=0;b<brandList.length;b++){
                                brandHtml+='<div class="list_line">'+
                                    '<span>'+(b+1)+'</span>'+
                                    '<span>'+brandList[b].brand_name+'</span>'+
                                    '<span>'+brandList[b].amt+'</span>'+
                                    '<span>'+brandList[b].rate+'</span>'+
                                    '</div>'
                            }
                            $("#brandListContent").html(brandHtml).parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break;
                    case "actVip":
                        $("#vip_dev_total").text(msg.all_size);
                        $("#vip_new_amount").text(msg.today_vip.size);
                        $("#act_rate").text(msg.act_vip.rate);
                        $("#wx_rate").text(msg.wx_vip.rate);
                        $("#vip_rate").text(msg.wx_act_vip.rate);
                        $("#vip_loading").hide();
                        _self.actVipIsOk=true;
                        $("#actVip").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").hide();
                        break;
                    case "trade":
                        $("#lineChart").parents(".model_parent").find(".abilityNoData").hide();
                       var date = [],//日期数组
                            amt = []; //业绩数组
                        list = msg.list;
                        for(var i=0;i<list.length;i++){
                            date.push(list[i].data);
                            amt.push(list[i].amt);
                        }
                        _self.lineChartInit(date,amt);
                        break;
                    case "vip":
                        $("#barChart").parents(".model_parent").find(".abilityNoData").hide();
                        var date = [],//日期数组
                            person = [],//增长人数数组
                            arr = [],//柱状图虚拟数组（每个柱形条的帽子效果）
                        list = msg.list;
                        for(var i=0;i<list.length;i++){
                            date.push(list[i].date);
                            person.push(list[i].size);
                            arr.push(0);
                        }
                        _self.barChartInit(date,person,arr);
                        break;
                }
            }else{
                switch (type){
                    case "all":
                        $("#ach_today").html("加载失败");
                        $("#data_value").html("加载失败");
                        $("#before").html("加载失败");
                        $("#month").html("加载失败");
                        _self.allIsOk=true;
                        break;
                    case "channel":
                        $("#sourceListContent").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "area":
                        $("#areaListContent").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "brand":
                        $("#brandListContent").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "actVip":
                        $("#vip_dev_total").text(0);
                        $("#vip_new_amount").text(0);
                        $("#act_rate").text(0);
                        $("#wx_rate").text(0);
                        $("#vip_rate").text(0);
                        $("#actVip").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").show();
                        break;
                    case "trade":
                        _self.line.hideLoading();
                        $("#lineChart").parents(".model_parent").find(".abilityNoData").show();
                        break;
                    case "vip":
                        _self.bar.hideLoading();
                        $("#barChart").parents(".model_parent").find(".abilityNoData").show();
                        break;
                }
            }
        })
    },
    selectBrand:function(){
        var _self=this;
        $("#brand_value").change(function(){
            _self.initData();
        })
    },
    lineChartInit:function (date,amt) {//业绩图表实列化
        var option = {
            color:['#888'],
            backgroundColor:'rgba(255,255,255,0.8)',
            title: {
                textStyle:{
                    fontWeight:'normal',
                    fontSize:'16px'
                },
                text: '当月业绩(万元)',
                left:'10px',
                top:'10px'
            },
            grid: {
                left: '11',
                right: '15',
                top:'50',
                bottom:'10',
                containLabel: true
            },
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'line',        // 默认为直线，可选为：'line' | 'shadow'
                    color:"#c34950"
                }
            },
            //dataZoom: [{
            //    type: 'inside',
            //    start: 0,
            //    end: 100
            //}, {
            //    start: 0,
            //    end: 10,
            //    bottom:5,
            //    height: 20,
            //    handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            //    handleSize: '80%',
            //    handleStyle: {
            //        color: '#fff',
            //        shadowBlur: 3,
            //        shadowColor: 'rgba(0, 0, 0, 0.6)',
            //        shadowOffsetX: 2,
            //        shadowOffsetY: 2
            //    }
            //}],
            xAxis: {
                type: 'category',
                boundaryGap:true,
                axisLine:{
                    show:false
                },
                axisTick:{
                    show:false
                },
                axisLabel:{
                    color:'#888'
                },
                data: date,
                axisPointer:{
                    lineStyle:{
                        color:"#c34950"
                    }
                }
            },
            yAxis: {
                type: 'value',
                axisLine:{
                    show:false
                },
                axisTick:{
                    show:false
                },
                axisLabel:{
                    color:'#888',
                    margin:20
                },
                splitLine:{
                    lineStyle:{
                        type:'dotted',
                        color:'#888'
                    }
                }
            },
            series: [
                {
                    name:'当月累计业绩',
                    type:'line',
                    data:amt,
                    symbolSize:5,
                    symbol:'circle'
                }
            ]
        };
        source.line.setOption(option);
        source.line.hideLoading();
    },
    barChartInit:function (date,person,arr) {
        var option = {
            color: ['#888'],
            backgroundColor:'rgba(255,255,255,0.8)',
            title: {
                textStyle:{
                    color:"#888",
                    fontWeight:'normal',
                    fontSize:'16px'
                },
                text: '每日会员增长人数(人)',
                left:'10px',
                top:'10px'
            },
            tooltip : {
                trigger: 'axis',
                backgroundColor:"#ba2832",
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow',        // 默认为直线，可选为：'line' | 'shadow'
                    shadowStyle:{
                        color:["rgba(186,40,50,0.3)"]
                    }
                },
                formatter:'{c0}'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data: date,
                    axisLine:{
                        show:false
                    },
                    axisTick:{
                        show:false
                    },
                    axisLabel:{
                        color:'#888'
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    axisLine:{
                        show:false
                    },
                    axisTick:{
                        show:false
                    },
                    axisLabel:{
                        color:'#888'
                    },
                    splitLine:{
                        lineStyle:{
                            type:'dotted',
                            color:'#888'
                        }
                    }
                }
            ],
            series : [
                {
                    name:'直接访问',
                    type:'bar',
                    stack:'月份',
                    //barWidth:25,
                    data:person,
                    itemStyle:{
                        normal:{
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 1, color: 'rgba(186,50,50,0)'},
                                    {offset: 0, color: 'rgba(186,50,50,0.4)'}
                                ]
                            )
                        }
                    }
                },
                {
                    type: 'bar',
                    stack: '月份',
                    silent: true,
                    barMinHeight:2,
                    data: arr,
                    itemStyle: {
                        normal: {
                            color: '#ba2832'
                        }
                    }
                }
            ]
        };
        source.bar.setOption(option);
        source.bar.hideLoading();
    },
    initData:function(){
        $("#content_source").find(".spinner_warp").show();
        isChangeTabsSource=true;
        this.getData("all","z_1");
        this.getData("channel","z_2");
        this.getData("area","z_3");
        //this.getData("brand","z_4");
        this.getData("actVip","z_5");
        this.getData("trade","z_6");
        this.getData("vip","z_7");
        setSessionParam();
    },
    init:function(){
        this.getBrand("z_0").then(function(){
            if(sessionParams){
                var paramSource=JSON.parse(sessionParams).source;
                var paramBrand=JSON.parse(sessionParams).brand;
                $("#brand_value").val(paramSource.brand_code);
                $("#dateClassOtherSource").val(paramSource.time);
                $("#select_date").attr("data-value",paramSource.time);
                $("#select_date").html(paramSource.time.replace(/-/g,""));
                $("#source_list").val(paramBrand.source_code);
                $("#brand_value_brand").val(paramBrand.brand_code);
                $("#dateClassOtherBrand").val(paramBrand.time);
                $("#select_date_brand").attr("data-value",paramBrand.time);
                $("#select_date_brand").html(paramBrand.time.replace(/-/g,""));
                $("#time_tabs span a[data-type="+paramBrand.time_type+"]").addClass("active").parent().siblings().find("a").removeClass("active");
                if(JSON.parse(sessionParams).nowActive=="brand"){
                    brand.initData();
                }else if(JSON.parse(sessionParams).nowActive=="source"){
                    source.initData();
                }
            }else{
                source.initData();
            }
        },function(){
            if(sessionParams){
                var paramSource=JSON.parse(sessionParams).source;
                var paramBrand=JSON.parse(sessionParams).brand;
                $("#brand_value").val("");
                $("#dateClassOtherSource").val(paramSource.time);
                $("#select_date").attr("data-value",paramSource.time);
                $("#select_date").html(paramSource.time.replace(/-/g,""));
                $("#source_list").val(paramBrand.source_code);
                $("#brand_value_brand").val("");
                $("#dateClassOtherBrand").val(paramBrand.time);
                $("#select_date_brand").attr("data-value",paramBrand.time);
                $("#select_date_brand").html(paramBrand.time.replace(/-/g,""));
                $("#time_tabs span a[data-type="+paramBrand.time_type+"]").addClass("active").parent().siblings().find("a").removeClass("active");
                if(JSON.parse(sessionParams).nowActive=="brand"){
                    brand.initData();
                }else if(JSON.parse(sessionParams).nowActive=="source"){
                    source.initData();
                }
            }else{
                source.initData();
            }
        });
        this.selectBrand();
    }
};
var brand={
    cycleRatioChart:echarts.init(document.getElementById("cycleRatio")),
    activeRatioChart90:echarts.init(document.getElementById("activeVip90")),
    activeRatioChart180:echarts.init(document.getElementById("activeVip180")),
    getData:function(type,custom){
        var _params={
            type:type,
            time_type:$("#time_tabs a.active").attr("data-type"),
            time_value:$("#select_date_brand").attr("data-value"),
            brand_code:$("#brand_value_brand").val(),
            source:$('#source_list').val(),
            corp_code:theRequest.corp_code,
            sign:theRequest.sign,
            method:"brandAnaly",
            custom:custom
        };
        oc.postRequire("post", API,"", _params, function(data){
            if(data.code==0){
                switch (type){
                    case "brand":
                        var msg=JSON.parse(data.message);
                        var brand_management=msg.brandAchv;
                        var data_brand_ranking=msg.brandRank;
                        $("#brand_amt_trade").html(formatCurrency(brand_management.amt_trade));
                        $("#num_trade").html(formatCurrency(brand_management.num_trade));
                        $("#num_sales").html(formatCurrency(brand_management.num_sales));
                        $("#relate_rate").html(brand_management.relate_rate);
                        $("#discount").html(brand_management.discount);
                        $("#trade_price").html(formatCurrency(brand_management.trade_price));
                        $("#sale_price").html(formatCurrency(brand_management.sale_price));
                        if(data_brand_ranking.length==0){
                            $("#brandListContentBand").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }else{
                            var li="";
                            for(var b=0;b<data_brand_ranking.length;b++){
                                var val=Number(data_brand_ranking[b].amt_trade);
                                li+='<div class="list_line">'+
                                    '<span class="list_num list_num_5">'+(b+1)+'</span>'+
                                    '<span title="'+data_brand_ranking[b].brand_name+'">'+data_brand_ranking[b].brand_name+'</span>'+
                                    '<span class="ranking_data" >'+formatCurrency(val)+'</span>'+
                                    '<span class="ranking_data" >'+formatCurrency(data_brand_ranking[b].num_sales)+'</span>'+
                                    '<span class="ranking_data" >'+data_brand_ranking[b].last_year_amt_trade_rate+'</span>'+
                                    '<span class="ranking_data" >'+data_brand_ranking[b].last_time_amt_trade_rate+'</span>'+
                                    '</div>';
                            }
                            $("#brandListContentBand").html(li).parents(".model_parent").find(".spinner_warp").hide();
                            if(data_brand_ranking.length>10){
                                $("#brandListContentBand").addClass("hasMore");
                                $("#brandListContentBand").children().eq(9).nextAll().hide();
                                $("#brandListContentBand").append('<div class="list_line look_more">查看全部</div>');
                            }else {
                                $("#brandListContentBand").removeClass("hasMore");
                            }
                        }
                        $("#brand_management").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").hide();
                        break;
                    case "newVip":
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

                        $('.band_ability').next(".spinner_warp").hide();
                        $('.type_data_all').next(".spinner_warp").hide();
                        $("#vip_amt_trade").html(formatCurrency(vipAchv.amt_trade));
                        $("#vip_num_trade").html(formatCurrency(vipAchv.num_trade));
                        $("#vip_num_sales").html(formatCurrency(vipAchv.num_sales));
                        $("#vip_relate_rate").html(vipAchv.relate_rate);
                        $("#vip_discount").html(vipAchv.discount);
                        $("#vip_trade_price").html(formatCurrency(vipAchv.trade_price));
                        $("#vip_sale_price").html(formatCurrency(vipAchv.sale_price));
                        $(".vip_pay .model_title").find("span").remove();
                        $(".band_ability .model_title").find("span").remove();
                        $("#brandAbility").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").hide();
                        $("#newVip").parents(".model_parent").find(".abilityNoData").hide().parents(".model_parent").find(".spinner_warp").hide();

                        break;
                    case "sourceRank":
                        var msg=JSON.parse(data.message);
                        var sourceRank=msg.sourceRank;
                        if(sourceRank.length>0){
                            var li="";
                            for(var c= 0,len=sourceRank.length;c<len;c++){
                                var val=Number(sourceRank[c].amt_trade);
                                li+= '<div class="list_line"><span class="list_num list_num_5">'+(c+1)+'</span>'+
                                    '<span title="'+sourceRank[c].name+'">'+sourceRank[c].name+'</span>'+
                                    '<span class="ranking_data">'+formatCurrency(val)+'</span>'+
                                    '<span class="ranking_data">'+sourceRank[c].rate+'</span>'+
                                    '<span class="ranking_data">'+sourceRank[c].last_year_amt_trade_rate+'</span>'+
                                    '<span class="ranking_data">'+sourceRank[c].last_time_amt_trade_rate+'</span>'+
                                    '</div>'
                            }
                            $("#sourceListContentBand").html(li).parents(".model_parent").find(".spinner_warp").hide();
                        }else{
                            $("#sourceListContentBand").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break;
                    case "skuRank":
                            var msg=JSON.parse(data.message);
                            var skuRank=msg.skuRank;
                            if(skuRank.length>0){
                                var li="";
                                for(var k= 0,len=skuRank.length;k<len;k++){
                                    var val=Number(skuRank[k].amt_trade);
                                    li+='<div class="list_line">'+
                                        '<span class="list_num list_num_3">'+(k+1)+'</span>'+
                                        '<span >'+skuRank[k].product_code+'</span>'+
                                        '<span class="ranking_data">'+formatCurrency(val)+'</span>'+
                                        '<span class="ranking_data">'+formatCurrency(skuRank[k].num_sales)+'</span>'+
                                        '</div>'
                                }
                                $("#skuListContentBand").html(li).parents(".model_parent").find(".spinner_warp").hide();
                                if(skuRank.length>10){
                                    $("#skuListContentBand").addClass("hasMore");
                                    $("#skuListContentBand").children().eq(9).nextAll().hide();
                                    $("#skuListContentBand").append('<div class="list_line look_more">查看全部</div>');
                                }else{
                                    $("#skuListContentBand").removeClass("hasMore")
                                }
                            }else{
                                $("#skuListContentBand").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                            }
                        break;
                    case "storeRank":
                        var msg=JSON.parse(data.message);
                        var storeRank=msg.storeRank;
                        if(storeRank.length>0){
                            var li="";
                            for(var s= 0,len=storeRank.length;s<len;s++){
                                var val=Number(storeRank[s].amt_trade);
                                li+='<div class="list_line">'+
                                    '<span class="list_num list_num_4">'+(s+1)+'</span>'+
                                    '<span style="width: 31.5%">'+storeRank[s].store_name+'</span>'+
                                    '<span class="ranking_data" >'+formatCurrency(val)+'</span>'+
                                    '<span class="ranking_data" style="width:18%;">'+storeRank[s].last_year_amt_trade_rate+'</span>'+
                                    '<span class="ranking_data" style="width: 18%">'+storeRank[s].last_time_amt_trade_rate+'</span>'+
                                    '</div>'
                            }
                            $("#shopListContentBand").html(li).parents(".model_parent").find(".spinner_warp").hide();
                            if(storeRank.length>10){
                                $("#shopListContentBand").children().eq(9).nextAll().hide();
                                $("#shopListContentBand").addClass("hasMore");
                                $("#shopListContentBand").append('<div class="list_line look_more">查看全部</div>');
                            }else{
                                $("#shopListContentBand").removeClass("hasMore")
                            }
                        }else{
                            $("#shopListContentBand").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break;
                    case "vipCard":
                        var msg=JSON.parse(data.message);
                        var vipCard=msg.vipCard;
                        if(vipCard.length>0){
                            var li="";
                            for(var c= 0,len=vipCard.length;c<len;c++){
                                var val=Number(vipCard[c].vip_num);
                                li+= '<div class="list_line"><span class="list_num list_num_3">'+(c+1)+'</span>'+
                                    '<span >'+vipCard[c].card_type+'</span>'+
                                    '<span class="ranking_data" >'+formatCurrency(val)+'</span>'+
                                    '<span class="ranking_data" >'+vipCard[c].vip_rate+'</span>'+
                                    '</div>'
                            }
                            $("#cardTypeListContentBand").html(li).parents(".model_parent").find(".spinner_warp").hide();
                            if(vipCard.length>10){
                                $("#cardTypeListContentBand").addClass("hasMore");
                                $("#cardTypeListContentBand").children().eq(9).nextAll().hide();
                                $("#cardTypeListContentBand").append('<div class="list_line look_more">查看全部</div>');
                            }else {
                                $("#cardTypeListContentBand").removeClass("hasMore");
                            }
                        }else{
                            $("#cardTypeListContentBand").html("<div class='noDataList'>暂无数据</div>").parents(".model_parent").find(".spinner_warp").hide();
                        }
                        break
                }
            }else{
                switch (type){
                    case "brand":
                        $("#brand_amt_trade").html(0);
                        $("#num_trade").html(0);
                        $("#num_sales").html(0);
                        $("#relate_rate").html(0);
                        $("#discount").html(0);
                        $("#trade_price").html(0);
                        $("#sale_price").html(0);
                        $("#brandListContentBand").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        $("#brand_management").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").show();
                        break;
                    case "newVip":
                        $("#vip_amt_trade").html(0);
                        $("#vip_num_trade").html(0);
                        $("#vip_num_sales").html(0);
                        $("#vip_relate_rate").html(0);
                        $("#vip_discount").html(0);
                        $("#vip_trade_price").html(0);
                        $("#vip_sale_price").html(0);
                        $("#brandAbility").parents(".model_parent").find(".spinner_warp").hide().parents(".model_parent").find(".abilityNoData").show();
                        $("#newVip").parents(".model_parent").find(".abilityNoData").show().parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "sourceRank":
                        $("#sourceListContentBand").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "skuRank":
                        $("#skuListContentBand").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "storeRank":
                        $("#shopListContentBand").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break;
                    case "vipCard":
                        $("#cardTypeListContentBand").html("<div class='noDataList'>获取数据失败</div>").parents(".model_parent").find(".spinner_warp").hide();
                        break
                }
            }
        })
    },
    getChartData:function(type,custom){
        var _self=this;
        var param={
            type:"vip",
            start_time:"",
            end_time:"",
            query_type:type,
            corp_code:theRequest.corp_code,
            brand_code:$("#brand_value_brand").val(),
            source:$('#source_list').val(),
            sign:theRequest.sign,
            method:"vipAnaly",
            custom:custom
        };
        if(type == "cycle"){
            brand.cycleRatioChart.resize();
            _self.cycleRatioChart.showLoading({maskColor: 'rgba(255, 255, 255, 0.1)',textColor: '#ba2832'});
        }else if(type == 'actvip'){
            brand.activeRatioChart90.resize();
            brand.activeRatioChart90.showLoading({maskColor: 'rgba(255, 255, 255, 0.1)',textColor: '#ba2832'});
            brand.activeRatioChart180.resize();
            brand.activeRatioChart180.showLoading({maskColor: 'rgba(255, 255, 255, 0.1)',textColor: '#ba2832'});
        }
        oc.postRequire("post", API,"", param, function (data) {
           if(data.code==0){
               switch (type){
                   case "cycle":
                       $("#cycleRatio").parents(".model_parent").find(".abilityNoData").hide();
                       var msg=JSON.parse(data.message);
                       var cycle=msg.cycle;
                       var data=[];
                       for(var i=0;i<cycle.length;i++){
                           data.push({
                               name:cycle[i].name,
                               value:cycle[i].size
                           })
                       }
                       _self.cycleRatio(data);
                       break;
                   case "actvip":
                       $("#activeVip90").parents(".model_parent").find(".abilityNoData").hide();
                       $("#activeVip180").parents(".model_parent").find(".abilityNoData").hide();
                       _self.activeVipRatio(data);
                       break;
               }
           }else{
               switch (type){
                   case "cycle":
                        brand.cycleRatioChart.hideLoading();
                       $("#cycleRatio").parents(".model_parent").find(".abilityNoData").show();
                       break;
                   case "actvip":
                       brand.activeRatioChart90.hideLoading();
                       $("#activeVip90").parents(".model_parent").find(".abilityNoData").show();
                       brand.activeRatioChart180.hideLoading();
                       $("#activeVip180").parents(".model_parent").find(".abilityNoData").show();
                       break;
               }
           }
        })
    },
    changeTimeType:function(){
        var _self=this;
        $("#time_tabs a").click(function(){
            $(this).addClass("active").parent().siblings().children().removeClass("active");
            _self.initData();
        })
    },
    cycleRatio:function(data){  //生命周期占比
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
                                color: '#888'
                            }
                        }
                    }
                },
                labelLine: {
                    normal: {
                    }
                },
                data:data
            }
        };
        brand.cycleRatioChart.setOption(option);
        brand.cycleRatioChart.hideLoading();
    },
    activeVipRatio:function(data){  //活跃会员占比
        var option={
            title : {
                text: '56%',
                subtext: '90天活跃人数',
                subtextStyle:{
                    fontSize:"12px",
                    color:"#888",
                    fontWeight:"normal"
                },
                x:'center',
                bottom:0,
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
                radius: ['45%', '50%'],
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
                    {value:123, name:'活跃会员占比',label: {
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
                    {value:10, name:'非活跃会员占比'}
                ]
            }
        };
        var msg=JSON.parse(data.message);
        var actvip=msg.actvip;
        var allVip=msg.all_size==0?"1":msg.all_size;
        option.title.text=actvip[0].size;
        option.title.subtext="90天活跃人数";
        option.series.data[0].value=actvip[0].size;
        option.series.data[1].value=allVip-actvip[0].size;
        option.color=["#41b8db","#efefef"];
        option.title.textStyle.color="#41b8db";
        brand.activeRatioChart90=echarts.init(document.getElementById("activeVip90"));
        brand.activeRatioChart90.setOption(option);
        brand.activeRatioChart90.hideLoading();

        option.title.text=actvip[1].size;
        option.title.subtext="180天活跃人数";
        option.title.textStyle.color="#7a4db0";
        option.series.data[0].value=actvip[1].size;
        option.series.data[1].value=allVip-actvip[1].size;
        option.color=["#7a4db0","#efefef"];
        brand.activeRatioChart180=echarts.init(document.getElementById("activeVip180"));
        brand.activeRatioChart180.setOption(option);
        brand.activeRatioChart180.hideLoading();
    },
    bind:function(){
        var _self=this;
        $("#brand_value_brand").change(function(){
            _self.initData();
        });
        $("#source_list").change(function(){
            _self.initData();
        })
    },
    initData:function(){
        $("#content_brand").find(".spinner_warp").show();
        isChangeTabsBrand=true;
        this.getData("brand","z_15");
        this.getData("newVip","z_8");
        this.getData("sourceRank","z_9");
        //this.getData("skuRank","z_10");
        //this.getData("storeRank","z_11");
        this.getData("vipCard","z_12");
        this.getChartData("actvip","z_13");
        this.getChartData("cycle","z_14");
        setSessionParam();
    },
    init:function(){
        this.bind();
        this.changeTimeType();
    }
};
var sessionParams=sessionStorage.getItem("sessionParam");
if(sessionParams && JSON.parse(sessionParams).nowActive=="brand"){
    $(".tabs_head span:last-child").trigger("click");
}
var params=sessionParams?JSON.parse(sessionParams):{};
function chartResize(){
    source.line.resize();
    source.bar.resize();
    brand.activeRatioChart180.resize();
    brand.activeRatioChart90.resize();
    brand.cycleRatioChart.resize();
}
$(".list_content").on("click",".look_more",function(){
    if($(this).siblings(":visible").length>10){
        $(this).parent().children().eq(9).nextAll().hide();
        $(this).show();
        $(this).html("查看全部")
    }else{
        $(this).siblings().show();
        $(this).html("收起全部")
    }
});
window.addEventListener("resize", function() {
    setTimeout(function(){
        brand.activeRatioChart180.resize();
        brand.activeRatioChart90.resize();
        brand.cycleRatioChart.resize();
        source.line.resize();
        source.bar.resize();
    },500)
}, false);
//if(sessionParams && JSON.parse(sessionParams).nowIsActive=="brand"){
//    $(".tabs_head span").eq(1).trigger("click");
//}
$(function(){
    source.init();
    brand.init();

});
//防止ajax重复请求
    var pendingRequests = {};
    $.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
        var key = options.url;
        var name = JSON.parse(originalOptions.data.param).message.custom;
        if(key == "/api/external/youshuBoard" && !pendingRequests[name]){
            pendingRequests[name] = jqXHR;
        }else {
            // jqXHR.abort();    //放弃后触发的提交
            if(pendingRequests[name]){
                pendingRequests[name].abort();
                pendingRequests[name]=jqXHR;
            }
        }
        var complete = options.complete;
        options.complete = function(jqXHR, textStatus) {
            if ($.isFunction(complete)) {
                complete.apply(this, arguments);
            }
        };
    });

function getWebOStype(){
    var browser = navigator.userAgent;
    var isAndroid = browser.indexOf('Android') > -1 || browser.indexOf('Adr') > -1; //android终端
    var isiOS = !!browser.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    if(isAndroid){
        return "Android";
    }else if (isiOS) {
        return "iOS";
    }else{
        return "Unknown"
    }
}

$(".content").scroll(function(){
    var offsetHeight = document.getElementsByClassName("content")[0].offsetHeight,
        scrollHeight = document.getElementsByClassName("content")[0].scrollHeight,
        scrollTop = document.getElementsByClassName("content")[0].scrollTop;
    var osType = getWebOStype();
    if(osType == "Android"){
        if($(this).scrollTop()>0){
            USee.RefreshWebView("N");
        }else {
            USee.RefreshWebView("Y");
        }
    }

    if(scrollTop>=scrollHeight-offsetHeight-500){
        $(".backTop").show();
    }else{
        $(".backTop").hide();
    }
});
$(".backTop").click(function(){
    $(".content").animate({scrollTop:0},300)
});