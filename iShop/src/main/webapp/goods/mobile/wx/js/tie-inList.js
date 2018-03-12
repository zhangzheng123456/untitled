                                                                                           /**
 * Created by huxue on 2016/12/28.
 */
var htmlGress=0;//进度系数
var htmlMyGress=0;//进度系数
var htmlArr=[];//进度页面
var htmlMyArr=[]
cache = {
    'recPage':'1',
    'myPage':'1',
    'cebian':'1',
    'chooseVal':'',
    'match':"",
    next:false,
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
     var osType = getWebOSType();
    if(osType == "Android"){
        if($("#"+cache.chooseVal).scrollTop()>0){
            iShop.jumpToWebViewForScroll("N");
        }else {
            iShop.jumpToWebViewForScroll("Y");
        }
    }
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
     getRec();
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
//推荐列表
function getRec(){
    var type = 'wx_show';
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.recPage;
    var search_value=$("#input").val();
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"&search_value="+search_value+"&d_match_type="+cache.match+"", "0", "", function (data) {
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
         console.log(list);
         for(var i=0;i<list.length;i++){
             html+="<li id='"+list[i].id+"'>"+list[i].shopmatch_type+"</li>";
         }
         $("#match_type .list ul").html(html);
     })
 };
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
            if(cache.recPage=="1"){
                $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','block');
                return;
            }
        }else{
            $('.main').eq(0).css('height',valHeight);
            $('.main').eq(0).find('.none').css('display','none');
        }
        //替换模板
    }
    var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img src="${d_match_image}" alt="" data-userid="${userid}"/></li> <li class="the_list"> ';
    var tempHTML2 =' <img src="${r_match_goodsImage}" alt="" id="${r_match_goodsCode}"/>';
    var tempHTML3 =' <span class="num">${num}</span> </li></ul>';
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
            getSize(d_match_image,function(wdh,hgt,top,left){
                var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img style=" margin-top:'+top+'px;margin-left:'+left+'px;width:'+wdh+'px;height:'+hgt+'px" src="${d_match_image}" alt="" data-userid="${userid}"/></li> <li class="the_list"> ';
                var tempHTML2 =' <img src="${r_match_goodsImage}" style="width:'+imgWidth+'px;height:'+imgWidth+'px" alt="" id="${r_match_goodsCode}" data-userid="${userid}"/>';
                var tempHTML3 =' <span class="num">${num}</span> </li></ul>';
                var html = '';
                var d_match_code =arr.d_match_code;
                var id = arr.id;
                var d_match_title =arr.d_match_title;
                var d_match_image = arr.d_match_image;
                var like_status = arr.like_status; //点赞
                var collect_status =arr.collect_status; //收藏
                var d_match_image_num = d_match_image.indexOf(",");
                var userid=arr.creater;
                //多张图取首张
                if(d_match_image_num>=0){
                    d_match_image = d_match_image.substr(0,d_match_image_num);
                }
                var r_match_goods = arr.r_match_goods;
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${d_match_code}',d_match_code);
                nowHTML = nowHTML.replace('${id}',id);
                nowHTML = nowHTML.replace('${d_match_title}',d_match_title);
                nowHTML = nowHTML.replace('${d_match_image}',d_match_image);
                nowHTML = nowHTML.replace('${userid}',userid);//列表图片
                html+= nowHTML;
                for(var k=0;k<r_match_goods.length;k++){
                    if(k<3){
                        var r_match_goodsImage = r_match_goods[k].r_match_goodsImage;
                        var r_match_goodsCode = r_match_goods[k].r_match_goodsCode;

                        var nowHTML2 = tempHTML2;
                        nowHTML2 = nowHTML2.replace('${r_match_goodsImage}',r_match_goodsImage);//列表图片
                        nowHTML2 = nowHTML2.replace('${r_match_goodsCode}',r_match_goodsCode);//列表图片
                        html += nowHTML2;
                    }
                }
                var d_match_likeCount = arr.d_match_likeCount;
                var d_match_commentCount = arr.d_match_commentCount;
                var d_match_collectCount = arr.d_match_collectCount;
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

                    htmlGress++;
                    htmlArr.push({'name':html,'num':d_match_likeCount});
                    if( htmlGress>=list.length){
                        htmlArr.sort(function(a,b){
                            if(a.num<b.num){
                                return 1;
                            }else if(a.num>b.num){
                                return -1;
                            }
                            return 0;
                        })
                        var htmlNew= htmlArr.map(function(val){
                            return val.name
                        });
                        $('.main').eq(0).append(htmlNew.join(''));
                        htmlArr=[];
                        htmlGress=0;
                    }
                toNext();
                click();

            });
        })(arr);
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
    var chooseVal = cache.chooseVal;//选项卡
    var oneHeight =  $('.goods_box').height()+20;
    var bot = 50; //bot是底部距离的高度
    if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
           if(!cache.next){
               return;
           }
           cache.next=false;
           cache.recPage++;
           getRec();
   }else{

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
        var user_code = $(this).attr("data-userid");
        var action =$.cookie('action');
        var url="/goods/mobile/wx/details_share.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code+'&the_action='+action;
        window.location = url;
    });
    //$('.the_list img').click(function () {
    //    window.location = 'details.html';
    //})
}
var myScroll;
function loaded() {
       myScroll = new iScroll('guide', {
           checkDOMChanges: true,
           onScrollEnd: function(){
               console.log(123123);
           }
       });
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
    //获取推荐
    getRec();
    //控制宽高
    setTime();
    //评论跳转
    // getGoodsTypeByCorp();
    loaded();
}
 var boxWidth=parseFloat($('.the_img').css('width'))||(parseFloat($('html').css('fontSize'))*4.63768);
 var boxHeight=parseFloat($('.the_img').css('height'))||(parseFloat($('html').css('fontSize'))*4.63768);
