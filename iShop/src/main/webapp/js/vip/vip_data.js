var oc = new ObjectControl();
var page=1;
var param={};//定义传值对象
var cls="";//标签搜索下class名
var txt="";//标签搜索下标签名
var val="";//贴上input值

function getConsumCount(){//获取会员信息
    //whir.loading.add("",0.5);//加载等待框
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]="C10000";
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var conSumData=JSON.parse(Data.Consum);
       var HTML="";
        var Ablum_all_html="";
       var LABEL="";
       var LABELALL="";
      $("#total_amount_Y").html(conSumData.total_amount_Y);
      $("#consume_times_Y").html(conSumData.consume_times_Y);
      $("#total_amount").html(conSumData.total_amount);
      $("#consume_times").html(conSumData.consume_times);
      $("#dormant_time").html(conSumData.dormant_time);
      $("#last_date").html(conSumData.last_date);
        for(var i=0;i<album.length;i++){
                if(i<16){
                    HTML+="<span><img src="+album[i].image_url+" /></span>";
                }
                Ablum_all_html+="<li>"
                +"<img src='"+album[i].image_url+"'>"
                +"<div class='cancel_img'></div>"
                +"<span class='album_date'>"+album[i].created_date+"</span>"
                +"</li>"
            }
        $("#images").html(HTML);
        $("#Ablum-all").html(Ablum_all_html);
        for(var i=0;i<label.length;i++){
                LABEL+="<span >"+label[i].label_name+"</span>";
             if(label[i].label_type=="user"){
                 LABELALL+="<span class='label_u' data-rid="+label[i].rid+">"+label[i].label_name+"<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i></span>"
            }else {
                 LABELALL+="<span class='label_g' data-rid="+label[i].rid+">"+label[i].label_name+"<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i></span>"
            }
        }
        //统计已有标签
        $(".span_total").html(label.length);
        $("#labels").html(LABEL);
        $("#label_box").html(LABELALL);
        lg_img();
        img_hover();
    })

}
function lg_img(){
    //点击图片放大
    $("#images span").click(function(){
        var src=$(this).children().attr("src");
        whir.loading.add("",0.8,src);//显示图片
    });
    //相册图片点击放大.关闭
    $(".album li img").click(function () {
        var src=$(this).attr("src");
        whir.loading.add("",0.8,src);
    })
}
function img_hover(){
    //相册关闭按钮显示
    $(".album img").mouseover(function () {
        $(this).next(".cancel_img").show();
    }).mouseout(function () {
        $(this).next(".cancel_img").hide();
    })
    $(".cancel_img").mouseover(function () {
        $(this).show();
    }).mouseout(function () {
        $(this).hide();
    }).click(function(){
       $("#tk").show();
    })
}
$(".message-class ul li a").click(function(){
    $(this).addClass("active");
    $(this).parent().siblings().children().removeClass("active");
    var nowIndex=$(this).parent().index();
    $(".tabs-parent").children().eq(nowIndex).show();
    $(".tabs-parent").children().eq(nowIndex).siblings().hide()
});

$("#fenLei").click(function(){//点击查看更多跳到编辑资料
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
});
$("#VIP_message_back").click(function(){//回到会员信息
   $("#VIP_Message").show();
   $("#VIP_edit").hide();
});
//热门标签
function gethotVIPlabel() {
    //热门标签
    $("#hotlabel").empty();
    var param={};
    param["corp_code"]="C10000";
    oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
            var html="";
            console.log(msg.length);
            for(var i=0;i<msg.length;i++){
                if(msg[i].label_type=="user"){
                    html+="<span  draggable='true' data-id="+msg[i].label_id+" class="+'label_u'+" id="+i+">"+msg[i].label_name+"</span>"
                }else if(msg[i].label_type=="org"||msg[i].label_type=="sys"){
                    html+="<span  draggable='true' data-id="+msg[i].label_id+" class="+'label_g'+" id="+i+">"+msg[i].label_name+"</span>"
                }
            }
            $("#hotlabel").append(html);
        }
        //绑定拖拽事件
        $('#hotlabel span').on('dragstart',function (event) {
            var ev=event;
            console.log('触发');
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
    })
}
$("#hot_label").click(function () {
    gethotVIPlabel();
})
//官方用户标签
function getOtherlabel() {
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list)
                msg=msg.list;
            var html="";
            console.log(msg);
            if(msg[0].label_type=="org"){
                for(var i=0;i<msg.length;i++){
                    html+="<span  draggable='true' data-id="+msg[i].id+" class='label_g' id='"+i+"g'>"+msg[i].label_name+"</span>"
                }
                $("#label_org").append(html);
            }else if(msg[0].label_type=="user"){
                for(var j=0;j<msg.length;j++){
                    html+="<span  draggable='true' data-id="+msg[j].id+" class='label_u' id='"+j+"u'>"+msg[j].label_name+"</span>"
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
            console.log('触发');
            ev=ev.originalEvent;
            ev.dataTransfer.setData("Text",ev.target.id);
        });
    })
}
$("#label_li_org").click(function () {
    $("#label_org").empty();
    param["corp_code"]="C10000";
    param['pageNumber']=page;
    param['searchValue']="";
    param['type']="2";
    getOtherlabel();
})
$("#label_li_user").click(function () {
    $("#label_user").empty();
    param["corp_code"]="C10000";
    param['pageNumber']=page;
    param['searchValue']="";
    param['type']="3";
    getOtherlabel();
})

