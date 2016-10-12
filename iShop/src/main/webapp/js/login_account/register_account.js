var oc = new ObjectControl();
$("#PHONENUMBER").focus(function(){//手机号获得焦点的时候做的验证
	$('.PHONENUMBER .notice').html("");
});
$("#PHONENUMBER").blur(function(){//手机号失去焦点的时候的验证
	var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
	var PHONENUMBER=$('#PHONENUMBER').val();//手机号码
	var param={};
	param['PHONENUMBER']=PHONENUMBER;
    if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
    	$('.PHONENUMBER .notice').html("手机号码格式不正确");
    }
    if(PHONENUMBER!==""&&reg.test(PHONENUMBER)==true){
    	oc.postRequire("get", "/phone_exist", "find", param, function(data){
    		console.log(data);
    		if(data.code=='-1'){
    			$('.PHONENUMBER .notice').html("该手机号已被绑定,请<a class='link_blue' href='login.html' target='_blank'>直接登录</a>或<a class='link_forget' href='findpwd.html' target='_blank'>忘记密码？</a>");
    			$("#btn").addClass("checkCode col col-30 disabled");
				$("#btn").attr("disabled","true");
				$('#PHONENUMBER').attr("data-mark","N");
    		}else if(data.code=="0"){
    			$('.PHONENUMBER .notice').html("手机号码可用");
    			$("#btn").removeAttr("disabled");
				$("#btn").removeClass("disabled");
				$('#PHONENUMBER').attr("data-mark","Y");
    		};
    	})	
    }  
});
$("#PHONECODE").focus(function(){//验证码获取
	$('.PHONECODE .notice').html("");
})
$("#PHONECODE").blur(function(){//验证码失去焦点的时候的验证
	var PHONECODE=$('#PHONECODE').val();//验证码
	var code=$(this).attr("data-name");//获取名称
    if(PHONECODE!==""&&PHONECODE.length<6){//验证码的长度的验证
    	$('.PHONECODE .notice').html("验证码长度不正确");
    }
    if(code!==""&&code!==undefined&&PHONECODE!==""&&PHONECODE.length==6){
    	if(code!==PHONECODE){
    		$('.PHONECODE .notice').html("验证码不正确");
    		$('#PHONECODE').attr("data-mark","N");
    	}
    	
    }
    if(PHONECODE==""){
    	$('.PHONECODE .notice').html("请输入验证码");
    }  
});
$("#PASSWORD").focus(function(){//密码获取
	$('.PASSWORD .notice').html("密码6位-16位字母、数字或者英文符号，区分大小写");
})
$("#PASSWORD").blur(function(){//密码框失去焦点的时候的验证
	var reg =/^[^\u4e00-\u9fa5]{6,16}$/;//密码框的正则表达式
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
	var PASSWORD=$('#PASSWORD').val();//密码框
    if(repswd==""||repswd!==""&&repswd!==PASSWORD){
    	$('.repswd .notice').html("密码不一致");
    }
});
$("#USERNAME").focus(function(){//姓名
	$('.USERNAME .notice').html("");
});
sibile
$("#USERNAME").blur(function(){//姓名框失去焦点的时候的验证
	var USERNAME=$('#USERNAME').val();//姓名
    if(USERNAME==""){
    	$('.USERNAME .notice').html("姓名不能为空");
    }
});

$("#COMPANY").focus(function(){//公司
	$('.COMPANY .notice').html("");
});

$("#COMPANY").blur(function(){//企业搜索框失去焦点的时候的验证
	var COMPANY=$('#COMPANY').val();//企业名称
	var _params={};
    if(COMPANY==""){
    	$('.COMPANY .notice').html("企业名称不能为空");
    }
    if(COMPANY!==""){
    	_params["corp_name"]=COMPANY;
    	oc.postRequire("post","/corp/CorpNameExist","", _params, function(data){
    		if(data.code=="0"){
	            $('.COMPANY .notice').html("");
	            $("#COMPANY").attr("data-mark","Y");
	        }else if(data.code=="-1"){
	            $('.COMPANY .notice').html("企业名称已经存在");
	            $("#COMPANY").attr("data-mark","N");
	        }
    	})
    }
});

$("#CORPCODE").focus(function(){//公司编号的验证
	$('.CORPCODE .notice').html("支持以大写C开头必须是5位数字的组合");
});

