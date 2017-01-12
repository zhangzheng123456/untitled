/**
 * Created by zhaoWei on 2017/1/5.
 */
(function (){
    var oc= new ObjectControl();
    var vip_activity_add={
        next_page:false,
        init:function(){
           this.bind();
        },
        bind:function(){
            this.tabsSelect();
            this.getFirst();
            this.backActivityList();
        },
        tabsSelect:function(){
            $("#tabs>div").bind("click",function(){
                var src=$(this).attr("data-src");
                if(src==undefined || $(this).hasClass("active")){
                    return ;
                }else{
                    var index=$("#tabs .active").index();
                    console.log(index);
                    if(index==0){
                        activity.checkEmpty();
                        console.log(activity.isEmpty);
                        if(activity.isEmpty==true){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: "不能为空"
                            });
                            return ;
                        }
                        activity.add();
                    }
                    if(index=="1"){
                        
                    }
                    if(activity.next==true){
                        $.ajax({
                            type: "GET",
                            url: src+"?t="+ $.now(),
                            dataType: "html",
                            success: function (data) {
                                $("#tabs-content").html(data);
                            },
                            error: function (msg) {
                                alert(msg);
                            }
                        });
                        $(this).addClass("active");
                        $(this).siblings().removeClass("active");
                    }
                }
            });
        },
        getFirst:function(){
            $.ajax({
                type: "GET",
                url: "set_vip_activity.html?t="+ $.now(),
                dataType: "html",
                success: function (data) {
                    $("#tabs-content").html(data);
                },
                error: function (msg) {
                    alert(msg);
                }
            });
        },
        backActivityList:function(){
            //回到会员列表
            $("#back_corp_param").click(function(){
                $(window.parent.document).find('#iframepage').attr("src","/activity/activity.html");
            });
        }
    };
    $(function(){
        $("#tabs-content").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-220+"px");
        $(window).resize(function(){
            $("#tabs-content").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-220+"px");
        });
        vip_activity_add.init();
    })
})();
