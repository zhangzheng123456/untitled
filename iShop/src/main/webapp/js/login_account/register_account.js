var oc = new ObjectControl();
$("#PHONENUMBER").focus(function(){//手机号获得焦点的时候做的验证
	$('.PHONENUMBER .notice').html("");
});
$("#PHONENUMBER").blur(function(){//手机号失去焦点的时候的验证
	var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
	var PHONENUMBER=$('#PHONENUMBER').val();//手机号码 
    if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
    	$('.PHONENUMBER .notice').html("手机号码格式不正确!");
    }   
});
$("#PHONECODE").focus(function(){//验证码获取
	$('.PHONECODE .notice').html("");
})
$("#PHONECODE").blur(function(){//验证码失去焦点的时候的验证
	var PHONECODE=$('#PHONECODE').val();//验证码
    if(PHONECODE==""||PHONECODE!==""&&PHONECODE.length<6){
    	$('.PHONECODE .notice').html("验证码长度不正确！");
    }   
});
$("#PASSWORD").focus(function(){//密码获取
	$('.PASSWORD .notice').html("密码6位-16位字母、数字或者英文符号，区分大小写!");
})
$("#PASSWORD").blur(function(){//密码框失去焦点的时候的验证
	var reg =/^[^\u4e00-\u9fa5]{6,16}$/;
	var PASSWORD=$('#PASSWORD').val();//密码
    if(PASSWORD==""||PASSWORD!==""&&reg.test(PASSWORD)==false){
    	$('.PASSWORD .notice').html("密码格式不正确");
    }else if(PASSWORD==""||PASSWORD!==""&&reg.test(PASSWORD)==true){
    	$('.PASSWORD .notice').html("");
    }  
});
$("#repswd").focus(function(){//确认密码获取
	$('.repswd .notice').html("");
});
$("#repswd").blur(function(){//确认密码框失去焦点的时候的验证
	var repswd=$('#repswd').val();//确认密码
	var PASSWORD=$('#PASSWORD').val();
    if(repswd==""||repswd!==""&&repswd!==PASSWORD){
    	$('.repswd .notice').html("密码不一致");
    }
});
$("#USERNAME").focus(function(){//姓名
	$('.USERNAME .notice').html("");
});
$("#USERNAME").blur(function(){//姓名框失去焦点的时候的验证
	var USERNAME=$('#USERNAME').val();//姓名
	var PASSWORD=$('#PASSWORD').val();
    if(USERNAME==""){
    	$('.USERNAME .notice').html("姓名不能为空！");
    }
});
$("#COMPANY").focus(function(){//姓名
	$('.COMPANY .notice').html("");
});
$("#COMPANY").blur(function(){//企业收索框失去焦点的时候的验证
	var COMPANY=$('#USERNAME').val();//姓名
    if(COMPANY==""){
    	$('.COMPANY .notice').html("企业名称不能为空！");
    }
});
$("#addInfo").focus(function(){
	$('.addInfo .notice').html("");
})
$("#addInfo").blur(function(){
	var addInfo=$('#addInfo').val();//姓名
    if(addInfo==""){
    	$('.addInfo .notice').html("详细地址不能为空！");
    }
})

function sendSMS(btn){//手机获取验证码的验证
	var btn=btn;
	var num=10;
	var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
	var PHONENUMBER=$('#PHONENUMBER').val();//手机号码
	var param={};
	param["PHONENUMBER"]=PHONENUMBER;
	if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
		$('.PHONENUMBER .notice').html("手机号码格式不正确!");
		return;
	}else if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==true){
		$(btn).addClass("checkCode col col-30 disabled");
		$(btn).attr("disabled","true");
	}
	var timer=setInterval(function(){
		num--;
		console.log(num);
		$(btn).html(num+"秒");
		if(num<=0){
			$(btn).removeAttr("disabled");
			$(btn).removeClass("disabled");
			$(btn).html("获取验证码");
			clearInterval(timer);
		}
	},1000);
	oc.postRequire("post", "/authcode", "sms", param, function(data){
		console.log(data);
	})	
}
$(function(){
	$('#nextSub').click(function(){
		var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//手机号码的验证
		var red=/^[^\u4e00-\u9fa5]{6,16}$/;//密码的验证
		var PHONENUMBER=$('#PHONENUMBER').val();//手机号码
		var PHONECODE=$('#PHONECODE').val();//验证码
		var PASSWORD=$('#PASSWORD').val();//密码
		var USERNAME=$('#USERNAME').val();//姓名
		var COMPANY=$('#COMPANY').val();//企业名称
		var repswd=$('#repswd').val();//确认密码
		// var BUSINESS=$('#BUSINESS').val();//公司主营业
		var province=$('#province').val();//所在省
		var city=$('#city').val();//所在城市
		var regionId=$('#regionId').val();//所在县区
		var Address=$('#addInfo').val();//详细地址
		var param={};
		param["PHONENUMBER"]=PHONENUMBER;//手机号码
		param["PHONECODE"]=PHONECODE;//验证码
		param["PASSWORD"]=PASSWORD;//密码
		param["USERNAME"]=USERNAME;//姓名
		param["COMPANY"]=COMPANY;//企业名称
		param["PROVINCE"]=province;//所在省
		param["CITY"]=city;//城市
		param["REGIONID"]=regionId;//县区
		param["ADDRESS"]=Address;//详细地址
		console.log(param);
		if(PHONENUMBER==""||PHONECODE==""||PASSWORD==""||repswd==""||USERNAME==""||COMPANY==""||province=="省份"||city=="城市"||regionId=="区县"||Address==""){
			if(province=="省份"||city=="城市"||regionId=="区县"){
				$('#location').html('请正确选择省市区!');
			}
			return;
		};
		if(PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
    		$('.PHONENUMBER .notice').html("手机号码格式不正确!");
    		return;
    	};
    	if(PASSWORD!==""&&reg.test(PASSWORD)==false){
    		$('.PASSWORD .notice').html("密码格式不正确");
    		return;
        };
		oc.postRequire("post", "/register", "reg", param, function(data){
		
		})
	})
})