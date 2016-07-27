jQuery(function(){
	var url = location.search; //获取url中"?"符后的字串   
        if (url.indexOf("?") != -1) {   
	        var str = url.substr(1);   
	        strs = str.split("=")[1];
		}
	var corp_code=strs;
	console.log(corp_code);
	var oc = new ObjectControl();
	var rowno;
	var key;
	var query = {
        "rowno":rowno,
        "corp_code":corp_code,
        "key":key
	};
	if(corp_code!=""){
		oc.postRequire("post","/app/fab","0",param,function(data){
			console.log(data);
			console.log(data[0].message);
			var goodsList=JSON.parse(data[0].message)["goodsList"];
			console.log(goodsList);
			for(var i=0;i<goodsList.length;i++){
				jQuery('.allShops').append('<div class="shop"><div class="img"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><img src="'+goodsList[i].picture+'"><a/></div><div class="shop-t"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><h1>'+goodsList[i].goods_name+'</h1></a><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p>货号:'+goodsList[i].goods_code+'</p><a/><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p class="pice">价格:<span>￥'+goodsList[i].goods_price+'</span></p></a></div></div>');
			}
		});
			jQuery(window).scroll(function(){
				totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop())+150; 

				// if((jQuery(document).scrollTop())==(jQuery(document).height()-jQuery(window).height())){
				if($(document).height() <= totalheight){ 
					var rowno=jQuery('.allShops .shop').length+1;
					query={
						"rowno":rowno,
						"corp_code":corp_code,
						"key":key
					}
					console.log(rowno);
					console.log(key);
					oc.webaction("IShowGetFABCommand",query,function(data){
						console.log(data);
		                var goodsList=JSON.parse(data[0].message)["goodsList"];
		                for(var i=0;i<goodsList.length;i++){
				          jQuery('.allShops').append('<div class="shop"><div class="img"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><img src="'+goodsList[i].picture+'"><a/></div><div class="shop-t"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><h1>'+goodsList[i].goods_name+'</h1></a><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p>货号:'+goodsList[i].goods_code+'</p><a/><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p class="pice">价格:<span>￥'+goodsList[i].goods_price+'</span></p></a></div></div>');
			}
		        	})
				}
			});
		function searCh(){
	        query = {
		        "rowno":rowno,
		        "corp_code":corp_code,
		        "key":key
		    };
		    console.log(rowno);
		    oc.webaction("IShowGetFABCommand",query,function(data){
		    	console.log(data);
		    	var goodsList=JSON.parse(data[0].message)["goodsList"];
		    	console.log(goodsList);
		    	if(goodsList.length>=1){
		    		jQuery('.allShops').empty();
		    		for(var i=0;i<goodsList.length;i++){
				    		jQuery('.allShops').append('<div class="shop"><div class="img"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><img src="'+goodsList[i].picture+'"><a/></div><div class="shop-t"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><h1>'+goodsList[i].goods_name+'</h1></a><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p>货号:'+goodsList[i].goods_code+'</p><a/><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p class="pice">价格:<span>￥'+goodsList[i].goods_price+'</span></p></a></div></div>');
			}
		    	}else if(goodsList.length==0){
		    		jQuery('.allShops').empty();
		    		jQuery(".allShops").append("<p>没有收索结果</p><p class='p'>没有找到相关宝贝<p/>")
		    	}else if(key==""){
		    		jQuery('.allShops').empty();
		    			jQuery('.allShops').append('<div class="shop"><div class="img"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><img src="'+goodsList[i].picture+'"><a/></div><div class="shop-t"><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><h1>'+goodsList[i].goods_name+'</h1></a><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p>货号:'+goodsList[i].goods_code+'</p><a/><a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><p class="pice">价格:<span>￥'+goodsList[i].goods_price+'</span></p></a></div></div>');
			}
		    });
		  console.log(key); 
		}
		jQuery(document).keydown(function(event){
			var event=window.event||arguments[0];
			var code=event.keyCode;
			if(code==13){
	            key=$('#input').val().replace(/\s+/g,"");
	            if(key!=""){
				searCh();}}
		})
		$('#input').bind('input propertychange', function() {
			key=$('#input').val().replace(/\s+/g,"");
			console.log(key);
				searCh();
	    });
    }
});