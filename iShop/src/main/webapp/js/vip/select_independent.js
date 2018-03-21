var oc = new ObjectControl();
var isSelectNew = false;
var select_clickMark = "";
var select_corp_code = "C10000";
var select_type_data = {
    class: [],
    quarter: [],
    brand: [],
    typeSm: [],
    typeMid: [],
    typeSeries: [],
    price: [],
    store_area: [],
    week: [{ name: "周日" }, { name: "周一" }, { name: "周二" }, { name: "周三" }, { name: "周四" }, { name: "周五" }, { name: "周六" }],
    month: [{ name: "一月" }, { name: "二月" }, { name: "三月" }, { name: "四月" }, { name: "五月" }, { name: "六月" }, { name: "七月" }, { name: "八月" }, { name: "九月" }, { name: "十月" }, { name: "十一月" }, { name: "十二月" }]
};
var select_affiliation_init = {
    staff_num: 1,
    staff_next: false,
    area_num: 1,
    area_next: false,
    shop_num: 1,
    shop_next: false,
    act_store_num: 1,
    act_store_next: false,
    isscroll: false
};
var select_affiliation = {
    brand: {
        code: "",
        name: ""
    },
    area: {
        code: "",
        name: ""
    },
    shop: {
        brand: {
            code: "",
            name: ""
        },
        area: {
            code: "",
            name: ""
        },
        code: "",
        name: ""
    },
    staff: {
        brand: {
            code: "",
            name: ""
        },
        area: {
            code: "",
            name: ""
        },
        shop: {
            code: "",
            name: ""
        },
        code: "",
        name: ""
    },
    basic_staff: {
        brand: {
            code: "",
            name: ""
        },
        area: {
            code: "",
            name: ""
        },
        shop: {
            code: "",
            name: ""
        },
        code: "",
        name: ""
    },
    group: {
        code: "",
        name: ""
    },
    vip_type: {
        code: "",
        name: ""
    },
    public_signal: {
        code: "",
        name: ""
    },
    act_store: {
        code: "",
        name: ""
    }
};
$(".select_box_content_left,.select_box_content_center_box,.select_box_content_right .selected_list").niceScroll({
    cursorborder: "0 none",
    boxzoom: false,
    cursorcolor: " rgba(51,51,51,0.5)",
    background: " rgba(0,0,0,0.2)",
    cursoropacitymax: 1,
    touchbehavior: false,
    cursorminheight: 30,
    autohidemode: "leave"
});

function Ownformat(datas) { //纪念日开始日期回掉函数
    var date = datas.split("-");
    var index = $(this.elem).parents(".more_line").index();
    var type = $(this.elem).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    var id = $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("id");
    date = date[1] + "-" + date[2];
    $(this.elem).val(date);
    if (index == 1) {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',istoday:false, min:'" + datas + "',start:'" + datas + "', format: 'YYYY-MM-DD',choose:Ownformat})"
        )
    } else {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',istoday:false, max:'" + datas + "',start:'" + datas + "',format: 'YYYY-MM-DD',choose:Ownformat})"
        )
    }
    runtimeData(type)
}

function setDateSelection(datas) { //纪念日开始日期回掉函数
    var index = $(this.elem).parents(".more_line").index();
    var type = $(this.elem).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    var id = $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("id");
    if (index == 1) {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',istime: true, min:'" + datas + "',start:'" + datas + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})"
        )
    } else {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',istime: true, max:'" + datas + "',start:'" + datas + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})"
        )
    }
    runtimeData(type)
}

function setDateSelectionDate(datas) { //纪念日开始日期回掉函数
    var index = $(this.elem).parents(".more_line").index();
    var type = $(this.elem).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    var id = $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("id");
    if (index == 1) {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',format:'YYYY-MM-DD', min:'" + datas + "',start:'" + datas + "',choose:setDateSelectionDate})"
        )
    } else {
        $(this.elem).parents(".more_line").siblings(".more_line").find("input").attr("onclick",
            "laydate({elem:'#" + id + "',format:'YYYY-MM-DD', max:'" + datas + "',start:'" + datas + "',choose:setDateSelectionDate})"
        )
    }
    runtimeData(type)
}
$(".select_box_head .close_select_box,#cancel_select").click(function() {
    $(".select_box_wrap").hide();
    $("body").css("overflow", "auto");
    $(".select_box_content_left .item_group").find("ul").slideUp().next(".group_arrow").removeClass("item_group_active");
}); //关闭筛选框
$(window).resize(function() {
    $(".select_box").css({
        height: $(window).height() - 130 + "px"
    });
}); //当窗口大小改变时 重新计算筛选框大小
$(".item_group div,.item_group .group_arrow").click(function() {
    $(this).parents(".item_group").siblings().find("ul:visible").slideUp().next(".group_arrow").removeClass("item_group_active");
    if ($(this).siblings("ul").is(":visible")) {
        $(this).siblings("ul").slideUp(function() {
            $(".select_box_content_left").getNiceScroll().hide();
            $(".select_box_content_left").getNiceScroll().resize();
            $(".select_box_content_left").getNiceScroll().show();
        }).nextAll(".group_arrow").removeClass("item_group_active")
    } else {
        $(this).siblings("ul").slideDown(function() {
            $(".select_box_content_left").getNiceScroll().hide();
            $(".select_box_content_left").getNiceScroll().resize();
            $(".select_box_content_left").getNiceScroll().show();
        }).nextAll(".group_arrow").addClass("item_group_active")
    }

}); //左侧菜单展开和折叠
$(".tabs_head>label span,.tabs_head>label").click(function() {
    var index = $(this).index();
    if (index == 0) {
        $("#content_basic").show();
        $("#content_high .item_group").find("ul").hide();
        $("#content_high .item_group").find(".group_arrow").css({
            animation: "none"
        });
        $("#content_high").hide();
        select_type = "content_basic"
    } else {
        select_type = "content_high";
        $("#content_basic").hide();
        $("#content_high").show();
        $(".select_box_content_left").getNiceScroll().hide();
        $(".select_box_content_left").getNiceScroll().resize();
        $(".select_box_content_left").getNiceScroll().show();
    }
    resetLeftLonely();
}); //切换基本筛选和高级筛选
$(".select_box").mousedown(function(e) {
    //设置移动后的默认位置
    var endx = 0;
    var endy = 0;
    //获取div的初始位置，要注意的是需要转整型，因为获取到值带px
    var left = parseInt($(".select_box").css("left"));
    var top = parseInt($(".select_box").css("top"));

    //获取鼠标按下时的坐标，区别于下面的es.pageX,es.pageY
    var downx = e.pageX;
    var downy = e.pageY; //pageY的y要大写，必须大写！！
    var minendx = $(this).innerWidth() / 2 + 200;
    var winHy = $(window).height();
    var winHx = $(window).width();
    //    鼠标按下时给div挂事件
    $(document).bind("mousemove", function(es) {
        //es.pageX,es.pageY:获取鼠标移动后的坐标
        var maxendy = winHy - $(".select_box").outerHeight();
        var maxendx = $(".select_box").outerWidth() / 2 + (winHx - $(".select_box").outerWidth());
        endx = es.pageX - downx + left; //计算div的最终位置
        endy = es.pageY - downy + top;
        endy = endy < 80 ? 80 : endy;
        endy = endy > maxendy ? maxendy : endy;
        endx = endx < minendx ? minendx : endx;
        endx = endx > maxendx ? maxendx : endx;
        $(".select_box").css("left", endx + "px").css("top", endy + "px");
        if (es.pageY < 80 || es.pageX < 200) {
            $(this).unbind("mousemove");
        }
    });
    if (!$(e.target).is($(".select_box_head"))) {
        $(document).unbind("mousemove");
    }
}); //移动筛选框
//$(".select_box").bind("mouseout", function (es) {
//	$(document).unbind("mousemove")
//});
$(".select_box").mouseup(function() {
    //鼠标弹起时给div取消事件
    $(document).unbind("mousemove")
}); //移动筛选框
$(".select_box").on("click", ".imitate_select,.expend_select", function() { //展开 ul
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    if ($(this).next().is(":visible")) {
        $(".select_box").find(".imitate_select,.expend_select").next().hide();
        $(this).next().hide();
    } else {
        $(".select_box").find(".imitate_select,.expend_select").next().hide();
        $(this).next().show();
    }
    if (type == "activity" || type == "task" || type == "coupon" && $(this).hasClass("imitate_select_255")) {
        if ($(this).next().is(":hidden")) {
            $(this).find("input").blur();
        }
    }
    $(".select_box_content_center_box").getNiceScroll().resize();
});
$(".select_box").on("click", ".select_city_group .imitate_select", function() {
    var Index = $(this).parent().index();
    var self = $(this).parent().prev().find(".imitate_select");
    var _this = $(this);
    if ($(this).next("ul").find("li").length == 0) {
        var prevHtml = $(this).parent().prev().find(".imitate_select span").html();
        var prevLi = $(this).parent().prev().find(".imitate_select").next("ul").find("li");
        var prevCode = "";
        $(this).next("ul").hide();
        if (prevLi.length > 0) {
            prevLi.map(function(index, ele) {
                if ($(ele).html() == prevHtml && prevHtml != "钓鱼岛") {
                    prevCode = $(ele).attr("data-code");
                    if (Index == 1) {
                        getCitySelect(self, prevCode).then(function() {
                            $(_this).next().show();
                        })
                    } else if (Index == 2) {
                        getCountySelect(self, prevCode).then(function() {
                            $(_this).next().show();
                        })
                    }
                }
            })
        } else {
            var first = $(_this).parents(".select_city_group").find(".imitate_select").eq(0).find("span").html();
            var li = $(_this).parents(".select_city_group").find(".imitate_select").eq(0).next("ul").find("li");
            var code = "";
            li.map(function(index, ele) {
                if ($(ele).html() == first && first != "钓鱼岛") {
                    code = $(ele).attr("data-code");
                    getCitySelect($(_this).parents(".select_city_group").find(".imitate_select").eq(0), code).then(function() {
                        $(_this).trigger("click");
                    })
                }
            })
        }
    }
});
$(".select_box").on("click", ".expend_select_wrap ul li input", function() {
    var input = $(this).parents("ul").find("input:checked");
    var data = input.map(function() {
        return $(this).next().html()
    }).get().join(",");
    $(this).parents("ul").prev().find(".input_expend_select").val(data);
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    runtimeData(type);
});
$(".select_box").on("mousedown", ".imitate_select_wrap ul li", function() { //点击模拟select下值
    var val = $(this).html();
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    var dataType = $(this).attr("data-type");
    if (dataType !== undefined) {
        $(this).parent().prev().find("span").attr("data-type", dataType);
    }
    $(this).parent().prev().find("span").html(val);
    $(this).parent().hide();
    if (val == '为空' || val == "不为空") {
        $(this).parents(".imitate_select_wrap").next().hide();
    } else {
        $(this).parents(".imitate_select_wrap").next().show();
    }
    if (type == "area") {
        var imitate_select_wrap = $(this).parents(".imitate_select_wrap");
        var group = imitate_select_wrap.parent();
        if (imitate_select_wrap.index() == 0 && group.hasClass("select_city_group")) {
            var code = $(this).attr("data-code");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select span").eq(1).html("市");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select").eq(1).next("ul").html("");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select span").eq(2).html("区");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select").eq(2).next("ul").html("");
            getCitySelect($(this), code);
        } else if (imitate_select_wrap.index() == 1 && group.hasClass("select_city_group")) {
            var code = $(this).attr("data-code");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select span").eq(2).html("区");
            $(this).parents(".select_city_group").find(".imitate_select_wrap .imitate_select").eq(2).next("ul").html("");
            getCitySelect($(this), code);
        }
    }
    if (type == "activity" || type == "task" || type == "coupon") {
        $(this).parent().prev().find("input").attr("data-name", val);
        if (dataType !== undefined) {
            $(this).parent().prev().find("input").attr("data-type", dataType).val(val);
        }
        if ($(this).attr("data-type") == "" && $(this).html("请选择")) {
            $(this).parents(".select_" + type + "_right_line").next().hide();
        } else {
            $(this).parents(".select_" + type + "_right_line").next().show();
        }
    }
    runtimeData(type);

});
$(".select_box").on("click", ".select_box_content_center_box .delete_item", function(e) {
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    e.stopPropagation();
    if ($(this).parents(".item_line").hasClass("select_affiliation_brand")) {
        select_affiliation.brand = {
            code: "",
            name: ""
        };
        var child_type = $(this).parents(".item_line").attr("class").replace(/item_line select_affiliation_/, "");
        $(".select_box_content_right:visible .selected_list").find("div[data-type=" + child_type + "]").remove();
    } else if ($(this).parents(".item_line").hasClass("select_affiliation_storeGroup")) {
        select_affiliation.area = {
            code: "",
            name: ""
        };
        var child_type = $(this).parents(".item_line").attr("class").replace(/item_line select_affiliation_/, "");
        $(".select_box_content_right:visible .selected_list").find("div[data-type=" + child_type + "]").remove();
    } else if ($(this).parents(".item_line").hasClass("select_affiliation_store")) {
        select_affiliation.shop = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
        var child_type = $(this).parents(".item_line").attr("class").replace(/item_line select_affiliation_/, "");
        $(".select_box_content_right:visible .selected_list").find("div[data-type=" + child_type + "]").remove();
    } else if ($(this).parents(".item_line").hasClass("select_affiliation_staff")) {
        select_affiliation.staff = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
        var child_type = $(this).parents(".item_line").attr("class").replace(/item_line select_affiliation_/, "");
        $(".select_box_content_right:visible .selected_list").find("div[data-type=" + child_type + "]").remove();
    } else if ($(this).parents(".item_line").hasClass("select_show_group")) {
        select_affiliation.group = {
            code: "",
            name: ""
        };
    } else if ($(this).parents(".center_item_group").hasClass("select_attention_public_signal")) {
        select_affiliation.public_signal = {
            code: "",
            name: ""
        };
    } else if ($(this).parents(".center_item_group").hasClass("select_act_store")) {
        select_affiliation.act_store = {
            code: "",
            name: ""
        };
    } else if ($(this).parents(".center_item_group").hasClass("select_vip_card_type")) {
        select_affiliation.vip_type = {
            code: "",
            name: ""
        };
    } else if ($(this).parents(".center_item_group").hasClass("select_staff")) {
        select_affiliation.basic_staff = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
    }
    if ($(this).parents(".item_list").children().length == 1) {
        $(this).parents(".center_item_group").hide();
        $(this).parents(".item_line").remove();
        $(".select_box_content_right:visible .selected_list").find("div[data-type=" + type + "]").remove();
        resetLeftLonely();
    } else {
        $(this).parents(".item_line").remove();
        runtimeData(type);
    }
    if ($(".select_box_content_center_box:visible").children(".center_item_group:visible").length == 0) {
        $(".select_box_content_center_box:visible").find(".notSelect").show();
    }
});
$(".select_box").on("click", ".select_box_content_right .delete_item", function(e) {
    var type = $(this).parent().attr("data-type");
    $(this).parent().remove();
    if (type == "staff" && $("#content_basic").is(":visible")) {
        select_affiliation.basic_staff = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
    } else if (type == "group") {
        select_affiliation.group = {
            code: "",
            name: ""
        };
    } else if (type == "brand") {
        select_affiliation.brand = {
            code: "",
            name: ""
        };
        $(".center_item_group.select_affiliation .item_list").find(".item_line.select_affiliation_" + type).remove();
        if ($(".center_item_group.select_affiliation .item_list").children().length == 0) {
            $(".center_item_group.select_affiliation").hide();
        }
    } else if (type == "storeGroup") {
        select_affiliation.area = {
            code: "",
            name: ""
        };
        $(".center_item_group.select_affiliation .item_list").find(".item_line.select_affiliation_" + type).remove();
        if ($(".center_item_group.select_affiliation .item_list").children().length == 0) {
            $(".center_item_group.select_affiliation").hide();
        }
    } else if (type == "store") {
        select_affiliation.shop = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
        $(".center_item_group.select_affiliation .item_list").find(".item_line.select_affiliation_" + type).remove();
        if ($(".center_item_group.select_affiliation .item_list").children().length == 0) {
            $(".center_item_group.select_affiliation").hide();
        }
    } else if (type == "staff" && $("#content_high").is(":visible")) {
        select_affiliation.staff = {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        };
        $(".center_item_group.select_affiliation .item_list").find(".item_line.select_affiliation_" + type).remove();
        if ($(".center_item_group.select_affiliation .item_list").children().length == 0) {
            $(".center_item_group.select_affiliation").hide();
        }
    } else if (type == "vip_card_type") {
        select_affiliation.vip_type = {
            code: "",
            name: ""
        };
    } else if (type == "public_signal") {
        select_affiliation.public_signal = {
            code: "",
            name: ""
        };
    }
    $(".select_box_content_center:visible .center_item_group.select_" + type).find(".item_list").html("");
    $(".select_box_content_center:visible .center_item_group.select_" + type).hide();
    if ($(".select_box_content_center_box:visible").children(".center_item_group:visible").length == 0) {
        $(".select_box_content_center_box:visible").find(".notSelect").show();
    }
    resetLeftLonely();
});
$(".select_box").on("click", ".select_radio_group>label", function() {
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    runtimeData(type);
});
$(".select_box").on("blur", ".input_reset,textarea", function() {
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    runtimeData(type);
});
$(".select_box").on("click", ".select_box_content_left .item .add_btn", function() {
    var type = $(this).parent().attr("data-type");
    var lonely = $(this).parent().attr("data-lonely");
    var obj = $(".select_box_content_center_box:visible .select_" + type + ":visible");
    if (obj.length == 0) {
        obj = $(".select_box_content_center_box:visible .select_" + type);
        obj.show().appendTo($(".select_box_content_center_box:visible"));
        var clone_html = obj.find(".item_list").next().clone();
        if (type == "consumption_attribute" || type == "coupon" || type == "activity" || type == "task" || type == "affiliation") {
            var type_child = $(this).parent().attr("data-type_child");
            var selector = ".select_box_content_center_box:visible .select_" + type + "_" + type_child;
            clone_html = obj.find(".item_list").nextAll(selector).clone();
        }
        if (clone_html.has("input[type='radio']").length > 0) {
            var input_radio = clone_html.find("input[type='radio']");
            if (input_radio.length > 0) {
                input_radio.map(function(index, ele) {
                    var index = $(obj).find(".item_list").children().length;
                    var id = $(ele).attr("id");
                    var name = $(ele).attr("name");
                    $(ele).attr("id", id + index);
                    $(ele).attr("name", name + index);
                    $(ele).next().attr("for", id + index);
                })
            }
        }
        if (clone_html.has("input.laydate-icon").length > 0) {
            var dateInput = clone_html.find("input.laydate-icon");
            dateInput.map(function() {
                var index = $(obj).find(".item_list").children().length;
                var id = $(this).attr("id");
                $(this).attr("id", id + index);
                if ($(this).attr("onclick").indexOf("setDateSelectionDate") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                } else if ($(this).attr("onclick").indexOf("setDateSelection") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                } else if ($(this).attr("onclick").indexOf("Ownformat") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',istoday:false,choose:Ownformat})")
                }
            })
        }
        obj.find(".item_list").html(clone_html.show());
    } else {
        var clone_html = obj.find(".item_list").next().clone();
        if (type == "consumption_attribute" || type == "coupon" || type == "activity" || type == "task" || type == "affiliation") {
            var type_child = $(this).parent().attr("data-type_child");
            var selector = ".select_box_content_center_box:visible .select_" + type + "_" + type_child;
            if (obj.find(".item_list .select_" + type + "_" + type_child).length > 0 && type == "affiliation") {
                return
            }
            if (obj.find(".item_list .select_" + type + "_" + type_child).length > 0 && (type == "consumption_attribute" || type == "coupon" || type == "activity" || type == "task") && lonely == "true") {
                return
            }
            clone_html = obj.find(".item_list").nextAll(selector).clone()
        } else if (lonely == "true") {
            return
        }
        if (clone_html.has("input[type='radio']").length > 0) {
            var input_radio = clone_html.find("input[type='radio']");
            if (input_radio.length > 0) {
                input_radio.map(function(index, ele) {
                    var index = $(obj).find(".item_list").children().length;
                    var id = $(ele).attr("id");
                    var name = $(ele).attr("name");
                    $(ele).attr("id", id + index);
                    $(ele).attr("name", name + index);
                    $(ele).next().attr("for", id + index);
                })
            }
        }
        if (clone_html.has("input.laydate-icon").length > 0) {
            var dateInput = clone_html.find("input.laydate-icon");
            dateInput.map(function() {
                var index = $(obj).find(".item_list").children().length;
                var id = $(this).attr("id");
                $(this).attr("id", id + index);
                if ($(this).attr("onclick").indexOf("setDateSelectionDate") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                } else if ($(this).attr("onclick").indexOf("setDateSelection") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                } else if ($(this).attr("onclick").indexOf("Ownformat") != -1) {
                    $(this).attr("onclick", "laydate({elem:'#" + id + index + "',istoday:false,choose:Ownformat})")
                }
            })
        }
        obj.find(".item_list").append(clone_html.show());
    }
    if ($(".select_box_content_center_box:visible").children(".center_item_group:visible").length > 0) {
        $(".select_box_content_center_box:visible").find(".notSelect").hide();
    }
    runtimeData(type);
});

