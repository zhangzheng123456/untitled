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
        fuzhi(vipDataList);
        getVipPoints(vipDataList);
        showOption();
        getoselectvalue();
    })
}

function getVipPoints(data){
    var parmam_jifen={};
    parmam_jifen['vip_id']=sessionStorage.getItem("id");
    parmam_jifen['corp_code']=data.corp_code;
    parmam_jifen['type']=null;
    oc.postRequire("post","/vip/vipPoints","",parmam_jifen,function(data){
        var Data=JSON.parse(data.message);
        var pointsData=Data.result_points;//积分
        var consumnData=Data.result_consumn;//消费
        var consumnlistData=JSON.parse(consumnData.list);//消费
        var listData=JSON.parse(Data.result_points.list);//积分list
        jifenContent(listData,pointsData);
        var consumnHtml="";
        $("#consume_total").html(consumnData.amount);
        $("#amount_total").html(consumnData.times);
        $("#vip_card_1").html(consumnData.vip_card_type);
        $("#vip_card_num_1").html(consumnData.cardno);
        $("#vip_name_1").html(consumnData.vip_name);
        for(var i=0;i<consumnlistData.length;i++){
            consumnHtml+='<tr>'
            +'<td >'+consumnlistData[i].buy_time+'</td>'
            +'<td>'+consumnlistData[i].order_count+'</td>'
            +'<td>'+consumnlistData[i].order_discount+'</td>'
            +'<td>'+consumnlistData[i].order_total+'<i class="icon-ishop_8-03 style"></i></td>'
            +'</tr>'
        }
        $("#consum tbody").html(consumnHtml)
    })
}
function jifenContent(listData,pointsData){
    $("#points_total").html(pointsData.points);
    $("#vip_card").html(pointsData.vip_card_type);
    $("#vip_card_num").html(pointsData.cardno);
    $("#vip_name1").html(pointsData.vip_name);
    var listHtml="";
    var listHtmlAll="";
    for(var i=0;i<listData.length;i++){
        var a=i+1;
        if(i<10){
            listHtml+='<tr>'
                +'<td>'+listData[i].points+'</td>'
                +'<td>'+listData[i].date+'<i class="icon-ishop_8-03 style"></i></td>'
                +'</tr>'
        }
        listHtmlAll+='<tr>'
            +'<td>'
            +a+'</td>'
            +'<td>'+listData[i].points+'</td>'
            +'<td>'+listData[i].date+'</td>'
            +'</tr>'
    }
    $("#points tbody").html(listHtml);
    $("#points_all tbody").html(listHtmlAll)
}
function fuzhi(data){
    var sex=data.sex;
    if(sex=="male"){
        $("#vip_name").next().addClass("icon-ishop_9-03");
        $("#USER_SEX").val("男");
    }else{
        $("#vip_name").next().addClass("icon-ishop_9-02");
        $("#USER_SEX").val("女");
    }
    $("#vip_name").html(data.vip_name);
    $("#vip_name_edit").val(data.vip_name);
    $("#vip_card_no").html(data.cardno);
    $("#vip_card_no_edit").val(data.cardno);
    $("#vip_card_type").html(data.vip_card_type);
    $("#vip_card_type_edit").val(data.vip_card_type);
    $("#vip_phone").html(data.vip_phone);
    $("#vip_phone_edit").val(data.vip_phone);
    $("#user_name").html(data.user_name);
    $("#user_name_edit").val(data.user_name);
    $("#vip_total_amount").html(data.total_amount);
    $("#join_date").html(data.join_date);
    $("#vip_dormant_time").html(data.dormant_time);
    $("#vip_birthday").html(data.vip_birthday);
    $("#vip_birthday_edit").val(data.vip_birthday);
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
