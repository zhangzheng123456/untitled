var oc = new ObjectControl();
(function(root,factory){
	root.app = factory();
}(this,function(){
	var appjs={};
	appjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	appjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	appjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	appjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	appjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	appjs.bindbutton=function(){
		$(".appadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(appjs.firstStep()){
				var platform=$('#platform').val();//运行平台
				var download_addr=$("#download_addr").val();//下载地址
				var version_id=$("#version_id").val();//版本
				var crop_code=$("#crop_code").val();//企业编号
				var force_update=$("#is_force_update").val();//是否强制升级
				var version_describe=$("#version_describe").val();//版本说明
				var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var is_force_update="";
				if(force_update=="是"){
					is_force_update="Y";
				}else if(force_update=="否"){
					is_force_update="N";
				}
				var _command="/appversion/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"platform":platform,
					"download_addr": download_addr,
					"version_id": version_id,
					"crop_code":crop_code,
					"version_describe":version_describe,
					"is_force_update":is_force_update,
					"isactive": ISACTIVE
				};
				appjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".appedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(appjs.firstStep()){
				var platform=$('#platform').val();//运行平台
				var download_addr=$("#download_addr").val();//下载地址
				var version_id=$("#version_id").val();//版本
				var crop_code=$("#crop_code").val();//企业编号
				var force_update=$("#is_force_update").val();//是否强制升级
				var version_describe=$("#version_describe").val();//版本说明
				var ISACTIVE="";
				var input=$("#is_active")[0];
				var ID=sessionStorage.getItem("id");
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var is_force_update="";
				if(force_update=="是"){
					is_force_update="Y";
				}else if(force_update=="否"){
					is_force_update="N";
				}
				var _command="/appversion/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"id": ID,
					"platform":platform,
					"download_addr": download_addr,
					"version_id": version_id,
					"crop_code":crop_code,
					"version_describe":version_describe,
					"is_force_update":is_force_update,
					"isactive": ISACTIVE
				};
				appjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	appjs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/system/appversion.html");
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
		if(appjs['check' + command]){
			if(!appjs['check' + command].apply(appjs,[obj,hint])){
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
		appjs.bindbutton();
	}
	var obj = {};
	obj.appjs = appjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.app.init();//初始化
	if($(".pre_title label").text()=="编辑版本信息"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/appversion/selectById";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				msg=JSON.parse(msg.appversion);
				console.log(msg);
				$('#platform').val(msg.platform);
				$("#download_addr").val(msg.download_addr);
				$("#version_id").val(msg.version_id);
				$("#crop_code").val(msg.crop_code);
				if(msg.is_force_update=="Y"){
					$("#is_force_update").val("是");
				}else if(msg.is_force_update=="N"){
					$("#is_force_update").val("否");
				}
				$("#version_describe").val(msg.version_describe);
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
	$(".appadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/appversion.html");
	});
	$(".appedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/appversion.html");
	});
});