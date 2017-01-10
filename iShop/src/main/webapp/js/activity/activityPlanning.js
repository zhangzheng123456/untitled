
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
	},
	init:function(){
		this.allEvent();
		this.getTaskList();
		this.getPlanningList();
	},
	allEvent:function(){
		//任务切换
		var self=this;
		$("#task").on("click",".tabs_left ul li",function(){
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
			self.evaluationTask();
		});
		//下拉框样式
		$(".text_input").click(function(){
			var ul = $(this).siblings('.input_dropdown');
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
		    self.param.tasklist.remove(current);
			$("#task_titles li.active").remove();
			$("#task_title").val("");
			$("#task_type_code").val("");
			$("#task_description").val("");
			$(prev).addClass('active');
			self.evaluationTask();
		})
		//新增一个div
		$(".p_task_content").on("click",'.input_parent .group_add',function(){
			$(this).parents(".add_del").hide();
			var html=$(this).parents('.input_parent').clone();
			$(html).find(".add_del").show();
			$(html).find(".group_del").show();
			$(this).parents('.group_parent').append(html);
		})
		//删除一个div
		$(".p_task_content").on("click",".input_parent .group_del",function(){
			$(this).parents('.input_parent').prev().find(".add_del").show();
			$(this).parents('.input_parent').remove();
		})
		//开关按钮
		$(".switch div").click(function(){
			$(this).toggleClass("bg");
			$(this).find("span").toggleClass("Off");
			var id=$(this).parent().attr("id");
			if(id=="task_switch"){
				if($(this).attr("class")==""){
					$(this).parent().find(".switch_text").html("任务已关闭");
				}else if($(this).attr("class")=="bg"){
					$(this).parent().find(".switch_text").html("任务已开启");
				}
			}
			if(id=="group_switch"){
				if($(this).attr("class")==""){
					$(this).parent().find(".switch_text").html("群发已关闭");
				}else if($(this).attr("class")=="bg"){
					$(this).parent().find(".switch_text").html("群发已开启");
				}
			}
			// var param={};
			// var tasklist=self.param.tasklist;
			// param["tasklist"]=tasklist;
			// // 测试
			// param["activity_vip_code"]="ACls0000000001";
			// console.log(param);
			// oc.postRequire("post","/vipActivity/arrange/addOrUpdateTask","0",param, function (data) {
			// 	console.log(data);
			// });
			//测试
			// self.getGroupValue();
		})
		//编辑弹框
		$(".p_task_content").on("click",".input_parent .group_edit",function(){
			whir.loading.add("mask",0.5);//加载等待框
			$(this).parents('.input_parent').find(".edit_frame").show();
		})
		//编辑取消
		$(".p_task_content").on("click",".input_parent .edit_footer_close",function(){
			$(this).parents('.input_parent').find(".edit_frame").hide()
			whir.loading.remove('mask');
		})
	},
	evaluationTask:function(){
		var nextIndex=$("#task_titles li.active").index();
		var nextCurrent=this.param.tasklist[nextIndex];
		$("#task_title").val(nextCurrent.task_title);
		$("#task_type_code").val(nextCurrent.task_type_code);
		$("#task_description").val(nextCurrent.task_description);
		$("#target_start_time").val(nextCurrent.target_start_time);
		$("#target_end_time").val(nextCurrent.target_end_time);
	},
	getTaskList:function(){
		var param={};
		param["corp_code"]="C10000";
		oc.postRequire("post","/task/selectAllTaskType", "0",param, function (data) {
			var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html="";
            if (list.length>0) {
                for (var i = 0; i < list.length; i++) {
                    html += '<div data-code="' + list[i].task_type_code + '">' + list[i].task_type_name + '</div>';
                }
                $("#task_type_code").siblings('.input_dropdown').html(html);
            } else if (list.length <= 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先定义任务类型"
                });
            };
		})
	},
	addTask:function(){
		var self=this;
		var task_title=$("#task_title").val();//任务标题
		var target_start_time=$("#target_start_time").val();//开始时间
		var target_end_time=$("#target_end_time").val();//截止时间
		var task_type_code=$("#task_type_code").attr("data-code");//任务类型编号
		var task_description=$("#task_description").val();//任务简述
		var task_link=$("#task_link").val();//任务链接
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
				content:"截止时间不能为空"
			});
			return;
		}
		if(task_description==""){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content:"任务简述不能为空"
			});
			return;
		}
		$("#task_titles li").removeClass('active');
		var length=$("#task_titles li").length+1;
		$("#task_titles").append("<li class='active'>任务"+length+"</li>");
		var param={};
		param["task_title"]=task_title;//任务标题
		param["target_start_time"]=target_start_time;//开始时间
		param["target_end_time"]=target_end_time;//结束时间
		param["task_type_code"]=task_type_code;//任务编号
		param["task_description"]=task_description;//任务简述
		param["task_link"]=task_link//链接
		self.param.tasklist.push(param);
	},
	getGroupValue:function(){
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
	    //微信推送
		for(var i=0;i<wxlistnode.length;i++){
			var send_time=$(wxlistnode[i]).find(".text_input").val();//发送时间
			var title=$(wxlistnode[i]).find(".edit_frame .edit_title").val();//推送标题
			var url=$(wxlistnode[i]).find(".edit_frame .edit_link").val();//页面链接
			var desc=$(wxlistnode[i]).find(".edit_frame .edit_content").val();//摘要
			var image="";//封面链接
			var wxparam={"send_time":send_time,"content":{"title":title,url:url,desc:desc,image:image}};
			wxlist.push(wxparam);
		}
		//短信群发
		for(var h=0;h<smslistnode.length;h++){
			var send_time=$(smslistnode[h]).find(".text_input").val();//发送时间
			var content=$(smslistnode[h]).find(".edit_frame .edit_content").val();//短信内容
			var smsparam={"send_time":send_time,"content":content};
			smslist.push(smsparam);
		}
		//邮件群发
		for(var k=0;k<emlistnode.length;k++){
			var send_time=$(emlistnode[k]).find(".text_input").val();//发送时间
			var content="";//短信内容
			var emparam={"send_time":send_time,"content":content};
			emlist.push(emparam);
		}
		param["wxlist"]=wxlist;
		param["smslist"]=smslist;
		param["emlist"]=emlist;
		param["activity_vip_code"]="ACls0000000001";
		console.log(param);
		oc.postRequire("post","/vipActivity/arrange/addOrUpdateSend","0",param, function (data) {
			console.log(data);
		});
	},
	getPlanningList:function(){//获取列表信息
		var param={};
		param["activity_vip_code"]="ACls0000000001";
		oc.postRequire("post","/vipActivity/arrange/list","0",param, function (data) {
			console.log(data);
		});
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