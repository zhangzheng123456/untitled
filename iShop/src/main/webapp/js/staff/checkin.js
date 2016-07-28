var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var filtrate="";//筛选的定义的值
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
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
                if(value==""){
                    GET();
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
function setPage(container, count, pageindex,pageSize,funcCode,value) {
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
        var inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx);
            setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
            inx = parseInt(this.innerHTML);
                dian(inx);
                setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx);
            setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
    function dian(inx){//
        if(value==""){
            whir.loading.add("",0.5);//加载等待框
            oc.postRequire("get","/sign/list?pageNumber="+inx+"&pageSize="+pageSize
                +"&funcCode="+funcCode+"","","",function(data){
                    console.log(data);
                    if(data.code=="0"){
                        $(".table tbody").empty();
                        var message=JSON.parse(data.message);
                        var list=JSON.parse(message.list);
                        var cout=list.pages;
                        var list=list.list;
                        superaddition(list,inx);
                        jumpBianse();
                    }else if(data.code=="-1"){
                        // alert(data.message);
                    }
            });           
        }else if(value!==""){
            whir.loading.add("",0.5);//加载等待框
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            oc.postRequire("post","/sign/search","0",param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                    var cout=list.pages;
                    var list=list.list;
                    $(".table tbody").empty();
                    if(list.length<=0){
                        $(".table p").remove();
                        $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息请重新搜索</p>");
                        whir.loading.remove();//移除加载框
                    }else if(list.length>0){
                        $(".table p").remove();
                        superaddition(list,inx);
                        jumpBianse();
                    }
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            })        
        }else if(filtrate!==""){
            _param["pageNumber"]=inx;
            _param["pageSize"]=pageSize;
            oc.postRequire("post","/corp/screen","0",_param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                    var cout=list.pages;
                    var list=list.list;
                    var actions=message.actions;
                    $(".table tbody").empty();
                    if(list.length<=0){
                        $(".table p").remove();
                        $(".table").append("<p>没有找到与相关的信息请重新搜索</p>");
                        whir.loading.remove();//移除加载框
                    }else if(list.length>0){
                        $(".table p").remove();
                        superaddition(list,inx);
                        jumpBianse();
                    }
                    setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value,filtrate);
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            });
        }
    }
}
function superaddition(data,num){//页面加载循环
    console.log(data);
    for (var i = 0; i < data.length; i++) {
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        var status="";
        if(data[i].status=="0"){
            status="签到";
        }
        if(data[i].status=="-1"){
            status="签退";
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
                        + data[i].user_code
                        + "</td><td>"
                        + data[i].user_name
                        + "</td><td><span>"
                        + data[i].corp_name
                        + "</span></td><td>"
                        + data[i].sign_time
                        + "</td><td>"
                        + status
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
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
//页面加载时list请求
function GET(){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("get","/sign/list?pageNumber="+inx+"&pageSize="+pageSize
        +"&funcCode="+funcCode+"","","",function(data){
            // console.log(data);
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var cout=list.pages;
                var list=list.list;
                var actions=message.actions;
                superaddition(list,inx);
                jurisdiction(actions);
                jumpBianse();
                setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value);
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
GET();
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
        $(window.parent.document).find('#iframepage').attr("src","/staff/checkin_add.html");
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
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    value=this.value.replace(/\s+/g,"");
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        POST();
    }
});
//搜索的请求函数
function POST(){
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/sign/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,inx);
                jumpBianse();
            }
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
console.log(left);
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
    for(var i=0,ID="";i<tr.length;i++){
        var r=$(tr[i]).attr("id");
        if(i<tr.length-1){
            ID+=r+",";
        }else{
             ID+=r;
        }     
    }
    var param={};
    param["id"]=ID;
    console.log(param);
    oc.postRequire("post","/sign/delete","0",param,function(data){
        if(data.code=="0"){
            if(value==""){
               frame();
               $('.frame').html('删除成功');
               GET(); 
            }else if(value!==""){
               frame();
               $('.frame').html('删除成功');
               POST();
            }else if(data.code=="-1"){
                frame();
                $('.frame').html(data.message);
            }
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
$("#leading_out").click(function(){
    var l=$(window).width();
    var h=$(document.body).height();
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
    oc.postRequire("post","/sign/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            console.log(message);
            $("#file_list ul").empty();
            for(var i=0;i<=message.length;i++){
                 $("#file_list ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
})
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list input[type='checkbox']:checked").parents("li");
    var param={};
    var tablemanager=[];
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    oc.postRequire("post","/sign/exportExecl","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var path=message.path;
            var path=path.substring(1,path.length-1);
            $('#download').html("<a href='/"+path+"'>下载文件</a>");
            $('#download').addClass("download");
            $('#file_submit').hide();
            $('#download').show();
            //导出关闭按钮
            $('.file_close').click(function(){
                $('.file').hide();
            })
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
})
//导出关闭按钮
$('.file_close').click(function(){
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
        for(var i=0;i<filter.length;i++){
            $("#sxk .inputs ul").append("<li><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"'></li>");
        }
    }
});
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
        if(screen_value!=""){
            num++;
            var param1={"screen_key":screen_key,"screen_value":screen_value};
            list.push(param1);
        }
   }
   _param["list"]=list;
   if(num>0){
        value="";//把搜索滞空
        $("#search").val("");
        filtrate="sucess";
        whir.loading.add("",0.5);//加载等待框
        filtrates();
   }else if(num<=0){
        frame();
        $('.frame').html("请输入筛选值");
   }
})
//筛选发送请求
function filtrates(){
   oc.postRequire("post","/sign/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,inx);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value,filtrate);
        }else if(data.code=="-1"){
            alert(data.message);
        }
   });
}
