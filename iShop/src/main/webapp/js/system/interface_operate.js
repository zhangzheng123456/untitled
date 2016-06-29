var oc = new ObjectControl();
(function(root,factory){
	root.interface = factory();
}(this,function(){
	var interfacejs={};
	interfacejs.isEmpty=function(obj){
		if(obj.trim()== "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	interfacejs.checkEmpty=function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	interfacejs.hiddenHint=function(hint){
		hint.removeClass('error_tips');
		hint.html("");
	};
	interfacejs.displayHint=function(hint,content){
		hint.addClass('error_tips');
		if(!content)hint.html(hint.attr("hintInfo"));
		else hint.html(content);
	};
	interfacejs.firstStep=function(){
		var inputText=jQuery(".conpany_msg").find(":text");
		for(var i=0;length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))
				return false;
		}
		return true;
	};
	interfacejs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			if(interfacejs.firstStep()){
				var INTERFACE_VERSION=$("#INTERFACE_VERSION").val();
				var CORP_ID=$("#CORP_ID").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/interfacers/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"version":INTERFACE_VERSION,"crop_code":CORP_ID,"isactive":ISACTIVE};
				interfacejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(areajs.firstStep()){
				var INTERFACE_VERSION=$("#INTERFACE_VERSION").val();
				var CORP_ID=$("#CORP_ID").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/interfacers/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"version":INTERFACE_VERSION,"crop_code":CORP_ID,"isactive":ISACTIVE};
				interfacejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	interfacejs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
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
		if(interfacejs['check' + command]){
			if(!interfacejs['check' + command].apply(interfacejs,[obj,hint])){
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
		interfacejs.bindbutton();
	}
	var obj = {};
	obj.interfacejs = interfacejs;
	obj.init = init;
	return obj;
}));

jQuery(document).ready(function(){
	window.interface.init();//初始化
	if($(".pre_title label").text()=="编辑接口管理"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/interfacers/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				msg=JSON.parse(msg.appversion);
				console.log(msg);
				$('#INTERFACE_VERSION').val(msg.version);
				$("#CORP_ID").val(msg.crop_code);

				$("#creater").val(msg.creater);
				$("#created_date").val(msg.created_date);
				$("#modified_date").val(msg.modified_date);
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
	}else{
	}
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
	});
});