var oc = new ObjectControl();
var page=1;
var param={};//定义传值对象
var cls="";//标签搜索下class名
var txt="";//标签搜索下标签名
var val="";//贴上input值
var swip_image = [];//图片切换播放
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
function getConsumCount(){//获取会员信息
    //whir.loading.add("",0.5);//加载等待框
    whir.loading.add("",0.5);//加载等待框
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var HTML="";
       var Ablum_all_html="";
       var LABEL="";
       var LABELALL="";
        if(album.length!==0){
            $("#upAlbum").parent().parent().siblings().remove();
            for(var i=0;i<album.length;i++){
                swip_image.push(album[i].image_url);
                var date=album[i].time;
                date=date.substring(0,11);
                if(i<16){
                    HTML+="<span><img src="+album[i].image_url+" /></span>";
                }
                Ablum_all_html="<li>"
                    +"<img src='"+album[i].image_url+"'>"
                    +"<div class='cancel_img' data-time='"+album[i].time+"'></div>"
                    +"<span class='album_date'>"+date+"</span>"
                    +"</li>";
                $("#upAlbum").parent().parent().before(Ablum_all_html)
            }
        }else {
            HTML+="<p>暂无图片</p>";
        }
        $("#images").html(HTML);
        //$("#Ablum-all").html(Ablum_all_html);
        if(label.length!==0){
            for(var i=0;i<label.length;i++){
                LABEL+="<span>"+label[i].label_name+"</span>";
                LABELALL+="<span class='label_u' data-rid="+label[i].rid+">"+label[i].label_name+"<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i></span>"
            }
        }else{
            LABEL+="<p>暂无标签</p>";
        }
        //统计已有标签
        $(".span_total").html(label.length);
        $("#labels").html(LABEL);
        $("#label_box").html(LABELALL);
        img_hover();
        whir.loading.remove();
    })
}
    //点击图片放大
    $("#images").on("click","span",function(){
        var src=$(this).children().attr("src");
        whir.loading.add("",0.8,src);//显示图片
    });

    $(".album").on("click","li img",function(){
        var src=$(this).attr("src");
        whir.loading.add("",0.8,src);
    });
