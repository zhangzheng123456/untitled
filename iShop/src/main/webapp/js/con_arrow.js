$(function(){
	var second_li=$('.nav-second-level > li >a');
	for(var i=0;i<second_li.length;i++){
		$(second_li[i]).click(function() {
			$(this).parent().addClass('index_now').siblings().removeClass('index_now');
			$(this).children('.arrow').toggle();
			$(this).parent().siblings().children('a').children('.arrow').css('display','none');
		});
	}
	var third_li=$('.nav-third-level > li >a');
	for(var j=0;j<third_li.length;j++){
		$(third_li[j]).click(function() {
			$(this).parent().addClass('index3_now').siblings().removeClass('index3_now');
			$(this).children('.arrow').toggle();
			$(this).parent().siblings().children('a').children('.arrow').css('display','none');
		});
	}
});