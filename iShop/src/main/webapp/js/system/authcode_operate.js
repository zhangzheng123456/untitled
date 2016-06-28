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
		var isMob=isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
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
			var nameMark=$("#codeNAME").attr("data-mark");
			var codeMark=$("#codeID").attr("data-mark");
			console.log(nameMark);
			if(codejs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#codeNAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#codeID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
	            }
				var codeID=$("#codeID").val();
				var WXID=$("#WXID").val();
				var codeNAME=$("#codeNAME").val();
				var codeADDRESS=$("#codeADDRESS").val();
				var CONTACTS=$("#CONTACTS").val();
				var PHONE=$("#PHONE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/code/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"code_code":codeID,"app_id":WXID,"code_name":codeNAME,"address":codeADDRESS,"contact":CONTACTS,"phone":PHONE,"isactive":ISACTIVE};
				codejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".codeedit_oper_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#codeNAME").attr("data-mark");
			var codeMark=$("#codeID").attr("data-mark");
			console.log(nameMark);
			if(codejs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#codeNAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#codeID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
	            }
				var ID=sessionStorage.getItem("id");
				var HEADPORTRAIT="";
				if($("#codeID").val()!==''&&$("#preview img").attr("src")!=='../img/bg.png'){
				   HEADPORTRAIT="http://goods-image.oss-cn-hangzhou.aliyuncs.com/Avater/User/iShow/"+$("#codeID").val().trim()+".jpg";
				}else{
				   HEADPORTRAIT="";
				}
				var codeID=$("#codeID").val();
				var WXID=$("#WXID").val();
				var codeNAME=$("#codeNAME").val();
				var codeADDRESS=$("#codeADDRESS").val();
				var CONTACTS=$("#CONTACTS").val();
				var PHONE=$("#PHONE").val();
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
						ISACTIVE="N";
				}
				var _command="/code/edit";//接口名
				var opt = {//返回成功后的操作s
					success:function(){

					}
				};
				var _params={"id":ID,"avater":HEADPORTRAIT,"code_code":codeID,"app_id":WXID,"code_name":codeNAME,"address":codeADDRESS,"contact":CONTACTS,"phone":PHONE,"isactive":ISACTIVE};
				codejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	codejs.ajaxSubmit=function(_command,_params,opt){
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
				$(window.parent.document).find('#iframepage').attr("src","/system/authcode.html");
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
		var _params={"id":id};
		var _command="/code/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#preview img").attr("src",msg.avater);
				if($("#preview img").attr("src").indexOf('http')!==-1){
					$("#c_logo label").html("更换logo");
				}else{
					$("#c_logo label").html("上传logo");
				}
				$("#WXID").val(msg.app_id);
				$("#codeID").val(msg.code_code);
				$("#codeID").attr("data-name",msg.code_code);
				$("#codeNAME").val(msg.code_name);
				$("#codeADDRESS").val(msg.address);
				$("#CONTACTS").val(msg.contact);
				$("#PHONE").val(msg.contact_phone);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				$("#codeNAME").attr("data-name",msg.code_name);
				var input=$(".checkbox_isactive").find("input")[0];
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
				if(msg.is_authorize=="Y"){
					$("#state_val").val("已授权");
				}else if(msg.is_authorize=="N"){
					$("#state_val").val("未授权");
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
});