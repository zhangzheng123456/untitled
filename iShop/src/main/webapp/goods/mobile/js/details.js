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
            setDocumentTitle(d_match_title)
            console.log('秀搭名称:'+ d_match_title)
            //主图-多张图取首张
            var d_match_image = message.d_match_image;
            var imgArr = d_match_image.split();
            var tempHTML = '<div class="item"> <img src="${img}" alt=""> </div>';
            var tempHTML2 = ' <li data-target="#carousel-example-generic" data-slide-to="${num}"></li>'
            var html = '';
            var html2 = '';
            for(i=0;i<imgArr.length;i++){
                var nowHTML = tempHTML;
                var nowHTML2 = tempHTML2;
                nowHTML = nowHTML.replace('${img}',imgArr[i]);
                nowHTML2 = nowHTML2.replace('${img}',imgArr[i]);
                html+=nowHTML;
                html2+=nowHTML2
                $('.carousel-inner').html(html);
                $('.carousel-indicators').html(html2);
                $('.carousel-inner div').eq(0).addClass('active');
                $('.carousel-indicators li').eq(0).addClass('active');
            }
            if(imgArr.length =='1'){
                $('.carousel-control').css('display','none')
                $('.carousel-indicators').css('display','none')
            }else{
                $('.carousel-control').css('display','block')
                $('.carousel-indicators').css('display','block')
            }
            //var imgNum = d_match_image.indexOf(",");
            //if(imgNum>=0){
            //    d_match_image = d_match_image.substr(0,d_match_image_num);
            //}
            //首张图更改
            //$('.carousel-inner div').eq(i).find('img').('src',d_match_image);
            //秀搭简介
            var d_match_desc = message.d_match_desc;
            console.log('秀搭简介:'+ d_match_desc)
            $('.main_content').eq(0).find('div').html(d_match_desc)
            //评论暂无
            var like_status = message.like_status; //点赞
            var collect_status = message.collect_status;//收藏
            console.log('点赞'+like_status+'收藏'+collect_status)
            var d_match_likeCount = message.d_match_likeCount;
            var d_match_commentCount = message.d_match_commentCount;
            var d_match_collectCount = message.d_match_collectCount;
            var r_match_goods = message.r_match_goods;
            $('.main_list_title .num').text(r_match_goods.length);
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
function getConmments(){
    var param={};
    param["pageNumber"]='0';
    param["corp_code"]=GetRequest().corp_code;
    param["d_match_code"]=GetRequest().d_match_code;
    param["pageSize"]='20';
    oc.postRequire("post","/api/shopMatch/commentList","0",param,function(data){
        if (data.code == "0") {
            console.log(data)
            var message = JSON.parse(data.message);
            var list = message.list;
            for(i=0;i<list.length;i++){
                var img = ''
                var name = list[i].operate_userName;
                var time = list[i].created_date;
                var msg = list[i].comment_text
                conmmentsVal(img,name,time,msg)
            }
        } else if(data.code =='-1'){
            //alert(data);
        }
    })
}
function getConmmentsVal(){
    console.log('评论显示')
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
        //$(this).next('.num').text(parseInt(val)+1);
        operate_type = 'like';
        comment_text='';
        status = 'Y';
    }else if (src =='image/icon_点赞_已点赞@2x.png'){
        $(this).attr('src','image/icon_点赞@2x.png');
        var val = $(this).next('.num').text();
        //$(this).next('.num').text(parseInt(val)-1);
        operate_type = 'dislike';
        comment_text='';
        status = 'N';
    }
    //    评论
    if(src =='image/icon_评论@2x.png'){
        operate_type = 'comment';
        comment_text='';  //暂无内容暂无内容暂无内容暂无内容暂无内容
        status = '';
        return;
    }
    //    收藏
    if(src =='image/icon_收藏@2x.png'){
        $(this).attr('src','image/icon_收藏_已收藏@2x.png');
        var val = $(this).next('.num').text();
        //$(this).next('.num').text(parseInt(val)+1);
        operate_type = 'collect';
        comment_text='';
        status = 'Y';
    }else if (src =='image/icon_收藏_已收藏@2x.png'){
        $(this).attr('src','image/icon_收藏@2x.png');
        var val = $(this).next('.num').text();
        //$(this).next('.num').text(parseInt(val)-1);
        operate_type = 'discollect';
        comment_text='';
        status = 'N';
    }
    var param={};
    param["d_match_code"]=GetRequest().d_match_code;
    param["corp_code"]=GetRequest().corp_code;
    param["user_code"]=GetRequest().user_id;
    param["operate_userCode"]=GetRequest().user_id;
    param["operate_type"]=operate_type;
    param["status"]=status;
    param["comment_text"]=comment_text;
    oc.postRequire("post","/api/shopMatch/addRelByType","0",param,function(data){
        if (data.code == "0") {
            var message = data.message;
            if(operate_type=='like'||operate_type=='dislike'){
                $('.bottom div').eq(0).find('.num').text(message);
            }else if(operate_type=='collect'||operate_type=='discollect'){
                $('.bottom div').eq(1).find('.num').text(message);
            }else if(operate_type=='comment'){
                $('.bottom div').eq(2).find('.num').text(message);
            }else{
                console.log('error');
            }
        }else if(data.code =='-1'){
            //alert(data);
        }
    })
});
//底部评论按钮点击切换
$('.bottom div').eq(2).find('img').unbind('click').bind('click',function () {
    $('.bottom').toggle();
    $('.bottom_input').toggle();
    $('.main_select div').eq(1).click();
    $("body").scrollTop($("body")[0].scrollHeight);
});
$('.main_content').unbind('click').bind('click',function () {
    $('.bottom').toggle();
    $('.bottom_input').toggle();
});
//提交评论
function setConmments(){
    var msg = $('.bottom_input input').val();
    var time = getNowFormatDate();
    var name = GetRequest().user_id;
    var img = 'image/img_kong.png'
    if(msg.trim()!=''){
        var param={};
        param["corp_code"]=GetRequest().corp_code;
        param["d_match_code"]=GetRequest().d_match_code;
        param["operate_userCode"]=GetRequest().user_id;
        param["operate_type"]='comment';
        param["comment_text"]=msg;
        oc.postRequire("post","/api/shopMatch/addRelByType","0",param,function(data){
            if((data.code =='0')){
                $('.main_content').eq(1).find('.box').remove();
                getConmments();
            }else if(data.code =='-1'){
            }
        })
    }else{
        console.log('不可发送空消息');
    }
}
//评论显示
function conmmentsVal(img,name,time,msg){
    var tempHTML = '<li class="box"> <div class="top"> <img src="${img}" alt=""/> <div class="title"> <span>${name}</span> <span>${time}</span> </div> </div> <div class="msg">${msg}</div> </li>'
    var html ='';
    var nowHTML = tempHTML;
    if(img==''){
        img = 'image/head_none.png';
    }
    nowHTML = nowHTML.replace('${img}',img)
    nowHTML = nowHTML.replace('${name}',name)
    nowHTML = nowHTML.replace('${time}',time)
    nowHTML = nowHTML.replace('${msg}',msg)
    html+=nowHTML;
    $('.area').append(html);
    $("body").scrollTop($("body")[0].scrollHeight);
}
$('#send').click(function(){
    setConmments();
    $('.bottom').toggle();
    $('.bottom_input').toggle();
    //$('.bottom_input input').val('');
});
//获取当前时间
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + date.getMinutes()
        + seperator2 + date.getSeconds();
    return currentdate;
}
//点击编辑
$('.editor').unbind("click").bind('click',function () {
    var host=window.location.host;
    var param={};
    //var str="d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code;
    param["url"]="http://"+host+"/goods/mobile/add_new.html?d_match_code=" + d_match_code +"&corp_code=" + corp_code+"&user_id="+user_code;
    console.log(param);
    doAppWebEditor(param);
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
function doAppWebDelete(param){
    var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        console.log('删除ios')
        window.webkit.messageHandlers.NSJumpToWebDelete.postMessage(param);
    }else if(osType == "Android"){
        //iShop.returnAddResult(param);
        iShop.jumpToWebViewForWebDelete(param);
    }
}
function doAppWebEditor(param){
    var param=JSON.stringify(param);
    var osType = this.getWebOSType();
    if(osType=="iOS"){
        console.log('删除ios')
        window.webkit.messageHandlers.NSJumpToWebEditor.postMessage(param);
    }else if(osType == "Android"){
        //iShop.returnAddResult(param);
        iShop.jumpToWebViewForWebEditor(param);
    }
}
//删除
$('.delete').click(function () {
    var val = confirm('是否删除');
    if(val==true){
        deleteAction();
    }else{

    }
});
function deleteAction(){
    oc.postRequire("get", "/api/shopMatch/delete?corp_code=" + corp_code +"&d_match_code=" + d_match_code+"", "0", "", function (data) {
        if (data.code == "0") {
            console.log('删除成功');
            var host=window.location.host;
            var param={};
            param["result"]="success";
            console.log(param);
            doAppWebDelete(param);
        }else if(data.code =='-1'){
            console.log(data);
            param["result"]= "failed";
            doAppWebDelete(param);
        }
    });
}
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
//ios修改title
function setDocumentTitle(d_match_title) {
    document.title = d_match_title;
    if (/ip(hone|od|ad)/i.test(navigator.userAgent)) {
        var i = document.createElement('iframe');
        i.src = 'baidu.com';
        i.style.display = 'none';
        i.onload = function() {
            setTimeout(function(){
                i.remove();
            }, 9)
        }
        document.body.appendChild(i);
    }
}
//qq表情
$(function () {
    $('.bottom_input img').qqFace({
        assign: 'input', //给输入框赋值
        path: 'image/face/' //表情图片存放的路径
    });
});
//替换成图片
function replace_em(str) {
    str = str.replace(/\[em_([0-9]*)\]/g, '<img src="img/face/$1.gif" border="0" class="qq_face"/>');
    return str;
}
//emoji表情
function emojito(content){
    content = content.replace(/(\[.+?\])/g, '<img src="img/face/expression_$1@2x.png" style="width:73px"/>');
    return content
}
window.onload = function () {
    //拉取页面
    getPage();
    //拉取评论
    getConmments();
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