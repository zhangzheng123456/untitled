var oc = new ObjectControl();
$(function(){
    var _command1="/home/sys_home";
    var _param={
        time_type:'week'
    };
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
        console.log(JSON.stringify(sysMessage.user_increase))

    })
});