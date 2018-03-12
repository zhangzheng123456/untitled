/**
 * Created by hu.x on 2017/4/7.
 */
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
var isscroll=false;
var param = {};//搜索参数
var choose_this_group='';//当前已选择
var all_select_vip_list=[];
var clickMark="";
var select_type=""
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
$(function () {
    window.vip.init();
    if ($(".pre_title label").text() == "编辑积分清零") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        whir.loading.add("", 0.5);
        _params = {};
        _params["id"] = id;
        var _command = "/vipIntegral/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                $("#OWN_CORP option").val(msg.corp_code);
                $('#name').val(msg.integral_name);
                $('#No').val(msg.bill_no);
                var target_vips = msg.target_vips;
                $("#vipCode").attr("data-app_id",msg.app_id);
                $("#vipCode").val(msg.app_name);
                //if(msg.group_type=="define"){
                //    all_select_vip_list=JSON.parse(msg.group_condition);
                //    showSelect();
                //}else{
                //    var list=JSON.parse(msg.group_condition);
                //    showOtherGroup(list,msg.group_type);
                //}
                if(target_vips == '[]'){
                    $('#allVip').show();
                    $('#screenVip').hide();
                    $('#screenVip').next().hide();
                    $('#allVip').click();
                }else{
                    $('#allVip').hide();
                    $('#screenVip').hide();
                    $('#screenVip').next().hide();
                    $('#screenVip').click();
                    all_select_vip_list=JSON.parse(msg.target_vips);
                    showSelect_edit(all_select_vip_list,msg.target_vip_type);
                }
                $('#MESSAGE_TYPE').val(msg.integral_duration);
                var clear_cycle = msg.clear_cycle;
                cache.clear_cycle = clear_cycle;
                countChar(clear_cycle,'*');
                function countChar(str,char){    //str为字符串，char为字符
                    var count=0;
                    for(var i=0;i<str.length;i++){
                        if(str.charAt(i) == char){
                            count++;
                        }
                    }
                    var arr = str.split(' ');
                    if(arr.length == 6 && count == 1){
                       $('#clear_cycle').val('每月 '+arr[3]+'日 '+arr[2]+':'+arr[1]);
                        $('.clear_time p span').eq(0).click();
                        $('.clear_show input').eq(0).val(arr[1]);
                        $('.clear_show input').eq(1).val(arr[0]);

                    }else if(arr.length == 6){
                        $('#clear_cycle').val('每年 '+arr[4]+'月'+arr[3]+'日 '+arr[2]+':'+arr[1]);
                        $('.clear_time p span').eq(1).click();
                        $('.clear_show input').eq(0).val(arr[2]);
                        $('.clear_show input').eq(1).val(arr[1]);
                        $('.clear_show input').eq(2).val(arr[0]);
                    }else if(arr.length == 7){
                        $('#clear_cycle').val(arr[6]+'年'+arr[4]+'月'+arr[3]+'日 '+arr[2]+':'+arr[1]);
                        $('.clear_time p span').eq(2).click();
                        $('.clear_show input').eq(0).val(arr[3]);
                        $('.clear_show input').eq(1).val(arr[2]);
                        $('.clear_show input').eq(2).val(arr[1]);
                        $('.clear_show input').eq(3).val(arr[0]);
                    }
                    return count;
                }
                var remind = JSON.parse(msg.remind);
                var tempHTML = '<div style="position:relative;top:-30px;left: 123px;height: auto" class="once"> <!--内容--> <div class="drop_down item_1" style="float: left;"> <span> <input class="input_select" type="text" placeholder="时间" readonly style="width: 200px;margin-right: 10px;""/> <!--*****--> <div class="clear_time" style="display: none;left: 0px;"> <p> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none;" class="ck_1"> <b class="radio_b"></b> <label class="M" style="width: 40px;margin-left: -10px;margin-right:12px;color: #dbdbdb">每月</label> </span> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none" class="ck_2"> <b class="radio_b"></b> <label class="Y" style="width: 40px;margin-left:-10px;margin-right:12px;color:#dbdbdb;">每年</label> </span> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none" class="ck_3"> <b class="radio_b"></b> <label class="O" style="width: 50px;margin-left: -10px;color:#dbdbdb;">一次性</label> </span> </p> <span class="clear_show"> </span> <btn class="clear_time_btn" >确认</btn> </div> <!--*****--> </span> </div> <div class="drop_down item_1" style="float: left;"> <span> <input class="input_select" type="text" placeholder="微信模板消息" readonly style="width: 130px" id="tempInput" /> <ul style="width:130px; margin-left: 200px;"> <li>微信模板消息</li> <li>短信消息</li> </ul> </span> </div> <span class="add_per_icon remind_edit isWx" style="text-align: center;cursor: pointer;margin-left: 10px;padding-left: 0px;width: 70px">编辑内容</span> <span class="icon_action_add" onclick="add(this)"><span class="icon-ishop_6-01"></span></span></div>'
                if(remind.length>1){
                    for(i=0;i<remind.length-1;i++){
                        $('.once:last').find('.icon_action_add').hide();
                        $('.once:last').after(tempHTML);
                    }
                }
                if(remind.length==1){
                    $(".once .icon_action_delete").remove();
                }
                for(i = 0 ;i<remind.length;i++){
                    var time = remind[i].time;
                    if(time != ''){
                        var count = time.indexOf('*') == '-1' ? 0:1;
                        var arr = time.split(' ');
                        var input = $('.once').eq(i).find('input').eq(0);
                        if(arr.length == 6 && count ==1){
                            input.val('每月 '+arr[3]+'日 '+arr[2]+':'+arr[1]);

                        }else if(arr.length == 6){
                            input.val('每年 '+arr[4]+'月'+arr[3]+'日 '+arr[2]+':'+arr[1]);
                        }else if(arr.length == 7){
                            input.val(arr[6]+'年'+arr[4]+'月'+arr[3]+'日 '+arr[2]+':'+arr[1]);
                        }
                        input.css('background','#dfdfdf');
                        input.next().remove();
                        $('.once').eq(i).find('input').eq(1).css('background','#dfdfdf');
                        $('.once').eq(i).find('input').eq(1).next().remove();
                        input.attr('data-value',time);
                        $('.once').eq(i).find('input').eq(1).val(remind[i].type == 'sms' ? '短信消息':'微信模板消息');
                        $('.once').eq(i).find('.remind_edit').attr('data-vale',remind[i].content);
                        if(remind[i].type == 'sms'){
                            $('.once').eq(i).find('.remind_edit').removeClass("isWx");
                            $('.once').eq(i).find('.remind_edit').attr("onclick","editMsg(this)")
                        }else if(remind[i].type == 'wxTemp'){
                            $('.once').eq(i).find('.remind_edit').removeAttr("onclick");
                        }
                    }else{
                        return
                    }
                }
                $("#message_content").val(msg.remarks);
                //$("#PARAM_NAME").attr("data-code",msg.user_code);
                //$("#PARAM_NAME").val(msg.user_name);
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
                getcorplist(Code);
                setInterval(function () {
                    $('.searchable-select-holder').css('background','#dfdfdf');
                },1)
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
    uploadOSS();
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
        $(".edit_save").click(function () {
            var name = $("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N") {
                    var div = $("#vip_id").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
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
                var integral_name = $('#name').val().trim();
                var integral_duration = $('#MESSAGE_TYPE').val().trim();//积分保留时长
                var clear_cycle = $('#clear_cycle').attr('data-value');
                var clear_type = '';
                if($('#clear_cycle').val().indexOf('每年') != -1){
                    clear_type = 'Y';
                }else if($('#clear_cycle').val().indexOf('每月') != -1){
                    clear_type = 'M';
                }else if($('#clear_cycle').val().indexOf('每年') == -1 && $('#clear_cycle').val().indexOf('每月') == -1){
                    clear_type = 'once';
                }

                    if (integral_name == "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"请输入名称"
                    });
                    return false;
                }
                if (integral_duration =="") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"请选择保留积分时长"
                    });
                    return false;
                }
                if ($('#clear_cycle').val() == ""||$('#clear_cycle').val().trim() == '?') {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"请选择清理周期"
                    });
                    return false;
                }
                if($("#tempInput").val()=="微信模板消息"){
                    var val=$("#vipCode").attr("data-app_id");
                    if(val==""|| val==undefined){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请选择公众号"
                        });
                        return
                    }
                }
                var remind = [];
                var obj = $('.once');
                var time = '';
                var type = '';
                var content = '';
                for(i=0;i<obj.length;i++){
                    var arr= {};
                    var time = $(obj[i]).find('input').eq(0).attr('data-value');
                    if(time!='' && time != undefined){
                        var content = $(obj[i]).find('.remind_edit').attr('data-vale');
                        if(content == '' || content == undefined || content == null){
                            if($(obj[i]).find('#tempInput').val()=="微信模板消息"){
                                content=""
                            }else if($(obj[i]).find('#tempInput').val()=="短信消息"){
                                art.dialog({
                                    time: 1,
                                    lock: true,
                                    cancel: false,
                                    content:"请检查第"+(i+1)+"条提醒内容"
                                });
                                return false;
                            }
                        }
                        arr['time']= time;
                        //console.log('输入框的内容是：'+$(obj[i]).find('.input_select').eq(1).val())
                        arr['type'] = $(obj[i]).find('.input_select').eq(1).val() == '短信消息'? 'sms':'wxTemp';
                        arr['content'] = $(obj[i]).find('#tempInput').val()=="微信模板消息"?"":content;
                        remind.push(arr);
                    }else{

                    }

                }
                var remark = $('#message_content').val();
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "integral_name": integral_name,
                    //"target_vips": '',
                    "integral_duration": integral_duration,
                    "clear_cycle": clear_cycle,
                    "remind": remind,
                    "remarks": remark,
                    "isactive": ISACTIVE,
                    "clear_type":clear_type,
                    "app_id":$('#vipCode').attr('data-app_id')
                };
                var target_vips =[];
                var val = $('#custom_vip_list').css('display');
                val!="none"?isDefine():_params["target_vips"] = [];
                function isDefine(){
                    var group_condition_array=all_select_vip_list;
                    _params["target_vips"]=group_condition_array;
                }
                if(remind.length==0){
                    vipjs.send(_params)
                }else {
                    vipjs.getSendVipNum(_params);
                }
                //if($('.pre_title label').text() == '新增积分清零'){
                //    var _command = "/vipIntegral/insert";//接口名
                //    _params['clear_cycle'] =  clear_cycle;
                //}else{
                //    var _command = "/vipIntegral/update";//接口名
                //    _params['id'] =  sessionStorage.getItem("id");
                //    _params['clear_cycle'] =  cache.clear_cycle;
                //}
                //vipjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    vipjs.getSendVipNum=function(Data){
        var param={
            corp_code:Data.corp_code,
            point_contidion:Data.target_vips
        };
        whir.loading.add("", 0.5);
        oc.postRequire("post", "/vipFsend/getSendCount", "", param, function (data) {
            whir.loading.remove();
            if(data.code==0){
                var num=JSON.parse(data.message).count;
                $("#send_vip_num").html(num);
                $("#tk").show();
                $("#sendOk").unbind("click").bind("click",function(){
                    vipjs.send(Data)
                });
            }
        })
    };
    vipjs.send=function (_params){
            if($('.pre_title label').text() == '新增积分清零'){
                var _command = "/vipIntegral/insert";//接口名
            }else{
                var _command = "/vipIntegral/update";//接口名
                _params['id'] =  sessionStorage.getItem("id");
                _params['clear_cycle'] =  cache.clear_cycle;
            }
            vipjs.ajaxSubmit(_command, _params);
    };
    vipjs.ajaxSubmit = function (_command, _params) {
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if(_command=="/vipIntegral/insert"){
                    var msg = JSON.parse(data.message);
                    sessionStorage.setItem("id",msg.id);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_integralZero_edit.html");
                }
                if(_command=="/vipIntegral/update"){
                    $("#tk").hide();
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group.html");
            } else if (data.code == "-1") {
                $("#tk").hide();
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
                var t = setInterval(function (){
                    $('#corp_cover').val($('.searchable-select-holder').text());
                    if($('#corp_cover').val()!=''){
                        clearInterval(t);
                    }
                },1);
            }
            $('.corp_select select').searchableSelect();
            select_corp_code=$("#OWN_CORP").val();
            expend_data_1();
            getWxList($("#OWN_CORP").val());
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
                    select_clear_all();
                    _param.screen=[];
                    $("#custom_vip_list").html("");
                    all_select_vip_list=[];
                    cla=[];
                    br=[];
                    sea=[];
                    brand_value=[];
                    $("#group_list").find("input").val("");
                    $("#vipCode").val("请选择公众号");
                    $("#vipCode").attr("data-app_id","");
                    getWxList($("#OWN_CORP").val())
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
                _param.screen=[];
                select_clear_all();
                $("#custom_vip_list").html("");
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
                $("#vipCode").val("请选择公众号");
                $("#vipCode").attr("data-app_id","");
                getWxList($("#OWN_CORP").val());
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
    //whir.loading.remove("mask");
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
            wx="<span class='icon-ishop_6-22'style='color:#8ec750'></span>";
        }else{
            wx="<span class='icon-ishop_6-22'style='color:#cdcdcd'></span>";
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
}
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
        }
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
// var area_num = 1;
// var area_next = false;
// var shop_num = 1;
// var shop_next = false;
// var staff_num = 1;
// var staff_next = false;
var bd=[];//品牌
var ar=[];//区域
var sp=[];//店铺
var sf=[];//员工
var cla=[];//品类
var br=[];//品类
var sea=[];//季节
var str='';//调用allVip接口的区分
var brand_value=[];


