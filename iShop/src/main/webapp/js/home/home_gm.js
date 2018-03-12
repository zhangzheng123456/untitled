var oc = new ObjectControl();
$(function(){
	var Time=getNowFormatDate();
	$(".icon-text").val(Time);
	weekFun('date1',Time,'week');
    areaRanking(Time);
	storeRanking(Time);
	achieveChart(Time);
	achAnalysis(Time);
});
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
// 点击显示日周年月
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
function lay1(InputID){//定义日期格式
	var start = {
	 elem:InputID,
	 format: 'YYYY-MM-DD',
	 max: laydate.now(), //最大日期
	 istime: false, //是否开启时间选择
	 isclear:false, //是否显示清空
	 istoday: false,
	 issure: false,// 是否显示确认
	 choose: function(datas) {
		var type=Number(InputID.slice(-1));
		//switch (type){
		//	case 0:
		//		achAnalysis(datas);
		//		break;
		//	case 1:
		//		achieveChart(datas);
		//		break;
		//	case 2:areaRanking(datas);
		//		break;
		//	case 3:storeRanking(datas);
		//		break;
		//}
		 if(type==0){
			 if ($('#gm_achv_prev').html() == '按周查看') {
				 weekFun('date0', datas, 'week');
			 } else if ($('#gm_achv_prev').html() == '按月查看') {
				 weekFun('date0', datas, 'month');
			 } else document.getElementById('date0').style.width = '100px';
			 achAnalysis(datas);
		 }else if(type==1){
			 if($('#chart_prev').html()=='按周查看'){
				 weekFun('date1',datas,'week');
			 }else if($('#chart_prev').html()=='按月查看'){
				 weekFun('date1',datas,'month');
			 }else document.getElementById('date1').style.width='100px';
			 achieveChart(datas);
		 }else if(type==2){
			 if($('#area_prev').html()=='按周查看'){
				 weekFun('date2',datas,'week');
			 }else if($('#area_prev').html()=='按月查看'){
				 weekFun('date2',datas,'month');
			 }else if($('#area_prev').html()=='按年查看'){
				 weekFun('date2',datas,'year');
			 }else document.getElementById('date2').style.width='100px';
			 areaRanking(datas);
		 }else if(type==3){
			 if ($('#store_prev').html() == '按周查看') {
				 weekFun('date3', datas, 'week');
			 } else if ($('#store_prev').html() == '按月查看') {
				 weekFun('date3', datas, 'month');
			 }else if ($('#store_prev').html() == '按年查看') {
				 weekFun('date3', datas, 'year');
			 } else document.getElementById('date3').style.width = '100px';
			 storeRanking(datas);
		 }
	 }
	};
	laydate(start)
}
$(".icon-text").click(//点击input 显示日期控件界面
	function () {
		var InputID='#'+$(this).attr("id");
		lay1(InputID)
	}
);
function storeRanking(a){//店铺排行
	var param={};
	var a = a.replace(/[-]/g, "");
	param["time"]=a;
	param["store_name"]="";
	var value=$("#store_prev").html();
	$("#store_mask").show();
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		var achv_detail_d = message.achv_detail_d; //日查看店铺排行
		var achv_detail_m = message.achv_detail_m;//月查看店铺排行
		var achv_detail_w = message.achv_detail_w; //周查看店铺排行
		var achv_detail_y = message.achv_detail_y; //年查看店铺排行
		$("#store_total").html(total);
		$(".select_4 li").click(function() {
			value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "store") {
				$("#store_mask").show();
				superadditionStore(achv_detail_d);
			} else if (value == "按周查看" && id == "store") {
				$("#store_mask").show();
				superadditionStore(achv_detail_w);
			} else if (value == "按月查看" && id == "store") {
				$("#store_mask").show();
				superadditionStore(achv_detail_m);
			} else if (value == "按年查看" && id == "store") {
				$("#store_mask").show();
				superadditionStore(achv_detail_y);
			}
		});
		if (value == "按日查看") {
			superadditionStore(achv_detail_d);
		} else if (value == "按周查看") {
			superadditionStore(achv_detail_w);
		} else if (value == "按月查看") {
			superadditionStore(achv_detail_m);
		} else if (value == "按年查看") {
			superadditionStore(achv_detail_y);
		}
	})

}
//区域加载
function superadditionArea(c) {
	var area_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		area_list += "<tr><td style='width:145px'>" + a + "</td><td style='width:145px'>" + c[i].area_name //区域名称
			+ "</td><td style='width:145px'>" + c[i].amt_trade//折扣
			+ "</td></tr>"
	}
	var nodata="<tr><td colSpan='3' style='padding-top:70px;'>暂无数据</td></tr>";
	if(c.length==0){
		$("#area_list tbody").html(nodata);
	}else {
		$("#area_list tbody").html(area_list);
	}
	$("#area_mask").hide();
}
//店铺排行加载
function superadditionStore(c) {
	var area_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		area_list += "<tr><td style='width:108px'>" + a + "</td><td style='width:145px'>" + c[i].store_name //区域名称
			+ "</td><td style='width:145px'>" + c[i].achv_amount
			+ "</td><td style='width:145px'>" + c[i].discount//折扣
			+ "</td></tr>"
	}
	var nodata="<tr><td colSpan='3' style='padding-top:70px;'>暂无数据</td></tr>";
	if(c.length==0){
		$("#store_list tbody").html(nodata);
	}else {
		$("#store_list tbody").html(area_list);
	}
	$("#store_mask").hide();
}
function areaRanking(a){//区域排行
	var param={};
	var a = a.replace(/[-]/g, "");
	param["time"]=a;
	param["area_name"]="";
	var value=$("#area_prev").html();
	$("#area_mask").show();
	oc.postRequire("post","/home/areaRanking","", param, function(data){
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		var achv_detail_d = message.achv_detail_d; //日查看店铺排行
		var achv_detail_m = message.achv_detail_m;//月查看店铺排行
		var achv_detail_w = message.achv_detail_w;//周查看店铺排行
		var achv_detail_y = message.achv_detail_y;//年查看店铺排行
		$("#area_total").html(total);
		$(".reg_testdate li").click(function() {
			value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "area") {
				$("#area_mask").show();
				superadditionArea(achv_detail_d);
			} else if (value == "按周查看" && id == "area") {
				$("#area_mask").show();
				superadditionArea(achv_detail_w);
			} else if (value == "按月查看" && id == "area") {
				$("#area_mask").show();
				superadditionArea(achv_detail_m);
			} else if (value == "按年查看" && id == "area") {
				$("#area_mask").show();
				superadditionArea(achv_detail_y);
			}
		});
		if (value == "按日查看") {
			superadditionArea(achv_detail_d);
		} else if (value == "按周查看") {
			superadditionArea(achv_detail_w);
		} else if (value == "按月查看") {
			superadditionArea(achv_detail_m);
		} else if (value == "按年查看") {
			superadditionArea(achv_detail_y);
		}
	})
}


