var login=new Vue({
	el:'#con_table'
	,data:{
		time_show:true
		,time:''//筛选时间段
		,nav_4:''
		,time_start:''//周的开始时间
		,time_end:''//周的结束时间
		,year_down:''//下拉中的年份
		,app_action_code:''//传动作
		,data_value:'2017-02-03'//传时间
		,data_type:'M'//传日期类型
    }
});