var oc = new ObjectControl();
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var isscroll=false;
//点击新增的时候弹出不同的框
$("#screen_add").click(function(){
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	if(r_code==undefined||r_code==""){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择所属群组"
		});
		return;
	}
	if(r_code=="R2000"||r_code=="R3000"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_shop").show();
		$("#screen_shop").css({"position":"fixed"});
		$("#screen_area").hide();
		shop_num=1;
		isscroll=false;
		$("#store_search").val("");
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_shop .screen_content_l ul").empty();
		$("#screen_shop .screen_content_r ul").empty();
		$("#screen_area .screen_content_l ul").empty();
		$("#screen_area .screen_content_r ul").empty();
		$("#screen_brand .screen_content_l ul").empty();
		$("#screen_brand .screen_content_r ul").empty();
		$("#screen_city .screen_content_r ul").empty();
		$("#area_num").val("全部");
		$("#area_num").attr("data-areacode","");
		$("#brand_num").val("全部");
		$("#brand_num").attr("data-brandcode","");
		$("#city_num").val("全部");
		$("#city_num").attr("data-citycode","");
		$(".s_pitch span").html("0");
		getstorelist(shop_num);
	}
	if(r_code=="R4000"){
		isscroll=false;
	    $("#screen_area .screen_content_l").unbind("scroll");
		whir.loading.add("",0.5);
		$("#area_search").val("");
		$("#loading").remove();
		$("#screen_area").show();
		$("#screen_area").css({"position":"fixed"});
		$("#screen_shop").hide();
		area_num=1;
		$("#screen_area .screen_content_l ul").empty();
		$("#screen_area .screen_content_r ul").empty();
		$("#screen_shop .screen_content_l ul").empty();
		$("#screen_shop .screen_content_r ul").empty();
		$("#screen_brand .screen_content_l ul").empty();
		$("#screen_brand .screen_content_r ul").empty();
		$("#screen_city .screen_content_r ul").empty();
		$(".s_pitch span").html("0");
		getarealist(area_num);
	}
	if(r_code=="R4800"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_brand").show();
		$("#brand_search").val("");
		$("#screen_brand").css({"position":"fixed"});
		$("#screen_shop").hide();
		$("#screen_area").hide();
		$("#screen_brand .screen_content_l ul").empty();
		$("#screen_brand .screen_content_r ul").empty();
		$(".s_pitch span").html("0");
		getbrandlist();
	}
	if(r_code=="R5500"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_corp").show();
		$("#corp_search").val("");
		$("#screen_corp").css({"position":"fixed"});
		$("#screen_corp .screen_content_l ul").empty();
		$("#screen_corp .screen_content_r ul").empty();
		$(".s_pitch span").html("0");
		getCoprList();
	}
	if(r_code=="R3500"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_city").show();
		$("#city_search").val("");
		$("#screen_city").css({"position":"fixed"});
		$("#screen_city .screen_content_l ul").empty();
		$("#screen_city .screen_content_r ul").empty();
		$(".s_pitch span").html("0");
		getcitylist();
	}
});
//群组为区域的时候点击店铺
$("#shop_add").click(function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	shop_num=1;
	area_num=1;
	isscroll=false;
	$("#store_search").val("");
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_area .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	$("#screen_shop .screen_content_r ul").empty();
	$("#screen_area .screen_content_l ul").empty();
	$("#screen_area .screen_content_r ul").empty();
	$("#screen_brand .screen_content_l ul").empty();
	$("#screen_brand .screen_content_r ul").empty();
	$("#screen_city .screen_content_r ul").empty();
	$("#area_num").val("全部");
	$("#area_num").attr("data-areacode","");
	$("#brand_num").val("全部");
	$("#brand_num").attr("data-brandcode","");
	$("#city_num").val("全部");
	$("#city_num").attr("data-citycode","");
	$(".s_pitch span").html("0");
	whir.loading.add("",0.5);
	$("#loading").remove();
	if(r_code=="R4000"){
		$("#screen_shop").show();
		$("#screen_shop").css({"position":"fixed"});
		$("#screen_area").hide();
		getstorelist(shop_num);
	}
	if(r_code=="R4800"){
		$("#screen_area").show();
		$("#screen_area").css({"position":"fixed"});
		$("#screen_shop").hide();
		getarealist(area_num);
	}
});
//点击同步显示列表内容
$("#more_down").on("click","#synchronization",function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	whir.loading.add("",0.5);
	$("#loading").remove();
	$("#screen_shop").show();
	$("#screen_shop").css({"position":"fixed"});
	$("#screen_area").hide();
	getstorelist(shop_num);
	$(".input_search input").val("");
	shop_num=1;
	isscroll=false;
})
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
})
//点击店铺的区域
$("#shop_area").click(function(){
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	$("#area_search").val("");
	$("#screen_shop").hide();
	$("#screen_area").show();
	$("#screen_area").css({"position":"fixed"});
	area_num=1;
	$("#screen_area .screen_content_l ul").empty();
	$("#area_search").val("");
	if(r_code=="R4000"){
		$("#screen_area").attr("type","1");
	}
	isscroll=false;
	$("#screen_area .screen_content_l").unbind("scroll");
	getarealist(area_num);
})
//点击店铺的品牌
$("#shop_brand").click(function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	$("#brand_search").val("");
	$("#screen_shop").hide();
	$("#screen_brand").show();
	$("#screen_brand").css({"position":"fixed"});
	$("#screen_brand .screen_content_l ul").empty();
	// $("#screen_brand .screen_content_r ul").empty();
	getbrandlist();
})
//点击店铺的城市
$("#shop_city").click(function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	$("#city_search").val("");
	$("#screen_shop").hide();
	$("#screen_city").show();
	$("#screen_city").css({"position":"fixed"});
	$("#screen_city .screen_content_l ul").empty();
	getcitylist();
})
//移到右边
function removeRight(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li:visible");
	}
	if(li.length=="0"){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
		return;
	}
	if(li.length>0){
		for(var i=0;i<li.length;i++){
			var html=$(li[i]).html();
			var id=$(li[i]).find("input[type='checkbox']").val();
			$(li[i]).find("input[type='checkbox']")[0].checked=true;
			var input=$(b).parents(".screen_content").find(".screen_content_r li");
			for(var j=0;j<input.length;j++){
				if($(input[j]).attr("id")==id){
					$(input[j]).remove();
				}
			}
			$(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
			$(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//移到左边
function removeLeft(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li:visible");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li:visible");
	}
	if(li.length=="0"){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
		return;
	}
	if(li.length>0){
		for(var i=li.length-1;i>=0;i--){
			$(li[i]).remove();
			$(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//点击右移
$(".shift_right").click(function(){
	var right="only";
	var div=$(this);
	removeRight(right,div);
})
//点击右移全部
$(".shift_right_all").click(function(){
	var right="all";
	var div=$(this);
	removeRight(right,div);
})
//点击左移
$(".shift_left").click(function(){
	var left="only";
	var div=$(this);
	removeLeft(left,div);
})
//点击左移全部
$(".shift_left_all").click(function(){
	var left="all";
	var div=$(this);
	removeLeft(left,div);
})
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
    	isscroll=false;
	    $("#screen_area .screen_content_l").unbind("scroll");
    	$("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function(){
	var event=window.event||arguments[0];
	shop_num=1;
	if(event.keyCode==13){
		isscroll=false;
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_shop .screen_content_l ul").empty();
		getstorelist(shop_num);
	}
})
//品牌搜索
$("#brand_search").keydown(function(){
	var event=window.event||arguments[0];
	if(event.keyCode==13){
		$("#screen_brand .screen_content_l ul").empty();
		getbrandlist();
	}
})
//城市搜索
$("#city_search").keydown(function(){
	var event=window.event||arguments[0];
	if(event.keyCode==13){
		$("#screen_city .screen_content_l ul").empty();
		getcitylist();
	}
})
//店铺放大镜搜索
$("#store_search_f").click(function(){
	shop_num=1;
	isscroll=false;
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function(){
	area_num=1;
	isscroll=false;
	$("#screen_area .screen_content_l").unbind("scroll");
	$("#screen_area .screen_content_l ul").empty();
	getarealist(area_num);
})
//品牌放大镜搜索
$("#brand_search_f").click(function(){
	$("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
})
//城市放大镜搜索
$("#city_search_f").click(function(){
	$("#screen_city .screen_content_l ul").empty();
	getcitylist();
})
$("#corp_search").keydown(function(){
	var event=window.event||arguments[0];
	if(event.keyCode == 13){
		var value=$(this).val();
		$("#screen_corp .screen_content_l li").hide();
		$("#screen_corp .screen_content_l li .p16:contains('"+value+"')").parent().show();
		$("#screen_corp .screen_content_l li:visible:odd").css("backgroundColor","#fff");
		$("#screen_corp .screen_content_l li:visible:even").css("backgroundColor","#ededed");
	}
})
$("#corp_search_f").click(function(){
	var value=$("#corp_search").val();
	$("#screen_corp .screen_content_l li").hide();
	$("#screen_corp .screen_content_l li .p16:contains('"+value+"')").parent().show();
	$("#screen_corp .screen_content_l li:visible:odd").css("backgroundColor","#fff");
	$("#screen_corp .screen_content_l li:visible:even").css("backgroundColor","#ededed");
});
//区域关闭
$("#screen_close_area").click(function(){
	$("#screen_area").hide();
	whir.loading.remove();//移除遮罩层
})
//店铺关闭
$("#screen_close_shop").click(function(){
	$("#screen_area").attr("type","");
	$("#screen_shop").hide();
	whir.loading.remove();//移除遮罩层
	$("#screen_shop .screen_content_l").unbind("scroll");
})
//品牌关闭
$("#screen_close_brand").click(function(){
	$("#screen_brand").hide();
	whir.loading.remove();//移除遮罩层
})
//企业关闭
$("#screen_close_corp").click(function(){
	$("#screen_corp").hide();
	whir.loading.remove();//移除遮罩层
})
//城市关闭
$("#screen_close_city").click(function(){
	$("#screen_city").hide();
	whir.loading.remove();//移除遮罩层
})
//点击区域确定追加节点
$("#screen_que_area").click(function(){
	var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
	var type=$("#screen_area").attr("type");
	var area_codes="";
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	for(var i=0;i<li.length;i++){
		var a=$('#all_type .xingming input');
		var b=$("#shop .xingming input");
		var r=$(li[i]).attr("id");
        if(i<li.length-1){
            area_codes+=r+",";
        }else{
            area_codes+=r;
        }
        if(r_code=="R4000"&&type!="1"){
        	for(var j=0;j<a.length;j++){
				if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
					$(a[j]).parent("p").remove();
				}
		    }
			$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");
		}
		if(r_code=="R4800"){
			for(var c=0;c<b.length;c++){
				if($(b[c]).attr("data-code")==$(li[i]).attr("id")){
					$(b[c]).parent("p").remove();
				}
			}
			$('#shop .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");	
		};
	}
	if(r_code=="R2000"||r_code=="R3000"||(r_code=="R4000"&&type=="1")){
		$("#area_num").attr("data-areacode",area_codes);
		var arr=whir.loading.getPageSize();
		var left=(arr[0]-$("#screen_shop").width())/2;
		var tp=(arr[3]-$("#screen_shop").height())/2+50;
		$("#screen_area").attr("type","");
		$("#screen_shop").css({"position":"fixed"});
		$("#screen_area").hide();
		$("#screen_shop").show();
		var num=$("#screen_area .screen_content_r input[type='checkbox']").parents("li").length;
		$("#area_num").val("已选"+num+"个");
		var shop_num=1;
		isscroll=false;
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_shop .screen_content_l ul").empty();
	    // $("#screen_shop .screen_content_r ul").empty();
		getstorelist(shop_num);
	}
	if(r_code=="R4000"&&type!="1"){
		$("#screen_area").hide();
		whir.loading.remove();//移除遮罩层
	}
	if(r_code=="R4800"){
		$("#screen_area").hide();
		whir.loading.remove();//移除遮罩层
	}
	if(r_code==undefined){
		$("#area_num").attr("data-areacode",area_codes);
		var arr=whir.loading.getPageSize();
		var left=(arr[0]-$("#screen_shop").width())/2;
		var tp=(arr[3]-$("#screen_shop").height())/2+50;
		$("#screen_shop").css({"position":"fixed"});
		$("#screen_area").hide();
		$("#screen_shop").show();
		var num=$("#screen_area .screen_content_r input[type='checkbox']").parents("li").length;
		$("#area_num").val("已选"+num+"个");
		var shop_num=1;
		isscroll=false;
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_shop .screen_content_l ul").empty();
	    // $("#screen_shop .screen_content_r ul").empty();
		getstorelist(shop_num);
	}
})
//点击店铺确定追加节点
$("#screen_que_shop").click(function(){
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
	for(var i=0;i<li.length;i++){
		var a=$('#all_type .xingming input');
		var b=$("#shop .xingming input");
		if(r_code!=="R4000"&&r_code!==undefined){
			for(var j=0;j<a.length;j++){
				if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
					$(a[j]).parent("p").remove();
				}
		    }
			$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");	
		};
		if(r_code=="R4000"){
			for(var c=0;c<b.length;c++){
				if($(b[c]).attr("data-code")==$(li[i]).attr("id")){
					$(b[c]).parent("p").remove();
				}
			}
			$('#shop .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");	
		};
	}
	if(r_code==undefined){
		var store_codes="";
		var _param={};
		for(var i=0;i<li.length;i++){
			var r=$(li[i]).attr("id");
	        if(i<li.length-1){
	            store_codes+=r+",";
	        }else{
	            store_codes+=r;
	        }
		}
		_param["store_code"]=store_codes;
		if(store_codes==""){
			art.dialog({
				zIndex:10003,
				time: 1,
				lock: true,
				cancel: false,
				content:"店铺不能为空"
			});
			return;
		}
		whir.loading.add("",0.5);
		oc.postRequire("post","/user/synchronization","", _param, function(data) {
			if(data.code=="0"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}else if(data.code=="-1"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
			whir.loading.remove();//移除遮罩层
		})
	}
	$("#screen_shop").hide();
	whir.loading.remove();//移除遮罩层
})
//点击同步员工按钮
$("#screen_que_staff").click(function(){
	var _param={};
	var store_codes="";
	_param["store_code"]=store_codes;
	whir.loading.add("",0.5);
	oc.postRequire("post","/user/synchronization","", _param, function(data) {
			if(data.code=="0"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}else if(data.code=="-1"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
			whir.loading.remove();//移除遮罩层
	});
	$("#screen_shop").hide();
	whir.loading.remove();//移除遮罩层
})
//点击品牌确定追加节点
$("#screen_que_brand").click(function(){
	var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	if(r_code=="R4800"){
		for(var i=0;i<li.length;i++){
			var a=$('#all_type .xingming input');
			for(var j=0;j<a.length;j++){
				if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
					$(a[j]).parent("p").remove();
				}
			}
			$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");
	    }
	    $("#screen_brand").hide();
	    whir.loading.remove();//移除遮罩层
	    return;
	}
	var brand_codes="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
        if(i<li.length-1){
            brand_codes+=r+",";
        }else{
            brand_codes+=r;
        }
	}
	$("#brand_num").attr("data-brandcode",brand_codes);
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[3]-$("#screen_shop").height())/2+50;
	$("#screen_shop").css({"position":"fixed"});
	$("#screen_brand").hide();
	$("#screen_shop").show();
	var num=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li").length;
	$("#brand_num").val("已选"+num+"个");
	var shop_num=1;
	isscroll=false;
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	// $("#screen_shop .screen_content_r ul").empty();
	getstorelist(shop_num);
})
//点击区域的城市的确定追加节点
$("#screen_que_city").click(function(){
	var li=$("#screen_city .screen_content_r input[type='checkbox']").parents("li");
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	if(r_code=="R3500"){
		for(var i=0;i<li.length;i++){
			var a=$('#all_type .xingming input');
			for(var j=0;j<a.length;j++){
				if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
					$(a[j]).parent("p").remove();
				}
			}
			$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");
		}
		$("#screen_city").hide();
		whir.loading.remove();//移除遮罩层
		return;
	}
	var city_codes="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
        if(i<li.length-1){
            city_codes+=r+",";
        }else{
            city_codes+=r;
        }
	}
	$("#city_num").attr("data-citycode",city_codes);
	var num=$("#screen_city .screen_content_r input[type='checkbox']").parents("li").length;
	$("#city_num").val("已选"+num+"个");
	$("#screen_city").hide();
	$("#screen_shop").show();
	var shop_num=1;
	isscroll=false;
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	getstorelist(shop_num);
	whir.loading.remove();//移除遮罩层
})
//企业点击确定
$("#screen_que_corp").click(function(){
	var li=$("#screen_corp .screen_content_r input[type='checkbox']").parents("li");
	for(var i=0;i<li.length;i++){
		var a=$('#all_type .xingming input');
		var b=$("#shop .xingming input");
		for(var j=0;j<a.length;j++){
			if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
				$(a[j]).parent("p").remove();
			}
		}
		$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find(".p16").html()+"'><span class='power remove_app_id'>删除</span></p>");
	}
	$("#screen_corp").hide();
	whir.loading.remove();//移除遮罩层
})
//删除
$(".xingming").on("click",".remove_app_id",function(){
	$(this).parent().remove();
})
//拉取企业
function getCoprList(){
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var corps=JSON.parse(data.message).corps;
			var html="";
			for (var i = 0; i < corps.length; i++) {
				html+="<li><div class='checkbox1'><input  type='checkbox' value='"+corps[i].corp_code+"' data-storename='"+corps[i].corp_name+"' name='test'  class='check'  id='checkboxTowInput"
					+ i
					+ 1
					+ "'/><label for='checkboxTowInput"
					+ i
					+ 1
					+ "'></label></div><span class='p16'>"+corps[i].corp_name+"</span></li>"
			}
			$("#screen_corp .screen_content_l ul").html(html);
		}else  if(data.code=="-1"){
			alert(data.message);
		}
	});
}
//拉取区域
function getarealist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_command = "/area/selAreaByCorpCode";
	var searchValue=$("#area_search").val().trim();
	var pageSize=20;
	var pageNumber=a;
	var _param = {};
	if(corp_code==undefined){
		_param['corp_code']="C10000";
	}else{
		_param['corp_code']=corp_code;
	}
	_param["searchValue"]=searchValue;
	_param["pageSize"]=pageSize;
	_param["pageNumber"]=pageNumber;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
			var area_html_left = '';
			if (list.length == 0) {
			} else {
				if(list.length>0){
					for (var i = 0; i < list.length; i++) {
					    area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'/><label for='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
					}
				}
			}
			if(hasNextPage==true){
				area_num++;
				area_next=false;
			}
			if(hasNextPage==false){
				area_next=true;
			}
			$("#screen_area .screen_content_l ul").append(area_html_left);
			if(!isscroll){
				$("#screen_area .screen_content_l").bind("scroll",function () {
					var nScrollHight = $(this)[0].scrollHeight;
				    var nScrollTop = $(this)[0].scrollTop;
				    var nDivHight=$(this).height();
				    console.log(nScrollHight);
				    console.log(nScrollTop);
				    console.log(nDivHight);
				    if(nScrollTop + nDivHight >= nScrollHight){
				    	if(area_next){
				    		return;
				    	}
				    	getarealist(area_num);
				    };
				})
			}
			isscroll=true;
			var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
			whir.loading.remove();//移除加载框
		} else if (data.code == "-1") {
			art.dialog({
				zIndex:10003,
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	})
}
//获取店铺列表
function getstorelist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_code =$('#area_num').attr("data-areacode");//区域编号
	var brand_code=$('#brand_num').attr("data-brandcode");//品牌编号
	var city=$('#city_num').attr("data-citycode");//城市编号
	var searchValue=$("#store_search").val();
	var pageSize=20;
	var pageNumber=a;
	var _param={};
	if(corp_code==undefined){
		_param['corp_code']="C10000";
	}else{
		_param['corp_code']=corp_code;
	}
	_param['area_code']=area_code;
	_param['brand_code']=brand_code;
	_param['city']=city;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	// oc.postRequire("post","/user/stores","", _param, function(data) {
	oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
	    if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
			var store_html = '';
			if (list.length == 0){
				if(a==1){
					for(var h=0;h<9;h++){
						store_html+="<li></li>";
					}
				}
				shop_next=true;
			} else {
				if(list.length>0){
					for (var i = 0; i < list.length; i++) {
				    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
					}
				}
			}
			if(hasNextPage==true){
				shop_num++;
				shop_next=false;
			}
			if(hasNextPage==false){
				shop_next=true;
			}
			$("#screen_shop .screen_content_l ul").append(store_html);
			if(!isscroll){
				$("#screen_shop .screen_content_l").bind("scroll",function () {
					var nScrollHight = $(this)[0].scrollHeight;
				    var nScrollTop = $(this)[0].scrollTop;
				    var nDivHight=$(this).height();
				    if(nScrollTop + nDivHight >= nScrollHight){
				    	if(shop_next){
				    		return;
				    	}
				    	getstorelist(shop_num);
				    };
				})
		    }
		    isscroll=true;
			var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
			whir.loading.remove();//移除加载框
		} else if (data.code == "-1") {
			art.dialog({
				zIndex:10003,
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	})
}
//获取品牌列表
function getbrandlist(){
	var corp_code = $('#OWN_CORP').val();
	var searchValue=$("#brand_search").val();
	var _param={};
	if(corp_code==undefined){
		_param['corp_code']="C10000";
	}else{
		_param['corp_code']=corp_code;
	}
	_param["searchValue"]=searchValue;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	oc.postRequire("post","/shop/brand", "",_param, function(data){
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=message.brands;
			var brand_html_left = '';
			var brand_html_right='';
			if (list.length == 0){
				for(var h=0;h<9;h++){
					brand_html_left+="<li></li>"
				}
			} else if(list.length>0){
				for (var i = 0; i < list.length; i++) {
					brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
	                + i
	                + 1
	                + "'/><label for='checkboxThreeInput"
	                + i
	                + 1
	                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
				}
			}
			$("#screen_brand .screen_content_l ul").append(brand_html_left);
			var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
			whir.loading.remove();//移除加载框
		} else if (data.code == "-1") {
			art.dialog({
				zIndex:10003,
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	})
};
//获取城市列表
function getcitylist(){
	var corp_code = $('#OWN_CORP').val();
	var searchValue=$("#city_search").val();
	var _param={};
	if(corp_code==undefined){
		_param['corp_code']="C10000";
	}else{
		_param['corp_code']=corp_code;
	}
	_param["search_value"]=searchValue;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	oc.postRequire("post","/shop/getCorpCity", "",_param, function(data){
		var message=JSON.parse(data.message);
		var list=JSON.parse(message.list);
		var html="";
		for(var i=0;i<list.length;i++){
			html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].city+"' name='test'  class='check'"
	            + "'/><label"
	            + "></label></div><span class='p16'>"+list[i].city+"</span></li>"
		}
		$("#screen_city .screen_content_l ul").append(html);
		var li=$("#screen_city .screen_content_r input[type='checkbox']").parents("li");
		for(var k=0;k<li.length;k++){
			$("#screen_city .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
		}
		whir.loading.remove();
	})
};
