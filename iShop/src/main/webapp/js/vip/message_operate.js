var oc = new ObjectControl();
var area_num = 1;
var area_next = false;
var shop_num = 1;
var shop_next = false;
var staff_num = 1;
var staff_next = false;
var isscroll = false;
var sms_vips_edit = {};
var screenData = [];
var isAllVip = false;
var allVipNum = 0;
var message = {
    clickMark: "", //员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
    cache: { //缓存变量
        "vip_id": "",
        "area_codes": "",
        "area_names": "",
        "brand_codes": "",
        "brand_names": "",
        "store_codes": "",
        "store_names": "",
        "user_codes": "",
        "user_names": "",
        group_codes: "",
        group_names: "",
        "type": "",
        "count": "",
        "corp_code": "",
        check_status: ""
    },
    init: function() {
        this.allEvent();
        var id = sessionStorage.getItem("id");
        if (id !== null) {
            this.getSelect(id);
        } else if (id == null) {
            var group_vip = sessionStorage.getItem("group_vip");
            if (group_vip !== null) {
                var corp = JSON.parse(group_vip).corp_code;
                var group = JSON.parse(group_vip).group_code;
                $(".item_2").find("input").attr("data-value", "vip_group");
                $(".item_2").find("input").val("指定会员分组");
                $("#sendee").hide();
                $("#group_select_parent").show();
                this.getCorplist(corp, group);
            } else {
                this.getCorplist();
            }
        }
    },
    getCorplist: function(corp, group) { //获取所属企业列表
        var self = this;
        var corp_command = "/user/getCorpByUser";
        whir.loading.add("", 0.5); //加载等待框
        oc.postRequire("post", corp_command, "", "", function(data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                var corp_html = '';
                for (var i = 0; i < msg.corps.length; i++) {
                    corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
                }
                $("#OWN_CORP").append(corp_html);
                if (corp !== "") {
                    $("#OWN_CORP option[value='" + corp + "']").attr("selected", "true");
                }
                var corp_code = $("#OWN_CORP").val();
                self.getVipGroupList(corp_code, group);
                $('#corp_select select').searchableSelect();
                if (self.check_status == "Y") {
                    $(".conpany_msg .searchable-select-holder").css("backgroundImage", "url(../img/btn_dropRead.png)");
                }
                select_corp_code = $("#OWN_CORP").val();
                expend_data_1();
                $('#corp_select .searchable-select-input').keydown(function(event) {
                    var event = window.event || arguments[0];
                    if (event.keyCode == 13) {
                        var corp_code1 = $("#OWN_CORP").val();
                        if (corp_code !== corp_code1) {
                            self.cache.vip_id = "";
                            self.cache.area_codes = "";
                            self.cache.area_names = "";
                            self.cache.brand_codes = "";
                            self.cache.brand_names = "";
                            self.cache.store_codes = "";
                            self.cache.store_names = "";
                            self.cache.user_codes = "";
                            self.cache.user_names = "";
                            self.cache.type = "";
                            self.cache.count = "";
                            corp_code = corp_code1;
                            $("#WX_TYPE").val("");
                            $("#WX_TYPE").removeAttr('data-app_id');
                            $("#WX_TYPE").removeAttr('app_user_name');
                            $("#sendee_r").val("已选0个");
                            $("#message_content").val("");
                            $("#WX_Template").val("");
                            $("#WX_Template").attr('data-template_id', "");
                            $("#WX_Template").attr('data-template_name', "");
                            $("#template").html("");
                            $("#file").attr("data-id", "")
                            $("#templet_settings .tempalte_area .templet_line input:not(#heading,#tem_link)").parent().remove();
                            if ($("#MESSAGE_TYPE").val() == "微信群发消息") {
                                getWxList($("#OWN_CORP").val());
                            }
                            self.getVipGroupList(corp_code);
                            select_corp_code = $("#OWN_CORP").val();
                            expend_data_1();
                            $("#select_clear").trigger("click");
                            _param = {};
                            inx = 1;
                            pageNumber = 1;
                            pageSize = 10;
                            clearSelectList();
                            var reg = /活动|会员任务|券/;
                            if (reg.test($(".condition_active").eq(1).html())) {
                                $(".condition_active").eq(1).parent().trigger("click");
                            }
                        }
                    }
                });
                $('#corp_select .searchable-select-item').click(function() {
                    var corp_code1 = $("#OWN_CORP").val();
                    if (corp_code !== corp_code1) {
                        self.cache.vip_id = "";
                        self.cache.area_codes = "";
                        self.cache.area_names = "";
                        self.cache.brand_codes = "";
                        self.cache.brand_names = "";
                        self.cache.store_codes = "";
                        self.cache.store_names = "";
                        self.cache.user_codes = "";
                        self.cache.user_names = "";
                        self.cache.type = "";
                        self.cache.count = "";
                        corp_code = corp_code1;
                        $("#sendee_r").val("已选0个");
                        $("#WX_TYPE").val("");
                        $("#WX_TYPE").removeAttr('data-app_id');
                        $("#WX_TYPE").removeAttr('app_user_name');
                        $("#WX_Template").val("");
                        $("#WX_Template").attr('data-template_id', "");
                        $("#WX_Template").attr('data-template_name', "");
                        $("#template").html("");
                        $("#templet_settings .tempalte_area .templet_line input:not(#heading,#tem_link)").parent().remove();
                        $("#message_content").val("");
                        if ($("#MESSAGE_TYPE").val() == "微信群发消息") {
                            getWxList($("#OWN_CORP").val());
                        }
                        self.getVipGroupList(corp_code);
                        select_corp_code = $("#OWN_CORP").val();
                        expend_data_1();
                        _param = {};
                        inx = 1;
                        pageNumber = 1;
                        pageSize = 10;
                        $("#select_clear").trigger("click");
                        clearSelectList();
                        var reg = /活动|会员任务|券/;
                        if (reg.test($(".condition_active").eq(1).html())) {
                            $(".condition_active").eq(1).parent().trigger("click");
                        }
                    }
                })
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove(); //移除加载框
        });
    },
    getVipGroupList: function(corp_code, group) {
        var param = {};
        param["corp_code"] = corp_code;
        oc.postRequire("post", "/vipGroup/getCorpGroupsAll", "", param, function(data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var html = "";
                $('#group_select .searchable-select').remove();
                for (var i = 0; i < list.length; i++) {
                    html += "<option value='" + list[i].vip_group_code + "'>" + list[i].vip_group_name + "</option>"
                }
                $("#vip_group").html(html);
                if (group !== "") {
                    $("#vip_group option[value='" + group + "']").attr("selected", "true");
                }
                $('#vip_group').searchableSelect();
            }
        })
    },
    notEdit: function() {
        $("#edit_sendee,.btn-export").css("backgroundColor", "#dfdfdf");
        $("#sendee_r").css({
            "background-color": "#fff",
            "border-radius": "4px 0 0 4px"
        });
        $(".drop_down input").css("backgroundImage", "url(../img/btn_dropRead.png)");
        $("#send_close").css({
            "z-index": "1000",
            "position": "relative"
        })

    },
    //编辑界面
    getSelect: function(id) {
        var self = this;
        var param = {};
        param["id"] = id;
        oc.postRequire("post", "/vipFsend/select", "", param, function(data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var corp_code = message.corp_code;
                var group_code = message.vip_group_code;
                var send_time = message.send_time;
                var activity_vip_code = message.activity_vip_code;
                var send_scope = "";
                $("#created_time").val(message.created_date);
                $("#creator").val(message.creater);
                $("#modify_time").val(message.modified_date);
                $("#modifier").val(message.modifier);
                self.check_status = message.check_status;
                if (message.send_scope == "input_file") {
                    sms_vips_edit = message.sms_vips == "" ? "" : message.sms_vips;
                } else {
                    sms_vips_edit = message.sms_vips == "" ? "" : JSON.parse(message.sms_vips);
                }
                if (sms_vips_edit == "" || sms_vips_edit.vips == "") {
                    $("#edit_sendee").unbind("click").css("backgroundColor", "#888")
                }
                if (message.send_scope == "vip_condition" || message.send_scope == "vip_condition_new") {
                    send_scope = "指定会员"
                } else {
                    send_scope = $("#send_scope").siblings("ul").find("li[data-value='" + message.send_scope + "']").text();
                }

                $("#send_scope").val(send_scope);
                $("#send_time").val(send_time);
                $("#send_scope").attr("data-value", message.send_scope);
                if (message.send_scope == "vip" || message.send_scope == "vip_condition" || message.send_scope == "vip_condition_new") { //
                    $("#group_select_parent").hide();
                    $("#sendee").show();
                    var vips_lenght = message.vips_count;
                    //var vip_id=sms_vips.vips;
                    //self.cache.vip_id=vip_id;
                    //var vipList=vip_id.split(',');
                    $("#sendee_r").val("已选" + vips_lenght + "个");
                    self.getCorplist(corp_code);
                } else if (message.send_scope == "vip_group") {
                    $("#group_select_parent").show();
                    $("#sendee").hide();
                    self.getCorplist(corp_code, group_code);
                } else if (message.send_scope == "input_file") {
                    $("#group_select_parent").hide();
                    $("#sendee").hide();
                    $("#export_sendee").show();
                    $("#file").attr("data-id", sms_vips_edit)
                    $("#file").attr("title", "已导入会员列表");
                    $("#export_tip").html("导入会员数:" + (message.cardno_num == "" ? 0 : message.cardno_num))
                    $("#export_tip").show();
                    self.getCorplist(corp_code);
                }
                if (message.send_type == "wxmass") {
                    $("#MESSAGE_TYPE").val("微信群发消息");
                    getWxList(corp_code);
                    //self.templet_settings_value(message.content==""?"":JSON.parse(message.content));
                    $("#WX").show();
                    $("#WXTemplate").show();
                    $("#templet_settings").show();
                    $("#templet_settings").prev().hide();
                    $("#WX_TYPE").attr("data-app_id", message.app_id);
                    $("#WX_TYPE").val(message.app_name);
                    $("#WX_Template").val(message.template_name);
                    getTemplates(corp_code).then(function() {
                        $("#template").find("li[data-template_name=" + message.template_name + "]").trigger("click");
                        self.templet_settings_value(message.content == "" ? "" : JSON.parse(message.content));
                    })
                } else if (message.send_type == "sms") {
                    $("#MESSAGE_TYPE").val("短信");
                }
                if (message.check_status != "Y" && activity_vip_code == "") {
                    self.getPower();
                } else if (message.check_status == "Y" || activity_vip_code != "") {
                    var div = $("<div style='z-index: 888;position: fixed;left: 0;top: 160px;bottom: 0;right: 0;background-color: rgba(0,0,0,0)'></div>");
                    $("body").append(div);
                    self.notEdit();
                }
                $("#message_content").val(message.content);
            }
        });
    },
    getPower: function() {
        var key_val = sessionStorage.getItem("key_val"); //取页面的function_code
        key_val = JSON.parse(key_val); //取key_val的值
        var funcCode = key_val.func_code;
        var param = {
            funcCode: funcCode
        };
        oc.postRequire("post", "/list/action", "0", param, function(data) {
            var actions = JSON.parse(data.message).actions;
            for (var i = 0; i < actions.length; i++) {
                if (actions[i].act_name == "check") {
                    $('#send_close').before('<li id="auditParse" style="background-color: #41c7db" class="bg"><span class="icon-ishop_6-20"></span>通过审核</li>');
                }
            }
        })
    },
    templet_settings_value: function(content) {
        $("#heading").val(content.first);
        $("#remark").val(content.remark);
        $("#tem_link").val(content.url);
        for (var key in content) {
            if (key != "first" && key != "remark" && key != "url") {
                $("#templet_settings .tempalte_area .templet_line").find("input#" + key).val(content[key]);
            }
        }
    },
    allEvent: function() {
        $(".item_2").on("click", "li", function() {
            var text = $(this).attr("data-value");
            if (text == "vip") {
                $("#group_select_parent").hide();
                $("#sendee").show();
                $("#export_sendee").hide();
            }
            if (text == "vip_group") {
                $("#sendee").hide();
                $("#group_select_parent").show();
                $("#export_sendee").hide();
            }
            if (text == "input_file") {
                $("#sendee").hide();
                $("#group_select_parent").hide();
                $("#export_sendee").show();
            }
            if ($("#MESSAGE_TYPE").val() == "微信群发消息") {
                $("#WX").show();
                $("#WXTemplate").show();
                $("#templet_settings").show();
                $("#templet_settings").prev().hide();
            } else {
                $("#WX").hide();
                $("#WXTemplate").hide();
                $("#templet_settings").hide();
                $("#templet_settings").prev().show();
            }
        });
        $("#drop_down ul li").click(function() {
            if ($(this).html() == "微信群发消息") {
                getWxList($("#OWN_CORP").val());
                $("#WX").show();
                $("#WXTemplate").show();
                $("#templet_settings").show();
                $("#templet_settings").prev().hide();
            } else if ($(this).html() == "短信") {
                $("#WX").hide();
                $("#WXTemplate").hide();
                $("#templet_settings").hide();
                $("#templet_settings").prev().show();
            }
        });
        $("#WX_Template").click(function() {
            if ($("#WX_TYPE").attr("data-app_id") == undefined || $("#WX_TYPE").attr("data-app_id") == "") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请选择公众号"
                });
                $(this).next().hide();
                return
            }
            if ($(this).next().children().length == 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "暂无微信模板"
                });
                $(this).next().hide();
                return
            }
        })
    }
};
$(function() {
    message.init();
});
//获取公众号
function getWxList(corp_code) {
    var param = {};
    param["corp_code"] = corp_code;
    oc.postRequire("post", "/corp/selectWx", "0", param, function(data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var list = msg.list;
            var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-value="${msg}">${msg}</li>';
            var html = '';
            for (i = 0; i < list.length; i++) {
                var nowHTML = tempHTML;
                nowHTML = nowHTML.replace('${id}', list[i].app_id);
                nowHTML = nowHTML.replace('${usermane}', list[i].app_user_name);
                nowHTML = nowHTML.replace('${msg}', list[i].app_name);
                nowHTML = nowHTML.replace('${msg}', list[i].app_name);
                html += nowHTML;
            }
            $('#vipPublic').html(html);
            $('#vipPublic li').click(function() {
                var oldValue = $('#WX_TYPE').attr('data-app_id');
                $('#WX_TYPE').val($(this).text());
                if (oldValue != $(this).attr('data-id')) {
                    $('#WX_TYPE').attr('data-app_id', $(this).attr('data-id'));
                    $('#WX_TYPE').attr('app_user_name', $(this).attr('data-username'));
                    $('#WX_Template').val("");
                    $('#WX_Template').attr('data-template_id', "");
                    $('#WX_Template').attr('data-template_name', "");
                    $("#templet_settings .tempalte_area .templet_line input:not(#heading,#tem_link)").parent().remove();
                    getTemplates(corp_code);
                }
            });

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
//获取微信模板
function getTemplates(corp_code) {
    var param = {};
    var def = $.Deferred();
    param["corp_code"] = corp_code;
    param["app_id"] = $("#WX_TYPE").attr("data-app_id");
    oc.postRequire("post", "/wxTemplate/selectAllTemlates", "0", param, function(data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var list = msg.list;
            var html = '';
            for (var i = 0; i < list.length; i++) {
                html += '<li data-template_id="' + list[i].template_id + '" data-template_name="' + list[i].template_name + '" data-value=\'' + JSON.stringify(list[i].content_data) + '\'>' + list[i].template_name + '</li>';
            }
            $('#template').html(html);
            $('#template li').click(function() {
                var oldValue = $('#WX_Template').attr('data-template_id');
                $('#WX_Template').val($(this).text());
                $('#WX_Template').attr('data-template_id', $(this).attr('data-template_id'));
                $('#WX_Template').attr('data-template_name', $(this).attr('data-template_name'));
                if (oldValue != $(this).attr('data-template_id')) {
                    $("#templet_settings .tempalte_area .templet_line input:not(#heading,#tem_link)").parent().remove();
                    var data = JSON.parse($(this).attr("data-value"));
                    var div = '';
                    for (var t = 0; t < data.length; t++) {
                        if (data[t].key != "first" && data[t].key != "remark" && data[t].key != "url") {
                            div = '<div class="templet_line">' +
                                '<span class="templet_left">' + data[t].value + '</span>' +
                                '<input type="text" placeholder="请输入内容" id="' + data[t].key + '">' +
                                '</div>';
                            $("#remark").parent().before(div)
                        }
                    }
                }
            });
            def.resolve();
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
    return def
}

////获取品牌列表
//function getbrandlist(){
//	var corp_code = $('#OWN_CORP').val();
//	var searchValue=$("#brand_search").val();
//	var _param={};
//	_param["corp_code"]=corp_code;
//	_param["searchValue"]=searchValue;
//	whir.loading.add("",0.5);//加载等待框
//	$("#mask").css("z-index","10002");
//	oc.postRequire("post","/shop/brand", "",_param, function(data){
//		if (data.code == "0") {
//			var message=JSON.parse(data.message);
//            var list=message.brands;
//			var brand_html_left = '';
//			var brand_html_right='';
//			if (list.length == 0){
//				for(var h=0;h<9;h++){
//					brand_html_left+="<li></li>"
//				}
//			} else {
//				if(list.length<9){
//					for (var i = 0; i < list.length; i++) {
//					    brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
//	                        + i
//	                        + 1
//	                        + "'/><label for='checkboxThreeInput"
//	                        + i
//	                        + 1
//	                        + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
//					}
//					for(var j=0;j<9-list.length;j++){
//						brand_html_left+="<li></li>"
//					}
//				}else if(list.length>=9){
//					for (var i = 0; i < list.length; i++) {
//					    brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
//	                        + i
//	                        + 1
//	                        + "'/><label for='checkboxThreeInput"
//	                        + i
//	                        + 1
//	                        + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
//					}
//				}
//			}
//			$("#screen_brand .screen_content_l ul").append(brand_html_left);
//			var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
//			for(var k=0;k<li.length;k++){
//				$("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
//			}
//			whir.loading.remove();//移除加载框
//		} else if (data.code == "-1") {
//			art.dialog({
//				time: 1,
//				lock: true,
//				cancel: false,
//				content: data.message
//			});
//		}
//	})
//}
////获取分组
//function getGroup() {
//	var corp_command = "/vipGroup/getCorpGroupsAll";
//	var _param = {};
//	_param["corp_code"] = $('#OWN_CORP').val();
//	_param["search_value"] = $("#group_search").val().trim();
//	oc.postRequire("post", corp_command, "0", _param, function (data) {
//		if (data.code == "0") {
//			var message = JSON.parse(data.message);
//			var list = JSON.parse(message.list);
//			var html = "";
//			$("#screen_group .screen_content_l ul").empty();
//			$("#screen_group .s_pitch span").eq(1).text(list.total);
//			if (list.length > 0) {
//				for (var i = 0; i < list.length; i++) {
//					html+="<li><div class='checkbox1'><input  data-type='"+list[i].group_type+"' type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
//						+ i
//						+ "'/><label for='checkboxOneInput"
//						+ i
//						+ "'></label></div><span class='p16'>"+list[i].vip_group_name+"</span></li>"
//				}
//				$("#screen_group .screen_content_l ul").append(html);
//			} else if (list.length <= 0) {
//				art.dialog({
//					time: 1,
//					lock: true,
//					cancel: false,
//					content: data.message
//				});
//			}
//		}
//	})
//}
////拉取区域
//function getarealist(a){
//	var corp_code = $('#OWN_CORP').val();
//	var area_command = "/area/selAreaByCorpCode";
//	var searchValue=$("#area_search").val().trim();
//	var pageSize=20;
//	var pageNumber=a;
//	var _param = {};
//	_param["corp_code"] = corp_code;
//	_param["searchValue"]=searchValue;
//	_param["pageSize"]=pageSize;
//	_param["pageNumber"]=pageNumber;
//	whir.loading.add("",0.5);//加载等待框
//	$("#mask").css("z-index","10002");
//	oc.postRequire("post", area_command, "", _param, function(data) {
//		if (data.code == "0") {
//			var message=JSON.parse(data.message);
//            var list=JSON.parse(message.list);
//            var hasNextPage=list.hasNextPage;
//            var cout=list.pages;
//            var list=list.list;
//			var area_html_left ='';
//			var area_html_right='';
//			if (list.length == 0) {
//
//			} else {
//				if(list.length>0){
//					for (var i = 0; i < list.length; i++) {
//					    area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
//	                        + i
//	                        + a
//	                        + 1
//	                        + "'/><label for='checkboxOneInput"
//	                        + i
//	                        + a
//	                        + 1
//	                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
//					}
//				}
//			}
//			if(hasNextPage==true){
//				area_num++;
//				area_next=false;
//			}
//			if(hasNextPage==false){
//				area_next=true;
//			}
//			$("#screen_area .screen_content_l ul").append(area_html_left);
//			if(!isscroll){
//				$("#screen_area .screen_content_l").bind("scroll",function () {
//					var nScrollHight = $(this)[0].scrollHeight;
//				    var nScrollTop = $(this)[0].scrollTop;
//				    var nDivHight=$(this).height();
//				    if(nScrollTop + nDivHight >= nScrollHight){
//				    	if(area_next){
//				    		return;
//				    	}
//				    	getarealist(area_num);
//				    };
//				})
//			}
//			isscroll=true;
//			var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
//			for(var k=0;k<li.length;k++){
//				$("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
//			}
//			whir.loading.remove();//移除加载框
//		} else if (data.code == "-1") {
//			art.dialog({
//				time: 1,
//				lock: true,
//				cancel: false,
//				content: data.message
//			});
//		}
//	})
//}
////获取店铺列表
//function getstorelist(a){
//	var corp_code = $('#OWN_CORP').val();
//	var searchValue=$("#store_search").val();
//	var pageSize=20;
//	var pageNumber=a;
//	var _param={};
//	_param['corp_code']=corp_code;
//	_param['area_code']=message.cache.area_codes;
//	_param['brand_code']=message.cache.brand_codes;
//	_param['searchValue']=searchValue;
//	_param['pageNumber']=pageNumber;
//	_param['pageSize']=pageSize;
//	whir.loading.add("",0.5);//加载等待框
//	$("#mask").css("z-index","10002");
//	// oc.postRequire("post","/user/stores","", _param, function(data) {
//	oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
//	if (data.code == "0") {
//			var message=JSON.parse(data.message);
//            var list=JSON.parse(message.list);
//            var hasNextPage=list.hasNextPage;
//            var cout=list.pages;
//            var list=list.list;
//			var store_html = '';
//			if (list.length == 0){
//
//			} else {
//				if(list.length>0){
//					for (var i = 0; i < list.length; i++) {
//				    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
//                        + i
//                        + a
//                        + 1
//                        + "'/><label for='checkboxTowInput"
//                        + i
//                        + a
//                        + 1
//                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
//					}
//				}
//			}
//			if(hasNextPage==true){
//				shop_num++;
//				shop_next=false;
//			}
//			if(hasNextPage==false){
//				shop_next=true;
//			}
//			$("#screen_shop .screen_content_l ul").append(store_html);
//			if(!isscroll){
//				$("#screen_shop .screen_content_l").bind("scroll",function () {
//					var nScrollHight = $(this)[0].scrollHeight;
//				    var nScrollTop = $(this)[0].scrollTop;
//				    var nDivHight=$(this).height();
//				    if(nScrollTop + nDivHight >= nScrollHight){
//				    	if(shop_next){
//				    		return;
//				    	}
//				    	getstorelist(shop_num);
//				    };
//				})
//		    }
//		    isscroll=true;
//			var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
//			for(var k=0;k<li.length;k++){
//				$("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
//			}
//			whir.loading.remove();//移除加载框
//		} else if (data.code == "-1") {
//			art.dialog({
//				time: 1,
//				lock: true,
//				cancel: false,
//				content: data.message
//			});
//		}
//	})
//}
////获取员工列表
//function getstafflist(a){
//	var corp_code = $('#OWN_CORP').val();
//	var searchValue=$('#staff_search').val();
//    var pageSize=20;
//    var pageNumber=a;
//    var _param={};
//    _param["corp_code"]=corp_code;
//    _param['area_code']=message.cache.area_codes;
//    _param['brand_code']=message.cache.brand_codes;
//    _param['store_code']=message.cache.store_codes;
//    _param['searchValue']=searchValue;
//    _param['pageNumber']=pageNumber;
//    _param['pageSize']=pageSize;
//    whir.loading.add("",0.5);//加载等待框
//    $("#mask").css("z-index","10002");
//    oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
//        if (data.code == "0"){
//            var message=JSON.parse(data.message);
//            var list=JSON.parse(message.list);
//            var hasNextPage=list.hasNextPage;
//            var cout=list.pages;
//            var list=list.list;
//            var staff_html = '';
//            if (list.length == 0){
//
//            } else {
//                if(list.length>0){
//                    for (var i = 0; i < list.length; i++) {
//                    staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
//                        + i
//                        + a
//                        + 1
//                        + "'/><label for='checkboxFourInput"
//                        + i
//                        + a
//                        + 1
//                        + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
//                    }
//                }
//            }
//            if(hasNextPage==true){
//				staff_num++;
//                staff_next=false;
//			}
//			if(hasNextPage==false){
//				staff_next=true;
//			}
//			if(a==1 && searchValue==""){
//				$("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
//			}
//            $("#screen_staff .screen_content_l ul").append(staff_html);
//            if(!isscroll){
//				$("#screen_staff .screen_content_l").bind("scroll",function () {
//					var nScrollHight = $(this)[0].scrollHeight;
//				    var nScrollTop = $(this)[0].scrollTop;
//				    var nDivHight=$(this).height();
//				    if(nScrollTop + nDivHight >= nScrollHight){
//				    	if(staff_next){
//				    		return;
//				    	}
//				    	getstafflist(staff_num);
//				    }
//				})
//		    }
//		    isscroll=true;
//		    $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
//            $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
//            $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
//            $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
//			var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
//			for(var k=0;k<li.length;k++){
//				$("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
//			}
//            whir.loading.remove();//移除加载框
//        } else if (data.code == "-1") {
//            alert(data.message);
//        }
//    })
//}
////区域搜索
//$("#area_search").keydown(function(){
//    var event=window.event||arguments[0];
//    area_num=1;
//    if(event.keyCode == 13){
//    	isscroll=false;
//	    $("#screen_area .screen_content_l").unbind("scroll");
//    	$("#screen_area .screen_content_l ul").empty();
//        getarealist(area_num);
//    }
//});
////店铺搜索
//$("#store_search").keydown(function(){
//	var event=window.event||arguments[0];
//	shop_num=1;
//	if(event.keyCode==13){
//		isscroll=false;
//		$("#screen_shop .screen_content_l ul").unbind("scroll");
//		$("#screen_shop .screen_content_l ul").empty();
//		getstorelist(shop_num);
//	}
//});
////品牌搜索
//$("#brand_search").keydown(function(){
//	var event=window.event||arguments[0];
//	if(event.keyCode==13){
//		$("#screen_brand .screen_content_l ul").empty();
//		getbrandlist();
//	}
//});
////员工搜索
//$("#staff_search").keydown(function(){
//	var event=window.event||arguments[0];
//	staff_num=1;
//	if(event.keyCode==13){
//		isscroll=false;
//	    $("#screen_staff .screen_content_l").unbind("scroll");
//		$("#screen_staff .screen_content_l ul").empty();
//		getstafflist(staff_num);
//	}
//});
////店铺放大镜搜索
//$("#store_search_f").click(function(){
//	shop_num=1;
//	isscroll=false;
//	$("#screen_shop .screen_content_l").unbind("scroll");
//	$("#screen_shop .screen_content_l ul").empty();
//	getstorelist(shop_num);
//});
////区域放大镜收索
//$("#area_search_f").click(function(){
//	area_num=1;
//	isscroll=false;
//	$("#screen_area .screen_content_l").unbind("scroll");
//	$("#screen_area .screen_content_l ul").empty();
//	getarealist(area_num);
//});
////员工放大镜搜索
//$("#staff_search_f").click(function(){
//	staff_num=1;
//	isscroll=false;
//	$("#screen_staff .screen_content_l").unbind("scroll");
//    $("#screen_staff .screen_content_l ul").empty();
//	getstafflist(staff_num);
//});
////品牌放大镜收索
//$("#brand_search_f").click(function(){
//	$("#screen_brand .screen_content_l ul").empty();
//	getbrandlist();
//});
////区域关闭
//$("#screen_close_area").click(function(){
//	$("#screen_area").hide();
//    if(message.clickMark=="user"){
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//});
////员工关闭
//$("#screen_close_staff").click(function(){
//	$("#screen_staff").hide();
//	$("#screen_wrapper").show();
//});
////店铺关闭
//$("#screen_close_shop").click(function(){
//	$("#screen_shop").hide();
//    $("#screen_shop").hide();
//    if(message.clickMark=="user"){
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//});
////品牌关闭
//$("#screen_close_brand").click(function(){
//	$("#screen_brand").hide();
//    if(message.clickMark=="user"){
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//});
////弹框关闭
//$("#screen_wrapper_close").click(function(){
//    $("#screen_wrapper").hide();
//    $("#p").hide();
//});
////点击弹框的筛选按钮弹出区域框
//$("#screen_areal").click(function(){
//    message.clickMark="";
//	if(message.cache.area_codes!==""){
//		var area_codes=message.cache.area_codes.split(',');
//		var area_names=message.cache.area_names.split(',');
//		var area_html_right="";
//	    for(var h=0;h<area_codes.length;h++){
//			area_html_right+="<li id='"+area_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+area_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_area .s_pitch span").html(h);
//		$("#screen_area .screen_content_r ul").html(area_html_right);
//	}else{
//		$("#screen_area .s_pitch span").html("0");
//		$("#screen_area .screen_content_r ul").empty();
//	}
//	var area_num=1;
//	isscroll=false;
//    var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//    //$("#screen_area").css({"left":+left+"px","top":+tp+"px"});
//    $("#screen_area").show();
//    $("#screen_wrapper").hide();
//    $("#screen_area .screen_content_l").unbind("scroll");
//    $("#screen_area .screen_content_l ul").empty();
//    getarealist(area_num);
//});
////点击弹框的筛选按钮弹出品牌框
//$("#screen_brandl").click(function(){
//	if(message.cache.brand_codes!==""){
//        message.clickMark="";
//		var brand_codes=message.cache.brand_codes.split(',');
//		var brand_names=message.cache.brand_names.split(',');
//		var brand_html_right="";
//	    for(var h=0;h<brand_codes.length;h++){
//			brand_html_right+="<li id='"+brand_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+brand_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_brand .s_pitch span").html(h);
//		$("#screen_brand .screen_content_r ul").html(brand_html_right);
//	}else{
//		$("#screen_brand .s_pitch span").html("0");
//		$("#screen_brand .screen_content_r ul").empty();
//	}
//    var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//    //$("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
//    $("#screen_brand").show();
//    $("#screen_wrapper").hide();
//    $("#screen_brand .screen_content_l ul").empty();
//    getbrandlist();
//});
////点击弹框的筛选按钮弹出区域框
//$("#screen_shopl").click(function(){
//    message.clickMark="";
//	if(message.cache.store_codes!==""){
//		var store_codes=message.cache.store_codes.split(',');
//		var store_names=message.cache.store_names.split(',');
//		var shop_html_right="";
//	    for(var h=0;h<store_codes.length;h++){
//			shop_html_right+="<li id='"+store_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+store_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_shop .s_pitch span").html(h);
//		$("#screen_shop .screen_content_r ul").html(shop_html_right);
//	}else{
//		$("#screen_shop .s_pitch span").html("0");
//		$("#screen_shop .screen_content_r ul").empty();
//	}
//	var shop_num=1;
//	isscroll=false;
//    var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//    //$("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
//    $("#screen_shop").show();
//    $("#screen_wrapper").hide();
//    $("#screen_shop .screen_content_l").unbind("scroll");
//    $("#screen_shop .screen_content_l ul").empty();
//    getstorelist(shop_num);
//});
////点击弹框的导购按钮弹出导购框
//$(".screen_staffl").click(function(){
//    message.clickMark="";
//	if(message.cache.user_codes!==""){
//		var user_codes=message.cache.user_codes.split(',');
//		var user_names=message.cache.user_names.split(',');
//		var staff_html_right="";
//	    for(var h=0;h<user_codes.length;h++){
//			staff_html_right+="<li id='"+user_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+user_codes[h]+"'  data-storename='"+user_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+user_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_staff .s_pitch span").html(h);
//		$("#screen_staff .screen_content_r ul").html(staff_html_right);
//	}else{
//		$("#screen_staff .s_pitch span").html("0");
//		$("#screen_staff .screen_content_r ul").empty();
//	}
//	var staff_num=1;
//	isscroll=false;
//    $("#screen_staff").show();
//    $("#screen_wrapper").hide();
//    $("#screen_staff .screen_content_l").unbind("scroll");
//    $("#screen_staff .screen_content_l ul").empty();
//    getstafflist(staff_num);
//});
////店铺里面的区域点击
//$("#shop_area").click(function(){
//	if(message.cache.area_codes!==""){
//		var area_codes=message.cache.area_codes.split(',');
//		var area_names=message.cache.area_names.split(',');
//		var area_html_right="";
//	    for(var h=0;h<area_codes.length;h++){
//			area_html_right+="<li id='"+area_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+area_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_area .s_pitch span").html(h);
//		$("#screen_area .screen_content_r ul").html(area_html_right);
//	}else{
//		$("#screen_area .s_pitch span").html("0");
//		$("#screen_area .screen_content_r ul").empty();
//	}
//	isscroll=false;
//	area_num=1;
//	var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//	$("#screen_area .screen_content_l").unbind("scroll");
//	$("#screen_area .screen_content_l ul").empty();
//	//$("#screen_area").css({"left":+left+"px","top":+tp+"px"});
//	$("#screen_area").show();
//	$("#screen_shop").hide();
//	getarealist(area_num);
//});
////店铺里面的品牌点击
//$("#shop_brand").click(function(){
//	if(message.cache.brand_codes!==""){
//		var brand_codes=message.cache.brand_codes.split(',');
//		var brand_names=message.cache.brand_names.split(',');
//		var brand_html_right="";
//	    for(var h=0;h<brand_codes.length;h++){
//			brand_html_right+="<li id='"+brand_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+brand_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_brand .s_pitch span").html(h);
//		$("#screen_brand .screen_content_r ul").html(brand_html_right);
//	}else{
//		$("#screen_brand .s_pitch span").html("0");
//		$("#screen_brand .screen_content_r ul").empty();
//	}
//	var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//	$("#screen_brand .screen_content_l ul").empty();
//	//$("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
//	$("#screen_brand").show();
//	$("#screen_shop").hide();
//	getbrandlist();
//});
////员工里面的区域点击
//$("#staff_area").click(function(){
//    message.clickMark="user";
//	if(message.cache.area_codes!==""){
//		var area_codes=message.cache.area_codes.split(',');
//		var area_names=message.cache.area_names.split(',');
//		var area_html_right="";
//	    for(var h=0;h<area_codes.length;h++){
//			area_html_right+="<li id='"+area_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+area_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_area .s_pitch span").html(h);
//		$("#screen_area .screen_content_r ul").html(area_html_right);
//	}else{
//		$("#screen_area .s_pitch span").html("0");
//		$("#screen_area .screen_content_r ul").empty();
//	}
//	isscroll=false;
//	area_num=1;
//	var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//	$("#screen_area .screen_content_l").unbind("scroll");
//	$("#screen_area .screen_content_l ul").empty();
//	//$("#screen_area").css({"left":+left+"px","top":+tp+"px"});
//	$("#screen_area").show();
//	$("#screen_staff").hide();
//	getarealist(area_num);
//});
////员工里面的店铺点击
//$("#staff_shop").click(function(){
//    message.clickMark="user";
//	if(message.cache.store_codes!==""){
//		var store_codes=message.cache.store_codes.split(',');
//		var store_names=message.cache.store_names.split(',');
//		var shop_html_right="";
//	    for(var h=0;h<store_codes.length;h++){
//			shop_html_right+="<li id='"+store_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+store_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_shop .s_pitch span").html(h);
//		$("#screen_shop .screen_content_r ul").html(shop_html_right);
//	}else{
//		$("#screen_shop .s_pitch span").html("0");
//		$("#screen_shop .screen_content_r ul").empty();
//	}
//	isscroll=false;
//	shop_num=1;
//	var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//	$("#screen_shop .screen_content_l").unbind("scroll");
//	$("#screen_shop .screen_content_l ul").empty();
//	//$("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
//	$("#screen_shop").show();
//	$("#screen_staff").hide();
//	getstorelist(shop_num);
//});
////员工里面的品牌点击
//$("#staff_brand").click(function(){
//    message.clickMark="user";
//	if(message.cache.brand_codes!==""){
//		var brand_codes=message.cache.brand_codes.split(',');
//		var brand_names=message.cache.brand_names.split(',');
//		var brand_html_right="";
//	    for(var h=0;h<brand_codes.length;h++){
//			brand_html_right+="<li id='"+brand_codes[h]+"'>\
//			<div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
//			<label></div><span class='p16'>"+brand_names[h]+"</span>\
//			\</li>"
//		}
//		$("#screen_brand .s_pitch span").html(h);
//		$("#screen_brand .screen_content_r ul").html(brand_html_right);
//	}else{
//		$("#screen_brand .s_pitch span").html("0");
//		$("#screen_brand .screen_content_r ul").empty();
//	}
//	var arr=whir.loading.getPageSize();
//    //var left=(arr[0]-$("#screen_shop").width())/2;
//    //var tp=(arr[3]-$("#screen_shop").height())/2+50;
//	$("#screen_brand .screen_content_l ul").empty();
//	//$("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
//	$("#screen_brand").show();
//	$("#screen_staff").hide();
//	getbrandlist();
//});
////点击区域确定按钮
//$("#screen_que_area").click(function(){
//	var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
//    var area_codes="";
//    var area_names="";
//    for(var i=li.length-1;i>=0;i--){
//        var r=$(li[i]).attr("id");
//        var p=$(li[i]).find(".p16").html();
//        if(i>0){
//            area_codes+=r+",";
//            area_names+=p+",";
//        }else{
//            area_codes+=r;
//            area_names+=p;
//        }
//    }
//    message.cache.area_codes=area_codes;
//    message.cache.area_names=area_names;
//    $("#screen_area").hide();
//    if(message.clickMark=="user"){
//        area_num=1;
//        isscroll=false;
//        $("#screen_staff .screen_content_l").unbind("scroll");
//        $("#screen_staff .screen_content_l ul").empty();
//        getstafflist(area_num);
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//    $("#screen_area_num").val("已选"+li.length+"个");
//	$("#screen_area_num").attr("data-code",area_codes);
//    $("#screen_area_num").attr("data-name",area_names);
//    $(".area_num").val("已选"+li.length+"个");
//});
////点击品牌确定按钮
//$("#screen_que_brand").click(function(){
//	var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
//	var brand_codes="";
//	var brand_names="";
//	for(var i=li.length-1;i>=0;i--){
//        var r=$(li[i]).attr("id");
//        var p=$(li[i]).find(".p16").html();
//        if(i>0){
//            brand_codes+=r+",";
//            brand_names+=p+",";
//        }else{
//            brand_codes+=r;
//            brand_names+=p;
//        }
//    }
//    message.cache.brand_codes=brand_codes;
//    message.cache.brand_names=brand_names;
//    $("#screen_brand").hide();
//    if(message.clickMark=="user"){
//        staff_num=1;
//        isscroll=false;
//        $("#screen_staff .screen_content_l").unbind("scroll");
//        $("#screen_staff .screen_content_l ul").empty();
//        getstafflist(staff_num);
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//    $("#screen_brand_num").val("已选"+li.length+"个");
//    $(".brand_num").val("已选"+li.length+"个");
//	$("#screen_brand_num").attr("data-code",brand_codes);
//    $("#screen_brand_num").attr("data-name",brand_names);
//});
////点击店铺确定按钮
//$("#screen_que_shop").click(function(){
//	var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
//	var store_codes="";
//	var store_names="";
//	for(var i=li.length-1;i>=0;i--){
//        var r=$(li[i]).attr("id");
//        var p=$(li[i]).find(".p16").html();
//        if(i>0){
//            store_codes+=r+",";
//            store_names+=p+",";
//        }else{
//            store_codes+=r;
//            store_names+=p;
//        }
//    }
//    message.cache.store_codes=store_codes;
//    message.cache.store_names=store_names;
//    $("#screen_shop").hide();
//    if(message.clickMark=="user"){
//        shop_num=1;
//        isscroll=false;
//        $("#screen_staff .screen_content_l").unbind("scroll");
//        $("#screen_staff .screen_content_l ul").empty();
//        getstafflist(shop_num);
//        $("#screen_staff").show();
//    }else {
//        $("#screen_wrapper").show();
//    }
//    $("#screen_shop_num").val("已选"+li.length+"个");
//    $("#staff_shop_num").val("已选"+li.length+"个");
//	$("#screen_shop_num").attr("data-code",store_codes);
//    $("#screen_shop_num").attr("data-name",store_names);
//});
////点击员工确定按钮
//$("#screen_que_staff").click(function(){
//	var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
//	var user_codes="";
//	var user_names="";
//	for(var i=li.length-1;i>=0;i--){
//        var r=$(li[i]).attr("id");
//        var p=$(li[i]).find(".p16").html();
//        if(i>0){
//            user_codes+=r+",";
//            user_names+=p+",";
//        }else{
//            user_codes+=r;
//            user_names+=p;
//        }
//    }
//    message.cache.user_codes=user_codes;
//    message.cache.user_names=user_names;
//    $("#screen_staff").hide();
//    $("#screen_wrapper").show();
//    $(".screen_staff_num").val("已选"+li.length+"个");
//    $(".screen_staff_num").attr("data-code",user_codes);
//    $(".screen_staff_num").attr("data-name",user_names);
//});
/*************获取vip的接口***************/
var inx = 1; //默认是第一页
var pageNumber = 1; //删除默认第一页
var pageSize = 10; //默认传的每页多少行
var value = ""; //收索的关键词
var param = {}; //定义的对象
var _param = {}; //筛选定义的内容
var list = "";
var cout = "";
var filtrate = ""; //筛选的定义的值
$("#add_sendee").bind("click", function() {
    $("#page-wrapper").hide();
    $(".content").show();
    var corp_code = $("#OWN_CORP").val();
    if (corp_code !== message.cache.corp_code) {
        message.cache.corp_code = corp_code;
        $('#search').val("");
        $('.contion .input').val("");
        $('.screen_content_r ul').empty();
        $('.s_pitch span').html("0");
        $('.area_num').val("全部");
        $('.brand_num').val("全部");
        $("#staff_shop_num").val("全部");
        GET(inx, pageSize);
    }
});
$("#edit_sendee").bind("click", function() {
    $("#page-wrapper").hide();
    $(".content").show();
    var corp_code = $("#OWN_CORP").val();
    if (corp_code !== message.cache.corp_code) {
        message.cache.corp_code = corp_code;
        $('#search').val("");
        $('.contion .input').val("");
        $('.screen_content_r ul').empty();
        $('.s_pitch span').html("0");
        $('.area_num').val("全部");
        $('.brand_num').val("全部");
        $("#staff_shop_num").val("全部");
        getEditVip(inx, pageSize);
    }
});
$("#turnoff,#back_edit_vip_group").bind("click", function() {
    $("#page-wrapper").show();
    $(".content").hide();
});
//模仿select
$(function() {
    $("#page_row").click(function() {
        if ("block" == $("#liebiao").css("display")) {
            hideLi();
        } else {
            showLi();
        }
    });
    $("#liebiao li").each(function(i, v) {
        $(this).click(function() {
            inx = 1;
            pageSize = $(this).attr('id');
            if (sessionStorage.getItem("id") != null) {
                getEditVip(inx, pageSize);
            } else {
                if (value == "" && filtrate == "") {
                    inx = 1;
                    GET(inx, pageSize);
                } else if (value !== "") {
                    inx = 1;
                    param["pageSize"] = pageSize;
                    param["pageNumber"] = inx;
                    POST(inx, pageSize);
                } else if (filtrate !== "") {
                    inx = 1;
                    _param["pageNumber"] = inx;
                    _param["pageSize"] = pageSize;
                    filtrates(inx, pageSize);
                }
            }
            $("#page_row").val($(this).html());
            hideLi();
        });
    });
    $("#page_row").blur(function() {
        setTimeout(hideLi, 200);
    });
});

function showLi() {
    $("#liebiao").show();
}

function hideLi() {
    $("#liebiao").hide();
}

function setPage(container, count, pageindex, pageSize) {
    count == 0 ? count = 1 : '';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize = pageSize;
    var a = [];
    //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }

    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
        } else {
            a[a.length] = "<li><span>" + i + "</span></li>";
        }
    }
    //总页数小于10
    if (count <= 10) {
        for (var i = 1; i <= count; i++) {
            setPageList();
        }
    }
    //总页数大于10页
    else {
        if (pageindex <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        } else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        } else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx, pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}

function dian(a, b) { //点击分页的时候调什么接口
    if (sessionStorage.getItem("id") != null) {
        getEditVip(inx, pageSize);
    } else {
        if (value == "" && filtrate == "") {
            GET(a, b);
        } else if (value !== "") {
            param["pageNumber"] = a;
            param["pageSize"] = b;
            POST(a, b);
        } else if (filtrate !== "") {
            _param["pageNumber"] = a;
            _param["pageSize"] = b;
            filtrates(a, b);
        }
    }
}

function superaddition(data, num) { //页面加载循环
    $(".table p").remove();
    if (data.length == 0) {
        var len = $(".table thead tr th").length;
        var i;
        for (i = 0; i < 10; i++) {
            $(".table tbody").append("<tr></tr>");
            for (var j = 0; j < len; j++) {
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    if (data.length == 1 && num > 1) {
        pageNumber = num - 1;
    } else {
        pageNumber = num;
    }
    for (var i = 0; i < data.length; i++) {
        var wx = '';
        //判断是否有会员头像
        if (data[i].vip_avatar == '') {
            data[i].vip_avatar = '../img/head.png';
        }
        //性别
        if (data[i].sex == "F") {
            data[i].sex = "女"
        } else if (data[i].sex == "M") {
            data[i].sex = "男"
        }
        if (num >= 2) {
            var a = i + 1 + (num - 1) * pageSize;
        } else {
            var a = i + 1;
        }
        if (data[i].open_id) {
            wx = "<span class='icon-ishop_6-22'style='color:#8ec750'></span>";
        } else {
            wx = "<span class='icon-ishop_6-22'style='color:#cdcdcd'></span>";
        }
        $(".table tbody").append("<tr data-storeId='" + data[i].store_id + "' id='" + data[i].vip_id + "'><td style='text-align:left;padding-left:22px;'>" +
            a +
            "</td><td>" +
            data[i].vip_name +
            "</td><td>" +
            wx +
            "</td><td>" +
            data[i].sex +
            "</td><td>" +
            data[i].vip_phone +
            "</td><td>" +
            data[i].cardno +
            "</td><td>" +
            data[i].vip_card_type +
            "</td><td>" +
            data[i].user_name +
            "</td><td><span>" +
            data[i].store_name +
            "</span></td><td>" +
            data[i].message_date + "</td>" +
            "<td width='50px;' style='text-align: left;'><div class='checkbox1'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput" +
            i +
            1 +
            "'/><label for='checkboxTwoInput" +
            i +
            1 +
            "'></label></div>" +
            "</td></tr>");
    }
    //if(message.cache.vip_id!==""){
    //	var vip_id=message.cache.vip_id.split(",");
    //	for(var i=0;i<vip_id.length;i++){
    //		$("#"+vip_id[i]).find("input").attr("checked","true");
    //	}
    //}
    whir.loading.remove(); //移除加载框
    $(".th th:last-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
}

function superaddition_edit(data, num) { //页面加载循环
    $(".table p").remove();
    if (data.length == 0) {
        var len = $(".table thead tr th").length;
        var i;
        for (i = 0; i < 10; i++) {
            $(".table tbody").append("<tr></tr>");
            for (var j = 0; j < len; j++) {
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    if (data.length == 1 && num > 1) {
        pageNumber = num - 1;
    } else {
        pageNumber = num;
    }
    for (var i = 0; i < data.length; i++) {
        var wx = '';
        //判断是否有会员头像
        if (data[i].vip_avatar == '') {
            data[i].vip_avatar = '../img/head.png';
        }
        //性别
        if (data[i].sex == "F") {
            data[i].sex = "女"
        } else if (data[i].sex == "M") {
            data[i].sex = "男"
        }
        if (num >= 2) {
            var a = i + 1 + (num - 1) * pageSize;
        } else {
            var a = i + 1;
        }
        if (data[i].open_id) {
            wx = "<span class='icon-ishop_6-22'style='color:#8ec750'></span>";
        } else {
            wx = "<span class='icon-ishop_6-22'style='color:#cdcdcd'></span>";
        }
        $(".table tbody").append("<tr data-storeId='" + data[i].store_id + "' id='" + data[i].vip_id + "'><td style='text-align:left;padding-left:22px;'>" +
            a +
            "</td><td>" +
            data[i].vip_name +
            "</td><td>" +
            wx +
            "</td><td>" +
            data[i].sex +
            "</td><td>" +
            data[i].vip_phone +
            "</td><td>" +
            data[i].cardno +
            "</td><td>" +
            data[i].vip_card_type +
            "</td><td>" +
            data[i].user_name +
            "</td><td><span>" +
            data[i].store_name +
            "</span></td>" +
            "</tr>");
    }
    //if(message.cache.vip_id!==""){
    //	var vip_id=message.cache.vip_id.split(",");
    //	for(var i=0;i<vip_id.length;i++){
    //		$("#"+vip_id[i]).find("input").attr("checked","true");
    //	}
    //}
    whir.loading.remove(); //移除加载框
    $(".th th:first-child input").removeAttr("checked");
    sessionStorage.removeItem("return_jump");
}

function GET(a, b) {
    whir.loading.add("", 0.5); //加载等待框
    var param = {};
    var corp_code = $("#OWN_CORP").val();
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["corp_code"] = corp_code;
    param["field"] = "fsend";
    oc.postRequire("post", "/vipAnalysis/allVip", "", param, function(data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var messages = JSON.parse(data.message);
            var list = messages.all_vip_list;
            cout = messages.pages;
            allVipNum = messages.count;
            var pageNum = messages.pageNum;
            superaddition(list, pageNum);
            jumpBianse();
            filtrate = "";
            $('.contion .input').val("");
            message.cache.area_codes = "";
            message.cache.area_names = "";
            message.cache.brand_codes = "";
            message.cache.brand_names = "";
            message.cache.store_codes = "";
            message.cache.store_names = "";
            message.cache.user_codes = "";
            message.cache.user_names = "";
            message.cache.type = "";
            message.cache.count = "";
            setPage($("#foot-num")[0], cout, pageNum, b);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}

function getEditVip(a, b) {
    whir.loading.add("", 0.5); //加载等待框
    var param = {};
    var corp_code = $("#OWN_CORP").val();
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["corp_code"] = $("#OWN_CORP").val();
    param["sms_vips"] = sms_vips_edit;
    param["send_scope"] = $("#send_scope").attr("data-value"); //发送范围
    oc.postRequire("post", "/vipFsend/getSendVip", "", param, function(data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var messages = JSON.parse(data.message);
            messages = JSON.parse(messages.list);
            var list = messages.all_vip_list;
            cout = messages.pages;
            var pageNum = messages.pageNum;
            superaddition_edit(list, pageNum);
            jumpBianse();
            filtrate = "";
            $('.contion .input').val("");
            message.cache.area_codes = "";
            message.cache.area_names = "";
            message.cache.brand_codes = "";
            message.cache.brand_names = "";
            message.cache.store_codes = "";
            message.cache.store_names = "";
            message.cache.user_codes = "";
            message.cache.user_names = "";
            message.cache.type = "";
            message.cache.count = "";
            setPage($("#foot-num")[0], cout, pageNum, b);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
//鼠标按下时触发的收索
$("#search").keydown(function() {
    var event = window.event || arguments[0];
    if (event.keyCode == 13) {
        value = this.value.trim();
        if (value !== "") {
            inx = 1;
            param["searchValue"] = value;
            param["pageNumber"] = inx;
            param["pageSize"] = pageSize;
            POST(inx, pageSize);
        } else if (value == "") {
            GET(inx, pageSize);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function() {
    value = $("#search").val().replace(/\s+/g, "");
    if (value !== "") {
        inx = 1;
        param["searchValue"] = value;
        param["pageNumber"] = inx;
        param["pageSize"] = pageSize;
        POST(inx, pageSize);
    } else {
        GET(inx, pageSize);
    }
});
//搜索的请求函数
function POST(a, b) {
    var corp_code = $("#OWN_CORP").val();
    param["corp_code"] = corp_code;
    param["field"] = "fsend";

    whir.loading.add("", 0.5); //加载等待框
    oc.postRequire("post", "/vip/vipSearch", "0", param, function(data) {
        if (data.code == "0") {
            var messages = JSON.parse(data.message);
            var list = messages.all_vip_list;
            cout = messages.pages;
            allVipNum = messages.count;
            var pageNum = messages.pageNum;
            var actions = messages.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove(); //移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, pageNum);
                jumpBianse();
            }
            filtrate = "";
            $('.contion .input').val("");
            message.cache.area_codes = "";
            message.cache.area_names = "";
            message.cache.brand_codes = "";
            message.cache.brand_names = "";
            message.cache.store_codes = "";
            message.cache.store_names = "";
            message.cache.user_codes = "";
            message.cache.user_names = "";
            message.cache.type = "";
            message.cache.count = "";
            setPage($("#foot-num")[0], cout, pageNum, b);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//全选
function checkAll(name) {
    var el = $("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name)) {
            el[i].checked = true;
        }
    }
}
//取消全选
function clearAll(name) {
    var el = $("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name)) {
            el[i].checked = false;
        }
    }
}

function jumpBianse() {
    $(document).ready(function() { //隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    });
}
$("#table").on("click", "tbody tr", function() {
    var input = $(this).find("input")[0];
    var thinput = $("thead input")[0];
    $(this).toggleClass("tr");
    if (input.type == "checkbox" && input.name == "test" && input.checked == false) {
        input.checked = true;
        $(this).addClass("tr");
    } else if (input.type == "checkbox" && input.name == "test" && input.checked == true) {
        if (thinput.type == "checkbox" && input.name == "test" && input.checked == true) {
            thinput.checked = false;
        }
        input.checked = false;
        $(this).removeClass("tr");
    }
});
//点击保存
$("#save").click(function() {
    var vip_id = "";
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    for (var i = tr.length - 1, ID = ""; i >= 0; i--) {
        var r = $(tr[i]).attr("id");
        if (i > 0) {
            vip_id += r + ",";
        } else {
            vip_id += r;
        }
    }
    message.cache.vip_id = vip_id;
    message.cache.type = "2";
    $("#sendee_r").val("已选" + tr.length + "个");
    $("#page-wrapper").show();
    $(".content").hide();
    isAllVip = false;
});
//点击保存全部
$("#save_all").click(function() {
    //if(allVipNum>100000){
    //	art.dialog({
    //		time: 1,
    //		lock: true,
    //		cancel: false,
    //		content: "最多选择10万个会员"
    //	});
    //	return;
    //}
    isAllVip = true;
    message.cache.vip_id = "";
    message.cache.type = "";
    $("#sendee_r").val("已选" + allVipNum + "个");
    $("#page-wrapper").show();
    $(".content").hide();
});
//点击发送按钮
$("#send").click(function() {
    var param = {};
    var sms_vips = {};
    var corp_code = $("#OWN_CORP").val(); //企业编号
    var send_scope = isAllVip ? "vip_condition_new" : $("#send_scope").attr("data-value"); //发送范围
    var content = $("#message_content").val().trim();
    var send_time = $("#send_time").val().trim();
    sms_vips["type"] = message.cache.type;
    var send_type = "";
    if ($("#MESSAGE_TYPE").val() == "短信") {
        send_type = "sms";
    } else if ($("#MESSAGE_TYPE").val() == "微信群发消息") {
        send_type = "wxmass";
    }
    sms_vips["vips"] = message.cache.vip_id;
    param["sms_vips"] = sms_vips;
    param["vip_group_code"] = "";
    if (send_time == "") {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "发送时间不能为空"
        });
        return;
    }
    if (send_time != "") {
        if (Date.parse(send_time) < new Date().getTime()) {
            art.dialog({
                time: 2,
                lock: true,
                cancel: false,
                content: "发送时间不能小于当前时间"
            });
            return;
        }
    }
    if (send_scope == "vip_group") {
        var vip_group_code = $("#vip_group").val();
        if (vip_group_code == "" || vip_group_code == null) {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "会员分组不能为空"
            });
            return;
        }
        param["vip_group_code"] = vip_group_code;
        param["sms_vips"] = "";
    }
    if (send_scope == "input_file") {
        var data_id = $("#file").attr("data-id");
        if (!data_id || data_id == "") {
            art.dialog({
                time: 2,
                lock: true,
                cancel: false,
                content: "请导入会员文件"
            });
            return;
        } else {
            param["sms_vips"] = data_id;
        }
    }
    if (send_type == "") {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "发送类型不能为空"
        });
        return;
    }
    param["corp_code"] = corp_code;
    param["send_time"] = send_time;
    param["content"] = content;
    param["send_type"] = send_type;
    param["send_scope"] = send_scope;
    if (isAllVip) {
        param.sms_vips = screenData;
    }
    if (send_type == "") {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "发送类型不能为空"
        });
        return;
    }
    if ($("#WX").is(":visible") && $("#WX_TYPE").attr("data-app_id") == undefined) {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "公众号不能为空"
        });
        return
    } else if ($("#WX").is(":visible") && $("#WX_TYPE").attr("data-app_id") != "") {
        param["app_id"] = $("#WX_TYPE").attr("data-app_id");
    }
    if ($("#WXTemplate").is(":visible") && ($("#WX_Template").attr("data-template_id") == undefined || $("#WX_Template").attr("data-template_id") == "")) {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "微信模板不能为空"
        });
        return
    } else {
        param["template_name"] = $("#WX_Template").attr("data-template_name");
    }
    if ($("#templet_settings").is(":visible")) {
        if (!teplete_test()) {
            return
        }
        var temlateAll = $("#templet_settings .tempalte_area .templet_line input:not(#heading,#tem_link)").parent();
        var content = {
            "first": $("#heading").val().trim(),
            "remark": $("#remark").val().trim(),
            "url": $("#tem_link").val().trim()
        };
        temlateAll.map(function() {
            var key = $(this).find("input").attr("id");
            var value = $(this).find("input").val().trim();
            content[key] = value;
        });
        param.content = content;
    }
    if (content == "") {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "发送内容不能为空"
        });
        return;
    }
    getSendVipNum(param);
});

function send(param) {
    whir.loading.add("", 0.5); //加载等待框
    oc.postRequire("post", "/vipFsend/add", "", param, function(data) {
        if (data.code == "0") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "保存成功"
            });
            var id = data.message;
            sessionStorage.setItem("id", id);
            $(window.parent.document).find('#iframepage').attr("src", "/vip/message_edit.html");
        } else if (data.code == "-1") {
            $("#tk").hide();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove(); //移除加载框
    });
}

function getSendVipNum(Data) {
    var param = {
        corp_code: Data.corp_code,
        send_scope: Data.send_scope,
        sms_vips: Data.sms_vips,
        vip_group_code: Data.vip_group_code
    };
    whir.loading.add("", 0.5);
    oc.postRequire("post", "/vipFsend/getSendCount", "", param, function(data) {
        whir.loading.remove();
        if (data.code == 0) {
            var num = JSON.parse(data.message).count;
            if (num == 0) {
                art.dialog({
                    time: 1.5,
                    lock: true,
                    cancel: false,
                    content: "群发未选择会员"
                });
            } else {
                $("#send_vip_num").html(num);
                $("#tk").show();
                $("#sendOk").unbind("click").bind("click", function() {
                    send(Data)
                });
            }
        } else {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请求失败"
            });
        }
    })
}

function teplete_test() {
    var reg = /^((https|http)?:\/\/)[^\s]+/;
    var isAll = true;
    $(".templet_left+input").not('#tem_link,#heading').map(function() { if ($(this).val().trim() == "") { isAll = false } });
    if (!isAll) {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "请完善模板配置"
        });
        return false;
    }
    if ($("#tem_link").val().trim() != "" && !reg.test($("#tem_link").val().trim())) {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "请输入有效的链接地址"
        });
        return false;
    }
    return true
}
//筛选调接口
function filtrates(a, b) {
    _param["field"] = "fsend";
    _param["send_scope"] = $("#send_scope").attr("data-value"); //发送范围
    whir.loading.add("", 0.5); //加载等待框
    oc.postRequire("post", "/vip/newScreen", "", _param, function(data) {
        if (data.code == "0") {
            var messages = JSON.parse(data.message);
            var list = messages.all_vip_list;
            screenData = _param.screen;
            cout = messages.pages;
            allVipNum = messages.count;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                whir.loading.remove(); //移除加载框
            } else if (list.length > 0) {
                message.cache.count = messages.count;
                $(".table p").remove();
                superaddition(list, a);
                jumpBianse();
            }
            setPage($("#foot-num")[0], cout, a, b);
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "筛选失败"
            });
            whir.loading.remove()
        }
    })
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function() {
    var event = window.event || arguments[0];
    var inx = this.value.replace(/[^0-9]/g, '');
    var inx = parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (sessionStorage.getItem("id") != null) {
                getEditVip(inx, pageSize);
            } else {
                if (value == "" && filtrate == "") {
                    GET(inx, pageSize);
                } else if (value !== "") {
                    param["pageSize"] = pageSize;
                    param["pageNumber"] = inx;
                    POST(inx, pageSize);
                } else if (filtrate !== "") {
                    _param["pageSize"] = pageSize;
                    _param["pageNumber"] = inx;
                    filtrates(inx, pageSize);
                }
            }
        }
    }
});
//关闭按钮回到列表页
$("#send_close").click(function() {
    sessionStorage.removeItem("group_vip");
    $(window.parent.document).find('#iframepage').attr("src", "/vip/message.html?t=" + $.now());
});
$('#back_message,#back_vip_group_1').click(function() {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/message.html?t=" + $.now());
});
$("#cancel,#X").click(function() {
    $("#tk").hide();
});
laydate({
    elem: '#send_time',
    format: 'YYYY-MM-DD hh:mm:ss', // 分隔符可以任意定义，该例子表示只显示年月
    festival: false, //显示节日
    istime: true,
    min: laydate.now(), //设定最小日期为当前日期
    choose: function(datas) { //选择日期完毕的回调
        //            alert('得到：'+datas);
    }
});
$("#empty_filter").click(function() {
    clearSelectList();
})

