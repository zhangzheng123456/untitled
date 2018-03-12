/**
 * Created by Administrator on 2017/6/16.
 */
var oc = new ObjectControl();
var page_num=1;
var page_size=10;
var count = "";
var send_type="";
var send_data=JSON.parse(sessionStorage.getItem("send_data"));
var search="";
var screen="";
function setPage(container, count, pageindex, pageSize,total) {
    var container = container;
    var count = count==0?1:count;
    var pageindex = pageindex;
    var pageSize = pageSize;
    var total = total;
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
        page_num = pageindex==0?1:pageindex; //初始的页码
        $("#input-txt").val(page_num);
        $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
        oAlink[0].onclick = function () { //点击上一页
            if (page_num == 1) {
                return false;
            }
            page_num--;
            dian(page_num, pageSize);
            // setPage(container, count, page_num,pageSize,funcCode,value);
            return false;
        };
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                page_num = parseInt(this.innerHTML);
                dian(page_num, pageSize);
                // setPage(container, count, page_num,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (page_num == count) {
                return false;
            }
            page_num++;
            dian(page_num, pageSize);
            // setPage(container, count, page_num,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(a, b) {//点击分页的时候调什么接口
   get();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > count) {
        page_num = count
    }else{
        page_num=inx;
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
          get();
        }
    }
});
function superaddition(list, num) {//页面加载循环
    if(list.length==1&&num>1){
        page_num=num-1;
    }else{
        page_num=num;
    }
    $(".table thead").append("<tr class='th'>" +
        "<th style='text-align:center;width: 50px;'>序号</th>" +
        "<th>会员编号</th>" +
        "<th>会员名称</th>" +
        "<th>手机号</th>" +
        "<th>会员卡号</th>" +
        "<th>发送状态</th>" +
        "<th>发送时间</th>" +
        "</tr>");

    if (list.length == 0) {
        var len = 10;
        var i;
        for (i = 0; i < 10; i++) {
            $(".table tbody").append("<tr></tr>");
            for (var j = 0; j < len; j++) {
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").children().eq(5).html("<span style='margin-top: -15px;position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    }else {
        if (send_type == "sms") {
            for (var i = 0; i < list.length; i++) {
                if (num >= 2) {
                    var a = i + 1 + (num - 1) * page_size;
                } else {
                    var a = i + 1;
                }
                var is_send = "";
                if (list[i].is_send == "Y") {
                    is_send = "发送成功";
                }
                if (list[i].is_send == "N") {
                    is_send = "发送失败";
                }
                $(".table tbody").append("<tr><td width='50px;' style='text-align: center;'>"
                    + a
                    + "</td><td>"
                    + list[i].vip_id
                    + "</td><td>"
                    + list[i].vip_name
                    + "</td><td>"
                    + list[i].vip_phone
                    + "</td><td>"
                    + list[i].cardno
                    + "</td><td>"
                    + is_send
                    + "</td>" +
                    "<td>"+list[i].message_date+"</td>"+
                    "</tr>");
            }
        } else if (send_type == "wxmass") {
            for (var j = 0; j < list.length; j++) {
                if (num >= 2) {
                    var a = j + 1 + (num - 1) * page_size;
                } else {
                    var a = j + 1;
                }
                var is_send = "";
                var is_read = "";
                if (list[j].is_send == "Y") {
                    is_send = "发送成功";
                }
                if (list[j].is_send == "N") {
                    is_send = "发送失败";
                }
                if (list[j].is_read == "Y") {
                    is_read = "已读";
                }
                if (list[j].is_read == "N") {
                    is_read = "未读";
                }
                $(".table tbody").append("<tr><td width='50px;' style='text-align: center;'>"
                    + a
                    + "</td><td>"
                    + list[j].vip_id
                    + "</td><td>"
                    + list[j].vip_name
                    + "</td><td>"
                    + list[j].vip_phone
                    + "</td><td>"
                    + list[j].cardno
                    + "</td><td>"
                    + is_send
                    + "</td>"+
                    "<td>"+list[j].message_date+"</td>"+
                    + "</tr>");
            }
        }
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    //sessionStorage.removeItem("send_data");
}
//加载完成以后页面进行的操作
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
        $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
    })
}
function getInputValue(){
    var input=$('#sxk .inputs>ul>li');
    var screenObj={};
    for(var i=0;i<input.length;i++){
        var screen_key=$(input[i]).find("input").attr("id");
        var screen_value;
        if($(input[i]).attr("class")=="isActive_select"){
            screen_value=$(input[i]).find("input").attr("data-code");
        }else if($(input[i]).attr("class")=="selectDate"){
            var start=$(input[i]).find("input").attr("data-start");
            var end=$(input[i]).find("input").attr("data-end");
            screen_value={"start":start,"end":end};
        }else{
            screen_value=$(input[i]).find('input').val().trim();
        }
        screenObj[screen_key]=screen_value;
    }
    screen=screenObj
}
function get() {
    var param = {
        id: send_data.id,
        page_num:page_num,
        page_size:page_size,
        search:search,
        screen:screen
    };
    send_type = send_data.send_type;
    param.send_type=send_type;
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vipFsend/checkVipInfo", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.vip_info;
            $(".table tbody").empty();
            $(".table thead").empty();
            count = message.pages==0?1:message.pages;
            var pageNum = message.page_num;
            var total = message.count;
            var pageSize=message.page_size;
            superaddition(list, pageNum);
            jumpBianse();
            setPage($("#foot-num")[0], count, pageNum, pageSize,total);
        }
        if (data.code == "-1") {
            alert(data.message);
        }
    })
}
$("#select").click(function(){
    page_num=1;
    search="";
    $('#search').val("");
    getInputValue();
    get();
});
$("#search").keydown(function(){
    var event=window.event|| arguments[0];
    if (event.keyCode == 13) {
        $("#d_search").trigger("click");
    }
});
$("#d_search").click(function(){
    page_num=1;
    screen="";
    search=$("#search").val().trim();
    resetInputValue();
    get();
});
$("#turnoff,#back_message").click(function(){
    sessionStorage.removeItem("send_data");
    $(window.parent.document).find('#iframepage').attr("src", "/vip/message.html?t="+$.now());
});
function resetInputValue(){
    var input=$('#sxk .inputs>ul>li');
    for(var i=0;i<input.length;i++){
        if($(input[i]).attr("class")=="isActive_select"){
            $(input[i]).find("input").val("全部");
            $(input[i]).find("input").attr("data-code","");
        }else if($(input[i]).attr("class")=="selectDate"){
            $(input[i]).find("input").attr("data-start","").attr("data-end","").val("");
        }else{
            $(input[i]).find('input').val("");
        }
    }
}
$("#empty").click(function(){
    resetInputValue();
    page_num=1;
    search="";
    $('#search').val("");
    getInputValue();
    get();
});
function showLi() {
    $("#liebiao").show();
}
function hideLi() {
    $("#liebiao").hide();
}
$(function(){
    $("#page_row,.page_p .icon-ishop_8-02").click(function(){
        if("block" == $("#liebiao").css("display")){
            hideLi();
        }else{
            showLi();
        }
    });
    $("#liebiao li").each(function(i,v){
        $(this).click(function(){
            page_num=1;
            page_size=$(this).attr('id');
            $("#page_row").val($(this).html());
            get();
            hideLi();
        });
    });
    $("#page_row").blur(function(){
        setTimeout(hideLi,200);
    });
});
//筛选select框
$(".isActive_select input").click(function() {
    var ul = $(this).next(".isActive_select_down");
    if (ul.css("display") == "none") {
        ul.show();
    } else {
        ul.hide();
    }
});
$(".isActive_select input").blur(function() {
    var ul = $(this).next(".isActive_select_down");
    setTimeout(function() {
        ul.hide();
    }, 200);
});

