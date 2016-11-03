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
			return false;
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
		$(".fabadd_oper_btn ul li:nth-of-type(1)").click(function(){
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
			console.log(nr);
			if(fabjs.firstStep()){
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var GOODS_CODE=$("#GOODS_CODE").val().trim();//商品编号
				var GOODS_NAME=$("#GOODS_NAME").val();//商品名称
				var GOODS_PRICE=$("#GOODS_PRICE").val();//商品价格
				var GOODS_QUARTER=$("#GOODS_QUARTER").val();//季度
				var GOODS_BAND=$("#GOODS_BAND").val();//波段
				var GOODS_RELEASETIME=$("#GOODS_RELEASETIME").val();//发布时间
				var GOODS_BUYPOINT= nr;//商品卖点
				var ISACTIVE="";//是否可用
				var brand_code=$("#OWN_BRAND").val();//品牌编号
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				if(brand_code==""||brand_code==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"品牌不能为空"
					});
					return;
				}
				if(GOODS_RELEASETIME==""){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"发布时间不能为空"
					});
					return;
				}
				/*
				获取上传的图片地址
				 */
				// var img_list=[];
				var img_list_json=[];
				var img_url_list=$('.good_imgs .parentFileBox .fileBoxUl .diyUploadHover:visible .viewThumb img');
				if(img_url_list.length<=20){
					// for(var i=0;i<img_url_list.length;i++){
					// 	if(i<img_url_list.length-1){
					// 		if(img_url_list[i].src.indexOf("http")!==-1){
					// 			img_list_json+=img_url_list[i].src+",";
					// 			// img_list.push(img_url_list[i].src);
					// 		}else{
					// 			img_list_json+=$(img_url_list[i]).attr("data-name")+",";
					// 			// img_list.push($(img_url_list[i]).attr("data-name"));
					// 		}
					// 	}else {
					// 		if(img_url_list[i].src.indexOf("http")!==-1){
					// 			img_list_json+=img_url_list[i].src;
					// 			// img_list.push(img_url_list[i].src);
					// 		}else{
					// 			img_list_json+=$(img_url_list[i]).attr("data-name");
					// 			// img_list.push($(img_url_list[i]).attr("data-name"));
					// 		}
					// 	}
					// }
					// for(var j=0;j<img_list.length;j++){
					// 	img_list_json[j]=img_list[j];
					// }
					// img_list_json=JSON.stringify(img_list_json);
					console.log(img_url_list.length);
					for(var i=0;i<img_url_list.length;i++){
						var img_list_json_sub={};
						img_list_json_sub.image=$(img_url_list[i]).attr('src');
						console.log($(img_url_list[i]).parent().parent().find('input').attr('checked'));
						img_list_json_sub.is_public=$(img_url_list[i]).parent().parent().find('input').attr('checked')?'Y':'N'
						img_list_json.push(img_list_json_sub);
					}
				}else{
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"商品图片最多可以上传20张"
					});
				}
				var li=$(".match_goods ul").find("li");
				for(var i=0,matchgoods="";i<li.length;i++){
					var r=$(li[i]).attr("id");
					if(i<li.length-1){
						matchgoods+=r+",";
					}else{
						matchgoods+=r;
					}
				}
				var _command="/goods/fab/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"corp_code": OWN_CORP,
					"goods_code": GOODS_CODE,
					"goods_name": GOODS_NAME,
					"brand_code":brand_code,
					"goods_price": GOODS_PRICE,
					"goods_image": img_list_json,
					"goods_quarter": GOODS_QUARTER,
					"goods_wave": GOODS_BAND,
					"goods_time": GOODS_RELEASETIME,
					"goods_description": GOODS_BUYPOINT,
					"isactive": ISACTIVE,
					"match_goods":matchgoods
				};
				console.log(_params);
				fabjs.ajaxSubmit(_command,_params,opt);

			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			// $('#close_match_goods').trigger("click");
			var delete_image=[];//需要删除的数据
			console.log(sessionStorage.getItem('goods_description'));
			console.log(getContent()==sessionStorage.getItem('goods_description'))
			console.log('抓取图片');
			var load_image=sessionStorage.getItem('goods_description').match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
			var save_image=getContent().match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
			// console.log('加载时的图片'+sessionStorage.getItem('goods_description').match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig));
			// console.log('保存时的图片'+getContent().match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig));
			if(load_image==!null){
				for(var i=0;i<load_image.length;i++){
					load_image[i]=save_image[i]?"":delete_image.push(load_image[i]);
				}
			}
			//如果有内容则进行比较
			// function getContent() {
			// 	var arr = [];
			// 	arr.push(UE.getEditor('editor').getPlainTxt());
			// 	return arr.join("\n");
			// }
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
			// var nr= getContent().replace(reg,getPlainTxt().match(reg));

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
			console.log(nr);
			if(fabjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var GOODS_CODE=$("#GOODS_CODE").val().trim();
				var GOODS_NAME=$("#GOODS_NAME").val();
				var GOODS_PRICE=$("#GOODS_PRICE").val();
				var GOODS_QUARTER=$("#GOODS_QUARTER").val();
				var GOODS_BAND=$("#GOODS_BAND").val();
				var GOODS_RELEASETIME=$("#GOODS_RELEASETIME").val();
				var GOODS_BUYPOINT=nr;
				var brand_code=$("#OWN_BRAND").val();//品牌编号
				var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				if(brand_code==""||brand_code==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"品牌不能为空"
					});
					return;
				}
				if(GOODS_RELEASETIME==""){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"发布时间不能为空"
					});
					return;
				}
				/*
				获取上传的图片地址
				 */
				// var img_list=[];
				// var isexit_flg=[];
				var img_list_json=[];
				var img_url_list=$('.good_imgs .parentFileBox .fileBoxUl .diyUploadHover:visible .viewThumb img');
				// console.log(img_url_list);
				if(img_url_list.length<=20){
					// for(var i=0;i<img_url_list.length;i++){
					// 	var img_list_json_sub={};
					// 	if(i<img_url_list.length-1){
					// 		if(img_url_list[i].src.indexOf("http")!==-1){
					// 			img_list_json+=img_url_list[i].src+",";
					// 			// img_list.push(img_url_list[i].src);
					// 		}else{
					// 			img_list_json+=$(img_url_list[i]).attr("data-name")+",";
					// 			// img_list.push($(img_url_list[i]).attr("data-name"));
					// 		}
					// 	}else {
					// 		if(img_url_list[i].src.indexOf("http")!==-1){
					// 			img_list_json+=img_url_list[i].src;
					// 			// img_list.push(img_url_list[i].src);
					// 		}else{
					// 			img_list_json+=$(img_url_list[i]).attr("data-name");
					// 			// img_list.push($(img_url_list[i]).attr("data-name"));
					// 		}
					// 	}
					// }
					// for(var j=0;j<img_list.length;j++){
					// 	img_list_json[j]=img_list[j];
					// }
					// img_list_json=JSON.stringify(img_list_json);
                    console.log(img_url_list.length);
					for(var i=0;i<img_url_list.length;i++){
						var img_list_json_sub={};
						img_list_json_sub.image=$(img_url_list[i]).attr('src');
						console.log($(img_url_list[i]).parent().parent().find('input').attr('checked'));
						img_list_json_sub.is_public=$(img_url_list[i]).parent().parent().find('input').attr('checked')?'Y':'N'
						img_list_json.push(img_list_json_sub);
					}
				}else{
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"商品图片最多可以上传20张"
					});
				}
				console.log(img_list_json);
				var li=$(".match_goods ul").find("li");
				for(var i=0,matchgoods="";i<li.length;i++){
					var r=$(li[i]).attr("id");
					if(i<li.length-1){
						matchgoods+=r+",";
					}else{
						matchgoods+=r;
					}
				}
				var _command="/goods/fab/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"goods_code": GOODS_CODE,
					"goods_name": GOODS_NAME,
					"goods_price": GOODS_PRICE,
					"brand_code":brand_code,
					"goods_image": img_list_json,
					"goods_quarter": GOODS_QUARTER,
					"goods_wave": GOODS_BAND,
					"goods_time": GOODS_RELEASETIME,
					"goods_description": GOODS_BUYPOINT,
					"isactive": ISACTIVE,
					"match_goods":matchgoods,
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
				if(_command=="/goods/fab/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/goods/fab_edit.html");
                }
                if(_command=="/goods/fab/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                    window.location.reload();
                }
				// if(role=='add'){
				// 	$(".fabadd_oper_btn ul li:nth-of-type(2)").trigger('click');
				// }else if(role=='edit'){
				// 	
				// }
				// $(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
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
	if($(".pre_title label").text()=="编辑商品培训(FAB)"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			var data=JSON.parse(data);
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length<=0){
					$("#edit_save").hide();
				}
			}
		});
		var _params={"id":id};
		var _command="/goods/fab/select";
		var img_html='';
		var a="";
		var b="";
		whir.loading.add("",0.5);
		oc.postRequire("post", _command,"", _params, function(data){
			console.log('请求回的数据');
			if(data.code=="0"){
				var m=JSON.parse(data.message);
				var msg=JSON.parse(m.goods);
				console.log(msg.goods_description);
				//将读取到的卖点信息保存在本地
				sessionStorage.setItem('goods_description',msg.goods_description)
				var goods_arr=msg.goods_image==''?'':JSON.parse(msg.goods_image);
				ue.ready(function() {
					// ue.setContent(msg.goods_description);
					ue.body.innerHTML=msg.goods_description;
				});
				console.log(goods_arr);
				// var goods_arr=[];
				// var filename;//图片名
				// if(goods_img.indexOf(',')!==-1){
				// 	goods_arr= goods_img.split(",");
				// }else{
				// 	goods_arr.push(goods_img);
				// }
				for(var i=0;i<goods_arr.length;i++){
					if(goods_arr[i].image.indexOf('http')==-1)continue;
					// if(goods_arr[i].indexOf("/")>0)//如果包含有"/"号 从最后一个"/"号+1的位置开始截取字符串
					// {
					//     filename=goods_arr[i].substring(goods_arr[i].lastIndexOf("/")+1,goods_arr[i].length);
					var check__or='';
					if(goods_arr[i].is_public=='N'){
						check__or='<em style="position: absolute; top: 0px;left: -9px;">'
							+'<input style="width: 30px;margin:0px" type="checkbox" value="" name="test" class="check">'
							+'<label onclick="public_click(this)"class="check_public"></label>';
					}else{
						check__or='<em class="checkbox_isactive" style="position: absolute; top: 0px;left: -9px;">'
							+'<input style="width: 30px;margin:0px"checked type="checkbox" value="" name="test" class="check">'
							+'<label onclick="public_click(this)"class="check_public"></label>';
					}
					    img_html +='<li id="fileBox_WU_FILE_'+(i+10)+'" class="diyUploadHover"style="height: 90px">'
                                   	+'<div class="viewThumb"><img src="'+goods_arr[i].image+'"></div>'
                                   	+'<div class="diyCancel"></div>'
                                    +'<div class="diySuccess"></div>'
                                    // +'<div class="diyFileName">'+filename+'</div>'
                                    +'<div class="diyBar">'
                                        +'<div class="diyProgress"></div>'
                                        +'<div class="diyProgressText">0%</div>'
                                    +'</div>'
							+'<span style="position: absolute;top: 60px;width: 60px;padding-right:4px;margin-top: 2px">'
				+'<label>公开</label>'
				// +'<em class="checkbox_isactive"style="position: absolute; top: 0px;left: -9px;">'
				// +'<input style="width: 30px;margin:0px" checked type="checkbox" value="" name="test" class="check">'
				// +'<label onclick="public_click(this)"class="check_public"></label>'
				+check__or
				+'</em>'
				+'</span>'
						+	'</li>';
					// }
				}
				$(".good_imgs .parentFileBox .fileBoxUl").prepend(img_html);
				var corp_code=msg.corp_code;//公司编号
				var brand_code=msg.brand_code;//品牌编号
				$("#GOODS_CODE").val(msg.goods_code);
				$("#GOODS_CODE").attr("data-name",msg.goods_code);//编辑的时候code区分
				$("#GOODS_NAME").val(msg.goods_name);
				$("#GOODS_NAME").attr("data-name",msg.goods_name);//编辑的时候名称的区分
				$("#GOODS_PRICE").val(msg.goods_price);
				$("#GOODS_QUARTER").val(msg.goods_quarter);
				$("#GOODS_BAND").val(msg.goods_wave);
				$("#GOODS_RELEASETIME").val(msg.goods_time);
				$("#edit .froala-element").html(msg.goods_description);
				var list=msg.matchgoods;
				for(var i=0;i<list.length;i++){
					jQuery('.match_goods ul').append('<li id="'+list[i].goods_code+'"><img class="goodsImg" src="'
						+ list[i].goods_image
						+ '"><span class="goods_code">'
						+ list[i].goods_code + '</span><span>'
						+ list[i].goods_name
						+'</span><i class="icon-ishop_6-12"></i></li>');
				}
				$("#match_goods ul li i").click(function (){
					$(this).parent("li").remove();
				});
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
				getcorplist(corp_code,brand_code);
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
		getcorplist(a,b);
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
    // $("#GOODS_NAME").blur(function(){
    // 	var goods_name=$("#GOODS_NAME").val();
    // 	var goods_name1=$("#GOODS_NAME").attr("data-name");
    // 	var div=$(this).next('.hint').children();
    // 	var corp_code=$("#OWN_CORP").val();
    // 	if(goods_name!==""&&goods_name!==goods_name1){
	   //  	var _params={};
	   //  	_params["goods_name"]=goods_name;
	   //  	_params["corp_code"]=corp_code;
	   //  	oc.postRequire("post","/goods/FabNameExist","", _params, function(data){
	   //          if(data.code=="0"){
	   //          	div.html("");
	   //          	$("#GOODS_NAME").attr("data-mark","Y");
	   //          }else if(data.code=="-1"){
	   //          	div.html("该名称已经存在！")
	   //          	div.addClass("error_tips");
	   //          	$("#GOODS_NAME").attr("data-mark","N");
	   //          }
	   //  	})
	   //  }
    // })
	$(".fabadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});
	$("#back_goods_fab").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});

});
// 删除加载的已存在的商品图片
// function img_del(obj) {
// 	$(obj).parent().remove();
// }
var num=1;
var next=false;
function getcorplist(a,b){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
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
			var c=$("#OWN_CORP").val();//公司编号
			getvarbrandlist(c,b);
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					var c=$("#OWN_CORP").val();//公司编号
					getvarbrandlist(c,b);
					$("#GOODS_CODE").val("");
					$("#GOODS_CODE").attr("data-mark","");
					// $(".good_imgs .parentFileBox .fileBoxUl").empty();
					$(".good_imgs .parentFileBox .fileBoxUl li:not('li.add_li')").remove();
					$("#search_match_goods ul").empty();
					$("#search").empty();
					$(".match_goods ul").empty();

				}
			})
			$('.searchable-select-item').click(function(){
				var c=$(this).attr("data-value");
				getvarbrandlist(c,b);
				$("#GOODS_CODE").val("");
				$("#GOODS_CODE").attr("data-mark","");
				// $(".good_imgs .parentFileBox .fileBoxUl").empty();
				$(".good_imgs .parentFileBox .fileBoxUl li:not('li.add_li')").remove();
				$("#search_match_goods ul").empty();
				$("#search").empty();
				$(".match_goods ul").empty();
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
function getvarbrandlist(c,d){
	var _params={};
	_params["corp_code"]=c;//公司编号
	var brand_command="/shop/brand";
	oc.postRequire("post", brand_command,"",_params,function(data){
		if(data.code=="0"){
			var brands=JSON.parse(data.message);//品牌编号list
			brands=brands.brands;
			var brand_html="";
			$('#brand_select .searchable-select').remove();//删除
			$('#OWN_BRAND').empty();//清空
			if(brands.length>0){
				for(var i=0;i<brands.length;i++){
					brand_html+='<option value="'+brands[i].brand_code+'">'+brands[i].brand_name+'</option>';
				}
			}else if(brands.length<=0){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "该企业没有品牌,请先定义品牌"
				});
			}
			$('#OWN_BRAND').html(brand_html);
			if(d!==""){
				$("#OWN_BRAND option[value='"+d+"']").attr("selected","true");
			}
			$('#OWN_BRAND').searchableSelect();
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
// function goodsAddHide() {
//加号添加商品
	$("#search_match_goods ul").on("click",".goods_add",function () {
		$(this).hide();
		$(this).next().show();
		$(this).parent("#search_match_goods ul li").css("background","#cde6e8");
		var li=$(this).parent("li").html();
		var goods_code=$(this).parent().find(".goods_code").html();
		var goods_code2=$("#GOODS_CODE").val();
		var len=$(".match_goods ul li").length;
		if(goods_code==$("#"+goods_code).attr("id")|| goods_code==goods_code2){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "请勿重复添加"
			});
		}else if(len>9){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "最多添加十个"
			});
		}
		else  {
			$(".match_goods ul").append('<li id="'+goods_code+'">'+li+'</li>');
		}

		$(".match_goods ul li i").click(function () {
			$(this).parent("li").remove();
		})
	})
