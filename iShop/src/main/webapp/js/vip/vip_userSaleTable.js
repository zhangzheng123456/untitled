/**
 * Created by ghy on 2017/6/26.
 */
var oc = new ObjectControl();
var export_list_Array=[];
function arrAverageNum(arr){
    var sum = 0;
    for (var i = 0; i < arr.length; i++) {
        sum += arr[i];
    }
    return (sum/arr.length*100)/100;
}
var shop={
    filter_list:[
        {"screen_key":"store_code","screen_value":""},
        {"screen_key":"user_code","screen_value":""},
        {"screen_key":"start_time","screen_value":{"start":"","end":""}}
    ],//筛选的值
    filter_list_chart:[
        {"screen_key":"store_code","screen_value":""},
        {"screen_key":"user_code","screen_value":""}
    ],
    nav_type:"user",
    query_type:"",
    search_value:"",//搜索的值
    allData:0,
    data:{
        Num:1,//列表页码
        area_num:1,//区域页码
        staff_num:1,//区域页码
        shop_num:1,//店铺页码
        next_page:false,//下一页标志
        isscroll: false,//滚动标志
        brand_code:"",//存储品牌编号
        brand_code_chart:"",
        brand_names_chart:"",
        area_names:"",//存储区域名称
        area_code:"",//存储区域编号
        store_names:"",//存储店铺名称
        area_code_chart:"",
        area_names_chart:"",
        brand_names:"",//存储品牌名称
        store_code:"",//存储店铺编号
        store_code_chart:"",
        store_names_chart:"",
        storeNames:"",
        storeCodes:"",
        storeNames_chart:"",
        storeCodes_chart:"",
        staffName:"",//存储店铺单独筛选名称
        staffCode:"",//存储店铺群组单独筛选编号
        staffName_chart:"",
        staffCode_chart:"",
        popMark:"",//筛选弹窗区分店铺和群组标志
        chart_year:[],//图表中年数据
        year_choosed:"",//缓存选中年
        month_choosed:"",//缓存选中月份
        date_choosed:"",//缓存选中日期
        has_next_list:false,//列表下一页布尔值
        chartData:[],//图表数据
        chart_date:[],//图表横轴日期
        is_chart_select:false,
        staff_search_val:"",
        staff_search_val_chart:"",
        brand_search_val:"",
        brand_search_val_chart:"",
        store_search_val:"",
        store_search_val_chart:"",
        area_search_val:"",
        area_search_val_chart:""
    },
    init:function () {
        shop.allEvent();
        shop.getYear();
        shop.timeSwitchEvent();
        //shop.getSale();//获取业绩
        shop.getTabelList();//获取列表数据
        shop.getVipCardType();
        shop.getWx();
        shop.getAgeGroup();
    },
    allEvent:function () {
        //图表nav切换
        $("#chart_nav li").click(function () {
            if($(this).hasClass("nav-active"))return;
            $(this).addClass("nav-active");
            $(this).siblings("li").removeClass("nav-active");
            shop.getChartData();
        });
        //图表右侧下拉选项操作
        $("#chart_selectInput").click(function () {
            $("#chart_filter").toggle();
        });
        $("#chart_filter li").click(function () {
            $("#chart_selectInput").val($(this).text());
            $("#chart_filter").hide();
            shop.chartInit();
        });
        $("#chartTimeInput").click(function () {
            $("#time_choose_wrap").toggle();
        });
        //图表导出.关闭
        $("#chart_export_btn").click(function () {
            $("#chart_export_wrap").show();
            var time_type = $("#time_nav_switch .time_active").text()=="周"?"W":$("#time_nav_switch .time_active").text()=="月"?"M":"Y";
            var type = $("#chart_nav .nav-active").text() == "新老顾客消费趋势" ? "1" : $("#chart_nav .nav-active").text() == "多渠道新增会员" ? "2" : "3";
            var time_value = $("#chartTimeInput").attr("data-time");
            var param = {};
            param.time_type = time_type;
            param.time_value = time_value;
            param.view = "user";
            param.type = type;
            param.list = shop.filter_list_chart;
            oc.postRequire("post","/VipBusiness/storeViewExportExecl","",param,function (data) {
                if(data.code == 0){
                    var path = JSON.parse(data.message).path;
                    $("#chart_export_enter a").attr("href","/"+path);
                }
            });
        });
        $("#cancel_export,#close_export_box").click(function () {
            $("#chart_export_wrap").hide();
        });
        $(document).click(function (e) {
            if(!($(e.target).is("#chart_selectInput")||$(e.target).is("#chart_filter")||$(e.target).is("#chart_filter li"))){
                $("#chart_filter").hide();
            }
            if(!($(e.target).is(".timeClickArea li")||$(e.target).is(".timeClickArea"))){
                $("#time_choose_wrap").hide();
            }
            var divTop = $('.export_type');   // 设置目标区域

            if(!divTop.is(e.target) && divTop.has(e.target).length === 0){
                $("#export_type_second_list_other").hide();
                $(".export_type_first_list").hide();
            }

        });
        //店铺群组单独筛选操作
        $("#staffInput").click(function () {
            shop.data.is_chart_select=false;
            $("#staff_search").val(shop.data.staff_search_val);
            $("#brand_search").val(shop.data.brand_search_val);
            $("#area_search").val(shop.data.area_search_val);
            $("#store_search").val(shop.data.store_search_val);
            if (shop.data.staffCode !== "") {
                var area_codes = shop.data.staffCode.split(',');
                var area_names = shop.data.staffName.split(',');
                var area_html_right = "";
                for (var h = 0; h < area_codes.length; h++) {
                    area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            </li>"
                }
                $("#screen_staff .s_pitch span").eq(0).html(h);
                $("#screen_staff .screen_content_r ul").html(area_html_right);
            } else {
                $("#screen_staff .s_pitch span").eq(0).html("0");
                $("#screen_staff .screen_content_r ul").empty();
            }
            //shop.data.popMark = "area";
            if(shop.data.brand_code=="" || shop.data.brand_code==0){
                $("#staff_brand_num").val("全部")
            }else{
                var len=shop.data.brand_code.split(",").length;
                $("#staff_brand_num").val("已选"+len+"个")
            }
            if(shop.data.store_code=="" || shop.data.store_code==0){
                $("#staff_area_num").val("全部")
            }else{
                var len=shop.data.store_code.split(",").length;
                $("#staff_area_num").val("已选"+len+"个")
            }
            if(shop.data.area_code=="" || shop.data.area_code==0){
                $("#staff_shop_num").val("全部")
            }else{
                var len=shop.data.area_code.split(",").length;
                $("#staff_shop_num").val("已选"+len+"个")
            }
            shop.data.staff_num = 1;
            shop.data.isscroll = false;
            shop.data.popMark="staff";
            $("#pop-up-wrap").show();
            $("#screen_staff").show();
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            shop.getstafflist(shop.data.staff_num);
        });
        //店铺群组单独筛选操作
        $("#staffInput_1").click(function () {
            shop.data.is_chart_select=true;
            $("#staff_search").val(shop.data.staff_search_val_chart);
            $("#brand_search").val(shop.data.brand_search_val_chart);
            $("#area_search").val(shop.data.area_search_val_chart);
            $("#store_search").val(shop.data.store_search_val_chart);
            if (shop.data.staffCode_chart !== "") {
                var area_codes = shop.data.staffCode_chart.split(',');
                var area_names = shop.data.staffName_chart.split(',');
                var area_html_right = "";
                for (var h = 0; h < area_codes.length; h++) {
                    area_html_right += "<li id='" + area_codes[h] + "'>\
                    <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
                    <label></div><span class='p16'>" + area_names[h] + "</span>\
                    </li>"
                }
                $("#screen_staff .s_pitch span").eq(0).html(h);
                $("#screen_staff .screen_content_r ul").html(area_html_right);
            } else {
                $("#screen_staff .s_pitch span").eq(0).html("0");
                $("#screen_staff .screen_content_r ul").empty();
            }
            //shop.data.popMark = "area";
            if(shop.data.brand_code_chart=="" || shop.data.brand_code_chart==0){
                $("#staff_brand_num").val("全部")
            }else{
                var len=shop.data.brand_code_chart.split(",").length;
                $("#staff_brand_num").val("已选"+len+"个")
            }
            if(shop.data.area_code_chart=="" || shop.data.area_code_chart==0){
                $("#staff_area_num").val("全部")
            }else{
                var len=shop.data.area_code_chart.split(",").length;
                $("#staff_area_num").val("已选"+len+"个")
            }
            if(shop.data.store_code_chart=="" || shop.data.store_code_chart==0){
                $("#staff_shop_num").val("全部")
            }else{
                var len=shop.data.store_code_chart.split(",").length;
                $("#staff_shop_num").val("已选"+len+"个")
            }
            shop.data.staff_num = 1;
            shop.data.isscroll = false;
            shop.data.popMark="staff";
            $("#pop-up-wrap").show();
            $("#screen_staff").show();
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            shop.getstafflist(shop.data.staff_num);
        });
        //店铺,品牌，区域筛选弹窗
        $("#storeInput").click(function () {
            shop.data.is_chart_select=false;
            $("#brand_search").val(shop.data.brand_search_val);
            $("#area_search").val(shop.data.area_search_val);
            $("#store_search").val(shop.data.store_search_val);
            if (shop.data.storeCodes !== "") {
                var store_codes = shop.data.storeCodes.split(',');
                var store_names = shop.data.storeNames.split(',');
                var shop_html_right = "";
                for (var h = 0; h < store_codes.length; h++) {
                    shop_html_right += "<li id='" + store_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_codes[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").eq(0).html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            } else {
                $("#screen_shop .s_pitch span").eq(0).html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            if(shop.data.area_code=="" || shop.data.area_code==0){
                $("#area_num").val("全部")
            }
            shop.data.shop_num = 1;
            shop.data.isscroll = false;
            shop.data.popMark="shop";
            $("#screen_shop").show();
            $("#pop-up-wrap").show();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            shop.getstorelist(shop.data.shop_num);
        });
        //店铺,品牌，区域筛选弹窗
        $("#storeInput_1").click(function () {
            shop.data.is_chart_select=true;
            $("#brand_search").val(shop.data.brand_search_val_chart);
            $("#area_search").val(shop.data.area_search_val_chart);
            $("#store_search").val(shop.data.store_search_val_chart);
            if (shop.data.storeCodes_chart !== "") {
                var store_codes = shop.data.storeCodes_chart.split(',');
                var store_names = shop.data.storeNames_chart.split(',');
                var shop_html_right = "";
                for (var h = 0; h < store_codes.length; h++) {
                    shop_html_right += "<li id='" + store_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_codes[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").eq(0).html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            } else {
                $("#screen_shop .s_pitch span").eq(0).html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            if(shop.data.area_code_chart=="" || shop.data.area_code_chart==0){
                $("#area_num").val("全部")
            }
            shop.data.shop_num = 1;
            shop.data.isscroll = false;
            shop.data.popMark="shop";
            $("#screen_shop").show();
            $("#pop-up-wrap").show();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            shop.getstorelist(shop.data.shop_num);
        });
        $("#brand_num,#shop_brand").click(function () {
            if (shop.data.brand_code !== "") {
                var brand_codes = shop.data.brand_code.split(',');
                var brand_names = shop.data.brand_names.split(',');
                var brand_html_right = "";
                for (var h = 0; h < brand_codes.length; h++) {
                    brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").eq(0).html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            } else {
                $("#screen_brand .s_pitch span").eq(0).html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#screen_brand").show();
            $("#screen_shop").hide();
            $("#screen_brand .screen_content_l ul").empty();
            shop.getbrandlist();
        });
        $("#area_num,#shop_area").click(function () {
            if(shop.data.is_chart_select){
                if (shop.data.area_code_chart !== "") {
                    var area_codes = shop.data.area_code_chart.split(',');
                    var area_names = shop.data.area_names_chart.split(',');
                    var area_html_right = "";
                    for (var h = 0; h < area_codes.length; h++) {
                        area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_area .s_pitch span").eq(0).html(h);
                    $("#screen_area .screen_content_r ul").html(area_html_right);
                } else {
                    $("#screen_area .s_pitch span").eq(0).html("0");
                    $("#screen_area .screen_content_r ul").empty();
                }
            }else{
                if (shop.data.area_code !== "") {
                    var area_codes = shop.data.area_code.split(',');
                    var area_names = shop.data.area_names.split(',');
                    var area_html_right = "";
                    for (var h = 0; h < area_codes.length; h++) {
                        area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_area .s_pitch span").eq(0).html(h);
                    $("#screen_area .screen_content_r ul").html(area_html_right);
                } else {
                    $("#screen_area .s_pitch span").eq(0).html("0");
                    $("#screen_area .screen_content_r ul").empty();
                }
            }
            shop.data.area_num = 1;
            shop.data.isscroll = false;
            $("#screen_area").show();
            $("#screen_shop").hide();
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            shop.getarealist(shop.data.area_num);
        });
        $("#staff_area_num,#staff_area").click(function () {
            if(shop.data.is_chart_select){
                if (shop.data.area_code_chart !== "") {
                    var area_codes = shop.data.area_code_chart.split(',');
                    var area_names = shop.data.area_names_chart.split(',');
                    var area_html_right = "";
                    for (var h = 0; h < area_codes.length; h++) {
                        area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_area .s_pitch span").eq(0).html(h);
                    $("#screen_area .screen_content_r ul").html(area_html_right);
                } else {
                    $("#screen_area .s_pitch span").eq(0).html("0");
                    $("#screen_area .screen_content_r ul").empty();
                }
            }else{
                if (shop.data.area_code !== "") {
                    var area_codes = shop.data.area_code.split(',');
                    var area_names = shop.data.area_names.split(',');
                    var area_html_right = "";
                    for (var h = 0; h < area_codes.length; h++) {
                        area_html_right += "<li id='" + area_codes[h] + "'>\
                        <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
                        <label></div><span class='p16'>" + area_names[h] + "</span>\
                        </li>"
                    }
                    $("#screen_area .s_pitch span").eq(0).html(h);
                    $("#screen_area .screen_content_r ul").html(area_html_right);
                } else {
                    $("#screen_area .s_pitch span").eq(0).html("0");
                    $("#screen_area .screen_content_r ul").empty();
                }
            }
            shop.data.area_num = 1;
            shop.data.isscroll = false;
            $("#screen_area").show();
            $("#screen_staff").hide();
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            shop.getarealist(shop.data.area_num);
        });
        $("#staff_shop_num,#staff_shop").click(function () {
            if(shop.data.is_chart_select){
                if (shop.data.store_code_chart !== "") {
                    var store_code = shop.data.store_code_chart.split(',');
                    var store_names = shop.data.store_names_chart.split(',');
                    var shop_html_right = "";
                    for (var h = 0; h < store_code.length; h++) {
                        shop_html_right += "<li id='" + store_code[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_code[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_shop .s_pitch span").eq(0).html(h);
                    $("#screen_shop .screen_content_r ul").html(shop_html_right);
                } else {
                    $("#screen_shop .s_pitch span").eq(0).html("0");
                    $("#screen_shop .screen_content_r ul").empty();
                }
            }else{
                if (shop.data.store_code !== "") {
                    var store_code = shop.data.store_code.split(',');
                    var store_names = shop.data.store_names.split(',');
                    var shop_html_right = "";
                    for (var h = 0; h < store_code.length; h++) {
                        shop_html_right += "<li id='" + store_code[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_code[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_shop .s_pitch span").eq(0).html(h);
                    $("#screen_shop .screen_content_r ul").html(shop_html_right);
                } else {
                    $("#screen_shop .s_pitch span").eq(0).html("0");
                    $("#screen_shop .screen_content_r ul").empty();
                }
            }
            shop.data.shop_num = 1;
            shop.data.isscroll = false;
            $("#screen_shop").show();
            $("#screen_staff").hide();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            shop.getstorelist(shop.data.shop_num);
        });
        $("#staff_brand_num,#staff_brand").click(function () {
            if(shop.data.is_chart_select){
                if (shop.data.brand_code_chart !== "") {
                    var brand_codes = shop.data.brand_code_chart.split(',');
                    var brand_names = shop.data.brand_names_chart.split(',');
                    var brand_html_right = "";
                    for (var h = 0; h < brand_codes.length; h++) {
                        brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_brand .s_pitch span").eq(0).html(h);
                    $("#screen_brand .screen_content_r ul").html(brand_html_right);
                } else {
                    $("#screen_brand .s_pitch span").eq(0).html("0");
                    $("#screen_brand .screen_content_r ul").empty();
                }
            }else{
                if (shop.data.brand_code !== "") {
                    var brand_codes = shop.data.brand_code.split(',');
                    var brand_names = shop.data.brand_names.split(',');
                    var brand_html_right = "";
                    for (var h = 0; h < brand_codes.length; h++) {
                        brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            </li>"
                    }
                    $("#screen_brand .s_pitch span").eq(0).html(h);
                    $("#screen_brand .screen_content_r ul").html(brand_html_right);
                } else {
                    $("#screen_brand .s_pitch span").eq(0).html("0");
                    $("#screen_brand .screen_content_r ul").empty();
                }
                //shop.data.brand_num = 1;
                //shop.data.isscroll = false;
            }
            $("#screen_brand").show();
            $("#screen_staff").hide();
            $("#screen_brand .screen_content_l ul").empty();
            shop.getbrandlist()
        });

        //店铺,品牌，区域点击确定
        $("#screen_que_shop").click(function () {
            var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            var store_codes = "";
            var store_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    store_codes += r + ",";
                    store_names += p + ",";
                } else {
                    store_codes += r;
                    store_names += p;
                }
            }
            if(shop.data.popMark=="shop"){
                if(shop.data.is_chart_select){
                    shop.data.storeCodes_chart = store_codes;
                    shop.data.storeNames_chart = store_names;
                    $("#storeInput_1").val(li.length!=0?"已选" + li.length + "个":"");
                }else{
                    shop.data.storeCodes = store_codes;
                    shop.data.storeNames = store_names;
                    $("#storeInput").val(li.length!=0?"已选" + li.length + "个":"");
                }
                $("#screen_shop").hide();
                $("#pop-up-wrap").hide();
            }else if(shop.data.popMark=="staff"){
                shop.data.store_code = store_codes;
                shop.data.store_names = store_names;
                shop.data.staff_num = 1;
                shop.data.isscroll = false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                shop.getstafflist(shop.data.staff_num);
                li.length == 0?$("#staff_shop_num").val("全部"): $("#staff_shop_num").val("已选" + li.length + "个");
                $("#screen_shop").hide();
                $("#screen_staff").show();
            }
        });
        $("#screen_que_brand").click(function () {
            var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            var brand_codes = "";
            var brand_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    brand_codes += r + ",";
                    brand_names += p + ",";
                } else {
                    brand_codes += r;
                    brand_names += p;
                }
            }
            if(shop.data.is_chart_select){
                shop.data.brand_code_chart = brand_codes;
                shop.data.brand_names_chart = brand_names;
            }else{
                shop.data.brand_code = brand_codes;
                shop.data.brand_names = brand_names;
            }
            $("#screen_brand").hide();
            if(shop.data.popMark=="staff"){
                shop.data.staff_num = 1;
                shop.data.isscroll = false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                shop.getstafflist(shop.data.staff_num);
                //li.length == 0?$("#staff_brand_num").val("全部"): $("#staff_brand_num").val("已选" + li.length + "个");
                $("#screen_brand").hide();
                $("#screen_staff").show();
            }else{
                shop.data.shop_num = 1;
                shop.data.isscroll = false;
                $("#screen_shop .screen_content_l").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                shop.getstorelist(shop.data.shop_num);
                //li.length == 0?$("#brand_num").val("全部"): $("#brand_num").val("已选" + li.length + "个");
                $("#screen_brand").hide();
                $("#screen_shop").show();
            }
            li.length == 0?$("#staff_brand_num").val("全部"): $("#staff_brand_num").val("已选" + li.length + "个");
            li.length == 0?$("#brand_num").val("全部"): $("#brand_num").val("已选" + li.length + "个");
        });
        $("#screen_que_area").click(function () {
            var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            var area_codes = "";
            var area_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    area_codes += r + ",";
                    area_names += p + ",";
                } else {
                    area_codes += r;
                    area_names += p;
                }
            }
            if(shop.data.is_chart_select){
                shop.data.area_code_chart = area_codes;
                shop.data.area_names_chart = area_names;
            }else{
                shop.data.area_code = area_codes;
                shop.data.area_names = area_names;
            }
            if(shop.data.popMark == "shop"){
                shop.data.shop_num = 1;
                shop.data.isscroll = false;
                $("#screen_shop .screen_content_l").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                shop.getstorelist(shop.data.shop_num);
                //li.length == 0?$("#area_num").val("全部"): $("#area_num").val("已选" + li.length + "个");
                $("#screen_area").hide();
                $("#screen_shop").show();
            }else if(shop.data.popMark == "staff"){
                shop.data.staff_num = 1;
                shop.data.isscroll = false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                shop.getstafflist(shop.data.staff_num);
                //li.length == 0?$("#staff_area_num").val("全部"): $("#staff_area_num").val("已选" + li.length + "个");
                $("#screen_area").hide();
                $("#screen_staff").show();
            }
            li.length == 0?$("#area_num").val("全部"): $("#area_num").val("已选" + li.length + "个");
            li.length == 0?$("#staff_area_num").val("全部"): $("#staff_area_num").val("已选" + li.length + "个");
        });
        $("#screen_que_staff").click(function () {
            var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            var staff_codes = "";
            var staff_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    staff_codes += r + ",";
                    staff_names += p + ",";
                } else {
                    staff_codes += r;
                    staff_names += p;
                }
            }
            if(shop.data.is_chart_select){
                shop.data.staffCode_chart = staff_codes;
                shop.data.staffName_chart = staff_names;
                $("#staffInput_1").val(li.length!=0?"已选" + li.length + "个":"");
                $("#screen_staff").hide();
                $("#pop-up-wrap").hide();
            }else{
                shop.data.staffCode = staff_codes;
                shop.data.staffName = staff_names;
                $("#staffInput").val(li.length!=0?"已选" + li.length + "个":"");
                $("#screen_staff").hide();
                $("#pop-up-wrap").hide();
            }
        });
        //店铺,品牌，区域搜索
        $("#store_search").keydown(function () {
            var event = event || window.event || arguments[0];
            shop.data.shop_num = 1;
            if (event.keyCode == 13) {
                shop.data.isscroll = false;
                if(shop.data.is_chart_select){
                    shop.data.store_search_val_chart=$('#store_search').val().trim();
                }else{
                    shop.data.store_search_val=$('#store_search').val().trim();
                }
                $("#screen_shop .screen_content_l ul").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                shop.getstorelist(shop.data.shop_num);
            }
        });
        $("#store_search_f").click(function () {
            shop.data.shop_num = 1;
            shop.data.isscroll = false;

            if(shop.data.is_chart_select){
                shop.data.store_search_val_chart=$('#store_search').val().trim();
            }else{
                shop.data.store_search_val=$('#store_search').val().trim();
            }
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            shop.getstorelist(shop.data.shop_num);
        });
        $("#staff_search").keydown(function () {
            var event = event || window.event || arguments[0];
            shop.data.staff_num = 1;
            if (event.keyCode == 13) {
                shop.data.isscroll = false;
                if(shop.data.is_chart_select){
                    shop.data.staff_search_val_chart=$('#staff_search').val().trim();
                }else{
                    shop.data.staff_search_val=$('#staff_search').val().trim();
                }
                $("#screen_staff .screen_content_l ul").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                shop.getstafflist(shop.data.staff_num);
            }
        });
        $("#staff_search_f").click(function () {
            shop.data.staff_num = 1;
            shop.data.isscroll = false;
            if(shop.data.is_chart_select){
                shop.data.staff_search_val_chart=$('#staff_search').val().trim();
            }else{
                shop.data.staff_search_val=$('#staff_search').val().trim();
            }
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            shop.getstafflist(shop.data.staff_num);
        });
        $("#brand_search").keydown(function () {
            var event = event || window.event || arguments[0];
            if (event.keyCode == 13) {
                if(shop.data.is_chart_select){
                    shop.data.brand_search_val_chart=$('#brand_search').val().trim();
                }else{
                    shop.data.brand_search_val=$('#brand_search').val().trim();
                }
                $("#screen_brand .screen_content_l ul").empty();
                shop.getbrandlist();
            }
        });
        $("#brand_search_f").click(function () {
            $("#screen_brand .screen_content_l ul").empty();
            if(shop.data.is_chart_select){
                shop.data.brand_search_val_chart=$('#brand_search').val().trim();
            }else{
                shop.data.brand_search_val=$('#brand_search').val().trim();
            }
            shop.getbrandlist();
        });
        $("#area_search").keydown(function () {
            var event = event || window.event || arguments[0];
            shop.data.area_num = 1;
            if (event.keyCode == 13) {
                shop.data.isscroll = false;
                if(shop.data.is_chart_select){
                    shop.data.area_search_val_chart=$('#area_search').val().trim();
                }else{
                    shop.data.area_search_val=$('#area_search').val().trim();
                }
                $("#screen_area .screen_content_l").unbind("scroll");
                $("#screen_area .screen_content_l ul").empty();
                shop.getarealist(shop.data.area_num);
            }
        });
        $("#area_search_f").click(function () {
            shop.data.area_num = 1;
            shop.data.isscroll = false;
            if(shop.data.is_chart_select){
                shop.data.area_search_val_chart=$('#area_search').val().trim();
            }else{
                shop.data.area_search_val=$('#area_search').val().trim();
            }
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            shop.getarealist(shop.data.area_num);
        });
        //关闭品牌，区域，店铺弹窗
        $("#screen_close_brand").click(function () {
            if(shop.data.popMark == "shop"){
                $("#screen_brand").hide();
                $("#screen_shop").show();
            }else if(shop.data.popMark == "staff"){
                $("#screen_brand").hide();
                $("#screen_staff").show();
            }
        });
        $("#screen_close_area").click(function () {
            if(shop.data.popMark == "shop"){
                $("#screen_area").hide();
                $("#screen_shop").show();
            }else if(shop.data.popMark == "staff"){
                $("#screen_area").hide();
                $("#screen_staff").show();
            }
        });
        $("#screen_close_staff").click(function () {
            $("#pop-up-wrap").hide();
            $("#screen_staff").hide();
        });
        $("#screen_close_shop").click(function () {
            if(shop.data.popMark == "shop"){
                $("#pop-up-wrap").hide();
                $("#screen_shop").hide();
            }else if(shop.data.popMark == "staff"){
                $("#screen_shop").hide();
                $("#screen_staff").show();
            }
        });

        //点击右移
        $(".shift_right").click(function () {
            var right = "only";
            var div = $(this);
            shop.removeRight(right, div);
        });
        //点击右移全部
        $(".shift_right_all").click(function () {
            var right = "all";
            var div = $(this);
            shop.removeRight(right, div);
        });
        //点击左移
        $(".shift_left").click(function () {
            var left = "only";
            var div = $(this);
            shop.removeLeft(left, div);
        });
        //点击左移全部
        $(".shift_left_all").click(function () {
            var left = "all";
            var div = $(this);
            shop.removeLeft(left, div);
        });
        //点击列表显示选中状态
        $(".screen_content").on("click", "li", function () {
            var input = $(this).find("input")[0];
            if (input.type == "checkbox" && input.checked == false ) {
                input.checked = true;
            } else if (input.type == "checkbox" && input.checked == true) {
                input.checked = false;
            }
        });
        //列表滚动分页
        $(".table_tbodyWrap").unbind("scroll").bind("scroll",function(){
            var offsetHeight = $(this).height(),
                scrollHeight = $(this)[0].scrollHeight,
                scrollTop = $(this)[0].scrollTop;
            if(scrollTop + offsetHeight >= scrollHeight && scrollTop != 0){
                if(shop.data.has_next_list){
                    shop.data.Num++;
                }else{
                    return
                }
                shop.getTabelList();
            }
        });

        //列表nav切换
        $("#table_nav li").click(function (e) {
            $(this).siblings("li").find("dl").hide();
            if($(e.target).is(".nav_select")||$(e.target).is(".nav_select i")||$(e.target).is(".nav_select label")){
                $(this).find("dl").slideToggle(200,function () {
                    $(".scroll_area").getNiceScroll().resize();
                });
            }else if($(e.target).is(".nav_select dd")){
                shop.query_type = $(e.target).attr("data-type");
                $("#table_title span").eq(0).text($(this).find('label').text());
                $("#table_title i").show();
                $("#table_title span").eq(1).text($(e.target).text());
                $(this).parent().find(".nav-active").removeClass("nav-active");
                $(e.target).addClass("nav-active");
                shop.nav_type = $(this).attr("data-nav");
                shop.data.Num = 1;
                shop.getTabelList();
            }else {
                if(shop.nav_type == $(this).attr("data-nav"))return;
                $("#table_title span").eq(0).text($(this).text());
                $("#table_title i").hide();
                $("#table_title span").eq(1).text("");
                $(this).parent().find(".nav-active").removeClass("nav-active");
                $(this).addClass("nav-active");
                shop. query_type = "";
                shop.nav_type = $(this).attr("data-nav");
                shop.data.Num = 1;
                shop.getTabelList();
            }
        });
        //列表搜索，筛选，清空
        $("#search_icon").click(function () {
            shop.filter_list = [
                {"screen_key":"store_code","screen_value":""},
                {"screen_key":"user_code","screen_value":""},
                {"screen_key":"start_time","screen_value":{"start":"","end":""}}
            ];
            shop.search_value = $(this).prev("input").val().trim();
            shop.data.Num = 1;
            shop.getTabelList();
        });
        $("#table_search_input").keydown(function (e) {
            var event = e || window.event;
            if(event.keyCode == 13){
                shop.filter_list = [
                    {"screen_key":"store_code","screen_value":""},
                    {"screen_key":"user_code","screen_value":""},
                    {"screen_key":"start_time","screen_value":{"start":"","end":""}}
                ];
                shop.search_value = $(this).val();
                shop.data.Num = 1;
                shop.data.areaName = "";
                shop.data.areaCode = "";
                shop.data.area_names = "";
                shop.data.area_code = "";
                shop.data.store_code = "";
                shop.data.staffCode = "";
                shop.data.staffName = "";
                shop.data.store_names = "";
                shop.data.brand_code = "";
                shop.data.brand_names = "";
                shop.data.storeCodes="";
                shop.data.storeNames="";
                start.max = '2099-06-16 23:59:59';
                end.min = '1919-01-01 23:59:59';
                $("#staff_shop_num").val("全部");
                $("#staff_brand_num").val("全部");
                $("#staff_area_num").val("全部");
                $("#brand_num").val("全部");
                $("#area_num").val("全部");
                $("#filter_inputWrap input").val('');
                shop.getTabelList();
            }
        });
        $("#filterBtn").click(function () {
            $("#table_search_input").val("");
            shop.search_value = "";
            shop.filter_list = [
                {"screen_key":"store_code","screen_value":shop.data.storeCodes},
                {"screen_key":"user_code","screen_value":shop.data.staffCode},
                {"screen_key":"start_time","screen_value":{"start":$("#time_start").val(),"end":$("#time_end").val()}}
            ];
            shop.data.Num = 1;
            shop.getTabelList();
        });
        $("#filterBtn_1").click(function () {
            shop.filter_list_chart = [
                {"screen_key":"store_code","screen_value":shop.data.storeCodes_chart},
                {"screen_key":"user_code","screen_value":shop.data.staffCode_chart}
            ];
            shop.getChartData();
            shop.getSale();
        });
        $("#emptyFilterBtn").click(function () {//清空筛选
            $("#filter_inputWrap input").val('');
            shop.filter_list = [
                {"screen_key":"store_code","screen_value":""},
                {"screen_key":"user_code","screen_value":""},
                {"screen_key":"start_time","screen_value":{"start":"","end":""}}
            ];
            shop.data.areaName = "";
            shop.data.areaCode = "";
            shop.data.area_names = "";
            shop.data.area_code = "";
            shop.data.store_code = "";
            shop.data.staffCode = "";
            shop.data.staffName = "";
            shop.data.store_names = "";
            shop.data.brand_code = "";
            shop.data.brand_names = "";
            shop.data.storeCodes="";
            shop.data.storeNames="";
            start.max = '2099-06-16 23:59:59';
            end.min = '1919-01-01 23:59:59';
            $("#staff_shop_num").val("全部");
            $("#staff_brand_num").val("全部");
            $("#staff_area_num").val("全部");
            $("#brand_num").val("全部");
            $("#area_num").val("全部");
            shop.data.Num = 1;
            shop.getTabelList();
        });
        $("#emptyFilterBtn_1").click(function () {//清空筛选
            $("#filter_inputWrap_1 input:not('#chartTimeInput')").val('');
            shop.filter_list_chart = [
                {"screen_key":"store_code","screen_value":""},
                {"screen_key":"user_code","screen_value":""}
            ];
            shop.data.areaName = "";
            shop.data.areaCode = "";
            shop.data.area_names_chart = "";
            shop.data.area_code_chart = "";
            shop.data.store_code = "";
            shop.data.staffCode_chart = "";
            shop.data.staffName_chart = "";
            shop.data.store_names = "";
            shop.data.brand_code_chart = "";
            shop.data.brand_names_chart = "";
            shop.data.storeCodes_chart="";
            shop.data.storeNames_chart="";
            //start.max = '2099-06-16 23:59:59';
            //end.min = '1919-01-01 23:59:59';
            $("#staff_shop_num").val("全部");
            $("#staff_brand_num").val("全部");
            $("#staff_area_num").val("全部");
            $("#brand_num").val("全部");
            $("#area_num").val("全部");
            //shop.data.Num = 1;
            $("#time_nav_switch li:first-child").trigger("click")
            shop.getYear();
            //shop.getChartData();
            //shop.getSale();
        });
    },
    getChartData:function () {
        var time_type = $("#time_nav_switch .time_active").text()=="周"?"W":$("#time_nav_switch .time_active").text()=="月"?"M":"Y";
        var type = $("#chart_nav .nav-active").text() == "新老顾客消费趋势" ? "1" : $("#chart_nav .nav-active").text() == "多渠道新增会员" ? "2" : "3";
        var time_value = $("#chartTimeInput").attr("data-time");
        var param = {};
        param.time_type = time_type;
        param.time_value = time_value;
        param.type = type;
        param.view="user";
        param.list=shop.filter_list_chart;
        $("#chart_loading").show();
        oc.postRequire("post","/VipBusiness/storeKpiView","",param,function (data) {
            if(data.code == 0){
                $("#chart_selectInput").val("笔数");
                var msg = JSON.parse(data.message);
                var list = msg.view;
                shop.data.chartData = list;
                shop.chartInit();
                $("#chart_loading").hide();
            }
        });
    },
    chartInit:function () {
        var countmax= 0;
        var shop_data = [];
        var schema = [
            {text: '折扣'},
            {text: '金额'},
            {text: '会员数'}
        ];
        var itemStyle = {
            normal: {
                opacity: 0.8
                // shadowBlur: 10,
                // shadowOffsetX: 0,
                // shadowOffsetY: 0,
                // shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        };
        var type = $("#chart_nav .nav-active").text() == "新老顾客消费趋势" ? "1" : $("#chart_nav .nav-active").text() == "多渠道新增会员" ? "2" : "3";
        var arr_l = [],arr_m = [],arr_r = [],date = [],name_l,name_m,name_r;
        var myChart = echarts.init(document.getElementById('chartContentWrap'));
        shop.data.chartData.map(function (arr) {
            date.push(arr.date);
            if(type == 1){
                $("#chart_selectInput").show();
                name_l = "老会员";
                name_m = "新会员";
                name_r = "非会员";
                if($("#chart_selectInput").val() == "笔数"){
                    arr_l.push(arr.ovip_num_trade);
                    arr_m.push(arr.nvip_num_trade);
                    arr_r.push(arr.novip_num_trade);
                }else if($("#chart_selectInput").val() == "金额"){
                    arr_l.push(arr.ovip_amt_trade);
                    arr_m.push(arr.nvip_amt_trade);
                    arr_r.push(arr.novip_amt_trade);
                }else {
                    arr_l.push(arr.ovip_num_sales);
                    arr_m.push(arr.nvip_num_sales);
                    arr_r.push(arr.novip_num_sales);
                }
            }else if(type == 2){
                $("#chart_selectInput").hide();
                name_l = "新增微会员";
                name_m = "绑卡微会员";
                name_r = "粉丝关注";
                arr_l.push(arr.wechat_vip);
                arr_m.push(arr.wechat_vip_bind);
                arr_r.push(arr.new_fans);
            }else {
                $("#chart_selectInput").hide();
                countmax = Math.max(countmax,arr.vip_count);
                var newArr = [arr.discount,arr.amt_trade,arr.vip_count,arr.user_code,arr.active_rate];
                shop_data.push(newArr);
            }
        });
        option = {
            color:['#41c7db','#97c94b','#d8d8e1'],
            tooltip : {
                trigger: 'axis'
            },
            grid:{
                top:'30px',
                left:'5%',
                right:'3%'
            },
            legend: {
                bottom:0,
                itemWidth:10,
                itemGap:100,
                data:[
                    {name:name_l,icon:'circle'},
                    {name:name_m,icon:'circle'},
                    {name:name_r,icon:'circle'}
                ]
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    axisLine:{
                        lineStyle:{
                            color:'#dfdfdf'
                        }
                    },
                    axisLabel:{
                        textStyle:{
                            color:"#888"
                        }
                    },
                    axisTick:{
                        show:false
                    },
                    data : date
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    axisLine:{
                        show:false
                    },
                    axisLabel:{
                        textStyle:{
                            color:"#888"
                        }
                    },
                    axisTick:{
                        show:false
                    },
                    splitLine:{
                        lineStyle:{
                            color:"#eee"
                        }
                    }
                }
            ],
            series : [
                {
                    name:name_l,
                    type:'line',
                    stack: '总量',
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data:arr_l
                    //markLine: {
                    //    data: [
                    //        {yAxis:arrAverageNum(arr_l), name: '平均值'}
                    //    ],
                    //    lineStyle:{
                    //        normal:{
                    //            color:"#666"
                    //        },
                    //        emphasis:{
                    //            color:"#666"
                    //        }
                    //    }
                    //}
                },
                {
                    name:name_m,
                    type:'line',
                    stack: '总量',
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data:arr_m
                    //markLine: {
                    //    data: [
                    //        {yAxis:arrAverageNum(arr_m), name: '平均值'}
                    //    ],
                    //    lineStyle:{
                    //        normal:{
                    //            color:"#666"
                    //        },
                    //        emphasis:{
                    //            color:"#666"
                    //        }
                    //    }
                    //}
                },
                {
                    name:name_r,
                    type:'line',
                    stack: '总量',
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data:arr_r
                    //markLine: {
                    //    data: [
                    //        {yAxis:arrAverageNum(arr_r), name: '平均值'}
                    //    ],
                    //    lineStyle:{
                    //        normal:{
                    //            color:"#666"
                    //        },
                    //        emphasis:{
                    //            color:"#666"
                    //        }
                    //    }
                    //}
                }
            ]
        };//折线图
        options = {
            color:['#c6eef4','#e0efc9'],
            //legend: {
            //    y: 'top',
            //    data: ['店铺'],
            //    textStyle: {
            //        fontSize: 14
            //    }
            //},
            grid:{
                top:'30px',
                left:'5%',
                right:'15%'
            },
            tooltip: {
                padding: 10,
                backgroundColor: '#222',
                borderColor: '#777',
                borderWidth: 1,
                formatter: function (obj) {
                    var value = obj.value;
                    if(value.length>1){
                        return '<div style="border-bottom: 1px solid rgba(255,255,255,.3);font-size: 14px;padding-bottom: 7px;margin-bottom: 7px">'
                            + "导购编号:"+value[3]
                            + '</div>'
                            + schema[0].text + '：' + value[0] + '<br>'
                            + schema[1].text + '：' + value[1] + '<br>'
                            + schema[2].text + '：' + value[2] + '<br>'
                            + '活跃度' + '：' + value[4] + '<br>'
                    }else{
                        if(obj.data["valueIndex"]!=0){
                            return obj.seriesName + ' :<br/>'
                                + obj.name + ' : '
                                + obj.value ;
                        }else{
                            return obj.seriesName + ' :<br/>'
                                + obj.name + ' : '
                                + obj.value;
                        }
                    }
                }
            },
            xAxis: {
                type: 'value',
                name: '折扣',
                nameGap: 16,
                nameTextStyle: {
                    fontSize: 14
                },
                axisLine:{
                    lineStyle:{
                        color:'#dfdfdf'
                    }
                },
                axisLabel:{
                    textStyle:{
                        color:"#888"
                    }
                },
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                name: '金额',
                nameLocation: 'end',
                // nameTextStyle: {
                //     fontSize: 16
                // },
                axisTick:{
                    show:false
                },
                axisLine:{
                    lineStyle:{
                        color:'#dfdfdf'
                    }
                },
                axisLabel:{
                    textStyle:{
                        color:"#888"
                    }
                },
                splitLine: {
                    show: false
                }
            },
            visualMap: [
                {
                    left: 'right',
                    top: 0,
                    dimension: 2,
                    min: 0,
                    max: countmax,
                    itemWidth: 10,
                    itemHeight: 60,
                    calculable: true,
                    precision: 0.1,
                    text: ['圆形大小：会员数'],
                    textGap: 30,
                    inRange: {
                        symbolSize: [10, 70]
                    },
                    outOfRange: {
                        symbolSize: [10, 70],
                        color: ['rgba(255,255,255,.6)']
                    },
                    controller: {
                        inRange: {
                            color: ['#67d2e2']
                        },
                        outOfRange: {
                            color: ['#fff']
                        }
                    }
                },
                {
                    right:'1%',
                    bottom: '2%',
                    dimension: 4,
                    min: 0,
                    max:1,
                    itemWidth: 10,
                    itemHeight: 60,
                    calculable: true,
                    precision: 2,
                    text: ['明暗：活跃度'],
                    textGap: 30,
                    // textStyle: {
                    //     color: '#fff'
                    // },
                    inRange: {
                        colorLightness: [0.8, 0.5]
                    },
                    outOfRange: {
                        color: ['rgba(255,255,255,.6)']
                    },
                    controller: {
                        inRange: {
                            color: ['#67d2e2']
                        },
                        outOfRange: {
                            color: ['#e4f5f9']
                        }
                    }
                }
            ],
            series: [
                {
                    name:"导购",
                    type: 'scatter',
                    itemStyle: itemStyle,
                    data: shop_data,
                    markLine : {
                        data : [
                            {type : 'average', name: '金额(平均值)',valueIndex:1},
                            {type : 'average', name: '折扣(平均值)',valueIndex:0}
                        ]
                    }
                }
            ]
        };//散点图
        //type == 3 ? myChart.setOption(options) : myChart.setOption(option);
        if(type == 3){
            myChart.setOption(options)
        }else{
            if(type==2){
                option.color=["#97c94b","#41c7db","#9999cc"]
            }else{
                option.color=['#41c7db','#97c94b','#d8d8e1']
            }
            myChart.setOption(option);
        }
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    },
    getstorelist: function (a) {//店铺接口
        var searchValue = $("#store_search").val();
        var _param = {};
        _param['area_code'] = shop.data.is_chart_select==true?shop.data.area_code_chart:shop.data.area_code;
        _param['brand_code'] = shop.data.is_chart_select==true?shop.data.brand_code_chart:shop.data.brand_code;
        _param['searchValue'] = searchValue;
        _param['pageNumber'] = a;
        _param['pageSize'] = 50;
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var total = list.total;
                var hasNextPage = list.hasNextPage;
                var list = list.list;
                var store_html = '';
                $("#screen_shop .s_pitch span").eq(1).text(total);
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        store_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].store_name + "</span></li>"
                    }
                }
                if (hasNextPage) {
                    shop.data.shop_num++;
                    shop.data.next_page = false;
                }else {
                    shop.data.next_page = true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if (!shop.data.isscroll) {
                    $("#screen_shop .screen_content_l").unbind("scroll").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                            if (shop.data.next_page) {
                                return;
                            }
                            shop.getstorelist(shop.data.shop_num);
                        }
                    })
                }
                shop.data.isscroll = true;
                var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_shop .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
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
        })
    },
    getarealist: function (a) {//区域接口
        var searchValue = $("#area_search").val().trim();
        var _param = {};
        _param["searchValue"] = searchValue;
        _param["pageSize"] = 20;
        _param["pageNumber"] = a;
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", "/area/selAreaByCorpCode", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var total =list.total;
                var hasNextPage = list.hasNextPage;
                var list = list.list;
                var area_html_left = '';
                $("#screen_area .s_pitch span").eq(1).text(total);
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        area_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].area_code + "' data-areaname='" + list[i].area_name + "' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].area_name + "</span></li>"
                    }
                }
                if (hasNextPage) {
                    shop.data.area_num++;
                    shop.data.next_page = false;
                }else {
                    shop.data.next_page = true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if (!shop.data.isscroll) {
                    $("#screen_area .screen_content_l").unbind("scroll").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight >= nScrollHight && nScrollTop!=0) {
                            if (shop.data.next_page) {
                                return;
                            }
                            shop.getarealist(shop.data.area_num);
                        }
                    })
                }
                shop.data.isscroll = true;
                var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_area .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
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
        })
    },
    //获取员工列表
    getstafflist:function (a){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    var searchValue=shop.data.is_chart_select==true?shop.data.staff_search_val_chart: shop.data.staff_search_val;
    _param['area_code']=shop.data.is_chart_select==true?shop.data.area_code_chart:shop.data.area_code;
    _param['brand_code']=shop.data.is_chart_select==true?shop.data.brand_code_chart:shop.data.brand_code;
    _param['store_code']=shop.data.is_chart_select==true?shop.data.store_code_chart:shop.data.store_code;
    _param['searchValue']=searchValue;
    _param["corp_code"]="C10000";
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
        if (data.code == "0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var total =list.total;
            var hasNextPage=list.hasNextPage;
                 list=list.list;
            var staff_html = '';
            $("#screen_staff .s_pitch span").eq(1).text(total);
            if (list.length == 0){

            } else if(list.length>0){
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
            if(hasNextPage==true){
                shop.data.staff_num++;
                shop.data.next_page=false;
            }
            if(hasNextPage==false){
                shop.data.next_page=true;
            }
            if(a==1&&searchValue==""){
                $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            //$("#choose_staff .select_list ul").append(choose_staff_html);
            if(!shop.data.isscroll){
                $("#screen_staff .screen_content_l").unbind("scroll").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight && nScrollTop!=0){
                        if(shop.data.next_page){
                            return;
                        }
                        shop.getstafflist(shop.data.staff_num);
                    }
                })
            }
        shop.data.isscroll=true;
            //$("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
            //$("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
            //$("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
            //$("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
            var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            whir.loading.remove();
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                zIndex:10003,
                content: data.message
            });
        }
    })
},
    getbrandlist: function () {
        var searchValue = $("#brand_search").val();
        var corp_code="C10000";
        var _param = {};
        _param["searchValue"] = shop.data.is_chart_select==true?shop.data.brand_search_val_chart:shop.data.brand_search_val;
        _param["corp_code"] = corp_code;
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", "/shop/brand", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = message.brands;
                var total = list.total;
                var brand_html_left = '';
                $("#screen_brand .s_pitch span").eq(1).text(total);
                if (list.length == 0) {
                    for (var h = 0; h < 9; h++) {
                        brand_html_left += "<li></li>"
                    }
                } else {
                    if (list.length < 9) {
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                        }
                        for (var j = 0; j < 9 - list.length; j++) {
                            brand_html_left += "<li></li>"
                        }
                    } else if (list.length >= 9) {
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                        }
                    }
                }
                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_brand .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
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
    },
    removeRight: function (a,b) {
        var li = "";
        if (a == "only") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
        }
        if (a == "all") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox'][data-type!='define']").parents("li:visible");
        }
        // if(li.length>100){
        //     art.dialog({
        //         zIndex: 10003,
        //         time: 1,
        //         lock: true,
        //         cancel: false,
        //         content: "请选择少于100个"
        //     });
        //     return;
        // }
        if (li.length == "0") {
            art.dialog({
                zIndex: 10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "请先选择"
            });
            return;
        }
        if (li.length > 0) {
            for (var i = 0; i < li.length; i++) {
                var html = $(li[i]).html();
                var id = $(li[i]).find("input[type='checkbox']").val();
                $(li[i]).find("input[type='checkbox']")[0].checked = true;
                var input = $(b).parents(".screen_content").find(".screen_content_r li");
                for (var j = 0; j < input.length; j++) {
                    if ($(input[j]).attr("id") == id) {
                        $(input[j]).remove();
                    }
                }
                $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>");
                $(b).parents(".screen_content").find(".screen_content_r input[value='" + id + "']").removeAttr("checked");
            }
        }
        var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
        $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
    },
    removeLeft: function(a, b){
        var li = "";
        if (a == "only") {
            li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
        }
        if (a == "all") {
            li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
        }
        if (li.length == "0") {
            art.dialog({
                zIndex: 10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "请先选择"
            });
            return;
        }
        if (li.length > 0) {
            for (var i = li.length - 1; i >= 0; i--) {
                $(li[i]).remove();
                $(b).parents(".screen_content").find(".screen_content_l input[value='" + $(li[i]).attr("id") + "']").removeAttr("checked");
            }
        }
        var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
        $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
    },
    timeSwitchEvent:function () {
        $("#time_nav_switch li").click(function () {
            var time_type = $(this).text();
            $(this).addClass("time_active");
            $(this).siblings("li").removeClass("time_active");
            $('#years').text(shop.data.year_choosed);
            shop.getTime(time_type);
            time_type == "年" ? $("#year_change_wrap").hide() : $("#year_change_wrap").show();
        });
        $("#time_details").on("click","li",function () {
            var nav = $("#time_nav_switch .time_active").text();
            $(this).addClass("choosedTime");
            $(this).siblings("li").removeClass("choosedTime");
            $("#chartTimeInput").attr("data-time",$(this).attr("data-time"));
            if(nav == "周"){
                $("#chartTimeInput").val($(this).children().eq(0).text());
            }else {
                if(nav == "年"){
                    shop.data.year_choosed = $(this).text();
                }
                $("#chartTimeInput").val($(this).text());
            }
            //shop.getChartData();
            //shop.getSale();//获取业绩
            $("#time_choose_wrap").hide();
        });//选中具体周月年点击事件
        $("#arrow_left").click(function () {
            var nav = $("#time_nav_switch .time_active").text();
            if(shop.data.year_choosed == shop.data.chart_year[0]){
                return ;
            }else {
                shop.data.year_choosed--
            }
            $('#years').text(shop.data.year_choosed);
            shop.getTime(nav);
        });//左切换年
        $("#arrow_right").click(function () {
            var nav = $("#time_nav_switch .time_active").text();
            var len = shop.data.chart_year.length-1;
            if(shop.data.year_choosed == shop.data.chart_year[len]){
                return ;
            }else {
                shop.data.year_choosed++;
            }
            $('#years').text(shop.data.year_choosed);
            shop.getTime(nav);
        });//右切换年
    },
    getYear:function () {//加载是获取当前年月
        var date = new Date();
        var Y = date.getFullYear();
        var M = date.getMonth()+1;
        var D = date.getDate();
        for(var i = 2000;i < (Y+1);i++){
            shop.data.chart_year.push(i);
        }
        $("#years").text(Y);
        shop.data.date_choosed = D;
        shop.data.year_choosed = Y;
        shop.data.month_choosed = M;
        shop.getTime("周").then(function () {
            $("#time_details .choosedTime").trigger("click");
            $("#filterBtn_1").trigger("click");
        });
    },
    getTime:function (type) {//周月年切换页面细节渲染
        var def=$.Deferred();
        var details = "";
        var year = shop.data.year_choosed;
        if(type == "年"){
            var year_arr = shop.data.chart_year;
            for(var i = 0;i < year_arr.length;i++){
                var li = year_arr[i] == year ? "<li data-time='"+year_arr[i]+"-01-01' class='choosedTime'>"+year_arr[i]+"</li>":"<li data-time='"+year_arr[i]+"-01-01'>"+year_arr[i]+"</li>";
                details += li;
            }
            $("#time_details").html(details);
        }
        if(type == "月"){
            for(var k = 1;k < 13;k++){
                var mm = k <10 ? "0"+k : k;
                var className = k == shop.data.month_choosed ? "class='choosedTime'" : "";
                details += "<li data-time='"+year+"-"+mm+"-01' "+className+">"+year+"年"+k+"月</li>"
            }
            $("#time_details").html(details);
        }
        if(type == "周"){
            var d = new Date(year,0,1);
            var w1 = d.getDay() || 7;
            if(w1>1&&w1<4){//1月1号若为周二周三，则从上一年开始算第一周
                var n = w1 - 1 ;
                d.setDate(d.getDate()-n);
            }else if(w1>3){
                var n = 7 - w1 + 1;
                d.setDate(d.getDate()+n);
            }
            var week = 1;
            var timer=setInterval(function(){
                week>1?d.setDate(d.getDate()+1):"";
                var Y=d.getFullYear();
                var M=d.getMonth()+1;
                var D=d.getDate();
                d.setDate(d.getDate()+6);
                var y=d.getFullYear();
                var m=d.getMonth()+1;
                var dd=d.getDate();
                var range = Y+"-"+M+"-"+D+" 至 "+y+"-"+m+"-"+dd;
                var className =  "";
                var mm_format = M < 10 ? "0"+M : M;
                var dd_format = D <10 ? "0"+D : D;
                if(M == m && shop.data.month_choosed == M && shop.data.date_choosed >= D && shop.data.date_choosed <= dd){
                    className = "choosedTime";
                }else if(M != m &&((shop.data.month_choosed == M && shop.data.date_choosed >= D)||(shop.data.month_choosed == m && shop.data.date_choosed <= dd))){
                    className = "choosedTime";
                }
                details += "<li data-time='"+Y+"-"+mm_format+"-"+dd_format+"' class='weekContent "+className+"'><div>"+year+"年"+week+"周</div><div class='color_grey'>"+range+"</div></li>";
                $("#time_details").html(details);
                // console.log("第"+week+"周"+Y+"年"+M+"月"+D+"日到"+y+"年"+m+"月"+dd+"日");
                week ++;
                if(y!=year||m==12&&(dd==29||dd==30||dd==31)){//最后一周的周日为29，30，31则为最后一周
                    clearInterval(timer);
                    def.resolve();
                }
            },0);
        }
        return def;
    },
    getSale:function () {
        var list = shop.filter_list_chart;
        var time_type = $("#time_nav_switch .time_active").text()=="周"?"W":$("#time_nav_switch .time_active").text()=="月"?"M":"Y";
        var time_value = $("#chartTimeInput").attr("data-time");
        var param = {
            list:list,
            time_type:time_type,
            time_value:time_value
        };
        oc.postRequire("post","/VipBusiness/empKpiSum","",param,function (data) {
            var msg = JSON.parse(data.message);
            $("#amt_trade").text("¥"+shop.formatCurrency(msg.amt_trade));
            $("#amt_trade_avg").text("¥"+shop.formatCurrency(msg.amt_trade_avg));
            $("#num_sales").text(shop.formatCurrency(msg.num_sales));
            $("#num_trade").text(shop.formatCurrency(msg.num_trade));
            $("#vip_amt_trade").text("¥"+shop.formatCurrency(msg.vip_amt_trade));
            $("#vip_amt_trade_avg").text("¥"+shop.formatCurrency(msg.vip_amt_trade_avg));
            $("#vip_num_sales").text(shop.formatCurrency(msg.vip_num_sales));
            $("#vip_num_trade").text(shop.formatCurrency(msg.vip_num_trade));
        });
    },
    getAgeGroup:function () {
        oc.postRequire("post","/VipBusiness/ageGroup","","",function (data) {
            if(data.code == "0"){
                var msg = JSON.parse(data.message);
                msg = msg.age.split(",");
                shop.data.ageGroup = msg;
            }else {
                console.log(data.message);
            }
        });
    },
    getWx:function () {//获取公众号
        var param={"corp_code":"C10000"};
        //获取公众号
        oc.postRequire("post","/corp/selectWx","",param,function (data) {
            if(data.code=="0") {
                var msg = JSON.parse(data.message);
                var list = msg.list;
                var li = "";
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        li+="<dd data-type='"+list[i].app_id+"'>"+list[i].app_name+"</dd>";
                    }
                    $("#Channel_member_nav dl").append(li);
                }
            }
        });
    },
    getVipCardType:function () {
        var param = {"corp_code": "C10000"};
        oc.postRequire("post", "/vipCardType/getVipCardTypes", "0", param, function (data) {
            if (data.code == 0) {
                var li = "";
                var message = JSON.parse(data.message);
                var msg = JSON.parse(message.list);
                for (var i = 0; i < msg.length; i++) {
                    li += "<dd data-type='" + msg[i].vip_card_type_name + "'>" + msg[i].vip_card_type_name + "</dd>"
                }
                $("#cardType_nav dl").append(li);
            }
        });
    },
    //getTabelList:function () {
    //    var type = $("#table_nav .nav-active").attr("data-nav");
    //    var search = shop.search_value;
    //    var list = shop.filter_list;
    //    var param = {
    //        "pageNumber":shop.data.Num,
    //        "pageSize":50,
    //        "searchValue":search,
    //        "list":list,
    //        "type":type
    //    };
    //    $("#table_loading").show();
    //    oc.postRequire("post","/VipBusiness/empKpi","",param,function (data) {
    //        if(data.code == 0){
    //            var msg = JSON.parse(data.message);
    //            var list = msg.message;
    //            var head = '<th width="10%">员工编号</th><th width="10%">员工名称</th><th width="10%">所属店铺</th>';
    //            var tbody = "",name = "";
    //            shop.data.has_next_list = msg.page_num == msg.page_count ? false : true;
    //            if(type != "wechat") {
    //                var amt_trade = "",num_sales = "",num_trade = "",trade_price = '',relate_rate = "",sale_price = "",avg_amt_trade = "",avg_num_sales = "",avg_num_trade = "";
    //                if(type == "vip"){
    //                    amt_trade = msg.sum.sum_vip_amt_trade;//总业绩
    //                    avg_amt_trade = msg.sum.avg_vip_amt_trade;//人均业绩
    //                    num_sales = msg.sum.sum_vip_num_sales;//总件数
    //                    avg_num_sales = msg.sum.avg_vip_num_sales;//人均笔数
    //                    num_trade = msg.sum.sum_vip_num_trade;//总笔数
    //                    avg_num_trade =msg.sum.avg_vip_num_trade;//人均笔数
    //                    trade_price = msg.sum.avg_vip_trade_price;//人均客单价
    //                    relate_rate = msg.sum.avg_vip_relate_rate;//人均连带率
    //                    sale_price = msg.sum.avg_vip_sales_price;//人均件单价
    //                    name = "会销";
    //                    for(var i=0;i<list.length;i++){
    //                        var name_one = list[i].vip_amt_trade > avg_amt_trade ? "color81a227" : "colord78558";
    //                        var name_two = list[i].vip_num_sales > avg_num_sales ? "color81a227" : "colord78558";
    //                        var name_three = list[i].vip_num_trade > avg_num_trade ? "color81a227" : "colord78558";
    //                        var name_four = list[i].trade_price > trade_price ? "color81a227" : "colord78558";
    //                        var name_five = list[i].relate_rate > relate_rate ? "color81a227" : "colord78558";
    //                        var name_six = list[i].sale_price > sale_price ? "color81a227" : "colord78558";
    //                        tbody += `<tr>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].user_code+`">`+list[i].user_code+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].user_name+`">`+list[i].user_name+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].store_name+`">`+list[i].store_name+`</span></td>
    //                    <!--<td width="10%"><span class="ellipisis" title="`+list[i].store_area+`">`+list[i].store_area+`</span></td>-->
    //                    <td width="10%" class="textCenter `+name_one+`">¥`+list[i].vip_amt_trade+`</td>
    //                    <td width="10%" class="textCenter `+name_two+`">`+list[i].vip_num_sales+`</td>
    //                    <td width="10%" class="textCenter `+name_three+`">`+list[i].vip_num_trade+`</td>
    //                    <td width="10%" class="textCenter `+name_four+`">¥`+list[i].trade_price+`</td>
    //                    <td width="10%" class="textCenter `+name_five+`">`+list[i].relate_rate+`</td>
    //                    <td width="10%" class="textCenter `+name_six+`">¥`+list[i].sale_price+`</td>
    //                    </tr>`
    //                    }
    //                }else if(type == "user"){
    //                    amt_trade = msg.sum.sum_amt_trade;
    //                    avg_amt_trade = msg.sum.avg_amt_trade;
    //                    num_sales = msg.sum.sum_num_sales;
    //                    avg_num_sales = msg.sum.avg_num_sales;
    //                    num_trade = msg.sum.sum_num_trade;
    //                    avg_num_trade =msg.sum.avg_num_trade;
    //                    trade_price = msg.sum.avg_trade_price;
    //                    relate_rate = msg.sum.avg_relate_rate;
    //                    sale_price = msg.sum.avg_sales_price;
    //                    for(var i=0;i<list.length;i++){
    //                        var name_one = list[i].amt_trade > avg_amt_trade ? "color81a227" : "colord78558";
    //                        var name_two = list[i].num_sales > avg_num_sales ? "color81a227" : "colord78558";
    //                        var name_three = list[i].num_trade > avg_num_trade ? "color81a227" : "colord78558";
    //                        var name_four = list[i].trade_price > trade_price ? "color81a227" : "colord78558";
    //                        var name_five = list[i].relate_rate > relate_rate ? "color81a227" : "colord78558";
    //                        var name_six = list[i].sale_price > sale_price ? "color81a227" : "colord78558";
    //                        tbody += `<tr>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].user_code+`">`+list[i].user_code+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].user_name+`">`+list[i].user_name+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].store_name+`">`+list[i].store_name+`</span></td>
    //                    <td width="10%" class="textCenter `+name_one+`">¥`+list[i].amt_trade+`</td>
    //                    <td width="10%" class="textCenter `+name_two+`">`+list[i].num_sales+`</td>
    //                    <td width="10%" class="textCenter `+name_three+`">`+list[i].num_trade+`</td>
    //                    <td width="10%" class="textCenter `+name_four+`">¥`+list[i].trade_price+`</td>
    //                    <td width="10%" class="textCenter `+name_five+`">`+list[i].relate_rate+`</td>
    //                    <td width="10%" class="textCenter `+name_six+`">¥`+list[i].sale_price+`</td>
    //                    </tr>`
    //                    }
    //                }
    //                head += `<th width="10%" class="bgf0f0f0 textCenter">`+name+`业绩<br/><span class="color33b4c7">总 ¥`+amt_trade+`</span><br/><span>人均 ¥`+avg_amt_trade+`</span></th>
    //                    <th width="10%" class="textCenter">`+name+`件数<br/><span class="color33b4c7">总 `+num_sales+`</span><br/><span>人均 `+avg_num_sales+`</span></th>
    //                    <th width="10%" class="bgf0f0f0 textCenter">`+name+`笔数<br/><span class="color33b4c7">总 `+num_trade+`</span><br/><span>人均 `+avg_num_trade+`</span></th>
    //                    <th width="10%" class="textCenter textCenter">`+name+`客单价<br/><span>人均 `+trade_price+`</span></th>
    //                    <th width="10%" class="bgf0f0f0 textCenter">`+name+`连带率<br/><span>人均 `+relate_rate+`</span></th>
    //                    <th width="10%" class="textCenter">`+name+`件单价<br/><span>人均 ¥`+sale_price+`</span></th>`;
    //            }else if(type == "wechat"){
    //                head += `<th width="10%" class="bgf0f0f0 textCenter">微会员数量<br/><span class="color33b4c7">总 `+msg.sum.sum_wechat_vip_sum+`</span><br/><span>人均 `+msg.sum.avg_wechat_vip_sum+`</span></th>
    //                    <th width="10%" class="textCenter">新增粉丝<br/><span class="color33b4c7">总 `+msg.sum.sum_new_fans+`</span><br/><span>人均 `+msg.sum.avg_new_fans+`</span></th>
    //                    <th width="10%" class="bgf0f0f0 textCenter">新增微会员<br/><span class="color33b4c7">总 `+msg.sum.sum_wechat_vip+`</span><br/><span>人均 `+msg.sum.avg_wechat_vip+`</span></th>
    //                    <th width="10%" class="textCenter textCenter">绑卡微会员<br/><span class="color33b4c7">总 `+msg.sum.sum_wechat_vip_bind+`</span><br/><span>人均 `+msg.sum.avg_wechat_vip_bind+`</span></th>`;
    //                for(var i=0;i<list.length;i++){
    //                    var name_one = list[i].wechat_vip > msg.sum.avg_wechat_vip_sum ? "color81a227" : "colord78558";
    //                    var name_two = list[i].new_fans > msg.sum.avg_new_fans ? "color81a227" : "colord78558";
    //                    var name_three = list[i].new_vip > msg.sum.avg_wechat_vip ? "color81a227" : "colord78558";
    //                    var name_four = list[i].wechat_vip_bind > msg.sum.avg_wechat_vip_bind ? "color81a227" : "colord78558";
    //                    tbody += `<tr>
    //                            <td width="10%"><span class="ellipisis" title="`+list[i].user_code+`">`+list[i].user_code+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].user_name+`">`+list[i].user_name+`</span></td>
    //                    <td width="10%"><span class="ellipisis" title="`+list[i].store_name+`">`+list[i].store_name+`</span></td>
    //                    <td width="10%" class="textCenter `+name_one+`">`+list[i].wechat_vip_sum+`</td>
    //                    <td width="10%" class="textCenter `+name_two+`">`+list[i].new_fans+`</td>
    //                    <td width="10%" class="textCenter `+name_three+`">`+list[i].wechat_vip+`</td>
    //                    <td width="10%" class="textCenter `+name_four+`">`+list[i].wechat_vip_bind+`</td>
    //                    </tr>`
    //                }
    //            }
    //            shop.data.Num == 1 ? ($("#table_tbody tbody").html(tbody)&&$(".table_tbodyWrap").scrollTop(0)) : $("#table_tbody tbody").append(tbody);
    //            $("#table_thead thead").html(head);
    //            $("#table_loading").hide();
    //        }else {
    //            $("#table_loading").hide();
    //            var tr = "";
    //            for(var i=0;i<10;i++){
    //                if(i==4){
    //                    tr += `<tr><td style="text-align: center;padding-left: 0" colspan="10">暂无数据</td></tr>`
    //                }else {
    //                    tr += `<tr><td colspan="10"></td></tr>`
    //                }
    //            }
    //            $("#table_tbody tbody").html(tr)
    //        }
    //    })
    //}
    formatCurrency: function (num) {
        num = String(num);
        var reg = num.indexOf('.') > -1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
        num = num.replace(reg, '$1,');//千分位格式化
        return num;
    },
    getTabelList:function () {
        var type = shop.nav_type;
        var search = shop.search_value;
        var list = shop.filter_list;
        var query_type = shop.query_type;
        var param = {
            "pageNumber":shop.data.Num,
            "pageSize":50,
            "searchValue":search,
            "list":list,
            "type":type,
            "query_type":query_type
        };
        $("#table_loading").show();
        oc.postRequire("post","/VipBusiness/empKpi","",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var list = msg.message;
                var sum = msg.sum;
                var tbody = [],head_concat = [];
                shop.data.has_next_list = msg.page_num == msg.page_count ? false : true;
                shop.allData=Number(msg.count);
                var head = [
                    {"name":"员工编号", "width":"10"},
                    {"name":"员工名称", "width":"10"},
                    {"name":"所属店铺", "width":"10"}
                ];
                $(".table_tbodyWrap").css("overflow-y","auto");
                switch (type){
                    case "vip" : head_concat = [
                        {"name":"会销金额", "width":"8.57", "all":'¥'+shop.formatCurrency(sum.sum_vip_amt_trade), "avg":'¥'+shop.formatCurrency(sum.avg_vip_amt_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"会销件数", "width":"8.57", "all":shop.formatCurrency(sum.sum_vip_num_sales), "avg":shop.formatCurrency(sum.avg_vip_num_sales),"classname":"textCenter"},
                        {"name":"会销笔数", "width":"8.57", "all":shop.formatCurrency(sum.sum_vip_num_trade), "avg":shop.formatCurrency(sum.avg_vip_num_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"会销件单价", "width":"8.57", "avg":'¥'+shop.formatCurrency(sum.avg_vip_sales_price),"classname":"textCenter"},
                        {"name":"会销客单价", "width":"8.57", "avg":'¥'+shop.formatCurrency(sum.avg_vip_trade_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"会销连带率", "width":"8.57", "avg":sum.avg_vip_relate_rate,"classname":"textCenter"},
                        {"name":"会销占比", "width":"8.57", "per":sum.sum_vip_rate,"classname":"bgf0f0f0 textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":'¥'+shop.formatCurrency(item.vip_amt_trade), "width":"8.57"},
                                {"name":shop.formatCurrency(item.vip_num_sales), "width":"8.57"},
                                {"name":shop.formatCurrency(item.vip_num_trade), "width":"8.57"},
                                {"name":'¥'+shop.formatCurrency(item.sale_price), "width":"8.57"},
                                {"name":'¥'+shop.formatCurrency(item.trade_price), "width":"8.57"},
                                {"name":item.relate_rate, "width":"8.57"},
                                {"name":item.vip_rate, "width":"8.57","per":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                    case "user" : head_concat = [
                        {"name":"业绩", "width":"10", "all":shop.formatCurrency(sum.sum_amt_trade), "avg":shop.formatCurrency(sum.avg_amt_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"件数", "width":"10", "all":shop.formatCurrency(sum.sum_num_sales), "avg":shop.formatCurrency(sum.avg_num_sales),"classname":"textCenter"},
                        {"name":"笔数", "width":"10", "all":shop.formatCurrency(sum.sum_num_trade), "avg":shop.formatCurrency(sum.avg_num_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"件单价", "width":"10","avg":shop.formatCurrency(sum.avg_sales_price),"classname":"textCenter"},
                        {"name":"客单价", "width":"10","avg":shop.formatCurrency(sum.avg_trade_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"连带率", "width":"10","avg":sum.avg_relate_rate,"classname":"textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":'¥'+shop.formatCurrency(item.amt_trade), "width":"10"},
                                {"name":shop.formatCurrency(item.num_sales), "width":"10"},
                                {"name":shop.formatCurrency(item.num_trade), "width":"10"},
                                {"name":'¥'+shop.formatCurrency(item.sale_price), "width":"10"},
                                {"name":'¥'+shop.formatCurrency(item.trade_price), "width":"10"},
                                {"name":item.relate_rate, "width":"10"}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                    case "vipNum" : head_concat = [
                        {"name":"总数", "width":"8.75", "all":shop.formatCurrency(sum.sum_vip_all), "avg":shop.formatCurrency(sum.avg_vip_all),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新增会员", "width":"8.75", "all":shop.formatCurrency(sum.sum_vip_new), "avg":shop.formatCurrency(sum.avg_vip_new),"classname":"textCenter"},
                        {"name":"新客人数", "width":"8.75", "all":shop.formatCurrency(sum.sum_new_vip), "avg":shop.formatCurrency(sum.avg_new_vip),"classname":"bgf0f0f0 textCenter"},
                        {"name":"复购人数", "width":"8.75", "all":shop.formatCurrency(sum.sum_sec_vip), "per":shop.formatCurrency(sum.rate_sec_vip),"classname":"textCenter"},
                        {"name":"已购(总)", "width":"8.75", "all":shop.formatCurrency(sum.sum_vip_have_trade_all), "per":shop.formatCurrency(sum.rate_vip_have_trade_all),"classname":"bgf0f0f0 textCenter"},
                        {"name":"已购(新增)", "width":"8.75", "all":shop.formatCurrency(sum.sum_vip_have_trade_new), "per":shop.formatCurrency(sum.rate_vip_have_trade_new),"classname":"textCenter"},
                        {"name":"活跃人数", "width":"8.75", "all":shop.formatCurrency(sum.sum_vip_active), "per":shop.formatCurrency(sum.rate_vip_active),"classname":"bgf0f0f0 textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.vip_all), "width":"8.75"},
                                {"name":shop.formatCurrency(item.vip_new), "width":"8.75"},
                                {"name":shop.formatCurrency(item.new_vip), "width":"8.75"},
                                {"name":shop.formatCurrency(item.sec_vip), "width":"8.75"},
                                {"name":shop.formatCurrency(item.vip_have_trade_all), "width":"8.75"},
                                {"name":shop.formatCurrency(item.vip_have_trade_new), "width":"8.75"},
                                {"name":shop.formatCurrency(item.vip_active), "width":"8.75"}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                    case "newVip" : head_concat = [
                        {"name":"新客人数", "width":"6", "all":shop.formatCurrency(sum.sum_new_vip), "avg":shop.formatCurrency(sum.avg_new_vip),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新客已购", "width":"6", "all":shop.formatCurrency(sum.sum_new_vip_trade), "per":shop.formatCurrency(sum.rate_new_vip_trade),"classname":"textCenter"},
                        {"name":"新客消费", "width":"6", "all":shop.formatCurrency(sum.sum_amt_trade), "avg":shop.formatCurrency(sum.avg_amt_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新客件数", "width":"6", "all":shop.formatCurrency(sum.sum_num_sales), "avg":shop.formatCurrency(sum.avg_num_sales),"classname":"textCenter"},
                        {"name":"新客笔数", "width":"6", "all":shop.formatCurrency(sum.sum_num_trade), "avg":shop.formatCurrency(sum.avg_num_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新客件单价", "width":"6","avg":shop.formatCurrency(sum.avg_sales_price),"classname":"textCenter"},
                        {"name":"新客客单价", "width":"6","avg":shop.formatCurrency(sum.avg_trade_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新客连带率", "width":"6","avg":sum.avg_relate_rate,"classname":"textCenter"},
                        {"name":"占比/总业绩", "width":"6","avg":sum.avg_achv_rate,"per":"", "classname":"bgf0f0f0 textCenter"},
                        {"name":"占比/会销", "width":"6","avg":sum.avg_vip_achv_rate,"per":"", "classname":"textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.new_vip), "width":"6", "avg":""},
                                {"name":shop.formatCurrency(item.new_vip_trade), "width":"6"},
                                {"name":'¥'+shop.formatCurrency(item.amt_trade), "width":"6"},
                                {"name":shop.formatCurrency(item.num_sales), "width":"6"},
                                {"name":shop.formatCurrency(item.num_trade), "width":"6"},
                                {"name":'¥'+shop.formatCurrency(item.sale_price), "width":"6"},
                                {"name":'¥'+shop.formatCurrency(item.trade_price), "width":"6"},
                                {"name":item.relate_rate, "width":"6"},
                                {"name":item.achv_rate, "width":"6","per":""},
                                {"name":item.vip_achv_rate, "width":"6","per":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","115%");
                        break;
                    case "actVip" : head_concat = [
                        {"name":"活跃会员人数", "width":"6.66", "all":shop.formatCurrency(sum.sum_act_vips), "per":shop.formatCurrency(sum.rate_act_vips),"classname":"bgf0f0f0 textCenter"},
                        {"name":"活跃业绩", "width":"6.66", "all":shop.formatCurrency(sum.sum_amt_trade), "avg":shop.formatCurrency(sum.avg_amt_trade),"classname":"textCenter"},
                        {"name":"活跃件数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_sales), "avg":shop.formatCurrency(sum.avg_num_sales),"classname":"bgf0f0f0 textCenter"},
                        {"name":"活跃笔数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_trade), "avg":shop.formatCurrency(sum.avg_num_trade),"classname":"textCenter"},
                        {"name":"活跃件单价", "width":"6.66","avg":shop.formatCurrency(sum.avg_sale_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"活跃客单价", "width":"6.66","avg":shop.formatCurrency(sum.avg_trade_price),"classname":"textCenter"},
                        {"name":"活跃连带率", "width":"6.66","avg":sum.avg_relate_rate,"classname":"bgf0f0f0 textCenter"},
                        {"name":"占比/总业绩", "width":"6.66","avg":sum.avg_achv_rate,"per":"", "classname":"textCenter"},
                        {"name":"占比/会销", "width":"6.66","avg":sum.avg_vip_achv_rate,"per":"", "classname":"bgf0f0f0 textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.act_vips), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.amt_trade), "width":"6.66"},
                                {"name":shop.formatCurrency(item.num_sales), "width":"6.66"},
                                {"name":shop.formatCurrency(item.num_trade), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.sale_price), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.trade_price), "width":"6.66"},
                                {"name":item.relate_rate, "width":"6.66"},
                                {"name":item.achv_rate, "width":"6.66","per":""},
                                {"name":item.vip_achv_rate, "width":"6.66","per":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","115%");
                        break;
                    case "secVip" : head_concat = [
                        {"name":"复购人数", "width":"6.66", "all":shop.formatCurrency(sum.sum_sec_vips), "per":shop.formatCurrency(sum.rate_sec_vips),"classname":"bgf0f0f0 textCenter"},
                        {"name":"复购消费", "width":"6.66", "all":shop.formatCurrency(sum.sum_amt_trade), "avg":shop.formatCurrency(sum.avg_amt_trade),"classname":"textCenter"},
                        {"name":"复购件数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_sales), "avg":shop.formatCurrency(sum.avg_num_sales),"classname":"bgf0f0f0 textCenter"},
                        {"name":"复购笔数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_trade), "avg":shop.formatCurrency(sum.avg_num_trade),"classname":"textCenter"},
                        {"name":"复购件单价", "width":"6.66", "avg":shop.formatCurrency(sum.avg_sale_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"复购客单价", "width":"6.66","avg":shop.formatCurrency(sum.avg_trade_price),"classname":"textCenter"},
                        {"name":"复购连带率", "width":"6.66", "avg":sum.avg_relate_rate,"classname":"bgf0f0f0 textCenter"},
                        {"name":"占比/总业绩", "width":"6.66", "avg":sum.avg_achv_rate,"per":"", "classname":"textCenter"},
                        {"name":"占比/会销", "width":"6.66", "avg":sum.avg_vip_achv_rate,"per":"", "classname":"bgf0f0f0 textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.sec_vips), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.amt_trade), "width":"6.66"},
                                {"name":shop.formatCurrency(item.num_sales), "width":"6.66"},
                                {"name":shop.formatCurrency(item.num_trade), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.sale_price), "width":"6.66"},
                                {"name":'¥'+shop.formatCurrency(item.trade_price), "width":"6.66"},
                                {"name":item.relate_rate, "width":"6.66"},
                                {"name":item.achv_rate, "width":"6.66","per":""},
                                {"name":item.vip_achv_rate, "width":"6.66","per":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","115%");
                        break;
                    case "vipAge" : head_concat = [
                        {"name":"平均年龄", "width":"10","classname":"bgf0f0f0 textCenter"}
                    ];
                        var age = shop.data.ageGroup;
                        var w = 50/(age.length).toFixed(2);
                        head_concat = head_concat.concat(age.map(function (item,i) {
                            var obj = {};
                            var classname = i%2==0 ? "textCenter" : "bgf0f0f0 textCenter"
                            obj.name = item;
                            obj.all = sum['sum_'+item];
                            obj.avg = sum['avg_'+item];
                            obj.classname = classname;
                            obj.width = w;
                            return obj;
                        }));
                        tbody = list.map(function (item) {
                            var arr = [
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":item.age_avg, "width":"10"}
                            ];
                            age.map(function (items) {
                                var obj = {"name":shop.formatCurrency(item[items]),"width":w};
                                arr.push(obj);
                            });
                            var objs = {"data":arr};
                            return objs;
                        });
                        break;
                    case "vipSex" : head_concat = [
                        {"name":"总数(男/女)", "width":"10", "all":shop.formatCurrency(sum.sum_vip_m)+'/'+shop.formatCurrency(sum.sum_vip_f),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新增数量(男/女)", "width":"10", "all":shop.formatCurrency(sum.sum_vip_new_m)+'/'+shop.formatCurrency(sum.sum_vip_new_f), "avg":shop.formatCurrency(sum.avg_vip_new_m)+'/'+shop.formatCurrency(sum.avg_vip_new_f),"classname":"bgf0f0f0 textCenter"},
                        {"name":"销售金额(男/女)", "width":"10", "all":shop.formatCurrency(sum.sum_amt_trade_M)+'/'+shop.formatCurrency(sum.sum_amt_trade_F), "avg":shop.formatCurrency(sum.avg_amt_trade_M)+'/'+shop.formatCurrency(sum.avg_amt_trade_F),"classname":"bgf0f0f0 textCenter"},
                        {"name":"销售件数(男/女)", "width":"10", "all":shop.formatCurrency(sum.sum_num_sales_M)+'/'+shop.formatCurrency(sum.sum_num_sales_F), "avg":shop.formatCurrency(sum.avg_num_sales_M)+'/'+shop.formatCurrency(sum.avg_num_sales_F),"classname":"bgf0f0f0 textCenter"},
                        {"name":"销售笔数(男/女)", "width":"10", "all":shop.formatCurrency(sum.sum_num_trade_M)+'/'+shop.formatCurrency(sum.sum_num_trade_F), "avg":shop.formatCurrency(sum.avg_num_trade_M)+'/'+shop.formatCurrency(sum.avg_num_trade_F),"classname":"bgf0f0f0 textCenter"},
                        {"name":"折扣(男/女)", "width":"10","avg":sum.avg_discount_M+'/'+sum.avg_discount_F,"classname":"bgf0f0f0 textCenter"},
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.vip_m)+'/'+shop.formatCurrency(item.vip_f), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.vip_new_m)+'/'+shop.formatCurrency(item.vip_new_f), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.amt_trade_M)+'/'+shop.formatCurrency(item.amt_trade_F), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.num_sales_M)+'/'+shop.formatCurrency(item.num_sales_F), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.num_trade_M)+'/'+shop.formatCurrency(item.num_trade_F), "width":"10", "avg":""},
                                {"name":item.discount_M+'/'+item.discount_F, "width":"10", "avg":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                    case "vipSource" : head_concat = [
                        {"name":"会员数量", "width":"10", "all":shop.formatCurrency(sum.sum_wechat_vip_sum), "avg":shop.formatCurrency(sum.avg_wechat_vip_sum),"classname":"bgf0f0f0 textCenter"},
                        {"name":"新增会员", "width":"10", "all":shop.formatCurrency(sum.sum_wechat_vip), "avg":shop.formatCurrency(sum.avg_wechat_vip),"classname":"textCenter"},
                        {"name":"绑卡会员", "width":"10", "all":shop.formatCurrency(sum.sum_wechat_vip_bind), "avg":shop.formatCurrency(sum.avg_wechat_vip_bind),"classname":"bgf0f0f0 textCenter"},
                        {"name":"粉丝总数", "width":"10", "all":shop.formatCurrency(sum.sum_fans), "avg":shop.formatCurrency(sum.avg_fans),"classname":"textCenter"},
                        {"name":"新增粉丝", "width":"10", "all":shop.formatCurrency(sum.sum_new_fans), "avg":shop.formatCurrency(sum.avg_new_fans),"classname":"bgf0f0f0 textCenter"},
                        {"name":"已购会员", "width":"10", "all":shop.formatCurrency(sum.sum_wechat_vip_buy), "avg":shop.formatCurrency(sum.avg_wechat_vip_buy),"classname":"textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.wechat_vip_sum), "width":"10", "avg":"","classname":""},
                                {"name":shop.formatCurrency(item.wechat_vip), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.wechat_vip_bind), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.fans), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.new_fans), "width":"10", "avg":""},
                                {"name":shop.formatCurrency(item.wechat_vip_buy), "width":"10", "avg":""}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                    case "vipCardType" : head_concat = [
                        {"name":"会员总数", "width":"6.66", "all":shop.formatCurrency(sum.sum_vip_num), "avg":shop.formatCurrency(sum.avg_vip_num),"classname":"bgf0f0f0 textCenter"},
                        {"name":"已购会员", "width":"6.66", "all":shop.formatCurrency(sum.sum_vip_num_trade), "avg":shop.formatCurrency(sum.avg_vip_num_trade),"classname":"textCenter"},
                        {"name":"会销金额", "width":"6.66", "all":"¥"+shop.formatCurrency(sum.sum_amt_trade), "avg":"¥"+shop.formatCurrency(sum.avg_amt_trade),"classname":"bgf0f0f0 textCenter"},
                        {"name":"业绩占比", "width":"6.66", "all":sum.sum_achv_rate, "avg":sum.avg_achv_rate,"per":"", "classname":"textCenter"},
                        {"name":"会销件数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_sales), "avg":shop.formatCurrency(sum.avg_num_sales),"classname":"bgf0f0f0 textCenter"},
                        {"name":"会销笔数", "width":"6.66", "all":shop.formatCurrency(sum.sum_num_trade), "avg":shop.formatCurrency(sum.avg_num_trade),"classname":"textCenter"},
                        {"name":"会销件单价", "width":"6.66","avg":"¥"+shop.formatCurrency(sum.avg_sale_price),"classname":"bgf0f0f0 textCenter"},
                        {"name":"会销客单价", "width":"6.66","avg":"¥"+shop.formatCurrency(sum.avg_trade_price),"classname":"textCenter"},
                        {"name":"会销连带率", "width":"6.66","avg":sum.avg_relate_rate,"classname":"bgf0f0f0 textCenter"}
                    ];
                        tbody = list.map(function (item) {
                            var obj = {"data":[
                                {"name":item.user_code, "width":"10","classname":"ellipisis"},
                                {"name":item.user_name, "width":"10","classname":"ellipisis"},
                                {"name":item.store_name, "width":"10","classname":"ellipisis"},
                                {"name":shop.formatCurrency(item.vip_num), "width":"6.66"},
                                {"name":shop.formatCurrency(item.vip_num_trade), "width":"6.66"},
                                {"name":"¥"+shop.formatCurrency(item.amt_trade), "width":"6.66"},
                                {"name":shop.formatCurrency(item.achv_rate), "width":"6.66","per":""},
                                {"name":shop.formatCurrency(item.num_sales), "width":"6.66"},
                                {"name":shop.formatCurrency(item.num_trade), "width":"6.66"},
                                {"name":"¥"+shop.formatCurrency(item.sale_price), "width":"6.66"},
                                {"name":"¥"+shop.formatCurrency(item.trade_price), "width":"6.66"},
                                {"name":item.relate_rate, "width":"6.66"}
                            ]};
                            return obj;
                        });
                        $(".tabel_x_scroll").css("width","100%");
                        break;
                }
                head = head.concat(head_concat);
                var head_html = head.map(function (item) {
                    var data = Number(item.avg)*100;
                    data = data.toFixed(2)+"%";
                    var all = item.all != undefined ? '<br/><span class="color33b4c7">总 '+shop.formatCurrency(item.all)+'</span>' : '';
                    var avg = (item.avg != undefined&&item.per != undefined)? '<br/><span>均 '+shop.formatCurrency(data)+'</span>' :item.avg != undefined ? '<br/><span>均 '+item.avg+'</span>' : item.per != undefined ? '<br/><span>占比 '+(item.per*100).toFixed(2)+'%'+'</span>' : "";
                    return '<th width="'+item.width+'%" class="'+item.classname+'">'+item.name+all+avg+'</th>'
                });
                var tbody_html = tbody.map(function (item) {
                    var td = "";
                    item.data.map(function (items,i) {
                        var cur_avg = head[i].avg;
                        var per = items.per;
                        var data = items.name;
                        if(cur_avg != undefined && cur_avg.toString().indexOf("/") < 0){
                            var avg = Number(cur_avg.toString().replace("¥",""));
                            var compare = items.name.toString();
                            var name = Number(compare.replace("¥","")) > avg ? "color81a227" : "colord78558";
                        }
                        if(per != undefined){
                            data = Number(data)*100;
                            data = data.toFixed(2)+"%"
                        }
                        items.classname == "ellipisis" ? td += '<td width="'+items.width+'%"><span class="'+items.classname+'" title="'+data+'">'+data+'</span></td>' : td += '<td width="'+items.width+'%" class="textCenter '+name+'">'+data+'</td>';
                    });
                    return '<tr>'+td+'</tr>';
                });
                $("#table_thead thead").html(head_html);
                if(list.length == 0){
                    $("#table_loading").hide();
                    $(".table_tbodyWrap").css("overflow-y","hidden");
                    var tr = "";
                    for(var i=0;i<10;i++){
                        if(i==4){
                            tr += '<tr><td style="text-align: center;padding-left: 0" colspan="10">暂无数据</td></tr>'
                        }else {
                            tr += '<tr><td colspan="10"></td></tr>'
                        }
                    }
                    $("#table_tbody tbody").html(tr);
                    return;
                }
                shop.data.Num == 1 ? ($("#table_tbody tbody").html(tbody_html)&&$(".table_tbodyWrap").scrollTop(0)) : $("#table_tbody tbody").append(tbody_html);
                $("#table_loading").hide();
                $(".table_thead").css("padding-right",$(".table_tbodyWrap")[0].offsetWidth-$(".table_tbodyWrap")[0].clientWidth);
            }else {
                $("#table_loading").hide();
                var tr = "";
                $(".table_tbodyWrap").css("overflow-y","hidden");
                for(var i=0;i<10;i++){
                    if(i==4){
                        tr += '<tr><td style="text-align: center;padding-left: 0" colspan="10">获取数据失败</td></tr>'
                    }else {
                        tr += '<tr><td colspan="10"></td></tr>'
                    }
                }
                $("#table_tbody tbody").html(tr)
            }
        })
    }
};
$(function(){
    shop.init();
    $(".table_contentWrap").niceScroll({
        cursorborder: "0 none",
        cursoropacitymin: "0",
        boxzoom: false,
        cursorcolor: " #dfdfdf",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 200,
        autohidemode: true,
        cursorwidth: "9px",
        cursorborderradius: "10px"
    });
});
var start = {
    elem: '#time_start',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: false,
    fixed: false,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas; //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#time_end',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: false,
    fixed: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);

$(".scroll_area").niceScroll({
    cursorborder: "0 none",
    cursoropacitymin: "0",
    boxzoom: false,
    cursorcolor: " #dfdfdf",
    cursoropacitymax: 1,
    touchbehavior: false,
    cursorminheight: 50,
    autohidemode: false,
    cursorwidth: "9px",
    cursorborderradius: "10px"
});

function create_export_list(){
    var allData=shop.allData;
    var list_html="";
    var allPage=Math.ceil(allData/2000);
    $("#export_list_all ul").html("");
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*2000 + 1;
        var end_num="";
        if (allData < a*2000 ){
            end_num = allData
        }else{
            end_num = a*2000
        }
        list_html+= '<li>'
            +'<span style="float: left">导购VIP经营表('+start_num+'~'+end_num+')</span>'
            +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
            +'<span style="margin-right:10px;" class="state"></span>'
            +'</li>'
    }
    $("#export_list_all ul").html(list_html);
}
//导出提交的
$("#list_export_btn").click(function(){
    $.blockUI({ message:null,overlayCSS: { cursor:"default",opacity:"0.1",zIndex:600 },fadeIn:0 });
    var allData=shop.allData;
    if(allData!=0 && $("#export_list_all ul li").length==0){
        create_export_list();
    }
    $("#export_list").show();
    $("#export_list_all").scrollTop(0);
    $("#export_list_all").on("click",".export_list_btn",function () {
        if($(this).hasClass("btn_active")){
            return
        }
        if(export_list_Array.length==0){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请先选择列表"
            });
            return
        }
        var self=$(this);
        var page=$(this).attr("data-page");
        $(this).next().text("导出中...");
        $(this).addClass("btn_active");
        var search = shop.search_value;
        var list = shop.filter_list;
        var param = {
            "pageNumber":page,
            "pageSize":2000,
            "searchValue":search,
            "list":list,
            "type":export_list_Array,
             "execl_type":"user"
        };
        oc.postRequire("post","/VipBusiness/storeExportExecl","0",param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var path=message.path;
                self.attr("data-path",path);
                self.next().text("导出完成");
                self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                self.css("backgroundColor","#637ea4");
            }else if(data.code=="-1"){
                self.removeClass("btn_active");
                self.next().text("导出失败");
            }
        })
    });
});

$("#select_export_type").click(function(e){
    e.stopPropagation();
    if($(".export_type_first_list ul li").length!=0){
        $(".export_type_first_list").show();
        return
    }
    var li=$("#table_nav li");
    var textArray=[];
    var liHtml='';
    li.map(function(){
        var obj={};
        if($(this).hasClass("nav_select")){
            obj.name=$(this).find("label").html();
            obj.type=$(this).attr("data-nav");
            obj.children=[];
            var children=$(this).find("dl").children();
            children.map(function(){
                obj.children.push({
                    name:$(this).html(),
                    type:$(this).attr("data-type")
                })
            })
        }else{
            obj.name=$(this).html();
            obj.type=$(this).attr("data-nav");
        }
        textArray.push(obj)
    });
    for(var t=0;t<textArray.length;t++){
        var arrow='';
        var ul="";
        if(textArray[t].children!=undefined){
            var childrenHtml="";
            arrow='<span class="icon-ishop_8-03 arrow_right"></span>';
            var children=textArray[t].children;
            for(var c=0;c<children.length;c++){
                childrenHtml+='<li data-type="'+children[c].type+'">'
                    +'<div class="checkbox" ><input type="checkbox" value="" name="test" class="check" ><label ></label></div>'
                    +'<span>'+children[c].name+'</span>'
                    +'</li>';
            }
            ul='<div class="export_type_second_list" data-type="'+textArray[t].type+'"><ul>'+childrenHtml+'</ul>'
                +'<div class="select_all">'
                +'<label class="select_second_all"><div class="checkbox"><input type="checkbox" value="" name="test" class="check" id=select_all_box_'+textArray[t].type+' ><label for=select_all_box_'+textArray[t].type+' ></label></div>'
                +'<span>全选</span></label>'
                +'</div>'
                +'</div>';
            $("#export_type_second_list_other").append(ul)
        }
        liHtml+='<li data-type="'+textArray[t].type+'">'
            +'<div class="checkbox" ><input type="checkbox" value="" name="test" class="check" ><label></label></div>'
            +'<span>'+textArray[t].name+'</span>'
            +arrow
            +'</li>';
    }
    $(".export_type_first_list ul").html(liHtml);
    $(".export_type_first_list").show();
    $(".export_type_second_list ul").niceScroll({
        cursorborder:"0 none",cursoropacitymin:"0",boxzoom:false,
        cursorcolor:" #dddddd",
        cursoropacitymax:1,
        touchbehavior:false,
        cursorminheight:50,
        cursorwidth:"5px",
        autohidemode:false,
        cursorborderradius:"1px"});
    $(".export_type_first_list ul").getNiceScroll().resize();
});
var showChildrenList=false;
$(".export_type_first_list>ul").on("click","li",function(e){
    e.stopPropagation();
    var check=$(this).children(".checkbox").children("input").attr("checked");
    if(check==undefined||check==false){
        $(this).children(".checkbox").children("input").attr("checked",true);
        $(this).addClass("active_slect_checkbox").siblings().removeClass("active_slect_checkbox");
        var H=$(this).position().top;
        if($(this).children(".arrow_right").length!=0){
            showChildrenList=true;
            var type=$(this).attr("data-type");
            var showChildren=$("#export_type_second_list_other").find(".export_type_second_list[data-type="+type+"]");

            $("#export_type_second_list_other").css("top",H+30);
            $("#export_type_second_list_other").show();
            showChildren.show();
            showChildren.siblings(".export_type_second_list").hide();
        }
    }else {
        $(this).children(".checkbox").children("input").attr("checked",false);
        $(this).removeClass("active_slect_checkbox");
        if($(this).children(".arrow_right").length!=0){
            showChildrenList=false;
            var type=$(this).attr("data-type");
            var showChildren=$("#export_type_second_list_other").find(".export_type_second_list[data-type="+type+"]");
            $("#export_type_second_list_other").hide();
            showChildren.hide();
        }
    }

    var len=$(".export_type_first_list li .checkbox input[checked='checked']").parents("li").length;
    var allLen=$(".export_type_first_list li").length;
    if(len==allLen){
        $(".export_type_first_list .select_all input").attr("checked",true)
    }else{
        $(".export_type_first_list .select_all input").attr("checked",false)
    }
});

$("#export_type_second_list_other").on("click","li",function(e){
    e.stopPropagation();
    var check=$(this).children(".checkbox").children("input").attr("checked");
    if(check==undefined||check==false){
        $(this).children(".checkbox").children("input").attr("checked",true)
    }else {
        $(this).children(".checkbox").children("input").attr("checked",false)
    }
   var len=$(".export_type_second_list:visible li .checkbox input[checked='checked']").parents("li").length;
   var allLen=$(".export_type_second_list:visible li").length;
    if(len==allLen){
        $(".export_type_second_list:visible .select_all input").attr("checked",true)
    }else{
        $(".export_type_second_list:visible .select_all input").attr("checked",false)
    }
});

$(".export_type_first_list>ul").on("mouseenter","li",function(){
    showChildrenList=false;
    $("#export_type_second_list_other").hide();
});
$(".export_type_first_list>ul").on("mouseover","li",function(){
    var H=$(this).position().top;
    var check=$(this).children(".checkbox").children("input").attr("checked");
    if($(this).children(".arrow_right").length!=0 && check=="checked"){
        var type=$(this).attr("data-type");
        var showChildren=$("#export_type_second_list_other").find(".export_type_second_list[data-type="+type+"]");
        $("#export_type_second_list_other").css("top",H+30);
        $("#export_type_second_list_other").show();
        showChildren.show();
        showChildren.siblings(".export_type_second_list").hide();
        $(".export_type_second_list ul").getNiceScroll().resize();
    }
});
$(".export_type_first_list>ul").on("mouseout","li",function(){
    var check=$(this).children(".checkbox").children("input").attr("checked");
    if($(this).children(".arrow_right").length!=0){
        if(!showChildrenList && check!="checked"){
            $("#export_type_second_list_other").hide();
        }
    }
});

$("#select_sure").click(function(e){
    e.stopPropagation();
    export_list_Array=[];
    var li=$(".export_type_first_list li .checkbox input[checked='checked']").parents("li");
    var hasChildrenLi=$(".export_type_first_list li .arrow_right").parents("li");
    hasChildrenLi.map(function(){
        var type=$(this).attr("data-type");
        if($(this).find(".checkbox input:checkbox").attr("checked")!="checked"){
           $("#export_type_second_list_other .export_type_second_list[data-type="+type+"] .checkbox input:checkbox").attr("checked",false)
        }
    });
    li.map(function(){
        var type=$(this).attr("data-type");
        if($(this).children(".arrow_right").length!=0){
            var children=[];
            var childrenLi=$("#export_type_second_list_other .export_type_second_list[data-type="+type+"] li .checkbox input[checked='checked']").parents("li");
            childrenLi.map(function(){
                children.push($(this).attr("data-type"))
            });
            export_list_Array.push({type:type,query_type:children})
        }else {
            export_list_Array.push({type:type})
        }
    });
    $(".export_type_first_list").hide();
    $("#export_type_second_list_other").hide();
    if(export_list_Array.length!=0){
        $("#select_export_type").val("已选"+export_list_Array.length+"个")
    }else {
        $("#select_export_type").val("")
    }
    create_export_list();
});

$("#check_all_first span,#check_all_first label").click(function(e){
    e.stopPropagation();
    var check=$("#check_all_first .checkbox input").attr("checked");
    if(check==undefined){
        $(".export_type_first_list li .checkbox input").attr("checked",true)
    }else{
        $(".export_type_first_list li .checkbox input").attr("checked",false)
    }
});
$("#export_type_second_list_other").on("click",".select_second_all span,.select_second_all label",function(e){
    e.stopPropagation();
    var check=$(this).parents(".select_second_all").find(".checkbox input").attr("checked");
    if(check==undefined){
        $(this).parents(".export_type_second_list").find("li .checkbox input").attr("checked",true)
    }else{
        $(this).parents(".export_type_second_list").find("li .checkbox input").attr("checked",false)
    }
});
$('#hide_export').click(function(){
    $('#export_list').hide();
    $.unblockUI({ fadeOut: 0 });
});
//$("#export_type_second_list_other").on("mouseleave",".export_type_second_list",function(){
//       $("#export_type_second_list_other").hide();
//});
$(".export_type_first_list .select_all").mouseenter(function(){
    $("#export_type_second_list_other").hide();
});
//
$(".export_type_first_list ul").niceScroll({
    cursorborder:"0 none",cursoropacitymin:"0",boxzoom:false,
    cursorcolor:" #dddddd",
    cursoropacitymax:1,
    touchbehavior:false,
    cursorminheight:50,
    autohidemode:false,
    cursorwidth:"5px",
    cursorborderradius:"1px"});