function clearSelectList() {
    $(".tab_activity").eq(0).addClass("active").siblings().remove();
    $(".tab_task").eq(0).addClass("active").siblings().remove();
    $(".tab_coupon").eq(0).addClass("active").siblings().remove();

    $(".activity_line").eq(0).siblings().remove();
    $(".activity_line").show();
    $(".activity_line").find(".filter_activity").val("全部").attr("data-value", "");
    //$(".activity_line").find(".filter_activity_ul").html("");

    $(".task_line").eq(0).siblings().remove();
    $(".task_line").show();
    $(".task_line").find(".filter_task").val("全部").attr("data-value", "");
    //$(".task_line").find(".filter_task_ul").html("");

    $(".coupon_line").eq(0).siblings().remove();
    $(".coupon_line").show();
    $(".coupon_line input").val("");
    $(".coupon_line").find(".filter_coupon").val("全部").attr("data-value", "");
    //$(".coupon_line").find(".filter_coupon_ul").html("");

    $(".participate").val("全部");
}


$(".oper_btn").on("click", "#auditParse", function() {
    var id = sessionStorage.getItem("id");
    var params = {};
    params["id"] = id;
    whir.loading.add("", "0.5");
    oc.postRequire("post", "/vipFsend/checkFsend", "0", params, function(data) {
        whir.loading.remove();
        if (data.code == "0") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "审核成功"
            });
            setTimeout(function() {
                window.location.reload();
            }, 1500)
        } else {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
});

