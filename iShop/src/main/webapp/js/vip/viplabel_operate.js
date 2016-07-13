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
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var LABEL_NAME=$("#LABEL_NAME").val();//标签名称
				// var LABEL_TYPE=$("#LABEL_TYPE").val();//标签类型
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
				var _params = {
					"corp_code": OWN_CORP,
					"label_name": LABEL_NAME,
					// "label_type": LABEL_TYPE,
					"isactive": ISACTIVE
				};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(viplabeljs.firstStep()){
				var ID=sessionStorage.getItem("id");//编辑时候的id
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var LABEL_NAME=$("#LABEL_NAME").val();//标签名称
				// var LABEL_TYPE=$("#LABEL_TYPE").val();//标签类型
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
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"label_name": LABEL_NAME,
					// "label_type": LABEL_TYPE,
					"isactive": ISACTIVE
				};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	viplabeljs.ajaxSubmit=function(_command,_params,opt){
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html");
			}else if(data.code=="-1"){
				alert(data.message);
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
		var a="";
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_code=msg.corp_code;//公司编号
				// var label_type=msg.label_type//会员标签
				// $("#LABEL_TYPE option[value='"+label_type+"']").attr("selected","true");
				$("#LABEL_NAME").val(msg.label_name);
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
				getcorplist(corp_code);
				// $('#LABEL_TYPE').searchableSelect();
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
		getcorplist(a);
		// $('#LABEL_TYPE').searchableSelect();
	}
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html");
	});
});
function getcorplist(a){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var corp_html='';
			var c=null;
			for(var i=0;i<msg.corps.length;i++){
				c=msg.corps[i];
				corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('.corp_select select').searchableSelect();
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#STORE_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#STORE_NAME").attr("data-mark","");
			})
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	});//获取企业列表信息
}