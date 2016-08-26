var oc = new ObjectControl();
$(function(){
	var Time=getNowFormatDate();
	$(".laydate-icon").val(Time)

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
			case 2:Fn2(datas);
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
function areaRanking(){//区域排行
	var param={};
	param["time"]="20160823";
	param["area_name"]="";
	oc.postRequire("post","/home/areaRanking","", param, function(data){
		console.log(data);
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
function Fn2(){
	console.log("我是第三个Input")
}
function Fn3(){
	console.log("我是第四个Input")
}
storeRanking();
areaRanking();