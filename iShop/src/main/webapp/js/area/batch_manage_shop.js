var oc = new ObjectControl();
var inx = 1;//默认是第一页
var pageSize = "10";
var cout = "";
var value = "";//收索的关键词
var param = {};//定义的对象
var area_code = "";//区域编号
var area_name = "";//区域名称
var corp_code = ""//企业编号
var store_num=1;
var isscroll=false;
var store_next=false;
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
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
function setPage(container, count, pageindex, pageSize) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize = pageSize;
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
        } else if (pageindex >= count - 3) {
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
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function () {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function () { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx, pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(a, b) {//点击分页的时候调什么接口
    if (value == "") {
        GET(a, b);
    } else if (value !== "") {
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a, b);
    }
}
//模仿select
$(function () {
        $("#page_row").click(function () {
            if ("block" == $("#liebiao").css("display")) {
                hideLi();
            } else {
                showLi();
            }
        });
        $("#liebiao li").each(function (i, v) {
            $(this).click(function () {
                pageSize = $(this).attr('id');
                if (value == "") {
                    inx = 1;
                    GET(inx, pageSize);
                } else if (value !== "") {
                    inx = 1;
                    param["pageSize"] = pageSize;
                    param["pageNumber"] = inx;
                    POST(inx, pageSize);
                }
                $("#page_row").val($(this).html());
                hideLi();
            });
        });
        $("#page_row").blur(function () {
            setTimeout(hideLi, 200);
        });
    }
);
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    };
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "") {
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                POST(inx, pageSize);
            }
        };
    }
})
function showLi() {
    $("#liebiao").show();
}
function hideLi() {
    $("#liebiao").hide();
}
$("#edit_shop_icon").click(function () {
    area_code = $('#AREA_ID').val();
    area_name = $('#AREA_NAME').val();
    $('#area_code').val(area_code);
    $('#areaName').val(area_name);
    $("#page-wrapper").hide();
    $(".content").show();
    $("#search").val("");
    GET(inx,pageSize);
});
$('#turnoff').click(function () {
    $("#page-wrapper").show();
    $(".content").hide();
});
$('#back_area_edit').click(function () {
    $("#page-wrapper").show();
    $(".content").hide();
});
$("#back_area_1").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/area/area.html");
});
//加载完成以后页面进行的操作
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    })
}
function superaddition(data, num) {
    $(".table p").remove();
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>")
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>")
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }


    for (var i = 0; i < data.length; i++) {
        if (num >= 2) {
            var a = i+1 + (num-1) * pageSize;
        } else {
            var a = i + 1;
        }
        console.log(num);
        $(".table tbody").append("<tr id="+data[i].id+" data-store_code='"+data[i].store_code+"' data-store_name='"+data[i].store_name+"' ><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td>"
            + "<td>"
            + a
            + "</td><td>"
            + data[i].store_code
            + "</td><td>"
            + data[i].store_name
            + "</td><td><span title='"+data[i].area_name+"'>"
            + data[i].area_name
            + "</span></td><td><span title='"+data[i].brand_name+"'>"
            + data[i].brand_name
            + "</span></td></tr>");
    }
    var thinput=$("thead input")[0];
    thinput.checked=false;
};
$(".table tbody").on("click","tr",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
        $(this).addClass("tr");
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
            thinput.checked=false;
        }
        input.checked = false;
        $(this).removeClass("tr");
    }
});
//页面加载时list请求
function GET(a, b) {
    whir.loading.add("",0.5);
    corp_code = $("#OWN_CORP").val();
    var searchAreaCode=$('#area_code').val();
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["area_code"] = area_code;
    param["corp_code"] = corp_code;
    param["searchValue"] =$("#search").val().trim();
    param["searchAreaCode"]=searchAreaCode;
    oc.postRequire("post", "/area/stores/check", "0", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            cout = list.pages;
            var is_this_area = list.is_this_area;
            list = list.list;
            console.log(list);
            var actions = message.actions;
            superaddition(list, inx);
            // jurisdiction(actions);
            jumpBianse();
            setPage($("#foot-num")[0], cout, a, b);
        } else if (data.code == "-1") {
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    });
}
//鼠标按下时触发的收索
$("#search").keydown(function () {
    param={};
    inx=1;
    var searchAreaCode=$('#area_code').val();
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    param["searchValue"] = value;
    param["searchAreaCode"]=searchAreaCode;
    param["pageNumber"]=inx;
    if (event.keyCode == 13) {
        //显示样式
        $('.r_filrate').next().html('显示当前区域店铺');
        $('.r_filrate').attr('style','color:#fff');
        var input=$(".inputs input");
        for(var i=0;i<input.length;i++){
            input[i].value="";
            $(input[i]).attr("data-code","");
        }
        $(".sxk").slideUp();
        POST(inx,pageSize);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function () {
    //显示样式
    $('.r_filrate').next().html('显示当前区域店铺');
    $('.r_filrate').attr('style','color:#fff');
    param={};
    var input=$(".inputs input");
    var searchAreaCode=$('#area_code').val();
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $(".sxk").slideUp();
    value = $("#search").val().replace(/\s+/g, "");
    param["searchValue"] = value;
    param["searchAreaCode"]=searchAreaCode;
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    POST(inx,pageSize);
})
//点击搜索按钮
$('.r_filrate').click(function () {
    param={};
    inx=1;
    var searchAreaCode='';
    //清空搜索内容
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $(".sxk").slideUp();
    $("#search").val('');
    if($(this).next().text().trim()=='显示当前区域店铺'){
        $(this).next().html('显示全部店铺');
        $(this).attr('style','color:#50a3aa');
        value ='';
        searchAreaCode=$('#area_code').val();
    }else{
        $(this).next().html('显示当前区域店铺');
        $(this).attr('style','color:#fff');
        value ='';
    }
    param['searchAreaCode']=searchAreaCode;
    param["searchValue"] = value;
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    POST(inx,pageSize);
});
//点击搜索按钮提示与否
$('.r_filrate').hover(function () {
    $(this).next().show();
},function () {
    $(this).next().hide();
});
$("#filtrate").click(function(){//点击筛选框弹出下拉框
    $(".sxk").slideToggle();
})
$("#pack_up").click(function(){//点击收回 取消下拉框
    $(".sxk").slideUp();
})
//点击清空  清空input的value值
$("#empty").click(function(){
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $('#find').trigger('click');
});
$(".inputs input").keydown(function () {
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    param["searchValue"] = value;
    if (event.keyCode == 13) {
        $('#find').trigger('click');
    }
});
$('#find').click(function () {
    //显示样式
    $('.r_filrate').next().html('显示当前区域店铺');
    $('.r_filrate').attr('style','color:#fff');
    $("#search").val('');
    param={};
    value=''
    var input=$(".inputs input");
        inx=1;
      var  list=[];//定义一个list
    console.log(list);
        for(var i=0;i<input.length;i++){
            var screen_key=$(input[i]).attr("id");
            var screen_value=$(input[i]).val().trim();
            var param1={"screen_key":screen_key,"screen_value":screen_value};
            list.push(param1);
        }
    param["list"]=list;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    POST(inx,pageSize);
});
//搜索的请求函数
function POST(a,b) {
    whir.loading.add("",0.5);
    corp_code = $("#OWN_CORP").val();
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["area_code"] = area_code;
    param["corp_code"] = corp_code;
    param["searchValue"] = value;
    oc.postRequire("post", "/area/stores/check", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            cout = list.pages;
            list = list.list;
            console.log(list);
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                if(param.searchAreaCode!==""){
                    $(".table").append("<p>当前区域下无店铺</p>");
                }else if(param.searchAreaCode==""){
                    $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                }
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, inx);
                jumpBianse();
            }
            setPage($("#foot-num")[0], cout, a, b);
        } else if (data.code == "-1") {
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    })
}
//弹框
function frame() {
    var left = ($(window).width() - $("#frame").width()) / 2;//弹框定位的left值
    var tp = ($(window).height() - $("#frame").height()) / 2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:' + left + 'px;top:' + tp + 'px;"></div>');
    $(".frame").animate({opacity: "1"}, 1000);
    $(".frame").animate({opacity: "0"}, 1000);
}
//全选
function checkAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        console.log(el[i].name);
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = true;
        }
    }
};
//取消全选
function clearAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = false;
        }
    }
};
$("#add").click(function(){
    var corp_code = $('#OWN_CORP').val();
    var corp_code1 = $('#OWN_CORP').attr("corp_code");
    $("#screen_store").show();
    // $("#screen_store .screen_content_r ul").empty();
    // $("#screen_store .screen_content_l ul input").removeAttr("checked");
    // $(".s_pitch span").html(0);
    whir.loading.add("mask",0.5);//加载等待框
    if (corp_code == corp_code1) {
        return;
    }
    $(".xuanzhong_input").val("");
    $('#OWN_CORP').attr("corp_code",corp_code);
    $("#screen_store .screen_content_l").unbind("scroll");
    $("#screen_store .screen_content_l ul").empty();
    $("#screen_store .screen_content_r ul").empty();
    store_num=1;
    getStoreList(store_num);
    getTopList();
})
$("#screen_close_store").click(function(){
    $("#screen_store").hide();
    whir.loading.remove('mask');
});
//拉取店铺上面的三个下拉框
function getTopList() {
    var corp_code=$("#OWN_CORP").val();
    var param={};
    param["corp_code"]=corp_code;
    oc.postRequire("post", "/shop/getStoreByOdsType", "0", param, function (data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var dealers=JSON.parse(message.dealers);//所属经销商
            var offline_areas=JSON.parse(message.offline_areas);//所属区域
            var store_types=JSON.parse(message.store_types);//店铺类型
            var dealers_html="<li>全部</li>";
            var offline_areas_html="<li>全部</li>";
            var store_types_html="<li>全部</li>";
            for(var i=0;i<dealers.length;i++){
                dealers_html+="<li title='"+dealers[i].dealer+"'>"+dealers[i].dealer+"</li>";
            }
            for(var j=0;j<offline_areas.length;j++){
                offline_areas_html+="<li title='"+offline_areas[j].offline_area+"'>"+offline_areas[j].offline_area+"</li>";
            }
            for(var k=0;k<store_types.length;k++){
                store_types_html+="<li title='"+store_types[k].store_type+"'>"+store_types[k].store_type+"</li>";
            }
            $("#dealer_parent ul").html(dealers_html);
            $("#offline_area_parent ul").html(offline_areas_html);
            $("#store_type_parent ul").html(store_types_html);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
$("#screen_store .xuanzhong .xuanzhong_p .xuanzhong_input").click(function(){
    var div = $(this).parent(".xuanzhong_p").find("div");
    var divs=$(this).parent(".xuanzhong_p").siblings().find("div");
    divs.hide();
    if(div.css("display")=="none"){
        div.show();
    }else{
        div.hide();
    }
})
$(document).click(function (e) {
    if ($(e.target).is('.xuanzhong_p') || $(e.target).is('.xuanzhong_input') || $(e.target).is('.xuanzhong_input') || $(e.target).is('.search') || $(e.target).is('.xuanzhong_p div') || $(e.target).is('.xuanzhong_p ul') || $(e.target).is('.xuanzhong_p li')) {
        return;
    } else {
        $(".xuanzhong_p").find("div").hide();
    }
});
// $("#screen_store .xuanzhong .xuanzhong_p .xuanzhong_input").blur(function(){
//     var div = $(this).parent(".xuanzhong_p").find("div");
//     setTimeout(function(){
//         div.hide();
//     },200);
// })
$("#screen_store .xuanzhong .xuanzhong_p").on("click","li",function(){
    var text=$(this).text();
    $(this).parents(".xuanzhong_p").find(".xuanzhong_input").val(text);
    $(this).parents(".xuanzhong_p").find("div").hide();
})
$(".search").keyup(function(){
    var value=$(this).val();
    $(this).parent().find("li").hide();
    $(this).parent().find("li:contains('"+value+"')").show();
})
//店铺搜索
$("#store_search").keydown(function(){
    var event=window.event||arguments[0];
    store_num=1;
    if(event.keyCode==13){
        $("#store_search").blur();
        isscroll=false;
        $("#screen_store .screen_content_l").unbind("scroll");
        $("#screen_store .screen_content_l ul").empty();
        getStoreList(store_num);
    }
})
$("#store_search_f").click(function(){
    store_num=1;
    isscroll=false;
    $("#screen_store .screen_content_l").unbind("scroll");
    $("#screen_store .screen_content_l ul").empty();
    getStoreList(store_num);
})
$("#r_search").click(function(){
    store_num=1;
    isscroll=false;
    $("#screen_store .screen_content_l").unbind("scroll");
    $("#screen_store .screen_content_l ul").empty();
    $("#store_search").val("");
    getStoreList(store_num);
})
function getStoreList(a){
    whir.loading.add("",0.5);
    $("#mask").css("z-index","10002");
    var corp_code = $("#OWN_CORP").val();
    var param={};
    param["pageNumber"] = a;
    param["pageSize"] = "10";
    param["area_code"] = area_code;
    param["corp_code"] = corp_code;
    param["searchValue"] = $("#store_search").val().trim();
    param["dealer"]=$("#dealer").val().trim()=='全部'?"":$("#dealer").val().trim();
    param["offline_area"]=$("#offline_area").val().trim()=='全部'?"":$("#offline_area").val().trim();
    param["store_type"]=$("#store_type").val().trim()=='全部'?"":$("#store_type").val().trim();
    oc.postRequire("post", "/area/stores/check", "0", param, function (data) {
        if(data.code=="0"){
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var store_html_left="";
            var cout = list.pages;
            var list = list.list;
            var actions = message.actions;
            for (var i = 0; i < list.length; i++) {
                store_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].id+"' data-areaname='"+list[i].id+"' name='test'  class='check'  id='checkboxOneInput"
                    + i
                    + a
                    + 1
                    + "'/><label for='checkboxOneInput"
                    + i
                    + a
                    + 1
                    + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
            }
            if(hasNextPage==true){
                store_num++;
                store_next=false;
            }
            if(hasNextPage==false){
                store_next=true;
            }
            $("#screen_store .screen_content_l ul").append(store_html_left);
            if(!isscroll){
                 $("#screen_store .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(store_next){
                            return;
                        }
                        console.log(123123);
                        getStoreList(store_num);
                    };
                })
            }
            isscroll=true;
        }else if(data.code=="-1"){
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    })
}
$("#del").click(function(){
    var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length==0){
        frame();
        $('.frame').html("请先选择");
        return;
    }
    whir.loading.add("mask",0.5);//加载等待框
    $("#tk").show();
})
//弹框关闭
$("#X").click(function(){
    whir.loading.remove('mask');
    $("#tk").hide();
})
//取消关闭
$("#cancel").click(function(){
    whir.loading.remove('mask');
    $("#tk").hide();
})
//弹框删除关闭
$("#delete").click(function(){
     whir.loading.remove('mask');
    $("#tk").hide();
    save("delete");
})
$("#screen_que_store").click(function(){
    $("#screen_store").hide();
    whir.loading.remove('mask');
    var li=$("#screen_store .screen_content_r input[type='checkbox']").parents("li");
    if(li.length<="0"){
        return;
    }
    save("add");
})
function save(type){
    var area_code=$('#AREA_ID').val();
    var param={};
    if(type=="add"){
        var li=$("#screen_store .screen_content_r input[type='checkbox']").parents("li");
        for(var i=0,ID="";i<li.length;i++){
            var r=$(li[i]).attr("id");
            if(i<li.length-1){
                ID+=r+",";
            }else{
                ID+=r;
            }
        }
        param["choose"] =ID;
        param["quit"]="";
    }
    if(type=="delete"){
        var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
        for(var j=0,dId="";j<tr.length;j++){
            var id=$(tr[j]).attr("id");
            if(j<tr.length-1){
                dId+=id+",";
            }else{
                dId+=id;
            }
        }
        param["choose"] ="";
        param["quit"]=dId;
    }
    param["area_code"]=area_code;
    oc.postRequire("post", "/area/stores/save", "0", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            $("#search").val("");
            inx=1;
            value="";
            POST(inx,pageSize);
            var id=sessionStorage.getItem("id");
            var _params={};
            _params["id"]=id;
            oc.postRequire("post","/area/select","", _params, function(data){
                var msg=JSON.parse(data.message);
                $("#area_shop").val("共"+msg.store_count+"个店铺");
            })
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
