/**
 * Created by Administrator on 2017/6/23.
 */
var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageNumber=1;//删除默认的第一行
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var allData="";//所有的结果
var filtrate="";//筛选的定义的值
var titleArray=[];
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
//模仿select
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
                pageSize=$(this).attr('id');
                if(value==""&&filtrate==""){
                    inx=1;
                    param["page_num"]=inx;
                    param["page_size"]=pageSize;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["page_num"]=inx;
                    param["page_size"]=pageSize;
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    inx=1;
                    _param["page_num"]=inx;
                    _param["page_size"]=pageSize;
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
});
$("#pack_up").click(function(){//点击收回 取消下拉框
    $(".sxk").slideUp();
});
//点击清空  清空input的value值
$("#empty").click(function(){
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        if($(input[i]).parent().hasClass("isActive_select")){
            input[i].value="全部";
        }else{
            input[i].value="";
        }
        $(input[i]).attr("data-code","");
    }
    value="";
    filtrate="";
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    param["page_num"]=inx;
    param["page_size"]=pageSize;
    param["searchValue"]="";
    GET(inx,pageSize);
});
function setPage(container, count, pageindex,pageSize,funcCode,total) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
    var total=total;
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
        $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
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
        param["page_num"]=inx;
        param["page_size"]=pageSize;
        param["searchValue"]="";
        GET(a,b);
    }else if (value!==""){
        param["page_num"] = a;
        param["page_size"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["page_num"] = a;
        _param["page_size"] = b;
        filtrates(a,b);
    }
}
function superaddition(data,num){//页面加载循环
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
            if(i==4){
                $($(".table tbody tr")[i]).append("<td colspan='"+len+"'>暂无内容</td>");
            }else {
                $($(".table tbody tr")[i]).append("<td colspan='"+len+"'></td>");
            }
        }
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
                if(code=="approved_cycle"){
                    var day="";
                    if(data[i].approved_type=="Y"){
                        day="每年"+data[i].approved_cycle.split("-")[0]+"月"+data[i].approved_cycle.split("-")[1]+"日"
                    }else if(data[i].approved_type=="M"){
                        day="每月"+data[i].approved_cycle+"日"
                    }
                    TD+="<td><span title='"+day+"'>"+day+"</span></td>";
                }else if(code=="isactive"){
                    var activename=data[i].isactive=='Y'?'是':'否';
                    TD+="<td><span title='"+activename+"'>"+activename+"</span></td>";
                }else if(code=="approved_detail"){
                    TD+="<td><a href='javascript:void(0)' class='detail'>详情</a></td>";
                }else{
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }

            })(c)
        }
        var day="";
        if(data[i].approved_type=="Y"){
            day="每年"+data[i].approved_cycle.split("-")[0]+"月"+data[i].approved_cycle.split("-")[1]+"日"
        }else if(data[i].approved_type=="M"){
            day="每月"+data[i].approved_cycle+"日"
        }
        $(".table tbody").append("<tr id='"+data[i].id+"' data-corp_code='"+data[i].corp_code+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:left;'>"
            + a
            + "</td>"
            +TD
                //+"<td>"+data[i].approved_name+"</td>"
                //+"<td>"+day+"</td>"
                //+"<td>"+data[i].remarks+"</td>"
                //+"<td>"+data[i].created_date+"</td>"
                //+"<td>"+data[i].modified_date+"</td>"
                //+"<td>"+activename+"</td>"
            +"</tr>");

    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
}
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/wxTemplate/search","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var total = list.total;
            allData=list.total;
            var list=list.list;
            $(".table p").remove();
            superaddition(list,pageNum);
            jumpBianse();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/wxTemplate/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var total = list.total;
            allData=list.total;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
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
            filtrate="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
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
        }else if(actions[i].act_name=="output"){
            $("#more_down").append("<div id='leading_out'>导出</div>");
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into'>导入</div>");
        }
    }
}
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    });
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        var return_jump={};//定义一个对象
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["filtrate"]=filtrate;//筛选的值
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        return_jump["_param"]=JSON.stringify(_param) //筛选定义的值
        return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        if(id == "" || id == undefined){
            return ;
        }else{
            $(window.parent.document).find('#iframepage').attr("src","/system/Wechat_templates_adit.html");
        }
    });
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            var id=$(tr).attr("id");
            var store_id=$(tr).attr("data-storeId");
            var store_code=$(tr).attr("data-storecode");
            var corp_code=$(tr).attr("data-code");
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["filtrate"]=filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(_param);//筛选定义的值
            return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.setItem("id",id);
            if(id == "" || id == undefined){
                return ;
            }else{
                $(window.parent.document).find('#iframepage').attr("src","/system/Wechat_templates_adit.html");
            }
        }else if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(tr.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    });
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
    });
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        sessionStorage.removeItem("id");
        $(window.parent.document).find('#iframepage').attr("src","/system/Wechat_templates_add.html");
    });
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
    })
}
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
            param["page_num"]=inx;
            param["page_size"]=pageSize;
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
            param["page_num"]=inx;
            param["page_size"]=pageSize;
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
}
function tableTh(){ //table  的表头
    var TH="";
    for(var i=0;i<titleArray.length;i++){
        TH+="<th>"+titleArray[i].show_name+"</th>"
    }
    $("#tableOrder").after(TH);
}
qjia();
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
}
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
}
$("#cancel,#X").click(function(){
    $("#p").hide();
    $("#tk").hide();
});
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
    var param={};
    param["id"]=ID;
    oc.postRequire("post","/wxTemplate/delete","0",param,function(data){
        if(data.code=="0"){
            if (value == "" && filtrate == "") {
                frame().then(function(){
                    GET(pageNumber, pageSize);
                });
                $('.frame').html('删除成功');
                param["page_num"]=inx;
                param["page_size"]=pageSize;
                param["searchValue"]="";
            } else if (value !== "") {
                frame().then(function(){
                    POST(pageNumber, pageSize);
                });
                $('.frame').html('删除成功');
                param["page_num"]=pageNumber;
            } else if (filtrate !== "") {
                frame().then(function(){
                    filtrates(pageNumber, pageSize);
                });
                $('.frame').html('删除成功');
                _param["page_num"]=pageNumber;
            }
            var thinput=$("thead input")[0];
            thinput.checked =false;
        }else if(data.code=="-1"){
            frame();
            $('.frame').html(data.message);
        }
    })
});
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    //window.location.reload();
    inx=1
    if (value == "" && filtrate == "") {
        param["page_num"]=inx;
        param["page_size"]=pageSize;
        param["searchValue"]="";
        $("#search").val("");
        GET(inx, pageSize);
    } else if (value !== "") {
        param["page_size"] = pageSize;
        param["page_num"]=inx;
        POST(inx, pageSize);
    } else if (filtrate !== "") {
        _param["page_size"] = pageSize;
        _param["page_num"]=inx;
        filtrates(inx, pageSize);
    }
});
//鼠标按下时触发的收索
$("#search").keydown(function() {
    delete _param.screen;
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        value=$("#search").val().replace(/\s+/g,"");
        if(value!=""){
            inx=1;
            param["searchValue"]=value;
            param["page_num"]=inx;
            param["page_size"]=pageSize;
            POST(inx,pageSize);
        }else if(value==""){
            param["searchValue"]=value;
            GET(inx,pageSize);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    delete _param.screen;
    value=$("#search").val().replace(/\s+/g,"");
    if(value!==""){
        inx=1;
        param["searchValue"]=value;
        param["page_num"]=inx;
        param["page_size"]=pageSize;
        POST(inx,pageSize);
    }else{
        param["searchValue"]=value;
        GET(inx,pageSize);
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
function checkStart(data){
    $("#end").attr("onclick","laydate({elem:'#end',min:'"+data+"',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
}
function checkEnd(data){
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '"+data+"',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
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
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
                }
                ul+="</ul>";
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' value='全部' readonly>"+ul+"</li>"
            }else if(filter[i].type=="date"){
                li+="<li class='created_date' id='"
                    +filter[i].col_name
                    +"'><label>"
                    +filter[i].show_name
                    +"</label>"
                    +"<input type='text' id='start' class='time_data laydate-icon' onClick=\"laydate({elem: '#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})\">"
                    +"<label class='tm20'>至</label>"
                    +"<input type='text' id='end' class='time_data laydate-icon' onClick=\"laydate({elem: '#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})\">"
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
                if($("#"+list[i].screen_key).attr("class")=="created_date"){
                    $("#start").val(list[i].screen_value.start);
                    $("#end").val(list[i].screen_value.end);
                }
            }
        }
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
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "" && filtrate == "") {
                param["page_num"]=inx;
                param["page_size"]=pageSize;
                param["searchValue"]="";
                GET(inx, pageSize);
            } else if (value !== "") {
                param["page_size"] = pageSize;
                param["page_num"]=inx;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["page_size"] = pageSize;
                _param["page_num"]=inx;
                filtrates(inx, pageSize);
            }
        }
    }
});
//点击清空  清空input的value值
$("#empty").click(function(){
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        if($(input[i]).parent().hasClass("isActive_select")){
            input[i].value="全部";
        }else{
            input[i].value="";
        }
        $(input[i]).attr("data-code","");
    }
    value="";
    filtrate="";
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    param["page_num"]=inx;
    param["page_size"]=pageSize;
    param["funcCode"]=funcCode;
    param["searchValue"]="";
    GET(inx,pageSize);
});
//筛选查找
$("#find").click(function(){
    getInputValue();
});
//筛选发送请求
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/wxTemplate/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.list);
            var list=message.list;
            cout=message.pages;
            var pageNum = message.pageNum;
            var total = message.total;
            allData=message.total;
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
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
function getInputValue(){
    var input=$('#sxk .inputs>ul>li');
    inx=1;
    _param["page_num"]=inx;
    _param["page_size"]=pageSize;
    _param["funcCode"]=funcCode;
    var num=0;
    list=[];//定义一个list
    for(var i=0;i<input.length;i++){
        var screen_key="";
        var screen_value={};
        if($(input[i]).attr("class")=="created_date"){
            var start=$('#start').val();
            var end=$('#end').val();
            screen_key=$(input[i]).attr("id");
            screen_value={"start":start,"end":end};
        }else if($(input[i]).attr("class")=="isActive_select"){
            screen_key=$(input[i]).find("input").attr("id");
            screen_value=$(input[i]).find("input").attr("data-code");
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
    if(num>0){
        filtrate="sucess";
        filtrates(inx,pageSize);
    }else if(num<=0){
        filtrate="";
        param["page_num"]=inx;
        param["page_size"]=pageSize;
        param["funcCode"]=funcCode;
        param["searchValue"]="";
        GET(inx,pageSize);
    }
}
$("#table").on("click",".detail",function(){
    var approved_id=$(this).parents("tr").attr("id");
    var corp_code=$(this).parents("tr").attr("data-corp_code");
    var approved_detail={"approved_id":approved_id,"corp_code":corp_code};
    sessionStorage.setItem("approved_detail",JSON.stringify(approved_detail));
    $(window.parent.document).find('#iframepage').attr("src","/vip/approval_detail.html");
});