function img_hover(){
    //相册关闭按钮显示
    $(".album img").mouseover(function () {
        $(this).next(".cancel_img").show();
    }).mouseout(function () {
        $(this).next(".cancel_img").hide();
    });

    $(".cancel_img").mouseover(function () {
        $(this).show();
    }).mouseout(function () {
        $(this).hide();
    })
}
$("#Ablum-all").on("click",".cancel_img",function(){
    whir.loading.add("mask",0.5);
    var time=$(this).attr("data-time");
    $("#tk").show();
    $("#delete").attr("data-time",time);
});
$("#X").click(function(){
    $("#tk").hide();
    whir.loading.remove("mask");
});
$("#Ablum-all").on("mouseover","img",function(){
   $(this).next().show()
});$("#Ablum-all").on("mouseover","div",function(){
   $(this).show()
});
$("#Ablum-all").on("mouseout","img",function(){
   $(this).next().hide()
});
$("#Ablum-all").on("mouseout","div",function(){
    $(this).hide()
});
$(".message-class ul li a").click(function(){
    $(this).addClass("active");
    $(this).parent().siblings().children().removeClass("active");
    var nowIndex=$(this).parent().index();
    $(".tabs-parent").children().eq(nowIndex).show();
    $(".tabs-parent").children().eq(nowIndex).siblings().hide()
});
function toall(){
    $('html,body').animate({
        'scrollTop': 0
    },0);
    var nowdataName=$(".message-class ul li .active").attr("data-name");
    $("#VIP_Message").hide();
    $("#VIP_edit").show();
    $("#nav_bar").find("li").each(function(index){
        if($(this).attr("data-name")==nowdataName){
            var len=$(this).width();
            $(this).addClass("active1");
            $(this).siblings().removeClass("active1");
            $(".all_list").children().eq(index).show();
            $(".all_list").children().eq(index).siblings().hide();
            $("#remark").animate({left:len*index},0.1);
        }
    });
    gethotVIPlabel();
}
$("#fenLei").click(function(){//点击查看更多跳到编辑资料
    toall();
});
$("#consum tbody").click(function(){
    toall();
});
$("#points tbody").click(function(){
    toall();
});
$("#VIP_message_back").click(function(){//回到会员信息
   $("#VIP_Message").show();
   $("#VIP_edit").hide();
   text_first="";
   text_second="";
   text_third="";
   time_start="";
   time_end="";
    getConsumCount();
    getVipInfo();
});
//热门标签
function gethotVIPlabel() {
    //热门标签
    $("#hotlabel").empty();
    var param={};
    param['vip_id']=sessionStorage.getItem("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
            var html="";
            var classname="";
            for(var i=0;i<msg.length;i++){
                if(msg[i].label_type=="user"){
                    if(msg[i].label_sign=="Y"){
                        classname="label_u_active";
                    }else {
                        classname="label_u";
                    }
                }else{
                    if(msg[i].label_sign=="Y"){
                        classname="label_g_active";
                    }else {
                        classname="label_g";
                    }
                }
                html+="<span  draggable='true' data-id="+msg[i].label_id+" class="+classname+" id="+i+">"+msg[i].label_name+"</span>"
            }
            $("#hotlabel").append(html);
        }
        //绑定拖拽事件
        $('#hotlabel span').on('dragstart',function (event) {
            var ev=event;
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
        //右侧标签点击事件
        clickLabeladd();
    })
}
$("#hot_label").click(function () {
    gethotVIPlabel();
});
//官方用户标签
function getOtherlabel() {
    param['vip_id']=sessionStorage.getItem("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['pageNumber']=page;
    param['searchValue']="";
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list)
            var hasNextPage=list.hasNextPage;
                list=list.list;
            var html="";
            var classname="";
            if(hasNextPage==false){
                $("#more_label_g").hide();
                $("#more_label_u").hide();
            }else {
                $("#more_label_g").show();
                $("#more_label_u").show();
            }
            if(list[0].label_type=="org"){
                for(var i=0;i<list.length;i++){
                    if(list[i].label_sign=="Y"){
                        classname="label_g_active";
                    }else {
                        classname="label_g";
                    }
                    html+="<span  draggable='true' data-id="+list[i].id+" class="+classname+" id='"+i+"g'>"+list[i].label_name+"</span>"
                }
                $("#label_org").append(html);
            }else if(list[0].label_type=="user"){
                for(var j=0;j<list.length;j++){
                    if(list[j].label_sign=="Y"){
                        classname="label_u_active";
                    }else {
                        classname="label_u";
                    }
                    html+="<span  draggable='true' data-id="+list[j].id+" class="+classname+" id='"+j+"u'>"+list[j].label_name+"</span>"
                }
                $("#label_user").append(html);
            }

        }
        //绑定拖拽事件
        $('#label_org span').on('dragstart',function (event) {
            var ev=event;
                ev=ev.originalEvent;
                ev.dataTransfer.setData("Text",ev.target.id);
        });
        //绑定拖拽事件
        $('#label_user span').on('dragstart',function (event) {
            var ev=event;
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
        //右侧标签点击事件
        clickLabeladd();
    })
}
$("#label_li_org").click(function () {
    page=1;
    $("#label_org").empty();
    param['type']="2";
    getOtherlabel();
})
$("#label_li_user").click(function () {
    page=1;
    $("#label_user").empty();
    param['type']="3";
    getOtherlabel();
})

//右侧加载更多标签
$("#more_label_g").click(function () {
    page=page+1;
    param['type']="2";
    getOtherlabel();
})
$("#more_label_u").click(function () {
    page=page+1;
    param['type']="3";
    getOtherlabel();
})

