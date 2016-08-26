var oc = new ObjectControl();
var myDate = new Date();
var year = myDate.getFullYear();
var month = myDate.getMonth() + 1;
if (month < 10) {
	month = "0" + month;
} else if (month >= 10) {
	month = month;
}
var data = myDate.getDate();
var today = year + "-" + month + "-" + data;
$(".icon-text").val(today);
//点击显示日周年月
$(".title").click(function() {
	ul = $(this).nextAll("ul");
	$(this).parent(".choose").toggleClass("cur");
	if (ul.css("display") == "none") {
		ul.show();
	} else {
		ul.hide();
	};
});
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
	$("#store_list tbody").html(store_list);
}
//店铺排行
function storeRanking(a) {
	var a = a.replace(/[-]/g, "");
	var param = {};
	param["time"] = a;
	param["store_name"] = "";
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
				superadditionStore(achv_detail_d);
			} else if (value == "按周查看" && id == "store") {
				superadditionStore(achv_detail_w);
			} else if (value == "按月查看" && id == "store") {
				superadditionStore(achv_detail_m);
			} else if (value == "按年查看" && id == "store") {
				superadditionStore(achv_detail_y);
			}
		})
		superadditionStore(achv_detail_d);
	})
}
//导购加载
function superadditionStaff(c) {
	var store_list = "";
	for (var i = 0; i < c.length; i++) {
		var a = i + 1;
		store_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].store_name //导购名称
			+ "</td><td>" + c[i].achv_amount //业绩
			+ "</td><td>" + c[i].discount //所属店铺
			+ "</td></tr>"
	}
	$("#store_list tbody").html(store_list);
}
//导购排行
function staffRanking(a) { 
	var a = a.replace(/[-]/g, "");
	var param = {};
	param["time"] = a;
	param["store_name"] = "";
	oc.postRequire("post", "/home/staffRanking", "", param, function(data) {
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		var achv_detail_d = message.achv_detail_d //日查看店铺排行
		var achv_detail_m = message.achv_detail_m //月查看店铺排行
		var achv_detail_w = message.achv_detail_w //周查看店铺排行
		var achv_detail_y = message.achv_detail_y //年查看店铺排行
		$("#staff_total").html(total);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "staff") {
				superadditionStaff(achv_detail_d);
			} else if (value == "按周查看" && id == "staff") {
				superadditionStaff(achv_detail_w);
			} else if (value == "按月查看" && id == "staff") {
				superadditionStaff(achv_detail_m);
			} else if (value == "按年查看" && id == "staff") {
				superadditionStaff(achv_detail_y);
			}
		})
		superadditionStaff(achv_detail_d);
	})
}
//业绩
function achAnalysis(a){
	var a = a.replace(/[-]/g, ""); 
	var param={};
	param["time"]=a;
	oc.postRequire("post", "/home/achAnalysis", "", param, function(data) {
		var message = JSON.parse(data.message);
		console.log(message);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "achv") {
				superadditionStaff(achv_detail_d);
			} else if (value == "按周查看" && id == "achv") {
				superadditionStaff(achv_detail_w);
			} else if (value == "按月查看" && id == "achv") {
				superadditionStaff(achv_detail_m);
			} else if (value == "按年查看" && id == "achv") {
				superadditionStaff(achv_detail_y);
			}
		})
		// superadditionStaff(achv_detail_d);
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
		storeRanking(datas);
	}
};
//员工排行日历
var staff = {
	elem: '#staffRanking',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		staffRanking(datas);
	}
}
//折线图日历
var achInfo={
	elem: '#staffRanking',
	format: 'YYYY-MM-DD',
	max: laydate.now(), //最大日期
	istime: true,
	istoday: false,
	choose: function(datas) {
		staffRanking(datas);
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
		achAnalysis(datas);
	}
}
laydate(store); //店铺
laydate(staff); //员工
laydate(achv);//业绩
storeRanking(today);
staffRanking(today);
achAnalysis(today);
