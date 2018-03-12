/**
 * Created by Bizvane on 2016/12/28.
 */
var addProduct={
     param:{},
     page:1,
     html:'',
     html2:'',
     next:false,
     next2:false,
     searchName:'',
     first:true,
     first2:true,
     r_match_goods:[],
     save_match_goods:[],
     theRequest:{},
     status:'',
     count:0,
     role:'',
     rowno:0,
     rowno_fab:0,
     start:0,//fab个数
     start_2:0,//wx个数
     fab_search:'',
     wx_search_post:'',
     oc:new ObjectControl(),
     tabBar:true,//选项卡fab/wx
    waves_arr:[],
    brands_arr:[],
    quarters_arr:[],
    filtrateWx:[],//微信的筛选id
    chooseId:[],
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
            });
            $('.list_box2').find('.changeBcColor').each(function () {
                $(this).removeClass('changeBcColor');
            });
            me.tabBar&&(me.firstBar());
            //默认FAB商品
            // $('.product_header .div_l').addClass('header_active').trigger('click');
            if($('#fab .search_box input').val()!=''){
                $('#fab .search_box input').val('');
                $('.list_box2').empty();
                me.rowno_fab = 0;
                $('#fab .search_box .search').css('display','block');
                me.getList();
            }
            if($('#wx .search_box input').val()!=''){
                $('#wx .search_box input').val('');
                me.rowno = 0;
                $('.list_box').empty();
                me.first2=true;
                $('#wx .search_box .search').css('display','block');
                me.getList();
            }
            if($('#picture_box .picture').length>=10){
                $('.err_txt').html('最多添加10件搭配商品');
                $('.tips').show();
                setTimeout(function () {
                    $('.tips').hide();
                },1000);
                return;
            }
            //联通APP
            var toApp='choose';
            var type={type:'新增商品列表'};
            me.doAppWebRefresh(JSON.stringify(type),toApp);
            $('.bg').hide();
            $('.product_list').show();
            me.count= $('#picture_box ').find('.picture').length;
            $('.count').html('已选择'+me.count+'个');
            $(window).bind('scroll',me.scroll.bind(me));
            //输入框显示控制
            $('#fab .search_box input').focus(function(){
                $('#fab .search_box .search').css('display','none');
            });
            $('#wx .search_box input').focus(function(){
                $('#wx .search_box .search').css('display','none');
            });
            $('#fab .search_box input').blur(function(){
                var val = $('#fab .search_box input').val();
                if(val==''){
                    $('#fab .search_box .search').css('display','block');
                }
            });
            $('#wx .search_box input').blur(function(){
                var val = $('#wx .search_box input').val();
                if(val==''){
                    $('#wx .search_box .search').css('display','block');
                }
            });
        });
        $(window).bind('scroll',this.scroll.bind(this));
        $('.complete_btn').click(this.choose.bind(this));
        $('#fab .search_box input').keydown(function (e) {
            if(e.keyCode==13) {
                $(window).unbind('scroll');
                    $('#loading').show();
                   $('.list_box2').empty();
                    me.rowno_fab = 0;
                    me.fab_search = 'fab';
                    me.brands_arr=[];
                    me.waves_arr=[];
                    me.quarters_arr=[];
                $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start))+'个');
                    me.fabSearch();
            }
        });
        $('#wx .search_box input').keydown(function (e) {
            if(e.keyCode==13) {
                me.wx_search_post='wx'
                $(window).unbind('scroll');
                   me.page=1;
                   me.filtrateWx=[];
                   $('#loading').show();
                    me.rowno = 0;
                    $('.list_box').empty();
                    me.fab_search = '';
                $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start))+'个');
                    me.getList();
                // me.wx_search_post='';
            }
        });
        $('.search_box .search').click(function () {
            $('.search_box input').trigger('focus')
        });
        //点击wx效果
        $('.list_box').on('click','.list',function () {
            console.log(me.start);
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
            if((parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box').find('.changeBcColor').length))>=10){
                if(this.className.indexOf('changeBcColor')!=-1){
                    $(this).toggleClass("changeBcColor");
                    $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box').find('.changeBcColor').length))+'个');
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
            $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box').find('.changeBcColor').length))+'个');
        });
        //fab模块
        $('.list_box2').on('click','.list',function () {
            console.log(me.start)
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
            if((parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box2').find('.changeBcColor').length))>=10){
                if(this.className.indexOf('changeBcColor')!=-1){
                    $(this).toggleClass("changeBcColor");
                    $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box2').find('.changeBcColor').length))+'个');
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
            $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start)+parseInt($('.list_box2').find('.changeBcColor').length))+'个');
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
        //FAB或WX
        $('.product_header .div_l').click(function () {
            console.log(me.first);
            me.start=$('.list_box').find('.changeBcColor').length;
            console.log(me.start);
            me.role='fab';
            $('#fab').show();
            $('#wx').hide();
            // if($('.search_box input').val()!=''){
            //     $('.search_box input').val('');
            //     $('.list_box2').empty();
            //     $('.search_box .search').css('display','block');
            //     me.getList();
            // }
            $('.product_header .div_l').addClass('header_active').siblings('.header_active').removeClass('header_active');
           //发请求
            if(!me.first){
               if($('.list_box2').html().trim()){  $('#fab .search_null').hide()};
                return
            }
            me.first=false;
            me.getList();
        });
        $('.product_header .div_r').click(function () {//微商城
            me.role='wx';
            me.start=$('.list_box2').find('.changeBcColor').length;
            $('#fab').hide();
            $('#wx').show();
            // if($('.search_box input').val()!=''){
            //     $('.search_box input').val('');
            //     $('.search_box .search').css('display','block');
            //     $('.list_box').empty();
            //     me.getList();
            // }
            $('.product_header .div_r').addClass('header_active').siblings('.header_active').removeClass('header_active');
           //发请求
           //  me.first2=true;
            if(!me.first2){
                if($('.list_box').html().trim()){  $('#wx .search_null').hide()};
                return
            }
            me.first2=false;
            me.getList();
        });
        $("#screen").click(function(){
            $("#guide").animate({right:'0'},300);
            $("#cover").show();
            $("html").addClass("sift-move");
            $("body").addClass("sift-move");
        });
        this.filtrateFun();
        $('#screen_content ul').on('click','li',function(){
            $(this).toggleClass('active');
        });
        $('#reset').click(function(){
            me.brands_arr=[];
            me.waves_arr=[];
            me.quarters_arr=[];
            $('#screen_content ul li').each(function(index,val){
                $(val).removeClass('active');
            });
        })
        $('#screen_content_wx ').on('click','ul li',function(){
            $(this).toggleClass('active');
        });
        $('#reset_wx').click(function(){
            me.filtrateWx=[];
            $('#screen_content_wx ul li').each(function(index,val){
                $(val).removeClass('active');
            });
        })
        $('#complete').click(this.getFiltrateFun.bind(this));
        $('#complete_wx').click(this.getFiltrateWxFun.bind(this));
        $('#screen_wx').click(function(){
            $("#guide_wx").animate({right:'0'},300);
            $("#cover").show();
            $("html").addClass("sift-move");
            $("body").addClass("sift-move");
        });
        this.filtrateWxFun();
        $("#cover").click(function(){
            me.role=='fab'&&( $("#guide").animate({right:"-"+$("#guide").width()+"px"},300))
            me.role=='wx'&&(  $("#guide_wx").animate({right:"-"+$("#guide").width()+"px"},300));
            $("#cover").hide();
            $("html").removeClass("sift-move");
            $("body").removeClass("sift-move");
        });
        $('.chooseType').click(function(){
            //准备参
            var arr=[];
            var corp_code=me.theRequest.corp_code;
            $('.type_box li').each(function(index,val){
                //arr.push({'name':$(val).text().trim(),'id':val.dataset.id});
                arr.push(val.dataset.id);
            });
            me.chooseId=arr;
            var param={'label':arr.join(','),'address':'http://'+window.location.host+'/goods/mobile_v2/add_type.html?corp_code='+corp_code};
            console.log(JSON.stringify(param));
            //联通APP
            var toApp='label';
            me.doAppWebRefresh(JSON.stringify(param),toApp);
        });
        $('.type_box ul').on('click','li',function(){
            $(this).remove();
        });
    },
    getFiltrateWxFun:function(){
        var me=this;
        me.filtrateWx=[];
        $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start))+'个');
        me.wx_search_post='wx'
        $(window).unbind('scroll');
        me.page=1;
        $('#loading').show();
        me.rowno = 0;
        $('.list_box').empty();
        me.fab_search = '';
        $('#screen_content_wx ul li.active').each(function(index,val){
            me.filtrateWx.push(val.dataset.catid)
        });
        me.getList();
        $('#cover').trigger('click')
    },
    filtrateWxFun:function(){
        var corp_code=this.theRequest.corp_code;
        //getProductCategoryByWx   get  corp_code
        this.oc.postRequire("get","/api/shopMatch/getProductCategoryByWx?corp_code="+corp_code,'','',function(data){
            var data_arr=JSON.parse(data.message).category
            for(var i=0;i<data_arr.length;i++){
                var html='';
                html='<div class="parent"><h4>'+data_arr[i].catName+'</h4><div class="list"><ul>';
                for(var g=0;g<data_arr[i].subCategory.length;g++){
                    html+='<li data-catid="'+data_arr[i].subCategory[g].catId+'">'+data_arr[i].subCategory[g].catName+'</li>';
                }
                html+=' </ul></div><div class="xian"></div></div>';
                $('#screen_content_wx').append(html);
            }
        })
    },
    getFiltrateFun:function(){
        var me=this;
        me.brands_arr=[];
        me.waves_arr=[];
        me.quarters_arr=[];
        $('.count').html('已选择'+(parseInt(me.count)+parseInt(me.start))+'个');
        $('#match_type ul li.active').each(function(index,val){
            me.brands_arr.push(val.dataset.code)
        });
        $('#waves ul li.active').each(function(index,val){
            me.waves_arr.push(val.innerHTML)
        });
        $('#goods_quarter ul li.active').each(function(index,val){
            me.quarters_arr.push(val.innerHTML)
        });
        console.log(me.waves_arr);
        console.log(me.brands_arr);
        console.log(me.quarters_arr)
        $(window).unbind('scroll');
        $('#loading').show();
        $('.list_box2').empty();
        this.rowno_fab = 0;
        this.fab_search = 'fab';
        this.fabSearch();
        $('#cover').trigger('click')
    },
    filtrateFun:function(){
        var param={};
        param["corp_code"]=this.theRequest.corp_code;
        param["user_id"]=this.theRequest.user_id;
        this.oc.postRequire("post","/api/fab/screenValue","0",param,function(data){
           try{
               var waves=JSON.parse(JSON.parse(data.message).waves);
               var brands=JSON.parse(JSON.parse(data.message).brands);
               var quarters=JSON.parse(JSON.parse(data.message).quarters);
           }catch(err){
               var waves=[];
               var brands=[];
               var quarters=[];
           }

            var waves_arr=[];
            var brands_arr=[];
            var quarters_arr=[];
            for(var i=0;i<waves.length;i++){
                waves_arr.push(waves[i].goods_wave);
            }
            for(var i=0;i<brands.length;i++){
                brands_arr.push('<li data-code="'+brands[i].brand_code+'">'+brands[i].brand_name+'</li>');
            }
            for(var i=0;i<quarters.length;i++){
                quarters_arr.push(quarters[i].goods_quarter);
            }
            $('#match_type ul').html(brands_arr.join(''));
            $('#waves ul').html('<li>'+waves_arr.join('</li><li>')+"</li>");
            $('#goods_quarter ul').html('<li>'+quarters_arr.join('</li><li>')+"</li>");
        });
    },
    firstBar:function(){
        var me=this;
        me.oc.postRequire("get","/api/shopMatch/getGoodsTypeByCorp?corp_code="+me.theRequest.corp_code+"",'','',function(data){
            me.tabBar=false;
            if(data.code=="0"){
                if(data.message=="ALL"){
                    $('.product_header ').show();
                    $('#wx').css('margin-top','1.30435rem');
                    $('#fab').css('margin-top','1.30435rem');
                    $('#fab').addClass('product_action').siblings('div.product_action').removeClass('product_action');
                    $('.product_header .div_l').addClass('header_active');
                    me.role='fab';
                    me.first=false;
                    me.getList();
                }
                if(data.message=="FAB"){
                    $('.product_header ').hide();
                    me.role='fab';
                    me.getList();
                    $('#fab').addClass('product_action').siblings('div.product_action').removeClass('product_action');
                }
                if(data.message=="WX"){
                    $('.product_header ').hide();
                    me.role='wx'
                    me.getList();
                    $('#wx').addClass('product_action').siblings('div.product_action').removeClass('product_action');
                }
            }
        })
    },
    fabSearch:function () {
        this.html='';
        var param={};
        if($('#fab .search_box input').val()=='')this.fab_search='';//带值时调搜索

        param["search_value"]=$('#fab .search_box input').val().trim();
        param["rowno"]=this.rowno_fab*20;
        param["corp_code"]=this.theRequest.corp_code;
        param["user_id"]=this.theRequest.user_id;
        if(this.brands_arr.join('')||(this.waves_arr.join(''))||(this.quarters_arr.join(''))){
            this.fab_search='fab'
            param["brand_code"]=this.brands_arr.join(',');
            param["goods_quarter"]=this.quarters_arr.join(',');
            param["goods_wave"]=this.waves_arr.join(',');
        }
        var me=this;
        this.oc.postRequire("post","/api/fab/search","0",param,function(data){
            if(data.code==0){
                var list=JSON.parse(data.message).productList;
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                if(!hasNextPage){me.next = false;}else {me.next=true}
                me.rowno_fab++;
                var list=list.list;
                if (list.length <= 0) {
                    $('#fab .search_null').show();
                    // me.first=true;
                    // if (list.length == "0") {
                    //     me.next = false;
                    // }
                } else {
                    $('#fab .search_null').hide();
                    for (var i = 0; i < list.length; i++) {
                        me.html += '<li class="list"> <div class="list_picture"><img src="' + list[i].goods_image +
                            '" alt=""/></div> <div class="list_describe"> <p class="list_name">' + list[i].goods_name +
                            '</p> <p>货号：<span class="list_number">' + list[i].goods_code +
                            '</span></p> <p>价格：<span class="list_price">￥' + list[i].goods_price +
                            '</span></p> </div> <p class="list_line"></p></li>';
                    }
                }
                $('.list_box2').append(me.html);
                $(window).bind('scroll',me.scroll.bind(me));
                $('#loading').hide();
                //绑定
            }else if(data.code==-1){
                alert(data.message);
            }
        })
    },
    completeToApp:function (){
        var toApp='addComplete';
        var img=[];
        var goods=[];
        $('#photo_box .picture>img').each(function () {
            img.push(this.src);
        });
        //标题不为空
        if(!$('#d_match_title').val()){
            this.test('搭配名称不能为空');
            return
        }
        //秀搭类型不能为空
        if($('.type_box ul').find('li').length<=0){
            this.test('秀搭类型不能为空');
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
        var arrType=[]
        $('.type_box li').each(function(index,val){
            arrType.push({'name':$(val).text().trim(),'id':val.dataset.id});
        });
        this.param.corp_code=this.theRequest.corp_code;
        this.param.d_match_title=$('#d_match_title').val().trim();
        this.param.d_match_type=arrType;
        this.param.d_match_image=img.join(',');
        this.param.d_match_desc=$('#describe').val().trim();
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
        this.start=0;
        $('#fab .search_null').hide();
        $('#wx .search_null').hide();
        $(window).unbind('scroll');
        //联通APP
        var toApp='chooseComplete';
        var remove={'removeView':'完成'};
        this.doAppWebRefresh(JSON.stringify(remove),toApp);
        $('#picture_box .picture').remove();
        var me=this;
        //save_match_goods中的r_match_goodsCode
        var arr_goodsCode=[];
        for(var i=0;i<this.save_match_goods.length;i++){arr_goodsCode.push(this.save_match_goods[i].r_match_goodsCode)}
        $('.list_box').find('.changeBcColor').each(function () {
            var r_match_goodsCode=$(this).find('.list_number').html();
            var r_match_goodsType=me.role;
            var r_match_goodsImage=$(this).find('img').attr('src');
            var  r_match_goodsPrice=$(this).find('.list_price').html();
            var   r_match_goodsName=$(this).find('.list_name').html();
            var productUrl=$(this).find('img').attr("productUrl");
            var shareUrl=$(this).find('img').attr("shareUrl");
            var use_id=me.theRequest.use_id
            var param={"r_match_goodsCode":r_match_goodsCode,
                        "r_match_goodsImage":r_match_goodsImage,
                        "r_match_goodsPrice":r_match_goodsPrice,
                        "r_match_goodsName":r_match_goodsName,
                        "productUrl":productUrl,
                        "shareUrl":shareUrl,
                        "r_match_goodsType":"wx",
                        "use_id":use_id
            };
            arr_goodsCode.indexOf(r_match_goodsCode)==-1&&(me.r_match_goods.push(param));
        });
        $('.list_box2').find('.changeBcColor').each(function () {
            var r_match_goodsCode=$(this).find('.list_number').html();
            var r_match_goodsType=me.role;
            var r_match_goodsImage=$(this).find('img').attr('src');
            var  r_match_goodsPrice=$(this).find('.list_price').html();
            var   r_match_goodsName=$(this).find('.list_name').html();
            var param={"r_match_goodsCode":r_match_goodsCode,
                "r_match_goodsImage":r_match_goodsImage,
                "r_match_goodsPrice":r_match_goodsPrice,
                "r_match_goodsName":r_match_goodsName,
                "r_match_goodsType":"fab"
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
             if(this.role=='wx'){
                 if(!this.next2){
                     return;
                 }
                 this.next2=false;
                 this.page++;
                 this.getList();
             }else if(this.role=='fab'){
                 if(!this.next){
                     return;
                 }
                 this.next=false;
                 //可能会有2个接口
                 if(this.fab_search){
                     this.fabSearch();
                 }else{
                     this.getList();
                 }
             }

        }
    },
    showImg:function (storeAs) {
        console.log(storeAs)
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
                    console.log('OK');
                    wdh=maxSize;
                    hgt=imgHeight/wRatio;
                    //向上移动
                    top=parseInt(-(hgt-maxSize)/2);
                    left=0;
                    console.log(top);
                }else if(hRatio<wRatio){
                    hgt=maxSize;
                    wdh=imgWidth/hRatio;
                    //向左移动
                    left=-(wdh-maxSize)/2;
                }
            }
            // var tempHTML = '<li class="picture"><img style="margin-top: '+top+'px;margin-left: '+left+'px" src="${storeAs}" width="'+wdh+'" alt="showImage " height="'+hgt+'"> <div class="picture_btn"></div> </li>';
            var tempHTML = '<li class="picture">' +
                '<img src="${storeAs}" width="'+wdh+'" alt="showImage " height="'+hgt+'">' +
                ' <div class="picture_btn"></div>' +
                '</li>';
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
        for(var i=this.save_match_goods.length-1;i>=0;i--){
            console.log( $(dom).find('img')[0].src==this.save_match_goods[i].r_match_goodsImage);
         $(dom).find('img')[0].src==this.save_match_goods[i].r_match_goodsImage&&(this.save_match_goods.splice(i,1));
        }
},
    getList:function() {
        $('#loading').show();
        if(this.role=='fab'){
            this.html='';
            var param={};
            param["rowno"]=this.rowno_fab*20;
            param["corp_code"]=this.theRequest.corp_code;
            param["user_id"]=this.theRequest.user_id;
            var  me=this;
            this.oc.postRequire("post","/api/fab/","0",param,function(data){
                if(data.code==0){
                    var list=JSON.parse(data.message).productList;
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                    var hasNextPage=list.hasNextPage;
                    me.rowno_fab++;
                    var list=list.list;
                    if(!hasNextPage){me.next = false;}else {me.next=true}
                    if (list.length <= 0) {
                        $('#fab .search_null').show();
                    } else {
                        $('#fab .search_null').hide();

                        for (var i = 0; i < list.length; i++) {
                            var imgUrl=list[i].goods_image?list[i].goods_image:'../mobile/image/goods_default_image.png';
                            me.html += '<li class="list"> <div class="list_picture"><img src="' + imgUrl+
                                '" alt=""/></div> <div class="list_describe"> <p class="list_name">' + list[i].goods_name +
                                '</p> <p>货号：<span class="list_number">' + list[i].goods_code +
                                '</span></p> <p>价格：<span class="list_price">￥' + list[i].goods_price +
                                '</span></p> </div> <p class="list_line"></p></li>';
                        }
                    }
                    $('.list_box2').append(me.html);
                    $(window).bind('scroll',me.scroll.bind(me));
                    $('#loading').hide();
                    //绑定
                }else if(data.code==-1){
                    alert(data.message);
                    $('#loading').hide();
                }
            });
        }else{
            this.html2='';
            var search_value='';
            if(this.wx_search_post=='wx'){ search_value=$('#wx .search_box input').val().trim();}
            var corp_code=this.theRequest.corp_code;
            var store_id=this.theRequest.store_id;
            var pageSize=10;
            var pageIndex=this.page;
            var categoryId=this.filtrateWx.join(',');
            var row_num=this.page==1?0:(this.page-1)*10;
            var productName=search_value;
            var user_id=this.theRequest.user_id;
            var me=this;
            // me.oc.postRequire("get","/api/shopMatch/getGoodsByWx?corp_code="+corp_code+"&pageSize="+pageSize+"&user_id="+user_id+"&pageIndex="+pageIndex+"&categoryId="+categoryId+"&row_num="+row_num+"&productName="+productName,'','',function(data){
            me.oc.postRequire("get","/api/shopMatch/getGoodsByWx?corp_code="+corp_code+"&pageSize="+pageSize+"&store_id="+store_id+"&user_id="+user_id+"&pageIndex="+pageIndex+"&categoryId="+categoryId+"&row_num="+row_num+"&productName="+productName,'','',function(data){
                if(data.code==0){
                    console.log(data);
                    var list=JSON.parse(data.message).productList;
                    if (list.length <= 0) {
                        me.page==1&&($('#wx .search_null').show());
                        // me.first2=true;
                        if (list.length == "0") {
                            me.next2= false;
                        }
                    } else {
                        $('#wx .search_null').hide();
                        for (var i = 0; i < list.length; i++) {
                            var imgUrl=list[i].imageUrl?list[i].imageUrl:'../mobile/image/goods_default_image.png';
                            me.html2 += '<li class="list"> <div class="list_picture"><img src="'+imgUrl
                            +'" alt="" productUrl="'+list[i].productUrl+'" shareUrl="'+list[i].shareUrl+'"/></div> <div class="list_describe"> <p class="list_name">' + list[i].productName +
                                '</p> <p>货号：<span class="list_number">' + list[i].productId +
                                '</span></p> <p>价格：<span class="list_price">￥' + list[i].price +
                                '</span></p> </div> <p class="list_line"></p></li>';
                        }
                        me.next2=true;
                    }
                    $('.list_box').append(me.html2);
                    $(window).bind('scroll',me.scroll.bind(me));
                    $('#loading').hide();
                    //绑定
                }else if(data.code==-1){
                    alert(data.message);
                    $('#loading').hide();
                }
            });
        }
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
            $('#d_match_title').val(val.d_match_title);
            $("#d_match_type").val(val.d_match_type);
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
            //秀搭类型
            var arrType=JSON.parse(val.d_match_type);
            var typeHTML=[];
            for(var i=0;i<arrType.length;i++){
                typeHTML.push('<li data-id="'+arrType[i].id+'">'+arrType[i].name+'<span><img width="100%"height="100%" src="image/icon_delete.png" alt=""/> </span></li>');
            }
            $('.type_box ul').html(typeHTML.join(''));
        })

     },
    returnToApp:function () {
        $(window).unbind('scroll');
        $('#fab .search_null').hide();
        $('#wx .search_null').hide();
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
            case 'label':window.webkit.messageHandlers.chooseLabel.postMessage(param);break;//label
        }
        // window.webkit.messageHandlers.NSCompleteAddXiuda.postMessage(param);
    }else if(osType == "Android"){
        switch (toApp){
            case 'choose': iShop.chooseProduct(param);break;//添加商品列表图片
            case 'chooseComplete': iShop.chooseComplete(param);break;//商品列表完成按钮
            case 'addComplete':iShop.addComplete(param);break;//新增商品完成按钮
            case 'returnAdd':iShop.returnAdd(param);break;//新增商品列表返回
            case 'localUpload':iShop.localUpload(param);break;//本地上传图片
            case 'label':iShop.chooseLabel(param);break;//label
        }
        // iShop.returnAddResult(param);
    }
}
    ,updateLabel:function(msg){
        //只能append 去重
        //var str='{"label":[{"name":"企业类型--我是孔德松（罗莱）","id":"49"},{"name":"机车","id":"45"},{"name":"小清新","id":"38"},{"name":"炫酷","id":"35"}]}'
        var str=msg;
        var arr=JSON.parse(str);
        arr=arr.label;
        var typeHTML=[];
        for(var i=0;i<arr.length;i++){
            //if(addProduct.chooseId.indexOf(String(arr[i].id))!=-1)continue
            typeHTML.push('<li data-id="'+arr[i].id+'">'+arr[i].name+'<span><img width="100%"height="100%" src="image/icon_delete.png" alt=""/> </span></li>');
        }
        //$('.type_box ul').append(typeHTML.join(''));
        $('.type_box ul').html(typeHTML.join(''));
    }
}
jQuery(function(){
    //完成时的操作
    addProduct.init();
    // addProduct.updateLabel();
    // addProduct.returnToApp()
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10141/ABC123-1483513648.jpg');
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10141/ABC123-1483513761.jpg');
    // addProduct.updateImage('https://products-image.oss-cn-hangzhou.aliyuncs.com/ShowImage/C10160/THSHRE1605001-1483532056.jpg');
});
