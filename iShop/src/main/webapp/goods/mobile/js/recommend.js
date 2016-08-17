jQuery(function(){
	var url = location.search; //获取url中"?"符后的字串   
        if (url.indexOf("?") != -1) {   
	        var str = url.substr(1);   
	        strs = str.split("=")[1];
		}

	var hasNextPage="";
	var is
	var corp_code=strs;
	console.log(corp_code);
	var oc = new ObjectControl();
	var rowno=0;
	var key;
	var param = {
        "rowno":rowno,
        "corp_code":corp_code,
        "key":key
	};
	if(corp_code!=""){
		oc.postRequire("post","/api/fab","0",param,function(data){
			console.log(data);
			var list=JSON.parse(data.message);
			var list=JSON.parse(list.list);
			counts=list.pages;
			hasNextPage=list.hasNextPage;
			var list=list.list;
			console.log(list);
			for(var i=0;i<list.length;i++){
				jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code='+corp_code+'&id='+list[i].id+'"><div class="img"><img src="'+list[i].goods_image+'"></div><div class="shop-t"><h1>'+list[i].goods_name+'</h1><p>货号:'+list[i].goods_code+'</p><p class="pice">价格:<span>￥'+list[i].goods_price+'</span></p></div></a></div>');
			}
			morehide();
			morelist();
		});

		function searCh(){
	        query = {
		        "rowno":rowno,
		        "corp_code":corp_code,
		        "search_value":key
		    };
		    console.log(rowno);
		    oc.postRequire("post","/api/fab/search","0",query,function(data){
		    	console.log(data);
				var list=JSON.parse(data.message);
				var list=JSON.parse(list.list);
				hasNextPage=list.hasNextPage;
				var list=list.list;
		    	console.log(list);
		    	if(list.length>=1){
		    		jQuery('.allShops').empty();
		    		for(var i=0;i<list.length;i++){
				    		jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code='+corp_code+'&id='+list[i].id+'"><div class="img"><img src="'+list[i].goods_image+'"></div><div class="shop-t"><h1>'+list[i].goods_name+'</h1><p>货号:'+list[i].goods_code+'</p><p class="pice">价格:<span>￥'+list[i].goods_price+'</span></p></div></a></div>');
			        }
					morehide();
					if(hasNextPage==true){
							$(".more").show();
						    $(".more").click(function() {
							var rowno = jQuery('.allShops .shop').length+1;
							param = {
								"rowno": rowno,
								"corp_code": corp_code,
								"search_value": key
							}
							oc.postRequire("post", "/api/fab/search", "0", param, function (data) {
								console.log(data);
								var list = JSON.parse(data.message);
								var list = JSON.parse(list.list);
								hasNextPage=list.hasNextPage;
								var list = list.list;
								for (var i = 0; i < list.length; i++) {
									jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code=' + corp_code + '&goods_price=' + list[i].goods_code + '"><div class="img"><img src="' + list[i].goods_image + '"></div><div class="shop-t"><h1>' + list[i].goods_name + '</h1><p>货号:' + list[i].goods_code + '</p><p class="pice">价格:<span>￥' + list[i].goods_price + '</span></p></div></a></div>');
								}
								morehide();
							})
						})

					}
		    	}else if(list.length==0){
		    		jQuery('.allShops').empty();
		    		jQuery(".allShops").append("<p>没有收索结果</p><p class='p'>没有找到相关宝贝<p/>")
					morehide();
		    	}else if(key==""){
		    		jQuery('.allShops').empty();
					jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code='+corp_code+'&id='+list[i].id+'"><div class="img"><img src="'+list[i].goods_image+'"></div><div class="shop-t"><h1>'+list[i].goods_name+'</h1><p>货号:'+list[i].goods_code+'</p><p class="pice">价格:<span>￥'+list[i].goods_price+'</span></p></div></a></div>');
					morehide();
					morelist();
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
        // $('#input').bind('input propertychange', function() {
			// key=$('#input').val().replace(/\s+/g,"");
			// console.log(key);
			// 	searCh();
	    // });

		$("#d_search").click(function () {
			key=$('#input').val().replace(/\s+/g,"");
			searCh();
		})

		function morehide() {
			var shop_len=$(".shop").length;
			console.log(hasNextPage);
			if(hasNextPage==false){
				$(".more").hide();
			}
		}

		function morelist() {
			if(hasNextPage==true){
				$(".more").show();
				$(".more").click(function() {
				  if($("#input").val()=="") {
					  var rowno = jQuery('.allShops .shop').length + 1;
					  query = {
						  "rowno": rowno,
						  "corp_code": corp_code,
						  "key": key
					  }
					  console.log(rowno);
					  console.log(key);
					  oc.postRequire("post", "/api/fab", "", query, function (data) {
						  console.log(data);
						  var list = JSON.parse(data.message);
						  var list = JSON.parse(list.list);
						  counts = list.pages;
						  hasNextPage = list.hasNextPage;
						  var list = list.list;
						  for (var i = 0; i < list.length; i++) {
							  jQuery('.allShops').append('<div class="shop"><a href="goods.html?corp_code=' + corp_code + '&goods_price=' + list[i].goods_code + '"><div class="img"><img src="' + list[i].goods_image + '"></div><div class="shop-t"><h1>' + list[i].goods_name + '</h1><p>货号:' + list[i].goods_code + '</p><p class="pice">价格:<span>￥' + list[i].goods_price + '</span></p></div></a></div>');
						  }
						  morehide();
					  })
				  }
				})

			}
		}
    }

});

