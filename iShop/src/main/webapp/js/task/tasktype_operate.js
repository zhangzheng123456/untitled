var oc = new ObjectControl();
(function(root,factory){
	root.tasktype = factory();
}(this,function(){
	var tasktypejs={};
	tasktypejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	tasktypejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	tasktypejs.checkCode=function(obj,hint){
		var isCode=/^[T]{1}[0-9]{4}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"任务编号为必填项，支持以大写T开头必须是4位数字的组合！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	}
	tasktypejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	tasktypejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	tasktypejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	tasktypejs.bindbutton=function(){
		$(".areaadd_oper_btn ul li:nth-of-type(1)").click(function(){
			// var nameMark=$("#AREA_NAME").attr("data-mark");//区域编号是否唯一的标志
			// var codeMark=$("#AREA_ID").attr("data-mark");//区域名称是否唯一的标志
			if(tasktypejs.firstStep()){
				// if(nameMark=="N"||codeMark=="N"){
				// 	if(nameMark=="N"){
				// 		var div=$("#AREA_NAME").next('.hint').children();
				// 		div.html("该名称已经存在！");
		  //           	div.addClass("error_tips");
				// 	}
				// 	if(codeMark=="N"){
				// 		var div=$("#AREA_ID").next('.hint').children();
				// 		div.html("该编号已经存在！");
		  //           	div.addClass("error_tips");
				// 	}
	   //          	return;
				// }
				var task_type_code=$("#task_type_code").val();
				var task_type=$("#task_type").val();
				var corp_code=$("#OWN_CORP").val();
				var isactive="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					isactive="Y";
				}else if(input.checked==false){
					isactive="N";
				}
				var _command="/task_type/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_code":corp_code,"task_type_code":task_type_code,"task_type_name":task_type,"isactive":isactive};
				tasktypejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			// var codeMark=$("#AREA_ID").attr("data-mark");//区域名称是否唯一的标志
			// var nameMark=$("#AREA_NAME").attr("data-mark");//区域编号是否唯一的标志
			if(tasktypejs.firstStep()){
				// if(nameMark=="N"){
				// 	var div=$("#AREA_NAME").next('.hint').children();
				// 	div.html("该名称已经存在！");
		  //           div.addClass("error_tips");
		  //           return;
				// }
				// if(codeMark=="N"){
				// 	var div=$("#AREA_ID").next('.hint').children();
				// 	div.html("该编号已经存在！");
		  //           div.addClass("error_tips");
		  //           return;
				// }
				var id=sessionStorage.getItem("id");
				var task_type_code=$("#task_type_code").val();
				var task_type=$("#task_type").val();
				var corp_code=$("#OWN_CORP").val();
				var isactive="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					isactive="Y";
				}else if(input.checked==false){
					isactive="N";
				}
				var _params={"id":id,"corp_code":corp_code,"task_type_code":task_type_code,"task_type_name":task_type,"isactive":isactive};
				tasktypejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	tasktypejs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/task/tasktype.html");
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
		if(tasktypejs['check' + command]){
			if(!tasktypejs['check' + command].apply(tasktypejs,[obj,hint])){
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
		tasktypejs.bindbutton();
	}
	var obj = {};
	obj.tasktypejs = tasktypejs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.tasktype.init();//初始化
	if($(".pre_title label").text()=="编辑任务类型"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		whir.loading.add("",0.5);
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
		var _params={};
		_params["id"]=id;
		var _command="/task_type/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				$("#task_type_code").val(msg.task_type_code);
				$("#task_type_code").attr("data-name",msg.task_type_code);
				$("#task_type").val(msg.task_type_name);
				$("#task_type").attr("data-name",msg.task_type_name);
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
	//验证编号是不是唯一
	$("input[verify='Code']").blur(function(){
    	var isCode=/^[A]{1}[0-9]{4}$/;
    	var _params={};
    	var area_code=$(this).val();
    	var area_code1=$(this).attr("data-name");
    	var corp_code=$("#OWN_CORP").val();
		if(area_code!==""&&area_code!==area_code1&&isCode.test(area_code)==true){
			_params["area_code"]=area_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/area/Area_codeExist","", _params, function(data){
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
	    	oc.postRequire("post","/area/Area_nameExist","", _params, function(data){
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
		$(window.parent.document).find('#iframepage').attr("src","/task/tasktype.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/task/tasktype.html");
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
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#AREA_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
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