$("#CORPCODE").blur(function(){//企业编号搜索框失去焦点的时候的验证
	var reg=/^[C]{1}[0-9]{5}$/;//企业编号的格式
	var CORPCODE=$('#CORPCODE').val();//姓名
	var _params={};
    if(CORPCODE==""){
    	$('.CORPCODE .notice').html("企业编号不能为空");
    }
    if(CORPCODE!==""&&reg.test(CORPCODE)==false){
    	$('.CORPCODE .notice').html("企业编号格式不正确");
    }
    if(CORPCODE!==""&&reg.test(CORPCODE)==true){
    	_params["corp_code"]=CORPCODE;
		oc.postRequire("post","/corp/Corp_codeExist","", _params, function(data){
	        if(data.code=="0"){
	            $('#CORPCODE').attr("data-mark","Y");
	            $('.CORPCODE .notice').html("");
	        }else if(data.code=="-1"){
	            $('#CORPCODE').attr("data-mark","N");
			    $('.CORPCODE .notice').html("企业编号已经存在");
	        }
		});
    }
});
$("#addInfo").focus(function(){
	$('.addInfo .notice').html("");
})
$("#addInfo").blur(function(){
	var addInfo=$('#addInfo').val();//姓名
    if(addInfo==""){
    	$('.addInfo .notice').html("详细地址不能为空");
    }
})
function sendSMS(btn){//手机获取验证码的验证
	var btn=btn;
	var num=59;
	var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
	var PHONENUMBER=$('#PHONENUMBER').val();//手机号码
	var param={};
	param["PHONENUMBER"]=PHONENUMBER;
	if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
		$('.PHONENUMBER .notice').html("手机号码格式不正确");
		return;
	}else if(PHONENUMBER==""||PHONENUMBER!==""&&reg.test(PHONENUMBER)==true){
		$(btn).addClass("checkCode col col-30 disabled");
		$(btn).attr("disabled","true");
	}
	oc.postRequire("post", "/authcode", "sms", param, function(data){
		console.log(data);
		if(data.code=="0"){
			var code=data.message;//获取短信内容
			$('.PHONECODE .notice').html("验证码发送成功");
			$('#PHONECODE').attr("data-name",code);//给验证码一个标志
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
		}else if(data.code=="-1"){
			$('#PHONECODE').attr("data-mark","");//给验证码一个标志
			$('.PHONECODE .notice').html("验证码发送成功");
			$('.PHONECODE .notice').html("验证码没有发送成功,请重新发送");
			$(btn).removeAttr("disabled");
			$(btn).removeClass("disabled");
			$(btn).html("获取验证码");	
		}
	})	
}
$(function(){
	$('#nextSub').click(function(){
		var reg=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//手机号码的验证
		var red=/^[^\u4e00-\u9fa5]{6,16}$/;//密码的验证
		var phoneMark=$('#PHONENUMBER').attr("data-mark");//phone是否唯一的标志
		var nameMark=$('#COMPANY').attr("data-mark");//公司名称是否唯一的标志
		var codeMark=$('#CORPCODE').attr("data-mark");//公司编号是否唯一的标志
		var phonecodeMark=$("#PHONECODE").attr("data-mark");//手机验证码
		var PHONENUMBER=$('#PHONENUMBER').val();//手机号码
		var PHONECODE=$('#PHONECODE').val();//验证码
		var PASSWORD=$('#PASSWORD').val();//密码
		var USERNAME=$('#USERNAME').val();//姓名
		var COMPANY=$('#COMPANY').val();//企业名称
		var CORPCODE=$('#CORPCODE').val();//企业编号
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
		param["CORPCODE"]=CORPCODE//企业编号
		param["ADDRESS"]=province+city+regionId+Address;//详细地址
		if(CORPCODE==""||PHONENUMBER==""||PHONECODE==""||PASSWORD==""||repswd==""||USERNAME==""||COMPANY==""||province=="省份"||city=="城市"||regionId=="区县"||Address==""){
			if(province=="省份"||city=="城市"||regionId=="区县"){
				$('#location').html('请正确选择省市区');
			}
			return;
		};
		if(PHONENUMBER!==""&&reg.test(PHONENUMBER)==false){
    		$('.PHONENUMBER .notice').html("手机号码格式不正确");
    		return;
    	};
    	if(PASSWORD!==""&&red.test(PASSWORD)==false){
    		$('.PASSWORD .notice').html("密码格式不正确");
    		return;
        };
        if(phoneMark=="N"||nameMark=="N"||codeMark=="N"||phonecodeMark=="N"){
        	return;
        }
		oc.postRequire("post", "/register", "reg", param, function(data){
			console.log(data);
			if(data.code=="0"){
				window.location.href="sucess.html";
			}else if(data.code=="-1"){
				alert("注册失败");
			}
		})
	})
})