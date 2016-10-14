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
                var OWN_CORP = $("#OWN_CORP").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_TYPE = $("#PARAM_TYPE").val();
                var param_class = $("#param_class").val();
                var required =$("#is_fill").val();
                if(required=="是"){
                    required="Y";
                }else{
                    required="N";
                }
                if(PARAM_TYPE=="时间"){
                    PARAM_TYPE="date"
                }else if(PARAM_TYPE=="选择列表"){
                    PARAM_TYPE="select"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="text"
                }else if(PARAM_TYPE=="长文本"){
                    PARAM_TYPE="longtext"
                }else if(PARAM_TYPE=="分割线"){
                    PARAM_TYPE="rule"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                if(PARAM_TYPE==""){
                    alert("参数不能为空！");
                    return;
                }
                // if(PARAM_TYPE=="select" && PARAM_VALUE==""){
                //     alert("参数值不能为空！");
                //     return;
                // }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
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
                var OWN_CORP = $("#OWN_CORP").val();
                var PARAM_DESC = $("#PARAM_DESC").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_TYPE = $("#PARAM_TYPE").val();
                var param_class = $("#param_class").val();
                var required =$("#is_fill").val();
                if(required=="是"){
                    required="Y";
                }else{
                    required="N";
                }
                if(PARAM_TYPE=="时间"){
                    PARAM_TYPE="date"
                }else if(PARAM_TYPE=="选择列表"){
                    PARAM_TYPE="select"
                }else if(PARAM_TYPE=="自定义"){
                    PARAM_TYPE="text"
                }else if(PARAM_TYPE=="长文本"){
                    PARAM_TYPE="longtext"
                }else if(PARAM_TYPE=="分割线"){
                    PARAM_TYPE="rule"
                }
                var PARAM_VALUE= $("#PARAM_VALUE").val();
                // if(PARAM_TYPE=="select" && PARAM_VALUE==""){
                //     alert("参数值不能为空！");
                //     return;
                // }
                var REMARK = $("#REMARK").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
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
                // art.dialog({
                // 	time: 1,
                // 	lock:true,
                // 	cancel: false,
                // 	content: data.message
                // });
                $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html");
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
    if ($(".pre_title label").text() == "编辑会员参数") {
        var id = sessionStorage.getItem("id");
        var _params = {"id": id};
        var _command = "/vipparam/selectById";
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                    msg = JSON.parse(msg.vipParam);
                console.log(msg);
                var corp_code=msg.corp_code;//公司编号
                $("#PARAM_NAME").val(msg.param_name);
                $("#PARAM_DESC").val(msg.param_desc);
                var input=$(".checkbox_isactive").find("input")[0];
                if(msg.isactive=="Y"){
                    input.checked=true;
                }else if(msg.isactive=="N"){
                    input.checked=false;
                }
                var param_type=msg.param_type
                if(param_type=="date"){
                    param_type="时间";
                    $("#PARAM_VALUE").attr("disabled","true");
                }else if(param_type=="select"){
                    param_type="选择列表";
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
                var required=msg.required;
                if(required=="Y"){
                    required="是";
                }else if(required=="N"){
                    required="否"
                }
                $("#is_fill").val(required);
                $("#PARAM_TYPE").val(param_type);
                $("#PARAM_VALUE").val(msg.param_values);
                $("#param_class").val(msg.param_class);
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
        });
    } else {
        getcorplist();
    }


    $(".operadd_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html");
    });
    $("#back_vip_param").click(function(){
        $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_param.html");
    })
});
function getcorplist(a, b) {
    //获取所属企业列表
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
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
            $("#OWN_CORP").searchableSelect();
            // var c = $('#corp_select .selected').attr("data-value");
            // param_data(c, b);
            // $("#corp_select .searchable-select-item").click(function () {
            //     var c = $(this).attr("data-value");
            //     param_data(c, b);
            // })
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
    if(val=="自定义"||val=="时间"||val=="长文本"||val=="分割线"){
        $("#PARAM_VALUE").attr("disabled","true");
    }else if(val=="选择列表"){
        $("#PARAM_VALUE").removeAttr("disabled");
    }
})

$("#is_fill").click(function () {
    if ($(".is_fill").css("display") == "none") {
        $(".is_fill").show();
    } else {
        $(".is_fill").hide();
    }
})
$("#is_fill").blur(function () {
    setTimeout(function () {
        $(".is_fill").hide();
    }, 200)
})
$(".is_fill li").click(function () {
    $("#is_fill").val($(this).html());
})