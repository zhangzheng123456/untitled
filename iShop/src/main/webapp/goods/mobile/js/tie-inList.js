/**
 * Created by huxue on 2016/12/28.
 */
cache = {
    'Page':'1',
    'chooseVal':''
}
var oc = new ObjectControl();
var corp_code = $.cookie('corp_code');
var pageSize = '20';
var user_code = $.cookie('user_code');
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
$('.title div').eq(0).click(function () {
    $('.main').eq(0).css('display','block');
    $('.main').eq(1).css('display','none');
    $('.title div').eq(0).css('color','#6dc1c8');
    $('.title div').eq(0).css('background-color','white');
    $('.title div').eq(1).css('color','#888888');
    $('.title div').eq(1).css('background-color','#ededed');
    $.cookie('action','0');
    cache.chooseVal ='rec';
    cache.Page ='1';
    var nowWidth = $('.main').eq(0).find('.the_img').width();
    $('.main').eq(0).find('.the_img').css('height',nowWidth);
    $('.main').eq(0).find('.the_img img').css('max-height',nowWidth);
});
//    选项卡-我的
$('.title div').eq(1).click(function () {
    $('.main').eq(0).css('display','none');
    $('.main').eq(1).css('display','block');
    $('.title div').eq(1).css('color','#6dc1c8');
    $('.title div').eq(1).css('background-color','white');
    $('.title div').eq(0).css('color','#888888');
    $('.title div').eq(0).css('background-color','#ededed');
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
    var pageNumber = cache.Page;
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            $('.div').eq(0).find('.out').remove();
            pageVal(list,type);
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//我的列表
function getMy(pageNumber){
    var type = 'my';
    var urlMsg = GetRequest();
    var user_code = urlMsg.user_id;
    var corp_code = urlMsg.corp_code;
    var pageNumber = cache.Page;
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" +corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            $('.div').eq(1).find('.out').remove();
            pageVal(list,type);
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//页面加载获取数据
function  pageVal(list,type){
    var screenHeight = $(window).height();
    var titleHeight = $('.title').height();
    var valHeight = screenHeight - titleHeight - 25;
    //如果拉取到的数据>=pageSize，更新缓存
    if(list.length >= pageSize){
            cache.recPage+=1;
    }else{
        if(list.length == 0){
            if(type == 'rec'){
                console.log('rec')
                $('.main').eq(0).css('height',valHeight);
                $('.main').eq(0).find('.none').css('display','block');
                return;
            }
            if(type == 'my'){
                console.log('my');
                $('.main').eq(1).css('height',valHeight);
                $('.main').eq(1).find('.none').css('display','block');
                return;
            }
        }else{
            $('.none').css('display','none');
        }
        //替换模板
    }

    var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img src="${d_match_image}" alt=""/></li> <li class="the_list"> ';
    var tempHTML2 =' <img src="${r_match_goodsImage}" alt="" id="${r_match_goodsCode}"/>';
    var tempHTML3 =' <span class="num">${num}</span> </li> <li class="add"> <div><img src="${imgLick}" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="${imgSave}" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
    var html = ''
    for(i=0;i<list.length;i++){
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
        console.log('点赞'+like_status+'收藏'+collect_status);
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
            $('.main').eq(0).html(html);
        }else if(type =='my'){
            $('.main').eq(1).html(html);
        }
    }
    var tempHTML4 =' <br/><div class="out">-- 已经是底部了 --</div>';
    if(type == 'rec'){
        $('.main').eq(0).append(tempHTML4);
    }else if(type== 'my'){
        $('.main').eq(1).append(tempHTML4);
    }
    toNext();
    click();
}
//监听滚动条，实现翻页
$(window).scroll( function() {
    var oneHeight =  $('.goods_box').height()+20;
    var pageNum = cache.recPage;
    var allHeight = oneHeight *10;           //必须翻页高度
    var nowHeight = $(window).scrollTop();   //滚动条高度
    var chooseVal = cache.chooseVal;         //选项卡
   if(nowHeight>allHeight*pageNum-200){
       console.log('请求数据')
       if(chooseVal=='rec'){
           getRec();
           console.log('推荐翻页');
       }
       if(chooseVal=='my'){
           getMy();
           console.log('我的翻页');
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
            console.log(123);
            var d_match_code = $(this).parents('.goods_box').attr('id');
            var corp_code = GetRequest().corp_code;
            var user_code = GetRequest().user_id;
            var action = 'comments';
            window. location.href = "details.html?d_match_code=" + d_match_code +"&user_id="+user_code+"&corp_code="+corp_code+"&the_action="+action;
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
//跳转
function toNext(){
    $('.the_img img').unbind("click").bind('click',function () {
        var d_match_code  = $(this).parents('.goods_box').attr('id');
        var host=window.location.host;
        var param={};
        //param["type"]="FAB";
        var corp_code = GetRequest().corp_code;
        var user_code = GetRequest().user_id;
        param["url"]="http://"+host+"/goods/mobile/details.html?d_match_code=" + d_match_code +'&user_id='+user_code+'&corp_code='+corp_code;
        console.log(param);
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

window.onload = function () {
    $('.main').eq(0).find('.none').css('display','none');
    //默认
    var val =  $.cookie('action');
    //$('.title div').eq(0).text(val);
    if(val == '0'||val==''|| val ==undefined){
        $('.title div').eq(0).click();
    }else if(val=='1'){
        $('.title div').eq(1).click();
    }
    //获取推荐
    getRec();
    //获取我的
    getMy();
    //控制宽高
    setTime();
    //评论跳转

}
