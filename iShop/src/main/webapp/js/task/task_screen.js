/**
 * Created by lishun on 20170519/5/9.
 */
var oc = new ObjectControl();
var  message={
    cache:{//缓存变量
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "user_codes":"",
        "user_names":"",
        "group_codes":"",
        "group_names":"",
        "type":"",
        "corp_code":""
    }
};
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var store_num=1;
var store_next=false;
var isscroll=false;
//点击右移
$(".shift_right").click(function () {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function () {
    var right = "all";
    var div = $(this);
    removeRight(right, div);
});
//点击左移
$(".shift_left").click(function () {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function () {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
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
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox'][data-type!='define']").parents("li:visible");
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    // $("#screen_staff .screen_content_l li:odd").css("backgroundColor", "#fff");
    // $("#screen_staff .screen_content_l li:even").css("backgroundColor", "#ededed");
    // $("#screen_staff .screen_content_r li:odd").css("backgroundColor", "#fff");
    // $("#screen_staff .screen_content_r li:even").css("backgroundColor", "#ededed");
}
//移到左边
function removeLeft(a, b) {
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//获取区域
function getarealist(a){
    var area_command = "/area/selAreaByCorpCode";
    var searchValue=$("#area_search").val().trim();
    var pageSize=20;
    var pageNumber=a;
    var _param = {};
    _param['corp_code']=GetRequest().corp_code;
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
//拉取品牌
function getbrandlist(){
    var searchValue=$("#brand_search").val().trim();
    var _param={};
    _param['corp_code']=GetRequest().corp_code;
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

            } else {
                if(list.length>=0){
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
//拉取店铺
function getstorelist(a){
    var searchValue=$("#store_search").val().trim();
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param['corp_code']=GetRequest().corp_code;
    _param['area_code']=message.cache.area_codes;
    _param['brand_code']=message.cache.brand_codes;
    _param['searchValue']=searchValue;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    _param["find_type"]="user";
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
///获取员工列表
function getstafflist(a){
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    var searchValue=$('#staff_search').val().trim();
    _param['area_code']=message.cache.area_codes;
    _param['brand_code']=message.cache.brand_codes;
    _param['store_code']=message.cache.store_codes;
    _param['searchValue']=searchValue;
    _param['corp_code']=GetRequest().corp_code;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/user/selectUsersByRoleAndGroup","", _param, function(data) {
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
            console.log(staff_html);
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
                    }
                })
            }
            if(!isscroll){
                $("#choose_staff .select_list").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    }
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
$("#filtrate").click(function(){
    whir.loading.add("mask",0.1);//加载等待框
    $("#screen_filtrate").show();
})
//品牌点击
$("#brand_add").click(function(){
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
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
});
//店铺群组点击
$("#area_add").click(function(){
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
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
});
//店铺点击
$("#shop_add").click(function(){
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
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").show();
    getstorelist(shop_num);
});
//所属导购点击
$("#staff_add").click(function(){
    $("#staff_search").val("");
    if(message.cache.user_codes!==""){
        var user_codes=message.cache.user_codes.split(',');
        var user_names=message.cache.user_names.split(',');
        var user_html_right="";
        for(var h=0;h<user_codes.length;h++){
            user_html_right+="<li id='"+user_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+user_codes[h]+"'  data-storename='"+user_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+user_names[h]+"</span>\
            \</li>"
        }
        $("#screen_staff .s_pitch span").html(h);
        $("#screen_staff .screen_content_r ul").html(user_html_right);
    }else{
        $("#screen_staff .s_pitch span").html("0");
        $("#screen_staff .screen_content_r ul").empty();
    }
    staff_num=1;
    isscroll=false;
    $("#screen_staff").show();
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
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
});
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
$("#group_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        var value=$(this).val();
        console.log(value);
        $("#screen_group .screen_content_l li").hide();
        $("#screen_group .screen_content_l li .p16:contains('"+value+"')").parent().show();
        if(value==""){
            $("#screen_group .screen_content_l li").show();
        }
        $("#screen_group .screen_content_l li:visible:odd").css("backgroundColor","#fff");
        $("#screen_group .screen_content_l li:visible:even").css("backgroundColor","#ededed");
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
});
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
//员工放大镜搜索
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
//品牌放大镜收索
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    if(message.clickMark=="user"){
        $("#screen_staff").show();
    }else {
        $("#screen_wrapper").show();
    }
});
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
});
//店铺关闭
$("#screen_close_shop").click(function(){
    $("#screen_shop").hide();
    if(message.clickMark=="user"){
        $("#screen_staff").show();
    }else {
        $("#screen_wrapper").show();
    }
});
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    if(message.clickMark=="user"){
        $("#screen_staff").show();
    }else {
        $("#screen_wrapper").show();
    }
});
//群组关闭
$("#screen_close_group").click(function(){
    $("#screen_group").hide();
})
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
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
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
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
});
//员工里面的区域点击
$("#staff_area").click(function(){
    message.clickMark="user";
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
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
});
//员工里面的店铺点击
$("#staff_shop").click(function(){
    message.clickMark="user";
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
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
});
//员工里面的品牌点击
$("#staff_brand").click(function(){
    message.clickMark="user";
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
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
});
//点击区域确定按钮
$("#screen_que_area").click(function(){
    var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes="";
    var area_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            area_codes+=r+",";
            area_names+=p+",";
        }else{
            area_codes+=r;
            area_names+=p;
        }
    };
    message.cache.area_codes=area_codes;
    message.cache.area_names=area_names;
    $("#screen_area").hide();
    $("#screen_area_num").val("已选"+li.length+"个");
    $("#screen_area_num").attr("data-code",area_codes);
    $("#screen_area_num").attr("data-name",area_names);
    $(".area_num").val("已选"+li.length+"个");
});
//点击品牌确定按钮
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes="";
    var brand_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            brand_codes+=r+",";
            brand_names+=p+",";
        }else{
            brand_codes+=r;
            brand_names+=p;
        }
    };
    message.cache.brand_codes=brand_codes;
    message.cache.brand_names=brand_names;
    $("#screen_brand").hide();
    $("#screen_brand_num").val("已选"+li.length+"个");
    $("#screen_brand_num").attr("data-code",brand_codes);
    $("#screen_brand_num").attr("data-name",brand_names);
    $(".brand_num").val("已选"+li.length+"个");
});
$("#screen_close").click(function(){
    $("#screen_filtrate").hide();
    whir.loading.remove();//移除加载框
})
//点击店铺确定按钮
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_codes="";
    var store_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            store_codes+=r+",";
            store_names+=p+",";
        }else{
            store_codes+=r;
            store_names+=p;
        }
    };
    message.cache.store_codes=store_codes;
    message.cache.store_names=store_names;
    $("#screen_shop").hide();
    $("#screen_store_num").val("已选"+li.length+"个");
    $("#screen_store_num").attr("data-code",store_codes);
    $("#screen_store_num").attr("data-name",store_names);
    $("#staff_shop_num").val("已选"+li.length+"个");
});
//点击导购的确定按钮
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var user_codes="";
    var user_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            user_codes+=r+",";
            user_names+=p+",";
        }else{
            user_codes+=r;
            user_names+=p;
        }
    };
    message.cache.user_codes=user_codes;
    message.cache.user_names=user_names;
    $("#screen_staff").hide();
    $("#screen_staff_num").val("已选"+li.length+"个");
    $("#screen_staff_num").attr("data-code",user_codes);
    $("#screen_staff_num").attr("data-name",user_names);
})
$("#screen_queding").click(function(){
    pageNumber=1;
    POST(pageNumber,pageSize);
    $("#screen_filtrate").hide();
    whir.loading.remove();//移除加载框
})
$("#empty_screen").click(function(){
    message.cache.area_codes="";
    message.cache.area_names="";
    message.cache.brand_codes="";
    message.cache.brand_names="";
    message.cache.store_codes="";
    message.cache.store_names="";
    message.cache.user_codes="";
    message.cache.user_names="";
    message.cache.group_codes="";
    message.cache.group_names="";
    $("#screen_brand_num").val("");
    $("#screen_area_num").val("");
    $("#screen_store_num").val("");
    $("#screen_group_num").val("");
    $("#screen_staff_num").val("");
    $("#screen_brand_num").attr("data-code","");
    $("#screen_area_num").attr("data-code","");
    $("#screen_store_num").attr("data-code","");
    $("#screen_staff_num").attr("data-code","");
    $(".brand_num").val("全部");
    $(".area_num").val("全部");
    $(".s_pitch span").html("0");
    $(".input_search input").val("");
})
