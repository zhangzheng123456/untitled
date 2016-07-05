var oc = new ObjectControl();
(function(root,factory){
	root.iShop = factory();
}(this,function(){
	var iShopjs={};
	iShopjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	iShopjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	iShopjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	iShopjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	iShopjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	iShopjs.bindbutton=function(){
		$(".iShopadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(iShopjs.firstStep()){
				var RESERVEL=$("#RESERVEL").val();//接收人
				var MESSAGE_TYPE=$("#MESSAGE_TYPE").val();//消息类型
				var CONTENT_TITEL=$("#CONTENT_TITEL").val();//内容标题
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//模板内容
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/storeAchvGoal/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"reservel": RESERVEL,
					"message_type": MESSAGE_TYPE,
					"content_titel": CONTENT_TITEL,
					"moban_content":MOBAN_CONTENT,
					"isactive": ISACTIVE
				};
				iShopjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".iShopedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(iShopjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var RESERVEL=$("#RESERVEL").val();//接收人
				var MESSAGE_TYPE=$("#MESSAGE_TYPE").val();//消息类型
				var CONTENT_TITEL=$("#CONTENT_TITEL").val();//内容标题
				var MOBAN_CONTENT=$("#MOBAN_CONTENT").val();//模板内容
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/storeAchvGoal/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"id": ID,
					"reservel": RESERVEL,
					"message_type": MESSAGE_TYPE,
					"content_titel": CONTENT_TITEL,
					"moban_content":MOBAN_CONTENT,
					"isactive": ISACTIVE
				};
				iShopjs.ajaxSubmit(_command, _params, opt);
			}else{
				return;
			}
		});
	};
	iShopjs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				// if(opt.success){
				// 	opt.success();
				// }
				// window.location.href="";
				$(window.parent.document).find('#iframepage').attr("src","/message/iShop.html");
			}else if(data.code=="-1"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
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
		if(iShopjs['check' + command]){
			if(!iShopjs['check' + command].apply(iShopjs,[obj,hint])){
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
		iShopjs.bindbutton();
	}
	var obj = {};
	obj.iShopjs = iShopjs;
	obj.init = init;
	return obj;
}));
//编辑页面赋值
jQuery(document).ready(function(){
	window.iShop.init();//初始化
	var a="";//
	var b="";//
	if($(".pre_title label").text()=="编辑店铺业绩目标"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/storeAchvGoal/select";
		oc.postRequire("post", _command,"", _params, function(data){
			// console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				msg=JSON.parse(msg.storeAchvGoal);
				var corp_code=msg.corp_code;//公司编号
				var store_code=msg.store_code;//店铺编号


				$("#PER_GOAL").val(msg.target_amount);
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
				getcorplist(corp_code,store_code);
			}else if(data.code=="-1"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
			}
		});
	}else{
		getcorplist(a,b);
	}
    $(".iShopadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/iShop.html");
	});
	$(".iShopedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/message/iShop.html");
	});
});
function getcorplist(a,b){
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
			$("#OWN_CORP").searchableSelect();
			var c=$('#corp_select .selected').attr("data-value");
			store_data(c,b);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				store_data(c);
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
function store_data(c,b){
	var _params={};
	_params["corp_code"]=c;//企业编号
	var _command="/user/store";//调取店铺的名字
	oc.postRequire("post", _command,"", _params, function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			var msg_stores=JSON.parse(msg.stores);
			$('#SHOP_NAME').empty();
			$('#shop_select .searchable-select').remove();
			if(msg_stores.length>0){
				for(var i=0;i<msg_stores.length;i++){
					$('#SHOP_NAME').append("<option value='"+msg_stores[i].store_code+"'>"+msg_stores[i].store_name+"</option>");
				}
			}else if(msg_stores.length<=0){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "改企业没有店铺"
			    });
			}
			if(b!==""){
				$("#SHOP_NAME option[value='"+b+"']").attr("selected","true")
			}
			$("#SHOP_NAME").searchableSelect();
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	})
}