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
function superadditionStore(a){
	var store_list="";
	for(var i=0;i<a.length;i++){
		var a=i+1;
		store_list+="<tr><td style='windth:13%'>"
		+a
		+"</td><td>"
		+a[i].store_name//店铺名称
		+"</td><td>"
		+a[i].achv_amount//店铺业绩
		+"</td><td>"
		+a[i].discount//折扣
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
		console.log(total);
		console.log(achv_detail_d);
		console.log(achv_detail_m);
		console.log(achv_detail_y);
		console.log(achv_detail_w);
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
laydate(start);
storeRanking(today);

$(".reg_testdate").click(function(){
	var div=$(this);
	var select_div=$(this).children('span');
	for(var i=0;i<select_div.length;i++){
		select_div[i].onclick=function(){
			$(this).addClass('select');
			$(this).siblings().removeClass('select');
			$(this).siblings().children('ul').css('display','none');
			$(this).siblings().removeClass('cur')
			$(div).children('.select').children('ul').slideToggle();
			$(this).toggleClass('cur');
			var select_li=$(div).children('.select').children('ul').children('li');
			select_li.on('click',function(){
				$(this).addClass('selected');
				$(this).siblings().removeClass('selected');
				var value=$(this).html();
				$(div).children('.select').children('.title').html(value);
			});
		}
	}
	var CDvalue=$(div).children('.choose').children('.title')[0].innerText;
	if(CDvalue=="按日查看"){
		console.log(0);
		// $(div).children('.Month').show();
		// $(div).children('.Day').show();
		// $(div).children('.Week').hide();
	}else if(CDvalue=="按周查看"){
		console.log(1);
		// $(div).children('.Day').hide();
		// $(div).children('.Month').hide();
		// $(div).children('.Week').show();
	}else if(CDvalue=="按月查看"){
		console.log(2);
		// $(div).children('.Day').hide();
	 //    $(div).children('.Month').show();
	 //    $(div).children('.Week').hide();
	}else if(CDvalue=="按年查看"){
		console.log(3);
		// $(div).children('.Day').hide();
		// $(div).children('.Month').hide();
		// $(div).children('.Week').hide();
	}
	$(div).children('span').on('click',function(){
		var _index=$(this).children('div').html();
		var li_index=$(this).children('ul').children('li');
		for(var i=0;i<li_index.length;i++){
			if(li_index[i].innerText==_index){
				$(li_index[i]).addClass('selected');
			}
		}
	});
})



