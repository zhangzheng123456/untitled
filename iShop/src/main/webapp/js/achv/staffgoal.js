var oc = new ObjectControl();
(function(root,factory){
	root.staffgoal = factory();
}(this,function(){
	var staffgoaljs={};
	staffgoaljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	staffgoaljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	staffgoaljs.checkPhone = function(obj,hint){
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
	staffgoaljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	staffgoaljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	staffgoaljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	staffgoaljs.bindbutton=function(){
		$(".staffgoaladd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffgoaljs.firstStep()){
				var OWN_CORP=$("#OWN_CORP").val();
				var SHOP_ID=$("#SHOP_ID").val();
				var STAFF_ID=$("#STAFF_ID").val();
				var SHOP_NAME=$("#SHOP_NAME").val();
				var TIME_TYPE=$("#TIME_TYPE").val();
				var PER_GOAL=$("#PER_GOAL").val();
				var DATE=$("#DATE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"corp_code":OWN_CORP,"store_code":SHOP_ID,"user_code":STAFF_ID,"store_name":SHOP_NAME,"achv_type":TIME_TYPE,"achv_goal":PER_GOAL,"end_time":DATE,"isactive":ISACTIVE};
				staffgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".staffgoaledit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffgoaljs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var SHOP_ID=$("#SHOP_ID").val();
				var STAFF_ID=$("#STAFF_ID").val();
				var SHOP_NAME=$("#SHOP_NAME").val();
				var TIME_TYPE=$("#TIME_TYPE").val();
				var PER_GOAL=$("#PER_GOAL").val();
				var DATE=$("#DATE").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"store_code":SHOP_ID,"user_code":STAFF_ID,"store_name":SHOP_NAME,"achv_type":TIME_TYPE,"achv_goal":PER_GOAL,"end_time":DATE,"isactive":ISACTIVE};
				staffgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	staffgoaljs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				// if(opt.success){
				// 	opt.success();
				// }
				// window.location.href="";
				$(window.parent.document).find('#iframepage').attr("src","/user/roles.html");
			}else if(data.code=="-1"){
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
		if(staffgoaljs['check' + command]){
			if(!staffgoaljs['check' + command].apply(staffgoaljs,[obj,hint])){
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
		staffgoaljs.bindbutton();
	}
	var obj = {};
	obj.staffgoaljs = staffgoaljs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.role.init();//初始化
	if($(".pre_title label").text()=="编辑员工业绩目标"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/storeAchvGoal/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				var OWN_CORP=$("#OWN_CORP").val(msg.corp_code);
				var SHOP_ID=$("#SHOP_ID").val(msg.store_code);
				var STAFF_ID=$("#STAFF_ID").val(msg.user_code);
				var SHOP_NAME=$("#SHOP_NAME").val(msg.store_name);
				var TIME_TYPE=$("#TIME_TYPE").val(msg.achv_type);
				var PER_GOAL=$("#PER_GOAL").val(msg.achv_goal);
				var DATE=$("#DATE").val(msg.end_time);
				// var check_per=$("#check_per").val(msg.check_per);
				// $("#ROLE_NUM").val(msg.role_num);
				// $("#ROLE_NAME").val(msg.role_name);
				// $("#BEIZHU").val(msg.beizhu);
				var created_time=$("#created_time").val(msg.created_date);
				var creator=$("#creator").val(msg.creater);
				var modify_time=$("#modify_time").val(msg.modified_date);
				var modifier=$("#modifier").val(msg.modifier);			

				$("#OWN_CORP").val(msg.own_corp);
				$("#SHOP_ID").val(msg.shop_id);
				$("#SHOP_NAME").val(msg.shop_name);
				$("#TIME_TYPE").val(msg.time_type);
				$("#PER_GOAL").val(msg.per_goal);
				$("#DATE").val(msg.date);
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

$(".shopgoaladd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/roles.html");
	});
	$(".shopgoaledit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/roles.html");
	});
});