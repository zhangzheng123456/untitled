var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var page=1;//批量标签搜索
var list="";
var cout="";
var select_type="";
var filtrate="";//筛选的定义的值
//var area_num=1;
//var area_next=false;
//var shop_num=1;
//var shop_next=false;
//var staff_num=1;
//var staff_next=false;
//var store_num=1;
var choose_store_num=1;
var choose_store_next=false;
var choose_staff_num=1;
var choose_staff_next=false;
var vip_type_num=1;
//var store_next=false;
var isscroll=false;
var titleArray=[];
var vip={};
var exportAllvip="";
var exportAllvip_num="";
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
var sort_key="";
var sort_value="";
var showBasic=false;
var showAdvance=false;
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
//筛选的缓存
var  message={
    clickMark:"",//员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
    cache:{//缓存变量
        "vip_id":"",
        "type":"",
        "count":"",
        "corp_code":""
    }
};
//模仿select
$(function(){
        if(funcCode != 'F0044'){
            $("#vipTitle").text("会员卡档案")
        }else {
            $("#album_leadingout").remove();
            $("#vipTitle").text("会员档案");
        }
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
                    filtrates(inx,pageSize)
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
function setPage(container, count, pageindex,pageSize,funcCode,total){
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
        GET(a,b);
    }else if (value!==""){
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b)
    }
}
function superaddition(data,num){//页面加载循环
    $(".table p").remove();
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

        var avatar="";
        var reg=/(^(http|https:\/\/)(.*?)(\/(.*)\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))/;
        if(data[i].avatar==undefined||data[i].avatar==""||reg.test(data[i].avatar)==false){
            avatar="../img/head.png";
        }
        if(data[i].avatar!==""&&data[i].avatar!==undefined&&reg.test(data[i].avatar)==true){
            avatar=data[i].avatar;
        }
        if(data[i].open_id){
            wx="<td><span class='icon-ishop_6-22' style='color:#8ec750'></span></td>";
        }else{
            wx="<td><span class='icon-ishop_6-22' style='color:#cdcdcd'></span></td>";
        }
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                if(code=='vip_name'){
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>"+wx;
                }else if(code=="avatar"){
                    TD+="<td><img src='"+avatar+"' alt=''></td>";
                } else  if(code=="user_name"){
                    TD+="<td data-value='user_name'><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }else  if(code=="store_name"){
                    TD+="<td data-value='store_name'><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }else if(code=="fr_active"){
                    if(data[i][code]=="Y"){
                        TD+="<td><span>是</span></td>";
                    }else{
                        TD+="<td><span>否</span></td>";
                    }
                }else {
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }
            })(c)
        }
        $(".table tbody").append("<tr data-name='"+data[i].vip_name+"' data-storecode='"+data[i].store_code+"'  data-phone='"+data[i].vip_phone+"' data-card_no='"+data[i].cardno+"' data-storeId='"+data[i].store_code+"' data-code='"+data[i].corp_code+"' id='"+data[i].vip_id+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
}
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="cancelVip"){
            $('#jurisdiction').append("<li id='abolishVip' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>作废</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="chooseUser"){
            $('#jurisdiction').append("<li id='chooseUser'><a href='javascript:void(0);'><span class='icon-ishop_6-30'></span>分配导购</a></li>");
        }else if(actions[i].act_name=="output"){
            //$("#more_down").append("<div id='leading_out'>导出</div>");
            $("#filtrate").before("<li class='leading_out' title='因会员数据量大，请筛选后再导出'> <span class='icon-ishopwebicon_6-24'></span>导出"+
                "<div class='more_down'>"+
                "<div style='font-size: 10px' id='leading_out'>导出选中行</div>" +
                "<div style='font-size: 10px' id='leading_out_all'>导出结果集</div>" +
                "</div>"+
                "</li>")
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into'>导入</div>");
        }else if(actions[i].act_name=="addLabel"){
            $("#more_down").append("<div style='font-size:10px;' id='batch_label' title='至少选择一个会员'>批量贴标签</div>");
        }else if(actions[i].act_name=="changeVipsStore"){
            $("#more_down").append("<div style='font-size:10px;' id='select_store' title='至少选择一个会员'>修改所属店铺</div>");
        }else if(actions[i].act_name=="changVipsCardType"){
            $("#more_down").append("<div style='font-size:10px;' id='select_type' title='至少选择一个会员'>修改会员类型</div>");
        }else if(actions[i].act_name=="basicScreen"){
            showBasic=true;
        }else if(actions[i].act_name=="advanceScreen"){
            showAdvance=true;
        }else if(actions[i].act_name=="addVipGroup"){
            $("#more_down").append("<div style='font-size:10px;' id='add_vip_group' title='根据筛选条件创建分组，请先筛选会员'>创建分组</div>");
        }
    }
    if(showAdvance && !showBasic){
        $(".tabs_head>label:last-child").trigger("click");
        $(".select_box .tabs_head").css("visibility","hidden");
    }
    if(!showAdvance && showBasic){
        $(".tabs_head>label:first-child").trigger("click");
        $(".select_box .tabs_head").css("visibility","hidden");
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
        select_type=return_jump.select_type
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
            filtrates(inx,pageSize)
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
        if(titleArray[i].show_name.trim()=='姓名'){
            TH+="<th>"+titleArray[i].show_name+"</th>"+'<th style="width: 20px"></th>'
        }else if(titleArray[i].column_name=="vip_birthday" || titleArray[i].column_name=="vip_phone" || titleArray[i].column_name=="cardno"){
            TH+="<th>"+titleArray[i].show_name+"<span class='icon-ishop_7-03 sort_list' style='display: inline-block;width: 10px' data-code='"+titleArray[i].column_name+"' data-type='desc'></span></th>"
        }else if(titleArray[i].column_name=="join_date"){
            TH+="<th>"+titleArray[i].show_name+"<span class='icon-ishop_7-03 sort_list sort_active'style='display: inline-block;width: 10px' data-code='"+titleArray[i].column_name+"' data-type='desc'></span></th>"
        }else{
            TH+="<th>"+titleArray[i].show_name+"</th>"
        }
    }
    $("#tableOrder").after(TH);
   if(return_jump!=null){
       $("#table thead th span[data-code='"+return_jump.sort_key+"']").attr("data-type",return_jump.sort_value);
       if(return_jump.sort_value=="asc"){
           $("#table thead th span.sort_list").removeClass("sort_active")
           $("#table thead th span[data-code='"+return_jump.sort_key+"']").removeClass('icon-ishop_7-03').addClass("icon-ishop_7-04 sort_active");
       }}
   }
