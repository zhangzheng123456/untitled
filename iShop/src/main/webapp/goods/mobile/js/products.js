// jQuery(function () {
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
        returnShareInfoToApp();
    });
    function GetRequest(){
        var url = location.search; //获取url中"?"符后的字串
        var theRequest = new Object();
        if(url.indexOf("?") != -1) {
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
    var type=a.type;
    var goodsImage=[];
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
        if(type=="share"){
            for(var i=0;i<goods_image.length;i++){
                if(goods_image[i].image.indexOf("http")!==-1&&goods_image[i].is_public=="Y"){
                    jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + goods_image[i].image+ '"></div>');
                }
            }
            jQuery('#buy').html("详情");
            jQuery('#content').html(list.share_description);
        }else if(type=="app"){
            for(var i=0;i<goods_image.length;i++){
                if(goods_image[i].image.indexOf("http")!==-1){
                    if(goods_image[i].is_public=="Y"){
                        goodsImage.push(goods_image[i].image);
                    }
                    jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + goods_image[i].image+ '"></div>');
                }
            }
            jQuery('#content').html(list.goods_description);
        }
        document.title = list.goods_name;
        jQuery('.detail').html('<p class="product_code">货号:' + list.goods_code + '</p><p class="pice">价格:<span>￥' + list.goods_price + '</span></p><div class="total"><p>年份:' + list.goods_time + '</p><p>季度:' + list.goods_quarter + '</p><p>波段:' + list.goods_wave + '</p></div>');
        list=list.matchgoods;
        if(list.length>0){
            var arr=[];
            var unqiuearr=[];
            var hash={};
            //获取所有搭配id
            for(var a=0;a< list.length;a++){
                arr.push(list[a].goods_match_code);
            }
            //搭配id去重
            for(var b=0;b<arr.length;b++){
                if(!hash[arr[b]]){
                    hash[arr[b]] = true; //存入hash表
                    unqiuearr.push(arr[b]);
                }
            }
            var html="";
            var title="";
            for(var c=0;c<unqiuearr.length;c++){
                var li="";
                for(var k=0;k<list.length;k++){
                    if(list[k].goods_match_code ==unqiuearr[c]){
                        title=list[k].goods_match_title;
                        var goods_image="";
                        if(list[k].goods_image.indexOf("http")!==-1){
                            goods_image=list[k].goods_image;
                        }
                        if(list[k].goods_image.indexOf("http")==-1){
                            goods_image="image/goods_default_image.png";
                        }
                        li+='<a href="goods.html?corp_code='
                            + corp_code
                            + '&id='
                            + list[k].id
                            + '"><li class="ti_img_wrap"><div class="ti_img"><img src="'
                            + goods_image
                            + '" alt="暂无图片"></div><p>'
                            + list[k].goods_code
                            + '</p></li></a>'
                    }
                }
                html+="<div class='footer'><h4 class='biaoti'>"+title+"</h4><div class='goods_list_p'><div class='goods_list'>"+li+"</div></div></div>";
            }
            jQuery('#match-con ul').append(html);
        }
        var width=$(".swiper-slide").width();
        var height=$(".swiper-slide").height();
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
    //获取手机系统
    function getWebOSType(){
        var browser = navigator.userAgent;
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
    function returnShareInfoToApp(){
        var param={};
        if(goodsImage[0]==undefined){
            param["goodsImage"]="";
        }else{
            param["goodsImage"]=goodsImage[0];
        }
        param["url"]="http://"+window.location.host+"/goods/mobile/goods.html?corp_code="+corp_code+"&id="+id+"&type=share";
        var param=JSON.stringify(param);
        var osType = getWebOSType();
        alert(param);
        if(osType=="iOS"){
            NSJumpToWebViewForWeb(param);
        }else if(osType == "Android"){
            iShop.jumpToWebViewForWeb(param);
        }
    }
// });