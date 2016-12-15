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
                    for(index_li in p.functions){
                        if(p.functions[index_li].func_code=='F0036'||p.functions[index_li].func_code=='F0034'||p.functions[index_li].func_code=='F0032'||p.functions[index_li].func_code=='F0041'){
                            html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'" ><b class="pro_parent">'+p.functions[index_li].fun_name+'<b class="pro">pro</b></b></a></li>';
                        }else{
                            html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'" >'+p.functions[index_li].fun_name+'</a></li>';
                        }
                    }
                }
                else{
                    html +='<li>'
                    +'<a href="../../navigation_bar.html?url='+p.url+'&func_code='+p.func_code+'">'
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
    });
    //关闭提示
    $("#tip_enter").click(function () {
        $(".update_tip").hide();
        whir.loading.remove();//移除加载框
    });
    $("#tip_close").click(function () {
        $(".update_tip").hide();
        whir.loading.remove();//移除加载框
    })
});
//退出登录
// $("#return").click(function(){
//     var _command="/login_out";
//     oc.postRequire("get", _command,"", "", function(data){
//         // window.location="/login.html";
//     });
// })