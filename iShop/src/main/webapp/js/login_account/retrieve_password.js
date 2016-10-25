/**
 * Created by Administrator on 2016/10/24.
 */
var oc = new ObjectControl();
var retiveve={
    telright:false,
    verify:null
};
function getCode(){
    $("#get_code").bind("click",function(){
        var N=60;
        var param={};
        var tel=$("#tel").val().trim(); //获取手机号
        param["PHONENUMBER"]=tel;
        if(tel==""){
            $("#tel_tip").show();
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
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
               retiveve.verify=data.message;
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
        $("#tel_tip").show();
        $("#tel_tip img").attr("src","img/icon_error.png");
        $("#tel_tip a").html("请输入手机号");
        return false;
    }
    if(telReg == false){
        $("#tel_tip").show();
        $("#tel_tip img").attr("src","img/icon_error.png");
        $("#tel_tip a").html("手机号格式不正确")
    }else {
        param["phone"]=tel;
        $("#tel_tip").show();
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
        });
    }
}
function checkyzm(){
    var yzm=$("#yzm").val().trim(); //获取输入的验证码
    if(yzm==""){
        $("#yzm_tip").show();
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("请输入验证码");
        return false;
    }else if(retiveve.verify==null){
        $("#yzm_tip").show();
        $("#yzm_tip img").attr("src","img/icon_error.png");
        $("#yzm_tip a").html("请先获取验证码");
    }else if(retiveve.verify==null){

}

}
$(function(){
//如果手机号码不能通过验证
    $("#tel").blur(checkphone);
    $("#yzm").blur(checkyzm);
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
            $("#tel_tip").show();
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("请输入手机号");
        }
        if(yzm==""){
            $("#yzm_tip").show();
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("请输入验证码");
        }
        if(tel!=""&&yzm!=""&&retiveve.telright==true){
            if(md5(yzm)==retiveve.verify){
                $("#step1").hide();
                $("#step2").show();
            }else{
                $("#yzm_tip").show();
                $("#yzm_tip img").attr("src","img/icon_error.png");
                $("#yzm_tip a").html("验证码不正确");
            }
        }
    })
});