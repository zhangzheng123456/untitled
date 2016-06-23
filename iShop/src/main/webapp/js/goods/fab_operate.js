var oc = new ObjectControl();
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
	fabjs.checkPhone = function(obj,hint){
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
		$(".fabadd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(fabjs.firstStep()){
				var OWN_CORP=$("#OWN_CORP").val();
				var GOODS_CODE=$("#GOODS_CODE").val();
				var GOODS_NAME=$("#GOODS_NAME").val();
				var GOODS_PRICE=$("#GOODS_PRICE").val();
				var GOODS_QUARTER=$("#GOODS_QUARTER").val();
				var GOODS_BAND=$("#GOODS_BAND").val();
				var GOODS_RELEASETIME=$("#GOODS_RELEASETIME").val();
				var GOODS_BUYPOINT=$("#GOODS_BUYPOINT").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				/*
				获取上传的图片地址
				 */
				var img_list=[];
				var img_list_json={};
				var img_url_list=$('.good_imgs .parentFileBox .fileBoxUl .diyUploadHover:visible .viewThumb img');
				if(img_url_list.length<=5){
					for(var i=0;i<img_url_list.length;i++){
						img_list.push("http://goods-image.oss-cn-hangzhou.aliyuncs.com//goods-images/"+$("#GOODS_CODE").val()+"_"+i+".jpg");
					}
					for(var j=0;j<img_list.length;j++){
						img_list_json[j]=img_list[j];
					}
					img_list_json=JSON.stringify(img_list_json);
				}else{
					alert("商品图片最多可以上传5张");
				}
				var _command="/goods/fab/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_code":OWN_CORP,"goods_code":GOODS_CODE,"goods_name":GOODS_NAME,"goods_price":GOODS_PRICE,"goods_image":img_list_json,"goods_quarter":GOODS_QUARTER,"goods_wave":GOODS_BAND,"goods_time":GOODS_RELEASETIME,"goods_description":GOODS_BUYPOINT,"isactive":ISACTIVE};
				fabjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".fabedit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(fabjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var GOODS_CODE=$("#GOODS_CODE").val();
				var GOODS_NAME=$("#GOODS_NAME").val();
				var GOODS_PRICE=$("#GOODS_PRICE").val();
				var GOODS_QUARTER=$("#GOODS_QUARTER").val();
				var GOODS_BAND=$("#GOODS_BAND").val();
				var GOODS_RELEASETIME=$("#GOODS_RELEASETIME").val();
				var GOODS_BUYPOINT=$("#GOODS_BUYPOINT").val();
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/goods/fab/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"goods_code":GOODS_CODE,"goods_name":GOODS_NAME,"goods_price":GOODS_PRICE,"goods_image":"","goods_quarter":GOODS_QUARTER,"goods_wave":GOODS_BAND,"goods_time":GOODS_RELEASETIME,"goods_description":GOODS_BUYPOINT,"isactive":ISACTIVE};
				fabjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	fabjs.ajaxSubmit=function(_command,_params,opt){
		console.log(_params);
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
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
	if($(".pre_title label").text()=="编辑商品培训(FAB)"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/goods/fab/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data.message);
			if(data.code=="0"){
				var m=JSON.parse(data.message);
				var msg=JSON.parse(m.goods);
				var img_html='';
				var goods_img=JSON.parse(msg.goods_image);
				for(var i=0;i<goods_img.length;i++){
					img_html +='<li id="fileBox_WU_FILE_10" class="diyUploadHover">'
                                   	+'<div class="viewThumb"><img src="'+goods_img[i]+'"></div>'
                                   	+'<div class="diyCancel"></div>'
                                    +'<div class="diySuccess"></div>'
                                    +'<div class="diyFileName"></div>'
                                    +'<div class="diyBar">'
                                        +'<div class="diyProgress"></div>'
                                        +'<div class="diyProgressText">0%</div>'
                                    +'</div>'
                                +'</li>';
				}
				$(".fileBoxUl").append(img_html);
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);
				$("#GOODS_CODE").val(msg.goods_code);
				$("#GOODS_NAME").val(msg.goods_name);
				$("#GOODS_PRICE").val(msg.goods_price);
				$("#GOODS_QUARTER").val(msg.goods_quarter);
				$("#GOODS_BAND").val(msg.goods_wave);
				$("#GOODS_RELEASETIME").val(msg.goods_time);
				$("#GOODS_BUYPOINT").val(msg.goods_description);

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
			$('.corp_select select').searchableSelect();
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	});
	$(".fabadd_oper_btn ul li:nth-of-type(2").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});
	$(".fabedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});

});
