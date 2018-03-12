var oc = new ObjectControl();
function getNowFormatDate() {//获取当前日期
	var date = new Date();
	var seperator1 = "-";
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = year + seperator1 + month + seperator1 + strDate;
	return currentdate
}
var today = getNowFormatDate();
$(".icon-text").val(today);
$(".title").mouseover(function() {
	ul = $(this).nextAll("ul");
		ul.show();
	$(this).parent(".choose").toggleClass("cur");
});
$(".title").mouseout(function() {
	ul = $(this).nextAll("ul");
	$(this).parent(".choose").toggleClass("cur");
	ul.hide();
});
$(".select_Date").mouseover(function(){
	$(this).parent(".choose").addClass("cur");
	$(this).show()
});
$(".select_Date").mouseout(function(){
	$(this).parent(".choose").removeClass("cur");
	$(this).hide()
});
//点击店铺
$(".c_a_shoppe").click(function(e){
	var ul=$(".c_a_shoppe ul");
	if(ul.css("display")=="none"){
		ul.show();
		$("#drop_down_m").attr("src","../img/img_arrow_up.png");
	}else if(ul.css("display")!=="none"&&!($(e.target).is(".bm_search_shop"))){
		ul.hide();
		$("#drop_down_m").attr("src","../img/img_arrow_down.png");
	}
});
//本地搜索店铺
$.expr[":"].searchableSelectContains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
$(".bm_search_shop").on('keyup', function(event){
    var text=$(this).val();
    $(this).siblings('li').addClass('store_list_kuang_hide');
    $(this).siblings('li:searchableSelectContains('+text+')').removeClass('store_list_kuang_hide');
});
//获取
function getShopList(){
	oc.postRequire("get", "/shop/findStore", "", "", function(data) {
		if(data.code=="0"){
			var message=JSON.parse(data.message);
			var list=JSON.parse(message.list);
        	$(".area_name").html("全部");
        	$(".area_name").attr("title","");
        	$(".area_name").attr("data-code","");
        	var html="";
        	for(var i=0;i<list.length;i++){
        		html+="<li data-code='"+list[i].store_code+"' title='"+list[i].store_name+"'>"+list[i].store_name+"</li>"
        	}
        	$(".c_a_shoppe ul").append(html);
        	$(".c_a_shoppe ul li").click(function(){
        		var area_code=$(this).attr("data-code");
        		$(".area_name").attr("data-code",area_code);
        		$(".area_name").html($(this).html());
				staffRanking(strReponse($("#staffRanking").val()),area_code);
				achAnalysis(strReponse($("#achAnalysis").val()),area_code);
				goodsRanking(strReponse($("#date4").val()),area_code);
				achieveChart(strReponse($("#date1").val()),area_code);
        	})
		}
	})
}
//导购加载
function superadditionStaff(c) {
	var staff_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		var b="";
		if(c[i].devote_rate<=0){
			b=0;
		}
		if(c[i].devote_rate<=20&&c[i].devote_rate>0){
			b=10;
		}
		if(c[i].devote_rate<=30&&c[i].devote_rate>20){
			b=20;
		}
		if(c[i].devote_rate<=40&&c[i].devote_rate>30){
			b=30;
		}
		if(c[i].devote_rate<=50&&c[i].devote_rate>40){
			b=40;
		}
		if(c[i].devote_rate<=60&&c[i].devote_rate>50){
			b=50;
		}
		if(c[i].devote_rate<=70&&c[i].devote_rate>60){
			b=60;
		}
		if(c[i].devote_rate<=80&&c[i].devote_rate>70){
			b=70;
		}
		if(c[i].devote_rate<=90&&c[i].devote_rate>80){
			b=80;
		}
		if(c[i].devote_rate<100&&c[i].devote_rate>90){
			b=90;
		}
		if(c[i].devote_rate>=100){
			b=100;
		}
		staff_list += "<tr><td style='width:87px'>" + a + "</td><td style='width:87px'>" + c[i].user_name//导购名称
			+ "</td><td style='width:87px'>" + c[i].amount //业绩
			+ "</td><td><span title='"+c[i].store_name+"'>"
			+ c[i].store_name //贡献度
			+ "</span></td></tr>"
	}
	var nodata="<tr><td colSpan='4' style='padding-top:70px;'>暂无数据</td></tr>";
	if(c.length==0){
		$("#staff_list tbody").html(nodata);
	}else {
		$("#staff_list tbody").html(staff_list);
	}
	$("#staff_mask").hide();
}
//导购排行
function staffRanking(a,b) { 
	var a = a.replace(/[-]/g, "");
	var val=$("#staff_type").html();
	var param = {};
	param["time"] = a;
	param["store_name"] = "";
	if(b!==""&&b!==undefined){
		param["store_code"]=b;
	}
	$("#staff_mask").show();
	oc.postRequire("post", "/home/staffRanking", "", param, function(data) {
		var message = JSON.parse(data.message);
		var total = message.user_count; //导购总数
		var store_achv_d = message.store_achv_d //日查看导购排行
		var store_achv_m = message.store_achv_m //月查看导购排行
		var store_achv_w = message.store_achv_w //周查看导购排行
		$("#staff_total").html(total);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "staff") {
				$("#staff_mask").show();
				$("#staff_avg").html(message.store_d_value);
				superadditionStaff(store_achv_d);
			} else if (value == "按周查看" && id == "staff") {
				$("#staff_mask").show();
				$("#staff_avg").html(message.store_w_value);
				superadditionStaff(store_achv_w);
			} else if (value == "按月查看" && id == "staff") {
				$("#staff_mask").show();
				$("#staff_avg").html(message.store_m_value);
				superadditionStaff(store_achv_m);
			}
		})
		if (val == "按日查看") {
			$("#staff_avg").html(message.store_d_value);
			superadditionStaff(store_achv_d);
		} else if (val == "按周查看") {
			$("#staff_avg").html(message.store_w_value);
			superadditionStaff(store_achv_w);
		} else if (val == "按月查看") {
			$("#staff_avg").html(message.store_m_value);
			superadditionStaff(store_achv_m);
		}
	})
}
//业绩追加
function superadditionAchv(c){
	$("#num_sales").html(c.sm.num_sales);
	$("#num_trade").html(c.sm.num_trade);
	$("#all_price").html(c.sm.all_price);
	$("#amount_price").html(c.sm.amount_price);
	$("#relate_rate").html(c.sm.relate_rate);
	$("#discount").html(c.sm.discount);
	$("#num_nvip").html(c.sm.num_nvip);
	$("#vip_amt_rate").html(c.sm.vip_amt_rate);
	$("#amt_trade").html(c.sm.amt_trade);
	$("#area_ranking").attr("data-percent",c.sm.achieve_rate);
	$("#achv_mask").hide();
	$(".yield_rate canvas").remove();
	var chart = window.chart = new EasyPieChart(document.querySelector('.yield_rate span'), {
        easing: 'easeOutElastic',
        delay: 3000,
        barColor: '#6cc1c8',
        trackColor: '#4a5f7c',
        scaleColor: false,
        lineWidth: 10,
        trackWidth: 10,
        lineCap: 'butt',
        onStep: function(from, to, percent) {
            this.el.children[0].innerHTML = Math.round(percent)+"%"+"<div style='color:#97a4b6'>达成率</div>";
        }
    });
}
//业绩加载
function achAnalysis(a,b){
	var a = a.replace(/[-]/g, "");
	var val=$("#achv_type").html();
	var param={};
	param["time"]=a;
	if(b!==""&&b!==undefined){
		param["store_code"]=b;
	}
	$("#achv_mask").show();
	oc.postRequire("post", "/home/achAnalysis", "", param, function(data) {
		var message = JSON.parse(data.message);
		var D=JSON.parse(message.D);
		var M=JSON.parse(message.M);
		var W=JSON.parse(message.W);
		var Y=JSON.parse(message.Y);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "achv") {
				$("#achv_mask").show();
				superadditionAchv(D);
			} else if (value == "按周查看" && id == "achv") {
				$("#achv_mask").show();
				superadditionAchv(W);
			} else if (value == "按月查看" && id == "achv") {
				$("#achv_mask").show();
				superadditionAchv(M);
			} else if (value == "按年查看" && id == "achv") {
				$("#achv_mask").show();
				superadditionAchv(Y);
			}
		})
		if (val == "按日查看") {
			superadditionAchv(D);
		} else if (val == "按周查看") {
			superadditionAchv(W);
		} else if (val == "按月查看") {
			superadditionAchv(M);
		} else if (val == "按年查看") {
			superadditionAchv(Y);
		}
	})
}
//商品排行
function superadditionGoods(c) {
	var goods_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		goods_list +="<tr><td style='width: 87px'>" + a + "</td><td style='width: 87px'>"+c[i].product_code+"</td><td style='width: 87px'>"+c[i].price+"</td><td style='width: 174px'>"+c[i].num_sales+"</td></tr>";
}
	var nodata="<tr><td colSpan='4' style='padding-top:70px;'>暂无数据</td></tr>";
	if(c.length==0){
		$("#goods_list tbody").html(nodata);
	}else {
		$("#goods_list tbody").html(goods_list);
	}
	$("#goods_mask").hide();
}
//商品排行
function goodsRanking(a,b) {
	var param = {};
	var a = a.replace(/[-]/g, "");
	var val=$("#staff_type").html();
	var date_type=$("#goods_type").attr("date-type");
	var store_code=$(".area_name").attr("data-code");
	if(store_code!=''&&store_code!=undefined){
		param["store_code"]=store_code;
	}
	param["time"] = a;
	param["time_type"]=date_type;
	$("#goods_mask").show();
	oc.postRequire("post", "/home/goodsRanking", "", param, function(data) {
		var message = JSON.parse(data.message);
		var total = message.counts; //商品总数
		var sku_list=message.sku_list;//商品列表
		$("#goods_total").html(total);
		superadditionGoods(sku_list);
	})
}
$(".reg_testdate li").click(function() {
	var value = $(this).html();
	var id = $(this).parent("ul").attr("id");
	$(this).parent("ul").prev(".title").html(value);
	$(this).parent("ul").hide();
	$(this).parent("ul").parent(".choose").removeClass("cur");
	var dateType=$(this).attr("date-type");
	$(this).parent("ul").prev(".title").attr("date-type",dateType);
	if(id == "chart"){
		var time=[];
		var role=this.innerHTML=='按周查看'?'week':'month';
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		//var getTime=click_chart?click_chart:time.join('');
		weekFun('date1',time.join('-'),role);
		achieveChart(time.join(''));
	}
	if(id=="goods"){
		//var getTime=$("#date4").val();
		var time=[];
		var role='';
		if(this.innerHTML=='按日查看'){
			role='day'
		}else if(this.innerHTML=='按周查看'){
			role='week'
		}else if(this.innerHTML=='按月查看'){
			role='month'
		}
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		//var getTime2=click_goods?click_goods:time.join('');
		weekFun('date4',time.join('-'),role);
		goodsRanking(time.join(''));
	}
	if(id=='achv'){
		var time=[];
		var role='';
		if(this.innerHTML=='按日查看'){
			role='day'
		}else if(this.innerHTML=='按周查看'){
			role='week'
		}else if(this.innerHTML=='按月查看'){
			role='month'
		}
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		weekFun('achAnalysis',time.join('-'),role);
	}else if(id=='staff'){
		var time=[];
		var role='';
		if(this.innerHTML=='按日查看'){
			role='day'
		}else if(this.innerHTML=='按周查看'){
			role='week'
		}else if(this.innerHTML=='按月查看'){
			role='month'
		}
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		weekFun('staffRanking',time.join('-'),role);
	}
});

