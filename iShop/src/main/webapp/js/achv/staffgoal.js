var oc = new ObjectControl();
(function(root,factory){
	root.staffgoal = factory();
}(this,function(){
	var staffgoaljs={};
	staffgoaljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	staffgoaljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	staffgoaljs.checkNumber=function(obj,hint){
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
	staffgoaljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	staffgoaljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	staffgoaljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	staffgoaljs.bindbutton=function(){
		$(".staffgoaladd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffgoaljs.firstStep()){
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var SHOP_ID=$("#SHOP_NAME").val();//店铺编号
				var TIME_TYPE1=$("#TIME_TYPE").val();//时间类型
				var PER_GOAL=parseInt($("#PER_GOAL").val());//业绩目标
				var SHOP_NAME=$("#SHOP_NAME").find("option:selected").text();//店铺名称
				var user_code=$("#STAFF_NAME").val();//员工编号
				var user_name=$("#STAFF_NAME").find("option:selected").text();//员工名称
				var DATE="";//日期
				var TIME_TYPE="";//日期类型
				if(SHOP_ID==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content: "店铺名称不能为空"
					});
					return;
				}
				if(user_code==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content: "员工名称不能为空"
					});
					return;
				}
				if(TIME_TYPE1!=="年"&&TIME_TYPE1!=="月"){
					DATE=$("#GOODS_RELEASETIME").val();
					if(DATE==""){
                        // var div=$("#GOODS_RELEASETIME").next('.hint').children();
                        // div.html("不能为空！");
		            	// div.addClass("error_tips");
						art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content: "日期不能为空"
						});
		            	return;
					}
				}else if(TIME_TYPE1=="年"){
					DATE=$("#year").val();
				}else if(TIME_TYPE1=="月"){
					var year=$("#year").val();//年份
					var month=$("#month").val();//月份
					DATE=year+"-"+month;
				}
				if(TIME_TYPE1=="年"){
                    TIME_TYPE="Y";
				}else if(TIME_TYPE1=="月"){
					TIME_TYPE="M";
				}else if(TIME_TYPE1=="周"){
					TIME_TYPE="W";
				}else if(TIME_TYPE1=="日"){
					TIME_TYPE="D";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/userAchvGoal/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"corp_code": OWN_CORP,
					"store_code": SHOP_ID,
					"achv_type": TIME_TYPE,
					"store_name":SHOP_NAME,
					"achv_goal": PER_GOAL,
					"end_time": DATE,
                    "user_code":user_code,
                    "user_name":user_name,
					"isactive": ISACTIVE
				};
				staffgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".staffgoaledit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(staffgoaljs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var SHOP_ID=$("#SHOP_NAME").val();//店铺编号
				var TIME_TYPE1=$("#TIME_TYPE").val();//时间类型
				var PER_GOAL=parseInt($("#PER_GOAL").val());//业绩目标
				var SHOP_NAME=$("#SHOP_NAME").find("option:selected").text();//店铺名称
				var user_code=$("#STAFF_NAME").val();//员工编号
				var user_name=$("#STAFF_NAME").find("option:selected").text();//员工名称
				var DATE="";//日期
				var TIME_TYPE="";//日期类型
				if(SHOP_ID==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content: "店铺名称不能为空"
					});
					return;
				}
				if(user_code==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content: "员工名称不能为空"
					});
					return;
				}
				if(TIME_TYPE1!=="年"&&TIME_TYPE1!=="月"){
					DATE=$("#GOODS_RELEASETIME").val();
					if(DATE==""){
                        // var div=$("#GOODS_RELEASETIME").next('.hint').children();
                        // div.html("不能为空！");
		            	// div.addClass("error_tips");
						art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content: "日期不能为空"
						});
		            	return;
					}
				}else if(TIME_TYPE1=="年"){
					DATE=$("#year").val();
				}else if(TIME_TYPE1=="月"){
					var year=$("#year").val();//年份
					var month=$("#month").val();//月份
					DATE=year+"-"+month;
				}
				if(TIME_TYPE1=="年"){
                    TIME_TYPE="Y";
				}else if(TIME_TYPE1=="月"){
					TIME_TYPE="M";
				}else if(TIME_TYPE1=="周"){
					TIME_TYPE="W";
				}else if(TIME_TYPE1=="日"){
					TIME_TYPE="D";
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/userAchvGoal/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"store_code": SHOP_ID,
					"achv_type": TIME_TYPE,
					"store_name":SHOP_NAME,
					"achv_goal": PER_GOAL,
					"end_time": DATE,
                    "user_code":user_code,
                    "user_name":user_name,
					"isactive": ISACTIVE
				};
				staffgoaljs.ajaxSubmit(_command, _params, opt);
			}else{
				return;
			}
		});
	};
	staffgoaljs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/userAchvGoal/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/achv/staffgoal_edit.html");
                }
                if(_command=="/userAchvGoal/edit"){
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
		if(staffgoaljs['check' + command]){
			if(!staffgoaljs['check' + command].apply(staffgoaljs,[obj,hint])){
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
		staffgoaljs.bindbutton();
	}
	var obj = {};
	obj.staffgoaljs = staffgoaljs;
	obj.init = init;
	return obj;
}));
jQuery(document).ready(function(){
	window.staffgoal.init();//初始化
	if($(".pre_title label").text()=="编辑员工业绩目标"){
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
		var _command="/userAchvGoal/select";
		var a="";
		var b="";
		var e="";
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				$("#PER_GOAL").val(msg.user_target);
				var corp_code=msg.corp.corp_code;//公司编号
				var store_code=msg.store.store_code//店铺编号
				var user_code=msg.user.user_code;//员工编号
				if(msg.target_type=="D"){
					$('#TIME_TYPE').val("日");
					$("#GOODS_RELEASETIME").val(msg.target_time);
				}else if(msg.target_type=="W"){
					$('#TIME_TYPE').val("周");
					$("#GOODS_RELEASETIME").val(msg.target_time);
				}else if(msg.target_type=="Y"){
					$('#TIME_TYPE').val("年");
					$('#day').hide();
    				$('#week_p').show();
    				$('#month').hide();
					$("#year").val(msg.target_time);
				}else if(msg.target_type=="M"){
					$('#TIME_TYPE').val("月");
					$('#day').hide();
    				$('#week_p').show();
    				var target_time=msg.target_time;
    				var target_time=target_time.split('-');
					$("#year").val(target_time[0]);
					$("#month").val(target_time[1]);
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
				getcorplist(corp_code,store_code,user_code);
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
		getcorplist(a,b,e);
	}
    $(".staffgoaladd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/staffgoal.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/staffgoal.html");
	});
	$("#achv_staff").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/staffgoal.html");
	});
	//日期类型的点击事件
    $("#drop_down li").click(function(){
    	var text=$(this).html();
    	if(text=="日"){
    		$('#day').show();
    		$('#week_p').hide();
    		$("#GOODS_RELEASETIME").val("");
    	}else if(text=="周"){
    		$('#day').show();
    		$('#week_p').hide();
    		$("#GOODS_RELEASETIME").val("");
    	}else if(text=="年"){
    		$('#day').hide();
    		$('#week_p').show();
    		$('#month').hide();
    	}else if(text=="月"){
    		$('#day').hide();
    		$('#week_p').show();
    		$('#month').show();
    	}
    })
    //点击年的input出来ul
    $("#year").click(function(){
    	$(".year").toggle();
    })
    $("#year").blur(function(){
    	setTimeout(function(){
    		$("#week_p .year").hide();	
    	},200);  
    })
    //点击月份的input出来ul
    $("#month").click(function(){
    	$(".month").toggle();
    })
    $("#month").blur(function(){
    	setTimeout(function(){
    		$("#week_p .month").hide();	
    	},200);  
    })
});
//获取企业列表信息
function getcorplist(a,b,e){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", corp_command,"", "", function(data){
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
			$("#OWN_CORP").searchableSelect();
			$("#corp_select .searchable-select-input").keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					var corp_code=$("#OWN_CORP").val();
					store_data(corp_code,b,e);
				}
			})
			var c=$('#corp_select .selected').attr("data-value");
			store_data(c,b,e);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				store_data(c,b,e);
			})
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
}
//获取店铺列表信息
function store_data(c,b,e){
	var _params={};
	_params["corp_code"]=c;//企业编号
	var _command="/user/store";//调取店铺的名字
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", _command,"", _params, function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			// console.log(msg.stores);
			var msg_stores=JSON.parse(msg.stores);
			$('#SHOP_NAME').empty();
			$('#shop_select .searchable-select').remove();
			if(msg_stores.length>0){
				for(var i=0;i<msg_stores.length;i++){
					$('#SHOP_NAME').append("<option value='"+msg_stores[i].store_code+"'>"+msg_stores[i].store_name+"</option>");
				}
			}else if(msg_stores.length<=0){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "该企业没有店铺"
			    });
			}
			if(b!==""){
				$("#SHOP_NAME option[value='"+b+"']").attr("selected","true")
			}
			$("#SHOP_NAME").searchableSelect();
			var store_code=$('#SHOP_NAME').val();
			staff_data(c,store_code,b,e);
			$("#shop_select .searchable-select-input").keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					var store_code=$('#SHOP_NAME').val();
					console.log(store_code);
					staff_data(c,store_code,b,e);
				}
			})
			$("#shop_select .searchable-select-item").click(function(){
				var store_code=$(this).attr("data-value");
				staff_data(c,store_code,b,e);
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
	})
}
//获取员工列表信息
function staff_data(d,f,b,e){
	var _params={};
	if(f==null){
		$('#STAFF_NAME').empty();//清空员工selet
		$('#staff_select .searchable-select').remove();//删除插件对应的ul
		$("#STAFF_NAME").searchableSelect();
		return;
	}
	_params["corp_code"]=d;
	_params["store_code"]=f;
	whir.loading.add("",0.5);
	oc.postRequire("post","/shop/staff","list",_params,function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			$('#STAFF_NAME').empty();
			$('#staff_select .searchable-select').remove();
			if(msg.length>0){
				for(var i=0;i<msg.length;i++){
					$('#STAFF_NAME').append("<option value='"+msg[i].user_code+"'>"+msg[i].user_name+"</option>");
				}
			}else if(msg.length<=0){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "该店铺没有员工"
			    });
			}
			if(e!==""){
				$("#STAFF_NAME option[value='"+e+"']").attr("selected","true")
			}
			$("#STAFF_NAME").searchableSelect();
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: data.message
			});
		}
		whir.loading.remove();
	})
}
function year(){
	var myDate = new Date();
	var year=myDate.getFullYear();
	$('#year').val(year)
	var year=year-1;
	console.log($('#week_p .year'));
	$('#week_p .year').empty();
	for(var i=0;i<10;i++){
		year++;
		var li="<li>";
		li+=""+year+"</li>"
		$('#week_p .year').append(li);
	}
	$("#week_p .year>li").click(function(){
    	console.log(this);
    	var value=$(this).html();
    	$('#year').val(value);
    	$('#week_p .year').hide();
		month();
    })
}
function month(){
	var myDate=new Date();
	var month=myDate.getMonth(); //获取当前月份(0-11,0代表1月)
	var year=new Date().getFullYear();
	month=month+1;
	if(month<10){
		month="0"+month;
	}
	$('#month').val(month);
	var html="";
	for(var i=1;i<=12;i++){
		var style="";
		if(i<10){
			i="0"+i;
		}
		if($("#year").val()==year&&i<month){
			style="opacity:0.5";
		}
		html+="<li style='"+style+"'>"+i+"</li>";
	}
	$('#week_p .month').html(html);
	$("#week_p .month>li").click(function(){
		if($(this).attr("style")!==""){
			return;
		}
    	console.log(this);
    	var value=$(this).html();
    	$('#month').val(value);
    	$('#week_p .month').hide();
    })
}
year();
month();