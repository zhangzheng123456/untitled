var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
// var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var role_code=group_corp.role_code;//角色编号
var role_code="";//角色编号
var group_corp=sessionStorage.getItem("group_corp");//取本地的群组编号

if(group!==null){
  group_corp=JSON.parse(group_corp);
  role_code=group_corp.role_code;
  $("#page-wrapper").hide();
  $(".content").show();
  GET();  
}

//编辑页面点击弹出角色权限的框
$('#edit_power').click(function(){
    role_code=$("#ROLE_NUM").val();
    if(role_code==''){
        frame();
        $('.frame').html("请先定义角色编号！");
        return;
    }
    $("#page-wrapper").hide();
    $(".content").show();
    GET();
})
//新增页面点击弹出角色权限的框
$("#add_power").click(function(){
    if(role_code==''){
        frame();
        $('.frame').html("请先定义角色编号！");
        return;
    }
   role_code=$("#ROLE_NUM").val();
   $("#page-wrapper").hide();
   $(".content").show();
   GET();
})
//关闭弹框
$('#turnoff').click(function(){
    $("#page-wrapper").show();
    $(".content").hide();
    sessionStorage.removeItem('group_corp');
})
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
function superaddition(data,num,live){
    for (var i = 0; i < data.length; i++) {
        if(num>=2){
            var a=i+num*pageSize;
        }else{
            var a=i+1;
        }
        $(".table tbody").append("<tr data-action='"+data[i].action_code+"' data-function='"+data[i].function_code+"'>"
                        + "</td><td style='text-align:left;padding-left:22px'>"
                        + a
                        + "</td><td>"
                        + data[i].module_name
                        +"</td><td>"
                        +data[i].function_name
                        + "</td><td>"
                        +data[i].action_name
                        +"</td><td width='50px;' style='text-align: left;'><div class='checkbox1' id='"+data[i].id+"'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div></td></tr>");
    }
    for(var p=0;p<live.length;p++){
        if($("#"+live[p].id).find("input").length>0){
            $("#"+live[p].id).find("input")[0].checked=true;
        }
    }
};
//页面加载时list请求
function GET(){
    param["role_code"]=role_code;
    oc.postRequire("post","/user/role/check_power","0",param,function(data){
        console.log(data);
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var live=message.live;
                var list=JSON.parse(message.list);
                console.log(list);
                var actions=message.actions;
                superaddition(list,inx,live);
                // jurisdiction(actions);
                jumpBianse();
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
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    value=this.value.replace(/\s+/g,"");
    param["searchValue"]=value;
    param["role_code"]=role_code;
    // param["pageNumber"]=inx;
    // param["pageSize"]=pageSize;
    // param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        POST();
    }
});
//搜索的请求函数
function POST(){
    oc.postRequire("post","/corp/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与"+value+"相关的信息请重新搜索</p>")
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,inx);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
// //弹框关闭
// $("#X").click(function(){
//     $("#p").hide();
//     $("#tk").hide();
// })
// //取消关闭
// $("#cancel").click(function(){
//     $("#p").hide();
//     $("#tk").hide();
// })
// //弹框删除关闭
// $("#delete").click(function(){
//     $("#p").hide();
//     $("#tk").hide();
//     var tr=$("tbody input[type='checkbox']:checked").parents("tr");
//     for(var i=0,ID="";i<tr.length;i++){
//         var r=$(tr[i]).attr("id");
//         if(i<tr.length-1){
//             ID+=r+",";
//         }else{
//              ID+=r;
//         }     
//     }
//     var param={};
//     param["id"]=ID;
//     console.log(param);
//     oc.postRequire("post","/corp/delete","0",param,function(data){
//         if(data.code=="0"){
//             if(value==""){
//                frame();
//                $('.frame').html('删除成功');
//                GET(); 
//             }else if(value!==""){
//                frame();
//                $('.frame').html('删除成功');
//                POST();
//             }
//         }
//     })
// })
//删除弹框
 function frame(){
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
    
    $('body').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    $('.frame').remove();
} 
//全选
function checkAll(name){
    console.log(name);
    var el=$("tbody input[name='"+name+"']");
    var len = el.length;
    for(var i=0; i<len; i++)
        {
            console.log(el[i].name);
            if((el[i].type=="checkbox") && (el[i].name==name));
            {
              el[i].checked = true;
            }
        }
};

//取消全选
function clearAll(name){
    console.log(name);
    var el=$("tbody input[name='"+name+"']");
    var len = el.length;
    for(var i=0; i<len; i++)
        {
            if((el[i].type=="checkbox") && (el[i].name==name));
            {
              el[i].checked = false;
            }
        }
};
$('#save').click(function(){
    var param={};
    param["group_code"]=role_code;
    var tr=$("tbody input[name='test'][type='checkbox']:checked").parents('tr');
    var list=[];
    for(var i=0;i<tr.length;i++){
        var action_code=$(tr[i]).attr("data-action");
        var function_code=$(tr[i]).attr("data-function");
        var param1={"function_code":function_code,"action_code":action_code};
        list.push(param1);
    }
    param["list"]=list;
    oc.postRequire("post","/user/group/check_power/save","0",param,function(data){
        console.log(data);
        if(data.code=="0"){
            $("#page-wrapper").show();
            $(".content").hide();
            sessionStorage.removeItem('group_corp');   
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
})