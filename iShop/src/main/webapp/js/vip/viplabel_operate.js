var oc = new ObjectControl();
(function(root,factory){
	root.viplabel = factory();
}(this,function(){
	var viplabeljs={};
	viplabeljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	viplabeljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	viplabeljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	viplabeljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	viplabeljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	viplabeljs.bindbutton=function(){
		$(".operadd_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#LABEL_NAME").attr("data-mark");//标签名称是否唯一的标志
			var b=$('#OWN_BRAND_All input');
			var BRAND_CODE="";
			for(var i=0;i<b.length;i++){
				var U=$(b[i]).attr("data-code");
				if(i<b.length-1){
					BRAND_CODE+=U+",";
				}else{
					BRAND_CODE+=U;
				}
			}
			if (BRAND_CODE == "") {
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: "所属品牌不能为空"
				});
				return;
			}
			if(viplabeljs.firstStep()){
				if(nameMark=="N"){
					var nameMark=$("#LABEL_NAME").attr("data-mark");//标签名称是否唯一的标志
					if(nameMark=="N"){
						var div=$("#LABEL_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var LABEL_NAME=$("#LABEL_NAME").val();//标签名称
				// var label_group=$("#label_group").attr("data-id");
				// var label_gpname=$("#label_group").val();
				// if(label_gpname==""){
				// 	alert("请选择标签分组!");
				// 	return;
				// }
				// var LABEL_TYPE=$("#LABEL_TYPE").val();//标签类型
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/VIP/label/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"corp_code": OWN_CORP,
					"label_name": LABEL_NAME,
					"label_group_code":"",
					"brand_code":BRAND_CODE,
					// "label_type": LABEL_TYPE,
					"isactive": ISACTIVE
				};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			var nameMark=$("#LABEL_NAME").attr("data-mark");//标签名称是否唯一的标志
			if(viplabeljs.firstStep()){
				var b=$('#OWN_BRAND_All input');
				var BRAND_CODE="";
				for(var i=0;i<b.length;i++){
					var U=$(b[i]).attr("data-code");
					if(i<b.length-1){
						BRAND_CODE+=U+",";
					}else{
						BRAND_CODE+=U;
					}
				}
				if (BRAND_CODE == "") {
					art.dialog({
						zIndex:10003,
						time: 1,
						lock: true,
						cancel: false,
						content: "所属品牌不能为空"
					});
					return;
				}
				if(nameMark=="N"){
					if(nameMark=="N"){
						var div=$("#LABEL_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var ID=sessionStorage.getItem("id");//编辑时候的id
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var LABEL_NAME=$("#LABEL_NAME").val();//标签名称
				// var label_group=$("#label_group").attr("data-id");
				// var LABEL_TYPE=$("#LABEL_TYPE").val();//标签类型
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/VIP/label/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"label_name": LABEL_NAME,
					"label_group_code":"",
					"brand_code":BRAND_CODE,
					// "label_type": LABEL_TYPE,
					"isactive": ISACTIVE
				};
				viplabeljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	viplabeljs.ajaxSubmit=function(_command,_params,opt){
		// console.log(_params);
		whir.loading.add("", 0.5);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/VIP/label/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/viplabel_edit.html");
                }
                if(_command=="/VIP/label/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
				// $(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html");
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
			whir.loading.remove();
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
		if(viplabeljs['check' + command]){
			if(!viplabeljs['check' + command].apply(viplabeljs,[obj,hint])){
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
		viplabeljs.bindbutton();
	}
	var obj = {};
	obj.viplabeljs = viplabeljs;
	obj.init = init;
	return obj;
}));
var checknow_data=[];
var checknow_namedata=[];
jQuery(document).ready(function(){
	window.viplabel.init();//初始化
	if($(".pre_title label").text()=="编辑标签"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length<=0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/VIP/label/select";
		var a="";
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_code=msg.corp_code;//公司编号
				if (msg.brand_code != "") {
					if (msg.brand_code.indexOf(',') !== -1) {
						checknow_data = msg.brand_code.split(",");
						checknow_namedata = msg.brand_name.split(",");
					} else {
						checknow_data.push(msg.brand_code);
						checknow_namedata.push(msg.brand_name);
					}
				}
				checknow_data.reverse();
				checknow_namedata.reverse();
				for(var i=0;i<checknow_namedata.length;i++){
					$('#OWN_BRAND_All').append("<p><input type='text 'readonly='readonly' style='width: 348px;margin-right: 10px' data-code='"+checknow_data[i]+"'  value='"+checknow_namedata[i]+"'><span class='power remove_app_id'>删除</span></p>");
				}
				// var label_type=msg.label_type//会员标签
				// $("#LABEL_TYPE option[value='"+label_type+"']").attr("selected","true");
				$("#LABEL_NAME").val(msg.label_name);
				// $("#label_group").val(msg.label_group_name);
				// $("#label_group").attr("data-id",msg.label_group_code);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				$("#LABEL_NAME").attr("data-name",msg.label_name);
				var input=$(".checkbox_isactive").find("input")[0];
				if(msg.isactive=="Y"){
					input.checked=true;
				}else if(msg.isactive=="N"){
					input.checked=false;
				}
				getcorplist(corp_code);
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
		getcorplist(a);
	}
	$(".operadd_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html?t="+ $.now());
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html?t="+ $.now());
	});
	$("#back_vip_label").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/vip/viplabel.html?t="+ $.now());
	});
	//标签分组下拉菜单
	$("#label_group").click(function () {
		$("#labelgp_select").show()
		getlabelGroup();
	})

	$("#label_group").blur(function () {
		setTimeout(function () {
			$("#labelgp_select").hide();
		}, 200)
	})
});

function getcorplist(a){
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
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('.corp_select select').searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					$("input[verify='Code']").val("");
					$("#STORE_NAME").val("");
					$("input[verify='Code']").attr("data-mark","");
					$("#STORE_NAME").attr("data-mark","");
					$("#label_group").attr("data-id","");
					$("#label_group").val("");
				}
			})
			$('.searchable-select-item').click(function(){
				$("input[verify='Code']").val("");
				$("#STORE_NAME").val("");
				$("input[verify='Code']").attr("data-mark","");
				$("#STORE_NAME").attr("data-mark","");
				$("#label_group").attr("data-id","");
				$("#label_group").val("");
			})
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
	});//获取企业列表信息
}
function getlabelGroup(){
	var corp_code=$("#OWN_CORP").val();
	console.log(corp_code);
	var param={};
	    param["corp_code"]=corp_code;
	oc.postRequire("post","/viplablegroup/selectViplabGroupList","",param,function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			    msg=JSON.parse(msg.viplableGroups);
			$("#labelgp_select").empty();
			if(msg.length=="0"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "该企业下暂无标签分组"
				});
			}
			for(var i=0;i<msg.length;i++){
				$("#labelgp_select").append('<li id="'+msg[i].label_group_code+'">'+msg[i].label_group_name+'</li>')
			}
			$("#labelgp_select li").click(function () {
				$("#label_group").val($(this).html());
				var id=$(this).attr("id");
				$("#label_group").attr("data-id",id);
			})
		}else if(data.code=="-1") {
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	})
}
$("#ADD_BRAND").click(function(){
	//$("#screen_brand .screen_content_l").unbind("scroll");
	var arr=whir.loading.getPageSize();
	$("#brand_search").val("");
	$("#screen_brand .s_pitch span").html("0");
	$("#screen_brand .screen_content_l ul").empty();
	$("#screen_brand .screen_content_r ul").empty();
	$("#screen_brand").show();
	// var left=(arr[0]-$("#screen_brand").width())/2;
	// var tp=(arr[3]-$("#screen_brand").height())/2+40;
	$("#screen_brand").css({"position":"fixed"});
	getBrand();
});


