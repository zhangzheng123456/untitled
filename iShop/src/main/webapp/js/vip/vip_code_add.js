/**
 * Created by hu.x on 2017/3/15.
 */
var oc = new ObjectControl();
var isscroll=false;
var area_num=1;
(function(root,factory){
    root.vipCardType= factory();
}(this,function(){
    var vipCardTypeJs={};
    vipCardTypeJs.param={//定义参数类型
        "card":true,
        "name":true,
        "vip_type_code":"",
        "vip_type_name":""
    };
    vipCardTypeJs.isEmpty=function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    };
    vipCardTypeJs.checkEmpty = function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    };
    vipCardTypeJs.checkNumber=function(obj,hint){//检查必须是数字但是可以为空的状态
        var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
        if(!this.isEmpty(obj)){
            if(isCode.test(obj)){
                this.hiddenHint(hint);
                return true;
            }else{
                this.displayHint(hint,"此处仅支持输入数字！");
                return false;
            }
        }else{
            this.displayHint(hint);
            return true;
        }
    };
    vipCardTypeJs.hiddenHint = function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    vipCardTypeJs.displayHint = function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    vipCardTypeJs.firstStep = function(){
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!bindFun(inputText[i]))return false;
        }
        return true;
    };
    vipCardTypeJs.bindbutton=function(){
        var self=this;
        $("#edit_save").click(function(){
            var param={};
            var corp_code = cache.corp_code;
            if($("#vipCode").val() == '请选择公众号' || $('#vipCode').val() == ''){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择公众号"
                });
                return;
            }else{
                var app_id = $("#vipCode").attr('app_id');
                var app_name = $("#vipCode").val();
                var app_user_name = $("#vipCode").attr('app_user_name');

            }
           var qrcode_type=$("#vipCodeType").val().trim();//二维码类型
            var aging=$("#vipCodeDate").val().trim();//有效期限
            var qrcode_name=$("#vipCodeName").val().trim();//二维码名称
            var remark=$("#vipCodeNote").val().trim();//备注
            var input=$("#is_active")[0];//是否可用
            if(input.checked==true){//是否可用
                isactive="Y";
            }else if(input.checked==false){
                isactive="N";
            }
            var a=$('#OWN_AREA_All input');
            var AREA_CODE="";
            for(var i=0;i<a.length;i++){
                var u=$(a[i]).attr("data-code");
                if(i<a.length-1){
                    AREA_CODE+=u+",";
                }else{
                    AREA_CODE+=u;
                }
            }
            var b=$('#OWN_BRAND_All input');
            var BRAND_CODE="";
            for(var i=0;i<b.length;i++){
                var U=$(b[i]).attr("data-code");
                if(i<b.length-1){
                    BRAND_CODE+=U+",";
                }else{
                    BRAND_CODE+=U;
                }
            }
            if (qrcode_name == "") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "二维码名称不能为空"
                });
                return;
            }
            if ($('.searchable-select-holder').text() == "请选择所属企业") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择所属企业"
                });
                return;
            }
            if (app_name == "请选择公众号") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择公众号"
                });
                return;
            }
            if (qrcode_type == "请选择类型") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择类型"
                });
                return;
            }
            if (aging == "请选择时效") {
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择时效"
                });
                return;
            }
            if(qrcode_type=='材料类'){
                qrcode_type = 'material';
            }else if(qrcode_type=='印刷类'){
                qrcode_type = 'print';
            }else if(qrcode_type=='礼品类'){
                qrcode_type = 'gift';
            }
            param["corp_code"]=corp_code;
            param["app_id"]=app_id;
            param["app_name"]=app_name;
            param["app_user_name"]=app_user_name;
            param["qrcode_type"]=qrcode_type;
            param["aging"]=aging;
            param["qrcode_name"]=qrcode_name;
            param["remark"]=remark;
            param["isactive"]=isactive;//是否可用
            var id=sessionStorage.getItem("id");//获取保存的id
            if(id==null){
                var command="/qrCode/insert";
                vipCardTypeJs.ajaxSubmit(command,param);
            }else if(id!==null){
                //if ($('#vipCodeShow img').attr('src') == "") {
                //    art.dialog({
                //        zIndex:10003,
                //        time: 1,
                //        lock: true,
                //        cancel: false,
                //        content: "请点击显示获取二维码"
                //    });
                //    return;
                //}
                param["id"]=id;
                //param["qrcode"]=$('#vipCodeShow img').attr('src');
                var command="/qrCode/update";
                vipCardTypeJs.ajaxSubmit(command,param);
            }else{
            return;
        }
    });
        //关闭
        $("#edit_close").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/vip/vip_code.html");
        });
        //回到列表
        $("#back_regime").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/vip/vip_code.html");
        });
        //验证会员定义编号是否唯一
        $("#vip_type_code").blur(function(){
            var param={};
            var vip_type_code=$("#vip_type_code").val();
            param["corp_code"]=$("#OWN_CORP").val();//企业编号
            param["vip_card_type_code"]=vip_type_code;//会员类型
            var div=$(this).next('.hint').children();
            if(vip_type_code!==""&&vip_type_code!==self.param.vip_type_code){
                oc.postRequire("post","/vipCardType/vipCardTypeCodeExist","",param, function(data){
                    if(data.code=="0"){
                        div.html("");
                        self.param.card=true;
                    }else if(data.code=="-1"){
                        div.addClass("error_tips");
                        div.html(data.message+"！");
                        self.param.card=false;
                    }
                })
            }
        });
        //验证会员定义编号是否唯一
        $("#vip_type_name").blur(function(){
            var param={};
            var vip_type_name=$("#vip_type_name").val();
            param["corp_code"]=$("#OWN_CORP").val();//企业编号
            param["vip_card_type_name"]=vip_type_name;//会员类型
            var div=$(this).next('.hint').children();
            if(vip_type_name!==""&&vip_type_name!==self.param.vip_type_name){
                oc.postRequire("post","/vipCardType/vipCardTypeNameExist","",param, function(data){
                    if(data.code=="0"){
                        div.html("");
                        self.param.name=true;
                    }else if(data.code=="-1"){
                        div.addClass("error_tips");
                        div.html(data.message+"！");
                        self.param.name=false;
                    }
                })
            }
        });
    };
    vipCardTypeJs.getInputValue=function(id){//编辑时给input赋值
        var param={};
        var self=this;
        param["id"]=id;
        whir.loading.add("",0.5);
        oc.postRequire("post","/qrCode/select","",param,function(data){
            console.log(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var corp_code = message.corp_code;
                var app_id = message.app_id;
                var app_name = message.app_name;
                var app_user_name = message.app_user_name;
                var qrcode_type = message.qrcode_type;
                var aging = message.aging;
                var qrcode_name = message.qrcode_name;
                var remark = message.remark;
                var qrcode = message.qrcode;//二维码图片地址
                if(qrcode_type=='material'){
                    qrcode_type = '材料类';
                }else if(qrcode_type=='print'){
                    qrcode_type = '印刷类';
                }else if(qrcode_type=='gift'){
                    qrcode_type = '礼品类';
                }
                if(qrcode !=undefined && qrcode !==''){
                    $('#theShow ').hide();
                    $('#vipCodeShow img').attr('src',qrcode);
                    $('#vipCodeShow img').show();
                }else{
                    $('#theShow ').show();
                    $('#vipCodeShow img').css('display','none');
                }
                $("#vipCode").attr('app_id',app_id);
                $("#vipCode").val(app_name);
                findCropName(corp_code)
                $("#vipCode").attr('app_user_name',app_user_name);
                $("#vipCodeType").val(qrcode_type);
                $("#vipCodeDate").val(aging);
                $("#vipCodeName").val(qrcode_name);
                $("#vipCodeNote").val(remark);
                $("#created_time").val(message.created_date);//创建时间
                $("#creator").val(message.creater);//创建人
                $("#modify_time").val(message.modified_date);//修改人
                $("#modifier").val(message.modifier);//修改时间
                var input=$("#is_active")[0];//是否可用
                //是否可用
                if(message.isactive=="Y"){
                    input.checked=true;
                }else if(message.isactive=="N"){
                    input.checked=false;
                }
                //self.getcorplist(corp_code);
            }else if(data.code=="-1"){
                alert(data.message);
            }
            whir.loading.remove();//移除加载框
        })
    };
    vipCardTypeJs.ajaxSubmit=function(command,param){//提交接口
        whir.loading.add("",0.5);
        oc.postRequire("post", command,"",param, function(data){
            if(data.code=="0"){
                if(command=="/qrCode/insert"){
                    var msg = JSON.parse(data.message);
                    sessionStorage.setItem("id",msg.id);
                    console.log(msg.id)
                    $(window.parent.document).find('#iframepage').attr("src","/vip/vip_code_editor.html");
                }
                if(command=="/qrCode/update"){
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
        if(vipCardTypeJs['check' + command]){
            if(!vipCardTypeJs['check' + command].apply(vipCardTypeJs,[obj,hint])){
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


    $("#screen_close_area").click(function(){
        whir.loading.remove("mask");
        $("#screen_area").hide();
    });
    $("#screen_close_brand").click(function(){
        whir.loading.remove("mask");
        $("#screen_brand").hide();
    });
    $("#ADD_AREA").click(function () {
        whir.loading.add("mask");
        $("#screen_area .screen_content_l").unbind("scroll");
        var arr=whir.loading.getPageSize();
        area_num=1;
        $("#screen_area .s_pitch span").html("0");
        $("#area_search").val("");
        $("#screen_area .screen_content_l ul").empty();
        $("#screen_area .screen_content_r ul").empty();
        $("#screen_area").show();
        var left=(arr[0]-$("#screen_area").width())/2;
        var tp=(arr[3]-$("#screen_area").height())/2+40;
        $("#screen_area").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
        getArea(area_num);
        isscroll=false;
        console.log(1);
    });
    $("#ADD_BRAND").click(function(){
        whir.loading.add("mask");
        var arr=whir.loading.getPageSize();
        $("#brand_search").val("");
        $("#screen_brand .s_pitch span").html("0");
        $("#screen_brand .screen_content_l ul").empty();
        $("#screen_brand .screen_content_r ul").empty();
        $("#screen_brand").show();
        var left=(arr[0]-$("#screen_brand").width())/2;
        var tp=(arr[3]-$("#screen_brand").height())/2+40;
        $("#screen_brand").css({"left":left+"px","top":tp+"px","position":"fixed"});
        getBrand();
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
    //获取区域
    function getArea(a){
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        var area_command = "/area/selAreaByCorpCode";
        var corp_code = $('#OWN_CORP').val();
        var searchValue=$("#area_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var area_param = {};
        area_param["searchValue"]=searchValue;
        area_param["pageSize"]=pageSize;
        area_param["pageNumber"]=pageNumber;
        area_param["corp_code"]=corp_code;
        oc.postRequire("post", area_command, "", area_param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list=JSON.parse(msg.list);
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var area_html = '';
                if (list.length == 0) {

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            area_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
                        }
                    }
                }
                if(hasNextPage==true){
                    area_num++;
                    area_next=false;
                }
                if(hasNextPage==false){
                    area_next=true;
                }
                $("#screen_area .screen_content_l ul").append(area_html);
                if(!isscroll){
                    $("#screen_area .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >=nScrollHight){
                            if(area_next){
                                return;
                            }
                            getArea(area_num);
                        }
                    });
                }
                isscroll=true;
                var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
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

    }
    //获取品牌
    function getBrand(){
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

    }
    //区域搜索
    $("#area_search").keydown(function(){
        $("#screen_area .screen_content_l").unbind("scroll");
        isscroll=false;
        var event=window.event||arguments[0];
        area_num=1;
        if(event.keyCode == 13){
            $("#screen_area .screen_content_l ul").empty();
            getArea(area_num);
        }
    });
    //品牌搜索
    $("#brand_search").keydown(function(){
        var event=window.event||arguments[0];
        if(event.keyCode == 13){
            $("#screen_brand .screen_content_l ul").empty();
            getBrand();
        }
    });
    //区域放大镜搜索
    $("#area_search_f").click(function(){
        $("#screen_area .screen_content_l").unbind("scroll");
        area_num=1;
        $("#screen_area .screen_content_l ul").empty();
        getArea(area_num);
        console.log(3)
    });
    //品牌放大镜搜索
    $("#brand_search_f").click(function(){
        $("#screen_brand .screen_content_l ul").empty();
        getBrand();
    });
    $("#screen_que_area").click(function(){
        var li=$(this).prev().children(".screen_content_r").find("ul li");
        var hasli=$("#OWN_AREA_All p");
        var a=$('#OWN_AREA_All input');
        if((li.length+hasli.length)>20){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                zIndex:10003,
                content: "所选区域不能超过20个"
            });
            return;
        }
        if(li.length>0){
            for(var i=0;i<li.length;i++){
                for(var j=0;j<a.length;j++){
                    if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
                        $(a[j]).parent("p").remove();
                    }
                }
                $('#OWN_AREA_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id' style='margin-left: 3px'>删除</span></p>");
            }
        }
        $("#screen_area").hide();
        whir.loading.remove("mask");
    });
    $("#screen_que_brand").click(function(){
        var li=$(this).prev().children(".screen_content_r").find("ul li");
        var hasli=$("#OWN_BRAND_All p");
        var a=$('#OWN_BRAND_All input');
        if((li.length+hasli.length)>10){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                zIndex:10003,
                content: "所选品牌不能超过10个"
            });
            return;
        }
        if(li.length>0){
            for(var i=0;i<li.length;i++){
                for(var j=0;j<a.length;j++){
                    if($(a[j]).attr("data-code")==$(li[i]).attr("id")){
                        $(a[j]).parent("p").remove();
                    }
                }
                $('#OWN_BRAND_All').append("<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='"+$(li[i]).attr("id")+"'  value='"+$(li[i]).find("span").html()+"'><span class='power remove_app_id' style='margin-left: 3px;'>删除</span></p>");
            }
        }
        whir.loading.remove("mask");
        $("#screen_brand").hide();
    });
    $(".xingming").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0.3)",cursoropacitymin:"0",boxzoom:false,autohidemode:true});
    //删除
    $(".xingming").on("click",".remove_app_id",function(){
        $(this).parent().remove();
    });
    var init=function(){
        var id=sessionStorage.getItem("id");
        vipCardTypeJs.bindbutton();
        var val = $(document).attr("title");
        if(val == '渠道二维码-新增'){
            cache.type = '新增'
            allCorp();
        }else if(val =='渠道二维码-编辑'){
            cache.type = '编辑'
            vipCardTypeJs.getInputValue(sessionStorage.getItem("id"))
        }
        //if(id==null){
        //    var corp_code="";
        //    vipCardTypeJs.getcorplist(corp_code);
        //}else if(id!==null){
        //    // vipCardTypeJs.getInputValue(id);
        //}
        //vipCardTypeJs.getInputValue(id);
    };
    var obj = {};
    obj.vipCardTypeJs = vipCardTypeJs;
    obj.init = init;
    return obj;
}));
$(function(){
    window.vipCardType.init();//初始化
});
//生成渠道二维码
var oc = new ObjectControl();
$('#theShow').click(function () {
    whir.loading.add("",0.5);//加载等待框
    var params={};
    params["id"]=sessionStorage.getItem("id");
    var qrcode_type=$("#vipCodeType").val().trim();//二维码类型
    if(qrcode_type=='材料类'){
        qrcode_type = 'material';
    }else if(qrcode_type=='印刷类'){
        qrcode_type = 'print';
    }else if(qrcode_type=='礼品类'){
        qrcode_type = 'gift';
    }
    params["qrcode_type"]=qrcode_type;
    getCode(params)
});
function getCode(params){
    oc.postRequire("post","/qrCode/creatQrcode","", params, function(data){
        if(data.code=="0"){
            $('#theShow').hide();
            var msg = JSON.parse(data.message);
            var src = msg.qrcode;
            $('#vipCodeShow img').attr('src',src);
            $('#vipCodeShow img').show();
        }else if(data.code=="-1"){
            art.dialog({
                time: 2,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove();//移除加载框
    })
}
//所有企业
function allCorp() {
    var param = {};
    oc.postRequire("post", "/user/getCorpByUser", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var corps = msg.corps;
            var corp_html='';
            for(var i=0;i<corps.length;i++){
                corp_html+='<option value="'+corps[i].corp_code +'">'+corps[i].corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.corp_select .searchable-select-input').keydown(function (event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    $('#vipCodeName').val('');
                    $('#vipCode').val('');
                    $('#vipCode').attr('placeholder','请选择公众号');
                    $('#vipCodeType').val('请选择类型');
                    $('#vipCodeDate').val('请选择时效');
                    $('#vipCodeNote').val('');
                    var val = $('.searchable-select-holder').text();
                    $('.searchable-select-item').each(function () {
                        if($(this).text()==val){
                            var corp_code =  $(this).attr('data-value');
                            cache.corp_code = corp_code;
                            getcorplist(corp_code);
                        }
                    });
                }
            })
            $('.searchable-select-holder').text('请选择所属企业');
            $('.searchable-select-item').click(function () {
                $('#vipCodeName').val('');
                $('#vipCode').val('');
                $('#vipCode').attr('placeholder','请选择公众号');
                $('#vipCodeType').val('请选择类型');
                $('#vipCodeDate').val('请选择时效');
                $('#vipCodeNote').val('');
                var corp_code = $(this).attr('data-value');
                cache.corp_code = corp_code;
                getcorplist(corp_code);
            })
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//corpcode查找corp_name
function findCropName(corp_code) {
    var param = {};
    oc.postRequire("post", "/user/getCorpByUser", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var corps = msg.corps;
           for(i=0;i<corps.length;i++){
               if(corps[i].corp_code == corp_code){
                   $('#corpCopy').val(corps[i].corp_name);
               }
           }
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//获取公众号
function getcorplist(corp_code){
    whir.loading.add("",0.5);
    var param={};
    param["corp_code"]=corp_code;
    oc.postRequire("post","/corp/selectWx","0",param, function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var list = msg.list;
            var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-value="${msg}">${msg}</li>';
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
                $('.input_select').eq(0).val($(this).text());
                $('.input_select').eq(0).attr('app_id',$(this).attr('data-id'));
                $('.input_select').eq(0).attr('app_user_name',$(this).attr('data-username'));
            });
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
var cache = {
    corp_code:'',
    corp :'',
    type:''
};
