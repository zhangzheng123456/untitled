var oc = new ObjectControl();
$(function(){
    homeGetSys('week');
});

function homeGetSys(timeType){
    var _command1="/home/sys_home";
    var _param={
        time_type:timeType
    };
    var dateArr=[];
    var perArr=[];
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post", _command1,"", _param, function(data){
        var sysMessage=JSON.parse(data.message);
        var feedbackContent=JSON.parse(sysMessage.feedback);
        var errorlog=JSON.parse(sysMessage.errorlog);
        var feedbackHtml="";
        var errorhtml="";
        $("#corp_count").html(sysMessage.corp_count);
        $("#store_count").html(sysMessage.store_count);
        $("#user_count").html(sysMessage.user_count);

        $("#corp_new_count").html(sysMessage.corp_new_count);
        $("#store_new_count").html(sysMessage.store_new_count);
        $("#user_new_count").html(sysMessage.user_new_count);
        for(index in feedbackContent){
            feedbackHtml+='<li>'
                +' <label><i class="icon-ishop_8-01"></i>'+feedbackContent[index].feedback_date+'</label>'
                +'<label style="float: right">'+feedbackContent[index].phone+'</label>'
                +'<div title="'+feedbackContent[index].feedback_content+'">'+feedbackContent[index].feedback_content+'</div>'
                +'</li>';
            $("#feed_back").html(feedbackHtml);
        }
        for(index in errorlog){
            errorhtml+='<li>'
                +'<label><i class="icon-ishop_8-01"></i>'+errorlog[index].created_date+'</label>'
                +'<label style="float: right">'+errorlog[index].app_platform+'</label>'
                +'<div title="'+errorlog[index].content+'">'+errorlog[index].content+'</div>'
                +'</li>';
            $("#error_log").html(errorhtml);
        }
        for(index in sysMessage.user_increase){
            perArr.push(sysMessage.user_increase[index].count);
            dateArr.push(sysMessage.user_increase[index].date);
        }
        perArr=perArr.reverse();
        dateArr=dateArr.reverse();
        init(perArr,dateArr);
        whir.loading.remove();//移除加载框
        $(window).resize(function() {
            init(perArr,dateArr);
        });
    });

}
$("#set_time").mouseover(function(){
    $("#set_Time_ul").show();
});
$("#set_time").mouseout(function(){
    $("#set_Time_ul").hide();
});
$("#set_Time_ul").mouseover(function(){
    $("#set_Time_ul").show();
});
$("#set_Time_ul").mouseout(function(){
    $("#set_Time_ul").hide();
});

$("#set_Time_ul li").click(function(){
    $("#set_time b").text($(this).text());
    $("#set_Time_ul").hide();
   if($(this).text()=='最近一周'){
       homeGetSys('week');
   }else{
       homeGetSys('month');
   }
});
$(document).click(function (e) {
    if($(e.target).is("#set_time")||$(e.target).is("#set_time b")|| $(e.target).is("#set_time i")){
        return;
    }else{
        $("#set_Time_ul").hide();
    }
})