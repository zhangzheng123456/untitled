/**
 * Created by 赵伟 on 2017/1/9.
 */
var oc=new ObjectControl();
var inx=1;//默认是第一页
var count=1;
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var _param={};//筛选定义的内容
var filtrate="";//筛选的定义的值
var area_num = 1;
var area_next = false;
var shop_num = 1;
var shop_next = false;
var staff_num = 1;
var staff_next = false;
var bd=[];//品牌
var ar=[];//区域
var sp=[];//店铺
var sf=[];//员工
var nowScreen=[];
var corp_code=$("#tabs").children().eq(0).attr("data-corp");
var  message={
    cache:{//缓存变量
        "group_codes":"",
        "brand_codes":"",
        "area_codes":"",
        "store_codes":""
    }
};
//获取拓展资料
function expend_data() {
    var param={"corp_code":corp_code};
    oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
        if(data.code == 0){
            $("#expend_attribute").empty();
            var msg = JSON.parse(data.message);
            var list = JSON.parse(msg.list);
            var html="";
            var simple_html="";
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    var param_type = list[i].param_type;
                    if(param_type=="date"){
                        simple_html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                            + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"><label class="jian">~</label>'
                            + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"></div>'
                        html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                            + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"><label class="jian">~</label>'
                            + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, format: \'YYYY-MM-DD\'})"></div>'
                    }
                    if(param_type=="select"){
                        var param_values = "";
                        param_values = list[i].param_values.split(",");
                        if(param_values.length>0){
                            var li="";
                            for(var j=0;j<param_values.length;j++){
                                li+='<li>'+param_values[j]+'</li>'
                            }
                            html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                + li
                                + '</ul></div>'
                        }else {
                            html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                        }
                    }
                    if(param_type=="text"){
                        html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                            + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                    }
                    if(param_type=="longtext"){
                        html+='<div class="textarea"><label>'+list[i].param_desc+'</label>'
                            + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                    }
                }
            }
            $("#expend_attribute").append(html);
            $("#memorial_day").append(simple_html);
        }else if(data.code == -1){
            console.log(data.message);
        }
    });
}
//员工里面的店铺点击
$("#staff_shop").click(function(){
    if(message.cache.store_codes!==""){
        var store_codes=message.cache.store_codes.split(',');
        var store_names=message.cache.store_names.split(',');
        var shop_html_right="";
        for(var h=0;h<store_codes.length;h++){
            shop_html_right+="<li id='"+store_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+store_names[h]+"</span>\
            \</li>"
        }
        $("#screen_shop .s_pitch span").html(h);
        $("#screen_shop .screen_content_r ul").html(shop_html_right);
    }else{
        $("#screen_shop .s_pitch span").html("0");
        $("#screen_shop .screen_content_r ul").empty();
    }
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
});
//员工里面的区域点击
$("#staff_area").click(function(){
    if(message.cache.area_codes!==""){
        var area_codes=message.cache.area_codes.split(',');
        var area_names=message.cache.area_names.split(',');
        var area_html_right="";
        for(var h=0;h<area_codes.length;h++){
            area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
        }
        $("#screen_area .s_pitch span").html(h);
        $("#screen_area .screen_content_r ul").html(area_html_right);
    }else{
        $("#screen_area .s_pitch span").html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
});
expend_data();
//   打开筛选界面
$("#select_vip").bind("click",function(){
    var arr = whir.loading.getPageSize();

    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_wrapper").show();
});
//关闭筛选界面
$('#screen_wrapper_close').on('click', function () {
    $('#screen_wrapper')[0].style.display = 'none';
});
//点击店铺的品牌
$("#screen_brandl").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_wrapper").hide();
    $("#screen_brand").show();
    $("#screen_brand").css({"left": +left + "px", "top": +tp + "px","position":"fixed"});
    $("#screen_brand .screen_content_l ul").empty();
    // $("#screen_brand .screen_content_r ul").empty();
    getbrandlist();
});
//点击筛选会员的所属区域
$("#screen_areal").click(function () {
    var arr = whir.loading.getPageSize();
    nodeSave($('#screen_area .screen_content_r ul li'),$('#screen_area .screen_content_l ul li'),ar, $('#screen_area .screen_content_r ul'));
    $('#screen_area .s_pitch span').html($('#screen_area .screen_content_r ul li').length);
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_area").css({"left": +left + "px", "top": +tp + "px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_area").show();
    var area_num = 1;
    //if ($("#screen_area .screen_content_l ul li").length > 1) {
    //    return;
    //}
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
//点击筛选会员的所属店铺
$("#screen_shopl").click(function () {
    nodeSave($('#screen_shop .screen_content_r ul li'),$('#screen_shop .screen_content_l ul li'),sp, $('#screen_shop .screen_content_r ul'));
    $('#screen_shop .s_pitch span').html($('#screen_shop .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_shop").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_wrapper").hide();
    $("#screen_shop").show();
    $("#screen_shop .screen_content_l ul").empty();
    var shop_num = 1;
    getstorelist(shop_num);
});
//点击筛选会员的所属员工
$("#screen_staffl").click(function () {
    nodeSave($('#screen_staff .screen_content_r ul li'),$('#screen_staff .screen_content_l ul li'),sf, $('#screen_staff .screen_content_r ul'));
    $('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_staff").css({"left": +left + "px", "top": +tp + "px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_staff").show();
    $("#screen_staff .screen_content_l ul").empty();
    var staff_num = 1;
    getstafflist(staff_num);
});
$("#activity_screen_group_icon").click(function(){
    if(message.cache.group_codes!==""){
        var group_codes=message.cache.group_codes.split(',');
        var group_names=message.cache.group_names.split(',');
        var group_html_right="";
        for(var h=0;h<group_codes.length;h++){
            group_html_right+="<li id='"+group_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+group_codes[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+group_names[h]+"</span>\
            \</li>"
        }
        $("#screen_group .s_pitch span").html(h);
        $("#screen_group .screen_content_r ul").html(group_html_right);
    }else{
        $("#screen_group .s_pitch span").html("0");
        $("#screen_group .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_group").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_group").show();
    getActivityGroup();
});
//节点状态保存
function nodeSave(node_r,node_l,arr,node_container) {
    if(node_r.length!=0){
        node_r.each(function () {
            arr.indexOf(this.id)==-1&&($(this).remove());
        })
    }else if(node_r.length==0&&(arr.length!=0)){
        node_l.each(function () {
            if(arr.indexOf(this.id)!=-1){
                node_container.append($(this).clone())
            }
        })
    }
}
//获取品牌列表
function getbrandlist() {
    var searchValue=$("#brand_search").val();
    var _param = {};
    _param['corp_code']=corp_code;
    _param["searchValue"]=searchValue;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/shop/brand", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.brands;
            var brand_html_left = '';
            var brand_html_right = '';
            if (list.length == 0) {
                for (var h = 0; h < 9; h++) {
                    brand_html_left += "<li></li>"
                }
            } else {
                if (list.length < 9) {
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left += "<li id='" + list[i].brand_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
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
                        brand_html_left += "<li id='" + list[i].brand_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
                            + i
                            + 1
                            + "'/><label for='checkboxThreeInput"
                            + i
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                    }
                }

                $("#screen_brand .screen_content_l ul").append(brand_html_left);
            }
            // bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//拉取区域
function getarealist(a) {
    var area_command = "/area/selAreaByCorpCode";
    var searchValue = $("#area_search").val().trim();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param["corp_code"] =corp_code;
    _param["searchValue"] = searchValue;
    _param["pageSize"] = pageSize;
    _param["pageNumber"] = pageNumber;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", area_command, "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var area_html_left = '';
            var area_html_right = '';
            if (list.length == 0) {
            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        area_html_left += "<li  id='" + list[i].area_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].area_code + "' data-areaname='" + list[i].area_name + "' name='test'  class='check'  id='checkboxOneInput"
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
            }
            if(hasNextPage==true){
                area_num++;
                area_next=false;
            }
            if(hasNextPage==false){
                area_next=true;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            // bianse();
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
function getstorelist(a) {
    var area_code = $('#screen_area_num').attr("data-code");//
    var brand_code = $('#screen_brand_num').attr("data-code");
    var searchValue = $("#store_search").val();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    var checknow_data = [];
    var checknow_namedata = [];
    _param['corp_code'] = corp_code;
    _param['area_code'] = area_code;
    _param['brand_code'] = brand_code;
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var store_html = '';
            if (list.length == 0) {
            } else {
                if (list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        store_html += "<li id='" + list[i].store_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput"
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
            }
            if(hasNextPage==true){
                shop_num++;
                shop_next = false;
            }
            if(hasNextPage==false){
                shop_next=true;
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            // bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//员工里面的品牌点击
$("#staff_brand").click(function(){
    if(message.cache.brand_codes!==""){
        var brand_codes=message.cache.brand_codes.split(',');
        var brand_names=message.cache.brand_names.split(',');
        var brand_html_right="";
        for(var h=0;h<brand_codes.length;h++){
            brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
        }
        $("#screen_brand .s_pitch span").html(h);
        $("#screen_brand .screen_content_r ul").html(brand_html_right);
    }else{
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
});
//店铺里面的区域点击
$("#shop_area").click(function(){
    if(message.cache.area_codes!==""){
        var area_codes=message.cache.area_codes.split(',');
        var area_names=message.cache.area_names.split(',');
        var area_html_right="";
        for(var h=0;h<area_codes.length;h++){
            area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
        }
        $("#screen_area .s_pitch span").html(h);
        $("#screen_area .screen_content_r ul").html(area_html_right);
    }else{
        $("#screen_area .s_pitch span").html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
});
//店铺里面的品牌点击
$("#shop_brand").click(function(){
    if(message.cache.brand_codes!==""){
        var brand_codes=message.cache.brand_codes.split(',');
        var brand_names=message.cache.brand_names.split(',');
        var brand_html_right="";
        for(var h=0;h<brand_codes.length;h++){
            brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
        }
        $("#screen_brand .s_pitch span").html(h);
        $("#screen_brand .screen_content_r ul").html(brand_html_right);
    }else{
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
});
//获取员工列表
function getstafflist(a) {
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code = $('#screen_area_num').attr("data-code");//区域
    var brand_code = $('#screen_brand_num').attr("data-code");//品牌
    var store_code = $("#screen_shop_num").attr("data-code");//员工
    var searchValue = $("#staff_search").val();//员工的value值
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param['corp_code'] =corp_code;
    _param['area_code'] = area_code;
    _param['brand_code'] = brand_code;
    _param['store_code'] = store_code;
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var staff_html = '';
            if (list.length == 0) {

            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li id='" + list[i].user_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].user_name + "</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                staff_num++;
                staff_next = false;
            }
            if(hasNextPage==false){
                staff_next=true;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            // bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取分组
function getActivityGroup() {
    var corp_command = "/vipGroup/getCorpGroups";
    var _param = {};
    _param["corp_code"] =corp_code;
    _param["search_value"] = $("#group_search_activity").val();
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#screen_group .screen_content_l ul").empty();
            $("#filter_group").attr("data-corp", corp_code);
            if (list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    html+="<li><div class='checkbox1'><input  data-type='"+list[i].group_type+"' type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + "'/><label for='checkboxOneInput"
                        + i
                        + "'></label></div><span class='p16'>"+list[i].vip_group_name+"</span></li>"
                }
                $("#screen_group .screen_content_l ul").append(html);
            } else if (list.length <= 0) {
                //art.dialog({
                //    time: 1,
                //    lock: true,
                //    cancel: false,
                //    zIndex:10002,
                //    content: data.message
                //});
            }
        }
    })
}
$("#activity_screen_que_group").click(function(){
    var li=$("#screen_group .screen_content_r input[type='checkbox']").parents("li");
    var group_codes="";
    var group_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            group_codes+=r+",";
            group_names+=p+",";
        }else{
            group_codes+=r;
            group_names+=p;
        }
    };
    message.cache.group_codes=group_codes;
    message.cache.group_names=group_names;
    $("#screen_group").hide();
    $("#screen_wrapper").show();
    $("#filter_group").val("已选"+li.length+"个");
    $("#filter_group").attr("data-code",group_codes);
    $("#filter_group").attr("data-code_name",group_names);
})
//点击品牌确定按钮
$("#screen_que_brand").click(function () {
    bd=[];
    var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes = "";
    var brand_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            brand_codes += r + ",";
            brand_name += p + ",";
        } else {
            brand_codes += r;
            brand_name += p;
        }
    }
    var num = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_brand .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        bd.push(this.id);
    });
    $("#brand_num").attr("data-brandcode", brand_codes);
    $("#brand_num").val("已选" + num + "个");
    // $("#screen_brand_num").val(brand_name);
    $("#screen_brand_num").val("已选" + num + "个");
    $("#screen_brand_num").attr("data-code", brand_codes);
    $("#screen_brand_num").attr("data-code_name", brand_name);
    $("#staff_brand_num").val("已选" + num + "个");
    $("#staff_brand_num").attr("data-brandcode", brand_codes);
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
});
//点击区域确定追加节点
$("#screen_que_area").click(function () {
    ar=[];
    var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes = "";
    var area_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            area_codes += r + ",";
            area_name += p + ",";
        } else {
            area_codes += r;
            area_name += p;
        }
    }
    var num = $("#screen_area .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_area .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        ar.push(this.id);
    });
    // $("#screen_area_num").val(area_name);
    $("#screen_area_num").val("已选" + num + "个");
    $("#screen_area_num").attr("data-code", area_codes);
    $("#screen_area_num").attr("data-code_name", area_name);
    $("#area_num").val("已选" + num + "个");
    $("#area_num").attr("data-areacode", area_codes);
    $("#staff_area_num").val("已选" + num + "个");
    $("#staff_area_num").attr("data-areacode", area_codes);
    $("#screen_area").hide();
    $("#screen_wrapper").show();
});
//点击店铺确定追加节点
$("#screen_que_shop").click(function () {
    sp=[];
    var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_name = "";
    var store_code = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            store_code += r + ",";
            store_name += p + ",";
        } else {
            store_code += r;
            store_name += p;
        }
    }
    var num = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_shop .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        sp.push(this.id);
    });
    // $("#screen_shop_num").val(store_name);
    $("#screen_shop_num").val("已选" + num + "个");
    $("#screen_shop_num").attr("data-code", store_code);
    $("#screen_shop_num").attr("data-code_name", store_name);
    $("#staff_shop_num").val("已选" + num + "个")
    $("#staff_shop_num").attr("data-storecode", store_code);
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
});
//点击员工确定追加节点
$("#screen_que_staff").click(function () {
    sf=[];
    var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var staff_codes = "";
    var staff_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            staff_codes += r + ",";
            staff_name += p + ",";
        } else {
            staff_codes += r;
            staff_name += p;
        }
    }
    var num = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_staff .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        sf.push(this.id);
    });
    $("#screen_stff_num").val("已选" + num + "个");
    $("#screen_stff_num").attr("data-code", staff_codes);
    $("#screen_stff_num").attr("data-code_name", staff_name);
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
});
//分组搜索
$("#group_search_activity").keydown(function () {
    var event = window.event || arguments[0];
    if (event.keyCode == 13) {
        getActivityGroup();
    }
});
$("#group_search_f_activity").click(function () {
    getActivityGroup();
});
//区域关闭
$("#screen_close_area").click(function () {
    $("#screen_area").hide();
    $("#screen_wrapper").show();
});
//店铺关闭
$("#screen_close_shop").click(function () {
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
});
//品牌关闭
$("#screen_close_brand").click(function () {
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
});
//员工关闭
$("#screen_close_staff").click(function () {
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
});

