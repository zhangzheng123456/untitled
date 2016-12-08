var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
// var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var corp_code="";
var group_code="";
var group_corp=sessionStorage.getItem("group_corp");//取本地的群组编号
if(group_corp!==null){
    group_corp=JSON.parse(group_corp);
    corp_code=group_corp.corp_code;//企业编号
    group_code=group_corp.group_code;//群组编号
    var group_name=group_corp.group_name;//群组名称
    $('#group_code').val(group_code);
    $('#group_name').val(group_name);
    $("#page-wrapper").hide();
    $(".content").show();
    GET();
}
//编辑页面点击弹出角色权限的框
$('#edit_power').click(function(){
    group_code=$("#GROUP_ID").val();
    corp_code=$('#OWN_CORP').val();
    var group_name=$('#GROUP_NAME').val();
    $('#group_code').val(group_code);
    var group_corp={"corp_code":corp_code,"group_code":group_code,"group_name":group_name};
    sessionStorage.setItem("group_corp",JSON.stringify(group_corp));
    $(window.parent.document).find('#iframepage').attr("src","user/usercheck_power1.html");
    $('#group_name').val(group_name);
    $("#page-wrapper").hide();
    $(".content").show();
    // GET();
})
//新增页面点击弹出角色权限的框
$("#add_power").click(function(){
   group_code=$("#GROUP_ID").val();
   corp_code=$('#OWN_CORP').val();
   $("#page-wrapper").hide();
   $(".content").show();
   GET();
})
//点击查看名单的时候的操作
$("#check_name").click(function(){
    var group_code=$("#GROUP_ID").val();
    var corp_code=$('#OWN_CORP').val();
    var group_name=$('#GROUP_NAME').val();
    var check_name={"group_code":group_code,"corp_code":corp_code,"group_name":group_name};
    sessionStorage.setItem("check_name",JSON.stringify(check_name));//保存到本地
    $(window.parent.document).find('#iframepage').attr("src","user/groupcheck_name.html");
});
//关闭弹框
$('#turnoff').click(function(){
    $("#page-wrapper").show();
    $(".content").hide();
    sessionStorage.removeItem('group_corp');
});
//回到编辑群组
$('#back_group_edit').click(function(){
    $("#page-wrapper").show();
    $(".content").hide();
    sessionStorage.removeItem('group_corp');
});
//回到群组管理
$("#back_group_1").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/user/group.html");
});
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
function superaddition(data,num,die,live){
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
    for(var j=0;j<die.length;j++){
        if($("#"+die[j].id).find("input").length>0){
            $("#"+die[j].id).find("input").parent("div").attr("class","checkbox2");
            $("#"+die[j].id).find("input")[0].checked=true;
            $("#"+die[j].id).find("input").attr("disabled","true");
            $("#"+die[j].id).find("input").attr("name","die");
        }
    }
    for(var p=0;p<live.length;p++){
        if($("#"+live[p].id).find("input").length>0){
            $("#"+live[p].id).find("input")[0].checked=true;
        }
    }
};
//页面加载时list请求
function GET(){
    // param["pageNumber"]=inx;
    // param["pageSize"]=pageSize;
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
    oc.postRequire("post","/user/group/check_power1","0",param,function(data){
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var die=message.die;
                var live=message.live;
                var list=JSON.parse(message.list);
                var actions=message.actions;
                superaddition(list,inx,die,live);
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
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
        POST();
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
//点击列表显把一组数据放进数组里面
$("#table").on("click",'tr',function(){
    var action_code=$(this).attr("data-action");
    var function_code=$(this).attr("data-function");
})
//搜索的请求函数
function POST(){
    oc.postRequire("post","/user/group/check_power","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var die=message.die;
            var live=message.live;
            var list=JSON.parse(message.list);
            jumpBianse();
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,inx,die,live);
                jumpBianse();
            }
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
    oc.postRequire("post","/corp/delete","0",param,function(data){
        if(data.code=="0"){
            if(value==""){
               frame();
               $('.frame').html('删除成功');
               GET(); 
            }else if(value!==""){
               frame();
               $('.frame').html('删除成功');
               POST();
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
    var el=$("tbody input[name='"+name+"']");
    var len = el.length;
    for(var i=0; i<len; i++)
        {
            if((el[i].type=="checkbox") && (el[i].name==name));
            {
              el[i].checked = true;
            }
        }
};

//取消全选
function clearAll(name){
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
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
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
        if(data.code=="0"){
            $("#page-wrapper").show();
            $(".content").hide();
            window.location.reload();
            sessionStorage.removeItem('group_corp');   
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
});
//刷新列表
$(".icon-ishop_6-07").parent().click(function () {
    window.location.reload();
});