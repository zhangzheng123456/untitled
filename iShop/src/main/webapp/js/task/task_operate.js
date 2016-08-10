var oc = new ObjectControl();
var a="";
var b="";
var num=1;//区域默认第一页
var dnum=1;//店铺默认第一页
var ynum=1;//员工默认第一页
//获取企业类型的下拉框
var start = {
    elem: '#target_start_time',
    format: 'YYYY-MM-DD',
    min: laydate.now(), //设定最小日期为当前日期
    max: '2099-06-16 23:59:59', //最大日期
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
    istime: true,
    istoday: false,
    choose: function(datas) {
    	start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);
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
			$("#OWN_CORP").empty();
			$('#corp_select .searchable-select').remove();
			var corp_html="";
			for(var i=0;i<msg.corps.length;i++){
				corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
			}
			$("#OWN_CORP").append(corp_html);
			if(a!==""){
				$("#OWN_CORP option[value='"+a+"']").attr("selected","true");
			}
			$("#OWN_CORP").searchableSelect();
			var c=$('#OWN_CORP').val();
			getasktypelist(c,b);
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				getasktypelist(c,b);
				$(".xingming").empty();
				$("#area_input").val("");
				$("#area_input").attr("data-areacode","");
				$("#store_input").val("");
				$("#store_input").attr("data-storecode","");
				$("#staff_input").val("");
				$("#staff_input").attr("data-usercode","");
				$("#staff_input").attr("data-userphone","");
				$("#store_code ul").empty();
				$("#staff_code ul").empty();
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
function getarealist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_command = "/area/selAreaByCorpCode";
	var searchValue=$("#area_search").val().trim();
	var pageSize=100;
	var pageNumber=a;
	var _param = {};
	var checknow_data=[];
    var checknow_namedata=[];
	_param["corp_code"] = corp_code;
	_param["searchValue"]=searchValue;
	_param["pageSize"]=pageSize;
	_param["pageNumber"]=pageNumber;
	$("#area_input").val("");
	$("#area_input").attr("data-areacode","");
	$("#store_input").val("");
	$("#store_input").attr("data-storecode","");
	$("#staff_input").val("");
	$("#staff_input").attr("data-usercode","");
	$("#staff_input").attr("data-userphone","");
	$("#store_code ul").empty();
	$("#staff_code ul").empty();
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var area_html = '';
			if (list.length == 0) {
				$("#area_code ul").html("<li class='search_c'>没有内容</li>");
				$("#area_more").hide();
			} else {
				for (var i = 0; i < list.length; i++) {
				    area_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxOneInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
				}
				if(pageNumber==1){
					$("#area_code ul").html(area_html);
				}else if(pageNumber>1){
					$("#area_code ul").append(area_html);
				}
				if(pageNumber==cout){
					$("#area_more").hide();
				}
				if(cout>1&&pageNumber<cout){
					$("#area_more").show();
				}
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
}
//获取店铺列表
function getstorelist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_code =$('#area_input').attr("data-areacode");
	var searchValue=$("#store_search").val();
	if(corp_code==""||area_code==""||area_code==undefined){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择区域"
		});
		return;
	}
	var pageSize=100;
	var pageNumber=a;
	var _param={};
	var checknow_data=[];
    var checknow_namedata=[];
	_param['corp_code']=corp_code;
	_param['area_code']=area_code;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	$("#store_input").val("");
	$("#store_input").attr("data-storecode","");
	$("#staff_input").val("");
	$("#staff_input").attr("data-usercode","");
	$("#staff_input").attr("data-userphone","");
	$("#store_code ul").empty();
	$("#staff_code ul").empty();
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/shop/selectByAreaCode", "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var store_html = '';
			if (list.length == 0) {
				$("#store_code ul").html("<li class='search_c'>没有内容</li>");
				$("#store_more").hide();
			} else {
				for (var i = 0; i < list.length; i++) {
				    store_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
				}
				if(pageNumber==1){
					$("#store_code ul").html(store_html);
				}else if(pageNumber>1){
					$("#store_code ul").append(store_html);
				}
				if(pageNumber==cout){
					$("#store_more").hide();
				}
				if(cout>1&&pageNumber<cout){
					$("#store_more").show();
				}
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
}
//获取员工列表
function getstafflist(a){
	var corp_code = $('#OWN_CORP').val();
	var store_code =$('#store_input').attr("data-storecode");
	var searchValue=$("#staff_search").val();
	var pageSize=100;
	var pageNumber=a;
	if(corp_code==""||store_code==""||store_code==undefined){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择店铺"
		});
		return;
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
	$("#staff_input").val("");
	$("#staff_input").attr("data-usercode","");
	$("#staff_input").attr("data-userphone","");
	$("#staff_code ul").empty();
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/user/selectByPart", "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            console.log(list);
			var staff_html = '';
			if (list.length == 0) {
				$("#staff_code ul").html("<li class='search_c'>没有内容</li>");
				$("#staff_more").hide();
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
				if(pageNumber==1){
					$("#staff_code ul").html(staff_html);
				}else if(pageNumber>1){
					$("#staff_code ul").append(staff_html);
				}
				if(pageNumber==cout){
					$("#staff_more").hide();
				}
				if(cout>1&&pageNumber<cout){
					$("#staff_more").show();
				}
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
							num=1
						}
					}
				}
				whir.loading.remove();//移除加载框
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
		var left=($(window).width()-$(".distribution_frame").width())/2;//弹框定位的left值
		var tp=($(window).height()-$(".distribution_frame").height())/2;//弹框定位的top值
		$(".distribution_frame").css({"left":+left+"px","top":+tp+"px"});
		var corp_code = $('#OWN_CORP').val();
		var corp_code1=$('#OWN_CORP').attr("corp_code");
		if(corp_code==corp_code1){
			return;
		}
		$('#OWN_CORP').attr("corp_code",corp_code);
		num=1;
		getarealist(num);
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
	dnum=1;
	getstorelist(dnum);
})
//点击店铺拉取员工
$("#store_code_drop").click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	ynum=1;
	getstafflist(ynum);
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
	if(user_names==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请选择员工"
		});
		return;
	}
	user_names=user_names.split(',');
	user_codes=user_codes.split(',');
	phone=phone.split(',');
	console.log(user_names);
	console.log(user_codes);
	console.log(phone);
	var ul="";
	for(var i=0;i<user_names.length;i++){
		var a=$('.xingming li');
		for(var j=0;j<a.length;j++){
			if($(a[j]).attr("data-code")==user_codes[i]){
				$(a[j]).remove();
			}
		}
		$('.xingming').append("<li data-code='"+user_codes[i]+"' data-phone='"+phone[i]+"'>"+user_names[i]+"<div class='delectxing' onclick='deleteName(this)'></div></li>");	
	}
	//删除姓名
	$(".xingming li").hover(function(){
	    $(this).find('.delectxing').show();
	},function(){
	    $(this).find('.delectxing').hide();
	})
	$(".distribution_frame").hide();
	flase=0;
})
$(document).click(function(e){
	if($(e.target).is('.drop-down')||$(e.target).is('.drop-down input')||$(e.target).is('.drop-down .area_more')||$(e.target).is('.drop-down .drop_down_ul')||$(e.target).is('.drop-down span')||$(e.target).is('.drop-down .search i')||$(e.target).is('.drop-down h5')||$(e.target).is('.drop-down .checkbox_isactive')||$(e.target).is('.checkbox_isactive label')||$(e.target).is('.drop-down ul li')||$(e.target).is('.drop-down ul')){
	    return;
    }else{
	    $("#distribution_frame").hide();
	    flase=0;
	}
});
//区域搜索的
$("#area_search").keydown(function() {
    var event=window.event||arguments[0];
    num=1;
    if(event.keyCode == 13){
       getarealist(num);
    }
});
//区域放大镜搜索
$("#area_search_d").click(function(){
	num=1
	getarealist(num);
})
//区域加载更多按钮
$("#area_more").click(function(){
	num++;
	getarealist(num);
})
//店铺搜索
$("#store_search").keydown(function() {
    var event=window.event||arguments[0];
    dnum=1;
    if(event.keyCode == 13){
		getstorelist(dnum);
    }
});
//店铺放大镜搜索
$("#store_search_d").click(function(){
	dnum=1
	getstorelist(dnum);
})
//店铺加载更多按钮
$("#store_more").click(function(){
	dnum++;
	getstorelist(dnum);
})
//员工搜索
$("#staff_search").keydown(function() {
    var event=window.event||arguments[0];
    ynum=1;
    if(event.keyCode == 13){
		getstafflist(ynum);
    }
});
//员工放大镜搜索
$("#staff_search_d").click(function(){
	ynum=1
	getstafflist(ynum);
})
//员工加载更多按钮
$("#staff_more").click(function(){
	ynum++;
	getstafflist(ynum);
})
//点击保存
$("#add_save").click(function(){
	var _param={};
	var a=$('.xingming li');
	var user_codes="";
	var phone="";
	for(var i=0;i<a.length;i++){
        var u=$(a[i]).attr("data-code");
        var p=$(a[i]).attr("data-phone");
        if(i<a.length-1){
            user_codes+=u+",";
            phone+=p+",";
        }else{
             user_codes+=u;
             phone+=p;
        }     
    }
	var corp_code = $('#OWN_CORP').val();//公司编号
	var task_type_code = $('#task_type_code').val();//公司类型
	var task_title=$('#task_title').val();//任务名称
	var task_description=$("#task_describe").val();//任务描述
	var target_end_time=$("#target_end_time").val();//截止时间
	var target_start_time=$("#target_start_time").val();//开始时间
	var isactive = "";//是否可用
	var input = $("#is_active")[0];
	if(task_type_code==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务类型不能为空"
		});
		return;
	}
	if(task_title==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务名称不能为空"
		});
		return;
	}
	if(task_description==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务描述不能为空"
		});
		return;
	}
	if(user_codes==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"员工不能为空"
		});
		return;
	}
	if(target_end_time==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"截止时间不能为空"
		});
		return;
	}
	if(target_start_time==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"开始时间不能为空"
		});
		return;
	}
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
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/task/addTask","", _param, function(data) {
		if(data.code=="0"){
			if(data.message=="新增成功"){
				$(window.parent.document).find('#iframepage').attr("src","/task/task.html");
				whir.loading.remove();//移除加载框
			}
		}
	})
})
//编辑点击保存
$("#edit_save").click(function(){
	var _param={};
	var a=$('.xingming li');
	var user_codes="";
	var phone="";
	for(var i=0;i<a.length;i++){
        var u=$(a[i]).attr("data-code");
        var p=$(a[i]).attr("data-phone");
        if(i<a.length-1){
            user_codes+=u+",";
            phone+=p+",";
        }else{
             user_codes+=u;
             phone+=p;
        }     
    }
    console.log(user_codes);
    console.log(phone);
	var corp_code = $('#OWN_CORP').val();//公司编号
	var task_type_code = $('#task_type_code').val();//公司类型
	var task_title=$('#task_title_e').val();//任务名称
	var task_description=$("#task_describe").val();//任务描述
	var target_end_time=$("#target_end_time_e").val();//截止时间
	var target_start_time=$("#target_start_time_e").val();//开始时间
	var id=$('#task_id').val();//id名称
	if(task_type_code==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务类型不能为空"
		});
		return;
	}
	if(task_title==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务名称不能为空"
		});
		return;
	}
	if(task_description==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"任务描述不能为空"
		});
		return;
	}
	if(user_codes==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"员工不能为空"
		});
		return;
	}
	if(target_end_time==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"截止时间不能为空"
		});
		return;
	}
	if(target_start_time==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"开始时间不能为空"
		});
		return;
	}
	var task_code=$('#task_code_e').val();
	var isactive = "";//是否可用
	var input = $("#is_active")[0];
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
	_param["task_code"]=task_code;//任务编号
	_param["id"]=id;//id名称
	_param["isactive"]=isactive;
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/task/edit","", _param, function(data) {
		if(data.code=="0"){
			// $("#page-wrapper").hide();
 		// 	$("#content").show();
 		// 	$("#details").hide();
 			window.location.reload();
 			whir.loading.remove();//移除加载框
		}
		console.log(data);
	})
})
if($(".pre_title label").text()=="新增任务"){
	getcorplist(a,b);
}
var param = {};
function nssignment(){//加载list的文件
	oc.postRequire("post", "/task/selectTaskById", "0", param, function(data) {
 		var msg=data.message;
 		var msg=JSON.parse(msg);
 		var list=JSON.parse(msg.list);
 		var msg=JSON.parse(msg.task);
 		console.log(list);
 		console.log(msg);
 		var corp_code=msg.corp_code;//公司编号
 		var task_code=msg.task_code;//任务编号
 		var ul="";
 		for(var i=0;i<list.length;i++){
 			ul+="<li data-code='"+list[i].user_code+"' data-phone='"+list[i].phone+"'>"+list[i].user_name+"<div class='delectxing' onclick='deleteName(this)'></div></li>";	
 		}
 		$('.xingming').html(ul);
 		$(".xingming li").hover(function(){
	    	$(this).find('.delectxing').show();
		},function(){
		    $(this).find('.delectxing').hide();
		})
 		$("#task_title_e").val(msg.task_title);//任务名称
 		$("#task_describe").val(msg.task_description);//任务描述
 		$("#task_code_e").val(msg.task_code);//任务编号
 		$("#target_start_time_e").val(msg.target_start_time);//开始时间
 		$("#target_end_time_e").val(msg.target_end_time);//截止时间
 		$("#created_time").val(msg.created_date);//创建时间
		$("#creator").val(msg.creater);//创建人
		$("#modify_time").val(msg.modified_date);//修改时间
		$("#modifier").val(msg.modifier);//修改人
 		getcorplist(corp_code,task_code);//
 		whir.loading.remove();//移除加载框
 	});
}
//双击进入编辑界面
function editAssignment(a){
	whir.loading.add("",0.5);//加载等待框
	$("#page-wrapper").show();
 	$("#content").hide();
 	$("#details").hide();
 	var id = $(a).attr("id");
 	var corp_code = $(a).find(".corp_code").attr("data-code");
 	var task_code = $(a).find("td:eq(2) span").html();
 	param["corp_code"] = corp_code;//公司编号
 	param["task_code"] = task_code;//任务编号
 	param["id"] = id;//公司id
 	$('#task_id').val(id);
 	nssignment();
}
//编辑进入界面
function editAssignmentb(a){
    var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
	if (tr.length>1||tr.length=='0') {
		return;
	}
	whir.loading.add("",0.5);//加载等待框
	var id=$(tr).attr("id");
	var corp_code=$(tr).find(".corp_code").attr("data-code");
	var task_code=$(tr).find("td:eq(2) span").html();
	param["corp_code"] = corp_code;//公司编号
 	param["task_code"] = task_code;//任务编号
 	param["id"] = id;//公司id
 	nssignment();
}
//删除名称
function deleteName(a){
	$(a).parent("li").remove();
}
//编辑关闭
$("#edit_close").click(function(){
	// $("#page-wrapper").hide();
 // 	$("#content").show();
 // 	$("#details").hide();
 	window.location.reload();
})
//新增关闭
$("#add_close").click(function(){
	$(window.parent.document).find('#iframepage').attr("src","/task/task.html");
})