//搜索热门标签
function searchHotlabel() {
    param["corp_code"]="C10000";
    param['pageNumber']=page;
    param['searchValue']=$('#search_input').val().replace(/\s+/g,"");
    param['type']="1";
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list)
            var hasNextPage=JSON.parse(msg.hasNextPage);
            list=msg.list;
            var html="";
            console.log(hasNextPage);
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
            cls=$(this).attr("class");
            txt=$(this).html();
            param['label_name']=txt
            $("#search_input").val("");
            addViplabel();
        })
    })
}
//加载更多
function moreSearch() {
    $("#more_search").click(function () {
        page=page+1;
       searchHotlabel();
    })
};
$(document).click(function(e){
    if($(e.target).is("#more_search")){
        return;
    }else{
        $(".search_box").hide();
    }
});
//input输入框里面
$('#search_input').bind('input propertychange', function() {
    $(".search_list").empty();
    page=1;
    searchHotlabel();
});
//隐藏下拉框滚动条
$(function(){
    $(".search_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
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
})
//添加，删除标签
function labelDelete(obj) {
    // $("#label_box span i").click(function () {
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
    })
    // });
}
function addViplabel() {
    var id=sessionStorage.getItem("id");
    var store_id=sessionStorage.getItem("store_id");
    param["corp_code"]="C10000";
    param['vip_code']=id;
    param['label_id']="";
    param['store_code']=store_id;
    oc.postRequire("post","/VIP/label/addRelViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var rid=JSON.parse(msg.list);
            var html=""
            if(cls==""||cls==undefined){
                html='<span class="label_g" data-rid="'+rid+'">'+val+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }else{
                html='<span class='+cls+' data-rid="'+rid+'">'+txt+'<i class="icon-ishop_6-12" onclick="labelDelete(this)"></i></span>';
            }
            $("#label_box").append(html);
            var total=parseInt($(".span_total").html())+1;
            $(".span_total").html(total);
        }else if(data.code=="-1"){
            alert("请勿重复添加");
        }
    })
}
$("#labeladd_btn").click(function () {
    cls="";
    val=$("#search_input").val().replace(/\s+/g,"");
    val=val.substring(0,8);
    if(val==""){
        return;
    }
    param['label_name']=val;
    addViplabel();
});
function upLoadAlbum(){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('upAlbum').addEventListener('change', function (e) {
        var file = e.target.files[0];
        var time=getNowFormatDate();
        var storeAs='Album/Vip/iShow/C10000-15915655912-'+time+'.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            $("#imghead").attr("src",result.url);
            $("#upAlbum").val("");
            console.log(result);
            addVipAlbum(result.url)
        }).catch(function (err) {
             console.log(err);
        });
    });
}
function addVipAlbum(url){//上传照片到相册
     var param_addAblum={};
    param_addAblum["vip_code"]=sessionStorage.getItem("id");
    param_addAblum["vip_name"]="罗晓珊";
    param_addAblum["cardno"]="suda10900123103308";
    param_addAblum["image_url"]=url;
    param_addAblum["corp_code"]="C10000";
    oc.postRequire("post","/vipAlbum/add","",param_addAblum,function(data){
        console.log(data)
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
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year+month+strDate+H+M+S;
    return currentdate
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
    var clone= $(document.getElementById(data)).clone();
    var label_id=clone.attr("data-id");
    var val=$(clone).text();
    console.log(clone);
    console.log(val);

    //调用借口
    var id=sessionStorage.getItem("id");
    var store_id=sessionStorage.getItem("store_id");
    param["corp_code"]="C10000";
    param['label_name']=val;
    param['vip_code']=id;
    param['label_id']="";
    param['store_code']=store_id;
    oc.postRequire("post","/VIP/label/addRelViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var rid=JSON.parse(msg.list);
            var html= "<i class='icon-ishop_6-12' onclick='labelDelete(this);'></i>";
                clone=$(clone).append(html);
                $(clone).attr("data-rid",rid);
                $(ev.target).append(clone);
            var total=parseInt($(".span_total").html())+1;
            $(".span_total").html(total);
        }
    })
}
$(function(){
    getConsumCount();
    upLoadAlbum();
    moreSearch();
});
