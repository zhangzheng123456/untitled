var oc = new ObjectControl();
var inx=1;//默认是第一页
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var titleArray=[];
var key_val=sessionStorage.getItem("key_val");//取function_code的值
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
return_jump=JSON.parse(return_jump);
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
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
    value="";
    filtrate="";
    inx=1;
    $('#search').val("");
    $(".table p").remove();
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    param["searchValue"]="";
    GET(inx,pageSize);
    getAction();
})
function setPage(container, count, pageindex,pageSize,funcCode,total) {
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
    if (value==""&&filtrate=="") {
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["funcCode"]=funcCode;
        param["searchValue"]="";
        GET(a,b);
    }else if (value!==""){
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
    }
}
//页面加载循环
function superaddition(data,num){
    // if(data.length==1&&num>1){
    //     pageNumber=num-1;
    // }else{
    //     pageNumber=num;
    // }
    pageNumber=num;
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        var isonline="";
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
               if(code=="avatar"){
                   TD+="<td><img src='"+avatar+"' alt=''></td>";
               }else{
                   TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
               }
            })(c)
        }
        $(".table tbody").append("<tr id='"+data[i].id+"'' data-code='"+data[i].corp_code+"' data-user_code='"+data[i].user_code+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                        + i
                        + 1
                        + "'/><label for='checkboxTwoInput"
                        + i
                        + 1
                        + "'></label></div>"
                        + "</td><td style='text-align:left;'>"
                        + a
                        +"</td>"+
                        TD +
                        "</tr>");
    }
    whir.loading.remove();//移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
}
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a><div class='fen'><div id='admin'>新增系统管理员</div><div id='corp'>新增企业用户</div></div></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="qrcode"){
            $('#jurisdiction').append("<li id='qrcode'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>生成二维码</a></li>");
        }else if(actions[i].act_name=="signIn"){
            $('#more_down').append("<div id='signIn' style='font-size: 10px;'>签到</div>");
        }else if(actions[i].act_name=="signOut"){
            $('#more_down').append("<div id='signOut' style='font-size: 10px;'>签退</div>");
        }else if(actions[i].act_name=="synchronization"){
            $('#more_down').append("<div id='synchronization' style='font-size: 10px;'>同步</div>");
        }else if(actions[i].act_name=="output"){
            $("#more_down").append("<div id='leading_out' style='font-size: 10px;'>导出</div>");
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into' style='font-size: 10px;'>导入</div>");
        } else  if(actions[i].act_name=="output_qrcode"){
            $('#more_down').append("<div id='output_qrcode' style='font-size: 10px;'>导出二维码</div>");
        }else  if(actions[i].act_name=="output_zip"){
            $('#more_down').append("<div id='output_zip' style='font-size: 10px;'>打包二维码</div>");
        }else  if(actions[i].act_name=="batch_dismiss"){
            $('#more_down').append("<div id='batch_dismiss' style='font-size: 10px;'>批量离职</div>");
        }else  if(actions[i].act_name=="batch_callback"){
            $('#jurisdiction').append("<li id='batch_callback'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>设置回访方式</a></li>");
        }
    }
    quanXian();
}
//导出二维码
$("#more_down").on("click","#output_qrcode",function(){
    var param={};
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/user/exportExecl_view","0",param,function(data){
        if(data.code=="0"){
            var url=JSON.parse(data.message);
            $("#code_ma p").html("是否确认导出二维码?");
            $("#code_ma").show();
            $("#code_ma").css("z-index","10002");
            whir.loading.add("mask",0.5);//加载等待框
            $("#enter").html("<a href='/"+JSON.parse(url.path)+"' target='_blank'>确认</a>");
        }else if(data.code=="-1"){
            alert(data.message);
        }
        whir.loading.remove();//移除加载框
    })
})
$("#dao").click(function(){
    $("#code_ma").hide();
    whir.loading.remove();//移除加载框
})
$("#X1").click(function(){
    $("#batch").hide();
    whir.loading.remove();//移除加载框
})
$("#cancel1").click(function(){
    $("#batch").hide();
    whir.loading.remove();//移除加载框
})
$("#code_q").click(function(){
    $("#code_ma").hide();
    whir.loading.remove();//移除加载框
})
function InitialState(){
    if(return_jump!==null){
        inx=return_jump.inx;
        pageSize=return_jump.pageSize;
        value=return_jump.value;
        filtrate=return_jump.filtrate;
        list=return_jump.list;
        param=JSON.parse(return_jump.param);
        _param=JSON.parse(return_jump._param);
    }
    if(return_jump==null){
        if(value==""&&filtrate==""){
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            param["searchValue"]="";
            GET(inx,pageSize);
        }
    }else if(return_jump!==null){
        if(pageSize==10){
            $("#page_row").val("10行/页");
        }
        if(pageSize==30){
            $("#page_row").val("30行/页");
        }
        if(pageSize==50){
            $("#page_row").val("50行/页");
        }
        if(pageSize==100){
            $("#page_row").val("100行/页");
        }
        if(value==""&&filtrate==""){
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["funcCode"]=funcCode;
            param["searchValue"]="";
            GET(inx,pageSize);
        }else if(value!==""){
            $("#search").val(value);
            POST(inx,pageSize);
        }else if(filtrate!==""){
            filtrates(inx,pageSize);
        }
    }
}
//页面加载调权限接口
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        titleArray=message.columns;
        jurisdiction(actions);
        InitialState();
        tableTh();
        getAction();
    })
}
function tableTh(){ //table  的表头
    var TH="";
    for(var i=0;i<titleArray.length;i++){
        TH+="<th>"+titleArray[i].show_name+"</th>"
    }
    $("#tableOrder").after(TH);
};
function quanXian(){
    $(document).ready(function(){//隔行变色 
         $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
    })
     //点击新增时页面进行的跳转
    $('#add').click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/reply/auto_reply_add.html");
        sessionStorage.removeItem('id');
    })
    //点击编辑时页面进行的跳转
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            id=$(tr).attr("id");
            var return_jump={};//定义一个对象
            return_jump["inx"]=inx;//跳转到第几页
            return_jump["value"]=value;//搜索的值;
            return_jump["filtrate"]=filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
            return_jump["list"]=list;//筛选的请求的list;
            return_jump["pageSize"]=pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.setItem("id",id);
            $(window.parent.document).find('#iframepage').attr("src","/reply/auto_reply_edit.html");
        }else if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
        }else if(tr.length>1){
            frame();
            $('.frame').html("不能选择多个");
        }
    })
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
    })
}
qjia();
//页面加载时list请求
function GET(a,b){
    whir.loading.add("",0.5);//加载等待框
    //oc.postRequire("get","/user/list?pageNumber="+a+"&pageSize="+b
    //    +"&funcCode="+funcCode+"","","",function(data){
    param["searchType"]="1";
    oc.postRequire("post","/reply/search","0",param,function(data){
            if(data.code=="0"){
            	$(".table tbody").empty();
                var message=JSON.parse(data.message);
                var list=message.list;
                cout=message.pages;
                var pageNum = message.page_number;
                var total = message.total;
                superaddition(list,pageNum);
                jumpBianse();
                setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
            }else if(data.code=="-1"){
                alert(data.message);
            }
    });
}
//加载完成以后页面进行的操作
function jumpBianse(){
	$(document).ready(function(){//隔行变色 
   		 $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
    	 $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
         $("#jurisdiction li:odd").css("backgroundColor","#f4f4f4");
	})
	//双击跳转
	$(".table tbody tr").dblclick(function(){
	    var id=$(this).attr("id");
        var return_jump={};//定义一个对象
        return_jump["inx"]=inx;//跳转到第几页
        return_jump["value"]=value;//搜索的值;
        return_jump["filtrate"]=filtrate;//筛选的值
        return_jump["param"]=JSON.stringify(param);//搜索定义的值
        return_jump["_param"]=JSON.stringify(_param)//筛选定义的值
        return_jump["list"]=list;//筛选的请求的list;
        return_jump["pageSize"]=pageSize;//每页多少行
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        if(id == "" || id == undefined){
            return ;
        }else{
            $(window.parent.document).find('#iframepage').attr("src","/reply/auto_reply_edit.html");
        }
	});
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
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    inx=1;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
        POST(inx,pageSize);
    }
});
//点击放大镜触发搜索
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    inx=1;
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    POST(inx,pageSize);
})
//搜索的请求函数
function POST(a,b){
    whir.loading.add("",0.5);//加载等待框
    $('#search').attr("disabled",true);
    param["searchType"]="1";
	oc.postRequire("post","/reply/search","0",param,function(data){
		if(data.code=="0"){
			$(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.list;
            cout=message.pages;
            var pageNum = message.page_number;
            var total = message.total;
			if(list.length<=0){
				$(".table p").remove();
				$(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
                $('#search').removeAttr("disabled");
		 	}else if(list.length>0){
                $(".table p").remove();
                $('#search').removeAttr("disabled");
		 		superaddition(list,pageNum);
                jumpBianse();
		 	}
            filtrate="";
            list="";
            getAction();
            $(".sxk").slideUp();
		 	setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
		}else if(data.code=="-1"){
			alert.log(data.message);
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
//弹框删除关闭
$("#delete").click(function(){
    $("#p").hide();
    $("#tk").hide();
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    for(var i=tr.length-1,ID="";i>=0;i--){
        var r=$(tr[i]).attr("id");
        if(i>0){
            ID+=r+",";
        }else{
             ID+=r;
        }     
    }
    var params={};
    params["id"]=ID;
    oc.postRequire("post","/reply/delete","0",params,function(data){
       if(data.code=="0"){
            if (value == "" && filtrate == "") {
                frame().then(function(){
                    GET(pageNumber, pageSize)
                });
                $('.frame').html('删除成功');
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["funcCode"]=funcCode;
                param["searchValue"]="";
                //GET(pageNumber, pageSize);
            } else if (value !== "") {
                frame().then(function(){
                    POST(pageNumber, pageSize);
                });
                $('.frame').html('删除成功');
                param["pageNumber"]=pageNumber;
            } else if (filtrate !== "") {
                frame().then(function(){
                    filtrates(inx, pageSize);
                });
                $('.frame').html('删除成功');
                _param["pageNumber"]=pageNumber;
            }
        var thinput=$("thead input")[0];
        thinput.checked =false;
        }else if(data.code=="-1"){
            frame();
            $('.frame').html(data.message);
        }
    })
})
//删除弹框
function frame(){
    var def=$.Deferred();
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
        def.resolve();
    },1500);
    return def;
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
//导出拉出list
$("#more_down").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    var left=($(window).width()-$(".file").width())/2;//弹框定位的left值
    var tp=($(window).height()-$(".file").height())/2;//弹框定位的top值
    $(".file").css("position","fixed");
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/list/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            $("#file_list_l ul").empty();
            for(var i=0;i<message.length;i++){
                 $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
            }
            bian();
            $("#file_list_r ul").empty();
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//移除加载框
        }
    })
})
function bian(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
//导出提交的
$("#file_submit").click(function(){
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var tablemanager=[];
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/user/exportExecl","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var path=message.path;
            var path=path.substring(1,path.length-1);
            $("#enter1").html("<a href='/"+path+"'>下载文件</a>");
            $(".file").hide();
            $("#code_ma1").show();
            // $('#file_submit').hide();
            $('#download').show();
            //导出关闭按钮
            $('#file_close').click(function(){
                $('.file').hide();
            })
            $('#download').click(function(){
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            })
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//移除加载框
        }
    })
})
$("#dao1").click(function(){
    $("#p").hide();
    $("#code_ma1").hide();
})
$("#code_q1").click(function(){
    $("#p").hide();
    $("#code_ma1").hide();
})
//导出关闭按钮
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
})
//筛选按钮
function getAction(){
    oc.postRequire("get","/list/filter_column?funcCode="+funcCode+"","0","",function(data){
    if(data.code=="0"){
        var message=JSON.parse(data.message);
        var filter=message.filter;
        $("#sxk .inputs ul").empty();
        var li="";
        for(var i=0;i<filter.length;i++){
            if(filter[i].type=="text"){
                li+="<li><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"'></li>";
            }else if(filter[i].type=="select"){
                var msg=filter[i].value;
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
                }
                ul+="</ul>";
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
            }else  if(filter[i].type=="check"){
                var msg=filter[i].value;
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    var type="";
                    if(msg[j].key=="微信"){
                        type="wx";
                    }
                    if(msg[j].key=="短信"){
                        type="sms";
                    }
                    if(msg[j].key=="手机"){
                        type="call";
                    }
                    ul+="<li data-code='"+msg[j].value+"' id='"+type+"'><div class='checkbox1'><input type='checkbox' value='"+msg[j].key+"' name='test' class='check'><label for='' style='min-width: 16px;'></label></div><span class='p16' style='margin-left: 16px;'>"+msg[j].key+"</span></li>"
                }
                ul+="</ul>";
                li+="<li class='check_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
            }

        }
        $("#sxk .inputs ul").html(li);
        filtrateDown();
        if(filtrate!==""){
            $(".sxk").slideDown();
            for(var i=0;i<list.length;i++){
                if($("#"+list[i].screen_key).parent("li").attr("class")==undefined){
                    $("#"+list[i].screen_key).val(list[i].screen_value);
                }else if($("#"+list[i].screen_key).parent("li").attr("class")=="isActive_select"){
                    var svalue=$("#"+list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+list[i].screen_value+"']").html();
                    $("#"+list[i].screen_key).val(svalue);
                }else if($("#"+list[i].screen_key).parent("li").attr("class")=="check_select"){
                    if(list[i].screen_value==""){
                        $(".check_select .isActive_select_down").find("li:contains('全部')").click();
                    }else {
                        for(var key in list[i].screen_value){
                           if(list[i].screen_value[key]=="Y"){
                               $("#"+key).click();
                           };
                        }
                    }
                }
            }
        }else if(filtrate==""){
            $(".check_select .isActive_select_down").find("li:contains('全部')").click();
        }
        //筛选的keydow事件
        $('#sxk .inputs input').keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode == 13){
                getInputValue();
            }
        })
    }
});
}
function filtrateDown(){
    var username=[];
    //筛选select框
    $(".isActive_select input").click(function (event){
        if(event.stopPropagation){
            event.stopPropagation();
        }else {
            event.cancelBubble=true;
        }
        var ul=$(this).next(".isActive_select_down");
        var sibiling=$(this).parent("li").siblings().find(".isActive_select_down");
        sibiling.hide();
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
    })
    $(".isActive_select input").blur(function(){
        var ul=$(this).next(".isActive_select_down");
        setTimeout(function(){
            ul.hide();
        },200);
    })
    $(".isActive_select .isActive_select_down li").click(function () {
        var html=$(this).text();
        var code=$(this).attr("data-code");
        $(this).parents("li").find("input").val(html);
        $(this).parents("li").find("input").attr("data-code",code);
        $(".isActive_select_down").hide();
    })
    $(".check_select .isActive_select_down li").click(function () {
        var input=$(this).find("input")[0];
        if(input.type=="checkbox"&&input.checked==false){
            input.checked = true;
            if($(this).find("input").val()=="全部"){
                username=[];
                var siblingsInput= $(this).siblings().find("input");
                for(var i=0;i<siblingsInput.length;i++){
                    $(siblingsInput[i])[0].checked=false;
                }
            }else if($(this).find("input").val()!=="全部"){
                $(this).siblings().find("input[value='全部']")[0].checked=false;
                username.remove("全部");
            }
            username.push($(this).find("input").val());
            $(this).parents("li").find("input[type='text']").val(username.toString());
        }else if(input.type=="checkbox"&&input.checked==true){
            input.checked = false;
            username.remove($(this).find("input").val());
            $(this).parents("li").find("input[type='text']").val(username.toString());
        }
    })
    $(".check_select input").click(function(){
        var ul=$(this).next(".isActive_select_down");
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
    })
}
//筛选查找
$("#find").click(function(){
    getInputValue();
})
function getInputValue(){
    // var input=$('#sxk .inputs li');
    var input=$('#sxk .inputs>ul>li');
    inx=1;
    _param["pageNumber"]=inx;
    _param["pageSize"]=pageSize;
    _param["funcCode"]=funcCode;
    var num=0;
    list=[];//定义一个list
    for(var i=0;i<input.length;i++){
        var screen_key="";
        var screen_value={};
        if($(input[i]).attr("class")=="isActive_select2"){
            screen_key=$(input[i]).attr("id");
            switch ($(input[i]).find("input").val()){
                case '>=':screen_value['type']='gt';screen_value['value']=$(input[i]).find("input").next().val();break;
                case '<=':screen_value['type']='lt';screen_value['value']=$(input[i]).find("input").next().val();break;
                case '介于':screen_value['type']='between';_value();break;
                case '等于':screen_value['type']='eq';screen_value['value']=$(input[i]).find("input").next().val();break;
                case '全部':screen_value['type']='all';screen_value['value']='';break;
                case '':screen_value['type']='all';screen_value['value']='';break;
            }
            function _value(){
                screen_value['value']={};
                var between_value=$(input[i]).find("input").nextAll();
                screen_value['value'].start=$(between_value[0]).val();
                screen_value['value'].end=$(between_value[1]).val();
            }
        }else if($(input[i]).attr("class")=="created_date"){
            var start=$('#start').val();
            var end=$('#end').val();
            screen_key=$(input[i]).attr("id");
            screen_value={"start":start,"end":end};
        }else if($(input[i]).attr("class")=="isActive_select"){
            screen_key=$(input[i]).find("input[type='text']").attr("id");
            screen_value=$(input[i]).find("input[type='text']").attr("data-code");
        }else if($(input[i]).attr("class")=="check_select"){
            screen_key=$(input[i]).find("input[type='text']").attr("id");
            if($(input[i]).find("input[type='text']").val()=="全部"){
                screen_value="";
            }else {
                var wx="";
                if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='微信']")[0].checked==true){
                    wx="Y";
                }else if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='微信']")[0].checked==false ){
                    wx="N";
                }
                var sms="";
                if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='短信']")[0].checked==true){
                    sms="Y";
                }else if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='短信']")[0].checked==false ){
                    sms="N";
                }
                var call="";
                if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='手机']")[0].checked==true){
                    call="Y";
                }else if($(input[i]).find("input[type='text']").next(".isActive_select_down").find("input[value='手机']")[0].checked==false ){
                    call="N";
                }
                screen_value={"wx":wx,"sms":sms,"call":call};
            }
        }else{
            screen_value=$(input[i]).find("input[type='text']").val().trim();
            screen_key=$(input[i]).find("input[type='text']").attr("id");
        }
        if(screen_value!=""){
            num++;
        }
        var param1={"screen_key":screen_key,"screen_value":screen_value};
        list.push(param1);
    }
    _param["list"]=list;
    value="";//把搜索滞空
    $("#search").val("");
    filtrates(inx,pageSize)
    if(num>0){
        filtrate="sucess";
    }else if(num<=0){
        filtrate="";
    }
}
//筛选发送请求
function filtrates(a,b){
    whir.loading.add("",0.5);//加载等待框
    _param["searchType"]="1";
    oc.postRequire("post","/reply/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.list;
            cout=message.pages;
            var pageNum = message.page_number;
            var total = message.total;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove();//移除加载框
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode,total);
        }else if(data.code=="-1"){
            alert(data.message);
        }
   });
}
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
            if (value == "" && filtrate == "") {
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["funcCode"]=funcCode;
                param["searchValue"]="";
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                param["pageNumber"]=inx;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["pageSize"] = pageSize;
                _param["pageNumber"]=inx;
                filtrates(inx, pageSize);
            }
        };
    }
})
