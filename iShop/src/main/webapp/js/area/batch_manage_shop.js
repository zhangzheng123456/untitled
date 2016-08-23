var oc = new ObjectControl();
var inx = 1;//默认是第一页
var pageSize = "10";
var value = "";//收索的关键词
var param = {};//定义的对象
var user_id = sessionStorage.getItem("id");//用户id
var area_code = "";//区域编号
var area_name = "";//区域名称
var corp_code = ""//企业编号
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
if(return_jump!==null){
    console.log(return_jump);
    inx=return_jump.inx;
    pageSize=return_jump.pageSize;
    value=return_jump.value;
    filtrate=return_jump.filtrate;
    list=return_jump.list;
    param=JSON.parse(return_jump.param);
    _param=JSON.parse(return_jump._param);
}
if(return_jump==null){
    if(value==""&&filtrate==""){
        GET(inx,pageSize);
    }
}else if(return_jump!==null){
    if(pageSize==10){
        $("#page_row").val("10行/页");
    }
    if(pageSize==30){
        $("#page_row").val("30行/页");
    }
    if(pageSize==50){
        $("#page_row").val("50行/页");
    }
    if(pageSize==100){
        $("#page_row").val("100行/页");
    }
    if(value==""&&filtrate==""){
        GET(inx,pageSize);
    }else if(value!==""){
        $("#search").val(value);
        POST(inx,pageSize);
    }else if(filtrate!==""){
        filtrates(inx,pageSize);
    }
}
function setPage(container, count, pageindex,pageSize,funcCode){
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
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(a,b){//点击分页的时候调什么接口
    if (value==""&&filtrate=="") {
        GET(a,b);
    }else if (value!==""){
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
    }
}
//模仿select
$(function(){
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
                if(value==""&&filtrate==""){
                    inx=1;
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["pageSize"]=pageSize;
                    param["pageNumber"]=inx;
                    POST(inx,pageSize);
                }else if(filtrate!==""){
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
    }
);
function showLi(){
    $("#liebiao").show();
}
function hideLi(){
    $("#liebiao").hide();
}
$("#edit_shop_icon").click(function () {
    area_code = $('#AREA_ID').val();
    area_name = $('#AREA_NAME').val();
    $('#area_code').val(area_code);
    $('#areaName').val(area_name);
    $("#page-wrapper").hide();
    $(".content").show();
    GET();
})
$('#turnoff').click(function () {
    $("#page-wrapper").show();
    $(".content").hide();
})
$("#filtrate").click(function () {//点击筛选框弹出下拉框
    $(".sxk").slideToggle();
})
$("#pack_up").click(function () {//点击收回 取消下拉框
    $(".sxk").slideUp();
})
//点击清空  清空input的value值
$("#empty").click(function () {
    var input = $(".inputs input");
    for (var i = 0; i < input.length; i++) {
        input[i].value = "";
    }
})
function superaddition(data, num, die, live) {
    for (var i = 0; i < data.length; i++) {
        if (num >= 2) {
            var a = i + num * pageSize;
        } else {
            var a = i + 1;
        }
        $(".table tbody").append("<tr data-action='" + data[i].action_code + "' data-function='" + data[i].function_code + "'>"
            + "<td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td>"
            + data[i].store_code
            + "</td><td>"
            + data[i].store_name
            + "</td><td>"
            + data[i].area_code
            + "</td><td width='50px;' style='text-align: left;'><div class='checkbox1' id='" + data[i].id + "'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div></td></tr>");
    }
    for (var j = 0; j < die.length; j++) {
        if ($("#" + die[j].id).find("input").length > 0) {
            $("#" + die[j].id).find("input").parent("div").attr("class", "checkbox2");
            $("#" + die[j].id).find("input")[0].checked = true;
            $("#" + die[j].id).find("input").attr("disabled", "true");
            $("#" + die[j].id).find("input").attr("name", "die");
        }
    }
};
//页面加载时list请求
function GET() {
    corp_code=$("#OWN_CORP").val();
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    param["area_code"] = area_code;
    param["corp_code"] = corp_code;
    param["searchValue"] = value;
    oc.postRequire("post", "/area/stores/check", "0", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var die = message.die;
            var live = message.live;
            var list = JSON.parse(message.list);
                list=list.list;
            console.log(list);
            console.log(die)
            var actions = message.actions;
            superaddition(list, inx, die, live);
            // jurisdiction(actions);
            jumpBianse();
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
//加载完成以后页面进行的操作
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function () {
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    param["searchValue"] = value;
    param["group_code"] = group_code;
    param["user_id"] = user_id;
    if (event.keyCode == 13) {
        POST();
    }
});
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().replace(/\s+/g, "");
    param["searchValue"] = value;
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    param["funcCode"] = funcCode;
    POST();
})
//搜索的请求函数
function POST() {
    oc.postRequire("post", "/user/check_power", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var die = message.die;
            var live = message.live;
            var list = JSON.parse(message.list);
            console.log(list);
            console.log(die)
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, inx, die, live);
                jumpBianse();
            }
        } else if (data.code == "-1") {
            alert(data.message);
        }
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
//保存调的接口
$('#save').click(function () {
    var param = {};
    param["group_code"] = user_code;
    param["corp_code"] = corp_code;
    var tr = $("tbody input[name='test'][type='checkbox']:checked").parents('tr');
    var list = [];
    for (var i = 0; i < tr.length; i++) {
        var action_code = $(tr[i]).attr("data-action");
        var function_code = $(tr[i]).attr("data-function");
        var param1 = {"function_code": function_code, "action_code": action_code};
        list.push(param1);
    }
    param["list"] = list;
    oc.postRequire("post", "/user/check_power/save", "0", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            $("#page-wrapper").show();
            $(".content").hide();
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
})
