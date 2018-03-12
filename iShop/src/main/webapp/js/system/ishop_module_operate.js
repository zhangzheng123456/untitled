var oc = new ObjectControl();
var val = sessionStorage.getItem("key");
val = JSON.parse(val);
var messages= JSON.parse(val.message);
var fixHelper = function(e, ui)     {
    console.log(ui);
    return ui;
};
(function (root, factory) {
    root.param = factory();
}(this, function () {
    var paramjs = {};
    paramjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    paramjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    paramjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    paramjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    paramjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    paramjs.bindbutton = function () {
        $(".operadd_btn ul li:nth-of-type(1)").click(function () {
            if (paramjs.firstStep()) {
                var corp_code=$("#OWN_CORP").val().trim();
                var ISACTIVE = "";
                var icon_order=[];
                var input = $("#is_active")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var ul=$("#icons_list ul");
                var cfg_info={
                };
                for(var i=0;i<ul.length;i++){
                    var a=$(ul[i]).find("li:eq(0)").text();
                    var name=$(ul[i]).find("li:eq(1)").text();
                    var flag=$(ul[i]).attr("data-flag");
                    var isactive="";
                    var input5=$(ul[i]).find("li:eq(3) input")[0];
                    if(input5.checked==true){
                        isactive='Y';
                    }else if(input5.checked==false){
                        isactive='N';
                    }
                    cfg_info[flag]=isactive;
                }
                var _command = "/appBottomIconCfg/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "corp_code":corp_code,
                    "cfg_info":cfg_info,
                    "isactive":ISACTIVE
                };
                whir.loading.add("", 0.5);
                paramjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $(".operedit_btn ul li:nth-of-type(1)").click(function () {
            if (paramjs.firstStep()) {
                var id = sessionStorage.getItem("id");
                var corp_code=$("#OWN_CORP").val().trim();
                var ISACTIVE = "";
                var icon_order=[];
                var input = $("#is_active")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var ul=$("#icons_list ul");
                var cfg_info={
                };
                for(var i=0;i<ul.length;i++){
                    var a=$(ul[i]).find("li:eq(0)").text();
                    var name=$(ul[i]).find("li:eq(1)").text();
                    var flag=$(ul[i]).attr("data-flag");
                    var isactive="";
                    var input5=$(ul[i]).find("li:eq(3) input")[0];
                    if(input5.checked==true){
                        isactive='Y';
                    }else if(input5.checked==false){
                        isactive='N';
                    }
                    cfg_info[flag]=isactive;
                }
                var _command = "/appBottomIconCfg/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                whir.loading.add("", 0.5);
                var _params = {
                    "id":id,
                    "corp_code":corp_code,
                    "cfg_info":cfg_info,
                    "isactive":ISACTIVE
                };
                paramjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#icons_list").on("click",".isactive",function(){
            var tr=$("#icons_list input[type='checkbox']:checked");
                var input=$(this).find("input")[0];
                if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
                    input.checked = true;
                }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
                    input.checked = false;
                }
        });
        $(".operadd_btn ul li:nth-of-type(2)").click(function () {
            if (messages.user_type=="admin") {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module.html");
            }else {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module_user.html");
            }
         });
        $(".operedit_btn ul li:nth-of-type(2)").click(function () {
            if (messages.user_type=="admin") {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module.html");
            }else {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module_user.html");
            }
        });
        $("#back_icons").click(function () {
            if (messages.user_type=="admin") {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module.html");
            }else {
                $(window.parent.document).find('#iframepage').attr("src", "/system/ishop_module_user.html");
            }
        });
    };
    paramjs.getcorplist=function(corp_code){//获取企业列表
        var self=this;
        whir.loading.add("",0.5);
        oc.postRequire("post","/user/getCorpByUser","", "", function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var corp_html='';
                for(var i=0;i<msg.corps.length;i++){
                    corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
                }
                $("#OWN_CORP").append(corp_html);
                if(corp_code!==""){
                    $("#OWN_CORP option[value='"+corp_code+"']").attr("selected","true");
                }
                $('.corp_select select').searchableSelect();
                var code=$("#OWN_CORP").val();
                $('.searchable-select-item').each(function(){
                    if($(this).text() == $('.searchable-select-holder').text()){
                        corp_code = $(this).attr('data-value');
                    }
                });
                $('.corp_select .searchable-select-input').keydown(function(event){
                    var event=window.event||arguments[0];
                    if(event.keyCode == 13){
                        var corp_code1=$("#OWN_CORP").val();
                        if(code!==corp_code1){
                            code=corp_code1;
                        }
                    }
                });
                $('.searchable-select-item').click(function(){
                    var corp_code1=$("#OWN_CORP").val();
                    if(code!==corp_code1){
                        code=corp_code1;
                    }
                })
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    };
    paramjs.superaddition=function(list){
        var html='';
        var num=0;
        for (var key in list) {
            var img="";
            num++;
            if(num=="5"){
                break;
            }
            var name="";
            if(key=="vips"){
                img="../img/vip.png";
                name="会员"
            }
            if(key=="community"){
                img="../img/shequ.png";
                name="社区"
            }
            if(key=="achievement"){
                img="../img/achv.png";
                name="业绩"
            }
            if(key=="goods"){
                img="../img/mall.png";
                name="商品"
            }
            if(list[key]=="Y"){
                html+="<ul data-flag='"+key+"'><li style='text-align: center;width: 25%;'><span class='icon-ishop_6-27'></span>"+
                    num+"</li><li style='width: 25%;text-align: center;'>"+
                    name+"</li><li style='text-align: center;width: 25%;'><img src='"+
                    img+"'></li><li style='width: 25%;text-align: center;' class='isactive'><em class='checkbox_isactive' style='margin-left: -20px;'><input type='checkbox' checked='true'  value='' name='test' class='check' ><label for=''></label></em></li></ul>";
            }else if(list[key]=="N"){
                html+="<ul data-flag='"+key+"'><li style='text-align: center;width: 25%;'><span class='icon-ishop_6-27'></span>"+
                    num+"</li><li style='width: 25%;text-align: center;'>"+
                    name+"</li><li style='text-align: center;width: 25%;'><img src='"+
                    img+"'></li><li style='width: 25%;text-align: center;' class='isactive'><em class='checkbox_isactive' style='margin-left: -20px;'><input type='checkbox' value='' name='test' class='check' ><label for=''></label></em></li></ul>";
            }

        }
        $("#icons_list").html(html);
    };
    paramjs.geticonlist=function(){
        oc.postRequire("post","/appBottomIconCfg/iconList","","", function(data){
            var list=JSON.parse(data.message).list;
            var list=JSON.parse(list).list;
            console.log(list);
            self.superaddition(list);
        })
    };
    paramjs.getselect=function(id){
        var param={};
        param["id"]=id;
        var self=this;
        oc.postRequire("post","/appBottomIconCfg/select","",param, function(data){
            var message=JSON.parse(data.message);
            var corp_code=message.corp_code;
            var cfg_info=JSON.parse(message.cfg_info);
            $("#created_time").val(message.created_date);
            $("#creator").val(message.creater);
            $("#modify_time").val(message.modified_date);
            $("#modifier").val(message.modifier);
            var input=$(".checkbox_isactive").find("input")[0];
            if(message.isactive=="Y"){
                input.checked=true;
            }else if(message.isactive=="N"){
                input.checked=false;
            }
            self.getcorplist(corp_code);
            self.superaddition(cfg_info);
        })
    }
    paramjs.ajaxSubmit = function (_command, _params, opt) {
        console.log(_params);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                if(_command=="/appBottomIconCfg/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/system/ishop_module_edit.html");
                }
                if(_command=="/appBottomIconCfg/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();
        });
    };
    var bindFun = function (obj1) {//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if (obj1) {
            _this = jQuery(obj1);
        } else {
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if (paramjs['check' + command]) {
            if (!paramjs['check' + command].apply(paramjs, [obj, hint])) {
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function () {
        var _this = this;
        interval = setInterval(function () {
            bindFun(_this);
        }, 500);
    }).blur(function (event) {
        clearInterval(interval);
    });
    var init = function () {
        var id=sessionStorage.getItem("id");
        console.log(id);
        paramjs.bindbutton();
        if ($(".pre_title label").text() == "编辑爱秀模块管理"){
            paramjs.getselect(id);
        }else{
            var corp_code="";
            paramjs.getcorplist(corp_code);
            paramjs.geticonlist();
        }
    }
    var obj = {};
    obj.paramjs = paramjs;
    obj.init = init;
    return obj;
}));
$(function(){
    window.param.init();
})