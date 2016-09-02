var oc = new ObjectControl();
$(function(){
    window.vip.init();
    if($(".pre_title label").text()=="编辑会员分组"){
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
            var _command="/vipGroup/select";
            oc.postRequire("post", _command,"", _params, function(data){
                console.log(data);
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    console.log(msg);
                    $("#vip_id").val(msg.vip_group_name);
                    $("#vip_id").attr("data-name",msg.vip_group_name);
                    $("#vip_num").val(msg.vip_group_code);
                    $("#vip_num").attr("data-name",msg.vip_group_code);
                    $("#vip_remark").val(msg.remark);
                    $("#OWN_CORP option").val(msg.corp.corp_code);
                    console.log(msg.corp.corp_code);
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
                    getcorplist();
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
        console.log("这是新增界面");
        getcorplist();
    }
});
(function(root,factory){
    root.vip = factory();
}(this,function(){
    var vipjs={};
    vipjs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    vipjs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    vipjs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    vipjs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    vipjs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length-1;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    vipjs.bindbutton=function(){
        $(".areaadd_oper_btn ul li:nth-of-type(1)").click(function(){
            var name=$("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            var num=$("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if(vipjs.firstStep()){
                if(name=="N"||num=="N"){
                    if(name=="N"){
                        var div=$("#vip_id").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if(num=="N"){
                        var div=$("#vip_num").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var vip_id=$("#vip_id").val();
                var vip_num=$("#vip_num").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var vip_remark=$("#vip_remark").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/vipGroup/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={"vip_group_code":vip_num,"vip_group_name":vip_id,"corp_code":OWN_CORP,"remark":vip_remark,"isactive":ISACTIVE};
                vipjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $("#edit_save").click(function(){
            var codeMark=$("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            var nameMark=$("#vip_nym").attr("data-mark");//区域编号是否唯一的标志
            if(vipjs.firstStep()){
                //if(nameMark=="N"){
                //    var div=$("#AREA_NAME").next('.hint').children();
                //    div.html("该名称已经存在！");
                //    div.addClass("error_tips");
                //    return;
                //}
                //if(codeMark=="N"){
                //    var div=$("#AREA_ID").next('.hint').children();
                //    div.html("该编号已经存在！");
                //    div.addClass("error_tips");
                //    return;
                //}
                var ID=sessionStorage.getItem("id");
                var AREA_ID=$("#AREA_ID").val();
                var AREA_NAME=$("#AREA_NAME").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/area/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){

                    }
                };
                var _params={"id":ID,"corp_code":OWN_CORP,"area_code":AREA_ID,"area_name":AREA_NAME,"isactive":ISACTIVE};
                vipjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    vipjs.ajaxSubmit=function(_command,_params,opt){
        oc.postRequire("post", _command,"",_params, function(data){
            if(data.code=="0"){
                $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group.html");
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
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
        if(vipjs['check' + command]){
            if(!vipjs['check' + command].apply(vipjs,[obj,hint])){
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
        vipjs.bindbutton();
    };
    var obj = {};
    obj.vipjs = vipjs;
    obj.init = init;
    return obj;
}));
function getcorplist(){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        //console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            //console.log(msg);
            var index=0;
            var corp_html='';
            var c=null;
            for(index in msg.corps){
                c=msg.corps[index];
                corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function(){
                $("#vip_id").val("");
                $("#vip_num").val("");
                $("#vip_id").attr("data-mark","");
                $("#vip_num").attr("data-mark","");
                //console.log($("#OWN_CORP").val())
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
$("#vip_num").blur(function(){
    // var isCode=/^[A]{1}[0-9]{4}$/;
    var _params={};
    var vip_group_code=$(this).val();
    var vip_group_code1=$(this).attr("data-name");
    var corp_code=$("#OWN_CORP").val();
    if(vip_group_code!==""&&vip_group_code!==vip_group_code1){
        _params["vip_group_code"]=vip_group_code;
        _params["corp_code"]=corp_code;
        var div=$(this).next('.hint').children();
        oc.postRequire("post","/vipGroup/vipGroupCodeExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#vip_num").attr("data-mark","Y");
            }else if(data.code=="-1"){
                $("#vip_num").attr("data-mark","N");
                div.addClass("error_tips");
                div.html("该编号已经存在！");
            }
        })
    }
});
//验证名称是否唯一
$("#vip_id").blur(function(){
    var corp_code=$("#OWN_CORP").val();
    var vip_id=$("#vip_id").val();
    var vip_id1=$("#vip_id").attr("data-name");
    var div=$(this).next('.hint').children();
    if(vip_id!==""&&vip_id!==vip_id1){
        var _params={};
        _params["vip_group_name"]=vip_id;
        _params["corp_code"]=corp_code;
        oc.postRequire("post","/vipGroup/vipGroupNameExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#vip_id").attr("data-mark","Y");
            }else if(data.code=="-1"){
                div.html("该名称已经存在！");
                div.addClass("error_tips");
                $("#vip_id").attr("data-mark","N");
            }
        })
    }
});