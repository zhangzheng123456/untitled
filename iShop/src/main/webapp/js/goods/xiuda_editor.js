/**
 * Created by Administrator on 20170519/1/16.
 */
var oc = new ObjectControl();
var swip_image = [];
//记录选项卡行为
cache = {
    'chooseType':'',
    'corp':'',
    "searchType":"NAME",
    "brand_code":""
};
(function(root,factory){
    root.fab = factory();
}(this,function(){
    var fabjs={};
    fabjs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    fabjs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    fabjs.checkNumber=function(obj,hint){
        var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
        if(!this.isEmpty(obj)){
            if(isCode.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"请输入数字！");
                return false;
            }
        }else{
            this.displayHint(hint);
            return false;
        }
    }
    fabjs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    fabjs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    fabjs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    //保存功能
    fabjs.bindbutton=function(){
        $(".fabadd_oper_btn ul li:nth-of-type(1)").click(function(){
            if(fabjs.firstStep()){
                var corp_code = $('.searchable-select-items .selected').attr('data-value');
                console.log('企业编号是'+corp_code);
                var d_match_title  = $('.the_listTitle').eq(1).find('input').val();
                var d_match_image = '';
                $('.list_content').eq(0).find('.item_box').each(function () {
                    d_match_image +=$(this).find('img').attr('src')+',';
                });
                d_match_image = d_match_image.substring(0,d_match_image.length-1);
                console.log('搭配图片'+d_match_image);
                var d_match_desc = $('#d_match_desc').val();
                var r_match_num = $('.list_content').eq(1).find('.item_box').length; //搭配商品长度
                var r_match_goods = [];
                var obj = {};
                var goodsBox = $('.list_content').eq(1).find('.item_box');
                var isactive="";
                var input=$("#is_active")[0];
                  var keepType=[]
            //秀搭类型
            $('.show_type .li').each(function(index, el) {
                keepType.push({'name':el.innerText,'id':el.dataset.id});
            });
                var d_match_type=keepType;
                console.log(d_match_type);
                isactive = input.checked ? "Y":"N";
                for(i=0;i<r_match_num;i++){
                    obj[i] = {
                        'r_match_goodsCode':$(goodsBox[i]).attr('id'),
                        'r_match_goodsImage': $(goodsBox[i]).find('img').attr('src'),
                        'r_match_goodsPrice': '',
                        'r_match_goodsName':$(goodsBox[i]).find('.item_text').text(),
                        'r_match_goodsType':$(goodsBox[i]).attr('type'),
                        'productUrl':$(goodsBox[i]).find('img').attr('producturl'),
                        "shareUrl":$(goodsBox[i]).find('img').attr('shareurl')
                    }
                    r_match_goods.push(obj[i]);
                }
                // if(d_match_title.length<1||d_match_desc.trim()==''){
                //     art.dialog({
                //         time: 1,
                //         lock: true,
                //         cancel: false,
                //         content:"秀搭标题或商品描述未填写"
                //     });
                // }else if(d_match_image.length == 0 || r_match_goods.length == 0){
                //     art.dialog({
                //         time: 1,
                //         lock: true,
                //         cancel: false,
                //         content:"搭配图片或搭配商品未添加"
                //     });
                // }else{
                if(d_match_title.length<1){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"秀搭标题未填写"
                    });
                    return;
                }
                if(d_match_image == ''){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"搭配效果图未添加"
                    });
                    return;
                }
                if(r_match_goods.length == 0){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"商品搭配未添加"
                    });
                    return;
                }
                if(d_match_desc.trim()==''){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"搭配描述未填写"
                    });
                    return;
                }
                    var _params = {
                        "corp_code": corp_code,
                        "d_match_code":$.cookie('d_match_code'),
                        "d_match_title":d_match_title ,
                        "d_match_image":d_match_image,
                        "d_match_desc":d_match_desc,
                        "user_code":$.cookie('user_code'),
                        "r_match_goods":r_match_goods,
                        "d_match_type":d_match_type,
                        "isactive":isactive,
                    };
                    var _command = '/api/shopMatch/updGoodsByWx'
                    fabjs.ajaxSubmit(_command, _params);
                // }
            }else{
                return;
            }
        });
    };
    fabjs.ajaxSubmit=function(_command,_params){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", _command,"",_params, function(data){
            if(data.code=="0"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"保存成功"
                });
                //$(window.parent.document).find('#iframepage').attr("src", "/goods/xiuda.html");
            }else if(data.code=="-1"){
                art.dialog({
                    time: 2,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    };
    var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if(obj1){
            _this = jQuery(obj1);
        }else{
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if(fabjs['check' + command]){
            if(!fabjs['check' + command].apply(fabjs,[obj,hint])){
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function() {
        var _this = this;
        interval = setInterval(function() {
            bindFun(_this);
        }, 500);
    }).blur(function(event) {
        clearInterval(interval);
    });
    var init=function(){
        fabjs.bindbutton();
    }
    var obj = {};
    obj.fabjs = fabjs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function(){
    window.fab.init();//初始化

});
$("#edit_close").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/goods/xiuda.html");
});
var num=1;
var next=false;
var fab_next=false;
var a=1;
//加号添加商品
$("#search_match_goods ul").on("click",".goods_add",function () {
    $(this).hide();
    $(this).next().show();
    $(this).parent("#search_match_goods ul li").css("background","#cde6e8");
    var img = $(this).parent('li').find('img').attr('src');
    var producturl= $(this).parent('li').find('img').attr('producturl');
    var shareurl=$(this).parent('li').find('img').attr('shareurl');
    var goods_code=  $(this).parent().find(".goods_code").html().trim();
    var goods_title=  $(this).parent().find(".goods_code").next().html();
    var r_match_goodsType =  $(this).parent('li').attr('class');
    var goods_code2=$("#GOODS_CODE").val();
    // var d_match_type=$("#d_match_type").val().trim();
    var len=$(".list_content").eq(1).find('.item_box').length;
    console.log('长度是'+len);
    if(goods_code==$("#"+goods_code).attr("id")|| goods_code==goods_code2){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "请勿重复添加"
        });
        return;
    }else if(len>9){
        $(this).css('display','block');
        $(this).next('.icon-ishop_6-12').css('display','none');
        $(this).parent().css('background-color','');
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "最多添加十个"
        });
    }
    else  {
        $("#add_one").before('<div type ="'+ r_match_goodsType +'"class="item_box" id="'+goods_code+'">'+'<div class="item_area" onmouseover="hover(this)" onmouseout="unHover(this)"><div class="remove_title" onclick = "removeIt2(this)"><i class="icon-ishop_6-12" style="display: inline"></i></div><img producturl="'+producturl+'" shareurl="'+shareurl+'" src="'+img+'" alt=""/></div> <div class="item_text" title="'+goods_title+'">'+goods_title+'</div>'+'</li>');
        //$(".list_content").eq(1).find('i').css('display','none');
    }

    $(".conpany_msg li i").click(function () {
        var code = $(this).parents("li").find(".goods_code").html();
        var len = $("#search_match_goods ul li").length;
        console.log(len);
        for(var i=0;i<len;i++){
            var code_l = $($("#search_match_goods ul li")[i]).find(".goods_code").html();
            console.log(code_l);
            if(code_l == code){
                $($("#search_match_goods ul li")[i]).css("background","");
                $($("#search_match_goods ul li")[i]).find("i").hide();
                $($("#search_match_goods ul li")[i]).find(".goods_add").show();
            }
        }
        $(this).parent("li").remove();
    });
    $(".conpany_msg li i").hover(function () {
        $(this).css("display","inline");
    });
});
//hover显示关闭按钮
function overShow(dom){
    $(dom).children('i').css("display","inline");
}
function outHide(dom){
    $(dom).children('i').css("display","none");
}

