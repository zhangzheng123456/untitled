var oc = new ObjectControl();
// (function(root,factory){
// 	root.callback = factory();
// }(this,function(){
// 	var callbackjs={};
// 	callbackjs.isEmpty=function(obj){
// 		if(obj.trim() == "" || obj.trim() == undefined){
// 			return true;
// 		}else{
// 			return false;
// 		}
// 	};
// 	callbackjs.checkEmpty = function(obj,hint){
// 		if(!this.isEmpty(obj)){
// 			this.hiddenHint(hint);
// 			return true;
// 		}else{
// 			this.displayHint(hint);
// 			return false;
// 		}
// 	};
// 	callbackjs.hiddenHint = function(hint){
// 		hint.removeClass('error_tips');
// 		hint.html("");//关闭，如果有友情提示则显示
// 	};
// 	callbackjs.displayHint = function(hint,content){
// 		hint.addClass("error_tips");
// 		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
// 		else hint.html(content);
// 	};
// 	callbackjs.firstStep = function(){
// 		var inputText = jQuery(".conpany_msg").find(":text");
// 		for(var i=0,length=inputText.length;i<length;i++){
// 			if(!bindFun(inputText[i]))return false;
// 		}
// 		return true;
// 	};
// 	// callbackjs.bindbutton=function(){
// 	// 	$(".operadd_btn ul li:nth-of-type(1)").click(function(){
// 	// 		if(callbackjs.firstStep()){
// 	// 			var OWN_CORP=$("#OWN_CORP").val();
// 	// 			var CALLBACK_STUFF=$("#CALLBACK_STUFF").val();
// 	// 			var VIP=$("#VIP").val();
// 	// 			var CALLBACK_DATE=$("#CALLBACK_DATE").val();
// 	// 			var CALLBACK_TYPE=$("#CALLBACK_TYPE").val();
// 	// 			var ISACTIVE="";
// 	// 			var input=$(".checkbox_isactive").find("input")[0];
// 	// 			if(input.checked==true){
// 	// 				ISACTIVE="Y";
// 	// 			}else if(input.checked==false){
// 	// 				ISACTIVE="N";
// 	// 			}
// 	// 			var _command="/VIP/callback/add";//接口名
// 	// 			var opt = {//返回成功后的操作
// 	// 				success:function(){
// 	// 				}
// 	// 			};
// 	// 			var _params = {
// 	// 				"corp_code": OWN_CORP,
// 	// 				"user_code": CALLBACK_STUFF,
// 	// 				"vip_code": VIP,
// 	// 				"callback_time": CALLBACK_DATE,
// 	// 				"callback_type": CALLBACK_TYPE,
// 	// 				"isactive": ISACTIVE
// 	// 			};
// 	// 			callbackjs.ajaxSubmit(_command,_params,opt);
// 	// 		}else{
// 	// 			return;
// 	// 		}
// 	// 	});
// 	// 	$(".operedit_btn ul li:nth-of-type(1)").click(function(){
// 	// 		if(callbackjs.firstStep()){
// 	// 			var ID=sessionStorage.getItem("id");

