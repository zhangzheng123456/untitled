                                                                                           /**
 * Created by huxue on 2016/12/28.
 */
cache = {
    'recPage':'1',
    'myPage':'1',
    'sharePage':'1',
    'cebian':'1',
    'chooseVal':'',
    'match':"",
    'sort_key':'shopCount',
    'sort_type':'-1',
    'share_sort_key':'shopCount',
    'share_sort_type':'-1',
    "type":"my",
    d_match_category:"",
    next:false,
    next2:false,
    next3:false
}
var oc = new ObjectControl();
var corp_code = $.cookie('corp_code');
var pageSize = '10';
var user_code = $.cookie('user_code');
 $("#screen").click(function(){
      $("#guide").animate({right:'0'},300);
      $("#cover").show();
      $("html").addClass("sift-move");
      $("body").addClass("sift-move");
      var osType = getWebOSType();
     if(osType == "Android"){
             iShop.jumpToWebViewForScroll("N");
     }

})
$("#cover").click(function(){
     $("#guide").animate({right:"-"+$("#guide").width()+"px"},300);
     $("#cover").hide();
     $("html").removeClass("sift-move");
     $("body").removeClass("sift-move");
     var osType = getWebOSType();
    if(osType == "Android"){
        if($("#"+cache.chooseVal).scrollTop()>0){
            iShop.jumpToWebViewForScroll("N");
        }else {
            iShop.jumpToWebViewForScroll("Y");
        }
    }
})
 //筛选
$(".screen_content #source").on("click","ul li",function(e){
        e.stopPropagation();
        cache.d_match_category=$(this).attr("data-type");
        $(this).toggleClass("active");
        if($(this).attr("class")==""){
            cache.d_match_category="";
        }
        $(this).siblings("li").attr("class","");
})
$(".screen_content #match_type").on("click","ul li",function(e){
        e.stopPropagation();
        $(this).toggleClass("active");
})
$("#reset").click(function(){
     cache.d_match_category="";
     $(".screen_content .list li").attr("class","");
})
 $("#complete").click(function(){
     $("#guide").animate({right:"-"+$("#guide").width()+"px"},300);
     $("#cover").hide();
     $("html").removeClass("sift-move");
     $("body").removeClass("sift-move");
     var match_type=$("#match_type .list .active");
     var match="";
     for(var i=0;i<match_type.length;i++){
         if(i<match_type.length-1){
             match+=$(match_type[i]).html()+",";
         }else {
             match+=$(match_type[i]).html()
         }
     }
     cache.match=match;
     if(cache.chooseVal =='rec'){
         $("#input").val("");
         cache.recPage=1;
         $('.main').eq(0).children("img").siblings().remove();
         getRec();
     }
     if(cache.chooseVal =='my'){
         $("#input1").val("");
         cache.myPage=1;
         $('.main').eq(1).children("img").siblings().remove();
         getMy();
     }
     if(cache.chooseVal =='share'){
         $("#input2").val("");
         cache.sharePage=1;
         $('.main').eq(2).children("img").siblings().remove();
         getShare();
     }
     var osType = getWebOSType();
     if(osType == "Android"){
         if($("#"+cache.chooseVal).scrollTop()>0){
             iShop.jumpToWebViewForScroll("N");
         }else {
             iShop.jumpToWebViewForScroll("Y");
         }
     }
 })
