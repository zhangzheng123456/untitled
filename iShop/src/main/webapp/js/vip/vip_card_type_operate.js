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
				// if(!self.param.card){
				// 	$("#vip_type").next('.hint').children().html("当前企业下该会员类型已存在！");
				// 	$("#vip_type").next('.hint').children().addClass("error_tips");
				// 	return;
				// }
				// if(!self.param.name){
				// 	$("#vip_type").next('.hint').children().html("当前企业下该会员类型已存在！");
				// 	$("#vip_type").next('.hint').children().addClass("error_tips");
				// 	return;
				// }
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
				param["degree"]=degree;
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
		//验证会员定义编号是否唯一
		$("#vip_type_code").blur(function(){
			var param={};
			var vip_type_code=$("#vip_type_code").val();
			param["corp_code"]=$("#OWN_CORP").val();//企业编号
			param["vip_type_code"]=vip_type_code;//会员类型
			var div=$(this).next('.hint').children();
			if(vip_type_code!==""&&vip_type_code!==self.param.vip_type_code){
				oc.postRequire("post","/vipCardType/vipCardTypeCodeExist","",param, function(data){
					if(data.code=="0"){
						div.html("");
						self.param.code=true;
					}else if(data.code=="-1"){
						div.addClass("error_tips");
						div.html(data.message);
						self.param.code=false;
					}
				})
			}
		});
		//验证会员定义编号是否唯一
		$("#vip_type_name").blur(function(){
			var param={};
			var vip_type_name=$("#vip_type_name").val();
			param["corp_code"]=$("#OWN_CORP").val();//企业编号
			param["vip_type_name"]=vip_type_name;//会员类型
			var div=$(this).next('.hint').children();
			if(vip_type_name!==""&&vip_type_name!==self.param.vip_type_name){
				oc.postRequire("post","/vipCardType/vipCardTypeNameExist","",param, function(data){
					if(data.code=="0"){
						div.html("");
						self.param.name=true;
					}else if(data.code=="-1"){
						div.addClass("error_tips");
						div.html(data.message);
						self.param.name=false;
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
			console.log(data);
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var corp_code=message.corp_code;
				$("#vip_type_code").val(message.vip_card_type_code);
				$("#vip_type_name").val(message.vip_card_type_name);
				$("#vip_type_degree").attr("data-value",message.degree);
				$("#created_time").val(message.created_date);//创建时间
				$("#creator").val(message.creater);//创建人
				$("#modify_time").val(message.modified_date);//修改人
				$("#modifier").val(message.modifier);//修改时间
				self.param.vip_type_code=message.vip_card_type_code;
    		    self.param.vip_type_name=message.vip_card_type_name;
				var degree=$("#vip_type_degree").siblings("ul").find("li[data-value='"+message.degree+"']").text();
				$("#vip_type_degree").val(degree);//
				var input=$("#is_active")[0];//是否可用
				//是否可用
				if(message.isactive=="Y"){
					input.checked=true;
				}else if(message.isactive=="N"){
					input.checked=false;
				};
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
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						if(code!==corp_code1){
							self.param.vip_type_code="";
    						self.param.vip_type_name="";
    						code=corp_code1;
						}
					}
				})
				$('.searchable-select-item').click(function(){
					var corp_code1=$("#OWN_CORP").val();
					if(code!==corp_code1){
						self.param.vip_type_code="";
    				    self.param.vip_type_name="";
    					code=corp_code1;
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
	window.vipCardType.init();//初始化
});
