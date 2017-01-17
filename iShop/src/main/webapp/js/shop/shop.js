var oc = new ObjectControl();
var area_next=false;
var area_num=1;
var isscroll=false;
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
                var location = $("#show_map").attr("data-location");
                var province="";
                var city = "";
                var area = "";
                var street = "";
                var address = $("#STORE_address").val();
                address = address.split("/");
                if(location==undefined){
                    location="";
                }
                if(address.length > 0 && address[0] !== ""){
                    province = address[0];//获取省份code
                    city = address[1];//获取市code
                    area = address[2];//获取县区code
                    street = address[3];//获取街道值
                }
                if(city == undefined){
                    console.log(address);
                    console.log(city);
                    alert("您还没选择城市");
                    return ;
                }
                if(area == undefined){
                    alert("您还没选择县区");
                    return ;
                }
                if(street == undefined){
                    street = "";
                }
                //var OWN_AREA = $("#OWN_AREA").attr("data-myacode");
                //var OWN_BRAND = $("#OWN_BRAND").attr("data-mybcode");
                var a=$('#OWN_AREA_All input');
                var AREA_CODE="";
                for(var i=0;i<a.length;i++){
                    var u=$(a[i]).attr("data-code");
                    if(i<a.length-1){
                        AREA_CODE+=u+",";
                    }else{
                        AREA_CODE+=u;
                    }
                }
                var b=$('#OWN_BRAND_All input');
                var BRAND_CODE="";
                for(var i=0;i<b.length;i++){
                    var U=$(b[i]).attr("data-code");
                    if(i<b.length-1){
                        BRAND_CODE+=U+",";
                    }else{
                        BRAND_CODE+=U;
                    }
                }
                if (AREA_CODE == "") {
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属区域不能为空"
                    });
                    return;
                }
                if (BRAND_CODE == "") {
                    art.dialog({
                        zIndex:10003,
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
                    "brand_code": BRAND_CODE,
                    "store_code": STORE_ID,
                    "store_id": store_id,
                    "province":province,
                    "city":city,
                    "area":area,
                    "street":street,
                    "store_location":location,
                    "area_code": AREA_CODE,
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
                //var OWN_AREA = $("#OWN_AREA").attr("data-myacode");
                //var OWN_BRAND = $("#OWN_BRAND").attr("data-mybcode");
                var STORE_ID = $("#STORE_ID").val();
                var STORE_NAME = $("#STORE_NAME").val();
                var is_zhiying = $("#FLG_TOB").val();
                var a=$('#OWN_AREA_All input');
                var location = $("#show_map").attr("data-location");
                var AREA_CODE="";
                var province="";
                var city = "";
                var area = "";
                var street = "";
                var address = $("#STORE_address").val();
                address = address.split("/");
                if(location==undefined){
                    location="";
                }
                if(address.length > 0 && address[0] !== ""){
                    province = address[0];//获取省份code
                    city = address[1];//获取市code
                    area = address[2];//获取县区code
                    street = address[3];//获取街道值
                }
                if(city == undefined){
                    console.log(address);
                    console.log(city);
                    alert("您还没选择城市");
                    return ;
                }
                if(area == undefined){
                    alert("您还没选择县区");
                    return ;
                }
                if(street == undefined){
                    street = "";
                }
                for(var i=0;i<a.length;i++){
                    var u=$(a[i]).attr("data-code");
                    if(i<a.length-1){
                        AREA_CODE+=u+",";
                    }else{
                        AREA_CODE+=u;
                    }
                }
                var b=$('#OWN_BRAND_All input');
                var BRAND_CODE="";
                for(var i=0;i<b.length;i++){
                    var U=$(b[i]).attr("data-code");
                    if(i<b.length-1){
                        BRAND_CODE+=U+",";
                    }else{
                        BRAND_CODE+=U;
                    }
                }

                if (AREA_CODE == "") {
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属区域不能为空"
                    });
                    return;
                }
                if(a>20){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所选区域不能超过20个"
                    });
                    return;
                }
                if (BRAND_CODE == "") {
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所属品牌不能为空"
                    });
                    return;
                }
                if(b>10){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "所选品牌不能超过10个"
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
                var logo=""
                var img=$("#STORE_LOGO").parent().prev("img").attr("src");
                if(img!==undefined){
                    logo=img;
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
                    "brand_code": BRAND_CODE,
                    "store_code": STORE_ID,
                    "province":province,
                    "city":city,
                    "area":area,
                    "street":street,
                    "store_location":location,
                    "store_id": store_id,
                    "area_code": AREA_CODE,
                    "store_name": STORE_NAME,
                    "flg_tob": FLG_TOB,
                    "logo":logo,
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
                if(_command=="/shop/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/shop/shop_edit.html");
                }
                if(_command=="/shop/edit"){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
            } else if (data.code == "-1") {
                art.dialog({
                    zIndex:10003,
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
var areaData=[];
var areaDataCode=[];
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
                    $("#edit_close").css("margin-left","120px");
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
                if (msg.area_code != "") {
                    if (msg.area_code.indexOf(',') !== -1) {
                        areaDataCode= msg.area_code.split(",");
                        areaData = msg.area_name.split(",");
                    } else {
                        areaDataCode.push(msg.area_code);
                        areaData.push(msg.area_name);
                    }
                }
                console.log(areaDataCode);
                console.log(areaData);
                for(var i=0;i<areaData.length;i++){
                    $('#OWN_AREA_All').append("<p><input type='text 'readonly='readonly' style='width: 348px;margin-right: 10px' data-code='"+areaDataCode[i]+"'  value='"+areaData[i]+"'><span class='power remove_app_id'>删除</span></p>");
                }
                for(var i=0;i<checknow_namedata.length;i++){
                    $('#OWN_BRAND_All').append("<p><input type='text 'readonly='readonly' style='width: 348px;margin-right: 10px' data-code='"+checknow_data[i]+"'  value='"+checknow_namedata[i]+"'><span class='power remove_app_id'>删除</span></p>");
                }
                var logo=msg.logo;
                if(logo!==""&&logo!==undefined){
                    var img="<img style='width: 60px;margin-bottom: 10px;' src='"+logo+"' alt='暂无图片'>";
                    $("#STORE_LOGO").parent().before(img);
                }
                // $("#OWN_CORP option").val(msg.corp.corp_code);
                // $("#OWN_CORP option").text(msg.corp.corp_name);
                //$("#OWN_BRAND").val(msg.brand_name);
                //$("#OWN_BRAND").attr("data-mybcode", msg.brand_code);
                $("#STORE_NAME").val(msg.store_name);
                $("#STORE_NAME").attr("data-name", msg.store_name);
                $("#STORE_ID").val(msg.store_code);
                $("#STORE_ID").attr("data-name", msg.store_code);
                $("#storeId").val(msg.store_id);
                $("#storeId").attr("data-name", msg.store_id);
                $("#show_map").attr("data-location",msg.store_location);
                var address_code = "";
                var address = "";
                if(msg.province_location_name!==""){
                    if(msg.street == ""){
                        address=msg.province_location_name+'/'+msg.city_location_name+'/'+msg.area_location_name;
                        address_code=msg.province+','+msg.city+','+msg.area;
                    }else {
                        address=msg.province_location_name+'/'+msg.city_location_name+'/'+msg.area_location_name+'/'+msg.street;
                        address_code=msg.province+','+msg.city+','+msg.area+','+msg.street;
                    }
                }
                // $("#STORE_address").attr("title",address);
                // $("#STORE_address").attr("data-code",address_code);
                $("#STORE_address").val(address);
                //$("#OWN_AREA").val(msg.area_name);
                //$("#OWN_AREA").attr("data-myacode", msg.area_code);
                //$("#OWN_AREA").attr("title",msg.area_name);
                //$("#OWN_BRAND").attr("title",msg.brand_name);
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
                getcorplist(msg.corp.corp_code);
            } else if (data.code == "-1") {
                art.dialog({
                    zIndex:10003,
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

    $(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false});
    $("#screen_close_area").click(function(){
        $("#screen_area").hide();
    });
    $("#screen_close_brand").click(function(){
        $("#screen_brand").hide();
    });
    $("#ADD_AREA").click(function () {
        $("#screen_area .screen_content_l").unbind("scroll");
        var arr=whir.loading.getPageSize();
        area_num=1;
        $("#screen_area .s_pitch span").html("0");
        $("#area_search").val("");
        $("#screen_area .screen_content_l ul").empty();
        $("#screen_area .screen_content_r ul").empty();
        $("#screen_area").show();
        var left=(arr[0]-$("#screen_area").width())/2;
        var tp=(arr[3]-$("#screen_area").height())/2+40;
        $("#screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
        getArea(area_num);
        isscroll=false;
        console.log(1);
    });
    $("#ADD_BRAND").click(function(){
        $("#screen_brand .screen_content_l").unbind("scroll");
        var arr=whir.loading.getPageSize();
        area_num=1;
        $("#brand_search").val("");
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_l ul").empty();
        $("#screen_brand .screen_content_r ul").empty();
        $("#screen_brand").show();
        var left=(arr[0]-$("#screen_brand").width())/2;
        var tp=(arr[3]-$("#screen_brand").height())/2+40;
        $("#screen_brand").css({"left":left+"px","top":tp+"px","position":"fixed"});
        //getArea(area_num);
        getBrand(area_num);
        isscroll=false;
        console.log(1);
    });
    //点击右移选中
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
    //点击列表显示选中状态
    $(".screen_content").on("click","li",function(){
        var input=$(this).find("input")[0];
        var thinput=$("thead input")[0];
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            input.checked = false;
        }
    });
    //获取区域
    function getArea(a){
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        var area_command = "/area/selAreaByCorpCode";
        var corp_code = $('#OWN_CORP').val();
        var searchValue=$("#area_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var area_param = {};
        area_param["searchValue"]=searchValue;
        area_param["pageSize"]=pageSize;
        area_param["pageNumber"]=pageNumber;
        area_param["corp_code"]=corp_code;
        oc.postRequire("post", area_command, "", area_param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list=JSON.parse(msg.list);
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var area_html = '';
                if (list.length == 0) {
                   
                } else {
                   if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
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
                $("#screen_area .screen_content_l ul").append(area_html);
                if(!isscroll){
                        $("#screen_area .screen_content_l").bind("scroll",function () {
                            console.log("滚动了吗");
                            var nScrollHight = $(this)[0].scrollHeight;
                            var nScrollTop = $(this)[0].scrollTop;
                            var nDivHight=$(this).height();
                            if(nScrollTop + nDivHight >=nScrollHight){
                                if(area_next){
                                    return;
                                }
                                getArea(area_num);
                                console.log(4)
                            }
                        });
                }
                isscroll=true;
                var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
                }
            } else if (data.code == "-1") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }

            whir.loading.remove();//移除加载框
        });

    }
    //获取品牌
    function getBrand(a){
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        var brand_command = "/shop/brand";
        var brand_code = $("#OWN_CORP").val();
        var searchValue=$("#brand_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var area_param = {};
        area_param["searchValue"]=searchValue;
        area_param["pageSize"]=pageSize;
        area_param["pageNumber"]=pageNumber;
        area_param["corp_code"]=brand_code;
        oc.postRequire("post", brand_command, "", area_param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list=msg.brands;
                var area_html = '';
                if (list.length == 0) {
                    if(a==1){
                        for(var h=0;h<9;h++){
                            area_html+="<li></li>";
                        }
                    }
                    area_next=true;
                } else {
                    if(list.length<9&&a==1){
                        for (var i = 0; i < list.length; i++) {
                            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16' title='"+list[i].brand_name+"'>"+list[i].brand_name+"</span></li>"
                        }
                        for(var j=0;j<9-list.length;j++){
                            area_html+="<li></li>"
                        }
                    }else if(list.length>=9||list.length<9&&a>1){
                        for (var i = 0; i < list.length; i++) {
                            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16' title='"+list[i].brand_name+"'>"+list[i].brand_name+"</span></li>"
                        }
                    }
                    area_num++;
                    area_next=false;

                }
                $("#screen_brand .screen_content_l ul").append(area_html);
                if(!isscroll){
                        $("#screen_brand .screen_content_l").bind("scroll",function () {
                            var nScrollHight = $(this)[0].scrollHeight;
                            var nScrollTop = $(this)[0].scrollTop;
                            var nDivHight=$(this).height();
                            if(nScrollTop + nDivHight >=nScrollHight){
                                if(area_next){
                                    return;
                                }
                                getBrand(area_num);
                            }
                        });
                }
                isscroll=true;
                var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
                }
            } else if (data.code == "-1") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });

    }
    //区域搜索
    $("#area_search").keydown(function(){
        $("#screen_area .screen_content_l").unbind("scroll");
        isscroll=false;
        var event=window.event||arguments[0];
        area_num=1;
        if(event.keyCode == 13){
            $("#screen_area .screen_content_l ul").empty();
            getArea(area_num);
        }
    });
    //品牌搜索
    $("#brand_search").keydown(function(){
        $("#screen_brand .screen_content_l").unbind("scroll");
        isscroll=false;
        var event=window.event||arguments[0];
        area_num=1;
        if(event.keyCode == 13){
            $("#screen_brand .screen_content_l ul").empty();
            getBrand(area_num);
        }
    });
    //区域放大镜搜索
    $("#area_search_f").click(function(){
        $("#screen_area .screen_content_l").unbind("scroll");
        area_num=1;
        $("#screen_area .screen_content_l ul").empty();
        getArea(area_num);
        console.log(3)
    });
    //品牌放大镜搜索
    $("#brand_search_f").click(function(){
        $("#screen_brand .screen_content_l").unbind("scroll");
        area_num=1;
        $("#screen_brand .screen_content_l ul").empty();
        getBrand(area_num);
        console.log(3)
    });
$("#screen_que_area").click(function(){
    var li=$(this).prev().children(".screen_content_r").find("ul li");
    var hasli=$("#OWN_AREA_All p");
    var a=$('#OWN_AREA_All input');
    if((li.length+hasli.length)>20){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            zIndex:10003,
            content: "所选区域不能超过20个"
        });
        return;
    }
    if(li.length>0){
        for(var i=0;i<li.length;i++){
            for(var j=0;j<a.length;j++){
                if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
                    $(a[j]).parent("p").remove();
                }
            }
            $('#OWN_AREA_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id'>删除</span></p>");
        }
    }
    $("#screen_area").hide();
});
    $("#screen_que_brand").click(function(){
    var li=$(this).prev().children(".screen_content_r").find("ul li");
    var hasli=$("#OWN_BRAND_All p");
    var a=$('#OWN_BRAND_All input');
    if((li.length+hasli.length)>10){
            art.dialog({
                zIndex:10003,
                time: 1,
                lock: true,
                cancel: false,
                zIndex:10003,
                content: "所选品牌不能超过10个"
            });
        return;
        }
        if(li.length>0){
        for(var i=0;i<li.length;i++){
            for(var j=0;j<a.length;j++){
                if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
                    $(a[j]).parent("p").remove();
                }
            }
            $('#OWN_BRAND_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id'>删除</span></p>");
        }
    }
        $("#screen_brand").hide();
});
    //删除
    $(".xingming").on("click",".remove_app_id",function(){
        $(this).parent().remove();
    });
    $(".shopadd_oper_btn ul li:nth-of-type(2)").click(function () {//点击关闭按钮跳转到列表页面
        $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
    });
    $("#edit_close").click(function () {//编辑页面点击关闭按钮跳转到列表页面
        sessionStorage.removeItem("edit");
        $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
    });
    $("#back_shop").click(function () {//编辑页面点击关闭按钮跳转到列表页面
        sessionStorage.removeItem("edit");
        $(window.parent.document).find('#iframepage').attr("src", "/shop/shop.html");
    });
    //$("#OWN_BRAND").click(function () {
    //    //$("#OWN_BRAND").parent().children("#brand_data").toggle();
    //});
    $(document).click(function (e) {

        if($(e.target).is("li")||$(e.target).is("#area_select")||$(e.target).is('input')){
            return;
        }else {
            $("#OWN_AREA").parent().children('ul').css("display", "none");
        }
        if ($(e.target).is('#brand_data') || $(e.target).is('#OWN_BRAND') || $(e.target).is('.checkboxselect-item') || $(e.target).is('.checkboxselect-item input')) {
            return;
        } else {
            $("#OWN_BRAND").parent().children("#brand_data").css("display", "none");
        }
        if($(e.target).is('#STORE_address') || $(e.target).is('.address_container a') || $(e.target).is('.dl_box dl dd') || $(e.target).is('.address_content') || $(e.target).is('#enter') ||$(e.target).is('.drop') ||$(e.target).is('#show_map .BMap_mask')||$(e.target).is("#show_map .BMap_Marker")){
            return ;
        } else {
            $(".address_container").hide();
        }
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
    $("#STORE_address").click(function () {
        $(".address_container").toggle();
    });
    getProvince();
    uploadLOGO();
});
function getcorplist(a) {
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
            if(a!==""){
                $("#OWN_CORP option[value='"+a+"']").attr("selected","true");
            }
            $('.corp_select select').searchableSelect();
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    $("#OWN_AREA_All").html("");
                    $("#OWN_BRAND_All").html("");
                    $("#STORE_ID").val("");
                    $("#storeId").val("");
                    $("#STORE_NAME").val("");
                    $("input[verify='Code']").attr("data-mark", "");
                    $("#STORE_NAME").attr("data-mark", "");
                }
            });
            $('.searchable-select-item').click(function () {
                $("#OWN_AREA_All").html("");
                $("#OWN_BRAND_All").html("");
                $("#STORE_ID").val("");
                $("#storeId").val("");
                $("#STORE_NAME").val("");
                $("input[verify='Code']").attr("data-mark", "");
                $("#STORE_NAME").attr("data-mark", "");

            })
        } else if (data.code == "-1") {
            art.dialog({
                zIndex:10003,
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
                zIndex:10003,
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
//获取省市区
function getProvince() {
    var param={};
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        if(data.code == "0"){
            $(".dl_box dl dd").empty();
            var msg = JSON.parse(data.message);
            for(var i=0;i<msg.length;i++){
                $("#province dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#province a").click(function () {
                var val=$(this).html();
                var j=$(this).index();
                var location = msg[j].lat+","+msg[j].lng;
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(2)").trigger("click");
                $("#county dl dd").empty();
                getCity();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-code",$(this).attr("data-code"));
                // $("#show_map").attr("data-location",location);
            })
        }else {
            console.log(data.message);
        }
    })
}
function getCity() {
    var param={};
    param['higher_level_code'] = $("#province .current").attr("data-code");
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        var msg = JSON.parse(data.message);
        if(data.code == "0"){
            $("#city dl dd").empty();
            for(var i=0;i<msg.length;i++){
                $("#city dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#city a").click(function () {
                var j=$(this).index();
                var location = msg[j].lat+","+msg[j].lng;
                var val=$("#province .current").html();
                    val+='/'+$(this).html();
                var data_code = $("#province .current").attr("data-code")+','+$(this).attr("data-code");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(3)").trigger("click");
                getCounty();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-code",data_code);
                // $("#show_map").attr("data-location",location);
            })
        }else {
            console.log(data.message);
        }
    })
}
function getCounty() {
    var param={};
    param['higher_level_code'] = $("#city .current").attr("data-code");
    oc.postRequire('post','/location/getProvince','',param,function (data) {
        var msg = JSON.parse(data.message);
        if(data.code == "0"){
            $("#county dl dd").empty();
            for(var i=0;i<msg.length;i++){
                $("#county dl dd").append('<a title="'+msg[i].short_name+'" data-code="'+msg[i].location_code+'" href="javascript:;">'+msg[i].short_name+'</a>');
            }
            $("#county a").click(function () {
                var j=$(this).index();
                var location = msg[j].lat+","+msg[j].lng;
                var val=$("#province .current").html()+'/'+$("#city .current").html()+'/'+$(this).html();
                var data_code = $("#province .current").attr("data-code")+','+$("#city .current").attr("data-code")+','+$(this).attr("data-code");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(3)").trigger("click");
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-code",data_code);
                // $("#show_map").attr("data-location",location);
            })
        }else {
            console.log(data.message);
        }
    })
}
$("#address_nav a").click(function () {
    var index=$(this).index();
    $(this).addClass("address_liActive");
    $(this).siblings().removeClass("address_liActive");
    $(".address_content").children('.dl_box').eq(index).show();
    $(".address_content").children('.dl_box').eq(index).siblings().hide();
})
//添加街道
$("#enter").click(function () {
    var val = $(".street").val().trim();
    var input = $("#STORE_address").val();
    var input_l =  input.split("/");
        input = input_l[0]+'/'+input_l[1]+'/'+input_l[2];
    // var input = $("#province .current").html()+'/'+$("#city .current").html()+'/'+$("#county .current").html();
    var current = input_l[2];
    var data_code = $("#STORE_address").attr("data-code");
    if(current==undefined){
        alert("您还没选择县区");
    }else {
        if(val == ""){
            $("#STORE_address").val(input);
            $("#STORE_address").attr("data-code",data_code);
        }else {
            input+='/'+val;
            data_code+=','+val;
            $("#STORE_address").val(input);
            $("#STORE_address").attr("data-code",data_code);
            var location = input_l[1]+input_l[2]+val;
            $("#show_map").attr("data-location",location);
        }
    }
});
//logo OSS
function uploadLOGO() {
    var _this=this;
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('STORE_LOGO').addEventListener('change', function (e) {
        whir.loading.add("上传中,请稍后...",0.5);
        var file = e.target.files[0];
        var time=_this.getNowFormatDate();
        var corp_code=$("#OWN_CORP").val();
        var store_code=$("#STORE_ID").attr("data-name");
        var storeAs='STORE/logo'+corp_code+'_'+store_code+'_'+time+'.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
            var img="<img style='width: 60px;margin-bottom: 10px;' src='"+url+"' alt='暂无图片'>";
            var len = $("#STORE_LOGO").parent().prevAll("img").length;
            if(len == 0){
                $("#STORE_LOGO").parent().before(img);
            }else {
                $("#STORE_LOGO").parent().prev("img").replaceWith(img);
            }
            whir.loading.remove();
        }).catch(function (err) {
            console.log(err);
        });
    });
}
function getNowFormatDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var H=date.getHours();
    var M=date.getMinutes();
    var S=date.getSeconds();
    var m=date.getMilliseconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = ""+year+month+strDate+H+M+S+m;
    return currentdate
}
//创建地图
$("#address_nav a:last-child").click(function () {
    var map = new BMap.Map("show_map", {enableMapClick:false});//构造底图时，关闭底图可点功能
    var location_detail = $("#show_map").attr("data-location");
    var point;
    if(location_detail==undefined || location_detail==""){
         point = new BMap.Point(116.404, 39.915);
        function myFun(result){
            var cityName = result.name;
            map.setCenter(cityName);
        }
        var myCity = new BMap.LocalCity();
        myCity.get(myFun);
        map.centerAndZoom(point, 15);
        var marker = new BMap.Marker(point); //创建marker对象
        map.addOverlay(marker);
        marker.enableDragging(); //marker可拖拽
        var label = new BMap.Label("该坐标将用于员工签到距离计算",{offset:new BMap.Size(20,-10)});
        label.setStyle({
            "font-size":"10px",
            "color":"#333",
            "width":"171px",
            "max-width":"200px",
            "height":"20px"
        });
        marker.setLabel(label);
        map.addEventListener("click",function(e){
            console.log(e.point.lng+","+e.point.lat);// 单击地图获取坐标点；
            map.panTo(new BMap.Point(e.point.lng,e.point.lat));// map.panTo方法，把点击的点设置为地图中心点
            var now_point =  new BMap.Point(e.point.lng, e.point.lat );
            var location =e.point.lat+","+e.point.lng;
            marker.setPosition(now_point);//设置覆盖物位置
            $("#show_map").attr("data-location",location);
        });
        marker.addEventListener("dragend", function(){
            var o_Point_now =  marker.getPosition();
            var lng = o_Point_now.lng;//获取经度
            var lat = o_Point_now.lat;//获取纬度
            var location = lat+","+lng;
            map.panTo(new BMap.Point(lng,lat));// map.panTo方法，把点击的点设置为地图中心点
            $("#show_map").attr("data-location",location);
        })
    }else if(location_detail.indexOf(",") == -1){
        var myGeo = new BMap.Geocoder();
        // 将地址解析结果显示在地图上,并调整地图视野
        myGeo.getPoint(location_detail, function(point){
            if (point) {
                map.centerAndZoom(point, 15);
                var marker = new BMap.Marker(point); //创建marker对象
                map.addOverlay(marker);
                marker.enableDragging(); //marker可拖拽
                var label = new BMap.Label("该坐标将用于员工签到距离计算",{offset:new BMap.Size(20,-10)});
                label.setStyle({
                    "font-size":"10px",
                    "color":"#333",
                    "width":"171px",
                    "max-width":"200px",
                    "height":"20px"
                });
                marker.setLabel(label);
                var o_Point_now =  marker.getPosition();
                var lng = o_Point_now.lng;//获取经度
                var lat = o_Point_now.lat;//获取纬度
                var location = lat+","+lng;
                $("#show_map").attr("data-location",location);
                map.addEventListener("click",function(e){
                    console.log(e.point.lng+","+e.point.lat);// 单击地图获取坐标点；
                    map.panTo(new BMap.Point(e.point.lng,e.point.lat));// map.panTo方法，把点击的点设置为地图中心点
                    var now_point =  new BMap.Point(e.point.lng, e.point.lat );
                    var location =e.point.lat+","+e.point.lng;
                    marker.setPosition(now_point);//设置覆盖物位置
                    $("#show_map").attr("data-location",location);
                });
                marker.addEventListener("dragend", function(){
                    var o_Point_now =  marker.getPosition();
                    var lng = o_Point_now.lng;//获取经度
                    var lat = o_Point_now.lat;//获取纬度
                    var location = lat+","+lng;
                    map.panTo(new BMap.Point(lng,lat));// map.panTo方法，把点击的点设置为地图中心点
                    $("#show_map").attr("data-location",location);
                })
            }else{
                alert("您选择地址没有解析到结果!");
            }
        });
    }else {
        location_detail=location_detail.split(",");
        point = new BMap.Point(location_detail[1], location_detail[0]);
        map.centerAndZoom(point, 15);
        var marker = new BMap.Marker(point); //创建marker对象
        map.addOverlay(marker);
        marker.enableDragging(); //marker可拖拽
        var label = new BMap.Label("该坐标将用于员工签到距离计算",{offset:new BMap.Size(20,-10)});
        label.setStyle({
            "font-size":"10px",
            "color":"#333",
            "width":"171px",
            "max-width":"200px",
            "height":"20px"
        });
        marker.setLabel(label);
        map.addEventListener("click",function(e){
            console.log(e.point.lng+","+e.point.lat);// 单击地图获取坐标点；
            map.panTo(new BMap.Point(e.point.lng,e.point.lat));// map.panTo方法，把点击的点设置为地图中心点
            var now_point =  new BMap.Point(e.point.lng, e.point.lat );
            var location =e.point.lat+","+e.point.lng;
            marker.setPosition(now_point);//设置覆盖物位置
            $("#show_map").attr("data-location",location);
        });
        marker.addEventListener("dragend", function(){
            var o_Point_now =  marker.getPosition();
            var lng = o_Point_now.lng;//获取经度
            var lat = o_Point_now.lat;//获取纬度
            var location = lat+","+lng;
            map.panTo(new BMap.Point(lng,lat));// map.panTo方法，把点击的点设置为地图中心点
            $("#show_map").attr("data-location",location);
        })
    }
    var opts = {type: BMAP_NAVIGATION_CONTROL_ZOOM};
    map.addControl(new BMap.NavigationControl(opts));
    map.enableScrollWheelZoom();
    function G(id) {
        return document.getElementById(id);
    }
    var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
        {"input" : "suggestId"
            ,"location" : map
        });

    ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
        }
        str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
        }
        str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
        G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
        G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

        setPlace();
    });

    function setPlace(){
        map.clearOverlays();    //清除地图上所有覆盖物
        var pp;
        function myFun(){
                pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
            var location = marker.getPosition();
                location = location.lat+","+location.lng;
            $("#show_map").attr("data-location",location);
            map.centerAndZoom(pp, 18);
            map.addOverlay(new BMap.Marker(pp));    //添加标注
        }
        var local = new BMap.LocalSearch(map, { //智能搜索
            onSearchComplete: myFun
        });
        local.search(myValue);
        var marker = new BMap.Marker(point); //创建marker对象
        map.addOverlay(marker);
        marker.enableDragging(); //marker可拖拽
        var label = new BMap.Label("该坐标将用于员工签到距离计算",{offset:new BMap.Size(20,-10)});
        label.setStyle({
            "font-size":"10px",
            "color":"#333",
            "width":"171px",
            "max-width":"200px",
            "height":"20px"
        });
        marker.setLabel(label);
        map.addEventListener("click",function(e){
            console.log(e.point.lng+","+e.point.lat);// 单击地图获取坐标点；
            map.panTo(new BMap.Point(e.point.lng,e.point.lat));// map.panTo方法，把点击的点设置为地图中心点
            var now_point =  new BMap.Point(e.point.lng, e.point.lat );
            var location =e.point.lat+","+e.point.lng;
            marker.setPosition(now_point);//设置覆盖物位置
            $("#show_map").attr("data-location",location);
        });
        marker.addEventListener("dragend", function(){
            var o_Point_now =  marker.getPosition();
            var lng = o_Point_now.lng;//获取经度
            var lat = o_Point_now.lat;//获取纬度
            var location = lat+","+lng;
            map.panTo(new BMap.Point(lng,lat));// map.panTo方法，把点击的点设置为地图中心点
            $("#show_map").attr("data-location",location);
        })
    }
})