$(".reg_testdate li").click(function() {
	var value = $(this).html();
	var id = $(this).parent("ul").attr("id");
	$(this).parent("ul").prev(".title").html(value);
	$(this).parent("ul").hide();
	$(this).parent("ul").parent(".choose").removeClass("cur");
	var getTime=$("#date1").val();
	var dateType=$(this).attr("date-type");
	$(this).parent("ul").prev(".title").attr("date-type",dateType);
	if(id == "chart"){
		var time=[];
		var role=this.innerHTML=='按周查看'?'week':'month';
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		weekFun('date1',time.join('-'),role);
		achieveChart(time.join(''));
		//achieveChart(getTime);
	}else if(id=='gm_achv'){
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
		weekFun('date0',time.join('-'),role);
	}else if(id=='area'){
		var time=[];
		var role='';
		if(this.innerHTML=='按日查看'){
			role='day'
		}else if(this.innerHTML=='按周查看'){
			role='week'
		}else if(this.innerHTML=='按月查看'){
			role='month'
		}else if(this.innerHTML=='按年查看'){
			role='year'
		}
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		weekFun('date2',time.join('-'),role);
	}
	else if(id=='store'){
		var time=[];
		var role='';
		if(this.innerHTML=='按日查看'){
			role='day'
		}else if(this.innerHTML=='按周查看'){
			role='week'
		}else if(this.innerHTML=='按月查看'){
			role='month'
		}else if(this.innerHTML=='按年查看'){
			role='year'
		}
		time.push(new Date().getFullYear());
		time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
		time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
		weekFun('date3',time.join('-'),role);
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
			}else {
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
//function achieveChart(data){//获取折线图
//	var param={
//		time:data.replace(/-/g,"")
//	};
//	$("#chart_mask").show();
//	oc.postRequire("post","/home/achInfo","", param, function(data){
//		var infodata_W=JSON.parse(data.message).W;
//		var infodata_M=JSON.parse(data.message).M;
//		var infodata_Y=JSON.parse(data.message).Y;
//		var TimeData=JSON.parse(infodata_W).amount;
//		var value=$("#chart_prev").html();
//		var perArr=[];
//		var dateArr=[];
//		if (value == "按周查看") {
//			TimeData=JSON.parse(infodata_W).amount;
//			$("#yeJiToTal").html(JSON.parse(infodata_W).total);
//		} else if (value == "按月查看") {
//			TimeData=JSON.parse(infodata_M).amount;
//			$("#yeJiToTal").html(JSON.parse(infodata_M).total);
//		} else if (value == "按年查看") {
//			TimeData=JSON.parse(infodata_Y).amount;
//			$("#yeJiToTal").html(JSON.parse(infodata_Y).total);
//		}
//		for(index in TimeData){
//			perArr.push(TimeData[index].trade);
//			if(value == "按年查看"){
//				dateArr.push(TimeData[index].date.substring(2,7));
//			}else {
//				dateArr.push(TimeData[index].date);
//			}
//		}
//		$("#chart_mask").hide();
//		init(perArr,dateArr);
//		function setData(V){
//			for(index in TimeData){
//				perArr.push(TimeData[index].trade);
//				if(V == "按年查看"){
//					dateArr.push(TimeData[index].date.substring(2,7));
//				}else {
//					dateArr.push(TimeData[index].date);
//				}
//			}
//		}
//		$(".reg_testdate li").click(function() {
//			perArr=[];
//			dateArr=[];
//			var value = $(this).html();
//			var id = $(this).parent("ul").attr("id");
//			$(this).parent("ul").prev(".title").html(value);
//			$(this).parent("ul").hide();
//			$(this).parent("ul").parent(".choose").removeClass("cur");
//			 if (value == "按周查看" && id == "chart") {
//				 TimeData=JSON.parse(infodata_W).amount;
//				 $("#yeJiToTal").html(JSON.parse(infodata_W).total);
//				 setData(value);
//				 init(perArr,dateArr);
//			} else if (value == "按月查看" && id == "chart") {
//				 TimeData=JSON.parse(infodata_M).amount;
//				 $("#yeJiToTal").html(JSON.parse(infodata_M).total);
//				 setData(value);
//				 init(perArr,dateArr);
//			} else if (value == "按年查看" && id == "chart") {
//				 TimeData=JSON.parse(infodata_Y).amount;
//				 $("#yeJiToTal").html(JSON.parse(infodata_Y).total);
//				 setData(value);
//				 init(perArr,dateArr);
//			}
//
//		});
//		$(window).resize(function() {
//			init(perArr,dateArr);
//		});
//	})
//}
//业绩追加
function superadditionAchv(c){
	$("#num_sales").html(c.gm.num_sales);
	$("#num_trade").html(c.gm.num_trade);
	$("#all_price").html(c.gm.all_price);
	$("#amount_price").html(c.gm.amount_price);
	$("#relate_rate").html(c.gm.relate_rate);
	$("#discount").html(c.gm.discount);
	$("#num_nvip").html(c.gm.num_nvip);
	$("#vip_amt_rate").html(c.gm.vip_amt_rate);
	$("#amt_trade").html(c.gm.amt_trade);
	$("#area_ranking").attr("data-percent",c.gm.achieve_rate);
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
function achAnalysis(a){
	var a = a.replace(/[-]/g, "");
	var param={};
	param["time"]=a;
	var value=$('#gm_achv_prev').html();
	$("#achv_mask").show();
	oc.postRequire("post", "/home/achAnalysis", "", param, function(data) {
		var message = JSON.parse(data.message);
		var D=JSON.parse(message.D);
		var M=JSON.parse(message.M);
		var W=JSON.parse(message.W);
		var Y=JSON.parse(message.Y);
		$(".reg_testdate li").click(function() {
			value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "gm_achv") {
				$("#achv_mask").show();
				superadditionAchv(D);
			} else if (value == "按周查看" && id == "gm_achv") {
				$("#achv_mask").show();
				superadditionAchv(W);
			} else if (value == "按月查看" && id == "gm_achv") {
				$("#achv_mask").show();
				superadditionAchv(M);
			} else if (value == "按年查看" && id == "gm_achv") {
				$("#achv_mask").show();
				superadditionAchv(Y);
			}
		});
		if (value == "按日查看") {
			superadditionAchv(D);
		} else if (value == "按周查看") {
			superadditionAchv(W);
		} else if (value == "按月查看") {
			superadditionAchv(M);
		} else if (value == "按年查看") {
			superadditionAchv(Y);
		}
	})
}
$(document).click(function(e){
	if(!$(e.target).parents('.c_a_shoppe').length){
		$('.c_a_shoppe ul').hide();
	}
})