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
	brandjs.checkCode=function(obj,hint){
		var isCode=/^[B]{1}[0-9]{4}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"品牌编号为必填项，支持以大写B开头必须是4位数字的组合！");
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
			var nameMark=$("#BRAND_NAME").attr("data-mark");//品牌编号是否唯一的标志
			var codeMark=$("#BRAND_ID").attr("data-mark");//品牌名称是否唯一的标志
			if(brandjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#BRAND_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#BRAND_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
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
		$("#edit_save").click(function(){
			var nameMark=$("#BRAND_NAME").attr("data-mark");//品牌编号是否唯一的标志
			var codeMark=$("#BRAND_ID").attr("data-mark");//品牌名称是否唯一的标志
			if(brandjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#BRAND_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#BRAND_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
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
		whir.loading.add("",0.5);//加载等待框
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
				}
			}
		});
		var _params={"id":id};
		var _command="/brand/select";
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#BRAND_ID").val(msg.brand_code);
				$("#BRAND_ID").attr("data-name",msg.brand_code);
				$("#BRAND_NAME").val(msg.brand_name);
				$("#BRAND_NAME").attr("data-name",msg.brand_name);
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
				getcorplist();
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
	}else{
		getcorplist();
	}
	$("input[verify='Code']").blur(function(){
    	var isCode=/^[B]{1}[0-9]{4}$/;
    	var _params={};
    	var brand_code=$(this).val();
    	var corp_code=$("#OWN_CORP").val();
    	var brand_code1=$(this).attr("data-name");
		if(brand_code!==""&&brand_code!==brand_code1&&isCode.test(brand_code)==true){
			_params["brand_code"]=brand_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/brand/Brand_codeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#BRAND_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#BRAND_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    })
    $("#BRAND_NAME").blur(function(){
    	var brand_name=$("#BRAND_NAME").val();
    	var brand_name1=$("#BRAND_NAME").attr("data-name");
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();
    	if(brand_name!==""&&brand_name!==brand_name1){
	    	var _params={};
	    	_params["brand_name"]=brand_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/brand/Brand_nameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#BRAND_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！")
	            	div.addClass("error_tips");
	            	$("#BRAND_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    })
	$(".brandadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
});
function getcorplist(){
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
			$('.corp_select .searchable-select-input').keydown(function(){
				var event=window.event||arguments[0];
				if(event.code==13){
					console.log(123123);
				}
			})
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#BRAND_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#BRAND_NAME").attr("data-mark","");
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