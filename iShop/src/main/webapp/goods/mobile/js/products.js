jQuery(function () {
    jQuery('#buy').click(function () {
        jQuery(this).css({backgroundColor: "#fff", color: "#dd6c5e"});
        jQuery('#match').css({backgroundColor: "#dfdfdf", color: "#8d8d8d"});
        jQuery('#content').show();
        jQuery('#match-con').hide();
    });
    $('#match').click(function () {
        jQuery('#match-con').show();
        jQuery('#content').hide();
        var h=$('.ti_img_wrap').width();
        $('.ti_img').css({"height":+h+"px"});
        jQuery(this).css({backgroundColor: "#fff", color: "#dd6c5e"});
        jQuery('#buy').css({backgroundColor: "#dfdfdf", color: "#8d8d8d"});
    });
    function GetRequest(){
        var url = location.search; //获取url中"?"符后的字串
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
    var id = a.id;
    var corp_code = a.corp_code;
    var oc = new ObjectControl();
    var query = {
        "id": id,
        "corp_code": corp_code
    };
    var oc = new ObjectControl();
    var query = {
        "id": id,
        "corp_code": corp_code
    };
    oc.postRequire("post", "/api/fab/select", "", query, function (data) {
        var list = JSON.parse(data.message);
        var list = JSON.parse(list.goods);
        var goods_image=JSON.parse(list.goods_image);
        // if(list.goods_image.indexOf("http")!==-1){
        //     var goodsImage = list.goods_image.split(",");
        //     for (var i = 0; i < goodsImage.length; i++){
        //         jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + goodsImage[i] + '"></div>');
        //     }
        // }
        for(var i=0;i<goods_image.length;i++){
            if(goods_image[i].image.indexOf("http")!==-1){
                jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + goods_image[i].image+ '"></div>');
            }
        }
        document.title = list.goods_name;
        jQuery('.detail').html('<p class="product_code">货号:' + list.goods_code + '</p><p class="pice">价格:<span>￥' + list.goods_price + '</span></p><div class="total"><p>年份:' + list.goods_time + '</p><p>季度:' + list.goods_quarter + '</p><p>波段:' + list.goods_wave + '</p></div>');
        jQuery('#content').html(list.goods_description);
        list=list.matchgoods;
        for (var i = 0; i < list.length; i++) {
            jQuery('#match-con ul').append('<a href="goods.html?corp_code='
                + corp_code
                + '&id='
                + list[i].id
                + '"><li class="ti_img_wrap"><div class="ti_img"><img src="'
                + list[i].goods_image
                + '" alt="暂无图片"></div><p>'
                + list[i].goods_code
                + '</p></li></a>');
        }
        var width=$(".swiper-slide").width();
        var height=$(".swiper-slide").height();
        console.log(width);
        console.log(height);
        $(".swiper-slide").css({"height":+width+"px"});
        $('#swipe').swiper({
            pagination: '#swipe .swiper-pagination',
            slidesPerView: 'auto',
            paginationClickable: true,
            spaceBetween: 0,
            autoplay: 3000,
            loop: true,
            loopedSlides: 5,
            autoplayDisableOnInteraction: false
        });
    })
    window.onresize=function(){  
        var h=$('.ti_img_wrap').width();
        $('.ti_img').css({"height":+h+"px"});
    }  
});