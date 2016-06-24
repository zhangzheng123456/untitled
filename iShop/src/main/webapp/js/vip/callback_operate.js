var oc = new ObjectControl();
(function(root,factory){
	root.callback = factory();
}(this,function(){
	var callbackjs={};
	callbackjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	callbackjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	callbackjs.checkPhone = function(obj,hint){
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
	callbackjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	callbackjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	callbackjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	callbackjs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			if(callbackjs.firstStep()){
				var CALLBACK_STUFF=$("#CALLBACK_STUFF").val();
				var VIP=$("#VIP").val();
				var CALLBACK_DATE=$("#CALLBACK_DATE").val();
				var CALLBACK_TYPE=$("#CALLBACK_TYPE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/VIP/callback/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"user_code":CALLBACK_STUFF,"vip_code":VIP,"callback_time":CALLBACK_DATE,
				"callback_type":CALLBACK_TYPE,"isactive":ISACTIVE};
				callbackjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(callbackjs.firstStep()){
				var ID=sessionStorage.getItem("id");

				var CALLBACK_STUFF=$("#CALLBACK_STUFF").val();
				var VIP=$("#VIP").val();
				var CALLBACK_DATE=$("#CALLBACK_DATE").val();
				var CALLBACK_TYPE=$("#CALLBACK_TYPE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/VIP/callback/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"id":ID,"user_code":CALLBACK_STUFF,"vip_code":VIP,"callback_time":CALLBACK_DATE,
				"callback_type":CALLBACK_TYPE,"isactive":ISACTIVE};
				callbackjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	callbackjs.ajaxSubmit=function(_command,_params,opt){
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
				$(window.parent.document).find('#iframepage').attr("src","/vip/callback.html");
			}else if(data.code=="-1"){
				// alert(data.message);
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
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
		if(callbackjs['check' + command]){
			if(!callbackjs['check' + command].apply(callbackjs,[obj,hint])){
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
		callbackjs.bindbutton();
	}
	var obj = {};
	obj.callbackjs = callbackjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.callback.init();//初始化
	if($(".pre_title label").text()=="编辑会员标签"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/VIP/label/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				var CALLBACK_STUFF=$("#CALLBACK_STUFF").val(msg.user_code);
				var VIP=$("#VIP").val(msg.vip_code);
				var CALLBACK_DATE=$("#CALLBACK_DATE").val(msg.callback_time);
				var CALLBACK_TYPE=$("#CALLBACK_TYPE").val(msg.callback_type);

				var created_time=$("#created_time").val(msg.created_date);
				var creator=$("#creator").val(msg.creater);
				var modify_time=$("#modify_time").val(msg.modified_date);
				var modifier=$("#modifier").val(msg.modifier);			

				$("#CALLBACK_STUFF").val(msg.user_code);
				$("#VIP").val(msg.vip_code);
				$("#CALLBACK_DATE").val(msg.callback_time);
				$("#CALLBACK_TYPE").val(msg.callback_type);
				// $("#OWN_DOCU").val(msg.own_docu);
				
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
	}

//获取企业信息列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var index=0;
			var corp_html='';
			var c=null;
			for(index in msg.corps){
				c=msg.corps[index];
				corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			$('.corp_select select').searchableSelect();
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	});
	//change 事件
	$('#OWN_CORP').change(function(){
		console.log(123);
	})
	 $(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/callback.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/callback.html");
	});
});

//     $(".operadd_btn ul li:nth-of-type(2)").click(function(){
// 		$(window.parent.document).find('#iframepage').attr("src","/achv/roles.html");
// 	});
// 	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
// 		$(window.parent.document).find('#iframepage').attr("src","/achv/roles.html");
// 	});
// 	$("#che").click(function(){
// 		$(window.parent.document).find('#iframepage').attr("src","/user/rolecheck_power.html");
// 	})
// });