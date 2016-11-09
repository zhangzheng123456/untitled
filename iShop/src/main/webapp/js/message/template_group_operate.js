var oc = new ObjectControl();
$(function(){
    window.templateGroup.init();
    if($(".pre_title label").text()=="编辑消息模板分组"){
        var id=sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        whir.loading.add("",0.5);
        $.get("/detail?funcCode="+funcCode+"", function(data){
            var data=JSON.parse(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var action=message.actions;
                if(action.length<=0){
                    $("#edit_save").remove();
                }
            }
        });
        var _params={};
        _params["id"]=id;
        var _command="/smsTemplateType/select";
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                $("#template_group_name").val(msg.template_type_name);
                $("#template_group_name").attr("data-name",msg.template_type_name);
                $("#template_group_code").val(msg.template_type_code);
                $("#template_group_code").attr("data-name",msg.template_type_code);
                $("#OWN_CORP option").val(msg.corp.corp_code);
                var Code=msg.corp.corp_code;
                $("#OWN_CORP option").text(msg.corp.corp_name);
                //$("#area_shop").val("共"+msg.store_count+"家店铺");
                // $("#OWN_CORP").val(msg.corp_code);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var input=$(".checkbox_isactive").find("input")[0];
                if(msg.isactive=="Y"){
                    input.checked=true;
                }else if(msg.isactive=="N"){
                    input.checked=false;
                }
                getcorplist(Code);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    }else{
        getcorplist();
    }
});
(function(root,factory){
    root.templateGroup = factory();
}(this,function(){
    var templateGroupjs={};
    templateGroupjs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    templateGroupjs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    templateGroupjs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    templateGroupjs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    templateGroupjs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    templateGroupjs.bindbutton=function(){
        $(".areaadd_oper_btn ul li:nth-of-type(1)").click(function(){
            var name=$("#template_group_name").attr("data-mark");//区域名称是否唯一的标志
            var num=$("#template_group_code").attr("data-mark");//区域编号是否唯一的标志
            if(templateGroupjs.firstStep()){
                if(name=="N"||num=="N"){
                    if(name=="N"){
                        var div=$("#template_group_name").next('.hint').children();
                        div.html("该分组名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if(num=="N"){
                        var div=$("#template_group_code").next('.hint').children();
                        div.html("该分组编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var template_group_name=$("#template_group_name").val();
                var template_group_code=$("#template_group_code").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/smsTemplateType/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"template_type_code":template_group_code,"template_type_name":template_group_name,"corp_code":OWN_CORP,"isactive":ISACTIVE};
                templateGroupjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $("#edit_save").click(function(){
            var name=$("#template_group_name").attr("data-mark");//区域名称是否唯一的标志
            var num=$("#template_group_code").attr("data-mark");//区域编号是否唯一的标志
            if(templateGroupjs.firstStep()){
                if(name=="N"){
                    var div=$("#template_group_name").next('.hint').children();
                    div.html("该分组名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                if(num=="N"){
                    var div=$("#template_group_code").next('.hint').children();
                    div.html("该分组编号已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var ID=sessionStorage.getItem("id");
                var template_group_name=$("#template_group_name").val();
                var template_group_code=$("#template_group_code").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/smsTemplateType/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){

                    }
                };
                var _params={"id":ID,"template_type_code":template_group_code,"template_type_name":template_group_name,"corp_code":OWN_CORP,"isactive":ISACTIVE};
                templateGroupjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    templateGroupjs.ajaxSubmit=function(_command,_params,opt){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", _command,"",_params, function(data){
            if(data.code=="0"){
                if(_command=="/smsTemplateType/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/message/template_group_edit.html");
                }
                if(_command=="/smsTemplateType/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
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
        if(templateGroupjs['check' + command]){
            if(!templateGroupjs['check' + command].apply(templateGroupjs,[obj,hint])){
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
        templateGroupjs.bindbutton();
    };
    var obj = {};
    obj.templateGroupjs = templateGroupjs;
    obj.init = init;
    return obj;
}));
function getcorplist(C){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var index=0;
            var corp_html='';
            var c=null;
            for(index in msg.corps){
                c=msg.corps[index];
                if(c.corp_code==C){
                    corp_html+='<option value="'+c.corp_code+'" selected>'+c.corp_name+'</option>';
                }else{
                    corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
                }
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function(){
                $("#template_group_code").val("");
                $("#template_group_name").val("");
                $("#template_group_name").attr("data-mark","");
                $("#template_group_code").attr("data-mark","");
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
//验证编号是不是唯一
$("#template_group_code").blur(function(){
    // var isCode=/^[A]{1}[0-9]{4}$/;
    var _params={};
    var template_group_code=$(this).val();
    var template_group_code1=$(this).attr("data-name");
    var corp_code=$("#OWN_CORP").val();
    if(template_group_code!==""&&template_group_code!==template_group_code1){
        _params["template_type_code"]=template_group_code;
        _params["corp_code"]=corp_code;
        var div=$(this).next('.hint').children();
        oc.postRequire("post","/smsTemplateType/smsTemplateTypeCodeExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#template_group_code").attr("data-mark","Y");
            }else if(data.code=="-1"){
                $("#template_group_code").attr("data-mark","N");
                div.addClass("error_tips");
                div.html("该分组编号已经存在！");
            }
        })
    }
});
//验证名称是否唯一
$("#template_group_name").blur(function(){
    var corp_code=$("#OWN_CORP").val();
    var template_group_name=$(this).val();
    var template_group_name1=$(this).attr("data-name");
    var div=$(this).next('.hint').children();
    if(template_group_name!==""&&template_group_name!==template_group_name1){
        var _params={};
        _params["template_type_name"]=template_group_name;
        _params["corp_code"]=corp_code;
        oc.postRequire("post","/smsTemplateType/smsTemplateTypeNameExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#template_group_name").attr("data-mark","Y");
            }else if(data.code=="-1"){
                div.html("该分组名称已经存在！");
                div.addClass("error_tips");
                $("#template_group_name").attr("data-mark","N");
            }
        })
    }
});
$(".areaadd_oper_btn ul li:nth-of-type(2)").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
$("#edit_close").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
$("#back_tem_group").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
