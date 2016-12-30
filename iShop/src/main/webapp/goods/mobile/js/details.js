/**
 * Created by huxue on 2016/12/28.
 */
var oc = new ObjectControl();
var d_match_code =  GetRequest().d_match_code;
var corp_code =  GetRequest().corp_code;
var user_code =  GetRequest().user_id;
//    选项卡
$('.main_select div').click(function () {
    $('.main_select div').css('background-color','#ededed');
    $(this).css('background-color','white');
})
$('.main_select div').eq(0).click(function () {
    $('.main_content').eq(0).css('display','block');
    $('.main_content').eq(1).css('display','none');
});
$('.main_select div').eq(1).click(function () {
    $('.main_content').eq(1).css('display','block');
    $('.main_content').eq(0).css('display','none');
});
function getPage(){
   var d_match_code =  GetRequest().d_match_code;
   var corp_code =  GetRequest().corp_code;
   var user_code =  GetRequest().user_id;
    oc.postRequire("get", "/api/shopMatch/selectById?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_code="+user_code+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            //搭配名称
            var d_match_title = message.d_match_title;
            $(document).attr("title",d_match_title);
            //主图-多张图取首张
            var d_match_image = message.d_match_image;
            var d_match_image_num = d_match_image.indexOf(",");
            if(d_match_image_num>=0){
                d_match_image = d_match_image.substr(0,d_match_image_num);
            }
            //秀搭简介
            var d_match_desc = message.d_match_desc;
            $('.main_content').eq(0).html(d_match_desc)
            //评论
            var like_status = message.like_status; //点赞
            var collect_status = message.collect_status;//收藏
            console.log('点赞'+like_status+'收藏'+collect_status)
            var d_match_likeCount = message.d_match_likeCount;
            var d_match_commentCount = message.d_match_commentCount;
            var d_match_collectCount = message.d_match_collectCount;
            //小图更改
           $('.main_img #mainImg').attr('src',d_match_image);
            var r_match_goods = message.r_match_goods;
            var tempHTML = '<img src="${img}" alt="" id="${id}" title="${title}"/>';
            var html='';
            for(i=0;i<r_match_goods.length;i++){
                var r_match_goodsImage = r_match_goods[i].r_match_goodsImage;
                var r_match_goodsCode = r_match_goods[i].r_match_goodsCode;
                var r_match_goodsName = r_match_goods[i].r_match_goodsName;
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${img}',r_match_goodsImage);
                nowHTML = nowHTML.replace('${id}',r_match_goodsCode);
                nowHTML = nowHTML.replace('${title}',r_match_goodsName);
                html+=nowHTML;
                $('.main_list_main').html(html);
            }
            if(like_status == 'Y'){
                $('.bottom div').eq(0).find('img').attr('src','image/icon_点赞_已点赞@2x.png');
            }else if(like_status =='N'){
                $('.bottom div').eq(0).find('img').attr('src','image/icon_点赞@2x.png');
            }
            if(collect_status=='Y'){
                $('.bottom div').eq(1).find('img').attr('src','image/icon_收藏_已收藏@2x.png');
            }else if(collect_status=='N'){
                $('.bottom div').eq(1).find('img').attr('src','image/icon_收藏@2x.png');
            }
            $('.bottom div').eq(0).find('.num').text(d_match_likeCount);
            $('.bottom div').eq(2).find('.num').text(d_match_commentCount);
            $('.bottom div').eq(1).find('.num').text(d_match_collectCount);
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//    点赞-收藏-评论
$('.bottom div img').click(function () {
    var src = $(this).attr("src");
    var corp_code = corp_code;
    var operate_userCode = user_code ; //操作人user_code
    var user_code=GetRequest().user_id;
    var operate_type = '';//type
    var comment_text = '';//评论内容
    var status = '';  //是否点赞or收藏
    if(src =='image/icon_点赞@2x.png'){
        $(this).attr('src','image/icon_点赞_已点赞@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)+1);
        operate_type = 'like';
        comment_text='';
        status = 'Y';
    }else if (src =='image/icon_点赞_已点赞@2x.png'){
        $(this).attr('src','image/icon_点赞@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)-1);
        operate_type = 'dislike';
        comment_text='';
        status = 'N';
    }
    //    评论
    if(src =='image/icon_评论@2x.png'){
        alert('暂无该功能')
        operate_type = 'comment';
        comment_text='';  //暂无内容暂无内容暂无内容暂无内容暂无内容
        status = '';
    }
    //    收藏
    if(src =='image/icon_收藏@2x.png'){
        $(this).attr('src','image/icon_收藏_已收藏@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)+1);
        operate_type = 'collect';
        comment_text='';
        status = 'Y';
    }else if (src =='image/icon_收藏_已收藏@2x.png'){
        $(this).attr('src','image/icon_收藏@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)-1);
        operate_type = 'discollect';
        comment_text='';
        status = 'N';
    }
    var param={};
    param["d_match_code"]=d_match_code;
    param["corp_code"]=corp_code;
    param["user_code"]=user_code;
    param["operate_userCode"]=operate_userCode;
    param["operate_type"]=operate_type;
    param["status"]=status;
    param["comment_text"]=comment_text;
    oc.postRequire("post","/api/shopMatch/addRelByType","0",param,function(data){
        if (data.code == "0") {
            console.log(data);
            //pageVal(num);
        }else if(data.code =='-1'){
            //alert(data);
        }
    })
});
//点击编辑
$('.editor').unbind("click").bind('click',function () {
    var host=window.location.host;
    var param={};
    var str="d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code;
    param["url"]="http://"+host+"/goods/mobile/add_new.html?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code;
    console.log(param);
    doAppWebRefresh(param);
    //str=encodeURIComponent(str);
    //window.location = "add_new.html?"+str;
});

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
//删除
$('.delete').click(function () {
    oc.postRequire("get", "/api/shopMatch/delete?corp_code=" + corp_code +"&d_match_code=" + d_match_code+"", "0", "", function (data) {
        if (data.code == "0") {
            console.log('删除成功');
            window.location = "tie-inList.html?corp_code="+corp_code+'&user_id='+user_code;
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
});
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
window.onload = function () {
    getPage();
    setInterval(function () {
        var heightVal = document.body.clientWidth;
        $('.main_img').css('height',heightVal);
        $('.main_img img').css('width',heightVal);
        $('.main_img img').css('max-height',heightVal);
        var imgHeight = $('.main_list_main img').height();
        $('.main_list_main img').css('width',imgHeight);
    },1);
    $('.main_select div').eq(0).click();
}