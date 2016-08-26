var oc = new ObjectControl();
$(function(){
	var Time=getNowFormatDate();
	$(".laydate-icon").val(Time)
    areaRanking(Time);
});
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
				Fn0(datas);
				break;
			case 1:
				achieveChart(datas);
				break;
			case 2:areaRanking(datas);
				break;
			case 3:Fn3(datas);
				break;
		}
	 }
	};
	laydate(start)
}
$(".laydate-icon").click(//点击input 显示日期控件界面
	function () {
		var InputID='#'+$(this).attr("id");
		lay1(InputID)
	}
);
function storeRanking(){//店铺排行
	var param={};
	param["time"]="20160823";
	param["store_name"]="";
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		console.log(data);
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
	$("#area_list tbody").html(area_list);
}
function areaRanking(a){//区域排行
	var param={};
	var a = a.replace(/[-]/g, "");
	param["time"]=a;
	param["area_name"]="";
	oc.postRequire("post","/home/areaRanking","", param, function(data){
		var message = JSON.parse(data.message);
		var total = message.total; //店铺总数
		var achv_detail_d = message.achv_detail_d //日查看店铺排行
		var achv_detail_m = message.achv_detail_m //月查看店铺排行
		var achv_detail_w = message.achv_detail_w //周查看店铺排行
		var achv_detail_y = message.achv_detail_y //年查看店铺排行
		console.log(achv_detail_d);
		$("#area_total").html(total);
		$(".reg_testdate li").click(function() {
			var value = $(this).html();
			var id = $(this).parent("ul").attr("id");
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if (value == "按日查看" && id == "") {
				superadditionArea(achv_detail_d);
			} else if (value == "按周查看" && id == "area") {
				superadditionArea(achv_detail_w);
			} else if (value == "按月查看" && id == "area") {
				superadditionArea(achv_detail_m);
			} else if (value == "按年查看" && id == "area") {
				superadditionArea(achv_detail_y);
			}
		})
		superadditionArea(achv_detail_d);
	})
}
function Fn0(){
	console.log("我是第一个Input")
}
function achieveChart(data){//获取折线图或雷达图
	var param={
		time:data.replace(/-/g,"")
	};
	oc.postRequire("post","/home/achInfo","", param, function(data){
		console.log(data);
	})
}
function Fn3(){
	console.log("我是第四个Input")
}