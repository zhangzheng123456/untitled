var oc = new ObjectControl();
(function(root,factory){
	root.shopgoal = factory();
}(this,function(){
	var shopgoaljs={};
	shopgoaljs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	shopgoaljs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	shopgoaljs.checkNumber=function(obj,hint){
		var isCode=/^[0-9]{1,13}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				console.log(obj.length);
				this.displayHint(hint,"请输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	shopgoaljs.checkNumber1=function(obj,hint){
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
	};
	shopgoaljs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	shopgoaljs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	shopgoaljs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	shopgoaljs.bindbutton=function(){
		$(".shopgoaladd_oper_btn ul li:nth-of-type(1)").click(function(){
			if(shopgoaljs.firstStep()){
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var SHOP_ID=$("#SHOP_NAME").val();//店铺编号
				var TIME_TYPE1=$("#time_type").val();//时间类型
				var PER_GOAL=$("#PER_GOAL").val();//业绩目标
				// var SHOP_NAME=$("#SHOP_NAME").text();//店铺名称
				var SHOP_NAME=$("#SHOP_NAME").find("option:selected").text();//店铺名称
				var DATE="";
				var TIME_TYPE="";//日期类型
				var _params = {};
				if(SHOP_ID==null){
					art.dialog({
						time: 1,
						lock:true,
						cancel: false,
						content: "店铺名称不能为空"
					});
					return;
				}
				if(TIME_TYPE1=="按日"){
					DATE=$("#day_input").val();
					if(DATE==""){
						art.dialog({
							time: 1,
							lock:true,
							cancel: false,
							content: "日期不能为空"
						});
		            	return;
					}
				}else if(TIME_TYPE1=="按月"){
					var year=$("#month_input").attr("data-year");//年份
					var month=$("#month_input").attr("data-month");//月份
					DATE=year+"-"+month;
				}
				if(TIME_TYPE1=="按月"){
                    TIME_TYPE="M";
					if($("#mean_radio")[0].checked==true){
						_params["per_amount"]=$("#month_day span").html();
						_params["isaverage"]="Y";
					}else if($("#mean_radio")[0].checked==false){
						var proportion="";
						var targets_arr="";
						var week_achv=$("#week_achv li");
						var week_parent=$("#week_parent .week_text");
						for(var i=0;i<week_parent.length;i++){
							proportion+=$(week_parent[i]).text()+",";
							targets_arr+=$(week_achv[i]).attr("data-money")+",";
						}
						_params["isaverage"]="N";
						_params["proportion"]=proportion;
						_params["targets_arr"]=targets_arr;
					}
				}else if(TIME_TYPE1=="按日"){
					TIME_TYPE="D";
					delete _params.per_amount;
					delete _params.isaverage;
					delete _params.proportion;
					delete _params.targets_arr;
				}
				var ISACTIVE="";
				var input=$(".checkbox_isactive").find("input")[0];
				if(input.checked==true){
					ISACTIVE="Y";
				}else if(input.checked==false){
					ISACTIVE="N";
				}
				var _command="/storeAchvGoal/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
					_params["corp_code"]= OWN_CORP,
					_params["store_code"]= SHOP_ID,
					_params["achv_type"]=TIME_TYPE,
					_params["store_name"]=SHOP_NAME,
					_params["achv_goal"]=PER_GOAL,
					_params["end_time"]=DATE,
					_params["isactive"]=ISACTIVE
				shopgoaljs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$(".shopgoaledit_oper_btn ul li:nth-of-type(1)").click(function(){
			if(shopgoaljs.firstStep()){
				var ID=sessionStorage.getItem("id");
				var OWN_CORP=$("#OWN_CORP").val();//公司编号
				var SHOP_ID=$("#SHOP_NAME").val();//店铺编号
				var TIME_TYPE1=$("#TIME_TYPE").val();//时间类型
				var PER_GOAL=$("#PER_GOAL").val();//业绩目标
				var SHOP_NAME=$("#SHOP_NAME").find("option:selected").text();//店铺名称
				var DATE="";
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
				if(TIME_TYPE1!=="年"&&TIME_TYPE1!=="月"){
					DATE=$("#GOODS_RELEASETIME").val();
					if(DATE==""){
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
				var _command="/storeAchvGoal/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){
					}
				};
				var _params = {
					"id": ID,
					"corp_code": OWN_CORP,
					"store_code": SHOP_ID,
					"store_name":SHOP_NAME,
					"achv_type": TIME_TYPE,
					"achv_goal": PER_GOAL,
					"end_time": DATE,
					"isactive": ISACTIVE
				};
				shopgoaljs.ajaxSubmit(_command, _params, opt);
			}else{
				return;
			}
		});
		$("#drop_down li").click(function(){
			var text=$(this).html();
			$(this).parents(".data_list").find("input").val(text);
			$(this).parent().hide();
			$(this).css({"color":"#32b5c9"});
			$(this).siblings("li").css({"color":"#888"});
			if(text=="按日"){
				$("#allot_type").hide();
				$("#day_input").show();
				$("#month_input").hide();
				$("#mean_content").hide();
				$("#month_day").hide();
			}
			if(text=="按月"){
				$("#day_input").hide();
				$("#allot_type").show();
				$("#month_input").show();
				if($("#mean_radio")[0].checked==true){
					$("#mean_content").hide();
					$("#month_day").show();
				}else if($("#mean_radio")[0].checked==false){
					$("#mean_content").show();
				}
				month_day();
			}
		})
		$("#drop_down input").click(function(){
			if($(this).siblings("ul").css("display")=="none"){
				$(this).siblings("ul").show();
			}else if($(this).siblings("ul").css("display")=="block"){
				$(this).siblings("ul").hide();
			}
		})
		$("#allot_type .rido").click(function(){
			$(this).find("input")[0].checked=true;
			if($(this).find("input").attr("id")=="mean_radio"){
				$("#month_day").show();
				$("#mean_content").hide();
				$("#month_scale").hide();
			}
			if($(this).find("input").attr("id")=="scale_radio"){
				$("#month_day").hide();
				$("#mean_content").show();
				$("#month_scale").show();
			}
		})
		$("#drop_down input").blur(function(){
			var self=this;
			setTimeout(function () {
				$(self).siblings("ul").hide();
			},200);
		})
		$("#month_input").click(function(e){//月的时候点击弹出框
			e.stopPropagation();
			if($(this).siblings(".ym_parent").css("display")=="none"){
				$(this).siblings(".ym_parent").show();
			}else if($(this).siblings(".ym_parent").css("display")=="block"){
				$(this).siblings(".ym_parent").hide();
			}
		})
		$("#ym_header li").click(function(){
			$(this).addClass("active");
			$(this).siblings("li").attr("class","");
			$("#ym_content>div").eq($(this).index()).show();
			$("#ym_content>div").eq($(this).index()).siblings().hide();
		})
		$(document).click(function(e){//点击旁边隐藏选择日期的div
			if($(e.target).is('.ym_header')||$(e.target).is('.ym_header li')||$(e.target).is(".ym_parent")||$(e.target).is(".ym_parent li")||$(e.target).is(".ym_parent .ym_footer")||$(e.target).is(".ym_parent .ym_footer .footer_q")||$(e.target).is(".month_header")||$(e.target).is(".month_header span")){
				return;
			}else {
				$(".ym_parent").hide();
			}
		});
		$("#yearnew").on("click","li",function(){//点击年的列表给年赋值
			$("#m_year").html($(this).text());
			$("#m_year").attr("data-type",$(this).attr("data-type"));
			$(this).addClass("active");
			$(this).siblings("li").attr("class","");
			if($("#m_year").attr("data-type")==$("#yearnew li").eq(0).attr("data-type")){
				$("#monthnew .year_add").css({"opacity":"0.5","color":"#888"});
				$("#monthnew .year_del").css({"color":"#41c7db","opacity":"1"});
			}else if($("#m_year").attr("data-type")==$("#yearnew li:last").attr("data-type")){
				$("#monthnew .year_del").css({"opacity":"0.5","color":"#888"});
				$("#monthnew .year_add").css({"color":"#41c7db","opacity":"1"});
			}else {
				$("#monthnew .year_del").css({"color":"#41c7db","opacity":"1"});
				$("#monthnew .year_add").css({"color":"#41c7db","opacity":"1"});
			}
			month();
		})
		$("#monthnew").on("click","li",function(){//点击月的时候赋值
			if($(this).attr("style")!==""){
				return;
			}
			$("#month_input").val($(this).text());
			$(this).addClass("active");
			$(this).siblings("li").attr("class","");
			$("#month_input").attr("data-year",$("#m_year").attr("data-type"));
			$("#month_input").attr("data-month",$(this).attr("data-type"));
			month_day();
		})
		$("#monthnew .year_add").click(function(){//减年份
			if($("#m_year").attr("data-type")==$("#yearnew li").eq(0).attr("data-type")){
				$(this).css({"opacity":"0.5","color":"#888"});
				return;
			}
			$("#monthnew .year_del").css({"color":"#41c7db","opacity":"1"});
			$("#m_year").attr("data-type",parseInt($("#m_year").attr("data-type"))-1);
			if($("#m_year").attr("data-type")==$("#yearnew li").eq(0).attr("data-type")){
				$(this).css({"opacity":"0.5","color":"#888"});
			}
			$("#m_year").html(parseInt($('#m_year').attr('data-type'))+"年");
			$("#yearnew li:contains('"+$('#m_year').text()+"')").addClass("active");
			$("#yearnew li:contains('"+$('#m_year').text()+"')").siblings("li").attr("class","");
			month();
		})
		$("#monthnew .year_del").click(function(){//加年份
			if($("#m_year").attr("data-type")==$("#yearnew li:last").attr("data-type")){
				$(this).css({"opacity":"0.5","color":"#888"});
				return;
			}
			$("#monthnew .year_add").css({"color":"#41c7db","opacity":"1"});
			$("#m_year").attr("data-type",parseInt($("#m_year").attr("data-type"))+1);
			if($("#m_year").attr("data-type")==$("#yearnew li:last").attr("data-type")){
				$(this).css({"opacity":"0.5","color":"#888"});
			}
			$("#m_year").html(parseInt($('#m_year').attr('data-type'))+"年");
			$("#yearnew li:contains('"+$('#m_year').text()+"')").addClass("active");
			$("#yearnew li:contains('"+$('#m_year').text()+"')").siblings("li").attr("class","");
			month();
		})
		//点击加号比例
		$("#week_parent .scale_add").click(function(){
			if($(this).parent(".week_scale").find(".week_text").text()=="10"){
				return;
			}
			 $(this).parent(".week_scale").find(".week_text").text(parseFloat($(this).parent(".week_scale").find(".week_text").text())+1);
			$(this).parent(".week_scale").find(".scale_del").css({"color":"#6dc1c8","opacity":"1"});
			month_day();
			if($(this).parent(".week_scale").find(".week_text").text()=="10"){
				$(this).css({"opacity":"0.5","color":"#888"});
				return;
			}
		})
		//点击减号比例减少
		$("#week_parent .scale_del").click(function(){
			if($(this).parent(".week_scale").find(".week_text").text()=="1"){
				return;
			}
			$(this).parent(".week_scale").find(".week_text").text(parseFloat($(this).parent(".week_scale").find(".week_text").text())-1);
			$(this).parent(".week_scale").find(".scale_add").css({"color":"#6dc1c8","opacity":"1"});
			month_day();
			if($(this).parent(".week_scale").find(".week_text").text()=="1"){
				$(this).css({"color":"#888","opacity":"0.5"});
				return;
			}
		})
		$("#footer_q").click(function(){
			$(this).parents(".ym_parent").hide();
		})
	};
	shopgoaljs.ajaxSubmit=function(_command,_params,opt){
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				if(_command=="/storeAchvGoal/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/achv/shopgoal.html");
                }
                if(_command=="/storeAchvGoal/edit"){
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
		if(shopgoaljs['check' + command]){
			if(!shopgoaljs['check' + command].apply(shopgoaljs,[obj,hint])){
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
		shopgoaljs.bindbutton();
	}
	var obj = {};
	obj.shopgoaljs = shopgoaljs;
	obj.init = init;
	return obj;
}));
//编辑页面赋值
jQuery(document).ready(function(){
	window.shopgoal.init();//初始化
	var a="";//
	var b="";//
	if($(".pre_title label").text()=="编辑店铺业绩目标"){
		var id=sessionStorage.getItem("id");
		var key_val=sessionStorage.getItem("key_val");//取页面的function_code
		key_val=JSON.parse(key_val);
		var funcCode=key_val.func_code;
		$.get("/detail?funcCode="+funcCode+"", function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var action=message.actions;
				if(action.length==0){
					$("#edit_save").remove();
					$("#edit_close").css("margin-left","120px");
				}
			}
		});
		var _params={"id":id};
		var _command="/storeAchvGoal/select";
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"", _params, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				msg=JSON.parse(msg.storeAchvGoal);
				var corp_code=msg.corp_code;//公司编号
				var store_code=msg.store_code;//店铺编号
				$("#PER_GOAL").val(msg.target_amount);
				if(msg.time_type=="D"){
					$('#TIME_TYPE').val("日");
					$("#GOODS_RELEASETIME").val(msg.target_time);
				}else if(msg.time_type=="W"){
					$('#TIME_TYPE').val("周");
					$("#GOODS_RELEASETIME").val(msg.target_time);
				}else if(msg.time_type=="Y"){
					$('#TIME_TYPE').val("年");
					$('#day').hide();
    				$('#week_p').show();
    				$('#month').hide();
					$("#year").val(msg.target_time);
				}else if(msg.time_type=="M"){
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
				getcorplist(corp_code,store_code);
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
	}else{
		getcorplist(a,b);
	}
    $(".shopgoaladd_oper_btn ul li:nth-of-type(2)").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/shopgoal.html");
	});
	$("#edit_close").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/shopgoal.html");
	});
	$("#back_achvshop").click(function(){
		$(window.parent.document).find('#iframepage').attr("src","/achv/shopgoal.html");
	});
	//日期类型的点击事件
    $("#drop_down li").click(function(){
    	var text=$(this).html();
    	if(text=="日"){
			$("#month_day").hide();
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
			 month_day();
			$("#month_day").show();
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
function getcorplist(a,b){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	whir.loading.add("",0.5);
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
			$("#OWN_CORP").append(corp_html,b);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$("#OWN_CORP").searchableSelect();
			var c=$('#corp_select .selected').attr("data-value");
			store_data(c,b);
			$("#corp_select .searchable-select-input").keydown(function(event){
				var event=window.event||arguments[0];
				if(event.keyCode == 13){
					var corp_code=$("#OWN_CORP").val();
					store_data(corp_code,b);
				}
			})
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				store_data(c,b);
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
	});
}
function store_data(c,b){
	var _params={};
	_params["corp_code"]=c;//企业编号
	var _command="/user/store";//调取店铺的名字
	whir.loading.add("",0.5);
	oc.postRequire("post", _command,"", _params, function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
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
	var yearActive=$("#month_input").attr("data-year");
	$('#m_year').html(year+"年");//给年赋值
	$('#m_year').attr("data-type",year);//给年赋值
	var year=year-1;
	$('#week_p .year').empty();
	for(var i=0;i<4;i++){
		year++;
		var active="";
		if(yearActive==year){
			active="active";
		}
		var li="<li data-type='"+year+"' class='"+active+"'>";
		li+=""+year+"年</li>";
		$('#yearnew ul').append(li);
	}
}
function month(){
	var myDate=new Date();
	var month=myDate.getMonth(); //获取当前月份(0-11,0代表1月)
	var year=new Date().getFullYear();
	var year1=$("#year").val();
	month=month+1;
	if(month<10){
		month="0"+month;
	}
	$('#month').val(month);
	var li='';
	for(var i=1;i<=12;i++){
		var style="";
		var active="";
		var a=i;
		if($("#m_year").attr("data-type")==year&&i<month){
			style="opacity:0.5";
		}
		if(i<10){
			a="0"+i;
		}
		if($("#m_year").attr("data-type")==$("#month_input").attr("data-year")&&$("#month_input").attr("data-month")==a){
			active="active";
		}
		li+="<li style='"+style+"' data-type='"+a+"' class='"+active+"'>"+$("#m_year").text()+a+"月</li>";
	}
	$('#monthnew ul').html(li);
}
$("#PER_GOAL").keyup(function () {
	month_day();
})
function month_day(){
	var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
	var type=$("#time_type").val();
	var text=$("#PER_GOAL").val();
	var week=[0,0,0,0,0,0,0];
	var week_achv=$("#week_achv li");
	var achv_bg=$("#achv_bg li");
	var week_parent=$("#week_parent .week_text");
	var year=$("#month_input").attr("data-year");
	var month=$("#month_input").attr("data-month");
	var day=DayNumOfMonth(year,month);
	var d_month=$("#PER_GOAL").val()/day;
	for(var i=1;i<=day;i++){
		if(new Date(year,month-1,i).getDay()=="0"){
			week[0]++;
		}
		if(new Date(year,month-1,i).getDay()=="1"){
			week[1]++;
		}
		if(new Date(year,month-1,i).getDay()=="2"){
			week[2]++;
		}
		if(new Date(year,month-1,i).getDay()=="3"){
			week[3]++;
		}
		if(new Date(year,month-1,i).getDay()=="4"){
			week[4]++;
		}
		if(new Date(year,month-1,i).getDay()=="5"){
			week[5]++;
		}
		if(new Date(year,month-1,i).getDay()=="6"){
			week[6]++;
		}
	}
	var num_total=null;
	for(var i=0;i<week_parent.length;i++){
		num_total+=parseFloat($(week_parent[i]).text())*week[i];
	}
	if(isCode.test(text)&&type=="按月"){
		$("#month_day span").html(d_month.toFixed(2));
		for(var i=0;i<week_achv.length;i++){
			$(week_achv[i]).text("￥"+(($("#PER_GOAL").val()/num_total)*week[i]*parseFloat($(week_parent[i]).text())).toFixed(2));
			$(week_achv[i]).attr("title","￥"+(($("#PER_GOAL").val()/num_total)*week[i]*parseFloat($(week_parent[i]).text())).toFixed(2));
			$(week_achv[i]).attr("data-money",((($("#PER_GOAL").val()/num_total)*week[i]*parseFloat($(week_parent[i]).text())).toFixed(2)/week[i]).toFixed(2));
			$(achv_bg[i]).css({"width":(week[i]*parseFloat($(week_parent[i]).text())/num_total)*100+"%"});
		}
	}else{
		$("#month_day span").html(0.00);
		for(var i=0;i<week_achv.length;i++) {
			$(week_achv[i]).text("￥" + 0);
			$(week_achv[i]).attr("title", "￥" + 0);
			$(week_achv[i]).attr("data-money", "￥" + 0);
			$(achv_bg[i]).css({"width": (week[i] * parseFloat($(week_parent[i]).text()) / num_total) * 100 + "%"});
		}
	}
}
function DayNumOfMonth(Year,Month) {
	Month--;
	var d = new Date(Year,Month,1);
	d.setDate(d.getDate()+32-d.getDate());
	return (32-d.getDate());
}
//DayNumOfMonth(2017,02);
year();
month();