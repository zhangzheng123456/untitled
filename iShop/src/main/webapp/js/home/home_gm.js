var oc = new ObjectControl();
$(function(){
	var Time=getNowFormatDate();
	$(".icon-text").val(Time);
    areaRanking(Time);
	storeRanking(Time);
	// achieveChart(Time);
	achAnalysis(Time);
});
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
	$(this).parent(".choose").toggleClass("cur");
	$(this).show()
});
$(".select_Date").mouseout(function(){
	$(this).parent(".choose").toggleClass("cur");
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
	 istime: true,
	 istoday: false,
	 choose: function(datas) {
		var type=Number(InputID.slice(-1));
		switch (type){
			case 0:
				achAnalysis(datas);
				break;
			case 1:
				achieveChart(datas);
				break;
			case 2:areaRanking(datas);
				break;
			case 3:storeRanking(datas);
				break;
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
		//console.log(achv_detail_d);
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
		area_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].area_name //区域名称
			+ "</td><td>" + c[i].amt_trade//折扣
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
		area_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].store_name //区域名称
			+ "</td><td>" + c[i].achv_amount
			+ "</td><td>" + c[i].discount//折扣
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
	area_mask
	$("#area_mask").show();
	oc.postRequire("post","/home/areaRanking","", param, function(data){
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		//console.log(message);
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
function achieveChart(data){//获取折线图
	var param={
		time:data.replace(/-/g,"")
	};
	$("#chart_mask").show();
	oc.postRequire("post","/home/achInfo","", param, function(data){
		var infodata_W=JSON.parse(data.message).W;
		var infodata_M=JSON.parse(data.message).M;
		var infodata_Y=JSON.parse(data.message).Y;
		var TimeData=JSON.parse(infodata_W).amount;
		var value=$("#chart_prev").html();
		var perArr=[];
		var dateArr=[];
		if (value == "按周查看") {
			TimeData=JSON.parse(infodata_W).amount;
			$("#yeJiToTal").html(JSON.parse(infodata_W).total);
		} else if (value == "按月查看") {
			TimeData=JSON.parse(infodata_M).amount;
			$("#yeJiToTal").html(JSON.parse(infodata_M).total);
		} else if (value == "按年查看") {
			TimeData=JSON.parse(infodata_Y).amount;
			$("#yeJiToTal").html(JSON.parse(infodata_Y).total);
		}
		for(index in TimeData){
			perArr.push(TimeData[index].trade);
			if(value == "按年查看"){
				dateArr.push(TimeData[index].date.substring(2,7));
			}else {
				dateArr.push(TimeData[index].date);
			}
		}
		$("#chart_mask").hide();
		init(perArr,dateArr);
		function setData(V){
			for(index in TimeData){
				perArr.push(TimeData[index].trade);
				if(V == "按年查看"){
					dateArr.push(TimeData[index].date.substring(2,7));
				}else {
					dateArr.push(TimeData[index].date);
				}
			}
		}
		$(".reg_testdate li").click(function() {
			perArr=[];
			dateArr=[];
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			 if (value == "按周查看" && id == "chart") {
				 TimeData=JSON.parse(infodata_W).amount;
				 $("#yeJiToTal").html(JSON.parse(infodata_W).total);
				 setData(value);
				 init(perArr,dateArr);
			} else if (value == "按月查看" && id == "chart") {
				 TimeData=JSON.parse(infodata_M).amount;
				 $("#yeJiToTal").html(JSON.parse(infodata_M).total);
				 setData(value);
				 init(perArr,dateArr);
			} else if (value == "按年查看" && id == "chart") {
				 TimeData=JSON.parse(infodata_Y).amount;
				 $("#yeJiToTal").html(JSON.parse(infodata_Y).total);
				 setData(value);
				 init(perArr,dateArr);
			}

		});
		$(window).resize(function() {
			init(perArr,dateArr);
		});
	})
}
//业绩追加
function superadditionAchv(c){
	$("#num_sales").html(c.am.num_sales);
	$("#num_trade").html(c.am.num_trade);
	$("#all_price").html(c.am.all_price);
	$("#amount_price").html(c.am.amount_price);
	$("#relate_rate").html(c.am.relate_rate);
	$("#discount").html(c.am.discount);
	$("#num_nvip").html(c.am.num_nvip);
	$("#vip_amt_rate").html(c.am.vip_amt_rate);
	$("#amt_trade").html(c.am.amt_trade);
	$("#area_ranking").attr("data-percent",c.am.area_ranking);
	$("#achv_mask").hide();
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
