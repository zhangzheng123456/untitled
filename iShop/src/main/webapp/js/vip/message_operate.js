var oc = new ObjectControl();
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var isscroll=false;
var  message={
	cache:{//缓存变量
    	"vip_id":"",
    	"area_codes":"",
    	"area_names":"",
    	"brand_codes":"",
    	"brand_names":"",
    	"store_codes":"",
    	"store_names":"",
    	"user_codes":"",
    	"user_names":"",
    	"type":"",
    	"count":""
    },
	init:function(){
		this.getCorplist();
	},
	getCorplist:function(){//获取所属企业列表
	    var corp_command="/user/getCorpByUser";
		oc.postRequire("post", corp_command,"", "", function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_html='';
				for(var i=0;i<msg.corps.length;i++){
					corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
				}
				$("#OWN_CORP").append(corp_html);
				$('.corp_select select').searchableSelect();
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
					
					}
				})
				$('.searchable-select-item').click(function(){
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
};
$(function(){
	message.init();
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
//员工放大镜搜索
$("#staff_search_f").click(function(){
	staff_num=1;
	isscroll=false;
	$("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
	getstafflist(staff_num);
})
//品牌放大镜收索
$("#brand_search_f").click(function(){
	$("#screen_brand .screen_content_l ul").empty();
	getbrandlist();
})
//区域关闭
$("#screen_close_area").click(function(){
	$("#screen_area").hide();
	$("#p").hide();
	$("#screen_wrapper").show();
})
//员工关闭
$("#screen_close_staff").click(function(){
	$("#screen_staff").hide();
	$("#p").hide();
	$("#screen_wrapper").show();
})
//店铺关闭
$("#screen_close_shop").click(function(){
	$("#screen_shop").hide();
	$("#screen_wrapper").show();
	$("#p").hide();
	$("#screen_shop .screen_content_l").unbind("scroll");
})
//品牌关闭
$("#screen_close_brand").click(function(){
	$("#screen_brand").hide();
	$("#screen_wrapper").show();
	$("#p").hide();
})
//弹框关闭
$("#screen_wrapper_close").click(function(){
    $("#screen_wrapper").hide();
    $("#p").hide();
})
//点击弹框的筛选按钮弹出区域框
$("#screen_areal").click(function(){
	var area_num=1;
	isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_area").show();
    $("#screen_wrapper").hide();
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
//点击弹框的筛选按钮弹出品牌框
$("#screen_brandl").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_brand").show();
    $("#screen_wrapper").hide();
    getbrandlist();
});
//点击弹框的筛选按钮弹出区域框
$("#screen_shopl").click(function(){
	var shop_num=1;
	isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_shop").show();
    $("#screen_wrapper").hide();
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//点击弹框的导购按钮弹出导购框
$("#screen_staffl").click(function(){
	var staff_num=1;
	isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_staff").show();
    $("#screen_wrapper").hide();
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
//点击区域确定按钮
$("#screen_que_area").click(function(){
	var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes="";
    var area_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            area_codes+=r+",";
            area_names+=p+",";
        }else{
            area_codes+=r;
            area_names+=p;
        }
    };
    message.cache.area_codes=area_codes;
    message.cache.area_names=area_names;
    $("#screen_area").hide();
    $("#screen_wrapper").show();
    $("#screen_area_num").val("已选"+li.length+"个");
    $(".area_num").val("已选"+li.length+"个");
});
//点击品牌确定按钮
$("#screen_que_brand").click(function(){
	var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
	var brand_codes="";
	var brand_names="";
	for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            brand_codes+=r+",";
            brand_names+=p+",";
        }else{
            brand_codes+=r;
            brand_names+=p;
        }
    };
    message.cache.brand_codes=brand_codes;
    message.cache.brand_names=brand_names;
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
    $("#screen_brand_num").val("已选"+li.length+"个");
    $(".brand_num").val("已选"+li.length+"个");
    console.log(message.cache.brand_codes);
    console.log(message.cache.brand_names);
});
//点击店铺确定按钮
$("#screen_que_shop").click(function(){
	var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
	var store_codes="";
	var store_names="";
	for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            store_codes+=r+",";
            store_names+=p+",";
        }else{
            store_codes+=r;
            store_names+=p;
        }
    };
    message.cache.store_codes=store_codes;
    message.cache.store_names=store_names;
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
    $("#screen_shop_num").val("已选"+li.length+"个");
    $("#staff_shop_num").val("已选"+li.length+"个");
});
//点击员工确定按钮
$("#screen_que_staff").click(function(){
	var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
	var user_codes="";
	var user_names="";
	for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            user_codes+=r+",";
            user_names+=p+",";
        }else{
            user_codes+=r;
            user_names+=p;
        }
    };
    message.cache.user_codes=user_codes;
    message.cache.user_names=user_names;
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
    $("#screen_stff_num").val("已选"+li.length+"个");
});
/*************获取vip的接口***************/
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
$("#add_sendee").bind("click",function(){
	$("#page-wrapper").hide();
	$(".content").show();
	GET(inx,pageSize);
})
$("#turnoff").bind("click",function(){
	$("#page-wrapper").show();
	$(".content").hide();
})
//模仿select
$(function(){
    $("#page_row").click(function(){
        if("block" == $("#liebiao").css("display")){
            hideLi();
        }else{
            showLi();
        }
    });
    $("#liebiao li").each(function(i,v){
        $(this).click(function(){
	        pageSize=$(this).attr('id');
	        if(value==""&&filtrate==""){
	            inx=1;
	            GET(inx,pageSize);
	        }else if(value!==""){
	            inx=1;
	            param["pageSize"]=pageSize;
	            param["pageNumber"]=inx;
	            POST(inx,pageSize);
	        }else if(filtrate!==""){
	            inx=1;
	            _param["pageNumber"]=inx;
	            _param["pageSize"]=pageSize;
	            filtrates(inx,pageSize);
	        }
	        $("#page_row").val($(this).html());
	            hideLi();
	        });
    	});
	    $("#page_row").blur(function(){
	        setTimeout(hideLi,200);
	    });
	}
);
function showLi(){
    $("#liebiao").show();
}
function hideLi(){
    $("#liebiao").hide();
}
function setPage(container, count, pageindex,pageSize){
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
    var a = [];
    //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }
    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
        } else {
            a[a.length] = "<li><span>" + i + "</span></li>";
        }
    }
    //总页数小于10
    if (count <= 10) {
        for (var i = 1; i <= count; i++) {
            setPageList();
        }
    }
    //总页数大于10页
    else {
        if (pageindex <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        }
        else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    }else{
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(a,b){//点击分页的时候调什么接口
    if (value==""&&filtrate=="") {
        GET(a,b);
    }else if (value!==""){
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
    }
}
function superaddition(data,num){//页面加载循环
    $(".table p").remove();
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        //判断是否有会员头像
        if(data[i].vip_avatar==''){
            data[i].vip_avatar='../img/head.png';
        }
        //性别
        if(data[i].sex=="F"){
            data[i].sex="女"
        }else if(data[i].sex=="M"){
            data[i].sex="男"
        }
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        $(".table tbody").append("<tr data-storeId='"+data[i].store_id+"' id='"+data[i].vip_id+"'><td style='text-align:left;padding-left:22px;'>"
        + a
        + "</td><td>"
        + data[i].vip_name
        + "</td><td>"
        + data[i].sex
        +"</td><td>"
        + data[i].vip_phone
        +"</td><td>"
        + data[i].cardno
        +"</td><td>"
        + data[i].vip_card_type
        +"</td><td>"
        + data[i].user_name
        +"</td><td><span>"
        + data[i].store_name
        +"</span></td>"
        +"<td width='50px;' style='text-align: left;'><div class='checkbox1'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
        + i
        + 1
        + "'/><label for='checkboxTwoInput"
        + i
        + 1
        + "'></label></div>"
        + "</td></tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
};
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    var param={};
    var corp_code=$("#OWN_CORP").val();
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]=corp_code;
    oc.postRequire("post","/vipAnalysis/allVip","",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            cout=message.pages;
            var pageNum = message.pageNum;
            superaddition(list,pageNum);
            jumpBianse();
            filtrate="";
            setPage($("#foot-num")[0],cout,pageNum,b);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
};
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        value=this.value.trim();
        if(value!==""){
            inx=1;
            param["searchValue"]=value;
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            POST(inx,pageSize);
        }else if(value==""){
            GET(inx,pageSize);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    if(value!==""){
        inx=1;
        param["searchValue"]=value;
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        POST(inx,pageSize);
    }else{
        GET(inx,pageSize);
    }
});
//搜索的请求函数
function POST(a,b){
	var corp_code=$("#OWN_CORP").val();
    param["corp_code"]=corp_code;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipSearch","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            cout=message.pages;
            var pageNum = message.pageNum;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            filtrate="";
            setPage($("#foot-num")[0],cout,pageNum,b);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
//全选
function checkAll(name){
    var el=$("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = true;
        }
    }
};
//取消全选
function clearAll(name){
    var el=$("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = false;
        }
    }
};
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    });
}
//点击筛选按钮弹出会员筛选框
$("#filtrate").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").show();
})
//点击保存
$("#save").click(function(){
	var vip_id="";
	var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
	for(var i=tr.length-1,ID="";i>=0;i--){
        var r=$(tr[i]).attr("id");
        if(i>0){
            vip_id+=r+",";
        }else{
            vip_id+=r;
        }
    };
    message.cache.vip_id=vip_id;
    message.cache.type="2";
    $("#sendee_r").val("已选"+tr.length+"个");
    $("#page-wrapper").show();
	$(".content").hide();
});
//点击保存全部
$("#save_all").click(function(){
	message.cache.type="1";
	if(message.cache.count>500){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "选择的会员不能大于500个"
		});
		return;
	};
	$("#sendee_r").val("已选"+message.cache.count+"个");
	$("#page-wrapper").show();
	$(".content").hide();
})
//点击发送按钮
$("#send").click(function(){
	var param={};
	var sms_vips={};
	sms_vips["type"]=message.cache.type;
	if(message.cache.type=="1"){
		sms_vips["area_code"]=message.cache.area_codes;
		sms_vips["brand_code"]=message.cache.brand_codes;
		sms_vips["store_code"]=message.cache.store_codes;
		sms_vips["user_code"]=message.cache.user_codes;
		if(message.cache.count==""){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "接收会员不能为空"
			});
			return;
		};
	};
	if(message.cache.type=="2"){
		sms_vips["vips"]=message.cache.vip_id;
		if(message.cache.vip_id==""){
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: "接收会员不能为空"
			});
			return;
		};
	};
	var corp_code=$("#OWN_CORP").val();
	var content=$("#message_content").val();
	param["corp_code"]=corp_code;
	param["content"]=content;
	param["sms_vips"]=sms_vips;
	if(content==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "群发内容不能为空"
		});
		return;
	};
	whir.loading.add("",0.5);//加载等待框
	oc.postRequire("post","/vipFsend/add","",param,function(data){
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
	});
});
//点击会员确定
$("#screen_vip_que").click(function(){
    inx=1;
    _param["corp_code"]=$("#OWN_CORP").val();
    _param["brand_code"]=message.cache.brand_codes;
    _param["store_code"]=message.cache.store_codes;
    _param["area_code"]=message.cache.area_codes;
    _param["user_code"]=message.cache.user_codes;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    if(message.cache.area_codes==""&&message.cache.brand_codes==""&&message.cache.store_codes==""&&message.cache.user_codes==""){
        GET(inx,pageSize);
        $("#save_all").hide();
    }
    if(message.cache.area_codes!==""||message.cache.brand_codes!==""||message.cache.store_codes!==""||message.cache.user_codes!==""){
        filtrate="sucess";
        filtrates(inx,pageSize);
    }
    $("#search").val("");
    $("#screen_wrapper").hide();
    $("#p").hide();
})
//筛选调接口
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipScreen","", _param, function(data) {
        if(data.code=="0"){
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            cout=messages.pages;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
            	message.cache.count=messages.count;
                $(".table p").remove();
                superaddition(list,a);
                jumpBianse();
                $("#save_all").show();
                console.log(message.cache.count);
            }
            setPage($("#foot-num")[0],cout,a,b);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
//关闭按钮回到列表页
$("#send_close").click(function(){
	 $(window.parent.document).find('#iframepage').attr("src", "/vip/message.html");
})