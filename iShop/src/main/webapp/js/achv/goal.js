var oc = new ObjectControl();
(function(root,factory){
	root.goal = factory();
}(this,function(){
	var shopgoaljs={};
	shopgoaljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	shopgoaljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	shopgoaljs.checkPhone = function(obj,hint){
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
	shopgoaljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	shopgoaljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	shopgoaljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	shopgoaljs.bindbutton=function(){
		$(".oper_btn ul li:nth-of-type(1)").click(function(){
			if(shopgoaljs.firstStep()){
				var CORPID=$("#CORPID").val();
				var CORPNAME=$("#CORPNAME").val();
				var CORPADDRESS=$("#CORPADDRESS").val();
				var CONTACTS=$("#CONTACTS").val();
				var PHONE=$("#PHONE").val();
				var _command="";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"CORPID":CORPID,"CORPNAME":CORPNAME,"CORPADDRESS":CORPADDRESS,"CONTACTS":CONTACTS,"PHONE":PHONE};
				shopgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	shopgoaljs.ajaxSubmit=function(_command,_params,opt){
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
		if(shopgoaljs['check' + command]){
			if(!shopgoaljs['check' + command].apply(shopgoaljs,[obj,hint])){
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
		shopgoaljs.bindbutton();
	}
	var obj = {};
	obj.shopgoaljs = shopgoaljs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.goal.init();//初始化
});