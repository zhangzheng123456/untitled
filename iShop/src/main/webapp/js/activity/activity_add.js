var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var staff_num = 1;//分配员工初始page
var area_num = 1;
var shop_num = 1;
var staff_next=false;//下一页标志
var value = "";//收索的关键词
var param={};
var _param={};
var isscroll=false;
var count = "";//分配会员数量
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
(function (root, factory) {
    root.area = factory();
}(this, function () {
    var areajs = {};
    areajs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    areajs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    areajs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    areajs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    areajs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    areajs.bindbutton = function () {
        $("#save").click(function () {
            if (areajs.firstStep()) {
                function getContent() {
                    var arr = [];
                    arr.push(UE.getEditor('editor').getContent());
                    return arr.join("\n");
                }
                function getPlainTxt() {
                    var arr = [];
                    arr.push(UE.getEditor('editor').getPlainTxt());
                    return arr.join("\n");
                }
                var reg = /<img[^>]*>/gi;;
                function imge_change() {
                    var  i=0;
                    return function img_change(){
                        i++;
                        return i;
                    }
                }
                var img_c=imge_change();
                var nr= getContent().replace(reg,function () {
                    var i=img_c();
                    return getPlainTxt().match(reg)[i-1];
                });
                var OWN_CORP = $("#OWN_CORP").val();
                var activity_theme = $("#activity_theme").val();
                if(activity_theme == ""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "活动主题不能为空"
                    });
                    return ;
                }
                var execution_input = $("#execution_input").val();
                var start_time = $("#start").val();
                var end_time = $("#end").val();
                if(execution_input.indexOf("任务")>-1&&(start_time==""||end_time=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "活动时间不能为空"
                    });
                    return ;
                }
                var target_vip = {};
                var type = $("#target_vip").attr("data-type");
                if(type == ""||type == undefined){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "目标会员不能为空"
                    });
                    return ;
                }else if(type == "1"){
                    target_vip["type"] = "1";
                    target_vip["area_code"] = $("#target_vip").attr("data-areacode");
                    target_vip["brand_code"] = $("#target_vip").attr("data-brandcode");
                    target_vip["store_code"] = $("#target_vip").attr("data-shopcode");
                    target_vip["user_code"] = $("#target_vip").attr("data-usercode");
                }else {
                    target_vip["type"] = "2";
                    target_vip["vips"] = $("#target_vip").attr("data-vips");
                }
                var executor = [];
                if($(".executor").css("display") !=="none"){
                    var code = $("#executor").attr("data-code");
                    var name = $("#executor").attr("data-name");
                    if(code == "" || code == undefined){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "执行范围不能为空"
                        });
                        return ;
                    }else {
                        code = code.split(",");
                        name = name.split(",");
                        for(var i=0;i<code.length;i++){
                            var param = {
                                "store_code":code[i],
                                "store_name":name[i]
                            }
                            executor.push(param);
                        }
                    }
                }
                var send_title = $("#send_title").val();
                var summary = $("#summary").val();
                if(execution_input.indexOf("微信")>-1&&(send_title==""||summary=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "推送标题和简介不能为空"
                    });
                    return ;
                }
                var task_title = $("#task_title").val();
                var task_dec = $("#task_dec").val();
                if(execution_input.indexOf("任务")>-1&&(task_title==""||task_dec=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "任务标题和描述不能为空"
                    });
                    return ;
                }
                var short_msg = $("#short_msg").val();
                if(execution_input.indexOf("系统")>-1&&short_msg==""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "短信正文不能为空"
                    });
                    return ;
                }
                var outer_link = $("#outer_link").val();
                if(execution_input.indexOf("社交")>-1&& outer_link == ""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "外部链接不能为空"
                    });
                    return ;
                }
                var activity_content = nr;
                var create_link = $("#src_input").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/activity/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "activity_theme": activity_theme,
                    "run_mode": execution_input,
                    "start_time": start_time,
                    "end_time": end_time,
                    "target_vips": target_vip,
                    "operators":executor,
                    "task_code":"",
                    "task_title": task_title,
                    "task_desc": task_dec,
                    "msg_info": short_msg,
                    "wechat_title": send_title,
                    "wechat_desc": summary,
                    "activity_content": activity_content,
                    "activity_url": outer_link,
                    "content_url": create_link,
                    "isactive": ISACTIVE
                };
                areajs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#edit_save").click(function () {
            if (areajs.firstStep()) {
                function getContent() {
                    var arr = [];
                    arr.push(UE.getEditor('editor').getContent());
                    return arr.join("\n");
                }
                function getPlainTxt() {
                    var arr = [];
                    arr.push(UE.getEditor('editor').getPlainTxt());
                    return arr.join("\n");
                }
                var reg = /<img[^>]*>/gi;;
                function imge_change() {
                    var  i=0;
                    return function img_change(){
                        i++;
                        return i;
                    }
                }
                var img_c=imge_change();
                var nr= getContent().replace(reg,function () {
                    var i=img_c();
                    return getPlainTxt().match(reg)[i-1];
                });
                var ID=sessionStorage.getItem("id");
                var OWN_CORP = $("#OWN_CORP").val();
                var activity_theme = $("#activity_theme").val();
                if(activity_theme == ""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "活动主题不能为空"
                    });
                    return ;
                }
                var execution_input = $("#execution_input").val();
                var start_time = $("#start").val();
                var end_time = $("#end").val();
                if(execution_input.indexOf("任务")>-1&&(start_time==""||end_time=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "活动时间不能为空"
                    });
                    return ;
                }
                var target_vip = {};
                var type = $("#target_vip").attr("data-type");
                if(type == ""||type == undefined){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "目标会员不能为空"
                    });
                    return ;
                }else if(type == "1"){
                    target_vip["type"] = "1";
                    target_vip["area_code"] = $("#target_vip").attr("data-areacode");
                    target_vip["brand_code"] = $("#target_vip").attr("data-brandcode");
                    target_vip["store_code"] = $("#target_vip").attr("data-shopcode");
                    target_vip["user_code"] = $("#target_vip").attr("data-usercode");
                }else {
                    target_vip["type"] = "2";
                    target_vip["vips"] = $("#target_vip").attr("data-vips");
                }
                var executor = [];
                if($(".executor").css("display") !=="none"){
                    var code = $("#executor").attr("data-code");
                    var name = $("#executor").attr("data-name");
                    if(code == "" || code == undefined){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "执行范围不能为空"
                        });
                        return ;
                    }else {
                        code = code.split(",");
                        name = name.split(",");
                        for(var i=0;i<code.length;i++){
                            var param = {
                                "store_code":code[i],
                                "store_name":name[i]
                            }
                            executor.push(param);
                        }
                    }
                }
                var send_title = $("#send_title").val();
                var summary = $("#summary").val();
                if(execution_input.indexOf("微信")>-1&&(send_title==""||summary=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "推送标题和简介不能为空"
                    });
                    return ;
                }
                if(execution_input.indexOf("微信")>-1&&(send_title==""||summary=="")){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "推送标题和简介不能为空"
                    });
                    return ;
                }
                var task_title = $("#task_title").val();
                var task_dec = $("#task_dec").val();
                var short_msg = $("#short_msg").val();
                if(execution_input.indexOf("系统")>-1&&short_msg==""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "短信正文不能为空"
                    });
                    return ;
                }
                var outer_link = $("#outer_link").val();
                if(execution_input.indexOf("社交")>-1&&outer_link == ""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "外部链接不能为空"
                    });
                    return ;
                }
                var activity_content = nr;
                var create_link = $("#src_input").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/activity/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "id":ID,
                    "corp_code": OWN_CORP,
                    "activity_theme": activity_theme,
                    "run_mode": execution_input,
                    "start_time": start_time,
                    "end_time": end_time,
                    "target_vips": target_vip,
                    "operators":executor,
                    "task_code":"",
                    "task_title": task_title,
                    "task_desc": task_dec,
                    "msg_info": short_msg,
                    "wechat_title": send_title,
                    "wechat_desc": summary,
                    "activity_content": activity_content,
                    "activity_url": outer_link,
                    "content_url": create_link,
                    "isactive": ISACTIVE
                };
                areajs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    areajs.ajaxSubmit = function (_command, _params, opt) {
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if (_command == "/activity/add") {
                    sessionStorage.setItem("id", data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/activity/activity_edit.html");
                }
                if (_command == "/activity/edit") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "保存成功"
                    });
                    setTimeout(function () {
                        window.location.reload();
                    },500);
                }
                // $(window.parent.document).find('#iframepage').attr("src","/area/area.html");
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
        if (areajs['check' + command]) {
            if (!areajs['check' + command].apply(areajs, [obj, hint])) {
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
        areajs.bindbutton();
    }
    var obj = {};
    obj.areajs = areajs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function () {
    window.area.init();//初始化
    if ($(".pre_title label").text() == "编辑活动") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        whir.loading.add("", 0.5);
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            var data = JSON.parse(data);
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                if (action.length <= 0) {
                    $("#edit_save").remove();
                }
            }
        });
        var _params = {};
        _params["id"] = id;
        var _command = "/activity/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                    msg = JSON.parse(msg.activityVip);
                console.log(msg);
                var val = msg.run_mode;
                var target_vips = JSON.parse(msg.target_vips);
                var executor = JSON.parse(msg.operators);
                var store_code = "";
                var store_name = "";
                for(var i=0;i<executor.length;i++){
                    var code = executor[i].store_code;
                    var name = executor[i].store_name;
                    if(i<executor.length-1){
                        store_code+=code+",";
                        store_name+=name+",";
                    }else {
                        store_code+=code;
                        store_name+=name;
                    }
                }
                $("#executor").val("已选"+executor.length+"个");
                $("#executor").attr("data-code",store_code);
                $("#executor").attr("data-name",store_name);
                if(target_vips.type == "2"){
                    $("#target_vip").attr("data-type","2");
                    $("#target_vip").attr("data-vips",target_vips.vips);
                    var vips = target_vips.vips.split(",");
                    $("#target_vip").val("已选"+vips.length+"个");
                }else if(target_vips.type == "1"){
                    $("#target_vip").attr("data-type","1");
                    $("#target_vip").val("已选"+msg.target_vips_count+"个");
                    $("#target_vip").attr("data-areacode",target_vips.area_code);
                    $("#target_vip").attr("data-brandcode",target_vips.brand_code);
                    $("#target_vip").attr("data-shopcode",target_vips.store_code);
                    $("#target_vip").attr("data-usercode",target_vips.user_code);
                }
                $("#activity_theme").val(msg.activity_theme);
                $("#execution_input").val(msg.run_mode);
                $("#start").val(msg.start_time);
                $("#end").val(msg.end_time);
                $("#task_title").val(msg.task_title);
                $("#task_dec").val(msg.task_desc);
                $("#short_msg").val(msg.msg_info);
                $("#send_title").val(msg.wechat_title);
                $("#summary").val(msg.wechat_desc);
                //将读取到的活动详情保存在本地
                sessionStorage.setItem('activity_content',msg.activity_content)
                ue.ready(function() {
                    ue.body.innerHTML=msg.activity_content;
                });
                $("#outer_link").val(msg.activity_url);
                $("#src_input").val(msg.content_url);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                $("#src_input").attr("data-code",msg.activity_vip_code);//生成链接code
                var input = $(".checkbox_isactive").find("input")[0];
                if (msg.isactive == "Y") {
                    input.checked = true;
                } else if (msg.isactive == "N") {
                    input.checked = false;
                }
                getcorplist(msg.corp_code);
                execution_change(val);
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
    }
    $(".areaadd_oper_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/area/area.html");
    });
    $("#close").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/activity/activity.html");
    });
});
//日期插件调用
var start = {
    elem: '#start',
    format: 'YYYY-MM-DD',
    min: laydate.now(), //设定最小日期为当前日期
    max: '2099-06-16 23:59:59', //最大日期
    istime: true,
    istoday: false,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#end',
    format: 'YYYY-MM-DD',
    min: laydate.now(),
    max: '2099-06-16 23:59:59',
    istime: true,
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);
function getcorplist(a) {
    //获取所属企业列表
    oc.postRequire("post", "/user/getCorpByUser", "", "", function (data) {
        console.log(data);
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            console.log(msg);
            var index = 0;
            var corp_html = '';
            var c = null;
            for (index in msg.corps) {
                c = msg.corps[index];
                corp_html += '<option value="' + c.corp_code + '">' + c.corp_name + '</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if (a !== "") {
                $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
            }
            $('.corp_select select').searchableSelect();
            $('.corp_select .searchable-select-input').keydown(function (event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    var type = $("#target_vip").attr("data-type");
                    if(type == "2"){
                        $("#target_vip").attr("data-type","");
                        $("#target_vip").attr("data-vips","");
                        $("#target_vip").val("");
                    }else if(type == "1"){
                        $("#target_vip").attr("data-type","");
                        $("#target_vip").val("");
                        $("#target_vip").attr("data-areacode","");
                        $("#target_vip").attr("data-brandcode","");
                        $("#target_vip").attr("data-shopcode","");
                        $("#target_vip").attr("data-usercode","");
                    }
                    $("#executor").val("");
                    $("#executor").attr("data-code","");
                }
            })
            $('.searchable-select-item').click(function () {
                var type = $("#target_vip").attr("data-type");
                if(type == "2"){
                    $("#target_vip").attr("data-type","");
                    $("#target_vip").attr("data-vips","");
                    $("#target_vip").val("");
                }else if(type == "1"){
                    $("#target_vip").attr("data-type","");
                    $("#target_vip").val("");
                    $("#target_vip").attr("data-areacode","");
                    $("#target_vip").attr("data-brandcode","");
                    $("#target_vip").attr("data-shopcode","");
                    $("#target_vip").attr("data-usercode","");
                }
                $("#executor").val("");
                $("#executor").attr("data-code","");
            })
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
//执行方式变化页面变化
function execution_change(val) {
    if (val == "(任务)电话通知顾客" || val == "(任务)短信通知顾客" || val == "(任务)分享链接到社交平台") {
        if(val == "(任务)分享链接到社交平台"){
            $("#outer_link").prev().css("color","#c26555");
            $("#outer_link").prev().html("外部链接*");
        }else {
            $("#outer_link").prev().css("color","");
            $("#outer_link").prev().html("外部链接");
        }
        $(".task_dec").show();
        $(".executor").show();
        $(".task_title").show();
        $(".send_title").hide();
        $(".summary").hide();
        $(".short_msg").hide();
    } else if (val == "系统短信通知顾客") {
        $("#outer_link").prev().css("color","");
        $("#outer_link").prev().html("外部链接");
        $(".task_dec").hide();
        $(".executor").hide();
        $(".task_title").hide();
        $(".send_title").hide();
        $(".summary").hide();
        $(".short_msg").show();
    } else if (val == "微信公众号推送") {
        $("#outer_link").prev().css("color","");
        $("#outer_link").prev().html("外部链接");
        $(".task_dec").hide();
        $(".task_title").hide();
        $(".executor").hide();
        $(".send_title").show();
        $(".summary").show();
        $(".short_msg").hide();
    }
}
//请求页面数据
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    var param={};
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]=$('#OWN_CORP').val();
    oc.postRequire("post","/vipAnalysis/allVip","",param,function(data){
        console.log(data);
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            console.log(message);
            var list=message.all_vip_list;
            cout=message.pages;
            var pageNum = message.pageNum;
            //var list=list.list;
            superaddition(list,pageNum);
            jumpBianse();
            filtrate="";
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
            var type = $("#target_vip").attr("data-type");
            if(type == "2"){
                var vips = $("#target_vip").attr("data-vips");
                var tr = $("#table tbody tr");
                vips = vips.split(",");
                for(var i=0;i<vips.length;i++){
                    for(var j=0;j<tr.length;j++){
                        if($(tr[j]).children("td:nth-child(2)").attr("data_vip_id") == vips[i]){
                           var input = $(tr[j]).find("input[type='checkbox']")[0];
                            input.checked = true;
                        }
                    }
                }
            }
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
function POST(a,b){
    param["corp_code"]=$('#OWN_CORP').val();;
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
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipScreen","", _param, function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            count=message.count;
            cout=message.pages;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                $("#target_vip_all").show();
                superaddition(list,a);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,a,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    })
}
function superaddition(data, num) {
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
    var judge = '';
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
        var gender = data[i].sex == 'F' ? '女' : '男';
        var tr_vip_id = data[i].vip_id;
        var tr_node = "<tr data-storeid='" + data[i].store_id + "' data-user='" + data[i].user_code + "' id='" + data[i].corp_code + "'>"
            + "</td><td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td data_vip_id='" + tr_vip_id + "'>"
            + data[i].vip_name
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
        $(".table tbody").append(tr_node);
    }
    $("tbody tr").click(function () {
        var input = $(this).find("input")[0];
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            input.checked = false;
        }
    })
    $(".th th:last-child input").removeAttr("checked");
    whir.loading.remove();//移除加载框
};
//生成分页
function setPage(container, count, pageindex, pageSize, c) {
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
            dian(inx, pageSize, c);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize, c);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize, c);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
