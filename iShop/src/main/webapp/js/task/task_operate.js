var oc = new ObjectControl();
var a = "";
var b = "";
var num = 1;//区域默认第一页
var dnum = 1;//店铺默认第一页
var ynum = 1;//员工默认第一页
//获取企业类型的下拉框
var param={};
var state=JSON.parse(sessionStorage.getItem("state"));
if(state!==null){
    whir.loading.add("", 0.5);//加载等待框
    $("#page-wrapper").show();
    $("#content").hide();
    $("#details").hide();
    var id = state.id;
    var corp_code = state.corp_code;
    var task_code = state.task_code;
    param["corp_code"] = corp_code;//公司编号
    param["task_code"] = task_code;//任务编号
    param["id"] = id;//公司id
    $('#task_id').val(id);
    nssignment();
}
var start = {
    elem: '#target_start_time',
    format: 'YYYY-MM-DD',
    min: laydate.now(), //设定最小日期为当前日期
    max: '2099-06-16 23:59:59', //最大日期
    istime: false,
    istoday: false,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#target_end_time',
    format: 'YYYY-MM-DD',
    min: laydate.now(),
    max: '2099-06-16 23:59:59',
    istime: false,
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);
function getasktypelist(a, b) {
    var corp_command = "/task/selectAllTaskType";
    var _param = {};
    _param["corp_code"] = a;
    var task_type_code=b;
    oc.postRequire("post", corp_command, "0", _param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);
            var list = JSON.parse(message.list);
            var type_html = "";
            $("#task_type_code").empty();
            $('#task_type_list .searchable-select').remove();
            if (list.length>0) {
                for (var i = 0; i < list.length; i++) {
                    type_html += '<option value="' + list[i].task_type_code + '">' + list[i].task_type_name + '</option>';
                }
                $("#task_type_code").html(type_html);
            } else if (list.length <= 0) {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请先定义任务类型"
                });
            };
            if(task_type_code!=="") {
               console.log(task_type_code);
               $("#task_type_list option[value='"+task_type_code+"']").attr("selected","true");
            }
            $("#task_type_code").searchableSelect();
        }
    })
}
//获取企业的下拉框
function getcorplist(a, b) {
    //获取所属企业列表
    var corp_command = "/user/getCorpByUser";
    oc.postRequire("post", corp_command, "", "", function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            $("#OWN_CORP").empty();
            $('#corp_select .searchable-select').remove();
            var corp_html = "";
            for (var i = 0; i < msg.corps.length; i++) {
                corp_html += '<option value="' + msg.corps[i].corp_code + '">' + msg.corps[i].corp_name + '</option>';
            }
            $("#OWN_CORP").append(corp_html);
            if (a !== "") {
                $("#OWN_CORP option[value='" + a + "']").attr("selected", "true");
            }
            $("#OWN_CORP").searchableSelect();
            var c = $('#OWN_CORP').val();
            getasktypelist(c, b);
            $("#corp_select .searchable-select-input").keydown(function (event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    var corp_code = $("#OWN_CORP").val();
                    console.log(corp_code);
                    if(c!==corp_code) {
                        getasktypelist(corp_code,b);
                        message.cache.vip_id="";
                        message.cache.area_codes="";
                        message.cache.area_names="";
                        message.cache.brand_codes="";
                        message.cache.brand_names="";
                        message.cache.store_codes="";
                        message.cache.store_names="";
                        message.cache.user_codes="";
                        message.cache.user_names="";
                        $(".xuanzhong_p input").val("全部");
                        corp_code=c;
                        $(".xingming").empty();
                        $("#area_input").val("");
                        $("#area_input").attr("data-areacode", "");
                        $("#store_input").val("");
                        $("#store_input").attr("data-storecode", "");
                        $("#staff_input").val("");
                        $("#staff_input").attr("data-usercode", "");
                        $("#staff_input").attr("data-userphone", "");
                        $("#store_code ul").empty();
                        $("#staff_code ul").empty();
                    }
                }
            })
            $("#corp_select .searchable-select-item").click(function () {
                var corp_code = $(this).attr("data-value");
                console.log(corp_code);
                if(c!==corp_code) {
                    message.cache.vip_id="";
                    message.cache.area_codes="";
                    message.cache.area_names="";
                    message.cache.brand_codes="";
                    message.cache.brand_names="";
                    message.cache.store_codes="";
                    message.cache.store_names="";
                    message.cache.user_codes="";
                    message.cache.user_names="";
                    $(".xuanzhong_p input").val("全部");
                    getasktypelist(corp_code, b);
                    corp_code=c;
                    $(".xingming").empty();
                    $("#area_input").val("");
                    $("#area_input").attr("data-areacode", "");
                    $("#store_input").val("");
                    $("#store_input").attr("data-storecode", "");
                    $("#staff_input").val("");
                    $("#staff_input").attr("data-usercode", "");
                    $("#staff_input").attr("data-userphone", "");
                    $("#store_code ul").empty();
                    $("#staff_code ul").empty();
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
    });
}
//点击保存
$("#add_save").click(function () {
    var _param = {};
    function getContent() {
        var arr = [];
        arr.push(UE.getEditor('editor').getContent());
        return arr.join("\n");
    }
    function getPlainTxt() {
        var arr = [];
        arr.push(UE.getEditor('editor').getPlainTxt());
        return arr.join("\n");
    }
    var reg = /<img[^>]*>/gi;;
    function imge_change() {
        var  i=0;
        return function img_change(){
            i++;
            return i;
        }
    }
    var img_c=imge_change();
    var nr= getContent().replace(reg,function () {
        var i=img_c();
        return getPlainTxt().match(reg)[i-1];
    });
    var a = $('.xingming input');
    var user_codes = "";
    var phone = "";
    for (var i = 0; i < a.length; i++) {
        var u = $(a[i]).attr("data-code");
        var p = $(a[i]).attr("data-phone");
        if (i < a.length - 1) {
            user_codes += u + ",";
            phone += p + ",";
        } else {
            user_codes += u;
            phone += p;
        }
    }
    var corp_code = $('#OWN_CORP').val();//公司编号
    var task_type_code = $('#task_type_code').val();//公司类型
    var task_title=$('#task_title').val();//任务名称
    var task_description=nr;//任务描述
    var target_end_time=$("#target_end_time").val();//截止时间
    var target_start_time=$("#target_start_time").val();//开始时间
    var isactive = "";//是否可用
    var input = $("#is_active")[0];
    if(task_type_code==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务类型不能为空"
        });
        return;
    }
    if(task_title==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务名称不能为空"
        });
        return;
    }
    // if(task_description==""){
    //     art.dialog({
    //         time: 1,
    //         lock: true,
    //         cancel: false,
    //         content:"任务描述不能为空"
    //     });
    //     return;
    // }
    if(user_codes==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"员工不能为空"
        });
        return;
    }
    if(target_end_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"截止时间不能为空"
        });
        return;
    }
    if(target_start_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"开始时间不能为空"
        });
        return;
    }
    if (input.checked == true) {
        isactive = "Y";
    } else if (input.checked == false) {
        isactive = "N";
    }
    _param["user_codes"]=user_codes;
    _param["phone"]=phone;
    _param["corp_code"]=corp_code;
    _param["task_type_code"]=task_type_code;
    _param["task_title"]=task_title;
    _param["task_description"]=task_description;
    _param["target_end_time"]=target_end_time;
    _param["target_start_time"]=target_start_time;
    _param["isactive"]=isactive;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/task/addTask","", _param, function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var state={};
            state["corp_code"]=corp_code;
            state["task_code"]=message.taskCode;
            state["id"]=message.taskId;
            sessionStorage.setItem("state", JSON.stringify(state));
            $(window.parent.document).find('#iframepage').attr("src", "/task/task_edit.html");
            whir.loading.remove();//移除加载框
        }
    })
});
//编辑点击保存
$("#edit_save").click(function(){
    var delete_image=[];//需要删除的数据
    var load_image=sessionStorage.getItem('goods_description').match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
    var save_image=getContent().match(/<img\b[^>]*src\s*=\s*"[^>"]*\.(?:png|jpg|bmp|gif)"[^>]*>/ig);
    if(load_image!==null&&save_image!==null){
        save_image.forEach(function (val,index,arr) {
            return val.indexOf('/image/upload')!=-1?arr.splice(index,1):arr[index];
        })
        load_image.map(function (val,index,arr) {
            var reg= /src=[\'\"]?([^\'\"]*)[\'\"]?/;
            var test=val.match(reg)[0];
            save_image.join('').indexOf(test)==-1?delete_image.push(val):'';
        })
    }else if(load_image!==null&&save_image==null){
        load_image.forEach(function (val) {
            delete_image.push(val);
        })
    }
    function getContent() {
        var arr = [];
        arr.push(UE.getEditor('editor').getContent());
        return arr.join("\n");
    }
    function getPlainTxt() {
        var arr = [];
        arr.push(UE.getEditor('editor').getPlainTxt());
        return arr.join("\n");
    }
    var reg = /<img[^>]*>/gi;;
    function imge_change() {
        var  i=0;
        return function img_change(){
            i++;
            return i;
        }
    }
    var img_c=imge_change();
    var nr= getContent().replace(reg,function () {
        var i=img_c();
        return getPlainTxt().match(reg)[i-1];
    });
    var _param={};
    var a=$('.xingming input');
    var user_codes="";
    var phone="";
    for(var i=0;i<a.length;i++){
        var u=$(a[i]).attr("data-code");
        var p=$(a[i]).attr("data-phone");
        if(i<a.length-1){
            user_codes+=u+",";
            phone+=p+",";
        }else{
            user_codes+=u;
            phone+=p;
        }
    }
    var corp_code = $('#OWN_CORP').val();//公司编号
    var task_type_code = $('#task_type_code').val();//公司类型
    var task_title=$('#task_title').val();//任务名称
    var task_description=nr;//任务描述
    var target_end_time=$("#target_end_time").val();//截止时间
    var target_start_time=$("#target_start_time").val();//开始时间
    var id=$('#task_id').val();//id名称
    if(task_type_code==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务类型不能为空"
        });
        return;
    }
    if(task_title==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"任务名称不能为空"
        });
        return;
    }
    if(user_codes==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"员工不能为空"
        });
        return;
    }
    if(target_end_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"截止时间不能为空"
        });
        return;
    }
    if(target_start_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content:"开始时间不能为空"
        });
        return;
    }
    var task_code=$('#task_code_e').val();
    var isactive = "";//是否可用
    var input = $("#is_active")[0];
    if (input.checked == true) {
        isactive = "Y";
    } else if (input.checked == false) {
        isactive = "N";
    }
    _param["user_codes"]=user_codes;
    _param["phone"]=phone;
    _param["corp_code"]=corp_code;
    _param["task_type_code"]=task_type_code;
    _param["task_title"]=task_title;
    _param["task_description"]=task_description;
    _param["target_end_time"]=target_end_time;
    _param["target_start_time"]=target_start_time;
    _param["task_code"]=task_code;//任务编号
    _param["id"]=id;//id名称
    _param["isactive"]=isactive;
    _param["delImgPath"]=delete_image.join('');
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/task/edit","", _param, function(data) {
        if(data.code=="0"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"保存成功"
            });
            whir.loading.remove();//移除加载框
            window.location.reload();
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:data.message
            });
            whir.loading.remove();//移除加载框
        }
    })
})
if ($(".pre_title label").text() == "新增任务") {
    getcorplist(a, b);
}
function nssignment() {//加载list的文件
    var key_val=sessionStorage.getItem("key_val");//取页面的function_code
    key_val=JSON.parse(key_val);
    var funcCode=key_val.func_code;
    whir.loading.add("",0.5);
    $.get("/detail?funcCode="+funcCode+"", function(data){
        // var data=JSON.parse(data);
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var action=message.actions;
            if(action.length<=0){
                $("#edit_save").remove();
                $("#edit_close").css("margin-left","120px");
            }
        }
        whir.loading.remove();//移除加载框
    });
    oc.postRequire("post", "/task/selectTaskById", "0", param, function (data) {
        // var msg = data.message;
        var msg = JSON.parse(data.message);
        var list = JSON.parse(msg.list);
        var msg = JSON.parse(msg.task);
        var corp_code = msg.corp_code;//公司编号
        var task_code = msg.task_code;//任务编号
        var task_type_code=msg.task_type_code;//类型编号
        var ul = "";
        for (var i = 0; i < list.length; i++) {
            //ul+="<li data-code='"+list[i].user_code+"' data-phone='"+list[i].phone+"'>"+list[i].user_name+"<div class='delectxing' onclick='deleteName(this)'></div></li>"
            ul += "<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-code='" + list[i].user_code + "' data-phone='" + list[i].phone + "' value='" + list[i].user_name + "'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>";
        }
        $('.xingming').html(ul);
        //将读取到的卖点信息保存在本地
        sessionStorage.setItem('goods_description',msg.task_description);
        ue.ready(function() {
            // ue.setContent(msg.goods_description);
            ue.body.innerHTML=msg.task_description;

        });
        $("#task_title").val(msg.task_title);//任务名称
        // $("#task_describe").val(msg.task_description);//任务描述
        $("#task_code_e").val(msg.task_code);//任务编号
        $("#target_start_time").val(msg.target_start_time);//开始时间
        $("#target_end_time").val(msg.target_end_time);//截止时间
        $("#created_time").val(msg.created_date);//创建时间
        $("#creator").val(msg.creater);//创建人
        $("#modify_time").val(msg.modified_date);//修改时间
        $("#modifier").val(msg.modifier);//修改人
        getcorplist(corp_code, task_type_code);//
        whir.loading.remove();//移除加载框
    });
}
//双击进入编辑界面
function editAssignment(a){
    whir.loading.add("", 0.5);//加载等待框
    $("#page-wrapper").show();
    $("#content").hide();
    $("#details").hide();
    var id = $(a).attr("id");
    var corp_code = $(a).attr("data-code");
    //var task_code = $(a).find("td:eq(2) span").html();
    var task_code=$(a).attr("data-task_code");
    param["corp_code"] = corp_code;//公司编号
    param["task_code"] = task_code;//任务编号
    param["id"] = id;//公司id
    $('#task_id').val(id);
    nssignment();
}
//编辑进入界面
function editAssignmentb(a) {
    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
    if (tr.length > 1 || tr.length == '0') {
        return;
    }
    whir.loading.add("", 0.5);//加载等待框
    var id = $(tr).attr("id");
    var corp_code = $(tr).attr("data-code");
    var task_code=$(tr).attr("data-task_code");
    //var task_code = $(tr).find("td:eq(2) span").html();
    param["corp_code"] = corp_code;//公司编号
    param["task_code"] = task_code;//任务编号
    param["id"] = id;//公司id
    nssignment();
}
//删除名称
function deleteName(a) {
    $(a).parent("p").remove();
}
//新增关闭
$("#add_close").click(function () {
    sessionStorage.removeItem("state");
    $(window.parent.document).find('#iframepage').attr("src", "/task/task.html");
});
$("#back_task").click(function () {
    $("#page-wrapper").hide();
    $("#content").show();
    $("#details").hide();
    sessionStorage.removeItem("state");
    $(window.parent.document).find('#iframepage').attr("src", "/task/task.html");
});
$(".xingming").niceScroll({
    cursorborder: "0 none",
    cursorcolor: "rgba(0,0,0,0.3)",
    cursoropacitymin: "0",
    boxzoom: false,
    autohidemode:false
});
