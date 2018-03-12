var oc = new ObjectControl();
(function(root,factory){
	root.area = factory();
}(this,function(){
	var areajs={};
	areajs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	areajs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	// areajs.checkCode=function(obj,hint){
	// 	var isCode=/^[A]{1}[0-9]{4}$/;
	// 	if(!this.isEmpty(obj)){
	// 		if(isCode.test(obj)){
	// 			this.hiddenHint(hint);
	// 			return true;
	// 		}else{
	// 			this.displayHint(hint,"区域编号为必填项，支持以大写A开头必须是4位数字的组合！");
	// 			return false;
	// 		}
	// 	}else{
	// 		this.displayHint(hint);
	// 		return false;
	// 	}
	// };
	areajs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	areajs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	areajs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	areajs.bindbutton=function(){
		$(".areaadd_oper_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#AREA_NAME").attr("data-mark");//区域编号是否唯一的标志
			var codeMark=$("#AREA_ID").attr("data-mark");//区域名称是否唯一的标志
			if(areajs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#AREA_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#AREA_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var AREA_ID=$("#AREA_ID").val();
				var AREA_NAME=$("#AREA_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/area/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_code":OWN_CORP,"area_code":AREA_ID,"area_name":AREA_NAME,"isactive":ISACTIVE};
				areajs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			var codeMark=$("#AREA_ID").attr("data-mark");//区域名称是否唯一的标志
			var nameMark=$("#AREA_NAME").attr("data-mark");//区域编号是否唯一的标志
			if(areajs.firstStep()){
				if(nameMark=="N"){
					var div=$("#AREA_NAME").next('.hint').children();
					div.html("该名称已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				if(codeMark=="N"){
					var div=$("#AREA_ID").next('.hint').children();
					div.html("该编号已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				var ID=sessionStorage.getItem("id");
				var AREA_ID=$("#AREA_ID").val();
				var AREA_NAME=$("#AREA_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/area/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"area_code":AREA_ID,"area_name":AREA_NAME,"isactive":ISACTIVE};
				areajs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	areajs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				if(_command=="/area/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/area/area_edit.html");
                }
                if(_command=="/area/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/area/area.html");
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
		if(areajs['check' + command]){
			if(!areajs['check' + command].apply(areajs,[obj,hint])){
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
		areajs.bindbutton();
	}
	var obj = {};
	obj.areajs = areajs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.area.init();//初始化
	if($(".pre_title label").text()=="编辑店铺群组"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		whir.loading.add("",0.5);
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length<=0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={};
		_params["id"]=id;
		var _command="/area/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#AREA_ID").val(msg.area_code);
				$("#AREA_ID").attr("data-name",msg.area_code);
				$("#AREA_NAME").val(msg.area_name);
				$("#AREA_NAME").attr("data-name",msg.area_name);
				// $("#OWN_CORP option").val(msg.corp.corp_code);
				// $("#OWN_CORP option").text(msg.corp.corp_name);
				$("#area_shop").val("共"+msg.store_count+"个店铺");
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
				getcorplist(msg.corp.corp_code);
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
	//验证编号是不是唯一
	$("#AREA_ID").blur(function(){
    	// var isCode=/^[A]{1}[0-9]{4}$/;
    	var _params={};
    	var area_code=$(this).val();
    	var area_code1=$(this).attr("data-name");
    	var corp_code=$("#OWN_CORP").val();
		if(area_code!==""&&area_code!==area_code1){
			_params["area_code"]=area_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/area/areaCodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#AREA_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#AREA_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");
	               }
		    })
		}
    });
    //验证名称是否唯一
    $("#AREA_NAME").blur(function(){
    	var corp_code=$("#OWN_CORP").val();
    	var area_name=$("#AREA_NAME").val();
    	var area_name1=$("#AREA_NAME").attr("data-name");
    	var div=$(this).next('.hint').children();
    	if(area_name!==""&&area_name!==area_name1){
	    	var _params={};
	    	_params["area_name"]=area_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/area/areaNameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#AREA_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！")
	            	div.addClass("error_tips");
	            	$("#AREA_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    });
	$(".areaadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/area/area.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/area/area.html");
	});
	$("#back_area").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/area/area.html");
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
			$('.corp_select select').searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					$("#AREA_ID").val("");
					$("#AREA_NAME").val("");
					$("#AREA_ID").attr("data-mark","");
					$("#AREA_NAME").attr("data-mark","");
				}
			})
			$('.searchable-select-item').click(function(){
				$("#AREA_ID").val("");
				$("#AREA_NAME").val("");
				$("#AREA_ID").attr("data-mark","");
				$("#AREA_NAME").attr("data-mark","");
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