function resetOther(dom) {
    var OtherLi = $(".select_box_content_left:visible li[data-type=" + dom + "]");
    var OtherCenterLi = $(".select_box_content_center_box:visible .select_" + dom + ":visible").find(".item_list").children();
    OtherLi.map(function(index, ele) {
        var typeLeft = $(ele).attr("data-type_child");
        $(ele).removeClass("lonely_set");
        OtherCenterLi.map(function(num, element) {
            var type = $(element).attr("class").replace("item_line select_" + dom + "_", "");
            if (typeLeft == type && dom == "affiliation") {
                $(ele).addClass("lonely_set")
            } else if (typeLeft == type && (dom == "consumption_attribute" || dom == "coupon")) {
                if ($(ele).attr("data-lonely") == "true") {
                    $(ele).addClass("lonely_set")
                }
            }
        })
    })
}

function resetLeftLonely() {
    var leftLi = $(".select_box_content_left:visible li[data-lonely=true]");
    var contentLI = $(".select_box_content_center_box:visible .center_item_group:visible");
    leftLi.map(function(index, ele) {
        if ($(ele).attr("data-lonely") !== undefined && $(ele).attr("data-lonely") == "true") {
            var typeLeft = $(ele).attr("data-type");
            $(ele).removeClass("lonely_set");
            contentLI.map(function(num, element) {
                var type = $(element).attr("class").replace(/center_item_group select_/, "");
                if (typeLeft == type) {
                    $(ele).addClass("lonely_set")
                }
            })
        }
    });
    resetOther("affiliation");
    resetOther("consumption_attribute");
    resetOther("coupon")
}

