/**
 * Created by Administrator on 2017/7/3.
 */
var oc = new ObjectControl();

function formatCurrency(num) {
    num = String(num);
    var reg = num.indexOf('.') > -1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g; //千分符的正则
    num = num.replace(reg, '$1,'); //千分位格式化
    return num;
}
var vipFilterList = {};
var analysis = {
    corp_code: "C10000",
    page_num: 1,
    page_size: 20,
    has_next_page: false,
    area_num: 1,
    area_has_next_page: false,
    store_num: 1,
    store_has_next_page: false,
    staff_num: 1,
    staff_has_next_page: false,
    app_id: "",
    qrcode_id: "",
    store_code: "",
    user_code: "",
    option: {
        backgroundColor: "#fff",
        color: ['#4176c0', '#41c7db'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: "#97c94b"
                }
            }
        },
        legend: {
            show: true,
            right: '10',
            data: ['关注人数', '新增会员数']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {
                    show: false
                }
            }
        },
        xAxis: {
            axisLine: {
                lineStyle: {
                    color: "#e1e1e1",
                    width: "5"
                }
            },
            nameTextStyle: {
                color: "#2c323e"
            },
            boundaryGap: false,
            type: 'category',
            data: []
                //data: this.chart_item
        },
        yAxis: {
            axisLine: {
                lineStyle: {
                    color: "#e1e1e1",
                    width: "0"
                }
            },
            boundaryGap: false,
            type: 'value'
        },
        textStyle: {
            color: "#2c323e"
        },
        series: [{
                name: '关注人数',
                type: 'line',
                stack: "总量1",
                symbol: "circle",
                data: [],
                lineStyle: {
                    normal: {
                        width: "1"
                    }
                }
            },
            {
                name: '新增会员数',
                type: 'line',
                stack: "总量2",
                symbol: "circle",
                data: [],
                lineStyle: {
                    normal: {
                        width: "1"
                    }
                }
            }
        ]
    },
    getWxList: function getWxList() {
        var param = {};
        var self = this;
        $("#public_signal ul").html("");
        param["corp_code"] = this.corp_code;
        oc.postRequire("post", "/corp/selectWx", "0", param, function(data) {
            if (data.code == "0") {
                var list = JSON.parse(data.message).list;
                var html = "<li data-code=''>请选择</li>";
                for (var i = 0; i < list.length; i++) {
                    html += "<li data-code='" + list[i].app_id + "'>" + list[i].app_name + "</li>"
                }
                $("#public_signal ul").html(html);
                $("#public_signal .default").html("请选择");
                if (list.length > 0) {
                    analysis.corp_code = list[0].corp_code
                }
            } else if (data.code == "-1") {
                $("#public_signal .default").html("请求失败");
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    },
    toggleSelectTabs: function() {
        var self = this;
        $(".nav_down").click(function() {
            $(this).siblings().find("ul").hide();
            $(this).find(".down_ul").toggle();
        });

        $("#analysis li").click(function() { //切换分析类型
            var text = $(this).html();
            $(this).parents("#analysis").find(".default").html(text);
            if (text == "店铺二维码分析") {
                $("#qrCode,#type,#staff_select").hide();
                $("#store_group,#store_select").show();
                self.clearSelect3();
            } else if (text == "导购二维码分析") {
                $("#qrCode,#type").hide();
                $("#store_group,#store_select,#staff_select").show();
                self.clearSelect3();
            } else if (text == "渠道二维码分析") {
                $("#qrCode,#type").show();
                $("#store_group,#store_select,#staff_select").hide();
            }
        });
        $("#public_signal").on("click", "li", function() { //切换公众号
            var text = $(this).html();
            var code = $(this).attr("data-code");
            $(this).parents("#public_signal").find(".default").html(text);
            self.clearQr();
            $(this).parents("#public_signal").find(".default").attr("data-code", code);
        });
        $("#type").click(function() { //选择二维码类型
            $(".pop_select").show();
            $("#select_qr_type").show();
            $("#select_qr_type").siblings().hide();
        });
        $("#qrCode").click(function() { //选择二维码
            var public_signal_code = $("#public_signal .default").attr("data-code").trim();
            var type_id = $("#type .default").attr("data-id").trim();
            if (public_signal_code == "" || type_id == "") {
                art.dialog({
                    zIndex: 10003,
                    time: 2,
                    lock: true,
                    cancel: false,
                    content: "请选择公众号和二维码类型"
                });
                return;
            }
            $(".pop_select").show();
            $("#select_qr_code").show();
            $("#select_qr_code").siblings().hide();
            self.getPublic();
        });
        $("#store_group").click(function() {
            self.area_num = 1;
            $("#select_area .screen_content_l ul").empty();
            $(".pop_select").show();
            $("#select_area").show();
            $("#select_area").siblings().hide();
            self.getArea();
        });
        $("#store_select").click(function() {
            self.store_num = 1;
            $("#select_store .screen_content_l ul").empty();
            $(".pop_select").show();
            $("#select_store").show();
            $("#select_store").siblings().hide();
            self.getStore();
        });
        $("#staff_select").click(function() {
            self.staff_num = 1;
            $("#select_staff .screen_content_l ul").empty();
            $(".pop_select").show();
            $("#select_staff").show();
            $("#select_staff").siblings().hide();
            self.getStaff();
        })
    },
    changeTabsTable: function() {
        var self = this;
        $("#table .chart_title span").bind("click", function() {
            var text = $(this).html();
            var analysisText = $("#analysis .default").html();
            self.page_num = 1;
            $(".table_below_shop #tb_body").html("");
            $(this).addClass("active").siblings("span").removeClass("active");
            switch (analysisText) {
                case "渠道二维码分析":
                    if (self.app_id == "" || self.qrcode_id == "") {
                        art.dialog({
                            zIndex: 10003,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请先选择公众号和二维码"
                        });
                        return;
                    }
                    $("#tb_channel").show().siblings().hide();
                    self.getChannelList();
                    break;
                case "店铺二维码分析":
                    if (self.app_id == "" || self.store_code == "") {
                        art.dialog({
                            zIndex: 10003,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请先选择公众号和店铺"
                        });
                        return;
                    }
                    $("#tb_store").show().siblings().hide();
                    self.getStoreList();
                    break;
                case "导购二维码分析":
                    if (self.app_id == "" || self.user_code == "") {
                        art.dialog({
                            zIndex: 10003,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "请先选择公众号和导购"
                        });
                        return;
                    }
                    $("#tb_staff").show().siblings().hide();
                    self.getStaffList();
                    break;
            }
            if (text == "明细列表") {
                $("#tb_detail").show().siblings().hide();
                if (!$("#show_filter").is(":visible")) { $("#filter").show(); }
            } else {
                $("#filter").hide();
                $("#show_filter").hide();
                $("#sxk").hide();
            }
        })
    },
    clearQr: function() { //重置二维码的内容
        $("#select_qr_code .screen_content_l ul,#select_qr_code .screen_content_r ul").empty();
        $("#qr_code_search").val("");
        $("#select_qr_code .s_pitch span").html("0");
        $("#qrCode .default").html("请选择");
        $("#qrCode .default").attr("data-code", "")
    },
    clearSelect3: function() {
        $("#select_area .screen_content_l ul,#select_area .screen_content_r ul").empty();
        $("#select_store .screen_content_l ul,#select_store .screen_content_r ul").empty();
        $("#select_staff .screen_content_l ul,#select_staff .screen_content_r ul").empty();

        $("#select_area .s_pitch span").html("0");
        $("#select_store .s_pitch span").html("0");
        $("#select_staff .s_pitch span").html("0");

        $("#area_search").val("");
        $("#store_search").val("");
        $("#staff_search").val("");

        $("#store_group .default").attr("data-code", "").html("请选择");
        $("#store_select .default").attr("data-code", "").html("请选择");
        $("#staff_select .default").attr("data-code", "").html("请选择");
    },
    select_que: function() {
        var self = this;
        $("#screen_que_qr_type").click(function() { //选择二维码类型确定
            var li = $("#select_qr_type .screen_content_r input[type='checkbox']").parents("li");
            if (li.length > 0) {
                var qr_id = li.map(function() {
                    return $(this).attr("id");
                }).get().reverse().join(",");
                $("#type .default").attr("data-id", qr_id);
                $("#type .default").html("已选择" + li.length + "个");
                $("#select_qr_type").hide();
                $(".pop_select").hide();
                self.clearQr();
            } else {
                $("#type .default").attr("data-id", "");
                $("#type .default").html("请选择");
                $("#select_qr_type").hide();
                $(".pop_select").hide();
            }
        });
        $("#screen_que_qr_code").click(function() { //选择二维码确定
            var li = $("#select_qr_code .screen_content_r input[type='checkbox']").parents("li");
            if (li.length > 0) {
                var qr_id = li.map(function() {
                    return $(this).attr("id");
                }).get().reverse().join(",");
                $("#qrCode .default").attr("data-code", qr_id);
                $("#qrCode .default").html("已选择" + li.length + "个");
                $("#select_qr_type").hide();
                $(".pop_select").hide();
            } else {
                $("#qrCode .default").attr("data-code", "");
                $("#qrCode .default").html("请选择");
                $("#select_qr_code").hide();
                $(".pop_select").hide();
            }
        });
        $("#screen_que_area").click(function() { //选择店铺群组确定
            var li = $("#select_area .screen_content_r input[type='checkbox']").parents("li");
            if (li.length > 0) {
                var area_code = li.map(function() {
                    return $(this).attr("id");
                }).get().reverse().join(",");
                $("#store_group .default").attr("data-code", area_code);
                $("#store_group .default").html("已选择" + li.length + "个");
                $("#select_area").hide();
                $(".pop_select").hide();
            } else {
                $("#store_group .default").attr("data-code", "");
                $("#store_group .default").html("请选择");
                $("#select_area").hide();
                $(".pop_select").hide();
            }
        });
        $("#screen_que_store").click(function() { //选择店铺确定
            var li = $("#select_store .screen_content_r input[type='checkbox']").parents("li");
            if (li.length > 0) {
                var store_code = li.map(function() {
                    return $(this).attr("id");
                }).get().reverse().join(",");
                $("#store_select .default").attr("data-code", store_code);
                $("#store_select .default").html("已选择" + li.length + "个");
                $("#select_store").hide();
                $(".pop_select").hide();
            } else {
                $("#store_select .default").attr("data-code", "");
                $("#store_select .default").html("请选择");
                $("#select_store").hide();
                $(".pop_select").hide();
            }
        });
        $("#screen_que_staff").click(function() { //选择店铺确定
            var li = $("#select_staff .screen_content_r input[type='checkbox']").parents("li");
            if (li.length > 0) {
                var store_code = li.map(function() {
                    return $(this).attr("id");
                }).get().reverse().join(",");
                $("#staff_select .default").attr("data-code", store_code);
                $("#staff_select .default").html("已选择" + li.length + "个");
                $("#select_staff").hide();
                $(".pop_select").hide();
            } else {
                $("#staff_select .default").attr("data-code", "");
                $("#staff_select .default").html("请选择");
                $("#select_staff").hide();
                $(".pop_select").hide();
            }
        })
    },
    getPublic: function() {
        $("#qrCode ul").html("");
        var param = {
            app_id: $("#public_signal .default").attr("data-code"),
            type: $("#type .default").attr("data-id")
        };
        whir.loading.add("", "0.5");
        oc.postRequire("post", "/qrCode/qrcodeList", "0", param, function(data) {
            whir.loading.remove();
            $("#loading").hide();
            if (data.code == "0") {
                var list = JSON.parse(JSON.parse(data.message).list);
                var html = "";
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html += '<li>' +
                            '<div class="checkbox1">' +
                            '<input type="checkbox" value="' + list[i].id + '" data-name="" name="test" class="check" id="qr_type_' + i + '">' +
                            '<label for="qr_type_' + i + '"></label>' +
                            '</div>' +
                            '<span class="p16">' + list[i].qrcode_name + '</span>' +
                            '</li>'
                    }
                    $("#select_qr_code .screen_content_l ul").html(html);
                } else {

                }
            }
        })
    },
    getArea: function() { //店铺群组
        var self = this;
        var param = {
            "corp_code": "C10000",
            "searchValue": $("#area_search").val().trim(),
            "pageSize": 1000,
            "pageNumber": self.area_num
        };
        whir.loading.add("", "0.5");
        oc.postRequire("post", "/area/selAreaByCorpCode", "0", param, function(data) {
            whir.loading.remove();
            $("#loading").hide();
            if (data.code == "0") {
                var msg = JSON.parse(JSON.parse(data.message).list);
                var list = msg.list;
                var html = "";
                self.area_has_next_page = msg.hasNextPage;
                if (msg.hasNextPage) {
                    self.area_num++
                }
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html += '<li>' +
                            '<div class="checkbox1">' +
                            '<input type="checkbox" value="' + list[i].area_code + '" data-name="" name="test" class="check" id="area_type_' + i + '">' +
                            '<label for="area_type_' + i + '"></label>' +
                            '</div>' +
                            '<span class="p16">' + list[i].area_name + '</span>' +
                            '</li>'
                    }
                    $("#select_area .screen_content_l ul").append(html);
                }
                $("#select_area .screen_content_l").unbind("scroll").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                        if (!self.area_has_next_page) {
                            return;
                        }
                        self.getArea();
                    }
                })
            }
        })
    },
    getStore: function() { //店铺
        var self = this;
        var param = {
            "corp_code": "C10000",
            "area_code": $("#store_group .default").attr("data-code"),
            "brand_code": "",
            "searchValue": $("#store_search").val().trim(),
            "pageSize": 1000,
            "pageNumber": self.store_num
        };
        whir.loading.add("", "0.5");
        oc.postRequire("post", "/shop/selectByAreaCode", "0", param, function(data) {
            whir.loading.remove();
            $("#loading").hide();
            if (data.code == "0") {
                var msg = JSON.parse(JSON.parse(data.message).list);
                var list = msg.list;
                var html = "";
                self.store_has_next_page = msg.hasNextPage;
                if (msg.hasNextPage) {
                    self.store_num++
                }
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html += '<li>' +
                            '<div class="checkbox1">' +
                            '<input type="checkbox" value="' + list[i].store_code + '" data-name="" name="test" class="check" id="area_type_' + i + '">' +
                            '<label for="area_type_' + i + '"></label>' +
                            '</div>' +
                            '<span class="p16">' + list[i].store_name + '</span>' +
                            '</li>'
                    }
                    $("#select_store .screen_content_l ul").append(html);
                }
                $("#select_store .screen_content_l").unbind("scroll").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                        if (!self.store_has_next_page) {
                            return;
                        }
                        self.getStore();
                    }
                })
            }
        })
    },
    getStaff: function() { //导购
        var self = this;
        var param = {
            "corp_code": "C10000",
            "area_code": $("#store_group .default").attr("data-code"),
            "brand_code": "",
            "store_code": $("#store_select .default").attr("data-code"),
            "searchValue": $("#staff_search").val().trim(),
            "pageSize": 1000,
            "pageNumber": self.staff_num
        };
        whir.loading.add("", "0.5");
        oc.postRequire("post", "/user/selectUsersByRole", "0", param, function(data) {
            whir.loading.remove();
            $("#loading").hide();
            if (data.code == "0") {
                var msg = JSON.parse(JSON.parse(data.message).list);
                var list = msg.list;
                var html = "";
                self.staff_has_next_page = msg.hasNextPage;
                if (msg.hasNextPage) {
                    self.staff_num++
                }
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html += '<li>' +
                            '<div class="checkbox1">' +
                            '<input type="checkbox" value="' + list[i].user_code + '" data-name="" name="test" class="check" id="area_type_' + i + '">' +
                            '<label for="area_type_' + i + '"></label>' +
                            '</div>' +
                            '<span class="p16">' + list[i].user_name + '</span>' +
                            '</li>'
                    }
                    $("#select_staff .screen_content_l ul").append(html);
                }
                $("#select_staff .screen_content_l").unbind("scroll").bind("scroll", function() {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight = $(this).height();
                    if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                        if (!self.staff_has_next_page) {
                            return;
                        }
                        self.getStaff();
                    }
                })
            }
        })
    },
    removeLeft: function(a, b) {
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
    removeRight: function(a, b) {
        var li = "";
        if (a == "only") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
        }
        if (a == "all") {
            li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox'][data-type!='define']").parents("li:visible");
        }
        if (li.length > 1000) {
            art.dialog({
                zIndex: 10003,
                time: 1,
                lock: true,
                cancel: false,
                content: "请选择少于1000个"
            });
            return;
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
    },
    Dclose: function() { //点击文档隐藏需要隐藏的东西
        $(document).click(function(e) {
            if ($(".nav_down").has(e.target).length === 0 && !$(".nav_down").is(e.target)) {
                $(".nav_down ul").hide();
            }
        })
    },
    getChannelChart: function() { //获取渠道二维码图表
        var self = this;
        var url = "/qrCode/analy/view";
        var param = {
            app_id: $("#public_signal .default").attr("data-code"),
            qrcode_id: $("#qrCode .default").attr("data-code"),
            date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() }
        };
        $("#chart_loading").show();
        self.getChartData(url, param)
    },
    getStoreChart: function() { //获取店铺二维码图表
        var self = this;
        var url = "/qrCode/analyStore/view";
        var param = {
            app_id: $("#public_signal .default").attr("data-code"),
            store_code: $("#store_select .default").attr("data-code"),
            date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() }
        };
        $("#chart_loading").show();
        self.getChartData(url, param)
    },
    getStaffChart: function() { //获取店铺二维码图表
        var self = this;
        var url = "/qrCode/analyEmp/view";
        var param = {
            app_id: $("#public_signal .default").attr("data-code"),
            user_code: $("#staff_select .default").attr("data-code"),
            date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() }
        };
        $("#chart_loading").show();
        self.getChartData(url, param)
    },
    getChartData: function(url, param) {
        var self = this;
        oc.postRequire("post", url, "0", param, function(data) {
            $("#chart_loading").hide();
            $("#chart_temp").hide();
            if (data.code == 0) {
                var all_vip = [];
                var new_vip = [];
                var date = [];
                var analyView = JSON.parse(data.message).analyView;
                for (var i = 0; i < analyView.length; i++) {
                    all_vip.push(analyView[i].all);
                    new_vip.push(analyView[i].newVip);
                    date.push(analyView[i].date);
                }
                self.option["xAxis"]["data"] = date;
                self.option["series"][0]["data"] = all_vip;
                self.option["series"][1]["data"] = new_vip;
                var myChart = echarts.init(document.getElementById('chart'));
                myChart.setOption(analysis.option);
                window.addEventListener("resize", function() {
                    myChart.resize();
                });

            } else {

            }
        })
    },
    getChannelList: function() {
        var self = this;
        var isDegree = $("#table .chart_title span.active").html() == "关注度分析表";
        var url = isDegree ? "/qrCode/analy/list" : "/qrCode/analy/vipList";
        var type = isDegree ? "degree" : "detail";
        if (url == "/qrCode/analy/list") {
            var param = {
                app_id: self.app_id,
                qrcode_id: self.qrcode_id,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size
            };
        } else {
            var param = {
                app_id: self.app_id,
                qrcode_id: self.qrcode_id,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size,
                screen: self.getInputValue()
            };
        }
        $("#list_loading").show();
        self.getListData(url, param, type);
    },
    getStoreList: function() {
        var self = this;
        var isDegree = $("#table .chart_title span.active").html() == "关注度分析表";
        var url = isDegree ? "/qrCode/analyStore/list" : "/qrCode/analyStore/VipList";
        var type = isDegree ? "degree" : "detail";
        if (url == "/qrCode/analyStore/list") {
            var param = {
                app_id: self.app_id,
                store_code: self.store_code,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size
            };
        } else {
            var param = {
                app_id: self.app_id,
                store_code: self.store_code,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size,
                screen: self.getInputValue()
            };
        }

        $("#list_loading").show();
        self.getListData(url, param, type);
    },
    getStaffList: function() {
        var self = this;
        var isDegree = $("#table .chart_title span.active").html() == "关注度分析表";
        var url = isDegree ? "/qrCode/analyEmp/list" : "/qrCode/analyEmp/VipList";
        var type = isDegree ? "degree" : "detail";
        if (url == "/qrCode/analyEmp/list") {
            var param = {
                app_id: self.app_id,
                user_code: self.user_code,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size
            };
        } else {
            var param = {
                app_id: self.app_id,
                user_code: self.user_code,
                date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                page_num: self.page_num,
                page_size: self.page_size,
                screen: self.getInputValue()
            };
        }
        $("#list_loading").show();
        self.getListData(url, param, type);
    },
    getListData: function(url, param, type) {
        var self = this;
        oc.postRequire("post", url, "0", param, function(data) {
            $("#list_loading").hide();
            $(".data_no").hide();
            if (data.code == 0) {
                var msg = JSON.parse(data.message);
                var list = msg.list;
                var pages = msg.pages;
                $("#allAnaly").html(formatCurrency(msg.allAnaly));
                $("#allVip").html(formatCurrency(msg.allVip));
                if (self.page_num >= pages || pages == undefined) {
                    self.has_next_page = false
                } else {
                    self.has_next_page = true;
                }
                if (list.length == 0) {
                    $(".data_no").show();
                    return
                }

                if (type == "detail") {
                    self.TableBodyDetailHtml(list)
                } else if (type == "degree") {
                    //$("#allAnaly").html(formatCurrency(msg.allAnaly));
                    //$("#allVip").html(formatCurrency(msg.allVip));
                    self.TableBodyDegreeHtml(list)
                }
            } else {

            }
        })
    },
    TableBodyDegreeHtml: function(list) {
        var self = this;
        var html = "";
        var dataName = ""; //名称
        var dataCode = ""; //编号，当时二位时，是类型
        if ($("#tb_channel").is(":visible")) {
            dataName = "qrcode_name";
            dataCode = "qrcode_type";
        }
        if ($("#tb_store").is(":visible")) {
            dataName = "store_code";
            dataCode = "store_name";
        }
        if ($("#tb_staff").is(":visible")) {
            dataName = "user_code";
            dataCode = "user_name";
        }
        list.forEach(function(data, i) {
            var a = (self.page_num - 1) * self.page_size + i + 1,
                rate = data.allCount == 0 ? 0 : (Number(data.newVipCount) / Number(data.allCount) * 100).toFixed(2);
            html += "<tr><td><span>" + a + "</span></td>" +
                "<td><span title='" + data[dataName] + "'>" + data[dataName] + "</span></td>" +
                "<td><span title='" + data[dataCode] + "'>" + data[dataCode] + "</span></td>" +
                "<td><span title='" + formatCurrency(data.allCount) + "'>" + formatCurrency(data.allCount) + "</span></td>" +
                "<td><span title='" + formatCurrency(data.newVipCount) + "'>" + formatCurrency(data.newVipCount) + "</span></td>" +
                "<td><span title='" + rate + "'>" + rate + "%</span></td>" +
                "<td><span title='" + data.create_date + "'>" + data.create_date + "</span></td>" +
                "</tr>"
        });
        $(".table_below_shop #tb_body").append(html)
    },
    TableBodyDetailHtml: function(list) {
        var html = "";
        var self = this;
        list.forEach(function(data, i) {
            var a = (self.page_num - 1) * self.page_size + i + 1;
            html += "<tr><td><span>" + a + "</span></td>" +
                "<td><span title='" + data.nick_name + "'>" + data.nick_name + "</span></td>" +
                "<td><span title='" + data.vip['vip_name'] + "'>" + data.vip['vip_name'] + "</span></td>" +
                "<td><span title='" + data.vip['cardno'] + "'>" + data.vip['cardno'] + "</span></td>" +
                "<td><span title='" + data.vip['vip_phone'] + "'>" + data.vip['vip_phone'] + "</span></td>" +
                "<td><span title='" + data.vip['user_name'] + "'>" + data.vip['user_name'] + "</span></td>" +
                "<td><span title='" + data.vip['store_name'] + "'>" + data.vip['store_name'] + "</span></td>" +
                "<td><span title='" + data.attention_time + "'>" + data.attention_time + "</span></td>" +
                "<td><span title='" + data.vip['join_date'] + "'>" + data.vip['join_date'] + "</span></td>" +
                "<td><span title='" + (data.is_new == "Y" ? "是" : "否") + "'>" + (data.is_new == "Y" ? "是" : "否") + "</span></td>" +
                "</tr>"
        });
        $(".table_below_shop #tb_body").append(html)
    },
    searchSelect: function() {
        var self = this;
        $("#staff_search").keydown(function() {
            var event = window.event || arguments[0];
            if (event.keyCode == 13) {
                self.staff_num = 1;
                $("#select_staff .screen_content_l ul").empty();
                self.getStaff();
            }
        });
        $("#staff_search_f").click(function() {
            self.staff_num = 1;
            $("#select_staff .screen_content_l ul").empty();
            self.getStaff();
        });

        $("#store_search").keydown(function() {
            var event = window.event || arguments[0];
            if (event.keyCode == 13) {
                self.store_num = 1;
                $("#select_store .screen_content_l ul").empty();
                self.getStore();
            }
        });
        $("#store_search_f").click(function() {
            self.store_num = 1;
            $("#select_store .screen_content_l ul").empty();
            self.getStore();
        });

        $("#area_search").keydown(function() {
            var event = window.event || arguments[0];
            if (event.keyCode == 13) {
                self.area_num = 1;
                $("#select_area .screen_content_l ul").empty();
                self.getArea();
            }
        });
        $("#area_search_f").click(function() {
            self.area_num = 1;
            $("#select_area .screen_content_l ul").empty();
            self.getArea();
        });

        $("#qr_code_search").keydown(function() {
            var event = window.event || arguments[0];
            var val = $("#qr_code_search").val().trim();
            if (event.keyCode == 13) {
                if (val == "") {
                    $("#select_qr_code .screen_content_l ul li").show();
                } else {
                    $("#select_qr_code .screen_content_l ul li").hide();
                    $("#select_qr_code .screen_content_l ul li:contains(" + val + ")").show();
                }
                $("#select_qr_code .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
                $("#select_qr_code .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
            }
        });
        $("#qr_code_search_f").click(function() {
            var val = $("#qr_code_search").val().trim();
            if (val == "") {
                $("#select_qr_code .screen_content_l ul li").show();
                return
            }
            $("#select_qr_code .screen_content_l ul li").hide();
            $("#select_qr_code .screen_content_l ul li:contains(" + val + ")").show();
            $("#select_qr_code .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
            $("#select_qr_code .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
        });
        $("#qr_type_search").keydown(function() {
            var event = window.event || arguments[0];
            var val = $("#qr_type_search").val().trim();
            if (event.keyCode == 13) {
                if (val == "") {
                    $("#select_qr_type .screen_content_l ul li").show();
                    return
                }
                $("#select_qr_type .screen_content_l ul li").hide();
                $("#select_qr_type .screen_content_l ul li:contains(" + val + ")").show();
                $("#select_qr_type .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
                $("#select_qr_type .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
            }
        });
        $("#qr_type_search_f").click(function() {
            var val = $("#qr_type_search").val().trim();
            if (val == "") {
                $("#select_qr_type .screen_content_l ul li").show();
                return
            }
            $("#select_qr_type .screen_content_l ul li").hide();
            $("#select_qr_type .screen_content_l ul li:contains(" + val + ")").show();
            $("#select_qr_type .screen_content_l ul li:visible:odd").css("backgroundColor", "#fff");
            $("#select_qr_type .screen_content_l ul li:visible:even").css("backgroundColor", "#ededed");
        })
    },
    select: function() { //点击确定按钮 调取图表和列表数据
        var self = this;
        $("#select").click(function() {
            var analysisText = $("#analysis .default").html();
            var public_signalCode = $("#public_signal .default").attr("data-code");
            if (public_signalCode == "") {
                art.dialog({
                    zIndex: 10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先选择公众号"
                });
                return
            }
            if (analysisText == "渠道二维码分析") {
                var qrCode = $("#qrCode .default").attr("data-code");
                if (qrCode == "") {
                    art.dialog({
                        zIndex: 10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请先选择二维码"
                    });
                    return
                }
                self.qrcode_id = $("#qrCode .default").attr("data-code");
                self.getChannelChart();
            } else if (analysisText == "店铺二维码分析") {
                if ($("#store_select .default").attr("data-code") == "") {
                    art.dialog({
                        zIndex: 10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请先选择店铺"
                    });
                    return
                }
                self.store_code = $("#store_select .default").attr("data-code");
                self.getStoreChart();
            } else if (analysisText == "导购二维码分析") {
                if ($("#staff_select .default").attr("data-code") == "") {
                    art.dialog({
                        zIndex: 10003,
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请先选择导购"
                    });
                    return
                }
                self.user_code = $("#staff_select .default").attr("data-code");
                self.getStaffChart();
            }
            self.app_id = $("#public_signal .default").attr("data-code");
            $("#table .chart_title span.active").trigger("click");
            //$("#clear_filter").trigger("click");
        })
    },
    scrollList: function() {
        var self = this;
        $(".table_below_shop").scroll(function() {
            var text = $("#analysis .default").html();
            var nScrollHight = $(this)[0].scrollHeight;
            var nScrollTop = $(this)[0].scrollTop;
            var nDivHight = $(this).height();
            if (nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0) {
                if (!self.has_next_page) {
                    return;
                }
                self.page_num++;
                if (text == "渠道二维码分析") {
                    self.getChannelList();
                }
                if (text == "店铺二维码分析") {
                    self.getStoreList()
                }
                if (text == "导购二维码分析") {
                    self.getStaffList();
                }
            }
        })
    },
    exportList: function() {
        var self = this;
        $("#export_table").click(function() {
            var text = $("#table .chart_title span.active").html();
            var len = $(".table_below_shop #tb_body").children().length;
            if (len == 0) {
                art.dialog({
                    zIndex: 10003,
                    time: 2,
                    lock: true,
                    cancel: false,
                    content: "暂无要导出的数据"
                });
                return
            }
            $("#export_consumeExport p").html("是否导出" + text);
            $("#export_consumeExport .album_enter").html("确认");
            $(".export_mask").show();
        });
        $("#export_consumeExport .cancel,#export_consumeExport .icon-ishop_6-12").click(function() {
            $(".export_mask").hide();
        });
        $("#export_consumeExport .album_enter").click(function() {
            var text = $("#table .chart_title span.active").html();
            var url = text == "关注度分析表" ? "/qrCode/exportExeclAnaly" : "/qrCode/exportExeclNewVip";
            var typeText = $("#analysis .default").html();
            var tablemanager = [];
            var thead = $(".table_top_shop thead:visible th");
            if ($(this).html() != "确认") {
                $(".export_mask").hide();
            } else {
                thead.map(function() {
                    if ($(this).attr("data-id") !== undefined) {
                        tablemanager.push({
                            column_name: $(this).attr("data-id"),
                            show_name: $(this).text()
                        })
                    }
                });
                if (url == "/qrCode/exportExeclNewVip") {
                    var param = {
                        app_id: self.app_id,
                        corp_code: analysis.corp_code,
                        date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                        tablemanager: tablemanager,
                        screen: vipFilterList
                    };
                } else {
                    var param = {
                        app_id: self.app_id,
                        corp_code: analysis.corp_code,
                        date: { start: $("#select_date_start").val(), end: $("#select_date_end").val() },
                        tablemanager: tablemanager
                    };
                }
                if (typeText == "渠道二维码分析") {
                    param.type = "qrcode";
                    param.qrcode_id = self.qrcode_id;

                } else if (typeText == "店铺二维码分析") {
                    param.type = "store";
                    param.store_code = self.store_code;
                } else if (typeText == "导购二维码分析") {
                    param.type = "user";
                    param.user_code = self.user_code;
                }
                whir.loading.add("", 0.5);
                oc.postRequire("post", url, "0", param, function(data) {
                    whir.loading.remove();
                    if (data.code == 0) {
                        var url = JSON.parse(data.message).path;
                        $("#export_consumeExport .album_enter").html('<a href="/' + JSON.parse(url) + '">下载文件</a>')
                    } else {
                        art.dialog({
                            zIndex: 10003,
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "导出失败"
                        });
                    }
                })
            }
        });
    },
    getInputValue: function() {
        var input = $('#sxk .inputs>ul>li');
        vipFilterList = {}; //定义一个list
        for (var i = 0; i < input.length; i++) {
            var screen_key = $(input[i]).find("input").attr("id");
            var screen_value;
            if ($(input[i]).attr("class") == "isActive_select") {
                screen_value = $(input[i]).find("input").attr("data-code");
            } else if ($(input[i]).attr("class") == "created_date") {
                var start = $('#join_date_screen_start').val();
                var end = $('#join_date_screen_end').val();
                screen_key = $(input[i]).attr("id");
                screen_value = { "start": start, "end": end };
            } else {
                screen_key = $(input[i]).find("input").attr("id");
                screen_value = $(input[i]).find('input').val().trim();
            }
            vipFilterList[screen_key] = screen_value
        }
        return vipFilterList;
    },
    filtrateDown: function() {
        //筛选select框
        $("#sxk .isActive_select input").click(function() {
            var ul = $(this).next(".isActive_select_down");
            if (ul.css("display") == "none") {
                ul.show();
            } else {
                ul.hide();
            }
        })
        $("#sxk .isActive_select input").blur(function() {
            var ul = $(this).next(".isActive_select_down");
            setTimeout(function() {
                ul.hide();
            }, 200);
        })
        $("#sxk .isActive_select_down li").click(function() {
            var html = $(this).text();
            var code = $(this).attr("data-code");
            $(this).parents("li").find("input").val(html);
            $(this).parents("li").find("input").attr("data-code", code);
            $(".isActive_select_down").hide();
        })
    },
    bind: function() {
        this.Dclose();
        this.select_que();
        this.toggleSelectTabs();
        this.select();
        this.changeTabsTable();
        this.searchSelect();
        this.scrollList();
        this.exportList();
        this.filtrateDown();
        $("#filter").click(function() {
            $("#sxk").slideDown(function() {
                $("#show_filter").show();
                $("#filter").hide();
            });
        });
        $("#up_filter").click(function() {
            $("#sxk").slideUp(function() {
                $("#show_filter").hide();
                $("#filter").show();
            });
        });
        $("#filter_select").click(function() {
            $("#table .chart_title span.active").trigger("click");
        });
        $("#clear_filter").click(function() {
            var input = $("#sxk .inputs input");
            for (var i = 0; i < input.length; i++) {
                if ($(input[i]).parent().hasClass("isActive_select")) {
                    input[i].value = "全部";
                } else {
                    input[i].value = "";
                }
                $(input[i]).attr("data-code", "");
            }
            $("#join_date_screen_start").attr("onclick", "laydate({elem:'#join_date_screen_start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:join_date_screen_start})");
            $("#join_date_screen_end").attr("onclick", "laydate({elem:'#join_date_screen_end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:join_date_screen_end})");

            $("#table .chart_title span.active").trigger("click");
        })
    },
    init: function() {
        this.bind();
        this.getWxList();
    }
};
var start = {
    elem: '#select_date_start',
    format: 'YYYY-MM-DD',
    min: '1900-01-01', //设定最小日期为当前日期
    max: '2099-12-31', //最大日期
    //istime: true,
    istoday: false,
    choose: function(datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas; //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#select_date_end',
    format: 'YYYY-MM-DD',
    min: '1900-01-01',
    max: '2099-12-31',
    //istime: true,
    istoday: false,
    choose: function(datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var join_date_screen_start = {
    elem: '#join_date_screen_start',
    format: 'YYYY-MM-DD',
    min: '1900-01-01',
    max: '2099-12-31',
    //istime: true,
    istoday: false,
    choose: function(datas) {
        join_date_screen_end.min = datas; //开始日选好后，重置结束日的最小日期
        join_date_screen_end.start = datas; //将结束日的初始值设定为开始日
    }
};
var join_date_screen_end = {
    elem: '#join_date_screen_end',
    format: 'YYYY-MM-DD',
    min: '1900-01-01',
    max: '2099-12-31',
    //istime: true,
    istoday: false,
    choose: function(datas) {
        join_date_screen_start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);
laydate(join_date_screen_start);
laydate(join_date_screen_end);

$(function() {
    analysis.init();
});
//点击右移
$(".shift_right").click(function() {
    var right = "only";
    var div = $(this);
    analysis.removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function() {
    var right = "all";
    var div = $(this);
    analysis.removeRight(right, div);
});
//点击左移
$(".shift_left").click(function() {
    var left = "only";
    var div = $(this);
    analysis.removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function() {
    var left = "all";
    var div = $(this);
    analysis.removeLeft(left, div);
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

$(".screen_close").click(function() {
    $(".pop_select").hide();
    $(".screen_area").hide();
});