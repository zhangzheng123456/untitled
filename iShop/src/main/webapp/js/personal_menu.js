$(function(){
	$(document).bind('click',function(e){
		var target = $(e.target);
		if(target.closest(".personal_menu").length == 0){
			$('.personal_menu').removeClass('open');
		}else{
			$('.personal_menu').toggleClass('open');
		}
	});
});