//搜索热门标签
function searchHotlabel() {
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['pageNumber']=page;
    param['vip_id']=sessionStorage.getItem("id");
    param['searchValue']=$('#search_input').val().replace(/\s+/g,"");
    param['type']="1";
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list)
            var hasNextPage=JSON.parse(msg.hasNextPage);
            list=msg.list;
            var html="";
            if(list.length!==0){
                $(".search_box").show();
            }else {
                $(".search_box").hide();
            }
            if(hasNextPage==true){
                for(var i=0;i<list.length;i++){
                    if(list[i].label_type=="user"){
                        html+="<li class='label_u' data-id="+list[i].id+">"+list[i].label_name+"</li>"
                    }else {
                        html+="<li class='label_g' data-id="+list[i].id+">"+list[i].label_name+"</li>"
                    }
                }
                $("#more_search").show();
                $(".search_list").append(html);
            }else {
                for(var j=0;j<list.length;j++){
                    if(list[j].label_type=="user"){
                        html+="<li class='label_u' data-id="+list[j].id+">"+list[j].label_name+"</li>"
                    }else {
                        html+="<li class='label_g' data-id="+list[j].id+">"+list[j].label_name+"</li>"
                    }
                }
                $("#more_search").hide();
                $(".search_list").append(html);
            }
        }
        //搜索下拉点击事件
        $(".search_list li").click(function () {
            var a="li";
            cls=$(this).attr("class");
            txt=$(this).html();
            param['label_name']=txt;
            $("#search_input").val("");
            addViplabel(a);
        });
    })
}
//加载更多
function moreSearch() {
    $("#more_search").click(function () {
        page=page+1;
       searchHotlabel();
    })
}
$(document).click(function(e){
    if($(e.target).is("#more_search")){
        return;
    }else{
        $(".search_box").hide();
    }
    if(!($(e.target).is("#send_code i") || $(e.target).is("#send_code input"))){
        $("#send_code ul").hide();
    }
});
//input输入框里面
$('#search_input').bind('input propertychange', function() {
    var thatFun=arguments.callee;
    var that=this;
    $(this).unbind("input propertychange",thatFun);
    $(".search_list").empty();
    page=1;
    searchHotlabel();
    setTimeout(function(){$(that).bind("input propertychange",thatFun)},0);
});
//隐藏下拉框滚动条
$(function(){
    $("#label_user").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $("#label_org").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $(".search_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $("#label_box").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
});
//回到会员列表
$("#VIP_LIST_info").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});

