var oc = new ObjectControl();
$(function(){
    window.templateGroup.init();
    $(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    if($(".pre_title label").text()=="编辑消息模板分组"){
        var id=sessionStorage.getItem("id");
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        whir.loading.add("",0.5);
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
        var _params={};
        _params["id"]=id;
        var _command="/smsTemplateType/select";
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                if(msg.brand_code!==""){
                    var brand_codes=msg.brand_code.split(",");
                    var brand_names=msg.brand_name.split(",");
                    var ul = "";
                    for (var i = 0; i < brand_codes.length; i++) {
                        if(brand_codes[i]==""){
                            continue;
                        }
                        //ul+="<li data-code='"+list[i].user_code+"' data-phone='"+list[i].phone+"'>"+list[i].user_name+"<div class='delectxing' onclick='deleteName(this)'></div></li>"
                        ul += "<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='" + brand_codes[i]+ "'  value='" + brand_names[i]+ "'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>";
                    }
                }
                $('.xingming').html(ul);
                $("#template_group_name").val(msg.template_type_name);
                $("#template_group_name").attr("data-name",msg.template_type_name);
                $("#template_group_code").val(msg.template_type_code);
                $("#template_group_code").attr("data-name",msg.template_type_code);
                $("#OWN_CORP option").val(msg.corp.corp_code);
                var Code=msg.corp.corp_code;
                $("#OWN_CORP option").text(msg.corp.corp_name);
                //$("#area_shop").val("共"+msg.store_count+"家店铺");
                // $("#OWN_CORP").val(msg.corp_code);
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
                getcorplist(Code);
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
    }else{
        getcorplist();
    }
});
(function(root,factory){
    root.templateGroup = factory();
}(this,function(){
    var templateGroupjs={};
    templateGroupjs.message={
        "brand_codes":"",
        "brand_names":""
    }
    templateGroupjs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    templateGroupjs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    templateGroupjs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    templateGroupjs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    templateGroupjs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    templateGroupjs.bindbutton=function(){
        var self=this;
        $(".areaadd_oper_btn ul li:nth-of-type(1)").click(function(){
            var name=$("#template_group_name").attr("data-mark");//区域名称是否唯一的标志
            var num=$("#template_group_code").attr("data-mark");//区域编号是否唯一的标志
            if(templateGroupjs.firstStep()){
                if(name=="N"||num=="N"){
                    if(name=="N"){
                        var div=$("#template_group_name").next('.hint').children();
                        div.html("该分组名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if(num=="N"){
                        var div=$("#template_group_code").next('.hint').children();
                        div.html("该分组编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var a = $('.xingming input');
                var brand_codes = "";
                var brand_names= "";
                for (var i = 0; i < a.length; i++) {
                    var u = $(a[i]).attr("data-code");
                    var p = $(a[i]).val();
                    if (i < a.length - 1) {
                        brand_codes += u + ",";
                        brand_names += p + ",";
                    } else {
                        brand_codes += u;
                        brand_names += p;
                    }
                }
                if(brand_codes==""){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"所属品牌不能为空"
                    });
                    return
                }
                var template_group_name=$("#template_group_name").val();
                var template_group_code=$("#template_group_code").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/smsTemplateType/add";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params={
                    "template_type_code":template_group_code,
                    "template_type_name":template_group_name,
                    "corp_code":OWN_CORP,
                    "isactive":ISACTIVE,
                    "brand_codes":brand_codes
                };
                templateGroupjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $("#edit_save").click(function(){
            var name=$("#template_group_name").attr("data-mark");//区域名称是否唯一的标志
            var num=$("#template_group_code").attr("data-mark");//区域编号是否唯一的标志
            if(templateGroupjs.firstStep()){
                if(name=="N"){
                    var div=$("#template_group_name").next('.hint').children();
                    div.html("该分组名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                if(num=="N"){
                    var div=$("#template_group_code").next('.hint').children();
                    div.html("该分组编号已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var a = $('.xingming input');
                var brand_codes = "";
                var brand_names= "";
                for (var i = 0; i < a.length; i++) {
                    var u = $(a[i]).attr("data-code");
                    var p = $(a[i]).val();
                    if (i < a.length - 1) {
                        brand_codes += u + ",";
                        brand_names += p + ",";
                    } else {
                        brand_codes += u;
                        brand_names += p;
                    }
                }
                if(brand_codes==""){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"所属品牌不能为空"
                    });
                    return
                }
                var ID=sessionStorage.getItem("id");
                var template_group_name=$("#template_group_name").val();
                var template_group_code=$("#template_group_code").val();
                var OWN_CORP=$("#OWN_CORP").val();
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/smsTemplateType/edit";//接口名
                var opt = {//返回成功后的操作
                    success:function(){

                    }
                };
                var _params={"id":ID,
                    "template_type_code":template_group_code,
                    "template_type_name":template_group_name,
                    "corp_code":OWN_CORP,
                    "isactive":ISACTIVE,
                    "brand_codes":brand_codes
                };
                templateGroupjs.ajaxSubmit(_command,_params,opt);
            }else{
                return;
            }
        });
        $("#ADD_BRAND").click(function(){
            whir.loading.add("mask",0.5);//加载等待框
            var a=$('.xingming input');//所属客服
            self.message.brand_codes="";
            self.message.brand_names="";
            for(var i=0;i<a.length;i++){
                var u=$(a[i]).attr("data-code");
                var n=$(a[i]).val();
                if(i<a.length-1){
                    self.message.brand_codes+=u+",";
                    self.message.brand_names+=n+",";
                }else{
                    self.message.brand_codes+=u;
                    self.message.brand_names+=n;
                }
            }
            if(self.message.brand_codes!==""){
                var brand_codes=self.message.brand_codes.split(',');
                var brand_names=self.message.brand_names.split(',');
                var brand_html_right="";
                for(var h=0;h<brand_codes.length;h++){
                    brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'   data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            }else{
                $("#screen_brand .s_pitch span").html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#brand_search").val("");
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_brand").css({"position":"fixed"});
            self.getbrandlist();
        });
        //品牌搜索
        $("#brand_search").keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode==13){
                $("#screen_brand .screen_content_l ul").empty();
                self.getbrandlist();
            }
        });
        //品牌放大镜收索
        $("#brand_search_f").click(function(){
            $("#screen_brand .screen_content_l ul").empty();
            self.getbrandlist();
        });
        //点击右移选中
        $(".shift_right").click(function(){
            var right="only";
            var div=$(this);
            removeRight(right,div);
        });
        //点击右移全部
        $(".shift_right_all").click(function(){
            var right="all";
            var div=$(this);
            removeRight(right,div);
        });
        //点击左移
        $(".shift_left").click(function(){
            var left="only";
            var div=$(this);
            removeLeft(left,div);
        });
        //点击左移全部
        $(".shift_left_all").click(function(){
            var left="all";
            var div=$(this);
            removeLeft(left,div);
        });
        //移到右边
        function removeRight(a,b){
            var li="";
            if(a=="only"){
                li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
            }
            if(a=="all"){
                li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
            }
            if(li.length=="0"){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先选择"
                });
                return;
            }
            if(li.length>0){
                for(var i=0;i<li.length;i++){
                    var html=$(li[i]).html();
                    var id=$(li[i]).find("input[type='checkbox']").val();
                    $(li[i]).find("input[type='checkbox']")[0].checked=true;
                    var input=$(b).parents(".screen_content").find(".screen_content_r li");
                    for(var j=0;j<input.length;j++){
                        if($(input[j]).attr("id")==id){
                            $(input[j]).remove();
                        }
                    }
                    $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
                    $(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
                }
            }
            var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
            $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
        }
        //移到左边
        function removeLeft(a,b){
            var li="";
            if(a=="only"){
                li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
            }
            if(a=="all"){
                li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
            }
            if(li.length=="0"){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先选择"
                });
                return;
            }
            if(li.length>0){
                for(var i=li.length-1;i>=0;i--){
                    $(li[i]).remove();
                    $(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
                }
            }
            var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
            $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
        }
        //点击列表显示选中状态
        $(".screen_content").on("click","li",function(){
            var input=$(this).find("input")[0];
            var thinput=$("thead input")[0];
            if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
                input.checked = true;
            }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
                input.checked = false;
            }
        });
        $("#screen_close_brand").click(function(){
            $("#screen_brand").hide();
            whir.loading.remove();//移除加载框
        });
        $("#screen_que_brand").click(function(){
            var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            var brand_codes="";
            var brand_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    brand_codes+=r+",";
                    brand_names+=p+",";
                }else{
                    brand_codes+=r;
                    brand_names+=p;
                }
            };
            self.message.brand_codes=brand_codes;
            self.message.brand_names=brand_names;
            var hasli=$("#OWN_BRAND_All p");
            var a=$('#OWN_BRAND_All input');
            if(li.length>0){
                for(var i=0;i<li.length;i++){
                    for(var j=0;j<a.length;j++){
                        if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
                            $(a[j]).parent("p").remove();
                        }
                    }
                    $('#OWN_BRAND_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id'>删除</span></p>");
                }
            }
            $("#screen_brand").hide();
            whir.loading.remove();//移除加载框
        });
        //删除
        $(".xingming").on("click",".remove_app_id",function(){
            $(this).parent().remove();
        });
    };
    templateGroupjs.getbrandlist=function(){
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        var brand_command = "/shop/brand";
        var brand_code = $("#OWN_CORP").val();
        var searchValue=$("#brand_search").val().trim();
        var pageSize=20;
        var area_param = {};
        area_param["searchValue"]=searchValue;
        area_param["pageSize"]=pageSize;
        area_param["corp_code"]=brand_code;
        oc.postRequire("post", brand_command, "", area_param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list=msg.brands;
                var brand_html = '';
                for (var i = 0; i < list.length; i++) {
                    brand_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxOneInput"
                        + i
                        + 1
                        + "'/><label for='checkboxOneInput"
                        + i
                        + 1
                        + "'></label></div><span class='p16' title='"+list[i].brand_name+"'>"+list[i].brand_name+"</span></li>"
                }
                $("#screen_brand .screen_content_l ul").append(brand_html);
                var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
            } else if (data.code == "-1") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    };
    templateGroupjs.ajaxSubmit=function(_command,_params,opt){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", _command,"",_params, function(data){
            if(data.code=="0"){
                if(_command=="/smsTemplateType/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/message/template_group_edit.html");
                }
                if(_command=="/smsTemplateType/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
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
        if(templateGroupjs['check' + command]){
            if(!templateGroupjs['check' + command].apply(templateGroupjs,[obj,hint])){
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
        templateGroupjs.bindbutton();
    };
    var obj = {};
    obj.templateGroupjs = templateGroupjs;
    obj.init = init;
    return obj;
}));
function getcorplist(C){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var index=0;
            var corp_html='';
            var c=null;
            for(index in msg.corps){
                c=msg.corps[index];
                if(c.corp_code==C){
                    corp_html+='<option value="'+c.corp_code+'" selected>'+c.corp_name+'</option>';
                }else{
                    corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
                }
            }
            $("#OWN_CORP").append(corp_html);
            var corp=$("#OWN_CORP").val();
            $('.corp_select select').searchableSelect();
            $(".corp_select .searchable-select-input").keydown(function (event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    var corp_code = $("#OWN_CORP").val();
                    if(corp!==corp_code) {
                        corp=corp_code;
                        $("#template_group_code").val("");
                        $("#template_group_name").val("");
                        $("#template_group_name").attr("data-mark","");
                        $("#template_group_code").attr("data-mark","");
                        $(".xingming").empty();
                    }
                }
            })
            $(".corp_select .searchable-select-item").click(function () {
                var corp_code = $(this).attr("data-value");
                if(corp!==corp_code) {
                    corp=corp_code;
                    $("#template_group_code").val("");
                    $("#template_group_name").val("");
                    $("#template_group_name").attr("data-mark","");
                    $("#template_group_code").attr("data-mark","");
                    $(".xingming").empty();
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
    });
}
//验证编号是不是唯一
$("#template_group_code").blur(function(){
    // var isCode=/^[A]{1}[0-9]{4}$/;
    var _params={};
    var template_group_code=$(this).val();
    var template_group_code1=$(this).attr("data-name");
    var corp_code=$("#OWN_CORP").val();
    if(template_group_code!==""&&template_group_code!==template_group_code1){
        _params["template_type_code"]=template_group_code;
        _params["corp_code"]=corp_code;
        var div=$(this).next('.hint').children();
        oc.postRequire("post","/smsTemplateType/smsTemplateTypeCodeExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#template_group_code").attr("data-mark","Y");
            }else if(data.code=="-1"){
                $("#template_group_code").attr("data-mark","N");
                div.addClass("error_tips");
                div.html("该分组编号已经存在！");
            }
        })
    }
});
//验证名称是否唯一
$("#template_group_name").blur(function(){
    var corp_code=$("#OWN_CORP").val();
    var template_group_name=$(this).val();
    var template_group_name1=$(this).attr("data-name");
    var div=$(this).next('.hint').children();
    if(template_group_name!==""&&template_group_name!==template_group_name1){
        var _params={};
        _params["template_type_name"]=template_group_name;
        _params["corp_code"]=corp_code;
        oc.postRequire("post","/smsTemplateType/smsTemplateTypeNameExist","", _params, function(data){
            if(data.code=="0"){
                div.html("");
                $("#template_group_name").attr("data-mark","Y");
            }else if(data.code=="-1"){
                div.html("该分组名称已经存在！");
                div.addClass("error_tips");
                $("#template_group_name").attr("data-mark","N");
            }
        })
    }
});
$(".areaadd_oper_btn ul li:nth-of-type(2)").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
$("#edit_close").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
$("#back_tem_group").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/message/template_group.html");
});
