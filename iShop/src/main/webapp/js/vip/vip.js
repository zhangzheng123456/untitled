var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var page=1;//批量标签搜索
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var isscroll=false;
var titleArray=[];
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
//筛选的缓存
var  message={
    cache:{//缓存变量
        "vip_id":"",
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "user_codes":"",
        "user_names":"",
        "group_codes":"",
        "group_name":"",
        "type":"",
        "count":"",
        "corp_code":""
    }
};
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
function setPage(container, count, pageindex,pageSize){
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
    console.log(data);
    $(".table p").remove();
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
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        var wx='';
        //判断是否有会员头像
        if(data[i].vip_avatar==''){
            data[i].vip_avatar='../img/head.png';
        }
        //性别
        if(data[i].sex=="F"){
            data[i].sex="女"
        }else if(data[i].sex=="M"){
            data[i].sex="男"
        }
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        if(data[i].open_id){
            wx="<td><span class='icon-ishop_6-22'style='color:#8ec750'></span></td>";
        }else{
            wx="<td><span class='icon-ishop_6-22'style='color:#cdcdcd'></span></td>";
        }
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                if(code=='vip_name'){
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>"+wx;
                }else{
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }
            })(c)
        }
        $(".table tbody").append("<tr data-storecode='"+data[i].store_code+"'  data-phone='"+data[i].phone+"' data-storeId='"+data[i].store_id+"' data-code='"+data[i].corp_code+"' id='"+data[i].vip_id+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
        }else if(actions[i].act_name=="chooseUser"){
            $('.more_down').append("<div id='chooseUser' style='font-size: 10px'>设置所属导购</div>");
        }else if(actions[i].act_name=="output"){
            //$("#more_down").append("<div id='leading_out'>导出</div>");
            $("#filtrate").before("<li id='leading_out' title='因会员数据量大，请筛选后再导出'> <span class='icon-ishopwebicon_6-24'></span> 导出</li>")
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into'>导入</div>");
        }else if(actions[i].act_name=="addLabel"){
            $("#more_down").append("<div style='font-size:10px;' id='batch_label'>批量贴标签</div>");
        }
    }
}
function InitialState(){
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
    console.log(titleArray);
    for(var i=0;i<titleArray.length;i++){
        if(titleArray[i].show_name.trim()=='姓名'){
            TH+="<th>"+titleArray[i].show_name+"</th>"+'<th style="width: 20px"></th>'
        }else{
            TH+="<th>"+titleArray[i].show_name+"</th>"
        }
    }
    $("#tableOrder").after(TH);
}
qjia();
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    var param={};
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]='C10000';
    oc.postRequire("post","/vipAnalysis/allVip","",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            cout=messages.pages;
            var pageNum = messages.pageNum;
            //var list=list.list;
            superaddition(list,pageNum);
            jumpBianse();
            filtrate="";
            $('.contion .input').val("");
            message.cache.area_codes="";
            message.cache.area_names="";
            message.cache.brand_codes="";
            message.cache.brand_names="";
            message.cache.store_codes="";
            message.cache.store_names="";
            message.cache.user_codes="";
            message.cache.user_names="";
            message.cache.type="";
            message.cache.count=""
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
//批量贴标签
$("#more_down").on("click","#batch_label",function () {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length == 0){
        frame();
        $(".frame").html("请选择会员");
    }else if(tr.length>0) {
        var arr=whir.loading.getPageSize();
        var left=(arr[0]-$("#batch_label_wrapper").width())/2;
        var tp=(arr[3]-$("#batch_label_wrapper").height())/2;
        $("#batch_label_wrapper").css({"left":+left+"px","top":+tp+"px","position":"fixrd"});
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#p").show();
        $("#batch_label_wrapper").show();
        $("#batch_label_hot").empty();
        var param={};
        var vip_id="";
        for(var j=0;j<tr.length;j++){
            vip_id+=$(tr[j]).attr("id")+",";
        }
        param['vip_id']=vip_id;
        param["corp_code"]=$(tr[0]).attr("data-code");
        oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
                var html="";
                var classname="";
                for(var i=0;i<msg.length;i++){
                    if(msg[i].label_type=="user"){
                        classname="label_u";
                    }else{
                        classname="label_g";
                    }
                    html+="<span data-id="+msg[i].label_id+" class="+classname+" id="+i+">"+msg[i].label_name+"</span>"
                }
                $("#batch_label_hot").append(html);
            }
        })
    }
});
$("#close_label").click(function () {
    $("#batch_label_wrapper").hide();
    $("#p").hide();
});
//批量标签tabel页切换
$("#label_title li").click(function () {
    $(this).children("a").addClass("liactive");
    $(this).siblings("li").children("a").removeClass("liactive");
    if($(this).children("a").html()=="热门"){
        $("#batch_label_hot").show();
        $("#batch_label_gov").hide();
    }else if($(this).children("a").html()=="官方"){
        $("#batch_label_gov").show();
        $("#batch_label_hot").hide();
        if($("#batch_label_gov span").length>0){
            return ;
        }else {
            $("#batch_label_gov").empty();
            var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
            var param={};
            var vip_id="";
            var page=1;
            for(var j=0;j<tr.length;j++){
                vip_id+=$(tr[j]).attr("id")+",";
            }
            param["type"]="2";
            param['vip_id']=vip_id;
            param["corp_code"]=$(tr[0]).attr("data-code");
            param['pageNumber']=page;
            param['searchValue']="";
            oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var list=JSON.parse(msg.list)
                    var hasNextPage=list.hasNextPage;
                    list=list.list;
                    var html="";
                    var classname="";
                    for(var i=0;i<list.length;i++){
                        classname="label_g";
                        html+="<span  draggable='true' data-id="+list[i].id+" class="+classname+" id='"+i+"g'>"+list[i].label_name+"</span>"
                    }
                    $("#batch_label_gov").append(html);
                }
            })
        }
    }
});
//批量添加标签
$("#batch_label_box").on("click","span",function () {
    var that=$(this);
    addViplabel(that,"");
});
//按钮添加
$("#label_add").click(function () {
    var btn=$("#batch_search_label").val().trim();
    if(btn==""){
        return ;
    }
    addViplabel("",btn);
});
//搜索标签
$('#batch_search_label').bind('input propertychange', function() {
    var thatFun=arguments.callee;
    var that=this;
    $(this).unbind("input propertychange",thatFun);
    $(".batch_search_label ul").empty();
    page=1;
    searchHotlabel();
    setTimeout(function(){$(that).bind("input propertychange",thatFun)},0);
});
//搜索热门标签
function searchHotlabel() {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var param={};
    var vip_id="";
    for(var j=0;j<tr.length;j++){
        vip_id+=$(tr[j]).attr("id")+",";
    }
    param["corp_code"]=$(tr[0]).attr("data-code");
    param['pageNumber']=page;
    param['vip_id']=vip_id;
    param['searchValue']=$('#batch_search_label').val().replace(/\s+/g,"");
    param['type']="1";
    oc.postRequire("post","/VIP/label/findViplabelByType","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list)
            var hasNextPage=msg.hasNextPage;
            var list=msg.list;
            var html="";
            if(list.length>0){
                $(".batch_search_label ul").show();
                for(var i=0;i<list.length;i++){
                    html+="<li data-id="+list[i].id+">"+list[i].label_name+"</li>"
                }
                $(".batch_search_label ul").append(html);
            }else {
                $(".batch_search_label ul").hide();
            }
            if(hasNextPage==true){

            }
        }
        //搜索下拉点击事件
        $(".batch_search_label ul li").click(function () {
          $("#batch_search_label").val($(this).html());
        })
    })
}
function addViplabel(obj,btn) {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var classname=$(obj).attr("class");
    var param={};
    var list=[];
    for(var i=0;i<tr.length;i++){
        var code=$(tr[i]).attr("id");
        var store_id=$(tr[i]).attr("data-storeid");
        var params={
            "vip_code":code,
            "store_code":store_id
        }
        list.push(params);
    }
    if(btn!==""){
        param['label_name']=btn;
    }else{
        param['label_name']=$(obj).html();
    }
    param["corp_code"]=$(tr[0]).attr("data-code");
    param['label_id']="";
    param['list']=list;
    oc.postRequire("post","/VIP/label/addBatchRelViplabel","",param,function(data){
        if(data.code=="0"){
            if(btn!==""){
                var len_g = $("#batch_label_gov span").length;
                var len_h = $("#batch_label_hot span").length;
                for(var i=0;i<len_g;i++){
                    if(btn == $($("#batch_label_gov span")[i]).html()){
                        $($("#batch_label_gov span")[i]).removeClass("label_g").addClass("label_g_active");
                    }
                }
                for(var j=0;j<len_h;j++){
                    if(btn == $($("#batch_label_hot span")[j]).html()){
                        var name = $($("#batch_label_hot span")[j]).attr("class");
                        if(name=="label_u"){
                            $($("#batch_label_hot span")[j]).removeClass("label_u").addClass("label_u_active");
                        }else if(name=="label_g"){
                            $($("#batch_label_hot span")[j]).removeClass("label_g").addClass("label_g_active");
                        }
                    }
                }
                frame();
                $(".frame").html("添加成功");
            }
            if(classname=="label_u"){
                $(obj).removeClass(classname).addClass("label_u_active");
            }else if(classname=="label_g"){
                $(obj).removeClass(classname).addClass("label_g_active");
            }
        }else if(data.code=="-1"){
            frame();
            $('.frame').html('添加失败');
        }
    })
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
        //console.log(input);
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

    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        var store_id=$(this).attr("data-storeId");
        var corp_code=$(this).attr("data-code");
        var return_jump={};//定义一个对象
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["filtrate"]=filtrate;//筛选的值
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
        return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        console.log(return_jump);
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        sessionStorage.setItem("corp_code",corp_code);
        sessionStorage.setItem("store_id",store_id);
        if(id == "" || id == undefined){
            return ;
        }else{
            $(window.parent.document).find('#iframepage').attr("src","/vip/vip_data.html");
        }
    })
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            var id=$(tr).children().eq(2).attr("id");
            var store_id=$(tr).attr("data-storeId");
            var corp_code=$(tr).attr("id");
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
            sessionStorage.setItem("store_id",store_id);
            sessionStorage.setItem("corp_code",corp_code);
            $(window.parent.document).find('#iframepage').attr("src","/vip/vip_data.html");
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
        console.log(left);
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
    //选择导购
    $("#chooseUser").unbind("click");
    $("#chooseUser").bind("click",function () {
        var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
        var arr=whir.loading.getPageSize();
        var left=(arr[0]-$(".screen_area").width())/2;
        var tp=(arr[3]-$(".screen_area").height())/2+30;
        var staff_num=1;
        var mark="staff";
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#choose_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
        if(tr.length==0){
            frame();
            $('.frame').html('请先选择会员');
        }else if(tr.length>0){
            console.log($(tr[0]).attr("data-storeid"));
            var storeid=$(tr[0]).attr("data-storeid");
            for(var i=0;i<tr.length-1;i++){
                if($(tr[i]).attr("data-storeid")!==$(tr[i+1]).attr("data-storeid")){
                    frame();
                    $('.frame').html('请选择相同店铺下的会员');
                    return;
                }
            }
            $("#p").show();
            $("#choose_staff").show();
            isscroll=false;
            $("#choose_staff .screen_content_l").unbind("scroll");
            $("#choose_staff .screen_content_l ul").empty();
            $("#choose_staff .screen_content_r ul").empty();
            getstafflist(staff_num,mark);
        }
    });

}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        value=this.value.trim();
        console.log(value);
        if(value!==""){
            inx=1;
            param["searchValue"]=value;
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            POST(inx,pageSize);
        }else if(value==""){
            GET(inx,pageSize);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    if(value!==""){
        inx=1;
        param["searchValue"]=value;
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["funcCode"]=funcCode;
        POST(inx,pageSize);
    }else{
        GET(inx,pageSize);
    }
});
//搜索的请求函数
function POST(a,b){
    param["corp_code"]="C10000";
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipSearch","0",param,function(data){
        if(data.code=="0"){
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            cout=messages.pages;
            var pageNum = messages.pageNum;
            pageNum=parseInt(pageNum);
            var actions=messages.actions;
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
            filtrate="";
            $('.contion .input').val("");
            message.cache.area_codes="";
            message.cache.area_names="";
            message.cache.brand_codes="";
            message.cache.brand_names="";
            message.cache.store_codes="";
            message.cache.store_names="";
            message.cache.user_codes="";
            message.cache.user_names="";
            message.cache.type="";
            message.cache.count=""
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
});
//取消关闭
$("#cancel").click(function(){
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
    var params={};
    params["id"]=ID;
    console.log(params);
    oc.postRequire("post","/area/delete","0",params,function(data){
        if(data.code=="0"){
            if(value==""&&filtrate==""){
                frame().then(function(){
                    GET(inx,pageSize);
                });
                $('.frame').html('删除成功');
            }else if(value!==""){
                frame().then(function(){
                    POST(inx,pageSize);
                });
                $('.frame').html('删除成功');
                param["pageNumber"]=pageNumber;
            }else if(filtrate!==""){
                frame().then(function(){
                    filtrates(inx,pageSize);
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
}
//导出会员相册
$("#album_leadingout").click(function () {
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length == 0){
        frame();
        $('.frame').html('请先选择会员');
    }else {
        var params = {};
        var list=[];
        for(var i=0;i<tr.length;i++){
            var param = {};
            var vip_id = $(tr[i]).find("td").eq(2).text();
            var vip_name = $(tr[i]).find("td").eq(3).text();
            var phone = $(tr[i]).attr("data-phone");
            var card_no = $(tr[i]).find("td").eq(7).text();
            var corp_code =$(tr[i]).attr("data-code");
            param['vip_id'] = vip_id;
            param['vip_name'] = vip_name;
            param['corp_code'] = corp_code;
            param['phone'] = phone;
            param['card_no'] = card_no;
            list.push(param);
        }
        params['vip'] = list;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire('post','/vip/exportVipAlbums',0,params,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var path = msg.path;
                path=path.substring(1,path.length-1);
                var l=$(window).width();
                var h=$(document.body).height();
                $("#p").css({"width":+l+"px","height":+h+"px"});
                $("#tk").css({"left":+left+"px","top":+tp+"px"});
                $("#p").show();
                $("#tk").show();
                $("#enter").html("<a style='color: white;' href='/"+path+"'>确认</a>");
                whir.loading.remove();//移除加载框
                $("#enter").click(function () {
                    $("#p").hide();
                    $("#tk").hide();
                })
            }else {
                alert("导出相册失败");
                whir.loading.remove();//移除加载框
            }
        })
    }
});
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
//导出拉出list
//$("#more_down").on("click","#leading_out",function(){
$(".action_r ul").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
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
});
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var value = $("#search").val().trim();
    var _param={};
    var tablemanager=[];
    var screen = [];
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
    _param["tablemanager"]=tablemanager;
    _param["searchValue"]=value;
    if(filtrate==""){
        _param["screen_message"]="";
    }else if(filtrate!==""){
        if ($("#simple_filter").css("display") == "block") {
            $("#simple_contion .contion_input").each(function () {
                var input = $(this).find("input");
                var key = $(input[0]).attr("data-kye");
                var classname = $(input[0]).attr("class");
                var expend_key = $(input[0]).attr("data-expend");
                if(key == "17"){
                    return ;
                }else if (key == "4") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        var date = $("#consume_date_basic_4").attr("data-date");
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        param['date'] = date;
                        screen.push(param);
                    }
                }else if (key == "3") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        var date = $("#consume_date_basic_3").attr("data-date");
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        param['date'] = date;
                        screen.push(param);
                    }
                }else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                    if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                        var param = {};
                        var val = {};
                        val['start'] = $(input[0]).val();
                        val['end'] = $(input[1]).val();
                        param['type'] = "json";
                        param['key'] = key;
                        param['value'] = val;
                        screen.push(param);
                    }
                }else if(key == "6" && $(input[0]).val() !== "全部"){
                    var param = {};
                    var val = $(input[0]).val();
                    val == "已冻结"? val="Y":val="N";
                    param['key'] = key;
                    param['value'] = val;
                    param['type'] = "text";
                    screen.push(param);
                }else {
                    if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                        var param = {};
                        var val = $(input[0]).val();
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                }
            });
        } else {
            $("#contion>div").each(function () {
                $(this).find(".contion_input").each(function (i, e) {
                    var input = $(e).find("input");
                    var key = $(input[0]).attr("data-kye");
                    var expend_key = $(input[0]).attr("data-expend");
                    var classname = $(input[0]).attr("class");
                    if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            screen.push(param);
                        }
                    } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15"|| key == "16") {
                        if ($(input[0]).attr("data-code") !== "") {
                            var param = {};
                            var val = $(input[0]).attr("data-code");
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            screen.push(param);
                        }
                    } else if (key == "17") {
                        return;
                    } else if (key == "3" || key == "4") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            var date = $("#consume_date").attr("data-date");
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param['date'] = date;
                            screen.push(param);
                        }
                    }else if(key == "6" && $(input[0]).val() !== "全部" ){
                        var param = {};
                        var val = $(input[0]).val();
                        val == "已冻结"?val="Y":val="N";
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }else {
                        if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                            var param = {};
                            var val = $(input[0]).val();
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            screen.push(param);
                        }
                    }
                });
                $(this).find("textarea").each(function () {
                    var key = $(this).attr("data-kye");
                    var param = {};
                    var val = $(this).val();
                    if(val !== ""){
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        screen.push(param);
                    }
                });
            });
        }
        _param["screen_message"]=screen;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/exportExecl","0",_param,function(data){
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
//点击导入
$("#more_down").on("click","#guide_into",function(){
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
});
//上传文件
function UpladFile() {
    whir.loading.add("",0.5);//加载等待框
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
    xhr.onreadystatechange = function() {
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
        var data=JSON.parse(data);
        if(data.code=="0"){
            alert('导入成功');
            window.location.reload();
        }else if(data.code=="-1"){
            alert("导入失败"+data.message);
        }
        $('#file').val("");
        whir.loading.remove();//移除加载框
    }
    xhr.open("post", FileController, true);
    xhr.onload = function() {
        // alert("上传完成!");
    };
    xhr.send(form);
    $("#p").hide();
    $(".into_frame").hide();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
        inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    };
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "" && filtrate == "") {
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
});
//获取品牌列表
function getbrandlist(){
    var searchValue=$("#brand_search").val();
    var _param={};
    _param['corp_code']="C10000";
    _param["searchValue"]=searchValue;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/shop/brand", "",_param, function(data){
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=message.brands;
            var brand_html_left = '';
            var brand_html_right='';
            if (list.length == 0){
                for(var h=0;h<9;h++){
                    brand_html_left+="<li></li>"
                }
            } else {
                if(list.length<9){
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                            + i
                            + 1
                            + "'/><label for='checkboxThreeInput"
                            + i
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                    }
                    for(var j=0;j<9-list.length;j++){
                        brand_html_left+="<li></li>"
                    }
                }else if(list.length>=9){
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                            + i
                            + 1
                            + "'/><label for='checkboxThreeInput"
                            + i
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                    }
                }
            }
            $("#screen_brand .screen_content_l ul").append(brand_html_left);
            var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//拉取区域
