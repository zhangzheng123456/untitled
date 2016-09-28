var oc = new ObjectControl();
var area_num=1;
var area_next=false;
//提示弹框
function frame(){
    var left=($(window).width()-$(".frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".frame").height())/2;//弹框定位的top值
    $('.frame').remove();
    $('body').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
    },2000);
} 
//点击新增的时候弹出不同的框
$("#screen_add").click(function(){
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	var arr=whir.loading.getPageSize();
	var left=(arr[0]-$("#screen_shop").width())/2;
	var tp=(arr[1]-$("#screen_shop").height())/2+80;
	if(r_code==undefined||r_code==""){
		frame();
		$(".frame").html("请先选择所属群组");
		return;
	}
	if(r_code=="R2000"||r_code=="R3000"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_shop").show();
		$("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_area").hide();
		bianse();
	}
	if(r_code=="R4000"){
		whir.loading.add("",0.5);
		$("#loading").remove();
		$("#screen_area").show();
		$("#screen_area").css({"left":+left+"px","top":+tp+"px"});
		$("#screen_shop").hide();
		area_num=1;
		$("#screen_area .screen_content_l ul").empty();
		$("#screen_area .screen_content_r ul").empty();
		area_next=false;
		getarealist(area_num);
		bianse();
	}
})
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
})
//
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
		for(var i=li.length-1;i>=0;i--){
			var html=$(li[i]).html();
			var id=$(li[i]).find("input[type='checkbox']").val();
			var input=$(b).parents(".screen_content").find(".screen_content_r li");
			for(var j=0;j<input.length;j++){
				if($(input[j]).attr("id")==id){
					$(input[j]).remove();
				}
			}
			$(b).parents(".screen_content").find(".screen_content_r ul li").before("<li id='"+id+"'>"+html+"</li>")
		}
	}
	bianse();
}
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
		}
	}
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
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
    	$("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//区域放大镜收索
$("#area_search_f").click(function(){
	area_num=1;
	$("#screen_area .screen_content_l ul").empty();
	getarealist(area_num);
})
//区域关闭
$("#screen_close_area").click(function(){
	$("#screen_area").hide();
	whir.loading.remove();//移除遮罩层
})
function bianse(){
    $(".screen_content_l li:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:even").css("backgroundColor","#ededed");
    $(".screen_content_r li:odd").css("backgroundColor","#fff");
    $(".screen_content_r li:even").css("backgroundColor","#ededed");
}
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
			var area_html_left = '';
			var area_html_right='';
			if (list.length == 0) {
				area_next=true;
			} else {
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
				for(var i=0;i<6;i++){
					area_html_right+="<li></li>"
				}
				$("#screen_area .screen_content_l ul").append(area_html_left);
				$("#screen_area .screen_content_r ul").append(area_html_right);
				bianse();
			}
			area_num++;
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
//区域滚动事件
$("#screen_area .screen_content_l").scroll(function () {
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

