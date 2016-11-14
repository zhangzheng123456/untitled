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
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
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
function setPage(container, count, pageindex,pageSize,funcCode){
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
        $(".table tbody").append("<tr data-storeId='"+data[i].store_id+"' id='"+data[i].corp_code+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
        + i
        + 1
        + "'/><label for='checkboxTwoInput"
        + i
        + 1
        + "'></label></div>"
        + "</td><td style='text-align:left;'>"
        + a
        + "</td><td id='"+data[i].vip_id+"'>"
        + data[i].vip_id
        + "</td><td>"
        + data[i].vip_name
        + "</td><td>"
        + data[i].sex
        +"</td><td>"
        + data[i].vip_phone
        +"</td><td>"
        + data[i].vip_card_type
        +"</td><td>"
        + data[i].cardno
        +"</td><td>"
        + data[i].user_name
        +"</td><td><span>"
        + data[i].store_name
        +"</span></td><td>"
        + data[i].vip_birthday
        +"</td><td>"
        +data[i].join_date
        +"</td></tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
};
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    console.log('OK');
    console.log(actions);
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="chooseUser"){
            $('.more_down').append("<div id='chooseUser' style='font-size: 10px'>设置所属导购</div>");
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
    var param={};
    param["pageNumber"]=a;
    param["pageSize"]=b;
    param["corp_code"]='C10000';
    oc.postRequire("post","/vipAnalysis/allVip","",param,function(data){
        console.log(data);
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            console.log(message);
            var list=message.all_vip_list;
            cout=message.pages;
            var pageNum = message.pageNum;
            //var list=list.list;
            superaddition(list,pageNum);
            jumpBianse();
            filtrate="";
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
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
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group_add.html");
    })
    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id=$(this).children().eq(2).attr("id");
        var store_id=$(this).attr("data-storeId");
        var corp_code=$(this).attr("id");
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
        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_data.html");
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
})
//搜索的请求函数
function POST(a,b){
    param["corp_code"]="C10000";
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/vip/vipSearch","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.all_vip_list;
            cout=message.pages;
            var pageNum = message.pageNum;
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
            filtrate="";
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
    console.log(params);
    oc.postRequire("post","/area/delete","0",params,function(data){
        if(data.code=="0"){
            if(value==""&&filtrate==""){
                frame();
                $('.frame').html('删除成功');
                GET(inx,pageSize);
            }else if(value!==""){
                frame();
                $('.frame').html('删除成功');
                param["pageNumber"]=pageNumber;
                POST(inx,pageSize);
            }else if(filtrate!==""){
                frame();
                $('.frame').html('删除成功');
                _param["pageNumber"]=pageNumber;
                filtrates(inx,pageSize);
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
            var phone = $(tr[i]).find("td").eq(5).text();
            var card_no = $(tr[i]).find("td").eq(7).text();
            var corp_code =$(tr[i]).attr("id");
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
})
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
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list input[type='checkbox']:checked").parents("li");
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
    oc.postRequire("post","/area/exportExecl","0",param,function(data){
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
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
//点击筛选
$("#filtrate").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_wrapper").width())/2;
    var tp=(arr[3]-$("#screen_wrapper").height())/2;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_wrapper").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").show();
})
//点击筛选会员的所属区域
$("#screen_areal").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").hide();
     $("#screen_area").show();
    var area_num=1
    if($("#screen_arsea .screen_content_l ul li").length>1){
        return;
    }
    getarealist(area_num);
})
//点击筛选会员的所属品牌
$("#screen_brandl").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_brand").show();
    if($("#screen_brand .screen_content_l ul li").length>1){
        return;
    }
    getbrandlist();
})
//点击筛选会员的所属店铺
$("#screen_shopl").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_shop").show();
    $("#screen_shop .screen_content_l ul").empty();
    var shop_num=1;
    getstorelist(shop_num);
})
//点击筛选会员的所属导购
$("#screen_staffl").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#screen_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_wrapper").hide();
    $("#screen_staff").show();
    $("#screen_staff .screen_content_l ul").empty();
    var staff_num=1;
    getstafflist(staff_num);
})
//点击列表显示选中状态
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
})
//点击店铺的区域
$("#shop_area").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    $("#screen_shop").hide();
    $("#screen_area").show();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    var area_num=1;
    $("#screen_area .screen_content_l ul").empty();
    // $("#screen_area .screen_content_r ul").empty();
    // $("#screen_area .s_pitch span").html("0");
    $("#area_search").val("");
    getarealist(area_num);
})
//点击店铺的品牌
$("#shop_brand").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_brand").width())/2;
    var tp=(arr[3]-$("#screen_brand").height())/2+30;
    $("#screen_shop").hide();
    $("#screen_brand").show();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#screen_brand .screen_content_l ul").empty();
    // $("#screen_brand .screen_content_r ul").empty();
    getbrandlist();
})
//移到右边
function removeRight(a,b){
    var li="";
    if(a=="only"){
        li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
    }
    if(a=="all"){
        li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
    }
    if(li.length=="0"){
        frame();
        $('.frame').html("请先选择");
        return;
    }
    if(li.length>0){
        for(var i=0;i<li.length;i++){
            var html=$(li[i]).html();
            var id=$(li[i]).find("input[type='checkbox']").val();
            var input=$(b).parents(".screen_content").find(".screen_content_r li");
            for(var j=0;j<input.length;j++){
                if($(input[j]).attr("id")==id){
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>")
        }
    }
    var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    bianse();
}
//移到左边
function removeLeft(a,b){
    var li="";
    if(a=="only"){
        li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
    }
    if(a=="all"){
        li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
    }
    if(li.length=="0"){
        frame();
        $('.frame').html("请先选择");
        return;
    }
    if(li.length>0){
        for(var i=li.length-1;i>=0;i--){
            $(li[i]).remove();
        }
    }
    var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    bianse();
}
//点击右移
$(".shift_right").click(function(){
    var right="only";
    var div=$(this);
    removeRight(right,div);
})
//点击右移全部
$(".shift_right_all").click(function(){
    var right="all";
    var div=$(this);
    removeRight(right,div);
})
//点击左移
$(".shift_left").click(function(){
    var left="only";
    var div=$(this);
    removeLeft(left,div);
})
//点击左移全部
$(".shift_left_all").click(function(){
    var left="all";
    var div=$(this);
    removeLeft(left,div);
})
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        $("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function(){
    var event=window.event||arguments[0];
    shop_num=1;
    if(event.keyCode==13){
        $("#screen_shop .screen_content_l ul").empty();
        getstorelist(shop_num);
    }
})
//员工搜索
$("#staff_search").keydown(function(){
    var event=window.event||arguments[0];
    staff_num=1;
    if(event.keyCode==13){
        $("#screen_staff .screen_content_l ul").empty();
        getstafflist(staff_num);
    }
})
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
})
//员工放大镜收索
$("#staff_search_f").click(function(){
    staff_num=1;
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
})
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_wrapper").show();
})
//店铺关闭
$("#screen_close_shop").click(function(){
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
})
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
})
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
})
//弹框关闭
$("#screen_wrapper_close").click(function(){
    $("#screen_wrapper").hide();
    $("#p").hide();
})
function bianse(){
    $(".screen_content_l li:odd").css("backgroundColor","#fff");
    $(".screen_content_l li:even").css("backgroundColor","#ededed");
    $(".screen_content_r li:odd").css("backgroundColor","#fff");
    $(".screen_content_r li:even").css("backgroundColor","#ededed");
}
//区域滚动事件
$("#screen_area .screen_content_l").scroll(function () {
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
//店铺滚动事件
$("#screen_shop .screen_content_l").scroll(function () {
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
//导购滚动事件
$("#screen_staff .screen_content_l").scroll(function(){
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
//点击区域确定追加节点
$("#screen_que_area").click(function(){
    var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes="";
    var area_name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            area_codes+=r+",";
            area_name+=p+",";
        }else{
            area_codes+=r;
            area_name+=p;
        }
    }
    var num=$("#screen_area .screen_content_r input[type='checkbox']").parents("li").length;
    $("#screen_area_num").val(area_name);
    $("#screen_area_num").attr("data-code",area_codes);
    $("#area_num").val("已选"+num+"个");
    $("#area_num").attr("data-areacode",area_codes);
    $("#staff_area_num").val("已选"+num+"个");
    $("#staff_area_num").attr("data-areacode",area_codes);
    $("#screen_area").hide();
    $("#screen_wrapper").show();
})
//点击店铺确定追加节点
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_name="";
    var store_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            store_code+=r+",";
            store_name+=p+",";
        }else{
            store_code+=r;
            store_name+=p;
        }
    }
    var num=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li").length;
    $("#screen_shop_num").val(store_name);
    $("#screen_shop_num").attr("data-code",store_code);
    $("#staff_shop_num").val("已选"+num+"个")
    $("#staff_shop_num").attr("data-storecode",store_code);
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
})
//点击品牌确定追加节点
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes="";
    var brand_name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            brand_codes+=r+",";
            brand_name+=p+",";
        }else{
            brand_codes+=r;
            brand_name+=p;
        }
    }
    var num=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li").length;
    $("#brand_num").attr("data-brandcode",brand_codes);
    $("#brand_num").val("已选"+num+"个");
    $("#screen_brand_num").val(brand_name);
    $("#screen_brand_num").attr("data-code",brand_codes);
    $("#staff_brand_num").val("已选"+num+"个");
    $("#staff_brand_num").attr("data-brandcode",brand_codes);
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
})
//点击员工确定追加节点
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var staff_codes="";
    var staff_name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            staff_codes+=r+",";
            staff_name+=p+",";
        }else{
            staff_codes+=r;
            staff_name+=p;
        }
    }
    $("#screen_stff_num").val(staff_name);
    $("#screen_stff_num").attr("data-code",staff_codes);
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
})

