var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var key_val=sessionStorage.getItem("key_val");
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
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
    value="";
    filtrate="";
    $('#search').val("");
    $(".table p").remove();
    GET(inx,pageSize);
})
function setPage(container, count, pageindex,pageSize) {
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
function superaddition(data,num){//页面加载循环
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div>"
                        + "</td><td style='text-align:left;'>"
                        + a
                        + "</td><td>"
                        + data[i].store_code
                        + "</td><td>"
                        + data[i].store_name
                        + "</td><td class='staff' data-code='"+data[i].corp_code+"'><a href='javascript:void(0)'>"
                        +"查看"
                        + "</a></td><td>"
                        + data[i].area.area_name
                        + "</td><td><span title='"+data[i].brand_name+"'>"
                        +data[i].brand_name
                        + "</span></td><td>"
                        +data[i].corp.corp_name
                        + "</td><td>"
                        +data[i].modifier
                        + "</td><td>"
                        +data[i].modified_date
                        + "</td><td>"
                        +data[i].isactive
                        +"</td></tr>");
    }
    whir.loading.remove();//移除加载框
    sessionStorage.removeItem("return_jump");
};
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="qrcode"){
            $('#jurisdiction').append("<li id='qrcode'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>生成</a></li>");
        }
    }
}
//页面加载调权限接口
function qjia(){
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
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("get","/shop/list?pageNumber="+a+"&pageSize="+b
        +"&funcCode="+funcCode+"","","",function(data){
            console.log(data);
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                cout=list.pages;
                var list=list.list;
                superaddition(list,a);
                jumpBianse();
                setPage($("#foot-num")[0],cout,a,b,funcCode);
            }else if(data.code=="-1"){
                alert(data.message);
            }
    });
}
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色 
         $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
         $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
         $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
    })
    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        var return_jump={};//定义一个对象
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["filtrate"]=filtrate;//筛选的值
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
        return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);//把id好保存到本地
        console.log(id);
        $(window.parent.document).find('#iframepage').attr("src","/shop/shop_edit.html");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function(){
        var input=$(this).find("input")[0];
        var thinput=$("thead input")[0];
        $(this).toggleClass("tr");  
        console.log(input);
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
            $(window.parent.document).find('#iframepage').attr("src","/shop/shop_add.html");
        })
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            id=$(tr).attr("id");
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["filtrate"]=filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
            return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.setItem("id",id);
            $(window.parent.document).find('#iframepage').attr("src","/shop/shop_edit.html");
        }else if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(tr.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    })
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html('请先选择');
            return;
        }
        $("#p").show();
        $("#tk").show();
        console.log(left);
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
    //查看员工跳转查看员工页面
    $('.staff').click(function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        var store_code=$(this).parents('tr').find("td:eq(2)").html();
        var corp_code=$(this).attr("data-code");
        var store_corp={"store_code":store_code,"corp_code":corp_code};
        sessionStorage.setItem("store_corp",JSON.stringify(store_corp));
        $(window.parent.document).find('#iframepage').attr("src","/shop/shopcheck_staff.html");
    })
    //批量生成二维码
    $('#qrcode').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        var param={};
        var list=[];
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        for(var i=0;i<tr.length;i++){
            var store_code=$(tr[i]).find("td:eq(2)").html();
            var corp_code=$(tr[i]).find(".staff").attr("data-code");
            var param1={"store_code":store_code,"corp_code":corp_code};
            list.push(param1);
        }
        param["list"]=list;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/creatStoresQrcode","0",param,function(data){
            if(data.code=="0"){
                frame();
                $('.frame').html(data.message);
            }else if(data.code=="-1"){
                frame();
                $('.frame').html(data.message);
            }
            whir.loading.remove();//移除加载框
        })
    });
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    value=this.value.replace(/\s+/g,"");
    inx=1;
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        POST(inx,pageSize);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    inx=1;
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    POST(inx,pageSize);
})
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/shop/search","0",param,function(data){
        console.log(data);
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,a);
                jumpBianse();
            }
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,a,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
//弹框关闭
$("#X").click(function(){
    $("#p").hide();
    $("#tk").hide();
})
//取消关闭
$("#cancel").click(function(){
    $("#p").hide();
    $("#tk").hide();
})
//删除弹框
function frame(){
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
}
//弹框删除关闭
    $("#delete").click(function() {
        $("#p").hide();
        $("#tk").hide();
        var tr = $("tbody input[type='checkbox']:checked").parents("tr");
        for (var i = 0, ID = ""; i < tr.length; i++) {
            var r = $(tr[i]).attr("id");
            if (i < tr.length - 1) {
                ID += r + ",";
            } else {
                ID += r;
            }
        }
        var params= {};
        params["id"] = ID;
        oc.postRequire("post", "/shop/delete", "0", params, function(data) {
            if (data.code == "0") {
                if (value == "" && filtrate == "") {
                    frame();
                    $('.frame').html('删除成功');
                    GET(pageNumber, pageSize);
                } else if (value !== "") {
                    frame();
                    $('.frame').html('删除成功');
                    param["pageNumber"]=pageNumber;
                    POST(pageNumber, pageSize);
                } else if (filtrate !== "") {
                    frame();
                    $('.frame').html('删除成功');
                    param["pageNumber"]=pageNumber;
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
//全选
function checkAll(name){
    var el=$("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for(var i=0; i<len; i++)
        {
           if((el[i].type=="checkbox") && (el[i].name==name))
            {
              el[i].checked = true;
            }
        }
};

//取消全选
function clearAll(name){
    var el=$("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for(var i=0; i<len; i++)
        {
            if((el[i].type=="checkbox") && (el[i].name==name))
            {
              el[i].checked = false;
            }
        }
};
//导出拉出list
$("#leading_out").click(function(){
    var l=$(window).width();
    var h=$(document.body).height();
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/shop/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            console.log(message);
            $("#file_list ul").empty();
            for(var i=0;i<message.length;i++){
                 $("#file_list ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//移除加载框
        }
    })
})
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list input[type='checkbox']:checked").parents("li");
    var tablemanager=[];
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();//反序
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/shop/exportExecl","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var path=message.path;
            var path=path.substring(1,path.length-1);
            $('#download').html("<a href='/"+path+"'>下载文件</a>");
            $('#download').addClass("download");
            $('#file_submit').hide();
            $('#download').show();
            //导出关闭按钮
            $('#file_close').click(function(){
                $('.file').hide();
            })
            $('#download').click(function(){
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            })
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//移除加载框
        }
    })
})
//导出关闭按钮
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
})
//点击导入
$("#guide_into").click(function(){
    var l=$(window).width();
    var h=$(document.body).height();
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').hide();
    $(".into_frame").show();
})
//导入关闭按钮
$("#x1").click(function(){
    $("#p").hide();
    $(".into_frame").hide();
})
//上传文件
function UpladFile() {
    whir.loading.add("",0.5);//加载等待框
    var fileObj = document.getElementById("file").files[0];
    console.log(fileObj);
    var FileController = "/shop/addByExecl"; //接收上传文件的后台地址
    var form = new FormData();
    form.append("file", fileObj); // 文件对象
    // XMLHttpRequest 对象
    var xhr = null;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else {
        xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                doResult(xhr.responseText);
            } else {
                console.log('服务器返回了错误的响应状态码');
                $('#file').val("");
                whir.loading.remove();
            }
        }
    }
    function doResult(data) {
        var data=JSON.parse(data);
        whir.loading.remove();
        if(data.code=="0"){
            alert('导入成功');
        }else if(data.code=="-1"){
             alert("导入失败"+data.message);
        }
        $('#file').val("");
    }
    xhr.open("post", FileController, true);
    xhr.onload = function() {
        // alert("上传完成!");
    };
    xhr.send(form);
}
//筛选按钮
oc.postRequire("get","/list/filter_column?funcCode="+funcCode+"","0","",function(data){
    if(data.code=="0"){
        var message=JSON.parse(data.message);
        var filter=message.filter;
        $("#sxk .inputs ul").empty();
        var li="";
        for(var i=0;i<filter.length;i++){
            if(filter[i].type=="text"){
                li+="<li><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"'></li>";
            }else if(filter[i].type=="select"){
                var msg=filter[i].value;
                console.log(msg);
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
                }
                ul+="</ul>";
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
            }

        }
        $("#sxk .inputs ul").html(li);
        filtrateDown();
    }
});
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
   var input=$('#sxk .inputs input');
   _param["pageNumber"]=inx;
   _param["pageSize"]=pageSize;
   _param["funcCode"]=funcCode;
   var num=0;
   list=[];//定义一个list
    for(var i=0;i<input.length;i++){
        var screen_key=$(input[i]).attr("id");
        var screen_value=$(input[i]).val().trim();
        var screen_value="";
        if($(input[i]).parent("li").attr("class")=="isActive_select"){
            screen_value=$(input[i]).attr("data-code");
        }else{
            screen_value=$(input[i]).val().trim();
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
    filtrates(inx,pageSize);
    if(num>0){
        filtrate="sucess";
    }else if(num<=0){
        filtrate="";
    }
})
//筛选发送请求
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/shop/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,a);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,a,b,funcCode);
        }else if(data.code=="-1"){
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
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["pageSize"] = pageSize;
                filtrates(inx, pageSize);
            }
        };
    }
})