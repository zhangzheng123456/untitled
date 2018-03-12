var oc = new ObjectControl();
function getList(){
    oc.postRequire("post","/api/getRelAppHelps","","", function(data){
        var message=JSON.parse(data.message);
        var list=JSON.parse(message.list);
        var html="";
        for(var i=0;i<list.length;i++){
            html+="<li><h1>"+list[i].rel_app_help.app_help_name+"<span class='icon-ishop_8-03'></span></h1><dl data-code='"+list[i].rel_app_help.app_help_code+"'>";
            for(var j=0;j<list[i].app_help.length;j++){
                html+="<dd>"+list[i].app_help[j]+"<span class='icon-ishop_8-03'></span></dd>";
            }
            html+="</dl></li>";
        }
        $("#content ul").html(html);
        $(".content ul li dl dd").click(function(e){
            e.stopPropagation();
            var app_help_code=$(this).parent().attr("data-code");
            var app_help_title=$(this).text();
            location.href="shequ.html?app_help_code="+app_help_code+"&app_help_title="+app_help_title+"";
        });
        var tianjia="0";
        $(".content ul li").click(function(e){
            e.stopPropagation();
            if($(this).find("h1 span").attr("class")=="icon-ishop_8-02"){
                $(this).find("h1 span").attr("class","icon-ishop_8-03");
            }else if($(this).find("h1 span").attr("class")=="icon-ishop_8-03"){
                $(this).find("h1 span").attr("class","icon-ishop_8-02");
            }
            $(this).find("dl").slideToggle(300);
            $(this).find("h1").parents().siblings("li").find("h1 span").attr("class","icon-ishop_8-03");
            $(this).find("h1").parents().siblings("li").find("dl").slideUp(300);
        });
    })
}
getList();


