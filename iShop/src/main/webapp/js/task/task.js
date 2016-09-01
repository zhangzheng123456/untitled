var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageNumber=1;//默认删除是第一页
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
    $('.file').hide();
    $(".into_frame").hide();
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
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    GET(inx,pageSize);
})
function setPage(container, count, pageindex,pageSize,funcCode) {
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
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
            inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
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
//页面加载循环
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
        $(".table #table tbody").append("<tr id='"+data[i].id+"' ondblclick='editAssignment(this)'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div>"
                        + "</td><td style='text-align:left;'>"
                        + a
                        + "</td><td><span title='"+data[i].task_code+"'>"
                        + data[i].task_code
                        + "</span></td><td><span title='"+data[i].task_title+"'>"
                        + data[i].task_title
                        + "</span></td><td><span title='"+data[i].task_type_name+"'>"
                        + data[i].task_type_name
                        + "<span></td><td><span title='"+data[i].task_description+"'>"
                        + data[i].task_description
                        + "</span></td><td class='corp_code' data-code='"+data[i].corp_code+"'><span title='"+data[i].corp_name+"'>"
                        + data[i].corp_name
                        + "</span></td><td class='details'><a href='javascript:void(0)'>"
                        + "查看"
                        + "</a></td><td>"
                        + data[i].target_start_time
                        + "</td><td>"
                        + data[i].target_end_time
                        + "</td><td>"
                        + data[i].modifier
                        + "</td><td>"
                        +data[i].isactive
                        +"</td></tr>");
    }
    whir.loading.remove();//移除加载框
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
            $('#jurisdiction').append("<li id='compile' onclick='editAssignmentb();'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
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
    oc.postRequire("get","/task/list?pageNumber="+a+"&pageSize="+b
        +"&funcCode="+funcCode+"","","",function(data){
            console.log(data);
            if(data.code=="0"){
                $("#table tbody").empty();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                cout=list.pages;
                var list=list.list;
                superaddition(list,a);
                jumpBianse();
                setPage($("#foot-num")[0],cout,a,b,funcCode);
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
//get请求
GET(inx,pageSize);
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色 
         $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
         $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
         $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function(){
        var input=$(this).find("input")[0];
        var thinput=$("#table thead input")[0];
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
        $(window.parent.document).find('#iframepage').attr("src","/task/task_add.html");
    })
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            $("#page-wrapper").show();
            $("#content").hide();
            $("#details").hide();
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
        var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        console.log(left);
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
    //查看任务详情跳转查看员工页面
    $('.details').click(function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        var param={};
        var corp_code=$(this).parents('tr').find(".corp_code").attr("data-code");
        var task_code=$(this).parents('tr').find("td:eq(2) span").html();
        param["corp_code"]=corp_code;
        param["task_code"]=task_code;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/task/userlist","0",param,function(data){
            if(data.code=="0"){
                $('#details').show();
                $('#content').hide();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                console.log(list);
                $(".table #table_r tbody").empty();
                for(var i=0;i<list.length;i++){
                    var a=i+1;
                    $(".table #table_r tbody").append("<tr><td width='50px;' style='text-align: center;'>"
                                + a
                                + "</td><td>"
                                + list[i].user_name
                                + "</td><td>"
                                + list[i].task_status
                                + "</td><td>"
                                + list[i].real_start_time
                                + "</td><td>"
                                + list[i].real_end_time
                                +"</td></tr>");
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
    oc.postRequire("post","/task/search","0",param,function(data){
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
//弹框删除关闭
$("#delete").click(function(){
    $("#p").hide();
    $("#tk").hide();
    var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
    var params={};
    var list=[];
    for(var i=0;i<tr.length;i++){
        var id=$(tr[i]).attr("id");
        var corp_code=$(tr[i]).find(".corp_code").attr("data-code");
        var task_code=$(tr[i]).find("td:eq(2) span").html();
        var param1={"id":id,"corp_code":corp_code,"task_code":task_code};
        list.push(param1);
    }
    params["list"]=list;
    oc.postRequire("post","/task/delete","0",params,function(data){
        if(data.code=="0"){
            if(value==""&&filtrate==""){
               frame();
               $('.frame').html('删除成功');
               GET(pageNumber,pageSize);
            }else if(value!==""){
               frame();
               $('.frame').html('删除成功');
               param["pageNumber"]=pageNumber;
               POST(pageNumber,pageSize);
            }else if(filtrate==""){
               frame();
               $('.frame').html('删除成功');
               _param["pageNumber"]=pageNumber;
               filtrates(pageNumber,pageSize);
            }
        var thinput=$("#table thead input")[0];
        thinput.checked =false;
        }else if(data.code=="-1"){
            frame();
            $('.frame').html(data.message);
        }
    })
})
//删除弹框
function frame(){
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide(); 
    },2000); 
} 
//全选
function checkAll(name){
    var el=$("#table tbody input");
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
    var el=$("#table tbody input");
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
    oc.postRequire("post","/corp/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            console.log(message);
            $("#file_list_l ul").empty();
            for(var i=0;i<=message.length;i++){
                 $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            bianse();
            $("#file_list_r ul").empty();
        }else if(data.code=="-1"){
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
$("#file_submit").click(function(){
    var li=$("#file_list_r input[type='checkbox']:checked").parents("li");
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
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    oc.postRequire("post","/corp/exportExecl","0",param,function(data){
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
        }else if(data.code=="-1"){
            alert(data.message);
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
    var FileController = "/corp/addByExecl"; //接收上传文件的后台地址
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
        //筛选的keydow事件
        $('#sxk .inputs input').keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode == 13){
                getInputValue();
            }
        })
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
    getInputValue();
})
function getInputValue(){
    var input=$('#sxk .inputs input');
   inx=1;
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
    filtrates(inx,pageSize)
    if(num>0){
        filtrate="sucess";
    }else if(num<=0){
        filtrate="";
    }
}
//筛选发送请求
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/task/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息请重新搜索</p>");
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
    var inx= this.value.replace(/[^1-9]/g, '');
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

//编辑点击保存
$("#edit_save").click(function(){
    var _param={};
    var a=$('.xingming li');
    var user_codes="";
    var phone="";
    for(var i=0;i<a.length;i++){
        var u=$(a[i]).attr("data-code");
        var p=$(a[i]).attr("data-phone");
        if(i<a.length-1){
            user_codes+=u+",";
            phone+=p+",";
        }else{
             user_codes+=u;
             phone+=p;
        }     
    }
    console.log(user_codes);
    console.log(phone);
    var corp_code = $('#OWN_CORP').val();//公司编号
    var task_type_code = $('#task_type_code').val();//公司类型
    var task_title=$('#task_title_e').val();//任务名称
    var task_description=$("#task_describe").val();//任务描述
    var target_end_time=$("#target_end_time_e").val();//截止时间
    var target_start_time=$("#target_start_time_e").val();//开始时间
    var id=$('#task_id').val();//id名称
    if(task_type_code==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务类型不能为空"
        });
        return;
    }
    if(task_title==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务名称不能为空"
        });
        return;
    }
    if(task_description==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务描述不能为空"
        });
        return;
    }
    if(user_codes==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"员工不能为空"
        });
        return;
    }
    if(target_end_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"截止时间不能为空"
        });
        return;
    }
    if(target_start_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"开始时间不能为空"
        });
        return;
    }
    var task_code=$('#task_code_e').val();
    var isactive = "";//是否可用
    var input = $("#is_active")[0];
    if (input.checked == true) {
        isactive = "Y";
    } else if (input.checked == false) {
        isactive = "N";
    }
    _param["user_codes"]=user_codes;
    _param["phone"]=phone;
    _param["corp_code"]=corp_code;
    _param["task_type_code"]=task_type_code;
    _param["task_title"]=task_title;
    _param["task_description"]=task_description;
    _param["target_end_time"]=target_end_time;
    _param["target_start_time"]=target_start_time;
    _param["task_code"]=task_code;//任务编号
    _param["id"]=id;//id名称
    _param["isactive"]=isactive;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/task/edit","", _param, function(data) {
        if(data.code=="0"){
            $("#page-wrapper").hide();
            $("#content").show();
            $("#details").hide();
            whir.loading.remove();//移除加载框
            if(value==""&&filtrate==""){
                GET(inx,pageSize);
            }else if(value!==""){
                POST(inx,pageSize); 
            }else if(filtrate!==""){
                filtrates(inx,pageSize); 
            }
        }
    })
})
//编辑点击关闭
$("#edit_close").click(function(){
    $("#page-wrapper").hide();
    $("#content").show();
    $("#details").hide();
})