var oc = new ObjectControl();
var val = sessionStorage.getItem("key");
val = JSON.parse(val);
var message = JSON.parse(val.message);
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
                var list=[];
                var len=$(".wx_app").find(".wx_span");
                for(var i=0;i<len.length;i++){
                    var app_id=$(len[i]).find('.WXID').val();
                    var app_name=$(len[i]).find('.AppName').val();
                    if(app_id!==""&&app_name==""){
                        alert("名称不能为空！")
                        return;
                    }
                    var wechat={"app_id":app_id,"app_name":app_name}
                    list.push(wechat);
                }
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
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
            console.log(nameMark);
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
                var HEADPORTRAIT = "";
                if ($("#OWN_CORP").val() !== '' && $("#preview img").attr("src") !== '../img/bg.png') {
                    HEADPORTRAIT = "http://products-image.oss-cn-hangzhou.aliyuncs.com/Corp_logo/ishow/" + $("#OWN_CORP").val().trim() + ".jpg";
                } else {
                    HEADPORTRAIT = "";
                }
                var CORPID = $("#OWN_CORP").val();
                var WXID = $("#WXID").val();
                var CORPNAME = $("#CORPNAME").val();
                var CORPADDRESS = $("#CORPADDRESS").val();
                var CONTACTS = $("#CONTACTS").val();
                var PHONE = $("#PHONE").val();
                var list=[];
                var arr=[];
                var len=$(".wx_app").find(".wx_span");
                for(var i=0;i<len.length;i++){
                    var app_id=$(len[i]).find('.WXID').val();
                    var app_name=$(len[i]).find('.AppName').val();
                    arr.push(app_id);
                    arr.sort();
                    for(var j=0;j<arr.length;j++){
                        if(arr[j]==arr[j+1]&&arr[j]!==""){
                            alert("公众号ID不能重复！")
                            return;
                        }
                    }
                    if(app_id!==""&&app_name==""){
                        alert("名称不能为空！")
                        return;
                    }else if(app_name!==""&&app_id==""){
                        alert("公众号ID不能为空！")
                        return;
                    }
                    var wechat={"app_id":app_id,"app_name":app_name}
                    list.push(wechat);
                }
                var input = $(".checkbox_isactive").find("input")[0];
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
                    "avater": HEADPORTRAIT,
                    "corp_code": CORPID,
                    "app_id": WXID,
                    "corp_name": CORPNAME,
                    "address": CORPADDRESS,
                    "contact": CONTACTS,
                    "phone": PHONE,
                    "isactive": ISACTIVE,
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
        // console.log(JSON.stringify(_params));
        // _params=JSON.stringify(_params);
        console.log(_params);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if (message.user_type == "admin") {
                    $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
                } else {
                    $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
                }
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
    if ($(".pre_title label").text() == "编辑企业信息") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            var data = JSON.parse(data);
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                if (action.length == 0) {
                    $("#edit_save").remove();
                }
            }
        });
        var _params = {"id": id};
        var _command = "/corp/select";
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                $("#preview img").attr("src", msg.avater);
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
                var wechat=msg.wechats;
                console.log(wechat);
                var len=$(".wx_app").find(".wx_span");
                if(wechat.length>0) {
                    // if (wechat[0].is_authorize== "Y") {
                    //     $('.state_val').val("已授权");
                    // } else if (wechat[0].is_authorize == "N") {
                    //     $('.state_val').val("未授权");
                    // }
                    // $(len[0]).find(".WXID").val(wechat[0].app_id);
                    // $(len[0]).find(".AppName").val(wechat[0].app_name);
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
                        + '<input type="text" disabled="true" value="'+is_authorize+'" ><p class="icon-ishop_6-12"onclick="removeselect(this)"></p>'
                        + '</span>')
                    }
                }
                var input = $(".checkbox_isactive").find("input")[0];
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
            console.log(corp_code);
            _params["corp_code"] = corp_code;
            oc.postRequire("post", "/corp/Corp_codeExist", "", _params, function (data) {
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
            oc.postRequire("post", "/corp/CorpNameExist", "", _params, function (data) {
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
            console.log(data);

        }
    });
    function callback(data) {
        var a = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=wxa6780115cc7c1db5&pre_auth_code=" + data + "&redirect_uri=http://wechat.app.bizvane.com/app/wechat/callback";
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
            arr.push(app_id);
            arr.sort();
            for (var j = 0; j < arr.length; j++) {
                if (arr[j] == arr[j + 1] && arr[j] !== "") {
                    alert("公众号ID不能重复！")
                    return false;
                }
            }
            if(app_name == "") {
                alert("名称不能为空！")
                return false;
            }
            if (app_id == "") {
                alert("公众号ID不能为空！")
                return false;
            }
            var wechat = {
                "app_id": app_id,
                "app_name": app_name
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
                console.log(data.code);
                if(data.code=="0"){
                    a=true;
                }else if(data.code=="-1"){
                    a=false;
                    alert(data.message);
                }
            },
            error: function (data) {
                console.log(data);

            }
        });
        return a;
    })
    // //检查是否可否授权状态、
    // $("#state").click(function () {
    //     var corp_code = $("#CORPID").val();
    //     var _params = {};
    //     _params["corp_code"] = corp_code;
    //     oc.postRequire("post", "/corp/is_authorize", "", _params, function (data) {
    //         var message=JSON.parse(data.message);
    //         for(var i=0;i<message.length;i++){
                  
    //         }
    //         // if (data.code == "0") {
    //         //     $(".state_val").val(data.message);
    //         // } else {
    //         //     alert(data.message);
    //         // }
    //         // if(data.code=="0"){

    //         // }
    //     })
    // })
    if (message.user_type == "admin") {
        $(".corpadd_oper_btn ul li:nth-of-type(2)").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
        });
        $("#edit_close").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp.html");
        });
    } else {
        $(".corpadd_oper_btn ul li:nth-of-type(2)").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
        });
        $("#edit_close").click(function () {
            $(window.parent.document).find('#iframepage').attr("src", "/corp/corp_user.html");
        });
    }
});
