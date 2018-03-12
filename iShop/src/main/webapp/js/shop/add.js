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
        "group_name":"",
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
    _param['corp_code']=$("#OWN_CORP").val();;
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
    _param['corp_code']=$("#OWN_CORP").val();
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
    _param['corp_code']=$("#OWN_CORP").val();
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
                        store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].id+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
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
    console.log(message.cache.brand_codes);
    console.log(message.cache.brand_names);
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
function getcorplist(){
    //获取企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var corp_html='';
            for(var i=0;i<msg.corps.length;i++){
                corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $("#OWN_CORP").searchableSelect();
            var corp_code=$("#OWN_CORP").val();
            $('.contion_input .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    var corp_code1=$("#OWN_CORP").val();
                    if(corp_code!==corp_code1){
                        message.cache.vip_id="";
                        message.cache.area_codes="";
                        message.cache.area_names="";
                        message.cache.brand_codes="";
                        message.cache.brand_names="";
                        message.cache.store_codes="";
                        message.cache.store_names="";
                        corp_code=corp_code1;
                        $("#screen_brand_num").val("");
                        $("#screen_area_num").val("");
                        $("#screen_store_num").val("");
                        $(".brand_num").val("全部");
                        $(".area_num").val("全部");
                        $(".s_pitch span").html("0");
                        $(".input_search input").val("");
                    }
                }
            })
            $('.contion_input .searchable-select-item').click(function(){
                var corp_code1=$("#OWN_CORP").val();
                if(corp_code!==corp_code1){
                    message.cache.area_codes="";
                    message.cache.area_names="";
                    message.cache.brand_codes="";
                    message.cache.brand_names="";
                    message.cache.store_codes="";
                    message.cache.store_names="";
                    message.cache.user_codes="";
                    message.cache.user_names="";
                    corp_code=corp_code1;
                    $("#screen_brand_num").val("");
                    $("#screen_area_num").val("");
                    $("#screen_store_num").val("");
                    $(".brand_num").val("全部");
                    $(".area_num").val("全部");
                    $(".s_pitch span").html("0");
                    $(".input_search input").val("");
                }
            })
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
}
$("#screen_queding").click(function(){
    if(message.cache.store_codes.length<=0){
        frame();
        $('.frame').html("请先选择店铺");
        return
    }
    var num=message.cache.store_codes.split(",").length;
    $("#code_ma2 p").html("确定开启"+num+"个爱秀店铺？");
    $("#screen_filtrate").hide();
    $("#code_ma2").show();
    $("#code_ma2").css("z-index","10003");
    var param={};
    param["id"]= message.cache.store_codes;
    param["type"]="start";
    // whir.loading.add("",0.5);//加载等待框
    // oc.postRequire("post","/shop/batchStartOrEnd","0",param,function(data){
    //     if(data.code=="0"){
    //         var return_jump={};//定义一个对象
    //         return_jump["inx"]=inx;//跳转到第几页
    //         return_jump["value"]=value;//搜索的值;
    //         return_jump["filtrate"]=filtrate;//筛选的值
    //         return_jump["param"]=JSON.stringify(param);//搜索定义的值
    //         return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
    //         return_jump["list"]=list;//筛选的请求的list;
    //         return_jump["pageSize"]=pageSize;//每页多少行
    //         sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
    //         $("#code_ma2").hide();
    //         whir.loading.remove();//移除加载框
    //         frame().then(function(){
    //             if(value==""&&filtrate==""){
    //                 GET(inx,pageSize);
    //             }else if(value!==""){
    //                 POST(inx,pageSize);
    //             }else if(filtrate!==""){
    //                 filtrates(inx,pageSize);
    //             }
    //         });
    //         $('.frame').html("开启成功");
    //         $("#screen_filtrate").hide();
    //         whir.loading.remove();//移除加载框
    //     }else if(data.code=="-1"){
    //         alert(data.message);
    //     }
    //     whir.loading.remove();//移除加载框
    // })
})
$("#enter2").click(function(){
    var param={};
    param["id"]= message.cache.store_codes;
    param["type"]="start";
    whir.loading.add("",0.5);//加载等待框
    var num=message.cache.store_codes.split(",").length;
    oc.postRequire("post","/shop/batchStartOrEnd","0",param,function(data){
        if(data.code=="0"){
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["filtrate"]=filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
            return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            $("#code_ma2").hide();
            whir.loading.remove();//移除加载框
            frame().then(function(){
                if(value==""&&filtrate==""){
                    GET(inx,pageSize);
                }else if(value!==""){
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    filtrates(inx,pageSize);
                }
            });
            $('.frame').html("成功开启了"+num+"个爱秀店铺");
            $("#screen_store_num").val("");
            message.cache.store_codes="";
        }else if(data.code=="-1"){
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    })
});
$("#empty_screen").click(function(){
    message.cache.area_codes="";
    message.cache.area_names="";
    message.cache.brand_codes="";
    message.cache.brand_names="";
    message.cache.store_codes="";
    message.cache.store_names="";
    message.cache.user_codes="";
    message.cache.user_names="";
    $("#screen_brand_num").val("");
    $("#screen_area_num").val("");
    $("#screen_store_num").val("");
    $(".brand_num").val("全部");
    $(".area_num").val("全部");
    $(".s_pitch span").html("0");
    $(".input_search input").val("");
})
getcorplist();
