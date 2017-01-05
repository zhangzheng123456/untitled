console.log(123123);
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
		//添加任务
		$("#task_add").click(function(){
			$("#task_titles li").removeClass('active');
			$("#task_titles").append("<li class='active'>"+$("#task_title").val()+"</li>")
		})
		//删除任务
		$("#task_del").click(function(){
			$("#task_titles li.active").remove();
		})
		//新增一个div
		$(".p_task_content").on("click",'.input_parent .group_add',function(){
			$(this).parents(".add_del").hide();
			var html=$(this).parents('.input_parent').clone();
			$(html).find(".add_del").show();
			$("#p_task_content").append(html);
		})
		//删除一个div
		$(".p_task_content").on("click",".input_parent .group_del",function(){
			$(this).parents('.input_parent').prev().find(".add_del").show();
			$(this).parents('.input_parent').remove();
		})
	}
}
$(function(){
	activityPlanning.init();
})