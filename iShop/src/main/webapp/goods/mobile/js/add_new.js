/**
 * Created by Bizvane on 2016/12/28.
 */
var addProduct={
     param:{},
     page:1,
     html:'',
     next:false,
     searchName:'',
     first:true,
     r_match_goods:[],
     save_match_goods:[],
     theRequest:{},
     status:'',
     count:0,
     oc:new ObjectControl(),
    init:function () {
        this.getUrl();
        //判断是否进入编辑页面
        this.theRequest.d_match_code?this.getValue(): $.cookie('action','1');
        $("#file").on('change','',function(e){
            var oss = new OSS.Wrapper({
                region: 'oss-cn-hangzhou',
                accessKeyId: 'O2zXL39br8rSn1zC',
                accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
                bucket: 'products-image'
            });
            var files= e.target.files;
            for(var key in files){
                if(!files[key].type)return;
                var pos=files[key].name.lastIndexOf(".");
                var imgclass=files[key].name.substring(pos+1,files[key].name.length);
                if(imgclass =="jpg" ||imgclass =="jpeg" || imgclass =="png" || imgclass=="gif" || imgclass=="JPG"||imgclass=='PNG'||imgclass=='png' ||imgclass=='bmp' ||imgclass=='JPEG' ||imgclass=='GIF' ||imgclass=='BMP'){
                    var storeAs='ShowImage/'+this.theRequest.corp_code+'/'+new Date().getTime()+this.theRequest.user_id+'.jpg';
                    var me=this;
                    oss.multipartUpload(storeAs, files[key]).then(function (result) {
                        console.log(result);
                        var storeAs='http://products-image.oss-cn-hangzhou.aliyuncs.com/'+result.name;
                        console.log(storeAs);
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
                $('.list_box').find('.changeBcColor').each(function () {
                    $(this).removeClass('changeBcColor');
                })
            //联通APP
            var toApp='choose';
            var type={type:'新增商品列表'};
            me.doAppWebRefresh(JSON.stringify(type),toApp);
            me.count= $('#picture_box ').find('.picture').length;
            $('.bg').hide();
            $('.product_list').show();
            $('.count').html('已选择'+me.count+'个');
            $(window).bind('scroll',me.scroll.bind(me));
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
            if(!me.first){
                return
            }
            me.first=false;
            me.getList();
        });
        $(window).bind('scroll',this.scroll.bind(this));
        $('.complete_btn').click(this.choose.bind(this));
        $('.search_box input').keydown(function (e) {
            if(e.keyCode==13){
                me.searchName=$(this).val();
                me.html='';
                me.page=1;
                $('.list_box').empty();
                me.getList();
            }
        });
        $('.search_box .search').click(function () {
            $('.search_box input').trigger('focus')
        });
        //点击效果
        $('.list_box').on('click','.list',function () {
            //商品已存在
            var arr_number=[];
            for(var i=0;i<me.save_match_goods.length;i++){
                arr_number.push(me.save_match_goods[i].r_match_goodsCode)
            }
            for(var i=0;i<me.r_match_goods.length;i++){
                arr_number.push(me.r_match_goods[i].r_match_goodsCode)
            }
            if(arr_number.indexOf($(this).find('.list_number').html())!=-1){
                $('.tips').show();
                $('.err_txt').html('该商品已被选中');
                setTimeout(function () {
                    $('.tips').hide();
                },1000);
                return;
            }
            if((parseInt(me.count)+parseInt($('.list_box').find('.changeBcColor').length))>=10){
                if(this.className.indexOf('changeBcColor')!=-1){
                    $(this).toggleClass("changeBcColor");
                    $('.count').html('已选择10个');
                    return;
                }
                $('.tips').show();
                $('.err_txt').html('最多添加10件搭配');
                setTimeout(function () {
                    $('.tips').hide();
                },1000);
                return;
            }
            $(this).toggleClass("changeBcColor");
            $('.count').html('已选择'+(parseInt(me.count)+parseInt($('.list_box').find('.changeBcColor').length))+'个');
        });
        //删除
        $('#photo_box').on('click','.picture_btn',function () {
            $(this).parent().remove();
        });
        $('#picture_box').on('click','.picture_btn',function () {
            me.deleteIt($(this).parent()[0]);
        });
        //点击主动传值
        $('#photo').click(function () {
            var pic=$('#photo_box').find('.picture').length;
            //联通APP
            var toApp='localUpload';
            var type={upload:pic};
            me.doAppWebRefresh(JSON.stringify(type),toApp);
        });

    },
    completeToApp:function (){
        var toApp='addComplete';
        var img=[];
        var goods=[];
        $('#photo_box .picture>img').each(function () {
            img.push(this.src);
        });
        //标题不为空
        if(!$('.header_title_r').val()){
            this.test('搭配名称不能为空');
            return
        }
        //照片不为空
        if(img.length<=0){
            this.test('搭配照片不能为空');
            return
        }
        //商品图片不为空
        if(this.r_match_goods.concat(this.save_match_goods).length<=0){
            this.test('搭配商品不能为空');
            return
        }
        //描述不为空
        if(!$('#describe').val()){
            this.test('搭配说明不能为空');
            return
        }
        //保存时对当前的商品图片进行校对
        $('#picture_box .picture>img').each(function () {
            goods.push(this.src);
        });
        //进行校对
        // console.log(goods);
        // console.log(this.r_match_goods);
        // for(var i=0;i<this.r_match_goods.length;i++){
        //     // console.log(this.r_match_goods[i])
        //     // console.log(this.r_match_goods[i].r_match_goodsImage);
        //     // console.log(goods.indexOf(this.r_match_goods[i].r_match_goodsImage));
        //     // console.log(save_goods.indexOf(this.r_match_goods[i].r_match_goodsImage)==-1);
        //     if(goods.indexOf(this.r_match_goods[i].r_match_goodsImage)!=-1){
        //         save_goods.push(this.r_match_goods[i]);
        //     }
        // }
        this.param.corp_code=this.theRequest.corp_code;
        this.param.d_match_title=$('.header_title_r').val();
        this.param.d_match_image=img.join(',');
        this.param.d_match_desc=$('#describe').val();
        this.param.user_code=this.theRequest.user_id;
        this.param.r_match_goods=this.r_match_goods.concat(this.save_match_goods);
        console.log(this.param);
        if(this.status=='edit'){
            //编辑
            this.param.d_match_code=this.theRequest.d_match_code;
            this.oc.postRequire("post","/api/shopMatch/updGoodsByWx",'',this.param,function(data){
                console.log(data);
                if(data.code==0){
                    //联通APP
                    this.doAppWebRefresh('success',toApp);
                }else if(data.code==-1){
                    this.doAppWebRefresh(' defeat',toApp);
                }
            }.bind(this))
        }else{
            //新增
            this.oc.postRequire("post","/api/shopMatch/addGoodsByWx",'',this.param,function(data){
                console.log(data);
                if(data.code==0){
                    //联通APP
                    this.doAppWebRefresh('success',toApp);
                }else if(data.code==-1){
                    this.doAppWebRefresh(' defeat',toApp);
                }
            }.bind(this))
        }

    },
    updateImage:function (data) {
        this.showImg(data);
    },
    test:function (html) {
        $('.err_txt').html(html);
        $('.tips').show();
        setTimeout(function () {
            $('.tips').hide();
        },1000);
    },
    choose:function () {
        $('.search_null').hide();
        $(window).unbind('scroll');
        //联通APP
        var toApp='chooseComplete';
        var remove={'removeView':'完成'}
        this.doAppWebRefresh(JSON.stringify(remove),toApp);
        // this.r_match_goods=[];
        $('#picture_box .picture').remove();
        var me=this;
        //save_match_goods中的r_match_goodsCode
        var arr_goodsCode=[];
        for(var i=0;i<this.save_match_goods.length;i++){arr_goodsCode.push(this.save_match_goods[i].r_match_goodsCode)}
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
            arr_goodsCode.indexOf(r_match_goodsCode)==-1&&(me.r_match_goods.push(param));
        });
        $('.bg').show();
        $('.product_list' ).hide();
        //添加商品图片
        var arr=this.r_match_goods.concat(me.save_match_goods);
        for(var i=0;i<arr.length;i++){
            this.showImg(arr[i].r_match_goodsImage,'');
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
        var wdh='';
        var hgt='';
        var top='';
        var left='';
        var imgWidth='';
        var imgHeight='';
        var image = new Image();
        image.src = storeAs;
      if(arguments.length==1){
        image.onload = function(){
            console.log(image.width,image.height);
            //获取容器的最大尺寸
            var maxSize=parseFloat($('#photo').css('width'));
            imgWidth=image.width;
            imgHeight=image.height;
            var hRatio=imgHeight/maxSize;
            var wRatio=imgWidth/maxSize;
            console.log(imgWidth,imgHeight,hRatio,wRatio);
            if (image.width < maxSize&& image.height < maxSize) {//放大
            }else if(image.width < maxSize&& image.height > maxSize){//
            }else if(image.width > maxSize&& image.height < maxSize){//
            }else if(imgWidth>maxSize&& imgHeight>maxSize){
                if(hRatio>=wRatio){
                    wdh=maxSize;
                    hgt=imgHeight/wRatio;
                    //向上移动
                    top=-(hgt-maxSize)/2
                }else if(hRatio<wRatio){
                    hgt=maxSize;
                    wdh=imgWidth/hRatio;
                    //向左移动
                    left=-(wdh-maxSize)/2;
                }
            }
            var tempHTML = '<li class="picture"><img style="margin-top: '+top+'px;margin-left: '+left+'px" src="${storeAs}" width="'+wdh+'" alt="showImage " height="'+hgt+'"> <div class="picture_btn"></div> </li>';
            var html='';
            var nowHTML = tempHTML;
            nowHTML = nowHTML.replace('${storeAs}',storeAs);
            html+=nowHTML;
           $('#photo').before(html);
        }
      }else if (arguments.length==2){
        var tempHTML = '<li class="picture"><img  src="${storeAs}"  alt="showImage "> <div class="picture_btn"></div> </li>';
        var html='';
        var nowHTML = tempHTML;
        nowHTML = nowHTML.replace('${storeAs}',storeAs);
        html+=nowHTML;
        $('#picture').before(html);
        }
    },
    deleteIt:function (dom){
        $(dom).remove();
        console.log(this.r_match_goods)
        console.log(this.save_match_goods)
        console.log($(dom).find('img')[0].src);
        for(var i=this.r_match_goods.length-1;i>=0;i--){
            $(dom).find('img')[0].src==this.r_match_goods[i].r_match_goodsImage&&(this.r_match_goods.splice(i,1));
        }
        console.log(this.save_match_goods.length-1);
        for(var i=this.save_match_goods.length-1;i>=0;i--){
            console.log( $(dom).find('img')[0].src==this.save_match_goods[i].r_match_goodsImage);
         $(dom).find('img')[0].src==this.save_match_goods[i].r_match_goodsImage&&(this.save_match_goods.splice(i,1));
        }
},
    getList:function() {
        this.html='';
        var corp_code=this.theRequest.corp_code;
        var pageSize=10;
        var pageIndex=this.page;
        var categoryId='';
        var row_num=0;
        var productName=$('.search_box input').val().trim();
        var me=this;
        this.oc.postRequire("get","/api/shopMatch/getGoodsByWx?corp_code="+corp_code+"&pageSize="+pageSize+"&pageIndex="+pageIndex+"&categoryId="+categoryId+"&row_num="+row_num+"&productName="+productName,'','',function(data){
        if(data.code==0){
            console.log(JSON.parse(data.message));
            var list=JSON.parse(data.message).productList;
            if (list.length <= 0) {
                $('.search_null').show();
                if (list.length == "0") {
                    me.next = false;
                }
            } else {
                $('.search_null').hide();
                for (var i = 0; i < list.length; i++) {
                    me.html += '<li class="list"> <div class="list_picture"><img src="' + list[i].imageUrl +
                        '" alt=""/></div> <div class="list_describe"> <p class="list_name">' + list[i].productName +
                        '</p> <p>货号：<span class="list_number">' + list[i].productId +
                        '</span></p> <p>价格：<span class="list_price">￥' + list[i].price +
                        '</span></p> </div> <p class="list_line"></p></li>';
                }
                me.next=true;
            }
            $('.list_box').append(me.html);
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
        this.status='edit';
        //清空页面
        $('#photo_box .picture').remove();
        $('#picture_box .picture').remove();
        //编辑页面
        var corp_code=this.theRequest.corp_code;
        var d_match_code=this.theRequest.d_match_code;
        var user_code=this.theRequest.user_id;
        var me=this;
        this.oc.postRequire("get","/api/shopMatch/selectById?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_code="+user_code,'','',function(data){
            var val=JSON.parse(data.message);
            $('.header_title_r').val(val.d_match_title);
            $('#describe').val(val.d_match_desc);
            if(val.d_match_image){
                var arr=val.d_match_image.split(',');
                for(var i=0;i<arr.length;i++){
                    me.showImg(arr[i])
                }
            }
            me.save_match_goods=val.r_match_goods;
            if(val.r_match_goods){
                for(var i=0;i<val.r_match_goods.length;i++){
                    me.showImg(val.r_match_goods[i].r_match_goodsImage,'')
                }
            }
        })

     },
    returnToApp:function () {
        $('.search_null').hide();
        var toApp='returnAdd'
        $('.bg').show();
        //显示商品列表
        $('.product_list').hide();
        //联通APP
        var remove={'removeView':'完成'}
        this.doAppWebRefresh(JSON.stringify(remove),toApp);
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
   doAppWebRefresh:function (param,toApp){
    // var param=JSON.stringify(param);
    var osType = this.getWebOSType();
       console.log(toApp)
    if(osType=="iOS"){
        switch (toApp){
            case 'choose':window.webkit.messageHandlers.chooseProduct.postMessage(param);break;//添加商品列表图片
            case 'chooseComplete':window.webkit.messageHandlers.chooseComplete.postMessage(param);break;//商品列表完成按钮
            case 'addComplete':window.webkit.messageHandlers.addComplete.postMessage(param);break;//新增商品完成按钮
            case 'returnAdd':window.webkit.messageHandlers.returnAdd.postMessage(param);break;//新增商品列表返回
            case 'localUpload':window.webkit.messageHandlers.localUpload.postMessage(param);break;//本地上传图片
        }
        // window.webkit.messageHandlers.NSCompleteAddXiuda.postMessage(param);
    }else if(osType == "Android"){
        switch (toApp){
            case 'choose': iShop.chooseProduct(param);break;//添加商品列表图片
            case 'chooseComplete': iShop.chooseComplete(param);break;//商品列表完成按钮
            case 'addComplete':iShop.addComplete(param);break;//新增商品完成按钮
            case 'returnAdd':iShop.returnAdd(param);break;//新增商品列表返回
            case 'localUpload':iShop.localUpload(param);break;//本地上传图片
        }
        // iShop.returnAddResult(param);
    }
},
}
jQuery(function(){
    //完成时的操作
    addProduct.init();
    // addProduct.completeToApp();
    // addProduct.returnToApp()
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10141/ABC123-1483513648.jpg');
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10141/ABC123-1483513761.jpg');
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10160/THSHRE1605001-1483532056.jpg');
});
