var oc = new ObjectControl();
(function(root,factory){
	root.interface = factory();
}(this,function(){
	var interfacejs={};
	interfacejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	interfacejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	interfacejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	interfacejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	interfacejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
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
				var _params={"version":INTERFACE_VERSION,"corp_code":CORP_ID,"isactive":ISACTIVE};
				interfacejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(interfacejs.firstStep()){
				var ID=sessionStorage.getItem("id");
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
				var _params={"id":ID,"version":INTERFACE_VERSION,"corp_code":CORP_ID,"isactive":ISACTIVE};
				interfacejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	interfacejs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
				$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
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
	if($(".pre_title label").text()=="编辑接口信息"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				console.log(action.length);
				if(action.length==0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/interfacers/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				// msg=JSON.parse(msg.appversion);
				msg=JSON.parse(msg.interfacers);
				$('#INTERFACE_VERSION').val(msg.version);
				$("#CORP_ID").val(msg.corp_code);

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
	}else{
	}
	//change 事件
	$('#OWN_CORP').change(function(){
	});
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
	});
	$("#back_interface").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/interface.html");
	});
});