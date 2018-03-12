var oc = new ObjectControl();
var code;
function createCode() { //随机生成验证码
	code = "";
	var codeLength = 4; //验证码的长度
	var checkCode = document.getElementById("checkCode");
	var codeChars = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'); //所有候选组成验证码的字符，当然也可以用中文的
	for (var i = 0; i < codeLength; i++) {
		var charNum = Math.floor(Math.random() * 52);
		code += codeChars[charNum];
	}
	if (checkCode) {
		checkCode.innerHTML = code;
	}
	console.log(code);
}
createCode();

function login(){
	var phone = $('#login').val().trim();
	var password = $('#password1').val().trim();
	var verifyCode = $('#verifyCode').val().trim();
	var param = {};
	param["phone"] = phone;
	param["password"] = md5(password);
	console.log(param);
	if (phone == '' || password == '' || verifyCode == '' || verifyCode !== '' && verifyCode.toUpperCase() !== code.toUpperCase()) {
		if (phone == "") {
			$(".portlet-msg-error").html("手机号不能为空");
			return;
		}
		if (password == '') {
			$(".portlet-msg-error").html("密码不能为空");
			return;
		}
		if (verifyCode == '') {
			$(".portlet-msg-error").html("验证码不能为空");
			return;
		}
		if (verifyCode !== '' && verifyCode.toUpperCase() !== code.toUpperCase()) {
			$(".portlet-msg-error").html("验证码不正确");
			createCode();
		}
		return;
	}
	$('.btn_login').html("正在登陆");
	oc.postRequire("post", "/userlogin", "0", param, function(data) {
		console.log(data);
		var str = JSON.stringify(data);
		var key = "key";
		sessionStorage.setItem(key, str);
		if (data.code == "0") {
			var message = JSON.parse(data.message);
			var user_type = message.user_type;
			var user_id = message.user_id;
			var val = message.menu;
			sessionStorage.removeItem("sessionCorp");
			console.log(message.menu);
			if (user_type == "admin") {
				window.location.href = "home/index_admin.html?v="+$.now();
			} else if (user_type == "am") {
				window.location.href = "home/index_am.html?v="+$.now();
			} else if (user_type == "gm") {
				window.location.href = "home/index_gm.html?v="+$.now();
			} else if (user_type == "staff") {
				window.location.href = "home/index_staff.html?v="+$.now();
			} else if(user_type == "sm"){
				window.location.href="home/index_sm.html?v="+$.now();
			} else if(user_type == "bm"){
				window.location.href="home/index_bm.html?v="+$.now();
			}else if(user_type=="cm"){
				window.location.href="home/index_cm.html?v="+$.now();
			}
		} else if (data.code == "-1") {
			$(".portlet-msg-error").html("手机号或密码错误");
			$('.btn_login').html("登陆");
		}
	})
}
$(function() { //点击登陆
	$('.btn_login').click(function() {
		login();
	});
	$(window).keydown(function() {
		var event = window.event || arguments[0];
		if (event.keyCode == 13) {
			login();
		}
	});
});
$("#login-box input").bind("input propertychange",function(){
	$(".portlet-msg-error").html("");
});