//获取？后缀
function GetRequest() {
    var url = decodeURI(location.search); //获取url中"?"符后的字串
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
//    选项卡-推荐
$('.title .top').click(function () {
    var index=$(this).index();
    $('.main').eq(index).show();
    $('.main').eq(index).siblings('.main').hide();
    $(this).css({"color":"#6dc1c8","backgroundColor":"white"});
    $(this).siblings(".top").css({"color":"#888","backgroundColor":"#e8e8e8"});
    $(".search form").eq(index).show();
    $(".search form").eq(index).siblings("form").hide();
    $(".tab_list .tab_list_left").eq(index).show();
    $(".tab_list .tab_list_left").eq(index).siblings(".tab_list_left").hide();
    $.cookie('action',index);
    cache.chooseVal =$(this).attr("data-type");
    var osType = getWebOSType();
    if(osType == "Android"){
        if($("#"+cache.chooseVal).scrollTop()>0){
            iShop.jumpToWebViewForScroll("N");
        }else {
            iShop.jumpToWebViewForScroll("Y");
        }
    }
});
$("#tab_list_rec li").click(function(){
    var param={};
    if($(this).attr("class")==""||$(this).attr("class")==undefined){
        $(this).attr("class","active");
        cache.sort_key=$(this).attr("data-type");
        cache.sort_type="-1";
    }else if($(this).attr("class")=="active"){
        $(this).attr("class","active rotateb");
        cache.sort_key=$(this).attr("data-type");
        cache.sort_type="1";
    }else if($(this).attr("class")=="active rotateb"){
        $(this).attr("class","active");
        cache.sort_key=$(this).attr("data-type");
        cache.sort_type="-1";
    }
    var index=$(this).index();
    param["sort_key"]=$(this).attr("data-type");
    param["sort_type"]=cache.sort_type;
    param["index"]=index;
    sessionStorage.setItem('state',JSON.stringify(param));
    $(this).siblings("li").attr("class","");
    $(window).unbind("scroll");
    $('.main').eq(0).children("img").siblings().remove();
    htmlArr=[];
    cache.recPage=1;
    getRec();
});
$("#tab_list_share li").click(function(){
    var param={};
    if($(this).attr("class")==""||$(this).attr("class")==undefined){
        $(this).attr("class","active");
        cache.share_sort_key=$(this).attr("data-type");
        cache.share_sort_type="-1";
    }else if($(this).attr("class")=="active"){
        $(this).attr("class","active rotateb");
        cache.share_sort_key=$(this).attr("data-type");
        cache.share_sort_type="1";
    }else if($(this).attr("class")=="active rotateb"){
        $(this).attr("class","active");
        cache.share_sort_key=$(this).attr("data-type");
        cache.share_sort_type="-1";
    }
    var index=$(this).index();
    param["sort_key"]=$(this).attr("data-type");
    param["sort_type"]=cache.share_sort_type;
    param["index"]=index;
    sessionStorage.setItem('share_state',JSON.stringify(param));
    $(this).siblings("li").attr("class","");
    $(window).unbind("scroll");
    $('.main').eq(2).children("img").siblings().remove();
    cache.sharePage=1;
    getShare();
});
$("#tab_list_my li").click(function () {
    var param={};
    var index=$(this).index();
    var type=$(this).attr("data-type");
    cache.type=type;
    param["index"]=index;
    param["type"]=type;
    $(this).addClass("active").siblings("li").removeClass("active");
    sessionStorage.setItem('my_state',JSON.stringify(param));
    $(window).unbind("scroll");
    $('.main').eq(1).children("img").siblings().remove();
    cache.myPage=1;
    getMy();
})
//推荐列表
function getRec(){
    var type = 'rec';
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.recPage;
    var search_value=$("#input").val();
    $("#loading").show();
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"&search_value="+search_value+"&d_match_type="+cache.match+"&sort_key="+cache.sort_key+"&sort_type="+cache.sort_type+"&d_match_category="+cache.d_match_category+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            $('.div').eq(0).find('.out').remove();
            pageVal(list,type);
            if(list.length>0){
                cache.next=true;
            }
            if(list.length<0){
                cache.next=false;
            }
        }else if(data.code =='-1'){

        }
    });
}
 //搜索推薦
 $("#input").keydown(function() {
     var event=window.event||arguments[0];
     if(event.keyCode == 13){
         cache.recPage=1;
         cache.match="";
         $('.main').eq(0).children("img").siblings().remove();
         getRec();
     }
 });
 //收縮我的
  $("#input1").keydown(function() {
      var event=window.event||arguments[0];
      if(event.keyCode == 13){
          cache.myPage=1;
          cache.match="";
          $('.main').eq(1).children("img").siblings().remove();
          getMy();
      }
  })
