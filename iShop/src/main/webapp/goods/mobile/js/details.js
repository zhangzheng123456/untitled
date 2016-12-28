/**
 * Created by huxue on 2016/12/28.
 */
var oc = new ObjectControl();
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
//    点赞-收藏-评论
$('.bottom div img').click(function () {
    var src = $(this).attr("src");
    var corp_code = 'C10000';
    var d_match_code = '10000';  //秀搭编号
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
        operate_type = 'like';
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
        operate_type = 'collect';
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
            alert(data);
        }
    })
});
window.onload = function () {
    setInterval(function () {
        var heightVal = document.body.clientWidth;
        $('.main_img').css('height',heightVal);
        $('.main_img img').css('height',heightVal);
        var imgHeight = $('.main_list_main img').height();
        $('.main_list_main img').css('width',imgHeight);
    },1);
    $('.main_select div').eq(0).click();
}