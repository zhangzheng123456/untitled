var oc = new ObjectControl();
(function(root,factory){
	root.code = factory();
}(this,function(){
	var codejs={};
	codejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	codejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	codejs.checkPhone = function(obj,hint){
		var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
		var isMob=isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8,9]{1}\d{9}$/;//验证手机号码格式正不正确
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
	codejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	codejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	codejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	codejs.bindbutton=function(){
		$(".codeadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(codejs.firstStep()){
				var phone=$('#phone').val();//手机号
				var validate_code=$('#validate_code').val();//验证码
				var platform=$('#platform').val();//来源
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/validatecode/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"phone": phone,
					"validate_code": validate_code,
					"platform": platform,
					"isactive": ISACTIVE
				};
				codejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".codeedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(codejs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var phone=$('#phone').val();//手机号
				var validate_code=$('#validate_code').val();//验证码
				var platform=$('#platform').val();//来源
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
						ISACTIVE="N";
				}
				var _command="/validatecode/edit";//接口名
				var opt = {//返回成功后的操作s
					success:function(){

					}
				};
				var _params = {
					"id": ID,
					"phone": phone,
					"validate_code": validate_code,
					"platform": platform,
					"isactive": ISACTIVE
				};
				codejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	codejs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/validatecode/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/system/authcode_edit.html");
                }
                if(_command=="/validatecode/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/system/authcode.html");
			}else if(data.code=="-1"){
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
		if(codejs['check' + command]){
			if(!codejs['check' + command].apply(codejs,[obj,hint])){
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
		codejs.bindbutton();
	}
	var obj = {};
	obj.codejs = codejs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.code.init();//初始化
	if($(".pre_title label").text()=="编辑验证码信息"){
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
		var _command="/validatecode/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				msg=JSON.parse(msg.validateCode);
				$('#phone').val(msg.phone);//手机号
				$('#validate_code').val(msg.validate_code);//验证码
				$('#platform').val(msg.platform);//来源
				$("#creater").val(msg.creater);
				$("#created_date").val(msg.created_date);
				$("#modified_date").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				var input=$("#is_active")[0];
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		});
	}
    $(".codeadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/authcode.html");
	});
	$(".codeedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/authcode.html");
	});
	$("#back_authCode").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/authcode.html");
	});
});