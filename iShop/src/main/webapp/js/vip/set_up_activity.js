var oc = new ObjectControl();
var _param = {};//筛选会员存值
var staff_num = 1;
var staff_next = false;
var isscroll = false;
var activity = {
    isEmpty: false,//input为空判断
    label: "",
    theme: "",//活动标题唯一性判断
    shop_num: 1,
    shopperNum: 1,//商品页码
    goodsNext: false,//商品是否有下一页
    chooseCount: 0,//已选商品数量
    goodsWrap: "",//当前选择商品的容器
    shop_next: false,
    area_num: 1,
    area_next: false,
    isscroll: false,
    swicth: true,//优惠券开关标志
    activity_code: "",
    popUp: "",//选择会员和选择范围标志
    edit_flag:false,//审核状态下，不可编辑
    cache: {//缓存变量
        "corp_code": "",
        "area_codes": "",
        "area_names": "",
        "brand_codes": "",
        "brand_names": "",
        "store_codes": "",
        "store_names": "",
        "card_type": "",
        "coupon": "",
        "souvenir_type": "",
    },
    vipcache: {
        clickMark: "",//员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
        "area_codes": "",
        "area_names": "",
        "brand_codes": "",
        "brand_names": "",
        "store_codes": "",
        "store_names": "",
        "user_codes": "",
        "user_names": "",
        "group_codes": "",
        "group_name": ""
    },
    init: function () {
        activity.activity_code = sessionStorage.getItem("activity_code");
        if (activity.activity_code !== null) {
            this.activityEdit();
        } else {
            activity.activity_code = "";
            this.getcorplist();
        }
        this.selectClick();
        this.activityType();
        this.addLine();
        this.chooseShop();
        this.uploadOSS();
        this.getNowFormatDate();
        this.testTheme();
        this.createCode();
        this.filterEvent();
    },
    selectClick: function () {//input下拉模拟
        $(".setUp_activity").on("click", ".select_input,.checkbox_select_input", function () {
            $(this).nextAll("ul").toggle();
            $(this).nextAll("ul").find("li").show();
            $(this).parent("div").siblings().find(".shopperSelect").hide();
            var li = $(this).nextAll(".activity_select").children();
            var label = $(this).prev("label").html();
            if($(this).hasClass("online_choose_shopper")){
                $("#onlineChooseShopper").show();
                if($(this).attr("data-mark") == "true") {
                    return
                }
                var param = {};
                param.corp_code = $("#OWN_CORP").val();
                param.searchValue = "";
                activity.goodsNext = false;
                activity.shopperNum = 1;
                activity.goodsWrap = $(this);
                $(this).attr("data-mark","true");
                $(this).parents("li").siblings("li").find(".online_choose_shopper").removeAttr("data-mark");
                $("#onlineChooseShopper tbody").empty();
                $("#onlineShopperCondition input[type='text']").each(function () {
                    if ($(this).hasClass("checkbox_select_input")) {
                        $(this).val("全部");
                        $(this).attr("data-code", "");
                    } else {
                        $(this).val("");
                    }
                });
                activity.getGoodsType();
                activity.getshopperList("online");
                oc.postRequire("post", "/shop/brand", "", param, function (data) {
                    if (data.code == 0) {
                        var message = JSON.parse(data.message);
                        var list = message.brands;
                        var li = "";
                        for (var i = 0; i < list.length; i++) {
                            li += "<li data-code='" + list[i].brand_code + "' title='" + list[i].brand_name + "'><input type='checkbox'>" + list[i].brand_name + "</li>";
                        }
                        $("#onlineBrandWrap").html(li);
                    }
                });
                return
            }
            if (li.length < 1) {
                $(this).nextAll(".activity_select").css("border", "none");
                if (label == "卡类型*" || label == "招募级别*") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有卡类型"
                    });
                } else if (label == "选择优惠券*") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有优惠券"
                    });
                } else if (label.indexOf("公众号") > -1) {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有公众号"
                    });
                }
            } else {
                $(this).nextAll(".activity_select").css("border", "1px solid #d7d7d7");
            }
        });
        $(".setUp_activity").on("mousedown", ".activity_select li", function () {
            var vue = $(this).text();
            var id = $(this).attr("data-id");
            var name = $(this).attr("data-name");
            var require = $(this).attr("data-require");
            var type = $(this).attr("data-type");
            var param_type = $(this).attr("data-param_type");
            var param_value = $(this).attr("data-value");
            var attr = $(this).attr("data-attr");
            if ($(this).parent().hasClass("shopperSelect")) {
                return;
            } else if ($(this).parent().hasClass("vipCardType")) {
                var warps = $(this).parents(".coupon_details_wrap").siblings(".coupon_details_wrap");
                for (var i = 0; i < warps.length; i++) {
                    if ($(this).attr("data-code") == $($(warps[i]).find("input")[0]).attr("data-code")) {
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请勿选择重复的卡类型"
                        });
                        return;
                    }
                }
            } else if ($(this).parent().hasClass("levelSelect")) {
                var warps = $(this).parents(".coupon_details_wrap").siblings(".coupon_details_wrap");
                for (var i = 0; i < warps.length; i++) {
                    if ($(this).text() == $($(warps[i]).find("input")[0]).val()) {
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请勿选择重复优先级"
                        });
                        return;
                    }
                }
            } else if ($(this).parent().hasClass("souvenir_type")) {
                var warps = $(this).parents(".coupon_details_wrap").siblings(".coupon_details_wrap");
                for (var i = 0; i < warps.length; i++) {
                    if ($(this).text() == $($(warps[i]).find("input")[0]).val()) {
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请勿选择重复纪念日"
                        });
                        return;
                    }
                }
            }else if ($(this).parent().hasClass("SpreadWaySelect")) {
                if(vue == "自定义链接"){
                    setTimeout(function () {
                        $("#SpreadTitle").hide();
                        $("#SpreadImg").hide();
                    },300);
                }else {
                    setTimeout(function () {
                        $("#SpreadTitle").show();
                        $("#SpreadImg").show();
                    },300);
                }
            } else if($(this).parent().hasClass("online_itemType_select")){
                if(vue == "商品"){
                    $("#shopper_item").show();
                    $("#custom_item").hide();
                }else {
                    $("#shopper_item").hide();
                    $("#custom_item").show();
                }
            } else if($(this).parent().hasClass("td_allow")){
                vue == '是' ? $("#unsubscribe_date").parents("li").show() : $("#unsubscribe_date").parents("li").hide();
            } else if($(this).parent().hasClass("item_present_select")){
                if(vue == "积分"){
                    $(this).parents("dt").children("span").eq(1).html('<input class="text_input number_input" type="text"><span class="unit">积分</span>');
                }else if(vue == "金额(元)"){
                    $(this).parents("dt").children("span").eq(1).html('<input class="text_input number_input" type="text"><span class="unit">元</span>');
                }else {
                    $(this).parents("dt").children("span").eq(1).html('<span class="consumeWrap"><input type="text" class="text_input number_input"><span class="unit">元</span></span> 或 <span class="consumeWrap"><input type="text" class="text_input number_input"><span class="unit">积分</span></span>');
                }
            }
            name ? $(this).parent().prevAll("input").attr("data-name", name) : "";
            require != undefined ? $(this).parent().prevAll("input").attr("data-require", require) : "";
            type != undefined ? $(this).parent().prevAll("input").attr("data-type", type) : "";
            param_type != undefined ? $(this).parent().prevAll("input").attr("data-param_type", param_type) : "";
            param_value != undefined ? $(this).parent().prevAll("input").attr("data-value", param_value) : "";
            attr != undefined ? $(this).parent().prevAll("input").attr("data-attr", attr) : "";
            $(this).parent().prevAll("input").val(vue);
            $(this).parent().prevAll("input").attr("data-id", id);
            var couponCode = $(this).attr("data-code");
            if (couponCode !== "" || couponCode !== undefined) {
                $(this).parent().prevAll("input").attr("data-code", couponCode);
                $(this).parent().prevAll("input").attr("data-type", $(this).attr("data-type"));
            }
            if ($(this).parent().attr("id") == "wechat") {//公众号调接口
                var inputs = $("#coupon_activity").find("input:not('.option_input,.option_input_long,.notEmpty')");
                for (var j = 0; j < inputs.length; j++) {
                    $(inputs[j]).val("");
                    $(inputs[j]).attr("data-code", "");
                }
                activity.getCoupon();
            }
        });
        $(".setUp_activity").on("keyup",".presentCoupon_wrap .select_input,#recruit_activity .select_input,.cardTypeWrap .select_input",function () {
            var search = $(this).val().trim();
            $(this).next("ul").show();
            if(search != ''){
                $(this).next("ul").find("li").hide();
                $(this).next("ul").find("li:contains("+search+")").show();
            }else {
                $(this).next("ul").find("li").show();
            }
        });
        $("#chooseShopperWrap,#onlineShopperCondition").on("click", ".shopperSelect input", function () {
            var input = $(this).parents(".shopperSelect").find("li input:checked");
            var text = "";
            var code = "";
            for (var i = input.length - 1; i >= 0; i--) {
                if (i > 0) {
                    text += $(input[i]).parent("li").text() + ","
                    code += $(input[i]).parent("li").attr("data-code") + ","
                } else {
                    text += $(input[i]).parent("li").text();
                    code += $(input[i]).parent("li").attr("data-code");
                }
            }
            var textArray = text.split(",").reverse();
            var codeArray = code.split(",").reverse();
            text = textArray.join(",");
            code = codeArray.join(",");
            $(this).parents("ul").prev().val(text);
            $(this).parents("ul").prev().attr("data-code", code);
        });
        $(".setUp_activity").on("blur", ".select_input", function () {
            var ul = $(this).nextAll("ul");
            if(ul.hasClass("coupon_activity") || ul.hasClass("vipCardType")){
                $(this).val().trim() != $(this).attr("data-name") ? $(this).val($(this).attr("data-name")) : "";
            }
            setTimeout(function () {
                ul.hide();
            }, 200);
        });
        $(document).click(function (e) {
            if (!($(e.target).is(".shopperSelect") || $(e.target).is(".shopperSelect input") || $(e.target).is(".shopperSelect li") || $(e.target).is(".checkbox_select_input"))) {
                $(".shopperSelect").hide();
            }
        });
    },
    activityType: function () {
        //活动切换
        $("#activityType_select").on("click", "li", function () {
            var index = $(this).index();
            $("#setUp_activityType").children("div").eq(index).show();
            $("#setUp_activityType").children("div").eq(index).siblings("div").hide();
        });
        $("#coupon_title li").click(function () {//优惠券切换
            if (activity.swicth) {
                var index = $(this).index();
                $("#coupon_activity>div").eq(index).show();
                $("#coupon_activity>div").eq(index).siblings("div:not('.switch')").hide();
                $(this).addClass("coupon_active");
                $(this).siblings("li").removeClass("coupon_active");
            }
        });
        $("#url_mould").click(function () {//线下邀约切换
            $(this).addClass("invite_btn_background");
            $(this).next().removeClass();
            $("#extend_url").show();
            $("#invite_wrap").hide();
        });
        $("#invite_span").click(function () {//线下邀约切换
            $(this).addClass("invite_btn_background");
            $(this).prev().removeClass();
            $("#extend_url").hide();
            $("#invite_wrap").show();
        });
        $(".switch div").click(function () {//优惠券开关
            if ($(this).attr("class") == "bg") {
                activity.swicth = false;
                $("#coupon_activity>div:not('.switch')").hide();
                $("#coupon_activity .switch_text").html("优惠券已关闭");
            } else {
                activity.swicth = true;
                var index = $(".coupon_active").index();
                $("#coupon_activity>div").eq(index).show();
                $("#coupon_activity .switch_text").html("优惠券已开启");
            }
            $(this).toggleClass("bg");
            $(this).find("span").toggleClass("Off");
        });
        $(".setUp_activity_details").on("click", ".pointCheck", function () {//消费送券点击积分
            if ($(this).parents(".coupon_details_wrap").length != 0) {
                $(this).prop("checked") ? $(this).parents(".coupon_details_wrap").find(".presentPoint_wrap").show() : $(this).parents(".coupon_details_wrap").find(".presentPoint_wrap").hide();
            } else {
                $(this).prop("checked") ? $(this).parents(".choose_coupon").find(".presentPoint_wrap").show() : $(this).parents(".choose_coupon").find(".presentPoint_wrap").hide();
            }
        });
        $(".setUp_activity_details").on("click", ".couponCheck", function () {//消费送券点击优惠券
            if ($(this).parents(".coupon_details_wrap").length != 0) {
                $(this).prop("checked") ? $(this).parents(".coupon_details_wrap").find(".presentCoupon_wrap").show() : $(this).parents(".coupon_details_wrap").find(".presentCoupon_wrap").hide();
            } else {
                $(this).prop("checked") ? $(this).parents(".choose_coupon").find(".presentCoupon_wrap").show() : $(this).parents(".choose_coupon").find(".presentCoupon_wrap").hide();
            }
        });
        $("#chooseShopperSwitch").click(function () {//消费后送券-选择商品排除开关
            $(this).toggleClass("on");
            $(this).children().toggleClass("off");
        });
        $("#coupon_activity").on("click", ".consumeBtn", function () {//消费后送券选择商品弹窗
            activity.goodsWrap = $(this).parents(".coupon_details_wrap");
            activity.shopperNum = 1;
            $("#chooseShopperTable tbody").empty();
            $("#chooseShopperCondition input[type='text']").each(function () {
                if ($(this).hasClass("checkbox_select_input")) {
                    $(this).val("全部");
                    $(this).attr("data-code", "");
                } else {
                    $(this).val("");
                }
            });
            $("#chooseShopperSwitch").removeClass("on");
            $("#chooseShopperSwitch em").removeClass("off");
            var param = {};
            param.corp_code = $("#OWN_CORP").val();
            param.searchValue = "";
            oc.postRequire("post", "/shop/brand", "", param, function (data) {
                if (data.code == 0) {
                    var message = JSON.parse(data.message);
                    var list = message.brands;
                    var li = "";
                    for (var i = 0; i < list.length; i++) {
                        li += "<li data-code='" + list[i].brand_code + "' title='" + list[i].brand_name + "'><input type='checkbox'>" + list[i].brand_name + "</li>";
                    }
                    $("#brandCondition").empty();
                    $("#brandCondition").append(li);
                } else {
                    console.log(data.message);
                }
            });
            activity.getGoodsType();
            activity.getshopperList();
            $("#chooseShopperWrap").show();
        });
        $("#chooseShopEnter").click(function () {
            $("#chooseShopperTable tbody").empty();
            activity.goodsNext = false;
            activity.shopperNum = 1;
            activity.getshopperList();
            var arr = [];
            $("#chooseShopperCondition input[type='text']").each(function () {
                var obj = {};
                if ($(this).val() != "全部" && $(this).val() != "") {
                    obj.name = $(this).prev("label").text();
                    obj.value = $(this).val();
                    arr.push(obj)
                }
            });
            $(activity.goodsWrap).find(".lookCondition").attr("data-code", JSON.stringify(arr));
        });
        $("#coupon_activity").on("click", ".lookCondition", function () {//查看已选商品条件
            var div = "";
            if ($(this).attr("data-code") == undefined || $(this).attr("data-code") == "") {
                div = '<div style="text-align: center;line-height: 150px">暂无条件</div>'
            } else {
                var arr = JSON.parse($(this).attr("data-code"));
                for (var i = 0; i < arr.length; i++) {
                    div += '<div><span class="conditionName">' + arr[i].name + '</span><span>' + arr[i].value + '</span></div>'
                }
            }
            $("#choosedConditionWrap .choosedContent").html(div);
            $("#choosedConditionWrap").show();
        });
        $("#choosePartGoods").click(function () {
            var id = $("#chooseShopperTable tbody tr input:checked").map(function () {
                return $(this).parents("tr").attr("id")
            }).get().join(",");
            if (id == undefined || id == "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择商品"
                });
                return;
            }
            var ID = $(activity.goodsWrap).find(".consumeBtn").attr("data-id");
            ID.length == 0 ? ID = id : ID = ID + "," + id;
            var arr = ID.split(",");
            arr.sort();
            var re = [arr[0]];
            for (var i = 1; i < arr.length; i++) {
                if (arr[i] !== re[re.length - 1]) {
                    re.push(arr[i]);
                }
            }
            if (id.split(",").length > 100 || re.length > 100) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: '选择商品不能超过100个'
                });
                return;
            }
            id = re.toString();
            $(activity.goodsWrap).find(".consumeBtn").attr("data-id", id);
            $(activity.goodsWrap).find(".chooseGoodsCount").text(re.length);
            $("#chooseCount").text(re.length);
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: '添加成功'
            });
        });
        $(".goodsList").bind("scroll", function () {
            var nScrollHight = $(this)[0].scrollHeight;
            var nScrollTop = $(this)[0].scrollTop;
            var nDivHight = $(this).height();
            if (nScrollTop + nDivHight >= nScrollHight && nScrollHight !== 0 && nScrollTop != 0) {
                if($(this).attr("id") == 'onlineGood'){
                    activity.goodsNext ? activity.shopperNum++ && activity.getshopperList("online") : "";
                }else {
                    activity.goodsNext ? activity.shopperNum++ && activity.getshopperList() : "";
                }
            }
        });
        $("#deleteAllGoods").click(function () {//删除选中商品
            var skuCode = $(activity.goodsWrap).find(".consumeBtn").attr("data-id");
            var id = $("#chooseShopperTable tbody tr input:checked").map(function () {
                return $(this).parents("tr").attr("id")
            }).get().join(",");
            if (id == undefined || id == "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择商品"
                });
                return;
            }
            skuCode = skuCode.split(",");
            var arr = [];
            for (var i = 0; i < skuCode.length; i++) {
                if (id.indexOf(skuCode[i]) < 0) {
                    arr.push(skuCode[i]);
                }
            }
            $(activity.goodsWrap).find(".consumeBtn").attr("data-id", arr.toString());
            $(activity.goodsWrap).find(".chooseGoodsCount").text(arr.length);
            $("#chooseCount").text(arr.length);
            $("#ck_11").trigger("click");
        });
        $(".chooseShopperBtn").click(function () {//会员，范围，推广设置弹窗关闭
            if ($(this).attr("id") == "chooseShopEnter" || $(this).attr("id") == 'chooseShopperBtn') {
                return;
            }
            $(this).parents(".chooseShadow").hide();
        });
        $(".spreadBtn").click(function () {//推广设置弹窗
            $("#activitySpreadWrap").show();
        });
        $("#chooseShopperBtn").click(function () {//线上活动选商品搜索
            activity.goodsNext = false;
            activity.shopperNum = 1;
            $("#onlineChooseShopper tbody").empty();
            activity.getshopperList('online');
        });
        $("#onlineChooseShop").click(function () {
            $(activity.goodsWrap).val($('#onlineGood input:checked').parents("tr").children("td").eq(3).text());
            $(activity.goodsWrap).attr("data-code",$('#onlineGood input:checked').parents("tr").attr("id"));
            $("#onlineChooseShopper").hide();
        });
        $("#present_time input").change(function () {
           $(this).attr("id") == 'timelyCheck' ? $("#distribute_reward_date").hide() : $("#distribute_reward_date").show();
        });
        $("#chooseVips").mouseover(function () {//参与会员悬浮事件
            $(this).prev("ul").show();
        }).mouseleave(function () {
            $(this).prev("ul").hide();
        });
        $("#vipBtnWrap").mouseover(function () {
            $(this).show();
        }).mouseleave(function () {
            $(this).hide();
        });
        $('#exportVipBtn').click(function () {
            $("#exportVipWrap").show();
        });
        $("#online_activity .online_item_wrap").on("click","a",function () {
           var src = $(this).prev("span").find("input").attr("data-src");
           if(src != undefined && src != ''){
               whir.loading.add("",0.5,src);
           }else {
               art.dialog({
                   time: 1,
                   lock: true,
                   cancel: false,
                   content: "请先上传图片"
               });
           }
        });
        $("#online_activity").on("click","#reliefPreview",function () {
            if($("#reliefView").attr("data-src") == '' || $("#reliefView").attr("data-src") == undefined){
                $("#reliefView").show();
                return;
            }
            var src = $("#reliefBtn").attr("data-src");
            $.ajax({
                type: "GET",//请求方式
                url: src,//地址，就是json文件的请求路径
                dataType: "text",//数据类型可以为 text xml json  script  jsonp
                contentType: "application/text; charset=gbk",
                success: function(result){//返回的参数就是 action里面所有的有get和set方法的参数
                    $("#reliefView").show();
                    $("#reliefView textarea").val(result);
                }
            });
        });
        $("#reliefView").click(function (e) {
            if($(e.target).is("#reliefView")){
                $(this).hide();
            }

        });
    },
    addLine: function () {
        $("#recruit_activity .operate_ul").on("click", ".add_recruit", function () {//招募活动新增
            var clone = $(this).parent().parents("li").clone();
            $($(clone).find("input")[0]).val("");
            $($(clone).find("input")[1]).val("");
            $($(clone).find("input")[0]).attr("data-code", "");
            $($(clone).find("input")[0]).attr("data-name", "");
            $(this).hide();
            $(this).next("span").css("display", "inline-block");
            $(clone).find("li:last-child .remove_recruit").css("display", "inline-block");
            $("#recruit_activity .operate_ul").append(clone);
        });
        $("#recruit_activity .operate_ul").on("click", ".remove_recruit", function () {//招募活动移出
            if ($(this).parent().parents("li").next('li').length == 0) {//判断当前删除节点是最后一个
                if ($(this).parent().parents("li").index() == 1) {
                    $(this).parent().parents("li").prev('li').find("li:last-child .remove_recruit").hide();
                }
                $(this).parent().parents("li").prev('li').find("li:last-child .add_recruit").show();
                $(this).parent().parents("li").remove();
            } else {
                if ($(this).parent().parents(".operate_ul").children("li").length == 2) {
                    $(this).parent().parents("li").next('li').find("li:last-child .remove_recruit").hide();
                } else {
                    $(this).parent().parents("li").next('li').find("li:last-child .remove_recruit").css("display", "inline-block");
                }
                $(this).parent().parents("li").remove();
            }
        });
        $("#online_item_add").click(function () {//线上活动项目新增
            var r = new Date().getTime();
            if($("#online_item_type").val() == "商品"){
                var temp = '<dl><dt><input type="checkbox" id="itemCheck'+r+'"><label for="itemCheck'+r+'"></label></dt><dt><input class="text_input select_input online_choose_shopper" type="text" placeholder="请选择商品" readonly></dt><dt><input type="text" class="text_input number_input"></dt><dt>'
                    + '<span><input value="积分" class="text_input select_input" type="text" readonly="readonly"><ul class="activity_select item_present_select"><li>积分</li><li>金额(元)</li><li>金额或积分</li></ul></span><span><input class="text_input number_input" type="text"><span class="unit">积分</span></span></dt><dt><span><span class="chooseVipBtn">上传文件</span><input type="file"  accept="image/*"></span><a>预览<i class="icon-ishop_8-03"></i></a></dt></dl>';
                $("#online_shop_wrap").append(temp);
            }else {
                var temp = '<dl><dt><input type="checkbox" id="itemCheck'+r+'"><label for="itemCheck'+r+'"></label></dt><dt><input class="text_input" type="text" maxlength="200"></dt><dt><input type="text" class="text_input number_input"></dt><dt>'
                    + '<span><input value="积分" class="text_input select_input" type="text" readonly="readonly"><ul class="activity_select item_present_select"><li>积分</li><li>金额(元)</li><li>金额或积分</li></ul></span><span><input class="text_input number_input" type="text"><span class="unit">积分</span></span></dt><dt><span><span class="chooseVipBtn">上传文件</span><input type="file"  accept="image/*"></span><a>预览<i class="icon-ishop_8-03"></i></a></dt></dl>';
                $("#online_item_wrap").append(temp);
            }
        });
        $("#online_item_remove").click(function () {
            var item = $(".online_item_wrap:visible input[type=checkbox]:checked:not('#itemCheckHead,#itemCheckHeads')");
            if(item.length == 0){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择要删除的项目"
                });
            }else {
                $(item).parents("dl").remove();
            }
        });
        $("#setUp_activityType").on("click", ".add_btn", function () {//优惠券项目新增
            var clone = $(this).parent().parents("li").clone();
            var date_inputs = $(clone).find(".souvenir_content input");
            for (var i = 0; i < date_inputs.length; i++) {
                $(date_inputs[i]).val("1");
            }
            $($(clone).find("input")[0]).val("");
            $($(clone).find("input")[1]).val("");
            $($(clone).find("input")[2]).val("");
            $($(clone).find("input")[0]).attr("data-code", "");
            $($(clone).find("input")[0]).removeAttr("data-name");
            $($(clone).find("input")[0]).removeAttr("data-require");
            $($(clone).find("input")[0]).removeAttr("data-type");
            $($(clone).find("input")[0]).removeAttr("data-param_type");
            $($(clone).find("input")[0]).removeAttr("data-value");
            $($(clone).find("input")[0]).removeAttr("data-attr");
            $(this).hide();
            $(this).next("span").css("display", "inline-block");
            $(clone).find("li:last-child .remove_btn").css("display", "inline-block");
            $(this).parents(".operate_ul").find(".select_input").next("ul").hide();
            $(clone).find(".select_input").next("ul").hide();
            $(this).parents(".operate_ul").append(clone);
        });
        $("#setUp_activityType").on("click", ".remove_btn", function () {//优惠券移出
            if ($(this).parent().parents("li").next('li').length == 0) {//判断当前删除节点是最后一个
                if ($(this).parent().parents("li").index() == 1) {
                    $(this).parent().parents("li").prev('li').find("li:last-child .remove_btn").hide();
                }
                if($(this).parent().parents("li").prev('li').find("li:last-child .item_add_btn").length != 0){
                    $(this).parent().parents("li").prev('li').find("li:last-child .item_add_btn").show();
                }else {
                    $(this).parent().parents("li").prev('li').find("li:last-child .add_btn").show();
                }
                $(this).parent().parents("li").remove();
            } else {
                if ($(this).parent().parents(".operate_ul").children("li").length == 2) {
                    $(this).parent().parents("li").next('li').find("li:last-child .remove_btn").hide();
                } else {
                    $(this).parent().parents("li").next('li').find("li:last-child .remove_btn").css("display", "inline-block");
                }
                $(this).parent().parents("li").remove();
            }
        });
        $("#coupon_activity").on("click", "#coupon_btn", function () {//新增开卡送券
            var i = $("#cardContentWrap>.coupon_details_wrap").length;
            var r = new Date().getTime();
            var html = '<div class="coupon_details_wrap"><div class="cardTypeWrap"><ul><li style="margin-right: 5px"><label style="color:#c26555;">卡类型*</label><input class="text_input select_input" data-code="" type="text" placeholder="请选择卡类型">'
                + '<ul class="activity_select vipCardType">'
                + activity.cache.card_type
                + ' </ul></li></ul></div>'
                + '<div class="consumeCheckWrap"><label>开卡赠送</label><div class="checkbox1">'
                + '<span><input type="checkbox" checked="checked" class="check pointCheck" id="cardPointCheck' + r + '"><label for="cardPointCheck' + r + '"></label>积分</span><span><input type="checkbox" checked="checked" class="check couponCheck" id="cardCouponCheck' + r + '"><label for="cardCouponCheck' + r + '"></label>优惠券</span>'
                + '</div></div>'
                + '<div class="presentPoint_wrap"><label style="color:#c26555;">赠送积分*</label><input class="input130px text_input numberInput cardPointInput" type="text"></div>'
                + '<div class="presentCoupon_wrap"><ul class="operate_ul"><li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text"><ul class="activity_select coupon_activity">'
                + activity.cache.coupon
                + '</ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li></ul></li></ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号"></div></div>'
                + '<i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#cardContentWrap").append(html);
        });
        $("#coupon_activity").on("click", "#souvenirBtn", function () {//新增纪念日送券
            var i = $("#souvenirContentWrap>.coupon_details_wrap").length;
            var r = new Date().getTime();
            var html = '<div class="coupon_details_wrap"><div class="cardTypeWrap">'
                + '<ul><li><label style="color:#c26555;">纪念日类型*</label><input placeholder="请选择纪念日类型" class="text_input select_input" data-code="" type="text" readonly="readonly"><ul class="activity_select souvenir_type">'
                + activity.cache.souvenir_type
                + '</ul></li><li style="margin-left: 10px"><label style="color:#c26555;">执行时间*</label><input class="text_input select_input" placeholder="选择发券日期范围" type="text" readonly="readonly">'
                + '<ul class="activity_select"><li>当日</li><li>当月</li></ul></li>'
                + '</ul></div><div class="consumeCheckWrap"><label>开卡赠送</label><div class="checkbox1">'
                + '<span><input type="checkbox" checked="checked" class="check pointCheck" id="souvenirPointCheck' + r + '"><label for="souvenirPointCheck' + r + '"></label>积分</span><span><input type="checkbox" checked="checked" class="check couponCheck" id="souvenirCouponCheck' + r + '"><label for="souvenirCouponCheck' + r + '"></label>优惠券</span>'
                + '</div></div><div class="presentPoint_wrap"><label style="color:#c26555;">赠送积分*</label><input class="input130px text_input numberInput cardPointInput" type="text"></div>'
                + '<div class="presentCoupon_wrap"><ul class="operate_ul"><li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text"><ul class="activity_select coupon_activity">'
                + activity.cache.coupon
                + '</ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li>'
                + '</ul></li></ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号"></div></div><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#souvenirContentWrap").append(html);
        });
        $("#register").on("click", "#register_btn", function () {//新增注册
            var i = $("#registerContentWrap>.coupon_details_wrap").length + 1;
            var r = new Date().getTime();
            var html = '<div class="coupon_details_wrap"><div class="coupon_details_wrap_header">计划' + i + '</div>'
                + '<div><label style="color:#c26555;">人数区间*</label><input class="input50px numberInput text_input" type="text">~<input class="input50px numberInput text_input" type="text">人<label class="marginLeft100">每邀请</label><input class="input50px numberInput text_input" type="text">人</div>'
                + '<div><div class="coupon_details_wrap_left"><label>赠送</label></div>'
                + '<div class="coupon_details_wrap_right"><div class="giftWrap"><div><div class="consumeCheckWrap"><div class="checkbox1">'
                + '<span><input type="checkbox" checked="checked" class="check pointCheck" id="registerPointCheck' + r + '"><label for="registerPointCheck' + r + '"></label>积分</span><span><input type="checkbox" checked="checked" class="check couponCheck" id="registerCouponCheck' + r + '"><label for="registerCouponCheck' + r + '"></label>优惠券</span></div></div>'
                + '<div class="presentPoint_wrap"><label style="color:#c26555;">赠送积分*</label><input class="input130px text_input numberInput cardPointInput" type="text"></div>'
                + '<div class="presentCoupon_wrap"><ul class="operate_ul"><li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text"><ul class="activity_select coupon_activity">'
                + activity.cache.coupon
                + '</ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li></ul></li></ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号"></div></div></div></div></div></div>'
                + '<i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#registerContentWrap").append(html);
        });
        $("#setUp_activityType").on("click", ".coupon_details_close", function () {
            var len = $(this).parents(".coupon_details_wrap").siblings(".coupon_details_wrap").length;
            if (len == 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "至少保留一项"
                });
                return;
            }
            $(this).parents(".coupon_details_wrap").remove();
            if ($("#register").css("display") != "none") {
                $("#registerContentWrap .coupon_details_wrap").each(function (i, ele) {
                    $(ele).find(".coupon_details_wrap_header").text("计划" + (i + 1));
                });
            }
        });
        $("#sendCouponBtn").click(function () {//消费后送券-新增条件选项
            if ($("#consumeCouponWrap>div").length > 8) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "最多添加9个"
                });
                return;
            }
            var i = $("#consumeCouponWrap>div").length;
            var html = '<div class="coupon_details_wrap"><div class="ConsumerHeader"><span class="levels"><label style="color:#c26555;">优先级*</label><input class="text_input select_input" type="text" readonly="readonly"><ul class="activity_select levelSelect"><li>1</li><li>2</li><li>3</li><li>4</li><li>5</li><li>6</li><li>7</li><li>8</li><li>9</li></ul></span>'
                + '<div><label>&emsp;&emsp;商品</label><span class="consumeBtn" data-id="">选择商品</span><span class="lookCondition">查看条件<i class="icon-ishop_8-03"></i></span></div>'
                + '<div><input class="input50px numberInput" type="text"><label>≤ 整单金额(元) <</label><input class="input50px numberInput" type="text">元<br /><input class="input50px numberInput" type="text"><label>≤ 购买件数(件) <</label><input class="input50px numberInput" type="text"></div>'
                + '<div><input class="input50px number_input discount_num_input" type="text"><label>≤ 消费折扣(折:0-10) <</label><input class="input50px number_input discount_num_input" type="text"><br /><label>会员参与次数</label><input class="input130px numberInput" type="text">次</div></div>'
                + '<div class="ConsumerFooter"><div class="consumeCheckWrap"><label>消费后赠送</label><div class="checkbox1">'
                + '<span><input type="checkbox" checked="checked" class="check pointCheck" id="pointCheck' + i + '"><label for="pointCheck' + i + '"></label>积分</span><span><input type="checkbox" checked="checked" class="check couponCheck" id="couponCheck' + i + '"><label for="couponCheck' + i + '"></label>优惠券</span></div></div>'
                + '<div><div class="presentPoint_wrap"><label style="color:#c26555;">赠送积分*</label><input class="input130px text_input numberInput" type="text"></div><div class="presentCoupon_wrap"><ul class="operate_ul"><li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text">'
                + '<ul class="activity_select coupon_activity">'
                + activity.cache.coupon
                + '</ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li>'
                + '</ul></li></ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号"></div></div></div></div><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
            $("#consumeCouponWrap").append(html);
        });
    },
    uploadOSS: function () {//上传logo OSS
        var _this = this;
        var client = new OSS.Wrapper({
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            bucket: 'products-image'
        });
        $("#reliefBtn").change(function (e) {
            var file = e.target.files[0];
            var time = _this.getNowFormatDate();
            var corp_code = $("#OWN_CORP").val();
            // var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            //
            // if (isIE) {
            //     var filePath = e.value;
            //     var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
            //     // var files = fileSystem.GetFile(filePath);
            //     var file = fileSystem.GetFile(filePath);
            //     console.log(file);
            // } else {
            //     var file = e.target.files[0];
            // }
            if(file.type != "text/plain"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请上传.txt类型的文件"
                });
                $("#reliefBtn").val("");
                return;
            }
            var reader = new FileReader();
            reader.onload = function () {
                $("#reliefView textarea").val(this.result);
            };
            reader.readAsText(file,'gbk');
            whir.loading.add("上传中,请稍后...", 0.5);
            var storeAs = '/crm/UploadFiles/' + corp_code + '/' + time + '.txt';
            client.multipartUpload(storeAs, file).then(function (result) {
                var url = "http://products-image.oss-cn-hangzhou.aliyuncs.com/" + result.name;
                $(e.target).attr('data-src',url);
                $("#reliefView").attr("data-src","");
                // $("#reliefPreview").attr("href",url);
                whir.loading.remove();
            }).catch(function (err) {
                console.log(err);
                whir.loading.remove();
            });
        });
        $("#online_activity .online_item_wrap").on("change","input[type=file]",function (e) {
            var file = e.target.files[0];
            var time = _this.getNowFormatDate();
            var corp_code = $("#OWN_CORP").val();
            var fileSize = 0;
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            if (isIE) {
                var filePath = e.value;
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                var files = fileSystem.GetFile(filePath);
                fileSize = files.Size;
            } else {
                fileSize = e.target.files[0].size;
            }
            if (fileSize / 1024 / 1024 > 5) {//限制大小为5M
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请上传小于5M的图片"
                });
                $(e.target).val("");
                return;
            }
            whir.loading.add("上传中,请稍后...", 0.5);
            var storeAs = 'Album/Vip/iShow/' + corp_code + '_' + '_' + time + '.jpg';
            client.multipartUpload(storeAs, file).then(function (result) {
                var url = "http://products-image.oss-cn-hangzhou.aliyuncs.com/" + result.name;
                $(e.target).attr('data-src',url);
                whir.loading.remove();
            }).catch(function (err) {
                console.log(err);
                whir.loading.remove();
            });
        });
        document.getElementById('upload_logo').addEventListener('change', function (e) {
            var file = e.target.files[0];
            var time = _this.getNowFormatDate();
            var corp_code = $("#OWN_CORP").val();
            var fileSize = 0;

            var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            if (isIE) {
                var filePath = e.value;
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                var files = fileSystem.GetFile(filePath);
                fileSize = files.Size;
            } else {
                fileSize = e.target.files[0].size;
            }
            if (fileSize / 1024 / 1024 > 5) {//限制大小为5M
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请上传小于5M的图片"
                });
                $("#upload_logo").val("");
                return;
            }
            whir.loading.add("上传中,请稍后...", 0.5);
            var storeAs = 'Album/Vip/iShow/' + corp_code + '_' + '_' + time + '.jpg';
            client.multipartUpload(storeAs, file).then(function (result) {
                var url = "http://products-image.oss-cn-hangzhou.aliyuncs.com/" + result.name;
                _this.addLogo(url);
            }).catch(function (err) {
                console.log(err);
            });
        });
    },
    getNowFormatDate: function () {//获取时间戳
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        var H = date.getHours();
        var M = date.getMinutes();
        var S = date.getSeconds();
        var m = date.getMilliseconds();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = "" + year + month + strDate + H + M + S + m;
        return currentdate
    },
    addLogo: function (url) {//添加logo节点
        var img = "<img src='" + url + "' alt='暂无图片'>";
        // $("#upload_logo").val(url);
        var len = $("#upload_logo").parent().prevAll("img").length;
        if (len == 0) {
            $("#upload_logo").parent().before(img);
        } else {
            $("#upload_logo").parent().prev("img").replaceWith(img);
        }
        whir.loading.remove();
    },
    chooseShop: function () {//选择活动范围
        $("#chooseBtn").click(function () {
            var arr = whir.loading.getPageSize();
            activity.popUp = "";
            $("#p").css({
                "width": +arr[0] + "px",
                "height": +arr[1] + "px"
            });
            $("#p").show();
            $("#screen_wrapper").show();
        });
        $("#screen_activity_btn").click(function () {
            $("#p").hide();
            $("#screen_wrapper").hide();
            var input = $("#coupon_activity").find("input:not('.option_input,.option_input_long,.notEmpty')");
            for (var j = 0; j < input.length; j++) {
                $(input[j]).val("");
                $(input[j]).attr("data-code", "");
            }
            activity.getCoupon();
        });
        $("#screen_wrapper_close").click(function () {
            $("#screen_wrapper").hide();
            $("#p").hide();
        });
        $("#choose_shop").click(function () {//店铺弹窗
            if (activity.cache.store_codes !== "") {
                var store_codes = activity.cache.store_codes.split(',');
                var store_names = activity.cache.store_names.split(',');
                var shop_html_right = "";
                for (var h = 0; h < store_codes.length; h++) {
                    shop_html_right += "<li id='" + store_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_codes[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").eq(0).html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            } else {
                $("#screen_shop .s_pitch span").eq(0).html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            activity.shop_num = 1;
            activity.isscroll = false;
            $("#screen_shop").show();
            $("#screen_wrapper").hide();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            activity.getstorelist(activity.shop_num);
        });
        $("#shop_brand").click(function () {
            if (activity.cache.brand_codes !== "") {
                var brand_codes = activity.cache.brand_codes.split(',');
                var brand_names = activity.cache.brand_names.split(',');
                var brand_html_right = "";
                for (var h = 0; h < brand_codes.length; h++) {
                    brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").eq(0).html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            } else {
                $("#screen_brand .s_pitch span").eq(0).html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_wrapper").hide();
            activity.getbrandlist();
        });
        $("#shop_area").click(function () {//区域弹窗
            if (activity.cache.area_codes !== "") {
                var area_codes = activity.cache.area_codes.split(',');
                var area_names = activity.cache.area_names.split(',');
                var area_html_right = "";
                for (var h = 0; h < area_codes.length; h++) {
                    area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            \</li>"
                }
                $("#screen_area .s_pitch span").eq(0).html(h);
                $("#screen_area .screen_content_r ul").html(area_html_right);
            } else {
                $("#screen_area .s_pitch span").eq(0).html("0");
                $("#screen_area .screen_content_r ul").empty();
            }
            activity.isscroll = false;
            activity.area_num = 1;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").show();
            $("#screen_wrapper").hide();
            activity.getarealist(activity.area_num);
        });
        $("#lookStoreCondition").click(function () {
            var div = "";
            if (activity.cache.brand_names == "" && activity.cache.area_names == "" && activity.cache.store_names == "") {
                div = '<div style="text-align: center;line-height: 150px">暂无条件</div>';
            }
            if (activity.cache.area_names != "") {
                div += '<div><span class="conditionName">所选群组</span><span title=' + activity.cache.area_names + '>' + activity.cache.area_names + '</span></div>';
            }
            if (activity.cache.brand_names != "") {
                div += '<div><span class="conditionName">所选品牌</span><span title=' + activity.cache.brand_names + '>' + activity.cache.brand_names + '</span></div>';
            }
            if (activity.cache.store_names != "") {
                div += '<div><span class="conditionName">所选店铺</span><span title=' + activity.cache.store_names + '>' + activity.cache.store_names + '</span></div>';
            }
            $("#storeConditionWrap .choosedContent").html(div);
            $("#storeConditionWrap").show();
        });
    },
    getcorplist: function (a) {
        oc.postRequire("post", "/user/getCorpByUser", "", "", function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var corp_html = '';
                for (var i = 0; i < msg.corps.length; i++) {
                    corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
                }
                $("#OWN_CORP").append(corp_html);
                if (a !== "") {
                    $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
                    activity.getWxTypeParams();
                }
                $('.corp_select select').searchableSelect();
                $('.corp_select .searchable-select-input').keydown(function (event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        var corp = $("#OWN_CORP").val();
                        if (corp !== activity.cache.corp_code) {
                            $("#shop_amount").html("0");
                            $("#wechat").empty();
                            $("#wechat_input").val("");
                            $("#wechat_input").attr("data-id", "");
                            activity.cache.store_codes = "";
                            activity.cache.store_names = "";
                            $("#tabs>div:first-child").attr("data-corp", corp);
                            var input = $("#coupon_activity").find("input:not('.option_input,.notEmpty,.option_input_long')");
                            var input_l = $("#recruit_activity").find("input");
                            for (var j = 0; j < input.length; j++) {
                                $(input[j]).val("");
                                $(input[j]).attr("data-code", "");
                            }
                            for (var k = 0; k < input_l.length; k++) {
                                $(input_l[k]).val("");
                                $(input_l[k]).attr("data-code", "");
                            }
                            activity.getCoupon();
                            activity.getWxTypeParams();
                            activity.getexpend();
                            activity.cache.corp_code = corp;
                        }
                    }
                });
                $('.searchable-select-item').click(function () {
                    var corp = $("#OWN_CORP").val();
                    if (corp !== activity.cache.corp_code) {
                        $("#shop_amount").html("0");
                        $("#wechat").empty();
                        $("#wechat_input").val("");
                        $("#wechat_input").attr("data-id", "");
                        activity.cache.store_codes = "";
                        activity.cache.store_names = "";
                        var input = $("#coupon_activity").find("input:not('.option_input,.option_input_long,.notEmpty')");
                        var input_l = $("#recruit_activity").find("input");
                        for (var j = 0; j < input.length; j++) {
                            $(input[j]).val("");
                            $(input[j]).attr("data-code", "");
                        }
                        for (var k = 0; k < input_l.length; k++) {
                            $(input_l[k]).val("");
                            $(input_l[k]).attr("data-code", "");
                        }
                        $("#tabs>div:first-child").attr("data-corp", corp);
                        activity.getCoupon();
                        activity.getexpend();
                        activity.getWxTypeParams();
                        activity.cache.corp_code = corp;
                    }
                });
                var corp = $("#OWN_CORP").val();
                $("#tabs>div:first-child").attr("data-corp", corp);
                activity.getCoupon();
                activity.getexpend();
                activity.cache.corp_code = corp;
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
    getstorelist: function (a) {//店铺接口
        var searchValue = $("#store_search").val();
        var pageSize = 20;
        var pageNumber = a;
        var area_code = "";
        var brand_code = "";
        if (activity.popUp == "") {
            area_code = activity.cache.area_codes;
            brand_code = activity.cache.brand_codes;
        } else {
            area_code = activity.vipcache.area_codes;
            brand_code = activity.vipcache.brand_codes;
        }
        var _param = {};
        _param['corp_code'] = $("#OWN_CORP").val();
        _param['area_code'] = area_code;
        _param['brand_code'] = brand_code;
        _param['searchValue'] = searchValue;
        _param['pageNumber'] = pageNumber;
        _param['pageSize'] = pageSize;
        whir.loading.add("", 0.5);//加载等待框
        $("#mask").css("z-index", "10002");
        oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var total = list.total;
                var hasNextPage = list.hasNextPage;
                var list = list.list;
                var store_html = '';
                $("#screen_shop .s_pitch span").eq(1).text(total);
                if (list.length == 0) {

                } else {
                    if (list.length > 0) {
                        for (var i = 0; i < list.length; i++) {
                            store_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>" + list[i].store_name + "</span></li>"
                        }
                    }
                }
                if (hasNextPage == true) {
                    activity.shop_num++;
                    activity.shop_next = false;
                }
                if (hasNextPage == false) {
                    activity.shop_next = true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if (!activity.isscroll) {
                    $("#screen_shop .screen_content_l").unbind("scroll").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                            if (activity.shop_next) {
                                return;
                            }
                            activity.getstorelist(activity.shop_num);
                        }
                    })
                }
                activity.isscroll = true;
                var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_shop .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
                }
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        })
    },
    getarealist: function (a) {//区域接口
        var searchValue = $("#area_search").val().trim();
        var pageSize = 20;
        var pageNumber = a;
        var _param = {};
        _param['corp_code'] = $("#OWN_CORP").val();
        _param["searchValue"] = searchValue;
        _param["pageSize"] = pageSize;
        _param["pageNumber"] = pageNumber;
        whir.loading.add("", 0.5);//加载等待框
        $("#mask").css("z-index", "10002");
        oc.postRequire("post", "/area/selAreaByCorpCode", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var hasNextPage = list.hasNextPage;
                var total = list.total;
                var list = list.list;
                var area_html_left = '';
                $("#screen_area .s_pitch span").eq(1).text(total);
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        area_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].area_code + "' data-areaname='" + list[i].area_name + "' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].area_name + "</span></li>"
                    }
                }
                if (hasNextPage == true) {
                    activity.area_num++;
                    activity.area_next = false;
                }
                if (hasNextPage == false) {
                    activity.area_next = true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if (!activity.isscroll) {
                    $("#screen_area .screen_content_l").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight >= nScrollHight) {
                            if (activity.area_next) {
                                return;
                            }
                            activity.getarealist(activity.area_num);
                        }
                    })
                }
                activity.isscroll = true;
                var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_area .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
                }
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        })
    },
    getbrandlist: function () {
        var searchValue = $("#brand_search").val();
        var _param = {};
        _param['corp_code'] = $("#OWN_CORP").val();
        _param["searchValue"] = searchValue;
        whir.loading.add("", 0.5);//加载等待框
        $("#mask").css("z-index", "10002");
        oc.postRequire("post", "/shop/brand", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = message.brands;
                var total = message.total;
                var brand_html_left = '';
                $("#screen_brand .s_pitch span").eq(1).text(total);
                if (list.length == 0) {
                    for (var h = 0; h < 9; h++) {
                        brand_html_left += "<li></li>"
                    }
                } else {
                    if (list.length < 9) {
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                        }
                        for (var j = 0; j < 9 - list.length; j++) {
                            brand_html_left += "<li></li>"
                        }
                    } else if (list.length >= 9) {
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                        }
                    }
                }
                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_brand .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
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
    getstafflist: function (a) {//获取导购方法
        var self = this;
        var pageSize = 20;
        var pageNumber = a;
        var _param = {};
        var searchValue = $('#staff_search').val().trim();
        _param['area_code'] = self.vipcache.area_codes;
        _param['brand_code'] = self.vipcache.brand_codes;
        _param['store_code'] = self.vipcache.store_codes;
        _param['searchValue'] = searchValue;
        _param["corp_code"] = $("#OWN_CORP").val();
        _param['pageNumber'] = pageNumber;
        _param['pageSize'] = pageSize;
        whir.loading.add("", 0.5);//加载等待框
        oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var total = list.total+1;
                var hasNextPage = list.hasNextPage;
                var list = list.list;
                var staff_html = '';
                $("#screen_staff .s_pitch span").eq(1).text(total);
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-phone='" + list[i].phone + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].user_name + "\(" + list[i].user_code + "\)</span></li>"
                    }
                }
                if (hasNextPage == true) {
                    staff_num++;
                    staff_next = false;
                }
                if (hasNextPage == false) {
                    staff_next = true;
                }
                if (a == 1 && searchValue == "") {
                    $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
                }
                $("#screen_staff .screen_content_l ul").append(staff_html);
                if (!isscroll) {
                    $("#screen_staff .screen_content_l").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight >= nScrollHight) {
                            if (staff_next) {
                                return;
                            }
                            self.getstafflist(staff_num);
                        }
                    })
                }
                isscroll = true;
                var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_staff .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                console.log(data.message);
            }
        })
    },
    getGroup: function () {
        var _param = {};
        _param["corp_code"] = $("#OWN_CORP").val();
        _param["search_value"] = $("#task_group_search").val().trim();
        oc.postRequire("post", "/vipGroup/getCorpGroups", "0", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var total = message.total;
                var html = "";
                $("#screen_group .s_pitch span").eq(1).text(total);
                $("#screen_group .screen_content_l ul").empty();
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html += "<li><div class='checkbox1'><input  data-type='" + list[i].group_type + "' type='checkbox' value='" + list[i].vip_group_code + "' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + "'/><label for='checkboxOneInput"
                            + i
                            + "'></label></div><span class='p16'>" + list[i].vip_group_name + "</span></li>"
                    }
                    $("#screen_group .screen_content_l ul").append(html);
                }
            }
        })
    },
    getshopperList: function (a) {
        if(a == "online"){
            var params = {
                "TYPE": "AND",
                "BRAND_ID": $("#item_brand").attr("data-code"),
                "PRODUCT_NAME": $("#item_shopName").val(),
                "SEASON_PRD": $("#item_season").val() == "全部" ? "" : $("#seasonInput").val(),
                "CATA1_PRD": $("#item_typeB").val() == "全部" ? "" : $("#bgTypeInput").val(),
                "CATA2_PRD": $("#item_typeM").val() == "全部" ? "" : $("#midTypeInput").val(),
                "CATA3_PRD": $("#item_typeS").val() == "全部" ? "" : $("#smTypeInput").val(),
                "PRODUCT_CODE": $("#item_shopCode").val(),
                "SKU_CODE": $("#item_skuCode").val()
            };
        }else {
            var isRemove = "";
            $("#chooseShopperSwitch").hasClass("on") == true ? isRemove = "NOT" : isRemove = "AND";
            var skuCode = $(activity.goodsWrap).find(".consumeBtn").attr("data-id");
            skuCode == "" ? $("#chooseCount").text("0") : $("#chooseCount").text(skuCode.split(",").length);
            var params = {
                "TYPE": isRemove,
                "BRAND_ID": $("#brandID").attr("data-code"),
                "PRODUCT_NAME": $("#goodsName").val(),
                "SEASON_PRD": $("#seasonInput").val() == "全部" ? "" : $("#seasonInput").val(),
                "CATA1_PRD": $("#bgTypeInput").val() == "全部" ? "" : $("#bgTypeInput").val(),
                "CATA2_PRD": $("#midTypeInput").val() == "全部" ? "" : $("#midTypeInput").val(),
                "CATA3_PRD": $("#smTypeInput").val() == "全部" ? "" : $("#smTypeInput").val(),
                "PRODUCT_CODE": $("#goodsCode").val(),
                "SKU_CODE": $("#skuCode").val()
            };
        }
        var param = {};
        param.corp_code = $("#OWN_CORP").val();
        param.page_num = activity.shopperNum;
        param.page_size = "100";
        param.param = params;
        whir.loading.add("", 0.5);
        oc.postRequire("post", "/vipActivity/getSku", "", param, function (data) {
            if (data.code == 0) {
                var msg = JSON.parse(data.message);
                var list = msg.sku_list;
                a != 'online' ? $(activity.goodsWrap).find(".consumeBtn").attr("data-id", msg.condition) : '';
                var nextPage = msg.lastPage;
                nextPage == "true" ? activity.goodsNext = false : activity.goodsNext = true;
                var tr = '';
                if (list.length != 0) {
                    for (var i = 0; i < list.length; i++) {
                        var n = i + (activity.shopperNum - 1) * 100 + 1,
                            td = a == 'online' ? '<td width="30px"><input id="onlineCheck'+n+'" class="checkbox" name="goods" type="radio"><label for="onlineCheck'+n+'"></label></td>' : '';
                        tr += '<tr id="' + list[i].SKU_CODE + '">'
                            + td
                            + '<td width="30px">'
                            + n
                            + '</td><td width="80px"><img src="' + list[i].PRODUCT_IMG + '" onerror="activity.imgError(this)">'
                            + '</td><td width="100px" title="' + list[i].PRODUCT_NAME + '"><span class="ellipsis">'
                            + list[i].PRODUCT_NAME
                            + '</span></td><td width="125px" title="' + list[i].PRODUCT_CODE + '"><span class="ellipsis">'
                            + list[i].PRODUCT_CODE
                            + '</span></td><td width="125px" title="' + list[i].SKU_CODE + '"><span class="ellipsis">'
                            + list[i].SKU_CODE
                            + '</span></td><td width="60px" title="' + list[i].PRICE_SUG + '">'
                            + list[i].PRICE_SUG
                            + '</td><td width="70px" title="' + list[i].SEASON_PRD + '">'
                            + list[i].SEASON_PRD
                            + '</td><td title="' + list[i].CATA1_PRD + '"><span style="width: 50px" class="ellipsis">'
                            + list[i].CATA1_PRD
                            + '</span></td><td title="' + list[i].CATA2_PRD + '"><span style="width: 50px" class="ellipsis">'
                            + list[i].CATA2_PRD
                            + '</span></td></tr>'
                    }
                } else {
                    activity.goodsNext = false;
                    for (var i = 0; i < 5; i++) {
                        if (i == 2) {
                            tr += "<tr><td colspan='8'>暂无数据</td></tr>";
                        } else {
                            tr += "<tr><td colspan='8'></td></tr>"
                        }
                    }
                }
                a !='online' ? $("#chooseShopperTable tbody").append(tr) : $("#onlineChooseShopper tbody").append(tr);
            } else {
                var tr = "";
                activity.goodsNext = false;
                for (var i = 0; i < 5; i++) {
                    if (i == 2) {
                        tr += "<tr><td colspan='8'>暂无数据</td></tr>";
                    } else {
                        tr += "<tr><td colspan='8'></td></tr>"
                    }
                }
                a !='online' ? $("#chooseShopperTable tbody").append(tr) : $("#onlineChooseShopper tbody").append(tr);
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "获取商品失败"
                });
            }
            whir.loading.remove();
        });
    },
    getexpend: function () {
        var corp_code = $("#OWN_CORP").val();
        var param = {"corp_code": corp_code};
        oc.postRequire("post", "/vipparam/corpVipParams", "0", param, function (data) {
            if (data.code == 0) {
                $("#expend_attribute").empty();
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var param = "";
                var html = "";
                var simple_html = "";
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        var param_type = list[i].param_type;
                        if (param_type == "date") {
                            simple_html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                + '<input data-expend="date" data-kye="' + list[i].param_name + '" readonly="true" id="start' + i + 's" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start' + i + 's\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end' + i + 's" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end' + i + 's\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                            html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                + '<input data-expend="date" data-kye="' + list[i].param_name + '" readonly="true" id="start' + i + '" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start' + i + '\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end' + i + '" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end' + i + '\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                        }
                        if (param_type == "select") {
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if (param_values.length > 0) {
                                var li = "";
                                for (var j = 0; j < param_values.length; j++) {
                                    li += '<li>' + param_values[j] + '</li>'
                                }
                                html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                    + '<input data-expend="text" data-kye="' + list[i].param_name + '" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            } else {
                                html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                    + '<input data-expend="text" data-kye="' + list[i].param_name + '" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                        if (param_type == "text") {
                            html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                + '<input data-expend="text" data-kye="' + list[i].param_name + '" class="input"><ul class="sex_select"></ul></div>'
                        }
                        if (param_type == "longtext") {
                            html += '<div class="textarea"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                + '<textarea data-kye="' + list[i].param_name + '" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                        }
                        if (param_type == "check") {
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if (param_values.length > 0) {
                                var li = "";
                                for (var j = 0; j < param_values.length; j++) {
                                    li += '<li class="type_check">'
                                        + '<input type="checkbox" style="margin:0;vertical-align:middle;width: 15px;height: 15px;"/>'
                                        + '<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="' + param_values[j] + '">' + param_values[j] + '</span>'
                                        + '</li>'
                                }
                                html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="' + list[i].param_name + '" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            } else {
                                html += '<div class="contion_input"><label title="' + list[i].param_desc + '">' + list[i].param_desc + '</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="' + list[i].param_name + '" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                    }
                }
                $("#expend_attribute").html(html);
                $("#memorial_day").html(simple_html);
            } else if (data.code == -1) {
                console.log(data.message);
            }
        });
        oc.postRequire("post", "/vipparam/vipInfoParams", "0", param, function (data) {
            if (data.code == 0) {
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var temp = '';
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        var param_type = list[i].param_type;
                        temp += '<li data-require="'+list[i].required+'" data-name="'+list[i].param_name+'" data-param_type="'+list[i].param_type+'" data-value="'+list[i].param_values+'" data-attr="'+list[i].param_attribute+'">'+list[i].param_desc+'</li>';
                    }
                }
                $("#apply_condition .activity_select").html(temp);
            } else if (data.code == -1) {
                console.log(data.message);
            }
        });
    },
    imgError: function (image) {//图片502处理
        $(image).attr("src", "../img/goods_default_image.png");
    },
    getGoodsType: function () {
        var param = {};
        param.corp_code = $("#OWN_CORP").val();
        oc.postRequire("post", "/vipGroup/getClassQuarter", "", param, function (data) {
            if (data.code == 0) {
                var msg = JSON.parse(data.message);
                var typeMid = msg.CATA2_PRD;//中类
                var typeBg = msg.CATA1_PRD;//大类
                var typeSm = msg.CATA3_PRD;//小类
                var typeSeason = msg.SEASON_PRD;//季节
                var bg = "";
                var sm = "";
                var mid = "";
                var season = "";
                for (var i = 0; i < typeMid.length; i++) {
                    mid += "<li  title='" + typeMid[i].name + "'><input type='checkbox'>" + typeMid[i].name + "</li>";
                }
                for (var j = 0; j < typeBg.length; j++) {
                    bg += "<li  title='" + typeBg[j].name + "'><input type='checkbox'>" + typeBg[j].name + "</li>";
                }
                for (var k = 0; k < typeSm.length; k++) {
                    sm += "<li  title='" + typeSm[k].name + "'><input type='checkbox'>" + typeSm[k].name + "</li>";
                }
                for (var l = 0; l < typeSeason.length; l++) {
                    season += "<li  title='" + typeSeason[l].name + "'><input type='checkbox'>" + typeSeason[l].name + "</li>";
                }
                $(".typeBg").html(bg);
                $(".typeMid").html(mid);
                $(".typeSm").html(sm);
                $(".season").html(season);
            } else {
                console.log(data.message);
            }
        });
    },
    testTheme: function () {//匹配input规则
        $("#activity_theme").blur(function () {
            var theme = $("#activity_theme").val();
            if (theme !== "") {
                var theme_l = $("#activity_theme").attr("data-name");
                if (theme == theme_l) {
                    activity.theme = "";
                    return;
                } else {
                    var param = {};
                    param["corp_code"] = $("#OWN_CORP").val();
                    param["activity_theme"] = theme;
                    param["activity_code"] = activity.activity_code;
                    oc.postRequire("post", "/vipActivity/activityThemeExist", "0", param, function (data) {
                        if (data.code == "-1") {
                            activity.theme = "当前企业下该会员活动标题已存在";
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: data.message
                            });
                        } else if (data.code == 0) {
                            activity.theme = "";
                        }
                    });
                }
            }
        });
        //招募金额限制数字格式
        $(".setUp_activity_details").on("blur", ".number_input", function () {
            var reg = /^\d+(\.\d{1,2})?$/g;
            var val = $(this).val();
            if (!reg.test(val) && val !== "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请输入数字"
                });
                $(this).val("");
            }
        });
        //折扣限制
        $(".setUp_activity_details").on("blur", ".discount_num_input", function () {
            var reg = /^(\d|10)(\.\d{1,2})?$/g
            // var reg = /^\d+[0-10]+(\.\d{1,2})?$/g
            var val = $(this).val();
            if (!reg.test(val) && val !== "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请输入0-10的数字"
                });
                $(this).val("");
            }
        });
        $("#setUp_activityType").on("blur", "#h5_activity input,.url_input", function () {
            var reg = /^((https|http)?:\/\/)[^\s]+/;
            // var reg = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
            var val = $(this).val();
            if (!reg.test(val) && val !== "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请填写正确的网页地址并且以“http://”开头"
                });
                $(this).val("");
                return;
            }
        });
        $("#setUp_activityType").on("blur", ".numberInput", function () {//验证数字，前后大小限制
            var reg = /^\d+$/;
            var val = $(this).val();
            $(this).hasClass('discount_num_input')?reg = /^(\d|10)(\.\d{1,2})?$/g:"";
            if (!reg.test(val) && val !== "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请输入整数"
                });
                $(this).val("");
            }
            if ($(this).next(".numberInput").length == 1 && val != "") {
                var max = parseFloat($(this).next(".numberInput").val());
                if (val > max && max != "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请小于后面的数值"
                    });
                    $(this).val("");
                }
            } else if ($(this).prev(".numberInput").length == 1 && val != "") {
                var min = parseFloat($(this).prev(".numberInput").val());
                if (val < min && min != "") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请大于前面的数值"
                    });
                    $(this).val("");
                }
            }
            if ($(this).attr("id") == "attendVipTotal") {
                $("#consumeCouponWrap .coupon_details_wrap:visible").each(function () {//消费后送券会员参与次数小于总次数
                    var count = $(this).find("input")[7].value;
                    if (val != "" && val < count) {
                        art.dialog({
                            time: 2,
                            lock: true,
                            cancel: false,
                            content: "会员参与总数不能小于会员参与次数"
                        });
                        $("#attendVipTotal").val("")
                    }
                });
            }
            if ($(this).prev("label").text() == '会员参与次数') {
                if (val > $("#attendVipTotal").val() && $("#attendVipTotal").val() != "") {
                    art.dialog({
                        time: 2,
                        lock: true,
                        cancel: false,
                        content: "会员参与次数不能大于会员参与总数"
                    });
                    $(this).val("");
                }
            }
        });
    },
    createCode: function () {//生成二维码
        $("#create_code").click(function () {
            $(".offline_right_wrap").empty()
            // 创建二维码
            var qrcode = new QRCode(document.getElementById("qrcode"), {
                width: 160,//设置宽高
                height: 160
            });
            qrcode.makeCode("http://www.baidu.com");
        });
    },
    getCoupon: function () {
        var corp_code = $("#OWN_CORP").val();
        var store_codes = activity.cache.store_codes;
        var app_id = $("#wechat_input").attr("data-id");
        var _param = {
            "corp_code": corp_code,
            "app_id": app_id,
            'store_code': store_codes
        };
        $(".coupon_activity").html("<div style='height: 30px;line-height: 30px;text-align: center'>数据获取中...</div>");
        oc.postRequire("post", "/vipRules/getCoupon", "0", _param, function (data) {
            if (data.code == 0) {
                var li = "";
                var msg = JSON.parse(data.message);
                if (msg.length == 0 && $("#coupon_activity").css("display") == "block") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有优惠券"
                    });
                    activity.cache.coupon = "";
                } else if (msg.length > 0) {
                    for (var i = 0; i < msg.length; i++) {
                        li += "<li data-type='"+msg[i].send_type+"' data-code='" + msg[i].couponcode + "' title='" + msg[i].name + "' data-name='"+msg[i].name+"'>" + msg[i].name + "</li>"
                    }
                    activity.cache.coupon = li;
                }
                $(".coupon_activity").empty();
                $(".coupon_activity").append(li);
            }
        });
    },
    getWxTypeParams: function () {//卡类型，微信，纪念日类型
        var corp_code = $("#OWN_CORP").val();
        var param = {"corp_code": corp_code};
        oc.postRequire("post", "/vipCardType/getVipCardTypes", "0", param, function (data) {
            if (data.code == 0) {
                var li = "";
                var message = JSON.parse(data.message);
                var msg = JSON.parse(message.list);
                if (msg.length == 0 && (($("#coupon_activity").css("display") == "block" && $("#coupon_title li:nth-child(2)").hasClass("coupon_active")) || $("#recruit_activity").css("display") == "block")) {
                    activity.cache.card_type = "";
                } else if (msg.length > 0) {
                    for (var i = 0; i < msg.length; i++) {
                        li += "<li data-code='" + msg[i].vip_card_type_code + "' data-name='"+msg[i].vip_card_type_name+"("+msg[i].vip_card_type_code+")'>" +msg[i].vip_card_type_name+"("+msg[i].vip_card_type_code+ ")</li>"
                    }
                    activity.cache.card_type = li;
                }
                $(".vipCardType").empty();
                $(".vipCardType").append(li);
            }
        });
        oc.postRequire("post", "/vipparam/dateVipParams", "0", param, function (data) {
            if (data.code == 0) {
                var li = "";
                var message = JSON.parse(data.message);
                var msg = JSON.parse(message.list);
                if (msg.length == 0 && $("#coupon_activity").css("display") == "block" && $(".coupon_active").attr("data-id") == "anniversary") {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "该企业下没有设置纪念日类型"
                    });
                    activity.cache.souvenir_type = "";
                } else if (msg.length > 0) {
                    for (var i = 0; i < msg.length; i++) {
                        li += "<li data-name='" + msg[i].param_name + "'>" + msg[i].param_desc + "</li>"
                    }
                    activity.cache.souvenir_type = li;
                }
                $(".souvenir_type").empty();
                $(".souvenir_type").append(li);
            }
        });
        //获取公众号
        oc.postRequire("post", "/corp/selectWx", "", param, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var list = msg.list;
                var li = "";
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        li += "<li data-id='" + list[i].app_id + "'>" + list[i].app_name + "</li>";
                    }
                    $("#wechat").append(li);
                }
            }
        });
    },
    checkEmpty: function () {
        activity.isEmpty = false;
        $("#setUp_activityType>div:visible").each(function () {
            var id = $(this).attr("id");
            if (id == "coupon_activity") {
                $(this).children("div:not('.switch')").each(function (i, e) {
                    if ($(e).css("display") == "block") {
                        var vue = $(e).find(".text_input:visible:not('.url_input')");
                        $(vue).each(function (i, e) {
                            if($(e).attr("class").indexOf('bath_num_input') > -1){
                                $(e).parent().prev(".operate_ul").find("input").each(function (index,item) {
                                    if($(item).attr("data-type") == "2" && $(vue[i]).val() == "" ){
                                        activity.isEmpty = true;
                                        activity.label = "批次号";
                                    }
                                });
                            }else {
                                if ($(vue[i]).val() == "") {
                                    activity.isEmpty = true;
                                    if ($(e).prev("label").html() == undefined) {
                                        activity.label = "选择优惠券";
                                    } else {
                                        activity.label = $(e).prev("label").html().replace("*", "");
                                    }
                                }
                            }
                        });
                    }
                });
            } else if (id == "invite_activity") {
                if ($("#extend_url").css("display") !== "none") {
                    var vue = $("#invite_url").val();
                    if (vue == "") {
                        activity.isEmpty = true;
                        activity.label = "推广链接";
                    }
                } else {
                    var vue = $("#invite_wrap").find(".text_input");
                    $(vue).each(function (i, e) {
                        if ($(vue[i]).val() == "") {
                            activity.isEmpty = true;
                            activity.label = $(e).prev("label").html().replace("*", "");
                        }
                    });
                }
            } else if(id == "online_activity"){
                if($("#td_allow").val() == "是" && $("#unsubscribe_date").val() == ''){
                    activity.isEmpty = true;
                    activity.label = '退订截止日期';
                }
                if($("#setTimeCheck").prop("checked") && $("#distribute_reward_date").val() == ''){
                    activity.isEmpty = true;
                    activity.label = '奖励发放日期';
                }
                if($("#online_activity .online_item_wrap:visible dl").length < 2){
                    activity.isEmpty = true;
                    activity.label = '项目';
                }
                $("#online_activity .online_item_wrap:visible dl:not(:first-child)").each(function (i,item) {
                    if($(item).find("input").eq(1).val().trim() == ''){
                        activity.isEmpty = true;
                        activity.label = '项目名称';
                    }
                    if($(item).find("input").eq(2).val().trim() == ''){
                        activity.isEmpty = true;
                        activity.label = '人数上限';
                    }
                    if($(item).find("dt:nth-child(4)").find("input").eq(1).val().trim() == ''){
                        activity.isEmpty = true;
                        activity.label = '报名费用';
                    }
                    if($(item).find("dt:nth-child(4)").find("input").eq(2).length != 0 && $(item).find("dt:nth-child(4)").find("input").eq(2).val().trim() == ''){
                        activity.isEmpty = true;
                        activity.label = '报名费用';
                    }
                });
                $("#apply_condition .operate_ul>li").each(function (i,item) {
                    if($(item).find("input").val() == ''){
                        activity.isEmpty = true;
                        activity.label = '字段名称';
                    }
                });
                if($("#useCheck").prop("checked") && ($("#reliefBtn").attr("data-src") == '' || $("#reliefBtn").attr("data-src") == undefined)){
                    activity.isEmpty = true;
                    activity.label = '免责协议';
                }
                if($("#onlinePointCheck").prop("checked") && $("#online_point").val() == ''){
                    activity.isEmpty = true;
                    activity.label = '赠送积分';
                }
                if($("#onlineCouponCheck").prop("checked")){
                    $("#online_coupon>li").each(function (i,item) {
                        if($(item).find("input").val() == ''){
                            activity.isEmpty = true;
                            activity.label = '优惠券';
                        }
                        if($(item).find("input").attr("data-type") == 2 && $("#online_batch_num").val() == ''){
                            activity.isEmpty = true;
                            activity.label = '批次号';
                        }
                    });
                }
            } else {
                var vue = $(this).find(".text_input:visible:not('.url_input')");
                $(vue).each(function (i, e) {
                    if($(e).attr("class").indexOf('bath_num_input') > -1){
                        $(e).parent().prev(".operate_ul").find("li").each(function (index,item) {
                            if($(item).find("input").attr("data-type") == "2" && $(vue[i]).val() == "" ){
                                activity.isEmpty = true;
                                activity.label = "批次号";
                            }
                        });
                    }else {
                        if ($(vue[i]).val() == "") {
                            activity.isEmpty = true;
                            if($(e).prev("label").length != 0) {
                                activity.label = $(e).prev("label").html().replace("*", "");
                                if (activity.label == "至") {
                                    activity.label = "节日时间";
                                }
                                if (activity.label == "每邀请") {
                                    activity.label = "邀请人数";
                                }
                            }else {
                                activity.label = "人数区间";
                            }
                        }
                    }
                });
            }
        });
        var div = $("#activity_basic .text_input");
        for (var i = div.length - 1; i >= 0; i--) {
            var vue = $(div[i]).val();
            var id = $(div[i]).attr("id");
            if (vue == "") {
                activity.isEmpty = true;
                activity.label = $(div[i]).prev("label").html().replace("*", "");
                if (activity.label == "至") {
                    activity.label = "活动时间";
                }
            }
        }
    },
    add: function () {
        var def = $.Deferred();
        var corp_code = $("#tabs>div:first-child").attr("data-corp");
        var activity_code = activity.activity_code;
        var param = {};
        var runMode = $("#activity_type").attr("data-id");
        if (activity_code !== "" && activity_code !== undefined) {
            param["activity_code"] = activity_code;
        } else {
            param["activity_code"] = "";
        }
        var store = [], brand = [], area = [];
        var code = activity.cache.store_codes.split(",");
        var name = activity.cache.store_names.split(",");
        var brandcode = activity.cache.brand_codes.split(",");
        var brandname = activity.cache.brand_names.split(",");
        var areacode = activity.cache.area_codes.split(",");
        var areaname = activity.cache.area_names.split(",");
        for (var j = 0; j < code.length; j++) {
            var obj = {};
            obj["store_code"] = code[j];
            obj["store_name"] = name[j];
            store.push(obj);
        }
        for (var a = 0; a < brandcode.length; a++) {
            var obj = {};
            obj["brand_code"] = brandcode[a];
            obj["brand_name"] = brandname[a];
            brand.push(obj);
        }
        for (var b = 0; b < areacode.length; b++) {
            var obj = {};
            obj["area_code"] = areacode[b];
            obj["area_name"] = areaname[b];
            area.push(obj);
        }
        var run_scope = {
            "store_code": store,
            "area_code": area,
            "brand_code": brand
        };
        param["corp_code"] = corp_code;
        param["activity_theme"] = $("#activity_theme").val();
        param["run_mode"] = runMode;
        param['run_scope'] = run_scope;
        param["start_time"] = $("#activity_start").val();
        param["end_time"] = $("#activity_end").val();
        param["activity_desc"] = $("#activity_describe").val();
        param["activity_store_code"] = "";
        param["app_id"] = $("#wechat_input").attr("data-id");
        param["app_name"] = $("#wechat_input").val();
        _param.screen ? "" : _param.screen = [];
        param["screen"] = _param;
        whir.loading.add("", 0.5);
        oc.postRequire("post", "/vipActivity/add", "0", param, function (data) {
            if (data.code == 0) {
                var msg = data.message;
                if (msg !== "编辑成功") {
                    activity.activity_code = msg;
                    sessionStorage.setItem("activity_code", msg);
                }
                var _param = {};
                _param["corp_code"] = $("#tabs>div:first-child").attr("data-corp");
                _param["activity_code"] = activity.activity_code;
                _param["activity_type"] = runMode;
                if (runMode == "recruit") {
                    var recruit = [];
                    var line = $("#recruit_activity>ul>li");
                    var url = $("#recruit_url").val();
                    // if(url.indexOf("http")==-1){
                    //     url="http://"+url;
                    // }
                    for (var i = 0; i < line.length; i++) {
                        var input = $(line[i]).find("input");
                        var obj = {};
                        obj["vip_card_type_code"] = $(input[0]).attr("data-code");
                        obj["join_threshold"] = $(input[1]).val();
                        recruit.push(obj);
                    }
                    _param["recruit"] = recruit;
                    _param["activity_url"] = url;
                }
                if (runMode == "h5") {
                    _param["h5_url"] = $("#h5_activity").find("input").val();
                    _param["activity_url"] = "";
                }
                if (runMode == "sales") {
                    var url = $("#sales_url").val();
                    _param["sales_no"] = $($("#sales_activity").find("input")[1]).val();
                    _param["activity_url"] = url;
                }
                if (runMode == "festival") {
                    _param["activity_url"] = $("#festival_url").val();
                    _param["festival_start"] = $($("#festival_activity").find("input")[1]).val();
                    _param["festival_end"] = $($("#festival_activity").find("input")[2]).val();
                }
                if (runMode == "coupon") {
                    var url = $("#coupon_url").val();
                    _param["activity_url"] = url;
                    if (!(activity.swicth)) {
                        console.log("优惠券已关闭");
                    } else {
                        var send_coupon_type = $("#coupon_title .coupon_active").attr("data-id");
                        _param["send_coupon_type"] = send_coupon_type;
                        if (send_coupon_type == "batch") {
                            var point = "";
                            var coupon_type = [];
                            var input = $($("#coupon_activity .choose_coupon")[0]).find(".presentCoupon_wrap ul input");
                            for (var i = 0; i < input.length; i++) {
                                var coupon_code = "";
                                var coupon_name = "";
                                var obj = {};
                                coupon_code += $(input[i]).attr("data-code");
                                coupon_name += $(input[i]).val();
                                obj['coupon_code'] = coupon_code;
                                obj['coupon_name'] = coupon_name;
                                coupon_type.push(obj);
                            }
                            $("#batchCouponCheck").prop("checked") ? "" : coupon_type = [];
                            $("#batchPointCheck").prop("checked") ? point = $("#batchPointInput").val() : "";
                            _param['present_point'] = point;
                            _param["coupon_type"] = coupon_type;
                            _param["batch_no"] = $("#batch_num_input").val();
                        } else if (send_coupon_type == "anniversary") {
                            var coupon_type = [];
                            $("#souvenirContentWrap .coupon_details_wrap").each(function () {
                                var obj = {};
                                var point = "";
                                var coupon = [];
                                obj.param_desc = $(this).find("input")[0].value;
                                obj.param_name = $($(this).find("input")[0]).attr("data-name");
                                obj.anniversary_time = $(this).find("input")[1].value == "当日" ? "D" : "M";
                                $(this).find(".pointCheck").prop("checked") ? point = $(this).find(".cardPointInput").val() : "";
                                if ($(this).find(".couponCheck").prop("checked")) {
                                    var inputs = $(this).find(".presentCoupon_wrap ul input");
                                    for (var i = 0; i < inputs.length; i++) {
                                        var coupons = {};
                                        coupons.coupon_code = $(inputs[i]).attr("data-code");
                                        coupons.coupon_type = $(inputs[i]).attr("data-type");
                                        coupons.coupon_name = $(inputs[i]).val();
                                        coupon.push(coupons);
                                    }
                                }
                                obj.send_points = point;
                                obj.coupon_type = coupon;
                                obj.batch_no = $(this).find(".bath_num_input").val();
                                coupon_type.push(obj);
                            });
                            _param["coupon_type"] = coupon_type;
                        } else if (send_coupon_type == "card") {
                            var coupon_type = [];
                            $("#cardCouponWrap .coupon_details_wrap").each(function () {
                                var obj = {};
                                var vip_card_type_code = $($(this).find("input")[0]).attr("data-code");
                                var input = $(this).find(".operate_ul input");
                                var point = "";
                                var coupon_code = "";
                                var coupon_name = "";
                                var coupon_types = '';
                                if ($(this).find(".couponCheck").prop("checked")) {
                                    for (var i = 0; i < input.length; i++) {
                                        if (i < input.length - 1) {
                                            coupon_code += $(input[i]).attr("data-code") + ",";
                                            coupon_types += $(input[i]).attr("data-type") + ",";
                                            coupon_name += $(input[i]).val() + ",";
                                        } else {
                                            coupon_code += $(input[i]).attr("data-code");
                                            coupon_types += $(input[i]).attr("data-type");
                                            coupon_name += $(input[i]).val();
                                        }
                                    }
                                }
                                $(this).find(".pointCheck").prop("checked") ? point = $(this).find(".cardPointInput").val() : "";
                                obj['vip_card_type_code'] = vip_card_type_code;
                                obj['present_point'] = point;
                                obj['coupon_code'] = coupon_code;
                                obj['coupon_name'] = coupon_name;
                                obj['coupon_type'] = coupon_types;
                                obj['batch_no'] = $(this).find(".bath_num_input").val();
                                coupon_type.push(obj);
                            });
                            _param["coupon_type"] = coupon_type;
                        } else if (send_coupon_type == "consume") {
                            var consume_condition = [];
                            $("#consumePostCoupon .coupon_details_wrap").each(function () {
                                var obj = {};
                                var input = $(this).find("input");
                                var coupon_type = [];
                                obj.priority = $(this).find(".levels input").val();
                                obj.consume_goods = $(this).find(".consumeBtn").attr("data-id");
                                obj.goods_condition = $(this).find(".lookCondition").attr("data-code");
                                obj.trade_start = input[1].value;
                                obj.trade_end = input[2].value;
                                obj.num_start = input[3].value;
                                obj.num_end = input[4].value;
                                obj.discount_start = input[5].value;
                                obj.discount_end = input[6].value;
                                obj.vip_join_count = input[7].value;
                                input[8].checked ? obj.send_points = input[10].value : obj.send_points = "";//是否传积分
                                // obj.send_points = input[10].value;
                                for (var i = 11; i < input.length - 1; i++) {
                                    var coupon = {};
                                    coupon.coupon_code = $(input[i]).attr("data-code");
                                    coupon.coupon_type = $(input[i]).attr("data-type");
                                    coupon.coupon_name = $(input[i]).val();
                                    coupon_type.push(coupon);
                                }
                                input[9].checked ? "" : coupon_type = [];
                                obj.coupon_type = coupon_type;
                                obj.batch_no = $(this).find(".bath_num_input").val();
                                consume_condition.push(obj);
                            });
                            _param["coupon_type"] = "";
                            _param["join_count"] = $("#attendVipTotal").val();
                            _param["consume_condition"] = consume_condition;
                        }
                    }
                }
                if (runMode == "invite") {
                    if ($("#invite_wrap").css("display") == "none") {
                        var url = $("#invite_url").val();
                        _param["activity_url"] = url;
                        _param['apply_title'] = "";
                        _param['apply_endtime'] = "";
                        _param['apply_desc'] = "";
                        _param['apply_success_tips'] = "";
                        _param['apply_logo'] = "";
                    } else {
                        var logo = $("#upload_logo").parent().prev("img").attr("src");
                        if (logo == undefined || logo == "") {
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                content: "请添加logo图片"
                            });
                            return;
                        }
                        var allow = $("#allow_vipAttend_btn")[0];
                        if (allow.checked == true) {
                            allow = "Y";
                        } else {
                            allow = "N";
                        }
                        _param["activity_url"] = "";
                        _param['apply_title'] = $("#invite_title").val();
                        _param['apply_endtime'] = $("#offline_end").val();
                        _param['apply_desc'] = $("#invite_summary").val();
                        _param['apply_success_tips'] = $("#invite_message").val();
                        _param['apply_logo'] = logo;
                        _param['apply_allow_vip'] = allow;
                    }
                }
                if (runMode == "register") {
                    var register = [];
                    $("#registerContentWrap .coupon_details_wrap").each(function () {
                        var obj = {};
                        var number_interval = {};
                        var present = {};
                        var point = "";
                        var coupon = [];
                        number_interval.start = $(this).find("input")[0].value;
                        number_interval.end = $(this).find("input")[1].value;
                        obj.number_interval = number_interval;
                        obj.invite = $(this).find("input")[2].value;
                        $(this).find(".pointCheck").prop("checked") ? point = $(this).find(".cardPointInput").val() : "";
                        if ($(this).find(".couponCheck").prop("checked")) {
                            var inputs = $(this).find(".presentCoupon_wrap ul input");
                            for (var i = 0; i < inputs.length; i++) {
                                var coupons = {};
                                coupons.coupon_code = $(inputs[i]).attr("data-code");
                                coupons.coupon_type = $(inputs[i]).attr("data-type");
                                coupons.coupon_name = $(inputs[i]).val();
                                coupon.push(coupons);
                            }
                        }
                        present.point = point;
                        present.coupon = coupon;
                        obj.present = present;
                        obj.batch_no = $(this).find(".bath_num_input").val();
                        register.push(obj);
                    });
                    _param["register"] = register;
                    _param["activity_url"] = "";
                }
                if(runMode == 'online_apply'){
                    var present_time_type = '',
                        apply_condition = [],
                        coupons = [],
                        apply_type_info = [];
                    $("#apply_condition .operate_ul>li").each(function (i,item) {
                        var obj = {};
                        if($(item).find("input").val() != ''){
                            obj.param_name = $(item).find("input").attr("data-name");
                            obj.required = $(item).find("input").attr("data-require");
                            obj.param_type = $(item).find("input").attr("data-param_type");
                            obj.param_values = $(item).find("input").attr("data-value");
                            obj.param_attribute = $(item).find("input").attr("data-attr");
                            obj.param_desc = $(item).find("input").val();
                            apply_condition.push(obj);
                        }
                    });
                    if($("#present_time input:checked").attr("id") == 'timelyCheck'){
                        present_time_type = {'type':'timely','date': ''};
                    }else if($("#present_time input:checked").attr("id") == 'setTimeCheck'){
                        present_time_type = {'type':'timing','date': $("#distribute_reward_date").val()};
                    }
                    if($("#onlineCouponCheck").prop("checked")){
                        $("#online_coupon>li").each(function (i,item) {
                            var obj = {};
                            obj.coupon_code = $(item).find("input").attr('data-code');
                            obj.coupon_type = $(item).find("input").attr('data-type');
                            obj.coupon_name = $(item).find("input").val();
                            coupons.push(obj);
                        });
                    }
                    $("#online_activity .online_item_wrap:visible dl:not(:first-child)").each(function (i,item) {
                        var obj = {};
                        obj.item_name = $(item).find("dt:nth-child(2) input").val().trim();
                        obj.limit_count = $(item).find("dt:nth-child(3) input").val().trim();
                        obj.item_picture = $(item).find("dt:nth-child(5) input").attr("data-src");
                        if($(item).find("dt:nth-child(4) input").eq(0).val() == "积分"){
                            obj.fee_points = $(item).find("dt:nth-child(4) input").eq(1).val().trim();
                            obj.fee_money = '';
                        }else if($(item).find("dt:nth-child(4) input").eq(0).val() == "金额(元)"){
                            obj.fee_points = "";
                            obj.fee_money = $(item).find("dt:nth-child(4) input").eq(1).val().trim();
                        }else {
                            obj.fee_points = $(item).find("dt:nth-child(4) input").eq(2).val().trim();
                            obj.fee_money = $(item).find("dt:nth-child(4) input").eq(1).val().trim();
                        }
                        $(item).find("dt:nth-child(2) input").hasClass("online_choose_shopper") ? obj.sku_id = $(item).find("dt:nth-child(2) input").attr("data-code") : '';
                        apply_type_info.push(obj);
                    });
                    _param["activity_url"] = $("#online_url").val();
                    _param['td_allow'] = $("#td_allow").val() == "是" ? "Y" : "N";
                    _param['td_end_time'] = $("#td_allow").val() == "是" ? $("#unsubscribe_date").val() : '';
                    _param['present_time'] = present_time_type;
                    _param['apply_condition'] = apply_condition;
                    _param['present_point'] = $("#onlinePointCheck").prop("checked") ? $("#online_point").val() : '';
                    _param['coupon_type'] = $("#onlineCouponCheck").prop("checked") ? coupons : [];
                    _param['batch_no'] = $("#online_batch_num").val().trim();
                    _param['apply_type'] = $("#online_item_type").val() == "商品" ? 'sku' : 'custom';
                    _param['apply_agreement'] = $("#useCheck").prop("checked") ? $("#reliefBtn").attr("data-src") : '';
                    _param['apply_type_info'] = apply_type_info;
                }
                oc.postRequire("post", "/vipActivity/detail/add", "0", _param, function (data) {
                    if (data.code == "0") {
                        def.resolve("成功");
                    } else if (data.code == "-1") {
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: data.message
                        });
                        def.resolve("失败");
                    }
                })
            } else if (data.code == -1) {
                console.log(data.message);
            }
            whir.loading.remove();
        });
        return def;
    },
    activityEdit: function () {
        var param = {
            "activity_code": activity.activity_code
        };
        whir.loading.add("", 0.5);
        oc.postRequire("post", "/vipActivity/select", "0", param, function (data) {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.activityVip);
            var type = "";
            var run_scope = JSON.parse(list.run_scope);
            var vip_condition = JSON.parse(list.vip_condition);
            _param = vip_condition;
            // var activity_store_code=JSON.parse(list.activity_store_code);
            var activity_store_code = run_scope.store_code;
            var brand_code = run_scope.brand_code;
            var area_code = run_scope.area_code;
            list.bill_status == "1" ? activity.edit_flag = true : false;
            list.bill_status == "1" ? $("#tabs>div:nth-of-type(2)").attr("data-state","true") : $("#tabs>div:nth-of-type(2)").attr("data-state","false");
            if (area_code.length == 0) {
                $("#area_num").val("全部");
            } else {
                $("#area_num").val("已选" + area_code.length + "个");
            }
            if (brand_code.length == 0) {
                $("#brand_num").val("全部");
            } else {
                $("#brand_num").val("已选" + brand_code.length + "个");
            }
            if (activity_store_code.length == 0) {
                $("#shop_num").val("全部");
            } else {
                $("#shop_num").val("已选" + activity_store_code.length + "个");
            }
            switch (list.run_mode) {
                case "招募活动":
                    type = "recruit";
                    break;
                case "网页活动":
                    type = "h5";
                    break;
                case "促销活动":
                    type = "sales";
                    break;
                case "优惠券活动":
                    type = "coupon";
                    break;
                case "线下报名活动":
                    type = "invite";
                    break;
                case "节日活动":
                    type = "festival";
                    break;
                case "邀请注册":
                    type = "register";
                    break;
                case "线上报名活动":
                    type = "online_apply";
                    break;
            }
            activity.getcorplist(list.corp_code);
            $("#activity_start").attr("onclick", "laydate({elem:'#activity_start',min:laydate.now(0, 'YYYY/MM/DD 00:00:00'),max: '" + list.end_time + "',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:checkStart})");
            $("#activity_start").val(list.start_time);
            $("#activity_end").attr("onclick", "laydate({elem:'#activity_end',min:'" + list.start_time + "',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:checkEnd})");
            $("#activity_end").val(list.end_time);
            $("#activity_theme").val(list.activity_theme);
            $("#activity_theme").attr("data-name", list.activity_theme);
            $("#activity_type").attr("data-id", type);
            $("#activity_type").val(list.run_mode);
            $("#activity_describe").val(list.activity_desc);
            $("#wechat_input").val(list.app_name);
            $("#wechat_input").attr("data-id", list.app_id);
            for (var i = 0; i < activity_store_code.length; i++) {
                if (i < activity_store_code.length - 1) {
                    activity.cache.store_codes += activity_store_code[i].store_code + ",";
                    activity.cache.store_names += activity_store_code[i].store_name + ",";
                } else {
                    activity.cache.store_codes += activity_store_code[i].store_code;
                    activity.cache.store_names += activity_store_code[i].store_name;
                }
            }
            for (var a = 0; a < brand_code.length; a++) {
                if (a < brand_code.length - 1) {
                    activity.cache.brand_codes += brand_code[a].brand_code + ",";
                    activity.cache.brand_names += brand_code[a].brand_name + ",";
                } else {
                    activity.cache.brand_codes += brand_code[a].brand_code;
                    activity.cache.brand_names += brand_code[a].brand_name;
                }
            }
            for (var b = 0; b < area_code.length; b++) {
                if (b < area_code.length - 1) {
                    activity.cache.area_codes += area_code[b].area_code + ",";
                    activity.cache.area_names += area_code[b].area_name + ",";
                } else {
                    activity.cache.area_codes += area_code[b].area_code;
                    activity.cache.area_names += area_code[b].area_name;
                }
            }
            $("#lookVipCondition").attr("data-code", JSON.stringify(vip_condition.screen));
            oc.postRequire("post", "/vipActivity/detail/select", "0", param, function (data) {
                if (data.code == 0) {
                    var msg = JSON.parse(data.message);
                    var list = msg.activityVip;
                    var type = list.activity_type;
                    if (type == "促销活动") {
                        $("#sales_activity").show();
                        $("#sales_url").val(list.activity_url);
                        $($("#sales_activity").find("input")[1]).val(list.sales_no);
                    }
                    if (type == "网页活动") {
                        $("#h5_activity").show();
                        $($("#h5_activity").find("input")[0]).val(list.activity_url);
                    }
                    if (type == "节日活动") {
                        $("#festival_activity").show();
                        $("#holiday_start").val(list.festival_start);
                        $("#holiday_end").val(list.festival_end);
                        $("#festival_url").val(list.activity_url);
                        $("#holiday_end").attr("onclick", "laydate({elem:'#holiday_end',min:'" + list.festival_start + "',max: '2099-12-31 23:59:59',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:holidayEnd})");
                        $("#holiday_start").attr("onclick", "laydate({elem:'#holiday_start',min:laydate.now(0, 'YYYY/MM/DD 00:00:00'),max: '" + list.festival_end + "',istime: true, format: 'YYYY-MM-DD hh:mm:ss',choose:holidayStart})");
                    }
                    if (type == "线下报名活动") {
                        $(".offline_activity").show();
                        if (list.activity_url == "") {
                            $("#invite_span").trigger("click");
                            $("#extend_url").hide();
                            $("#invite_wrap").show();
                            var twoCode = list.apply_qrcode;
                            if (twoCode !== "") {
                                $(".offline_right_wrap").empty().append("<img style='width: 100%' src='" + twoCode + "'>");
                            }
                            var len = $("#upload_logo").parent().prevAll("img").length;
                            if (list.apply_logo !== "") {
                                var img = "<img src='" + list.apply_logo + "' alt='暂无图片'>";
                                if (len == 0) {
                                    $("#upload_logo").parent().before(img);
                                } else {
                                    $("#upload_logo").parent().prev("img").replaceWith(img);
                                }
                            }
                            if (list.apply_allow_vip == "Y") {
                                $("#allow_vipAttend_btn")[0].checked = true;
                            } else {
                                $("#allow_vipAttend_btn")[0].checked = false;
                            }
                            $("#invite_title").val(list.apply_title);
                            $("#offline_end").val(list.apply_endtime);
                            $("#invite_summary").val(list.apply_desc);
                            $("#invite_message").val(list.apply_success_tips);
                        } else if (list.activity_url !== "") {
                            $("#url_mould").trigger("click");
                            $("#extend_url").show();
                            $("#invite_wrap").hide();
                            $("#invite_url").val(list.activity_url);
                        }
                    }
                    if (type == "招募活动") {
                        var recruit = JSON.parse(list.recruit);
                        var li = "";
                        $("#recruit_url").val(list.activity_url);
                        if (recruit.length > 1) {
                            for (var i = 0; i < recruit.length; i++) {
                                var display = "";
                                if (i < recruit.length - 1) {
                                    display = "style='display:none'";
                                } else {
                                    display = "style='display:inline-block'";
                                }
                                li += "<li><ul><li style='margin-right: 30px'><label style='color:#c26555;'>招募级别*</label><input class='text_input select_input' data-name='"+recruit[i].vip_card_type_name + '('+recruit[i].vip_card_type_code+")' value='" + recruit[i].vip_card_type_name + '('+recruit[i].vip_card_type_code+")' data-code='" + recruit[i].vip_card_type_code + "'>"
                                    + "<ul class='activity_select vipCardType'>"
                                    + "</ul></li><li>"
                                    + "<label style='color:#c26555;'>招募金额*</label><input placeholder='请输入招募最低消费额' value='" + recruit[i].join_threshold + "' class='text_input number_input'></li>"
                                    + "<li><span " + display + " class='add_recruit icon-ishop_6-01'></span><span class='remove_recruit icon-ishop_6-02'></span></li></ul></li>"
                            }
                            $("#recruit_activity .operate_ul").html(li);
                            $("#recruit_activity>ul>li:first-child").find(".remove_recruit").css("display", "inline-block");
                        } else {
                            $($("#recruit_activity>ul>li:first-child").find("input")[0]).val(recruit[0].vip_card_type_name+"("+recruit[0].vip_card_type_code+")");
                            $($("#recruit_activity>ul>li:first-child").find("input")[0]).attr("data-code", recruit[0].vip_card_type_code);
                            $($("#recruit_activity>ul>li:first-child").find("input")[0]).attr("data-name", recruit[0].vip_card_type_name+"("+recruit[0].vip_card_type_code+")");
                            $($("#recruit_activity>ul>li:first-child").find("input")[1]).val(recruit[0].join_threshold);
                        }
                        $("#recruit_activity").show();
                    }
                    if (type == "优惠券活动") {
                        var send_coupon_type = list.send_coupon_type;
                        $("#coupon_url").val(list.activity_url);
                        if (list.coupon_type == "") {
                            var coupon_type = list.coupon_type;
                        } else {
                            var coupon_type = JSON.parse(list.coupon_type);
                        }
                        if (send_coupon_type == undefined || send_coupon_type == "") {
                            $(".switch div").trigger("click");
                        } else {
                            switch (send_coupon_type) {
                                case "batch":
                                    $(".coupon_title li:first-child").trigger("click");
                                    var li = "";
                                    var display = "";
                                    var removeDisplay = "";
                                    if (list.present_point == "") {
                                        $("#batchPointCheck").prop("checked", false);
                                        $($(".choose_coupon")[0]).children(".presentPoint_wrap").hide();
                                    } else {
                                        $("#batchPointInput").val(list.present_point);
                                    }
                                    if (coupon_type.length > 0) {
                                        coupon_type.length == 1 ? "" : removeDisplay = "style='display:inline-block'";
                                        for (var i = 0; i < coupon_type.length; i++) {
                                            if (i < coupon_type.length - 1) {
                                                display = "style='display:none'";
                                            } else {
                                                display = "style='display:inline-block'";
                                            }
                                            li += "<li><ul><li><label style='color:#c26555;'>选择优惠券*</label><input class='text_input select_input' value='" + coupon_type[i].coupon_name + "' data-name='"+coupon_type[i].coupon_name+"' data-code='" + coupon_type[i].coupon_code + "' data-type='" + coupon_type[i].coupon_type + "' placeholder='请选择优惠券'>"
                                                + "<ul class='activity_select coupon_activity'></ul>"
                                                + "</li><li><span " + display + " class='add_btn icon-ishop_6-01'></span><span " + removeDisplay + " class='remove_btn icon-ishop_6-02'></span></li></ul></li>"
                                        }
                                        $($(".choose_coupon")[0]).children(".presentCoupon_wrap").children("ul").html(li);
                                        $("#batch_num_input").val(list.batch_no);
                                    } else {
                                        $("#batchCouponCheck").prop("checked", false);
                                        $($(".choose_coupon")[0]).children(".presentCoupon_wrap").hide()
                                    }
                                    break;
                                case "anniversary":
                                    $(".coupon_title li:nth-child(3)").trigger("click");
                                    var ul = "";
                                    var display = "";
                                    for (var i = 0; i < coupon_type.length; i++) {
                                        var li = "";
                                        var coupon = JSON.parse(coupon_type[i].coupon_type);
                                        var pointcheck = "";
                                        var pointDisplay = "";
                                        var couponcheck = "";
                                        var couponDisplay = "";
                                        var anniversary_time = coupon_type[i].anniversary_time == "D" ? "当日" : "当月";
                                        coupon_type[i].send_points == "" ? pointDisplay = "style='display:none'" : pointcheck = 'checked="checked"';
                                        coupon_type[i].coupon_type.length == 0 ? couponDisplay = "style='display:none'" : couponcheck = 'checked="checked"';
                                        if (coupon.length != 0) {
                                            for (var j = 0; j < coupon.length; j++) {
                                                var display = "";
                                                var removeStyle = "";
                                                j == 0 && coupon.length != 1 ? removeStyle = "style='display:inline-block'" : "";
                                                if (j < coupon.length - 1) {
                                                    display = "style='display:none'";
                                                } else {
                                                    display = "style='display:inline-block'";
                                                }
                                                li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" value="' + coupon[j].coupon_name + '" data-name="'+coupon[j].coupon_name+'" data-code="' + coupon[j].coupon_code + '" data-type="' + coupon[j].coupon_type + '" placeholder="请选择优惠券" type="text">'
                                                    + '<ul class="activity_select coupon_activity"></ul></li><li><span ' + display + ' class="add_btn icon-ishop_6-01"></span><span ' + removeStyle + ' class="remove_btn icon-ishop_6-02"></span></li>'
                                                    + '</ul></li>';
                                            }
                                        } else {
                                            li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text">'
                                                + '<ul class="activity_select coupon_activity"></ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li>'
                                                + '</ul></li>';
                                        }
                                        ul += '<div class="coupon_details_wrap"><div class="cardTypeWrap">'
                                            + '<ul><li><label style="color:#c26555;">纪念日类型*</label><input value="' + coupon_type[i].param_desc + '" data-name="' + coupon_type[i].param_name + '" placeholder="请选择纪念日类型" class="text_input select_input"  type="text" readonly="readonly"><ul class="activity_select souvenir_type">'
                                            + activity.cache.souvenir_type
                                            + '</ul></li><li style="margin-left: 10px"><label style="color:#c26555;">执行时间*</label><input value="' + anniversary_time + '" class="text_input select_input" placeholder="选择发券日期范围" type="text" readonly>'
                                            + '<ul class="activity_select"><li>当日</li><li>当月</li></ul></li>'
                                            + '</ul></div><div class="consumeCheckWrap"><label>开卡赠送</label><div class="checkbox1">'
                                            + '<span><input type="checkbox" ' + pointcheck + ' class="check pointCheck" id="souvenirPointCheck' + i + '"><label for="souvenirPointCheck' + i + '"></label>积分</span><span><input type="checkbox" ' + couponcheck + ' class="check couponCheck" id="souvenirCouponCheck' + i + '"><label for="souvenirCouponCheck' + i + '"></label>优惠券</span>'
                                            + '</div></div><div class="presentPoint_wrap" ' + pointDisplay + '><label style="color:#c26555;">赠送积分*</label><input value="' + coupon_type[i].send_points + '" class="input130px text_input numberInput cardPointInput" type="text"></div>'
                                            + '<div class="presentCoupon_wrap" ' + couponDisplay + '><ul class="operate_ul">'
                                            + li
                                            + '</ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号" value="'+coupon_type[i].batch_no+'"></div></div><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
                                    }
                                    $('#souvenirContentWrap').html(ul);
                                    break;
                                case "card":
                                    $(".coupon_title li:nth-child(2)").trigger("click");
                                    var ul = "";
                                    var display = "";
                                    for (var i = 0; i < coupon_type.length; i++) {
                                        var li = "";
                                        var display = "";
                                        var pointcheck = "";
                                        var pointDisplay = "";
                                        var couponcheck = "";
                                        var couponDisplay = "";
                                        coupon_type[i].present_point == "" ? pointDisplay = "style='display:none'" : pointcheck = 'checked="checked"';
                                        var coupon_code = coupon_type[i].coupon_code.split(",");
                                        var coupon_name = coupon_type[i].coupon_name.split(",");
                                        var coupontype = coupon_type[i].coupon_type.split(",");
                                        if (coupon_type[i].coupon_code == "") {
                                            couponDisplay = "style='display:none'";
                                            li += "<li><ul><li><label style='color:#c26555;'>选择优惠券*</label><input class='text_input select_input' data-code='' data-type='' placeholder='请选择优惠券'>"
                                                + "<ul class='activity_select coupon_activity'></ul></li>"
                                                + "<li><span class='add_btn icon-ishop_6-01'></span><span class='remove_btn icon-ishop_6-02'></span></li></ul></li>";
                                        } else {
                                            couponcheck = 'checked="checked"';
                                            for (var j = 0; j < coupon_code.length; j++) {
                                                if (j < coupon_code.length - 1) {
                                                    display = "style='display:none'";
                                                } else {
                                                    display = "style='display:inline-block'";
                                                }
                                                li += "<li><ul><li><label style='color:#c26555;'>选择优惠券*</label><input class='text_input select_input' value='" + coupon_name[j] + "' data-name='"+coupon_name[j]+"' data-code='" + coupon_code[j] + "' data-type='" + coupontype[j] + "' placeholder='请选择优惠券'>"
                                                    + "<ul class='activity_select coupon_activity'></ul></li>"
                                                    + "<li><span " + display + " class='add_btn icon-ishop_6-01'></span><span class='remove_btn icon-ishop_6-02'></span></li></ul></li>";
                                            }
                                        }
                                        ul += '<div class="coupon_details_wrap"><div class="cardTypeWrap"><ul><li style="margin-right: 5px"><label style="color:#c26555;">卡类型*</label><input value="' + coupon_type[i].vip_card_type_name+'('+coupon_type[i].vip_card_type_code+')" data-name="'+coupon_type[i].vip_card_type_name+'('+coupon_type[i].vip_card_type_code+')" data-code="' + coupon_type[i].vip_card_type_code + '" class="text_input select_input" data-code="" type="text">'
                                            + '<ul class="activity_select vipCardType"></ul></li></ul></div>'
                                            + '<div class="consumeCheckWrap"><label>开卡赠送</label><div class="checkbox1">'
                                            + '<span><input type="checkbox" ' + pointcheck + ' class="check pointCheck" id="cardPointCheck' + i + '"><label for="cardPointCheck' + i + '"></label>积分</span><span><input type="checkbox" ' + couponcheck + ' class="check couponCheck" id="cardCouponCheck' + i + '"><label for="cardCouponCheck' + i + '"></label>优惠券</span>'
                                            + '</div></div>'
                                            + '<div class="presentPoint_wrap" ' + pointDisplay + '><label style="color:#c26555;">赠送积分*</label><input value="' + coupon_type[i].present_point + '" class="input130px text_input numberInput cardPointInput" type="text"></div>'
                                            + '<div class="presentCoupon_wrap" ' + couponDisplay + '><ul class="operate_ul">'
                                            + li
                                            + '</ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号" value="'+coupon_type[i].batch_no+'"></div></div><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
                                    }
                                    $('#cardContentWrap').html(ul);
                                    break;
                                case "consume":
                                    $(".coupon_title li:nth-child(4)").trigger("click");
                                    $("#attendVipTotal").val(list.join_count);
                                    var html = "";
                                    var display = "";
                                    var consume_condition = JSON.parse(list.consume_condition);
                                    for (var i = 0; i < consume_condition.length; i++) {
                                        var pointcheck = "";
                                        var pointDisplay = "";
                                        var couponcheck = "";
                                        var couponDisplay = "";
                                        var coupon = JSON.parse(consume_condition[i].coupon_type);
                                        consume_condition[i].send_points == "" ? pointDisplay = "style='display:none'" : pointcheck = 'checked="checked"';
                                        var li = "";
                                        if (coupon.length != 0) {
                                            couponcheck = 'checked="checked"';
                                            for (var j = 0; j < coupon.length; j++) {
                                                var removeStyle = "";
                                                j == 0 ? removeStyle = "style='display:inline-block'" : "";
                                                if (j < coupon.length - 1) {
                                                    display = "style='display:none'";
                                                } else {
                                                    display = "style='display:inline-block'";
                                                }
                                                li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" value="' + coupon[j].coupon_name + '" data-name="'+coupon[j].coupon_name+'" data-code="' + coupon[j].coupon_code + '" data-type="' + coupon[j].coupon_type + '" placeholder="请选择优惠券" type="text">'
                                                    + '<ul class="activity_select coupon_activity">'
                                                    + activity.cache.coupon
                                                    + '</ul></li><li><span ' + display + ' class="add_btn icon-ishop_6-01"></span><span ' + removeStyle + ' class="remove_btn icon-ishop_6-02"></span></li></ul></li>'
                                            }
                                        } else {
                                            couponDisplay = "style='display:none'";
                                            li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text">'
                                                + '<ul class="activity_select coupon_activity">'
                                                + activity.cache.coupon
                                                + '</ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li></ul></li>'
                                        }
                                        html += '<div class="coupon_details_wrap"><div class="ConsumerHeader"><span class="levels"><label style="color:#c26555;">优先级*</label><input value="' + consume_condition[i].priority + '" class="text_input select_input" type="text" readonly="readonly"><ul class="activity_select levelSelect"><li>1</li><li>2</li><li>3</li><li>4</li><li>5</li><li>6</li><li>7</li><li>8</li><li>9</li></ul></span>'
                                            + '<div><label>&emsp;&emsp;商品</label><span class="consumeBtn" data-id="' + consume_condition[i].consume_goods + '">选择商品</span><span class="lookCondition" data-code=' + consume_condition[i].goods_condition + '>查看条件<i class="icon-ishop_8-03"></i></span></div>'
                                            + '<div><input class="input50px numberInput" value="' + consume_condition[i].trade_start + '" type="text"><label>≤ 整单金额(元) <</label><input value="' + consume_condition[i].trade_end + '" class="input50px numberInput" type="text"><br />'
                                            + '<input value="' + consume_condition[i].num_start + '" class="input50px numberInput" type="text"><label>≤ 购买件数(件) <</label><input value="' + consume_condition[i].num_end + '" class="input50px numberInput" type="text"></div>'
                                            + '<div><input value="' + consume_condition[i].discount_start + '" class="input50px number_input discount_num_input" type="text"><label>≤ 消费折扣(折:0-10) <</label><input value="' + consume_condition[i].discount_end + '" class="input50px number_input discount_num_input" type="text"><br /><label>会员参与次数</label><input value="' + consume_condition[i].vip_join_count + '" class="input130px numberInput" type="text">次</div></div>'
                                            + '<div class="ConsumerFooter"><div class="consumeCheckWrap"><label>消费后赠送</label><div class="checkbox1">'
                                            + '<span><input type="checkbox" ' + pointcheck + 'class="check pointCheck" id="pointCheck' + i + '"><label for="pointCheck' + i + '"></label>积分</span><span><input type="checkbox" ' + couponcheck + ' class="check couponCheck" id="couponCheck' + i + '"><label for="couponCheck' + i + '"></label>优惠券</span></div></div>'
                                            + '<div><div class="presentPoint_wrap" ' + pointDisplay + '><label style="color:#c26555;">赠送积分*</label><input class="input130px text_input numberInput" value="' + consume_condition[i].send_points + '" type="text"></div><div class="presentCoupon_wrap" ' + couponDisplay + '><ul class="operate_ul">'
                                            + li
                                            + '</ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号" value="'+consume_condition[i].batch_no+'"></div></div></div></div><i class="icon-ishop_6-12 coupon_details_close"></i></div>';
                                    }
                                    $("#consumeCouponWrap").empty().append(html);
                            }
                        }
                        $("#coupon_activity").show();
                    }
                    if (type == "邀请注册") {
                        $("#register").show();
                        var register = JSON.parse(list.register_data);
                        var html = "";
                        for (var i = 0; i < register.length; i++) {
                            var n = i + 1;
                            var coupon = register[i].present.coupon;
                            var li = "";
                            var pointcheck = "";
                            var pointDisplay = "";
                            var couponcheck = "";
                            var couponDisplay = "";
                            register[i].present.point == "" ? pointDisplay = "style='display:none'" : pointcheck = 'checked="checked"';
                            register[i].present.coupon.length == 0 ? couponDisplay = "style='display:none'" : couponcheck = 'checked="checked"';
                            if (coupon.length != 0) {
                                for (var j = 0; j < coupon.length; j++) {
                                    var display = "";
                                    var removeStyle = "";
                                    j == 0 && coupon.length != 1 ? removeStyle = "style='display:inline-block'" : "";
                                    if (j < coupon.length - 1) {
                                        display = "style='display:none'";
                                    } else {
                                        display = "style='display:inline-block'";
                                    }
                                    li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" value="' + coupon[j].coupon_name + '" data-name="'+coupon[j].coupon_name+'" data-code="' + coupon[j].coupon_code + '" data-type="' + coupon[j].coupon_type + '" placeholder="请选择优惠券" type="text">'
                                        + '<ul class="activity_select coupon_activity"></ul></li><li><span ' + display + ' class="add_btn icon-ishop_6-01"></span><span ' + removeStyle + ' class="remove_btn icon-ishop_6-02"></span></li>'
                                        + '</ul></li>';
                                }
                            } else {
                                li += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" data-code="" placeholder="请选择优惠券" type="text">'
                                    + '<ul class="activity_select coupon_activity"></ul></li><li><span class="add_btn icon-ishop_6-01"></span><span class="remove_btn icon-ishop_6-02"></span></li>'
                                    + '</ul></li>';
                            }
                            html += '<div class="coupon_details_wrap"><div class="coupon_details_wrap_header">计划' + n + '</div>'
                                + '<div><label style="color:#c26555;">人数区间*</label><input value="' + register[i].number_interval.start + '" class="text_input input50px numberInput" type="text">~<input value="' + register[i].number_interval.end + '" class="text_input input50px numberInput" type="text">人<label class="marginLeft100">每邀请</label><input value="' + register[i].invite + '" class="text_input input50px numberInput" type="text">人</div>'
                                + '<div><div class="coupon_details_wrap_left"><label>赠送</label></div>'
                                + '<div class="coupon_details_wrap_right"><div class="giftWrap"><div><div class="consumeCheckWrap"><div class="checkbox1">'
                                + '<span><input type="checkbox" ' + pointcheck + ' class="check pointCheck" id="registerPointCheck' + i + '"><label for="registerPointCheck' + i + '"></label>积分</span><span><input type="checkbox" ' + couponcheck + ' class="check couponCheck" id="registerCouponCheck' + i + '"><label for="registerCouponCheck' + i + '"></label>优惠券</span></div></div>'
                                + '<div class="presentPoint_wrap" ' + pointDisplay + '><label style="color:#c26555;">赠送积分*</label><input value="' + register[i].present.point + '" class="input130px text_input numberInput cardPointInput" type="text"></div>'
                                + '<div class="presentCoupon_wrap" ' + couponDisplay + '><ul class="operate_ul">'
                                + li
                                + '</ul><div class="batch_num"><label></label><input type="text" class="input130px text_input bath_num_input" placeholder="请输入批次号" value="'+register[i].batch_no+'"></div></div></div></div></div></div>'
                                + '<i class="icon-ishop_6-12 coupon_details_close"></i></div>';
                        }
                        $("#registerContentWrap").html(html);
                    }
                    if (type == '线上报名活动'){
                        var apply_condition = JSON.parse(list.apply_condition),
                            TempHead = list.apply_type != "sku" ? "itemCheckHead" : "itemCheckHeads",
                            apply_type_info = JSON.parse(list.apply_type_info),
                            item_temp = '<dl><dt><input type="checkbox" id="'+TempHead+'"><label for="'+TempHead+'"></label></dt><dt>项目名称</dt><dt>人数上限</dt><dt>报名费用</dt><dt>项目图片</dt></dl>',
                            condition_temp = '',
                            coupon_temp = '',
                            coupon = JSON.parse(list.coupon_type);
                        $("#online_activity").show();
                        $("#online_url").val(list.activity_url);
                        $("#online_batch_num").val(list.batch_no);
                        $("#online_item_type").val(list.apply_type == "sku" ? "商品" : "自定义项目");
                        list.apply_agreement == '' ? $("#noUseCheck").prop("checked",true) : $("#reliefBtn").attr("data-src",list.apply_agreement);
                        list.apply_agreement == '' ? $("#noUseCheck").prop("checked",true) : $("#reliefView").attr("data-src",list.apply_agreement);
                        list.present_point == '' ? $("#onlinePointCheck").prop("checked",false) && $("#online_activity .presentPoint_wrap").hide() : $("#online_point").val(list.present_point);
                        if(JSON.parse(list.present_time).type == 'timely'){
                            $("#timelyCheck").prop("checked",true)
                        }else {
                            $("#setTimeCheck").prop("checked",true);
                            $("#distribute_reward_date").show();
                            $("#distribute_reward_date").val(JSON.parse(list.present_time).date);
                        }
                        if(list.td_allow == "Y"){
                            $("#unsubscribe_date").val(list.td_end_time);
                            $("#td_allow").val("是");
                        }else {
                            $("#unsubscribe_date").parents("li").hide();
                            $("#td_allow").val("否");
                        }
                        if(apply_condition.length != 0){
                            for(var i=0;i<apply_condition.length;i++){
                                var display = "";
                                var removeStyle = "";
                                i == 0 && apply_condition.length != 1 ? removeStyle = "style='display:inline-block'" : "";
                                if (i < apply_condition.length - 1) {
                                    display = "style='display:none'";
                                } else {
                                    display = "style='display:inline-block'";
                                }
                                condition_temp += '<li><ul><li><label style="color:#c26555;">字段名称*</label><input class="text_input select_input" data-attr="'+apply_condition[i].param_attribute+'" data-value="'+apply_condition[i].param_values+'" data-param_type="'+apply_condition[i].param_type+'" data-require="'+apply_condition[i].required+'" data-name="'+apply_condition[i].param_name+'" value="'+apply_condition[i].param_desc+'" placeholder="请选择" type="text" readonly="readonly">'
                                    + '<ul class="activity_select" style="display: none;"></ul></li><li><span '+display+' class="add_btn icon-ishop_6-01"></span><span '+removeStyle+' class="remove_btn icon-ishop_6-02"></span></li></ul></li>'
                            }
                            $("#apply_condition .operate_ul").html(condition_temp);
                        }
                        if(coupon.length != 0){
                            for(var i = 0; i < coupon.length;i++){
                                var display = "";
                                var removeStyle = "";
                                i == 0 && coupon.length != 1 ? removeStyle = "style='display:inline-block'" : "";
                                if (i < coupon.length - 1) {
                                    display = "style='display:none'";
                                } else {
                                    display = "style='display:inline-block'";
                                }
                                coupon_temp += '<li><ul><li><label style="color:#c26555;">选择优惠券*</label><input class="text_input select_input" value="'+coupon[i].coupon_name+'" data-code="'+coupon[i].coupon_code+'" data-type="'+coupon[i].coupon_type+'" placeholder="请选择优惠券" type="text" readonly="readonly">'
                                    + '<ul class="activity_select coupon_activity"></ul></li><li><span '+display+' class="add_btn icon-ishop_6-01"></span><span '+removeStyle+' class="remove_btn icon-ishop_6-02"></span></li></ul></li>'
                            }
                            $("#online_coupon").html(coupon_temp);
                        }else {
                            $("#onlineCouponCheck").prop("checked",false);
                            $("#online_activity .presentCoupon_wrap").hide();
                        }
                        if(apply_type_info.length != 0){
                            for(var i = 0; i < apply_type_info.length; i++){
                                var Temp2 = '',
                                    Temp1 = '';
                                if(list.apply_type == "sku"){
                                    Temp2 = '<dt><input class="text_input select_input online_choose_shopper" data-code="'+apply_type_info[i].sku_id+'" value="'+apply_type_info[i].item_name+'" type="text" placeholder="请选择商品" readonly></dt>';
                                }else {
                                    Temp2 = '<dt><input class="text_input" value="'+apply_type_info[i].item_name+'" type="text" maxlength="200"></dt>';
                                }
                                if(apply_type_info[i].fee_points != '' && apply_type_info[i].fee_money != ''){
                                    Temp1 = '<dt><span><input value="金额或积分" class="text_input select_input" type="text" readonly><ul class="activity_select item_present_select"><li>积分</li><li>金额(元)</li><li>金额或积分</li></ul></span><span><span class="consumeWrap"><input type="text" class="text_input number_input" value="'+apply_type_info[i].fee_money+'"><span class="unit">元</span></span> 或 <span class="consumeWrap"><input type="text" class="text_input number_input" value="'+apply_type_info[i].fee_points+'"><span class="unit">积分</span></span></span></dt>';
                                }else if(apply_type_info[i].fee_money == ''){
                                    Temp1 = '<dt><span><input value="积分" class="text_input select_input" type="text" readonly><ul class="activity_select item_present_select"><li>积分</li><li>金额(元)</li><li>金额或积分</li></ul></span><span><input class="text_input number_input" type="text" value="'+apply_type_info[i].fee_points+'"><span class="unit">积分</span></span></dt>';
                                }else {
                                    Temp1 = '<dt><span><input value="金额(元)" class="text_input select_input" type="text" readonly><ul class="activity_select item_present_select"><li>积分</li><li>金额(元)</li><li>金额或积分</li></ul></span><span><input class="text_input number_input" type="text" value="'+apply_type_info[i].fee_money+'"><span class="unit">元</span></span></dt>';
                                }
                                item_temp +='<dl><dt><input type="checkbox" id="itemCheckHead'+i+'"><label for="itemCheckHead'+i+'"></label></dt>'
                                    + Temp2
                                    +'<dt><input type="text" class="text_input number_input" value="'+apply_type_info[i].limit_count+'"></dt>'
                                    + Temp1
                                    +'<dt><span><span class="chooseVipBtn">上传文件</span><input type="file"  accept="image/*" data-src="'+apply_type_info[i].item_picture+'"></span><a>预览<i class="icon-ishop_8-03"></i></a></dt></dl>';
                            }
                            if(list.apply_type == "sku"){
                                $("#online_shop_wrap").html(item_temp);
                            }else {
                                $("#online_item_wrap").html(item_temp);
                                $("#shopper_item").hide();
                                $("#custom_item").show();
                            }
                        }
                    }
                }
                whir.loading.remove();
            });
        });
    },
    filterEvent: function () {
        var self = this;
        //员工里面的区域点击
        $("#staff_area").click(function () {
            self.vipcache.clickMark = "user";
            if (self.vipcache.area_codes !== "") {
                var area_codes = self.vipcache.area_codes.split(',');
                var area_names = self.vipcache.area_names.split(',');
                var area_html_right = "";
                for (var h = 0; h < area_codes.length; h++) {
                    area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            \</li>"
                }
                $("#screen_area .s_pitch span").eq(0).html(h);
                $("#screen_area .screen_content_r ul").html(area_html_right);
            } else {
                $("#screen_area .s_pitch span").eq(0).html("0");
                $("#screen_area .screen_content_r ul").empty();
            }
            self.isscroll = false;
            self.area_num = 1;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").show();
            $("#screen_staff").hide();
            self.getarealist(self.area_num);
        });
        //员工里面的店铺点击
        $("#staff_shop").click(function () {
            self.vipcache.clickMark = "user";
            if (self.vipcache.store_codes !== "") {
                var store_codes = self.vipcache.store_codes.split(',');
                var store_names = self.vipcache.store_names.split(',');
                var shop_html_right = "";
                for (var h = 0; h < store_codes.length; h++) {
                    shop_html_right += "<li id='" + store_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_codes[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").eq(0).html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            } else {
                $("#screen_shop .s_pitch span").eq(0).html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            self.isscroll = false;
            self.shop_num = 1;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            $("#screen_shop").show();
            $("#screen_staff").hide();
            self.getstorelist(self.shop_num);
        });
        //员工里面的品牌点击
        $("#staff_brand").click(function () {
            self.vipcache.clickMark = "user";
            if (self.vipcache.brand_codes !== "") {
                var brand_codes = self.vipcache.brand_codes.split(',');
                var brand_names = self.vipcache.brand_names.split(',');
                var brand_html_right = "";
                for (var h = 0; h < brand_codes.length; h++) {
                    brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").eq(0).html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            } else {
                $("#screen_brand .s_pitch span").eq(0).html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_staff").hide();
            self.getbrandlist();
        });
        //筛选弹框关闭
        $("#screen_vipWrapper_close").click(function () {
            $("#screen_vipWrapper").hide();
            $("#p").hide();
            $(document.body).css("overflow", "auto");
        });
        //弹出导购框/关闭／搜索／确定
        $(".screen_staffl").click(function () {
            self.vipcache.clickMark = "";
            if (self.vipcache.user_codes !== "") {
                var user_codes = self.vipcache.user_codes.split(',');
                var user_names = self.vipcache.user_names.split(',');
                var staff_html_right = "";
                for (var h = 0; h < user_codes.length; h++) {
                    staff_html_right += "<li id='" + user_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + user_codes[h] + "'  data-storename='" + user_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + user_names[h] + "</span>\
            \</li>"
                }
                $("#screen_staff .s_pitch span").eq(0).html(h);
                $("#screen_staff .screen_content_r ul").html(staff_html_right);
            } else {
                $("#screen_staff .s_pitch span").eq(0).html("0");
                $("#screen_staff .screen_content_r ul").empty();
            }
            staff_num = 1;
            isscroll = false;
            $("#screen_staff").show();
            $("#screen_vipWrapper").hide();
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            // $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-phone="" data-storename="无" name="test" class="check" id="checkboxFourInputwu"><label for="checkboxFourInputwu"></label></div><span class="p16">无</span></li>');
            self.getstafflist(staff_num);
        });
        $("#screen_close_staff").click(function () {
            $("#screen_staff").hide();
            $("#screen_vipWrapper").show();
        });
        $("#staff_search").keydown(function () {
            var event = event || window.event || arguments[0];
            staff_num = 1;
            if (event.keyCode == 13) {
                isscroll = false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                self.getstafflist(staff_num);
            }
        });
        $("#staff_search_f").click(function () {
            staff_num = 1;
            isscroll = false;
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            self.getstafflist(staff_num);
        });
        $("#screen_que_staff").click(function () {
            var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            var user_codes = "";
            var user_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    user_codes += r + ",";
                    user_names += p + ",";
                } else {
                    user_codes += r;
                    user_names += p;
                }
            }
            self.vipcache.user_codes = user_codes;
            self.vipcache.user_names = user_names;
            $("#screen_staff").hide();
            $("#screen_vipWrapper").show();
            $(".screen_staff_num").val("已选" + li.length + "个");
            $(".screen_staff_num").attr("data-code", user_codes);
            $(".screen_staff_num").attr("data-name", user_names);
        });
        //弹出区域框／关闭／搜索／确定
        $("#screen_areal").click(function () {
            self.vipcache.clickMark = "";
            if (self.vipcache.area_codes !== "") {
                var area_codes = self.vipcache.area_codes.split(',');
                var area_names = self.vipcache.area_names.split(',');
                var area_html_right = "";
                for (var h = 0; h < area_codes.length; h++) {
                    area_html_right += "<li id='" + area_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + area_codes[h] + "'  data-storename='" + area_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + area_names[h] + "</span>\
            \</li>"
                }
                $("#screen_area .s_pitch span").eq(0).html(h);
                $("#screen_area .screen_content_r ul").html(area_html_right);
            } else {
                $("#screen_area .s_pitch span").eq(0).html("0");
                $("#screen_area .screen_content_r ul").empty();
            }
            self.area_num = 1;
            self.isscroll = false;
            $("#screen_area").show();
            $("#screen_vipWrapper").hide();
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            self.getarealist(self.area_num);
        });
        $("#screen_close_area").click(function () {
            $("#screen_area").hide();
            if (self.popUp == "") {
                $("#screen_wrapper").show();
            } else {
                if (self.vipcache.clickMark == "user") {
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
            }
        });
        $("#area_search").keydown(function () {
            var event = event || window.event || arguments[0];
            self.area_num = 1;
            if (event.keyCode == 13) {
                self.isscroll = false;
                $("#screen_area .screen_content_l").unbind("scroll");
                $("#screen_area .screen_content_l ul").empty();
                self.getarealist(self.area_num);
            }
        });
        $("#area_search_f").click(function () {
            self.area_num = 1;
            self.isscroll = false;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            self.getarealist(self.area_num);
        });
        $("#screen_que_area").click(function () {
            var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            var area_codes = "";
            var area_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    area_codes += r + ",";
                    area_names += p + ",";
                } else {
                    area_codes += r;
                    area_names += p;
                }
            }
            if (self.popUp == "") {
                if (li.length == 0) {
                    $("#area_num").val("全部");
                } else {
                    $("#area_num").val("已选" + li.length + "个");
                }
                self.cache.area_codes = area_codes;
                self.cache.area_names = area_names;
                self.isscroll = false;
                $("#screen_area").hide();
                $("#screen_wrapper").show();
                $("#area_num").attr("data-areacode", area_codes);
            } else {
                self.vipcache.area_codes = area_codes;
                self.vipcache.area_names = area_names;
                $("#screen_area").hide();
                if (self.vipcache.clickMark == "user") {
                    staff_num = 1;
                    isscroll = false;
                    $("#screen_staff .screen_content_l").unbind("scroll");
                    $("#screen_staff .screen_content_l ul").empty();
                    self.getstafflist(staff_num);
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
                $("#screen_area_num").val("已选" + li.length + "个");
                $("#screen_area_num").attr("data-code", area_codes);
                $("#screen_area_num").attr("data-name", area_names);
                $(".area_num").val("已选" + li.length + "个");
            }
        });
        //弹出品牌框／关闭／搜索／确定
        $("#screen_brandl").click(function () {
            self.vipcache.clickMark = "";
            if (self.vipcache.brand_codes !== "") {
                var brand_codes = self.vipcache.brand_codes.split(',');
                var brand_names = self.vipcache.brand_names.split(',');
                var brand_html_right = "";
                for (var h = 0; h < brand_codes.length; h++) {
                    brand_html_right += "<li id='" + brand_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + brand_codes[h] + "'  data-storename='" + brand_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + brand_names[h] + "</span>\
            \</li>"
                }
                $("#screen_brand .s_pitch span").eq(0).html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            } else {
                $("#screen_brand .s_pitch span").eq(0).html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#screen_brand").show();
            $("#screen_vipWrapper").hide();
            $("#screen_brand .screen_content_l ul").empty();
            self.getbrandlist();
        });
        $("#screen_close_brand").click(function () {
            $("#screen_brand").hide();
            if (self.popUp == "") {
                $("#screen_wrapper").show();
            } else {
                if (self.vipcache.clickMark == "user") {
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
            }
        });
        $("#brand_search").keydown(function () {
            var event = event || window.event || arguments[0];
            if (event.keyCode == 13) {
                $("#screen_brand .screen_content_l ul").empty();
                self.getbrandlist();
            }
        });
        $("#brand_search_f").click(function () {
            $("#screen_brand .screen_content_l ul").empty();
            self.getbrandlist();
        });
        $("#screen_que_brand").click(function () {
            var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            var brand_codes = "";
            var brand_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    brand_codes += r + ",";
                    brand_names += p + ",";
                } else {
                    brand_codes += r;
                    brand_names += p;
                }
            }
            if (self.popUp == "") {
                if (li.length == 0) {
                    $("#brand_num").val("全部");
                } else {
                    $("#brand_num").val("已选" + li.length + "个");
                }
                activity.cache.brand_codes = brand_codes;
                activity.cache.brand_names = brand_names;
                activity.isscroll = false;
                $("#screen_brand").hide();
                $("#screen_wrapper").show();
            } else {
                self.vipcache.brand_codes = brand_codes;
                self.vipcache.brand_names = brand_names;
                $("#screen_brand").hide();
                if (self.vipcache.clickMark == "user") {
                    staff_num = 1;
                    isscroll = false;
                    $("#screen_staff .screen_content_l").unbind("scroll");
                    $("#screen_staff .screen_content_l ul").empty();
                    self.getstafflist(staff_num);
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
                $("#screen_brand_num").val("已选" + li.length + "个");
                $("#screen_brand_num").attr("data-code", brand_codes);
                $("#screen_brand_num").attr("data-name", brand_names);
                $(".brand_num").val("已选" + li.length + "个");
            }
        });
        //弹出店铺框／关闭／搜索／确定
        $("#screen_shopl").click(function () {
            self.vipcache.clickMark = "";
            if (self.vipcache.store_codes !== "") {
                var store_codes = self.vipcache.store_codes.split(',');
                var store_names = self.vipcache.store_names.split(',');
                var shop_html_right = "";
                for (var h = 0; h < store_codes.length; h++) {
                    shop_html_right += "<li id='" + store_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + store_codes[h] + "'  data-storename='" + store_names[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + store_names[h] + "</span>\
            \</li>"
                }
                $("#screen_shop .s_pitch span").eq(0).html(h);
                $("#screen_shop .screen_content_r ul").html(shop_html_right);
            } else {
                $("#screen_shop .s_pitch span").eq(0).html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            self.shop_num = 1;
            self.isscroll = false;
            $("#screen_shop").show();
            $("#screen_vipWrapper").hide();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            self.getstorelist(self.shop_num);
        });
        $("#screen_close_shop").click(function () {
            $("#screen_shop").hide();
            if (self.popUp == "") {
                $("#screen_wrapper").show();
            } else {
                if (self.vipcache.clickMark == "user") {
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
            }
        });
        $("#store_search").keydown(function () {
            var event = event || window.event || arguments[0];
            self.shop_num = 1;
            if (event.keyCode == 13) {
                self.isscroll = false;
                $("#screen_shop .screen_content_l ul").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                self.getstorelist(self.shop_num);
            }
        });
        $("#store_search_f").click(function () {
            self.shop_num = 1;
            self.isscroll = false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            self.getstorelist(self.shop_num);
        });
        $("#screen_que_shop").click(function () {
            var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            var store_codes = "";
            var store_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    store_codes += r + ",";
                    store_names += p + ",";
                } else {
                    store_codes += r;
                    store_names += p;
                }
            }
            if (self.popUp == "") {
                if (li.length == 0) {
                    $("#shop_num").val("全部");
                } else {
                    $("#shop_num").val("已选" + li.length + "个");
                }
                activity.cache.store_codes = store_codes;
                activity.cache.store_names = store_names;
                $("#screen_shop").hide();
                $("#screen_wrapper").show();
            } else {
                self.vipcache.store_codes = store_codes;
                self.vipcache.store_names = store_names;
                $("#screen_shop").hide();
                if (self.vipcache.clickMark == "user") {
                    self.shop_num = 1;
                    self.isscroll = false;
                    $("#screen_staff .screen_content_l").unbind("scroll");
                    $("#screen_staff .screen_content_l ul").empty();
                    self.getstafflist(self.shop_num);
                    $("#screen_staff").show();
                } else {
                    $("#screen_vipWrapper").show();
                }
                $("#screen_shop_num").val("已选" + li.length + "个");
                $("#screen_shop_num").attr("data-code", store_codes);
                $("#screen_shop_num").attr("data-name", store_names);
                $("#staff_shop_num").val("已选" + li.length + "个");
            }
        });
        //分组弹窗/搜索/确定
        $("#vipTask_group_icon").click(function () {
            if (self.vipcache.group_codes !== "") {
                var group_codes = self.vipcache.group_codes.split(',');
                var group_names = self.vipcache.group_name.split(',');
                var group_html_right = "";
                for (var h = 0; h < group_codes.length; h++) {
                    group_html_right += "<li id='" + group_codes[h] + "'>\
            <div class='checkbox1'><input type='checkbox' value='" + group_codes[h] + "' name='test' class='check'>\
            <label></div><span class='p16'>" + group_names[h] + "</span>\
            \</li>"
                }
                $("#screen_group .s_pitch span").eq(0).html(h);
                $("#screen_group .screen_content_r ul").html(group_html_right);
            } else {
                $("#screen_group .s_pitch span").eq(0).html("0");
                $("#screen_group .screen_content_r ul").empty();
            }
            $("#screen_vipWrapper").hide();
            $("#screen_group").show();
            self.getGroup();
        });
        $("#vip_screen_close_group").click(function () {
            $("#screen_vipWrapper").show();
            $("#screen_group").hide();
        });
        $("#task_group_search").keydown(function () {
            var event = event || window.event || arguments[0];
            if (event.keyCode == 13) {
                self.getGroup();
            }
        });
        $("#task_group_search_f").click(function () {
            self.getGroup();
        });
        $("#task_screen_que_group").click(function () {
            var li = $("#screen_group .screen_content_r input[type='checkbox']").parents("li");
            var group_codes = "";
            var group_names = "";
            for (var i = li.length - 1; i >= 0; i--) {
                var r = $(li[i]).attr("id");
                var p = $(li[i]).find(".p16").html();
                if (i > 0) {
                    group_codes += r + ",";
                    group_names += p + ",";
                } else {
                    group_codes += r;
                    group_names += p;
                }
            }
            self.vipcache.group_codes = group_codes;
            self.vipcache.group_names = group_names;
            $("#screen_group").hide();
            $("#screen_vipWrapper").show();
            $("#filter_group").val("已选" + li.length + "个");
            $("#filter_group").attr("data-code", group_codes);
            $("#filter_group").attr("data-name", group_names);
        });
        //弹出筛选
        $("#chooseVipBtn").click(function () {
            activity.popUp = "vip";
            $("#expend_attribute").getNiceScroll().resize();
            taskfuzhi();
            var arr = whir.loading.getPageSize();
            $("#p").css({
                "width": +arr[0] + "px",
                "height": +arr[1] + "px"
            });
            $("#p").show();
            $("#screen_vipWrapper").show();
        });
        //清空筛选
        $("#vipTask_empty_filter").click(function () {
            self.vipcache.area_codes = "";
            self.vipcache.area_names = "";
            self.vipcache.brand_codes = "";
            self.vipcache.brand_names = "";
            self.vipcache.store_codes = "";
            self.vipcache.store_names = "";
            self.vipcache.user_codes = "";
            self.vipcache.user_names = "";
            self.vipcache.group_codes = "";
            self.vipcache.group_name = "";
            $("#screen_vipWrapper .contion_input input").each(function () {
                var key = $(this).attr("data-kye");
                if (key == "6" || key == "8" || key == "12" || key == "18") {
                    $(this).val("全部");
                } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15") {
                    $(this).val("");
                    $(this).attr("data-code", "");
                } else if (key == "16") {
                    $(this).val("");
                    $(this).attr("data-code", "");
                    $("#filter_group").attr("data-corp", "");
                } else if (key == "17") {
                    $(this).val("最近3个月");
                    $(this).attr("data-date", "3");
                } else {
                    $(this).val("");
                }
            });
            $("#screen_vipWrapper textarea").each(function () {
                $(this).val("");
            });
            $("#staff_brand_num").val("全部");
            $("#staff_brand_num").attr("data-brandcode", "");
            $("#staff_area_num").val("全部");
            $("#staff_area_num").attr("data-areacode", "");
            $("#staff_shop_num").val("全部");
            $("#staff_shop_num").attr("data-storecode", "");
        });
        //筛选确定
        $("#vip_screen_vip_que").click(function () {
            $(document.body).css("overflow", "auto");
            var screen = [];
            if ($("#simple_filter").css("display") == "block") {
                _param['screen_type'] = "easy";
                $("#simple_contion .contion_input").each(function () {
                    var input = $(this).find("input");
                    var key = $(input[0]).attr("data-kye");
                    var classname = $(input[0]).attr("class");
                    var expend_key = $(input[0]).attr("data-expend");
                    if (key == "17") {
                        return;
                    } else if (key == "4") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            var date = $("#consume_date_basic_4").attr("data-date");
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param['date'] = date;
                            param['names'] = $(this).find("label:first-child").text();
                            screen.push(param);
                        }
                    } else if (key == "3") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            var date = $("#consume_date_basic_3").attr("data-date");
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param['date'] = date;
                            param['names'] = $(this).find("label:first-child").text();
                            screen.push(param);
                        }
                    } else if (key == "15") {
                        if ($(input[0]).attr("data-code") !== "") {
                            var param = {};
                            var val = $(input[0]).attr("data-code");
                            var name = $(input[0]).attr("data-name");
                            param['key'] = key;
                            param['value'] = val;
                            param['name'] = name;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        }
                    } else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0) || expend_key == "date") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param['names'] = $(this).find("label:first-child").text();
                            screen.push(param);
                        }
                    } else if (key == "18" && $(input[0]).val() !== "全部") {
                        var param = {};
                        var val = $(input[0]).val();
                        param['name'] = val;
                        val = val == "未关注" ? "N": "Y";
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        param['names'] = $(this).find("label").text();
                        screen.push(param);
                    } else {
                        if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                            var param = {};
                            var val = $(input[0]).val();
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        }
                    }
                });
            } else {
                _param['screen_type'] = "difficult";
                $("#contions>div").each(function () {
                    $(this).find(".contion_input").each(function (i, e) {
                        var input = $(e).find("input");
                        var key = $(input[0]).attr("data-kye");
                        var expend_key = $(input[0]).attr("data-expend");
                        var classname = $(input[0]).attr("class");
                        if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0) || expend_key == "date") {
                            if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                                var param = {};
                                var val = {};
                                val['start'] = $(input[0]).val();
                                val['end'] = $(input[1]).val();
                                param['type'] = "json";
                                param['key'] = key;
                                param['value'] = val;
                                param['names'] = $(this).find("label:first-child").text();
                                screen.push(param);
                            }
                        } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15" || key == "16") {
                            if ($(input[0]).attr("data-code") !== "") {
                                var param = {};
                                var val = $(input[0]).attr("data-code");
                                var name = $(input[0]).attr("data-name");
                                param['key'] = key;
                                param['value'] = val;
                                param['name'] = name;
                                param['type'] = "text";
                                param['names'] = $(this).find("label").text();
                                screen.push(param);
                            }
                        } else if (key == "17") {
                            return;
                        } else if (key == "3" || key == "4") {
                            if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                                var param = {};
                                var val = {};
                                var date = $("#consume_date").attr("data-date");
                                val['start'] = $(input[0]).val();
                                val['end'] = $(input[1]).val();
                                param['type'] = "json";
                                param['key'] = key;
                                param['value'] = val;
                                param['date'] = date;
                                param['names'] = $(this).find("label:first-child").text();
                                screen.push(param);
                            }
                        } else if ((key == "6"||key == "18") && $(input[0]).val() !== "全部") {
                            var param = {};
                            var val = $(input[0]).val();
                            param['name'] = val;
                            val = (val == "已冻结"|| val == "未关注") ? "N": "Y";
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        } else {
                            if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                                var param = {};
                                var val = $(input[0]).val();
                                param['key'] = key;
                                param['value'] = val;
                                param['type'] = "text";
                                param['names'] = $(this).find("label").text();
                                screen.push(param);
                            }
                        }
                    });
                    $(this).find("textarea").each(function () {
                        var key = $(this).attr("data-kye");
                        var param = {};
                        var val = $(this).val();
                        if (val !== "") {
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        }
                    });
                });
            }
            _param['screen'] = screen;
            $("#lookVipCondition").attr("data-code", JSON.stringify(screen));
            $("#screen_vipWrapper").hide();
            $("#p").hide();
            // $("#vipTask_empty_filter").trigger("click");
        });
        $("#lookVipCondition").click(function () {
            var div = "";
            if ($(this).attr("data-code") == undefined || $(this).attr("data-code") == "[]") {
                div = '<div style="text-align: center;line-height: 150px">当前未设置筛选条件,默认为<span style="color:#c26555;display: inline">全部会员</span></div>'
            } else {
                var arr = JSON.parse($(this).attr("data-code"));
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i].date != undefined) {
                        div += '<div><span class="conditionName">' + arr[i].names + '</span><span>最近' + arr[i].date + '个月</span><span>' + arr[i].value.start + '</span><span>~</span><span>' + arr[i].value.end + '</span></div>';
                    } else {
                        if (arr[i].type == "text") {
                            if (arr[i].name != undefined) {
                                div += '<div><span class="conditionName">' + arr[i].names + '</span><span title=' + arr[i].name + '>' + arr[i].name + '</span></div>'
                            } else {
                                div += '<div><span class="conditionName">' + arr[i].names + '</span><span title=' + arr[i].value + '>' + arr[i].value + '</span></div>'
                            }
                        }
                        if (arr[i].type == "json") {
                            div += '<div><span class="conditionName">' + arr[i].names + '</span><span>' + arr[i].value.start + '</span><span>~</span><span>' + arr[i].value.end + '</span></div>'
                        }
                    }
                }
            }
            $("#vipConditionWrap .choosedContent").html(div);
            $("#vipConditionWrap").show();
        });
    }
};
$(function () {
    activity.init();
    //日期调用插件
    var simple_birth_starts = {
        elem: '#simple_birth_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date = datas.split("-");
            date = date[1] + "-" + date[2];
            $(this.elem).val(date);
            simple_birth_ends.min = datas; //开始日选好后，重置结束日的最小日期
            simple_birth_ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var simple_birth_ends = {
        elem: '#simple_birth_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date = datas.split("-");
            date = date[1] + "-" + date[2];
            $(this.elem).val(date);
            simple_birth_starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    var starts = {
        elem: '#birth_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date = datas.split("-");
            date = date[1] + "-" + date[2];
            $(this.elem).val(date);
            ends.min = datas; //开始日选好后，重置结束日的最小日期
            ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var ends = {
        elem: '#birth_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            var date = datas.split("-");
            date = date[1] + "-" + date[2];
            $(this.elem).val(date);
            starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    var activity_starts = {
        elem: '#activate_card_starts',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59', //最大日期
        istoday: false,
        fixed: false,
        choose: function (datas) {
            activity_ends.min = datas; //开始日选好后，重置结束日的最小日期
            activity_ends.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var activity_ends = {
        elem: '#activate_card_ends',
        format: 'YYYY-MM-DD',
        istime: false,
        max: '2099-06-16 23:59:59',
        istoday: false,
        fixed: false,
        choose: function (datas) {
            activity_starts.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    $("#simple_birth_starts").bind("click", function () {
        laydate(simple_birth_starts)
    });
    $("#simple_birth_ends").bind("click", function () {
        laydate(simple_birth_ends)
    });
    $("#birth_ends").bind("click", function () {
        laydate(ends)
    });
    $("#birth_starts").bind("click", function () {
        laydate(starts)
    });
    $("#activate_card_starts").bind("click", function () {
        laydate(activity_starts);
    });
    $("#activate_card_ends").bind("click", function () {
        laydate(activity_ends)
    });
});
function taskfuzhi() {
    if (_param.screen == undefined || _param == undefined) {
        return;
    } else {
        if (_param.screen_type == "easy") {
            var screen = _param.screen;
            for (var i = 0; i < screen.length; i++) {
                switch (screen[i].key) {
                    case "1":
                        $("#simple_birth_starts").val(screen[i].value.start);
                        $("#simple_birth_ends").val(screen[i].value.end);
                        break;
                    case "3":
                        $("#consume_date_basic_3").val("最近" + screen[i].date + "个月");
                        $("#consume_date_basic_3").attr("data-date", screen[i].date);
                        $("#consume_time_start").val(screen[i].value.start);
                        $("#consume_time_end").val(screen[i].value.end);
                        break;
                    case "4":
                        $("#consume_date_basic_4").val("最近" + screen[i].date + "个月");
                        $("#consume_date_basic_4").attr("data-date", screen[i].date);
                        $("#consume_money_start").val(screen[i].value.start);
                        $("#consume_money_end").val(screen[i].value.end);
                        break;
                    case "5":
                        $("#point_start").val(screen[i].value.start);
                        $("#point_end").val(screen[i].value.end);
                        break;
                    case "18":
                        if (screen[i].value == "Y") {
                            $("#simple_state").val("已关注")
                        } else if (screen[i].value == "N") {
                            $("#simple_state").val("未关注")
                        }
                        break;
                    case "15":
                        $(".screen_staff_num").attr("data-code", screen[i].value);
                        $(".screen_staff_num").val("已选" + screen[i].value.split(",").length + "个");
                        $(".screen_staff_num").attr("data-name", screen[i].name);
                        activity.vipcache.user_codes = screen[i].value;
                        activity.vipcache.user_names = screen[i].name;
                        break;
                    default:
                        $("#memorial_day input").each(function () {
                            var key = $(this).attr("data-kye");
                            if (key == screen[i].key) {
                                $(this).val(screen[i].value.start);
                                $(this).nextAll("input").val(screen[i].value.end);
                            }
                        });
                }
            }
        } else if (_param.screen_type == "difficult") {
            var screen = _param.screen;
            for (var i = 0; i < screen.length; i++) {
                switch (screen[i].key) {
                    case "7":
                        $("#basic_message input[data-kye='7']").val(screen[i].value);
                        break;
                    case "8":
                        $("#sex").val(screen[i].value);
                        break;
                    case "9":
                        $("#age_l").val(screen[i].value.start);
                        $("#age_r").val(screen[i].value.end);
                        break;
                    case "1":
                        $("#birth_starts").val(screen[i].value.start);
                        $("#birth_ends").val(screen[i].value.end);
                        break;
                    case "10":
                        $("#basic_message input[data-kye='10']").val(screen[i].value);
                        break;
                    case "11":
                        $("#basic_message input[data-kye='11']").val(screen[i].value);
                        break;
                    case "12":
                        $("#basic_message input[data-kye='12']").val(screen[i].value);
                        break;
                    case "13":
                        $("#activate_card_starts").val(screen[i].value.start);
                        $("#activate_card_ends").val(screen[i].value.end);
                        break;
                    case "5":
                        $("#basic_message input[data-kye='5']").val(screen[i].value.start);
                        $("#basic_message input[data-kye='5']").nextAll("input").val(screen[i].value.end);
                        break;
                    case "6":
                        if (screen[i].value == "Y") {
                            $("#state").val("未冻结")
                        } else if (screen[i].value == "N") {
                            $("#state").val("已冻结")
                        }
                        break;
                    case "18":if(screen[i].value=="Y"){$("#govWx").val("已关注")}else if(screen[i].value=="N"){$("#govWx").val("未关注")}
                        break;
                    case "3":
                        $("#consume_date").val("最近" + screen[i].date + "个月");
                        $("#consume_date").attr("data-date", screen[i].date);
                        $("#consume_time_before").val(screen[i].value.start);
                        $("#consume_time_after").val(screen[i].value.end);
                        break;
                    case "4":
                        $("#consume_date").val("最近" + screen[i].date + "个月");
                        $("#consume_date").attr("data-date", screen[i].date);
                        $("#consume_money_before").val(screen[i].value.start);
                        $("#consume_money_after").val(screen[i].value.end);
                        break;
                    case "brand_code":
                        $("#screen_brand_num").attr("data-code", screen[i].value);
                        $("#screen_brand_num").val("已选" + screen[i].value.split(",").length + "个");
                        $("#screen_brand_num").attr("data-name", screen[i].name);
                        activity.vipcache.brand_codes = screen[i].value;
                        activity.vipcache.brand_names = screen[i].name;
                        break;
                    case "area_code":
                        $("#screen_area_num").attr("data-code", screen[i].value);
                        $("#screen_area_num").val("已选" + screen[i].value.split(",").length + "个");
                        $("#screen_area_num").attr("data-name", screen[i].name);
                        activity.vipcache.area_codes = screen[i].value;
                        activity.vipcache.area_names = screen[i].name;
                        break;
                    case "14":
                        $("#screen_shop_num").attr("data-code", screen[i].value);
                        $("#screen_shop_num").val("已选" + screen[i].value.split(",").length + "个");
                        $("#screen_shop_num").attr("data-name", screen[i].name);
                        activity.vipcache.store_codes = screen[i].value;
                        activity.vipcache.store_names = screen[i].name;
                        break;
                    case "15":
                        $(".screen_staff_num").attr("data-code", screen[i].value);
                        $(".screen_staff_num").val("已选" + screen[i].value.split(",").length + "个");
                        $(".screen_staff_num").attr("data-name", screen[i].name);
                        activity.vipcache.user_codes = screen[i].value;
                        activity.vipcache.user_names = screen[i].name;
                        break;
                    case "16":
                        $("#filter_group").attr("data-code", screen[i].value);
                        $("#filter_group").val("已选" + screen[i].value.split(",").length + "个");
                        $("#filter_group").attr("data-name", screen[i].name);
                        activity.vipcache.group_codes = screen[i].value;
                        activity.vipcache.group_name = screen[i].name;
                        break;
                    default:
                        $("#expend_attribute input").each(function () {
                            var key = $(this).attr("data-kye");
                            var date = $(this).attr("data-expend");
                            if (key == screen[i].key && date == "date") {
                                $(this).val(screen[i].value.start);
                                $(this).nextAll("input").val(screen[i].value.end);
                            } else if (key == screen[i].key) {
                                $(this).val(screen[i].value);
                            }
                        });
                }
            }
        }
    }
}
