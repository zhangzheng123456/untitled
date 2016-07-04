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
				var TIME_TYPE=$("#TIME_TYPE").val();//时间类型
				var PER_GOAL=$("#PER_GOAL").val();//业绩目标
				var SHOP_NAME=$("#SHOP_NAME").text();//店铺名称
				var DATE="";
				if(TIME_TYPE!=="年"){
					DATE=$("#GOODS_RELEASETIME").val();
					if("DATE"==""){
						var div=$("#GOODS_RELEASETIME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
		            	return;
					}
				}else if(TIME_TYPE=="年"){
					DATE=$("#year").val();
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
				var _params={"corp_code":OWN_CORP,"store_code":SHOP_ID,"user_code":STAFF_ID,"store_name":SHOP_NAME,"achv_type":TIME_TYPE,"achv_goal":PER_GOAL,"end_time":DATE,"isactive":ISACTIVE};
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
				var TIME_TYPE=$("#TIME_TYPE").val();//时间类型
				var PER_GOAL=$("#PER_GOAL").val();//业绩目标
				// var SHOP_NAME=$("#SHOP_NAME").text();//店铺名称
				var SHOP_NAME=$("#SHOP_NAME").find("option:selected").text();
				var DATE="";
				if(TIME_TYPE!=="年"){
					DATE=$("#GOODS_RELEASETIME").val();
					if("DATE"==""){
						var div=$("#GOODS_RELEASETIME").next('.hint').children();
						div.html("该名称已经存在！");
		            	div.addClass("error_tips");
		            	return;
					}
				}else if(TIME_TYPE=="年"){
					DATE=$("#year").val();
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
				var _params={"id":ID,"corp_code":OWN_CORP,"store_code":SHOP_ID,
				"user_code":STAFF_ID,"store_name":SHOP_NAME,"achv_type":TIME_TYPE,
				"achv_goal":PER_GOAL,"end_time":DATE,"isactive":ISACTIVE};
				staffgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
	};
	staffgoaljs.ajaxSubmit=function(_command,_params,opt){
		// console.log(JSON.stringify(_params));
		// _params=JSON.stringify(_params);
		console.log(_params);
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				// if(opt.success){
				// 	opt.success();
				// }
				// window.location.href="";
				$(window.parent.document).find('#iframepage').attr("src","/user/roles.html");
			}else if(data.code=="-1"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
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
		var _params={"id":id};
		var _command="/userAchvGoal/select";
		oc.postRequire("post", _command,"", _params, function(data){
			//console.log(data);
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				//console.log(msg);
				msg=JSON.parse(msg.userAchvGoal);
				// var created_time=$("#created_time").val(msg.created_date);
				// var creator=$("#creator").val(msg.creater);
				// var modify_time=$("#modify_time").val(msg.modified_date);
				// var modifier=$("#modifier").val(msg.modifier);			
				$("#OWN_CORP option").val(msg.corp.corp_code);
				$("#OWN_CORP option").text(msg.corp.corp_name);
				$("#SHOP_NAME option").text(msg.store_name);
				$("#STAFF_NAME option").text(msg.store_name);
				// $("#OWN_CORP").val(msg.own_corp);
				$("#SHOP_ID").val(msg.shop_id);
				// $("#SHOP_NAME").val(msg.shop_name);
				$("#TIME_TYPE").val(msg.time_type);
				$("#PER_GOAL").val(msg.per_goal);
				$("#DATE").val(msg.date);
				
				if(msg.achv_type!=="年"){
					$("#GOODS_RELEASETIME").val(msg.end_time);
					$("#TIME_TYPE").val(msg.achv_type);
				}else if(msg.achv_type=="年"){
					$('#day').hide();
    				$('#week_p').show();
					$("#year").val(msg.end_time);
					$("#TIME_TYPE").val(msg.achv_type);
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
				console.log($("#SHOP_NAME option[value='"+msg.store_code+"']"));
			}else if(data.code=="-1"){
				// art.dialog({
				// 	time: 1,
				// 	lock:true,
				// 	cancel: false,
				// 	content: data.message
				// });
			}
		});
	}else{
		getcorplist();
	}
    $(".staffgoaladd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/staffgoal.html");
	});
	$(".staffgoaledit_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/staffgoal.html");
	});
	//日期类型的点击事件
    $("#drop_down li").click(function(){
    	var text=$(this).html();
    	if(text=="日"){
    		$('#day').show();
    		$('#week_p').hide();
    	}else if(text=="周"){
    		$('#day').show();
    		$('#week_p').hide();
    	}else if(text=="年"){
    		$('#day').hide();
    		$('#week_p').show();
    		year();
    	}else if(text=="月"){
    		$('#day').show();
    		$('#week_p').hide();
    	}
    })
    //点击年的input出来ul
    $("#year").click(function(){
    	$("#week_p .year").show();
    })
    $("#year").blur(function(){
    	setTimeout(function(){
    		$("#week_p .year").hide();	
    	},200);  
    })
});
//获取企业列表信息
function getcorplist(){
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
			$("#OWN_CORP").searchableSelect();
			var c=$('#corp_select .selected').attr("data-value");
			store_data(c);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				store_data(c);
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
//获取店铺列表信息
function store_data(c){
	var _params={};
	_params["corp_code"]=c;//企业编号
	var _command="/user/store";//调取店铺的名字
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
					content: "改企业没有店铺"
			    });
			}
			$("#SHOP_NAME").searchableSelect();
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
//选择年
function year(){
	var myDate = new Date();
	var year=myDate.getFullYear();
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
    })
}