var oc = new ObjectControl();
(function(root,factory){
	root.brand = factory();
}(this,function(){
	var brandjs={};
	brandjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	brandjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	brandjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	brandjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	brandjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	brandjs.bindbutton=function(){
		$(".brandadd_oper_btn ul li:nth-of-type(1)").click(function(){
			var nameMark=$("#BRAND_NAME").attr("data-mark");//品牌编号是否唯一的标志
			var codeMark=$("#BRAND_ID").attr("data-mark");//品牌名称是否唯一的标志
			if(brandjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#BRAND_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#BRAND_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var BRAND_ID=$("#BRAND_ID").val();
				var BRAND_NAME=$("#BRAND_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var app_id=$("#Accounts").attr("data-appid");
				var a=$('.xingming input');
				var cus_user_code="";
				for(var i=0;i<a.length;i++){
			        var u=$(a[i]).attr("data-code");
			        if(i<a.length-1){
			            cus_user_code+=u+",";
			        }else{
			            cus_user_code+=u;
			        }     
    			}
				var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/brand/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"corp_code": OWN_CORP,
					"brand_code": BRAND_ID,
					"brand_name": BRAND_NAME,
					"app_id":app_id,
					"cus_user_code":cus_user_code,
					"isactive": ISACTIVE
				};
				brandjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			var nameMark=$("#BRAND_NAME").attr("data-mark");//品牌编号是否唯一的标志
			var codeMark=$("#BRAND_ID").attr("data-mark");//品牌名称是否唯一的标志
			if(brandjs.firstStep()){
				if(nameMark=="N"||codeMark=="N"){
					if(nameMark=="N"){
						var div=$("#BRAND_NAME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
					}
					if(codeMark=="N"){
						var div=$("#BRAND_ID").next('.hint').children();
						div.html("该编号已经存在！");
		            	div.addClass("error_tips");
					}
	            	return;
				}
				var ID=sessionStorage.getItem("id");
				var BRAND_ID=$("#BRAND_ID").val();
				var BRAND_NAME=$("#BRAND_NAME").val();
				var OWN_CORP=$("#OWN_CORP").val();
				var app_id=$("#Accounts").attr("data-appid");//公众号
				var a=$('.xingming input');//所属客服
				var logo=""
				var img=$("#BRAND_LOGO").parent().prev("img").attr("src");
				if(img!==undefined){
					logo=img;
				}
				var cus_user_code="";
				for(var i=0;i<a.length;i++){
			        var u=$(a[i]).attr("data-code");
			        if(i<a.length-1){
			            cus_user_code+=u+",";
			        }else{
			            cus_user_code+=u;
			        }     
    			}
    			var ISACTIVE="";
				var input=$("#is_active")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/brand/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"brand_code": BRAND_ID,
					"app_id":app_id,
					"cus_user_code":cus_user_code,
					"brand_name": BRAND_NAME,
					"logo":logo,
					"isactive": ISACTIVE
				};
				brandjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	brandjs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				if(_command=="/brand/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/brand/brand_edit.html");
                }
                if(_command=="/brand/edit"){
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
		if(brandjs['check' + command]){
			if(!brandjs['check' + command].apply(brandjs,[obj,hint])){
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
		brandjs.bindbutton();
	}
	var obj = {};
	obj.brandjs = brandjs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	var checknow_data=[];
    var checknow_namedata=[];
	window.brand.init();//初始化
	$(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
	if($(".pre_title label").text()=="编辑品牌信息"){
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
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/brand/select";
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var list=msg.cus_user;
				var corp_code=msg.corp.corp_code;//公司编号
				var logo=msg.logo;
				console.log(list);
				$("#BRAND_ID").val(msg.brand_code);
				$("#BRAND_ID").attr("data-name",msg.brand_code);
				$("#BRAND_NAME").val(msg.brand_name);
				$("#BRAND_NAME").attr("data-name",msg.brand_name);
				// $("#OWN_CORP option").val(msg.corp.corp_code);
				// $("#OWN_CORP option").text(msg.corp.corp_name);
				$("#created_time").val(msg.created_date);
				$("#creator").val(msg.creater);
				$("#modify_time").val(msg.modified_date);
				$("#modifier").val(msg.modifier);
				$("#Accounts").val(msg.app_name);
				$("#Accounts").attr("data-appid",msg.app_id);
				if(logo!==""&&logo!==undefined){
					var img="<img style='width: 60px;margin-bottom: 10px;' src='"+logo+"' alt='暂无图片'>";
					$("#BRAND_LOGO").parent().before(img);
				}
				if (msg.app_id!= "") {
                    if (msg.brand_code.indexOf(',')== -1) {
                        checknow_data = msg.app_id.split(",");
                        checknow_namedata = msg.app_name.split(",");
                    } else {
                        checknow_data.push(msg.app_id);
                        checknow_namedata.push(msg.app_name);
                    }
                }
				var ul="";
		 		for(var i=0;i<list.length;i++){
					ul+="<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+list[i].cus_user_code+"' value='"+list[i].cus_user_name
                     +"'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>";
		 		}
 				$('.xingming').html(ul);
 				var input=$("#is_active")[0];
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
			whir.loading.remove();//移除加载框
		});
	}else{
		getcorplist();
	}
	$("#BRAND_ID").blur(function(){
    	// var isCode=/^[B]{1}[0-9]{4}$/;
    	var _params={};
    	var brand_code=$(this).val();
    	var corp_code=$("#OWN_CORP").val();
    	var brand_code1=$(this).attr("data-name");
		if(brand_code!==""&&brand_code!==brand_code1){
			_params["brand_code"]=brand_code;
			_params["corp_code"]=corp_code;
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/brand/brandCodeExist","", _params, function(data){
	               if(data.code=="0"){
	                    div.html("");
	                    $("#BRAND_ID").attr("data-mark","Y");
	               }else if(data.code=="-1"){
	               		$("#BRAND_ID").attr("data-mark","N");
	               		div.addClass("error_tips");
						div.html("该编号已经存在！");	
	               }
		    })
		}
    })
    $("#BRAND_NAME").blur(function(){
    	var brand_name=$("#BRAND_NAME").val();
    	var brand_name1=$("#BRAND_NAME").attr("data-name");
    	var div=$(this).next('.hint').children();
    	var corp_code=$("#OWN_CORP").val();
    	if(brand_name!==""&&brand_name!==brand_name1){
	    	var _params={};
	    	_params["brand_name"]=brand_name;
	    	_params["corp_code"]=corp_code;
	    	oc.postRequire("post","/brand/brandNameExist","", _params, function(data){
	            if(data.code=="0"){
	            	div.html("");
	            	$("#BRAND_NAME").attr("data-mark","Y");
	            }else if(data.code=="-1"){
	            	div.html("该名称已经存在！")
	            	div.addClass("error_tips");
	            	$("#BRAND_NAME").attr("data-mark","N");
	            }
	    	})
	    }
    })
    //公众号多选
    $("#Accounts").click(function(e){
    	e.stopPropagation();
    	$('.Acc_dropdown').toggle();
    	var corp_code = $('#OWN_CORP').val();
		var corp_code1=$('#OWN_CORP').attr("corp_code1");
		if(corp_code==corp_code1){
			if($('#Acc_dropdown li').length=="0"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content:"请先授权公众号"
				});
			} 
			return;
		}
		$('#OWN_CORP').attr("corp_code1",corp_code);
		Accounts();
    })
    //公众号
    function Accounts(){
    	var param={};
    	var corp_code=$("#OWN_CORP").val();
    	whir.loading.add("",0.5);//加载等待框
    	param["corp_code"]=corp_code;
    	oc.postRequire("post","/corp/selectWx","0",param,function(data){
    		if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var list=msg.list;
                var html="";
                if(list.length==0){
                	art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content:"请先授权公众号"
					});
                }
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                   		html+="<li><p class='checkbox_isactive'><input  type='checkbox' value='"+list[i].app_id+"' data-appname='"+list[i].app_name+"' name='test'  class='check'"
                        + "'/><label></label>\
                        </p><span class='p16'>"+list[i].app_name+"</span></li>"
                    }
                }
                $("#Acc_dropdown").html(html);
				var s = $('#Accounts').attr("data-appid");
                var c_input = $('#Acc_dropdown input');
                var ss = '';
                if (s.indexOf(',')!==-1) {
                    ss = s.split(",");
                    for (var i = 0; i < ss.length; i++) {
                        for (var j = 0; j < c_input.length; j++) {
                            if ($(c_input[j]).val() == ss[i]) {
                                $(c_input[j]).attr("checked", true);
                            }
                        }
                    }
                } else {
                    ss = s;
                    for (var j = 0; j < c_input.length; j++) {
                        if ($(c_input[j]).val() == ss) {
                            $(c_input[j]).attr("checked", true);
                        }
                    }
                }
            }else if(data.code=="-1"){
                // frame();
                // $('.frame').html(data.message);
            }
            whir.loading.remove();//移除加载框
    	});
    }
    $("#Acc_dropdown").on("click","li",function(e){
    	e.stopPropagation();
    	var input=$(this).find("input")[0];
	    if(input.type=="checkbox"&&input.checked==false){
	        input.checked = true;
	        checknow_data.push($(this).find("input").val());
			checknow_namedata.push($(this).find("input").attr("data-appname"));
			$('#Accounts').val(checknow_namedata.toString());
			$('#Accounts').attr('data-appid', checknow_data.toString());
	    }else if(input.type=="checkbox"&&input.checked==true){
	        input.checked = false;
	        checknow_namedata.remove($(this).find("input").attr("data-appname"));
			checknow_data.remove($(this).find("input").val());
			$('#Accounts').val(checknow_namedata.toString());
			$('#Accounts').attr('data-appid', checknow_data.toString());
	    }
    })
	$(".brandadd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
	$("#back_brand").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/brand/brand.html");
	});
	uploadLOGO();
});
function getcorplist(a){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		console.log(data);
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			console.log(msg);
			var index=0;
			var corp_html='';
			for( var i=0;i<msg.corps.length;i++){
				corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$('.corp_select select').searchableSelect();
			$('.corp_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					$("#services").html("");
					$("input[verify='Code']").val("");
					$("#BRAND_NAME").val("");
					$("input[verify='Code']").attr("data-mark","");
					$("#BRAND_NAME").attr("data-mark","");
				}
			});
			$('.searchable-select-item').click(function(){
				$("#services").html("");
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
//logo OSS
function uploadLOGO() {
	var _this=this;
	var client = new OSS.Wrapper({
		region: 'oss-cn-hangzhou',
		accessKeyId: 'O2zXL39br8rSn1zC',
		accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
		bucket: 'products-image'
	});
	document.getElementById('BRAND_LOGO').addEventListener('change', function (e) {
		whir.loading.add("上传中,请稍后...",0.5);
		var file = e.target.files[0];
		var time=_this.getNowFormatDate();
		var corp_code=$("#OWN_CORP").val();
		var brand_code=$("#BRAND_ID").attr("data-name");
		var storeAs='BRAND/logo'+corp_code+'_'+brand_code+'_'+time+'.jpg';
		client.multipartUpload(storeAs, file).then(function (result) {
			var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
			var img="<img style='width: 60px;margin-bottom: 10px;' src='"+url+"' alt='暂无图片'>";
			var len = $("#BRAND_LOGO").parent().prevAll("img").length;
			if(len == 0){
				$("#BRAND_LOGO").parent().before(img);
			}else {
				$("#BRAND_LOGO").parent().prev("img").replaceWith(img);
			}
			whir.loading.remove();
		}).catch(function (err) {
			console.log(err);
		});
	});
}
function getNowFormatDate() {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	var H=date.getHours();
	var M=date.getMinutes();
	var S=date.getSeconds();
	var m=date.getMilliseconds();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = ""+year+month+strDate+H+M+S+m;
	return currentdate
}