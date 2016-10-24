/**
 * Created by Administrator on 2016/10/24.
 */
var oc = new ObjectControl();
$(function(){
    var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;
//如果手机号码不能通过验证
    $("#tel").blur(function(){
        var param={};
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
            param["phone"]=tel;
            $("#tel_tip").show();
                oc.postRequire("post", "/user/PhoneExist","", param, function(data) {
                    if(data.code==0){
                        $("#tel_tip img").attr("src","img/icon_error.png");
                        $("#tel_tip a").html("手机号未被注册");
                    }else if(data.code==-1){
                        $("#tel_tip img").attr("src","img/icon_right.png");
                        $("#tel_tip a").html("");
                        $("#get_code").trigger("done",tel)
                    }
                });
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
        $("#get_code").unbind("click");
        $("#tel_tip").hide();
    });
    $("#yzm").focus(function(){
        $("#yzm_tip").hide();
    });

    $("#get_code").on("done",getCode);
    function getCode(){
        $("#get_code").bind("click",function(tel){
            var param={};
            param["PHONENUMBER"]=tel;
            oc.postRequire("post", "/authcode","sms", param, function(data) {
                console.log(data)
            })
        });
    }
});