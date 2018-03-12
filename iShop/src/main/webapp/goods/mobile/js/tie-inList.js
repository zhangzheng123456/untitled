                                                                                           /**
 * Created by huxue on 2016/12/28.
 */
cache = {
    'recPage':'1',
    'myPage':'1',
    'chooseVal':'',
    'match':"",
    next:false,
    next2:false
}
var oc = new ObjectControl();
var corp_code = $.cookie('corp_code');
var pageSize = '20';
var user_code = $.cookie('user_code')
 $("#screen").click(function(){
      $("#guide").animate({right:'0'},300);
      $("#cover").show();
      $("html").addClass("sift-move");
      $("body").addClass("sift-move");
})
$("#cover").click(function(){
     $("#guide").animate({right:"-"+$("#guide").width()+"px"},300);
     $("#cover").hide();
     $("html").removeClass("sift-move");
     $("body").removeClass("sift-move");
})
$(".screen_content .list").on("click","ul li",function(e){
        e.stopPropagation();
        $(this).toggleClass("active");
})
$("#reset").click(function(){
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
$('.title .top').eq(0).click(function () {
    $('.main').eq(0).css('display','block');
    $('.main').eq(1).css('display','none');
    $('.title .top').eq(0).css('color','#6dc1c8');
    $('.title .top').eq(0).css('background-color','white');
    $('.title .top').eq(1).css('color','#888888');
    $('.title .top').eq(1).css('background-color','#ededed');
    $("#input").show();
    $("#input1").hide();
    $.cookie('action','0');
    cache.chooseVal ='rec';
    cache.Page ='1';
    var nowWidth = $('.main').eq(0).find('.the_img').width();
    $('.main').eq(0).find('.the_img').css('height',nowWidth);
    $('.main').eq(0).find('.the_img img').css('max-height',nowWidth);
});
//    选项卡-我的
$('.title .top').eq(1).click(function () {
    $('.main').eq(0).css('display','none');
    $('.main').eq(1).css('display','block');
    $('.title .top').eq(1).css('color','#6dc1c8');
    $('.title .top').eq(1).css('background-color','white');
    $('.title .top').eq(0).css('color','#888888');
    $('.title .top').eq(0).css('background-color','#ededed');
    $("#input").hide();
    $("#input1").show();
    $.cookie('action','1');
    cache.chooseVal ='my';
    cache.Page ='1';
    var nowWidth = $('.main').eq(1).find('.the_img').width();
    $('.main').eq(1).find('.the_img').css('height',nowWidth);
    $('.main').eq(1).find('.the_img img').css('max-height',nowWidth);;
});
//推荐列表
function getRec(){
    var type = 'rec';
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.recPage;
    var search_value=$("#input").val();
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"&search_value="+search_value+"&d_match_type="+cache.match+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            console.log(list);
            $('.div').eq(0).find('.out').remove();
            pageVal(list,type);
            if(list.length>0){
                cache.next=true;
            }
            if(list.length<0){
                cache.next=false;
            }
        }else if(data.code =='-1'){
            console.log(data);
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
//我的列表
function getMy(pageNumber){
    var type = 'my';
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.myPage;
    var search_value=$("#input1").val();
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" +corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"&search_value="+search_value+"&d_match_type="+cache.match+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            $('.div').eq(1).find('.out').remove();
            pageVal(list,type);
            if(list.length>0){
                cache.next2=true;
            }
            if(list.length<0){
                cache.next2=false;
            }
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//获取侧边类型
 function getGoodsTypeByCorp(){
     var corp_code=GetRequest().corp_code;
     oc.postRequire("get", "/api/shopMatch/shopTypeList?corp_code=" +corp_code +"", "0", "", function (data) {
         var message=JSON.parse(data.message);
         var html="";
         for(var i=0;i<message.length;i++){
             html+="<li>"+message[i]+"</li>"
         }
         $("#match_type .list ul").html(html);
     })
 }
//页面加载获取数据
function  pageVal(list,type){
    var screenHeight = $(window).height();
    var titleHeight = $('.title').height();
    var valHeight = screenHeight - titleHeight - 25;
    //如果拉取到的数据>=pageSize，更新缓存
    if(list.length >= pageSize){
            // cache.recPage+=1;
    }else{
        if(list.length == 0){
            console.log('length'+list.length);
            if(type == 'rec'&&cache.recPage=="1"){
                $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','block');
                return;
            }
            if(type == 'my'&&cache.myPage=="1"){
                $('.main').eq(1).css('height',valHeight);
                $('.main').eq(1).find('.none').css('display','block');
                return;
            }
        }else{
            if(type == 'rec'){
                $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','none');
            }
            if(type == 'my'){
                $('.main').eq(1).css('height',valHeight);
                $('.main').eq(1).find('.none').css('display','none');
            }
        }
        //替换模板
    }

    var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img src="${d_match_image}" alt=""/></li> <li class="the_list"> ';
    var tempHTML2 =' <img src="${r_match_goodsImage}" alt="" id="${r_match_goodsCode}"/>';
    var tempHTML3 =' <span class="num">${num}</span> </li> <li class="add"> <div><img src="${imgLick}" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="${imgSave}" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
    var html = ''
    console.log(list);
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
        var r_match_goods = list[i].r_match_goods;
        var nowHTML = tempHTML;
        nowHTML = nowHTML.replace('${d_match_code}',d_match_code);
        nowHTML = nowHTML.replace('${id}',id);
        nowHTML = nowHTML.replace('${d_match_title}',d_match_title);
        nowHTML = nowHTML.replace('${d_match_image}',d_match_image);
        html+= nowHTML;
        //小图标
        for(k=0;k<r_match_goods.length;k++){
            if(k<3){
                var r_match_goodsImage = r_match_goods[k].r_match_goodsImage;
                var r_match_goodsCode = r_match_goods[k].r_match_goodsCode;
                var nowHTML2 = tempHTML2;
                nowHTML2 = nowHTML2.replace('${r_match_goodsImage}',r_match_goodsImage);//列表图片
                nowHTML2 = nowHTML2.replace('${r_match_goodsCode}',r_match_goodsCode);//列表图片
                html += nowHTML2;
            }
        }
        var d_match_likeCount = list[i].d_match_likeCount;
        var d_match_commentCount = list[i].d_match_commentCount;
        var d_match_collectCount = list[i].d_match_collectCount;
        var nowHTML3 = tempHTML3;
        nowHTML3 = nowHTML3.replace('${num}',r_match_goods.length); //搭配商品件数
        nowHTML3 = nowHTML3.replace('${num}',d_match_likeCount); //点赞数
        nowHTML3 = nowHTML3.replace('${num}',d_match_commentCount); //评论数
        nowHTML3 = nowHTML3.replace('${num}',d_match_collectCount); //收藏数
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
    }
    if(type=='rec'){
        $('.main').eq(0).append(html);
    }else if(type =='my'){
        $('.main').eq(1).append(html);
    }
    var tempHTML4 =' <br/><div class="out">-- 已经是底部了 --</div>';
    if(type == 'rec'&&list.length == 0 ){
        $('.main').eq(0).append(tempHTML4);
    }else if(type== 'my'&&list.length == 0){
        $('.main').eq(1).append(tempHTML4);
    }
    toNext();
    click();
}
//监听滚动条，实现翻页
$(window).scroll( function() {
    var oneHeight =  $('.goods_box').height()+20;
    var chooseVal = cache.chooseVal;         //选项卡
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
   }else{
       console.log('未达到翻页高度')
   }

})
//控制宽高
function setTime(){
    var val =  $('.the_img').width();
    $('.the_img').css('height',val);
    $('.the_img img').css('height',val);
    var listVal = $('.the_list img').width();
    $('.the_list img').css('height',listVal);
    $('.the_list').css('height',listVal);
}
//点击事件-点赞-评论-收藏
function  click(){
    $('.add div img').unbind("click").bind("click",function () {
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
            param["url"]="http://"+host+"/goods/mobile/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+"&the_action="+action+'&type='+type;
            console.log(param);
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
                            console.log('error');
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
        var action =$.cookie('action');
        param["url"]="http://"+host+"/goods/mobile/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+'&the_action='+action;
        console.log(param);
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
       myScroll = new iScroll('guide', { checkDOMChanges: true });
       // setInterval(function () {
       // 	if (myScroll.isReady())
       // 		document.getElementById('thelist').innerHTML += '<li>new row</li>';
       // }, 2000);
 }
window.onload = function () {
    //$('.main').eq(0).find('.none').css('display','none');
    //默认
    var val = '';
    var val1  =  $.cookie('action');//页面存值
    var val2 = GetRequest().action;//url取值
    var repeat = $.cookie('repeat');//验证是否重复获取
    //alert(val2);
    //$('.title div').eq(0).text(val2);
    if(repeat == false && val2 == '1'){
        val ='1';
        $.cookie('repeat',true);
    }else{
        val = val1;
    }
    if(val == '0'||val==''|| val ==undefined){
        $('.title .top').eq(0).click();
    }else if(val=='1'){
        $('.title .top').eq(1).click();
    }
    //获取推荐
    getRec();
    //获取我的
    getMy();
    //控制宽高
    setTime();
    //评论跳转
    getGoodsTypeByCorp();
    loaded();
}
