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
		$("#edit_save").click(function(){
			console.log(asdasd);
			if(regimejs.firstStep()){
				regimejs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_close").click(function(){
			if(regimejs.firstStep()){
				regimejs.ajaxSubmit(_command, _params, opt);
			}else{
				return;
			}
		});
		$("#edit_close").click(function(){
			console.log(12324);
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		});
		$("#back_regime").click(function(){
			console.log(12324);
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		})
	};
	regimejs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/storeAchvGoal/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/achv/shopgoal_edit.html");
                }
                if(_command=="/storeAchvGoal/edit"){
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
	regimejs.getcorplist=function(corp_code){
		oc.postRequire("post","/user/getCorpByUser","", "", function(data){
			if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var index=0;
			var corp_html='';
			for(var i=0;i<msg.corps.length;i++){
				corp_html+='<option value="'+msg.corps[i]corp_code+'">'+msg.corps[i].corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(corp_code!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('.corp_select select').searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					$("#AREA_ID").val("");
					$("#AREA_NAME").val("");
					$("#AREA_ID").attr("data-mark","");
					$("#AREA_NAME").attr("data-mark","");
				}
			})
			$('.searchable-select-item').click(function(){
				$("#AREA_ID").val("");
				$("#AREA_NAME").val("");
				$("#AREA_ID").attr("data-mark","");
				$("#AREA_NAME").attr("data-mark","");
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
		console.log(id);
		regimejs.bindbutton();
		if(id==null){
			var corp_code=="";
			regimejs.getcorplist(corp_code);
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
