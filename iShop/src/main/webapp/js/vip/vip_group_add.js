var oc = new ObjectControl();
var pageNumber = 1;//删除默认第一页
var pageSize = 10;//默认传的每页多少行
var value = "";//收索的关键词
var role = '';//识别页面
var _param = {};
var group_cheked = [];
var corp_code = '';//企业编号
var group_code = '';//分组编号
var group_name='';//分组名称
var group_remark='';//分组备注
var group_count = '';
var user_nextpage = false;//导购拉下一页
var user_num=1;
var param = {};//搜索参数
var choose_this_group='';//当前已选择
var all_select_vip_list=[];
var clickMark="";
var defindeVipScreen="";
var  message={
    cache:{//缓存变量
        "group_codes":"",
        "staff_codes":"",
        "staff_names":"",
        "brand_codes":"",
        "brand_names":"",
        "area_codes":"",
        "area_names":"",
        "store_codes":"",
        "store_names":""
    }
};
var group='<ul class="condition_group">'
    +'<li>'
    +'<div class="group_title"><span class="icon-ishop_6-33" style="margin-right: 5px"></span><span>条件组</span><span class="select_jb" data-code="AND"><span>交集</span><div style="display: none;"><span data-code="AND">交集-同时满足条件</span><span data-code="OR">并集-满足任意条件</span></div></span><span class="icon-ishop_6-29"></span></div>'
    +'</li>'
    +'</ul>';
