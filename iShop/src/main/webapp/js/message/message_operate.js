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
				$("#sendee_r").val("已选0个");
				$(".s_pitch span").html("0");
				$("#sendee_r").attr("data-code","");
				$("#sendee_r").attr("data-name","");
				$("#sendee_r").attr("data-phone","");
				$(".screen_content_r ul").empty();
				$(".input_search input").val("");
				$("#sendee_r").attr("data-type","");
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
function getmodelist(){
	oc.postRequire("post","/message/pullSendScope", "","", function(data) {
		if(data.code=="0"){
			var message=JSON.parse(data.message);
			var send_scope=message.send_scope;
			var html="";
			for(var i=0;i<send_scope.length;i++){
				var data_type="";
				if(send_scope[i]=="全体成员"){
					data_type="corp";
				}
				if(send_scope[i]=="指定区域"){
					data_type="area";
				}
				if(send_scope[i]=="指定店铺"){
					data_type="store";
				}
				if(send_scope[i]=="指定员工"){
					data_type="staff";
				}
				html+="<li data-type='"+data_type+"''>"+send_scope[i]+"</li>"
			}
		}
		$("#drop_down ul").html(html);
	})
}
getmodelist();
getcorplist(a);
$("#drop_down ul").on("click","li",function(){
	var txt = $(this).text();
	if(txt=="全体成员"){
		$("#sendee").hide();
	}
	if(txt=="指定区域"){
		$("#sendee").show();
		if($(this).attr("data-type")==$("#sendee_r").attr("data-type")){
			return;
		}
		$("#sendee_r").val("已选0个");
		$(".s_pitch span").html("0");
	    $("#sendee_r").attr("data-code","");
	    $("#sendee_r").attr("data-name","");
		$("#sendee_r").attr("data-phone","");
		$(".screen_content_r ul").empty();
		$(".input_search input").val("");
		$("#sendee_r").attr("data-type","");
	}
	if(txt=="指定店铺"){
		$("#sendee").show();
		if($(this).attr("data-type")==$("#sendee_r").attr("data-type")){
			return;
		}
		$("#sendee_r").val("已选0个");
		$(".s_pitch span").html("0");
	    $("#sendee_r").attr("data-code","");
	    $("#sendee_r").attr("data-name","");
		$("#sendee_r").attr("data-phone","");
		$(".screen_content_r ul").empty();
		$(".input_search input").val("");
		$("#sendee_r").attr("data-type","");
	}
	if(txt=="指定员工"){
		$("#sendee").show();
		if($(this).attr("data-type")==$("#sendee_r").attr("data-type")){
			return;
		}
		$("#sendee_r").val("已选0个");
		$(".s_pitch span").html("0");
	    $("#sendee_r").attr("data-code","");
	    $("#sendee_r").attr("data-name","");
		$("#sendee_r").attr("data-phone","");
		$(".screen_content_r ul").empty();
		$(".input_search input").val("");
		$("#sendee_r").attr("data-type","");
	}
	$("#send_mode").val(txt);
	$("#send_mode").attr("data-type",$(this).attr("data-type"));
	$("#drop_down ul").hide();
});
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
//店铺搜
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
//员工搜索
$("#staff_search").keydown(function(){
	var event=window.event||arguments[0];
	staff_num=1;
	if(event.keyCode==13){
		$("#screen_staff .screen_content_l ul").empty();
		getstafflist(staff_num);
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
	oc.postRequire("post", area_command, "", _param, function(data) {
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
			var area_html_left ='';
			var area_html_right='';
			var area_codes=$("#sendee_r").attr("data-code").split(',');
			var area_names=$("#sendee_r").attr("data-name").split(',');
			for(var h=0;i<area_codes.length;h++){
				area_html_right+="<li id='"+area_codes[i]+"'>\
				<div class='checkbox1'><input type='checkbox' value='"+list[i].area_codes[i]+"' data-areaname='"+list[i].area_name+"' name='test' class='check' id=''>\
				\
				</div>\
				</li>"
			}
			if (list.length == 0) {
				area_next=true;
			} else {
				if(list.length>0){
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
				shop_next=true;
			} else {
				if(list.length>0){
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
                staff_next=true;
            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                    staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
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
            bianse();
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
	var name="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		var h=$(li[i]).find(".p16").html();
		if(i<li.length-1){
            area_code+=r+",";
            name+=h+","
        }else{
            area_code+=r;
            name+=h;
        }
	}
	$("#sendee_r").attr("data-code",area_code);
	$("#sendee_r").attr("data-name",name);
	$("#screen_area").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	$("#sendee_r").attr("data-type","area");
	whir.loading.remove();//移除遮罩层
})
//点击店铺的确定
$("#screen_que_shop").click(function(){
	var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
	var store_code="";
	var name=""
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		var h=$(li[i]).find(".p16").html();
		if(i<li.length-1){
            store_code+=r+",";
            name+=h+",";
        }else{
            store_code+=r;
            name+=h;
        }
	}
	$("#sendee_r").attr("data-code",store_code);
	$("#sendee_r").attr("data-name",name);
	$("#screen_shop").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	$("#sendee_r").attr("data-type","store");
	whir.loading.remove();//移除遮罩层
})
//点击员工的确定
$("#screen_que_staff").click(function(){
	var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
	var store_code="";
	var phone="";
	var name="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		var p=$(li[i]).find("input").attr("data-phone");
		var h=$(li[i]).find(".p16").html();
		if(i<li.length-1){
            store_code+=r+",";
            phone+=p+",";
            name+=h+",";
        }else{
            store_code+=r;
            phone+=p;
            name+=h;
        }
	}
	$("#sendee_r").attr("data-code",store_code);
	$("#sendee_r").attr("data-phone",phone);
	$("#sendee_r").attr("data-name",name);
	$("#screen_staff").hide();
	$("#sendee_r").val("已选"+li.length+"个");
	$("#sendee_r").attr("data-type","staff");
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
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
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
		art.dialog({
			zIndex:10003,
			time: 1,
			lock: true,
			cancel: false,
			content: "请先选择"
		});
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
$("#send").click(function(){
	var param={};
	var corp_code = $('#OWN_CORP').val();//企业编号
	var send_mode=$('#send_mode').attr("data-type");
	var title=$("#message_title").val();
	var message_content=$("#message_content").val();
	param["corp_code"]=corp_code;
	param["title"]=title;
	param["message_content"]=message_content;
	param["send_mode"]=send_mode;
	if(corp_code==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "所属企业不能为空"
		});
		return;
	}
	if(send_mode==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "发送范围不能为空"
		});
		return;
	}
	if(send_mode=="corp"){
		param["store_id"]="";
		param["user_id"]={"user_id":"","phone":""};
		param["area_code"]="";
	}
	if(send_mode=="area"){
		var area_code=$("#sendee_r").attr("data-code");
		var area_code=area_code.split(",");
		var area_codes=[];
		for(var i=0;i<area_code.length;i++){
		 	var param1={"area_code":area_code[i]};
            area_codes.push(param1);
		}
		param["store_id"]="";
		param["user_id"]={"user_id":"","phone":""};
		param["area_code"]=area_codes;
		if(area_code==""){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "接受对象不能为空"
		    });
		    return;
		}
	}
	if(send_mode=="store"){
		var store_id=$("#sendee_r").attr("data-code");
		var store_id=store_id.split(",");
		var store_ids=[];
		for(var i=0;i<store_id.length;i++){
		 	var param1={"store_id":store_id[i]};
            store_ids.push(param1);
		}
		param["store_id"]=store_ids;
		param["user_id"]={"user_id":"","phone":""};
		param["area_code"]="";
		if(store_id==""){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "接受对象不能为空"
		    });
		    return;
		}
	}
	if(send_mode=="staff"){
		var user_id=$("#sendee_r").attr("data-code");
		var phone=$("#sendee_r").attr("data-phone");
		var user_id=user_id.split(",");
		var phone=phone.split(",");
		var user_ids=[];
		for(var i=0;i<user_id.length;i++){
		 	var param1={"user_id":user_id[i],"phone":phone[i]};
            user_ids.push(param1);
		}
		param["store_id"]="";
		param["user_id"]=user_ids;
		param["area_code"]="";
		if(user_id==""){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "接受对象不能为空"
		    });
		    return;
		}
	}
	if(title==""){
		art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "通知标题不能为空"
		});
		return;
	}
	if(message_content==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "通知内容不能为空"
		});
		return;
	}
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/message/add","",param, function(data){
		if(data.code=="0"){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "发送成功"
		    });
		}else if(data.code=="-1"){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "发送失败"
		    });
		}
		whir.loading.remove();//移除加载框
	})
});
//新增关闭
$("#send_close").click(function(){
	$(window.parent.document).find('#iframepage').attr("src","/message/message.html");
});
$("#back_message").click(function(){
	$(window.parent.document).find('#iframepage').attr("src","/message/message.html");
});

