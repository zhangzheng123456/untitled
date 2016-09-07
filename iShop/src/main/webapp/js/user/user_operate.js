var oc = new ObjectControl();
//自定义选择器
$.expr[":"].searchableSelectContains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
      return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
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
		var isMob=/^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
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
	useroperatejs.checkMail = function(obj,hint){
		var reg=/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/;
		if(!this.isEmpty(obj)){
			if(reg.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"邮箱格式不正确！")
				return true;
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
		$(".useradd_oper_btn ul li:nth-of-type(1)").click(function(){
			var codeMark=$("#USERID").attr("data-mark");//编号是唯一的标志
			var idMark=$("#user_id").attr("data-mark");//ID是否唯一的标志
			var phoneMark=$("#USER_PHONE").attr("data-mark");//手机号码是否唯一的标志
			var emailMark=$("#USER_EMAIL").attr("data-mark");//邮箱是否唯一的标志
			if(useroperatejs.firstStep()){
				if(idMark=="N"){
					var div=$("#user_id").next('.hint').children();
					div.html("员工ID已经存在！");
					div.addClass("error_tips");
					return;
				}
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
				var user_id=$("#user_id").val();
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
							content:"所属群组不能为空！"
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
				var STORE_CODE="";
				var storelist_length=$(".shop_list .input_select");;
				for(var i=0;i<storelist_length.length;i++){
					var r=$(storelist_length[i]).attr("data-myscode");
					if(i<storelist_length.length-1){
						STORE_CODE +=r+",";
					}else{
						STORE_CODE +=r;
					}
				}
				//如果角色是导购，店长，区经的时候
				if(r_code=="R2000"||r_code=="R3000"||r_code=="R4000"){
					if(STORE_CODE==""){
						art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content:"所属店铺或所属区域不能为空！"
						});
						return;
					}
				}
				var _command="/user/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["user_code"]=USERID;//员工编号
				_params["user_id"]=user_id;//员工ID
				_params["username"]=USER_NAME;//员工名称
				_params["avater"]="";//新增的时候头像字段先设置为空
				_params["phone"]=USER_PHONE;//手机
				_params["position"]=position;//职务
				_params["email"]=USER_EMAIL//邮箱
				_params["sex"]=SEX//性别
				_params["group_code"]=OWN_RIGHT;//群组编号
				_params["role_code"]=r_code;//角色编号
				_params["isactive"]=ISACTIVE;//是否可用
				_params["corp_code"]=OWN_CORP;//公司编号
				_params["can_login"]=can_login;//是否登录
				if(r_code=="R2000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            }else if(r_code=="R3000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            }else if(r_code=="R4000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=STORE_CODE;//区域编号
	            }
	            else if(r_code=="R5000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            }else if(r_code=="R6000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            }
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
				var user_id=$("#user_id").val();
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
						content:"所属群组不能为空！"
					});
					return;
				}
				var avater="";//头像
                if ($("#OWN_CORP").val() !== '' && $("#preview img").attr("src") !== '../img/head.png') {
                    avater= "http://products-image.oss-cn-hangzhou.aliyuncs.com/Avatar/User/iShow/"+OWN_CORP.trim()+USERID.trim()+'.jpg';
                } else {
                   avater=$("#preview img").attr("src");//头像
                }
				var can_login="";//可登录状态
                var input1=$("#invisible")[0];
                if(input1.checked==true){
                	can_login="Y";
                }else if(input1.checked==false){
                	can_login="N";
                }

				var STORE_CODE="";
				var storelist_length=$(".shop_list .input_select");;
				for(var i=0;i<storelist_length.length;i++){
					var r=$(storelist_length[i]).attr("data-myscode");
					if(i<storelist_length.length-1){
						STORE_CODE +=r+",";
					}else{
						STORE_CODE +=r;
					}
				}
				// var PSW=$("#init_password").val();
				//如果角色是导购，店长，区经的时候
				if(r_code=="R2000"||r_code=="R3000"||r_code=="R4000"){
					if(STORE_CODE==""){
						art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content:"所属店铺或所属区域不能为空！"
						});
						return;
					}
				}
				// if(PSW==""){
				// 	art.dialog({
				// 		time: 1,
				// 		lock:true,
				// 		cancel: false,
				// 		content:"密码不能为空！"
				// 	});
				// 	return;
				// }
				var _command="/user/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["user_code"]=USERID;//员工编号
				_params["user_id"]=user_id;//员工ID
				_params["username"]=USER_NAME;//员工名称
				_params["avater"]=avater;//头像
				_params["position"]=position;//职务
				_params["phone"]=USER_PHONE;//手机
				_params["email"]=USER_EMAIL//邮箱
				_params["sex"]=SEX//性别
				_params["group_code"]=OWN_RIGHT;//群组编号
				_params["role_code"]=r_code;//角色编号
				_params["isactive"]=ISACTIVE;//是否可用
				_params["corp_code"]=OWN_CORP;//公司编号
				_params["can_login"]=can_login;//是否登录
				// _params["password"]=PSW;//密码
				_params["id"]=ID;//ID
				if(r_code=="R2000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            }else if(r_code=="R3000"){
	            	_params["store_code"]=STORE_CODE;//店铺编号
	            	_params["area_code"]="";//区域编号
	            }else if(r_code=="R4000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=STORE_CODE;//区域编号
	            }
	            else if(r_code=="R5000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            }else if(r_code=="R6000"){
	            	_params["store_code"]=""//店铺编号
	            	_params["area_code"]=""//区域编号
	            }
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	useroperatejs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
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
function selectownshop(obj){//加载店铺列表的时候
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	console.log(obj);
	var input=$(obj);
	var div=$(obj).nextAll('.store_list_kuang');
	var inputs=$(obj).nextAll('.store_list_kuang').find('.search');
    if(div.css("display")=="none"){
        div.show();
    }else{
        div.hide();
    };
    // $(this).find("h1").parents().siblings("li").find("dl").slideUp(300);
    $(div).parent().parent().siblings('div').find(".store_list_kuang").hide();
    $(inputs).on('keyup', function(event){
	    var text=$(this).val();
	    console.log(text);
	    $(this).siblings('ul').find("li").addClass('store_list_kuang_hide');
	    $(this).siblings('ul').find('li:searchableSelectContains('+text+')').removeClass('store_list_kuang_hide');
    })
    var c_code=$('#OWN_CORP').val();
	var corp_code1=$(input).attr("corp_code");
	if(c_code==corp_code1){
		return;
	}
	$(input).attr("corp_code",c_code);

    store_li_list(obj);     
}
function selectownarea(obj){//加载区域列表的时候
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	console.log(obj);
	var input=$(obj);
	var div=$(obj).nextAll('.store_list_kuang');
	var inputs=$(obj).nextAll('.store_list_kuang').find('.search');
    if(div.css("display")=="none"){
        div.show();
    }else{
        div.hide();
    };
    $(div).parent().parent().siblings('div').find(".store_list_kuang").hide();
    $(inputs).on('keyup', function(event){
	    var text=$(this).val();
	    console.log(text);
	    $(this).siblings('ul').find("li").addClass('store_list_kuang_hide');
	    $(this).siblings('ul').find('li:searchableSelectContains('+text+')').removeClass('store_list_kuang_hide');
    })
    var c_code=$('#OWN_CORP').val();
	var corp_code1=$(input).attr("corp_code");
	if(c_code==corp_code1){
		return;
	}
	$(input).attr("corp_code",c_code);
	area_li_list(obj);    
}
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
// var c_code="";
// var r_code="";
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
				html +='<li data-jucode="'+msg_roles[index].role_code+'" data-rolecode="'+msg_roles[index].group_code+'">'+msg_roles[index].group_name+'</li>';
			}
		}else{
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"该企业目前没有群组，请先定义群组！"
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
            if(j_code=="R2000"){
            	$('#sidename label').html("所属店铺");
            	$('#OWN_STORE').attr("placeholder","请选择所属店铺");
            	$('#OWN_STORE').attr("data-myscode","");
            	$("#OWN_STORE").attr("corp_code","");
            	$('#OWN_STORE').val("");
            	$('#OWN_STORE').attr("onclick","selectownshop(this)");
            	$('#add_per_icon').attr("onclick","addshopselect()");
            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增店铺");
            	// $('#add_per_icon').css({"backgroundColor":"#ddd"});
            	$("#ownshop_list").show();
            	$("#ownshop_list .per_type").nextAll().remove();
            }else if(j_code=="R3000"){
            	$('#sidename label').html("所属店铺");
            	$('#OWN_STORE').attr("placeholder","请选择所属店铺");
            	$('#OWN_STORE').attr("data-myscode","");
            	$("#OWN_STORE").attr("corp_code","");
            	$('#OWN_STORE').val("");
            	$('#OWN_STORE').attr("onclick","selectownshop(this)");
            	$('#add_per_icon').attr("onclick","addshopselect()");
            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增店铺");
            	$('#add_per_icon').css({"backgroundColor":"#6cc1c8"});
            	$("#ownshop_list").show();
            	$("#ownshop_list .per_type").nextAll().remove();
            }else if(j_code=="R4000"){
            	$('#sidename label').html("所属区域");
            	$('#OWN_STORE').attr("placeholder","请选择所属区域");
            	$('#OWN_STORE').attr("data-myscode","");
            	$('#OWN_STORE').val("");
            	$('#OWN_STORE').attr("onclick","selectownarea(this)");
            	$('#add_per_icon').attr("onclick","addareaselect()");
            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增区域");
            	$('#add_per_icon').css({"backgroundColor":"#6cc1c8"});
            	$("#ownshop_list").show();
            	$("#OWN_STORE").attr("corp_code","");
            	$("#ownshop_list .per_type").nextAll().remove();
            }
            else if(j_code=="R5000"){
            	$("#ownshop_list").hide();
            }else if(j_code=="R6000"){
            	$("#ownshop_list").hide();
            }
        });
	});
}
function store_li_list(p){//店铺
	var c_code=$('#OWN_CORP').val();
	// var corp_code1=$('#OWN_CORP').attr("corp_code");
	// if(c_code==corp_code1){
	// 		return;
	// }
	$('#OWN_CORP').attr("corp_code",c_code);
	store_data(p,c_code);
}
function area_li_list(p) {//区域
	var c_code=$('#OWN_CORP').val();
	// var corp_code1=$('#OWN_CORP').attr("corp_code");
	// if(c_code==corp_code1){
	// 		return;
	// }
	$('#OWN_CORP').attr("corp_code",c_code);
	area_data(p,c_code);
}
function store_data(p,c){//店铺
	// var _params={"group_code":r,"corp_code":c};
	var _params={"corp_code":c};	
	console.log(_params);
	var _command="/user/store";
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", _command,"", _params, function(data){
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
		whir.loading.remove();//移除加载框
		$(p).siblings('.store_list_kuang').find("ul").html(html);
		$(p).siblings('.store_list_kuang').find("li").click(function(){
			var event=window.event||arguments[0];
        	if(event.stopPropagation){
            	event.stopPropagation();
        	}else{
            	event.cancelBubble=true;
        	}
            var this_=this;
            var txt = $(this_).text();
            console.log(txt);
            var s_code=$(this_).data("storecode");
            $(this_).parent().parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().parent().children(".input_select").attr('data-myscode',s_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
            $(this_).parent().parent().hide();
        });
	});//追加店铺
}
function area_data(p,c){//区域
	var _params={"corp_code":c};	
	console.log(_params);
	var _command="/shop/area";
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		var msg=JSON.parse(data.message);
		var msg_areas=msg.areas;
		var index=0;
		var html="";
		if(msg_areas.length!==0){
			for(index in msg_areas){
				html +='<li data-storecode="'+msg_areas[index].area_code+'">'+msg_areas[index].area_name+'</li>';
			}
		}else{
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"该企业目前没有区域，请先定义区域！"
			});
		}
		whir.loading.remove();//移除加载框
		$(p).siblings('.store_list_kuang').find("ul").html(html);
		$(p).siblings('.store_list_kuang').find("li").click(function(){
			var event=window.event||arguments[0];
        	if(event.stopPropagation){
            	event.stopPropagation();
        	}else{
            	event.cancelBubble=true;
        	}
            var this_=this;
            var txt = $(this_).text();
            console.log(txt);
            var s_code=$(this_).data("storecode");
            $(this_).parent().parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().parent().children(".input_select").attr('data-myscode',s_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
            $(this_).parent().parent().hide();
        });
	});//追加店铺
}
function addshopselect(){//店铺
		var k=$("#select_ownshop .shop_list div").length;
		console.log(k);
		$(".shop_list").append('<div id="per_type">'
            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+k+'">'
                +'<input class="input_select"  style="width:280px" type="text" placeholder="请选择所属店铺" readonly data-myscode="" onclick="selectownshop(this)"/>'
                +'<div class="store_list_kuang">'
                +'<input class="search" type="text" placeholder="请输入搜索内容">'
                +'<ul style="margin-left:0px" id="store_list">'
                +'</ul>'
                +'</div>'
            +'</span>'
            +' <span class="minus_per_icon" onclick="minusshopselect(this)"><i class="icon-ishop_6-12"></i>删除店铺</span>'
        +'</div>');
}
function addareaselect(){//区域
		var k=$("#select_ownshop .shop_list div").length;
		$(".shop_list").append('<div id="per_type">'
            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+k+'">'
                +'<input class="input_select"  style="width:280px" type="text" placeholder="请选择所属区域" readonly data-myscode="" onclick="selectownarea(this)"/>'
                +'<div class="store_list_kuang">'
                +'<input class="search" type="text" placeholder="请输入搜索内容">'
                +'<ul style="margin-left:0px" id="store_list">'
                +'</ul>'
                +'</div>'
            +'</span>'
            +' <span class="minus_per_icon" onclick="minusareaselect(this)"><i class="icon-ishop_6-12"></i>删除区域</span>'
        +'</div>');

}
function minusareaselect(obj){//区域  删除
	$(obj).parent().remove();
}
function minusshopselect(obj){//店铺  删除
	$(obj).parent().remove();
}
$(document).click(function(e){
	if($(e.target).is('.shop_list .input_select')||$(e.target).is('.store_list_kuang')||$(e.target).is('.store_list_kuang .search')||$(e.target).is('.store_list_kuang ul')||$(e.target).is('.store_list_kuang li')){
	    return;
	}else{
	    $(".store_list_kuang").hide();
	}
});
//ready事件
jQuery(document).ready(function(){
	window.user.init();//初始化
    if($(".pre_title label").text()=="编辑员工信息"){
    	var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/user/select";
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var j_code=msg.group.role_code;//角色编号
				$("#USERID").val(msg.user_code);
				$("#USERID").attr("data-name",msg.user_code);
				$("#user_id").val(msg.user_id);
				$("#user_id").attr("data-name",msg.user_id);
				$("#USER_NAME").val(msg.user_name);
				if(msg.avatar==""){
					$("#preview img").attr("src","../img/head.png");
				}else if(msg.avatar!==""){
					$("#preview img").attr("src",msg.avatar);
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
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);
				$("#OWN_RIGHT").val(msg.group.group_name);
				$("#OWN_RIGHT").attr("data-myrcode",msg.group.group_code);//编辑的时候赋值给群组编号
				$("#OWN_RIGHT").attr("data-myjcode",j_code);//编辑的时候赋值给角色编号
				if(j_code=="R2000"){
	            	$('#sidename label').html("所属店铺");
	            	$('#OWN_STORE').attr("placeholder","请选择所属店铺");
	            	$('#sidedown').attr("onclick","selectownshop(this)");
	            	$('#add_per_icon').attr("onclick","addshopselect()");
	            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增店铺");
	            	// $('#add_per_icon').css({"backgroundColor":"#ddd"});
	            	$("#ownshop_list").show();
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
						        +'<span style="display:inline-block;" data-i="1" id="store_lists_'+i+'">'
							        +'<input class="input_select" style="width:280px" type="text" data-myscode="'+storecode_list[i]+'"  value="'+store_lists[i]+'" placeholder="请选择所属店铺" onclick="selectownshop(this)" readonly/>'
							        +'<div class="store_list_kuang">'
	                				+'<input class="search" type="text" placeholder="请输入搜索内容">'
	                				+'<ul style="margin-left:0px" id="store_list">'
	                				+'</ul>'
	                				+'</div>'
						        +'</span>'
						        +' <span class="minus_per_icon" onclick="minusshopselect(this)"><i class="icon-ishop_6-12"></i>删除店铺</span>'
						        +'</div>';
						}
						$(".shop_list").append(html);
					}	
            	}else if(j_code=="R3000"){
	            	$('#sidename label').html("所属店铺");
	            	$('#OWN_STORE').attr("placeholder","请选择所属店铺");
	            	$('#sidedown').attr("onclick","selectownshop(this)");
	            	$('#add_per_icon').attr("onclick","addshopselect()");
	            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增店铺");
	            	$("#ownshop_list").show();
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
						        +'<span style="display:inline-block;" data-i="1" id="store_lists_'+i+'">'
						        +'<input class="input_select" style="width:280px" type="text" data-myscode="'+storecode_list[i]+'"  value="'+store_lists[i]+'" placeholder="请选择所属店铺" onclick="selectownshop(this)" readonly/>'
						        +'<div class="store_list_kuang">'
	                				+'<input class="search" type="text" placeholder="请输入搜索内容">'
	                				+'<ul style="margin-left:0px" id="store_list">'
	                				+'</ul>'
	                			+'</div>'
						        +'</span>'
						        +' <span class="minus_per_icon" onclick="minusshopselect(this)"><i class="icon-ishop_6-12"></i>删除店铺</span>'
						        +'</div>';
						}
						$(".shop_list").append(html);
					}	
            	}else if(j_code=="R4000"){
	            	$('#sidename label').html("所属区域");
	            	$('#OWN_STORE').attr("placeholder","请选择所属区域");
	            	$('#OWN_STORE').attr("onclick","selectownarea(this)");
	            	$('#add_per_icon').attr("onclick","addareaselect()");
	            	$('#add_per_icon').html("<i class='icon-ishop_6-01'></i>新增区域");
	            	$("#ownshop_list").show();
	            	var area_lists=msg.area_name.split(",");
					var areacode_list=msg.area_code.split(",");
	            	if(area_lists.length==0){
						$("#OWN_STORE").val("");
					}else if(area_lists==1){
						$("#OWN_STORE").val(area_lists[0]);
						$("#OWN_STORE").attr("data-myscode",msg.area_code);
					}else{
						$("#OWN_STORE").val(area_lists[0]);
						$("#OWN_STORE").attr("data-myscode",areacode_list[0]);
						var html='';
						for(var i=1;i<area_lists.length;i++){
							html +='<div id="per_type">'
						        +'<span style="display:inline-block;" data-i="1" id="store_lists_'+i+'">'
						        +'<input class="input_select" style="width:280px" type="text" data-myscode="'+areacode_list[i]+'"  value="'+area_lists[i]+'" placeholder="请选择所属店铺" onclick="selectownarea(this)" readonly/>'
						        +'<div class="store_list_kuang">'
	                				+'<input class="search" type="text" placeholder="请输入搜索内容">'
	                				+'<ul style="margin-left:0px" id="store_list">'
	                				+'</ul>'
	                				+'</div>'
						        +'</span>'
						        +' <span class="minus_per_icon" onclick="minusareaselect(this)"><i class="icon-ishop_6-12"></i>删除区域</span>'
						        +'</div>';
						}
						$(".shop_list").append(html);
					}	
            	}
	            else if(j_code=="R5000"){
            		$("#ownshop_list").hide();
            	}else if(j_code=="R6000"){
            		$("#ownshop_list").hide();
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
				var qrcodeList=msg.qrcodeList;
				if(qrcodeList.length>0) {
					for (var i = 0; i < qrcodeList.length; i++) {
						$("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" value="' + qrcodeList[i].app_name + '" readonly="readonly"><ul></ul>'
							+ '<span class="power create" onclick="getTwoCode(this)">生成</span>'
							+ '<span class="power k_close" style="display: none;">关闭</span>'
							+ '<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
							+ '<div class="kuang"><img src="' + qrcodeList[i].qrcode + '" alt="">'
							+'</div></li>')
					}
					$(".kuang").show();
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
	$(".useradd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/user/user.html");
	});
	$(".useredit_oper_btn ul li:nth-of-type(2)").click(function(){
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
		_params["password"]=password;
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
function getcorplist(){
	//获取企业列表
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
					$("#USER_PHONE").val("");
					$("#USER_EMAIL").val("");
					$("#USERID").val("");
					$("#USERID").attr("data-mark","");
					$("#USER_PHONE").attr("data-mark","");
					$("#USER_EMAIL").attr("data-mark","");
					$("#OWN_RIGHT").val('');
					$("#OWN_RIGHT").attr("data-myrcode","");
					$("#OWN_STORE").val('');
					$("#OWN_STORE").attr("data-myscode","");
					$("#ownshop_list .per_type").nextAll().remove();
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
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			var list=msg.list;
			console.log(list);
			$(a).next("ul").empty();
			for(var i=0;i<list.length;i++){
				$(a).next("ul").append('<li data-id="'+list[i].app_id+'">'+list[i].app_name+'</li>')
			}
			$(a).next("ul").find("li").click(function () {
				var value = $(this).html();
				console.log(value);
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
	var input=$(b).prevAll("input").val();
	close_two_code();
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
		alert("请选择公众号!");
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
//关闭二维码
function close_two_code() {
	$(".k_close").click(function () {
		$(this).nextAll(".kuang").hide();
		$(this).hide();
		$(this).prev(".create").show();
	})
}

$("#add_app_id").click(function(){
	$("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" readonly="readonly"><ul></ul>'
		+'<span class="power create" onclick="getTwoCode(this)">生成</span>'
		+'<span class="power k_close" style="display: none;">关闭</span>'
		+'<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
		+'<div class="kuang"><img src="" alt="">'
		+'</div></li>')

	close_two_code();
})
function remove_app_id(obj) {
	$(obj).parent().remove();
}