$("#file").on('change', function() { //导入接收会员文件
    var excelName = $("#file").val();
    var fileTArr = excelName.split(".");
    //切割出后缀文件名
    var filetype = fileTArr[fileTArr.length - 1];
    if (filetype !== 'xls') {
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "只能上传后缀为'xls'的Excel文件"
        });
        $("#file").val("");
        return;
    }
    uploadFile();
});

function uploadFile() {
    whir.loading.add("", 0.5); //加载等待框
    var fileObj = document.getElementById("file").files[0];
    var FileController = "/vipActivity/excludeMultipart"; //接收上传文件的后台地址
    var form = new FormData();
    form.append("file", fileObj); // 文件对象
    form.append("type", "vip_send"); // 文件对象
    form.append("corp_code", $("#OWN_CORP").val()); // 文件对象
    // XMLHttpRequest 对象
    var xhr = null;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else {
        xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }
    xhr.onreadystatechange = function() {
        $("#leading-in").hide();
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                doResult(xhr.responseText);
            } else {
                $('#file').val("");
            }
        }
    }

    function doResult(data) {
        var data = JSON.parse(data);
        whir.loading.remove();
        if (data.code == "0") {
            var msg = JSON.parse(data.message)
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "导入成功"
            });
            $("#file").attr("data-id", msg.file_id);
            $("#export_tip").html("导入会员数:" + msg.cardno_num)
            $("#file").attr("title", "已导入会员列表")
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            $("#file").attr("data-id", "");
            $("#export_tip").html("导入失败:" + data.message)
        }
        $("#export_tip").show();
        $('#file').val("");
    }
    xhr.open("post", FileController, true);
    xhr.send(form);
    $("#p").hide();
    $(".into_frame").hide();
}

$("#exprot_vips").click(function() { //导出接收会员文件
    var data_id = $("#file").attr("data-id");
    if (!data_id || data_id == "") {
        art.dialog({
            time: 2,
            lock: true,
            cancel: false,
            content: "暂无可导出的文件"
        });
    } else {
        export_vips(data_id)
    }
})

function export_vips(id) {
    var param = {
        id: id,
        type: "vip_send"
    }
    whir.loading.add("", 0.5); //加载等待框
    oc.postRequire("post", "/vipActivity/fileOutExecl", "", param, function(data) {
        whir.loading.remove(); //移除等待框
        if (data.code == 0) {
            var msg = JSON.parse(data.message);
            var path = msg.path;
            $("#enter a").attr("href", path)
            $("body").append("<div id='download_mask'></div>")
            $("#code_ma").show();
        } else {
            art.dialog({
                time: 2,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}

$("#code_q").click(function() { //关闭导出
    $("#p").hide();
    $("#download_mask").remove();
    $("#code_ma").hide();
})
$("#dao").click(function() {
    $("#p").hide();
    $("#code_ma").hide();
    $("#download_mask").remove();
})