var oc = new ObjectControl();
(function(root,factory){
	root.staff = factory();
}(this,function(){
	var staffjs={};
	staffjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	staffjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	staffjs.checkPhone = function(obj,hint){
		// var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
		var isMob=/^((\+?86)|(\(\+86\)))?(13[012356789][0-9]{8}|15[012356789][0-9]{8}|18[02356789][0-9]{8}|147[0-9]{8}|1349[0-9]{7})$/;
		if(!this.isEmpty(obj)){
			if(isMob.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"手机号码格式不正确!");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	staffjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	staffjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	staffjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	staffjs.bindbutton=function(){
		$(".staffadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffjs.firstStep()){
				var STAFF_ID=$("#STAFF_ID").val();
				var STAFF_NUMBER=$("#STAFF_NUMBER").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var STAFF_NAME=$("#STAFF_NAME").val();
				var STAFF_TYPE=$("#STAFF_TYPE").val();
				var IPHONE=$("#IPHONE").val();
				var TEST_IPHONE=$("#TEST_IPHONE").val();
				var BIRTHDAY=$("#BIRTHDAY").val();
				var POSITION=$("#POSITION").val();
				var OWN_STOREID=$("#OWN_STOREID").val();
				var _command="";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"STAFF_ID":STAFF_ID,"STAFF_NUMBER":STAFF_NUMBER,"HEADPORTRAIT":HEADPORTRAIT,"STAFF_NAME":STAFF_NAME,"STAFF_TYPE":STAFF_TYPE,"IPHONE":IPHONE,"TEST_IPHONE":TEST_IPHONE,"BIRTHDAY":BIRTHDAY,"POSITION":POSITION,"OWN_STOREID":OWN_STOREID};
				staffjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".staffedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffjs.firstStep()){
				var STAFF_ID=$("#STAFF_ID").val();
				var STAFF_NUMBER=$("#STAFF_NUMBER").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var STAFF_NAME=$("#STAFF_NAME").val();
				var STAFF_TYPE=$("#STAFF_TYPE").val();
				var IPHONE=$("#IPHONE").val();
				var TEST_IPHONE=$("#TEST_IPHONE").val();
				var BIRTHDAY=$("#BIRTHDAY").val();
				var POSITION=$("#POSITION").val();
				var OWN_STOREID=$("#OWN_STOREID").val();
				var _command="";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"STAFF_ID":STAFF_ID,"STAFF_NUMBER":STAFF_NUMBER,"HEADPORTRAIT":HEADPORTRAIT,"STAFF_NAME":STAFF_NAME,"STAFF_TYPE":STAFF_TYPE,"IPHONE":IPHONE,"TEST_IPHONE":TEST_IPHONE,"BIRTHDAY":BIRTHDAY,"POSITION":POSITION,"OWN_STOREID":OWN_STOREID};
				staffjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	staffjs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		_params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command, _params, function(data){
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
		if(staffjs['check' + command]){
			if(!staffjs['check' + command].apply(staffjs,[obj,hint])){
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
		staffjs.bindbutton();
	}
	var obj = {};
	obj.staffjs = staffjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.staff.init();//初始化
});