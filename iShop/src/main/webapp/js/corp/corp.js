//删除的弹框
var oc = new ObjectControl();
var pageNumber=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词 
// function setPage(container, count, pageindex,value) {
//     var container = container;
//     var count = count;
//     var pageindex = pageindex;
//     var a = [];
//               //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
//     if (pageindex == 1) {
//         a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
//     } else {
//         a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
//     }
//     function setPageList() {
//         if (pageindex == i) {
//             a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
//         } else {
//             a[a.length] = "<li><span>" + i + "</span></li>";
//         }
//     }
//     //总页数小于10
//     if (count <= 10) {
//         for (var i = 1; i <= count; i++) {
//             setPageList();
//         }
//     }
//     //总页数大于10页
//     else {
//         if (pageindex <= 4) {
//             for (var i = 1; i <= 5; i++) {
//                 setPageList();
//             }
//             a[a.length] = "...<li><span>" + count + "</span></li>";
//         }else if (pageindex >= count - 3) {
//             a[a.length] = "<li><span>1</span></li>...";
//             for (var i = count - 4; i <= count; i++) {
//                 setPageList();
//             }
//         }
//         else { //当前页在中间部分
//             a[a.length] = "<li><span>1</span></li>...";
//             for (var i = pageindex - 2; i <= pageindex + 2; i++) {
//                 setPageList();
//             }
//                 a[a.length] = "...<li><span>" + count + "</span></li>";
//             }
//         }
//     if (pageindex == count) {
//         a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
//     }else{
//         a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
//     }
//     container.innerHTML = a.join("");
//     var pageClick = function() {
//         var oAlink = container.getElementsByTagName("span");
//         var inx = pageindex; //初始的页码
//         // console.log(inx);
//         // console.log(count);
//         $("#input-txt").val(inx);
//         $(".foot-sum .zy").html("共 "+count+"页");
//         oAlink[0].onclick = function() { //点击上一页
//             if (inx == 1) {
//                 return false;
//             }
//             inx--;
//             setPage(container, count, inx);
//             return false;
//         }
//         for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
//             oAlink[i].onclick = function() {
//             inx = parseInt(this.innerHTML);
//                 setPage(container, count, inx);
//                 return false;
//             }
//         }
//         oAlink[oAlink.length - 1].onclick = function() { //点击下一页
//             if (inx == count) {
//                 return false;
//             }
//             inx++;
//             setPage(container, count, inx);
//             return false;
//         }
//     }()
// }
// setPage($("#foot-num")[0],11,1);
//页面加载循环
function superaddition(data){
	console.log(data);
    for (var i = 0; i < data.length; i++) {
        $(".table tbody").append("<tr id='"+data[i].id+"' data-id='"+data[i].is_admin+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div>"
                        + "</td><td>"
                        + data[i].corp_code
                        + "</td><td style='text-align:left;'>"
                        + data[i].corp_name
                        + "</td><td>"
                        + data[i].address
                        + "</td><td>"
                        + data[i].contact
                        +"</td><td>"
                        +data[i].contact_phone
                        + "</td><td>"
                        +data[i].modifier
                        + "</td><td>"
                        +data[i].modified_date
                        + "</td><td>"
                        +data[i].creater
                        +"</td></tr>");
    }
};
function GET(){//页面加载时的GET请求
    oc.postRequire("get","/corp/list?pageNumber="+pageNumber
        +"&pageSize="+pageSize+"","","",function(data){
        	console.log(data);
            if(data.code=="0"){
                message=JSON.parse(data.message);
                content=message.corpInfo;
                cout=message.totalPages;
                superaddition(content);
                setPage($("#foot-num")[0],cout,pageNumber,pageSize,value);
                jumpBianse();
            }else if(data.code=="-1"){
                // alert(data.message);
            }
        });
}
GET();
//隔行变色
function jumpBianse(){
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
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
                thinput.checked=false;
            }
            input.checked = false;
        }
    })
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
