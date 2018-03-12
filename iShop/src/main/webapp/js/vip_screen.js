//日期调用插件
var simple_birth_start = {
    elem: '#simple_birth_start',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        var date=datas.split("-");
        date=date[1]+"-"+date[2];
        $(this.elem).val(date);
        simple_birth_end.min = datas; //开始日选好后，重置结束日的最小日期
        simple_birth_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var simple_birth_end = {
    elem: '#simple_birth_end',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        var date=datas.split("-");
        date=date[1]+"-"+date[2];
        $(this.elem).val(date);
        simple_birth_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var start = {
    elem: '#birth_start',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        var date=datas.split("-");
        date=date[1]+"-"+date[2];
        $(this.elem).val(date);
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas; //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#birth_end',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        var date=datas.split("-");
        date=date[1]+"-"+date[2];
        $(this.elem).val(date);
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var activity_start = {
    elem: '#activate_card_start',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: true,
    fixed: false,
    choose: function (datas) {
        activity_end.min = datas; //开始日选好后，重置结束日的最小日期
        activity_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var activity_end = {
    elem: '#activate_card_end',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: true,
    fixed: false,
    choose: function (datas) {
        activity_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(simple_birth_start);
laydate(simple_birth_end);
laydate(start);
laydate(end);
laydate(activity_start);
laydate(activity_end);
//点击筛选
$("#filtrate").click(function () {
    //$(document.body).css("overflow","hidden");
    expend_data().then(function () {
        fuzhi();
    });
    var arr = whir.loading.getPageSize();
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").show();
});
//高级筛选弹窗
$("#more_filter").click(function () {
    $("#expend_attribute").getNiceScroll().show();
    $("#vip_attribute").getNiceScroll().show();
    $("#expend_attribute").getNiceScroll().resize();
    $("#vip_attribute").getNiceScroll().show();
    $("#simple_filter").hide();
    $("#senior_filter").show();
});
$("#back_filter").click(function () {
    $("#expend_attribute").getNiceScroll().hide();
    $("#vip_attribute").getNiceScroll().hide();
    $("#simple_filter").show();
    $("#senior_filter").hide();
});
//筛选的下拉模拟事件
$("#simple_filter_condition ul li").click(function () {
    var index = $(this).index();
    $(this).children("a").addClass("condition_active");
    $(this).siblings("li").children("a").removeClass("condition_active");
    $("#simple_contion").children("div").eq(index).css("display", "block");
    $("#simple_contion").children("div").eq(index).siblings("div").css("display", "none");
});
$("#filter_condition ul li").click(function () {
    var index = $(this).index();
    $(this).children("a").addClass("condition_active");
    $(this).siblings("li").children("a").removeClass("condition_active");
    $("#contion").children("div").eq(index).css("display", "block");
    $("#contion").children("div").eq(index).siblings("div").css("display", "none");
    $("#contions").children("div").eq(index).css("display", "block");
    $("#contions").children("div").eq(index).siblings("div").css("display", "none");
});
$("#vip_card_type").click(function () {
    vipCardtype();
    $("#card_type_select").toggle();
});
$("#card_type_select").on("click","li",function () {
    $("#vip_card_type").val($(this).html());
    $("#vip_card_type").attr("data-code",$(this).attr("data-code"));
});
$("#sex").click(function () {
    $("#sex_select").toggle();
});
$("#sex_select li").click(function () {
    $("#sex").val($(this).html());
    $("#sex_select").hide();
});
$("#consume_date").click(function () {
    $("#consume_select").toggle();
});
$("#consume_date_basic_3").click(function () {
    $("#consume_select_basic_3").toggle();
});
$("#consume_date_basic_4").click(function () {
    $("#consume_select_basic_4").toggle();
});
$("#consume_select li").click(function () {
    $("#consume_date").val($(this).html());
    $("#consume_date").attr("data-date", $(this).attr("data-date"));
    $("#consume_select").hide();
});
$("#consume_select_basic_3 li").click(function () {
    $("#consume_date_basic_3").val($(this).html());
    $("#consume_date_basic_3").attr("data-date", $(this).attr("data-date"));
    $("#consume_select_basic_3").hide();
});
$("#consume_select_basic_4 li").click(function () {
    $("#consume_date_basic_4").val($(this).html());
    $("#consume_date_basic_4").attr("data-date", $(this).attr("data-date"));
    $("#consume_select_basic_4").hide();
});
$("#state").click(function () {
    $("#state_select").toggle();
});
$("#state_select li").click(function () {
    $("#state").val($(this).html());
    $("#state_select").hide();
});
$("#simple_state").click(function () {
    $("#simple_state_select").toggle();
});
$("#simple_state_select li").click(function () {
    $("#simple_state").val($(this).html());
    $("#simple_state_select").hide();
});
$("#contion").on("click",".participate",function(){
    $(this).next().toggle();
});
$("#contion").on("click",".participate_select li",function(){
    $(this).parent().prev().val($(this).html());
    $(this).parent().hide();
});
$("#govWx").click(function () {
    $("#govWx_select").toggle();
});
$("#govWx_select li").click(function () {
    $("#govWx").val($(this).html());
    $("#govWx_select").hide();
});
$("#expend_attribute").on("click",".select",function (event) {
    event.stopPropagation();
    $('input[data-type="check"]').next().hide();
    $(this).next(".sex_select").toggle();
});
$("#expend_attribute").on("click",".sex_select li:not(.type_check)",function () {
    var _this=$(this).parent(".sex_select").prev(".select");
    $(_this).val($(this).html());
    $(".sex_select").hide();
});
$("#expend_attribute").on("click",".sex_select li.type_check,.sex_select li.type_check span",function (event) {
    event.stopPropagation();
    return
});
$("#expend_attribute").on("click",".sex_select li.type_check input",function (event) {
    event.stopPropagation();
    var input=$(this).parents("ul.sex_select").find("li.type_check input[type='checkbox']:checked");
    var text="";
    for(var i=input.length-1;i>=0;i--){
        if(i>0){
            text+=$(input[i]).next().html()+","
        }else{
            text+=$(input[i]).next().html();
        }
    }
    var textArray=text.split(",").reverse();
    text=textArray.join(",");
    $(this).parents("ul").prev().val(text);
    $(this).parents("ul").prev().attr("title",text);
});
$("#expend_attribute").on("blur",".select",function () {
    if($(this).attr("data-type")=="check") return;
   var ul = $(this).next(".sex_select");
    setTimeout(function(){
        ul.hide();
    },200);
});
//筛选确定
$("#screen_vip_que").click(function () {
    //$(document.body).css("overflow","auto");
    inx = 1;
    var corp_code = $("#OWN_CORP").val();
    if (corp_code !== "" && corp_code !== undefined) {
        _param["corp_code"] = corp_code;
    } else {
        _param["corp_code"] = "C10000";
    }
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    var screen = [];
    if ($("#simple_filter").css("display") == "block") {
        _param['screen_type']="easy";
        $("#simple_contion .contion_input").each(function () {
            var input = $(this).find("input");
            var key = $(input[0]).attr("data-kye");
            var classname = $(input[0]).attr("class");
            var expend_key = $(input[0]).attr("data-expend");
            if(key == "17"){
                 return ;
            }else if (key == "4") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    var date = $("#consume_date_basic_4").attr("data-date");
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    param['date'] = date;
                    screen.push(param);
                }
            }else if (key == "3") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    var date = $("#consume_date_basic_3").attr("data-date");
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    param['date'] = date;
                    screen.push(param);
                }
            }else if(key == "15"){
                if ($(input[0]).attr("data-code") !== "") {
                    var param = {};
                    var val = $(input[0]).attr("data-code");
                    var name = $(input[0]).attr("data-name");
                    param['key'] = key;
                    param['value'] = val;
                    param['name'] = name;
                    param['type'] = "text";
                    screen.push(param);
                }
            }else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    screen.push(param);
                }
            }else if((key == "6"||key == "18") && $(input[0]).val() !== "全部"){
                var param = {};
                var val = $(input[0]).val();
                val = (val == "已冻结"||val == "未关注") ? "N": "Y";
                param['key'] = key;
                param['value'] = val;
                param['type'] = "text";
                screen.push(param);
            }else {
                if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                    var param = {};
                    var val = $(input[0]).val();
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }
            }
        });
    } else {
        _param['screen_type']="difficult";
        $("#contion>div").each(function () {
            $(this).find(".contion_input").each(function (i, e) {
                var input = $(e).find("input");
                var key = $(input[0]).attr("data-kye");
                var expend_key = $(input[0]).attr("data-expend");
                var classname = $(input[0]).attr("class");
                if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        screen.push(param);
                    }
                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15"|| key == "16") {
                    if ($(input[0]).attr("data-code") !== "") {
                        var param = {};
                        var val = $(input[0]).attr("data-code");
                        var name = $(input[0]).attr("data-name");
                        param['key'] = key;
                        param['value'] = val;
                        param['name'] = name;
                        param['type'] = "text";
                        screen.push(param);
                    }
                }else if (key == "17") {
                    return;
                } else if (key == "3" || key == "4") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        var date = $("#consume_date").attr("data-date");
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        param['date'] = date;
                        screen.push(param);
                    }
                }else if((key == "6"|| key == "18") && $(input[0]).val() !== "全部" ){
                        var param = {};
                        var val = $(input[0]).val();
                        val = (val == "已冻结"||val == "未关注") ? "N": "Y";
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                }else {
                    if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                        var param = {};
                        var val = $(input[0]).val();
                        val = (val == "空"&&$(input[0]).hasClass("select")) ? null : val;
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                }
            });
            $(this).find("textarea").each(function () {
                var key = $(this).attr("data-kye");
                var param = {};
                var val = $(this).val();
                if(val !== ""){
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }
            });
        });
        var param_coupon={};
        var param_task={};
        var param_activity={};
        param_coupon['value']=[];
        param_task['value']=[];
        param_activity['value']=[];
        $("#coupon_list .coupon_line").each(function(i,e){
            var val=$(e).find(".filter_coupon").val();
            var text="";
            var input=$(e).find("input.short_input_date").map(function(){
                text+=$(this).val().trim()
            });
            if(text!=""){
                var param_val={
                    coupon_code:val=="全部"?"ALL":$(e).find(".filter_coupon").attr("data-value"),
                    num:{
                        start:$(e).find(".coupon_all").children("input").eq(0).val(),
                        end:$(e).find(".coupon_all").children("input").eq(1).val()
                    },
                    use:{
                        start:$(e).find(".coupon_used").children("input").eq(0).val(),
                        end:$(e).find(".coupon_used").children("input").eq(1).val()
                    },
                    expired_num:{
                        start:$(e).find(".coupon_overdue").children("input").eq(0).val(),
                        end:$(e).find(".coupon_overdue").children("input").eq(1).val()
                    },
                    can_use:{
                        start:$(e).find(".coupon_Usable").children("input").eq(0).val(),
                        end:$(e).find(".coupon_Usable").children("input").eq(1).val()
                    }
                };
                param_coupon['value'].push(param_val);
            }
        });
        $("#task_list .task_line").each(function(i,e){
            var val=$(e).find(".filter_task").val();
            var participate=$(e).find(".participate").val();
            if(val=="全部")return;
            var param_val={
                task_code:$(e).find(".filter_task").attr("data-value"),
                is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
            };
            param_task['value'].push(param_val);
        });
        $("#activity_list .activity_line").each(function(i,e){
            var val=$(e).find(".filter_activity").val();
            var participate=$(e).find(".participate").val();
            if(val=="全部")return;
            var param_val={
                activity_code:$(e).find(".filter_activity").attr("data-value"),
                is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
            };
            param_activity['value'].push(param_val);
        });
        if(param_task['value'].length>0){
            param_task['key'] = 22;
            param_task['type'] = "array";
            param_task["name"]="会员任务";
            screen.push(param_task);
        }
        if(param_activity['value'].length>0){
            param_activity['key'] = 23;
            param_activity['type'] = "array";
            param_activity["name"]="活动";
            screen.push(param_activity);
        }
        if(param_coupon['value'].length>0){
            param_coupon['key'] = 21;
            param_coupon['type'] = "array";
            param_coupon["name"]="券";
            screen.push(param_coupon);
        }
    }
    _param['screen'] = screen;
    if (screen.length == 0) {
        GET(inx, pageSize);
    }else {
        filtrate = "sucess";
        filtrates(inx, pageSize);
    }
    $("#search").val("");
    value="";
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//清空筛选
$("#empty_filter").click(function () {
    message.cache.area_codes="";
    message.cache.area_names="";
    message.cache.brand_codes="";
    message.cache.brand_names="";
    message.cache.store_codes="";
    message.cache.store_names="";
    message.cache.user_codes="";
    message.cache.user_names="";
    message.cache.group_codes="";
    message.cache.group_name="";
    $("#screen_wrapper .contion_input input").each(function () {
        var key = $(this).attr("data-kye");
        if (key == "6" || key == "8" || key == "12" || key == "18") {
            $(this).val("全部");
        } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15") {
            $(this).val("");
            $(this).attr("data-code", "");
        } else if (key == "16") {
            $(this).val("");
            $(this).attr("data-code", "");
            $("#filter_group").attr("data-corp", "");
            groupCode = [];
            groupName = [];
        } else if (key == "17") {
            $(this).val("最近3个月");
            $(this).attr("data-date","3");
        } else {
            $(this).val("");
        }
    });
    $("#screen_wrapper textarea").each(function () {
        $(this).val("");
    });
    $("#staff_brand_num").val("全部");
    $("#staff_brand_num").attr("data-brandcode","");
    $("#staff_area_num").val("全部");
    $("#staff_brand_num").attr("data-areacode","");
    $("#staff_shop_num").val("全部");
    $("#staff_shop_num").attr("data-storecode","");
    $("#brand_num").val("全部");
    $("#brand_num").attr("data-brandcode","");
    $("#area_num").val("全部");
    $("#area_num").attr("data-areacode","");
    $(".tab_activity").eq(0).addClass("active").siblings().remove();
    $(".tab_task").eq(0).addClass("active").siblings().remove();
    $(".tab_coupon").eq(0).addClass("active").siblings().remove();

    $(".activity_line").eq(0).siblings().remove();
    $(".activity_line").show();
    $(".activity_line").find(".filter_activity").val("全部").attr("data-value","");
    // $(".activity_line").find(".filter_activity_ul").html("");

    $(".task_line").eq(0).siblings().remove();
    $(".task_line").show();
    $(".task_line").find(".filter_task").val("全部").attr("data-value","");
    // $(".task_line").find(".filter_task_ul").html("");

    $(".coupon_line").eq(0).siblings().remove();
    $(".coupon_line").show();
    $(".coupon_line input").val("");
    $(".coupon_line").find(".filter_coupon").val("全部").attr("data-value","");
    // $(".coupon_line").find(".filter_coupon_ul").html("");

    $(".participate").val("全部");
});
//分组弹窗
$("#screen_group_icon").click(function () {
    if(message.cache.group_codes!==""){
        var group_codes=message.cache.group_codes.split(',');
        var group_names=message.cache.group_names.split(',');
        var group_html_right="";
        for(var h=0;h<group_codes.length;h++){
            group_html_right+="<li id='"+group_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+group_codes[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+group_names[h]+"</span>\
            \</li>"
        }
        $("#screen_group .s_pitch span").eq(0).html(h);
        $("#screen_group .screen_content_r ul").html(group_html_right);
    }else{
        $("#screen_group .s_pitch span").eq(0).html("0");
        $("#screen_group .screen_content_r ul").empty();
    }
    // var arr=whir.loading.getPageSize();
    // var left=(arr[0]-$("#screen_shop").width())/2;
    // var tp=(arr[3]-$("#screen_shop").height())/2+50;
    // $("#screen_group").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_wrapper").hide();
    $("#screen_group").show();
    getGroup();
});
$("#screen_close_group").click(function () {
   $("#screen_wrapper").show();
   $("#screen_group").hide();
});
//分组确定
$("#screen_que_group").click(function () {
    var li=$("#screen_group .screen_content_r input[type='checkbox']").parents("li");
    var group_codes="";
    var group_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            group_codes+=r+",";
            group_names+=p+",";
        }else{
            group_codes+=r;
            group_names+=p;
        }
    }
    $("#screen_group").hide();
    //if(isSelectNew){
    //    select_affiliation.group={
    //        code:group_codes,
    //        name:group_names
    //    };
    //    $(".select_box_wrap").show();
    //    $(".item_list:visible .select_show_group input").attr("data-code",group_codes);
    //    $(".item_list:visible .select_show_group input").attr("data-name",group_names);
    //    $(".item_list:visible .select_show_group input").val("已选"+li.length+"个");
    //    $("#p").hide();
    //    isSelectNew=false;
    //    var type=$(".item_list:visible .select_show_group").parents(".center_item_group").attr("class").replace(/center_item_group select_/,"");
    //    runtimeData(type);
    //}else{
        message.cache.group_codes=group_codes;
        message.cache.group_names=group_names;
        $("#screen_wrapper").show();
        $("#filter_group").val("已选"+li.length+"个");
        $("#filter_group").attr("data-code",group_codes);
        $("#filter_group").attr("data-name",group_names);
    //}
});
//分组搜索
$("#group_search").keydown(function () {
    var event = window.event || arguments[0];
    if (event.keyCode == 13) {
        getGroup();
    }
});
$("#group_search_f").click(function () {
    getGroup();
});
//点击右移
$(".shift_right").click(function () {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function () {
    var right = "all";
    var div = $(this);
    removeRight(right, div);
});
//点击左移
$(".shift_left").click(function () {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function () {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
});
$(document).click(function (e) {
   $('input[data-type="check"]').next().hide();
    if (!($(e.target).is("#sex_select") || $(e.target).is("#sex"))) {
        $("#sex_select").hide();
    }
    if (!($(e.target).is("#state_select") || $(e.target).is("#state"))) {
        $("#state_select").hide();
    }
    if (!($(e.target).is("#simple_state_select") || $(e.target).is("#simple_state"))) {
        $("#simple_state_select").hide();
    }
    if (!($(e.target).is("#filter_group") || $(e.target).is(".filter_group input"))) {
        $(".filter_group").hide();
    }
    if (!($(e.target).is("#batch_search_label"))) {
        $(".batch_search_label ul").hide();
    }
    if (!($(e.target).is("#vip_card_type"))) {
        $("#card_type_select").hide();
    }
    if (!($(e.target).is("#vip_card_type_add"))) {
        $("#card_type_select_add").hide();
    }
    if (!($(e.target).is("#consume_date"))) {
        $("#consume_select").hide();
    }
    if (!($(e.target).is("#consume_date_basic_3"))) {
        $("#consume_select_basic_3").hide();
    }
    if (!($(e.target).is("#consume_date_basic_4"))) {
        $("#consume_select_basic_4").hide();
    }
    if (!($(e.target).is(".filter_task"))) {
        $(".filter_task_ul").hide();
    }
    if (!($(e.target).is(".filter_coupon"))) {
        $(".filter_coupon_ul").hide();
    }
    if (!($(e.target).is(".filter_activity"))) {
        $(".filter_activity_ul").hide();
    }
    if (!($(e.target).is(".participate"))) {
        $(".participate_select").hide();
    }

});
$(function () {
    //给年龄赋值
    for (var i = 1; i < 101; i++) {
        $(".age_l").append('<li>' + i + '</li>');
        $(".age_r").append('<li>' + i + '</li>')
    }
    //引用滚动样式插件
    $("#expend_attribute,#memorial_day,#vip_attribute").niceScroll({
        cursorborder: "0 none", cursoropacitymin: "0", boxzoom: false,
        cursorcolor: " rgba(0,0,0,0.2)",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 30,
        autohidemode:false,
    });
    $(window).scroll(function () {
        if ($(window).scrollTop() > 100) {
            $("#side_bar .gotop").fadeIn();
        } else {
            $("#side-bar .gotop").hide();
        }
    });
    $("#side_bar .gotop").click(function () {
        $('html,body').animate({
            'scrollTop': 0
        }, 500);
    });
    $("#side_bar .gobottom").click(function () {
        var btm = $('html,body').height();
        $('html,body').animate({
            'scrollTop': btm
        }, 500);
    });
});
//点击列表显示选中状态
$(".screen_content").on("click", "li", function () {
    var input = $(this).find("input")[0];
    // var type=$(this).find("input").eq(0).attr("data-type");
    // if(type=="define"){
    //     art.dialog({
    //         zIndex: 10010,
    //         time: 1,
    //         lock: true,
    //         cancel: false,
    //         content: "暂不支持选择自定义分组"
    //     });
    //     return false;
    // }
    if (input.type == "checkbox" && input.checked == false ) {
        input.checked = true;
    } else if (input.type == "checkbox" && input.checked == true) {
        input.checked = false;
    }
});
//验证input值
$("#screen_wrapper .contion_input,#screen_vipWrapper .contion_input").on("blur", "input", function () {
    var classname = $(this).attr("class");
    var key = $(this).attr("data-kye");
    var val = $(this).val();
    var _this=$(this);
    if (key == "10" && val!=="") {
        testInputNumber(val).then(function () {
            _this.val("");
        });
    }else if (classname == "short_input_date" && (key =="19" || key=="20") && val!=="") {
        var max = $(this).nextAll("input").val();
        if(max!==""){
            max = parseInt(max);
            testInputNumber_zhen(val, "", max).then(function () {
                _this.val("");
            });
        }else {
            testInputNumber_zhen(val).then(function () {
                _this.val("");
            });
        }
    } else if (classname == "short_input_date" && key !== undefined && val!=="") {
        var max = $(this).nextAll("input").val();
        if(max!==""){
            max = parseInt(max);
            testInputNumber(val, "", max).then(function () {
                _this.val("");
            });
        }else {
            testInputNumber(val).then(function () {
                _this.val("");
            });
        }
    } else if (classname == "short_input_date" && key == undefined && val!=="") {
        var min = $(this).prevAll("input").val();
        var type=$(this).prevAll("input").attr("data-kye");
        if(type=="19" || type=="20"){
            if(min!==""){
                min = parseInt(min);
                testInputNumber_zhen(val, min, "").then(function () {
                    _this.val("");
                });
            }else {
                testInputNumber_zhen(val).then(function () {
                    _this.val("");
                });
            }
        }else{
            if(min!==""){
                min = parseInt(min);
                testInputNumber(val, min, "").then(function () {
                    _this.val("");
                });
            }else {
                testInputNumber(val).then(function () {
                    _this.val("");
                });
            }
        }
    }
});
$("#screen_wrapper ,#screen_vipWrapper").on("blur", ".contion_input_coupon input", function () {
    var _this=$(this);
    var val = $(this).val().trim();
    var index = $(this).index();
    if($(this).hasClass("select")) return;
    if(index==1 && val!=""){
        var max = $(this).nextAll("input").val();
        if(max!==""){
            max = parseInt(max);
            testInputNumber_zhen(val, "", max).then(function () {
                _this.val("");
            });
        }else {
            testInputNumber_zhen(val).then(function () {
                _this.val("");
            });
        }
    }else if(index==3 && val!=""){
        var min = $(this).prevAll("input").val();
        if(min!==""){
            min = parseInt(min);
            testInputNumber_zhen(val, min, "").then(function () {
                _this.val("");
            });
        }else {
            testInputNumber_zhen(val).then(function () {
                _this.val("");
            });
        }
    }
});
function testInputNumber(val, min, max) {//验证数字
    // var reg = /^[0-9]*$/;
    var reg = /^\d+(\.\d+)?$/;
    var def=$.Deferred();
    if (!reg.test(val)) {
        art.dialog({
            zIndex: 10010,
            time: 1,
            lock: true,
            cancel: false,
            content: "只允许输入数字"
        });
        def.resolve();
        return def;
    }
    if (val < min && min !== "") {
        art.dialog({
            zIndex: 10010,
            time: 1,
            lock: true,
            cancel: false,
            content: "不能小于前面的值"
        });
        def.resolve();
        return def;
    }
    if (val > max && max !== "") {
        art.dialog({
            zIndex: 10010,
            time: 1,
            lock: true,
            cancel: false,
            content: "不能大于后面的值"
        });
        def.resolve();
        return def;
    }
    def.reject();
    return def;
}
function testInputNumber_zhen(val, min, max) {//验证数字
    // var reg = /^[0-9]*$/;
    var reg = /^(0|[1-9][0-9]*)$/;
    var def=$.Deferred();
    if (!reg.test(val)) {
        art.dialog({
            zIndex: 10010,
            time: 2,
            lock: true,
            cancel: false,
            content: "只允许输入零及大于零的整数"
        });
        def.resolve();
        return def;
    }
    if (val < min && min !== "") {
        art.dialog({
            zIndex: 10010,
            time: 2,
            lock: true,
            cancel: false,
            content: "不能小于前面的值"
        });
        def.resolve();
        return def;
    }
    if (val > max && max !== "") {
        art.dialog({
            zIndex: 10010,
            time: 2,
            lock: true,
            cancel: false,
            content: "不能大于后面的值"
        });
        def.resolve();
        return def;
    }
    def.reject();
    return def;
}
//获取分组
function getGroup() {
    var corp_command = "/vipGroup/getCorpGroups";
    var _param = {};
    _param["corp_code"] = "C10000";
    _param["search_value"] = $("#group_search").val().trim();
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#screen_group .screen_content_l ul").empty();
            $("#screen_group .s_pitch span").eq(1).text(list.total);
            if (list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    html+="<li><div class='checkbox1'><input  data-type='"+list[i].group_type+"' type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + "'/><label for='checkboxOneInput"
                        + i
                        + "'></label></div><span class='p16'>"+list[i].vip_group_name+"</span></li>"
                }
                $("#screen_group .screen_content_l ul").append(html);
            } else if (list.length <= 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        }
    })
}
//获取拓展资料
function expend_data() {
    var def = $.Deferred();
    var param={"corp_code":"C10000"};
    oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
       if(data.code == 0){
           $("#expend_attribute").empty();
           var msg = JSON.parse(data.message);
           var list = JSON.parse(msg.list);
           var html="";
           var simple_html="";
           if(list.length>0){
               for(var i=0;i<list.length;i++){
                   var param_type = list[i].param_type;
                   if(param_type=="date"){
                       simple_html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                           + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                           + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                       html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                           + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                           + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                   }
                   if(param_type=="select"){
                       var param_values = "";
                           param_values = list[i].param_values.split(",");
                       if(param_values.length>0){
                           var li="";
                           for(var j=0;j<param_values.length;j++){
                               li+='<li>'+param_values[j]+'</li>'
                           }
                           li+='<li>空</li>';
                           html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                               + li
                               + '</ul></div>'
                       }else {
                           html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                       }
                   }
                   if(param_type=="text"){
                       html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                           + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                   }
                   if(param_type=="longtext"){
                       html+='<div class="textarea"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                           + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                   }
                   if(param_type=="check"){
                       var param_values = "";
                       param_values = list[i].param_values.split(",");
                       if(param_values.length>0){
                           var li="";
                           for(var j=0;j<param_values.length;j++){
                               li+='<li class="type_check">'
                                   +'<input type="checkbox" style="margin:0;vertical-align:middle;width: 15px;height: 15px;"/>'
                                   +'<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+param_values[j]+'">'+param_values[j]+'</span>'
                                   +'</li>'
                           }
                           html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                               + li
                               + '</ul></div>'
                       }else {
                           html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                       }
                   }
               }
           }
           $("#expend_attribute").html(html);
           $("#memorial_day").html(simple_html);
       }else if(data.code == -1){
           console.log(data.message);
       }
       def.resolve();
    });
    return def;
}
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li:visible");
    }
    // if(li.length>100){
    //     art.dialog({
    //         zIndex: 10003,
    //         time: 1,
    //         lock: true,
    //         cancel: false,
    //         content: "请选择少于100个"
    //     });
    //     return;
    // }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = 0; i < li.length; i++) {
            var html = $(li[i]).html();
            var id = $(li[i]).find("input[type='checkbox']").val();
            $(li[i]).find("input[type='checkbox']")[0].checked = true;
            var input = $(b).parents(".screen_content").find(".screen_content_r li");
            for (var j = 0; j < input.length; j++) {
                if ($(input[j]).attr("id") == id) {
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>");
            $(b).parents(".screen_content").find(".screen_content_r input[value='" + id + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
}
//移到左边
function removeLeft(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
    }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = li.length - 1; i >= 0; i--) {
            $(li[i]).remove();
            $(b).parents(".screen_content").find(".screen_content_l input[value='" + $(li[i]).attr("id") + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
}
//卡类型
function vipCardtype() {
    var corp_code="C10000";
    var param={"corp_code":corp_code};
    oc.postRequire("post","/vipCardType/getVipCardTypes","0",param,function (data) {
        if(data.code==0){
            var li="";
            var message=JSON.parse(data.message);
            var msg=JSON.parse(message.list);
            if(msg.length==0){

            }else if(msg.length>0){
                for(var i=0;i<msg.length;i++){
                    li+="<li data-code='"+msg[i].vip_card_type_code+"'>"+msg[i].vip_card_type_name+"</li>"
                }
            }
            $("#card_type_select").empty();
            $("#card_type_select").append(li);
        }
    });
}
function fuzhi() {
    if(_param.screen==undefined || _param == undefined){
        return ;
    }else {
        if(_param.screen_type=="easy"){
            var screen=_param.screen;
            for(var i=0;i<screen.length;i++){
                switch (screen[i].key){
                    case "1":$("#simple_birth_start").val(screen[i].value.start);
                        $("#simple_birth_end").val(screen[i].value.end);
                        break;
                    case "3":$("#consume_date_basic_3").val("最近"+screen[i].date+"个月");
                             $("#consume_date_basic_3").attr("data-date",screen[i].date);
                             $("#consume_time_start").val(screen[i].value.start);
                             $("#consume_time_end").val(screen[i].value.end);
                        break;
                    case "4":$("#consume_date_basic_4").val("最近"+screen[i].date+"个月");
                             $("#consume_date_basic_4").attr("data-date",screen[i].date);
                             $("#consume_money_start").val(screen[i].value.start);
                             $("#consume_money_end").val(screen[i].value.end);
                        break;
                    case "5":$("#point_start").val(screen[i].value.start);
                             $("#point_end").val(screen[i].value.end);
                        break;
                    case "18":if(screen[i].value=="Y"){$("#simple_state").val("已关注")}else if(screen[i].value=="N"){$("#simple_state").val("未关注")}
                        break;
                    case "15":$(".screen_staff_num").attr("data-code",screen[i].value);$(".screen_staff_num").val("已选"+screen[i].value.split(",").length+"个");
                        $(".screen_staff_num").attr("data-name",screen[i].name);
                        message.cache.user_codes=screen[i].value;
                        message.cache.user_names=screen[i].name;
                        break;
                    default:$("#memorial_day input").each(function () {
                        var key=$(this).attr("data-kye");
                        if(key==screen[i].key){
                            $(this).val(screen[i].value.start);
                            $(this).nextAll("input").val(screen[i].value.end);
                        }
                    });
                }
            }
        }else if(_param.screen_type=="difficult"){
            var screen=_param.screen;
            for(var i=0;i<screen.length;i++){
                switch (screen[i].key){
                    case "7":$("#basic_message input[data-kye='7']").val(screen[i].value);
                        break;
                    case "8":$("#sex").val(screen[i].value==null?"空":screen[i].value);
                        break;
                    case "9":$("#age_l").val(screen[i].value.start);$("#age_r").val(screen[i].value.end);
                        break;
                    case "1":$("#birth_start").val(screen[i].value.start);$("#birth_end").val(screen[i].value.end);
                        break;
                    case "10":$("#basic_message input[data-kye='10']").val(screen[i].value);
                        break;
                    case "11":$("#basic_message input[data-kye='11']").val(screen[i].value);
                        break;
                    case "12":$("#basic_message input[data-kye='12']").val(screen[i].value);
                        break;
                    case "13":$("#activate_card_start").val(screen[i].value.start);$("#activate_card_end").val(screen[i].value.end);
                        break;
                    case "5":$("#basic_message input[data-kye='5']").val(screen[i].value.start);$("#basic_message input[data-kye='5']").nextAll("input").val(screen[i].value.end);
                        break;
                    case "6":if(screen[i].value=="Y"){$("#state").val("未冻结")}else if(screen[i].value=="N"){$("#state").val("已冻结")}
                        break;
                    case "18":if(screen[i].value=="Y"){$("#govWx").val("已关注")}else if(screen[i].value=="N"){$("#govWx").val("未关注")}
                        break;
                    case "19":$(".contion_input input[data-kye='19']").val(screen[i].value.start);$(".contion_input input[data-kye='19']").nextAll("input").val(screen[i].value.end);
                        break;
                    case "20":$(".contion_input input[data-kye='20']").val(screen[i].value.start);$(".contion_input input[data-kye='20']").nextAll("input").val(screen[i].value.end);
                        break;
                    case "3":$("#consume_date").val("最近"+screen[i].date+"个月");
                        $("#consume_date").attr("data-date",screen[i].date);
                        $("#consume_time_before").val(screen[i].value.start);
                        $("#consume_time_after").val(screen[i].value.end);
                        break;
                    case "4":$("#consume_date").val("最近"+screen[i].date+"个月");
                        $("#consume_date").attr("data-date",screen[i].date);
                        $("#consume_money_before").val(screen[i].value.start);
                        $("#consume_money_after").val(screen[i].value.end);
                        break;
                    case "brand_code":$("#screen_brand_num").attr("data-code",screen[i].value);$("#screen_brand_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_brand_num").attr("data-name",screen[i].name);
                        message.cache.brand_codes=screen[i].value;
                        message.cache.brand_names=screen[i].name;
                        break;
                    case "area_code":$("#screen_area_num").attr("data-code",screen[i].value);$("#screen_area_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_area_num").attr("data-name",screen[i].name);
                        message.cache.area_codes=screen[i].value;
                        message.cache.area_names=screen[i].name;
                        break;
                    case "14":$("#screen_shop_num").attr("data-code",screen[i].value);$("#screen_shop_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_shop_num").attr("data-name",screen[i].name);
                        message.cache.store_codes=screen[i].value;
                        message.cache.store_names=screen[i].name;
                        break;
                    case "15":$(".screen_staff_num").attr("data-code",screen[i].value);$(".screen_staff_num").val("已选"+screen[i].value.split(",").length+"个");
                        $(".screen_staff_num").attr("data-name",screen[i].name);
                        message.cache.user_codes=screen[i].value;
                        message.cache.user_names=screen[i].name;
                        break;
                    case "16":$("#filter_group").attr("data-code",screen[i].value);$("#filter_group").val("已选"+screen[i].value.split(",").length+"个");
                        $("#filter_group").attr("data-name",screen[i].name);
                        message.cache.group_codes=screen[i].value;
                        message.cache.group_name=screen[i].name;
                        break;
                    default:$("#expend_attribute .contion_input>input").each(function () {
                        var key=$(this).attr("data-kye");
                        var date=$(this).attr("data-expend");
                        if(key==screen[i].key&&date=="date"){
                            $(this).val(screen[i].value.start);
                            $(this).nextAll("input").val(screen[i].value.end);
                        }else if(key==screen[i].key){
                            $(this).val(screen[i].value==null?"空":screen[i].value);
                        }
                    });
                }
            }
        }
    }
}
function Ownformat(datas) {//纪念日开始日期回掉函数
    var date=datas.split("-");
    date=date[1]+"-"+date[2];
    var id=$(this.elem).nextAll("input").attr("id");
    $(this.elem).val(date);
    $(this.elem).nextAll("input").attr("onclick","laydate({elem:'#"+id+"',min:'"+datas+"', max:\'2099-12-31 23:59:59\' , istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})")
}
function Ownformatend(datas) {//纪念日截止日期回掉函数
    var date=datas.split("-");
    date=date[1]+"-"+date[2];
    var id=$(this.elem).prevAll("input").attr("id");
    $(this.elem).val(date);
    $(this.elem).prevAll("input").attr("onclick","laydate({elem:'#"+id+"',min:\'1900-01-0\', max:'"+datas+"',istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})")
}

$("#add_coupon").click(function(){
    var index=$(this).prev().children().length;
    if(index==5) return;
    $(this).prev().append("<li class='tab_coupon'>券"+(index+1)+"</li>");
    var html="<div class='coupon_line' style='display: none'>"+$("#coupon_list .coupon_line:last-child").html()+"</div>";
    $("#coupon_list").append(html);
    var input=$("#coupon_list .coupon_line:last-child input");
    for(var i=0;i<input.length;i++){
        if($(input[i]).hasClass("select")){
            $(input[i]).val("全部");
        }else{
            $(input[i]).val("");
        }
        if($(input[i]).attr("data-value")!=undefined){
            $(input[i]).attr("data-value","")
        }
    }
});
$("#add_task").click(function(){
    var index=$(this).prev().children().length;
    if(index==5) return;
    $(this).prev().append("<li class='tab_task'>任务"+(index+1)+"</li>");
    var html="<div class='task_line' style='display: none'>"+$("#task_list .task_line:last-child").html()+"</div>";
    $("#task_list").append(html);
    var input=$("#task_list .task_line:last-child input");
    for(var i=0;i<input.length;i++){
        $(input[i]).val("全部");
        if($(input[i]).attr("data-value")!=undefined){
            $(input[i]).attr("data-value","")
        }
    }
});
$("#add_activity").click(function(){
    var index=$(this).prev().children().length;
    if(index==5) return;
    $(this).prev().append("<li class='tab_activity'>活动"+(index+1)+"</li>");
    var html="<div class='activity_line' style='display: none'>"+$("#activity_list .activity_line:last-child").html()+"</div>";
    $("#activity_list").append(html);
    var input=$("#activity_list .activity_line:last-child input");
    for(var i=0;i<input.length;i++){
        $(input[i]).val("全部");
        if($(input[i]).attr("data-value")!=undefined){
            $(input[i]).attr("data-value","")
        }
    }
});
$("#tab_coupon_ul").on("click",".tab_coupon",function(){
    var index=$(this).index();
    $(this).siblings().removeClass("active");
    $(this).addClass("active");
    $("#coupon_list").children().hide();
    $("#coupon_list").children().eq(index).show()
});
$("#tab_task_ul").on("click",".tab_task",function(){
    var index=$(this).index();
    $(this).siblings().removeClass("active");
    $(this).addClass("active");
    $("#task_list").children().hide();
    $("#task_list").children().eq(index).show()
});
$("#tab_activity_ul").on("click",".tab_activity",function(){
    var index=$(this).index();
    $(this).siblings().removeClass("active");
    $(this).addClass("active");
    $("#activity_list").children().hide();
    $("#activity_list").children().eq(index).show()
});
$("#coupon").on("click",".filter_coupon",function(){
    $(this).next().toggle();
});
$("#coupon").on("click",".filter_coupon_ul li",function(){
    $(this).parent().prev().val($(this).text());
    $(this).parent().prev().attr("data-value",$(this).attr("data-couponcode"));
    $(this).parent().hide();
});
$("#activity").on("click",".filter_activity",function(){
    $(this).next().toggle();
});
$("#activity").on("click",".filter_activity_ul li",function(){
    $(this).parent().prev().val($(this).text());
    $(this).parent().prev().attr("data-value",$(this).attr("data-code"));
    $(this).parent().hide();
});
$("#participation_task").on("click",".filter_task",function(){
    $(this).next().toggle();
});
$("#participation_task").on("click",".filter_task_ul li",function(){
    $(this).parent().prev().val($(this).text());
    $(this).parent().prev().attr("data-value",$(this).attr("data-code"));
    $(this).parent().hide();
});
$("#filter_condition li").click(function(){
    var text=$(this).find("a").html();
    if(text=="券"){
        var param={};
        param["corp_code"]=$("#group_list li ul li.active").attr("data-code")?$("#group_list li ul li.active").attr("data-code"):$("#OWN_CORP").val();
        param["app_id"]="";
        var li="<li data-couponcode=''>全部</li>";
        oc.postRequire("post","/vipRules/getAllCoupon","",param, function(data){
            if(data.code=="0"){
                var list=JSON.parse(data.message);
                for(var i=0;i<list.length;i++){
                    li+="<li data-couponcode='"+list[i].couponcode+"' title='"+list[i].name+"'>"+list[i].name+"</li>";
                }
                $(".filter_coupon").next().html(li)
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
});
$("#filter_condition li").click(function(){
    var text=$(this).find("a").html();
    if(text=="会员任务"){
        var param={};
        param["corp_code"]=$("#group_list li ul li.active").attr("data-code")?$("#group_list li ul li.active").attr("data-code"):$("#OWN_CORP").val();
        var li="<li data-code=''>全部</li>";
        oc.postRequire("post","/vipTask/getAllByStatus","",param, function(data){
            if(data.code=="0"){
                var list=JSON.parse(data.message).list;
                for(var i=0;i<list.length;i++){
                    li+="<li data-code='"+list[i].task_code+"' title='"+list[i].task_title+"'>"+list[i].task_title+"</li>";
                }
                $(".filter_task").next().html(li)
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
});
$("#filter_condition li").click(function(){
    var text=$(this).find("a").html();
    if(text=="活动"){
        var param={};
        param["corp_code"]=$("#group_list li ul li.active").attr("data-code")?$("#group_list li ul li.active").attr("data-code"):$("#OWN_CORP").val();
        var li="<li data-code=''>全部</li>";
        oc.postRequire("post","/vipActivity/getAllActivityBySate","",param, function(data){
            if(data.code=="0"){
                var list=JSON.parse(data.message).list;
                for(var i=0;i<list.length;i++){
                    li+="<li data-code='"+list[i].activity_code+"' title='"+list[i].activity_theme+"'>"+list[i].activity_theme+"</li>";
                }
                $(".filter_activity").next().html(li)
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
});
$("#task_list").on("click",".task_line .contion_input_task:last-child input.select",function(){
    if($(this).parent().prev().find("input.select").val()=="全部"){
        $(this).next("ul").find("li:not(:first-child)").hide();
    }else{
        $(this).next("ul").find("li").show()
    }
});
$("#task_list").on("click",".task_line .contion_input_task:first-child ul li",function(){
    if($(this).html()=="全部"){
        $(this).parents(".contion_input_task").next().find("input.select").val("全部")
    }
});
$("#activity_list").on("click",".activity_line .contion_input_activity:last-child input.select",function(){
    if($(this).parent().prev().find("input.select").val()=="全部"){
        $(this).next("ul").find("li:not(:first-child)").hide();
    }else{
        $(this).next("ul").find("li").show()
    }
});
$("#activity_list").on("click",".activity_line .contion_input_activity:first-child ul li",function(){
    if($(this).html()=="全部"){
        $(this).parents(".contion_input_activity").next().find("input.select").val("全部")
    }
});
