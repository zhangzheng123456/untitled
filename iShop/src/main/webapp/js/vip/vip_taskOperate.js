/**
 * Created by ghy on 2017/4/25.
 */
var oc = new ObjectControl();
var _param = {};
var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var store_num=1;
var store_next=false;
var isscroll=false;
var task = {
    isempty:false,//验证必填项不能为空
    nameEmpty:"",//为空选项名称
    taskExamine:"0",//会员任务审核权限0不能提交，1提交，2确认，3执行
    share:{ //分享次数模版配置标题,内容,图片
        "share_title":"",
        "share_content":"",
        "share_img":""
    },
    invite:{//邀请注册模版配置标题,内容,图片
        "share_title":"",
        "share_content":"",
        "share_img":""
    },
    cache:{
        clickMark:"",//员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
        coupons:"",//优惠券节点
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "user_codes":"",
        "user_names":"",
        "group_codes":"",
        "group_name":""
    },
    init:function(){
        var id = sessionStorage.getItem("task_vip_id"),
            task_check = sessionStorage.getItem("task_check");
        if(id !== undefined && id !== null){
            task.editTask(id);
            $("#activityType").show();
            $("#edit_submit span:nth-child(1)").attr("class","bg_danger");
            $("#edit_submit span:nth-child(2)").text("*提交审核通过后方可执行");
            task.taskExamine = "1";
        }else {
            task.getcorplist();
        }
        task.select();
        task.testInput();
        task.uploadOSS();
        // task.filterEvent();
    },
    select:function () {//下拉框操作
        $("#page-wrapper").on("click",".selectInput",function () {
            $(this).nextAll("ul").toggle();
            $(this).nextAll("ul").children("li").show();
            var li = $(this).nextAll("ul").children("li");
            if(li.length == 0){
                var name = $(this).attr("data-empty");
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "该企业无"+name
                });
            }
        });
        $("#page-wrapper").on("keyup",".couponsWrap input",function () {
            var search = $(this).val().trim();
            $(this).next("ul").show();
            if(search != ''){
                $(this).next("ul").find("li").hide();
                $(this).next("ul").find("li:contains("+search+")").show();
            }else {
                $(this).next("ul").find("li").show();
            }
        });
        $("#page-wrapper").on("mousedown",".selectWrap li",function () {
            var name = $(this).attr("data-name");
            var id = $(this).attr("data-id");
            var code = $(this).attr("data-code");
            var attribute = $(this).attr("data-attribute");
            var value = $(this).attr("data-value");
            if(name != undefined){
                $(this).parent("ul").prevAll("input").attr("data-name",$(this).attr("data-name"));
                $(this).parent("ul").prevAll("input").attr("data-types",$(this).attr("data-type"));
            }
            if(id !=undefined){
                $(this).parent("ul").prevAll("input").attr("data-id",$(this).attr("data-id"));
            }
            if(code != undefined){
                $(this).parent("ul").prevAll("input").attr("data-code",$(this).attr("data-code"));
            }
            if(value != undefined){
                $(this).parent("ul").prevAll("input").attr("data-value",$(this).attr("data-value"));
            }
            if(attribute != undefined){
                $(this).parent("ul").prevAll("input").attr("data-attribute",$(this).attr("data-attribute"));
            }
            $(this).parent("ul").prevAll("input").val($(this).text());
        });
        $("#page-wrapper").on("blur",".selectInput,.drop_down input",function () {
            var ul = $(this).nextAll("ul");
            if(ul.hasClass("coupon_activity")){
                $(this).val().trim() != $(this).attr("data-name") ? $(this).val($(this).attr("data-name")) : "";
            }
            setTimeout(function () {
                ul.hide();
            },200);
        });
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
                    var event = event || window.event || arguments[0];
                    if (event.keyCode == 13) {
                        $("#wechat_input").val("");
                        $("#wechat_input").attr("data-id","");
                        $(".couponsWrap input").val("");
                        $(".couponsWrap input").attr("data-code","");
                        $("#vipTask_empty_filter").trigger("click");
                        $("#back_filter").trigger("click");
                        $("#simple_filter_condition li a").removeClass("condition_active");
                        $("#filter_condition li a").removeClass("condition_active");
                        $("#filter_condition li a").eq(0).addClass("condition_active");
                        $("#simple_filter_condition li a").eq(0).addClass("condition_active");
                        $(".removeContidion").trigger("click");
                        task.getTaskCondition();
                        task.getCoupon();
                        task.getWx();
                        task.getSurvey();
                    }
                });
                $('.searchable-select-item').click(function () {
                    $("#wechat_input").val("");
                    $("#wechat_input").attr("data-id","");
                    $(".couponsWrap input").val("");
                    $(".couponsWrap input").attr("data-code","");
                    $("#vipTask_empty_filter").trigger("click");
                    $("#back_filter").trigger("click");
                    $("#simple_filter_condition li a").removeClass("condition_active");
                    $("#filter_condition li a").removeClass("condition_active");
                    $("#filter_condition li a").eq(0).addClass("condition_active");
                    $("#simple_filter_condition li a").eq(0).addClass("condition_active");
                    $(".removeContidion").trigger("click");
                    task.getTaskCondition();
                    task.getCoupon();
                    task.getWx();
                    task.getSurvey();
                });
                task.getTaskCondition();
                task.getCoupon();
                task.getWx();
                task.getSurvey();
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
    getCoupon:function () {//获取优惠券
        $(".coupon_activity").empty();
        var corp_code = $("#OWN_CORP").val();
        var app_id = $("#wechat_input").attr("data-id");//公众号
        var _param={
            "corp_code":corp_code,
            "app_id":app_id,
            'store_code':""
        };
        oc.postRequire("post","/vipRules/getCoupon","0",_param,function (data) {
            if(data.code==0){
                var li="";
                var msg=JSON.parse(data.message);
                if(msg.length==0){
                    task.cache.coupons="";
                }else if(msg.length>0){
                    for(var i=0;i<msg.length;i++){
                        li+="<li data-type='"+msg[i].send_type+"' data-code='"+msg[i].couponcode+"' title='"+msg[i].name+"' data-name='"+msg[i].name+"'>"+msg[i].name+"</li>"
                    }
                    task.cache.coupons=li;
                }
                $(".coupon_activity").append(li);
            }
        });
    },
    getarealist:function(a){//获取店铺群组
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
                var total =list.total;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var area_html_left ='';
                $("#screen_area .s_pitch span").eq(1).text(total);
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
                if(hasNextPage==true){
                    area_num++;
                    area_next=false;
                }
                if(hasNextPage==false){
                    area_next=true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if(!isscroll){
                    $("#screen_area .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(area_next){
                                return;
                            }
                            task.getarealist(area_num);
                        }
                    });
                }
                isscroll=true;
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
    getstafflist:function (a) {//获取导购方法
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        var searchValue=$('#staff_search').val().trim();
        _param['area_code']=task.cache.area_codes;
        _param['brand_code']=task.cache.brand_codes;
        _param['store_code']=task.cache.store_codes;
        _param['searchValue']=searchValue
        _param["corp_code"]=$("#OWN_CORP").val();
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
            if (data.code == "0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var total = list.total+1;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var staff_html = '';
                $("#screen_staff .s_pitch span").eq(1).text(total);
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                    }
                }
                if(hasNextPage==true){
                    staff_num++;
                    staff_next=false;
                }
                if(hasNextPage==false){
                    staff_next=true;
                }
                if(a==1&&searchValue==""){
                    $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
                }
                $("#screen_staff .screen_content_l ul").append(staff_html);
                if(!isscroll){
                    $("#screen_staff .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(staff_next){
                                return;
                            }
                            task.getstafflist(staff_num);
                        }
                    })
                }
                isscroll=true;
                var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                console.log(data.message);
            }
        })
    },
    getbrandlist:function () {
        var searchValue=$("#brand_search").val().trim();
        var _param={};
        _param['corp_code']=$("#OWN_CORP").val();
        _param["searchValue"]=searchValue;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/brand", "",_param, function(data){
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var total=message.total;
                var list=message.brands;
                var brand_html_left = '';
                $("#screen_brand .s_pitch span").eq(1).text(total);
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
    getstorelist:function (a) {
        var searchValue=$("#store_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        _param['corp_code']=$("#OWN_CORP").val();
        _param['area_code']=task.cache.area_codes;
        _param['brand_code']=task.cache.brand_codes;
        _param['searchValue']=searchValue;
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var total=list.total;
                var hasNextPage=list.hasNextPage;
                var list=list.list;
                var store_html = '';
                $("#screen_shop .s_pitch span").eq(1).text(total);
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
                if(hasNextPage==true){
                    shop_num++;
                    shop_next=false;
                }
                if(hasNextPage==false){
                    shop_next=true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if(!isscroll){
                    $("#screen_shop .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(shop_next){
                                return;
                            }
                            task.getstorelist(shop_num);
                        }
                    })
                }
                isscroll=true;
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
    getTaskCondition:function () {
        var corp_code = $("#OWN_CORP").val();
        var param = {"corp_code":corp_code};
        oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
            if(data.code == 0){
                $("#expend_attribute").empty();
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var html="";
                var simple_html="";
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        var param_type = list[i].param_type;
                        if(param_type=="date"){
                            simple_html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false, istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                            html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformatend})"></div>'
                        }
                        if(param_type=="select"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li>'+param_values[j]+'</li>'
                                }
                                li+='<li>空</li>';
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                        if(param_type=="text"){
                            html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="longtext"){
                            html+='<div class="textarea"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="check"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li class="type_check">'
                                        +'<input type="checkbox" style="vertical-align:middle;width: 15px;height: 15px;margin: 0"/>'
                                        +'<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+param_values[j]+'">'+param_values[j]+'</span>'
                                        +'</li>'
                                }
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                    }
                }
                $("#expend_attribute").append(html);
                $("#memorial_day").append(simple_html);
            }else if(data.code == -1){
                console.log(data.message);
            }
        });
        oc.postRequire("post","/vipparam/vipInfoParams","0",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var param = "";
               if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        param += '<li data-name="'+list[i].param_name+'" data-attribute="'+list[i].param_attribute+'" data-value="'+list[i].param_values+'" data-type="'+list[i].param_type+'">'+list[i].param_desc+'</li>';
                    }
                    $(".improveDataWrap").html(param);
                }
            }else if(data.code == -1){
                console.log(data.message);
            }
        });
    },
    getSurvey:function () {
        var corp_code = $("#OWN_CORP").val();
        var param = {"corp_code":corp_code};
        oc.postRequire("post","/questionnaire/allQtNaire","0",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var list = msg.list;
                var li = '';
                for(var i=0;i<list.length;i++){
                    li += '<li data-code="'+list[i].id+'">'+list[i].title+'</li>'
                }
                $(".surveySelect").html(li);
            }
        });
    },
    getGroup:function () {
        var _param = {};
        _param["corp_code"] = $("#OWN_CORP").val();
        _param["search_value"] = $("#task_group_search").val().trim();
        oc.postRequire("post", "/vipGroup/getCorpGroups", "0", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var total = message.total;
                var list = JSON.parse(message.list);
                var html = "";
                $("#screen_group .s_pitch span").eq(1).text(total);
                $("#task_group_search .screen_content_l ul").empty();
                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        html+="<li><div class='checkbox1'><input  data-type='"+list[i].group_type+"' type='checkbox' value='"+list[i].vip_group_code+"' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + "'/><label for='checkboxOneInput"
                            + i
                            + "'></label></div><span class='p16'>"+list[i].vip_group_name+"</span></li>"
                    }
                    $("#screen_group .screen_content_l ul").html(html);
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
    },
    getWx:function () {//获取公众号
        $("#wechat_input").next("ul").empty();
        var corp_code=$("#OWN_CORP").val();
        var param={"corp_code":corp_code};
        //获取公众号
        oc.postRequire("post","/corp/selectWx","",param,function (data) {
            if(data.code=="0") {
                var msg = JSON.parse(data.message);
                var list = msg.list;
                var li = "";
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        li+="<li data-id='"+list[i].app_id+"'>"+list[i].app_name+"</li>";
                    }
                    $("#wechat_input").next("ul").append(li);
                }
            }
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
        document.getElementById('upload_img').addEventListener('change', function (e) {
            var file = e.target.files[0];
            var time=_this.getNowFormatDate();
            var corp_code=$("#OWN_CORP").val();
            var fileSize=0;
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            if (isIE) {
                var filePath = e.value;
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                var files = fileSystem.GetFile (filePath);
                fileSize = files.Size;
            }else {
                fileSize = e.target.files[0].size;
            }
            if(fileSize/1024/1024>5){//限制大小为5M
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请上传小于5M的图片"
                });
                $("#upload_img").val("");
                return ;
            }
            whir.loading.add("上传中,请稍后...",0.5);
            var storeAs='Album/Vip/Task/'+corp_code+'_'+'_'+time+'.jpg';
            client.multipartUpload(storeAs, file).then(function (result) {
                var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
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
        var img="<img src='"+url+"'>";
        $("#imgBox").html(img);
        whir.loading.remove();
    },
    testInput:function () {
        $(".conpany_msg").on("blur",".numberInput",function () {
            var reg = /^\d+$|^\d+\.\d+$/g
            var val = $(this).val();
            if(!reg.test(val)&&val!==""){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请输入数字"
                });
                $(this).val("");
            }
        });
        $(".conpany_msg").on("blur",".urlInput",function () {
            var reg = /^((https|http)?:\/\/)[^\s]+/;
            // var reg = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
            var val=$(this).val();
            if(!reg.test(val) && val!==""){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "请填写正确的网页地址并且以“http://”开头"
                });
                $(this).val("");
            }
        });
    },
    testEmpty:function () {
        task.isempty = false;
        var input = $("#page-wrapper .notEmpty:visible");
        for(var i=input.length-1;i>=0;i--){
            if(input[i].value == "" ){
                if($(input[i]).attr("id") == 'bath_num_input'){
                    $("#couponWrap input").each(function (i,item) {
                        if($(item).attr("data-types") == '2'){
                            task.isempty = true ;
                            task.nameEmpty = "批次号";
                        }
                    });
                }else {
                    task.isempty = true ;
                    if($(input[i]).prevAll("label").text()){
                        task.nameEmpty = $(input[i]).prevAll("label").text().replace("*","");
                    }else {
                        task.nameEmpty = $(input[i]).attr("data-empty");
                    }
                }
            }
        }
    },
    addTask:function () {
        var id = sessionStorage.getItem("task_vip_id");
        var command = '';
        var param = {};
        if(id == undefined || id == null){
            command = "/vipTask/insert";
        }else {
            param.id = id;
            command = "/vipTask/update";
        }
        var point ="";
        var check = "";//
        var wx_check = ""//微信提醒
        var target_vips = [];
        var present_coupon = '';
        var type =  $("#taskType").attr("data-type");
        var advance_show = $("#is_advance_show").prop("checked") ? "Y" : "N";//提前显示
        _param.screen ? "" : _param.screen = [];
        $("#allVips")[0].checked == true ? "":$("#exportVips")[0].checked == true ? target_vips = $("#exportVipBtn").attr("data-src") :target_vips = _param.screen;//所有会员和筛选会员传值
        $("#is_active")[0].checked == false ? check = "N" : check = "Y";//是否可用
        wx_check = $("#is_wx_tips")[0].checked == false ? "N" : "Y";//是否可用
        $("#pointWrap").css("display") == "none" ? point ="" : point = $("#pointWrap input").val();//取积分的值
        var coupon = [];
        $("#couponWrap .couponsWrap input").map(function () {//优惠券遍历取值；
            var obj = {};
            obj.coupon_name = $(this).val();
            obj.coupon_code = $(this).attr("data-code");
            obj.coupon_type = $(this).attr("data-types");
            coupon.push(obj);
        });
        if(type == "share_counts"){
            var task_condition = {};
            task_condition.shareCount = $("#shareCounts").val();
            task_condition.shareUrl = $("#shareUrl").val();
            param.share_content = {
                "share_title":task.share.share_title,
                "share_desc":task.share.share_content,
                "pic":task.share.share_img
            };
        }else if(type == "activity_count"){
            var task_condition = {};
            task_condition.activity_type = $("#activityType").attr("data-types");
            task_condition.count = $("#activityType").nextAll("input").val();
        }else if(type == "improve_data"){
            var task_condition = [];
            $("#improve_dataWrap input").map(function () {
                var obj = {};
                obj.param_desc = $(this).val();
                obj.param_name = $(this).attr("data-name");
                obj.param_type = $(this).attr("data-types");
                obj.param_values =$(this).attr("data-value");
                obj.param_attribute=$(this).attr("data-attribute");
                task_condition.push(obj);
            });
        }else if(type == "consume_count" || type == "consume_money" || type == "ticket_sales"){
            var task_condition = {};
            task_condition.start_time = $("#taskCondition .change_wrap>div:visible input").eq(0).val();
            task_condition.end_time = $("#taskCondition .change_wrap>div:visible input").eq(1).val();
            task_condition.count = $("#taskCondition .change_wrap>div:visible input").eq(2).val();
        }else if(type == 'questionnaire'){
            var task_condition = {};
            task_condition.id = $("#taskCondition .change_wrap>div:visible input").attr("data-code");
            task_condition.title = $("#taskCondition .change_wrap>div:visible input").val();
        }else {
            if(type == "invite_registration"){
                param.share_content = {
                    "share_title":task.invite.share_title,
                    "share_desc":task.invite.share_content,
                    "pic":task.invite.share_img
                };
            }
            var task_condition = $("#taskCondition .change_wrap>div:visible input").val();
        }
        task.condition = task_condition;
        $("#couponWrap").css("display") == "none" ? "" : present_coupon = coupon;
        param.corp_code = $("#OWN_CORP").val().trim();
        param.task_title = $("#taskTitle").val().trim();
        param.start_time = $("#startTime").val().trim();
        param.end_time = $("#endTime").val().trim();
        param.task_type = type;
        param.select_scope = $("#exportVips").prop("checked") ? "input_file" : "condition_vip";
        param.target_vips = target_vips;
        param.task_condition = task_condition;
        param.present_coupon = present_coupon;
        param.present_point = point;
        param.batch_no = $("#bath_num_input").val().trim();
        param.task_description = $("#task_dec").val().trim();
        param.app_id = $("#wechat_input").attr("data-id");
        param.app_name = $("#wechat_input").val().trim();
        param.isactive = check;
        param.is_send_notice = wx_check;
        param.is_advance_show = advance_show;
        whir.loading.add("",0.6);
        oc.postRequire("post",command,"",param,function (data) {
            if(data.code == 0){
                if( command == "/vipTask/insert"){
                    var msg = JSON.parse(data.message);
                    var status = msg.task_status;
                    sessionStorage.setItem("task_vip_id",msg.id);
                    if(status == "0"){
                        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_taskAdd.html");
                    }else {
                        $(window.parent.document).find('#iframepage').attr("src","/vip/taskSelect.html");
                    }
                }else {
                    var msg = JSON.parse(data.message);
                    var status = msg.task_status;
                    if(status == "0"){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "编辑成功"
                        });
                    }else {
                        $(window.parent.document).find('#iframepage').attr("src","/vip/taskSelect.html");
                    }
                }
            }else if(data.code == -1){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();
        });
    },
    editTask:function (id) {
        param = {"id":id};
        whir.loading.add("",0.5);
        oc.postRequire("post","/vipTask/select","",param,function (data) {
           if(data.code == 0){
               var msg = JSON.parse(data.message);
               msg = msg.task;
               var coupons = "";
               msg.present_coupon != "" ? coupons = JSON.parse(msg.present_coupon): "";
               var li = "";
               var type = msg.task_type;
               if(type != "share_goods" && type != "integral_accumulate" && type != "invite_registration" && type != "activity"){
                   var task_condition = JSON.parse(msg.task_condition);
               }else {
                   var task_condition = msg.task_condition;
               }
               if(msg.bill_status == '' || msg.bill_status == "0"){
                   $("#edit_submit span:nth-child(1)").attr("class","bg_danger");
               }else {
                   task.examineSelect();
               }
               task.getcorplist(msg.corp_code);
               task.corp_code = msg.corp_code;
               $("#taskTitle").val(msg.task_title);
               $("#startTime").val(msg.start_time);
               $("#endTime").val(msg.end_time);
               $("#taskType").next().children("li[data-type='"+type+"']").trigger("click");//找到当前任务类型选中
               $("#taskType").next("ul").hide();
               $('#batch_num_input').val(msg.batch_no);
               msg.present_point == "" ? $("#pointLabel").trigger("click") :  $("#pointWrap input").val(msg.present_point);
               $("#wechat_input").val(msg.app_name);
               $("#wechat_input").attr("data-id",msg.app_id);
               $("#task_dec").val(msg.task_description);
               msg.isactive == "Y" ? $("#is_active").prop("checked",true) : $("#is_active").prop("checked",false);
               msg.is_send_notice == "Y" ? $("#is_wx_tips").prop("checked",true) : $("#is_wx_tips").prop("checked",false);
               msg.is_advance_show == "Y" ? $("#is_advance_show").prop("checked",true) : $("#is_advance_show").prop("checked",false);
               if(msg.select_scope == 'input_file'){
                   $("#exportVips").trigger("click");
                   $("#exportVipBtn").attr('data-src',msg.target_vips);
                   $("#exportCount").show();
                   $("#exportCount").text("导入会员数:"+msg.cardno_num);
               }else {
                   _param.screen = JSON.parse(msg.target_vips);
                   if( _param.screen.length == 0){
                       $("#allVips").trigger("click");
                   }else {
                       var screen =  _param.screen;
                       var html = "";
                       if(screen.length >0){
                           showSelect_edit(screen,msg.target_vip_type);
                       }else {
                           html = '<div style="text-align: center">暂无条件</div>';
                       }
                       $("#conditionWrap").html(html);
                       $("#chooseVips").trigger("click");
                   }
               }
               if(coupons.length != 0){
                   for(var i=0;i<coupons.length;i++){
                       var display="";
                       if(i<coupons.length-1){
                           display="style='display:none'";
                       }else {
                           display="style='display:inline-block'";
                       }
                       li += '<div class="couponsWrap"><input data-types="'+coupons[i].coupon_type+'" value="'+coupons[i].coupon_name+'" data-name="'+coupons[i].coupon_name+'" data-code="'+coupons[i].coupon_code+'" type="text" data-empty="优惠券" class="input250px selectInput notEmpty" placeholder="请选择优惠券">'
                           + '<ul class="selects coupon_activity selectWrap"></ul><span class="couponOperate"><span class="add_btn icon-ishop_6-01" '+display+'></span><span class="remove_btn icon-ishop_6-02"></span></span></div>'
                   }
                   $("#couponWrap .inputWrap").html(li);
               }else {
                   $("#couponLabel").trigger("click");
               }
               if(type == "share_counts"){
                   $("#shareCounts").val(task_condition.shareCount );
                   $("#shareUrl").val(task_condition.shareUrl);
                   task.share.share_title = JSON.parse(msg.share_content).share_title;
                   task.share.share_content = JSON.parse(msg.share_content).share_desc;
                   task.share.share_img = JSON.parse(msg.share_content).pic;
                   $("#shareTemplet").text("已编辑");
               }else if(type == "activity_count"){
                   $("#activityType").next().children("li[data-type='"+task_condition.activity_type+"']").trigger("click");
                   $("#activityType").nextAll("input").eq(0).val(task_condition.count);
               }else if(type == "improve_data"){
                   var div = "";
                   for(var i=0;i<task_condition.length;i++){
                       var display="";
                       var remove = "";
                       task_condition.length == 1 ?  remove="style='display:none'" : "";
                       if(i<task_condition.length-1){
                           display="style='display:none'";
                       }else {
                           display="style='display:inline-block'";
                       }
                       div +='<div><input data-empty="任务条件" value="'+task_condition[i].param_desc+'" data-value="'+task_condition[i].param_values+'" data-name="'+task_condition[i].param_name+'" data-types="'+task_condition[i].param_type+'" type="text" class="input320px selectInput notEmpty" placeholder="请选择条件" readonly="">'
                           + '<ul class="improveDataWrap conditionSelect selectWrap"></ul><span class="couponOperate"><span class="add_btn icon-ishop_6-01" '+display+'></span><span class="remove_btn icon-ishop_6-02" '+remove+'></span></span></div>'
                   }
                   $("#improve_dataWrap").html('<span class="infoMsg">!</span><span class="infoContent">*任务条件数据来源自定义的拓展字段</span>'+div);
               }else if(type == "consume_count" || type == "consume_money" || type == "ticket_sales"){
                   $("#taskCondition .change_wrap>div:visible input").eq(0).val(task_condition.start_time);
                   $("#taskCondition .change_wrap>div:visible input").eq(1).val(task_condition.end_time);
                   $("#taskCondition .change_wrap>div:visible input").eq(2).val(task_condition.count);
               }else if(type == 'questionnaire'){
                   $("#taskCondition .change_wrap>div:visible input").eq(0).val(task_condition.title);
                   $("#taskCondition .change_wrap>div:visible input").eq(0).attr("data-code",task_condition.id);
               }else {
                   if(type == "invite_registration"){
                       task.invite.share_title = JSON.parse(msg.share_content).share_title;
                       task.invite.share_content = JSON.parse(msg.share_content).share_desc;
                       task.invite.share_img = JSON.parse(msg.share_content).pic;
                       $("#inviteTemplet").text("已编辑");
                   }
                   $("#taskCondition .change_wrap>div:visible input").eq(0).val(task_condition);
               }
           }
           whir.loading.remove();
        });
    },
    examineSelect:function () {
        var id = sessionStorage.getItem("task_vip_id"),
            funcode = JSON.parse(sessionStorage.getItem("key_val")).func_code,
            param = {
                "id":id,
                "function_code":funcode
            };
        oc.postRequire("post","/vipTask/user/status","",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                if(msg.status == "1"){
                    $("#edit_submit span:nth-child(1)").attr("class","bg_enter");
                    $("#examine_task").html("<i class='icon-ishop_6-11'></i>通过审核");
                    $("#taskSave").hide();
                    $("#cancel_submit").css("display","inline-block");
                    task.taskExamine = "2";
                }else if(msg.status == "2"){
                    task.taskExamine = "0";
                    $("#examine_task").text("审核中("+msg.rate+")");
                    $("#edit_submit span:nth-child(2)").text("*当前任务审核中,如需修改任务请先取消提交");
                    $("#edit_submit span:nth-child(1)").attr("class","bg_default");
                    $("#taskSave").hide();
                    $("#cancel_submit").css("display","inline-block");
                }else if(msg.status == "3"){
                    task.taskExamine = "3";
                    $("#taskSave").hide();
                    $("#cancel_submit").css("display","inline-block");
                    $("#edit_submit span:nth-child(2)").text("*审核已通过,点击执行后任务将按您设置的时间执行");
                    $("#examine_task").html("<i class='icon-ishop_6-20'></i>开始执行");
                    $("#edit_submit span:nth-child(1)").attr("class","bg_normal");
                }
            }else if(data.code == 1){
                task.taskExamine = "0";
                var msg = JSON.parse(data.message);
                $("#examine_task").text("审核中("+msg.rate+")");
                $("#taskSave").hide();
                $("#edit_submit span:nth-child(2)").text("*当前任务审核中,如需修改任务请先取消提交");
                $("#edit_submit span:nth-child(1)").attr("class","bg_default");
            }else if(data.code == 2){
                task.taskExamine = "0";
                var msg = JSON.parse(data.message);
                $("#examine_task").text("审核中("+msg.rate+")");
                $("#edit_submit span:nth-child(2)").text("*当前任务审核中,如需修改任务请先取消提交");
                $("#edit_submit span:nth-child(1)").attr("class","bg_default");
                $("#taskSave").hide();
                $("#cancel_submit").css("display","inline-block");
            }else if(data.code == 3){
                task.taskExamine = "3";
                $("#taskSave").hide();
                $("#cancel_submit").css("display","inline-block");
                $("#edit_submit span:nth-child(2)").text("*点击执行后任务将按您设置的时间执行");
                $("#examine_task").html("<i class='icon-ishop_6-20'></i>开始执行");
                $("#edit_submit span:nth-child(1)").attr("class","bg_normal");
            }else if(data.code == 4){
                task.taskExamine = "0";
                $("#taskSave").hide();
                $("#edit_submit").hide();
            }else if(data.code == -1){
                task.taskExamine = "0";
                $("#taskSave").hide();
                $("#edit_submit").hide();
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:data.message
                });
            }
        });
    },
    examineTask:function (type) {
        var id = sessionStorage.getItem("task_vip_id"),
            funcode = JSON.parse(sessionStorage.getItem("key_val")).func_code,
            param = {
            "id":id,
            "type":type,
            "function_code":funcode
        };
        oc.postRequire("post","/vipTask/examine/user","",param,function (data) {
            if(type == "check"){
                if(data.code == 0){
                    task.examineSelect();
                }else {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:data.message
                    });
                }
            }else {
                if(data.code == 0){
                   window.location.reload();
                }else {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:data.message
                    });
                }
            }
        });
    },
    filterEvent:function () {
        //店铺里面的区域点击
        $("#shop_area").click(function(){
            if(task.cache.area_codes!==""){
                var area_codes=task.cache.area_codes.split(',');
                var area_names=task.cache.area_names.split(',');
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
            isscroll=false;
            area_num=1;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").show();
            $("#screen_shop").hide();
            task.getarealist(area_num);
        });
        //店铺里面的品牌点击
        $("#shop_brand").click(function(){
            if(task.cache.brand_codes!==""){
                var brand_codes=task.cache.brand_codes.split(',');
                var brand_names=task.cache.brand_names.split(',');
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
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_shop").hide();
            task.getbrandlist();
        });
        //员工里面的区域点击
        $("#staff_area").click(function(){
            task.clickMark="user";
            if(task.cache.area_codes!==""){
                var area_codes=task.cache.area_codes.split(',');
                var area_names=task.cache.area_names.split(',');
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
            isscroll=false;
            area_num=1;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").show();
            $("#screen_staff").hide();
            task.getarealist(area_num);
        });
        //员工里面的店铺点击
        $("#staff_shop").click(function(){
            task.clickMark="user";
            if(task.cache.store_codes!==""){
                var store_codes=task.cache.store_codes.split(',');
                var store_names=task.cache.store_names.split(',');
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
            isscroll=false;
            shop_num=1;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            $("#screen_shop").show();
            $("#screen_staff").hide();
            task.getstorelist(shop_num);
        });
        //员工里面的品牌点击
        $("#staff_brand").click(function(){
            task.clickMark="user";
            if(task.cache.brand_codes!==""){
                var brand_codes=task.cache.brand_codes.split(',');
                var brand_names=task.cache.brand_names.split(',');
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
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_staff").hide();
            task.getbrandlist();
        });
        //筛选弹框关闭
        $("#screen_wrapper_close").click(function(){
            $("#screen_wrapper").hide();
            $("#p").hide();
            $(document.body).css("overflow","auto");
        });
        //弹出导购框/关闭／搜索／确定
        $(".screen_staffl").click(function(){
            task.clickMark="";
            if(task.cache.user_codes!==""){
                var user_codes=task.cache.user_codes.split(',');
                var user_names=task.cache.user_names.split(',');
                var staff_html_right="";
                for(var h=0;h<user_codes.length;h++){
                    staff_html_right+="<li id='"+user_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+user_codes[h]+"'  data-storename='"+user_names[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+user_names[h]+"</span>\
            \</li>"
                }
                $("#screen_staff .s_pitch span").html(h);
                $("#screen_staff .screen_content_r ul").html(staff_html_right);
            }else{
                $("#screen_staff .s_pitch span").html("0");
                $("#screen_staff .screen_content_r ul").empty();
            }
            staff_num=1;
            isscroll=false;
            $("#screen_staff").show();
            $("#screen_wrapper").hide();
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            // $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-phone="" data-storename="无" name="test" class="check" id="checkboxFourInputwu"><label for="checkboxFourInputwu"></label></div><span class="p16">无</span></li>');
            task.getstafflist(staff_num);
        });
        $("#screen_close_staff").click(function(){
            $("#screen_staff").hide();
            $("#screen_wrapper").show();
        });
        $("#staff_search").keydown(function(){
            var event=event||window.event||arguments[0];
            staff_num=1;
            if(event.keyCode==13){
                isscroll=false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                task.getstafflist(staff_num);
            }
        });
        $("#staff_search_f").click(function(){
            staff_num=1;
            isscroll=false;
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            task.getstafflist(staff_num);
        });
        $("#screen_que_staff").click(function(){
            var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            var user_codes="";
            var user_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    user_codes+=r+",";
                    user_names+=p+",";
                }else{
                    user_codes+=r;
                    user_names+=p;
                }
            }
            task.cache.user_codes=user_codes;
            task.cache.user_names=user_names;
            $("#screen_staff").hide();
            $("#screen_wrapper").show();
            $(".screen_staff_num").val("已选"+li.length+"个");
            $(".screen_staff_num").attr("data-code",user_codes);
            $(".screen_staff_num").attr("data-name",user_names);
        });
        //弹出区域框／关闭／搜索／确定
        $("#screen_areal").click(function(){
            task.clickMark="";
            if(task.cache.area_codes!==""){
                var area_codes=task.cache.area_codes.split(',');
                var area_names=task.cache.area_names.split(',');
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
            area_num=1;
            isscroll=false;
            $("#screen_area").show();
            $("#screen_wrapper").hide();
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            task.getarealist(area_num);
        });
        $("#screen_close_area").click(function(){
            $("#screen_area").hide();
            if(task.clickMark=="user"){
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
        });
        $("#area_search").keydown(function(){
            var event=event||window.event||arguments[0];
            area_num=1;
            if(event.keyCode == 13){
                isscroll=false;
                $("#screen_area .screen_content_l").unbind("scroll");
                $("#screen_area .screen_content_l ul").empty();
                task.getarealist(area_num);
            }
        });
        $("#area_search_f").click(function(){
            area_num=1;
            isscroll=false;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            task.getarealist(area_num);
        });
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
            }
            task.cache.area_codes=area_codes;
            task.cache.area_names=area_names;
            $("#screen_area").hide();
            if(task.clickMark=="user"){
                area_num=1;
                isscroll=false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                task.getstafflist(area_num);
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
            $("#screen_area_num").val("已选"+li.length+"个");
            $("#screen_area_num").attr("data-code",area_codes);
            $("#screen_area_num").attr("data-name",area_names);
            $(".area_num").val("已选"+li.length+"个");
        });
        //弹出品牌框／关闭／搜索／确定
        $("#screen_brandl").click(function(){
            task.clickMark="";
            if(task.cache.brand_codes!==""){
                var brand_codes=task.cache.brand_codes.split(',');
                var brand_names=task.cache.brand_names.split(',');
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
            $("#screen_brand").show();
            $("#screen_wrapper").hide();
            $("#screen_brand .screen_content_l ul").empty();
            task.getbrandlist();
        });
        $("#screen_close_brand").click(function(){
            $("#screen_brand").hide();
            if(task.clickMark=="user"){
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
        });
        $("#brand_search").keydown(function(){
            var event=event||window.event||arguments[0];
            if(event.keyCode==13){
                $("#screen_brand .screen_content_l ul").empty();
                task.getbrandlist();
            }
        });
        $("#brand_search_f").click(function(){
            $("#screen_brand .screen_content_l ul").empty();
            task.getbrandlist();
        });
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
            }
            task.cache.brand_codes=brand_codes;
            task.cache.brand_names=brand_names;
            $("#screen_brand").hide();
            if(task.clickMark=="user"){
                staff_num=1;
                isscroll=false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                task.getstafflist(staff_num);
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
            $("#screen_brand_num").val("已选"+li.length+"个");
            $("#screen_brand_num").attr("data-code",brand_codes);
            $("#screen_brand_num").attr("data-name",brand_names);
            $(".brand_num").val("已选"+li.length+"个");
        });
        //弹出店铺框／关闭／搜索／确定
        $("#screen_shopl").click(function(){
            task.clickMark="";
            if(task.cache.store_codes!==""){
                var store_codes=task.cache.store_codes.split(',');
                var store_names=task.cache.store_names.split(',');
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
            shop_num=1;
            isscroll=false;
            $("#screen_shop").show();
            $("#screen_wrapper").hide();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            task.getstorelist(shop_num);
        });
        $("#screen_close_shop").click(function(){
            $("#screen_shop").hide();
            if(task.clickMark=="user"){
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
        });
        $("#store_search").keydown(function(){
            var event=event||window.event||arguments[0];
            shop_num=1;
            if(event.keyCode==13){
                isscroll=false;
                $("#screen_shop .screen_content_l ul").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                task.getstorelist(shop_num);
            }
        });
        $("#store_search_f").click(function(){
            shop_num=1;
            isscroll=false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            task.getstorelist(shop_num);
        });
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
            }
            task.cache.store_codes=store_codes;
            task.cache.store_names=store_names;
            $("#screen_shop").hide();
            if(task.clickMark=="user"){
                shop_num=1;
                isscroll=false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                task.getstafflist(shop_num);
                $("#screen_staff").show();
            }else {
                $("#screen_wrapper").show();
            }
            $("#screen_shop_num").val("已选"+li.length+"个");
            $("#screen_shop_num").attr("data-code",store_codes);
            $("#screen_shop_num").attr("data-name",store_names);
            $("#staff_shop_num").val("已选"+li.length+"个");
        });
        //分组弹窗/搜索/确定
        $("#vipTask_group_icon").click(function () {
            if(task.cache.group_codes!==""){
                var group_codes=task.cache.group_codes.split(',');
                var group_names=task.cache.group_names.split(',');
                var group_html_right="";
                for(var h=0;h<group_codes.length;h++){
                    group_html_right+="<li id='"+group_codes[h]+"'>\
            <div class='checkbox1'><input type='checkbox' value='"+group_codes[h]+"' name='test' class='check'>\
            <label></div><span class='p16'>"+group_names[h]+"</span>\
            \</li>"
                }
                $("#screen_group .s_pitch span").html(h);
                $("#screen_group .screen_content_r ul").html(group_html_right);
            }else{
                $("#screen_group .s_pitch span").html("0");
                $("#screen_group .screen_content_r ul").empty();
            }
            $("#screen_wrapper").hide();
            $("#screen_group").show();
            task.getGroup();
        });
        $("#task_group_search").keydown(function () {
            var event = event || window.event || arguments[0];
            if (event.keyCode == 13) {
                task.getGroup();
            }
        });
        $("#task_group_search_f").click(function () {
           task.getGroup();
        });
        $("#task_screen_que_group").click(function () {
            var li=$("#screen_group .screen_content_r input[type='checkbox']").parents("li");
            var group_codes="";
            var group_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    group_codes+=r+",";
                    group_names+=p+",";
                }else{
                    group_codes+=r;
                    group_names+=p;
                }
            }
            task.cache.group_codes=group_codes;
            task.cache.group_names=group_names;
            $("#screen_group").hide();
            $("#screen_wrapper").show();
            $("#filter_group").val("已选"+li.length+"个");
            $("#filter_group").attr("data-code",group_codes);
            $("#filter_group").attr("data-name",group_names);
        });
        //弹出筛选
        $("#giftWrapHead").on("click","#task_filtrate",function () {
            $("#expend_attribute").getNiceScroll().resize();
            // taskfuzhi();
            var arr = whir.loading.getPageSize();
            $("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
            $("#p").show();
            $("#screen_wrapper").show();
        });
        //清空筛选
        $("#vipTask_empty_filter").click(function () {
            task.cache.area_codes="";
            task.cache.area_names="";
            task.cache.brand_codes="";
            task.cache.brand_names="";
            task.cache.store_codes="";
            task.cache.store_names="";
            task.cache.user_codes="";
            task.cache.user_names="";
            task.cache.group_codes="";
            task.cache.group_name="";
            $("#screen_wrapper .contion_input input").each(function () {
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
                    $(this).attr("data-date","3");
                } else {
                    $(this).val("");
                }
            });
            $("#screen_wrapper textarea").each(function () {
                $(this).val("");
            });
            $("#staff_brand_num").val("全部");
            $("#staff_brand_num").attr("data-brandcode","");
            $("#staff_area_num").val("全部");
            $("#staff_brand_num").attr("data-areacode","");
            $("#staff_shop_num").val("全部");
            $("#staff_shop_num").attr("data-storecode","");
            $("#brand_num").val("全部");
            $("#brand_num").attr("data-brandcode","");
            $("#area_num").val("全部");
            $("#area_num").attr("data-areacode","");
            //任务,活动,优惠券
            $(".tab_activity").eq(0).addClass("active").siblings().remove();
            $(".tab_task").eq(0).addClass("active").siblings().remove();
            $(".tab_coupon").eq(0).addClass("active").siblings().remove();

            $(".activity_line").eq(0).siblings().remove();
            $(".activity_line").show();
            $(".activity_line").find(".filter_activity").val("全部").attr("data-value","");

            $(".task_line").eq(0).siblings().remove();
            $(".task_line").show();
            $(".task_line").find(".filter_task").val("全部").attr("data-value","");

            $(".coupon_line").eq(0).siblings().remove();
            $(".coupon_line").show();
            $(".coupon_line input").val("");
            $(".coupon_line").find(".filter_coupon").val("全部").attr("data-value","");

            $(".participate").val("全部");
        });
        //筛选确定
        $("#vip_screen_vip_que").click(function () {
            $(document.body).css("overflow", "auto");
            var screen = [];
            if ($("#simple_filter").css("display") == "block") {
                _param['screen_type']="easy";
                $("#simple_contion .contion_input").each(function () {
                    var input = $(this).find("input");
                    var key = $(input[0]).attr("data-kye");
                    var classname = $(input[0]).attr("class");
                    var expend_key = $(input[0]).attr("data-expend");
                    if(key == "17"){
                        return ;
                    }else if (key == "4") {
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
                    }else if (key == "3") {
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
                    }else if(key == "15"){
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
                    }else if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
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
                    }else if(key == "18" && $(input[0]).val() !== "全部"){
                        var param = {};
                        var val = $(input[0]).val();
                        param['name'] = val;
                        val = val == "未关注" ? "N": "Y";
                        param['key'] = key;
                        param['value'] = val;
                        param['type'] = "text";
                        param['names'] = $(this).find("label").text();
                        screen.push(param);
                    }else {
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
                _param['screen_type']="difficult";
                $("#contion>div").each(function () {
                    $(this).find(".contion_input").each(function (i, e) {
                        var input = $(e).find("input");
                        var key = $(input[0]).attr("data-kye");
                        var expend_key = $(input[0]).attr("data-expend");
                        var classname = $(input[0]).attr("class");
                        if ((key !== "3" && key !== "4" && classname.indexOf("short") == 0)||expend_key=="date") {
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
                        } else if (key == "brand_code" || key == "area_code" || key == "14" || key == "15"|| key == "16") {
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
                        }else if (key == "17") {
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
                        }else if((key == "6"||key == "18") && $(input[0]).val() !== "全部" ){
                            var param = {};
                            var val = $(input[0]).val();
                            param['name'] = val;
                            val = (val == "已冻结"|| val == "未关注") ? "N": "Y";
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        }else {
                            if ($(input[0]).val() !== "" && $(input[0]).val() !== "全部") {
                                var param = {};
                                var val = $(input[0]).val();
                                val = (val == "空"&&$(input[0]).hasClass("select")) ? null : val;
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
                        if(val !== ""){
                            param['key'] = key;
                            param['value'] = val;
                            param['type'] = "text";
                            param['names'] = $(this).find("label").text();
                            screen.push(param);
                        }
                    });
                });
                var param_coupon={};
                var param_task={};
                var param_activity={};
                param_coupon['value']=[];
                param_task['value']=[];
                param_activity['value']=[];
                $("#coupon_list .coupon_line").each(function(i,e){
                    var val=$(e).find(".filter_coupon").val();
                    if(val!="全部"){
                        var param_val={
                            coupon_name:$(e).find(".filter_coupon").val(),
                            coupon_code:$(e).find(".filter_coupon").attr("data-value"),
                            num:{
                                start:$(e).find(".coupon_all").children("input").eq(0).val(),
                                end:$(e).find(".coupon_all").children("input").eq(1).val()
                            },
                            use:{
                                start:$(e).find(".coupon_used").children("input").eq(0).val(),
                                end:$(e).find(".coupon_used").children("input").eq(1).val()
                            },
                            expired_num:{
                                start:$(e).find(".coupon_overdue").children("input").eq(0).val(),
                                end:$(e).find(".coupon_overdue").children("input").eq(1).val()
                            },
                            can_use:{
                                start:$(e).find(".coupon_Usable").children("input").eq(0).val(),
                                end:$(e).find(".coupon_Usable").children("input").eq(1).val()
                            }
                        };
                        param_coupon['value'].push(param_val);
                    }
                });
                $("#task_list .task_line").each(function(i,e){
                    var val=$(e).find(".filter_task").val();
                    var participate=$(e).find(".participate").val();
                    if(val=="全部")return;
                    var param_val={
                        task_name:$(e).find(".filter_task").val(),
                        task_code:$(e).find(".filter_task").attr("data-value"),
                        is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
                    };
                    param_task['value'].push(param_val);
                });
                $("#activity_list .activity_line").each(function(i,e){
                    var val=$(e).find(".filter_activity").val();
                    var participate=$(e).find(".participate").val();
                    if(val=="全部")return;
                    var param_val={
                        activity_name:$(e).find(".filter_activity").val(),
                        activity_code:$(e).find(".filter_activity").attr("data-value"),
                        is_join:participate=="已参与"?"Y":participate=="全部"?"ALL":"N"
                    };
                    param_activity['value'].push(param_val);
                });
                if(param_task['value'].length>0){
                    param_task['key'] = 22;
                    param_task['type'] = "array";
                    param_task["name"]="会员任务";
                    screen.push(param_task);
                }
                if(param_activity['value'].length>0){
                    param_activity['key'] = 23;
                    param_activity['type'] = "array";
                    param_activity["name"]="活动";
                    screen.push(param_activity);
                }
                if(param_coupon['value'].length>0){
                    param_coupon['key'] = 21;
                    param_coupon['type'] = "array";
                    param_coupon["name"]="券";
                    screen.push(param_coupon);
                }
            }
            _param['screen'] = screen;
            var html = "";
            if(screen.length >0){
                for(var i=0;i<screen.length;i++){
                    if(screen[i].date !== undefined){
                        html += '<div><label title="'+screen[i].names+'">'+screen[i].names+'</label><input value="最近'+screen[i].date+'个月" type="text" class="input80" readonly><input value="'+screen[i].value.start+'" class="input80" type="text" readonly>~<input value="'+screen[i].value.end+'" class="input80" type="text" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>'
                    }else {
                        if(screen[i].type == "text"){
                            if(screen[i].name != undefined){
                                html += '<div><label title="'+screen[i].names+'">'+screen[i].names+'</label><input value="'+screen[i].name+'" type="text" class="input288" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>'
                            }else {
                                var value  = screen[i].value == null ? '空' : screen[i].value
                                html += '<div><label title="'+screen[i].names+'">'+screen[i].names+'</label><input value="'+value+'" type="text" class="input288" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>'
                            }
                        }
                        if(screen[i].type == "json"){
                            html += '<div><label title="'+screen[i].names+'">'+screen[i].names+'</label><input value="'+screen[i].value.start+'" type="text" class="input130" readonly>~<input value="'+screen[i].value.end+'" class="input130" type="text" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>'
                        }
                        if(screen[i].type == "array"){
                            if(screen[i].key == "23"){
                                var value = screen[i].value;
                                var values="";
                                value.map(function (arr) {
                                    var join = arr.is_join=="ALL"?"全部":arr.is_join=="Y"?"已参与":"未参与";
                                    values += "活动名称:"+arr.activity_name+",是否参与:"+join+";";
                                });
                                html += '<div><label title="'+screen[i].name+'">'+screen[i].name+'</label><input value="'+values+'" type="text" class="input288" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>';
                            }
                            if(screen[i].key == "22"){
                                var value = screen[i].value;
                                var values="";
                                value.map(function (arr) {
                                    var join = arr.is_join=="ALL"?"全部":arr.is_join=="Y"?"已参与":"未参与";
                                    values += "任务名称:"+arr.task_name+",是否参与:"+join+";";
                                });
                                html += '<div><label title="'+screen[i].name+'">'+screen[i].name+'</label><input value="'+values+'" type="text" class="input288" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>';
                            }
                            if(screen[i].key == "21"){
                                var value = screen[i].value;
                                var values="";
                                value.map(function (arr) {
                                    var text = "";
                                    if(arr.can_use.start!="" || arr.can_use.end!=""){
                                        text +="可用数量:"+arr.can_use.start+"~"+arr.can_use.end+",";
                                    }
                                    if(arr.expired_num.start!="" || arr.expired_num.end!=""){
                                        text +="过期数量:"+arr.expired_num.start+"~"+arr.expired_num.end+",";
                                    }
                                    if(arr.num.start!="" || arr.num.end!=""){
                                        text +="使用数量:"+arr.num.start+"~"+arr.num.end+",";
                                    }
                                    if(arr.use.start!="" || arr.use.end!=""){
                                        text +="获得数量:"+arr.use.start+"~"+arr.use.end+",";
                                    }
                                    text=text.slice(0,-1);
                                    values += "券类型:"+arr.coupon_name+","+text+";";
                                });
                                html += '<div><label title="'+screen[i].name+'">'+screen[i].name+'</label><input value="'+values+'" type="text" class="input288" readonly><i class="icon-ishop_6-12 removeContidion"></i></div>';
                            }
                        }
                    }
                }
            }else {
                html = '<div style="text-align: center">暂无条件</div>';
            }
            $("#conditionWrap").html(html);
            $("#screen_wrapper").hide();
            $("#p").hide();
            // $("#vipTask_empty_filter").trigger("click");
        });
        //删除筛选条件
    }
};
$(function () {
    task.init();
    $(".closeEndTaskWrap").click(function () {
        $("#examineWrap").hide();
    });
    $("#examineTaskBtn").click(function () {
        $("#examineWrap").hide();
        var funcode = JSON.parse(sessionStorage.getItem("key_val")).func_code;
        var param = {"id":sessionStorage.getItem("task_vip_id"),"function_code":funcode};
        whir.loading.add("",0.5);
        oc.postRequire("post","/vipTask/examine/creater","",param,function (data) {
            if(data.code == 0){
                window.location.reload();
            }else {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:data.message
                });
            }
            whir.loading.remove();
        });
    });
    $("#examine_task").click(function () {//提交审核
        if(task.taskExamine == "0"){
            return
        }else if(task.taskExamine == "1"){
            $("#examineWrap").show();
        }else if(task.taskExamine == "2"){
            task.examineTask("check");
        }else if(task.taskExamine == "3"){
            var param = {
                "id":sessionStorage.getItem("task_vip_id")
            };
            whir.loading.add("",0.5);
            oc.postRequire("post","/vipTask/execute","",param,function (data) {
                if(data.code == 0){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"提交成功"
                    });
                    $(window.parent.document).find('#iframepage').attr("src","/vip/taskSelect.html");
                }else {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:data.message
                    });
                }
                whir.loading.remove();
            });
        }
    });
    $("#cancel_submit").click(function () {
       task.examineTask("uncheck");
    });
    $(".edit_close,#back_vip_label").click(function () {//关闭新增，编辑页面
        $(window.parent.document).find('#iframepage').attr("src","/vip/vip_task.html?t="+ $.now());
    });
    $("#taskSave").click(function () {
        task.testEmpty();
        var condition = [];
        var hash = {};
        $("#improve_dataWrap input:visible").each(function () {
            condition.push($(this).val());
        });
        for(var i=0;i<condition.length;i++){
            if(!hash[condition[i]]){
                hash[condition[i]] = true ;
            }else {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"任务条件不能重复"
                });
                return ;
            }
        }
        if(task.isempty == true){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: task.nameEmpty+"不能为空"
            });
            return ;
        }
        if($("#taskType").attr("data-type") == "invite_registration" && $("#inviteTemplet").text()=="未编辑"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"请配置模版内容"
            });
            return ;
        }
        if($("#taskType").attr("data-type") == "share_counts" && $("#shareTemplet").text()=="未编辑"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"请配置模版内容"
            });
            return ;
        }
        if($("#points").prop("checked") == false && $("#coupons").prop("checked") == false){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"至少选择一项达成奖励"
            });
            return ;
        }
        if($("#exportVips").prop("checked") == true && $("#exportVipBtn").attr("data-src") == undefined){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"请导入文件"
            });
            return ;
        }
        task.addTask();
    });
    $(".drop_down").click(function (e) {
        e.stopPropagation();
        $(this).children("ul").toggle();
    });
    $(".drop_down").on("click","li",function () {
        if($(this).attr("data-id")){
            $(this).parent().prev().attr("data-id",$(this).attr("data-id"));
            $(this).parent().prev().val($(this).text());
            task.getCoupon();
        }else {
            var index = $(this).index();
            $("#taskCondition .change_wrap>div").eq(index).show();
            $("#taskCondition .change_wrap>div").eq(index).siblings("div").hide();
            $(this).parent().prev().val($(this).text());
            $(this).parent().prev().attr("data-type",$(this).attr("data-type"));
        }
    });
    $("#allVips").click(function () {
        $("#conditionWrap,#exportWrap").hide();
        $("#giftWrapHead").find("input[type=button]").removeAttr("id");
        // $("#giftWrapHead").find("input[type=button]").css("background","#ccc");
    });
    $("#exportVips").click(function () {
        $("#conditionWrap").hide();
        $("#exportWrap").show();
        $("#giftWrapHead").find("input[type=button]").removeAttr("id");
    });
    $("#chooseVips").click(function () {
        $("#conditionWrap").show();
        $("#exportWrap").hide();
        $("#giftWrapHead").find("input[type=button]").attr("id","task_filtrate");
        // $("#giftWrapHead").find("input[type=button]").css("background","#6cc1c8");
    });
    $("#exportVipBtn").change(function () {//会员导入
        whir.loading.add("",0.5);//加载等待框
        var fileObj = document.getElementById("exportVipBtn").files[0];
        var FileController = "/vipActivity/excludeMultipart"; //接收上传文件的后台地址
        var form = new FormData();
        form.append("file", fileObj); // 文件对象
        form.append("type", "vip_task"); // 文件对象
        form.append("corp_code", $("#OWN_CORP").val()); // 文件对象
        // XMLHttpRequest 对象
        var xhr = null;
        if (window.XMLHttpRequest) {
            xhr = new XMLHttpRequest();
        } else {
            xhr = new ActiveXObject('Microsoft.XMLHTTP');
        }
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    doResult(xhr.responseText);
                } else {
                    $('#exportVipBtn').val("");
                }
            }
        }
        function doResult(data) {
            var data=JSON.parse(data);
            whir.loading.remove();
            if(data.code=="0"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "导入成功"
                });
                $("#exportVipBtn").attr("data-src",JSON.parse(data.message).file_id);
                $("#exportCount").show();
                $("#exportCount").text("导入会员数:"+JSON.parse(data.message).cardno_num);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
            $('#exportVipBtn').val("");
        }
        xhr.open("post", FileController, true);
        xhr.send(form);
    });
    $("#exportVipsBtn").click(function () {
        var id = $("#exportVipBtn").attr("data-src");
        if(id == undefined || id == ""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "无文件可导出"
            });
            return
        }
        var param = {"id":id};
        oc.postRequire("post","/vipActivity/fileOutExecl","",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message).path;
                $("#enter a").attr("href",msg);
                $("#downloadWrap").show();
                whir.loading.add("mask",0.5);
            }else {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    });
    $(".closeWrap").click(function () {
        $(this).parents(".tk").hide();
    });
    $("#page-wrapper").on("click",".add_btn",function () {//加号事件(优惠券，完善资料条件)
        var clone = $(this).parent().parent().clone();
        $(clone).find("input").val("");
        $(clone).find("ul").hide();
        $(clone).find("ul li").show();
        $(clone).find(".remove_btn").css("display","inline-block");
        $(this).hide();
        $(this).next(".remove_btn").css("display","inline-block");
        $(this).parents(".inputWrap").append($(clone));
    });
    $("#page-wrapper").on("click",".remove_btn",function () {//减号事件(优惠券，完善资料条件)
        var wrap =  $(this).parents(".inputWrap");
        $(this).parent().parent().remove();
        var length = $(wrap).children("div").length;
        length==1?$(wrap).children("div").find(".remove_btn").hide():"";
        $(wrap).children("div:last-child").find(".add_btn").css("display","inline-block");
    });
    $("#pointLabel").click(function () {
        var check = $(this).prev("input")[0].checked;
        check==true?$("#pointWrap").hide():$("#pointWrap").show();
    });
    $("#couponLabel").click(function () {
        var check = $(this).prev("input")[0].checked;
        if(check){
            $("#couponWrap").hide();
            $(".batch_num").hide();
        }else {
            $("#couponWrap").show();
            $(".batch_num").show();
        }
    });
    $("#page-wrapper").on("mousemove",".infoMsg",function () {
        $(".infoContent").show();
    }).on("mouseleave",".infoMsg",function () {
        $(".infoContent").hide();
    });
    $(".templetBtn").click(function () {//模版配置
        if($("#taskType").attr("data-type") == "share_counts"){
            $("#shareTitle").val(task.share.share_title);
            $("#shareContent").val(task.share.share_content);
            $("#imgBox").html("<img src='"+task.share.share_img+"'>");
        }else {
            $("#shareTitle").val(task.invite.share_title);
            $("#shareContent").val(task.invite.share_content);
            $("#imgBox").html("<img src='"+task.invite.share_img+"'>");
        }
        $("#messageSend_wrap").show();
    });
    $("#message_send").click(function () {
        var title = $("#shareTitle").val();
        var content = $("#shareContent").val();
        var img = $("#imgBox img").attr("src");
        if(title == ""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请输入推送标题"
            });
            return ;
        }else if(content == ""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请输入消息内容"
            });
            return ;
        }else if(img == "" || img == undefined){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请上传图片"
            });
            return ;
        }
        if($("#taskType").attr("data-type") == "share_counts"){
            task.share.share_title = title;
            task.share.share_content = content;
            task.share.share_img = img;
            $("#shareTemplet").text("已编辑")
        }else {
            task.invite.share_title = title;
            task.invite.share_content = content;
            task.invite.share_img = img;
            $("#inviteTemplet").text("已编辑")
        }
        $("#messageSend_wrap").hide();
    });
    $("#message_close").click(function () {
        $("#messageSend_wrap").hide();
    });
});
function taskfuzhi() {
    if(_param.screen==undefined || _param == undefined){
        return ;
    }else {
        if(_param.screen_type=="easy"){
            var screen=_param.screen;
            for(var i=0;i<screen.length;i++){
                switch (screen[i].key){
                    case "1":$("#simple_birth_start").val(screen[i].value.start);
                        $("#simple_birth_end").val(screen[i].value.end);
                        break;
                    case "3":$("#consume_date_basic_3").val("最近"+screen[i].date+"个月");
                        $("#consume_date_basic_3").attr("data-date",screen[i].date);
                        $("#consume_time_start").val(screen[i].value.start);
                        $("#consume_time_end").val(screen[i].value.end);
                        break;
                    case "4":$("#consume_date_basic_4").val("最近"+screen[i].date+"个月");
                        $("#consume_date_basic_4").attr("data-date",screen[i].date);
                        $("#consume_money_start").val(screen[i].value.start);
                        $("#consume_money_end").val(screen[i].value.end);
                        break;
                    case "5":$("#point_start").val(screen[i].value.start);
                        $("#point_end").val(screen[i].value.end);
                        break;
                    case "18":if(screen[i].value=="Y"){$("#simple_state").val("已关注")}else if(screen[i].value=="N"){$("#simple_state").val("未关注")}
                        break;
                    case "15":$(".screen_staff_num").attr("data-code",screen[i].value);$(".screen_staff_num").val("已选"+screen[i].value.split(",").length+"个");
                        $(".screen_staff_num").attr("data-name",screen[i].name);
                        task.cache.user_codes=screen[i].value;
                        task.cache.user_names=screen[i].name;
                        break;
                    default:$("#memorial_day input").each(function () {
                        var key=$(this).attr("data-kye");
                        if(key==screen[i].key){
                            $(this).val(screen[i].value.start);
                            $(this).nextAll("input").val(screen[i].value.end);
                        }
                    });
                }
            }
        }else if(_param.screen_type=="difficult"){
            var screen=_param.screen;
            for(var i=0;i<screen.length;i++){
                switch (screen[i].key){
                    case "7":$("#basic_message input[data-kye='7']").val(screen[i].value);
                        break;
                    case "8":$("#sex").val(screen[i].value);
                        break;
                    case "9":$("#age_l").val(screen[i].value.start);$("#age_r").val(screen[i].value.end);
                        break;
                    case "1":$("#birth_start").val(screen[i].value.start);$("#birth_end").val(screen[i].value.end);
                        break;
                    case "10":$("#basic_message input[data-kye='10']").val(screen[i].value);
                        break;
                    case "11":$("#basic_message input[data-kye='11']").val(screen[i].value);
                        break;
                    case "12":$("#basic_message input[data-kye='12']").val(screen[i].value);
                        break;
                    case "13":$("#activate_card_start").val(screen[i].value.start);$("#activate_card_end").val(screen[i].value.end);
                        break;
                    case "5":$("#basic_message input[data-kye='5']").val(screen[i].value.start);$("#basic_message input[data-kye='5']").nextAll("input").val(screen[i].value.end);
                        break;
                    case "6":if(screen[i].value=="Y"){$("#state").val("未冻结")}else if(screen[i].value=="N"){$("#state").val("已冻结")}
                        break;
                    case "18":if(screen[i].value=="Y"){$("#govWx").val("已关注")}else if(screen[i].value=="N"){$("#govWx").val("未关注")}
                        break;
                    case "3":$("#consume_date").val("最近"+screen[i].date+"个月");
                        $("#consume_date").attr("data-date",screen[i].date);
                        $("#consume_time_before").val(screen[i].value.start);
                        $("#consume_time_after").val(screen[i].value.end);
                        break;
                    case "4":$("#consume_date").val("最近"+screen[i].date+"个月");
                        $("#consume_date").attr("data-date",screen[i].date);
                        $("#consume_money_before").val(screen[i].value.start);
                        $("#consume_money_after").val(screen[i].value.end);
                        break;
                    case "brand_code":$("#screen_brand_num").attr("data-code",screen[i].value);$("#screen_brand_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_brand_num").attr("data-name",screen[i].name);
                        task.cache.brand_codes=screen[i].value;
                        task.cache.brand_names=screen[i].name;
                        break;
                    case "area_code":$("#screen_area_num").attr("data-code",screen[i].value);$("#screen_area_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_area_num").attr("data-name",screen[i].name);
                        task.cache.area_codes=screen[i].value;
                        task.cache.area_names=screen[i].name;
                        break;
                    case "14":$("#screen_shop_num").attr("data-code",screen[i].value);$("#screen_shop_num").val("已选"+screen[i].value.split(",").length+"个");
                        $("#screen_shop_num").attr("data-name",screen[i].name);
                        task.cache.store_codes=screen[i].value;
                        task.cache.store_names=screen[i].name;
                        break;
                    case "15":$(".screen_staff_num").attr("data-code",screen[i].value);$(".screen_staff_num").val("已选"+screen[i].value.split(",").length+"个");
                        $(".screen_staff_num").attr("data-name",screen[i].name);
                        task.cache.user_codes=screen[i].value;
                        task.cache.user_names=screen[i].name;
                        break;
                    case "16":$("#filter_group").attr("data-code",screen[i].value);$("#filter_group").val("已选"+screen[i].value.split(",").length+"个");
                        $("#filter_group").attr("data-name",screen[i].name);
                        task.cache.group_codes=screen[i].value;
                        task.cache.group_name=screen[i].name;
                        break;
                    default:$("#expend_attribute input").each(function () {
                        var key=$(this).attr("data-kye");
                        var date=$(this).attr("data-expend");
                        if(key==screen[i].key&&date=="date"){
                            $(this).val(screen[i].value.start);
                            $(this).nextAll("input").val(screen[i].value.end);
                        }else if(key==screen[i].key){
                            $(this).val(screen[i].value);
                        }
                    });
                }
            }
        }
    }
}