/**
 * Created by ZhaoWei on 2017/1/5.
 */
(function(){
    var oc=new ObjectControl();
    var audit_info={
        corp_code:"",
        init:function(){
            //this.getInfo();
            this.bind();
        },
        bind:function(){
            this.backCheck();
            this.showType();
            this.editBill();
            this.parseAudit();
        },
        backCheck:function(){
            $("#backCheck").bind("click",function(){
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit.html");
            });
            $("#close").bind("click",function(){
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit.html");
            })
        },
        getInfo:function(isOparate){
            var id=sessionStorage.getItem("id");
            var param={
                id:id
            };
            oc.postRequire("post","/vipCheck/select","",param,function(data){
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var list=msg.list;
                    audit_info.corp_code=list.corp_code;
                    if(isOparate){
                        audit_info.getStoreName()
                    };
                  $("#page-wrapper .conpany_msg").find(">div").hide();
                  list.status=="0"?list.status="未审核":list.status=="1"?list.status="审核通过":list.status="失败";
                    for(var key in list){

                        if((key=="store_name" || key=="user_name") && isOparate) {
                            $("#"+key).show();
                        }else{
                            $("#"+key).show();
                            $("#"+key).find("input").val(list[key]);
                        }

                    }
                    if(list["status"]=="审核通过"){
                        $("#page-wrapper .conpany_msg").find("input").attr("disabled",true);
                        $("#auditSuccess").hide();
                        $("#auditError").hide();
                    }else{
                        $("#auditSuccess").show();
                        $("#keep").show();
                    }
                }
            })
        },
        setBillDate:function(){
            //日期调用插件
            var bill_date = {
                elem: '#bill_date_input',
                format: 'YYYYMMDD',
                istime: false,
                max: '2099-06-16 23:59:59', //最大日期
                istoday: true,
                fixed: false,
                choose: function (datas) {

                }
            };
            $("#bill_date_input").bind("click",function(){
                laydate(bill_date)
            })
        },
        getStoreName:function(){
            oc.postRequire("get","/shop/findStore","","",function(data){
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    msg=JSON.parse(msg.list);
                    var corp_html='';
                    for( var i=0;i<msg.length;i++){
                        corp_html+='<option value="'+msg[i].store_code+'">'+msg[i].store_name+'</option>';
                    }
                    $("#OWN_CORP").append(corp_html);
                    $("#OWN_CORP").searchableSelect();
                    $('.corp_select .searchable-select-input').keydown(function(event){
                        var event=window.event||arguments[0];
                        if(event.keyCode == 13){
                        }
                    });
                    $('.searchable-select-item').click(function(){

                    });
                    audit_info.getUserName();
                }else if(data.code=="-1"){
                    art.dialog({
                        time: 1,
                        lock:true,
                        cancel: false,
                        content: data.message
                    });
                }
            })
        },
        getUserName:function(){
            var param={
                store_code:$("#OWN_CORP").val(),
                corp_code:audit_info.corp_code
            };
            oc.postRequire("post","/shop/staff","",param,function(data){
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var corp_html='';
                    if(msg.length>0){
                        for( var i=0;i<msg.length;i++){
                            corp_html+='<option value="'+msg[i].user_code+'">'+msg[i].user_name+'</option>';
                        }
                    }
                    $("#user_select").append(corp_html);
                    $('#user_select').searchableSelect();
                    $('#user_select .searchable-select-input').keydown(function(event){
                        var event=window.event||arguments[0];
                        if(event.keyCode == 13){
                        }
                    });
                    $('.searchable-select-item').click(function(){

                    });
                }else if(data.code=="-1"){
                    art.dialog({
                        time: 1,
                        lock:true,
                        cancel: false,
                        content: data.message
                    });
                }
            })
        },
        selectRechargeType:function(){
            $("#recharge_type_input").bind("click",function(){
                $(this).next().toggle()
            });
            $("#recharge_type_input").next().find("li").bind("click",function(){
                var val=$(this).html();
                $("#recharge_type_input").val(val);
                $(this).parent().hide();
            })
        },
        showType:function(){
            var isOparate=false;
            if($(".pre_title label").text()=="审核详情"){
                isOparate=false;
                this.getInfo(isOparate)
            }else {
                isOparate=true;
                this.getInfo(isOparate);
                this.setBillDate();
                this.selectRechargeType();
            }
        },
        editBill:function(){
            $("#keep").bind("click",function(){
                var editParam={};
                editParam.id=sessionStorage.getItem("id");
               var allInput=$("#page-wrapper .conpany_msg").find("input.isChange:visible");
                for(var i=0;i<allInput.length;i++){
                    editParam[$(allInput[i]).parents("div").attr("id")]=$(allInput[i]).val()
                }
                editParam.store_name=$("#OWN_CORP").val();
                editParam.user_name=$("#user_select").val()==null?"":$("#user_select").val();
                editParam.type=$("#type").find("input").val()=="充值"?"pay":$("#type").find("input").val()=="退款"?"refund":"";
                oc.postRequire("post","/vipCheck/editBill","",editParam,function(data){
                    if(data.code==0){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "保存成功"
                        })
                    }else{
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: data.message
                        })
                    }
                })
            })
        },
        parseAudit: function () {
            $("#auditSuccess").bind("click",function(){
                var type=$("#type").find("input").val()=="充值"?"pay":"refund";
                var params={};
                params["id"]=sessionStorage.getItem("id");
                params["type"]=type;
                oc.postRequire("post","/vipCheck/changeStatus","",params,function(data){
                    if(data.code=="0"){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "审核成功"
                        });
                    }else{
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: data.message
                        });
                    }
                })
            })
        }
    };
    $(function(){
        audit_info.init();
    })
})();