//选择导购筛选区域
$("#staff_choose_area").click(function () {
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    var li="";
    var result=$("#staff_choose_area").val();
    var result_id=$("#staff_choose_area").attr("data-code");
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#choose_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#choose_staff").hide();
    $("#choose_area").show();
       if(result!=="") {
           result = result.split(",");
           result_id = result_id.split(",");
           for(var i=0;i<result.length;i++){
               li+="<li id='"+result_id[i]+"'><div class='checkbox1'><input  type='checkbox' name='test'  class='check'  id='checkboxOneInput"
                   + i
                   + 1
                   + "'/><label for='checkboxOneInput"
                   + i
                   + 1
                   + "'></label></div><span class='p16'>"+result[i]+"</span></li>"
           }
           $("#choose_area .screen_content_r ul").empty();
           $("#choose_area .screen_content_r ul").append(li);
           $("#choose_area .s_pitch span").html(result.length);
           bianse();
       }
    var area_num=1
    if($("#choose_area .screen_content_l ul li").length>1){
        return;
    }
    getarealist(area_num);
})
//区域下确定
$("#choose_area .screen_que").click(function () {
    var li=$("#choose_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes="";
    var area_name="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            area_codes+=r+",";
            area_name+=p+",";
        }else{
            area_codes+=r;
            area_name+=p;
        }
    }
    var num=$("#choose_area .screen_content_r input[type='checkbox']").parents("li").length;
    $("#staff_choose_area").val(area_name);
    $("#staff_choose_area").attr("data-code",area_codes);
    // $("#area_num").val("已选"+num+"个");
    // $("#area_num").attr("data-areacode",area_codes);
    $("#shop_choose_area").val("已选"+num+"个");
    // $("#staff_area_num").attr("data-areacode",area_codes);
    $("#choose_area").hide();
    $("#choose_staff").show();
    $("#staff_choose_shop").val("");//重新选择店铺
    $("#staff_choose_shop").attr("data-code","");
})
//区域下搜索
$("#search_area").keydown(function(){
    var mark="area"
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        $("#choose_area .screen_content_l ul").empty();
        getarealist(area_num,mark);
    }
})
$("#search_area_f").click(function () {
    var mark="area";
    area_num=1;
    $("#choose_area .screen_content_l ul").empty();
    getarealist(area_num,mark);
})
//区域滚动事件
$("#choose_area .screen_content_l").scroll(function () {
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
//选择导购筛选店铺
$("#staff_choose_shop").click(function () {
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    var li="";
    var result=$("#staff_choose_shop").val();
    var result_id=$("#staff_choose_shop").attr("data-code");
    var mark="shop";
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#choose_shop").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#choose_staff").hide();
    $("#choose_shop").show();
    if(result!=="") {
        result = result.split(",");
        result_id = result_id.split(",");
        for(var i=0;i<result.length;i++){
            li+="<li id='"+result_id[i]+"'><div class='checkbox1'><input  type='checkbox' name='test'  class='check'  id='checkboxOneInput"
                + i
                + 1
                + "'/><label for='checkboxOneInput"
                + i
                + 1
                + "'></label></div><span class='p16'>"+result[i]+"</span></li>"
        }
        bianse();
    }
    $("#choose_shop .s_pitch span").html(result.length);
    $("#choose_shop .screen_content_r ul").empty();
    $("#choose_shop .screen_content_r ul").append(li);
    var area_num=1
    // if($("#choose_shop .screen_content_l ul li").length>1){
    //     return;
    // }
    $("#choose_shop .screen_content_l ul").empty();
    $("#search_shop").val("");
    getstorelist(area_num,mark);
})
//店铺下确定
$("#choose_shop .screen_que").click(function(){
    var li=$("#choose_shop .screen_content_r input[type='checkbox']").parents("li");
    var shop_codes="";
    var shop_name="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            shop_codes+=r+",";
            shop_name+=p+",";
        }else{
            shop_codes+=r;
            shop_name+=p;
        }
    }
    $("#staff_choose_shop").val(shop_name);
    $("#staff_choose_shop").attr("data-code",shop_codes);
    $("#choose_shop").hide();
    $("#choose_staff").show();
})
//店铺滚动事件
$("#choose_shop .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight=$(this).height();
    var mark="shop";
    if(nScrollTop + nDivHight >= nScrollHight){
        if(shop_next){
            return;
        }
        getstorelist(shop_num,mark);
    };
})
//店铺下搜索
$("#search_shop").keydown(function(){
    var mark="shop"
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        $("#choose_shop .screen_content_l ul").empty();
        getstorelist(area_num,'',mark);
    }
})
$("#search_shop_f").click(function () {
    var mark="shop";
    area_num=1;
    $("#choose_shop .screen_content_l ul").empty();
    getstorelist(area_num,'',mark);
})
//选择导购筛选品牌
$("#staff_choose_brand").click(function () {
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    var li="";
    var result=$("#staff_choose_brand").val();
    var result_id=$("#staff_choose_brand").attr("data-code");
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#choose_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#choose_staff").hide();
    $("#choose_brand").show();
    if(result!==""){
        result = result.split(",");
        result_id = result_id.split(",");
        for(var i=0;i<result.length;i++){
            li+="<li id='"+result_id[i]+"'><div class='checkbox1'><input  type='checkbox' name='test'  class='check'  id='checkboxOneInput"
                + i
                + 1
                + "'/><label for='checkboxOneInput"
                + i
                + 1
                + "'></label></div><span class='p16'>"+result[i]+"</span></li>"
        }
        $("#choose_brand .screen_content_r ul").empty();
        $("#choose_brand .screen_content_r ul").append(li);
        $("#choose_brand .s_pitch span").html(result.length);
        bianse();
    }
    var area_num=1
    if($("#choose_brand .screen_content_l ul li").length>1){
        return;
    }
    getbrandlist(area_num);
})
//品牌下确定
$("#choose_brand .screen_que").click(function () {
    var li=$("#choose_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes="";
    var brand_name="";
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find(".p16").html();
        if(i>0){
            brand_codes+=r+",";
            brand_name+=p+",";
        }else{
            brand_codes+=r;
            brand_name+=p;
        }
    }
    var num=$("#choose_brand .screen_content_r input[type='checkbox']").parents("li").length;
    $("#staff_choose_brand").val(brand_name);
    $("#staff_choose_brand").attr("data-code",brand_codes);
    // $("#area_num").val("已选"+num+"个");
    // $("#area_num").attr("data-areacode",area_codes);
    $("#shop_choose_brand").val("已选"+num+"个");
    // $("#staff_area_num").attr("data-areacode",area_codes);
    $("#choose_brand").hide();
    $("#choose_staff").show();
    $("#staff_choose_shop").val("");
    $("#staff_choose_shop").attr("data-code","");
})
//品牌下搜索
$("#search_brand").keydown(function(){
    var mark="brand"
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        $("#choose_brand .screen_content_l ul").empty();
        getbrandlist(mark);
    }
})
$("#search_brand_f").click(function () {
    var mark="brand";
    $("#choose_brand .screen_content_l ul").empty();
    getbrandlist(mark);
})
//店铺下筛选区域
$("#shop_choose_area").click(function () {
    $("#choose_shop").hide();
    $("#choose_area").show();
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    var li="";
    var result=$("#staff_choose_area").val();
    var result_id=$("#staff_choose_area").attr("data-code");
    $("#choose_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    if(result!=="") {
        result = result.split(",");
        result_id = result_id.split(",");
        for(var i=0;i<result.length;i++){
            li+="<li id='"+result_id[i]+"'><div class='checkbox1'><input  type='checkbox' name='test'  class='check'  id='checkboxOneInput"
                + i
                + 1
                + "'/><label for='checkboxOneInput"
                + i
                + 1
                + "'></label></div><span class='p16'>"+result[i]+"</span></li>"
        }
        $("#choose_area .screen_content_r ul").empty();
        $("#choose_area .screen_content_r ul").append(li);
        $("#choose_area .s_pitch span").html(result.length);
        bianse();
    }
    var area_num=1
    if($("#choose_area .screen_content_l ul li").length>1){
        return;
    }
    getarealist(area_num);
})
//店铺下筛选品牌
$("#shop_choose_brand").click(function () {
    $("#choose_shop").hide();
    $("#choose_brand").show();
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_area").width())/2;
    var tp=(arr[3]-$("#screen_area").height())/2+30;
    var li="";
    var result=$("#staff_choose_brand").val();
    var result_id=$("#staff_choose_brand").attr("data-code");
    $("#choose_brand").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    if(result!==""){
        result = result.split(",");
        result_id = result_id.split(",");
        for(var i=0;i<result.length;i++){
            li+="<li id='"+result_id[i]+"'><div class='checkbox1'><input  type='checkbox' name='test'  class='check'  id='checkboxOneInput"
                + i
                + 1
                + "'/><label for='checkboxOneInput"
                + i
                + 1
                + "'></label></div><span class='p16'>"+result[i]+"</span></li>"
        }
        $("#choose_brand .screen_content_r ul").empty();
        $("#choose_brand .screen_content_r ul").append(li);
        $("#choose_brand .s_pitch span").html(result.length);
        bianse();
    }
    var area_num=1
    if($("#choose_brand .screen_content_l ul li").length>1){
        return;
    }
    getbrandlist(area_num);
})
//选择导购关闭
$("#close_choose_staff").click(function(){
    $("#choose_staff").hide();
    $("#p").hide();
})
//选择导购区域关闭
$("#close_area").click(function () {
    $("#choose_staff").show();
    $("#choose_area").hide();
    $("#p").hide();
})
//选择导购品牌关闭
$("#close_brand").click(function () {
    $("#choose_staff").show();
    $("#choose_brand").hide();
    $("#p").hide();
})
//选择导购店铺关闭
$("#close_shop").click(function () {
    $("#choose_staff").show();
    $("#choose_shop").hide();
    $("#p").hide();
})
//导购搜索
$("#search_staff").keydown(function () {
    var mark="staff"
    var event=window.event||arguments[0];
    staff_num=1;
    if(event.keyCode == 13){
        $("#choose_staff .screen_content_l ul").empty();
        getstafflist(staff_num,mark);
    }
})
$("#search_staff_f").click(function () {
    var mark='staff';
    $("#choose_staff .screen_content_l ul").empty();
    staff_num=1;
    getstafflist(staff_num,mark);
})
//导购滚动事件
$("#choose_staff .screen_content_l").scroll(function(){
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight=$(this).height();
    var mark="staff";
    if(nScrollTop + nDivHight >= nScrollHight){
        if(staff_next){
            return;
        }
        getstafflist(staff_num,mark);
    };
})
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
})

