var oc = new ObjectControl();

 jQuery(document).ready(function(){
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
                 $("#IMG").val(msg.user_code);
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
                 $("#OWN_GROUP").val(msg.group_name);
                 $("#OWN_SHOP").val(msg.area_name);
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
    var user_id=sessionStorage.getItem("id");
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

//  保存提交
(function(root,factory){
    root.personalData = factory();
}(this,function(){
    var personalDatajs={};
    personalDatajs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    personalDatajs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    personalDatajs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    personalDatajs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    personalDatajs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    personalDatajs.bindbutton=function(){
       if(personalDatajs.firstStep()){
       $("#personal_save").click(function () {
           var id = $("#id").val();//编辑时候的id
           var USER_NAME = $("#USER_NAME").val();//姓名
           var IPHONE = $("#IPHONE").val();//手机号
           var EMAIL = $("#EMAIL").val();//邮箱
           var PASSWORD = $("#PASSWORD").val();//密码
           var USER_SEX = $("USER_SEX").val();//性别

           if (USER_SEX == "男") {
               USER_SEX = "M"
           } else if (USER_SEX == "女") {
               USER_SEX = "F"
           }
           var _command = "/user/edit";//接口名
           var opt = {//返回成功后的操作
               success: function () {
               }
           };
           var _params = {
               "id": id,
               "user_name": USER_NAME,
               "phone": IPHONE,
               "email": EMAIL,
               "password": PASSWORD,
               "sex": USER_SEX
           };
           personalDatajs.ajaxSubmit(_command, _params, opt);
       })}else{
               return;
           }
       }
    personalDatajs.ajaxSubmit=function(_command,_params,opt){
        console.log(_params);
        oc.postRequire("get", _command,"", _params, function(data){
            if(data.code=="0"){
                $(window.parent.document).find('#iframepage').attr("src","http://ishop.dev.bizvane.com/");
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
        if(personalDatajs['check' + command]){
            if(!personalDatajs['check' + command].apply(personalDatajs,[obj,hint])){
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
        personalDatajs.bindbutton();
    }
    var obj = {};
    obj.personalDatajs = personalDatajs;
    obj.init = init;
    return obj;
}));
window.personalData.init();
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