//叉号取消添加商品
$("#search_match_goods ul").on("click","li i",function () {
    $(this).prev().show();
    $(this).hide();
    $(this).parent("#search_match_goods ul li").css("background","");
    var goods_code=$(this).parent().find(".goods_code").html().trim();
    $("#"+goods_code).remove();
})
// }
//删除图片
Array.prototype.removeByValue = function(val) {
    for(var i=0; i<this.length; i++) {
        if(this[i] == val) {
            this.splice(i, 1);
            break;
        }
    }
}
$(".good_imgs").on("click",".diyCancel",function(){
    var src = $(this).parent().children().find("img").attr("src");
    swip_image.removeByValue(src);
    $(this).parent().remove();
});
function getmatchgoodsList(a,type) {
    var chooseType = cache.chooseType;
    var search_value=$("#search").val();
    var search_w=$("#search_w").val();
    var pageNumber=a;
    if(type =='fab'){
        $("#fabList").show();
        $("#wxList").hide();
        fabList(search_value,pageNumber);
    }
    if(type =='wx'){
        $("#fabList").hide();
        $("#wxList").show();
        wxList(search_w,pageNumber);
    }
    if(type =='all'){
        if(chooseType=="1"){
            $("#fabList").hide();
            $("#wxList").show();
        }
        if(chooseType=="0"){
            $("#fabList").show();
            $("#wxList").hide();
        }
        if(chooseType!=="0"&&chooseType!=="1"){
            $("#fabList").show();
            $("#wxList").hide();
        }
        fabList(search_value,pageNumber);
        wxList(search_w,pageNumber);
    }
}
//获取fab商品
function fabList(search_value,pageNumber){
    var param={};
    var goods_code=''; //新增的时候goods_code传''，编辑传goods_code，多个以逗号分隔
    var pageSize=20;
    //var corp_code = '';
    param["goods_code"]=goods_code;
    param["pageNumber"] =pageNumber;
    param["pageSize"] =pageSize;
    param["search_value"]=search_value;
    param["corp_code"]=$("#OWN_CORP").val();
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post", "/goods/getMatchFab","",param, function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list);
            var hasNextPage=list.hasNextPage;
            var list=list.list;
            var shopList=$(".list_content .item_box");
            if(list.length<=0){
                $('#fabList').empty();
                jQuery('#fabList').append("<p>没有相关商品</p>");
            }else{
                var len = $("#fabList li").length;
                for(var i=0;i<list.length;i++){
                    var imgUrl="";
                    if(list[i].goods_image.indexOf("http")!==-1){
                        imgUrl = list[i].goods_image;
                    }
                    if(list[i].goods_image.indexOf("http")==-1){
                        imgUrl="../img/goods_default_image.png";
                    }
                    jQuery('#fabList').append('<li class="fab" data-code="'+list[i].goods_code+'"><img class="goodsImg" src="'
                        + imgUrl
                        + '"><span class="goods_code">'
                        + list[i].goods_code + '</span><span>'
                        + list[i].goods_name + '</span><span class="goods_add">'
                        +'+</span><i class="icon-ishop_6-12"></i></li>');
                }
                for(var j=0;j<shopList.length;j++){
                    if($(shopList[j]).attr("type")=="fab"){
                        $("#fabList .fab[data-code='"+$(shopList[j]).attr('id')+"']").find('.goods_add').hide();
                        $("#fabList .fab[data-code='"+$(shopList[j]).attr('id')+"']").find('i').show();
                        $("#fabList .fab[data-code='"+$(shopList[j]).attr('id')+"']").css("background","#cde6e8");
                    }
                }
                whir.loading.remove();//移除加载框
            }
            if(hasNextPage==true){
                a++;
                fab_next=false;
            }
            if(hasNextPage==false){
                fab_next=true;
            }
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove();//移除加载框
        // goodsAddHide();
    });
}
//获取微商城商品
function wxList(search_value,pageNumber){
    console.log('获取微商城商品');
    var pageSize=10;
    var row_num=$("#wxList li").length;
    var categoryId="";
    var corp_code =$("#OWN_CORP").val();//获取企业编号
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("get","/api/shopMatch/getGoodsByWx?corp_code="+corp_code+"&pageSize="+pageSize+"&pageIndex="+pageNumber+"&categoryId="+categoryId+"&row_num="+row_num+"&searchType="+cache.searchType+"&productName="+search_value+"&brand_code="+cache.brand_code,'','',function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list=msg.productList;
            var shopList=$(".list_content .item_box");
            if(list.length<=0&&pageNumber==1){
                jQuery('#wxList').html("<p>没有相关商品了</p>");
                next=true;
            }else if(list.length>0){
                var len = $("#wxList li").length;
                for(var i=0;i<list.length;i++){
                    console.log(list.length);
                    var imgUrl="";
                    if(list[i].imageUrl.indexOf("http")!==-1){
                        imgUrl = list[i].imageUrl;
                    }
                    if(list[i].imageUrl.indexOf("http")==-1){
                        imgUrl="../img/goods_default_image.png";
                    }
                    jQuery('#wxList').append('<li class="wx" data-code="'+list[i].productId+'"><img producturl="'+list[i].productUrl+'" shareurl="'+list[i].shareUrl+'" class="goodsImg" src="'
                        + imgUrl
                        + '"><span class="goods_code">'
                        + list[i].productId + '</span><span>'
                        + list[i].productName + '</span><span class="goods_add">'
                        +'+</span><i class="icon-ishop_6-12"></i></li>');
                }
                for(var j=0;j<shopList.length;j++){
                    if($(shopList[j]).attr("type")=="wx"){
                        $("#wxList .wx[data-code='"+$(shopList[j]).attr('id')+"']").find('.goods_add').hide();
                        $("#wxList .wx[data-code='"+$(shopList[j]).attr('id')+"']").find('i').show();
                        $("#wxList .wx[data-code='"+$(shopList[j]).attr('id')+"']").css("background","#cde6e8");
                    }
                }
                num++;
                next=false;

            }
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }

        // goodsAddHide();
    });
}
$("#fabList").bind("scroll",function () {
    var nScrollHight = $(this)[0].scrollHeight;  //初始化的顶部，距离可见（当前底部）的高度
    var nScrollTop = $(this)[0].scrollTop;  //距离顶部的高度
    var nDivHight=$(this).height()+10;       //单个盒子的高度
    if(nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0){
        if(fab_next){
            return;
        }
        fab_next=true;
        var type = 'fab';
        getmatchgoodsList(a,type);
    };
})
$("#wxList").bind("scroll",function () {
    var nScrollHight = $(this)[0].scrollHeight;  //初始化的顶部，距离可见（当前底部）的高度
    var nScrollTop = $(this)[0].scrollTop;  //距离顶部的高度
    var nDivHight=$(this).height()+1;       //单个盒子的高度
    if(nScrollTop + nDivHight >= nScrollHight&& nScrollTop != 0){
        if(next){
            return;
        }
        next=true;
        var type = 'wx';
        getmatchgoodsList(num,type);
    };
})
//点击添加匹配商品弹窗
$("#add_one").click(function () {
    var chooseType = cache.chooseType;
    var arr=whir.loading.getPageSize();
    num=1;
    a=1;
    $("#goods_box").css({"width":arr[0]+"px","height":arr[1]+"px"});
    chooseType=='1'? $('.good_list_type span').eq(1).trigger('click'): $('.good_list_type span').eq(0).trigger('click');
    $("#goods_box").show();
    $("#search_match_goods").show();
    //if(num>1){
    //    return;
    //}else {
    //    num=1;
    //    next=false;
    var param={};
    var corp_code=$("#OWN_CORP").val();
    oc.postRequire("get", "/api/shopMatch/getGoodsTypeByCorp?corp_code="+corp_code+"", "","", function (data) {
        if(data.code=="0"){
            var type ="";
            if(data.message=="ALL"){
                $("#good_list_all").show();
                $("#good_list_type").hide();
                $("#search").attr("placeholder","商品编号/商品名称");
                type ="all";
            }
            if(data.message=="FAB"){
                $("#good_list_all").hide();
                $("#good_list_type").show();
                $("#good_list_type").html("FAB商品");
                $("#search").attr("placeholder","商品编号/商品名称");
                $("#fab_search").show();
                $("#wx_search").hide();
                type ="fab";
            }
            if(data.message=="WX"){
                $("#good_list_all").hide();
                $("#good_list_type").show();
                $("#good_list_type").html("微商城商品");
                $("#search").attr("placeholder","商品名称");
                $("#fab_search").hide();
                $("#wx_search").show();
                type="wx";
            }
            jQuery('#search_match_goods ul').empty();
            getmatchgoodsList(num,type);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
        // jQuery('#search_match_goods ul').empty();
        // var type = 'all';
        // getmatchgoodsList(num,type);
    //}
});
//关闭搜索匹配商品弹窗
$("#close_match_goods").click(function () {
    $("#goods_box").hide();
    $("#search_match_goods").hide();
});

//搜索相关商品
$("#d_search").click(function () {
    num=1;
    var val = $('#fabList').css('display');
    //判断页面
    var type = val == 'block'?'fab':'wx';
    //判断清空的页面
    var temp = type == 'fab'?'#fabList':'#wxList';
    //jQuery(temp).empty();
    $(temp).empty();
    console.log('搜索类型'+type);
    getmatchgoodsList(num,type);
});
//搜索微信
$("#w_search").click(function(){
    $("#d_search").click();
})
$("#search_match_goods").keydown(function () {
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $("#d_search").click();
        //jQuery('#search_match_goods ul').empty();
        //num=1;
        //getmatchgoodsList(num);
    }
})
//publick_click   class="checkbox_isactive"
function public_click(a) {
    if($(a).prev().attr('checked')){
        $(a).prev().removeAttr('checked');
        $(a).parent().addClass("checkbox_isactive");
        $(a).addClass('check_public');
    }else{
        $(a).prev()[0].checked='true';
        $(a).parent().addClass("checkbox_isactive");
        $(a).prev().attr('checked','true')
    }
}
function changePage(){
    window.location.href="fab_match.html";
}
//点击隐藏提醒
function msgHide(){
    $('#areaTemp').css('display','none');

}
//输入框不为空检查
function checkTemp(){
    $('.the_listTitle').find('input').blur(function(){
        ($(this).val()=='')?
            $(this).next('.hint').show(): $(this).next('.hint').hide();
    });
}
//搭配效果图
function addImg(){
    var self=this;
    self.client=self.client || new OSS.Wrapper({
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            bucket: 'products-image',
            secure:true
        });
    $("#upFile").change(function(e){
        var checkNum = $('.list_content').eq(0).find('.item_box').length;
        if(checkNum<=4) {
            var file = e.target.files[0];
            var pos = file.name.lastIndexOf(".");
            var imgclass = file.name.substring(pos + 1, file.name.length);
            var corp_code = $("#OWN_CORP").val();
            if (imgclass == "jpg" || imgclass == "jpeg" || imgclass == "png" || imgclass == "gif" || imgclass == "JPG" || imgclass == 'PNG' || imgclass == 'png' || imgclass == 'bmp' || imgclass == 'JPEG' || imgclass == 'GIF' || imgclass == 'BMP') {
                var time = new Date().getTime();
                var storeAs = '/ShowImage/' + corp_code + '/' + corp_code + "-" + time + "." + imgclass;
                showImg(storeAs);
                self.client.multipartUpload(storeAs, file).then(function (result) {
                    console.log(result);
                    var storeAs = 'https://products-image.oss-cn-hangzhou.aliyuncs.com/' + result.name;
                    //删除加载中
                    $('.loading_title').remove();
                    $(".showBoxImg:last").attr("src", storeAs);
                    $("#upFile").val("");
                }).catch(function (err) {
                    console.log(err);
                });
            } else {
                console.log("请确保文件为图像类型");
                return false;
            }
        }else{
            console.log('弹窗提醒');
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"搭配效果图最多可上传5张"
            });
            return;
        }
    })
}
//搭配效果图展现
function showImg(storeAs){
    var tempHTML = '<div class="item_box"><div class="item_area" onmouseover="hover(this)" onmouseout="unHover(this)"><div class="remove_title" onclick = "removeIt2(this)"><i class="icon-ishop_6-12" style="display: inline"></i></div>${loading}${img}</div></div>'
    var html = '';
    var nowHTML1 = tempHTML;
    var loading = '<div class="loading_title">加载中...</div>';
    if(storeAs.indexOf('http')=='-1'){
        var msg =  '<img src = " "  class="showBoxImg" />';
    }else{
        var msg =  '<img src = "' + storeAs + '"  class="showBoxImg" />';
    }
    nowHTML1 = nowHTML1.replace("${loading}", loading);
    nowHTML1 = nowHTML1.replace("${img}", msg);
    html += nowHTML1;
    $(".list_content #add_all").before(html);
}
function removeIt(dom){
    $(dom).parents('.item_box').remove();
}
function removeIt2(dom){
    $(dom).parents('.item_box').remove();
    var key = $(dom).parents('.item_box').attr('id');
    $('#search_match_goods ul li').each(function () {
        if( $(this).find('.goods_code').text() == key){
            $(this).find('.icon-ishop_6-12').click();
        }
    });
}
//所有企业
function allCorp(corp_code) {
    var corp=corp_code;
    var param = {};
    oc.postRequire("post", "/user/getCorpByUser", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var corps = msg.corps;
            var corp_html='';
            for(var i=0;i<corps.length;i++){
                corp_html+='<option value="'+corps[i].corp_code +'">'+corps[i].corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $("#OWN_CORP option[value='"+corp+"']").attr("selected","true");
            var corp_code=$("#OWN_CORP").val();
            $('.corp_select select').searchableSelect();
            getBrandlist();
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    var corp_code1=$("#OWN_CORP").val();
                    if(corp_code!==corp_code1){
                        corp_code=corp_code1;
                        $("#brand em").html("请选择品牌");
                        cache.brand_code="";
                        $("#list_content .item_box").remove();
                        getBrandlist();
                    }
                }
            })
            $('.searchable-select-item').click(function(){
                var corp_code1=$("#OWN_CORP").val();
                if(corp_code!==corp_code1){
                    corp_code=corp_code1;
                    $("#brand em").html("请选择品牌");
                    cache.brand_code="";
                    $("#list_content .item_box").remove();
                    getBrandlist()
                }
            })
            // var corp_code = $.cookie('corp_code');
            // cache.corp = corp_code;
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
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
//跳转
function toXiuda(){
    window.location.href='/goods/xiuda.html';
}
function getPage(){
    var corp_code = $.cookie('corp_code');
    var d_match_code = $.cookie('d_match_code');
    var user_code = $.cookie('user_code');
    oc.postRequire("get","/api/shopMatch/selectById?d_match_code="+d_match_code+"&corp_code="+corp_code+"&user_code="+user_code,"0","",function(data){
        if(data.code=="0"){
            var message = JSON.parse(data.message);
            var corp_code=message.corp_code;
            console.log(corp_code);
            allCorp(corp_code); //所有企业
            $('.the_listTitle').eq(1).find('input').val(message.d_match_title);//标题
            $('#d_match_desc').val(message.d_match_desc);//详情
            $('#created_time').val(message.created_date);//创建时间
            $('#creator').val(message.creater);//创建人
            $('#modify_time').val(message.modified_date);//修改时间
            $('#modifier').val(message.modifier);//修改人
            // $('#d_match_type').val(message.d_match_type);//秀搭类型
            console.log(message);
            try{
                   alreadyChoose=$.parseJSON(message.d_match_type);
                   alreadyChoose.forEach(function(val){
                    roleId.push(val.id);
                 });
                }catch(err){
                  roleId=[];
                }
                for(var g=0;g<alreadyChoose.length;g++){
                $('.show_type').prepend('<li class="li" data-id="'+alreadyChoose[g].id+'">'+alreadyChoose[g].name+'<i class="icon-ishop_6-12"></i></li>')
            }
           
            var d_match_image = message.d_match_image.split(',');;
            for(i = 0;i<d_match_image.length;i++){
                showImg(d_match_image[i]);
            }
            var r_match_goods = message.r_match_goods;
            addGoods(r_match_goods);
            var isactive = message.isactive;
            if(isactive=="Y"){
                $('#is_active').attr("checked",true);
            }else if(isactive=="N"){
                $('#is_active').attr("checked",false);
            }
            $('.loading_title').remove();

        }else if(data.code=="-1"){

        }
    })
}
function addGoods(r_match_goods){
    for(k=0;k<r_match_goods.length;k++){
        var goods_code = r_match_goods[k].r_match_goodsCode;
        var img = r_match_goods[k].r_match_goodsImage;
        var goods_title = r_match_goods[k].r_match_goodsName;
        var r_match_goodsPrice = r_match_goods[k].r_match_goodsPrice;
        var r_match_goodsType = r_match_goods[k].r_match_goodsType;
        var producturl= r_match_goods[k].productUrl;
        var shareurl=r_match_goods[k].shareUrl;
        $("#add_one").before('<div type ="'+ r_match_goodsType +'"class="item_box" id="'+goods_code+'">'+'<div class="item_area" onmouseover="hover(this)" onmouseout="unHover(this)"><div class="remove_title" onclick = "removeIt2(this)"><i class="icon-ishop_6-12" style="display: inline"></i></div><img producturl="'+producturl+'" shareurl="'+shareurl+'" src="'+img+'" alt=""/></div> <div class="item_text" title="'+goods_title+'">'+goods_title+'</div>'+'</li>');
        //$(".list_content").eq(1).find('i').css('display','none');
    }
}
//FAB商品-点击
$('.good_list_type span').eq(0).click(function () {
    cache.chooseType = '0';
    $('#search').val('');
    $(this).addClass('change_type');
    $(this).next('span').removeClass('change_type');
    $('#fabList').css('display','block');
    $('#wxList').css('display','none');
    $("#search").attr("placeholder","商品编号/商品名称");
    $("#fab_search").show();
    $("#wx_search").hide();
});
//微商城商品-点击
$('.good_list_type span').eq(1).click(function () {
    cache.chooseType = '1';
    $('#search').val('');
    $(this).addClass('change_type');
    $(this).prev('span').removeClass('change_type');
    $('#fabList').css('display','none');
    $('#wxList').css('display','block');
    $("#fab_search").hide();
    $("#wx_search").show();
});
//hover
function hover(dom){
    $(dom).find('.remove_title').css('opacity','0.4');
    $(dom).find('.icon-ishop_6-12').css('opacity','1');
}
function unHover(dom){
    $(dom).find('.remove_title').css('opacity','0');
    $(dom).find('.icon-ishop_6-12').css('opacity','0');
}
window.onload = function(){
    $('#areaTemp').css('display','none');
    // allCorp(); //所有企业
    getPage(); //获取页面
    checkTemp();//检查空
    addImg();//添加图片
    // setTimeout(function(){
    //     $('.searchable-select-input').css('margin-bottom','4px');
    // },500)

}
// 秀搭类型
var alreadyChoose=[];
var choose_type=[];
var roleId=[];
var show_type_pageNumber=1;
 var show_type_search='';
 Array.prototype.unique= function(){
        var res = [];
        var json = {};
        for(var i = 0; i < this.length; i++){
            if(!json[this[i].id]){
                res.push(this[i]);
                json[this[i].id] = 1;
            }
        }
        return res;
    }
