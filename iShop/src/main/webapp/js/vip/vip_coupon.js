var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var titleArray=[];
var allData="";//所有的结果
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
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
                    param["funcCode"]=funcCode;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["page_size"]=pageSize;
                    param["page_num"]=inx;
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
})
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
    checkStart("1900-01-01 00:00:00","reset");
    checkEnd("2099-12-31 23:59:59","reset");
    param["page_num"]=inx;
    param["page_size"]=pageSize;
    param["funcCode"]=funcCode;
    param["searchValue"]="";
    GET(inx,pageSize);
});
function setPage(container, count, pageindex,pageSize,funcCode,total){
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
    if (pageindex == count||count==0) {
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
            if (inx == count||count==0) {
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
        param["funcCode"]=funcCode;
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
        //console.log(titleArray)
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                if(titleArray[j].show_name=="原会员使用"||titleArray[j].show_name=="是否过期"){
                    var val=data[i][code]=="Y"?"是":data[i][code]=="N"?"否":'';
                    TD+="<td><span title='"+val+"'>"+val+"</span></td>";
                }else{
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }
            })(c)
        }
        $(".table tbody").append("<tr data-coupon='"+JSON.stringify(data[i])+"' id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
            "<td><a href='javascript:void(0)' class='details'>查看</a></td>"+
            "</tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
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
            param["funcCode"]=funcCode;
            param["searchValue"]="";
            param["corp_code"]="C10222";
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
}function tableTh(){ //table  的表头
    var TH="";
    var reg=/VERIFYED_ORDER|TOTAL_TRADE|DEDUCT_TRADE|NUM_SALES|VERIFYED_STORE|EMP_ID|VERIFYED_TIME|V_VIP_CARD_NO|V_VIP_PHONE|VOU_SOURCE/;
    function clearTh(){
        for(var i=0;i<titleArray.length;i++){
            if(reg.test(titleArray[i].column_name)) {
                titleArray.splice(i, i + 1);
                clearTh()
            }
        }
    }
    clearTh();
   for(var t=0;t<titleArray.length;t++){
       TH+="<th>"+titleArray[t].show_name+"</th>";
   }
    TH+="<th>券使用详情</th>";
    $("#tableOrder").after(TH);
}
qjia();
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/VipBusiness/couponSearch","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.coupon_list;
            cout=Number(message.pages);
            var pageNum = Number(message.page_num);
            var total = Number(message.count);
            allData=message.count;
            superaddition(list,pageNum);
            jumpBianse();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
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
        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group_add.html");
    });
    //双击跳转
    //$(".table tbody tr").dblclick(function(){
    //    var id=$(this).attr("id");
    //    var return_jump={};//定义一个对象
    //    return_jump["inx"]=inx;//跳转到第几页
    //    return_jump["value"]=value;//搜索的值;
    //    return_jump["filtrate"]=filtrate;//筛选的值
    //    return_jump["param"]=JSON.stringify(param);//搜索定义的值
    //    return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
    //    return_jump["list"]=list;//筛选的请求的list;
    //    return_jump["pageSize"]=pageSize;//每页多少行
    //    sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
    //    sessionStorage.setItem("id",id);
    //    var screenArr = {};
    //    if($('#sxk').css('display')=='block'){
    //        $('#sxk input').each(function(){
    //            if($(this).val().trim()!=''){
    //                var id ='#'+ $(this).attr('id');
    //                var val = $(this).val();
    //                screenArr[id] = val;
    //            }
    //        });
    //        console.log(screenArr);
    //        sessionStorage.setItem("screenArr",JSON.stringify(screenArr));
    //    }
    //    if(id == "" || id == undefined){
    //        return ;
    //    }else{
    //        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group_edit.html");
    //    }
    //})
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            var id=$(tr).attr("id");
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
            $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group_edit.html");
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
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    value=this.value.replace(/\s+/g,"");
    inx=1;
    param["page_num"]=inx;
    param["page_size"]=pageSize;
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
    param["page_num"]=inx;
    param["page_size"]=pageSize;
    //param["funcCode"]=funcCode;
    POST(inx,pageSize);
});
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/VipBusiness/couponSearch","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.coupon_list;
            cout=Number(message.pages);
            var pageNum = Number(message.page_num);
            var total = Number(message.count);
            allData=message.count;
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
                if($(input[i]).parent().hasClass("isActive_select")){
                    input[i].value="全部";
                }else{
                    input[i].value="";
                }
            }
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
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
    oc.postRequire("post","/vipGroup/delete","0",params,function(data){
        if(data.code=="0"){
            if(value==""&&filtrate==""){
                frame().then(function(){
                    GET(inx,pageSize);
                });
                $('.frame').html('删除成功');
                param["page_num"]=inx;
                param["page_size"]=pageSize;
                param["funcCode"]=funcCode;
                param["searchValue"]="";
            }else if(value!==""){
                frame().then(function(){
                    POST(inx,pageSize);
                });
                $('.frame').html('删除成功');
                param["page_num"]=pageNumber;
            }else if(filtrate!==""){
                frame().then(function(){
                    filtrates(inx,pageSize);
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
        def.resolve()
    },2000);
    return def
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
//导出关闭按钮
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
    $.unblockUI({ fadeOut: 0 });
});
//导出拉出list
$("#more_down").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    $.blockUI({ message:null,overlayCSS: { cursor:"default",opacity:"0.1" },fadeIn:0 });
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
            alert(data.message);
        }
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
    var list_html="";
    var allPage=Math.ceil(allData/20000);
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
    $(".file").hide();
    $("#export_list_all ul").html("");
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*20000 + 1;
        var end_num="";
        if (allData < a*20000 ){
            end_num = allData
        }else{
            end_num = a*20000
        }
        list_html+= '<li>'
            +'<span style="float: left">优惠券查询('+start_num+'~'+end_num+')</span>'
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
        param["page_num"]=page;
        param["page_size"]="20000";
        param["tablemanager"]=tablemanager;
        param["searchValue"]=value;
        param["corp_code"]="C10222";
        if(filtrate==""){
            param["list"]="";
        }else if(filtrate!==""){
            param["list"]=list;
        }
        oc.postRequire("post","/VipBusiness/couponSearchExport","0",param,function(data){
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
        })
    });
});
$("#hide_export").click(function(){
    $("#export_list").hide();
    $("#p").hide();
    $.unblockUI({ fadeOut: 0 });
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
    param.name="优惠券查询";
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
$("#cancel_download,#X_download").click(function(){
    $("#p").css("zIndex","787");
    $("#download_all").hide();
    $("#download_all a").removeProp("href");
});
//点击导入
//$("#more_down").on("click","#guide_into",function(){
//    var l=$(window).width();
//    var h=$(document.body).height();
//    $("#p").show();
//    $("#p").css({"width":+l+"px","height":+h+"px"});
//    $('.file').hide();
//    $(".into_frame").show();
//});
//导入关闭按钮
$("#x1").click(function(){
    $("#p").hide();
    $(".into_frame").hide();
})
//上传文件
//function UpladFile() {
//    whir.loading.add("",0.5);//加载等待框
//    var fileObj = document.getElementById("file").files[0];
//    var FileController = "/area/addByExecl"; //接收上传文件的后台地址
//    var form = new FormData();
//    form.append("file", fileObj); // 文件对象
//    // XMLHttpRequest 对象
//    var xhr = null;
//    if (window.XMLHttpRequest) {
//        xhr = new XMLHttpRequest();
//    } else {
//        xhr = new ActiveXObject('Microsoft.XMLHTTP');
//    }
//    xhr.onreadystatechange = function() {
//        if (xhr.readyState === 4) {
//            if (xhr.status === 200) {
//                doResult(xhr.responseText);
//            } else {
//                console.log('服务器返回了错误的响应状态码');
//                $('#file').val("");
//                whir.loading.remove();//移除加载框
//            }
//        }
//    }
//    function doResult(data) {
//        var data=JSON.parse(data);
//        if(data.code=="0"){
//            alert('导入成功');
//            window.location.reload();
//        }else if(data.code=="-1"){
//            alert("导入失败"+data.message);
//        }
//        $('#file').val("");
//        whir.loading.remove();//移除加载框
//    }
//    xhr.open("post", FileController, true);
//    xhr.onload = function() {
//        // alert("上传完成!");
//    };
//    xhr.send(form);
//    $("#p").hide();
//    $(".into_frame").hide();
//}
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
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly value='全部'>"+ul+"</li>"
            }else if(filter[i].type=="date"){
                var start="#start"+i;
                var end="#end"+i;
                li+="<li class='created_date' id='"
                    +filter[i].col_name
                    +"'><label>"
                    +filter[i].show_name
                    +"</label>"
                    +"<input type='text' id=start"+i+" class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '"+start+"',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})\">"
                    +"<label class='tm20'>至</label>" +
                    "<input style='margin-left: 0' type='text' id=end"+i+" class='time_data laydate-icon' autocomplete='off' onClick=\"laydate({elem: '"+end+"',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})\">"
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
    });
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
    _param["page_num"]=inx;
    _param["page_size"]=pageSize;
    _param["funcCode"]=funcCode;
    var num=0;
    list=[];//定义一个list
    for(var i=0;i<input.length;i++){
        var screen_key=$(input[i]).find("input").attr("id");
        var screen_value;
        if($(input[i]).attr("class")=="isActive_select"){
            screen_value=$(input[i]).find("input").attr("data-code");
        }else if($(input[i]).attr("class")=="created_date"){
            var start= $(input[i]).children("input").eq(0).val();
            var end=$(input[i]).children("input").eq(1).val();
            screen_key=$(input[i]).attr("id");
            screen_value={"start":start,"end":end};
        }else{
            screen_key=$(input[i]).find("input").attr("id");
            screen_value=$(input[i]).find('input').val().trim();
        }
        if(screen_value!="" && screen_value[start]!=="" && screen_value[end]!==""){
            num++;
        }
        var param1={"screen_key":screen_key,"screen_value":screen_value};
        list.push(param1);
    }
    _param["list"]=list;
    value="";//把搜索滞空
    $("#search").val("");
    if(num>0){
        filtrate="success";
        filtrates(inx,pageSize);
    }else if(num<=0){
        filtrate="";
        GET();
    }
}
//筛选发送请求
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    _param["corp_code"]="C10222";
    oc.postRequire("post","/VipBusiness/couponSearch","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.coupon_list;
            cout=Number(message.pages);
            var pageNum = Number(message.page_num);
            var total = Number(message.count);
            allData=message.count;
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
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
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
                param["page_num"]=inx;
                param["page_size"]=pageSize;
                param["funcCode"]=funcCode;
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
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    //window.location.reload();
    inx=1;
    if (value == "" && filtrate == "") {
        param["page_num"]=inx;
        param["page_size"]=pageSize;
        param["funcCode"]=funcCode;
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
function getScreenVal(){
    var screenArr = sessionStorage.getItem("screenArr");
    if(screenArr != null&& screenArr != 'underfind' && screenArr != ''){
        $('#sxk').css('display','block');
        var screenArr = JSON.parse(screenArr);
        for(x in screenArr){
            $(x).val(screenArr[x]);
            screenArr[x] == '自定义分组'?$(x).attr('data-code','define'):'';
            screenArr[x] == '品类喜好分组'?$(x).attr('data-code','class'):'';
            screenArr[x] == '品牌喜好分组'?$(x).attr('data-code','brand'):'';
            screenArr[x] == '折扣偏好分组'?$(x).attr('data-code','discount'):'';
            screenArr[x] == '季节偏好分组'?$(x).attr('data-code','season'):'';
        }
        sessionStorage.setItem("screenArr",'');
    }else{
        $('#sxk').css('display','none');
        var input=$(".inputs input");
        for(var i=0;i<input.length;i++){
            input.eq(i).css('background').indexOf('url') != -1? input[i].value="全部":'';

        }
    }

}

$("table tbody").on("click",".details",function(){
    $(this).parents("tr").trigger("click");
    $.blockUI({ message:null,overlayCSS: { cursor:"default",opacity:"0.1" },fadeIn:0 });
    $(".coupon_pop").show();
    $(".coupon_pop_detail_content").getNiceScroll(0).doScrollTop("y", 0);
    var data=JSON.parse($(this).parents("tr").attr("data-coupon"));
    var spans=$(".coupon_pop_info_content div span:last-child,.coupon_pop_detail_content div span:last-child");
    spans.map(function(){
        if($(this).attr("id")=="SAME_BV_VIP"){
            $(this).html(data[$(this).attr("id")]=="Y"?"是":"否");
        }else{
            $(this).html(data[$(this).attr("id")]);
        }
    });
});

$(".close_coupon_pop").click(function(){
    var spans=$(".coupon_pop_info_content div span:last-child,.coupon_pop_detail_content div span:last-child");
    $(".coupon_pop").hide();
    spans.map(function(){
        $(this).html("");
    });
    $.unblockUI({ fadeOut: 0 });
});

function checkStart(data,type){
    if(type===undefined){
        var id=$(this)[0].elem;
        var id_index=id.substr(-1);
        id="#end"+id_index;
        $(id).attr("onclick","laydate({elem:'"+id+"',min:'"+data+"',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
    }else{
        $('#sxk .inputs>ul>li.created_date').map(function(){
            var id=$(this).children("input").eq(0).attr("id");
            $("#"+id).attr("onClick","laydate({elem:'#"+id+"',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})")
        })
    }
}
function checkEnd(data,type){
    if(type===undefined){
        var id=$(this)[0].elem;
        var id_index=id.substr(-1);
        id="#start"+id_index;
        $(id).attr("onclick","laydate({elem:'"+id+"',min:'1900-01-01 00:00:00',max: '"+data+"',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
    }else {
        $('#sxk .inputs>ul>li.created_date').map(function(){
            var id=$(this).children("input").eq(1).attr("id");
            $("#"+id).attr("onClick","laydate({elem:'#"+id+"',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})")
        })
    }
}