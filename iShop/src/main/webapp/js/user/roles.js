var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var cout="";
var titleArray=[];
var filtrate="";
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
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
                inx=1;
                pageSize=$(this).attr('id');  
                if(value==""){
                    GET(inx,pageSize);
                }else if(value!==""){
                    param["pageSize"]=pageSize;
                    POST(); 
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
    }
})
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
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
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
                if(code=="details"){
                    TD+="<td class='power'><a href='javascript:void(0);'>"
                    +"编辑"
                    + "</a></td>"
                }else {
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                }
            })(c)
        }
        $(".table tbody").append("<tr id='"+data[i].id+"' data-role_code='"+data[i].role_code+"' data-role_name='"+data[i].role_name+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
                        +"</tr>");
    }
    whir.loading.remove();//移除加载框
    sessionStorage.removeItem("return_jump");
}
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li  id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li class='bg'id='compile'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
function InitialState(){
    if(return_jump!==null){
        inx=return_jump.inx;
        pageSize=return_jump.pageSize;
        value=return_jump.value;
        //filtrate=return_jump.filtrate;
        //list=return_jump.list;
        param=JSON.parse(return_jump.param);
        //_param=JSON.parse(return_jump._param);
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
        if(value==""&& filtrate==""){
            GET(inx,pageSize);
        }else if(value!==""){
            $("#search").val(value);
            POST(inx,pageSize);
        }else if(filtrate!==""){
            filtrates(inx,pageSize);
        }
    }
}
//页面加载调权限
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
};
qjia();
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("get","/user/role/list?pageNumber="+inx+"&pageSize="+pageSize
        +"&funcCode="+funcCode+"","","",function(data){
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                cout=list.pages;
                total = list.total;
                var list=list.list;
                superaddition(list,a);
                jumpBianse();
                setPage($("#foot-num")[0],cout,a,b,funcCode,total);
            }else if(data.code=="-1"){
                alert(data.message);
            }
    });
}
//GET(inx,pageSize);
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
    })
    //点击编辑查看权限
    $('.power').click(function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        var id=$(this).parents('tr').attr('id');
        sessionStorage.setItem("id",id);
        var role_code=$(this).parents('tr').attr("data-role_code");//角色编号
        var role_name=$(this).parents('tr').attr("data-role_name");//角色名称
        var group_corp={"role_code":role_code,"role_name":role_name};//组成一个code的字符串
        sessionStorage.setItem("group_corp",JSON.stringify(group_corp));//保存到本地
        $(window.parent.document).find('#iframepage').attr("src","/user/rolecheck_power1.html");
    });
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        sessionStorage.removeItem('id');
        $(window.parent.document).find('#iframepage').attr("src","/user/role_add.html");
    })
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            id=$(tr).attr("id");
            sessionStorage.setItem("id",id);
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            //return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.removeItem('group_corp');
            // var role_code=$(tr).find("td:eq(2)").html();
            // var group_corp={"role_code":role_code};
            // sessionStorage.setItem("group_corp",JSON.stringify(group_corp));//保存到本地
            $(window.parent.document).find('#iframepage').attr("src","/user/role_edit.html");//
        }else if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(tr.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    })
    //双击跳转
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        var return_jump={};//定义一个对象
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        //return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        sessionStorage.removeItem('group_corp');
        // var role_code=$(this).find("td:eq(2)").html();
        // var group_corp={"role_code":role_code};
        // sessionStorage.setItem("group_corp",JSON.stringify(group_corp));//保存到本地
        $(window.parent.document).find('#iframepage').attr("src","/user/role_edit.html");
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
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
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    POST();
})
//搜索的请求函数
function POST(){
    oc.postRequire("post","/corp/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            total=list.total;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与"+value+"相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,a);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,a,b,funcCode,total);
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
    var param={};
    param["id"]=ID;
    oc.postRequire("post","/user/role/delete","0",param,function(data){
        if(data.code=="0"){
            if(value==""){
               frame().then(function(){
                   GET();
               });
               $('.frame').html('删除成功');
            }else if(value!==""){
               frame().then(function(){
                   POST();
               });
               $('.frame').html('删除成功');
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
});
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    window.location.reload();
});