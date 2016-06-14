var oc = new ObjectControl();
(function(root,factory){
	root.corp = factory();
}(this,function(){
	var corpjs={};
	corpjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	corpjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	corpjs.checkPhone = function(obj,hint){
		console.log()
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
	corpjs.checkCode=function(obj,hint){
		var isCode=/^[C]{1}[0-9]{7}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"请以大写字母C开头从一位到七位之间的数字!");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	}
	corpjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	corpjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	corpjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	corpjs.bindbutton=function(){
		$(".corpadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(corpjs.firstStep()){
				// var CORPID=$("#CORPID").val();
				var CORPNAME=$("#CORPNAME").val();
				var CORPADDRESS=$("#CORPADDRESS").val();
				var CONTACTS=$("#CONTACTS").val();
				var PHONE=$("#PHONE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/corp/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_name":CORPNAME,"address":CORPADDRESS,"contact":CONTACTS,"phone":PHONE,"isactive":ISACTIVE};
				corpjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".corpedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(corpjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var HEADPORTRAIT=$("#preview img").attr("src");
				var CORPID=$("#CORPID").val();
				var CORPNAME=$("#CORPNAME").val();
				var CORPADDRESS=$("#CORPADDRESS").val();
				var CONTACTS=$("#CONTACTS").val();
				var PHONE=$("#PHONE").val();
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/corp/edit";//接口名
				var opt = {//返回成功后的操作s
					success:function(){

					}
				};
				var _params={"id":ID,"avater":HEADPORTRAIT,"corp_code":CORPID,"corp_name":CORPNAME,"address":CORPADDRESS,"contact":CONTACTS,"phone":PHONE,"isactive":ISACTIVE};
				corpjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	corpjs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
				$(window.parent.document).find('#iframepage').attr("src","/corp/corp.html");
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
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
		if(corpjs['check' + command]){
			if(!corpjs['check' + command].apply(corpjs,[obj,hint])){
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
		corpjs.bindbutton();
	}
	var obj = {};
	obj.corpjs = corpjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.corp.init();//初始化
	if($(".pre_title label").text()=="编辑企业信息"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/corp/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#CORPID").val(msg.corp_code)
				$("#CORPNAME").val(msg.corp_name);
				$("#CORPADDRESS").val(msg.address);
				$("#CONTACTS").val(msg.contact);
				$("#PHONE").val(msg.contact_phone);

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
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		});
	}
	var val=sessionStorage.getItem("key");
	val=JSON.parse(val);
    var message=JSON.parse(val.message);
    $("#CORPID").blur(function(){
    	var corp_code=$("#CORPID").val();
    	var _params={};
    	_params["corp_code"]=corp_code;
    	oc.postRequire("post","/corp/Corp_codeExist","", _params, function(data){
            console.log(data);
    	})

    })
    $("#CORPNAME").blur(function(){
    	var corp_name=$("#CORPNAME").val();
    	var _params={};
    	_params["corp_name"]=corp_code;
    	oc.postRequire("post","/corp/Corp_codeExist","", _params, function(data){
            console.log(data);
    	})

    })
    if(message.user_type=="admin"){
    	$(".corpadd_oper_btn ul li:nth-of-type(2)").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/corp/corp.html");
		});
		$(".corpedit_oper_btn ul li:nth-of-type(2)").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/corp/corp.html");
		});
    }else{
    	$(".corpadd_oper_btn ul li:nth-of-type(2)").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/corp/corp_user.html");
		});
		$(".corpedit_oper_btn ul li:nth-of-type(2)").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/corp/corp_user.html");
		});
    }
});