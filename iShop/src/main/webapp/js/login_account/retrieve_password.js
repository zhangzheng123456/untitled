/**
 * Created by Administrator on 2016/10/24.
 */
var oc = new ObjectControl();
var retiveve={
    telright:false,
    //verify:false,
    lasttel:null
};

function checkphone(){
    var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
    var tel = $("#tel").val().trim(); //获取手机号
    var telReg =reg.test(tel);
    if(tel==""){
        $("#tel_tip img").attr("src","img/icon_error.png");
        $("#tel_tip a").html("请输入手机号");
        $("#tel_tip").show();
        return false;
    }
    if(telReg == false){
        $("#tel_tip img").attr("src","img/icon_error.png");
        $("#tel_tip a").html("手机号格式不正确");
        $("#tel_tip").show();
        return false
    }else {
        checkPhoenExist(tel);
    }
}
function checkyzm(){
    var yzm=$("#yzm").val().trim(); //获取输入的验证码
    var reg=/^\d{6}$/;
    if(yzm==""){
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("请输入验证码");
        $("#yzm_tip").show();
        return false;
    }else if(reg.test(yzm)==false){
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("验证码不正确");
        $("#yzm_tip").show();
        return false;
    }
}
function pwdd(){
    var pwdd=$("#pwdd").val();
    var pwd=$("#pwd").val();
    if(pwdd==""){
        $("#pwdd_tip img").attr("src","img/icon_error.png");
        $("#pwdd_tip a").html("请再次输入密码");
        $("#pwdd_tip").show();
    }else if(pwd!=pwdd){
        $("#pwdd_tip img").attr("src","img/icon_error.png");
        $("#pwdd_tip a").html("输入密码不一致");
        $("#pwdd_tip").show();
    }else if(pwd==pwdd&&pwd!=''&&pwdd!=""){
        $("#pwdd_tip img").attr("src","img/icon_right.png");
        $("#pwdd_tip a").html("");
        $("#pwdd_tip").show();
    }
}
function checkPhoenExist(tel,yzm){
    var param={};
    param["phone"]=tel;
    oc.postRequire("post", "/user/PhoneExist","", param, function(data) {
        if(data.code==0){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("手机号未注册");
            $("#tel_tip").show();
        }else if(data.code==-1){
            $("#tel_tip img").attr("src","img/icon_right.png");
            $("#tel_tip a").html("");
            $("#tel_tip").show();
            //checkAuthcode(yzm)
        }
    });
}
function checkAuthcode(tel,yzm){
    var param_1={};
    param_1["phone"]=tel;
    param_1["authcode"]=yzm;
    oc.postRequire("post", "/checkAuthcode","", param_1, function(data) {
        if(data.code==0){
            retiveve.lasttel=tel;
            $("#step1").hide();
            $("#step2").show();
        }else if(data.code==-1){
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("验证码不正确");
            $("#yzm_tip").show();
        }
    });
}
function pwd(){
    var pwd=$("#pwd").val();
    var pwdd=$("#pwdd").val();
    if(pwd==""){
        $("#pwd_tip img").attr("src","img/icon_error.png");
        $("#pwd_tip a").html("请输入密码");
        $("#pwd_tip").show();
    }else{
        $("#pwd_tip img").attr("src","img/icon_right.png");
        $("#pwd_tip a").html("");
        $("#pwd_tip").show();
    }
}
//弹框
function frame(){
    var left=($(window).width())/2;//弹框定位的left值
    var tp=($(window).height())/2;//弹框定位的top值
    $('.frame').remove();
    $(".main").append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;position: absolute"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
    },2000);
}
    $("#get_code").click(function(){
        var N=10;
        var param={};
        var tel=$("#tel").val().trim(); //获取手机号
        var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
        var telReg =reg.test(tel);
        //param["PHONENUMBER"]=tel;
        if(tel==""){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
            $("#tel_tip").show();
            return false;
        }
        if(telReg){
            param["phone"]=tel;
            oc.postRequire("post", "/user/PhoneExist","", param, function(data) {
                if(data.code==0){
                    $("#tel_tip img").attr("src","img/icon_error.png");
                    $("#tel_tip a").html("手机号未注册");
                    $("#tel_tip").show();
                }else if(data.code==-1){
                    $("#tel_tip img").attr("src","img/icon_right.png");
                    $("#tel_tip a").html("");

                    $("#get_code").attr("disabled","disabled");
                        $("#get_code").html(N+' S');
                        var t=setInterval(function(){
                            N--;
                            $("#get_code").html(N+' S');
                            if(N<=0){
                                clearInterval(t);
                                $("#get_code").html("发送验证码");
                                $("#get_code").removeAttr("disabled");
                            }
                        },1000);
                        console.log("发送验证码"+tel)
                    //oc.postRequire("post", "/authcode","", param, function(data) {
                    //    if(data.code==0){
                    //
                    //    }
                    //})
                }
            });
        }
        //if(){
        //    $("get_code").attr("disabled","disabled");
        //    $(this).html(N+' S');
        //    var t=setInterval(function(){
        //        N--;
        //        $("#get_code").html(N+' S');
        //        if(N<=0){
        //            clearInterval(t);
        //            $("#get_code").html("发送验证码");
        //            $("#get_code").removettr("disabled");
        //        }
        //    },1000);
        //    console.log("发送。。。")
        //}
        //oc.postRequire("post", "/authcode","", param, function(data) {
        //    if(data.code==0){
        //
        //    }
        //})
    });
