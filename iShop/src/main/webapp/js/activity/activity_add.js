var oc = new ObjectControl();
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
                var execution_input = $("#execution_input").val();
                var start_time = $("#start").val();
                var end_time = $("#end").val();
                var target_vip = $("#target_vip").val();
                var executor = $("#executor").val();
                var send_title = $("#send_title").val();
                var summary = $("#summary").val();
                var task_title = $("#task_title").val();
                var task_dec = $("#task_dec").val();
                var short_msg = $("#short_msg").val();
                var outer_link = $("#outer_link").val();
                var activity_content = nr;
                var creat_link = $("#creat_link").val();
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
                    "activity_state":"",
                    "task_code":"",
                    "task_title": task_title,
                    "task_desc": task_dec,
                    "msg_info": short_msg,
                    "wechat_title": send_title,
                    "wechat_desc": summary,
                    "activity_content": activity_content,
                    "activity_url": outer_link,
                    "content_url": creat_link,
                    "isactive": ISACTIVE
                };
                areajs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#edit_save").click(function () {
            var codeMark = $("#AREA_ID").attr("data-mark");//区域名称是否唯一的标志
            var nameMark = $("#AREA_NAME").attr("data-mark");//区域编号是否唯一的标志
            if (areajs.firstStep()) {
                if (nameMark == "N") {
                    var div = $("#AREA_NAME").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                if (codeMark == "N") {
                    var div = $("#AREA_ID").next('.hint').children();
                    div.html("该编号已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var ID = sessionStorage.getItem("id");
                var AREA_ID = $("#AREA_ID").val();
                var AREA_NAME = $("#AREA_NAME").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/area/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "id": ID,
                    "corp_code": OWN_CORP,
                    "area_code": AREA_ID,
                    "area_name": AREA_NAME,
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
    if ($(".pre_title label").text() == "编辑区域信息") {
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
        var _command = "/area/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            console.log(data);
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                $("#AREA_ID").val(msg.area_code);
                $("#AREA_ID").attr("data-name", msg.area_code);
                $("#AREA_NAME").val(msg.area_name);
                $("#AREA_NAME").attr("data-name", msg.area_name);
                // $("#OWN_CORP option").val(msg.corp.corp_code);
                // $("#OWN_CORP option").text(msg.corp.corp_name);
                $("#area_shop").val("共" + msg.store_count + "个店铺");
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
                getcorplist(msg.corp.corp_code);
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
                    $("#AREA_ID").val("");
                    $("#AREA_NAME").val("");
                    $("#AREA_ID").attr("data-mark", "");
                    $("#AREA_NAME").attr("data-mark", "");
                }
            })
            $('.searchable-select-item').click(function () {
                $("#AREA_ID").val("");
                $("#AREA_NAME").val("");
                $("#AREA_ID").attr("data-mark", "");
                $("#AREA_NAME").attr("data-mark", "");
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
function bianse(){
    $(".screen_content_l li:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:even").css("backgroundColor","#ededed");
    $(".screen_content_r li:odd").css("backgroundColor","#fff");
    $(".screen_content_r li:even").css("backgroundColor","#ededed");
}
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
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
})
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
})
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
})
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
})
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
})
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
})
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_staff").show();
    whir.loading.remove();//移除遮罩层
});
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    whir.loading.remove();//移除遮罩层
})
//店铺关闭
$("#screen_close_shop").click(function(){
    $("#screen_shop").hide();
    $("#screen_staff").show();
    whir.loading.remove();//移除遮罩层
    $("#screen_shop .screen_content_l").unbind("scroll");
})
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_staff").show();
    whir.loading.remove();//移除遮罩层
})
//获取品牌列表
function getbrandlist(){
    var corp_code = $('#OWN_CORP').val();
    var searchValue=$("#brand_search").val();
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
            var brand_html_right='';
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
            var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            bianse();
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
function getarealist(a){
    var corp_code = $('#OWN_CORP').val();
    var area_command = "/area/selAreaByCorpCode";
    var searchValue=$("#area_search").val().trim();
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
            var area_html_right='';
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
                })
            }
            isscroll=true;
            var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            bianse();
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
function getstorelist(a){
    var corp_code = $('#OWN_CORP').val();
    var area_code =$('#area_num').attr("data-areacode");//
    var brand_code=$('#brand_num').attr("data-brandcode");
    var searchValue=$("#store_search").val();
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
    // oc.postRequire("post","/user/stores","", _param, function(data) {
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
            if(!isscroll){
                $("#screen_shop .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num);
                    };
                })
            }
            isscroll=true;
            var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            bianse();
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
function getstafflist(a){
    var corp_code = $('#OWN_CORP').val();
    var searchValue=$('#staff_search').val();
    var area_code =$("#staff_area_num").attr("data-areacode");
    var brand_code=$("#staff_brand_num").attr("data-brandcode");
    var store_code=$("#staff_shop_num").attr("data-storecode");
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
    $("#mask").css("z-index","10002");
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
            if(!isscroll){
                $("#screen_staff .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
                })
            }
            isscroll=true;
            $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
            $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
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
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            area_code+=r+",";
            name+=h+","
        }else{
            area_code+=r;
            name+=h;
        }
    }
    if($("#send_mode").val()=="指定店铺"){
        isscroll=false;
        shop_num=1;
        $("#area_num").attr("data-areacode",area_code);
        $("#area_num").val("已选"+li.length+"个");
        $("#screen_shop .screen_content_l ul").empty();
        $("#screen_shop .screen_content_l").unbind("scroll");
        $("#screen_area").hide();
        $("#screen_shop").show();
        getstorelist(shop_num);
    }
    if($("#send_mode").val()=="指定员工"){
        isscroll=false;
        staff_num=1;
        $("#staff_area_num").attr("data-areacode",area_code);
        $("#staff_area_num").val("已选"+li.length+"个");
        $("#area_num").attr("data-areacode",area_code);
        $("#area_num").val("已选"+li.length+"个");
        $("#screen_staff .screen_content_l ul").empty();
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_area").hide();
        $("#screen_staff").show();
        getstafflist(staff_num);
    }
    if($("#send_mode").val()=="指定区域"){
        $("#sendee_r").attr("data-code",area_code);
        $("#sendee_r").attr("data-name",name);
        $("#screen_area").hide();
        $("#sendee_r").val("已选"+li.length+"个");
        $("#sendee_r").attr("data-type","area");
        whir.loading.remove();//移除遮罩层
    }
})
//点击店铺的确定
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var name=""
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
    if($("#send_mode").val()=="指定员工"){
        isscroll=false;
        staff_num=1;
        $("#staff_shop_num").attr("data-storecode",store_code);
        $("#staff_shop_num").val("已选"+li.length+"个");
        $("#screen_staff .screen_content_l ul").empty();
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_shop").hide();
        $("#screen_staff").show();
        getstafflist(staff_num);
    }
    if($("#send_mode").val()=="指定店铺"){
        $("#sendee_r").attr("data-code",store_code);
        $("#sendee_r").attr("data-name",name);
        $("#screen_shop").hide();
        $("#sendee_r").val("已选"+li.length+"个");
        $("#sendee_r").attr("data-type","store");
        whir.loading.remove();//移除遮罩层
    }
})
//点击员工的确定
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var phone="";
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find("input").attr("data-phone");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            store_code+=r+",";
            phone+=p+",";
            name+=h+",";
        }else{
            store_code+=r;
            phone+=p;
            name+=h;
        }
    }
    $("#sendee_r").attr("data-code",store_code);
    $("#sendee_r").attr("data-phone",phone);
    $("#sendee_r").attr("data-name",name);
    $("#screen_staff").hide();
    $("#sendee_r").val("已选"+li.length+"个");
    $("#sendee_r").attr("data-type","staff");
    whir.loading.remove();//移除遮罩层
})
//点击品牌的确定
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        if(i<li.length-1){
            brand_code+=r+",";
        }else{
            brand_code+=r;
        }
    }
    if($("#send_mode").val()=="指定员工"){
        isscroll=false;
        staff_num=1;
        $("#staff_brand_num").attr("data-brandcode",brand_code);
        $("#staff_brand_num").val("已选"+li.length+"个");
        $("#brand_num").attr("data-brandcode",brand_code);
        $("#brand_num").val("已选"+li.length+"个");
        $("#screen_staff .screen_content_l ul").empty();
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_brand").hide();
        $("#screen_staff").show();
        getstafflist(staff_num);
    }
    if($("#send_mode").val()=="指定店铺"){
        isscroll=false;
        shop_num=1;
        $("#brand_num").attr("data-brandcode",brand_code);
        $("#brand_num").val("已选"+li.length+"个");
        $("#screen_shop .screen_content_l ul").empty();
        $("#screen_shop .screen_content_l").unbind("scroll");
        $("#screen_brand").hide();
        $("#screen_shop").show();
        getstorelist(shop_num);
    }
})
//店铺里面的区域点击
$("#shop_area").click(function(){
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
})
//店铺里面的品牌点击
$("#shop_brand").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
})
//员工里面的区域点击
$("#staff_area").click(function(){
    console.log(123);
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
})
//员工里面的店铺点击
$("#staff_shop").click(function(){
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
})
//员工里面的品牌点击
$("#staff_brand").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
})
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
    bianse();
    $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
    $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
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
    bianse();
}
//点击右移
$(".shift_right").click(function(){
    var right="only";
    var div=$(this);
    removeRight(right,div);
})
//点击右移全部
$(".shift_right_all").click(function(){
    var right="all";
    var div=$(this);
    removeRight(right,div);
})
//点击左移
$(".shift_left").click(function(){
    var left="only";
    var div=$(this);
    removeLeft(left,div);
})
//点击左移全部
$(".shift_left_all").click(function(){
    var left="all";
    var div=$(this);
    removeLeft(left,div);
})
