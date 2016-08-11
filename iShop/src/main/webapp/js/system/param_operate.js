var oc=new ObjectControl();

(function(root,factory){
    root.param = factory();
}(this,function(){
    var paramjs={};
    paramjs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    paramjs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    paramjs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    paramjs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    paramjs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    paramjs.bindbutton=function(){
        $(".operadd_btn ul li:nth-of-type(1)").click(function(){
            if(paramjs.firstStep()){
                var PARAM=$("#PARAM").val();
                var PARAM_NAME=$("#PARAM_NAME").val();
                var REMARK=$("#REMARK").val();
                var _command="/param/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"param_key":PARAM,"param_name":PARAM_NAME,"remark":REMARK};
                whir.loading.add("",0.5);
                paramjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function(){
            if(paramjs.firstStep()){
                var id=sessionStorage.getItem("id");
                var PARAM=$("#PARAM").val();
                var PARAM_NAME=$("#PARAM_NAME").val();
                var REMARK=$("#REMARK").val();
                var _command="/param/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"id":id,"param_key":PARAM,"param_name":PARAM_NAME,"remark":REMARK};
                whir.loading.add("",0.5);
                paramjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    paramjs.ajaxSubmit=function(_command,_params,opt){
        console.log(_params);
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                // art.dialog({
                // 	time: 1,
                // 	lock:true,
                // 	cancel: false,
                // 	content: data.message
                // });
                $(window.parent.document).find('#iframepage').attr("src","/system/param.html");
            }else if(data.code=="-1"){
                art.dialog({
                	time: 1,
                	lock:true,
                	cancel: false,
                	content: data.message
                });
            }
            whir.loading.remove();
        });
    };
    var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if(obj1){
            _this = jQuery(obj1);
        }else{
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if(paramjs['check' + command]){
            if(!paramjs['check' + command].apply(paramjs,[obj,hint])){
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function() {
        var _this = this;
        interval = setInterval(function() {
            bindFun(_this);
        }, 500);
    }).blur(function(event) {
        clearInterval(interval);
    });
    var init=function(){
        paramjs.bindbutton();
    }
    var obj = {};
    obj.paramjs = paramjs;
    obj.init = init;
    return obj;
}));

jQuery(document).ready(function(){
    window.param.init();
    if($(".pre_title label").text()=="编辑参数配置"){
        var id=sessionStorage.getItem("id");
        var _params={"id":id};
        var _command="/param/select";
        oc.postRequire("post", _command,"", _params, function(data){
            console.log(data);
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                console.log(msg);
                $("#PARAM").val(msg.param_key);
                $("#PARAM_NAME").val(msg.param_name);
                $("#REMARK").val(msg.remark);
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
    

    $(".operadd_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/param.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/param.html");
    });
});