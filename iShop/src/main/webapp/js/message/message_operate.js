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
				$("#sendee_r").val("已选0个");
				$(".area_num").val("全部");
				$(".area_num").attr("data-areacode","");
				$(".brand_num").val("全部");
				$(".brand_num").attr("data-brandcode","");
				$("#staff_shop_num").val("全部");
				$("#staff_shop_num").attr("data-storecode","");
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
		$("#sendee_r").attr("data-type","");
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
		$(".area_num").val("全部");
		$(".area_num").attr("data-areacode","");
		$(".brand_num").val("全部");
		$(".brand_num").attr("data-brandcode","");
		$("#staff_shop_num").val("全部");
		$("#staff_shop_num").attr("data-storecode","");
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
		$(".area_num").val("全部");
		$(".area_num").attr("data-areacode","");
		$(".brand_num").val("全部");
		$(".brand_num").attr("data-brandcode","");
		$("#staff_shop_num").val("全部");
		$("#staff_shop_num").attr("data-storecode","");
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
		$(".area_num").val("全部");
		$(".area_num").attr("data-areacode","");
		$(".brand_num").val("全部");
		$(".brand_num").attr("data-brandcode","");
		$("#staff_shop_num").val("全部");
		$("#staff_shop_num").attr("data-storecode","");
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
	    var area_html_right="";
	    $("#screen_area .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		if($("#sendee_r").attr("data-code")!==""){
			var area_codes=$("#sendee_r").attr("data-code").split(',');
			var area_names=$("#sendee_r").attr("data-name").split(',');
			for(var h=0;h<area_codes.length;h++){
				area_html_right+="<li id='"+area_codes[h]+"'>\
				<div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"' data-areaname='"+area_names[h]+"' name='test' class='check' id=''>\
				<label></div><span class='p16'>"+area_names[h]+"</span>\
				\
				</li>"
			}
			$("#screen_area .s_pitch span").html(h);
			$("#screen_area .screen_content_r ul").html(area_html_right);
		}
		$("#loading").remove();
		$("#screen_area").show();
		// $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
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
	    var store_html_right="";
	    $("#screen_shop .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		if($("#sendee_r").attr("data-code")!==""){
			var store_codes=$("#sendee_r").attr("data-code").split(',');
			var store_names=$("#sendee_r").attr("data-name").split(',');
			for(var h=0;h<store_codes.length;h++){
				store_html_right+="<li id='"+store_codes[h]+"'>\
				<div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"' data-storename='"+store_names[h]+"' name='test' class='check'>\
				<label></div><span class='p16'>"+store_names[h]+"</span>\
				\
				</li>"
			}
			$("#screen_store .s_pitch span").html(h);
			$("#screen_store .screen_content_r ul").html(store_html_right);
		}
		$("#loading").remove();
		$("#screen_shop").show();
		// $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
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
	    var staff_html_right="";
	    $("#screen_staff .screen_content_l ul").empty();
		// $("#screen_area .screen_content_r ul").empty();
		if($("#sendee_r").attr("data-code")!==""){
			var staff_codes=$("#sendee_r").attr("data-code").split(',');
			var staff_names=$("#sendee_r").attr("data-name").split(',');
			var staff_phone=$("#sendee_r").attr("data-phone").split(',');
			var staff_html_right="";
			for(var h=0;h<staff_codes.length;h++){
				staff_html_right+="<li id='"+staff_codes[h]+"'>\
				<div class='checkbox1'><input type='checkbox' value='"+staff_codes[h]+"' data-phone='"+staff_phone[h]+"' data-storename='"+staff_names[h]+"' name='test' class='check'>\
				<label></div><span class='p16'>"+staff_names[h]+"</span>\
				\
				</li>"
			}
			$("#screen_staff .s_pitch span").html(h);
			$("#screen_staff .screen_content_r ul").html(staff_html_right);
		}
		$("#loading").remove();
		$("#screen_staff").show();
		// $("#screen_staff").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_staff .screen_content_l").unbind("scroll");
		getstafflist(staff_num);
	}
	if(send_mode=="全体成员"){

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
});
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
//员工搜索
$("#staff_search").keydown(function(){
	var event=window.event||arguments[0];
	staff_num=1;
	if(event.keyCode==13){
		isscroll=false;
	    $("#screen_staff .screen_content_l").unbind("scroll");
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
$("#staff_search_f").click(function(){
	staff_num=1;
	isscroll=false;
	$("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
	getstafflist(staff_num);
})
$("#brand_search_f").click(function(){
	$("#screen_brand .screen_content_l ul").empty();
	getbrandlist();
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
//获取品牌列表
function getbrandlist(){
	var corp_code = $('#OWN_CORP').val();
	var searchValue=$("#brand_search").val();
	var _param={};
	_param["corp_code"]=corp_code;
	_param["searchValue"]=searchValue;
	whir.loading.add("",0.5);//加载等待框
	$("#mask").css("z-index","10002");
	oc.postRequire("post","/shop/brand", "",_param, function(data){
		if (data.code == "0") {
			var message=JSON.parse(data.message);
            var list=message.brands;
			var brand_html_left = '';
			var brand_html_right='';
			if (list.length == 0){
				for(var h=0;h<9;h++){
					brand_html_left+="<li></li>"
				}
			} else {
				if(list.length<9){
					for (var i = 0; i < list.length; i++) {
					    brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
	                        + i
	                        + 1
	                        + "'/><label for='checkboxThreeInput"
	                        + i
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
					}
					for(var j=0;j<9-list.length;j++){
						brand_html_left+="<li></li>"
					}
				}else if(list.length>=9){
					for (var i = 0; i < list.length; i++) {
					    brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
	                        + i
	                        + 1
	                        + "'/><label for='checkboxThreeInput"
	                        + i
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
					}
				}
			}
			$("#screen_brand .screen_content_l ul").append(brand_html_left);
			var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
			for(var k=0;k<li.length;k++){
				$("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
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
};
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
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
			var area_html_left ='';
			var area_html_right='';
			if (list.length == 0) {

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
			}
			if(hasNextPage==true){
				area_num++;
				area_next=false;
			}
			if(hasNextPage==false){
				area_next=true;
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
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
			var store_html = '';
			if (list.length == 0){
				
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
			}
			if(hasNextPage==true){
				shop_num++;
				shop_next=false;
			}
			if(hasNextPage==false){
				shop_next=true;
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
    var area_code =$("#staff_area_num").attr("data-areacode");
    var brand_code=$("#staff_brand_num").attr("data-brandcode");
    var store_code=$("#staff_shop_num").attr("data-storecode");
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
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var staff_html = '';
            if (list.length == 0){
                
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
                        + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
				staff_num++;
                staff_next=false;
			}
			if(hasNextPage==false){
				staff_next=true;
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
		    $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
            $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
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
	if($("#send_mode").val()=="指定店铺"){
		isscroll=false;
		shop_num=1;
		$("#area_num").attr("data-areacode",area_code);
		$("#area_num").val("已选"+li.length+"个");
		$("#screen_shop .screen_content_l ul").empty();
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_area").hide();
		$("#screen_shop").show();
		getstorelist(shop_num);
	}
	if($("#send_mode").val()=="指定员工"){
		isscroll=false;
		staff_num=1;
		$("#staff_area_num").attr("data-areacode",area_code);
		$("#staff_area_num").val("已选"+li.length+"个");
		$("#area_num").attr("data-areacode",area_code);
		$("#area_num").val("已选"+li.length+"个");
		$("#screen_staff .screen_content_l ul").empty();
		$("#screen_staff .screen_content_l").unbind("scroll");
		$("#screen_area").hide();
		$("#screen_staff").show();
		getstafflist(staff_num);
	}
	if($("#send_mode").val()=="指定区域"){
		$("#sendee_r").attr("data-code",area_code);
		$("#sendee_r").attr("data-name",name);
		$("#screen_area").hide();
		$("#sendee_r").val("已选"+li.length+"个");
		$("#sendee_r").attr("data-type","area");
		whir.loading.remove();//移除遮罩层
	}
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
	if($("#send_mode").val()=="指定员工"){
		isscroll=false;
		staff_num=1;
		$("#staff_shop_num").attr("data-storecode",store_code);
		$("#staff_shop_num").val("已选"+li.length+"个");
		$("#screen_staff .screen_content_l ul").empty();
		$("#screen_staff .screen_content_l").unbind("scroll");
		$("#screen_shop").hide();
		$("#screen_staff").show();
		getstafflist(staff_num);
	}
	if($("#send_mode").val()=="指定店铺"){
		$("#sendee_r").attr("data-code",store_code);
		$("#sendee_r").attr("data-name",name);
		$("#screen_shop").hide();
		$("#sendee_r").val("已选"+li.length+"个");
		$("#sendee_r").attr("data-type","store");
		whir.loading.remove();//移除遮罩层
	}
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
//点击品牌的确定
$("#screen_que_brand").click(function(){
	var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
	var brand_code="";
	for(var i=0;i<li.length;i++){
		var r=$(li[i]).attr("id");
		if(i<li.length-1){
            brand_code+=r+",";
        }else{
            brand_code+=r;
        }
	}
	if($("#send_mode").val()=="指定员工"){
		isscroll=false;
		staff_num=1;
		$("#staff_brand_num").attr("data-brandcode",brand_code);
		$("#staff_brand_num").val("已选"+li.length+"个");
		$("#brand_num").attr("data-brandcode",brand_code);
		$("#brand_num").val("已选"+li.length+"个");
		$("#screen_staff .screen_content_l ul").empty();
		$("#screen_staff .screen_content_l").unbind("scroll");
		$("#screen_brand").hide();
		$("#screen_staff").show();
		getstafflist(staff_num);
	}
	if($("#send_mode").val()=="指定店铺"){
		isscroll=false;
		shop_num=1;
		$("#brand_num").attr("data-brandcode",brand_code);
		$("#brand_num").val("已选"+li.length+"个");
		$("#screen_shop .screen_content_l ul").empty();
		$("#screen_shop .screen_content_l").unbind("scroll");
		$("#screen_brand").hide();
		$("#screen_shop").show();
		getstorelist(shop_num);
	}
})
//店铺里面的区域点击
$("#shop_area").click(function(){
	isscroll=false;
	area_num=1;
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+63;
	$("#screen_area .screen_content_l").unbind("scroll");
	$("#screen_area .screen_content_l ul").empty();
	// $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
	$("#screen_area").show();
	$("#screen_shop").hide();
	getarealist(area_num);
})
//店铺里面的品牌点击
$("#shop_brand").click(function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+63;
	$("#screen_brand .screen_content_l ul").empty();
	// $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
	$("#screen_brand").show();
	$("#screen_shop").hide();
	getbrandlist();
})
//员工里面的区域点击
$("#staff_area").click(function(){
	console.log(123);
	isscroll=false;
	area_num=1;
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+63;
	$("#screen_area .screen_content_l").unbind("scroll");
	$("#screen_area .screen_content_l ul").empty();
	// $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
	$("#screen_area").show();
	$("#screen_staff").hide();
	getarealist(area_num);
})
//员工里面的店铺点击
$("#staff_shop").click(function(){
	isscroll=false;
	shop_num=1;
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+63;
	$("#screen_shop .screen_content_l").unbind("scroll");
	$("#screen_shop .screen_content_l ul").empty();
	// $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
	$("#screen_shop").show();
	$("#screen_staff").hide();
	getstorelist(shop_num);
})
//员工里面的品牌点击
$("#staff_brand").click(function(){
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+63;
	$("#screen_brand .screen_content_l ul").empty();
	// $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
	$("#screen_brand").show();
	$("#screen_staff").hide();
	getbrandlist();
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
	$("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
    $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
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
	param["receiver_type"]=send_mode;
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
		param["user_id"]="";
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
		param["user_id"]="";
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
		param["user_id"]="";
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

