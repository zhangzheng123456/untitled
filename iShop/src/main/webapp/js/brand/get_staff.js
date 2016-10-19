var oc = new ObjectControl();
var a="";
var b="";
var num=1;//区域默认第一页
var dnum=1;//店铺默认第一页
var ynum=1;//员工默认第一页
//获取区域列表
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
				$("#area_code ul").html("<li class='search_c'>暂无区域</li>");
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
				$("#store_code ul").html("<li class='search_c'>暂无店铺</li>");
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
	var area_code =$('#area_input').attr("data-areacode");
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
	_param['area_code']=area_code;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	$("#staff_input").val("");
	$("#staff_input").attr("data-usercode","");
	$("#staff_input").attr("data-userphone","");
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
				$("#staff_code ul").html("<li class='search_c'>暂无员工</li>");
				$("#staff_more").hide();
			} else {
				for (var i = 0; i < list.length; i++) {
				    staff_html+="<li><div class='checkbox_isactive'><input  type='checkbox' value='"+list[i].user_code+"' data-username='"+list[i].user_name+"' name='"+list[i].phone+"'  class='check'  id='checkboxThreeInput"
                        + i
                        + 1
                        + "'/><label for='checkboxThreeInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].user_name+"("+list[i].phone+")</span></li>"
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
//点击弹出框
var flase=0;
$('#test').click(function(e){
	var event = window.event || arguments[0];
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
	$("#Acc_dropdown").hide();
	if(flase=="0"){
		$(".distribution_frame").show();
		flase=1;
		var left=($(window).width()-$(".distribution_frame").width())/2;//弹框定位的left值
		var tp=($(".fadeInRight").height()-$(".distribution_frame").height())/2+100;//弹框定位的top值
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
		var a=$('.xingming input');
		for(var j=0;j<a.length;j++){
			if($(a[j]).attr("data-code")==user_codes[i]){
				$(a[j]).parent("p").remove();
			}
		}
		//$('.xingming').append("<li data-code='"+user_codes[i]+"' data-phone='"+phone[i]+"'>"+user_names[i]+"<div class='delectxing' onclick='deleteName(this)'></div></li>");
		$('.xingming').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+user_codes[i]+"' data-phone='"+phone[i]+"' value='"+user_names[i]+"'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>");
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
	    $('.Acc_dropdown').hide();
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
//删除名称
function deleteName(a){
	$(a).parent("p").remove();
}