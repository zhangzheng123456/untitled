//删除的弹框
var oc = new ObjectControl();
function setPage(container, count, pageindex,pageSize,name,value) {//分页
    var container = container;
    var pageindex = pageindex;
    var name=name;
    var value=value;
    var a = [];
    var value=value;
    param["pageSize"]=pageSize;
    param["pageNumber"]=pageindex;
    // if()
    //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"prev\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"unclick\"></span></li>";
    }
    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"bg\">" + i + "</span></li>";
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
        } else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        } else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"xnclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"next\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        var inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            setPage(container, count, inx,pageSize,name,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                setPage(container, count, inx,pageSize,name,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            setPage(container, count, inx,pageSize,name,value);
            return false;
        }
    }()
    if(name==''&&value==''){
        oc.postRequire("get","user/list?pageNumber="+pageindex
        +"&pageSize="+pageSize+"","","",function(data){
            if(data.code=="0"){
                $(".table tbody").empty();
                message=JSON.parse(data.message);
                content=message.content;
                cout=message.totalPages;
                superaddition(content);
                jumpBianse();
            }
        });
    }else if(name!=''||value!=''){
        oc.postRequire("post","user/list/search","0",param,function(data){
            console.log(data);
            if(data.code=="0"){
                message=JSON.parse(data.message);
                content=message.content;
                if(content.length>0){
                    $(".table tbody").empty();
                    $(".table p").remove();
                    superaddition(content);
                    jumpBianse();
                }
            }else if(data.code=="-1"){
                alert(data.message);
            }
        })
    }
}
//隔行变色
    $(document).ready(function(){//隔行变色 
         $(".table tbody tr:even").css("backgroundColor","#f1f1f1");
    })
    //双击跳转
    $(".table tbody tr").dblclick(function(){
        id=$(this).attr("id");
        window.location.href="user_detail?id="+id+"";
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
