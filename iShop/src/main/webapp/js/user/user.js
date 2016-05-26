var oc = new ObjectControl();
var pageNumber=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var funcCode=$(window.parent.document).find('#iframepage').attr("data-code");
console.log(funcCode);
function setPage(container, count, pageSize,funcCode,value) {//分页
    var container = container;
    var count = count;
    var pageSize = pageSize;
    var a = [];
              //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageSize == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }
    function setPageList() {
        if (pageSize == i) {
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
        if (pageSize <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }else if (pageSize >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        }
        else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageSize - 2; i <= pageSize + 2; i++) {
                setPageList();
            }
                a[a.length] = "...<li><span>" + count + "</span></li>";
            }
        }
    if (pageSize == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    }else{
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        var inx = pageSize; //初始的页码
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
function superaddition(data){//页面加载循环
	console.log(data);
    for (var i = 0; i < data.length; i++) {
        $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div>"
                        + "</td><td style='text-align:left;'>"
                        + data[i].id
                        + "</td><td>"
                        + data[i].email
                        + "</td><td>"
                        + data[i].user_name
                        + "</td><td>"
                        + data[i].sex
                        +"</td><td>"
                        +data[i].phone
                        + "</td><td>"
                        +data[i].corp_code
                        + "</td><td>"
                        +data[i].login_time_recently
                        + "</td><td>"
                        +data[i].role_code
                        + "</td><td>"
                        +data[i].modifier
                        + "</td><td>"
                        +data[i].modified_date
                        + "</td><td>"
                        +data[i].isactive
                        +"</td></tr>");
    }
};
function GET(){//页面加载时的GET请求
    oc.postRequire("get","/user/list?pageNumber="+pageNumber+"&pageSize="+pageSize
        +"&funcCode="+funcCode+"","","",function(data){
        	console.log(data);
            if(data.code=="0"){
            	$(".table tbody").empty();
                message=JSON.parse(data.message);
                console.log(message);
                // cout=message.totalPages;
                superaddition(message);
                setPage($("#foot-num")[0],cout,pageNumber,pageSize,funcCode,value);
                jumpBianse();
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
GET();
//鼠标按下时触发的收索
$("#search").keydown(function() {
	var event=window.event||arguments[0];
    value=this.value.replace(/\s+/g,"");
    var param={};
	param["value"]=value;
	param["pageNumber"]=pageNumber;
	param["pageSize"]=pageSize;
	if(event.keyCode == 13) {
		if(name==""||name=="10"){
			alert("请先选择字段");
			return;
		}
		POST();
		console.log(name);
		console.log(param);
	}
});
//搜索的请求函数
function POST(){
	oc.postRequire("post","logic/list/search","0",param,function(data){
		if(data.code=="0"){
			message=JSON.parse(data.message);
			// content=message.content;
			// cout=message.totalPages;
			$(".table tbody").empty();
			if(content.length<=0){
				$(".table p").remove();
				$(".table").append("<p>没有找到与"+value+"相关的信息请重新搜索</p>")
		 	}else if(content.length>0){

		 	}
		 	setPage($("#foot-num")[0],cout,pageNumber,pageSize,funcCode,value);
		}else if(data.code=="-1"){
			alert(data.message);
		}
	})
}