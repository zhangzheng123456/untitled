var oc = new ObjectControl();
(function(root,factory){
	root.feedback = factory();
}(this,function(){
	var feedbackjs={};
	feedbackjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	feedbackjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	feedbackjs.checkPhone = function(obj,hint){
		var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
		var isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
		if(!this.isEmpty(obj)){
			if(isPhone.test(obj)||isMob.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"联系电话格式不正确!");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	feedbackjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	feedbackjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	feedbackjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	feedbackjs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			// var codeMark=$("#USER_CODE").attr("data-mark");
			// var phoneMark=$("#PHONE").attr("data-mark");//手机号码是否唯一的标志
			if(feedbackjs.firstStep()){
			// 	if(phoneMark=="N"){
			// 		var div=$("#PHONE").next('.hint').children();
			// 		div.html("该手机号码已经存在！");
		 //            div.addClass("error_tips");
		 //            return;
			// 	}
			// 	if(codeMark=="N"){
			// 		var div=$("#USER_CODE").next('.hint').children();
			// 		div.html("该编号已经存在！");
		 //            div.addClass("error_tips");
		 //            return;
			// 	}
				var USER_CODE=$("#USER_CODE").val();
				var FEEDBACK_DATE=$("#FEEDBACK_DATE").val();
				var PHONE=$("#PHONE").val();
				var FEEDBACK_CONTENT=$("#FEEDBACK_CONTENT").val();
				var PROCESS_STATE=$("#PROCESS_STATE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/feedback/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"user_code":USER_CODE,"feedback_date":FEEDBACK_DATE,"phone":PHONE,
				"feedback_content":FEEDBACK_CONTENT,"process_state":PROCESS_STATE,"isactive":ISACTIVE};
				feedbackjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(feedbackjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var USER_CODE=$("#USER_CODE").val();
				var FEEDBACK_DATE=$("#FEEDBACK_DATE").val();
				var PHONE=$("#PHONE").val();
				var FEEDBACK_CONTENT=$("#FEEDBACK_CONTENT").val();
				var PROCESS_STATE=$("#PROCESS_STATE").val();
				if(PROCESS_STATE=="未处理"){
					PROCESS_STATE="0";
				}
				if(PROCESS_STATE=="处理中"){
					PROCESS_STATE="1";
				}
				if(PROCESS_STATE=="已完成"){
					PROCESS_STATE="2";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/feedback/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"user_code":USER_CODE,"feedback_date":FEEDBACK_DATE,"phone":PHONE,
				"feedback_content":FEEDBACK_CONTENT,"process_state":PROCESS_STATE,"isactive":ISACTIVE};
				feedbackjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	feedbackjs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/feedback/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/system/feedback_edit.html");
                }
                if(_command=="/feedback/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/system/feedback.html");
			}else if(data.code=="-1"){
				alert(data.message);
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
			whir.loading.remove();//移除加载框
		});
	};
	var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
		var _this;
		if(obj1){
			_this = jQuery(obj1);
		}else{
			_this = jQuery(this);
		}
		var command = _this.attr("verify");
		var obj = _this.val();
		var hint = _this.nextAll(".hint").children();
		if(feedbackjs['check' + command]){
			if(!feedbackjs['check' + command].apply(feedbackjs,[obj,hint])){
				return false;
			}
		}
		return true;
	};
	jQuery(":text").focus(function() {
		var _this = this;
		interval = setInterval(function() {
			bindFun(_this);
		}, 500);
	}).blur(function(event) {
		clearInterval(interval);
	});
	var init=function(){
		feedbackjs.bindbutton();
	}
	var obj = {};
	obj.feedbackjs = feedbackjs;
	obj.init = init;
	return obj;
}));

jQuery(document).ready(function(){
	window.feedback.init();//初始化
	if($(".pre_title label").text()=="反馈详情"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				console.log(action.length);
				if(action.length==0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/feedback/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var msg=JSON.parse(msg.feedback);
				var process_state="";
				if(msg.process_state=="0"){
					process_state="未处理";
				}
				if(msg.process_state=="1"){
					process_state="处理中";
				}
				if(msg.process_state=="2"){
					process_state="已完成";
				}
				console.log(msg);
				$('#USER_CODE').val(msg.user_code);
				$("#FEEDBACK_DATE").val(msg.created_date);
				$("#PHONE").val(msg.phone);
				$("#FEEDBACK_CONTENT").val(msg.feedback_content);
				$("#PROCESS_STATE").val(process_state);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				var input=$(".checkbox_isactive").find("input")[0];
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
			}else if(data.code=="-1"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
			}
		});
	}else{
	}
	//change 事件
	$('#OWN_CORP').change(function(){
		console.log(123);
	})
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/feedback.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/feedback.html");
	});
	$("#back_feedBack").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/feedback.html");
	});


	 //验证手机是否唯一的方法
    // $("#PHONE").blur(function(){
    // 	var phone=$("#PHONE").val();//手机号码
    // 	var phone1=$("#PHONE").attr("data-name");//取手机号的一个标志
    // 	var div=$(this).next('.hint').children();
    // 	var corp_code=$("#OWN_CORP").val();
    // 	if(phone!==""&&phone!==phone1){
	   //  	var _params={};
	   //  	_params["phone"]=phone;
	   //  	_params["corp_code"]=corp_code;
	   //  	oc.postRequire("post","/user/PhoneExist","", _params, function(data){
	   //          if(data.code=="0"){
	   //          	div.html("");
	   //          	$("#PHONE").attr("data-mark","Y");
	   //          }else if(data.code=="-1"){
	   //          	div.html("该名称已经存在！");
	   //          	div.addClass("error_tips");
	   //          	$("#PHONE").attr("data-mark","N");
	   //          }
	   //  	})
	   //  }
    // })
});