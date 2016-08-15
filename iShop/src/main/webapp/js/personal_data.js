var oc = new ObjectControl();
//  保存提交
(function(root,factory){
    root.user = factory();
}(this,function(){
    var useroperatejs={};
    useroperatejs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    useroperatejs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    useroperatejs.checkPhone = function(obj,hint){
        var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
        var isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
        if(!this.isEmpty(obj)){
            if(isPhone.test(obj)||isMob.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"联系电话格式不正确!");
                return false;
            }
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    useroperatejs.checkMail = function(obj,hint){
        var reg=/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/;
        if(!this.isEmpty(obj)){
            if(reg.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"邮箱格式不正确！")
                return true;
            }
        }else{
            this.displayHint(hint);
            return true;
        }
    };
    useroperatejs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    useroperatejs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    useroperatejs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    useroperatejs.bindbutton=function(){
        $("#personal_save").click(function(){
            if(useroperatejs.firstStep()){
                var ID=$("#id").val();
                var HEADIMG=$("#IMG").attr("src");
                var USERID=$("#USER_ID").val();
                var CORP_CODE=$("#corp_code").val();//空字段
                var GROUP_CODE=$("#group_code").val();//空字段
                var role_code=$("#role_code").val();//空字段
                var area_code=$("#area_code").val();//空字段
                var store_code=$("#store_code").val();//空字段
                var isactive=$("#isactive").val();//空字段
                var can_login=$("#can_login").val();//空字段
                var USER_NAME=$("#USER_NAME").val();
                var POSITION=$("#POSITION").val();
                var USER_PHONE=$("#IPHONE").val();
                var USER_EMAIL=$("#USER_EMAIL").val();
                var USER_SEX=$("#USER_SEX").val();
                var SEX="";
                if(USER_SEX=="男"){
                    SEX="M";
                }else if(USER_SEX=="女"){
                    SEX="F";
                }

                var _command="/user/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){

                    }
                };
                var _params={};
                _params["id"]=ID;//ID
                _params["user_code"]=USERID;//员工编号
                _params["username"]=USER_NAME;//员工名称
                _params["avatar"]=HEADIMG;//头像
                _params["position"]=POSITION;//职务
                _params["phone"]=USER_PHONE;//手机
                _params["email"]=USER_EMAIL//邮箱
                _params["sex"]=SEX//性别s
                _params["corp_code"]=CORP_CODE;//
                _params["group_code"]=GROUP_CODE;//
                _params["role_code"]=role_code;//
                _params["area_code"]=area_code;//
                _params["store_code"]=store_code;//
                _params["isactive"]=isactive;//
                _params["can_login"]=can_login;//
                useroperatejs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    useroperatejs.ajaxSubmit=function(_command,_params,opt){
        // console.log(JSON.stringify(_params));
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                $(window.parent.document).find('#iframepage').attr("src","/user/user.html");
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
        if(useroperatejs['check' + command]){
            if(!useroperatejs['check' + command].apply(useroperatejs,[obj,hint])){
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
        useroperatejs.bindbutton();
    }
    var obj = {};
    obj.useroperatejs = useroperatejs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function(){
    window.user.init();//初始化
     if($(".pre_title label").text()=="个人资料"){
         var _params={"id":id};
         var _command="/user/myAccount";
         oc.postRequire("get", _command, "", _params, function(data) {
             console.log(data);
             if(data.code=="0"){
                 var msg = JSON.parse(data.message);
                     msg=JSON.parse(msg.user);
                 console.log(msg);
                 $("#id").val(msg.id);
                 $("#IMG").attr("src",msg.avatar);
                 $("#OWN_CORP").val(msg.corp_name);
                 $("#USER_ID").val(msg.user_code);
                 $("#USER_NAME").val(msg.user_name);
                 $("#POSITION").val(msg.position);
                 $("#IPHONE").val(msg.phone);
                 $("#USER_EMAIL").val(msg.email);
                 $("#PASSWORD").val(msg.password);
                 if(msg.sex=="F"){
                     $("#USER_SEX").val("女");
                 }else if(msg.sex=="M"){
                     $("#USER_SEX").val("男");
                 }
                 $("#OWN_AREA").val(msg.area_name);
                 $("#OWN_GROUP").val(msg.group_name);
                 $("#OWN_SHOP").val(msg.store_name);
                 $("#corp_code").val(msg.corp_code);//空字段
                 $("#group_code").val(msg.group_code);//空字段
                 $("#role_code").val(msg.role_code);//空字段
                 $("#area_code").val(msg.area_code);//空字段
                 $("#store_code").val(msg.store_code);//空字段
                 $("#isactive").val(msg.isactive);//空字段
                 $("#can_login").val(msg.can_login);//空字段
                 if($("#OWN_GROUP").val()=="区经"){
                     console.log($("#OWN_GROUP").val());
                     $("#OWN_SHOP").css("display","none");
                     $("#OWN_SHOP").prev().css("display","none");
                 }
             }
         })
     }
})
//性别选择下拉框显示/隐藏
$("#USER_SEX").click(function(){
    if($("#sex_down").css("display")=="none"){
        $("#sex_down").show();
    }else if($("#sex_down").css("display")=="block"){
        $("#sex_down").hide();
    }
});
$("#sex_down li").click(function(){
    var sex=$(this).text();
    $("#USER_SEX").val(sex);
    $("#sex_down").hide();
})
//重置密码时提示消失
$("#first_pwd").focus(function () {
    $(".em_1").css("display","none")
})
$("#second_pwd").focus(function () {
    $(".em_2").css("display","none")
})
//重置密码
$("#baocun").click(function(){
    if($("#first_pwd").val()==""||$("#second_pwd").val()==""||$("#first_pwd").val()!=$("#second_pwd").val()){
        if($("#first_pwd").val()==""){
            $(".em_1").css("display","block")
        }else if($("#second_pwd").val()!=$("#first_pwd").val()){
            $(".em_2").css("display","block")
        }
        return;
    }
    var pwd_creat="/user/change_passwd";
    var user_id=$("#id").val();
    var password=$('#first_pwd').val();
    var _params={};
    _params["password"]=password;
    _params["user_id"]=user_id;
    oc.postRequire("post",pwd_creat,"",_params,function (data) {
        if(data.code=="0"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
            $("#chongzhi_pwd").css('display','none');
            $("#chongzhi_box").css('display','none');
        }else if(data=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: 失败
            });
        }

    })
})

//  关闭
$("#personal_close").click(function () {
    $(window.parent.document).find('#iframepage').attr("src","http://ishop.dev.bizvane.com/");
})

function chongzhi() {
    $("#chongzhi_pwd").css('display','block');
    $("#chongzhi_box").css('display','block');
}
function quxiao() {
    $("#first_pwd").val("");
    $("#second_pwd").val("");
    $("#chongzhi_pwd").css('display','none');
    $("#chongzhi_box").css('display','none');
    $(".em_1").css("display","none");
    $(".em_2").css("display","none")
}