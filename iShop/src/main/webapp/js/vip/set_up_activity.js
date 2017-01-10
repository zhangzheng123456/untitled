var oc = new ObjectControl();
var activity={
    isEmpty:false,
    shop_num:1,
    shop_next:false,
    area_num:1,
    area_next:false,
    isscroll:false,
    swicth:true,//优惠券开关标志
    cache:{//缓存变量
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "card_type":"",
        "coupon":""
    },
    init:function () {
        this.activityEdit();
        this.selectClick();
        this.activityType();
        this.addLine();
        this.getcorplist();
        this.chooseShop();
        this.uploadOSS();
        this.getNowFormatDate();
        this.testTheme();
        this.createCode();
    },
    selectClick:function () {//input下拉模拟
        $(".setUp_activity_details").on("click",".select_input",function () {
            $(this).nextAll("ul").toggle();
        });
        $(".setUp_activity_details").on("click","i",function () {
            $(this).nextAll("ul").toggle();
        });
        $(".setUp_activity_details").on("click"," .activity_select li",function () {
            var vue = $(this).html();
            var id = $(this).attr("data-id");
            var couponCode = $(this).attr("data-code");
            if(couponCode!==""||couponCode!==undefined){
                $(this).parent().prevAll("input").attr("data-code",couponCode);
            }
            $(this).parent().prevAll("input").val(vue);
            $(this).parent().prevAll("input").attr("data-id",id);
        });
        $(".setUp_activity_details").on("blur",".select_input",function () {
            var ul = $(this).nextAll("ul");
            setTimeout(function () {
                ul.hide();
            },200);
        });
        $(".screen_content").on("click", "li", function () {
            var input = $(this).find("input")[0];
            if (input.type == "checkbox" && input.checked == false) {
                input.checked = true;
            } else if (input.type == "checkbox" && input.checked == true) {
                input.checked = false;
            }
        });
    },
    activityType:function () {//活动切换
        $("#activityType_select").on("click","li",function () {
            var index=$(this).index();
            $("#setUp_activityType").children("div").eq(index).show();
            $("#setUp_activityType").children("div").eq(index).siblings("div").hide();
        });
        $("#coupon_title li").click(function () {//优惠券切换
            if(activity.swicth){
                var index=$(this).index();
                $("#coupon_activity>div").eq(index).show();
                $("#coupon_activity>div").eq(index).siblings("div:not('.switch')").hide();
                $(this).addClass("coupon_active");
                $(this).siblings("li").removeClass("coupon_active");
            }
        });
        $(".switch div").click(function(){//优惠券开关
            if($(this).attr("class")=="bg"){
                activity.swicth=false;
                $("#coupon_activity>div:not('.switch')").hide();
            }else {
                activity.swicth=true;
                var index=$(".coupon_active").index();
                $("#coupon_activity>div").eq(index).show();
            }
            $(this).toggleClass("bg");
            $(this).find("span").toggleClass("Off");
        });
    },
    addLine:function () {
        $("#recruit_activity .operate_ul").on("click",".add_recruit",function () {//招募活动新增
            var clone=$(this).parent().parents("li").clone();
            $(this).parent().hide();
            $("#recruit_activity .operate_ul").append(clone);
        });
        $("#recruit_activity .operate_ul").on("click",".remove_recruit",function () {//招募活动移出
            $(this).parent().parents("li").prev('li').find("li:last-child").show();
            $(this).parent().parents("li").remove();
        });
        $("#coupon_activity").on("click",".add_btn",function () {//优惠券新增新增
            var clone=$(this).parent().parents("li").clone();
            $(this).parent().hide();
            $(this).parents(".operate_ul").append(clone);
        });
        $("#coupon_activity").on("click",".remove_btn",function () {//优惠券移出
            $(this).parent().parents("li").prev('li').find("li:last-child").show();
            $(this).parent().parents("li").remove();
        });
        $("#coupon_btn").click(function () {//新增开卡送券
            var html='<div class="coupon_details_wrap"><ul><li style="margin-right: 5px"><label>卡类型</label><input class="text_input select_input" data-code type="text" readonly="readonly"><i class="icon-ishop_8-02"></i>'
                + '<ul class="activity_select vipCardType">'
                + activity.cache.card_type
                + '</ul></li></ul><ul class="operate_ul">'
                + '<li><ul><li><label>选择优惠券</label><input class="text_input select_input" data-code= type="text" readonly="readonly"><i class="icon-ishop_8-02"></i>'
                + '<ul class="activity_select coupon_activity">'
                + activity.cache.coupon
                + '</ul></li><li><span class="add_btn">+</span><span class="remove_btn">-</span></li></ul>'
                + '</ul><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#coupon_btn").parent().append(html);
        });
        $("#coupon_activity").on("click",".coupon_details_close",function () {
            $(this).parents(".coupon_details_wrap").remove();
        });
    },
    uploadOSS:function () {//上传logo OSS
        var _this=this;
        var client = new OSS.Wrapper({
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            bucket: 'products-image'
        });
        document.getElementById('upload_logo').addEventListener('change', function (e) {
            // whir.loading.add("上传中,请稍后...",0.5);
            var file = e.target.files[0];
            var time=_this.getNowFormatDate();
            var corp_code=$("#OWN_CORP").val();
            var storeAs='Album/Vip/iShow/'+corp_code+'_'+'_'+time+'.jpg';
            client.multipartUpload(storeAs, file).then(function (result) {
                var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
                // $("#upload_logo").val("");
                _this.addLogo(url);
            }).catch(function (err) {
                console.log(err);
            });
        });
    },
    getNowFormatDate:function () {//获取时间戳
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        var H=date.getHours();
        var M=date.getMinutes();
        var S=date.getSeconds();
        var m=date.getMilliseconds();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = ""+year+month+strDate+H+M+S+m;
        return currentdate
    },
    addLogo:function (url) {//添加logo节点
        var img="<img src='"+url+"' alt='暂无图片'>";
        $("#upload_logo").val(url);
        var len = $("#upload_logo").parent().prevAll("img").length;
        if(len == 0){
            $("#upload_logo").parent().before(img);
        }else {
            $("#upload_logo").parent().prev("img").replaceWith(img);
        }
        // whir.loading.remove();
    },
    chooseShop:function () {//选择店铺操作
        $("#choose_shop").click(function () {//店铺弹窗
            if(activity.cache.store_codes!==""){
                var store_codes=activity.cache.store_codes.split(',');
                var store_names=activity.cache.store_names.split(',');
                var shop_html_right="";
                for(var h=0;h<store_codes.length;h++){
                    shop_html_right+="<li id='"+store_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+store_names[h]+"</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            }else{
                $("#screen_shop .s_pitch span").html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            var shop_num=1;
            activity.isscroll=false;
            var arr=whir.loading.getPageSize();
            $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
            $("#screen_shop").show();
            $("#p").show();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            activity.getstorelist(shop_num);
        });
        $("#screen_close_shop").click(function(){//店铺关闭
            $("#screen_shop").hide();
            $("#p").hide();
        });
        $("#store_search").keydown(function(){//店铺搜索
            var event=window.event||arguments[0];
            activity.shop_num=1;
            if(event.keyCode==13){
                activity.isscroll=false;
                $("#screen_shop .screen_content_l ul").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                activity.getstorelist(activity.shop_num);
            }
        });
        $("#store_search_f").click(function(){//店铺放大镜
            activity.shop_num=1;
            activity.isscroll=false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            activity.getstorelist(activity.shop_num);
        });
        $("#shop_brand").click(function(){
            if(activity.cache.brand_codes!==""){
                var brand_codes=activity.cache.brand_codes.split(',');
                var brand_names=activity.cache.brand_names.split(',');
                var brand_html_right="";
                for(var h=0;h<brand_codes.length;h++){
                    brand_html_right+="<li id='"+brand_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+brand_names[h]+"</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            }else{
                $("#screen_brand .s_pitch span").html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            var arr=whir.loading.getPageSize();
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_shop").hide();
            activity.getbrandlist();
        });
        $("#screen_close_brand").click(function(){//品牌关闭
            $("#screen_brand").hide();
            $("#screen_shop").show();
        });
        $("#brand_search").keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode==13){
                $("#screen_brand .screen_content_l ul").empty();
                activity.getbrandlist();
            }
        });
        $("#brand_search_f").click(function(){
            $("#screen_brand .screen_content_l ul").empty();
            activity.getbrandlist();
        });
        $("#shop_area").click(function(){//区域弹窗
            if(activity.cache.area_codes!==""){
                var area_codes=activity.cache.area_codes.split(',');
                var area_names=activity.cache.area_names.split(',');
                var area_html_right="";
                for(var h=0;h<area_codes.length;h++){
                    area_html_right+="<li id='"+area_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+area_names[h]+"</span>\
            \</li>"
                }
                $("#screen_area .s_pitch span").html(h);
                $("#screen_area .screen_content_r ul").html(area_html_right);
            }else{
                $("#screen_area .s_pitch span").html("0");
                $("#screen_area .screen_content_r ul").empty();
            }
            activity.isscroll=false;
            activity.area_num=1;
            var arr=whir.loading.getPageSize();
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").show();
            $("#screen_shop").hide();
            activity.getarealist(activity.area_num);
        });
        $("#screen_close_area").click(function(){
            $("#screen_area").hide();
            $("#screen_shop").show();
        });
        $("#area_search").keydown(function(){
            var event=window.event||arguments[0];
            activity.area_num=1;
            if(event.keyCode == 13){
                activity.isscroll=false;
                $("#screen_area .screen_content_l").unbind("scroll");
                $("#screen_area .screen_content_l ul").empty();
                activity.getarealist(activity.area_num);
            }
        });
        $("#area_search_f").click(function(){
            activity.area_num=1;
            activity.isscroll=false;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            activity.getarealist(activity.area_num);
        });
        //左右移
        $(".shift_right").click(function () {
            var right = "only";
            var div = $(this);
            activity.removeRight(right, div);
        });
        $(".shift_right_all").click(function () {
            var right = "all";
            var div = $(this);
            activity.removeRight(right, div);
        });
        $(".shift_left").click(function () {
            var left = "only";
            var div = $(this);
            activity.removeLeft(left, div);
        });
        $(".shift_left_all").click(function () {
            var left = "all";
            var div = $(this);
            activity.removeLeft(left, div);
        });
        //点击区域确定按钮
        $("#screen_que_area").click(function(){
            var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            var area_codes="";
            var area_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    area_codes+=r+",";
                    area_names+=p+",";
                }else{
                    area_codes+=r;
                    area_names+=p;
                }
            };
            activity.cache.area_codes=area_codes;
            activity.cache.area_names=area_names;
            activity.shop_num=1;
            activity.isscroll=false;
            $("#screen_area").hide();
            $("#screen_shop").show();
            $("#area_num").val("已选"+li.length+"个");
            $("#area_num").attr("data-areacode",area_codes);
            $("#screen_shop .screen_content_l ul").empty();
            activity.getstorelist(activity.shop_num);
        });
        //点击品牌确定按钮
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
            activity.cache.brand_codes=brand_codes;
            activity.cache.brand_names=brand_names;
            activity.shop_num=1;
            activity.isscroll=false;
            $("#screen_brand").hide();
            $("#screen_shop").show();
            $("#brand_num").val("已选"+li.length+"个");
            $("#brand_num").attr("data-code",brand_codes);
            $("#screen_shop .screen_content_l ul").empty();
            activity.getstorelist(activity.shop_num);
        });
        //点击店铺确定按钮
        $("#screen_que_shop").click(function(){
            var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            var store_codes="";
            var store_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    store_codes+=r+",";
                    store_names+=p+",";
                }else{
                    store_codes+=r;
                    store_names+=p;
                }
            };
            activity.cache.store_codes=store_codes;
            activity.cache.store_names=store_names;
            $("#screen_shop").hide();
            $("#p").hide();
            $("#shop_amount").html(li.length);
        });
    },
    removeRight:function (a,b) {
        var li = "";
        if (a == "only") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
        }
        if (a == "all") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
        }
        if (li.length == "0") {
            art.dialog({
                zIndex: 10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "请先选择"
            });
            return;
        }
        if (li.length > 0) {
            for (var i = 0; i < li.length; i++) {
                var html = $(li[i]).html();
                var id = $(li[i]).find("input[type='checkbox']").val();
                $(li[i]).find("input[type='checkbox']")[0].checked = true;
                var input = $(b).parents(".screen_content").find(".screen_content_r li");
                for (var j = 0; j < input.length; j++) {
                    if ($(input[j]).attr("id") == id) {
                        $(input[j]).remove();
                    }
                }
                $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>");
                $(b).parents(".screen_content").find(".screen_content_r input[value='" + id + "']").removeAttr("checked");
            }
        }
        var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
        $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
        $("#screen_staff .screen_content_l li:odd").css("backgroundColor", "#fff");
        $("#screen_staff .screen_content_l li:even").css("backgroundColor", "#ededed");
        $("#screen_staff .screen_content_r li:odd").css("backgroundColor", "#fff");
        $("#screen_staff .screen_content_r li:even").css("backgroundColor", "#ededed");
    },
    removeLeft:function (a,b) {
        var li = "";
        if (a == "only") {
            li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
        }
        if (a == "all") {
            li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
        }
        if (li.length == "0") {
            art.dialog({
                zIndex: 10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "请先选择"
            });
            return;
        }
        if (li.length > 0) {
            for (var i = li.length - 1; i >= 0; i--) {
                $(li[i]).remove();
                $(b).parents(".screen_content").find(".screen_content_l input[value='" + $(li[i]).attr("id") + "']").removeAttr("checked");
            }
        }
        var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
        $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    },
    getcorplist:function (a) {
        oc.postRequire("post", "/user/getCorpByUser", "", "", function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var corp_html = '';
                for (var i=0;i<msg.corps.length;i++) {
                    corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
                }
                $("#OWN_CORP").append(corp_html);
                if (a !== "") {
                    $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
                }
                $('.corp_select select').searchableSelect();
                $('.corp_select .searchable-select-input').keydown(function (event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        var corp=$("#OWN_CORP").val();
                        $("#tabs>div:first-child").attr("data-corp",corp);
                        activity.getCoupon();
                    }
                });
                $('.searchable-select-item').click(function () {
                    var corp=$("#OWN_CORP").val();
                    $("#tabs>div:first-child").attr("data-corp",corp);
                    activity.getCoupon();
                });
                var corp=$("#OWN_CORP").val();
                $("#tabs>div:first-child").attr("data-corp",corp);
                activity.getCoupon();
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    },
    getstorelist:function (a) {//店铺接口
        var searchValue=$("#store_search").val();
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        _param['corp_code']=$("#OWN_CORP").val();
        _param['area_code']=activity.cache.area_codes;
        _param['brand_code']=activity.cache.brand_codes;
        _param['searchValue']=searchValue;
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var store_html = '';
                if (list.length == 0){

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
                        }
                    }
                }
                if(hasNextPage==true){
                    activity.shop_num++;
                    activity.shop_next=false;
                }
                if(hasNextPage==false){
                    activity.shop_next=true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if(!activity.isscroll){
                    $("#screen_shop .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(activity.shop_next){
                                return;
                            }
                            activity.getstorelist(activity.shop_num);
                        }
                    })
                }
                activity.isscroll=true;
                var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    getarealist:function (a) {//区域接口
        var searchValue=$("#area_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param = {};
        _param['corp_code']=$("#OWN_CORP").val();
        _param["searchValue"]=searchValue;
        _param["pageSize"]=pageSize;
        _param["pageNumber"]=pageNumber;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", "/area/selAreaByCorpCode", "", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var area_html_left ='';
                if (list.length == 0) {

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
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
                    activity.area_num++;
                    activity.area_next=false;
                }
                if(hasNextPage==false){
                    activity.area_next=true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if(!activity.isscroll){
                    $("#screen_area .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(activity.area_next){
                                return;
                            }
                            getarealist(activity.area_num);
                        }
                    })
                }
                activity.isscroll=true;
                var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    getbrandlist:function () {
        var searchValue=$("#brand_search").val();
        var _param={};
        _param['corp_code']=$("#OWN_CORP").val();
        _param["searchValue"]=searchValue;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/brand", "",_param, function(data){
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=message.brands;
                var brand_html_left = '';
                if (list.length == 0){
                    for(var h=0;h<9;h++){
                        brand_html_left+="<li></li>"
                    }
                } else {
                    if(list.length<9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                        for(var j=0;j<9-list.length;j++){
                            brand_html_left+="<li></li>"
                        }
                    }else if(list.length>=9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                    }
                }
                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    testTheme:function () {
        $("#activity_theme").blur(function () {
            var theme=$("#activity_theme").val();
            if(theme==""){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "活动主题不能为空"
                });
            }else {
                var param={};
                param["corp_code"]=$("#OWN_CORP").val();
                param["activity_theme"]=theme;
                param["activity_code"]="";
                oc.postRequire("post","/vipActivity/activityThemeExist","0",param,function (data) {
                        if(data.code=="-1"){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: data.message
                            });
                        }
                });
            }
        });
    },
    createCode:function () {//生成二维码
        $("#create_code").click(function () {
            $(".offline_right_wrap").empty().append("<img style='width:100%' src='http://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQGz8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyNlAzQ29HMTY5NFUxMDAwMGcwN0IAAgTNAnNYAwQAAAAA'>");
        });
    },
    getCoupon:function () {
        var corp_code=$("#OWN_CORP").val();
        var param={"corp_code":corp_code};
        oc.postRequire("post","/vipRules/getCoupon","0",param,function (data) {
            if(data.code==0){
                var li="";
                var msg=JSON.parse(data.message);
                if(msg.length==0&&$("#coupon_activity").css("display")=="block"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有优惠券"
                    });
                    activity.cache.coupon="";
                }else if(msg.length>0){
                    for(var i=0;i<msg.length;i++){
                        li+="<li data-code='"+msg[i].couponcode+"'>"+msg[i].name+"</li>"
                    }
                    activity.cache.coupon=li;
                }
                $(".coupon_activity").empty();
                $(".coupon_activity").append(li);
            }
        });
        oc.postRequire("post","/vipCardType/getVipCardTypes","0",param,function (data) {
            if(data.code==0){
                var li="";
                var message=JSON.parse(data.message);
                var msg=JSON.parse(message.list);
                if(msg.length==0&&$("#coupon_activity").css("display")=="block"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有卡类型"
                    });
                    activity.cache.card_type="";
                }else if(msg.length>0){
                    for(var i=0;i<msg.length;i++){
                        li+="<li data-code='"+msg[i].vip_card_type_code+"'>"+msg[i].vip_card_type_name+"</li>"
                    }
                    activity.cache.card_type=li;
                }
                $(".vipCardType").empty();
                $(".vipCardType").append(li);
            }
        });
    },
    checkEmpty:function () {
        $("#setUp_activityType>div").each(function () {
            var id=$(this).attr("id");
            if($(this).css("display")!=="none"&&id!=="coupon_activity"){
                var vue=$(this).find(".text_input");
                $(vue).each(function (i) {
                    if($(vue[i]).val()==""){
                        activity.isEmpty=true;
                    }
                });
            }else if($(this).css("display")!=="none"&&id=="coupon_activity"){
                $(this).children("div:not('.switch')").each(function (i,e) {
                    if($(e).css("display")=="block"){
                        var vue=$(e).find(".text_input");
                        $(vue).each(function (i) {
                            if($(vue[i]).val()==""){
                                activity.isEmpty=true;
                            }
                        });
                    }
                });
            }
        });
        $("#activity_basic .text_input").each(function () {
            var vue=$(this).val();
            if(vue==""){
                activity.isEmpty=true;
            }
        });
    },
    add:function () {
        var corp_code=$("#tabs>div:first-child").attr("data-corp");
        var activity_code=$("#tabs>div:first-child").attr("data-code");
        var param={};
        var runMode=$("#activity_type").attr("data-id");
        if(activity_code!==""&&activity_code!==undefined){
            param["activity_code"]=activity_code;
        }else {
            param["activity_code"]="";
        }
        param["corp_code"]=corp_code;
        param["activity_theme"]=$("#activity_theme").val();
        param["run_mode"]=runMode;
        param["start_time"]=$("#activity_start").val();
        param["end_time"]=$("#activity_end").val();
        param["activity_desc"]=$("#activity_describe").val();
        param["activity_store_code"]=activity.cache.store_codes;
        oc.postRequire("post","/vipActivity/add","0",param,function (data) {
            if(data.code==0){
                var msg=data.message;
                $("#tabs>div:first-child").attr("data-code",msg);
                var _param={};
                _param["corp_code"]=$("#tabs>div:first-child").attr("data-corp");
                _param["activity_code"]="";
                _param["run_mode"]=runMode;
                _param["activity_type"]=runMode;
                if(runMode=="recruit"){
                    var recruit=[];
                    var line=$("#recruit_activity>ul>li");
                    for (var i=0;i<line.length;i++){
                        var input=$(line[i]).find("input");
                        var obj={};
                        obj["vip_card_type_code"]=$(input[0]).attr("data-code");
                        obj["join_threshold"]=$(input[1]).val();
                        recruit.push(obj);
                    }
                    _param["recruit"]=recruit;
                }
                if(runMode=="h5"){
                    _param["h5_url"]=$("#h5_activity").find("input").val();
                }
                if(runMode=="sales"){
                    _param["sales_no"]=$("#sales_activity").find("input").val();
                }
                if(runMode=="festival"){
                    _param["festival_start"]=$($("#festival_activity").find("input")[0]).val();
                    _param["festival_end"]=$($("#festival_activity").find("input")[1]).val();
                }
                if(runMode=="coupon"){
                    var send_coupon_type=$("#coupon_title .coupon_active").attr("data-id");
                    _param["send_coupon_type"]=send_coupon_type;
                    if(send_coupon_type!=="card"){
                        $("#coupon_activity .choose_coupon").each(function () {
                            if($(this).css("display")=="block"){
                                var input=$(this).find("input");
                                var coupon_type="";
                                for(var i=0;i<input.length;i++){
                                    if(i<input.length-1){
                                        coupon_type+=$(input[i])+","
                                    }else {
                                        coupon_type+=$(input[i])
                                    }
                                }
                                _param["coupon_type"]=coupon_type;
                            }
                        })
                    }else if(send_coupon_type=="card"){
                        var coupon_type=[];
                        $(".coupon_details_wrap").each(function () {
                            var obj={};
                            var vip_card_type_code=$($(this).find("input")[0]).attr("data-code");
                            var input=$(this).children("operate_ul").find("input");
                            var coupon_code=""
                            for(var i=0;i<input.length;i++){
                                if(i<input.length-1){
                                    coupon_code+=$(input[i]).attr("data-code")+",";
                                }else {
                                    coupon_code+=$(input[i]).attr("data-code");
                                }
                            }
                            obj['vip_card_type_code']=vip_card_type_code;
                            obj['coupon_code']=coupon_code;
                            coupon_type.push(obj);
                        });
                        _param["coupon_type"]=coupon_type;
                    }
                }
                if(runMode=="invite"){
                    _param['apply_title']=$("#invite_title").val();
                    _param['apply_endtime']=$("#offline_end").val();
                    _param['apply_desc']=$("#invite_summary").val();
                    _param['apply_success_tips']=$("#invite_message").val();
                    _param['apply_logo']=$("#upload_logo").val();
                    _param['apply_qrcode']="";
                }
                oc.postRequire("post","/vipActivity/detail/add","0",_param,function (data) {

                })
            }else if(data.code==-1){
                console.log(data.message);
            }
        });
    },
    activityEdit:function () {
        var corp_code=$("#tabs>div:first-child").attr("data-corp");
        var activity_code=$("#tabs>div:first-child").attr("data-code");
        if(activity_code!==""&&activity_code!==undefined){
            var param={
                "corp_code":corp_code,
                "activity_code":activity_code
            };
            oc.postRequire("post","/vipActivity/detail/select","0",param,function (data) {
                console.log(data.message);
            });
        }
    }
};
$(function () {
    activity.init();
});
