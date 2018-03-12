/**
 * Created by Administrator on 2016/11/25.
 */
var oc = new ObjectControl();
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
function waterFull(){

    var container = $('#waterfull ul');
    var loading=$('#imloading');
    // 初始化loading状态
    loading.data("on",false);
    /*判断瀑布流最大布局宽度，最大为1280*/
    function tores(){
        var tmpWid=$(window).width()-220;
        //if(tmpWid>1280){
        //    tmpWid=tmpWid;
        //}else{
            var column=Math.floor(tmpWid/360);
            tmpWid=column*360;
        //}
        $('.waterfull').width(tmpWid);
    }
    tores();
    $(window).resize(function(){
        tores();
    });
    container.imagesLoaded(function(){
        container.masonry('destroy');
        container.masonry({
            columnWidth: 360,
            itemSelector : '.item',
            isFitWidth: false,//是否根据浏览器窗口大小自动适应默认false
            isAnimated: false,//是否采用jquery动画进行重拍版
            isRTL:false,//设置布局的排列方式，即：定位砖块时，是从左向右排列还是从右向左排列。默认值为false，即从左向右
            isResizable: true,//是否自动布局默认true
            animationOptions: {
                duration: 800,
                queue: true//是否队列，从一点填充瀑布流
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
}
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
    });
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var val=$(".masonry input[type='checkbox']:checked").parents("li");
        console.log('val是'+val);
        if(val.length==1){
            var id=$(val).find('input').attr("id");
            console.log('id是'+id);
            var corp_code = $(val).attr("data-code");
            var goods_match_code = id;
            sessionStorage.setItem("corp_code",corp_code);//存储的方法
            sessionStorage.setItem("goods_match_code",goods_match_code);//存储的方法
            $(window.parent.document).find('#iframepage').attr("src","/goods/fab_matchEditor.html");
    }
         if(val.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(val.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    });
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("#waterfull .item").find("input:checked");
        console.log(tr.length);
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
    });
}
//获取数据
function getVal(){
    whir.loading.add("", 0.5);//加载等待框
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
            whir.loading.remove();

        },
        error: function (data) {
            console.log('获取数据失败')
            whir.loading.remove();
        }
    });
}
//数据模板
function pageVal(arr,unqiuearr,list){
    console.log(unqiuearr);
    //盒子上部分+复选框
    var tempHTML1='<li data-code="${corp_code}" class="item" ondblclick="dblclick(this)" title="${titleShow}" ><div class="boxArea"><div class="checkbox"><input id="${code}" type="checkbox" class="check"/><label for="${code}"></label></div>';
   //搭配标题
    var tempTitle='<div class="box_title">${msg}</div>'
    //内容（图片+文字）迭代生成
    var tempHTML2='<div class="oneArea"> <img src="${goods_image}" alt=""/> <div>${goods_code}</div> </div>';
    //盒子下部分
    var tempHTML3='</div></li>';
    if(unqiuearr.length==0){
        $(".waterfull ul").html("<div style='position: absolute;left: 50%;top: 50%;font-size: 18px;color: #999;'>暂无内容</div>");
    }else{
        var num="";
        for(i=0;i<unqiuearr.length;i++){
            var html = '';
            var nowHTML1 = tempHTML1;
            nowHTML1 = nowHTML1.replace("${code}", unqiuearr[i]);
            nowHTML1 = nowHTML1.replace("${code}", unqiuearr[i]);
            var nowHTML2 = "";
            var nowTitle = '';
            var dismatch = [];
            for(k=0;k<list.length;k++){
                if(list[k].goods_match_code ==unqiuearr[i]){
                    if(list[k].match_display!==""&&list[k].match_display!==undefined){
                        dismatch=list[k].match_display.split(",");
                    }
                    if(num!==i){
                        for(var j=0;j<dismatch.length;j++){
                            nowHTML2 += tempHTML2;
                            nowTitle += tempTitle;
                            nowHTML2 = nowHTML2.replace("${goods_image}", dismatch[j]);
                            nowHTML2 = nowHTML2.replace("${goods_code}","效果图");
                        }
                    }
                    num=i;
                    nowHTML2 += tempHTML2;
                    nowTitle += tempTitle;
                    var titleShow = list[k].goods_match_desc;
                    nowHTML1 = nowHTML1.replace("${titleShow}",titleShow);
                    var goods_image="";
                    if(list[k].goods_image.indexOf("http")!==-1){
                        goods_image = list[k].goods_image;
                    }
                    if(list[k].goods_image.indexOf("http")==-1){
                        goods_image="../img/goods_default_image.png";
                    }
                    var  goods_match_title =list[k].goods_match_title;
                    var goods_code = list[k].goods_code;
                    nowTitle = nowTitle.replace("${msg}", goods_match_title);
                    nowHTML2 = nowHTML2.replace("${goods_image}", goods_image);
                    nowHTML2 = nowHTML2.replace("${goods_code}", goods_code);
                    nowHTML2 = nowHTML2.replace("${goods_code}", goods_code);
                    nowHTML1 = nowHTML1.replace("${corp_code}", list[k].corp_code);
                }
            }
            var nowHTML3 = tempHTML3;
            html += nowHTML1;
            html += nowTitle;
            html += nowHTML2;
            html += nowHTML3;
            $(".waterfull ul").append(html);
            $('.boxArea').find('.box_title:not(.box_title:first)').css('display', 'none');
        }
        waterFull();
    }
}
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().trim();
    inx = 1;
    param["searchValue"] = value;
    //param["pageNumber"] = inx;
    param["pageSize"] = pageSize;
    //param["funcCode"] = funcCode;
    POST(inx, pageSize);
});
$("#search").keydown(function(){
    var event=window.event||arguments[0];
    inx=1;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
        POST(inx,pageSize);
    }
})
//搜索的请求函数
function POST(a, b) {
    whir.loading.add("", 0.5);//加载等待框
    $('#search').attr("disabled",true);
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
                whir.loading.remove();//移除加载框
                $('#search').removeAttr("disabled");
                $("#waterfull p").remove();
                $("#waterfull ul").html("");
                $('.masonry').css('height','');
                $("#waterfull").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
            } else if (forLength > 0) {
                $("#waterfull p").remove();
                var arr=[];
                var unqiuearr=[];
                var hash={};
                //获取所有搭配id
                for(i=0;i< list.length;i++){
                    arr.push(list[i].goods_match_code);
                }
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
                whir.loading.remove();//移除加载框
                $('#search').removeAttr("disabled");
                jumpBianse();
            }

            var input = $(".inputs input");
            for (var i = 0; i < input.length; i++) {
                input[i].value = "";
            }
            filtrate = "";
            list = "";
            $(".sxk").slideUp();
            //setPage($("#foot-num")[0], cout, pageNum, b, funcCode);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//弹框关闭
