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