qjia();
$("#table").on("click",".sort_list",function(){
    var code=$(this).attr("data-code");
    var type=$(this).attr("data-type");
    $(this).parents("tr").find(".sort_list").removeClass("sort_active");
    $(this).addClass("sort_active");
    $(this).parents("tr").find(".sort_list").removeClass("icon-ishop_7-04");
    $(this).parents("tr").find(".sort_list").addClass("icon-ishop_7-03");
    $(this).parents("tr").find(".sort_list").attr("data-type","desc");

    if(type=="desc"){
        $(this).attr("data-type","asc");
        $(this).removeClass("icon-ishop_7-03");
        $(this).addClass("icon-ishop_7-04")
    }
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
});
var states=[];
//页面加载时list请求
function GET(a,b){
    // var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    // var obj={};
    // var statePage=$("#foot-num .p-bg").text();
    // obj.page=statePage;
    // obj.list=[];
    // for(var i=0;i<tr.length;i++){
    //     var id=$(tr[i]).attr("id");
    //     obj.list.push(id);
    // }
    // states.push(obj);
    // console.log(states);
    var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
    return_jump=JSON.parse(return_jump);
    whir.loading.add("",0.5);//加载等待框
    var param={};
    var url = funcCode == "F0044" ? "/mobileVip/allMobile" : "/vipAnalysis/allVip";
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]='C10000';
    param["sort_key"]=return_jump==null?$("table th").children(".sort_active").attr("data-code"):return_jump.sort_key;
    param["sort_value"]=return_jump==null?$("table th").children(".sort_active").attr("data-type"):return_jump.sort_value;
    oc.postRequire("post",url,"",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            cout=messages.pages;
            exportAllvip_num=messages.count;
            var pageNum = messages.pageNum;
            var total = messages.count;
            //var list=list.list;
            superaddition(list,pageNum);
            jumpBianse();
            filtrate="";
            $('.contion .input').val("");
            message.cache.type="";
            message.cache.count="";
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove();
    });
}
//设置所属店铺
$("#more_down").on("click","#select_store",function(){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    isscroll=false;
    if(tr.length == 0){
        frame();
        $(".frame").html("请选择会员");
    }else if(tr.length>0) {
        //$(document.body).css("overflow","hidden");
        var arr=whir.loading.getPageSize();
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#p").show();
        $("#set_store_group .select_list").unbind("scroll");
        $("#set_store_group .select_list ul").empty();
        $("#set_store_group").css({"left":"50%","top":"50%"}).show();
        store_num=1;
        getStore(store_num)
    }
});
//设置所属店铺
$("#more_down").on("click","#select_type",function(){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    isscroll=false;
    if(tr.length == 0){
        frame();
        $(".frame").html("请选择会员");
    }else if(tr.length>0) {
        //$(document.body).css("overflow","hidden");
        var arr=whir.loading.getPageSize();
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#p").show();
        $("#set_vip_type .select_list").unbind("scroll");
        $("#set_vip_type .select_list ul").empty();
        $("#set_vip_type").css({"left":"50%","top":"50%"}).show();
        //getStore(store_num)
        getVipType()
    }
});
function canmove(dom,handle){
    $(dom).mousedown(function (e) {
        //设置移动后的默认位置
        var endx = 0;
        var endy = 0;
        //获取div的初始位置，要注意的是需要转整型，因为获取到值带px
        var left = parseInt($(dom).css("left"));
        var top = parseInt($(dom).css("top"));
        //获取鼠标按下时的坐标，区别于下面的es.pageX,es.pageY
        var downx = e.pageX;
        var downy = e.pageY;     //pageY的y要大写，必须大写！！
        var minendx=$(this).innerWidth()/2+200;
        var minendy=$(this).innerHeight()/2+40;
        //    鼠标按下时给div挂事件
        $(dom).bind("mousemove", function (es) {
            //es.pageX,es.pageY:获取鼠标移动后的坐标
            endx = es.pageX - downx + left;     //计算div的最终位置
            endy = es.pageY - downy + top;
            endx=endx<minendx?minendx:endx;
            endy=endy<minendy?minendy:endy;
            //带上单位
            $(dom).css("left", endx + "px").css("top", endy + "px")
        });
        if (!$(e.target).is($(handle))) {
            $(dom).unbind("mousemove");
        }
    }); //移动筛选框
    $(dom).mouseout(function () {
        //鼠标弹起时给div取消事件
        $(dom).unbind("mousemove")
    });
    $(dom).mouseup(function () {
        //鼠标弹起时给div取消事件
        $(dom).unbind("mousemove")
    });
}
canmove("#set_add_vip_group","#set_add_vip_group .select_title");
canmove("#set_store_group","#set_store_group .select_title");
canmove("#set_vip_type","#set_vip_type .select_title");
function setAddVipGroupList(screenList,parentSelector){
    var data=screenList;
    var div="";
    for(var d=0;d<data.length;d++){
        var value=data[d].value;
        div+='<div><em>'+data[d].groupName+'</em><span class="select_detail">';
        if(data[d].type=="interval"){
            for(var v=0;v<value.length;v++){
                var logic_1=getLogicDefault(value[v].logic1);
                var logic_2=getLogicDefault(value[v].logic2);
                if(v!=0){
                    var opera=value[v].opera=="AND"?"并且":value[v].opera=="OR"?"或者":"";
                    div+=opera
                }
                if(data[d].key=="TRADE" || data[d].key=="COUPON"){
                    var head="";
                    var foot="";
                    var time="";
                    var dataKey="";
                    var index=value[v].key.lastIndexOf("_");
                    var name=value[v].name;
                    if(data[d].key=="COUPON"){
                        dataKey=value[v].key.slice(index+1);
                    }else {
                        time=value[v].key.slice(index+1);
                        if(time!="all" && isNaN(time)){
                            dataKey=value[v].key
                        }else{
                            dataKey=value[v].key.slice(0,index)
                        }
                    }
                    switch (dataKey){
                        case "NUM_TRADE":
                            head="消费次数";
                            foot="次";
                            break;
                        case "AMT_TRADE":
                            head="消费金额";
                            foot="元";
                            break;
                        case "NUM_TRADE_R":
                            head="退款次数";
                            foot="次";
                            name="";
                            break;
                        case "AMT_TRADE_R":
                            head="退款金额";
                            foot="元";
                            name="";
                            break;
                        case "TOTAL":
                            head="获得";
                            foot="个";
                            break;
                        case "VERIFYED":
                            head="使用";
                            foot="个";
                            break;
                        case "EXPIRE":
                            head="过期";
                            foot="个";
                            break;
                        case "AVAILABLE":
                            head="可用";
                            foot="个";
                            break
                    }
                    var htmlHead="";
                    if(data[d].key=="TRADE"){
                        htmlHead='<p class="logic">'+name+'</p>'+head;
                    }else{
                        htmlHead=head+'<p class="selected_value">'+name+'</p>';
                    }
                    div+=htmlHead+
                        '<p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'+
                        foot
                }else if(data[d].key=="CATA1_PRD" || data[d].key=="CATA2_PRD" || data[d].key=="CATA3_PRD"|| data[d].key=="CATA4_PRD" || data[d].key=="SEASON_PRD" || data[d].key=="T_BL_M" || data[d].key=="DAYOFWEEK" || data[d].key=="BRAND_ID" || data[d].key=="CATA3_PRD" || data[d].key=="STORE_AREA" || data[d].key=="PRICE_CODE"){
                    div+='<p class="selected_value">'+value[v].name+'</p><p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'
                }else{
                    div+='<p class="logic">'+logic_1+'</p>'+
                        '<p class="selected_value">'+value[v].start+'</p>'+
                        '至'+
                        '<p class="logic">'+logic_2+'</p>'+
                        '<p class="selected_value">'+value[v].end+'</p>'
                }
            }
        }else if(data[d].type=="radio"){
            for(var r=0;r<value.length;r++){
                if(data[d].key=="TASK" || data[d].key=="ACTIVITY"){
                    if(value[r].value=="Y"){
                        div+='<p class="logic">已参与</p>'+
                            '<p class="selected_value">'+value[r].name+'</p>'
                    }else if(value[r].value=="N"){
                        div+='<p class="logic">未参与</p>'+
                            '<p class="selected_value">'+value[r].name+'</p>'
                    }
                }else{
                    if(data[d].key=="SEX_VIP"){
                        div+='<p class="logic">'+getLogicDefault(value[r].logic)+'</p>'
                    }
                    div+='<p class="selected_value">'+value[r].dataName+'</p>'
                }
            }
        }else if(data[d].type=="text"){
            if(data[d].key=="user_code" || data[d].key=="brand_code" || data[d].key=="area_code" || data[d].key=="store_code"|| data[d].key=="VIP_GROUP_CODE" ){
                div+='<p class="selected_value">'+data[d].name+'</p>'
            }else if(data[d].key=="ZONE"){
                var value=data[d].value;
                for(var t=0;t<value.length;t++ ){
                    var valName=value[t].value.split("-");
                    var name_zone="";
                    if(valName[0]!="省"){
                        name_zone+=valName[0];
                    }if(valName[1]!="市"){
                        name_zone+="-"+valName[1];
                    }
                    if(valName[2]!="区"){
                        name_zone+="-"+valName[2];
                    }
                    if(t!=0){
                        var opera=value[t].opera=="AND"?"并且":value[t].opera=="OR"?"或者":"";
                        div+=opera
                    }
                    div+='<p class="logic">'+getLogicDefault(value[t].logic)+'</p>';
                    div+='<p class="selected_value">'+name_zone+'</p>'
                }
            }else{
                var value=data[d].value;
                for(var t=0;t<value.length;t++ ){
                    if(t!=0){
                        var opera=value[t].opera=="AND"?"并且":value[t].opera=="OR"?"或者":"";
                        div+=opera
                    }
                    div+='<p class="logic">'+getLogicDefault(value[t].logic)+'</p>';
                    if(data[d].key=="APP_ID"){
                        div+='<p class="selected_value">'+value[t].name+'</p>'
                    }else {
                        div+='<p class="selected_value">'+value[t].value+'</p>'
                    }
                }
            }
        }
        div=div+'</span></div></div>'
    }
    parentSelector.html(div)
}
//把筛选结果分组
$("#more_down").on("click","#add_vip_group",function(){
    if(_param.screen==undefined || _param.screen.length==0){
        frame();
        $(".frame").html("请先筛选会员");
    }else if(_param.screen!=undefined && _param.screen.length>0){
        var arr=whir.loading.getPageSize();
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#p").show();
        $("#set_add_vip_group").css({"left":"50%","top":"50%"}).show();
        var list=_param.screen;
        var parentSelector=$("#set_add_vip_group .select_list");
        setAddVipGroupList(list,parentSelector);
    }
});
//确定把筛选结果分组
function validateAddGroup(){
    var groupName=$("#set_add_vip_group .add_vip_group_name").val().trim();
    var grouplist=$("#set_add_vip_group .select_list");
    var def= $.Deferred();
    if(groupName==""){
        frame();
        $(".frame").html("分组名称不能为空");
    }else{
        validateAddGroupName().then(function(){
            if (grouplist.children().length==0){
                frame();
                $(".frame").html("分组条件不能为空");
                return
            }
            def.resolve();
        },function(){

        })
    }
    return def;
}
function validateAddGroupName(){
    var params = {};
    var def= $.Deferred();
    params["vip_group_name"] = $("#set_add_vip_group .add_vip_group_name").val().trim();
    params["corp_code"] = "C10000";
    oc.postRequire("post", "/vipGroup/vipGroupNameExist", "", params, function (data) {
        if (data.code == "0") {
            def.resolve();
        } else if (data.code == "-1") {
            frame();
            $(".frame").html("该名称已经存在");
            $("#set_add_vip_group .add_vip_group_name").val("");
            def.reject()
        }
    });
    return def
}
$("#set_add_vip_type_sure").click(function(){
    validateAddGroup().then(function(){
        var param={
                    vip_group_name:$("#set_add_vip_group .add_vip_group_name").val().trim(),
                    corp_code:"C10000",
                    user_code:"",
                    remark:$("#set_add_vip_group .add_vip_group_remark").val().trim(),
                    group_type:"define_v2",
                    group_condition:_param.screen,
                    "isactive":"Y",
                    "is_public":"Y"
                };
                console.log(param);
                oc.postRequire("post", "/vipGroup/add", "", param, function (data) {
                    if(data.code==0){
                        frame();
                        $(".frame").html("新建成功");
                        $("#set_add_vip_group").hide();
                        $("#p").hide();
                    }else{
                        frame();
                        $(".frame").html("新建失败");
                    }
                })
    });
});
function getStore(a){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var param={};
    var vip_id="";
    for(var j=0;j<tr.length;j++){
        vip_id+=$(tr[j]).attr("id")+",";
    }
    param["corp_code"]=$(tr[0]).attr("data-code");
    param["area_code"]="";
    param["brand_code"]="";
    param["pageNumber"]=a;
    param["pageSize"]=20;
    param["searchValue"]=$("#set_store_group_search").val().trim();
    whir.loading.add("",0.5);
    oc.postRequire("post","/shop/selectByAreaCode","",param,function(data){
        whir.loading.remove();//移除加载框
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            list=list.list;
            var store_html = '';
            if(list.length==0 && a==1){
                $("#set_store_group .select_list ul").empty();
                store_html="<div style=\"line-height: 255px;text-align: center;color: #888;\">暂无店铺</div>"
            }else if(list.length>0){
                for (var i = 0; i < list.length; i++) {
                    store_html+="<li data-store_code='"+list[i].store_code+"' data-store_name='"+list[i].store_name+"'><span title='"+list[i].store_name+"'>"+list[i].store_name+"</span><span class=\"select_this\">选择</span><span class=\"cancel_this\">取消</span></li>"
                }
            }
            if(hasNextPage==true){
                store_num++;
                store_next=false;
            }
            if(hasNextPage==false){
                store_next=true;
            }
            $("#set_store_group .select_list ul").append(store_html);
            if(!isscroll){
                $("#set_store_group .select_list").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(store_next){
                            return;
                        }
                        getStore(store_num);
                    }
                })
            }
            isscroll=true;
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
//导购搜索
$("#search_staff").keydown(function () {
    var event = window.event || arguments[0];
    choose_staff_num = 1;
    if (event.keyCode == 13) {
        isscroll = false;
        $("#choose_staff .select_list").unbind("scroll");
        $("#choose_staff .select_list ul").empty();
        getStaff(choose_staff_num);
    }
});
//获取员工列表
function getStaff(a) {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param['store_code'] = $(tr[0]).attr("data-storecode");//店铺
    _param['searchValue'] = $("#search_staff").val().trim();//搜索值
    _param["corp_code"] = "C10000";
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/shop/staffPage", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list =message.list;
            var hasNextPage = list.hasNextPage;
            var choose_staff_html = '';
                if (list.length > 0) {
                    for (var j = 0; j < list.length; j++) {
                        choose_staff_html += "<li data-user_code='" + list[j].user_code + "' data-user_name='" + list[j].user_name + "'><span title='" + list[j].user_name + "'>" + list[j].user_name + "\(" + list[j].user_code + "\)</span><span class=\"select_this\">选择</span><span class=\"cancel_this\">取消</span></li>"
                    }
                }
            if (hasNextPage == true) {
                choose_staff_num++;
                choose_staff_next = false;
            }
            if (hasNextPage == false) {
                choose_staff_next = true;
            }
            $("#choose_staff .select_list ul").append(choose_staff_html);
            if (!isscroll) {
                $("#choose_staff .select_list").bind("scroll", function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight >= nScrollHight) {
                        if (choose_staff_next) {
                            return;
                        }
                        getStaff(choose_staff_num);
                    }
                })
            }
            isscroll = true;
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            whir.loading.remove();
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                zIndex: 10003,
                content: data.message
            });
        }
    })
}
function getVipType(){
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var param={};
    param["corp_code"]=$(tr[0]).attr("data-code");
    param["searchValue"]=$("#set_vip_type_search").val().trim();
    whir.loading.add("",0.5);
    oc.postRequire("post","/vipCardType/getVipCardTypes","", param, function(data) {
        whir.loading.remove();//移除加载框
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var store_html = '';
            if(list.length==0){
                $("#set_vip_type .select_list ul").empty();
                store_html="<div style=\"line-height: 255px;text-align: center;color: #888;\">暂无会员类型</div>"
            }else if(list.length>0){
                for (var i = 0; i < list.length; i++) {
                    store_html+="<li data-store_code='"+list[i].vip_card_type_code+"' data-store_name='"+list[i].vip_card_type_name+"'><span title='"+list[i].vip_card_type_name+"'>"+list[i].vip_card_type_name+"("+list[i].vip_card_type_code+")</span><span class=\"select_this\">选择</span><span class=\"cancel_this\">取消</span></li>"
                }
            }
            $("#set_vip_type .select_list ul").append(store_html);
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
$("#select_ok").click(function(){
    var li=$("#set_store_group .select_list li.active")
    if(!$("#set_store_group .select_list").find("li").hasClass("active")){
        frame();
        $(".frame").html("请先选择店铺");
        return;
    }
    var store_codes;
    var store_names;
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var param={};
    var vip_id="";
    store_codes=$(li).attr("data-store_code");
    store_names=$(li).attr("data-store_name");
    for(var j=0;j<tr.length;j++){
        if(j<tr.length-1){
            vip_id+=$(tr[j]).attr("id")+",";
        }else{
            vip_id+=$(tr[j]).attr("id");
        }

    }
    param.vip_id=vip_id;
    param.store_code=store_codes;
    param.store_name=store_names;
    param.corp_code="C10000";
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/changeVipsStore","",param,function(data){
        whir.loading.remove();
        if(data.code==0){
            whir.loading.add("mask");
            frame().then(function(){
                whir.loading.remove("mask");
                $("#set_store_group").hide();
                $("#p").hide();
                if(value==""&&filtrate==""){
                    GET(inx,pageSize);
                }else if(value!==""){
                    $("#search").val(value);
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    filtrates(inx,pageSize);
                }
            });
            //for (var a=0;a<tr.length;a++){
            //    $(tr[a]).children("td[data-value=store_name]").html(param.store_name);
            //    $(tr[a]).attr("data-storecode",store_codes);
            //    $(tr[a]).attr("data-storeid",store_codes)
            //}
            $(".frame").html("分配成功");
        }else{
           frame();
           $(".frame").html(data.message==""?"分配失败":data.message);
        }
    })
});
$("#set_vip_type_sure").click(function(){
    var li=$("#set_vip_type .select_list li.active");
    if(!$("#set_vip_type .select_list").find("li").hasClass("active")){
        frame();
        $(".frame").html("请先选择会员类型");
        return;
    }
    var vip_card_type_code;
    var vip_card_type;
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var param={};
    var vip_id="";
    vip_card_type_code=$(li).attr("data-store_code");
    vip_card_type=$(li).attr("data-store_name");
    for(var j=0;j<tr.length;j++){
        if(j<tr.length-1){
            vip_id+=$(tr[j]).attr("id")+",";
        }else{
            vip_id+=$(tr[j]).attr("id");
        }

    }
    var vip_id=tr.map(function(){
            return $(this).attr("id");
    }).get().reverse().join(",");
    param.vip_id=vip_id;
    param.vip_card_type_code=vip_card_type_code;
    param.vip_card_type=vip_card_type;
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/changeVipsCardType","",param,function(data){
        whir.loading.remove();
        if(data.code==0){
            frame().then(function(){
                $("#set_vip_type").hide();
                $("#p").hide();
                if(value==""&&filtrate==""){
                    GET(inx,pageSize);
                }else if(value!==""){
                    $("#search").val(value);
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    filtrates(inx,pageSize);
                }
                // $(".icon-ishop_6-07").parent().trigger("click");
            });
            $(".frame").html("修改成功");
        }else{
           frame();
           $(".frame").html(data.message==""?"修改失败":data.message);
        }
    })
});
$("#set_store_group_search").keydown(function(){
    var event=window.event||arguments[0];
    store_num=1;
    if(event.keyCode==13){
        isscroll=false;
        $("#set_store_group .select_list").unbind("scroll");
        $("#set_store_group .select_list ul").empty();
       getStore(store_num)
    }
});
$("#set_vip_type_search").keydown(function(){
    var event=window.event||arguments[0];
    store_num=1;
    if(event.keyCode==13){
        $("#set_vip_type .select_list ul").empty();
        getVipType();
    }
});
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
        //$(document.body).css("overflow","hidden");
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
                if(msg.length==0){
                    html = '<div style="text-align: center;height: 300px;line-height: 300px;color: #888;">暂无数据</div>';
                }
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
    //$(document.body).css("overflow","auto");
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
                if(list.length==0){
                    html = '<div style="text-align: center;height: 300px;line-height: 300px;color: #888;">暂无数据</div>';
                }
                for(var i=0;i<list.length;i++){
                    classname="label_g";
                    html+="<span  draggable='true' data-id="+list[i].id+" class="+classname+" id='"+i+"g'>"+list[i].label_name+"</span>"
                }
                $("#batch_label_gov").append(html);
            }
        })
    }
});
//批量添加标签
$("#batch_label_box").on("click","span",function () {
    var that=$(this);
    var val=$(this).html();
    addViplabel(that,val);
});
//按钮添加
$("#label_add").click(function () {
    var btn=$("#batch_search_label").val().trim();
    if(btn==""){
        return ;
    }
    addViplabel("",btn);
    $("#batch_search_label").val("");
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
function addViplabel(obj,val) {
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
    param['label_name']=val;
    param["corp_code"]=$(tr[0]).attr("data-code");
    param['label_id']="";
    param['list']=list;
    oc.postRequire("post","/VIP/label/addBatchRelViplabel","",param,function(data){
        if(data.code=="0"){
            var len_g = $("#batch_label_gov span").length;
            var len_h = $("#batch_label_hot span").length;
            for(var i=0;i<len_g;i++){
                if(val == $($("#batch_label_gov span")[i]).html()){
                    $($("#batch_label_gov span")[i]).removeClass("label_g").addClass("label_g_active");
                }
            }
            for(var j=0;j<len_h;j++){
                if(val == $($("#batch_label_hot span")[j]).html()){
                    var name = $($("#batch_label_hot span")[j]).attr("class");
                    if(name=="label_u"){
                        $($("#batch_label_hot span")[j]).removeClass("label_u").addClass("label_u_active");
                    }else if(name=="label_g"){
                        $($("#batch_label_hot span")[j]).removeClass("label_g").addClass("label_g_active");
                    }
                }
            }
            if(classname!=="label_u_active"&&classname!=="label_g_active"){
                frame();
                $(".frame").html("添加成功");
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
    });

    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id= $(this).attr("id") != 'undefined' ? $(this).attr("id") : '';
        var store_id=$(this).attr("data-storeId");
        var store_code=$(this).attr("data-storecode");
        var corp_code=$(this).attr("data-code");
        var phone = $(this).attr("data-phone");
        var return_jump={};//定义一个对象
        return_jump["sort_key"]=$("table th").children(".sort_active").attr("data-code");
        return_jump["sort_value"]=$("table th").children(".sort_active").attr("data-type");
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["filtrate"]=filtrate;//筛选的值
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        return_jump["_param"]=JSON.stringify(_param);//筛选定义的值
        return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        return_jump["select_type"]=select_type;//当前筛选的项
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        sessionStorage.setItem("corp_code",corp_code);
        sessionStorage.setItem("store_id",store_id);
        sessionStorage.setItem("store_code",store_code);
        sessionStorage.setItem("vip_phone",phone);
        if((id == "" && funcCode != 'F0044') || phone == undefined ){
            return ;
        }else{
            if(funcCode == "F0010"){
                $(window.parent.document).find('#iframepage').attr("src","/vip/vip_data.html?t="+ $.now());
            }else if(funcCode == 'F0044'){
                $(window.parent.document).find('#iframepage').attr("src","/vip/newVipData.html?t="+ $.now());
            }
        }
    });
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            var id=$(tr).attr("id") != 'undefined' ? $(tr).attr("id") : '';
            var store_id=$(tr).attr("data-storeId");
            var store_code=$(tr).attr("data-storecode");
            var corp_code=$(tr).attr("data-code");
            var phone=$(tr).attr("data-phone");
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["filtrate"]=filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(_param);//筛选定义的值
            return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            return_jump["select_type"]=select_type;//当前筛选的项
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.setItem("id",id);
            sessionStorage.setItem("store_id",store_id);
            sessionStorage.setItem("store_code",store_code);
            sessionStorage.setItem("corp_code",corp_code);
            sessionStorage.setItem("vip_phone",phone);
            funcCode == 'F0044' ? $(window.parent.document).find('#iframepage').attr("src","/vip/newVipData.html") : $(window.parent.document).find('#iframepage').attr("src","/vip/vip_data.html");
        }else if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(tr.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    });
    //删除
    $("#abolishVip").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#abolishWrap").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
    });
    //选择导购
    $("#chooseUser").unbind("click");
    $("#chooseUser").bind("click",function () {
        var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
        var arr=whir.loading.getPageSize();
        var staff_num=1;
        var mark="staff";
        $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
        $("#search_staff").val("");
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
            //$(document.body).css("overflow","hidden");
            $("#p").show();
            $("#choose_staff").show();
            isscroll=false;
            $("#choose_staff .select_list").unbind("scroll");
            $("#choose_staff .select_list ul").empty();
            getStaff(staff_num,mark);
        }
    });
}
//作废会员信息
$(".closeAbolishWrap").click(function () {
    $("#p").hide();
    $("#abolishWrap").hide();
});
$("#abolishEnter").click(function () {
    $("#p").hide();
    $("#abolishWrap").hide();
    var vips = [];
    $("#table tbody input[type='checkbox']:checked").parents("tr").each(function () {
        var obj = {
            "vip_id":$(this).attr("id"),
            "vip_name":$(this).attr("data-name"),
            "card_no":$(this).attr("data-card_no")
        }
        vips.push(obj);
    });
    var param = {};
    param.vips = vips;
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/cancelVip","",param,function (data) {
        if(data.code == 0){
            whir.loading.remove();
            frame();
            $('.frame').html('操作成功');
            inx=1;
            if (value == "" && filtrate == "") {
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
        }else {
            whir.loading.remove();
            $("#content_box").show();
            $("#addVipTk").show();
            $("#msg").html(data.message==""?"操作失败":data.message);
        }
    });
});
//鼠标按下时触发的收索
$("#search").keydown(function() {
    $("#empty_filter").trigger("click");
    delete _param.screen;
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
    $("#empty_filter").trigger("click");
    delete _param.screen;
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
    var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
    var url = funcCode != 'F0044' ? '/vip/vipSearch' : '/mobileVip/vipSearchMobile';
    return_jump=JSON.parse(return_jump);
    param["corp_code"]="C10000";
    param["sort_key"]=return_jump==null?$("table th").children(".sort_active").attr("data-code"):return_jump.sort_key;
    param["sort_value"]=return_jump==null?$("table th").children(".sort_active").attr("data-type"):return_jump.sort_value;
    whir.loading.add("",0.5);//加载等待框
    $('#search').attr("disabled",true);
    oc.postRequire("post",url,"0",param,function(data){
        if(data.code=="0"){
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            cout=messages.pages;
            exportAllvip_num=messages.count;
            var pageNum = messages.pageNum;
            var total = messages.count;
            pageNum=parseInt(pageNum);
            var actions=messages.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
                $('#search').removeAttr("disabled");
            }else if(list.length>0){
                $(".table p").remove();
                $('#search').removeAttr("disabled");
                superaddition(list,pageNum);
                jumpBianse();
            }
            filtrate="";
            $('.contion .input').val("");
            message.cache.type="";
            message.cache.count="";
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "获取数据失败"
            });
        }
    })
}
//弹框关闭
$("#X").click(function(){
    $("#p").hide();
    $("#tk").hide();
    //$(document.body).css("overflow","auto");
});
//取消关闭
$("#cancel").click(function(){
    $("#p").hide();
    $("#tk").hide();
    //$(document.body).css("overflow","auto");
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
    },3000);
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
//导出会员相册
$("#album_leadingout").click(function () {
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length == 0){
        frame();
        $('.frame').html('请先选择会员');
    }else {
        var params = {};
        var list=[];
        //$(document.body).css("overflow","hidden");
        for(var i=0;i<tr.length;i++){
            var param = {};
            var vip_id = $(tr[i]).attr("id");
            var vip_name = $(tr[i]).attr("data-name");
            var phone = $(tr[i]).attr("data-phone");
            var card_no = $(tr[i]).attr("data-card_no");
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
                $("#p").show();
                $("#tk").show();
                $("#enter").html("<a style='color: white;' data-src='/"+path+"'>确认</a>");
                whir.loading.remove();//移除加载框
                $("#enter").click(function () {
                    window.open($(this).find("a").eq(0).attr("data-src"));
                    //$(document.body).css("overflow","auto");
                    $("#p").hide();
                    $("#tk").hide();
                    return true;
                })
            }else {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "导出相册失败"
                });
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
function getCols(){
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
                $("#file_list_l ul").append("<li style='width: 100%;overflow: hidden;white-space: nowrap;text-overflow: ellipsis' title='"+message[i].show_name+"' data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                    +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            bianse();
            $("#file_list_r ul").empty();
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();//移除加载框
        }
    })
}
$(".action_r ul").on("click","#leading_out_all",function(){
    //$(document.body).css("overflow","hidden");
    exportAllvip=exportAllvip_num;
    $(this).addClass("active");
    getCols();
});
//导出拉出list
$(".action_r ul").on("click","#leading_out",function(){
    $("#leading_out_all").removeClass("active");
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    exportAllvip=tr.length;
    var allVip="";
    if(tr.length == 0){
        frame();
        $('.frame').html('请先选择会员');
        return;
    }
    //$(document.body).css("overflow","hidden");
    for(var a=0;a<tr.length;a++){
        if(a<tr.length-1){
            allVip+=$(tr).attr("id")+","
        }else {
            allVip+=$(tr).attr("id")
        }
    }
    getCols();
});
//导出提交的
$("#file_submit").click(function(){
    var num = funcCode == 'F0044' ? 10000 : 10000;
    var name = funcCode == 'F0044' ? '会员档案' : '会员卡档案';
    var allPage=Math.ceil(exportAllvip/num);
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var value = $("#search").val().trim();
    var param={};
    var tablemanager=[];
    var screen = [];
    var list_html="";
    var url = funcCode == 'F0044' ? '/mobileVip/exportExecl' : '/vip/exportExecl';
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    $("#to_zip").show();
    $("#download_all").hide();
    $("#download_all a").removeProp("href");
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    $('.file').hide();
    $("#export_list_all ul").html("");
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*num + 1;
        var end_num="";
        if (exportAllvip < a*num ){
            end_num = exportAllvip
        }else{
            end_num = a*num
        }
        list_html+= '<li>'
            +'<span style="float: left">'+name+'('+start_num+'~'+end_num+')</span>'
            +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
            +'<span style="margin-right:10px;" class="state"></span>'
            +'</li>'
    }
    $("#export_list_all ul").html(list_html);
    $("#export_list").show();
    $("#export_list_all").scrollTop(0);
    if(filtrate==""){
        param["screen_message"]="";
    }else if(filtrate!==""){
        param["screen_message"]=_param['screen']
    }
    //whir.loading.add("",0.5);//加载等待框
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    var allVip="";
    for(var n=tr.length-1;n>=0;n--){
        if(n>0){
            funcCode != 'F0044' ? allVip+=$(tr[n]).attr("id")+"," : allVip+=$(tr[n]).attr("data-phone")+",";
        }else {
            funcCode != 'F0044' ? allVip+=$(tr[n]).attr("id") : allVip+=$(tr[n]).attr("data-phone");
        }
    }
    param.ids=allVip;
    if($("#leading_out_all").hasClass("active")){
        param.ids="";
    }
    $("#export_list_all .export_list_btn").click(function () {
        if($(this).hasClass("btn_active")){
            return
        }
        var self=$(this);
        var page=$(this).attr("data-page");
        $(this).next().text("导出中...");
        $(this).addClass("btn_active");
        param["page_num"]=page;
        param["page_size"]=num;
        param["sort_key"]=$("table th").children(".sort_active").attr("data-code");
        param["sort_value"]=$("table th").children(".sort_active").attr("data-type");
        oc.postRequire("post",url,"0",param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var path=message.path;
                if(message.path_type && message.path_type=="oss"){
                    self.attr("data-path",path);
                    self.html("<a href='"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                }else {
                    path=path.substring(1,path.length-1);
                    self.attr("data-path",path);
                    self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                }
                self.next().text("导出完成");
                //self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                self.css("backgroundColor","#637ea4");
            }else if(data.code=="-1"){
                self.removeClass("btn_active");
                self.next().text("导出失败");
            }
        },function(){
            self.removeClass("btn_active");
            self.next().text("导出超时");
        })
    });
});
//导出关闭按钮
$('#file_close').click(function(){
    //$(document.body).css("overflow","auto");
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
});
$("#hide_export").click(function(){
    //$(document.body).css("overflow","auto");
    $("#export_list").hide();
    $("#p").hide();
});
$("#to_zip").click(function(){
    var a=$("#export_list_all ul li a");
    var URL="";
    var param={};
    if(a.length==0){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "请先导出文件"
        });
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
    param.name=funcCode == 'F0044' ? '会员档案' : '会员卡档案';
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
$("#more_down").on("click","#guide_into",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').hide();
    $(".into_frame").show();
});
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
                filtrates(inx,pageSize)
            }
        }
    }
});

