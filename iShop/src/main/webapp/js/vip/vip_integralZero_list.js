/**
 * Created by Administrator on 2017/5/19.
 */
var oc = new ObjectControl();
var page_size=10;
var page_num=1;
var searchValue="";
var count="";
var integraZero=sessionStorage.getItem("integraZero");
integraZero=JSON.parse(integraZero);
var corp_code=integraZero.corp_code;
var bill_no=integraZero.bill_no;
var integral_name=integraZero.integral_name;
$("#bill_no").val(bill_no);
$("#bill_no").attr("title",bill_no);
$("#integral_name").val(integral_name);
$("#integral_name").attr("title",integral_name);
function superaddition(data){
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>")
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>")
            }
        }
        $(".table tbody tr:nth-child(5)").html("<td colspan='"+len+"'><span style='font-size: 15px;color:#999'>暂无内容</span></td>");
    }
    console.log(data.length);
    if(data.length!=0) {
        for (var i = 0; i < data.length; i++) {
            var a = i + 1;
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
                + data[i].vip["vip_name"]
                + "</td><td>"
                + data[i].vip["cardno"]
                + "</td><td>"
                + data[i].vip["vip_phone"]
                +"</td><td>"
                +data[i].vip["user_name"]
                + "</td><td>"
                +data[i].vip["store_name"]
                + "</td><td>"
                +data[i].vip["join_date"]
                + "</td><td>"
                +data[i].clean_point
                + "</td><td>"
                +data[i].last_point
                +"</td></tr>");
        }
    }
}
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    });
}
function setPage(container, count,pageindex,pageSize,total) {
    var container = container;
    var count = count==0?1:count;
    var pageindex = pageindex;
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
        page_num = pageindex; //初始的页码
        $("#input-txt").val(page_num);
        $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
        oAlink[0].onclick = function() { //点击上一页
            if (page_num == 1) {
                return false;
            }
            page_num--;
            dian();
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                page_num = parseInt(this.innerHTML);
                dian();
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (page_num == count) {
                return false;
            }
            page_num++;
            dian();
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
function dian(){//点击分页的时候调什么接口
 getData();
}
 function getData(){
     var param={
         corp_code:corp_code,
         bill_no:bill_no,
         search_value:searchValue,
         page_num:page_num,
         page_size:page_size
     };
     whir.loading.add("",0.5)
    oc.postRequire("post","/vipIntegral/cleanLog","",param,function(data){
        whir.loading.remove("",0.5);
        if(data.code=="0"){
            $("#table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.list;
            page_size=message.page_size;
            page_num=message.page_num;
            count=message.pages;
            var total=message.total;
            superaddition(list,total);
            jumpBianse();
            setPage($("#foot-num")[0],count,page_num,page_size,total);
        }
    })
}
$("#turnoff,#back_integra").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip_integralZero.html");
});
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        value=this.value.trim();
        searchValue=value;
        getData(page_num,page_size);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    searchValue=value;
    getData(page_num,page_size);
});
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > count) {
        inx = count
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            page_num=inx;
            getData();
        }
    }
});
$("#page_row,.page_p .icon-ishop_8-02").click(function(){
    if("block" == $("#liebiao").css("display")){
        hideLi();
    }else{
        showLi();
    }
});
$("#liebiao li").each(function(i,v){
    $(this).click(function(){
        page_size=$(this).attr('id');
        getData();
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
getData();