//拉取区域
function getarealist(a,b){
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var searchValue="";
    if(b=="area"){
         searchValue=$("#search_area").val().trim();
    }else {
         searchValue=$("#area_search").val().trim();
    }
    var area_command = "/area/selAreaByCorpCode";
    var pageSize=20;
    var pageNumber=a;
    var _param = {};
    _param["corp_code"]="C10000";
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
            var area_html_left = '';
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
            $("#choose_area .screen_content_l ul").append(area_html_left);
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取店铺列表
function getstorelist(a,b,c){
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code ="";
    var brand_code="";
    if(b=="shop"){
        var a_code = $('#staff_choose_area').attr("data-code");
        var b_code = $('#staff_choose_brand').attr("data-code");
        if(a_code!==undefined){
            area_code = a_code;
        }
        if(b_code!==undefined){
           brand_code = b_code;
        }
    }else {
        area_code =$('#screen_area_num').attr("data-code");
        brand_code=$('#screen_brand_num').attr("data-code");
    }
    var searchValue="";
    if(c=="shop"){
         searchValue=$("#search_shop").val().trim();
    }else{
         searchValue=$("#store_search").val().trim();
    }
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    var checknow_data=[];
    var checknow_namedata=[];
    _param["corp_code"]="C10000";
    _param['area_code']=area_code;
    _param['brand_code']=brand_code;
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
            $("#choose_shop .screen_content_l ul").append(store_html);
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取品牌列表
function getbrandlist(a){
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var searchValue="";
    if(a=="brand"){
        searchValue=$("#search_brand").val().trim();
    }
    var _param={};
    _param["corp_code"]="C10000";
    _param['searchValue']=searchValue;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/shop/brand", "",_param, function(data){
        if(data.code == "0") {
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

                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                $("#choose_brand .screen_content_l ul").html(brand_html_left);
            }
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取员工列表
function getstafflist(a,b){
   var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    // var corp_code=$(tr[0]).attr("id");
    var area_code ="";
    var brand_code="";
    var store_code="";
    var searchValue="";
    if(b=="staff"){
        store_code=$(tr[0]).attr("data-storeid");//店铺
        searchValue=$("#search_staff").val().trim();
    }else{
        area_code =$('#screen_area_num').attr("data-code");//区域
        brand_code=$('#screen_brand_num').attr("data-code");//品牌
        store_code=$("#screen_shop_num").attr("data-code");//店铺
        searchValue=$("#staff_search").val();//员工的value值
    }
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param["corp_code"]="C10000";
    _param['area_code']=area_code;
    _param['brand_code']=brand_code;
    _param['store_code']=store_code;
    _param['searchValue']=searchValue;
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
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//点击会员确定
$("#screen_vip_que").click(function(){
    inx=1;
    var tr= $('#table tbody tr');
    var corp_code=$(tr[0]).attr("id");
    var area_code =$('#screen_area_num').attr("data-code");//区域
    var brand_code=$('#screen_brand_num').attr("data-code");//品牌
    var store_code=$("#screen_shop_num").attr("data-code");//店铺
    var user_code=$("#screen_stff_num").attr("data-code");//员工
    _param["corp_code"]="C10000";
    _param["brand_code"]=brand_code;
    _param["store_code"]=store_code;
    _param["area_code"]=area_code;
    _param["user_code"]=user_code;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    if(area_code==""&&brand_code==""&&store_code==""&&user_code==""){
        GET(inx,pageSize);
    }
    if(area_code!==""||brand_code!==""||store_code!==""||user_code!==""){
        filtrate="sucess";
        filtrates(inx,pageSize);

    }
    $("#search").val("");
    $("#screen_wrapper").hide();
    $("#p").hide();
})
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
            alert(data.message);
        }
    })
}
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    window.location.reload();
})


