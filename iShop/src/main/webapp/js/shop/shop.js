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
	shopjs.checkPhone = function(obj,hint){
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
			if(shopjs.firstStep()){
				var STORE_ID=$("#STORE_ID").val();
				var STORE_NAME=$("#STORE_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_AREA=$("#OWN_AREA").data("myacode");
				var OWN_BRAND=$("#OWN_BRAND").data("mybcode");
				// var BRAND_ID=$("#BRAND_ID").val();
				// var AREA_ID=$("#AREA_ID").val();
				var is_zhiying=$("#FLG_TOB").val();
				var FLG_TOB="";
				if(is_zhiying=="是"){
					FLG_TOB="Y";
				}else if(is_zhiying=="否"){
					FLG_TOB="Y";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==true){
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
			if(shopjs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();
				var OWN_AREA=$("#OWN_AREA").data("myacode");
				var OWN_BRAND=$("#OWN_BRAND").data("mybcode");
				var STORE_ID=$("#STORE_ID").val();
				var STORE_NAME=$("#STORE_NAME").val();
				var is_zhiying=$("#FLG_TOB").val();
				var FLG_TOB="";
				if(is_zhiying=="是"){
					FLG_TOB="Y";
				}else if(is_zhiying=="否"){
					FLG_TOB="Y";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==true){
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
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
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
				// $("#OWN_CORP option").val(msg.corp.corp_code);
				// $("#OWN_CORP option").text(msg.corp.corp_name);
				$("#OWN_BRAND").val(msg.brand_name);
				$("#OWN_BRAND").attr("data-mybcode",msg.brand_code);
				// $("#OWN_BRAND option:nth-child(1)").html(msg.brand_name);
				$("#STORE_NAME").val(msg.store_name);
				$("#STORE_ID").val(msg.store_code);
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
				var index=0;
				var area_html='';
				var a=null;
				for(index in msg.areas){
					a=msg.areas[index];
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
	$(".shopadd_oper_btn ul li:nth-of-type(2").click(function(){
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
			var index=0;
			var brand_html='';
			var b=null;
			for(index in msg.brands){
				b=msg.brands[index];
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
	var checknow_data=[];
	var checknow_namedata=[];
	$("#OWN_BRAND").click(function(){
		$(".checkboxselect-container").html('');
		var brand_param={"corp_code":$("#OWN_CORP").val()};
		var brand_command="/shop/brand";
		oc.postRequire("post", brand_command,"",brand_param, function(rdata){
			console.log(rdata);
			if(rdata.code=="0"){
				var msg=JSON.parse(rdata.message);
				console.log(msg);
				var index=0;
				var brand_html='';
				var b=null;
				for(index in msg.brands){
					b=msg.brands[index];
					// brand_html+='<option value="'+b.brand_code+'">'+b.brand_name+'</option>';
					brand_html+='<div class="checkboxselect-item"><input type="checkbox" value="'+b.brand_code+'" data-brandname="'+b.brand_name+'" style="-webkit-appearance: checkbox; width: 14px; height: 14px;">'+b.brand_name+'</div>';
				}
				$(".checkboxselect-container").html(brand_html);
				var check_input=$('.checkboxselect-container input');
				for(var c=0;c<check_input.length;c++){
					check_input[c].onclick=function(){
						if(this.checked==true){
							checknow_data.push($(this).val())
							checknow_namedata.push($(this).data("brandname"));
							console.log(checknow_namedata);
							$('#OWN_BRAND').val(checknow_namedata.toString());
							$('#OWN_BRAND').attr('data-mybcode',checknow_data.toString());
						}else{
							checknow_namedata.remove($(this).data("brandname"));
							checknow_data.remove($(this).val());
							$('#OWN_BRAND').val(checknow_namedata.toString());
							$('#OWN_BRAND').attr('data-mybcode',checknow_data.toString());
						}
					}
				}
				var s=$("#OWN_BRAND").data("mybcode");
				var c_input=$('.checkboxselect-container input');
				var ss = s.split(",");
				for(var i=0;i<ss.length;i++){
					for(var j=0;j<c_input.length;j++){
						console.log($(c_input[j]).val());
						console.log(ss[i]);
						if($(c_input[j]).val()==ss[i]){
							console.log($(c_input[j]).val());
							$(c_input[j]).attr("checked",true);
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
});

