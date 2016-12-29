/**
 * Created by Bizvane on 2016/12/28.
 */
var addProduct={
     param:{},
     page:1,
     html:'',
     next:false,
     searchName:'',
     r_match_goods:[],
    theRequest:{},
     oc:new ObjectControl(),
     oss:new OSS.Wrapper({
    region: 'oss-cn-hangzhou',
    accessKeyId: 'O2zXL39br8rSn1zC',
    accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
    bucket: 'products-image'
}),
    init:function () {
        this.getUrl();
        //判断是否进入编辑页面
        this.theRequest.d_match_code?this.getValue():'';
        $("#file").on('change','',function(e){
            var files= e.target.files;
            for(var key in files){
                if(!files[key].type)return;
                var pos=files[key].name.lastIndexOf(".");
                var imgclass=files[key].name.substring(pos+1,files[key].name.length);
                if(imgclass =="jpg" ||imgclass =="jpeg" || imgclass =="png" || imgclass=="gif" || imgclass=="JPG"||imgclass=='PNG'||imgclass=='png' ||imgclass=='bmp' ||imgclass=='JPEG' ||imgclass=='GIF' ||imgclass=='BMP'){
                    var storeAs='ShowImage/'+pos;
                    var me=this;
                    this.oss.multipartUpload(storeAs, files[key]).then(function (result) {
                        var storeAs='http://products-image.oss-cn-hangzhou.aliyuncs.com/'+result.name;
                        me.showImg(storeAs);
                    }).catch(function (err) {
                        console.log(err);
                    });
                }else
                {
                    alert("请确保文件为图像类型");
                    return false;
                }
            }

        }.bind(this));
        //模拟APP
        $('#submit').click(this.completeToApp.bind(this));
        //获取商品列表
        var me=this;
        $('#picture').click(function () {
            $('.bg').hide();
            me.getList();
            //联通APP
            var type={type:'新增商品列表'}
            me.doAppWebRefresh(JSON.stringify(type));
            //显示商品列表
            $('.product_list').show();
            //输入框显示控制
            $('.search_box input').focus(function(){
                $('.search_box .search').css('display','none');
            });
            $('.search_box input').blur(function(){
                var val = $('.search_box input').val();
                if(val==''){
                    $('.search_box .search').css('display','block');
                }
            });
        });
        $(window).bind('scroll',this.scroll.bind(this));
        $('.complete_btn').click(this.choose.bind(this));
        $('.search_box input').keydown(function (e) {
            if(e.keyCode==13){
                me.searchName=$(this).val();
                me.html='';
                me.getList();
            }
        });
        $('.search_box .search').click(function () {
            $('.search_box input').trigger('focus')
        });
        //点击效果
        $('.list_box').on('click','.list',function () {
            $(this).toggleClass("changeBcColor");
            $('.count').html('已选择'+ $('.list_box').find('.changeBcColor').length+'个');
        });
    },
    completeToApp:function (){
        var img=[];
        $('.picture>img').each(function () {
            img.push(this.src)
        });
        this.param.corp_code=this.theRequest.corp_code;
        this.param.d_match_title=$('.header_title_r').html();
        this.param.d_match_image=img.join(',');
        this.param.d_match_desc='';
        this.param.user_code=this.theRequest.user_code;
        this.param.r_match_goods=this.r_match_goods;
        console.log(this.param);
        //请求/api/shopMatch/addGoodsByWx
        this.oc.postRequire("post","/api/shopMatch/addGoodsByWx",'',this.param,function(data){
            console.log(data);
            if(data.code==0){
                //联通APP
                this.doAppWebRefresh('success');
            }else if(data.code==-1){
                this.doAppWebRefresh(' defeat');
            }
        }.bind(this))
    },
    choose:function () {
        //联通APP
        var remove={'removeView':'完成'}
        this.doAppWebRefresh(JSON.stringify(remove));

        this.r_match_goods=[];
        $('#picture_box .picture').remove();
        var me=this;
        $('.list_box').find('.changeBcColor').each(function () {
            var r_match_goodsCode=$(this).find('.list_number').html();
            var r_match_goodsImage=$(this).find('img').attr('src');
            var  r_match_goodsPrice=$(this).find('.list_price').html();
            var   r_match_goodsName=$(this).find('.list_name').html();
            var param={"r_match_goodsCode":r_match_goodsCode,
                        "r_match_goodsImage":r_match_goodsImage,
                        "r_match_goodsPrice":r_match_goodsPrice,
                        "r_match_goodsName":r_match_goodsName
            };
            me.r_match_goods.push(param);
        });
        $('.bg').show();
        $('.product_list' ).hide();
        //添加商品图片
        for(var i=0;i<this.r_match_goods.length;i++){
            this.showImg(this.r_match_goods[i].r_match_goodsImage,'');
        }
    },
    scroll:function () {
        var bot = 50; //bot是底部距离的高度
        if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
            if(!this.next){
                return;
            }
            this.next=false;
            this.page++;
            this.getList();
        }
    },
    showImg:function (storeAs) {
        var tempHTML = '<li class="picture"><img src="${storeAs}" alt="showImage "> <div class="picture_btn"><img src="image/btn_img_del@2x_67.png"width="100%"height="100%" alt="delete"></div> </li>';
        var html='';
        var nowHTML = tempHTML;
        nowHTML = nowHTML.replace('${storeAs}',storeAs);
        html+=nowHTML;
        arguments.length==1? $('#photo').before(html): $('#picture').before(html);
        this.deleteIt();
    },
    deleteIt:function (){
    $('.picture img').click(function () {
        $(this).parents('.picture').remove();
    });
},
    getList:function() {
        var corp_code=this.theRequest.corp_code;
        var pageSize=10;
        var pageIndex=this.page;
        var categoryId='';
        var row_num=0;
        var productName=$('.search_box input').val().trim();
        var me=this;
        this.oc.postRequire("get","/api/shopMatch/getGoodsByWx?corp_code="+corp_code+"&pageSize="+pageSize+"&pageIndex="+pageIndex+"&categoryId="+categoryId+"&row_num="+row_num+"&productName="+productName,'','',function(data){
        if(data.code==0){
            var list=JSON.parse(data.message).productList;
            if (list.length <= 0) {
                if (list.length == "0") {
                    me.next = false;
                }
            } else {
                var arr_match=me.r_match_goods;
                var arr_code=[];
                for(var i=0;i<arr_match.length;i++){
                    arr_code.push(arr_match[i].r_match_goodsCode);
                }
                for (var i = 0; i < list.length; i++) {
                    if(arr_code.indexOf(list[i].productId)!=-1){
                        me.html += '<li class="list changeBcColor"> <div class="list_picture"><img src="' + list[i].imageUrl +
                            '" alt=""/></div> <div class="list_describe"> <p class="list_name">' + list[i].productName +
                            '</p> <p>货号：<span class="list_number">' + list[i].productId +
                            '</span></p> <p>价格：<span class="list_price">￥' + list[i].price +
                            '</span></p> </div> </li>';
                    }else{
                        me.html += '<li class="list"> <div class="list_picture"><img src="' + list[i].imageUrl +
                            '" alt=""/></div> <div class="list_describe"> <p class="list_name">' + list[i].productName +
                            '</p> <p>货号：<span class="list_number">' + list[i].productId +
                            '</span></p> <p>价格：<span class="list_price">￥' + list[i].price +
                            '</span></p> </div> </li>';
                    }
                }
                me.next=true;
                me.searchName='';
            }
            $('.list_box').html(me.html);
            //绑定
        }else if(data.code==-1){
            alert(data.message);
        }
    });
},
    getUrl:function(){
            var url = decodeURIComponent(location.search); //获取url中"?"符后的字串
            // var theRequest = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                strs = str.split("&");
                for (var i = 0; i < strs.length; i++) {
                    this.theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
                }
            }
            // return theRequest;
    },
    getValue:function () {
        //编辑页面
        console.log(this.theRequest);
    },
    returnToApp:function () {
        $('.bg').show();
        //显示商品列表
        $('.product_list').hide();
    },
    //获取手机系统
    getWebOSType:function (){
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
},
   //获取iShop用户信息
    getAppUserInfo:function (){
    var osType = getWebOSType();
    var userInfo = null;
    if(osType=="iOS"){
        userInfo = NSReturnUserInfo();
    }else if(osType == "Android"){
        userInfo = iShop.ReturnUserInfo();
    }
    return userInfo;
},
  //调用APP方法传参 param 格式 type：** ;url:**
   doAppWebRefresh:function (param){
    // var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        window.webkit.messageHandlers.NSJumpToWebViewForWeb.postMessage(param);
    }else if(osType == "Android"){
        iShop.returnAddResult(param);
    }
},
}
jQuery(function(){
    //完成时的操作
    addProduct.init();
    // addProduct.completeToApp();
    // addProduct.returnToApp()
});