var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageNumber=1;//默认删除是第一页
var pageSize=10;//默认传的每页多少行
var param={};//定义的对象
var list="";
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
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
                    param["funcCode"]=funcCode;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
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
function GetRequest(){
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if(url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}
function setPage(container, count, pageindex,pageSize,total) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
    var total=total;
    var a = [];//总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
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
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
    }()
}
function dian(a,b){//点击分页的时候调什么接口
    POST(a,b);
}
//页面加载循环
function superaddition(list,num){//页面加载循环
    // if(data.length==1&&num>1){
    //     pageNumber=num-1;
    // }else{
    //     pageNumber=num;
    // }
    pageNumber=num;
    console.log(list);
    for(var i=0;i<list.length;i++){
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        var img="";
        var task_back="";
        $(".table #table tbody").append("<tr><td width='50px;' style='text-align: center;'>"
            + a
            + "</td><td>"
            + list[i].user_name
            + "</td><td>"
            + list[i].user_code
            + "</td><td>"
            + list[i].brand_name
            + "</td><td><span title='"+list[i].store_name+"'>"
            + list[i].store_name
            + "</span></td><td>"
            +list[i].sum
            +"</td><td>"
            + list[i].send_num
            + "</td><td>"
            + list[i].recruit_num
            +"</td></tr>");
    }
    $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
    $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    whir.loading.remove();//移除加载框
};
//搜索的请求函数
function POST(pageNumber,pageSize){
    whir.loading.add("",0.5);//加载等待框
    param["pageNumber"]=pageNumber;
    param["pageSize"]=pageSize;
    param["id"]=GetRequest().id;
    value=$("#search").val().replace(/\s+/g,"");
    param["search_value"]=value;
    oc.postRequire("post","/sendTicket/userlist","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var total = list.total;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            setPage($("#foot-num")[0],cout,pageNum,pageSize,total);
            if(list.length<=0){
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
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
});
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

};
$("#dao").click(function(){
    $("#p").hide();
    $("#code_ma").hide();
    whir.loading.remove();//加载等待框
});
$("#code_q").click(function(){
    $("#p").hide();
    $("#code_ma").hide();
    whir.loading.remove();//加载等待框
});
//导出关闭按钮
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
});
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
            POST(inx,pageSize);
        };
    }
});
$("#turnoff").click(function(){
    $(window.parent.document).find('#iframepage').attr("src", "/vip/coupon_list.html");
});
$("#back_task").click(function(){
    $(window.parent.document).find('#iframepage').attr("src", "/vip/coupon_list.html");
});
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    inx=1;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["search_value"]=value;
        POST(inx,pageSize);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    param["search_value"]=value;
    POST(pageNumber,pageSize);
})
$(function(){
    POST(pageNumber,pageSize);
})