$(".isActive_select_down").on("click", "li", function() {
    var html = $(this).text();
    var code = $(this).attr("data-code");
    $(this).parents("li").find("input").val(html);
    $(this).parents("li").find("input").attr("data-code", code);
    $(".isActive_select_down").hide();
});
//筛选按钮
$('#filtrate').click(function() {
    $(this).parents(".action").next('.sxk').slideToggle();
    if ($(this).html() == "收起筛选") {
        //收起
        $(this).html('筛选');
        $(this).prevAll().hide();
    } else {
        //展开
        $(this).html('收起筛选');
        $(this).prevAll().show();
    }
});
$("#export").click(function(){
    var param = {
        id: send_data.id,
        page_num:page_num,
        page_size:page_size,
        search:search,
        screen:screen
    };
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vipFsend/outExecl", "0", param, function (data) {
        whir.loading.remove();
        if(data.code==0){
            var message=JSON.parse(data.message);
            var path=message.path;
            $("#export_list_sure").html("<a href='/"+path+"'>下载文件</a>");
            $("#p").css({right:0,bottom:0}).show();
            $("#export_list").show();
        }else{
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "导出失败"
            });
        }

    })
});
$(".export_list_cancel").click(function(){
    $("#export_list_sure").html("下载文件");
    $("#export_list").hide();
    $("#p").hide();
});
laydate.render({
    elem: '#send_date',
    range: true,
    type: 'datetime',
    done: function(value) {
        if (value == "") {
            $("#send_date").attr("data-start", "");
            $("#send_date").attr("data-end", "");
        } else {
            value = value.split(" - ");
            $("#send_date").attr("data-start", value[0]);
            $("#send_date").attr("data-end", value[1]);
        }
    }
});
get();
