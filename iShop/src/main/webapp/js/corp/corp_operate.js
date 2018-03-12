var oc = new ObjectControl();
var val = sessionStorage.getItem("key");
val = JSON.parse(val);
var messages= JSON.parse(val.message);
(function (root, factory) {
    root.corp = factory();
}(this, function () {
    var corpjs = {};
    corpjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    corpjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    corpjs.checkPhone = function (obj, hint) {
        var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
        var isMob = isMob = /^((\(\d{2,3}\))|(\d{3}\-))?1[3,4,5,7,8]{1}\d{9}$/;//验证手机号码格式正不正确
        if (!this.isEmpty(obj)) {
            if (isPhone.test(obj) || isMob.test(obj)) {
                this.hiddenHint(hint);
                return true;
            } else {
                this.displayHint(hint, "联系电话格式不正确!");
                return false;
            }
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    corpjs.checkCode = function (obj, hint) {
        var isCode = /^[C]{1}[0-9]{5}$/;
        if (!this.isEmpty(obj)) {
            if (isCode.test(obj)) {
                this.hiddenHint(hint);
                return true;
            } else {
                this.displayHint(hint, "企业编号为必填项，支持以大写C开头必须是5位数字的组合！");
                return false;
            }
        } else {
            this.displayHint(hint);
            return false;
        }
    }
    corpjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    corpjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    corpjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    corpjs.bindbutton = function () {
        $(".corpadd_oper_btn ul li:nth-of-type(1)").click(function () {
            var nameMark = $("#CORPNAME").attr("data-mark");
            var codeMark = $("#OWN_CORP").attr("data-mark");
            if (corpjs.firstStep()) {
                if (nameMark == "N" || codeMark == "N") {
                    if (nameMark == "N") {
                        var div = $("#CORPNAME").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#OWN_CORP").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var CORPID = $("#OWN_CORP").val();
                var WXID = $("#WXID").val();
                var CORPNAME = $("#CORPNAME").val();
                var CORPADDRESS = $("#CORPADDRESS").val();
                var CONTACTS = $("#CONTACTS").val();
                var PHONE = $("#PHONE").val();
                var use_offline=$("#use_offline").val();
                var list=[];
                var len=$(".wx_app").find(".wx_span");
                for(var i=0;i<len.length;i++){
                    var app_id=$(len[i]).find('.WXID').val();
                    var app_name=$(len[i]).find('.AppName').val();
                    var access_key=$(len[i]).find('.access_key').val();
                    if(app_id!==""&&app_name==""){
                        alert("名称不能为空！")
                        return;
                    }
                    var wechat={"app_id":app_id,"app_name":app_name,"access_key":access_key}
                    list.push(wechat);
                }
                var ISACTIVE = "";
                var input = $("#is_active")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/corp/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                whir.loading.add("", 0.5);
                var _params = {
                    "corp_code": CORPID,
                    "app_id": WXID,
                    "corp_name": CORPNAME,
                    "address": CORPADDRESS,
                    "contact": CONTACTS,
                    "phone": PHONE,
                    "isactive": ISACTIVE,
                    "use_offline":use_offline,
                    "wechat":list
                };
                corpjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $(".corpedit_oper_btn ul li:nth-of-type(1)").click(function () {
            var nameMark = $("#CORPNAME").attr("data-mark");
            var codeMark = $("#OWN_CORP").attr("data-mark");
            if (corpjs.firstStep()) {
                if (nameMark == "N" || codeMark == "N") {
                    if (nameMark == "N") {
                        var div = $("#CORPNAME").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#OWN_CORP").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var ID = sessionStorage.getItem("id");
                var avatar="";//头像
                if($("#preview img").attr("data-src").indexOf("http")!==-1){
                    avatar=$("#preview img").attr("data-src");
                }
                if($("#preview img").attr("data-src").indexOf("http")==-1){
                    avatar="";
                }
                var CORPID = $("#OWN_CORP").val();
                var WXID = $("#WXID").val();
                var CORPNAME = $("#CORPNAME").val();
                var CORPADDRESS = $("#CORPADDRESS").val();
                var CONTACTS = $("#CONTACTS").val();
                var PHONE = $("#PHONE").val();
                var use_offline=$("#use_offline").val();
                var list=[];
                var arr=[];
                var len=$(".wx_app").find(".wx_span");
                for(var i=0;i<len.length;i++){
                    var app_id=$(len[i]).find('.WXID').val();
                    var app_name=$(len[i]).find('.AppName').val();
                    var access_key=$(len[i]).find('.access_key').val();
                    arr.push(app_id);
                    arr.sort();
                    for(var j=0;j<arr.length;j++){
                        if(arr[j]==arr[j+1]&&arr[j]!==""){
                            alert("公众号ID不能重复")
                            return;
                        }
                    }
                    if(app_id!==""&&app_name==""){
                        alert("名称不能为空")
                        return;
                    }else if(app_name!==""&&app_id==""){
                        alert("公众号ID不能为空")
                        return;
                    }
                    var wechat={"app_id":app_id,"app_name":app_name,"access_key":access_key}
                    list.push(wechat);
                }
                var a=$('.xingming input');//所属客服
                var cus_user_code="";
                for(var i=0;i<a.length;i++){
                    var u=$(a[i]).attr("data-code");
                    if(i<a.length-1){
                        cus_user_code+=u+",";
                    }else{
                        cus_user_code+=u;
                    }     
                }
                var input = $("#is_active")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/corp/edit";//接口名
                var opt = {//返回成功后的操作s
                    success: function () {

                    }
                };
                var _params = {
                    "id": ID,
                    "avatar":avatar,
                    "corp_code": CORPID,
                    "app_id": WXID,
                    "corp_name": CORPNAME,
                    "address": CORPADDRESS,
                    "contact": CONTACTS,
                    "phone": PHONE,
                    "isactive": ISACTIVE,
                    "use_offline":use_offline,
                    "cus_user_code":cus_user_code,
                    "wechat":list
                };
                whir.loading.add("", 0.5);
                corpjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    corpjs.ajaxSubmit = function (_command, _params, opt) {
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if(_command=="/corp/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/corp/crop_edit.html");
                }
                if(_command=="/corp/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
            } else if (data.code == "-1") {
                // art.dialog({
                //     time: 1,
                //     lock: true,
                //     cancel: false,
                //     content: data.message
                // });
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
        if (corpjs['check' + command]) {
            if (!corpjs['check' + command].apply(corpjs, [obj, hint])) {
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
        corpjs.bindbutton();
    }
    var obj = {};
    obj.corpjs = corpjs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function () {
    window.corp.init();//初始化
    $(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    if ($(".pre_title label").text() == "编辑企业信息") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                if (action.length == 0) {
                    $("#edit_save").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
        var _params = {"id": id};
        var _command = "/corp/select";
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list=msg.cus_user;
                if(msg.avatar!==undefined){
                    if(msg.avatar.indexOf("http")==-1){
                        $("#preview img").attr("src","../img/bg1.png");
                        $("#preview img").attr("data-src","../img/bg1.png");
                    }
                    if(msg.avatar.indexOf("http")!==-1){
                        var date=new Date().getTime();
                        $("#preview img").attr("src",msg.avatar+"?"+date);
                        $("#preview img").attr("data-src",msg.avatar);
                    }
                }
                if(msg.avatar==undefined){
                    $("#preview img").attr("src","../img/bg1.png");
                    $("#preview img").attr("data-src","../img/bg1.png");
                }
                if ($("#preview img").attr("src").indexOf('http') !== -1) {
                    $("#c_logo label").html("更换logo");
                } else {
                    $("#c_logo label").html("上传logo");
                }
                $("#WXID").val(msg.app_id);
                $("#OWN_CORP").val(msg.corp_code);
                $("#OWN_CORP").attr("data-name", msg.corp_code);
                $("#CORPNAME").val(msg.corp_name);
                $("#CORPADDRESS").val(msg.address);
                $("#CONTACTS").val(msg.contact);
                $("#PHONE").val(msg.contact_phone);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                $("#CORPNAME").attr("data-name", msg.corp_name);
                $("#use_offline").val(msg.use_offline);
                selChannelByCorp();
                var wechat=msg.wechats;
                var len=$(".wx_app").find(".wx_span");
                if(wechat.length>0) {
                    for (var i = 0; i < wechat.length; i++) {
                        var is_authorize="";
                        if (wechat[i].is_authorize == "Y") {
                            is_authorize="已授权";
                        }
                        if (wechat[i].is_authorize == "N") {
                            is_authorize="未授权";
                        }
                        $(".wx_app").append('<span class="wx_span" style="display:inline-flex">'
                        + '<input type="text" class="WXID" value=' + wechat[i].app_id + '>'
                        + '<input type="text" class="AppName" value=' + wechat[i].app_name + '>'
                        + '<input type="text" class="access_key" value=' + wechat[i].access_key+ '>'
                        + '<input type="text" disabled="true" value="'+is_authorize+'" style="margin-top: 10px;"><p class="icon-ishop_6-12"onclick="removeselect(this)" style="margin-top: 10px;"></p>'
                        + '</span>')
                    }
                }
                var ul="";
                for(var i=0;i<list.length;i++){
                    ul+="<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+list[i].cus_user_code+"' value='"+list[i].cus_user_name
                     +"'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>";
                }
                $('.xingming').html(ul);
                var input = $("#is_active")[0];
                if (msg.isactive == "Y") {
                    input.checked = true;
                } else if (msg.isactive == "N") {
                    input.checked = false;
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
        });
    }
    $("input[verify='Code']").blur(function () {
        var isCode = /^[C]{1}[0-9]{5}$/;
        var _params = {};
        var corp_code = $(this).val();
        var corp_code1 = $(this).attr("data-name");
        var div = $(this).next('.hint').children();
        if (corp_code !== "" && corp_code !== corp_code1 && isCode.test(corp_code) == true) {
            _params["corp_code"] = corp_code;
            oc.postRequire("post", "/corp/corpCodeExist", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#OWN_CORP").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    $("#OWN_CORP").attr("data-mark", "N");
                    div.addClass("error_tips");
                    div.html("该编号已经存在！");
                }
            })
        }
    })
    $("#CORPNAME").blur(function () {
        var corp_name = $("#CORPNAME").val();
        var corp_name1 = $("#CORPNAME").attr("data-name");
        var div = $(this).next('.hint').children();
        if (corp_name !== "" && corp_name !== corp_name1) {
            var _params = {};
            _params["corp_name"] = corp_name;
            oc.postRequire("post", "/corp/corpNameExist", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#CORPNAME").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    div.html("该名称已经存在！")
                    div.addClass("error_tips");
                    $("#CORPNAME").attr("data-mark", "N");
                }
            })
        }
    })
    jQuery.ajax({
        url: "/app/wechat/authorize",
        type: "post",
        dataType: 'text',
        data: {param: JSON.stringify(_params)},
        success: function (data) {
            if (data) {
                callback(data);
            } else {

            }
        },
        error: function (data) {

        }
    });
    function callback(data) {
        //测试
        var a = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=wx722fb7eaa40020e9&pre_auth_code=" + data + "&redirect_uri=http://wechat.dev.bizvane.com/app/wechat/callback";
        //正式
        // var a = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=wxa6780115cc7c1db5&pre_auth_code=" + data + "&redirect_uri=http://wechat.app.bizvane.com/app/wechat/callback";
        $('#power').html('<a href="' + a + '" target="_parent">打开授权二维码</a>');
    }
    $("#power").click(function(){
        var require_data={};
        var corp_code=$("#OWN_CORP").val();
        var wechat=[];
        require_data["corp_code"]=corp_code;
        var list = [];
        var arr = [];
        var len = $(".wx_app").find(".wx_span");
        for (var i = 0; i < len.length; i++) {
            var app_id = $(len[i]).find('.WXID').val();
            var app_name = $(len[i]).find('.AppName').val();
            var access_key=$(len[i]).find('.access_key').val();
            arr.push(app_id);
            arr.sort();
            for (var j = 0; j < arr.length; j++) {
                if (arr[j] == arr[j + 1] && arr[j] !== "") {
                    alert("公众号ID不能重复");
                    return false;
                }
            }
            if(app_name == "") {
                alert("名称不能为空");
                return false;
            }
            if (app_id == "") {
                alert("公众号ID不能为空");
                return false;
            }
            var wechat = {
                "app_id": app_id,
                "app_name": app_name,
                "access_key":access_key
            }
            list.push(wechat);
        }
        require_data["wechat"]=list;
        var _params = {
            "id":"0",
            "message":require_data
        };
        var a="";
        jQuery.ajax({
            url: "/corp/updateWechat",
            type: "post",
            dataType: 'json',
            data: {param: JSON.stringify(_params)},
            async:false,
            success: function (data) {
                if(data.code=="0"){
                    a=true;
                }else if(data.code=="-1"){
                    a=false;
                    alert(data.message);
                }
            },
            error: function (data) {

            }
        });
        return a;
    })
    if (messages.role_code == "R6000"||messages.role_code == "R5500") {
        $(".corpadd_oper_btn ul li:nth-of-type(2)").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
        });
        $("#edit_close").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
        });
        $("#back_corp").click(function(){
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
        })
    } else {
        $(".corpadd_oper_btn ul li:nth-of-type(2)").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
        });
        $("#back_corp").click(function(){
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
        })
        $("#edit_close").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
        });
    }
});
//短信通道
$(".select_list").on("click","div",function () {
    var text=$(this).text();
    var type=$(this).attr("data-type");
    $(this).addClass("active");
    $(this).siblings("div").removeClass("active");
    $(this).parents(".content_right").find(".select").attr("data-type",type);
    $(this).parents(".content_right").find(".select").val(text);
    $(this).parent(".select_list").hide();
});
//点击input框
$(".content_right").on("click",".select",function () {
    var ul=$(this).parents(".content_right").find(".select_list");
    if(ul.css("display")=="none"){
        ul.show();
    }else{
        ul.hide();
    }
});
//input框失去焦点的时候
$(".content_right").on("blur",".select",function () {
    var ul=$(this).parents(".content_right").find(".select_list");
    setTimeout(function(){
        ul.hide();
    },200);
});
//点击左侧选中
$("#sms_list_content").on("click",".check_left",function () {
    var thinput=$("#checkboxTwoInput0")[0];
    if($(this).find(".checkbox input")[0].checked==true){
        $(this).find(".checkbox input")[0].checked=false;
        thinput.checked=false;
    }else if($(this).find(".checkbox input")[0].checked==false){
        $(this).find(".checkbox input")[0].checked=true;
        if($("#sms_list_content .check_left input[type='checkbox']:checked:visible").parents(".sms_content").length==$("#sms_list_content .check_left input[type='checkbox']:visible").parents(".sms_content").length){
            thinput.checked=true;
        }
    }
});
//新增行
$("#add").click(function(){
    // var html=$("#sms_content").clone();
    // $(html).show();
    // $("#sms_list_content").append(html);
    whir.loading.add("mask", 0.5);
    $("#batchbomb").show();
    $("#batchbomb").attr("data-id","");
    $("#type").val("");
    $("#type").attr("data-type","");
    $("#channel_name").val("");
    $("#channel_name").attr("data-type","");
    $("#channel_account").val("");
    $("#password").val("");
    $("#channel_price").val("");
    $("#channel_sign").val("");
    $("#channel_code").val("");
    $("#channel_child").val("");
    var input = $("#is_forced")[0];
    input.checked = false;
})
$("#close_batchbomb").click(function(){
    whir.loading.remove("mask", 0.5);
    $("#batchbomb").hide();
})
$("#batchbomb_return").click(function () {
    whir.loading.remove("mask", 0.5);
    $("#batchbomb").hide();
})
//删除行
$("#del").click(function(){
    whir.loading.add("mask", 0.5);
    $("#tk").show();
})
$("#cancel,#X").click(function () {
    whir.loading.remove("mask", 0.5);
    $("#tk").hide();
})
//删除
$("#delete").click(function(){
    var li=$("#sms_list_content .check_left input[type='checkbox']:checked:visible").parents(".sms_content");
    for(var i=li.length-1,ID="";i>=0;i--){
        var r=$(li[i]).attr("data-id");
        if(i>0){
            ID+=r+",";
        }else{
            ID+=r;
        }
    }
    var param={};
    param["id"]=ID;
    oc.postRequire("post","/msgChannelCfg/delete","",param, function (data) {
        if(data.code=="0"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"删除成功"
            });
            $("#tk").hide();
            whir.loading.remove("mask", 0.5);
            selChannelByCorp();
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:data.message
            });
            $("#tk").hide();
            whir.loading.remove("mask", 0.5);
        }
    })
})
//编辑行
$("#edit").click(function(){
    var li=$("#sms_list_content .check_left input[type='checkbox']:checked:visible").parents(".sms_content");
    var param={};
    param["id"]=$(li[0]).attr("data-id");
    if(li.length=="0"){
        art.dialog({
            zIndex: 10004,
            time: 1,
            lock: true,
            cancel: false,
            content:"请先选择"
        });
        return;
    }
    if(li.length>1){
        art.dialog({
            zIndex: 10004,
            time: 1,
            lock: true,
            cancel: false,
            content:"不能选择多个"
        });
        return;
    }
    assignment(param);
});
//双击行
$("#sms_list_content").on("dblclick",".sms_content",function () {
    var id=$(this).attr("data-id");
    var param={};
    param["id"]=id;
    assignment(param);
});
//赋值
function assignment(param) {
    oc.postRequire("post","/msgChannelCfg/select","",param, function (data) {
        var message=JSON.parse(data.message);
        if(data.code=="0"){
            if(message.type=="Marketing"){
                $("#type").val("营销短信通道");
            }
            if(message.type=="Production"){
                $("#type").val("生产短信通道");
            }
            $("#type").attr("data-type",message.type);
            $("#channel_name").val(message.name);
            $("#channel_name").attr("data-type",message.channel_name);
            $("#channel_account").val(message.channel_account);
            $("#password").val(message.password);
            $("#channel_price").val(message.channel_price);
            $("#channel_sign").val(message.channel_sign);
            $("#channel_code").val(message.channel_code);
            $("#channel_child").val(message.channel_child);
            var input = $("#is_forced")[0];
            if (message.is_forced == "Y") {
                input.checked = true;
            } else if (message.is_forced == "N") {
                input.checked = false;
            }
        }
    })
    whir.loading.add("mask", 0.5);
    $("#batchbomb").show();
    $("#batchbomb").attr("data-id",param.id);
}
//全选
function checkAll(name){
    var el=$("#sms_list_content .check_left input:visible");
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
    var el=$("#sms_list_content .check_left input:visible");
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
function getMsgList() {
    var ul=$("#batchbomb_content .content_parent .content_right");
    var input=$("#is_forced");
    var param={};
    for(var i=0;i<ul.length;i++){
        var li=$(ul[i]).find("ul li");
        var type=$(ul[i]).find("input").attr("id");
        var value="";
        if($(ul[i]).find("input").attr("id")=="type"||$(ul[i]).find("input").attr("id")=="channel_name"){
            value=$(ul[i]).find("input").attr("data-type");
        }else {
            value=$(ul[i]).find("input").val();
        }
        if(type=="type"||type=="channel_name"||type=="channel_account"||type=="password"||type=="channel_price"||type=="channel_sign"){
            if(value==""){
                switch(type)
                {
                    case "type":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"类型不能为空"
                        });
                        break;
                    case "channel_name":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"通道名称不能为空"
                        });
                        break;
                    case "channel_account":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"账号不能为空"
                        });
                        break;
                    case "password":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"密码不能为空"
                        });
                        break;
                    case "channel_price":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"单价不能为空"
                        });
                        break;
                    case "channel_sign":
                        art.dialog({
                            zIndex: 10004,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"签名不能为空"
                        });
                        break;
                }
                return;
            }
        }
        param[type]=value;
    }
    if(input[0].checked==true){
        param["is_forced"]="Y";
    }
    if(input[0].checked==false){
        param["is_forced"]="N";
    }
    param["corp_code"]=$("#OWN_CORP").val();
    var id=$("#batchbomb").attr("data-id");
    if(id==""){
        oc.postRequire("post","/msgChannelCfg/add","",param, function (data) {
            if(data.code=="0"){
                art.dialog({
                    zIndex:"10004",
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"保存成功"
                });
                selChannelByCorp();
            }
            if(data.code=="-1") {
                art.dialog({
                    zIndex: "10004",
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
    if(id!==""){
        param["id"]=id;
        oc.postRequire("post","/msgChannelCfg/edit","",param, function (data) {
            if(data.code=="0"){
                art.dialog({
                    zIndex:"10004",
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"保存成功"
                });
                selChannelByCorp();
            }
            if(data.code=="-1"){
                art.dialog({
                    zIndex:"10004",
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:data.message
                });
            }
        })
    }
    $("#batchbomb").hide();
    whir.loading.remove("mask", 0.5);

};
function getmsgChannelCfg() {
    oc.postRequire("post","/msgChannelCfg/getChannelsInfo","","", function (data) {
        var list=JSON.parse(data.message);
        var html=""
        for(var i=0;i<list.info.length;i++){
            html+="<div data-type="+list.info[i].channel+">"+list.info[i].channel_name+"</div>";
        }
        $("#channel_list").html(html);
    })
};
function selChannelByCorp(){
    var param={};
    param["corp_code"]=$("#OWN_CORP").val();
    oc.postRequire("post","/msgChannelCfg/selChannelByCorp","",param, function (data) {
        var message=JSON.parse(data.message).list;
        var list=JSON.parse(message);
        var html="";
        if(list.length>0){
            for(var i=0;i<list.length;i++){
                var type="";
                if(list[i].type=="营销短信通道"){
                    type="Marketing";
                }
                if(list[i].type=="生产短信通道"){
                    type="Production";
                }
                if(list[i].is_forced=="Y"){
                    html+='<div class="sms_content" data-id="'+list[i].id+'">\
                        <div class="check_left">\
                            <div class="checkbox">\
                            <input  type="checkbox" value="" name="test" title="全选/取消" class="check" /><label></label>\
                            </div>\
                        </div>\
                        <ul>\
                            <li class="type">\
                                <input type="text"  readonly value="'+list[i].type+'" bute-type="type" data-type="'+type+'">\
                                <div class="select_list">\
                                    <div data-type="Marketing">营销短信通道</div>\
                                    <div data-type="Production">生产短信通道</div>\
                                </div>\
                            </li>\
                            <li class="channel_name">\
                                <input type="text" bute-type="channel_name" data-type="'+list[i].channel_name+'" value="'+list[i].ch_name+'" readonly placeholder="请选择...">\
                                <div class="select_list">\
                                </div>\
                            </li>\
                            <li class="channel_account">\
                                <input type="text" bute-type="channel_account" value="'+list[i].channel_account+'" readonly>\
                            </li>\
                            <li class="passwored">\
                                <input type="text" bute-type="password" value="'+list[i].password+'" readonly>\
                            </li>\
                            <li class="channel_price">\
                                <input type="text" bute-type="channel_price" value="'+list[i].channel_price+'" readonly>\
                            </li>\
                            <li class="channel_sign">\
                                <input type="text" bute-type="channel_sign" value="'+list[i].channel_sign+'" readonly>\
                            </li>\
                            <li class="channel_code">\
                                <input type="text" bute-type="channel_code" value="'+list[i].channel_code+'" readonly>\
                            </li>\
                            <li class="channel_child">\
                                <input type="text" bute-type="channel_child" value="'+list[i].channel_child+'" readonly>\
                            </li>\
                        </ul>\
                        <div class="check_right">\
                            <div class="checkbox1">\
                                <input  type="checkbox" value="" name="test" checked="true" title="全选/取消" class="check" /><label></label>\
                            </div>\
                        </div>\
                        </div>'
                }else if(list[i].is_forced=="N"){
                    html+='<div class="sms_content" data-id="'+list[i].id+'">\
                                <div class="check_left">\
                                    <div class="checkbox">\
                                    <input  type="checkbox" value="" readonly name="test" title="全选/取消" class="check" /><label></label>\
                                    </div>\
                                </div>\
                                <ul>\
                                    <li class="type">\
                                        <input type="text"  readonly value="'+list[i].type+'" bute-type="type" data-type="'+type+'">\
                                        <div class="select_list">\
                                            <div data-type="Marketing">营销短信通道</div>\
                                            <div data-type="Production">生产短信通道</div>\
                                        </div>\
                                    </li>\
                                    <li class="channel_name">\
                                        <input type="text" readonly bute-type="channel_name" data-type="'+list[i].channel_name+'" value="'+list[i].ch_name+'" readonly placeholder="请选择...">\
                                        <div class="select_list">\
                                        </div>\
                                    </li>\
                                    <li class="channel_account">\
                                        <input type="text" readonly bute-type="channel_account" value="'+list[i].channel_account+'">\
                                    </li>\
                                    <li class="passwored">\
                                        <input type="text" bute-type="password" value="'+list[i].password+'" readonly>\
                                    </li>\
                                    <li class="channel_price">\
                                        <input type="text" bute-type="channel_price" readonly value="'+list[i].channel_price+'">\
                                    </li>\
                                    <li class="channel_sign">\
                                        <input type="text" bute-type="channel_sign" readonly value="'+list[i].channel_sign+'">\
                                    </li>\
                                    <li class="channel_code">\
                                        <input type="text" bute-type="channel_code" readonly value="'+list[i].channel_code+'">\
                                    </li>\
                                    <li class="channel_child">\
                                        <input type="text" bute-type="channel_child" readonly value="'+list[i].channel_child+'">\
                                    </li>\
                                </ul>\
                                <div class="check_right">\
                                    <div class="checkbox1">\
                                        <input  type="checkbox" value="" name="test" title="全选/取消" class="check" /><label></label>\
                                    </div>\
                                </div>\
                        </div>'
                }
            }
        }
        $("#sms_list_content").html(html);
        getmsgChannelCfg();
    })
}
//短信通道保存
$("#batchbomb_que").click(function(){
    getMsgList();
})


