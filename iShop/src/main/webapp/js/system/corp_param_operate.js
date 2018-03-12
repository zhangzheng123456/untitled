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
                var PARAM_ID = $("#PARAM_NAME").attr("data-id");
                var REMARK = $("#REMARK").val();
                var PARAM_VALUE = $("#param_value").val();
                if(PARAM_NAME==""){
                    alert("参数不能为空!");
                    return;
                }
                if(PARAM_VALUE==""){
                    alert("参数值不能为空!");
                    return;
                }
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/corpParam/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "param_id": PARAM_ID,
                    "remark": REMARK,
                    "param_value": PARAM_VALUE,
                    "isactive": ISACTIVE
                };
                console.log(_params)
                whir.loading.add("", 0.5);
                paramjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function () {
            if (paramjs.firstStep()) {
                var id = sessionStorage.getItem("id");
                var PARAM_ID = $("#PARAM_NAME").attr("data-id");
                var OWN_CORP = $("#OWN_CORP").val();
                var PARAM_NAME = $("#PARAM_NAME").val();
                var PARAM_VALUE = $("#param_value").val();
                if(PARAM_VALUE==""){
                    alert("参数值不能为空!");
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
                var _command = "/corpParam/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "id": id,
                    "corp_code": OWN_CORP,
                    "param_id": PARAM_ID,
                    "remark": REMARK,
                    "param_value": PARAM_VALUE,
                    "isactive": ISACTIVE
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
                if(_command=="/corpParam/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/system/corp_param_edit.html");
                }
                if(_command=="/corpParam/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src", "/system/corp_param.html");
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
    if ($(".pre_title label").text() == "编辑企业参数") {
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
        var _command = "/corpParam/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var corp_code = msg.corp_code;
                var param_id = msg.param_id;
                var param_values=msg.param_values;
                var param_type=msg.param_type;
                console.log(msg);
                $("#OWN_CORP option").val(msg.corp_code);
                $("#OWN_CORP option").text(msg.corp_name);
                $("#PARAM_NAME").val(msg.param_desc);
                $("#PARAM_NAME").attr("data-id", msg.param_id);
                $("#REMARK").attr("data-name", msg.remark);
                $("#REMARK").val(msg.remark);
                if(param_type!=="custom"){
                    $("#param_value").addClass("param_value");
                    $("#param_value").attr("readonly","true");
                    param_values=param_values.split(",");
                    for(var j=0;j<param_values.length;j++){
                        $("#paramValue_down").append('<li>'+param_values[j]+'</li>')
                    }
                    $("#paramValue_down li").click(function () {
                        $("#param_value").val($(this).html());
                    })
                }
                $("#param_value").val(msg.param_value);
                var input=$(".checkbox_isactive").find("input")[0];
                if(msg.isactive=="Y"){
                    input.checked=true;
                }else if(msg.isactive=="N"){
                    input.checked=false;
                }
                // $("#param_value").attr("data-name", msg.param_value);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                getcorplist(corp_code, param_id);
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
        $(window.parent.document).find('#iframepage').attr("src", "/system/corp_param.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/system/corp_param.html");
    });
    $("#back_corp_param").click(function () {
        $(window.parent.document).find('#iframepage').attr("src", "/system/corp_param.html");
    });
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
            var corp_html = "<option value='all'>全部</option>";
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
            var c = $('#corp_select .selected').attr("data-value");
            param_data(c, b);
            $("#corp_select .searchable-select-item").click(function () {
                var c = $(this).attr("data-value");
                param_data(c, b);
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
function param_data(c, b) {
    var _params = {};
    _params["corp_code"] = c;//企业编号
    var _command = "/param/getParamInfo";//调取参数
    oc.postRequire("post", _command, "", _params, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
                msg=msg.params;
            console.log(msg);
            if (msg.length > 0) {
                for (var i = 0; i < msg.length; i++) {
                    $('#paramName_down').append('<li data-id="'
                        +msg[i].param_id
                        +'" data-type="'
                        +msg[i].param_type
                        +'" data-value="'
                        +msg[i].param_values
                        +'"><span>'
                        +msg[i].param_desc
                        +'</span></li>')
                }
                $("#paramName_down li").click(function (e) {
                    var event=window.event||arguments[0];
                    if(event.stopPropagation){
                        event.stopPropagation();
                    }else{
                        event.cancelBubble=true;
                    }
                    $("#paramName_down").hide();
                    var dataId=$(this).attr("data-id");
                    var dataType=$(this).attr("data-type");
                    $("#PARAM_NAME").attr("data-id",dataId);
                    $("#PARAM_NAME").attr("data-type",dataType);
                    var val = $(this).find("span").html();
                    $("#paramValue_down").empty();
                    $("#param_value").val("");
                    $("#param_value").removeClass("param_value");
                    $("#PARAM_NAME").val(val);
                    var dataType=$(this).attr("data-type");
                    var datavalue=$(this).attr("data-value");
                        datavalue=datavalue.split(",");
                    console.log(dataType);
                    console.log(datavalue);
                    if(dataType=="custom"){
                        $("#param_value").val(datavalue);
                        $("#param_value").removeAttr("readonly");
                    }else{
                        $("#param_value").attr("readonly","true");
                        $("#param_value").addClass("param_value");
                        for(var j=0;j<datavalue.length;j++){
                            $("#paramValue_down").append('<li>'+datavalue[j]+'</li>')
                        }
                        $("#paramValue_down li").click(function () {
                            $("#param_value").val($(this).html());
                        })
                    }
                })
            } else if (msg.length <= 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "该企业没有参数"
                });
            }

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

$("#PARAM_NAME").click(function () {
    if ($("#paramName_down").css("display") == "none") {
        $("#paramName_down").show();
    } else {
        $("#paramName_down").hide();
    }
})
$(document).click(function(e){
    if($(e.target).is("#paramName_down")||$(e.target).is("#search_param")||$(e.target).is("#PARAM_NAME")){
        return;
    }else{
        $("#paramName_down").hide();
    }
})

$("#param_value").click(function () {
    if ($("#paramValue_down").css("display") == "none") {
        $("#paramValue_down").show();
    } else {
        $("#paramValue_down").hide();
    }
})
$("#param_value").blur(function () {
    setTimeout(function () {
        $("#paramValue_down").hide();
    }, 200)
})
//自定义选择器
$.expr[":"].searchableSelectContains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
$("#search_param").on('keyup', function(event){
    var text=$(this).val();
    console.log(text);
    $(this).siblings('li').addClass('store_list_kuang_hide');
    $(this).siblings('li:searchableSelectContains('+text+')').removeClass('store_list_kuang_hide');
});
