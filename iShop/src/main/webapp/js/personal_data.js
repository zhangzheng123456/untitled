var oc = new ObjectControl();

 jQuery(document).ready(function(){
     if($(".pre_title label").text()=="个人资料"){
         var id=sessionStorage.getItem("id");
         var _params={"id":id};
         var _command="/user/myAccount";
         oc.postRequire("get", _command, "", _params, function(data) {
             console.log(data);
             if(data.code=="0"){
                 var msg = JSON.parse(data.message);
                     msg=JSON.parse(msg.user);
                 console.log(msg);
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

//关闭
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