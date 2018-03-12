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
        if(url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for (var i = 0;i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
            }
        }
        return theRequest;
    }
    var a = GetRequest();
    var id = a.id;
    var corp_code = a.corp_code;
    var user_id=a.user_id;
    var type=a.type;
    var goodsImage=[];
    var goodsName="";
    var oc = new ObjectControl();
    var query = {
        "id": id,
        "corp_code": corp_code,
        "user_id":user_id
    };
    var oc = new ObjectControl();
    var query = {
        "id": id,
        "corp_code": corp_code,
        "user_id":user_id
    };
    $("#loading").show();
    oc.postRequire("post", "/api/fab/select", "", query, function (data) {
        $("#loading").hide();
        var list = JSON.parse(data.message);
        var list = JSON.parse(list.goods);
        var goods_image=JSON.parse(list.goods_image);
        var pice="";
        var goods_year="";
        if(list.goods_year==""){
            goods_year="暂无";
        }
        if(list.goods_year!=""){
            goods_year=list.goods_year;
        }
        if(list.goods_price==""){
            pice="暂无";
        }
        if(list.goods_price!==""){
            pice=list.goods_price;
        }
        if(type=="share"){
            for(var i=0;i<goods_image.length;i++){
                if(goods_image[i].image.indexOf("http")!==-1&&goods_image[i].is_public=="Y"){
                    var img="";
                    if(goods_image[i].image.indexOf("products-image.oss-cn-hangzhou.aliyuncs.com")!==-1){
                        img=goods_image[i].image+ '@500h_500w_1e_1c';
                    }else {
                        img=goods_image[i].image;
                    }
                    jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + img+'" data-preview-src="'+goods_image[i].image+'" data-preview-group="image"></div>');
                }
            }
            jQuery('#buy').html("详情");
            jQuery('#content').html(list.share_description);
            jQuery('.detail').html('<p class="product_code">商品编号:'
                + list.goods_code + '</p><p class="pice">价格:<span>￥'
                + pice + '</span></p><div class="total"><p style="width: 42%;"  title='
                +goods_year+'>年份:'
                +goods_year + '</p><p style="width:29%;"title='+list.goods_quarter+'>季度:'
                + list.goods_quarter + '</p><p style="width: 29%;" title='+ list.goods_wave +'>波段:'
                + list.goods_wave + '</p></div><div class="total"><p style="width: 42%;" title='+list.goods_time+'>发布日期:'+list.goods_time+'</p><p style="width: 29%;">销量:'+list.num_sales+'</p><p style="width: 29%;">库存:'+list.num_stocks+'</p></div>');
        }else if(type=="app"){
            for(var i=0;i<goods_image.length;i++){
                if(goods_image[i].image.indexOf("http")!==-1){
                    if(goods_image[i].is_public=="Y"){
                        goodsImage.push(goods_image[i].image);
                    }
                    var img="";
                    if(goods_image[i].image.indexOf("products-image.oss-cn-hangzhou.aliyuncs.com")!==-1){
                        img=goods_image[i].image+ '@500h_500w_1e_1c';
                    }else {
                        img=goods_image[i].image;
                    }
                    jQuery('.header .swiper-wrapper').append('<div class="swiper-slide"><span class="item"></span><img src="' + img+'" data-preview-src="'+goods_image[i].image+'" data-preview-group="image"></div>');
                }
            }
            jQuery('#content').html(list.goods_description);
            jQuery('.detail').html('<p class="product_code">货号:'
                + list.goods_code + '</p><p class="pice">价格:<span>￥'
                + pice + '</span></p><div class="total"><p style="width: 42%;"  title='
                +goods_year+'>年份:'
                +goods_year + '</p><p style="width: 29%;" title='+list.goods_quarter+'>季度:'
                + list.goods_quarter + '</p><p style="width: 29%;" title='+ list.goods_wave +'>波段:'
                + list.goods_wave + '</p></div><div class="total"><p style="width: 42%;" title='+list.goods_time+'>发布日期:'+list.goods_time+'</p><p style="width: 29%;">销量:'+list.num_sales+'</p><p style="width: 29%;">库存:'+list.num_stocks+'</p></div>');
        }
        if(list.goods_source!=="out"){
            $("#standard").hide();
            $(".bgcolor").hide();
            $(".detail .pice").show();
        }else if(list.goods_source=="out"){
            $("#standard").show();
            $(".bgcolor").show();
            $(".detail .pice").hide();
            var standardList=JSON.parse(list.standard);
            console.log(standardList);
            var product_detail=standardList.product_detail;
            var html="";
            for(var i=0;i<product_detail.length;i++){
                html+="<ul><li>"+product_detail[i].COLOR_PRD+"</li><li>"+product_detail[i].SIZE_PRD+"</li><li class='pirce'>￥"+product_detail[i].PRICE_SUG+"</li></ul>"
            }
            $("#standard_pice_list").html(html);
        }
        document.title = list.goods_name;
        goodsName=list.goods_name;
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
            var num="";
            for(var c=0;c<unqiuearr.length;c++){
                var li="";
                var dismatch = [];
                for(var k=list.length-1;k>=0;k--){
                    if(list[k].goods_match_code ==unqiuearr[c]){
                        title=list[k].goods_match_title;
                        if(list[k].match_display!==""&&list[k].match_display!==undefined){
                            dismatch=list[k].match_display.split(",");
                        }
                        if(num!==c){
                            for(var j=0;j<dismatch.length;j++){
                                li+='<li class="ti_img_wrap" data-type=""><div class="ti_img"><img src="'
                                    + dismatch[j]
                                    + '@300h_300w_1e_1c" alt="暂无图片" data-preview-src="'+dismatch[j]+'" data-preview-group="'+c+'"></div><p>'
                                    + "效果图"
                                    + '</p></li>'
                            }
                        }
                        num=c;
                        var goods_image="";
                        var isShare="";
                        if(type=="app"){
                            isShare='app';
                        }else if(type=="share"){
                            isShare='share';
                        }
                        if(list[k].goods_image!==""){
                            var dapei=JSON.parse(list[k].goods_image);
                            var dapei_goodsimage=[];
                            for(var l=0;l<dapei.length;l++){
                                if(type=="app"){
                                    dapei_goodsimage.push(dapei[l].image);
                                }
                                if(type=="share"){
                                    if(dapei[l].is_public=="Y"){
                                        dapei_goodsimage.push(dapei[l].image);
                                    }
                                    
                                }
                            }
                            if(dapei_goodsimage.length>0){
                                if(dapei_goodsimage[0].indexOf("http")!==-1){
                                    goods_image=dapei_goodsimage[0]+"@300h_300w_1e_1c";
                                }
                                if(dapei_goodsimage[0].indexOf("http")==-1){
                                    goods_image="image/goods_default_image.png";
                                }
                            }
                            if(dapei_goodsimage.length<=0){
                                goods_image="image/goods_default_image.png";
                            }
                            li+='<li class="ti_img_wrap" data-type="shop" id="'+list[k].id+'"><div class="ti_img"><img src="'
                                + goods_image
                                + '" alt="暂无图片"></div><p>'
                                + list[k].goods_code
                                + '</p></li>'
                        }
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
            loop:false,
            loopedSlides: 5,
            autoplayDisableOnInteraction: true
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
            param["goodsImage"]=goodsImage[0].replace(/(^\s*)|(\s*$)/g, "");
            param["goods_image_list"]=goodsImage.join(",");
        }
        param["url"]="http://"+window.location.host+"/goods/mobile/goods.html?corp_code="+corp_code+"&id="+id+"&type=share&user_id="+user_id+"";
        param["goodsName"]=goodsName;
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
    $("#match-con").on("click",".ti_img_wrap",function(){
        var shop=$(this).attr("data-type");
        if(shop=="shop"){
            var id=$(this).attr("id");
            location.href="http://"+window.location.host+"/goods/mobile/goods.html?corp_code="+corp_code+"&id="+id+"&type="+type+"&user_id="+user_id+"";
        }
    })