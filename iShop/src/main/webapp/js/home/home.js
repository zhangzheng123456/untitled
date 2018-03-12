var oc = new ObjectControl();
$(function(){
    var _command="/menu";
    oc.postRequire("get", _command,"", "", function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var index=0;
            var html =" ";
            var li_html=" ";
            var p=null;
            var login_state=message.logout;
            var logo=message.home_logo;
            if(logo==""){
                $("#home_logo").attr("src","../img/img_BIZVANE_logo.png");
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
                $('.nav-header .img-circle').attr("src",message.avatar);
            }else if(reg.test(message.avatar)==false){
                $('.nav-header .img-circle').attr("src","../img/head.png");
            }
            if(message.version_describe !== ""){
                $(".update_tip").show();
                whir.loading.add("",0.5);
                $("#loading").remove();
            }else {
                $(".update_tip").hide();
            }
            if(message.version_describe !== ""){
                $(".tip_list ul").html(message.version_describe);
            }
            $('.nav-header .font-bold').html(message.user_name);
            $('#corp_name').html(message.corp_name);
            for(index in message.menu){
                var index_li=0;
                p=message.menu[index];
                if(p.functions!==''){
                    html +='<li>'
                    +'<a>'
                        +'<i class="'+p.icon+'"></i>'
                        +'<div class="nav-label">'+p.mod_name+'<span class="icon-ishop_8-02 per"></span></div>'
                        // +'<i class="icon-ishop_8-02 per" style="font-size:14px;position:absolute;right:24px;top:21px;"></i>'
                    +'</a>'
                    +'<ul class="nav nav-second-level">';
                    if(p.mod_name=="会员管理"){
                        var reg=/F0010|F0011|F0050|F0012|F0069/;
                        for(index_li in p.functions){
                            if(reg.test(p.functions[index_li].func_code)){
                                html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'&t=20180102" >'+p.functions[index_li].fun_name+'</a></li>';
                            }else{
                                html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'&t=20180102" ><b class="pro_parent">'+p.functions[index_li].fun_name+'<b class="pro">pro</b></b></a></li>';
                            }
                        }
                    }else{
                        for(index_li in p.functions){
                            html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'&t=20180102" >'+p.functions[index_li].fun_name+'</a></li>';
                        }
                    }
                }else{
                    html +='<li>'
                    +'<a href="../../navigation_bar.html?url='+p.url+'&func_code='+p.func_code+'&t=20180102">'
                        +'<i class="'+p.icon+'"></i>'
                        +'<div class="nav-label">'+p.mod_name+'</div>'
                    +'</a>'
                    +'<ul class="nav nav-second-level">';
                }
                html +='</ul>'
                 +'</li>';

            }
            $(html).insertAfter('.navbar-static-side .sidebar-collapse #side-menu .nav-header');
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
        //li被点击时的样式切换
        $("#side-menu li").click(function(){
            if($(this).children('ul').children('li').length>0){
                $(this).children('ul').slideToggle();
                $(this).children('a').find('.nav-label span').toggleClass('per');
                $(this).siblings().children('a').find('.nav-label span').addClass('per');
                $(this).siblings().children('ul').slideUp();
            }
        });
        if($("#side-menu .nav-header").next().find("ul li").length>0){
            $("#side-menu .nav-header").next().trigger("click");
        }
    });
    //关闭提示
    $("#tip_enter").click(function () {
        $(".update_tip").hide();
        whir.loading.remove();//移除加载框
    });
    $("#tip_close").click(function () {
        $(".update_tip").hide();
        whir.loading.remove();//移除加载框
    });
    console.info("                 ___          ___          ___          ___     \n      ___       /\\  \\        /\\__\\        /\\  \\        /\\  \\    \n     /\\  \\     /::\\  \\      /:/  /       /::\\  \\      /::\\  \\   \n     \\:\\  \\   /:/\\ \\  \\    /:/__/       /:/\\:\\  \\    /:/\\:\\  \\  \n     /::\\__\\ _\\:\\~\\ \\  \\  /::\\  \\ ___  /:/  \\:\\  \\  /::\\~\\:\\  \\ \n  __/:/\\/__//\\ \\:\\ \\ \\__\\/:/\\:\\  /\\__\\/:/__/ \\:\\__\\/:/\\:\\ \\:\\__\\\n /\\/:/  /   \\:\\ \\:\\ \\/__/\\/__\\:\\/:/  /\\:\\  \\ /:/  /\\/__\\:\\/:/  /\n \\\::/__/     \\\:\\\ \\\:\\__\\       \\::/\  /  \\:\\  /:/  /      \\::/  / \n  \\:\\__\\      \\:\\/:/  /       /:/  /    \\:\\/:/  /        \\/__/  \n   \\/__/       \\::/  /       /:/  /      \\::/  /                \n                \\/__/        \\/__/        \\/__/                 ");
    console.log("欢迎使用爱秀后台:http://"+window.location.host)
});

//退出登录
// $("#return").click(function(){
//     var _command="/login_out";
//     oc.postRequire("get", _command,"", "", function(data){
//         // window.location="/login.html";
//     });
// })
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
                $("#wrapper").after(msg)
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
            $("#wrapper").after(msg)
        }
    });

    }
});