function runtimeData(type) {
    var obj = $(".select_box_content_center_box:visible .select_" + type + ":visible");
    var dataType = obj.attr("data-type");
    var item_line = obj.find(".item_list .item_line");
    var html = "";
    var name = $(".select_box_content_left:visible li[data-type=" + type + "]").children().eq(0).html();
    resetLeftLonely();
    if (name == "消费次数" || name == "消费金额") {
        name = "消费属性"
    }
    if (name == "获得数量" || name == "使用数量") {
        name = "券"
    }

    function notFirst(index, ele) {
        if (index != 0) {
            html += "<br>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(0).html() + " ";
        }
    }

    if (dataType == "affiliation_other") {
        if (type == "staff") {
            html += "<p class='logic'>" + $(obj).find(".imitate_select_wrap .imitate_select span").eq(0).html() + "</p><p class='selected_value' title='" + $(obj).find(".item_list:visible input").attr("data-name") + "'>" + $(obj).find(".item_list:visible input").attr("data-name") + "</p>";
        } else {
            html += "<p class='selected_value' title='" + $(obj).find(".item_list:visible input").attr("data-name") + "'>" + $(obj).find(".item_list:visible input").attr("data-name") + "</p>";
        }
        if ($(".select_box_content_right:visible .selected_list div[data-type=" + type + "]").length == 0) {
            $(".select_box_content_right:visible .selected_list").append("<div data-type='" + type + "' style='margin-left: 0;text-indent: 0'>" + "<em>" + name + "</em>" + "<span  class='select_detail' style='margin-left: 2em;display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 3;overflow: hidden;'>" + html + "</span><i class='delete_item icon-ishop_6-12 '></i></div>");
        } else {
            $(".select_box_content_right:visible .selected_list div[data-type=" + type + "] span").html(html)
        }
    } else if (dataType == "affiliation") {
        item_line.map(function(index, ele) {
            var child_type = $(this).attr("class").replace(/item_line select_affiliation_/, "");
            html = "<p class='logic'>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(0).html() + "</p><p class='selected_value' title='" + $(ele).find("input").attr("data-name") + "'>" + $(ele).find("input").attr("data-name") + "</p>";
            name = $(ele).find(".center_item_group_title").html();
            if ($(".select_box_content_right:visible .selected_list div[data-type=" + child_type + "]").length == 0) {
                $(".select_box_content_right:visible .selected_list").append("<div data-type='" + child_type + "' style='margin-left: 0;text-indent: 0'>" + name + "<span class='select_detail' style='margin-left: 2em;display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 3;overflow: hidden;'>" + html + "</span><i class='delete_item icon-ishop_6-12'></i></div>");
            } else {
                $(".select_box_content_right:visible .selected_list div[data-type=" + child_type + "] span").html(html);
            }
        });
    } else {
        switch (dataType) {
            case "text":
                if (type == "area") {
                    item_line.map(function(index, ele) {
                        notFirst(index, ele);
                        html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(1).html() + "</p>";
                        var city = $(ele).find(".select_city_group");
                        if (city.is(":visible")) {
                            html += city.find(".imitate_select span").eq(0).html() == "省" ? "" : "<p class='selected_value'>" + city.find(".imitate_select span").eq(0).html() + "</p>";
                            html += city.find(".imitate_select span").eq(1).html() == "市" ? "" : "<p class='selected_value'>" + city.find(".imitate_select span").eq(1).html() + "</p>";
                            html += city.find(".imitate_select span").eq(2).html() == "区" ? "" : "<p class='selected_value'>" + city.find(".imitate_select span").eq(2).html() + "</p>";
                        }
                    });
                } else if (type == "attention_public_signal" || type == "vip_card_type" || type == "act_store") {
                    item_line.map(function(index, ele) {
                        notFirst(index, ele);
                        html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(1).html() + "</p>";
                        html += "<p class='selected_value'>" + $(ele).find("input").attr("data-name") + "</p>"
                    })
                } else {
                    item_line.map(function(index, ele) {
                        notFirst(index, ele);
                        html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(1).html() + "</p>";
                        if ($(this).find("textarea").length > 0) {
                            if ($(this).find("textarea").is(":visible")) {
                                html += "<p class='selected_value'>" + $(this).find("textarea").val().trim() + "</p>";
                            } else {
                                html += ""
                            }
                        } else {
                            if ($(ele).find("input").is(":visible")) {
                                html += "<p class='selected_value'>" + $(ele).find("input").val().trim() + "</p>";
                            } else {
                                html += "";
                            }
                        }
                    });
                }
                break;
            case "interval":
                if (type == "consumption_attribute") {
                    item_line.map(function(index, ele) {
                        notFirst(index, ele);
                        var dataKey = $(this).attr("data-key");
                        var head = "";
                        var foot = "";
                        switch (dataKey) {
                            case "NUM_TRADE":
                                head = "消费次数";
                                foot = "次";
                                break;
                            case "AMT_TRADE":
                                head = "消费金额";
                                foot = "元";
                                break;
                            case "NUM_TRADE_R":
                                head = "退款次数";
                                foot = "次";
                                break;
                            case "AMT_TRADE_R":
                                head = "退款金额";
                                foot = "元";
                                break
                        }
                        if (dataKey == "NUM_TRADE_R" || dataKey == "AMT_TRADE_R") {
                            html += head;
                            html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(1).find("span").html() + "</p>";
                            html += "<p class='selected_value'>" + $(ele).find("input").eq(0).val() + "</p>";
                            html += "至" + "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(2).find("span").html() + "</p>";
                            html += "<p class='selected_value'>" + $(ele).find("input").eq(1).val() + "</p>";
                            html += foot
                        } else {
                            html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(0).find("span").html() + "</p>";
                            html += head;
                            html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(1).find("span").html() + "</p>";
                            html += "<p class='selected_value'>" + $(ele).find("input").eq(0).val() + "</p>";
                            html += "至" + "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(2).find("span").html() + "</p>";
                            html += "<p class='selected_value'>" + $(ele).find("input").eq(1).val() + "</p>";
                            html += foot
                        }
                    });
                } else if (type == "coupon") {
                    item_line = obj.find(".item_list .item_line .select_" + type + "_right_line+div:visible").parents(".item_line");
                    var len_c = item_line.length - 1;
                    for (var c = len_c; c >= 0; c--) {
                        if (c != len_c) {
                            html += "<br>" + $(item_line[c]).find(".imitate_select_wrap .imitate_select span").eq(0).html() + " ";
                        }
                        var dataKey = $(item_line[c]).attr("data-key");
                        var head = "";
                        switch (dataKey) {
                            case "TOTAL":
                                head = "获得";
                                break;
                            case "VERIFYED":
                                head = "使用";
                                break;
                            case "EXPIRE":
                                head = "过期";
                                break;
                            case "AVAILABLE":
                                head = "可用";
                                break

                        }
                        html += head;
                        html += "<p class='selected_value'>" + $(item_line[c]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(0).find("input").val() + "</p>";
                        html += "<p class='logic'>" + $(item_line[c]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(1).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(item_line[c]).find("input").eq(1).val() + "</p>";
                        html += "至" + "<p class='logic'>" + $(item_line[c]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(2).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(item_line[c]).find("input").eq(2).val() + "</p>" + "个";
                    }
                } else if (type == "class" || type == "week" || type == "month" || type == "quarter" || type == "brand" || type == "typeSm" || type == "typeMid" || type == "typeSeries" || type == "price" || type == "store_area") {
                    item_line = obj.find(".item_list .item_line .select_dimension+div:visible").parents(".item_line");
                    var len_t = item_line.length - 1;
                    for (var t = len_t; t >= 0; t--) {
                        if (t != len_t) {
                            html += "<br>" + $(item_line[t]).find(".imitate_select_wrap .imitate_select span").eq(0).html() + " ";
                        }
                        html += "<p class='logic'>" + $(item_line[t]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(0).find("span").html() + "</p>";
                        html += "<p class='logic'>" + $(item_line[t]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(1).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(item_line[t]).find("input").eq(0).val() + "</p>";
                        html += "至" + "<p class='logic'>" + $(item_line[t]).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(2).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(item_line[t]).find("input").eq(1).val() + "</p>";
                    }
                } else {
                    item_line.map(function(index, ele) {
                        notFirst(index, ele);
                        html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(0).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(ele).find("input").eq(0).val() + "</p>";
                        html += "至" + "<p class='logic'>" + $(ele).find(".imitate_select_wrap:not('.andOrNot') .imitate_select").eq(1).find("span").html() + "</p>";
                        html += "<p class='selected_value'>" + $(ele).find("input").eq(1).val() + "</p>";

                    });
                }
                break;
            case "radio":
                if (type == "activity" || type == "task") {
                    item_line = obj.find(".item_list .item_line .select_" + type + "_right_line+div:visible").parents(".item_line");
                    var len = item_line.length - 1;
                    for (var i = len; i >= 0; i--) {
                        if (i != len) {
                            html += "<br>" + $(item_line[i]).find(".imitate_select_wrap .imitate_select span").eq(0).html() + " ";
                        }
                        var val = $(item_line[i]).find(".select_radio_group .checkbox input:checked").val();
                        if (val == "Y") {
                            html += "<p class='logic'>参与</p>" + "<p class='selected_value'>" + $(item_line[i]).find(".imitate_select_wrap .imitate_select input").eq(0).val() + "</p>";
                        } else {
                            html += "<p class='logic'>未参与</p>" + "<p class='selected_value'>" + $(item_line[i]).find(".imitate_select_wrap .imitate_select input").eq(0).val() + "</p>";
                        }
                    }
                } else if (type == "sex") {
                    item_line.map(function(index, ele) {
                        html += "<p class='logic'>" + $(ele).find(".imitate_select_wrap .imitate_select span").eq(1).html() + "</p>";
                        html += "<p class='selected_value'>" + $(ele).find(".select_radio_group .checkbox input:checked").parent().next().html() + "</p>";
                    });
                } else {
                    item_line.map(function(index, ele) {
                        html += "<p class='selected_value'>" + $(ele).find(".select_radio_group .checkbox input:checked").parent().next().html() + "</p>";
                    });
                }
                break;
        }
        if ($(".select_box_content_right:visible .selected_list div[data-type=" + type + "]").length == 0) {
            $(".select_box_content_right:visible .selected_list").append("<div data-type='" + type + "'> <em style='font-style: normal'>" + name + " </em><span class='select_detail'>" + html + "</span><i class='delete_item icon-ishop_6-12'></i></div>");
        } else {
            $(".select_box_content_right:visible .selected_list div[data-type=" + type + "] span").html(html);
        }
    }
}
$(".select_box").on("click", ".up_num", function() {
    var val = parseInt($(this).prevAll("input").val());
    var num = val + 1;
    var min = $(this).prevAll("input").attr("min");
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    $(this).prevAll("input").val(num);
    if (num > min) {
        $(this).siblings(".down_num").removeClass("disable");
    }
    runtimeData(type)
});
$(".select_box").on("click", ".down_num", function() {
    var val = parseInt($(this).prevAll("input").val());
    var num;
    var min = $(this).prevAll("input").attr("min");
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    num = val - 1;
    $(this).prevAll("input").val(num);
    if (num < min || num == min) {
        $(this).addClass("disable");
        $(this).prevAll("input").val(min);
        return;
    }
    runtimeData(type)
});

function filterNum(element) {
    var val = $(element).val();
    //var reg = /^(0|[1-9]\d*)(\.\d+)?$/g;
    //var min = $(element).attr("min");
    //var event = window.event || arguments[0];
    var type = $(element).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    //if (event.keyCode == 37 || event.keyCode == 39) {
    //	return
    //}
    if (isNaN(val)) {
        $(element).val("");
    }
    //if (val < min || val == min) {
    //	$(element).val(min);
    //	$(element).siblings(".down_num").addClass("disable");
    //} else {
    //	$(element).siblings(".down_num").removeClass("disable");
    //}
    runtimeData(type);
}
$(".input_num").keydown(function(e) {
    var event = window.event || arguments[0];
    var min = $(this).attr("min");
    if (event.keyCode == 8) {
        e.preventDefault();
        var val = $(this).val();
        if (val.slice(0, -1) == "") {
            $(this).val(min);
            $(this).siblings(".down_num").addClass("disable")
        } else {
            $(this).val(val.slice(0, -1));
        }
    }
});

function show_select_wrap(type) {
    $(".select_box").on("click", ".item_list:visible .select_show_" + type, function() {
        var arr = whir.loading.getPageSize();
        $("#p").css({ "width": +arr[0] + "px", "height": +arr[1] + "px" });
        $("#p").show();
        $("#screen_" + type + " .screen_content_l ul").empty();
        $("#screen_" + type + " .screen_content_l ul").unbind("scroll");
        $("#screen_" + type).show();
        $(".select_box_wrap").hide();
        select_clickMark = "";
        switch (type) {
            case "vip_type":
                if (select_affiliation.vip_type.code !== "") {
                    var codes = select_affiliation.vip_type.code.split(',');
                    var names = select_affiliation.vip_type.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_vip_type .s_pitch span").eq(0).html(h);
                    $("#screen_vip_type .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_vip_type .s_pitch span").eq(0).html("0");
                    $("#screen_vip_type .screen_content_r ul").empty();
                }
                selectCardType();
                break;
            case "brand":
                if (select_affiliation.brand.code !== "") {
                    var codes = select_affiliation.brand.code.split(',');
                    var names = select_affiliation.brand.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_brand .s_pitch span").eq(0).html(h);
                    $("#screen_brand .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_brand .s_pitch span").eq(0).html("0");
                    $("#screen_brand .screen_content_r ul").empty();
                }
                getbrandlist();
                break;
            case "public_signal":
                if (select_affiliation.public_signal.code !== "") {
                    var codes = select_affiliation.public_signal.code.split(',');
                    var names = select_affiliation.public_signal.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_public_signal .s_pitch span").eq(0).html(h);
                    $("#screen_public_signal .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_public_signal .s_pitch span").eq(0).html("0");
                    $("#screen_public_signal .screen_content_r ul").empty();
                }
                selectPublicSignal();
                break;
            case "staff":
                select_affiliation_init.staff_num = 1;
                var staff = {};
                if (select_type == "content_basic") {
                    staff = select_affiliation.basic_staff;
                    var brand_code = staff.brand.code;
                    var area_code = staff.area.code;
                    var shop_code = staff.shop.code;
                    $("#staff_brand_num").val("已选" + (brand_code == "" ? 0 : brand_code.split(',').length) + "个");
                    $("#staff_area_num").val("已选" + (area_code == "" ? 0 : area_code.split(',').length) + "个");
                    $("#staff_shop_num").val("已选" + (shop_code == "" ? 0 : shop_code.split(',').length) + "个");
                } else if (select_type == "content_high") {
                    staff = select_affiliation.staff;
                    var brand_code = staff.brand.code;
                    var area_code = staff.area.code;
                    var shop_code = staff.shop.code;
                    $("#staff_brand_num").val("已选" + (brand_code == "" ? 0 : brand_code.split(',').length) + "个");
                    $("#staff_area_num").val("已选" + (area_code == "" ? 0 : area_code.split(',').length) + "个");
                    $("#staff_shop_num").val("已选" + (shop_code == "" ? 0 : shop_code.split(',').length) + "个");
                }
                if (staff.code !== "") {
                    var codes = staff.code.split(',');
                    var names = staff.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_staff .s_pitch span").eq(0).html(h);
                    $("#screen_staff .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_staff .s_pitch span").eq(0).html("0");
                    $("#screen_staff .screen_content_r ul").empty();
                }
                getstafflist(select_affiliation_init.staff_num);
                break;
            case "area":
                select_affiliation_init.area_num = 1;
                if (select_affiliation.area.code !== "") {
                    var codes = select_affiliation.area.code.split(',');
                    var names = select_affiliation.area.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_area .s_pitch span").eq(0).html(h);
                    $("#screen_area .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_area .s_pitch span").eq(0).html("0");
                    $("#screen_area .screen_content_r ul").empty();
                }
                getarealist(select_affiliation_init.area_num);
                break;
            case "shop":
                select_affiliation_init.shop_num = 1;
                if (select_affiliation.shop.code !== "") {
                    var codes = select_affiliation.shop.code.split(',');
                    var names = select_affiliation.shop.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_shop .s_pitch span").eq(0).html(h);
                    $("#screen_shop .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_shop .s_pitch span").eq(0).html("0");
                    $("#screen_shop .screen_content_r ul").empty();
                }
                getstorelist(select_affiliation_init.shop_num);
                break;
            case "act_store":
                select_affiliation_init.act_store_num = 1;
                if (select_affiliation.act_store.code !== "") {
                    var codes = select_affiliation.act_store.code.split(',');
                    var names = select_affiliation.act_store.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_act_store .s_pitch span").eq(0).html(h);
                    $("#screen_act_store .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_act_store .s_pitch span").eq(0).html("0");
                    $("#screen_act_store .screen_content_r ul").empty();
                }
                getactstorelist(select_affiliation_init.act_store_num);
                break;
            case "group":
                isSelectNew = true;
                if (select_affiliation.group.code !== "") {
                    var codes = select_affiliation.group.code.split(',');
                    var names = select_affiliation.group.name.split(',');
                    var html_right = "";
                    for (var h = 0; h < codes.length; h++) {
                        html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
                    }
                    $("#screen_group .s_pitch span").eq(0).html(h);
                    $("#screen_group .screen_content_r ul").html(html_right);
                } else {
                    $("#screen_group .s_pitch span").eq(0).html("0");
                    $("#screen_group .screen_content_r ul").empty();
                }
                getGroup();
                break;
        }
        var li = $("#screen_" + type + " .screen_content_r input[type='checkbox']").parents("li");
        for (var k = 0; k < li.length; k++) {
            $("#screen_" + type + " .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
        }
    });
}
show_select_wrap("vip_type");
show_select_wrap("brand");
show_select_wrap("public_signal");
show_select_wrap("area");
show_select_wrap("shop");
show_select_wrap("group");
show_select_wrap("staff");
show_select_wrap("act_store");

function bind_single_select(type) {
    $("#screen_close_" + type).click(function() { //关闭会员类型
        $("#screen_" + type).hide();
        $("#p").hide();
        $(".select_box_wrap").show();
    });

    $("#" + type + "_search").keydown(function() { //会员类型筛选 搜索
        var event = window.event || arguments[0];
        if (event.keyCode == 13) {
            var value = $(this).val().trim();
            if (value == "") {
                $("#screen_" + type + " .screen_content_l ul li").show();
            } else {
                $("#screen_" + type + " .screen_content_l ul li").hide();
                $("#screen_" + type + " .screen_content_l ul li:contains('" + value + "')").show();
            }
            $("#screen_" + type + " .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
            $("#screen_" + type + " .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
        }
    });

    $("#" + type + "_search_f").click(function() { //会员类型筛选 搜索
        var value = $("#" + type + "_search").val().trim();
        if (value == "") {
            $("#screen_" + type + " .screen_content_l ul li").show();
        } else {
            $("#screen_" + type + " .screen_content_l ul li").hide();
            $("#screen_" + type + " .screen_content_l ul li:contains('" + value + "')").show();
        }
        $("#screen_" + type + " .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
        $("#screen_" + type + " .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
    });

    $("#screen_que_" + type).click(function() { //会员类型筛选 确定
        var li = $("#screen_" + type + " .screen_content_r input[type='checkbox']").parents("li");
        var codes = [],
            names = [];
        var type_p = $(".select_show_" + type).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
        li.map(function() {
            codes.push($(this).attr("id"));
            names.push($(this).children("span").html());
        });
        $(".item_list .select_show_" + type).val("已选" + li.length + "个");
        $(".item_list .select_show_" + type).attr("data-code", codes.reverse().join(","));
        $(".item_list .select_show_" + type).attr("data-name", names.reverse().join(","));
        select_affiliation[type].code = codes.join(",");
        select_affiliation[type].name = names.join(",");
        $("#screen_" + type).hide();
        $("#p").hide();
        $(".select_box_wrap").show();
        runtimeData(type_p);
    });
}
bind_single_select("vip_type");
bind_single_select("public_signal");
$("#select_clear").click(function() {
    $(".select_box_content_center_box:visible .center_item_group:visible").hide().find(".item_list").html("");
    $(".select_box_content_center_box:visible").find(".notSelect").show();
    $(".select_box_content_right:visible .selected_list").html("");
    $("#staff_brand_num").val("全部");
    $("#staff_area_num").val("全部");
    $("#staff_shop_num").val("全部");
    $("#brand_num").val("全部");
    $("#area_num").val("全部");
    $("#staff_search").val("");
    $("#brand_search").val("");
    $("#store_search").val("");
    $("#area_search").val("");
    $("#group_search").val("");
    $("#vip_type_search").val("");
    $("#public_signal_search").val("");
    select_affiliation = {
        brand: {
            code: "",
            name: ""
        },
        area: {
            code: "",
            name: ""
        },
        shop: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        staff: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        basic_staff: {
            brand: {
                code: "",
                name: ""
            },
            area: {
                code: "",
                name: ""
            },
            shop: {
                code: "",
                name: ""
            },
            code: "",
            name: ""
        },
        group: {
            code: "",
            name: ""
        },
        vip_type: {
            code: "",
            name: ""
        },
        public_signal: {
            code: "",
            name: ""
        },
        act_store: {
            code: "",
            name: ""
        }
    };
    //select_affiliation = {
    //    brand: {
    //        code: "",
    //        name: ""
    //    },
    //    area: {
    //        code: "",
    //        name: ""
    //    },
    //    shop: {
    //        code: "",
    //        name: ""
    //    },
    //    staff: {
    //        code: "",
    //        name: ""
    //    },
    //    group: {
    //        code: "",
    //        name: ""
    //    },
    //    vip_type:{
    //        code:"",
    //        name:""
    //    },
    //    public_signal:{
    //        code:"",
    //        name:""
    //    },
    //    act_store:{
    //        code:"",
    //        name:""
    //    }
    //};
    resetLeftLonely();
});

function getProvinceSelect() {
    var param = {};
    getProvinceData(param).then(function(data) {
        $(".select_area .item_list+.item_line .select_city_group .imitate_select_wrap").eq(0).find("ul").html("");
        data.map(function(item) {
            var html = "<li data-code='" + item.location_code + "'>" + item.location_name + "</li>";
            $(".select_area .item_list+.item_line .select_city_group .imitate_select_wrap").eq(0).find("ul").append(html);
        });
    })
} //获取省份
function getCitySelect(ele, code) {
    var param = {
        higher_level_code: code
    };
    var cityDef = $.Deferred();
    getProvinceData(param).then(function(data) {
        $(ele).parents(".imitate_select_wrap").next().find("ul").html("");
        data.map(function(item) {
            var html = "<li data-code='" + item.location_code + "'>" + item.location_name + "</li>";
            $(ele).parents(".imitate_select_wrap").next().find("ul").append(html);
        });
        cityDef.resolve();
    });
    return cityDef
}

function getCountySelect(ele, code) {
    var def = $.Deferred();
    var param = {
        higher_level_code: code
    };
    getProvinceData(param).then(function(data) {
        $(ele).parents(".imitate_select_wrap").next().find("ul").html("");
        data.map(function(item) {
            var html = "<li data-code='" + item.location_code + "'>" + item.location_name + "</li>";
            $(ele).parents(".imitate_select_wrap").next().find("ul").append(html);
        });
        def.resolve()
    });
    return def
}

function getProvinceData(param) {
    var def = $.Deferred();
    oc.postRequire('post', '/location/getProvince', '', param, function(data) {
        if (data.code == "0") {
            def.resolve(JSON.parse(data.message));
        } else {}
    });
    return def
}

function selectCardType() {
    var corp_code = select_corp_code;
    var param = { "corp_code": corp_code };
    oc.postRequire("post", "/vipCardType/getVipCardTypes", "0", param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var vip_type_html = '';
            $("#screen_vip_type .s_pitch span").eq(1).text(list.length);
            if (list.length == 0) {
                for (var h = 0; h < 9; h++) {
                    vip_type_html += "<li></li>"
                }
            } else {
                if (list.length < 9) {
                    for (var i = 0; i < list.length; i++) {
                        vip_type_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].vip_card_type_code + "'  name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].vip_card_type_name + "</span></li>"
                    }
                    for (var j = 0; j < 9 - list.length; j++) {
                        vip_type_html += "<li></li>"
                    }
                } else if (list.length >= 9) {
                    for (var i = 0; i < list.length; i++) {
                        vip_type_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].vip_card_type_code + "' name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].vip_card_type_name + "</span></li>"
                    }
                }
            }
            $("#screen_vip_type .screen_content_l ul").append(vip_type_html);
            var li = $("#screen_vip_type .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_vip_type .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            $("#vip_type_search_f").trigger("click");
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}

function selectPublicSignal() {
    var corp_code = select_corp_code;
    var param = { "corp_code": corp_code };
    oc.postRequire("post", "/corp/selectWx", "0", param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.list;
            var public_signal_html = '';
            $("#screen_public_signal .s_pitch span").eq(1).text(list.length);
            if (list.length == 0) {
                for (var h = 0; h < 9; h++) {
                    public_signal_html += "<li></li>"
                }
            } else {
                if (list.length < 9) {
                    for (var i = 0; i < list.length; i++) {
                        public_signal_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].app_id + "'  name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].app_name + "</span></li>"
                    }
                    for (var j = 0; j < 9 - list.length; j++) {
                        public_signal_html += "<li></li>"
                    }
                } else if (list.length >= 9) {
                    for (var i = 0; i < list.length; i++) {
                        public_signal_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].app_id + "' name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].app_name + "</span></li>"
                    }
                }
            }
            $("#screen_public_signal .screen_content_l ul").append(public_signal_html);
            var li = $("#screen_public_signal .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_public_signal .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            $("#public_signal_search_f").trigger("click");
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
//获取拓展资料
function expend_data_1() {
    var def = $.Deferred();
    var param = { "corp_code": select_corp_code };
    oc.postRequire("post", "/vipparam/corpVipParams", "0", param, function(data) {
        if (data.code == 0) {
            $("#expend_attribute").empty();
            var msg = JSON.parse(data.message);
            var list = JSON.parse(msg.list);
            var html = "";
            $("#select_expanding_data").next("ul").html("");
            $("#content_high .select_box_content_center .select_box_content_center_box .center_item_group[data-expend=is_expend]").remove();
            for (var e = 0; e < list.length; e++) {
                var html = "";
                if ($(".select_box_wrap").hasClass("isGroupAdd")) {
                    $("#select_expanding_data").next("ul").append(
                        '<li class="item" data-lonely="true" data-type="' + list[e].param_name + '"><span>' + list[e].param_desc + '</span><span class="add_btn icon-ishop_6-01"></span></li>'
                    );
                } else {
                    $("#select_expanding_data").next("ul").append(
                        '<li class="item" data-type="' + list[e].param_name + '"><span>' + list[e].param_desc + '</span><span class="add_btn icon-ishop_6-01"></span></li>'
                    );
                }
                if (list[e].param_type == "check" || list[e].param_type == "select") {
                    var param_values = "";
                    var li = "";
                    param_values = list[e].param_values.split(",");
                    for (var j = 0; j < param_values.length; j++) {
                        li += '<li class="type_check">' +
                            '<input type="checkbox" style="margin:0;vertical-align:middle;width: 15px;height: 15px;"/>' +
                            '<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="' + param_values[j] + '">' + param_values[j] + '</span>' +
                            '</li>'
                    }
                    html = '<div class="center_item_group select_' + list[e].param_name + '"  data-key="' + list[e].param_name + '" data-expend="is_expend" data-type="text">' +
                        '<div class="center_item_group_title">' + list[e].param_desc + '</div>' +
                        '<div class="item_list">' +
                        '</div>' +
                        '<div class="item_line" style="display: none">' +
                        '<span class="imitate_select_wrap andOrNot"><div class="imitate_select imitate_select_60"> <span>并且</span>' +
                        '<i class="icon-ishop_8-02"></i> </div><ul> <li>并且</li> <li>或者</li> </ul> </span><span class="imitate_select_wrap">' +
                        '<div class="imitate_select imitate_select_90">' +
                        '<span>等于</span> <i class="icon-ishop_8-02"></i></div><ul><li>等于</li><li>不等于</li><li>包含</li> <li>不包含</li><li>为空</li><li>不为空</li> </ul> </span> <span class="expend_select_wrap"><span class="expend_select">' +
                        '<input type="text" class="input_expend_select" style="width:210px;" readonly>' +
                        '<i class="icon-ishop_8-02"></i>' +
                        '</span>' +
                        '<ul>' + li + '</ul></span>' +
                        '<span class="delete_item icon-ishop_6-12"></span>' +
                        '</div>' +
                        '</div>';
                }
                if (list[e].param_type == "date") {
                    html = '<div class="center_item_group select_' + list[e].param_name + '"  data-key="' + list[e].param_name + '" data-expend="is_expend" data-type="interval">' +
                        '<div class="center_item_group_title">' + list[e].param_desc + '</div>' +
                        '<div class="item_list"></div> <div class="item_line" style="display:none;"> <span class="imitate_select_wrap andOrNot" style="margin-right: 50px">' +
                        '<div class="imitate_select imitate_select_60"> <span>并且</span>' +
                        '<i class="icon-ishop_8-02"></i> </div> <ul style="display: none;"> <li>并且</li> <li>或者</li> </ul> </span> <div class="more_line"> <span class="imitate_select_wrap">' +
                        '<div class="imitate_select imitate_select_90"> <span>大于</span> <i class="icon-ishop_8-02"></i> </div>' +
                        '<ul style="display: none;"> <li>大于</li> <li>大于等于</li> </ul> </span>' +
                        '<span class="select_date"> <input type="text" class="laydate-icon" readonly="" autocomplete="off" placeholder="请选择日期" id="' + list[e].param_name + '_start" onclick="laydate({elem:\'' + list[e].param_name + '\'_strat,choose:Ownformat})">' +
                        '</span> </div> <span style="position: absolute;left: 100px;top: 25px;">至</span> <div class="more_line" style="margin-top: 10px">' +
                        '<span class="imitate_select_wrap"> <div class="imitate_select imitate_select_90"> <span>小于</span> <i class="icon-ishop_8-02"></i>' +
                        '</div> <ul style="display: none;"> <li>小于</li> <li>小于等于</li> </ul>' +
                        '</span> <span class="select_date"> <input type="text" class="laydate-icon" readonly="" autocomplete="off" placeholder="请选择日期" id="' + list[e].param_name + '_end" onclick="laydate({elem:\'' + list[e].param_name + '\'_end,choose:Ownformat})">' +
                        '</span> </div> <span class="delete_item icon-ishop_6-12"></span> </div> </div>'
                }
                if (list[e].param_type == "text") {
                    html = '<div class="center_item_group select_' + list[e].param_name + '"  data-key="' + list[e].param_name + '" data-expend="is_expend" data-type="text">' +
                        '<div class="center_item_group_title">' + list[e].param_desc + '</div>' +
                        '<div class="item_list"></div>' +
                        '<div class="item_line" style="display: none"> <span class="imitate_select_wrap andOrNot"> <div class="imitate_select imitate_select_60">' +
                        '<span>并且</span> <i class="icon-ishop_8-02"></i> </div> <ul style="display: none;"> <li>并且</li> <li>或者</li> </ul> </span>' +
                        '<span class="imitate_select_wrap"> <div class="imitate_select imitate_select_90"> <span>包含</span> <i class="icon-ishop_8-02"></i>' +
                        '</div> <ul style="display: none;"> <li>包含</li> <li>不包含</li> <li>等于</li> <li>不等于</li> </ul> </span>' +
                        '<input type="text" class="input_reset" style="width:210px;"> <span class="delete_item icon-ishop_6-12"></span>' +
                        '</div> </div>'
                }
                if (list[e].param_type == "longtext") {
                    html = '<div class="center_item_group select_' + list[e].param_name + '"  data-key="' + list[e].param_name + '" data-expend="is_expend" data-type="text">' +
                        '<div class="center_item_group_title">' + list[e].param_desc + '</div>' +
                        '<div class="item_list"></div>' +
                        '<div class="item_line" style="display: none"> <span class="imitate_select_wrap andOrNot"> <div class="imitate_select imitate_select_60">' +
                        '<span>并且</span> <i class="icon-ishop_8-02"></i> </div> <ul style="display: none;"> <li>并且</li> <li>或者</li> </ul> </span>' +
                        '<span class="imitate_select_wrap"> <div class="imitate_select imitate_select_90"> <span>包含</span> <i class="icon-ishop_8-02"></i>' +
                        '</div> <ul style="display: none;"> <li>包含</li> <li>不包含</li> <li>等于</li> <li>不等于</li> </ul> </span>' +
                        '<textarea rows="0" cols="0"></textarea> <span class="delete_item icon-ishop_6-12"></span>' +
                        '</div> </div>'
                }
                $("#content_high .select_box_content_center .select_box_content_center_box").append(html);
            }
        } else if (data.code == -1) {}
        def.resolve();
    });
    return def;
}

function getTask() {
    var param = {};
    //var li = "<li data-type=''>请选择</li>";
    var li = "<div style='height: 30px;line-height: 30px;text-align: center;display: none'>无数据</div>";
    oc.postRequire("post", "/vipTask/getAllByStatus", "", param, function(data) {
        if (data.code == "0") {
            var list = JSON.parse(data.message).list;
            if (list && list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    li += "<li data-type='" + list[i].task_code + "' title='" + list[i].task_title + "'>" + list[i].task_title + "</li>";
                }
            } else {
                li = "<div style='height: 30px;line-height: 30px;text-align: center;display: none'>无数据</div>";
            }
            $(".center_item_group.select_task").find(".select_task_task_item .imitate_select").eq(1).next("ul").html(li)
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

function inputSelectValue(dom) {
    var _this = $(dom);
    var value = _this.val();
    _this.parent().next("ul").find("div").hide();
    if (value == "") {
        if (_this.parent().next("ul").find("li").length > 0) {
            _this.parent().next("ul").find("li").show();
        } else {
            _this.parent().next("ul").find("div").show();
        }
    } else {
        _this.parent().next("ul").find("li").hide();
        _this.parent().next("ul").find("li:contains('" + value + "')").show();
        if (_this.parent().next("ul").find("li:contains('" + value + "')").length == 0) {
            _this.parent().next("ul").find("div").show();
        }
    }
}

function inputSelectBlur(dom) {
    var _this = $(dom);
    var value = _this.val();
    var imitate_select_wrap = _this.parents(".imitate_select_wrap");
    var type = _this.parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    if (_this.val() != "") {
        _this.val(_this.attr("data-name"));
        //if(_this.parent().next("ul").find("li:contains('" + value + "')").length==0){
        //	_this.val("");
        //	_this.attr("data-type","");
        //	imitate_select_wrap.parent().next().hide();
        //}
    } else {
        imitate_select_wrap.parent().next().hide();
        _this.attr("data-type", "");
        _this.attr("data-name", "");
    }
    runtimeData(type)
}

function inputSelectFocus(dom) {
    var _this = $(dom);
    if (_this.parent().next("ul").find("li").length > 0) {
        _this.parent().next("ul").find("div").hide();
        _this.parent().next("ul").find("li").show();
    } else {
        _this.parent().next("ul").find("div").show();
    }
}

function getActivity() {
    var param = {};
    //var li = "<li data-type=''>请选择</li>";
    var li = "<div style='height: 30px;line-height: 30px;text-align: center;display: none'>无数据</div>";
    oc.postRequire("post", "/vipActivity/getAllActivityBySate", "", param, function(data) {
        if (data.code == "0") {
            var list = JSON.parse(data.message).list;
            if (list && list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    li += "<li data-type='" + list[i].activity_code + "' title='" + list[i].activity_theme + "'>" + list[i].activity_theme + "</li>";
                }
            } else {
                li = "<div style='height: 30px;line-height: 30px;text-align: center'>无数据</div>";
            }
            $(".center_item_group.select_activity").find(".select_activity_activity_item .imitate_select").eq(1).next("ul").html(li);
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

function getCouponSelect() {
    var param = {};
    param["app_id"] = "";
    //var li = "<li data-type=''>请选择</li>";
    var li = "<div style='height: 30px;line-height: 30px;text-align: center;display: none'>无数据</div>";
    oc.postRequire("post", "/vipRules/getAllCoupon", "", param, function(data) {
        if (data.code == "0") {
            var list = JSON.parse(data.message);
            if (list && list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    li += "<li data-type='" + list[i].couponcode + "' title='" + list[i].name + "'>" + list[i].name + "</li>";
                }
            } else {
                li = "<div style='height: 30px;line-height: 30px;text-align: center'>无数据</div>";
            }
            $(".center_item_group.select_coupon").children(".item_line").find(".select_coupon_right_line .imitate_select_wrap").find("ul").html(li)
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
//获取店铺列表
function getstorelist(a) {
    var searchValue = $("#store_search").val().trim();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};

    if (select_clickMark == "user") {
        var area_code = select_affiliation.staff.area;
        var brand_code = select_affiliation.staff.brand;
        _param['area_code'] = area_code.code;
        _param['brand_code'] = brand_code.code;
    } else {
        var area_code = select_affiliation.shop.area;
        var brand_code = select_affiliation.shop.brand;
        _param['area_code'] = area_code.code;
        _param['brand_code'] = brand_code.code;
    }
    //var area_code=select_affiliation.shop.area;
    //var brand_code=select_affiliation.shop.brand;
    //_param['area_code'] = area_code.code;
    //_param['brand_code'] = brand_code.code;
    _param['corp_code'] = select_corp_code;
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5); //加载等待框
    $("#mask").css("z-index", "10002");
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage = list.hasNextPage;
            var cout = list.total + 1;
            $("#checkShopAll").attr("data-count", list.total);
            var list = list.list;
            var store_html = '';
            var session_key = sessionStorage.getItem("key");
            var key_message = JSON.parse(session_key).message;
            var role_code = JSON.parse(key_message).role_code;
            $("#screen_shop .s_pitch span").eq(1).text(cout);
            if (list.length == 0) {

            } else {
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        var checked = "";
                        if ($("#screen_shop").is(":visible") && $("#checkShopAll").prop("checked")) {
                            checked = "checked"
                        }
                        store_html += "<li><div class='checkbox1'><input " + checked + " type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput" +
                            i +
                            a +
                            1 +
                            "'/><label for='checkboxTowInput" +
                            i +
                            a +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].store_name + "</span></li>"
                    }
                }
            }
            if (hasNextPage == true) {
                select_affiliation_init.shop_num++;
                select_affiliation_init.shop_next = false;
            }
            if (hasNextPage == false) {
                select_affiliation_init.shop_next = true;
            }
            if (a == 1 && searchValue == "" && role_code && (role_code.slice(1) - 5000 >= 0)) {
                $("#screen_shop .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-storename="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            if (!select_affiliation_init.isscroll) {
                $("#screen_shop .screen_content_l").unbind("scroll").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();

                    if (nScrollTop + nDivHight + 5 >= nScrollHight && nScrollTop != 0) {
                        if (select_affiliation_init.shop_next) {
                            return;
                        }
                        select_affiliation_init.shop_next = true;
                        getstorelist(select_affiliation_init.shop_num);
                    }
                })
            }
            select_affiliation_init.isscroll = true;
            var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_shop .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            whir.loading.remove(); //移除加载框
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
//获取活跃门店
function getactstorelist(a) {
    var searchValue = $("#act_store_search").val().trim();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param['corp_code'] = select_corp_code;
    //_param['area_code'] = select_affiliation.area.code;
    //_param['brand_code'] = select_affiliation.brand.code;
    _param['area_code'] = "";
    _param['brand_code'] = "";
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5); //加载等待框
    $("#mask").css("z-index", "10002");
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage = list.hasNextPage;
            var cout = 0;
            if (searchValue == "" && list.total != 0) {
                cout = list.total + 1
            } else if (searchValue == "" && list.total == 0) {
                cout = list.total
            }
            if (searchValue != "") {
                cout = list.total;
            }
            var list = list.list;
            var store_html = '';
            var session_key = sessionStorage.getItem("key");
            var key_message = JSON.parse(session_key).message;
            var role_code = JSON.parse(key_message).role_code;
            $("#screen_act_store .s_pitch span").eq(1).text(cout);
            if (list.length == 0) {

            } else {
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        store_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput" +
                            i +
                            a +
                            1 +
                            "'/><label for='checkboxTowInput" +
                            i +
                            a +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].store_name + "</span></li>"
                    }
                }
            }
            if (hasNextPage == true) {
                select_affiliation_init.act_store_num++;
                select_affiliation_init.act_store_next = false;
            }
            if (hasNextPage == false) {
                select_affiliation_init.act_store_next = true;
            }
            if (a == 1 && searchValue == "" && role_code && (role_code.slice(1) - 5000 >= 0)) {
                $("#screen_act_store .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-storename="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
            }
            $("#screen_act_store .screen_content_l ul").append(store_html);
            if (!select_affiliation_init.isscroll) {
                $("#screen_act_store .screen_content_l").unbind("scroll").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();

                    if (nScrollTop + nDivHight + 5 >= nScrollHight && nScrollTop != 0) {
                        if (select_affiliation_init.act_store_next) {
                            return;
                        }
                        select_affiliation_init.act_store_next = true;
                        getactstorelist(select_affiliation_init.act_store_num);
                    }
                })
            }
            select_affiliation_init.isscroll = true;
            var li = $("#screen_act_store .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_act_store .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            whir.loading.remove(); //移除加载框
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
//拉取区域
function getarealist(a) {
    var area_command = "/area/selAreaByCorpCode";
    var searchValue = $("#area_search").val().trim();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param['corp_code'] = select_corp_code;
    _param["searchValue"] = searchValue;
    _param["pageSize"] = pageSize;
    _param["pageNumber"] = pageNumber;
    whir.loading.add("", 0.5); //加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", area_command, "", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage = list.hasNextPage;
            var cout = list.total;
            var list = list.list;
            var area_html_left = '';
            $("#screen_area .s_pitch span").eq(1).text(cout);
            if (list.length == 0) {

            } else {
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        area_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].area_code + "' data-areaname='" + list[i].area_name + "' name='test'  class='check'  id='checkboxOneInput" +
                            i +
                            a +
                            1 +
                            "'/><label for='checkboxOneInput" +
                            i +
                            a +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].area_name + "</span></li>"
                    }
                }
            }
            if (hasNextPage == true) {
                select_affiliation_init.area_num++;
                select_affiliation_init.area_next = false;
            }
            if (hasNextPage == false) {
                select_affiliation_init.area_next = true;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            if (!select_affiliation_init.isscroll) {
                $("#screen_area .screen_content_l").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight + 5 >= nScrollHight && nScrollTop != 0) {
                        if (select_affiliation_init.area_next) {
                            return;
                        }
                        select_affiliation_init.area_next = true;
                        getarealist(select_affiliation_init.area_num);
                    }
                })
            }
            select_affiliation_init.isscroll = true;
            var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_area .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            whir.loading.remove(); //移除加载框
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
//获取品牌列表
function getbrandlist() {
    var searchValue = $("#brand_search").val().trim();
    var _param = {};
    _param['corp_code'] = select_corp_code;
    _param["searchValue"] = searchValue;
    whir.loading.add("", 0.5); //加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/shop/brand", "", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var total = message.total;
            var list = message.brands;
            var brand_html_left = '';
            $("#screen_brand .s_pitch span").eq(1).text(total);
            if (list.length == 0) {
                for (var h = 0; h < 9; h++) {
                    brand_html_left += "<li></li>"
                }
            } else {
                if (list.length < 9) {
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                    }
                    for (var j = 0; j < 9 - list.length; j++) {
                        brand_html_left += "<li></li>"
                    }
                } else if (list.length >= 9) {
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput" +
                            i +
                            1 +
                            "'/><label for='checkboxThreeInput" +
                            i +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].brand_name + "</span></li>"
                    }
                }
            }
            $("#screen_brand .screen_content_l ul").append(brand_html_left);
            var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_brand .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            whir.loading.remove(); //移除加载框
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
//获取员工列表
function getstafflist(a, b) {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    var searchValue = $('#staff_search').val().trim();
    if (b == "staff") {
        _param['store_code'] = $(tr[0]).attr("data-storecode"); //店铺
        _param['searchValue'] = $("#search_staff").val().trim(); //搜索值
    } else {
        if (select_type == "content_basic") {
            var brand_code = select_affiliation.basic_staff.brand;
            var area_code = select_affiliation.basic_staff.area;
            var shop_code = select_affiliation.basic_staff.shop;
        } else if (select_type == "content_high") {
            var brand_code = select_affiliation.staff.brand;
            var area_code = select_affiliation.staff.area;
            var shop_code = select_affiliation.staff.shop;
        }
        _param['area_code'] = area_code.code;
        _param['brand_code'] = brand_code.code;
        _param['store_code'] = shop_code.code;
        _param['searchValue'] = searchValue
    }
    _param["corp_code"] = select_corp_code;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5); //加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/user/selectUsersByRole", "", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage = list.hasNextPage;
            var cout = list.total + 1;
            var list = list.list;
            var staff_html = '';
            var choose_staff_html = '';
            $("#screen_staff .s_pitch span").eq(1).text(cout);
            if (list.length == 0) {

            } else {
                if (list.length > 0 && b == "staff") {
                    for (var j = 0; j < list.length; j++) {
                        choose_staff_html += "<li data-user_code='" + list[j].user_code + "' data-user_name='" + list[j].user_name + "'><span title='" + list[j].user_name + "'>" + list[j].user_name + "\(" + list[j].user_code + "\)</span><span class=\"select_this\">选择</span><span class=\"cancel_this\">取消</span></li>"
                    }
                } else if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-phone='" + list[i].phone + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput" +
                            i +
                            a +
                            1 +
                            "'/><label for='checkboxFourInput" +
                            i +
                            a +
                            1 +
                            "'></label></div><span class='p16'>" + list[i].user_name + "\(" + list[i].user_code + "\)</span></li>"
                    }
                }
            }
            if (hasNextPage == true) {
                select_affiliation_init.staff_num++;
                select_affiliation_init.staff_next = false;
            }
            if (hasNextPage == false) {
                select_affiliation_init.staff_next = true;
            }
            if (a == 1 && searchValue == "") {
                $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            $("#choose_staff .select_list ul").append(choose_staff_html);
            if (!select_affiliation_init.isscroll) {
                $("#screen_staff .screen_content_l").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight + 5 >= nScrollHight && nScrollTop != 0) {
                        if (select_affiliation_init.staff_next) {
                            return;
                        }
                        select_affiliation_init.staff_next = true;
                        getstafflist(select_affiliation_init.staff_num);
                    }
                });
                $("#choose_staff .select_list").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight >= nScrollHight) {
                        if (select_affiliation_init.staff_next) {
                            return;
                        }
                        getstafflist(select_affiliation_init.staff_num);
                    }
                })
            }
            select_affiliation_init.isscroll = true;
            var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            for (var k = 0; k < li.length; k++) {
                $("#screen_staff .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
            }
            whir.loading.remove(); //移除加载框
        } else if (data.code == "-1") {
            whir.loading.remove();
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                zIndex: 10003,
                content: data.message
            });
        }
    })
}

