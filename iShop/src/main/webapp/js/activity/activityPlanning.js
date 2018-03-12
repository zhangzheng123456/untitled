
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
var oc= new ObjectControl();
var _param = {};
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var store_num=1;
var store_next=false;
var isscroll=false;
var select_type="";
var activityPlanning={
	param:{
		tasklist:[],
		task:true,
		group:true,
		card_type:"",
		coupon:""
	},
    cache:{
        clickMark:"",//员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "user_codes":"",
        "user_names":"",
        "group_codes":"",
        "group_name":""
    },
	init:function(){
		var self=this;
		self.allEvent();
		self.getTaskList();
		self.getPlanningList();
		self.filterEvent();
		// if(document.title=="创建活动"){
		// 	this.getPlanningList();
		// }
		if(document.title=="策略补充"){
			self.getTitle();
			// this.getCoupon();
		}
		setTimeout(function(){
			// self.lay1();
		},1000);
	},
	allEvent:function(){
		//任务切换
		var self=this;
		$("#task").on("click",".tabs_left ul li",function(){
			if(self.modifieTask()!=="成功"){
				return;
			}
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
		});
		//失去焦点隐藏div
		$(".text_input").blur(function(){
			var ul = $(this).siblings('.input_dropdown');
			setTimeout(function(){
				ul.hide();
			},200);
		});
		$("#group_parent").on("blur",".wxUrl",function () {
            var reg = /^((https|http)?:\/\/)[^\s]+/;
            var val=$(this).val();
            if(!reg.test(val) && val!==""){
                art.dialog({
                    time: 1,
                    lock: true,
					zIndex:10002,
                    cancel: false,
                    content: "请填写正确的网页地址并且以“http://”开头"
                });
                $(this).val("");
                return ;
            }
        });
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
			$("#task_type_code").attr("data-code","");
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
			$("#task_titles li.active").remove();
			$(prev).addClass('active');
			self.evaluationTask();
		});
		//新增一个div
		$(".p_task_content").on("click",'.input_parent .group_add',function(){
			$(this).hide();
			var html=$(this).parents('.input_parent').clone();
            $(this).parents('.input_parent').find(".group_del").css("margin-left","0");
			$(html).find(".text_input").val("");
			var id=$(html).find(".laydate-icon").attr("id");
			$(html).find(".laydate-icon").attr("id",id+new Date().getTime());
			$(html).attr("is_modify","");
			$(html).find(".text_input").removeAttr("disabled");
			$(html).find(".group_add").show();
			$(html).find(".group_del").show();
            $(html).find(".edit_frame").attr("data-msg","");
			$(html).find(".edit_frame .wxTitle").val("");
            $(html).find(".edit_frame .wxUrl").val("");
            $(html).find(".edit_frame .imgInput").val("");
            $(html).find(".edit_frame .imgBox img").remove();
            $(html).find(".edit_frame .edit_content").val("");
			$(html).find(".edit_frame .edit_content").removeAttr("disabled");
			$(html).find(".edit_frame .edit_content").attr("data-content","");
			$(html).find(".vip_num b").html("<img src='../img/loading-upload.gif'>");
			$(this).parents('.group_parent').append(html);
            $(html).prev(".input_parent").find(".group_del").show();
			$(html).find(".chooseVipBtn").attr("data-screen","[]").attr("data-screen-type","trans").attr("data-select_type","content_basic").removeClass("currentSelect").removeClass("canNotSelectShow");
            self.uploadOSS();
			getSendVipCount([],"trans").then(function(num){
				$(html).find(".vip_num b").html(num);
			})
		});
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
			// var index=$(this).parents('.input_parent').prev().index();
			// if(index==0){
			// 	$(this).parents('.input_parent').prev().find(".group_del").hide();
			// }
			var html = $(this).parents('.group_parent');
			$(this).parents('.input_parent').prev().find(".add_del").show();
			$(this).parents('.input_parent').remove();
            $(html).find(".input_parent").length == 1 ?$(html).find(".input_parent .group_del").hide():"";
            $(html).find(".input_parent:last-child .group_add").show();
            $(html).find(".input_parent:last-child .group_del").css("margin-left","6px");
		});
		$("#p_task_content").on("click",".input_parent .laydate-icon",function(){
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
				$("#chooseVipWrap").slideToggle(200);
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
			var is_modify=$(this).parents('.input_parent').attr("is_modify");
			// if(is_modify!=="Y"){
			// 	var content=$(this).parents('.input_parent').find(".edit_content").attr("data-content");
			// 	$(this).parents('.input_parent').find(".edit_content").val(content);
			// }
			whir.loading.remove('mask');
		});
		//编辑保存
		$(".p_task_content").on("click",".input_parent .edit_footer_save",function(){
			var id = $(this).parents(".group_parent").attr("id");
            var title=$(this).parents(".edit_frame").find(".wxTitle").val();//推送标题
            var url=$(this).parents(".edit_frame").find(".wxUrl").val();//页面链接
            var img=$(this).parents(".edit_frame").find(".imgBox img").attr("src");//图片
            var info_content=$(this).parents(".edit_frame").find(".edit_content").val();//内容
			if(id=="wxlist"&&(title==""||info_content==""||img==""||img==undefined)){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
					zIndex:10002,
                    content:"请将群发信息填写完整"
                });
                return
			}
            var content = {
                "title":title,
                "info_content":info_content,
                "details_url":url,
                "picture_url":img
            };
			$(this).parents('.input_parent').find(".edit_frame").hide();
			$(this).parents('.edit_frame').attr("data-msg",JSON.stringify(content));
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
			$(this).siblings("li").removeClass("active");
			$("#ticket_content .ticket_content").eq(index).show();
			$("#ticket_content .ticket_content").eq(index).siblings().hide();
		});
		$(".setUp_activity_details").on("click",".select_input",function (vv) {
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
        });
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
		console.log(nextCurrent.is_modify);
		if(nextCurrent.is_modify=="Y"){
			$("#task_title").attr("disabled",true);
			$("#task_type_code").attr("disabled",true);
			$("#task_type_code").attr("disabled",true);
			$("#task_description").attr("disabled",true);
			$("#target_start_time").attr("disabled",true);
			$("#target_start_time").css("background","#e8e8e8");
			$("#target_end_time").css("background","#e8e8e8");
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
			$("#target_start_time").css("background","");
			$("#target_end_time").css("background","");
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
				content:"结束时间不能为空"
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
		var self=this;
		$("#task_content").show();
		var length=$("#task_titles li").length+1;
		if(length>=11){
			art.dialog({
	            time: 1,
	            lock: true,
	            cancel: false,
	            content:"添加任务总数不能超过10个"
	        });
			return;
		}
		var index=$("#task_titles li.active").index();//选取选中的下标值
		if(length>1){
			var param=self.checkoutTask();
			if(param==undefined){
				return;
			}
			var arrLength=self.param.tasklist.length;
			if(index<arrLength){
				if(self.param.tasklist[index].is_modify=="N"){
					self.param.tasklist[index]=param;//给当前数组赋值
				}else if(self.param.tasklist[index].is_modify!=="N"){
					self.param.tasklist.push(param);
				}
			}else if(index>=arrLength){
				self.param.tasklist.push(param);
			}
		}
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
			$("#target_start_time").css("background","");
			$("#target_end_time").css("background","");
		}else if(document.title=="创建活动"){
			$("#task_titles").append("<li class='active'>任务"+length+"</li>");
		}
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
            var content = $(wxlistnode[i]).find(".edit_frame").attr("data-msg");//发送时间
			if(send_time!=="" && content!=undefined && content!=""){
				var screen=[];
				$(wxlistnode[i]).find(".chooseVipBtn").attr("data-screen")? screen =$(wxlistnode[i]).find(".chooseVipBtn").attr("data-screen") : screen=[];
				var wxparam={
					"send_time":send_time,
					"content":content,
					"screen":screen,
					"select_type":$(wxlistnode[i]).find(".chooseVipBtn").attr("data-select_type")
				};
				wxlist.push(wxparam);
			}
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
				var screen=[];
				$(smslistnode[h]).find(".chooseVipBtn").attr("data-screen")? screen =$(smslistnode[h]).find(".chooseVipBtn").attr("data-screen") : screen=[];
				var smsparam={
					"send_time":send_time,
					"content":content,
					"screen":screen,
					"select_type":$(smslistnode[h]).find(".chooseVipBtn").attr("data-select_type")
				};
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
		//var screen = [];
		//_param.screen ? screen = _param.screen : "";
		param["wxlist"]=wxlist;
		param["smslist"]=smslist;
		param["emlist"]=emlist;
		param["activity_vip_code"]=activity_vip_code;
		//param["screen"]=screen;
		//param["select_type"]=select_type;
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
			}
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
					content:data.message
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
		var def = $.Deferred();
		if(param==undefined&&self.param.group==false){
			param={};
			param["activity_vip_code"]=sessionStorage.getItem("activity_code");//活动编号
		}
		if(param==undefined&&self.param.group==true){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"请将群发信息填写完整"
				});
			def.resolve("失败");
		}else{
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
						content:data.message
					});
					def.resolve("失败");
				}
				whir.loading.remove();//移除加载框
			});
		}
		return def;
	},
	getPlanningList:function(){//获取列表信息
		var self=this;
		var param={};
		param["activity_vip_code"]=sessionStorage.getItem("activity_code");
		whir.loading.add("", 0.5);
		oc.postRequire("post","/vipActivity/arrange/list","0",param, function (data) {
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var wxlist=message.wxlist;
				var smslist=message.smslist;
				var emlist=message.emlist;
				var tasklist=message.tasklist;
				var wxhtml="";
				var smshtml="";
				var emlhtml="";
				var is_modify="";
				var disabled="";
				var canNotSelectShow="";
				select_type=message.select_type;
			    if(document.title=="策略补充"){
					is_modify="Y";
					disabled="disabled";
					canNotSelectShow=" canNotSelectShow"
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
						$("#chooseVipWrap").hide();
						$("#group_parent").hide(200);
						self.param.group=false;
					}
				}
				if(wxlist.length>0){
                    $("#lookCondition").attr("data-code",wxlist[0].sms_vips);
					//_param.screen = JSON.parse(wxlist[0].sms_vips);
					for(var i=0;i<wxlist.length;i++){
						var list=JSON.parse(wxlist[i].content);
						var addnode="";
						var delnode="";
						var left = "";
						if(i==0&&wxlist.length==1){
							addnode="display:block;"
							delnode="display:none;"
						}
						if(i>=0&&i<wxlist.length-1){
							addnode="display:none;";
							delnode="display:block;margin:0"
						}
						if(i==wxlist.length-1 && i!==0){
							addnode="display:block;"
							delnode="display:block;"
						}
						wxhtml+="<div class='input_parent' is_modify='"+is_modify+"'>\
							<div class='chooseVipWrap'>"+
							"<label>选择会员</label><span class='chooseVipBtn"+canNotSelectShow+"' data-screen_type='noTrans' data-screen='"+wxlist[i].sms_vips+"' data-select_type='"+wxlist[i].select_type+"'>选择会员</span>\
							<span class='vip_num' style='display: inline-block;width: 120px'>已选择 <b><img src='../img/loading-upload.gif'></b>人</span>\
							<span class='lookCondition'>查看已选<i class='icon-ishop_8-03'></i></span>\
							</div>\
							    	<div class='float'>\
										<label style='color:#c26555;'>发送时间*</label><input id='wxlist"+i+"' "+disabled+" value='"+wxlist[i].send_time+"' type='text' class='text_input laydate-icon' placeholder=''>\
									</div>\
									<div class='float margin_left'>微信推送配置</div>\
									<div class='float group_edit margin_left'>编辑</div>\
									<div class='float add_del' >\
                    					<div class='group_add' style='"+addnode+"'>\
                        					<span class='icon-ishop_6-01'></span>\
                    					</div>\
                    					<div class='group_del' style='"+delnode+"'>\
                        					<span class='icon-ishop_6-02'></span>\
                    					</div>\
                					</div>\
                					<div class='edit_frame edit_message' data-msg='"+wxlist[i].content+"'>\
					                    <div class='tabs_title_p'>\
					                        <div class='tabs_left'>\
					                            <span class='title_icon'></span>\
					                            <span>微信内容设定</span>\
					                        </div>\
					                    </div>\
					                    <div class='edit_frame_left'>\
					                    	<span class='message_label'>推送标题*</span><input class='wxTitle' type='text' value="+list.title+">\
                        					<span class='message_label' style='vertical-align: top'>消息内容*</span><textarea class='edit_content' placeholder='请输入微信内容' maxlength='140'>"+list.info_content+"</textarea>\
                            				<div style='color: #888;padding-left:65px;word-break:break-all;font-size: 12px;margin-bottom: 10px;line-height: 20px;'>标记说明：\"#store#\" 店铺名；\"#name#\" 会员名；\"#birthday#\" 会员生日；\"#join_time#\" 加入时间；\"#sex#\" 会员性别</div>\
                        					<span style='color: #888'>详情链接</span><input placeholder='链接以http://或https://开头' class='wxUrl' type='text' value="+list.details_url+">\
                            				<span class='imgUploadWrap'><span class='message_label'>推送图片*</span><span class='imgBox'><img src="+list.picture_url+"></span><input class='imgInput' type='file'><span class='imgBtn'>上传图片</span></span>\
					                    </div>\
					                    <div class='edit_footer'>\
					                        <div class='edit_footer_close'>取消</div>\
					                        <div class='edit_footer_save'>保存</div>\
					                    </div>\
                					</div>\
								</div>"
					}
					$("#wxlist").html(wxhtml);
					var chooseVipBtn=$("#wxlist .input_parent .chooseVipBtn");
					chooseVipBtn.map(function(i,btn){
						var screen=JSON.parse($(btn).attr("data-screen"));
						var screen_type=$(btn).attr("data-screen_type");
						getSendVipCount(screen,screen_type).then(function(num){
							$(btn).next(".vip_num").find("b").html(num)
						})
					})
				}
				if(smslist.length>0){
					//_param.screen = JSON.parse(smslist[0].sms_vips);
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
							delnode="display:block;margin:0"
						}
						if(i==smslist.length-1 && i!==0){
							addnode="display:block;"
							delnode="display:block;"
						}
						smshtml+="<div class='input_parent' is_modify='"+is_modify+"'>\
						 <div class='chooseVipWrap'>"+
							"<label>选择会员</label><span class='chooseVipBtn"+canNotSelectShow+"' data-screen_type='noTrans' data-screen='"+smslist[i].sms_vips+"' data-select_type='"+smslist[i].select_type+"'>选择会员</span>\
							<span class='vip_num' style='display: inline-block;width: 120px'>已选择 <b><img src='../img/loading-upload.gif'></b>人</span>\
							<span class='lookCondition'>查看已选<i class='icon-ishop_8-03'></i></span>\
							</div>\
							    	<div class='float'>\
										<label style='color:#c26555;'>发送时间*</label><input id='smslist"+i+"' "+disabled+" value='"+smslist[i].send_time+"' type='text' class='text_input laydate-icon' placeholder=''>\
									</div>\
									<div class='float margin_left'>短信内容设定</div>\
									<div class='float group_edit margin_left'>编辑</div>\
									<div class='float add_del' >\
                    					<div class='group_add' style='"+addnode+"'>\
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
					                        <label  class='label_frame' style='vertical-align: top;margin-top: 5px;color:#c26555;'>短信内容*</label><textarea class='edit_content' "+disabled+" placeholder='请输入短信内容' data-content="+smslist[i].content+">"+smslist[i].content+"</textarea>\
					                    </div>\
					                    <div class='edit_footer'>\
					                        <div class='edit_footer_close'>取消</div>\
					                        <div class='edit_footer_save'>保存</div>\
					                    </div>\
                					</div>\
								</div>"
					};
					$("#smslist").html(smshtml);
					var chooseVipBtn=$("#smslist .input_parent .chooseVipBtn");
					chooseVipBtn.map(function(i,btn){
						var screen=JSON.parse($(btn).attr("data-screen"));
						var screen_type=$(btn).attr("data-screen_type");
						getSendVipCount(screen,screen_type).then(function(num){
							$(btn).next(".vip_num").find("b").html(num)
						})
					})
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
					                        <label  class='label_frame' style='vertical-align: top;margin-top: 5px;'>邮件内容设定</label><textarea "+disabled+" class='edit_content' placeholder='请输入推送摘要' data-content="+emlist[i].content+">"+emlist[i].content+"</textarea>\
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
						taskparam["task_title"]=tasklist[i].task_title;//任务标题
						taskparam["target_start_time"]=tasklist[i].target_start_time;//开始时间
						taskparam["target_end_time"]=tasklist[i].target_end_time;//结束时间
						taskparam["task_type_code"]=tasklist[i].task_type_code;//任务编号
						taskparam["task_type_name"]=tasklist[i].task_type_name;//任务名称
						taskparam["task_description"]=tasklist[i].task_description;//任务简述
						taskparam["task_link"]=tasklist[i].task_link//链接
						if(document.title=="策略补充"){
							taskparam["is_modify"]="Y";
						}else if(document.title=="创建活动"){
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
                self.uploadOSS();

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
			var activityVip=JSON.parse(message.activityVip);
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
						content:data.message
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
		}
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
							content:data.message
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
		laydate.reset(); 
		var start = {
			elem:"#target_start_time",
			format: 'YYYY-MM-DD',
			min: laydate.now(),
			max: '2099-06-16 23:59:59',
			istime: false,
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
		        // start.max = datas; //结束日选好后，重置开始日的最大日期
		    }
		};
		laydate(start);
		laydate(end);
	},
	getNowFormatDate:function () {//获取当前日期
		var date = new Date();
        var year = date.getFullYear();
        var H=date.getHours();
        var M=date.getMinutes();
        var S=date.getSeconds();
        var m=date.getMilliseconds();
	    var seperator1 = "-";
	    var seperator2 = ":";
	    var month = date.getMonth() + 1;
	    var strDate = date.getDate();
	    if (month >= 1 && month <= 9) {
	        month = "0" + month;
	    }
	    if (strDate >= 0 && strDate <= 9) {
	        strDate = "0" + strDate;
	    }
	    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
	    	 + " " + date.getHours() + seperator2 + date.getMinutes()
             + seperator2 + date.getSeconds();
        var currentdates = ""+year+month+strDate+H+M+S+m;
    	return [currentdate,currentdates];
	},
	lay2:function(InputID){
		var str=this.getNowFormatDate()[0];
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
	},
    uploadOSS:function () {//上传logo OSS
        var _this=this;
        var client = new OSS.Wrapper({
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            bucket: 'products-image'
        });
        var imgInput =  document.getElementsByClassName('imgInput');
        for(var i=0;i<imgInput.length;i++){
            imgInput[i].addEventListener('change', function (e) {
                var file = e.target.files[0];
                var time=_this.getNowFormatDate()[1];
                var corp_code=sessionStorage.getItem("corp_code");
                var fileSize=0;
                var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
                if (isIE) {
                    var filePath = e.value;
                    var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                    var files = fileSystem.GetFile (filePath);
                    fileSize = files.Size;
                }else {
                    fileSize = e.target.files[0].size;
                }
                if(fileSize/1024/1024>5){//限制大小为5M
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
						zIndex:10010,
                        content: "请上传小于5M的图片"
                    });
                    $(this).val("");
                    return ;
                }
                var wrap = $(this).prev(".imgBox");
                whir.loading.add("上传中,请稍后...",0.5);
                $("#mask").css("z-index","10002");
                var storeAs='Album/Vip/activity/'+corp_code+'_'+'_'+time+'.jpg';
                client.multipartUpload(storeAs, file).then(function (result) {
                    var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
                    $(this).val("");
                    _this.addLogo(url,wrap);
                }).catch(function (err) {
                    console.log(err);
                });
            });
		}
    },
    addLogo:function (url,wrap) {//添加logo节点
        var img="<img src='"+url+"'>";
        $(wrap).html(img);
        whir.loading.remove();
    },
    getarealist:function(a){//获取店铺群组
		var self = this ;
        var searchValue=$("#area_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param = {};
        _param['corp_code']=sessionStorage.getItem("corp_code");
        _param["searchValue"]=searchValue;
        _param["pageSize"]=pageSize;
        _param["pageNumber"]=pageNumber;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", "/area/selAreaByCorpCode", "", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var total = list.total;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var area_html_left ='';
                $("#screen_area .s_pitch span").eq(1).text(total);
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
                    }
                }
                if(hasNextPage==true){
                    area_num++;
                    area_next=false;
                }
                if(hasNextPage==false){
                    area_next=true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if(!isscroll){
                    $("#screen_area .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(area_next){
                                return;
                            }
                            self.getarealist(area_num);
                        }
                    });
                }
                isscroll=true;
                var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    getstafflist:function (a) {//获取导购方法
		var self = this;
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        var searchValue=$('#staff_search').val().trim();
        _param['area_code']=self.cache.area_codes;
        _param['brand_code']=self.cache.brand_codes;
        _param['store_code']=self.cache.store_codes;
        _param['searchValue']=searchValue;
        _param["corp_code"]=sessionStorage.getItem("corp_code");
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
            if (data.code == "0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var total = list.total+1;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var staff_html = '';
                $("#screen_staff .s_pitch span").eq(1).text(total);
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                    }
                }
                if(hasNextPage==true){
                    staff_num++;
                    staff_next=false;
                }
                if(hasNextPage==false){
                    staff_next=true;
                }
                if(a==1&&searchValue==""){
                    $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
                }
                $("#screen_staff .screen_content_l ul").append(staff_html);
                if(!isscroll){
                    $("#screen_staff .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(staff_next){
                                return;
                            }
                            self.getstafflist(staff_num);
                        }
                    })
                }
                isscroll=true;
                var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                console.log(data.message);
            }
        })
    },
    getbrandlist:function () {
        var searchValue=$("#brand_search").val().trim();
        var _param={};
        _param['corp_code']=sessionStorage.getItem("corp_code");
        _param["searchValue"]=searchValue;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/brand", "",_param, function(data){
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var total = message.total;
                var list=message.brands;
                var brand_html_left = '';
                $("#screen_brand .s_pitch span").eq(1).text(total);
                if (list.length == 0){
                    for(var h=0;h<9;h++){
                        brand_html_left+="<li></li>"
                    }
                } else {
                    if(list.length<9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                        for(var j=0;j<9-list.length;j++){
                            brand_html_left+="<li></li>"
                        }
                    }else if(list.length>=9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                    }
                }
                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    getstorelist:function (a) {
    	var self = this;
        var searchValue=$("#store_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        _param['corp_code']=sessionStorage.getItem("corp_code");
        _param['area_code']=self.cache.area_codes;
        _param['brand_code']=self.cache.brand_codes;
        _param['searchValue']=searchValue;
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var total = list.total;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var store_html = '';
                $("#screen_shop .s_pitch span").eq(1).text(total);
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
                    }
                }
                if(hasNextPage==true){
                    shop_num++;
                    shop_next=false;
                }
                if(hasNextPage==false){
                    shop_next=true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if(!isscroll){
                    $("#screen_shop .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(shop_next){
                                return;
                            }
                            self.getstorelist(shop_num);
                        }
                    })
                }
                isscroll=true;
                var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    getGroup:function () {
        var _param = {};
        _param["corp_code"] = sessionStorage.getItem("corp_code");
        _param["search_value"] = $("#task_group_search").val().trim();
        oc.postRequire("post", "/vipGroup/getCorpGroups", "0", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var total = message.total;
                var list = JSON.parse(message.list);
                var html = "";
                $("#screen_group .s_pitch span").eq(1).text(total);
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html+="<li><div class='checkbox1'><input  data-type='"+list[i].group_type+"' type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + "'/><label for='checkboxOneInput"
                            + i
                            + "'></label></div><span class='p16'>"+list[i].vip_group_name+"</span></li>"
                    }
                    $("#screen_group .screen_content_l ul").append(html);
                }
            }
        })
    },
    getexpend:function () {
        var corp_code = sessionStorage.getItem("corp_code");
        var param = {"corp_code":corp_code};
        oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
            if(data.code == 0){
                $("#expend_attribute").empty();
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var param = "";
                var html="";
                var simple_html="";
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        var param_type = list[i].param_type;
                        if(param_type=="date"){
                            simple_html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                            html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                        }
                        if(param_type=="select"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li>'+param_values[j]+'</li>'
                                }
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                        if(param_type=="text"){
                            html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="longtext"){
                            html+='<div class="textarea"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="check"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li class="type_check">'
                                        +'<input type="checkbox" style="vertical-align:middle;width: 15px;height: 15px;margin: 0"/>'
                                        +'<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+param_values[j]+'">'+param_values[j]+'</span>'
                                        +'</li>'
                                }
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                    }
                }
                $("#expend_attribute").html(html);
                $("#memorial_day").html(simple_html);
            }else if(data.code == -1){
                console.log(data.message);
            }
        });
    },
    filterEvent:function () {
        var self=this;
        ////店铺里面的区域点击
        //$("#shop_area").click(function(){
        //    if(self.cache.area_codes!==""){
        //        var area_codes=self.cache.area_codes.split(',');
        //        var area_names=self.cache.area_names.split(',');
        //        var area_html_right="";
        //        for(var h=0;h<area_codes.length;h++){
        //            area_html_right+="<li id='"+area_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+area_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_area .s_pitch span").eq(0).html(h);
        //        $("#screen_area .screen_content_r ul").html(area_html_right);
        //    }else{
        //        $("#screen_area .s_pitch span").eq(0).html("0");
        //        $("#screen_area .screen_content_r ul").empty();
        //    }
        //    isscroll=false;
        //    area_num=1;
        //    $("#screen_area .screen_content_l").unbind("scroll");
        //    $("#screen_area .screen_content_l ul").empty();
        //    $("#screen_area").show();
        //    $("#screen_shop").hide();
        //    self.getarealist(area_num);
        //});
        ////店铺里面的品牌点击
        //$("#shop_brand").click(function(){
        //    if(self.cache.brand_codes!==""){
        //        var brand_codes=self.cache.brand_codes.split(',');
        //        var brand_names=self.cache.brand_names.split(',');
        //        var brand_html_right="";
        //        for(var h=0;h<brand_codes.length;h++){
        //            brand_html_right+="<li id='"+brand_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+brand_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_brand .s_pitch span").eq(0).html(h);
        //        $("#screen_brand .screen_content_r ul").html(brand_html_right);
        //    }else{
        //        $("#screen_brand .s_pitch span").eq(0).html("0");
        //        $("#screen_brand .screen_content_r ul").empty();
        //    }
        //    $("#screen_brand .screen_content_l ul").empty();
        //    $("#screen_brand").show();
        //    $("#screen_shop").hide();
        //    self.getbrandlist();
        //});
        ////员工里面的区域点击
        //$("#staff_area").click(function(){
        //    self.clickMark="user";
        //    if(self.cache.area_codes!==""){
        //        var area_codes=self.cache.area_codes.split(',');
        //        var area_names=self.cache.area_names.split(',');
        //        var area_html_right="";
        //        for(var h=0;h<area_codes.length;h++){
        //            area_html_right+="<li id='"+area_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+area_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_area .s_pitch span").eq(0).html(h);
        //        $("#screen_area .screen_content_r ul").html(area_html_right);
        //    }else{
        //        $("#screen_area .s_pitch span").eq(0).html("0");
        //        $("#screen_area .screen_content_r ul").empty();
        //    }
        //    isscroll=false;
        //    area_num=1;
        //    $("#screen_area .screen_content_l").unbind("scroll");
        //    $("#screen_area .screen_content_l ul").empty();
        //    $("#screen_area").show();
        //    $("#screen_staff").hide();
        //    self.getarealist(area_num);
        //});
        ////员工里面的店铺点击
        //$("#staff_shop").click(function(){
        //    self.clickMark="user";
        //    if(self.cache.store_codes!==""){
        //        var store_codes=self.cache.store_codes.split(',');
        //        var store_names=self.cache.store_names.split(',');
        //        var shop_html_right="";
        //        for(var h=0;h<store_codes.length;h++){
        //            shop_html_right+="<li id='"+store_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+store_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_shop .s_pitch span").eq(0).html(h);
        //        $("#screen_shop .screen_content_r ul").html(shop_html_right);
        //    }else{
        //        $("#screen_shop .s_pitch span").eq(0).html("0");
        //        $("#screen_shop .screen_content_r ul").empty();
        //    }
        //    isscroll=false;
        //    shop_num=1;
        //    $("#screen_shop .screen_content_l").unbind("scroll");
        //    $("#screen_shop .screen_content_l ul").empty();
        //    $("#screen_shop").show();
        //    $("#screen_staff").hide();
        //    self.getstorelist(shop_num);
        //});
        ////员工里面的品牌点击
        //$("#staff_brand").click(function(){
        //    self.clickMark="user";
        //    if(self.cache.brand_codes!==""){
        //        var brand_codes=self.cache.brand_codes.split(',');
        //        var brand_names=self.cache.brand_names.split(',');
        //        var brand_html_right="";
        //        for(var h=0;h<brand_codes.length;h++){
        //            brand_html_right+="<li id='"+brand_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+brand_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_brand .s_pitch span").eq(0).html(h);
        //        $("#screen_brand .screen_content_r ul").html(brand_html_right);
        //    }else{
        //        $("#screen_brand .s_pitch span").eq(0).html("0");
        //        $("#screen_brand .screen_content_r ul").empty();
        //    }
        //    $("#screen_brand .screen_content_l ul").empty();
        //    $("#screen_brand").show();
        //    $("#screen_staff").hide();
        //    self.getbrandlist();
        //});
        ////筛选弹框关闭
        //$("#screen_wrapper_close").click(function(){
        //    $("#screen_wrapper").hide();
        //    $("#p").hide();
        //    $(document.body).css("overflow","auto");
        //});
        ////弹出导购框/关闭／搜索／确定
        //$(".screen_staffl").click(function(){
        //    self.clickMark="";
        //    if(self.cache.user_codes!==""){
        //        var user_codes=self.cache.user_codes.split(',');
        //        var user_names=self.cache.user_names.split(',');
        //        var staff_html_right="";
        //        for(var h=0;h<user_codes.length;h++){
        //            staff_html_right+="<li id='"+user_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+user_codes[h]+"'  data-storename='"+user_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+user_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_staff .s_pitch span").eq(0).html(h);
        //        $("#screen_staff .screen_content_r ul").html(staff_html_right);
        //    }else{
        //        $("#screen_staff .s_pitch span").eq(0).html("0");
        //        $("#screen_staff .screen_content_r ul").empty();
        //    }
        //    staff_num=1;
        //    isscroll=false;
        //    $("#screen_staff").show();
        //    $("#screen_wrapper").hide();
        //    $("#screen_staff .screen_content_l").unbind("scroll");
        //    $("#screen_staff .screen_content_l ul").empty();
        //    // $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-phone="" data-storename="无" name="test" class="check" id="checkboxFourInputwu"><label for="checkboxFourInputwu"></label></div><span class="p16">无</span></li>');
        //    self.getstafflist(staff_num);
        //});
        //$("#screen_close_staff").click(function(){
        //    $("#screen_staff").hide();
        //    $("#screen_wrapper").show();
        //});
        //$("#staff_search").keydown(function(){
        //    var event=event||window.event||arguments[0];
        //    staff_num=1;
        //    if(event.keyCode==13){
        //        isscroll=false;
        //        $("#screen_staff .screen_content_l").unbind("scroll");
        //        $("#screen_staff .screen_content_l ul").empty();
        //        self.getstafflist(staff_num);
        //    }
        //});
        //$("#staff_search_f").click(function(){
        //    staff_num=1;
        //    isscroll=false;
        //    $("#screen_staff .screen_content_l").unbind("scroll");
        //    $("#screen_staff .screen_content_l ul").empty();
        //    self.getstafflist(staff_num);
        //});
        //$("#screen_que_staff").click(function(){
        //    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
        //    var user_codes="";
        //    var user_names="";
        //    for(var i=li.length-1;i>=0;i--){
        //        var r=$(li[i]).attr("id");
        //        var p=$(li[i]).find(".p16").html();
        //        if(i>0){
        //            user_codes+=r+",";
        //            user_names+=p+",";
        //        }else{
        //            user_codes+=r;
        //            user_names+=p;
        //        }
        //    }
        //    self.cache.user_codes=user_codes;
        //    self.cache.user_names=user_names;
        //    $("#screen_staff").hide();
        //    $("#screen_wrapper").show();
        //    $(".screen_staff_num").val("已选"+li.length+"个");
        //    $(".screen_staff_num").attr("data-code",user_codes);
        //    $(".screen_staff_num").attr("data-name",user_names);
        //});
        ////弹出区域框／关闭／搜索／确定
        //$("#screen_areal").click(function(){
        //    self.clickMark="";
        //    if(self.cache.area_codes!==""){
        //        var area_codes=self.cache.area_codes.split(',');
        //        var area_names=self.cache.area_names.split(',');
        //        var area_html_right="";
        //        for(var h=0;h<area_codes.length;h++){
        //            area_html_right+="<li id='"+area_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+area_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_area .s_pitch span").eq(0).html(h);
        //        $("#screen_area .screen_content_r ul").html(area_html_right);
        //    }else{
        //        $("#screen_area .s_pitch span").eq(0).html("0");
        //        $("#screen_area .screen_content_r ul").empty();
        //    }
        //    area_num=1;
        //    isscroll=false;
        //    $("#screen_area").show();
        //    $("#screen_wrapper").hide();
        //    $("#screen_area .screen_content_l").unbind("scroll");
        //    $("#screen_area .screen_content_l ul").empty();
        //    self.getarealist(area_num);
        //});
        //$("#screen_close_area").click(function(){
        //    $("#screen_area").hide();
        //    if(self.clickMark=="user"){
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //});
        //$("#area_search").keydown(function(){
        //    var event=event||window.event||arguments[0];
        //    area_num=1;
        //    if(event.keyCode == 13){
        //        isscroll=false;
        //        $("#screen_area .screen_content_l").unbind("scroll");
        //        $("#screen_area .screen_content_l ul").empty();
        //        self.getarealist(area_num);
        //    }
        //});
        //$("#area_search_f").click(function(){
        //    area_num=1;
        //    isscroll=false;
        //    $("#screen_area .screen_content_l").unbind("scroll");
        //    $("#screen_area .screen_content_l ul").empty();
        //    self.getarealist(area_num);
        //});
        //$("#screen_que_area").click(function(){
        //    var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
        //    var area_codes="";
        //    var area_names="";
        //    for(var i=li.length-1;i>=0;i--){
        //        var r=$(li[i]).attr("id");
        //        var p=$(li[i]).find(".p16").html();
        //        if(i>0){
        //            area_codes+=r+",";
        //            area_names+=p+",";
        //        }else{
        //            area_codes+=r;
        //            area_names+=p;
        //        }
        //    }
        //    self.cache.area_codes=area_codes;
        //    self.cache.area_names=area_names;
        //    $("#screen_area").hide();
        //    if(self.clickMark=="user"){
        //        area_num=1;
        //        isscroll=false;
        //        $("#screen_staff .screen_content_l").unbind("scroll");
        //        $("#screen_staff .screen_content_l ul").empty();
        //        self.getstafflist(area_num);
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //    $("#screen_area_num").val("已选"+li.length+"个");
        //    $("#screen_area_num").attr("data-code",area_codes);
        //    $("#screen_area_num").attr("data-name",area_names);
        //    $(".area_num").val("已选"+li.length+"个");
        //});
        ////弹出品牌框／关闭／搜索／确定
        //$("#screen_brandl").click(function(){
        //    self.clickMark="";
        //    if(self.cache.brand_codes!==""){
        //        var brand_codes=self.cache.brand_codes.split(',');
        //        var brand_names=self.cache.brand_names.split(',');
        //        var brand_html_right="";
        //        for(var h=0;h<brand_codes.length;h++){
        //            brand_html_right+="<li id='"+brand_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+brand_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_brand .s_pitch span").eq(0).html(h);
        //        $("#screen_brand .screen_content_r ul").html(brand_html_right);
        //    }else{
        //        $("#screen_brand .s_pitch span").eq(0).html("0");
        //        $("#screen_brand .screen_content_r ul").empty();
        //    }
        //    $("#screen_brand").show();
        //    $("#screen_wrapper").hide();
        //    $("#screen_brand .screen_content_l ul").empty();
        //    self.getbrandlist();
        //});
        //$("#screen_close_brand").click(function(){
        //    $("#screen_brand").hide();
        //    if(self.clickMark=="user"){
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //});
        //$("#brand_search").keydown(function(){
        //    var event=event||window.event||arguments[0];
        //    if(event.keyCode==13){
        //        $("#screen_brand .screen_content_l ul").empty();
        //        self.getbrandlist();
        //    }
        //});
        //$("#brand_search_f").click(function(){
        //    $("#screen_brand .screen_content_l ul").empty();
        //    self.getbrandlist();
        //});
        //$("#screen_que_brand").click(function(){
        //    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
        //    var brand_codes="";
        //    var brand_names="";
        //    for(var i=li.length-1;i>=0;i--){
        //        var r=$(li[i]).attr("id");
        //        var p=$(li[i]).find(".p16").html();
        //        if(i>0){
        //            brand_codes+=r+",";
        //            brand_names+=p+",";
        //        }else{
        //            brand_codes+=r;
        //            brand_names+=p;
        //        }
        //    }
        //    self.cache.brand_codes=brand_codes;
        //    self.cache.brand_names=brand_names;
        //    $("#screen_brand").hide();
        //    if(self.clickMark=="user"){
        //        staff_num=1;
        //        isscroll=false;
        //        $("#screen_staff .screen_content_l").unbind("scroll");
        //        $("#screen_staff .screen_content_l ul").empty();
        //        self.getstafflist(staff_num);
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //    $("#screen_brand_num").val("已选"+li.length+"个");
        //    $("#screen_brand_num").attr("data-code",brand_codes);
        //    $("#screen_brand_num").attr("data-name",brand_names);
        //    $(".brand_num").val("已选"+li.length+"个");
        //});
        ////弹出店铺框／关闭／搜索／确定
        //$("#screen_shopl").click(function(){
        //    self.clickMark="";
        //    if(self.cache.store_codes!==""){
        //        var store_codes=self.cache.store_codes.split(',');
        //        var store_names=self.cache.store_names.split(',');
        //        var shop_html_right="";
        //        for(var h=0;h<store_codes.length;h++){
        //            shop_html_right+="<li id='"+store_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+store_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_shop .s_pitch span").eq(0).html(h);
        //        $("#screen_shop .screen_content_r ul").html(shop_html_right);
        //    }else{
        //        $("#screen_shop .s_pitch span").eq(0).html("0");
        //        $("#screen_shop .screen_content_r ul").empty();
        //    }
        //    shop_num=1;
        //    isscroll=false;
        //    $("#screen_shop").show();
        //    $("#screen_wrapper").hide();
        //    $("#screen_shop .screen_content_l").unbind("scroll");
        //    $("#screen_shop .screen_content_l ul").empty();
        //    self.getstorelist(shop_num);
        //});
        //$("#screen_close_shop").click(function(){
        //    $("#screen_shop").hide();
        //    if(self.clickMark=="user"){
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //});
        //$("#store_search").keydown(function(){
        //    var event=event||window.event||arguments[0];
        //    shop_num=1;
        //    if(event.keyCode==13){
        //        isscroll=false;
        //        $("#screen_shop .screen_content_l ul").unbind("scroll");
        //        $("#screen_shop .screen_content_l ul").empty();
        //        self.getstorelist(shop_num);
        //    }
        //});
        //$("#store_search_f").click(function(){
        //    shop_num=1;
        //    isscroll=false;
        //    $("#screen_shop .screen_content_l").unbind("scroll");
        //    $("#screen_shop .screen_content_l ul").empty();
        //    self.getstorelist(shop_num);
        //});
        //$("#screen_que_shop").click(function(){
        //    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
        //    var store_codes="";
        //    var store_names="";
        //    for(var i=li.length-1;i>=0;i--){
        //        var r=$(li[i]).attr("id");
        //        var p=$(li[i]).find(".p16").html();
        //        if(i>0){
        //            store_codes+=r+",";
        //            store_names+=p+",";
        //        }else{
        //            store_codes+=r;
        //            store_names+=p;
        //        }
        //    }
        //    self.cache.store_codes=store_codes;
        //    self.cache.store_names=store_names;
        //    $("#screen_shop").hide();
        //    if(self.clickMark=="user"){
        //        shop_num=1;
        //        isscroll=false;
        //        $("#screen_staff .screen_content_l").unbind("scroll");
        //        $("#screen_staff .screen_content_l ul").empty();
        //        self.getstafflist(shop_num);
        //        $("#screen_staff").show();
        //    }else {
        //        $("#screen_wrapper").show();
        //    }
        //    $("#screen_shop_num").val("已选"+li.length+"个");
        //    $("#screen_shop_num").attr("data-code",store_codes);
        //    $("#screen_shop_num").attr("data-name",store_names);
        //    $("#staff_shop_num").val("已选"+li.length+"个");
        //});
        ////分组弹窗/搜索/确定
        //$("#vipTask_group_icon").click(function () {
        //    if(self.cache.group_codes!==""){
        //        var group_codes=self.cache.group_codes.split(',');
        //        var group_names=self.cache.group_names.split(',');
        //        var group_html_right="";
        //        for(var h=0;h<group_codes.length;h++){
        //            group_html_right+="<li id='"+group_codes[h]+"'>\
        //    <div class='checkbox1'><input type='checkbox' value='"+group_codes[h]+"' name='test' class='check'>\
        //    <label></div><span class='p16'>"+group_names[h]+"</span>\
        //    \</li>"
        //        }
        //        $("#screen_group .s_pitch span").html(h);
        //        $("#screen_group .screen_content_r ul").html(group_html_right);
        //    }else{
        //        $("#screen_group .s_pitch span").html("0");
        //        $("#screen_group .screen_content_r ul").empty();
        //    }
        //    $("#screen_wrapper").hide();
        //    $("#screen_group").show();
        //    self.getGroup();
        //});
        //$("#task_group_search").keydown(function () {
        //    var event = event || window.event || arguments[0];
        //    if (event.keyCode == 13) {
        //        self.getGroup();
        //    }
        //});
        //$("#task_group_search_f").click(function () {
        //    self.getGroup();
        //});
        //$("#task_screen_que_group").click(function () {
        //    var li=$("#screen_group .screen_content_r input[type='checkbox']").parents("li");
        //    var group_codes="";
        //    var group_names="";
        //    for(var i=li.length-1;i>=0;i--){
        //        var r=$(li[i]).attr("id");
        //        var p=$(li[i]).find(".p16").html();
        //        if(i>0){
        //            group_codes+=r+",";
        //            group_names+=p+",";
        //        }else{
        //            group_codes+=r;
        //            group_names+=p;
        //        }
        //    }
        //    self.cache.group_codes=group_codes;
        //    self.cache.group_names=group_names;
        //    $("#screen_group").hide();
        //    $("#screen_wrapper").show();
        //    $("#filter_group").val("已选"+li.length+"个");
        //    $("#filter_group").attr("data-code",group_codes);
        //    $("#filter_group").attr("data-name",group_names);
        //});
        //弹出筛选
		$("#p_task_content").on("click",".chooseVipBtn",function () {
			if($(this).hasClass("canNotSelectShow")){
				return
			}
			$("#select_clear").trigger("click");
			$(".chooseVipBtn").removeClass("currentSelect");
			$(this).addClass("currentSelect");
			var screen=$(this).attr("data-screen");
			if(screen==undefined || screen==""){
				_param.screen=""
			}else{
				_param.screen=JSON.parse(screen);
			}
			select_type=$(this).attr("data-select_type")==undefined?"content_basic":$(this).attr("data-select_type");
			$(".select_box").css({
				height: $(window).height() - 130 + "px"
			});
			$(".select_box_wrap").show();
			$(".select_box_wrap .select_box").css({"top":"100px","left":"50%"});
			$(".select_box .center_item_group .item_list").html("");
			$(".select_box .center_item_group").hide();
			$(".select_box .selected_list").html("");
			if(_param.screen!==undefined && _param.screen.length>0){
				$(".notSelect").hide();
				if(select_type=="content_basic"){
					$("#content_high .notSelect").show();
					$(".tabs_head>label:first-child").trigger("click");
				}else{
					$("#content_basic .notSelect").show();
					$(".tabs_head>label:last-child").trigger("click");
				}
				fuzhiSelect();
			}else{
				if($(".select_box .tabs_head").css("visibility")!="hidden"){
					$(".tabs_head>label:first-child").trigger("click");
				} else{
					//if(showAdvance && !showBasic){
					//	$(".tabs_head>label:last-child").trigger("click");
					//}
					//if(!showAdvance && showBasic){
					//	$(".tabs_head>label:first-child").trigger("click");
					//}
				}
				$(".notSelect").show();
			}
			$("#content_high .item_group").find(".group_arrow").css({
				animation: "none"
			});
			$(".select_box_wrap").css("bottom",$(window).height()-$(document).height());
        });
        //清空筛选
        $("#vipTask_empty_filter").click(function () {
            self.cache.area_codes="";
            self.cache.area_names="";
            self.cache.brand_codes="";
            self.cache.brand_names="";
            self.cache.store_codes="";
            self.cache.store_names="";
            self.cache.user_codes="";
            self.cache.user_names="";
            self.cache.group_codes="";
            self.cache.group_name="";
            $("#screen_wrapper .contion_input input").each(function () {
                var key = $(this).attr("data-kye");
                if (key == "6" || key == "8" || key == "12" || key == "18") {
                    $(this).val("全部");
                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15") {
                    $(this).val("");
                    $(this).attr("data-code", "");
                } else if (key == "16") {
                    $(this).val("");
                    $(this).attr("data-code", "");
                    $("#filter_group").attr("data-corp", "");
                } else if (key == "17") {
                    $(this).val("最近3个月");
                    $(this).attr("data-date","3");
                } else {
                    $(this).val("");
                }
            });
            $("#screen_wrapper textarea").each(function () {
                $(this).val("");
            });
            $("#staff_brand_num").val("全部");
            $("#staff_brand_num").attr("data-brandcode","");
            $("#staff_area_num").val("全部");
            $("#staff_brand_num").attr("data-areacode","");
            $("#staff_shop_num").val("全部");
            $("#staff_shop_num").attr("data-storecode","");
            $("#brand_num").val("全部");
            $("#brand_num").attr("data-brandcode","");
            $("#area_num").val("全部");
            $("#area_num").attr("data-areacode","");
        });
        ////筛选确定
        //$("#vip_screen_vip_que").click(function () {
        //    $(document.body).css("overflow", "auto");
        //    var screen = [];
        //    if ($("#simple_filter").css("display") == "block") {
        //        _param['screen_type']="easy";
        //        $("#simple_contion .contion_input").each(function () {
        //            var input = $(this).find("input");
        //            var key = $(input[0]).attr("data-kye");
        //            var classname = $(input[0]).attr("class");
        //            var expend_key = $(input[0]).attr("data-expend");
        //            if(key == "17"){
        //                return ;
        //            }else if (key == "4") {
        //                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
        //                    var param = {};
        //                    var val = {};
        //                    var date = $("#consume_date_basic_4").attr("data-date");
        //                    val['start'] = $(input[0]).val();
        //                    val['end'] = $(input[1]).val();
        //                    param['type'] = "json";
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['date'] = date;
        //                    param['names'] = $(this).find("label:first-child").text();
        //                    screen.push(param);
        //                }
        //            }else if (key == "3") {
        //                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
        //                    var param = {};
        //                    var val = {};
        //                    var date = $("#consume_date_basic_3").attr("data-date");
        //                    val['start'] = $(input[0]).val();
        //                    val['end'] = $(input[1]).val();
        //                    param['type'] = "json";
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['date'] = date;
        //                    param['names'] = $(this).find("label:first-child").text();
        //                    screen.push(param);
        //                }
        //            }else if(key == "15"){
        //                if ($(input[0]).attr("data-code") !== "") {
        //                    var param = {};
        //                    var val = $(input[0]).attr("data-code");
        //                    var name = $(input[0]).attr("data-name");
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['name'] = name;
        //                    param['type'] = "text";
        //                    param['names'] = $(this).find("label").text();
        //                    screen.push(param);
        //                }
        //            }else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
        //                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
        //                    var param = {};
        //                    var val = {};
        //                    val['start'] = $(input[0]).val();
        //                    val['end'] = $(input[1]).val();
        //                    param['type'] = "json";
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['names'] = $(this).find("label:first-child").text();
        //                    screen.push(param);
        //                }
        //            }else if(key == "18" && $(input[0]).val() !== "全部"){
        //                var param = {};
        //                var val = $(input[0]).val();
        //                param['name'] = val;
        //                val = val == "未关注" ? "N": "Y";
        //                param['key'] = key;
        //                param['value'] = val;
        //                param['type'] = "text";
        //                param['names'] = $(this).find("label").text();
        //                screen.push(param);
        //            }else {
        //                if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
        //                    var param = {};
        //                    var val = $(input[0]).val();
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['type'] = "text";
        //                    param['names'] = $(this).find("label").text();
        //                    screen.push(param);
        //                }
        //            }
        //        });
        //    }
			//else {
        //        _param['screen_type']="difficult";
        //        $("#contion>div").each(function () {
        //            $(this).find(".contion_input").each(function (i, e) {
        //                var input = $(e).find("input");
        //                var key = $(input[0]).attr("data-kye");
        //                var expend_key = $(input[0]).attr("data-expend");
        //                var classname = $(input[0]).attr("class");
        //                if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
        //                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
        //                        var param = {};
        //                        var val = {};
        //                        val['start'] = $(input[0]).val();
        //                        val['end'] = $(input[1]).val();
        //                        param['type'] = "json";
        //                        param['key'] = key;
        //                        param['value'] = val;
        //                        param['names'] = $(this).find("label:first-child").text();
        //                        screen.push(param);
        //                    }
        //                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15"|| key == "16") {
        //                    if ($(input[0]).attr("data-code") !== "") {
        //                        var param = {};
        //                        var val = $(input[0]).attr("data-code");
        //                        var name = $(input[0]).attr("data-name");
        //                        param['key'] = key;
        //                        param['value'] = val;
        //                        param['name'] = name;
        //                        param['type'] = "text";
        //                        param['names'] = $(this).find("label").text();
        //                        screen.push(param);
        //                    }
        //                }else if (key == "17") {
        //                    return;
        //                } else if (key == "3" || key == "4") {
        //                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
        //                        var param = {};
        //                        var val = {};
        //                        var date = $("#consume_date").attr("data-date");
        //                        val['start'] = $(input[0]).val();
        //                        val['end'] = $(input[1]).val();
        //                        param['type'] = "json";
        //                        param['key'] = key;
        //                        param['value'] = val;
        //                        param['date'] = date;
        //                        param['names'] = $(this).find("label:first-child").text();
        //                        screen.push(param);
        //                    }
        //                }else if((key == "6"||key == "18") && $(input[0]).val() !== "全部" ){
        //                    var param = {};
        //                    var val = $(input[0]).val();
        //                    param['name'] = val;
        //                    val = (val == "已冻结"||val == "未关注") ? "N": "Y";
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['type'] = "text";
        //                    param['names'] = $(this).find("label").text();
        //                    screen.push(param);
        //                }else {
        //                    if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
        //                        var param = {};
        //                        var val = $(input[0]).val();
        //                        param['key'] = key;
        //                        param['value'] = val;
        //                        param['type'] = "text";
        //                        param['names'] = $(this).find("label").text();
        //                        screen.push(param);
        //                    }
        //                }
        //            });
        //            $(this).find("textarea").each(function () {
        //                var key = $(this).attr("data-kye");
        //                var param = {};
        //                var val = $(this).val();
        //                if(val !== ""){
        //                    param['key'] = key;
        //                    param['value'] = val;
        //                    param['type'] = "text";
        //                    param['names'] = $(this).find("label").text();
        //                    screen.push(param);
        //                }
        //            });
        //        });
        //    }
        //    _param['screen'] = screen;
        //    $("#lookCondition").attr("data-code",JSON.stringify(screen));
        //    $("#screen_wrapper").hide();
        //    $("#p").hide();
        //    // $("#vipTask_empty_filter").trigger("click");
        //});
		$("#select_sure_activity").click(function(){
			getSelectData().then(function(screenData){
				//_param['screen'] = screenData;
				$(".chooseVipBtn.currentSelect").attr("data-screen",JSON.stringify(screenData)).attr("data-select_type",select_type);
				$(".select_box_wrap").hide();
				$(".select_box_content_left .item_group").find("ul").slideUp().next(".group_arrow").removeClass("item_group_active");
				$(".chooseVipBtn.currentSelect").next(".vip_num").find("b").html('<img src="../img/loading-upload.gif">')
				var screen_type=$(".chooseVipBtn.currentSelect").attr("data-screen_type");
				getSendVipCount(screenData,screen_type).then(function(num){
					$(".chooseVipBtn.currentSelect").next(".vip_num").find("b").html(num)
				});
			})
		});
		$("#p_task_content").on("click",".lookCondition",function (){
			var div='';
			if($(this).prevAll(".chooseVipBtn").hasClass("canNotSelectShow")){
				$(this).prevAll(".chooseVipBtn").removeClass("canNotSelectShow").addClass("notSelectShow")
			}
			$(this).prevAll(".chooseVipBtn").trigger("click");
			$(".select_box_wrap").css("visibility","hidden");
			if(_param.screen==undefined || _param.screen.length==0){
				div = '<div style="text-align: center;line-height: 150px">当前未设置筛选条件,默认为<span style="color:#c26555;">全部会员</span></div>'
			}else {
				div=$("#" + select_type).find(".select_box_content_right .selected_list").html();
				//div=div.replace(/<br>/g,"");
			}
			$("#choosedConditionWrap .selected_list").html(div);
			$("#choosedConditionWrap .selected_list").find(".delete_item").remove();
			$("#choosedConditionWrap").show();
		});
        $("#closeChoosedWrap").click(function () {//关闭已选商品条件
			$(".select_box_wrap").css("visibility","visible");
			$(".select_box_wrap").hide();
            $("#choosedConditionWrap").hide();
            //
            if($(".currentSelect").hasClass("notSelectShow")){
				$(".currentSelect").removeClass("notSelectShow").addClass("canNotSelectShow")
            }
        });
    }
};
$(function(){
	activityPlanning.init();
	select_corp_code=sessionStorage.getItem("corp_code");
	expend_data_1();
    //日期调用插件
    var simple_birth_starts = {
        elem: '#simple_birth_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date=datas.split("-");
            date=date[1]+"-"+date[2];
            $(this.elem).val(date);
            simple_birth_ends.min = datas; //开始日选好后，重置结束日的最小日期
            simple_birth_ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var simple_birth_ends = {
        elem: '#simple_birth_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date=datas.split("-");
            date=date[1]+"-"+date[2];
            $(this.elem).val(date);
            simple_birth_starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    var starts = {
        elem: '#birth_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date=datas.split("-");
            date=date[1]+"-"+date[2];
            $(this.elem).val(date);
            ends.min = datas; //开始日选好后，重置结束日的最小日期
            ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var ends = {
        elem: '#birth_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date=datas.split("-");
            date=date[1]+"-"+date[2];
            $(this.elem).val(date);
            starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    var activity_starts = {
        elem: '#activate_card_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            activity_ends.min = datas; //开始日选好后，重置结束日的最小日期
            activity_ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var activity_ends = {
        elem: '#activate_card_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            activity_starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    $("#simple_birth_starts").bind("click",function(){
        laydate(simple_birth_starts)
    });
    $("#simple_birth_ends").bind("click",function(){
        laydate(simple_birth_ends)
    });
    $("#birth_ends").bind("click",function(){
        laydate(ends)
    });
    $("#birth_starts").bind("click",function(){
        laydate(starts)
    });
    $("#activate_card_starts").bind("click",function() {
        laydate(activity_starts);
    });
    $("#activate_card_ends").bind("click",function(){
        laydate(activity_end1)
    });
});
function formatCurrency(num) {
	num=String(num);
	var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
	num=num.replace(reg, '$1,');//千分位格式化
	return num;
}
function checkStart(data){
    $("#target_end_time").attr("onclick","laydate({elem:'#target_end_time',min:'"+data+"',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD'})");
}
function getSendVipCount(screenData,screen_type){
	var param={
		send_scope:"vip_condition_new",
		sms_vips:screenData,
		vip_group_code:"",
		type:screen_type
	};
	if(document.title=="创建活动"){//如果是创建活动的页面
		param["corp_code"]=$("#tabs div").eq(0).attr("data-corp");
	}
	if(document.title=="策略补充"){//如果是策略补充的页面
		param["corp_code"]=sessionStorage.getItem("corp_code");
	}
	var def= $.Deferred();
	oc.postRequire("post", "/vipFsend/getSendCount", "", param, function (data) {
		if(data.code==0){
			var msg=JSON.parse(data.message);
			count=msg.count;
			def.resolve(formatCurrency(count))
		}else{
			def.reject(0)
		}
	});
	return def
}
var chooseVipBtn=$("#p_task_content .group_parent").not("#emlist").find(".chooseVipBtn");
chooseVipBtn.map(function(i,btn){
	var screen=JSON.parse($(btn).attr("data-screen"));
	var screen_type=$(btn).attr("data-screen_type");
	getSendVipCount(screen,screen_type).then(function(num){
		$(btn).next(".vip_num").find("b").html(num)
	})
});