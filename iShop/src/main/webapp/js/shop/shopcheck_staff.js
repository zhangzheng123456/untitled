var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var value="";//收索的关键词
var param={};//定义的对象
var store_corp=sessionStorage.getItem("store_corp");//获取本地储存的store_corp值
store_corp=JSON.parse(store_corp);//转成json格式
var store_code=store_corp.store_code;//店仓编号
var corp_code=store_corp.corp_code;//企业编号
$("#store_code").val(store_code);
$("#store_name").val(store_corp.store_name);
function superaddition(data){
    console.log(data);
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>")
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>")
            }
        }
        $(".table tbody tr:nth-child(5) td:nth-child(6)").append("<span style='font-size: 15px;color:#999'>暂无内容</span>");
    }
    console.log(data.length);
if(data.length!=0) {
    for (var i = 0; i < data.length; i++) {
        var a = i + 1;
        var avatar="";
        if(data[i].avatar==undefined||data[i].avatar==""){
            avatar="../img/head.png";
        }
        if(data[i].avatar!==""&&data[i].avatar!==undefined){
            avatar=data[i].avatar;
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
                        +"</td><td>"
                        + data[i].user_code
                        + "</td><td><img src='"+avatar+"' alt=''>"
                        + "</td><td>"
                        + data[i].user_name
                        + "</td><td>"
                        + data[i].sex
                        +"</td><td>"
                        +data[i].phone
                        + "</td><td>"
                        +data[i].group.group_name
                        + "</td><td>"
                        +data[i].modifier
                        + "</td><td>"
                        +data[i].modified_date
                        + "</td><td>"
                        +data[i].isactive
                        +"</td></tr>");
        }
}
};
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li class='bg' id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
//页面加载时list请求
function GET(){
    var param={};
    param["store_code"]=store_code;
    param["corp_code"]=corp_code;
    oc.postRequire("post","/shop/staff_list","list",param,function(data){
            console.log(data);
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                // var list=JSON.parse(message.list);
                console.log(message);
                var actions=message.actions;
                superaddition(message);
                // jurisdiction(actions);
                jumpBianse();
                // setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value);
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
    });
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
    });
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            alert("请先选中所选项");
            return;
        }
        $("#p").show();
        $("#tk").show();
        console.log(left);
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
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
        param["user_id"]=ID;
        param["store_code"]=store_code;
        console.log(param);
        oc.postRequire("post","/shop/staff/delete","0",param,function(data){
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
            var thinput=$("thead input")[0];
            thinput.checked =false;
            }else if(data.code=="-1"){
                frame();
                $('.frame').html(data.message);
            }
        })
    })
    $('#turnoff').click(function(){
        sessionStorage.removeItem("store_corp");
        window.history.go(-1)
        // $(window.parent.document).find('#iframepage').attr("src","/shop/shop.html");
    })
    $('#back_shop').click(function(){
        sessionStorage.removeItem("store_corp");
        // $(window.parent.document).find('#iframepage').attr("src","/shop/shop.html");
        window.history.go(-1)
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
    oc.postRequire("post","/shop/search","0",param,function(data){
        console.log(data);
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var cout=list.pages;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到与"+value+"相关的信息，请重新搜索</p>")
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,inx,pageSize,funcCode,value);
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