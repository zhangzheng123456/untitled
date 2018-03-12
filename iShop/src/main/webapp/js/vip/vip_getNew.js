/**
 * Created by Bizvane on 2016/12/13.
 */

//新增页面弹窗事件
var getNewVip = {
    billNo: [],
    init: function() {
        $('#jurisdiction').on('click', '#add', function(e) {
            //whir.loading.add("mask",0.5);//加载等待框
            //$(document.body).css("overflow","hidden");
            var arr = whir.loading.getPageSize();
            $("#p").css({ "width": +arr[0] + "px", "height": +arr[1] + "px" });
            $("#p").show();
            // $('#loading').remove();
            if (!($('#delete_dom').find('select').css('display') == 'inline-flex')) {
                $('#delete_dom').find('select').css('display', 'inline-flex')
            }
            if (!($('#delete_dom2').find('select').css('display') == 'inline-flex')) {
                $('#delete_dom2').find('select').css('display', 'inline-flex')
            }
            $('#OWN_SHOPPERS').empty().next().remove();
            $("#STORE_address").attr("data-name", "");
            $("#address_detail").val("");
            $("#recommend_card").val("");
            $('.vipName').val('');
            $('.birthday').val('');
            $('.billNo').val('');
            $('.phone').val('');
            $("#STORE_address").val('');
            $("#vipCardType_error").hide();
            $("#addTip").html("");
            $("#addTip").hide();
            $('#get_more').show();
            $("#address_nav a:nth-child(1)").trigger("click");
            $('#content .hint').remove();
            e.stopPropagation();
            this.getMoreStore();
            this.getparamCtrol();
            this.gender();
            getProvince();
        }.bind(this));
        $('#get_more .head_span_r').click(function() {
            $('#get_more').hide();
            //whir.loading.remove('mask');//移除加载框
            $("#p").hide();
            //$(document.body).css("overflow","auto");
        });
        $('#get_more_close').click(function() {
            $('#get_more').hide();
            $("#p").hide();
            //$(document.body).css("overflow","auto");
            //whir.loading.remove('mask');//移除加载框
        });
        $('#get_more_save').click(function() {
            var reg = /^1[3|4|5|7|8][0-9]{9}$/; //验证规则
            if ($("#OWN_STORE").val() == "" || $("#OWN_STORE").val() == null) {
                $("#OWN_STORE_error").html('<span class="hint hint_l">所属店铺不可为空</span>');
                $("#OWN_STORE_error").show();
                return
            }
            if ($("#OWN_SHOPPERS").val() == "" || $("#OWN_SHOPPERS").val() == null) {
                $("#OWN_SHOPPERS_error").html('<span class="hint hint_l">所属导购不可为空</span>');
                $("#OWN_SHOPPERS_error").show();
                return
            }
            if ($('#content .billNo').val().trim() == '') {

                if ($('#billNo_error').find('.hint_l').length == 1) return;
                $('#billNo_error').show();
                var html = '<span class="hint hint_l" style="display: inline-block;width: 100%;">零售单号不能为空</span>';
                $('#billNo_error').append(html);
                return
            }
            if ($('#content .vipName').val().trim() == '') { $('#content .vipName').trigger('blur'); return }
            if ($('#content .birthday').val().trim() == '') { $('#content .birthday').trigger('blur'); return }
            if ($('#content .cardNo').val().trim() == '') { $('#content .cardNo').trigger('blur'); return }
            if ($('#content .phone').val().trim() != "" && !reg.test($('#content .phone').val().trim())) {
                $('#content .phone').trigger('blur');
                return;
            }
            if ($("#vipCardType").val() == "" || $("#vipCardType").val() == null) {
                $("#vipCardType_error").html('<span class="hint hint_l">会员卡类型不可为空</span>');
                $("#vipCardType_error").show();
                return
            }
            this.postParma();
        }.bind(this));
        var me = this;
        $('#content').on('blur', 'input', function() {
            var html = '';
            if (this.className == 'billNo') {
                if ($(this).val().trim() == '') {
                    if ($('#billNo_error').find('.hint_l').length == 1) return;
                    $('#billNo_error').show();
                    html = '<span class="hint hint_l" style="display: inline-block;width: 100%;">零售单号不能为空</span>';
                    $('#billNo_error').append(html);
                } else {
                    $('#billNo_error').find('.hint_l').remove();
                }
            }
            // if(this.className=='addressInput'){
            //     if($(this).val().trim()==''){
            //         $('#address .addressInput').parent().parent().next().find('.content_hint').html('<span class="hint">地址不能为空</span>');
            //         $('#address .addressInput').parent().parent().next().find('.content_hint').show();
            //     }else{
            //         $('#address .addressInput').parent().parent().next().find('.content_hint').hide();
            //     }
            // }
            if (this.className == 'vipName') {
                if ($(this).val().trim() == '') {
                    if ($('#name_error').find('.hint_l').length == 1) return;
                    $('#name_error').show();
                    html = '<span class="hint hint_l" style="display: inline-block;width: 100%;">姓名不能为空</span>';
                    $('#name_error').append(html);
                } else {
                    $('#name_error').find('.hint_l').remove();
                }
            } else if (this.className.search('birthday') != -1) {
                if ($(this).val().trim() == '') {
                    if ($('#birth_day_error').find('.hint_r').length == 1) return;
                    $('#birth_day_error').show();
                    html = '<span class="hint hint_r" style="width: 100%">生日不能为空</span>';
                    $('#birth_day_error').append(html);
                } else {
                    $('#birth_day_error').find('.hint_r').remove();
                }
            } else if (this.className == 'phone') {
                if ($(this).val().trim() == '') {
                    //if($('#phone_error').find('.hint_l').length==1)return;
                    //$('#phone_error').show();
                    //html='<span class="hint hint_l" style="display: inline-block;width: 100%;">手机号不能为空</span>';
                    //$('#phone_error').html(html);
                } else {
                    var reg = /^1[0-9]{10}$/; //验证规则
                    $('#phone_error').show();
                    if (!reg.test($(this).val().trim())) {
                        html = '<span class="hint hint_l" style="display: inline-block;width: 100%;">手机号格式不正确</span>';
                        $('#phone_error').html(html);
                        return
                    }
                    $('#phone_error').find('.hint_l').remove();
                }
            }
        });
        //失去焦点
        $('.billNo').focus(function() {
            $('#billNo_drop_down').show();
        });
        $('#sale_num .down_icon_vip').click(function() {
            $("#content").getNiceScroll().resize();
            $('#billNo_drop_down').toggle();
            $("#billNo_drop_down").niceScroll({
                cursorborder: "0 none",
                cursoropacitymin: "0",
                boxzoom: false,
                cursorcolor: " #dfdfdf",
                cursoropacitymax: 1,
                touchbehavior: false,
                cursorminheight: 50,
                cursorwidth: "5px",
                cursorborderradius: "10px"
            });
        });
        $('#address .down_icon_vip').click(function() {
            $('#address_container').toggle();
            $("#content").scrollTop($("#content")[0].scrollHeight - $("#content").height());
            $("#content").getNiceScroll().resize();
        });
        $("#STORE_address").click(function() {
            $('#address_container').toggle();
        });
        $('#billNo_drop_down').on('click', 'li', function() {
            $(this).addClass('selected').siblings('.selected').removeClass('selected');
            $('.billNo').val(this.innerHTML);
            $('#content .billNo').parent().parent().next().find('.content_hint').hide();
            $('#billNo_drop_down').toggle();
        });
        $('.billNo').bind('input propertychange', function() {
            var key = this.value;
            var html_dom = [];
            var search_dom = [];
            html_dom = me.billNo.filter(function(val, index, arr) {
                return val.search(key) != -1 && (html_dom.join(',').search(val) == -1);
            });
            for (var i = 0; i < html_dom.length; i++) {
                search_dom.push('<li>' + html_dom[i] + '</li>');
            }
            $('#billNo_drop_down').html(search_dom.join(''));
            $("#billNo_drop_down").niceScroll({
                cursorborder: "0 none",
                cursoropacitymin: "0",
                boxzoom: false,
                cursorcolor: " #dfdfdf",
                cursoropacitymax: 1,
                touchbehavior: false,
                cursorminheight: 50,
                cursorwidth: "5px",
                cursorborderradius: "10px"
            });
        });
        //关闭点击事件
        $(document).click(function(event) {
            if (!$("#vipCardType").next().children().eq(2).hasClass("searchable-select-hide")) {
                $("#content").scrollTop($("#content")[0].scrollHeight - $("#content").height());
            }
            if (event.target.className == 'down_icon_vip') return;
            //取消事件冒泡
            var e = arguments.callee.caller.arguments[0] || event;
            if (e && e.stopPropagation) {
                e.stopPropagation();
            } else if (window.event) {
                window.event.cancelBubble = true;
            }
            if ($(event.target).parents('#billNo_drop_down').length == 0 && (event.target.className != 'billNo'))($('#billNo_drop_down').hide());
            if (!($(event.target).is("#STORE_address") || $(event.target).is("#address .down_icon_vip") || $(event.target).is(".address_nav a") || $(event.target).is(".dl_box a"))) {
                $("#address_container").hide();
                $("#content").getNiceScroll().resize();
            } else {
                $("#content").scrollTop($("#content")[0].scrollHeight - $("#content").height());
                $("#content").getNiceScroll().resize();
            }
        });
        $("#address_nav a").click(function() { //选择地址
            var index = $(this).index();
            $(this).addClass("address_liActive");
            $(this).siblings().removeClass("address_liActive");
            $(".address_content").children('.dl_box').eq(index).show();
            $(".address_content").children('.dl_box').eq(index).siblings().hide();
        });
        $("#addVipEnter,#addVipX").click(function() {
            if ($("#msg").html() == "新增成功" || $("#msg").html() == "更新成功") {
                window.location.reload();
            }
            if ($("#update_vip_msg").is(":visible")) {
                $("#content_box").css("zIndex", "1000");
            } else {
                $("#content_box").hide();
            }
            $("#addVipTk").hide();
            $("#msg").html("");
            //$(document.body).css("overflow","auto");
        });
        $("#close_up").click(function() {
            $("#update_vip_msg").hide();
            $("#content_box").hide();
            $("#get_more").show();
            //$(document.body).css("overflow","auto");
        })
    },
    getBillNo: function() {
        var param = {};
        var me = this;
        $("#billNo_error").hide();
        param.store_code = String($('#OWN_STORE').val()).search('null') != -1 ? '' : $('#OWN_STORE').val().split('-')[0];
        param.corp_code = 'C10000';
        oc.postRequire("post", "/vip/dayNoVipBill", "", param, function(data) {
            if (data.code == 0) {
                var data = JSON.parse(JSON.parse(data.message).order_list);
                var html = [];
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        html.push('<li>' + data[i].order_id + '</li>');
                        me.billNo.push(data[i].order_id);
                    }
                }
                if (html.length == 0) {
                    $('#billNo_drop_down').html('<span style="display:block;width: 100%;height: 100%;text-align: center;line-height: 160px;">暂无数据</span>');
                } else {
                    $('#billNo_drop_down').html(html.join(''));
                }
            } else if (data.code == -1) {}
        });
    },
    getparamCtrol: function() {
        oc.postRequire("get", "/vip/paramController?corp_code=" + sessionStorage.getItem('corp_code'), "", '', function(data) {
            if (data.code == 0) {
                //控制两个节点
                var obj = JSON.parse(data.message);
                for (var key in obj) {
                    if (key == 'is_show_cardNo') {
                        if (obj[key] == 'Y') {
                            $('#content .cardNo').val('').removeAttr('readonly');
                        } else {
                            $('#content .cardNo').val('由系统自动生成').attr('readonly');
                        }
                    } else if (key == 'is_show_billNo') {
                        if (obj[key] == 'N') {
                            // $('#content .billNo').hide();
                            $('#content li:first-child').hide();
                        } else {
                            $('#content li:first-child').show();
                        }
                    }
                }
            } else if (data.code == -1) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });

            }
        })
    },
    getMoreStore: function() {
        whir.loading.add("处理中,请稍后", 0.5); //加载等待框
        $('#content_box').show();
        var me = this;
        $('#OWN_STORE').empty().next().remove();
        //获取所属企业列表
        var corp_command = "/shop/findStore";
        oc.postRequire("get", corp_command, "", "", function(data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                msg = JSON.parse(msg.list);
                var corp_html = '';
                for (var i = 0; i < msg.length; i++) {
                    corp_html += '<option value="' + msg[i].store_code + '-' + msg[i].corp_code + '">' + msg[i].store_name + '</option>';
                }
                $("#OWN_STORE").append(corp_html);
                $('#OWN_STORE').searchableSelect();
                var title = $("#delete_dom .searchable-select-items .selected").html();
                $(".searchable-select-holder").attr("title", title);
                // $('#OWN_STORE').parent().find('.searchable-select-input')
                $('#get_more_store .corp_select .searchable-select-input').keydown(function(event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        $(".searchable-select-holder").attr("title", $("#delete_dom .searchable-select-items .selected").html());
                        $("#services").html("");
                        $("input[verify='Code']").val("");
                        $("#BRAND_NAME").val("");
                        $("input[verify='Code']").attr("data-mark", "");
                        $("#BRAND_NAME").attr("data-mark", "");
                        //调导购
                        me.getMoreStaff();
                        me.getBillNo();
                        me.getLevel();
                    }
                });
                $('#get_more_store .searchable-select-item').click(function() {
                    $(".searchable-select-holder").attr("title", $(this).html());
                    $("#services").html("");
                    $("input[verify='Code']").val("");
                    $("#BRAND_NAME").val("");
                    $("input[verify='Code']").attr("data-mark", "");
                    $("#BRAND_NAME").attr("data-mark", "");
                    //调导购
                    me.getMoreStaff();
                    me.getBillNo();
                    me.getLevel();
                })
                whir.loading.remove(); //移除加载框
                $('#content_box').hide();
                me.getMoreStaff();
                me.getBillNo();
                me.getLevel();
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
                $('#content_box').hide();
            }
        });
    },
    gender: function() {
        $('.searchable-select').remove();
        $('#gender').empty();
        $('#vipCardType').empty();
        //性别
        var corp_html = '<option value="男">男</option>' + '<option value="女">女</option>';
        $("#gender").append(corp_html);
        $('#gender').searchableSelect();
        $('#gender').parent().find('.searchable-select-holder').html('男');
        $('#gender').parent().find('.searchable-select-input').remove();

        // var corp_htm2='<option value="东鹏玖姿贵宾卡">东鹏玖姿贵宾卡</option>'
        //     +'<option value="玖姿贵宾卡">玖姿贵宾卡</option>'
        //     +'<option value="南阳贵宾卡">南阳贵宾卡</option>'
        //     +'<option value="玖姿累积卡">玖姿累积卡</option>';
        //     $("#vipCardType").append(corp_htm2);
        //     $('#vipCardType').searchableSelect();
        //     $('#vipCardType').parent().find('.searchable-select-holder').html('东鹏玖姿贵宾卡');
        //     $('#vipCardType').parent().find('.searchable-select-input').remove();
    },
    getLevel: function() {
        $("#vipCardType").empty().next().remove();
        var _param = {};
        var code = $('#OWN_STORE').val().split('-');
        _param.corp_code = code[1];
        _param.store_code = code[0];
        oc.postRequire("post", "/vipCardType/getCardTypesByRole", "", _param, function(data) {
            if (data.code == "0") {
                var arr_card = JSON.parse(JSON.parse(data.message).list);
                if (arr_card.length > 0) {
                    var card_html = [];
                    for (var i = 0; i < arr_card.length; i++) {
                        card_html.push('<option value="' + arr_card[i].vip_card_type_name + '">' + arr_card[i].vip_card_type_name + '</option>');
                    }
                    $("#vipCardType").append(card_html.join(''));
                    $('#vipCardType').searchableSelect();
                    $('#vipCardType').parent().find('.searchable-select-holder').html(arr_card[0].vip_card_type_name);
                    //$('#vipCardType').parent().find('.searchable-select-input').remove();
                } else {
                    var corp_htm2 = '';
                    $("#vipCardType").append(corp_htm2);
                    $('#vipCardType').searchableSelect();
                    $('#vipCardType').parent().find('.searchable-select-holder').html('暂无会员卡类型');
                    $('#vipCardType').parent().find('.searchable-select-input').remove();
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
    },
    getMoreStaff: function() {
        whir.loading.add("处理中，请稍后", 0.5); //加载等待框
        $('#content_box').show();
        $('#OWN_SHOPPERS').empty().next().remove();
        $("#OWN_SHOPPERS_error").hide();
        var _param = {};
        var code = $('#OWN_STORE').val().split('-');
        _param.store_code = code[0];
        _param.corp_code = code[1];
        oc.postRequire("post", "/shop/staff", "", _param, function(data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var corp_html = '';
                for (var i = 0; i < msg.length; i++) {
                    corp_html += '<option value="' + msg[i].user_code + '-' + msg[i].user_name + '">' + msg[i].user_name + '</option>';
                }
                $("#OWN_SHOPPERS").append(corp_html);
                $('#OWN_SHOPPERS').searchableSelect();
                $('.corp_select .searchable-select-input').keydown(function(event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        $("#services").html("");
                        $("input[verify='Code']").val("");
                        $("#BRAND_NAME").val("");
                        $("input[verify='Code']").attr("data-mark", "");
                        $("#BRAND_NAME").attr("data-mark", "");
                    }
                });
                $('.searchable-select-item').click(function() {
                    $("#services").html("");
                    $("input[verify='Code']").val("");
                    $("#BRAND_NAME").val("");
                    $("input[verify='Code']").attr("data-mark", "");
                    $("#BRAND_NAME").attr("data-mark", "");
                });
                whir.loading.remove(); //移除加载框
                $('#content_box').hide();
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
                $('#content_box').hide();
                whir.loading.remove(); //移除加载框
            }
        })
    },
    postParma: function() {
        //获取参数
        var self = this;
        var param = {};
        param.corp_code = 'C10000';
        param.address_detail = $("#address_detail").val().trim();
        param.recom_vip_card_no = $("#recommend_card").val().trim();
        param.phone = $('#content').find('.phone').val().trim();
        param.card_no = $('#content').find('.cardNo').val().trim();
        param.vip_name = $('#content').find('.vipName').val().trim();
        param.billNo = $('#content').find('.billNo').val().trim();
        param.vip_card_type = $('#vipCardType').val().trim();
        param.birthday = $('#content').find('.birthday').val().trim();
        param.sex = $('#gender').val().trim();
        param.user_code = String($('#OWN_SHOPPERS').val()).search('null') != -1 ? '' : $('#OWN_SHOPPERS').val().split('-')[0];
        param.user_name = String($('#OWN_SHOPPERS').val()).search('null') != -1 ? '' : $('#OWN_SHOPPERS').val().split('-')[1];
        param.store_code = String($('#OWN_STORE').val()).search('null') != -1 ? '' : $('#OWN_STORE').val().split('-')[0];
        param.wechat = $("#wechat_num").val();
        param.address = $("#STORE_address").attr("data-name");
        $('#loading_2').show();
        $("#content_box").css("position", "fixed");
        $("#content_box").show();
        oc.postRequire("post", "/vip/addVip", "", param, function(data) {
            $('#loading_2').hide();
            if (data.code == 0) {
                //$('#loading').remove();
                $("#addVipTk").show();
                $("#msg").html("新增成功");
                $("#addTip").html("<span class='icon-ishop_6-15' style='font-size: 20px;color: #6dc1c8;vertical-align: middle;margin-right: 5px;'></span><span style='vertical-align: middle'>完成</span>")
                $("#addTip").show();
            } else if (data.code == -1) {
                var str = data.message == '' ? '新增失败' : data.message;
                $("#addTip").html("<span class='icon-ishop_6-12' style='font-size: 20px;color: #c26555;vertical-align: middle;margin-right: 5px;'></span><span style='vertical-align: middle' title='" + data.message + "'>" + data.message + "</span>")
                $("#addTip").show();
                if (data.remark != undefined) {
                    $("#update_vip_msg").show();
                    $("#get_more").hide();
                    self.updateVip(JSON.parse(data.remark))
                } else {
                    $("#addVipTk").show();
                    $("#msg").niceScroll({
                        cursorborder: "0 none",
                        cursoropacitymin: "0",
                        boxzoom: false,
                        cursorcolor: " #dfdfdf",
                        cursoropacitymax: 1,
                        touchbehavior: false,
                        cursorminheight: 50,
                        cursorwidth: "5px",
                        autohidemode: false,
                        cursorborderradius: "10px"
                    });
                    $("#msg").html(str);
                }
            }
        })
    },
    updateVip: function(remark) {
        var self = this;
        $("#up_store_name").html($("#OWN_STORE").find("option:selected").text());
        $("#up_vip_name").html($('#content').find('.vipName').val());
        $("#up_sex").html($('#gender').val());
        $("#up_phone").html($('#content').find('.phone').val());
        $("#up_vip_card").html(remark.card_no_vip);
        $("#up_vip_card_type").html(remark.card_type_id);
        $("#up_staff").html(String($('#OWN_SHOPPERS').val()).search('null') != -1 ? '' : $('#OWN_SHOPPERS').val().split('-')[1]);

        $("#up_staff").attr("data-value", String($('#OWN_SHOPPERS').val()).search('null') != -1 ? '' : $('#OWN_SHOPPERS').val().split('-')[0]);
        $("#up_store_name").attr("title", $("#OWN_STORE").find("option:selected").text());
        $("#up_store_name").attr("data-value", String($('#OWN_STORE').val()).search('null') != -1 ? '' : $('#OWN_STORE').val().split('-')[0]);
        $("#update").unbind("click").bind("click", function() {
            self.updateVipSend(remark.vip_id)
        })
    },
    updateVipSend: function(remarkVipId) {
        var param = {};
        param.vip_id = remarkVipId;
        param.vip_name = $('#up_vip_name').html();
        param.corp_code = "";
        param.phone = $('#up_phone').html();
        param.birthday = "";
        param.card_no = $('#up_vip_card').html();
        param.vip_card_type = $("#up_vip_card_type").html();
        param.sex = $("#up_sex").html();
        param.user_code = $("#up_staff").attr("data-value");
        param.user_name = $("#up_staff").html();
        param.store_code = $("#up_store_name").attr("data-value");
        param.store_name = $("#up_store_name").html();
        $("#content_box").css("zIndex", "10003");
        $('#loading_2').show();
        oc.postRequire("post", "/vip/updateVip", "", param, function(data) {
            $('#loading_2').hide();
            $("#addVipTk").css("zIndex", "10004");
            if (data.code == 0) {
                $("#addVipTk").show();
                $("#msg").html("更新成功");
            } else if (data.code == -1) {
                $("#addVipTk").show();
                $("#msg").html("更新失败");
            }
        });
    },
    testBlur: function() {
        if ($('#content').find('.billNo').val() == '') return;
        var param = {};
        param.corp_code = 'C10000';
        param.type = 'billNo';
        param.billNo = $('#content').find('.billNo').val();
        oc.postRequire("post", "/vip/checkBillNo", "", param, function(data) {
            if (data.code == 0) {
                var obj = JSON.parse(data.message);
                obj.can_pass == 'N' ? $('#content .content_hint').show() : $('#content .content_hint').hide();
            } else if (data.code == -1) {
                art.dialog({
                    zIndex: 10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    testInput: function(node) {
        if ($(node).val().trim() == '') {
            var reg = /^1[3|4|5|7|8][0-9]{9}$/; //验证规则
        }
        return;
        //不为空验证
        var html = '';
        if ($(node).val().trim() == '') {
            html = $(node).parent().prev().html().slice(0, -1);
            //left
            var HTML = '<li class="hint_li"><div class="content_hint"style="display: block;overflow: hidden"><div style="width:50%;float: left"><span class="hint">' + html + '不能为空</span></div></div></li>';
            //right
            var HTML = '<li class="hint_li"><div class="content_hint"style="display: block;overflow: hidden"><div style="width:50%;float:right"><span class="hint">' + html + '不能为空</span></div></div></li>';
            var nd = $(node).parent().parent().next()[0];
            if (!nd.className) {
                $(node).parent().parent().after(HTML);
            }
        } else {
            $(node).parent().parent().next('.hint_li').remove();
        }
    }
}

function checkStart(data) {
    $('#birth_day_error').find('.hint_r').remove();
    getNewVip.testInput($('#content .birthday')[0]);
    $('#test_1').find('.hint_r').hide();
}

function scroll() {
    console.log('触发');
}

function getProvince() {
    var param = {};
    oc.postRequire('post', '/location/getProvince', '', param, function(data) {
        if (data.code == "0") {
            $(".dl_box dl dd").empty();
            var msg = JSON.parse(data.message);
            for (var i = 0; i < msg.length; i++) {
                $("#province dl dd").append('<a title="' + msg[i].short_name + '" data-code="' + msg[i].location_code + '" data-name="' + msg[i].location_name + '" href="javascript:;">' + msg[i].short_name + '</a>');
            }
            $("#province a").click(function() {
                var val = $(this).html();
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(2)").trigger("click");
                $("#county dl dd").empty();
                getCity();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name", $(this).attr("data-name"));
            })
        } else {
            console.log(data.message);
        }
    })
} //获取省份
function getCity() {
    var param = {};
    param['higher_level_code'] = $("#province .current").attr("data-code");
    oc.postRequire('post', '/location/getProvince', '', param, function(data) {
        var msg = JSON.parse(data.message);
        if (data.code == "0") {
            $("#city dl dd").empty();
            for (var i = 0; i < msg.length; i++) {
                $("#city dl dd").append('<a title="' + msg[i].short_name + '" data-code="' + msg[i].location_code + '" data-name="' + msg[i].location_name + '" href="javascript:;">' + msg[i].short_name + '</a>');
            }
            $("#city a").click(function() {
                var val = $("#province .current").html();
                val += '/' + $(this).html();
                var data_name = $("#province .current").attr("data-name") + '/' + $(this).attr("data-name");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(3)").trigger("click");
                getCounty();
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name", data_name);
            })
        } else {
            console.log(data.message);
        }
    })
}

function getCounty() {
    var param = {};
    param['higher_level_code'] = $("#city .current").attr("data-code");
    oc.postRequire('post', '/location/getProvince', '', param, function(data) {
        var msg = JSON.parse(data.message);
        if (data.code == "0") {
            $("#county dl dd").empty();
            for (var i = 0; i < msg.length; i++) {
                $("#county dl dd").append('<a title="' + msg[i].short_name + '" data-code="' + msg[i].location_code + '" data-name="' + msg[i].location_name + '" href="javascript:;">' + msg[i].short_name + '</a>');
            }
            $("#county a").click(function() {
                var val = $("#province .current").html() + '/' + $("#city .current").html() + '/' + $(this).html();
                var data_name = $("#province .current").attr("data-name") + '/' + $("#city .current").attr("data-name") + '/' + $(this).attr("data-name");
                $(this).addClass("current");
                $(this).siblings().removeClass("current");
                $("#address_nav a:nth-child(3)").trigger("click");
                $("#STORE_address").val(val);
                $("#STORE_address").attr("data-name", data_name);
            })
        } else {
            console.log(data.message);
        }
    })
}
$(document).ready(function() {
    getNewVip.init();
    $('#update_vip_msg').draggable({ handle: '.update_vip_msg_title' });
    $("#vipCardType").next().click(function() {
        if ($(this).next().children().eq(2).hasClass("searchable-select-hide")) {
            alert(2)
        } else {
            $("#content").scrollTop(100)
        }
    });
    $('#content').scroll(function() {
        laydate.reset();
    })
});