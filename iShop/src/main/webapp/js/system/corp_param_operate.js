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
                var OWN_CORP=$("#OWN_CORP option").val();
                var PARAM_NAME=$("#PARAM_NAME").val();
                var PARAM_ID=$("#PARAM_NAME").val();
                var REMARK=$("#REMARK").val();
                var PARAM_VALUE=$("#PARAM_VALUE").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/corpParam/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"corp_code":OWN_CORP,"param_id":PARAM_ID,"remark":REMARK,"param_value":PARAM_VALUE,"isactive":ISACTIVE};
                whir.loading.add("",0.5);
                paramjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function(){
            if(paramjs.firstStep()){
                var id=sessionStorage.getItem("id");
                var PARAM_ID=$("#PARAM_NAME").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var PARAM_NAME=$("#PARAM_NAME").val();
                var PARAM_VALUE=$("#PARAM_VALUE").val();
                var REMARK=$("#REMARK").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/corpParam/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"id":id,"corp_code":OWN_CORP,"param_id":PARAM_ID,"remark":REMARK,"param_value":PARAM_VALUE,"isactive":ISACTIVE};
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
                $(window.parent.document).find('#iframepage').attr("src","/system/corp_param.html");
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
    if($(".pre_title label").text()=="编辑企业参数"){
        var id=sessionStorage.getItem("id");
        var _params={"id":id};
        var _command="/corpParam/select";
        oc.postRequire("post", _command,"", _params, function(data){
            console.log(data);
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var corp_code=msg.corp_code;
                var param_id=msg.param_id;
                console.log(msg);
                $("#OWN_CORP option").val(msg.corp_code);
                $("#OWN_CORP option").text(msg.corp_name);
                $("#PARAM_NAME").val(msg.param);
                $("#PARAM_NAME").attr("data-name",msg.param);
                $("#REMARK").attr("data-name",msg.remark);
                $("#REMARK").val(msg.remark);
                $("#PARAM_VALUE").val(msg.param_value);
                $("#PARAM_VALUE").attr("data-name",msg.param_value);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                getcorplist(corp_code,param_id);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    }else{
        getcorplist();
    }


    $(".operadd_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/corp_param.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/system/corp_param.html");
    });
});

function getcorplist(a,b){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
            var index=0;
            var corp_html='';
            var c=null;
            for(index in msg.corps){
                c=msg.corps[index];
                corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if(a!==""){
                $("#OWN_CORP option[value='"+a+"']").attr("selected","true");
            }
            $("#OWN_CORP").searchableSelect();
            var c=$('#corp_select .selected').attr("data-value");
            param_data(c,b);
            $("#corp_select .searchable-select-item").click(function(){
                var c=$(this).attr("data-value");
                param_data(c,b);
            })
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
function param_data(c,b){
    var _params={};
    _params["corp_code"]=c;//企业编号
    var _command="/param/getParamByUser";//调取参数
    oc.postRequire("post", _command,"", _params, function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
            var msg_paramName=msg.params;
            $('#PARAM_NAME').empty();
            $('#param_select .searchable-select').remove();
            if(msg_paramName.length>0){
                for(var i=0;i<msg_paramName.length;i++){
                    $('#PARAM_NAME').append("<option value='"+msg_paramName[i].param_id+"'>"+msg_paramName[i].param_key+"</option>");
                }
            }else if(msg_paramName.length<=0){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: "该企业没有参数"
                });
            }
            if(b!==""){
                $("#PARAM_NAME option[value='"+b+"']").attr("selected","true")
            }
            $("#PARAM_NAME").searchableSelect();
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    })
}