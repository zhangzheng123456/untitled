var oc = new ObjectControl();
var code;
function createCode() {//随机生成验证码
	code = "";
	var codeLength = 4; //验证码的长度
	var checkCode = document.getElementById("checkCode");
	var codeChars = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'); //所有候选组成验证码的字符，当然也可以用中文的
	for (var i = 0; i < codeLength; i++) {
		var charNum = Math.floor(Math.random() * 52);
		console.log(charNum);
		code += codeChars[charNum];
	}
	if (checkCode) {
		checkCode.innerHTML = code;
	}
}
createCode();
$(function() {//点击登陆
	$('.btn_login').click(function() {
		var phone = $('#login').val();
		var password = $('#password1').val();
		var verifyCode = $('#verifyCode').val();
		var param = {};
		param["phone"] = phone;
		param["password"] = password;
		console.log(param);
		console.log(verifyCode.toUpperCase());
		console.log(code.toUpperCase());
		if (phone== ''|| password ==''|| verifyCode==''||verifyCode!==''&&verifyCode.toUpperCase()!==code.toUpperCase()) {
			if(phone=="") {
				$(".portlet-msg-error").html("用户名不能为空");
			}
			if(password=='') {
				$(".portlet-msg-error").html("密码不能为空");
			}
			if(verifyCode==''){
				$(".portlet-msg-error").html("验证码不能为空");
			}
			if(verifyCode!==''&&verifyCode.toUpperCase()!==code.toUpperCase()){
				$(".portlet-msg-error").html("验证码不正确");	
			}
			return;
		}
		oc.postRequire("post", "/userlogin", "0", param, function(data) {
			var str = JSON.stringify(data);
			var key="key";
			sessionStorage.setItem(key,str);
			if(data.code=="0"){
				var message=JSON.parse(data.message);
                var user_type=message.user_type;
                var user_id=message.user_id;
                var val=message.menu;
                console.log(message.menu);
				if(user_type=="admin"){
                	window.location.href="home/index_admin.html";
				}else if(user_type=="am"){
					window.location.href="home/index_am.html";
				}else if(user_type=="gm"){
					window.location.href="home/index_gm.html";
				}else if(user_type=="staff"){
					window.location.href="home/index_staff.html";
				}
			}else if(data.code=="-1"){
				$(".portlet-msg-error").html("用户名或密码错误");
			}
		})
	})
})