function getarealist(a){
    var area_command = "/area/selAreaByCorpCode";
    var searchValue=$("#area_search").val().trim();
    var pageSize=20;
    var pageNumber=a;
    var _param = {};
    _param['corp_code']="C10000";
    _param["searchValue"]=searchValue;
    _param["pageSize"]=pageSize;
    _param["pageNumber"]=pageNumber;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post", area_command, "", _param, function(data) {
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var area_html_left ='';
            var area_html_right='';
            if (list.length == 0) {

            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                area_num++;
                area_next=false;
            }
            if(hasNextPage==false){
                area_next=true;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            if(!isscroll){
                $("#screen_area .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(area_next){
                            return;
                        }
                        getarealist(area_num);
                    };
                })
            }
            isscroll=true;
            var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//获取店铺列表
function getstorelist(a){
    var searchValue=$("#store_search").val();
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param['corp_code']="C10000";
    _param['area_code']=message.cache.area_codes;
    _param['brand_code']=message.cache.brand_codes;
    _param['searchValue']=searchValue;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
    if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var store_html = '';
            if (list.length == 0){
                
            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxTowInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                shop_num++;
                shop_next=false;
            }
            if(hasNextPage==false){
                shop_next=true;
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            if(!isscroll){
                $("#screen_shop .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num);
                    };
                })
            }
            isscroll=true;
            var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//获取员工列表