//获取分组
function getGroup() {
    var corp_command = "/vipGroup/getCorpGroupsAll";
    var _param = {};
    _param["corp_code"] = select_corp_code;
    _param["search_value"] = "";
    oc.postRequire("post", corp_command, "0", _param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var html = "";
            $("#screen_group .screen_content_l ul").empty();
            $("#screen_group .s_pitch span").eq(1).text(message.total);
            if (list.length > 0) {
                for (var i = 0; i < list.length; i++) {
                    html += "<li><div class='checkbox1'><input  data-type='" + list[i].group_type + "' type='checkbox' value='" + list[i].vip_group_code + "' name='test'  class='check'  id='checkboxOneInput" +
                        i +
                        "'/><label for='checkboxOneInput" +
                        i +
                        "'></label></div><span class='p16'>" + list[i].vip_group_name + "</span></li>"
                }
                $("#screen_group .screen_content_l ul").append(html);
                $("#group_search_f").trigger("click");
            } else if (list.length <= 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        }
    })
}
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li:visible");
    }
    //if(li.length>100){
    //    art.dialog({
    //        zIndex: 10003,
    //        time: 1,
    //        lock: true,
    //        cancel: false,
    //        content: "请选择少于100个"
    //    });
    //    return;
    //}
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
}
//移到左边
function removeLeft(a, b) {
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
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
    if ($("#screen_shop").is(":visible") && $("#checkShopAll").prop("checked")) {
        $("#checkShopAll").prop("checked", false);
        $("#checkShopAll").parent().next().css("visibility", "hidden");
    }
}
//区域搜索
$("#area_search").keydown(function() {
    var event = window.event || arguments[0];
    select_affiliation_init.area_num = 1;
    if (event.keyCode == 13) {
        select_affiliation_init.isscroll = false;
        $("#screen_area .screen_content_l").unbind("scroll");
        $("#screen_area .screen_content_l ul").empty();
        getarealist(select_affiliation_init.area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function() {
    var event = window.event || arguments[0];
    select_affiliation_init.shop_num = 1;
    if (event.keyCode == 13) {
        select_affiliation_init.isscroll = false;
        $("#screen_shop .screen_content_l ul").unbind("scroll");
        $("#screen_shop .screen_content_l ul").empty();
        getstorelist(select_affiliation_init.shop_num);
    }
});
//活跃门店搜索
$("#act_store_search").keydown(function() {
    var event = window.event || arguments[0];
    select_affiliation_init.act_store_num = 1;
    if (event.keyCode == 13) {
        select_affiliation_init.isscroll = false;
        $("#screen_act_store .screen_content_l ul").unbind("scroll");
        $("#screen_act_store .screen_content_l ul").empty();
        getactstorelist(select_affiliation_init.act_store_num);
    }
});
//品牌搜索
$("#brand_search").keydown(function() {
    var event = window.event || arguments[0];
    if (event.keyCode == 13) {
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
//员工搜索
$("#staff_search").keydown(function() {
    var event = window.event || arguments[0];
    select_affiliation_init.staff_num = 1;
    if (event.keyCode == 13) {
        select_affiliation_init.isscroll = false;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        getstafflist(select_affiliation_init.staff_num);
    }
});
//店铺放大镜搜索
$("#store_search_f").click(function() {
    select_affiliation_init.shop_num = 1;
    select_affiliation_init.isscroll = false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(select_affiliation_init.shop_num);
});
//活跃门店放大镜搜索
$("#act_store_search_f").click(function() {
    select_affiliation_init.act_store_num = 1;
    select_affiliation_init.isscroll = false;
    $("#screen_act_store .screen_content_l").unbind("scroll");
    $("#screen_act_store .screen_content_l ul").empty();
    getactstorelist(select_affiliation_init.act_store_num);
});
//区域放大镜收索
$("#area_search_f").click(function() {
    select_affiliation_init.area_num = 1;
    select_affiliation_init.isscroll = false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(select_affiliation_init.area_num);
});
//员工放大镜搜索
$("#staff_search_f").click(function() {
    select_affiliation_init.staff_num = 1;
    select_affiliation_init.isscroll = false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(select_affiliation_init.staff_num);
});
//品牌放大镜收索
$("#brand_search_f").click(function() {
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});

//区域关闭
$("#screen_close_area").click(function() {
    $("#screen_area").hide();
    if (select_clickMark == "user") {
        $("#screen_staff").show();
    } else if (select_clickMark == "shop") {
        $("#screen_shop").show();
    } else {
        $("#p").hide();
        $(".select_box_wrap").show();
    }
});
//员工关闭
$("#screen_close_staff").click(function() {
    $("#screen_staff").hide();
    $("#p").hide();
    $(".select_box_wrap").show();
});
//店铺关闭
$("#screen_close_shop").click(function() {
    if ($("#checkShopAll") && $("#checkShopAll").length > 0) {
        $("#checkShopAll").prop("checked", false);
        $("#checkShopAll").parent().next().css("visibility", "hidden")
    }
    $("#screen_shop").hide();
    if (select_clickMark == "user") {
        $("#screen_staff").show();
    } else {
        $("#p").hide();
        $(".select_box_wrap").show();
    }
});
//活跃门店关闭
$("#screen_close_act_store").click(function() {
    $("#screen_act_store").hide();
    $("#p").hide();
    $(".select_box_wrap").show();
});
//品牌关闭
$("#screen_close_brand").click(function() {
    $("#screen_brand").hide();
    if (select_clickMark == "user") {
        $("#screen_staff").show();
    } else if (select_clickMark == "shop") {
        $("#screen_shop").show();
    } else {
        $("#p").hide();
        $(".select_box_wrap").show();
    }
});

$("#screen_close_group").click(function() {
    $("#screen_group").hide();
    $("#p").hide();
    $(".select_box_wrap").show();
});

//店铺里面的区域点击
$("#shop_area").click(function() {
    select_clickMark = "shop";
    var area_code = select_affiliation.shop.area;
    if (area_code.code !== "") {
        var codes = area_code.code.split(',');
        var names = area_code.name.split(',');
        var html_right = "";
        for (var h = 0; h < codes.length; h++) {
            html_right += "<li id='" + codes[h] + "'>\
					            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
					            <label></div><span class='p16'>" + names[h] + "</span>\
					            \</li>"
        }
        $("#screen_area .s_pitch span").eq(0).html(h);
        $("#screen_area .screen_content_r ul").html(html_right);
    } else {
        $("#screen_area .s_pitch span").eq(0).html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    select_affiliation_init.isscroll = false;
    select_affiliation_init.area_num = 1;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(select_affiliation_init.area_num);
});
//店铺里面的品牌点击
$("#shop_brand").click(function() {
    select_clickMark = "shop";
    var brand_code = select_affiliation.shop.brand;
    if (brand_code.code !== "") {
        var codes = brand_code.code.split(',');
        var names = brand_code.name.split(',');
        var html_right = "";
        for (var h = 0; h < codes.length; h++) {
            html_right += "<li id='" + codes[h] + "'>\
					            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
					            <label></div><span class='p16'>" + names[h] + "</span>\
					            \</li>"
        }
        $("#screen_brand .s_pitch span").eq(0).html(h);
        $("#screen_brand .screen_content_r ul").html(html_right);
    } else {
        $("#screen_brand .s_pitch span").eq(0).html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
});
//员工里面的区域点击
$("#staff_area").click(function() {
    select_clickMark = "user";
    select_affiliation_init.area_num = 1;
    if (select_type == "content_basic") {
        var area_code = select_affiliation.basic_staff.area;
    } else if (select_type == "content_high") {
        var area_code = select_affiliation.staff.area;
    }
    //var area_code=select_affiliation.staff.area;
    if (area_code.code !== "") {
        var codes = area_code.code.split(',');
        var names = area_code.name.split(',');
        var html_right = "";
        for (var h = 0; h < codes.length; h++) {
            html_right += "<li id='" + codes[h] + "'>\
						            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
						            <label></div><span class='p16'>" + names[h] + "</span>\
						            \</li>"
        }
        $("#screen_area .s_pitch span").eq(0).html(h);
        $("#screen_area .screen_content_r ul").html(html_right);
    } else {
        $("#screen_area .s_pitch span").eq(0).html("0");
        $("#screen_area .screen_content_r ul").empty();
    }
    select_affiliation_init.isscroll = false;
    select_affiliation_init.area_num = 1;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(select_affiliation_init.area_num);
});
//员工里面的店铺点击
$("#staff_shop").click(function() {
    select_clickMark = "user";
    if (select_type == "content_basic") {
        var shop_code = select_affiliation.basic_staff.shop;
    } else if (select_type == "content_high") {
        var shop_code = select_affiliation.staff.shop;
    }
    //var shop_code=select_affiliation.staff.shop;
    if (shop_code.code !== "") {
        var codes = shop_code.code.split(',');
        var names = shop_code.name.split(',');
        var html_right = "";
        for (var h = 0; h < codes.length; h++) {
            html_right += "<li id='" + codes[h] + "'>\
					            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
					            <label></div><span class='p16'>" + names[h] + "</span>\
					            \</li>"
        }
        $("#screen_shop .s_pitch span").eq(0).html(h);
        $("#screen_shop .screen_content_r ul").html(html_right);
    } else {
        $("#screen_shop .s_pitch span").eq(0).html("0");
        $("#screen_shop .screen_content_r ul").empty();
    }
    select_affiliation_init.isscroll = false;
    select_affiliation_init.shop_num = 1;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(select_affiliation_init.shop_num);
});
//员工里面的品牌点击
$("#staff_brand").click(function() {
    select_clickMark = "user";
    if (select_type == "content_basic") {
        var brand_code = select_affiliation.basic_staff.brand;
    } else if (select_type == "content_high") {
        var brand_code = select_affiliation.staff.brand;
    }
    if (brand_code.code !== "") {
        var codes = brand_code.code.split(',');
        var names = brand_code.name.split(',');
        var html_right = "";
        for (var h = 0; h < codes.length; h++) {
            html_right += "<li id='" + codes[h] + "'>\
					            <div class='checkbox1'><input type='checkbox' value='" + codes[h] + "' name='test' class='check'>\
					            <label></div><span class='p16'>" + names[h] + "</span>\
					            \</li>"
        }
        $("#screen_brand .s_pitch span").eq(0).html(h);
        $("#screen_brand .screen_content_r ul").html(html_right);
    } else {
        $("#screen_brand .s_pitch span").eq(0).html("0");
        $("#screen_brand .screen_content_r ul").empty();
    }
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
});

//点击区域确定按钮
$("#screen_que_area").click(function() {
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
    $("#screen_area").hide();
    //select_affiliation.area = {
    //    code: area_codes,
    //    name: area_names
    //};
    //select_affiliation.shop = {
    //    code: "",
    //    name: ""
    //};
    //select_affiliation.staff = {
    //    code: "",
    //    name: ""
    //};
    if (select_clickMark == "user") {
        select_affiliation_init.staff_num = 1;
        select_affiliation_init.isscroll = false;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        if (select_type == "content_basic") {
            select_affiliation.basic_staff.area = {
                code: area_codes,
                name: area_names
            };
        } else if (select_type == "content_high") {
            select_affiliation.staff.area = {
                code: area_codes,
                name: area_names
            };
        }
        getstafflist(select_affiliation_init.staff_num);
        $("#screen_staff").show();
        $("#screen_staff .area_num").val("已选" + li.length + "个");
    } else if (select_clickMark == "shop") {
        select_affiliation_init.shop_num = 1;
        select_affiliation_init.isscroll = false;
        $("#screen_shop .screen_content_l").unbind("scroll");
        $("#screen_shop .screen_content_l ul").empty();
        select_affiliation.shop.area = {
            code: area_codes,
            name: area_names
        };
        getstorelist(select_affiliation_init.shop_num);
        $("#screen_shop").show();
        $("#screen_shop .area_num").val("已选" + li.length + "个");
        if ($("#screen_shop").is(":visible") && $("#checkShopAll").prop("checked")) {
            $("#checkShopAll").prop("checked", false);
            $("#checkShopAll").parent().next().css("visibility", "hidden")
        }
    } else {
        select_affiliation.area = {
            code: area_codes,
            name: area_names
        };
        $(".select_box_wrap").show();
        $("#p").hide();
        //$(".select_affiliation .item_list .select_affiliation_store").find("input").attr("data-code", "").attr("data-name", "").val("");
        //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
        $(".select_affiliation .item_list .select_show_area input").attr("data-code", area_codes);
        $(".select_affiliation .item_list .select_show_area input").attr("data-name", area_names);
        $(".select_affiliation .item_list .select_show_area input").val("已选" + li.length + "个");
        //$(".area_num").val("已选" + li.length + "个");
        if ($(".select_affiliation .item_list .select_show_area").length > 0) {
            var type = $(".select_affiliation .item_list .select_show_area").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
            runtimeData(type);
        }
    }
    //$(".select_affiliation .item_list .select_affiliation_store").find("input").attr("data-code", "").attr("data-name", "").val("");
    //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
    //$(".select_affiliation .item_list .select_show_area input").attr("data-code", area_codes);
    //$(".select_affiliation .item_list .select_show_area input").attr("data-name", area_names);
    //$(".select_affiliation .item_list .select_show_area input").val("已选" + li.length + "个");
    //$(".area_num").val("已选" + li.length + "个");
    //if($(".select_affiliation .item_list .select_show_area").length>0){
    //    var type = $(".select_affiliation .item_list .select_show_area").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    //    runtimeData(type);
    //}
});
//点击品牌确定按钮
$("#screen_que_brand").click(function() {
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
    $("#screen_brand").hide();
    //select_affiliation.brand = {
    //    code: brand_codes,
    //    name: brand_names
    //};
    //select_affiliation.shop = {
    //    code: "",
    //    name: ""
    //};
    //select_affiliation.staff = {
    //    code: "",
    //    name: ""
    //};
    if (select_clickMark == "user") {
        select_affiliation_init.staff_num = 1;
        select_affiliation_init.isscroll = false;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        if (select_type == "content_basic") {
            select_affiliation.basic_staff.brand = {
                code: brand_codes,
                name: brand_names
            };
        } else if (select_type == "content_high") {
            select_affiliation.staff.brand = {
                code: brand_codes,
                name: brand_names
            };
        }
        getstafflist(select_affiliation_init.staff_num);
        $("#screen_staff").show();
        $("#screen_staff .brand_num").val("已选" + li.length + "个");
    } else if (select_clickMark == "shop") {
        select_affiliation_init.shop_num = 1;
        select_affiliation_init.isscroll = false;
        $("#screen_shop .screen_content_l").unbind("scroll");
        $("#screen_shop .screen_content_l ul").empty();
        select_affiliation.shop.brand = {
            code: brand_codes,
            name: brand_names
        };
        getstorelist(select_affiliation_init.shop_num);
        $("#screen_shop").show();
        $("#screen_shop .brand_num").val("已选" + li.length + "个");
        if ($("#screen_shop").is(":visible") && $("#checkShopAll").prop("checked")) {
            $("#checkShopAll").prop("checked", false);
            $("#checkShopAll").parent().next().css("visibility", "hidden")
        }
    } else {
        select_affiliation.brand = {
            code: brand_codes,
            name: brand_names
        };
        $(".select_box_wrap").show();
        $("#p").hide();
        $(".select_affiliation .item_list .select_show_brand input").attr("data-code", brand_codes);
        $(".select_affiliation .item_list .select_show_brand input").attr("data-name", brand_names);
        $(".select_affiliation .item_list .select_show_brand input").val("已选" + li.length + "个");
        if ($(".select_affiliation .item_list .select_show_brand").length > 0) {
            var type = $(".select_affiliation .item_list .select_show_brand").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
            runtimeData(type);
        }
    }
    //$(".select_affiliation .item_list .select_affiliation_store").find("input").attr("data-code", "").attr("data-name", "").val("");
    //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
    //$(".select_affiliation .item_list .select_show_brand input").attr("data-code", brand_codes);
    //$(".select_affiliation .item_list .select_show_brand input").attr("data-name", brand_names);
    //$(".select_affiliation .item_list .select_show_brand input").val("已选" + li.length + "个");
    //$(".brand_num").val("已选" + li.length + "个");
    //if($(".select_affiliation .item_list .select_show_brand").length>0){
    //    var type = $(".select_affiliation .item_list .select_show_brand").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    //    runtimeData(type);
    //}
});
//点击店铺确定按钮
$("#screen_que_shop").click(function() {
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
    if ($("#checkShopAll") && $("#checkShopAll").length > 0) {
        $("#checkShopAll").prop("checked", false);
        $("#checkShopAll").parent().next().css("visibility", "hidden")
    }
    $("#screen_shop").hide();
    //select_affiliation.shop = {
    //    code: store_codes,
    //    name: store_names
    //};
    //
    //select_affiliation.staff = {
    //    code: "",
    //    name: ""
    //};
    select_affiliation_init.isscroll = false;
    if (select_clickMark == "user") {
        select_affiliation_init.staff_num = 1;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        if (select_type == "content_basic") {
            select_affiliation.basic_staff.shop = {
                code: store_codes,
                name: store_names
            };
        } else if (select_type == "content_high") {
            select_affiliation.staff.shop = {
                code: store_codes,
                name: store_names
            };
        }
        getstafflist(select_affiliation_init.staff_num);
        $("#screen_staff").show();
        $("#staff_shop_num").val("已选" + li.length + "个");
    } else {
        select_affiliation.shop.code = store_codes;
        select_affiliation.shop.name = store_names;
        $(".select_box_wrap").show();
        $("#p").hide();
        //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
        $(".select_affiliation .item_list .select_show_shop").find("input").attr("data-code", store_codes);
        $(".select_affiliation .item_list .select_show_shop").find("input").attr("data-name", store_names);
        $(".select_affiliation .item_list .select_show_shop").find("input").val("已选" + li.length + "个");
        //$("#staff_shop_num").val("已选" + li.length + "个");
        if ($(".select_affiliation .item_list .select_show_shop").length > 0) {
            var type = $(".select_affiliation .item_list .select_show_shop").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
            runtimeData(type);
        }
    }
    //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
    //$(".select_affiliation .item_list .select_show_shop").find("input").attr("data-code", store_codes);
    //$(".select_affiliation .item_list .select_show_shop").find("input").attr("data-name", store_names);
    //$(".select_affiliation .item_list .select_show_shop").find("input").val("已选" + li.length + "个");
    //$("#staff_shop_num").val("已选" + li.length + "个");
    //if($(".select_affiliation .item_list .select_show_shop").length>0){
    //    var type = $(".select_affiliation .item_list .select_show_shop").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    //    runtimeData(type);
    //}
});

//点击活跃门店确定按钮
$("#screen_que_act_store").click(function() {
    var li = $("#screen_act_store .screen_content_r input[type='checkbox']").parents("li");
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
    $("#screen_act_store").hide();
    select_affiliation.act_store = {
        code: store_codes,
        name: store_names
    };

    select_affiliation_init.isscroll = false;
    //if (select_clickMark == "user") {
    //	select_affiliation_init.staff_num = 1;
    //	$("#screen_staff .screen_content_l").unbind("scroll");
    //	$("#screen_staff .screen_content_l ul").empty();
    //	getstafflist(select_affiliation_init.staff_num);
    //	$("#screen_staff").show();
    //}else{
    //	$(".select_box_wrap").show();
    //	$("#p").hide();
    //}
    $(".select_box_wrap").show();
    $("#p").hide();
    //$(".select_affiliation .item_list .select_affiliation_staff").find("input").attr("data-code", "").attr("data-name", "").val("");
    $(".item_list .select_show_act_store").attr("data-code", store_codes);
    $(".item_list .select_show_act_store").attr("data-name", store_names);
    $(".item_list .select_show_act_store").val("已选" + li.length + "个");
    //$("#staff_shop_num").val("已选" + li.length + "个");
    if ($(".item_list .select_show_act_store").length > 0) {
        var type = $(".item_list .select_show_act_store").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
        runtimeData(type);
    }
});
//点击员工确定按钮
$("#screen_que_staff").click(function() {
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
    $("#screen_staff").hide();
    if (select_type == "content_basic") {
        select_affiliation.basic_staff.code = user_codes;
        select_affiliation.basic_staff.name = user_names;
    } else if (select_type == "content_high") {
        select_affiliation.staff.code = user_codes;
        select_affiliation.staff.name = user_names;
    }

    $(".select_box_wrap").show();
    $(".item_list:visible .select_show_staff input").attr("data-code", user_codes);
    $(".item_list:visible .select_show_staff input").attr("data-name", user_names);
    $(".item_list:visible .select_show_staff input").val("已选" + li.length + "个");
    $("#p").hide();
    var type = $(".item_list:visible .select_show_staff").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    runtimeData(type);
});

//分组搜索
$("#group_search").keydown(function() {
    var event = window.event || arguments[0];
    //if (event.keyCode == 13) {
    //	getGroup();
    //}
    if (event.keyCode == 13) {
        var value = $(this).val().trim();
        if (value == "") {
            $("#screen_group .screen_content_l ul li").show();
        } else {
            $("#screen_group .screen_content_l ul li").hide();
            $("#screen_group .screen_content_l ul li:contains('" + value + "')").show();
        }
        $("#screen_group .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
        $("#screen_group .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
    }
});
$("#group_search_f").click(function() {
    var value = $("#group_search").val().trim();
    if (value == "") {
        $("#screen_group .screen_content_l ul li").show();
    } else {
        $("#screen_group .screen_content_l ul li").hide();
        $("#screen_group .screen_content_l ul li:contains('" + value + "')").show();
    }
    $("#screen_group .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
    $("#screen_group .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
});
//分组确定
$("#screen_que_group").click(function() {
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
    $("#screen_group").hide();
    select_affiliation.group = {
        code: group_codes,
        name: group_names
    };
    $(".select_box_wrap").show();
    $(".item_list:visible .select_show_group input").attr("data-code", group_codes);
    $(".item_list:visible .select_show_group input").attr("data-name", group_names);
    $(".item_list:visible .select_show_group input").val("已选" + li.length + "个");
    $("#p").hide();
    isSelectNew = false;
    var type = $(".item_list:visible .select_show_group").parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    runtimeData(type);
});
//筛选确定
$("#select_sure").click(function() {
    getSelectData().then(function(screenData) {
        _param['screen'] = screenData;
        inx = 1;
        var corp_code = $("#OWN_CORP").val();
        if (corp_code !== "" && corp_code !== undefined) {
            _param["corp_code"] = corp_code;
        } else {
            _param["corp_code"] = select_corp_code;
        }
        _param["pageNumber"] = inx;
        _param["pageSize"] = pageSize;
        $(".select_box_wrap").hide();
        $(".select_box_content_left .item_group").find("ul").slideUp().next(".group_arrow").removeClass("item_group_active");
        if (screenData.length == 0) {
            GET(inx, pageSize);
        } else {
            filtrate = "success";
            filtrates(inx, pageSize);
        }
        $("#search").val("");
        value = "";
        $("body").css("overflow", "auto")
    })
});

function getSelectData() {
    var content = $(".select_box .select_box_content:visible").attr("id");
    var screen = [];
    var group = $("#" + content).find(".select_box_content_center_box:visible").children(":visible");
    var SelectDataDef = $.Deferred();
    select_type = content;
    group.map(function() {
        var type = $(this).attr("data-type");
        var key = $(this).attr("data-key");
        var item_line = $(this).find(".item_list .item_line");
        var opera = "";
        var _group = $(this);

        function getLogic(logic_default) {
            switch (logic_default) {
                case "包含":
                    return "contains";
                    break;
                case "不包含":
                    return "not contains";
                    break;
                case "等于":
                    return "equal";
                    break;
                case "不等于":
                    return "not equal";
                    break;
                case "为空":
                    return "null";
                    break;
                case "不为空":
                    return "not null";
                    break;
                case "大于":
                    return "{";
                    break;
                case "小于":
                    return "}";
                    break;
                case "大于等于":
                    return "[";
                    break;
                case "小于等于":
                    return "]";
                    break;
            }
        }

        var obj = { "key": key, "value": [], "type": "interval" };
        if (type == "affiliation") {
            //if($("#content_high").is(":visible")){
            var value = "";
            var name = "";
            var groupName = "";
            item_line.map(function() {
                value = $(this).find("input").attr("data-code");
                name = $(this).find("input").attr("data-name");
                groupName = $(this).find(".center_item_group_title").html();
                key = $(this).attr("data-key");
                var logic_default = $(this).find(".imitate_select_wrap").eq(0).find(".imitate_select span").html();
                var logic = getLogic(logic_default);
                obj = { "key": key, "value": value, "logic": logic, "name": name, "type": "text", "groupName": groupName };
                if (obj.value.length != 0) {
                    screen.push(obj)
                }
            });
        } else if (type == "affiliation_other") {
            var value = item_line.find("input").attr("data-code");
            var name = item_line.find("input").attr("data-name");
            var groupName = _group.children(".center_item_group_title").html();
            if (key == "user_code") {
                var logic_default = $(this).find(".imitate_select_wrap").eq(0).find(".imitate_select span").html();
                var logic = getLogic(logic_default);
                obj = { "key": key, "value": value, "logic": logic, "name": name, "type": "text", "groupName": groupName };
            } else {
                obj = { "key": key, "value": value, "name": name, "type": "text", "groupName": groupName };
            }
            if (obj.value.length != 0) {
                screen.push(obj)
            }
        } else {
            var groupName = _group.children(".center_item_group_title").html();
            switch (type) {
                case "text":
                    if (key == "ZONE") {
                        item_line.map(function() {
                            var span = $(this).find(".select_city_group .imitate_select_wrap .imitate_select span");
                            if (span.is(":visible")) {
                                var value = span.eq(0).html() + "-" + span.eq(1).html() + "-" + span.eq(2).html();
                            } else {
                                var value = "";
                            }
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            if (value != "省-市-区") {
                                obj.value.push({ "opera": opera, "logic": logic, "value": value })
                            }
                        });
                        if (obj.value.length > 0) {
                            obj.value[0].opera = "";
                        }
                    } else if (key == "APP_ID" || key == "act_store") {
                        item_line.map(function() {
                            var value = $(this).find("input:text").attr("data-code");
                            var name = $(this).find("input:text").attr("data-name");
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            obj.value.push({ "opera": opera, "logic": logic, "value": value, "name": name })
                        })
                    } else if (key == "CARD_TYPE_ID") {
                        item_line.map(function() {
                            var value = $(this).find("input:text").attr("data-name");
                            var code = $(this).find("input:text").attr("data-code");
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            obj.value.push({ "opera": opera, "logic": logic, "value": value, "code": code })
                        })
                    } else {
                        item_line.map(function() {
                            if ($(this).find("textarea").length > 0) {
                                if ($(this).find("textarea").is(":visible")) {
                                    var value = $(this).find("textarea").val().trim();
                                } else {
                                    var value = ""
                                }
                            } else {
                                if ($(this).find("input:text").is(":visible")) {
                                    var value = $(this).find("input:text").val().trim();
                                } else {
                                    var value = ""
                                }
                            }
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic_default = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic_default);
                            if (value != "" || (value == "" && (logic == "not null" || logic == "null"))) {
                                obj.value.push({ "opera": opera, "logic": logic, "value": value })
                            }
                            if (obj.value.length > 0) {
                                obj.value[0].opera = "";
                            }
                        });
                    }
                    obj.type = "text";
                    break;
                case "interval":
                    if (key == "TRADE" || key == "COUPON") {
                        var selectType = $(this).attr("class").replace(/center_item_group select_/, "");
                        var key_second_I = "";
                        item_line = $(this).find(".item_list .item_line .select_" + selectType + "_right_line+div:visible").parents(".item_line");
                        var len = item_line.length - 1;
                        for (var s = len; s >= 0; s--) {
                            var logic_1 = $(item_line[s]).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic_2 = $(item_line[s]).find(".imitate_select_wrap").eq(3).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            var name = $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            if (s != len) {
                                opera = $(item_line[s]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[s]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[s]).find("input").eq(1).val().trim();
                            if (key == "COUPON") {
                                start_val = $(item_line[s]).find("input").eq(1).val().trim();
                                end_val = $(item_line[s]).find("input").eq(2).val().trim();
                                name = $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
                                var val = $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type").trim();
                                if (val != "") {
                                    key_second_I = "VOUTYPE_" + val + "_" + $(item_line[s]).attr("data-key");
                                } else {
                                    key_second_I = "VOUTYPE_" + $(item_line[s]).attr("data-key");
                                }
                            } else {
                                if ($(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").is(":hidden")) {
                                    key_second_I = $(item_line[s]).attr("data-key");
                                } else {
                                    key_second_I = $(item_line[s]).attr("data-key") + "_" + $(item_line[s]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-type").trim();
                                }

                            }
                            if (start_val != "" || end_val != "") {
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val,
                                    "key": key_second_I,
                                    "name": name
                                })
                            }
                        }
                    } else if (key == "CATA1_PRD" || key == "CATA2_PRD" || key == "CATA3_PRD" || key == "CATA4_PRD" || key == "SEASON_PRD" || key == "T_BL_M" || key == "DAYOFWEEK" || key == "BRAND_ID" || key == "CATA3_PRD" || key == "STORE_AREA" || key == "PRICE_CODE") {
                        item_line = $(this).find(".item_list .item_line .select_dimension+div:visible").parents(".item_line");
                        var len = item_line.length - 1;
                        for (T = len; T >= 0; T--) {
                            var logic_1 = $(item_line[T]).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic_2 = $(item_line[T]).find(".imitate_select_wrap").eq(3).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            var key_second_I = $(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-value");
                            var name = $(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            if ($(item_line[T]).index() != 0) {
                                opera = $(item_line[T]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(item_line[T]).find("input").eq(0).val().trim();
                            var end_val = $(item_line[T]).find("input").eq(1).val().trim();
                            if (start_val != "" || end_val != "") {
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val,
                                    "key": key_second_I,
                                    "name": name
                                })
                            }
                        }
                    } else {
                        item_line.map(function() {
                            var logic_1 = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic_2 = $(this).find(".imitate_select_wrap").eq(2).find(".imitate_select span").html();
                            var logic1 = getLogic(logic_1);
                            var logic2 = getLogic(logic_2);
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var start_val = $(this).find("input").eq(0).val().trim();
                            var end_val = $(this).find("input").eq(1).val().trim();
                            if (start_val != "" || end_val != "") {
                                obj.value.push({
                                    "opera": opera,
                                    "logic1": logic1,
                                    "logic2": logic2,
                                    "start": start_val,
                                    "end": end_val
                                });
                            }
                        });
                    }
                    if (obj.value.length > 0) {
                        obj.value[0].opera = "";
                    }
                    obj.type = "interval";
                    break;
                case "radio":
                    if (key == "ACTIVITY" || key == "TASK") {
                        var selectTypeR = $(this).attr("class").replace(/center_item_group select_/, "");
                        item_line = $(this).find(".item_list .item_line .select_" + selectTypeR + "_right_line+div:visible").parents(".item_line");
                        var len_r = item_line.length - 1;
                        for (var T = len_r; T >= 0; T--) {
                            if (T != len_r) {
                                opera = $(item_line[T]).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            if ($(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type").trim() != "") {
                                var key_second = key + "_" + $(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type");
                                var value = $(item_line[T]).find("input:checked").val();
                                var name = $(item_line[T]).find(".imitate_select_wrap").eq(1).find(".imitate_select input").val();
                                var dataName = $(item_line[T]).find("input:checked").parent().next().html();
                                obj.value.push({
                                    "opera": opera,
                                    "logic": "",
                                    "value": value,
                                    "key": key_second,
                                    "name": name,
                                    "dataName": dataName
                                })
                            }
                        }
                    } else if (key == "SEX_VIP") {
                        item_line.map(function() {
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var logic = $(this).find(".imitate_select_wrap").eq(1).find(".imitate_select span").html();
                            var logic = getLogic(logic);
                            var value = $(this).find("input:checked").val();
                            var dataName = $(this).find("input:checked").parent().next().html();
                            obj.value.push({ "opera": opera, "logic": logic, "value": value, "dataName": dataName });
                        });
                    } else {
                        item_line.map(function() {
                            if ($(this).index() != 0) {
                                opera = $(this).find(".andOrNot .imitate_select span").eq(0).html() == "并且" ? "AND" : "OR";
                            }
                            var value = $(this).find("input:checked").val();
                            var dataName = $(this).find("input:checked").parent().next().html();
                            obj.value.push({ "opera": opera, "logic": "", "value": value, "dataName": dataName })
                        });
                    }
                    obj.type = "radio";
                    break
            }
            if (obj.value.length != 0) {
                obj.groupName = groupName;
                screen.push(obj)
            }
        }
    });
    SelectDataDef.resolve(screen);
    return SelectDataDef
}
//点击右移
$(".shift_right").click(function() {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function() {
    var right = "all";
    var div = $(this);
    if ($("#screen_shop").is(":visible") && $("#checkShopAll").prop("checked")) {
        var a = 1;
        var searchValue = $("#store_search").val().trim();
        var pageSize = Number($("#checkShopAll").attr("data-count"));
        var pageNumber = a;
        var _param = {};
        if (select_clickMark == "user") {
            var area_code = select_affiliation.staff.area;
            var brand_code = select_affiliation.staff.brand;
            _param['area_code'] = area_code.code;
            _param['brand_code'] = brand_code.code;
        } else {
            var area_code = select_affiliation.shop.area;
            var brand_code = select_affiliation.shop.brand;
            _param['area_code'] = area_code.code;
            _param['brand_code'] = brand_code.code;
        }
        _param['corp_code'] = select_corp_code;
        _param['searchValue'] = searchValue;
        _param['pageNumber'] = pageNumber;
        _param['pageSize'] = pageSize;
        whir.loading.add("", 0.5); //加载等待框
        $("#mask").css("z-index", "10002");
        oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function(data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var count = list.total + 1;
                var list = list.list;
                var store_html = '';
                var session_key = sessionStorage.getItem("key");
                var key_message = JSON.parse(session_key).message;
                var role_code = JSON.parse(key_message).role_code;
                if (list.length == 0) {

                } else {
                    if (list.length > 0) {
                        for (var i = 0; i < list.length; i++) {
                            var id = list[i].store_code;
                            var hasVal = false;
                            var input = $(div).parents(".screen_content").find(".screen_content_r li");
                            for (var j = 0; j < input.length; j++) {
                                if ($(input[j]).attr("id") == id) {
                                    hasVal = true
                                }
                            }
                            if (!hasVal) {
                                store_html += "<li id='" + list[i].store_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput" +
                                    i +
                                    a +
                                    1 +
                                    "'/><label for='checkboxTowInput" +
                                    i +
                                    a +
                                    1 +
                                    "'></label></div><span class='p16'>" + list[i].store_name + "</span></li>";
                            }
                        }
                    }
                }
                if (a == 1 && searchValue == "" && role_code && (role_code.slice(1) - 5000 >= 0)) {
                    if ($("#screen_shop .screen_content_r ul li#无").length == 0) {
                        $("#screen_shop .screen_content_r ul").append('<li id="无"><div class="checkbox1"><input type="checkbox" value="无" data-storename="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
                    }
                }
                $("#screen_shop .screen_content_r ul").append(store_html);
                $("#screen_shop .s_pitch span").eq(0).text($("#screen_shop .screen_content_r ul li").length);
                $("#screen_shop .screen_content_l ul li").find("input").prop("checked", true);
                whir.loading.remove(); //移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    } else {
        removeRight(right, div);
    }
});
//点击左移
$(".shift_left").click(function() {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function() {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
});
getCouponSelect();
getActivity();
getTask();
getProvinceSelect();
if (!$(".select_box_wrap").hasClass("isGroupAdd") && !$(".select_box_wrap").hasClass("deferredGroup")) {
    expend_data_1();
}
$(document).click(function(e) {
    var _con = $('.expend_select_wrap'); // 设置目标区域
    var _con_1 = $('.imitate_select_wrap'); // 设置目标区域
    var dimension = $('.dimension'); // 设置目标区域
    if (!_con.is(e.target) && _con.has(e.target).length === 0) {
        _con.find("ul").hide()
    }
    if (!_con_1.is(e.target) && _con_1.has(e.target).length === 0) {
        _con_1.find("ul").hide()
    }
    if (!dimension.is(e.target) && dimension.has(e.target).length === 0) {
        dimension.hide();
        $(".select_box #select_tyle_wrap").hide();
    }

});
$(".select_box").on("click", ".laydate-icon", function() {
    var is_expend = $(this).parents(".center_item_group").attr("data-expend") == "is_expend";
    if ($(this).parents(".center_item_group").hasClass("select_birthday") || is_expend == true) {
        $("#laydate_YY").hide()
    } else {
        $("#laydate_YY").show()
    }
});

//点击列表显示选中状态
$(".screen_content").on("click", "li", function() {
    var input = $(this).find("input")[0];
    if (input.type == "checkbox" && input.checked == false) {
        input.checked = true;
    } else if (input.type == "checkbox" && input.checked == true) {
        input.checked = false;
    }
});

function getLogicDefault(logic) {
    switch (logic) {
        case "contains":
            return "包含";
            break;
        case "not contains":
            return "不包含";
            break;
        case "equal":
            return "等于";
            break;
        case "not equal":
            return "不等于";
            break;
        case "null":
            return "为空";
            break;
        case "not null":
            return "不为空";
            break;
        case "{":
            return "大于";
            break;
        case "}":
            return "小于";
            break;
        case "[":
            return "大于等于";
            break;
        case "]":
            return "小于等于";
            break;
        default:
            return ""
    }
}

function fuzhiSelect() {
    var list = _param.screen;
    for (var i = 0; i < list.length; i++) {
        var key = list[i].key;
        var type = list[i].type;
        var value = list[i].value;
        if ((key == "brand_code" || key == "area_code" || key == "store_code" || key == "user_code") && $("#content_high").is(":visible")) {
            var wrapHtml = $("#content_high .select_box_content_center .select_box_content_center_box .select_affiliation");
        } else {
            var wrapHtml = $("#" + select_type + " .select_box_content_center .select_box_content_center_box .center_item_group[data-key=" + key + "]");
        }
        wrapHtml.show();
        if ((key == "brand_code" || key == "area_code" || key == "store_code" || key == "user_code") && $("#content_high").is(":visible")) {
            cloneHtml = wrapHtml.find(".item_list").nextAll("[data-key=" + key + "]").clone();
            var name = list[i].name;
            var logic_data = getLogicDefault(list[i].logic);
            var typeChild = "";
            switch (key) {
                case "brand_code":
                    typeChild = "brand";
                    break;
                case "area_code":
                    typeChild = "area";
                    break;
                case "store_code":
                    typeChild = "shop";
                    break;
                case "user_code":
                    typeChild = "staff";
                    break;
            }
            select_affiliation[typeChild].code = value;
            select_affiliation[typeChild].name = name;
            cloneHtml.find("input").attr("data-code", value);
            cloneHtml.find("input").attr("data-name", name);
            cloneHtml.find("input").val("已选" + value.split(",").length + "个");
            cloneHtml.find(".imitate_select_wrap .imitate_select span").eq(0).html(logic_data);
            wrapHtml.find(".item_list").append(cloneHtml.show());
        } else if (key == "user_code") {
            var name = list[i].name;
            var cloneHtml = wrapHtml.find(".item_list").next().clone();
            var logic_data = getLogicDefault(list[i].logic);
            cloneHtml.find("input").attr("data-code", value);
            cloneHtml.find("input").attr("data-name", name);
            select_affiliation.basic_staff.code = value;
            select_affiliation.basic_staff.name = name;
            cloneHtml.find("input").val("已选" + value.split(",").length + "个");
            cloneHtml.find(".imitate_select_wrap .imitate_select span").eq(0).html(logic_data);
            wrapHtml.find(".item_list").append(cloneHtml.show());
        } else if (key == "VIP_GROUP_CODE") {
            var name = list[i].name;
            var cloneHtml = wrapHtml.find(".item_list").next().clone();
            cloneHtml.find("input").attr("data-code", value);
            cloneHtml.find("input").attr("data-name", name);
            select_affiliation.group.code = value;
            select_affiliation.group.name = name;
            cloneHtml.find("input").val("已选" + value.split(",").length + "个");
            wrapHtml.find(".item_list").append(cloneHtml.show());
        } else {
            switch (type) {
                case "radio":
                    for (var d = 0; d < value.length; d++) {
                        var cloneHtml = wrapHtml.find(".item_list").next().clone();
                        var logic = value[d].logic;
                        var logic_data = "";
                        var opera = value[d].opera;
                        var input_radio = cloneHtml.find("input[type='radio']");
                        if (input_radio.length > 0) {
                            input_radio.map(function(index, ele) {
                                var index = wrapHtml.find(".item_list").children().length;
                                var id = $(ele).attr("id");
                                var name = $(ele).attr("name");
                                $(ele).attr("id", id + index);
                                $(ele).attr("name", name + index);
                                $(ele).next().attr("for", id + index);
                                if ($(ele).val() == value[d].value) {
                                    $(ele).parents(".select_radio_group").find("input[type='radio']").prop("checked", false);
                                    $(ele).prop("checked", true);
                                }
                            })
                        }
                        if (key == "SEX_VIP") {
                            logic_data = getLogicDefault(logic);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_data)
                        } else if (key == "ACTIVITY" || key == "TASK") {
                            var child_key = value[d].key.replace(key + "_", "");
                            var selectType = wrapHtml.attr("class").replace(/center_item_group select_/, "");
                            logic_data = getLogicDefault(logic);
                            if (d > 0) {
                                opera = opera == "AND" ? "并且" : opera == "OR" ? "或者" : "";
                                cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                            }
                            var child_key_name = cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select").next("ul").find("li[data-type=" + child_key + "]").html();
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").val(child_key_name);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type", child_key);
                            cloneHtml.find(".select_" + selectType + "_right_line+div").show();
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break;
                case "text":
                    for (var t = 0; t < value.length; t++) {
                        var cloneHtml = wrapHtml.find(".item_list").next().clone();
                        var logic = value[t].logic;
                        var logic_data = "";
                        var opera = value[t].opera;
                        if (t > 0) {
                            opera = opera == "AND" ? "并且" : opera == "OR" ? "或者" : "";
                            cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                        }
                        logic_data = getLogicDefault(logic);
                        cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_data);
                        if (logic_data == "为空" || logic_data == "不为空") {
                            cloneHtml.find(".imitate_select_wrap").eq(1).next().hide();
                        } else {
                            if (key == "ZONE") {
                                (function(t) {
                                    var zone = value[t].value.split("-");
                                    var provinceSelect = cloneHtml.find(".select_city_group .imitate_select").eq(0);
                                    var citySelect = cloneHtml.find(".select_city_group .imitate_select").eq(1);
                                    var areaSelect = cloneHtml.find(".select_city_group .imitate_select").eq(2);
                                    provinceSelect.find("span").html(zone[0]);
                                    citySelect.find("span").html(zone[1]);
                                    areaSelect.find("span").html(zone[2]);
                                })(t)
                            } else if (key == "APP_ID") {
                                if (value[t].value == "") {
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code", value[t].value);
                                    cloneHtml.find("input:text").attr("data-name", value[t].name);
                                } else {
                                    select_affiliation["public_signal"].code = value[t].value;
                                    select_affiliation["public_signal"].name = value[t].name;
                                    cloneHtml.find("input:text").val("已选" + value[t].value.split(",").length + "个");
                                    cloneHtml.find("input:text").attr("data-code", value[t].value);
                                    cloneHtml.find("input:text").attr("data-name", value[t].name);
                                }

                            } else if (key == "act_store") {
                                if (value[t].value == "") {
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code", value[t].value);
                                    cloneHtml.find("input:text").attr("data-name", value[t].name);
                                } else {
                                    select_affiliation["act_store"].code = value[t].value;
                                    select_affiliation["act_store"].name = value[t].name;
                                    cloneHtml.find("input:text").val("已选" + value[t].value.split(",").length + "个");
                                    cloneHtml.find("input:text").attr("data-code", value[t].value);
                                    cloneHtml.find("input:text").attr("data-name", value[t].name);
                                }

                            } else if (key == "CARD_TYPE_ID") {
                                if (value[t].value == "") {
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code", value[t].code);
                                    cloneHtml.find("input:text").attr("data-name", value[t].value);
                                } else {
                                    select_affiliation["vip_type"].code = value[t].code;
                                    select_affiliation["vip_type"].name = value[t].value;
                                    cloneHtml.find("input:text").val("已选" + value[t].value.split(",").length + "个");
                                    cloneHtml.find("input:text").attr("data-code", value[t].code);
                                    cloneHtml.find("input:text").attr("data-name", value[t].value);
                                }
                            } else {
                                if (cloneHtml.find("textarea").length > 0) {
                                    cloneHtml.find("textarea").val(value[t].value);
                                } else {
                                    cloneHtml.find("input:text").val(value[t].value);
                                }
                            }
                            if (wrapHtml.attr("data-expend") !== undefined && cloneHtml.has(".expend_select_wrap")) { //拓展资料，给多选赋值
                                var valArray = value[t].value.split(",");
                                var expendLi = cloneHtml.find(".expend_select_wrap ul li");
                                expendLi.map(function(dex, ele) {
                                    for (var c = 0; c < valArray.length; c++) {
                                        if ($(ele).find("span").html() == valArray[c]) {
                                            $(ele).find("input").attr("checked", true)
                                        }
                                    }
                                })
                            }
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break;
                case "interval":
                    for (var l = 0; l < value.length; l++) {
                        var cloneHtml = wrapHtml.find(".item_list").next().clone();
                        var logic_1 = value[l].logic1;
                        var logic_2 = value[l].logic2;
                        var opera = value[l].opera;
                        logic_1 = getLogicDefault(logic_1);
                        logic_2 = getLogicDefault(logic_2);
                        if (key == "TRADE") {
                            var child_key = value[l].key;
                            var index = value[l].key.lastIndexOf("_");
                            var month = "";
                            month = child_key.slice(index + 1);
                            if (month != "all" && isNaN(month)) {
                                cloneHtml = wrapHtml.find(".item_list").nextAll("[data-key=" + child_key + "]").clone();
                            } else {
                                child_key = child_key.slice(0, index);
                                cloneHtml = wrapHtml.find(".item_list").nextAll("[data-key=" + child_key + "]").clone();
                                if (month == "all") {
                                    cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html("累计");
                                } else {
                                    cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html("最近" + month + "个月");
                                }
                                cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-type", month);
                            }
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                        } else if (key == "COUPON") {
                            var child_key = value[l].key;
                            var index = value[l].key.lastIndexOf("_");
                            var index_prev = value[l].key.indexOf("_");
                            var li = "";
                            var type_code = "";
                            var type_name = "";
                            type_code = child_key.slice(index_prev + 1, index);
                            child_key = child_key.slice(index + 1);
                            li = cloneHtml.find(".select_coupon_right_label_name+span").find("ul li");
                            li.map(function() {
                                if ($(this).attr("data-type") == type_code) {
                                    type_name = $(this).html();
                                }
                            });
                            if (type_name == "") {
                                //type_name=="请选择";
                                type_code == ""
                            }
                            cloneHtml = wrapHtml.find(".item_list").nextAll("[data-key=" + child_key + "]").clone();
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").val(type_name);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type", type_code);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(1).val(value[l].start);
                            cloneHtml.find("input:text").eq(2).val(value[l].end);
                            cloneHtml.find(".select_coupon_right_line+div").show();
                        } else if (key == "CATA1_PRD" || key == "CATA2_PRD" || key == "CATA3_PRD" || key == "CATA4_PRD" || key == "SEASON_PRD" || key == "T_BL_M" || key == "DAYOFWEEK" || key == "BRAND_ID" || key == "CATA3_PRD" || key == "STORE_AREA" || key == "PRICE_CODE") {
                            var child_key = value[l].key;
                            var type_name = value[l].name;
                            cloneHtml.find(".select_dimension").find(".imitate_select span").html(type_name);
                            cloneHtml.find(".select_dimension").find(".imitate_select span").attr("data-value", child_key);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                            cloneHtml.find(".select_dimension+div").show();
                        } else {
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                            if (cloneHtml.has("input.laydate-icon").length > 0) {
                                var dateInput = cloneHtml.find("input.laydate-icon");
                                dateInput.map(function(num) {
                                    var index = wrapHtml.find(".item_list").children().length;
                                    var id = $(this).attr("id");
                                    $(this).attr("id", id + index);
                                    if ($(this).attr("onclick").indexOf("setDateSelectionDate") != -1) {
                                        if (num == 0) {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',max:'" + value[l].end + "',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                                        } else {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',min:'" + value[l].start + "',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                                        }
                                    } else if ($(this).attr("onclick").indexOf("setDateSelection") != -1) {
                                        if (num == 0) {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',max:'" + value[l].end + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                                        } else {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',min:'" + value[l].start + "',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                                        }
                                    } else if ($(this).attr("onclick").indexOf("Ownformat") != -1) {
                                        if (num == 0) {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',max:'2017-" + value[l].end + "',format: 'YYYY-MM-DD', istoday:false,choose:Ownformat})")
                                        } else {
                                            $(this).attr("onclick", "laydate({elem:'#" + id + index + "',min:'2017-" + value[l].start + "',format: 'YYYY-MM-DD',istoday:false,choose:Ownformat})")
                                        }
                                    }
                                })
                            }
                        }
                        if (l > 0) {
                            opera = opera == "AND" ? "并且" : opera == "OR" ? "或者" : "";
                            cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break
            }
        }
        var type = wrapHtml.attr("class").replace(/center_item_group select_/, "");
        runtimeData(type);
    }
}

$(".select_box").on("click", ".dimension ul li", function() {
    $(this).parent().show();
    $(this).addClass("active");
    $(this).siblings().removeClass("active");
    $(this).find("i").addClass("active");
    $(this).parent().next("ul").show();
    var top = parseInt($(this).parent().css("top")) + $(this).position().top;
    $(this).parent().next("ul").css("top", top);
    $(".select_box_wrap").css("bottom", $(window).height() - $(document).height());
});
$(".select_box").on("mouseout", ".dimension ul li", function() {
    setTimeout(function() {
        $(this).parent().next("ul").hide();
    }, 500)
});
$(".select_box").on("click", ".select_dimension", function(e) {
    var top = $(this).offset();
    var type = $(this).parents(".center_item_group").attr("class").replace(/center_item_group select_/, "");
    var index = $(this).parents(".item_line").index();
    e.stopPropagation();
    top.top = top.top + 30;
    if ($(".dimension").is(":visible")) {
        $(".dimension").hide();
        $(".select_box #select_tyle_wrap").hide();
        return
    }
    $(".dimension").html($("#dimension_none").html()).show();
    $(".dimension").offset(top);
    var year = new Date().getFullYear();
    var html_y = "";
    for (var y = year; y > (year - 5); y--) {
        html_y += '<li>' + y + '<i class="icon-ishop_8-03"></i></li>'
    }
    $(".dimension .year").html(html_y);
    $(".dimension .select_type_list").html("");
    if (select_type_data[type].length == 0) {
        $(".dimension .select_type_tip").html("加载中...");
        getClassSeasonListSelect(type, index)
    } else {
        initSelectType(select_type_data[type], type, index)
    }
    var html = "<div id='select_tyle_wrap' style='position: fixed;left:220px;top:0;right: 0;bottom:0;z-index: 10'></div>";
    $(".select_box_content_center").append(html)
});

function initSelectType(list, type, index) {
    var class_html = '<li>请选择</li>';
    var _this = $(".select_" + type).find(".select_dimension");
    $(".dimension .select_type_tip").hide();
    $(".dimension .tuli .select_type_list").html("");
    for (var cl = 0; cl < list.length; cl++) {
        if (type == "week" || type == "month") {
            $(".dimension .tuli .search_type").hide();
        }
        if (list[cl]["code"] == undefined) {
            class_html += "<li>" + list[cl]["name"] + "</li>"
        } else {
            class_html += "<li data-code='" + list[cl]["code"] + "' >" + list[cl]["name"] + "</li>"
        }
    }
    $(".dimension .tuli .select_type_list").append(class_html);
    $(".dimension .select_type_list li").unbind("click").bind("click", function() {
        if ($(this).html() == "请选择") {
            _this.eq(index).find("span").html($(this).html());
            _this.eq(index).find("span").attr("data-value", "");
            _this.eq(index).next("div").hide();
        } else {
            _this.eq(index).find("span").html($(".dimension .dimension_list li.active").text().trim() + "/" + $(".dimension .year li.active").text().trim() + "/" + $(this).html());
            _this.eq(index).find("span").attr("data-value", $(".dimension .dimension_list li.active").attr("data-type") + "§" + $(".dimension .year li.active").text().trim() + "§" + ($(this).attr("data-code") == undefined ? $(this).html() : $(this).attr("data-code")));
            _this.eq(index).next("div").show();
        }
        $(".dimension").hide();
        runtimeData(type);
        $(".select_box #select_tyle_wrap").hide();
    })
}

function getClassSeasonListSelect(type, index) {
    var type = type;
    $("#mask").css("z-index", "10002");
    var param = {
        corp_code: select_corp_code,
        type: type
    };
    oc.postRequire("post", "/vipGroup/getClassQuarter", "0", param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = "";
            select_type_data["class"] = message.CATA1_PRD;
            select_type_data["typeMid"] = message.CATA2_PRD;
            select_type_data["typeSm"] = message.CATA3_PRD;
            select_type_data["typeSeries"] = message.CATA4_PRD;
            select_type_data["quarter"] = message.SEASON_PRD;
            select_type_data["brand"] = message.BRAND_PRD;
            select_type_data["price"] = message.PRICE_PRD;
            select_type_data["store_area"] = message.AREA_PRD;
            list = select_type_data[type] !== undefined ? select_type_data[type] : [];
            $(".dimension .select_type_tip").html("暂无数据");
            if (list.length > 0) {
                initSelectType(list, type, index)
            }
        } else if (data.code == "-1") {
            $(".dimension .select_type_tip").html("加载失败");
        }
    })
}
jQuery.expr[':'].Contains = function(a, i, m) {
    return jQuery(a).text().toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
};

$(".select_box").on("keyup", ".dimension .search_type input", function() {
    var val = $(this).val().trim();
    if (val == "") {
        $(".dimension .select_type_list li").show();
    } else {
        $(".dimension .select_type_list li").hide();

        $(".dimension .select_type_list li:Contains('" + val + "')").show();
    }
});

//店铺勾选全部是，判断总店铺的数量
$("#checkShopAll").change(function() {
    if ($(this).prop("checked")) {
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked", true);
        if ($(this).attr("data-count") && $(this).attr("data-count") >= 500) {
            $(this).parent().next().css("visibility", "visible");
        }
    } else {
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked", false);
        $("#checkShopAll").parent().next().css("visibility", "hidden");
    }
});
$("#toggleCheckAllShop").click(function() {
    if ($("#checkShopAll").prop("checked")) {
        $("#checkShopAll").prop("checked", false);
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked", false);
        $("#checkShopAll").parent().next().css("visibility", "hidden");
    } else {
        $("#checkShopAll").prop("checked", true);
        $("#screen_shop .screen_content_l ul li").find("input").prop("checked", true);
        if ($("#checkShopAll").attr("data-count") && $("#checkShopAll").attr("data-count") >= 500) {
            $("#checkShopAll").parent().next().css("visibility", "visible");
        }
    }
});