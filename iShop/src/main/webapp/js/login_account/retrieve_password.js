/**
 * Created by Administrator on 2016/10/24.
 */
$(function(){
    var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
//如果手机号码不能通过验证
    $("#tel").blur(function(){
        var tel = $(this).val().trim(); //获取手机号
        var telReg =reg.test(tel);
        if(tel==""){
            $("#tel_tip").show();
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("手机号不能为空");
            return false;
        }
        if(telReg == false){
            $("#tel_tip").show();
            $("#tel_tip img").attr("src","img/icon_error.png");
            $("#tel_tip a").html("手机号格式不正确")
        }else {
            $("#tel_tip").show();
            $("#tel_tip img").attr("src","img/icon_right.png");
            $("#tel_tip a").html("")
        }
    });
    $("#yzm").blur(function(){
        var yzm=$(this).val().trim(); //获取输入的验证码
        if(yzm==""){
            $("#yzm_tip").show();
            $("#yzm_tip img").attr("src","img/icon_error.png");
            $("#yzm_tip a").html("请输入验证码");
            return false;
        }
        $("#yzm_tip").show();
        $("#yzm_tip img").attr("src","img/icon_right.png");
        $("#yzm_tip a").html("");
    });
    $("#tel").focus(function(){
        $("#tel_tip").hide();
    });
    $("#yzm").focus(function(){
        $("#yzm_tip").hide();
    });
    $("#get_code").click(function(){
        var tel = $("#tel").val().trim(); //获取手机号
        var telReg =reg.test(tel);
        if(telReg==false){
            return false;
        }else{
            alert("可以获取验证码")
        }
    })
});