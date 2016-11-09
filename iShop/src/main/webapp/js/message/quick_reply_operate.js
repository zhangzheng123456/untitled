var oc = new ObjectControl();
(function(root,factory){
    root.mobile = factory();
}(this,function(){
    var mobilejs={};
    mobilejs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    mobilejs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    mobilejs.checkCode=function(obj,hint){
        var isCode=/^[M]{1}[0-9]{4}$/;
        if(!this.isEmpty(obj)){
            if(isCode.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"模板编号为必填项，支持以大写M开头必须是4位数字的组合！");
                return false;
            }
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    mobilejs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    mobilejs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    mobilejs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    mobilejs.bindbutton=function(){
        $(".operadd_btn ul li:nth-of-type(1)").click(function(){
            if(mobilejs.firstStep()){
                var contentMark=$("#MOBAN_CONTENT").attr("data-mark");//编号是唯一的标志
                if(contentMark=="N"){
                    var div=$("#MOBAN_CONTENT").next('.hint').children();
                    div.html("内容已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var OWN_CORP=$("#OWN_CORP").val();//企业编号
                var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//内容
                if(MOBAN_CONTENT==""){
                    $(".reply_area .hint div").addClass("error_tips");
                    $(".reply_area .hint div").html("内容为必填项，不能为空！");
                    return ;
                }else if(MOBAN_CONTENT!==""){
                    $(".reply_area .hint div").removeClass("error_tips");
                    $(".reply_area .hint div").html("");
                }
                var ISACTIVE="";//是否可用
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/quickReply/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,//公司编号
                    "content": MOBAN_CONTENT,//模板内容
                    "isactive": ISACTIVE//是否可用
                };
                mobilejs.ajaxSubmit(_command, _params, opt);
            }else{
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function(){
            if(mobilejs.firstStep()){
                var contentMark=$("#MOBAN_CONTENT").attr("data-mark");//编号是唯一的标志
                if(contentMark=="N"){
                    var div=$("#MOBAN_CONTENT").next('.hint').children();
                    div.html("内容已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var ID=sessionStorage.getItem("id");
                var MOBAN_ID=$("#MOBAN_ID").val();
                var OWN_CORP=$("#OWN_CORP").val();//编号
                var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//模板内容
                if(MOBAN_CONTENT==""){
                    $(".reply_area .hint div").addClass("error_tips");
                    $(".reply_area .hint div").html("内容为必填项，不能为空！");
                    return ;
                }else if(MOBAN_CONTENT!==""){
                    $(".reply_area .hint div").removeClass("error_tips");
                    $(".reply_area .hint div").html("");
                }
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/quickReply/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "id": ID,
                    "corp_code": OWN_CORP,//公司编号
                    "content": MOBAN_CONTENT,//模板内容
                    "isactive": ISACTIVE//是否可用
                };
                mobilejs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    mobilejs.ajaxSubmit=function(_command,_params,opt){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                if(_command=="/quickReply/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/message/quick_replyedit.html");
                }
                if(_command=="/quickReply/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/message/quick_reply.html");
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
        if(mobilejs['check' + command]){
            if(!mobilejs['check' + command].apply(mobilejs,[obj,hint])){
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
        mobilejs.bindbutton();
    }
    var obj = {};
    obj.mobilejs = mobilejs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function(){
    window.mobile.init();//初始化
    var a="";
    var b="";
    if($(".pre_title label").text()=="编辑快捷回复"){
        var id=sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        $.get("/detail?funcCode="+funcCode+"", function(data){
            var data=JSON.parse(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var action=message.actions;
                if(action.length<=0){
                    $(".corpedit_oper_btn li:eq(0)").remove();
                }
            }
        });
        var _params={"id":id};
        var _command="/quickReply/select";
        oc.postRequire("post", _command,"", _params, function(data){
            console.log(data);
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var corp_code=msg.corp.corp_code;
                $("#MOBAN_CONTENT").val(msg.content);
                $("#MOBAN_CONTENT").attr("data-name",msg.content);
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
                getcorplist(corp_code);
            }else if(data.code=="-1"){
                // art.dialog({
                //     time: 1,
                //     lock:true,
                //     cancel: false,
                //     content: data.message
                // });
            }
        });
    }else{
        getcorplist(a);
    }

    //验证内容是否唯一的方法
    $("#MOBAN_CONTENT").keyup(function(){
        var _params={};
        var content=$(this).val();//员工编号
        var corp_code=$("#OWN_CORP").val();//公司编号
        var content1=$(this).attr("data-name");
        if(content!==""&&content!==content1){
            _params["content"]=content;
            _params["corp_code"]=corp_code;
            var div=$(this).next('.hint').children();
            oc.postRequire("post","/quickReply/quickReplyCodeExist","", _params, function(data){
                if(data.code=="0"){
                    div.html("");
                    $("#MOBAN_CONTENT").attr("data-mark","Y");
                }else if(data.code=="-1"){
                    $("#MOBAN_CONTENT").attr("data-mark","N");
                    div.addClass("error_tips");
                    div.html("该内容已经存在！");
                }
            })
        }else if(content=content1){
            var div=$(this).next('.hint').children();
            $("#MOBAN_CONTENT").attr("data-mark","Y");
            div.html("");
        }
    })

    $(".operadd_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/message/quick_reply.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/message/quick_reply.html");
    });
    $("#back_quick").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/message/quick_reply.html");
    });
});
function getcorplist(a){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
            var corp_html='';
            var c=null;
            for(var i=0;i<msg.corps.length;i++){
                c=msg.corps[i];
                corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if(a!==""){
                $("#OWN_CORP option[value='"+a+"']").attr("selected","true");
            }
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function(){
                $("input[verify='Code']").val("");
                $("#STORE_NAME").val("");
                $("input[verify='Code']").attr("data-mark","");
                $("#STORE_NAME").attr("data-mark","");
            })
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });//获取企业列表信息
}