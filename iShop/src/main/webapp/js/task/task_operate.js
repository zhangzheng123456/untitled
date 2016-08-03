var oc = new ObjectControl();
var a="";
var b="";
//获取企业类型的下拉框
function getasktypelist(a,b){
	var corp_command="/task/selectAllTaskType";
	var _param={};
	_param["corp_code"]=a;
	oc.postRequire("post",corp_command,"0",_param,function(data){
		if(data.code=="0"){
			var message=JSON.parse(data.message);
			var list=JSON.parse(message.list);
			var type_html="";
			$("#task_type_code").empty();
			$('#task_type_list .searchable-select').remove();
			if(list.length>0){
				for(var i=0;i<list.length;i++){
					type_html+='<option value="'+list[i].task_type_code+'">'+list[i].task_type_name+'</option>';
				}
			    $("#task_type_code").html(type_html);
			}else if(list.length<=0){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: "请先定义任务类型"
				});
			}
			if(b!==""){
				$("#task_type_code option[value='"+b+"']").attr("selected","true")
			}
			$("#task_type_code").searchableSelect();
		}
	})
}
//获取企业类型的下拉框
function getcorplist(a,b){
	//获取所属企业列表
	var corp_command="/user/getCorpByUser";
	oc.postRequire("post", corp_command,"", "", function(data){
		if(data.code=="0"){
			var msg=JSON.parse(data.message);
			var index=0;
			var corp_html='';
			var c=null;
			for(index in msg.corps){
				c=msg.corps[index];
				corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$("#OWN_CORP").searchableSelect();
			var c=$('#corp_select .selected').attr("data-value");
			getasktypelist(c,b);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				getasktypelist(c,b);
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
//获取企业区域下面下拉框
function getarealist(){
	var corp_code = $('#OWN_CORP').val();
	var area_command = "/shop/area";
	var _param = {};
	var checknow_data=[];
    var checknow_namedata=[];
	_param["corp_code"] = corp_code;
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var msg = JSON.parse(data.message);
			console.log(msg);
			var area_html = '';
			console.log(msg.areas);
			if (msg.areas.length == 0) {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: "该企业目前分配区域！"
				});
			} else {
				for (var i = 0; i < msg.areas.length; i++) {
				    area_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+msg.areas[i].area_code+"' data-areaname='"+msg.areas[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + 1
                        + "'/><label for='checkboxOneInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16'>"+msg.areas[i].area_name+"</span></li>"
				}
				$("#area_code ul").html(area_html);
				var check_input = $('#area_code ul input');
				for (var c = 0; c < check_input.length; c++) {
					check_input[c].onclick = function() {
						if (this.checked == true) {
							checknow_data.push($(this).val());
							checknow_namedata.push($(this).attr("data-areaname"));
							$('#area_input').val(checknow_namedata.toString());
							$('#area_input').attr('data-areacode', checknow_data.toString());
						} else if (this.checked == false) {
							checknow_namedata.remove($(this).attr("data-areaname"));
							checknow_data.remove($(this).val());
							$('#area_input').val(checknow_namedata.toString());
							$('#area_input').attr('data-areacode', checknow_data.toString());
						}
					}
				}
			}
		} else if (data.code == "-1") {
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	})
}
getcorplist(a,b);
var flase=0;
//获取店铺员工列表
function getstorelist(){
	var corp_code = $('#OWN_CORP').val();
	var area_code =$('#area_input').val();
	var _param={};
	_param['corp_code']=corp_code;
	_param['area_code']=area_code;
	oc.postRequire("post","/shop/selectByAreaCode", "", _param, function(data) {
		console.log(data);
	})
}
//
//点击弹出框
$('#test').click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	if(flase=="0"){
		$(".distribution_frame").show();
		getarealist();
		flase=1;
	}else if(flase=="1"){
		$(".distribution_frame").hide();
		flase=0;
	}	
})