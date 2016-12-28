/**
 * Created by huxue on 2016/12/28.
 */
var oc = new ObjectControl();
var corp_code = 'C10000';
var pageNumber = '0';
var pageSize = '';
var user_code = '';

//    选项卡-推荐
$('.title div').eq(0).click(function () {
    $('.main').eq(0).css('display','block');
    $('.main').eq(1).css('display','none');
    $('.title div').eq(0).css('color','#6dc1c8');
    $('.title div').eq(0).css('background-color','white');
    $('.title div').eq(1).css('color','#888888');
    $('.title div').eq(1).css('background-color','#ededed');
    var type = 'rec';
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code + "pageNumber" + pageNumber + "pageSize" + pageSize+"user_code"+user_code+"type"+type+"", "0", "", function (data) {
        if (data.code == "0") {
            console.log(data);
        }
    });
    //var num = '5';
    //pageVal(num);
});
//    选项卡-我的
$('.title div').eq(1).click(function () {
    $('.main').eq(0).css('display','none');
    $('.main').eq(1).css('display','block');
    $('.title div').eq(1).css('color','#6dc1c8');
    $('.title div').eq(1).css('background-color','white');
    $('.title div').eq(0).css('color','#888888');
    $('.title div').eq(0).css('background-color','#ededed');
    var type = 'my';
    oc.postRequire("get", "/api/shopMatch/list?corp_code=" + corp_code + "pageNumber" + pageNumber + "pageSize" + pageSize+"user_code"+user_code+"type"+type+"", "0", "", function (data) {
        if (data.code == "0") {
            console.log(data);
        }
    });
});

//    页面加载获取数据
function pageVal(num){
    var tempHTML = '<ul class="goods_box" id="${id}" title="${id}"> <li class="the_img"><img src="${img}" alt=""/></li> <li class="the_list"> <img src="${img}" alt=""/> <img src="${img}" alt=""/> <img src="${img}" alt=""/> <span class="num">${num}</span> </li> <li class="add"> <div><img src="image/icon_点赞@2x.png" alt="点赞"/><span class="add_num">${num}</span></div> <div><img src="image/icon_评论@2x.png" alt="评论"/><span class="add_num">${num}</span></div> <div><img src="image/icon_收藏@2x.png" alt="收藏"/><span class="add_num">${num}</span></div> </li> </ul>';
    var html = ''
    for(i=0;i<10;i++){
        var nowHTML = tempHTML;
        nowHTML = nowHTML.replace('${img}','image/list_item_00.jpg');
        nowHTML = nowHTML.replace('${img}','image/list_item_01.jpg');
        nowHTML = nowHTML.replace('${img}','image/list_item_02.jpg');
        nowHTML = nowHTML.replace('${img}','image/list_item_02.jpg');
        nowHTML = nowHTML.replace('${num}',num);
        nowHTML = nowHTML.replace('${num}',num);
        nowHTML = nowHTML.replace('${num}',num);
        nowHTML = nowHTML.replace('${num}',num);
        nowHTML = nowHTML.replace('${id}','id_'+i);
        nowHTML = nowHTML.replace('${id}','id_'+i);
        html+= nowHTML;
        $('.main').eq(0).html(html);
        if(i<2){
            $('.main').eq(1).html(html);
        }
    }
}
//   控制宽高
function setTime(){
    var val =  $('.the_img').width();
    $('.the_img').css('height',val);
    $('.the_img img').css('height',val);
    var listVal = $('.the_list img').width();
    $('.the_list img').css('height',listVal);
    $('.the_list').css('height',listVal);
}

window.onload = function () {
    //默认推荐
    $('.title div').eq(0).click();
    setTime()
    $('.add div img').click(function () {
        var src = $(this).attr("src");
        //    点赞
        if(src =='image/icon_点赞@2x.png'){
            $(this).attr('src','image/icon_点赞_已点赞@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)+1);
        }else if (src =='image/icon_点赞_已点赞@2x.png'){
            $(this).attr('src','image/icon_点赞@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)-1);
        }
        //    评论
        if(src =='image/icon_评论@2x.png'){
            alert('暂无该功能')
        }
        //    收藏
        if(src =='image/icon_收藏@2x.png'){
            $(this).attr('src','image/icon_收藏_已收藏@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)+1);
        }else if (src =='image/icon_收藏_已收藏@2x.png'){
            $(this).attr('src','image/icon_收藏@2x.png');
            var val = $(this).next('.add_num').text();
            $(this).next('.add_num').text(parseInt(val)-1);
        }
    });
    //跳转
    $('.the_img img').click(function () {
        window.location = 'details.html';
    })
    $('.the_list img').click(function () {
        window.location = 'details.html';
    })
}
//交互
//oc.postRequire("post", _command,"",_params, function(data){
//    if(data.code=="0"){
//        if(_command=="/defmatch/addMatch"){
//            var message=JSON.parse(data.message);
//            sessionStorage.setItem("goods_match_code",message.goods_match_code);
//            sessionStorage.setItem("corp_code",message.corp_code);
//            $(window.parent.document).find('#iframepage').attr("src", "/goods/fab_matchEditor.html");
//        }
//        if(_command=="/goods/fab/edit"){
//            art.dialog({
//                time: 2,
//                lock: true,
//                cancel: false,
//                content:"保存成功"
//            });
//            window.location.reload();
//        }
//    }else if(data.code=="-1"){
//        art.dialog({
//            time: 1,
//            lock:true,
//            cancel: false,
//            content: data.message
//        });
//    }
//    whir.loading.remove();//移除加载框
//});