$(function(){
//如果手机号码不能通过验证
    $("#tel").blur(checkphone);
    $("#yzm").blur(checkyzm);
    $("#pwd").blur(pwd);
    $("#pwdd").blur(pwdd);
    $("#tel").focus(function(){
        $("#tel_tip").hide();
    });
    $("#yzm").focus(function(){
        $("#yzm_tip").hide();
    });
    $("#next").click(function(){
        var param_exist={};
        var tel=$("#tel").val().trim();
        var yzm=$("#yzm").val().trim();
        var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
        var regyzm=/^\d{6}$/;
        if(tel==""){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
        }
        if(yzm==""){
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("请输入验证码");
        }
        if(tel!="" && !reg.test(tel)){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("手机号格式不正确");
        }
        if(yzm!="" && !regyzm.test(yzm)){
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("验证码不正确");
        }
        if(regyzm.test(yzm)){
            $("#yzm_tip").hide();
        }
        if( reg.test(tel) && yzm==""){
            $("#tel_tip").hide();
        }
        $("#tel_tip").show();
        $("#yzm_tip").show();
        if(tel!="" && yzm!="" && regyzm.test(yzm) && reg.test(tel)){
            param_exist["phone"]=tel;
            oc.postRequire("post", "/user/PhoneExist","", param_exist, function(data) {
                if(data.code==0){
                    $("#tel_tip img").attr("src","img/icon_error.png");
                    $("#tel_tip a").html("手机号未注册");
                    $("#tel_tip").show();
                }else if(data.code==-1){
                    $("#tel_tip img").attr("src","img/icon_right.png");
                    $("#tel_tip a").html("");
                    $("#tel_tip").show();
                    checkAuthcode(tel,yzm);
                }
            });
        }else{

        }


        //checkAuthcode(tel,yzm);
        //checkPhoenExist(tel,yzm);
    });
    $("#submit").click(function(){
        var pwd=$("#pwd").val();
        var pwdd=$("#pwdd").val();
        var param={};
        param['phone']=retiveve.lasttel;
        if(pwd==""){
            $("#pwd_tip img").attr("src","img/icon_error.png");
            $("#pwd_tip a").html("请输入密码");
            $("#pwd_tip").show();
        }else{
            $("#pwd_tip img").attr("src","img/icon_right.png");
            $("#pwd_tip a").html("");
            $("#pwd_tip").show();
        }
        if(pwdd==""){
            $("#pwdd_tip img").attr("src","img/icon_error.png");
            $("#pwdd_tip a").html("请输入密码");
            $("#pwdd_tip").show();
            return false;
        }
        if(pwd==pwdd&&pwd!=""&&pwdd!=""){
            param['password']=md5(pwdd);
            oc.postRequire("post", "/user/change_passwd", "0", param, function(data) {
                if(data.code==0){
                    window.location.href="login.html"
                }else if(data.code=-1){
                    frame();
                    $('.frame').html("操作失败");
                }
            })
        }else{
            $("#pwdd_tip img").attr("src","img/icon_error.png");
            $("#pwdd_tip a").html("输入密码不一致");
            $("#pwdd_tip").show();
        }
    });
    $(document).bind('keyup', function(e) {
        if (e.keyCode == 13) {
            if($("#step1").css("display")=="block"){
                $("#next").click();
            }
            if($("#step2").css("display")=="block"){
                $("#submit").click();
            }
        }
    });
});/**
 * Created by Administrator on 2016/10/26.
 */
