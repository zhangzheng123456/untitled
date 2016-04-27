//删除的弹框
$(function(){
    var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值

     //左侧导航栏
    $(".sidebar ul li dl dd").click(function(e){
        e.stopPropagation();
        $(this).find("span").addClass("icon-ishop_8-01");
        $(this).find("a").css({color:"#6cc1c8"});
        $(this).siblings("dd").find("a").css({color:"#fff"});
        $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
    })
    $(".sidebar .log").click(function(e){
        $(this).next("ul").slideToggle(300);
        $(this).toggleClass("h1");
        $(this).find("i").toggleClass("icon-ishop_8-02");
    })
    $(".sidebar ul li").click(function(e){
        e.stopPropagation();
        $(this).find("h1").next("dl").slideToggle(300).parents().siblings("li").find("dl").slideUp(300);
        $(this).find("h1").toggleClass("h1").parents().siblings("li").find("h1").removeClass("h1");
        $(this).find("h1 span").toggleClass("icon-ishop_8-02").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");   
    })
    $(".sidebar ul li dl ul li").click(function(e){
        e.stopPropagation();
        $(this).find("span").addClass("icon-ishop_8-01");
        $(this).find("a").css({color:"#6cc1c8"});
         $(this).siblings("dd").find("a").css({color:"#fff"});
        $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
    })
})
//左侧导航栏折叠功能
var flag=1;
$("#btn").click(function(e){
    if(flag==1){
        $("#sidebar").animate({left: '-200px'},300);
        $("#con_table").animate({marginLeft: '20px'},300);
        $(this).removeClass('icon-ishop_7-02');
        $(this).addClass('icon-ishop_7-01');
        flag=0;
    }else{
        $("#sidebar").animate({left: '0'},300);
        $("#con_table").animate({marginLeft: '220px'},300);
        $(this).removeClass('icon-ishop_7-01');
        $(this).addClass('icon-ishop_7-02');
        flag=1;
    }
});
 //表格隔行变色
$(document).ready(function(){ 
    $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
    $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
})
//表格全选
function checkAll(name){
    var el=$("input");
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
    var el=$("input");
    var len = el.length;
    for(var i=0; i<len; i++)
      {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
          el[i].checked = false;
        }
      }
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
    tr.remove();
    $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
    $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
})  
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
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $("#tk").css({"left":+left+"px","top":+tp+"px"});
})
//排序
$("#sort").click(function(){
    var oTable=$("#table")[0];
    console.log(oTable);
    var arr=[];
    $(this).toggleClass("icon-ishop_7-04");
    for(var i=0;i<oTable.tBodies[0].rows.length;i++){
        arr[i]=oTable.tBodies[0].rows[i];
    }
    for(var i=arr.length;i>=0;i--){
        $("#table tbody").append(arr[i]);
    };
    $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
    $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
})
//分页
function setPage(container, count, pageindex) {
    var container = container;
    var count = count;
    var pageindex = pageindex;
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
    //事件点击
    $("#input-txt").keydown(function(){
      var inx=this.value.replace(/[^1-9]/g,'');
      if(inx>count){inx=count};
      if(inx>0){
        if(event.keyCode==13){setPage(container, count, inx);};
      }
    })
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        var inx = pageindex; //初始的页码
        // console.log(inx);
        // console.log(count);
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            setPage(container, count, inx);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
            inx = parseInt(this.innerHTML);
                setPage(container, count, inx);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            setPage(container, count, inx);
            return false;
        }
    }()
}
setPage($("#foot-num")[0],11,1);
//模仿select的语句
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
//hover事件个人菜单出现
function show(node,isShow){
    var obj=$(node);
    var show=isShow?"block":"none";
    obj[0].style.display=show;
}
//筛选
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
//点击查找
$("#find").click(function(){

})

