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
var titleArray=[];
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
//模拟select每页
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
                    param["funcCode"]=funcCode;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["pageSize"]=pageSize;
                    param["pageNumber"]=inx;
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    inx=1;
                    _param["pageSize"]=pageSize;
                    _param["pageNumber"]=inx;
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
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    param["searchValue"]="";
    GET(inx,pageSize);
})
function setPage(container, count, pageindex,pageSize) {
    count==0?count=1:'';
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
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["funcCode"]=funcCode;
        param["searchValue"]="";
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
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>")
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>")
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:50%;font-size: 15px;color:#999'>暂无内容</span>");
    }


    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
            })(c)
        }
        $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:center;'>"
            + a
            + "</td>" +
            TD +
            "</tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
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
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="output"){
            $("#more_down").append("<div id='leading_out'>导出</div>");
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into'>导入</div>");
        }
    }
};
function InitialState(){
    if(return_jump!==null){
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
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            param["searchValue"]="";
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
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            param["searchValue"]="";
            GET(inx,pageSize);
        }else if(value!==""){
            $("#search").val(value);
            POST(inx,pageSize);
        }else if(filtrate!==""){
            filtrates(inx,pageSize);
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
        titleArray=message.columns;
        jurisdiction(actions);
        jumpBianse();
        InitialState();
        tableTh();
    })
};
function tableTh(){ //table  的表头
    var TH="";
    for(var i=0;i<titleArray.length;i++){
        TH+="<th>"+titleArray[i].show_name+"</th>"
    }
    $("#tableOrder").after(TH);
};
qjia();
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    //oc.postRequire("get","/userOperation/list?pageNumber="+a+"&pageSize="+b
    //    +"&funcCode="+funcCode+"","","",function(data){
    oc.postRequire("post","/userOperation/search","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.list;
            var page_number = message.page_number;
            cout=message.pages;
            superaddition(list,page_number);
            jumpBianse();
            $("#end").attr("onclick","laydate({elem:'#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkEnd})");
            $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkStart})");
            setPage($("#foot-num")[0],cout,a,b,funcCode);
            setPage($("#foot-num")[0],cout,page_number,b,funcCode);
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
        $(window.parent.document).find('#iframepage').attr("src","/goods/xiuda_add.html");
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
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    inx=1;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    //param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
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
    //param["funcCode"]=funcCode;
    POST(inx,pageSize);
})
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/userOperation/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.list;
            cout=message.pages;
            var pageNum = message.page_number;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
            $("#end").attr("onclick","laydate({elem:'#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkEnd})");
            $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkStart})");
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
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
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    for(var i=tr.length-1,ID="";i>=0;i--){
        var r=$(tr[i]).attr("id");
        if(i>0){
            ID+=r+",";
        }else{
            ID+=r;
        }
    }
    var params={};
    params["id"]=ID;
    oc.postRequire("post","/userOperation/delete","0",params,function(data){
        if(data.code=="0"){
            if (value == "" && filtrate == "") {
                frame().then(function(){
                    GET(pageNumber, pageSize);
                });
                $('.frame').html('删除成功');
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["funcCode"]=funcCode;
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
            var thinput=$("thead input")[0];
            thinput.checked =false;
        }else if(data.code=="-1"){
            frame();
            $('.frame').html(data.message);
        }
    })
})
//删除弹框
function frame(){
    var def= $.Deferred();
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
        def.resolve();
    },2000);
    return def;
}
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
$("#more_down").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    var left=($(window).width()-$(".file").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".file").height())/2;//弹框定位的top值
    $(".file").css({"left":+left+"px","top":+tp+"px"});
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/list/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            $("#file_list_l ul").empty();
            for(var i=0;i<message.length;i++){
                $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                    +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            bianse();
            $("#file_list_r ul").empty();
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//移除加载框
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
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/userOperation/exportExecl","0",param,function(data){
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
        whir.loading.remove();//移除加载框
    })
})
//导出关闭按钮
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
})
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
                    +"<input type='text' id='start' class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkStart})\">"
                    +"<label class='tm20'>至</label>"
                    +"<input type='text' id='end' class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkEnd})\">"
                    +"</li>";
            }else if(filter[i].type=='number'){
                li+="<li class='isActive_select' id='"
                    +filter[i].col_name
                    +"'><label>"
                    +filter[i].show_name
                    +"</label>"
                    +"<input style='width: 66px' type='text' id='isactive' readonly>"
                    +" <ul class='isActive_select_down'style='display:none;width:41px;left:65px'><li style='text-indent: 5px'>>=</li><li  style='text-indent: 5px'><=</li><li  style='text-indent: 5px'>介于</li><li style='text-indent: 5px'>等于</li><li style='text-indent: 5px'>全部</li></ul>"
                    +"</li>";
            }
        }
        $("#sxk .inputs ul").html(li);
        if(filtrate!==""){
            $(".sxk").slideDown();
            for(var i=0;i<list.length;i++){
                if($("#"+list[i].screen_key).parent("li").attr("class")!=="isActive_select"){
                    $("#"+list[i].screen_key).val(list[i].screen_value);
                }else if($("#"+list[i].screen_key).parent("li").attr("class")=="isActive_select"){
                    var svalue=$("#"+list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+list[i].screen_value+"']").html();
                    $("#"+list[i].screen_key).val(svalue);
                }
            }
        }
        filtrateDown();
    }
});
//筛选的keydow事件
$('#sxk .inputs').on("keydown","input",function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        var test_input=$('#isactive').nextAll('input');
        for(var i=0;i<test_input.length;i++){
            var reg=/^[0-9]*$/g;
            var input_value=$(test_input[i]).val().trim();
            var test_value=reg.test(input_value);
            if(!test_value){
                frame();
                $('.frame').html('请输入数字');
                return
            }
        }
        getInputValue();
    }
})
function filtrateDown(){
    //筛选select框
    $(".isActive_select input:nth-child(2)").click(function (){
        var ul=$(this).nextAll("ul.isActive_select_down");
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
function checkStart(data){
    $("#end").attr("onclick","laydate({elem:'#end',min:'"+data+"',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD',choose:checkEnd})");
};
function checkEnd(data){
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '"+data+"',istime: true, format: 'YYYY-MM-DD',choose:checkStart})");
};
//筛选查找
$("#find").click(function(){
    var test_input=$('#isactive').nextAll('input');
    for(var i=0;i<test_input.length;i++){
        var reg=/^[0-9]*$/g;
        var input_value=$(test_input[i]).val().trim();
        var test_value=reg.test(input_value);
        if(!test_value){
            frame();
            $('.frame').html('请输入数字');
            return
        }
    }
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
        var screen_key="";
        var screen_value={};
        if($(input[i]).attr("class")=="isActive_select"){
            screen_key=$(input[i]).attr("id");
            switch ($(input[i]).find("input").val()){
                case '>=':screen_value['type']='gt';screen_value['value']=$(input[i]).find("input").next().val();break;
                case '<=':screen_value['type']='lt';screen_value['value']=$(input[i]).find("input").next().val();break;
                case '介于':screen_value['type']='between';_value();break;
                case '等于':screen_value['type']='eq';screen_value['value']=$(input[i]).find("input").next().val();;break;
                case '全部':screen_value['type']='all';screen_value['value']='';;break;
                case '':screen_value['type']='all';screen_value['value']='';;break;
            }
            function _value(){
                screen_value['value']={};
                var between_value=$(input[i]).find("input").nextAll();
                screen_value['value'].start=$(between_value[0]).val();
                screen_value['value'].end=$(between_value[1]).val();
            }
        }else if($(input[i]).attr("class")=="created_date"){
            var start=$('#start').val();
            var end=$('#end').val();
            screen_key=$(input[i]).attr("id");
            screen_value={"start":start,"end":end};
        }else{
            screen_value=$(input[i]).find("input").val().trim();
            screen_key=$(input[i]).find("input").attr("id");
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
    oc.postRequire("post","/userOperation/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.list;
            cout=message.pages;
            var pageNum = message.page_number;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
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
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["funcCode"]=funcCode;
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
//刷新页面
function reload(){
    document.location.reload();
}
$(".icon-ishop_6-07").parent().click(function () {
    window.location.reload();
});
