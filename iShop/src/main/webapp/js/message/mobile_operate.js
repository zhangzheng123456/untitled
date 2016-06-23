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
	mobilejs.checkPhone = function(obj,hint){
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
				var _params={"tem_code":MOBAN_ID,"tem_content":MOBAN_CONTENT,"tem_name":MOBAN_NAME,"type_code":MOBAN_TYPE,"isactive":ISACTIVE};
				mobilejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(mobilejs.firstStep()){
				var ID=sessionStorage.getItem("id");

				var MOBAN_ID=$("#MOBAN_ID").val();
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
				var _params={"id":ID,"tem_code":MOBAN_ID,"tem_content":MOBAN_CONTENT,"tem_name":MOBAN_NAME,"type_code":MOBAN_TYPE,"isactive":ISACTIVE};
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
				// var OWN_CORP=$("#OWN_CORP").val(msg.role_code);
				// var LABEL_NAME=$("#LABEL_NAME").val(msg.role_name);
				// var LABEL_TYPE=$("#LABEL_TYPE").val(msg.remark);

				var MOBAN_ID=$("#MOBAN_ID").val(msg.tem_code);
				var MOBAN_NAME=$("#MOBAN_NAME").val(msg.tem_name);
				var MOBAN_TYPE=$("#MOBAN_TYPE").val(msg.type_code);
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val(msg.tem_content);
				// var check_per=$("#check_per").val(msg.check_per);
				// $("#ROLE_NUM").val(msg.role_num);
				// $("#ROLE_NAME").val(msg.role_name);
				// $("#BEIZHU").val(msg.beizhu);
				var created_time=$("#created_time").val(msg.created_date);
				var creator=$("#creator").val(msg.creater);
				var modify_time=$("#modify_time").val(msg.modified_date);
				var modifier=$("#modifier").val(msg.modifier);			

				$("#MOBAN_ID").val(msg.tem_code);
				$("#MOBAN_NAME").val(msg.tem_name);
				$("#MOBAN_TYPE").val(msg.type_code);
				$("#MOBAN_CONTENT").val(msg.tem_content);
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
		$(window.parent.document).find('#iframepage').attr("src","/message/mobile.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/mobile.html");
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