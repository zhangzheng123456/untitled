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
				var OWN_RIGHT=$("#OWN_RIGHT").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==true){
					ISACTIVE="N";
				}
				var _command="/user/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"role_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":""};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".useredit_oper_btn ul li:nth-of-type(1)").click(function(){
			// if(useroperatejs.firstStep()){
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
				var OWN_RIGHT=$("#OWN_RIGHT").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==true){
					ISACTIVE="N";
				}
				var PSW=$("#init_password").val();
				var _command="/user/edit";//接口名
				console.log(HEADPORTRAIT);
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"user_code":USERID,"username":USER_NAME,"avater":HEADPORTRAIT,"phone":USER_PHONE,"email":USER_EMAIL,"sex":SEX,"role_code":OWN_RIGHT,"isactive":ISACTIVE,"corp_code":OWN_CORP,"store_code":"","password":PSW};
				useroperatejs.ajaxSubmit(_command,_params,opt);
			// }else{
			// 	return;
			// }
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
var i=0;
function selectownshop(obj){
	if(i==0){
		store_li_list();
	}
	var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
        $(obj).children("ul").children('li').click(function(){
            var this_=this;
            var txt = $(this_).text();
            $(this_).parent().parent().children(".input_select").val(txt);
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
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
    }else{
        ul.hide();
    }
}
function addshopselect(){
	$(".shop_list").append('<div>'
            +'<span style="display:inline-block;" onclick="selectownshop(this)">'
                +'<input class="input_select" id="OWN_RIGHT" type="text" placeholder="请选择所属店铺" readonly/><span class="down_icon "><i class="icon-ishop_8-02"></i></span>'
                +'<ul style="margin-left:0px">'
                    +'<li>南京店铺</li>'
                    +'<li>上海店铺</li>'
                    +'<li>杭州店铺</li>'
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
				html +='<li>'+msg_roles[index].role_name+'</li>';
			}
		}
		$("#role_list").append(html);
	});
}
function store_li_list() {
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
				html +='<li>'+msg_stores[index].store_name+'</li>';
			}
		}
		$("#store_list").append(html);
	});
}
jQuery(document).ready(function(){
	window.user.init();//初始化
	var val=sessionStorage.getItem("key");
    val=JSON.parse(val);
    var message=JSON.parse(val.message);
	if($(".pre_title label").text()=="新增用户"){
		console.log(message.user_type);
		if(message.user_type=="admin"){
			$("#OWN_CORP").parent().parent().css("display","none");
			$("#select_ownshop").css("display","none");
		}else{
			$("#OWN_CORP").css({"background-color":"#dfdfdf"});
			$("#OWN_CORP").attr("readonly",true);
			$("#select_ownshop").css("display","block");
		}
	}else if($(".pre_title label").text()=="编辑用户信息"){
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
				}else{
					$("#OWN_CORP").val(msg.corp_code);
					$("#OWN_RIGHT").val(msg.role.role_name);
					console.log(msg.store_name);
					var store_lists=msg.store_name.split(",");
					console.log(store_lists);
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
});