//点击页码
function dian(a, b) {//点击分页的时候调什么接口
    var corp_code=$('#OWN_CORP').val();
    var area_code =$('#screen_area_num').attr("data-code");//区域
    var brand_code=$('#screen_brand_num').attr("data-code");//品牌
    var store_code=$("#screen_shop_num").attr("data-code");//店铺
    var user_code=$("#screen_staff_num").attr("data-code");//员工
    if (area_code!==""||brand_code!==""||store_code!==""||user_code!=="") {
        _param["corp_code"]=corp_code;
        _param["brand_code"]=brand_code;
        _param["store_code"]=store_code;
        _param["area_code"]=area_code;
        _param["user_code"]=user_code;
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
    } else if (value !== "") {
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a, b);
    }else if( value == "" && area_code==""&&brand_code==""&&store_code==""&&user_code==""){
        GET(a,b);
    }
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var corp_code=$('#OWN_CORP').val();
    var area_code =$('#screen_area_num').attr("data-code");//区域
    var brand_code=$('#screen_brand_num').attr("data-code");//品牌
    var store_code=$("#screen_shop_num").attr("data-code");//店铺
    var user_code=$("#screen_staff_num").attr("data-code");//员工
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "" && area_code==""&&brand_code==""&&store_code==""&&user_code=="") {
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                param["pageNumber"]=inx;
                POST(inx, pageSize);
            } else if (area_code!==""||brand_code!==""||store_code!==""||user_code!=="") {
                _param["corp_code"]=corp_code;
                _param["brand_code"]=brand_code;
                _param["store_code"]=store_code;
                _param["area_code"]=area_code;
                _param["user_code"]=user_code;
                _param["pageSize"] = pageSize;
                _param["pageNumber"]=inx;
                filtrates(inx, pageSize);
            }
        };
    }
});
//全选
function checkAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        console.log(el[i].name);
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = true;
        }
    }
};
//取消全选
function clearAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = false;
        }
    }
};
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
                    GET(inx, pageSize);
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
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
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
});
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
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
});
//放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//区域,员工，店铺，品牌关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_shop").show();
});
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    $(".box_shadow").hide();
});
$("#screen_close_shop").click(function(){
    $("#screen_shop").hide();
    $("#screen_shop .screen_content_l").unbind("scroll");
    $(".box_shadow").hide();
});
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_shop").show();
});
//获取品牌列表
function getbrandlist(a){
    var corp_code = $('#OWN_CORP').val();
    if(a="brand"){
        var searchValue=$("#search_brand").val().trim();
    }else {
        var searchValue=$("#brand_search").val().trim();
    }
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
            $("#vip_screen_brand .screen_content_l ul").append(brand_html_left);
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
function getarealist(a,b){
    if(b == "area"){
        var searchValue=$("#search_area").val().trim();
    }else {
        var searchValue=$("#area_search").val().trim();
    }
    var corp_code = $('#OWN_CORP').val();
    var area_command = "/area/selAreaByCorpCode";
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
            $("#vip_screen_area .screen_content_l ul").append(area_html_left);
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
                });
                $("#vip_screen_area .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    var b="area";
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(area_next){
                            return;
                        }
                        getarealist(area_num,b);
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
function getstorelist(a,b){
    if(b =="shop"){
        var searchValue=$("#search_store").val().trim();
        var area_code =$('#screen_area_num').attr("data-code");
        var brand_code=$('#screen_brand_num').attr("data-code");
    }else {
        var searchValue=$("#store_search").val().trim();
        var area_code =$('#area_num').attr("data-areacode");
        var brand_code=$('#brand_num').attr("data-brandcode");
    }
    var corp_code = $('#OWN_CORP').val();
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
            $("#vip_screen_shop .screen_content_l ul").append(store_html);
            if(!isscroll){
                $("#screen_shop .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    var b="shop";
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num,b);
                    };
                });
                $("#vip_screen_shop .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num);
                    };
                });
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
function getstafflist(a,b){
    if(b=="staff"){
        var searchValue=$('#search_staff').val().trim();
        var area_code =$("#screen_area_num").attr("data-code");
        var brand_code=$("#screen_brand_num").attr("data-code");
        var store_code=$("#screen_shop_num").attr("data-code");
    }else {
        var searchValue=$('#staff_search').val().trim();
        var area_code =$("#area_num").attr("data-code");
        var brand_code=$("#brand_num").attr("data-code");
    }
    var corp_code = $('#OWN_CORP').val();
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
            $("#vip_screen_staff .screen_content_l ul").append(staff_html);
            if(!isscroll){
                $("#screen_staff .screen_content_l ul").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
                });
                $("#vip_screen_staff .screen_content_l ul").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
                });
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
//获取导购
function getuserlist(a,b) {
    var param={};
    var target_vips={};
    param["pageNumber"] = inx;
    param["pageSize"] = 20;
    param["searchValue"] = $("#staff_search").val().trim();
    param["corp_code"] = $('#OWN_CORP').val();
    param["target_vips"] = target_vips;
    if(b=="1"){
        target_vips["type"] = "1";
        target_vips["area_code"] = $("#target_vip").attr("data-areacode");
        target_vips["brand_code"] = $("#target_vip").attr("data-brandcode");
        target_vips["store_code"] = $("#target_vip").attr("data-shopcode");
        target_vips["user_code"] = $("#target_vip").attr("data-usercode");
    }else if(b=="2"){
        target_vips["type"] = "2";
        target_vips["vips"] = $("#target_vip").attr("data-vips");
    }
    whir.loading.add("",0.5);
    oc.postRequire("post","/activity/selUserByVip","",param,function (data) {
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
                $("#screen_staff .screen_content_l ul").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
                });
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
    var area_name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            area_code+=r+",";
            area_name+=h+",";
        }else{
            area_code+=r;
            area_name+=h;
        }
    }
    isscroll=false;
    shop_num=1;
    $("#area_num").attr("data-areacode",area_code);
    $("#area_num").attr("data-name",area_name);
    $("#area_num").val("已选"+li.length+"个");
    $("#screen_area").hide();
    $("#screen_shop").show();
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//点击店铺的确定
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var name = "";
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
    isscroll=false;
    $("#executor").attr("data-name",name);
    $("#executor").attr("data-code",store_code);
    $("#executor").val("已选"+li.length+"个");
    $("#screen_shop").hide();
    $(".box_shadow").hide();
});
//点击品牌的确定
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_code="";
    var brand_name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            brand_code+=r+",";
            brand_name+=h+",";
        }else{
            brand_code+=r;
            brand_name+=h;
        }
    }
    shop_num=1;
    $("#brand_num").attr("data-brandcode",brand_code);
    $("#brand_num").attr("data-name",brand_name);
    $("#brand_num").val("已选"+li.length+"个");
    $("#screen_brand").hide();
    $("#screen_shop").show();
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//点击员工的确定
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var user_code="";
    var phone="";
    var name="";
    console.log(li.length);
    if(li.length == 0){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "没有分配的执行人"
        });
        return ;
    }
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find("input").attr("data-phone");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            user_code+=r+",";
            phone+=p+",";
            name+=h+",";
        }else{
            user_code+=r;
            phone+=p;
            name+=h;
        }
    }
    $("#executor").attr("data-code", user_code);
    $("#executor").attr("data-phone",phone);
    $("#executor").attr("data-name",name);
    $("#screen_staff").hide();
    $("#executor").val("已选"+li.length+"个");
    $(".box_shadow").hide();
});
//筛选区域
$("#area_num").click(function(){
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#area_num").attr("data-areacode");
    var name = $("#area_num").attr("data-name");
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area .screen_content_r ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    if(code!==undefined && code!==""){
        code = code.split(",");
        name = name.split(",");
        var area_html = "";
        for(var i=0;i<code.length;i++){
            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-areaname='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_area .screen_content_r ul").append(area_html);
    }
    getarealist(area_num);
});
$("#shop_area").click(function () {
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#area_num").attr("data-areacode");
    var name = $("#area_num").attr("data-name");
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area .screen_content_r ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    if(code!==undefined && code!==""){
        code = code.split(",");
        name = name.split(",");
        var area_html = "";
        for(var i=0;i<code.length;i++){
            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-areaname='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_area .screen_content_r ul").append(area_html);
    }
    getarealist(area_num);
});
//筛选品牌
$("#brand_num").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#brand_num").attr("data-brandcode");
    var name = $("#brand_num").attr("data-name");
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand .screen_content_r ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    if(code!==undefined && code!==""){
        code = code.split(",");
        name = name.split(",");
        var brand_html = "";
        for(var i=0;i<code.length;i++){
            brand_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-areaname='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_brand .screen_content_r ul").append(brand_html);
    }
    getbrandlist();
});
$("#shop_brand").click(function () {
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#brand_num").attr("data-brandcode");
    var name = $("#brand_num").attr("data-name");
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand .screen_content_r ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    if(code!==undefined && code!==""){
        code = code.split(",");
        name = name.split(",");
        var brand_html = "";
        for(var i=0;i<code.length;i++){
            brand_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-areaname='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_brand .screen_content_r ul").append(brand_html);
    }
    getbrandlist();
});
//分配执行范围
$("#executor").click(function () {
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#executor").attr("data-code");
    var name = $("#executor").attr("data-name");
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop .screen_content_r ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $(".box_shadow").show();
    if(code!==undefined&&code!==""){
        code = code.split(",");
        name = name.split(",");
        var store_html = "";
        for(var i=0;i<code.length;i++){
            store_html+="<li id="+code[i]+"><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-storename='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_shop .screen_content_r ul").append(store_html);
    }
    getstorelist(shop_num);
});
$("#executor_icon").click(function () {
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2-200;
    var tp=(arr[3]-$("#screen_shop").height())/2+63;
    var code = $("#executor").attr("data-code");
    var name = $("#executor").attr("data-name");
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop .screen_content_r ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $(".box_shadow").show();
    console.log(code);
    if(code!==undefined&&code!==""){
        code = code.split(",");
        name = name.split(",");
        var store_html = "";
        for(var i=0;i<code.length;i++){
            store_html+="<li id="+code[i]+"><div class='checkbox1'><input  type='checkbox' value='"+code[i]+"' data-storename='"+name[i]+"' name='test'  class='check'  id='checkboxTowInput"+ i
                + "'/><label for='checkboxTowInput"+i+ "'></label></div><span class='p16'>"+name[i]+"</span></li>";
        }
        $("#screen_shop .screen_content_r ul").append(store_html);
    }
    getstorelist(shop_num);
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
    // $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
    // $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
    // $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
    // $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
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
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        value=this.value.trim();
        $('#screen_area_num').attr("data-code","");
        $('#screen_brand_num').attr("data-code","");
        $("#screen_shop_num").attr("data-code","");
        $("#screen_staff_num").attr("data-code","");
        if(value!==""){
            inx=1;
            param["searchValue"]=value;
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            POST(inx,pageSize);
        }else if(value==""){
            GET(inx,pageSize);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    $('#screen_area_num').attr("data-code","");
    $('#screen_brand_num').attr("data-code","");
    $("#screen_shop_num").attr("data-code","");
    $("#screen_staff_num").attr("data-code","");
    value=$("#search").val().trim();
    if(value!==""){
        inx=1;
        param["searchValue"]=value;
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["funcCode"]=funcCode;
        POST(inx,pageSize);
    }else{
        GET(inx,pageSize);
    }
});
//目标会员保存
$("#target_vip_save").click(function () {
    var list = "";
    var tr = $("tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length == 0){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "没有勾选需要分配的会员"
        });
    }else if(tr.length > 0){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "保存成功"
        });
        setTimeout(function () {
            $(".content").hide();
            $("#page-wrapper").show();
        },500);
        for(var i=0;i<tr.length;i++){
            if(i<tr.length-1){
                list += $(tr[i]).children("td:nth-child(2)").attr("data_vip_id")+",";
            }else {
                list += $(tr[i]).children("td:nth-child(2)").attr("data_vip_id");
            }
        }
        $("#target_vip").val("已选"+tr.length+"个");
        $("#target_vip").attr("data-type","2");
        $("#target_vip").attr("data-vips",list);
    }
});
$("#target_vip_all").click(function () {
    var execution_input = $("#execution_input").val();
    if(execution_input.indexOf("任务") == -1 && count>500){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "选择的会员不能超过500个"
        });
        return ;
    }else {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "保存成功"
        });
        setTimeout(function () {
            $(".content").hide();
            $("#page-wrapper").show();
        },500);
        $("#target_vip").val("已选"+count+"个");
        $("#target_vip").attr("data-type","1");
        $("#target_vip").attr("data-areacode",$('#screen_area_num').attr("data-code"));
        $("#target_vip").attr("data-brandcode",$('#screen_brand_num').attr("data-code"));
        $("#target_vip").attr("data-shopcode",$("#screen_shop_num").attr("data-code"));
        $("#target_vip").attr("data-usercode",$("#screen_staff_num").attr("data-code"));
    }
});
//执行方式下拉选择
$("#execution li").click(function () {
    var val = $(this).html();
    $(this).addClass("liactive").siblings("li").removeClass("liactive");
    $("#execution_input").val(val);
    execution_change(val);
});
$("#execution_input").click(function () {
    $("#execution").toggle();
});
$(document).click(function (e) {
    if ($(e.target).is("#execution_input")) {
            return ;
    } else {
        $("#execution").hide();
    }
});
//关闭弹出层
$("#cancel").click(close_float);
$("#X").click(close_float);
function close_float() {
    $(".box_shadow").hide();
    $(".tk").hide();
}
//分配目标会员
$("#target_vip").click(function () {
    $(".content").show();
    $("#page-wrapper").hide();
    GET(inx,pageSize);
});
$("#target_vip_icon").click(function () {
    $(".content").show();
    $("#page-wrapper").hide();
    GET(inx,pageSize);
});
//关闭分配目标会员
$("#turnoff").click(function () {
    $(".content").hide();
    $("#page-wrapper").show();
});
//分配目标会员筛选
$("#filtrate").click(function () {
    var arr=whir.loading.getPageSize();
    $("#p").css("height",arr[3]);
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2+63;
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").show();
    $("#p").show();
});
$("#screen_wrapper_close").click(function () {
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//区域显示,关闭
$("#screen_area_num").click(function () {
    $("#vip_screen_area").show();
    $("#screen_wrapper").hide();
    isscroll=false;
    area_num=1;
    var b="area";//标记
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_area").width())/2;
    var tp=(arr[3]-$("#vip_screen_area").height())/2+63;
    $("#vip_screen_area .screen_content_l").unbind("scroll");
    $("#vip_screen_area .screen_content_l ul").empty();
    $("#vip_screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    getarealist(area_num,b);
});
$("#screen_areal").click(function () {
    $("#vip_screen_area").show();
    $("#screen_wrapper").hide();
    var b="area";//标记
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_area").width())/2;
    var tp=(arr[3]-$("#vip_screen_area").height())/2+63;
    $("#vip_screen_area .screen_content_l").unbind("scroll");
    $("#vip_screen_area .screen_content_l ul").empty();
    $("#vip_screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    getarealist(area_num,b);
});
$("#close_area").click(function () {
    $("#vip_screen_area").hide();
    $("#screen_wrapper").show();
});
//区域搜索，确定
$("#search_area_f").click(function () {
    area_num=1;
    isscroll=false;
    var b="area";//标记
    $("#vip_screen_area .screen_content_l").unbind("scroll");
    $("#vip_screen_area .screen_content_l ul").empty();
    getarealist(area_num,b);
});
$("#search_area").keydown(function () {
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        var b="area";//标记
        isscroll=false;
        $("#vip_screen_area .screen_content_l").unbind("scroll");
        $("#vip_screen_area .screen_content_l ul").empty();
        getarealist(area_num,b);
    }
});
$("#que_area").click(function () {
    $("#vip_screen_area").hide();
    $("#screen_wrapper").show();
    var li=$("#vip_screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        if(i<li.length-1){
            area_code+=r+",";
        }else{
            area_code+=r;
        }
    }
    isscroll=false;
    $("#screen_area_num").val("已选"+li.length+"个");
    $("#screen_area_num").attr("data-code",area_code);
});
//品牌显示，关闭
$("#screen_brand_num").click(function () {
    var a="brand";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_brand").width())/2;
    var tp=(arr[3]-$("#vip_screen_brand").height())/2+63;
    $("#vip_screen_brand .screen_content_l ul").empty();
    $("#vip_screen_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_brand").show();
    $("#screen_wrapper").hide();
    getbrandlist(a);
});
$("#screen_brandl").click(function () {
    var a="brand";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_brand").width())/2;
    var tp=(arr[3]-$("#vip_screen_brand").height())/2+63;
    $("#vip_screen_brand .screen_content_l ul").empty();
    $("#vip_screen_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_brand").show();
    $("#screen_wrapper").hide();
    getbrandlist(a);
});
$("#close_brand").click(function () {
    $("#vip_screen_brand").hide();
    $("#screen_wrapper").show();
});
//品牌搜索，确定
$("#search_brand_f").click(function () {
    var a="brand";
    $("#vip_screen_brand .screen_content_l").unbind("scroll");
    $("#vip_screen_brand .screen_content_l ul").empty();
    getbrandlist(a);
});
$("#search_brand").keydown(function () {
    var a="brand";
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $("#vip_screen_brand .screen_content_l").unbind("scroll");
        $("#vip_screen_brand .screen_content_l ul").empty();
        getbrandlist(a);
    }
});
$("#que_brand").click(function () {
    var li=$("#vip_screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        if(i<li.length-1){
            brand_code+=r+",";
        }else{
            brand_code+=r;
        }
    }
    $("#screen_brand_num").attr("data-code",brand_code);
    $("#screen_brand_num").val("已选"+li.length+"个");
    $("#vip_screen_brand").hide();
    $("#screen_wrapper").show();
});
//店铺显示，关闭
$("#screen_shop_num").click(function () {
    isscroll=false;
    shop_num=1;
    var b="shop";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_shop").width())/2;
    var tp=(arr[3]-$("#vip_screen_shop").height())/2+63;
    $("#vip_screen_shop .screen_content_l ul").empty();
    $("#vip_screen_shop").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_shop").show();
    $("#screen_wrapper").hide();
    getstorelist(shop_num,b);
});
$("#screen_shopl").click(function () {
    isscroll=false;
    shop_num=1;
    var b="shop";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_shop").width())/2;
    var tp=(arr[3]-$("#vip_screen_shop").height())/2+63;
    $("#vip_screen_shop .screen_content_l ul").empty();
    $("#vip_screen_shop").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_shop").show();
    $("#screen_wrapper").hide();
    getstorelist(shop_num,b);
});
$("#close_shop").click(function () {
    $("#vip_screen_shop").hide();
    $("#screen_wrapper").show();
});
//店铺搜索，确定
$("#search_store_f").click(function () {
    shop_num=1;
    isscroll=false;
    var b="shop";
    $("#vip_screen_shop .screen_content_l").unbind("scroll");
    $("#vip_screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num,b)
});
$("#search_store").keydown(function () {
    shop_num=1;
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        var b="shop";
        isscroll=false;
        $("#vip_screen_shop .screen_content_l").unbind("scroll");
        $("#vip_screen_shop .screen_content_l ul").empty();
        getstorelist(shop_num,b);
    }
});
$("#que_shop").click(function () {
    var li=$("#vip_screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        if(i<li.length-1){
            store_code+=r+",";
        }else{
            store_code+=r;
        }
    }
    isscroll=false;
    $("#screen_shop_num").attr("data-code",store_code);
    $("#screen_shop_num").val("已选"+li.length+"个");
    $("#vip_screen_shop").hide();
    $("#screen_wrapper").show();
});
//导购显示，关闭
$("#screen_staff_num").click(function () {
    isscroll=false;
    staff_num=1;
    var b="staff";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_staff").width())/2;
    var tp=(arr[3]-$("#vip_screen_staff").height())/2+63;
    $("#vip_screen_staff .screen_content_l ul").empty();
    $("#vip_screen_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_staff").show();
    $("#screen_wrapper").hide();
    getstafflist(staff_num,b);
});
$("#screen_staffl").click(function () {
    isscroll=false;
    staff_num=1;
    var b="staff";
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#vip_screen_staff").width())/2;
    var tp=(arr[3]-$("#vip_screen_staff").height())/2+63;
    $("#vip_screen_staff .screen_content_l ul").empty();
    $("#vip_screen_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#vip_screen_staff").show();
    $("#screen_wrapper").hide();
    getstafflist(staff_num,b);
});
$("#close_staff").click(function () {
    $("#vip_screen_staff").hide();
    $("#screen_wrapper").show();
});
//导购搜索，确定
$("#search_staff_f").click(function () {
    staff_num=1;
    isscroll=false;
    var b="staff";
    $("#vip_screen_staff .screen_content_l").unbind("scroll");
    $("#vip_screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num,b);
});
$("#search_staff").keydown(function () {
    staff_num=1;
    var b="staff";
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        isscroll=false;
        $("#vip_screen_staff .screen_content_l").unbind("scroll");
        $("#vip_screen_staff .screen_content_l ul").empty();
        getstafflist(staff_num,b);
    }
});
$("#que_staff").click(function () {
    var li=$("#vip_screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var user_code="";
    var phone="";
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find("input").attr("data-phone");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            user_code+=r+",";
            phone+=p+",";
            name+=h+",";
        }else{
            user_code+=r;
            phone+=p;
            name+=h;
        }
    }
    $("#screen_staff_num").attr("data-code", user_code);
    $("#vip_screen_staff").hide();
    $("#screen_wrapper").show();
    $("#screen_staff_num").val("已选"+li.length+"个");
});
//分配会员确定
$("#screen_vip_que").click(function () {
    $("#search").val("");
    inx=1;
    var tr= $('#table tbody tr');
    var corp_code=$('#OWN_CORP').val();
    var area_code =$('#screen_area_num').attr("data-code");//区域
    var brand_code=$('#screen_brand_num').attr("data-code");//品牌
    var store_code=$("#screen_shop_num").attr("data-code");//店铺
    var user_code=$("#screen_staff_num").attr("data-code");//员工
    _param["corp_code"]=corp_code;
    _param["brand_code"]=brand_code;
    _param["store_code"]=store_code;
    _param["area_code"]=area_code;
    _param["user_code"]=user_code;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    if(area_code==""&&brand_code==""&&store_code==""&&user_code==""){
        GET(inx,pageSize);
    }
    if(area_code!==""||brand_code!==""||store_code!==""||user_code!==""){
        filtrates(inx,pageSize);
    }
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//点击执行
$("#performance").click(function () {
    var left=($(window).width()-$("#tk").width())/2-200;//弹框定位的left值
    var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
    // $("#edit_save").trigger("click");
    // setTimeout(function () {
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
        $(".box_shadow").show();
        $(".tk").show();
    // },1000);
});
$("#enter").click(function () {
    whir.loading.add("",0.5);//加载等待框
    var id = sessionStorage.getItem("id");
    var param = {
        "id":id
    };
    oc.postRequire("post","/activity/executeActivity","",param,function (data) {
        if(data.code == "0"){
            $(window.parent.document).find('#iframepage').attr("src", "/activity/activity_details.html");
        }else if(data.code == "-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();
            return ;
        }
        whir.loading.remove();
    })
});
//点击生成链接
$("#creat_link").click(function () {
    var code = $("#src_input").attr("data-code");
   $("#src_input").val(window.location.host+"/activity/active_ueditor.html?code="+code);
});