$("#input2").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        cache.sharePage=1;
        cache.match="";
        $('.main').eq(2).children("img").siblings().remove();
        getShare();
    }
})
$("#search").click(function(){
    if(cache.chooseVal =='rec'){
        cache.recPage=1;
        $('.main').eq(0).children("img").siblings().remove();
        getRec();
    }
    if(cache.chooseVal =='my'){
        cache.myPage=1;
        $('.main').eq(1).children("img").siblings().remove();
        getMy();
    }
    if(cache.chooseVal =='share'){
        cache.sharePage=1;
        $('.main').eq(2).children("img").siblings().remove();
        getShare();
    }
})
//我的列表
function getMy(pageNumber){
    var type =cache.type;
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.myPage;
    var search_value=$("#input1").val();
    $("#loading").show();
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" +corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"&search_value="+search_value+"&d_match_type="+cache.match+"&d_match_category="+cache.d_match_category+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var num=message.total;
            var list = message.list;
            $("#my_collect").html("("+message.collect_count+")");
            $("#my_creat").html("("+message.my_count+")");
            $('.div').eq(1).find('.out').remove();
            pageVal(list,"my");
            if(list.length>0){
                cache.next2=true;
            }
            if(list.length<0){
                cache.next2=false;
            }
        }else if(data.code =='-1'){
        }
    });
}
//我的分享
function getShare() {
    var param={};
    param["corp_code"]=GetRequest().corp_code;
    param["user_id"]=GetRequest().user_id;
    param["pageNumber"]=cache.sharePage;
    param["pageSize"]="20";
    param["sort_key"]=cache.share_sort_key;
    param["sort_type"]=cache.share_sort_type;
    param["search_value"]=$("#input2").val();
    param["d_match_type"]=cache.match;
    param["d_match_category"]=cache.d_match_category;
    $("#loading").show();
    oc.postRequire("post", "/api/shopMatch/getShopMatchShareLog", "0", param, function (data) {
        $("#loading").hide();
        var message=JSON.parse(data.message);
        var list=message.list;
        console.log(list);
        if(list.length==0&&cache.sharePage=="1"){
            $('.main').eq(2).find('.none').css('display','block');
        }
        if(list.length>0){
            $('.main').eq(2).find('.none').css('display','none');
            for(var i=0;i<list.length;i++){
                $("#share").append("<div class='share_list' isClick='"+list[i].isClick+"' shopMoney='"+list[i].shopMoney+"' shopCount='"+list[i].shopCount+"' data-detail='"+JSON.stringify(list[i].shop_match)+"' d_match_code='"+list[i].d_match_code+"'><div class='shop'><div class='img'><img src='"+list[i].shop_match.d_match_image.split(",")[0]
                    +"@400h_400w_1e_1c_1o'></div><div class='text'><p>"+list[i].shop_match.d_match_title
                    +"</p><div class='text_footer'><span>已售 "+list[i].shopCount
                    +"</span><span>  成交金额 "+list[i].shopMoney+"</span></div><div class='text_share'><span>浏览人数 "+list[i].visitors_count
                    +"</span></div><div class='text_select'><div></div>展开</div></div></div><div class='list_content'><div class='list_share'>"
                    +"<div class='icon_img'></div>"
                    +"我分享了<span class='blue'>"+list[i].shareCount+"</span>次"
                    +"<div class='share_content'>继续分享</div>"
                    +"</div><div class='list_look'>"
                    +"<div class='icon_img'></div>"
                    +"通过我的分享共有<span class='red'>"+list[i].visitors_count+"</span>人查看"
                    +"<div class='look_more'>查看更多</div>"
                    +"</div></div></div>")
            }
        }
    })
}
$("#share").on("click",".share_list .shop",function (e) {
    if($(e.target).is('.text_select down')||$(e.target).is('.text_select')||$(e.target).is('.text_select div')){
        return;
    }
    var d_match_code=$(this).parents(".share_list").attr("d_match_code");
    var host=window.location.host;
    var param={};
    //param["type"]="FAB";
    var corp_code = GetRequest().corp_code;
    var user_code = GetRequest().user_id;
    var store_id=GetRequest().store_id;
    var action =$.cookie('action');
    var shopMoney=$(this).parents(".share_list").attr("shopMoney");
    var shopcount=$(this).parents(".share_list").attr("shopcount");
    param["url"]="http://"+host+"/goods/mobile_v2/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+'&store_id='+store_id+'&the_action='+action+'&shopMoney='+shopMoney+'&shopcount='+shopcount+'&user_type=my';
    $.cookie('repeat',false);
    doAppWebRefresh(param);
})
$("#share").on("click",".share_list .text_select",function(){
    $(this).toggleClass("down");
    if($(this).attr("class")=="text_select"){
        $(this).html("<div></div>展开");
    }
    if($(this).attr("class")=="text_select down"){
        $(this).html("<div></div>收起");
    }
    $(this).parents(".share_list").find(".list_content").slideToggle();
});
$("#share").on("click",".share_list .share_content",function () {
    var host=window.location.host;
    var isClick=$(this).parents(".share_list").attr("isclick");
    if(isClick=="N"){
        $(".success .msg").html("该秀搭已被删除");
        $(".success").show();
        setTimeout(function () {
            $('.success').css('display','none');
        },1200);
        return;
    }
    var detail=JSON.parse($(this).parents(".share_list").attr("data-detail"));
    var share_param={};
    share_param["d_match_image_first"]=detail.d_match_image.split(",")[0];
    share_param["d_match_image_list"]=detail.d_match_image;
    share_param["d_match_desc"]=detail.d_match_desc;
    share_param["d_match_code"]=detail.d_match_code;
    share_param["d_match_title"]=detail.d_match_title;
    var host=window.location.host;
    var param={};
    var corp_code=GetRequest().corp_code;
    var store_id=GetRequest().store_id;
    var user_code=GetRequest().user_id;
    var d_match_code=$(this).parents(".share_list").attr("d_match_code");
    param["corp_code"]=corp_code;
    param["store_id"]=store_id;
    param["user_id"]=user_code;
    param["match_value"]="d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code+"&store_id="+store_id;
    $("#loading").show();
    oc.postRequire("post", "/api/getOpenId", "0",param, function (data) {
        $("#loading").hide();
        if(data.code=="0"){
            share_param["share_url"]=data.message;
            doAppWenShare(share_param);
        }else if(data.code=="-1"){
            share_param["share_url"]="http://"+host+"/goods/mobile_v2/details_share.html?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code+"&store_id="+store_id;
            doAppWenShare(share_param);
        }
    });
})
$("#share").on("click",".share_list .look_more",function () {
    var d_match_code=$(this).parents(".share_list").attr("d_match_code");
    var host=window.location.host;
    var param={};
    var corp_code = GetRequest().corp_code;
    var user_code = GetRequest().user_id;
    var store_id=GetRequest().store_id;
    var user_type="my";
    param["url"]="http://"+host+"/goods/mobile_v2/seen_people.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+'&store_id='+store_id+'&user_type='+user_type;
    doAppWebSeen(param);
})
//获取侧边类型
 function getGoodsTypeByCorp(){
     var param={};
     var corp_code=GetRequest().corp_code;
     var searchValue="";
     var pageSize=1000;
     param["corp_code"]=corp_code;
     param["searchValue"]=searchValue;
     param["pageSize"]=pageSize;
     param["pageNumber"]=cache.cebian;
     oc.postRequire("post","/api/shopMatch/type/searchByApp", "0",param, function (data) {
         var message=JSON.parse(data.message);
         var list=JSON.parse(message.list).list;
         var html="";
         for(var i=0;i<list.length;i++){
             html+="<li id='"+list[i].id+"'>"+list[i].shopmatch_type+"</li>";
         }
         $("#match_type .list ul").html(html);
     })
 };