$("#custom_vip_list").on("click",".icon-ishop_6-33",function(){
    $(this).parent().siblings().hide();
    $(this).addClass("icon-ishop_6-32").removeClass("icon-ishop_6-33")
});
$("#custom_vip_list").on("click",".icon-ishop_6-32",function(){
    $(this).parent().siblings().show();
    $(this).addClass("icon-ishop_6-33").removeClass("icon-ishop_6-32")
});
$("#custom_vip_list").on("click",".icon-ishop_6-29",function(event){
    event.stopPropagation();
    $(".icon-ishop_6-29").children("div").remove();
    if($(this).parent().hasClass("group_title")){
        $(this).append($("#isGroup").clone());
        $(this).find("#isGroup").show();
        $("#isCondition").hide()
    }else{
        $(this).append($("#isCondition").clone());
        $(this).find("#isCondition").show();
        $("#isGroup").hide()
    }
    $(".select_jb").children("div").hide();
});
$("#custom_vip_list").on("click","#isGroup span",function(event){
    event.stopPropagation();
    var todo=$(this).attr("data-code");
    if(todo=="add_group"){
        var floor=$(this).closest(".icon-ishop_6-29").attr("data-floor");
        if(floor==5){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "层级最高为5级"
            });
            return
        }
        if($(this).parents(".group_title").parent().find(">ul").length==0){
            $(this).parents(".group_title").after(group);
            $(this).parents(".group_title").next().children("li").children(".group_title").find(".icon-ishop_6-29").attr("data-floor",Number(floor)+1)
        }else{
            var ul=$(this).parents(".group_title").parent().find(">ul");
            ul.eq(ul.length-1).after(group);
            ul.eq(ul.length-1).next().children("li").children(".group_title").find(".icon-ishop_6-29").attr("data-floor",Number(floor)+1)
        }
        if($(this).parents(".group_title").children().eq(0).hasClass("icon-ishop_6-32")){
            $(this).parents(".group_title").siblings().hide();
        }
        $(".icon-ishop_6-29").children("div").remove();
    }else if(todo=="add_condition"){
        $(".select_box").css({
            height: $(window).height() - 130 + "px"
        });
        $("#custom_vip_list .icon-ishop_6-29").removeClass("is_add_condition");
        $(this).parent().parent().addClass("is_add_condition");
        $(".select_box_wrap").show();
        var top=$(window).height()-$(".select_box_wrap .select_box").outerHeight();
        $(".select_box_wrap .select_box").css({"top":top/2,"left":"50%"});
        select_clear_all();
        $(".tabs_head>label:first-child").trigger("click");
        $(".select_box_wrap").css("bottom",$(window).height()-$(document).height());
    }else if(todo=="delete_group"){
        if($(this).closest("ul").hasClass("is_first_condition_group")){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "此条件组不能删除"
            });
        }else {
            $(this).closest("ul").remove()
        }
    }
});
function select_clear_all() {
    $(".select_box_content_center_box .center_item_group").hide().find(".item_list").html("");
    $(".select_box_content_center_box").find(".notSelect").show();
    $(".select_box_content_right .selected_list").html("");
    $("#staff_brand_num").val("全部");
    $("#staff_area_num").val("全部");
    $("#staff_shop_num").val("全部");
    $("#brand_num").val("全部");
    $("#area_num").val("全部");
    $("#staff_search").val("");
    $("#brand_search").val("");
    $("#store_search").val("");
    $("#area_search").val("");
    $("#group_search").val("");
    $("#vip_type_search").val("");
    $("#public_signal_search").val("");
    select_affiliation = {
        brand: {
            code: "",
            name: ""
        },
        area: {
            code: "",
            name: ""
        },
        shop: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        staff: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        basic_staff: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        group: {
            code: "",
            name: ""
        },
        vip_type:{
            code:"",
            name:""
        },
        public_signal:{
            code:"",
            name:""
        },
        act_store:{
            code:"",
            name:""
        }
    };
    resetLeftLonely();
}
$("#custom_vip_list").on("click","#isCondition span",function(event){
    event.stopPropagation();
    var todo=$(this).attr("data-code");
    if(todo=="delete"){
        $(this).closest("div.condition").remove()
    }
});
$("#custom_vip_list").on("click",".select_jb",function(event){
    event.stopPropagation();
    $("#custom_vip_list .select_jb").children("div").hide();
    $(this).children("div").show();
    $(".icon-ishop_6-29").children("div").hide();
});
$("#custom_vip_list").on("click",".select_jb div span",function(event){
    event.stopPropagation();
    if($(this).attr("data-code")=="AND"){
        $(this).parents("span.select_jb").css("backgroundImage","url(../img/jji.png)");
        $(this).parents("span.select_jb").attr("data-code","AND");
        $(this).parents("span.select_jb div").prev().text("交集")
    }else {
        $(this).parents("span.select_jb").css("backgroundImage","url(../img/bji.png)");
        $(this).parents("span.select_jb").attr("data-code","OR");
        $(this).parents("span.select_jb div").prev().text("并集")
    }
    $("#custom_vip_list .select_jb").children("div").hide();
});
$(document).click(function(e){
    $(".icon-ishop_6-29").children("div").remove();
    $("#custom_vip_list .select_jb").children("div").hide();
});
function define(li,arr){
    for(var a=0;a<li.length;a++){
        if($(li[a]).hasClass("condition")){
            arr.push(JSON.parse($(li[a]).attr("data-value")));
        }else if($(li[a]).hasClass("condition_group")){
            var op = $(li[a]).find(".select_jb").attr("data-code");
            var b = [];
            var obj = {list:b,operator:op};
            if($(li[a]).find(".condition").length==0){
                continue
            }
            arr.push(obj);
            define($(li[a]).children("li").children(),b)
        }
    }
}
function setAddVipGroupList(screenList,parentSelector){
    var data=screenList;
    var div="";
    for(var d=0;d<data.length;d++){
        var value=data[d].value;
        div+='<div><em>'+data[d].groupName+'</em><span class="select_detail">';
        if(data[d].type=="interval"){
            for(var v=0;v<value.length;v++){
                var logic_1=getLogicDefault(value[v].logic1);
                var logic_2=getLogicDefault(value[v].logic2);
                if(v!=0){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    div+=opera
                }
                if(data[d].key=="TRADE" || data[d].key=="COUPON"){
                    var head="";
                    var foot="";
                    var time="";
                    var dataKey="";
                    var index=value[v].key.lastIndexOf("_");
                    var name=value[v].name;
                    if(data[d].key=="COUPON"){
                        dataKey=value[v].key.slice(index+1);
                    }else {
                        time=value[v].key.slice(index+1);
                        if(time!="all" && isNaN(time)){
                            dataKey=value[v].key
                        }else{
                            dataKey=value[v].key.slice(0,index)
                        }
                    }
                    switch (dataKey){
                        case "NUM_TRADE":
                            head="消费次数";
                            foot="次";
                            break;
                        case "AMT_TRADE":
                            head="消费金额";
                            foot="元";
                            break;
                        case "NUM_TRADE_R":
                            head="退款次数";
                            foot="次";
                            name="";
                            break;
                        case "AMT_TRADE_R":
                            head="退款金额";
                            foot="元";
                            name="";
                            break;
                        case "TOTAL":
                            head="获得";
                            foot="个";
                            break;
                        case "VERIFYED":
                            head="使用";
                            foot="个";
                            break;
                        case "EXPIRE":
                            head="过期";
                            foot="个";
                            break;
                        case "AVAILABLE":
                            head="可用";
                            foot="个";
                            break
                    }
                    var htmlHead="";
                    if(data[d].key=="TRADE"){
                        htmlHead='<p class="logic">'+name+'</p>'+head;
                    }else{
                        htmlHead=head+'<p class="selected_value">'+name+'</p>';
                    }
                    div+=htmlHead+
                        '<p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'+
                        foot
                }else if(data[d].key=="CATA1_PRD" || data[d].key=="CATA2_PRD" || data[d].key=="CATA3_PRD"|| data[d].key=="CATA4_PRD" || data[d].key=="SEASON_PRD" || data[d].key=="T_BL_M" || data[d].key=="DAYOFWEEK" || data[d].key=="BRAND_ID" || data[d].key=="CATA3_PRD" || data[d].key=="STORE_AREA" || data[d].key=="PRICE_CODE"){
                    div+='<p class="selected_value">'+value[v].name+'</p><p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'
                }else{
                    div+='<p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'
                }
            }
        }else if(data[d].type=="radio"){
            for(var r=0;r<value.length;r++){
                if(data[d].key=="TASK" || data[d].key=="ACTIVITY"){
                    if(value[r].value=="Y"){
                        div+='<p class="logic">已参与</p>'+
                            '<p class="selected_value">'+value[r].name+'</p>'
                    }else if(value[r].value=="N"){
                        div+='<p class="logic">未参与</p>'+
                            '<p class="selected_value">'+value[r].name+'</p>'
                    }
                }else{
                    if(data[d].key=="SEX_VIP"){
                        div+='<p class="logic">'+getLogicDefault(value[r].logic)+'</p>'
                    }
                    div+='<p class="selected_value">'+value[r].dataName+'</p>'
                }
            }
        }else if(data[d].type=="text"){
            if( data[d].key=="user_code" || data[d].key=="brand_code" || data[d].key=="area_code" || data[d].key=="store_code"|| data[d].key=="VIP_GROUP_CODE" ){
                div+='<p class="selected_value">'+data[d].name+'</p>'
            }else if(data[d].key=="ZONE"){
                var value=data[d].value;
                for(var t=0;t<value.length;t++ ){
                    var valName=value[t].value.split("-");
                    var name_zone="";
                    if(valName[0]!="省"){
                        name_zone+=valName[0];
                    }if(valName[1]!="市"){
                        name_zone+="-"+valName[1];
                    }
                    if(valName[2]!="区"){
                        name_zone+="-"+valName[2];
                    }
                    if(t!=0){
                        var opera=value[t].opera=="AND"?"并且":value[t].opera=="OR"?"或者":"";
                        div+=opera
                    }
                    div+='<p class="logic">'+getLogicDefault(value[t].logic)+'</p>';
                    div+='<p class="selected_value">'+name_zone+'</p>'
                }
            }else{
                var value=data[d].value;
                for(var t=0;t<value.length;t++ ){
                    if(t!=0){
                        var opera=value[t].opera=="AND"?"并且":value[t].opera=="OR"?"或者":"";
                        div+=opera
                    }
                    div+='<p class="logic">'+getLogicDefault(value[t].logic)+'</p>';
                    if(data[d].key=="APP_ID"){
                        div+='<p class="selected_value">'+value[t].name+'</p>'
                    }else {
                        div+='<p class="selected_value">'+value[t].value+'</p>'
                    }
                }
            }
        }
        div=div+'</span></div></div>'
    }
    parentSelector.html(div)
}
$(function () {
    window.vip.init();
    if ($(".pre_title label").text() == "编辑分组") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        whir.loading.add("", 0.5);
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                if (action.length <= 0) {
                    $(".vip_group_edit_oper_btn ul li:nth-of-type(1)").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
        _params = {};
        _params["id"] = id;
        var _command = "/vipGroup/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                $('#group_recode').val('共' + msg.vip_count + "个会员");
                $("#vip_id").val(msg.vip_group_name);
                $("#vip_id").attr("data-name", msg.vip_group_name);
                $("#vip_num").val(msg.vip_group_code);
                $("#vip_num").attr("data-name", msg.vip_group_code);
                $("#vip_remark").val(msg.remark);
                $("#OWN_CORP option").val(msg.corp_code);
                $("#PARAM_NAME").attr("data-code",msg.user_code);
                $("#PARAM_NAME").val(msg.user_name);
                var Code = msg.corp_code;
                corp_code=msg.corp_code;
                $("#OWN_CORP option").text(msg.corp_name);
                $("#area_shop").val("共"+msg.store_count+"家店铺");
                 $("#OWN_CORP").val(msg.corp_code);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var active_input = $("#is_active");
                var public_input = $("#is_public");
                if (msg.isactive == "Y") {
                    active_input.prop("checked",true)
                } else if (msg.isactive == "N") {
                    active_input.prop("checked",false)
                }
                if (msg.is_public == "Y") {
                    public_input.prop("checked",true)
                } else if (msg.is_public == "N") {
                    public_input.prop("checked",false)
                }
                if(msg.group_type=="define"){
                    var K=2;
                    var html='<div style="margin: 0"><ul class="condition_group is_first_condition_group">'
                        +'<li>'
                        +'<div class="group_title"><span class="icon-ishop_6-33" style="margin-right: 5px"></span><span>条件组</span><span class="select_jb" data-code="AND"><span>交集</span><div style="display: none;"><span data-code="AND">交集-同时满足条件</span><span data-code="OR">并集-满足任意条件</span></div></span><span class="icon-ishop_6-29" data-floor="1"></span></div></li></ul></div>'
                        $("#operate_div").prev().remove();
                        $("#custom_vip_list").append(html);
                        $("#custom_vip_list").show();
                    var html1='<ul class="condition_group" data-code="1"><li><div class="group_title"><span class="icon-ishop_6-33" style="margin-right: 5px"></span><span>条件组</span><span class="select_jb" data-code="AND"><span>交集</span><div style="display: none;"><span data-code="AND">交集-同时满足条件</span><span data-code="OR">并集-满足任意条件</span></div></span><span class="icon-ishop_6-29"></span></div></li></ul>'
                    function showSelect_1(list,li,K){
                        for(var i=0;i<list.length;i++){
                            if(list[i].list){
                                if(list[i].operator=="OR"){
                                    var html= $(html1).clone();
                                    $(html).find(".select_jb").attr("data-code","OR");
                                    $(html).find(".select_jb").css("backgroundImage","url(../img/bji.png)");
                                    $(html).find(".select_jb").children().eq(0).text("并集");
                                }else{
                                    var html= $(html1).clone();
                                }
                                $(html).find(".icon-ishop_6-29").attr("data-floor",K);
                                $(li).children("li").append(html);
                                showSelect_1(list[i].list,$(html),K+1);
                            }else{
                                var type=list[i].type;
                                var key=list[i].key;
                                var name=list[i].name;
                                var ob=list[i];
                                if(type=="interval"){
                                    var logic1=getLogicDefault(list[i].logic1);
                                    var logic2=getLogicDefault(list[i].logic2);
                                    if(key=="TRADE"){
                                        var head="";
                                        var foot="";
                                        var key_v2=list[i].key_v2;
                                        var time=key_v2.slice(key_v2.lastIndexOf("_")+1);
                                        if(time!="all" && isNaN(time)){
                                            switch (key_v2){
                                                case "NUM_TRADE":
                                                    head="消费次数";
                                                    foot="次";
                                                    break;
                                                case "AMT_TRADE":
                                                    head="消费金额";
                                                    foot="元";
                                                    break;
                                                case "NUM_TRADE_R":
                                                    head="退款次数";
                                                    foot="次";
                                                    break;
                                                case "AMT_TRADE_R":
                                                    head="退款金额";
                                                    foot="元";
                                                    break
                                            }
                                            var condition='<div class="condition" ' +
                                                'data-value=\''+JSON.stringify(ob)+'\'>'+
                                                '<span class="condition_title" title="'+list[i]["scondeName"]+'">'+list[i]["scondeName"]+'</span>' +
                                                '<span class="condition_detail" title="'+head+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+foot+'">'+head+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+foot+'</span>'+
                                                '<span class="icon-ishop_6-29"></span>' +
                                                '</div>'
                                        }else{
                                            var dataKey=key_v2.slice(0,key_v2.lastIndexOf("_"));
                                            switch (dataKey){
                                                case "NUM_TRADE":
                                                    head="消费次数";
                                                    foot="次";
                                                    break;
                                                case "AMT_TRADE":
                                                    head="消费金额";
                                                    foot="元";
                                                    break;
                                                case "NUM_TRADE_R":
                                                    head="退款次数";
                                                    foot="次";
                                                    break;
                                                case "AMT_TRADE_R":
                                                    head="退款金额";
                                                    foot="元";
                                                    break
                                            }
                                            var timeHtml="";
                                            time=="all"?timeHtml="累计":timeHtml='最近'+time+'个月';
                                            var condition='<div class="condition" ' +
                                                'data-value=\''+JSON.stringify(ob)+'\'>'+
                                                '<span class="condition_title" title="'+list[i]["scondeName"]+'">'+list[i]["scondeName"]+'</span>' +
                                                '<span class="condition_detail" title="'+timeHtml+head+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+foot+'">'+timeHtml+head+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+foot+'</span>'+
                                                '<span class="icon-ishop_6-29"></span>' +
                                                '</div>'
                                        }
                                    }else  if(key=="COUPON"){
                                        var head="";
                                        var key_v2=list[i].key_v2;
                                        var dataKey=key_v2.slice(key_v2.lastIndexOf("_")+1);
                                        switch (dataKey){
                                            case "TOTAL":
                                                head="获得";
                                                break;
                                            case "VERIFYED":
                                                head="使用";
                                                break;
                                            case "EXPIRE":
                                                head="过期";
                                                break;
                                            case "AVAILABLE":
                                                head="可用";
                                                break
                                        }
                                        var condition='<div class="condition" ' +
                                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                                            '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                            '<span class="condition_detail" title="'+head+list[i]["typeName"]+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'个">'+head+list[i]["typeName"]+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'个</span>'+
                                            '<span class="icon-ishop_6-29"></span>' +
                                            '</div>'
                                    }else if(key=="CATA1_PRD" || key=="CATA2_PRD" || key=="CATA3_PRD"|| key=="CATA4_PRD" || key=="SEASON_PRD" || key=="T_BL_M" || key=="DAYOFWEEK" || key=="BRAND_ID" || key=="CATA3_PRD" || key=="STORE_AREA" || key=="PRICE_CODE"){
                                        var condition='<div class="condition" ' +
                                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                                            '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                            '<span class="condition_detail" title="'+list[i]["typeName"]+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'">'+list[i]["typeName"]+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'</span>' +
                                            '<span class="icon-ishop_6-29"></span>' +
                                            '</div>'
                                    }else{
                                        var condition='<div class="condition" ' +
                                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                                            '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                            '<span class="condition_detail" title="'+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'">'+logic1+list[i]["start"]+'至'+logic2+list[i]["end"]+'</span>' +
                                            '<span class="icon-ishop_6-29"></span>' +
                                            '</div>'
                                    }
                                }else if(type=="text"){
                                        var logic=getLogicDefault(list[i].logic);
                                        if(key == "brand_code" || key == "area_code" || key == "store_code" || key =="user_code" || key=="VIP_GROUP_CODE"){
                                            var condition='<div class="condition" ' +
                                                'data-value=\''+JSON.stringify(ob)+'\'>'+
                                                '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                                '<span class="condition_detail" title="'+logic+list[i]["dataName"]+'">'+logic+list[i]["dataName"]+'</span>' +
                                                '<span class="icon-ishop_6-29"></span>' +
                                                '</div>'
                                        }else if(key=="ZONE"){
                                            var valName=list[i]["value"].split("-");
                                            var name_zone="";
                                            if(valName[0]!="省"){
                                                name_zone+=valName[0];
                                            }if(valName[1]!="市"){
                                                name_zone+="-"+valName[1];
                                            }
                                            if(valName[2]!="区"){
                                                name_zone+="-"+valName[2];
                                            }
                                            var condition='<div class="condition" ' +
                                                'data-value=\''+JSON.stringify(ob)+'\'>'+
                                                '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                                '<span class="condition_detail" title="'+logic+name_zone+'">'+logic+name_zone+'</span>' +
                                                '<span class="icon-ishop_6-29"></span>' +
                                                '</div>'
                                        }else {
                                            var value="";
                                            if(key=="APP_ID"){
                                                value=list[i]["dataName"]
                                            }else{
                                                value=list[i]["value"];
                                            }
                                            var condition='<div class="condition" ' +
                                                'data-value=\''+JSON.stringify(ob)+'\'>'+
                                                '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                                '<span class="condition_detail" title="'+logic+value+'">'+logic+value+'</span>' +
                                                '<span class="icon-ishop_6-29"></span>' +
                                                '</div>'
                                        }
                                }else if(type=="radio"){
                                    var logic=getLogicDefault(list[i].logic);
                                    if(key == "ACTIVITY" || key == "TASK") {
                                        var val=list[i]["value"]=='Y'?"已参与":list[i]["value"]=='N'?"未参与":"";
                                        var dataName=list[i]["dataName"];
                                        var condition='<div class="condition" ' +
                                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                                            '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                            '<span class="condition_detail" title="'+val+dataName+'">'+val+dataName+'</span>' +
                                            '<span class="icon-ishop_6-29"></span>' +
                                            '</div>'
                                    }else {
                                        var val=list[i]["dataName"];
                                        var condition='<div class="condition" ' +
                                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                                            '<span class="condition_title" title="'+name+'">'+name+'</span>' +
                                            '<span class="condition_detail" title="'+logic+val+'">'+logic+val+'</span>' +
                                            '<span class="icon-ishop_6-29"></span>' +
                                            '</div>'
                                    }
                                }
                                $(li).children("li").append(condition);
                            }
                        }
                    }
                    var group_condition=JSON.parse(msg.group_condition);
                    var li = $(".is_first_condition_group");
                    if(group_condition.operator=="OR"){
                        $(".is_first_condition_group").children("li").children(".group_title").find(".select_jb").attr("data-code","OR");
                        $(".is_first_condition_group").children("li").children(".group_title").find(".select_jb").css("backgroundImage","url(../img/bji.png)");
                        $(".is_first_condition_group").children("li").children(".group_title").find(".select_jb").children().eq(0).text("并集");
                    }
                    showSelect_1(group_condition.list,li,K);
                }else if(msg.group_type=="define_v2"){
                    var screen=JSON.parse(msg.group_condition);
                    var parentSelector=$("#custom_vip_list");
                    defindeVipScreen=screen;
                    $("#group_list").parent().before("<span style='margin-left: 125px;margin-top: -10px;display: inherit;margin-bottom: 5px;font-size: 13px;color: #888888;'>*该分组条件由会员档案生成，不支持修改</span>")
                    $("#group_type").attr("data-type","define_v2").unbind("click").css({'background':"#ffffff url(../img/btn_dropRead.png) no-repeat 100% 50%","cursor":"not-allowed","backgroundSize": "30px"});
                    $("#custom_vip_list").show().addClass("isDefinedVip");
                    setAddVipGroupList(screen,parentSelector)
                }else {
                    var list=JSON.parse(msg.group_condition);
                    showOtherGroup(list,msg.group_type);
                }
                getcorplist(Code);
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    } else {
        getcorplist();
        //$("#vip_num").val("VG"+ new Date().getTime())
    }
    //分配导购
    // $("#PARAM_NAME").click(function () {
    //     if ($("#allUser").css("display") == "none") {
    //         $("#allUser").show();
    //     } else {
    //         $("#allUser").hide();
    //     }
    // });
    // $(document).click(function (e) {
    //     if($(e.target).is("#search_user")||$(e.target).is("#PARAM_NAME")){
    //         return;
    //     }else {
    //         $("#allUser").hide();
    //     }
    // });
    //搜索导购
    // $("#search_user").keydown(function (e) {
    //     user_num = 1;
    //     var event=window.event||arguments[0]
    //     if(event.keyCode == 13){
    //         $("#allUser_list").empty();
    //         getuserlist();
    //     }
    // })
    //绑定导购点击事件
    // $("#allUser_list").on("click", "li", function (e) {
    //     e.stopPropagation();
    //     $("#PARAM_NAME").val($(this).text());
    //     $("#PARAM_NAME").attr("data-code", $(this).attr("id"));
    //     $("#allUser").hide();
    //     $(this).addClass("groupUser_checked");
    //     $(this).siblings("li").removeClass("groupUser_checked");
    // })
    //绑定导购滚动事件
    // $("#allUser_list").scroll(function () {
    //     var nScrollHight = $(this)[0].scrollHeight;
    //     var nScrollTop = $(this)[0].scrollTop;
    //     var nDivHight = $(this).height();
    //     if (nScrollTop + nDivHight >= nScrollHight) {
    //         if (user_nextpage==false) {
    //             return;
    //         }else {
    //             getuserlist();
    //         }
    //     }
    // })
    //滚动条样式
    // $("#allUser_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0)",cursoropacitymin:"0",boxzoom:false});
});
(function (root, factory) {
    root.vip = factory();
}(this, function () {
    var vipjs = {};
    vipjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    vipjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    vipjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    vipjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    vipjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length - 1; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    vipjs.bindbutton = function () {
        $(".vip_group_add_oper_btn ul li:nth-of-type(1)").click(function () {
            var name = $("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            //var num = $("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N") {
                    if (name == "N") {
                        var div = $("#vip_id").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    //if (num == "N") {
                    //    var div = $("#vip_num").next('.hint').children();
                    //    div.html("该编号已经存在！");
                    //    div.addClass("error_tips");
                    //}
                    return;
                }
                var vip_id = $("#vip_id").val();
                var vip_num = $("#vip_num").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var vip_remark = $("#vip_remark").val();
                var user_code = $("#PARAM_NAME").attr("data-code");
                var ISACTIVE = "";
                var ISPUBLIC = "";
                if(user_code==undefined){
                    user_code = "";
                }
                var active_input = $("#is_active");
                var public_input = $("#is_public");
                if (active_input.prop("checked") == true) {
                    ISACTIVE = "Y";
                } else if (active_input.prop("checked") == false) {
                    ISACTIVE = "N";
                }
                if (public_input.prop("checked") == true) {
                    ISPUBLIC = "Y";
                }else if (public_input.prop("checked") == false) {
                    ISPUBLIC = "N";
                }
                var _command = "/vipGroup/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    //"vip_group_code": vip_num,
                    "vip_group_name": vip_id,
                    "corp_code": OWN_CORP,
                    "user_code": user_code,
                    "remark": vip_remark,
                    "isactive": ISACTIVE,
                    "is_public": ISPUBLIC
                };
                var vip_group_type=$("#group_type").attr("data-type");
                _params["group_type"]=vip_group_type;
                vip_group_type=="define"?isDefine():noDefine();
                function isDefine(){
                    //var group_condition_array=all_select_vip_list;
                    var li=$("#custom_vip_list .is_first_condition_group>li").children();
                    var list = [] ;
                    define(li,list);
                    var obj={
                        list:list,
                        operator:$(".is_first_condition_group>li").find(".select_jb").attr("data-code")
                    };
                    _params["group_condition"]=obj;
                    if(obj["list"].length=="0"){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请先设置条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                function noDefine(){
                    var group_condition={};
                    var circle_val=$("#type").attr("data-type");
                    group_condition["circle"]=circle_val;
                    //$("#class").is(":hidden")==true? delete group_condition.class:group_condition["class"]=$("#class input").attr("data-name");
                    //$("#season").is(":hidden")==true? delete group_condition.quarter:group_condition["quarter"]=$("#season input").attr("data-name");
                    //$("#brand").is(":hidden")==true? delete group_condition.brand:group_condition["brand"]=brand_value;
                    if($("#class").is(":hidden")==true){
                        delete group_condition.class;
                    }else if($("#class").is(":hidden")==false && $("#class input").attr("data-code")!=undefined && $("#class input").attr("data-code")!=""){
                        group_condition["class"]=$("#class input").attr("data-code");
                    }else if($("#class").is(":hidden")===false && $("#class input").attr("data-code")==undefined || $("#class input").attr("data-code")==""){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择品类"
                        });
                        return false;
                    }
                    if($("#season").is(":hidden")==true){
                        delete group_condition.quarter;
                    }else if($("#season").is(":hidden")==false && $("#season input").attr("data-code")!=undefined && $("#season input").attr("data-code")!=""){
                        group_condition["quarter"]=$("#season input").attr("data-code");
                    }else if($("#season").is(":hidden")===false && $("#season input").attr("data-code")==undefined || $("#season input").attr("data-code")==""){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择季节"
                        });
                        return false;
                    }

                    if($("#brand").is(":hidden")==true){
                        delete group_condition.brand;
                    }else if($("#brand").is(":hidden")==false && (brand_value.length>0)){
                        group_condition["brand"]=brand_value;
                    }else if($("#brand").is(":hidden")==false && (brand_value.length==0)){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择品牌"
                        });
                        return false;
                    }
                    $("#consume_amount").is(":hidden")==true? delete group_condition.consume_amount:group_condition["consume_amount"]={start:$("#consume_amount input").eq(0).val(),end:$("#consume_amount input").eq(1).val()}; //消费金额
                    $("#consume_piece").is(":hidden")==true?delete group_condition.consume_piece:group_condition["consume_piece"]={start:$("#consume_piece input").eq(0).val(),end:$("#consume_piece input").eq(1).val()};   //消费件数
                    $("#consume_discount").is(":hidden")==true?delete group_condition.consume_discount:group_condition["consume_discount"]={start:$("#consume_discount input").eq(0).val(),end:$("#consume_discount input").eq(1).val()}; //消费折扣
                    _params["group_condition"]=group_condition;
                    var allInput=$("#group_list>div").not('.select_more').find("input");
                    var isNoValue=true;
                    for(var i=0;i<allInput.length;i++){
                        if(!$(allInput[i]).parent().is(":hidden") && $(allInput[i]).val().trim()!=""){
                            isNoValue=false;
                            break;
                        }
                    }
                    if(isNoValue){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"至少输入一项条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                //vipjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#edit_save").click(function () {
            var name = $("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            //var num = $("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N") {
                    var div = $("#vip_id").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                //if (num == "N") {
                //    var div = $("#vip_num").next('.hint').children();
                //    div.html("该编号已经存在！");
                //    div.addClass("error_tips");
                //    return;
                //}
                var ID = sessionStorage.getItem("id");
                var vip_id = $("#vip_id").val();
                var vip_num = $("#vip_num").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var vip_remark = $("#vip_remark").val();
                var user_code = $("#PARAM_NAME").attr("data-code");
                var ISACTIVE = "";
                var ISPUBLIC = "";
                var active_input = $("#is_active");
                var public_input = $("#is_public");
                if (active_input.prop("checked") == true) {
                    ISACTIVE = "Y";
                } else if (active_input.prop("checked") == false) {
                    ISACTIVE = "N";
                }
                if (public_input.prop("checked") == true) {
                    ISPUBLIC = "Y";
                } else if (public_input.prop("checked") == false) {
                    ISPUBLIC = "N";
                }
                var _command = "/vipGroup/edit";//接口名
                //var _command = "/vipGroup/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "id": ID,
                    "vip_group_code": vip_num,
                    "vip_group_name": vip_id,
                    "corp_code": OWN_CORP,
                    "user_code": user_code,
                    "remark": vip_remark,
                    // 'choose':sessionStorage.getItem('vip_choose'),
                    // 'quit':sessionStorage.getItem('vip_quit'),
                    "isactive": ISACTIVE,
                    "is_public":ISPUBLIC
                };
                var vip_group_type=$("#group_type").attr("data-type");
                _params["group_type"]=vip_group_type;
                if(vip_group_type=="define"){
                    isDefine()
                }else if(vip_group_type=="define_v2"){
                    _params["group_condition"]=defindeVipScreen;
                    vipjs.ajaxSubmit(_command, _params, opt);
                }else{
                    noDefine();
                }
                function isDefine(){
                    var li=$("#custom_vip_list .is_first_condition_group>li").children();
                    var list = [] ;
                    define(li,list);
                    var obj={
                        list:list,
                        operator:$(".is_first_condition_group>li").find(".select_jb").attr("data-code")
                    };
                    _params["group_condition"]=obj;
                    if(obj["list"].length=="0"){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请先设置条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                function noDefine(){
                    var group_condition={};
                    var circle_val=$("#type").attr("data-type");
                    group_condition["circle"]=circle_val;
                    //$("#class").is(":hidden")==true? delete group_condition.class:group_condition["class"]=$("#class input").attr("data-name");
                    //$("#season").is(":hidden")==true? delete group_condition.quarter:group_condition["quarter"]=$("#season input").attr("data-name");
                    //$("#brand").is(":hidden")==true? delete group_condition.brand:group_condition["brand"]=brand_value;
                    if($("#class").is(":hidden")==true){
                        delete group_condition.class;
                    }else if($("#class").is(":hidden")==false && $("#class input").attr("data-code")!=undefined && $("#class input").attr("data-code")!=""){
                        group_condition["class"]=$("#class input").attr("data-code");
                    }else if($("#class").is(":hidden")===false && $("#class input").attr("data-code")==undefined || $("#class input").attr("data-code")==""){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择品类"
                        });
                        return false;
                    }
                    if($("#season").is(":hidden")==true){
                        delete group_condition.quarter;
                    }else if($("#season").is(":hidden")==false && $("#season input").attr("data-code")!=undefined && $("#season input").attr("data-code")!=""){
                        group_condition["quarter"]=$("#season input").attr("data-code");
                    }else if($("#season").is(":hidden")===false && $("#season input").attr("data-code")==undefined || $("#season input").attr("data-code")==""){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择季节"
                        });
                        return false;
                    }

                    if($("#brand").is(":hidden")==true){
                        delete group_condition.brand;
                    }else if($("#brand").is(":hidden")==false && (brand_value.length>0)){
                       group_condition["brand"]=brand_value;
                    }else if($("#brand").is(":hidden")==false && (brand_value.length==0)){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择品牌"
                        });
                        return false;
                    }
                    $("#consume_amount").is(":hidden")==true? delete group_condition.consume_amount:group_condition["consume_amount"]={start:$("#consume_amount input").eq(0).val(),end:$("#consume_amount input").eq(1).val()}; //消费金额
                    $("#consume_piece").is(":hidden")==true?delete group_condition.consume_piece:group_condition["consume_piece"]={start:$("#consume_piece input").eq(0).val(),end:$("#consume_piece input").eq(1).val()};   //消费件数
                    $("#consume_discount").is(":hidden")==true?delete group_condition.consume_discount:group_condition["consume_discount"]={start:$("#consume_discount input").eq(0).val(),end:$("#consume_discount input").eq(1).val()}; //消费折扣
                    _params["group_condition"]=group_condition;
                   var allInput=$("#group_list>div").not('.select_more').find("input");
                    var isNoValue=true;
                    for(var i=0;i<allInput.length;i++){
                        if(!$(allInput[i]).parent().is(":hidden") && $(allInput[i]).val().trim()!=""){
                             isNoValue=false;
                            break;
                        }
                    }
                    if(isNoValue ){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"至少输入一项条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
            } else {
                return;
            }
        });
    };
    vipjs.ajaxSubmit = function (_command, _params, opt) {
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
               if(_command=="/vipGroup/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group_edit.html");
                }
                if(_command=="/vipGroup/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group.html");
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();
        });
    };
    var bindFun = function (obj1) {//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if (obj1) {
            _this = jQuery(obj1);
        } else {
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if (vipjs['check' + command]) {
            if (!vipjs['check' + command].apply(vipjs, [obj, hint])) {
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function () {
        var _this = this;
        interval = setInterval(function () {
            bindFun(_this);
        }, 500);
    }).blur(function (event) {
        clearInterval(interval);
    });
    var init = function () {
        vipjs.bindbutton();
    };
    var obj = {};
    obj.vipjs = vipjs;
    obj.init = init;
    return obj;
}));
$(".corp_select").on("click",".searchable-select-holder",function(){
    if($('#group_type').attr("data-type")=="define_v2"){
        $("#OWN_CORP").next().find(".searchable-select-dropdown").hide();
        return
    }
});

