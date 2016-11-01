var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var group_corp=sessionStorage.getItem("check_name");//取本地的群组编号
group_corp=JSON.parse(group_corp);
var corp_code=group_corp.corp_code;//企业编号
var group_code=group_corp.group_code;//群主编号
$("#group_code").val(group_code);
$("#group_name").val(group_corp.group_name);
//关闭弹框
$('#turnoff').click(function(){
    sessionStorage.removeItem("check_name");
    $(window.parent.document).find('#iframepage').attr("src","user/group_edit.html");
});
//返回编辑群组
$('#back_edit_group').click(function(){
    sessionStorage.removeItem("check_name");
    $(window.parent.document).find('#iframepage').attr("src","user/group_edit.html");
});
$("#back_group").click(function(){
    sessionStorage.removeItem("check_name");
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
function setPage(container, count, pageindex,pageSize,group_code,corp_code,value) {
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
        var inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx);
            setPage(container, count, inx,pageSize,group_code,corp_code,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
            inx = parseInt(this.innerHTML);
                dian(inx);
                setPage(container, count, inx,pageSize,group_code,corp_code,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx);
            setPage(container, count, inx,pageSize,group_code,corp_code,value);
            return false;
        }
    }()
    function dian(inx){//
        if(value==""){
            var param={};
            value=this.value.replace(/\s+/g,"");
            param["searchValue"]=value;
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["group_code"]=group_code;
            param["corp_code"]=corp_code;
            oc.postRequire("post","/user/group/check_name","0",param,function(data){
                console.log(data);
                    if(data.code=="0"){
                        $(".table tbody").empty();
                        var message=JSON.parse(data.message);
                        var list=JSON.parse(message.list);
                        var cout=list.pages;
                        var list=list.list;
                        console.log(list);
                        var actions=message.actions;
                        superaddition(list,inx);
                        // jurisdiction(actions);
                        jumpBianse();
                    }else if(data.code=="-1"){
                        alert(data.message);
                    }
            });
        }else if(value!==""){
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            oc.postRequire("post","/user/group/check_name","0",param,function(data){
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
                        superaddition(list,inx);
                        jumpBianse();
                    }
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            })      
        }
    }
}
function superaddition(data,num){
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
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        $(".table tbody").append("<tr id='"+data[i].id+"''>"
                        + "</td><td style='text-align:left;padding-left:22px'>"
                        + a
                        + "</td><td>"
                        + data[i].user_code
                        + "</td><td>"
                        + data[i].user_name
                        + "</td><td>"
                        + data[i].sex
                        +"</td><td>"
                        +data[i].phone
                        + "</td><td>"
                        +data[i].corp.corp_name
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
};
function GET(){
    var param={};
    value=this.value.replace(/\s+/g,"");
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
    oc.postRequire("post","/user/group/check_name","0",param,function(data){
        console.log(data);
            if(data.code=="0"){
                $(".table tbody").empty();
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var cout=list.pages;
                var list=list.list;
                console.log(list);
                var actions=message.actions;
                superaddition(list,inx);
                // jurisdiction(actions);
                jumpBianse();
                setPage($("#foot-num")[0],cout,inx,pageSize,group_code,corp_code,value);
            }else if(data.code=="-1"){
                alert(data.message);
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
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
        POST();
    }
});
//放大镜搜索
$("#d_search").click(function () {
    value=$("#search").val().replace(/\s+/g,"");
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["group_code"]=group_code;
    param["corp_code"]=corp_code;
    POST();
})
//搜索的请求函数
function POST(){
    oc.postRequire("post","/user/group/check_name","0",param,function(data){
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
                superaddition(list,inx);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,inx,pageSize,group_code,corp_code,value);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}