$("#class span:last-child,#class input").click(function(){
    //$('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_class").width()) / 2;
    var tp = (arr[3] - $("#screen_class").height()) / 2 + 30;
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
            html_right+="<li id='"+arr[i]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+arr[i]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+arr[i]+"</span>\
            \</li>"
        }else if(typeof (arr[i])=="object"){
            html_right+="<li id='"+arr[i]["brand_code"]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+arr[i]["brand_name"]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+arr[i]["brand_name"]+"</span>\
            \</li>"
        }
    }
    $(node_container).html(html_right);
}
//点击店铺的区域
$("#shop_area").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_shop").hide();
    $("#screen_area").show();
    $("#screen_area").css("position","fixed");
    area_num = 1;
    $("#screen_area .screen_content_l ul").empty();
    // $("#screen_area .screen_content_r ul").empty();
    // $("#screen_area .s_pitch span").html("0");
    $("#area_search").val("");
    getarealist(area_num);
});
//点击店铺的品牌
$("#shop_brand").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_shop").hide();
    $("#screen_brand").show();
    $("#screen_brand").css("position","fixed");
    $("#screen_brand .screen_content_l ul").empty();
    // $("#screen_brand .screen_content_r ul").empty();
    getbrandlist();
});







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
            $("#group_type").css("width","320px");
            $("#group_type").next("ul").css("width","420px");
            $("#select_vip").css("display","inline-block");
            $("#type").hide();
            $("#group_list").hide();
            $("#custom_vip_list").show();
            break;
        case "class":
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
            break;
        case "brand":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
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
            $("#select_vip").css("display","none");
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
$("#select_vip").click(function(){
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    whir.loading.add("mask");
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").css("position","fixed");
    $("#screen_wrapper").show();
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
        var item_line = $(this).find(".item_list .item_line");
        var opera = "";
        var _group=$(this);
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

        var obj = {"key": key, "value": [], "type": "interval"};
        if (type == "affiliation") {
            //if($("#content_high").is(":visible")){
            var value = "";
            var name = "";
            var groupName="";
            item_line.map(function () {
                value = $(this).find("input").attr("data-code");
                name = $(this).find("input").attr("data-name");
                groupName=$(this).find(".center_item_group_title").html();
                key = $(this).attr("data-key");
                obj = {"key": key, "value": value, "name":name,"type": "text","groupName":groupName};
                if (obj.value.length != 0) {
                    screen.push(obj)
                }
            });
        } else if (type == "affiliation_other") {
            var value = item_line.find("input").attr("data-code");
            var name = item_line.find("input").attr("data-name");
            var groupName=_group.children(".center_item_group_title").html();
            obj = {"key": key, "value": value, "name":name,"type": "text","groupName":groupName};
            if (obj.value.length != 0) {
                screen.push(obj)
            }
        } else {
            var groupName=_group.children(".center_item_group_title").html();
            switch (type) {
                case "text":
                    if (key == "ZONE") {
                        item_line.map(function () {
                            var span = $(this).find(".select_city_group .imitate_select_wrap .imitate_select span");
                            if (span.is(":visible")) {
                                var value = span.eq(0).html() + "-" + span.eq(1).html() + "-" + span.eq(2).html();
                            } else {
                                var value = "";
                            }
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            if(value!="省-市-区"){
                                obj.value.push({"opera": opera, "logic": logic, "value": value})
                            }
                        });
                        if(obj.value.length>0){
                            obj.value[0].opera="";
                        }
                    } else if(key=="APP_ID" || key=="act_store"){
                        item_line.map(function () {
                            var value = $(this).find("input:text").attr("data-code");
                            var name= $(this).find("input:text").attr("data-name");
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            obj.value.push({"opera": opera, "logic": logic, "value": value,"name":name})
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
                            obj.value.push({"opera": opera, "logic": logic, "value": value,"code":code})
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
                            if(value!="" || (value=="" && (logic=="not null" || logic=="null"))){
                                obj.value.push({"opera": opera, "logic": logic, "value": value})
                            }
                            if(obj.value.length>0){
                                obj.value[0].opera="";
                            }
                        });
                    }
                    obj.type = "text";
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
                            var name=$(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var scondeName=$(item_line[s]).find(".center_item_group_title").html();
                            if (s!= len) {
                                opera = $(item_line[s]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[s]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[s]).find("input").eq(1).val().trim();
                            if (key == "COUPON") {
                                start_val = $(item_line[s]).find("input").eq(1).val().trim();
                                end_val = $(item_line[s]).find("input").eq(2).val().trim();
                                name=$(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
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
                                    key_second_I = $(item_line[s]).attr("data-key") + "_" + $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-type").trim();
                                }

                            }
                            if(start_val!="" || end_val!=""){
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val,
                                    "key": key_second_I,
                                    "name":name,
                                    "scondeName":scondeName
                                })
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
                            var name=$(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            if ($(item_line[T]).index() != 0) {
                                opera = $(item_line[T]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[T]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[T]).find("input").eq(1).val().trim();
                            if(start_val!="" || end_val!=""){
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val,
                                    "key": key_second_I,
                                    "name": name
                                })
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
                            if(start_val!="" || end_val!=""){
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val
                                });
                            }
                        });
                    }
                    if(obj.value.length>0){
                        obj.value[0].opera="";
                    }
                    obj.type = "interval";
                    break;
                case "radio":
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
                                var name=$(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
                                var dataName=$(item_line[T]).find("input:checked").parent().next().html();
                                obj.value.push({
                                    "opera": opera,
                                    "logic": "",
                                    "value": value,
                                    "key": key_second,
                                    "name": name,
                                    "dataName":dataName
                                })
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
                            var dataName=$(this).find("input:checked").parent().next().html();
                            obj.value.push({"opera": opera, "logic": logic, "value": value,"dataName":dataName});
                        });
                    } else {
                        item_line.map(function () {
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var value = $(this).find("input:checked").val();
                            var dataName = $(this).find("input:checked").parent().next().html();
                            obj.value.push({"opera": opera, "logic": "", "value": value,"dataName":dataName})
                        });
                    }
                    obj.type = "radio";
                    break
            }
            if (obj.value.length != 0) {
                obj.groupName=groupName;
                screen.push(obj)
            }
        }
    });
    SelectDataDef.resolve(screen);
    return SelectDataDef
}
$("#select_vip_sure").click(function(){ //筛选确定
    var screen = [];
    //whir.loading.remove("mask");
    //function settingValue(){
    //    if ($("#simple_filter").css("display") == "block") {
    //        $("#simple_contion .contion_input").each(function () {
    //            var input = $(this).find("input");
    //            var key = $(input[0]).attr("data-kye");
    //            var classname = $(input[0]).attr("class");
    //            if (classname.indexOf("short") == 0 && key != "3" && key != "4" && key!='17') {
    //                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
    //                    var param = {};
    //                    var val = {};
    //                    var name=$(input[0]).prev().text();
    //                    val['start'] = $(input[0]).val();
    //                    val['end'] = $(input[1]).val();
    //                    param['type'] = "json";
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param["name"]=name;
    //                    screen.push(param);
    //                }
    //            }else if (key == "15" ) {
    //                if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
    //                    var param = {};
    //                    var val = $(input[0]).attr("data-code");
    //                    var name=$(input[0]).prev().text();
    //                    var all_list_name=$(input[0]).attr("data-code_name");
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param['type'] = "text";
    //                    param["name"]=name;
    //                    param["all_list_name"]=all_list_name;
    //                    screen.push(param);
    //                }
    //            }else if(key=='17'){
    //
    //            }else if (key == "3" || key == "4") {
    //                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
    //                    var param = {};
    //                    var val = {};
    //                    var date =$("#simple_filter").find("input[data-kye='"+key+"']").parent().siblings().find("#consume_date_basic_"+key).attr("data-date");
    //                    var name=$(input[0]).prev().text();
    //                    val['start'] = $(input[0]).val();
    //                    val['end'] = $(input[1]).val();
    //                    param['type'] = "json";
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param['date'] = date;
    //                    param["name"]=name;
    //                    screen.push(param);
    //                }
    //            }else if(key == "18" && $(input[0]).val() !== "全部"){
    //                var param = {};
    //                var val = $(input[0]).val();
    //                var name=$(input[0]).prev().text();
    //                if(val=="已关注"){
    //                    val="Y"
    //                }else if(val=="未关注"){
    //                    val="N"
    //                }
    //                param['key'] = key;
    //                param['value'] = val;
    //                param['type'] = "text";
    //                param["name"]=name;
    //                screen.push(param);
    //            }else {
    //                if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
    //                    var param = {};
    //                    var val = $(input[0]).val();
    //                    var name=$(input[0]).prev().text();
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param['type'] = "text";
    //                    param["name"]=name;
    //                    screen.push(param);
    //                }
    //            }
    //        });
    //        return true
    //    } else {
    //        //if( $("input[data-kye='11']").val() !== "" && $("#vip_card_type").val()=="全部"){
    //        //    art.dialog({
    //        //        time: 1,
    //        //        lock: true,
    //        //        cancel: false,
    //        //        zIndex:10002,
    //        //        content: "请选择会员卡类型"
    //        //    });
    //        //    return false;
    //        //}
    //        $("#contion>div").each(function () {
    //            $(this).find(".contion_input").each(function (i, e) {
    //                var input = $(e).find("input");
    //                var key = $(input[0]).attr("data-kye");
    //                var classname = $(input[0]).attr("class");
    //                var id = $(input[0]).attr("id");
    //                if (key !== "3" && key !== "4" && classname.indexOf("short") == 0) {
    //                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
    //                        var param = {};
    //                        var val = {};
    //                        var name=$(input[0]).prev().text();
    //                        val['start'] = $(input[0]).val();
    //                        val['end'] = $(input[1]).val();
    //                        param['type'] = "json";
    //                        param['key'] = key;
    //                        param['value'] = val;
    //                        param['name']=name;
    //                        screen.push(param);
    //                    }
    //                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15" || key == "16") {
    //                    if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
    //                        var param = {};
    //                        var val = $(input[0]).attr("data-code");
    //                        var name=$(input[0]).prev().text();
    //                        var all_list_name=$(input[0]).attr("data-code_name");
    //                        param['key'] = key;
    //                        param['value'] = val;
    //                        param['type'] = "text";
    //                        param["name"]=name;
    //                        param["all_list_name"]=all_list_name;
    //                        screen.push(param);
    //                    }
    //                } else if (key == "17") {
    //
    //                } else if (key == "3" || key == "4") {
    //                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
    //                        var param = {};
    //                        var val = {};
    //                        var date = $("#consume_date").attr("data-date");
    //                        var name=$(input[0]).prev().text();
    //                        val['start'] = $(input[0]).val();
    //                        val['end'] = $(input[1]).val();
    //                        param['type'] = "json";
    //                        param['key'] = key;
    //                        param['value'] = val;
    //                        param['date'] = date;
    //                        param["name"]=name;
    //                        screen.push(param);
    //                    }
    //                } else if((key == "6"|| key == "18") && $(input[0]).val() !== "全部"){
    //                    var param = {};
    //                    var val = $(input[0]).val();
    //                    var name=$(input[0]).prev().text();
    //                    val = (val == "已冻结"||val == "未关注") ? "N": "Y";
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param['type'] = "text";
    //                    param["name"]=name;
    //                    screen.push(param);
    //                }else {
    //                    if($(input[0]).val() !== "" && $(input[0]).val() !== "全部" && $(input[0]).attr("data-type")=="isNull"){
    //                        var param = {};
    //                        var val = $(input[0]).val()=="空"?"null":$(input[0]).val();
    //                        var name=$(input[0]).prev().text();
    //                        param['key'] = key;
    //                        param['value'] = val;
    //                        param['type'] = "text";
    //                        param['dataType'] = "isNull";
    //                        param["name"]=name;
    //                        screen.push(param);
    //                    }else if($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
    //                        var param = {};
    //                        var val = $(input[0]).val();
    //                        var name=$(input[0]).prev().text();
    //                        param['key'] = key;
    //                        param['value'] = val;
    //                        param['type'] = "text";
    //                        param["name"]=name;
    //                        screen.push(param);
    //                    }
    //                    //if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
    //                    //    var param = {};
    //                    //    var val = $(input[0]).val();
    //                    //    var name=$(input[0]).prev().text();
    //                    //    param['key'] = key;
    //                    //    param['value'] = val;
    //                    //    param['type'] = "text";
    //                    //    param["name"]=name;
    //                    //    screen.push(param);
    //                    //}
    //                }
    //            });
    //            $(this).find("textarea").each(function () {
    //                if($(this).val()!=""){
    //                    var key = $(this).attr("data-kye");
    //                    var param = {};
    //                    var val = $(this).val();
    //                    var name=$(this).prev().text();
    //                    param['key'] = key;
    //                    param['value'] = val;
    //                    param['type'] = "text";
    //                    param["name"]=name;
    //                    screen.push(param);
    //                }
    //            });
    //        });
    //        var param_coupon={};
    //        var param_task={};
    //        var param_activity={};
    //        param_coupon['value']=[];
    //        param_task['value']=[];
    //        param_activity['value']=[];
    //        $("#coupon_list .coupon_line").each(function(i,e){
    //            var val=$(e).find(".filter_coupon").val();
    //            var text="";
    //            var input=$(e).find("input.short_input_date").map(function(){
    //                text+=$(this).val().trim()
    //            });
    //            if(text!=""){
    //                var param_val={
    //                    coupon_code:val=="全部"?"ALL":$(e).find(".filter_coupon").attr("data-value"),
    //                    coupon_name:$(e).find(".filter_coupon").val(),
    //                    num:{
    //                        start:$(e).find(".coupon_all").children("input").eq(0).val(),
    //                        end:$(e).find(".coupon_all").children("input").eq(1).val()
    //                    },
    //                    use:{
    //                        start:$(e).find(".coupon_used").children("input").eq(0).val(),
    //                        end:$(e).find(".coupon_used").children("input").eq(1).val()
    //                    },
    //                    expired_num:{
    //                        start:$(e).find(".coupon_overdue").children("input").eq(0).val(),
    //                        end:$(e).find(".coupon_overdue").children("input").eq(1).val()
    //                    },
    //                    can_use:{
    //                        start:$(e).find(".coupon_Usable").children("input").eq(0).val(),
    //                        end:$(e).find(".coupon_Usable").children("input").eq(1).val()
    //                    }
    //                };
    //                param_coupon['value'].push(param_val);
    //            }
    //
    //        });
    //        $("#task_list .task_line").each(function(i,e){
    //            var val=$(e).find(".filter_task").val();
    //            var participate=$(e).find(".participate").val();
    //            if(val=="全部")return;
    //            var param_val={
    //                task_code:$(e).find(".filter_task").attr("data-value"),
    //                task_name:$(e).find(".filter_task").val(),
    //                is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
    //            };
    //            param_task['value'].push(param_val);
    //        });
    //        $("#activity_list .activity_line").each(function(i,e){
    //            var val=$(e).find(".filter_activity").val();
    //            var participate=$(e).find(".participate").val();
    //            if(val=="全部")return;
    //            var param_val={
    //                activity_code:$(e).find(".filter_activity").attr("data-value"),
    //                activity_name:$(e).find(".filter_activity").val(),
    //                is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
    //            };
    //            param_activity['value'].push(param_val);
    //        });
    //        if(param_task['value'].length>0){
    //            param_task['key'] = 22;
    //            param_task['type'] = "array";
    //            param_task["name"]="会员任务";
    //            screen.push(param_task);
    //        }
    //        if(param_activity['value'].length>0){
    //            param_activity['key'] = 23;
    //            param_activity['type'] = "array";
    //            param_activity["name"]="活动";
    //            screen.push(param_activity);
    //        }
    //        if(param_coupon['value'].length>0){
    //            param_coupon['key'] = 21;
    //            param_coupon['type'] = "array";
    //            param_coupon["name"]="券";
    //            screen.push(param_coupon);
    //        }
    //        return true;
    //    }
    //}
    //var setting=settingValue();
    //if(screen.length>0){
    //    if(all_select_vip_list.length==0){
    //        all_select_vip_list=screen;
    //    }else{
    //        for(var i=0;i<screen.length;i++){
    //            var has=false;
    //            for(var a= 0,len=all_select_vip_list.length;a<len;a++){
    //                if(all_select_vip_list[a].key==screen[i].key){
    //                    all_select_vip_list[a]=screen[i];
    //                    has=true;
    //                }
    //            }
    //            if(!has){
    //                all_select_vip_list.push(screen[i]);
    //            }
    //        }
    //    }
    //    showSelect()
    //}
    //if(setting){
    //    $("#screen_wrapper").hide();
    //}
    getSelectDataDefine().then(function(screenData){
        _param['screen'] = screenData;
        $(".select_box_wrap").hide();
        $(".select_box_content_left .item_group").find("ul").slideUp().next(".group_arrow").removeClass("item_group_active");
        all_select_vip_list=screenData;
        showSelect()
    });
    //$("#p").hide();
});
function showSelect(){
    var html="";
    if ($(".pre_title label").text() == "编辑积分清零") {
        var xHTML = "<span style ='width: 40px;float:right'></span>";
    }else{
        var xHTML = "<i class='icon-ishop_6-12 q_remove' title='删除'></i>";
    }
    for(var b=0;b<all_select_vip_list.length;b++){
        var ob=all_select_vip_list[b];
        var value=all_select_vip_list[b].value;
        if(all_select_vip_list[b].type=="interval"){
            //var logic1=getLogicDefault(all_select_vip_list[b].logic1);
            //var logic2=getLogicDefault(all_select_vip_list[b].logic2);
            var groupName=all_select_vip_list[b].groupName;
            var detailHtml='';
            if(all_select_vip_list[b].key=="TRADE"){
                for(var v=0;v<value.length;v++){
                    var head="";
                    var foot="";
                    var key_v2=value[v].key;
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic1=getLogicDefault(value[v].logic1);
                    var logic2=getLogicDefault(value[v].logic2);
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
                        detailHtml+=opera+head+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]+foot
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
                        detailHtml+=opera+timeHtml+head+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]+foot
                    }
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                    detailHtml+
                    xHTML+
                    '</div>'
            }else  if(all_select_vip_list[b].key=="COUPON"){
                for(var v=0;v<value.length;v++){
                    var head="";
                    var key_v2=value[v].key;
                    var dataKey=key_v2.slice(key_v2.lastIndexOf("_")+1);
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic1=getLogicDefault(value[v].logic1);
                    var logic2=getLogicDefault(value[v].logic2);
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
                    detailHtml+=opera+head+value[v].name+logic1+value[v].start+'至'+logic2+value[v].end+"个"
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                    detailHtml+
                    xHTML+
                    '</div>'
            }else if(all_select_vip_list[b].key=="CATA1_PRD" || all_select_vip_list[b].key=="CATA2_PRD" || all_select_vip_list[b].key=="CATA3_PRD"|| all_select_vip_list[b].key=="CATA4_PRD" || all_select_vip_list[b].key=="SEASON_PRD" || all_select_vip_list[b].key=="T_BL_M" || all_select_vip_list[b].key=="DAYOFWEEK" || all_select_vip_list[b].key=="BRAND_ID" || all_select_vip_list[b].key=="CATA3_PRD" || all_select_vip_list[b].key=="STORE_AREA" || all_select_vip_list[b].key=="PRICE_CODE"){
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                    '<span class="condition_detail" title="'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'">'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'</span>' +
                    '<span class="icon-ishop_6-29"></span>' +
                    '</div>'
            }else{
                for(var v=0;v<value.length;v++){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic1=getLogicDefault(value[v].logic1);
                    var logic2=getLogicDefault(value[v].logic2);
                    detailHtml+=opera+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                html+='<div class="condition"' +
                    'data-value=\''+JSON.stringify(ob)+'\'>'+
                    '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                    detailHtml+
                    xHTML+
                    '</div>'
            }
        }else if(all_select_vip_list[b].type=="text"){
            var key=all_select_vip_list[b].key;
            var detailHtml='';
            if(key == "brand_code" || key == "area_code" || key == "store_code" || key =="user_code" || key=="VIP_GROUP_CODE"){
                detailHtml='<span class="condition_detail" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>';
            }else if(key=="ZONE"){
                for(var v=0;v<value.length;v++){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic=getLogicDefault(value[v].logic);
                    var name="";
                    if(value[v]["value"]!=""){
                        var valName=value[v]["value"].split("-");
                        if(valName[0]!="省"){
                            name+=valName[0];
                        }if(valName[1]!="市"){
                            name+="-"+valName[1];
                        }
                        if(valName[2]!="区"){
                            name+="-"+valName[2];
                        }
                    }
                    detailHtml+=opera+logic+name
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
            }else if(key=="APP_ID" || key=="act_store"){
                for(var v=0;v<value.length;v++){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic=getLogicDefault(value[v].logic);
                    detailHtml+=opera+logic+value[v].name
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
            }else{
                for(var v=0;v<value.length;v++){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    var logic=getLogicDefault(value[v].logic);
                    detailHtml+=opera+logic+value[v].value
                }
                detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
            }
            html+='<div class="condition"' +
                'data-value=\''+JSON.stringify(ob)+'\'>'+
                '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                detailHtml+
                xHTML+
                '</div>'
        }else if(all_select_vip_list[b].type=="radio"){
            var key=all_select_vip_list[b].key;
            var detailHtml='';
            for(var v=0;v<value.length;v++){
                var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                var logic=getLogicDefault(value[v].logic);
                if(key == "ACTIVITY" || key == "TASK") {
                    var val=value[v]["value"]=='Y'?"已参与":value[v]["value"]=='N'?"未参与":"";
                    var dataName=value[v]["name"];
                    detailHtml+=opera+logic+val+dataName
                }else {
                    var val=value[v]["dataName"];
                    detailHtml+=opera+logic+val
                }
            }
            detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
            html+='<div class="condition"'+
                'data-value=\''+JSON.stringify(ob)+'\'>'+
                '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                detailHtml+
                xHTML+
                '</div>'
        }
    }
    //for(var b=0;b<all_select_vip_list.length;b++){
    //    if(all_select_vip_list[b].type=="json" && all_select_vip_list[b].key!=="3" && all_select_vip_list[b].key!="4"){
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align: middle;text-align: right;display: inline-block;margin-right: 10px; max-width:70px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;'>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='width: 130px;' value='"+all_select_vip_list[b].value["start"]+"' readonly title='"+all_select_vip_list[b].value["start"]+"'>" +
    //            "<span style='display: inline-block;width: 30px;text-align: center'>~</span>" +
    //            "<input readonly type='text' style='width: 130px;' value='"+all_select_vip_list[b].value["end"]+"' title='"+all_select_vip_list[b].value["end"]+"'>" +
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="brand_code" ||all_select_vip_list[b].key=="area_code" ||all_select_vip_list[b].key=="14" ||all_select_vip_list[b].key=="15" || all_select_vip_list[b].key=="16"){
    //        if(all_select_vip_list[b].value!=""){
    //            html+="<div style='float: right'>" +
    //                "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width:70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis'>"+all_select_vip_list[b].name+"</span>" +
    //                "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+all_select_vip_list[b].all_list_name+"' readonly  title='"+all_select_vip_list[b].all_list_name+"'>"+
    //                xHTML+
    //                "</div>"
    //        }
    //    }else if(all_select_vip_list[b].key=="3" || all_select_vip_list[b].key=="4"){
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align: middle;text-align: right;display: inline-block;margin-right: 10px; max-width:70px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;'>"+all_select_vip_list[b].name+"</span>" +
    //            "<input readonly type='text' style='width: 90px;margin-right: 10px;' value='最近"+all_select_vip_list[b].date+"个月'>" +
    //            "<input type='text' style='width: 80px;' value='"+all_select_vip_list[b].value["start"]+"' readonly title='"+all_select_vip_list[b].value["start"]+"'>" +
    //            "<span style='display: inline-block;width: 30px;text-align: center'>~</span>" +
    //            "<input readonly type='text' style='width: 80px;' value='"+all_select_vip_list[b].value["end"]+"' title='"+all_select_vip_list[b].value["end"]+"'>" +
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="17"){
    //
    //    }else if(all_select_vip_list[b].key=="6"){
    //        var value=all_select_vip_list[b].value=="N"?"已冻结":"未冻结";
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+value+"' readonly title='"+value+"'>"+
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="18"){
    //        var value=all_select_vip_list[b].value=="N"?"未关注":"已关注";
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+value+"' readonly title='"+value+"'>"+
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="21"){
    //        var val=all_select_vip_list[b].value;
    //        var text="";
    //        for(var v=0;v<val.length;v++){
    //            var expired_num=val[v].expired_num;
    //            var can_use=val[v].can_use;
    //            var num=val[v].num;
    //            var use=val[v].use;
    //            if(val[v].coupon_code!=""){
    //                var coupon_text="券类型:"+val[v].coupon_name+",";
    //                text+=coupon_text;
    //            }
    //            if(expired_num.start!="" || expired_num.end!=""){
    //                var expired_num_text="过期数量:"+expired_num.start+"到"+expired_num.end+",";
    //                text+=expired_num_text;
    //            }
    //            if(can_use.start!="" || can_use.end!=""){
    //                var can_use_text="可用数量:"+can_use.start+"到"+can_use.end+",";
    //                text+=can_use_text;
    //            }
    //            if(num.start!="" || num.end!=""){
    //                var num_text="使用数量:"+num.start+"到"+num.end+",";
    //                text+=num_text;
    //            }
    //            if(use.start!="" || use.end!=""){
    //                var use_text="获得数量:"+use.start+"到"+use.end+",";
    //                text+=use_text;
    //            }
    //            text=text.slice(0,-1);
    //            text+=";"
    //        }
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+text+"' readonly title='"+text+"'>"+
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="22"){
    //        var task=all_select_vip_list[b].value;
    //        var text="";
    //        for(var t=0;t<task.length;t++){
    //            var isJoin=task[t].is_join=="ALL"?"全部":task[t].is_join=="Y"?"已参与":"未参与";
    //            var task_text="任务名称:"+task[t].task_name+","+"是否参与:"+isJoin+";";
    //            text+=task_text
    //        }
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+text+"' readonly title='"+text+"'>"+
    //            xHTML+
    //            "</div>"
    //    }else if(all_select_vip_list[b].key=="23"){
    //        var activity=all_select_vip_list[b].value;
    //        var text="";
    //        for(var a=0;a<activity.length;a++){
    //            var isJoin=activity[a].is_join=="ALL"?"全部":activity[a].is_join=="Y"?"已参与":"未参与";
    //            var activity_text="活动名称:"+activity[a].activity_name+","+"是否参与:"+isJoin+";";
    //            text+=activity_text
    //        }
    //        html+="<div style='float: right'>" +
    //            "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //            "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+text+"' readonly title='"+text+"'>"+
    //            xHTML+
    //            "</div>"
    //    }else {
    //        if(all_select_vip_list[b].dataType!==undefined){
    //            console.log(all_select_vip_list[b].value)
    //            html+="<div style='float: right'>" +
    //                "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //                "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+(all_select_vip_list[b].value=="null"?"空":all_select_vip_list[b].value)+"' readonly title='"+(all_select_vip_list[b].value=="null"?"空":all_select_vip_list[b].value)+"'>"+
    //                xHTML+
    //                "</div>"
    //        }else{
    //            html+="<div style='float: right'>" +
    //                "<span title='"+all_select_vip_list[b].name+"' style='margin-top:-3px;vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
    //                "<input type='text' style='text-overflow: ellipsis;width: 290px;' value='"+all_select_vip_list[b].value+"' readonly title='"+all_select_vip_list[b].value+"'>"+
    //                xHTML+
    //                "</div>"
    //        }
    //    }
    //}
    $("#custom_vip_list").html(html);
    $("#custom_vip_list input").each(function (){
        $(this).css('background','');
    });
}
function showSelect_edit(all_select_vip_list,type){
    var html="";
    if ($(".pre_title label").text() == "编辑积分清零") {
        var xHTML = "<span style ='width: 40px;float:right'></span>";
    }else{
        var xHTML = "<i class='icon-ishop_6-12 q_remove' title='删除'></i>";
    }
    $("#custom_vip_list").empty();
    if(all_select_vip_list.length != 0){
        if(type=="part"){
            var list=all_select_vip_list;
            for(var i=0;i<list.length;i++){
                if(list[i].type=="json" && list[i].key!="3" && list[i].key!="4"){
                    var startVal = list[i]["value"].start;
                    var endVal = list[i]["value"].end;
                    start = startVal != '' ? startVal: ' ';
                    end = endVal != '' ? endVal: ' ';
                    //start = startVal != '' ? setTime(startVal): ' ';
                    //end = endVal != '' ? setTime(endVal): ' ';
                    //function setTime(val){
                    //    var arr = val.split('-');
                    //    val = arr[0]+'月'+arr[1]+'日'
                    //    return val
                    //}
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value='+JSON.stringify(list[i].value)+'><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span>'+start+' ~ '+end+'</span></div>';
                }else if(list[i].key=="brand_code" ||list[i].key=="area_code" ||list[i].key=="14" ||list[i].key=="15" || list[i].key=="16"){

                    var condition='<div style="margin-right: 0!important;" class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value-name="'+list[i].value_name+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="max-width: 82%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;display: inline-block;" title="'+list[i].all_list_name+'">'+list[i].all_list_name+'</span></div>';
                }else if(list[i].key=="3" || list[i].key=="4"){
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value='+JSON.stringify(list[i].value)+' data-date="'+list[i].date+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="margin-right: 5px">最近'+list[i].date+'个月</span><span>'+list[i]["value"].start+'~'+list[i]["value"].end+'次</span></div>';
                }else if(list[i].key=="17"){

                }else if(list[i].key=="6"){
                    var value=list[i].value=="Y"?"未冻结":"已冻结";
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span>'+value+'</span></div>';
                }else if(list[i].key=="18"){
                    var value=list[i].value=="Y"?"已关注":"未关注";
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span>'+value+'</span></div>';
                }else if(list[i].key=="21"){
                    var val=list[i].value;
                    var text="";
                    for(var v=0;v<val.length;v++){
                        var expired_num=val[v].expired_num;
                        var can_use=val[v].can_use;
                        var num=val[v].num;
                        var use=val[v].use;
                        if(val[v].coupon_code!=""){
                            var coupon_text="券类型:"+val[v].coupon_name+",";
                            text+=coupon_text;
                        }
                        if(expired_num.start!="" || expired_num.end!=""){
                            var expired_num_text="过期数量:"+expired_num.start+"到"+expired_num.end+",";
                            text+=expired_num_text;
                        }
                        if(can_use.start!="" || can_use.end!=""){
                            var can_use_text="可用数量:"+can_use.start+"到"+can_use.end+",";
                            text+=can_use_text;
                        }
                        if(num.start!="" || num.end!=""){
                            var num_text="使用数量:"+num.start+"到"+num.end+",";
                            text+=num_text;
                        }
                        if(use.start!="" || use.end!=""){
                            var use_text="获得数量:"+use.start+"到"+use.end+",";
                            text+=use_text;
                        }
                        text=text.slice(0,-1);
                        text+=";"
                    }
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="display: inline-block;max-width: 76%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+text+'">'+text+'</span></div>'
                }else if(list[i].key=="22"){
                    var task=list[i].value;
                    var text="";
                    for(var t=0;t<task.length;t++){
                        var isJoin=task[t].is_join=="ALL"?"全部":task[t].is_join=="Y"?"已参与":"未参与";
                        var task_text="任务名称:"+task[t].task_name+","+"是否参与:"+isJoin+";";
                        text+=task_text
                    }
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="display: inline-block;max-width: 76%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+text+'">'+text+'</span></div>'
                }
                else if(list[i].key=="23"){
                    var activity=list[i].value;
                    var text="";
                    for(var a=0;a<activity.length;a++){
                        var isJoin=activity[a].is_join=="ALL"?"全部":activity[a].is_join=="Y"?"已参与":"未参与";
                        var activity_text="活动名称:"+activity[a].activity_name+","+"是否参与:"+isJoin+";";
                        text+=activity_text
                    }
                    var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="display: inline-block;max-width: 76%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+text+'">'+text+'</span></div>'
                }else {
                    if(list[i].dataType!==undefined){
                        var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="max-width: 76%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+list[i].value+'">'+(list[i].value=="null"?"空":list[i].value)+'</span></div>'
                    }else{
                        var condition='<div class="condition" data-name="'+list[i].name+'" data-key="'+list[i].key+'" data-type="'+list[i].type+'" data-value="'+list[i].value+'"><span class="condition_title" title="'+list[i].name+'">'+list[i].name+'</span><span style="max-width: 76%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+list[i].value+'">'+list[i].value+'</span></div>'
                    }

                }
                $("#custom_vip_list").append(condition);
            }
        }else if(type=="part_new"){
            for(var b=0;b<all_select_vip_list.length;b++){
                var ob=all_select_vip_list[b];
                var value=all_select_vip_list[b].value;
                if(all_select_vip_list[b].type=="interval"){
                    //var logic1=getLogicDefault(all_select_vip_list[b].logic1);
                    //var logic2=getLogicDefault(all_select_vip_list[b].logic2);
                    var groupName=all_select_vip_list[b].groupName;
                    var detailHtml='';
                    if(all_select_vip_list[b].key=="TRADE"){
                        for(var v=0;v<value.length;v++){
                            var head="";
                            var foot="";
                            var key_v2=value[v].key;
                            var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                            var logic1=getLogicDefault(value[v].logic1);
                            var logic2=getLogicDefault(value[v].logic2);
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
                                detailHtml+=opera+head+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]+foot
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
                                detailHtml+=opera+timeHtml+head+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]+foot
                            }
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'<span>';
                        html+='<div class="condition"' +
                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                            '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                            detailHtml+
                            xHTML+
                            '</div>'
                    }else  if(all_select_vip_list[b].key=="COUPON"){
                        for(var v=0;v<value.length;v++){
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
                            detailHtml+=head+value[v].name+value[v].start+value[v].end
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'<span>';
                        html+='<div class="condition"' +
                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                            '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                            detailHtml+
                            xHTML+
                            '</div>'
                    }else if(all_select_vip_list[b].key=="CATA1_PRD" || all_select_vip_list[b].key=="CATA2_PRD" || all_select_vip_list[b].key=="CATA3_PRD"|| all_select_vip_list[b].key=="CATA4_PRD" || all_select_vip_list[b].key=="SEASON_PRD" || all_select_vip_list[b].key=="T_BL_M" || all_select_vip_list[b].key=="DAYOFWEEK" || all_select_vip_list[b].key=="BRAND_ID" || all_select_vip_list[b].key=="CATA3_PRD" || all_select_vip_list[b].key=="STORE_AREA" || all_select_vip_list[b].key=="PRICE_CODE"){
                        html+='<div class="condition"' +
                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                            '<span class="condition_title" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>' +
                            '<span class="condition_detail" title="'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'">'+all_select_vip_list[b]["typeName"]+logic1+all_select_vip_list[b]["start"]+'至'+logic2+all_select_vip_list[b]["end"]+'</span>' +
                            '<span class="icon-ishop_6-29"></span>' +
                            '</div>'
                    }else{
                        for(var v=0;v<value.length;v++){
                            var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                            var logic1=getLogicDefault(value[v].logic1);
                            var logic2=getLogicDefault(value[v].logic2);
                            detailHtml+=opera+logic1+value[v]["start"]+'至'+logic2+value[v]["end"]
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                        html+='<div class="condition"' +
                            'data-value=\''+JSON.stringify(ob)+'\'>'+
                            '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                            detailHtml+
                            xHTML+
                            '</div>'
                    }
                }else if(all_select_vip_list[b].type=="text"){
                    var key=all_select_vip_list[b].key;
                    var detailHtml='';
                    if(key == "brand_code" || key == "area_code" || key == "store_code" || key =="user_code" || key=="VIP_GROUP_CODE"){
                        detailHtml='<span class="condition_detail" title="'+all_select_vip_list[b].name+'">'+all_select_vip_list[b].name+'</span>';
                    }else if(key=="ZONE"){
                        for(var v=0;v<value.length;v++){
                            var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                            var logic=getLogicDefault(value[v].logic);
                            var name="";
                            if(value[v]["value"]!=""){
                                var valName=value[v]["value"].split("-");
                                if(valName[0]!="省"){
                                    name+=valName[0];
                                }if(valName[1]!="市"){
                                    name+="-"+valName[1];
                                }
                                if(valName[2]!="区"){
                                    name+="-"+valName[2];
                                }
                            }
                            detailHtml+=opera+logic+name
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                    }else if(key=="APP_ID" || key=="act_store"){
                        for(var v=0;v<value.length;v++){
                            var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                            var logic=getLogicDefault(value[v].logic);
                            detailHtml+=opera+logic+value[v].name
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                    }else{
                        for(var v=0;v<value.length;v++){
                            var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                            var logic=getLogicDefault(value[v].logic);
                            detailHtml+=opera+logic+value[v].value
                        }
                        detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                    }
                    html+='<div class="condition"' +
                        'data-value=\''+JSON.stringify(ob)+'\'>'+
                        '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                        detailHtml+
                        xHTML+
                        '</div>'
                }else if(all_select_vip_list[b].type=="radio"){
                    var key=all_select_vip_list[b].key;
                    var detailHtml='';
                    for(var v=0;v<value.length;v++){
                        var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                        var logic=getLogicDefault(value[v].logic);
                        if(key == "ACTIVITY" || key == "TASK") {
                            var val=value[v]["value"]=='Y'?"已参与":value[v]["value"]=='N'?"未参与":"";
                            var dataName=value[v]["name"];
                            detailHtml+=opera+logic+val+dataName
                        }else {
                            var val=value[v]["dataName"];
                            detailHtml+=opera+logic+val
                        }
                    }
                    detailHtml='<span class="condition_detail" title="'+detailHtml+'">'+detailHtml+'</span>';
                    html+='<div class="condition"'+
                        'data-value=\''+JSON.stringify(ob)+'\'>'+
                        '<span class="condition_title" title="'+all_select_vip_list[b].groupName+'">'+all_select_vip_list[b].groupName+'</span>' +
                        detailHtml+
                        xHTML+
                        '</div>'
                }
            }
            $("#custom_vip_list").html(html);
        }
    }else{
        $("#custom_vip_list").html('<p style="text-align: center;height: 50px;line-height: 50px;margin: 0" id="p">暂无条件</p>');
    }

}
$("#custom_vip_list").on("click","i",function(){
    var name= $(this).parent().children().eq(0).text();
    for(var n=0;n<all_select_vip_list.length;n++){
        if(all_select_vip_list[n].groupName==name){
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
//$("#consume_discount").find("input").bind("blur",function(){
//    if(Number($(this).val())<0 || Number($(this).val())>10){
//        art.dialog({
//            zIndex: 10010,
//            time: 1,
//            lock: true,
//            cancel: false,
//            content: "折扣范围为1-10"
//        });
//        $(this).val("")
//    }
//});
//获取拓展资料
//function expend_data1() {
//    var param={"corp_code":$("#OWN_CORP").val()};
//    oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
//        if(data.code == 0){
//            $("#expend_attribute").empty();
//            var msg = JSON.parse(data.message);
//            var list = JSON.parse(msg.list);
//            var html="";
//            var simple_html="";
//            if(list.length>0){
//                for(var i=0;i<list.length;i++){
//                    var param_type = list[i].param_type;
//                    if(param_type=="date"){
//                        simple_html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                            + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
//                            + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
//                        html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                            + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
//                            + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
//                    }
//                    if(param_type=="select"){
//                        var param_values = "";
//                        param_values = list[i].param_values.split(",");
//                        //if(param_values.length>0){
//                            var li="<li>空</li>";
//                            for(var j=0;j<param_values.length;j++){
//                                li+='<li>'+param_values[j]+'</li>'
//                            }
//                            html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                                + '<input data-expend="text" data-type="isNull" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
//                                + li
//                                + '</ul></div>';
//                        //}else {
//                        //    html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                        //        + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
//                        //}
//                    }
//                    if(param_type=="text"){
//                        html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                            + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
//                    }
//                    if(param_type=="longtext"){
//                        html+='<div class="textarea"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
//                            + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
//                    }
//                }
//            }
//            $("#expend_attribute").append(html);
//            $("#memorial_day").html(" ").append(simple_html);
//        }else if(data.code == -1){
//            console.log(data.message);
//        }
//    });
//}
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
//function Ownformat(datas) {//纪念日开始日期回掉函数
//    var date=datas.split("-");
//    date=date[1]+"-"+date[2];
//    var id=$(this.elem).nextAll("input").attr("id");
//    $(this.elem).val(date);
//    $(this.elem).nextAll("input").attr("onclick","laydate({elem:'#"+id+"',min:'"+datas+"', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})")
//}
//function Ownformatend(datas) {//纪念日截止日期回掉函数
//    var date=datas.split("-");
//    date=date[1]+"-"+date[2];
//    var id=$(this.elem).prevAll("input").attr("id");
//    $(this.elem).val(date);
//    $(this.elem).prevAll("input").attr("onclick","laydate({elem:'#"+id+"',min:\'1900-01-0\', max:'"+datas+"',istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})")
//}
//关闭按钮回到列表页
$("#send_close").click(function(){
    sessionStorage.removeItem("group_vip");
    $(window.parent.document).find('#iframepage').attr("src", "vip/vip_integralZero.html?t="+ $.now());
});
$('#back_message').click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "vip/vip_integralZero.html?t="+ $.now());
});
$("#wxTemplet").on("blur",".wxUrl",function () {
    var reg= /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
    var val=$(this).val();
    if(!reg.test(val) && val!==""){
        art.dialog({
            time: 1,
            lock: true,
            zIndex:10002,
            cancel: false,
            content: "请填写正确的网页地址并且以“http://”开头"
        });
        $(this).val("");
        return ;
    }
});
cache = {
    'type':'',
    'sMinH':'',
    'd':'',
    'm':'',
    'y':'',
    'clear_cycle':'',
    'clear_cycle_0':''
};
//    删除一条提醒
function removeIt(dom){
    $(dom).parent().remove();
    $('.icon_action_add:last').show();
    var $doms = $('.once');
    if($doms.length < 2){
        $doms.find('.icon_action_delete').hide();
    }
}
//    新增一条提醒
function add(dom){
    $(dom).hide();
    $(dom).next().show();
    $('.once:last').after('<div style="position:relative;top:-30px;left: 123px;height: auto" class="once"> <!--内容--> <div class="drop_down item_1" style="float: left;"> <span> <input class="input_select" type="text" placeholder="时间" readonly style="width: 200px;margin-right: 10px;" onclick="showNext(this)"/> <!--*****--> <div class="clear_time" style="display: none;left: 0px;"> <p> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none;" class="ck_1"> <b class="radio_b"></b> <label class="M" style="width: 40px;margin-left: -10px;margin-right:12px;color: #dbdbdb">每月</label> </span> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none" class="ck_2"> <b class="radio_b"></b> <label class="Y" style="width: 40px;margin-left:-10px;margin-right:12px;color:#dbdbdb;">每年</label> </span> <span style="margin-right: 5px;float: left;"> <input type="radio" name="ck" class="check" style="display: none" class="ck_3"> <b class="radio_b"></b> <label class="O" style="width: 50px;margin-left: -10px;color:#dbdbdb;">一次性</label> </span> </p> <span class="clear_show"> </span> <btn class="clear_time_btn" >确认</btn> </div> <!--*****--> </span> </div> <div class="drop_down item_1" style="float: left;"> <span> <input class="input_select" type="text" placeholder="微信模板消息" value="微信模板消息" readonly style="width: 130px" id="tempInput" onclick="showMsgType(this)"/> <ul style="width:130px; margin-left: 200px;"> <li>微信模板消息</li> <li>短信消息</li> </ul> </span> </div> <span class="add_per_icon remind_edit isWx" style="text-align: center;cursor: pointer;margin-left: 10px;padding-left: 0px;width: 70px">编辑内容</span> <span class="icon_action_add" onclick="add(this)"><span class="icon-ishop_6-01"></span></span> <span class="icon_action_delete" onclick="removeIt(this)"><span class="icon-ishop_6-02"></span></span> </div>');
    $('.clear_time p span').attr('onclick','checkType(this)');
}
$('body').unbind('click').bind('click',function(){
    var doms_1 = $('.clear_time');
    for(i = 0;i <doms_1.length;i++){
        var isOpen = $(doms_1[i]).attr('isOpen');
        if(isOpen != 'true'){
            $(doms_1[i]).hide()
        }else if(isOpen == 'true'){
            $(doms_1[i]).show();
        }
    }
    var doms_2 = $('.once .item_1').eq(1).find('ul');
    for(i = 0;i <$('.once').length;i++){
        var isOpen = $(doms_2[i]).attr('isOpen');
        if(isOpen == 'false' || isOpen != 'true'){
            $(doms_2).eq(i).hide();
        }else if(isOpen == 'true'){
            $(doms_2[i]).show();
        }
    }

});
//    点击打开时间窗
function showNext(dom) {
    stopEvent(dom);
    //隐藏消息模板
    //var uls = $('.once .drop_down').eq(1).find('ul')
    //for(i = 0; i < uls.length; i++){
    //    if(i = 0){return}
    //    $(uls[i]).hide();
    //}
    //隐藏编辑内容弹窗
    $('.edit_frame').hide();
    $('.once .drop_down').find('ul').hide();
    $(dom).next().toggle();
    var display = $(dom).next().css('display')
    if(display == 'block' || display == ''){
        $('.clear_time').attr('isOpen',false)
        $(dom).next().attr('isOpen',true)
        var doms_1 = $('.clear_time');
        for(i = 0;i <doms_1.length;i++){
            var isOpen = $(doms_1[i]).attr('isOpen');
            if(isOpen != 'true'){
                $(doms_1[i]).hide();
            }else if(isOpen == 'true'){
                $(doms_1[i]).show();
            }
        }
        //***********************
        $(dom).mouseover(function(){
            $(dom).next().attr('isOpen',true)
        })
        $(dom).mouseout(function(){
            $(dom).next().attr('isOpen',false)
            //对弹窗的标记
            var doms = $('.clear_time');
            for(i = 0; i<doms.length; i++){
                doms[i].onmouseover = function(){
                    for(i = 0; i<doms.length; i++){
                        doms[i].onmouseover = function(){
                            $(this).attr('isOpen',true);
                        }
                        doms[i].onmouseout = function(){
                            $(this).attr('isOpen',false);
                        }
                    }
                }
            }
        })
    }else if(display == 'none'){
        $('.clear_time').attr('isOpen',false)
    }
    //所有相同点击事件class
    var doms = $('.input_select');
    //获取是否设置过时间
    var val = $(dom).attr('data-value');
    //设置当前点击true
    domsVal(doms,'clickValue');
    $(dom).attr('clickValue', 'true');
    //根据设置过的时间给输入框赋值
    if (val == undefined) {
        $(dom).next().find('p span').eq(0).click();
    }else{
        var arr = val.split(' ');
        if(arr.length == 6){
            if(val.indexOf('*')!= -1){
                //每月
                $(dom).next().find('p span').eq(0).click();
            }else{
                //每年
                $(dom).next().find('p span').eq(1).click();
            }
        }else if(arr.length ==7){
            //一次性
            $(dom).next().find('p span').eq(2).click();
        }
    }
}
//    时间选项卡切换
$('.clear_time p span').unbind('click').bind("click",(function (e) {
    stopEvent(e);
    //当前时间
    var date=new Date;
    var year = date.getFullYear();
    var month  = date.getMonth()+1;
    var day  = date.getDate();
    var hour  = date.getHours();
    var min = date.getMinutes()
    $('.clear_time p span .radio_b').css('background','white');
    $('.clear_time p span label').css('color','#dbdbdb');
    $(this).find('.radio_b').css('background','url(../img/radio_chicked.png) no-repeat center');
    $(this).find('.radio_b').css('background-size','15px 15px');
    $(this).find('label').css('color','#888');
    var id = $(this).find('label').attr('class');
    var value = $(this).parents('.clear_time').prev().attr('data-value');
    var inputs = '';
    var arr = '';
    if(id == 'M'){
        $(this).parent().next().html('<input type="tel" maxlength="2" class="theDay" style="width: 50px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 50px;"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 50px;"/> 分')
        cache.type = '每月';
        check(cache.type)
        inputs = $(this).parent().next().find('input');
        if(value == undefined){
            inputs.eq(0).val('0'+1)
            inputs.eq(1).val('00')
            inputs.eq(2).val('00')
        }else if(value.split(' ').length == 6 && value.split(' ').indexOf('*') != -1){
            arr = value.split(' ');
            inputsPush(inputs,2,arr[1])//分
            inputsPush(inputs,1,arr[2])//时
            inputsPush(inputs,0,arr[3])//日
        }else{
            inputs.eq(0).val('0'+1)
            inputs.eq(1).val('00')
            inputs.eq(2).val('00')
        }
        inputs.eq(0).focus();
    }
    if(id == 'Y'){
        $(this).parent().next().html('<input type="tel" maxlength="2" class="theMon" style="width: 88px;"/> 月 <input type="tel" maxlength="2" class="theDay" style="width: 88px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 88px;margin-top: 5px"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 88px;margin-top: 5px"/> 分')
        cache.type = '每年';
        check(cache.type);
        inputs = $(this).parent().next().find('input');
        if(value == undefined){
            inputs.eq(0).val('0'+1)
            inputs.eq(1).val('0'+1)
            inputs.eq(2).val('00')
            inputs.eq(3).val('00')
        }else if(value.split(' ').length == 6 && value.split(' ').indexOf('*') == -1){
            arr = value.split(' ');
            inputsPush(inputs,3,arr[1])//分
            inputsPush(inputs,2,arr[2])//时
            inputsPush(inputs,1,arr[3])//日
            inputsPush(inputs,0,arr[4])//月
        }else{
            inputs.eq(0).val('0'+1)
            inputs.eq(1).val('0'+1)
            inputs.eq(2).val('00')
            inputs.eq(3).val('00')
        }
        inputs.eq(0).focus();
    }
    if(id == 'O'){
        $(this).parent().next().html('<input type="tel" maxlength="4" class="theYear" style="width: 50px;"/> 年 <input type="tel" maxlength="2" class="theMon" style="width: 50px;"/> 月 <input type="tel" maxlength="2" class="theDay" style="width: 50px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 88px;margin-top: 5px"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 88px;margin-top: 5px"/> 分')
        cache.type = '一次性';
        check(cache.type);
        inputs = $(this).parent().next().find('input');
        if(value == undefined){
            inputsPush(inputs,4,min)//分
            inputsPush(inputs,3,hour)//时
            inputsPush(inputs,2,day)//日
            inputsPush(inputs,1,month)//月
            inputsPush(inputs,0,year) //年
        }else if(value.split(' ').length == 7){
            arr = value.split(' ');
            inputsPush(inputs,4,arr[1])//分
            inputsPush(inputs,3,arr[2])//时
            inputsPush(inputs,2,arr[3])//日
            inputsPush(inputs,1,arr[4])//月
            inputsPush(inputs,0,arr[6]) //年
        }else{
            inputsPush(inputs,4,min)//分
            inputsPush(inputs,3,hour)//时
            inputsPush(inputs,2,day)//日
            inputsPush(inputs,1,month)//月
            inputsPush(inputs,0,year) //年
        }
        inputs.eq(0).focus();
    }
    $('.clear_show input').attr('onkeyup','this.value=this.value.replace(/[^0-9-]+/,"")');
}));
function checkType(dom){
    stopEvent(event)
    //当前时间
    var date=new Date;
    var year = date.getFullYear();
    var month  = date.getMonth()+1;
    var day  = date.getDate();
    var hour  = date.getHours();
    var min = date.getMinutes()
    $('.clear_time p span .radio_b').css('background','white');
    $('.clear_time p span label').css('color','#dbdbdb');
    $(dom).find('.radio_b').css('background','url(../img/radio_chicked.png) no-repeat center');
    $(dom).find('.radio_b').css('background-size','15px 15px');
    $(dom).find('label').css('color','#888');
    var id = $(dom).find('label').attr('class');
    var value = $(dom).parents('.clear_time').prev().attr('data-value');
    var inputs = '';
    var arr = '';
    if(id == 'M'){
        $(dom).parent().next().html('<input type="tel" maxlength="2" class="theDay" style="width: 50px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 50px;"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 50px;"/> 分')
        cache.type = '每月';
        check(cache.type)
        inputs = $(dom).parent().next().find('input');
        if(value == undefined){
            inputs.val('0'+1)
        }else if(value.split(' ').length == 6 && value.split(' ').indexOf('*') != -1){
            arr = value.split(' ');
            inputsPush(inputs,2,arr[1])//分
            inputsPush(inputs,1,arr[2])//时
            inputsPush(inputs,0,arr[3])//日
        }else{
            inputs.val('0'+1)
        }
        inputs.eq(0).focus();
    }
    if(id == 'Y'){
        $(dom).parent().next().html('<input type="tel" maxlength="2" class="theMon" style="width: 88px;"/> 月 <input type="tel" maxlength="2" class="theDay" style="width: 88px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 88px;margin-top: 5px"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 88px;margin-top: 5px"/> 分')
        cache.type = '每年';
        check(cache.type);
        inputs = $(dom).parent().next().find('input');
        if(value == undefined){
            inputs.val('0'+1)
        }else if(value.split(' ').length == 6 && value.split(' ').indexOf('*') == -1){
            arr = value.split(' ');
            inputsPush(inputs,3,arr[1])//分
            inputsPush(inputs,2,arr[2])//时
            inputsPush(inputs,1,arr[3])//日
            inputsPush(inputs,0,arr[4])//月
        }else{
            inputs.val('0'+1)
        }
        inputs.eq(0).focus();
    }
    if(id == 'O'){
        $(dom).parent().next().html('<input type="tel" maxlength="4" class="theYear" style="width: 50px;"/> 年 <input type="tel" maxlength="2" class="theMon" style="width: 50px;"/> 月 <input type="tel" maxlength="2" class="theDay" style="width: 50px;"/> 日 <input type="tel" maxlength="2" class="theMin" style="width: 88px;margin-top: 5px"/> 时 <input type="tel" maxlength="2" class="theSec"style="width: 88px;margin-top: 5px"/> 分')
        cache.type = '一次性';
        check(cache.type);
        inputs = $(dom).parent().next().find('input');
        if(value == undefined){
            inputsPush(inputs,4,min)//分
            inputsPush(inputs,3,hour)//时
            inputsPush(inputs,2,day)//日
            inputsPush(inputs,1,month)//月
            inputsPush(inputs,0,year) //年
        }else if(value.split(' ').length == 7){
            arr = value.split(' ');
            inputsPush(inputs,4,arr[1])//分
            inputsPush(inputs,3,arr[2])//时
            inputsPush(inputs,2,arr[3])//日
            inputsPush(inputs,1,arr[4])//月
            inputsPush(inputs,0,arr[6]) //年
        }else{
            inputsPush(inputs,4,min)//分
            inputsPush(inputs,3,hour)//时
            inputsPush(inputs,2,day)//日
            inputsPush(inputs,1,month)//月
            inputsPush(inputs,0,year) //年
        }
        inputs.eq(0).focus();
    }
    $('.clear_show input').attr('onkeyup','this.value=this.value.replace(/[^0-9-]+/,"")');
}
function GetDateStr(AddDayCount) { //获取前后日期  AddDayCount：-1 昨天 AddDayCount：1 明天 0 今天 2 后天
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    return y+"-"+m+"-"+d;
}
$("#clear_time,#remind").on("click",".clear_time_btn", function (e) {
    stopEvent(e);
    var type = cache.type;
    var val = '';
    var data_value = '';
    //当前时间
    var date=new Date;
    var y = date.getFullYear();
    var m  = date.getMonth()+1;
    var d  = date.getDate();
    var h  = date.getHours();
    var m_1 = date.getMinutes();
    //获取输入框的值
    var dom = $(this).parent();
    var year = dom.find('.theYear').val();
    var mon  = dom.find('.theMon').val();
    var day  = dom.find('.theDay').val();
    var min  = dom.find('.theMin').val();  //小时
    var sec  = dom.find('.theSec').val();  //分钟
    //判断是否是年
    if(year != undefined && year == ''|| year <= y ){
        year = y;
        dom.find('.theYear').val(year);
        if(mon == '' ||mon <= m ){
            mon = m;
            dom.find('.theMon').val(mon);
            if(day == '' || day <= d){
                day = d;
                dom.find('.theDay').val(day);
                if(min == ''|| min <= h ){
                    min = h;
                    dom.find('.theMin').val(min);
                    //if(sec == '' || sec <= m_1){
                    //    art.dialog({
                    //        time: 1,
                    //        lock: true,
                    //        cancel: false,
                    //        content: '不可小于当前时间'
                    //    });
                    //    $('.clear_show input:last').focus();
                    //    return
                    //}
                }
            }
        }
        var regDay = checkDate(day,30,1);
        var regMin = checkDate(min,23,0);
        var tegSec = checkDate(sec,60,0);
        var regMon = checkDate(mon,12,1);
        var select_date=year+"-"+regMon+"-"+regDay+" "+regMin+":"+tegSec;
        select_date=new Date(select_date).getTime();
        var newDay=new Date(GetDateStr(1)).getTime();
        if(select_date<newDay){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: '不可小于明天时间'
            });
            $('.clear_show input:last').focus();
            return
        }
    }
    if(mon  !=undefined) {
        mon = checkDate(mon,12,1);
    }
    day = checkDate(day,30,1);
    min = checkDate(min,23,0);
    sec = checkDate(sec,60,0);
    if(type == '每月'){
        val+= day;
        val+= '日 ';
        val+= min;
        val+= ' : ';
        val+= sec ;
        data_value = '00'+ ' '+sec+' '+min+' '+day+' '+'*'+' '+'?'
    }
    if(type == '每年'){
        val+= mon;
        val+= '月';
        val+= day;
        val+= '日 ';
        val+= min;
        val+= ' : ';
        val+= sec ;
        data_value = '00'+ ' '+sec+' '+min+' '+day+' '+mon+' '+'?'
    }
    if(type == '一次性'){
        val+= year;
        val+= '年';
        val+= mon;
        val+= '月';
        val+= day;
        val+= '日 ';
        val+= min;
        val+= ' : ';
        val+= sec ;
        data_value = '00'+ ' '+sec+' '+min+' '+day+' '+mon+' '+'?'+' '+year
    }
    type = type!='一次性' ? type:'';
    dom.prev().val(type+ ' '+val);
    dom.prev().attr('data-value',data_value);
    dom.hide();
});
//    所有会员/筛选会员
$("#allVip").click(function(){
    $('#drop_down .select_icon').removeClass('select_icon_check');
    $(this).find(".select_icon").addClass("select_icon_check");
    $('#custom_vip_list').css('display','none');
    $('#add_select').css('background-color','#ccc');
});
$("#screenVip").click(function(){
    $('#drop_down .select_icon').removeClass('select_icon_check');
    $(this).find(".select_icon").addClass("select_icon_check");
    $('#add_select').css('background-color','#6dc1c8');
    $('#custom_vip_list').css('display','block');
});
//提醒弹窗
function editMsg(dom){
    var val = $(dom).prev().find("input").val();
    $(dom).prev().find('ul').hide();
    $('.clear_time').hide();
    if(val == "微信模板消息"){
        $("#wxTemplet").show();
        if($(dom).attr('data-vale') != undefined){
            var message = JSON.parse($(dom).attr('data-vale'));
            $("#wxTemplet .wxTitle").val(message.title);
            $("#wxTemplet .edit_content").val(message.info_content);
            $("#wxTemplet .wxUrl").val(message.details_url);
            $("#wxTemplet .imgBox").html("<img src='"+message.picture_url+"'>");
        }
    }else {
        $("#messageTemplet").show();
        if($(dom).attr('data-vale') != undefined){
            $('.edit_content').val($(dom).attr('data-vale'));
        }else{
            $('.edit_content').val("")
        }
    }
    $('.edit_footer div').unbind('click').bind('click',(function(){
        if($(this).attr('class') == 'edit_footer_close'){
            $(".edit_message").hide();
        }
        if($(this).attr('class') == 'edit_footer_save' && val == "短信消息"){
            $(dom).attr('data-vale', $('.edit_content').val());
            $("#messageTemplet").hide();
        }else if($(this).attr('class') == 'edit_footer_save' && val == "微信模板消息"){
            var title=$("#wxTemplet .wxTitle").val();//推送标题
            var url=$("#wxTemplet .wxUrl").val();//页面链接
            var img=$("#wxTemplet .imgBox img").attr("src");//图片
            var info_content=$("#wxTemplet .edit_content").val();//内容
            if(title==""||url==""||info_content==""||img==""||img==undefined){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    zIndex:10002,
                    content:"请将微信内容填写完整"
                });
                return
            }
            var content = {
                "title":title,
                "info_content":info_content,
                "details_url":url,
                "picture_url":img
            };
            $(dom).attr('data-vale', JSON.stringify(content));
            $("#wxTemplet").hide();
        }
    }));
}
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
            code: "",
            name: ""
        },
        staff: {
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
$('#add_select').click(function () {
    var val = $(this).css('background-color');
    if(val != 'rgb(204, 204, 204)'){
        $(".select_box").css({
            height: $(window).height() - 130 + "px"
        });
        $(".select_box_wrap").show();
        $(".select_box_wrap .select_box").css({"top":"100px","left":"50%"});
        $(".select_box .center_item_group .item_list").html("");
        $(".select_box .center_item_group").hide();
        $(".select_box .selected_list").html("");
        if(_param["screen"]!==undefined && _param["screen"].length>0){
            $(".notSelect").hide();
            if(select_type=="content_basic"){
                $("#content_high .notSelect").show();
                $(".tabs_head>label:first-child").trigger("click");
            }else{
                $("#content_basic .notSelect").show();
                $(".tabs_head>label:last-child").trigger("click");
            }
            fuzhiSelect();
        }else{
            if($(".select_box .tabs_head").css("visibility")!="hidden"){
                $(".tabs_head>label:first-child").trigger("click");
            } else{
                //                if(showAdvance && !showBasic){
                //                    $(".tabs_head>label:last-child").trigger("click");
                //                }
                //                if(!showAdvance && showBasic){
                //                    $(".tabs_head>label:first-child").trigger("click");
                //                }
            }
            $(".notSelect").show();
        }
        $("#content_high .item_group").find(".group_arrow").css({
            animation: "none"
        });
        $(".select_box_wrap").css("bottom",$(window).height()-$(document).height());
        //$('#screen_wrapper').show();
    }
});
//一次性清理时间不能小于当前时间
function check(type){
    var date=new Date;
    var year=date.getFullYear();
    var mon = date.getMonth()+1;
    var day = date.getDate();
    var hours = date.getHours();
    var min = date.getMinutes();
    $('.clear_show input').focus().blur(function () {
        var input = $('.clear_show input');
        var theClass = $(this).attr('class');
        var theVal = $(this).val().trim();;
        if(type == '每月' || type == '每年'){
            if(theClass == 'theDay' && theVal!=''){
                if(theVal>30){
                   $(this).val(30)
                }else if(theVal<1){
                    $(this).val(1)
                }
            }else if(theClass == 'theMin'&& theVal!=''){
                if(theVal>23){
                    $(this).val(23)
                }else if(theVal<0){
                    $(this).val(0)
                }
            }else if(theClass == 'theSec'&& theVal!=''){
                if(theVal>59){
                    $(this).val(59)
                }else if(theVal<0){
                    $(this).val(0)
                }
            }else if(type == '每年'){
                if(theClass == 'theMon'&& theVal!=''){
                    if(theVal>12){
                        $(this).val(12)
                    }else if(theVal<1){
                        $(this).val(1)
                    }
                }
            }
        }else if(type == '一次性'){
            input.each(function(){
                if($(this).val() != ''){
                    var val = $(this).attr('class')
                    if(val == 'theYear'){
                        $(this).val().length < 4 ? $(this).val(year) :'';
                        $(this).val() < year ? $(this).val(year) :'';
                    }else if(val == 'theMon'){
                        if(input.eq(0).val() == year){
                            $(this).val() < mon ? $(this).val(mon) :'';
                            $(this).val() >12 ? $(this).val(12) :'';
                        }else{
                            $(this).val() >12 ? $(this).val(12) :'';
                        }
                    }else if(val == 'theDay'){
                        if(input.eq(0).val() == year && input.eq(1).val() == mon){
                            $(this).val() < day ? $(this).val(day) :'';
                            $(this).val() >30 ? $(this).val(30) :'';
                        }else{
                            $(this).val() >30 ? $(this).val(30) :'';
                        }
                    }else if(val == 'theMin'){
                        if(input.eq(0).val() == year && input.eq(1).val() == mon && input.eq(2).val() == day){
                            $(this).val() < hours ? $(this).val(hours) :'';
                            $(this).val() >23 ? $(this).val(23) :'';
                        }else{
                            $(this).val() >23 ? $(this).val(23) :'';
                        }
                    }else if(val == 'theSec'){
                        if(input.eq(0).val() == year && input.eq(1).val() == mon && input.eq(2).val() == day && input.eq(3).val() == hours){
                            $(this).val() < min ? $(this).val(min) :'';
                            $(this).val() >59 ? $(this).val(59) :'';
                        }else{
                            $(this).val() >59 ? $(this).val(59) :'';
                        }
                    }
                }
            });
        }

    });
}
function stopEvent(event){ //阻止冒泡事件
    //取消事件冒泡
    var e=arguments.callee.caller.arguments[0]||event; //若省略此句，下面的e改为event，IE运行可以，但是其他浏览器就不兼容
    if (e && e.stopPropagation) {
        // this code is for Mozilla and Opera
        e.stopPropagation();
    } else if (window.event) {
        // this code is for IE
        window.event.cancelBubble = true;
    }
}
function getNowFormatDate() {//获取当前日期
    var date = new Date();
    var year = date.getFullYear();
    var H=date.getHours();
    var M=date.getMinutes();
    var S=date.getSeconds();
    var m=date.getMilliseconds();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = ""+year+month+strDate+H+M+S+m;
    return currentdate;

}
//微信模版上传图片
function uploadOSS() {
    var _this=this;
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('imgInput').addEventListener('change', function (e) {
        var file = e.target.files[0];
        var time=_this.getNowFormatDate();
        var corp_code=$("#OWN_CORP").val();
        var fileSize=0;
        var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
        //console.log(isIE);
        if (isIE) {
            var filePath = e.value;
            var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
            var files = fileSystem.GetFile (filePath);
            fileSize = files.Size;
        }else {
            fileSize = e.target.files[0].size;
        }
        if(fileSize/1024/1024>5){//限制大小为5M
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请上传小于5M的图片"
            });
            $("#imgInput").val("");
            return ;
        }
        whir.loading.add("上传中,请稍后...",0.5);
        $("#mask").css("z-index","10002");
        var storeAs='Album/Vip/iShow/'+corp_code+'_'+'_'+time+'.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
            addLogo(url);
            $("#imgInput").val("");
        }).catch(function (err) {
            //console.log(err);
        });
    });
}
function addLogo(url) {//添加logo节点
    var img="<img src='"+url+"'>";
    $("#imgBox").html(img);
    whir.loading.remove();
}
function showMsgType_0(dom){
    stopEvent(dom);
    $('.clear_time').hide();
    $('.edit_frame').hide();
    $('.once .drop_down').find('ul').hide();
    $(dom).attr('clickValue','true');
    $(dom).next().find('li').attr('onclick','toPre_1(this)');
}
function showMsgType(dom){
    $('.clear_time').hide();
    $('.edit_frame').hide();
    $(dom).next().toggle();
    ////***********************
    var display = $(dom).next().css('display');
    if(display == 'block' || display == ''){
        $('.once .drop_down').find('ul').attr('isOpen',false);
        $(dom).next().attr('isOpen',true);
        var doms_1 = $('.once .drop_down').find('ul');
        for(i = 0;i <doms_1.length;i++){
            var isOpen = $(doms_1[i]).attr('isOpen');
            if(isOpen != 'true'){
                $(doms_1[i]).hide();
            }else if(isOpen == 'true'){
                $(doms_1[i]).show();
            }
        }
        //***********************
        $(dom).mouseover(function(){
            $(dom).next().attr('isOpen',true)
        });
        $(dom).mouseout(function(){
            $(dom).next().attr('isOpen',false)
            //对弹窗的标记
            var doms = $('.once .drop_down').find('ul')
            for(i = 0; i<doms.length; i++){
                doms[i].onmouseover = function(){
                    for(i = 0; i<doms.length; i++){
                        doms[i].onmouseover = function(){
                            $(this).attr('isOpen',true);
                        }
                        doms[i].onmouseout = function(){
                            $(this).attr('isOpen',false);
                        }
                    }
                }
            }
        })
    }else if(display == 'none'){
        $('.once .drop_down').find('ul').attr('isOpen',false)
    }

    $(dom).next().find('li').attr('onclick','toPre_1(this)');
}
function toPre_1(dom){
    if($(dom).text()=="微信模板消息"){
        $(dom).parents(".item_1").next().removeAttr("onclick");
        $(dom).parents(".item_1").next().addClass("isWx");
    }else if($(dom).text()=="短信消息"){
        $(dom).parents(".item_1").next().removeClass("isWx")
        $(dom).parents(".item_1").next().attr("onclick","editMsg(this)")
    }
    $(dom).parent().prev().val($(dom).text());
    $(dom).parent().hide();
}
//判断输入内容是否小于10
function p(s) {
    return s < 10 ? '0' + s: s;
}
//对所有input进行填充，inputs是输入框，i是第几个，data是填充内容
function inputsPush(inputs,i,data){
    inputs.eq(i).val(data);
}
//清空某个dom属性，value是属性名称
function domsVal(doms,value){
    for (i = 0; i < doms.length; i++) {
        $(doms[i]).attr(value, '');
    }
}
//日期时间格式检查
function checkDate(value,max,min){
    if(value == 0 || value == ''){
        value = min == 1 ? '0'+1 :'00'
    }else if(value.length < 2){
        value = '0'+value
    }else if(value > max){
        value = max;
    }
    return value
}
//获取公众号
function getWxList(corp_code){
    var param={};
    param["corp_code"]=corp_code;
    oc.postRequire("post","/corp/selectWx","0",param, function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list = msg.list;
            var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-value="${msg}">${msg}</li>';
            var html = '';
            for(i=0;i<list.length;i++){
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${id}',list[i].app_id);
                nowHTML = nowHTML.replace('${usermane}',list[i].app_user_name);
                nowHTML = nowHTML.replace('${msg}',list[i].app_name);
                nowHTML = nowHTML.replace('${msg}',list[i].app_name);
                html+=nowHTML;
            }
            $('#vipPublic').html(html);
            $('#vipPublic li').click(function () {
                $('#vipCode').val($(this).text());
                $('#vipCode').attr('data-app_id',$(this).attr('data-id'));
                $('#vipCode').attr('app_user_name',$(this).attr('data-username'));
            });

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
window.onload = function () {
    //隐藏第一条提醒的删除删除按钮
    $('.icon_action_delete').eq(0).hide();
    //设置编辑目标会员-筛选会员-输入框背景颜色
    var code0 = setInterval(function (){
        if ($(".pre_title label").text() == "编辑积分清零") {
            $('#custom_vip_list input').css('background-color', '#dfdfdf')
        }
        var $dom = $('#custom_vip_list div')
        if($dom.length < 1) {
            $('#custom_vip_list').html('<p style="text-align: center;height: 50px;line-height: 50px;margin: 0" id="p">暂无条件</p>')
        }else{
        }
    },1);
    //筛选会员-切换选项卡左上角出现额外的日期弹窗
    var code1 = setInterval(function() {
        var doms = $('.laydate_box');
        if(doms.length > 1){
            doms.eq(1).remove();
            doms.show();
        }
    }, 1);
    window.onunload = function() {
        clearInterval(code0);
        clearInterval(code1);
    }
};
$("#MESSAGE_TYPE").blur(function(){
    var val=parseInt($(this).val().trim());
    var reg=/^[0-9]*$/;
    if(val!="" && !reg.test(val)){
        $(this).val("");
        return
    }
    $(this).val(val);
    if(val>24 || val <0){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "时间区间为0到24"
        });
        $(this).val("")
    }

});
$("#remind").on("blur",".item_1 .input_select",function(){
    var ul = $(this).parent().find("ul");
    setTimeout(function(){
        ul.hide();
    },200);
});
$("#cancel,#X").click(function(){
    $("#tk").hide();
});