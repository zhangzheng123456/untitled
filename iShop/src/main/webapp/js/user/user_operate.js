var oc = new ObjectControl();
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
			if(useroperatejs.firstStep()){
				console.log("1");
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
				}else if(input.checked==true){
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
				var _params={"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"role_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":STORE_CODE};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
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
				}else if(input.checked==true){
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
				// var STORE_list=;
				// for(var i=0;i<){

				// }
				var PSW=$("#init_password").val();
				var _command="/user/edit";//接口名
				console.log(HEADPORTRAIT);
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"role_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":STORE_CODE,"password":PSW};
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
	if(obj.getAttribute("data-i")=="1"){
		store_li_list(obj.id);
		obj.setAttribute('data-i','2');
	}
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
        $(obj).children("ul").children('li').click(function(){
            var this_=this;
            var txt = $(this_).text();
            var s_code=$(this_).data("storecode");
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().children(".input_select").attr('data-myscode',s_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
    }else{
        ul.hide();
    }
}
var j=0;
function selectownrole(obj){
	if(j==0){
		role_li_list();
		j++;
	}
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
        $(obj).children("ul").children('li').click(function(){
            var this_=this;
            var txt = $(this_).text();
            var r_code=$(this_).data("rolecode");
            console.log(r_code);
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).parent().parent().children(".input_select").attr("data-myrcode",r_code);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
    }else{
        ul.hide();
    }
}
function addshopselect(){
		var k=$("#shop_list div").length;
		$(".shop_list").append('<div>'
            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+k+'" onclick="selectownshop(this)">'
                +'<input class="input_select"  type="text" placeholder="请选择所属店铺" readonly/><span class="down_icon "><i class="icon-ishop_8-02"></i></span>'
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
	var _params={"role_code":r_code,"corp_code":c_code};
	var _command="/user/role";
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		var msg=JSON.parse(data.message);
		console.log(msg.roles);
		var msg_roles=JSON.parse(msg.roles);
		console.log(msg_roles);
		var index=0;
		var html="";
		if(msg_roles[0].role_name){
			for(index in msg_roles){
				html +='<li data-rolecode="'+msg_roles[index].role_code+'">'+msg_roles[index].role_name+'</li>';
			}
		}
		$("#role_list").append(html);
	});
}
function store_li_list(p) {
	var _params={"role_code":c_code,"corp_code":c_code};
	var _command="/user/store";
	oc.postRequire("post", _command,"", _params, function(data){
		console.log(data);
		var msg=JSON.parse(data.message);
		console.log(msg.stores);
		var msg_stores=JSON.parse(msg.stores);
		var index=0;
		var html="";
		if(msg_stores[0].store_name){
			for(index in msg_stores){
				html +='<li data-storecode="'+msg_stores[index].store_code+'">'+msg_stores[index].store_name+'</li>';
			}
		}
		$("#"+p+" ul").append(html);
	});
}
jQuery(document).ready(function(){
	window.user.init();//初始化
	var val=sessionStorage.getItem("key");
	var addtype=sessionStorage.getItem("addtype");
	addtype=JSON.parse(addtype);
    val=JSON.parse(val);
    var message=JSON.parse(val.message);
	if($(".pre_title label").text()=="新增用户"){
		if(addtype.user_type=="admin"){
			if(addtype.isAdmin=="Y"){
				$("#OWN_CORP").parent().parent().css("display","none");
				$("#select_ownshop").css("display","none");
			}else if(addtype.isAdmin=="N"){
				$("#OWN_CORP").parent().parent().css("display","block");
				$("#select_ownshop").css("display","block");
			}
			var _command="/getCorpByUser";
			oc.postRequire("post", _command,"", "", function(data){
				console.log(data);
			});
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

	}else if($(".pre_title label").text()=="编辑用户信息"){
		console.log(message.user_type);
		if(message.user_type=="admin"){
			$("#OWN_CORP").parent().parent().css("display","none");
			$("#select_ownshop").css("display","none");
		}else{
			$("#OWN_CORP").css({"background-color":"#dfdfdf"});
			$("#OWN_CORP").attr("readonly",true);
			$("#select_ownshop").css("display","block");
		}
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/user/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				console.log(msg.user_code);
				console.log(msg.role);
				c_code=msg.corp_code;
				r_code=msg.role_code;
				$("#USERID").val(msg.user_code);
				$("#USER_NAME").val(msg.user_name);
				$("#preview img").attr("src",msg.avatar);
				$("#USER_PHONE").val(msg.phone);
				$("#USER_EMAIL").val(msg.email);
				if(msg.sex=="M"){
					$("#USER_SEX").val("女");
				}else if(msg.sex=="F"){
					$("#USER_SEX").val("男");
				}
				if(msg.corp_code==''){
					$("#select_ownshop").css("display","none");
					$("#OWN_CORP").parent().parent().css("display","none");
				}else if(msg.corp_code !==''){
					$("#OWN_CORP").parent().parent().css("display","block");
					$("#select_ownshop").css("display","block");
					$("#OWN_CORP").val(msg.corp_code);
					$("#OWN_RIGHT").val(msg.role.role_name);
					$("#OWN_RIGHT").attr("data-myrcode",msg.role.role_code);
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
							html +='<div>'
					            +'<span style="display:inline-block;" data-i="1" id="store_lists_'+i+'" onclick="selectownshop(this)">'
					                +'<input class="input_select"  type="text" data-myscode="'+storecode_list[i]+'"  value="'+store_lists[i]+'" placeholder="请选择所属店铺" readonly/><span class="down_icon "><i class="icon-ishop_8-02"></i></span>'
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

	// $("#OWN_CORP").focus(function() {
	// 	interval = setInterval(function() {
	// 		$("#OWN_CORP").blur(function(){
	// 			var this_code=$(this).val();
	// 		  	var _params={"corp_code":this_code};
	// 			var _command="/corp/exist";
	// 			oc.postRequire("post", _command,"", _params, function(data){
	// 				if(data.code=="-1"){
	// 					art.dialog({
	// 						time: 1,
	// 						lock:true,
	// 						cancel: false,
	// 						content: "该企业编号以存在，请重新输入！"
	// 					});
	// 				}
	// 			});
	// 		});
	// 	}, 500);
	// }).blur(function(event) {
	// 	clearInterval(interval);
	// });

});