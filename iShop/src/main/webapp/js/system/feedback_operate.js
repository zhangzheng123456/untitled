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
			if(feedbackjs.firstStep()){
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
				$(window.parent.document).find('#iframepage').attr("src","/system/feedback.html");
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
	if($(".pre_title label").text()=="编辑用户反馈"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/feedback/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$('#USER_CODE').val(msg.user_code);
				$("#FEEDBACK_DATE").val(msg.feedback_date);
				$("#PHONE").val(msg.phone);
				$("#FEEDBACK_CONTENT").val(msg.feedback_content);
				$("#PROCESS_STATE").val(msg.process_state);
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
});