var oc = new ObjectControl();
var myDate = new Date();       
var year=myDate.getFullYear();
var month=myDate.getMonth()+1;
if(month<10){
	month="0"+month;
}else if(month>=10){
	month=month;
}
var data=myDate.getDate();
var today=year+"-"+month+"-"+data;
$(".icon-text").val(today);
//点击显示日周年月
$(".title").click(function(){
	ul=$(this).nextAll("ul");
	$(this).parent(".choose").toggleClass("cur");
	if(ul.css("display")=="none"){
        ul.show();
    }else{
        ul.hide();
    };
});
//加载
function superadditionStore(c){
	var store_list="";
	for(var i=0;i<c.length;i++){
		var a=i+1;
		store_list+="<tr><td style='windth:13%'>"
		+a
		+"</td><td>"
		+c[i].store_name//店铺名称
		+"</td><td>"
		+c[i].achv_amount//店铺业绩
		+"</td><td>"
		+c[i].discount//折扣
		+"</td></tr>"
	}
	$("#store_list tbody").html(store_list);
}
function storeRanking(a){//店铺排行
	var a=a.replace(/[-]/g,"");
	var param={};
	param["time"]=a;
	param["store_name"]="";
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		var message=JSON.parse(data.message);
		var total=message.total;//店铺总数
		var achv_detail_d=message.achv_detail_d//日查看店铺排行
		var achv_detail_m=message.achv_detail_m//月查看店铺排行
		var achv_detail_w=message.achv_detail_w//周查看店铺排行
		var achv_detail_y=message.achv_detail_y//年查看店铺排行
		$("#store_total").html(total);
		$(".reg_testdate li").click(function(){
			var value=$(this).html();
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if(value=="按日查看"){
				superadditionStore(achv_detail_d);
			}else if(value=="按周查看"){
				superadditionStore(achv_detail_w);
			}else if(value=="按月查看"){
				superadditionStore(achv_detail_m);
			}else if(value=="按年查看"){
				superadditionStore(achv_detail_y);
			}
		})
		superadditionStore(achv_detail_d);
	})
}
function staffRanking(a){//导购排行
	var a=a.replace(/[-]/g,"");
	var param={};
	param["time"]=a;
	param["store_name"]="";
	oc.postRequire("post","/home/staffRanking","", param, function(data){
		var message=JSON.parse(data.message);
		var total=message.total;//店铺总数
		var achv_detail_d=message.achv_detail_d//日查看店铺排行
		var achv_detail_m=message.achv_detail_m//月查看店铺排行
		var achv_detail_w=message.achv_detail_w//周查看店铺排行
		var achv_detail_y=message.achv_detail_y//年查看店铺排行
		$("#store_total").html(total);
		$(".reg_testdate li").click(function(){
			var value=$(this).html();
			$(this).parent("ul").prev(".title").html(value);
			$(this).parent("ul").hide();
			$(this).parent("ul").parent(".choose").removeClass("cur");
			if(value=="按日查看"){
				superadditionStore(achv_detail_d);
			}else if(value=="按周查看"){
				superadditionStore(achv_detail_w);
			}else if(value=="按月查看"){
				superadditionStore(achv_detail_m);
			}else if(value=="按年查看"){
				superadditionStore(achv_detail_y);
			}
		})
		superadditionStore(achv_detail_d);
	})
}
//店铺排行
var start = {
    elem: '#storeRanking',
    format: 'YYYY-MM-DD',
    max: laydate.now(), //最大日期
    istime: true,
    istoday: false,
    choose: function(datas) {
    	storeRanking(datas);
    }
};
var start = {
	elem: '#staffRanking',
    format: 'YYYY-MM-DD',
    max: laydate.now(), //最大日期
    istime: true,
    istoday: false,
    choose: function(datas) {
    	storeRanking(datas);
    }
}
laydate(start);
storeRanking(today);