// 	// 			var CALLBACK_STUFF=$("#CALLBACK_STUFF").val();
// 	// 			var OWN_CORP=$("#OWN_CORP").val();
// 	// 			var VIP=$("#VIP").val();
// 	// 			var CALLBACK_DATE=$("#CALLBACK_DATE").val();
// 	// 			var CALLBACK_TYPE=$("#CALLBACK_TYPE").val();
// 	// 			var ISACTIVE="";
// 	// 			var input=$(".checkbox_isactive").find("input")[0];
// 	// 			if(input.checked==true){
// 	// 				ISACTIVE="Y";
// 	// 			}else if(input.checked==false){
// 	// 				ISACTIVE="N";
// 	// 			}
// 	// 			var _command="/VIP/callback/edit";//接口名
// 	// 			var opt = {//返回成功后的操作
// 	// 				success:function(){
// 	// 				}
// 	// 			};
// 	// 			var _params = {
// 	// 				"id": ID,
// 	// 				"corp_code": OWN_CORP,
// 	// 				"user_code": CALLBACK_STUFF,
// 	// 				"vip_code": VIP,
// 	// 				"callback_time": CALLBACK_DATE,
// 	// 				"callback_type": CALLBACK_TYPE,
// 	// 				"isactive": ISACTIVE
// 	// 			};
// 	// 			callbackjs.ajaxSubmit(_command,_params,opt);
// 	// 		}else{
// 	// 			return;
// 	// 		}
// 	// 	});
// 	// };
// 	// callbackjs.ajaxSubmit=function(_command,_params,opt){
// 	// 	// console.log(JSON.stringify(_params));
// 	// 	// _params=JSON.stringify(_params);
// 	// 	console.log(_params);
// 	// 	oc.postRequire("post", _command,"", _params, function(data){
// 	// 		if(data.code=="0"){
// 	// 			// art.dialog({
// 	// 			// 	time: 1,
// 	// 			// 	lock:true,
// 	// 			// 	cancel: false,
// 	// 			// 	content: data.message
// 	// 			// });
// 	// 			$(window.parent.document).find('#iframepage').attr("src","/vip/callback.html");
// 	// 		}else if(data.code=="-1"){
// 	// 			// alert(data.message);
// 	// 			// art.dialog({
// 	// 			// 	time: 1,
// 	// 			// 	lock:true,
// 	// 			// 	cancel: false,
// 	// 			// 	content: data.message
// 	// 			// });
// 	// 		}
// 	// 	});
// 	// };
// 	var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
// 		var _this;
// 		if(obj1){
// 			_this = jQuery(obj1);
// 		}else{
// 			_this = jQuery(this);
// 		}
// 		var command = _this.attr("verify");
// 		var obj = _this.val();
// 		var hint = _this.nextAll(".hint").children();
// 		if(callbackjs['check' + command]){
// 			if(!callbackjs['check' + command].apply(callbackjs,[obj,hint])){
// 				return false;
// 			}
// 		}
// 		return true;
// 	};
// 	jQuery(":text").focus(function() {
// 		var _this = this;
// 		interval = setInterval(function() {
// 			bindFun(_this);
// 		}, 500);
// 	}).blur(function(event) {
// 		clearInterval(interval);
// 	});
// 	var init=function(){
// 		callbackjs.bindbutton();
// 	}
// 	var obj = {};
// 	obj.callbackjs = callbackjs;
// 	obj.init = init;
// 	return obj;
// }));
jQuery(document).ready(function() {
	var id = sessionStorage.getItem("id");
	var _params = {
		"id": id
	};
	var _command = "/VIP/callback/select";
	oc.postRequire("post", _command, "", _params, function(data) {
		if (data.code == "0") {
			var msg = JSON.parse(data.message)[0];
			console.log(msg);
			$("#OWN_CORP").val(msg.corp_name);//赋值给所属企业
			$("#CALLBACK_DATE").val(msg.message_date);//回访日期
			$("#CALLBACK_TYPE").val(msg.action);//回访类型
			$("#VIP").val(msg.vip_name)//会员名称
			$("#task_dec").val(msg.message_content)//会员名称
			$("#CALLBACK_STUFF").val(msg.user_name)//回访员工
			$("#STUFF_CODE").val(msg.user_code);
			$("#created_time").val(msg.created_date);
			$("#creator").val(msg.creater);
			$("#modify_time").val(msg.modified_date);
			$("#modifier").val(msg.modifier);
            if(msg.type_name.trim()=='电话'){
				$("#task_dec_content").hide();
			}else{
				$("#task_dec_content").show();
			}
			var input = $(".checkbox_isactive").find("input")[0];
			if (msg.isactive == "Y") {
				input.checked = true;
			} else if (msg.isactive == "N") {
				input.checked = false;
			}
		} else if (data.code == "-1") {
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	});
	$(".operadd_btn ul li:nth-of-type(2)").click(function() {
		$(window.parent.document).find('#iframepage').attr("src", "/vip/callback.html");
	});
	$(".operedit_btn ul li").click(function() {
		$(window.parent.document).find('#iframepage').attr("src", "/vip/callback.html");
	});
});