//区域滚动事件
$("#screen_area .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (area_next) {
            return;
        }
        getarealist(area_num);
    }
});
//店铺滚动事件
$("#screen_shop .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (shop_next) {
            return;
        }
        getstorelist(shop_num);
    }
});
//导购滚动事件
$("#screen_staff .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (staff_next) {
            return;
        }
        getstafflist(staff_num);
    }
});

//日期调用插件
var simple_birth_start1 = {
    elem: '#simple_birth_start1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: true,
    fixed: false,
    choose: function (datas) {
        simple_birth_end1.min = datas; //开始日选好后，重置结束日的最小日期
        simple_birth_end1.start = datas; //将结束日的初始值设定为开始日
    }
};
var simple_birth_end1 = {
    elem: '#simple_birth_end1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: true,
    fixed: false,
    choose: function (datas) {
        simple_birth_start1.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var start1 = {
    elem: '#birth_start1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: true,
    fixed: false,
    choose: function (datas) {
        end1.min = datas; //开始日选好后，重置结束日的最小日期
        end1.start = datas; //将结束日的初始值设定为开始日
    }
};
var end1 = {
    elem: '#birth_end1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: true,
    fixed: false,
    choose: function (datas) {
        start1.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var activity_start1 = {
    elem: '#activate_card_start1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59', //最大日期
    istoday: true,
    fixed: false,
    choose: function (datas) {
        activity_end1.min = datas; //开始日选好后，重置结束日的最小日期
        activity_end1.start = datas; //将结束日的初始值设定为开始日
    }
};
var activity_end1 = {
    elem: '#activate_card_end1',
    format: 'YYYY-MM-DD',
    istime: false,
    max: '2099-06-16 23:59:59',
    istoday: true,
    fixed: false,
    choose: function (datas) {
        activity_start1.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
$("#simple_birth_start1").bind("click",function(){
    laydate(simple_birth_start1)
});
$("#simple_birth_end1").bind("click",function(){
    laydate(simple_birth_end1)
});
$("#birth_end1").bind("click",function(){
    laydate(end1)
});
$("#birth_start1").bind("click",function(){
    laydate(start1)
});
$("#activate_card_start1").bind("click",function() {
    laydate(activity_start1);
});
$("#activate_card_end1").bind("click",function(){
    laydate(activity_end1)
});

$("#screen_vip_que_activity").bind("click",function(){  //筛选确定
    inx = 1;
    _param["corp_code"] = corp_code;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    var screen = [];
    if ($("#simple_filter").css("display") == "block") {
        $("#simple_contion .contion_input").each(function () {
            var input = $(this).find("input");
            var key = $(input[0]).attr("data-kye");
            var classname = $(input[0]).attr("class");
            var expend_key = $(input[0]).attr("data-expend");
            if(key == "17"){
                return ;
            }else if (key == "4") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    var date = $("#consume_date_basic_4").attr("data-date");
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    param['date'] = date;
                    screen.push(param);
                }
            }else if (key == "3") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    var date = $("#consume_date_basic_3").attr("data-date");
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    param['date'] = date;
                    screen.push(param);
                }
            }else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                    var param = {};
                    var val = {};
                    val['start'] = $(input[0]).val();
                    val['end'] = $(input[1]).val();
                    param['type'] = "json";
                    param['key'] = key;
                    param['value'] = val;
                    screen.push(param);
                }
            }else if(key == "6" && $(input[0]).val() !== "全部"){
                var param = {};
                var val = $(input[0]).val();
                val == "已冻结"? val="Y":val="N";
                param['key'] = key;
                param['value'] = val;
                param['type'] = "text";
                screen.push(param);
            }else {
                if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                    var param = {};
                    var val = $(input[0]).val();
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }
            }
        });
    } else {
        $("#contion>div").each(function () {
            $(this).find(".contion_input").each(function (i, e) {
                var input = $(e).find("input");
                var key = $(input[0]).attr("data-kye");
                var expend_key = $(input[0]).attr("data-expend");
                var classname = $(input[0]).attr("class");
                if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        screen.push(param);
                    }
                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15"|| key == "16") {
                    if ($(input[0]).attr("data-code") !== "") {
                        var param = {};
                        var val = $(input[0]).attr("data-code");
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                } else if (key == "17") {
                    return;
                } else if (key == "3" || key == "4") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        var date = $("#consume_date").attr("data-date");
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        param['date'] = date;
                        screen.push(param);
                    }
                }else if(key == "6" && $(input[0]).val() !== "全部" ){
                    var param = {};
                    var val = $(input[0]).val();
                    val == "已冻结"?val="Y":val="N";
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }else {
                    if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                        var param = {};
                        var val = $(input[0]).val();
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                }
            });
            $(this).find("textarea").each(function () {
                var key = $(this).attr("data-kye");
                var param = {};
                var val = $(this).val();
                if(val !== ""){
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }
            });
        });
    }
    _param['screen'] = screen;
    nowScreen=screen;
    if (screen.length == 0) {
        $("#vip_list ul").html(" ");
        $("#num").html("0");
        setPage($("#foot-num")[0],1,1,pageSize);
        _param["screen"]=[];
        var Array=[];
        whir.loading.add("",0.5);
        superaddition(Array);
    }else {
        filtrate = "sucess";
        filtrates(inx, pageSize);
    }
    $("#search").val("");
    value="";
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    var param={};
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]=corp_code;
    oc.postRequire("post","/vipAnalysis/allVip","",param,function(data){
        if(data.code=="0"){
            $("#vip_list ul").empty();
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            count=messages.pages;
            var pageNum = messages.pageNum;
            //var list=list.list;
            $("#num").html(messages.count);
            superaddition(list,pageNum);
//                    jumpBianse();
            filtrate="";
            $('.contion .input').val("");
            setPage($("#foot-num")[0],count,pageNum,b);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
//筛选调接口
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipScreen","", _param, function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            count=message.pages;
            $("#num").html(message.count);
            $("#vip_list ul").empty();
            if(list.length<=0){
                $("#vip_list ul p").remove();
                $("#vip_list ul").append("<p>没有符合筛选条件的会员。</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $("#vip_list ul p").remove();
                superaddition(list,a);
//                    jumpBianse();
            }
            setPage($("#foot-num")[0],count,a,b);
        }else if(data.code=="-1"){
            whir.loading.remove();//移除加载框
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                content: "筛选失败"
            });
        }
    })
}
function superaddition(data,num){//页面加载循环
    $(".table p").remove();
            if(data.length == 0){
                $("#vip_list ul p").remove();
                $("#vip_list ul").append("<p>暂无会员</p>");
            }
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        //性别
        if(data[i].sex=="F"){
            data[i].sex="女"
        }else if(data[i].sex=="M"){
            data[i].sex="男"
        }
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        if(data[i].open_id){
            wx="<td><span class='icon-ishop_6-22'style='color:#8ec750'></span></td>";
        }else{
            wx="<td><span class='icon-ishop_6-22'style='color:#cdcdcd'></span></td>";
        }
        $("#vip_list ul").append(
            "<li>"
            +"<span>"+data[i].vip_name+"</span>"
            +"<span>"+data[i].vip_phone+"</span>"
            +"<span>"+data[i].sex+"</span>"
            +"<span>"+data[i].vip_card_type+"</span>"
            +"</li>"
        );
    }
    var allList=$("#vip_list li");
    for(var i=0;i<allList.length;i++){
        i%2=="0"?$(allList[i]).css("backgroundColor","#fff"):$(allList[i]).css("backgroundColor","#efefef");
    }
    whir.loading.remove();//移除加载框
}
function setPage(container, count, pageindex,pageSize){
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
    var a = [];
    //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }
    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
        } else {
            a[a.length] = "<li><span>" + i + "</span></li>";
        }
    }
    //总页数小于10
    if (count <= 10) {
        for (var i = 1; i <= count; i++) {
            setPageList();
        }
    }
    //总页数大于10页
    else {
        if (pageindex <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        }
        else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    }else{
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx,pageSize);
            return false;
        };
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            return false;
        }
    }()
}
function dian(a,b){//点击分页的时候调什么接口
    if(_param["screen"].length=="0"){
        return;
    }
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
}
$("#page_row").click(function(){
    if("block" == $("#liebiao").css("display")){
        hideLi();
    }else{
        showLi();
    }
});
$("#liebiao li").each(function(i,v){
    $(this).click(function(){
        pageSize=$(this).attr('id');
        if(_param["screen"].length=="0"){
        }else {
            inx=1;
            _param["pageNumber"]=inx;
            _param["pageSize"]=pageSize;
            filtrates(inx,pageSize);
        }
        $("#page_row").val($(this).html());
        hideLi();
    });
});
$("#page_row").blur(function(){
    setTimeout(hideLi,200);
});
function showLi(){
    $("#liebiao").show();
}
function hideLi(){
    $("#liebiao").hide();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    inx=parseInt(inx);
    if (inx > count) {
        inx = count
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            if(_param["screen"].length=="0"){
                return;
            }else {
                _param["pageSize"] = pageSize;
                _param["pageNumber"]=inx;
                filtrates(inx, pageSize);
            }
        }
    }
});
function getData(){
    var param={
        activity_code:sessionStorage.getItem("activity_code")
    };
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vipActivity/select","0",param,function(data){
        if(data.code!="0") return;
        whir.loading.remove();//移除加载框
        var msg=JSON.parse(data.message);
        var activityVip=JSON.parse(msg.activityVip);
        var num=activityVip.target_vips_count=="" ? "0" : activityVip.target_vips_count;
        $("#num").html(num);
        _param["corp_code"] = corp_code;
        _param["pageNumber"] = inx;
        _param["pageSize"] = pageSize;
        _param["screen"]=activityVip.target_vips =="" ?[]:JSON.parse(activityVip.target_vips);
        if(_param["screen"].length>0){
            filtrates(1,10);
        }else if(_param["screen"].length==0){
            whir.loading.add("",0.5);//加载等待框
            setPage($("#foot-num")[0],1,1,pageSize);
            superaddition(_param["screen"])
        }

    })
}
getData();
function postSelect(){
    var def = $.Deferred();
    if($("#num").html()=="0"){
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "会员数量不能为0"
        });
        def.resolve("失败");
    }
    var param={
        target_vips_count:$("#num").html(),
        activity_vip_code:sessionStorage.getItem("activity_code"),
        screen:_param["screen"]
    };

    oc.postRequire("post","/vipActivity/arrange/addOrUpdateVip","0",param,function(data){
        if(data.code!="0") return;
        def.resolve("成功");
    });
    return def
}