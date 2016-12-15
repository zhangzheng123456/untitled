var groupName = [];
var groupCode = [];//定义分组里的值
//日期调用插件
var simple_birth_start={
    elem: '#simple_birth_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed:true,
    choose: function (datas) {
        simple_birth_end.min = datas; //开始日选好后，重置结束日的最小日期
        simple_birth_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var simple_birth_end={
    elem: '#simple_birth_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed:true,
    choose: function (datas) {
        simple_birth_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var mark_start={
    elem: '#simple_mark_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed:true,
    choose: function (datas) {
        mark_end.min = datas; //开始日选好后，重置结束日的最小日期
        mark_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var mark_end={
    elem: '#simple_mark_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed:true,
    choose: function (datas) {
        mark_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var start={
    elem: '#birth_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed:true,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas; //将结束日的初始值设定为开始日
    }
};
var end={
    elem: '#birth_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed:true,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var activity_start={
    elem: '#activate_card_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed:true,
    choose: function (datas) {
        activity_end.min = datas; //开始日选好后，重置结束日的最小日期
        activity_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var activity_end={
    elem: '#activate_card_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed:true,
    choose: function (datas) {
        activity_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var recent_start={
    elem: '#recent_start',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed:true,
    choose: function (datas) {
        recent_end.min = datas; //开始日选好后，重置结束日的最小日期
        recent_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var recent_end={
    elem: '#recent_end',
    format: 'YYYY-MM-DD',
    istime: true,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed:true,
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
laydate(recent_start);
laydate(recent_end);
//点击筛选
$("#filtrate").click(function () {
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_wrapper").show();
});
//高级筛选弹窗
$("#more_filter").click(function () {
    $("#simple_filter").hide();
    $("#senior_filter").show();
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2;
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px"});
});
$("#back_filter").click(function () {
    $("#simple_filter").show();
    $("#senior_filter").hide();
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2;
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px"});
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
$("#age_l").click(function () {
    $(".age_l").toggle();
});
$("#age_r").click(function () {
    $(".age_r").toggle();
});
$(".age_l").on("click","li",function () {
    var max = $("#age_r").val();
    var val = parseInt($(this).html());
    if(val >= max && max !== ""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "不能大于右边的年龄哦"
        });
        return;
    }
    $("#age_l").val($(this).html());
    $(".age_l").hide();
});
$(".age_r").on("click","li",function () {
    var min = $("#age_l").val();
    var val = parseInt($(this).html());
    if(val <= min  && min !== ""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "不能小于右边的年龄哦"
        });
        return;
    }
    $("#age_r").val($(this).html());
    $(".age_r").hide();
});
$(".filter_group ul").on("click","li",function () {
    var val = $(this).html();
    var code = $(this).attr("id");
    if($(this).attr("class") == "group_active"){
        $(this).removeClass("group_active");
        groupName.remove(val);
        groupCode.remove(code);
    }else {
        $(this).addClass("group_active");
        groupName.push(val);
        groupCode.push(code);
    }
    $("#filter_group").val(groupName.toString());
    $("#filter_group").attr("data-code",groupCode.toString);
});
//获取分组
$("#filter_group").click(function () {
    $(".filter_group").toggle();
    var corp = $("#filter_group").attr("data-corp");
    if(corp !== undefined){
        $(".filter_group #ul").getNiceScroll().resize();
        return ;
    }else {
        getGroup();
    }
});
$("#search_filter_group").keydown(function () {
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        getGroup();
    }
});
$(document).click(function (e) {
    if (!($(e.target).is("#sex_select")||$(e.target).is("#sex"))) {
        $("#sex_select").hide();
    }
    if (!($(e.target).is("#state_select")||$(e.target).is("#state"))) {
        $("#state_select").hide();
    }
    if (!($(e.target).is("#simple_state_select")||$(e.target).is("#simple_state"))) {
        $("#simple_state_select").hide();
    }
    if(!($(e.target).is(".age_l")||$(e.target).is("#age_l"))) {
        $(".age_l").hide()
    }
    if(!($(e.target).is(".age_r")||$(e.target).is("#age_r"))){
        $(".age_r").hide();
    }
    if(!($(e.target).is("#filter_group")||$(e.target).is(".filter_group input"))){
        $(".filter_group").hide();
    }
    if(!($(e.target).is("#batch_search_label"))){
        $(".batch_search_label ul").hide();
    }
    if(!($(e.target).is("#vip_card_type"))){
        $("#card_type_select").hide();
    }
});
$(function () {
    //给年龄赋值
    for(var i=1;i<101;i++){
        $(".age_l").append('<li>'+i+'</li>');
        $(".age_r").append('<li>'+i+'</li>')
    }
    //引用滚动样式插件
    $(".filter_group #ul").niceScroll({
        cursorborder:"0 none",cursoropacitymin:"0",boxzoom:false,
        cursorcolor:" rgba(0,0,0,0.2)",
        cursoropacitymax:1,
        touchbehavior:false,
        cursorminheight:30,
        autohidemode:false
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
    })
});
//定义remove
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    if(input.type=="checkbox"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.checked==true){
        input.checked = false;
    }
});
//获取分组
function getGroup() {
    var corp_command = "/vipGroup/getCorpGroups";
    var _param = {};
    _param["corp_code"] = "C10000";
    _param["search_value"] = $("#search_filter_group").val();
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#filter_group").attr("data-corp",list[0].corp_code);
            $(".filter_group ul").empty();
            if (list.length>0) {
                for (var i = 0; i < list.length; i++) {
                    html += '<li id="' + list[i].vip_group_code + '">' + list[i].vip_group_name + '</li>';
                }
                $(".filter_group ul").append(html);
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
});
//点击右移全部
$(".shift_right_all").click(function(){
    var right="all";
    var div=$(this);
    removeRight(right,div);
});
//点击左移
$(".shift_left").click(function(){
    var left="only";
    var div=$(this);
    removeLeft(left,div);
});
//点击左移全部
$(".shift_left_all").click(function(){
    var left="all";
    var div=$(this);
    removeLeft(left,div);
});