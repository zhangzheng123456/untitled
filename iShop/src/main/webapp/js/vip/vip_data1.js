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
        var extend=vipData.extend;
        var extend_info=vipData.extend_info;
        var sex=vipDataList.sex;
        var extendhtml="";
        function getvalue(){
            var VALUE="";
            for(var a=0;a<extend_info.length;a++){
                if(extend[i].key==extend_info[a].key){
                    VALUE=extend_info[a].value
                }
            }
            return VALUE;
        }
        for(var i=0;i<extend.length;i++){
            console.log(extend[i].name+extend[i].key+"文本类型"+extend[i].type);
            if(extend[i].type=="text"){
                var value=getvalue();
                extendhtml+="<li>"
                    +"<b>"+extend[i].name+"</b>"
                    +"<div>"
                    +"<input type='text' value='"+value+"' />"
                    +"</div>"
                    +"</li>"
            }
            if(extend[i].type=="select"){
                var value=getvalue();
                var selectValue=extend[i].values;
                selectValue=selectValue.split(",");
                var ul="<ul class='expand_selection'>";
                for(var b=0;b<selectValue.length;b++){
                    ul+="<li>"+selectValue[b]+"</li>";
                    console.log(selectValue[b])
                }
                extendhtml+='<li class="drop_down item_1">'
                    +'<b>'+extend[i].name+'</b>'
                    +'<div class="position" >'
                    +'<input class="input_select" readonly value="'+value+'" type="text" />'
                    +ul
                    +'</div>'
                    +'</li>'
            }
            if(extend[i].type=="date"){
                var value=getvalue();
                extendhtml+='<li >'
                +'<b>'+extend[i].name+'</b>'
                +'<div>'
                +'<input type="text" readonly="true" placeholder="请输入日期" class="laydate-icon" value="'+value+'" onclick="laydate({istime: true, format: \'YYYY-MM-DD\'})">'
                +'</div>'
                +'</li>'
            }
            if(extend[i].type=="longtext"){
                var value=getvalue();
                extendhtml+='<li style="width: 100%">'
                +'<b>'+extend[i].name+'</b>'
                +'<div>'
                +'<input type="text"  value="'+value+'"/>'
                +'</div>'
                +'</li>'
            }
        }
        $("#extend ul").html(extendhtml);
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
        showOption();
        getoselectvalue();
    })
}
$("#more_message").click(function(){
    gotovipallmessage();
});
$("#edit_message").click(function(){
    gotovipallmessage();
});
function gotovipallmessage(){
    $('html,body').animate({
        'scrollTop': 0
    },0);
    $("#VIP_Message").hide();
    $("#VIP_edit").show();
    $("#nav_bar").children().eq(0).addClass("active1");
    $("#nav_bar").children().eq(0).siblings().removeClass("active1");
    $(".all_list").children().eq(0).show();
    $(".all_list").children().eq(0).siblings().hide();
    $("#remark").animate({left:0},0.1);
}
function showOption(){
    $(".drop_down input").click(function (){
        var ul=$(this).next(".expand_selection");
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
    });
}
function getoselectvalue(){
    $(".expand_selection li").click(function(){
        $(this).parent().prev().val($(this).html());
        $(this).parent().hide()
    })
}