function achieveChart(data){//获取折线图
	var param={};
	param["time"]=data.replace(/-/g,"");
	var store_code=$(".area_name").attr("data-code");
	if(store_code!=''&&store_code!=undefined){
		param["store_code"]=store_code;
	}
	$("#chart_mask").show();
	var value=$("#chart_prev").html();
	var date_type=$("#chart_prev").attr("date-type");
	param["date_type"]=date_type;
	oc.postRequire("post","/home/achInfo","", param, function(data){
		var date_type=param["date_type"];
		var perArr=[];
		var dateArr=[];
		var  dateData=JSON.parse(data.message)[date_type];
		$("#yeJiToTal").html(JSON.parse(dateData).total);
		var TimeData=JSON.parse(dateData).amount;
		for(index in TimeData){
			perArr.push(TimeData[index].trade);
			if(value == "按年查看"){
				dateArr.push(TimeData[index].date.substring(2,7));
			}else{
				dateArr.push(TimeData[index].date);
			}
		}
		init(perArr,dateArr);
		$("#chart_mask").hide();
		$(window).resize(function() {
			init(perArr,dateArr);
		});
	})
}
//店铺排行日历
var store = {
	elem: '#storeRanking',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		var area_code=$('.area_name').attr("data-code");
		storeRanking(datas,area_code);
	}
};
//员工排行日历
var staff = {
	elem: '#staffRanking',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: false, //是否开启时间选择
	isclear:false, //是否显示清空
	istoday: false,
	issure: false,// 是否显示确认
	choose: function(datas) {
		var area_code=$('.area_name').attr("data-code");
		if ($('#staff_type').html() == '按周查看') {
			weekFun('staffRanking', datas, 'week');
		} else if ($('#staff_type').html() == '按月查看') {
			weekFun('staffRanking', datas, 'month');
		} else document.getElementById('staffRanking').style.width = '100px';
		staffRanking(datas,area_code);
	}
}
//折线图日历
var achInfo={
	elem: '#date1',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: false, //是否开启时间选择
	isclear:false, //是否显示清空
	istoday: false,
	issure: false,// 是否显示确认
	choose: function(datas) {
		//var area_code=$('.area_name').attr("data-code");
		if($('#chart_prev').html()=='按周查看'){
			weekFun('date1',datas,'week');
		}else if($('#chart_prev').html()=='按月查看'){
			weekFun('date1',datas,'month');
		}else document.getElementById('date1').style.width='100px';
		achieveChart(datas);
	}
}
//商品排行日历
var goods = {
	elem: '#date4',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: false, //是否开启时间选择
	isclear:false, //是否显示清空
	istoday: false,
	issure: false,// 是否显示确认
	choose: function (datas) {
		var area_code = $('.area_name').attr("data-code");
		if ($('#goods_type').html() == '按周查看') {
			weekFun('date4', datas, 'week');
		} else if ($('#goods_type').html() == '按月查看') {
			weekFun('date4', datas, 'month');
		} else document.getElementById('date4').style.width = '100px';
		goodsRanking(datas, area_code);
	}
};
//业绩日历
var achv={
	elem: '#achAnalysis',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: false, //是否开启时间选择
	isclear:false, //是否显示清空
	istoday: false,
	issure: false,// 是否显示确认
	choose: function(datas) {
		var area_code=$('.area_name').attr("data-code");
		if ($('#achv_type').html() == '按周查看') {
			weekFun('achAnalysis', datas, 'week');
		} else if ($('#achv_type').html() == '按月查看') {
			weekFun('achAnalysis', datas, 'month');
		} else document.getElementById('achAnalysis').style.width = '100px';
		achAnalysis(datas,area_code);
	}
}
function strReponse(str){
	var time='';
	if(str.search('至')!=-1){//周
		time=str.split('至')[0]
	}else{//日月年
		if(str.split('-').length==1){//年
			time=str+'0101';
		}else if(str.split('-').length==2){//月
			time=str+'01'
		}else if(str.split('-').length==3){//月
			time=str;
		}
	}
	return time;
}
function weekFun(node,date,role){
	var value='';
	var time=getNowFormatDate();
	//周数据处理
	if(role=='week'){
		var start_week=new Date(date).getDay();
		var start_s='';
		var end_s='';
		if(date==time&&node=="date1"){
			start_s=new Date(date).setDate(new Date(date).getDate()+1-7);
			end_s=new Date(date).setDate(new Date(date).getDate());
		}else {
			if(start_week==0){
				start_s=new Date(date).setDate(new Date(date).getDate()-6);
				end_s=new Date(date);
			}else{
				start_s=new Date(date).setDate(new Date(date).getDate()+1-start_week);
				end_s=new Date(date).setDate(new Date(date).getDate()+(7-start_week));
			}
		}
		var start=new Date(start_s).getFullYear()+'-'+((new Date(start_s).getMonth()+1)<=9?('0'+(new Date(start_s).getMonth()+1)):(new Date(start_s).getMonth()+1))+'-'+(new Date(start_s).getDate()<=9?('0'+new Date(start_s).getDate()):new Date(start_s).getDate());
		var  end=new Date(end_s).getFullYear()+'-'+((new Date(end_s).getMonth()+1)<=9?('0'+(new Date(end_s).getMonth()+1)):(new Date(end_s).getMonth()+1))+'-'+(new Date(end_s).getDate()<=9?('0'+new Date(end_s).getDate()):new Date(end_s).getDate());
		value=start+' 至 '+end;
		document.getElementById(node).style.width='200px';
	}else if(role=='month'){
		value=date.split('-').slice(0,2).join('-');
		document.getElementById(node).style.width='100px';
	}else if(role=='day'){
		value=date.split('-').join('-');
		document.getElementById(node).style.width='100px';
	}else if(role=='year'){
		value=date.split('-')[0];
		document.getElementById(node).style.width='70px';
	}
	document.getElementById(node).value=value;
}
laydate(staff); //导购
laydate(achv);//业绩
laydate(achInfo)//折线图
weekFun('date1',today,'week');
laydate(goods)//商品
staffRanking(today);//导购排行
achAnalysis(today);//业绩
achieveChart(today);//折线图
goodsRanking(today);//商品排行
getShopList()//店长获取店铺列表
$(document).click(function(e){
	if(!$(e.target).parents('.c_a_shoppe').length){
		$('.c_a_shoppe ul').hide();
	}
})