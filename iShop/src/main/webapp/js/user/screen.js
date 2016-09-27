var oc = new ObjectControl();
var area_num=1;
//点击新增的时候弹出不同的框
$("#screen_add").click(function(){
	var r_code=$("#OWN_RIGHT").attr("data-myjcode");//角色编号
	var left=($(window).width()-$("#screen_shop").width())/2;//弹框定位的left值
    var tp=100;//弹框定位的top值
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
		getarealist(area_num);
		bianse();
	}
})
function bianse(){
    $(".screen_content_l li:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:even").css("backgroundColor","#ededed");
    $(".screen_content_r li:odd").css("backgroundColor","#fff");
    $(".screen_content_r li:even").css("backgroundColor","#ededed");
}
//拉取区域的
function getarealist(a){
	var corp_code = $('#OWN_CORP').val();
	var area_command = "/area/selAreaByCorpCode";
	var searchValue=$("#area_search").val().trim();
	var pageSize=20;
	var pageNumber=a;
	var _param = {};
	var checknow_data=[];
    var checknow_namedata=[];
	_param["corp_code"] = corp_code;
	_param["searchValue"]=searchValue;
	_param["pageSize"]=pageSize;
	_param["pageNumber"]=pageNumber;
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
				    area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxOneInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
				}
				$("#screen_area .screen_content_l ul").append(area_html);
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

