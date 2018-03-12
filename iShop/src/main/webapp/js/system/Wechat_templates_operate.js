var oc = new ObjectControl();
//获取公众号
function getWxList(corp_code){
    var param={};
    param["corp_code"]=corp_code;
    whir.loading.add("","0.5");
    oc.postRequire("post","/corp/selectWx","0",param, function(data){
        whir.loading.remove();
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list = msg.list;
            var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-name="${msg}">${msg}</li>';
            var html = '';
            for(i=0;i<list.length;i++){
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
            });

        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
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
            getWxList($("#OWN_CORP").val());
            $('.corp_select .searchable-select-input').keydown(function(event){
                var event=window.event||arguments[0];
                if(event.keyCode == 13){
                    getWxList($("#OWN_CORP").val());
                    $("#vipCode").val("");
                    $("#vipCode").attr("data-app_id","");
                    $("#vipCode").attr("data-app_name","");
                    $("#vipCode").attr("data-app_user_name","");
                    $("#wx_title").val("");
                    $("#wx_title").attr("data-id","");
                }
            });
            $('.searchable-select-item').click(function(){
                getWxList($("#OWN_CORP").val());
                $("#vipCode").val("");
                $("#vipCode").attr("data-app_id","");
                $("#vipCode").attr("data-app_name","");
                $("#vipCode").attr("data-app_user_name","");
                $("#wx_title").val("");
                $("#wx_title").attr("data-id","");
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
    oc.postRequire("get", "/wxTemplate/selectName","", "", function(data){
            var msg=JSON.parse(data.message);
            var list = msg.name.split(",");
            var tempHTML = ' <li>${msg}</li>';
            var html = '';
            for(var i=0;i<list.length;i++){
                if(list[i]!=""){
                    var nowHTML = tempHTML;
                    nowHTML = nowHTML.replace('${msg}',list[i]);
                    html+=nowHTML;
                }
            }
            $('#wx_title_select').html(html);
            $('#wx_title_select li').click(function () {
                $('#wx_title').val($(this).text());
            });
    })
}
function ajaxSubmit(url,params){
    whir.loading.add("", 0.5);
    oc.postRequire("post", url,"", params, function(data){
        whir.loading.remove();
        if(data.code=="0"){
            if(url=="/wxTemplate/insert"){
                var id=JSON.parse(data.message).id;
                sessionStorage.setItem("id",id);
                $(window.parent.document).find('#iframepage').attr("src", "/system/Wechat_templates_adit.html");
            }
            if(url=="/wxTemplate/update"){
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
if($(".pre_title label").text()=="编辑微信模板"){
    var id=sessionStorage.getItem("id");
    var key_val=sessionStorage.getItem("key_val");//取页面的function_code
    key_val=JSON.parse(key_val);
    var funcCode=key_val.func_code;
    var params={};
    params.id=id;
    whir.loading.add("","0.5");
    oc.postRequire("post","/wxTemplate/select","", params, function(data){
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
            $("#wx_industry").val(msg.industry);
            $("#wx_template_id").val(msg.template_id);
            $("#wx_content").html(msg.template_content_data);
            $("#wx_content_example").html(msg.content_example);
            $("#wx_title").val(msg.template_name);
            $("#vipCode").attr("data-app_id",msg.app_id);
            $("#vipCode").attr("data-app_name",msg.app_name);
            $("#vipCode").attr("data-app_user_name",msg.app_user_name);
            $("#vipCode").val(msg.app_name);
            getcorplist(corp_code);
        }
    })
}else{
    getcorplist();
}
templateName();
function confirm(){
    var corp_code=$("#OWN_CORP").val();
    var app_id=$("#vipCode").attr("data-app_id");
    var wx_title=$("#wx_title").val().trim();
    var wx_industry=$("#wx_industry").val().trim();
    var wx_template_id=$("#wx_template_id").val().trim();
    var wx_content=$("#wx_content").val().trim();
    var wx_content_example=$("#wx_content_example").val().trim();
    var confirmData={
        corp_code:corp_code,
        app_id:app_id,
        template_name:wx_title,
        industry:wx_industry,
        template_id:wx_template_id,
        template_content_data:wx_content,
        content_example:wx_content_example
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
    if(wx_industry==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请填写行业"
        });
        return false
    }
    if(wx_template_id==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请填写模板id"
        });
        return false
    }
    if(wx_content==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请填写详细内容"
        });
        return false
    }
    if(wx_content_example==""){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请填写内容示例"
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
        url="/wxTemplate/insert";
    }else{
        url="/wxTemplate/update";
        params.id=id;
    }
    ajaxSubmit(url,params)
});
$("#wx_close,#back_wx").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/system/WeChat_templates.html");
});