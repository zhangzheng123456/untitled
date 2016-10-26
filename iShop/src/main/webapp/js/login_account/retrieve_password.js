/**
 * Created by Administrator on 2016/10/24.
 */
var oc = new ObjectControl();
var retiveve={
    telright:false,
    verify:false,
    lasttel:null
};
function getCode(){
    $("#get_code").bind("click",function(){
        var N=60;
        var param={};
        var tel=$("#tel").val().trim(); //获取手机号
        param["PHONENUMBER"]=tel;
        if(tel==""){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
            $("#tel_tip").show();
            return false;
        }
        $(this).html(N+' S');
        $("#get_code").unbind("click");
        var t=setInterval(function(){
            N--;
            $("#get_code").html(N+' S');
            if(N<=0){
                clearInterval(t);
                $("#get_code").html("发送验证码");
                getCode();
            }
        },1000);
        oc.postRequire("post", "/authcode","", param, function(data) {
           if(data.code==0){
               //retiveve.verify=data.message;
           }
        })
    });
}
function checkphone(){
    var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
    var param={};
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
    }else {
        param["phone"]=tel;
        retiveve.lasttel=tel;
        oc.postRequire("post", "/user/PhoneExist","", param, function(data) {
            if(data.code==0){
                $("#tel_tip img").attr("src","img/icon_error.png");
                $("#tel_tip a").html("手机号未被注册");
            }else if(data.code==-1){
                $("#tel_tip img").attr("src","img/icon_right.png");
                $("#tel_tip a").html("");
                $("#get_code").trigger("done");
                retiveve.telright=true;
            }
            $("#tel_tip").show();
        });
    }
}
function checkyzm(){
    var yzm=$("#yzm").val().trim(); //获取输入的验证码
    var reg=/^\d{6}$/;
    if(yzm==""){
        retiveve.verify=false;
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("请输入验证码");
        $("#yzm_tip").show();
        return false;
    }else if(reg.test(yzm)==false){
        retiveve.verify=false;
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("验证码不正确");
        $("#yzm_tip").show();
        return false;
    }else if(reg.test(yzm)==true){
        retiveve.verify=true;
    }
        // else if(retiveve.verify==null){
    //    $("#yzm_tip").show();
    //    $("#yzm_tip img").attr("src","img/icon_error.png");
    //    $("#yzm_tip a").html("请先获取验证码");
    //    return false;
    //}else if(retiveve.verify!==null){
    //    console.log(retiveve.verify);
    //    console.log(md5(yzm));
    //    if(md5(yzm)==retiveve.verify){
    //        $("#yzm_tip").show();
    //        $("#yzm_tip img").attr("src","img/icon_right.png");
    //        $("#yzm_tip a").html("")
    //    }else{
    //        $("#yzm_tip").show();
    //        $("#yzm_tip img").attr("src","img/icon_error.png");
    //        $("#yzm_tip a").html("验证码不正确");
    //    }
    //}

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
$(function(){
//如果手机号码不能通过验证
    $("#tel").blur(checkphone);
    $("#yzm").blur(checkyzm);
    $("#pwd").blur(pwd);
    $("#pwdd").blur(pwdd);
    $("#tel").focus(function(){
        $("#get_code").unbind("click");
        $("#tel_tip").hide();
    });
    $("#yzm").focus(function(){
        $("#yzm_tip").hide();
    });
    $("#get_code").on("done",getCode);
    getCode();
    $("#next").click(function(){
        var tel=$("#tel").val().trim();
        var yzm=$("#yzm").val().trim();
        if(tel==""){
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
            $("#tel_tip").show();
        }
        if(yzm==""){
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("请输入验证码");
            $("#yzm_tip").show();
        }
        if(tel!=""&&yzm!=""&&retiveve.telright==true&& retiveve.verify==true){
            var param={};
            param["phone"]=retiveve.lasttel;
            param["authcode"]=yzm;
            oc.postRequire("post", "/checkAuthcode","", param, function(data) {
                if(data.code==0){
                        $("#step1").hide();
                        $("#step2").show();
                }else if(data.code==-1){
                    $("#yzm_tip img").attr("src","img/icon_error.png");
                    $("#yzm_tip a").html("验证码不正确");
                    $("#yzm_tip").show();
                }
            });
            //if(md5(yzm)==retiveve.verify){
            //    $("#step1").hide();
            //    $("#step2").show();
            //}else{
            //    $("#yzm_tip").show();
            //    $("#yzm_tip img").attr("src","img/icon_error.png");
            //    $("#yzm_tip a").html("验证码不正确");
            //}
        }
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
        }
        if(pwdd==""){
            $("#pwdd_tip img").attr("src","img/icon_error.png");
            $("#pwdd_tip a").html("请输入密码");
            $("#pwdd_tip").show();
            return false;
        }
        if(pwd==pwdd&&pwd!=""&&pwdd!=""){
            param['password']=pwdd;
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

    })
});