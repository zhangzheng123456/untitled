var oc = new ObjectControl();

(function (root, factory) {
    root.param = factory();
}(this, function () {
    var paramjs = {};
    paramjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    paramjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    paramjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    paramjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    paramjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    paramjs.bindbutton = function () {
        $(".operadd_btn ul li:nth-of-type(1)").click(function () {
            var nameMark=$("#PARAM_NAME").attr("data-mark");//名称是否唯一的标志
            if (paramjs.firstStep()) {
                if(nameMark == "N"){
                    var div=$("#PARAM_NAME").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var OWN_CORP = $("#OWN_CORP").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_TYPE = $(".paramType").prev("input").val();
                var param_class = $("#param_class").val();
                if(PARAM_TYPE=="时间"){
                    PARAM_TYPE="date"
                }else if(PARAM_TYPE=="单选"){
                    PARAM_TYPE="select"
                }else if(PARAM_TYPE=="多选框"){
                    PARAM_TYPE="check"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="text"
                }else if(PARAM_TYPE=="长文本"){
                    PARAM_TYPE="longtext"
                }else if(PARAM_TYPE=="分割线"){
                    PARAM_TYPE="rule"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                if(PARAM_TYPE==""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"参数类型不能为空"
                    });
                    return;
                }
                if(PARAM_TYPE=="select" && PARAM_VALUE==""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"参数值不能为空"
                    });
                    return;
                }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = $(".checkbox_isactive").find("#is_active").prop("checked") ? "Y" : 'N';
                var required = $(".checkbox_isactive").find("#require").prop("checked") ? "Y" : 'N';
                var param_attribute = $("#param_attr").attr("data-id");
                var _command = "/vipparam/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "param_name": PARAM_NAME,
                    "param_desc": PARAM_DESC,
                    "param_type": PARAM_TYPE,
                    "param_values":PARAM_VALUE,
                    "required" : required,
                    "param_class" : param_class,
                    "param_attribute":param_attribute,
                    "remark": REMARK,
                    "isactive":ISACTIVE
                };
                whir.loading.add("", 0.5);
                paramjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function () {
            var nameMark=$("#PARAM_NAME").attr("data-mark");//名称是否唯一的标志
            if (paramjs.firstStep()) {
                if(nameMark == "N"){
                    var div=$("#PARAM_NAME").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var id = sessionStorage.getItem("id");
                var OWN_CORP = $("#OWN_CORP").val();
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_TYPE = $(".paramType").prev("input").val();
                var param_class = $("#param_class").val();
                if(PARAM_TYPE=="时间"){
                    PARAM_TYPE="date"
                }else if(PARAM_TYPE=="单选"){
                    PARAM_TYPE="select"
                }else if(PARAM_TYPE=="多选框"){
                    PARAM_TYPE="check"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="text"
                }else if(PARAM_TYPE=="长文本"){
                    PARAM_TYPE="longtext"
                }else if(PARAM_TYPE=="分割线"){
                    PARAM_TYPE="rule"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                if(PARAM_TYPE=="select" && PARAM_VALUE==""){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"参数值不能为空"
                    });
                    return;
                }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = $(".checkbox_isactive").find("#is_active").prop("checked") ? "Y" : 'N';
                var required = $(".checkbox_isactive").find("#require").prop("checked") ? "Y" : 'N';
                var param_attribute = $("#param_attr").attr("data-id");
                var _command = "/vipparam/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "id": id,
                    "corp_code": OWN_CORP,
                    "param_name": PARAM_NAME,
                    "param_desc": PARAM_DESC,
                    "param_type": PARAM_TYPE,
                    "param_values":PARAM_VALUE,
                    "required" : required,
                    "param_class" : param_class,
                    "param_attribute":param_attribute,
                    "remark": REMARK,
                    "isactive":ISACTIVE
                };
                whir.loading.add("", 0.5);
                paramjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    paramjs.ajaxSubmit = function (_command, _params, opt) {
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if(_command=="/vipparam/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_paramedit.html");
                }
                if(_command=="/vipparam/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html");
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
        if (paramjs['check' + command]) {
            if (!paramjs['check' + command].apply(paramjs, [obj, hint])) {
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
        paramjs.bindbutton();
    }
    var obj = {};
    obj.paramjs = paramjs;
    obj.init = init;
    return obj;
}));

jQuery(document).ready(function () {
    window.param.init();
    if ($(".pre_title label").text() == "编辑拓展参数") {
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
                    $("#edit_save").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
        var _params = {"id": id};
        var _command = "/vipparam/selectById";
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                    msg = JSON.parse(msg.vipParam);
                var corp_code=msg.corp_code;//公司编号
                var param_attribute = msg.param_attribute;
                $("#PARAM_NAME").val(msg.param_name);
                $("#PARAM_NAME").attr("data-name", msg.param_name);
                $("#PARAM_DESC").val(msg.param_desc);
                msg.required == "Y" ? $(".checkbox_isactive").find("#require").prop("checked",true) : $(".checkbox_isactive").find("#require").prop("checked",false);
                msg.isactive=="Y" ? $(".checkbox_isactive").find("#is_active").prop("checked",true) : $(".checkbox_isactive").find("#is_active").prop("checked",false);
                var param_type=msg.param_type
                if(param_type=="date"){
                    param_type="时间";
                    $("#PARAM_VALUE").attr("disabled","true");
                }else if(param_type=="select"){
                    param_type="单选";
                    $("#PARAM_VALUE").prev("label").html("参数值*");
                    $("#PARAM_VALUE").prev("label").css('color','#c26555');
                }else if(param_type=="check"){
                    param_type="多选框";
                    $("#PARAM_VALUE").prev("label").html("参数值*");
                    $("#PARAM_VALUE").prev("label").css('color','#c26555');
                }else if(param_type=="text"){
                    param_type="自定义";
                    $("#PARAM_VALUE").attr("disabled","true");
                }else if(param_type=="longtext"){
                    param_type="长文本";
                    $("#PARAM_VALUE").attr("disabled","true");
                }else if(param_type=="rule"){
                    param_type="分割线";
                    $("#PARAM_VALUE").attr("disabled","true");
                }
                $("#PARAM_TYPE").val(param_type);
                $("#PARAM_VALUE").val(msg.param_values);
                $("#param_class").val(msg.param_class);
                $("#param_attr").val(param_attribute != '' ? $(".param_attr li[data-id="+param_attribute+"]").text() : '');
                $("#param_attr").attr("data-id",msg.param_attribute);
                $("#REMARK").val(msg.remark);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                getcorplist(corp_code);
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
    //验证参数名称唯一性
    $("#PARAM_NAME").blur(function () {
        var corp_code = $("#OWN_CORP").val();
        var param_name = $("#PARAM_NAME").val();
        var param_name_l = $(this).attr("data-name");
        var div=$(this).next('.hint').children();
        var param={};
        param['corp_code']=corp_code;
        param['param_name']=param_name;
        if(param_name!==""&&param_name!==param_name_l){
            oc.postRequire('post','/vipparam/checkNameOnly',0,param,function (data) {
                if(data.code=="0"){
                    div.html("");
                    $("#PARAM_NAME").attr("data-mark","Y");
                }else if(data.code=="-1"){
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    $("#PARAM_NAME").attr("data-mark","N");
                }
            })
        }else if(param_name==param_name_l){
            $("#PARAM_NAME").attr("data-mark","Y");
        }
    })

    $(".operadd_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html?t="+ $.now());
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html?t="+ $.now());
    });
    $("#back_vip_param").click(function(){
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html?t="+ $.now());
    })
});
function getcorplist(a, b) {
    //获取所属企业列表
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
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
            $("#OWN_CORP").searchableSelect();
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
//参数类型下拉
$("#page-wrapper").on("click","#PARAM_TYPE",function () {
    $(".paramType").toggle();
});
$("#page-wrapper").on("blur","#PARAM_TYPE",function () {
    setTimeout(function () {
        $(".paramType").hide();
    }, 200)
});
$(".paramType li").click(function () {
    var val = $(this).html();
    $(".paramType").prev("input").val(val);
    if(val=="自定义"||val=="时间"||val=="长文本"||val=="分割线"){
        $("#PARAM_VALUE").attr("disabled","true");
        $("#PARAM_VALUE").prev("label").html("参数值");
        $("#PARAM_VALUE").prev("label").css('color','#434960');
        $("#PARAM_VALUE").val("");
    }else if(val=="单选"||val=="多选框"){
        $("#PARAM_VALUE").removeAttr("disabled");
        $("#PARAM_VALUE").prev("label").html("参数值*");
        $("#PARAM_VALUE").prev("label").css('color','#c26555');
    }
});

$("#param_attr").click(function () {
    $(".param_attr").toggle();
});
$("#param_attr").blur(function () {
    setTimeout(function () {
        $(".param_attr").hide();
    }, 200)
});
$(".param_attr li").click(function () {
    $("#param_attr").val($(this).attr("data-id") != '' ? $(this).text() : "");
    $("#param_attr").attr("data-id",$(this).attr("data-id"));
    if($(this).attr("data-id") != ''){
        if($(this).attr("data-id") == 'sex'){
            $(".paramType li:contains('单选')").trigger("click");
            $("#PARAM_VALUE").val("男,女");
            $("#PARAM_VALUE").attr("disabled","true");
            $("#PARAM_TYPE").removeAttr("id");
        }else if($(this).attr("data-id") != 'sex'){
            $(".paramType li:contains('自定义')").trigger("click");
            $("#PARAM_VALUE").val("");
            $("#PARAM_TYPE").removeAttr("id");
        }else {
            $(".paramType").prev("input").attr("id","PARAM_TYPE");
        }
    }else {
        $(".paramType").prev("input").attr("id","PARAM_TYPE");
        $(".paramType").prev("input").removeAttr("disabled");
    }
});