var oc = new ObjectControl();
var groupPower={
	init:function(){
		this.getPowerlist();
		this.close();
	},
	getSession:function(){//获取本地存储
		var group_corp=JSON.parse(sessionStorage.getItem("group_corp"));//取本地的群组编号
		return group_corp
	},
    pageRendering:function(list){//绘制页面
    	console.log(list);
    },
	getPowerlist:function(){
		var self=this;
		var group_corp=this.getSession();
        var param={};
        param["corp_code"]=group_corp.corp_code;
        param["group_code"]=group_corp.group_code;
        oc.postRequire("post","/user/group/check_power1","0",param,function(data){
        	var message=JSON.parse(data.message);
        	var list=message.list;
        	self.pageRendering(list);
        });
	},
	close:function(){
		$("#turnoff").bind("click",function(){
			$(window.parent.document).find('#iframepage').attr("src","/user/group_edit.html");
		});
	}
};
$(function(){
	groupPower.init();
})
