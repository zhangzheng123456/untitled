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
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var GOODS_CODE=$("#GOODS_CODE").val();//商品编号
				var GOODS_NAME=$("#GOODS_NAME").val();//商品名称
				var GOODS_PRICE=$("#GOODS_PRICE").val();//商品价格
				var GOODS_QUARTER=$("#GOODS_QUARTER").val();//季度
				var GOODS_BAND=$("#GOODS_BAND").val();//波段
				var GOODS_RELEASETIME=$("#GOODS_RELEASETIME").val();//发布时间
				var GOODS_BUYPOINT=$("#GOODS_BUYPOINT").val();//商品卖点
				var ISACTIVE="";//是否可用
				var brand_code=$("#OWN_BRAND").val();//品牌编号
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
						if(img_url_list[i].src.indexOf("http")!==-1){
							img_list.push(img_url_list[i].src);
						}else{
							img_list.push($(img_url_list[i]).attr("data-name"));
						}
					}
					for(var j=0;j<img_list.length;j++){
						img_list_json[j]=img_list[j];
					}
					img_list_json=JSON.stringify(img_list_json);
				}else{
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"商品图片最多可以上传5张!"
					});
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
					"isactive": ISACTIVE
				};
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
				var brand_code=$("#OWN_BRAND").val();//品牌编号
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
				var isexit_flg=[];
				var img_list_json={};
				var img_url_list=$('.good_imgs .parentFileBox .fileBoxUl .diyUploadHover:visible .viewThumb img');
				if(img_url_list.length<=5){
					for(var i=0;i<img_url_list.length;i++){
						if(img_url_list[i].src.indexOf("http")!==-1){
							img_list.push(img_url_list[i].src);
						}else{
							img_list.push($(img_url_list[i]).attr("data-name"));
						}
					}
					for(var j=0;j<img_list.length;j++){
						img_list_json[j]=img_list[j];
					}
					img_list_json=JSON.stringify(img_list_json);
				}else{
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"商品图片最多可以上传5张!"
					});
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
					"isactive": ISACTIVE
				};
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
	var a="";
	var b="";
	if($(".pre_title label").text()=="编辑商品培训(FAB)"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/goods/fab/select";
		var img_html='';
		var a="";
		var b="";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data.message);
			if(data.code=="0"){
				var m=JSON.parse(data.message);
				var msg=JSON.parse(m.goods);
				var goods_img=msg.goods_image;
				var goods_arr=[];
				var filename;//图片名
				if(goods_img.indexOf(',')!==-1){
					goods_arr= goods_img.split(",");
				}else{
					goods_arr.push(goods_img);
				}
				for(var i=0;i<goods_arr.length;i++){
					if(goods_arr[i].indexOf("/")>0)//如果包含有"/"号 从最后一个"/"号+1的位置开始截取字符串
					{
					    filename=goods_arr[i].substring(goods_arr[i].lastIndexOf("/")+1,goods_arr[i].length);
					    img_html +='<li id="fileBox_WU_FILE_'+(i+10)+'" class="diyUploadHover">'
                                   	+'<div class="viewThumb"><img src="'+goods_arr[i]+'"></div>'
                                   	+'<div class="diyCancel" onclick="img_del(this)"></div>'
                                    +'<div class="diySuccess"></div>'
                                    +'<div class="diyFileName">'+filename+'</div>'
                                    +'<div class="diyBar">'
                                        +'<div class="diyProgress"></div>'
                                        +'<div class="diyProgressText">0%</div>'
                                    +'</div>'
                                +'</li>';
					}
				}
				$(".good_imgs .parentFileBox .fileBoxUl").append(img_html);
				var corp_code=msg.corp.corp_code;//公司编号
				var brand_code=msg.brand.brand_code;//品牌编号
				$("#GOODS_CODE").val(msg.goods_code);
				$("#GOODS_CODE").attr("data-name",msg.goods_code);//编辑的时候code区分
				$("#GOODS_NAME").val(msg.goods_name);
				$("#GOODS_NAME").attr("data-name",msg.goods_name);//编辑的时候名称的区分
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
				getcorplist(corp_code,brand_code);
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
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
    $("#GOODS_NAME").blur(function(){
    	var goods_name=$("#GOODS_NAME").val();
    	var goods_name1=$("#GOODS_NAME").attr("data-name");
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();
    	if(goods_name!==""&&goods_name!==goods_name1){
	    	var _params={};
	    	_params["goods_name"]=goods_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/goods/FabNameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#GOODS_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！")
	            	div.addClass("error_tips");
	            	$("#GOODS_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    })
	$(".fabadd_oper_btn ul li:nth-of-type(2").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});
	$(".fabedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/goods/fab.html");
	});

});
// 删除加载的已存在的商品图片
function img_del(obj) {
	$(obj).parent().remove();
}
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
			$('.corp_select select').searchableSelect();
			var c=$("#OWN_CORP").val();//公司编号
			getvarbrandlist(c,b);
			$('.searchable-select-item').click(function(){
				var c=$(this).attr("data-value");
				getvarbrandlist(c,b);
				$("input[verify='Code']").val("");
				$("#BRAND_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#BRAND_NAME").attr("data-mark","");
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
			console.log(data);
			var brands=JSON.parse(data.message);//品牌编号list
			brands=brands.brands;
			console.log(brands);
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
					content: "该企业没有品牌,请先定义品牌！"
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