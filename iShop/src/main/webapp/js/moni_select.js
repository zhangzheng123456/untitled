$(function(){
	$(".item_1 .input_select").click(function(){
		var ul = $(".item_1 ul");
		if(ul.css("display")=="none"){
			ul.show();
		}else{
			ul.hide();
		}
	});
	$(".item_2 .input_select").click(function(){
		var ul = $(".item_2 ul");
		if(ul.css("display")=="none"){
			ul.show();
		}else{
			ul.hide();
		}
	});
	$(".icn_1").click(function(){
		var ul = $(".item_1 ul");
		if(ul.css("display")=="none"){
			ul.show();
		}else{
			ul.hide();
		}
	});
	$(".icn_2").click(function(){
		var ul = $(".item_2 ul");
		if(ul.css("display")=="none"){
			ul.show();
		}else{
			ul.hide();
		}
	});
	$(".item_1 ul li").click(function(){
		var txt = $(this).text();
		$(".item_1 .input_select").val(txt);
		var value = $(this).attr("rel");
		$(".item_1 ul").hide();
	});
	$(".item_2 ul li").click(function(){
		var txt = $(this).text();
		if(txt=="全体成员"){
			$("#sendee").hide();
		}else if(txt=="指定区域"||txt=="指定店铺"||txt=="指定员工"){
			$("#sendee").show();
		}
		if(txt=='Web'){$('.conpany_msg').children(':not(".version_web")').hide()}else{$('.conpany_msg').children(':not(".version_web")').show();}
		$(".item_2 .input_select").val(txt);
		var value = $(this).attr("rel");
		$(".item_2 ul").hide();
	});
	$(".item_1 .input_select").blur(function(){
		var ul = $(".item_1 ul");
		setTimeout(hideLi,200);
	})
	$(".item_2 .input_select").blur(function(){
		var ul = $(".item_2 ul");
		setTimeout(hideli,200);
	})
	function hideLi(){
	    $(".item_1 ul").hide();
	}
	function hideli(){
		$(".item_2 ul").hide();
	}
});