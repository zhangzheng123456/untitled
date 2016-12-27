//日期调用插件
var simple_birth_start = {
    elem: '#simple_birth_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        simple_birth_end.min = datas; //开始日选好后，重置结束日的最小日期
        simple_birth_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var simple_birth_end = {
    elem: '#simple_birth_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        simple_birth_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var mark_start = {
    elem: '#simple_mark_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        mark_end.min = datas; //开始日选好后，重置结束日的最小日期
        mark_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var mark_end = {
    elem: '#simple_mark_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        mark_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var start = {
    elem: '#birth_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas; //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#birth_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var activity_start = {
    elem: '#activate_card_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        activity_end.min = datas; //开始日选好后，重置结束日的最小日期
        activity_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var activity_end = {
    elem: '#activate_card_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        activity_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(simple_birth_start);
laydate(simple_birth_end);
laydate(mark_start);
laydate(mark_end);
laydate(start);
laydate(end);
laydate(activity_start);
laydate(activity_end);
//点击筛选
$("#filtrate").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_wrapper").show();
});
//高级筛选弹窗
$("#more_filter").click(function () {
    $("#simple_filter").hide();
    $("#senior_filter").show();
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px"});
});
$("#back_filter").click(function () {
    $("#simple_filter").show();
    $("#senior_filter").hide();
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px"});
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
});
$("#vip_card_type").click(function () {
    $("#card_type_select").toggle();
});
$("#card_type_select li").click(function () {
    $("#vip_card_type").val($(this).html());
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
$("#consume_select li").click(function () {
    $("#consume_date").val($(this).html());
    $("#consume_date").attr("data-date", $(this).attr("data-date"));
    $("#consume_select").hide();
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
$("#expend_attribute").on("click",".select",function () {
    $(this).next(".sex_select").toggle();
});
$("#expend_attribute").on("click",".sex_select li",function () {
    var _this=$(this).parent(".sex_select").prev(".select");
    $(_this).val($(this).html());
    $(".sex_select").hide();
});
$("#expend_attribute").on("blur",".select",function () {
   var ul = $(this).next(".sex_select");
    setTimeout(function(){
        ul.hide();
    },200);
});
//筛选确定
$("#screen_vip_que").click(function () {
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
        $("#simple_contion .contion_input").each(function () {
            var input = $(this).find("input");
            var key = $(input[0]).attr("data-kye");
            var classname = $(input[0]).attr("class");
            if (classname.indexOf("short") == 0) {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val()
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    screen.push(param);
                }
            } else {
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
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                } else if (key == "17") {
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
                } else {
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
    }
    _param['screen'] = screen;
    if (screen.length == 0) {
        GET(inx, pageSize);
    }else {
        filtrate = "sucess";
        filtrates(inx, pageSize);
    }
    $("#search").val("");
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//清空筛选
$("#empty_filter").click(function () {
    $("#screen_wrapper .contion_input input").each(function () {
        var key = $(this).attr("data-kye");
        if (key == "6" || key == "8" || key == "12") {
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
            return;
        } else {
            $(this).val("");
        }
    });
    $("#screen_wrapper textarea").each(function () {
        $(this).val("");
    });
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
        $("#screen_group .s_pitch span").html(h);
        $("#screen_group .screen_content_r ul").html(group_html_right);
    }else{
        $("#screen_group .s_pitch span").html("0");
        $("#screen_group .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_group").css({"left":+left+"px","top":+tp+"px"});
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
    };
    message.cache.group_codes=group_codes;
    message.cache.group_names=group_names;
    $("#screen_group").hide();
    $("#screen_wrapper").show();
    $("#filter_group").val("已选"+li.length+"个");
    $("#filter_group").attr("data-code",group_codes);
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
    if (!($(e.target).is("#consume_date"))) {
        $("#consume_select").hide();
    }
});
$(function () {
    expend_data();
    //给年龄赋值
    for (var i = 1; i < 101; i++) {
        $(".age_l").append('<li>' + i + '</li>');
        $(".age_r").append('<li>' + i + '</li>')
    }
    //引用滚动样式插件
    $(".filter_group #ul").niceScroll({
        cursorborder: "0 none", cursoropacitymin: "0", boxzoom: false,
        cursorcolor: " rgba(0,0,0,0.1)",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 30,
        autohidemode: false
    });
    $("#expend_attribute").niceScroll({
        cursorborder: "0 none", cursoropacitymin: "0", boxzoom: false,
        cursorcolor: " rgba(0,0,0,0.2)",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 30,
        autohidemode: "scroll"
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
    if (input.type == "checkbox" && input.checked == false) {
        input.checked = true;
    } else if (input.type == "checkbox" && input.checked == true) {
        input.checked = false;
    }
});
//验证input值
$("#screen_wrapper .contion_input").on("blur", "input", function () {
    var classname = $(this).attr("class");
    var key = $(this).attr("data-kye");
    var val = $(this).val();
    var _this=$(this);
    if (key == "10" && val!=="") {
        testInputNumber(val).then(function () {
            _this.val("");
        });
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
});
function testInputNumber(val, min, max) {//验证数字
    var reg = /^[0-9]*$/;
    var phpne = /^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$/;
    var def=$.Deferred();
    if (!reg.test(val)) {
        art.dialog({
            zIndex: 10010,
            time: 1,
            lock: true,
            cancel: false,
            content: "只能输入数字"
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
//获取分组
function getGroup() {
    var corp_command = "/vipGroup/getCorpGroups";
    var _param = {};
    _param["corp_code"] = "C10000";
    _param["search_value"] = $("#group_search").val();
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#screen_group .screen_content_l ul").empty();
            $("#filter_group").attr("data-corp", list[0].corp_code);
            if (list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
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
    var param={"corp_code":"C10000"};
    oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
       if(data.code == 0){
           $("#expend_attribute").empty();
           var msg = JSON.parse(data.message);
           var list = JSON.parse(msg.list);
           var html="";
           if(list.length>0){
               for(var i=0;i<list.length;i++){
                   var param_type = list[i].param_type;
                   if(param_type=="date"){
                       html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>' 
                           + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"><label class="jian">~</label>'
                           + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"></div>'
                   }
                   if(param_type=="select"){
                       var param_values = "";
                           param_values = list[i].param_values.split(",");
                       if(param_values.length>0){
                           var li="";
                           for(var j=0;j<param_values.length;j++){
                               li+='<li>'+param_values[j]+'</li>'
                           }
                           html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                               + li
                               + '</ul></div>'
                       }else {
                           html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                               + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                       }
                   }
                   if(param_type=="text"){
                       html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                           + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                   }
                   if(param_type=="longtext"){
                       html+='<div class="textarea"><label>'+list[i].param_desc+'</label>'
                           + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                   }
               }
           }
           $("#expend_attribute").append(html);
       }else if(data.code == -1){
           console.log(data.message);
       }
    });
}
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    $("#screen_staff .screen_content_l li:odd").css("backgroundColor", "#fff");
    $("#screen_staff .screen_content_l li:even").css("backgroundColor", "#ededed");
    $("#screen_staff .screen_content_r li:odd").css("backgroundColor", "#fff");
    $("#screen_staff .screen_content_r li:even").css("backgroundColor", "#ededed");
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}