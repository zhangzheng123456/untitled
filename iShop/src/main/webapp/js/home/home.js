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
            if(message.avatar==""){
                $('.nav-header .img-circle').attr("src","../img/head.png");
            }else if(message.avatar!==""){
                $('.nav-header .img-circle').attr("src",message.avatar);
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
                        +'<span class="nav-label">'+p.mod_name+'</span>'
                        +'<i class="icon-ishop_8-02 per" style="font-size:14px;position:absolute;right:24px;top:21px;"></i>'
                    +'</a>'
                    +'<ul class="nav nav-second-level">';
                    for(index_li in p.functions){
                        html +='<li><a href="../../navigation_bar.html?url='+p.functions[index_li].url+'&func_code='+p.functions[index_li].func_code+'" >'+p.functions[index_li].fun_name+'</a></li>';
                    }
                }
                else{
                    html +='<li>'
                    +'<a href="../../navigation_bar.html?url='+p.url+'&func_code='+p.func_code+'">'
                        +'<i class="'+p.icon+'"></i>'
                        +'<span class="nav-label">'+p.mod_name+'</span>'
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
                $(this).children('a').children('i:nth-of-type(2)').toggleClass('per');
                $(this).siblings().children('a').children('i:nth-of-type(2)').addClass('per');
                $(this).siblings().children('ul').slideUp();
            }
        });
    });
});
//退出登录
// $("#return").click(function(){
//     var _command="/login_out";
//     oc.postRequire("get", _command,"", "", function(data){
//         // window.location="/login.html";
//     });
// })