//页面加载获取数据
function  pageVal(list,type){
    $(".out").remove();
    var screenHeight = $(window).height();
    var titleHeight = $('.title').height();
    var valHeight = screenHeight - titleHeight - 25;
    $("#loading").hide();
        if(list.length == 0){
            if(type == 'rec'&&cache.recPage=="1"){
                // $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','block');
                return;
            }
            if(type == 'my'&&cache.myPage=="1"){
                // $('.main').eq(1).css('height',valHeight);
                $('.main').eq(1).find('.none').css('display','block');
                return;
            }
        }else{
            if(type == 'rec'){
                // $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','none');
            }
            if(type == 'my'){
                // $('.main').eq(1).css('height',valHeight);
                $('.main').eq(1).find('.none').css('display','none');
            }
        }
        //替换模板
    // }

    var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img src="${d_match_image}" alt=""/></li> <li class="the_list"> ';
    var tempHTML2 =' <img src="${r_match_goodsImage}" alt="" id="${r_match_goodsCode}"/>';
    var tempHTML3 =' <span class="num">${num}</span> </li> <li class="add"> <div><img src="${imgLick}" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="${imgSave}" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
    var html = ''
    var imgWidth=parseFloat($('.main').css('width')) *0.465*0.97*0.27;
    for(var i=0;i<list.length;i++){
        var d_match_code = list[i].d_match_code;
        var id = list[i].id;
        var d_match_title = list[i].d_match_title;
        var d_match_image = list[i].d_match_image;
        var like_status = list[i].like_status; //点赞
        var collect_status = list[i].collect_status; //收藏
        var d_match_image_num = d_match_image.indexOf(",");
        //多张图取首张
        if(d_match_image_num>=0){
            d_match_image = d_match_image.substr(0,d_match_image_num);
        }
        var arr=list[i];
        (function(arr){
            var d_match_image = arr.d_match_image;
            var d_match_image_num = d_match_image.indexOf(",");
            //多张图取首张
            if(d_match_image_num>=0){
                d_match_image = d_match_image.substr(0,d_match_image_num);
            }
            // getSize(d_match_image,function(wdh,hgt,top,left){
                var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img  src="${d_match_image}" alt=""/><div class="img_top" style="${isShow}">企业</div><div class="xiao_t"><span class="left">${num}</span><span class="right">${shopCount}</span></div><div class="seen_num" style="${isShow}"><div class="seen_bg"></div> <span>$(shareCount)</span> </div> <div class="share_num"> <div class="share_bg"></div><span>$(pageViews)</span></div></li> <li class="the_list"> ';
                var tempHTML2 =' <img src="${r_match_goodsImage}" style="width:'+imgWidth+'px;height:'+imgWidth+'px" alt="" id="${r_match_goodsCode}"/>';
                var tempHTML3 ='</li> <li class="add" style="${isShow}"> <div><img src="${imgLick}" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="${imgSave}" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
                var html = '';
                var d_match_code =arr.d_match_code;
                var id = arr.id;
                var d_match_title =arr.d_match_title;
                var d_match_image = arr.d_match_image;
                var like_status = arr.like_status; //点赞
                var collect_status =arr.collect_status; //收藏
                var d_match_image_num = d_match_image.indexOf(",");
                //多张图取首张
                if(d_match_image_num>=0){
                    d_match_image = d_match_image.substr(0,d_match_image_num);
                }
                var r_match_goods = arr.r_match_goods;
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${d_match_code}',d_match_code);
                nowHTML = nowHTML.replace('${id}',id);
                nowHTML = nowHTML.replace('${d_match_title}',d_match_title);
                nowHTML = nowHTML.replace('${d_match_image}',d_match_image+"@800h_800w_1e_1c_1o");
                nowHTML=nowHTML.replace('${num}',r_match_goods.length+"件搭配");
                nowHTML=nowHTML.replace('${shopCount}',"销量:"+arr.shopCount);
                nowHTML=nowHTML.replace('$(pageViews)',arr.pageViews);
                nowHTML=nowHTML.replace('$(shareCount)',arr.shareCount);
                if(arr.d_match_category=="企业"){
                    nowHTML=nowHTML.replace('${isShow}',"display:block");
                }
                html+= nowHTML;
                for(var k=0;k<r_match_goods.length;k++){
                    if(k<3){
                        var r_match_goodsImage = r_match_goods[k].r_match_goodsImage;
                        var r_match_goodsCode = r_match_goods[k].r_match_goodsCode;
                        var nowHTML2 = tempHTML2;
                        nowHTML2 = nowHTML2.replace('${r_match_goodsImage}',r_match_goodsImage+"?x-oss-process=image/resize,m_fill,h_130,w_130,auto-orient,0");//列表图片
                        nowHTML2 = nowHTML2.replace('${r_match_goodsCode}',r_match_goodsCode);//列表图片
                        html += nowHTML2;
                    }
                }
                var d_match_likeCount = arr.d_match_likeCount;
                var d_match_commentCount = arr.d_match_commentCount;
                var d_match_collectCount = arr.d_match_collectCount;
                var nowHTML3 = tempHTML3;
                nowHTML3 = nowHTML3.replace('${num}',d_match_likeCount); //点赞数
                nowHTML3 = nowHTML3.replace('${num}',d_match_commentCount); //评论数
                nowHTML3 = nowHTML3.replace('${num}',d_match_collectCount); //收藏数
                if(type=="rec"){
                    nowHTML3 = nowHTML3.replace('${isShow}',"display:none");
                }
                if(type=="my"){
                    nowHTML3 = nowHTML3.replace('${isShow}',"display:none");
                }
                if(like_status=='Y'){
                    nowHTML3 = nowHTML3.replace('${imgLick}','image/icon_点赞_已点赞@2x.png');
                }else if(like_status=='N'){
                    nowHTML3 = nowHTML3.replace('${imgLick}','image/icon_点赞@2x.png');
                }
                if(collect_status=='Y'){
                    nowHTML3 = nowHTML3.replace('${imgSave}','image/icon_收藏_已收藏@2x.png');
                }else if(collect_status=='N'){
                    nowHTML3 = nowHTML3.replace('${imgSave}','image/icon_收藏@2x.png');
                }
                html += nowHTML3;
                if(type=='rec'){
                        $('.main').eq(0).append(html);
                }else if(type =='my'){
                        $('.main').eq(1).append(html);

                    }
                toNext();
                click();
        })(arr);
    }
    var tempHTML4 ='<div class="out">-- 已经是底部了 --</div>';
    if(type == 'rec'&&list.length == 0 ){
        $(tempHTML4);
        $('.main').eq(0).append(tempHTML4);
    }else if(type== 'my'&&list.length == 0){
        $('.main').eq(1).append(tempHTML4);
    }
    toNext();
    click();
    //监听滚动条，实现翻页
    $(window).unbind().bind("scroll",function() {
        var chooseVal = cache.chooseVal;//选项卡
        var oneHeight =  $('.goods_box').height()+20;
        var bot = 50; //bot是底部距离的高度
        if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
            if(chooseVal=='rec'){
                if(!cache.next){
                    return;
                }
                cache.next=false;
                cache.recPage++;
                getRec();
            }
            if(chooseVal=='my'){
                if(!cache.next2){
                    return;
                }
                cache.next2=false;
                cache.myPage++;
                getMy();
            }
            if(chooseVal=='share'){
                if(!cache.next3){
                    return;
                }
                cache.next3=false;
                cache.sharePage++;
                getShare();
            }
        }else{

        }
    })
}
 $(".main").scroll(function(){
     var osType = getWebOSType();
     if(osType == "Android"){
         if($(this).scrollTop()>0){
             iShop.jumpToWebViewForScroll("N");
         }else {
             iShop.jumpToWebViewForScroll("Y");
         }
     }
 })
