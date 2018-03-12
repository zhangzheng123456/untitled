var oc = new ObjectControl();
var swip_image = [];
(function(root,factory){
	root.fab = factory();
}(this,function(){
	var fabjs={};
	fabjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	fabjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	fabjs.checkNumber=function(obj,hint){
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
			return true;
		}
	}
	fabjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	fabjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	fabjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	fabjs.bindbutton=function(){
		//新增保存
		$("#add_save").click(function(){
			function getContent() {
				var arr = [];
				arr.push(UE.getEditor('editor').getContent());
				return arr.join("\n");
			}
			function getPlainTxt() {
				var arr = [];
				arr.push(UE.getEditor('editor').getPlainTxt());
				return arr.join("\n");
			}
			var reg = /<img[^>]*>/gi;;
			function imge_change() {
				var  i=0;
				return function img_change(){
					i++;
					return i;
				}
			}
			var img_c=imge_change();
			var nr= getContent().replace(reg,function () {
				var i=img_c();
				return getPlainTxt().match(reg)[i-1];
			});
			if(fabjs.firstStep()){
				var app_help_content= nr;//商品卖点
				var app_help_code=$("#OWN_CORP").val();
				var app_help_title=$("#app_help_title").val();
				var ISACTIVE="";//是否可用
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/relAppHelp/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"app_help_code":app_help_code,
					"app_help_title":app_help_title,
					"app_help_content":app_help_content,
					"isactive": ISACTIVE
				};
				console.log(_params);
				fabjs.ajaxSubmit(_command,_params,opt);

			}else{
				return;
			}
		});
		//编辑保存
		$("#edit_save").click(function(){
			// $('#close_match_goods').trigger("click");
			var delete_image=[];//需要删除的数据
			var load_image=sessionStorage.getItem('goods_description').match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
			var save_image=getContent().match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
			if(load_image!==null&&save_image!==null){
				save_image.forEach(function (val,index,arr) {
					return val.indexOf('/image/upload')!=-1?arr.splice(index,1):arr[index];
				})
				load_image.map(function (val,index,arr) {
					var reg= /src=[\'\"]?([^\'\"]*)[\'\"]?/;
					var test=val.match(reg)[0];
					save_image.join('').indexOf(test)==-1?delete_image.push(val):'';
				})
			}else if(load_image!==null&&save_image==null){
				load_image.forEach(function (val) {
					delete_image.push(val);
				})
			}
			function getContent() {
				var arr = [];
				arr.push(UE.getEditor('editor').getContent());
				return arr.join("\n");
			}
			function getPlainTxt() {
				var arr = [];
				arr.push(UE.getEditor('editor').getPlainTxt());
				return arr.join("\n");
			}
			var reg = /<img[^>]*>/gi;;
			function imge_change() {
				var  i=0;
				return function img_change(){
					i++;
					return i;
				}
			}
			var img_c=imge_change();
			var nr= getContent().replace(reg,function () {
				var i=img_c();
				return getPlainTxt().match(reg)[i-1];
			});
			if(fabjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var app_help_content= nr;//商品卖点
				var app_help_code=$("#OWN_CORP").val();
				var app_help_title=$("#app_help_title").val();
				var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/relAppHelp/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"id": ID,
					"app_help_code":app_help_code,
					"app_help_title":app_help_title,
					"app_help_content":app_help_content,
					"isactive": ISACTIVE,
					'delImgPath':delete_image.join('')
				};
				fabjs.ajaxSubmit(_command,_params,opt);

			}else{
				return;
			}
		});
	};
	fabjs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				if(_command=="/relAppHelp/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/system/help_article_edit.html");
                }
                if(_command=="/relAppHelp/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                    window.location.reload();
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
		if(fabjs['check' + command]){
			if(!fabjs['check' + command].apply(fabjs,[obj,hint])){
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
		fabjs.bindbutton();
	}
	var obj = {};
	obj.fabjs = fabjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.fab.init();//初始化
	var a="";
	var b="";
	if($(".pre_title label").text()=="编辑帮助文章"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length<=0){
					$("#edit_save").hide();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/relAppHelp/select";
		var img_html='';
		var a="";
		var b="";
		whir.loading.add("",0.5);
		oc.postRequire("post", _command,"", _params, function(data){
			console.log('请求回的数据');
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				//将读取到的卖点信息保存在本地
				sessionStorage.setItem('goods_description',msg.app_help_content);
				ue.ready(function() {
					// ue.setContent(msg.goods_description);
					ue.body.innerHTML=msg.app_help_content;

				});
				var app_help_code=msg.app_help_code;
				$("#app_help_title").val(msg.app_help_title);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				var input=$("#is_active")[0];
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
				getAppHelps(app_help_code);
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
		getAppHelps(a,b);
	}
	$("#GOODS_CODE").blur(function(){
    	var _params={};
    	var goods_code=$(this).val();
    	var corp_code=$("#OWN_CORP").val();
    	var goods_code1=$(this).attr("data-name");
		if(goods_code!==""&&goods_code!==goods_code1){
			_params["goods_code"]=goods_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/goods/FabCodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#GOODS_CODE").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#GOODS_CODE").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");
	               }
		    })
		}
    })
	$(".fabadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/help_article.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/help_article.html");
	});
	$("#back_goods_fab").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/system/help_article.html");
	});

});
function getAppHelps(a,b){
	//获取所属企业列表
	var corp_command="/appHelp/getAppHelps";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			msg=JSON.parse(msg.list);
			var help_html='';
			for(i=0;i<msg.length;i++){
				help_html+='<option value="'+msg[i].app_help_code+'">'+msg[i].app_help_name+'</option>';
			}
			//}
			$("#OWN_CORP").append(help_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$("#OWN_CORP").searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){

				}
			})
			$('.searchable-select-item').click(function(){

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
