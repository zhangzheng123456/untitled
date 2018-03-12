var oc = new ObjectControl();
var pageNumber=1;//
var pageSize=10;//默认传的每页多少行

var approved_detail=JSON.parse(sessionStorage.getItem("approved_detail"));
$("#approved_date").val(approved_detail.created_date);
$("#approved_name").val(approved_detail.approved_name);
delete approved_detail.approved_name;

function superaddition(data,num){//页面加载循环
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
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
    for (var i = 0; i < data.length; i++) {
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        var day="";
        if(data[i].approved_type=="Y"){
            day="每年"+data[i].approved_cycle.split("-")[0]+"月"+data[i].approved_cycle.split("-")[1]+"日"
        }else if(data[i].approved_type=="M"){
            day="每月"+data[i].approved_cycle+"日"
        }
        $(".table tbody").append("<tr id='"+data[i].approved_id+"' data-date='"+data[i].created_date+"' data-name='"+data[i].approved_name+"' data-corp_code='"+data[i].corp_code+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:left;'>"
            + a
            + "</td>"
            +"<td>"+data[i].vip["vip_name"]+"</td>"
            +"<td>"+data[i].vip["cardno"]+"</td>"
            +"<td>"+data[i].vip["vip_phone"]+"</td>"
            +"<td>"+data[i].old_card_type+"</td>"
            +"<td>"+data[i].new_card_type+"</td>"
            +"<td>"+data[i].vip["user_name"]+"</td>"
            +"<td>"+data[i].vip["store_name"]+"</td>"
            +"<td>"+data[i].vip["vip_birthday"]+"</td>"
            +"<td>"+data[i].vip["join_date"]+"</td>"
            +"</tr>");

    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
}
function setPage(container, count, pageindex,pageSize,total) {
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
        pageNumber = pageindex; //初始的页码
        $("#input-txt").val(pageNumber);
        $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
        oAlink[0].onclick = function() { //点击上一页
            if (pageNumber == 1) {
                return false;
            }
            pageNumber--;
            dian(pageNumber,pageSize);
            // setPage(container, count, pageNumber,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                pageNumber = parseInt(this.innerHTML);
                dian(pageNumber,pageSize);
                // setPage(container, count, pageNumber,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (pageNumber == count) {
                return false;
            }
            pageNumber++;
            dian(pageNumber,pageSize);
            // setPage(container, count, pageNumber,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(){//点击分页的时候调什么接口
    GET();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            pageNumber=inx;
           GET();
        }
    }
});
//模仿select
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
        pageNumber=1;
        GET();
        $("#page_row").val($(this).html());
        hideLi();
    });
});
$("#page_row").blur(function(){
    setTimeout(hideLi,200);
});
function showLi(){
    $("#liebiao").show();
}
function hideLi(){
    $("#liebiao").hide();
}
$("#filtrate").click(function(){//点击筛选框弹出下拉框
    $(".sxk").slideToggle();
});
$("#pack_up").click(function(){//点击收回 取消下拉框
    $(".sxk").slideUp();
});
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    });
}
function GET(){
    whir.loading.add("",0.5);//加载等待框
    var param=approved_detail;
    param.pageNumber=pageNumber;
    param.pageSize=pageSize;
    oc.postRequire("post","/approved/approvalDetails","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.details;
            cout=message.pages;
            var pageNum = message.page_number;
            var total = message.total;
            allData=message.total;
            superaddition(list,pageNum);
            jumpBianse();
            setPage($("#foot-num")[0],cout,pageNum,pageSize,total);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
$("#turnoff,#back_approved").click(function(){
    sessionStorage.removeItem("approved_detail");
    $(window.parent.document).find('#iframepage').attr("src","/vip/approval_record.html");
});
$(function(){
    GET();
});