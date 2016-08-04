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
//获取企业的下拉框
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
	var area_command = "/area/selAreaByCorpCode";
	var searchValue=$("#area_search").val();
	var pageSize=20;
	var pageNumber=1;
	var _param = {};
	var checknow_data=[];
    var checknow_namedata=[];
	_param["corp_code"] = corp_code;
	_param["searchValue"]=searchValue;
	_param["pageSize"]=pageSize;
	_param["pageNumber"]=pageNumber;
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var area_html = '';
			if (list.length == 0) {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: "该企业目前分配区域！"
				});
			} else {
				for (var i = 0; i < list.length; i++) {
				    area_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + 1
                        + "'/><label for='checkboxOneInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
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
//执行企业的拉取方法
getcorplist(a,b);
//获取店铺列表
function getstorelist(){
	var corp_code = $('#OWN_CORP').val();
	var area_code =$('#area_input').attr("data-areacode");
	var searchValue=$("#store_search").val();
	if(corp_code==""||area_code==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择区域"
		});
	}
	var pageSize=20;
	var pageNumber=1;
	var _param={};
	var checknow_data=[];
    var checknow_namedata=[];
	_param['corp_code']=corp_code;
	_param['area_code']=area_code;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	oc.postRequire("post","/shop/selectByAreaCode", "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var store_html = '';
			if (list.length == 0) {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: "该区域没有店铺"
				});
			} else {
				for (var i = 0; i < list.length; i++) {
				    store_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
				}
				$("#store_code ul").html(store_html);
				var check_input = $('#store_code ul input');
				for (var c = 0; c < check_input.length; c++) {
					check_input[c].onclick = function() {
						if (this.checked == true) {
							checknow_data.push($(this).val());
							checknow_namedata.push($(this).attr("data-storename"));
							$('#store_input').val(checknow_namedata.toString());
							$('#store_input').attr('data-storecode', checknow_data.toString());
						} else if (this.checked == false) {
							checknow_namedata.remove($(this).attr("data-storename"));
							checknow_data.remove($(this).val());
							$('#store_input').val(checknow_namedata.toString());
							$('#store_input').attr('data-storecode', checknow_data.toString());
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
//获取员工列表
function getstafflist(){
	var corp_code = $('#OWN_CORP').val();
	var store_code =$('#store_input').attr("data-storecode");
	var searchValue=$("#staff_search").val();
	var pageSize=20;
	var pageNumber=1;
	if(corp_code==""||store_code==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择店铺"
		});
	}
	var _param={};
	var checknow_data=[];
    var checknow_namedata=[];
    var checknow_phone=[];
	_param['corp_code']=corp_code;
	_param['store_code']=store_code;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	oc.postRequire("post","/user/selectByPart", "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            console.log(list);
			var staff_html = '';
			if (list.length == 0) {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: "该店铺没有员工"
				});
			} else {
				for (var i = 0; i < list.length; i++) {
				    staff_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].user_code+"' data-username='"+list[i].user_name+"' name='"+list[i].phone+"'  class='check'  id='checkboxThreeInput"
                        + i
                        + 1
                        + "'/><label for='checkboxThreeInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].user_name+"</span></li>"
				}
				$("#staff_code ul").html(staff_html);
				var check_input = $('#staff_code ul input');
				for (var c = 0; c < check_input.length; c++) {
					check_input[c].onclick = function() {
						if (this.checked == true) {
							checknow_data.push($(this).val());
							checknow_namedata.push($(this).attr("data-username"));
							checknow_phone.push($(this).attr("name"));
							$('#staff_input').val(checknow_namedata.toString());
							$('#staff_input').attr('data-usercode', checknow_data.toString());
							$('#staff_input').attr('data-userphone', checknow_phone.toString());
						} else if (this.checked == false) {
							checknow_namedata.remove($(this).attr("data-username"));
							checknow_data.remove($(this).val());
							checknow_phone.remove($(this).attr("name"));
							$('#staff_input').val(checknow_namedata.toString());
							$('#staff_input').attr('data-usercode', checknow_data.toString());
							$('#staff_input').attr('data-userphone', checknow_phone.toString());
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
//点击弹出框
var flase=0;
var corp_code1="";
$('#test').click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	if(flase=="0"){
		$(".distribution_frame").show();
		flase=1;
		getarealist();
	}else if(flase=="1"){
		$(".distribution_frame").hide();
		flase=0;
	}	
})
//点击区域拉取店铺
$("#area_code_drop").click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	getstorelist();
})
//点击店铺拉去员工
$("#store_code_drop").click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	getstafflist();
})
//点击员工确定
$("#staff_code_drop").click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	var user_names=$('#staff_input').val();
	var user_codes=$('#staff_input').attr("data-usercode");
	var phone=$('#staff_input').attr("data-userphone");
})
//点击保存
$("#edit_save").click(function(){
	var _param={};
	var user_codes=$('#staff_input').attr("data-usercode");//员工编号
	var phone=$('#staff_input').attr("data-userphone");//手机号
	var corp_code = $('#OWN_CORP').val();//公司编号
	var task_type_code = $('#task_type_code').val();//公司类型
	var task_title=$('#task_title').val();//任务名称
	var task_description=$("#task_describe").val();//任务描述
	var target_end_time=$("#target_end_time").val();//截止时间
	var target_start_time=$("#target_start_time").val();//开始时间
	var isactive = "";//是否可用
	var input = $(".checkbox_isactive").find("input")[0];
	if (input.checked == true) {
		isactive = "Y";
	} else if (input.checked == false) {
		isactive = "N";
	}
	_param["user_codes"]=user_codes;
	_param["phone"]=phone;
	_param["corp_code"]=corp_code;
	_param["task_type_code"]=task_type_code;
	_param["task_title"]=task_title;
	_param["task_description"]=task_description;
	_param["target_end_time"]=target_end_time;
	_param["target_start_time"]=target_start_time;
	_param["isactive"]=isactive;
	oc.postRequire("post","/task/addTask","", _param, function(data) {
		console.log(data);
	})
})