var oc = new ObjectControl();
(function(root,factory){
	root.vipCardType= factory();
}(this,function(){
	var vipCardTypeJs={};
	vipCardTypeJs.param={//定义参数类型
		"card":true,
		"name":true,
		"vip_type_code":"",
		"vip_type_name":""
	};
	vipCardTypeJs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	vipCardTypeJs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	vipCardTypeJs.checkNumber=function(obj,hint){//检查必须是数字但是可以为空的状态
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"此处仅支持输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return true;
		}
	};
	vipCardTypeJs.checkNumber1=function(obj,hint){//检查必须是数字还要非空的状态
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"此处仅支持输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	}
	vipCardTypeJs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	vipCardTypeJs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	vipCardTypeJs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	vipCardTypeJs.bindbutton=function(){
		var self=this;
		$("#edit_save").click(function(){
			if(vipCardTypeJs.firstStep()){
				if(!self.param.price){
					$("#vip_type").next('.hint').children().html("当前企业下该会员类型已存在！");
					$("#vip_type").next('.hint').children().addClass("error_tips");
					return;
				}
				var param={};
				var corp_code=$("#OWN_CORP").val().trim();//公司编号
				var vip_card_type_code=$("#vip_type_code").val().trim();//会员类型定义编号
				var vip_card_type_name=$("#vip_type_name").val().trim();//会员类型定义名称
				var degree=$("#vip_type_degree").attr("data-value");//会员类型等级
				var input=$("#is_active")[0];//是否可用
				if(input.checked==true){//是否可用
					isactive="Y";
				}else if(input.checked==false){
					isactive="N";
				};
				param["corp_code"]=corp_code;//公司编号
				param["vip_card_type_code"]=vip_card_type_code;//上级会员类型
				param["vip_card_type_name"]=vip_card_type_name;//会员类型
				param["isactive"]=isactive;//是否可用
				var id=sessionStorage.getItem("id");//获取保存的id
				if(id==null){
					var command="/vipCardType/add";
					vipCardTypeJs.ajaxSubmit(command,param);
				}else if(id!==null){
					param["id"]=id;
					var command="/vipCardType/edit";
					vipCardTypeJs.ajaxSubmit(command,param);
				}
			}else{
				return;
			}
		});
		//关闭
		$("#edit_close").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_card_type.html");
		});
		//回到列表
		$("#back_regime").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_card_type.html");
		});
		//获取vip类型
		$("#vip_type").blur(function(){
			var param={};
			var vip_type=$("#vip_type").val();
			param["corp_code"]=$("#OWN_CORP").val();//企业编号
			param["vip_type"]=vip_type;//会员类型
			var div=$(this).next('.hint').children();
			if(vip_type!==""&&vip_type!==self.param.vip_type){
				oc.postRequire("post","/vipRules/vipTypeExist","",param, function(data){
					if(data.code=="0"){
						div.html("");
						self.param.price=true;
					}else if(data.code=="-1"){
						div.addClass("error_tips");
						div.html(data.message);
						self.param.price=false;
					}
				})
			}
		});
	};
	vipCardTypeJs.getInputValue=function(id){//编辑时给input赋值
		var param={};
		var self=this;
		param["id"]=id;
		whir.loading.add("",0.5);
		oc.postRequire("post","/vipCardType/select","",param,function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var corp_code=message.corp_code;
				$("#vip_type_code").val(message.vip_card_type_code);
				$("#vip_type_name").val(message.vip_type_name);
				$("#vip_type_degree").attr("data-value",degree);
				var degree=$("#vip_type_degree").siblings("ul").find("li[data-value='"+message.upgrade_time+"']").text();
				$("#vip_type_degree").val(upgrade_time);//升级门槛时间
				var input=$("#is_active")[0];//是否可用
				self.getcorplist(corp_code);
			}else if(data.code=="-1"){
				alert(data.message);
			}
			whir.loading.remove();//移除加载框
		})
	};
	vipCardTypeJs.getcorplist=function(corp_code){//获取企业列表
		var self=this;
		whir.loading.add("",0.5);
		oc.postRequire("post","/user/getCorpByUser","", "", function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
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
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						if(code!==corp_code1){
							self.param.area_codes="";
    						self.param.area_names="";
    						self.param.brand_codes="";
    						self.param.brand_names="";
    						self.param.store_codes="";
    						self.param.store_names="";
    						self.param.vip_type="";
    						code=corp_code1;
    						$("#shop_list").val("已选0个");
    						$("#vip_type").val("");
    						$("#high_vip_type").val("");
    						$("#quan_select").empty();
    						self.getVipType(code);
						}
					}
				})
				$('.searchable-select-item').click(function(){
					var corp_code1=$("#OWN_CORP").val();
					if(code!==corp_code1){
						self.param.area_codes="";
    					self.param.area_names="";
    					self.param.brand_codes="";
    					self.param.brand_names="";
    					self.param.store_codes="";
    					self.param.store_names="";
    					self.param.vip_type="";
    					code=corp_code1;
    					$("#shop_list").val("已选0个");
    					$("#vip_type").val("");
    					$("#high_vip_type").val("");
    					$("#quan_select").empty();
    					self.getVipType(code);
					}
				})
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
	vipCardTypeJs.ajaxSubmit=function(command,param){//提交接口
		whir.loading.add("",0.5);
		oc.postRequire("post", command,"",param, function(data){
			if(data.code=="0"){
				if(command=="/vipCardType/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/vip/vip_card_type_edit.html");
                }
                if(command=="/vipCardType/edit"){
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
		if(vipCardTypeJs['check' + command]){
			if(!vipCardTypeJs['check' + command].apply(vipCardTypeJs,[obj,hint])){
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
		var id=sessionStorage.getItem("id");
		vipCardTypeJs.bindbutton();
		if(id==null){
			var corp_code="";
			vipCardTypeJs.getcorplist(corp_code);
		}else if(id!==null){
			vipCardTypeJs.getInputValue(id);
		}
	}
	var obj = {};
	obj.vipCardTypeJs = vipCardTypeJs;
	obj.init = init;
	return obj;
}));
$(function(){
	window.regime.init();//初始化
});
