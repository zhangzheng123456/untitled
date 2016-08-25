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
        for(index in sysMessage.user_increase){
            perArr.push(sysMessage.user_increase[index].count);
            dateArr.push(sysMessage.user_increase[index].date);
        }
        perArr=perArr.reverse();
        dateArr=dateArr.reverse();

        var dateLast=[];
        var perLast=[];
        function selectData(Array){
            if(Array==perArr){
                for(index in Array){
                    if(index%2==0&&timeType=='month'){
                        perLast.push(Array[index])
                    }else if(timeType=='week'){
                        perLast=Array;
                    }
                }
            }else {
                for(index in Array){
                    if(index%2==0&&timeType=='month'){
                        dateLast.push(Array[index])
                    }else if(timeType=='week'){
                        dateLast=Array;
                    }
                }
            }

        }
        selectData(perArr,perLast);
        selectData(dateArr,dateLast);
        init(perLast,dateLast);
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