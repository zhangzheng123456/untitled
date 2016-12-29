/**
 * Created by huxue on 2016/12/28.
 */
var oc = new ObjectControl();
var corp_code = 'C10000';
var pageNumber = '1';
var pageSize = '20';
var user_code = '10000';

//    选项卡-推荐
$('.title div').eq(0).click(function () {
    $('.main').eq(0).css('display','block');
    $('.main').eq(1).css('display','none');
    $('.title div').eq(0).css('color','#6dc1c8');
    $('.title div').eq(0).css('background-color','white');
    $('.title div').eq(1).css('color','#888888');
    $('.title div').eq(1).css('background-color','#ededed');
});
//    选项卡-我的
$('.title div').eq(1).click(function () {
    $('.main').eq(0).css('display','none');
    $('.main').eq(1).css('display','block');
    $('.title div').eq(1).css('color','#6dc1c8');
    $('.title div').eq(1).css('background-color','white');
    $('.title div').eq(0).css('color','#888888');
    $('.title div').eq(0).css('background-color','#ededed');
});
//推荐列表
function getRec(){
    var type = 'rec';
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            pageVal(list,type);
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//我的列表
function getMy(){
    var type = 'my';
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" +corp_code +"&pageNumber=" + pageNumber + "&pageSize=" + pageSize+"&user_code="+user_code+"&type="+type+"", "0", "", function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            pageVal(list,type);
        }else if(data.code =='-1'){
            console.log(data);
        }
    });
}
//页面加载获取数据
function  pageVal(list,type){
    var tempHTML = '<ul class="goods_box" id="${d_match_code}" title="${d_match_title}" > <li class="the_img" id="${id}"><img src="${d_match_image}" alt=""/></li> <li class="the_list"> ';
    var tempHTML2 =' <img src="${r_match_goodsImage}" alt="" id="${r_match_goodsCode}"/>';
    var tempHTML3 =' <span class="num">${num}</span> </li> <li class="add"> <div><img src="image/icon_点赞@2x.png" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="image/icon_收藏@2x.png" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
    var html = ''
    for(i=0;i<list.length;i++){
        var d_match_code = list[i].d_match_code;
        var id = list[i].id;
        var d_match_title = list[i].d_match_title;
        var d_match_image = list[i].d_match_image;
        var d_match_image_num = d_match_image.indexOf(",");
        console.log(d_match_image_num)
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
        html += nowHTML3;
        if(type=='rec'){
            $('.main').eq(0).html(html);
        }else if(type =='my'){
            $('.main').eq(1).html(html);
        }
        var like_status = list[i].like_status; //点赞
        var collect_status = list[i].collect_status; //收藏
        if(like_status=='Y'){
            $('.add div img').eq(0).attr('src','image/icon_点赞_已点赞@2x.png');
        }else if(like_status=='N'){
            $('.add div img').eq(0).attr('src','image/icon_点赞@2x.png');
        }
        if(collect_status=='Y'){
            $('.add div img').eq(2).attr('src','image/icon_收藏_已收藏@2x.png');
        }else if(collect_status=='N'){
            $('.add div img').eq(2).attr('src','image/icon_收藏@2x.png');
        }
    }
    toNext();
    click();
}
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
        var corp_code = 'C10000';
        var d_match_code = $(this).parents('.goods_box').attr('id');  //秀搭编号
        var operate_userCode = '10000'; //操作人user_code
        var operate_type = '';//type
        var comment_text = '';//评论内容
        var status = '';  //是否点赞or收藏
        var status
        if(src =='image/icon_点赞@2x.png'){
            $(this).attr('src','image/icon_点赞_已点赞@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)+1);
            operate_type = 'like';
            comment_text='';
            status = 'Y';
        }else if (src =='image/icon_点赞_已点赞@2x.png'){
            $(this).attr('src','image/icon_点赞@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)-1);
            operate_type = 'dislike';
            comment_text='';
            status = 'N';
        }
        //    评论
        if(src =='image/icon_评论@2x.png'){
            alert('暂无该功能')
            //operate_type = 'comment';
            //comment_text='';  //暂无内容暂无内容暂无内容暂无内容暂无内容
            //status = '';
        }
        //    收藏
        if(src =='image/icon_收藏@2x.png'){
            $(this).attr('src','image/icon_收藏_已收藏@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)+1);
            operate_type = 'collect';
            comment_text='';
            status = 'Y';
        }else if (src =='image/icon_收藏_已收藏@2x.png'){
            $(this).attr('src','image/icon_收藏@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)-1);
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
                console.log('调用事件执行')
                console.log(data);
                //pageVal(num);
            }else if(data.code =='-1'){
                //alert(data);
            }
        })
    });
}
//跳转
function toNext(){
    $('.the_img img').click(function () {
        var d_match_code  = $(this).parents('.goods_box').attr('id');
        $.cookie('d_match_code',d_match_code);
        window.location = 'details.html';
    })
    //$('.the_list img').click(function () {
    //    window.location = 'details.html';
    //})
}
window.onload = function () {
    //默认推荐
    $('.title div').eq(0).click();
    //控制宽高
    setTime();
    //获取推荐
    getRec();
    //获取我的
    getMy();
}
//var src = $(this).attr("src");
////    点赞
//if(src =='image/icon_点赞@2x.png'){
//    $(this).attr('src','image/icon_点赞_已点赞@2x.png');
//    var val = $(this).next('.add_num').text();
//    $(this).next('.add_num').text(parseInt(val)+1);
//}else if (src =='image/icon_点赞_已点赞@2x.png'){
//    $(this).attr('src','image/icon_点赞@2x.png');
//    var val = $(this).next('.add_num').text();
//    $(this).next('.add_num').text(parseInt(val)-1);
//}
////    评论
//if(src =='image/icon_评论@2x.png'){
//    alert('暂无该功能')
//}
////    收藏
//if(src =='image/icon_收藏@2x.png'){
//    $(this).attr('src','image/icon_收藏_已收藏@2x.png');
//    var val = $(this).next('.add_num').text();
//    $(this).next('.add_num').text(parseInt(val)+1);
//}else if (src =='image/icon_收藏_已收藏@2x.png'){
//    $(this).attr('src','image/icon_收藏@2x.png');
//    var val = $(this).next('.add_num').text();
//    $(this).next('.add_num').text(parseInt(val)-1);
//}