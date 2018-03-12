var oc=new ObjectControl();

jQuery(document).ready(function(){
    // window.param.init();
    if($(".pre_title label").text()=="查看错误日志"){
        var id=sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        $.get("/detail?funcCode="+funcCode+"", function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var action=message.actions;
                console.log(action.length);
                if(action.length==0){
                    $("#edit_save").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
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
    $("#back_errorlog").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/errorlog.html");
    });
});