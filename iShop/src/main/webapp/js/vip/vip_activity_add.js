/**
 * Created by zhaoWei on 2017/1/5.
 */
(function (){
    var oc= new ObjectControl();
    var vip_activity_add={
        init:function(){
           this.bind();
        },
        bind:function(){
            this.tabsSelect()
        },
        tabsSelect:function(){
            $("#tabs>div").bind("click",function(){
                var src=$(this).attr("data-src");
                if(src==undefined || $(this).hasClass("active")){
                    return ;
                }else{
                    $.ajax({
                        type: "GET",
                        url: src+"?t="+ $.now(),
                        dataType: "html",
                        success: function (data) {
                            $("#tabs-content").html(data)
                        },
                        error: function (msg) {
                            alert(msg);
                        }
                    });
                    $(this).addClass("active");
                    $(this).siblings().removeClass("active");
                }

            })
        }
    };
    $(function(){
        $("#tabs-content").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-121+"px");
        $(window).resize(function(){
            $("#tabs-content").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-121+"px");
        });
        vip_activity_add.init();
    })
})();
