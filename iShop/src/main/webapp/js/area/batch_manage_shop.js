var oc = new ObjectControl();
var inx = 1;//默认是第一页
var pageSize = "10";
var cout = "";
var value = "";//收索的关键词
var param = {};//定义的对象
var area_code = "";//区域编号
var area_name = "";//区域名称
var corp_code = ""//企业编号

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
        $(".table tbody").append("<tr data-action='" + data[i].action_code + "' data-function='" + data[i].function_code + "'>"
            + "<td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td>"
            + data[i].store_code
            + "</td><td>"
            + data[i].store_name
            + "</td><td><span title='"+data[i].area_name+"'>"
            + data[i].area_name
            + "</span></td><td><span title='"+data[i].brand_name+"'>"
            + data[i].brand_name
            + "</span></td><td width='50px;' style='text-align: left;'><div class='checkbox1' id='" + data[i].id + "'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div></td></tr>");
    }
    for (var j = 0; j < data.length; j++) {
        if (data[j].is_this_area == "Y") {
            $("#"+data[j].id).find("input").parent("div").attr("class", "checkbox2");
            $("#"+data[j].id).find("input")[0].checked = true;
            //$("#"+data[j].id).find("input").attr("disabled", "true");
            $("#"+data[j].id).find("input").attr("name", "die");
        }
    }
    $("tbody tr").click(function () {
        var input = $(this).find("input")[0];
        if(input.type=="checkbox"&&input.checked==false){
            input.checked = true;
        }else if(input.type=="checkbox"&&input.checked==true){
            input.checked = false;
        }
    })
    $(".th th:last-child input").removeAttr("checked");
};
//页面加载时list请求
function GET(a, b) {
    whir.loading.add("",0.5);
    corp_code = $("#OWN_CORP").val();
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["area_code"] = area_code;
    param["corp_code"] = corp_code;
    param["searchValue"] = value;
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
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    param["searchValue"] = value;
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
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $(".sxk").slideUp();
    value = $("#search").val().replace(/\s+/g, "");
    param["searchValue"] = value;
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
    console.log(param.searchAreaCode);
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
                setPage($("#foot-num")[0], cout, a, b);
            }
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
//保存调的接口
$('#save').click(function () {
    var param = {};
    var tr = $("tbody input[name='test'][type='checkbox']:checked").parent('div');
    for(var i=0,ID="";i<tr.length;i++){
        var r=$(tr[i]).attr("id");
        if(i<tr.length-1){
            ID+=r+",";
        }else{
            ID+=r;
        }
    }
    var tr1 = $("tbody input[name='die'][type='checkbox']:not(:checked)").parent('div');
    for(var a=0,quit="";a<tr1.length;a++){
        var quitID=$(tr1[a]).attr("id");
        if(a<tr1.length-1){
            quit+=quitID+",";
        }else{
            quit+=quitID;
        }
    }
    console.log(quit);
    param["choose"] =ID;
    param["quit"] =quit;
    param["area_code"] = area_code;
    oc.postRequire("post", "/area/stores/save", "0", param, function (data) {
        console.log(data);
        if (data.code == "0") {
            $("#page-wrapper").show();
            $(".content").hide();
            window.location.reload();
        } else if (data.code == "-1") {
            alert("请选择店铺!");
        }
    })
});
