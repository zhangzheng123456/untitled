jQuery(function(){
	var url = location.search; //获取url中"?"符后的字串   
        if (url.indexOf("?") != -1) {   
	        var str = url.substr(1);   
	        strs = str.split("=")[1];
		}
	var corp_code=strs;
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
		getSearchList(rowno);
	})
	$("#reset").click(function(){
		$(".screen_content .list li").attr("class","");
	})
	//页面加载循环
	function superaddition(list){
		for (var i = 0; i < list.length; i++){
			jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code='
			+ corp_code 
			+ '&id='
			+ list[i].id
			+ '"><div class="img"><img src="' 
			+ list[i].goods_image 
			+ '"></div><div class="shop-t"><h1>' 
			+ list[i].goods_name + '</h1><p>货号:' 
			+ list[i].goods_code + '</p><p class="pice">价格:<span>￥' 
			+ list[i].goods_price + '</span></p></div></a></div>');
		}
	}
	//刚进页面的list请求
	function getList(a){
		var param={};
		param["rowno"]=a;
		param["corp_code"]=corp_code;
		oc.postRequire("post","/api/fab/","0",param,function(data){
			console.log(data);
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
	//input输入框里面
	$('#input').bind('input propertychange', function() {
		value=$('#input').val().replace(/\s+/g,"");
		$(".screen_content .list li").attr("class","");
		param["search_value"]=value;
		param["brand_code"]="";
		param["goods_quarter"]="";
		param["goods_wave"]="";
		getSearchList(rowno);
	});
	//搜索加载list
	function getSearchList(a){
		param["rowno"]=a;
		param["corp_code"]=corp_code;
		oc.postRequire("post","/api/fab/search","0",param,function(data){
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
				jQuery('.allShops').empty();
			    jQuery(".allShops").append("<p>没有收索结果</p><p class='p'>没有找到相关宝贝<p/>")
			}
			if(a<20){
				jQuery('.allShops').empty();
			}
			if(list.length>0){
				superaddition(list);
			}
		})
	}
	getList(rowno);
    //点击加载更多
    $(".more").click(function(){
        var rowno=$('.allShops .shop').length;
        if(value==""&&filtrate==""){
        	getList(rowno);
        }else if(value!==""||filtrate!==""){
        	getSearchList(rowno);
        }
    	
    })
});

