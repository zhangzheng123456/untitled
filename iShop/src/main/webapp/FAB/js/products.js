jQuery(function(){
		jQuery('#buy').click(function( ){
	        jQuery(this).css({backgroundColor:"#fff",color:"#dd6c5e"});
	        jQuery('#match').css({backgroundColor:"#dfdfdf",color:"#8d8d8d"});
	        jQuery('#content').show();
	        jQuery('#match-con').hide();
		});
		$('#match').click(function(){
	        jQuery(this).css({backgroundColor:"#fff",color:"#dd6c5e"});
	        jQuery('#buy').css({backgroundColor:"#dfdfdf",color:"#8d8d8d"});
	        jQuery('#match-con').show();
	        jQuery('#content').hide();
		});
         function GetRequest(){   
		    var url = location.search; //获取url中"?"符后的字串   
		    var theRequest = new Object();   
		    if (url.indexOf("?") != -1) {   
		    var str = url.substr(1);   
		    strs = str.split("&");   
			for(var i = 0; i < strs.length; i ++) {   
			  theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);   
				}   
			}   
		    return theRequest;   
        }
        var a=GetRequest();
        console.log(a);
        var goods_code=a.goods_price;
        var corp_code=a.corp_code;      
	    var oc = new ObjectControl();
	    var query = {
            "goods_code":goods_code,
             "corp_code":corp_code
	    };
	    var oc = new ObjectControl();
	    var query = {
            "goods_code":goods_code,
             "corp_code":corp_code
	    };
	    oc.postRequire("post","/app/fab/select","",param,function(data){
            console.log(data);
		    console.log(data[0].message);
		    var goodsLists=JSON.parse(data[0].message);
		    var goodsList=goodsLists["goodsList"];
		    console.log(goodsList);
		    console.log(goodsLists.selling_point);
		    for(var i=0;i<goodsLists.picture.length;i++){
		    	jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><img src="'+goodsLists.picture[i]+'"></div>');
		    	console.log(i);
		    }
		    document.title=goodsLists.goods_name;
		    jQuery('.detail').html('<p class="product_code">货号:'+goodsLists.goods_code+'</p><p class="pice">价格:<span>￥'+goodsLists.goods_price+'</span></p><div class="total"><p>年份:'+goodsLists.production_year+'</p><p>季度:'+goodsLists.quarter+'</p><p>波段:'+goodsLists.wave_band+'</p></div>');
		    jQuery('#content').html(goodsLists.selling_point);
            for(var i=0;i<goodsList.length;i++){
               jQuery('#match-con ul').append('<a href="goods.html?corp_code='+corp_code+'&goods_price='+goodsList[i].goods_code+'"><li><img src="'+goodsList[i].picture+'" alt="暂无图片"><p>'+goodsList[i].goods_code+'</p></li></a>');
            }
            $('#swipe').swiper({
	          pagination: '#swipe .swiper-pagination',
	          slidesPerView: 'auto',
	          paginationClickable: true,
	          spaceBetween:0,
	          autoplay:3000,
	          loop:true,
	          loopedSlides:5,
          	  autoplayDisableOnInteraction:false
           });
		})
	}); 