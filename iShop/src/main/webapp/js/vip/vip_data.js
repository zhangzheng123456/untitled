var oc = new ObjectControl();
$(function(){
    getConsumCount();
});
function getConsumCount(){
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]="C10000";
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var conSumData=JSON.parse(data.message);
      $("#total_amount_Y").html(conSumData.total_amount_Y);
      $("#consume_times_Y").html(conSumData.consume_times_Y);
      $("#total_amount").html(conSumData.total_amount);
      $("#consume_times").html(conSumData.consume_times);
      $("#dormant_time").html(conSumData.dormant_time);
      $("#last_date").html(conSumData.last_date);
    })
}

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
      $(".album_shade").next("img").addClass("album_lg")
      $(".album_shade").show();
})
$(".album_shade").click(function () {
    $(".album_shade").next(".album_lg").remove();
    $(".album_shade").hide();
})

//标签导航切换窗口
$(".label_nav li").click(function () {
    var index=$(this).index()+1;
    $(this).addClass("label_li_active");
    $(this).siblings().removeClass("label_li_active");
    $(".label_box").eq(index).show();
    $(".label_box").eq(index).siblings("div").hide();
})
//添加，删除标签
$(".labeladd_btn").click(function () {
    var val=$(".labeladd_box input").val();
    console.log(val);
    if(val!==""){
        $("#label_box span:last-child").after('<span class="label_u_active">'+val+'<i class="icon-ishop_6-12"></i></span>')
    }
    $("#label_box span i").click(function () {
        $(this).parent("span").remove();
    })
})
$("#label_box span i").click(function () {
    $(this).parent("span").remove();
})