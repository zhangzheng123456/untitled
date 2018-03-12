var oc = new ObjectControl();
$("#month_input,#year_input").click(function(){
    $(this).prev().children("label").trigger("click")
});
$(".checkbox_isactive label").click(function(){
    var index=$(this).parent().index();
    if(index==0){
        $("#time_select").children().hide().eq(0).show()
    }else if(index==2){
        $("#time_select").children().hide().eq(1).show()
    }
});
function monthData(){
    if($("#month_day").val().trim()==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "日期不能为空"
        });
        return false;
    }
    return true;
}
function yearData(){
    if($("#year_month").val().trim()=="" || $("#year_day").val().trim()==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "日期不能为空"
        });
        return false;
    }
    return true
}
$("#month_day,#year_day").blur(function(){
    var val=Number($(this).val().trim());
    if($(this).val().trim()=="") return;
    if(val<0 || val==0){
        $(this).val("1")
    }
    if(val>31){
        $(this).val("31")
    }
});
$("#year_month").blur(function(){
    var val=Number($(this).val().trim());
    if($(this).val().trim()=="") return;
    if(val<0 || val==0){
        $(this).val("1")
    }
    if(val>12){
        $(this).val("12")
    }
});
$(".operadd_btn ul li:nth-of-type(1)").click(function(){
    var id=sessionStorage.getItem("id");
    var url=id==null?"/approved/insert":"/approved/update";
    var params={};
    params.remarks=$("#message_content").val().trim();
    params.corp_code=$("#OWN_CORP").val();
    params.id=id;
    var ISACTIVE="";
    var input=$("#is_active");
    if(input.prop("checked")==true){
        ISACTIVE="Y";
    }else if(input.prop("checked")==false){
        ISACTIVE="N";
    }
    params.isactive=ISACTIVE;
    if($("#approval_name").val().trim()==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "名称不能为空"
        });
        return
    }
    params.approved_name=$("#approval_name").val().trim();
    if($("#input_moth").prop("checked")){
        var month=monthData();
        if(month){
            params.approved_type="M";
            params.approved_cycle=$("#month_day").val().trim();
            ajaxSubmit(url,params)
        }
    }
    if($("#input_year").prop("checked")){
        var year=yearData();
        if(year){
            params.approved_type="Y";
            params.approved_cycle=$("#year_month").val().trim()+"-"+$("#year_day").val().trim();
            ajaxSubmit(url,params)
        }
    }
});
function getcorplist(a){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var corp_html='';
            var c=null;
            for(var i=0;i<msg.corps.length;i++){
                c=msg.corps[i];
                corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if(a!==""){
                $("#OWN_CORP option[value='"+a+"']").attr("selected","true");
            }
            $('.corp_select select').searchableSelect();
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    $("input[verify='Code']").val("");
                    $("#STORE_NAME").val("");
                    $("input[verify='Code']").attr("data-mark","");
                    $("#STORE_NAME").attr("data-mark","");
                    $("#label_group").attr("data-id","");
                    $("#label_group").val("");
                }
            });
            $('.searchable-select-item').click(function(){
                $("input[verify='Code']").val("");
                $("#STORE_NAME").val("");
                $("input[verify='Code']").attr("data-mark","");
                $("#STORE_NAME").attr("data-mark","");
                $("#label_group").attr("data-id","");
                $("#label_group").val("");
            })
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });//获取企业列表信息
}
function ajaxSubmit(url,params){
    whir.loading.add("", 0.5);
    oc.postRequire("post", url,"", params, function(data){
        whir.loading.remove();
        if(data.code=="0"){
            if(url=="/approved/insert"){
                var id=JSON.parse(data.message).id;
                sessionStorage.setItem("id",id);
                $(window.parent.document).find('#iframepage').attr("src", "/vip/approval_edit.html");
            }
            if(url=="/approved/update"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"保存成功"
                });
            }
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:data.message
            });

        }
    })
}
if($(".pre_title label").text()=="编辑核准计划"){
    var id=sessionStorage.getItem("id");
    var key_val=sessionStorage.getItem("key_val");//取页面的function_code
    key_val=JSON.parse(key_val);
    var funcCode=key_val.func_code;
    var params={};
    params.id=id;
    $.get("/detail?funcCode="+funcCode+"", function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var action=message.actions;
            if(action.length<=0){
                $("#edit_save").remove();
                $("#edit_close").css("margin-left","120px");
            }
        }
    });
    oc.postRequire("post","/approved/select","", params, function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var corp_code=msg.corp_code;//公司编号
            var input=$("#is_active")[0];
            $("#approval_name").val(msg.approved_name);
            $("#message_content").val(msg.remarks);
            if(msg.approved_type=="M"){
                $("#month_input").trigger("click");
                $("#month_day").val(msg.approved_cycle);
            }else if(msg.approved_type=="Y"){
                $("#year_input").trigger("click");
                $("#year_month").val(msg.approved_cycle.split("-")[0]);
                $("#year_day").val(msg.approved_cycle.split("-")[1])
            }
            if(msg.isactive=="Y"){
                input.checked=true;
            }else if(msg.isactive=="N"){
                input.checked=false;
            }
            getcorplist(corp_code);
        }
    })
}else{
    getcorplist();
}
$("#edit_close,#back_vip_label").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/approval.html");
});
