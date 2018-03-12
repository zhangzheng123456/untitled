var oc = new ObjectControl();

function formatCurrency(num) {
    num = String(num);
    var reg = num.indexOf('.') > -1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g; //千分符的正则
    num = num.replace(reg, '$1,'); //千分位格式化
    return num;
}

function selectCardType(ele) {
    if (window.vipCardType) {
        return
    }
    var _self = ele;
    var corp_code = task_track.corp_code;
    var param = { "corp_code": corp_code };
    oc.postRequire("post", "/vipCardType/getVipCardTypes", "0", param, function(data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var vip_type_html = '<li data-code="">全部</li>';
            window.vipCardType = list;
            if (list.length > 0) {
                for (var t = 0; t < list.length; t++) {
                    vip_type_html += "<li data-code='" + list[t].vip_card_type_name + "'>" + list[t].vip_card_type_name + "</li>"
                }
                $(_self).next("ul").html(vip_type_html)
            } else {
                $(_self).next("ul").find("div").html("暂无会员类型")
            }
        } else if (data.code == "-1") {
            $(_self).next("ul").find("div").html("获取会员类型失败")
        }
    });
}
var info = {
    pageNum: 1,
    pageSize: 20,
    has_next_page: false,
    open_id: "",
    bind: function() {
        $("#inviteRegistrationInfoDetails_back").click(function() {
            $("#inviteRegistrationInfoDetailBox").hide();
            info.pageNum = 1;
            $(".inviteRegistrationInfo table tbody").html("");
        })
    },
    inviteRegistrationShow: function(dom) {
        var e = event || window.event;
        e.stopPropagation();
        info.open_id = $(dom).parents("tr").attr("data-open_id");
        $("#inviteRegistrationInfoDetailBox .action_detail_box .s1").html("邀请人：" + $(dom).parents("tr").attr("data-name"));
        $("#inviteRegistrationInfoDetailBox .action_detail_box .s2").html($(dom).parents("tr").attr("data-card"));
        var w = $("#inviteRegistrationInfoDetailBox").outerWidth();
        var h = $("#inviteRegistrationInfoDetailBox").outerHeight();
        $("#inviteRegistrationInfoDetailBox").css("margin-left", -w / 2).css("margin-top", -h / 2);
        $("#inviteRegistrationInfoDetailBox").show();
        info.pageNum = 1;
        info.inviteRegistrationInfo();
    },
    inviteRegistrationInfo: function() {
        var param = {
            task_code: task_track.task_code,
            page_num: info.pageNum,
            page_size: info.pageSize,
            open_id: info.open_id
        };
        oc.postRequire("post", "/vipTaskAnaly/registerVipInfo", "", param, function(data) {
            if (data.code == 0) {
                var msg = JSON.parse(data.message);
                var list = msg.list;
                info.has_next_page = msg.is_next;
                if (list.length > 0) {
                    var tr = "";
                    for (var i = 0; i < list.length; i++) {
                        tr += "<tr >" +
                            "<td>" + ((info.pageNum - 1) * 20 + i + 1) + "</td>" +
                            "<td>" + list[i].vip_name + "</td>" +
                            "<td>" + list[i].cardno + "</td>" +
                            "<td>" + list[i].vip_card_type + "</td>" +
                            "<td>" + list[i].amt_trade + "</td>" +
                            "</tr>"
                    }
                    $(".inviteRegistrationInfo table tbody").append(tr)
                } else {
                    var tr = "";
                    for (var i = 0; i < 9; i++) {
                        if (i == 0) {
                            tr += "<tr'>" +
                                "<td colspan='5' rowspan='10'>暂无数据</td>" +
                                "</tr>"
                        } else {
                            tr += "<tr></tr>"
                        }
                    }
                    $(".inviteRegistrationInfo table tbody").html(tr)
                }
            } else {
                var tr = "";
                for (var i = 0; i < 9; i++) {
                    if (i == 0) {
                        tr += "<tr'>" +
                            "<td colspan='5' rowspan='10'>获取数据失败</td>" +
                            "</tr>"
                    } else {
                        tr += "<tr></tr>"
                    }
                }
                $(".inviteRegistrationInfo table tbody").html(tr)
            }
        });
    },
    tableScroll: function() {
        var _self = this;
        //滚动分页
        $("#inviteRegistrationInfoDetailBox .inviteRegistrationInfo").unbind("scroll").bind("scroll", function() {
            var offsetHeight = $(this).height(),
                scrollHeight = $(this)[0].scrollHeight,
                scrollTop = $(this)[0].scrollTop;
            if (scrollTop + offsetHeight >= (scrollHeight - 20) && scrollTop != 0) {
                if (_self.has_next_page) {
                    _self.pageNum++;
                } else {
                    return
                }
                _self.inviteRegistrationInfo()
            }
        });
    },
    init: function() {
        this.bind();
        this.tableScroll();
    }
};
var task_track = {
    task_code: "",
    pieUrl: "",
    chartUrl: "",
    listUrl: "",
    task_start_time: "",
    task_end_time: "",
    task_type: "",
    corp_code: "",
    pageIndex: 1,
    has_next_page: false,
    getServerTime: function() { //发送一个ajax请求用于获取服务器时间去做任务倒计时
        var xhr = null,
            time = null,
            curDate = null,
            def = $.Deferred();
        if (window.XMLHttpRequest) {
            xhr = new window.XMLHttpRequest();
        } else { // ie
            xhr = new ActiveObject("Microsoft")
        }
        // 通过get的方式请求当前文件
        xhr.open("get", "/");
        xhr.send(null);
        // 监听请求状态变化
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 2) {
                // 获取响应头里的时间戳
                time = xhr.getResponseHeader("Date");
                curDate = new Date(time).getTime(); //服务器时间
            }
            def.resolve(curDate);
        };
        return def
    },
    bind: function() {
        var _self = this;
        _self.operateSelectPub();
        _self.filtrateDown();
        _self.selectCondition();
        _self.canMove("#task_setting", ".task_setting_head");
        _self.canMove("#improve_data", ".improve_data_head");
        _self.canMove("#questionnaire_detail", ".questionnaire_detail_head");
        _self.tableScroll();
        $("#hide_questionnaire_detail").click(function() {
            $("body").find(".mask").remove();
            $("#questionnaire_detail").attr("style", "").hide();

        });
        $("#return_back").click(function() {
            $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_task.html?t="+ $.now());
        });
        //筛选按钮
        $('.choose').click(function() {
            $(this).parent().next('.screening').slideToggle(600, function() {
                $(".people").getNiceScroll().resize();
            });
            if ($(this).html() == "收起筛选") {
                //收起
                $(this).html('筛选');
                $(this).nextAll().hide();
            } else {
                //展开
                $(this).html('收起筛选');
                $(this).nextAll().show();
            }
        });
        $("#empty_select").click(function() { //清楚筛选
            var li = $(".inputs li");
            li.map(function() {
                if ($(this).hasClass("isActive_select")) {
                    $(this).find("input").val("全部").attr("data-code", "");
                } else if ($(this).hasClass("selectDate")) {
                    $(this).find("input").val("").attr("data-start", "").attr("data-end", "");
                } else {
                    $(this).find("input").val("")
                }
            });
            _self.pageIndex = 1;
            $(".list_box .peopleContent table tbody").html("");
            _self.getListData();
        });
        $("#select_list").click(function() { //筛选
            _self.pageIndex = 1;
            $(".list_box .peopleContent table tbody").html("");
            _self.getListData();
        });
        $("#hide_improve_data").click(function() {
            $("body").find(".mask").remove();
            $("#improve_data").attr("style", "").hide();
            $("#improve_data .improve_data_content").html("")
        })
    },
    closeTask: function() {
        //取消下拉框
        $(document).on('click', function(e) {
            if (e.target.id == 'cancel_box') { return }
            if ($(e.target).parents('#cancel_box').length == 0) $('.action_tip').hide();
        });
        $('.title_action').click(function() { //活动左上角设置
            $('.action_tip').toggle();
        });
        $(".action_btn").click(function() {
            $("#action_over_box").show();
        });
        $(".action_footer_l").click(function() {
            $("#action_over_box").hide();
        });
        $(".action_footer_r").click(function() {
            var params = {
                id: task_track.id
            };
            whir.loading.add("", 0.5); //加载等待框
            oc.postRequire("post", "/vipTask/terminateTask", "0", params, function(data) {
                whir.loading.remove(); //加载等待框
                if (data.code == 0) {
                    window.location.reload();
                } else {
                    art.dialog({
                        time: 3,
                        lock: true,
                        cancel: false,
                        content: "操作失败"
                    });
                }
            })
        })
    },
    selectCondition: function() {
        var _self = this;
        $("#select_condition").click(function() {
            var task_condition = JSON.parse(_self.task_condition);
            if (task_condition && task_condition.length > 0) {
                var li = "";
                for (var c = 0; c < task_condition.length; c++) {
                    li += "<li>" +
                        "<div class='checkbox1'>" +
                        "<input  type='checkbox' value='" + task_condition[c].param_name + "'  name='test'  class='check'/>" +
                        "<label>" +
                        "</label>" +
                        "</div>" +
                        "<span class='p16'>" + task_condition[c].param_desc + "</span>" +
                        "</li>"
                }
                $("#screen_task_condition .screen_content_l ul").html(li);
            }
            $("#screen_task_condition").show();
            $("body").append("<div class='mask'></div>")
        });
        $("#screen_que_task_condition").click(function() {
            var li = $("#screen_task_condition .screen_content_r ul li");
            if (li.length > 0) {
                var condition_key = li.map(function() {
                    return $(this).find("input").val();
                }).get().join(",");
                var condition_value = li.map(function() {
                    return $(this).children("span").html();
                }).get().join(",");
                $("#select_condition").val(condition_value).attr("data-code", condition_key);
            } else {
                $("#select_condition").val("全部").attr("data-code", "");
            }
            $("#screen_task_condition").hide();
            $("body").find(".mask").remove();
        });
        $("#screen_close_task_condition").click(function() {
            $("#screen_task_condition").hide();
            $("body").find(".mask").remove();
        })
    },
    operateSelectPub: function() {
        //移到右边
        function removeRight(a, b) {
            var li = "";
            if (a == "only") {
                li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
            }
            if (a == "all") {
                li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li:visible");
            }
            //                if(li.length>100){
            //                    art.dialog({
            //                        zIndex: 10003,
            //                        time: 1,
            //                        lock: true,
            //                        cancel: false,
            //                        content: "请选择少于100个"
            //                    });
            //                    return;
            //                }
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
            removeRight(right, div);
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
        //点击列表显示选中状态
        $(".screen_content").on("click", "li", function() {
            var input = $(this).find("input")[0];
            if (input.type == "checkbox" && input.checked == false) {
                input.checked = true;
            } else if (input.type == "checkbox" && input.checked == true) {
                input.checked = false;
            }
        });
    },
    initTaskSettings: function(data) {
        $("#app_name").html(data.app_name);
        $(".present_loading").hide();
        $("#task_setting_desc").html(data.task_description);
        $(".task_setting_title").html(data.task_title);
        $("#task_time").html(data.start_time + " - " + data.end_time);
        var target_vip = JSON.parse(data.target_vips);
        if (target_vip.length == 0) {
            $("#target_key").html("全部会员");
        } else {
            var vips = '';
            for (var i = 0; i < target_vip.length; i++) {
                if (target_vip[i].date !== undefined) {
                    vips += '<div title="'+target_vip[i].names + ':最近' + target_vip[i].date + '个月' + target_vip[i].value.start + '到' + target_vip[i].value.end+'">' + target_vip[i].names + ':最近' + target_vip[i].date + '个月' + target_vip[i].value.start + '到' + target_vip[i].value.end + '</div>'
                } else {
                    if (target_vip[i].type == "text") {
                        if (target_vip[i].name != undefined) {
                            vips += '<div title="'+target_vip[i].names + ':' + target_vip[i].name + '">' + target_vip[i].names + ':' + target_vip[i].name + '</div>'
                        } else {
                            var value = target_vip[i].value == null ? '空' : target_vip[i].value;
                            vips += '<div title="'+target_vip[i].names + ':' + value +'">' + target_vip[i].names + ':' + value + '</div>'
                        }
                    }
                    if (target_vip[i].type == "json") {
                        vips += '<div title="'+target_vip[i].names + ':' + target_vip[i].value.start + '到' + target_vip[i].value.end +'">' + target_vip[i].names + ':' + target_vip[i].value.start + '到' + target_vip[i].value.end + '</div>'
                    }
                    if (target_vip[i].type == "array") {
                        if (target_vip[i].key == "23") {
                            var value = target_vip[i].value;
                            var values = "";
                            value.map(function(arr) {
                                var join = arr.is_join == "ALL" ? "全部" : arr.is_join == "Y" ? "已参与" : "未参与";
                                values += "活动名称:" + arr.activity_name + ",是否参与:" + join + ";";
                            });
                            vips += '<div title="'+target_vip[i].name + ':' + values + '">' + target_vip[i].name + ':' + values + '</div>'
                        }
                        if (target_vip[i].key == "22") {
                            var value = target_vip[i].value;
                            var values = "";
                            value.map(function(arr) {
                                var join = arr.is_join == "ALL" ? "全部" : arr.is_join == "Y" ? "已参与" : "未参与";
                                values += "任务名称:" + arr.task_name + ",是否参与:" + join + ";";
                            });
                            vips += '<div title="'+target_vip[i].name + ':' + values+'">' + target_vip[i].name + ':' + values + '</div>'
                        }
                        if (target_vip[i].key == "21") {
                            var value = target_vip[i].value;
                            var values = "";
                            value.map(function(arr) {
                                var text = "";
                                if (arr.can_use.start != "" || arr.can_use.end != "") {
                                    text += "可用数量:" + arr.can_use.start + "~" + arr.can_use.end + ",";
                                }
                                if (arr.expired_num.start != "" || arr.expired_num.end != "") {
                                    text += "过期数量:" + arr.expired_num.start + "~" + arr.expired_num.end + ",";
                                }
                                if (arr.num.start != "" || arr.num.end != "") {
                                    text += "使用数量:" + arr.num.start + "~" + arr.num.end + ",";
                                }
                                if (arr.use.start != "" || arr.use.end != "") {
                                    text += "获得数量:" + arr.use.start + "~" + arr.use.end + ",";
                                }
                                text = text.slice(0, -1);
                                values += "券类型:" + arr.coupon_name + "," + text + ";";
                            });
                            vips += '<div title="'+target_vip[i].name + ':' + values +'">' + target_vip[i].name + ':' + values + '</div>'
                        }
                    }
                }
            }
        }
        // for(var v=0;v<target_vip.length;v++){
        //     if(target_vip[v].type=="json"){
        //         vips+="<div title='"+target_vip[v].names+":"+target_vip[v].value+"'>"+target_vip[v].names+":"+target_vip[v].value.start+"至"+target_vip[v].value.end+"</div>"
        //     }else{
        //         vips+="<div title='"+target_vip[v].names+":"+target_vip[v].value+"'>"+target_vip[v].names+":"+target_vip[v].value+"</div>"
        //     }
        // }
        $("#target_key").html(vips)
        if (data.present_coupon != "") {
            var present_coupon = JSON.parse(data.present_coupon);
            var coupon = "";
            for (var p = 0; p < present_coupon.length; p++) {
                coupon += "<div title='" + present_coupon[p].coupon_name + "'>" + present_coupon[p].coupon_name + "</div>"
            }
            $("#task_setting_present_coupon").html(coupon);
            $("#task_reward .task_setting_part_line:last-child").show()
        }
        if (data.present_point != "") {
            $("#present_point").html(data.present_point + "积分");
            $("#task_reward .task_setting_part_line:nth-child(2)").show();
        }
        switch (task_track.task_type) {
            case "improve_data": //完善资料
                $("#task_condition").append("<div>完善资料</div>");
                break;
            case "share_goods": //分享商品
                $("#task_condition").append("<div>分享商品<a style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </a>次</div>");
                break;
            case "share_counts": //分享次数
                $("#task_condition").append("<div>分享次数<a style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </a>次</div>");
                break;
            case "integral_accumulate": //积分积累
                $("#task_condition").append("<div>积分积累<a style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </a>分</div>");
                break;
            case "consume_count": //累计消费次数
                if (data.task_condition.indexOf("{") != "-1") {
                    var task_condition = JSON.parse(data.task_condition);
                    if (task_condition.start_time != "" && task_condition.end_time != "") {
                        $("#task_condition").append("<span>" + task_condition.start_time + "~" + task_condition.end_time + "内</span>");
                    }
                }
                $("#task_condition").append("<span>累计消费<a style='color: #1b1b1f'>" + (data.target_count == '' ? 0 : data.target_count) + " </a>次</span>");
                break;
            case "consume_money": //累计消费金额
                if (data.task_condition.indexOf("{") != "-1") {
                    var task_condition = JSON.parse(data.task_condition);
                    if (task_condition.start_time != "" && task_condition.end_time != "") {
                        $("#task_condition").append("<span>" + task_condition.start_time + "~" + task_condition.end_time + "内</span>")
                    }
                }
                $("#task_condition").append("<span>累计消费<span style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </span>元</span>");
                break;
            case "ticket_sales": //最高客单价
                if (data.task_condition.indexOf("{") != "-1") {
                    var task_condition = JSON.parse(data.task_condition);
                    if (task_condition.start_time != "" && task_condition.end_time != "") {
                        $("#task_condition").append("<span>" + task_condition.start_time + "~" + task_condition.end_time + "内</span>");
                    }
                }
                $("#task_condition").append("<span>最高客单价<span style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </>元</span>");
                break;
            case "invite_registration": //邀请注册、
                $("#task_condition").append("<div>成功邀请<a style='color: #1b1b1f'> " + (data.target_count == '' ? 0 : data.target_count) + " </a>人</div>");
                break;
            case "activity_count": //参与活动次数
                var task_condition = JSON.parse(data.task_condition);
                $("#task_condition").append("<div>活动类型<a style='color: #1b1b1f'> " + task_condition.activity_type + " </a></div>");
                $("#task_condition").append("<div>参与活动<a style='color: #1b1b1f'> " + task_condition.count + " </a>次</div>");
                break;
            case "questionnaire": //问卷调查
                var task_condition = JSON.parse(data.task_condition);
                $("#task_condition").append("<div>完成问卷调查</div>");
        }
    },
    initHead: function(data) {
        var _self = this;
        $(".time .time_l").html(data.start_time);
        $(".time .time_r").html(data.end_time);
        $(".active_name .name_l").html(data.task_type_name);
        $(".active_name .name_r").html(data.corp_name);
        $(".name_title .title_l").html(data.task_title).attr("title", data.task_title);
        $("#vip_count .p_2").html(formatCurrency(data.target_vip_count));
        _self.initTaskSettings(data);
        if (data.task_status == 2) {
            $('#task_time_status').html("<p style='color:#dee2e8;'>任务已结束</p>");
            $("#cancel_box").hide();
        } else if (data.task_status == 1) {
            $("#cancel_box").show();
            _self.getServerTime().then(function(time) {
                var start_time = new Date(data.start_time).getTime();
                var end_time = new Date(data.end_time).getTime();
                _self.countDownAssign(start_time, end_time, time);
            });
        }
    },
    countDownAssign: function(start_time, end_time, curTime) { //倒计时赋值
        var now_time = "";
        now_time = curTime;
        if (end_time <= now_time) {
            $('#task_time_status').html("<p style='color:#dee2e8;'>任务已结束</p>");
        } else {
            if (start_time < now_time && end_time > now_time) {
                var time = (end_time - now_time) / 1000;
                var d = parseInt(time / (24 * 60 * 60));
                var h = parseInt((time - d * (24 * 60 * 60)) / (60 * 60));
                var m = parseInt((time - d * (24 * 60 * 60) - h * (60 * 60)) / 60);
                var s = parseInt(time - d * (24 * 60 * 60) - h * (60 * 60) - m * 60);
                d <= 0 && (d = 0);
                h <= 0 && (h = 0);
                m <= 0 && (m = 0);
                s <= 0 && (s = 0);
                $('#task_time_status .p_2').html(d + '天' + h + '小时' + m + '分钟' + s + '秒');
                var timerStart = setInterval(function() {
                    now_time = now_time + 1000;
                    var time = (end_time - now_time) / 1000;
                    var d = parseInt(time / (24 * 60 * 60));
                    var h = parseInt((time - d * (24 * 60 * 60)) / (60 * 60));
                    var m = parseInt((time - d * (24 * 60 * 60) - h * (60 * 60)) / 60);
                    var s = parseInt(time - d * (24 * 60 * 60) - h * (60 * 60) - m * 60);
                    d <= 0 && (d = 0);
                    h <= 0 && (h = 0);
                    m <= 0 && (m = 0);
                    s <= 0 && (s = 0);
                    $('#task_time_status .p_2').html(d + '天' + h + '小时' + m + '分钟' + s + '秒');
                    $('#task_time_status .p_3').html('距离任务结束还剩');
                    if (d == 0 && h == 0 && m == 0 && (s == 0)) {
                        $('#task_time_status').html("<p style='color:#dee2e8;'>任务已结束</p>");
                        clearInterval(timerStart);
                    }
                }, 1000);
            }
        }
    },
    getTaskData: function() { //获取任务名称等信息
        var param = {
            id: sessionStorage.getItem("task_vip_id")
        };
        var _self = this;
        oc.postRequire("post", "/vipTask/select", "", param, function(data) {
            var task = JSON.parse(data.message).task;
            var task_type = task.task_type;
            _self.task_code = task.task_code;
            _self.task_start_time = task.start_time;
            _self.task_edn_time = task.end_time;
            _self.task_type = task.task_type;
            _self.corp_code = task.corp_code;
            _self.id = task.id;
            _self.initHead(task);
            _self.closeTask();
            $("#show_task_settings").unbind("click").bind("click", function() {
                $("body").append("<div class='mask'></div>");
                var w = $("#task_setting").outerWidth();
                var h = $("#task_setting").outerHeight();
                $("#task_setting").css("margin-left", -w / 2).css("margin-top", -h / 2);
                $("#task_setting").show();
            });
            $("#hide_task_settings").click(function() {
                $("body").find(".mask").remove();
                $("#task_setting").attr("style", "").hide();

            });
            var ins = laydate.render({
                elem: '#search_date',
                range: true,
                min: task.start_time,
                max: task.end_time,
                //btns: ["confirm"],
                ready: function() {
                    if (window.chart_time_tip) {
                        return
                    }
                    window.chart_time_tip = true;
                    ins.hint("日期选择范围<br>" + task.start_time.slice(0, 11) + "到 " + task.end_time.slice(0, 11))
                },
                done: function(value) {
                    if (value == "") {
                        $("#search_date").attr("data-start", "");
                        $("#search_date").attr("data-end", "");
                    } else {
                        value = value.split(" - ");
                        $("#search_date").attr("data-start", value[0]);
                        $("#search_date").attr("data-end", value[1]);
                    }
                    $("#chartLine").next(".listLoading_box").show();
                    task_track.getChartData();
                }
            });
            var tr = "";
            if (task_type == "invite_registration") {
                //tr='<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>分享次数</td><td>链接打开次数</td><td>注册人数</td><td>邀请顾客消费金额</td><td>完成时间</td><td>注册详情</td></tr>';
                tr = '<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>分享次数</td><td>注册人数</td><td>完成时间</td><td>注册详情</td></tr>';
                _self.pieUrl = "/vipTaskAnaly/registerRate";
                _self.chartUrl = "/vipTaskAnaly/registerChart";
                _self.listUrl = "/vipTaskAnaly/registerList";
                _self.getRateData();
                _self.getChartData();
                _self.getListData();
            } else if (task_type == "improve_data") {
                tr = '<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>是否达成</td><td>任务完成进度</td><td>完成时间</td><td>完善详情</td></tr>';
                _self.pieUrl = "/vipTaskAnaly/completeRate";
                _self.chartUrl = "/vipTaskAnaly/completeChart";
                _self.listUrl = "/vipTaskAnaly/completeList";
                _self.task_condition = task.task_condition;
                _self.getRateData();
                _self.getChartData();
                _self.getListData();
            } else if (task_type == "consume_count" || task_type == "consume_money" || task_type == "ticket_sales") {
                tr = '<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>消费次数</td><td>消费金额</td><td>最高客单价</td><td style="width: auto;">完成时间</td></tr>';
                _self.pieUrl = "/vipTaskAnaly/saleInfoRate";
                _self.chartUrl = "/vipTaskAnaly/saleInfoChart";
                _self.listUrl = "/vipTaskAnaly/saleInfoList";
                _self.getRateData();
                _self.getChartData();
                _self.getListData();
            } else if (task_type == "share_counts") {
                //tr='<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>分享次数</td><td>点击次数</td><td>点击人数</td><td>是否达成</td><td>完成时间</td></tr>';
                tr = '<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>分享次数</td><td>是否达成</td><td style="width: auto">完成时间</td></tr>';
                _self.pieUrl = "/vipTaskAnaly/shareUrlRate";
                _self.chartUrl = "/vipTaskAnaly/shareUrlChart";
                _self.listUrl = "/vipTaskAnaly/shareUrlList";
                _self.getRateData();
                _self.getChartData();
                _self.getListData();
            } else if (task_type == "questionnaire") {
                tr = '<tr><td>序号</td><td>会员名称</td><td>会员卡号</td> <td>手机号</td> <td>会员类型</td> <td>答题数</td><td>完成时间</td><td>问卷详情</td></tr>';
                _self.pieUrl = "/vipTaskAnaly/questionNaireoRate";
                _self.chartUrl = "/vipTaskAnaly/questionNaireoChart";
                _self.listUrl = "/vipTaskAnaly/questionNaireoList";
                _self.getRateData();
                _self.getChartData();
                _self.getListData();
            }
            $(".people_head thead").html(tr);
            var ins_select = laydate.render({
                elem: '.selectDate input',
                range: true,
                type: 'datetime',
                //                    min:task.start_time,
                //                    max:task.end_time,
                //                    ready:function(){
                //                        if(window.select_time_tip){
                //                            return
                //                        }
                //                        window.select_time_tip=true;
                //                        ins_select.hint("日期选择范围<br>"+task.start_time+"到 "+task.end_time)
                //                    },
                done: function(value) {
                    if (value == "") {
                        $(".selectDate input").attr("data-start", "");
                        $(".selectDate input").attr("data-end", "");
                    } else {
                        value = value.split(" - ");
                        $(".selectDate input").attr("data-start", value[0]);
                        $(".selectDate input").attr("data-end", value[1]);
                    }
                }
            });
        })
    },
    canMove: function(dom, handle) {
        $(dom).mousedown(function(e) {
            //设置移动后的默认位置
            var endx = 0;
            var endy = 0;
            //获取div的初始位置，要注意的是需要转整型，因为获取到值带px
            var left = parseInt($(dom).css("left"));
            var top = parseInt($(dom).css("top"));
            //获取鼠标按下时的坐标，区别于下面的es.pageX,es.pageY
            var downx = e.pageX;
            var downy = e.pageY; //pageY的y要大写，必须大写！！
            var minendx = $(this).innerWidth() / 2 + 200;
            var minendy = $(this).innerHeight() / 2 + 40;
            //    鼠标按下时给div挂事件
            $(document).bind("mousemove", function(es) {
                //es.pageX,es.pageY:获取鼠标移动后的坐标
                endx = es.pageX - downx + left; //计算div的最终位置
                endy = es.pageY - downy + top;
                endx = endx < minendx ? minendx : endx;
                endy = endy < minendy ? minendy : endy;
                //带上单位
                $(dom).css("left", endx + "px").css("top", endy + "px")
            });
            if (!$(e.target).is($(handle))) {
                $(document).unbind("mousemove");
            }
        }); //移动筛选框
        $(dom).mouseup(function() {
            //鼠标弹起时给div取消事件
            $(document).unbind("mousemove")
        });
    },
    filtrateDown: function() {
        //筛选select框
        $(".isActive_select input").click(function() {
            var ul = $(this).next(".isActive_select_down");
            if (ul.css("display") == "none") {
                ul.show();
            } else {
                ul.hide();
            }
        });
        $(".isActive_select input").blur(function() {
            var ul = $(this).next(".isActive_select_down");
            setTimeout(function() {
                ul.hide();
            }, 200);
        });

        $(".isActive_select_down").on("click", "li", function() {
            var html = $(this).text();
            var code = $(this).attr("data-code");
            $(this).parents("li").find("input").val(html);
            $(this).parents("li").find("input").attr("data-code", code);
            $(".isActive_select_down").hide();
        })
    },
    getRateData: function() { //邀请注册 占比图
        var _self = this;
        var param = {
            task_code: _self.task_code
        };
        oc.postRequire("post", _self.pieUrl, "", param, function(data) {
            var info = JSON.parse(data.message).info;
            if (_self.task_type == "invite_registration") {
                $(".chartOneClass .chart_count .chart_count_l").eq(0).find(".shoppers").html(info.share_vip_num);
                $(".chartOneClass .chart_count .chart_count_l").eq(1).find(".shoppers").html(info.share_url_num);
                $(".chartOneClass .chart_count .chart_count_r").find(".target").html(info.register_vip_num);
                $(".chartOneClass").children().css("visibility", "visible");
                _self.initChartClassOne(info.register_vip_num, info.share_url_num)
            } else if (_self.task_type == 'improve_data') {
                $(".chartPie .chart_title").html("任务完成率");
                $(".chartOneClass .chart_count .chart_count_l").eq(0).find(".shoppers").html(info.vip_target_count).prev().html("目标会员数");
                $(".chartOneClass .chart_count .chart_count_l").eq(1).css("visibility", "hidden");
                $(".chartOneClass .chart_count .chart_count_r").find(".target").html(info.complete_num).prev().html("完成会员数");
                $(".chartOneClass").children().css("visibility", "visible");
                _self.initChartClassOne(info.complete_num, info.vip_target_count)
            } else if (_self.task_type == 'consume_count' || _self.task_type == 'consume_money' || _self.task_type == 'ticket_sales') {
                $(".chartPie .chart_title").html("任务完成率");
                $(".chartOneClass .chart_count .chart_count_l").eq(0).find(".shoppers").html(info.vip_target_count).prev().html("目标会员数");
                $(".chartOneClass .chart_count .chart_count_l").eq(1).css("visibility", "hidden");
                $(".chartOneClass .chart_count .chart_count_r").find(".target").html(info.complete_vip_num).prev().html("完成会员数");
                $(".chartOneClass").children().css("visibility", "visible");
                _self.initChartClassOne(info.complete_vip_num, info.vip_target_count)
            } else if (_self.task_type == 'share_counts') {
                $(".chartPie .chart_title").html("会员参与率");
                $(".chartOneClass .chart_count .chart_count_l").eq(0).find(".shoppers").html(info.vip_target_count).prev().html("目标会员数");
                $(".chartOneClass .chart_count .chart_count_l").eq(1).find(".shoppers").html(info.share_vip_num).prev().html("分享人数");
                $(".chartOneClass .chart_count .chart_count_r").find(".target").html(info.complete_vip_num).prev().html("完成会员数");
                $(".chartOneClass").children().css("visibility", "visible");
                _self.initChartClassOne(info.share_vip_num, info.vip_target_count)
            } else if (_self.task_type == "questionnaire") {
                $(".chartPie .chart_title").html("任务完成率");
                $(".chartOneClass .chart_count .chart_count_l").eq(0).find(".shoppers").html(info.vip_target_count).prev().html("目标会员数");
                $(".chartOneClass .chart_count .chart_count_l").eq(1).css("visibility", "hidden");
                $(".chartOneClass .chart_count .chart_count_r").find(".target").html(info.complete_vip_num).prev().html("完成会员数");
                $(".chartOneClass").children().css("visibility", "visible");
                _self.initChartClassOne(info.complete_vip_num, info.vip_target_count)
            }
        })
    },
    getChartData: function() { //邀请注册折线图
        var _self = this;
        var param = {
            task_code: _self.task_code,
            start_time: $("#search_date").attr("data-start"),
            end_time: $("#search_date").attr("data-end")
        };
        oc.postRequire("post", _self.chartUrl, "", param, function(data) {
            var list = JSON.parse(data.message).list;
            _self.initChartLine(list);
        })
    },
    improveDateShow: function(dom) {
        var e = event || window.event;
        e.stopPropagation();
        var key = JSON.parse($(dom).parents("tr").attr("data-key"));
        var data = JSON.parse($(dom).parents("tr").attr("data-value"));
        for (var k = 0; k < key.length; k++) {
            var name_key = key[k].param_desc;
            var name_value = key[k].param_name;
            var hasValue = false;
            for (var d = 0; d < data.length; d++) {
                if (data[d].column == name_value) {
                    hasValue = true;
                    if (data[d].column == "province") {
                        var province_data;
                        if (data[d].value == "") {
                            province_data = "";
                        } else {
                            var V = JSON.parse(data[d].value);
                            province_data =V.province + V.city+ V.area;
                        }
                        $("#improve_data .improve_data_content").append('<div class="improve_data_part_line" title="' + name_key + ':' + province_data + '">' + name_key + ':' + province_data + '</div>')
                    } else {
                        $("#improve_data .improve_data_content").append('<div class="improve_data_part_line" title="' + name_key + ':' + data[d].value + '">' + name_key + ':' + data[d].value + '</div>')
                    }
                }
            }
            if (!hasValue) {
                $("#improve_data .improve_data_content").append('<div class="improve_data_part_line">' + name_key + ':未完善</div>')
            }
        }
        $("body").append("<div class='mask'></div>");
        var w = $("#improve_data").outerWidth();
        var h = $("#improve_data").outerHeight();
        $("#improve_data").css("margin-left", -w / 2).css("margin-top", -h / 2);
        $("#improve_data").show();
    },
    questionnaireShow: function(dom) {
        var answer = JSON.parse($(dom).parents("tr").attr("data-answer"));
        var qtnaire = JSON.parse($(dom).parents("tr").attr("data-qtnaire"));
        var arr = JSON.parse(qtnaire.template);
        var templte = '';
        var info = answer;
        var index = 1;
        $("#preview_name").html(qtnaire.title);
        $("#preview_desc").html(qtnaire.illustrate);
        arr.map(function(item, i) {
            var type = item.type;
            var is_fill = item.check == 'true' ? "*必填" : '';
            var name = item.name;
            if (type == "radio" || type == "checkbox") {
                var li = '';
                var type_name = type == "radio" ? "【单选题】" : "【多选题】";
                if (type == 'radio') {
                    item.options.map(function(items) {
                        var check = info[i].value == items ? "checked='true'" : '';
                        li += '<li><input ' + check + ' type="radio"><label></label><span><span>' + items + '</span></span></li>'
                    });
                } else {
                    item.options.map(function(items) {
                        var check = '';
                        var arr = info[i].value.split(",");
                        for (var n = 0; n < arr.length; n++) {
                            arr[n] == items ? check = "checked='true'" : '';
                        }
                        li += '<li><input ' + check + ' type="checkbox"><label></label><span><span>' + items + '</span></span></li>'
                    });
                }
                templte += '<div class="exercise_box">' +
                    '<span class="exercise_index">' + index + '</span><span class="must_fill">' + is_fill + '</span><span>' + name + '</span><span>' + type_name + '</span><ul>' +
                    li +
                    '</ul></div>';
            } else if (type == "input") {
                templte += ' <div class="exercise_box">' +
                    '<span class="exercise_index">' + index + '</span><span class="must_fill">' + is_fill + '</span><span>' + name + '</span><span>【填空题】</span><ul>' +
                    '<li><input type="text" class="exercise_input" value="' + info[i].value + '" readonly></li></ul></div>';
            } else if (type == "grade") {
                var li = '';
                for (var n = 0; n < 5; n++) {
                    if (info[i].value > n) {
                        li += '<li class="grade_star choose_star"></li>'
                    } else {
                        li += '<li class="grade_star"></li>'
                    }
                }
                templte += '<div class="exercise_box">' +
                    '<span class="exercise_index">' + index + '</span><span class="must_fill">' + is_fill + '</span><span>' + name + '</span><span>【评分题】</span><ul>' +
                    '<ul style="padding-left: 30px">' + li + '</ul></div>';
            } else if (type == "title") {
                templte += '<div class="exercise_box"><span>' + name + '</span></div>';
            }
            type == "title" ? index = 1 : index++;
        });
        $(".questionnaire_detail_content .survey_content").html(templte);
        $("body").append("<div class='mask'></div>");
        var w = $("#questionnaire_detail").outerWidth();
        var h = $("#questionnaire_detail").outerHeight();
        $("#questionnaire_detail").css("margin-left", -w / 2).css("margin-top", -h / 2);
        $("#questionnaire_detail").show();
    },
    getListData: function() {
        var _self = this;
        var param = {
            task_code: _self.task_code,
            page_num: _self.pageIndex,
            page_size: 20,
            screen: {}
        };
        $(".inputs.select_" + _self.task_type).show().siblings().remove();
        var li = $(".inputs li");
        li.map(function() {
            var key = $(this).attr("data-key");
            var value = "";
            if ($(this).hasClass("isActive_select")) {
                value = $(this).find("input").attr("data-code");
                param.screen[key] = value;
            } else if ($(this).find("input").length > 1) {
                param.screen[key] = {
                    start: $(this).find("input").eq(0).val().trim(),
                    end: $(this).find("input").eq(1).val().trim()
                }
            } else if ($(this).hasClass("selectDate")) {
                param.screen[key] = {
                    start: $(this).find("input").attr("data-start"),
                    end: $(this).find("input").attr("data-end")
                }
            } else if ($(this).find("input").length == 1) {
                value = $(this).find("input").val().trim();
                param.screen[key] = value;
            }
        });
        $(".list_box .listLoading_box").show();
        oc.postRequire("post", _self.listUrl, "", param, function(data) {
            $(".list_box .listLoading_box").hide();
            $(".people_head").show();
            if (data.code == 0) {
                var list = JSON.parse(data.message).list;
                _self.has_next_page = JSON.parse(data.message).is_next;
                if (list.length > 0) {
                    $(".list_box .peopleError").hide();
                    _self.listShow(list);
                } else {
                    $(".list_box .peopleError").show();
                }
            } else {
                $(".list_box .peopleError").show();
            }
        })
    },
    linkToVipInfo: function() {
        $(".list_box .peopleContent tbody tr td:not(:first-child)").dblclick(function() {
            var ID = $(this).parent().attr("data-id");
            var corpCode = $(this).parent().attr("data-code");
            sessionStorage.setItem("id", ID);
            sessionStorage.setItem("corp_code", corpCode);
            window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
        })
    },
    listShow: function(list) {
        var tr = "";
        var _self = this;
        if (_self.task_type == "invite_registration") {
            for (var i = 0; i < list.length; i++) {
                var vip = list[i].vip_info;
                tr += "<tr data-open_id='" + vip.open_id + "' data-code='" + vip.corp_code + "' data-id='" + vip.vip_id + "' data-name='" + vip.vip_name + "' data-card='" + vip.cardno + "'>" +
                    "<td>" + ((_self.pageIndex - 1) * 20 + i + 1) + "</td>" +
                    "<td title='" + vip.vip_name + "'>" + vip.vip_name + "</td>" +
                    "<td title='" + vip.cardno + "'>" + vip.cardno + "</td>" +
                    "<td title='" + vip.vip_phone + "'>" + vip.vip_phone + "</td>" +
                    "<td title='" + vip.vip_card_type + "'>" + vip.vip_card_type + "</td>" +
                    "<td title='" + list[i].share_url_num + "'>" + list[i].share_url_num + "</td>" +
                    //"<td title='"+list[i].click_url_num+"'>"+list[i].click_url_num+"</td>" +
                    "<td title='" + list[i].register_vip_num + "'>" + list[i].register_vip_num + "</td>" +
                    //"<td title='"+list[i].register_sale_amt+"'>"+list[i].register_sale_amt+"</td>" +
                    "<td title='" + list[i].complete_date + "'>" + list[i].complete_date + "</td>" +
                    "<td class='detail'><span onclick='info.inviteRegistrationShow(this)'>详情<span class='icon-ishop_8-03'></span></span></td>" +
                    "</tr>"
            }
        } else if (_self.task_type == "improve_data") {
            for (var i = 0; i < list.length; i++) {
                var vip = list[i].vip;
                tr += "<tr data-id='" + vip.vip_id + "' data-code='" + vip.corp_code + "' data-key='" + JSON.stringify(list[i].total_array) + "' data-value='" + JSON.stringify(list[i].complete_array) + "'>" +
                    "<td>" + ((_self.pageIndex - 1) * 20 + i + 1) + "</td>" +
                    "<td title='" + vip.vip_name + "'>" + vip.vip_name + "</td>" +
                    "<td title='" + vip.cardno + "'>" + vip.cardno + "</td>" +
                    "<td title='" + vip.vip_phone + "'>" + vip.vip_phone + "</td>" +
                    "<td title='" + vip.vip_card_type + "'>" + vip.vip_card_type + "</td>" +
                    "<td >" + list[i].status + "</td>" +
                    "<td> " +
                    //                            "<span class='progress_bar'>" +
                    //                            "   <span class='progress_bi' style='width: "+(list[i].complete_num/list[i].total_num)*100+"%'></span>" +
                    //                            "</span>" +
                    "<span style='vertical-align: middle'>" + list[i].complete_num + "/" + list[i].total_num + "</span>" +
                    "</td>" +
                    "<td title='" + list[i].complete_date + "'>" + list[i].complete_date + "</td>" +
                    "<td class='detail'><span onclick='task_track.improveDateShow(this)'>详情<span class='icon-ishop_8-03'></span></span></td>" +
                    "</tr>"
            }
        } else if (_self.task_type == "consume_count" || _self.task_type == "consume_money" || _self.task_type == "ticket_sales") {
            for (var i = 0; i < list.length; i++) {
                var vip = list[i].vip;
                tr += "<tr data-id='" + vip.vip_id + "' data-code='" + vip.corp_code + "'>" +
                    "<td>" + ((_self.pageIndex - 1) * 20 + i + 1) + "</td>" +
                    "<td title='" + vip.vip_name + "'>" + vip.vip_name + "</td>" +
                    "<td title='" + vip.cardno + "'>" + vip.cardno + "</td>" +
                    "<td title='" + vip.vip_phone + "'>" + vip.vip_phone + "</td>" +
                    "<td title='" + vip.vip_card_type + "'>" + vip.vip_card_type + "</td>" +
                    "<td title='" + list[i].num_trade + "'>" + list[i].num_trade + "</td>" +
                    "<td title='" + list[i].amt_trade + "'>" + list[i].amt_trade + "</td>" +
                    "<td title='" + list[i].ticket_sales + "'>" + list[i].ticket_sales + "</td>" +
                    "<td title='" + list[i].complete_date + "' style='width: auto'>" + list[i].complete_date + "</td>" +
                    "</tr>"
            }
        } else if (_self.task_type == "questionnaire") {
            for (var i = 0; i < list.length; i++) {
                var vip = list[i].vip;
                tr += "<tr data-answer='" + JSON.stringify(list[i].answer_info) + "' data-qtNaire='" + JSON.stringify(list[i].qtNaire_info) + "' data-id='" + vip.vip_id + "' data-code='" + vip.corp_code + "'>" +
                    "<td>" + (i + 1) + "</td>" +
                    "<td title='" + vip.vip_name + "'>" + vip.vip_name + "</td>" +
                    "<td title='" + vip.cardno + "'>" + vip.cardno + "</td>" +
                    "<td title='" + vip.vip_phone + "'>" + vip.vip_phone + "</td>" +
                    "<td title='" + vip.vip_card_type + "'>" + vip.vip_card_type + "</td>" +
                    "<td title='" + list[i].answer_num + "'>" + list[i].answer_num + "</td>" +
                    "<td title='" + list[i].complete_date + "'>" + list[i].complete_date + "</td>" +
                    "<td class='detail'><span onclick='task_track.questionnaireShow(this)'>详情<span class='icon-ishop_8-03'></span></span></td>" +
                    "</tr>"
            }
        } else if (_self.task_type == "share_counts") {
            for (var i = 0; i < list.length; i++) {
                var vip = list[i].vip_info;
                tr += "<tr data-id='" + vip.vip_id + "' data-code='" + vip.corp_code + "'>" +
                    "<td>" + (i + 1) + "</td>" +
                    "<td title='" + vip.vip_name + "'>" + vip.vip_name + "</td>" +
                    "<td title='" + vip.cardno + "'>" + vip.cardno + "</td>" +
                    "<td title='" + vip.vip_phone + "'>" + vip.vip_phone + "</td>" +
                    "<td title='" + vip.vip_card_type + "'>" + vip.vip_card_type + "</td>" +
                    "<td title='" + list[i].share_url_num + "'>" + list[i].share_url_num + "</td>" +
                    //"<td title='"+list[i].click_url_num+"'>"+list[i].click_url_num+"</td>" +
                    //"<td title='"+list[i].click_vip_num+"'>"+list[i].click_vip_num+"</td>" +
                    "<td title='" + list[i].status + "'>" + list[i].status + "</td>" +
                    "<td title='" + list[i].complete_date + "' style='width: auto'>" + list[i].complete_date + "</td>" +
                    "</tr>"
            }
        }
        $(".list_box .peopleContent table tbody").append(tr);
        _self.linkToVipInfo()
    },
    initChartClassOne: function(part, all) {
        var option = {
            title: {
                text: all == 0 ? "0%" : parseInt((part / all) * 100) + '%',
                x: 'center',
                y: 'center',
                textStyle: {
                    fontWeight: 'normal',
                    color: "#4a5f7c",
                    fontSize: 25
                }
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {

            },
            series: {
                name: '访问来源',
                type: 'pie',
                radius: ['60%', '70%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        show: true,
                        textStyle: {
                            fontSize: '30',
                            fontWeight: 'bold'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data: [{
                        value: part,
                        name: '占比',
                        itemStyle: {
                            normal: {
                                color: "#4a5f7c"
                            }
                        }
                    },
                    {
                        value: (all - part) < 0 ? 0 : (all - part),
                        name: '其他',
                        itemStyle: {
                            normal: {
                                color: "#eee"
                            }
                        }
                    }
                ]
            },
            silent: true
        };
        if (all == 0 && part == 0) {
            option.series.data[0] = {
                value: part,
                name: '占比',
                itemStyle: {
                    normal: {
                        color: "#eee"
                    }
                }
            }
        }
        var myChart = echarts.init(document.getElementById('chartClassOne'));
        myChart.setOption(option);
        window.addEventListener("resize", function() {
            myChart.resize();
        });
        $(".chartPie .chartOneClass").find(".listLoading_box").hide();
    },
    initChartLine: function(data) {
        var option;
        var _self = this;
        var time = [];
        if (_self.task_type == "invite_registration") {
            var share_vip_num = [];
            var share_url_num = [];
            var register_vip_num = [];
            for (var i = 0; i < data.length; i++) {
                share_vip_num.push(data[i].share_vip_num);
                share_url_num.push(data[i].share_url_num);
                register_vip_num.push(data[i].register_vip_num);
                time.push(data[i].time);
            }
            $("#chartLine").prev("p").html("注册人数统计").show();
            option = {
                color: ["#5f8bc8", '#eb8bbf', '#9a9acc'],
                title: {},
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ["分享人数", '分享次数', '注册人数'],
                    icon: "circle",
                    itemWidth: 10,
                    textStyle: {
                        rich: {
                            a: {
                                color: '#5f8bc8',
                                lineHeight: 10
                            },

                            b: {
                                color: '#eb8bbf',
                                lineHeight: 10
                            },
                            c: {
                                color: '#9a9acc',
                                lineHeight: 10
                            }
                        }
                    },
                    formatter: function(name) {
                        if (option.legend.data.indexOf(name) == "0") {
                            return name + '{a|   ' + share_vip_num.reduce(function(total, num) { return total + num; }) + '}';
                        } else if (option.legend.data.indexOf(name) == "1") {
                            return name + '{b|  ' + share_url_num.reduce(function(total, num) { return total + num; }) + '}';
                        } else if (option.legend.data.indexOf(name) == "2") {
                            return name + '{c|   ' + register_vip_num.reduce(function(total, num) { return total + num; }) + '}';
                        }

                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    type: 'category',
                    data: time
                },
                yAxis: [{
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    type: 'value'
                }],
                series: [{
                        name: '分享人数',
                        type: 'line',
                        data: share_vip_num,
                        symbol: 'circle'
                    },
                    {
                        name: "分享次数",
                        type: 'line',
                        symbol: 'circle',
                        data: share_url_num
                    },
                    {
                        name: '注册人数',
                        type: 'line',
                        data: register_vip_num,
                        symbol: 'circle'
                    }
                ]
            };
        } else if (_self.task_type == 'improve_data') {
            var complete_num = [];
            var partake_num = [];
            for (var n = 0; n < data.length; n++) {
                partake_num.push(data[n].partake_num);
                complete_num.push(data[n].complete_num);
                time.push(data[n].time);
            }
            $("#chartLine").prev("p").html("完成人数统计").show();
            option = {
                color: ['#9a9acc', "#5f8bc8"],
                title: {},
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ['参与会员数', "完成会员数"],
                    icon: "circle",
                    itemWidth: 10,
                    textStyle: {
                        rich: {
                            a: {
                                color: '#5f8bc8',
                                lineHeight: 10
                            },

                            b: {
                                color: '#eb8bbf',
                                lineHeight: 10
                            },
                            c: {
                                color: '#9a9acc',
                                lineHeight: 10
                            }
                        }
                    },
                    formatter: function(name) {
                        if (option.legend.data.indexOf(name) == "0") {
                            return name + '{a|   ' + partake_num.reduce(function(total, num) { return total + num; }) + '}';
                        } else if (option.legend.data.indexOf(name) == "1") {
                            return name + '{c|   ' + complete_num.reduce(function(total, num) { return total + num; }) + '}';
                        }

                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    type: 'category',
                    data: time
                },
                yAxis: [{
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    type: 'value'
                }],
                series: [{
                        name: '参与会员数',
                        type: 'line',
                        data: partake_num,
                        symbol: 'circle'
                    },
                    {
                        name: "完成会员数",
                        type: 'line',
                        symbol: 'circle',
                        data: complete_num
                    }
                ]
            };
        } else if (_self.task_type == 'consume_count' || _self.task_type == 'consume_money' || _self.task_type == 'ticket_sales') {
            var complete_num = [];
            var time = [];
            for (var n = 0; n < data.length; n++) {
                complete_num.push(data[n].complete_vip_num);
                time.push(data[n].time);
            }
            $("#chartLine").prev("p").html("完成人数统计").show();
            option = {
                color: ['#5f8bc8'],
                title: {},
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ["累计完成会员数"],
                    icon: "circle",
                    itemWidth: 10,
                    textStyle: {
                        rich: {
                            a: {
                                color: '#5f8bc8',
                                lineHeight: 10
                            },

                            b: {
                                color: '#eb8bbf',
                                lineHeight: 10
                            },
                            c: {
                                color: '#9a9acc',
                                lineHeight: 10
                            }
                        }
                    },
                    formatter: function(name) {
                        if (option.legend.data.indexOf(name) == "0") {
                            return name + '{a|   ' + complete_num.slice(complete_num.length - 1) + '}';
                        }
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    type: 'category',
                    data: time
                },
                yAxis: [{
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    type: 'value'
                }],
                series: [{
                    name: '累计完成会员数',
                    type: 'line',
                    data: complete_num,
                    symbol: 'circle'
                }]
            };
        } else if (_self.task_type == 'share_counts') {
            var share_vip_num = [];
            var share_url_num = [];
            var complete_vip_num = [];
            //                var share_url_num=[];
            for (var n = 0; n < data.length; n++) {
                share_vip_num.push(data[n].share_vip_num);
                share_url_num.push(data[n].share_url_num);
                complete_vip_num.push(data[n].complete_vip_num);
                time.push(data[n].time);
            }
            $("#chartLine").prev("p").html("完成人数统计").show();
            option = {
                color: ["#5f8bc8", '#eb8bbf', "#9a9acc"],
                title: {},
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ["分享人数", "分享次数", "完成会员数"],
                    icon: "circle",
                    itemWidth: 10,
                    textStyle: {
                        rich: {
                            a: {
                                color: '#5f8bc8',
                                lineHeight: 10
                            },

                            b: {
                                color: '#eb8bbf',
                                lineHeight: 10
                            },
                            c: {
                                color: '#9a9acc',
                                lineHeight: 10
                            }
                        }
                    },
                    formatter: function(name) {
                        if (option.legend.data.indexOf(name) == "0") {
                            return name + '{a|   ' + share_vip_num.reduce(function(total, num) { return total + num; }) + '}';
                        } else if (option.legend.data.indexOf(name) == "1") {
                            return name + '{b|   ' + share_url_num.reduce(function(total, num) { return total + num; }) + '}';
                        } else if (option.legend.data.indexOf(name) == "2") {
                            return name + '{c|   ' + complete_vip_num.reduce(function(total, num) { return total + num; }) + '}';
                        }
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    type: 'category',
                    data: time
                },
                yAxis: [{
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    type: 'value'
                }],
                series: [{
                    name: '分享人数',
                    type: 'line',
                    data: share_vip_num,
                    symbol: 'circle'
                }, {
                    name: '分享次数',
                    type: 'line',
                    data: share_url_num,
                    symbol: 'circle'
                }, {
                    name: '完成会员数',
                    type: 'line',
                    data: complete_vip_num,
                    symbol: 'circle'
                }]
            };
        } else if (_self.task_type == 'questionnaire') {
            var complete_vip_num = [];
            //                var share_url_num=[];
            for (var n = 0; n < data.length; n++) {
                complete_vip_num.push(data[n].complete_vip_num);
                time.push(data[n].time);
            }
            $("#chartLine").prev("p").html("完成人数统计").show();
            option = {
                color: ["#5f8bc8", '#eb8bbf'],
                title: {},
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ["累计完成会员数"],
                    icon: "circle",
                    itemWidth: 10,
                    textStyle: {
                        rich: {
                            a: {
                                color: '#5f8bc8',
                                lineHeight: 10
                            },

                            b: {
                                color: '#eb8bbf',
                                lineHeight: 10
                            },
                            c: {
                                color: '#9a9acc',
                                lineHeight: 10
                            }
                        }
                    },
                    formatter: function(name) {
                        if (option.legend.data.indexOf(name) == "0") {
                            return name + '{a|   ' + complete_vip_num.slice(complete_vip_num.length - 1) + '}';
                        }
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    type: 'category',
                    data: time
                },
                yAxis: [{
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            type: "dotted",
                            color: "#d4d7e0"
                        }
                    },
                    axisLine: {
                        show: false,
                        lineStyle: {
                            color: '#888'
                        }
                    },
                    type: 'value'
                }],
                series: [{
                    name: '累计完成会员数',
                    type: 'line',
                    data: complete_vip_num,
                    symbol: 'circle'
                }]
            };
        }
        var myChartLine = echarts.init(document.getElementById('chartLine'));
        myChartLine.setOption(option);
        window.addEventListener("resize", function() {
            myChartLine.resize();
        });
        $("#chartLine").next(".listLoading_box").hide();
        if (time.length > 0) {
            var ins = laydate.render({
                elem: '#search_date',
                range: true,
                min: _self.task_start_time,
                max: _self.task_end_time,
                //value:time[0]+" - "+time[time.length-1],
                //btns: ["confirm"],
                ready: function() {
                    if (window.chart_time_tip) {
                        return
                    }
                    window.chart_time_tip = true;
                    ins.hint("日期选择范围<br>" + task.start_time.slice(0, 11) + "到 " + task.end_time.slice(0, 11))
                },
                done: function(value) {
                    if (value == "") {
                        $("#search_date").attr("data-start", "");
                        $("#search_date").attr("data-end", "");
                    } else {
                        value = value.split(" - ");
                        $("#search_date").attr("data-start", value[0]);
                        $("#search_date").attr("data-end", value[1]);
                    }
                    $("#chartLine").next(".listLoading_box").show();
                    task_track.getChartData();
                }
            });
        }
    },
    tableScroll: function() {
        var _self = this;
        //滚动分页
        $(".list_box .people").unbind("scroll").bind("scroll", function() {
            var offsetHeight = $(this).height(),
                scrollHeight = $(this)[0].scrollHeight,
                scrollTop = $(this)[0].scrollTop;
            if (scrollTop + offsetHeight >= scrollHeight && scrollTop != 0) {
                if (_self.has_next_page) {
                    _self.pageIndex++;
                } else {
                    return
                }
                _self.getListData()
            }
        });
    },
    init: function() {
        this.getTaskData();
        this.bind();
    }
};
function resizePubPosition(selector){  //当窗口大小改变时，从新计算弹框的位置
    if($(selector).is(":visible")){
        var w = $(selector).outerWidth();
        var h = $(selector).outerHeight();
        $(selector).css("margin-left", -w / 2).css("margin-top", -h / 2);
    }
}
$(function() {
    $(window).resize(function() {
        resizePubPosition("#questionnaire_detail");
        resizePubPosition("#task_setting");
        resizePubPosition("#improve_data");
        resizePubPosition("#inviteRegistrationInfoDetailBox");
        $(".people,.list_content").getNiceScroll().resize();
    });
    $(".people,.inviteRegistrationInfo,.questionnaire_detail_content,.improve_data_content,.task_setting_content").niceScroll({
        cursorborder: "0 none",
        cursoropacitymin: "0",
        boxzoom: false,
        cursorcolor: " #dfdfdf",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 200,
        cursorwidth: "9px",
        cursorborderradius: "10px",
        autohidemode: true
    });
    task_track.init();
    info.init();
});