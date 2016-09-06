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
var today = getNowFormatDate()
$(".icon-text").val(today);
$(".title").mouseover(function() {
	ul = $(this).nextAll("ul");
		ul.show();
	$(this).parent(".choose").toggleClass("cur");
	console.log(ul.css("display"))
});
$(".title").mouseout(function() {
	ul = $(this).nextAll("ul");
	$(this).parent(".choose").toggleClass("cur");
	ul.hide();
	console.log(ul.css("display"))
});
$(".select_Date").mouseover(function(){
	$(this).parent(".choose").toggleClass("cur");
	$(this).show()
});
$(".select_Date").mouseout(function(){
	$(this).parent(".choose").toggleClass("cur");
	$(this).hide()
});

//点击区域
$(".c_a_shoppe").click(function(){
	var ul=$(".c_a_shoppe ul");
	if(ul.css("display")=="none"){
		ul.show();
		$("#drop_down_m").attr("src","../img/img_arrow_up.png");
	}else{
		ul.hide();
		$("#drop_down_m").attr("src","../img/img_arrow_down.png");
	}
})
//区域获取区域
function getAreaList(){
	oc.postRequire("get", "/area/findArea", "", "", function(data) {
		if(data.code=="0"){
			var message=JSON.parse(data.message);
			var list=JSON.parse(message.list);
        	console.log(list);
        	$(".area_name").html(list[0].area_name);
        	$(".area_name").attr("title",list[0].area_name);
        	$(".area_name").attr("data-code",list[0].area_code);
        	var html="";
        	for(var i=0;i<list.length;i++){
        		html+="<li data-code='"+list[i].area_code+"'>"+list[i].area_name+"</li>"
        	}
        	$(".c_a_shoppe ul").html(html);
        	$(".c_a_shoppe ul li").click(function(){
        		var area_code=$(this).attr("data-code");
        		$(".area_name").attr("data-code",area_code);
        		$(".area_name").html($(this).html());
        		storeRanking(today,area_code);
				staffRanking(today,area_code);
				achAnalysis(today,area_code);
				achieveChart(today);
        	})
		}
	})
}
//店铺加载
function superadditionStore(c) {
	var store_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		store_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].store_name //店铺名称
			+ "</td><td>" + c[i].achv_amount //店铺业绩
			+ "</td><td>" + c[i].discount //折扣
			+ "</td></tr>"
	}
	var nodata="<tr><td colSpan='3' style='padding-top:70px;'>暂无数据</td></tr>";
	if(c.length==0){
		$("#store_list tbody").html(nodata);
	}else {
		$("#store_list tbody").html(store_list);
	}
	$("#store_mask").hide();
}
//店铺排行
function storeRanking(a,b) {
	var a = a.replace(/[-]/g, "");
	var val=$("#store_type").html();
	var param = {};
	param["time"] = a;
	param["store_name"] = "";
	if(b!==""&&b!==undefined){
		param["area_code"]=b;
	}
	$("#store_mask").show();
	oc.postRequire("post", "/home/storeRanking", "", param, function(data) {
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		var achv_detail_d = message.achv_detail_d //日查看店铺排行
		var achv_detail_m = message.achv_detail_m //月查看店铺排行
		var achv_detail_w = message.achv_detail_w //周查看店铺排行
		var achv_detail_y = message.achv_detail_y //年查看店铺排行
		$("#store_total").html(total);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
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
		})
		if (val == "按日查看") {
			superadditionStore(achv_detail_d);
		} else if (val == "按周查看") {
			superadditionStore(achv_detail_w);
		} else if (val == "按月查看") {
			superadditionStore(achv_detail_m);
		} else if (val == "按年查看") {
			superadditionStore(achv_detail_y);
		}
	})
}
//导购加载
function superadditionStaff(c) {
	var staff_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		staff_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].user_name//导购名称
			+ "</td><td>" + c[i].amount //业绩
			+ "</td><td>" + c[i].store_name //所属店铺
			+ "</td></tr>"
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
		param["area_code"]=b;
	}
	$("#staff_mask").show();
	oc.postRequire("post", "/home/staffRanking", "", param, function(data) {
		var message = JSON.parse(data.message);
		var total = message.user_count; //导购总数
        console.log(message);
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
function achAnalysis(a,b){
	var a = a.replace(/[-]/g, "");
	var val=$("#achv_type").html();
	var param={};
	param["time"]=a;
	if(b!==""&&b!==undefined){
		param["area_code"]=b;
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
//获取折线图
// function achieveChart(a,b){
// 	var a = a.replace(/[-]/g, "");
// 	var param={};
// 	param["time"]=a;
// 	if(b!==""&&b!==undefined){
// 		param["area_code"]=b;
// 	}
// 	$("#chart_mask").show();
// 	oc.postRequire("post","/home/achInfo","", param, function(data){
// 		var infodata_W=JSON.parse(data.message).W;
// 		var infodata_M=JSON.parse(data.message).M;
// 		var infodata_Y=JSON.parse(data.message).Y;
// 		var TimeData=JSON.parse(infodata_W).amount;
// 		var value=$("#chart_prev").html();
// 		var perArr=[];
// 		var dateArr=[];
// 		console.log(JSON.parse(data.message));
// 		$("#chart_mask").hide();
// 		if (value == "按周查看") {
// 			TimeData=JSON.parse(infodata_W).amount;
// 			$("#yeJiToTal").html(JSON.parse(infodata_W).total);
// 		} else if (value == "按月查看") {
// 			TimeData=JSON.parse(infodata_M).amount;
// 			$("#yeJiToTal").html(JSON.parse(infodata_M).total);
// 		} else if (value == "按年查看") {
// 			TimeData=JSON.parse(infodata_Y).amount;
// 			$("#yeJiToTal").html(JSON.parse(infodata_Y).total);
// 		}
// 		for(index in TimeData){
// 			perArr.push(TimeData[index].trade);
// 			if(value == "按年查看"){
// 				dateArr.push(TimeData[index].date.substring(2,7));
// 			}else {
// 				dateArr.push(TimeData[index].date);
// 			}
// 		}
// 		init(perArr,dateArr);
// 		function setData(V){
// 			for(index in TimeData){
// 				perArr.push(TimeData[index].trade);
// 				if(V == "按年查看"){
// 					dateArr.push(TimeData[index].date.substring(2,7));
// 				}else {
// 					dateArr.push(TimeData[index].date);
// 				}
// 			}
// 		}
// 		$(".reg_testdate li").click(function() {
// 			perArr=[];
// 			dateArr=[];
// 			var value = $(this).html();
// 			var id = $(this).parent("ul").attr("id");
// 			$(this).parent("ul").prev(".title").html(value);
// 			$(this).parent("ul").hide();
// 			$(this).parent("ul").parent(".choose").removeClass("cur");
// 			 if (value == "按周查看" && id == "chart") {
// 				 TimeData=JSON.parse(infodata_W).amount;
// 				 $("#yeJiToTal").html(JSON.parse(infodata_W).total);
// 				 setData(value);
// 				 init(perArr,dateArr);
// 			} else if (value == "按月查看" && id == "chart") {
// 				 TimeData=JSON.parse(infodata_M).amount;
// 				 $("#yeJiToTal").html(JSON.parse(infodata_M).total);
// 				 setData(value);
// 				 init(perArr,dateArr);
// 			} else if (value == "按年查看" && id == "chart") {
// 				 TimeData=JSON.parse(infodata_Y).amount;
// 				 $("#yeJiToTal").html(JSON.parse(infodata_Y).total);
// 				 setData(value);
// 				 init(perArr,dateArr);
// 			}

// 		});
// 	})
// }
$(".reg_testdate li").click(function() {
    var value = $(this).html();
    var id = $(this).parent("ul").attr("id");
    $(this).parent("ul").prev(".title").html(value);
    $(this).parent("ul").hide();
    $(this).parent("ul").parent(".choose").removeClass("cur");
    var getTime=$("#achvChart").val();
    var dateType=$(this).attr("date-type");
    $(this).parent("ul").prev(".title").attr("date-type",dateType);
  if(id == "chart"){
    achieveChart(getTime);
  }
});
function achieveChart(data){//获取折线图
    var param={};
    param["time"]=data.replace(/-/g,"");
    var area_code=$(".area_name").attr("data-code");
    if(area_code!=''&&area_code!=undefined){
        param["area_code"]=area_code;
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
            }else if(value == "按月查看"){
                dateArr.push(TimeData[index].date.substring(5));
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
//员工排行日历
var staff = {
	elem: '#staffRanking',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		var area_code=$('.area_name').attr("data-code");
		staffRanking(datas,area_code);
	}
}
//折线图日历
var achInfo={
	elem: '#achvChart',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		// var area_code=$('.area_name').attr("data-code");
		achieveChart(datas);
	}
}
//业绩日历
var achv={
	elem: '#achAnalysis',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		var area_code=$('.area_name').attr("data-code");
		achAnalysis(datas,area_code);
	}
}
laydate(store); //店铺
laydate(staff); //员工
laydate(achv);//业绩
laydate(achInfo)//折线图
storeRanking(today);
staffRanking(today);
achAnalysis(today);
achieveChart(today);
getAreaList()//获取区域列表