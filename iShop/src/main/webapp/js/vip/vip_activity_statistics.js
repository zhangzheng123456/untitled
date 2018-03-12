/**
 * Created by Administrator on 2017/3/24.
 */
var oc=new ObjectControl();
var corp_code="";
var staffData=[];//页面的数据
var show_filtrate='';
var add_store_code='';//原始店铺号
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
}
function getActiveSet() {
    whir.loading.add("", 0.5);
    var param = {};
    param.activity_code = sessionStorage.getItem('activity_code');
    oc.postRequire("post", "/vipActivity/detail/select", "0", param, function (data) {
        if(data.code==0){
            var message = JSON.parse(data.message);
            var activityVip = message.activityVip;
            var activity_type = activityVip.activity_type;//活动类型
            var activity_url = activityVip.activity_url;//推广链接
            var batch_no = activityVip.batch_no == '' ? "无" :  activityVip.batch_no;
            if(activity_url=='http://'||activity_url==''){
                activity_url = '无';
            }
            if(activity_type=='招募活动'){
                $('#activitySetPage').css('position','fixed');
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var recruit = JSON.parse(activityVip.recruit);
                var div = '';
                for(i=0;i<recruit.length;i++){
                    div += '<div><span class="width52">招募级别</span><span class="width230">'+recruit[i].vip_card_type_name+'</span><span class="width52">招募金额</span><span>'+recruit[i].join_threshold+'</span></div>';
                }
                html = div+html;
            }else if(activity_type=='促销活动'){
                var html = '<div><span class="width52">促销编号</span><span>'+activityVip.sales_no+'</span></div><div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='网页活动'){
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='优惠券活动'){
                $("#ticket").show();
                var html = '<div><span class="width52">发券类型</span><span class="width230">${msg}</span><span></span><span></span></div>'
                    + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var detail = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动计划${n}</span><span class="colorAaa slideArea left5px" style="opacity: 1">- 点击展开</span></div>'
                    + '<div class="wrapperContent"><div class="ContentLeft"><span class="marginBottom10px">参与活动条件</span>${condition}</div>'
                    + '<div class="ContentRight"><div><span class="marginBottom10px">任务奖励</span>'
                    + '<div><span class="width52">积分</span><span>${points}</span></div>'
                    + '<div><span class="width52 floot-l">优惠券</span><span class="ellipsis-4">${coupons}</span></div>'
                    + '<div><span class="width52">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div></div></div>';
                var send_coupon_type = activityVip.send_coupon_type;//发券类型
                if(send_coupon_type == "consume"){
                    var consume_condition = JSON.parse(activityVip.consume_condition);
                    var join_count = activityVip.join_count == '' ? "无" : activityVip.join_count;
                    html = '<div><span class="width52">发券类型</span><span class="width230">消费后送券</span><span style="margin-right: 5px">会员参与总数</span><span>'+join_count+'</span></div>'
                        + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                    for(var i=0;i<consume_condition.length;i++){
                        var coupon_type = JSON.parse(consume_condition[i].coupon_type);
                        var nowHtml = detail;
                        var points = consume_condition[i].send_points == "" ? "无" : consume_condition[i].send_points;
                        var coupons;
                        if(coupon_type.length>0){
                            coupons = [];
                            for(var j=0;j<coupon_type.length;j++){
                                coupons.push(coupon_type[j].coupon_name);
                            }
                            coupons = coupons.toString();
                        }else {
                            coupons = "无";
                        }
                        var n=i+1;
                        var consume_goods = consume_condition[i].consume_goods;
                        var vip_join_count = consume_condition[i].vip_join_count == '' ? "无" : consume_condition[i].vip_join_count;
                        var condition = "<div><span class='width52'>指定商品</span><span id='checkGoods' data-goods='"+consume_goods+"' class='color6cc1c8'>查看 <i class='icon-ishop_8-03'></i></span></div>"
                            + '<div><span class="width52">整单金额</span><span>'+consume_condition[i].trade_start+'~'+consume_condition[i].trade_end+' 元</span></div>'
                            + '<div><span class="width52">消费折扣</span><span>'+consume_condition[i].discount_start+'~'+consume_condition[i].discount_end+'</span></div>'
                            + '<div><span class="width52">购买件数</span><span>'+consume_condition[i].num_start+'~'+consume_condition[i].num_end+'</span></div>'
                            + '<div><span class="width52">会员参与</span><span>'+vip_join_count+'</span></div>';
                        nowHtml = nowHtml.replace('${condition}',condition);
                        nowHtml = nowHtml.replace('${points}',points);
                        nowHtml = nowHtml.replace('${coupons}',coupons);
                        nowHtml = nowHtml.replace('${n}',n);
                        html+=nowHtml;
                    }
                }else {
                    var coupon_type = JSON.parse(activityVip.coupon_type);//内容List
                    if(send_coupon_type =='card'){
                        var card = '开卡送券';
                        html = html.replace('${msg}',card);
                        for(i=0;i<coupon_type.length;i++){
                            var points = coupon_type[i].present_point == "" ? "无" : coupon_type[i].present_point;
                            var nowHtml = detail;
                            var n=i+1;
                            var condition = '<div><span class="width52">卡类型</span><span>'+ coupon_type[i].vip_card_type_name+'</span></div>';
                            nowHtml = nowHtml.replace('${condition}',condition);
                            nowHtml = nowHtml.replace('${points}',points);
                            nowHtml = nowHtml.replace('${coupons}',coupon_type[i].coupon_name);
                            nowHtml = nowHtml.replace('${n}',n);
                            html+=nowHtml;
                        }
                    }else if(send_coupon_type == 'anniversary'){
                        var card = '纪念日发券';
                        html = html.replace('${msg}',card);
                        for(i=0;i<coupon_type.length;i++){
                            var nowHtml = detail;
                            var points = coupon_type[i].send_points == "" ? "无" : coupon_type[i].send_points;
                            var coupon = JSON.parse(coupon_type[i].coupon_type);
                            var anniversary_time = coupon_type[i].run_time_type == "M" ? "当月" : "当日";
                            var coupons;
                            if(coupon.length == 0){
                                coupons = "无";
                            }else {
                                coupons = [];
                                for(var j=0;j<coupon.length;j++){
                                    coupons.push(coupon[j].coupon_name);
                                }
                                coupons = coupons.toString();
                            }
                            var n=i+1;
                            var condition = '<div><span class="width65">纪念日类型</span><span>'+ coupon_type[i].param_desc+'</span></div>'
                                + '<div><span class="width52">执行时间</span><span>'+anniversary_time+'</span></div>';
                            nowHtml = nowHtml.replace('${condition}',condition);
                            nowHtml = nowHtml.replace('${points}',points);
                            nowHtml = nowHtml.replace('${coupons}',coupons);
                            nowHtml = nowHtml.replace('${n}',n);
                            html+=nowHtml;
                        }
                    }else if(send_coupon_type=='batch'){
                        var card = '批量发券';
                        var points = activityVip.present_point == "" ? "无" : activityVip.present_point;
                        html = html.replace('${msg}',card);
                        var coupons = [];
                        if(coupon_type.length != 0){
                            for(i=0;i<coupon_type.length;i++){
                                coupons.push(coupon_type[i].coupon_name);
                            }
                            coupons = coupons.toString();
                        }else {
                            coupons = '无';
                        }
                        var condition = '<div><span class="width52">无</span></div>';
                        detail = detail.replace('${n}',"");
                        detail = detail.replace('${condition}',condition);
                        detail = detail.replace('${points}',points);
                        detail = detail.replace('${coupons}',coupons);
                        html+=detail;
                    }
                }
            }else if(activity_type=='线下报名活动'){
                $('#btn_signUp').show();
                var html = '';
                var apply_title = activityVip.apply_title;//判断依据
                if(apply_title.trim()==''){
                    html = '<div><span class="width52">推广链接</span><span class="color6cc1c8">'+activity_url+'</span></div>';
                }else{//标准模板
                    var apply_allow_vip = activityVip.apply_allow_vip == "Y" ? "是" : "否";
                    html = '<div><span class="width52">报名标题</span><span>'+apply_title+'</span></div>'
                        + '<div><span class="width52">截止时间</span><span>'+activityVip.apply_endtime+'</span></div>'
                        + '<div><span class="width52">报名简介</span><span>'+activityVip.apply_desc+'</span></div>'
                        + '<div><span class="width52">成功提示</span><span>'+activityVip.apply_success_tips+'</span></div>'
                        + '<div><span class="width65">允许非会员</span><span>'+apply_allow_vip+'</span></div>'
                        + '<div><span class="width65" style="vertical-align: top">logo图片</span><span><img style="width: 100px;height: 100px" src="'+activityVip.apply_logo+'"></span></div>';
                    + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                }
            }else if(activity_type=='节日活动'){
                var html = '<div><span class="width52">时间范围</span><span>'+activityVip.festival_start+'&nbsp;至&nbsp;'+activityVip.festival_end+'</span></div>'
                    + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='邀请注册'){
                var register = JSON.parse(activityVip.register_data);
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var detail = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动计划${n}</span><span class="colorAaa slideArea left5px" style="opacity: 1">- 点击展开</span></div>'
                    + '<div class="wrapperContent"><div class="ContentLeft"><span class="marginBottom10px">参与活动条件</span>${condition}</div>'
                    + '<div class="ContentRight"><div><span class="marginBottom10px">任务奖励</span>'
                    + '<div><span class="width52">积分</span><span>${points}</span></div>'
                    + '<div><span class="width52 floot-l">优惠券</span><span class="ellipsis-4">${coupons}</span></div>'
                    + '<div><span class="width52">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div></div></div>';
                for(var i=0;i<register.length;i++){
                    var nowHtml = detail;
                    var points = register[i].present.point == "" ? "无" : register[i].present.point;
                    var coupons;
                    var coupon = register[i].present.coupon;
                    if(coupon.length == 0){
                        coupons = "无";
                    }else {
                        coupons = [];
                        for(var j=0;j<coupon.length;j++){
                            coupons.push(coupon[j].coupon_name);
                        }
                    }
                    var n=i+1;
                    var condition = '<div><span class="width52">人数区间</span><span>'+ register[i].number_interval.start+'~'+register[i].number_interval.end+'</span></div>'
                        + '<div><span class="width52">每邀请</span><span>'+register[i].invite+'人</span></div>';
                    nowHtml = nowHtml.replace('${condition}',condition);
                    nowHtml = nowHtml.replace('${points}',points);
                    nowHtml = nowHtml.replace('${coupons}',coupons);
                    nowHtml = nowHtml.replace('${n}',n);
                    html+=nowHtml;
                }
                $("#vip_effect").show();
            }else if(activity_type=='线上报名活动'){
                var arr = JSON.parse(activityVip.apply_condition),
                    couponList = JSON.parse(activityVip.coupon_type),
                    td_allow = activityVip.td_allow == "Y" ? "是" : "否",
                    td_end_time = activityVip.td_end_time == '' ? '' : '<div><span class="width52">退订时间</span><span>'+activityVip.td_end_time+'</span></div>',
                    present_time_type = JSON.parse(activityVip.present_time).type == "timely" ? "即时" : "定时",
                    present_time_date = JSON.parse(activityVip.present_time).date == '' ? '' : '<div><span class="width52">发送时间</span><span>'+JSON.parse(activityVip.present_time).date+'</span></div>',
                    temp = '',
                    apply_type_info = JSON.parse(activityVip.apply_type_info),
                    apply_agreement = activityVip.apply_agreement != '' ? '<a target="_blank" href="'+activityVip.apply_agreement+'" style="color: #6cc1c8">预览<i class="icon-ishop_8-03"></i></a>' : '<span>无</span>',
                    item_type = activityVip.apply_type == "sku" ? "商品" : '自定义项目',
                    apply_condition = arr.map(function (item) {
                        return item.param_desc;
                    }).join(","),
                    coupon = couponList.map(function (item) {
                        return item.coupon_name;
                    }).join(","),
                    coupon = coupon == "" ? "无" : coupon,
                    present_point = activityVip.present_point == "" ? "无" : activityVip.present_point,
                    batch_no = activityVip.batch_no == "" ? "无" : activityVip.batch_no;
                for(var i=0;i<apply_type_info.length;i++){
                    var n = i+1,cost = '',
                        img = apply_type_info[i].item_picture != '' ? apply_type_info[i].item_picture : '../img/goods_default_image.png';
                    if(apply_type_info[i].fee_money == ''){
                        cost = "积分 : "+apply_type_info[i].fee_points;
                    }else if(apply_type_info[i].fee_points == ''){
                        cost = "金额 : "+apply_type_info[i].fee_money;
                    }else {
                        cost = "金额 : "+apply_type_info[i].fee_money+" 或 积分 : "+apply_type_info[i].fee_points;
                    }
                    temp += '<li><span>'+n+'</span><span>'+apply_type_info[i].item_name+'</span><span>'+apply_type_info[i].limit_count+'</span><span>'+cost+'</span><span><img src="'+img+'"></span></li>';
                }
                html = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动基本设置</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52">是否退订</span><span>'+td_allow+'</span></div>'+td_end_time
                    + '<div><span class="width52">奖励发放</span><span>'+present_time_type+'</span></div>'+present_time_date
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动项目 '+item_type+'</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<ul class="itemBox"><li><span>序号</span><span>项目名称</span><span>人数上限</span><span>报名费用</span><span>项目图片</span></li>'
                    + temp
                    +'</ul></div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>免责协议</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52">免责协议</span>'+apply_agreement+'</div>'
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>报名资料</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52 floot-l">报名资料</span><span class="text_overflow_line">'+apply_condition+'</span></div>'
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动赠送</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div style="width: 100%"><span class="width52 floot-l">积分</span><span>'+present_point+'</span></div>'
                    + '<div style="width: 100%"><span class="width52 floot-l" class="text_overflow_line">优惠券</span><span>'+coupon+'</span></div>'
                    + '<div style="width: 100%"><span class="width52 floot-l">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div>';
                $('#btn_online').show();
                $("#ticket").show();
            }
            $("#activitySet_detail").html(html);
            toSee();
            whir.loading.remove();//移除加载框
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();//移除加载框
        }
    })
}
function toSee(){
    $('#toSee').click(function(){
        $('#activityVipList').hasClass("goodsListWrap") ? $('#activityVipList').removeClass("goodsListWrap") : "";
        $('#activitySetPage').hide();
        $('#activitySet').hide();
        $('#activityVipListPage').show();
        $('#activityVipList').show();
        $("#listTitle").text("参与会员列表");
        $("#vip_list .vip_list_title").html('<span>序号</span><span>姓名</span><span>手机号</span><span>会员类型</span><span>会员卡号</span>');
        inx=1;
        GET(inx,pageSize);
    })
}
$(function(){
    var oc=new ObjectControl();
    getActiveSet()
    // 给日期类对象添加日期差方法，返回日期与diff参数日期的时间差，单位为天
    $(".people").niceScroll({
        cursorborder: "0 none",
        cursoropacitymin: "0",
        boxzoom: false,
        cursorcolor: " #dfdfdf",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 200,
        autohidemode: true,
        cursorwidth: "9px",
        cursorborderradius: "10px"
    });
    $("#showList_box3").niceScroll({
        cursorborder: "0 none",
        cursoropacitymin: "0",
        boxzoom: false,
        cursorcolor: " #dfdfdf",
        cursoropacitymax: 1,
        touchbehavior: false,
        cursorminheight: 100,
        autohidemode: true,
        cursorwidth: "9px",
        cursorborderradius: "10px"
    });
    Date.prototype.diff = function(date){
        var time="";
        if((this.getTime()-date.getTime())<0 || (this.getTime()-date.getTime())==0){
            $("#activity_all_day").html("0");
            return;
        }
        var time=(this.getTime()-date.getTime())/1000;
        var d=parseInt(time/(24*60*60));
        var h=parseInt((time-d*(24*60*60))/(60*60));
        var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
        var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
        d==0?d="":d=d+"天";
        h==0?h="":h=h+"时";
        m==0?m="":m=m+"分";
        s==0?s="":s=s+"秒";
        time=d+h+m+s;
        return time
    };
    //加载员工列表
    function listShow(userList){
        $("#peopleContent").empty();
        $('.people').animate({scrollTop:0}, 'fast');
        var tempHTML = '<li class="people_title"> <div class="people_title_order ellipsis" style="text-align: center">${order}</div> <div class="people_title_name ellipsis"title="${title_name}">${name}</div> <div class="people_title_num ellipsis"title="${title_num}">${num}</div> <div class="people_title_area ellipsis" title="${title}">${area}</div> <div class="people_title_shop ellipsis"title="${title_shop}">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done" style="width: ${percent}%"></div></div><span class="percent_percent">${percent}%</span></div> '
            +'<div class="data_detail" style="float: right;margin-right: 5px;height: 40px"><div class="detail">详情 <span class="icon-ishop_8-03"style="color:#5f8bc8"></span></div></div></li>';
        var html = '';
        for(var i=0;i<userList.length;i++) {
            //随机进度
            var order = i + 1;
            var nowHTML1 = tempHTML;
            var percent = userList[i].complete_rate;
            nowHTML1 = nowHTML1.replace("${order}", order);
            nowHTML1 = nowHTML1.replace("${name}", userList[i].user_name);
            nowHTML1 = nowHTML1.replace("${title_name}", userList[i].user_name);
            nowHTML1 = nowHTML1.replace("${num}", userList[i].user_code);
            nowHTML1 = nowHTML1.replace("${title_num}", userList[i].user_code);
            nowHTML1 = nowHTML1.replace("${area}", userList[i].store_code);
            nowHTML1 = nowHTML1.replace("${title}", userList[i].store_code);
            nowHTML1 = nowHTML1.replace("${shop}", userList[i].store_name);
            nowHTML1 = nowHTML1.replace("${title_shop}", userList[i].store_name);
            nowHTML1 = nowHTML1.replace("${percent}", percent);
            nowHTML1 = nowHTML1.replace("${percent}", percent);
            html += nowHTML1;
            //判断进度颜色
            if (percent < 100 && percent > 0) {
                $('.percent_percent').css('color', '#42a0a8');
            } else if (percent == 100) {
                $('.percent_percent').css('color', '#42a0a8');
            } else if (percent == 0) {
                $('.percent_percent').css('color', '#42a0a8');
            }
        }
        $("#peopleContent").append(html);
        var nowLength = $('.people_title').length;
        if (nowLength > 1) {
            $('#peopleError').hide();
        }else{
            $('#peopleError div').text('暂无数据');
            $('#peopleError').show();
        }
    }
    function getActivityDataInfo(){
        var param={
            activity_code:sessionStorage.getItem("activity_code")
        };
        oc.postRequire("post","/vipActivity/select","0",param,function(data){
            if(data.code!="0") return;
            var msg=JSON.parse(data.message);
            var activityVip=JSON.parse(msg.activityVip);
            var task_code=activityVip.task_code;
            var task_code_array=task_code.split(",");
            corp_code=activityVip.corp_code;
            for(var i=0;i<task_code_array.length;i++){
               if(i==0){
                   $('.progress_nav').append('<div class="progress_nav_btn progress_nav_btn_action" data-role="'+task_code_array[i]+'">任务'+(i+1)+'</div>');
               }else{
                   $('.progress_nav').append('<div class="progress_nav_btn" data-role="'+task_code_array[i]+'">任务'+(i+1)+'</div>');
               }
            }
            $("#them").html(activityVip.activity_theme);
            $("#startTime").html(activityVip.start_time);
            $("#endTime").html(activityVip.end_time);
            $("#mode").html(activityVip.run_mode);
            $("#corp").html(activityVip.corp_name);
            $("#store_num").html(formatCurrency(activityVip.store_count));
            $('.task_detail_l .p3').html(activityVip.store_count);
            $("#appName").text(activityVip.app_name);
            $("#corpName").text(activityVip.corp_name);
            $("#activitySet_title").html(activityVip.activity_theme);//活动设置弹窗标题
            $('.time_l').html(activityVip.start_time);//处理
            $('.time_r').html(activityVip.end_time);
            $("#activityType").text(activityVip.run_mode);
            sessionStorage.setItem("target_vips",activityVip.target_vips);
            $("#activityDesc").text(activityVip.activity_desc==""?"无":activityVip.activity_desc);
            $("#activityDesc").attr("title",activityVip.activity_desc==""?"无":activityVip.activity_desc);
            if(activityVip.start_time=="" || activityVip.start_time==undefined || activityVip.end_time=="" || activityVip.end_time==undefined ){
                $("#activity_all_day").html("活动时间异常");
                return;
            }
            var startTime=new Date(activityVip.start_time.replace(/-/g,"/")); //构造两个日期，活动截止日期和开始日期
            var endTime=new Date(activityVip.end_time.replace(/-/g,"/"));
            $("#activity_all_day").html(endTime.diff(startTime));
            task_code==""?$("#active_tabs ul li:first-child").hide()&&$("#progress").hide()&&$("#customer_nav").trigger("click"):getExecuteDetail(task_code_array[0]);
        })
    }
    function toSee(){
        $('#toSee').click(function(){
            $('#activityVipList').hasClass("goodsListWrap") ? $('#activityVipList').removeClass("goodsListWrap") : "";
            $('#activitySetPage').hide();
            $('#activitySet').hide();
            $('#activityVipListPage').show();
            $('#activityVipList').show();
            $("#listTitle").text("参与会员列表");
            $("#vip_list .vip_list_title").html('<span>序号</span><span>姓名</span><span>手机号</span><span>会员类型</span><span>会员卡号</span>');
            inx=1;
            GET(inx,pageSize);
        })
    }
    //获取活动执行情况
    function getExecuteDetail(task_code){
        var param={
          "corp_code":corp_code,
          "activity_code":sessionStorage.getItem("activity_code"),
          "task_code":task_code
        };
        oc.postRequire("post", '/activityAnaly/executeDetail',"0",param,function(data){
            if(data.code==0){
                if(data.message==''){
                    $('.btnSecond').hide();
                    $('.people_head').hide();
                    $('.people').hide();
                }else {
                    var message = JSON.parse(data.message);
                    if(message.taskComplete.length==0){
                        $("#chart_bar").html("暂无数据")
                    }else {
                        barChart(message.taskComplete);
                    }
                    pieChart(message.complete_vips_count,message.target_vips_count);
                    doExecuteDetail(message);
                    var complete_vips_count = message.complete_vips_count;
                    var userList =message.userList;
                    staffData=userList;
                    listShow(userList);
                }
            }else if(data.code==-1){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    }
    function pieChart(a,b) {
        //a  完成 b  目标
        var myChart = echarts.init(document.getElementById('chart_pie'));
        var NoCover ='';
        var TheCover ='';
        if(b==0){
            NoCover=100;
            TheCover = 0;
        }else{
            NoCover = ((b - a)/b*100).toFixed(2);
            TheCover = (a/b*100).toFixed(2);
        }
        // 指定图表的配置项和数据
        var labelTop = {
            normal : {
                color:'#7bc7cd',
                label : {
                    show : false,
                    position : 'top',
                    formatter : '{b}'
                },
                labelLine : {
                    show : false
                }
            }
        };
        var labelFromatter = {
            normal : {
                label : {
                    formatter : function (params){
                        console.log(params)
                        var data=(100 - params.value).toString().length>3?(100 - params.value).toFixed(2):(100 - params.value);
                        data=data>100?100:data;
                        return data+ '%'
                    },
                    textStyle: {
                        color:'#7bc7cd',
                        fontSize: 18
                    }
                }
            },
        }
        var     labelBottom = {
            normal : {
                color: '#eaeaea',
                label : {
                    show : true,
                    position : 'center'
                },
                labelLine : {
                    show : false
                }
            },
            emphasis: {
                color: '#eaeaea'
            }
        };
        var radius = [55, 70];
        var  option = {
            series : [
                {
                    type : 'pie',
                    center : ['50%', '50%'],
                    radius : radius,
                    x: '0%', // for funnel
                    itemStyle : labelFromatter,
                    data : [
                        {name:'未覆盖会员数', value:NoCover,itemStyle :labelBottom },
                        {name:'已覆盖会员数', value:TheCover, itemStyle : labelTop}
                    ]
                }
            ]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    function doExecuteDetail(message) {
        $('#progress .shoppers').html(message.user_count);
        $('#progress .covers').html(message.complete_vips_count);
        $('#progress .target').html(message.target_vips_count);
        var taskInfo=JSON.parse(message.taskInfo);
        var start_time=new Date();
        var end_time=new Date(taskInfo.target_end_time);
        var time=(end_time.getTime()-start_time.getTime())/1000;
        var d=parseInt(time/(24*60*60));
        d<=0&&(d=0);
        $('.task_detail_l .p1').html(taskInfo.task_title);
        $('.task_detail_l .p2').html(taskInfo.task_type_name);
        $('.task_detail_l .p4').html(taskInfo.target_start_time+' 开始');
        $('.task_detail_l .p5').html(taskInfo.target_end_time+' 结束');
        $('.task_detail_l .p6').html(d);
        $('.task_detail_l .p7').html(taskInfo.task_description);
        $('.task_detail_l .p8').html(d==0?"已经结束":"正在执行");
    }
    function barChart(data) {
        var myChart = echarts.init(document.getElementById('chart_bar'));
        var data_count = [];
        var date=[];
        var data=data.reverse();
        for(var i=0;i<data.length;i++){
            date.push(data[i].date);
            data_count.push(data[i].complete_count)
        }
        var dataZoom = [];
        if(date.length>4){
            dataZoom = [
                {
                    type: 'slider',
                    show: true,
                    start:0,
                    end: 100,
                    handleSize: 8,
                    height:15,
                    bottom:10,
                    left:80,
                    right:80
                }
            ]
        }
        var option = {
            grid: {
                bottom: 50
            },
            xAxis: {
                data: date,
                axisLabel: {
                    inside: false,
                    textStyle: {
                        color: '#000'
                    }
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                z: 10
            },
            yAxis: {
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#999'
                    }
                },
                splitLine: {
                    show: true,
                    lineStyle: {
                        type: 'dashed'
                    }
                }
            },
            dataZoom:dataZoom,
            tooltip: {
                show: true,
                position: 'top',
                formatter: '{c}',
                backgroundColor: '#9999cc',
                padding: [2, 8]
            },
            series: [
                {
                    type: 'bar',
                    barMaxWidth:10,
                    itemStyle: {
                        normal: {
                            color: '#d8d8e1'
                        },
                        emphasis: {
                            color: '#9999cc'
                        }
                    },
                    barCategoryGap: '50%',
                    data: data_count
                }
            ]
        };
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    $("#activitySet_detail").on("click",".slideArea",function () {
        $(this).css('opacity') == 1 ? $(this).animate({"opacity":0},0) : $(this).animate({"opacity":1},0);
        $(this).parents(".wrapperHeader").next(".wrapperContent").slideToggle(200);
        $(".activitySet").getNiceScroll().resize();
    });
    //任务控制
    $('.progress_nav').on('click','.progress_nav_btn',function () {
        $(this).addClass('progress_nav_btn_action').siblings('.progress_nav_btn_action').removeClass('progress_nav_btn_action');
        whir.loading.add("", 0.5);
        var role=this.dataset.role;
        getExecuteDetail(role);
        whir.loading.remove();//移除加载框
    });
    $('#showDone').click(function(){
        var data=[];
        staffData.map(function (val,index,arr) {
            val.complete_rate==100?data.push(val):'';
        });
        listShow(data);
        var nowLength = $('.people .people_title').length;
        if(nowLength <1) {
            $('#peopleError').show();
            $('#peopleError div').text('未发现已完成');
        }
        data=null;
        $('#screening').slideUp(600,function () {
            $(".people").getNiceScroll().resize();
        });
        if(!(show_filtrate=='show')) {
            //收起
            $('#progress .choose').html('筛选');
            $('#progress .empty').hide();
            state = 0;
            show_filtrate=''
        }
    });
    //显示未完成
    $('#showDoing').click(function(){
        var data=[];
        staffData.map(function (val,index,arr) {
            val.complete_rate<100?data.push(val):'';
        })
        listShow(data);
        var nowLength = $('.people_title').length;
        if(nowLength <=1) {
            $('#peopleError div').text('未发现未完成')
            $('#peopleError').show();
        }
        data=null;
        $('#screening').slideUp(600,function () {
            $(".people").getNiceScroll().resize();
        });
        if(!(show_filtrate=='show')) {
            //收起
            $('#progress .choose').html('筛选');
            $('#progress .empty').hide();
            state = 0;
            show_filtrate='';
        }
    });
    //显示全部
    $('#showAll').click(function(){
        listShow(staffData);
        var nowLength = $('.people_title').length;
        if(nowLength <=1) {
            $('#peopleError div').text('暂无数据');
            $('#peopleError').show();
        }
        if(!(show_filtrate=='show')) {
            //收起
            $('#progress .choose').html('筛选');
            $('#progress .empty').hide();
            state = 0;
        }
        show_filtrate=='show'?show_filtrate='':$('#screening').slideUp(600,function () {
            $(".people").getNiceScroll().resize();
        });
    });
    //点击radio时
    $('.btnSecond .radio_b').click(function () {
        $(this).next().trigger('click');
    });
    //筛选按钮
    $('.choose').click(function(){
        $(this).parent().next('.screening').slideToggle(600,function () {
            $(".people").getNiceScroll().resize();
        });
        if($(this).html()=="收起筛选"){
            //收起
            $(this).html('筛选');
            $(this).next('.empty').hide();
        }else{
            //展开
            $(this).html('收起筛选');
            $(this).next('.empty').show();
        }
    });
    $(".empty").click(function () {
        var id=$(this).attr("data-id");
        $(this).parent().next().find(".inputs input").val("");
        if(id=="task"){
            show_filtrate='show';
            $('#showAll').trigger('click');
        }else if(id=="customer"){
            Num = 1;
            $("#customer .peopleContent ul").empty();
            getCustomerList();
        }else if(id=="influence"){
            Num = 1;
            $("#influence .peopleContent ul").empty();
            getUserList();
        }else if(id=="spread"){
            Num = 1;
            $("#spread .peopleContent ul").empty();
            getspreadList();
        }else if(id=='online'){
            Num = 1;
            getOnlineList();
        }
    });
    function search(name,num,area,shop){
        var data=[];
        var param=[];
        for(var i=0;i<arguments.length;i++){
            arguments[i]==''?'':param.push(i);
        }
        if(param.length==0){
            data=staffData;
        }else if(param.length==1){
            staffData.map(function (val,index,arr) {
                //           0                  1                      2                3
                // if(val.user_name==name||val.user_code==num||val.area_name==area||val.store_name==shop){
                //     data.push(val)
                // }
                // console.log(param[0])
                if((param.indexOf(0)!=-1)&&(val.user_name.search(name)!=-1)){data.push(val)};
                if((param.indexOf(1)!=-1)&&(val.user_code.search(num)!=-1)){data.push(val)};
                if((param.indexOf(2)!=-1)&&(val.store_code.search(area)!=-1)){data.push(val)};
                if((param.indexOf(3)!=-1)&&(val.store_name.search(shop)!=-1)){data.push(val)};
            });
        }else if(param.length==2){
            if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)){ // 0 1
                staffData.map(function (val,index,arr) {
                    // if ((val.user_name == name) && (val.user_code == num)) {
                    //     data.push(val)
                    // }
                    if ((val.user_name.search(name)!=-1) && (val.user_code.search(num)!=-1)) {
                        data.push(val)
                    }
                });
            }else if((param.indexOf(0)!=-1) && (param.indexOf(2)!=-1)){ //0  2
                staffData.map(function (val,index,arr) {
                    // if ((val.user_name == name) && (val.area_name==area)) {
                    //     data.push(val)
                    // }
                    if ((val.user_name.search(name)!=-1) && (val.store_code.search(area)!=-1)) {
                        data.push(val)
                    }
                })
            }else if((param.indexOf(0)!=-1) && (param.indexOf(3)!=-1)){  //0  3
                staffData.map(function (val,index,arr) {
                    // if ((val.user_name == name) && (val.store_name==shop)) {
                    //     data.push(val)
                    // }
                    if ((val.user_name.search(name)!=-1) && (val.store_name.search(shop)!=-1)) {
                        data.push(val)
                    }
                })
            }else if((param.indexOf(1)!=-1) && (param.indexOf(2)!=-1)){    //1  2
                staffData.map(function (val,index,arr) {
                    // if ((val.user_code==num) && (val.area_name==area)) {
                    //     data.push(val)
                    // }
                    if ((val.user_code.search(num)!=-1) && (val.store_code.search(area)!=-1)) {
                        data.push(val)
                    }
                })
            }else if((param.indexOf(1)!=-1) && (param.indexOf(3)!=-1)){   //1  3
                staffData.map(function (val,index,arr) {
                    // if ((val.user_code==num) && (val.store_name==shop)) {
                    //         data.push(val)
                    // }
                    if ((val.user_code.search(num)!=-1) && (val.store_name.search(shop)!=-1)) {
                        data.push(val)
                    }
                })
            }else if((param.indexOf(2)!=-1) && (param.indexOf(3)!=-1)){  //2  3
                staffData.map(function (val,index,arr) {
                    // if ((val.area_name==area) && (val.store_name==shop)) {
                    //     data.push(val)
                    // }
                    if ((val.store_code.search(area)!=-1) && (val.store_name.search(shop)!=-1)) {
                        data.push(val)
                    }
                })
            }
        }else if(param.length==3){
            if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)&&  (param.indexOf(2)!=-1) ){   // 012
                staffData.map(function (val,index,arr) {
                    if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1) ){
                        data.push(val)
                    }
                });
            }else if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)&&  (param.indexOf(3)!=-1)){ //013
                staffData.map(function (val,index,arr) {
                    if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_name.search(shop)!=-1) ){
                        data.push(val)
                    }
                });
            }else if((param.indexOf(0)!=-1) && (param.indexOf(2)!=-1)&&  (param.indexOf(3)!=-1)){ //023
                staffData.map(function (val,index,arr) {
                    if( (val.user_name.search(name)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                        data.push(val)
                    }
                });
            }else if((param.indexOf(1)!=-1) && (param.indexOf(2)!=-1)&&  (param.indexOf(3)!=-1)){  //123
                staffData.map(function (val,index,arr) {
                    if( (val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                        data.push(val)
                    }
                });
            }
        }else if(param.length==4){
            staffData.map(function (val,index,arr) {
                if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }
        listShow(data);
        data=null;
    }
    function searchDown() {
        $('.btnSecond input:checked').removeAttr('checked');
        var name = $('#p1').val();
        var num = $('#p2').val();
        var area = $('#p3').val();
        var shop = $('#p4').val();
        search(name,num,area,shop);
        var nowLength = $('.people_title').length;
        if(nowLength <=1) {
            $('#peopleError div').text('暂无匹配数据');
            $('#peopleError').show();
        }
    }
    $("#p1").keydown(function(){
        var event=window.event||arguments[0];
        if(event.keyCode == 13){
            $('#find').trigger('click');
            searchDown()
        }
    });
    $("#p2").keydown(function(){
        var event=window.event||arguments[0];
        if(event.keyCode == 13){
            $('#find').trigger('click');
            searchDown()
        }
    });
    $("#p3").keydown(function(){
        var event=window.event||arguments[0];
        if(event.keyCode == 13){
            $('#find').trigger('click');
            searchDown()
        }
    });
    $("#p4").keydown(function(){
        var event=window.event||arguments[0];
        if(event.keyCode == 13){
            $('#find').trigger('click');
            searchDown()
        }
    });
    $('#peopleContent').on('click','li div.data_detail',function () {
        $('#detail').show();
        //获取该行数据
        $('.action_detail_box .s1').html('员工：'+$(this).parent().find('.people_title_name ').html());
        $('.action_detail_box .s2').html($(this).parent().find('.people_title_num  ').html());
        $('.action_detail_box .s3').html($(this).parent().find('.people_title_area  ').html());
        $('.action_detail_box .s4').html($(this).parent().find('.people_title_shop  ').html());
        $('.action_detail_box .s6').css('width',$(this).parent().find('.percent_percent').html());
        $('.action_detail_box .s7').html($(this).parent().find('.percent_percent').html());
        //处理函数
        var param={};
        param.task_code=$('.progress_nav').find('.progress_nav_btn_action')[0].dataset.role;
        param.user_code=$(this).parent().find('.people_title_num  ').html();
        param.corp_code=corp_code;
        oc.postRequire("post","/activityAnaly/userExecuteDetail","0",param,function(data){
            if(data.code==0){
                doResponse(JSON.parse(data.message).list);
            }else if(data.code==-1){

            }
        });
        function doResponse(msg) {
            if(msg.length==0){
                $('#showList3  tbody').empty();
                $('#showList3  tbody').append(' <div class="no_data">暂无数据</div>');
                return;
            }
            var arr=msg;
            var html='';
            $('#showList3 tbody').empty();
            for(var i=0;i<arr.length;i++){
                var a=i+1;
                var status='';
                if(arr[i].is_assort=="N"){
                    status='未分配'
                }else if(arr[i].is_assort=='Y'){
                    if(arr[i].status=='Y'){
                        status='已执行'
                    }else{
                        status='未执行'
                    }
                }
                html+="<tr><td>"+a+"</td><td>"+arr[i].vip_info.NAME_VIP+"</td><td>"+arr[i].vip_info.MOBILE_VIP+"</td><td>"+status+"</td></tr>"
            }
            html=html==''?'<div class="no_data">暂无数据</div>':html;
            $('#showList3  tbody').append(html);
        }
    });
    $('.vip_status_btn').click(function () {
        $('#detail').hide();
    });
    //参与门店
    $('.task_list').click(function () {
        $('.join_store_box').toggle();
        storeShow(add_store_code,'store');
    });
    function storeShow(store_code) {
        var role=arguments[1];
        var param={};
        param.activity_code=sessionStorage.getItem('activity_code');
        whir.loading.add("", 0.5);
        oc.postRequire("post","/vipActivity/getStoreList","0",param,function(data){
            if(data.code==0){
                storeShowList(JSON.parse(data.message).storeList,role);
                whir.loading.remove();//移除加载框
            }else if(data.code==-1){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
                whir.loading.remove();//移除加载框
            }
        })
    }
    function storeShowList(arr) {
        var html=[];
        $('#showList tbody').empty();
        $('#showList2 tbody').empty();
        for(var i=0;i<arr.length;i++){
            html.push('<tr><td>'+(i+1)+'</td><td data-code="'+arr[i].store_code+'">'+arr[i].store_name+'</td><td>'
                +arr[i].province+arr[i].city+arr[i].area+arr[i].street+'</td></tr>');
        }
        arguments[1]?$('#showList2 tbody').html(html.join(',')):$('#showList tbody').html(html.join(','));
    }
    $("#page_signUp").on("click",".btn_page_signUp",function(){
        btn_page_signUp_1($(this).parent().parent());
    });
    $('.join_footer_btn').click(function () {
        $('.join_store_box').toggle();
    });
    getActivityDataInfo();
    $("#active_tabs li").bind("click",function(){
        var index=$(this).index();
        var navType=$(this).attr("data-nav");
        $(this).addClass("current");
        $(this).siblings().removeClass("current");
        $("#analyze_wrap").children().eq(index).siblings().hide();
        $("#analyze_wrap").children().eq(index).show();
        if(navType=="customer"){//意向客户分析
            Num = 1;
            $("#customer .peopleContent ul").empty();
            getCustomerChart();
            getCustomerList();
        }
        if(navType=="influence"){//导购分析
            Num = 1;
            $("#influence .peopleContent ul").empty();
            getUserChart();
            getUserList();
        }
        if(navType=="spread"){//传播分析
            Num = 1;
            $("#spread .peopleContent ul").empty();
            getspreadList();
            getspreadChart();
        }
        if(navType=="coupon"){//优惠券分析
            Num = 1;
            $("#coupon .peopleContent ul").empty();
            getStore();
            getcouponChart();
            getcouponList();
            getActivityCoupons();
        }
        if(navType=="btn_signUp"){//报名
            Num = 1;
            $("#page_signUp .peopleContent ul").empty();
            cache.tabType = '#page_signUp';
            cache.tabPeople = '#signUpPeople';
            getSignUpView();//图表
            getSignUpList();//列表
        }
        if(navType=="online"){//报名会员费分析
            Num = 1;
            $("#online_apply .peopleContent ul").empty();
            getOnlineView();//图表
            getOnlineList();//列表
        }
//            if($(this).attr("id")=="ticket"){
//                $("#ticket_list").show();
//                $("#ticket_list").prev().hide();
//            }else{
//                $("#ticket_list").hide();
//                $("#ticket_list").prev().show();
//            }
    });
    $('.modify_footer_l').click(function () {
        $('.modify_time_box').hide()
    });
    $("#back_corp_param").bind("click",function(){
        $(window.parent.document).find('#iframepage').attr("src", "/activity/activity.html");
    });
});