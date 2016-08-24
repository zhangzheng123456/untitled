var oc = new ObjectControl();
function storeRanking(){//店铺排行
	var param={};
	param["time"]="20160823";
	param["store_name"]="";
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		console.log(data);
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
    	var datas=datas.replace(/[-]/g,"");
    	console.log(datas);
    	storeRanking(datas);
    }
};
//点击日周年月的生日
function CDT(obj)//选择删选条件（按日、按周、按月、按年）
{
	var select_div=$(obj).children('span');
	for(var i=0;i<select_div.length;i++){
		select_div[i].onclick=function(){
			$(this).addClass('select');
			$(this).siblings().removeClass('select');
			$(this).siblings().children('ul').css('display','none');
			$(this).siblings().removeClass('cur')
			$(obj).children('.select').children('ul').slideToggle();
			$(this).toggleClass('cur');
			var select_li=$(obj).children('.select').children('ul').children('li');
			select_li.on('click',function(){
				$(this).addClass('selected');
				$(this).siblings().removeClass('selected');
				var value=$(this).html();
				$(obj).children('.select').children('.title').html(value);
			});
		}
	}
	var CDvalue=$(obj).children('.choose').children('.title')[0].innerText;
	if(CDvalue=="按日查看"){
		$(obj).children('.Month').show();
		$(obj).children('.Day').show();
		$(obj).children('.Week').hide();
	}else if(CDvalue=="按周查看"){
		$(obj).children('.Day').hide();
		$(obj).children('.Month').hide();
		$(obj).children('.Week').show();
	}else if(CDvalue=="按月查看"){
		$(obj).children('.Day').hide();
	    $(obj).children('.Month').show();
	    $(obj).children('.Week').hide();
	}else if(CDvalue=="按年查看"){
		$(obj).children('.Day').hide();
		$(obj).children('.Month').hide();
		$(obj).children('.Week').hide();
	}
	$(obj).children('span').on('click',function(){
		
		var _index=$(this).children('div').html();
		var li_index=$(this).children('ul').children('li');
		for(var i=0;i<li_index.length;i++){
			if(li_index[i].innerText==_index){
				$(li_index[i]).addClass('selected');
			}
		}
	});
}
laydate(start);
storeRanking();

