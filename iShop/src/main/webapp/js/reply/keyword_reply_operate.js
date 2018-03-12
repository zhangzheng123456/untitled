var oc = new ObjectControl();
(function(root,factory){
    root.auto = factory();
}(this,function(){
    var autojs={};
    autojs.message={
        "brand_codes":"",
        "brand_names":""
    }
    autojs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    autojs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    autojs.checkCode=function(obj,hint){
        var isCode=/^[M]{1}[0-9]{4}$/;
        if(!this.isEmpty(obj)){
            if(isCode.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"模板编号为必填项，支持以大写M开头必须是4位数字的组合！");
                return false;
            }
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    autojs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    autojs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    autojs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text,textarea");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    autojs.bindbutton=function(){
        var self=this;
        $("#add_save").click(function(){
            if(autojs.firstStep()){
                var OWN_CORP=$("#OWN_CORP").val();//企业编号
                var corp_name=$('#OWN_CORP option:selected').text();//企业名称
                var name=$("#name").val().trim();//名称
                var content=$("#default_content").val().trim();//内容
                var type="1";
                var a=$('#OWN_BRAND_All input');//所属品牌;
                var keyword=$("#keyword_all p");//关键字
                var brand_codes = "";
                var brand_names= "";
                var list=[];
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
                for(var i=0;i<keyword.length;i++){
                    var content=$(keyword[i]).find("input[type='text']").val();
                    if(content==""){
                        continue;
                    }
                    var ismatch="";
                    var match=$(keyword[i]).find("input[type='checkbox']")[0].checked;
                    if(match==false){
                        ismatch="N";
                    }
                    if(match==true){
                        ismatch="Y";
                    }
                    var param={"content":content,"ismatch":ismatch};
                    list.push(param);
                }
                console.log(list);
                if(list.length==0){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"关键字不能为空"
                    });
                    return;
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
                var ISACTIVE="";//是否可用
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/reply/addReply";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "corp_code": OWN_CORP,//公司编号
                    "corp_name":corp_name,//公司名称
                    "name":name,//名称
                    "type":"2",//类型
                    "content": content,//模板内容
                    "isactive": ISACTIVE,//是否可用
                    "brand_code":brand_codes,//所属品牌
                    "brand_name":brand_names,//品牌名称
                    "reply_list":list//关键字
                };
                autojs.ajaxSubmit(_command, _params, opt);
            }else{
                console.log(123123);
                return;
            }
        });
        $("#edit_save").click(function(){
            if(autojs.firstStep()){
                var ID=sessionStorage.getItem("id");
                var OWN_CORP=$("#OWN_CORP").val();//企业编号
                var corp_name=$('#OWN_CORP option:selected').text();//企业名称
                var name=$("#name").val().trim();//名称
                var content=$("#default_content").val().trim();//内容
                var type="2";
                var a=$('#OWN_BRAND_All input');//所属品牌
                var keyword=$("#keyword_all p");//关键字
                var brand_codes = "";
                var brand_names= "";
                var list=[];
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
                for(var i=0;i<keyword.length;i++){
                    var content=$(keyword[i]).find("input[type='text']").val();
                    if(content==""){
                        continue;
                    }
                    var ismatch="";
                    var match=$(keyword[i]).find("input[type='checkbox']")[0].checked;
                    if(match==false){
                        ismatch="N";
                    }
                    if(match==true){
                        ismatch="Y";
                    }
                    var param={"content":content,"ismatch":ismatch};
                    list.push(param);
                }
                if(list.length==0){
                    art.dialog({
                        zIndex:10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"关键字不能为空"
                    });
                    return;
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
                var ISACTIVE="";
                var input=$(".checkbox_isactive").find("input")[0];
                if(input.checked==true){
                    ISACTIVE="Y";
                }else if(input.checked==false){
                    ISACTIVE="N";
                }
                var _command="/reply/updReply";//接口名
                var opt = {//返回成功后的操作
                    success:function(){
                    }
                };
                var _params = {
                    "id": ID,
                    "corp_code": OWN_CORP,//公司编号
                    "corp_name":corp_name,//公司名称
                    "name":name,//名称
                    "type":"2",//类型
                    "content": content,//模板内容
                    "isactive": ISACTIVE,//是否可用
                    "brand_code":brand_codes,//所属品牌
                    "brand_name":brand_names,//品牌名称
                    "reply_list":list//关键字
                };
                autojs.ajaxSubmit(_command,_params,opt);
            }else{
                console.log(12123123);
                return;
            }
        });
        $("#ADD_BRAND").click(function(){
            whir.loading.add("mask",0.5);//加载等待框
            var a=$('#OWN_BRAND_All input');//所属客服
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
        $("#add_keyword").click(function(){
            $("#keyword_all").append("<p><input type='text' style='width: 250px;margin-right: 10px'   value=''><span class='power remove_app_id'>删除</span><span class='r'><span class='r_text' style='vertical-align: middle'>完全匹配</span><em class='checkbox_isactive'>"
                +"<input type='checkbox' value='' class='check'>"
                +"<label></label>"
                +"</em>"
                +"</span></p>")
        })
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
        $("#edit_close,#back").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/reply/keyword_reply.html");
        });
        $("#keyword_all").on("click",".r",function () {
            var input=$(this).find("input")[0];
            if(input.type=="checkbox"&&input.checked==false){
                input.checked = true;
            }else if(input.type=="checkbox"&&input.checked==true){
                input.checked = false;
            }
        })
    };
    autojs.getbrandlist=function(){
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
    autojs.getcorplist=function(a){
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
                var corp=$("#OWN_CORP").val();
                $('.corp_select select').searchableSelect();
                $(".corp_select .searchable-select-input").keydown(function (event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        var corp_code = $("#OWN_CORP").val();
                        if(corp!==corp_code) {
                            corp=corp_code;
                            $("input[verify='Code']").val("");
                            $("#STORE_NAME").val("");
                            $("input[verify='Code']").attr("data-mark","");
                            $("#STORE_NAME").attr("data-mark","");
                            $(".xingming").empty();
                        }
                    }
                })
                $(".corp_select .searchable-select-item").click(function () {
                    var corp_code = $(this).attr("data-value");
                    if(corp!==corp_code) {
                        corp=corp_code;
                        $("input[verify='Code']").val("");
                        $("#STORE_NAME").val("");
                        $("input[verify='Code']").attr("data-mark","");
                        $("#STORE_NAME").attr("data-mark","");
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
        });//获取企业列表信息
    };
    autojs.getselect=function(){
        whir.loading.add("",0.5);//加载等待框
        var param={};
        var id=sessionStorage.getItem("id");
        param["id"]=id;
        oc.postRequire("post", "/reply/select","",param, function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var corp_code=message.corp_code;
                var brand_code=message.brand_code.split(",");
                var brand_name=message.brand_name.split(",");
                $("#name").val(message.name);
                $("#default_content").val(message.content);
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
                for(var i=0;i<brand_code.length;i++){
                    $('#OWN_BRAND_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+brand_code[i]+"'  value='"+brand_name[i]+"'><span class='power remove_app_id'>删除</span></p>");
                }
                for(var i=0;i<message.reply_list.length;i++){
                    if(message.reply_list[i].ismatch=="Y"){
                        $("#keyword_all").append("<p><input type='text' style='width: 250px;margin-right: 10px'   value='"+message.reply_list[i].content+"'><span class='power remove_app_id'>删除</span><span class='r'><span class='r_text' style='vertical-align: middle'>完全匹配</span><em class='checkbox_isactive'>"
                            +"<input type='checkbox' value='' class='check' checked='true'>"
                            +"<label></label>"
                            +"</em>"
                            +"</span></p>")
                    }else if(message.reply_list[i].ismatch=="N"){
                        $("#keyword_all").append("<p><input type='text' style='width: 250px;margin-right: 10px'   value='"+message.reply_list[i].content+"'><span class='power remove_app_id'>删除</span><span class='r'><span class='r_text' style='vertical-align: middle'>完全匹配</span><em class='checkbox_isactive'>"
                            +"<input type='checkbox' value='' class='check'>"
                            +"<label></label>"
                            +"</em>"
                            +"</span></p>")
                    }
                }
                autojs.getcorplist(corp_code);
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
    }
    autojs.ajaxSubmit=function(_command,_params,opt){
        console.log("我是");
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", _command,"", _params, function(data){
            if(data.code=="0"){
                if(_command=="/reply/addReply"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/reply/keyword_reply_edit.html");
                }
                if(_command=="/reply/updReply"){
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
        if(autojs['check' + command]){
            if(!autojs['check' + command].apply(autojs,[obj,hint])){
                return false;
            }
        }
        return true;
    };
    jQuery(":text,textarea").focus(function() {
        var _this = this;
        interval = setInterval(function() {
            bindFun(_this);
        }, 500);
    }).blur(function(event) {
        clearInterval(interval);
    });
    var init=function(){
        autojs.bindbutton();
        if($(".pre_title label").text()=="编辑关键字回复"){
            autojs.getselect();
        }else {
            autojs.getcorplist("");
        }
        $(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:false});
    }
    var obj = {};
    obj.autojs = autojs;
    obj.init = init;
    return obj;
}));
jQuery(document).ready(function(){
    window.auto.init();//初始化
});