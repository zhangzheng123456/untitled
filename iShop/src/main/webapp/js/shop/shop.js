
var oc = new ObjectControl();
(function (root, factory) {
    root.shop = factory();
}(this, function () {
    var shopjs = {};
    shopjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    shopjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    shopjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    shopjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    shopjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    shopjs.bindbutton = function () {
        $(".shopadd_oper_btn ul li:nth-of-type(1)").click(function () {
            var nameMark = $("#STORE_NAME").attr("data-mark");//店铺名称是否唯一的标志
            var codeMark = $("#STORE_ID").attr("data-mark");//店铺ID是否唯一的标志
            var idMark = $("#storeId").attr("data-mark");//店铺ID是唯一的标志
            if (shopjs.firstStep()) {
                if (nameMark == "N" || codeMark == "N"||idMark=="N") {
                    if (nameMark == "N") {
                        var div = $("#STORE_NAME").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#STORE_ID").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    if(idMark=="N"){
                        var div = $("#storeId").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var STORE_ID = $("#STORE_ID").val();
                var STORE_NAME = $("#STORE_NAME").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var store_id = $("#storeId").val();
                var OWN_AREA = $("#OWN_AREA").attr("data-myacode");
                var OWN_BRAND = $("#OWN_BRAND").attr("data-mybcode");
                if (OWN_AREA == "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属区域不能为空"
                    });
                    return;
                }
                if (OWN_BRAND == "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属品牌不能为空"
                    });
                    return;
                }
                var is_zhiying = $("#FLG_TOB").val();
                var FLG_TOB = "";
                if (is_zhiying == "是") {
                    FLG_TOB = "Y";
                } else if (is_zhiying == "否") {
                    FLG_TOB = "N";
                }
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                // var SHOP_MANAGER=$("#SHOP_MANAGER").val();
                var _command = "/shop/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "brand_code": OWN_BRAND,
                    "store_code": STORE_ID,
                    "store_id": store_id,
                    "area_code": OWN_AREA,
                    "store_name": STORE_NAME,
                    "flg_tob": FLG_TOB,
                    "isactive": ISACTIVE
                };
                shopjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#edit_save").click(function () {
            var nameMark = $("#STORE_NAME").attr("data-mark");//店铺名称是否唯一的标志
            var codeMark = $("#STORE_ID").attr("data-mark");//店铺编号是否唯一的标志
            var idMark=$("#storeId").attr("data-mark");//店铺id是否唯一的标志
            if (shopjs.firstStep()) {
                if (nameMark == "N" || codeMark == "N"||idMark=="N") {
                    if (nameMark == "N") {
                        var div = $("#STORE_NAME").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#STORE_ID").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    if(idMark=="N"){
                        var div = $("#storeId").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                console.log($("#OWN_BRAND").data("mybcode"));
                var ID = sessionStorage.getItem("id");
                var store_id = $("#storeId").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var OWN_AREA = $("#OWN_AREA").attr("data-myacode");
                var OWN_BRAND = $("#OWN_BRAND").attr("data-mybcode");
                var STORE_ID = $("#STORE_ID").val();
                var STORE_NAME = $("#STORE_NAME").val();
                var is_zhiying = $("#FLG_TOB").val();
                if (OWN_AREA == "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属区域不能为空"
                    });
                    return;
                }
                if (OWN_BRAND == "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属品牌不能为空"
                    });
                    return;
                }
                var FLG_TOB = "";
                if (is_zhiying == "是") {
                    FLG_TOB = "Y";
                } else if (is_zhiying == "否") {
                    FLG_TOB = "N";
                }
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                // var SHOP_MANAGER=$("#SHOP_MANAGER").val();
                var _command = "/shop/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "id": ID,
                    "corp_code": OWN_CORP,
                    "brand_code": OWN_BRAND,
                    "store_code": STORE_ID,
                    "store_id": store_id,
                    "area_code": OWN_AREA,
                    "store_name": STORE_NAME,
                    "flg_tob": FLG_TOB,
                    "isactive": ISACTIVE
                };
                shopjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    shopjs.ajaxSubmit = function (_command, _params, opt) {
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
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
        if (shopjs['check' + command]) {
            if (!shopjs['check' + command].apply(shopjs, [obj, hint])) {
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
        shopjs.bindbutton();
    }
    var obj = {};
    obj.shopjs = shopjs;
    obj.init = init;
    return obj;
}));
var checknow_data = [];
var checknow_namedata = [];
var flg_index = 0;
jQuery(document).ready(function () {
    window.shop.init();//初始化
    if ($(".pre_title label").text() == "编辑店铺信息") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            var data = JSON.parse(data);
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                console.log(action.length);
                if (action.length == 0) {
                    $("#edit_save").remove();
                }
            }
        });
        var _params = {"id": id};
        var _command = "/shop/select";
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                if (msg.brand_code != "") {
                    if (msg.brand_code.indexOf(',') !== -1) {
                        checknow_data = msg.brand_code.split(",");
                        checknow_namedata = msg.brand_name.split(",");
                    } else {
                        checknow_data.push(msg.brand_code);
                        checknow_namedata.push(msg.brand_name);
                    }
                }
                $("#OWN_CORP option").val(msg.corp.corp_code);
                $("#OWN_CORP option").text(msg.corp.corp_name);
                $("#OWN_BRAND").val(msg.brand_name);
                $("#OWN_BRAND").attr("data-mybcode", msg.brand_code);
                $("#STORE_NAME").val(msg.store_name);
                $("#STORE_NAME").attr("data-name", msg.store_name);
                $("#STORE_ID").val(msg.store_code);
                $("#STORE_ID").attr("data-name", msg.store_code);
                $("#storeId").val(msg.store_id);
                $("#storeId").attr("data-name", msg.store_id);
                $("#OWN_AREA").val(msg.area_name);
                $("#OWN_AREA").attr("data-myacode", msg.area_code);
                if (msg.flg_tob == "Y") {
                    $("#FLG_TOB").val("是");
                } else if (msg.flg_tob == "N") {
                    $("#FLG_TOB").val("否");
                }
                var qrcodeList = msg.qrcodeList;
                var appinput = $(".er_code li input");
                var img = $(".er_code .kuang img")
                console.log(qrcodeList);
                console.log(img);
                for (var i = 0; i < qrcodeList.length; i++) {
                    $(appinput[i]).val(qrcodeList[i].app_name);
                    $(img[i]).attr("src", qrcodeList[i].qrcode);
                }
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var input = $(".checkbox_isactive").find("input")[0];
                if (msg.isactive == "Y") {
                    input.checked = true;
                } else if (msg.isactive == "N") {
                    input.checked = false;
                }
                if (qrcodeList.length > 0) {
                    for (var i = 0; i < qrcodeList.length; i++) {
                        $("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" id="'+qrcodeList[i].app_id+'" value="' + qrcodeList[i].app_name + '" readonly="readonly"><ul></ul>'
                            + '<span class="power create" onclick="getTwoCode(this)">生成</span>'
                            + '<span class="power k_close" style="display: none;">关闭</span>'
                            + '<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
                            + '<div class="kuang"><span class="icon-ishop_6-12 k_close"></span><img src="' + qrcodeList[i].qrcode + '" alt="">'
                            + '</div></li>')
                    }
                    $(".kuang").show();
                    $(".k_close").click(function () {
                        $(this).parents(".kuang").hide();
                    })
                }
                getcorplist();
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
    $("#STORE_ID").blur(function () {
        var _params = {};
        var store_code = $(this).val();//店仓编号
        var store_code1 = $(this).attr("data-name");//标志
        var corp_code = $("#OWN_CORP").val();//公司编号
        if (store_code !== "" && store_code !== store_code1) {
            _params["store_code"] = store_code;
            _params["corp_code"] = corp_code;
            var div = $(this).next('.hint').children();
            oc.postRequire("post", "/shop/storeCodeExist", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#STORE_ID").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    $("#STORE_ID").attr("data-mark", "N");
                    div.addClass("error_tips");
                    div.html("该编号已经存在！");
                }
            })
        }
    })
    $("#storeId").blur(function () {
        var _params = {};
        var store_id= $(this).val();//店仓ID
        var store_id1 = $(this).attr("data-name");//标志
        var corp_code = $("#OWN_CORP").val();//公司编号
        if (store_id !== "" && store_id !== store_id1) {
            _params["store_id"] = store_id;
            _params["corp_code"] = corp_code;
            var div = $(this).next('.hint').children();
            oc.postRequire("post", "/shop/storeCodeExist", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#storeId").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    $("#storeId").attr("data-mark", "N");
                    div.addClass("error_tips");
                    div.html("店铺ID已经存在！");
                }
            })
        }
    })
    $("#STORE_NAME").blur(function () {
        var store_name = $("#STORE_NAME").val();//店铺名称
        var store_name1 = $("#STORE_NAME").attr("data-name");//给店铺的名称是一个字
        var div = $(this).next('.hint').children();
        var corp_code = $("#OWN_CORP").val();
        if (store_name !== "" && store_name !== store_name1) {
            var _params = {};
            _params["store_name"] = store_name;
            _params["corp_code"] = corp_code;
            oc.postRequire("post", "/shop/storeNameExist", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#STORE_NAME").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    $("#STORE_NAME").attr("data-mark", "N");
                }
            })
        }
        
    });
    $("#OWN_AREA").click(function (event) {
        $("#OWN_BRAND").parent().children("#brand_data").css("display", "none");
        $("#area_select").html('');
        event = event || window.event;
        event.stopPropagation();
        $(this).parent().children('ul').toggle();
        var area_param = {"corp_code": $("#OWN_CORP").val()};
        var area_command = "/shop/area";
        oc.postRequire("post", area_command, "", area_param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                var area_html = '';
                var a = null;
                console.log(msg.areas);
                if (msg.areas.length == 0) {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业目前分配区域！"
                    });
                } else {
                    for (var i = 0; i < msg.areas.length; i++) {
                        a = msg.areas[i];
                        area_html += '<li data-areacode="' + a.area_code + '">' + a.area_name + '</li>';
                    }
                    $("#area_select").append(area_html);
                    $("#area_select li").click(function (event) {
                        var this_ = this;
                        var txt = $(this_).text();
                        var a_code = $(this_).attr("data-areacode");
                        $(this_).parent().parent().children(".input_select").val(txt);
                        $(this_).parent().parent().children(".input_select").attr('data-myacode', a_code);
                        $(this_).addClass('rel').siblings().removeClass('rel');
                        $(this_).parent().toggle();
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
        });
    });
    $(".shopadd_oper_btn ul li:nth-of-type(2)").click(function () {//点击关闭按钮跳转到列表页面
        $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
    });
    $("#edit_close").click(function () {//编辑页面点击关闭按钮跳转到列表页面
        sessionStorage.removeItem("edit");
        $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
    });
    $("#OWN_BRAND").click(function () {
        $("#OWN_BRAND").parent().children("#brand_data").toggle();
    });
    $(document).click(function (e) {
        $("#OWN_AREA").parent().children('ul').css("display", "none");
        if ($(e.target).is('#brand_data') || $(e.target).is('#OWN_BRAND') || $(e.target).is('.checkboxselect-item') || $(e.target).is('.checkboxselect-item input')) {
            return;
        } else {
            $("#OWN_BRAND").parent().children("#brand_data").css("display", "none");
        }
    });
    var brandname = [];
    $("#brand_data").remove();
    var brand_param = {"corp_code": $("#OWN_CORP").val()};
    var brand_command = "/shop/brand";
    oc.postRequire("post", brand_command, "", brand_param, function (rdata) {
        console.log(rdata);
        if (rdata.code == "0") {
            var msg = JSON.parse(rdata.message);
            console.log(msg);
            // var index=0;
            var brand_html = '';
            var b = null;
            for (var j = 0; j < msg.brands.length; j++) {
                b = msg.brands[j];
                brand_html += '<option value="' + b.brand_code + '">' + b.brand_name + '</option>';
                brandname.push(b.brand_name);
            }
            var checkboxSelect = new CheckboxSelect({
                input: document.getElementById('OWN_BRAND'),
                hiddeninput: document.getElementById('hiddencheckboxSelect'),
                width: 420,
                opacity: 1,
                data: brandname,
            });
            console.log(brandname);
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
    $("#OWN_BRAND").click(function () {
        $(".checkboxselect-container").html('');
        var brand_param = {"corp_code": $("#OWN_CORP").val()};
        var brand_command = "/shop/brand";
        oc.postRequire("post", brand_command, "", brand_param, function (rdata) {
            console.log(rdata);
            if (rdata.code == "0") {
                var msg = JSON.parse(rdata.message);
                console.log(msg);
                // var index=0;
                var brand_html = '';
                var b = null;
                if (msg.brands.length == 0) {
                    art.dialosg({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业目前没有品牌！"
                    });
                    $(".checkboxselect-container").css("display", "none");
                } else {
                    for (var m = 0; m < msg.brands.length; m++) {
                        b = msg.brands[m];
                        brand_html += '<div class="checkboxselect-item"><input type="checkbox" value="' + b.brand_code + '" data-brandname="' + b.brand_name + '" style="-webkit-appearance: checkbox; width: 14px; height: 14px;">' + b.brand_name + '</div>';
                    }
                    $(".checkboxselect-container").html(brand_html);
                    var check_input = $('.checkboxselect-container input');
                    for (var c = 0; c < check_input.length; c++) {
                        check_input[c].onclick = function () {
                            if (this.checked == true) {
                                checknow_data.push($(this).val())
                                checknow_namedata.push($(this).attr("data-brandname"));
                                $('#OWN_BRAND').val(checknow_namedata.toString());
                                $('#OWN_BRAND').attr('data-mybcode', checknow_data.toString());
                            } else if (this.checked == false) {
                                checknow_namedata.remove($(this).attr("data-brandname"));
                                checknow_data.remove($(this).val());
                                console.log(checknow_data);
                                console.log(checknow_namedata);
                                $('#OWN_BRAND').val(checknow_namedata.toString());
                                $('#OWN_BRAND').attr('data-mybcode', checknow_data.toString());
                            }
                        }
                    }
                    var s = $("#OWN_BRAND").attr("data-mybcode");
                    var c_input = $('.checkboxselect-container input');
                    var ss = '';
                    if (s.indexOf(',')!==-1) {
                        ss = s.split(",");
                        for (var i = 0; i < ss.length; i++) {
                            for (var j = 0; j < c_input.length; j++) {
                                if ($(c_input[j]).val() == ss[i]) {
                                    $(c_input[j]).attr("checked", true);
                                }
                            }
                        }
                    } else {
                        ss = s;
                        for (var j = 0; j < c_input.length; j++) {
                            if ($(c_input[j]).val() == ss) {
                                $(c_input[j]).attr("checked", true);
                            }
                        }
                    }
                }
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    });
    $(".corp_select").click(function () {
        $("#OWN_AREA").val('');
        $("#OWN_BRAND").val('');
        $('#OWN_BRAND').attr('data-mybcode', '');
        $("#OWN_AREA").attr("data-myacode", "");
        flg_index++;
        checknow_data = [];
        checknow_namedata = [];
    });
});
function getcorplist() {
    //获取所属企业列表
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        console.log(data);
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            console.log(msg);
            var corp_html = '';
            var c = null;
            for (var i = 0; i < msg.corps.length; i++) {
                c = msg.corps[i];
                corp_html += '<option value="' + c.corp_code + '">' + c.corp_name + '</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    $("#STORE_ID").val("");
                    $("#storeId").val("");
                    $("#STORE_NAME").val("");
                    $("input[verify='Code']").attr("data-mark", "");
                    $("#STORE_NAME").attr("data-mark", "");
                }
            })
            $('.searchable-select-item').click(function () {
                $("#STORE_ID").val("");
                $("#storeId").val("");
                $("#STORE_NAME").val("");
                $("input[verify='Code']").attr("data-mark", "");
                $("#STORE_NAME").attr("data-mark", "");

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
function getAppName(a) {
    var corp_code = $("#OWN_CORP").val();
    var param = {};
    param["corp_code"] = corp_code;
    var _command = "/corp/selectWx";
    oc.postRequire("post", _command, "", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var list = msg.list;
            console.log(list);
            $(a).next("ul").empty();
            for (var i = 0; i < list.length; i++) {
                $(a).next("ul").append('<li data-id="' + list[i].app_id + '">' + list[i].app_name + '</li>')
            }
            $(a).next("ul").find("li").click(function () {
                var value = $(this).html();
                console.log(value);
                $(a).val(value);
                $(a).attr("id", $(this).attr("data-id"));
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
//点击生成二维码
function getTwoCode(b) {
    var input=$(b).prevAll("input").val();
    close_two_code();
    if(input!==""){
        $(b).hide();
        $(b).nextAll(".k_close").show();
    }
    var user_creat = "/shop/creatQrcode";
    var store_code = $('#STORE_ID').val();
    var corp_code = $('#OWN_CORP').val();
    var app_id = $(b).prevAll("input").attr("id");
    var _params = {};
    _params["store_code"] = store_code;
    _params["corp_code"] = corp_code;
    _params["app_id"] = app_id;
    if (app_id == "" || app_id == undefined) {
        alert("请选择公众号!");
        return;
    }
    oc.postRequire("post", user_creat, "", _params, function (data) {
        var message = data.message;
        if (data.code == "0") {
            $(b).nextAll(".kuang").show();
            $(b).nextAll(".kuang").find("img").attr("src", message);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}

//生成二维码下拉框
function select_down(a) {
    if ($(a).next().css("display") == "none") {
        $(a).next().show();
        $(a).find("ul").empty();
        getAppName(a);
    } else {
        $(a).next().hide();
    }
    $(a).blur(function () {
        var ul = $(this).next();
        setTimeout(function () {
            ul.hide();
        }, 200);
    })
}
//关闭二维码
function close_two_code() {
    $(".k_close").click(function () {
        $(this).nextAll(".kuang").hide();
        $(this).hide();
        $(this).prev(".create").show();
    })
}

$("#add_app_id").click(function () {
    $("#add_app_id").before('<li class="app_li"><input onclick="select_down(this)" readonly="readonly"><ul></ul>'
        + '<span class="power create" onclick="getTwoCode(this)">生成</span>'
        + '<span class="power k_close" style="display: none;">关闭</span>'
        + '<span class="power remove_app_id" onclick="remove_app_id(this)">删除</span>'
        + '<div class="kuang"><img src="" alt="">'
        + '</div></li>')

    close_two_code();
})
function remove_app_id(obj) {
    var store_code = $("#STORE_ID").val();//店铺编号
    var corp_code = $("#OWN_CORP").val();//公司编号
    var app_id = $(obj).prevAll("input").attr("id");
    var src=$(obj).next(".kuang").children().attr("src");
    var param={
        "corp_code":corp_code,
        "store_code":store_code,
        "app_id":app_id
    }
    if(src!==""){
        oc.postRequire("post","/shop/deletQrcode","",param,function (data) {
            if(data.code=="0"){
                $(obj).parent().remove();
            }else if(data.code=="-1"){
                alert("删除失败!");
            }
        })
    }else if(src ==""){
        $(obj).parent().remove();
    }
}
