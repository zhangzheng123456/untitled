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
            var self=this;
            $("#tabs>div").bind("click",function(){
                var src=$(this).attr("data-src");
                var html=$(this);
                if(src==undefined || $(this).hasClass("active")){
                    return ;
                }else{
                    var index=$("#tabs .active").index();
                    console.log(index);
                    if(index==0){
                        activity.checkEmpty();
                        if(activity.isEmpty==true){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: activity.label+"不能为空"
                            });
                            return ;
                        }
                        if(activity.theme!==""){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: activity.theme
                            });
                            return ;
                        }
                        if(activity.cache.store_codes==""){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: "没有选择参与门店"
                            });
                            return ;
                        }
                        $.when(activity.add())
                            .then(function(data){
                                if(data=="成功"){
                                    self.getHtml(src,html);
                                }
                            });
                    }
                    if(index=="1"){
                        $.when(activityPlanning.submitJob(),activityPlanning.submitGroup())
                            .then(function(data1,data2){
                                if(data1=="成功"&&data2=="成功"){
                                    self.getHtml(src,html);
                                }
                        });
                    }
                    if(index=="2"){
                        $.when(postSelect()).then(function (data) {
                            if(data=="失败") return;
                            self.getHtml(src,html);
                        });
                    }
                    if(index=="3"){
                        self.getHtml(src,html);
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
        getHtml:function(src,html){
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
            $(html).addClass("active");
            $(html).siblings().removeClass("active");
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
