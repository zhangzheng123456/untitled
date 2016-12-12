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
    $('#iframepage').attr("src",url);//给获取的src赋值
    oc.postRequire("get","/menu","0","",function(data){//左侧导航栏的循环操作
        var str = JSON.stringify(data);
        var key = "key";
        sessionStorage.setItem(key, str);
        var message=JSON.parse(data.message);
        var reg=/(^(http:\/\/)(.*?)(\/(.*)\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))/;
        if(reg.test(message.avatar)==true){
            $("#img img").attr("src",message.avatar);
        }else if(reg.test(message.avatar)==false){
            $("#img img").attr("src","../img/head.png");
        }
        var menu=message.menu;
        for(var i=0;i<menu.length;i++){
        	var li="<li>";
        	li+="<h1 class='menu_t' data-url='"+menu[i].url+"' data-code='"+menu[i].func_code+"'><i class='"+menu[i].icon+"'></i>"+menu[i].mod_name+"<span></span></h1>";
        	if(menu[i].functions!==""){
                li+="<dl>"
        		for(var j=0;j<menu[i].functions.length;j++){
        			li+="<dd data-url='"+menu[i].functions[j].url+"' data-code='"+menu[i].functions[j].func_code+"'><a href='javascript:void(0);'><span></span>"+menu[i].functions[j].fun_name+"</a></dd>"
        		}
                li+="</dl>"		
        	}
        	li+="</li>"
        	$(".sidebar ul").append(li);
        }
        $(".sidebar ul").append("<li style='height:38px;'><li>");
        //左侧导航栏
        $(".sidebar ul li dl dd").click(function(e){
        	e.stopPropagation();
        	var src=$(this).attr("data-url");
        	var func_code=$(this).attr("data-code");
        	$('#iframepage').attr("src",src);
            console.log(src);
            sessionStorage.removeItem("return_jump");
            sessionStorage.removeItem("state");
            var key_val={"url":src,"func_code":func_code};
            sessionStorage.setItem("key_val",JSON.stringify(key_val));
            $(this).find("span").addClass("icon-ishop_8-01");
            $(this).find("a").css({color:"#6cc1c8"});
            $(this).siblings("dd").find("a").css({color:"#fff"});
            $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
        });
        $(".sidebar ul li").click(function(e){
            e.stopPropagation();
            $(this).find("h1").toggleClass("h1").parents().siblings("li").find("h1").removeClass("h1");
            var src=$(this).find("h1").attr("data-url");
            var func_code=$(this).find("h1").attr("data-code");
            if(src!=="undefined"){
            	$('#iframepage').attr("src",src);
                sessionStorage.removeItem("return_jump");
                sessionStorage.removeItem("state");
                $(this).find("h1").parents().siblings("li").find("dl").slideUp(300);
                $(this).find("h1 span").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");
            }
            if(src=="undefined"){
            	$(this).find("h1").next("dl").slideToggle(300).parents().siblings("li").find("dl").slideUp(300);
            	$(this).find("h1 span").toggleClass("icon-ishop_8-02").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");   	
            }
            if(src!=="undefined"&&func_code!=="undefined"){
                var key_val={"url":src,"func_code":func_code};
                sessionStorage.setItem("key_val",JSON.stringify(key_val));   
            }   
        });
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
            }
    }
    $("#logo").click(function(){
       renturn_h();
    })
    $("#renturn_h").click(function(){
        renturn_h();
    })
})