$('.show_type').on('click','.li_add',function(){
    $('.show_type_box').show();
    if($('.show_type_box_3 input').val())return;
    $('.show_type_box_4').empty();
    show_type_pageNumber=1;
     getShowType();
});
$('.show_type').on('click','.li',function(){
   $(this).remove()
   console.log(this.dataset.id)
   console.log(roleId)
   for(var i=roleId.length-1;i>=0;i--){
    console.log(roleId[i]==this.dataset.id)
       if(roleId[i]==this.dataset.id)roleId.splice(i,1)
   }
});
$('.show_type_box_5 span').click(function(e){
    $('.show_type_box').hide();
    choose_type=[];
    roleId=[];
    $('.show_type_box_4 .en_choose').each(function(index, el) {
        choose_type.push({'name':el.innerHTML,'id':el.dataset.id})     
    });
     $('.show_type .li').each(function(index, el) {
        choose_type.push({'name':el.innerText,'id':el.dataset.id})     
    });
    var htmlArr=choose_type.unique();
    htmlArr.forEach(function(val){
        roleId.push(val.id);
    });
    var htmlDom=[];
    htmlArr.forEach(function(val){
        htmlDom.push('<li class="li" data-id="'+val.id+'">'+val.name+'<i class="icon-ishop_6-12"></i></li>');
    });
    htmlDom.push('<li class="li_add" style=" "><div class="addAdd" style="top: -8px;left: 23px;font-size: 29px;">+</div></li>');
    $('.show_type').html(htmlDom.join(''));
});
$('.show_type_box_4').scroll(function(){
     var nDivHight=parseFloat($('.show_type_box_4').css('height'))
      var nScrollHight = $(this)[0].scrollHeight;
       var   nScrollTop = $(this)[0].scrollTop;
         if(nScrollTop + nDivHight+1 >= nScrollHight){
            if(show_type_pageNumber==1)return
            getShowType();
         }
          
})
$('.show_type_box_4').on('click','li',function(){
   $(this).toggleClass('en_choose');
})
$('.show_type_box_3 input').keydown(function(event) {
    /* Act on the event */
    if(event.keyCode==13){
         $('.show_type_box_4').empty();
         show_type_search=$(this).val();
         $(this).blur();
         show_type_pageNumber=1;
         getShowType()
    }
});
$('.show_type_box_3 input').next().click(function(){
      $('.show_type_box_4').empty();
         show_type_search=$('.show_type_box_3 input').val();
         show_type_pageNumber=1;
         getShowType()
});
function getShowType(){
    console.log(alreadyChoose)
    console.log(roleId)
    whir.loading.add("",0.5);//加载等待框
     var param={};
        param.pageNumber=show_type_pageNumber;
        param.pageSize=20;
        param.searchValue=show_type_search;
        oc.postRequire("post","/api/shopMatch/type/searchByWeb","0",param,function(data){
            if(data.code==0){
                console.log(JSON.parse(JSON.parse(data.message).list));
                      var obj=JSON.parse(JSON.parse(data.message).list);
                        if(!obj.hasNextPage){//没有下一页
                           show_type_pageNumber=1;
                        }else{
                            show_type_pageNumber++;
                        }
                      var typeList=obj.list;
                       var typeHtml=typeList.map(function(val){
                        return {'name':val.shopmatch_type,'id':val.id};
                    })
                    var html=[];
                    for(var i=0;i<typeHtml.length;i++){
                        if(roleId.indexOf(String(typeHtml[i].id))==-1){
                            html.push('<li data-id="'+typeHtml[i].id+'">'+typeHtml[i].name+'</li>');
                        }else{
                            html.push('<li class="en_choose" data-id="'+typeHtml[i].id+'">'+typeHtml[i].name+'</li>');
                        }
                    }
                    $('.show_type_box_4').append(html.join(''));
                     whir.loading.remove();//移除加载框
            }
        })
};
function getBrandlist() {
    var param={};
    param["corp_code"]=$("#OWN_CORP").val();
    oc.postRequire("post","/shop/brand","0",param,function(data){
        var list=JSON.parse(data.message).brands;
        var html="";
        for(var i=0;i<list.length;i++){
            html+="<p data-type='"+list[i].brand_code+"'>"+list[i].brand_name+"</p>"
        }
        $("#brand_list").html(html);
    })
}
$("#select_list p").click(function(){
    $(this).addClass("active");
    cache.searchType=$(this).attr("data-type");
    $("#d_search").click();
    $(this).siblings().removeClass("active");
    $(this).parents(".select").find("em").text($(this).text());
})
$("#brand_list").on("click","p",function(){
    $(this).addClass("active");
    cache.brand_code=$(this).attr("data-type");
    $("#d_search").click();
    $(this).siblings().removeClass("active");
    $(this).parents(".brand").find("em").text($(this).text());
})