//弹框关闭
$("#screen_wrapper_close").click(function(){
    $("#screen_wrapper").hide();
    $("#p").hide();
    //$(document.body).css("overflow","auto");
});
//分配导购确定
$("#choose_staff #select_staff_ok").click(function () {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var li = $("#choose_staff .select_list li.active");
    if(li.length>1){
        frame();
        $('.frame').html('只能分配一个导购');
    }else if(li.length==0){
        frame();
        $('.frame').html('请先选择导购');
    }else  if(li.length==1){
       $("#choose_staff").hide();
       $("#p").hide();
       var staff_name = $("#choose_staff .select_list ul li.active").attr("data-user_name");
       var user_code = $("#choose_staff  .select_list ul li.active").attr("data-user_code");
       var store_code = $(tr[0]).attr("data-storecode");
       var vip_id ="";
       for(var i=0;i<tr.length;i++){
           if(i<tr.length-1){
               vip_id+=$(tr[i]).attr("id")+",";
           }else{
               vip_id+=$(tr[i]).attr("id")
           }
       }
       var _param={};
        _param["corp_code"]="C10000";
        _param["store_code"]=store_code;
        _param["vip_id"]=vip_id;
        _param["user_code"]=user_code;
        _param["user_name"]=staff_name;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/vip/changeVipsUser","",_param,function (data) {
            whir.loading.remove();//移除加载框
            if(data.code=="0"){
                frame().then(function(){
                    if(value==""&&filtrate==""){
                        GET(inx,pageSize);
                    }else if(value!==""){
                        $("#search").val(value);
                        POST(inx,pageSize);
                    }else if(filtrate!==""){
                        filtrates(inx,pageSize);
                    }
                    // $(".icon-ishop_6-07").parent().trigger("click");
                });
                //for (var a=0;a<tr.length;a++){
                //    $(tr[a]).children("td[data-value=store_name]").html(param.store_name);
                //    $(tr[a]).attr("data-storecode",store_codes);
                //    $(tr[a]).attr("data-storeid",store_codes)
                //}
                $(".frame").html("分配成功");
            }else if(data.code == "-1"){
                frame();
                $(".frame").html(data.message==""?"分配失败":data.message);
            }
        })
    }
});
//筛选调接口
function filtrates(a,b){
    var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
    var url = funcCode != 'F0044' ? '/vip/newScreen' : '/mobileVip/newScreenMobile';
    return_jump=JSON.parse(return_jump);
    whir.loading.add("",0.5);//加载等待框
    _param["sort_key"]=return_jump==null?$("table th").children(".sort_active").attr("data-code"):return_jump.sort_key;
    _param["sort_value"]=return_jump==null?$("table th").children(".sort_active").attr("data-type"):return_jump.sort_value;
    oc.postRequire("post",url,"", _param, function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            cout=message.pages;
            exportAllvip_num=message.count;
            total = message.count;
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
            setPage($("#foot-num")[0],cout,a,b,funcCode,total);
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
    //window.location.reload();
    inx=1;
    if (value == "" && filtrate == "") {
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
$("#set_store_group").on("click",".select_this",function(){
    if($(this).parents("ul").find("li").hasClass("active")){
        frame();
        $(".frame").html("只能分配一个店铺");
        return;
    }
    $(this).parent().addClass("active");
});
$("#set_vip_type").on("click",".select_this",function(){
    if($(this).parents("ul").find("li").hasClass("active")){
        frame();
        $(".frame").html("只能分配一个会员类型");
        return;
    }
    $(this).parent().addClass("active");
});
$("#choose_staff").on("click",".select_this",function(){
    if($(this).parents("ul").find("li").hasClass("active")){
        frame();
        $(".frame").html("只能分配一个导购");
        return;
    }
    $(this).parent().addClass("active");
});
$(".select_modal").on("click",".cancel_this",function(){
    $(this).parent().removeClass("active");
});
$(".select_cancel").click(function(){
    $(".select_modal").hide();
    $("#p").hide();
    //$(document.body).css("overflow","auto");
});
$(window).scroll(function(){
    laydate.reset()
});
$("#expend_attribute,#vip_attribute").scroll(function(){
    laydate.reset()
});

$("#filtrate").click(function () {
    $(".select_box").css({
        height: $(window).height() - 130 + "px"
    });
    $(".select_box_wrap").show();
    $(".select_box_wrap .select_box").css({"top":"100px","left":"50%"});
    $(".select_box .center_item_group .item_list").html("");
    $(".select_box .center_item_group").hide();
    $(".select_box .selected_list").html("");
    if(_param.screen!==undefined && _param.screen.length>0){
        $(".notSelect").hide();
        if(select_type=="content_basic"){
            $("#content_high .notSelect").show();
            $(".tabs_head>label:first-child").trigger("click");
        }else{
            $("#content_basic .notSelect").show();
            $(".tabs_head>label:last-child").trigger("click");
        }
        fuzhiSelect();
    }else{
       if($(".select_box .tabs_head").css("visibility")!="hidden"){
           $(".tabs_head>label:first-child").trigger("click");
       } else{
           if(showAdvance && !showBasic){
               $(".tabs_head>label:last-child").trigger("click");
           }
           if(!showAdvance && showBasic){
               $(".tabs_head>label:first-child").trigger("click");
           }
       }
        $(".notSelect").show();
    }
    $("#content_high .item_group").find(".group_arrow").css({
        animation: "none"
    });
    $(".select_box_wrap").css("bottom",$(window).height()-$(document).height());
    //$("body").css("overflow", "hidden");
});

//店铺勾选全部是，判断总店铺的数量
$("#checkShopAll").change(function(){
    if($(this).prop("checked")){
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked",true);
        if($(this).attr("data-count") && $(this).attr("data-count")>=500){
            $(this).parent().next().css("visibility","visible");
        }
    }else{
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked",false);
        $("#checkShopAll").parent().next().css("visibility","hidden");
    }
});
$("#toggleCheckAllShop").click(function(){
    if($("#checkShopAll").prop("checked")){
        $("#checkShopAll").prop("checked",false);
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked",false);
        $("#checkShopAll").parent().next().css("visibility","hidden");
    }else{
    $("#checkShopAll").prop("checked",true);
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked",true);
        if($("#checkShopAll").attr("data-count") && $("#checkShopAll").attr("data-count")>=500){
            $("#checkShopAll").parent().next().css("visibility","visible");
        }
    }
});