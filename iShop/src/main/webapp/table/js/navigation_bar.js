$(function(){
    var oc = new ObjectControl();
    function GetRequest(){   
        var url = location.search; //获取url中"?"符后的字串   
        var theRequest = new Object();   
        if (url.indexOf("?") != -1) {   
        var str = url.substr(1);   
        strs = str.split("&");   
        for(var i = 0; i < strs.length; i ++) {   
            theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);   
            }   
        }   
        return theRequest;   
    }
    var a=GetRequest();//取得url的后面的参数
    var src=a.url;//获取url后面的参数
    var func_code=a.func_code;//获取func_code
    var key=sessionStorage.getItem("key_val");//获取本地的key值
    key=JSON.parse(key);
    if(key!==null){
       var src1=key.url;  
    }
    var key_val={"url":src,"func_code":func_code};//组成一个对象字符串
    if(key==null||src!==src1){
        sessionStorage.setItem("key_val",JSON.stringify(key_val));//保存到本地
    }
    var keyVal=sessionStorage.getItem("key_val");//获取本地的属性
    keyVal=JSON.parse(keyVal);//把本地的属性转成json
    var url=keyVal.url;//url的参数
    sessionStorage.removeItem("return_jump");
    sessionStorage.removeItem("state");
    $('#iframepage').attr("src",url+"?"+$.now());//给获取的src赋值
    oc.postRequire("get","/menu","0","",function(data){//左侧导航栏的循环操作
        var str = JSON.stringify(data);
        var key = "key";
        sessionStorage.setItem(key, str);
        var message=JSON.parse(data.message);
        var user_type=message.user_type;
        $("#iframepage").attr("data-userType",user_type);
        if(user_type!=="cm"){
            $("#qita_corp").show();
            $("#cm_corp").hide();
        }
        if(user_type=="cm"){
            $("#qita_corp").hide();
            $("#cm_corp").show();
        }
        var login_state=message.logout;
        var logo=message.home_logo;
        if(logo==""){
            $("#home_logo").attr("src","../../img/img_BIZVANE_logo.png");
        }else {
            $("#home_logo").attr("src",logo);
        }
        if(login_state=="N"){
            $("#return").hide();
        }else {
            $("#return").show();
        }
        var reg=/(^(http:\/\/)(.*?)(\/(.*)\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))/;
        if(reg.test(message.avatar)==true){
            $("#img img").attr("src",message.avatar);
        }else if(reg.test(message.avatar)==false){
            $("#img img").attr("src","../img/head.png");
        }
        var menu=message.menu;
        for(var i=0;i<menu.length;i++){
        	var li="<li>";
        	li+="<h1 class='menu_t' data-url='"+menu[i].url+"' data-code='"+menu[i].func_code+"'><i class='"+menu[i].icon+"'></i><b class='quchu'>"+menu[i].mod_name+"</b><span {name}></span></h1>";
        	if(menu[i].functions!==""){
                li+="<dl>";
                if(menu[i].mod_name=="会员管理"){
                    var reg=/F0010|F0011|F0050|F0012|F0069/;
                    for(var j=0;j<menu[i].functions.length;j++){
                        var className = "",style = "";
                        if(func_code == menu[i].functions[j].func_code){
                            className = 'class="icon-ishop_8-01"';
                            style = 'style="color:#6cc1c8"';
                            li = li.replace('<dl>',"<dl style='display: block'>");
                            li = li.replace("class='menu_t'","class='menu_t h1'");
                        }
                        if(reg.test(menu[i].functions[j].func_code)){
                            li+="<dd data-url='"+menu[i].functions[j].url+"' data-code='"+menu[i].functions[j].func_code+"'><a "+style+" href='javascript:void(0);'><span "+className+"></span><b class='quchu'>"+menu[i].functions[j].fun_name+"</b></a></dd>"
                        }else{
                            li+="<dd data-url='"+menu[i].functions[j].url+"' data-code='"+menu[i].functions[j].func_code+"'><a "+style+" href='javascript:void(0);'><span  "+className+"></span><b class='pro_parent'>"+menu[i].functions[j].fun_name+"<b class='pro'>pro</b></b></a></dd>"
                        }
                    }
                }else{
                    for(var j=0;j<menu[i].functions.length;j++){
                        var className = "",style = "";
                        if(func_code == menu[i].functions[j].func_code){
                            className = 'class="icon-ishop_8-01"';
                            style = 'style="color:#6cc1c8"';
                            li = li.replace('<dl>',"<dl style='display: block'>");
                            li = li.replace("class='menu_t'","class='menu_t h1'");
                        }
                        li+="<dd data-url='"+menu[i].functions[j].url+"' data-code='"+menu[i].functions[j].func_code+"'><a "+style+" href='javascript:void(0);'><span  "+className+"></span><b class='quchu'>"+menu[i].functions[j].fun_name+"</b></a></dd>"
                    }
                }
                li+="</dl>"		
        	}
        	li+="</li>";
        	$(".sidebar ul").append(li);
        }
        //左侧导航栏
        $(".sidebar ul li dl dd").click(function(e){
        	e.stopPropagation();
        	var src=$(this).attr("data-url");
        	var func_code=$(this).attr("data-code");
        	$('#iframepage').attr("src",src+"?"+$.now());
            sessionStorage.removeItem("return_jump");
            sessionStorage.removeItem("state");
            var key_val={"url":src,"func_code":func_code};
            sessionStorage.setItem("key_val",JSON.stringify(key_val));
            $(this).find("span").addClass("icon-ishop_8-01");
            $(this).find("a").css({color:"#6cc1c8"});
            $(this).siblings("dd").find("a").css({color:"#fff"});
            $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
            $(this).parents("li").siblings("li").find("dd a").css({color:"#fff"});
            $(this).parents("li").siblings("li").find("dd span").removeClass("icon-ishop_8-01");
        });
        $(".sidebar ul li").click(function(e){
            e.stopPropagation();
            $(this).find("h1").toggleClass("h1").parents().siblings("li").find("h1").removeClass("h1");
            var src=$(this).find("h1").attr("data-url");
            var func_code=$(this).find("h1").attr("data-code");
            if(src!=="undefined"){
                $('#iframepage').attr("src",src+"?"+$.now());
                sessionStorage.removeItem("return_jump");
                sessionStorage.removeItem("state");
                $(this).find("h1").parents().siblings("li").find("dl").slideUp(300);
                $(this).find("h1 span").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");
            }
            if(src=="undefined"){
            	$(this).find("h1").next("dl").slideToggle(300,function(){$(".sidebar").getNiceScroll().resize()}).parents().siblings("li").find("dl").slideUp(300,function(){$(".sidebar").getNiceScroll().resize()});
            	$(this).find("h1 span").toggleClass("icon-ishop_8-02").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");   	
            }
            if(src!=="undefined"&&func_code!=="undefined"){
                var key_val={"url":src,"func_code":func_code};
                sessionStorage.setItem("key_val",JSON.stringify(key_val));   
            }   
        });
        var settingKey=JSON.parse(sessionStorage.getItem("key"));
        var settingMsg=JSON.parse(settingKey.message);
        if(settingMsg.user_type!="admin"){
            $("#setting").parents("li").hide();
        }
    })
    function renturn_h(){
        var val=sessionStorage.getItem("key");//取登录里面的key
        val=JSON.parse(val);//
        var message=JSON.parse(val.message);//
        sessionStorage.removeItem("return_jump");
        var user_type=message.user_type;//用户类型
            if (user_type == "admin") {
                window.location.href = "home/index_admin.html";
            } else if (user_type == "am") {
                window.location.href = "home/index_am.html";
            } else if (user_type == "gm") {
                window.location.href = "home/index_gm.html";
            } else if (user_type == "staff") {
                window.location.href = "home/index_staff.html";
            } else if(user_type == "sm"){
                window.location.href="home/index_sm.html";
            } else if(user_type == "bm"){
                window.location.href="home/index_bm.html";
            }else if(user_type == "cm"){
                window.location.href="home/index_cm.html";
            }
    }
    $("#logo").click(function(){
       renturn_h();
    })
    $("#renturn_h").click(function(){
        renturn_h();
    })
});
var oc = new ObjectControl();
function getUnreadMes(){
    oc.postRequire("get", "/message/unReadMessage", "", "", function(data) {
        var count=JSON.parse(data.message);
        count=count.unreadmsg_count;
        $("#unreadNum").text(count=="0"?"":count);
    })
}
getUnreadMes();
$("#message_btn").click(function(){
    $("#download_body").animate({right:"-320px"},200,function () {
        $("#download_body").remove();
    });
    if($("#message_body").css("right")!=undefined && $("#message_body").css("right")=="-320px"){
        $("#message_body").animate({right:"0px"},200);
    }else if($("#message_body").css("right")==undefined){
        $.ajax({
            url:"/home/message_list.html",
            dataType: "html",
            cache:false,
            type:"GET",
            success: function(msg) {
                $("table").after(msg)
            }
        });
    }
});
$("#download").click(function(){
    $("#message_body").animate({right:"-320px"},200,function () {
        $("#message_body").remove();
    });
    if($("#download_body").css("right")!=undefined && $("#download_body").css("right")=="-320px"){
        $("#download_body").animate({right:"0px"},200);
    }else if($("#download_body").css("right")==undefined){
        $.ajax({
            url:"/home/download_list.html",
            dataType: "html",
            cache:false,
            type:"GET",
            success: function(msg) {
                $("table").after(msg)
            }
        });
    }
});
function getCorpList(){
    var corp_command="/user/getCorpByCm";
    oc.postRequire("post",corp_command, "", "", function(data) {
        var corps=JSON.parse(data.message).corps;
        var html="";
        for(var i=0;i<corps.length;i++){
            html+="<option value='"+corps[i].corp_code+"'>"+corps[i].corp_name+"</option>"
        }
        $("#corp_list").html(html);
        var b=sessionStorage.getItem("sessionCorp");
        $("#corp_list").html(html);
        if(b!=="null"){
            $("#corp_list option[value='"+b+"']").attr("selected","true");
        }
        $("#corp_list").searchableSelect();
        var corp_code=$("#corp_list").val();
        $('.search .searchable-select-input').keydown(function(event){
            var event=window.event||arguments[0];
            if(event.keyCode == 13){
                var corp_code1=$("#corp_list").val();
                if(corp_code!==corp_code1){
                    $('#iframepage').attr("src",$('#iframepage').attr("src"));
                    corp_code=corp_code1;
                    sessionStorage.setItem("sessionCorp",corp_code);//保存到本地
                    insertSession(corp_code);
                }
            }
        });
        $('.search .searchable-select-item').click(function(){
            var corp_code1=$("#corp_list").val();
            if(corp_code!==corp_code1){
                corp_code=corp_code1;
                sessionStorage.setItem("sessionCorp",corp_code);//保存到本地
                insertSession(corp_code);
            }
        })
    })
}
function insertSession(corp_code){
    var param={};
    param["corp_code"]=corp_code;
    oc.postRequire("post","/user/getCmCorpBySession", "",param, function(data) {
        if(data.code=="0"){
            $('#iframepage').attr("src",$('#iframepage').attr("src"));
        }else {
            alert(data.message);
        }
    })
}
getCorpList();
