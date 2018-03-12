/**
 * Created by hu.x on 2017/4/27.
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
function setPage(container, count, pageindex,pageSize,funcCode,total) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
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
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
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
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        var day="";
        if(data[i].approved_type=="Y"){
            day="每年"+data[i].approved_cycle.split("-")[0]+"月"+data[i].approved_cycle.split("-")[1]+"日"
        }else if(data[i].approved_type=="M"){
            day="每月"+data[i].approved_cycle+"日"
        }
        var activename=data[i].isactive=='Y'?'是':'否'
        var arr = [];
        arr.push(data[i].amt_trade);
        arr.push(data[i].num_trade);
        arr.push(data[i].piece_sales);//总客单价
        arr.push(data[i].vip_amt_trade);
        arr.push(data[i].vip_num_trade);
        arr.push(data[i].vip_piece_sales);
        arr.push(data[i].vip_sales_scale);
        arr.push(data[i].nvip_amt_trade);
        arr.push(data[i].ovip_amt_trade);
        arr.push(data[i].sales_scale);
        arr.push(data[i].vip_have_trade);
        arr.push(data[i].vip_have_trades);
        arr.push(data[i].ovip_num_trade);
        $(".table tbody").append("<tr id='"+data[i].store_id+"'>"
            //"<td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            //+ i
            //+ 1
            //+ "'/><label for='checkboxTwoInput"
            //+ i
            //+ 1
            //+ "'></label></div>"
            //+ "</td><td style='text-align:left;'>"
            //+ a
            //+ "</td>"
            +"<td ><span title='"+data[i].store_id+"' class='store_id'>"+data[i].store_id+"</span></td>"
            +"<td ><span title='"+data[i].store_name+"' class='store_name'>"+data[i].store_name+"</span></td>"
            +"<td ><span title='"+data[i].store_group+"'>"+data[i].store_group+"</span></td>"
            +"<td ><span title='"+data[i].store_area+"'>"+data[i].store_area+"</span></td>"
            +"<td ><span title='"+data[i].vip_all+"'>"+data[i].vip_all+"</span></td>"
            +"<td ><span title='"+data[i].un_assort_vip+"'>"+data[i].un_assort_vip+"</span></td>"  //已分配会员
            +"<td ><span title='"+data[i].new_vip+"'>"+data[i].new_vip+"</span></td>"
            +"<td ><span title='"+data[i].new_fans+"'>"+data[i].new_fans+"</span></td>" //新增粉丝
            +"<td ><span title='"+data[i].sum_wechat_vip+"'>"+data[i].sum_wechat_vip+"</span></td>"
            +"<td ><span title='"+data[i].wechat_vip+"'>"+data[i].wechat_vip+"</span></td>"
            +"<td ><span title='"+data[i].bind_wechat_vip+"'>"+data[i].bind_wechat_vip+"</span></td>"
            +"<td style='color: #5f8bc8;cursor: pointer' onclick='showPopup(this)' data-value="+ JSON.stringify(arr) +">"+"详情"+"<span class='icon-ishop_8-03' style='color:#5f8bc8'></span></td>"
            +"</tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
}
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    param.list="";
    oc.postRequire("post","/vipAnalysis/storeKpi","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            $(".table p").remove();
            var message=JSON.parse(data.message);
            cout=Number(message.page_count);
            var pageNum = Number(message.page_num)==0?1:Number(message.page_num);
            var total = Number(message.count);
            allData=Number(message.count);
            var list=message.message;
            superaddition(list,pageNum);
            jumpBianse();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    param.type="store"
    oc.postRequire("post","/vipAnalysis/piKSearch","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.message;
            cout=Number(message.page_count);
            var pageNum = Number(message.page_num)==0?1:Number(message.page_num);
            var total = Number(message.count);
            allData=Number(message.count);
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
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
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
            //showPopup(id);
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
                //$(window.parent.document).find('#iframepage').attr("src","/vip/approval_edit.html");
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
    //$(".table tbody tr").click(function(){
    //    var input=$(this).find("input")[0];
    //    var thinput=$("thead input")[0];
    //    $(this).toggleClass("tr");
    //    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
    //        input.checked = true;
    //        $(this).addClass("tr");
    //    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
    //        if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
    //            thinput.checked=false;
    //        }
    //        input.checked = false;
    //        $(this).removeClass("tr");
    //    }
    //});
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        sessionStorage.removeItem("id");
        //$(window.parent.document).find('#iframepage').attr("src","/vip/approval_add.html");
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
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
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
    })
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
    oc.postRequire("post","/approved/delete","0",param,function(data){
        if(data.code=="0"){
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
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["searchValue"]="";
        $("#search").val("");
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
});
//鼠标按下时触发的收索
$("#search").keydown(function() {
    delete _param.screen;
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        value=$("#search").val().replace(/\s+/g,"");
        $("#search").blur();
        if(value!=""){
            inx=1;
            param["searchValue"]=value;
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
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
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
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
};
function checkEnd(data){
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '"+data+"',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
};
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
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
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
    oc.postRequire("post","/vipAnalysis/storeKpi","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.message;
            cout=Number(message.page_count);
            var pageNum = Number(message.page_num)==0?1:Number(message.page_num);
            var total = Number(message.count);
            allData=Number(message.count);
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
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
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
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["funcCode"]=funcCode;
        param["searchValue"]="";
        GET(inx,pageSize);
    }
}
function showPopup(dom){
    var dataVal = JSON.parse($(dom).attr('data-value'));
    var $popup = $('#table_achievement_area');
    //获取关闭按钮
    var $closeBtn = $popup.find('.modify_footer_l');
    //加载等待框
    //获取变动参数的span
    var $doms = $popup.find('ul span');
    //获取店铺和区域
    $('#achievement_shopName').text($(dom).parent("tr").find(".store_name").text()==""?"暂无店铺":$(dom).parent("tr").find(".store_name").text());
    $('#achievement_shopName').prop("title",$(dom).parent("tr").find(".store_name").text());
    $('#achievement_area').text($(dom).parent("tr").find(".store_id").text()==""?"暂无店铺编号":$(dom).parent("tr").find(".store_id").text());
    $('#achievement_area').prop("title",$(dom).parent("tr").find(".store_id").text());
    //打开窗体
    $popup.show();
    //赋值
    popupVal($doms,dataVal);
    //关闭
    $closeBtn.click(function(){
        $popup.hide();
    });
}
function popupVal($doms,dataVal){
    //数据
    for(var i = 0; i<dataVal.length;i++){
        $($doms[i]).text(dataVal[i])
    }
}

//导出拉出list
$("#more_down").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    var left=($(window).width()-$(".file").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".file").height())/2;//弹框定位的top值
    $(".file").css("position","fixed");
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
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
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
});
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
$("#hide_export").click(function(){
    $("#export_list").hide();
    $("#p").hide();
});
//导出提交的
$("#file_submit").click(function(){
    //$("#export_list").show();
    //whir.loading.add("",0.5);//加载等待框
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var list_html="";
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    var allPage=Math.ceil(allData/2000);
    var param={};
    var tablemanager=[];
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();
    $(".file").hide();
    $("#export_list_all ul").html("");
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*2000 + 1;
        var end_num="";
        if (allData < a*2000 ){
            end_num = allData
        }else{
            end_num = a*2000
        }
        list_html+= '<li>'
            +'<span style="float: left">店铺VIP经营报表('+start_num+'~'+end_num+')</span>'
            +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
            +'<span style="margin-right:10px;" class="state"></span>'
            +'</li>'
    }
    $("#export_list_all ul").html(list_html);
    $("#export_list").show();
    $("#export_list_all").scrollTop(0);
    $("#export_list_all .export_list_btn").click(function () {
        if($(this).hasClass("btn_active")){
            return
        }
        var self=$(this);
        var page=$(this).attr("data-page");
        $(this).next().text("导出中...");
        $(this).addClass("btn_active");
        param["pageNumber"]=page;
        param["pageSize"]="2000";
        param["tablemanager"]=tablemanager;
        param["searchValue"]=value;
        if(filtrate==""){
            param["list"]="";
        }else if(filtrate!==""){
            param["list"]=list;
        }
        oc.postRequire("post","/vipAnalysis/storeExportExecl","0",param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var path=message.path;
                path=path.substring(1,path.length-1);
                self.attr("data-path",path);
                self.next().text("导出完成");
                self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                self.css("backgroundColor","#637ea4");
            }else if(data.code=="-1"){
                self.removeClass("btn_active");
                self.next().text("导出失败");
            }
        },function(data){
            if(data.statusText=="timeout"){
                self.removeClass("btn_active");
                self.next().text("导出超时");
                art.dialog({
                    time: 3,
                    lock: true,
                    cancel: false,
                    content: "请稍后至下载列表查看"
                });
            }
        })
    });
});
$("#to_zip").click(function(){
    var a=$("#export_list_all ul li a");
    var URL="";
    var param={};
    if(a.length==0){
        frame();
        $('.frame').html("请先导出文件");
        return;
    }
    for(var i=a.length-1;i>=0;i--){
        if(i>0){
            URL+=$(a[i]).attr("href")+","
        }else{
            URL+=$(a[i]).attr("href");
        }
    }
    param.url=URL;
    param.name="店铺VIP经营报表";
    whir.loading.add("","0.5");
    oc.postRequire("post","/vip/exportZip", "",param, function(data){
        if(data.code=="0"){
            var path=JSON.parse(data.message).path;
            path=path.substring(1,path.length-1);
            $("#download_all a").prop("href","/"+path);
            $("#p").css("zIndex","789");
            whir.loading.remove();
            $("#download_all").show();
        }
        if(data.code=="-1"){
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "操作失败"
            });
        }
    })
});
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
});
$("#cancel_download,#X_download").click(function(){
    $("#p").css("zIndex","787");
    $("#download_all").hide();
    $("#download_all a").removeProp("href");
});