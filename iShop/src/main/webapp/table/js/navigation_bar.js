$(function(){
    var val=sessionStorage.getItem("key");
    val=JSON.parse(val);
    var message=JSON.parse(val.message);
    console.log(message);
    var menu=message.menu;
    console.log(menu);
    for(var i=0;i<menu.length;i++){
    	var li="<li>";
    	li+="<h1 class='menu_t' data-url='"+menu[i].url+"'><i class='"+menu[i].icon+"'></i>"+menu[i].mod_name+"<span></span></h1><dl>";
    	if(menu[i].functions!==""){
    		for(var j=0;j<menu[i].functions.length;j++){
    			li+="<dd data-url='"+menu[i].functions[j].url+"'><a href='javascript:void(0);'><span></span>"+menu[i].functions[j].fun_name+"</a></dd>"
    		}		
    	}
    	li+="</dl></li>"
    	$(".sidebar ul").append(li);
    }
    //左侧导航栏
    $(".sidebar ul li dl dd").click(function(e){
    	e.stopPropagation();
    	var src=$(this).attr("data-url");
    	$('#iframepage').attr("src",src);
        $(this).find("span").addClass("icon-ishop_8-01");
        $(this).find("a").css({color:"#6cc1c8"});
        $(this).siblings("dd").find("a").css({color:"#fff"});
        $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
    });
    $(".sidebar ul li").click(function(e){
        e.stopPropagation();
        $(this).find("h1").toggleClass("h1").parents().siblings("li").find("h1").removeClass("h1");
        var src=$(this).find("h1").attr("data-url");
        if(src!=="undefined"){
        	$('#iframepage').attr("src",src);
        }
        if(src=="undefined"){
        	$(this).find("h1").next("dl").slideToggle(300).parents().siblings("li").find("dl").slideUp(300);
        	$(this).find("h1 span").toggleClass("icon-ishop_8-02").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");   	
        }   
    });
})