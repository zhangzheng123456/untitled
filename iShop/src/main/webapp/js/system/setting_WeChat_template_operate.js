/**
 * Created by Administrator on 2017/6/27.
 */
var oc = new ObjectControl();
//获取公众号
function getWxList(corp_code){
    var param={};
    var def= $.Deferred();
    param["corp_code"]=corp_code;
    whir.loading.add("","0.5");
    oc.postRequire("post","/corp/selectWx","0",param, function(data){
        whir.loading.remove();
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list = msg.list;
            var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-name="${msg}">${msg}</li>';
            var html = '';
            for(var i=0;i<list.length;i++){
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${id}',list[i].app_id);
                nowHTML = nowHTML.replace('${usermane}',list[i].app_user_name);
                nowHTML = nowHTML.replace('${msg}',list[i].app_name);
                nowHTML = nowHTML.replace('${msg}',list[i].app_name);
                html+=nowHTML;
            }
            $('#vipPublic').html(html);
            $('#vipPublic li').click(function () {
                $('#vipCode').val($(this).text());
                $('#vipCode').attr('data-app_id',$(this).attr('data-id'));
                $('#vipCode').attr('data-app_user_name',$(this).attr('data-username'));
                $('#vipCode').attr('data-app_name',$(this).attr('data-name'));
                $("#wx_title").val("");
                $("#wx_title").attr("data-id","");
                templateName($("#OWN_CORP").val());
            });
            def.resolve();
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
    return def;
}
//获取企业
function getcorplist(a){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    whir.loading.add("","0.5");
    oc.postRequire("post", corp_command,"", "", function(data){
        whir.loading.remove();
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
            getWxList($("#OWN_CORP").val()).then(function(){
                templateName($("#OWN_CORP").val())
            });
            getVipCardTypes($("#OWN_CORP").val());
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    $("#vipCode").val("");
                    $("#vipCode").attr("data-app_id","");
                    $("#vipCode").attr("data-app_name","");
                    $("#vipCode").attr("data-app_user_name","");

                    $("#wx_title").val("");
                    $("#wx_title").attr("data-id","");
                    $("#temp_tip").html('标记说明："#store#" 店铺名；"#name#" 会员名；"#birthday#" 会员生日；"#join_time#" 加入时间；"#sex#" 会员性别');
                    getWxList($("#OWN_CORP").val()).then(function(){
                        templateName($("#OWN_CORP").val())
                    });
                    getVipCardTypes($("#OWN_CORP").val());
                }
            });
            $('.searchable-select-item').click(function(){
                $("#vipCode").val("");
                $("#vipCode").attr("data-app_id","");
                $("#vipCode").attr("data-app_name","");
                $("#vipCode").attr("data-app_user_name","");

                $("#wx_title").val("");
                $("#wx_title").attr("data-id","");
                $("#temp_tip").html('标记说明："#store#" 店铺名；"#name#" 会员名；"#birthday#" 会员生日；"#join_time#" 加入时间；"#sex#" 会员性别');
                getWxList($("#OWN_CORP").val()).then(function(){
                    templateName($("#OWN_CORP").val());
                });
                getVipCardTypes($("#OWN_CORP").val());
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
function templateName(){
    var param={
        corp_code:$("#OWN_CORP").val(),
        app_id:$("#vipCode").attr("data-app_id")
    };
    oc.postRequire("post", "/wxTemplate/selectAllWx","", param, function(data){
        var msg=JSON.parse(data.message);
        var list = msg.list;
        var html ="";
        for(var i=0;i<list.length;i++){
            html+='<li id="'+list[i].template_id+'">'+list[i].template_name+'</li>'
        }
        $('#wx_title_select').html(html);
        $('#wx_title_select').on("click","li",function () {
            $('#wx_title').val($(this).text());
            $('#wx_title').attr("data-id",$(this).attr("id"));
            if($(this).text()=="会员等级变更提醒"){
                $("#change_type").show();
                $("#change_vip_card_type").show();
                $("#temp_tip").html('标记说明："#name#" 会员名;"#card_type#" 原会员类型;"#card_no#" 会员卡号;"#new_card_type#" 会员升级/降级后类型')
            }else if($(this).text()=="积分到期提醒"){
                $("#change_type").hide();
                $("#change_vip_card_type").hide();
                $("#temp_tip").html('标记说明："#name#" 会员名;"#card_type#" 会员类型;"#card_no#" 会员卡号;"#clean_time#" 清理时间;"#clean_points#" 到期积分')
            }else{
                $("#change_type").hide();
                $("#change_vip_card_type").hide();
                $("#temp_tip").html('标记说明："#store#" 店铺名；"#name#" 会员名；"#birthday#" 会员生日；"#join_time#" 加入时间；"#sex#" 会员性别');
            }
        });
    })
}
function getVipCardTypes (corp_code){
    var param={};
    param["corp_code"]=corp_code;
    $('#VIP_TYPE').empty();
    $('#vip_type_select .searchable-select').remove();
    oc.postRequire("post","/vipCardType/getVipCardTypes","", param, function(data) {
        if(data.code=="0"){
            var list=JSON.parse(data.message).list;
            var list=JSON.parse(list);
            var html="<option value=''>请选择</option>";
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    html+="<option data-name='"+list[i].vip_card_type_name+"' value='"+list[i].vip_card_type_code+"'>"+list[i].vip_card_type_name+"("+list[i].vip_card_type_code+")</option>"
                }
            }else{
                html+="<option value=''>无会员类型</option>";
            }
            $("#VIP_TYPE").append(html);
            var vip_type_code=$("#VIP_TYPE").attr("data-value");
            if(vip_type_code && vip_type_code!==""){
                $("#VIP_TYPE option[value='"+vip_type_code+"']").attr("selected","true");
                $("#VIP_TYPE").removeAttr("data-value");
            }
            $('#VIP_TYPE').searchableSelect();
            $('#vip_type_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){

                }
            });
            $('#vip_type_select .searchable-select-item').click(function(){

            })
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
function ajaxSubmit(url,params){
    whir.loading.add("", 0.5);
    oc.postRequire("post", url,"", params, function(data){
        whir.loading.remove();
        if(data.code=="0"){
            if(url=="/wxTemplateContent/insert"){
                var id=JSON.parse(data.message).id;
                sessionStorage.setItem("id",id);
                $(window.parent.document).find('#iframepage').attr("src", "/system/setting_WeChat_template_edit.html");
            }
            if(url=="/wxTemplateContent/update"){
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
if($(".pre_title label").text()=="编辑模板配置"){
    var id=sessionStorage.getItem("id");
    var key_val=sessionStorage.getItem("key_val");//取页面的function_code
    key_val=JSON.parse(key_val);
    var funcCode=key_val.func_code;
    var params={};
    params.id=id;
    whir.loading.add("","0.5");
    oc.postRequire("post","/wxTemplateContent/select","", params, function(data){
        if(data.code=="0"){
            whir.loading.remove();
            var msg=JSON.parse(data.message);
            var corp_code=msg.corp_code;//公司编号
            var input=$("#is_active")[0];
            if(msg.isactive=="Y"){
                input.checked=true;
            }else if(msg.isactive=="N"){
                input.checked=false;
            }
            $("#wx_title").attr("data-id",msg.template_id);
            $("#wx_title").val(msg.template_name);
            $("#vipCode").attr("data-app_id",msg.app_id);
            $("#vipCode").attr("data-app_name",msg.app_name);
            $("#vipCode").attr("data-app_user_name",msg.app_user_name);
            $("#vipCode").val(msg.app_name);
            $("#heading").val(msg.template_first);
            $("#remark").val(msg.template_remark);
            $("#tem_link").val(msg.template_url);
            $("#VIP_TYPE").attr("data-value",msg.vip_card_type_id);
            if(msg.template_name=="会员等级变更提醒"){
                $("#change_type").show();
                $("#change_vip_card_type").show();
                $("#temp_tip").html('标记说明："#name#" 会员名;"#card_type#" 原会员类型;"#card_no#" 会员卡号;"#new_card_type#" 会员升级/降级后类型')
            }
            if(msg.template_name=="积分到期提醒"){
                $("#temp_tip").html('标记说明："#name#" 会员名;"#card_type#" 会员类型;"#card_no#" 会员卡号;"#clean_time#" 清理时间;"#clean_points#" 到期积分');
            }
            msg.type=="up"?$("#change_type_input").val("升级"):$("#change_type_input").val("降级")
            getcorplist(corp_code)
        }
    })
}else{
    getcorplist();
}
function confirm(){
    var corp_code=$("#OWN_CORP").val();
    var app_id=$("#vipCode").attr("data-app_id");
    var wx_title=$("#wx_title").val().trim();
    var wx_title_id=$("#wx_title").attr("data-id");
    var template_first=$("#heading").val().trim();
    var template_remark=$("#remark").val().trim();
    var template_url=$("#tem_link").val().trim();
    var vip_card_type=$("#VIP_TYPE").val().trim();
    var reg=/^((https|http)?:\/\/)[^\s]+/;
    var confirmData={
        corp_code:corp_code,
        app_id:app_id,
        template_name:wx_title,
        template_id:wx_title_id,
        template_first:template_first,
        template_remark:template_remark,
        template_url:template_url
    };
    if(corp_code==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请选择所属企业"
        });
        return false
    }
    if(app_id==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请选择公众号"
        });
        return false
    }
    if(wx_title==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请选择标题"
        });
        return false
    }
    if($("#change_type").is(":visible") && $("#change_type_input").val()==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请选择变更类型"
        });
        return false
    }
    //if($("#change_vip_card_type").is(":visible") && vip_card_type==""){
    //    art.dialog({
    //        zIndex:10003,
    //        time: 1,
    //        lock: true,
    //        cancel: false,
    //        content: "请选择会员类型"
    //    });
    //    return false
    //}
    if(template_first=="" || template_remark==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请完善模板配置"
        });
        return false
    }
    if($("#tem_link").val().trim()!=""&&!reg.test($("#tem_link").val().trim())){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "请输入有效的链接地址"
        });
        return false;
    }
    return confirmData
}
$(".operadd_btn ul li:nth-of-type(1)").click(function(){
    var id=sessionStorage.getItem("id");
    var url="";
    var params={};
    var confirmData=confirm();
    if(confirmData==false){
        return
    }
    params=confirmData;
    var ISACTIVE="";
    var input=$("#is_active");
    if(input.prop("checked")==true){
        ISACTIVE="Y";
    }else if(input.prop("checked")==false){
        ISACTIVE="N";
    }
    params.isactive=ISACTIVE;
    params.app_name=$("#vipCode").attr("data-app_name");
    params.app_user_name=$("#vipCode").attr("data-app_user_name");
    if(id==null){
        url="/wxTemplateContent/insert";
    }else{
        url="/wxTemplateContent/update";
        params.id=id;
    }
    if($("#change_type").is(":visible")){
        var val=$("#change_type_input").val().trim();
        if(val=="升级"){
            params.type="up"
        }else if(val=="降级"){
            params.type="down"
        }
    }
    if($("#change_vip_card_type").is(":visible")){
        params.vip_card_type=$("#VIP_TYPE").val().trim();
    }
    ajaxSubmit(url,params)
});
$("#wx_close,#back_wx").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/system/setting_WeChat_template.html");
});