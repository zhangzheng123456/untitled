/**
 * Created by ZhaoWei on 2017/1/5.
 */
(function(){
    var oc=new ObjectControl();
    var audit_info={
        corp_code:"",
        funcCode:"",
        init:function(){
            //this.getInfo();
            this.getFunCode();
            this.bind();
        },
        bind:function(){
            this.closeTk();
            this.backCheck();
            this.showType();
            this.editBill();
            this.parseAudit();
        },
        getFunCode:function(){
            var key_val=sessionStorage.getItem("key_val");//取页面的function_code
            key_val=JSON.parse(key_val);//取key_val的值
            this.funcCode=key_val.func_code;
        },
        closeTk:function(){
            $("#addVipEnter,#addVipX").click(function(){
                $("#tk").hide();
                $("#msg").html("");
                whir.loading.remove("mask")
            });
        },
        backCheck:function(){
            $("#backCheck").bind("click",function(){
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit.html");
            });
            $("#close").bind("click",function(){
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit.html");
            })
        },
        qjia:function(){  //页面加载调权限接口
            var param={};
            var def= $.Deferred();
            param["funcCode"]=this.funcCode;
            oc.postRequire("post","/list/action","0",param,function(data){
                var message=JSON.parse(data.message);
                var actions=message.actions;
                def.resolve(actions)
            });
            return def.promise();
        },
        getInfo:function(isOparate){
            var id=sessionStorage.getItem("id");
            var param={
                id:id
            };
            whir.loading.add("",0.5);
            oc.postRequire("post","/vipCheck/select","",param,function(data){
                whir.loading.remove("",0.5);
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var list=msg.list;
                    audit_info.corp_code=list.corp_code;
                    if(isOparate){
                        $("#user_select").html("<option value='"+list.user_code+"'>"+list.user_name+"</option>");
                        $("#OWN_CORP").html("<option value='"+list.store_code+"'>"+list.store_name+"</option>");
                        audit_info.getStoreName(list.store_code,list.user_code);
                    }
                    var input=$("#is_active")[0];
                    if(list.isactive=="Y"){
                        input.checked=true;
                    }else if(list.isactive=="N"){
                        input.checked=false;
                    }
                    $("#modified_man").val(list.modifier);
                    $("#created_man").val(list.creater);
                    //list.type=="充值"?$("#recharge_type").find("label").html("充值类型"):"退款类型";
                    if(list.type=="充值"){
                        $("#recharge_type").find("label").html("充值类型");
                    }
                    if(list.type=="退款"){
                        $("#is_active").attr("readonly",true);
                        $("#is_active").next().attr("for","");
                        $(".checkbox_isactive .check:checked + label").css("backgroundImage","url(../img/icon_radio_S_U.png)")
                    }
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
                    if($("#pay_price").is(":visible") && $("#tag_price").is(":visible")){
                        $("#discount").show();
                        var val=Number($("#pay_price").find("input").val())/Number($("#tag_price").find("input").val());
                        $("#discount").find("input").val(val.toFixed(2));
                    }
                    var D=audit_info.qjia();
                    D.then(function (data) {
                        var actions=data;
                        var hasCheck=false;
                        var hasEdit=false;
                        for(var a=0;a<actions.length;a++){
                            if(actions[a].act_name=="check"){
                                hasCheck=true;
                            }
                            if(actions[a].act_name=="edit"){
                                hasEdit=true;
                            }
                        }
                        if(list["status"]=="未审核" && hasCheck==true){
                            $("#auditSuccess").show();
                        }
                        if(list["status"]=="未审核" && hasEdit==true){
                            $("#keep").show();
                            if(list.type=="充值"){
                                $("#execution").html("<li>直接充值</li><li>退换转充值</li>")
                            }else if(list.type=="退款"){
                                $("#execution").html("<li>按余额退款</li><li>按充值单退款</li>")
                            }
                        }else{
                            $("#page-wrapper .conpany_msg").find("input").attr("disabled",true);
                        }
                    })
                }
            })
        },
        setBillDate:function(){
            //日期调用插件
            var bill_date = {
                elem: '#bill_date_input',
                format: 'YYYY-MM-DD',
                istime: false,
                max: '2099-06-16 23:59:59', //最大日期
                istoday: true,
                fixed: false,
                isclear: false,
                choose: function (datas) {

                }
            };
            $("#bill_date_input").bind("click",function(){
                laydate(bill_date)
            })
        },
        getStoreName:function(store_code,user_code){
            whir.loading.add("",0.5);
            oc.postRequire("get","/shop/findStore?corp_code="+audit_info.corp_code,"","",function(data){
                whir.loading.remove("",0.5);
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    msg=JSON.parse(msg.list);
                    var corp_html='';
                    if(msg.length>0){
                        for( var i=0;i<msg.length;i++){
                            $("#OWN_CORP").html("");
                            corp_html+='<option value="'+msg[i].store_code+'">'+msg[i].store_name+'</option>';
                        }
                    }
                    $("#OWN_CORP").append(corp_html);
                    $("#OWN_CORP option[value='"+ store_code+"']").attr("selected","true");
                    $("#OWN_CORP").searchableSelect();
                    $('.corp_select .searchable-select-input').keydown(function(event){
                        var event=window.event||arguments[0];
                        if(event.keyCode == 13){
                            audit_info.getUserName();
                        }
                    });
                    $('.searchable-select-item').click(function(){
                           audit_info.getUserName();
                    });
                    audit_info.getUserName(user_code);
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
        getUserName:function(user_code){
            var param={
                store_code:$("#OWN_CORP").val(),
                corp_code:audit_info.corp_code
            };
            $('#user_select').empty().next().remove();
            whir.loading.add("",0.5);
            oc.postRequire("post","/shop/staff","",param,function(data){
                whir.loading.remove("",0.5);
                if(data.code=="0"){
                    var msg=JSON.parse(data.message);
                    var corp_html='';
                    if(msg.length>0){
                        $("#user_select").html("");
                        for( var i=0;i<msg.length;i++){
                            corp_html+='<option value="'+msg[i].user_code+'">'+msg[i].user_name+'</option>';
                        }
                    }
                    $("#user_select").append(corp_html);
                    if(user_code!=""){
                        $("#user_select option[value='"+ user_code+"']").attr("selected","true");
                    }
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
            $("#recharge_type_input").next().on("click","li",function(){
                var val=$(this).html();
                $("#recharge_type_input").val(val);
                $(this).parent().hide();
            });
            $("#recharge_type_input").blur(function(){
                setTimeout(function(){
                    $(this).next().hide();
                },200)
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
                    if($(allInput[i]).parents("div").attr("id")=="active_content"){
                        editParam.activity_content=$(allInput[i]).val()
                    }else{
                        editParam[$(allInput[i]).parents("div").attr("id")]=$(allInput[i]).val()
                    }
                }
                var ISACTIVE="";
                var input=$("#is_active");
                if(input.prop("checked")==true){
                    ISACTIVE="Y";
                }else if(input.prop("checked")==false){
                    ISACTIVE="N";
                }
                editParam.store_code=$("#OWN_CORP").val();
                editParam.store_name=$("#OWN_CORP").next().find(".searchable-select-holder").html();
                editParam.user_name=$("#user_select").next().find(".searchable-select-holder").html();
                editParam.user_code=$("#user_select").val()==null?"":$("#user_select").val();
                editParam.type=$("#type").find("input").val()=="充值"?"pay":$("#type").find("input").val()=="退款"?"refund":"";
                editParam.isactive=ISACTIVE;
                whir.loading.add("",0.5);
                oc.postRequire("post","/vipCheck/editBill","",editParam,function(data){
                    whir.loading.remove("",0.5);
                    if(data.code==0){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "保存成功"
                        })
                    }else{
                        whir.loading.add("mask");
                        $("#tk").show();
                        $("#msg").html(data.message);
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
                whir.loading.add("",0.5);
                oc.postRequire("post","/vipCheck/changeStatus","",params,function(data){
                    whir.loading.remove("",0.5);
                    if(data.code=="0"){
                        art.dialog({
                            time: 2,
                            lock: true,
                            cancel: false,
                            content: "审核成功"
                        });
                       setTimeout(function(){
                           $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit_info.html");
                       },2000)
                    }else{
                        whir.loading.add("mask");
                        $("#tk").show();
                        $("#msg").html(data.message);
                    }
                })
            })
        }
    };
    $(function(){
        audit_info.init();
    })
})();