function getSize(storeAs,fun){
    var wdh='';
    var hgt='';
    var top='';
    var left='';
    var imgWidth='';
    var imgHeight='';
    var image = new Image();
    image.src = storeAs;
    image.onload = function(){
        //获取容器的最大尺寸
        //var boxWidth=parseFloat($('.the_img').css('width'))||(parseFloat($('html').css('fontSize'))*4.63768);
        //var boxHeight=parseFloat($('.the_img').css('height'))||(parseFloat($('html').css('fontSize'))*4.63768);
        imgWidth=image.width;
        imgHeight=image.height;
        var hRatio=imgHeight/boxHeight;//高度缩放比例
        var wRatio=imgWidth/boxWidth;//宽度缩放比例
        if (image.width < boxWidth&& image.height <boxHeight) {//宽高放
            //值小为准
            if(hRatio>=wRatio){//高度比列大 以宽度缩放比例为准
                wdh=boxWidth;
                hgt=imgHeight/wRatio;
                //不需要位移
                top=0;
                left=0;
            }else if(hRatio<wRatio){//宽度比例大 以高度比例为准
                hgt=boxHeight;
                wdh=imgWidth/hRatio;
                //不需要位移
                top=0;
                left=0;
            }

        }else if(image.width <boxWidth && image.height > boxHeight){//高度缩
            //宽度放为准
            wdh=boxWidth;
            hgt=imgHeight/wRatio;
            top=-parseInt((hgt- boxHeight)/2);
            left=0;
        }else if(image.width > boxWidth&& image.height < boxHeight){//宽度缩
            //高度放为准
            hgt=boxHeight;
            wdh=imgWidth/hRatio;
            //宽度方向位移即 向左移动
            left=-(wdh-boxWidth)/2;
            top=0;
        }else if(imgWidth>boxWidth&& imgHeight>boxHeight){
            //值大为准
            if(hRatio>=wRatio){//高度比列大 以宽度缩比例为准
                wdh=boxWidth;
                hgt=imgHeight/wRatio;
                //高度方向为转移即向上移动
                top=-parseInt((hgt-boxHeight)/2);
                left=0;
            }else if(hRatio<wRatio){//宽度比例大 以高度缩比例为准
                hgt=boxHeight;
                wdh=imgWidth/hRatio;
                //宽度方向位移即 向左移动
                left=-parseFloat((wdh-boxWidth)/2);
                top=0;
            }
        }
        fun(wdh,hgt,top,left)
    }

}
