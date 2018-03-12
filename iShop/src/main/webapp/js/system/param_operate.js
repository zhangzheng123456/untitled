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
            if (paramjs.firstStep()) {
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_TYPE = $("#PARAM_TYPE").val();
                if(PARAM_TYPE=="开关"){
                    PARAM_TYPE="switch"
                }else if(PARAM_TYPE=="选择列表"){
                    PARAM_TYPE="list"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="custom"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                if(PARAM_TYPE==""){
                    alert("参数不能为空！");
                    return;
                }
                if(PARAM_TYPE=="list" && PARAM_VALUE==""){
                    alert("参数值不能为空！");
                    return;
                }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/param/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "param_name": PARAM_NAME,
                    "param_desc": PARAM_DESC,
                    "param_type": PARAM_TYPE,
                    "param_values":PARAM_VALUE,
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
            if (paramjs.firstStep()) {
                var id = sessionStorage.getItem("id");
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_TYPE = $("#PARAM_TYPE").val();
                if(PARAM_TYPE=="开关"){
                    PARAM_TYPE="switch"
                }else if(PARAM_TYPE=="选择列表"){
                    PARAM_TYPE="list"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="custom"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                if(PARAM_TYPE=="list" && PARAM_VALUE==""){
                    alert("参数值不能为空！");
                    return;
                }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/param/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "id": id,
                    "param_name": PARAM_NAME,
                    "param_desc": PARAM_DESC,
                    "param_type": PARAM_TYPE,
                    "param_values":PARAM_VALUE,
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
        console.log(_params);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if(_command=="/param/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/system/param_edit.html");
                }
                if(_command=="/param/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src", "/system/param.html");
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
    if ($(".pre_title label").text() == "编辑参数") {
        var id = sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        $.get("/detail?funcCode="+funcCode+"", function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var action=message.actions;
                console.log(action.length);
                if(action.length==0){
                    $("#edit_save").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
        var _params = {"id": id};
        var _command = "/param/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                $("#PARAM_NAME").val(msg.param_name);
                $("#PARAM_DESC").val(msg.param_desc);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var input=$(".checkbox_isactive").find("input")[0];
                if(msg.isactive=="Y"){
                    input.checked=true;
                }else if(msg.isactive=="N"){
                    input.checked=false;
                }
                var param_type=msg.param_type
                if(param_type=="switch"){
                    param_type="开关";
                    $("#PARAM_VALUE").attr("disabled","true");
                }else if(param_type=="list"){
                    param_type="选择列表";
                }else if(param_type=="custom"){
                    param_type="自定义";
                    $("#PARAM_VALUE").attr("disabled","true");
                }
                $("#PARAM_TYPE").val(param_type);
                $("#PARAM_VALUE").val(msg.param_values);
                $("#REMARK").val(msg.remark);
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

    $(".operadd_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/system/param.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/system/param.html");
    });
    $("#back_param").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/system/param.html");
    });
});

//参数类型下拉
$("#PARAM_TYPE").click(function () {
    if ($(".paramType").css("display") == "none") {
        $(".paramType").show();
    } else {
        $(".paramType").hide();
    }
})
$("#PARAM_TYPE").blur(function () {
    setTimeout(function () {
        $(".paramType").hide();
    }, 200)
})

$(".paramType li").click(function () {
    var val = $(this).html();
    console.log(val);
    $("#PARAM_TYPE").val(val);
    if(val=="自定义"||val=="开关"){
        $("#PARAM_VALUE").attr("disabled","true");
    }else if(val=="选择列表"){
        $("#PARAM_VALUE").removeAttr("disabled");
    }
})