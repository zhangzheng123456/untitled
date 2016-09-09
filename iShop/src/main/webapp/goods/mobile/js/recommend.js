jQuery(function(){
	var url = location.search; //获取url中"?"符后的字串   
        if (url.indexOf("?") != -1) {   
	        var str = url.substr(1);   
	        strs = str.split("=")[1];
		}
	var corp_code=strs;
	console.log(corp_code);
	var oc = new ObjectControl();
	var rowno=0;
	var value="";
	//页面加载循环
	function superaddition(list){
		for (var i = 0; i < list.length; i++) {
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
		getSearchList(rowno);
	});
	//搜索加载list
	function getSearchList(a){
		var param={};
		param["rowno"]=a;
		param["corp_code"]=corp_code;
		param["search_value"]=value;
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
			if(list.length<0){
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
        if(value==""){
        	getList(rowno);
        }else if(value!==""){
        	getSearchList(rowno);
        }
    	
    })
});