//回到会员列表
$("#VIP_LIST").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});
//导航点击切换窗口
$("#nav_bar li").click(function () {
    var index=$(this).index();
    $(this).addClass("active1");
    $(this).siblings().removeClass("active1");
    $(".all_list").children().eq(index).show();
    $(".all_list").children().eq(index).siblings().hide();
    if($(this).attr('data-name')=='jifen'){
        getVipPoints($("#corp_code").html(),'1')
    }
    if($(this).attr('data-name')=='consume'){
        getVipPoints($("#corp_code").html(),'2')
    }
    if($(this).attr('data-name')=='label'){
        gethotVIPlabel();
    }
}).mouseover(function(){
    var index=$(this).index();
    var len=$(this).width();
    $("#remark").animate({left:len*index},200);
}).mouseout(function(){
    $("#remark").stop(true,true);
});
$(".nav_bar").mouseleave(function() {
    $("#remark").stop();
    var _this = $(".active1").index();
    $(this).children().eq(_this).addClass("active1");
    var len = $(this).children().eq(_this).width();
    $("#remark").animate({left: len * _this}, 200);
});
//标签导航切换窗口
$(".label_nav li").click(function () {
    var index=$(this).index()+1;
    $(this).addClass("label_li_active");
    $(this).siblings().removeClass("label_li_active");
    $(".label_box").eq(index).show();
    $(".label_box").eq(index).siblings("div").hide();
});
//添加，删除标签
function labelDelete(obj) {
    var param={};
    var span=$(obj);
    var rid=$(obj).parent("span").attr("data-rid");
    param["rid"]=rid;
    oc.postRequire("post","/VIP/label/delRelViplabel","",param,function(data){
        if(data.code=="0"){
            span.parent("span").remove();
            var total=parseInt($(".span_total").html())-1;
            $(".span_total").html(total);
        }
    });
    var that=span.parent("span").text();
    var len=$("#hotlabel span").length;
    var len_o=$("#label_org span").length;
    var len_u=$("#label_user span").length;
    for(var i=0;i<len;i++){
        if($($("#hotlabel span")[i]).html()==that){
           var classname=$($("#hotlabel span")[i]).attr("class");
            if(classname=="label_u_active"){
                $($("#hotlabel span")[i]).removeClass().addClass("label_u");
            }
            if(classname=="label_g_active"){
                $($("#hotlabel span")[i]).removeClass().addClass("label_g");
            }
        }
    }
    for(var i=0;i<len_o;i++){
        if($($("#label_org span")[i]).html()==that){
            $($("#label_org span")[i]).removeClass().addClass("label_g")
        }
    }
    for(var i=0;i<len_u;i++){
        if($($("#label_user span")[i]).html()==that){
            $($("#label_user span")[i]).removeClass().addClass("label_u")
        }
    }
}
function addViplabel(obj,ev) {
    var id=sessionStorage.getItem("id");
    var store_id=sessionStorage.getItem("store_id");
    var that="";//获取贴上标签的名字
    var total=parseInt($(".span_total").html())+1;
    if(total>50){
        frame();
        $('.frame').html('亲，最多添加50个标签');
        return ;
    }
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['vip_code']=id;
    param['label_id']="";
    param['store_code']=store_id;
    oc.postRequire("post","/VIP/label/addRelViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var rid=JSON.parse(msg.list);
            var html="";
            if(cls==""||cls==undefined){
                html='<span class="label_u" data-rid="'+rid+'">'+val+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }else{
                html='<span class='+cls+' data-rid="'+rid+'">'+txt+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }
            $(".span_total").html(total);
            $("#label_box").append(html);
            if(obj=="btn"){
                that = val;
            }else if(obj=="li"){
                that = txt;
            }
            var len=$("#hotlabel span").length;
            var len_o=$("#label_org span").length;
            var len_u=$("#label_user span").length;
            for(var i=0;i<len;i++){
                if($($("#hotlabel span")[i]).html()==that){
                    var classname=$($("#hotlabel span")[i]).attr("class");
                    if(classname=="label_u"){
                        $($("#hotlabel span")[i]).removeClass().addClass("label_u_active");
                    }
                    if(classname=="label_g"){
                        $($("#hotlabel span")[i]).removeClass().addClass("label_g_active");
                    }
                }
            }
            for(var i=0;i<len_o;i++){
                if($($("#label_org span")[i]).html()==that){
                    $($("#label_org span")[i]).removeClass().addClass("label_g_active")
                }
            }
            for(var i=0;i<len_u;i++){
                if($($("#label_user span")[i]).html()==that){
                    $($("#label_user span")[i]).removeClass().addClass("label_u_active")
                }
            }
            if($(ev).attr("class")=="label_u"){
                $(ev).addClass("label_u_active").removeClass("label_u");
            }else if($(ev).attr("class")=="label_g") {
                $(ev).addClass("label_g_active").removeClass("label_g");
            }
        }else if(data.code=="-1"){
            frame();
            $('.frame').html('请勿重复添加');
        }
    })
}
$("#labeladd_btn").click(function () {
    var a="btn";
    cls="";
    val=$("#search_input").val().replace(/\s+/g,"");
    // val=val.substring(0,8);
    if(val==""){
        return;
    }
    param['label_name']=val;
    addViplabel(a);
    $("#search_input").val("");
});
//右侧点击添加标签
function clickLabeladd() {
    $("#hotlabel span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
    });
    $("#label_org span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
    });
    $("#label_user span").click(function () {
        val= $(this).html()
        param['label_name'] = val;
        var b= $(this);
        addViplabel("",b);
});
}
//拖拽
//阻止拖拽默认事件
function allowDrop(ev)
{
    ev.preventDefault();
}
//拖拽事件
function drop(ev)
{
    var param={};
    ev.preventDefault();
    var data=ev.dataTransfer.getData("Text");
    var span=$(document.getElementById(data));
    var clone= $(document.getElementById(data)).clone();
    var label_id=clone.attr("data-id");
    var val=$(clone).text();
    //调用借口
    var id = sessionStorage.getItem("id");
    var store_id = sessionStorage.getItem("store_id");
    var total = parseInt($(".span_total").html()) + 1;
    if(total>50){
        frame();
        $('.frame').html('亲，最多添加50个标签');
        return ;
    }
    param["corp_code"] = sessionStorage.getItem("corp_code");
    param['label_name'] = val;
    param['vip_code'] = id;
    param['label_id'] = "";
    param['store_code'] = store_id;
    oc.postRequire("post", "/VIP/label/addRelViplabel", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var rid = JSON.parse(msg.list);
            var html = "<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i>";
            clone = $(clone).append(html);
            $(clone).attr("data-rid", rid);
            $("#label_box").append(clone);
            $(".span_total").html(total);
            if(span.attr("class")=="label_u"){
                $(span).addClass("label_u_active").removeClass("label_u");
            }else {
                $(span).addClass("label_g_active").removeClass("label_g");
            }
        }else if(data.code =="-1"){
            frame();
            $('.frame').html('请勿重复添加');
        }
    })
}

