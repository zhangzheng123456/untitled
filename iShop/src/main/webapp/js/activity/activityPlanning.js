
var activityPlanning={
	init:function(){
		this.allEvent();
	},
	allEvent:function(){
		//任务切换
		$("#task").on("click",".tabs_left ul li",function(){
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
		});
		//群发管理切换
		$("#group").on("click",".tabs_left ul li",function(){
			var index=$(this).index();
			$(this).addClass("active");
			$(this).siblings("li").removeClass("active");
			$("#p_task_content .group_parent").eq(index).show();
			$("#p_task_content .group_parent").eq(index).siblings().hide();
		})
		//添加任务
		$("#task_add").click(function(){
			$("#task_titles li").removeClass('active');
			$("#task_titles").append("<li class='active'>"+$("#task_title").val()+"</li>");
			var html=$("#task_content .task_parent").eq(0).clone();
			$(html).find('input').val();
			$("#task_content").append(html)
		})
		//删除任务
		$("#task_del").click(function(){
			var prev=$("#task_titles li.active").prev();
			$("#task_titles li.active").remove();
			$(prev).addClass('active');
		})
		//新增一个div
		$(".p_task_content").on("click",'.input_parent .group_add',function(){
			console.log(123123);
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
	}
}
$(function(){
	activityPlanning.init();
})