jQuery(function(){
	function GetRequest() {
        var url = decodeURI(location.search); //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for (var i = 0; i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
            }
        }
        return theRequest;
    }
    var a = GetRequest();
    var corp_code = a.corp_code;
    var user_id=a.user_id;
	var oc = new ObjectControl();
	var param={};
	var rowno=0;
	var value="";
	var brand_code="";
	var goods_quarter="";
	var goods_wave="";
	var filtrate="";
	function getScreen(){
		var param={};
		param["corp_code"]=corp_code;
		param["user_id"]=user_id;
		oc.postRequire("post","/api/fab/screenValue","0",param,function(data){
			var message=JSON.parse(data.message);
			var waves=JSON.parse(message.waves);
			var brands=JSON.parse(message.brands);
			var quarters=JSON.parse(message.quarters);
			var waves_html="";
			for(var i=0;i<waves.length;i++){
				waves_html+="<li>"+waves[i].goods_wave+"</li>"
			}
			$("#waves .list ul").html(waves_html);
			var brands_html="";
			for(var i=0;i<brands.length;i++){
				brands_html+="<li data-code='"+brands[i].brand_code+"'>"+brands[i].brand_name+"</li>"
			}
			$("#brand .list ul").html(brands_html);
			var quarters_html="";
			for(var i=0;i<quarters.length;i++){
				quarters_html+="<li>"+quarters[i].goods_quarter+"</li>"
			}
			$("#goods_quarter .list ul").html(quarters_html);
		})//获取筛选的字段
	}
	getScreen();
	$(".screen_content .list").on("click","ul li",function(e){
		e.stopPropagation();
		$(this).toggleClass("active");
	})
	$("#complete").click(function(){
		$("#guide").animate({right:"-"+$("#guide").width()+"px"},300);
		$("#cover").hide();
		$("html").removeClass("sift-move");
		$("body").removeClass("sift-move");
		var brand=$("#brand .list .active");
		var quarter=$("#goods_quarter .list .active");
		var wave=$("#waves .list .active");
		brand_code="";
	    goods_quarter="";
	    goods_wave="";
		for(var i=0;i<brand.length;i++){
			if(i<brand.length-1){
				brand_code+=$(brand[i]).attr("data-code")+",";
			}else {
				brand_code+=$(brand[i]).attr("data-code")
			}
		}
		for(var i=0;i<quarter.length;i++){
			if(i<quarter.length-1){
				goods_quarter+=$(quarter[i]).html()+",";
			}else {
				goods_quarter+=$(quarter[i]).html();
			}
		}
		for(var i=0;i<wave.length;i++){
			if(i<wave.length-1){
				goods_wave+=$(wave[i]).html()+",";
			}else {
				goods_wave+=$(wave[i]).html();
			}
		}
		param["brand_code"]=brand_code;
		param["goods_quarter"]=goods_quarter;
		param["goods_wave"]=goods_wave;
		param["search_value"]="";
		filtrate="sucess";
		jQuery('#input').val("");
		jQuery('.allShops').empty();
		getSearchList(rowno);
		var osType = getWebOSType();
		if(osType == "Android"){
			if($(window).scrollTop()>0){
				iShop.jumpToWebViewForScroll("N");
			}else {
				iShop.jumpToWebViewForScroll("Y");
			}
		}
	})
	$("#reset").click(function(){
		$(".screen_content .list li").attr("class","");
	})
	//页面加载循环
	function superaddition(list){
		console.log(list);
		for (var i = 0; i < list.length; i++){
			var goods_image="";
			var standard="";
			if(list[i].standard!==""){
				standard=JSON.parse(list[i].standard).product_detail[0].PRICE_SUG;
			}
	        if(list[i].goods_image.indexOf("http")!==-1){
				if(list[i].goods_image.indexOf("products-image.oss-cn-hangzhou.aliyuncs.com")!==-1){
					goods_image=list[i].goods_image+"@100h_100w_1e_1c";
				}else {
					goods_image=list[i].goods_image;
				}
	        }
	        if(list[i].goods_image.indexOf("http")==-1){
	            goods_image="image/goods_default_image.png";
	        }
	        var price="";
	        if(list[i].goods_price==""){
				if(standard==""){
					price="0";
				}
	        	if(standard!==""){
					price=standard;
				}
	        }
	        if(list[i].goods_price!==""){
	        	price=list[i].goods_price;
	        }
			jQuery('.allShops').append('<div class="shop" data-id="'+list[i].id+'"><a href="javascript:void(0);"><div class="img"><img src="' 
			+ goods_image
			+ '"></div><div class="shop-t"><h1>' 
			+ list[i].goods_name + '</h1><p>货号:' 
			+ list[i].goods_code + '</p><p>销量:'+list[i].num_sales+'<span style="margin-left: 15px;">库存:'+list[i].num_stocks+'</span></p><p class="pice">价格:<span>￥'
			+ price + '</span></p></div></a></div>');
		}
		sessionStorage.removeItem("return_jump");
	}
	//刚进页面的list请求
	function getList(a){
		var param={};
		param["rowno"]=a;
		param["corp_code"]=corp_code;
		param["user_id"]=user_id;
		$("#loading").show();
		oc.postRequire("post","/api/fab/","0",param,function(data){
			$("#loading").hide();
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            cout=list.pages;
            var list=list.list;
            if(list.length==0){
            	$("#kong_img").show();
            }else{
            	$("#kong_img").hide();
            }
            superaddition(list);
            if(hasNextPage==true){
            	$(".more").show();
            }
            if(hasNextPage==false){
            	$(".more").hide();
            }
			$("#input").val("");
		})
	}
	getList(rowno);
	// //input输入框里面
	// $('#input').bind('input propertychange', function() {
	//     var thatFun=arguments.callee;
     //    var that=this;
     //    $(this).unbind("input propertychange",thatFun);
	// 	value=$('#input').val().replace(/\s+/g,"");
	// 	if(value==""){
	// 		jQuery('.allShops').empty();
	// 		jQuery(".more").hide();
	// 		getList(rowno);
	// 	}
	// 	setTimeout(function(){$(that).bind("input propertychange",thatFun)},0);
	// });
	$("#input").keydown(function() {
	    var event=window.event||arguments[0];
	    if(event.keyCode == 13){
	    	value=$('#input').val().trim();
	    	// if(value!==""){
	    		$(".screen_content .list li").attr("class","");
				param["search_value"]=value;
				param["brand_code"]="";
				param["goods_quarter"]="";
				param["goods_wave"]="";
				jQuery('.allShops').empty();
				getSearchList(rowno);
	    	// }
	    }
    });
	//搜索加载list
	function getSearchList(a){
		param["rowno"]=a;
		param["corp_code"]=corp_code;
		param["user_id"]=user_id;
		$("#kong_img").hide();
		$(".more").hide();
		$("#loading").show();
		oc.postRequire("post","/api/fab/search","0",param,function(data){
			$("#loading").hide();
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            cout=list.pages;
            var list=list.list;
            if(hasNextPage==true){
            	$(".more").show();
            }
            if(hasNextPage==false){
            	$(".more").hide();
            }
			if(list.length<=0){
				$("#kong_img").show();
			}
			if(list.length>0){
				$("#kong_img").hide();
				superaddition(list);
			}
		})
	}
    //点击加载更多
    $(".more").click(function(){
        var rowno=$('.allShops .shop').length;
        if(value==""&&filtrate==""){
        	getList(rowno);
        }else if(value!==""||filtrate!==""){
        	getSearchList(rowno);
        }
    	
    })
	$("#screen").click(function(){
		console.log(123123);
		$("#guide").animate({right:'0'},300);
		$("#cover").show();
		$("html").addClass("sift-move");
		$("body").addClass("sift-move");
		var osType = getWebOSType();
		if(osType == "Android"){
			iShop.jumpToWebViewForScroll("N");
		}
	});
	$("#cover").click(function(){
		$("#guide").animate({right:"-"+$("#guide").width()+"px"},300);
		$("#cover").hide();
		$("html").removeClass("sift-move");
		$("body").removeClass("sift-move");
		var osType = getWebOSType();
		if(osType == "Android"){
			if($(window).scrollTop()>0){
				iShop.jumpToWebViewForScroll("N");
			}else {
				iShop.jumpToWebViewForScroll("Y");
			}
		}
	})
    $(".allShops").on("click",".shop",function(e){
    	var host=window.location.host;
    	var id=$(this).attr("data-id");
 		var param={};
 		param["type"]="FAB";
 		param["url"]="http://"+host+"/goods/mobile_v2/goods.html?corp_code="+corp_code+"&id="+id+"&type=app&user_id="+user_id+"";
 		param["title"]=$(this).find(".shop-t h1").html();
    	doAppWebRefresh(param);
	})
	//获取手机系统
	function getWebOSType(){
		var browser = navigator.userAgent;
		var isAndroid = browser.indexOf('Android') > -1 || browser.indexOf('Adr') > -1; //android终端
		var isiOS = !!browser.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
		if(isAndroid){
			return "Android";
		}else if (isiOS) {
			return "iOS";
		}else{
			return "Unknown"
		}
	}
	//获取iShop用户信息
	function getAppUserInfo(){
		var osType = getWebOSType();
		var userInfo = null;
		if(osType=="iOS"){
			userInfo = NSReturnUserInfo();
		}else if(osType == "Android"){
			userInfo = iShop.ReturnUserInfo();
		}
		return userInfo;
	}
	//调用APP方法传参 param 格式 type：** ;url:**
	function doAppWebRefresh(param){
		var param=JSON.stringify(param);
		var osType = getWebOSType();
		if(osType=="iOS"){
			try{
                window.webkit.messageHandlers.NSJumpToWebViewForWeb.postMessage(param);
            } catch(err){
                NSJumpToWebViewForWeb(param);
            }
		}else if(osType == "Android"){
			iShop.jumpToWebViewForWeb(param);
		}
	}
});
function doAppWebHeaderRefresh(param){
	if(param=="headerRefresh"){
		$(".header_line").css({"position":"absolute"});
	}else{
		$(".header_line").css({"position":"fixed"});
	}
}
$(window).scroll(function(){
	var osType = getWebOSType();
	if(osType == "Android"){
		if($(this).scrollTop()>0){
			iShop.jumpToWebViewForScroll("N");
		}else {
			iShop.jumpToWebViewForScroll("Y");
		}
	}
})

