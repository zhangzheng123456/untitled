var oc = new ObjectControl();
$(function(){
    getConsumCount();
});
function getConsumCount(){
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]="C10141";
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var conSumData=JSON.parse(Data.Consum);
       var HTML="";
       var LABEL="";
      $("#total_amount_Y").html(conSumData.total_amount_Y);
      $("#consume_times_Y").html(conSumData.consume_times_Y);
      $("#total_amount").html(conSumData.total_amount);
      $("#consume_times").html(conSumData.consume_times);
      $("#dormant_time").html(conSumData.dormant_time);
      $("#last_date").html(conSumData.last_date);
        for(var i=0;i<album.length;i++){
            if(i<16){
                HTML+="<span><img src="+album[0].image_url+" /></span>"
            }
            }
        $("#images").html(HTML);
        for(var i=0;i<label.length;i++){
                LABEL+="<span >"+label[i].label_name+"</span>"
        }
        $("#labels").html(LABEL);
        lg_img()
    })

}
function lg_img(){
    //点击图片放大
    $("#images span").click(function(){
        var src=$(this).children().attr("src");
        $("#mask").css("display","block");
        $("#lg_img img").attr("src",src);
        $("#lg_img").show();
    });
}
$("#mask").click(function(){
    $(this).hide();
    $("#lg_img").hide();
});
$("#lg_img").click(function(){
    $(this).hide();
    $("#mask").hide();
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
    var len=$(this).width();
    var index=$(this).index();
    $("#remark").animate({left:len*index},200);
}).mouseout(function(){
    $("#remark").stop(true,true);
});
$(".nav_bar").mouseleave(function() {
    $("#remark").stop();
    var _this = $(".active1").index();
    $(this).children().eq(_this).addClass("active1");
    var len = $(this).children().width();
    $("#remark").animate({left: len * _this}, 200);})

//相册关闭按钮显示
$(".album img").mouseover(function () {
    $(this).next(".cancel_img").show();
}).mouseleave(function () {
    $(this).next(".cancel_img").hide();
})
$(".cancel_img").mouseover(function () {
    $(this).show();
}).mouseleave(function () {
    $(this).hide();
})

//相册图片点击放大.关闭
$(".album li").click(function () {
  var img=$(this).find("img").prop("outerHTML");
      $(".album_shade").after(img);
      $(".album_shade").next("img").addClass("album_lg");
      $(".album_shade").show();
})
$(".album_shade").click(function () {
    $(".album_shade").next(".album_lg").remove();
    $(".album_shade").hide();
})
