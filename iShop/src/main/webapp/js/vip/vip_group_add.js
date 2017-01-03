var oc = new ObjectControl();
var pageNumber = 1;//删除默认第一页
var pageSize = 10;//默认传的每页多少行
var value = "";//收索的关键词
var role = '';//识别页面
var _param = {};
var group_cheked = [];
var corp_code = '';//企业编号
var group_code = '';//分组编号
var group_name='';//分组名称
var group_remark='';//分组备注
var group_count = '';
var user_nextpage = false;//导购拉下一页
var user_num=1;
var param = {};//搜索参数
var choose_this_group='';//当前已选择
var all_select_vip_list=[];
var  message={
    cache:{//缓存变量
        //"vip_id":"",
        //"area_codes":"",
        //"area_names":"",
        //"brand_codes":"",
        //"brand_names":"",
        //"store_codes":"",
        //"store_names":"",
        //"user_codes":"",
        //"user_names":"",
        "group_codes":"",
        //"group_name":"",
        //"type":"",
        //"count":"",
        //"corp_code":""
    }
};
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
                    $(".areaadd_oper_btn ul li:nth-of-type(1)").remove();
                    $("#edit_close").css("margin-left","120px");
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
                $("#OWN_CORP option").val(msg.corp_code);
                $("#PARAM_NAME").attr("data-code",msg.user_code);
                $("#PARAM_NAME").val(msg.user_name);
                var Code = msg.corp_code;
                corp_code=msg.corp_code;
                $("#OWN_CORP option").text(msg.corp_name);
                $("#area_shop").val("共"+msg.store_count+"家店铺");
                 $("#OWN_CORP").val(msg.corp_code);
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
                if(msg.group_type=="define"){
                    all_select_vip_list=JSON.parse(msg.group_condition);
                    showSelect();
                }else{
                    var list=JSON.parse(msg.group_condition);
                    showOtherGroup(list,msg.group_type);
                }
                console.log(all_select_vip_list)
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
        //$("#vip_num").val("VG"+ new Date().getTime())
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
            //var num = $("#vip_num").attr("data-mark");//区域编号是否唯一的标志
            if (vipjs.firstStep()) {
                if (name == "N") {
                    if (name == "N") {
                        var div = $("#vip_id").next('.hint').children();
                        div.html("该名称已经存在！");
                        div.addClass("error_tips");
                    }
                    //if (num == "N") {
                    //    var div = $("#vip_num").next('.hint').children();
                    //    div.html("该编号已经存在！");
                    //    div.addClass("error_tips");
                    //}
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
                    //"vip_group_code": vip_num,
                    "vip_group_name": vip_id,
                    "corp_code": OWN_CORP,
                    "user_code": user_code,
                    "remark": vip_remark,
                    "isactive": ISACTIVE
                };
                var vip_group_type=$("#group_type").attr("data-type");
                _params["group_type"]=vip_group_type;
                vip_group_type=="define"?isDefine():noDefine();
                function isDefine(){
                    var group_condition_array=all_select_vip_list;
                    _params["group_condition"]=group_condition_array;
                    if(group_condition_array.length==0){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请设置分组条件"
                        });
                        return false;
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                function noDefine(){
                    var group_condition={};
                    var circle_val=$("#type").attr("data-type");
                    group_condition["circle"]=circle_val;
                    $("#consume_amount").is(":hidden")==true? delete group_condition.consume_amount:group_condition["consume_amount"]={start:$("#consume_amount input").eq(0).val(),end:$("#consume_amount input").eq(1).val()}; //消费金额
                    $("#consume_piece").is(":hidden")==true?delete group_condition.consume_piece:group_condition["consume_piece"]={start:$("#consume_piece input").eq(0).val(),end:$("#consume_piece input").eq(1).val()};   //消费件数
                    $("#consume_discount").is(":hidden")==true?delete group_condition.consume_discount:group_condition["consume_discount"]={start:$("#consume_discount input").eq(0).val(),end:$("#consume_discount input").eq(1).val()}; //消费折扣
                    _params["group_condition"]=group_condition;
                    var allInput=$("#group_list input");
                    var isNoValue=true;
                    for(var i=0;i<allInput.length;i++){
                        if(!$(allInput[i]).parent().is(":hidden") && $(allInput[i]).val().trim()!=""){
                            isNoValue=false;
                            break;
                        }
                    }
                    if(isNoValue){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"至少输入一项条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                //vipjs.ajaxSubmit(_command, _params, opt);
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
                //var _command = "/vipGroup/add";//接口名
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
                var vip_group_type=$("#group_type").attr("data-type");
                _params["group_type"]=vip_group_type;
                vip_group_type=="define"?isDefine():noDefine();
                function isDefine(){
                    var group_condition_array=all_select_vip_list;
                    _params["group_condition"]=group_condition_array;
                    if(all_select_vip_list.length==0){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"请设置分组条件"
                        });
                        return false;
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
                function noDefine(){
                    var group_condition={};
                    var circle_val=$("#type").attr("data-type");
                    group_condition["circle"]=circle_val;
                    $("#consume_amount").is(":hidden")==true? delete group_condition.consume_amount:group_condition["consume_amount"]={start:$("#consume_amount input").eq(0).val(),end:$("#consume_amount input").eq(1).val()}; //消费金额
                    $("#consume_piece").is(":hidden")==true?delete group_condition.consume_piece:group_condition["consume_piece"]={start:$("#consume_piece input").eq(0).val(),end:$("#consume_piece input").eq(1).val()};   //消费件数
                    $("#consume_discount").is(":hidden")==true?delete group_condition.consume_discount:group_condition["consume_discount"]={start:$("#consume_discount input").eq(0).val(),end:$("#consume_discount input").eq(1).val()}; //消费折扣
                    _params["group_condition"]=group_condition;
                   var allInput=$("#group_list input");
                   var isNoValue=true;
                    for(var i=0;i<allInput.length;i++){
                        if(!$(allInput[i]).parent().is(":hidden") && $(allInput[i]).val().trim()!=""){
                             isNoValue=false;
                            break;
                        }
                    }
                    if(isNoValue ){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"至少输入一项条件"
                        });
                    }else{
                        vipjs.ajaxSubmit(_command, _params, opt);
                    }
                }
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
function getcorplist(a) {
    //获取所属企业列表
    var userNum = 1;//分配导购分页，默认第一页; var userNum = 1;//分配导购分页，默认第一页;
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        //console.log(data);
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            //console.log(msg);
            var corp_html = '';
            for (var i=0;i<msg.corps.length;i++) {
                corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if (a !== "") {
                $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
            }
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function () {
                user_num = 1;
                $("#vip_id").val("");
                $("#vip_num").val("");
                $("#group_recode").val("");
                $("#vip_remark").val("");
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
//$("#vip_num").blur(function () {
//    // var isCode=/^[A]{1}[0-9]{4}$/;
//    var _params = {};
//    var vip_group_code = $(this).val();
//    var vip_group_code1 = $(this).attr("data-name");
//    var corp_code = $("#OWN_CORP").val();
//    if (vip_group_code !== "" && vip_group_code !== vip_group_code1) {
//        _params["vip_group_code"] = vip_group_code;
//        _params["corp_code"] = corp_code;
//        var div = $(this).next('.hint').children();
//        oc.postRequire("post", "/vipGroup/vipGroupCodeExist", "", _params, function (data) {
//            if (data.code == "0") {
//                div.html("");
//                $("#vip_num").attr("data-mark", "Y");
//            } else if (data.code == "-1") {
//                $("#vip_num").attr("data-mark", "N");
//                div.addClass("error_tips");
//                div.html("该编号已经存在！");
//            }
//        })
//    }
//});
//验证名称是否唯一
 $("#vip_id").blur(function () {
     var corp_code = $("#OWN_CORP").val();
     var vip_id = $("#vip_id").val();
     var vip_id1 = $("#vip_id").attr("data-name");
     var div = $(this).next('.hint').children();
     if (vip_id !== "" && vip_id !== vip_id1) {
         var _params = {};
         _params["vip_group_name"] = vip_id;
         _params["corp_code"] = corp_code;
         oc.postRequire("post", "/vipGroup/vipGroupNameExist", "", _params, function (data) {
             if (data.code == "0") {
                 div.html("");
                 $("#vip_id").attr("data-mark", "Y");
             } else if (data.code == "-1") {
                 div.html("该名称已经存在！");
                 div.addClass("error_tips");
                 $("#vip_id").attr("data-mark", "N");
             }
         })
     }
 });
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
    group_name = $("#vip_id").val();
    group_remark = $("#vip_remark").val();
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
    //去重
    console.log(group_cheked);
    console.log(choose);
    for(var r=choose.length-1;r>=0;r--){
        for(var i=0;i<group_cheked.length;i++){
            choose[r]==group_cheked[i]?choose.splice(r,1):'';
        }
    }
    console.log(choose);
    console.log(quit);
    param['vip_group_id'] =sessionStorage.getItem("id");
    param['choose'] = choose.toString();
    param['vip_group_code'] = group_code;//分组编号
    param['vip_group_name'] = group_name;//分组名称
    param['vip_group_remark'] = group_remark;//备注
    param['quit'] = quit.toString();
    console.log(param);
    //发起异步请求
    oc.postRequire("post", '/vipGroup/saveVips', "", param, function (data) {
        if (data.code == 0) {
            var msg=JSON.parse(data.message);
            console.log(msg);
            $('#vip_num').val(group_code);
            $('#vip_id').val(group_name);
            $('#vip_remark').val(group_remark);
            $('#group_recode').val('共' +msg.vip_count+ "个会员");
            // if (role == 'eidtor') {
            //     // window.location.reload()
            // } else {
            //     $('#group_recode').val('共' + group_count + "个会员");
            //     $('#page-wrapper')[0].style.display = 'block';
            //     $('.content')[0].style.display = 'none';
            // }
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
    var role_add=arguments[3];
    whir.loading.add("", 0.5);//加载等待框
    var user_code =$("#PARAM_NAME").attr("data-code");
    var vip_group_code = $("#vip_num").val();
    if(user_code == undefined){
        user_code = "";
    }
    var param = {};
    arguments[3]=='search'&&(param['searchGroupVip']='');
    param["pageNumber"] = a;
    param["pageSize"] = b;
    param["corp_code"] = corp_code;
    // param["user_code"] = user_code;
    param["vip_group_id"] =sessionStorage.getItem("id");
    oc.postRequire("post", "/vipGroup/allVip ", "", param, function (data) {
        if (data.code == "0") {
            $(".table tbody").empty();
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            console.log(list);
            cout = message.pages;
            //var list=list.list;
            superaddition(list, a, c);
            jumpBianse();
            filtrate = "";
            $(".table p").remove();
            setPage($("#foot-num")[0], cout, a, b, c,role_add);
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
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:50%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    group_cheked = [];
    var judge = '';
    var tr_node='';
    var wx='';
    for (var i = 0; i < data.length; i++) {
        if ( data[i].is_this_group == "Y") {
            judge = 'checked'
        } else {
            judge = '';
        }
        if (num >= 2) {
            var a = i + 1 + (num - 1) * pageSize;
        } else {
            var a = i + 1;
        }
        if(data[i].open_id){
            wx="<span class='icon-ishop_6-22'style='color:#8ec750'></span>";
        }else{
            wx="<span class='icon-ishop_6-22'style='color:#cdcdcd'></span>";
        }
        var gender = data[i].sex == 'F' ? '女' : '男';
        var tr_vip_id = data[i].vip_id;
         tr_node += "<tr data-storeid='" + data[i].store_id + "' id='" + data[i].corp_code + "'>"
            + "</td><td style='text-align:left;padding-left:22px'>"
            + a
            + "</td><td data_vip_id='" + tr_vip_id + "'>"
            + data[i].vip_name
             + "</td><td>"
             + wx
            + "</td><td>"
            + gender
            + "</td><td>"
            + data[i].vip_phone
            // + "</td><td data_cardno='" + data[i].cardno + "'>"
            + "</td><td>"
            + data[i].cardno
            + "</td><td>"
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
        if (judge) {
            group_cheked.push(tr_vip_id);
        }
    }
    $(".table tbody").append(tr_node);
    $("tbody tr").click(function () {
        var input = $(this).find("input")[0];
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            input.checked = false;
        }
    })
    $(".th th:last-child input").removeAttr("checked");
};
//生成分页
function setPage(container, count, pageindex, pageSize, c,role_add) {
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
            dian(inx, pageSize, c,role_add);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize, c,role_add);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize, c,role_add);
            // setPage(container, count, inx,pageSize,funcCode,value);
            return false;
        }
    }()
}
//点击页码
function dian(a, b, c,role_add) {//点击分页的时候调什么接口
    console.log(role_add)
    if (value == "" && filtrate == "") {
        GET(a, b, c,role_add);
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
                    GET(inx, pageSize,'',str);
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
                GET(inx, pageSize,'',str);
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
var bd=[];//品牌
var ar=[];//区域
var sp=[];//店铺
var sf=[];//员工
var str='';//调用allVip接口的区分
//点击筛选会员的所属区域
$("#screen_areal").click(function () {
    // if($('#screen_area .screen_content_r ul li').length!=0){
    //     console.log(1);
    //     $('#screen_area .screen_content_r ul li').each(function () {
    //         ar.indexOf(this.id)==-1&&($(this).remove());
    //     })
    // }else if($('#screen_area .screen_content_r ul li').length==0&&(ar.length!=0)){
    //     console.log(2)
    //     $('#screen_area .screen_content_l ul li').each(function () {
    //         if(ar.indexOf(this.id)!=-1){
    //             $('#screen_area .screen_content_r ul').append($(this).clone())
    //         }
    //     })
    // }
    nodeSave($('#screen_area .screen_content_r ul li'),$('#screen_area .screen_content_l ul li'),ar, $('#screen_area .screen_content_r ul'));
    $('#screen_area .s_pitch span').html($('#screen_area .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_area").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_wrapper").hide();
    $("#screen_area").show();
    var area_num = 1
    //if ($("#screen_area .screen_content_l ul li").length > 1) {
    //    return;
    //}
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
})
//点击筛选会员的所属品牌
$("#screen_brandl").click(function () {
    nodeSave($('#screen_brand .screen_content_r ul li'),$('#screen_brand .screen_content_l ul li'),bd, $('#screen_brand .screen_content_r ul'));
    $('#screen_brand .s_pitch span').html($('#screen_brand .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_brand").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_brand").show();
    //if ($("#screen_brand .screen_content_l ul li").length > 1) {
    //    return;
    //}
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//点击筛选会员的所属店铺
$("#screen_shopl").click(function () {
    nodeSave($('#screen_shop .screen_content_r ul li'),$('#screen_shop .screen_content_l ul li'),sp, $('#screen_shop .screen_content_r ul'));
    $('#screen_shop .s_pitch span').html($('#screen_shop .screen_content_r ul li').length);
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
});
//点击筛选会员的所属员工
$("#screen_staffl").click(function () {
    nodeSave($('#screen_staff .screen_content_r ul li'),$('#screen_staff .screen_content_l ul li'),sf, $('#screen_staff .screen_content_r ul'));
    $('#screen_staff .s_pitch span').html($('#screen_staff .screen_content_r ul li').length);
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_area").width()) / 2;
    var tp = (arr[3] - $("#screen_area").height()) / 2 + 30;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_staff").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_wrapper").hide();
    $("#screen_staff").show();
    $("#screen_staff .screen_content_l ul").empty();
    var staff_num = 1;
    getstafflist(staff_num);
})
//节点状态保存
function nodeSave(node_r,node_l,arr,node_container) {
    if(node_r.length!=0){
        node_r.each(function () {
            arr.indexOf(this.id)==-1&&($(this).remove());
        })
    }else if(node_r.length==0&&(arr.length!=0)){
        node_l.each(function () {
            if(arr.indexOf(this.id)!=-1){
                node_container.append($(this).clone())
            }
        })
    }
}
//点击店铺的区域
$("#shop_area").click(function () {
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_shop").width()) / 2;
    var tp = (arr[3] - $("#screen_shop").height()) / 2 + 30;
    $("#screen_shop").hide();
    $("#screen_area").show();
    $("#screen_area").css({"left": +left + "px", "top": +tp + "px"});
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
    $("#screen_brand").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_brand .screen_content_l ul").empty();
    // $("#screen_brand .screen_content_r ul").empty();
    getbrandlist();
});
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
    ar=[];
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
    $($("#screen_area .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        ar.push(this.id);
    });
    // $("#screen_area_num").val(area_name);
    $("#screen_area_num").val("已选" + num + "个");
    $("#screen_area_num").attr("data-code", area_codes);
    $("#screen_area_num").attr("data-code_name", area_name);
    $("#area_num").val("已选" + num + "个");
    $("#area_num").attr("data-areacode", area_codes);
    $("#staff_area_num").val("已选" + num + "个");
    $("#staff_area_num").attr("data-areacode", area_codes);
    $("#screen_area").hide();
    $("#screen_wrapper").show();
})
//点击店铺确定追加节点
$("#screen_que_shop").click(function () {
    sp=[];
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
    $($("#screen_shop .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        sp.push(this.id);
    });
    // $("#screen_shop_num").val(store_name);
    $("#screen_shop_num").val("已选" + num + "个");
    $("#screen_shop_num").attr("data-code", store_code);
    $("#screen_shop_num").attr("data-code_name", store_name);
    $("#staff_shop_num").val("已选" + num + "个")
    $("#staff_shop_num").attr("data-storecode", store_code);
    $("#screen_shop").hide();
    $("#screen_wrapper").show();
})
//点击品牌确定追加节点
$("#screen_que_brand").click(function () {
    bd=[];
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
    $($("#screen_brand .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        bd.push(this.id);
    });
    $("#brand_num").attr("data-brandcode", brand_codes);
    $("#brand_num").val("已选" + num + "个");
    // $("#screen_brand_num").val(brand_name);
    $("#screen_brand_num").val("已选" + num + "个");
    $("#screen_brand_num").attr("data-code", brand_codes);
    $("#screen_brand_num").attr("data-code_name", brand_name);
    $("#staff_brand_num").val("已选" + num + "个");
    $("#staff_brand_num").attr("data-brandcode", brand_codes);
    $("#screen_brand").hide();
    $("#screen_wrapper").show();
})
//点击员工确定追加节点
$("#screen_que_staff").click(function () {
    sf=[];
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
    var num = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li").length;
    $($("#screen_staff .screen_content_r input[type='checkbox']").parents("li")).each(function () {
        sf.push(this.id);
    });
    $("#screen_stff_num").val("已选" + num + "个");
    $("#screen_stff_num").attr("data-code", staff_codes);
    $("#screen_stff_num").attr("data-code_name", staff_name);
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
    _param["corp_code"] =$("#OWN_CORP").val();
    _param["searchValue"] = searchValue;
    _param["pageSize"] = pageSize;
    _param["pageNumber"] = pageNumber;
    whir.loading.add("", 0.5);//加载等待框
    $("#mask").css("z-index", "10002");
    oc.postRequire("post", area_command, "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var area_html_left = '';
            var area_html_right = '';
            console.log(list);
            if (list.length == 0) {
            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        area_html_left += "<li  id='" + list[i].area_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].area_code + "' data-areaname='" + list[i].area_name + "' name='test'  class='check'  id='checkboxOneInput"
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
            }
            if(hasNextPage==true){
                area_num++;
                area_next=false;
            }
            if(hasNextPage==false){
                area_next=true;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            // bianse();
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
    _param['corp_code'] = $("#OWN_CORP").val();
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
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var store_html = '';
            if (list.length == 0) {
            } else {
                if (list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        store_html += "<li id='" + list[i].store_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].store_code + "' data-storename='" + list[i].store_name + "' name='test'  class='check'  id='checkboxTowInput"
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
            if(hasNextPage==true){
                shop_num++;
                shop_next = false;
            }
            if(hasNextPage==false){
                shop_next=true;
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            // bianse();
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
    var searchValue=$("#brand_search").val();
    var _param = {};
    console.log($("#OWN_CORP").val());
    _param['corp_code']=$("#OWN_CORP").val();
    _param["searchValue"]=searchValue;
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
                        brand_html_left += "<li id='" + list[i].brand_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
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
                        brand_html_left += "<li id='" + list[i].brand_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].brand_code + "' data-areaname='" + list[i].brand_name + "' name='test'  class='check'  id='checkboxThreeInput"
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
            // bianse();
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
    _param['corp_code'] = $("#OWN_CORP").val();
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
            var hasNextPage=list.hasNextPage;
            var cout = list.pages;
            var list = list.list;
            var staff_html = '';
            if (list.length == 0) {

            } else {
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        staff_html += "<li id='" + list[i].user_code + "'><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-storename='" + list[i].user_name + "' name='test'  class='check'  id='checkboxFourInput"
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
            }
            if(hasNextPage==true){
                staff_num++;
                staff_next = false;
            }
            if(hasNextPage==false){
                staff_next=true;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            // bianse();
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//点击会员确定
$("#screen_vip_que").click(function () {
    $('#search').val('');
    inx = 1;
    // var tr= $('#table tbody tr');
    // var corp_code=$(tr[0]).attr("id");
    var area_code = $('#screen_area_num').attr("data-code");//区域
    var brand_code = $('#screen_brand_num').attr("data-code");//品牌
    var store_code = $("#screen_shop_num").attr("data-code");//店铺
    var user_code = $("#screen_stff_num").attr("data-code");//员工
    _param["corp_code"] = $("#OWN_CORP").val();
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
        filtrates(inx, pageSize, group_code);
    }
    $("#screen_wrapper").hide();
    $("#p").hide();
})
//筛选调接口
function filtrates(a, b, c) {
    _param["vip_group_id"] =sessionStorage.getItem("id");
    whir.loading.add("", 0.5);//加载等待框
    // oc.postRequire("post", "/vip/vipScreen", "", _param, function (data) {
    oc.postRequire("post", "/vipGroup/vipScreen", "", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = message.all_vip_list;
            cout = message.pages;
            $(".table tbody").empty();
            if (list.length <= 0) {
                $(".table p").remove();
                $(".table").append("<p>没有找到信息,请重新搜索</p>");
                // $(".foot ul").empty();
                whir.loading.remove();//移除加载框
            } else if (list.length > 0) {
                $(".table p").remove();
                superaddition(list, a, c);
                jumpBianse();
                console.log($("#foot-num")[0]);
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
            POST(inx, pageSize, group_code);
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
$('#staff_area').click(function () {
    $('#screen_staff').hide();
    $('#screen_areal').trigger('click');
});
$('#staff_brand').click(function () {
    $('#screen_staff').hide();
    $('#screen_brandl').trigger('click');
});
$('#staff_shop').click(function () {
    $('#screen_staff').hide();
    $('#screen_shopl').trigger('click');
});
//点击搜索按钮提示与否
$('.r_filrate').hover(function () {
    $(this).next().show();
},function () {
    $(this).next().hide();
});
//点击搜索按钮
$('.r_filrate').click(function () {
    //清空搜索内容
     str='';
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    $(".sxk").slideUp();
    $("#search").val('');
    if($(this).next().text().trim()=='显示当前分组会员'){
        $(this).next().html('显示全部会员');
        $(this).attr('style','color:#50a3aa');
        str='search';
    }else{
        $(this).next().html('显示当前分组会员');
        $(this).attr('style','color:#fff');
        str=''
    }
    GET(1,10,group_code,str);
});
$("#group_type").next("ul").find("li").click(function(){
    var Val=$(this).text();
    var type=$(this).attr("data-type");
    switch (type){
        case "define":
            $("#group_type").css("width","320px");
            $("#group_type").next("ul").css("width","420px");
            $("#select_vip").css("display","inline-block");
            $("#type").hide();
            $("#group_list").hide();
            $("#custom_vip_list").show();
            break;
        case "class":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            break;
        case "brand":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li><li data-type='12'>近12个月消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            break;
        case "discount":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$(
                "<li data-type='3'>近3月</li>" +
                "<li data-type='6'>近6月</li>" +
                "<li data-type='12'>近12月</li>" +
                "<li data-type='15'>近15月</li>" +
                "<li data-type='18'>近18月</li>" +
                "<li data-type='21'>近21月</li>" +
                "<li data-type='24'>近24月</li>" +
                "<li data-type='all'>累计消费</li>"
                );
            $("#type").next("ul").html(LI);
            $("#type").val("近3月");
            $("#type").attr("data-type","3");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().hide();
            $("#group_list").children().last().show();
            $("#custom_vip_list").hide();
            break;
        case "season":
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").show();
            $("#type").attr("data-type","all");
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().last().hide();
            $("#group_list").children().eq(1).css("margin","0");
            $("#custom_vip_list").hide();
            break;
    }
    $(this).parent().prev("input").val(Val);
    $(this).parent().prev("input").attr("data-type",type);
    $(this).parent().hide();
});
$("#type").next("ul").on("click","li",function(){
    var Val=$(this).text();
    var type=$(this).attr("data-type");
    $(this).parent().prev("input").val(Val);
    $(this).parent().prev("input").attr("data-type",type);
    $(this).parent().hide();
});
$("#type").click(function(){
    $(this).next("ul").is(":hidden")==true?$(this).next("ul").show():$(this).next("ul").hide();
});
$("#type").blur(function(){
    var self=$(this);
    setTimeout(function(){
        self.next("ul").hide();
    },200)
});
$("#group_type").blur(function(){
    var self=$(this);
    setTimeout(function(){
        self.next("ul").hide();
    },200)
});
$("#select_vip").click(function(){
    var arr = whir.loading.getPageSize();
    var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    $("#p").show();
    $("#screen_wrapper").css({"left": +left + "px", "top": +tp + "px"});
    $("#screen_wrapper").show();
});
$("#select_vip_que").click(function(){ //筛选确定
        var screen = [];
        function settingValue(){
            if ($("#simple_filter").css("display") == "block") {
                $("#simple_contion .contion_input").each(function () {
                    var input = $(this).find("input");
                    var key = $(input[0]).attr("data-kye");
                    var classname = $(input[0]).attr("class");
                    if (classname.indexOf("short") == 0 && key != "3" && key != "4" && key!='17') {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            var name=$(input[0]).prev().text();
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param["name"]=name;
                            screen.push(param);
                        }
                    }else if(key=='17'){

                    }else if (key == "3" || key == "4") {
                        if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                            var param = {};
                            var val = {};
                            var date =$("#simple_filter").find("input[data-kye='"+key+"']").parent().siblings().find("#consume_date_basic_"+key).attr("data-date");
                            var name=$(input[0]).prev().text();
                            val['start'] = $(input[0]).val();
                            val['end'] = $(input[1]).val();
                            param['type'] = "json";
                            param['key'] = key;
                            param['value'] = val;
                            param['date'] = date;
                            param["name"]=name;
                            screen.push(param);
                        }
                    }else {
                        if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                            var param = {};
                            var val = $(input[0]).val();
                            var name=$(input[0]).prev().text();
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param["name"]=name;
                            screen.push(param);
                        }
                    }
                });
                return true
            } else {
                if( $("input[data-kye='11']").val() !== "" && $("#vip_card_type").val()=="全部"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        zIndex:10002,
                        content: "请选择会员卡类型"
                    });
                    return false;
                }
                $("#contion>div").each(function () {
                    $(this).find(".contion_input").each(function (i, e) {
                        var input = $(e).find("input");
                        var key = $(input[0]).attr("data-kye");
                        var classname = $(input[0]).attr("class");
                        var id = $(input[0]).attr("id");
                        if (key !== "3" && key !== "4" && classname.indexOf("short") == 0) {
                            if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                                var param = {};
                                var val = {};
                                var name=$(input[0]).prev().text();
                                val['start'] = $(input[0]).val();
                                val['end'] = $(input[1]).val();
                                param['type'] = "json";
                                param['key'] = key;
                                param['value'] = val;
                                param['name']=name;
                                screen.push(param);
                            }
                        } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15" || key == "16") {
                            if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                                var param = {};
                                var val = $(input[0]).attr("data-code");
                                var name=$(input[0]).prev().text();
                                var all_list_name=$(input[0]).attr("data-code_name");
                                param['key'] = key;
                                param['value'] = val;
                                param['type'] = "text";
                                param["name"]=name;
                                param["all_list_name"]=all_list_name;
                                screen.push(param);
                            }
                        } else if (key == "17") {

                        } else if (key == "3" || key == "4") {
                            if ($(input[0]).val() !== "" || $(input[1]).val() !== "") {
                                var param = {};
                                var val = {};
                                var date = $("#consume_date").attr("data-date");
                                var name=$(input[0]).prev().text();
                                val['start'] = $(input[0]).val();
                                val['end'] = $(input[1]).val();
                                param['type'] = "json";
                                param['key'] = key;
                                param['value'] = val;
                                param['date'] = date;
                                param["name"]=name;
                                screen.push(param);
                            }
                        } else if(key == "6" && $(input[0]).val() !== "全部"){
                            var param = {};
                            var val = $(input[0]).val();
                            var name=$(input[0]).prev().text();
                            if(val=="已冻结"){
                                val="Y"
                            }else if(val=="未冻结"){
                                val="N"
                            }
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param["name"]=name;
                            screen.push(param);
                        }else {
                            if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                                var param = {};
                                var val = $(input[0]).val();
                                var name=$(input[0]).prev().text();
                                param['key'] = key;
                                param['value'] = val;
                                param['type'] = "text";
                                param["name"]=name;
                                screen.push(param);
                            }
                        }
                    });
                    $(this).find("textarea").each(function () {
                        if($(this).val()!=""){
                            var key = $(this).attr("data-kye");
                            var param = {};
                            var val = $(this).val();
                            var name=$(this).prev().text();
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param["name"]=name;
                            screen.push(param);
                        }
                    });
                });
                return true;
            }
        }
       var setting=settingValue();
        if(screen.length>0){
            if(all_select_vip_list.length==0){
                all_select_vip_list=screen;
            }else{
                for(var i=0;i<screen.length;i++){
                    var has=false;
                    for(var a= 0,len=all_select_vip_list.length;a<len;a++){
                        if(all_select_vip_list[a].key==screen[i].key){
                         all_select_vip_list[a]=screen[i];
                          has=true;
                        }
                    }
                    if(!has){
                        console.log(all_select_vip_list);
                        all_select_vip_list.push(screen[i]);
                    }
                }
            }
            showSelect()
        }
        if(setting){
            $("#screen_wrapper").hide();
        }
        $("#p").hide();
});
function showSelect(){
    var html="";
    for(var b=0;b<all_select_vip_list.length;b++){
        if(all_select_vip_list[b].type=="json" && all_select_vip_list[b].key!=="3" && all_select_vip_list[b].key!="4"){
            html+="<div style='float: right'>" +
                "<span title='"+all_select_vip_list[b].name+"' style='text-align: right;display: inline-block;margin-right: 10px; max-width:70px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;'>"+all_select_vip_list[b].name+"</span>" +
                "<input type='text' style='width: 130px;' value='"+all_select_vip_list[b].value["start"]+"' readonly title='"+all_select_vip_list[b].value["start"]+"'>" +
                "<span style='display: inline-block;width: 30px;text-align: center'>~</span>" +
                "<input readonly type='text' style='width: 130px;' value='"+all_select_vip_list[b].value["end"]+"' title='"+all_select_vip_list[b].value["end"]+"'>" +
                "<i class='icon-ishop_6-12 q_remove' title='删除'></i>"+
                "</div>"
        }else if(all_select_vip_list[b].key=="brand_code" ||all_select_vip_list[b].key=="area_code" ||all_select_vip_list[b].key=="14" ||all_select_vip_list[b].key=="15" || all_select_vip_list[b].key=="16"){
            if(all_select_vip_list[b].value!=""){
                html+="<div style='float: right'>" +
                    "<span title='"+all_select_vip_list[b].name+"' style='vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width:70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis'>"+all_select_vip_list[b].name+"</span>" +
                    "<input type='text' style='width: 290px;' value='"+all_select_vip_list[b].all_list_name+"' readonly  title='"+all_select_vip_list[b].all_list_name+"'>"+
                    "<i class='icon-ishop_6-12 q_remove'title='删除'></i>"+
                    "</div>"
            }
        }else if(all_select_vip_list[b].key=="3" || all_select_vip_list[b].key=="4"){
            html+="<div style='float: right'>" +
                "<span title='"+all_select_vip_list[b].name+"' style='text-align: right;display: inline-block;margin-right: 10px; max-width:70px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;'>"+all_select_vip_list[b].name+"</span>" +
                "<input readonly type='text' style='width: 90px;margin-right: 10px;' value='最近"+all_select_vip_list[b].date+"个月'>" +
                "<input type='text' style='width: 80px;' value='"+all_select_vip_list[b].value["start"]+"' readonly title='"+all_select_vip_list[b].value["start"]+"'>" +
                "<span style='display: inline-block;width: 30px;text-align: center'>~</span>" +
                "<input readonly type='text' style='width: 80px;' value='"+all_select_vip_list[b].value["end"]+"' title='"+all_select_vip_list[b].value["end"]+"'>" +
                "<i class='icon-ishop_6-12 q_remove' title='删除111'></i>"+
                "</div>"
        }else if(all_select_vip_list[b].key=="17"){

        }else if(all_select_vip_list[b].key=="6"){
            var value=all_select_vip_list[b].value=="N"?"未冻结":"已冻结";
            html+="<div style='float: right'>" +
                "<span title='"+all_select_vip_list[b].name+"' style='vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
                "<input type='text' style='width: 290px;' value='"+value+"' readonly title='"+value+"'>"+
                "<i class='icon-ishop_6-12 q_remove'title='删除'></i>"+
                "</div>"
        }else{
            html+="<div style='float: right'>" +
                "<span title='"+all_select_vip_list[b].name+"' style='vertical-align:middle;text-align: right;display: inline-block;margin-right: 10px;max-width: 70px;white-space:nowrap;overflow: hidden;text-overflow:ellipsis '>"+all_select_vip_list[b].name+"</span>" +
                "<input type='text' style='width: 290px;' value='"+all_select_vip_list[b].value+"' readonly title='"+all_select_vip_list[b].value+"'>"+
                "<i class='icon-ishop_6-12 q_remove'title='删除'></i>"+
                "</div>"
        }
    }
    $("#custom_vip_list").html(html);
}
$("#custom_vip_list").on("click","i",function(){
    var name= $(this).parent().children().eq(0).text();
    for(var n=0;n<all_select_vip_list.length;n++){
        if(all_select_vip_list[n].name==name){
            all_select_vip_list.splice(n,1)
        }
    }
    $(this).parent().remove();
});
function showOtherGroup(list,type){
    var list=list;
    switch (type){
        case "class":
            $("#group_type").val("品类喜好分组");
            $("#group_type").attr("data-type","class");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").attr("data-type","all");
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            console.log(list)
            break;
        case "brand":
            $("#group_type").val("品牌喜好分组");
            $("#group_type").attr("data-type","brand");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li><li data-type='12'>近12个月消费</li>");
            $("#type").next("ul").html(LI);
            var type_name=list.circle=="all"?"累计消费":"近12个月消费";
            $("#type").val(type_name);
            $("#type").attr("data-type",list.circle);
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().eq(1).css("margin-bottom","20px");
            $("#custom_vip_list").hide();
            break;
        case "discount":
            $("#group_type").val("折扣偏好分组");
            $("#group_type").attr("data-type","discount");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$(
                "<li data-type='3'>近3月</li>" +
                "<li data-type='6'>近6月</li>" +
                "<li data-type='12'>近12月</li>" +
                "<li data-type='15'>近15月</li>" +
                "<li data-type='18'>近18月</li>" +
                "<li data-type='21'>近21月</li>" +
                "<li data-type='24'>近24月</li>" +
                "<li data-type='all'>累计消费</li>"
            );
            $("#type").next("ul").html(LI);
            var type_name=list.circle=="all"?"累计消费":"近"+list.circle+"月";
            $("#type").val(type_name);
            $("#type").attr("data-type",list.circle);
            $("#type").show();
            $("#group_list").show();
            $("#group_list").children().hide();
            $("#group_list").children().last().show();
            $("#custom_vip_list").hide();
            break;
        case "season":
            $("#group_type").val("季节偏好分组");
            $("#group_type").attr("data-type","season");
            $("#group_type").css("width","236px");
            $("#group_type").next("ul").css("width","236px");
            $("#select_vip").css("display","none");
            var LI=$("<li data-type='all'>累计消费</li>");
            $("#type").next("ul").html(LI);
            $("#type").val("累计消费");
            $("#type").show();
            $("#type").attr("data-type","all");
            $("#group_list").show();
            $("#group_list").children().show();
            $("#group_list").children().last().hide();
            $("#group_list").children().eq(1).css("margin","0");
            $("#custom_vip_list").hide();
            break;
    }
    for(var key in list){
        if(typeof (list[key])=="object"){
            $("#"+key).show();
            $("#"+key).find("input").eq(0).val(list[key].start);
            $("#"+key).find("input").eq(1).val(list[key].end);
          }
    }
}
    $("#group_list").find("input").blur(function(){
        var reg=/^\d+(\.\d+)?$/
        if(!reg.test($(this).val().trim()) && $(this).val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "只能输入数字"
            });
            $(this).val("")
        }
        if($(this).index()=="1" && Number($(this).val())-Number($(this).siblings("input").val())>0 && $(this).siblings("input").val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "不能大于后面的值"
            });
            $(this).val("")
        }
        if($(this).index()=="3" && Number($(this).val())-Number($(this).siblings("input").val())<0 && $(this).val()!=""){
            art.dialog({
                zIndex: 10010,
                time: 1,
                lock: true,
                cancel: false,
                content: "不能小于前面的的值"
            });
            $(this).val("")
        }
        //Number($(this).val())-Number($(this).val())
    });
$("#group_type").click(function(){
    $("#group_all_list").is(":hidden")==true?$("#group_all_list").show():$("#group_all_list").hide();
});
//$("#expend_attribute").scroll(function(){
//    $("#laydate_box").css("visibility","hidden")
//})