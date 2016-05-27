var oc = new ObjectControl();
(function(root,factory){
	root.user = factory();
}(this,function(){
	var useroperatejs={};
	useroperatejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	useroperatejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	useroperatejs.checkPhone = function(obj,hint){
		var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
		var isMob=/^((\+?86)|(\(\+86\)))?(13[012356789][0-9]{8}|15[012356789][0-9]{8}|18[02356789][0-9]{8}|147[0-9]{8}|1349[0-9]{7})$/;
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
	useroperatejs.checkMail = function(obj,hint){
		var reg=/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/;
		if(!this.isEmpty(obj)){
			if(reg.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"邮箱格式不正确！")
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	useroperatejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	useroperatejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	useroperatejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	useroperatejs.bindbutton=function(){
		$(".useradd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(useroperatejs.firstStep()){
				var ACCOUNT=$("#ACCOUNT").val();
				var USER_NAME=$("#USER_NAME").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var USER_PHONE=$("#USER_PHONE").val();
				var USER_EMAIL=$("#USER_EMAIL").val();
				var USER_SEX=$("#USER_SEX").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_RIGHT=$("#OWN_RIGHT").val();
				var _command="/user/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"ACCOUNT":ACCOUNT,"USER_NAME":USER_NAME,"HEADPORTRAIT":HEADPORTRAIT,"USER_PHONE":USER_PHONE,"USER_EMAIL":USER_EMAIL,"USER_SEX":USER_SEX,"OWN_CORP":OWN_CORP,"OWN_RIGHT":OWN_RIGHT};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".useredit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(useroperatejs.firstStep()){
				var ACCOUNT=$("#ACCOUNT").val();
				var USER_NAME=$("#USER_NAME").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var USER_PHONE=$("#USER_PHONE").val();
				var USER_EMAIL=$("#USER_EMAIL").val();
				var USER_SEX=$("#USER_SEX").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_RIGHT=$("#OWN_RIGHT").val();
				var _command="/user/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"ACCOUNT":ACCOUNT,"USER_NAME":USER_NAME,"HEADPORTRAIT":HEADPORTRAIT,"USER_PHONE":USER_PHONE,"USER_EMAIL":USER_EMAIL,"USER_SEX":USER_SEX,"OWN_CORP":OWN_CORP,"OWN_RIGHT":OWN_RIGHT};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	useroperatejs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		_params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(opt.success){
					opt.success();
				}
				// window.location.href="";
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data[0].message
				});
			}
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
		if(useroperatejs['check' + command]){
			if(!useroperatejs['check' + command].apply(useroperatejs,[obj,hint])){
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
		useroperatejs.bindbutton();
	}
	var obj = {};
	obj.useroperatejs = useroperatejs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.user.init();//初始化
	var id=sessionStorage.getItem("id");
	var _params={"id":id};
	var _command="/user/select";
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=data.message;
			console.log(msg);
			$("#ACCOUNT").val(msg["user_code"]);
			$("#USER_NAME").val(JSON.parse(msg)["username"]);
			$("#preview img").attr("src",msg.avater);
			$("#USER_PHONE").val(msg.phone);
			$("#USER_EMAIL").val(msg.email);
			if(msg.sex=="M"){
				$("#USER_SEX").val("女");
			}else if(msg.sex=="F"){
				$("#USER_SEX").val("男");
			}
			$("#OWN_CORP").val(msg.corp_code);
			$("#OWN_RIGHT").val(msg.role_code);
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data[0].message
			});
		}
	});
});