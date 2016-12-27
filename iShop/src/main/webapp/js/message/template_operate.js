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
	mobilejs.checkCode=function(obj,hint){
		var isCode=/^[M]{1}[0-9]{4}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"模板编号为必填项，支持以大写M开头必须是4位数字的组合！");
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
				var OWN_CORP=$("#OWN_CORP").val();//企业编号
				var MOBAN_ID=$("#MOBAN_ID").val();//模板编号
				var MOBAN_NAME=$("#MOBAN_NAME").val();//模板名称
				var template_type=$("#OWN_template").val();//模板类型
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//模板内容
				var ISACTIVE="";//是否可用
				var input=$(".checkbox_isactive").find("input")[0];
				if(template_type==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"该企业没有设置消息模板分组"
					});
					return;
				}
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
				var _params = {
					"corp_code": OWN_CORP,//公司编号
					"template_code": MOBAN_ID,//模板编号
					"template_content": MOBAN_CONTENT,//模板内容
					"template_name": MOBAN_NAME,//模板名称
					 "template_type": template_type,//模板类型
					"isactive": ISACTIVE//是否可用
				};
				mobilejs.ajaxSubmit(_command, _params, opt);
			}else{
				return;
			}
		});
		$(".operedit_btn ul li:nth-of-type(1)").click(function(){
			if(mobilejs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var MOBAN_ID=$("#MOBAN_ID").val();
				var OWN_CORP=$("#OWN_CORP").val();//模板编号
				var MOBAN_NAME=$("#MOBAN_NAME").val();//模板名称
				var template_type=$("#OWN_template").val();//模板类型
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//模板内容
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(template_type==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"该企业没有设置消息模板分组"
					});
					return;
				}
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
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,//公司编号
					"template_code": MOBAN_ID,//模板编号
					"template_content": MOBAN_CONTENT,//模板内容
					"template_name": MOBAN_NAME,//模板名称
					 "template_type": template_type,//模板类型
					"isactive": ISACTIVE//是否可用
				};
				mobilejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	mobilejs.ajaxSubmit=function(_command,_params,opt){
		console.log(_params);
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/message/mobile/template/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/message/template_edit.html");
                }
                if(_command=="/message/mobile/template/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/message/template.html");
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
			whir.loading.remove();//移除加载框
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
	var a="";
    var b="";
	if($(".pre_title label").text()=="编辑消息模板"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			var data=JSON.parse(data);
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length<=0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/message/mobile/template/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_code=msg.corp.corp_code;
				var template_type=msg.template_type;
				// var template_type=msg.template_type;
				$("#MOBAN_ID").val(msg.template_code);
				$("#MOBAN_ID").attr("data-name",msg.template_code);
				$("#MOBAN_NAME").val(msg.template_name);
				$("#MOBAN_NAME").attr("data-name",msg.template_name);
				$("#MOBAN_TYPE").val(msg.type_code);
				$("#MOBAN_TYPE").attr("data-name",msg.type_code);
				$("#MOBAN_CONTENT").val(msg.template_content);
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
				getcorplist(corp_code,template_type);
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
		getcorplist(a,b);
	}
	//验证编号是不是唯一
	$("input[verify='Code']").blur(function(){
    	var isCode=/^[M]{1}[0-9]{4}$/;
    	var _params={};
    	var template_code=$(this).val();
    	var template_code1=$(this).attr("data-name");
    	var corp_code=$("#OWN_CORP").val();
		if(template_code!==""&&template_code!==template_code1&&isCode.test(template_code)==true){
			_params["template_code"]=template_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/message/mobile/template/messageTemplateCodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#MOBAN_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#MOBAN_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    });
    //验证名称是否唯一
    $("#MOBAN_NAME").blur(function(){
    	var corp_code=$("#OWN_CORP").val();
    	var template_name=$("#MOBAN_NAME").val();
    	var template_name1=$("#MOBAN_NAME").attr("data-name");
    	var div=$(this).next('.hint').children();
    	if(template_name!==""&&template_name!==template_name1){
	    	var _params={};
	    	_params["template_name"]=template_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/message/mobile/template/messageTemplateNameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#MOBAN_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！")
	            	div.addClass("error_tips");
	            	$("#MOBAN_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    });
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/template.html");
	});
	$(".operedit_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/template.html");
	});
	$("#back_tem").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/template.html");
	});

});
function getcorplist(a,b){
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
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('#OWN_CORP').searchableSelect();
			var corp_code=$("#OWN_CORP").val();
			getTemplateGroup(corp_code,b);
			$('#corp_select .searchable-select-item').click(function(){
				var c=$(this).attr("data-value");
				getTemplateGroup(c,b);
				$("#MOBAN_ID").val("");
				$("#MOBAN_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#STORE_NAME").attr("data-mark","");

			});
			$('#corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						getTemplateGroup(c,b);
					}
				})
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
function getTemplateGroup(a,b){
//获取消息模板分组
	var corp_command="/smsTemplateType/getSmsTemplateTypeInfo";
	var param={};
	param["corp_code"]=a;
	oc.postRequire("post", corp_command,"", param, function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var index=0;
			var corp_html='';
			var c=null;
			if(msg.params.length=="0"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "该企业没有模板分组"
				});
			}
			$('#OWN_template').empty();
			$('#template_select .searchable-select').remove();
			for(index in msg.params){
				c=msg.params[index];
				corp_html+='<option value="'+c.template_type_code+'">'+c.template_type_name+'</option>';
			}
			$("#OWN_template").append(corp_html);
			if(b!==""){
				$("#OWN_template option[value='"+b+"']").attr("selected","true");
			}
			$('#OWN_template').searchableSelect();
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