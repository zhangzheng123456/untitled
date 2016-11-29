/**
 * Created by Administrator on 2016/11/25.
 */
var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageNumber=1;//删除的默认的第一页;
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;

/*
 抛开瀑布流布局各种乱七八糟的算法，基于masonry的瀑布流，很是简单的，而且通过扩展animate,能实现瀑布流布局的晃动、弹球等效果。
 masonry还有很多参数我这里注解了常用的参数
 */
$(function(){
    /*瀑布流开始*/
    var container = $('.waterfull ul');
    var loading=$('#imloading');
    // 初始化loading状态
    loading.data("on",false);
    /*判断瀑布流最大布局宽度，最大为1280*/
    function tores(){
        var tmpWid=$(window).width();
        if(tmpWid>1280){
            tmpWid=1280;
        }else{
            var column=Math.floor(tmpWid/340);
            tmpWid=column*340;
        }
        $('.waterfull').width(tmpWid);
    }
    tores();
    $(window).resize(function(){
        tores();
    });
    container.imagesLoaded(function(){
        container.masonry({
            columnWidth: 340,
            itemSelector : '.item',
            isFitWidth: false,//是否根据浏览器窗口大小自动适应默认false
            isAnimated: false,//是否采用jquery动画进行重拍版
            isRTL:false,//设置布局的排列方式，即：定位砖块时，是从左向右排列还是从右向左排列。默认值为false，即从左向右
            isResizable: true,//是否自动布局默认true
            animationOptions: {
                duration: 800,
                easing: 'easeInOutBack',//如果你引用了jQeasing这里就可以添加对应的动态动画效果，如果没引用删除这行，默认是匀速变化
                queue: false//是否队列，从一点填充瀑布流
            }
        });
    });

    function loadImage(url) {
        var img = new Image();
        //创建一个Image对象，实现图片的预下载
        img.src = url;
        if (img.complete) {
            return img.src;
        }
        img.onload = function () {
            return img.src;
        };
    };
    loadImage('images/one.jpeg');
//        /*item hover效果*/
//        var rbgB=['#71D3F5','#F0C179','#F28386','#8BD38B'];
//        $('#waterfull').on('mouseover','.item',function(){
//            var random=Math.floor(Math.random() * 4);
//            $(this).stop(true).animate({'backgroundColor':rbgB[random]},1000);
//        });
//        $('#waterfull').on('mouseout','.item',function(){
//            $(this).stop(true).animate({'backgroundColor':'#fff'},1000);
//        });
});
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
//页面加载调权限接口
function qjia(){
    console.log('权限接口调用');
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        jurisdiction(actions);
        jumpBianse();
    })
}
qjia();
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function(){
        var input=$(this).find("input")[0];
        var thinput=$("thead input")[0];
        $(this).toggleClass("tr");
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
    })
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/goods/fab_matchAdd.html");
    })
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
}
//获取数据
function getVal(){
    var _params= {
        "id":"0",
        "message":""
    };
    $.ajax({
        url: '/defmatch/list',
        type: 'get',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            //console.log('获取数据成功'+JSON.stringify(data));
            var message =JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var arr=[];
            var unqiuearr=[];
            var hash={};
            //获取所有搭配id
            for(i=0;i< list.length;i++){
                arr.push(list[i].goods_match_code);
            }
            console.log(arr);
            //搭配id去重
            for(var i=0;i<arr.length;i++){
                if(!hash[arr[i]]){
                    hash[arr[i]] = true; //存入hash表
                    unqiuearr.push(arr[i]);
                }
            }
            console.log(unqiuearr);
            //var goods_code = list[i].goods_code;
            //var goods_image = list[i].goods_image;
            pageVal(arr,unqiuearr,list);

        },
        error: function (data) {
            console.log('获取数据失败')
        }
    });
}
//数据模板
function pageVal(arr,unqiuearr,list){
    //盒子上部分+复选框
    var tempHTML1='<li class="item"><div class="boxArea"><input type="checkbox"/>';
    //内容（图片+文字）迭代生成
    var tempHTML2='<div class="oneArea" id="${goods_code}"> <img src="${goods_image}" alt=""/> <div>${goods_code}</div> </div>';
    //盒子下部分
    var tempHTML3='</div></li>';

    for(i=0;i<unqiuearr.length;i++){
        var html = '';
        var nowHTML1 = tempHTML1;
        var nowHTML2 = "";
        for(k=0;k<list.length;k++){
            if(list[k].goods_match_code ==unqiuearr[i]){
                nowHTML2 += tempHTML2;
                var goods_image = list[k].goods_image;
                var goods_code = list[k].goods_code;
                nowHTML2 = nowHTML2.replace("${goods_image}", goods_image);
                nowHTML2 = nowHTML2.replace("${goods_code}", goods_code);
                nowHTML2 = nowHTML2.replace("${goods_code}", goods_code);
        }
        }
        var nowHTML3 = tempHTML3;
        html += nowHTML1;
        html += nowHTML2;
        html += nowHTML3;
        $(".waterfull ul").append(html);
    }

}
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().replace(/\s+/g, "");
    inx = 1;
    param["searchValue"] = value;
    //param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    //param["funcCode"] = funcCode;
    POST(inx, pageSize);
})
//搜索的请求函数
function POST(a, b) {
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/defmatch/search", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            //cout = list.pages;
            //var pageNum = list.pageNum;
            //var list = list.list;
            //var actions = message.actions;
            var forLength = list.length;     //显示盒子数量
            $(".masonry").empty();
            if (forLength <= 0) {
                $(".masonry p").remove();
                $(".masonry").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            } else if (forLength > 0) {
                whir.loading.remove();//移除加载框
                $(".masonry p").remove();
                //superaddition(list, pageNum);
                for(i=0;i<forLength;i++){
                    var goods_code = list[i].goods_code;
                    var goods_image = list[i].goods_image;
                    pageVal(goods_code,goods_image);
                }
                jumpBianse();
            }

            var input = $(".inputs input");
            for (var i = 0; i < input.length; i++) {
                input[i].value = "";
            }
            filtrate = "";
            list = "";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0], cout, pageNum, b, funcCode);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//弹框删除关闭
