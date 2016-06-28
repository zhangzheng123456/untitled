var oc = new ObjectControl();
(function(root,factory){
	root.mobile = factory();
}(this,function(){
	var mobilejs={};
	mobilejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	mobilejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	mobilejs.checkCode=function(obj,hint){
		var isCode=/^[M]{1}[0-9]{4}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"模板编号为必填项，支持以大写M开头必须是4位数字的组合！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	mobilejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	mobilejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	mobilejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	mobilejs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			if(mobilejs.firstStep()){
				// var CORPID=$("#CORPID").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var MOBAN_ID=$("#MOBAN_ID").val();
				var MOBAN_NAME=$("#MOBAN_NAME").val();
				var MOBAN_TYPE=$("#MOBAN_TYPE").val();
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();
				// var check_per=$("#check_per").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/message/mobile/template/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"corp_code":OWN_CORP,"tem_code":MOBAN_ID,"tem_content":MOBAN_CONTENT,"tem_name":MOBAN_NAME,"type_code":MOBAN_TYPE,"isactive":ISACTIVE};
				mobilejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(mobilejs.firstStep()){
				var ID=sessionStorage.getItem("id");

				var MOBAN_ID=$("#MOBAN_ID").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var MOBAN_NAME=$("#MOBAN_NAME").val();
				var MOBAN_TYPE=$("#MOBAN_TYPE").val();
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();

				// var ROLE_NUM=$("#ROLE_NUM").val();
				// var ROLE_NAME=$("#ROLE_NAME").val();
				// var BEIZHU=$("#BEIZHU").val();
				// var check_per=$("#check_per").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/message/mobile/template/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"tem_code":MOBAN_ID,"tem_content":MOBAN_CONTENT,"tem_name":MOBAN_NAME,"type_code":MOBAN_TYPE,"isactive":ISACTIVE};
				mobilejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	mobilejs.ajaxSubmit=function(_command,_params,opt){
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
				$(window.parent.document).find('#iframepage').attr("src","/message/mobile.html");
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
		if(mobilejs['check' + command]){
			if(!mobilejs['check' + command].apply(mobilejs,[obj,hint])){
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
		mobilejs.bindbutton();
	}
	var obj = {};
	obj.mobilejs = mobilejs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.mobile.init();//初始化
	if($(".pre_title label").text()=="编辑短信模板"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/message/mobile/template/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				// var MOBAN_ID=$("#MOBAN_ID").val(msg.tem_code);
				// var MOBAN_NAME=$("#MOBAN_NAME").val(msg.tem_name);
				// var MOBAN_TYPE=$("#MOBAN_TYPE").val(msg.type_code);
				// var MOBAN_CONTENT=$("#MOBAN_CONTENT").val(msg.tem_content);

				// var created_time=$("#created_time").val(msg.created_date);
				// var creator=$("#creator").val(msg.creater);
				// var modify_time=$("#modify_time").val(msg.modified_date);
				// var modifier=$("#modifier").val(msg.modifier);

				// $("#MOBAN_ID").val(msg.tem_code);
				// $("#MOBAN_NAME").val(msg.tem_name);
				// $("#MOBAN_TYPE").val(msg.type_code);
				// $("#MOBAN_CONTENT").val(msg.tem_content);
				// // $("#OWN_DOCU").val(msg.own_docu);

				// $("#created_time").val(msg.created_date);
				// $("#creator").val(msg.creater);
				// $("#modify_time").val(msg.modified_date);
				// $("#modifier").val(msg.modifier);

				$("#MOBAN_ID").val(msg.tem_code);
				$("#MOBAN_ID").attr("data-name",msg.tem_code);
				$("#MOBAN_NAME").val(msg.tem_name);
				$("#MOBAN_NAME").attr("data-name",msg.tem_name);
				$("#MOBAN_TYPE").val(msg.type_code);
				$("#MOBAN_TYPE").attr("data-name",msg.type_code);
				$("#MOBAN_CONTENT").val(msg.tem_content);
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);
				// $("#OWN_CORP").val(msg.corp_code);
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
				getcorplist();
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
		getcorplist();
	}
	//验证编号是不是唯一
	$("input[verify='Code']").blur(function(){
		var isCode=/^[M]{1}[0-9]{4}$/;
		var _params={};
		var tem_code=$(this).val();
		var tem_code1=$(this).attr("data-name");
		var corp_code=$("#OWN_CORP").val();
		if(tem_code!==""&&tem_code!==tem_code1&&isCode.test(tem_code)==true){
			_params["tem_code"]=tem_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/message/mobile/template/messageTemplateCodeExist","", _params, function(data){
				if(data.code=="0"){
					div.html("");
					$("#MOBAN_ID").attr("data-mark","Y");
				}else if(data.code=="-1"){
					$("#MOBAN_ID").attr("data-mark","N");
					div.addClass("error_tips");
					div.html("该编号已经存在！");
				}
			})
		}
	});
	//验证名称是否唯一
	$("#MOBAN_NAME").blur(function(){
		var corp_code=$("#OWN_CORP").val();
		var tem_name=$("#MOBAN_NAME").val();
		var tem_name1=$("#MOBAN_NAME").attr("data-name");
		var div=$(this).next('.hint').children();
		if(tem_name!==""&&tem_name!==tem_name1){
			var _params={};
			_params["tem_name"]=tem_name;
			_params["corp_code"]=corp_code;
			oc.postRequire("post","/message/mobile/template/messageTemplateNameExist","", _params, function(data){
				if(data.code=="0"){
					div.html("");
					$("#MOBAN_NAME").attr("data-mark","Y");
				}else if(data.code=="-1"){
					div.html("该名称已经存在！")
					div.addClass("error_tips");
					$("#MOBAN_NAME").attr("data-mark","N");
				}
			})
		}
	});
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/mobile.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/mobile.html");
	});
});
function getcorplist(){
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
			var c=$('#corp_select .selected').attr("data-value");
			mobileType(c);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				mobileType(c);
			})
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#MOBAN_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#MOBAN_NAME").attr("data-mark","");
			})
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
function mobileType(code){
	var _command = "/message/mobile/type/getMessageTypeByUser";
	var _params = {};
	_params["corp_code"]=code;
	oc.postRequire("post", _command, "", _params, function(data) {
		console.log(data);
		if (data.code == "0") {
			var msg = JSON.parse(data.message);
			console.log(msg);
			var index = 0;
			var message_types = '';
			$('#MOBAN_TYPE').empty();
			$('#type_select .searchable-select').remove();
			for (index in msg.message_types) {
				type_tmp = msg.message_types[index];
				message_types += '<option value="' + type_tmp.type_code + '">' + type_tmp.type_name + '</option>';
			}
			$("#MOBAN_TYPE").append(message_types);
			$('.message_type_select select').searchableSelect();
		} else if (data.code = "1") {
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			})
		}
	});
}