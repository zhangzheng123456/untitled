var oc = new ObjectControl();
var pageNumber = 1;//删除默认第一页
var pageSize = 10;//默认传的每页多少行
var value = "";//收索的关键词
var role = '';//识别页面
var _params = {};
var group_cheked = [];
var corp_code = '';//企业编号
var group_code = '';//分组编号
var group_count = '';
var user_nextpage = false;//导购拉下一页
var user_num=1;
var param = {};//搜索参数
$(function () {
    window.vip.init();
    if ($(".pre_title label").text() == "编辑会员分组") {
        var id = sessionStorage.getItem("id");
        var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        var funcCode = key_val.func_code;
        whir.loading.add("", 0.5);
        $.get("/detail?funcCode=" + funcCode + "", function (data) {
            var data = JSON.parse(data);
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var action = message.actions;
                if (action.length <= 0) {
                    $("#edit_save").remove();
                }
            }
        });
        _params = {};
        _params["id"] = id;
        var _command = "/vipGroup/select";
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
                var msg = JSON.parse(data.message);
                console.log(msg);
                $('#group_recode').val('共' + msg.vip_count + "个会员");
                $("#vip_id").val(msg.vip_group_name);
                $("#vip_id").attr("data-name", msg.vip_group_name);
                $("#vip_num").val(msg.vip_group_code);
                $("#vip_num").attr("data-name", msg.vip_group_code);
                $("#vip_remark").val(msg.remark);
                $("#OWN_CORP option").val(msg.corp.corp_code);
                $("#PARAM_NAME").attr("data-code",msg.user_code);
                $("#PARAM_NAME").val(msg.user_name);
                var Code = msg.corp.corp_code;
                $("#OWN_CORP option").text(msg.corp.corp_name);
                //$("#area_shop").val("共"+msg.store_count+"家店铺");
                // $("#OWN_CORP").val(msg.corp_code);
                $("#created_time").val(msg.created_date);
                $("#creator").val(msg.creater);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                var input = $(".checkbox_isactive").find("input")[0];
                if (msg.isactive == "Y") {
                    input.checked = true;
                } else if (msg.isactive == "N") {
                    input.checked = false;
                }
                getcorplist(Code);
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    } else {
        getcorplist();
    }
    //分配导购
    // $("#PARAM_NAME").click(function () {
    //     if ($("#allUser").css("display") == "none") {
    //         $("#allUser").show();
    //     } else {
    //         $("#allUser").hide();
    //     }
    // });
    // $(document).click(function (e) {
    //     if($(e.target).is("#search_user")||$(e.target).is("#PARAM_NAME")){
    //         return;
    //     }else {
    //         $("#allUser").hide();
    //     }
    // });
    //搜索导购
    // $("#search_user").keydown(function (e) {
    //     user_num = 1;
    //     var event=window.event||arguments[0]
    //     if(event.keyCode == 13){
    //         $("#allUser_list").empty();
    //         getuserlist();
    //     }
    // })
    //绑定导购点击事件
    // $("#allUser_list").on("click", "li", function (e) {
    //     e.stopPropagation();
    //     $("#PARAM_NAME").val($(this).text());
    //     $("#PARAM_NAME").attr("data-code", $(this).attr("id"));
    //     $("#allUser").hide();
    //     $(this).addClass("groupUser_checked");
    //     $(this).siblings("li").removeClass("groupUser_checked");
    // })
    //绑定导购滚动事件
    // $("#allUser_list").scroll(function () {
    //     var nScrollHight = $(this)[0].scrollHeight;
    //     var nScrollTop = $(this)[0].scrollTop;
    //     var nDivHight = $(this).height();
    //     if (nScrollTop + nDivHight >= nScrollHight) {
    //         if (user_nextpage==false) {
    //             return;
    //         }else {
    //             getuserlist();
    //         }
    //     }
    // })
    //滚动条样式
    // $("#allUser_list").niceScroll({cursorborder:"0 none",cursorcolor:"rgba(0,0,0,0)",cursoropacitymin:"0",boxzoom:false});
});
(function (root, factory) {
    root.vip = factory();
}(this, function () {
    var vipjs = {};
    vipjs.isEmpty = function (obj) {
        if (obj.trim() == "" || obj.trim() == undefined) {
            return true;
        } else {
            return false;
        }
    };
    vipjs.checkEmpty = function (obj, hint) {
        if (!this.isEmpty(obj)) {
            this.hiddenHint(hint);
            return true;
        } else {
            this.displayHint(hint);
            return false;
        }
    };
    vipjs.hiddenHint = function (hint) {
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    };
    vipjs.displayHint = function (hint, content) {
        hint.addClass("error_tips");
        if (!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    };
    vipjs.firstStep = function () {
        var inputText = jQuery(".conpany_msg").find(":text");
        for (var i = 0, length = inputText.length - 1; i < length; i++) {
            if (!bindFun(inputText[i]))return false;
        }
        return true;
    };
    vipjs.bindbutton = function () {
        $(".areaadd_oper_btn ul li:nth-of-type(1)").click(function () {
            var name = $("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            var num = $("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N" || num == "N") {
                    if (name == "N") {
                        var div = $("#vip_id").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    if (num == "N") {
                        var div = $("#vip_num").next('.hint').children();
                        div.html("该编号已经存在！");
                        div.addClass("error_tips");
                    }
                    return;
                }
                var vip_id = $("#vip_id").val();
                var vip_num = $("#vip_num").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var vip_remark = $("#vip_remark").val();
                var user_code = $("#PARAM_NAME").attr("data-code");
                var ISACTIVE = "";
                if(user_code==undefined){
                    user_code = "";
                }
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/vipGroup/add";//接口名
                var opt = {//返回成功后的操作
                    success: function () {
                    }
                };
                var _params = {
                    "vip_group_code": vip_num,
                    "vip_group_name": vip_id,
                    "corp_code": OWN_CORP,
                    "user_code": user_code,
                    "remark": vip_remark,
                    "isactive": ISACTIVE
                };
                vipjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
        $("#edit_save").click(function () {
            var name = $("#vip_id").attr("data-mark");//区域名称是否唯一的标志
            var num = $("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N") {
                    var div = $("#vip_id").next('.hint').children();
                    div.html("该名称已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                if (num == "N") {
                    var div = $("#vip_num").next('.hint').children();
                    div.html("该编号已经存在！");
                    div.addClass("error_tips");
                    return;
                }
                var ID = sessionStorage.getItem("id");
                var vip_id = $("#vip_id").val();
                var vip_num = $("#vip_num").val();
                var OWN_CORP = $("#OWN_CORP").val();
                var vip_remark = $("#vip_remark").val();
                var user_code = $("#PARAM_NAME").attr("data-code");
                var ISACTIVE = "";
                var input = $(".checkbox_isactive").find("input")[0];
                if (input.checked == true) {
                    ISACTIVE = "Y";
                } else if (input.checked == false) {
                    ISACTIVE = "N";
                }
                var _command = "/vipGroup/edit";//接口名
                var opt = {//返回成功后的操作
                    success: function () {

                    }
                };
                var _params = {
                    "id": ID,
                    "vip_group_code": vip_num,
                    "vip_group_name": vip_id,
                    "corp_code": OWN_CORP,
                    "user_code": user_code,
                    "remark": vip_remark,
                    // 'choose':sessionStorage.getItem('vip_choose'),
                    // 'quit':sessionStorage.getItem('vip_quit'),
                    "isactive": ISACTIVE
                };
                vipjs.ajaxSubmit(_command, _params, opt);
            } else {
                return;
            }
        });
    };
    vipjs.ajaxSubmit = function (_command, _params, opt) {
        whir.loading.add("", 0.5);
        oc.postRequire("post", _command, "", _params, function (data) {
            if (data.code == "0") {
               if(_command=="/vipGroup/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group_edit.html");
                }
                if(_command=="/vipGroup/edit"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"保存成功"
                    });
                }
                // $(window.parent.document).find('#iframepage').attr("src","/vip/vip_group.html");
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();
        });
    };
    var bindFun = function (obj1) {//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if (obj1) {
            _this = jQuery(obj1);
        } else {
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if (vipjs['check' + command]) {
            if (!vipjs['check' + command].apply(vipjs, [obj, hint])) {
                return false;
            }
        }
        return true;
    };
    jQuery(":text").focus(function () {
        var _this = this;
        interval = setInterval(function () {
            bindFun(_this);
        }, 500);
    }).blur(function (event) {
        clearInterval(interval);
    });
    var init = function () {
        vipjs.bindbutton();
    };
    var obj = {};
    obj.vipjs = vipjs;
    obj.init = init;
    return obj;
}));
function getcorplist(C) {
    //获取所属企业列表
    var userNum = 1;//分配导购分页，默认第一页; var userNum = 1;//分配导购分页，默认第一页;
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        //console.log(data);
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            //console.log(msg);
            var index = 0;
            var corp_html = '';
            var c = null;
            for (index in msg.corps) {
                c = msg.corps[index];
                if (c.corp_code == C) {
                    corp_html += '<option value="' + c.corp_code + '" selected>' + c.corp_name + '</option>';
                } else {
                    corp_html += '<option value="' + c.corp_code + '">' + c.corp_name + '</option>';
                }
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function () {
                user_num = 1;
                $("#vip_id").val("");
                $("#vip_num").val("");
                $("#vip_id").attr("data-mark", "");
                $("#vip_num").attr("data-mark", "");
                // $("#PARAM_NAME").val("");
                // $("#PARAM_NAME").attr("data-code", "");
                // $("#allUser_list").empty();
                // getuserlist();
                //console.log($("#OWN_CORP").val())
            });
            // getuserlist();
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
//获取导购列表
// function getuserlist() {
//     var corp_code = $("#OWN_CORP").val();
//     var area_code = "";
//     var brand_code = "";
//     var store_code = "";
//     var searchValue = $("#search_user").val().trim();
//     var pageSize = 20;
//     var pageNumber = user_num;
//     var _param = {};
//     _param["corp_code"] = corp_code;
//     _param['area_code'] = area_code;
//     _param['brand_code'] = brand_code;
//     _param['store_code'] = store_code;
//     _param['searchValue'] = searchValue;
//     _param['pageNumber'] = pageNumber;
//     _param['pageSize'] = pageSize;
//     oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
//         if (data.code == "0") {
//             var msg = JSON.parse(data.message);
//                 msg = JSON.parse(msg.list);
//             var list = msg.list;
//             if(msg.hasNextPage==true){
//                 user_nextpage=true;
//                 user_num++;
//             }else {
//                 user_nextpage=false;
//             }
//             for (var i = 0; i < list.length; i++) {
//                 if(list[i].user_code == $("#PARAM_NAME").attr("data-code")){
//                     $("#allUser_list").append("<li class='groupUser_checked' id='" + list[i].user_code + "'>" + list[i].user_name + "</li>");
//                 }else {
//                     $("#allUser_list").append("<li id='" + list[i].user_code + "'>" + list[i].user_name + "</li>");
//                 }
//             }
//         } else if (data.code == "-1") {
//             console.log(data.message);
//         }
//     })
// }
//验证编号是不是唯一
$("#vip_num").blur(function () {
    // var isCode=/^[A]{1}[0-9]{4}$/;
    var _params = {};
    var vip_group_code = $(this).val();
    var vip_group_code1 = $(this).attr("data-name");
    var corp_code = $("#OWN_CORP").val();
    if (vip_group_code !== "" && vip_group_code !== vip_group_code1) {
        _params["vip_group_code"] = vip_group_code;
        _params["corp_code"] = corp_code;
        var div = $(this).next('.hint').children();
        oc.postRequire("post", "/vipGroup/vipGroupCodeExist", "", _params, function (data) {
            if (data.code == "0") {
                div.html("");
                $("#vip_num").attr("data-mark", "Y");
            } else if (data.code == "-1") {
                $("#vip_num").attr("data-mark", "N");
                div.addClass("error_tips");
                div.html("该编号已经存在！");
            }
        })
    }
});
//验证名称是否唯一
// $("#vip_id").blur(function () {
//     var corp_code = $("#OWN_CORP").val();
//     var vip_id = $("#vip_id").val();
//     var vip_id1 = $("#vip_id").attr("data-name");
//     var div = $(this).next('.hint').children();
//     if (vip_id !== "" && vip_id !== vip_id1) {
//         var _params = {};
//         _params["vip_group_name"] = vip_id;
//         _params["corp_code"] = corp_code;
//         oc.postRequire("post", "/vipGroup/vipGroupNameExist", "", _params, function (data) {
//             if (data.code == "0") {
//                 div.html("");
//                 $("#vip_id").attr("data-mark", "Y");
//             } else if (data.code == "-1") {
//                 div.html("该名称已经存在！");
//                 div.addClass("error_tips");
//                 $("#vip_id").attr("data-mark", "N");
//             }
//         })
//     }
// });
$(".areaadd_oper_btn ul li:nth-of-type(2)").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
$("#edit_close").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
$("#back_vip_group").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
$("#back_vip_group_1").click(function () {
    $(window.parent.document).find('#iframepage').attr("src", "/vip/vip_group.html");
});
/***************************************************************************************/
//新增分组会员
//绑定单击事件
//新增
$('#screen_add').on('click', function () {
    //如果是新增就退出，不调用接口
    if($(this).hasClass('vip_group_add'))return;
    var a = $('#vip_num').val();
    var b = $('#vip_id').val();
    if ($('#vip_num').val() == '') {
        $('#vip_num').next().find('div').addClass('error_tips').html('分组编号不能为空！');
        return
    }
    if ($('#vip_id').val() == '') {
        $('#vip_id').next().find('div').addClass('error_tips').html('分组名称不能为空！');
        return
    }
    var str = $('#screen_add').text().trim();
    str == '分配会员' && ( role = 'eidtor');
    group_code = $("#vip_num").val();
    corp_code = $('#OWN_CORP').val();
    $(this).attr('data_role') ? role = $(this).attr('data_role') : '';
    GET(1, 10, group_code);//第三个参数  分组编号
    $('#page-wrapper')[0].style.display = 'none';
    $('.content')[0].style.display = 'block';
    $('#group_id').val(a);
    $('#group_name').val(b);
});
//进入页面后保存操作
$("#save").click(function () {
    var param = {};
    var quit = [];//传参数
    var choose = [];//传参数
    var quit_obj = [];//存放放弃的对象
    var get_group_sub = [];//保存已选的参数
    //获取所有选择项
    var get_groups = $("#table tbody input:checked").parent().parent().parent();
    group_count = get_groups.length;
    //遍历现选中的
    for (var i = 0; i < get_groups.length; i++) {
        // console.log($(get_groups[i]).find('[data_vip_id]').attr('data_vip_id'));
        get_group_sub.push($(get_groups[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    //遍历已选择的与现选中的做对比
    console.log(group_cheked);
    for (var r = 0; r < group_cheked.length; r++) {
        //返回-1  就是没找到要将这个号保存到quit中
        if (get_group_sub.indexOf(group_cheked[r]) == -1) {
            var selector = "#table tbody td[data_vip_id='" + group_cheked[r] + "']";
            quit_obj.push($(selector).parent());
        };
    }
    console.log(quit_obj);
    for (var i = 0; i < quit_obj.length; i++) {
        // var quit_sub = {}
        // quit_sub['vip_id'] = $(quit_obj[i]).find('[data_vip_id]').attr('data_vip_id');
        // quit_sub['corp_code'] = $(quit_obj[i]).attr('id');
        // quit_sub['card_no'] = $(quit_obj[i]).find('[data_cardno]').attr('data_cardno');
        // quit_sub['phone'] = $(quit_obj[i]).find('td:nth-child(4)').html();
        // quit.push(quit_sub)
        quit.push($(quit_obj[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    for (var i = 0; i < get_groups.length; i++) {
        // var choose_sub = {}
        // choose_sub['vip_id'] = $(get_groups[i]).find('[data_vip_id]').attr('data_vip_id');
        // choose_sub['corp_code'] = $(get_groups[i]).attr('id');
        // choose_sub['card_no'] = $(get_groups[i]).find('[data_cardno]').attr('data_cardno');
        // choose_sub['phone'] = $(get_groups[i]).find('td:nth-child(4)').html();
        // choose.push(choose_sub);
        choose.push($(get_groups[i]).find('[data_vip_id]').attr('data_vip_id'));
    }
    // sessionStorage.setItem('vip_choose',choose);
    // sessionStorage.setItem('vip_quit',quit);
    param['vip_group_id'] =sessionStorage.getItem("id");
    param['choose'] = choose.toString();
    param['quit'] = quit.toString();
    console.log(param);
    //发起异步请求
    oc.postRequire("post", '/vipGroup/saveVips', "", param, function (data) {
        if (data.code == 0) {
            console.log(role);
            if (role == 'eidtor') {
                window.location.reload()
            } else {
                $('#group_recode').val('共' + group_count + "个会员");
                $('#page-wrapper')[0].style.display = 'block';
                $('.content')[0].style.display = 'none';
            }
        } else if (data.code == -1) {
            alert(data.message);
        }
    });
    //关闭当前页面
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//进入页面的关闭操作
$('#turnoff').on('click', function () {
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//返回编辑会员分组
$('#back_edit_vip_group').on('click', function () {
    $('#page-wrapper')[0].style.display = 'block';
    $('.content')[0].style.display = 'none';
});
//打开筛选界面
$('#filtrate').on('click', function () {
    $('#screen_wrapper')[0].style.display = 'block';
});
//关闭筛选界面
$('#screen_wrapper_close').on('click', function () {
    $('#screen_wrapper')[0].style.display = 'none';
});
//请求页面数据
function GET(a, b, c) {
    whir.loading.add("", 0.5);//加载等待框
    var user_code =$("#PARAM_NAME").attr("data-code");
    var vip_group_code = $("#vip_num").val();
    if(user_code == undefined){
        user_code = "";
    }
    var param = {};
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["corp_code"] = corp_code;
    // param["user_code"] = user_code;
    param["vip_group_code"] =vip_group_code;
    oc.postRequire("post", "/vipGroup/allVip ", "", param, function (data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            //var list=list.list;
            superaddition(list, a, c);
            jumpBianse();
            filtrate = "";
            setPage($("#foot-num")[0], cout, a, b, c);
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
function jumpBianse() {
    $(document).ready(function () {//隔行变色
        $(".table tbody tr:odd").css("backgroundColor", "#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor", "#f4f4f4");
    })
}
function superaddition(data, num, c) {
    console.log(data);
    group_cheked = [];
    var judge = '';
    for (var i = 0; i < data.length; i++) {
        if ( data[i].is_this_group == "Y") {
            judge = 'checked'
        } else {
            judge = '';
        }
        if (num >= 2) {
            var a = i + num * pageSize;
        } else {
            var a = i + 1;
        }
        var gender = data[i].sex == 'F' ? '女' : '男';
        var tr_vip_id = data[i].vip_id;
        var tr_node = "<tr data-storeid='" + data[i].store_id + "' id='" + data[i].corp_code + "'>"
            + "</td><td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td data_vip_id='" + tr_vip_id + "'>"
            + data[i].vip_name
            + "</td><td>"
            + gender
            + "</td><td>"
            + data[i].vip_phone
            + "</td><td data_cardno='" + data[i].cardno + "'>"
            + data[i].vip_card_type
            // + "</td><td>"
            // + data[i].vip_group_name
            + "</td><td>"
            + data[i].user_name
            + "</td><td>"
            + data[i].store_name
            + "</td><td width='50px;' style='text-align: left;'><div class='checkbox1' id='" + data[i].id + "'><input " + judge + " type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div></td></tr>";
        $(".table tbody").append(tr_node);
        if (judge) {
            group_cheked.push(tr_vip_id);
        }
    }
};
//生成分页
function setPage(container, count, pageindex, pageSize, c) {
    count==0?count=1:'';
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
        }
        else { //当前页在中间部分
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
    var pageClick = function () {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function () { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx, pageSize, c);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize, c);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize, c);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
//点击页码
function dian(a, b, c) {//点击分页的时候调什么接口
    console.log(c)
    if (value == "" && filtrate == "") {
        GET(a, b, c);
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
//全选
function checkAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        console.log(el[i].name);
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = true;
        }
    }
};

//取消全选
function clearAll(name) {
    console.log(name);
    var el = $("tbody input[name='" + name + "']");
    var len = el.length;
    for (var i = 0; i < len; i++) {
        if ((el[i].type == "checkbox") && (el[i].name == name));
        {
            el[i].checked = false;
        }
    }
};
//模仿select
$(function () {
        $("#page_row").click(function () {
            if ("block" == $("#liebiao").css("display")) {
                hideLi();
            } else {
                showLi();
            }
        });
        $("#liebiao li").each(function (i, v) {
            $(this).click(function () {
                pageSize = $(this).attr('id');
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
                $("#page_row").val($(this).html());
                hideLi();
            });
        });
        $("#page_row").blur(function () {
            setTimeout(hideLi, 200);
        });
    }
);
function showLi() {
    $("#liebiao").show();
}
function hideLi() {
    $("#liebiao").hide();
}
//跳转页面的键盘按下事件
$("#input-txt").keydown(function () {
    var event = window.event || arguments[0];
    var inx = this.value.replace(/[^0-9]/g, '');
    var inx = parseInt(inx);
    if (inx > cout) {
        inx = cout
    }
    ;
    if (inx > 0) {
        if (event.keyCode == 13) {
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
        ;
    }
})
//筛选
var area_num = 1;
var area_next = false;
var shop_num = 1;
var shop_next = false;
var staff_num = 1;
var staff_next = false;
//点击筛选
$("#filtrate").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    console.log(123123);
})
//点击筛选会员的所属区域
$("#screen_areal").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_area").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_wrapper").hide();
    $("#screen_area").show();
    var area_num = 1
    if ($("#screen_area .screen_content_l ul li").length > 1) {
        return;
    }
    getarealist(area_num);
})
//点击筛选会员的所属品牌
$("#screen_brandl").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_brand").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_brand").show();
    if ($("#screen_brand .screen_content_l ul li").length > 1) {
        return;
    }
    getbrandlist();
})
//点击筛选会员的所属店铺
$("#screen_shopl").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_shop").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_wrapper").hide();
    $("#screen_shop").show();
    $("#screen_shop .screen_content_l ul").empty();
    var shop_num = 1;
    getstorelist(shop_num);
})
//点击筛选会员的所属员工
$("#screen_staffl").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_staff").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_wrapper").hide();
    $("#screen_staff").show();
    $("#screen_staff .screen_content_l ul").empty();
    var staff_num = 1;
    getstafflist(staff_num);
})
//点击列表显示选中状态
$(".screen_content").on("click", "li", function () {
    var input = $(this).find("input")[0];
    var thinput = $("thead input")[0];
    if (input.type == "checkbox" && input.name == "test" && input.checked == false) {
        input.checked = true;
    } else if (input.type == "checkbox" && input.name == "test" && input.checked == true) {
        input.checked = false;
    }
})
//点击店铺的区域
$("#shop_area").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_shop").hide();
    $("#screen_area").show();
    $("#screen_area").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    var area_num = 1;
    $("#screen_area .screen_content_l ul").empty();
    // $("#screen_area .screen_content_r ul").empty();
    // $("#screen_area .s_pitch span").html("0");
    $("#area_search").val("");
    getarealist(area_num);
})
//点击店铺的品牌
$("#shop_brand").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_shop").hide();
    $("#screen_brand").show();
    $("#screen_brand").css({"left": +left + "px", "top": +tp + "px", "position": "fixed"});
    $("#screen_brand .screen_content_l ul").empty();
    // $("#screen_brand .screen_content_r ul").empty();
    getbrandlist();
})
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
    }
    if (li.length == "0") {
        frame();
        $('.frame').html("请先选择");
        return;
    }
    if (li.length > 0) {
        for (var i = 0; i < li.length; i++) {
            var html = $(li[i]).html();
            var id = $(li[i]).find("input[type='checkbox']").val();
            var input = $(b).parents(".screen_content").find(".screen_content_r li");
            for (var j = 0; j < input.length; j++) {
                if ($(input[j]).attr("id") == id) {
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>")
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    bianse();
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
        frame();
        $('.frame').html("请先选择");
        return;
    }
    if (li.length > 0) {
        for (var i = li.length - 1; i >= 0; i--) {
            $(li[i]).remove();
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    bianse();
}
//点击右移
$(".shift_right").click(function () {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
})
//点击右移全部
$(".shift_right_all").click(function () {
    var right = "all";
    var div = $(this);
    removeRight(right, div);
})
//点击左移
$(".shift_left").click(function () {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
})
//点击左移全部
$(".shift_left_all").click(function () {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
})
//区域搜索
$("#area_search").keydown(function () {
    var event = window.event || arguments[0];
    area_num = 1;
    if (event.keyCode == 13) {
        $("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function () {
    var event = window.event || arguments[0];
    shop_num = 1;
    if (event.keyCode == 13) {
        $("#screen_shop .screen_content_l ul").empty();
        getstorelist(shop_num);
    }
})
//员工搜索
$("#staff_search").keydown(function () {
    var event = window.event || arguments[0];
    staff_num = 1;
    if (event.keyCode == 13) {
        $("#screen_staff .screen_content_l ul").empty();
        getstafflist(staff_num);
    }
})
//店铺放大镜搜索
$("#store_search_f").click(function () {
    shop_num = 1;
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function () {
    area_num = 1;
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
})
//员工放大镜收索
$("#staff_search_f").click(function () {
    staff_num = 1;
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
})
//区域关闭
$("#screen_close_area").click(function () {
    $("#screen_area").hide();
    $("#screen_wrapper").show();
})
//店铺关闭
$("#screen_close_shop").click(function () {
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
})
//品牌关闭
$("#screen_close_brand").click(function () {
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
})
//员工关闭
$("#screen_close_staff").click(function () {
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
})
//弹框关闭
$("#screen_wrapper_close").click(function () {
    $("#screen_wrapper").hide();
    $("#p").hide();
})
function bianse() {
    $(".screen_content_l li:odd").css("backgroundColor", "#fff");
    $(".screen_content_l li:even").css("backgroundColor", "#ededed");
    $(".screen_content_r li:odd").css("backgroundColor", "#fff");
    $(".screen_content_r li:even").css("backgroundColor", "#ededed");
}
//区域滚动事件
$("#screen_area .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (area_next) {
            return;
        }
        getarealist(area_num);
    }
    ;
})
//店铺滚动事件
$("#screen_shop .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (shop_next) {
            return;
        }
        getstorelist(shop_num);
    }
    ;
})
//导购滚动事件
$("#screen_staff .screen_content_l").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        if (staff_next) {
            return;
        }
        getstafflist(staff_num);
    }
    ;
})
//点击区域确定追加节点
$("#screen_que_area").click(function () {
    var li = $("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_codes = "";
    var area_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            area_codes += r + ",";
            area_name += p + ",";
        } else {
            area_codes += r;
            area_name += p;
        }
    }
    var num = $("#screen_area .screen_content_r input[type='checkbox']").parents("li").length;
    $("#screen_area_num").val(area_name);
    $("#screen_area_num").attr("data-code", area_codes);
    $("#area_num").val("已选" + num + "个");
    $("#area_num").attr("data-areacode", area_codes);
    $("#staff_area_num").val("已选" + num + "个");
    $("#staff_area_num").attr("data-areacode", area_codes);
    $("#screen_area").hide();
    $("#screen_wrapper").show();
})
//点击店铺确定追加节点
$("#screen_que_shop").click(function () {
    var li = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_name = "";
    var store_code = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            store_code += r + ",";
            store_name += p + ",";
        } else {
            store_code += r;
            store_name += p;
        }
    }
    var num = $("#screen_shop .screen_content_r input[type='checkbox']").parents("li").length;
    $("#screen_shop_num").val(store_name);
    $("#screen_shop_num").attr("data-code", store_code);
    $("#staff_shop_num").val("已选" + num + "个")
    $("#staff_shop_num").attr("data-storecode", store_code);
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
})
//点击品牌确定追加节点
$("#screen_que_brand").click(function () {
    var li = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_codes = "";
    var brand_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            brand_codes += r + ",";
            brand_name += p + ",";
        } else {
            brand_codes += r;
            brand_name += p;
        }
    }
    var num = $("#screen_brand .screen_content_r input[type='checkbox']").parents("li").length;
    $("#brand_num").attr("data-brandcode", brand_codes);
    $("#brand_num").val("已选" + num + "个");
    $("#screen_brand_num").val(brand_name);
    $("#screen_brand_num").attr("data-code", brand_codes);
    $("#staff_brand_num").val("已选" + num + "个");
    $("#staff_brand_num").attr("data-brandcode", brand_codes);
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
})
//点击员工确定追加节点
$("#screen_que_staff").click(function () {
    var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var staff_codes = "";
    var staff_name = "";
    for (var i = 0; i < li.length; i++) {
        var r = $(li[i]).attr("id");
        var p = $(li[i]).find(".p16").html();
        if (i < li.length - 1) {
            staff_codes += r + ",";
            staff_name += p + ",";
        } else {
            staff_codes += r;
            staff_name += p;
        }
    }
    $("#screen_stff_num").val(staff_name);
    $("#screen_stff_num").attr("data-code", staff_codes);
    $("#screen_staff").hide();
    $("#screen_wrapper").show();
})
//拉取区域
function getarealist(a) {
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    // console.log(corp_code);
    var area_command = "/area/selAreaByCorpCode";
    var searchValue = $("#area_search").val().trim();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param["corp_code"] = corp_code;
    _param["searchValue"] = searchValue;
    _param["pageSize"] = pageSize;
    _param["pageNumber"] = pageNumber;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", area_command, "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var cout = list.pages;
            var list = list.list;
            var area_html_left = '';
            var area_html_right = '';
            if (list.length == 0) {
                if (a == 1) {
                    for (var h = 0; h < 9; h++) {
                        area_html_left += "<li></li>";
                    }
                }
                area_next = true;
            } else {
                if (list.length < 9 && a == 1) {
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
                    for (var j = 0; j < 9 - list.length; j++) {
                        area_html_left += "<li></li>"
                    }
                } else if (list.length >= 9 || list.length < 9 && a > 1) {
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
                area_num++;
                area_next = false;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            bianse();
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
}
//获取店铺列表
function getstorelist(a) {
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code = $('#screen_area_num').attr("data-code");//
    var brand_code = $('#screen_brand_num').attr("data-code");
    var searchValue = $("#store_search").val();
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    var checknow_data = [];
    var checknow_namedata = [];
    _param['corp_code'] = corp_code;
    _param['area_code'] = area_code;
    _param['brand_code'] = brand_code;
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post", "/shop/selectByAreaCode", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var cout = list.pages;
            var list = list.list;
            var store_html = '';
            if (list.length == 0) {
                if (a == 1) {
                    for (var h = 0; h < 9; h++) {
                        store_html += "<li></li>";
                    }
                }
                shop_next = true;
            } else {
                if (list.length < 9 && a == 1) {
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
                    for (var j = 0; j < 9 - list.length; j++) {
                        store_html += "<li></li>"
                    }
                } else if (list.length >= 9 || list.length < 9 && a > 1) {
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
                shop_num++;
                shop_next = false;
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取品牌列表
function getbrandlist() {
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var _param = {};
    _param["corp_code"] = corp_code;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/shop/brand", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.brands;
            var brand_html_left = '';
            var brand_html_right = '';
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

                $("#screen_brand .screen_content_l ul").append(brand_html_left);
            }
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//获取员工列表
function getstafflist(a) {
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code = $('#screen_area_num').attr("data-code");//区域
    var brand_code = $('#screen_brand_num').attr("data-code");//品牌
    var store_code = $("#screen_shop_num").attr("data-code");//员工
    var searchValue = $("#staff_search").val();//员工的value值
    var pageSize = 20;
    var pageNumber = a;
    var _param = {};
    _param['corp_code'] = corp_code;
    _param['area_code'] = area_code;
    _param['brand_code'] = brand_code;
    _param['store_code'] = store_code;
    _param['searchValue'] = searchValue;
    _param['pageNumber'] = pageNumber;
    _param['pageSize'] = pageSize;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var cout = list.pages;
            var list = list.list;
            var staff_html = '';
            if (list.length == 0) {
                if (a == 1) {
                    for (var h = 0; h < 9; h++) {
                        staff_html += "<li></li>";
                    }
                }
                staff_next = true;
            } else {
                if (list.length < 9 && a == 1) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].user_name + "</span></li>"
                    }
                    for (var j = 0; j < 9 - list.length; j++) {
                        staff_html += "<li></li>"
                    }
                } else if (list.length >= 9 || list.length < 9 && a > 1) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>" + list[i].user_name + "</span></li>"
                    }
                }
                staff_num++;
                staff_next = false;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//点击会员确定
$("#screen_vip_que").click(function () {
    inx = 1;
    var _param = {}
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code = $('#screen_area_num').attr("data-code");//区域
    var brand_code = $('#screen_brand_num').attr("data-code");//品牌
    var store_code = $("#screen_shop_num").attr("data-code");//店铺
    var user_code = $("#screen_stff_num").attr("data-code");//员工
    _param["corp_code"] = corp_code;
    _param["brand_code"] = brand_code;
    _param["store_code"] = store_code;
    _param["area_code"] = area_code;
    _param["user_code"] = user_code;
    _param["pageNumber"] = inx;
    _param["pageSize"] = pageSize;
    if (area_code == "" && brand_code == "" && store_code == "" && user_code == "") {
        GET(inx, pageSize, group_code);
    }
    if (area_code !== "" || brand_code !== "" || store_code !== "" || user_code !== "") {
        filtrate = "sucess";
        filtrates(inx, pageSize, _param, group_code);
    }
    $("#screen_wrapper").hide();
    $("#p").hide();
})
//筛选调接口
function filtrates(a, b, _param, c) {
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vip/vipScreen", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                $(".foot ul").empty();
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, a, c);
                jumpBianse();
                setPage($("#foot-num")[0], cout, a, b, c);
                whir.loading.remove();//移除加载框
            }
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//鼠标按下时触发的收索
$("#search").keydown(function () {
    var event = window.event || arguments[0];
    value = this.value.replace(/\s+/g, "");
    if (event.keyCode == 13) {
        value=this.value.trim();
        inx = 1;
        if(value!==""){
            param["searchValue"] = value;
            param["pageNumber"] = inx;
            param["pageSize"] = pageSize;
        }else{
            GET(inx, pageSize, group_code);
        }
    }
});
//点击放大镜触发搜索
$("#d_search").click(function () {
    value = $("#search").val().replace(/\s+/g, "");
    if (value !== "") {
        inx = 1;
        param["searchValue"] = value;
        param["pageNumber"] = inx;
        param["pageSize"] = pageSize;
        POST(inx, pageSize, group_code);
    } else {
        GET(inx, pageSize, group_code);
    }
})
//搜索的请求函数
function POST(a, b, c) {
    console.log(c);
    param["corp_code"] = corp_code;
    whir.loading.add("", 0.5);//加载等待框
    oc.postRequire("post", "/vip/vipSearch", "0", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            var actions = message.actions;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到与<span class='color'>“" + value + "”</span>相关的信息，请重新搜索</p>");
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, a, c);
                jumpBianse();
                whir.loading.remove();//移除加载框
            }
            filtrate = "";
            setPage($("#foot-num")[0], cout, a, b, c);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