//点击事件-点赞-评论-收藏
function  click(){
    $('.add div img').unbind("click").bind("click",function () {
        var div=$(this);
        var id=$(this).parents(".main").attr("id");
        var src = $(this).attr("src");
        var corp_code = GetRequest().corp_code;
        var user_code=GetRequest().user_id;
        var operate_userCode = GetRequest().user_id;; //操作人user_code
        var d_match_code = $(this).parents('.goods_box').attr('id');  //秀搭编号
        var operate_type = '';//type
        var comment_text = '';//评论内容
        var status = '';  //是否点赞or收藏
        if(src =='image/icon_点赞@2x.png'){
            $(this).attr('src','image/icon_点赞_已点赞@2x.png');
            //var val = $(this).next('.add_num').text();
            //$(this).next('.add_num').text(parseInt(val)+1);
            operate_type = 'like';
            comment_text='';
            status = 'Y';
        }else if (src =='image/icon_点赞_已点赞@2x.png'){
            $(this).attr('src','image/icon_点赞@2x.png');
            //var val = $(this).next('.add_num').text();
            //$(this).next('.add_num').text(parseInt(val)-1);
            operate_type = 'dislike';
            comment_text='';
            status = 'N';
        }
        //    评论
        if(src =='image/icon_评论@2x.png'){
            //operate_type = 'comment';
            //comment_text='';  //暂无内容暂无内容暂无内容暂无内容暂无内容
            //status = '';
            var d_match_code = $(this).parents('.goods_box').attr('id');
            var host=window.location.host;
            var param={};
            var corp_code = GetRequest().corp_code;
            var user_code = GetRequest().user_id;
            var type = 'comments';
            var action =$.cookie('action');
            param["url"]="http://"+host+"/goods/mobile_v2/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+"&the_action="+action+'&type='+type;
            $.cookie('repeat',true);
            doAppWebRefresh(param);
            return;
        }
        //    收藏
        if(src =='image/icon_收藏@2x.png'){
            $(this).attr('src','image/icon_收藏_已收藏@2x.png');
            //var val = $(this).next('.add_num').text();
            //$(this).next('.add_num').text(parseInt(val)+1);
            operate_type = 'collect';
            comment_text='';
            status = 'Y';
        }else if (src =='image/icon_收藏_已收藏@2x.png'){
            $(this).attr('src','image/icon_收藏@2x.png');
            //var val = $(this).next('.add_num').text();
            //$(this).next('.add_num').text(parseInt(val)-1);
            operate_type = 'discollect';
            comment_text='';
            status = 'N';
        }
        var param={};
        param["user_code"]=GetRequest().user_id;
        param["corp_code"]=GetRequest().corp_code;
        param["d_match_code"]=d_match_code;
        param["operate_userCode"]=operate_userCode;
        param["operate_type"]=operate_type;
        param["status"]=status;
        param["comment_text"]=comment_text;
        oc.postRequire("post","/api/shopMatch/addRelByType","0",param,function(data){
            if (data.code == "0") {
                var message = data.message;
                if(id=="rec"){
                    $('#my .goods_box').each(function () {
                        var id = $(this).attr('id')
                        if(id == d_match_code){
                            if(operate_type=='like'||operate_type=='dislike'){
                                if($(this).find('.add div').eq(0).find('img').attr('src')=='image/icon_点赞@2x.png'){
                                    $(this).find('.add div').eq(0).find('img').attr('src','image/icon_点赞_已点赞@2x.png');
                                }else if ($(this).find('.add div').eq(0).find('img').attr('src')=='image/icon_点赞_已点赞@2x.png'){
                                    $(this).find('.add div').eq(0).find('img').attr('src','image/icon_点赞@2x.png');
                                }
                            }else if(operate_type=='collect'||operate_type=='discollect'){
                                if($(this).find('.add div').eq(2).find('img').attr('src')=='image/icon_收藏@2x.png'){
                                    $(this).find('.add div').eq(2).find('img').attr('src','image/icon_收藏_已收藏@2x.png');

                                }else if ($(this).find('.add div').eq(2).find('img').attr('src')=='image/icon_收藏_已收藏@2x.png'){
                                    $(this).find('.add div').eq(2).find('img').attr('src','image/icon_收藏@2x.png');
                                }
                            }else if(operate_type=='comment'){
                                $(this).find('.add div').eq(1).find('.add_num').text(message);
                            }else{
                            }
                        }
                    });
                };
                if(id=="my"){
                    $('#rec .goods_box').each(function () {
                        var id = $(this).attr('id')
                        if(id == d_match_code){
                            if(operate_type=='like'||operate_type=='dislike'){
                                if($(this).find('.add div').eq(0).find('img').attr('src')=='image/icon_点赞@2x.png'){
                                    $(this).find('.add div').eq(0).find('img').attr('src','image/icon_点赞_已点赞@2x.png');
                                }else if ($(this).find('.add div').eq(0).find('img').attr('src')=='image/icon_点赞_已点赞@2x.png'){
                                    $(this).find('.add div').eq(0).find('img').attr('src','image/icon_点赞@2x.png');
                                }
                            }else if(operate_type=='collect'||operate_type=='discollect'){
                                if($(this).find('.add div').eq(2).find('img').attr('src')=='image/icon_收藏@2x.png'){
                                    $(this).find('.add div').eq(2).find('img').attr('src','image/icon_收藏_已收藏@2x.png');

                                }else if ($(this).find('.add div').eq(2).find('img').attr('src')=='image/icon_收藏_已收藏@2x.png'){
                                    $(this).find('.add div').eq(2).find('img').attr('src','image/icon_收藏@2x.png');
                                }
                            }else if(operate_type=='comment'){
                                $(this).find('.add div').eq(1).find('.add_num').text(message);
                            }else{
                            }
                        }
                    });
                }
                $('.goods_box').each(function () {
                    var id = $(this).attr('id')
                    if(id == d_match_code){
                        if(operate_type=='like'||operate_type=='dislike'){
                            $(this).find('.add div').eq(0).find('.add_num').text(message);
                        }else if(operate_type=='collect'||operate_type=='discollect'){
                            $(this).find('.add div').eq(2).find('.add_num').text(message);
                        }else if(operate_type=='comment'){
                            $(this).find('.add div').eq(1).find('.add_num').text(message);
                        }else{

                        }
                    }
                })

            }else if(data.code =='-1'){
                //alert(data);
            }
        })
    });
}
//正常跳转
function toNext(){
    $('.the_img img').unbind("click").bind('click',function () {
        var d_match_code  = $(this).parents('.goods_box').attr('id');
        var host=window.location.host;
        var param={};
        //param["type"]="FAB";
        var corp_code = GetRequest().corp_code;
        var user_code = GetRequest().user_id;
        var store_id=GetRequest().store_id;
        var action =$.cookie('action');
        param["url"]="http://"+host+"/goods/mobile_v2/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+'&store_id='+store_id+'&the_action='+action;
        $.cookie('repeat',false);
        doAppWebRefresh(param);
        //window.location = 'details.html?d_match_code'+d_match_code;
    });
    //$('.the_list img').click(function () {
    //    window.location = 'details.html';
    //})
}
//获取手机系统
function getWebOSType() {
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
function doAppWebRefresh(param){
     var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        window.webkit.messageHandlers.NSJumpToWebViewForWeb.postMessage(param);
    }else if(osType == "Android"){
        //iShop.returnAddResult(param);
        iShop.jumpToWebViewForWeb(param);
    }
}
//调用APP方法传参 param 格式 type：** ;url:**
function doAppWebSeen(param){
    var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        window.webkit.messageHandlers.NSJumpToWebViewForPeople.postMessage(param);
    }else if(osType == "Android"){
       iShop.jumpToWebViewForPeople(param);
    }
}
function doAppWenShare(param) {
    var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        try{
            window.webkit.messageHandlers.NSreturnShareInfo.postMessage(param);
        } catch(err){
            //returnShareInfo(param);
        }
    }else if(osType == "Android"){
        iShop.returnShareInfo(param);
    }
}
function doAppWebHeaderRefresh(param){
    if(param=="headerRefresh"){
        $(".header_line").css({"position":"absolute"});
        $(".title").css({"position":"absolute"});
    }else{
        $(".header_line").css({"position":"fixed"});
        $(".title").css({"position":"fixed"});
    }
}
var myScroll;
function loaded() {
       myScroll = new iScroll('guide', {
           checkDOMChanges: true,
           onScrollEnd: function(){
           }
       });
 }
