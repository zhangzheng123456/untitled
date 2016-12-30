var oc = new ObjectControl();
var left = ($(window).width() - $("#tk").width()) / 2;//弹框定位的left值
var tp = ($(window).height() - $("#tk").height()) / 2;//弹框定位的top值
var inx = 1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize = 10;//默认传的每页多少行
var value = "";//收索的关键词
var param = {};//定义的对象
var _param = {};//筛选定义的内容
var list = "";
var cout = "";
var filtrate = "";//筛选的定义的值
var titleArray=[];
var key_val = sessionStorage.getItem("key_val");//取页面的function_code
key_val = JSON.parse(key_val);
var funcCode = key_val.func_code;
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
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
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
    });
function showLi() {
    $("#liebiao").show();
}
function hideLi() {
    $("#liebiao").hide();
}
$("#filtrate").click(function () {//点击筛选框弹出下拉框
    $(".sxk").slideToggle();
})
$("#pack_up").click(function () {//点击收回 取消下拉框
    $(".sxk").slideUp();
})
//点击清空  清空input的value值
$("#empty").click(function(){
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    value="";
    filtrate="";
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["searchValue"]="";
    GET(inx,pageSize);
})
function setPage(container, count, pageindex, pageSize, funcCode) {
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
    if (value == "" && filtrate == "") {
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["searchValue"]="";
        GET(a, b);
    } else if (value !== "") {
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a, b);
    } else if (filtrate !== "") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a, b);
    }
}
function superaddition(data, num) {//页面加载循环
    console.log(data);
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


    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        if (num >= 2) {
            var a = i + 1 + (num - 1) * pageSize;
        } else {
            var a = i + 1;
        }
        var send_type='';
        if(data[i].send_type=="sms"){
            send_type="短信";
        }
        if(data[i].send_type=="wxmass"){
            send_type="微信群发消息";
        }
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                if(code=="details"){
                        TD+="<td class='details'><a href='javascript:void(0)'>"
                        + "查看"
                        + "</a></span></td>"
                }else{
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }
            })(c)
        }
        $(".table tbody").append("<tr id='" + data[i].id + "' data-send_type='" + data[i].send_type + "' data-content='" + data[i].content + "'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:left;'>"
            + a
            + "</td>" +
            TD +
            "</tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
};
//权限配置
function jurisdiction(actions) {
    $('#jurisdiction').empty();
    for (var i = 0; i < actions.length; i++) {
        if (actions[i].act_name == "add") {
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        } else if (actions[i].act_name == "delete") {
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        } else if (actions[i].act_name == "edit") {
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
//页面加载调权限接口
function qjia(){
    var param1={};
    param1["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param1,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        titleArray=message.columns;
        jurisdiction(actions);
        jumpBianse();
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["searchValue"]="";
        GET(inx, pageSize);
        tableTh();
    })
}
function tableTh(){ //table  的表头
    var TH="";
    for(var i=0;i<titleArray.length;i++){
        TH+="<th>"+titleArray[i].show_name+"</th>"
    }
    $("#tableOrder").after(TH);
}
qjia();
//页面加载时list请求
function GET(a, b) {
    whir.loading.add("", 0.5);//加载等待框
    //oc.postRequire("get", "/vipFsend/list?pageNumber=" + a + "&pageSize=" + b
    //    + "&funcCode=" + funcCode + "", "", "", function (data) {
    $("#end").attr("onclick","laydate({elem:'#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
    oc.postRequire("post", "/vipFsend/search", "0", param, function (data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            cout = list.pages;
            var pageNum = list.pageNum;
            var list = list.list;
            superaddition(list, pageNum);
            jumpBianse();
            setPage($("#foot-num")[0], cout, pageNum, b, funcCode);
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
        $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function () {
        var input = $(this).find("input")[0];
        var thinput = $("thead input")[0];
        $(this).toggleClass("tr");
        console.log(input);
        if (input.type == "checkbox" && input.name == "test" && input.checked == false) {
            input.checked = true;
            $(this).addClass("tr");
        } else if (input.type == "checkbox" && input.name == "test" && input.checked == true) {
            if (thinput.type == "checkbox" && input.name == "test" && input.checked == true) {
                thinput.checked = false;
            }
            input.checked = false;
            $(this).removeClass("tr");
        }
    })
    //点击新增时页面进行的跳转
    $('#add').click(function () {
        sessionStorage.removeItem("group_vip");
        sessionStorage.removeItem("id");
        $(window.parent.document).find('#iframepage').attr("src", "/vip/message_add.html");
    })
    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        sessionStorage.setItem("id",id);
        $(window.parent.document).find('#iframepage').attr("src","/vip/message_edit.html");
    })
    //删除
    $("#remove").click(function () {
        var l = $(window).width();
        var h = $(document.body).height();
        var tr = $("tbody input[type='checkbox']:checked").parents("tr");
        if (tr.length == 0) {
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        console.log(left);
        $("#p").css({"width": +l + "px", "height": +h + "px"});
        $("#tk").css({"left": +left + "px", "top": +tp + "px"});
    })
    //查看信息详情跳转
    $('.details').click(function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        var param={};
        var id=$(this).parents('tr').attr("id");
        var send_type=$(this).parents('tr').attr("data-send_type");
        if(send_type == "微信群发消息"){
            send_type = 'wxmass';
        }else if(send_type == "短信"){
            send_type = "sms";
        }
        param["id"]=id;
        // param["send_type"]=send_type;
        whir.loading.add("",0.5);//加载等待框
        console.log(param);
        oc.postRequire("post","/vipFsend/checkVipInfo","0",param,function(data){
            if(data.code=="0"){
                $('#details').show();
                $('#content').hide();
                var message=JSON.parse(data.message);
                var list=message.vip_info;
                $(".table #table_r thead").empty();
                $(".table #table_r tbody").empty();
                if(list.length == 0){
                    var len = $(".table #table_r thead tr th").length;
                    var i;
                    for(i=0;i<10;i++){
                        $(".table #table_r tbody").append("<tr></tr>");
                        for(var j=0;j<len;j++){
                            $($(".table #table_r tbody tr")[i]).append("<td></td>");
                        }
                    }
                    $(".table #table_r tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
                }else {
                    if(send_type == "sms"){
                        $(".table #table_r thead").append("<tr class='th'>" +
                            "<th style='text-align:center;width: 50px;'>序号</th>" +
                            "<th>会员名称</th>" +
                            "<th>会员卡号</th>" +
                            "<th>会员手机号</th>" +
                            "</tr>");
                        for(var i=0;i<list.length;i++) {
                            var a = i + 1;
                            $(".table #table_r tbody").append("<tr><td width='50px;' style='text-align: center;'>"
                                + a
                                + "</td><td>"
                                + list[i].vip_name
                                + "</td><td>"
                                + list[i].cardno
                                + "</td><td>"
                                + list[i].vip_phone
                                + "</td></tr>");
                        }
                    }else if(send_type == "wxmass"){
                        $(".table #table_r thead").append("<tr class='th'>" +
                            "<th style='text-align:center;width: 50px;'>序号</th>" +
                            "<th>会员编号</th>" +
                            "<th>会员名称</th>" +
                            "<th>发送状态</th>" +
                            "</tr>");
                        for(var j=0;j<list.length;j++) {
                            var a = j + 1;
                            $(".table #table_r tbody").append("<tr><td width='50px;' style='text-align: center;'>"
                                + a
                                + "</td><td>"
                                + list[j].vip_id
                                + "</td><td>"
                                + list[j].vip_name
                                + "</td><td>"
                                + list[j].is_send
                                + "</td></tr>");
                        }
                    }
                }
            }
            $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
            $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
            whir.loading.remove();//移除加载框
            if(data.code=="-1"){
                alert(data.message);
            }
        })
    })
    //任务详情关闭按钮
    $('#turnoff').click(function(){
        $('#details').hide();
        $('#content').show();
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function () {
    var event = window.event || arguments[0];

    inx=1;
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    param["funcCode"] = funcCode;
    if (event.keyCode == 13) {
        value = this.value.trim();
        param["searchValue"] = value;
        POST(inx,pageSize);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().replace(/\s+/g, "");
    inx=1;
    param["searchValue"] = value;
    param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    param["funcCode"] = funcCode;
    POST(inx,pageSize);
})
//搜索的请求函数
function POST(a,b) {
    whir.loading.add("", 0.5);//加载等待框
    $("#end").attr("onclick","laydate({elem:'#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
    oc.postRequire("post", "/vipFsend/search", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            cout = list.pages;
            var pageNum = list.pageNum;
            var list = list.list;
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, pageNum);
                jumpBianse();
            }
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0], cout, pageNum, b, funcCode);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
console.log(left);
//弹框关闭
$("#X").click(function () {
    $("#p").hide();
    $("#tk").hide();
})
//取消关闭
$("#cancel").click(function () {
    $("#p").hide();
    $("#tk").hide();
})
//弹框删除关闭
    $("#delete").click(function() {
        $("#p").hide();
        $("#tk").hide();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        for(var i=tr.length-1,ID="";i>=0;i--){
            var r=$(tr[i]).attr("id");
            if(i>0){
                ID+=r+",";
            }else{
                 ID+=r;
            }
        }
        var params= {};
        params["id"] = ID;
        oc.postRequire("post", "/vipFsend/delete", "0", params, function(data) {
            if (data.code == "0") {
                if (value == "" && filtrate == "") {
                    frame().then(function(){
                        GET(pageNumber, pageSize);
                    });
                    $('.frame').html('删除成功');
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
                    param["searchValue"]="";
                } else if (value !== "") {
                    frame().then(function(){
                        POST(pageNumber, pageSize);
                    });
                    $('.frame').html('删除成功');
                    param["pageNumber"]=pageNumber;
                } else if (filtrate !== "") {
                    frame().then(function(){
                        filtrates(pageNumber, pageSize);
                    });
                    $('.frame').html('删除成功');
                    _param["pageNumber"]=pageNumber;
                }
                var thinput = $("thead input")[0];
                thinput.checked = false;
            } else if (data.code == "-1") {
                frame();
                $('.frame').html('删除失败');
                $('.frame').html(data.message);
            }
        })
    })
//删除弹框
function frame() {
    var def= $.Deferred();
    var left = ($(window).width() - $("#frame").width()) / 2;//弹框定位的left值
    var tp = ($(window).height() - $("#frame").height()) / 2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:' + left + 'px;top:' + tp + 'px;"></div>');
    $(".frame").animate({opacity: "1"}, 1000);
    $(".frame").animate({opacity: "0"}, 1000);
    setTimeout(function(){
        $(".frame").hide();
        def.resolve();
    },2000);
    return def;
}
//全选
function checkAll(name) {
    var el = $("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name)) {
            el[i].checked = true;
        }
    }
};

//取消全选
function clearAll(name) {
    var el = $("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name)) {
            el[i].checked = false;
        }
    }
};
//导出拉出list
$("#leading_out").click(function () {
    var l = $(window).width();
    var h = $(document.body).height();
    var left=($(window).width()-$(".file").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".file").height())/2;//弹框定位的top值
    $(".file").css({"left":+left+"px","top":+tp+"px"});
    $("#p").show();
    $("#p").css({"width": +l + "px", "height": +h + "px"});
    $('.file').show();
    $(".into_frame").hide();
    var param = {};
    param["function_code"] = funcCode;
    oc.postRequire("post", "/list/ getCols", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var message = JSON.parse(message.tableManagers);
            console.log(message);
            $("#file_list_l ul").empty();
            for (var i = 0; i < message.length; i++) {
                $("#file_list_l ul").append("<li data-name='" + message[i].column_name + "'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                    + i + 1 + "'/><label for='checkboxInput" + i + 1 + "'></label></div><span class='p15'>" + message[i].show_name + "</span></li>")
            }
            bianse();
            $("#file_list_r ul").empty();
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
})
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
//导出提交的
$("#file_submit").click(function () {
    var li = $("#file_list_r input[type='checkbox']").parents("li");
    var param = {};
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    console.log(li.length);
    for (var i = 0, column_name = ""; i < li.length; i++) {
        var r = $(li[i]).attr("data-name");
        if (i < li.length - 1) {
            column_name += r + ",";
        } else {
            column_name += r;
        }
    }
    param["column_name"] = column_name;
    param["searchValue"] = value;
    if (filtrate == "") {
        param["list"] = "";
    } else if (filtrate !== "") {
        param["list"] = list;
    }
    oc.postRequire("post", "/area/exportExecl", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var path = message.path;
            var path = path.substring(1, path.length - 1);
            $('#download').html("<a href='/" + path + "'>下载文件</a>");
            $('#download').addClass("download");
            $('#file_submit').hide();
            $('#download').show();
            //导出关闭按钮
            $('#file_close').click(function () {
                $('.file').hide();
            })
            $('#download').click(function(){
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            })
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
})
//导出关闭按钮
$('#file_close').click(function () {
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
})
//点击导入
$("#guide_into").click(function () {
    var l = $(window).width();
    var h = $(document.body).height();
    var left=($(window).width()-$(".into_frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".into_frame").height())/2;//弹框定位的top值
    $(".into_frame").css({"left":+left+"px","top":+tp+"px"});
    $("#p").show();
    $("#p").css({"width": +l + "px", "height": +h + "px"});
    $('.file').hide();
    $(".into_frame").show();
})
//导入关闭按钮
$("#x1").click(function () {
    $("#p").hide();
    $(".into_frame").hide();
})
//上传文件
function UpladFile() {
    var fileObj = document.getElementById("file").files[0];
    console.log(fileObj);
    var FileController = "/area/addByExecl"; //接收上传文件的后台地址
    var form = new FormData();
    form.append("file", fileObj); // 文件对象
    // XMLHttpRequest 对象
    var xhr = null;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else {
        xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                doResult(xhr.responseText);
            } else {
                console.log('服务器返回了错误的响应状态码');
                $('#file').val("");
            }
        }
    }
    function doResult(data) {
        var data = JSON.parse(data);
        if (data.code == "0") {
            alert('导入成功');
        } else if (data.code == "-1") {
            alert("导入失败" + data.message);
        }
        $('#file').val("");
    }

    xhr.open("post", FileController, true);
    xhr.onload = function () {
        // alert("上传完成!");
    };
    xhr.send(form);
}
//筛选按钮
oc.postRequire("get", "/list/filter_column?funcCode=" + funcCode + "", "0", "", function (data) {
    if(data.code=="0"){
        var message=JSON.parse(data.message);
        var filter=message.filter;
        $("#sxk .inputs ul").empty();
        var li="";
        for(var i=0;i<filter.length;i++){
            if(filter[i].type=="text"){
                li += "<li><label>" + filter[i].show_name + "</label><input type='text' id='" + filter[i].col_name + "'></li>";
            }else if(filter[i].type=="select"){
                var msg=filter[i].value;
                console.log(msg);
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
                }
                ul+="</ul>";
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
            }else if(filter[i].type=="date"){
                li+="<li class='created_date' id='"
                    +filter[i].col_name
                    +"'><label>"
                    +filter[i].show_name
                    +"</label>"
                    +"<input type='text' id='start' class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})\">"
                    +"<label class='tm20'>至</label>"
                    +"<input type='text' id='end' class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})\">"
                    +"</li>";
            }

        }
        $("#sxk .inputs ul").html(li);
        filtrateDown();
        //筛选的keydow事件
        $('#sxk .inputs input').keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode == 13){
                getInputValue();
            }
        })
    }
});
function checkStart(data){
    $("#end").attr("onclick","laydate({elem:'#end',min:'"+data+"',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
};
function checkEnd(data){
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '"+data+"',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
};
function filtrateDown(){
    //筛选select框
    $(".isActive_select input").click(function (){
        var ul=$(this).next(".isActive_select_down");
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
    })
    $(".isActive_select input").blur(function(){
        var ul=$(this).next(".isActive_select_down");
        setTimeout(function(){
            ul.hide();
        },200);
    })
    $(".isActive_select_down li").click(function () {
        var html=$(this).text();
        var code=$(this).attr("data-code");
        $(this).parents("li").find("input").val(html);
        $(this).parents("li").find("input").attr("data-code",code);
        $(".isActive_select_down").hide();
    })
}
//筛选查找
$("#find").click(function(){
    getInputValue();
})
function getInputValue(){
    var input=$('#sxk .inputs>ul>li');
   inx=1;
   _param["pageNumber"]=inx;
   _param["pageSize"]=pageSize;
   _param["funcCode"]=funcCode;
   var num=0;
   list=[];//定义一个list
   for(var i=0;i<input.length;i++){
        var screen_key=$(input[i]).find("input").attr("id");
        var screen_value={};
       if($(input[i]).attr("class")=="isActive_select"){
           screen_value=$(input[i]).find("input").attr("data-code");
       }else if($(input[i]).attr("class")=="created_date"){
           var start=$('#start').val();
           var end=$('#end').val();
           screen_key=$(input[i]).attr("id");
           screen_value={"start":start,"end":end};
       }else{
           screen_key=$(input[i]).find("input").attr("id");
           screen_value=$(input[i]).find('input').val().trim();
       }
        if(screen_value!=""){
            num++;
        }
       var param1={"screen_key":screen_key,"screen_value":screen_value};
       list.push(param1);
   }
   _param["list"]=list;
    value="";//把搜索滞空
    $("#search").val("");
    filtrates(inx,pageSize)
    if(num>0){
        filtrate="sucess";
    }else if(num<=0){
        filtrate="";
    }
}
//筛选发送请求
function filtrates(a,b) {
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vipFsend/screen", "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            cout = list.pages;
            var pageNum = list.pageNum;
            var list = list.list;
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, pageNum);
                jumpBianse();
            }
            setPage($("#foot-num")[0], cout, pageNum, b, funcCode);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
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
            if (value == "" && filtrate == "") {
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["searchValue"]="";
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                param["pageNumber"]=inx;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["pageSize"] = pageSize;
                _param["pageNumber"]=inx;
                filtrates(inx, pageSize);
            }
        };
    }
})
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    window.location.reload();
})