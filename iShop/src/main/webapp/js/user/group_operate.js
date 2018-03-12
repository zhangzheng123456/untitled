var oc = new ObjectControl();
(function(root,factory){
	root.group = factory();
}(this,function(){
	var groupjs={};
	groupjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	groupjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	groupjs.checkCode=function(obj,hint){
		var isCode=/^[G]{1}[0-9]{4}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"群组编号为必填项，支持以大写G开头必须是4位数字的组合！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	}
	groupjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	groupjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	groupjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	groupjs.bindbutton=function(){
		$(".groupadd_oper_btn ul li:nth-of-type(1)").click(function(){
		    var codeMark=$("#GROUP_ID").attr("data-mark");//编号是唯一的标志
			var nameMark=$("#GROUP_NAME").attr("data-mark");//名称是否是否唯一的标志
			if(groupjs.firstStep()){
				if(codeMark=="N"){
					var div=$("#GROUP_ID").next('.hint').children();
					div.html("改编号已经存在！");
					div.addClass("error_tips");
					return;
				}
				if(nameMark=="N"){
					var div=$("#GROUP_NAME").next('.hint').children();
					div.html("改名称已经存在！");
					div.addClass("error_tips");
					return;
				}
				var GROUP_CODE=$("#GROUP_ID").val();
				var GROUP_NAME=$("#GROUP_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_ROLE=$("#OWN_ROLE").data("mygcode");
				var REMARK=$("#REMARK").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(OWN_ROLE==undefined){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"所属角色不可为空"
					});
					return;
				}
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/user/group/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"group_code":GROUP_CODE,"group_name":GROUP_NAME,"corp_code":OWN_CORP,"role_code":OWN_ROLE,"remark":REMARK,"isactive":ISACTIVE};
				groupjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".groupedit_oper_btn ul li:nth-of-type(1)").click(function(){
			var codeMark=$("#GROUP_ID").attr("data-mark");//编号是唯一的标志
			var nameMark=$("#GROUP_NAME").attr("data-mark");//名称是否是否唯一的标志
			if(groupjs.firstStep()){
				if(codeMark=="N"){
					var div=$("#GROUP_ID").next('.hint').children();
					div.html("改编号已经存在！");
					div.addClass("error_tips");
					return;
				};
				if(nameMark=="N"){
					var div=$("#GROUP_NAME").next('.hint').children();
					div.html("改名称已经存在！");
					div.addClass("error_tips");
					return;
				};
				var ID=sessionStorage.getItem("id");
				var GROUP_CODE=$("#GROUP_ID").val();
				var GROUP_NAME=$("#GROUP_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_ROLE=$("#OWN_ROLE").data("mygcode");
				var REMARK=$("#REMARK").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(OWN_ROLE==undefined){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"所属角色不可为空"
					});
					return;
				}
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/user/group/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"group_code":GROUP_CODE,"group_name":GROUP_NAME,"corp_code":OWN_CORP,"role_code":OWN_ROLE,"remark":REMARK,"isactive":ISACTIVE};
				groupjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	groupjs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				if(_command=="/user/group/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/user/group_edit.html");
                }
                if(_command=="/user/group/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/user/group.html");
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content:data.message
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
		if(groupjs['check' + command]){
			if(!groupjs['check' + command].apply(groupjs,[obj,hint])){
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
		groupjs.bindbutton();
	}
	var obj = {};
	obj.groupjs = groupjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.group.init();//初始化
	if($(".pre_title label").text()=="新增群组"){
		getcorplist();
	}else if($(".pre_title label").text()=="编辑群组"){
		$('#GROUP_USER').parent().parent().css("display","block");
		$('#GROUP_RIGHT').parent().parent().css("display","block");
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
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
		var _params={"id":id};
		var _command="/user/group/select";
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var mg=JSON.parse(msg.data);
				$("#GROUP_ID").val(mg.group_code);
				$("#GROUP_ID").attr("data-name",mg.group_code);
				$("#GROUP_NAME").val(mg.group_name);
				$("#GROUP_NAME").attr("data-name",mg.group_name);//给群组名称一个标志
				// $("#OWN_CORP option").val(mg.corp.corp_code);
				// $("#OWN_CORP option").text(mg.corp.corp_name);
				$("#OWN_ROLE").val(mg.role.role_name);
				$("#OWN_ROLE").attr("data-mygcode",mg.role.role_code);
				$("#REMARK").val(mg.remark);

				$("#created_time").val(mg.created_date);
				$("#creator").val(mg.creater);
				$("#modify_time").val(mg.modified_date);
				$("#modifier").val(mg.modifier);
				$("#name_num").val("共"+msg.user_count+"个名单");
				$("#power_num").val("共"+msg.privilege_count+"个权限");
				var input=$(".checkbox_isactive").find("input")[0];
				if(mg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
				getcorplist(mg.corp.corp_code);
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
	//获取角色列表
	$("#OWN_ROLE").click(function(){
		$("#grouprole_select").html('');
		 $(this).parent().children('ul').toggle();
		var role_param={"corp_code":$("#OWN_CORP").val()};
		var role_command="/user/group/role";
		oc.postRequire("post", role_command,"",role_param, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var index=0;
				var role_html='';
				var a=null;
				for(index in msg.role){
					a=msg.role[index];
					role_html+='<li data-grolecode="'+a.role_code+'">'+a.role_name+'</li>';
				}
				$("#grouprole_select").append(role_html);
				$("#grouprole_select li").click(function(){
		            var this_=this;
		            var txt = $(this_).text();
		            var g_code=$(this_).data("grolecode");
		            $(this_).parent().parent().children(".input_select").val(txt);
		            $(this_).parent().parent().children(".input_select").attr('data-mygcode',g_code);
		            $(this_).addClass('rel').siblings().removeClass('rel');
		            $(this_).parent().toggle();
		        });
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		});
	});
	$("#OWN_ROLE").blur(function(){
        setTimeout(function(){
        	$("#grouprole_select").hide();
        },200);  
    });     
	$("input[verify='Code']").blur(function(){//
    	var isCode=/^[G]{1}[0-9]{4}$/;
    	var _params={};
    	var group_code=$(this).val();//群组编号
    	var group_code1=$(this).attr("data-name");//标志
    	var corp_code=$("#OWN_CORP").val();//公司编号
		if(group_code!==""&&group_code!==group_code1&&isCode.test(group_code)==true){
			_params["group_code"]=group_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/user/group/code_exist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#GROUP_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#GROUP_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    });
    $("#GROUP_NAME").blur(function(){//群组名称失去焦点的的时候验证唯一性
    	var group_name=$(this).val();//群组名称
    	var group_name1=$(this).attr("data-name");//标志
    	var corp_code=$("#OWN_CORP").val();//公司编号
    	if(group_name!==""&&group_name!==group_name1){
    		var _params={};
    		_params["corp_code"]=corp_code;
    		_params["group_name"]=group_name;
    		var div=$(this).next('.hint').children();
    		oc.postRequire("post","/user/group/name_exist","", _params, function(data){
    			if(data.code=="0"){
    				div.html("");
    				$("#GROUP_NAME").attr("data-mark","Y");
    			}else if(data.code=="-1"){
    				$("#GROUP_NAME").attr("data-mark","N");
	               	div.addClass("error_tips");
					div.html("该名称已经存在！");	
    			}
    		})
    	}
    })

	$(".groupadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/group.html");
	});
	$(".groupedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/group.html");
	});
	$("#back_group").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/group.html");
	});

});
function getcorplist(a){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
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
					$("input[verify='Code']").val("");
					$("input[verify='Code']").attr("data-mark","");
				}
			})
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("input[verify='Code']").attr("data-mark","");
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
