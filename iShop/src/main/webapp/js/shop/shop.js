var oc = new ObjectControl();
(function(root,factory){
	root.shop = factory();
}(this,function(){
	var shopjs={};
	shopjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	shopjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	shopjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	shopjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	shopjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	shopjs.bindbutton=function(){
		$(".shopadd_oper_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#STORE_NAME").attr("data-mark");//店铺名称是否唯一的标志
			var codeMark=$("#STORE_ID").attr("data-mark");//店铺ID是否唯一的标志
			if(shopjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#STORE_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#STORE_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var STORE_ID=$("#STORE_ID").val();
				var STORE_NAME=$("#STORE_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_AREA=$("#OWN_AREA").data("myacode");
				var OWN_BRAND=$("#OWN_BRAND").attr("data-mybcode");
				// var BRAND_ID=$("#BRAND_ID").val();
				// var AREA_ID=$("#AREA_ID").val();
				var is_zhiying=$("#FLG_TOB").val();
				var FLG_TOB="";
				if(is_zhiying=="是"){
					FLG_TOB="Y";
				}else if(is_zhiying=="否"){
					FLG_TOB="N";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				// var SHOP_MANAGER=$("#SHOP_MANAGER").val();
				var _command="/shop/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"corp_code":OWN_CORP,"brand_code":OWN_BRAND,"store_code":STORE_ID,"area_code":OWN_AREA,"store_name":STORE_NAME,"flg_tob":FLG_TOB,"isactive":ISACTIVE};
				shopjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".shopedit_oper_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#STORE_NAME").attr("data-mark");//店铺名称是否唯一的标志
			var codeMark=$("#STORE_ID").attr("data-mark");//店铺ID是否唯一的标志
			if(shopjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#STORE_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#STORE_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				console.log($("#OWN_BRAND").data("mybcode"));
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_AREA=$("#OWN_AREA").data("myacode");
				var OWN_BRAND=$("#OWN_BRAND").attr("data-mybcode");
				var STORE_ID=$("#STORE_ID").val();
				var STORE_NAME=$("#STORE_NAME").val();
				var is_zhiying=$("#FLG_TOB").val();
				var FLG_TOB="";
				if(is_zhiying=="是"){
					FLG_TOB="Y";
				}else if(is_zhiying=="否"){
					FLG_TOB="N";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				// var SHOP_MANAGER=$("#SHOP_MANAGER").val();
				var _command="/shop/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={"id":ID,"corp_code":OWN_CORP,"brand_code":OWN_BRAND,"store_code":STORE_ID,"area_code":OWN_AREA,"store_name":STORE_NAME,"flg_tob":FLG_TOB,"isactive":ISACTIVE};
				shopjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	shopjs.ajaxSubmit=function(_command,_params,opt){
		console.log(_params);
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				$(window.parent.document).find('#iframepage').attr("src","/shop/shop.html");
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
		if(shopjs['check' + command]){
			if(!shopjs['check' + command].apply(shopjs,[obj,hint])){
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
		shopjs.bindbutton();
	}
	var obj = {};
	obj.shopjs = shopjs;
	obj.init = init;
	return obj;
}));
var checknow_data=[];
var checknow_namedata=[];
var flg_index=0;
jQuery(document).ready(function(){
	window.shop.init();//初始化
	if($(".pre_title label").text()=="编辑店铺信息"){
		var id=sessionStorage.getItem("id");
		var _params={"id":id};
		var _command="/shop/select";
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				if(msg.brand_code.indexOf(',')!==-1){
					checknow_data=msg.brand_code.split(",");
					checknow_namedata=msg.brand_name.split(",");
				}else{
					checknow_data.push(msg.brand_code);
					checknow_namedata.push(msg.brand_name);
				}
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);
				$("#OWN_BRAND").val(msg.brand_name);
				$("#OWN_BRAND").attr("data-mybcode",msg.brand_code);
				$("#STORE_NAME").val(msg.store_name);
				$("#STORE_NAME").attr("data-name",msg.store_name);
				$("#STORE_ID").val(msg.store_code);
				$("#STORE_ID").attr("data-name",msg.store_code);
				$("#OWN_AREA").val(msg.area_name);
				$("#OWN_AREA").attr("data-myacode",msg.area_code);
				if(msg.flg_tob=="Y"){
					$("#FLG_TOB").val("是");
				}else if(msg.flg_tob=="N"){
					$("#FLG_TOB").val("否");
				}
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
				getcorplist();
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
		getcorplist();
	}
	$("input[verify='Code']").blur(function(){
    	var _params={};
    	var store_code=$(this).val();//店仓编号
    	var store_code1=$(this).attr("data-name");//标志
    	var corp_code=$("#OWN_CORP").val();//公司编号
		if(store_code!==""&&store_code!==store_code1){
			_params["store_code"]=store_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/shop/Store_CodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#STORE_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#STORE_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    })
    $("#STORE_NAME").blur(function(){
    	var store_name=$("#STORE_NAME").val();//店铺名称
    	var store_name1=$("#STORE_NAME").attr("data-name");//给店铺的名称是一个字
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();
    	if(store_name!==""&&store_name!==store_name1){
	    	var _params={};
	    	_params["store_name"]=store_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/shop/Store_NameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#STORE_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！");
	            	div.addClass("error_tips");
	            	$("#STORE_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    })

	$("#OWN_AREA").click(function(){
		$("#area_select").html('');
		 $(this).parent().children('ul').toggle();
		var area_param={"corp_code":$("#OWN_CORP").val()};
		var area_command="/shop/area";
		oc.postRequire("post", area_command,"",area_param, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				console.log(msg);
				var area_html='';
				var a=null;
				console.log(msg.areas);
				if(msg.areas.length==0){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"该企业目前分配区域！"
					});
				}else{
					for(var i=0; i< msg.areas.length;i++){
						a=msg.areas[i];
						area_html+='<li data-areacode="'+a.area_code+'">'+a.area_name+'</li>';
					}
					$("#area_select").append(area_html);
					$("#area_select li").click(function(){
			            var this_=this;
			            var txt = $(this_).text();
			            var a_code=$(this_).data("areacode");
			            $(this_).parent().parent().children(".input_select").val(txt);
			            $(this_).parent().parent().children(".input_select").attr('data-myacode',a_code);
			            $(this_).addClass('rel').siblings().removeClass('rel');
			            $(this_).parent().toggle();
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
	})
	$(".shopadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/shop/shop.html");
	});
	$(".shopedit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/shop/shop.html");
	});
	$("#OWN_BRAND").click(function(){
       $("#OWN_BRAND").parent().children("#brand_data").toggle();
    })
    $("#OWN_BRAND").parent().children(".down_icon").click(function() {
    	$("#OWN_BRAND").parent().children("#brand_data").toggle();
    });
    var brandname=[];
	$("#brand_data").remove();
	var brand_param={"corp_code":$("#OWN_CORP").val()};
	var brand_command="/shop/brand";
	oc.postRequire("post", brand_command,"",brand_param, function(rdata){
		console.log(rdata);
		if(rdata.code=="0"){
			var msg=JSON.parse(rdata.message);
			console.log(msg);
			// var index=0;
			var brand_html='';
			var b=null;
			for(var j=0;j<msg.brands.length;j++){
				b=msg.brands[j];
				brand_html+='<option value="'+b.brand_code+'">'+b.brand_name+'</option>';
				brandname.push(b.brand_name);
			}
			var checkboxSelect = new CheckboxSelect({
		        input:document.getElementById('OWN_BRAND'),
		        hiddeninput:document.getElementById('hiddencheckboxSelect'),
		        width:420,
		        opacity:1,
		        data:brandname,
			});
		console.log(brandname);
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	});
	$("#OWN_BRAND").click(function(){
		$(".checkboxselect-container").html('');
		var brand_param={"corp_code":$("#OWN_CORP").val()};
		var brand_command="/shop/brand";
		oc.postRequire("post", brand_command,"",brand_param, function(rdata){
			console.log(rdata);
			if(rdata.code=="0"){
				var msg=JSON.parse(rdata.message);
				console.log(msg);
				// var index=0;
				var brand_html='';
				var b=null;
				if(msg.brands.length==0){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"该企业目前没有品牌！"
					});
					$(".checkboxselect-container").css("display","none");
				}else{
					for(var m=0;m<msg.brands.length;m++){
						b=msg.brands[m];
						brand_html+='<div class="checkboxselect-item"><input type="checkbox" value="'+b.brand_code+'" data-brandname="'+b.brand_name+'" style="-webkit-appearance: checkbox; width: 14px; height: 14px;">'+b.brand_name+'</div>';
					}
					$(".checkboxselect-container").html(brand_html);
					var check_input=$('.checkboxselect-container input');
					for(var c=0;c<check_input.length;c++){
						check_input[c].onclick=function(){
							if(this.checked==true){
								checknow_data.push($(this).val())
								checknow_namedata.push($(this).attr("data-brandname"));
								$('#OWN_BRAND').val(checknow_namedata.toString());
								$('#OWN_BRAND').attr('data-mybcode',checknow_data.toString());
							}else if(this.checked==false){
								checknow_namedata.remove($(this).attr("data-brandname"));
								checknow_data.remove($(this).val());
								console.log(checknow_data);
								console.log(checknow_namedata);
								$('#OWN_BRAND').val(checknow_namedata.toString());
								$('#OWN_BRAND').attr('data-mybcode',checknow_data.toString());
							}
						}
					}
					var s=$("#OWN_BRAND").attr("data-mybcode");
					var c_input=$('.checkboxselect-container input');
					var ss='';
					if(s.indexOf(',')!==-1){
						ss = s.split(",");
						for(var i=0;i<ss.length;i++){
							for(var j=0;j<c_input.length;j++){
								if($(c_input[j]).val()==ss[i]){
									$(c_input[j]).attr("checked",true);
								}
							}
						}
					}else{
						ss=s;
						for(var j=0;j<c_input.length;j++){
							if($(c_input[j]).val()==ss){
								$(c_input[j]).attr("checked",true);
							}
						}
					}
					
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
	});

	$(".corp_select").click(function(){
		$("#OWN_AREA").val('');
		$("#OWN_BRAND").val('');
		$('#OWN_BRAND').attr('data-mybcode','');
		flg_index ++;
		checknow_data=[];
		checknow_namedata=[];
	});
});
function getcorplist(){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var corp_html='';
			var c=null;
			for(var i=0;i<msg.corps.length;i++){
				c=msg.corps[i];
				corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			$('.corp_select select').searchableSelect();
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#STORE_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#STORE_NAME").attr("data-mark","");
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
