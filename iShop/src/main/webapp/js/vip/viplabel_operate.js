var oc = new ObjectControl();
(function(root,factory){
	root.viplabel = factory();
}(this,function(){
	var viplabeljs={};
	viplabeljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	viplabeljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	viplabeljs.checkPhone = function(obj,hint){
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
	viplabeljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	viplabeljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	viplabeljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	viplabeljs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			if(viplabeljs.firstStep()){
				// var CORPID=$("#CORPID").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var LABEL_NAME=$("#LABEL_NAME").val();
				var LABEL_TYPE=$("#LABEL_TYPE").val();
				// var check_per=$("#check_per").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/VIP/label/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"corp_code":OWN_CORP,"label_name":LABEL_NAME,"label_type":LABEL_TYPE,"isactive":ISACTIVE};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(viplabeljs.firstStep()){
				var ID=sessionStorage.getItem("id");

				var OWN_CORP=$("#OWN_CORP").val();
				var LABEL_NAME=$("#LABEL_NAME").val();
				var LABEL_TYPE=$("#LABEL_TYPE").val();

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
				var _command="/VIP/label/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"label_name":LABEL_NAME,"label_type":LABEL_TYPE,"isactive":ISACTIVE};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	viplabeljs.ajaxSubmit=function(_command,_params,opt){
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
				$(window.parent.document).find('#iframepage').attr("src","/user/roles.html");
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
		if(viplabeljs['check' + command]){
			if(!viplabeljs['check' + command].apply(viplabeljs,[obj,hint])){
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
		viplabeljs.bindbutton();
	}
	var obj = {};
	obj.viplabeljs = viplabeljs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.viplabel.init();//初始化
	if($(".pre_title label").text()=="编辑会员标签"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/VIP/label/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				var OWN_CORP=$("#OWN_CORP").val(msg.role_code);
				var LABEL_NAME=$("#LABEL_NAME").val(msg.role_name);
				var LABEL_TYPE=$("#LABEL_TYPE").val(msg.remark);
				// var check_per=$("#check_per").val(msg.check_per);
				// $("#ROLE_NUM").val(msg.role_num);
				// $("#ROLE_NAME").val(msg.role_name);
				// $("#BEIZHU").val(msg.beizhu);
				var created_time=$("#created_time").val(msg.created_date);
				var creator=$("#creator").val(msg.creater);
				var modify_time=$("#modify_time").val(msg.modified_date);
				var modifier=$("#modifier").val(msg.modifier);			

				$("#OWN_CORP").val(msg.corp_code);
				$("#LABEL_NAME").val(msg.label_name);
				$("#LABEL_TYPE").val(msg.label_type);
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

    $(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/roles.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/roles.html");
	});
	$("#che").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/rolecheck_power.html");
	})
});