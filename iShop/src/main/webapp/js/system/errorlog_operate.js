var oc=new ObjectControl();

jQuery(document).ready(function(){
    // window.param.init();
    if($(".pre_title label").text()=="编辑错误日志"){
        var id=sessionStorage.getItem("id");
        var _params={"id":id};
        var _command="/errorLog/select";
        oc.postRequire("post", _command,"", _params, function(data){
            console.log(data);
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                console.log(msg);
                $("#app_platform").val(msg.app_platform);
                $(".tempalte_area textarea").val(msg.content);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    }

    $(".oper_btn ul li").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/errorlog.html");
    });
});