$("#delete").click(function () {
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
    var params = {};
    params["id"] = ID;
    console.log(param);
    oc.postRequire("post", "/defmatch/delete", "0", params, function (data) {
        if (data.code == "0") {
            if (value == "" && filtrate == "") {
                frame();
                $('.frame').html('删除成功');
                GET(pageNumber, pageSize);
            } else if (value !== "") {
                frame();
                $('.frame').html('删除成功');
                param["pageNumber"] = pageNumber;
                POST(pageNumber, pageSize);
            } else if (filtrate !== "") {
                frame();
                $('.frame').html('删除成功');
                _param["pageNumber"] = pageNumber;
                filtrates(pageNumber, pageSize);
            }
            var thinput = $("thead input")[0];
            thinput.checked = false;
        } else if (data.code == "-1") {
            frame();
            $('.frame').html(data.message);
        }
    })
})
//删除弹框
function frame() {
    var left = ($(window).width() - $("#frame").width()) / 2;//弹框定位的left值
    var tp = ($(window).height() - $("#frame").height()) / 2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
    },2000);
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
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/list/ getCols", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var message = JSON.parse(message.tableManagers);
            console.log(message);
            $("#file_list_l ul").empty();
            for(var i=0;i<message.length;i++){
                $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                    +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            bianse();
            $("#file_list_r ul").empty();
        }else if(data.code=="-1"){
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    })
})
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var param={};
    var tablemanager=[];
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();
    param["tablemanager"] = tablemanager;
    param["searchValue"] = value;
    if (filtrate == "") {
        param["list"] = "";
    } else if (filtrate !== "") {
        param["list"] = list;
    }
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/goods/fab/exportExecl", "0", param, function (data) {
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
            $('#download').click(function () {
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            })
        } else if (data.code == "-1") {
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
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
    whir.loading.add("",0.5);//加载等待框
    var fileObj = document.getElementById("file").files[0];
    console.log(fileObj);
    var FileController = "/goods/fab/addByExecl"; //接收上传文件的后台地址
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
                whir.loading.remove();//移除加载框
            }
        }
    }
    function doResult(data) {
        var data = JSON.parse(data);
        whir.loading.remove();//移除加载框
        if (data.code == "0") {
            alert('导入成功');
            window.location.reload();
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
    $("#p").hide();
    $(".into_frame").hide();
}
//筛选按钮
oc.postRequire("get", "/list/filter_column?funcCode=" + funcCode + "", "0", "", function (data) {
    if (data.code == "0") {
        var message = JSON.parse(data.message);
        var filter = message.filter;
        $("#sxk .inputs ul").empty();
        var li = "";
        for (var i = 0; i < filter.length; i++) {
            if (filter[i].type == "text") {
                if(filter[i].show_name=="发布时间"){
                    li += "<li><label>" + filter[i].show_name + "</label><input type='text' id='" + filter[i].col_name + "' class='laydate-icon' onclick='laydate({istime: true, format: \"YYYY-MM-DD\"})' ></li>";
                }else {
                    li += "<li><label>" + filter[i].show_name + "</label><input type='text' id='" + filter[i].col_name + "'></li>";
                }
            } else if (filter[i].type == "select") {
                var msg = filter[i].value;
                console.log(msg);
                var ul = "<ul class='isActive_select_down'>";
                for (var j = 0; j < msg.length; j++) {
                    ul += "<li data-code='" + msg[j].value + "'>" + msg[j].key + "</li>"
                }
                ul += "</ul>";
                li += "<li class='isActive_select'><label>" + filter[i].show_name + "</label><input type='text' id='" + filter[i].col_name + "' data-code='' readonly>" + ul + "</li>"
            }

        }
        $("#sxk .inputs ul").html(li);
        if (filtrate !== "") {
            $(".sxk").slideDown();
            for (var i = 0; i < list.length; i++) {
                if ($("#" + list[i].screen_key).parent("li").attr("class") !== "isActive_select") {
                    $("#" + list[i].screen_key).val(list[i].screen_value);
                } else if ($("#" + list[i].screen_key).parent("li").attr("class") == "isActive_select") {
                    var svalue = $("#" + list[i].screen_key).next(".isActive_select_down").find("li[data-code='" + list[i].screen_value + "']").html();
                    $("#" + list[i].screen_key).val(svalue);
                }
            }
        }
        filtrateDown();
        //筛选的keydow事件
        $('#sxk .inputs input').keydown(function () {
            var event = window.event || arguments[0];
            if (event.keyCode == 13) {
                getInputValue();
            }
        })
    }
});
function filtrateDown() {
    //筛选select框
    $(".isActive_select input").click(function () {
        var ul = $(this).next(".isActive_select_down");
        if (ul.css("display") == "none") {
            ul.show();
        } else {
            ul.hide();
        }
    })
    $(".isActive_select input").blur(function () {
        var ul = $(this).next(".isActive_select_down");
        setTimeout(function () {
            ul.hide();
        }, 200);
    })
    $(".isActive_select_down li").click(function () {
        var html = $(this).text();
        var code = $(this).attr("data-code");
        $(this).parents("li").find("input").val(html);
        $(this).parents("li").find("input").attr("data-code", code);
        $(".isActive_select_down").hide();
    })
}
//筛选查找
$("#find").click(function () {
    getInputValue();
})
function getInputValue() {
    var input = $('#sxk .inputs input');
    inx = 1;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    _param["funcCode"] = funcCode;
    var num = 0;
    list = [];//定义一个list
    for (var i = 0; i < input.length; i++) {
        var screen_key = $(input[i]).attr("id");
        var screen_value = $(input[i]).val().trim();
        var screen_value = "";
        if ($(input[i]).parent("li").attr("class") == "isActive_select") {
            screen_value = $(input[i]).attr("data-code");
        } else {
            screen_value = $(input[i]).val().trim();
        }
        if (screen_value != "") {
            num++;
        }
        var param1 = {"screen_key": screen_key, "screen_value": screen_value};
        list.push(param1);
    }
    _param["list"] = list;
    value = "";//把搜索滞空
    $("#search").val("");
    filtrates(inx, pageSize)
    if (num > 0) {
        filtrate = "sucess";
    } else if (num <= 0) {
        filtrate = "";
    }
}
//筛选发送请求
function filtrates(a, b) {
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/goods/fab/screen", "0", _param, function (data) {
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
//编辑
$('#compile').click(function () {
    //获取选中的id，存储
    sessionStorage.setItem("corp_code",corp_code);//存储的方法
    //页面跳转
    $(window.parent.document).find('#iframepage').attr("src","/goods/fab_matchEditor.html");

})
window.onload =function() {
    getVal();
}