//叉号取消添加商品
	$("#search_match_goods ul").on("click","li i",function () {
		$(this).prev().show();
		$(this).hide();
		$(this).parent("#search_match_goods ul li").css("background","");
		var goods_code=$(this).parent().find(".goods_code").html();
		$("#"+goods_code).remove();
	})
// }
//删除图片
$(".good_imgs").on("click",".diyCancel",function(){
	$(this).parent().remove();
})
function getmatchgoodsList(a) {
	//获取相关商品搭配列表
	var param={};
	var corp_code=$("#OWN_CORP").val();
	var searchValue=$("#search").val();
	var goods_code=$("#GOODS_CODE").val();
	var pageNumber=a;
	var pageSize=20;
	param["corp_code"]=corp_code;
	param["goods_code"]=goods_code;
	param["pageNumber"] =pageNumber;
    param["pageSize"] =pageSize;
	param["searchValue"]=searchValue;
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", "/goods/matchGoodsList","",param, function(data){
		if(data.code=="0"){
			console.log(data);
			var msg=JSON.parse(data.message);
			var list=JSON.parse(msg.list);
			var hasNextPage=list.hasNextPage;
			var list=list.list;
			console.log(list);
			if(list.length<=0){
				jQuery('#search_match_goods ul').append("<p>没有相关商品了</p>");
			}else{
				for(var i=0;i<list.length;i++){
					jQuery('#search_match_goods ul').append('<li><img class="goodsImg" src="'
						+ list[i].goods_image
						+ '"><span class="goods_code">'
						+ list[i].goods_code + '</span><span>'
						+ list[i].goods_name + '</span><span class="goods_add">'
						+'+</span><i class="icon-ishop_6-12"></i></li>');
				}
			}
			if(hasNextPage==true){
				num++;
				a++;
				next=false;
			}
			if(hasNextPage==false){
				next=true;
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
		// goodsAddHide();
	});
}
$("#search_match_goods ul").scroll(function () {
	var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight=$(this).height();
    
    if(nScrollTop + nDivHight >= nScrollHight){
    	if(next){
    		return;
    	}
    	getmatchgoodsList(num);
    };
})
//点击添加匹配商品弹窗
$("#add").click(function () {
	$("#goods_box").show();
	$("#search_match_goods").show();
	var corp_code = $('#OWN_CORP').val();
	var corp_code1=$('#OWN_CORP').attr("corp_code");
	if(corp_code==corp_code1){
		return;
	}
	$('#OWN_CORP').attr("corp_code",corp_code);
	var num=1;
	next=false;
	getmatchgoodsList(num);
})
//关闭搜索匹配商品弹窗
$("#close_match_goods").click(function () {
	$("#goods_box").hide();
	$("#search_match_goods").hide();
})

//搜索相关商品
$("#d_search").click(function () {
	jQuery('#search_match_goods ul').empty();
	num=1;
	getmatchgoodsList(num);
})
$("#search_match_goods").keydown(function () {
	var event=window.event||arguments[0];
	if(event.keyCode == 13){
		jQuery('#search_match_goods ul').empty();
		num=1;
		getmatchgoodsList(num);
	}
})
//publick_click   class="checkbox_isactive"
function public_click(a) {
	if($(a).prev().attr('checked')){
		$(a).prev().removeAttr('checked');
		$(a).parent().addClass("checkbox_isactive");
		$(a).addClass('check_public');
	}else{
		$(a).prev()[0].checked='true';
		$(a).parent().addClass("checkbox_isactive");
		$(a).prev().attr('checked','true')
	}
}
//

