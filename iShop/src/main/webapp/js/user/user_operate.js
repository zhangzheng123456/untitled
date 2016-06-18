var oc = new ObjectControl();
var addtype=sessionStorage.getItem("addtype");
addtype=JSON.parse(addtype);
(function(root,factory){
	root.user = factory();
}(this,function(){
	var useroperatejs={};
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
	useroperatejs.checkCode=function(obj,hint){
		var isCode=/^[S]{1}[Y]{1}[0-9]{1,7}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"请以大写字母SY开头从一位到七位之间的数字!");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
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
			return false;
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
		$(".useradd_oper_btn ul li:nth-of-type(1)").click(function(){
			var codeMark=$("#USERID").attr("data-mark");
			var phoneMark=$("#USER_PHONE").attr("data-mark");//手机号码是否唯一的标志
			var emailMark=$("#USER_EMAIL").attr("data-mark");//邮箱是否唯一的标志
			if(useroperatejs.firstStep()){
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
				var USERID=$("#USERID").val();
				var USER_NAME=$("#USER_NAME").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var USER_PHONE=$("#USER_PHONE").val();
				var USER_EMAIL=$("#USER_EMAIL").val();
				var USER_SEX=$("#USER_SEX").val();
				var SEX="";
				if(USER_SEX=="男"){
					SEX="M";
				}else if(USER_SEX=="女"){
					SEX="F";
				}
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_RIGHT=$("#OWN_RIGHT").data("myrcode");
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var STORE_CODE="";
				var storelist_length=$(".shop_list input");;
				for(var i=0;i<storelist_length.length;i++){
					var r=$(storelist_length[i]).data("myscode");
					if(i<storelist_length.length-1){
						STORE_CODE +=r+",";
					}else{
						STORE_CODE +=r;
					}
				}
				var _command="/user/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"group_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":STORE_CODE};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				console.log("lalla");
				return;
			}
		});
		$(".useredit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(useroperatejs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var USERID=$("#USERID").val();
				var USER_NAME=$("#USER_NAME").val();
				var HEADPORTRAIT=$("#preview img").attr("src");
				var USER_PHONE=$("#USER_PHONE").val();
				var USER_EMAIL=$("#USER_EMAIL").val();
				var USER_SEX=$("#USER_SEX").val();
				var SEX="";
				if(USER_SEX=="男"){
					SEX="M";
				}else if(USER_SEX=="女"){
					SEX="F";
				}
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_RIGHT=$("#OWN_RIGHT").data("myrcode");
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var STORE_CODE="";
				var storelist_length=$(".shop_list input");;
				for(var i=0;i<storelist_length.length;i++){
					var r=$(storelist_length[i]).data("myscode");
					if(i<storelist_length.length-1){
						STORE_CODE +=r+",";
					}else{
						STORE_CODE +=r;
					}
				}
				var PSW=$("#init_password").val();
				var _command="/user/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"group_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":STORE_CODE,"password":PSW};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	useroperatejs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
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
function selectownshop(obj){
	$(".shop_list ul").html('');
	store_li_list(obj.id);
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
    }else{
        ul.hide();
    }
}
function selectownrole(obj){
	$("#role_list").html('');
	role_li_list();
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
    }else{
        ul.hide();
    }
}
function addshopselect(){
		var k=$("#select_ownshop #shop_list div").length;
		$(".shop_list").append('<div id="per_type">'
            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+k+'" onclick="selectownshop(this)">'
                +'<input class="input_select"  style="width:280px" type="text" placeholder="请选择所属店铺" readonly/><span class="down_icon "><i class="icon-ishop_8-02"></i></span>'
                +'<ul style="margin-left:0px" id="store_list">'
                +'</ul>'
            +'</span>'
            +' <span class="minus_per_icon" onclick="minusshopselect(this)"><i class="icon-ishop_6-12"></i>删除店铺</span>'
        +'</div>');
}
function minusshopselect(obj){
	$(obj).parent().remove();
}
var c_code="";
var r_code="";
function role_li_list(){
	//拉取角色下拉选项

	if($(".pre_title label").text().trim()=="新增用户"){
		var addtype=sessionStorage.getItem("addtype");
		addtype=JSON.parse(addtype);
		if(addtype.user_type=="admin"){
			if(addtype.isAdmin=="Y"){
				r_code=addtype.role_code;
				c_code="";
				role_data(r_code,c_code);
			}else if(addtype.isAdmin=="N"){
				console.log("lalall");
				r_code=addtype.role_code;
				c_code=$('#OWN_CORP').val();
				role_data(r_code,c_code);
			}
		}else{
			c_code=$('#OWN_CORP').val();
			r_code=addtype.role_code;
			role_data(r_code,c_code);
		}
	}else{
		// var addtype=sessionStorage.getItem("key");
		// addtype=JSON.parse(addtype);
	 //    var addtype=JSON.parse(addtype.message);
		c_code=$('#OWN_CORP').val();
		role_data(r_code,c_code);
	}
}
function role_data(r,c){
	var _params={"group_code":r,"corp_code":c};
	var _command="/user/role";
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		var msg=JSON.parse(data.message);
		console.log(msg.group);
		var msg_roles=JSON.parse(msg.group);
		console.log(msg_roles);
		var index=0;
		var html="";
		console.log(msg_roles.length);
		if(msg_roles.length!==0){
			for(index in msg_roles){
				html +='<li data-rolecode="'+msg_roles[index].group_code+'">'+msg_roles[index].group_name+'</li>';
			}
		}else{
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"该企业目前没有群组，请先定义群组！"
			});
		}
		$("#role_list").append(html);
		$("#role_list li").click(function(){
            var this_=this;
            var txt = $(this_).text();
            var r_code=$(this_).data("rolecode");
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().children(".input_select").attr('data-myrcode',r_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
	});
}
function store_li_list(p) {
	// var addtype=sessionStorage.getItem("key");
	// addtype=JSON.parse(addtype);
 //    var addtype=JSON.parse(addtype.message);
	if($(".pre_title label").text().trim()=="新增用户"){
		var addtype=sessionStorage.getItem("addtype");
		addtype=JSON.parse(addtype);
		if(addtype.user_type=="admin"){
			if(addtype.isAdmin=="Y"){
				r_code=addtype.role_code;
				c_code="";
				store_data(p,r_code,c_code);
			}else if(addtype.isAdmin=="N"){
				r_code=addtype.role_code;
				c_code=$('#OWN_CORP').val();
				store_data(p,r_code,c_code);
			}
		}else{
			c_code=$('#OWN_CORP').val();
			r_code=addtype.role_code;
			store_data(p,r_code,c_code);
		}
	}else{
		c_code=$('#OWN_CORP').val();
		store_data(p,r_code,c_code);
	}
}
function store_data(p,r,c){
	var _params={"group_code":r,"corp_code":c};
	console.log(_params);
	var _command="/user/store";
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		var msg=JSON.parse(data.message);
		console.log(msg.stores);
		var msg_stores=JSON.parse(msg.stores);
		var index=0;
		var html="";
		if(msg_stores.length!==0){
			for(index in msg_stores){
				html +='<li data-storecode="'+msg_stores[index].store_code+'">'+msg_stores[index].store_name+'</li>';
			}
		}else{
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"该企业目前没有店铺，请先定义店铺！"
			});
		}
		$("#"+p+" ul").append(html);
		$("#"+p+" ul li").click(function(){
            var this_=this;
            var txt = $(this_).text();
            var s_code=$(this_).data("storecode");
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().children(".input_select").attr('data-myscode',s_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
	});
}
jQuery(document).ready(function(){
	window.user.init();//初始化
	var val=sessionStorage.getItem("key");
    val=JSON.parse(val);
    var message=JSON.parse(val.message);
	if($(".pre_title label").text()=="新增用户"){
		if(addtype.user_type=="admin"){
			if(addtype.isAdmin=="Y"){
				// $("#OWN_CORP").parent().parent().parent().parent().css("display","none");
				var _command="/user/getAdminCorp";
				oc.postRequire("post", _command,"", "", function(data){
					console.log(data);
					if(data.code=="0"){
						var msg=JSON.parse(data.message);
						var corp=msg.corp
						var index=0;
						var corp_html='';
						// var c=null;
						// for(index in msg.corp){
						// 	c=msg.corp[index];
							corp_html+='<option value="'+corp.corp_code+'">'+corp.corp_name+'</option>';
						// }
						$("#OWN_CORP").append(corp_html);
						$('.corp_select select').searchableSelect();
						$('.searchable-select-item').click(function(){
							$("input[verify='Code']").val("");
							$("#USER_PHONE").val("");
							$("#USER_EMAIL").val("");
							$("#USERID").attr("data-mark");
							$("#USER_PHONE").attr("data-mark","");
							$("#USER_EMAIL").attr("data-mark","");
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
				$("#select_ownshop").css("display","none");
			}else if(addtype.isAdmin=="N"){
				// $("#OWN_CORP").parent().parent().parent().parent().css("display","block");
				var _command="/user/getCorpByUser";
				oc.postRequire("post", _command,"", "", function(data){
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
							$("#USER_PHONE").val("");
							$("#USER_EMAIL").val("");
							$("#USERID").attr("data-mark");
							$("#USER_PHONE").attr("data-mark","");
							$("#USER_EMAIL").attr("data-mark","");
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
				$("#select_ownshop").css("display","block");
			}
		}else{
			$("#OWN_CORP").css({"background-color":"#dfdfdf"});
			$("#OWN_CORP").attr("readonly",true);
			$("#select_ownshop").css("display","block");
			var _params="";
			var _command="/user/add_code";
			oc.postRequire("post", _command,"", _params, function(data){
				console.log(data);
				$("#OWN_CORP").val(data.message);
			});

		}

	}else if($(".pre_title label").text()=="编辑员工信息"){
		console.log(message.user_type);
		if(message.user_type=="admin"){
			$("#OWN_CORP").parent().parent().parent().parent().css("display","block");
			$("#select_ownshop").css("display","block");
			var _command="/user/getCorpByUser";
			oc.postRequire("post", _command,"", "", function(data){
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
						$("#USER_PHONE").val("");
						$("#USER_EMAIL").val("");
						$("#USERID").attr("data-mark");
						$("#USER_PHONE").attr("data-mark","");
						$("#USER_EMAIL").attr("data-mark","");
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
		}else{
			$("#OWN_CORP").parent().parent().parent().parent().css("display","none");
			$("#OWN_CORP").css({"background-color":"#dfdfdf"});
			$("#OWN_CORP").attr("readonly",true);
			$("#select_ownshop").css("display","none");
		}
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/user/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				c_code=msg.corp_code;
				r_code=msg.group_code;
				$("#USERID").val(msg.user_code);
				$("#USER_NAME").val(msg.user_name);
				$("#preview img").attr("src",msg.avatar);
				$("#USER_PHONE").val(msg.phone);
				$("#USER_PHONE").attr("data-name",msg.phone);
				$("#USER_EMAIL").val(msg.email);
				$("#USER_EMAIL").attr("data-name",msg.email);
				if(msg.sex=="F"){
					$("#USER_SEX").val("女");
				}else if(msg.sex=="M"){
					$("#USER_SEX").val("男");
				}
				if(msg.store_code==''){
					$("#select_ownshop").css("display","none");
					$("#OWN_CORP").parent().parent().parent().parent().css("display","none");
					$("#OWN_RIGHT").val(msg.group.group_name);
					$("#OWN_RIGHT").attr("data-myrcode",msg.group.group_code);
					$("#OWN_CORP option").val("");
					$("#OWN_CORP option").text("");
				}else if(msg.store_code !==''){
					$("#OWN_CORP").parent().parent().parent().parent().css("display","block");
					$("#select_ownshop").css("display","block");
					$("#OWN_CORP option").val(msg.corp.corp_code);
					$("#OWN_CORP option").text(msg.corp.corp_name);
					$("#OWN_RIGHT").val(msg.group.group_name);
					$("#OWN_RIGHT").attr("data-myrcode",msg.group.group_code);
					var store_lists=msg.store_name.split(",");
					var storecode_list=msg.store_code.split(",");
					if(store_lists.length==0){
						$("#OWN_STORE").val("");
					}else if(store_lists==1){
						$("#OWN_STORE").val(store_lists[0]);
						$("#OWN_STORE").attr("data-myscode",msg.store_code);
					}else{
						$("#OWN_STORE").val(store_lists[0]);
						$("#OWN_STORE").attr("data-myscode",storecode_list[0]);
						var html='';
						for(var i=1;i<store_lists.length;i++){
							html +='<div id="per_type">'
					            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+i+'" onclick="selectownshop(this)">'
					                +'<input class="input_select" style="width:280px" type="text" data-myscode="'+storecode_list[i]+'"  value="'+store_lists[i]+'" placeholder="请选择所属店铺" readonly/><span class="down_icon "><i class="icon-ishop_8-02"></i></span>'
					                +'<ul style="margin-left:0px">'
					                +'</ul>'
					            +'</span>'
					            +' <span class="minus_per_icon" onclick="minusshopselect(this)"><i class="icon-ishop_6-12"></i>删除店铺</span>'
					        +'</div>';
						}
						$(".shop_list").append(html);
					}
				}
				$("#register_time").val(msg.created_date);
				$("#recently_login").val(msg.login_time_recently);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				$("#init_password").val(msg.password);
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
	$(".useradd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	$(".useredit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	//验证编号是否唯一的方法
	$("input[verify='Code']").blur(function(){
    	var isCode=/^[S]{1}[Y]{1}[0-9]{1,7}$/;
    	var _params={};
    	var user_code=$(this).val();//员工编号
    	var corp_code=$("#OWN_CORP").val();//公司编号
    	var group_code=$
    	if(addtype.isAdmin=="Y"){
        	corp_code="";
    	}
		if(user_code!==""&&isCode.test(user_code)==true){
			_params["user_code"]=user_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/user/UserCodeExist","", _params, function(data){
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
    //验证邮箱是否唯一的方法
    $("#USER_EMAIL").blur(function(){
    	var email=$("#USER_EMAIL").val();//邮箱名称
    	var email1=$("#USER_EMAIL").attr("data-name");//编辑的标志
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();//企业编号
    	if(addtype.isAdmin=="Y"){
        	corp_code="";
    	}
    	console.log(corp_code);
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
    	if(addtype.isAdmin=="Y"){
        	corp_code="";
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
	            	div.html("该名称已经存在！");
	            	div.addClass("error_tips");
	            	$("#USER_PHONE").attr("data-mark","N");
	            }
	    	})
	    }
    })
});
$(".corp_select").click(function(){
	// $("select").change(function(){
		$("#OWN_RIGHT").val('');
		$("#OWN_STORE").val('');
	// });
});
