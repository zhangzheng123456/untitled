var oc = new ObjectControl();
(function(root,factory){
	root.regime= factory();
}(this,function(){
	var regimejs={};
	regimejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	regimejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	regimejs.checkNumber=function(obj,hint){
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"请输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	}
	regimejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	regimejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	regimejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	regimejs.bindbutton=function(){
		var self=this;
		$("#edit_save").click(function(){
			if(regimejs.firstStep()){
				var param={};
				var corp_code=$("#OWN_CORP").val();//公司编号
				var vip_type=$("#vip_type").val();//会员类型
				var high_vip_type=$("#high_vip_type").val();//上级会员类型
				var discount=$("#discount").val();//会员折扣
				var join_threshold=$("#join_threshold").val();//招募门槛
				var upgrade_time=$("#upgrade_time").val();//升级门槛时间
				var upgrade_amount=$("#upgrade_amount").val();//升级门槛金额
				var points_value=$("#points_value").val();//积分比例
				var present_point=$("#present_point").val();//送积分
				var present_coupon="";//券
				param["corp_code"]=corp_code;//公司编号
				param["vip_type"]=vip_type;//会员类型
				param["high_vip_type"]=high_vip_type;//上级会员类型
				param["discount"]=discount;//会员折扣
				param["join_threshold"]=join_threshold;//招募门槛
				param["upgrade_time"]=upgrade_time;//升级门槛时间
				param["upgrade_amount"]=upgrade_amount;//升级门槛金额
				param["points_value"]=points_value;//积分比例
				param["present_point"]=present_point;//送积分
				param["present_coupon"]=present_coupon;//券
				var id=sessionStorage.getItem("id");
				if(id==null){
					var command="/vipRules/add";
					regimejs.ajaxSubmit(command,param);
				}else if(id!==null){
					param["id"]=id;
					var command="/vipRules/edit";
					regimejs.ajaxSubmit(command,param);
				}
			}else{
				return;
			}
		});
		//关闭
		$("#edit_close").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		});
		//回到列表
		$("#back_regime").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		});
		$("#add_quan").click(function(){
			var corp_code=$("#OWN_CORP").val();
			self.getQuanList(corp_code);
		})
		$("#quan_select").on("click",".q_remove",function(){
			$(this).parent().remove();
		})
		$(".item_1").on("click","ul li",function(){
			var txt = $(this).text();
			$(".item_1 .input_select").val(txt);
			$(".item_1 ul").hide();
		});
		$(".item_2").on("click","ul li",function(){
			var txt = $(this).text();
			$(this).parents(".item_2").find(".input_select").val(txt);
			var value = $(this).attr("rel");
			$(".item_2 ul").hide();
		})
	};
	regimejs.ajaxSubmit=function(command,param){
		oc.postRequire("post", command,"",param, function(data){
			if(data.code=="0"){
				if(command=="/vipRules/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/achv/shopgoal_edit.html");
                }
                if(command=="/vipRules/edit"){
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
		if(regimejs['check' + command]){
			if(!regimejs['check' + command].apply(regimejs,[obj,hint])){
				return false;
			}
		}
		return true;
	};
	regimejs.getQuanList=function(corp_code){
		var param={};
		param["corp_code"]=corp_code;
		oc.postRequire("post","/vipRules/getCoupon","",param, function(data){
			var list=JSON.parse(data.message);
			console.log(list);
			var html="<div class='quan_select item_2'><input type='text' class='input_select quan' class='present_coupon' maxlength='50'/><ul style='display:none'>"
			for(var i=0;i<list.length;i++){
				html+="<li data-couponcode='"+list[i].couponcode+"'>"+list[i].name+"\("+list[i].user_code+"\)</li>";
			}
			html+="</ul><span class='icon-ishop_6-12 q_remove'></span></div>"
			$("#quan_select").append(html);
		})
	}
	regimejs.getInputValue=function(id){//编辑时给input赋值
		var param={};
		var self=this;
		param["id"]=id;
		oc.postRequire("post","/vipRules/select","",param,function(data){
			var message=JSON.parse(data.message);
			console.log(message);
			var corp_code=message.corp_code;
			$("#vip_type").val(message.vip_type);//会员类型
			$("#high_vip_type").val(message.high_vip_type);//上级会员类型
			$("#discount").val(message.discount);//会员折扣
			$("#join_threshold").val(message.join_threshold);//招募门槛
			$("#upgrade_time").val(message.upgrade_time);//升级门槛时间
			$("#upgrade_amount").val(message.upgrade_amount);//升级门槛金额
			$("#points_value").val(message.points_value);//积分比例
			$("#present_point").val(message.present_point);//送积分
			var points_value=message.present_coupon;//券
			self.getcorplist(corp_code);
		})
	};
	regimejs.getcorplist=function(corp_code){//获取企业列表
		var self=this;
		oc.postRequire("post","/user/getCorpByUser","", "", function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				var index=0;
				var corp_html='';
				for(var i=0;i<msg.corps.length;i++){
					corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
				}
				$("#OWN_CORP").append(corp_html);
				if(corp_code!==""){
					$("#OWN_CORP option[value='"+corp_code+"']").attr("selected","true");
				}
				$('.corp_select select').searchableSelect();
				var code=$("#OWN_CORP").val();
				self.getVipType(code);
				$('.searchable-select-item').click(function(){
					self.getVipType($(this).attr("data-value"));
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
	};
	regimejs.getVipType=function(corp_code){
		var param={};
		param["corp_code"]=corp_code;
		oc.postRequire("post","/vipRules/getVipTypes","",param, function(data){
			var list=JSON.parse(data.message).list;
			var list=JSON.parse(list);
			var html="<li>无上级会员类型</li>";
			for(var i=0;i<list.length;i++){
				html+="<li>"+list[i].vip_type+"</li>";
			}
			$("#high_vip_type").siblings("ul").html(html);
		})
	}
	jQuery(":text").focus(function() {
		var _this = this;
		interval = setInterval(function() {
			bindFun(_this);
		}, 500);
	}).blur(function(event) {
		clearInterval(interval);
	});
	var init=function(){
		var id=sessionStorage.getItem("id");
		regimejs.bindbutton();
		if(id==null){
			var corp_code="";
			regimejs.getcorplist(corp_code);
		}else if(id!==null){
			regimejs.getInputValue(id);
		}
	}
	var obj = {};
	obj.regimejs = regimejs;
	obj.init = init;
	return obj;
}));
$(function(){
	window.regime.init();//初始化
	console.log(123123);
});
