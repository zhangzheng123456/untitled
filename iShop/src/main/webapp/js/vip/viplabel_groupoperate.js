var oc = new ObjectControl();
(function(root,factory){
    root.viplabel = factory();
}(this,function(){
    var viplabeljs={};
    viplabeljs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    viplabeljs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    viplabeljs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    viplabeljs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    viplabeljs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    viplabeljs.bindbutton=function(){
        $(".operadd_btn ul li:nth-of-type(1)").click(function(){
            if(viplabeljs.firstStep()){
                var nameMark = $("#vipgp_name").attr("data-mark");
                var codeMark = $("#vipgp_code").attr("data-mark");
                if (nameMark == "N" || codeMark == "N") {
                    if (nameMark == "N") {
                        var div = $("#vipgp_name").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#vipgp_code").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var OWN_CORP=$("#OWN_CORP").val();//公司编号
                var labelgp_code=$("#vipgp_code").val();//标签分组编号
                var labelgp_name=$("#vipgp_name").val();//标签分组名称
                var remark=$("#remark").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/viplablegroup/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,
                    "label_group_code": labelgp_code,
                    "label_group_name": labelgp_name,
                    "remark":remark,
                    "isactive": ISACTIVE
                };
                viplabeljs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function(){
            if(viplabeljs.firstStep()){
                var nameMark = $("#vipgp_name").attr("data-mark");
                var codeMark = $("#vipgp_code").attr("data-mark");
                if (nameMark == "N" || codeMark == "N") {
                    if (nameMark == "N") {
                        var div = $("#vipgp_name").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (codeMark == "N") {
                        var div = $("#vipgp_code").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var ID=sessionStorage.getItem("id");//编辑时候的id
                var OWN_CORP=$("#OWN_CORP").val();//公司编号
                var labelgp_code=$("#vipgp_code").val();//标签分组编号
                var labelgp_name=$("#vipgp_name").val();//标签分组名称
                var remark=$("#remark").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/viplablegroup/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "id": ID,
                    "corp_code": OWN_CORP,
                    "label_group_code": labelgp_code,
                    "label_group_name": labelgp_name,
                    "remark":remark,
                    "isactive": ISACTIVE
                };
                viplabeljs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
    };
    viplabeljs.ajaxSubmit=function(_command,_params,opt){
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                if(_command=="/viplablegroup/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/viplabel_groupedit.html");
                }
                if(_command=="/viplablegroup/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/vip/viplabel_group.html");
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();
        });
    };
    var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if(obj1){
            _this = jQuery(obj1);
        }else{
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if(viplabeljs['check' + command]){
            if(!viplabeljs['check' + command].apply(viplabeljs,[obj,hint])){
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function() {
        var _this = this;
        interval = setInterval(function() {
            bindFun(_this);
        }, 500);
    }).blur(function(event) {
        clearInterval(interval);
    });
    var init=function(){
        viplabeljs.bindbutton();
    }
    var obj = {};
    obj.viplabeljs = viplabeljs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function(){
    window.viplabel.init();//初始化
    if($(".pre_title label").text()=="编辑标签分组"){
        var id=sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        $.get("/detail?funcCode="+funcCode+"", function(data){
            var data=JSON.parse(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var action=message.actions;
                if(action.length<=0){
                    $("#edit_save").remove();
                    $("#edit_close").css("margin-left","120px");
                }
            }
        });
        var _params={"id":id};
        var _command="/viplablegroup/selectById";
        var a="";
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                    msg=JSON.parse(msg.viplableGroup);
                console.log(msg);
                var corp_code=msg.corp_code;//公司编号
                $("#vipgp_code").val(msg.label_group_code);
                $("#vipgp_code").attr("data-name", msg.label_group_code);
                $("#vipgp_name").attr("data-name", msg.label_group_name);
                $("#vipgp_name").val(msg.label_group_name);
                $("#remark").val(msg.remark);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var input=$(".checkbox_isactive").find("input")[0];
                if(msg.isactive=="Y"){
                    input.checked=true;
                }else if(msg.isactive=="N"){
                    input.checked=false;
                }
                getcorplist(corp_code);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    }else{
        getcorplist(a);
    }
    $("#vipgp_code").blur(function () {
        var corp_code=$("#OWN_CORP").val();
        var labelgp_code = $("#vipgp_code").val();
        var labelgp_code1 = $("#vipgp_code").attr("data-name");
        var div = $(this).next('.hint').children();
        if (labelgp_code!== "" && labelgp_code !== labelgp_code1) {
            var _params = {};
            _params["corp_code"] = corp_code;
            _params["label_group_code"]=labelgp_code;
            oc.postRequire("post", "/viplablegroup/checkCodeOnly", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#vipgp_code").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    div.html("该编号已经存在！")
                    div.addClass("error_tips");
                    $("#vipgp_code").attr("data-mark", "N");
                }
            })
        }
    })
    $("#vipgp_name").blur(function () {
        var corp_code=$("#OWN_CORP").val();
        var labelgp_name = $("#vipgp_name").val();
        var labelgp_name1 = $("#vipgp_name").attr("data-name");
        var div = $(this).next('.hint').children();
        if (labelgp_name!== "" && labelgp_name !== labelgp_name1) {
            var _params = {};
            _params["corp_code"] = corp_code;
            _params["label_group_name"]=labelgp_name;
            oc.postRequire("post", "/viplablegroup/checkNameOnly", "", _params, function (data) {
                if (data.code == "0") {
                    div.html("");
                    $("#vipgp_name").attr("data-mark", "Y");
                } else if (data.code == "-1") {
                    div.html("该名称已经存在！")
                    div.addClass("error_tips");
                    $("#vipgp_name").attr("data-mark", "N");
                }
            })
        }
    })
    $(".operadd_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/vip/viplabel_group.html");
    });
    $(".operedit_btn ul li:nth-of-type(2)").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/vip/viplabel_group.html");
    });
    $("#back_vip_labelGroup").click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/vip/viplabel_group.html");
    });
});
function getcorplist(a){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
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
            $('.searchable-select-item').click(function(){
                $("input[verify='Code']").val("");
                $("#STORE_NAME").val("");
                $("input[verify='Code']").attr("data-mark","");
                $("#STORE_NAME").attr("data-mark","");
            })
            //getstafflist(userNum);
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