function getcorplist(a) {
    //获取所属企业列表
    var userNum = 1;//分配导购分页，默认第一页; var userNum = 1;//分配导购分页，默认第一页;
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var corp_html = '';
            for (var i=0;i<msg.corps.length;i++) {
                corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if (a !== "") {
                $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
            }
            $('.corp_select select').searchableSelect();
            if($('#group_type').attr("data-type")=="define_v2"){
                $(".searchable-select-holder").css({'background':"#ffffff url(../img/btn_dropRead.png) no-repeat 100% 50%","cursor":"not-allowed","backgroundSize":"30px"});
                return
            }
            select_corp_code=$("#OWN_CORP").val();
            expend_data_1();
            $('.corp_select .searchable-select-input').keydown(function(event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    user_num = 1;
                    $("#vip_id").val("");
                    $("#vip_num").val("");
                    $("#group_recode").val("");
                    $("#vip_remark").val("");
                    $("#vip_id").attr("data-mark", "");
                    $("#vip_num").attr("data-mark", "");
                    select_corp_code=$("#OWN_CORP").val();
                    expend_data_1();
                    $("#custom_vip_list .is_first_condition_group").children("li").children(".group_title").siblings().remove();
                    //$("#custom_vip_list").html("");
                    all_select_vip_list=[];
                    cla=[];
                    br=[];
                    sea=[];
                    brand_value=[];
                    $("#group_list").find("input").val("");
                    $("#empty_filter_define").trigger("click");
                    clearSelectList();
                    var reg=/活动|会员任务|券/;
                    if(reg.test($(".condition_active").eq(1).html())){
                        $(".condition_active").eq(1).parent().trigger("click");
                    }
                }
            });
            $('.searchable-select-item').click(function () {
                user_num = 1;
                $("#vip_id").val("");
                $("#vip_num").val("");
                $("#group_recode").val("");
                $("#vip_remark").val("");
                $("#vip_id").attr("data-mark", "");
                $("#vip_num").attr("data-mark", "");
                select_corp_code=$("#OWN_CORP").val();
                expend_data_1();
                $("#custom_vip_list .is_first_condition_group").children("li").children(".group_title").siblings().remove();
                message.cache.staff_codes="";
                message.cache.brand_codes="";
                message.cache.area_codes="";
                message.cache.store_codes="";
                $("#area_num").val("全部");
                $("#area_num").attr("data-brandcode","");
                $("#brand_num").val("全部");
                $("#brand_num").attr("data-brandcode","");
                $("#staff_area_num").val("全部");
                $("#staff_area_num").attr("data-areacode","");
                $("#staff_brand_num").val("全部");
                $("#staff_brand_num").attr("data-brandcode","");
                $("#staff_shop_num").val("全部");
                $("#staff_shop_num").attr("data-storecode","");
                all_select_vip_list=[];
                cla=[];
                br=[];
                sea=[];
                brand_value=[];
                $("#empty_filter_define").trigger("click");
                $("#group_list").find("input").val("");
                clearSelectList();
                var reg=/活动|会员任务|券/;
                if(reg.test($(".condition_active").eq(1).html())){
                  $(".condition_active").eq(1).parent().trigger("click");
                }
            });
            // getuserlist();
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
function clearSelectList(){
    $(".tab_activity").eq(0).addClass("active").siblings().remove();
    $(".tab_task").eq(0).addClass("active").siblings().remove();
    $(".tab_coupon").eq(0).addClass("active").siblings().remove();

    $(".activity_line").eq(0).siblings().remove();
    $(".activity_line").show();
    $(".activity_line").find(".filter_activity").val("全部").attr("data-value","");
    //$(".activity_line").find(".filter_activity_ul").html("");

    $(".task_line").eq(0).siblings().remove();
    $(".task_line").show();
    $(".task_line").find(".filter_task").val("全部").attr("data-value","");
    //$(".task_line").find(".filter_task_ul").html("");

    $(".coupon_line").eq(0).siblings().remove();
    $(".coupon_line").show();
    $(".coupon_line input").val("");
    $(".coupon_line").find(".filter_coupon").val("全部").attr("data-value","");
    //$(".coupon_line").find(".filter_coupon_ul").html("");

    $(".participate").val("全部")
}
//获取导购列表
// function getuserlist() {
//     var corp_code = $("#OWN_CORP").val();
//     var area_code = "";
//     var brand_code = "";
//     var store_code = "";
//     var searchValue = $("#search_user").val().trim();
//     var pageSize = 20;
//     var pageNumber = user_num;
//     var _param = {};
//     _param["corp_code"] = corp_code;
//     _param['area_code'] = area_code;
//     _param['brand_code'] = brand_code;
//     _param['store_code'] = store_code;
//     _param['searchValue'] = searchValue;
//     _param['pageNumber'] = pageNumber;
//     _param['pageSize'] = pageSize;
//     oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
//         if (data.code == "0") {
//             var msg = JSON.parse(data.message);
//                 msg = JSON.parse(msg.list);
//             var list = msg.list;
//             if(msg.hasNextPage==true){
//                 user_nextpage=true;
//                 user_num++;
//             }else {
//                 user_nextpage=false;
//             }
//             for (var i = 0; i < list.length; i++) {
//                 if(list[i].user_code == $("#PARAM_NAME").attr("data-code")){
//                     $("#allUser_list").append("<li class='groupUser_checked' id='" + list[i].user_code + "'>" + list[i].user_name + "</li>");
//                 }else {
//                     $("#allUser_list").append("<li id='" + list[i].user_code + "'>" + list[i].user_name + "</li>");
//                 }
//             }
//         } else if (data.code == "-1") {
//             console.log(data.message);
//         }
//     })
// }
//验证编号是不是唯一
//$("#vip_num").blur(function () {
//    // var isCode=/^[A]{1}[0-9]{4}$/;
//    var _params = {};
//    var vip_group_code = $(this).val();
//    var vip_group_code1 = $(this).attr("data-name");
//    var corp_code = $("#OWN_CORP").val();
//    if (vip_group_code !== "" && vip_group_code !== vip_group_code1) {
//        _params["vip_group_code"] = vip_group_code;
//        _params["corp_code"] = corp_code;
//        var div = $(this).next('.hint').children();
//        oc.postRequire("post", "/vipGroup/vipGroupCodeExist", "", _params, function (data) {
//            if (data.code == "0") {
//                div.html("");
//                $("#vip_num").attr("data-mark", "Y");
//            } else if (data.code == "-1") {
//                $("#vip_num").attr("data-mark", "N");
//                div.addClass("error_tips");
//                div.html("该编号已经存在！");
//            }
//        })
//    }
//});
//验证名称是否唯一
 $("#vip_id").blur(function () {
     var corp_code = $("#OWN_CORP").val();
     var vip_id = $("#vip_id").val();
     var vip_id1 = $("#vip_id").attr("data-name");
     var div = $(this).next('.hint').children();
     if (vip_id !== "" && vip_id !== vip_id1) {
         var _params = {};
         _params["vip_group_name"] = vip_id;
         _params["corp_code"] = corp_code;
         oc.postRequire("post", "/vipGroup/vipGroupNameExist", "", _params, function (data) {
             if (data.code == "0") {
                 div.html("");
                 $("#vip_id").attr("data-mark", "Y");
             } else if (data.code == "-1") {
                 div.html("该名称已经存在！");
                 div.addClass("error_tips");
                 $("#vip_id").attr("data-mark", "N");
             }
         })
     }
 });
//$(".areaadd_oper_btn ul li:nth-of-type(2)").click(function () {
//    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
//});
$("#edit_close").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
$("#back_vip_group").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
$("#back_vip_group_1").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
/***************************************************************************************/
//新增分组会员
//绑定单击事件
//新增
$('#screen_add').on('click', function () {
    //如果是新增就退出，不调用接口
    if($(this).hasClass('vip_group_add'))return;
    var a = $('#vip_num').val();
    var b = $('#vip_id').val();
    if ($('#vip_num').val() == '') {
        $('#vip_num').next().find('div').addClass('error_tips').html('分组编号不能为空！');
        return
    }
    if ($('#vip_id').val() == '') {
        $('#vip_id').next().find('div').addClass('error_tips').html('分组名称不能为空！');
        return
    }
    var str = $('#screen_add').text().trim();
    str == '分配会员' && ( role = 'eidtor');
    group_code = $("#vip_num").val();
    group_name = $("#vip_id").val();
    group_remark = $("#vip_remark").val();
    corp_code = $('#OWN_CORP').val();
    $(this).attr('data_role') ? role = $(this).attr('data_role') : '';
    GET(1, 10, group_code);//第三个参数  分组编号
    $('#page-wrapper')[0].style.display = 'none';
    $('.content')[0].style.display = 'block';
    $('#group_id').val(a);
    $('#group_name').val(b);
});
//进入页面后保存操作
$("#save").click(function () {
    var param = {};
    var quit = [];//传参数
    var choose = [];//传参数
    var quit_obj = [];//存放放弃的对象
    var get_group_sub = [];//保存已选的参数
    //获取所有选择项
    var get_groups = $("#table tbody input:checked").parent().parent().parent();
    group_count = get_groups.length;
    //遍历现选中的
    for (var i = 0; i < get_groups.length; i++) {
        get_group_sub.push($(get_groups[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    //遍历已选择的与现选中的做对比
    for (var r = 0; r < group_cheked.length; r++) {
        //返回-1  就是没找到要将这个号保存到quit中
        if (get_group_sub.indexOf(group_cheked[r]) == -1) {
            var selector = "#table tbody td[data_vip_id='" + group_cheked[r] + "']";
            quit_obj.push($(selector).parent());
        };
    }
    for (var i = 0; i < quit_obj.length; i++) {
        // var quit_sub = {}
        // quit_sub['vip_id'] = $(quit_obj[i]).find('[data_vip_id]').attr('data_vip_id');
        // quit_sub['corp_code'] = $(quit_obj[i]).attr('id');
        // quit_sub['card_no'] = $(quit_obj[i]).find('[data_cardno]').attr('data_cardno');
        // quit_sub['phone'] = $(quit_obj[i]).find('td:nth-child(4)').html();
        // quit.push(quit_sub)
        quit.push($(quit_obj[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    for (var i = 0; i < get_groups.length; i++) {
        // var choose_sub = {}
        // choose_sub['vip_id'] = $(get_groups[i]).find('[data_vip_id]').attr('data_vip_id');
        // choose_sub['corp_code'] = $(get_groups[i]).attr('id');
        // choose_sub['card_no'] = $(get_groups[i]).find('[data_cardno]').attr('data_cardno');
        // choose_sub['phone'] = $(get_groups[i]).find('td:nth-child(4)').html();
        // choose.push(choose_sub);
        choose.push($(get_groups[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    // sessionStorage.setItem('vip_choose',choose);
    // sessionStorage.setItem('vip_quit',quit);
    //去重
    for(var r=choose.length-1;r>=0;r--){
        for(var i=0;i<group_cheked.length;i++){
            choose[r]==group_cheked[i]?choose.splice(r,1):'';
        }
    }
    param['vip_group_id'] =sessionStorage.getItem("id");
    param['choose'] = choose.toString();
    param['vip_group_code'] = group_code;//分组编号
    param['vip_group_name'] = group_name;//分组名称
    param['vip_group_remark'] = group_remark;//备注
    param['quit'] = quit.toString();
    //发起异步请求
    oc.postRequire("post", '/vipGroup/saveVips', "", param, function (data) {
        if (data.code == 0) {
            var msg=JSON.parse(data.message);
            $('#vip_num').val(group_code);
            $('#vip_id').val(group_name);
            $('#vip_remark').val(group_remark);
            $('#group_recode').val('共' +msg.vip_count+ "个会员");
            // if (role == 'eidtor') {
            //     // window.location.reload()
            // } else {
            //     $('#group_recode').val('共' + group_count + "个会员");
            //     $('#page-wrapper')[0].style.display = 'block';
            //     $('.content')[0].style.display = 'none';
            // }
        } else if (data.code == -1) {
            alert(data.message);
        }
    });
    //关闭当前页面
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//进入页面的关闭操作
$('#turnoff').on('click', function () {
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//返回编辑会员分组
$('#back_edit_vip_group').on('click', function () {
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//打开筛选界面
$('#filtrate').on('click', function () {
    $('#screen_wrapper')[0].style.display = 'block';
});
//关闭筛选界面
$('#screen_wrapper_close').on('click', function () {
    whir.loading.remove("mask");
    $('#screen_wrapper')[0].style.display = 'none';
});
//请求页面数据
function GET(a, b, c) {
    var role_add=arguments[3];
    whir.loading.add("", 0.5);//加载等待框
    var user_code =$("#PARAM_NAME").attr("data-code");
    var vip_group_code = $("#vip_num").val();
    if(user_code == undefined){
        user_code = "";
    }
    var param = {};
    arguments[3]=='search'&&(param['searchGroupVip']='');
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["corp_code"] = corp_code;
    // param["user_code"] = user_code;
    param["vip_group_id"] =sessionStorage.getItem("id");
    oc.postRequire("post", "/vipGroup/allVip ", "", param, function (data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            //var list=list.list;
            superaddition(list, a, c);
            jumpBianse();
            filtrate = "";
            $(".table p").remove();
            setPage($("#foot-num")[0], cout, a, b, c,role_add);
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    })
}
function superaddition(data, num, c) {
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:50%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    group_cheked = [];
    var judge = '';
    var tr_node='';
    var wx='';
    for (var i = 0; i < data.length; i++) {
        if ( data[i].is_this_group == "Y") {
            judge = 'checked'
        } else {
            judge = '';
        }
        if (num >= 2) {
            var a = i + 1 + (num - 1) * pageSize;
        } else {
            var a = i + 1;
        }
        if(data[i].open_id){
            wx="<span class='icon-ishop_6-22' style='color:#8ec750'></span>";
        }else{
            wx="<span class='icon-ishop_6-22' style='color:#cdcdcd'></span>";
        }
        var gender = data[i].sex == 'F' ? '女' : '男';
        var tr_vip_id = data[i].vip_id;
         tr_node += "<tr data-storeid='" + data[i].store_id + "' id='" + data[i].corp_code + "'>"
            + "</td><td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td data_vip_id='" + tr_vip_id + "'>"
            + data[i].vip_name
             + "</td><td>"
             + wx
            + "</td><td>"
            + gender
            + "</td><td>"
            + data[i].vip_phone
            // + "</td><td data_cardno='" + data[i].cardno + "'>"
            + "</td><td>"
            + data[i].cardno
            + "</td><td>"
            + data[i].vip_card_type
            // + "</td><td>"
            // + data[i].vip_group_name
            + "</td><td>"
            + data[i].user_name
            + "</td><td>"
            + data[i].store_name
            + "</td><td width='50px;' style='text-align: left;'><div class='checkbox1' id='" + data[i].id + "'><input " + judge + " type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div></td></tr>";
        if (judge) {
            group_cheked.push(tr_vip_id);
        }
    }
    $(".table tbody").append(tr_node);
    $("tbody tr").click(function () {
        var input = $(this).find("input")[0];
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            input.checked = false;
        }
    })
    $(".th th:last-child input").removeAttr("checked");
};
//生成分页
function setPage(container, count, pageindex, pageSize, c,role_add) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize = pageSize;
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
        } else if (pageindex >= count - 3) {
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
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function () {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function () { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx, pageSize, c,role_add);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        };
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize, c,role_add);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize, c,role_add);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
//点击页码
function dian(a, b, c,role_add) {//点击分页的时候调什么接口
    if (value == "" && filtrate == "") {
        GET(a, b, c,role_add);
    } else if (value !== "") {
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a, b);
    } else if (filtrate !== "") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a, b);
    }
}
//全选
function checkAll(name) {
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = true;
        }
    }
}

//取消全选
function clearAll(name) {
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = false;
        }
    }
}
//模仿select
$(function () {
        $("#page_row").click(function () {
            if ("block" == $("#liebiao").css("display")) {
                hideLi();
            } else {
                showLi();
            }
        });
        $("#liebiao li").each(function (i, v) {
            $(this).click(function () {
                pageSize = $(this).attr('id');
                if (value == "" && filtrate == "") {
                    inx = 1;
                    GET(inx, pageSize,'',str);
                } else if (value !== "") {
                    inx = 1;
                    param["pageSize"] = pageSize;
                    param["pageNumber"] = inx;
                    POST(inx, pageSize);
                } else if (filtrate !== "") {
                    inx = 1;
                    _param["pageNumber"] = inx;
                    _param["pageSize"] = pageSize;
                    filtrates(inx, pageSize);
                }
                $("#page_row").val($(this).html());
                hideLi();
            });
        });
        $("#page_row").blur(function () {
            setTimeout(hideLi, 200);
        });
    }
);
function showLi() {
    $("#liebiao").show();
}
function hideLi() {
    $("#liebiao").hide();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function () {
    var event = window.event || arguments[0];
    var inx = this.value.replace(/[^0-9]/g, '');
    var inx = parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    ;
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "" && filtrate == "") {
                GET(inx, pageSize,'',str);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                param["pageNumber"] = inx;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["pageSize"] = pageSize;
                _param["pageNumber"] = inx;
                filtrates(inx, pageSize);
            }
        }
        ;
    }
})
//筛选
var area_num = 1;
var area_next = false;
var shop_num = 1;
var shop_next = false;
var staff_num = 1;
var staff_next = false;
var bd=[];//品牌
var ar=[];//区域
var sp=[];//店铺
var sf=[];//员工
var cla=[];//品类
var br=[];//品类
var sea=[];//季节
var str='';//调用allVip接口的区分
var brand_value=[];

$("#vip_card_type_add").click(function () {
    vipCardtypeAdd();
    $("#card_type_select_add").show();
});
//卡类型
function vipCardtypeAdd() {
    var corp_code=$("#OWN_CORP").val();
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
            $("#card_type_select_add").empty();
            $("#card_type_select_add").append(li);
        }
    });
}
$("#card_type_select_add").on("click","li",function () {
    $("#vip_card_type_add").val($(this).html());
    $("#vip_card_type_add").attr("data-code",$(this).attr("data-code"));
});

//点击筛选会员的所属品牌
$("#screen_group_icon_add").click(function () {

    clickMark="";
    if(message.cache.group_codes!==""){
        var group_codes=message.cache.group_codes.split(',');
        var group_names=message.cache.group_names.split(',');
        var group_html_right="";
        for(var h=0;h<group_codes.length;h++){
            group_html_right+="<li id='"+group_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+group_codes[h]+"'  data-storename='"+group_codes[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+group_names[h]+"</span>\
            \</li>"
        }
        $("#screen_group .s_pitch span").html(h);
        $("#screen_group .screen_content_r ul").html(group_html_right);
    }else{
        $("#screen_group .s_pitch span").html("0");
        $("#screen_group .screen_content_r ul").empty();
    }
    var arr = whir.loading.getPageSize();
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").hide();
    $("#screen_group").css("position","fixed");
    $("#screen_group").show();
    $("#screen_group .screen_content_l ul").empty();
    getGroupAdd();

    //nodeSave($('#screen_brand .screen_content_r ul li'),$('#screen_brand .screen_content_l ul li'),bd, $('#screen_brand .screen_content_r ul'));
    //$('#screen_group .s_pitch span').html($('#screen_group .screen_content_r ul li').length);
    //var arr = whir.loading.getPageSize();
    //var left = (arr[0] - $("#screen_area").width()) / 2;
    //var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    //$("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    //$("#p").show();
    //$("#screen_wrapper").hide();
    //$("#screen_group").css("position","fixed");
    //$("#screen_group").show();
    ////if ($("#screen_brand .screen_content_l ul li").length > 1) {
    ////    return;
    ////}
    //$("#screen_group .screen_content_l ul").empty();
    //getGroupAdd();
});
//获取分组
function getGroupAdd() {
    var corp_command = "/vipGroup/getCorpGroups";
    var _param = {};
    _param["corp_code"] = $("#OWN_CORP").val();
    _param["search_value"] = $("#group_search").val().trim();
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#screen_group .screen_content_l ul").empty();
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

$("#class span:last-child,#class input").click(function(){
    //$('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    whir.loading.add("mask");
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_class").css("position","fixed");
    $("#screen_class").show();
    $("#screen_class .screen_content_l ul").empty();
    $("#screen_class .screen_content_r ul").empty();
    $("#screen_class .input_s .s_pitch span").html(cla.length);
    var class_num = 1;
    //getstafflist(staff_num)
     getClassSeasonList(class_num,"class");

});
$("#brand span:last-child,#brand input").click(function(){
    //nodeSave($('#screen_brand_add .screen_content_r ul li'),$('#screen_brand_add .screen_content_l ul li'),br, $('#screen_brand_add .screen_content_r ul'));
    //$('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_brand_add").width()) / 2;
    var tp = (arr[3] - $("#screen_brand_add").height()) / 2 + 30;
    whir.loading.add("mask")
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_brand_add").css("position","fixed");
    $("#screen_brand_add").show();
    $("#screen_brand_add .screen_content_l ul").empty();
    $("#screen_brand_add .screen_content_r ul").empty();
    $("#screen_brand_add .input_s .s_pitch span").html(br.length);
    var brand_num = 1;
    getBrandList(brand_num)
});
$("#season span:last-child,#season input").click(function(){
    //$('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_season").width()) / 2;
    var tp = (arr[3] - $("#screen_season").height()) / 2 + 30;
    whir.loading.add("mask")
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_season").css("position","fixed");
    $("#screen_season").show();
    $("#screen_season .screen_content_l ul").empty();
    $("#screen_season .screen_content_r ul").empty();
    $("#screen_season .input_s .s_pitch span").html(sea.length);
    var season_num = 1;
    //getstafflist(staff_num)
    getClassSeasonList(season_num,"quarter")
});
$("#screen_close_class").click(function(){
    $("#screen_class").hide();
    whir.loading.remove("mask");
});
$("#screen_que_class").click(function(){
    cla=[];
    var li = $("#screen_class .screen_content_r input[type='checkbox']").parents("li");
    var class_codes = "";
    var class_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            class_codes += r + ",";
            class_name += p + ",";
        } else {
            class_codes += r;
            class_name += p;
        }
    }
    var num = $("#screen_class .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_class .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        cla.push(this.id);
    });
    $("#class input").val("已选"+num+"个");
    $("#class input").attr("data-code",class_codes);
    $("#class input").attr("data-name",class_name);
    $("#screen_class").hide();
    whir.loading.remove("mask");
});

$("#screen_close_brand_add").click(function(){
    $("#screen_brand_add").hide();
    whir.loading.remove("mask");
});
$("#screen_que_brand_add").click(function(){
    br=[];
    var li = $("#screen_brand_add .screen_content_r input[type='checkbox']").parents("li");
    brand_value = [];
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        brand_value.push({"brand_code":r,"brand_name":p})
    }
    var num = $("#screen_brand_add .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_brand_add .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        br.push(this.id);
    });
    $("#brand input").val("已选"+num+"个");
    $("#screen_brand_add").hide();
    whir.loading.remove("mask");
});
$("#screen_close_season").click(function(){
    $("#screen_season").hide();
    whir.loading.remove("mask");
});
$("#screen_que_season_add").click(function(){
    sea=[];
    var li = $("#screen_season .screen_content_r input[type='checkbox']").parents("li");
    var season_codes = "";
    var season_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            season_codes += r + ",";
            season_name += p + ",";
        } else {
            season_codes += r;
            season_name += p;
        }
    }
    var num = $("#screen_season .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_season .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        sea.push(this.id);
    });
    $("#season input").val("已选"+num+"个");
    $("#season input").attr("data-code",season_codes);
    $("#season input").attr("data-name",season_name);
    $("#screen_season").hide();
    whir.loading.remove("mask");
});
//节点状态保存
function nodeSave(node_r,node_l,arr,node_container) {
       if($(node_r).length!=0) return;
        var html_right="";
        arr.reverse();
        for(var i=0;i<arr.length;i++){
            if(typeof (arr[i])=="string"){
                html_right+="<li id='"+arr[i]+"'>"
                            +"<div class='checkbox1'><input type='checkbox' value='"+arr[i]+"' name='test' class='check'>"
                            +"<label></label></div><span class='p16'>"+arr[i]+"</span>"
                            +"</li>"

            }else if(typeof (arr[i])=="object"){
                html_right+="<li id='"+arr[i]["brand_code"]+"'>"
                            +"<div class='checkbox1'><input type='checkbox' value='"+arr[i]["brand_name"]+"' name='test' class='check'>"
                            +"<label></label></div><span class='p16'>"+arr[i]["brand_name"]+"</span>"
                            +"</li>"
            }
        }
    $(node_container).append(html_right);
}

