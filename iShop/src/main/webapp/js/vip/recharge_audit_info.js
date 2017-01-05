/**
 * Created by ZhaoWei on 2017/1/5.
 */
(function(){
    var oc=new ObjectControl();
    var audit_info={
        init:function(){
            this.getInfo();
            this.bind();
        },
        bind:function(){
            $("#backCheck").bind("click",function(){
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit.html");
            })
        },
        getInfo:function(){
            var id=sessionStorage.getItem("id");
            var param={
                id:id
            };
            oc.postRequire("post","/vipCheck/select","",param,function(data){
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var list=JSON.parse(msg.list);
                    for(var key in list[0]){
                        $("#"+key).find("input").val(list[0][key])
                    }
                }
            })
        }
    };
    $(function(){
        audit_info.init();
    })
})();