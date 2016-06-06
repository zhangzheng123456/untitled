// var oc = new ObjectControl();
(function(root,factory){
	root.vip = factory();
}(this,function(){
	var vipjs={};
	vipjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	vipjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	vipjs.checkPhone = function(obj,hint){
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
	vipjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	vipjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	vipjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	vipjs.bindbutton=function(){
		$(".oper_btn ul li:nth-of-type(1)").click(function(){
			if(vipjs.firstStep()){
				// var CORPID=$("#CORPID").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_STORE=$("#OWN_STORE").val();
				var OWN_SALES=$("#OWN_SALES").val();
				// var AREA_ID=$("#AREA_ID").val();
				var VIP_ID=$("#VIP_ID").val();
				var VIP_CARD=$("#VIP_CARD").val();
				var VIP_NAME=$("#VIP_NAME").val();
				var is_leixing=$("#VIP_STYLE").val();
				var is_xingbie=$("#SEX").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/vip/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"own_corp":OWN_CORP,"own_store":OWN_STORE,"own_sales":OWN_SALES,"vip_id":VIP_ID,
				"vip_card":VIP_CARD,"vip_name":VIP_NAME,"vip_style":is_leixing,"sex":is_xingbie,"isactive":ISACTIVE};
				vipjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".oper_btn ul li:nth-of-type(1)").click(function(){
			if(vipjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_STORE=$("#OWN_STORE").val();
				var OWN_SALES=$("#OWN_SALES").val();
				// var AREA_ID=$("#AREA_ID").val();
				var VIP_ID=$("#VIP_ID").val();
				var VIP_CARD=$("#VIP_CARD").val();
				var VIP_NAME=$("#VIP_NAME").val();
				var is_leixing=$("#VIP_STYLE").val();
				var is_xingbie=$("#SEX").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/vip/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"id":ID,"own_corp":OWN_CORP,"own_store":OWN_STORE,"own_sales":OWN_SALES,"vip_id":VIP_ID,
				"vip_card":VIP_CARD,"vip_name":VIP_NAME,"vip_style":is_leixing,"sex":is_xingbie,"isactive":ISACTIVE};
				vipjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	vipjs.ajaxSubmit=function(_command,_params,opt){
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
				$(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
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
		if(vipjs['check' + command]){
			if(!vipjs['check' + command].apply(vipjs,[obj,hint])){
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
		vipjs.bindbutton();
	}
	var obj = {};
	obj.vipjs = vipjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.vip.init();//初始化
	if($(".pre_title label").text()=="编辑企业信息"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/vip/select";
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
});