function getstafflist(a,b){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    if(b=="staff"){
        _param['store_code']=$(tr[0]).attr("data-storecode");//店铺
        _param['searchValue']=$("#search_staff").val().trim();//搜索值
    }else{
        _param['area_code']=message.cache.area_codes;
        _param['brand_code']=message.cache.brand_codes;
        _param['store_code']=message.cache.store_codes;
        _param['searchValue']=$('#staff_search').val();
    }
    _param["corp_code"]="C10000";
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
        if (data.code == "0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var staff_html = '';
            if (list.length == 0){
                
            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                    staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'/><label for='checkboxFourInput"
                        + i
                        + a
                        + 1
                        + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                staff_num++;
                staff_next=false;
            }
            if(hasNextPage==false){
                staff_next=true;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            $("#choose_staff .screen_content_l ul").append(staff_html);
            if(!isscroll){
                $("#screen_staff .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
                })
            }
            isscroll=true;
            $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
            $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
            var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        isscroll=false;
        $("#screen_area .screen_content_l").unbind("scroll");
        $("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function(){
    var event=window.event||arguments[0];
    shop_num=1;
    if(event.keyCode==13){
        isscroll=false;
        $("#screen_shop .screen_content_l ul").unbind("scroll");
        $("#screen_shop .screen_content_l ul").empty();
        getstorelist(shop_num);
    }
});
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
//员工搜索
$("#staff_search").keydown(function(){
    var event=window.event||arguments[0];
    staff_num=1;
    if(event.keyCode==13){
        isscroll=false;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        getstafflist(staff_num);
    }
});
//导购搜索
$("#search_staff").keydown(function(){
    var event=window.event||arguments[0];
    staff_num=1;
    if(event.keyCode==13){
        var mark="staff";
        isscroll=false;
        $("#choose_staff .screen_content_l").unbind("scroll");
        $("#choose_staff .screen_content_l ul").empty();
        getstafflist(staff_num,mark);
    }
});
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
//员工放大镜搜索
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
//品牌放大镜收索
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_wrapper").show();
});
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
});
//导购关闭
$("#close_choose_staff").click(function(){
    $("#choose_staff").hide();
    $("#p").hide();
});
//店铺关闭
$("#screen_close_shop").click(function(){
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
});
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
});
//弹框关闭
$("#screen_wrapper_close").click(function(){
    $("#screen_wrapper").hide();
    $("#p").hide();
});
//点击弹框的筛选按钮弹出区域框
$("#screen_areal").click(function(){
    if(message.cache.area_codes!==""){
        var area_codes=message.cache.area_codes.split(',');
        var area_names=message.cache.area_names.split(',');
        var area_html_right="";
        for(var h=0;h<area_codes.length;h++){
            area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
        }
        $("#screen_area .s_pitch span").html(h);
        $("#screen_area .screen_content_r ul").html(area_html_right);
    }else{
        $("#screen_area .s_pitch span").html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    var area_num=1;
    isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_wrapper").hide();
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
//点击弹框的筛选按钮弹出品牌框
$("#screen_brandl").click(function(){
    if(message.cache.brand_codes!==""){
        var brand_codes=message.cache.brand_codes.split(',');
        var brand_names=message.cache.brand_names.split(',');
        var brand_html_right="";
        for(var h=0;h<brand_codes.length;h++){
            brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
        }
        $("#screen_brand .s_pitch span").html(h);
        $("#screen_brand .screen_content_r ul").html(brand_html_right);
    }else{
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_wrapper").hide();
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//点击弹框的筛选按钮弹出区域框
$("#screen_shopl").click(function(){
    if(message.cache.store_codes!==""){
        var store_codes=message.cache.store_codes.split(',');
        var store_names=message.cache.store_names.split(',');
        var shop_html_right="";
        for(var h=0;h<store_codes.length;h++){
            shop_html_right+="<li id='"+store_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+store_names[h]+"</span>\
            \</li>"
        }
        $("#screen_shop .s_pitch span").html(h);
        $("#screen_shop .screen_content_r ul").html(shop_html_right);
    }else{
        $("#screen_shop .s_pitch span").html("0");
        $("#screen_shop .screen_content_r ul").empty();
    }
    var shop_num=1;
    isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $("#screen_wrapper").hide();
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//点击弹框的导购按钮弹出导购框
$("#screen_staffl").click(function(){
    if(message.cache.user_codes!==""){
        var user_codes=message.cache.user_codes.split(',');
        var user_names=message.cache.user_names.split(',');
        var staff_html_right="";
        for(var h=0;h<user_codes.length;h++){
            staff_html_right+="<li id='"+user_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+user_codes[h]+"'  data-storename='"+user_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+user_names[h]+"</span>\
            \</li>"
        }
        $("#screen_staff .s_pitch span").html(h);
        $("#screen_staff .screen_content_r ul").html(staff_html_right);
    }else{
        $("#screen_staff .s_pitch span").html("0");
        $("#screen_staff .screen_content_r ul").empty();
    }
    var staff_num=1;
    isscroll=false;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_staff").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_staff").show();
    $("#screen_wrapper").hide();
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
//店铺里面的区域点击
$("#shop_area").click(function(){
    if(message.cache.area_codes!==""){
        var area_codes=message.cache.area_codes.split(',');
        var area_names=message.cache.area_names.split(',');
        var area_html_right="";
        for(var h=0;h<area_codes.length;h++){
            area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
        }
        $("#screen_area .s_pitch span").html(h);
        $("#screen_area .screen_content_r ul").html(area_html_right);
    }else{
        $("#screen_area .s_pitch span").html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
});
//店铺里面的品牌点击
$("#shop_brand").click(function(){
    if(message.cache.brand_codes!==""){
        var brand_codes=message.cache.brand_codes.split(',');
        var brand_names=message.cache.brand_names.split(',');
        var brand_html_right="";
        for(var h=0;h<brand_codes.length;h++){
            brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
        }
        $("#screen_brand .s_pitch span").html(h);
        $("#screen_brand .screen_content_r ul").html(brand_html_right);
    }else{
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
});
//员工里面的区域点击
$("#staff_area").click(function(){
    if(message.cache.area_codes!==""){
        var area_codes=message.cache.area_codes.split(',');
        var area_names=message.cache.area_names.split(',');
        var area_html_right="";
        for(var h=0;h<area_codes.length;h++){
            area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
        }
        $("#screen_area .s_pitch span").html(h);
        $("#screen_area .screen_content_r ul").html(area_html_right);
    }else{
        $("#screen_area .s_pitch span").html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
});
//员工里面的店铺点击
$("#staff_shop").click(function(){
    if(message.cache.store_codes!==""){
        var store_codes=message.cache.store_codes.split(',');
        var store_names=message.cache.store_names.split(',');
        var shop_html_right="";
        for(var h=0;h<store_codes.length;h++){
            shop_html_right+="<li id='"+store_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+store_names[h]+"</span>\
            \</li>"
        }
        $("#screen_shop .s_pitch span").html(h);
        $("#screen_shop .screen_content_r ul").html(shop_html_right);
    }else{
        $("#screen_shop .s_pitch span").html("0");
        $("#screen_shop .screen_content_r ul").empty();
    }
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
});
//员工里面的品牌点击
$("#staff_brand").click(function(){
    if(message.cache.brand_codes!==""){
        var brand_codes=message.cache.brand_codes.split(',');
        var brand_names=message.cache.brand_names.split(',');
        var brand_html_right="";
        for(var h=0;h<brand_codes.length;h++){
            brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
        }
        $("#screen_brand .s_pitch span").html(h);
        $("#screen_brand .screen_content_r ul").html(brand_html_right);
    }else{
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[3]-$("#screen_shop").height())/2+50;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
});
//点击区域确定按钮
$("#screen_que_area").click(function(){
    var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes="";
    var area_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            area_codes+=r+",";
            area_names+=p+",";
        }else{
            area_codes+=r;
            area_names+=p;
        }
    };
    message.cache.area_codes=area_codes;
    message.cache.area_names=area_names;
    $("#screen_area").hide();
    $("#screen_wrapper").show();
    $("#screen_area_num").val("已选"+li.length+"个");
    $("#screen_area_num").attr("data-code",area_codes);
    $(".area_num").val("已选"+li.length+"个");
});
//点击品牌确定按钮
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes="";
    var brand_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            brand_codes+=r+",";
            brand_names+=p+",";
        }else{
            brand_codes+=r;
            brand_names+=p;
        }
    };
    message.cache.brand_codes=brand_codes;
    message.cache.brand_names=brand_names;
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
    $("#screen_brand_num").val("已选"+li.length+"个");
    $("#screen_brand_num").attr("data-code",brand_codes);
    $(".brand_num").val("已选"+li.length+"个");
    console.log(message.cache.brand_codes);
    console.log(message.cache.brand_names);
});
//点击店铺确定按钮
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_codes="";
    var store_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            store_codes+=r+",";
            store_names+=p+",";
        }else{
            store_codes+=r;
            store_names+=p;
        }
    };
    message.cache.store_codes=store_codes;
    message.cache.store_names=store_names;
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
    $("#screen_shop_num").val("已选"+li.length+"个");
    $("#screen_shop_num").attr("data-code",store_codes);
    $("#staff_shop_num").val("已选"+li.length+"个");
});
//点击员工确定按钮
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var user_codes="";
    var user_names="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            user_codes+=r+",";
            user_names+=p+",";
        }else{
            user_codes+=r;
            user_names+=p;
        }
    };
    message.cache.user_codes=user_codes;
    message.cache.user_names=user_names;
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
    $("#screen_stff_num").val("已选"+li.length+"个");
    $("#screen_stff_num").attr("data-code",user_codes);
});
//分配导购确定
$("#choose_staff .screen_que").click(function () {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var li = $("#choose_staff .screen_content_r ul li");
    if(li.length>1){
        frame();
        $('.frame').html('只能分配一个导购');
    }else if(li.length==0){
        frame();
        $('.frame').html('没有选择导购');
    }else  if(li.length==1){
       $("#choose_staff").hide();
       $("#p").hide();
       var staff_name = $("#choose_staff .screen_content_r ul li span").html();
       var user_code = $("#choose_staff .screen_content_r ul li").attr("id");
       var store_code = $(tr[0]).attr("data-storeid");
       var vip_id = "";
       for(var i=0;i<tr.length;i++){
           if(i<tr.length-1){
               vip_id += $(tr[i]).children("td:nth-child(3)").html()+",";
           }else{
               vip_id += $(tr[i]).children("td:nth-child(3)").html();
           }
       }
       var _param={};
        _param["corp_code"]="C10000";
        _param["store_code"]=store_code;
        _param["vip_id"]=vip_id;
        _param["user_code"]=user_code;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/vip/changeVipsUser","",_param,function (data) {
            if(data.code=="0"){
                for(var i=0;i<tr.length;i++){
                    $(tr[i]).children("td:nth-child(9)").html(staff_name);
                }
                whir.loading.remove();//移除加载框
            }else if(data.code == "-1"){
               console.log(data.message);
            }
        })
    }
});
//筛选调接口
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipScreen","", _param, function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            cout=message.pages;
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
            whir.loading.remove();//移除加载框
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                content: "筛选失败"
            });
        }
    })
}
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    $("#search").val("");
    window.location.reload();
});