window.onload = function () {
    var val = '';
    var val1  =  $.cookie('action');//页面存值
    var val2 = GetRequest().action;//url取值
    var repeat = $.cookie('repeat');//验证是否重复获取
    var state=JSON.parse(sessionStorage.getItem('state'));
    var share_state=JSON.parse(sessionStorage.getItem('share_state'));
    var my_state=JSON.parse(sessionStorage.getItem('my_state'));
    if(repeat == false && val2 == '1'){
        val ='1';
        $.cookie('repeat',true);
    }else{
        val = val1;
    }
    if(val == '0'||val==''|| val ==undefined){
        $('.title .top').eq(0).click();
    }else {
        $('.title .top').eq(val).click();
    }
    //获取推荐
    if(state!==null){
        cache.sort_key=state.sort_key;
        cache.sort_type=state.sort_type;
        if(state.sort_type=="-1"){
            $("#tab_list_rec li").eq(state.index).attr("class","active");
            $("#tab_list_rec li").eq(state.index).siblings("li").attr("class","");
        }
        if(state.sort_type=="1"){
            $("#tab_list_rec li").eq(state.index).attr("class","active rotateb");
            $("#tab_list_rec li").eq(state.index).siblings("li").attr("class","");
        }
    }
    if(share_state!==null){
        cache.share_sort_key=share_state.sort_key;
        cache.share_sort_type=share_state.sort_type;
        if(share_state.sort_type=="-1"){
            $("#tab_list_share li").eq(share_state.index).attr("class","active");
            $("#tab_list_share li").eq(share_state.index).siblings("li").attr("class","");
        }
        if(share_state.sort_type=="1"){
            $("#tab_list_share li").eq(share_state.index).attr("class","active rotateb");
            $("#tab_list_share li").eq(share_state.index).siblings("li").attr("class","");
        }
    }
    if(my_state!==null){
        cache.type=my_state.type;
        $("#tab_list_my li").eq(my_state.index).attr("class","active");
        $("#tab_list_my li").eq(my_state.index).siblings("li").attr("class","");
    }
    getRec();
    getMy();
    getGoodsTypeByCorp();
    getShare();
    loaded();
}

