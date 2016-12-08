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
    	var tr="";
    	for(var i=0;i<list.length;i++){
    		var num=i+1;
    		tr+="<tr data-function='"+list[i].function_code+"'><td style='text-align: left;padding-left:22px;width:7.21%;'>"
    		+num+"</td><td style='width:12.78%;'>"+list[i].module_name+"</td><td style='width: 12.78%;'>"+list[i].function_name+
    		"</td><td style='width: 22.69%;'><div class='action_name'><ul>"
    		for(var j=0;j<list[i].actions.length;j++){
                var color="";
                if(list[i].actions[j].is_die=="Y"){
                    color="die";
                }
                if(list[i].actions[j].is_die=="N"&&list[i].actions[j].is_live=="N"){
                    color="";
                }
                if(list[i].actions[j].is_die=="N"&&list[i].actions[j].is_live=="Y"){
                    color="active";
                }
    			tr+="<li class='"+color+"' data-actionCode='"+list[i].actions[j].action_code+"' data-actionName='"+list[i].actions[j].action_name+
    			"' data-actionId='"+list[i].actions[j].action_id+"'>"+
    			list[i].actions[j].show_name+"</li>"
    		}
    		tr+="</ul></div></td><td style='width: 44.54%;'><div class='modify_options'><ul>";
    		for(var k=0;k<list[i].columns.length;k++){
                var color="";
                if(list[i].columns[k].is_die=="Y"){
                    color="die";
                }
                if(list[i].columns[k].is_die=="N"&&list[i].columns[k].is_live=="N"){
                    color="";
                }
                if(list[i].columns[k].is_die=="N"&&list[i].columns[k].is_live=="Y"){
                    color="active";
                }
    			tr+="<li class='"+color+"' data-columnId='"+list[i].columns[k].column_id+"' data-columnName='"+list[i].columns[k].column_name+"'>"+
    			list[i].columns[k].show_name+"</li>"
    		}
    		tr+="</ul></div></td></tr>";
    	}
    	$("#table tbody").html(tr);
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
        var self=this;
		$("#turnoff").bind("click",function(){
			$(window.parent.document).find('#iframepage').attr("src","/user/group_edit.html");
		});
        $(".power_table").on("click","ul li",function(){
            var class_name=$(this).attr("class");
            if(class_name=="die"){
                return;
            }else{
                $(this).toggleClass("active");
            }
        })
	}
};
$(function(){
	groupPower.init();
})
