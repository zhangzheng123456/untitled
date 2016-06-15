var oc = new ObjectControl();
(function(root,factory){
	root.brand = factory();
}(this,function(){
	var brandjs={};
	brandjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	brandjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	brandjs.checkPhone = function(obj,hint){
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
	brandjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	brandjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	brandjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	brandjs.bindbutton=function(){
		$(".brandadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(brandjs.firstStep()){
				var BRAND_ID=$("#BRAND_ID").val();
				var BRAND_NAME=$("#BRAND_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/brand/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_code":OWN_CORP,"brand_code":BRAND_ID,"brand_name":BRAND_NAME,"isactive":ISACTIVE};
				brandjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".brandedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(brandjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var BRAND_ID=$("#BRAND_ID").val();
				var BRAND_NAME=$("#BRAND_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/brand/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"brand_code":BRAND_ID,"brand_name":BRAND_NAME,"isactive":ISACTIVE};
				brandjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	brandjs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
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
		if(brandjs['check' + command]){
			if(!brandjs['check' + command].apply(brandjs,[obj,hint])){
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
		brandjs.bindbutton();
	}
	var obj = {};
	obj.brandjs = brandjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.brand.init();//初始化
	if($(".pre_title label").text()=="编辑品牌信息"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/brand/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#BRAND_ID").val(msg.brand_code);
				$("#BRAND_NAME").val(msg.brand_name);
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);

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
	//获取所属企业列表
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
	$(".brandadd_oper_btn ul li:nth-of-type(2").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
	$(".brandedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
});