$(function(){
    getVipInfo();
});
function getVipInfo(){
    var param_info={};
    param_info["vip_id"]="1";
    param_info["corp_code"]="2";
    oc.postRequire("post","/vip/vipInfo","",param_info,function(data){
        var vipData=JSON.parse(data.message);
        var vipDataList=vipData.list;
        var sex=vipDataList.sex;
        if(sex=="male"){
            $("#vip_name").next().addClass("icon-ishop_9-03");
            $("#USER_SEX").val("男");
        }else{
            $("#vip_name").next().addClass("icon-ishop_9-02");
            $("#USER_SEX").val("女");
        }
        $("#vip_name").html(vipDataList.vip_name);
        $("#vip_name_edit").val(vipDataList.vip_name);
        $("#vip_card_no").html(vipDataList.vip_card_no);
        $("#vip_card_no_edit").val(vipDataList.vip_card_no);
        $("#vip_card_type").html(vipDataList.vip_card_type);
        $("#vip_card_type_edit").val(vipDataList.vip_card_type);
        $("#vip_phone").html(vipDataList.vip_phone);
        $("#vip_phone_edit").val(vipDataList.vip_phone);
        $("#user_name").html(vipDataList.user_name);
        $("#user_name_edit").val(vipDataList.user_name);
        $("#vip_total_amount").html(vipDataList.total_amount);
        $("#join_date").html(vipDataList.join_date);
        $("#vip_dormant_time").html(vipDataList.dormant_time);
        $("#vip_birthday").html(vipDataList.vip_birthday);
        $("#vip_birthday_edit").val(vipDataList.vip_birthday);

    })
}
$("#more_message").click(function(){
    gotovipallmessage();
});
$("#edit_message").click(function(){
    gotovipallmessage();
});
function gotovipallmessage(){
    $("#VIP_Message").hide();
    $("#VIP_edit").show();
    $("#nav_bar").children().eq(0).addClass("active1");
    $("#nav_bar").children().eq(0).siblings().removeClass("active1");
    $(".all_list").children().eq(0).show();
    $(".all_list").children().eq(0).siblings().hide();
    $("#remark").animate({left:0},0.1);
}