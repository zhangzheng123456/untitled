/**
 * Created by huxue on 2016/12/28.
 */
var oc = new ObjectControl();
var user_code = $.cookie('user_code');
var corp_code = $.cookie('corp_code');
var d_match_code = $.cookie('d_match_code');

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
   var d_match_code = $.cookie('d_match_code');
    oc.postRequire("get", "/api/shopMatch/selectById?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_code="+user_code+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var d_match_image = message.d_match_image;
            var d_match_image_num = d_match_image.indexOf(",");
            //多张图取首张
            if(d_match_image_num>=0){
                d_match_image = d_match_image.substr(0,d_match_image_num);
            }
            var like_status = message.like_status; //点赞
            var collect_status = message.collect_status;//收藏
            var d_match_likeCount = message.d_match_likeCount;
            var d_match_commentCount = message.d_match_commentCount;
            var d_match_collectCount = message.d_match_collectCount;
           $('.main_img #mainImg').attr('src',d_match_image);
            var r_match_goods = message.r_match_goods;
            var tempHTML = '<img src="${img}" alt=""/>';
            var html='';
            for(i=0;i<r_match_goods.length;i++){
                var r_match_goodsImage = r_match_goods[i].r_match_goodsImage;
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${img}',r_match_goodsImage);
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
    var corp_code = 'C10000';
    var operate_userCode = '10000'; //操作人user_code
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
    param["corp_code"]=corp_code;
    param["d_match_code"]=d_match_code;
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
    var d_match_code = $.cookie('d_match_code');
    var host=window.location.host;
    var param={};
    var str="d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_code="+user_code;
    param["url"]="http://"+host+"/goods/mobile/details.html?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code;
    doAppWebRefresh(param);
    str=encodeURIComponent(str);
    window.location = "add_new.html?"+str;
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
            window.location = "tie-inList.html";
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
});
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