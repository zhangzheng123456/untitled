var oc = new ObjectControl();
var a="";
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var isscroll=false;
//获取企业的下拉框
function getcorplist(a){
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
			$("#corp_select .searchable-select-item").click(function(){
				var c=$(this).attr("data-value");
				$("#sendee_r").empty();
				$("#area_input").val("");
				$("#area_input").attr("data-areacode","");
				$("#store_input").val("");
				$("#store_input").attr("data-storecode","");
				$("#staff_input").val("");
				$("#staff_input").attr("data-usercode","");
				$("#staff_input").attr("data-userphone","");
				$("#group_input").val("");
				$("#group_input").attr("data-groupcode","");
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
//点击接收人
$("#add_sendee").click(function(){
	var send_mode=$("#send_mode").val();
	if(send_mode==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请选择发送范围"
		});
	}
	if(send_mode=="指定区域"){
		isscroll=false;
		area_num=1;
		whir.loading.add("",0.5);
		var arr=whir.loading.getPageSize();
	    var left=(arr[0]-$("#screen_shop").width())/2;
	    var tp=(arr[1]-$("#screen_shop").height())/2+80;
	    $("#screen_area .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		$("#loading").remove();
		$("#screen_area").show();
		$("#screen_area").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_area .screen_content_l").unbind("scroll");
		getarealist(area_num);
	}
	if(send_mode=="指定店铺"){
		isscroll=false;
		shop_num=1;
		whir.loading.add("",0.5);
		var arr=whir.loading.getPageSize();
	    var left=(arr[0]-$("#screen_shop").width())/2;
	    var tp=(arr[1]-$("#screen_shop").height())/2+80;
	    $("#screen_shop .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		$("#loading").remove();
		$("#screen_shop").show();
		$("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_shop .screen_content_l").unbind("scroll");
		getstorelist(shop_num);
	}
	if(send_mode=="指定员工"){
		isscroll=false;
		staff_num=1;
		whir.loading.add("",0.5);
		var arr=whir.loading.getPageSize();
	    var left=(arr[0]-$("#screen_staff").width())/2;
	    var tp=(arr[1]-$("#screen_staff").height())/2+80;
	    $("#screen_staff .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		$("#loading").remove();
		$("#screen_staff").show();
		$("#screen_staff").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_staff .screen_content_l").unbind("scroll");
		getstafflist(staff_num);
	}
})
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.checked==true){
        input.checked = false;
    }
})
function bianse(){
    $(".screen_content_l li:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:even").css("backgroundColor","#ededed");
    $(".screen_content_r li:odd").css("backgroundColor","#fff");
    $(".screen_content_r li:even").css("backgroundColor","#ededed");
}
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
    	isscroll=false;
	    $("#screen_area .screen_content_l").unbind("scroll");
    	$("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function(){
	var event=window.event||arguments[0];
	shop_num=1;
	if(event.keyCode==13){
		isscroll=false;
		$("#screen_shop .screen_content_l ul").unbind("scroll");
		$("#screen_shop .screen_content_l ul").empty();
		getstorelist(shop_num);
	}
})
//品牌搜索
$("#brand_search").keydown(function(){
	var event=window.event||arguments[0];
	if(event.keyCode==13){
		$("#screen_brand .screen_content_l ul").empty();
		getbrandlist();
	}
})
//店铺放大镜搜索
$("#store_search_f").click(function(){
	shop_num=1;
	isscroll=false;
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function(){
	area_num=1;
	isscroll=false;
	$("#screen_area .screen_content_l").unbind("scroll");
	$("#screen_area .screen_content_l ul").empty();
	getarealist(area_num);
})
//区域关闭
$("#screen_close_area").click(function(){
	$("#screen_area").hide();
	whir.loading.remove();//移除遮罩层
})
//员工关闭
$("#screen_close_staff").click(function(){
	$("#screen_staff").hide();
	whir.loading.remove();//移除遮罩层
})
//店铺关闭
$("#screen_close_shop").click(function(){
	$("#screen_shop").hide();
	whir.loading.remove();//移除遮罩层
	$("#screen_shop .screen_content_l").unbind("scroll");
})
//品牌关闭
$("#screen_close_brand").click(function(){
	$("#screen_brand").hide();
	whir.loading.remove();//移除遮罩层
})
//拉取区域
function getarealist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_command = "/area/selAreaByCorpCode";
	var searchValue=$("#area_search").val().trim();
	var pageSize=20;
	var pageNumber=a;
	var _param = {};
	_param["corp_code"] = corp_code;
	_param["searchValue"]=searchValue;
	_param["pageSize"]=pageSize;
	_param["pageNumber"]=pageNumber;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	console.log(13123);
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var area_html_left = '';
			var area_html_right='';
			if (list.length == 0) {
				if(a==1){
					for(var h=0;h<9;h++){
						area_html_left+="<li></li>";
					}
				}
				area_next=true;
			} else {
				if(list.length<9&&a==1){
					for (var i = 0; i < list.length; i++) {
					    area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'/><label for='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
					}
					for(var j=0;j<9-list.length;j++){
						area_html_left+="<li></li>"
					}
				}else if(list.length>=9||list.length<9&&a>1){
					for (var i = 0; i < list.length; i++) {
					    area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'/><label for='checkboxOneInput"
	                        + i
	                        + a
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
					}
				}
				area_num++;
				area_next=false;
			}
			$("#screen_area .screen_content_l ul").append(area_html_left);
			console.log(isscroll);
			if(!isscroll){
				$("#screen_area .screen_content_l").bind("scroll",function () {
					var nScrollHight = $(this)[0].scrollHeight;
				    var nScrollTop = $(this)[0].scrollTop;
				    var nDivHight=$(this).height();
				    if(nScrollTop + nDivHight >= nScrollHight){
				    	if(area_next){
				    		return;
				    	}
				    	getarealist(area_num);
				    };
				})
			}
			isscroll=true;
			var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
			bianse();
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
	var area_code =$('#area_num').attr("data-areacode");//
	var brand_code=$('#brand_num').attr("data-brandcode");
	var searchValue=$("#store_search").val();
	var pageSize=20;
	var pageNumber=a;
	var _param={};
	_param['corp_code']=corp_code;
	_param['area_code']=area_code;
	_param['brand_code']=brand_code;
	_param['searchValue']=searchValue;
	_param['pageNumber']=pageNumber;
	_param['pageSize']=pageSize;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	// oc.postRequire("post","/user/stores","", _param, function(data) {
	oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {

	if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var store_html = '';
			if (list.length == 0){
				if(a==1){
					for(var h=0;h<9;h++){
						store_html+="<li></li>";
					}
				}
				shop_next=true;
			} else {
				if(list.length<9&&a==1){
					for (var i = 0; i < list.length; i++) {
				    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
					}
					for(var j=0;j<9-list.length;j++){
						store_html+="<li></li>"
					}
				}else if(list.length>=9||list.length<9&&a>1){
					for (var i = 0; i < list.length; i++) {
				    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
					}
				}
				shop_num++;
				shop_next=false;
			}
			$("#screen_shop .screen_content_l ul").append(store_html);
			if(!isscroll){
				$("#screen_shop .screen_content_l").bind("scroll",function () {
					var nScrollHight = $(this)[0].scrollHeight;
				    var nScrollTop = $(this)[0].scrollTop;
				    var nDivHight=$(this).height();
				    if(nScrollTop + nDivHight >= nScrollHight){
				    	if(shop_next){
				    		return;
				    	}
				    	getstorelist(shop_num);
				    };
				})
		    }
		    isscroll=true;
			var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
			bianse();
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
	var searchValue=$('#staff_search').val();
    var area_code ="";
    var brand_code="";
    var store_code="";
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param["corp_code"]=corp_code;
    _param['area_code']=area_code;
    _param['brand_code']=brand_code;
    _param['store_code']=store_code;
    _param['searchValue']=searchValue;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
        if (data.code == "0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            var staff_html = '';
            if (list.length == 0){
                if(a==1){
                    for(var h=0;h<9;h++){
                        staff_html+="<li></li>";
                    }
                }
                staff_next=true;
            } else {
                if(list.length<9&&a==1){
                    for (var i = 0; i < list.length; i++) {
                    staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].user_name+"</span></li>"
                    }
                    for(var j=0;j<9-list.length;j++){
                        staff_html+="<li></li>"
                    }
                }else if(list.length>=9||list.length<9&&a>1){
                    for (var i = 0; i < list.length; i++) {
                    staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].user_name+"</span></li>"
                    }
                }
                staff_num++;
                staff_next=false;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            if(!isscroll){
				$("#screen_staff .screen_content_l").bind("scroll",function () {
					var nScrollHight = $(this)[0].scrollHeight;
				    var nScrollTop = $(this)[0].scrollTop;
				    var nDivHight=$(this).height();
				    if(nScrollTop + nDivHight >= nScrollHight){
				    	if(staff_next){
				    		return;
				    	}
				    	getstafflist(staff_num);
				    };
				})
		    }
		    isscroll=true;
			var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
			}
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//点击区域的确定
$("#screen_que_area").click(function(){
	var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
	var area_code="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		if(i<li.length-1){
            area_code+=r+",";
        }else{
            area_code+=r;
        }
	}
	$("#sendee_r").attr("data-code",area_code);
	$("#screen_area").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	whir.loading.remove();//移除遮罩层
})
//点击店铺的确定
$("#screen_que_shop").click(function(){
	var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
	var store_code="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		if(i<li.length-1){
            store_code+=r+",";
        }else{
            store_code+=r;
        }
	}
	$("#sendee_r").attr("data-code",store_code);
	$("#screen_shop").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	whir.loading.remove();//移除遮罩层
})
//点击员工的确定
$("#screen_que_staff").click(function(){
	var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
	var store_code="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		if(i<li.length-1){
            store_code+=r+",";
        }else{
            store_code+=r;
        }
	}
	$("#sendee_r").attr("data-code",store_code);
	$("#screen_staff").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	whir.loading.remove();//移除遮罩层
})
//移到右边
function removeRight(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
	}
	if(li.length=="0"){
		frame();
		$('.frame').html("请先选择");
		return;
	}
	if(li.length>0){
		for(var i=0;i<li.length;i++){
			var html=$(li[i]).html();
			var id=$(li[i]).find("input[type='checkbox']").val();
			$(li[i]).find("input[type='checkbox']")[0].checked=true;
			var input=$(b).parents(".screen_content").find(".screen_content_r li");
			for(var j=0;j<input.length;j++){
				if($(input[j]).attr("id")==id){
					$(input[j]).remove();
				}
			}
			$(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
			$(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
	bianse();
}
//移到左边
function removeLeft(a,b){
	var li="";
	if(a=="only"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
	}
	if(a=="all"){
		li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
	}
	if(li.length=="0"){
		frame();
		$('.frame').html("请先选择");
		return;
	}
	if(li.length>0){
		for(var i=li.length-1;i>=0;i--){
			$(li[i]).remove();
			$(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
		}
	}
	var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
	$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
	bianse();
}
//点击右移
$(".shift_right").click(function(){
	var right="only";
	var div=$(this);
	removeRight(right,div);
})
//点击右移全部
$(".shift_right_all").click(function(){
	var right="all";
	var div=$(this);
	removeRight(right,div);
})
//点击左移
$(".shift_left").click(function(){
	var left="only";
	var div=$(this);
	removeLeft(left,div);
})
//点击左移全部
$(".shift_left_all").click(function(){
	var left="all";
	var div=$(this);
	removeLeft(left,div);
})
getcorplist(a);
//编辑关闭
$("#edit_close").click(function(){
	// $("#page-wrapper").hide();
 // 	$("#content").show();
 // 	$("#details").hide();
 	window.location.reload();
});
//新增关闭
$("#send_close").click(function(){
	$(window.parent.document).find('#iframepage').attr("src","/message/message.html");
});
$("#back_message").click(function(){
	$(window.parent.document).find('#iframepage').attr("src","/message/message.html");
});

