var oc = new ObjectControl();
(function(root,factory){
	root.user = factory();
}(this,function(){
	var useroperatejs={};
	useroperatejs.param={
		userback:[],
		username:[]
	};
	useroperatejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	useroperatejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	useroperatejs.checkPhone = function(obj,hint){
		var isPhone=/^([0-9]{3,4}-)?[0-9]{7,8}$/;
		var isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8,9]{1}\d{9}$/;//验证手机号码格式正不正确
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
			return true;
		}
	};
	useroperatejs.checkMail = function(obj,hint){
		var reg=/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/;
		if(!this.isEmpty(obj)){
			if(reg.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"邮箱格式不正确！")
				return false;
			}
		}else{
			this.displayHint(hint);
			return true;
		}
	};
	useroperatejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	useroperatejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	useroperatejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	useroperatejs.bindbutton=function(){
		var self=this;
		Array.prototype.remove = function(val) {
			var index = this.indexOf(val);
			if (index > -1) {
				this.splice(index, 1);
			}
		};
		$(".useradd_oper_btn ul li:nth-of-type(1)").click(function(){
			var codeMark=$("#USERID").attr("data-mark");//编号是唯一的标志
			// var idMark=$("#user_id").attr("data-mark");//ID是否唯一的标志
			var phoneMark=$("#USER_PHONE").attr("data-mark");//手机号码是否唯一的标志
			var emailMark=$("#USER_EMAIL").attr("data-mark");//邮箱是否唯一的标志
			if(useroperatejs.firstStep()){
				// if(idMark=="N"){
				// 	var div=$("#user_id").next('.hint').children();
				// 	div.html("员工ID已经存在！");
				// 	div.addClass("error_tips");
				// 	return;
				// }
				if(phoneMark=="N"){
					var div=$("#USER_PHONE").next('.hint').children();
					div.html("该手机号码已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				if(emailMark=="N"){
					var div=$("#USER_EMAIL").next('.hint').children();
					div.html("该邮箱已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				if(codeMark=="N"){
					var div=$("#USERID").next('.hint').children();
					div.html("该编号已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				var USERID=$("#USERID").val();//员工编号
				// var user_id=$("#user_id").val();
				var USER_NAME=$("#USER_NAME").val();//员工名称
				var USER_PHONE=$("#USER_PHONE").val();//手机
				var USER_EMAIL=$("#USER_EMAIL").val();//邮箱
				var USER_SEX=$("#USER_SEX").val();//性别
				var	position=$("#position").val();//职务
				var SEX="";//性别
				if(USER_SEX=="男"){
					SEX="M";
				}else if(USER_SEX=="女"){
					SEX="F";
				}
				var OWN_CORP=$("#OWN_CORP").val();//公司code
				var OWN_RIGHT=$("#OWN_RIGHT").attr("data-myrcode");//群组编号
				var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
				var ISACTIVE="";//是否可用字段
				var input=$("#is_active")[0];//是否可用
				if(OWN_RIGHT==""){//群组
					art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content:"所属群组不能为空"
						});
					return;
				}
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var can_login="";//可登录状态
                var input1=$("#invisible")[0];
                if(input1.checked==true){
                	can_login="Y";
                }else if(input.checked==false){
                	can_login="N";
                }
                // var isonline="";//签到状态
                // var input2=$("#isonline")[0];
                // if(input2.checked==true){
                // 	isonline="Y";
                // }else if(input2.checked==false){
                // 	isonline="N";
                // }
                var a=$('#all_type .xingming input');
				var STORE_CODE="";
				for(var i=0;i<a.length;i++){
			        var u=$(a[i]).attr("data-code");
			        if(i<a.length-1){
			            STORE_CODE+=u+",";
			        }else{
			             STORE_CODE+=u;
			        }     
			    }
			    var area_store_code="";
			    var b=$('#shop .xingming input');
			    for(var i=0;i<b.length;i++){
			    	var d=$(b[i]).attr("data-code");
			        if(i<b.length-1){
			             area_store_code+=d+",";
			        }else{
			             area_store_code+=d;
			        }     
			    }
				//如果角色是导购，店长，区经的时候
				if(r_code=="R2000"||r_code=="R3000"||r_code=="R4000"||r_code=="R4800"||r_code=="R5500"){
					if(STORE_CODE==""){
						if(r_code=="R2000"||r_code=="R3000"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属店铺不能为空"
							});
						return;
						}
						if(r_code=="R4000"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"店铺群组不能为空"
							});
							return;
						}
						if(r_code=="R4800"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属品牌不能为空"
							});
							return;
						}
						if(r_code=="R5500"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"企业不能为空"
							});
							return;
						}
						if(r_code=="R3500"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属城市不能为空"
							});
							return;
						}
					}
				}
				var _command="/user/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["user_code"]=USERID;//员工编号
				// _params["user_id"]=user_id;//员工ID
				_params["username"]=USER_NAME;//员工名称
				_params["avatar"]="";//新增的时候头像字段先设置为空
				_params["phone"]=USER_PHONE;//手机
				_params["position"]=position;//职务
				_params["email"]=USER_EMAIL//邮箱
				_params["sex"]=SEX//性别
				_params["group_code"]=OWN_RIGHT;//群组编号
				_params["role_code"]=r_code;//角色编号
				_params["isactive"]=ISACTIVE;//是否可用
				_params["corp_code"]=OWN_CORP;//公司编号
				_params["can_login"]=can_login;//是否登录
				// _params["isonline"]=isonline;//签到状态
				if(r_code=="R2000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R3000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R4000"){
	            	_params["store_code"]=area_store_code//店铺编号
	            	_params["area_code"]=STORE_CODE;//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }
	            else if(r_code=="R5000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R6000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R4800"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=area_store_code//区域编号
	            	_params["brand_code"]=STORE_CODE;//品牌编号
	            }else if(r_code=="R5500"){
					_params["store_code"]=""//店铺编号
					_params["area_code"]=""//区域编号
					_params["brand_code"]="";//品牌编号
					_params["manager_corp"]=STORE_CODE;//企业编号
				}else if(r_code=="R3500"){
					_params["store_code"]=""//店铺编号
					_params["area_code"]=""//区域编号
					_params["brand_code"]="";//品牌编号
					_params["manager_corp"]="";//企业编号
					_params["city"]=STORE_CODE;//企业编号
				}
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".useredit_oper_btn ul li:nth-of-type(1)").click(function(){
			var codeMark=$("#USERID").attr("data-mark");//编号是唯一的标志
			// var idMark=$("#user_id").attr("data-mark");//ID是否唯一的标志
			var phoneMark=$("#USER_PHONE").attr("data-mark");//手机号码是否唯一的标志
			var emailMark=$("#USER_EMAIL").attr("data-mark");//邮箱是否唯一的标志
			if(useroperatejs.firstStep()){
				// if(idMark=="N"){
				// 	var div=$("#user_id").next('.hint').children();
				// 	div.html("员工ID已经存在！");
				// 	div.addClass("error_tips");
				// 	return;
				// }
				if(phoneMark=="N"){
					var div=$("#USER_PHONE").next('.hint').children();
					div.html("该手机号码已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				if(emailMark=="N"){
					var div=$("#USER_EMAIL").next('.hint').children();
					div.html("该邮箱已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				if(codeMark=="N"){
					var div=$("#USERID").next('.hint').children();
					div.html("该编号已经存在！");
		            div.addClass("error_tips");
		            return;
				}
				var ID=sessionStorage.getItem("id");
				var USERID=$("#USERID").val();
				// var user_id=$("#user_id").val();
				var USER_NAME=$("#USER_NAME").val();
				var USER_PHONE=$("#USER_PHONE").val();
				var USER_EMAIL=$("#USER_EMAIL").val();
				var USER_SEX=$("#USER_SEX").val();
				var	position=$("#position").val();//职务
				var SEX="";
				if(USER_SEX=="男"){
					SEX="M";
				}else if(USER_SEX=="女"){
					SEX="F";
				}
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var OWN_RIGHT=$("#OWN_RIGHT").attr("data-myrcode");//群组编号
				var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
				var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				if(OWN_RIGHT==""){//群组
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"所属群组不能为空"
					});
					return;
				}
				var avatar="";//头像
                if($("#preview img").attr("data-src").indexOf("http")!==-1){
                    avatar=$("#preview img").attr("data-src");
                }
                if($("#preview img").attr("data-src").indexOf("http")==-1){
                    avatar="";
                }
				var wx="";
				if($("#wx").find("input")[0].checked==true){
					wx="Y";
				}else if($("#wx").find("input")[0].checked==false ){
					wx="N";
				}
				var sms="";
				if($("#sms").find("input")[0].checked==true){
					sms="Y";
				}else if($("#sms").find("input")[0].checked==false ){
					sms="N";
				}
				var call="";
				if($("#call").find("input")[0].checked==true){
					call="Y";
				}else if($("#call").find("input")[0].checked==false ){
					call="N";
				}
				var can_login="";//可登录状态
                var input1=$("#invisible")[0];
                if(input1.checked==true){
                	can_login="Y";
                }else if(input1.checked==false){
                	can_login="N";
                }
				var a=$('#all_type .xingming input');
				var STORE_CODE="";
				for(var i=0;i<a.length;i++){
			        var u=$(a[i]).attr("data-code");
			        if(i<a.length-1){
			            STORE_CODE+=u+",";
			        }else{
			             STORE_CODE+=u;
			        }     
			    }
			    var area_store_code="";
			    var b=$('#shop .xingming input');
			    for(var i=0;i<b.length;i++){
			    	var d=$(b[i]).attr("data-code");
			        if(i<b.length-1){
			             area_store_code+=d+",";
			        }else{
			             area_store_code+=d;
			        }     
			    }
				//如果角色是导购，店长，区经的时候
				if(r_code=="R2000"||r_code=="R3000"||r_code=="R4000"||r_code=="R4800"||r_code=="R5500"){
					if(STORE_CODE==""){
						if(r_code=="R2000"||r_code=="R3000"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属店铺不能为空"
							});
						return;
						}
						if(r_code=="R4000"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"店铺群组不能为空"
							});
							return;
						}
						if(r_code=="R4800"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属品牌不能为空"
							});
							return;
						}
						if(r_code=="R5500"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"企业不能为空"
							});
							return;
						}
						if(r_code=="R3500"){
							art.dialog({
								time: 1,
								lock:true,
								cancel: false,
								content:"所属城市不能为空"
							});
							return;
						}
					}
				}
				var _command="/user/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["user_code"]=USERID;//员工编号
				// _params["user_id"]=user_id;//员工ID
				_params["username"]=USER_NAME;//员工名称
				_params["avatar"]=avatar;//头像
				_params["position"]=position;//职务
				_params["phone"]=USER_PHONE;//手机
				_params["email"]=USER_EMAIL//邮箱
				_params["sex"]=SEX//性别
				_params["group_code"]=OWN_RIGHT;//群组编号
				_params["role_code"]=r_code;//角色编号
				_params["isactive"]=ISACTIVE;//是否可用
				_params["corp_code"]=OWN_CORP;//公司编号
				_params["can_login"]=can_login;//是否登录
				_params["user_back"]={"wx":wx,"sms":sms,"call":call};//回访功能
				// _params["isonline"]=isonline;//签到状态
				// _params["password"]=PSW;//密码
				_params["id"]=ID;//ID
				if(r_code=="R2000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R3000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R4000"){
	            	_params["store_code"]=area_store_code//店铺编号
	            	_params["area_code"]=STORE_CODE;//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }
	            else if(r_code=="R5000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R6000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            	_params["brand_code"]="";//品牌编号
	            }else if(r_code=="R4800"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=area_store_code;//区域编号
	            	_params["brand_code"]=STORE_CODE;//品牌编号
	            }else if(r_code=="R5500"){
					_params["store_code"]=""//店铺编号
					_params["area_code"]=""//区域编号
					_params["brand_code"]="";//品牌编号
					_params["manager_corp"]=STORE_CODE;//企业编号
				}else if(r_code=="R3500"){
				_params["store_code"]=""//店铺编号
				_params["area_code"]=""//区域编号
				_params["brand_code"]="";//品牌编号
				_params["manager_corp"]="";//企业编号
				_params["city"]=STORE_CODE;//企业编号
			}
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#Acc_dropdown").on("click","li",function(e){
			var input=$(this).find("input")[0];
			if(input.type=="checkbox"&&input.checked==false){
				input.checked = true;
				self.param.userback.push($(this).find("input").val());
				self.param.username.push($(this).find("input").attr("data-name"));
				console.log(self.param.userback);
				console.log(self.param.username);
				$('#Accounts').val(self.param.username.toString());
				$('#Accounts').attr('data-appid',self.param.userback.toString());
			}else if(input.type=="checkbox"&&input.checked==true){
				input.checked = false;
				self.param.username.remove($(this).find("input").attr("data-name"));
				self.param.userback.remove($(this).find("input").val());
				$('#Accounts').val(self.param.username.toString());
				$('#Accounts').attr('data-appid', self.param.userback.toString());
			}
		});
		$(document).click(function(e){
			if($(e.target).is('.Acc_dropdown')||$(e.target).is('.Acc_dropdown li')||$(e.target).is('.checkbox_isactive')||$(e.target).is('.Acc_dropdown li span')||$(e.target).is('#Acc_dropdown')||$(e.target).is('.drop-down .search i')||$(e.target).is('.drop-down h5')||$(e.target).is('.drop-down .checkbox_isactive')||$(e.target).is('.checkbox_isactive label')||$(e.target).is('.drop-down ul li')||$(e.target).is('.drop-down ul')){
				return;
			}else{
				$("#distribution_frame").hide();
				$('.Acc_dropdown').hide();
				flase=0;
			}
		});
		$("#Accounts").click(function(e){
			console.log(12313);
			e.stopPropagation();
			$('.Acc_dropdown').toggle();
		});
	};
	useroperatejs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/user/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/user/user_edit.html");
                }
                if(_command=="/user/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
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
		if(useroperatejs['check' + command]){
			if(!useroperatejs['check' + command].apply(useroperatejs,[obj,hint])){
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
		useroperatejs.bindbutton();
	}
	var obj = {};
	obj.useroperatejs = useroperatejs;
	obj.init = init;
	return obj;
}));
function selectownrole(obj){//加载群组列表的时候
	$("#role_list").html('');
	role_li_list();
	var input=$(obj).find("input");
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
    }else{
        ul.hide();
    }
    $(input).blur(function(){  
        setTimeout(function(){
        	ul.hide();
        },200);  
    });   
}
function role_li_list(){
	var c_code=$('#OWN_CORP').val();
	role_data(c_code);
}
function role_data(c){//
	// var _params={"group_code":r,"corp_code":c};
	var _params={"corp_code":c};
	var _command="/user/role";
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", _command,"", _params, function(data){
		var msg=JSON.parse(data.message);
		var msg_roles=JSON.parse(msg.group);
		var html="";
		if(msg_roles.length!==0){
			for(var i=0;i<msg_roles.length;i++){
				html +='<li data-jucode="'+msg_roles[i].role_code+'" data-rolecode="'+msg_roles[i].group_code+'">'+msg_roles[i].group_name+'</li>';
			}
		}else{
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"该企业目前没有群组，请先定义群组"
			});
		}
		whir.loading.remove();//移除加载框
		$("#role_list").append(html);
		$("#role_list li").click(function(){
            var this_=this;
            var txt = $(this_).text();
            var r_code=$(this_).data("rolecode");
            var j_code=$(this_).data("jucode");
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().children(".input_select").attr('data-myrcode',r_code);
            $(this_).parent().parent().children(".input_select").attr('data-myjcode',j_code);//角色编号
            $(this_).addClass('rel').siblings().removeClass('rel');
            if(j_code=="R2000"||j_code=="R3000"){
            	$('#all_type .task_allot').html("所属店铺*");
            	$('#all_type .task_allot').parent().show();
            	$('.xingming').empty();
            	$("#shop").hide();
            }else if(j_code=="R4000"){
            	$('#all_type .task_allot').html("店铺群组*");
            	$('#all_type .task_allot').parent().show();
            	$('.xingming').empty();
            	$("#shop").show();
            	$("#shop .task_allot").html("所属店铺")
            }
            else if(j_code=="R5000"){
            	$('#all_type .task_allot').parent().hide();
            	$("#shop").hide();
            }else if(j_code=="R6000"){
            	$('#all_type .task_allot').parent().hide();
            	$("#shop").hide();
            }else if(j_code=="R4800"){
            	$("#shop").show();
            	$('#all_type .task_allot').html("所属品牌*");
            	$('#all_type .task_allot').parent().show();
            	$('.xingming').empty();
            	$("#shop").show();
            	$("#shop .task_allot").html("店铺群组");
            }else if(j_code=="R5500"){
				$('#all_type .task_allot').html("管理企业*");
				$('#all_type .task_allot').parent().show();
				$('.xingming').empty();
				$("#shop").hide();
			}else if(j_code=="R3500"){
				$('#all_type .task_allot').html("所属城市*");
				$('#all_type .task_allot').parent().show();
				$('.xingming').empty();
				$("#shop").hide();
			}
        });
	});
}
//ready事件
jQuery(document).ready(function(){
	window.user.init();//初始化
	$(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    if($(".pre_title label").text()=="编辑员工信息"){
    	var id=sessionStorage.getItem("id");
		var key_val = sessionStorage.getItem("key_val");//取页面的function_code
		key_val = JSON.parse(key_val);
		var funcCode = key_val.func_code;
		var _params={"id":id};
		var _command="/user/select";
		whir.loading.add("",0.5);//加载等待框
		$.get("/detail?funcCode=" + funcCode + "", function (data) {
			if (data.code == "0") {
				var message = JSON.parse(data.message);
				var action = message.actions;
				if (action.length <= 0) {
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var j_code=msg.group.role_code;//角色编号
				$("#USERID").val(msg.user_code);
				$("#USERID").attr("data-name",msg.user_code);
				$("#user_id").val(msg.user_id);
				$("#user_id").attr("data-name",msg.user_id);
				$("#USER_NAME").val(msg.user_name);
				if(msg.avatar!==undefined){
                    if(msg.avatar.indexOf("http")==-1){
                        $("#preview img").attr("src","../img/head.png");
                        $("#preview img").attr("data-src","../img/head.png");
                    }
                    if(msg.avatar.indexOf("http")!==-1){
                        $("#preview img").attr("src",msg.avatar);
                        $("#preview img").attr("data-src",msg.avatar);
                    }
                }
                if(msg.avatar==undefined){
                    $("#preview img").attr("src","../img/head.png");
                    $("#preview img").attr("data-src","../img/head.png");
                }
				$("#USER_PHONE").val(msg.phone);
				$("#USER_PHONE").attr("data-name",msg.phone);
				$("#USER_EMAIL").val(msg.email);
				$("#USER_EMAIL").attr("data-name",msg.email);
				$("#position").val(msg.position);
				if(msg.sex=="F"){
					$("#USER_SEX").val("女");
				}else if(msg.sex=="M"){
					$("#USER_SEX").val("男");
				}
				if(msg.qrcode==""){
					$("#kuang").hide();
				}else if(msg.qrcode!==""&&msg.qrcode!==undefined){
					$("#kuang").show();
    				$('#kuang img').attr("src",msg.qrcode);
				}
				$("#OWN_RIGHT").val(msg.group.group_name);
				$("#OWN_RIGHT").attr("data-myrcode",msg.group.group_code);//编辑的时候赋值给群组编号
				$("#OWN_RIGHT").attr("data-myjcode",j_code);//编辑的时候赋值给角色编号
				if(j_code=="R2000"||j_code=="R3000"){
					$('#all_type .task_allot').html("所属店铺*");
					if(msg.store_name!==""){
			            var store_lists=msg.store_name.split(",");
						var storecode_list=msg.store_code.split(",");
						for(var i=0;i<store_lists.length;i++){
							$('.xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+storecode_list[i]+"'  value='"+store_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
            	}else if(j_code=="R4000"){
	            	$('#all_type .task_allot').html("店铺群组*");
	            	$('#shop').show();
	            	if(msg.area_name!==""){
		            	var area_lists=msg.area_name.split(",");
						var areacode_list=msg.area_code.split(",");
						for(var i=0;i<area_lists.length;i++){
							$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+areacode_list[i]+"'  value='"+area_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
					if(msg.store_name!==""){
						var store_lists=msg.store_name.split(",");
						var storecode_list=msg.store_code.split(",");
						for(var i=0;i<store_lists.length;i++){
							$('#shop .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+storecode_list[i]+"'  value='"+store_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
            	}else if(j_code=="R5000"||j_code=="R6000"){
            		$('#all_type .task_allot').parent().hide();
            	}else if(j_code=="R4800"){
            		$('#all_type .task_allot').html("所属品牌*");
            		$("#shop").show();
            		$("#shop .task_allot").html("店铺群组");
	            	if(msg.brand_name!==""){
		            	var brand_lists=msg.brand_name.split(",");
						var brandcode_list=msg.brand_code.split(",");
						for(var i=0;i<brand_lists.length;i++){
							$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+brandcode_list[i]+"'  value='"+brand_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
					if(msg.area_name!==""){
						var area_lists=msg.area_name.split(",");
						var areacode_list=msg.area_code.split(",");
						for(var i=0;i<area_lists.length;i++){
							$('#shop .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+areacode_list[i]+"'  value='"+area_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
            	}else if(j_code=="R5500"){
					$('#all_type .task_allot').html("管理企业*");
					$("#shop").hide();
					if(msg.manager_corp_name!==""&&msg.manager_corp_name!==undefined){
						var brand_lists=msg.manager_corp_name.split(",");
						var brandcode_list=msg.manager_corp.split(",");
						for(var i=0;i<brand_lists.length;i++){
							$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+brandcode_list[i]+"'  value='"+brand_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
				}else if(j_code=="R3500"){
					$('#all_type .task_allot').html("所属城市*");
					$("#shop").hide();
					if(msg.city!==""&&msg.city!==undefined){
						var brand_lists=msg.city.split(",");
						for(var i=0;i<brand_lists.length;i++){
							$('#all_type .xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+brand_lists[i]+"'  value='"+brand_lists[i]+"'><span class='power remove_app_id'>删除</span></p>");
						}
					}
				}
				$("#register_time").val(msg.created_date);//创建时间
				$("#recently_login").val(msg.login_time_recently);//是否登录
				$("#created_time").val(msg.created_date);//
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				$("#init_password").val(msg.password);
				var input=$("#is_active")[0];//是否可用
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
				var input1=$("#invisible")[0];
				if(msg.can_login=="Y"){
					input1.checked=true;
				}else if(msg.can_login=="N"){
					input1.checked=false;
				}
				// if(msg.isonline=="Y"){
				// 	$("#isonline")[0].checked=true;
				// }else if(msg.isonline=="N"||msg.isonline==""){
				// 	$("#isonline")[0].checked=false;
				// }
				var qrcodeList=msg.qrcodeList;
				if(qrcodeList.length>0) {
					for (var i = 0; i < qrcodeList.length; i++) {
						$("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" id="'+qrcodeList[i].app_id+'" value="' + qrcodeList[i].app_name + '" readonly="readonly"><ul></ul>'
							+ '<span class="power create" onclick="getTwoCode(this)">生成</span>'
							+ '<span class="power k_close" style="display: none;">关闭</span>'
							+ '<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
							+ '<div class="kuang"><img src="' + qrcodeList[i].qrcode + '" alt="">'
							+'</div></li>')
						$(".create").hide();
						$(".k_close").show();
					}
					$(".kuang").show();
				}
				//回访记录
				var user_back=JSON.parse(msg.user_back);
				for(var key in user_back){
					if(user_back[key]=="Y"){
						console.log(key);
						$("#"+key).click();
					}
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
	$(".useradd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	$("#back_user").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	//验证编号是否唯一的方法
	$("#USERID").blur(function(){
    	var _params={};
    	var user_code=$(this).val();//员工编号
    	var corp_code=$("#OWN_CORP").val();//公司编号
    	var user_code1=$(this).attr("data-name");
		if(user_code!==""&&user_code!==user_code1){
			_params["user_code"]=user_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/user/userCodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#USERID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#USERID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    })
	//验证员工ID唯一性
	$("#user_id").blur(function(){
		var _params={};
		var user_id=$(this).val();//员工编号
		var corp_code=$("#OWN_CORP").val();//公司编号
		var user_id1=$(this).attr("data-name");
		if(user_id!==""&&user_id!==user_id1){
			_params["user_id"]=user_id;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/user/userCodeExist","", _params, function(data){
				if(data.code=="0"){
					div.html("");
					$("#user_id").attr("data-mark","Y");
				}else if(data.code=="-1"){
					$("#user_id").attr("data-mark","N");
					div.addClass("error_tips");
					div.html("员工ID已经存在！");
				}
			})
		}
	})
    //验证邮箱是否唯一的方法
    $("#USER_EMAIL").blur(function(){
    	var email=$("#USER_EMAIL").val();//邮箱名称
    	var email1=$("#USER_EMAIL").attr("data-name");//编辑的标志
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();//企业编号
    	if(email==""){
    		div.html("");
	        $("#USER_EMAIL").attr("data-mark","Y");
    	}
    	if(email!==""&&email!==email1){
	    	var _params={};
	    	_params["email"]=email;//邮箱
	    	_params["corp_code"]=corp_code;//企业编号
	    	oc.postRequire("post","/user/EamilExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#USER_EMAIL").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该邮箱已经存在！");
	            	div.addClass("error_tips");
	            	$("#USER_EMAIL").attr("data-mark","N");
	            }
	    	})
	    }
    })
    //验证手机是否唯一的方法
    $("#USER_PHONE").blur(function(){
    	var phone=$("#USER_PHONE").val();//手机号码
    	var phone1=$("#USER_PHONE").attr("data-name");//取手机号的一个标志
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();
    	if(phone==""){
    		div.html("");
	        $("#USER_PHONE").attr("data-mark","Y");
    	}
    	if(phone!==""&&phone!==phone1){
	    	var _params={};
	    	_params["phone"]=phone;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/user/PhoneExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#USER_PHONE").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("手机号码已经存在！");
	            	div.addClass("error_tips");
	            	$("#USER_PHONE").attr("data-mark","N");
	            }
	    	})
	    }
    })
	//重置密码时提示消失
	$("#first_pwd").focus(function () {
		$(".em_1").css("display","none")
	})
	$("#second_pwd").focus(function () {
		$(".em_2").css("display","none")
	})
	//重置密码
	$("#baocun").click(function(){
		if($("#first_pwd").val()==""||$("#second_pwd").val()==""||$("#first_pwd").val()!=$("#second_pwd").val()){
             if($("#first_pwd").val()==""){
				 $(".em_1").css("display","block")
			 }else if($("#second_pwd").val()!=$("#first_pwd").val()){
				 $(".em_2").css("display","block")
			 }
			return;
		}
		var pwd_creat="/user/change_passwd";
		var user_id=sessionStorage.getItem("id");
		var password=$('#first_pwd').val();
		var _params={};
		_params["password"]=md5(password);
		_params["user_id"]=user_id;
		oc.postRequire("post",pwd_creat,"",_params,function (data) {
			if(data.code=="0"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
				$("#chongzhi_pwd").css('display','none');
				$("#chongzhi_box").css('display','none');
			}else if(data=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "失败"
				});
			}

		})
	})
});
function getcorplist(a){
	//获取企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			var corp_html='';
			for(var i=0;i<msg.corps.length;i++){
				corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('.corp_select select').searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					$("#USER_PHONE").val("");
					$("#USER_EMAIL").val("");
					$("#USERID").val("");
					$("#user_id").val("");
					$("#user_id").attr("data-mark","");
					$("#USERID").attr("data-mark","");
					$("#USER_PHONE").attr("data-mark","");
					$("#USER_EMAIL").attr("data-mark","");
					$("#OWN_RIGHT").val('');
					$("#OWN_RIGHT").attr("data-myrcode","");
					$("#OWN_RIGHT").attr("data-myjcode","");
					$("#OWN_STORE").val('');
					$("#OWN_STORE").attr("data-myscode","");
					$('.xingming').empty();
					$('#all_type .task_allot').html("所属店铺");
					$("#shop").hide();
				}
			})
			$('.searchable-select-item').click(function(){
					$("#USER_PHONE").val("");
					$("#USER_EMAIL").val("");
					$("#USERID").val("");
					$("#user_id").val("");
					$("#user_id").attr("data-mark","");
					$("#USERID").attr("data-mark","");
					$("#USER_PHONE").attr("data-mark","");
					$("#USER_EMAIL").attr("data-mark","");
					$("#OWN_RIGHT").val('');
					$("#OWN_RIGHT").attr("data-myrcode","");
					$("#OWN_RIGHT").attr("data-myjcode","");
					$("#OWN_STORE").val('');
					$("#OWN_STORE").attr("data-myscode","");
					$('.xingming').empty();
					$('#all_type .task_allot').html("所属店铺");
					$("#shop").hide();
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
function getAppName(a){
	var corp_code=$("#OWN_CORP").val();
	var param={};
	    param["corp_code"]=corp_code;
	var _command="/corp/selectWx";
	oc.postRequire("post", _command,"", param, function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			var list=msg.list;
			$(a).next("ul").empty();
			if(list.length<=0){
				alert("请先授权公众号!");
				return;
			}
			if(list.length>0){
				for(var i=0;i<list.length;i++){
					$(a).next("ul").append('<li data-id="'+list[i].app_id+'">'+list[i].app_name+'</li>')
				}
			}
			$(a).next("ul").find("li").click(function () {
				var value = $(this).html();
				$(a).val(value);
				$(a).attr("id",$(this).attr("data-id"));
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
//点击生成二维码
function getTwoCode(b){
	// $("#tk").show();
	
	var input=$(b).prevAll("input").val();
	if(input!==""){
		$(b).hide();
		$(b).nextAll(".k_close").show();
	}
	var user_creat="/user/creatQrcode";
	var user_code=$('#USERID').val();
	var corp_code=$('#OWN_CORP').val();
	var app_id=$(b).prevAll("input").attr("id");
	var _params={};
	_params["user_code"]=user_code;
	_params["corp_code"]=corp_code;
	_params["app_id"]=app_id;
	if(app_id==""||app_id==undefined){
		alert("请选择公众号");
		return;
	}
	oc.postRequire("post",user_creat,"", _params, function(data){
		var message=data.message;
		if(data.code=="0"){
			$(b).nextAll(".kuang").show();
			$(b).nextAll(".kuang").find("img").attr("src",message);
		}else if(data.code=="-1"){
			alert(data.message);
		}
	})
}
//生成二维码下拉框
function select_down(a){
	if($(a).next().css("display")=="none"){
		$(a).next().show();
		$(a).find("ul").empty();
		getAppName(a);
	}else {
		$(a).next().hide();
	}
	$(a).blur(function(){
		var ul=$(this).next();
		setTimeout(function(){
			ul.hide();
		},200);
	})
}
$(".er_code").on("click",".k_close",function(){
	$(this).nextAll(".kuang").hide();
	$(this).hide();
	$(this).prev(".create").show();
})
$("#add_app_id").click(function(){
	$("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" readonly="readonly"><ul></ul>'
		+'<span class="power create" onclick="getTwoCode(this)">生成</span>'
		+'<span class="power k_close" style="display: none;">关闭</span>'
		+'<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
		+'<div class="kuang"><img src="" alt="">'
		+'</div></li>')

});
$("#X").click(function(){
	$(this).parent(".tk").hide();
})
function remove_app_id(obj) {
	var user_code = $("#USERID").val();//员工编号
	var corp_code = $("#OWN_CORP").val();//公司编号
	var app_id = $(obj).prevAll("input").attr("id");
	var src=$(obj).next(".kuang").children().attr("src");
	var param={
			"corp_code":corp_code,
			"user_code":user_code,
			"app_id":app_id
	}
	if(src!==""){
		oc.postRequire("post","/user/deletQrcode","",param,function (data) {
			if(data.code=="0"){
				$(obj).parent().remove();
			}else if(data.code=="-1"){
				alert("删除失败");
			}
		})
	}else if(src ==""){
		$(obj).parent().remove();
	}

}