$("#X").click(function () {
    $("#p").hide();
    $("#tk").hide();
});
//取消关闭
$("#cancel").click(function () {
    $("#p").hide();
    $("#tk").hide();
});
//弹框删除关闭
$("#delete").click(function () {
    $("#p").hide();
    $("#tk").hide();
    var list=[];
    var params = {};
    var tr=$("#waterfull .item").find("input:checked");
    for(var i=0;i<tr.length;i++){
        var goods_match_code=$(tr[i]).attr("id");
        var corp_code=$(tr[i]).parents("li").attr("data-code");
        var param = {
            "corp_code":corp_code,
            "goods_match_code":goods_match_code
        };
        list.push(param);
        // if(i<tr.length-1){
        //     goods_code+=r+",";
        //     corp_code+=h+",";
        // }else{
        //     goods_code+=r;
        //     corp_code+=h;
        // }
    }
    params["list"] = list;
    oc.postRequire("post", "/defmatch/delete", "0", params, function (data) {
        if (data.code == "0") {
            if (value == "" && filtrate == "") {
                frame();
                $('.frame').html('删除成功');
               window.location.reload();
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
});
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
function dblclick(dom) {
    var goods_match_code = $(dom).find('.check').attr('id');
    var corp_code = $(dom).attr("data-code");
    sessionStorage.setItem("corp_code", corp_code);//存储的方法
    sessionStorage.setItem("goods_match_code", goods_match_code);//存储的方法
    $(window.parent.document).find('#iframepage').attr("src", "/goods/fab_matchEditor.html");
}
//刷新
$('#reload').click(function(){
    location.reload();
});
window.onload =function() {
    getVal();
}