//点击右移选中
$(".shift_right").click(function(){
	var right="only";
	var div=$(this);
	removeRight(right,div);
});
//点击右移全部
$(".shift_right_all").click(function(){
	var right="all";
	var div=$(this);
	removeRight(right,div);
});
//点击左移
$(".shift_left").click(function(){
	var left="only";
	var div=$(this);
	removeLeft(left,div);
});
//点击左移全部
$(".shift_left_all").click(function(){
	var left="all";
	var div=$(this);
	removeLeft(left,div);
});
//移到右边
function removeRight(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
	}
	if(li.length=="0"){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
		return;
	}
	if(li.length>0){
		for(var i=0;i<li.length;i++){
			var html=$(li[i]).html();
			var id=$(li[i]).find("input[type='checkbox']").val();
			$(li[i]).find("input[type='checkbox']")[0].checked=true;
			var input=$(b).parents(".screen_content").find(".screen_content_r li");
			for(var j=0;j<input.length;j++){
				if($(input[j]).attr("id")==id){
					$(input[j]).remove();
				}
			}
			$(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
			$(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//移到左边
function removeLeft(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
	}
	if(li.length=="0"){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
		return;
	}
	if(li.length>0){
		for(var i=li.length-1;i>=0;i--){
			$(li[i]).remove();
			$(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
	var input=$(this).find("input")[0];
	var thinput=$("thead input")[0];
	if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
		input.checked = true;
	}else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
		input.checked = false;
	}
});
//获取品牌
function getBrand(){
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	var brand_command = "/shop/brand";
	var brand_code = $("#OWN_CORP").val();
	var searchValue=$("#brand_search").val().trim();
	var pageSize=20;
	var area_param = {};
	area_param["searchValue"]=searchValue;
	area_param["pageSize"]=pageSize;
	area_param["corp_code"]=brand_code;
	oc.postRequire("post", brand_command, "", area_param, function (data) {
		if (data.code == "0") {
			var msg = JSON.parse(data.message);
			var list=msg.brands;
			var brand_html = '';
			for (var i = 0; i < list.length; i++) {
				brand_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxOneInput"
					+ i
					+ 1
					+ "'/><label for='checkboxOneInput"
					+ i
					+ 1
					+ "'></label></div><span class='p16' title='"+list[i].brand_name+"'>"+list[i].brand_name+"</span></li>"
			}
			$("#screen_brand .screen_content_l ul").append(brand_html);
			var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
			}
		} else if (data.code == "-1") {
			art.dialog({
				zIndex:10003,
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
		whir.loading.remove();//移除加载框
	});

}
//品牌搜索
$("#brand_search").keydown(function(){
	$("#screen_brand .screen_content_l").unbind("scroll");
	isscroll=false;
	var event=window.event||arguments[0];
	if(event.keyCode == 13){
		$("#screen_brand .screen_content_l ul").empty();
		getBrand();
	}
});
//品牌放大镜搜索
$("#brand_search_f").click(function(){
	$("#screen_brand .screen_content_l").unbind("scroll");
	$("#screen_brand .screen_content_l ul").empty();
	getBrand();
});
$("#screen_que_brand").click(function(){
	var li=$(this).prev().children(".screen_content_r").find("ul li");
	var hasli=$("#OWN_BRAND_All p");
	var a=$('#OWN_BRAND_All input');
	if((li.length+hasli.length)>10){
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			zIndex:10003,
			content: "所选品牌不能超过10个"
		});
		return;
	}
	if(li.length>0){
		for(var i=0;i<li.length;i++){
			for(var j=0;j<a.length;j++){
				if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
					$(a[j]).parent("p").remove();
				}
			}
			$('#OWN_BRAND_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id'>删除</span></p>");
		}
	}
	$("#screen_brand").hide();
});

//删除
$(".xingming").on("click",".remove_app_id",function(){
	$(this).parent().remove();
});
$("#screen_close_brand").click(function(){
	$("#screen_brand").hide();
});

$(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
//验证标签名称是否唯一的标志
$("#LABEL_NAME").blur(function(){
    var label_name=$("#LABEL_NAME").val();//标签名称
    var label_name1=$("#LABEL_NAME").attr("data-name");//编辑的标签名称的一个标志
    var div=$(this).next('.hint').children();
    var corp_code=$("#OWN_CORP").val();
    if(label_name!==""&&label_name!==label_name1){
	    var _params={};
	    _params["corp_code"]=corp_code;
	    _params["tag_name"]=label_name;
	    oc.postRequire("post","/VIP/label/VipLabelNameExist","", _params, function(data){
	        if(data.code=="0"){
	            div.html("");
	            $("#LABEL_NAME").attr("data-mark","Y");
	        }else if(data.code=="-1"){
	            div.html("改名称已经存在！");
	            div.addClass("error_tips");
	            $("#LABEL_NAME").attr("data-mark","N");
	        }
	    })
	}
})