var oc = new ObjectControl();
var pageNumber=1;//默认是第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var funcCode=$(window.parent.document).find('#iframepage').attr("data-code");
console.log(funcCode);
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
                message=JSON.parse(data.message);
                console.log(message);

                // cout=message.totalPages;
                superaddition(message);
                setPage($("#foot-num")[0],cout,pageNumber,pageSize,value);
                jumpBianse();
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
GET();