function searchBianSe(){ //搜索之后变色
    $(".screen_content_l li:visible:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:visible:even").css("backgroundColor","#ededed");
}
function bianse() {
    $(".screen_content_l li:odd").css("backgroundColor", "#fff");
    $(".screen_content_l li:even").css("backgroundColor", "#ededed");
    $(".screen_content_r li:odd").css("backgroundColor", "#fff");
    $(".screen_content_r li:even").css("backgroundColor", "#ededed");
}

//筛选调接口
function filtrates(a, b, c) {
    _param["vip_group_id"] =sessionStorage.getItem("id");
    whir.loading.add("", 0.5);//加载等待框
    // oc.postRequire("post", "/vip/vipScreen", "", _param, function (data) {
    oc.postRequire("post", "/vipGroup/vipScreen", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                // $(".foot ul").empty();
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, a, c);
                jumpBianse();
                setPage($("#foot-num")[0], cout, a, b, c);
                whir.loading.remove();//移除加载框
            }
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function () {
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    if (event.keyCode == 13) {
        value=this.value.trim();
        inx = 1;
        if(value!==""){
            param["searchValue"] = value;
            param["pageNumber"] = inx;
            param["pageSize"] = pageSize;
            POST(inx, pageSize, group_code);
        }else{
            GET(inx, pageSize, group_code);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().replace(/\s+/g, "");
    if (value !== "") {
        inx = 1;
        param["searchValue"] = value;
        param["pageNumber"] = inx;
        param["pageSize"] = pageSize;
        POST(inx, pageSize, group_code);
    } else {
        GET(inx, pageSize, group_code);
    }
})
//搜索的请求函数
function POST(a, b, c) {
    param["corp_code"] = corp_code;
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vip/vipSearch", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, a, c);
                jumpBianse();
                whir.loading.remove();//移除加载框
            }
            filtrate = "";
            setPage($("#foot-num")[0], cout, a, b, c);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//$('#staff_area').click(function () {
//    $('#screen_staff').hide();
//    $('#screen_areal').trigger('click');
//    clickMark="user";
//});
//$('#staff_brand').click(function () {
//    $('#screen_staff').hide();
//    if(message.cache.brand_codes!==""){
    //    var brand_codes=message.cache.brand_codes.split(',');
    //    var brand_names=message.cache.brand_names.split(',');
    //    var brand_html_right="";
    //    for(var h=0;h<brand_codes.length;h++){
    //        brand_html_right+="<li id='"+brand_codes[h]+"'>\
    //        <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
    //        <label></div><span class='p16'>"+brand_names[h]+"</span>\
    //        \</li>"
    //    }
    //    $("#screen_brand .s_pitch span").html(h);
    //    $("#screen_brand .screen_content_r ul").html(brand_html_right);
    //}else{
    //    $("#screen_brand .s_pitch span").html("0");
    //    $("#screen_brand .screen_content_r ul").empty();
    //}
    //$('#screen_brandl').trigger('click');
    //clickMark="user";
//});
//$('#staff_shop').click(function () {
//    $('#screen_staff').hide();
//    $('#screen_shopl').trigger('click');
//    clickMark="user"
//});
//点击搜索按钮提示与否
$('.r_filrate').hover(function () {
    $(this).next().show();
},function () {
    $(this).next().hide();
});
//点击搜索按钮
$('.r_filrate').click(function () {
    //清空搜索内容
     str='';
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $(".sxk").slideUp();
    $("#search").val('');
    if($(this).next().text().trim()=='显示当前分组会员'){
        $(this).next().html('显示全部会员');
        $(this).attr('style','color:#50a3aa');
        str='search';
    }else{
        $(this).next().html('显示当前分组会员');
        $(this).attr('style','color:#fff');
        str=''
    }
    GET(1,10,group_code,str);
});
$("#group_type").next("ul").find("li").click(function(){
    var Val=$(this).text();
    var type=$(this).attr("data-type");
    switch (type){
        case "define":
            $("#group_type").css("width","420px");
            $("#group_type").next("ul").css("width","420px");
            $("#type").hide();
            $("#group_list").hide();
            $("#custom_vip_list").show();
            break;
        case "class":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#class").show();
            break;
        case "brand":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            var LI=$("<li data-type='all'>累计消费</li><li data-type='12'>近12个月消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#brand").show();
            break;
        case "discount":
            $(".select_more").hide();
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            var LI=$(
                "<li data-type='3'>近3月</li>" +
                "<li data-type='6'>近6月</li>" +
                "<li data-type='12'>近12月</li>" +
                "<li data-type='15'>近15月</li>" +
                "<li data-type='18'>近18月</li>" +
                "<li data-type='21'>近21月</li>" +
                "<li data-type='24'>近24月</li>" +
                "<li data-type='all'>累计消费</li>"
                );
            $("#type").next("ul").html(LI);
            $("#type").val("近3月");
            $("#type").attr("data-type","3");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().hide();
            $("#group_list").children().last().show();
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            break;
        case "season":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").show();
            $("#type").attr("data-type","all");
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().last().hide();
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#season").show();
            $("#group_list>div:last-child").css("margin","0");
            break;
    }
    $(this).parent().prev("input").val(Val);
    $(this).parent().prev("input").attr("data-type",type);
    $(this).parent().hide();
});
$("#type").next("ul").on("click","li",function(){
    var Val=$(this).text();
    var type=$(this).attr("data-type");
    $(this).parent().prev("input").val(Val);
    $(this).parent().prev("input").attr("data-type",type);
    $(this).parent().hide();
});
$("#type").click(function(){
    $(this).next("ul").is(":hidden")==true?$(this).next("ul").show():$(this).next("ul").hide();
});
$("#type").blur(function(){
    var self=$(this);
    setTimeout(function(){
        self.next("ul").hide();
    },200)
});
$("#group_type").blur(function(){
    var self=$(this);
    setTimeout(function(){
        self.next("ul").hide();
    },200)
});

//清空筛选
$("#empty_filter_define").click(function () {
    $("#screen_wrapper .contion_input input").each(function () {
        var key = $(this).attr("data-kye");
        if (key == "6" || key == "8" || key == "12" || key=="18") {
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
            return;
        } else {
            $(this).val("");
        }
    });
    $("#screen_wrapper textarea").each(function () {
        $(this).val("");
    });
    all_select_vip_list=[];
    message.cache.staff_codes="";
    message.cache.brand_codes="";
    message.cache.area_codes="";
    message.cache.store_codes="";
    message.cache.group_codes="";
    $("#area_num").val("全部");
    $("#area_num").attr("data-brandcode","");
    $("#brand_num").val("全部");
    $("#brand_num").attr("data-brandcode","");
    $("#staff_area_num").val("全部");
    $("#staff_area_num").attr("data-areacode","");
    $("#staff_brand_num").val("全部");
    $("#staff_brand_num").attr("data-brandcode","");
    $("#staff_shop_num").val("全部");
    $("#staff_shop_num").attr("data-storecode","");
    clearSelectList()
});
function getSelectDataDefine(){
    var content = $(".select_box .select_box_content:visible").attr("id");
    var screen = [];
    var group = $("#" + content).find(".select_box_content_center_box:visible").children(":visible");
    var SelectDataDef=$.Deferred();
    select_type=content;
    group.map(function () {
        var type = $(this).attr("data-type");
        var key = $(this).attr("data-key");
        var name = $(this).children(".center_item_group_title").html();
        var item_line = $(this).find(".item_list .item_line");
        var opera = "";

        function getLogic(logic_default) {
            switch (logic_default) {
                case "包含":
                    return "contains";
                    break;
                case "不包含":
                    return "not contains";
                    break;
                case "等于":
                    return "equal";
                    break;
                case "不等于":
                    return "not equal";
                    break;
                case "为空":
                    return "null";
                    break;
                case "不为空":
                    return "not null";
                    break;
                case "大于":
                    return "{";
                    break;
                case "小于":
                    return "}";
                    break;
                case "大于等于":
                    return "[";
                    break;
                case "小于等于":
                    return "]";
                    break;
            }
        }

        if (type == "affiliation") {
            var value = "";
            var dataName = "";
            item_line.map(function () {
                value = $(this).find("input").attr("data-code");
                name = $(this).find(".center_item_group_title").html();
                dataName = $(this).find("input").attr("data-name");
                key = $(this).attr("data-key");
                var logic_default = $(this).find(".imitate_select_wrap").eq(0).find(".imitate_select span").html();
                var logic = getLogic(logic_default);
                var obj = {"key":key,"type": "text"};
                obj.type = "text";
                obj.name = name;
                obj.logic = logic;
                obj.value = value;
                obj.dataName = dataName;
                obj.style = "new";
                if (obj.value.length != 0) {
                    screen.push(obj)
                }
            });
        } else if (type == "affiliation_other") {
            var value = item_line.find("input").attr("data-code");
            var dataName = item_line.find("input").attr("data-name");
            var obj={};
            if(key=="user_code"){
                var logic_default = item_line.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html();
                var logic = getLogic(logic_default);
                 obj = {"key": key, "value": value,"logic":logic,"name":name,"type": "text","dataName":dataName,"style":"new"};
            }else{
                 obj = {"key": key, "value": value, "name":name,"type": "text","dataName":dataName,"style":"new"};
            }
            if (obj.value.length != 0) {
                screen.push(obj)
            }
        } else {
            switch (type) {
                case "text":
                    var obj={"key":key};
                    if (key == "ZONE") {
                        item_line.map(function () {
                            var span = $(this).find(".select_city_group .imitate_select_wrap .imitate_select span");
                            var value="";
                            if (span.is(":visible")) {
                                //var value = (span.eq(0).html()=="省"?"":span.eq(0).html()) + (span.eq(1).html()=="市"?"":"-"+span.eq(1).html()) + (span.eq(2).html()=="区"?"":"-" +span.eq(2).html());
                                 value = span.eq(0).html()+"-"+span.eq(1).html()+"-"+span.eq(2).html();
                                if(value=="省-市-区"){
                                    value="";
                                }
                            } else {
                                   value = "";
                            }
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            obj.logic=logic;
                            obj.value=value;
                        });
                    } else if(key=="APP_ID"){
                        item_line.map(function () {
                            var value = $(this).find("input:text").attr("data-code");
                            var dataName= $(this).find("input:text").attr("data-name");
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            //obj.value.push({"opera": opera, "logic": logic, "value": value,"name":name})
                            obj.logic=logic;
                            obj.value=value;
                            obj.dataName=dataName;
                        })
                    }else if(key=="CARD_TYPE_ID"){
                        item_line.map(function () {
                            var value = $(this).find("input:text").attr("data-name");
                            var code = $(this).find("input:text").attr("data-code");
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            obj.logic=logic;
                            obj.value=value;
                            obj.code=code;
                        })
                    }else{
                        item_line.map(function () {
                            if ($(this).find("textarea").length > 0) {
                                if ($(this).find("textarea").is(":visible")) {
                                    var value = $(this).find("textarea").val().trim();
                                } else {
                                    var value = ""
                                }
                            } else {
                                if ($(this).find("input:text").is(":visible")) {
                                    var value = $(this).find("input:text").val().trim();
                                } else {
                                    var value = ""
                                }
                            }
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            //obj.value.push({"opera": opera, "logic": logic, "value": value})
                            obj.logic=logic;
                            obj.value=value;
                        });
                    }
                    obj.type = "text";
                    obj.name = name;
                    obj.style = "new";
                    if(obj.value!="" || (obj.value=="" && (obj.logic=="null" || obj.logic=="not null"))){
                        screen.push(obj);
                    }
                    break;
                case "interval":
                    if (key == "TRADE" || key == "COUPON") {
                        var selectType=$(this).attr("class").replace(/center_item_group select_/,"");
                        var key_second_I="";
                        item_line= $(this).find(".item_list .item_line .select_"+selectType+"_right_line+div:visible").parents(".item_line");
                        var len=item_line.length-1;
                        for (var s=len;s>=0;s--){
                            var logic_1 = $(item_line[s]).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic_2 = $(item_line[s]).find(".imitate_select_wrap").eq(3).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            var typeName=$(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var scondeName=$(item_line[s]).find(".center_item_group_title").html();
                            var obj = {};
                            if (s!= len) {
                                opera = $(item_line[s]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[s]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[s]).find("input").eq(1).val().trim();
                            if (key == "COUPON") {
                                 typeName=$(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
                                 start_val = $(item_line[s]).find("input").eq(1).val().trim();
                                 end_val = $(item_line[s]).find("input").eq(2).val().trim();
                                var val = $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type").trim();
                                if (val != "") {
                                    key_second_I = "VOUTYPE_" + val + "_" + $(item_line[s]).attr("data-key");
                                } else {
                                    key_second_I = "VOUTYPE_" + $(item_line[s]).attr("data-key");
                                }
                            } else {
                                if($(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").is(":hidden")){
                                    key_second_I = $(item_line[s]).attr("data-key");
                                }else{
                                    key_second_I = $(item_line[s]).attr("data-key") + "_" + $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-type");
                                }
                            }
                            obj.logic1 = logic1;
                            obj.logic2 = logic2;
                            obj.start = start_val;
                            obj.end = end_val;
                            obj.key = key;
                            obj.key_v2 = key_second_I;
                            obj.type = "interval";
                            obj.name = name;
                            obj.typeName = typeName;
                            obj.scondeName = scondeName;
                            obj.style = "new";
                            if(start_val!="" || end_val!=""){
                                screen.push(obj)
                            }
                        }
                    } else if(key=="CATA1_PRD" || key=="CATA2_PRD" || key=="CATA3_PRD"|| key=="CATA4_PRD" || key=="SEASON_PRD" || key=="T_BL_M" || key=="DAYOFWEEK" || key=="BRAND_ID" || key=="CATA3_PRD" || key=="STORE_AREA" || key=="PRICE_CODE"){
                        item_line= $(this).find(".item_list .item_line .select_dimension+div:visible").parents(".item_line");
                        var len=item_line.length-1;
                        for(T=len;T>=0;T--){
                            var logic_1 = $(item_line[T]).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic_2 = $(item_line[T]).find(".imitate_select_wrap").eq(3).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            var key_second_I=$(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-value");
                            var typeName=$(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var obj = {};
                            if ($(item_line[T]).index() != 0) {
                                opera = $(item_line[T]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[T]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[T]).find("input").eq(1).val().trim();
                            obj.logic1 = logic1;
                            obj.logic2 = logic2;
                            obj.start = start_val;
                            obj.end = end_val;
                            obj.key = key;
                            obj.key_v2 = key_second_I;
                            obj.name = name;
                            obj.typeName = typeName;
                            obj.type = "interval";
                            obj.style = "new";
                            if(start_val!="" || end_val!=""){
                                screen.push(obj)
                            }
                        }
                    }else{
                        item_line.map(function () {
                            var logic_1 = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic_2 = $(this).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(this).find("input").eq(0).val().trim();
                            var end_val = $(this).find("input").eq(1).val().trim();
                            var obj = {};
                            obj.logic1 = logic1;
                            obj.logic2 = logic2;
                            obj.start = start_val;
                            obj.end = end_val;
                            obj.key = key;
                            obj.name = name;
                            obj.type = "interval";
                            obj.style = "new";
                            if(start_val!="" || end_val!=""){
                                screen.push(obj)
                            }
                        });
                    }
                    break;
                case "radio":
                    var obj={"key":key,"type":"radio","name":name};
                    if (key == "ACTIVITY" || key == "TASK") {
                        var selectTypeR=$(this).attr("class").replace(/center_item_group select_/,"");
                        item_line= $(this).find(".item_list .item_line .select_"+selectTypeR+"_right_line+div:visible").parents(".item_line");
                        var len_r=item_line.length-1;
                        for (var T=len_r;T>=0;T--){
                            if (T!= len_r) {
                                opera = $(item_line[T]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            if ($(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type").trim() != "") {
                                var key_second = key + "_" + $(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type");
                                var value = $(item_line[T]).find("input:checked").val();
                                var dataName=$(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
                                //obj.value.push({"opera": opera, "logic": "", "value": value, key: key_second})
                                obj.logic="";
                                obj.value=value;
                                obj.key_v2=key_second;
                                obj.dataName=dataName;
                                obj.style = "new";
                                screen.push(obj);
                            }
                        }
                    } else if (key == "SEX_VIP") {
                        item_line.map(function () {
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic);
                            var value = $(this).find("input:checked").val();
                            var dataName=$(this).find(".select_radio_group .checkbox input:checked").parent().next().html();
                            //obj.value.push({"opera": opera, "logic": logic, "value": value})
                            obj.logic=logic;
                            obj.value=value;
                            obj.dataName=dataName;
                            //obj.key_v2=key_second;
                            obj.style = "new";
                            screen.push(obj);
                        });
                    } else {
                        item_line.map(function () {
                            var dataName=$(this).find(".select_radio_group .checkbox input:checked").parent().next().html();
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var value = $(this).find("input:checked").val();
                            //obj.value.push({"opera": opera, "logic": "", "value": value})
                            obj.logic="";
                            obj.value=value;
                            obj.dataName=dataName;
                            obj.style = "new";
                            screen.push(obj);
                        });
                    }
                    break
            }
        }
    });
    SelectDataDef.resolve(screen);
    return SelectDataDef
}
$("#select_sure_operate").click(function () {
    getSelectDataDefine().then(function(screenData){
        _param['screen'] = screenData;
        $(".select_box_wrap").hide();
        $(".select_box_content_left .item_group").find("ul").slideUp().next(".group_arrow").removeClass("item_group_active");
        all_select_vip_list=screenData;
        showSelect()
    })
});
function showSelect(){
    var html="";
    for(var b=0;b<all_select_vip_list.length;b++){
        var ob=all_select_vip_list[b];
        if(all_select_vip_list[b].type=="interval"){
            var logic1=getLogicDefault(all_select_vip_list[b].logic1);
            var logic2=getLogicDefault(all_select_vip_list[b].logic2);
            if(all_select_vip_list[b].key=="TRADE"){
                var head="";
                var foot="";
                var key_v2=all_select_vip_list[b].key_v2;
                var time=key_v2.slice(key_v2.lastIndexOf("_")+1);
                if(time!="all" && isNaN(time)){
                    switch (key_v2){
                        case "NUM_TRADE":
                            head="消费次数";
                            foot="次";
                            break;
                        case "AMT_TRADE":
                            head="消费金额";
                            foot="元";
                            break;
                        case "NUM_TRADE_R":
                            head="退款次数";
                            foot="次";
                            break;
                        case "AMT_TRADE_R":
                            head="退款金额";
                            foot="元";
                            break
                    }
                    html+='<div class="condition"' +
                        'data-value=\''+JSON.stringify(ob)+'\'>'+
                        '<span class="condition_title" title="'+all_select_vip_list[b].scondeName+'">'+all_select_vip_list[b].scondeName+'</span>' +
                        '<span class="condition_detail" title="'+head+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+foot+'">'+head+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+foot+'</span>'+
                        '<span class="icon-ishop_6-29"></span>' +
                        '</div>'
                }else{
                    var dataKey=key_v2.slice(0,key_v2.lastIndexOf("_"));
                    switch (dataKey){
                        case "NUM_TRADE":
                            head="消费次数";
                            foot="次";
                            break;
                        case "AMT_TRADE":
                            head="消费金额";
                            foot="元";
                            break;
                        case "NUM_TRADE_R":
                            head="退款次数";
                            foot="次";
                            break;
                        case "AMT_TRADE_R":
                            head="退款金额";
                            foot="元";
                            break
                    }
                    var timeHtml="";
                    if(time=="all"){
                        timeHtml="累计"
                    }else{
                        timeHtml='最近'+time+'个月';
                    }
                    html+='<div class="condition"' +
                        'data-value=\''+JSON.stringify(ob)+'\'>'+
                        '<span class="condition_title" title="'+all_select_vip_list[b].scondeName+'">'+all_select_vip_list[b].scondeName+'</span>' +
                        '<span class="condition_detail" title="'+timeHtml+head+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+foot+'">'+timeHtml+head+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+foot+'</span>'+
                        '<span class="icon-ishop_6-29"></span>' +
                        '</div>'
                }
            }else  if(all_select_vip_list[b].key=="COUPON"){
                var head="";
                var key_v2=all_select_vip_list[b].key_v2;
                var dataKey=key_v2.slice(key_v2.lastIndexOf("_")+1);
                switch (dataKey){
                    case "TOTAL":
                        head="获得";
                        break;
                    case "VERIFYED":
                        head="使用";
                        break;
                    case "EXPIRE":
                        head="过期";
                        break;
                    case "AVAILABLE":
                        head="可用";
                        break
                }
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+head+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'个">'+head+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'个</span>'+
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else if(all_select_vip_list[b].key=="CATA1_PRD" || all_select_vip_list[b].key=="CATA2_PRD" || all_select_vip_list[b].key=="CATA3_PRD"|| all_select_vip_list[b].key=="CATA4_PRD" || all_select_vip_list[b].key=="SEASON_PRD" || all_select_vip_list[b].key=="T_BL_M" || all_select_vip_list[b].key=="DAYOFWEEK" || all_select_vip_list[b].key=="BRAND_ID" || all_select_vip_list[b].key=="CATA3_PRD" || all_select_vip_list[b].key=="STORE_AREA" || all_select_vip_list[b].key=="PRICE_CODE"){
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'">'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else{
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'">'+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }
        }else if(all_select_vip_list[b].type=="text"){
            var logic=getLogicDefault(all_select_vip_list[b].logic);
            var key=all_select_vip_list[b].key;
            if(key == "brand_code" || key == "area_code" || key == "store_code" || key =="user_code" || key=="VIP_GROUP_CODE" || key=="APP_ID"){
                html+='<div class="condition" ' +
                    'data-value=\''+JSON.stringify(ob)+'\'>' +
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+logic+all_select_vip_list[b]["dataName"]+'">'+logic+all_select_vip_list[b]["dataName"]+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else if(key=="ZONE"){
                var valName=all_select_vip_list[b]["value"].split("-");
                var name="";
                   if(valName[0]!="省"){
                       name+=valName[0];
                   }if(valName[1]!="市"){
                    name+="-"+valName[1];
                   }
                if(valName[2]!="区"){
                    name+="-"+valName[2];
                   }
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+logic+name+'">'+logic+name+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else {
            html+='<div class="condition"' +
                'data-value=\''+JSON.stringify(ob)+'\'>'+
                '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                '<span class="condition_detail" title="'+logic+all_select_vip_list[b]["value"]+'">'+logic+all_select_vip_list[b]["value"]+'</span>' +
                '<span class="icon-ishop_6-29"></span>' +
                '</div>'
            }
        }else if(all_select_vip_list[b].type=="radio"){
            var logic=getLogicDefault(all_select_vip_list[b].logic);
            if(all_select_vip_list[b].key == "ACTIVITY" || all_select_vip_list[b].key == "TASK") {
                var val=all_select_vip_list[b]["value"]=='Y'?"已参与":all_select_vip_list[b]["value"]=='N'?"未参与":"";
                var dataName=all_select_vip_list[b]["dataName"];
                html+='<div class="condition"'+
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+val+dataName+'">'+val+dataName+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else {
                var val=all_select_vip_list[b]["dataName"];
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+logic+val+'">'+logic+val+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }
        }
    }
    $(".is_add_condition").parent(".group_title").parent().append(html);
    if($(".is_add_condition").parent(".group_title").children().eq(0).hasClass("icon-ishop_6-32")){
        $(".is_add_condition").parent(".group_title").siblings().hide();
    }
}
$("#custom_vip_list").on("click","i",function(){
    var name= $(this).parent().children().eq(0).text();
    for(var n=0;n<all_select_vip_list.length;n++){
        if(all_select_vip_list[n].name==name){
            all_select_vip_list.splice(n,1)
        }
    }
    $(this).parent().remove();
});
function showOtherGroup(list,type){
    var list=list;
    switch (type){
        case "class":
            $("#group_type").val("品类喜好分组");
            $("#group_type").attr("data-type","class");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#class").show();
            $("#class input").attr("data-code",list.class);
            $("#class input").attr("data-name",list.class);
            cla=list.class.split(",");
            $("#class input").val("已选"+cla.length+"个");
            $("#screen_class .input_s .s_pitch span").html(cla.length);
            break;
        case "brand":
            $("#group_type").val("品牌喜好分组");
            $("#group_type").attr("data-type","brand");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li><li data-type='12'>近12个月消费</li>");
            $("#type").next("ul").html(LI);
            var type_name=list.circle=="all"?"累计消费":"近12个月消费";
            $("#type").val(type_name);
            $("#type").attr("data-type",list.circle);
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#brand").show();
            var brand=list.brand && list.brand;
            brand_value=brand;
            for(var i=0;i<brand.length;i++){
                br.push(brand[i].brand_code);
            }
            $("#brand input").val("已选"+br.length+"个");
            $("#screen_brand_add .input_s .s_pitch span").html(br.length);
            break;
        case "discount":
            $("#group_type").val("折扣偏好分组");
            $("#group_type").attr("data-type","discount");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$(
                "<li data-type='3'>近3月</li>" +
                "<li data-type='6'>近6月</li>" +
                "<li data-type='12'>近12月</li>" +
                "<li data-type='15'>近15月</li>" +
                "<li data-type='18'>近18月</li>" +
                "<li data-type='21'>近21月</li>" +
                "<li data-type='24'>近24月</li>" +
                "<li data-type='all'>累计消费</li>"
            );
            $("#type").next("ul").html(LI);
            var type_name=list.circle=="all"?"累计消费":"近"+list.circle+"月";
            $("#type").val(type_name);
            $("#type").attr("data-type",list.circle);
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().hide();
            $("#group_list").children().last().show();
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            break;
        case "season":
            $("#group_type").val("季节偏好分组");
            $("#group_type").attr("data-type","season");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").show();
            $("#type").attr("data-type","all");
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().last().hide();
            $("#group_list").children().eq(1).css("margin","0");
            $("#custom_vip_list").hide();
            $(".select_more").hide();
            $("#season").show();
            $("#season input").attr("data-code",list.quarter);
            $("#season input").attr("data-name",list.quarter);
            sea=list.quarter && list.quarter.split(",");
            sea && $("#season input").val("已选"+sea.length+"个");
            $("#screen_season .input_s .s_pitch span").html(sea.length);
            break;
    }
    for(var key in list){
        if(typeof (list[key])=="object" && key!="brand"){
            $("#"+key).show();
            $("#"+key).find("input").eq(0).val(list[key].start);
            $("#"+key).find("input").eq(1).val(list[key].end);
          }
    }
}
    $("#group_list>div").not('.select_more').find("input").blur(function(){
        var reg=/^\d+(\.\d+)?$/;
        if(!reg.test($(this).val().trim()) && $(this).val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "只允许输入数字"
            });
            $(this).val("");
            return;
        }
        if($(this).parent().attr("id")=="consume_discount"){
                if(Number($(this).val())<0|| Number($(this).val())==0 || Number($(this).val())>1){
                    art.dialog({
                        zIndex: 10010,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "折扣范围为0-1"
                    });
                    $(this).val("")
                 }
        }
        if($(this).index()=="1" && Number($(this).val())-Number($(this).siblings("input").val())>0 && $(this).siblings("input").val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "不能大于后面的值"
            });
            $(this).val("")
        }
        if($(this).index()=="3" && Number($(this).val())-Number($(this).siblings("input").val())<0 && $(this).val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "不能小于前面的的值"
            });
            $(this).val("")
        }
        //Number($(this).val())-Number($(this).val())
    });
$("#group_type").click(function(){
    $("#group_all_list").is(":hidden")==true?$("#group_all_list").show():$("#group_all_list").hide();
});
function getClassSeasonList(a,type){
    var type=type;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    var param={
        corp_code:$("#OWN_CORP").val(),
        type:type
    };
    oc.postRequire("post","/vipGroup/getClassQuarter","0",param,function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = type=="class"?message.CATA2_PRD:message.SEASON_PRD;
            var class_html = '';
            if (list.length == 0) {

            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        class_html += "<li id="+list[i]["name"]+"><div class='checkbox1'><input  type='checkbox' value='" + list[i]["name"] + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i]["name"] + "</span></li>"
                    }
                }
            }
            type=type=="class" ? "class" : "season";
            $("#screen_"+type+" .screen_content_l ul").append(class_html);
            if(type=="class"){
                nodeSave('#screen_'+type+' .screen_content_r ul li','#screen_'+type+' .screen_content_l ul li',cla, '#screen_'+type+' .screen_content_r ul');
            }else if(type=="season"){
                typeof sea=='string'&&(sea=[]);//kds 2017/1/23
                nodeSave('#screen_'+type+' .screen_content_r ul li','#screen_'+type+' .screen_content_l ul li',sea, '#screen_'+type+' .screen_content_r ul');
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
function getBrandList(a){
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    var param={
        corp_code:$("#OWN_CORP").val(),
        searchValue:$("#brand_search_add").val().trim()
    };
    oc.postRequire("post","/shop/brand","0",param,function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.brands;
            var brand_html = '';
            if (list.length == 0) {

            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        brand_html += "<li id="+list[i]["brand_code"]+"><div class='checkbox1'><input  type='checkbox' value='" + list[i]["brand_code"] + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i]["brand_name"] + "</span></li>"
                    }
                }
            }
            $("#screen_brand_add .screen_content_l ul").append(brand_html);
            nodeSave('#screen_brand_add .screen_content_r ul li','#screen_brand_add .screen_content_l ul li',brand_value, '#screen_brand_add .screen_content_r ul');
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
$("#class_search").keydown(function(){
    var event=window.event||arguments[0];
    if (event.keyCode == 13) {
        var val=$(this).val();
        $("#screen_class .screen_content_l li").hide();
        $("#screen_class .screen_content_l li:contains('" + val + "')").show();
        searchBianSe();
    }
});
$("#class_search_f").bind("click",function(){
    var val=$(this).prev().val();
    $("#screen_class .screen_content_l li").hide();
    $("#screen_class .screen_content_l li:contains('" + val + "')").show();
    searchBianSe();
});
$("#season_search_add").keydown(function(){
    var event=window.event||arguments[0];
    if (event.keyCode == 13) {
        var val=$(this).val();
        $("#screen_season .screen_content_l li").hide();
        $("#screen_season .screen_content_l li:contains('" + val + "')").show();
        searchBianSe();
    }
});
$("#season_search_f").bind("click",function(){
    var val=$(this).prev().val();
    $("#screen_season .screen_content_l li").hide();
    $("#screen_season .screen_content_l li:contains('" + val + "')").show();
    searchBianSe();
});
$("#brand_search_add").keydown(function(){
    var event=window.event||arguments[0];
    if (event.keyCode == 13) {
        $("#screen_brand_add .screen_content_l ul").empty();
        getBrandList();
    }
});
$("#brand_search_add_f").bind("click",function(){
    $("#screen_brand_add .screen_content_l ul").empty();
    getBrandList()
});
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if (event.keyCode == 13) {
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
$("#brand_search_f").bind("click",function(){
    $("#screen_brand .screen_content_l ul").empty();
        getbrandlist()
});

