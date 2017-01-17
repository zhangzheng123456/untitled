
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
var oc= new ObjectControl();
var activityPlanning={
	param:{
		tasklist:[],
		task:true,
		group:true,
		card_type:"",
		coupon:""
	},
	init:function(){
		var self=this;
		self.allEvent();
		self.getTaskList();
		self.getPlanningList();
		// if(document.title=="创建活动"){
		// 	this.getPlanningList();
		// }
		if(document.title=="策略补充"){
			self.getTitle();
			// this.getCoupon();
		}
		setTimeout(function(){
			self.lay1();
		},1000);
	},
	allEvent:function(){
		//任务切换
		var self=this;
		$("#task").on("click",".tabs_left ul li",function(){
			console.log(12312);
			console.log(self.modifieTask());
			if(self.modifieTask()!=="成功"){
				return;
			};
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
			self.evaluationTask();
		});
		//下拉框样式
		$(".text_input,.icon_down").click(function(){
			var ul = $(this).siblings('.input_dropdown');
		    if($(ul).find("div").length==0&&$(this).attr("id")=="task_type_code"){
		    	art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先定义任务类型"
                });
		    }
			if(ul.css("display")=="none"){
				ul.show();
			}else{
				ul.hide();
			}
		})
		//失去焦点隐藏div
		$(".text_input").blur(function(){
			var ul = $(this).siblings('.input_dropdown');
			setTimeout(function(){
				ul.hide();
			},200);
		})
		//点击div
		$(".input_dropdown").on("click","div",function(){
			$(this).parent().siblings(".text_input").val($(this).text());
			$(this).parent().siblings(".text_input").attr("data-code",$(this).attr("data-code"));
		})
		//群发管理切换
		$("#group").on("click",".tabs_left ul li",function(){
			var index=$(this).index();
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
			$("#p_task_content .group_parent").eq(index).show();
			$("#p_task_content .group_parent").eq(index).siblings().hide();
		})
		//添加任务
		$("#task_add").on("click",function(){
			self.addTask();
		});
		//删除任务
		$("#task_del").click(function(){
			var index=$("#task_titles li.active").index();//选取选中的下标值
			var prev='';
			if(index==0){
				prev=$("#task_titles li.active").next();
			}else if(index!==0){
				prev=$("#task_titles li.active").prev();
			}
		    var current=self.param.tasklist[index];//获取当前下标为几的值
		    if(current!==undefined){
			    if(current.is_modify=="Y"){
			    	art.dialog({
	                    time: 1,
	                    lock: true,
	                    cancel: false,
	                    content: "不能删除已新增的"
	                });
	                return;
			    }
			}
		    self.param.tasklist.remove(current);
			$("#task_title").val("");
			$("#task_type_code").val("");
			$("#task_description").val("");
			$('#target_start_time').val("");
			$('#target_end_time').val("");
			if(document.title=="创建活动"){
				if($("#task_titles li").length==1){
					return;
				}
			}
			if(document.title=="策略补充"){
				if($("#task_titles li").length==1){
					$("#task_content").hide();
				}
			}
			laydate.reset();
			$("#task_titles li.active").remove();
			$(prev).addClass('active');
			self.evaluationTask();
		});
		//新增一个div
		$(".p_task_content").on("click",'.input_parent .group_add',function(){
			$(this).parents(".add_del").hide();
			var html=$(this).parents('.input_parent').clone();
			$(html).find(".text_input").val("");
			var id=$(html).find(".laydate-icon").attr("id");
			$(html).find(".laydate-icon").attr("id",id+new Date().getTime());
			$(html).attr("is_modify","");
			$(html).find(".text_input").removeAttr("disabled");
			$(html).find(".add_del").show();
			$(html).find(".group_del").show();
			$(html).find(".edit_frame .edit_content").val("");
			$(html).find(".edit_frame .edit_content").removeAttr("disabled");
			$(this).parents('.group_parent').append(html);

		})
		//删除一个div
		$(".p_task_content").on("click",".input_parent .group_del",function(){
			var is_modify=$(this).parents('.input_parent').attr("is_modify");
			if(is_modify=="Y"){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "不能删除已新增的"
                });
                return;
			}
			var index=$(this).parents('.input_parent').prev().index();
			if(index==0){
				console.log(index);
				$(this).parents('.input_parent').prev().find(".group_del").hide();
			}
			$(this).parents('.input_parent').prev().find(".add_del").show();
			$(this).parents('.input_parent').remove();
		})
		$(".p_task_content").on("click",".input_parent .laydate-icon",function(){
			var id=$(this).attr("id");
			self.lay2(id);
		})
		//开关按钮
		$(".switch div").click(function(){
			$(this).toggleClass("bg");
			$(this).find("span").toggleClass("Off");
			var id=$(this).parent().attr("id");
			if(id=="task_switch"){
				if($(this).attr("class")==""){
					self.param.task=false;
					$(this).parent().find(".switch_text").html("任务已关闭");
				}else if($(this).attr("class")=="bg"){
					self.param.task=true;
					$(this).parent().find(".switch_text").html("任务已开启");
				}
				$("#task_parent").slideToggle(200);
			}
			if(id=="group_switch"){
				if($(this).attr("class")==""){
					self.param.group=false;
					$(this).parent().find(".switch_text").html("群发已关闭");
				}else if($(this).attr("class")=="bg"){
					self.param.group=true;
					$(this).parent().find(".switch_text").html("群发已开启");
				}
				$("#group_parent").slideToggle(200);
			}
		});
		//编辑弹框
		$(".p_task_content").on("click",".input_parent .group_edit",function(){
			whir.loading.add("mask",0.5);//加载等待框
			$(this).parents('.input_parent').find(".edit_frame").show();
		})
		//编辑取消
		$(".p_task_content").on("click",".input_parent .edit_footer_close",function(){
			$(this).parents('.input_parent').find(".edit_frame").hide()
			whir.loading.remove('mask');
		});
		//编辑保存
		$(".p_task_content").on("click",".input_parent .edit_footer_save",function(){
			$(this).parents('.input_parent').find(".edit_frame").hide()
			whir.loading.remove('mask');
		})
		//策略补充
		$("#strategy_footer").on("click","ul li",function(){
			var index=$(this).index();
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
			$("#tabs-content .tab_list").eq(index).show();
			$("#tabs-content .tab_list").eq(index).siblings().hide();
		});
		//优惠券切换
		$("#ticket").on("click",".tabs_left ul li",function(){
			var index=$(this).index();
			$(this).addClass("active");
			console.log(123);
			$(this).siblings("li").removeClass("active");
			$("#ticket_content .ticket_content").eq(index).show();
			$("#ticket_content .ticket_content").eq(index).siblings().hide();
		});
		$(".setUp_activity_details").on("click",".select_input",function () {
            $(this).nextAll("ul").toggle();
        });
        $(".setUp_activity_details").on("click","i",function () {
            $(this).nextAll("ul").toggle();
        });
        $(document).click(function (e) {
            if(!($(e.target).is(".icon-ishop_8-02")||$(e.target).is(".select_input"))){
                $(".select_input").nextAll("ul").hide();
            }
        });
        $("#coupon_activity").on("click",".add_btn",function () {//优惠券新增新增
            var clone=$(this).parent().parents("li").clone();
            $(this).parent().hide();
            $(this).parents(".operate_ul").append(clone);
        });
        $("#coupon_activity").on("click",".remove_btn",function () {//优惠券移出
            $(this).parent().parents("li").prev('li').find("li:last-child").show();
            $(this).parent().parents("li").remove();
        });
        $("#coupon_activity").on("click","#coupon_btn",function () {//新增开卡送券
            var html='<div class="coupon_details_wrap"><ul><li style="margin-right: 5px"><label>卡类型</label><input class="text_input select_input" data-code type="text" placeholder="请选择卡类型" readonly="readonly"><i class="icon-ishop_8-02"></i>'
                + '<ul class="activity_select vipCardType">'
                + activityPlanning.param.card_type
                + '</ul></li></ul><ul class="operate_ul">'
                + '<li><ul><li><label>选择优惠券</label><input class="text_input select_input" data-code= type="text" placeholder="请选择优惠券" readonly="readonly"><i class="icon-ishop_8-02"></i>'
                + '<ul class="activity_select coupon_activity">'
                + activityPlanning.param.coupon
                + '</ul></li><li><span class="add_btn">+</span><span class="remove_btn">-</span></li></ul>'
                + '</ul><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#coupon_btn").parent().parent().append(html);
        });
        //点击完成
        $("#complete").click(function(){
        	$.when(self.submitJobStrategy(),self.submitGroupStrategy()).then(function(data1,data2){
               	if(data1=="成功"&&data2=="成功"){
               		$(window.parent.document).find('#iframepage').attr("src","/activity/activity_details.html");
               	}
            });
        })
        //活动列表点击事件
        $(".setUp_activity_details").on("click"," .activity_select li",function () {
            var vue = $(this).html();
            var id = $(this).attr("data-id");
            var couponCode = $(this).attr("data-code");
            if(couponCode!==""||couponCode!==undefined){
                $(this).parent().prevAll("input").attr("data-code",couponCode);
            }
            $(this).parent().prevAll("input").val(vue);
            $(this).parent().prevAll("input").attr("data-id",id);
        });
        //活动列表失去焦点事件
        $(".setUp_activity").on("blur",".select_input",function () {
            var ul = $(this).nextAll("ul");
            setTimeout(function () {
                ul.hide();
            },200);
        });
        $("#coupon_activity").on("click",".coupon_details_close",function () {
            $(this).parents(".coupon_details_wrap").remove();
        });
        //回到活动列表页面
        $("#back_corp_param").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/activity/activity.html");
        });
         //回到跟踪页面
        $("#back_tracking").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/activity/activity_details.html");
        })
        // $(".input_time .laydate-icon").click(function(){
        // 	self.lay1();
        // })
	},
	getCoupon:function () {//拉取优惠券和卡类型
        var corp_code=sessionStorage.getItem("corp_code");
        var param={"corp_code":corp_code};
        oc.postRequire("post","/vipRules/getCoupon","0",param,function (data) {
            if(data.code==0){
                var li="";
                var msg=JSON.parse(data.message);
                if(msg.length==0&&$("#coupon_activity").css("display")=="block"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有优惠券"
                    });
                    activityPlanning.param.coupon="";
                }else if(msg.length>0){
                    for(var i=0;i<msg.length;i++){
                        li+="<li data-code='"+msg[i].couponcode+"'>"+msg[i].name+"</li>"
                    }
                   activityPlanning.param.coupon=li;
                }
                $(".coupon_activity").empty();
                $(".coupon_activity").append(li);
            }
        });
        oc.postRequire("post","/vipCardType/getVipCardTypes","0",param,function (data) {
            if(data.code==0){
                var li="";
                var message=JSON.parse(data.message);
                var msg=JSON.parse(message.list);
                if(msg.length==0&&$("#coupon_activity").css("display")=="block"&&$("#coupon_title li:nth-child(2)").hasClass("coupon_active")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有卡类型"
                    });
                    activityPlanning.param.card_type="";
                }else if(msg.length>0){
                    for(var i=0;i<msg.length;i++){
                        li+="<li data-code='"+msg[i].vip_card_type_code+"'>"+msg[i].vip_card_type_name+"</li>"
                    }
                    activityPlanning.param.card_type=li;
                }
                $(".vipCardType").empty();
                $(".vipCardType").append(li);
            }
        });
    },
	evaluationTask:function(){
		var nextIndex=$("#task_titles li.active").index();
		var nextCurrent=this.param.tasklist[nextIndex];
		console.log(000);
		if(nextCurrent.is_modify=="Y"){
			$("#task_title").attr("disabled",true);
			$("#task_type_code").attr("disabled",true);
			$("#task_type_code").attr("disabled",true);
			$("#task_description").attr("disabled",true);
			$("#target_start_time").attr("disabled",true);
			$("#target_end_time").attr("disabled",true);
			$("#task_link").attr("disabled",true);
		}else if(nextCurrent.is_modify=="N"){
			$("#task_title").removeAttr("disabled");
			$("#task_type_code").removeAttr("disabled");
			$("#task_type_code").removeAttr("disabled");
			$("#task_description").removeAttr("disabled");
			$("#target_start_time").removeAttr("disabled");
			$("#target_end_time").removeAttr("disabled");
			$("#task_link").removeAttr("disabled");
		}
		$("#task_title").val(nextCurrent.task_title);
		$("#task_type_code").val(nextCurrent.task_type_name);
		$("#task_type_code").attr("data-code",nextCurrent.task_type_code);
		$("#task_description").val(nextCurrent.task_description);
		$("#target_start_time").val(nextCurrent.target_start_time);
		$("#target_end_time").val(nextCurrent.target_end_time);
		$("#task_link").val(nextCurrent.task_link);//给任务的input赋值
	},
	getTaskList:function(){
		var param={};
		if(document.title=="创建活动"){//如果是创建活动的页面
			param["corp_code"]=$("#tabs div").eq(0).attr("data-corp");
		}
		if(document.title=="策略补充"){//如果是策略补充的页面
			param["corp_code"]=sessionStorage.getItem("corp_code");
		}
		whir.loading.add("", 0.5);
		oc.postRequire("post","/task/selectAllTaskType", "0",param, function (data) {
			if(data.code=="0"){
				var message = JSON.parse(data.message);
	            var list = JSON.parse(message.list);
	            var html="";
	            if (list.length>0) {
	                for (var i = 0; i < list.length; i++) {
	                    html += '<div data-code="' + list[i].task_type_code + '">' + list[i].task_type_name + '</div>';
	                }
	                $("#task_type_code").siblings('.input_dropdown').html(html);
	            } else if (list.length <= 0) {
	            };
        	}else if(data.code=="-1"){
        		art.dialog({
	                    time: 1,
	                    lock: true,
	                    cancel: false,
	                    content: data.message
	            });
        	}
            whir.loading.remove();//移除加载框
		})//获取任务列表
	},
	modifieTask:function(){//修改选中的任务
		var self=this;
		var result="成功";
		var index=$("#task_titles li.active").index();//选取选中的下标值
		var param=self.checkoutTask();
		if(param==undefined){
			result="失败";
			return;
		}
		var length=self.param.tasklist.length-1;
		if(index>length){
			self.param.tasklist[index]=param;//给当前数组赋值
		}
		if(self.param.tasklist[index].is_modify=="N"){
			self.param.tasklist[index]=param;//给当前数组赋值
		}
		return result;
	},
	checkoutTask:function(){//检查任务不会空的状态
		var task_title=$("#task_title").val();//任务标题
		var target_start_time=$("#target_start_time").val();//开始时间
		var target_end_time=$("#target_end_time").val();//截止时间
		var task_type_code=$("#task_type_code").attr("data-code");//任务类型编号
		var task_type_name=$("#task_type_code").val();//任务类型名称
		var task_description=$("#task_description").val();//任务简述
		var task_link=$("#task_link").val();//任务链接
		var is_modify="N";
		if(task_title==""){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"任务标题不能为空"
			});
			return;
		}
		if(target_start_time==""){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"开始时间不能为空"
			});
			return;
		}
		if(target_end_time==""){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"开始时间不能为空"
			});
			return;
		}
		if(task_type_code==""){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"任务类型不能为空"
			});
			return;
		}
		var param={};
		param["task_title"]=task_title;//任务标题
		param["target_start_time"]=target_start_time;//开始时间
		param["target_end_time"]=target_end_time;//结束时间
		param["task_type_code"]=task_type_code;//任务编号
		param["task_type_name"]=task_type_name;//任务名称
		param["task_description"]=task_description;//任务简述
		param["task_link"]=task_link//链接
		param["is_modify"]=is_modify;
		return param;
	},
	addTask:function(){//添加任务
		console.log(0);
		var self=this;
		$("#task_content").show();
		var length=$("#task_titles li").length+1;
		if(length>1){
			var param=self.checkoutTask();
			if(param==undefined){
				return;
			}
		}
		// if(length>=6){
		// 	art.dialog({
	 //            time: 1,
	 //            lock: true,
	 //            cancel: false,
	 //            content:"添加任务不能超过5个"
	 //        });
		// 	return;
		// }
		$("#task_titles li").removeClass('active');
		if(document.title=="策略补充"){
			$("#task_titles").append("<li class='active'>任务"+length+" *</li>");
			$("#task_title").removeAttr("disabled");
			$("#task_type_code").removeAttr("disabled");
			$("#task_type_code").removeAttr("disabled");
			$("#task_description").removeAttr("disabled");
			$("#target_start_time").removeAttr("disabled");
			$("#target_end_time").removeAttr("disabled");
			$("#task_link").removeAttr("disabled");
		}else if(document.title=="创建活动"){
			$("#task_titles").append("<li class='active'>任务"+length+"</li>");
		}
		self.param.tasklist.push(param);
		$("#task_title").val("");
		$("#task_type_code").val("");
		$("#task_type_code").attr("data-code","");
		$("#task_description").val("");
		$("#target_start_time").val("");
		$("#target_end_time").val("");
		$("#task_link").val("");
	},
	getGroupValue:function(){//获取群发的所有值
		var type=$("#group .tabs_left ul li.active").attr("data-type");
		var index=$("#group .tabs_left ul li.active").index();
		console.log(index);
		var param={};
		var wxlist=[];
		var smslist=[];
		var emlist=[];
	    var wxlistnode=$("#wxlist .input_parent");//微信
	    var smslistnode=$("#smslist .input_parent");//短信
	    var emlistnode=$("#emlist .input_parent");//邮件群发
	    var activity_vip_code=sessionStorage.getItem("activity_code");//活动编号
	    //微信推送
		for(var i=0;i<wxlistnode.length;i++){
			if(document.title=="策略补充"){
				if($(wxlistnode[i]).attr("is_modify")=="Y"){
					continue;
				}	
			}
			var send_time=$(wxlistnode[i]).find(".text_input").val();//发送时间
			// var title=$(wxlistnode[i]).find(".edit_frame .edit_title").val();//推送标题
			// var url=$(wxlistnode[i]).find(".edit_frame .edit_link").val();//页面链接
			// var desc=$(wxlistnode[i]).find(".edit_frame .edit_content").val();//摘要
			var content=$(wxlistnode[i]).find(".edit_frame .edit_content").val();//摘要
			if(send_time!==""&&content!==""){
				var wxparam={"send_time":send_time,"content":content};
				wxlist.push(wxparam);
			}
			// var image="";//封面链接
		}
		//短信群发
		for(var h=0;h<smslistnode.length;h++){
			if(document.title=="策略补充"){
				if($(smslistnode[h]).attr("is_modify")=="Y"){
					continue;
				}	
			}
			var send_time=$(smslistnode[h]).find(".text_input").val();//发送时间
			var content=$(smslistnode[h]).find(".edit_frame .edit_content").val();//短信内容
			if(send_time!==""&&content!==""){
				var smsparam={"send_time":send_time,"content":content};
				smslist.push(smsparam);
			}
			
		}
		//邮件群发
		for(var k=0;k<emlistnode.length;k++){
			if(document.title=="策略补充"){
				if($(emlistnode[k]).attr("is_modify")=="Y"){
					continue;
				}	
			}
			var send_time=$(emlistnode[k]).find(".text_input").val();//发送时间
			var content="";//短信内容
			if(send_time==!""&&content!==""){
				var emparam={"send_time":send_time,"content":content};
				emlist.push(emparam);
			}
		}
		if(wxlist.length=="0"&&smslist.length=="0"&&emlist.length=="0"){
			return;
		}
		console.log(wxlist);
		console.log(smslist);
		param["wxlist"]=wxlist;
		param["smslist"]=smslist;
		param["emlist"]=emlist;
		param["activity_vip_code"]=activity_vip_code;
		return param;
	},
	submitJob:function(){//提交任务
		var self=this;
		var def = $.Deferred();
		var taskparam={};
		if(self.param.task==true){
			var tasklist=self.param.tasklist;
			if(self.modifieTask()!=="成功"){
				def.resolve("失败");
				return;
			};
			if(tasklist.length==0){
				def.resolve("失败");
	   			art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"请先定义任务"
				});
				return;
			}
			taskparam["tasklist"]=tasklist;
			taskparam["task_status"]="Y";
		}
		if(self.param.task==false){
			taskparam["task_status"]="N";
			taskparam["tasklist"]=[];
		}
		taskparam["activity_vip_code"]=sessionStorage.getItem("activity_code");//活动编号
		whir.loading.add("", 0.5);
		oc.postRequire("post","/vipActivity/arrange/addOrUpdateTask","0",taskparam, function (data) {
			if(data.code=="0"){
				def.resolve("成功");
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"添加任务失败"
				});
				def.resolve("失败");
			}
			whir.loading.remove();//移除加载框
		});
		return def;
	},
	submitGroup:function(){//提交群发
		var self=this;
		var param=self.getGroupValue();
		console.log(param);
		var def = $.Deferred();
		if(param==undefined){
			param={};
			param["activity_vip_code"]=sessionStorage.getItem("activity_code");//活动编号
		};
			if(self.param.group==true){
				param["send_status"]="Y";
			}else if(self.param.group==false){
				param["send_status"]="N";
			}
			whir.loading.add("", 0.5);
			oc.postRequire("post","/vipActivity/arrange/addOrUpdateSend","0",param, function (data) {
				if(data.code=="0"){
					def.resolve("成功");
				}else if(data.code=="-1"){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"群发失败"
					});
					def.resolve("失败");
				}
				whir.loading.remove();//移除加载框
			});
		return def;
	},
	getPlanningList:function(){//获取列表信息
		var self=this;
		var param={};
		param["activity_vip_code"]=sessionStorage.getItem("activity_code");
		whir.loading.add("", 0.5);
		oc.postRequire("post","/vipActivity/arrange/list","0",param, function (data) {
			if(data.code=="0"){
				var message=JSON.parse(data.message);;
				console.log(message);
				var wxlist=message.wxlist;
				var smslist=message.smslist;
				var emlist=message.emlist;
				var tasklist=message.tasklist;
				var wxhtml="";
				var smshtml="";
				var emlhtml="";
				var is_modify="";
				var disabled="";
			    if(document.title=="策略补充"){
					is_modify="Y";
					disabled="disabled";
				}
				if(document.title=="创建活动"){
					if(message.task_status=="Y"){
						$("#task_switch div").addClass("bg");
						$("#task_switch div span").addClass("Off");
						$("#task_switch .switch_text").html("任务已开启");
					}
					if(message.task_status=="N"){
						$("#task_switch div").removeClass("bg");
						$("#task_switch div span").removeClass("Off");
						$("#task_switch .switch_text").html("任务已关闭");
						$("#task_parent").hide();
						self.param.task=false;
					}
					if(message.send_status=="Y"){
						$("#group_switch div").addClass("bg");
						$("#group_switch div span").addClass("Off");
						$("#group_switch .switch_text").html("群发已开启");
					}
					if(message.send_status=="N"){
						$("#group_switch div").removeClass("bg");
						$("#group_switch div span").removeClass("Off");
						$("#group_switch .switch_text").html("群发已关闭");
						$("#group_parent").hide(200);
						self.param.group=false;
					}
				}
				if(wxlist.length>0){
					for(var i=0;i<wxlist.length;i++){
						// var content=JSON.parse(wxlist[i].content);
						var addnode="";
						var delnode="";
						if(i==0&&wxlist.length==1){
							addnode="display:block;"
							delnode="display:none;"
						}
						if(i>=0&&i<wxlist.length-1){
							addnode="display:none;"
							delnode="display:block;"
						}
						if(i==wxlist.length-1 && i!==0){
							addnode="display:block;"
							delnode="display:block;"
						}
						wxhtml+="<div class='input_parent' is_modify='"+is_modify+"'>\
							    	<div class='float'>\
										<label>发送时间</label><input id='wxlist"+i+"' "+disabled+" value='"+wxlist[i].send_time+"' type='text' class='text_input laydate-icon' placeholder=''>\
									</div>\
									<div class='float margin_left'>微信推送配置</div>\
									<div class='float group_edit margin_left'>编辑</div>\
									<div class='float add_del' style='"+addnode+"'>\
                    					<div class='group_add'>\
                        					<span class='icon-ishop_6-01'></span>\
                    					</div>\
                    					<div class='group_del' style='"+delnode+"'>\
                        					<span class='icon-ishop_6-02'></span>\
                    					</div>\
                					</div>\
                					<div class='edit_frame edit_message'>\
					                    <div class='tabs_title_p'>\
					                        <div class='tabs_left'>\
					                            <span class='title_icon'></span>\
					                            <span>微信内容设定</span>\
					                        </div>\
					                    </div>\
					                    <div class='edit_frame_left'>\
					                        <label  class='label_frame' style='vertical-align: top;margin-top: 5px;'>微信内容</label><textarea "+disabled+" class='edit_content' placeholder='请输入推送摘要'>"+wxlist[i].content+"</textarea>\
					                    </div>\
					                    <div class='edit_footer'>\
					                        <div class='edit_footer_close'>取消</div>\
					                        <div class='edit_footer_save'>保存</div>\
					                    </div>\
                					</div>\
								</div>"
					};
					$("#wxlist").html(wxhtml);
				}
				if(smslist.length>0){
					for(var i=0;i<smslist.length;i++){
						// var content=JSON.parse(smslist[i].content);
						var addnode="";
						var delnode="";
						if(i==0&&smslist.length==1){
							addnode="display:block;"
							delnode="display:none;"
						}
						if(i>=0&&i<smslist.length-1){
							addnode="display:none;"
							delnode="display:block;"
						}
						if(i==smslist.length-1 && i!==0){
							addnode="display:block;"
							delnode="display:block;"
						}
						smshtml+="<div class='input_parent' is_modify='"+is_modify+"'>\
							    	<div class='float'>\
										<label>发送时间</label><input id='smslist"+i+"' "+disabled+" value='"+smslist[i].send_time+"' type='text' class='text_input laydate-icon' placeholder=''>\
									</div>\
									<div class='float margin_left'>短信内容设定</div>\
									<div class='float group_edit margin_left'>编辑</div>\
									<div class='float add_del' style='"+addnode+"'>\
                    					<div class='group_add'>\
                        					<span class='icon-ishop_6-01'></span>\
                    					</div>\
                    					<div class='group_del' style='"+delnode+"'>\
                        					<span class='icon-ishop_6-02'></span>\
                    					</div>\
                					</div>\
                					<div class='edit_frame edit_message'>\
					                    <div class='tabs_title_p'>\
					                        <div class='tabs_left'>\
					                            <span class='title_icon'></span>\
					                            <span>短信内容</span>\
					                        </div>\
					                    </div>\
					                    <div class='edit_frame_left'>\
					                        <label  class='label_frame' style='vertical-align: top;margin-top: 5px;'>短信内容</label><textarea class='edit_content' "+disabled+" placeholder='请输入推送摘要'>"+smslist[i].content+"</textarea>\
					                    </div>\
					                    <div class='edit_footer'>\
					                        <div class='edit_footer_close'>取消</div>\
					                        <div class='edit_footer_save'>保存</div>\
					                    </div>\
                					</div>\
								</div>"
					};
					$("#smslist").html(smshtml);
				}
				if(emlist.length>0){
					for(var i=0;i<emlist.length;i++){
						var addnode="";
						var delnode="";
						if(i==0&&emlist.length==1){
							addnode="display:block;"
							delnode="display:none;"
						}
						if(i>=0&&i<emlist.length-1){
							addnode="display:none;"
							delnode="display:block;"
						}
						if(i==emlist.length-1 && i!==0){
							addnode="display:block;"
							delnode="display:block;"
						}
						emlhtml+="<div class='input_parent' is_modify='"+is_modify+"'>\
							    	<div class='float'>\
										<label>发送时间</label><input id='emlhtml"+i+"' "+disabled+" value='"+emlist[i].send_time+"' type='text' class='text_input laydate-icon' placeholder=''>\
									</div>\
									<div class='float margin_left'>邮件内容设定</div>\
									<div class='float group_edit margin_left'>编辑</div>\
									<div class='float add_del' style='"+addnode+"'>\
                    					<div class='group_add'>\
                        					<span class='icon-ishop_6-01'></span>\
                    					</div>\
                    					<div class='group_del' style='"+delnode+"'>\
                        					<span class='icon-ishop_6-02'></span>\
                    					</div>\
                					</div>\
                					<div class='edit_frame edit_message'>\
					                    <div class='tabs_title_p'>\
					                        <div class='tabs_left'>\
					                            <span class='title_icon'></span>\
					                            <span>邮件内容设定</span>\
					                        </div>\
					                    </div>\
					                    <div class='edit_frame_left'>\
					                        <label  class='label_frame' style='vertical-align: top;margin-top: 5px;'>邮件内容设定</label><textarea "+disabled+" class='edit_content' placeholder='请输入推送摘要'>"+emlist[i].content+"</textarea>\
					                    </div>\
					                    <div class='edit_footer'>\
					                        <div class='edit_footer_close'>取消</div>\
					                        <div class='edit_footer_save'>保存修改</div>\
					                    </div>\
                					</div>\
								</div>"
					};
					$("#emlist").html(emlhtml);
				}
				if(tasklist.length>0){
					var html="";
					for(var i=0;i<tasklist.length;i++){
						var a=i+1;
						var taskparam={};
						console.log(tasklist);
						console.log(tasklist[i].task_type_code);
						taskparam["task_title"]=tasklist[i].task_title;//任务标题
						taskparam["target_start_time"]=tasklist[i].target_start_time;//开始时间
						taskparam["target_end_time"]=tasklist[i].target_end_time;//结束时间
						taskparam["task_type_code"]=tasklist[i].task_type_code;//任务编号
						taskparam["task_type_name"]=tasklist[i].task_type_name;//任务名称
						taskparam["task_description"]=tasklist[i].task_description;//任务简述
						taskparam["task_link"]=tasklist[i].task_link//链接
						if(document.title=="策略补充"){
							taskparam["is_modify"]="Y";
						}else if(document.title=="新增活动"){
							taskparam["is_modify"]="N";
						}
						self.param.tasklist.push(taskparam);
						html+="<li>任务"+a+"</li>";
						
					}
					$("#task_content").show();
					$("#task_titles").html(html);
					$("#task_titles li").eq(0).addClass("active");
					self.evaluationTask();
				}
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:data.message
				});
			}
			whir.loading.remove();//移除加载框
		});
	},
	getTitle:function(){//策略补充的
		var param={};
		var activity_vip_code=sessionStorage.getItem("activity_code");//活动编号
		param["activity_code"]=activity_vip_code;
		oc.postRequire("post","/vipActivity/select","0",param, function (data) {
			var message=JSON.parse(data.message);
			console.log(message);
			var activityVip=JSON.parse(message.activityVip);
			console.log(activityVip);
			$("#activity_theme").html(activityVip.activity_theme);
			$("#time").html(activityVip.start_time+"至"+activityVip.end_time);
			$("#corp_name").html(activityVip.run_mode+"&nbsp;|&nbsp;"+activityVip.corp_name);
		})
	},
	submitJobStrategy:function(){
		var self=this;
		var def = $.Deferred();
		var taskparam = {};
		var tasklist = self.param.tasklist;
		// if(self.modifieTask()!=="成功"){
		// 	def.resolve("失败");
		// 	return;
		// };
		if($("#task_titles li").length>0){
			if(self.modifieTask() !== "成功"){
				def.resolve("失败");
				return;
			}
		};
		var tasklistStrategy=[];
		for(var i=0;i<tasklist.length;i++){
			if(tasklist[i].is_modify!=="Y"){
				tasklistStrategy.push(tasklist[i]);
			}
		}
		if (tasklistStrategy.length == 0) {
			def.resolve("成功");
		}else if(tasklistStrategy.length>0){
			taskparam["tasklist"] = tasklistStrategy;
			taskparam["activity_vip_code"]=sessionStorage.getItem("activity_code");//活动编号
			whir.loading.add("", 0.5);
			oc.postRequire("post","/vipActivity/arrange/addStrategyByTask","0",taskparam, function (data) {
				if(data.code=="0"){
					def.resolve("成功");
				}else if(data.code=="-1"){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"添加任务失败"
					});
					def.resolve("失败");
				}
				whir.loading.remove();//移除加载框
			});
		}
		return def;
	},
	submitGroupStrategy:function(){
		var self=this;
		var param=self.getGroupValue();
		var def = $.Deferred();
		if(param==undefined){
			def.resolve("成功");
		};
		if(param!==undefined){
			whir.loading.add("", 0.5);
			oc.postRequire("post","/vipActivity/arrange/addStrategyBySend","0",param, function (data) {
				if(data.code=="0"){
					def.resolve("成功");
				}else if(data.code=="-1"){
					setTimeout(function(){
						art.dialog({
							time: 1,
							lock: true,
							cancel: false,
							content:"群发失败"
						});
						def.resolve("失败");
					},1500);
				}
				whir.loading.remove();//移除加载框
			});
		}
		return def;
	},
	lay1:function(){//定义日期格式
		var start = {
			elem:"#target_start_time",
			format: 'YYYY-MM-DD',
			min: laydate.now(),
			max: '2099-06-16 23:59:59',
			istime: true,
			istoday: false,
			choose: function(datas) {
				end.min = datas; //开始日选好后，重置结束日的最小日期
        		end.start = datas //将结束日的初始值设定为开始日
			}
		};
		var end = {
		    elem: '#target_end_time',
		    format: 'YYYY-MM-DD',
		    min: laydate.now(),
		    max: '2099-06-16 23:59:59',
		    istime: false,
		    istoday: false,
		    choose: function (datas) {
		    	console.log(datas);
		        start.max = datas; //结束日选好后，重置开始日的最大日期
		    }
		};
		laydate(start);
		laydate(end);
		console.log(0123123);
	},
	getNowFormatDate:function () {//获取当前日期
		var date = new Date();
		var seperator1 = "-";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var strDate = date.getDate() + 1;
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var currentdate = year + seperator1 + month + seperator1 + strDate;
		return currentdate
	},
	lay2:function(InputID){
		console.log(InputID);
		var time=this.getNowFormatDate();
		var str=time+"  00:00:00";
		console.log(str);
		var time = {
			elem:"#"+InputID,
			format: 'YYYY-MM-DD hh:mm:ss',
			min:str, //最大日期
			istime: true,
			istoday: false,
			choose: function(datas) {
				time.min=str;
			}
		};
		laydate(time);
	}
}
$(function(){
	activityPlanning.init();
})
function checkStart(data){
    $("#target_end_time").attr("onclick","laydate({elem:'#target_end_time',min:'"+data+"',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:checkEnd})");
};
function checkEnd(data){
    $("#target_start_time").attr("onclick","laydate({elem:'#target_start_time',min:'1900-01-01 00:00:00',max: '"+data+"',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:checkStart})");
};