function upLoadAlbum(){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('upAlbum').addEventListener('change', function (e) {
        whir.loading.add("上传中,请稍后...",0.5);
        var file = e.target.files[0];
        var time=getNowFormatDate();
        var corp_code=sessionStorage.getItem("corp_code");
        var vip_id=sessionStorage.getItem("id");
        var storeAs='Album/Vip/iShow/'+corp_code+'_'+vip_id+'_'+time+'.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
            $("#upAlbum").val("");
            addVipAlbum(url)
        }).catch(function (err) {
             //console.log(err);
        });
    });
}
function addVipAlbum(url){//上传照片到相册
     var param_addAblum={};
    param_addAblum["vip_id"]=sessionStorage.getItem("id");
    param_addAblum["vip_name"]=$("#vip_name").html();
    param_addAblum["phone"]=$("#vip_phone").html();
    param_addAblum["card_no"]=$("#vip_card_no").html();
    param_addAblum["image_url"]=url;
    param_addAblum["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/vipSaveInfo","",param_addAblum,function(data){
        var AlbumData=data;
        if(data.code=="0"){
            swip_image.push(url);
            frame();
            $('.frame').html('添加成功');
            $("#upAlbum").parent().parent().before("<li>"
                +"<img src='"+url+"'>"
                +"<div class='cancel_img' data-time='"+AlbumData.message+"'></div>"
                    +"<span class='album_date'>"+AlbumData.message.substring(0,11)+"</span>"
                +"</li>")
        }else{
            frame();
            $('.frame').html('添加失败');
        }
        whir.loading.remove();
    })
}
function getNowFormatDate() {//获取当前日期
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var H=date.getHours();
    var M=date.getMinutes();
    var S=date.getSeconds();
    var m=date.getMilliseconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = ""+year+month+strDate+H+M+S+m;
    return currentdate
}
//页面加载调权限接口
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        for(var i=0;i<actions.length;i++){
           if(actions[i].act_name=="changeVipType"){
                $("#grade_change").show();
            }else if(actions[i].act_name=="edit"){
               $("#change_save").show();
           }
        }
    })
}
//会员资料基本信息更改
$('#change_save').click(function () {
    var param={}
    param.vip_id=$('#vip_name_edit').attr('data_vip_id');
    param.vip_name=$('#vip_name_edit').val();
    param.corp_code=sessionStorage.getItem('corp_code');
    param.phone=$('#vip_phone_edit').val();
    param.birthday=$('#vip_birthday_edit').val().split('-').join('');
    param.card_no=$('#vip_card_no_edit').val();
    oc.postRequire("post","/vip/updateVip","",param,function(data){
        if(data.code==0){
            art.dialog({
                zIndex:10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "保存成功"
            });
            return;
        }else if(data.code==-1){
            art.dialog({
                zIndex:10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "保存失败"
            });
            return;
        }
    });
});
$(function(){
    getConsumCount();
    upLoadAlbum();
    moreSearch();
    qjia();
});


