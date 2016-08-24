var oc = new ObjectControl();
$(function(){
    homeGetSys('week');
});

function homeGetSys(timeType){
    var _command1="/home/sys_home";
    var _param={
        time_type:timeType
    };
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post", _command1,"", _param, function(data){
        var sysMessage=JSON.parse(data.message);
        var feedbackContent=JSON.parse(sysMessage.feedback);
        var feedbackHtml="";
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
                +'<div>'+feedbackContent[index].feedback_content+'</div>'
                +'</li>';
            $("#feed_back").html(feedbackHtml);
        }
        whir.loading.remove();//移除加载框
        //console.log(JSON.stringify(sysMessage.user_increase))
    });
}
$("#set_time").click(function(){
    $("#set_Time_ul").toggle();
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